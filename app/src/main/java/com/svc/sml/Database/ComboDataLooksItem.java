package com.svc.sml.Database;

import java.util.ArrayList;

/**
 * Created by himanshu on 3/3/16.
 */
public class ComboDataLooksItem {
    ArrayList<ComboData> comboList;
    String comboStyleCategory;
    String looksLabelName;

    public ComboDataLooksItem(ArrayList<ComboData> comboList) {
        this.comboList = comboList;
    }

    public ComboDataLooksItem() {
        if(comboList== null)
            comboList = new ArrayList<ComboData>();
    }

    public ComboDataLooksItem(String looksLabelName) {
        this.looksLabelName = looksLabelName;
        if(comboList== null)
            comboList = new ArrayList<ComboData>();
    }

    public ComboDataLooksItem(String looksLabelName, String comboStyleCategory) {
        this.looksLabelName = looksLabelName;
        this.comboStyleCategory = comboStyleCategory;
        if(comboList== null)
            comboList = new ArrayList<ComboData>();
    }

    public ArrayList<ComboData> getComboList() {
        return comboList;
    }

    public void setComboList(ArrayList<ComboData> comboList) {
        this.comboList = comboList;
    }

    public String getComboStyleCategory() {
        return comboStyleCategory;
    }

    public void setComboStyleCategory(String comboStyleCategory) {
        this.comboStyleCategory = comboStyleCategory;
    }

    public String getLooksLabelName() {
        return looksLabelName;
    }

    public void setLooksLabelName(String looksLabelName) {
        this.looksLabelName = looksLabelName;
    }
}
