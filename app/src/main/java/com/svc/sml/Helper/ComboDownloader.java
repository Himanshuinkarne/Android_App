package com.svc.sml.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.svc.sml.Database.ComboData;
import com.svc.sml.Database.DatabaseHandler;
import com.svc.sml.Database.InkarneDataSource;
import com.svc.sml.Database.User;
import com.svc.sml.InkarneAppContext;
import com.svc.sml.Model.BaseAccessoryItem;
import com.svc.sml.Model.ComboDataDownloadItem;
import com.svc.sml.Utility.AWSUtil;
import com.svc.sml.Utility.ConstantsUtil;
import com.svc.sml.Utility.Unzip;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by himanshu on 5/26/16.
 */
public class ComboDownloader {

    private final static String LOGTAG = ComboDownloader.class.toString();
    private static final int INDEX_DOWNLOAD_LEG_OBJ = 11;
    private static final int INDEX_DOWNLOAD_LEG_TEX = 111;
    private static final int INDEX_DOWNLOAD_A1_OBJ = 0;
    private static final int INDEX_DOWNLOAD_A1_TEX = 1;
    private static final int INDEX_DOWNLOAD_A6_OBJ = 6;
    private static final int INDEX_DOWNLOAD_A6_TEX = 61;
    private static final int INDEX_DOWNLOAD_A7_OBJ = 7;
    private static final int INDEX_DOWNLOAD_A7_TEX = 71;
    private static final int INDEX_DOWNLOAD_A8_OBJ = 8;
    private static final int INDEX_DOWNLOAD_A8_TEX = 81;
    private static final int INDEX_DOWNLOAD_A9_OBJ = 9;
    private static final int INDEX_DOWNLOAD_A9_TEX = 91;
    private static final int INDEX_DOWNLOAD_A10_OBJ = 10;
    private static final int INDEX_DOWNLOAD_A10_TEX = 101;

    //private Context context;
    private String textureKeyPath;
    private String objKeyPath;
    private WeakReference<OnComboDownloadListener> onComboDownloadWeakListener;
    private OnComboDownloadListener onComboDownloadListener;
    private boolean downloadedSuccessful = false;
    private TransferUtility transferUtility;
    private InkarneDataSource dataSource;
    protected ArrayList<TransferObserver> observers = new ArrayList<TransferObserver>();
    private int totalCountTobeDownloaded;
    private Unzip unzip = new Unzip();

    public interface OnComboDownloadListener {
        void onDownload(ComboData comboData);

        void onDownloadFailed(String comboId);

        void onDownloadProgress(String comboId, int percentage);

        void onComboInfoFailed(String comboId, int error_code);

        void onComboInfoResponse(String comboId);
    }

    public ComboDownloader(Context ctx) {
        //this.context = ctx;
        this.transferUtility = AWSUtil.getTransferUtility(ctx);
//        int SDK_INT = android.os.Build.VERSION.SDK_INT;
//        if (android.os.Build.VERSION.SDK_INT > 9) {
//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//            StrictMode.setThreadPolicy(policy);
//        }
    }

    public ComboDownloader(Context ctx, final ComboData comboData, OnComboDownloadListener onComboDownloadListener) {
        this.transferUtility = AWSUtil.getTransferUtility(ctx);
        this.dataSource = InkarneAppContext.getDataSource();

        //this.context = ctx;
        //this.onComboDownloadWeakListener =  new WeakReference<OnComboDownloadListener>(onComboDownloadListener);
        this.onComboDownloadListener = onComboDownloadListener;
        new Thread(new Runnable() {
            @Override
            public void run() {
                startSkusDownloadService(comboData);
            }
        }).start();
    }

    protected void removeTransferListener() {
        Log.w(LOGTAG, "removeTransferListener");
        for (TransferObserver ob : observers) {
            if (transferUtility != null)
                transferUtility.cancel(ob.getId());
            ob.cleanTransferListener();
        }
        observers.clear();
    }

