package com.svc.sml.Activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.svc.sml.Database.User;
import com.svc.sml.R;
import com.svc.sml.Utility.ConstantsFunctional;
import com.svc.sml.Utility.ConstantsUtil;

import java.util.Arrays;

public class PreFeducialActivity extends BaseActivity {
    public static final String LOGTAG = "PreFeducialActivity";
    private VideoView videoViewInScreen;
    private  String videoPathInScreen,videoPathInScreen2;
    private int interval_animation_in_milli = ConstantsFunctional.TIME_INSTRUCTION_VIEW_ANIMATION_IN_MILLI;
    private int interval_animation_in_milli_after_video = 4000;
    private TextView tvInstrTop;
    private LinearLayout tRow1,tRow2;
    private View footerView;

    private   View[] arrayViews;
    private  int countVideoLoop = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_feducial);
        videoViewInScreen = (VideoView) findViewById(R.id.vv_face_selection);
        if (User.getInstance().getmGender().equals("m")) {
            videoPathInScreen = ConstantsUtil.FILE_PATH_RAW_FOLDER + getPackageName() + "/" + R.raw.v_face_selection_light_instruction_loop_female;
        }else {
            videoPathInScreen = ConstantsUtil.FILE_PATH_RAW_FOLDER + getPackageName() + "/" + R.raw.v_face_selection_light_instruction_loop_female;
        }

        GATrackActivity(LOGTAG);
        playInScreenVideo();
//        boolean isFaceNotCreated =  User.getInstance().getDefaultFaceId() == null || User.getInstance().getDefaultFaceId().isEmpty()? true:false;
//        if(User.getInstance().getmGender().equals("m")){
//            if(isFaceNotCreated) {
//                videoPathInScreen = ConstantsUtil.FILE_PATH_RAW_FOLDER + getPackageName() + "/" + R.raw.v_prefiducial_shape_instruction_loop_male;
//                videoPathInScreen2 = ConstantsUtil.FILE_PATH_RAW_FOLDER + getPackageName() + "/" + R.raw.v_prefiducial_shape_instruction_loop_male_1;
//            }else{
//                videoPathInScreen = ConstantsUtil.FILE_PATH_RAW_FOLDER + getPackageName() + "/" + R.raw.v_prefiducial_shape_instruction_loop_male_1;
//                videoPathInScreen2 = videoPathInScreen;
//            }
//        }
//        else{
//            if(isFaceNotCreated) {
//                videoPathInScreen = ConstantsUtil.FILE_PATH_RAW_FOLDER + getPackageName() + "/" + R.raw.v_prefiducial_shape_instruction_loop_female;
//                videoPathInScreen2 = ConstantsUtil.FILE_PATH_RAW_FOLDER + getPackageName() + "/" + R.raw.v_prefiducial_shape_instruction_loop_female_1;
//            }else{
//                videoPathInScreen = ConstantsUtil.FILE_PATH_RAW_FOLDER + getPackageName() + "/" + R.raw.v_prefiducial_shape_instruction_loop_female_1;
//                videoPathInScreen2 = videoPathInScreen;
//            }
//        }

