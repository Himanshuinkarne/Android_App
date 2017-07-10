package com.svc.sml.Model;

/**
 * Created by himanshu on 12/27/15.
 */
public class HairItem extends BaseAccessoryItem {

    public  String content;
    public  String details;

    public boolean errorThumbnailDownload;
    public boolean errorObjDownload;

    public HairItem(String id, String content, String details) {
        this.objId = id;
        this.content = content;
        this.details = details;
        this.errorThumbnailDownload = false;
        this.errorObjDownload = false;
    }

    public HairItem() {
       this.errorThumbnailDownload = false;
        this.errorObjDownload = false;
    }

    @Override
    public String toString() {
        return content;
    }


}