    public void startSkusDownloadService(final ComboData combodata) {
        Log.d(LOGTAG, "LoadCombo getComboInfo " + combodata.getCombo_ID());
        ComboData comboData = combodata;
        if (combodata.getLegId() == null || combodata.getLegId().isEmpty() || combodata.getmA7_Obj_Key_Name() == null || combodata.getmA7_Obj_Key_Name().isEmpty()) {
            comboData = dataSource.getComboDataByComboID(combodata.getCombo_ID());
        }
        if (comboData != null && comboData.getLegId() != null && !comboData.getLegId().isEmpty() && comboData.getmA1_Obj_Key_Name() != null && comboData.getmA7_Obj_Key_Name() != null && !comboData.getmA7_Obj_Key_Name().isEmpty()) {
            Log.d(LOGTAG, "getComboInfo 2 " + comboData.getCombo_ID());
            downloadSkus(comboData);
        } else {

            DataManager.getInstance().requestComboDataInfoById(combodata.getCombo_ID(), User.getInstance().getDefaultFaceId(),
                    new DataManager.OnResponseHandlerInterface() {
                        @Override
                        public void onResponse(Object obj) {
                            ArrayList<ComboData> arrayListCombo = (ArrayList<ComboData>) obj;
                            if (arrayListCombo != null && arrayListCombo.size() > 0) {

                                if (onComboDownloadListener != null)
                                    onComboDownloadListener.onComboInfoResponse(combodata.getCombo_ID());

                                Log.d(LOGTAG, "getComboInfo 3 " + combodata.getCombo_ID());
                                ComboData comboData = (ComboData) arrayListCombo.get(0);
                                comboData = dataSource.getComboDataByComboID(comboData.getCombo_ID());
                                downloadSkus(comboData);
                            } else {
                                //Toast.makeText(getActivity(), "Looks Info not available ", Toast.LENGTH_SHORT).show();
//                                if(onComboDownloadWeakListener != null) {
//                                    OnComboDownloadListener listener =  onComboDownloadWeakListener.get();
//                                    if(listener != null)
//                                        listener.onComboInfoFailed(combodata.getCombo_ID(),2);
//                                }
                                if (onComboDownloadListener != null)
                                    onComboDownloadListener.onComboInfoFailed(combodata.getCombo_ID(), 2);
                            }
                        }

                        @Override
                        public void onResponseError(String errorMessage, int errorCode) {
                            Log.e(LOGTAG, "Error getting comboInfo :" + errorMessage);
//                            if(onComboDownloadWeakListener != null) {
//                                OnComboDownloadListener listener =  onComboDownloadWeakListener.get();
//                                if(listener != null)
//                                    listener.onComboInfoFailed(combodata.getCombo_ID(),errorCode);
//                            }
                            if (onComboDownloadListener != null)
                                onComboDownloadListener.onComboInfoFailed(combodata.getCombo_ID(), errorCode);

                        }
                    });
        }
    }

//    protected void downloadSkus1(ComboData combodata) {
//        if (combodata.getLegItem() == null) {
//            combodata.countTobeDownloaded = 10;
//            totalCountTobeDownloaded = 10;
//        } else {
//            combodata.countTobeDownloaded = 12;
//            totalCountTobeDownloaded = 12;
//            beginDownload(combodata.getLegItem().getObjAwsKey(), INDEX_DOWNLOAD_LEG_OBJ, combodata.getLegItem().getObjDStatus(), combodata);
//            beginDownload(combodata.getLegItem().getTextureAwsKey(), INDEX_DOWNLOAD_LEG_TEX, combodata.getLegItem().getTextureDStatus(), combodata);
//        }
//        Log.w(LOGTAG, "downloadSkus : " + totalCountTobeDownloaded);
//
//        beginDownload(combodata.getmA1_Obj_Key_Name(), INDEX_DOWNLOAD_A1_OBJ, combodata.getObjA1DStatus(), combodata);
//        beginDownload(combodata.getmA1_Png_Key_Name(), INDEX_DOWNLOAD_A1_TEX, combodata.getTextureA1DStatus(), combodata);
//
//        beginDownload(combodata.getmA6_Obj_Key_Name(), INDEX_DOWNLOAD_A6_OBJ, combodata.getObjA6DStatus(), combodata);
//        beginDownload(combodata.getmA6_Png_Key_Name(), INDEX_DOWNLOAD_A6_TEX, combodata.getTextureA6DStatus(), combodata);
//
//        beginDownload(combodata.getmA7_Obj_Key_Name(), INDEX_DOWNLOAD_A7_OBJ, combodata.getObjA7DStatus(), combodata);
//        beginDownload(combodata.getmA7_Png_Key_Name(), INDEX_DOWNLOAD_A7_TEX, combodata.getTextureA7DStatus(), combodata);
//
//        beginDownload(combodata.getmA9_Obj_Key_Name(), INDEX_DOWNLOAD_A9_OBJ, combodata.getObjA9DStatus(), combodata);
//        beginDownload(combodata.getmA9_Png_Key_Name(), INDEX_DOWNLOAD_A9_TEX, combodata.getTextureA9DStatus(), combodata);
//
//        beginDownload(combodata.getmA10_Obj_Key_Name(), INDEX_DOWNLOAD_A10_OBJ, combodata.getObjA10DStatus(), combodata);
//        beginDownload(combodata.getmA10_Png_Key_Name(), INDEX_DOWNLOAD_A10_TEX, combodata.getTextureA10DStatus(), combodata);
//    }

