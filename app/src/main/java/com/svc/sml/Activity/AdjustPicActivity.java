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
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
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
import com.svc.sml.Helper.AssetDownloader;
import com.svc.sml.Helper.DataManager;
import com.svc.sml.InkarneAppContext;
import com.svc.sml.Model.BaseAccessoryItem;
import com.svc.sml.Model.FaceItem;
import com.svc.sml.Model.TransferProgressModel;
import com.svc.sml.R;
import com.svc.sml.Utility.AWSUtil;
import com.svc.sml.Utility.AdjustPicListener;
import com.svc.sml.Utility.Connectivity;
import com.svc.sml.Utility.ConstantsFunctional;
import com.svc.sml.Utility.ConstantsUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * This activity displays an image in an image view and then sets up a touch event
 * listener so the image can be panned and zoomed.
 */

public class AdjustPicActivity extends BaseActivity {

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
    private ImageButton btnZoom;
    private ImageButton btnRotate;
    private TextView zoomRotateText;
    public Matrix matrix = new Matrix();
    private boolean isAdjusted;
    private int orientation = 0;
    private TransferProgressModel transferModel = null;
    private float ratioImageRisized = 1;


    private boolean isProgressOn = false;
    private int retryCountFed = 0;
    private int retryCountFace = 0;
    private String feducialPath = "";
    private String[] ucolumns = new String[11];
    private String[] urows = new String[11];
    private FaceItem faceItem;
    private boolean serverCallInProgress = false;
    private boolean isNextActivityLaunched = false;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adjustpic);
        this.transferUtility = AWSUtil.getTransferUtility(AdjustPicActivity.this);
        isAdjusted = false;
        picSource = getIntent().getStringExtra(FaceSelectionActivity.EXTRA_PARAM_PIC_SOURCE_TYPE);
        picPath = getIntent().getStringExtra(FaceSelectionActivity.EXTRA_PARAM_PIC_PATH);

        if (picPath == null) {
            Log.e(LOGTAG, "picPath null");
            Toast.makeText(getApplicationContext(), "Oops,could not read the file.Please select another pic.", Toast.LENGTH_SHORT).show();
            Intent i1 = new Intent(AdjustPicActivity.this, FaceSelectionActivity.class);
            startActivity(i1);
            finish();
            return;

        } else {
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

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    private void initView() {
        ivUserPic.postDelayed(new Runnable() {
            @Override
            public void run() {
                windowUpdate();
            }
        }, 1);
    }

    private void windowUpdate() {
        Log.w(LOGTAG, " windowUpdate");
        if (picPath == null) {
            if (InkarneAppContext.adjustPicActivityPicPath != null) {
                picPath = InkarneAppContext.adjustPicActivityPicPath;
            }
        }
        if (ivUserPic == null || picPath == null) {
            Log.e(LOGTAG, "ivUserPic null");
            Intent i1 = new Intent(AdjustPicActivity.this, FaceSelectionActivity.class);
            startActivity(i1);
            finish();
            return;
        }
        InkarneAppContext.adjustPicActivityPicPath = picPath;
        int tempW = ivUserPic.getWidth();
        int tempH = ivUserPic.getHeight();
        if (tempW > 1024 || tempH > 1024) {
            tempH /= 2;
            tempW /= 2;
        }

        //Bitmap temp = ConstantsUtil.decodeSampledBitmapFromFile(picPath, (int) (ivUserPic.getWidth()), (int) (ivUserPic.getHeight()));
        Bitmap temp = ConstantsUtil.decodeSampledBitmapFromFile(picPath, tempW, tempH);
        //temp = null;
        if (temp == null || temp.getWidth() == 0) { //TODO
            Log.e(LOGTAG, "decoded file null");
            Toast.makeText(getApplicationContext(), "Oops,could not read the file.Please select your pic again ", Toast.LENGTH_SHORT).show();
            Intent i1 = new Intent(AdjustPicActivity.this, FaceSelectionActivity.class);
            startActivity(i1);
            finish();
            return;
        } else {
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

    private void reset() {
        isProgressOn = false;
        retryCountFed = 0;
        retryCountFace = 0;
        feducialPath = "";
        FaceItem faceItem = null;
        serverCallInProgress = false;
        isNextActivityLaunched = false;
    }

    public void onStart() {
        super.onStart();
        reset();
    }

    public void onResume() {
        Log.w(LOGTAG, " onResume");
        super.onResume();
        if (((InkarneAppContext) this.getApplication()).wasInBackground) {
            Log.w(LOGTAG, " resume come from background");
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

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void fitImageToWindow() {
        Drawable d = new BitmapDrawable(getResources(), picBitmap);
        ivUserPic.setImageDrawable(d);
        float winWidth = fmContainer.getWidth();
        float winHeight = fmContainer.getHeight();
        float currentZoom = 1f;
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
                    default:
                        break;
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
        Log.e(LOGTAG, "w: " + realImage.getWidth() + "  h:" + realImage.getHeight());
        float ratio = Math.min(
                maxImageSize / realImage.getWidth(),
                maxImageSize / realImage.getHeight());
        ratioImageRisized = ratio;
        int width = Math.round(ratio * realImage.getWidth());
        int height = Math.round(ratio * realImage.getHeight());
        Log.e(LOGTAG, "new w: " + width + "  h:" + height + "  r:" + ratio);
        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }

    public class BitmapWorkerTask extends AsyncTask<Integer, Void, String> {
        private ImageView iv;
        private Bitmap mDrawingBitmap;

        public BitmapWorkerTask(ImageView imageView) {
            iv = imageView;
        }

        @Override
        protected void onPreExecute() {
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
            Log.d(LOGTAG, "I am on pre execute: " + matrix.toString());
        }

        @Override
        protected String doInBackground(Integer... params) {
            Bitmap bm = scaleDown(mDrawingBitmap, ConstantsFunctional.MAX_DIM_OF_UPLOAD_USER_PIC, true);
            mDrawingBitmap = bm;
            String filePath = getSaveImagePath(mDrawingBitmap);
            return filePath;
        }

        @Override
        protected void onPostExecute(String filePath) {
            iv.setDrawingCacheEnabled(false);
            ivUserPic.setImageMatrix(matrix);
            filePathAdjustedPic = filePath;
            beginUpload(filePathAdjustedPic);
            Log.e(LOGTAG,"onPostExecute "+ filePathAdjustedPic);
        }
    }

    private String getSaveImagePath(Bitmap finalBitmap) {
        String savedImagePath = null;
        String root = ConstantsUtil.FILE_PATH_APP_ROOT;
        File myDir = new File(root + ConstantsUtil.FILE_PATH_VISAGE_GALLERY_IMAGE);
        myDir.mkdirs();
        //Random generator = new Random();
        //int n = 20;
        //n = generator.nextInt(n);
        //String fName = "Image-" + n + ".png";
        String uuidFileName = UUID.randomUUID().toString()+".png";
        File file = new File(myDir, uuidFileName);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            savedImagePath = file.getAbsolutePath();
            Log.e(LOGTAG, " filePathAdjustedPic :" + savedImagePath);
            out.flush();
            out.close();
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
        String filname = file.getName();
        Log.e(LOGTAG,"beginUpload :"+ filname);
        //String awsKey = ConstantsUtil.FILE_PATH_AWS_KEY_ROOT + User.getInstance().getmUserId() + "/faces/" + ConstantsUtil.getFileNameForS3Upload(filePath);
        String awsKey = ConstantsUtil.FILE_PATH_AWS_KEY_ROOT + User.getInstance().getmUserId() + "/faces/" + filname;
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
            Toast.makeText(AdjustPicActivity.this, ConstantsUtil.MESSAGE_TOAST_NETWORK_RESPONSE_FAILED, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(AdjustPicActivity.this, ConstantsUtil.MESSAGE_TOAST_NETWORK_RESPONSE_FAILED, Toast.LENGTH_SHORT).show();
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

    /* changes  */
    private void checkUpload() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        retryCountFed = 0;
        getUserFedPoints();
    }

    protected void getUserFedPoints() {
        ucolumns = new String[11];
        urows = new String[11];
        if (progressDialog == null)
            createPDialog();
        progressDialog.setTitle(getString(R.string.message_fiducials_get_points_2));
        progressDialog.setMessage(getString(R.string.message_wait));
        if (!isFinishing()) {
            progressDialog.show();
        }
        isProgressOn = true;
        String url = ConstantsUtil.URL_BASEPATH_feducials + ConstantsUtil.URL_METHOD_USER_FEDUCIAL_POINTS + User.getInstance().getmUserId() + "/?Pic_Path=" + userPicUploadAwsKeyPath;
        DataManager.getInstance().requestUserFedPoints(url, new DataManager.OnResponseHandlerInterface() {
            @Override
            public void onResponse(Object obj) {
//                if (progressDialog != null && progressDialog.isShowing())
//                    progressDialog.dismiss();
                isProgressOn = false;
                JSONException error = null;
                JSONObject jsonObj = (JSONObject) obj;

                try {
                    int error_code = (int) jsonObj.get("Error_Code");
                    if (error_code != 0) {
                        String message = "";
                        String title = "";
                        boolean shouldProceed = true;
                        boolean shouldShow2ndInstruction = false;
                        switch (error_code) {
                            case 10: {
                                title = "Remove sunglasses/spectacles";
                                message ="Keep the camera at eye level and take frontal portrait shot.\nLight should be evenly distributed on the face.";
                                shouldProceed = false;
                            }
                            break;
                            case 20: {
                                title = "Mouth should be closed, avoid pouting.";
                                message = "IKeep the camera at eye level and take frontal portrait shot.\nLight should be evenly distributed on the face.";
                                shouldProceed = false;
                            }
                            break;
                            case 30: {
                                title = "Pull your hair back into a bun.";
                                message = "Keep the camera at eye level and take frontal portrait shot.\nLight should be evenly distributed on the face.";
                                shouldProceed = false;
                            }
                            break;
                            case 40: {
                                title = "Remove sunglasses/spectacles and keep eyes opened.";
                                message = "Keep the camera at eye level and take frontal portrait shot.\nLight should be evenly distributed on the face.";
                                shouldProceed = false;
                            }
                            break;
                            case 50: {
                                title = "Keep the camera at eye level and take frontal portrait shot.";
                                message = "Look straight and avoid opening mouth, pouting.\nLight should be evenly distributed on the face.";
                                shouldProceed = false;
                            }
                            break;
                            case 60: {
                                title = "Remove sunglasses/spectacles.";
                                message = "Keep the camera at eye level and take frontal portrait shot.\nLight should be evenly distributed on the face.";

                                shouldProceed = false;
                                shouldShow2ndInstruction = true;
                            }
                            break;
                            case 70: {
                                message = "There should be only one person in the frame.";
                                message = "Keep the camera at eye level and take frontal portrait shot.\nLight should be evenly distributed on the face.";
                                shouldProceed = false;
                            }
                            break;
                            case 300: {
                                title = "Keep the camera at eye level and take frontal portrait shot.";
                                message = "Look straight and avoid opening mouth, pouting.\nLight should be evenly distributed on the face.";
                                shouldProceed = false;
                                shouldProceed = false;
                                shouldShow2ndInstruction = true;
                            }
                            break;
                            default: {
                                title = "Keep the camera at eye level and take frontal portrait shot.";
                                message = "Look straight and avoid opening mouth, pouting.\nLight should be evenly distributed on the face.";
                                shouldProceed = false;
                            }
                            break;
                        }

                        Log.e(LOGTAG, "code :" + error_code + "  message : " + message);

                        if (!shouldProceed) {
                            showAlertError(title, message, shouldShow2ndInstruction);
                            return;
                        } else if (message.length() != 0) {
                            showAlertWarning(title, message);
                        }
                    }

                    JSONArray fDetails = jsonObj.getJSONArray("FedInfo");
                    for (int i = 0; i < fDetails.length(); i++) {
                        JSONObject obj2 = null;
                        try {
                            obj2 = fDetails.getJSONObject(i);
                            String c = obj2.getString("column");
                            //float c1 = Integer.parseInt(c) /ratioImageRisized;
                            float c1 = Integer.parseInt(c);
                            ucolumns[i] = String.valueOf(c1);

                            String r = obj2.getString("row");
                            //float r1 = Integer.parseInt(r) /ratioImageRisized;
                            float r1 = Integer.parseInt(r);
                            urows[i] = String.valueOf(r1);
                            Log.d(LOGTAG, "getUserFePoints - index : " + i + "  row: " + urows[i] + " col: " + ucolumns[i]);
                        } catch (JSONException e) {
                            error = e;
                            e.printStackTrace();
                        }
                    }
                    if (error == null) {
                        getFaceItem();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseError(String errorMessage, int errorCode) {
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();

                retryCountFed++;
                if (errorCode == DataManager.CODE_DATA_MANAGER_NETWORK_ERROR) {
                    if (retryCountFed == 1) {
                        Toast.makeText(getApplicationContext(), ConstantsUtil.MESSAGE_TOAST_NETWORK_RESPONSE_FAILED, Toast.LENGTH_SHORT).show();
                    }
                }
                if (retryCountFed < ConstantsUtil.COUNT_RETRY_SERVICE_CRITICAL) {
                    getUserFedPoints();
                } else {
                    isProgressOn = false;
                    finish();//TODO
                }
            }
        });
    }


    protected void showAlertError(String title, String msg, final boolean shouldShow2ndInstruction) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(msg)
                .setCancelable(false)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Intent intent = new Intent(AdjustPicActivity.this, FaceSelectionActivity.class);
                        intent.putExtra(FaceSelectionActivity.EXTRA_PARAM_SHOULD_SHOW_2nd_VIDEO, true);
                        startActivity(intent);
                        finish();
                    }
                }).create();

        if (!isFinishing()) {
            builder.show();
        }
    }

    protected void showAlertWarning(String title, String msg) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(msg)
                .setCancelable(false)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create();
        if (!isFinishing()) {
            builder.show();
        }
    }

    public void getFaceItem() {
        if (faceItem == null) {
            feducialPath = "";
            for (int i = 0; i < 11; i++) {
                feducialPath += "/" + (int)Float.parseFloat(ucolumns[i]) + "/";
                feducialPath += (int)Float.parseFloat(urows[i]);
            }
            requestFaceObj(0);
        } else {
            startFaceItemDownload(0);
        }
    }

    public void requestFaceObj(final int retryCount) {
        if (progressDialog == null)
            progressDialog = getProgressDialogTranslucent();
        if (retryCountFace == 0) {
            progressDialog.setTitle(getString(R.string.message_loading_face_response));
        } else {
            progressDialog.setTitle("Retrying ...");
        }
        progressDialog.setMessage(getString(R.string.message_wait));
        if (!isFinishing())
            progressDialog.show();

        if (serverCallInProgress) {
            return;
        }
        serverCallInProgress = true;
        String defaultHairstyle = "";
        String urlPath = null;
        if (User.getInstance().getDefaultFaceItem() != null && User.getInstance().getDefaultFaceItem().getHairstyleId() != null && !User.getInstance().getDefaultFaceItem().getHairstyleId().isEmpty()) {
            defaultHairstyle = User.getInstance().getDefaultFaceItem().getHairstyleId();
        } else {
            if (User.getInstance().getmGender().equals("m")) {
                defaultHairstyle = ConstantsFunctional.HAIRSTYLE_DEFAULT_MALE;
            } else {
                defaultHairstyle = ConstantsFunctional.HAIRSTYLE_DEFAULT_FEMALE;
            }
        }
        urlPath = ConstantsUtil.URL_BASEPATH_CREATE_V2 + ConstantsUtil.URL_METHOD_CREATEFACE_AND_HAIR + User.getInstance().getmUserId()
                + "/" + User.getInstance().getmGender()
                + "/" + picSource
                + "/" + defaultHairstyle
                + feducialPath
                + "/?Pic_Path=" + userPicUploadAwsKeyPath;

        DataManager.getInstance().requestFaceObj(urlPath, new DataManager.OnResponseHandlerInterface() {
            @Override
            public void onResponse(Object obj) {
                faceItem = (FaceItem) obj;
                serverCallInProgress = false;
                if (faceItem == null || faceItem.getFaceObjkey() == null || faceItem.getFaceObjkey().equals("null")) {
                    faceItem = null;
//                    if (progressDialog != null)
//                        progressDialog.dismiss();
                    showAlertError("Error", "Please select another pic and retry.",false);
                } else {
                    faceItem.setFeducialPoints(feducialPath);
                    if(filePathAdjustedPic != null && !filePathAdjustedPic.isEmpty())
                    faceItem.setImageSavedFilePath(filePathAdjustedPic);
                    faceItem.setImageResizeRatio(String.valueOf(ratioImageRisized));
                    dataSource.create(faceItem);
                    startFaceItemDownload(0);
                }
            }

            @Override
            public void onResponseError(String errorMessage, int errorCode) {
                serverCallInProgress = false;
                if (progressDialog != null)
                    progressDialog.dismiss();
                btnNext.setText("Retry");
                int count = retryCount + 1;
                if (errorCode == DataManager.CODE_DATA_MANAGER_NETWORK_ERROR) {
                    if (retryCountFace == 1 || count == ConstantsUtil.COUNT_RETRY_SERVICE_CRITICAL)
                        Toast.makeText(getApplicationContext(), ConstantsUtil.MESSAGE_TOAST_NETWORK_RESPONSE_FAILED, Toast.LENGTH_SHORT).show();
                }else {

                }
            }
        });
    }

    private void startFaceItemDownload(final int count) {
        if (progressDialog == null)
            progressDialog = getProgressDialogTranslucent();
        progressDialog.setTitle(getString(R.string.message_loading_face_response));
        progressDialog.setMessage(getString(R.string.message_wait));
        if (!isFinishing())
            progressDialog.show();
        final BaseAccessoryItem face = new BaseAccessoryItem(faceItem.getFaceObjkey(), faceItem.getObjFaceDStatus(), faceItem.getFacePngkey(), faceItem.getTextureFaceDStatus());
        if (faceItem.getHairObjkey() != null && !faceItem.getHairObjkey().isEmpty() && faceItem.getHairPngKey() != null && !faceItem.getHairPngKey().isEmpty()) {
            face.dependentItem = new BaseAccessoryItem(faceItem.getHairObjkey(), faceItem.getObjHairDStatus(), faceItem.getHairPngKey(), faceItem.getTextureHairDStatus());
        }
        new AssetDownloader(this).downloadAsset(face, new OnAssetDownloadListener() {
            @Override
            public void onDownload(BaseAccessoryItem item) {
                faceItem.setObjFaceDStatus(ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus());
                faceItem.setTextureFaceDStatus(ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus());
                if (faceItem.getHairObjkey() != null && !faceItem.getHairObjkey().isEmpty() && faceItem.getHairPngKey() != null && !faceItem.getHairPngKey().isEmpty()) {
                    faceItem.setObjHairDStatus(ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus());
                    faceItem.setTextureHairDStatus(ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus());
                    faceItem.setFaceObjkey(item.getObjAwsKey());
                    if (item.dependentItem != null)
                        faceItem.setHairObjkey(item.dependentItem.getObjAwsKey());
                }
                if (progressDialog != null)
                    progressDialog.dismiss();
                updateDefaultFace();
                launchDataActivity();
            }

            @Override
            public void onDownloadFailed(String comboId) {
                int retryCount = count+1;
                if(count<ConstantsUtil.COUNT_RETRY_SERVICE_CRITICAL){
                    startFaceItemDownload(retryCount);
                }
                if (progressDialog != null)
                    progressDialog.dismiss();
            }

            @Override
            public void onDownloadProgress(String comboId, int percentage) {

            }
        });
    }

    private void updateDefaultFace() {
        //feducialPath
        dataSource.deleteComboDetailForFaceChange(faceItem.getFaceId());
        dataSource.deleteComboDetailLikeForFaceChange(faceItem.getFaceId());
        InkarneAppContext.saveSettingIsDefaultFaceChanged(true);
        User.getInstance().setDefaultFaceId(faceItem.getFaceId());
        User.getInstance().setDefaultFaceItem(faceItem);
        InkarneAppContext.getDataSource().create(User.getInstance());
        InkarneAppContext.getDataSource().create(faceItem);
        updateDefaultFaceToServer(0);
    }

    private void updateDefaultFaceToServer(final int retryCount) {
        String url = ConstantsUtil.URL_BASEPATH + ConstantsUtil.URL_METHOD_UPDATE_DEFAULT_FACE + User.getInstance().getmUserId() + "/" + User.getInstance().getDefaultFaceId();
        DataManager.getInstance().updateDefaultFace(url, new DataManager.OnResponseHandlerInterface() {
            @Override
            public void onResponse(Object obj) {

            }
            @Override
            public void onResponseError(String errorMessage, int errorCode) {
                int count = retryCount + 1;
                if (count < ConstantsUtil.COUNT_RETRY_SERVICE_CRITICAL && !isFinishing())
                    updateDefaultFaceToServer(count);
            }
        });
    }

    public void launchDataActivity() {
        if (!isNextActivityLaunched) {
            isNextActivityLaunched = true;
            InkarneAppContext.refreshDataAsync();
            Log.e(LOGTAG, " ******** Launch ShopActivity in DataActivity  *******");
            Intent intent = new Intent(AdjustPicActivity.this, DataActivity.class);
            if (InkarneAppContext.comboId != null) {
                intent.putExtra("comboDataId", InkarneAppContext.comboId);
            }
            startActivity(intent);
            finish();
        }
    }

}
