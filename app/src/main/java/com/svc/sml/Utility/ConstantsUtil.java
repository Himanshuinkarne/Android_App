package com.svc.sml.Utility;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;

import com.svc.sml.Database.DatabaseHandler;
import com.svc.sml.InkarneAppContext;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by Inkarn on 12/11/2015.
 */
public class ConstantsUtil {

    /*com.svc.inkarne - debug true */
    //public static final String OTPSecretKey = "FVcK6LQzzO73Ksuh5cSBuxe-xyvOebhuWuIwMWhLF140zsYFoNPnXj8ElNK4wejo-vFQEfabREEJeh7wQcSggL0mjuEZh2YCwinj4BA1oVKvciwrxNVLKqkA_-LBEnNX-IDGzYWTJSrn6nf5VwOPxke5ZhJ1WpMfVZ5R_FoGMIY=";

    /*com.svc.sml - signed apk -release  - debug false */
    public static final String OTPSecretKey = "EyNxpriB8oXHoC_tdlgZQXrUnaqGHi3pi4DgZgI5hXm4rkj70KTfi6mGrTgfF9K5saMUo9_vHaxnqIfN30WaeDcL3ohsmr8v4N0vV3UOexh5d-hUmcmUsoskB9qk1A48vpvJg5X1uLJL4cuU3q_8W2ZRU8qTI0Uxv8xGjw37g0M=";
    //public static final String OTPSecretKey = "EyNxpriB8oXHoC_tdlgZQXrUnaqGHi3pi4DgZgI5hXm4rkj70KTfi6mGrTgfF9K5saMUo9_vHaxnqIfN30WaeDcL3ohsmr8v4N0vV3UOexh5d-hUmcmUsoskB9qk1A483wN_btFH6Z82kVT3XzvzO2ZRU8qTI0Uxv8xGjw37g0M=";//com.svc.sml signed apk -debug true
   // FVcK6LQzzO73Ksuh5cSBuxe-xyvOebhuWuIwMWhLF140zsYFoNPnXj8ElNK4wejo-vFQEfabREEJeh7wQcSggGTPhz1l1VEd_AxVQ4wevYkHzr9wevNz3mVbtRhNC9iJD4B424y1wPyrsFHPkKFVvjC8TZED3yvOSiizsVPwGxU=

    /*
          AWS Detail
     */
    public static final String AWSAccessKey = "AKIAJMV73XT4JBUBTIYQ";
    public static final String AWSSecretKey = "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";
    public static final String COGNITO_POOL_ID = "";
    public static final String AWSBucketName = "inkarnestore";

    public static final String URL_BASEPATH_CREATE = "http://styleavatar-prod.ap-southeast-1.elasticbeanstalk.com/Service1.svc/";
    public static final String URL_BASEPATH_CREATE_V2 = "http://styleavatar-prod.ap-southeast-1.elasticbeanstalk.com/Service1.svc/";
    public static final String URL_BASEPATH_0 = "http://styleme-prod.ap-southeast-1.elasticbeanstalk.com/Service1.svc/";
    //public static final String URL_BASEPATH = "http://stylemev2-prod.ap-southeast-1.elasticbeanstalk.com/Service1.svc/";     //22Nov
    public static final String URL_BASEPATH = "http://styleme-prod.ap-southeast-1.elasticbeanstalk.com/Service1.svc/";
    //public static final String URL_BASEPATH_V2 = "http://stylemev2-prod.ap-southeast-1.elasticbeanstalk.com/Service1.svc/";  //22nov
    public static final String URL_BASEPATH_V2 = "http://styleme-prod.ap-southeast-1.elasticbeanstalk.com/Service1.svc/";
    public static final String URL_BASEPATH_feducials = "http://styleimage-prod.ap-southeast-1.elasticbeanstalk.com/Service1.svc/";
    public static final String URL_BASEPATH_VIDEO = "http://style360-prod.ap-southeast-1.elasticbeanstalk.com/Service1.svc/";


    public static final String URL_APP_VERSION_UPDATE = "http://styleavatar-prod.ap-southeast-1.elasticbeanstalk.com/Service1.svc/CheckForceVersionUpdate/Android";
    /* New
     http://styleavatar-prod.ap-southeast-1.elasticbeanstalk.com/Service1.svc/CreateUser/f/angelina/jolie/8/7/1959/angelina@j.com/919686891414/Mumbai/
    ?Mobile_OS_Version=Android_Lollipop_2.0
    &Device_Manufacturer=Samsung&Device_Model=s7&Device_Memory=4GB&Device_Processor=SnapDragon&Network_Type=Wifi

    http://styleme-prod.ap-southeast-1.elasticbeanstalk.com/Service1.svc/CreateUser/f/angelina/jolie/8/7/1959/angelina@j.com/919686891414/Mumbai/
    ?Mobile_OS_Version=Android_Lollipop_2.0&Device_Manufacturer=Samsung&Device_Model=s7&Device_Memory=4GB
    &Device_Internal_Storage=12GB&Device_External_Storage=32GB&Device_Processor=SnapDragon&Network_Type=Wifi
     */
    //public static final String URL_METHOD_CREATEUSER = "CreateUser/";
    public static final String URL_METHOD_CREATEUSER = "CreateUserv4/";

