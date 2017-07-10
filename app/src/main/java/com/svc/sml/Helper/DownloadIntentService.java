package com.svc.sml.Helper;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.svc.sml.Activity.Video360Activity;
import com.svc.sml.Database.ComboData;
import com.svc.sml.Database.InkarneDataSource;
import com.svc.sml.Database.User;
import com.svc.sml.InkarneAppContext;
import com.svc.sml.Model.BaseAccessoryItem;
import com.svc.sml.Model.FaceItem;
import com.svc.sml.Model.LookBoardItem;
import com.svc.sml.Utility.AWSUtil;
import com.svc.sml.Utility.ConstantsFunctional;
import com.svc.sml.Utility.ConstantsUtil;

import java.io.File;
import java.util.ArrayList;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class DownloadIntentService extends IntentService {
    public static final String LOGTAG = "DownloadIntentService";
    public static final String INTENT_NAME_SKU_FILES_DOWNLOAD_SERVICE = "INTENT_NAME_SKU_FILES_DOWNLOAD_SERVICE";
    public static final String INTENT_NAME_COMBOS_DOWNLOAD_SERVICE = "INTENT_NAME_COMBOS_DOWNLOAD_SERVICE";
    public static final String INTENT_NAME_COMBOS_DOWNLOAD_SERVICE_SHOP_SCREEN = "INTENT_NAME_COMBOS_DOWNLOAD_SERVICE_SHOP_SCREEN";
    public static final String INTENT_NAME_COMBOS_DOWNLOAD_SERVICE_LAUNCH = "INTENT_NAME_COMBOS_DOWNLOAD_SERVICE_LAUNCH";
    public static final String INTENT_NAME_VIDEO_DOWNLOAD_SERVICE_LAUNCH = "INTENT_NAME_VIDEO_DOWNLOAD_SERVICE_LAUNCH";
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_DOWNLOAD_SKUS = "com.svc.inkarne.Downloader.action.SKUS";
    public static final String ACTION_DOWNLOAD_COMBOS = "com.svc.inkarne.Downloader.action.COMBOS";
    public static final String ACTION_DOWNLOAD_COMBO_SHOP_SCREEN = "com.svc.inkarne.Downloader.action.COMBO.SHOP.SCREEN";
    public static final String ACTION_DOWNLOAD_COMBOS_LAUNCH = "com.svc.inkarne.Downloader.action.COMBOS.LAUNCH";
    public static final String ACTION_DOWNLOAD_RECONCILE_FACE_ACC = "com.svc.inkarne.Downloader.action.RECONCILE_FACE_ACC";
    public static final String ACTION_DOWNLOAD_RECONCILE_BODY_ACC = "com.svc.inkarne.Downloader.action.RECONCILE_BODY_ACC";
    public static final String ACTION_DOWNLOAD_VIDEO = "com.svc.inkarne.Downloader.action.VIDEO";
    public static final String ACTION_DOWNLOAD_MEMORY_CLEANUP = "com.svc.inkarne.Downloader.action.MEMORY_CLEANUP";
    // TODO: Rename parameters
    public static final String EXTRA_PARAM_VIDEO_URL = "com.svc.inkarne.Downloader.extra.PARAM.VIDEO_URL";

    public static final String EXTRA_PARAM_COMBO = "com.svc.inkarne.Downloader.extra.PARAM.COMBO";
    public static final String EXTRA_PARAM_COMBO_ID = "com.svc.inkarne.Downloader.extra.PARAM.COMBO.ID";
    public static final String EXTRA_PARAM_COMBO_INDEX = "com.svc.inkarne.Downloader.extra.PARAM.COMBO.INDEX";
    public static final String EXTRA_PARAM_COMBOLIST = "com.svc.inkarne.Downloader.extra.PARAM.COMBOLIST";
    public static final String EXTRA_PARAM_COUNT_COMBOS_TOBE_FETCHED = "com.svc.inkarne.Downloader.extra.PARAM.COUNT.COMBOS.TOBE.FETCHED";
    public static final String EXTRA_PARAM_IS_NEXT_ACTION = "EXTRA_PARAM_IS_NEXT_ACTION";
    public static final int DEFAULT_COUNT_COMBOS_TOBE_DOWNLOADED = 10;

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

    private TransferUtility transferUtility;
    private InkarneDataSource datasource;
    private ArrayList<ComboData> listCombos = new ArrayList<ComboData>();
    private int countCombosTobeFetched = 0;
    private int currentDownloadIndex = 0;
    private int countTotalDownloadPerCombo = 2 * ConstantsUtil.COUNT_SKUS_IN_COMBO;
    private String action;

    protected ArrayList<TransferObserver> observers = new ArrayList<TransferObserver>();

    public DownloadIntentService() {
        super("DownloadIntentService");
        //transferUtility = AWSUtil.getTransferUtility(InkarneAppContext.getAppContext());
        //transferUtility = AWSUtil.getTransferUtility(DownloadIntentService.this);
        transferUtility = AWSUtil.getTransferUtility(DownloadIntentService.this);
        datasource = InkarneDataSource.getInstance(InkarneAppContext.getAppContext());
        datasource.open();

    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionSkus(Context context, ArrayList<ComboData> comboList) {
        Intent intent = new Intent(context, DownloadIntentService.class);
        intent.setAction(ACTION_DOWNLOAD_SKUS);
        intent.putExtra(EXTRA_PARAM_COMBOLIST, comboList);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionCombos(Context context, ComboData comboData) {
        Intent intent = new Intent(context, DownloadIntentService.class);
        intent.setAction(ACTION_DOWNLOAD_COMBOS);
        intent.putExtra(EXTRA_PARAM_COMBO, comboData);
        context.startService(intent);
    }

    public static void startActionReconcileFaceAcc(Context context, ComboData comboData) {
        Intent intent = new Intent(context, DownloadIntentService.class);
        intent.setAction(ACTION_DOWNLOAD_RECONCILE_FACE_ACC);
        context.startService(intent);
    }

    public static void startActionReconcileBodyAcc(Context context, ComboData comboData) {
        Intent intent = new Intent(context, DownloadIntentService.class);
        intent.setAction(ACTION_DOWNLOAD_RECONCILE_BODY_ACC);
        context.startService(intent);
    }

    private void getComboData(int comboCount) {
        String faceId = User.getInstance().getDefaultFaceId();
        DataManager.getInstance().requestComboDataByCount(comboCount, faceId, new DataManager.OnResponseHandlerInterface() {
            @Override
            public void onResponse(Object obj) {
                listCombos = (ArrayList<ComboData>) obj;
                /*
                for (ComboData comboData : listCombos) {
                    //downloadSkus(comboData);
                }
                */
                Log.d(LOGTAG, "listCombos: " + listCombos.size());
                if (listCombos != null && listCombos.size() > currentDownloadIndex)
                    downloadSkus(listCombos.get(currentDownloadIndex));
            }

            @Override
            public void onResponseError(String errorMessage, int errorCode) {
                Log.e(LOGTAG, " getComboData errorMessage");
            }
        });
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            //final String action = intent.getAction();
            action = intent.getAction();
            if (ACTION_DOWNLOAD_SKUS.equals(action)) {
                listCombos = (ArrayList<ComboData>) intent.getSerializableExtra(EXTRA_PARAM_COMBOLIST);
                countCombosTobeFetched = intent.getIntExtra(EXTRA_PARAM_COUNT_COMBOS_TOBE_FETCHED, DEFAULT_COUNT_COMBOS_TOBE_DOWNLOADED);
                if (listCombos != null && listCombos.size() != 0) {
                    if (listCombos.size() > currentDownloadIndex)
                        downloadSkus(listCombos.get(currentDownloadIndex));
                } else {
                    getComboData(countCombosTobeFetched);
                }
            } else if (ACTION_DOWNLOAD_COMBOS.equals(action)) {
                ComboData comboData = (ComboData) intent.getSerializableExtra(EXTRA_PARAM_COMBO);
                int index = intent.getIntExtra(EXTRA_PARAM_COMBO_INDEX, 0);
                downloadSkus(comboData);
            } else if (ACTION_DOWNLOAD_COMBO_SHOP_SCREEN.equals(action)) {
                ComboData comboData = (ComboData) intent.getSerializableExtra(EXTRA_PARAM_COMBO);
                int index = intent.getIntExtra(EXTRA_PARAM_COMBO_INDEX, 0);
                downloadSkus(comboData);
            } else if (ACTION_DOWNLOAD_COMBOS_LAUNCH.equals(action)) {
                ComboData comboData = (ComboData) intent.getSerializableExtra(EXTRA_PARAM_COMBO);
                //int index =  intent.getIntExtra(EXTRA_PARAM_COMBO_INDEX,0);
                downloadSkus(comboData);
            } else if (ACTION_DOWNLOAD_RECONCILE_FACE_ACC.equals(action)) {
                //DataManager.getInstance().requestReconcileBags();
                //DataManager.getInstance().requestReconcileEarrings();
                // DataManager.getInstance().requestReconcileSunglasses();
            } else if (ACTION_DOWNLOAD_RECONCILE_BODY_ACC.equals(action)) {
                //DataManager.getInstance().requestReconcileClutches();
                //DataManager.getInstance().requestReconcileShoes();
            } else if (ACTION_DOWNLOAD_VIDEO.equals(action)) {
                String comboId = intent.getStringExtra(EXTRA_PARAM_COMBO_ID);
                String videoUrl = intent.getStringExtra(EXTRA_PARAM_VIDEO_URL);

                downloadVideo(videoUrl, comboId);
            } else if (ACTION_DOWNLOAD_MEMORY_CLEANUP.equals(action)) {
                memoryCleanup();
            }
        }
    }

    private void getComboById(String comboId) {
        DataManager.getInstance().requestComboDataInfoById(comboId, User.getInstance().getDefaultFaceId(),
                new DataManager.OnResponseHandlerInterface() {
                    @Override
                    public void onResponse(Object obj) {
                        ArrayList<ComboData> arrayListCombo = (ArrayList<ComboData>) obj;
                        if (arrayListCombo != null && arrayListCombo.size() > 0) {
                            ComboData comboData = arrayListCombo.get(0);
                            datasource.create(comboData);
                        }
                    }

                    @Override
                    public void onResponseError(String errorMessage, int errorCode) {
                        Log.e(LOGTAG, "Error getting comboInfo :" + errorMessage);

                    }
                });
    }

    private void downloadEarring() {

    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */



    /*private void downloadSkus(ComboData combodata) {
        combodata.countTobeDownloaded = 16;
        DownloadListener dListener = new DownloadListener(combodata);
        beginDownload(combodata.getmA1_Obj_Key_Name(), dListener);
        beginDownload(combodata.getmA1_Png_Key_Name(), dListener);
        beginDownload(combodata.getmA2_Obj_Key_Name(), dListener);
        beginDownload(combodata.getmA2_Png_Key_Name(), dListener);
        beginDownload(combodata.getmA6_Obj_Key_Name(), dListener);
        beginDownload(combodata.getmA6_Png_Key_Name(), dListener);
        beginDownload(combodata.getmA7_Obj_Key_Name(), dListener);
        beginDownload(combodata.getmA7_Png_Key_Name(), dListener);
        beginDownload(combodata.getmA8_Obj_Key_Name(), dListener);
        beginDownload(combodata.getmA8_Png_Key_Name(), dListener);
        beginDownload(combodata.getmA9_Obj_Key_Name(), dListener);
        beginDownload(combodata.getmA9_Png_Key_Name(), dListener);
        beginDownload(combodata.getmA10_Obj_Key_Name(), dListener);
        beginDownload(combodata.getmA10_Png_Key_Name(), dListener);
        beginDownload(combodata.getLegObjKeyName(), dListener);
        beginDownload(combodata.getLegTextureKeyName(), dListener);
    }

    private void beginDownload(String key, DownloadListener dListener) {
        ComboData comboData = dListener.getComboData();
        if (key == null || key.length() < 2) {
            Log.d(LOGTAG, "Service download key is null or blank ");
            comboData.countTobeDownloaded--;
            checkDownloads(comboData);
            return;
        }
        File file = new File(Environment.getExternalStorageDirectory().toString() + "/" + key);
        if (ConstantsUtil.checkFileExist(file.getAbsolutePath())) {
            Log.d(LOGTAG, "Service already downloaded : " + key);
            comboData.countTobeDownloaded--;
            checkDownloads(comboData);
            return;
        }
        Log.d(LOGTAG, "Service To be downloaded. file:  " + file);
        if(transferUtility == null) {
            Log.d(LOGTAG, " transferUtility is null. file:  " + file);
            return;
        }
        TransferObserver observer = transferUtility.download(ConstantsUtil.AWSBucketName, key, file);
        observer.setTransferListener(dListener);
        dListener.comboData.hmDownloadTrack.put(observer.getId(), key);
    }


    private class DownloadListener implements TransferListener {
        public DownloadListener() {
        }

        private ComboData comboData = null;
        String key = null;

        public DownloadListener(ComboData comboData) {
            this.comboData = comboData;
        }

        public ComboData getComboData() {
            return comboData;
        }

        public void setComboData(ComboData comboData) {
            this.comboData = comboData;
        }

        @Override
        public void onError(int id, Exception e) {
            Log.e(LOGTAG, "Services Error during download");
        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

        }

        @Override
        public void onStateChanged(int id, TransferState state) {
            if (state == TransferState.FAILED) {
                Log.e(LOGTAG, " Service Download failed");
                comboData.countDownloadFailed++;
            }
            else if (state == TransferState.COMPLETED) {
                Log.e(LOGTAG, "***** Service Download successful  *****");
                String key = comboData.hmDownloadTrack.get(id);
                if(key != null ) {
                    comboData.countTobeDownloaded--;
                    comboData.hmDownloadTrack.remove(id);
                }
                checkDownloads(comboData);
            }
        }
    }

    public void checkDownloads(ComboData comboData){
        if (comboData.countTobeDownloaded == 0){// && comboData.getIsDisplayReady() ==0) {//TODO
            comboData.hmDownloadTrack.clear();
            comboData.setIsDisplayReady(1);
            comboData = datasource.create(comboData);
            ComboData comboDataFromDB = datasource.getComboDataByComboID(comboData.getCombo_ID());
            comboDataFromDB.indexTemp =comboData.indexTemp;
            currentDownloadIndex++;
            sendLocalBroadcast(comboDataFromDB);
            if(listCombos != null && listCombos.size()>currentDownloadIndex)
                downloadSkus(listCombos.get(currentDownloadIndex));
        }
    }
    */
    public void sendLocalBroadcast(ComboData comboData) {
        Intent intent;
        if (ACTION_DOWNLOAD_SKUS.equals(action)) {
            intent = new Intent(INTENT_NAME_SKU_FILES_DOWNLOAD_SERVICE);
            if (countCombosTobeFetched == 1)
                intent.putExtra(EXTRA_PARAM_IS_NEXT_ACTION, true);
        } else if (ACTION_DOWNLOAD_COMBOS_LAUNCH.equals(action)) {
            intent = new Intent(INTENT_NAME_COMBOS_DOWNLOAD_SERVICE_LAUNCH);
        } else if (ACTION_DOWNLOAD_COMBO_SHOP_SCREEN.equals(action)) {
            intent = new Intent(INTENT_NAME_COMBOS_DOWNLOAD_SERVICE_SHOP_SCREEN);
        } else {
            intent = new Intent(INTENT_NAME_COMBOS_DOWNLOAD_SERVICE);
        }
        intent.putExtra("comboData", comboData);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionCombos(String param2) {
        // TODO: Handle action Baz
        //throw new UnsupportedOperationException("Not yet implemented");
    }


    /* download SKU */

    //***************************************************************************************************

    public void checkDownloads(ComboData comboData) {
        if (comboData.countTobeDownloaded == 0) {// && comboData.getIsDisplayReady() ==0) {//TODO
            comboData.setIsDisplayReady(1);
            comboData = datasource.create(comboData);
            ComboData comboDataFromDB = datasource.getComboDataByComboID(comboData.getCombo_ID());
            comboDataFromDB.indexTemp = comboData.indexTemp;
            sendLocalBroadcast(comboDataFromDB);
        }
    }

    protected void downloadSkus(ComboData combodata) {
        combodata.countTobeDownloaded = 12;
        //beginDownload(combodata.getLegItem().getObjAwsKey(), INDEX_DOWNLOAD_LEG_OBJ, combodata.getLegItem().getObjDStatus(), combodata);
        //beginDownload(combodata.getLegItem().getTexAwsKey(), INDEX_DOWNLOAD_LEG_TEX, combodata.getLegItem().getTexDStatus(), combodata);

//        beginDownload(combodata.getLegObjKeyName(), INDEX_DOWNLOAD_LEG_OBJ, -1, combodata);
//        beginDownload(combodata.getLegTextureKeyName(), INDEX_DOWNLOAD_LEG_TEX, -1, combodata);

        beginDownload(combodata.getLegItem().getObjAwsKey(), INDEX_DOWNLOAD_LEG_OBJ, combodata.getLegItem().getObjDStatus(), combodata);
        beginDownload(combodata.getLegItem().getTextureAwsKey(), INDEX_DOWNLOAD_LEG_TEX, combodata.getLegItem().getTextureDStatus(), combodata);

        beginDownload(combodata.getmA1_Obj_Key_Name(), INDEX_DOWNLOAD_A1_OBJ, combodata.getObjA1DStatus(), combodata);
        beginDownload(combodata.getmA1_Png_Key_Name(), INDEX_DOWNLOAD_A1_TEX, combodata.getTextureA1DStatus(), combodata);

        beginDownload(combodata.getmA6_Obj_Key_Name(), INDEX_DOWNLOAD_A6_OBJ, combodata.getObjA6DStatus(), combodata);
        beginDownload(combodata.getmA6_Png_Key_Name(), INDEX_DOWNLOAD_A6_TEX, combodata.getTextureA6DStatus(), combodata);

        beginDownload(combodata.getmA7_Obj_Key_Name(), INDEX_DOWNLOAD_A7_OBJ, combodata.getObjA7DStatus(), combodata);
        beginDownload(combodata.getmA7_Png_Key_Name(), INDEX_DOWNLOAD_A7_TEX, combodata.getTextureA7DStatus(), combodata);

        beginDownload(combodata.getmA9_Obj_Key_Name(), INDEX_DOWNLOAD_A9_OBJ, combodata.getObjA9DStatus(), combodata);
        beginDownload(combodata.getmA9_Png_Key_Name(), INDEX_DOWNLOAD_A9_TEX, combodata.getTextureA9DStatus(), combodata);

        beginDownload(combodata.getmA10_Obj_Key_Name(), INDEX_DOWNLOAD_A10_OBJ, combodata.getObjA10DStatus(), combodata);
        beginDownload(combodata.getmA10_Png_Key_Name(), INDEX_DOWNLOAD_A10_TEX, combodata.getTextureA10DStatus(), combodata);

    }


    private void beginDownload(String key, int downloadIndex, int downloadStatus, ComboData comboData) {
        if (key == null || key.length() < 2 || key.equals("null")) {
            Log.d(LOGTAG, "combo :" + comboData.getCombo_ID() + " download key is null or blank ");
            comboData.countTobeDownloaded--;
            if (comboData.countTobeDownloaded == 0)
                checkDownloads(comboData);
            return;
        }

        if (downloadStatus == ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus() && ConstantsUtil.checkFileKeysExist(key)) {
            Log.e(LOGTAG, " already downloaded : " + key);
            comboData.countTobeDownloaded--;
            if (comboData.countTobeDownloaded == 0)
                checkDownloads(comboData);
            return;
        }
        updateDownloadStatus(comboData, downloadIndex, ConstantsUtil.EDownloadStatusType.eDownloading.intStatus());
        File file = new File(ConstantsUtil.FILE_PATH_APP_ROOT + key);
        Log.d(LOGTAG, "combo :" + comboData.getCombo_ID() + "  to be downloaded. file:  " + key);
        if (transferUtility == null) {
            Log.d(LOGTAG, " transferUtility is null. file:  " + key);
            return;
        }
        TransferObserver observer = transferUtility.download(ConstantsUtil.AWSBucketName, key, file);
        Log.d(LOGTAG, " transferUtility " + key);
        DownloadListener dListener = new DownloadListener(key, downloadIndex, comboData);
        observer.setTransferListener(dListener);
        observers.add(observer);
    }

    private class DownloadListener implements TransferListener {
        public DownloadListener() {

        }

        private ComboData comboData = null;
        private String key = "";
        private int downloadIndex = -1;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public DownloadListener(String key, int downloadIndex, ComboData comboData) {
            this.comboData = comboData;
            this.key = key;
            this.downloadIndex = downloadIndex;
        }

        @Override
        public void onError(int id, Exception e) {
            Log.e(LOGTAG, e.getStackTrace() + " Error during download  :" + key);
            updateDownloadStatus(comboData, downloadIndex, ConstantsUtil.EDownloadStatusType.eDownloadError.intStatus());
            //downloadComboSkuFailed(comboData);
        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

        }

        @Override
        public void onStateChanged(int id, TransferState state) {
            if (state == TransferState.FAILED) {
                Log.e(LOGTAG, "combo :" + comboData.getCombo_ID() + " Download failed :" + key);
                //downloadComboSkuFailed(comboData);
            } else if (state == TransferState.COMPLETED) {
                Log.e(LOGTAG, "combo :" + comboData.getCombo_ID() + " ***** Download successful  *****  :" + key);
                comboData.countTobeDownloaded--;
                updateDownloadStatus(comboData, downloadIndex, ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus());
                checkDownloads(comboData);
            }
        }
    }

    private void updateDownloadStatus(ComboData comboData, int downloadIndex, int downloadType) {
        switch (downloadIndex) {
            case INDEX_DOWNLOAD_A1_OBJ: {
                comboData.setObjA1DStatus(downloadType);
            }
            break;
            case INDEX_DOWNLOAD_A1_TEX: {
                comboData.setTextureA1DStatus(downloadType);
            }
            break;
            case INDEX_DOWNLOAD_A6_OBJ: {
                comboData.setObjA6DStatus(downloadType);
            }
            break;
            case INDEX_DOWNLOAD_A6_TEX: {
                comboData.setTextureA6DStatus(downloadType);
            }
            break;
            case INDEX_DOWNLOAD_A7_OBJ: {
                comboData.setObjA7DStatus(downloadType);
            }
            break;
            case INDEX_DOWNLOAD_A7_TEX: {
                comboData.setTextureA7DStatus(downloadType);
            }
            break;

            case INDEX_DOWNLOAD_A9_OBJ: {
                comboData.setObjA9DStatus(downloadType);
            }
            break;
            case INDEX_DOWNLOAD_A9_TEX: {
                comboData.setTextureA9DStatus(downloadType);
            }
            break;
            case INDEX_DOWNLOAD_A10_OBJ: {
                comboData.setObjA10DStatus(downloadType);
            }
            break;
            case INDEX_DOWNLOAD_A10_TEX: {
                comboData.setTextureA10DStatus(downloadType);
            }
            break;
            default:
                break;
        }
    }


    /********************************
     * VIDEO
     *******************************************************************/

    public void sendVideoLocalBroadcast(String comboId, String videoKey, Uri uri, int errorCode) {
        Intent intent = new Intent(INTENT_NAME_VIDEO_DOWNLOAD_SERVICE_LAUNCH);
        intent.putExtra(DataManager.ERROR_CODE_KEY, errorCode);
        intent.putExtra(Video360Activity.INTENT_KEY_COMBO_ID, comboId);
        intent.putExtra(Video360Activity.INTENT_KEY_VIDEO_PATH, videoKey);
        intent.putExtra(Video360Activity.INTENT_KEY_VIDEO_URI, uri);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void downloadVideo(final String uri, final String comboId) {
        DataManager.getInstance().requestCreateVideo(uri, new DataManager.OnResponseHandlerInterface() {
            @Override
            public void onResponse(Object obj) {

                String videoKey = (String) obj;
                beginVideoDownload(videoKey, comboId);
            }

            @Override
            public void onResponseError(String errorMessage, int errorCode) {

                sendVideoLocalBroadcast(comboId, "", null, errorCode);
            }
        });
    }

    private void beginVideoDownload(String key, String comboId) {
        if (key == null || key.length() < 2 || key.equals("null")) {
            Log.d(LOGTAG, "combo :" + comboId + " download key is null or blank ");
            sendVideoLocalBroadcast(comboId, "", null, 300);
            return;
        }

        File file = new File(ConstantsUtil.FILE_PATH_APP_ROOT_VIDEO + key);
        if (ConstantsUtil.checkFileKeysExist(key)) {
            Log.e(LOGTAG, "**deleted already downloaded : " + key);
            file.delete();
            return;
        }

        Log.d(LOGTAG, "combo :" + comboId + "  to be downloaded. Video file:  " + key);
        if (transferUtility == null) {
            Log.d(LOGTAG, " transferUtility is null. file:  " + key);
            return;
        }
        TransferObserver observer = transferUtility.download(ConstantsUtil.AWSBucketName, key, file);
        Log.d(LOGTAG, " transferUtility " + key);
        DownloadVideoListener dListener = new DownloadVideoListener(key, comboId);
        observer.setTransferListener(dListener);
        observers.add(observer);
    }

    private class DownloadVideoListener implements TransferListener {
        public DownloadVideoListener() {

        }

        private String comboId = null;
        private String key = "";

        public DownloadVideoListener(String key, String comboId) {
            this.comboId = comboId;
            this.key = key;
        }

        @Override
        public void onError(int id, Exception e) {
            Log.e(LOGTAG, e.getStackTrace() + " Error during download  :" + key);
            sendVideoLocalBroadcast(comboId, "", null, 300);
        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

        }

        @Override
        public void onStateChanged(int id, TransferState state) {
            if (state == TransferState.FAILED) {
                Log.e(LOGTAG, "combo :" + comboId + " Video Download failed :" + key);
                sendVideoLocalBroadcast(comboId, "", null, 300);

            } else if (state == TransferState.COMPLETED) {
                Log.e(LOGTAG, "combo :" + comboId + " ***** Video Download successful  *****  :" + key);
                Uri contentUri = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    //File f = new File("file://"+ Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
                    File file = new File(ConstantsUtil.FILE_PATH_APP_ROOT_VIDEO + key);
                    contentUri = Uri.fromFile(file);
                    Log.w(LOGTAG, "360 Uri: " + contentUri);
                    mediaScanIntent.setData(contentUri);
                    sendBroadcast(mediaScanIntent);
                } else {
                    File file = new File(ConstantsUtil.FILE_PATH_APP_ROOT + key);
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));

//                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + ConstantsUtil.FILE_PATH_APP_ROOT + key)));
//                    //file.delete();
//
//                   String ImagePath = MediaStore.Images.Media.insertImage(
//                            getContentResolver(),
//                            bitmap,
//                            "demo_image",
//                            "demo_image"
//                    );

//                    String ImagePath = MediaStore.Images.Media.insertImage(
//                            getContentResolver(),
//                            bitmap,
//                            "demo_image",
//                            "demo_image"
//                    );

                    //Uri URI = Uri.parse(ImagePath);
                }

                checkVideoDownloads(comboId, key, contentUri);
            }
        }
    }

    private void checkVideoDownloads(String comboId, String videoKey, Uri uri) {
        sendVideoLocalBroadcast(comboId, videoKey, uri, 0);
    }

    public static int AVERAGE_SIZE_ACCESSORY = 1;
    public static int AVERAGE_SIZE_FACE_ITEM = 4;
    public static int AVERAGE_SIZE_COMBO = 5;

    //
    private void memoryCleanup() {
        int space = overUsedSpace();
        Log.e(LOGTAG, "delete space diff in MB: " + space);
        //space =20;
        if (space < 10)
            return;
        int comboCount = (int) (space * 0.70 / AVERAGE_SIZE_COMBO);
        int accCount = (int) (space * 0.15 / AVERAGE_SIZE_ACCESSORY);
        int faceCount = (int) (space * 0.15 / AVERAGE_SIZE_FACE_ITEM);
        //accCount = 12;
        //faceCount = 10; //TODO
        cleanupCombo(comboCount);
        cleanupFace(faceCount);
        cleanupAccessory(accCount);
        cleanCompaign();
    }

    private void cleanCompaign(){
        int countActive =0;
        ArrayList<LookBoardItem> list = (ArrayList<LookBoardItem>) datasource.getComboLookBoard(false);
        for(LookBoardItem item:list){
            if(!item.getStatus().equals("Active")){
                deleteDirectory(new File(ConstantsUtil.FILE_PATH_APP_ROOT + item.getImage_Key_Name()));
            }else {
                countActive++;
                if(countActive>20){
                    deleteDirectory(new File(ConstantsUtil.FILE_PATH_APP_ROOT + item.getImage_Key_Name()));
                }
                if(item.getStatus().equals("Deleted") ){
                    datasource.delete(item);
                }
            }
        }
    }

    private void cleanupFace(int faceCount) {
        ArrayList<FaceItem> faceList = (ArrayList<FaceItem>) datasource.getAvatars();

        int status = ConstantsUtil.EDownloadStatusType.eDownloadTobeStarted.intStatus();
        if (faceList != null) {//141,18,2
            Log.d(LOGTAG, "delete face size: " + faceList.size());
            int i = 0;
            for (FaceItem faceItem : faceList) {
                Log.d(LOGTAG, "delete face:" + faceItem.getFaceId());

                if (faceItem.getFaceId().equals("1")
                        || faceItem.getFaceId().equals(User.getInstance().getDefaultFaceId())  || (User.getInstance().getDefaultFaceItem() != null && faceItem.getFaceId().equals(User.getInstance().getDefaultFaceItem().getFaceId())))//TODO
                    continue;
                if( i > faceCount)
                    break;
                File file = new File(ConstantsUtil.FILE_PATH_APP_ROOT+ConstantsUtil.FILE_PATH_AWS_KEY_ROOT + User.getInstance().getmUserId() + "/faces/" + faceItem.getFaceId());
                Log.d("AAA", file.getAbsolutePath());
                ConstantsUtil.deleteDirectory(file);
                faceItem.setObjBodyDStatus(status);
                faceItem.setTextureBodyDStatus(status);
                faceItem.setObjHairDStatus(status);
                faceItem.setTextureHairDStatus(status);
                faceItem.setObjFaceDStatus(status);
                faceItem.setTextureFaceDStatus(status);
                datasource.create(faceItem);
                i++;
            }
        }
    }

    private void cleanupLookBoard(){


    }


    private void cleanupCombo(int comboCount) {
        ArrayList<ComboData> comboList = (ArrayList<ComboData>) datasource.getDownloadedComboData(comboCount);

        int status = ConstantsUtil.EDownloadStatusType.eDownloadTobeStarted.intStatus();
        if (comboList != null) {//141,18,2
            Log.d(LOGTAG, "delete combos size: " + comboList.size());
            for (ComboData combo : comboList) {
                Log.d(LOGTAG, "delete Combo:" + combo.getCombo_ID());
                if(combo.getmA1_Obj_Key_Name()!= null && !combo.getmA1_Obj_Key_Name().isEmpty()) {
                    deleteDirectory(new File(ConstantsUtil.FILE_PATH_APP_ROOT + combo.getmA1_Obj_Key_Name()));
                }

                deleteDirectory(new File(ConstantsUtil.FILE_PATH_APP_ROOT + combo.getmA1_Png_Key_Name()));
                deleteDirectory(new File(ConstantsUtil.FILE_PATH_APP_ROOT + combo.getCombo_PIC_Png_Key_Name()));
                combo.setObjA1DStatus(status);
                combo.setTextureA1DStatus(status);

                deleteDirectory(new File(ConstantsUtil.FILE_PATH_APP_ROOT + combo.getmA6_Obj_Key_Name()));
                deleteDirectory(new File(ConstantsUtil.FILE_PATH_APP_ROOT + combo.getmA6_Png_Key_Name()));

                combo.setObjA6DStatus(status);
                combo.setTextureA6DStatus(status);

                deleteDirectory(new File(ConstantsUtil.FILE_PATH_APP_ROOT + combo.getmA7_Obj_Key_Name()));
                deleteDirectory(new File(ConstantsUtil.FILE_PATH_APP_ROOT + combo.getmA7_Png_Key_Name()));
                combo.setObjA7DStatus(status);
                combo.setTextureA7DStatus(status);

                deleteDirectory(new File(ConstantsUtil.FILE_PATH_APP_ROOT + combo.getmA9_Obj_Key_Name()));
                deleteDirectory(new File(ConstantsUtil.FILE_PATH_APP_ROOT + combo.getmA9_Png_Key_Name()));
                combo.setObjA9DStatus(status);
                combo.setTextureA9DStatus(status);

                deleteDirectory(new File(ConstantsUtil.FILE_PATH_APP_ROOT + combo.getmA10_Obj_Key_Name()));
                deleteDirectory(new File(ConstantsUtil.FILE_PATH_APP_ROOT + combo.getmA10_Png_Key_Name()));
                combo.setObjA10DStatus(status);
                combo.setTextureA10DStatus(status);
                combo.setIsDisplayReady(0);

                datasource.create(combo);
            }
        }
    }

    private void cleanupAccessory(int accCount) {
        if (accCount > 10) {
            ArrayList<BaseAccessoryItem> itemsB = (ArrayList<BaseAccessoryItem>) datasource.getDownloadedAccessories(ConstantsUtil.EAccessoryType.eAccTypeBags.toString(), accCount / 6);
            ArrayList<BaseAccessoryItem> itemsC = (ArrayList<BaseAccessoryItem>) datasource.getDownloadedAccessories(ConstantsUtil.EAccessoryType.eAccTypeClutches.toString(), accCount / 6);
            if (itemsB == null)
                itemsB = new ArrayList<>();
            if (itemsC != null)
                itemsB.addAll(itemsC);
            ArrayList<BaseAccessoryItem> itemsH = (ArrayList<BaseAccessoryItem>) datasource.getDownloadedAccessories(ConstantsUtil.EAccessoryType.eAccTypeHair.toString(), accCount / 6);
            if (itemsC != null)
                itemsB.addAll(itemsH);
            ArrayList<BaseAccessoryItem> itemsSpecs = (ArrayList<BaseAccessoryItem>) datasource.getDownloadedAccessories(ConstantsUtil.EAccessoryType.eAccTypeSpecs.toString(), accCount / 6);
            if (itemsC != null)
                itemsB.addAll(itemsSpecs);
            ArrayList<BaseAccessoryItem> itemsSun = (ArrayList<BaseAccessoryItem>) datasource.getDownloadedAccessories(ConstantsUtil.EAccessoryType.eAccTypeSunglasses.toString(), accCount / 6);
            if (itemsC != null)
                itemsB.addAll(itemsSun);

            if (User.getInstance().getmGender().equals("f")) {
                ArrayList<BaseAccessoryItem> itemsE = (ArrayList<BaseAccessoryItem>) datasource.getDownloadedAccessories(ConstantsUtil.EAccessoryType.eAccTypeHair.toString(), accCount / 6);
                if (itemsE != null)
                    itemsB.addAll(itemsC);
            }

            if (itemsB != null) {
                Log.d(LOGTAG, "delete acc size:" + itemsB.size());
                int status = ConstantsUtil.EDownloadStatusType.eDownloadTobeStarted.intStatus();
                for (BaseAccessoryItem i : itemsB) {
                    Log.d(LOGTAG, "delete Acc:" + i.getObjId());
                    deleteDirectory(new File(ConstantsUtil.FILE_PATH_APP_ROOT + i.getObjAwsKey()));
                    deleteDirectory(new File(ConstantsUtil.FILE_PATH_APP_ROOT + i.getTextureAwsKey()));
                    deleteDirectory(new File(ConstantsUtil.FILE_PATH_APP_ROOT + i.getThumbnailAwsKey()));
                    i.setObjDStatus(status);
                    i.setTextureDStatus(status);
                    datasource.create(i);
                }
            }
        }
    }

    public static boolean deleteDirectory(File path) {
        String pathstr = path.getAbsolutePath();
        Log.e(LOGTAG, "**deleted already downloaded : " + path.getAbsolutePath());
        if(pathstr.equals(ConstantsUtil.FILE_PATH_APP_ROOT) || pathstr.equals("/storage/emulated/0/.inkarne_root")){
            return false;
        }
        //if(pathstr.equals(ConstantsUtil.FILE_PATH_APP_ROOT+"inkarne"))


        if (path != null && path.exists()) {
            if (!path.isDirectory()) {
                path.delete();
                return true;
            }
            File[] files = path.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isDirectory()) {
                        deleteDirectory(files[i]);
                    } else {
                        files[i].delete();
                    }
                }
            }
            return path.delete();
        }
        return false;
    }

    public static void deleteDirectory2(File fileOrDirectory) {
        Log.d(LOGTAG, "delete :" + fileOrDirectory.toString());
        if (fileOrDirectory == null || !fileOrDirectory.exists()) {
            //Log.d(LOGTAG, "deleteDirectory return"+ fileOrDirectory);
            return;
        }

      // File [] files = fileOrDirectory.listFiles();
        if (fileOrDirectory.isDirectory() && fileOrDirectory.listFiles() != null) {
            for (File child : fileOrDirectory.listFiles())
                deleteDirectory(child);
        }
        fileOrDirectory.delete();
    }

    private static int overUsedSpace() {
        return totalSpaceOfAppDownloads() - availableSpace();

    }

    private static int availableSpace1() {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long bytesAvailable = (long) stat.getBlockSize() * (long) stat.getAvailableBlocks();
        long megAvailable = bytesAvailable / (1024 * 1024);
        Log.e(LOGTAG, "Available MB : " + megAvailable);
        return (int) megAvailable;
    }

    private static int availableSpace() {
        File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        //Environment.getDataDirectory().getUsableSpace();
        long space = f.getUsableSpace() / (1024 * 1024);
        Log.e(LOGTAG, "available MB : " + space);
        int space5Percent = (int) (space * ConstantsFunctional.PERCENTAGE_OF_FREE_SPACE_FOR_APP / 100);
        Log.e(LOGTAG, "5% for available space MB : " + space5Percent);
        int actualAvailableSpace = Math.min(space5Percent, ConstantsFunctional.MAX_LIMIT_FREE_SPACE_FOR_APP_IN_MB);
        Log.e(LOGTAG, "Actual available space for App MB : " + actualAvailableSpace);
        return actualAvailableSpace;
    }

    private static int totalSpaceOfAppDownloads() {
        File f = new File(ConstantsUtil.FILE_PATH_APP_ROOT + "inkarne");
        long usedSpace = ConstantsUtil.getFolderSize(f) / (1024 * 1024);
        Log.e(LOGTAG, "total space used by App MB : " + usedSpace);
        return (int) usedSpace;
    }

}
