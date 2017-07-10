package com.svc.sml.Database;

import java.io.Serializable;

/**
 * Created by Inkarne on 11/30/2015.
 */
public class ComboData extends ComboMixMatch implements Serializable {

    public static final String TAG = "ComboData";
    private static final long serialVersionUID = -7406082437623008161L;

    private String legId;
    private String pbId;
    private String faceId;

    private String mA1_Obj_Key_Name;
    private String mA1_PIC_Png_Key_Name;
    private String mA1_Png_Key_Name;

    //private String mA2_Category;
//    private String mA2_Obj_File_Name;
//    private String mA2_PIC_Png_File_Name;
//    private String mA2_Png_File_Name;

    private String mA2_Obj_Key_Name;
    private String mA2_PIC_Png_Key_Name;
    private String mA2_Png_Key_Name;

    private String mA3_Obj_Key_Name;
    private String mA3_PIC_Png_Key_Name;
    private String mA3_Png_Key_Name;

    private String mA4_Obj_Key_Name;
    private String mA4_PIC_Png_Key_Name;
    private String mA4_Png_Key_Name;

    private String mA5_Category;
    private String mA5_Obj_Key_Name;
    private String mA5_PIC_Png_Key_Name;
    private String mA5_Png_Key_Name;

    private String mA6_Category;
    private String mA6_Obj_Key_Name;
    private String mA6_PIC_Png_Key_Name;
    private String mA6_Png_Key_Name;

    private String mA7_Category;
    private String mA7_Obj_Key_Name;
    private String mA7_PIC_Png_Key_Name;
    private String mA7_Png_Key_Name;

    private String mA8_Category;
    private String mA8_Obj_Key_Name;
    private String mA8_PIC_Png_Key_Name;
    private String mA8_Png_Key_Name;

    private String mA9_Category;
    private String mA9_Obj_Key_Name;
    private String mA9_PIC_Png_Key_Name;
    private String mA9_Png_Key_Name;

    private String mA10_Category;
    private String mA10_Obj_Key_Name;
    private String mA10_PIC_Png_Key_Name;
    private String mA10_Png_Key_Name;

    private String mSKU_ID1;
    private String mSKU_ID2;
    private String mSKU_ID3;
    private String mSKU_ID4;
    private String mSKU_ID5;
    private String mSKU_ID6;
    private String mSKU_ID7;
    private String mSKU_ID8;
    private String mSKU_ID9;
    private String mSKU_ID10;


    protected int textureA1DStatus =0;
    protected int objA1DStatus =0;

    protected int textureA6DStatus =0;
    protected int objA6DStatus =0;

    protected int textureA7DStatus =0;
    protected int objA7DStatus =0;
    protected int textureA8DStatus =0;

    protected int objA8DStatus =0;
    protected int textureA9DStatus =0;
    protected int objA9DStatus =0;
    protected int textureA10DStatus =0;
    protected int objA10DStatus =0;
    //public HashMap<String,Integer> hmDownloadStatus = new HashMap<>();

   //public SparseArray<Integer> sArrayDStatus = new SparseArray<>();

    //private String mStyle_Rating;

    //private Integer mCountView;
    public int countTobeDownloaded =0;
    public int countDownloaded =0;
    //public int countTobeDownloaded =0;
    public int countDownloadFailed =0;
    public boolean isComboPicDownloading = true;
    public int indexTemp =0;

   // public HashMap<Integer,String> hmDownloadTrack = new HashMap<>();


    public String getmA5_Category() {
        return mA5_Category;
    }

    public void setmA5_Category(String mA5_Category) {
        this.mA5_Category = mA5_Category;
    }



    public String getmA6_Category() {
        return mA6_Category;
    }

    public void setmA6_Category(String mA6_Category) {
        this.mA6_Category = mA6_Category;
    }


    public String getmA7_Category() {
        return mA7_Category;
    }

    public void setmA7_Category(String mA7_Category) {
        this.mA7_Category = mA7_Category;
    }



    public String getmA8_Category() {
        return mA8_Category;
    }

    public void setmA8_Category(String mA8_Category) {
        this.mA8_Category = mA8_Category;
    }




    public String getmA9_Category() {
        return mA9_Category;
    }

    public void setmA9_Category(String mA9_Category) {
        this.mA9_Category = mA9_Category;
    }



    public String getmSKU_ID1() {
        return mSKU_ID1;
    }

    public void setmSKU_ID1(String mSKU_ID1) {
        this.mSKU_ID1 = mSKU_ID1;
    }

