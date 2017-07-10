package com.svc.sml.Helper;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.svc.sml.Activity.OnAssetDownloadListener;
import com.svc.sml.Model.BaseAccessoryItem;
import com.svc.sml.Model.BaseItem;
import com.svc.sml.Model.TransferProgressModel;
import com.svc.sml.Utility.AWSUtil;
import com.svc.sml.Utility.ConstantsUtil;
import com.svc.sml.Utility.Unzip;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by himanshu on 4/18/16.
 */
public class AssetDownloader {
    private final static String LOGTAG = AssetDownloader.class.toString();
    private Context context;
    private WeakReference<OnAssetDownloadListener> onAssetDownloadWeakListener;
    private OnAssetDownloadListener onAssetDownloadListener;
    private TransferUtility transferUtility;
    private boolean hasDependentItem = false;
    protected ArrayList<TransferObserver> observers = new ArrayList<TransferObserver>();

    //private HashMap<String, TransferObserver> observersHaspMap = new HashMap<>();
    //private CountDownLatch doneSignal = new CountDownLatch(1);


    public AssetDownloader(Context context) {
        //this.context = context;
        this.transferUtility = AWSUtil.getTransferUtility(context);
//        int SDK_INT = android.os.Build.VERSION.SDK_INT;
//        if (android.os.Build.VERSION.SDK_INT > 9) {
//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//            StrictMode.setThreadPolicy(policy);
//        }
    }

    protected void removeTransferListener() {
        for (TransferObserver ob : observers) {
            if (transferUtility != null)
                transferUtility.cancel(ob.getId());
            ob.cleanTransferListener();
        }
        observers.clear();
    }

    /*
    public void downloadAsset(String objKey,String textureKey, OnAssetDownloadListener onAssetDownloadListener) throws IOException {
        if(objKey == null || textureKey == null)
            return;
        BaseAccessoryItem item = new BaseAccessoryItem(objKey,textureKey);
        downloadAsset(item,onAssetDownloadListener);
    }
    */
    public void downloadAsset(String objKey, String textureKey, OnAssetDownloadListener onAssetDownloadListener) {
        if (objKey == null || textureKey == null)
            return;
        BaseAccessoryItem item = new BaseAccessoryItem();
        item.setTextureAwsKey(textureKey);
        item.setObjAwsKey(objKey);
        downloadAsset(item, onAssetDownloadListener);
    }

    public void downloadAsset(String objKey, int objDStatus, String textureKey, int textDStatus, OnAssetDownloadListener onAssetDownloadListener) {
        if (objKey == null || textureKey == null)
            return;
        BaseAccessoryItem item = new BaseAccessoryItem(objKey, objDStatus, textureKey, textDStatus);
        downloadAsset(item, onAssetDownloadListener);
    }

    public void downloadAsset(String objKey, String textureKey, String objKey2, String textureKey2, OnAssetDownloadListener onAssetDownloadListener) {
        if (objKey == null || textureKey == null)
            return;
        BaseAccessoryItem item = new BaseAccessoryItem(objKey, textureKey);
        BaseAccessoryItem dependentItem = new BaseAccessoryItem(objKey2, textureKey2);
        item.dependentItem = dependentItem;
        downloadAsset(item, onAssetDownloadListener);
    }


    public void downloadAsset(BaseAccessoryItem item, OnAssetDownloadListener onAssetDownloadListener) {
        if (item == null)
            return;

        this.onAssetDownloadWeakListener = new WeakReference<OnAssetDownloadListener>(onAssetDownloadListener);
        this.onAssetDownloadListener = onAssetDownloadListener;
        //&& !item.dependentItem.getObjId().equals("NA")
        if (item.dependentItem != null  && item.dependentItem.getObjAwsKey() != null && !item.dependentItem.getObjAwsKey().isEmpty()) {
            item.countTobeDownloaded = 4;
            hasDependentItem = true;
            beginGraphicsDownload(item, item.getObjAwsKey(), ConstantsUtil.EDownloadType.eDownloadTypeObj);
            beginGraphicsDownload(item, item.getTextureAwsKey(), ConstantsUtil.EDownloadType.eDownloadTypeTexture);
            beginGraphicsDownload(item, item.dependentItem.getObjAwsKey(), ConstantsUtil.EDownloadType.eDownloadTypeObj2);
            beginGraphicsDownload(item, item.dependentItem.getTextureAwsKey(), ConstantsUtil.EDownloadType.eDownloadTypeTexture2);
        } else {
            item.countTobeDownloaded = 2;
            beginGraphicsDownload(item, item.getObjAwsKey(), ConstantsUtil.EDownloadType.eDownloadTypeObj);
            beginGraphicsDownload(item, item.getTextureAwsKey(), ConstantsUtil.EDownloadType.eDownloadTypeTexture);
        }
    }