//        tvInstrTop = (TextView)findViewById(R.id.tv_instruction_image_selection);
//        tRow1  = (LinearLayout)findViewById(R.id.tableRow1);
//        tRow2  = (LinearLayout)findViewById(R.id.tableRow2);
//        footerView =(View)findViewById(R.id.shared_layout_bottom_view);
//
//        if(isFaceNotCreated) {
//            View[] array = new View[]{tvInstrTop, videoViewInScreen, tRow1, tRow2, footerView};
//            animateViews(interval_animation_in_milli, array);
//        }
    }


    protected String getVideoPaths() {
        String videoPath = null;
        String basePath = "android.resource://" + getPackageName() + "/";
        if (User.getInstance().getmGender().equals("m")) {
            videoPath = basePath + R.raw.v_face_selection_light_instruction_loop_female;
        } else {
            videoPath = basePath + R.raw.v_face_selection_light_instruction_loop_female;
        }
//        if (isExternalFiducialFeature()) {
//            if (User.getInstance().getmGender().equals("m")) {
//                videoPath = basePath + R.raw.v_fiducial_external_male;
//            } else {
//                videoPath = basePath + R.raw.v_fiducial_external_female;
//            }
//        } else {
//            if (User.getInstance().getmGender().equals("m")) {
//                videoPath = basePath + R.raw.v_fiducial_internal_female;
//            } else {
//                videoPath = basePath + R.raw.v_fiducial_internal_female;
//            }
//        }
        return videoPath;
    }

    @Override
    public void onResume() {
        super.onResume();
        playInScreenVideo();
//        if(videoViewInScreen.getVisibility()==View.VISIBLE) {
//            playInScreenVideo();
//        }
    }

    @Override
    public void onPause() {
        super.onPause();
            hideVideoInScreen();
    }

    public void animateViews(final int milli, final View ... views){
        if(views.length ==0)
            return;
        for(View v :views)
            v.setVisibility(View.INVISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                int length = views.length;
                View viewNew[] = Arrays.copyOfRange(views, 1, length);
                if (views[0] instanceof VideoView) {
                    playInScreenVideo();
                    arrayViews = viewNew;
                    animationAfterVideo();
                } else {
                    views[0].setVisibility(View.VISIBLE);
                    animateViews(milli, viewNew);
                }
            }
        }, milli);
    }

    public void animationAfterVideo() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                videoPathInScreen = videoPathInScreen2;
                videoViewInScreen.stopPlayback();
                playInScreenVideo();
                int length = arrayViews.length;
                arrayViews[0].setVisibility(View.VISIBLE);
                View viewNew[] = Arrays.copyOfRange(arrayViews, 1, length);
                animateViews(interval_animation_in_milli, viewNew);
            }
        }, interval_animation_in_milli_after_video);
    }


    public void hideVideoInScreen(){
        if(videoViewInScreen != null && videoViewInScreen.isPlaying())
            videoViewInScreen.stopPlayback();
        //videoViewInScreen.setVisibility(View.INVISIBLE);
    }

    /* change */
    public void playInScreenVideo() {
        videoViewInScreen.setVideoPath(videoPathInScreen);
        videoViewInScreen.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
//                countVideoLoop++;
//                if (countVideoLoop == 1) {
//                    int intervalMilli = interval_animation_in_milli;
//                    animateViews(intervalMilli, arrayViews);
//                }
//                if (countVideoLoop <= 50) {
//                    videoViewInScreen.seekTo(0);
//                    videoViewInScreen.start();
//                }
            }
        });
        videoViewInScreen.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
               // if(videoPathInScreen.equals(videoPathInScreen2))
                    mp.setLooping(true);
            }
        });
        videoViewInScreen.start();
        videoViewInScreen.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                /*
                if(videoView.isPlaying())
                   videoView.pause();
                else
                videoView.resume();
                */
                return true;
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                videoViewInScreen.setVisibility(View.VISIBLE);
            }
        }, 120);
    }


    public void nextBtnClickHandler(View v) {
        hideVideoInScreen();
        launchFiducial();
    }

    private synchronized void launchFiducialOld() {
        Intent intent = new Intent(this, FiducialActivity.class);
//        intent.putExtra(FaceSelectionActivity.EXTRA_PARAM_PIC_PATH, filePathAdjustedPic);
//        intent.putExtra(AdjustPicActivity.EXTRA_PARAM_PIC_AWS_KEY_PATH, userPicUploadAwsKeyPath);
//        intent.putExtra(FaceSelectionActivity.EXTRA_PARAM_PIC_SOURCE_TYPE, picSource);
        intent.putExtra(FaceSelectionActivity.EXTRA_PARAM_PIC_PATH, getIntent().getStringExtra(FaceSelectionActivity.EXTRA_PARAM_PIC_PATH));
        intent.putExtra(AdjustPicActivity.EXTRA_PARAM_PIC_AWS_KEY_PATH, getIntent().getStringExtra(AdjustPicActivity.EXTRA_PARAM_PIC_AWS_KEY_PATH));
        intent.putExtra(FaceSelectionActivity.EXTRA_PARAM_PIC_SOURCE_TYPE, getIntent().getStringExtra(FaceSelectionActivity.EXTRA_PARAM_PIC_SOURCE_TYPE));
        startActivity(intent);
    }

    private synchronized void launchFiducial() {
        // Intent intent = new Intent(this, FiducialActivity.class);
        Intent intent = new Intent(this, FiducialActivity2Final.class);
//        intent.putExtra(FaceSelectionActivity.EXTRA_PARAM_PIC_PATH, filePathAdjustedPic);
//        intent.putExtra(AdjustPicActivity.EXTRA_PARAM_PIC_AWS_KEY_PATH, userPicUploadAwsKeyPath);
//        intent.putExtra(FaceSelectionActivity.EXTRA_PARAM_PIC_SOURCE_TYPE, picSource);
        intent.putExtra(FaceSelectionActivity.EXTRA_PARAM_PIC_PATH, getIntent().getStringExtra(FaceSelectionActivity.EXTRA_PARAM_PIC_PATH));
        intent.putExtra(AdjustPicActivity.EXTRA_PARAM_PIC_AWS_KEY_PATH, getIntent().getStringExtra(AdjustPicActivity.EXTRA_PARAM_PIC_AWS_KEY_PATH));
        intent.putExtra(FaceSelectionActivity.EXTRA_PARAM_PIC_SOURCE_TYPE, getIntent().getStringExtra(FaceSelectionActivity.EXTRA_PARAM_PIC_SOURCE_TYPE));
        startActivity(intent);
    }

}
