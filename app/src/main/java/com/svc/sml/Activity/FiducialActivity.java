package com.svc.sml.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.svc.sml.Database.User;
import com.svc.sml.Fragments.VideoFragment;
import com.svc.sml.Helper.DataManager;
import com.svc.sml.InkarneAppContext;
import com.svc.sml.R;
import com.svc.sml.Utility.AWSUtil;
import com.svc.sml.Utility.ConstantsUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//import android.support.v7.app.AlertDialog;

public class FiducialActivity extends Activity implements VideoFragment.OnVideoFragmentInteractionListener {

    public static final String LOGTAG = "FiducialsActivity";
    public static final String EXTRA_PARAM_USER_PIC_PATH = "EXTRA_PARAM_USER_PIC_PATH";
    public static final String EXTRA_PARAM_USER_HEIGHT_ARRAY = "EXTRA_PARAM_USER_HEIGHT_ARRAY";
    public static final String EXTRA_PARAM_USER_WIDTH_ARRAY = "EXTRA_PARAM_USER_WIDTH_ARRAY";

    public static final String EXTRA_PARAM_DEMO_PIC_PATH = "EXTRA_PARAM_DEMO_PIC_PATH";
    public static final String EXTRA_PARAM_DEMO_HEIGHT_ARRAY = "EXTRA_PARAM_DEMO_HEIGHT_ARRAY";
    public static final String EXTRA_PARAM_DEMO_WIDTH_ARRAY = "EXTRA_PARAM_DEMO_WIDTH_ARRAY";
    protected static int widthNew = 500;
    protected static int heightNew = 500;
    protected static int border = 0;
    protected static int offsetY = 400;
    protected static final int zoomScale = 1;
    protected static final int lengthCrossLine = 25;
    protected static final int lengthCrossLineZoom = 200;
    protected boolean isProgressOn = false;
    protected static final int[] arrayFedRefImages = new int[]{
            R.drawable.f_1_ex_ear_right,
            R.drawable.f_0_ex_ear_left,

            R.drawable.f_3_in_nose_right,
            R.drawable.f_2_in_nose_left,

            R.drawable.f_5_in_lip_right,
            R.drawable.f_4_in_lip_left,

            R.drawable.f_7_in_eye_right,
            R.drawable.f_6_in_eye_left,

            R.drawable.f_9_ex_jaw_right,
            R.drawable.f_8_ex_jaw_left,

            R.drawable.f_10_ex_chin
    };

   // protected static final int[] arrayFedPointMovedInitial = new int[]{0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 1};
    protected static final int[] arrayFedPointMovedInitial = new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
    protected static int[] arrayFedPointMoved = arrayFedPointMovedInitial;

    protected static final int[] arrayFedPointImagesRed = new int[]{
            R.drawable.fd_1_ear_right_small_red,
            R.drawable.fd_0_ear_left_small_red,

            R.drawable.fd_3_nose_right_small_red,
            R.drawable.fd_2_nose_left_small_red,

            R.drawable.fd_5_lip_right_small,
            R.drawable.fd_4_lip_left_small,

            R.drawable.fd_7_eye_right_small,
            R.drawable.fd_6_eye_left_small,


            R.drawable.fd_9_jaw_right_small_red,
            R.drawable.fd_8_jaw_left_small_red,

            R.drawable.fd_10_chin_small
    };

    protected static final int[] arrayFedPointImages = new int[]{
            R.drawable.fd_1_ear_right_small,
            R.drawable.fd_0_ear_left_small,

            R.drawable.fd_3_nose_right_small,
            R.drawable.fd_2_nose_left_small,

            R.drawable.fd_5_lip_right_small,
            R.drawable.fd_4_lip_left_small,

            R.drawable.fd_7_eye_right_small,
            R.drawable.fd_6_eye_left_small,


            R.drawable.fd_9_jaw_right_small,
            R.drawable.fd_8_jaw_left_small,

            R.drawable.fd_10_chin_small
    };

    protected static final int[] arrayFedPointZoomImages = new int[]{
            R.drawable.fd_1_ear_right,
            R.drawable.fd_0_ear_left,

            R.drawable.fd_3_nose_right,
            R.drawable.fd_2_nose_left,

            R.drawable.fd_5_lip_right,
            R.drawable.fd_4_lip_left,

            R.drawable.fd_7_eye_right,
            R.drawable.fd_6_eye_left,

            R.drawable.fd_9_jaw_right,
            R.drawable.fd_8_jaw_left,

            R.drawable.fd_10_chin
    };