    private boolean isDownloaded(BaseAccessoryItem item, String key, ConstantsUtil.EDownloadType downloadType) {
        if (hasDependentItem) {
            return ((downloadType == ConstantsUtil.EDownloadType.eDownloadTypeObj && item.getObjDStatus() == ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus())
                    || (downloadType == ConstantsUtil.EDownloadType.eDownloadTypeTexture && item.getTextureDStatus() == ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus())
                    || (downloadType == ConstantsUtil.EDownloadType.eDownloadTypeObj2 && item.dependentItem.getObjDStatus() == ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus())
                    || (downloadType == ConstantsUtil.EDownloadType.eDownloadTypeTexture2 && item.dependentItem.getTextureDStatus() == ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus()))
                    && ConstantsUtil.checkFileKeyExist(key);

        } else {
            return ((downloadType == ConstantsUtil.EDownloadType.eDownloadTypeObj && item.getObjDStatus() == ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus())
                    || (downloadType == ConstantsUtil.EDownloadType.eDownloadTypeTexture && item.getTextureDStatus() == ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus()))
                    && ConstantsUtil.checkFileKeyExist(key);
        }
    }

    private boolean beginDownloadCheck1(BaseAccessoryItem item, String key, ConstantsUtil.EDownloadType downloadType) {
        if (hasDependentItem) {
            return ConstantsUtil.checkFileKeyExist(key);
        } else {
            return ConstantsUtil.checkFileKeyExist(key);
        }
    }

    //png and obj download
    private void beginGraphicsDownload(final BaseAccessoryItem item, String key, ConstantsUtil.EDownloadType downloadType) {
        if (key == null || key.length() == 0) {
            Log.d(LOGTAG, "key is null or blank");
            item.countTobeDownloaded--;
            checkDownload(item);
//                if (item.countTobeDownloaded == 0) {
//                    onDownloadComplete(item);
//                }
            return;
        }

        Log.w(LOGTAG, "To be downloaded. key:  " + key);
        if (isDownloaded(item, key.replace(".gz",".ply"), downloadType)) {
            Log.e(LOGTAG, "already downloaded :" + key);
            item.countTobeDownloaded--;
            checkDownload(item);
//                if (item.countTobeDownloaded == 0) {
//                    onDownloadComplete(item);
//                }
            return;
        }
        if(key.contains(".ply") ){ //TODO zip//&& !key.contains("bodies/") &&  !key.contains("legs/")
            key = key.replace(".ply",".gz");
        }
        File file = new File(ConstantsUtil.FILE_PATH_APP_ROOT + key);
        file.delete();//TODO
        TransferProgressModel tm = new TransferProgressModel(key);
        if (downloadType == ConstantsUtil.EDownloadType.eDownloadTypeObj) {
            item.setTransferModelObj(tm);
        } else if (downloadType == ConstantsUtil.EDownloadType.eDownloadTypeTexture) {
            item.setTransferModelTexture(tm);
        } else if (downloadType == ConstantsUtil.EDownloadType.eDownloadTypeObj2) {
            item.dependentItem.setTransferModelObj(tm);
        } else if (downloadType == ConstantsUtil.EDownloadType.eDownloadTypeTexture2) {
            item.dependentItem.setTransferModelTexture(tm);
        }

        TransferObserver observer = transferUtility.download(ConstantsUtil.AWSBucketName, key, file);
        observer.setTransferListener(new DownloadListenerRenderingObj(item, key, downloadType));
        observers.add(observer);
    }


    private class DownloadListenerRenderingObj implements TransferListener {

        public ConstantsUtil.EDownloadType eDType;
        private BaseAccessoryItem item;
        private String key;

        public DownloadListenerRenderingObj(final BaseAccessoryItem item, String key, ConstantsUtil.EDownloadType downloadType) {
            this.item = item;
            this.key = key;
            this.eDType = downloadType;
        }

