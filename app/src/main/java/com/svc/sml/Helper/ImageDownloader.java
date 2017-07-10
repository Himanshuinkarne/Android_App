package com.svc.sml.Helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.svc.sml.InkarneAppContext;
import com.svc.sml.Utility.AWSUtil;
import com.svc.sml.Utility.ConstantsUtil;

import java.io.File;

/**
 * Created by himanshu on 5/27/16.
 */
public class ImageDownloader {
    private final static String LOGTAG = ImageDownloader.class.toString();
    private Context context;
    private TransferUtility transferUtility;
    //protected ArrayList<TransferObserver> observers = new ArrayList<TransferObserver>();
    private SparseArray<TransferObserver> observerMap = new SparseArray<>();

    public interface OnImageDownloadListener {
        void onDownload(String id, String key,Bitmap bitmap);
        void onDownloadFailed(String id, String key);
    }

    public ImageDownloader() {
        this.transferUtility = AWSUtil.getTransferUtility(InkarneAppContext.getAppContext());
    }
//    public ImageDownloader(Context context) {
//        this.context = context;
//        this.transferUtility = AWSUtil.getTransferUtility(context);
//    }

    public void beginDownload(String objId,String key,OnImageDownloadListener onImageDownloadListener) {
        if (key == null || key.length() == 0) {
            Log.d(LOGTAG, "key is null or blank");
            return;
        }

        Log.d(LOGTAG, "To be downloaded. file:  " + key);
        if (ConstantsUtil.checkFileKeysExist(key)) {//TODO both file need to be checked
            new BitmapWorkerTask(objId,key, onImageDownloadListener).execute(key);
            return;
        }
        File file = new File(ConstantsUtil.FILE_PATH_APP_ROOT + key);
        TransferObserver observer = transferUtility.download(ConstantsUtil.AWSBucketName, key, file);

        //observers.add(observer);
        observerMap.append(observer.getId(),observer);
        observer.setTransferListener(new imageDownloadListener(objId,key,onImageDownloadListener));
    }
    /*
     * A TransferListener class that can listen to a download task and be
     * notified when the status changes.
     */

//    public void stopDownload() {
//        for (TransferObserver ob : observers) {
//            if (transferUtility != null)
//                transferUtility.cancel(ob.getId());
//            ob.cleanTransferListener();
//        }
//        observers.clear();
//    }

    public void clearAllObserver() {
        for (int i=0; i<observerMap.size(); i++){
            TransferObserver ob = observerMap.valueAt(i);
            if (transferUtility != null)
                transferUtility.cancel(ob.getId());
            ob.cleanTransferListener();
        }
        observerMap.clear();
    }

    public void clearObserver(int observerId){
        TransferObserver ob = observerMap.get(observerId);
        if (transferUtility != null)
            transferUtility.cancel(observerId);
        ob.cleanTransferListener();
        observerMap.delete(observerId);
    }


    private class imageDownloadListener implements TransferListener {

        private String key;
        private OnImageDownloadListener onImageDownloadListener;
        private String objId;
        //public imageDownloadListener(final BaseAccessoryItem item, final HolderItem holderItem, TransferObserver observer) {
        public imageDownloadListener(String id, String key,OnImageDownloadListener onImageDownloadListener) {
            this.key = key;
            this.onImageDownloadListener = onImageDownloadListener;
            this.objId = id;
        }

        @Override
        public void onError(int id, Exception e) {

        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

        }

        @Override
        public void onStateChanged(int id, TransferState state) {
            if (state == TransferState.FAILED) {
                clearObserver(id);
                Log.w(LOGTAG, "Thumbnail download failed :" + key);
                if(onImageDownloadListener != null)
                     onImageDownloadListener.onDownloadFailed(objId,key);
            }

            if (state == TransferState.COMPLETED) {
                clearObserver(id);
                Log.w(LOGTAG, "**** Thumbnail download successful **** "+ key);
                new BitmapWorkerTask(objId,key,onImageDownloadListener).execute(key);
            }
        }
    }


    public class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
        private String key;
        private OnImageDownloadListener onImageDownloadListener;
        private String objId;
        public BitmapWorkerTask(String objId,String key, OnImageDownloadListener onImageDownloadListener) {
            this.key = key;
            this.onImageDownloadListener = onImageDownloadListener;
            this.objId = objId;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String key = params[0];
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            File file = new File(ConstantsUtil.FILE_PATH_APP_ROOT + key);
            Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
//            if (bm != null) {
//                SharedResource.getInstance().addBitmapToMemoryCache(key,bm);
//            }
            return bm;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap == null || bitmap.getHeight() ==0) {
                if(onImageDownloadListener != null)
                    onImageDownloadListener.onDownloadFailed(objId, key);
                File file = new File(ConstantsUtil.FILE_PATH_APP_ROOT + key);
                file.delete();
            }
            else {
                if(onImageDownloadListener != null)
                    onImageDownloadListener.onDownload(objId, key, bitmap);
            }
        }
    }
}
