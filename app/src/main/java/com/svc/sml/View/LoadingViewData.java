package com.svc.sml.View;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.svc.sml.R;

/**
 * Created by himanshu on 4/8/16.
 */
public class LoadingViewData extends LinearLayout {
    private TextView tvLoadingText;
    private String loadingText ="Loading ...";
    private ProgressBar pbData;
    private Button btnCancel;
    private OnLoadingViewInteractionListener onLoadingViewInteractionListener;
    private TextView tvProgress;

    public interface OnLoadingViewInteractionListener{
        void onCancel();
    }
    public LoadingViewData(Context context) {
        super(context);
        init();
    }


    public TextView getTvLoadingText() {
        return tvLoadingText;
    }

    public void setTvLoadingText(TextView tvLoadingText) {
        this.tvLoadingText = tvLoadingText;
    }

    public ProgressBar getPbData() {
        return pbData;
    }

    public void setPbData(ProgressBar pbData) {
        this.pbData = pbData;
    }

    public OnLoadingViewInteractionListener getOnLoadingViewInteractionListener() {
        return onLoadingViewInteractionListener;
    }

    public void setOnLoadingViewInteractionListener(OnLoadingViewInteractionListener onLoadingViewInteractionListener) {
        this.onLoadingViewInteractionListener = onLoadingViewInteractionListener;
    }

    public LoadingViewData(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttribute(attrs);
        init();
    }

    public LoadingViewData(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttribute(attrs);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LoadingViewData(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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

         View v = inflate(getContext(), R.layout.view_loading_data, this);
        this.tvLoadingText = (TextView)findViewById(R.id.tv_loading_text);
        this.tvLoadingText.setText(loadingText);
        this.pbData = (ProgressBar)v.findViewById(R.id.pb_data);
        this.tvProgress =(TextView)v.findViewById(R.id.tv_progress_data);
        this.btnCancel =(Button)v.findViewById(R.id.btn_cancel_data);
        this.btnCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onLoadingViewInteractionListener != null) {
                    onLoadingViewInteractionListener.onCancel();
                }
            }
        });
        this.btnCancel.setVisibility(GONE);

    }

    public Button getBtnCancel() {
        return btnCancel;
    }

    public void setBtnCancel(Button btnCancel) {
        this.btnCancel = btnCancel;
    }

    public void setProgress(int percentage){
        if(pbData.isIndeterminate()){
            pbData.setIndeterminate(false);
        }
        pbData.setProgress(percentage);
        if(percentage != 0)
        tvProgress.setText(percentage+"%");
        else
            tvProgress.setText("");
    }

    public int getProgress(){
        if(pbData != null)
       return pbData.getProgress();
        else
            return 0;
    }
    public void incrementProgressBy(int increment){
        if(pbData != null)
            pbData.incrementProgressBy(increment);
        tvProgress.setText(pbData.getProgress()+"%");
    }
    public void setProgressIndeterminate(boolean isIndeterminate){
        pbData.setIndeterminate(isIndeterminate);
    }

    public void setLoadingText(String text){
        loadingText = text;
        this.tvLoadingText.setText(text);
    }

    public void showCancelButton(){
        this.btnCancel.setVisibility(VISIBLE);
    }
    public void hideCancelButton(){
        this.btnCancel.setVisibility(GONE);
    }
}
