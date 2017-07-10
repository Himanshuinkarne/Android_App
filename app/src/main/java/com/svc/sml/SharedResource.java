package com.svc.sml;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;

/**
 * Created by himanshu on 5/27/16.
 */
public class SharedResource {
    private final static String LOGTAG = SharedResource.class.toString();
    private static SharedResource ourInstance ;
    private Context context;
    private LruCache<String, Bitmap> mMemoryCache;


    public static SharedResource getInstance() {
        if(ourInstance == null)
            ourInstance = new SharedResource();
        return ourInstance;
    }

    private SharedResource() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 16;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }


    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        Log.d(LOGTAG," addBitmapToMemoryCache key "+key);
        if(key == null || bitmap == null){
            Log.d(LOGTAG," addBitmapToMemoryCache key or bitmap null ");
            return;
        }
        if (getBitmapFromMemCache(key) == null) {
            Log.d(LOGTAG,"  Put addBitmapToMemoryCache key:  "+ key );
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        if(key == null){
            return  null;
        }
        Bitmap bitmap = mMemoryCache.get(key);
        return bitmap;
    }

//    public void loadBitmap(int resId, ImageView imageView) {
//        final String imageKey = String.valueOf(resId);
//
//        final Bitmap bitmap = getBitmapFromMemCache(imageKey);
//        if (bitmap != null) {
//            imageView.setImageBitmap(bitmap);
//        } else {
//            imageView.setImageResource(R.drawable.liked);
//            BitmapWorkerTask task = new BitmapWorkerTask(imageView);
//            task.execute(resId);
//        }
//    }
//
//    class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
//
//        public BitmapWorkerTask() {
//        }
//        public BitmapWorkerTask(ImageView iv) {
//
//        }
//
//
//        @Override
//        protected Bitmap doInBackground(Integer... params) {
//            final Bitmap bitmap = decodeSampledBitmapFromResource(context.getResources(), params[0], 100, 100);
//            addBitmapToMemoryCache(String.valueOf(params[0]), bitmap);
//            return bitmap;
//        }
//
//    }
//
//
//    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
//                                                         int reqWidth, int reqHeight) {
//
//        // First decode with inJustDecodeBounds=true to check dimensions
//        final BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeResource(res, resId, options);
//
//        // Calculate inSampleSize
//        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
//
//        // Decode bitmap with inSampleSize set
//        options.inJustDecodeBounds = false;
//        return BitmapFactory.decodeResource(res, resId, options);
//    }
//
//
//    public static int calculateInSampleSize(
//            BitmapFactory.Options options, int reqWidth, int reqHeight) {
//        // Raw height and width of image
//        final int height = options.outHeight;
//        final int width = options.outWidth;
//        int inSampleSize = 1;
//
//        if (height > reqHeight || width > reqWidth) {
//
//            final int halfHeight = height / 2;
//            final int halfWidth = width / 2;
//
//            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
//            // height and width larger than the requested height and width.
//            while ((halfHeight / inSampleSize) > reqHeight
//                    && (halfWidth / inSampleSize) > reqWidth) {
//                inSampleSize *= 2;
//            }
//        }
//
//        return inSampleSize;
//    }

}
