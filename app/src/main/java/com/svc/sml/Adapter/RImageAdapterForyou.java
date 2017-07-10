package com.svc.sml.Adapter;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;

import com.svc.sml.Utility.AbstractCoverFlowImageAdapter2;

/**
 * This adapter provides reflected images from linked adapter.
 * 
 * @author potiuk
 * 
 */
public class RImageAdapterForyou extends AbstractCoverFlowImageAdapter2 {

    /** The linked adapter. */
    private final AbstractCoverFlowImageAdapter2 linkedAdapter;
    /**
     * Gap between the image and its reflection.
     */
    private float reflectionGap;

    /** The image reflection ratio. */
    private float imageReflectionRatio;

    /**
     * Sets the width ratio.
     *
     * @param imageReflectionRatio
     *            the new width ratio
     */
    public void setWidthRatio(final float imageReflectionRatio) {
        this.imageReflectionRatio = imageReflectionRatio;
    }

    /**
     * Creates reflecting adapter.
     *
     * @param linkedAdapter
     *            adapter that provides images to get reflections
     */
    public RImageAdapterForyou(final AbstractCoverFlowImageAdapter2 linkedAdapter) {
        super();
        this.linkedAdapter = linkedAdapter;
        this.context = linkedAdapter.context;
        this.inflater = linkedAdapter.inflater;
        this.comboList = linkedAdapter.comboList;
        this.looksCategoryTitle = linkedAdapter.looksCategoryTitle;
        this.transferUtility = linkedAdapter.transferUtility;
        this.imageFetcher = linkedAdapter.imageFetcher;
    }

    /**
     * Sets the reflection gap.
     * 
     * @param reflectionGap
     *            the new reflection gap
     */
    public void setReflectionGap(final float reflectionGap) {
        this.reflectionGap = reflectionGap;
    }

    /**
     * Gets the reflection gap.
     * 
     * @return the reflection gap
     */
    public float getReflectionGap() {
        return reflectionGap;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.svc.inkarne.Utility.AbstractCoverFlowImageAdapter#createBitmap(int)
     */
    @Override
    protected Bitmap createBitmap(final int position) {
        return createReflectedImages(linkedAdapter.getItem(position));
    }

    @Override
    protected Bitmap createBitmap(Bitmap bitmap) {
        return createReflectedImages(bitmap);
    }

    /**
     * Creates the reflected images.
     * 
     * @param originalImage
     *            the original image
     * @return true, if successful
     */
    public Bitmap createReflectedImages(final Bitmap originalImage) {
        final int width = originalImage.getWidth();
        final int height = originalImage.getHeight();
        final Matrix matrix = new Matrix();
        matrix.preScale(1, -1);
        final Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, (int) (height * imageReflectionRatio),
                width, (int) (height - height * imageReflectionRatio), matrix, false);
        final Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (int) (height + height * imageReflectionRatio),
                Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmapWithReflection);
        canvas.drawBitmap(originalImage, 0, 0, null);
        final Paint deafaultPaint = new Paint();
        deafaultPaint.setColor(Color.TRANSPARENT);
        canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);
        final Paint paint = new Paint();
        final LinearGradient shader = new LinearGradient(0, originalImage.getHeight(), 0,
                bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.CLAMP);
        paint.setShader(shader);
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        canvas.drawRect(0, height, width, bitmapWithReflection.getHeight() + reflectionGap, paint);
        return bitmapWithReflection;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        return linkedAdapter.getCount();
    }

}