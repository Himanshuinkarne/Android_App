package com.svc.sml.Model;

import com.svc.sml.Database.ComboDataReconcile;

import java.io.Serializable;

/**
 * Created by himanshu on 9/6/16.
 */
public class LookBoardItem extends ComboDataReconcile implements Serializable {
    private String Status;
    private String Category;
    private String Description;
    private String Image_Key_Name;
    private String Campaign_ID;
    private String Curator_ID;
    private String Gender;
    private String Combo_ID;
    private int Rank ;

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getImage_Key_Name() {
        return Image_Key_Name;
    }

    public void setImage_Key_Name(String image_Key_Name) {
        Image_Key_Name = image_Key_Name;
    }

    public String getCampaign_ID() {
        return Campaign_ID;
    }

    public void setCampaign_ID(String campaign_ID) {
        Campaign_ID = campaign_ID;
    }

    public String getCurator_ID() {
        return Curator_ID;
    }

    public void setCurator_ID(String curator_ID) {
        Curator_ID = curator_ID;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getCombo_ID() {
        return Combo_ID;
    }

    public void setCombo_ID(String combo_ID) {
        Combo_ID = combo_ID;
    }

    public int getRank() {
        return Rank;
    }

    public void setRank(int rank) {
        Rank = rank;
    }
}
