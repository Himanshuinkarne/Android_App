package com.svc.sml.Activity;

import com.svc.sml.Model.BaseAccessoryItem;

/**
 * Created by himanshu on 1/19/16.
 */
public interface OnAssetDownloadListener {
        void onDownload(BaseAccessoryItem item);
        void onDownloadFailed(String comboId);
        void onDownloadProgress(String comboId,int percentage);
}




