package com.svc.sml.Database;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

/**
 * Created by himanshu on 1/12/16.
 */
public class ComboDataReconcile implements Serializable {

    protected long rowId;
    public long getRowId() {
        return rowId;
    }
    public void setRowId(long id) {
        this.rowId = id;
    }

    protected float Forced_Rank; //
    protected String Vogue_Flag;
    protected String Push_Flag;
    protected String Combo_Gender;


    protected String Combo_ID;
    //protected String Combo_PIC_Png_File_Name;
    protected String Combo_PIC_Png_Key_Name;
    protected String Combo_Style_Category;
    protected String Combo_Title;
    protected String Combo_Color;
    protected String Combo_Description;

    protected int Likes_Count; //
    protected float Style_Rating; //

    private String mA1_Category;
    private String mA1_Color;
    private String mA2_Category;
    private String mA2_Color;
    private String mA3_Category;
    private String mA3_Color;
    private String mA4_Category;
    private String mA4_Color;

    //not in JSON
    protected int isLiked =0;
    protected int cartCount =0 ;
    protected int viewCount=0;
    protected int isDisplayReady =0;
    private String comboUpdatedDate;
    private boolean isDownloadedTempStatus = false;
    private long dateReconcile;

    public long getDateReconcile() {
        return dateReconcile;
    }

    public void setDateReconcile(long dateReconcile) {
        this.dateReconcile = dateReconcile;
    }

    public boolean isDownloadedTempStatus() {
        return isDownloadedTempStatus;
    }

    public void setIsDownloadedTempStatus(boolean isDownloaded) {
        this.isDownloadedTempStatus = isDownloaded;
    }
//private Bitmap thumbnailImage;

    //private long seenDate;

    private String updatedDate;

    private long dateSeenInMilli =0;
    private int seenFactor =0 ;
    private String looksCategoryTitle = "";


    public String getComboUpdatedDate() {
        return comboUpdatedDate;
    }

    public void setComboUpdatedDate(String comboUpdatedDate) {
        this.comboUpdatedDate = comboUpdatedDate;
    }

