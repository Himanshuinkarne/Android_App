package com.svc.sml.Database;

import com.svc.sml.Model.BaseAccessoryItem;

/**
 * Created by himanshu on 2/10/16.
 */
public class ComboMixMatch extends ComboDataReconcile {

    /* MixMatch  */
    private BaseAccessoryItem a61;
    private BaseAccessoryItem a71;
    private BaseAccessoryItem a81;
    private BaseAccessoryItem a91;
    private BaseAccessoryItem a101;
    private BaseAccessoryItem legItem;

    private boolean isA61Removed = false;
    //private boolean isA71Removed = false;
    //private boolean isA81Removed = false;
    private boolean isA91Removed = false;
    private boolean isA101Removed = false;

    public BaseAccessoryItem getLegItem() {
        return legItem;
    }

    public void setLegItem(BaseAccessoryItem legItem) {
        this.legItem = legItem;
    }

    public void resetMixMatch(){
        a61 = null;
        a71 = null;
        a91 = null;
        a101 = null;
        isA61Removed = false;
        //isA71Removed = false;
        //isA81Removed = false;
        isA91Removed = false;
        isA101Removed = false;
    }

    public BaseAccessoryItem getA61() {
        return a61;
    }

    public void setA61(BaseAccessoryItem a61) {
        this.a61 = a61;
    }

    public BaseAccessoryItem getA71() {
        return a71;
    }

    public void setA71(BaseAccessoryItem a71) {
        this.a71 = a71;
    }

    public BaseAccessoryItem getA81() {
        return a81;
    }

    public void setA81(BaseAccessoryItem a81) {
        this.a81 = a81;
    }

    public BaseAccessoryItem getA91() {
        return a91;
    }

    public void setA91(BaseAccessoryItem a91) {
        this.a91 = a91;
    }

    public BaseAccessoryItem getA101() {
        return a101;
    }

    public void setA101(BaseAccessoryItem a101) {
        this.a101 = a101;
    }

    public boolean isA61Removed() {
        return isA61Removed;
    }

    public void setIsA61Removed(boolean isA61Removed) {
        this.isA61Removed = isA61Removed;
    }

//    public boolean isA71Removed() {
//        return isA71Removed;
//    }
//
//    public void setIsA71Removed(boolean isA71Removed) {
//        this.isA71Removed = isA71Removed;
//    }
//
//    public boolean isA81Removed() {
//        return isA81Removed;
//    }
//
//    public void setIsA81Removed(boolean isA81Removed) {
//        this.isA81Removed = isA81Removed;
//    }

    public boolean isA91Removed() {
        return isA91Removed;
    }

    public void setIsA91Removed(boolean isA91Removed) {
        this.isA91Removed = isA91Removed;
    }

    public boolean isA101Removed() {
        return isA101Removed;
    }

    public void setIsA101Removed(boolean isA101Removed) {
        this.isA101Removed = isA101Removed;
    }

    private String mSKU_ID61;
    private String mA61_Category;
    private String mA61_Obj_Key_Name;
    private String mA61_PIC_Png_Key_Name;
    private String mA61_Png_Key_Name;

    private String mSKU_ID71;
    private String mA71_Category;
    private String mA71_Obj_Key_Name;
    private String mA71_PIC_Png_Key_Name;
    private String mA71_Png_Key_Name;

    private String legAwsObjKey;
    private String legAwsTexKey;

    private String mSKU_ID81;
    private String mA81_Obj_Key_Name;
    private String mA81_PIC_Png_Key_Name;
    private String mA81_Png_Key_Name;

    private String mSKU_ID91;
    private String mA91_Category;
    private String mA91_Obj_Key_Name;
    private String mA91_PIC_Png_Key_Name;
    private String mA91_Png_Key_Name;

    private String mSKU_ID101;
    private String mA101_Category;
    private String mA101_Obj_Key_Name;
    private String mA101_PIC_Png_Key_Name;
    private String mA101_Png_Key_Name;


    public String getmSKU_ID61() {
        return mSKU_ID61;
    }

    public void setmSKU_ID61(String mSKU_ID61) {
        this.mSKU_ID61 = mSKU_ID61;
    }

    public String getmA61_Category() {
        return mA61_Category;
    }

    public void setmA61_Category(String mA61_Category) {
        this.mA61_Category = mA61_Category;
    }

    public String getmA61_Obj_Key_Name() {
        return mA61_Obj_Key_Name;
    }

    public void setmA61_Obj_Key_Name(String mA61_Obj_Key_Name) {
        this.mA61_Obj_Key_Name = mA61_Obj_Key_Name;
    }

    public String getmA61_PIC_Png_Key_Name() {
        return mA61_PIC_Png_Key_Name;
    }

    public void setmA61_PIC_Png_Key_Name(String mA61_PIC_Png_Key_Name) {
        this.mA61_PIC_Png_Key_Name = mA61_PIC_Png_Key_Name;
    }

    public String getmA61_Png_Key_Name() {
        return mA61_Png_Key_Name;
    }

    public void setmA61_Png_Key_Name(String mA61_Png_Key_Name) {
        this.mA61_Png_Key_Name = mA61_Png_Key_Name;
    }

