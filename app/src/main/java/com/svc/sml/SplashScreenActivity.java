package com.svc.sml;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.svc.sml.Activity.BodyMeasurementActivity;
import com.svc.sml.Activity.DataActivity;
import com.svc.sml.Activity.NotificationWebActivity;
import com.svc.sml.Activity.RegistrationActivity;
import com.svc.sml.Database.User;
import com.svc.sml.Helper.DownloadIntentService;

public class SplashScreenActivity extends AppCompatActivity {
    public static final String LOGTAG = "SplashScreenActivity";
    public static final int DIALOG_FRAGMENT = 1;
    private LinearLayout conBtnSkip;
    private VideoView video1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        checkMemory();

//        resolutionTest();
//        try {
//            User user = User.getInstance();
//            if (user == null) {
//                splashPlayer();
//            }else{
//                jumpMain();
//            }
//        } catch (Exception ex) {
//            jumpMain();
//        }
        jumpMain();
    }

    private void checkMemory() {
        Intent intent = new Intent(SplashScreenActivity.this, DownloadIntentService.class);
        intent.setAction(DownloadIntentService.ACTION_DOWNLOAD_MEMORY_CLEANUP);
        startService(intent);
    }

    @Override
    public void onResume(){
        super.onResume();
//        try {
//            User user = User.getInstance();
//            if (user == null) {
//                splashPlayer();
//            }else{
//                jumpMain();
//            }
//        } catch (Exception ex) {
//            jumpMain();
//        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }

    public void splashPlayer() {
        setContentView(R.layout.splash);
         video1 = (VideoView) findViewById(R.id.vv_info_face_selection);
        conBtnSkip = (LinearLayout) findViewById(R.id.con_btn_skip);
        conBtnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                video1.stopPlayback();
                jumpMain();
            }
        });

