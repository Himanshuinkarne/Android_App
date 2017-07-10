package com.svc.sml.Model;

import android.view.Gravity;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.volley.toolbox.NetworkImageView;
import com.svc.sml.Database.SkuData;

/**
 * Created by himanshu on 11/3/16.
 */




public class ClausalSkuModel {

    public String skuId = "";
    public int marginRight = 0;
    public int marginLeft = 0;
    public int marginTop = 0;
    public int marginBottom = 0;
    public int hPercent = 1;
    public int wPercent = 1;
    public boolean isCenter = false;
    public boolean isCenterX = false;
    public boolean isCenterY = false;
    public EClausalType eClausalTypeFamily = EClausalType.eClausalTypeAcc;
    public EClausalType eClausalType ;

    public SkuData skuData;
    public int gravity = Gravity.TOP|Gravity.LEFT;
    public NetworkImageView iv = null;
    public ImageView iv2 = null;
    public ProgressBar pb = null;
    public boolean isSelected = false;
    public int indexViewLayer  = 0;

    public ClausalSkuModel() {

    }

    public ClausalSkuModel(EClausalType eClausalType) {
        this.eClausalType = eClausalType;
        if(eClausalType == EClausalType.eClausalTypeTop){
            this.eClausalTypeFamily =  EClausalType.eClausalTypeGarment;
        }
        else if(eClausalType == EClausalType.eClausalTypeBottom){
            this.eClausalTypeFamily =  EClausalType.eClausalTypeGarment;
        }
        else if(eClausalType == EClausalType.eClausalTypeShoes){
            this.eClausalTypeFamily =  EClausalType.eClausalTypeAccessory;
        }
        else if(eClausalType == EClausalType.eClausalTypeAcc){
            this.eClausalTypeFamily =  EClausalType.eClausalTypeAccessory;
        }
    }

    public ClausalSkuModel(EClausalType eClausalType, String skuId) {
        this.eClausalType = eClausalType;
        if(eClausalType == EClausalType.eClausalTypeTop){
            this.eClausalTypeFamily =  EClausalType.eClausalTypeGarment;
        }
        else if(eClausalType == EClausalType.eClausalTypeBottom){
            this.eClausalTypeFamily =  EClausalType.eClausalTypeGarment;
        }
        else if(eClausalType == EClausalType.eClausalTypeShoes){
            this.eClausalTypeFamily =  EClausalType.eClausalTypeAccessory;
        }
        else if(eClausalType == EClausalType.eClausalTypeAcc){
            this.eClausalTypeFamily =  EClausalType.eClausalTypeAccessory;
        }
        this.skuId = skuId;
    }

    public ClausalSkuModel(EClausalType eClausalType, SkuData skuData) {
        this.eClausalType = eClausalType;
        if(eClausalType == EClausalType.eClausalTypeTop){
            this.eClausalTypeFamily =  EClausalType.eClausalTypeGarment;
        }
        else if(eClausalType == EClausalType.eClausalTypeBottom){
            this.eClausalTypeFamily =  EClausalType.eClausalTypeGarment;
        }
        else if(eClausalType == EClausalType.eClausalTypeShoes){
            this.eClausalTypeFamily =  EClausalType.eClausalTypeAccessory;
        }
        else if(eClausalType == EClausalType.eClausalTypeAcc){
            this.eClausalTypeFamily =  EClausalType.eClausalTypeAccessory;
        }
        this.skuData = skuData;
    }



    public enum EClausalType {
        eClausalTypeGarment("Garment"),
        eClausalTypeAccessory("Accessory"),
        eClausalTypeTop("top"),
        eClausalTypeTop2("top2"),
        eClausalTypeTop3("top3"),
        eClausalTypeBottom("bottom"),
        eClausalTypeShoes("shoes"),
        eClausalTypeAcc("acc"),
        eClausalTypeAcc2("acc2"),
        eClausalTypeAcc3("acc3"),
        eClausalTypeAcc4("acc4"),
        eClausalTypeAcc5("acc5"),
        eClausalTypeAcc6("acc6"),
        eClausalTypeAcc7("acc7"),

        eClausalTypeAcc8("acc8");
        /*
        private final int value;
        private EDialFilter(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }
        */
        private final String name;

        EClausalType(String s) {
            name = s;
        }

        public boolean equalsName(String otherName) {
            return otherName != null && name.equals(otherName);
        }
        public String toString() {
            return this.name;
        }
    }


}