    /*
      http://styleme-prod.ap-southeast-1.elasticbeanstalk.com/Service1.svc/UpgradeUserv4/5/f/test/test/7/6/1977/test@test.com/919923123153/india/?Mobile_OS_Version=6.0.1&Device_Manufacturer=samsung&Device_Model=SM-G930F&Device_Memory=3.48GB&Device_Internal_Storage=24GB&Device_External_Storage=24GB&Device_Processor=aarch64&Network_Type=Wifi
            // &Facebook_ID=10002014&Device_ID=android-20013feas7bcc820c
     */
    public static final String URL_METHOD_UPDATEUSER = "UpgradeUserv4/";
    /*
    http://inkarnepub-prod.elasticbeanstalk.com/Service1.svc/GetReferencePicture/m
    */
    public static final String URL_METHOD_CREATE_REFERENCE_PICS = "GetReferencePicture/";
    /*
    http://styleimage-prod.ap-southeast-1.elasticbeanstalk.com/Service1.svc/CreateOnlyFace/834/f/0/1272/1032/160/1020/885/1284/626/1284/981/1502/530/1507/998/958/477/949/1165/1667/264/1665/756/1850/?Pic_Path=inkarne/users/834/faces/Image-5.png
     */
    public static final String URL_METHOD_CREATEFACE = "CreateOnlyFace/";

    /*
    http://style360-prod.ap-southeast-1.elasticbeanstalk.com/Service1.svc/CreateVideo/4/1/FB020/FC135/ER009/FSH023/FHS001/NA/CL010
    http://style360-prod.ap-southeast-1.elasticbeanstalk.com/Service1.svc/CreateVideo/789/2/FB019/FC1/ER020/FSH014/FHS001/FSG002/FBG021
    */
    public static final String URL_METHOD_CREATE_VIDEO = "CreateVideo/";

    /*
    http://styleimage-prod.ap-southeast-1.elasticbeanstalk.com/Service1.svc/CreateFaceAndHairstyle/834/f/0/FHS001/1272/1032/160/1020/885/1284/626/1284/981/1502/530/1507/998/958/477/949/1165/1667/264/1665/756/1850/?Pic_Path=inkarne/users/834/faces/Image-5.png
     */
    public static final String URL_METHOD_CREATEFACE_AND_HAIR = "CreateFaceAndHairstylev2/";

    /*http://inkarnepub-prod.elasticbeanstalk.com/Service1.svc/GetReferencePicture/m
    Format: GetReferencePicture/{Gender} */
    public static final String URL_METHOD_FEDUCIAL_FACE = "GetReferenceFidicuals/";

    /*http://inkarnepub-prod.elasticbeanstalk.com/Service1.svc/GetReferenceFidicuals/m
     Format: GetReferenceFidicuals/{Gender} */
    public static final String URL_METHOD_FEDUCIAL_POINTS = "GetReferenceFidicuals/";

    /*
    http://inkarnepub-prod.elasticbeanstalk.com/Service1.svc/GetUserFidicuals/227/?Pic_Path=inkarne/users/1/1001.jpg
     */
    public static final String URL_METHOD_USER_FEDUCIAL_POINTS = "GetUserFidicuals/";

    //http://styleavatar-prod.ap-southeast-1.elasticbeanstalk.com/Service1.svc/CreateBody/4/f/1/68.0/60/33/40/44/1
    /*http://inkarnepub-prod.elasticbeanstalk.com/Service1.svc/CreateBody/5/m/1/67/60/32/30/36
    Format: CreateBody/{User_ID}/{Gender}/{Face_ID}/{Height_Inches}/{Weight_Inches}/{Bust_Circumference_Inches}/{Waist_Circumference_Inches}/{Hips_Circumference_Inches} */
    public static final String URL_METHOD_CREATE_BODY = "CreateBodyV2/";

    /*http://inkarnepub-prod.elasticbeanstalk.com/Service1.svc/CreateLegs/5/m/1/MB067
     Format: User_ID/Gender/Face_ID/PB_ID
     */
    public static final String URL_METHOD_CREATE_LEGS = "CreateLegsV2/";

    /*http://inkarne-prod.elasticbeanstalk.com/Service1.svc/ReconcileHairstyleData/4/m
    */
    public static final String URL_METHOD_HAIRLIST = "ReconcileHairstyleData/";
    public static final String URL_METHOD_SPECSLIST = "ReconcileSpecsData/"; //sample http://inkarnepub-prod.elasticbeanstalk.com/Service1.svc/ReconcileSpecsData/4/m
    public static final String URL_METHOD_EARRINGS = "ReconcileEarringsData/"; //sample http://inkarnepub-prod.elasticbeanstalk.com/Service1.svc/ReconcileSpecsData/4/m
    public static final String URL_METHOD_SHOES = "ReconcileShoesDatav2/"; //sample http://inkarnepub-prod.elasticbeanstalk.com/Service1.svc/ReconcileSpecsData/4/m
    public static final String URL_METHOD_CLUTCHES = "ReconcileClutchesDatav2/"; //sample http://inkarnepub-prod.elasticbeanstalk.com/Service1.svc/ReconcileSpecsData/4/m
    public static final String URL_METHOD_BAGS = "ReconcileBagsDatav2/"; //sample http://inkarnepub-prod.elasticbeanstalk.com/Service1.svc/ReconcileSpecsData/4/m
    public static final String URL_METHOD_SUNGLASSES = "ReconcilesunglassesData/";