    protected void downloadSkus(ComboData combodata) {
        ComboDataDownloadItem comboDataDownloadItem = new ComboDataDownloadItem(combodata);
        comboDataDownloadItem.isRendered = false;
        int downloadToBeStarted = ConstantsUtil.EDownloadStatusType.eDownloadTobeStarted.intStatus();
        comboDataDownloadItem.sArrayDStatus.put(INDEX_DOWNLOAD_A1_OBJ, downloadToBeStarted);
        comboDataDownloadItem.sArrayDStatus.put(INDEX_DOWNLOAD_A1_TEX, downloadToBeStarted);
        comboDataDownloadItem.sArrayDStatus.put(INDEX_DOWNLOAD_A6_OBJ, downloadToBeStarted);
        comboDataDownloadItem.sArrayDStatus.put(INDEX_DOWNLOAD_A6_TEX, downloadToBeStarted);
        comboDataDownloadItem.sArrayDStatus.put(INDEX_DOWNLOAD_A7_OBJ, downloadToBeStarted);
        comboDataDownloadItem.sArrayDStatus.put(INDEX_DOWNLOAD_A7_TEX, downloadToBeStarted);
        comboDataDownloadItem.sArrayDStatus.put(INDEX_DOWNLOAD_A9_OBJ, downloadToBeStarted);
        comboDataDownloadItem.sArrayDStatus.put(INDEX_DOWNLOAD_A9_TEX, downloadToBeStarted);
        comboDataDownloadItem.sArrayDStatus.put(INDEX_DOWNLOAD_A10_OBJ, downloadToBeStarted);
        comboDataDownloadItem.sArrayDStatus.put(INDEX_DOWNLOAD_A10_TEX, downloadToBeStarted);

        if (combodata.getLegItem() == null) {
            combodata.countTobeDownloaded = 10;
            totalCountTobeDownloaded = 10;
            comboDataDownloadItem.getComboData().countTobeDownloaded = 10;
            beginDownload(comboDataDownloadItem.getComboData().getmA1_Obj_Key_Name(), INDEX_DOWNLOAD_A1_OBJ, comboDataDownloadItem.getComboData().getObjA1DStatus(), comboDataDownloadItem);
        } else {
            combodata.countTobeDownloaded = 12;
            totalCountTobeDownloaded = 12;
            comboDataDownloadItem.getComboData().countTobeDownloaded = 12;
            comboDataDownloadItem.sArrayDStatus.put(INDEX_DOWNLOAD_LEG_OBJ, downloadToBeStarted);
            comboDataDownloadItem.sArrayDStatus.put(INDEX_DOWNLOAD_LEG_TEX, downloadToBeStarted);
            beginDownload(combodata.getLegItem().getObjAwsKey(), INDEX_DOWNLOAD_LEG_OBJ, combodata.getLegItem().getObjDStatus(), comboDataDownloadItem);
            //beginDownload(combodata.getLegItem().getTextureAwsKey(), INDEX_DOWNLOAD_LEG_TEX, combodata.getLegItem().getTextureDStatus(), comboDataDownloadItem);
        }
        Log.w(LOGTAG, "downloadSkus : " + totalCountTobeDownloaded);
        Log.w(LOGTAG, "comboDataDownloadItem.getComboData().countTobeDownloaded : " + comboDataDownloadItem.getComboData().countTobeDownloaded);
    }

