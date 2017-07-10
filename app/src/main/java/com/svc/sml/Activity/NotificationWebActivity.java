package com.svc.sml.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.svc.sml.R;

public class NotificationWebActivity extends Activity {
    public static final String LOGTAG = "NotificationWebActivity";
    public ProgressBar pbWebView;
    public WebView webView;
    private LinearLayout conBtnGotIt;
    public String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_web);
        url = getIntent().getStringExtra("url");
        pbWebView = (ProgressBar) findViewById(R.id.pb_web);
        webView = (WebView) findViewById(R.id.wb_notification);
        conBtnGotIt = (LinearLayout) findViewById(R.id.con_btn_notification);
        conBtnGotIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivity();
                finish();
            }
        });

        //webView.loadUrl("file:///android_asset/terms_condition.html");
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

    }

    public void goBackIfCan() {
        if (webView.canGoBack()) {
            webView.goBack();
        }
    }

    public void onResume() {
        super.onResume();
    }

    private void openMainActivity() {
        Log.e(LOGTAG, " ******** Launch DataActivity in FirstActivity  *******");
        Intent intent = new Intent(NotificationWebActivity.this, DataActivity.class);
        startActivity(intent);
    }
}