    public static final String URL_METHOD_RECONCILE = "ReconcileComboData/";//sample http://inkarnepub-prod.elasticbeanstalk.com/Service1.svc/ReconcileComboData/4 <userid>
    public static final String URL_METHOD_GETCOMBOSKUDATA = "GetComboSKUData/";   // sample http://inkarnepub-prod.elasticbeanstalk.com/Service1.svc/GetComboSKUData/4/100 <userid/count>
    public static final String URL_METHOD_GETCOMBOSKUINFO_BY_ID = "GetComboSKUInfoV2/"; //sample http://inkarnepub-prod.elasticbeanstalk.com/Service1.svc/GetComboSKUInfo/4/FC01


    /*
      Format : {User_ID}
      http://styleme-prod.ap-southeast-1.elasticbeanstalk.com/Service1.svc/GetCartData/6
     */
    public static final String URL_METHOD_GET_CARTS =  "GetCartData/";

    /*
    User_ID/Video_ID
    http://styleme-prod.ap-southeast-1.elasticbeanstalk.com/Service1.svc/DeleteVideo/2/2
    */
    public static final String URL_METHOD_DELETE_VIDEO = "DeleteVideo/";

    /*
    User_ID/Face_ID
    http://styleme-prod.ap-southeast-1.elasticbeanstalk.com/Service1.svc/DeleteFace/2/2
    */
    public static final String URL_METHOD_DELETE_FACE = "DeleteFace/";

    /*
    http://inkarnepub-prod.elasticbeanstalk.com/Service1.svc/UpdateCart/4/F081M1
    Format: UpdateCart/{User_ID}/{Combo_ID}/{Purchase_SKU_ID}
     */
    public static final String URL_METHOD_UPDATE_CART = "UpdateCart/";
    public static final String URL_METHOD_UPDATE_BUY = "UpdateBuy/"; //sample http://inkarnepub-prod.elasticbeanstalk.com/Service1.svc/UpdateBuy/4/F081M1/0
    /*
    http://styleme-prod.ap-southeast-1.elasticbeanstalk.com/Service1.svc/UpdateShares/789/FC1/ER020/FSH014/FHS001/FSG001/CL002/Whatsapp/VIDEO/
    UpdateShares/{User_ID}/{Combo_ID}/{SKU_ID6}/{SKU_ID7}/{SKU_ID8}/{SKU_ID9}/{SKU_ID10}/{Share_Medium}/{Share_Type}/
     */
    public static final String URL_METHOD_UPDATE_SHARE = "UpdateShares/";
    /*
         http://inkarnepub-prod.elasticbeanstalk.com/Service1.svc/UpdateLikes/4/FC01/0
     */
    public static final String URL_METHOD_UPDATE_LIKE = "UpdateLikes/";
    /*
    http://styleimage-prod.ap-southeast-1.elasticbeanstalk.com/Service1.svc/GetLikesData/FC2
     */
    public static final String URL_METHOD_UPDATE_LIKE_COUNT = "GetLikesData/";
    /*
    http://styleme-prod.ap-southeast-1.elasticbeanstalk.com/Service1.svc/UpdateViewAccessory/1/ER001
    User_ID/SKU_ID
    */
    public static final String URL_METHOD_UPDATE_MIXMATCH_SLECTION = "UpdateViewAccessory/";

    /*
    http://styleme-prod.ap-southeast-1.elasticbeanstalk.com/Service1.svc/UpdateEngagementTime/789/FC1/?Start_Timestamp=2011-01-01%2000:00:00.000&End_Timestamp=2011-01-01%2000:00:01.400
    UpdateEngagementTime/{User_ID}/{Combo_ID}/?Start_Timestamp={Start_Timestamp}&End_Timestamp={End_Timestamp}
    Format of DateTime string: YYYY-mm-dd HH:MM:SS.sss
    */
    public static final String URL_METHOD_UPDATE_ENGAGEMENT_TIME = "UpdateEngagementTime/";

    public static final String URL_METHOD_LOOKALIKE = "GetLookalikes/";   //sample http://inkarnepub-prod.elasticbeanstalk.com/Service1.svc/GetLookalikes/4/F032M <userid/skuid>

    //http://inkarnepub-prod.elasticbeanstalk.com/Service1.svc/CreateHairstyle/5/36/MHS002/m
    //Format: CreateHairstyle/{User_ID}/{Face_ID}/{Hairstyle_ID}/{Gender}
    public static final String URL_METHOD_CREATE_HAIRSTYLE = "CreateHairstyleV2/";

    /*
    http://styleme-prod.ap-southeast-1.elasticbeanstalk.com/Service1.svc/CreateShoes/2/FB020/FSH008
    */

    public static final String URL_METHOD_CREATE_SHOES = "CreateShoesV2/";