        @Override
        public void onError(int id, Exception e) {
            Log.e(LOGTAG, "download error :" + key);
            Log.e(LOGTAG, "download error message :" + e.getMessage());
            onDownloadFailed(item, key);
        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            int p = (int) (bytesCurrent * 360 / bytesTotal);
            if (eDType == ConstantsUtil.EDownloadType.eDownloadTypeObj) {
                item.getTransferModelObj().progress = p;
            } else if (eDType == ConstantsUtil.EDownloadType.eDownloadTypeTexture) {
                item.getTransferModelTexture().progress = p;
            } else if (eDType == ConstantsUtil.EDownloadType.eDownloadTypeObj2) {
                item.dependentItem.getTransferModelObj().progress = p;
            } else if (eDType == ConstantsUtil.EDownloadType.eDownloadTypeTexture2) {
                item.dependentItem.getTransferModelTexture().progress = p;
            }
            int cumulativeProgress = 0;
            if (item.getTransferModelObj() != null && item.getTransferModelTexture() != null)
                cumulativeProgress = item.getTransferModelObj().progress + item.getTransferModelTexture().progress;
            if (item.dependentItem != null && item.dependentItem.getTransferModelObj() != null && item.dependentItem.getTransferModelTexture() != null) {
                cumulativeProgress += item.dependentItem.getTransferModelObj().progress + item.dependentItem.getTransferModelTexture().progress;
                cumulativeProgress /= (4);
            } else {
                cumulativeProgress /= (2);
            }
            onDownloadProgress(item, cumulativeProgress);
        }

        @Override
        public void onStateChanged(int id, TransferState state) {

            if (eDType == ConstantsUtil.EDownloadType.eDownloadTypeObj) {
                if(item.getTransferModelObj() != null)
                item.getTransferModelObj().transferState = state;
            } else if (eDType == ConstantsUtil.EDownloadType.eDownloadTypeTexture) {
                if(item.getTransferModelTexture() != null)
                item.getTransferModelTexture().transferState = state;
            } else if (eDType == ConstantsUtil.EDownloadType.eDownloadTypeObj2) {
                if(item.dependentItem.getTransferModelObj() != null)
                item.dependentItem.getTransferModelObj().transferState = state;
            } else if (eDType == ConstantsUtil.EDownloadType.eDownloadTypeTexture2) {
                if(item.dependentItem.getTransferModelTexture() != null)
                item.dependentItem.getTransferModelTexture().transferState = state;
            }
            if (state == TransferState.FAILED) {
                Log.e(LOGTAG, " download failed :" + key);

                onDownloadFailed(item, key);
            }
            if (state == TransferState.COMPLETED) {
                Log.e(LOGTAG, " **** download complete ***** :" + key);

                 /* new change*/
                Log.e(LOGTAG, " downloaded key :" + key);
                String unZipFile = "";
                if(key.contains(".gz")) {
                    Log.e(LOGTAG,"zip :"+key);
                    unZipFile = Unzip.getUnzipPlyFileName(ConstantsUtil.FILE_PATH_APP_ROOT + key);
                    Log.e(LOGTAG, "unzip File :" + unZipFile);
                    unZipFile = key.replace(".gz",".ply");
                }
                int downloadStatus = ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus();
                if(eDType == ConstantsUtil.EDownloadType.eDownloadTypeObj){
                    item.setObjDStatus(downloadStatus);
                    if(unZipFile != null && !unZipFile.isEmpty())
                        item.setObjAwsKey(unZipFile);
                    //dataSource.create(item);
                }else if(eDType == ConstantsUtil.EDownloadType.eDownloadTypeTexture){
                    item.setTextureDStatus(downloadStatus);
                    //dataSource.create(item);

                }else if(eDType == ConstantsUtil.EDownloadType.eDownloadTypeObj2){
                    if (item.dependentItem != null ){
                        item.dependentItem.setObjDStatus(downloadStatus);
                        if(unZipFile != null && !unZipFile.isEmpty())
                            item.dependentItem.setObjAwsKey(unZipFile);
                        //dataSource.create(item.dependentItem);
                    }
                } else if(eDType == ConstantsUtil.EDownloadType.eDownloadTypeTexture2){
                    if (item.dependentItem != null ){
                        item.dependentItem.setTextureDStatus(downloadStatus);
                        //dataSource.create(item.dependentItem);
                    }
                }


                /*
                if (item.getTransferModelObj() != null && item.getTransferModelObj().transferState == TransferState.COMPLETED) {
                    item.setObjDStatus(ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus());
                    //dataSource.create(item);
                }
                if (item.getTransferModelTexture() != null && item.getTransferModelTexture().transferState == TransferState.COMPLETED) {
                    item.setTextureDStatus(ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus());
                    //dataSource.create(item);
                }
                if (item.dependentItem != null) {
                    if (item.dependentItem.getTransferModelObj() != null && item.dependentItem.getTransferModelObj().transferState == TransferState.COMPLETED) {
                        item.dependentItem.setObjDStatus(ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus());
                    }
                    if (item.dependentItem.getTransferModelTexture() != null && item.dependentItem.getTransferModelTexture().transferState == TransferState.COMPLETED) {
                        item.dependentItem.setTextureDStatus(ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus());
                    }
                    //dataSource.create(item.dependentItem);
                }
                */

                this.item.countTobeDownloaded--;
                checkDownload(item);
//                if (item.countTobeDownloaded == 0) {
//                    onDownloadComplete(item);
//                }
            }
        }
    }