    public String getmSKU_ID2() {
        return mSKU_ID2;
    }

    public void setmSKU_ID2(String mSKU_ID2) {
        this.mSKU_ID2 = mSKU_ID2;
    }

    public String getmSKU_ID3() {
        return mSKU_ID3;
    }

    public void setmSKU_ID3(String mSKU_ID3) {
        this.mSKU_ID3 = mSKU_ID3;
    }

    public String getmSKU_ID4() {
        return mSKU_ID4;
    }

    public void setmSKU_ID4(String mSKU_ID4) {
        this.mSKU_ID4 = mSKU_ID4;
    }

    public String getmSKU_ID5() {
        return mSKU_ID5;
    }

    public void setmSKU_ID5(String mSKU_ID5) {
        this.mSKU_ID5 = mSKU_ID5;
    }

    public String getmSKU_ID6() {
        return mSKU_ID6;
    }

    public void setmSKU_ID6(String mSKU_ID6) {
        this.mSKU_ID6 = mSKU_ID6;
    }

    public String getmSKU_ID7() {
        return mSKU_ID7;
    }

    public void setmSKU_ID7(String mSKU_ID7) {
        this.mSKU_ID7 = mSKU_ID7;
    }

    public String getmSKU_ID8() {
        return mSKU_ID8;
    }

    public void setmSKU_ID8(String mSKU_ID8) {
        this.mSKU_ID8 = mSKU_ID8;
    }

    public String getmSKU_ID9() {
        return mSKU_ID9;
    }

    public void setmSKU_ID9(String mSKU_ID9) {
        this.mSKU_ID9 = mSKU_ID9;
    }

    public String getmA1_Obj_Key_Name() {
        return mA1_Obj_Key_Name;
    }

    public void setmA1_Obj_Key_Name(String mA1_Obj_Key_Name) {
        this.mA1_Obj_Key_Name = mA1_Obj_Key_Name;
    }

    public String getmA1_PIC_Png_Key_Name() {
        return mA1_PIC_Png_Key_Name;
    }

    public void setmA1_PIC_Png_Key_Name(String mA1_PIC_Png_Key_Name) {
        this.mA1_PIC_Png_Key_Name = mA1_PIC_Png_Key_Name;
    }

    public String getmA1_Png_Key_Name() {
        return mA1_Png_Key_Name;
    }

    public void setmA1_Png_Key_Name(String mA1_Png_Key_Name) {
        this.mA1_Png_Key_Name = mA1_Png_Key_Name;
    }

    public String getmA2_Obj_Key_Name() {
        return mA2_Obj_Key_Name;
    }

    public void setmA2_Obj_Key_Name(String mA2_Obj_Key_Name) {
        this.mA2_Obj_Key_Name = mA2_Obj_Key_Name;
    }

    public String getmA2_PIC_Png_Key_Name() {
        return mA2_PIC_Png_Key_Name;
    }

    public void setmA2_PIC_Png_Key_Name(String mA2_PIC_Png_Key_Name) {
        this.mA2_PIC_Png_Key_Name = mA2_PIC_Png_Key_Name;
    }

    public String getmA2_Png_Key_Name() {
        return mA2_Png_Key_Name;
    }

    public void setmA2_Png_Key_Name(String mA2_Png_Key_Name) {
        this.mA2_Png_Key_Name = mA2_Png_Key_Name;
    }

    public String getmA3_Obj_Key_Name() {
        return mA3_Obj_Key_Name;
    }

    public void setmA3_Obj_Key_Name(String mA3_Obj_Key_Name) {
        this.mA3_Obj_Key_Name = mA3_Obj_Key_Name;
    }

    public String getmA3_PIC_Png_Key_Name() {
        return mA3_PIC_Png_Key_Name;
    }

    public void setmA3_PIC_Png_Key_Name(String mA3_PIC_Png_Key_Name) {
        this.mA3_PIC_Png_Key_Name = mA3_PIC_Png_Key_Name;
    }

    public String getmA3_Png_Key_Name() {
        return mA3_Png_Key_Name;
    }

    public void setmA3_Png_Key_Name(String mA3_Png_Key_Name) {
        this.mA3_Png_Key_Name = mA3_Png_Key_Name;
    }

    public String getmA4_Obj_Key_Name() {
        return mA4_Obj_Key_Name;
    }

    public void setmA4_Obj_Key_Name(String mA4_Obj_Key_Name) {
        this.mA4_Obj_Key_Name = mA4_Obj_Key_Name;
    }

    public String getmA4_PIC_Png_Key_Name() {
        return mA4_PIC_Png_Key_Name;
    }

