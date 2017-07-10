package com.svc.sml.Database;

import android.content.SharedPreferences;
import android.util.Log;

import com.svc.sml.InkarneAppContext;
import com.svc.sml.Model.FaceItem;
import com.svc.sml.Utility.ConstantsUtil;

import java.io.Serializable;

public class User implements Serializable {

    public static final String TAG = "User";
    private static final long serialVersionUID = -7406082437623008161L;
    public final static String SHARED_PREF_IS_DEFAULT_FACE_CHANGED = "is_default_face_change";
    public String mUserId;
    private String mFirstName;
    private String mLastName;
    private String mGender;
    private String mMobileNumber;
    private String emailId;
    private String dob_dd_mmm_yyyy;
    private int dob_month;
    private int dob_year;
    private int dob_day;
    private String mPIN;
    private int points;

    private String defaultFaceId = "";
    private String PBId;
    private int weight;
    private float height;
    private int waist;
    private int bust;
    private int hip;
    private int isRegistrationComplete =0;
    private String thumbUrl ="";

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    private long id;
    private FaceItem defaultFaceItem;

    public User() {
    }


    private static User instance = null;

//    public static User getNewInstance() {
//        if (instance == null) {
//            instance = new User();
//        }
//        return instance;
//    }

    public boolean isMale(){
        return mGender.equals("m");
    }

    public static User getInstance() {
        if (instance == null) {
            SharedPreferences settings = InkarneAppContext.getAppContext().getSharedPreferences("inkarne", 0);
            String userId = settings.getString("user_id", "");
           if(userId == null || userId.length() == 0){
               //Toast.makeText(InkarneAppContext.getAppContext(), "User: " + userId + " not found", Toast.LENGTH_SHORT).show();
               return null;
           }
            Log.d("User","userId login :"+userId);
            instance = getInstanceOfId(userId);
        }
        return instance;
    }


    public static User getInstanceOfId(String userId) {
        User user = InkarneAppContext.getDataSource().findUser(userId);
        if(user == null) {
            //Toast.makeText(InkarneAppContext.getAppContext(), "New user? Please register.", Toast.LENGTH_SHORT).show();
            //Toast.makeText(InkarneAppContext.getAppContext(), "User: " + userId + " not found in DB", Toast.LENGTH_SHORT).show();
        }
        else {
            if ( user.getDefaultFaceId() != null && user.getDefaultFaceId().length() != 0  && user.getDefaultFaceItem() == null) {
                FaceItem face = InkarneAppContext.getDataSource().getAvatar(user.getDefaultFaceId());
                user.setDefaultFaceItem(face);
            }
            instance = user;
        }
        return instance;
    }

    public void saveUserId(String userId){
        SharedPreferences settings = InkarneAppContext.getAppContext().getSharedPreferences("inkarne", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("user_id", userId);
        editor.commit();
        Log.d("User", "userId saved userId :"+userId);
    }

    public static User setInstance(User user) {
        instance = user;
        return instance;
    }


    public String getmUserId() {
        if (mUserId != null)
            return mUserId;
        else {
            Log.e("User", "User become null..");
            return "4";
//            User user = getInstance();
//            Log.e("User", "User become null..");
//            if(user != null){
//                return mUserId;
//            }
//            else {
//                Log.e("User", "User become null..");
//                return "4";
//            }
        }
    }

    public void setmUserId(String mUserId) {
        this.mUserId = mUserId;
    }

    public String getmFirstName() {
        return mFirstName;
    }

    public void setmFirstName(String mFirstName) {
        this.mFirstName = mFirstName;
    }

    public String getmLastName() {
        return mLastName;
    }

    public void setmLastName(String mLastName) {
        this.mLastName = mLastName;
    }

    public String getmGender() {
        String gender;
        if (mGender != null && mGender.length() != 0)
            return mGender;
        else
            return "m";
    }

    public void setmGender(String mGender) {
        this.mGender = mGender;
    }

    public String getmMobileNumber() {
        return mMobileNumber;
    }

    public void setmMobileNumber(String mMobileNumber) {
        this.mMobileNumber = mMobileNumber;
    }

    public String getmPIN() {
        return mPIN;
    }

    public void setmPIN(String mPIN) {
        this.mPIN = mPIN;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getDob_dd_mmm_yyyy() {
        return dob_dd_mmm_yyyy;
    }

    public void setDob_dd_mmm_yyyy(String dob_dd_mmm_yyyy) {
        this.dob_dd_mmm_yyyy = dob_dd_mmm_yyyy;
    }

    public int getDob_month() {
        return dob_month;
    }

    public void setDob_month(int dob_month) {
        this.dob_month = dob_month;
    }

    public int getDob_year() {
        return dob_year;
    }

    public void setDob_year(int dob_year) {
        this.dob_year = dob_year;
    }

    public int getDob_day() {
        return dob_day;
    }

    public void setDob_day(int dob_day) {
        this.dob_day = dob_day;
    }

    public FaceItem getDefaultFaceItem() {
        if(defaultFaceItem == null){
            defaultFaceItem =  InkarneAppContext.getDataSource().getAvatar(getDefaultFaceId());
        }
        if(defaultFaceItem == null){
            FaceItem face = new FaceItem();
            face.setFaceId("1");
            setDefaultFaceItem(face);
            if(PBId!=null)
            face.setPbId(PBId);
            setDefaultFaceId(face.getFaceId());
            InkarneAppContext.getDataSource().create(face);
            defaultFaceItem = face;
        }
        return defaultFaceItem;
    }

    public void setDefaultFaceItem(FaceItem defaultFaceItem) {
        this.defaultFaceItem = defaultFaceItem;
        if(defaultFaceItem != null) {
            this.setDefaultFaceId(defaultFaceItem.getFaceId());
        }
    }

    public String getPBId() {
        return PBId;
    }

    public void setPBId(String PBId) {
        this.PBId = PBId;
    }

    public String getDefaultFaceId() {
        if(defaultFaceId==null||defaultFaceId.isEmpty()){
            defaultFaceId = InkarneAppContext.getSPValue(ConstantsUtil.SETTING_KEY_DEFAULT_FACE_ID,"1");
        }
        return defaultFaceId;
    }

    public void setDefaultFaceId(String defaultFaceId) {
        if(defaultFaceId!=null && !defaultFaceId.isEmpty()) {
            InkarneAppContext.saveSPValue(ConstantsUtil.SETTING_KEY_DEFAULT_FACE_ID, defaultFaceId);
        }
        this.defaultFaceId = defaultFaceId;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public int getWaist() {
        return waist;
    }

    public void setWaist(int waist) {
        this.waist = waist;
    }

    public int getBust() {
        return bust;
    }

    public void setBust(int bust) {
        this.bust = bust;
    }

    public int getHip() {
        return hip;
    }

    public void setHip(int hip) {
        this.hip = hip;
    }

    public int getIsRegistrationComplete() {
        return isRegistrationComplete;
    }

    public void setIsRegistrationComplete(int isRegistrationComplete) {
        this.isRegistrationComplete = isRegistrationComplete;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}