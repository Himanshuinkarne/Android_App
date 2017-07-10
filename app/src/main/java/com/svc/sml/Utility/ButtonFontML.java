package com.svc.sml.Utility;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.Button;

import com.svc.sml.InkarneAppContext;

/**
 * Created by himanshu on 4/26/16.
 */
public class ButtonFontML extends Button {
    public ButtonFontML(Context context) {
        super(context);
        setType(context);
    }

    public ButtonFontML(Context context, AttributeSet attrs) {
        super(context, attrs);
        setType(context);
    }

    public ButtonFontML(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setType(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ButtonFontML(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setType(context);
    }

    private void setType(Context context){
        //this.setTypeface(InkarneAppContext.getInkarneTypeFaceHeaderJennaSue());
        this.setTypeface(InkarneAppContext.getInkarneTypeFaceML());
        //this.setShadowLayer(1.5f, 5, 5, getContext().getResources().getColor(R.color.hint_text_color));
    }
}