    public static String COMBO_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public Date getComboUpdatedDateFormated() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(COMBO_DATE_FORMAT, Locale.getDefault());
        try {
            return dateFormat.parse(comboUpdatedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
           return null;
    }

    public void setComboUpdatedDateFormated(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(COMBO_DATE_FORMAT, Locale.getDefault());
        this.comboUpdatedDate =  dateFormat.format(date);
    }
    public String getCombo_PIC_Png_Key_Name() {
        return Combo_PIC_Png_Key_Name;
    }

    public void setCombo_PIC_Png_Key_Name(String Combo_PIC_Png_Key_Name) {
        this.Combo_PIC_Png_Key_Name = Combo_PIC_Png_Key_Name;
    }

    public String getCombo_Style_Category() {
        return Combo_Style_Category;
    }

    public void setCombo_Style_Category(String Combo_Style_Category) {
        this.Combo_Style_Category = Combo_Style_Category;
    }

    public String getCombo_Title() {
        return Combo_Title;
    }

    public void setCombo_Title(String Combo_Title) {
        this.Combo_Title = Combo_Title;
    }

    public String getCombo_ID() {
        return Combo_ID;
    }

    public void setCombo_ID(String Combo_ID) {
        this.Combo_ID = Combo_ID;
    }

    public String getCombo_Gender() {
        return Combo_Gender;
    }

    public void setCombo_Gender(String Combo_Gender) {
        this.Combo_Gender = Combo_Gender;
    }

    public String getPush_Flag() {
        return Push_Flag;
    }

    public void setPush_Flag(String push_Flag) {
        Push_Flag = push_Flag;
    }

    public float getForced_Rank() {
        return Forced_Rank;
    }

    public void setForced_Rank(float forced_Rank) {
        this.Forced_Rank = forced_Rank;
    }

    public String getCombo_Color() {
        return Combo_Color;
    }

    public void setCombo_Color(String combo_Color) {
        Combo_Color = combo_Color;
    }

    public int getLikes_Count() {
        return Likes_Count;
    }

    public void setLikes_Count(int likes_Count) {
        Likes_Count = likes_Count;
    }

    public float getStyle_Rating() {
        return Style_Rating;
    }

    public void setStyle_Rating(float style_Rating) {
        Style_Rating = style_Rating;
    }

    public int isLiked() {
        return isLiked;
    }

    public void setIsLiked(int isLiked) {
        this.isLiked = isLiked;
    }

    public int getCartCount() {
        return cartCount;
    }

    public void setCartCount(int cartCount) {
        this.cartCount = cartCount;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }


    public String getCombo_Description() {
        return Combo_Description;
    }

    public void setCombo_Description(String combo_Description) {
        Combo_Description = combo_Description;
    }

    public int getIsDisplayReady() {
        return isDisplayReady;
    }

    public void setIsDisplayReady(int isDisplayReady) {
        this.isDisplayReady = isDisplayReady;
    }

    public String getmA1_Category() {
        return mA1_Category;
    }

    public void setmA1_Category(String mA1_Category) {
        this.mA1_Category = mA1_Category;
    }

    public String getmA1_Color() {
        return mA1_Color;
    }

    public void setmA1_Color(String mA1_Color) {
        this.mA1_Color = mA1_Color;
    }

    public String getmA2_Category() {
        return mA2_Category;
    }

    public void setmA2_Category(String mA2_Category) {
        this.mA2_Category = mA2_Category;
    }

    public String getmA2_Color() {
        return mA2_Color;
    }

    public void setmA2_Color(String mA2_Color) {
        this.mA2_Color = mA2_Color;
    }

    public String getmA3_Category() {
        return mA3_Category;
    }

    public void setmA3_Category(String mA3_Category) {
        this.mA3_Category = mA3_Category;
    }

    public String getmA3_Color() {
        return mA3_Color;
    }

    public void setmA3_Color(String mA3_Color) {
        this.mA3_Color = mA3_Color;
    }

    public String getmA4_Category() {
        return mA4_Category;
    }

    public void setmA4_Category(String mA4_Category) {
        this.mA4_Category = mA4_Category;
    }

    public String getmA4_Color() {
        return mA4_Color;
    }

    public void setmA4_Color(String mA4_Color) {
        this.mA4_Color = mA4_Color;
    }

    public int getIsLiked() {
        return isLiked;
    }

    public String getVogue_Flag() {
        return Vogue_Flag;
    }

    public void setVogue_Flag(String vogue_Flag) {
        Vogue_Flag = vogue_Flag;
    }

//    public long getSeenDate() {
//        return seenDate;
//    }
//
//    public void setSeenDate(long seenDate) {
//        this.seenDate = seenDate;
//    }


//    public Bitmap getThumbnailImage() {
//        return thumbnailImage;
//    }
//
//    public void setThumbnailImage(Bitmap thumbnailImage) {
//        this.thumbnailImage = thumbnailImage;
//    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }

    public long getDateSeenInMilli() {
        return dateSeenInMilli;
    }

    public void setDateSeenInMilli(long dateSeenInMilli) {
        this.dateSeenInMilli = dateSeenInMilli;
    }

    public int getSeenFactor() {
        return seenFactor;
    }

    public void setSeenFactor(int seenFactor) {
        this.seenFactor = seenFactor;
    }

    public String getLooksCategoryTitle() {
        return looksCategoryTitle;
    }

    public void setLooksCategoryTitle(String looksCategoryTitle) {
        this.looksCategoryTitle = looksCategoryTitle;
    }

    /* Utility Methods */
    public static class Comparators {
        public static Comparator<ComboData> FORCED_RANK_DSC = new Comparator<ComboData>() {
            @Override
            public int compare(ComboData o1, ComboData o2) {
                return (int)(o2.getForced_Rank() - o1.getForced_Rank());
            }
        };

        public static Comparator<ComboData> COMBO_UPDATED_DATE_DSC = new Comparator<ComboData>() {
            @Override
            public int compare(ComboData o1, ComboData o2) {
                //if(todayDate.after(historyDate) && todayDate.before(futureDate))
                return o1.getComboUpdatedDateFormated().compareTo(o2.getComboUpdatedDateFormated());
            }
        };
        public static Comparator<ComboData> COMBO_UPDATED_AT_DSC = new Comparator<ComboData>() {
            @Override
            public int compare(ComboData o1, ComboData o2) {
                //if(todayDate.after(historyDate) && todayDate.before(futureDate))
                return (int) (o1.getDateSeenInMilli()- o2.getDateSeenInMilli());
            }
        };
    }
}
