package com.svc.sml.Activity;

/*
 * Copyright (C) 2011-2012 Wglxy.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.svc.sml.Database.User;
import com.svc.sml.InkarneAppContext;
import com.svc.sml.Model.TransferProgressModel;
import com.svc.sml.R;
import com.svc.sml.Utility.AWSUtil;
import com.svc.sml.Utility.AdjustPicListener;
import com.svc.sml.Utility.Connectivity;
import com.svc.sml.Utility.ConstantsFunctional;
import com.svc.sml.Utility.ConstantsUtil;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

/**
 * This activity displays an image in an image view and then sets up a touch event
 * listener so the image can be panned and zoomed.
 */

public class AdjustPicActivity_old extends BaseActivity {

    private final static String LOGTAG = "AdjustPicActivity";
    public final static String EXTRA_PARAM_PIC_AWS_KEY_PATH = "userPicUploadAwsKeyPath";
    public final static String EXTRA_PARAM_RESIZE_RATIO = "userPicResizedRatio";
    private ContentResolver mContentResolver;

    private final int IMAGE_MAX_SIZE = 1024;
    private final float CANVAS_SIZE = 1f;

    private final Bitmap.CompressFormat mOutputFormat = Bitmap.CompressFormat.JPEG;
    private String picSource;
    private String picPath;
    private Bitmap picBitmap;
    private FrameLayout fmContainer;
    private ImageView ivUserPic;
    private ImageView ivPicTransparentLayer;
    private String filePathAdjustedPic;
    private TransferUtility transferUtility;
    private Button btnNext;
    // private HashMap<String, TransferProgressModel> hashMapUpload = new HashMap<String, TransferProgressModel>();
    private String userPicUploadAwsKeyPath;
    protected ProgressDialog progressDialog;
    private String filePath;
    private ImageButton btnZoom;
    private ImageButton btnRotate;
    private TextView zoomRotateText;
    public Matrix matrix = new Matrix();
    private boolean isAdjusted;
    private int orientation = 0;
    private TransferProgressModel transferModel = null;
    private float ratioImageRisized = 1;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adjustpic);
        this.transferUtility = AWSUtil.getTransferUtility(AdjustPicActivity_old.this);
        isAdjusted = false;
        //this.transferUtility = AWSUtil.getTransferUtility(getApplicationContext());
        picSource = getIntent().getStringExtra(FaceSelectionActivity.EXTRA_PARAM_PIC_SOURCE_TYPE);
        picPath = getIntent().getStringExtra(FaceSelectionActivity.EXTRA_PARAM_PIC_PATH);

        if(picPath == null){
            Log.e(LOGTAG, "picpath null");
            Toast.makeText(getApplicationContext(),"Oops,could not read the file.Please select another pic.",Toast.LENGTH_SHORT).show();
            Intent i1 = new Intent(AdjustPicActivity_old.this, FaceSelectionActivity.class);
            startActivity(i1);
            finish();
            return;

        }else {
            try {
                ExifInterface ei = new ExifInterface(picPath);
                orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

            } catch (IOException e) {
                e.printStackTrace();
            }


            fmContainer = (FrameLayout) findViewById(R.id.fm_adjustpic_iv_container);
            ivUserPic = (ImageView) findViewById(R.id.iv_adjustpic_userpic);
            ivPicTransparentLayer = (ImageView) findViewById(R.id.iv_adjustpic_transparent_layer);
            btnNext = (Button) findViewById(R.id.btn_shared_next);
            btnZoom = (ImageButton) findViewById(R.id.btn_zoom);
            btnZoom.setSelected(true);
            btnRotate = (ImageButton) findViewById(R.id.btn_rotate);
            zoomRotateText = (TextView) findViewById(R.id.zoomRotateText);
            ivUserPic.setScaleType(ImageView.ScaleType.MATRIX);
            //fmContainer.setOnTouchListener(new AdjustPicListener(fmContainer, ivUserPic, AdjustPicListener.Anchor.CENTER, btnZoom, zoomRotateText, matrix));
            fmContainer.setOnTouchListener(new AdjustPicListener(fmContainer, ivUserPic, AdjustPicListener.Anchor.CENTER, btnZoom, btnRotate, zoomRotateText, matrix));
            createPDialog();
            GATrackActivity(LOGTAG);
            Log.e(LOGTAG, "onCreate");
            initView();
        }
    }

    private void createPDialog() {
        ProgressDialog pb = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
        pb.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pb.setCanceledOnTouchOutside(false);
        pb.setCancelable(true);
        progressDialog = pb;
    }

    //modified by ss
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //windowUpdate();
    }

    private void initView(){
        ivUserPic.postDelayed(new Runnable() {
            @Override
            public void run() {
                windowUpdate();
            }
        }, 1);
    }

    private void initView1(){
        ViewTreeObserver viewTreeObserver = ivUserPic.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    ivUserPic.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    windowUpdate();
                }
            });
        }
    }

    private void windowUpdate() {
//        if(new Random().nextInt(5)%2 == 0){
//            Log.w(LOGTAG, " finish");
//            finish();
//        }
        Log.w(LOGTAG, " windowUpdate");
        if (picPath == null) {
            if (InkarneAppContext.adjustPicActivityPicPath != null ) {
                picPath = InkarneAppContext.adjustPicActivityPicPath;
            }
        }
        if (ivUserPic == null || picPath == null) {
            Log.e(LOGTAG,"ivUserPic null");
            Intent i1 = new Intent(AdjustPicActivity_old.this, FaceSelectionActivity.class);
            startActivity(i1);
            finish();
            return;
        }
        InkarneAppContext.adjustPicActivityPicPath = picPath;
        int tempW = ivUserPic.getWidth();
        int tempH = ivUserPic.getHeight();
        if(tempW >1024 || tempH > 1024 ){
            tempH /= 2;
            tempW /=2;
        }

        //Bitmap temp = ConstantsUtil.decodeSampledBitmapFromFile(picPath, (int) (ivUserPic.getWidth()), (int) (ivUserPic.getHeight()));
        Bitmap temp = ConstantsUtil.decodeSampledBitmapFromFile(picPath, tempW, tempH);
        //temp = null;
        if(temp == null || temp.getWidth() == 0){ //TODO
            Log.e(LOGTAG, "decoded file null");
            Toast.makeText(getApplicationContext(),"Oops,could not read the file.Please select your pic again ",Toast.LENGTH_SHORT).show();
            Intent i1 = new Intent(AdjustPicActivity_old.this, FaceSelectionActivity.class);
            startActivity(i1);
            finish();
            return;
        }else {
            picBitmap = Bitmap.createBitmap((int) (temp.getWidth() * CANVAS_SIZE), (int) (temp.getHeight() * CANVAS_SIZE), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(picBitmap);
            //canvas.scale(1.5f,1.5f);
            canvas.drawBitmap(temp, temp.getWidth() * (CANVAS_SIZE - 1) / 2.0f, temp.getHeight() * (CANVAS_SIZE - 1) / 2.0f, null);
            //Log.d("SS","original: "+temp.getWidth()+" X "+temp.getHeight()+"   || after: "+picBitmap.getWidth()+" X "+picBitmap.getHeight());
            ivUserPic.setImageBitmap(picBitmap);
            //Here you can get the size!
            //((ImageView) ivUserPic).setImageMatrix(matrix);
            if (!isAdjusted) {
                fitImageToWindow();
                isAdjusted = true;
            }
        }
    }

    private void resetKey() {
        transferModel = null;
        userPicUploadAwsKeyPath = null;
        filePathAdjustedPic = null;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e(LOGTAG, " new intent");
        resetKey();
    }

    public void onResume() {
        Log.w(LOGTAG, " onResume");
        super.onResume();
        if (((InkarneAppContext) this.getApplication()).wasInBackground) {
            Log.w(LOGTAG, " resume Come from background");
            if (mTracker == null)
                createTracker();
            windowUpdate();
        }
    }

    public void onPause() {
        super.onPause();
        System.gc();
    }

    public void onStop() {
        super.onStop();
        resetKey();
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        //hashMapUpload.clear();
    }

    public void onDestroy() {
        super.onDestroy();
        InkarneAppContext.adjustPicActivityPicPath = null;
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Override
    public void onBackPressed() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
        super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();

        }
        return super.onKeyDown(keyCode, event);
    }

    //modified by ss
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void fitImageToWindow() {
        Drawable d = new BitmapDrawable(getResources(), picBitmap);
        ivUserPic.setImageDrawable(d);
        float winWidth = fmContainer.getWidth();
        float winHeight = fmContainer.getHeight();
        float currentZoom = 1f;
        PointF currentPan = new PointF(0, 0);

        ImageView view = ivUserPic;
        Drawable drawable = view.getDrawable();

        if (drawable != null) {
            Bitmap bm = ((BitmapDrawable) drawable).getBitmap();
            if (bm != null) {
                //Limit Pan

                float bmWidth = bm.getWidth();
                float bmHeight = bm.getHeight();

                float nbmWidth = bmWidth, nbmHeight = bmHeight;
                if (orientation == ExifInterface.ORIENTATION_ROTATE_90 || orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                    nbmHeight = bmWidth;
                    nbmWidth = bmHeight;
                }

                float fitToWindow = Math.min(winWidth / nbmWidth, winHeight / nbmHeight);
                float xOffset = (winWidth - nbmWidth * fitToWindow) * 0.5f;
                float yOffset = (winHeight - nbmHeight * fitToWindow) * 0.5f;
                matrix.reset();
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        matrix.postRotate(90, bmWidth / 2, bmHeight / 2);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        matrix.postRotate(180, bmWidth / 2, bmHeight / 2);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        matrix.postRotate(270, bmWidth / 2, bmHeight / 2);
                        break;
                    // etc.

                }
                matrix.postTranslate((nbmWidth - bmWidth) / 2, (nbmHeight - bmHeight) / 2);
                matrix.postScale(currentZoom * fitToWindow, currentZoom * fitToWindow);
                matrix.postTranslate(xOffset, yOffset);
                ivUserPic.setImageMatrix(matrix);
            }
        }
    }


    public void nextBtnClickHandler(View v) {
        new BitmapWorkerTask(ivUserPic).execute();

    }

    public Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        Log.e(LOGTAG,"w: "+ realImage.getWidth()+"  h:"+realImage.getHeight() );
        float ratio = Math.min(
                maxImageSize / realImage.getWidth(),
                maxImageSize / realImage.getHeight());
        ratioImageRisized = ratio;
        int width = Math.round(ratio * realImage.getWidth());
        int height = Math.round(ratio * realImage.getHeight());
        Log.e(LOGTAG,"new w: "+ width+"  h:"+height+ "  r:"+ratio );
        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }

    public class BitmapWorkerTask extends AsyncTask<Integer, Void, String> {
        private ImageView iv;
        private Bitmap mDrawingBitmap;
        private int mWidth = 1024;

        public BitmapWorkerTask(ImageView imageView) {
            iv = imageView;
        }

        @Override
        protected void onPreExecute() {
            // progressDialog.show(AdjustPicActivity.this, getString(R.string.message_fiducials_upload_pic), getString(R.string.message_wait));
            if (progressDialog == null)
                progressDialog = getProgressDialog();
            progressDialog.setTitle(getString(R.string.message_fiducials_upload_pic));
            progressDialog.setMessage(getString(R.string.message_wait));
            if (!isFinishing()) {
                progressDialog.show();
            }
            iv.setDrawingCacheEnabled(true);
            iv.buildDrawingCache();
            mDrawingBitmap = iv.getDrawingCache();
            ivUserPic.setImageMatrix(matrix);
            /*
            mWidth = iv.getWidth();
            int mHeight = iv.getHeight() * mWidth / iv.getWidth();
            mDrawingBitmap = Bitmap.createScaledBitmap(mDrawingBitmap, mWidth, mHeight, false);
            */
            Log.d("SSSS", "I am on pre execute: " + matrix.toString());
        }

        @Override
        protected String doInBackground(Integer... params) {
            Bitmap bm = scaleDown(mDrawingBitmap, ConstantsFunctional.MAX_DIM_OF_UPLOAD_USER_PIC,true);
            mDrawingBitmap = bm;
            String filePath = getSaveImagePath(mDrawingBitmap);
            return filePath;
        }

        private void saveImage(Bitmap bitmap, String filePath) {

            File file = new File(filePath);
            Log.d("SSSS", filePath);
            if (file.exists()) file.delete();
            try {
                FileOutputStream out = new FileOutputStream(file);
                if (filePath.substring(filePath.length() - 3).equalsIgnoreCase("png")) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                } else {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                }
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        protected void onPostExecute(String filePath) {
            iv.setDrawingCacheEnabled(false);
            ivUserPic.setImageMatrix(matrix);
            filePathAdjustedPic = filePath;
            beginUpload(filePathAdjustedPic);
            Log.d("SSSS", "I am on post execute");

        }
    }

    public Bitmap getVisibleRectangleBitmap() {
        ImageView imageView = ivUserPic;
        if (imageView == null) {
            return null;
        }
        Bitmap visibleBitmap = imageView.getDrawingCache();

        if (visibleBitmap == null) {
            visibleBitmap = Bitmap.createBitmap(imageView.getWidth(), imageView.getHeight(), Bitmap.Config.ARGB_8888);
            Log.d("Test", "getVisibleRectangleBitmap Width: " + imageView.getWidth() + " height :" + imageView.getHeight());
            Canvas c = new Canvas(visibleBitmap);
            imageView.draw(c);
        }
        return visibleBitmap;
    }

    private String getSaveImagePath(Bitmap finalBitmap) {
        String savedImagePath = null;
        String root = ConstantsUtil.FILE_PATH_APP_ROOT;
        File myDir = new File(root + ConstantsUtil.FILE_PATH_VISAGE_GALLERY_IMAGE);
        myDir.mkdirs();
        Random generator = new Random();
        int n = 8;
        n = generator.nextInt(n);
        String fName = "Image-" + n + ".png";
        File file = new File(myDir, fName);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            savedImagePath = file.getAbsolutePath();
            //filePathAdjustedPic = file.getAbsolutePath();
            Log.e(LOGTAG, " filePathAdjustedPic :" + savedImagePath);
            out.flush();
            out.close();
            //return savedImagePath;
            //beginUpload(filePathAdjustedPic);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return savedImagePath;
    }


    private void beginUpload(String filePath) {
        Log.e(LOGTAG, "userPicUploadAwsKeyPath 0  " + userPicUploadAwsKeyPath);
        if (!Connectivity.isConnected(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), getString(R.string.message_network_failure), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Connectivity.isConnectedWifi(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), getString(R.string.message_network_no_wifi), Toast.LENGTH_SHORT).show();

        }
        if (filePath == null || filePath.length() == 0) {
            Log.e(LOGTAG, "filepath null or blank ");
            return;
        }

        if (transferModel != null && transferModel.transferState == TransferState.COMPLETED && transferModel.awsUploadKeyPath != null) {
            Log.d(LOGTAG, "already uploaded filepath " + filePath);
            userPicUploadAwsKeyPath = transferModel.awsUploadKeyPath;
            checkUpload();
            return;
        }
        transferModel = null;

        File file = new File(filePath);
        String awsKey = ConstantsUtil.FILE_PATH_AWS_KEY_ROOT + User.getInstance().getmUserId() + "/faces/" + ConstantsUtil.getFileNameForS3Upload(filePath);
        Log.d(LOGTAG, "upload key " + awsKey);
        if (transferUtility == null) {
            Log.e(LOGTAG, "transferUtility null ");
            transferUtility = AWSUtil.getTransferUtility(InkarneAppContext.getAppContext());
        }
        TransferObserver observer = transferUtility.upload(ConstantsUtil.AWSBucketName, awsKey, file);
        TransferProgressModel tPModel = new TransferProgressModel(filePath, 0, TransferState.WAITING);
        tPModel.awsUploadKeyPath = awsKey;
        observer.setTransferListener(new UploadListener(tPModel));
        //observersUpload.add(observer);
        Log.e(LOGTAG, "upload start");
    }

    private class UploadListener implements TransferListener {
        public TransferProgressModel tModel;

        public UploadListener(TransferProgressModel tModel) {
            this.tModel = tModel;
        }

        @Override
        public void onError(int id, Exception e) {
            Log.e(LOGTAG, "Error during upload: " + tModel.filename + "  e" + e.toString());
            Toast.makeText(AdjustPicActivity_old.this, ConstantsUtil.MESSAGE_TOAST_NETWORK_RESPONSE_FAILED, Toast.LENGTH_SHORT).show();
            if (progressDialog != null)
                progressDialog.dismiss();
            btnNext.setText("Retry");
        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

        }

        @Override
        public void onStateChanged(int id, TransferState newState) {
            if (newState == TransferState.FAILED) {
                Log.e(LOGTAG, "Uploaded failed key " + tModel.filename);
                tModel.transferState = newState;
                Toast.makeText(AdjustPicActivity_old.this, ConstantsUtil.MESSAGE_TOAST_NETWORK_RESPONSE_FAILED, Toast.LENGTH_SHORT).show();
                btnNext.setText("Retry");
                if (progressDialog != null)
                    progressDialog.dismiss();
            } else if (newState == TransferState.COMPLETED) {
                Log.d(LOGTAG, "Uploaded successfully key " + tModel.filename);
                tModel.transferState = newState;
                userPicUploadAwsKeyPath = tModel.awsUploadKeyPath;
                transferModel = tModel;
                checkUpload();
            }
        }
    }

    private void checkUpload() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        launchFiducial();
    }




    private synchronized void launchFiducial() {
        Log.e(LOGTAG, "userPicUploadAwsKeyPath1  " + userPicUploadAwsKeyPath);
        if (userPicUploadAwsKeyPath != null) {
            Log.e(LOGTAG, "userPicUploadAwsKeyPath2  " + userPicUploadAwsKeyPath);
            // Intent intent = new Intent(this, FiducialActivity.class);
            Intent intent = new Intent(this, PreFeducialActivity.class);
            intent.putExtra(FaceSelectionActivity.EXTRA_PARAM_PIC_PATH, filePathAdjustedPic);
            intent.putExtra(EXTRA_PARAM_PIC_AWS_KEY_PATH, userPicUploadAwsKeyPath);
            intent.putExtra(EXTRA_PARAM_RESIZE_RATIO, ratioImageRisized);
            intent.putExtra(FaceSelectionActivity.EXTRA_PARAM_PIC_SOURCE_TYPE, picSource);
            startActivity(intent);
        }
    }

    /* unused*/
