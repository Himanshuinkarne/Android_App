package com.svc.sml.Utility;

/**
 * Created by himanshu on 5/30/16.
 */
public class ConstantsFunctional {
    public static final int HEIGHT_IN_INCHES_FEMALE=  65;
    public static final int HEIGHT_IN_INCHES_MALE =  69;
    public static final int WEIGHT_IN_KG_FEMALE =  54;
    public static final int WEIGHT_IN_KG_MALE=  64;

    public static final int TIME_REFRESH_INTERVAL_ACCESSORY_INSEC =  300;

    public static final int MIN_SIZE_BODY_MEASUREMENT = 0;
    public static final int MAX_SIZE_BODY_MEASUREMENT = 60 - MIN_SIZE_BODY_MEASUREMENT;// actual +MIN_SIZE_BODY_MEASUREMENT
    public static final int SIZE_BUST_IN_INCHES_FEMALE =  33 - MIN_SIZE_BODY_MEASUREMENT ;
    public static final int SIZE_BUST_IN_INCHES_MALE=  35 - MIN_SIZE_BODY_MEASUREMENT;

    public static final int SIZE_WAIST_IN_INCHES_FEMALE =  30 - MIN_SIZE_BODY_MEASUREMENT;
    public static final int SIZE_WAIST_IN_INCHES_MALE=  30 - MIN_SIZE_BODY_MEASUREMENT;

    public static final int SIZE_HIP_IN_INCHES_FEMALE =  33 - MIN_SIZE_BODY_MEASUREMENT ;
    public static final int SIZE_HIP_IN_INCHES_MALE=  33 - MIN_SIZE_BODY_MEASUREMENT;

    public static final int TIME_WAIT_TO_CANCEL_TO_GET_LAUNCH_DATA_IN_MILLI = 120000;
    public static final int TIME_WAIT_SHARE_POPUP_DISAPPEAR_IN_MILLI = 4000;

    public static final int TIME_INSTRUCTION_VIEW_ANIMATION_IN_MILLI = 500;

    public static final int SIZE_IMAGE_SHARED = 1024;

    public static final String HAIRSTYLE_DEFAULT_MALE = "MHS002";
    public static final String HAIRSTYLE_DEFAULT_FEMALE = "FHS005";

    public static final int PERCENTAGE_OF_FREE_SPACE_FOR_APP = 5;
    public static final int MAX_LIMIT_FREE_SPACE_FOR_APP_IN_MB = 150;//150

    public static final int COUNT_LOOKS_FORYOU = 30;

    public static final int MAX_DIM_OF_UPLOAD_USER_PIC = 1024;//250


}
