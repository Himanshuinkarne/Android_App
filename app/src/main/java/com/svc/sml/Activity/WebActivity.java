package com.svc.sml.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.svc.sml.Adapter.LookaLikeProductAdapter;
import com.svc.sml.InkarneAppContext;
import com.svc.sml.R;

public class WebActivity extends Activity implements View.OnClickListener {
    private final static String LOGTAG = WebActivity.class.getName();
    private String title = "";
    private WebView webView;
    private ProgressBar pbWebView;
    private String url;
    private ImageButton ibClose;
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        createTracker();
        url = getIntent().getStringExtra(LookaLikeProductAdapter.PARAM_EXTRA_WEB_URI);
        title = getIntent().getStringExtra(LookaLikeProductAdapter.PARAM_EXTRA_WEB_TITLE);
        TextView tvTitle = (TextView) findViewById(R.id.tv_web_combo_title);
        tvTitle.setText(title);
        GATrackActivity();
        ibClose = (ImageButton) findViewById(R.id.ib_webview_back);
        ibClose.setOnClickListener(this);
        webView = (WebView) findViewById(R.id.wb_combo_buy);
        pbWebView = (ProgressBar) findViewById(R.id.pb_webview_buy);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setDomStorageEnabled(true);


//        webView.setWebChromeClient(new WebChromeClient() {
//            @Override
//            public void onProgressChanged(WebView view, int progress) {
//                if (progress == 100) {
//                    //progressBar.setVisibility(View.INVISIBLE);
//                    //progressBar.setProgress(0);
//                } else {
//                    //progressBar.setVisibility(View.VISIBLE);
//                    //progressBar.setProgress(progress);
//                }
//            }
//
//            @Override
//            public void onReceivedTitle(WebView view, String title) {
//                //titleView.setText(title);
//            }
//
//        });

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                pbWebView.setVisibility(View.VISIBLE);
                view.loadUrl(url);

                return true;
            }

            @Override
            public void onPageFinished(WebView view, final String url) {
                //progDailog.dismiss();
                pbWebView.setVisibility(View.INVISIBLE);
            }
        });

        //webView.loadUrl("http://www.teluguoneradio.com/rssHostDescr.php?hostId=147");
        webView.loadUrl(url);
//        webView.setWebViewClient(new WebViewClient() {
//
//            public void onPageFinished(WebView view, String url) {
//                // do your stuff here
//                pbWebView.setVisibility(View.INVISIBLE);
//            }
//
    }


    protected  void createTracker(){
        InkarneAppContext application = (InkarneAppContext)getApplication();
        mTracker = application.getTracker();
    }


    private void GATrackActivity(){
        String LOGTAG1 = WebActivity.class.getName();
        String LOGTAG3 = WebActivity.class.toString();
        Log.i(LOGTAG, "WebActivity.class.getName(): " + LOGTAG1);
        Log.i(LOGTAG, "WebActivity.class.toString() " + LOGTAG3);
        Log.i(LOGTAG, "logtag: " + LOGTAG);

        mTracker.setScreenName("Buy Activity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        Log.e(LOGTAG, "onCreate");
    }


    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.ib_webview_back){
            //Log.d("CheckStartActivity", "onActivityResult and resultCode = " + resultCode);
            // TODO Auto-generated method stub
            //super.onActivityResult(requestCode, resultCode, data);
            this.finish();
        }
    }

    public class webClient extends WebChromeClient{

    }
}