//        video1.setVideoPath("android.resource://" + getPackageName() + "/"
//                + R.raw.v_splash);
        video1.setVideoPath("android.resource://" + getPackageName() + "/"
                + R.raw.v_face_selection_light_instruction_loop_female);
        video1.start();
        video1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                jumpMain();
            }

        });
        video1.start();
        video1.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //((VideoView) v).stopPlayback();
                //jumpMain();
                return true;
            }
        });
    }


    public void jumpMain() {
        User user = User.getInstance();
        if (user == null||user.getThumbUrl()==null|| user.getThumbUrl().isEmpty()) {
            Log.e(LOGTAG, "User is null");
            //Toast.makeText(getApplicationContext(), "New user? Please register.", Toast.LENGTH_SHORT).show();
            Intent i1 = new Intent(SplashScreenActivity.this, RegistrationActivity.class);
            startActivity(i1);
            finish();
        }else {
            if (InkarneAppContext.notificationData != null && InkarneAppContext.notificationData.optString("url", null) != null) {
                String url = InkarneAppContext.notificationData.optString("url", null);
                showNotificationFragmentDialog(url);
                InkarneAppContext.notificationData = null;
            } else {
                openMainActivity();
            }
        }
        //Toast.makeText(getApplicationContext(), "User: " + user.getmUserId() + "  :" + user.getmFirstName() + "  :" + user.getmGender(), Toast.LENGTH_SHORT).show();
    }

    public void showNotificationFragmentDialog(String url) {
        Log.e(LOGTAG, " ******** Launch DataActivity in FirstActivity  *******");
        Intent intent = new Intent(SplashScreenActivity.this, NotificationWebActivity.class);
        intent.putExtra("url",url);
        startActivity(intent);
        finish();
    }

    public void openMainActivity(){
        User user = User.getInstance();
        Log.d(LOGTAG, "User: " + user.getmUserId() + "  :" + user.getmFirstName() + "  :" + user.getmGender());
        if(user.getPBId() == null || user.getPBId().isEmpty()){
            Log.d(LOGTAG, "User PB is null/not created: ");
            Intent i1 = new Intent(SplashScreenActivity.this, BodyMeasurementActivity.class);
            startActivity(i1);
            finish();
        }
        else {
            Log.e(LOGTAG, " ******** Launch DataActivity in FirstActivity  *******");
            Intent intent = new Intent(SplashScreenActivity.this, DataActivity.class);

            //Intent intent = new Intent(SplashScreenActivity.this, LooksActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void showNotificationFragmentDialog1(String url) {
        TermsDialogFragment dialogFragTermsCondition = new TermsDialogFragment(url);
        dialogFragTermsCondition.setCancelable(true);
        dialogFragTermsCondition.setTargetFragment(dialogFragTermsCondition, DIALOG_FRAGMENT);
        dialogFragTermsCondition.show(getSupportFragmentManager().beginTransaction(), "notificationDialog");
    }


    private void resolutionTest(){
        Log.i("TAG", "SERIAL: " + Build.SERIAL);
        Log.i("TAG","MODEL: " + Build.MODEL);
        Log.i("TAG","ID: " + Build.ID);
        Log.i("TAG","Manufacture: " + Build.MANUFACTURER);
        Log.i("TAG","brand: " + Build.BRAND);
        //Log.i("TAG","BASE_OS: " + Build.VERSION.BASE_OS);
        Log.i("TAG","INCREMENTAL: " + Build.VERSION.INCREMENTAL);
        Log.i("TAG","SDK_INT: " + Build.VERSION.SDK_INT);
        Log.i("TAG","CODENAME: " + Build.VERSION.CODENAME);
        Log.i("TAG","CODENAME: " + Build.VERSION_CODES.LOLLIPOP);
        Log.i("TAG","type: " + Build.TYPE);
        Log.i("TAG","user: " + Build.USER);
        Log.i("TAG","BASE: " + Build.VERSION_CODES.BASE);
        Log.i("TAG","INCREMENTAL " + Build.VERSION.INCREMENTAL);
        Log.i("TAG","SDK  " + Build.VERSION.SDK);
        Log.i("TAG","BOARD: " + Build.BOARD);
        Log.i("TAG","BRAND " + Build.BRAND);
        Log.i("TAG","HOST " + Build.HOST);
        Log.i("TAG","FINGERPRINT: "+Build.FINGERPRINT);
        Log.i("TAG","Version Code: " + Build.VERSION.RELEASE);

        DisplayMetrics metrics1 = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics1);

        int hPx = metrics1.heightPixels;
        int wPx  = metrics1.widthPixels;
        Log.d(LOGTAG, "widthPx =  " + wPx + "   heightPx = " + hPx);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        Log.d(LOGTAG,"width =  "+ width  + "   height = "+height);
        //Determine screen size
        if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
            //Toast.makeText(this, "Large screen", Toast.LENGTH_SHORT).show();
            Log.e(LOGTAG,"Large screen" );
        }
        else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
            //Toast.makeText(this, "Normal sized screen", Toast.LENGTH_SHORT).show();
            Log.e(LOGTAG, "Normal sized screen");
        }
        else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {
            //Toast.makeText(this, "Small sized screen", Toast.LENGTH_SHORT).show();
            Log.e(LOGTAG, "Small sized screen");
        }
        else {
            //Toast.makeText(this, "Screen size is neither large, normal or small", Toast.LENGTH_SHORT).show();
            Log.e(LOGTAG,  "Screen size is neither large, normal or small");
        }

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int density = metrics.densityDpi;

        if (density == DisplayMetrics.DENSITY_HIGH) {
            //Toast.makeText(this, "DENSITY_HIGH... Density is " + String.valueOf(density), Toast.LENGTH_SHORT).show();
            Log.e(LOGTAG, "DENSITY_HIGH... Density is " + String.valueOf(density));
        }
        else if (density == DisplayMetrics.DENSITY_MEDIUM) {
            //Toast.makeText(this, "DENSITY_MEDIUM... Density is " + String.valueOf(density), Toast.LENGTH_SHORT).show();
            Log.e(LOGTAG, "DENSITY_MEDIUM... Density is " + String.valueOf(density));
        }
        else if (density == DisplayMetrics.DENSITY_LOW) {
            //Toast.makeText(this, "DENSITY_LOW... Density is " + String.valueOf(density), Toast.LENGTH_SHORT).show();
            Log.e(LOGTAG, "DENSITY_LOW... Density is " + String.valueOf(density));
        }
        else {
            //Toast.makeText(this, "Density is neither HIGH, MEDIUM OR LOW.  Density is " + String.valueOf(density), Toast.LENGTH_LONG).show();
            Log.e(LOGTAG, "Density is neither HIGH, MEDIUM OR LOW.  Density is " + String.valueOf(density));
        }
    }

    @SuppressLint("ValidFragment")
    public class TermsDialogFragment extends DialogFragment {
        public ProgressBar pbWebView;
        public WebView webView;
        private LinearLayout conBtnGotIt;
        private TextView tvTitle;
        private ImageButton btnBack;
        private String url = "http://www.stylemylooks.com/termsofuse.htm";

        public TermsDialogFragment(String url) {
          this.url = url;
        }

        public TermsDialogFragment newInstance(String url) {
            TermsDialogFragment dialogFragment = new TermsDialogFragment(url);
            Bundle bundle = new Bundle();
            bundle.putString("url", url);
            dialogFragment.setArguments(bundle);
            return dialogFragment;
        }

        public void goBackIfCan() {
            if (webView.canGoBack()) {
                webView.goBack();
            }
        }

        public void onResume() {
            super.onResume();
            getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        if (webView.canGoBack()) {
                            webView.goBack();
                            return false;
                        }
                        return true; // pretend we've processed it
                    } else
                        return false; // pass on to be processed as normal
                }
            });
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            View view = getActivity().getLayoutInflater().inflate(R.layout.dailog_notification, new LinearLayout(getActivity()), false);
            pbWebView = (ProgressBar) view.findViewById(R.id.pb_web);
            tvTitle = (TextView) view.findViewById(R.id.tv_header_title_terms_condition);
            //textview.setPaintFlags(textview.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            btnBack = (ImageButton) view.findViewById(R.id.btn_back_terms_condition);
            webView = (WebView) view.findViewById(R.id.wb_terms_condition);
            conBtnGotIt = (LinearLayout)view.findViewById(R.id.con_btn_notification);
            conBtnGotIt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    openMainActivity();
                }
            });

            //webView.loadUrl("file:///android_asset/terms_condition.html");
            Dialog builder = new Dialog(getActivity());
            builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
            builder.setContentView(view);


            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    pbWebView.setVisibility(View.VISIBLE);
                    view.loadUrl(url);
                    return true;
                }

                @Override
                public void onPageFinished(WebView view, final String url) {
                    pbWebView.setVisibility(View.INVISIBLE);
                }
            });
            webView.loadUrl(url);
            return builder;
        }
    }

}
