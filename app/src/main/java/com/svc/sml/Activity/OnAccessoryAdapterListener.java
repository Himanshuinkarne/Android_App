package com.svc.sml.Activity;

import com.svc.sml.Model.BaseAccessoryItem;

/**
 * Created by himanshu on 1/19/16.
 */
public interface OnAccessoryAdapterListener {

        void onAccessorySelected(BaseAccessoryItem item);
        void onAccessoryClicked(BaseAccessoryItem item);
}