    /*
    http://inkarnepub-prod.elasticbeanstalk.com/Service1.svc/CreateSpecs/5/36/MSP005/m
    Format: CreateSpecs/{User_ID}/{Face_ID}/{Specs_ID}/{Gender}
    */
    public static final String URL_METHOD_CREATE_SPECS = "CreateSpecsV2/";

    /*

     public static final String URL_METHOD_CREATE_SPECS = "CreateSpecs/";

    http://inkarnepub-prod.elasticbeanstalk.com/Service1.svc/CreateEarrings/5/145/ER001/F
    CreateEarrings/{User_ID}/{Face_ID}/{Earring_ID}/{Gender}
    */
    public static final String URL_METHOD_CREATE_EARRINGS = "CreateEarringsV2/";

    public static final String URL_METHOD_CREATE_CLUTCHES = "CreateClutches/";
    /*
    http://inkarnepub-prod.elasticbeanstalk.com/Service1.svc/CreateSunglasses/5/145/FSG008/F
     */
    public static final String URL_METHOD_CREATE_SUNGLASSES = "CreateSunglassesV2/";

    /*
     http://styleme-prod.ap-southeast-1.elasticbeanstalk.com/Service1.svc/UpdateDefaultFace/789/2
     UpdateDefaultFace/User_ID/Face_ID
     */
    public static final String URL_METHOD_UPDATE_DEFAULT_FACE = "UpdateDefaultFace/";

    /*
    http://styleme-prod.ap-southeast-1.elasticbeanstalk.com/Service1.svc/UpdateDefaultHairstyle/789/FHS001
    UpdateDefaultHairstyle/User_ID/Hairstyle_ID
     */
    public static final String URL_METHOD_UPDATE_DEFAULT_HAIRSTYLE = "UpdateDefaultHairstyle/";

    /*
    http://styleme-prod.ap-southeast-1.elasticbeanstalk.com/Service1.svc/UpdateDefaultSpecs/789/FSP001
    UpdateDefaultSpecs/User_ID/Specs_ID
   */
    public static final String URL_METHOD_UPDATE_DEFAULT_SPECS = "UpdateDefaultSpecs/";

    /*
    Format of DateTime string: YYYY-mm-dd HH:MM:SS.sss
    http://styleme-prod.ap-southeast-1.elasticbeanstalk.com/Service1.svc/UpdateEngagementTime/789/FC1/?Start_Timestamp=2011-01-01%2000:00:00.000&End_Timestamp=2011-01-01%2000:00:01.400
    UpdateEngagementTime/{User_ID}/{Combo_ID}/?Start_Timestamp={Start_Timestamp}&End_Timestamp={End_Timestamp}
    */
    public static final String URL_METHOD_UPDATE_COMBO_ENGAGEMENT_TIME = "UpdateEngagementTime/";

    /*
    http://styleme-prod.ap-southeast-1.elasticbeanstalk.com/Service1.svc/UpdatePoints/5/2500
    */
    public static final String URL_METHOD_UPDATE_POINTS = "UpdatePoints/";

    /*
    http://styleme-prod.ap-southeast-1.elasticbeanstalk.com/Service1.svc/ReconcileCampaignDatav3/0/F
     */
    public static final String URL_METHOD_RECONCILE_CAMPAIGN = "ReconcileCampaignDatav3/";

    public static final String MESSAGE_TOAST_NETWORK_RESPONSE_FAILED = "Please check your network connection.";
    public static final String MESSAGE_ALERT_NETWORK_RESPONSE_FAILED = "Please check your network connection.";

    /*Messages */
    public static final String TEXT_ON_SHARED_PICS = "Inkarne.com";

    public static int COUNT_SKUS_IN_COMBO = 11; //10 + 2(legs)

    public static final int MAX_COUNT_LOOKS_BITMAPS = 40;

    public static String[] ARRAY_DAYS = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
            "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
            "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"};
    public static String[] ARRAY_MONTHS = {"JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE", "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"};

    public static String[] ARRAY_YEARS = {"1930", "1931", "1932", "1933", "1934", "1935", "1936", "1937", "1938", "1939", "1940",
            "1941", "1942", "1943", "1944", "1945", "1946", "1947", "1948", "1949", "1950",
            "1951", "1952", "1953", "1954", "1955", "1956", "1957", "1958", "1959", "1960",
            "1961", "1962", "1963", "1964", "1965", "1966", "1967", "1968", "1969", "1970",
            "1971", "1972", "1973", "1974", "1975", "1976", "1977", "1978", "1979", "1980",
            "1981", "1982", "1983", "1984", "1985", "1986", "1987", "1988", "1989", "1990",
            "1991", "1992", "1993", "1994", "1995"};

    public static String[] ARRAY_WEIGHT = {"20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30",
            "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50",
            "51", "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62", "63", "64", "65", "66", "67", "68", "69", "70",
            "71", "72", "73", "74", "75", "76", "77", "78", "79", "80", "81", "82", "83", "84", "85", "86", "89", "90",
            "91", "92", "93", "94", "95", "96", "97", "98", "99", "100"};
    public static String[] ARRAY_HEIGHT_FT = {"4", "5", "6", "7", "8"};
    public static String[] ARRAY_HEIGHT_INCH = {"0.0","0.5", "1.0", "1.5", "2.0", "2.5", "3.0", "3.5", "4.0", "4.5", "5.0", "5.5", "6.0", "6.5",
            "7.0", "7.5", "8.0", "8.5", "9.0", "9.5", "10.0", "10.5", "11.0", "11.5"};

    public static final String FILE_PATH_VISAGE_GALLERY_IMAGE = "/inkarne/featured_points";
    public static final String FILE_PATH_VISAGE_SELFI_IMAGE = "/inkarne/featured_points/camera";
    public static final String FILE_PATH_AWS_KEY_ROOT = "inkarne/users/";
    public static final String FILE_PATH_RAW_FOLDER = "android.resource://";
    public static final String FILE_PATH_APP_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/" + ".inkarne_root/";
    public static final String FILE_PATH_APP_ROOT_VIDEO = Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/" ;//+ "inkarne_root/"
    public static final String FILE_PATH_SHARE = "inkarne/share/";
    public static final String FILE_NAME_SHARE = "sharepics";
    public static final String VIDEO_SHARE_FILENAME = "mylookwe.mp4";
    public static final int VIDEO_SHARE_FRAMES = 6;

    public static final int GL_INDEX_BG = 0;
    public static final int GL_INDEX_FACE = 2;
    public static final int GL_INDEX_HAIR_A8 = 8;//11;
    public static final int GL_INDEX_SPECS_A9 = 9;//12;
    public static final int GL_INDEX_BODY = 1;
    public static final int GL_INDEX_LEGS = 3;
    public static final int GL_INDEX_A1_BOTTOM = 4;
    //    public static final int GL_INDEX_A2_TOP = 5;