    protected static final String[] arrayFedRefText = new String[]{
//            "Middle of Right Ear",
            "Outer edge of right cheek bone",
            "Outer edge of left cheek bone",
//            "Middle of Left Ear",

            "Outer Middle Edge of Right Nostril",
            "Outer Middle Edge of Left Nostril",

            "Right Edge of Lips",
            "Left Edge of Lips",

            "Centre of Right Eye",
            "Centre of Left Eye",

            "Outer Edge of Right Jawline",
            "Outer Edge of Left Jawline",

            "Tip of the Chin"
    };


//    static {
//        System.loadLibrary("opencv_java3");
//    }

    protected ProgressDialog progressDialogFed;
//    protected Mat umatrix;
//    protected Mat dmatrix;

    protected Bitmap uBitmap;

    protected int uWIDTH; // physical user pic width in pixels
    protected int uHEIGHT;

    protected float[] uwidth;
    protected float[] uheight;

    protected float uratiow = 1;
    protected float uratioh = 1;

    //Other variables
    protected int FedNo;
    protected int selectedLayer;
    protected int[] fedPointsIndexToShow;
    //protected String dFilePathPic;
    protected String uFilePathPic;

    private int countRetry = 0;

    /*FaceItem related*/
    private TransferUtility transferUtility;

    protected ImageView utable;
    protected ImageView dtable;
    protected ImageView dtableLeft;
    protected Button btnNext;
    protected RelativeLayout conTopBar;
    protected TextView tvTitle;
    protected String title;
    protected ImageButton btnHint;
    private RelativeLayout conVideoFragment;

    protected String picSource = "0";
    protected String awsKeyPathPicUpload;
    protected float ratioImageResize = 1;
    protected Paint paint;
    protected Paint borderpaint;
    protected Paint paintLipJoin;
    protected VideoFragment videoFragment;

