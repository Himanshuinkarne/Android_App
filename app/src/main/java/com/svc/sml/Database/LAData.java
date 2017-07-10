package com.svc.sml.Database;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

public class LAData implements Serializable {

    public static final String TAG = "LoolAlikes";
    private static final long serialVersionUID = -7406082437623008161L;
    private String Brand;
    private String Link;
    private String Pic_File_Name;
    private String Pic_URL;
    private String Price;
    private String Purchase_SKU_ID;
    private String SKU_ID;
    private String Seller;
    private int User_Cart_Flag;


    private String Status;   //"Active"
    private String Exact_Match; //"True"

    private int isActive;

    private String fit;
    private String Title;
    private int Relevance;
    private String Combo_ID;
    private int Cart_Count =0;
    private int Buy_Count =0;


    private long id2;
    private Bitmap productBitmap;
    //private int isAddedToCart =0;

    private  String pngDownloadPath;
    public boolean isImageDownloaded;

    public LAData() {
    }

    public int getUser_Cart_Flag() {
        return User_Cart_Flag;
    }

    public void setUser_Cart_Flag(int user_Cart_Flag) {
        User_Cart_Flag = user_Cart_Flag;
    }

    public int getCart_Count() {
        return Cart_Count;
    }

    public void setCart_Count(int cart_Count) {
        this.Cart_Count = cart_Count;
    }

    public int getBuy_Count() {
        return Buy_Count;
    }

    public void setBuy_Count(int buy_Count) {
        this.Buy_Count = buy_Count;
    }

    public String getCombo_ID() {
        return Combo_ID;
    }

    public void setCombo_ID(String combo_ID) {
        Combo_ID = combo_ID;
    }

    public int getRelevance() {
        return Relevance;
    }

    public void setRelevance(Integer relevance) {
        this.Relevance = relevance;
    }

    public String getPic_URL() {
        return Pic_URL;
    }

    public void setPic_URL(String pic_URL) {
        this.Pic_URL = pic_URL;
    }

    public String getBrand() {
        return Brand;
    }

    public void setBrand(String brand) {
        this.Brand = brand;
    }

    public String getLink() {
        return Link;
    }

    public void setLink(String link) {
        this.Link = link;
    }

    public String getPic_File_Name() {
        return Pic_File_Name;
    }

    public void setPic_File_Name(String pic_File_Name) {
        this.Pic_File_Name = pic_File_Name;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        this.Price = price;
    }

    public String getPurchase_SKU_ID() {
        return Purchase_SKU_ID;
    }

    public void setPurchase_SKU_ID(String purchase_SKU_ID) {
        this.Purchase_SKU_ID = purchase_SKU_ID;
    }

    public String getSKU_ID() {
        return SKU_ID;
    }

    public void setSKU_ID(String SKU_ID) {
        this.SKU_ID = SKU_ID;
    }

    public String getSeller() {
        return Seller;
    }

    public void setSeller(String seller) {
        this.Seller = seller;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        this.Title = title;
    }

    public int getIsActive() {
        return isActive;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getExact_Match() {
        return Exact_Match;
    }

    public void setExact_Match(String exact_Match) {
        Exact_Match = exact_Match;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }
//    public int getIsAddedToCart() {
//        return isAddedToCart;
//    }
//
//    public void setIsAddedToCart(int isAddedToCart) {
//        this.isAddedToCart = isAddedToCart;
//    }

    public long getId2() {
        return id2;
    }

    public void setId2(long id2) {
        this.id2 = id2;
    }
    public Bitmap getProductBitmap() {
        return productBitmap;
    }

    public void setProductBitmap(Bitmap productBitmap) {
        this.productBitmap = productBitmap;
    }

    public String getFit() {
        return fit;
    }

    public void setFit(String fit) {
        this.fit = fit;
    }

    public String getPngDownloadPath() {
        return pngDownloadPath;
    }

    public void setPngDownloadPath(String pngDownloadPath) {
        this.pngDownloadPath = pngDownloadPath;
    }


    public static class Comparators {

        public static Comparator<LAData> NAME = new Comparator<LAData>() {
            @Override
            public int compare(LAData o1, LAData o2) {
                //return o1.name.compareTo(o2.name);
                return o1.getPurchase_SKU_ID().compareTo(o2.getPurchase_SKU_ID());
            }
        };

        public static Comparator<LAData> orderDSC = new Comparator<LAData>() {
            @Override
            public int compare(LAData o1, LAData o2) {
                //return o1.age - o2.age;
                return o2.getRelevance() - o1.getRelevance();
            }
        };
        public static Comparator<LAData> orderASC = new Comparator<LAData>() {
            @Override
            public int compare(LAData o1, LAData o2) {
                //return o1.age - o2.age;
                return o1.getRelevance() - o2.getRelevance();
            }
        };

        public static Comparator<LAData> priceASC = new Comparator<LAData>() {
            @Override
            public int compare(LAData o1, LAData o2) {
                //return o1.age - o2.age;
                return (int)(Float.parseFloat(o1.getPrice()) - Float.parseFloat(o2.getPrice()));
            }
        };


        public static Comparator<LAData> priceDSC = new Comparator<LAData>() {
            @Override
            public int compare(LAData o1, LAData o2) {
                //return o1.age - o2.age;
                return (int)(Float.parseFloat(o2.getPrice()) - Float.parseFloat(o1.getPrice()));
            }
        };


        public static Comparator<LAData> NAMEandAGE = new Comparator<LAData>() {
            @Override
            public int compare(LAData o1, LAData o2) {
//                int i = o1.name.compareTo(o2.name);
//                if (i == 0) {
//                    i = o1.age - o2.age;
//                }
//                return i;
                return 0;
            }
        };
    }

    // Comparator for Ascending Order
    public static Comparator<String > StringAscComparator = new Comparator<String>() {

        public int compare(String app1, String app2) {

            String stringName1 = app1;
            String stringName2 = app2;

            return stringName1.compareToIgnoreCase(stringName2);
        }
    };

    //Comparator for Descending Order
    public static Comparator<String> StringDescComparator = new Comparator<String>() {

        public int compare(String app1, String app2) {

            String stringName1 = app1;
            String stringName2 = app2;
            return stringName2.compareToIgnoreCase(stringName1);
        }
    };


    public class LADataWrapper implements Serializable {

        public static final String TAG = "LoolAlikes";
        private List<LAData> GetLookalikesResult;

        public List<LAData> getLaDatas() {
            return GetLookalikesResult;
        }
    }

    public class LACartDataWrapper implements Serializable {
        //GetCartDataResult
        public static final String TAG = "LoolAlikes";
        private List<LAData> GetCartDataResult;

        public List<LAData> getLaDatas() {
            return GetCartDataResult;
        }
    }

}