    private void checkDownload(BaseAccessoryItem item){
        if (item.countTobeDownloaded <= 0 && isAllDownloaded(item) ) {
            onDownloadComplete(item);
        }
    }

    private boolean isAllDownloaded(BaseAccessoryItem item){
        int downloadedStatus = ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus();
        if(item.getObjDStatus() == downloadedStatus && item.getTextureDStatus() == downloadedStatus){
            if(item.dependentItem != null){
                if(item.dependentItem.getObjDStatus() == downloadedStatus && item.dependentItem.getTextureDStatus() == downloadedStatus){
                    return true;
                }
            }else{
                return true;
            }
        }
        return false;
    }

    private void onDownloadComplete(BaseAccessoryItem item) {
        removeTransferListener();
//        if(onAssetDownloadWeakListener != null) {
//            OnAssetDownloadListener listener =  onAssetDownloadWeakListener.get();
//            if(listener != null) {
//                listener.onDownload(item);
//                Log.w(LOGTAG,"****** listener not null");
//            }
//        }

        if (onAssetDownloadListener != null)
            onAssetDownloadListener.onDownload(item);

    }

    private void onDownloadFailed(BaseAccessoryItem item, String key) {
        removeTransferListener();
//        if(onAssetDownloadWeakListener != null) {
//            OnAssetDownloadListener listener =  onAssetDownloadWeakListener.get();
//            if(listener != null)
//                listener.onDownloadFailed(item.getObjId());
//        }
        Log.e(LOGTAG, "download failed :"+key);
        if (onAssetDownloadListener != null)
            onAssetDownloadListener.onDownloadFailed(item.getObjId());

    }

    private void onDownloadProgress(BaseAccessoryItem item, int progressPercentage) {
//        if(onAssetDownloadWeakListener != null) {
//            OnAssetDownloadListener listener =  onAssetDownloadWeakListener.get();
//            if(listener != null)
//                listener.onDownloadProgress(item.getObjId(),progressPercentage);
//        }

        if (onAssetDownloadListener != null)
            onAssetDownloadListener.onDownloadProgress(item.getObjId(), progressPercentage);
    }

    /*******************************************************************************/
    ////
    public class DownloadWorkerTask extends AsyncTask<String, Void, Boolean> {
        private BaseItem baseItem;
        private OnAssetDownloadListener onAssetDownloadListener;

        public DownloadWorkerTask(BaseItem baseItem) {
            this.baseItem = baseItem;
        }

        public DownloadWorkerTask(BaseItem baseItem, OnAssetDownloadListener onAssetDownloadListener) {
            this.baseItem = baseItem;
            this.onAssetDownloadListener = onAssetDownloadListener;
        }

//        public DownloadWorkerTask(){
//        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Boolean doInBackground(String... params) {

            boolean isDownload1 = downloadAsset(params[0]);
            boolean isDownload2 = downloadAsset(params[1]);
            return isDownload1 && isDownload2;
        }

