package com.svc.sml.Model;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by himanshu on 12/24/15.
 */
public class FaceItem implements Serializable {

    public static final String EXTRA_PARAM_FACE_OBJ = "EXTRA_PARAM_FACE_OBJ";

    static  final public String reskeyFacePng =   "Face_Png_S3Key";
    static  final public String reskeyFaceObj =   "Face_Obj_S3Key";
    static  final public String reskeyHairObj =   "HS_Obj_S3Key";
    static  final public String reskeyHairPng =   "HS_Png_S3Key";
    static  final public String reskeyFaceId =   "Face_ID";
    static  final public String reskeyHairId =   "Hairstyle_ID";

    static  final public String reskeySpecsId =   "Specs_ID";
    static  final public String reskeySpecsObj =   "Specs_Png_S3Key";
    static  final public String reskeySpecsPng =   "Specs_Obj_S3Key";

    private int rowId;

    private  String faceId;

    private String facePngkey;
    private String faceObjkey;
    private int textureFaceDStatus =0;
    private int objFaceDStatus =0;

    private String hairstyleId ;
    private String hairPngKey;
    private String hairObjkey;
    private int textureHairDStatus =0;
    private int objHairDStatus =0;

    private String specsId;
    private String specsPngkey;
    private String specsObjkey;

    private String pbId;
    private String bodyPngkey;
    private String bodyObjkey;
    private int textureBodyDStatus =0;
    private int objBodyDStatus =0;

    private String defaultLegId;
    private String legPngkey;
    private String legObjkey;

    private String feducialPoints = "";
    private String imageFilePath = "";
    private String imageResizeRatio = "1";


    private int isComplete =0;

    private String errorCode;

    public FaceItem(){

    }

    public HashMap<String,TransferProgressModel> mapDownload = new HashMap<>();

    public int downloadedCount =0;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }


    public String getFaceId() {
        return faceId;
    }

    public void setFaceId(String faceId) {
        this.faceId = faceId;
    }

    public String getHairstyleId() {
        return hairstyleId;
    }

    public void setHairstyleId(String hairstyleId) {
        this.hairstyleId = hairstyleId;
    }

    public String getHairPngKey() {
        return hairPngKey;
    }

    public void setHairPngKey(String hairPngKey) {
        this.hairPngKey = hairPngKey;
    }

    public String getFaceObjkey() {
        return faceObjkey;
    }

    public void setFaceObjkey(String faceObjkey) {
        this.faceObjkey = faceObjkey;
    }

    public String getFacePngkey() {
        return facePngkey;
    }

    public void setFacePngkey(String facePngkey) {
        this.facePngkey = facePngkey;
    }

    public String getHairObjkey() {
        return hairObjkey;
    }

    public void setHairObjkey(String hairObjkey) {
        this.hairObjkey = hairObjkey;
    }

    public String getSpecsPngkey() {
        return specsPngkey;
    }

    public void setSpecsPngkey(String specsPngkey) {
        this.specsPngkey = specsPngkey;
    }

    public String getSpecsId() {
        return specsId;
    }

    public void setSpecsId(String specsId) {
        this.specsId = specsId;
    }

    public String getSpecsObjkey() {
        return specsObjkey;
    }

    public void setSpecsObjkey(String specsObjkey) {
        this.specsObjkey = specsObjkey;
    }

    public String getBodyPngkey() {
        return bodyPngkey;
    }

    public void setBodyPngkey(String bodyPngkey) {
        this.bodyPngkey = bodyPngkey;
    }

    public String getPbId() {
        return pbId;
    }

    public void setPbId(String pbId) {
        this.pbId = pbId;
    }

    public String getBodyObjkey() {
        return bodyObjkey;
    }

    public void setBodyObjkey(String bodyObjkey) {
        this.bodyObjkey = bodyObjkey;
    }

    public String getDefaultLegId() {
        return defaultLegId;
    }

    public void setDefaultLegId(String defaultLegId) {
        this.defaultLegId = defaultLegId;
    }

    public String getLegPngkey() {
        return legPngkey;
    }

    public void setLegPngkey(String legPngkey) {
        this.legPngkey = legPngkey;
    }

    public String getLegObjkey() {
        return legObjkey;
    }

    public void setLegObjkey(String legObjkey) {
        this.legObjkey = legObjkey;
    }

    public int getRowId() {
        return rowId;
    }

    public void setRowId(int rowId) {
        this.rowId = rowId;
    }

    public int getIsComplete() {
        return isComplete;
    }

    public void setIsComplete(int isComplete) {
        this.isComplete = isComplete;
    }

    public int getTextureFaceDStatus() {
        return textureFaceDStatus;
    }

    public void setTextureFaceDStatus(int textureFaceDStatus) {
        this.textureFaceDStatus = textureFaceDStatus;
    }

    public int getObjFaceDStatus() {
        return objFaceDStatus;
    }

    public void setObjFaceDStatus(int objFaceDStatus) {
        this.objFaceDStatus = objFaceDStatus;
    }

    public int getObjHairDStatus() {
        return objHairDStatus;
    }

    public void setObjHairDStatus(int objHairDStatus) {
        this.objHairDStatus = objHairDStatus;
    }

    public int getTextureHairDStatus() {
        return textureHairDStatus;
    }

    public void setTextureHairDStatus(int textureHairDStatus) {
        this.textureHairDStatus = textureHairDStatus;
    }

    public int getTextureBodyDStatus() {
        return textureBodyDStatus;
    }

    public void setTextureBodyDStatus(int textureBodyDStatus) {
        this.textureBodyDStatus = textureBodyDStatus;
    }

    public int getObjBodyDStatus() {
        return objBodyDStatus;
    }

    public void setObjBodyDStatus(int objBodyDStatus) {
        this.objBodyDStatus = objBodyDStatus;
    }

    public String getFeducialPoints() {
        return feducialPoints;
    }

    public void setFeducialPoints(String feducialPoints) {
        this.feducialPoints = feducialPoints;
    }

    public String getImageSavedFilePath() {
        return imageFilePath;
    }

    public void setImageSavedFilePath(String imageFilePath) {
        this.imageFilePath = imageFilePath;
    }

    public String getImageResizeRatio() {
        return imageResizeRatio;
    }

    public void setImageResizeRatio(String imageResizeRatio) {
        this.imageResizeRatio = imageResizeRatio;
    }
}