    public void setmA4_PIC_Png_Key_Name(String mA4_PIC_Png_Key_Name) {
        this.mA4_PIC_Png_Key_Name = mA4_PIC_Png_Key_Name;
    }

    public String getmA4_Png_Key_Name() {
        return mA4_Png_Key_Name;
    }

    public void setmA4_Png_Key_Name(String mA4_Png_Key_Name) {
        this.mA4_Png_Key_Name = mA4_Png_Key_Name;
    }

    public String getmA5_Obj_Key_Name() {
        return mA5_Obj_Key_Name;
    }

    public void setmA5_Obj_Key_Name(String mA5_Obj_Key_Name) {
        this.mA5_Obj_Key_Name = mA5_Obj_Key_Name;
    }

    public String getmA5_PIC_Png_Key_Name() {
        return mA5_PIC_Png_Key_Name;
    }

    public void setmA5_PIC_Png_Key_Name(String mA5_PIC_Png_Key_Name) {
        this.mA5_PIC_Png_Key_Name = mA5_PIC_Png_Key_Name;
    }

    public String getmA5_Png_Key_Name() {
        return mA5_Png_Key_Name;
    }

    public void setmA5_Png_Key_Name(String mA5_Png_Key_Name) {
        this.mA5_Png_Key_Name = mA5_Png_Key_Name;
    }

    public String getmA6_Obj_Key_Name() {
        return mA6_Obj_Key_Name;
    }

    public void setmA6_Obj_Key_Name(String mA6_Obj_Key_Name) {
        this.mA6_Obj_Key_Name = mA6_Obj_Key_Name;
    }

    public String getmA6_PIC_Png_Key_Name() {
        return mA6_PIC_Png_Key_Name;
    }

    public void setmA6_PIC_Png_Key_Name(String mA6_PIC_Png_Key_Name) {
        this.mA6_PIC_Png_Key_Name = mA6_PIC_Png_Key_Name;
    }

    public String getmA6_Png_Key_Name() {
        return mA6_Png_Key_Name;
    }

    public void setmA6_Png_Key_Name(String mA6_Png_Key_Name) {
        this.mA6_Png_Key_Name = mA6_Png_Key_Name;
    }

    public String getmA7_Obj_Key_Name() {
        return mA7_Obj_Key_Name;
    }

    public void setmA7_Obj_Key_Name(String mA7_Obj_Key_Name) {
        this.mA7_Obj_Key_Name = mA7_Obj_Key_Name;
    }

    public String getmA7_PIC_Png_Key_Name() {
        return mA7_PIC_Png_Key_Name;
    }

    public void setmA7_PIC_Png_Key_Name(String mA7_PIC_Png_Key_Name) {
        this.mA7_PIC_Png_Key_Name = mA7_PIC_Png_Key_Name;
    }

    public String getmA7_Png_Key_Name() {
        return mA7_Png_Key_Name;
    }

    public void setmA7_Png_Key_Name(String mA7_Png_Key_Name) {
        this.mA7_Png_Key_Name = mA7_Png_Key_Name;
    }

    public String getmA8_Obj_Key_Name() {
        return mA8_Obj_Key_Name;
    }

    public void setmA8_Obj_Key_Name(String mA8_Obj_Key_Name) {
        this.mA8_Obj_Key_Name = mA8_Obj_Key_Name;
    }

    public String getmA8_PIC_Png_Key_Name() {
        return mA8_PIC_Png_Key_Name;
    }

    public void setmA8_PIC_Png_Key_Name(String mA8_PIC_Png_Key_Name) {
        this.mA8_PIC_Png_Key_Name = mA8_PIC_Png_Key_Name;
    }

    public String getmA8_Png_Key_Name() {
        return mA8_Png_Key_Name;
    }

    public void setmA8_Png_Key_Name(String mA8_Png_Key_Name) {
        this.mA8_Png_Key_Name = mA8_Png_Key_Name;
    }

    public String getmA9_Obj_Key_Name() {
        return mA9_Obj_Key_Name;
    }

    public void setmA9_Obj_Key_Name(String mA9_Obj_Key_Name) {
        this.mA9_Obj_Key_Name = mA9_Obj_Key_Name;
    }

    public String getmA9_PIC_Png_Key_Name() {
        return mA9_PIC_Png_Key_Name;
    }

    public void setmA9_PIC_Png_Key_Name(String mA9_PIC_Png_Key_Name) {
        this.mA9_PIC_Png_Key_Name = mA9_PIC_Png_Key_Name;
    }