//    public static final int GL_INDEX_A3_BOTTOM = 6;
//    public static final int GL_INDEX_A4_TOP = 7;
    public static final int GL_INDEX_A5_BOTTOM = 5;//8;
    public static final int GL_INDEX_A7_EARRINGS = 7;//10;
    public static final int GL_INDEX_A6_SHOES = 6;//9;
    public static final int GL_INDEX_A10_BAGS_CLUTCHES = 10;//13;
    public static final int GL_INDEX_TOTAL = 11;


    public static final String[] arrayProductSortByOption = {"Relevance", "Price (High to Low)", "Price (Low to High)"};

   // public static final List<String> arrayListLooksLabelName = Arrays.asList("TRENDING", "FOR YOU", "CASUAL", "PARTY", "FORMAL", "INDO-WESTERN", "SPORTS", "YOUR LIKES", "RECENT HISTORY");
    public static final List<String> arrayListLooksLabelName = Arrays.asList("Trending", "Casual", "Party", "Formal", "Indo-western", "Sports", "For You","Your likes", "History");

    //public static final List<String> arrayListLooksCategory = Arrays.asList("Vogue_Flag", "StyleRating", "CASUAL", "PARTY", "FORMAL", "INDO-WESTERN", "SPORTS", "isLiked", DatabaseHandler.DATE_SEEN_IN_MILLI);
    public static final List<String> arrayListLooksCategory = Arrays.asList("Trending", "CASUAL", "PARTY", "FORMAL", "INDO-WESTERN", "SPORTS", "StyleRating", "isLiked", "History");
    public static final List<String> arrayListCategoryStyle = new ArrayList<String>(arrayListLooksCategory.subList(2, 7)); //Arrays.asList("INDO-WESTERN","CASUAL","SPORTS","FORMAL","PARTY");
    public static final int COUNT_RETRY_SERVICE_CRITICAL = 5;
    public static final int COUNT_RETRY_SERVICE = 2;

    public static final String TYPE_ACCESSORY_AS_BODY_RELATED = "ACCESSORY_AS_BODY_RELATED";
    public static final String TYPE_ACCESSORY_AS_FACE_RELATED = "ACCESSORY_AS_FACE_RELATED";
    public static final String SETTING_KEY_DEFAULT_FACE_ID = "setting_key_default_face_id";
    public static final String SETTING_KEY_IS_DOWNLOAD_WIFI_ONLY = "is_download_wifi_only";
    public static final String SETTING_KEY_IS_AUTO_ROTATE_LOOK = "is_autorotate_Look";
    public static final String SETTING_KEY_CART_NUMBER = "cart_number";
    public static final String SETTING_KEY_COUNT_INSTRUCTION_ADD_FACE_SHOWN = "count_inst_addface_shown";
    public static final String SETTING_KEY_COUNT_INSTRUCTION_SHOP_ACTIVITY = "count_inst_shop_shown";
    public static final String SETTING_KEY_IS_OLD_FOLDER_DELETED_ON_INSTALL = "count_inst_addface_shown";

    public static HashMap HASH_MAP_ACCESSORY = new HashMap() {
        {
            put(EAccessoryType.eAccTypeBags.toString(), new String[]{URL_METHOD_BAGS, "ReconcileBagsMasterInfoV2Result", ""});
            put(EAccessoryType.eAccTypeClutches.toString(), new String[]{URL_METHOD_CLUTCHES, "ReconcileClutchesMasterInfoV2Result", TYPE_ACCESSORY_AS_BODY_RELATED});
            put(EAccessoryType.eAccTypeShoes.toString(), new String[]{URL_METHOD_SHOES, "ReconcileShoesMasterInfoV2Result", TYPE_ACCESSORY_AS_BODY_RELATED});
            put(EAccessoryType.eAccTypeEarrings.toString(), new String[]{URL_METHOD_EARRINGS, "ReconcileEarringsMasterInfoResult", TYPE_ACCESSORY_AS_FACE_RELATED});
            put(EAccessoryType.eAccTypeSunglasses.toString(), new String[]{URL_METHOD_SUNGLASSES, "ReconcileSunglassesMasterInfoResult", TYPE_ACCESSORY_AS_FACE_RELATED});
            put(EAccessoryType.eAccTypeSpecs.toString(), new String[]{URL_METHOD_SPECSLIST, "ReconcileSpecsMasterInfoResult", TYPE_ACCESSORY_AS_FACE_RELATED});
            put(EAccessoryType.eAccTypeHair.toString(), new String[]{URL_METHOD_HAIRLIST, "ReconcileHairstyleMasterInfoResult", TYPE_ACCESSORY_AS_FACE_RELATED});
            put("key2", "value2");
        }
    };

    public static String[] getHASH_MAP_ACCESSORY(String accessoryType) {
        String[] array = (String[]) HASH_MAP_ACCESSORY.get(accessoryType);
        return array;
    }

    public static HashMap HASH_MAP_UPDATE_TYPE = new HashMap() {
        {
            put(EUpdateType.eUpdateTypeLike.toString(), new String[]{URL_METHOD_UPDATE_LIKE, "UpdateLikesResult"});
            put(EUpdateType.eUpdateTypeLikeCount.toString(), new String[]{URL_METHOD_UPDATE_LIKE_COUNT, "GetLikesDataResult"});
            put(EUpdateType.eUpdateTypeCart.toString(), new String[]{URL_METHOD_UPDATE_CART, "UpdateCartResult"});
            put(EUpdateType.eUpdateTypeBuy.toString(), new String[]{URL_METHOD_UPDATE_BUY, "UpdateBuyResult"});
            put(EUpdateType.eUpdateTypePoints.toString(), new String[]{URL_METHOD_UPDATE_BUY, "UpdatePointsResult"});
            put(EUpdateType.eUpdateTypeViewAccessory.toString(), new String[]{URL_METHOD_UPDATE_MIXMATCH_SLECTION, "UpdateViewAccessoryResult"});
            put(EUpdateType.eUpdateTypeShare.toString(), new String[]{URL_METHOD_UPDATE_SHARE, "UpdateSharesResult"});
            put(EUpdateType.eUpdateTypeEngagement.toString(), new String[]{URL_METHOD_UPDATE_ENGAGEMENT_TIME, "UpdateEngagementTimeResult"});
        }
    };

    public static String[] getHASH_MAP_UPDATE_TYPE(String updateType) {
        String[] array = (String[]) HASH_MAP_UPDATE_TYPE.get(updateType);
        return array;
    }

    public static boolean isAccessoryBodyRelated(String accessoryType) {
        return accessoryType.equals(EAccessoryType.eAccTypeShoes.toString());
    }

    public static boolean isAccessoryFaceRelated(String accessoryType) {
        return accessoryType.equals(EAccessoryType.eAccTypeEarrings.toString())
                || accessoryType.equals(EAccessoryType.eAccTypeSunglasses.toString())
                || accessoryType.equals(EAccessoryType.eAccTypeHair.toString())
                || accessoryType.equals(EAccessoryType.eAccTypeSpecs.toString());
    }

    public static boolean isAccessoryBodyFaceRelated(String accessoryType) {
        return accessoryType.equals(EAccessoryType.eAccTypeLegs.toString());
    }

    public static boolean isAccessoryIndependent(String accessoryType) {
        return accessoryType.equals(EAccessoryType.eAccTypeBags.toString())
                || accessoryType.equals(EAccessoryType.eAccTypeClutches.toString());
    }

