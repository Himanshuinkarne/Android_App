package com.svc.sml.Activity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.VideoView;

import com.svc.sml.R;
import com.svc.sml.Utility.ConstantsUtil;

public class Video360Activity extends BaseActivity {

    public static final String LOGTAG = "Video360Activity";
    public static final String INTENT_KEY_VIDEO_PATH = "videoKey";
    public static final String INTENT_KEY_COMBO_ID = "comboId";
    public static final String INTENT_KEY_VIDEO_URI = "videoUri";
    private LinearLayout conBtnSkip;
    private String videoPath,videoKey;
    private String comboId;
    private Uri videoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        comboId = getIntent().getStringExtra(INTENT_KEY_COMBO_ID);
        videoKey = getIntent().getStringExtra(INTENT_KEY_VIDEO_PATH);
        videoUri =   getIntent().getParcelableExtra(INTENT_KEY_VIDEO_URI);
        videoPath = ConstantsUtil.FILE_PATH_APP_ROOT_VIDEO + videoKey;

        splashPlayer();
        GATrackActivity(LOGTAG);
    }

    public void onResume(){
        super.onResume();
        splashPlayer();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finishActivity();
//        mTracker.send(new HitBuilders.EventBuilder()
//                .setCategory("L1")
//                .setAction("L2")
//                .setLabel("L3")
//                .build());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finishActivity();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void splashPlayer() {
        Log.w(LOGTAG,"video 360 :"+ videoPath);
        final VideoView video1 = (VideoView) findViewById(R.id.vv_info_face_selection);
        conBtnSkip = (LinearLayout) findViewById(R.id.con_btn_skip);
        conBtnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                video1.stopPlayback();
                finishActivity();
            }
        });

       // video1.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.v_splash);
        video1.setVideoPath(videoPath);
        video1.start();
        video1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                finishActivity();
            }
        });
        video1.start();
        video1.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return true;
            }
        });
    }


    private void finishActivity(){
        Intent resultIntent = new Intent();
// TODO Add extras or a data URI to this intent as appropriate.
        resultIntent.putExtra(INTENT_KEY_VIDEO_PATH, videoKey);
        resultIntent.putExtra(INTENT_KEY_COMBO_ID, comboId);
        resultIntent.putExtra(INTENT_KEY_VIDEO_URI, videoUri);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}