    public String getmA9_Png_Key_Name() {
        return mA9_Png_Key_Name;
    }

    public void setmA9_Png_Key_Name(String mA9_Png_Key_Name) {
        this.mA9_Png_Key_Name = mA9_Png_Key_Name;
    }

    public String getmA10_Obj_Key_Name() {
        return mA10_Obj_Key_Name;
    }

    public void setmA10_Obj_Key_Name(String mA10_Obj_Key_Name) {
        this.mA10_Obj_Key_Name = mA10_Obj_Key_Name;
    }

    public String getmA10_PIC_Png_Key_Name() {
        return mA10_PIC_Png_Key_Name;
    }

    public void setmA10_PIC_Png_Key_Name(String mA10_PIC_Png_Key_Name) {
        this.mA10_PIC_Png_Key_Name = mA10_PIC_Png_Key_Name;
    }

    public String getmA10_Png_Key_Name() {
        return mA10_Png_Key_Name;
    }

    public void setmA10_Png_Key_Name(String mA10_Png_Key_Name) {
        this.mA10_Png_Key_Name = mA10_Png_Key_Name;
    }

    public String getmA10_Category() {
        return mA10_Category;
    }

    public void setmA10_Category(String mA10_Category) {
        this.mA10_Category = mA10_Category;
    }
    

    public String getmSKU_ID10() {
        return mSKU_ID10;
    }

    public void setmSKU_ID10(String mSKU_ID10) {
        this.mSKU_ID10 = mSKU_ID10;
    }

//    private Bitmap comboBitmap;
//    public Bitmap getComboBitmap() {
//        return comboBitmap;
//    }
//
//    public void setComboBitmap(Bitmap comboBitmap) {
//        this.comboBitmap = comboBitmap;
//    }

    public String getLegId() {
        return legId;
    }

    public void setLegId(String legId) {
        this.legId = legId;
    }

    public String getPbId() {
        return pbId;
    }

    public void setPbId(String pbId) {
        this.pbId = pbId;
    }

    public String getFaceId() {
        return faceId;
    }

    public void setFaceId(String faceId) {
        this.faceId = faceId;
    }

    public int getTextureA1DStatus() {
        return textureA1DStatus;
    }

    public void setTextureA1DStatus(int textureA1DStatus) {
        this.textureA1DStatus = textureA1DStatus;
    }

    public int getObjA1DStatus() {
        return objA1DStatus;
    }

    public void setObjA1DStatus(int objA1DStatus) {
        this.objA1DStatus = objA1DStatus;
    }

    public int getTextureA6DStatus() {
        return textureA6DStatus;
    }

    public void setTextureA6DStatus(int textureA6DStatus) {
        this.textureA6DStatus = textureA6DStatus;
    }

    public int getObjA6DStatus() {
        return objA6DStatus;
    }

    public void setObjA6DStatus(int objA6DStatus) {
        this.objA6DStatus = objA6DStatus;
    }

    public int getTextureA7DStatus() {
        return textureA7DStatus;
    }

    public void setTextureA7DStatus(int textureA7DStatus) {
        this.textureA7DStatus = textureA7DStatus;
    }

    public int getObjA7DStatus() {
        return objA7DStatus;
    }

    public void setObjA7DStatus(int objA7DStatus) {
        this.objA7DStatus = objA7DStatus;
    }

    public int getTextureA8DStatus() {
        return textureA8DStatus;
    }

    public void setTextureA8DStatus(int textureA8DStatus) {
        this.textureA8DStatus = textureA8DStatus;
    }

    public int getObjA8DStatus() {
        return objA8DStatus;
    }

    public void setObjA8DStatus(int objA8DStatus) {
        this.objA8DStatus = objA8DStatus;
    }

    public int getTextureA9DStatus() {
        return textureA9DStatus;
    }

    public void setTextureA9DStatus(int textureA9DStatus) {
        this.textureA9DStatus = textureA9DStatus;
    }

    public int getObjA9DStatus() {
        return objA9DStatus;
    }

    public void setObjA9DStatus(int objA9DStatus) {
        this.objA9DStatus = objA9DStatus;
    }

    public int getTextureA10DStatus() {
        return textureA10DStatus;
    }

    public void setTextureA10DStatus(int textureA10DStatus) {
        this.textureA10DStatus = textureA10DStatus;
    }

    public int getObjA10DStatus() {
        return objA10DStatus;
    }

    public void setObjA10DStatus(int objA10DStatus) {
        this.objA10DStatus = objA10DStatus;
    }
}

