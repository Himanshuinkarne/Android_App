package com.svc.sml.Model;

import java.io.Serializable;

/**
 * Created by himanshu on 1/28/16.
 */
public class BaseItem implements Serializable {

    static  final public String keyNamePng =   "Pic_Key_Name";
    static  final public String keyNameObj =   "Obj_Key_Name";
    static  final public String keyNameTexture =   "Texture_Key_Name";
    static  final public String keyNameRank =   "Forced_Rank";
    static  final public String keyNameID =   "Accessory_ID";
    static  final public String keyNameObjId2 ="Leg_ID";

    protected String objId;
    protected String thumbnailAwsKey;
    protected String textureAwsKey;
    protected String objAwsKey;
    protected int textureDStatus =0;
    protected int objDStatus =0;
    protected int rank;
    protected long updatedDate;

    protected String accessoryType;

    protected String objId2;
//    protected String textureAwsKey2;
//    protected String objAwsKey2;
    protected String accessoryType2;

    private long dateReconcile;



    public BaseItem(String objAwsKey, String textureAwsKey) {
        this.objAwsKey = objAwsKey;
        this.textureAwsKey = textureAwsKey;
    }

    public BaseItem(String objAwsKey,int objDStatus, String textureAwsKey,int textureDStatus) {
        this.objAwsKey = objAwsKey;
        this.objDStatus = objDStatus;
        this.textureAwsKey = textureAwsKey;
        this.textureDStatus = textureDStatus;
    }


    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public BaseItem() {

    }

    public String getObjId() {
        return objId;
    }

    public void setObjId(String objId) {
        this.objId = objId;
    }


    public String getTextureAwsKey() {
        return textureAwsKey;
    }

    public void setTextureAwsKey(String textureAwsKey) {
        this.textureAwsKey = textureAwsKey;
    }

    public String getObjAwsKey() {
        return objAwsKey;
    }

    public void setObjAwsKey(String objAwsKey) {
        this.objAwsKey = objAwsKey;
    }

    public String getThumbnailAwsKey() {
        return thumbnailAwsKey;
    }

    public void setThumbnailAwsKey(String thumbnailAwsKey) {
        this.thumbnailAwsKey = thumbnailAwsKey;
    }

    public String getAccessoryType() {
        return accessoryType;
    }

    public void setAccessoryType(String accessoryType) {
        this.accessoryType = accessoryType;
    }

    public int getTextureDStatus() {
        return textureDStatus;
    }

    public void setTextureDStatus(int textureDStatus) {
        this.textureDStatus = textureDStatus;
    }

    public int getObjDStatus() {
        return objDStatus;
    }

    public void setObjDStatus(int objDStatus) {
        this.objDStatus = objDStatus;
    }

//    public String getObjType() {
//        return objType;
//    }
//
//    public void setObjType(String objType) {
//        this.objType = objType;
//    }


    public long getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(long lastUpdatedDate) {
        this.updatedDate = lastUpdatedDate;
    }

    public String getObjId2() {
        return objId2;
    }

    public void setObjId2(String objId2) {
        this.objId2 = objId2;
    }

//    public String getTextureAwsKey2() {
//        return textureAwsKey2;
//    }
//
//    public void setTextureAwsKey2(String textureAwsKey2) {
//        this.textureAwsKey2 = textureAwsKey2;
//    }
//
//    public String getObjAwsKey2() {
//        return objAwsKey2;
//    }
//
//    public void setObjAwsKey2(String objAwsKey2) {
//        this.objAwsKey2 = objAwsKey2;
//    }

    public String getAccessoryType2() {
        return accessoryType2;
    }

    public void setAccessoryType2(String accessoryType2) {
        this.accessoryType2 = accessoryType2;
    }

    public long getDateReconcile() {
        return dateReconcile;
    }

    public void setDateReconcile(long dateReconcile) {
        this.dateReconcile = dateReconcile;
    }
}