    private Bitmap canvasBitmap;
    private Bitmap bitmapZoomed;
    private Tracker mTracker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fiducial);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        InkarneAppContext application = (InkarneAppContext) getApplication();
        mTracker = application.getTracker();
        GATrackActivity();

        heightNew = (int) getResources().getDimension(R.dimen.height_fiducial_window);
        widthNew = heightNew;
        border = 0;
        offsetY = (int) getResources().getDimension(R.dimen.offset_fiducial_y_offset);

        btnNext = (Button) findViewById(R.id.btn_shared_next);
        //btnNext.setTypeface(InkarneAppContext.getInkarneTypeFaceFutura());
        //btnNext.setText("Click to confirm face detection markers \n and move next \n");
        conTopBar = (RelativeLayout) findViewById(R.id.container_topbar_fiducial);
        tvTitle = (TextView) findViewById(R.id.tv_title_fiducial);
        btnHint = (ImageButton) findViewById(R.id.btn_fiducial_hint);
        utable = (ImageView) findViewById(R.id.userpic);
        dtable = (ImageView) findViewById(R.id.demopic);
        dtableLeft = (ImageView) findViewById(R.id.demopic_left);
        conVideoFragment = (RelativeLayout) findViewById(R.id.container_video_fragment);
        progressDialogFed = getProgressDialog();

        uwidth = new float[11];
        uheight = new float[11];

        selectedLayer = -1;
        FedNo = 11;

        tvTitle.setText(getHeaderTitle());
        //showVideoFragment();
        initDrawFedPoints();
        btnHint.setVisibility(View.GONE);
        btnHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showVideoFragment();
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           btnNextHandler();
                                       }
                                   }

        );
        utable.setOnTouchListener(new View.OnTouchListener() {
                                      @Override
                                      public boolean onTouch(View v, MotionEvent event) {
                                          return onTouchHandler(v, event);
                                      }
                                  }
        );

    }

    protected void GATrackActivity() {
        Log.i(LOGTAG, "Setting screen name: " + LOGTAG);
        mTracker.setScreenName(LOGTAG);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        Log.e(LOGTAG, "onCreate");
    }

    public boolean onTouchHandler(View v, MotionEvent event) {
        //lets do some maths, objective is to calculate (x,y) which is coord at which zoomed image starts and (w,h) which is size of zoomed image
        int l = -1; // l means the point selected
        int k = 0;
        float ex = event.getX(); //touch coordinate on screen
        float ey = event.getY();

        //Determine the point to be moved
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            for (k = 0; k < FedNo; k++) {
                if (!isFedPointIndexToShow(k)) {
                    continue;
                }
                float num1 = uheight[k] - ey;
                float num2 = uwidth[k] - ex;
                float abs_num1 = (num1 < 0) ? -num1 : num1;
                float abs_num2 = (num2 < 0) ? -num2 : num2;

                if (((abs_num1) < 60) && ((abs_num2) < 60)) {
                    l = k;
                    selectedLayer = l; // this code lets you pick up the point touched
                }
            }
            l = selectedLayer;
            if (!(l == -1)) {
                if (l % 2 == 0) { //right clciked
                    dtable.setImageResource(0);
                    dtableLeft.setImageResource(arrayFedRefImages[l]);
                } else {
                    dtableLeft.setImageResource(0);
                    dtable.setImageResource(arrayFedRefImages[l]);
                }
                tvTitle.setText(arrayFedRefText[l]);
            }
        }

        l = selectedLayer;
        if (!(l == -1)) {
            arrayFedPointMoved[l] = 1;
            canvasBitmap.eraseColor(Color.TRANSPARENT);
            Canvas uca = new Canvas(canvasBitmap);
            setPaintZoomState();
            drawFeaturedZoomImage(uca, ex, ey, l);

            uwidth[l] = ex;
            uheight[l] = ey;

            widthNew = (int) (uBitmap.getWidth() / 2.5);
            heightNew = (int) (uBitmap.getHeight() / 2.5);

            if (widthNew > heightNew)
                widthNew = heightNew;
            else
                heightNew = widthNew;
            offsetY = heightNew;

            int xnew = Math.max((int) (ex - widthNew / 2), 0);
            int ynew = Math.max((int) (ey - heightNew / 2), 0);
            Log.w(LOGTAG, "xnew: " + xnew + "   ynew: " + ynew);

            int width_ = Math.min((uBitmap.getWidth() - xnew), widthNew);
            int headerH = (int) getResources().getDimension(R.dimen.height_header);
            int height_ = Math.min((uBitmap.getHeight() - ynew - headerH), heightNew);
            Log.e(LOGTAG, "width_: " + width_ + "   height_: " + height_);
            int diffH = heightNew - height_;
            if (diffH > 0) {
                ynew -= diffH;
                height_ = heightNew;
                //height_ = uBitmap.getHeight()/3;
            }

            int diffW = widthNew - width_;
            if (diffW > 0) {
                xnew -= diffW;
                width_ = widthNew;
                //width_ = uBitmap.getWidth()/3;
            }
            if (xnew < 0) {
                xnew = 0;
            }
            if (ynew < 0) {
                ynew = 0;
            }


            Bitmap bm = Bitmap.createBitmap(uBitmap, xnew + border, ynew + border, width_ - border * 2, height_ - border * 2);
            int startX = Math.max((int) (ex - bm.getWidth() / 2) - diffW, 0);
            int startY = Math.max((int) (ey - bm.getHeight() / 2 - offsetY - diffH), 0);
            int offset = offsetY;

            uca.drawRect(startX, startY, startX + width_, startY + height_, getBorderPaint());//draw your bg
            uca.drawBitmap(bm, startX + border, startY + border, getPaint());
            if (l == 8 || l == 9) {
                //drawCrossLineBetweenLips(uca, startX + border, startX + width_ - 2 * border, offset);
            }
            drawFeaturedImage(uca, ex, Math.max((int) (ey - offsetY), 0), l);

            BitmapDrawable ubmpl = new BitmapDrawable(getResources(), canvasBitmap);
            BitmapDrawable ubmp = new BitmapDrawable(getResources(), uBitmap);
            Drawable[] layers = new Drawable[2];
            layers[0] = ubmp;
            layers[1] = ubmpl;
            LayerDrawable uldr = new LayerDrawable(layers);
            utable.setImageDrawable(uldr);
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (!(l == -1)) {
//                uwidth[l] = xadj + ex / zw;
//                uheight[l] = yadj + ey / zh;
                uwidth[l] = ex;
                uheight[l] = ey;
            }
            selectedLayer = -1;

            //Lets redraw user image with new points
            //Bitmap ub = Bitmap.createBitmap(uWIDTH, uHEIGHT, Bitmap.Config.ARGB_8888);
            canvasBitmap.eraseColor(Color.TRANSPARENT);
            Canvas uca = new Canvas(canvasBitmap);
            setDefaultPaint();
            for (k = 0; k < FedNo; k++) {
                if (!isFedPointIndexToShow(k)) {
                    continue;
                }
                //drawCrossLine1(uca, uwidth[k], uheight[k], lengthCrossLine);
                drawFeaturedImage(uca, uwidth[k], uheight[k], k);
            }
            BitmapDrawable u_bmp1 = new BitmapDrawable(getResources(), canvasBitmap);
            Drawable[] layers = new Drawable[2];
            BitmapDrawable u_bmp = new BitmapDrawable(getResources(), uBitmap);
            layers[0] = u_bmp;
            layers[1] = u_bmp1;
            LayerDrawable u_ldr = new LayerDrawable(layers);
            utable.setImageDrawable(u_ldr);

            //reset demo pic and title
            dtable.setImageResource(0);
            dtableLeft.setImageResource(0);
            tvTitle.setText(getHeaderTitle());
            return true;
        }
        return true;
    }


    private static String getScreenResolution(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        return "{" + width + "," + height + "}";
    }

    public ProgressDialog getProgressDialog() {
        ProgressDialog p = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
        p.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        p.setCanceledOnTouchOutside(false);
        p.setCancelable(true);
        return p;
    }

    private String getHeaderTitle() {
        if (title == null) {
            if (isExternalFiducialFeature()) {
                //title = "External Facial Points";
                title = "Fine-tune facial features";
            } else {
                title = "Fine-tune facial features";
            }
        }
        return title;
    }

    protected void initFaceItem() {
        uFilePathPic = getIntent().getStringExtra(FaceSelectionActivity.EXTRA_PARAM_PIC_PATH);
        picSource = getIntent().getStringExtra(FaceSelectionActivity.EXTRA_PARAM_PIC_SOURCE_TYPE);
        awsKeyPathPicUpload = getIntent().getStringExtra(AdjustPicActivity.EXTRA_PARAM_PIC_AWS_KEY_PATH);
        ratioImageResize = getIntent().getFloatExtra(AdjustPicActivity.EXTRA_PARAM_RESIZE_RATIO, (float) 1.0);

        transferUtility = AWSUtil.getTransferUtility(this);
    }

    //@Override
    protected void btnNextHandler() {
        if (uheight.length == 0 || uheight[0] == 0) {
            getUserFedPoints();
            return;
        }
        Intent intent = new Intent(this, FiducialActivity2.class);
        intent.putExtra(EXTRA_PARAM_USER_PIC_PATH, uFilePathPic);
        intent.putExtra(EXTRA_PARAM_USER_HEIGHT_ARRAY, uheight);
        intent.putExtra(EXTRA_PARAM_USER_WIDTH_ARRAY, uwidth);

        intent.putExtra(AdjustPicActivity.EXTRA_PARAM_PIC_AWS_KEY_PATH, awsKeyPathPicUpload);
        intent.putExtra(FaceSelectionActivity.EXTRA_PARAM_PIC_SOURCE_TYPE, picSource);
        intent.putExtra(AdjustPicActivity.EXTRA_PARAM_RESIZE_RATIO, ratioImageResize);

        startActivity(intent);
    }

    //@Override
    protected void initDrawFedPoints() {
        if (getIntent() == null)//Unusual case TODO
        {
            Log.e(LOGTAG, "getIntent() null");
            Intent i1 = new Intent(FiducialActivity.this, FaceSelectionActivity.class);
            startActivity(i1);
            return;
        }
        initFaceItem();
        if (uFilePathPic != null) {
            InkarneAppContext.fiducialPicPath = uFilePathPic;
        } else {
            uFilePathPic = InkarneAppContext.fiducialPicPath;
        }

        if (uFilePathPic == null) {
            //finish();
            Log.e(LOGTAG, "uFilePathPic null");
            Intent i1 = new Intent(FiducialActivity.this, FaceSelectionActivity.class);
            startActivity(i1);
        } else {
            initUserFiducials();
            getUserFedPoints();
        }
    }


    protected boolean isFedPointIndexToShow(int k) {
        if (fedPointsIndexToShow == null || fedPointsIndexToShow.length == 0)
            fedPointsIndexToShow = getFedPointsIndexToShow();
        for (int i = 0; i < fedPointsIndexToShow.length; i++) {
            int febIndex = fedPointsIndexToShow[i];
            if (k == febIndex) {
                return true;
            }
        }
        return false;
    }

    protected String getVideoPaths() {
        String videoPath = null;
        String basePath = "android.resource://" + getPackageName() + "/";
        if (isExternalFiducialFeature()) {
            if (User.getInstance().getmGender().equals("m")) {
                videoPath = basePath + R.raw.v_face_selection_light_instruction_loop_female;
            } else {
                videoPath = basePath + R.raw.v_face_selection_light_instruction_loop_female;
            }
        } else {
            if (User.getInstance().getmGender().equals("m")) {
                // videoPath = basePath + R.raw.v_fiducial_internal_male;
            } else {
                //videoPath = basePath + R.raw.v_fiducial_internal_female;
            }
        }
        return videoPath;
    }

    protected void showVideoFragment() {
        if (videoFragment == null) {
            //videoFragment = VideoFragment.newInstance(isExternalFiducialFeature());
            String[] titles = new String[]{getString(R.string.video_title_feducial_point_move)};
            videoFragment = VideoFragment.newInstance(new String[]{getVideoPaths()}, titles, 0);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.container_video_fragment, videoFragment);
            //ft.addToBackStack(null);
            ft.commit();

        } else if (videoFragment.isDetached()) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.attach(videoFragment);
            ft.show(videoFragment);
            ft.commit();
        }
    }

    protected void hideVideoFragment() {
        if (videoFragment != null && !videoFragment.isHidden()) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            //ft.hide(videoFragment);
            ft.detach(videoFragment);
            try {
                ft.commit();
            } catch (IllegalStateException ignore) {

            }
        }
        if (isProgressOn && progressDialogFed != null) {
            progressDialogFed.setTitle(getString(R.string.message_fiducials_get_points_2));
            progressDialogFed.setMessage(getString(R.string.message_wait));
            if (!isFinishing()) {
                if (videoFragment == null || videoFragment.isHidden() || videoFragment.isDetached())
                    progressDialogFed.show();
            }
        }
    }

    @Override
    public void onVideoFragmentInteractionSkip(int code) {
        hideVideoFragment();
    }

    //@Override
    protected int[] getFedPointsIndexToShow() {
        int[] fedPoints = new int[]{0, 1, 8, 9, 10};
        return fedPoints;
    }

    //@Override
    protected boolean isExternalFiducialFeature() {
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        //showVideoFragment();

        countRetry = 0;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressDialogFed != null)
            progressDialogFed.dismiss();
        InkarneAppContext.fiducialPicPath = null;
        hideVideoFragment();
    }

    @Override
    public void onStop() {
        super.onStop();
//        if(progressDialogFed!=null)
//            progressDialogFed.dismiss();
        hideVideoFragment();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // your code
        }
        return super.onKeyDown(keyCode, event);
    }

    private Paint getPaint() {
        if (paint == null) {
            paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setAntiAlias(true);
            paint.setStrokeWidth((float) 7);
            paint.setARGB(251, 57, 255, 10);
        }
        return paint;
    }

    private Paint getBorderPaint() {
        if (borderpaint == null) {
            borderpaint = new Paint();
            borderpaint.setStyle(Paint.Style.STROKE);
            borderpaint.setAntiAlias(true);
            borderpaint.setStrokeWidth((float) 8);
            borderpaint.setARGB(251, 100, 100, 100);
        }
        return borderpaint;
    }

    private Paint getLipJoinPaint() {
        if (paintLipJoin == null) {
            paintLipJoin = new Paint();
            paintLipJoin.setStyle(Paint.Style.STROKE);
            paintLipJoin.setAntiAlias(true);
            paintLipJoin.setStrokeWidth((float) 4);
            //paintLipJoin.setPathEffect(new DashPathEffect(new float[]{5, 10, 15, 20}, 0));
            paintLipJoin.setAlpha(120);
            paintLipJoin.setPathEffect(new DashPathEffect(new float[]{10, 10}, 5));
            paintLipJoin.setARGB(255, 125, 25, 25);
        }
        return paintLipJoin;
    }

    private void setDefaultPaint() {
        paint = getPaint();
        paint.setStrokeWidth((float) 7);
        paint.setARGB(251, 57, 255, 10);
    }

    private void setPaintZoomState() {
        paint = getPaint();
        paint.setStrokeWidth((float) 20);
        paint.setARGB(251, 60, 34, 115);
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
                        Intent intent = new Intent(FiducialActivity.this, FaceSelectionActivity.class);
                        //if (shouldShow2ndInstruction)
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

    protected void getUserFedPoints() {
        arrayFedPointMoved = arrayFedPointMovedInitial;
        //arrayFedPointMoved = new int[]{0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 1};

        if (progressDialogFed == null)
            progressDialogFed = getProgressDialog();
        progressDialogFed.setTitle(getString(R.string.message_fiducials_get_points_2));
        progressDialogFed.setMessage(getString(R.string.message_wait));
        if (!isFinishing()) {
            if (videoFragment == null || videoFragment.isHidden() || videoFragment.isDetached()) {
                progressDialogFed.show();
            }
        }
        isProgressOn = true;
        final String[] ucolumns = new String[11];
        final String[] urows = new String[11];
        String url = ConstantsUtil.URL_BASEPATH_feducials + ConstantsUtil.URL_METHOD_USER_FEDUCIAL_POINTS + User.getInstance().getmUserId() + "/?Pic_Path=" + awsKeyPathPicUpload;
        DataManager.getInstance().requestUserFedPoints(url, new DataManager.OnResponseHandlerInterface() {
            @Override
            public void onResponse(Object obj) {
                if (progressDialogFed != null && progressDialogFed.isShowing())
                    progressDialogFed.dismiss();
                isProgressOn = false;
                JSONException error = null;
                JSONObject jsonObj = (JSONObject) obj;

                try {
                    int error_code = (int) jsonObj.get("Error_Code");
                    if (error_code != 0) {
                        String message = "";
                        boolean shouldProceed = true;
                        boolean shouldShow2ndInstruction = false;
                        switch (error_code) {
                            case 10: {
                                message = "Are you wearing specs?";
                                shouldProceed = false;
                            }
                            break;
                            case 20: {
                                message = "Is your mouth open?";
                                shouldProceed = false;
                            }
                            break;
                            case 30: {
                                message = "Is hair covering your face?";
                                shouldProceed = false;
                            }
                            break;
                            case 40: {
                                message = "Is your eyes closed?";
                                shouldProceed = false;
                            }
                            break;
                            case 50: {
                                message = "Are you looking straight?";
                                shouldProceed = false;
                            }
                            break;
                            case 60: {
                                message = "Picture is not suitable";
                                shouldProceed = false;
                                shouldShow2ndInstruction = true;
//                                SharedPreferences settings = InkarneAppContext.getAppContext().getSharedPreferences("inkarne", 0);
//                                SharedPreferences.Editor editor = settings.edit();
//                                editor.putBoolean(FaceSelectionActivity.EXTRA_PARAM_SHOULD_SHOW_2nd_VIDEO, true);
//                                editor.commit();
                            }
                            break;
                            case 70: {
                                message = "We detected multiple faces";
                                shouldProceed = false;
                            }
                            break;
                            case 300: {
                                message = "Picture is not suitable";
                                shouldProceed = false;
                                shouldShow2ndInstruction = true;
                            }
                            break;
                            default: {
                                //message = "#TEST ERROR Code";
                                //shouldProceed = true;
                            }
                            break;
                        }

                        Log.e(LOGTAG, "code :" + error_code + "  message : " + message);

                        if (!shouldProceed) {
                            showAlertError(message, "Please choose another pic.", shouldShow2ndInstruction);
                            return;
                        } else if (message.length() != 0) {
                            showAlertWarning(message, "");
                        }
                    }

                    JSONArray fDetails = jsonObj.getJSONArray("FedInfo");
                    for (int i = 0; i < fDetails.length(); i++) {
                        JSONObject obj2 = null;
                        try {
                            obj2 = fDetails.getJSONObject(i);
                            String c = obj2.getString("column");
                            float c1 = Integer.parseInt(c) /ratioImageResize;
                            ucolumns[i] = String.valueOf(c1);

                            String r = obj2.getString("row");
                            float r1 = Integer.parseInt(r) /ratioImageResize;
                            urows[i] = String.valueOf(r1);
                            Log.d(LOGTAG, "getUserFePoints - index : " + i + "  row: " + urows[i] + " col: " + ucolumns[i]);
                        } catch (JSONException e) {
                            error = e;
                            e.printStackTrace();
                        }
                    }

                    if (error == null) {
                        normalizeUserFedPoints(urows, ucolumns);
                        drawUserFiducialPoints();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseError(String errorMessage, int errorCode) {
                if (progressDialogFed != null && progressDialogFed.isShowing())
                    progressDialogFed.dismiss();

                countRetry++;
                if (errorCode == DataManager.CODE_DATA_MANAGER_NETWORK_ERROR) {
                    if (countRetry == 1) {
                        Toast.makeText(getApplicationContext(), ConstantsUtil.MESSAGE_TOAST_NETWORK_RESPONSE_FAILED, Toast.LENGTH_SHORT).show();
                    }
                }
                if (countRetry < ConstantsUtil.COUNT_RETRY_SERVICE_CRITICAL) {
                    getUserFedPoints();
                } else {
                    isProgressOn = false;
                    finish();//TODO
                }
            }
        });
    }


    public Bitmap scaleImage(Bitmap realImage, float ratio,
                            boolean filter) {
        Log.e(LOGTAG,"w: "+ realImage.getWidth()+"  h:"+realImage.getHeight() );
        int width = Math.round((float) realImage.getWidth()/ratio );
        int height = Math.round((float) realImage.getHeight()/ratio);
        Log.e(LOGTAG,"new w: "+ width+"  h:"+height+ "  r:"+ratio );
        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }
    //@Override
    protected void initUserFiducials() {
        int uwidth_physical_pixels;
        int uheight_physical_pixels;

        BitmapFactory.Options options2 = new BitmapFactory.Options();
        options2.inPreferredConfig = Bitmap.Config.ARGB_8888;

        Bitmap bm = BitmapFactory.decodeFile(uFilePathPic, options2);
        uBitmap = scaleImage(bm,ratioImageResize,true);

        uwidth_physical_pixels = uBitmap.getWidth();
        uheight_physical_pixels = uBitmap.getHeight();
        //bm.recycle();
        //BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        //options.inSampleSize = 2;

        //uBitmap = BitmapFactory.decodeFile(uFilePathPic, options);

        RelativeLayout.LayoutParams layoutParamsu = new RelativeLayout.LayoutParams(uwidth_physical_pixels, uheight_physical_pixels);
        layoutParamsu.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        utable.setLayoutParams(layoutParamsu);
        //uBitmap.recycle();

        String text1 = "Set Image HEIGHT:" + String.valueOf(uheight_physical_pixels) + " WIDTH:" + String.valueOf(uwidth_physical_pixels);
        Log.d("Check point ImageView", text1);

        uHEIGHT = uheight_physical_pixels;
        uWIDTH = uwidth_physical_pixels;
        canvasBitmap = Bitmap.createBitmap(uWIDTH, uHEIGHT, Bitmap.Config.ARGB_8888);
//        uratioh = 1;
//        uratiow = 1;
        utable.setImageBitmap(uBitmap);
    }

    private void normalizeUserFedPoints(String[] uRows, String[] uColumns) {
        for (int j = 0; j < FedNo; j++) {
//            uheight[j] = Float.parseFloat(uRows[j]) * uratioh;
//            uwidth[j] = Float.parseFloat(uColumns[j]) * uratiow;
            uheight[j] = Float.parseFloat(uRows[j]) ;
            uwidth[j] = Float.parseFloat(uColumns[j]);
        }
    }

    private void drawFeaturedImage(Canvas canvas, float cx, float cy, int index) {
        Bitmap bm = null;

        if (arrayFedPointMoved[index] == 0)
            bm = BitmapFactory.decodeResource(getResources(), arrayFedPointImagesRed[index]);
        else
            bm = BitmapFactory.decodeResource(getResources(), arrayFedPointImages[index]);

        canvas.drawBitmap(bm, cx - bm.getWidth() / 2, cy - bm.getHeight() / 2, getPaint());
        //bm.recycle();
    }

    private void drawFeaturedImageRed(Canvas canvas, float cx, float cy, int index) {
        Bitmap bm = BitmapFactory.decodeResource(getResources(), arrayFedPointImagesRed[index]);

        canvas.drawBitmap(bm, cx - bm.getWidth() / 2, cy - bm.getHeight() / 2, getPaint());
        //bm.recycle();
    }

    private void drawFeaturedZoomImage(Canvas canvas, float cx, float cy, int index) {
        Bitmap bm = BitmapFactory.decodeResource(getResources(), arrayFedPointZoomImages[index]);

        canvas.drawBitmap(bm, cx - bm.getWidth() / 2, cy - bm.getHeight() / 2, getPaint());
        //bm.recycle();
    }


    private void drawCrossLine1(Canvas canvas, float cx, float cy, float length) {
        float angle = (float) 45.0;
        drawCrossLine1(canvas, cx, cy, length, 45);
        angle += 90.0;
        drawCrossLine1(canvas, cx, cy, length, 45 + 90);
    }

    private void drawCrossLine1(Canvas canvas, float cx, float cy, float length, float angle) {
        Paint paint = getPaint();
        float startX = (float) (cx + length * Math.cos(Math.PI * angle / 180));
        float startY = (float) (cy + length * Math.sin(Math.PI * angle / 180));
        float stopX = (float) (cx + length * Math.cos(Math.PI * (angle + 180) / 180));
        float stopY = (float) (cy + length * Math.sin(Math.PI * (angle + 180) / 180));
        canvas.drawLine(startX, startY, stopX, stopY, paint);
    }

    private void drawCrossLineBetweenLips(Canvas canvas) {
        Paint paintLip = getLipJoinPaint();
        float startX = uwidth[4];
        float startY = uheight[4];
        float stopX = uwidth[5];
        float stopY = uheight[5];
        float len = 1000.0f;
        float tan = (stopY - startY) / (stopX - startX);
        startX += -len;
        startY += -len * tan;

        stopX += len;
        stopY += len * tan;

        canvas.drawLine(startX, startY, stopX, stopY, paintLip);
    }

    private void drawCrossLineBetweenLips(Canvas canvas, int startXX, int stopXX, int offset) {
        Paint paintLip = getLipJoinPaint();
        float startX = uwidth[4];
        float startY = uheight[4];
        float stopX = uwidth[5];
        float stopY = uheight[5];
        float len = 1000.0f;
        float tan = (stopY - startY) / (stopX - startX);

        int c = 0;
        //stopY = tan*stopX +c;
        c = (int) (stopY - tan * stopX);

        startX += -len;
        startY += -len * tan;

        stopX += len;
        stopY += len * tan;

        canvas.drawLine(startX, startY, stopX, stopY, paintLip);

        int startYY = (int) (startXX * tan) + c;
        int stopYY = (int) (stopXX * tan) + c;
        canvas.drawLine(startXX, startYY - offset, stopXX, stopYY - offset, paintLip);
    }

    //@Override
    protected void drawUserFiducialPoints() {
        //Lets create fiducials starting point for user picture
        //Bitmap ub = Bitmap.createBitmap(uWIDTH, uHEIGHT, Bitmap.Config.ARGB_8888);//
        if (canvasBitmap == null) {
            initDrawFedPoints();
            return;
            //initUserFiducials();
        }
        canvasBitmap.eraseColor(Color.TRANSPARENT);
        Canvas uca = new Canvas(canvasBitmap);
        for (int j = 0; j < (FedNo); j++) {
            if (!isFedPointIndexToShow(j)) {
                continue;
            }
            float cx = uwidth[j];
            float cy = uheight[j];
            drawFeaturedImage(uca, cx, cy, j);
            Log.d(LOGTAG, "index : " + j + "  uwidth: " + uwidth[j] + " uheight: " + uheight[j]);
        }
        BitmapDrawable ubmpl = new BitmapDrawable(getResources(), canvasBitmap);
        //Create user image with adjusted feducial layer
        Drawable[] layers = new Drawable[2];
        BitmapDrawable ubmp = new BitmapDrawable(getResources(), uBitmap);
        layers[0] = ubmp;
        layers[1] = ubmpl;
        LayerDrawable uldr = new LayerDrawable(layers);
        utable.setImageDrawable(uldr);
    }

}
