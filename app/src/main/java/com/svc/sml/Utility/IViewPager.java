package com.svc.sml.Utility;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by himanshu on 9/7/16.
 */
public class IViewPager extends ViewPager {
    public IViewPager(Context context) {
        super(context);
    }

    public IViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if (v instanceof HorizontalListView) {
            return true;
        }
        return super.canScroll(v, checkV, dx, x, y);
    }
}