    private boolean downloadNext(ComboDataDownloadItem comboDItem) {
        //ComboData  combodata = comboDItem.getComboData();
        int downloading = ConstantsUtil.EDownloadStatusType.eDownloading.intStatus();
        int downloaded = ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus();
        int notToBeDownloaded = ConstantsUtil.EDownloadStatusType.eDownloadNoToBe.intStatus();

        for (int i = 0; i < comboDItem.sArrayDStatus.size(); i++) {
            int downloadIndex = comboDItem.sArrayDStatus.keyAt(i);
            int dStatus = comboDItem.sArrayDStatus.get(downloadIndex);
            if (dStatus != downloaded && dStatus != downloading && dStatus != notToBeDownloaded) {
                switch (downloadIndex) {
                    case INDEX_DOWNLOAD_LEG_OBJ: {
                        beginDownload(comboDItem.getComboData().getLegItem().getObjAwsKey(), INDEX_DOWNLOAD_LEG_OBJ, comboDItem.getComboData().getLegItem().getObjDStatus(), comboDItem);

                    }
                    break;
                    case INDEX_DOWNLOAD_LEG_TEX: {
                        beginDownload(comboDItem.getComboData().getLegItem().getTextureAwsKey(), INDEX_DOWNLOAD_LEG_TEX, comboDItem.getComboData().getLegItem().getTextureDStatus(), comboDItem);
                    }
                    break;
                    case INDEX_DOWNLOAD_A1_OBJ: {
                        beginDownload(comboDItem.getComboData().getmA1_Obj_Key_Name(), INDEX_DOWNLOAD_A1_OBJ, comboDItem.getComboData().getObjA1DStatus(), comboDItem);
                    }
                    break;
                    case INDEX_DOWNLOAD_A1_TEX: {
                        beginDownload(comboDItem.getComboData().getmA1_Png_Key_Name(), INDEX_DOWNLOAD_A1_TEX, comboDItem.getComboData().getTextureA1DStatus(), comboDItem);
                    }
                    break;
                    case INDEX_DOWNLOAD_A6_OBJ: {
                        beginDownload(comboDItem.getComboData().getmA6_Obj_Key_Name(), INDEX_DOWNLOAD_A6_OBJ, comboDItem.getComboData().getObjA6DStatus(), comboDItem);
                    }
                    break;
                    case INDEX_DOWNLOAD_A6_TEX: {
                        beginDownload(comboDItem.getComboData().getmA6_Png_Key_Name(), INDEX_DOWNLOAD_A6_TEX, comboDItem.getComboData().getTextureA6DStatus(), comboDItem);
                    }
                    break;
                    case INDEX_DOWNLOAD_A7_OBJ: {
                        beginDownload(comboDItem.getComboData().getmA7_Obj_Key_Name(), INDEX_DOWNLOAD_A7_OBJ, comboDItem.getComboData().getObjA7DStatus(), comboDItem);
                    }
                    break;
                    case INDEX_DOWNLOAD_A7_TEX:
                        beginDownload(comboDItem.getComboData().getmA7_Png_Key_Name(), INDEX_DOWNLOAD_A7_TEX, comboDItem.getComboData().getTextureA7DStatus(), comboDItem);
                        break;

                    case INDEX_DOWNLOAD_A9_OBJ:
                        beginDownload(comboDItem.getComboData().getmA9_Obj_Key_Name(), INDEX_DOWNLOAD_A9_OBJ, comboDItem.getComboData().getObjA9DStatus(), comboDItem);
                        break;
                    case INDEX_DOWNLOAD_A9_TEX:
                        beginDownload(comboDItem.getComboData().getmA9_Png_Key_Name(), INDEX_DOWNLOAD_A9_TEX, comboDItem.getComboData().getTextureA9DStatus(), comboDItem);
                        break;
                    case INDEX_DOWNLOAD_A10_OBJ:
                        beginDownload(comboDItem.getComboData().getmA10_Obj_Key_Name(), INDEX_DOWNLOAD_A10_OBJ, comboDItem.getComboData().getObjA10DStatus(), comboDItem);
                        break;
                    case INDEX_DOWNLOAD_A10_TEX:
                        beginDownload(comboDItem.getComboData().getmA10_Png_Key_Name(), INDEX_DOWNLOAD_A10_TEX, comboDItem.getComboData().getTextureA10DStatus(), comboDItem);
                        break;
                    default:
                        break;
                }
                //break;
                return true;
            }
        }
        return false;
    }


