package com.svc.sml.Helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.svc.sml.SharedResource;
import com.svc.sml.Utility.ConstantsUtil;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Created by himanshu on 5/27/16.
 */
public class ImageFetcher {
    private final static String LOGTAG = ImageFetcher.class.toString();
    private final static float ALPHA_IMAGE_DOWNLOADED = 1.0f;
    private Context context;
    private ImageDownloader imageDownloader;

    public ImageFetcher() {
    }
    public ImageFetcher(Context context) {
        this.context = context;
        imageDownloader = new ImageDownloader();
    }

    private void hideProgressBar(WeakReference<ProgressBar> pbWeakRef) {
        if (pbWeakRef != null) {
            ProgressBar pb = pbWeakRef.get();
            if (pb != null) {
                Log.d(LOGTAG, "Tag complete " + pb.getTag());
                pb.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void manageSetImage(final String objId,String key,final ImageView iv ,final ProgressBar pb) {
        iv.setImageBitmap(null);
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        Bitmap bitmap = SharedResource.getInstance().getBitmapFromMemCache(key);
        if(bitmap != null){
            pb.setVisibility(View.INVISIBLE);
            iv.setImageBitmap(bitmap);
            return;
        }
        final WeakReference<ImageView> ivWeakThumbnail = new WeakReference<ImageView>(iv);
        final WeakReference<ProgressBar> pbWeakThumbnail = new WeakReference<ProgressBar>(pb);
        imageDownloader.beginDownload(objId, key, new ImageDownloader.OnImageDownloadListener() {
            @Override
            public void onDownload(String id, String key, Bitmap bitmap) {
                Log.w(LOGTAG," beginDownload key "+key);
                SharedResource.getInstance().addBitmapToMemoryCache(key,bitmap);
                if(pbWeakThumbnail != null){
                    ImageView imageView = ivWeakThumbnail.get();
                    if(imageView != null)
                        imageView.setImageBitmap(bitmap);
                }
                hideProgressBar(pbWeakThumbnail);
            }

            @Override
            public void onDownloadFailed(String id, String key) {
                hideProgressBar(pbWeakThumbnail);
            }
        });
    }


    public void manageSetImage(final String objId,String key,final ImageView iv ,final ProgressBar pb,final int countTrial) {
        iv.setImageBitmap(null);
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        Bitmap bitmap = SharedResource.getInstance().getBitmapFromMemCache(key);
        if(bitmap != null){
            pb.setVisibility(View.INVISIBLE);
            iv.setImageBitmap(bitmap);
            return;
        }
        final WeakReference<ImageView> ivWeakThumbnail = new WeakReference<ImageView>(iv);
        final WeakReference<ProgressBar> pbWeakThumbnail = new WeakReference<ProgressBar>(pb);
        imageDownloader.beginDownload(objId, key, new ImageDownloader.OnImageDownloadListener() {
            @Override
            public void onDownload(String id, String key, Bitmap bitmap) {
                Log.w(LOGTAG," beginDownload key "+key);
                SharedResource.getInstance().addBitmapToMemoryCache(key,bitmap);
                if(pbWeakThumbnail != null){
                    ImageView imageView = ivWeakThumbnail.get();
                    if(imageView != null)
                        imageView.setImageBitmap(bitmap);
                }
                hideProgressBar(pbWeakThumbnail);
            }

            @Override
            public void onDownloadFailed(final String id, final String key) {
                if(countTrial <4){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            int c = countTrial+1;
                            manageSetImage(objId,key,iv,pb,c);
                        }
                    },countTrial*100);

                }else {
                    hideProgressBar(pbWeakThumbnail);
                }

            }
        });
    }

//    public void downloadImage(final String objId,String key,final ImageView iv ,final ProgressBar pb){
//        imageDownloader.beginDownload(objId, key, new ImageDownloader.OnImageDownloadListener() {
//            @Override
//            public void onDownload(String id, String key, Bitmap bitmap) {
//                Log.w(LOGTAG," beginDownload key "+key);
//                SharedResource.getInstance().addBitmapToMemoryCache(key,bitmap);
//                if(pbWeakThumbnail != null){
//                    ImageView imageView = ivWeakThumbnail.get();
//                    if(imageView != null)
//                        imageView.setImageBitmap(bitmap);
//                }
//                hideProgressBar(pbWeakThumbnail);
//            }
//
//            @Override
//            public void onDownloadFailed(String id, String key) {
//                hideProgressBar(pbWeakThumbnail);
//            }
//        });
//    }

    public void stopDownload(){
        imageDownloader.clearAllObserver();
    }

    public static class BitmapAsyncTask extends AsyncTask<Void, Void, Bitmap> {
        private ImageView iv;
        private String key;

        public BitmapAsyncTask(ImageView imageView, String key) {
            this.iv = imageView;
            this.key = key;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            File file = new File(ConstantsUtil.FILE_PATH_APP_ROOT + key);
            Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            return bm;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            iv.setVisibility(View.VISIBLE);
            iv.setAlpha(1.0f);
            if (bitmap != null) {
                iv.setImageBitmap(bitmap);
                Log.e("Bitmap", "onPostExecute : " + key);
            }
        }
    }
}
