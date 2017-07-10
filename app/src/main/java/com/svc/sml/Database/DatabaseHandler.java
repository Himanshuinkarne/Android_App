package com.svc.sml.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {

    public static final String TAG = "DatabaseHandler";
    private static final String DATABASE_NAME = "inkarne.db";
    //private static final int DATABASE_VERSION = 1;
    //private static final int DATABASE_VERSION = 2; //build 39 18Nov 01:08pm
    private static final int DATABASE_VERSION = 3; //build 41 21Nov 03:30pm
    public static final String TABLE_ACCESSORY = "table_accessory_type1";
    public static final String TABLE_ACCESSORY_TYPE2 = "table_accessory_type2";
    public static final String TABLE_ACCESSORY_TYPE3 = "table_accessory_type3";
    public static final String TABLE_USER_AVATAR = "user_faces";
    public static final String TABLE_USER = "user";
    public static final String TABLE_LA = "la";
    public static final String TABLE_COMBODATA = "combodata_table";
    public static final String TABLE_COMBO_LOOKBOARD ="combo_lookboard_table";
    public static final String TABLE_COMBODATA_LIKE = "combodata_like_table";

    // columns of the User_Details table
    //public static final String SEEN_DATE ="UPDATED_AT";
    public static final String USER_ID = "user_id";
    public static final String MOBILE_NUMBER = "mobile_number";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String GENDER = "gender";
    public static final String PIN = "pin";
    public static final String EMAIL_ID = "email_id";
    public static final String DOB = "dob_dd_mmm_yyyy";
    public static final String DOB_MONTH = "dob_month";
    public static final String DOB_YEAR = "dob_year";
    public static final String DOB_DAY = "dob_day";
    public static final String WEIGHT = "weight";
    public static final String HEIGHT = "height";
    public static final String BUST_CIRCUM = "bust_circum_in_cm";
    public static final String WAIST_CIRCUM = "waist_circum";
    public static final String HIPS_CIRCUM = "hips_circum";
    public static final String IS_REGISTRATION_COMPLETE = "is_registration_complete";
    public static final String DEFAULT_FACE_ID = "default_face_id";
    public static final String POINTS = "points";
    public static final String USER_THUMB_URL = "thumburl";

    /* Table - USER_FACES */
    public static final String FACE_ID = "face_id";
    public static final String PB_ID = "pb_id";
    public static final String IS_COMPLETE = "is_complete";
    public static final String BODY_OBJ_KEY_NAME = "body_obj_key_name";
    public static final String BODY_TEXTURE_KEY_NAME = "body_texture_key_name";
    public static final String BODY_OBJ_DOWNLOAD_STATUS = "body_obj_download_status";
    public static final String BODY_TEXTURE_DOWNLOAD_STATUS = "body_texture_download_status";
    public static final String HAIR_OBJ_KEY_NAME = "hair_obj_key_name";
    public static final String HAIR_TEXTURE_KEY_NAME = "hair_texture_key_name";
    public static final String HAIR_OBJ_DOWNLOAD_STATUS = "hair_obj_download_status";
    public static final String HAIR_TEXTURE_DOWNLOAD_STATUS = "hair_texture_download_status";
    public static final String DEFAULT_HAIR_ID = "default_hair_id";
    public static final String DEFAULT_SPECS_ID = "default_specs_id";
    public static final String SPECS_OBJ_KEY_NAME = "specs_obj_key_name";
    public static final String SPECS_TEXTURE_KEY_NAME = "specs_texture_key_name";
    public static final String SPECS_OBJ_DOWNLOAD_STATUS = "specs_obj_download_status";
    public static final String SPECS_TEXTURE_DOWNLOAD_STATUS = "specs_texture_download_status";
    public static final String DEFAULT_LEG_ID = "default_leg_id";
    public static final String LEG_ID = "leg_id";

    public static final String FACE_OBJ_DOWNLOAD_STATUS = "face_obj_download_status";
    public static final String FACE_TEXTURE_DOWNLOAD_STATUS = "face_texture_download_status";

    public static final String FEDUCIAL_POINTS = "feducial_points";
    public static final String IMAGE_SAVED_FILEPATH = "image_saved_path";
    public static final String IMAGE_RESIZE_RATIO    = "image_resize_ratio";

    /* Table Column ACCESSORY */
    public static final String OBJ_ID = "obj_id";
    public static final String OBJ_ID2 = "obj_id2";
    public static final String OBJ_KEY_NAME = "obj_key_name";
    public static final String OBJ_PATH_NAME = "obj_path_name";
    public static final String TEXTURE_KEY_NAME = "texture_key_name";
    public static final String OBJ_DOWNLOAD_STATUS = "obj_download_status";
    public static final String TEXTURE_DOWNLOAD_STATUS = "texture_download_status";
    public static final String PIC_PNG_KEY_NAME = "pic_png_key_name";
    public static final String FORCED_RANK = "forced_rank";
    public static final String ACCESSORY_TYPE = "ACCESSORY_TYPE";
    public static final String ACCESSORY_TYPE2 = "ACCESSORY_TYPE2";
    // columns of the LA_Details table
    public static final String BRAND = "brand";
    public static final String LINK = "link";
    public static final String PIC_KEY_NAME = "pic_key_name";//better PIC_PNG_KEY_NAME uniform
    public static final String PRICE = "price";
    public static final String SKUID = "skuid";
    public static final String PURCHASE_SKUID = "purchase_skuid";
    public static final String RELEVANCE = "relevance";
    public static final String CART_COUNT = "cart";
    public static final String BUY_COUNT = "buy";
    public static final String SELLER = "seller";
    public static final String FIT = "status";
    public static final String TITLE = "title";


    // columns of the COMBODATA table
    public static final String COMBO_ID = "combo_id";
    public static final String COMBO_TITLE = "combo_title";
    public static final String COMBO_STYLE_CATEGORY = "combo_style_category";
    public static final String COMBO_PIC_PNG_FILE_NAME = "combo_pic_png_file_name";
    public static final String COMBO_PIC_PNG_KEY_NAME = "combo_pic_png_key_name";//
    public static final String SKU_ID1 = "sku_id1";
    public static final String SKU_ID2 = "sku_id2";
    public static final String SKU_ID3 = "sku_id3";
    public static final String SKU_ID4 = "sku_id4";
    public static final String SKU_ID5 = "sku_id5";
    public static final String SKU_ID6 = "sku_id6";
    public static final String SKU_ID7 = "sku_id7";
    public static final String SKU_ID8 = "sku_id8";
    public static final String SKU_ID9 = "sku_id9";
    public static final String SKU_ID10 = "sku_id10";
    public static final String LIKES_COUNT = "likes_count";
    public static final String STYLE_RATING = "style_rating";

    public static final String LEG_OBJ_KEY_NAME ="leg_obj_key";
    public static final String LEG_OBJ_PATH_NAME ="leg_obj_path";
    public static final String LEG_TEX_KEY_NAME ="leg_tex_key";

    public static final String A1_CATEGORY = "a1_category";
    public static final String A1_COLOR = "a1_color";
    //public static final String A1_OBJ_FILE_NAME = "a1_obj_file_name";
    public static final String A1_OBJ_KEY_NAME = "a1_obj_key_name";//
    public static final String A1_OBJ_PATH_NAME = "a1_obj_path_name";//
    public static final String A1_OBJ_DOWNLOAD_STATUS = "a1_obj_download_status";//
    //public static final String A1_PNG_FILE_NAME = "a1_png_file_name";
    public static final String A1_PNG_KEY_NAME = "a1_png_key_name";//
    public static final String A1_TEXTURE_DOWNLOAD_STATUS = "a1_texture_download_status";//
    //public static final String A1_PIC_PNG_FILE_NAME = "a1_pic_png_file_name";
    public static final String A1_PIC_PNG_KEY_NAME = "a1_pic_png_key_name";//


    public static final String A2_CATEGORY = "a2_category";
    public static final String A2_COLOR = "a2_color";
    //public static final String A2_OBJ_FILE_NAME = "a2_obj_file_name";
    public static final String A2_OBJ_KEY_NAME = "a2_obj_key_name";//
    //public static final String A2_PNG_FILE_NAME = "a2_png_file_name";
    public static final String A2_PNG_KEY_NAME = "a2_png_key_name";//
    //public static final String A2_PIC_PNG_FILE_NAME = "a2_pic_png_file_name";
    public static final String A2_PIC_PNG_KEY_NAME = "a2_pic_png_key_name";//

    public static final String A3_CATEGORY = "a3_category";
    public static final String A3_COLOR = "a_color";
    //public static final String A3_OBJ_FILE_NAME = "a3_obj_file_name";
    public static final String A3_OBJ_KEY_NAME = "a3_obj_key_name";//
    //public static final String A3_PNG_FILE_NAME = "a3_png_file_name";
    public static final String A3_PNG_KEY_NAME = "a3_png_key_name";//
    //public static final String A3_PIC_PNG_FILE_NAME = "a3_pic_png_file_name";
    public static final String A3_PIC_PNG_KEY_NAME = "a3_pic_png_key_name";//

    public static final String A4_CATEGORY = "a4_category";
    public static final String A4_COLOR = "a4_color";
    //public static final String A4_OBJ_FILE_NAME = "a4_obj_file_name";
    public static final String A4_OBJ_KEY_NAME = "a4_obj_key_name";//
    //public static final String A4_PNG_FILE_NAME = "a4_png_file_name";
    public static final String A4_PNG_KEY_NAME = "a4_png_key_name";//
    //public static final String A4_PIC_PNG_FILE_NAME = "a4_pic_png_file_name";
    public static final String A4_PIC_PNG_KEY_NAME = "a4_pic_png_key_name";//

    public static final String A5_CATEGORY = "a5_category";
    public static final String A5_COLOR = "a5_color";
    //public static final String A5_OBJ_FILE_NAME = "a5_obj_file_name";
    public static final String A5_OBJ_KEY_NAME = "a5_obj_key_name";//
    //public static final String A5_PNG_FILE_NAME = "a5_png_file_name";
    public static final String A5_PNG_KEY_NAME = "a5_png_key_name";//
    //public static final String A5_PIC_PNG_FILE_NAME = "a5_pic_png_file_name";
    public static final String A5_PIC_PNG_KEY_NAME = "a5_pic_png_key_name";//

    public static final String A6_CATEGORY = "a6_category";
    public static final String A6_COLOR = "a6_color";
    //public static final String A6_OBJ_FILE_NAME = "a6_obj_file_name";
    public static final String A6_OBJ_KEY_NAME = "a6_obj_key_name";//
    public static final String A6_OBJ_PATH_NAME = "a6_obj_path_name";//
    public static final String A6_OBJ_DOWNLOAD_STATUS = "a6_obj_download_status";
    //public static final String A6_PNG_FILE_NAME = "a6_png_file_name";
    public static final String A6_PNG_KEY_NAME = "a6_png_key_name";//
    public static final String A6_TEXTURE_DOWNLOAD_STATUS = "a6_texture_download_status";
    //public static final String A6_PIC_PNG_FILE_NAME = "a6_pic_png_file_name";
    public static final String A6_PIC_PNG_KEY_NAME = "a6_pic_png_key_name";//

    public static final String A7_CATEGORY = "a7_category";
    //public static final String A7_OBJ_FILE_NAME = "a7_obj_file_name";
    public static final String A7_OBJ_KEY_NAME = "a7_obj_key_name";//
    public static final String A7_OBJ_PATH_NAME = "a7_obj_path_name";//
    public static final String A7_OBJ_DOWNLOAD_STATUS = "a7_obj_download_status";
    //public static final String A7_PNG_FILE_NAME = "a7_png_file_name";
    public static final String A7_PNG_KEY_NAME = "a7_png_key_name";//
    public static final String A7_TEXTURE_DOWNLOAD_STATUS = "a7_texture_download_status";
    //public static final String A7_PIC_PNG_FILE_NAME = "a7_pic_png_file_name";
    public static final String A7_PIC_PNG_KEY_NAME = "a7_pic_png_key_name";//

    public static final String A8_CATEGORY = "a8_category";
    //public static final String A8_OBJ_FILE_NAME = "a8_obj_file_name";
    public static final String A8_OBJ_KEY_NAME = "a8_obj_key_name";//
    public static final String A8_OBJ_PATH_NAME = "a8_obj_path_name";//
    public static final String A8_OBJ_DOWNLOAD_STATUS = "a8_obj_download_status";
    //public static final String A8_PNG_FILE_NAME = "a8_png_file_name";
    public static final String A8_PNG_KEY_NAME = "a8_png_key_name";//
    public static final String A8_TEXTURE_DOWNLOAD_STATUS = "a8_texture_download_status";
    //public static final String A8_PIC_PNG_FILE_NAME = "a8_pic_png_file_name";
    public static final String A8_PIC_PNG_KEY_NAME = "a8_pic_png_key_name";//

    public static final String A9_CATEGORY = "a9_category";
    //public static final String A9_OBJ_FILE_NAME = "a9_obj_file_name";
    public static final String A9_OBJ_KEY_NAME = "a9_obj_key_name";//
    public static final String A9_OBJ_PATH_NAME = "a9_obj_path_name";//
    public static final String A9_OBJ_DOWNLOAD_STATUS = "a9_obj_download_status";
    //public static final String A9_PNG_FILE_NAME = "a9_png_file_name";
    public static final String A9_PNG_KEY_NAME = "a9_png_key_name";//
    public static final String A9_TEXTURE_DOWNLOAD_STATUS = "a9_texture_download_status";
    //public static final String A9_PIC_PNG_FILE_NAME = "a9_pic_png_file_name";
    public static final String A9_PIC_PNG_KEY_NAME = "a9_pic_png_key_name";//

    public static final String A10_CATEGORY = "a10_category";
    //public static final String A10_OBJ_FILE_NAME = "a10_obj_file_name";
    public static final String A10_OBJ_KEY_NAME = "a10_obj_key_name";//
    public static final String A10_OBJ_PATH_NAME = "a10_obj_path_name";//
    public static final String A10_OBJ_DOWNLOAD_STATUS = "a10_obj_download_status";
    //public static final String A10_PNG_FILE_NAME = "a10_png_file_name";
    public static final String A10_PNG_KEY_NAME = "a10_png_key_name";//
    public static final String A10_TEXTURE_DOWNLOAD_STATUS = "a10_texture_download_status";
    //public static final String A10_PIC_PNG_FILE_NAME = "a10_pic_png_file_name";
    public static final String A10_PIC_PNG_KEY_NAME = "a10_pic_png_key_name";//

    public static final String UPDATED_DATE = "updated_date";//
    public static final String DATE_RECONCILE_IN_MILLI = "date_reconcile";//

    public static final String PUSH_FLAG = "push_flag";
    public static final String VOGUE_FLAG = "Vogue_flag";

    public static final String COMBO_COLOR = "combo_color";
    public static final String DESCRIPTION = "description";

    //not in JSON
    public static final String COMBO_LOCAL_IS_LIKED = "combo_local_is_liked";//
    public static final String COMBO_LOCAL_VIEW_COUNT = "combo_local_view_count";
    public static final String COMBO_LOCAL_CART_COUNT = "combo_local_cart_count";
    public static final String COMBO_LOCAL_IS_DISPLAY_READY = "combo_local_is_display_ready";
    public static final String DATE_SEEN_IN_MILLI = "combo_local_seen_time";
    public static final String COMBO_LOCAL_SEEN_FACTOR = "combo_local_seen_factor";


    //LookBoard
    public static final String STATUS = "status";
    public static final String COMPAIGN_ID = "compaign_id";
    public static final String CURATOR_ID = "curatot_id";


    // SQL statement of the User_Details table creation
    private static final String SQL_CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USER + "("
            + USER_ID + " TEXT PRIMARY KEY NOT NULL , "
            + FIRST_NAME + " TEXT NOT NULL, "
            + LAST_NAME + " TEXT, "
            + MOBILE_NUMBER + " TEXT NOT NULL, "
            + GENDER + " TEXT NOT NULL, "
            + PIN + " TEXT, "
            + EMAIL_ID + " TEXT NOT NULL, "
            + DOB_DAY + " INTEGER, "
            + DOB_MONTH + " INTEGER, "
            + DOB_YEAR + " INTEGER, "
            + DOB + " TEXT NOT NULL, "
            + WEIGHT + " INTEGER, "
            + HEIGHT + " REAL, "
            + WAIST_CIRCUM + " INTEGER, "
            + BUST_CIRCUM + " INTEGER, "
            + HIPS_CIRCUM + " INTEGER, "
            + IS_REGISTRATION_COMPLETE + " INTEGER, "
            + PB_ID + " TEXT, "
            + POINTS +  " INTEGER DEFAULT 0 , "
            + USER_THUMB_URL +  " TEXT , "
            + DEFAULT_FACE_ID + " TEXT "
            + ")";


    private static final String SQL_CREATE_TABLE_USER_AVATAR = "CREATE TABLE " + TABLE_USER_AVATAR + "("
            + FACE_ID + " TEXT  NOT NULL, "
            + IS_COMPLETE + " INTEGER DEFAULT 0 , "
            + USER_ID + " TEXT , "
            + PIC_PNG_KEY_NAME + " TEXT,  "
            + OBJ_KEY_NAME + " TEXT , "
            + TEXTURE_KEY_NAME + " TEXT , "
            + FACE_TEXTURE_DOWNLOAD_STATUS + " INTEGER DEFAULT 0, "
            + FACE_OBJ_DOWNLOAD_STATUS + " INTEGER DEFAULT 0, "
            + PB_ID + " TEXT,  "
            + BODY_OBJ_KEY_NAME + " TEXT , "
            + BODY_TEXTURE_KEY_NAME + " TEXT , "
            + BODY_TEXTURE_DOWNLOAD_STATUS + " INTEGER DEFAULT 0, "
            + BODY_OBJ_DOWNLOAD_STATUS + " INTEGER DEFAULT 0, "
            + DEFAULT_HAIR_ID + " TEXT,  "
            + HAIR_OBJ_KEY_NAME + " TEXT , "
            + HAIR_TEXTURE_KEY_NAME + " TEXT , "
            + HAIR_TEXTURE_DOWNLOAD_STATUS + " INTEGER DEFAULT 0, "
            + HAIR_OBJ_DOWNLOAD_STATUS + " INTEGER DEFAULT 0, "

            + DEFAULT_SPECS_ID + " TEXT,  "
            + SPECS_OBJ_KEY_NAME + " TEXT , "
            + SPECS_TEXTURE_KEY_NAME + " TEXT , "
            + SPECS_TEXTURE_DOWNLOAD_STATUS + " INTEGER DEFAULT 0, "
            + SPECS_OBJ_DOWNLOAD_STATUS + " INTEGER DEFAULT 0, "
            + DEFAULT_LEG_ID + " TEXT ,"
            + UPDATED_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP ,"
            + FEDUCIAL_POINTS + " TEXT , "
            + IMAGE_SAVED_FILEPATH +" TEXT , "
            + IMAGE_RESIZE_RATIO + " TEXT , "
            + "PRIMARY KEY (FACE_ID, USER_ID) "
            + ")";

    private static final String SQL_CREATE_TABLE_ACCESSORY = "CREATE TABLE " + TABLE_ACCESSORY + "("
            + OBJ_ID + " TEXT NOT NULL, "
            + OBJ_ID2 + " TEXT DEFAULT '', "
            + USER_ID + " TEXT , "
            + PIC_PNG_KEY_NAME + " TEXT,  "
            + OBJ_KEY_NAME + " TEXT,  "
            //+ OBJ_PATH_NAME + " TEXT,  "
            + TEXTURE_KEY_NAME + " TEXT, "
            + TEXTURE_DOWNLOAD_STATUS + " INTEGER DEFAULT 0, "
            + OBJ_DOWNLOAD_STATUS + " INTEGER DEFAULT 0, "
            + FORCED_RANK + " INTEGER, "
            + ACCESSORY_TYPE + " TEXT,"
            + ACCESSORY_TYPE2 + " TEXT DEFAULT '', "
            + PB_ID + " TEXT DEFAULT '',   "
            + FACE_ID + " TEXT DEFAULT '',"
            + DATE_RECONCILE_IN_MILLI + " INTEGER DEFAULT 0  ,"
            + UPDATED_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP ,"
            + "PRIMARY KEY (OBJ_ID, PB_ID,FACE_ID)"
            + ")";

//    private static final String SQL_CREATE_TABLE_ACCESSORY_TYPE2 = "CREATE TABLE " + TABLE_ACCESSORY_TYPE2 + "("
//            + OBJ_ID +  " TEXT  NOT NULL, "
//            + USER_ID + " TEXT , "
//            + PIC_PNG_KEY_NAME + " TEXT,  "
//            + OBJ_KEY_NAME + " TEXT,  "
//            + TEXTURE_KEY_NAME + " TEXT, "
//            + FORCED_RANK + " INTEGER, "
//            + ACCESSORY_TYPE + " TEXT,"
//            + FACE_ID + " TEXT "
//
//            + ")";

    // SQL statement of the LA_Details table creation
    private static final String SQL_CREATE_TABLE_LA = "CREATE TABLE " + TABLE_LA + "("
            + PURCHASE_SKUID + " TEXT PRIMARY KEY  , "
            + COMBO_ID + " TEXT , "
            + SKUID + " TEXT , "
            + BRAND + " TEXT , "
            + LINK + " TEXT , "
            + PIC_KEY_NAME + " TEXT , "
            + SELLER + " TEXT , "
            + PRICE + " TEXT , "
            + FIT + " TEXT , "
            + TITLE + " TEXT ,"
            + RELEVANCE + "  INTEGER DEFAULT 0 ,"
            + CART_COUNT + " TEXT ,"
            + UPDATED_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP ,"
            + BUY_COUNT + " TEXT "
            + ")";

    // SQL statement of the ComboData table creation
    private static final String SQL_CREATE_TABLE_COMBODATA = "CREATE TABLE " + TABLE_COMBODATA + "("
            + LEG_ID + " TEXT , "
            + PB_ID + " TEXT , "
            + FACE_ID + " TEXT , "
            + COMBO_ID + " TEXT PRIMARY KEY, "
            + COMBO_TITLE + " TEXT , "
            + GENDER + " TEXT , "
            + COMBO_STYLE_CATEGORY + " TEXT , "
            + COMBO_PIC_PNG_FILE_NAME + " TEXT , " + COMBO_PIC_PNG_KEY_NAME + " TEXT , "
            + SKU_ID1 + " TEXT , "
            + SKU_ID2 + " TEXT , "
            + SKU_ID3 + " TEXT , "
            + SKU_ID4 + " TEXT , "
            + SKU_ID5 + " TEXT , "
            + SKU_ID6 + " TEXT , "
            + SKU_ID7 + " TEXT , "
            + SKU_ID8 + " TEXT , "
            + SKU_ID9 + " TEXT , "
            + SKU_ID10 + " TEXT , "
            + LIKES_COUNT + " INTEGER , "//
            + STYLE_RATING + " INTEGER , "//
            + A1_CATEGORY + " TEXT , "
            + A1_COLOR + " TEXT , "
            + A1_OBJ_KEY_NAME + " TEXT , "
            //+ A1_OBJ_PATH_NAME + " TEXT , "
            + A1_PNG_KEY_NAME + " TEXT , "
            + A1_TEXTURE_DOWNLOAD_STATUS + " INTEGER DEFAULT 0, "
            + A1_OBJ_DOWNLOAD_STATUS + " INTEGER DEFAULT 0, "
            + A1_PIC_PNG_KEY_NAME + " TEXT , "
            + A2_CATEGORY + " TEXT , "
            + A2_COLOR + " TEXT , "
            + A2_OBJ_KEY_NAME + " TEXT , "
            + A2_PNG_KEY_NAME + " TEXT , "
            + A2_PIC_PNG_KEY_NAME + " TEXT , "
            + A3_CATEGORY + " TEXT , "
            + A3_COLOR + " TEXT , "
            + A3_OBJ_KEY_NAME + " TEXT , "
            + A3_PNG_KEY_NAME + " TEXT , "
            + A3_PIC_PNG_KEY_NAME + " TEXT , "

            + A4_CATEGORY + " TEXT , "
            + A4_COLOR + " TEXT , "
            + A4_OBJ_KEY_NAME + " TEXT , "
            + A4_PNG_KEY_NAME + " TEXT , "
            + A4_PIC_PNG_KEY_NAME + " TEXT , "

            + A5_CATEGORY + " TEXT , "
            + A5_COLOR + " TEXT , "
            + A5_OBJ_KEY_NAME + " TEXT , "
            + A5_PNG_KEY_NAME + " TEXT , "
            + A5_PIC_PNG_KEY_NAME + " TEXT , "
            + A6_CATEGORY + " TEXT , "
            + A6_OBJ_KEY_NAME + " TEXT , "
           // + A6_OBJ_PATH_NAME + " TEXT , "
            + A6_PNG_KEY_NAME + " TEXT , "
            + A6_TEXTURE_DOWNLOAD_STATUS + " INTEGER DEFAULT 0, "
            + A6_OBJ_DOWNLOAD_STATUS + " INTEGER DEFAULT 0, "
            + A6_PIC_PNG_KEY_NAME + " TEXT , "
            + A7_CATEGORY + " TEXT , "
            + A7_OBJ_KEY_NAME + " TEXT , "
            //+ A7_OBJ_PATH_NAME + " TEXT , "
            + A7_PNG_KEY_NAME + " TEXT , "
            + A7_TEXTURE_DOWNLOAD_STATUS + " INTEGER DEFAULT 0, "
            + A7_OBJ_DOWNLOAD_STATUS + " INTEGER DEFAULT 0, "
            + A7_PIC_PNG_KEY_NAME + " TEXT , "
            + A8_CATEGORY + " TEXT , "
            + A8_OBJ_KEY_NAME + " TEXT , "
            //+ A8_OBJ_PATH_NAME + " TEXT , "
            + A8_PNG_KEY_NAME + " TEXT , "
            + A8_TEXTURE_DOWNLOAD_STATUS + " INTEGER DEFAULT 0, "
            + A8_OBJ_DOWNLOAD_STATUS + " INTEGER DEFAULT 0, "
            + A8_PIC_PNG_KEY_NAME + " TEXT , "
            + A9_CATEGORY + " TEXT , "
            + A9_OBJ_KEY_NAME + " TEXT , "
            //+ A9_OBJ_PATH_NAME + " TEXT , "
            + A9_PNG_KEY_NAME + " TEXT , "
            + A9_TEXTURE_DOWNLOAD_STATUS + " INTEGER DEFAULT 0, "
            + A9_OBJ_DOWNLOAD_STATUS + " INTEGER DEFAULT 0, "
            + A9_PIC_PNG_KEY_NAME + " TEXT  ,"

            + A10_CATEGORY + " TEXT , "
            + A10_OBJ_KEY_NAME + " TEXT , "
            //+ A10_OBJ_PATH_NAME + " TEXT , "
            + A10_PNG_KEY_NAME + " TEXT , "
            + A10_TEXTURE_DOWNLOAD_STATUS + " INTEGER DEFAULT 0, "
            + A10_OBJ_DOWNLOAD_STATUS + " INTEGER DEFAULT 0, "
            + A10_PIC_PNG_KEY_NAME + " TEXT  ,"

            + FORCED_RANK + " INTEGER ,"//
            + PUSH_FLAG + " TEXT ," //True
            + VOGUE_FLAG + " TEXT ," //False

            + COMBO_COLOR + " TEXT ,"
            + DESCRIPTION + " TEXT ,"

            + COMBO_LOCAL_IS_LIKED + " INTEGER DEFAULT 0  ,"
            + COMBO_LOCAL_VIEW_COUNT + " INTEGER DEFAULT 0  ,"
            + COMBO_LOCAL_CART_COUNT + " INTEGER DEFAULT 0 , "
            + COMBO_LOCAL_IS_DISPLAY_READY + " INTEGER DEFAULT 0, "
            //+ SEEN_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP , "
            + UPDATED_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP ,"
            + DATE_RECONCILE_IN_MILLI + " INTEGER DEFAULT 0  ,"
            + DATE_SEEN_IN_MILLI + " INTEGER DEFAULT 0  ,"
            + COMBO_LOCAL_SEEN_FACTOR + " INTEGER DEFAULT 0  "
            + ")";

    private static final String SQL_CREATE_TABLE_COMBODATA_LIKE = "CREATE TABLE " + TABLE_COMBODATA_LIKE + "("
            + LEG_ID + " TEXT , "
            + LEG_OBJ_KEY_NAME + " TEXT , "
            //+ LEG_OBJ_PATH_NAME + " TEXT , "
            + LEG_TEX_KEY_NAME + " TEXT , "
            + PB_ID + " TEXT , "
            + FACE_ID + " TEXT , "
            + COMBO_ID + " TEXT PRIMARY KEY, "

            + SKU_ID1 + " TEXT , "
            + SKU_ID6 + " TEXT , "
            + SKU_ID7 + " TEXT , "
            + SKU_ID8 + " TEXT , "
            + SKU_ID9 + " TEXT , "
            + SKU_ID10 + " TEXT , "

            + A6_CATEGORY + " TEXT , "
            + A6_OBJ_KEY_NAME + " TEXT , "
            //+ A6_OBJ_PATH_NAME + " TEXT , "
            + A6_PNG_KEY_NAME + " TEXT , "
            + A6_TEXTURE_DOWNLOAD_STATUS + " INTEGER DEFAULT 0, "
            + A6_OBJ_DOWNLOAD_STATUS + " INTEGER DEFAULT 0, "
            + A6_PIC_PNG_KEY_NAME + " TEXT , "

            + A7_CATEGORY + " TEXT , "
            + A7_OBJ_KEY_NAME + " TEXT , "
            //+ A7_OBJ_PATH_NAME + " TEXT , "
            + A7_PNG_KEY_NAME + " TEXT , "
            + A7_TEXTURE_DOWNLOAD_STATUS + " INTEGER DEFAULT 0, "
            + A7_OBJ_DOWNLOAD_STATUS + " INTEGER DEFAULT 0, "
            + A7_PIC_PNG_KEY_NAME + " TEXT , "

            + A8_CATEGORY + " TEXT , "
            + A8_OBJ_KEY_NAME + " TEXT , "
            //+ A8_OBJ_PATH_NAME + " TEXT , "
            + A8_PNG_KEY_NAME + " TEXT , "
            + A8_TEXTURE_DOWNLOAD_STATUS + " INTEGER DEFAULT 0, "
            + A8_OBJ_DOWNLOAD_STATUS + " INTEGER DEFAULT 0, "
            + A8_PIC_PNG_KEY_NAME + " TEXT , "

            + A9_CATEGORY + " TEXT , "
            + A9_OBJ_KEY_NAME + " TEXT , "
           // + A9_OBJ_PATH_NAME + " TEXT , "
            + A9_PNG_KEY_NAME + " TEXT , "
            + A9_TEXTURE_DOWNLOAD_STATUS + " INTEGER DEFAULT 0, "
            + A9_OBJ_DOWNLOAD_STATUS + " INTEGER DEFAULT 0, "
            + A9_PIC_PNG_KEY_NAME + " TEXT  ,"

            + A10_CATEGORY + " TEXT , "
            + A10_OBJ_KEY_NAME + " TEXT , "
            + A10_PNG_KEY_NAME + " TEXT , "
            + A10_TEXTURE_DOWNLOAD_STATUS + " INTEGER DEFAULT 0, "
            + A10_OBJ_DOWNLOAD_STATUS + " INTEGER DEFAULT 0, "
            + A10_PIC_PNG_KEY_NAME + " TEXT,  "
            + DATE_RECONCILE_IN_MILLI + " INTEGER DEFAULT 0  ,"
            + UPDATED_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP ,"
            + LIKES_COUNT + " INTEGER DEFAULT 0 "
            + ")";


    // SQL statement of the ComboData table creation
    private static final String SQL_CREATE_TABLE_COMBO_LOOKBOARD = "CREATE TABLE " + TABLE_COMBO_LOOKBOARD + "("
            + PB_ID + " TEXT , "
            //+ FACE_ID + " TEXT , "
            + STATUS + " TEXT , "
            + COMPAIGN_ID + " TEXT , "
            + CURATOR_ID + " TEXT , "
            + COMBO_ID + " TEXT PRIMARY KEY, "
            + GENDER + " TEXT , "
            + COMBO_STYLE_CATEGORY + " TEXT , "
            + COMBO_PIC_PNG_KEY_NAME + " TEXT , "
            + FORCED_RANK + " INTEGER ,"//
            + PUSH_FLAG + " TEXT ," //True
            + COMBO_COLOR + " TEXT ,"
            + DESCRIPTION + " TEXT ,"

            + COMBO_LOCAL_VIEW_COUNT + " INTEGER DEFAULT 0  ,"
            + DATE_RECONCILE_IN_MILLI + " INTEGER DEFAULT 0  ,"
            + DATE_SEEN_IN_MILLI + " INTEGER DEFAULT 0  "
            + ")";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

//    public DatabaseHandler( Context context) {
//        super(context, Environment.getExternalStorageDirectory()
//                + File.separator + "inkarne"
//                + File.separator + DATABASE_NAME, null, DATABASE_VERSION);
//    }

    public DatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }



    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(SQL_CREATE_TABLE_USERS);
        database.execSQL(SQL_CREATE_TABLE_COMBODATA);
        database.execSQL(SQL_CREATE_TABLE_COMBO_LOOKBOARD);
        database.execSQL(SQL_CREATE_TABLE_COMBODATA_LIKE);
        database.execSQL(SQL_CREATE_TABLE_USER_AVATAR);
        database.execSQL(SQL_CREATE_TABLE_ACCESSORY);
        database.execSQL(SQL_CREATE_TABLE_LA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading the database from version " + oldVersion + " to " + newVersion);
        switch(oldVersion) {
            case 1: {
                String sql = "ALTER TABLE " + TABLE_USER + " ADD COLUMN " +
                        USER_THUMB_URL + " TEXT";
                db.execSQL(sql);
                db.execSQL(SQL_CREATE_TABLE_COMBO_LOOKBOARD);

                String sql2 = "ALTER TABLE " + TABLE_USER_AVATAR + " ADD COLUMN " +
                        FEDUCIAL_POINTS + " TEXT ";

                String sql3 = "ALTER TABLE " + TABLE_USER_AVATAR + " ADD COLUMN " +
                        IMAGE_SAVED_FILEPATH + " TEXT ";
                String sql4 = "ALTER TABLE " + TABLE_USER_AVATAR + " ADD COLUMN " +
                        IMAGE_RESIZE_RATIO + " TEXT ";
                db.execSQL(sql2);
                db.execSQL(sql3);
                db.execSQL(sql4);
            }
                break;
            case 2: {
                String sql2 = "ALTER TABLE " + TABLE_USER_AVATAR + " ADD COLUMN " +
                        FEDUCIAL_POINTS + " TEXT ";

                String sql3 = "ALTER TABLE " + TABLE_USER_AVATAR + " ADD COLUMN " +
                        IMAGE_SAVED_FILEPATH + " TEXT ";
                String sql4 = "ALTER TABLE " + TABLE_USER_AVATAR + " ADD COLUMN " +
                        IMAGE_RESIZE_RATIO + " TEXT ";
                db.execSQL(sql2);
                db.execSQL(sql3);
                db.execSQL(sql4);
            }
                break;
            default:
                break;

        }
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMBODATA);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMBODATA_LIKE);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMBO_LOOKBOARD);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_AVATAR);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCESSORY);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LA);
        //recreate the tables
//        onCreate(db);
    }
}