/*
    @Override
    public Bitmap getCroppedImage() {
        Bitmap visibleBitmap = getVisibleRectangleBitmap();
        Rect displayedImageRect = ImageViewUtil.getBitmapRectCenterInside(visibleBitmap, getImageView());

        Log.d("Test", "getCroppedImage visibleBitmapW: " + visibleBitmap.getWidth() + " visibleBitmapH :" + visibleBitmap.getHeight());
        Log.d("Test", "getCroppedImage displayedImageRectW: " + displayedImageRect.width() + " displayedImageRectH :" + displayedImageRect.height());
        // Get the scale factor between the actual Bitmap dimensions and the
        // displayed dimensions for width.
        float actualImageWidth = visibleBitmap.getWidth();
        float displayedImageWidth = displayedImageRect.width();
        float scaleFactorWidth = actualImageWidth / displayedImageWidth;

        // Get the scale factor between the actual Bitmap dimensions and the
        // displayed dimensions for height.
        float actualImageHeight = visibleBitmap.getHeight();
        float displayedImageHeight = displayedImageRect.height();
        float scaleFactorHeight = actualImageHeight / displayedImageHeight;

        // Get crop window position relative to the displayed image.
        float cropWindowX = Edge.LEFT.getCoordinate() - displayedImageRect.left;
        float cropWindowY = Edge.TOP.getCoordinate() - displayedImageRect.top;
        float cropWindowWidth = Edge.getWidth();
        float cropWindowHeight = Edge.getHeight();

        // Scale the crop window position to the actual size of the Bitmap.
        float actualCropX = cropWindowX * scaleFactorWidth;
        float actualCropY = cropWindowY * scaleFactorHeight;
        float actualCropWidth = cropWindowWidth * scaleFactorWidth;
        float actualCropHeight = cropWindowHeight * scaleFactorHeight;
        Log.d("Test", "getCroppedImage actualCropX: " + actualCropX + " actualCropY :" + actualCropY);
        Log.d("Test", "getCroppedImage actualCropWidth: " + actualCropWidth + " actualCropHeight :" + actualCropHeight);
        // Crop the subset from the original Bitmap.
        return Bitmap.createBitmap(visibleBitmap,
                (int) actualCropX, (int) actualCropY, (int) actualCropWidth, (int) actualCropHeight);
    }
    */

    private boolean saveOutput() {
        Bitmap croppedImage = getVisibleRectangleBitmap();
        Uri mSaveUri = Uri.fromFile(new File("path"));
        if (mSaveUri != null) {
            OutputStream outputStream = null;
            try {
                outputStream = getContentResolver().openOutputStream(mSaveUri);
                if (outputStream != null) {
                    croppedImage.compress(mOutputFormat, 100, outputStream);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                return false;
            } finally {
                closeSilently(outputStream);
            }
        } else {
            Log.e(LOGTAG, "not defined image url");
            return false;
        }
        croppedImage.recycle();
        return true;
    }

    public void closeSilently(Closeable c) {
        if (c == null) return;
        try {
            c.close();
        } catch (Throwable t) {
            // do nothing
        }
    }

    public class RenderBitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
        private ImageView iv;
        private Bitmap mOfferText;
        private final int mWidth = 512;
        private int mHeight = 720;

        public RenderBitmapWorkerTask(ImageView imageView) {
            iv = imageView;
        }

        // Decode image in background.
        @Override
        protected void onPreExecute() {
            mOfferText = iv.getDrawingCache();
            mHeight = iv.getHeight() * mWidth / iv.getWidth();
        }

        @Override
        protected Bitmap doInBackground(Integer... params) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    mOfferText = iv.getDrawingCache();
//                    mHeight = iv.getHeight()*mWidth/ iv.getWidth();
//                }
//            });
            getSaveImagePath(mOfferText);
            mOfferText = Bitmap.createScaledBitmap(mOfferText, mWidth, mHeight, false);
            //getSaveImagePath(mOfferText);
            /*
            Matrix mMatrix2 = new Matrix();
            float[] mStartSrc = new float[] { 0, 0, mTextViewWidth, 0, mTextViewWidth, mTextViewHeight, 0, mTextViewHeight };
            float[] mDestSrc = new float[] { 0, 4f, 520f, 0f, 552f, 704f, 22f, 720f };
            mMatrix2.setPolyToPoly(mStartSrc, 0, mDestSrc, 0, 4);
            mOfferText = Bitmap.createBitmap(mOfferText, 0, 0, mTextViewWidth, mTextViewHeight, mMatrix2, false);
             */
            return mOfferText;
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            bitmap.recycle();
            //iv.setImageBitmap(bitmap);
            //bitmap.recycle();
            //Canvas c = new Canvas(bitmap);
            //iv.draw(c);
            //beginUpload(filePathAdjustedPic);
        }
    }


}