//    public static void deleteDirectory1(File fileOrDirectory) {
//        Log.d("ConstantsUtil", "delete :" + fileOrDirectory.toString());
//        if (fileOrDirectory == null || !fileOrDirectory.exists()) {
//            //Log.d("ConstantsUtil", "deleteDirectory return"+ fileOrDirectory);
//            return;
//        }
//        File [] files = fileOrDirectory.listFiles();
//        if (fileOrDirectory.isDirectory() && files != null) {
//            for (File child : files)
//                deleteDirectory(child);
//        }
//        fileOrDirectory.delete();
//    }
//
//    public static void deleteDirectory1(File fileOrDirectory,String fileExt) {
//        Log.d("ConstantsUtil", "delete :" + fileOrDirectory.toString());
//        if (fileOrDirectory == null || !fileOrDirectory.exists()) {
//            //Log.d("ConstantsUtil", "deleteDirectory return"+ fileOrDirectory);
//            return;
//        }
//        if(!fileOrDirectory.isDirectory()){
//            String filepath = fileOrDirectory.getAbsolutePath();
//            if(filepath.substring(filepath.lastIndexOf(".")).equals(fileExt)){
//                fileOrDirectory.delete();
//            }
//            return;
//        }
//
//        File [] files = fileOrDirectory.listFiles();
//        if (fileOrDirectory.isDirectory() && files != null) {
//            for (File child : files)
//                deleteDirectory(child);
//        }
//        fileOrDirectory.delete();
//    }


    public static boolean deleteDirectory(File path) {
        Log.d("***deleteDirectory",path.getAbsolutePath());
        String pathstr = path.getAbsolutePath();
        if(pathstr.equals(ConstantsUtil.FILE_PATH_APP_ROOT) || pathstr.equals("/storage/emulated/0/.inkarne_root")){
            return false;
        }
        if (path != null && path.exists()) {
            if(!path.isDirectory()){
                path.delete();
                return true;
            }
            File[] files = path.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isDirectory()) {
                        deleteDirectory(files[i]);
                    } else {
                        files[i].delete();
                    }
                }
            }
            return path.delete();
        }
      return false;
    }

    public static boolean deleteDirectory(File path,String fileExtension) {
        if (path != null && path.exists()) {
            if(!path.isDirectory()){
                String filepath = path.getAbsolutePath();
                if(filepath.substring(filepath.lastIndexOf(".")).equals(fileExtension));
                path.delete();
                return true;
            }

            File[] files = path.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isDirectory()) {
                        deleteDirectory(files[i], fileExtension);
                    } else {
                        String filepath = files[i].getAbsolutePath();
                        if (filepath.substring(filepath.lastIndexOf(".")).equals(fileExtension)) ;
                        files[i].delete();
                    }
                }
            }
            return path.delete();
        }
       return false;
    }


    public static boolean checkFileKeysExist(String... fileNames) {
        if(fileNames == null){
            return false;
        }
        for (int i = 0; i < fileNames.length; i++) {
            if (!checkFileKeyExist(fileNames[i])) {
                return false;
            }
        }
        return true;
    }


    public static boolean checkFileExist(String filePath) {
        boolean isExist = false;

        if (filePath != null && filePath.length() > 0) {
            File fileObj = new File(filePath);
            if (fileObj.exists() && fileObj.isFile()) {
                isExist = true;
            }
        }
        return isExist;
    }

    public static boolean checkFileKeyExist(String fileKey) {
        boolean isExist = false;

        if (fileKey != null && fileKey.length() > 0) {
            String filePath = ConstantsUtil.FILE_PATH_APP_ROOT + fileKey;
            File fileObj = new File(filePath);
            if (fileObj.exists() && fileObj.isFile()) {
                isExist = true;
            }
        }
        return isExist;
    }


    public static String[] getArrayDays() {
        return ConstantsUtil.ARRAY_DAYS;
    }

    public static String[] getArrayDays(String month) {
        //ArrayList<String> ARRAY_DAYS = new ArrayList<String>();
        return ConstantsUtil.ARRAY_DAYS;
    }

    public static String[] getArrayDays(String month, String year) {
        return ConstantsUtil.ARRAY_DAYS;
    }

    public static String[] getArrayYears(String day, String month) {
        //if day is 29, month Feb
        ArrayList<String> days = new ArrayList<String>();
        return ConstantsUtil.ARRAY_YEARS;
    }

    public static String[] getArrayYear() {
        //if day is 29, month Feb
        ArrayList<String> days = new ArrayList<String>();
        return ConstantsUtil.ARRAY_YEARS;
    }

    public static String[] getArrayMonths() {
        //if day is 31 and 30 - no Feb
        //if day is 31 not many months
        ArrayList<String> days = new ArrayList<String>();
        return ConstantsUtil.ARRAY_MONTHS;
    }

    public static String[] getArrayMonths(String day) {
        //if day is 31 and 30 - no Feb
        //if day is 31 not many months
        ArrayList<String> days = new ArrayList<String>();
        return ConstantsUtil.ARRAY_MONTHS;
    }

    public static String[] getArrayMonths(String day, String year) {
        //if day is 31 and 30 - no Feb
        //if day is 31 not many months
        ArrayList<String> days = new ArrayList<String>();
        return ConstantsUtil.ARRAY_MONTHS;
    }


    public static String getFileNameForS3Upload(String filepath) {
        if (filepath == null)
            return "";

        String[] array = filepath.split("/");
        if (array.length > 0) {
            Random r = new Random();
            int i1 = r.nextInt(899) + 100;
            //return "" + i1 + array[array.length - 1];
            return "" + array[array.length - 1];
        }
        return "";
    }

    public enum EDialFilter {
        eDailUnselected("Dail_Unselected"),//
        eDailLike(DatabaseHandler.LIKES_COUNT),
        eDailCart(DatabaseHandler.COMBO_LOCAL_CART_COUNT),
        eDailHistory("HISTORY"),//
        eDailLookOfday(DatabaseHandler.PUSH_FLAG),
        eDailDeals(DatabaseHandler.VOGUE_FLAG),
        eDailCasual("Casual"),
        eDailFormal("Formal"),
        eDailIndowestern("IndoWestern"),
        eDailParty("Party"),
        eDailSports("Sports"),
        eDailUpdateAvatar("Update Avatar");

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

        EDialFilter(String s) {
            name = s;
        }

        public boolean equalsName(String otherName) {
            return otherName != null && name.equals(otherName);
        }

        public String toString() {
            return this.name;
        }
    }

    public enum EDownloadType {
        eDownloadTypeObj("obj"),//
        eDownloadTypeTexture("png_texture"),
        eDownloadTypeThumbnail("png_thumbnail"),
        eDownloadTypeObj2("obj2"),//
        eDownloadTypeTexture2("png_texture2"),
        eDownloadTypeThumbnail2("png_thumbnail2");
        private final String name;

        EDownloadType(String s) {
            name = s;
        }

        public boolean equalsName(String otherName) {
            return otherName != null && name.equals(otherName);
        }

        public String toString() {
            return this.name;
        }
    }

    public enum EAccessoryType {
        eAccTypeHair("hairstyle"),//
        eAccTypeSpecs("specs"),
        eAccTypeEarrings("Earrings"),
        eAccTypeShoes("Shoes"),
        eAccTypeSunglasses("Sunglasses"),
        eAccTypeBags("Bags"),
        eAccTypeClutches("Clutches"),
        eAccTypeLegs("legs");

        private final String name;

        EAccessoryType(String s) {
            name = s;
        }

        public boolean equalsName(String otherName) {
            return otherName != null && name.equals(otherName);
        }

        public String toString() {
            return this.name;
        }
    }

    public enum EUpdateType {
        eUpdateTypeLike("updateLike"),//
        eUpdateTypeLikeCount("updateLikeCount"),
        eUpdateTypeCart("updateCart"),
        eUpdateTypeBuy("updateBuy"),
        eUpdateTypeShare("updateShare"),
        eUpdateTypeEngagement("UpdateEngagementTimeResult"),
        eUpdateTypeViewAccessory("updateViewAccessory"),
        eUpdateTypePoints("updatePoints");//UpdatePointsResult

        private final String name;

        EUpdateType(String s) {
            name = s;
        }

        public boolean equalsName(String otherName) {
            return otherName != null && name.equals(otherName);
        }

        public String toString() {
            return this.name;
        }
    }

    public enum EShareType {
        eShareTypePicture("picture"),//
        eShareTypeVideo("video");

        private final String name;

        EShareType(String s) {
            name = s;
        }

        public boolean equalsName(String otherName) {
            return otherName != null && name.equals(otherName);
        }

        public String toString() {
            return this.name;
        }
    }

    public enum EDownloadStatusType {
        eDownloadError(-1),
        eDownloadTobeStarted(0),//
        eDownloading(1),
        eDownloaded(2),
        eDownloadNoToBe(3);

        private final int status;

        EDownloadStatusType(int s) {
            status = s;
        }

        public int intStatus() {
            return this.status;
        }
    }

    public static String getEncodeTobase64(Bitmap image) {
        Bitmap immagex = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        Log.e("LOOK", imageEncoded);
        return imageEncoded;
    }


    public int getDpFromPx(int px) {
        DisplayMetrics displayMetrics = InkarneAppContext.getAppContext().getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    public static int getPxFromDp(int dp) {
        DisplayMetrics displayMetrics = InkarneAppContext.getAppContext().getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }


    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static Bitmap decodeSampledBitmapFromFile(String filename,
                                                     int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filename, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filename, options);
    }

    public static long getFolderSize(File directory) {
        Log.d("ConstantsUtil", "getFolderSize : " + directory);
        long length = 0;
        if(directory == null || !directory.exists() || directory.listFiles()== null)
            return 0;

        for (File file : directory.listFiles()) {
            if (file != null && file.isFile())
                length += file.length();
            else
                length += getFolderSize(file);
        }
        return length;
    }


    public static float parseFloatTest(String f) {
        final int len   = f.length();
        float     ret   = 0f;         // return value
        int       pos   = 0;          // read pointer position
        int       part  = 0;          // the current part (int, float and sci parts of the number)
        boolean   neg   = false;      // true if part is a negative number

        // find start
        while (pos < len && (f.charAt(pos) < '0' || f.charAt(pos) > '9') && f.charAt(pos) != '-' && f.charAt(pos) != '.')
            pos++;

        // sign
        if (f.charAt(pos) == '-') {
            neg = true;
            pos++;
        }

        // integer part
        while (pos < len && !(f.charAt(pos) > '9' || f.charAt(pos) < '0'))
            part = part*10 + (f.charAt(pos++) - '0');
        ret = neg ? (float)(part*-1) : (float)part;

        // float part
        if (pos < len && f.charAt(pos) == '.') {
            pos++;
            int mul = 1;
            part = 0;
            while (pos < len && !(f.charAt(pos) > '9' || f.charAt(pos) < '0')) {
                part = part*10 + (f.charAt(pos) - '0');
                mul*=10; pos++;
            }
            ret = neg ? ret - (float)part / (float)mul : ret + (float)part / (float)mul;
        }

        // scientific part
        if (pos < len && (f.charAt(pos) == 'e' || f.charAt(pos) == 'E')) {
            pos++;
            neg = (f.charAt(pos) == '-'); pos++;
            part = 0;
            while (pos < len && !(f.charAt(pos) > '9' || f.charAt(pos) < '0')) {
                part = part*10 + (f.charAt(pos++) - '0');
            }
            if (neg)
                ret = ret / (float)Math.pow(10, part);
            else
                ret = ret * (float)Math.pow(10, part);
        }
        return ret;
    }

}
