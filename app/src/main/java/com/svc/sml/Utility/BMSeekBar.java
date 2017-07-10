package com.svc.sml.Utility;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.SeekBar;

import com.svc.sml.InkarneAppContext;
import com.svc.sml.R;

/**
 * Created by himanshu on 23/01/16.
 */
public class BMSeekBar extends SeekBar {

    private String progressLeftTextString ="";
    private int progressLeftTextColor;
    private float progressLeftTextSize;
    private float progressLeftTextPadding;

    private String progressRightTextString ="";
    private int progressRightTextColor;
    private float progressRightTextSize;
    private float progressRightTextPadding;

  //  private String progressThumbTextString ="";
    private int progressThumbTextColor;
    private float progressThumbTextSize;
    private float progressThumbTextPadding;
    private Paint pThumb = null;
    private Paint pRightString  = null;
    private Paint pLeftString  = null;
    //private Bitmap bitmapThumb = null;
    private BitmapDrawable bitmapSeeBar = null;
    private Bitmap bitmapThumbBg = null;
    private BitmapDrawable bitmapDrawableThumb = null;

//    public BMSeekBar(Context context) {
//        super(context);
//    }

    public BMSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttribute(attrs);
    }

    public BMSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttribute(attrs);
    }

    public void setBarBackgroundText(String left, String right) {
        this.progressLeftTextString = left;
        this.progressRightTextString = right;
    }
    //public BMSeekBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    //    super(context, attrs, defStyleAttr, defStyleRes);
    //}

    private void initAttribute(AttributeSet attrs) {

        TypedArray a=getContext().obtainStyledAttributes(
                attrs,
                R.styleable.BMSeekBarAttr);

        //Use a
        Log.i("test", a.getString(
                R.styleable.BMSeekBarAttr_progressLeftText));

        this.setMax(ConstantsFunctional.MAX_SIZE_BODY_MEASUREMENT);
        Bitmap bitmapThumb  = BitmapFactory.decodeResource(getResources(), R.drawable.seek_bar_thumb_bm);
        bitmapDrawableThumb = new BitmapDrawable(getResources(), bitmapThumb);
        //bitmapThumbBg = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        bitmapThumbBg = Bitmap.createBitmap(bitmapThumb.getWidth(), bitmapThumb.getHeight(), Bitmap.Config.ARGB_8888);
        this.progressLeftTextString = a.getString(R.styleable.BMSeekBarAttr_progressLeftText);
        this.progressLeftTextColor = a.getColor(R.styleable.BMSeekBarAttr_progressLeftTextColor, Color.WHITE);
        this.progressLeftTextSize = a.getDimension(R.styleable.BMSeekBarAttr_progressLeftTextSize, 60);
        this.progressLeftTextPadding = a.getDimension(R.styleable.BMSeekBarAttr_progressLeftTextPadding, 50);

        this.progressRightTextString = a.getString(R.styleable.BMSeekBarAttr_progressRightText);
        this.progressRightTextColor = a.getColor(R.styleable.BMSeekBarAttr_progressRightTextColor, Color.WHITE);
        this.progressRightTextSize = a.getDimension(R.styleable.BMSeekBarAttr_progressRightTextSize, 50);
        this.progressRightTextPadding = a.getDimension(R.styleable.BMSeekBarAttr_progressRightTextPadding, 200);

        //this.progressThumbTextString = a.getString(R.styleable.BMSeekBarAttr_progressThumbText);
        this.progressThumbTextColor = a.getColor(R.styleable.BMSeekBarAttr_progressThumbTextColor, Color.WHITE);
        this.progressThumbTextSize = a.getDimension(R.styleable.BMSeekBarAttr_progressThumbTextSize,50);
        this.progressThumbTextPadding = a.getDimension(R.styleable.BMSeekBarAttr_progressThumbTextPadding,50);

        //Don't forget this=
        Bitmap bitmapSeekBar  = BitmapFactory.decodeResource(getResources(), R.drawable.styled_progress_bm);
        this.bitmapSeeBar = new BitmapDrawable(getResources(), bitmapSeekBar);
        setBackgroundDrawable(bitmapSeeBar);
        a.recycle();

    }


    private void initBarPaint() {

    }


    @Override
    protected synchronized void onDraw(Canvas c) {
        // First draw the regular progress bar, then custom draw our text
        super.onDraw(c);
        int width;
        int yPos;
        if(pLeftString==null) {
            pLeftString = new Paint();
            //pLeftString.setTypeface(Typeface.DEFAULT);
            pLeftString.setTypeface(InkarneAppContext.getInkarneTypeFaceML());
            pLeftString.setTextSize(progressLeftTextSize);
            pLeftString.setColor(progressLeftTextColor);
        }
        //seting left text
        //p.setTypeface(Typeface.DEFAULT_BOLD);//mychange

        width = (int) pLeftString.measureText(progressLeftTextString);
        yPos = (int) ((c.getHeight() / 2) - ((pLeftString.descent() + pLeftString .ascent()) / 2) );
        // int yPos = 0;//(int) (c.getHeight());
        c.drawText(progressLeftTextString, progressLeftTextPadding, yPos, pLeftString);

        //setring right text
        if(pRightString == null){
            pRightString = new Paint();
           // pRightString.setTypeface(Typeface.DEFAULT);
            pRightString.setTypeface(InkarneAppContext.getInkarneTypeFaceML());
            pRightString.setTextSize(progressRightTextSize);
            pRightString.setColor(progressRightTextColor);
        }
        c.drawText(progressRightTextString, c.getWidth()-progressRightTextPadding, yPos, pRightString);
        drawThumb();

        setBackgroundDrawable(bitmapSeeBar);
        //this.setThumb(new BitmapDrawable(getResources(), bmp));
        //   bustSeekBar.setBackground(new BitmapDrawable(getResources(),bmp));
     //   this.setProgressDrawable(new BitmapDrawable(getResources(), bmp));
//        final int ccount = c.save();
//        c.translate(100, 200);
//        c.restoreToCount(ccount);
    }

    BitmapDrawable bitmapDrawableBgWithText;
    private void drawThumb() {
       // Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.seek_bar_thumb_bm);
        //Bitmap bmp = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        bitmapThumbBg.eraseColor(Color.TRANSPARENT);
        Canvas c = new Canvas(bitmapThumbBg);

        String text = Integer.toString(this.getProgress()+ConstantsFunctional.MIN_SIZE_BODY_MEASUREMENT);
        if(pThumb == null)
        {
            pThumb = new Paint();
            pThumb.setTextSize(progressThumbTextSize);
            pThumb.setColor(progressThumbTextColor);
        }
        int width = (int) pThumb.measureText(text);
        //int yPos = (int) ((c.getHeight() / 2) - ((p.descent() + p .ascent()) / 2) );
        int yPos = (int)progressThumbTextPadding;//0;//(int) (c.getHeight());
        c.drawText(text, (bitmapThumbBg.getWidth() - width) / 2, yPos, pThumb);
//        this.setThumb(new BitmapDrawable(getResources(), bitmapThumbBg));


//        canvasBitmap.eraseColor(Color.TRANSPARENT);
//        Canvas uca = new Canvas(canvasBitmap);
//        setDefaultPaint();
//        for (k = 0; k < FedNo; k++) {
//            if (!isFedPointIndexToShow(k)) {
//                continue;
//            }
//            //drawCrossLine1(uca, uwidth[k], uheight[k], lengthCrossLine);
//            drawFeaturedImage(uca, uwidth[k], uheight[k], k);
//        }
        bitmapDrawableBgWithText = new BitmapDrawable(getResources(), bitmapThumbBg);
        Drawable[] layers = new Drawable[2];
        //bitmapDrawableThumb = new BitmapDrawable(getResources(), bitmapThumb);
        layers[0] = bitmapDrawableThumb;
        layers[1] = bitmapDrawableBgWithText;
        LayerDrawable u_ldr = new LayerDrawable(layers);
        this.setThumb(u_ldr);
        //u_ldr.invalidateSelf();
    }
}