        @Override
        protected void onPostExecute(Boolean filePath) {
            //doneSignal.countDown();
//            String obj = ConstantsUtil.FILE_PATH_APP_ROOT + baseItem.getObjAwsKey();
//            String texture = ConstantsUtil.FILE_PATH_APP_ROOT + baseItem.getTextureAwsKey();
//            Log.d(LOGTAG, " download body obj  " + obj + " and texture obj " + texture);
//            if (ConstantsUtil.checkFilesExist(texture, obj)) {
//                Log.d(LOGTAG, " downloaded " + obj + " and texture obj " + texture);
//                if(onAssetDownloadListener != null)
//                 onAssetDownloadListener.onDownload();
//            }else{
//                if(onAssetDownloadListener != null)
//                    onAssetDownloadListener.onDownloadFailed();
//            }
        }
    }

    public boolean downloadAsset(String key) {
//      AWSCredentials credential = new BasicAWSCredentials(ConstantsUtil.AWSAccessKey, ConstantsUtil.AWSSecretKey);
//      AmazonS3Client s3Client = new AmazonS3Client(credential);
        AmazonS3Client s3Client = AWSUtil.getS3Client(context);
        //key = "inkarne/pics/combo/test/bg_shop_screen_female.png";

        String filePath = ConstantsUtil.FILE_PATH_APP_ROOT + key;
        File file = new File(filePath);
        String dirPath = file.getParent();

//        String[] separated = key.split("/");
//        String str_FilePathInDevice = "/sdcard/" + "/" + "AWSInkarne" + "/" + separated[separated.length - 1];
//        Toast.makeText(context, separated[separated.length - 1], Toast.LENGTH_SHORT).show();
        File fileDir = new File(dirPath);
        //String str_Path = file.getPath().replace(file.getName(), "");
        //File fileDir = new File(str_Path);
        try {
            fileDir.mkdirs();
        } catch (Exception ex1) {
        }
        S3Object object = s3Client.getObject(new GetObjectRequest(
                ConstantsUtil.AWSBucketName, key));

        BufferedReader reader = new BufferedReader(new InputStreamReader(
                object.getObjectContent()));
        Writer writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(file));
            while (true) {
                String line = reader.readLine();
                if (line == null)
                    break;
                writer.write(line + "\n");
            }
            writer.flush();
            writer.close();
            reader.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    private void beginDownload1(String key, BaseItem baseItem) {
        if (key == null || key.length() == 0) {
            Log.d(LOGTAG, "key is null or blank");
            //doneSignal.countDown();
            return;
        }


        Log.d(LOGTAG, "To be downloaded :file  " + key);
        if (ConstantsUtil.checkFileKeyExist(key)) {
            checkDownload(key, baseItem);
            return;
        }
        File file = new File(ConstantsUtil.FILE_PATH_APP_ROOT + key);
        TransferObserver observer = transferUtility.download(ConstantsUtil.AWSBucketName, key, file);
        //observersHaspMap.put(key, observer);
        observer.setTransferListener(new DownloadListener(key, baseItem));
    }

    private class DownloadListener implements TransferListener {

        private String key;
        private BaseItem baseItem;

        public DownloadListener(String key, BaseItem baseItem) {
            this.key = key;
            this.baseItem = baseItem;
        }

        @Override
        public void onError(int id, Exception e) {
            Log.e(LOGTAG, "Error during download key :" + key);
            //doneSignal.countDown();
            //onAssetDownloadListener.onDownloadFailed();
        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

        }

        @Override
        public void onStateChanged(int id, TransferState state) {
            if (state == TransferState.FAILED) {
                Log.e(LOGTAG, " download failed key :" + key);
                //doneSignal.countDown();
                //onAssetDownloadListener.onDownloadFailed();
            }
            if (state == TransferState.COMPLETED) {
                checkDownload(key, baseItem);
            }
        }
    }

    protected void checkDownload(String key, BaseItem baseItem) {
        Log.d(LOGTAG, " downloaded body obj  " + baseItem.getObjAwsKey() + " and texture obj " + baseItem.getTextureAwsKey());
        if (ConstantsUtil.checkFileKeysExist(baseItem.getTextureAwsKey(), baseItem.getObjAwsKey())) {
            clearObserver(baseItem);
            //onAssetDownloadListener.onDownload();
            //downloadedSuccessful = true;
            //doneSignal.countDown();
        }
    }

    private void clearObserver(BaseItem baseItem) {
//        TransferObserver obObj = observersHaspMap.get(baseItem.getObjAwsKey());
//        TransferObserver obTex = observersHaspMap.get(baseItem.getTextureAwsKey());
//        if (obObj != null)
//            obObj.cleanTransferListener();
//        if (obTex != null)
//            obTex.cleanTransferListener();
    }
}
