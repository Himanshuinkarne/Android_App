package com.svc.sml.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.svc.sml.R;

/**
 * Created by himanshu on 7/6/16.
 */
public class TermsDialogFragment extends DialogFragment  {
    public ProgressBar pbWebView;
    public WebView webView;
    private CheckBox checkBox;
    private TextView tvTitle;
    private ImageButton btnBack;
    private String url = "http://stylemylooks.s3-website-ap-southeast-1.amazonaws.com/termsofuse.htm";

    public TermsDialogFragment() {

    }

    public static TermsDialogFragment newInstance(int num) {
        TermsDialogFragment dialogFragment = new TermsDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("num", num);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    public void goBackIfCan() {
        if (webView.canGoBack()) {
            webView.goBack();
        }
    }

    public void onResume(){
        super.onResume();
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                // pretend we've processed it
// pass on to be processed as normal
                return keyCode == KeyEvent.KEYCODE_SEARCH;
            }
        });
    }

//    @Override
//    public void onCancel(DialogInterface dialog) {
//        //super.onCancel(dialog);
//        if (webView.canGoBack()) {
//            webView.goBack();
//        } else {
//            super.onCancel(dialog);
//        }
//    }




    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dailog_reg_terms_condition, new LinearLayout(getActivity()), false);
        pbWebView = (ProgressBar) view.findViewById(R.id.pb_web);
        tvTitle = (TextView) view.findViewById(R.id.tv_header_title_terms_condition);

        //textview.setPaintFlags(textview.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        btnBack = (ImageButton) view.findViewById(R.id.btn_back_terms_condition);
        webView = (WebView) view.findViewById(R.id.wb_terms_condition);
//        checkBox = (CheckBox) view.findViewById(R.id.cb_reg_terms_condition);
//        checkBox.setOnCheckedChangeListener(
//                new CompoundButton.OnCheckedChangeListener() {
//                    @Override
//                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                        dismiss();
//                        //loginServiceCall();
//                    }
//                }
//        );

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

//    @Override
//    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            if (webView.canGoBack()) {
//                webView.goBack();
//                return false;
//            } else {
//                //super.onCancel(dialog);
//                return true;
//            }
//        }
//        return false;
//    }
}