    private void beginDownload(String key, int downloadIndex, int downloadStatus, ComboDataDownloadItem comboDItem) {
        //ComboData comboData = comboDItem.getComboData();
        if (key == null || key.length() < 2 || key.equals("null")) {
            Log.d(LOGTAG, "combo :" + comboDItem.getComboData().getCombo_ID() + " download key is null or blank ");
            updateDownloadStatusType1(comboDItem, downloadIndex, ConstantsUtil.EDownloadStatusType.eDownloadNoToBe.intStatus());
            if (comboDItem.sArrayDStatus != null)
                comboDItem.sArrayDStatus.put(downloadIndex, ConstantsUtil.EDownloadStatusType.eDownloadNoToBe.intStatus());
            comboDItem.getComboData().countTobeDownloaded--;
            downloadNext(comboDItem);
            checkDownload(comboDItem);
//            if (comboData.countTobeDownloaded == 0) {
//                 checkDownload(comboData);
//            }
            return;
        }


        if ((downloadStatus == ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus() || checkAccessoryDownload(key, downloadIndex)) && ConstantsUtil.checkFileKeysExist(key.replace(".gz", "ply"))) {
            // if (true) {
            Log.e(LOGTAG, " already downloaded : " + key);
            if (downloadStatus != ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus()) {
                updateDownloadStatusType1(comboDItem, downloadIndex, ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus());
            }
            if (comboDItem.sArrayDStatus != null)
                comboDItem.sArrayDStatus.put(downloadIndex, ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus());
            comboDItem.getComboData().countTobeDownloaded--;
            Log.w(LOGTAG,"c :"+comboDItem.getComboData().countTobeDownloaded);
            downloadNext(comboDItem);
            checkDownload(comboDItem);
//            if (comboData.countTobeDownloaded == 0) {
//                //checkDownload(comboData);
//            }
            return;
        }


        updateDownloadStatus(comboDItem, downloadIndex, ConstantsUtil.EDownloadStatusType.eDownloading.intStatus());
        if (key.contains(".ply")) {//TODO zip//&& !key.contains("legs/"
            key = key.replace(".ply", ".gz");
        }
        File file = new File(ConstantsUtil.FILE_PATH_APP_ROOT + key);
        //file.delete();//TODO
        Log.d(LOGTAG, "combo :" + comboDItem.getComboData().getCombo_ID() + "  to be downloaded. file:  " + key);
        if (transferUtility == null) {
            Log.d(LOGTAG, " transferUtility is null. file:  " + key);
            return;
        }
        TransferObserver observer = transferUtility.download(ConstantsUtil.AWSBucketName, key, file);
        Log.d(LOGTAG, " transferUtility " + key);
        DownloadListener dListener = new DownloadListener(key, downloadIndex, comboDItem);
        observer.setTransferListener(dListener);
        observers.add(observer);
    }

    private class DownloadListener implements TransferListener {
        public DownloadListener() {

        }

        //private ComboData comboData = null;
        private ComboDataDownloadItem comboDItem = null;
        private String key = "";
        private int downloadIndex = -1;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public DownloadListener(String key, int downloadIndex, ComboDataDownloadItem comboDItem) {
            //this.comboData = comboDItem.getComboData();
            this.comboDItem = comboDItem;
            this.key = key;
            this.downloadIndex = downloadIndex;
        }

        @Override
        public void onError(int id, Exception e) {
            e.printStackTrace();
            Log.e(LOGTAG, "OnError " + e.getLocalizedMessage() + "   :" + e.getMessage() + "   :" + " Error during download  :" + key);
            //removeTransferListener();
            updateDownloadStatus(comboDItem, downloadIndex, ConstantsUtil.EDownloadStatusType.eDownloadError.intStatus());
            if (comboDItem.sArrayDStatus != null)
                comboDItem.sArrayDStatus.put(downloadIndex, ConstantsUtil.EDownloadStatusType.eDownloadError.intStatus());
            onDownloadFailed(comboDItem.getComboData());
//            if (e.getLocalizedMessage().contains("Unable to store object contents to disk: timeout")) {
//                Integer c = comboDItem.sArrayError.get(downloadIndex);
//                if (c != null) {
//                    comboDItem.sArrayError.put(downloadIndex, c + 1);
//                } else {
//                    c =1;
//                    comboDItem.sArrayError.put(downloadIndex, 1);
//                }
//                if (c < 4) {
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            downloadNext(comboDItem);
//                        }
//                    },60);
//                } else {
//                    //removeTransferListener();
//                    onDownloadFailed(comboDItem.getComboData());
//                }
//            }
//            else
//            {
//                //removeTransferListener();
//                onDownloadFailed(comboDItem.getComboData());
//            }
        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

            int percentage = (int) ((bytesCurrent * 100) / bytesTotal);
            if (percentage > 60) {
                Log.d(LOGTAG, key + " :" + percentage);
            }
            /*
            int percentage = comboData.countTobeDownloaded*100/totalCountTobeDownloaded;
            if(onComboDownloadWeakListener != null) {
                OnComboDownloadListener listener =  onComboDownloadWeakListener.get();
                if(listener != null)
                    listener.onDownloadProgress(comboData.getCombo_ID(),percentage);
            }
            */
        }

