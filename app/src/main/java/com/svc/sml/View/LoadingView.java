package com.svc.sml.View;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.svc.sml.R;

/**
 * Created by himanshu on 4/8/16.
 */
public class LoadingView extends LinearLayout {
    private TextView tvLoadingText;
    private String loadingText ="Loading ...";
    public LoadingView(Context context) {
        super(context);
        init();
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttribute(attrs);
        init();
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttribute(attrs);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttribute(attrs);
        init();
    }

    private void initAttribute(AttributeSet attrs) {
        TypedArray a=getContext().obtainStyledAttributes(
                attrs,
                R.styleable.LoadingViewAttr);

        //Use a
        Log.i("test", a.getString(
                R.styleable.LoadingViewAttr_loadingViewText));

        this.loadingText = a.getString(R.styleable.LoadingViewAttr_loadingViewText);
        //Don't forget this
        a.recycle();
    }

    private void init() {

         View v = inflate(getContext(), R.layout.view_loading, this);
        this.tvLoadingText = (TextView)findViewById(R.id.tv_loading_text);
        //this.tvLoadingText.setTypeface(InkarneAppContext.getInkarneTypeFaceHeaderJennaSue());
        this.tvLoadingText.setText(loadingText);
    }

    public void setLoadingText(String text){
        loadingText = text;
        this.tvLoadingText.setText(text);
    }

    public void setBG(int resId){
      setBackgroundResource(resId);
    }
    public String getLoadingText(){
        //loadingText
        return  this.tvLoadingText.getText().toString();
    }
}
