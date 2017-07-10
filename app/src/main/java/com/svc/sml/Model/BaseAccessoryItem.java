package com.svc.sml.Model;

import com.svc.sml.Utility.ConstantsUtil;

import java.io.Serializable;

/**
 * Created by himanshu on 1/23/16.
 */
public class BaseAccessoryItem extends BaseItem implements Serializable{
    public BaseAccessoryItem dependentItem;
    public boolean isDownloadFailed = false;

    protected String pbID;
    protected String faceID;
    protected String categorySku;

    private long id2;
    public int itemType;
    //protected Bitmap thumbnailImage;
    public boolean isSelected = false;

    protected  TransferProgressModel transferModelObj;
    protected  TransferProgressModel transferModelTexture;
    protected  TransferProgressModel transferModelPng;

    public int countTobeDownloaded = -1;
    public boolean isPicDownloading;

    private boolean isAccRemovedInMixMatch = false;

    public BaseAccessoryItem() {

    }

    public BaseAccessoryItem(String objId, String accessoryType) {
        this.objId = objId;
        this.accessoryType = accessoryType;
    }

    public BaseAccessoryItem(String objId, String accessoryType,boolean isDownloaded) {
        this.objId = objId;
        this.accessoryType = accessoryType;
        if(!isDownloaded) {
            this.objDStatus = ConstantsUtil.EDownloadStatusType.eDownloadTobeStarted.intStatus();
            this.textureDStatus = ConstantsUtil.EDownloadStatusType.eDownloadTobeStarted.intStatus();
        }
    }

    public BaseAccessoryItem(String objAwsKey,int objDStatus, String textureAwsKey,int textureDStatus) {
        this.objAwsKey = objAwsKey;
        this.objDStatus = objDStatus;
        this.textureAwsKey = textureAwsKey;
        this.textureDStatus = textureDStatus;
    }

    public BaseAccessoryItem(String objId,String objAwsKey,int objDStatus, String textureAwsKey,int textureDStatus) {
        this.objId = objId;
        this.objAwsKey = objAwsKey;
        this.objDStatus = objDStatus;
        this.textureAwsKey = textureAwsKey;
        this.textureDStatus = textureDStatus;
    }

    public BaseAccessoryItem getDependentItem() {
        return dependentItem;
    }

    public void setDependentItem(BaseAccessoryItem dependentItem) {
        this.dependentItem = dependentItem;
    }

    public String getPbID() {
        return pbID;
    }

    public void setPbID(String pbID) {
        this.pbID = pbID;
    }

    public String getFaceID() {
        return faceID;
    }

    public void setFaceID(String faceID) {
        this.faceID = faceID;
    }

    public long getId2() {
        return id2;
    }

    public void setId2(long id2) {
        this.id2 = id2;
    }



//    public Bitmap getThumbnailImage() {
//        return thumbnailImage;
//    }
//
//    public void setThumbnailImage(Bitmap thumbnailImage) {
//        this.thumbnailImage = thumbnailImage;
//    }

    public TransferProgressModel getTransferModelTexture() {
        return transferModelTexture;
    }

    public void setTransferModelTexture(TransferProgressModel transferModel) {
        this.transferModelTexture = transferModel;
    }

    public TransferProgressModel getTransferModelObj() {
        return transferModelObj;
    }

    public void setTransferModelObj(TransferProgressModel transferModel) {
        this.transferModelObj = transferModel;
    }

//    public String getTextureDownloadFilePath() {
//        return textureDownloadFilePath;
//    }
//
//    public void setTextureDownloadFilePath(String textureDownloadFilePath) {
//        this.textureDownloadFilePath = textureDownloadFilePath;
//    }
//
//    public String getObjDownloadFilePath() {
//        return objDownloadFilePath;
//    }
//
//    public void setObjDownloadFilePath(String objDownloadFilePath) {
//        this.objDownloadFilePath = objDownloadFilePath;
//    }


//    public int getProgressCumulative() {
//        return progressCumulative;
//    }
//
//    public void setProgressCumulative(int progressCumulative) {
//        this.progressCumulative = progressCumulative;
//    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }


//    public String getThumbnailDownloadFilePath() {
//        return thumbnailDownloadFilePath;
//    }

    public TransferProgressModel getTransferModelPng() {
        return transferModelPng;
    }

    public void setTransferModelPng(TransferProgressModel transferModelPng) {
        this.transferModelPng = transferModelPng;
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

//    public void setThumbnailDownloadFilePath(String thumbnailDownloadFilePath) {
//        this.thumbnailDownloadFilePath = thumbnailDownloadFilePath;
//    }

    public String getCategorySku() {
        return categorySku;
    }

    public void setCategorySku(String categorySku) {
        this.categorySku = categorySku;
    }

    public boolean isAccRemovedInMixMatch() {
        return isAccRemovedInMixMatch;
    }

    public void setIsAccRemovedInMixMatch(boolean isAccRemovedInMixMatch) {
        this.isAccRemovedInMixMatch = isAccRemovedInMixMatch;
    }
}