        @Override
        public void onStateChanged(int id, TransferState state) {
            if (state == TransferState.FAILED) {
                Log.e(LOGTAG, "combo :" + comboDItem.getComboData().getCombo_ID() + " Download failed :" + key);
                //removeTransferListener();
                updateDownloadStatusType1(comboDItem, downloadIndex, ConstantsUtil.EDownloadStatusType.eDownloadError.intStatus());
                if (comboDItem.sArrayDStatus != null)
                    comboDItem.sArrayDStatus.put(downloadIndex, ConstantsUtil.EDownloadStatusType.eDownloadError.intStatus());
                onDownloadFailed(comboDItem.getComboData());
            } else if (state == TransferState.COMPLETED) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        String unZipFile = "";
                        if (key.contains(".gz")) {
                            Log.e(LOGTAG, "zip key :" + key);
                            unZipFile = Unzip.getUnzipPlyFileName(ConstantsUtil.FILE_PATH_APP_ROOT + key);
                            Log.e(LOGTAG, "unzip File :" + unZipFile);
                        }
                        if (unZipFile != null) {
                            Log.e(LOGTAG, "combo :" + comboDItem.getComboData().getCombo_ID() + " ***** Download successful  *****  :" + key);
                            comboDItem.getComboData().countTobeDownloaded--;
                            updateDownloadStatus(comboDItem, downloadIndex, ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus());
                            if (comboDItem.sArrayDStatus != null)
                                comboDItem.sArrayDStatus.put(downloadIndex, ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus());
                            downloadNext(comboDItem);
                            checkDownload(comboDItem);
                        }
                    }
                });
            }
        }
    }


    private boolean checkAccessoryDownload(String key, int downloadIndex) {
        boolean isAccessoryDownloaded = false;
        switch (downloadIndex) {
            case INDEX_DOWNLOAD_A1_OBJ:
            case INDEX_DOWNLOAD_A1_TEX: {
                isAccessoryDownloaded = true;
            }
            break;

            case INDEX_DOWNLOAD_LEG_OBJ:
            case INDEX_DOWNLOAD_A6_OBJ:
            case INDEX_DOWNLOAD_A7_OBJ:
            case INDEX_DOWNLOAD_A9_OBJ:
            case INDEX_DOWNLOAD_A10_OBJ: {
                isAccessoryDownloaded = dataSource.isObjDownloaded(key);

            }
            break;

            case INDEX_DOWNLOAD_LEG_TEX:
            case INDEX_DOWNLOAD_A6_TEX:
            case INDEX_DOWNLOAD_A7_TEX:
            case INDEX_DOWNLOAD_A9_TEX:
            case INDEX_DOWNLOAD_A10_TEX: {

                isAccessoryDownloaded = dataSource.isTexDownloaded(key);
            }
            break;
            default:
                break;
        }
        return isAccessoryDownloaded;
    }

    private void updateDownloadStatus(ComboDataDownloadItem comboDItem, int downloadIndex, int downloadType) {

        ContentValues values = new ContentValues();
        switch (downloadIndex) {

            case INDEX_DOWNLOAD_LEG_OBJ: {
                BaseAccessoryItem legItem = comboDItem.getComboData().getLegItem();
                legItem.setObjDStatus(downloadType);
                legItem.setObjAwsKey(legItem.getObjAwsKey().replace(".gz", ".ply"));
                dataSource.create(legItem);
                comboDItem.getComboData().setLegItem(legItem);

            }
            break;
            case INDEX_DOWNLOAD_LEG_TEX: {
                comboDItem.getComboData().getLegItem().setTextureDStatus(downloadType);
            }
            break;
            case INDEX_DOWNLOAD_A1_OBJ: {
                comboDItem.getComboData().setObjA1DStatus(downloadType);
                values.put(DatabaseHandler.A1_OBJ_DOWNLOAD_STATUS, downloadType);
                comboDItem.getComboData().setmA1_Obj_Key_Name(comboDItem.getComboData().getmA1_Obj_Key_Name().replace(".gz", ".ply"));
                values.put(DatabaseHandler.A1_OBJ_KEY_NAME, comboDItem.getComboData().getmA1_Obj_Key_Name());

            }
            break;
            case INDEX_DOWNLOAD_A1_TEX: {
                comboDItem.getComboData().setTextureA1DStatus(downloadType);
                values.put(DatabaseHandler.A1_TEXTURE_DOWNLOAD_STATUS, downloadType);
            }
            break;
            case INDEX_DOWNLOAD_A6_OBJ: {
                comboDItem.getComboData().setObjA6DStatus(downloadType);
                values.put(DatabaseHandler.A6_OBJ_DOWNLOAD_STATUS, downloadType);
                if (comboDItem.getComboData().getmA6_Obj_Key_Name() != null) {
                    comboDItem.getComboData().setmA6_Obj_Key_Name(comboDItem.getComboData().getmA6_Obj_Key_Name().replace(".gz", ".ply"));
                    values.put(DatabaseHandler.A6_OBJ_KEY_NAME, comboDItem.getComboData().getmA6_Obj_Key_Name());
                }
            }
            break;
            case INDEX_DOWNLOAD_A6_TEX: {
                comboDItem.getComboData().setTextureA6DStatus(downloadType);
                values.put(DatabaseHandler.A6_TEXTURE_DOWNLOAD_STATUS, downloadType);
            }
            break;
            case INDEX_DOWNLOAD_A7_OBJ: {
                comboDItem.getComboData().setObjA7DStatus(downloadType);
                values.put(DatabaseHandler.A7_OBJ_DOWNLOAD_STATUS, downloadType);
                comboDItem.getComboData().setmA7_Obj_Key_Name(comboDItem.getComboData().getmA7_Obj_Key_Name().replace(".gz", ".ply"));
                values.put(DatabaseHandler.A7_OBJ_KEY_NAME, comboDItem.getComboData().getmA7_Obj_Key_Name());
            }
            break;
            case INDEX_DOWNLOAD_A7_TEX: {
                comboDItem.getComboData().setTextureA7DStatus(downloadType);
                values.put(DatabaseHandler.A7_TEXTURE_DOWNLOAD_STATUS, downloadType);
            }
            break;

            case INDEX_DOWNLOAD_A9_OBJ: {
                comboDItem.getComboData().setObjA9DStatus(downloadType);
                values.put(DatabaseHandler.A9_OBJ_DOWNLOAD_STATUS, downloadType);
                if (comboDItem.getComboData().getmA9_Obj_Key_Name() != null) {
                    comboDItem.getComboData().setmA9_Obj_Key_Name(comboDItem.getComboData().getmA9_Obj_Key_Name().replace(".gz", ".ply"));
                    values.put(DatabaseHandler.A9_OBJ_KEY_NAME, comboDItem.getComboData().getmA9_Obj_Key_Name());
                }
            }
            break;
            case INDEX_DOWNLOAD_A9_TEX: {
                comboDItem.getComboData().setTextureA9DStatus(downloadType);
                values.put(DatabaseHandler.A9_TEXTURE_DOWNLOAD_STATUS, downloadType);
            }
            break;
            case INDEX_DOWNLOAD_A10_OBJ: {
                comboDItem.getComboData().setObjA10DStatus(downloadType);
                values.put(DatabaseHandler.A10_OBJ_DOWNLOAD_STATUS, downloadType);
                if (comboDItem.getComboData().getmA10_Obj_Key_Name() != null) {
                    comboDItem.getComboData().setmA10_Obj_Key_Name(comboDItem.getComboData().getmA10_Obj_Key_Name().replace(".gz", ".ply"));
                    values.put(DatabaseHandler.A10_OBJ_KEY_NAME, comboDItem.getComboData().getmA10_Obj_Key_Name());
                }
            }
            break;
            case INDEX_DOWNLOAD_A10_TEX: {
                comboDItem.getComboData().setTextureA10DStatus(downloadType);
                values.put(DatabaseHandler.A10_TEXTURE_DOWNLOAD_STATUS, downloadType);
            }
            break;
            default:
                break;
        }
        dataSource.updateComboItemDownloadStatus(comboDItem.getComboData().getCombo_ID(), values);
    }

    private void updateDownloadStatusType1(ComboDataDownloadItem comboDItem, int downloadIndex, int downloadType) {
        ContentValues values = new ContentValues();
        switch (downloadIndex) {

            case INDEX_DOWNLOAD_LEG_OBJ: {
                comboDItem.getComboData().getLegItem().setObjDStatus(downloadType);
            }
            break;
            case INDEX_DOWNLOAD_LEG_TEX: {
                comboDItem.getComboData().getLegItem().setTextureDStatus(downloadType);
            }
            break;
            case INDEX_DOWNLOAD_A1_OBJ: {
                comboDItem.getComboData().setObjA1DStatus(downloadType);
                values.put(DatabaseHandler.A1_OBJ_DOWNLOAD_STATUS, downloadType);
            }
            break;
            case INDEX_DOWNLOAD_A1_TEX: {
                comboDItem.getComboData().setTextureA1DStatus(downloadType);
                values.put(DatabaseHandler.A1_TEXTURE_DOWNLOAD_STATUS, downloadType);
            }
            break;
            case INDEX_DOWNLOAD_A6_OBJ: {
                comboDItem.getComboData().setObjA6DStatus(downloadType);
                values.put(DatabaseHandler.A6_OBJ_DOWNLOAD_STATUS, downloadType);
            }
            break;
            case INDEX_DOWNLOAD_A6_TEX: {
                comboDItem.getComboData().setTextureA6DStatus(downloadType);
                values.put(DatabaseHandler.A6_TEXTURE_DOWNLOAD_STATUS, downloadType);
            }
            break;
            case INDEX_DOWNLOAD_A7_OBJ: {
                comboDItem.getComboData().setObjA7DStatus(downloadType);
                values.put(DatabaseHandler.A7_OBJ_DOWNLOAD_STATUS, downloadType);
            }
            break;
            case INDEX_DOWNLOAD_A7_TEX: {
                comboDItem.getComboData().setTextureA7DStatus(downloadType);
                values.put(DatabaseHandler.A7_TEXTURE_DOWNLOAD_STATUS, downloadType);
            }
            break;

            case INDEX_DOWNLOAD_A9_OBJ: {
                comboDItem.getComboData().setObjA9DStatus(downloadType);
                values.put(DatabaseHandler.A9_OBJ_DOWNLOAD_STATUS, downloadType);
            }
            break;
            case INDEX_DOWNLOAD_A9_TEX: {
                comboDItem.getComboData().setTextureA9DStatus(downloadType);
                values.put(DatabaseHandler.A9_TEXTURE_DOWNLOAD_STATUS, downloadType);
            }
            break;
            case INDEX_DOWNLOAD_A10_OBJ: {
                comboDItem.getComboData().setObjA10DStatus(downloadType);
                values.put(DatabaseHandler.A10_OBJ_DOWNLOAD_STATUS, downloadType);
            }
            break;
            case INDEX_DOWNLOAD_A10_TEX: {
                comboDItem.getComboData().setTextureA10DStatus(downloadType);
                values.put(DatabaseHandler.A10_TEXTURE_DOWNLOAD_STATUS, downloadType);
            }
            break;
            default:
                break;
        }
        //dataSource.updateComboItemDownloadStatus(comboData.getCombo_ID(), values);
    }

    private void onDownloadFailed(final ComboData comboData) {
//        if(onComboDownloadWeakListener != null) {
//            OnComboDownloadListener listener =  onComboDownloadWeakListener.get();
//            if(listener != null)
//                listener.onDownloadFailed(comboData.getCombo_ID());
//        }
        removeTransferListener();

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (onComboDownloadListener != null)
                    onComboDownloadListener.onDownloadFailed(comboData.getCombo_ID());
            }
        });
    }

    private void checkDownload(ComboDataDownloadItem comboDItem) {
        Log.e(LOGTAG, " -- Count :" + comboDItem.getComboData().countTobeDownloaded);
        if (comboDItem.getComboData().countTobeDownloaded <= 0 && !downloadNext(comboDItem) && !comboDItem.isRendered) {//TODO

            Log.e(LOGTAG, " -- Downloaded ComboId :" + comboDItem.getComboData().getCombo_ID());
            //comboDItem.getComboData().countTobeDownloaded = 12;
            comboDItem.isRendered = true;
            removeTransferListener();
            comboDItem.sArrayError.clear();
            comboDItem.sArrayDStatus.clear();
            comboDItem.getComboData().setIsDisplayReady(1);
            InkarneAppContext.getDataSource().create(comboDItem.getComboData());
            //InkarneAppContext.getDataSource().updateComboDisplayReady(comboData.getCombo_ID());
            //ComboData comboDataFromDB = InkarneAppContext.getDataSource().getComboDataByComboID(comboData.getCombo_ID());
            //comboDataFromDB.indexTemp = comboData.indexTemp;

//            if(onComboDownloadWeakListener != null) {
//                OnComboDownloadListener listener =  onComboDownloadWeakListener.get();
//                if(listener != null)
//                    listener.onDownload(comboData);
//            }

//            if(onComboDownloadListener != null)
//                onComboDownloadListener.onDownload(comboData);
            final ComboData comboData = comboDItem.getComboData();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (onComboDownloadListener != null)
                        onComboDownloadListener.onDownload(comboData);
                }
            });
        } else {
            final ComboData comboData = comboDItem.getComboData();
            final int percentage = (totalCountTobeDownloaded - comboDItem.getComboData().countTobeDownloaded) * 100 / (totalCountTobeDownloaded);
//            if(onComboDownloadWeakListener != null) {
//                OnComboDownloadListener listener =  onComboDownloadWeakListener.get();
//                if(listener != null)
//                    listener.onDownloadProgress(comboData.getCombo_ID(),percentage);
//            }
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (onComboDownloadListener != null)
                        onComboDownloadListener.onDownloadProgress(comboData.getCombo_ID(), percentage);
                }
            });
        }
    }
}
