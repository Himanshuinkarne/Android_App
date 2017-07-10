package com.svc.sml.Model;

import android.util.SparseArray;

import com.svc.sml.Database.ComboData;

import java.io.Serializable;

/**
 * Created by himanshu on 8/30/16.
 */
public class ComboDataDownloadItem implements Serializable {
    ComboData comboData;
    public SparseArray<Integer> sArrayDStatus = new SparseArray<>();
    public SparseArray<Integer> sArrayError = new SparseArray<>();
    public boolean isRendered = false;

    public ComboDataDownloadItem(ComboData comboData) {
        this.comboData = comboData;
        this.sArrayDStatus = new SparseArray<>();
        this.sArrayError = new SparseArray<>();
        this.isRendered = false;
    }
    public ComboDataDownloadItem() {

    }

    public ComboData getComboData() {
        return comboData;
    }

    public void setComboData(ComboData comboData) {
        this.comboData = comboData;
    }

    public SparseArray<Integer> getsArrayDStatus() {
        return sArrayDStatus;
    }

    public void setsArrayDStatus(SparseArray<Integer> sArrayDStatus) {
        this.sArrayDStatus = sArrayDStatus;
    }
}