    public String getmSKU_ID71() {
        return mSKU_ID71;
    }

    public void setmSKU_ID71(String mSKU_ID71) {
        this.mSKU_ID71 = mSKU_ID71;
    }

    public String getmA71_Category() {
        return mA71_Category;
    }

    public void setmA71_Category(String mA71_Category) {
        this.mA71_Category = mA71_Category;
    }

    public String getmA71_Obj_Key_Name() {
        return mA71_Obj_Key_Name;
    }

    public void setmA71_Obj_Key_Name(String mA71_Obj_Key_Name) {
        this.mA71_Obj_Key_Name = mA71_Obj_Key_Name;
    }

    public String getmA71_PIC_Png_Key_Name() {
        return mA71_PIC_Png_Key_Name;
    }

    public void setmA71_PIC_Png_Key_Name(String mA71_PIC_Png_Key_Name) {
        this.mA71_PIC_Png_Key_Name = mA71_PIC_Png_Key_Name;
    }

    public String getmA71_Png_Key_Name() {
        return mA71_Png_Key_Name;
    }

    public void setmA71_Png_Key_Name(String mA71_Png_Key_Name) {
        this.mA71_Png_Key_Name = mA71_Png_Key_Name;
    }

    public String getLegObjAwsKey() {
        return legAwsObjKey;
    }

    public void setLegObjAwsKey(String legAwsObjKey) {
        this.legAwsObjKey = legAwsObjKey;
    }

    public String getLegTextureAwsKey() {
        return legAwsTexKey;
    }

    public void setLegTextureAwsKey(String legAwsTexKey) {
        this.legAwsTexKey = legAwsTexKey;
    }

    public String getmSKU_ID81() {
        return mSKU_ID81;
    }

    public void setmSKU_ID81(String mSKU_ID81) {
        this.mSKU_ID81 = mSKU_ID81;
    }

    public String getmA81_Obj_Key_Name() {
        return mA81_Obj_Key_Name;
    }

    public void setmA81_Obj_Key_Name(String mA81_Obj_Key_Name) {
        this.mA81_Obj_Key_Name = mA81_Obj_Key_Name;
    }

    public String getmA81_PIC_Png_Key_Name() {
        return mA81_PIC_Png_Key_Name;
    }

    public void setmA81_PIC_Png_Key_Name(String mA81_PIC_Png_Key_Name) {
        this.mA81_PIC_Png_Key_Name = mA81_PIC_Png_Key_Name;
    }

    public String getmA81_Png_Key_Name() {
        return mA81_Png_Key_Name;
    }

    public void setmA81_Png_Key_Name(String mA81_Png_Key_Name) {
        this.mA81_Png_Key_Name = mA81_Png_Key_Name;
    }

    public String getmSKU_ID91() {
        return mSKU_ID91;
    }

    public void setmSKU_ID91(String mSKU_ID91) {
        this.mSKU_ID91 = mSKU_ID91;
    }

    public String getmA91_Category() {
        return mA91_Category;
    }

    public void setmA91_Category(String mA91_Category) {
        this.mA91_Category = mA91_Category;
    }

    public String getmA91_Obj_Key_Name() {
        return mA91_Obj_Key_Name;
    }

    public void setmA91_Obj_Key_Name(String mA91_Obj_Key_Name) {
        this.mA91_Obj_Key_Name = mA91_Obj_Key_Name;
    }

    public String getmA91_PIC_Png_Key_Name() {
        return mA91_PIC_Png_Key_Name;
    }

    public void setmA91_PIC_Png_Key_Name(String mA91_PIC_Png_Key_Name) {
        this.mA91_PIC_Png_Key_Name = mA91_PIC_Png_Key_Name;
    }

    public String getmA91_Png_Key_Name() {
        return mA91_Png_Key_Name;
    }

    public void setmA91_Png_Key_Name(String mA91_Png_Key_Name) {
        this.mA91_Png_Key_Name = mA91_Png_Key_Name;
    }

    public String getmSKU_ID101() {
        return mSKU_ID101;
    }

    public void setmSKU_ID101(String mSKU_ID101) {
        this.mSKU_ID101 = mSKU_ID101;
    }

    public String getmA101_Category() {
        return mA101_Category;
    }

    public void setmA101_Category(String mA101_Category) {
        this.mA101_Category = mA101_Category;
    }

    public String getmA101_Obj_Key_Name() {
        return mA101_Obj_Key_Name;
    }

    public void setmA101_Obj_Key_Name(String mA101_Obj_Key_Name) {
        this.mA101_Obj_Key_Name = mA101_Obj_Key_Name;
    }

    public String getmA101_PIC_Png_Key_Name() {
        return mA101_PIC_Png_Key_Name;
    }

    public void setmA101_PIC_Png_Key_Name(String mA101_PIC_Png_Key_Name) {
        this.mA101_PIC_Png_Key_Name = mA101_PIC_Png_Key_Name;
    }

    public String getmA101_Png_Key_Name() {
        return mA101_Png_Key_Name;
    }

    public void setmA101_Png_Key_Name(String mA101_Png_Key_Name) {
        this.mA101_Png_Key_Name = mA101_Png_Key_Name;
    }

}
