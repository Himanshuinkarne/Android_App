package com.svc.sml.Database;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by himanshu on 1/6/16.
 */
public class SkuData implements Serializable {
    private String mSKU_ID;
    private String parentID;
    private  Integer order;

    private String mA_Category;

    private String mA_Obj_Key_Name;
    private String mA_PIC_Png_Key_Name;
    private String mA_Png_Key_Name;

    private String pngDownloadPath;
    public boolean isImageDownloaded;
    public int indexShortingSelection =0;
    //public ClausalSkuModel dimModel;
    private List<LAData> laDataList = new ArrayList<LAData>();
    public LAData laData;

    public boolean isSelected;

    public String getPngDownloadPath() {
        return pngDownloadPath;
    }

    public void setPngDownloadPath(String pngDownloadPath) {
        this.pngDownloadPath = pngDownloadPath;
    }

    public boolean isImageDownloaded() {
        return isImageDownloaded;
    }

    public void setImageDownloaded(boolean isImageDownloaded) {
        this.isImageDownloaded = isImageDownloaded;
    }



    public String getmA_Category() {
        return mA_Category;
    }

    public void setmA_Category(String mA_Category) {
        this.mA_Category = mA_Category;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getParentID() {
        return parentID;
    }

    public void setParentID(String parentID) {
        this.parentID = parentID;
    }

    public String getmSKU_ID() {
        return mSKU_ID;
    }

    public void setmSKU_ID(String mSKU_ID) {
        this.mSKU_ID = mSKU_ID;
    }



    public String getmA_Obj_Key_Name() {
        return mA_Obj_Key_Name;
    }

    public void setmA_Obj_Key_Name(String mA_Obj_Key_Name) {
        this.mA_Obj_Key_Name = mA_Obj_Key_Name;
    }

    public String getmA_PIC_Png_Key_Name() {
        return mA_PIC_Png_Key_Name;
    }

    public void setmA_PIC_Png_Key_Name(String mA_PIC_Png_Key_Name) {
        this.mA_PIC_Png_Key_Name = mA_PIC_Png_Key_Name;
    }

    public String getmA_Png_Key_Name() {
        return mA_Png_Key_Name;
    }

    public void setmA_Png_Key_Name(String mA_Png_Key_Name) {
        this.mA_Png_Key_Name = mA_Png_Key_Name;
    }

    public List<LAData> getLaDataList() {
        return laDataList;
    }

    public void setLaDataList(List<LAData> laDataList) {
        this.laDataList = laDataList;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public void setIsImageDownloaded(boolean isImageDownloaded) {
        this.isImageDownloaded = isImageDownloaded;
    }

    public int getIndexShortingSelection() {
        return indexShortingSelection;
    }

    public void setIndexShortingSelection(int indexShortingSelection) {
        this.indexShortingSelection = indexShortingSelection;
    }
}
