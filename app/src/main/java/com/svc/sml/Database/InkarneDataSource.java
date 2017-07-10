package com.svc.sml.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.svc.sml.InkarneAppContext;
import com.svc.sml.Model.BaseAccessoryItem;
import com.svc.sml.Model.FaceItem;
import com.svc.sml.Model.LookBoardItem;
import com.svc.sml.Utility.ConstantsUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class InkarneDataSource {

    public static final String LOGTAG = "Inkarne Database";

    private static InkarneDataSource inkarneDataSource;
    private SQLiteOpenHelper dbhelper;
    private SQLiteDatabase database;

    //TODO : CHANGE ON CALL OF THIS FUNTIONS..USE APPLICATION CONTEXT OR ACTIVITY CONTEXT... REMOVE HARDCORDING HERE
    private InkarneDataSource(Context context) { //private TODO
        dbhelper = new DatabaseHandler(InkarneAppContext.getAppContext());
    }

    public synchronized static InkarneDataSource getInstance(Context c) {
        if (inkarneDataSource == null) {
            inkarneDataSource = new InkarneDataSource(c);
        }
        return inkarneDataSource;
    }

    public synchronized void open() {
        Log.i(LOGTAG, "Database opened");
        database = dbhelper.getWritableDatabase();

    }

//    public DatabaseHelper(Context context)
//    {
//        super(context, dbName, null,Increment the value here);
//    }

    public void close() {
        Log.i(LOGTAG, "Database closed");
        dbhelper.close();
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }


    public void setDatabase(SQLiteDatabase database) {
        this.database = database;
    }

    private static final String[] userColumn = {
            DatabaseHandler.USER_ID,
            DatabaseHandler.FIRST_NAME,
            DatabaseHandler.LAST_NAME,
            DatabaseHandler.EMAIL_ID,
            DatabaseHandler.DOB,
            DatabaseHandler.DOB_DAY,
            DatabaseHandler.DOB_MONTH,
            DatabaseHandler.DOB_YEAR,
            DatabaseHandler.MOBILE_NUMBER,
            DatabaseHandler.GENDER,
            DatabaseHandler.PIN,
            DatabaseHandler.PB_ID,
            DatabaseHandler.IS_REGISTRATION_COMPLETE,
            DatabaseHandler.WEIGHT,
            DatabaseHandler.HEIGHT,
            DatabaseHandler.BUST_CIRCUM,
            DatabaseHandler.HIPS_CIRCUM,
            DatabaseHandler.WAIST_CIRCUM,
            DatabaseHandler.POINTS,
            DatabaseHandler.USER_THUMB_URL,
            DatabaseHandler.DEFAULT_FACE_ID

    };

    private static final String[] avatarColumn = {
            DatabaseHandler.FACE_ID,
            DatabaseHandler.IS_COMPLETE,
            DatabaseHandler.PIC_PNG_KEY_NAME,
            DatabaseHandler.OBJ_KEY_NAME,
            DatabaseHandler.TEXTURE_KEY_NAME,
            DatabaseHandler.FACE_OBJ_DOWNLOAD_STATUS,
            DatabaseHandler.FACE_TEXTURE_DOWNLOAD_STATUS,
            DatabaseHandler.PB_ID,
            DatabaseHandler.BODY_OBJ_KEY_NAME,
            DatabaseHandler.BODY_TEXTURE_KEY_NAME,
            DatabaseHandler.BODY_OBJ_DOWNLOAD_STATUS,
            DatabaseHandler.BODY_TEXTURE_DOWNLOAD_STATUS,


            DatabaseHandler.DEFAULT_HAIR_ID,
            DatabaseHandler.HAIR_OBJ_KEY_NAME,
            DatabaseHandler.HAIR_TEXTURE_KEY_NAME,
            DatabaseHandler.HAIR_OBJ_DOWNLOAD_STATUS,
            DatabaseHandler.HAIR_TEXTURE_DOWNLOAD_STATUS,

            DatabaseHandler.DEFAULT_SPECS_ID,
            DatabaseHandler.SPECS_OBJ_KEY_NAME,
            DatabaseHandler.SPECS_TEXTURE_KEY_NAME,
            DatabaseHandler.SPECS_OBJ_DOWNLOAD_STATUS,
            DatabaseHandler.SPECS_TEXTURE_DOWNLOAD_STATUS,
            DatabaseHandler.UPDATED_DATE,
            DatabaseHandler.FEDUCIAL_POINTS,
            DatabaseHandler.IMAGE_SAVED_FILEPATH,
            DatabaseHandler.IMAGE_RESIZE_RATIO,

            DatabaseHandler.DEFAULT_LEG_ID
    };


    private static final String[] laColumn = {
            DatabaseHandler.PURCHASE_SKUID,
            DatabaseHandler.SKUID,
            DatabaseHandler.COMBO_ID,
            DatabaseHandler.PIC_KEY_NAME,
            DatabaseHandler.RELEVANCE,
            DatabaseHandler.BRAND,
            DatabaseHandler.LINK,
            DatabaseHandler.PRICE,
            DatabaseHandler.SELLER,
            DatabaseHandler.FIT,
            DatabaseHandler.TITLE,
            DatabaseHandler.BUY_COUNT,
            DatabaseHandler.CART_COUNT
    };

    private static final String[] accessoryColumns = {
            DatabaseHandler.OBJ_ID,
            DatabaseHandler.OBJ_ID2,
            DatabaseHandler.PIC_PNG_KEY_NAME,
            DatabaseHandler.TEXTURE_KEY_NAME,
            DatabaseHandler.OBJ_KEY_NAME,
            DatabaseHandler.TEXTURE_DOWNLOAD_STATUS,

            DatabaseHandler.OBJ_DOWNLOAD_STATUS,
            DatabaseHandler.FORCED_RANK,
            DatabaseHandler.ACCESSORY_TYPE,
            DatabaseHandler.ACCESSORY_TYPE2,
            DatabaseHandler.PB_ID,
            DatabaseHandler.FACE_ID,
            DatabaseHandler.UPDATED_DATE,
            DatabaseHandler.DATE_RECONCILE_IN_MILLI,
            DatabaseHandler.ACCESSORY_TYPE
    };

    private static final String[] comboReconcileColumns = {
            DatabaseHandler.COMBO_ID,
            DatabaseHandler.COMBO_TITLE,
            DatabaseHandler.GENDER,
            DatabaseHandler.COMBO_STYLE_CATEGORY,
            DatabaseHandler.COMBO_PIC_PNG_KEY_NAME,

            DatabaseHandler.LIKES_COUNT,
            DatabaseHandler.STYLE_RATING,
            DatabaseHandler.A1_CATEGORY,
            DatabaseHandler.A1_COLOR,
            DatabaseHandler.A1_OBJ_KEY_NAME,
            DatabaseHandler.A1_PNG_KEY_NAME,

            DatabaseHandler.A2_CATEGORY,
            DatabaseHandler.A2_COLOR,

            DatabaseHandler.A3_CATEGORY,
            DatabaseHandler.A3_COLOR,

            DatabaseHandler.A4_CATEGORY,
            DatabaseHandler.A4_COLOR,


            DatabaseHandler.FORCED_RANK,
            DatabaseHandler.PUSH_FLAG,//text True
            DatabaseHandler.VOGUE_FLAG,//text True

            DatabaseHandler.COMBO_LOCAL_VIEW_COUNT,
            DatabaseHandler.COMBO_LOCAL_IS_LIKED,
            DatabaseHandler.COMBO_LOCAL_CART_COUNT,
            DatabaseHandler.COMBO_LOCAL_IS_DISPLAY_READY,
            //DatabaseHandler.SEEN_DATE,
            DatabaseHandler.UPDATED_DATE,
            DatabaseHandler.DATE_RECONCILE_IN_MILLI,
            DatabaseHandler.COMBO_LOCAL_SEEN_FACTOR,
            DatabaseHandler.DATE_SEEN_IN_MILLI
    };

    private static final String[] combodataLikeColumns = {
            DatabaseHandler.COMBO_ID,
            DatabaseHandler.SKU_ID1,
            DatabaseHandler.SKU_ID6,
            DatabaseHandler.SKU_ID7,
            DatabaseHandler.SKU_ID8,
            DatabaseHandler.SKU_ID9,
            DatabaseHandler.SKU_ID10,
            DatabaseHandler.LIKES_COUNT,
            DatabaseHandler.UPDATED_DATE,
            DatabaseHandler.DATE_RECONCILE_IN_MILLI,

            DatabaseHandler.A6_CATEGORY,
            DatabaseHandler.A6_OBJ_KEY_NAME,
            DatabaseHandler.A6_PNG_KEY_NAME,
            DatabaseHandler.A6_OBJ_DOWNLOAD_STATUS,
            DatabaseHandler.A6_TEXTURE_DOWNLOAD_STATUS,
            DatabaseHandler.A6_PIC_PNG_KEY_NAME,

            DatabaseHandler.A7_CATEGORY,
            DatabaseHandler.A7_OBJ_KEY_NAME,
            DatabaseHandler.A7_PNG_KEY_NAME,
            DatabaseHandler.A7_OBJ_DOWNLOAD_STATUS,
            DatabaseHandler.A7_TEXTURE_DOWNLOAD_STATUS,
            DatabaseHandler.A7_PIC_PNG_KEY_NAME,

            DatabaseHandler.A8_CATEGORY,
            DatabaseHandler.A8_OBJ_KEY_NAME,
            DatabaseHandler.A8_PNG_KEY_NAME,
            //DatabaseHandler.A8_OBJ_DOWNLOAD_STATUS,
            //DatabaseHandler.A8_TEXTURE_DOWNLOAD_STATUS,
            DatabaseHandler.A8_PIC_PNG_KEY_NAME,

            DatabaseHandler.A9_CATEGORY,
            DatabaseHandler.A9_OBJ_KEY_NAME,
            DatabaseHandler.A9_PNG_KEY_NAME,
            DatabaseHandler.A9_OBJ_DOWNLOAD_STATUS,
            DatabaseHandler.A9_TEXTURE_DOWNLOAD_STATUS,
            DatabaseHandler.A9_PIC_PNG_KEY_NAME,

            DatabaseHandler.A10_CATEGORY,
            DatabaseHandler.A10_OBJ_KEY_NAME,
            DatabaseHandler.A10_PNG_KEY_NAME,
            DatabaseHandler.A10_OBJ_DOWNLOAD_STATUS,
            DatabaseHandler.A10_TEXTURE_DOWNLOAD_STATUS,
            DatabaseHandler.A10_PIC_PNG_KEY_NAME,

            DatabaseHandler.LEG_ID,
            DatabaseHandler.PB_ID,
            DatabaseHandler.FACE_ID,
            DatabaseHandler.LEG_OBJ_KEY_NAME,
            DatabaseHandler.LEG_TEX_KEY_NAME,
    };


    private static final String[] combodataColumns = {
            DatabaseHandler.COMBO_ID,
            DatabaseHandler.COMBO_TITLE,
            DatabaseHandler.GENDER,
            DatabaseHandler.COMBO_STYLE_CATEGORY,
            DatabaseHandler.COMBO_PIC_PNG_FILE_NAME,
            DatabaseHandler.COMBO_PIC_PNG_KEY_NAME,
            DatabaseHandler.SKU_ID1,
            DatabaseHandler.SKU_ID2,
            DatabaseHandler.SKU_ID3,
            DatabaseHandler.SKU_ID4,
            DatabaseHandler.SKU_ID5,
            DatabaseHandler.SKU_ID6,
            DatabaseHandler.SKU_ID7,
            DatabaseHandler.SKU_ID8,
            DatabaseHandler.SKU_ID9,
            DatabaseHandler.SKU_ID10,
            DatabaseHandler.LIKES_COUNT,
            DatabaseHandler.STYLE_RATING,
            DatabaseHandler.A1_CATEGORY,
            DatabaseHandler.A1_COLOR,
            DatabaseHandler.A1_OBJ_KEY_NAME,
            DatabaseHandler.A1_PNG_KEY_NAME,
            DatabaseHandler.A1_OBJ_DOWNLOAD_STATUS,
            DatabaseHandler.A1_TEXTURE_DOWNLOAD_STATUS,
            DatabaseHandler.A1_PIC_PNG_KEY_NAME,

            DatabaseHandler.A2_CATEGORY,
            DatabaseHandler.A2_COLOR,
            DatabaseHandler.A2_OBJ_KEY_NAME,
            DatabaseHandler.A2_PNG_KEY_NAME,
            DatabaseHandler.A2_PIC_PNG_KEY_NAME,

            DatabaseHandler.A3_CATEGORY,
            DatabaseHandler.A3_COLOR,
            DatabaseHandler.A3_OBJ_KEY_NAME,
            DatabaseHandler.A3_PNG_KEY_NAME,
            DatabaseHandler.A3_PIC_PNG_KEY_NAME,

            DatabaseHandler.A4_CATEGORY,
            DatabaseHandler.A4_COLOR,
            DatabaseHandler.A4_OBJ_KEY_NAME,
            DatabaseHandler.A4_PNG_KEY_NAME,
            DatabaseHandler.A4_PIC_PNG_KEY_NAME,

            DatabaseHandler.A5_CATEGORY,
            DatabaseHandler.A5_COLOR,
            DatabaseHandler.A5_OBJ_KEY_NAME,
            DatabaseHandler.A5_PNG_KEY_NAME,
            DatabaseHandler.A5_PIC_PNG_KEY_NAME,

            DatabaseHandler.A6_CATEGORY,
            DatabaseHandler.A6_OBJ_KEY_NAME,
            DatabaseHandler.A6_PNG_KEY_NAME,
            DatabaseHandler.A6_OBJ_DOWNLOAD_STATUS,
            DatabaseHandler.A6_TEXTURE_DOWNLOAD_STATUS,
            DatabaseHandler.A6_PIC_PNG_KEY_NAME,

            DatabaseHandler.A7_CATEGORY,
            DatabaseHandler.A7_OBJ_KEY_NAME,
            DatabaseHandler.A7_PNG_KEY_NAME,
            DatabaseHandler.A7_OBJ_DOWNLOAD_STATUS,
            DatabaseHandler.A7_TEXTURE_DOWNLOAD_STATUS,
            DatabaseHandler.A7_PIC_PNG_KEY_NAME,

            DatabaseHandler.A8_CATEGORY,
            DatabaseHandler.A8_OBJ_KEY_NAME,
            DatabaseHandler.A8_PNG_KEY_NAME,
            //DatabaseHandler.A8_OBJ_DOWNLOAD_STATUS,
            //DatabaseHandler.A8_TEXTURE_DOWNLOAD_STATUS,
            DatabaseHandler.A8_PIC_PNG_KEY_NAME,

            DatabaseHandler.A9_CATEGORY,

            DatabaseHandler.A9_OBJ_KEY_NAME,
            DatabaseHandler.A9_PNG_KEY_NAME,
            DatabaseHandler.A9_OBJ_DOWNLOAD_STATUS,
            DatabaseHandler.A9_TEXTURE_DOWNLOAD_STATUS,
            DatabaseHandler.A9_PIC_PNG_KEY_NAME,

            DatabaseHandler.A10_CATEGORY,

            DatabaseHandler.A10_OBJ_KEY_NAME,
            DatabaseHandler.A10_PNG_KEY_NAME,
            DatabaseHandler.A10_OBJ_DOWNLOAD_STATUS,
            DatabaseHandler.A10_TEXTURE_DOWNLOAD_STATUS,
            DatabaseHandler.A10_PIC_PNG_KEY_NAME,

            DatabaseHandler.FORCED_RANK,
            DatabaseHandler.PUSH_FLAG,//text True
            DatabaseHandler.VOGUE_FLAG,//text True

            DatabaseHandler.COMBO_LOCAL_VIEW_COUNT,
            DatabaseHandler.COMBO_LOCAL_IS_LIKED,
            DatabaseHandler.COMBO_LOCAL_CART_COUNT,
            DatabaseHandler.COMBO_LOCAL_IS_DISPLAY_READY,

            DatabaseHandler.COMBO_COLOR,
            DatabaseHandler.DESCRIPTION,
            DatabaseHandler.LEG_ID,
            DatabaseHandler.PB_ID,
            DatabaseHandler.FACE_ID,
            //DatabaseHandler.SEEN_DATE,
            DatabaseHandler.UPDATED_DATE,
            DatabaseHandler.DATE_RECONCILE_IN_MILLI,
            DatabaseHandler.COMBO_LOCAL_SEEN_FACTOR,
            DatabaseHandler.DATE_SEEN_IN_MILLI
    };

    private static final String[] comboLookboardColumns = {
            DatabaseHandler.STATUS,
            DatabaseHandler.PB_ID,
            DatabaseHandler.COMPAIGN_ID,
            DatabaseHandler.CURATOR_ID,
            DatabaseHandler.COMBO_ID,
            DatabaseHandler.GENDER,
            DatabaseHandler.COMBO_STYLE_CATEGORY,
            DatabaseHandler.COMBO_PIC_PNG_KEY_NAME,

            DatabaseHandler.FORCED_RANK,
            DatabaseHandler.PUSH_FLAG,//text True

            DatabaseHandler.COMBO_LOCAL_VIEW_COUNT,
            DatabaseHandler.DATE_RECONCILE_IN_MILLI,
            DatabaseHandler.DATE_SEEN_IN_MILLI
    };

    private ContentValues populateContentValue(ContentValues cv, String columnName, String value) {
        if (value != null && value.length() != 0 && !value.equals("null")) {
            cv.put(columnName, value);
        }
//        if (value != null && !value.equals("null")) {
//            cv.put(columnName, value);
//        }
        return cv;
    }

    private ContentValues populateContentValue(ContentValues cv, String columnName, int value) {
        if (value != 0) {
            cv.put(columnName, value);
        }
        return cv;
    }
    private ContentValues populateContentValue(ContentValues cv, String columnName, Integer value) {
        if (value != null && value != 0) {
            cv.put(columnName, value);
        }
        return cv;
    }

    private ContentValues populateContentValue(ContentValues cv, String columnName, long value) {
        if ( value != 0) {
            cv.put(columnName, value);
        }
        return cv;
    }

    private ContentValues populateContentValue(ContentValues cv, String columnName, float value) {
        if (value != 0) {
            cv.put(columnName, value);
        }
        return cv;
    }

    public long insertOrUpdate(String TableName, ContentValues values, String IDColumn) {
        String valueId = String.valueOf(values.get(IDColumn));
        Cursor cursor = database.query(TableName, new String[]{IDColumn}, IDColumn + "=?",
                new String[]{valueId}, null, null, null);

        //Log.i(LOGTAG, " TableName : " + TableName + "  Returned  Data: " + cursor.getCount() + " rows");
        long insertId = 0;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            String comId = cursor.getString(cursor.getColumnIndex(IDColumn));
            database.update(TableName, values, IDColumn + "=? ", new String[]{valueId});
        } else {

            insertId = database.insert(TableName, null, values);
        }
        cursor.close();
        return insertId;
    }


    public long insertOrUpdate(String TableName, ContentValues contentValues, String[] whereColumns) {
        if (whereColumns == null) {
            Log.e(LOGTAG, "Insert failed : " + TableName + " whereColumns is null");
            return -2;
        }
        String whereString = "";
        String[] whereValues = new String[whereColumns.length];
        for (int i = 0; i < whereColumns.length; i++) {
            String columnName = whereColumns[i];
            if (!contentValues.containsKey(columnName)) {
                Log.e(LOGTAG, "Insert failed : " + TableName + "  contentValue does not contain columnName :" + columnName);
                return -2;
            }
            String value = String.valueOf(contentValues.get(columnName));
            whereValues[i] = value;
            if (i == whereColumns.length - 1) {
                whereString += columnName + " =? ";
            } else {
                whereString += columnName + " =? AND ";
            }
        }

        Cursor cursor = database.query(TableName, whereColumns, whereString,
                whereValues, null, null, null);

        //Log.i(LOGTAG, " TableName : " + TableName + "  Returned  Data " + cursor.getCount() + " rows");
        long insertId = 0;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            database.update(TableName, contentValues, whereString, whereValues);
        } else {
            insertId = database.insert(TableName, null, contentValues);
        }
        cursor.close();
        return insertId;
    }

    public Cursor queryData(String tableName, String[] columns, String[] whereColumns, String[] whereValues, String orderBy, String limit) {
        if (whereColumns == null || whereValues == null) {
            Log.e(LOGTAG, "Insert failed : " + tableName + " whereColumns is null");
            return null;
        }
        String whereString = "";
        String whereValueStr = "";
        for (int i = 0; i < whereColumns.length; i++) {
            String columnName = whereColumns[i];
            if (i == whereColumns.length - 1) {
                whereString += columnName + " =? ";
            } else {
                whereString += columnName + " =? AND ";
            }
            whereValueStr += whereValues[i];
            Log.d("whereValueStr", whereValueStr);
        }
        Log.i(LOGTAG, " TableName : " + tableName + "  query  Data " + whereString + "  value: " + whereValueStr);
        //String orderBy = DatabaseHandler.COMBO_LOCAL_VIEW_COUNT + " ASC, " + DatabaseHandler.FORCED_RANK + " ASC";

//        Cursor cursor = database.query(DatabaseHandler.TABLE_COMBODATA, combodataColumns,
//                DatabaseHandler.COMBO_LOCAL_IS_DISPLAY_READY + "=?", new String[]{"0"}, null, null, orderByUnseen, limit);

        Cursor cursor = database.query(tableName, columns,
                whereString, whereValues, null, null, orderBy, limit);

        Log.i(LOGTAG, "queryData : " + tableName + " : " + cursor.getCount() + " rows");
        return cursor;
    }

    public long delete(String TableName, String[] whereColumns, String[] whereValues) {
        if (whereColumns == null || whereValues == null) {
            Log.e(LOGTAG, "Insert failed : " + TableName + " whereColumns is null");
            return -2;
        }
        String whereString = "";
        String whereValueStr = "";
        for (int i = 0; i < whereColumns.length; i++) {
            String columnName = whereColumns[i];
            if (i == whereColumns.length - 1) {
                whereString += columnName + " =? ";
            } else {
                whereString += columnName + " =? AND ";
            }
            whereValueStr += whereValues[i];
            Log.d("whereValueStr", whereValueStr);
        }
        Log.i(LOGTAG, " TableName : " + TableName + "  delete  Data " + whereString + "  value: " + whereValueStr);

        long insertId = 0;
        insertId = database.delete(TableName, whereString, whereValues);
        return insertId;
    }

    public long delete(String TableName, String whereString, String[] whereValues) {
        Log.i(LOGTAG, " TableName : " + TableName + "  delete  Data " + whereString + "  value: " + whereValues);
        long insertId = 0;
        insertId = database.delete(TableName, whereString, whereValues);
        return insertId;
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //String strDate = sdf.format(new Date());
        return sdf.format(new Date());
    }

    private long getMilliSecond() {
        Date myDate = new Date();
        long timeMilliseconds = myDate.getTime();
        //add 1 hour
        timeMilliseconds = timeMilliseconds + 3600 * 1000; //3600 seconds * 1000 milliseconds
        //To convert back to Date
        //Date myDateNew = new Date(timeMilliseconds);

        //Calendar.getInstance().getTimeInMillis();
        Log.d(LOGTAG, "updatedAt timeMilliseconds: " + timeMilliseconds);
        Log.d(LOGTAG, "updatedAt Calendar timeMilliseconds: " + Calendar.getInstance().getTimeInMillis());
        return timeMilliseconds;
    }

    private String getFormattedDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        DateFormat formatter = new SimpleDateFormat(dateFormat);
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    /**********************************************************************************************************************
     * *******************************************************************************************************************
     * USER
     * ********************************************************************************************************************
     ********************************************************************************************************************/
    public User create(User user) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHandler.USER_ID, user.getmUserId());
        values.put(DatabaseHandler.FIRST_NAME, user.getmFirstName());
        values.put(DatabaseHandler.LAST_NAME, user.getmLastName());
        values.put(DatabaseHandler.MOBILE_NUMBER, user.getmMobileNumber());
        values.put(DatabaseHandler.GENDER, user.getmGender());
        values.put(DatabaseHandler.USER_ID, user.getmUserId());
        values.put(DatabaseHandler.EMAIL_ID, user.getEmailId());
        values.put(DatabaseHandler.DOB, user.getDob_dd_mmm_yyyy());
        values.put(DatabaseHandler.DOB_DAY, user.getDob_day());
        values.put(DatabaseHandler.DOB_MONTH, user.getDob_month());
        values.put(DatabaseHandler.DOB_YEAR, user.getDob_year());
        values.put(DatabaseHandler.BUST_CIRCUM, user.getBust());
        values.put(DatabaseHandler.HIPS_CIRCUM, user.getHip());
        values.put(DatabaseHandler.WAIST_CIRCUM, user.getWaist());
        values.put(DatabaseHandler.HEIGHT, user.getHeight());
        values.put(DatabaseHandler.WEIGHT, user.getWeight());
        values.put(DatabaseHandler.POINTS, user.getPoints());
        values.put(DatabaseHandler.USER_THUMB_URL, user.getThumbUrl());
        //values.put(DatabaseHandler.PIN, user.getmPIN());
        populateContentValue(values, DatabaseHandler.PIN, user.getmPIN());
        populateContentValue(values, DatabaseHandler.DEFAULT_FACE_ID, user.getDefaultFaceId());
        populateContentValue(values, DatabaseHandler.PB_ID, user.getPBId());

        Log.d(LOGTAG, "USER ID: " + user.getmUserId() + "   ");
        //long insertid = database.insert(DatabaseHandler.TABLE_USER, null, values);
        long insertId = insertOrUpdate(DatabaseHandler.TABLE_USER, values, DatabaseHandler.USER_ID);
        if (insertId != 0)
            user.setId(insertId);
        User.setInstance(user);

        return user;
    }

    private List<User> cursorToList(Cursor cursor) {
        List<User> users = new ArrayList<User>();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                User user = new User();
                user.setmUserId(cursor.getString(cursor.getColumnIndex(DatabaseHandler.USER_ID)));
                user.setmFirstName(cursor.getString(cursor.getColumnIndex(DatabaseHandler.FIRST_NAME)));
                user.setmLastName(cursor.getString(cursor.getColumnIndex(DatabaseHandler.LAST_NAME)));
                user.setmMobileNumber(cursor.getString(cursor.getColumnIndex(DatabaseHandler.MOBILE_NUMBER)));
                user.setmGender(cursor.getString(cursor.getColumnIndex(DatabaseHandler.GENDER)));
                user.setEmailId(cursor.getString(cursor.getColumnIndex(DatabaseHandler.EMAIL_ID)));
                user.setDob_dd_mmm_yyyy(cursor.getString(cursor.getColumnIndex(DatabaseHandler.DOB)));
                user.setDob_day(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.DOB_DAY)));
                user.setDob_month(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.DOB_MONTH)));
                user.setDob_year(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.DOB_YEAR)));
                user.setmPIN(cursor.getString(cursor.getColumnIndex(DatabaseHandler.PIN)));
                user.setPBId(cursor.getString(cursor.getColumnIndex(DatabaseHandler.PB_ID)));
                user.setDefaultFaceId(cursor.getString(cursor.getColumnIndex(DatabaseHandler.DEFAULT_FACE_ID)));
                user.setHeight(cursor.getFloat(cursor.getColumnIndex(DatabaseHandler.HEIGHT)));
                user.setWeight(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.WEIGHT)));
                user.setWaist(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.WAIST_CIRCUM)));
                user.setBust(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.BUST_CIRCUM)));
                user.setHip(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.HIPS_CIRCUM)));
                user.setPoints(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.POINTS)));
                user.setIsRegistrationComplete(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.IS_REGISTRATION_COMPLETE)));
                user.setThumbUrl(cursor.getString(cursor.getColumnIndex(DatabaseHandler.USER_THUMB_URL)));
                users.add(user);
            }
        }
        cursor.close();
        return users;
    }

    public User findUser(String userId) {
        Cursor cursor = database.query(DatabaseHandler.TABLE_USER, userColumn,
                DatabaseHandler.USER_ID + " = ? ",
                new String[]{userId}, null, null, null);
        Log.i(LOGTAG, "Returned " + cursor.getCount() + " rows");
        List<User> users = cursorToList(cursor);
        User user = null;
        if (users != null && users.size() != 0)
            user = users.get(0);
        cursor.close();
        return user;
    }

    public long updateUserWeight(int weight) {
        ContentValues values1 = new ContentValues();
        values1.put(DatabaseHandler.USER_ID, User.getInstance().getmUserId());
        values1.put(DatabaseHandler.WEIGHT, weight);
        long insertId = insertOrUpdate(DatabaseHandler.TABLE_USER, values1, DatabaseHandler.USER_ID);
        return insertId;
    }


    /**********************************************************************************************************************
     * *******************************************************************************************************************
     * FACE
     * ********************************************************************************************************************
     ********************************************************************************************************************/
    public FaceItem create(FaceItem item) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHandler.FACE_ID, item.getFaceId());
        contentValues.put(DatabaseHandler.USER_ID, User.getInstance().getmUserId());
        contentValues.put(DatabaseHandler.IS_COMPLETE, item.getIsComplete());
        //if(item.getObjAwsKey() != null && item.getObjAwsKey().length() != 0)
        //contentValues.put(DatabaseHandler.OBJ_KEY_NAME, item.getObjAwsKey());
        populateContentValue(contentValues, DatabaseHandler.TEXTURE_KEY_NAME, item.getFacePngkey());
        populateContentValue(contentValues, DatabaseHandler.OBJ_KEY_NAME, item.getFaceObjkey());
        contentValues.put(DatabaseHandler.FACE_TEXTURE_DOWNLOAD_STATUS, item.getTextureFaceDStatus());
        contentValues.put(DatabaseHandler.FACE_OBJ_DOWNLOAD_STATUS, item.getObjFaceDStatus());

        populateContentValue(contentValues, DatabaseHandler.PB_ID, item.getPbId());
        populateContentValue(contentValues, DatabaseHandler.BODY_TEXTURE_KEY_NAME, item.getBodyPngkey());
        populateContentValue(contentValues, DatabaseHandler.BODY_OBJ_KEY_NAME, item.getBodyObjkey());
        contentValues.put(DatabaseHandler.BODY_TEXTURE_DOWNLOAD_STATUS, item.getTextureBodyDStatus());
        contentValues.put(DatabaseHandler.BODY_OBJ_DOWNLOAD_STATUS, item.getObjBodyDStatus());


        populateContentValue(contentValues, DatabaseHandler.DEFAULT_HAIR_ID, item.getHairstyleId());
        populateContentValue(contentValues, DatabaseHandler.HAIR_TEXTURE_KEY_NAME, item.getHairPngKey());
        populateContentValue(contentValues, DatabaseHandler.HAIR_OBJ_KEY_NAME, item.getHairObjkey());
        contentValues.put(DatabaseHandler.HAIR_TEXTURE_DOWNLOAD_STATUS, item.getTextureHairDStatus());
        contentValues.put(DatabaseHandler.HAIR_OBJ_DOWNLOAD_STATUS, item.getObjHairDStatus());

        populateContentValue(contentValues, DatabaseHandler.DEFAULT_SPECS_ID, item.getSpecsId());
        populateContentValue(contentValues, DatabaseHandler.SPECS_TEXTURE_KEY_NAME, item.getSpecsPngkey());
        populateContentValue(contentValues, DatabaseHandler.SPECS_OBJ_KEY_NAME, item.getSpecsObjkey());
        populateContentValue(contentValues, DatabaseHandler.FEDUCIAL_POINTS, item.getFeducialPoints());
        populateContentValue(contentValues, DatabaseHandler.IMAGE_SAVED_FILEPATH, item.getImageSavedFilePath());
        populateContentValue(contentValues, DatabaseHandler.IMAGE_RESIZE_RATIO, item.getImageResizeRatio());


//        contentValues.put(DatabaseHandler.SPECS_TEXTURE_DOWNLOAD_STATUS, item.getTextureSpecsDStatus());
//        contentValues.put(DatabaseHandler.SPECS_OBJ_DOWNLOAD_STATUS, item.getObjSpecsDStatus());

        //long insertId = insertOrUpdate(DatabaseHandler.TABLE_USER_AVATAR, contentValues, DatabaseHandler.FACE_ID);
        long insertId = insertOrUpdate(DatabaseHandler.TABLE_USER_AVATAR, contentValues, new String[]{DatabaseHandler.FACE_ID, DatabaseHandler.USER_ID});
        if (insertId != 0)
            item.setRowId((int) insertId);
        return item;
    }

    private List<FaceItem> faceCursorToList(Cursor cursor) {
        List<FaceItem> list = new ArrayList<FaceItem>();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                FaceItem item = new FaceItem();
                item.setFaceId(cursor.getString(cursor.getColumnIndex(DatabaseHandler.FACE_ID)));
                item.setFacePngkey(cursor.getString(cursor.getColumnIndex(DatabaseHandler.TEXTURE_KEY_NAME)));
                item.setFaceObjkey(cursor.getString(cursor.getColumnIndex(DatabaseHandler.OBJ_KEY_NAME)));
                item.setObjFaceDStatus(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.FACE_OBJ_DOWNLOAD_STATUS)));
                item.setTextureFaceDStatus(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.FACE_TEXTURE_DOWNLOAD_STATUS)));
                item.setPbId(cursor.getString(cursor.getColumnIndex(DatabaseHandler.PB_ID)));
                item.setBodyPngkey(cursor.getString(cursor.getColumnIndex(DatabaseHandler.BODY_TEXTURE_KEY_NAME)));
                item.setBodyObjkey(cursor.getString(cursor.getColumnIndex(DatabaseHandler.BODY_OBJ_KEY_NAME)));

                item.setObjBodyDStatus(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.BODY_OBJ_DOWNLOAD_STATUS)));
                item.setTextureBodyDStatus(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.BODY_TEXTURE_DOWNLOAD_STATUS)));

                item.setHairstyleId(cursor.getString(cursor.getColumnIndex(DatabaseHandler.DEFAULT_HAIR_ID)));
                item.setHairPngKey(cursor.getString(cursor.getColumnIndex(DatabaseHandler.HAIR_TEXTURE_KEY_NAME)));
                item.setHairObjkey(cursor.getString(cursor.getColumnIndex(DatabaseHandler.HAIR_OBJ_KEY_NAME)));

                item.setObjHairDStatus(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.HAIR_OBJ_DOWNLOAD_STATUS)));
                item.setTextureHairDStatus(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.HAIR_TEXTURE_DOWNLOAD_STATUS)));

                item.setSpecsId(cursor.getString(cursor.getColumnIndex(DatabaseHandler.DEFAULT_SPECS_ID)));
                item.setSpecsPngkey(cursor.getString(cursor.getColumnIndex(DatabaseHandler.SPECS_TEXTURE_KEY_NAME)));
                item.setSpecsObjkey(cursor.getString(cursor.getColumnIndex(DatabaseHandler.SPECS_OBJ_KEY_NAME)));

                item.setDefaultLegId(cursor.getString(cursor.getColumnIndex(DatabaseHandler.DEFAULT_LEG_ID)));
                item.setFeducialPoints(cursor.getString(cursor.getColumnIndex(DatabaseHandler.FEDUCIAL_POINTS)));
                item.setImageSavedFilePath(cursor.getString(cursor.getColumnIndex(DatabaseHandler.IMAGE_SAVED_FILEPATH)));
                item.setImageResizeRatio(cursor.getString(cursor.getColumnIndex(DatabaseHandler.IMAGE_RESIZE_RATIO)));
                item.setIsComplete(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.IS_COMPLETE)));

                list.add(item);
            }
        }
        return list;
    }

    /***********
     * QUERY AVATAR - FACE
     ************/
    public List<FaceItem> getAvatars() {
        Cursor cursor = database.query(DatabaseHandler.TABLE_USER_AVATAR, avatarColumn,
                null, null, null, null, null, null);

        /* TODO
          Cursor cursor = database.query(DatabaseHandler.TABLE_USER_AVATAR, avatarColumn,
                DatabaseHandler.USER_ID + "? = ", new String[]{User.getInstance().getmUserId()}, null, null, null, null);
                */
        List<FaceItem> list = faceCursorToList(cursor);
        cursor.close();
        return list;
    }

    public FaceItem getAvatar(String faceId) {
        Cursor cursor = null;

        if (faceId != null) {
            cursor = database.query(DatabaseHandler.TABLE_USER_AVATAR, avatarColumn,
                    DatabaseHandler.FACE_ID + " =? ", new String[]{faceId}, null, null, null, null);
        } else { //TODO
            cursor = database.query(DatabaseHandler.TABLE_USER_AVATAR, avatarColumn,
                    null, null, null, null, null, null);
        }

        List<FaceItem> list = faceCursorToList(cursor);
        FaceItem item = null;
        if (list != null && list.size() != 0)
            item = list.get(0);
        cursor.close();
        return item;
    }

    public long delete(FaceItem faceItem) {
        String[] whereColumn = new String[]{DatabaseHandler.FACE_ID, DatabaseHandler.USER_ID};
        String[] whereValue = new String[]{faceItem.getFaceId(), User.getInstance().getmUserId()};
        long insertId = 0;
        insertId = delete(DatabaseHandler.TABLE_USER_AVATAR, whereColumn, whereValue);
        return insertId;
    }

    /**********************************************************************************************************************
     * *******************************************************************************************************************
     * LOOKALIKE
     * ********************************************************************************************************************
     ********************************************************************************************************************/
    public LAData create(LAData ladata) {
        ContentValues values2 = new ContentValues();
        values2.put(DatabaseHandler.SKUID, ladata.getSKU_ID());
        values2.put(DatabaseHandler.PURCHASE_SKUID, ladata.getPurchase_SKU_ID());
        values2.put(DatabaseHandler.COMBO_ID, ladata.getCombo_ID());
        values2.put(DatabaseHandler.RELEVANCE, ladata.getRelevance());
        values2.put(DatabaseHandler.PIC_KEY_NAME, ladata.getPic_URL());
        values2.put(DatabaseHandler.BRAND, ladata.getBrand());
        values2.put(DatabaseHandler.LINK, ladata.getLink());
        values2.put(DatabaseHandler.PRICE, ladata.getPrice());
        values2.put(DatabaseHandler.SELLER, ladata.getSeller());
        values2.put(DatabaseHandler.FIT, ladata.getFit());
        values2.put(DatabaseHandler.TITLE, ladata.getTitle());
        values2.put(DatabaseHandler.BUY_COUNT, ladata.getBuy_Count());
        values2.put(DatabaseHandler.CART_COUNT, ladata.getCart_Count());
        //long insertid2 = database.insert(DatabaseHandler.TABLE_LA, null, values2);
        long insertId = insertOrUpdate(DatabaseHandler.TABLE_LA, values2, DatabaseHandler.PURCHASE_SKUID);
        if (insertId != 0)
            ladata.setId2(insertId);
        return ladata;
    }


    private List<LAData> laDataCursorToList(Cursor cursor) {
        List<LAData> laDataList = new ArrayList<LAData>();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                LAData ladata = new LAData();
                ladata.setPurchase_SKU_ID(cursor.getString(cursor.getColumnIndex(DatabaseHandler.PURCHASE_SKUID)));
                ladata.setSKU_ID(cursor.getString(cursor.getColumnIndex(DatabaseHandler.SKUID)));
                ladata.setCombo_ID(cursor.getString(cursor.getColumnIndex(DatabaseHandler.COMBO_ID)));
                ladata.setPic_URL(cursor.getString(cursor.getColumnIndex(DatabaseHandler.PIC_KEY_NAME)));
                ladata.setRelevance(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.RELEVANCE)));
                ladata.setLink(cursor.getString(cursor.getColumnIndex(DatabaseHandler.LINK)));

                ladata.setPrice(cursor.getString(cursor.getColumnIndex(DatabaseHandler.PRICE)));
                ladata.setBrand(cursor.getString(cursor.getColumnIndex(DatabaseHandler.BRAND)));
                ladata.setSeller(cursor.getString(cursor.getColumnIndex(DatabaseHandler.SELLER)));
                ladata.setFit(cursor.getString(cursor.getColumnIndex(DatabaseHandler.FIT)));
                ladata.setTitle(cursor.getString(cursor.getColumnIndex(DatabaseHandler.TITLE)));
                ladata.setBuy_Count(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.BUY_COUNT)));
                ladata.setCart_Count(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.CART_COUNT)));
                laDataList.add(ladata);
            }
        }
        return laDataList;
    }

    public List<LAData> getLADataDetails() {
        Cursor cursor = database.query(DatabaseHandler.TABLE_LA, laColumn,
                null, null, null, null, null);
        Log.i(LOGTAG, "Returned LA Data" + cursor.getCount() + " rows");
        List<LAData> ladataList = laDataCursorToList(cursor);
        cursor.close();
        return ladataList;
    }

    public long delete(LAData laData) {
        String[] whereColumn = new String[]{DatabaseHandler.PURCHASE_SKUID};
        String[] whereValue = new String[]{laData.getPurchase_SKU_ID()};
        long insertId = 0;
        insertId = delete(DatabaseHandler.TABLE_LA, whereColumn, whereValue);
        return insertId;
    }


    /**********************************************************************************************************************
     * *******************************************************************************************************************
     * COMBO
     * ********************************************************************************************************************
     ********************************************************************************************************************/
    public ComboData create(ComboData combodata) {
        ContentValues values1 = new ContentValues();
        values1.put(DatabaseHandler.COMBO_ID, combodata.getCombo_ID());
        populateContentValue(values1, DatabaseHandler.LEG_ID, combodata.getLegId());
        populateContentValue(values1, DatabaseHandler.PB_ID, combodata.getPbId());
        populateContentValue(values1, DatabaseHandler.FACE_ID, combodata.getFaceId());
//        values1.put(DatabaseHandler.COMBO_TITLE, combodata.getmCombo_Title());
//        values1.put(DatabaseHandler.GENDER, combodata.getmCombo_Gender());
//        values1.put(DatabaseHandler.COMBO_STYLE_CATEGORY, combodata.getmCombo_Style_Category());
//        values1.put(DatabaseHandler.COMBO_PIC_PNG_KEY_NAME, combodata.getmCombo_PIC_Png_Key_Name());
        values1.put(DatabaseHandler.SKU_ID1, combodata.getmSKU_ID1());
        values1.put(DatabaseHandler.SKU_ID2, combodata.getmSKU_ID2());
        values1.put(DatabaseHandler.SKU_ID3, combodata.getmSKU_ID3());
        values1.put(DatabaseHandler.SKU_ID4, combodata.getmSKU_ID4());
        values1.put(DatabaseHandler.SKU_ID5, combodata.getmSKU_ID5());
        values1.put(DatabaseHandler.SKU_ID6, combodata.getmSKU_ID6());
        values1.put(DatabaseHandler.SKU_ID7, combodata.getmSKU_ID7());
        values1.put(DatabaseHandler.SKU_ID8, combodata.getmSKU_ID8());
        values1.put(DatabaseHandler.SKU_ID9, combodata.getmSKU_ID9());
        values1.put(DatabaseHandler.SKU_ID10, combodata.getmSKU_ID10());

        values1.put(DatabaseHandler.LIKES_COUNT, combodata.getLikes_Count());
//        values1.put(DatabaseHandler.STYLE_RATING, combodata.getmStyle_Rating());

        values1.put(DatabaseHandler.A1_CATEGORY, combodata.getmA1_Category());
        values1.put(DatabaseHandler.A1_OBJ_KEY_NAME, combodata.getmA1_Obj_Key_Name());
        values1.put(DatabaseHandler.A1_PNG_KEY_NAME, combodata.getmA1_Png_Key_Name());
        values1.put(DatabaseHandler.A1_PIC_PNG_KEY_NAME, combodata.getmA1_PIC_Png_Key_Name());
        values1.put(DatabaseHandler.A1_OBJ_DOWNLOAD_STATUS, combodata.getObjA1DStatus());
        values1.put(DatabaseHandler.A1_TEXTURE_DOWNLOAD_STATUS, combodata.getTextureA1DStatus());

        values1.put(DatabaseHandler.A2_CATEGORY, combodata.getmA2_Category());
        values1.put(DatabaseHandler.A2_OBJ_KEY_NAME, combodata.getmA2_Obj_Key_Name());
        values1.put(DatabaseHandler.A2_PNG_KEY_NAME, combodata.getmA2_Png_Key_Name());
        values1.put(DatabaseHandler.A2_PIC_PNG_KEY_NAME, combodata.getmA2_PIC_Png_Key_Name());

        values1.put(DatabaseHandler.A3_CATEGORY, combodata.getmA3_Category());
        values1.put(DatabaseHandler.A3_OBJ_KEY_NAME, combodata.getmA3_Obj_Key_Name());
        values1.put(DatabaseHandler.A3_PNG_KEY_NAME, combodata.getmA3_Png_Key_Name());
        values1.put(DatabaseHandler.A3_PIC_PNG_KEY_NAME, combodata.getmA3_PIC_Png_Key_Name());

        values1.put(DatabaseHandler.A4_CATEGORY, combodata.getmA4_Category());
        values1.put(DatabaseHandler.A4_OBJ_KEY_NAME, combodata.getmA4_Obj_Key_Name());
        values1.put(DatabaseHandler.A4_PNG_KEY_NAME, combodata.getmA4_Png_Key_Name());
        values1.put(DatabaseHandler.A4_PIC_PNG_KEY_NAME, combodata.getmA4_PIC_Png_Key_Name());


        values1.put(DatabaseHandler.A5_CATEGORY, combodata.getmA5_Category());
        values1.put(DatabaseHandler.A5_OBJ_KEY_NAME, combodata.getmA5_Obj_Key_Name());
        values1.put(DatabaseHandler.A5_PNG_KEY_NAME, combodata.getmA5_Png_Key_Name());
        values1.put(DatabaseHandler.A5_PIC_PNG_KEY_NAME, combodata.getmA5_PIC_Png_Key_Name());

        values1.put(DatabaseHandler.A6_CATEGORY, combodata.getmA6_Category());
        values1.put(DatabaseHandler.A6_OBJ_KEY_NAME, combodata.getmA6_Obj_Key_Name());
        values1.put(DatabaseHandler.A6_PNG_KEY_NAME, combodata.getmA6_Png_Key_Name());
        values1.put(DatabaseHandler.A6_PIC_PNG_KEY_NAME, combodata.getmA6_PIC_Png_Key_Name());
        values1.put(DatabaseHandler.A6_OBJ_DOWNLOAD_STATUS, combodata.getObjA6DStatus());
        values1.put(DatabaseHandler.A6_TEXTURE_DOWNLOAD_STATUS, combodata.getTextureA6DStatus());

        values1.put(DatabaseHandler.A7_CATEGORY, combodata.getmA7_Category());
        values1.put(DatabaseHandler.A7_OBJ_KEY_NAME, combodata.getmA7_Obj_Key_Name());
        values1.put(DatabaseHandler.A7_PNG_KEY_NAME, combodata.getmA7_Png_Key_Name());
        values1.put(DatabaseHandler.A7_PIC_PNG_KEY_NAME, combodata.getmA7_PIC_Png_Key_Name());
        values1.put(DatabaseHandler.A7_OBJ_DOWNLOAD_STATUS, combodata.getObjA7DStatus());
        values1.put(DatabaseHandler.A7_TEXTURE_DOWNLOAD_STATUS, combodata.getTextureA7DStatus());

        values1.put(DatabaseHandler.A8_CATEGORY, combodata.getmA8_Category());
        values1.put(DatabaseHandler.A8_OBJ_KEY_NAME, combodata.getmA8_Obj_Key_Name());
        values1.put(DatabaseHandler.A8_PNG_KEY_NAME, combodata.getmA8_Png_Key_Name());
        values1.put(DatabaseHandler.A8_PIC_PNG_KEY_NAME, combodata.getmA8_PIC_Png_Key_Name());


        values1.put(DatabaseHandler.A9_CATEGORY, combodata.getmA9_Category());
        values1.put(DatabaseHandler.A9_OBJ_KEY_NAME, combodata.getmA9_Obj_Key_Name());
        values1.put(DatabaseHandler.A9_PNG_KEY_NAME, combodata.getmA9_Png_Key_Name());
        values1.put(DatabaseHandler.A9_PIC_PNG_KEY_NAME, combodata.getmA9_PIC_Png_Key_Name());
        values1.put(DatabaseHandler.A9_OBJ_DOWNLOAD_STATUS, combodata.getObjA9DStatus());
        values1.put(DatabaseHandler.A9_TEXTURE_DOWNLOAD_STATUS, combodata.getTextureA9DStatus());

        values1.put(DatabaseHandler.A10_CATEGORY, combodata.getmA10_Category());
        values1.put(DatabaseHandler.A10_OBJ_KEY_NAME, combodata.getmA10_Obj_Key_Name());
        values1.put(DatabaseHandler.A10_PNG_KEY_NAME, combodata.getmA10_Png_Key_Name());
        values1.put(DatabaseHandler.A10_PIC_PNG_KEY_NAME, combodata.getmA10_PIC_Png_Key_Name());
        values1.put(DatabaseHandler.A10_OBJ_DOWNLOAD_STATUS, combodata.getObjA10DStatus());
        values1.put(DatabaseHandler.A10_TEXTURE_DOWNLOAD_STATUS, combodata.getTextureA10DStatus());

        populateContentValue(values1, DatabaseHandler.COMBO_LOCAL_VIEW_COUNT, combodata.getViewCount());
        populateContentValue(values1, DatabaseHandler.COMBO_LOCAL_IS_LIKED, combodata.isLiked());
        populateContentValue(values1, DatabaseHandler.COMBO_LOCAL_CART_COUNT, combodata.getCartCount());
        populateContentValue(values1, DatabaseHandler.COMBO_LOCAL_IS_DISPLAY_READY, combodata.getIsDisplayReady());
        populateContentValue(values1, DatabaseHandler.DATE_SEEN_IN_MILLI, combodata.getDateSeenInMilli());
        populateContentValue(values1, DatabaseHandler.COMBO_LOCAL_SEEN_FACTOR, combodata.getSeenFactor());

        populateContentValue(values1, DatabaseHandler.UPDATED_DATE, combodata.getComboUpdatedDate());
        //values1.put(DatabaseHandler.SEEN_DATE, combodata.getSeenDate());
        populateContentValue(values1, DatabaseHandler.COMBO_COLOR, combodata.getCombo_Color());
        populateContentValue(values1, DatabaseHandler.DESCRIPTION, combodata.getCombo_Description());
        if (combodata.getLegItem() != null) {
            create(combodata.getLegItem());
        }

        Log.d(LOGTAG, "create comboData ID: " + combodata.getCombo_ID() + "   ");
        long insertId = insertOrUpdate(DatabaseHandler.TABLE_COMBODATA, values1, DatabaseHandler.COMBO_ID);
        if (insertId != 0)
            combodata.setRowId(insertId);
        return combodata;
    }


    private List<ComboData> comboDataCursorToList(Cursor cursor) {
        List<ComboData> comboDataList = new ArrayList<ComboData>();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                ComboData combodata = new ComboData();
                combodata.setCombo_ID(cursor.getString(cursor.getColumnIndex(DatabaseHandler.COMBO_ID)));
                //combodata.setSeenDate(cursor.getLong(cursor.getColumnIndex(DatabaseHandler.SEEN_DATE)));
                combodata.setPbId(cursor.getString(cursor.getColumnIndex(DatabaseHandler.PB_ID)));
                combodata.setFaceId(cursor.getString(cursor.getColumnIndex(DatabaseHandler.FACE_ID)));
                combodata.setLegId(cursor.getString(cursor.getColumnIndex(DatabaseHandler.LEG_ID)));
                if (!(combodata.getLegId() == null || combodata.getLegId().isEmpty() || combodata.getLegId().equals("NA"))) {
                    BaseAccessoryItem accItem = inkarneDataSource.getAccessory(ConstantsUtil.EAccessoryType.eAccTypeLegs.toString(), combodata.getLegId());
                    if (accItem != null) {
                        combodata.setLegItem(accItem);
                    } else {
                        Log.d(LOGTAG, "legObj not found in accessory" + combodata.getLegId());
                    }
                }

                combodata.setCombo_Title(cursor.getString(cursor.getColumnIndex(DatabaseHandler.COMBO_TITLE)));
                combodata.setCombo_Gender(cursor.getString(cursor.getColumnIndex(DatabaseHandler.GENDER)));
                combodata.setCombo_Style_Category(cursor.getString(cursor.getColumnIndex(DatabaseHandler.COMBO_STYLE_CATEGORY)));
                combodata.setCombo_PIC_Png_Key_Name(cursor.getString(cursor.getColumnIndex(DatabaseHandler.COMBO_PIC_PNG_KEY_NAME)));

                combodata.setmSKU_ID1(cursor.getString(cursor.getColumnIndex(DatabaseHandler.SKU_ID1)));
                combodata.setmSKU_ID2(cursor.getString(cursor.getColumnIndex(DatabaseHandler.SKU_ID2)));
                combodata.setmSKU_ID3(cursor.getString(cursor.getColumnIndex(DatabaseHandler.SKU_ID3)));
                combodata.setmSKU_ID4(cursor.getString(cursor.getColumnIndex(DatabaseHandler.SKU_ID4)));
                combodata.setmSKU_ID5(cursor.getString(cursor.getColumnIndex(DatabaseHandler.SKU_ID5)));
                combodata.setmSKU_ID6(cursor.getString(cursor.getColumnIndex(DatabaseHandler.SKU_ID6)));
                combodata.setmSKU_ID7(cursor.getString(cursor.getColumnIndex(DatabaseHandler.SKU_ID7)));
                combodata.setmSKU_ID8(cursor.getString(cursor.getColumnIndex(DatabaseHandler.SKU_ID8)));
                combodata.setmSKU_ID9(cursor.getString(cursor.getColumnIndex(DatabaseHandler.SKU_ID9)));
                combodata.setmSKU_ID10(cursor.getString(cursor.getColumnIndex(DatabaseHandler.SKU_ID10)));

                combodata.setLikes_Count(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.LIKES_COUNT)));
                combodata.setStyle_Rating(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.STYLE_RATING)));

                combodata.setmA1_Category(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A1_CATEGORY)));
                combodata.setmA1_Color(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A1_COLOR)));
                combodata.setmA1_Obj_Key_Name(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A1_OBJ_KEY_NAME)));
                combodata.setmA1_Png_Key_Name(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A1_PNG_KEY_NAME)));
                combodata.setmA1_PIC_Png_Key_Name(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A1_PIC_PNG_KEY_NAME)));
                combodata.setObjA1DStatus(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.A1_OBJ_DOWNLOAD_STATUS)));
                combodata.setTextureA1DStatus(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.A1_TEXTURE_DOWNLOAD_STATUS)));

                combodata.setmA2_Category(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A2_CATEGORY)));
                combodata.setmA2_Color(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A2_COLOR)));
                combodata.setmA2_Obj_Key_Name(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A2_OBJ_KEY_NAME)));
                combodata.setmA2_Png_Key_Name(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A2_PNG_KEY_NAME)));
                combodata.setmA2_PIC_Png_Key_Name(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A2_PIC_PNG_KEY_NAME)));


                combodata.setmA3_Category(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A3_CATEGORY)));
                combodata.setmA3_Color(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A3_COLOR)));
                combodata.setmA3_Obj_Key_Name(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A3_OBJ_KEY_NAME)));
                combodata.setmA3_Png_Key_Name(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A3_PNG_KEY_NAME)));
                combodata.setmA3_PIC_Png_Key_Name(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A3_PIC_PNG_KEY_NAME)));

                combodata.setmA4_Category(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A4_CATEGORY)));
                combodata.setmA4_Color(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A4_COLOR)));
                combodata.setmA4_Obj_Key_Name(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A4_OBJ_KEY_NAME)));
                combodata.setmA4_Png_Key_Name(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A4_PNG_KEY_NAME)));
                combodata.setmA4_PIC_Png_Key_Name(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A4_PIC_PNG_KEY_NAME)));

                combodata.setmA5_Category(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A5_CATEGORY)));
                combodata.setmA5_Obj_Key_Name(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A5_OBJ_KEY_NAME)));
                combodata.setmA5_Png_Key_Name(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A5_PNG_KEY_NAME)));
                combodata.setmA5_PIC_Png_Key_Name(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A5_PIC_PNG_KEY_NAME)));

                combodata.setmA6_Category(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A6_CATEGORY)));
                combodata.setmA6_Obj_Key_Name(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A6_OBJ_KEY_NAME)));
                combodata.setmA6_Png_Key_Name(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A6_PNG_KEY_NAME)));
                combodata.setmA6_PIC_Png_Key_Name(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A6_PIC_PNG_KEY_NAME)));
                combodata.setObjA6DStatus(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.A6_OBJ_DOWNLOAD_STATUS)));
                combodata.setTextureA6DStatus(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.A6_TEXTURE_DOWNLOAD_STATUS)));

                combodata.setmA7_Category(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A7_CATEGORY)));
                combodata.setmA7_Obj_Key_Name(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A7_OBJ_KEY_NAME)));
                combodata.setmA7_Png_Key_Name(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A7_PNG_KEY_NAME)));
                combodata.setmA7_PIC_Png_Key_Name(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A7_PIC_PNG_KEY_NAME)));
                combodata.setObjA7DStatus(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.A7_OBJ_DOWNLOAD_STATUS)));
                combodata.setTextureA7DStatus(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.A7_TEXTURE_DOWNLOAD_STATUS)));

                combodata.setmA8_Category(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A8_CATEGORY)));
                combodata.setmA8_Obj_Key_Name(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A8_OBJ_KEY_NAME)));
                combodata.setmA8_Png_Key_Name(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A8_PNG_KEY_NAME)));
                combodata.setmA8_PIC_Png_Key_Name(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A8_PIC_PNG_KEY_NAME)));

                combodata.setmA9_Category(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A9_CATEGORY)));
                combodata.setmA9_Obj_Key_Name(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A9_OBJ_KEY_NAME)));
                combodata.setmA9_Png_Key_Name(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A9_PNG_KEY_NAME)));
                combodata.setmA9_PIC_Png_Key_Name(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A9_PIC_PNG_KEY_NAME)));
                combodata.setObjA9DStatus(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.A7_OBJ_DOWNLOAD_STATUS)));
                combodata.setTextureA9DStatus(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.A7_TEXTURE_DOWNLOAD_STATUS)));

                combodata.setmA10_Category(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A10_CATEGORY)));
                combodata.setmA10_Obj_Key_Name(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A10_OBJ_KEY_NAME)));
                combodata.setmA10_Png_Key_Name(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A10_PNG_KEY_NAME)));
                combodata.setmA10_PIC_Png_Key_Name(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A10_PIC_PNG_KEY_NAME)));
                combodata.setObjA10DStatus(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.A10_OBJ_DOWNLOAD_STATUS)));
                combodata.setTextureA10DStatus(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.A10_TEXTURE_DOWNLOAD_STATUS)));

                combodata.setForced_Rank(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.FORCED_RANK)));
                combodata.setPush_Flag(cursor.getString(cursor.getColumnIndex(DatabaseHandler.PUSH_FLAG)));
                combodata.setVogue_Flag(cursor.getString(cursor.getColumnIndex(DatabaseHandler.VOGUE_FLAG)));
                //combodata.setCombo_Color(cursor.getString(cursor.getColumnIndex(DatabaseHandler.COMBO_COLOR)));
                //combodata.setCombo_Description(cursor.getString(cursor.getColumnIndex(DatabaseHandler.DESCRIPTION)));

                combodata.setViewCount(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.COMBO_LOCAL_VIEW_COUNT)));
                combodata.setCartCount(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.COMBO_LOCAL_CART_COUNT)));
                combodata.setIsLiked(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.COMBO_LOCAL_IS_LIKED)));
                combodata.setIsDisplayReady(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.COMBO_LOCAL_IS_DISPLAY_READY)));
                //combodata.setSeenDate(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.SEEN_DATE)));
                combodata.setUpdatedDate(cursor.getString(cursor.getColumnIndex(DatabaseHandler.UPDATED_DATE)));
                combodata.setSeenFactor(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.COMBO_LOCAL_SEEN_FACTOR)));
                combodata.setDateSeenInMilli((cursor.getLong(cursor.getColumnIndex(DatabaseHandler.DATE_SEEN_IN_MILLI)))/1000);
                comboDataList.add(combodata);
            }
        }
        cursor.close();
        return comboDataList;
    }

    /************************
     * RECONCILE COMBO
     ***********************************/
    public ComboDataReconcile createReconcile(ComboDataReconcile combodata,long reconcileDateInMilli) {
        ContentValues values1 = new ContentValues();
        values1.put(DatabaseHandler.COMBO_ID, combodata.getCombo_ID());
        populateContentValue(values1, DatabaseHandler.COMBO_TITLE, combodata.getCombo_Title());

        values1.put(DatabaseHandler.GENDER, combodata.getCombo_Gender());
        populateContentValue(values1, DatabaseHandler.COMBO_STYLE_CATEGORY, combodata.getCombo_Style_Category());

        values1.put(DatabaseHandler.COMBO_PIC_PNG_KEY_NAME, combodata.getCombo_PIC_Png_Key_Name());
        populateContentValue(values1, DatabaseHandler.COMBO_TITLE, combodata.getCombo_Title());

        values1.put(DatabaseHandler.FORCED_RANK, combodata.getForced_Rank());
        populateContentValue(values1, DatabaseHandler.PUSH_FLAG, combodata.getPush_Flag());
        populateContentValue(values1, DatabaseHandler.VOGUE_FLAG, combodata.getVogue_Flag());

        populateContentValue(values1, DatabaseHandler.A1_CATEGORY, combodata.getmA1_Category());
        populateContentValue(values1, DatabaseHandler.A1_COLOR, combodata.getmA1_Color());

        populateContentValue(values1, DatabaseHandler.A2_CATEGORY, combodata.getmA2_Category());
        populateContentValue(values1, DatabaseHandler.A2_COLOR, combodata.getmA2_Color());

        populateContentValue(values1, DatabaseHandler.A3_CATEGORY, combodata.getmA3_Category());
        populateContentValue(values1, DatabaseHandler.A3_COLOR, combodata.getmA3_Color());

        populateContentValue(values1, DatabaseHandler.A4_CATEGORY, combodata.getmA4_Category());
        populateContentValue(values1, DatabaseHandler.A4_COLOR, combodata.getmA4_Color());
        populateContentValue(values1, DatabaseHandler.DATE_RECONCILE_IN_MILLI, reconcileDateInMilli);

        populateContentValue(values1, DatabaseHandler.COMBO_LOCAL_VIEW_COUNT, combodata.getViewCount());
        populateContentValue(values1, DatabaseHandler.COMBO_LOCAL_IS_LIKED, combodata.isLiked());
        populateContentValue(values1, DatabaseHandler.COMBO_LOCAL_CART_COUNT, combodata.getCartCount());
        populateContentValue(values1, DatabaseHandler.COMBO_LOCAL_IS_DISPLAY_READY, combodata.getIsDisplayReady());

        populateContentValue(values1, DatabaseHandler.COMBO_COLOR, combodata.getCombo_Color());
        populateContentValue(values1, DatabaseHandler.DESCRIPTION, combodata.getCombo_Description());

        values1.put(DatabaseHandler.LIKES_COUNT, combodata.getLikes_Count());
        values1.put(DatabaseHandler.STYLE_RATING, combodata.getStyle_Rating());

        Log.d(LOGTAG, "comboDataReconcile ID: " + combodata.getCombo_ID() + "   ");
        long insertId = insertOrUpdate(DatabaseHandler.TABLE_COMBODATA, values1, DatabaseHandler.COMBO_ID);
        if (insertId != 0)
            combodata.setRowId(insertId);
        return combodata;
    }


    public ComboDataReconcile createReconcile(ComboDataReconcile combodata) {
        return createReconcile(combodata,0);
    }

    private List<ComboData> comboReconcileCursorToList(Cursor cursor) {
        List<ComboData> combodataList = new ArrayList<ComboData>();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                ComboData combodata = new ComboData();
                combodata.setCombo_ID(cursor.getString(cursor.getColumnIndex(DatabaseHandler.COMBO_ID)));

                combodata.setCombo_Title(cursor.getString(cursor.getColumnIndex(DatabaseHandler.COMBO_TITLE)));
                combodata.setCombo_Gender(cursor.getString(cursor.getColumnIndex(DatabaseHandler.GENDER)));
                combodata.setCombo_Style_Category(cursor.getString(cursor.getColumnIndex(DatabaseHandler.COMBO_STYLE_CATEGORY)));
                combodata.setCombo_PIC_Png_Key_Name(cursor.getString(cursor.getColumnIndex(DatabaseHandler.COMBO_PIC_PNG_KEY_NAME)));

                combodata.setLikes_Count(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.LIKES_COUNT)));
                combodata.setStyle_Rating(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.STYLE_RATING)));

                combodata.setmA1_Category(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A1_CATEGORY)));
                combodata.setmA1_Color(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A1_COLOR)));
                combodata.setmA1_Obj_Key_Name(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A1_OBJ_KEY_NAME)));
                combodata.setmA1_Png_Key_Name(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A1_PNG_KEY_NAME)));

                combodata.setmA2_Category(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A2_CATEGORY)));
                combodata.setmA2_Color(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A2_COLOR)));

                combodata.setmA3_Category(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A3_CATEGORY)));
                combodata.setmA3_Color(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A3_COLOR)));

                combodata.setmA4_Category(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A4_CATEGORY)));
                combodata.setmA4_Color(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A4_COLOR)));

                combodata.setForced_Rank(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.FORCED_RANK)));
                combodata.setPush_Flag(cursor.getString(cursor.getColumnIndex(DatabaseHandler.PUSH_FLAG)));
                combodata.setVogue_Flag(cursor.getString(cursor.getColumnIndex(DatabaseHandler.VOGUE_FLAG)));
                combodata.setDateReconcile((cursor.getLong(cursor.getColumnIndex(DatabaseHandler.DATE_RECONCILE_IN_MILLI))));
                combodata.setDateSeenInMilli(cursor.getLong(cursor.getColumnIndex(DatabaseHandler.DATE_SEEN_IN_MILLI))/1000);
                combodata.setViewCount(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.COMBO_LOCAL_VIEW_COUNT)));
                combodata.setCartCount(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.COMBO_LOCAL_CART_COUNT)));
                combodata.setIsLiked(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.COMBO_LOCAL_IS_LIKED)));
                combodata.setIsDisplayReady(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.COMBO_LOCAL_IS_DISPLAY_READY)));
                combodataList.add(combodata);
            }
        }
        cursor.close();
        return combodataList;
    }


    /************************
     * LIKED COMBO
     ***********************************/
    public ComboData createComboLike(ComboData combodata) {
        ContentValues values1 = new ContentValues();
        values1.put(DatabaseHandler.COMBO_ID, combodata.getCombo_ID());
        populateContentValue(values1, DatabaseHandler.PB_ID, combodata.getPbId());
        populateContentValue(values1, DatabaseHandler.FACE_ID, combodata.getFaceId());
        populateContentValue(values1, DatabaseHandler.LEG_ID, combodata.getLegId());
        if(!(combodata.getLegId() == null  || combodata.getLegId().equals("NA"))) {
                if (combodata.getLegItem() == null) {
                    BaseAccessoryItem accItem = inkarneDataSource.getAccessory(ConstantsUtil.EAccessoryType.eAccTypeLegs.toString(), combodata.getLegId());
                    if (accItem != null) {
                        populateContentValue(values1, DatabaseHandler.LEG_OBJ_KEY_NAME, combodata.getLegItem().getObjAwsKey());
                        populateContentValue(values1, DatabaseHandler.LEG_TEX_KEY_NAME, combodata.getLegItem().getTextureAwsKey());
                    }
                } else {
                    populateContentValue(values1, DatabaseHandler.LEG_OBJ_KEY_NAME, combodata.getLegItem().getObjAwsKey());
                    populateContentValue(values1, DatabaseHandler.LEG_TEX_KEY_NAME, combodata.getLegItem().getTextureAwsKey());
                }
        }

        values1.put(DatabaseHandler.SKU_ID1, combodata.getmSKU_ID1());
        values1.put(DatabaseHandler.SKU_ID6, combodata.getmSKU_ID6());
        values1.put(DatabaseHandler.SKU_ID7, combodata.getmSKU_ID7());
        values1.put(DatabaseHandler.SKU_ID8, combodata.getmSKU_ID8());
        values1.put(DatabaseHandler.SKU_ID9, combodata.getmSKU_ID9());
        values1.put(DatabaseHandler.SKU_ID10, combodata.getmSKU_ID10());
        values1.put(DatabaseHandler.LIKES_COUNT, combodata.getLikes_Count());

        populateContentValue(values1, DatabaseHandler.A6_CATEGORY, combodata.getmA6_Category());
        values1.put(DatabaseHandler.A6_OBJ_KEY_NAME, combodata.getmA6_Obj_Key_Name());
        values1.put(DatabaseHandler.A6_PNG_KEY_NAME, combodata.getmA6_Png_Key_Name());
        values1.put(DatabaseHandler.A6_PIC_PNG_KEY_NAME, combodata.getmA6_PIC_Png_Key_Name());
        values1.put(DatabaseHandler.A6_OBJ_DOWNLOAD_STATUS, combodata.getObjA6DStatus());
        values1.put(DatabaseHandler.A6_TEXTURE_DOWNLOAD_STATUS, combodata.getTextureA6DStatus());

        populateContentValue(values1, DatabaseHandler.A7_CATEGORY, combodata.getmA7_Category());
        values1.put(DatabaseHandler.A7_OBJ_KEY_NAME, combodata.getmA7_Obj_Key_Name());
        values1.put(DatabaseHandler.A7_PNG_KEY_NAME, combodata.getmA7_Png_Key_Name());
        values1.put(DatabaseHandler.A7_PIC_PNG_KEY_NAME, combodata.getmA7_PIC_Png_Key_Name());
        values1.put(DatabaseHandler.A7_OBJ_DOWNLOAD_STATUS, combodata.getObjA7DStatus());
        values1.put(DatabaseHandler.A7_TEXTURE_DOWNLOAD_STATUS, combodata.getTextureA7DStatus());

        populateContentValue(values1, DatabaseHandler.A8_CATEGORY, combodata.getmA8_Category());
        values1.put(DatabaseHandler.A8_CATEGORY, combodata.getmA8_Category());
        values1.put(DatabaseHandler.A8_OBJ_KEY_NAME, combodata.getmA8_Obj_Key_Name());
        values1.put(DatabaseHandler.A8_PNG_KEY_NAME, combodata.getmA8_Png_Key_Name());
        values1.put(DatabaseHandler.A8_PIC_PNG_KEY_NAME, combodata.getmA8_PIC_Png_Key_Name());
        values1.put(DatabaseHandler.A8_OBJ_DOWNLOAD_STATUS, combodata.getObjA8DStatus());
        values1.put(DatabaseHandler.A8_TEXTURE_DOWNLOAD_STATUS, combodata.getTextureA8DStatus());

        populateContentValue(values1, DatabaseHandler.A9_CATEGORY, combodata.getmA9_Category());
        values1.put(DatabaseHandler.A9_OBJ_KEY_NAME, combodata.getmA9_Obj_Key_Name());
        values1.put(DatabaseHandler.A9_PNG_KEY_NAME, combodata.getmA9_Png_Key_Name());
        values1.put(DatabaseHandler.A9_PIC_PNG_KEY_NAME, combodata.getmA9_PIC_Png_Key_Name());
        values1.put(DatabaseHandler.A9_OBJ_DOWNLOAD_STATUS, combodata.getObjA9DStatus());
        values1.put(DatabaseHandler.A9_TEXTURE_DOWNLOAD_STATUS, combodata.getTextureA9DStatus());

        populateContentValue(values1, DatabaseHandler.A10_CATEGORY, combodata.getmA10_Category());
        values1.put(DatabaseHandler.A10_OBJ_KEY_NAME, combodata.getmA10_Obj_Key_Name());
        values1.put(DatabaseHandler.A10_PNG_KEY_NAME, combodata.getmA10_Png_Key_Name());
        values1.put(DatabaseHandler.A10_PIC_PNG_KEY_NAME, combodata.getmA10_PIC_Png_Key_Name());
        values1.put(DatabaseHandler.A10_OBJ_DOWNLOAD_STATUS, combodata.getObjA10DStatus());
        values1.put(DatabaseHandler.A10_TEXTURE_DOWNLOAD_STATUS, combodata.getTextureA10DStatus());
        if (combodata.getLegItem() != null) {
            create(combodata.getLegItem());
        }
        Log.d(LOGTAG, "create comboData ID: " + combodata.getCombo_ID() + "   ");
        long insertId = insertOrUpdate(DatabaseHandler.TABLE_COMBODATA_LIKE, values1, DatabaseHandler.COMBO_ID);
        if (insertId != 0)
            combodata.setRowId(insertId);
        return combodata;
    }

    private List<ComboData> comboDataLikeCursorToList(Cursor cursor) {
        List<ComboData> comboDataList = new ArrayList<ComboData>();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                ComboData combodata = new ComboData();
                combodata.setCombo_ID(cursor.getString(cursor.getColumnIndex(DatabaseHandler.COMBO_ID)));
                combodata.setPbId(cursor.getString(cursor.getColumnIndex(DatabaseHandler.PB_ID)));
                combodata.setFaceId(cursor.getString(cursor.getColumnIndex(DatabaseHandler.FACE_ID)));
                combodata.setLegId(cursor.getString(cursor.getColumnIndex(DatabaseHandler.LEG_ID)));
                combodata.setLegTextureAwsKey(cursor.getString(cursor.getColumnIndex(DatabaseHandler.LEG_TEX_KEY_NAME)));
                combodata.setLegObjAwsKey(cursor.getString(cursor.getColumnIndex(DatabaseHandler.LEG_OBJ_KEY_NAME)));
//                if (combodata.getLegId() != null && !combodata.getLegId().isEmpty()) {
//                    BaseAccessoryItem accItem = inkarneDataSource.getAccessory(ConstantsUtil.EAccessoryType.eAccTypeLegs.toString(), combodata.getLegId());
//                    if (accItem != null) {
//                        combodata.setLegItem(accItem);
//                    } else {
//                        Log.d(LOGTAG, "legObj not found in accessory" + combodata.getLegId());
//                    }
//                }
                combodata.setmSKU_ID1(cursor.getString(cursor.getColumnIndex(DatabaseHandler.SKU_ID1)));
                combodata.setmSKU_ID6(cursor.getString(cursor.getColumnIndex(DatabaseHandler.SKU_ID6)));
                combodata.setmSKU_ID7(cursor.getString(cursor.getColumnIndex(DatabaseHandler.SKU_ID7)));
                combodata.setmSKU_ID8(cursor.getString(cursor.getColumnIndex(DatabaseHandler.SKU_ID8)));
                combodata.setmSKU_ID9(cursor.getString(cursor.getColumnIndex(DatabaseHandler.SKU_ID9)));
                combodata.setmSKU_ID10(cursor.getString(cursor.getColumnIndex(DatabaseHandler.SKU_ID10)));

                combodata.setmA6_Category(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A6_CATEGORY)));
                combodata.setmA6_Obj_Key_Name(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A6_OBJ_KEY_NAME)));
                combodata.setmA6_Png_Key_Name(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A6_PNG_KEY_NAME)));
                combodata.setmA6_PIC_Png_Key_Name(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A6_PIC_PNG_KEY_NAME)));
                combodata.setObjA6DStatus(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.A6_OBJ_DOWNLOAD_STATUS)));
                combodata.setTextureA6DStatus(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.A6_TEXTURE_DOWNLOAD_STATUS)));

                combodata.setmA7_Category(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A7_CATEGORY)));
                combodata.setmA7_Obj_Key_Name(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A7_OBJ_KEY_NAME)));
                combodata.setmA7_Png_Key_Name(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A7_PNG_KEY_NAME)));
                combodata.setmA7_PIC_Png_Key_Name(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A7_PIC_PNG_KEY_NAME)));
                combodata.setObjA7DStatus(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.A7_OBJ_DOWNLOAD_STATUS)));
                combodata.setTextureA7DStatus(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.A7_TEXTURE_DOWNLOAD_STATUS)));

                combodata.setmA8_Category(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A8_CATEGORY)));
                combodata.setmA8_Obj_Key_Name(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A8_OBJ_KEY_NAME)));
                combodata.setmA8_Png_Key_Name(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A8_PNG_KEY_NAME)));
                combodata.setmA8_PIC_Png_Key_Name(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A8_PIC_PNG_KEY_NAME)));

                combodata.setmA9_Category(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A9_CATEGORY)));
                combodata.setmA9_Obj_Key_Name(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A9_OBJ_KEY_NAME)));
                combodata.setmA9_Png_Key_Name(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A9_PNG_KEY_NAME)));
                combodata.setmA9_PIC_Png_Key_Name(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A9_PIC_PNG_KEY_NAME)));
                combodata.setObjA9DStatus(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.A7_OBJ_DOWNLOAD_STATUS)));
                combodata.setTextureA9DStatus(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.A7_TEXTURE_DOWNLOAD_STATUS)));

                combodata.setmA10_Category(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A10_CATEGORY)));
                combodata.setmA10_Obj_Key_Name(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A10_OBJ_KEY_NAME)));
                combodata.setmA10_Png_Key_Name(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A10_PNG_KEY_NAME)));
                combodata.setmA10_PIC_Png_Key_Name(cursor.getString(cursor.getColumnIndex(DatabaseHandler.A10_PIC_PNG_KEY_NAME)));
                combodata.setObjA10DStatus(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.A10_OBJ_DOWNLOAD_STATUS)));
                combodata.setTextureA10DStatus(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.A10_TEXTURE_DOWNLOAD_STATUS)));

                comboDataList.add(combodata);
            }
        }
        cursor.close();
        return comboDataList;
    }

    /***********
     * QUERY COMBO
     ************/


    public List<ComboData> getDownloadedComboData(int count) {
        String orderBy = DatabaseHandler.DATE_RECONCILE_IN_MILLI + " ASC, "
                      + DatabaseHandler.COMBO_LOCAL_VIEW_COUNT + " DESC ";

        String whereClause = DatabaseHandler.A1_TEXTURE_DOWNLOAD_STATUS + " = ?";
        String[] whereValue = new String[]{String.valueOf(ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus())};
        Cursor cursor1 = database.query(DatabaseHandler.TABLE_COMBODATA, combodataColumns, whereClause, whereValue, null, null, orderBy, String.valueOf(count));
        List<ComboData> combodataList1 = comboDataCursorToList(cursor1);
        cursor1.close();

//        String whereClause2 = DatabaseHandler.A1_OBJ_DOWNLOAD_STATUS + " = ?";
//        String[] whereValue2 = new String[]{String.valueOf(ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus())};
//        Cursor cursor2 = database.query(DatabaseHandler.TABLE_COMBODATA, combodataColumns, whereClause2, whereValue2, null, null, orderBy, String.valueOf(count));
//        List<ComboData> combodataList2 = comboDataCursorToList(cursor2);
//
//
//        Cursor cursor = database.query(DatabaseHandler.TABLE_COMBODATA, combodataColumns,
//                null, null, null, null, orderBy, String.valueOf(count));
//        Log.i(LOGTAG, "getDownloadedComboData : " + cursor.getCount() + " rows");
//        List<ComboData> combodataList = comboDataCursorToList(cursor);
//        cursor.close();
        return combodataList1;
    }

    public List<ComboData>getComboReconcileByCategory(String category,boolean shouldRearrangeData){
        String orderBy = null;
        if(shouldRearrangeData) {
            orderBy = DatabaseHandler.DATE_RECONCILE_IN_MILLI + " DESC, "
                    + DatabaseHandler.COMBO_LOCAL_VIEW_COUNT + " ASC, " + DatabaseHandler.FORCED_RANK + " ASC " ;
        }else{
            orderBy = DatabaseHandler.DATE_RECONCILE_IN_MILLI + " DESC, " + DatabaseHandler.FORCED_RANK + " ASC, "
                    + DatabaseHandler.COMBO_LOCAL_VIEW_COUNT + " ASC ";
        }
        Cursor cursor = null;
        if(category.equals(ConstantsUtil.arrayListLooksLabelName.get(0).toUpperCase())) {
             cursor = database.query(DatabaseHandler.TABLE_COMBODATA, comboReconcileColumns,
                    DatabaseHandler.VOGUE_FLAG + "=?", new String[]{"True"}, null, null, orderBy, null);
        }
        //Your Likes
        else if(category.equals(ConstantsUtil.arrayListLooksLabelName.get(ConstantsUtil.arrayListLooksLabelName.size()-2).toUpperCase())) {
            orderBy = DatabaseHandler.DATE_SEEN_IN_MILLI + " DESC " ;
            cursor = database.query(DatabaseHandler.TABLE_COMBODATA, comboReconcileColumns,
                    DatabaseHandler.COMBO_LOCAL_IS_LIKED + "!= ?", new String[]{"0"}, null, null, orderBy, null);
        }
        //History
        else if(category.equals(ConstantsUtil.arrayListLooksLabelName.get(ConstantsUtil.arrayListLooksLabelName.size()-1).toUpperCase())) {
            orderBy = DatabaseHandler.DATE_SEEN_IN_MILLI + " DESC " ;
            cursor = database.query(DatabaseHandler.TABLE_COMBODATA, comboReconcileColumns,
                    DatabaseHandler.COMBO_LOCAL_VIEW_COUNT + "!= ?", new String[]{"0"}, null, null, orderBy, null);
        }
        else{
            cursor = database.query(DatabaseHandler.TABLE_COMBODATA, comboReconcileColumns,
                    DatabaseHandler.COMBO_STYLE_CATEGORY + "=?", new String[]{category}, null, null, orderBy, null);
        }
        Log.i(LOGTAG, "getComboReconcileByCategory : " + cursor.getCount() + " rows**");
        List<ComboData> combodataList = comboReconcileCursorToList(cursor);
        cursor.close();
        return combodataList;
    }

    public List<ComboData>getComboReconcileTrendingData(boolean shouldRearrangeData){
        String orderBy = null;
        if(shouldRearrangeData) {
            orderBy = DatabaseHandler.DATE_RECONCILE_IN_MILLI + " DESC, "
                    + DatabaseHandler.COMBO_LOCAL_VIEW_COUNT + " ASC, " + DatabaseHandler.FORCED_RANK + " ASC " ;
        }else{
            orderBy = DatabaseHandler.DATE_RECONCILE_IN_MILLI + " DESC, " + DatabaseHandler.FORCED_RANK + " ASC, "
                    + DatabaseHandler.COMBO_LOCAL_VIEW_COUNT + " ASC ";
        }

        Cursor cursor = database.query(DatabaseHandler.TABLE_COMBODATA, comboReconcileColumns,
                DatabaseHandler.VOGUE_FLAG+"=?", new String[]{"True"}, null, null, orderBy, null);
        Log.i(LOGTAG, "getComboReconcileTrendingData : " + cursor.getCount() + " rows**");
        List<ComboData> combodataList = comboReconcileCursorToList(cursor);
        cursor.close();
        return combodataList;
    }

    public List<ComboData> getComboReconcileData(boolean shouldRearrangeData) {
        String orderBy = null;//
        if(shouldRearrangeData) {
            orderBy = DatabaseHandler.DATE_RECONCILE_IN_MILLI + " DESC, "
                    + DatabaseHandler.COMBO_LOCAL_VIEW_COUNT + " ASC ,"
                    + DatabaseHandler.STYLE_RATING + " DESC ";
        }else {
            orderBy = DatabaseHandler.DATE_RECONCILE_IN_MILLI + " DESC, " + DatabaseHandler.STYLE_RATING + " DESC ";
        }
        Cursor cursor = database.query(DatabaseHandler.TABLE_COMBODATA, comboReconcileColumns,
                null, null, null, null, orderBy, null);
        Log.i(LOGTAG, "getComboReconcileData : " + cursor.getCount() + " rows");
        List<ComboData> combodataList = comboReconcileCursorToList(cursor);
        cursor.close();
        return combodataList;
    }

    public ComboData getComboLikeDataByComboID(String comboId) {
        Cursor cursor = database.query(DatabaseHandler.TABLE_COMBODATA_LIKE, combodataLikeColumns,
                DatabaseHandler.COMBO_ID + "=?", new String[]{comboId}, null, null, null, null);
        Log.i(LOGTAG, "getComboLikeDataByComboID : " + cursor.getCount() + " rows");
        List<ComboData> combodataList = comboDataLikeCursorToList(cursor);
        cursor.close();
        if (combodataList != null && combodataList.size() != 0)
            return combodataList.get(0);
        else
            return null;
    }


//    public List<ComboData> getComboDataDetails() {
//        String orderBy = DatabaseHandler.COMBO_LOCAL_VIEW_COUNT + " ASC, " + DatabaseHandler.FORCED_RANK + " ASC";
//        Cursor cursor = database.query(DatabaseHandler.TABLE_COMBODATA, combodataColumns,
//                null, null, null, null, orderBy, null);
//        Log.i(LOGTAG, "Returned Combo Data : " + cursor.getCount() + " rows");
//        List<ComboData> combodataList = comboDataCursorToList(cursor);
//        cursor.close();
//        return combodataList;
//    }

    public long getComboCount() {
        long cnt  = DatabaseUtils.queryNumEntries(database, DatabaseHandler.TABLE_COMBODATA);
        return cnt;
    }



    public List<ComboData> getComboDataLaunchUnseen(int count) {
        count = 6;
        String limit = String.valueOf(count);
        String orderByUnseen =  DatabaseHandler.COMBO_LOCAL_VIEW_COUNT + " ASC ,"
                +  DatabaseHandler.PUSH_FLAG + " DESC, "
                + DatabaseHandler.DATE_RECONCILE_IN_MILLI + " DESC ";
           // + DatabaseHandler.UPDATED_DATE + " DESC, " + DatabaseHandler.STYLE_RATING + " DESC ";
//        Cursor cursor = database.query(DatabaseHandler.TABLE_COMBODATA, combodataColumns,
//                DatabaseHandler.PUSH_FLAG + "=?", new String[]{"False"}, null, null, orderByUnseen, limit);

//        Cursor cursor = database.query(DatabaseHandler.TABLE_COMBODATA, combodataColumns,
//                DatabaseHandler.COMBO_LOCAL_IS_DISPLAY_READY + "=?", new String[]{"0"}, null, null, orderByUnseen, limit);

       // Cursor cursor = database.query(DatabaseHandler.TABLE_COMBODATA, combodataColumns,
       //     null,null, null, null, orderByUnseen, limit);

        Cursor cursor = database.query(DatabaseHandler.TABLE_COMBODATA, comboReconcileColumns,
                DatabaseHandler.VOGUE_FLAG+"=?", new String[]{"True"}, null, null, orderByUnseen, limit);

        Log.i(LOGTAG, "getComboDataUnseenLaunch : " + cursor.getCount() + " rows");
        List<ComboData> comboDataListUnseen = comboReconcileCursorToList(cursor);
        cursor.close();
        return comboDataListUnseen;
    }

    public ComboData getComboDataSeen(){
        List<ComboData> list = getComboDataLaunchSeen(2);
        if(list != null && list.size()>0)
            return  list.get(0);
        else return null;
    }

    public List<ComboData> getComboDataLaunchSeen(int count) {
        //DatabaseHandler.COMBO_LOCAL_IS_DISPLAY_READY + "=?", new String[]{"1"}
        String limit = String.valueOf(count);
        String orderBySeen =   DatabaseHandler.COMBO_LOCAL_IS_DISPLAY_READY + " DESC, "
                + DatabaseHandler.PUSH_FLAG + " DESC, "
                + DatabaseHandler.DATE_RECONCILE_IN_MILLI + " DESC ";
        //Cursor cursor = database.query(DatabaseHandler.TABLE_COMBODATA, combodataColumns,
        //        DatabaseHandler.PUSH_FLAG + "=? AND "+ DatabaseHandler.COMBO_LOCAL_IS_DISPLAY_READY + "=?", new String[]{"True","1"}, null, null, orderByUnseen, limit);

//        Cursor cursor = database.query(DatabaseHandler.TABLE_COMBODATA, combodataColumns,
//                DatabaseHandler.COMBO_LOCAL_IS_DISPLAY_READY + "=?", new String[]{"1"}, null, null, orderByUnseen, limit);

        //Cursor cursor = database.query(DatabaseHandler.TABLE_COMBODATA, combodataColumns,
         //       null, null, null, null, orderByUnseen, limit);

        Cursor cursor = database.query(DatabaseHandler.TABLE_COMBODATA, comboReconcileColumns,
                DatabaseHandler.VOGUE_FLAG+"=?", new String[]{"True"}, null, null, orderBySeen, limit);

        Log.i(LOGTAG, "getComboDataUnseenLaunch : " + cursor.getCount() + " rows");
        List<ComboData> comboDataListUnseen = comboReconcileCursorToList(cursor);
        cursor.close();
        return comboDataListUnseen;
    }


    public ComboData getComboDataByComboID(String comboID) {
        Cursor cursor = database.query(DatabaseHandler.TABLE_COMBODATA, combodataColumns,
                DatabaseHandler.COMBO_ID + "=?", new String[]{comboID}, null, null, null, null);
        Log.i(LOGTAG, "getComboDataByComboID : " + comboID + "   : " + cursor.getCount() + " rows");
        List<ComboData> comboDataList = comboDataCursorToList(cursor);
        cursor.close();
        if (comboDataList != null && comboDataList.size() > 0)
            return comboDataList.get(0);
        else return null;
    }


    /***********
     * DELETE COMBO
     ************/

    public long deleteComboDetailLikeById(String comboId) {
        long insertId = database.delete(DatabaseHandler.TABLE_COMBODATA_LIKE, DatabaseHandler.COMBO_ID + " =? ", new String[]{comboId});
        return insertId;
    }

    public long deleteComboDetailLikeForPBChange(String pbId) {
        int status = ConstantsUtil.EDownloadStatusType.eDownloadTobeStarted.intStatus();
        ContentValues values1 = new ContentValues();

        values1.put(DatabaseHandler.PB_ID, pbId);

        values1.put(DatabaseHandler.A7_OBJ_KEY_NAME, "");
        values1.put(DatabaseHandler.A7_PNG_KEY_NAME, "");
        values1.put(DatabaseHandler.A7_OBJ_DOWNLOAD_STATUS, status);
        values1.put(DatabaseHandler.A7_TEXTURE_DOWNLOAD_STATUS, status);

        //long insertId = insertOrUpdate(DatabaseHandler.TABLE_COMBODATA_LIKE,values1,DatabaseHandler.PB_ID);
        long insertId = database.update(DatabaseHandler.TABLE_COMBODATA_LIKE, values1, DatabaseHandler.PB_ID + " =? ", new String[]{pbId});
        return insertId;
    }

    public long deleteComboDetailLikeForFaceChange(String faceId) {
        int status = ConstantsUtil.EDownloadStatusType.eDownloadTobeStarted.intStatus();
        ContentValues values1 = new ContentValues();

        values1.put(DatabaseHandler.FACE_ID, faceId);
//        values1.put(DatabaseHandler.A1_OBJ_KEY_NAME, "");
//        values1.put(DatabaseHandler.A1_PNG_KEY_NAME, "");
        values1.put(DatabaseHandler.A6_OBJ_KEY_NAME, "");
        values1.put(DatabaseHandler.A6_PNG_KEY_NAME, "");
        values1.put(DatabaseHandler.A6_OBJ_DOWNLOAD_STATUS, status);
        values1.put(DatabaseHandler.A6_TEXTURE_DOWNLOAD_STATUS, status);

        values1.put(DatabaseHandler.LEG_OBJ_KEY_NAME, "");
        values1.put(DatabaseHandler.LEG_TEX_KEY_NAME, "");
        //values1.put(DatabaseHandler.A7_OBJ_DOWNLOAD_STATUS, status);
        //values1.put(DatabaseHandler.A7_TEXTURE_DOWNLOAD_STATUS, status);

        values1.put(DatabaseHandler.A8_OBJ_KEY_NAME, "");
        values1.put(DatabaseHandler.A8_PNG_KEY_NAME, "");
        values1.put(DatabaseHandler.A8_OBJ_DOWNLOAD_STATUS, status);
        values1.put(DatabaseHandler.A8_TEXTURE_DOWNLOAD_STATUS, status);

        values1.put(DatabaseHandler.A9_OBJ_KEY_NAME, "");
        values1.put(DatabaseHandler.A9_PNG_KEY_NAME, "");
        values1.put(DatabaseHandler.A9_OBJ_DOWNLOAD_STATUS, status);
        values1.put(DatabaseHandler.A9_TEXTURE_DOWNLOAD_STATUS, status);

        //long insertId = database.update(DatabaseHandler.TABLE_COMBODATA_LIKE, values1, DatabaseHandler.FACE_ID + " =? ", new String[]{faceId});
        long insertId = database.update(DatabaseHandler.TABLE_COMBODATA_LIKE, values1, null, null);
        return insertId;
    }

    public long deleteComboDetailForPBChange(String pbId) {
//      Cursor cursor = queryData(DatabaseHandler.TABLE_COMBODATA, combodataColumns, new String[]{DatabaseHandler.PB_ID}, new String[]{pbId}, null, null);
//      List<ComboData> comboDataList = comboDataCursorToList(cursor);
        int status = ConstantsUtil.EDownloadStatusType.eDownloadTobeStarted.intStatus();
        ContentValues cv = new ContentValues();

        cv.put(DatabaseHandler.LEG_ID, "");
        cv.put(DatabaseHandler.A1_OBJ_KEY_NAME, "");
        cv.put(DatabaseHandler.A1_PNG_KEY_NAME, "");
        cv.put(DatabaseHandler.A7_OBJ_KEY_NAME, "");
        cv.put(DatabaseHandler.A7_PNG_KEY_NAME, "");
        cv.put(DatabaseHandler.PB_ID, pbId);

        cv.put(DatabaseHandler.COMBO_LOCAL_IS_DISPLAY_READY, 0);
        cv.put(DatabaseHandler.A1_OBJ_DOWNLOAD_STATUS, status);
        cv.put(DatabaseHandler.A1_TEXTURE_DOWNLOAD_STATUS, status);
        cv.put(DatabaseHandler.A6_OBJ_DOWNLOAD_STATUS, status);
        cv.put(DatabaseHandler.A6_TEXTURE_DOWNLOAD_STATUS, status);
        cv.put(DatabaseHandler.A7_OBJ_DOWNLOAD_STATUS, status);
        cv.put(DatabaseHandler.A7_TEXTURE_DOWNLOAD_STATUS, status);
        cv.put(DatabaseHandler.A8_OBJ_DOWNLOAD_STATUS, status);
        cv.put(DatabaseHandler.A8_TEXTURE_DOWNLOAD_STATUS, status);
        cv.put(DatabaseHandler.A9_OBJ_DOWNLOAD_STATUS, status);
        cv.put(DatabaseHandler.A9_TEXTURE_DOWNLOAD_STATUS, status);
        cv.put(DatabaseHandler.A10_OBJ_DOWNLOAD_STATUS, status);
        cv.put(DatabaseHandler.A10_TEXTURE_DOWNLOAD_STATUS, status);
        long insertId = database.update(DatabaseHandler.TABLE_COMBODATA, cv, DatabaseHandler.PB_ID + " =? ", new String[]{pbId});
        return insertId;
    }

    public long deleteComboDetailForFaceChange(String faceId) {
        int status = ConstantsUtil.EDownloadStatusType.eDownloadTobeStarted.intStatus();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHandler.COMBO_LOCAL_IS_DISPLAY_READY, 0);
        cv.put(DatabaseHandler.LEG_ID, "");
        cv.put(DatabaseHandler.A1_OBJ_KEY_NAME, "");
        cv.put(DatabaseHandler.A7_OBJ_KEY_NAME, "");
        cv.put(DatabaseHandler.A7_PNG_KEY_NAME, "");
        cv.put(DatabaseHandler.FACE_ID, faceId);

        cv.put(DatabaseHandler.A1_OBJ_DOWNLOAD_STATUS, status);
        cv.put(DatabaseHandler.A1_TEXTURE_DOWNLOAD_STATUS, status);
        cv.put(DatabaseHandler.A6_OBJ_DOWNLOAD_STATUS, status);
        cv.put(DatabaseHandler.A6_TEXTURE_DOWNLOAD_STATUS, status);
        cv.put(DatabaseHandler.A7_OBJ_DOWNLOAD_STATUS, status);
        cv.put(DatabaseHandler.A7_TEXTURE_DOWNLOAD_STATUS, status);
        cv.put(DatabaseHandler.A8_OBJ_DOWNLOAD_STATUS, status);
        cv.put(DatabaseHandler.A8_TEXTURE_DOWNLOAD_STATUS, status);
        cv.put(DatabaseHandler.A9_OBJ_DOWNLOAD_STATUS, status);
        cv.put(DatabaseHandler.A9_TEXTURE_DOWNLOAD_STATUS, status);
        cv.put(DatabaseHandler.A10_OBJ_DOWNLOAD_STATUS, status);
        cv.put(DatabaseHandler.A10_TEXTURE_DOWNLOAD_STATUS, status);
        //long insertId = database.update(DatabaseHandler.TABLE_COMBODATA, cv, DatabaseHandler.FACE_ID + " =? ", new String[]{faceId});
        long insertId = database.update(DatabaseHandler.TABLE_COMBODATA, cv, null, null);
        return insertId;
    }

    public long delete(ComboData combo) {
        String[] whereColumn = new String[]{DatabaseHandler.COMBO_ID, DatabaseHandler.PB_ID};
        String[] whereValue = new String[]{combo.getCombo_ID(), combo.getPbId()};
        long insertId = 0;
        insertId = delete(DatabaseHandler.TABLE_COMBODATA, whereColumn, whereValue);
        return insertId;
    }

    public long delete(LookBoardItem item) {
        String[] whereColumn = new String[]{DatabaseHandler.COMPAIGN_ID};
        String[] whereValue = new String[]{item.getCampaign_ID()};
        long insertId = 0;
        insertId = delete(DatabaseHandler.TABLE_COMBO_LOOKBOARD, whereColumn, whereValue);
        return insertId;
    }

//    public long deleteComboDetailForPB1(String pbId) {
//        String[] whereColumn = new String[]{DatabaseHandler.PB_ID};
//        String[] whereValue = new String[]{pbId};
//        long insertId = 0;
//        insertId = delete(DatabaseHandler.TABLE_COMBODATA, whereColumn, whereValue);
//        return insertId;
//    }


//    public void deleteComboDetailColumnData(String faceId) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                ArrayList<ComboData> list = (ArrayList<ComboData>) getComboDataDetails();
//                for (ComboData c : list) {
//                    c.setmA1_Obj_Key_Name("");
//                    c.setmA1_Png_Key_Name("");
//                    c.setmA2_Obj_Key_Name("");
//                    create(c);
//                }
//            }
//        });
//    }

    /***********
     * UPDATE COMBO
     ************/

    public long updateComboDisplayReady(String comboID) {
        ContentValues values1 = new ContentValues();
        values1.put(DatabaseHandler.COMBO_ID, comboID);
        values1.put(DatabaseHandler.COMBO_LOCAL_IS_DISPLAY_READY, 1);
        long insertId = insertOrUpdate(DatabaseHandler.TABLE_COMBODATA, values1, DatabaseHandler.COMBO_ID);
        return insertId;
    }

    public long updateComboCartCount(String comboID, int cartCount) {
        ContentValues values1 = new ContentValues();
        values1.put(DatabaseHandler.COMBO_ID, comboID);
        values1.put(DatabaseHandler.COMBO_LOCAL_CART_COUNT, cartCount);
        long insertId = insertOrUpdate(DatabaseHandler.TABLE_COMBODATA, values1, DatabaseHandler.COMBO_ID);
        return insertId;
    }

    public long updateComboLikeCount(String comboId, int likeCount) {
        ContentValues values1 = new ContentValues();
        values1.put(DatabaseHandler.COMBO_ID, comboId);
        values1.put(DatabaseHandler.LIKES_COUNT, likeCount);
        long insertId = insertOrUpdate(DatabaseHandler.TABLE_COMBODATA, values1, DatabaseHandler.COMBO_ID);
        return insertId;
    }

    public void updateComboLikeAndIsLiked(String comboId, int likeCount, int isLiked) {
        ContentValues values1 = new ContentValues();
        values1.put(DatabaseHandler.COMBO_ID, comboId);
        values1.put(DatabaseHandler.LIKES_COUNT, likeCount);
        values1.put(DatabaseHandler.COMBO_LOCAL_IS_LIKED, isLiked);
        long insertId = insertOrUpdate(DatabaseHandler.TABLE_COMBODATA, values1, DatabaseHandler.COMBO_ID);
    }

    public void updateComboSeenDate(ComboData comboData) {
        ContentValues values1 = new ContentValues();
        values1.put(DatabaseHandler.COMBO_ID, comboData.getCombo_ID());
        //comboData.setViewCount(comboData.getViewCount() + 1);
        Log.e(LOGTAG, "updateComboSeenDate " + System.currentTimeMillis() + "   View count: " + comboData.getViewCount());
        values1.put(DatabaseHandler.COMBO_LOCAL_VIEW_COUNT, comboData.getViewCount());
        populateContentValue(values1, DatabaseHandler.DATE_SEEN_IN_MILLI, System.currentTimeMillis());
        long insertId = insertOrUpdate(DatabaseHandler.TABLE_COMBODATA, values1, DatabaseHandler.COMBO_ID);
    }

    public void updateComboItemDownloadStatus(String comboId,ContentValues values) {
        values.put(DatabaseHandler.COMBO_ID, comboId);
        long insertId = insertOrUpdate(DatabaseHandler.TABLE_COMBODATA, values, DatabaseHandler.COMBO_ID);
    }

    public void updateComboTimeStamp(ComboData combodata) {
        ContentValues values1 = new ContentValues();
        values1.put(DatabaseHandler.COMBO_ID, combodata.getCombo_ID());
        //values1.put(DatabaseHandler.UPDATED_AT, getCurrentDate());
        //values1.put(DatabaseHandler.UPDATED_AT, "datetime('now','localtime')");
        //values1.put(DatabaseHandler.UPDATED_AT, System.currentTimeMillis());
        Log.d(LOGTAG, "updatedAt: " + System.currentTimeMillis());
        //values1.put(DatabaseHandler.SEEN_DATE, getMilliSecond());
        combodata.setComboUpdatedDateFormated(new Date());
        values1.put(DatabaseHandler.UPDATED_DATE, combodata.getComboUpdatedDate());
    }



    /**********************************************************************************************************************
     * *******************************************************************************************************************
     * LOOKBOARD
     * ********************************************************************************************************************
     ********************************************************************************************************************/

    public long getLooBoardCount() {
        long cnt  = DatabaseUtils.queryNumEntries(database, DatabaseHandler.TABLE_COMBO_LOOKBOARD);
        return cnt;
    }

    public List<LookBoardItem>getComboLookBoard(boolean shouldRearrangeData){
        String orderBy = null;
//        if(shouldRearrangeData) {
//            orderBy = DatabaseHandler.DATE_RECONCILE_IN_MILLI + " ASC, "
//                    + DatabaseHandler.COMBO_LOCAL_VIEW_COUNT + " DESC, " + DatabaseHandler.FORCED_RANK + " ASC " ;
//        }else{
//            orderBy = DatabaseHandler.DATE_RECONCILE_IN_MILLI + " DESC, " + DatabaseHandler.FORCED_RANK + " ASC, "
//                    + DatabaseHandler.COMBO_LOCAL_VIEW_COUNT + " DESC ";
//        }

        orderBy = DatabaseHandler.DATE_RECONCILE_IN_MILLI + " ASC, "
                + DatabaseHandler.COMBO_LOCAL_VIEW_COUNT + " DESC ";

        String whereClause = DatabaseHandler.COMBO_LOCAL_VIEW_COUNT + " != ?";
        String[] whereValue = new String[]{String.valueOf(0)};
        //todo improve query

//        Cursor cursor = database.query(DatabaseHandler.TABLE_COMBO_LOOKBOARD, comboLookboardColumns,
//                DatabaseHandler.PUSH_FLAG+"=?", new String[]{"True"}, null, null, orderBy, null);

        //Cursor cursor2 = database.query(DatabaseHandler.TABLE_COMBO_LOOKBOARD, comboLookboardColumns,null, null, null, null,orderBy, null );
        //List<LookBoardItem> comboLookBoardList2 = comboLookBoardCursorToList(cursor2);
        Cursor cursor = database.query(DatabaseHandler.TABLE_COMBO_LOOKBOARD, comboLookboardColumns,
                whereClause, whereValue, null, null, orderBy,null );
        Log.i(LOGTAG, "getComboLookBoard : " + cursor.getCount() + " rows");
        List<LookBoardItem> comboLookBoardList = comboLookBoardCursorToList(cursor);
        cursor.close();
        return comboLookBoardList;
    }

    public List<LookBoardItem> getComboLookBoardByCategory(String category,boolean shouldRearrangeData) {
        String orderBy = null;//
        if(shouldRearrangeData) {
            orderBy = DatabaseHandler.DATE_RECONCILE_IN_MILLI + " DESC, "
                    + DatabaseHandler.COMBO_LOCAL_VIEW_COUNT + " ASC ";
        }else {
            orderBy = DatabaseHandler.DATE_RECONCILE_IN_MILLI + " DESC, " ;
        }
        //Cursor cursor = database.query(DatabaseHandler.TABLE_COMBO_LOOKBOARD, comboLookboardColumns,DatabaseHandler.COMBO_STYLE_CATEGORY + "=?", new String[]{category}, null, null, orderBy, null);

        Cursor cursor = database.query(DatabaseHandler.TABLE_COMBO_LOOKBOARD, comboLookboardColumns,
                DatabaseHandler.COMBO_STYLE_CATEGORY + "=? AND "+DatabaseHandler.STATUS+" =?", new String[]{category,"Active"}, null, null, orderBy, null);

//         cursor = database.query(DatabaseHandler.TABLE_COMBO_LOOKBOARD, comboLookboardColumns,
//                null, null, null, null, orderBy, null);
//
//        cursor = database.query(DatabaseHandler.TABLE_COMBO_LOOKBOARD, comboLookboardColumns,
//                null, null, null, null, orderBy, null);

        Log.i(LOGTAG, "getComboLookBoardByCategory : " + cursor.getCount() + " rows");
        List<LookBoardItem> comboLookBoardList = comboLookBoardCursorToList(cursor);
        cursor.close();
        return comboLookBoardList;
    }

    public LookBoardItem getComboLookBoardByComboID(String comboId) {
        Cursor cursor = database.query(DatabaseHandler.TABLE_COMBO_LOOKBOARD, comboLookboardColumns,
                DatabaseHandler.COMBO_ID + "=?", new String[]{comboId}, null, null, null, null);
        Log.i(LOGTAG, "getComboLikeDataByComboID : " + cursor.getCount() + " rows");
        List<LookBoardItem> comboLookBoardList = comboLookBoardCursorToList(cursor);
        cursor.close();
        if (comboLookBoardList != null && comboLookBoardList.size() != 0)
            return comboLookBoardList.get(0);
        else
            return null;
    }


    public long insertOrUpdateTemp(String TableName, ContentValues values, String IDColumn) {
        String valueId = String.valueOf(values.get(IDColumn));
        Cursor cursor = database.query(TableName, new String[]{IDColumn}, IDColumn + "=?",
                new String[]{valueId}, null, null, null);

        //Log.i(LOGTAG, " TableName : " + TableName + "  Returned  Data: " + cursor.getCount() + " rows");
        long insertId = 0;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            String comId = cursor.getString(cursor.getColumnIndex(IDColumn));
            database.update(TableName, values, IDColumn + "=? ", new String[]{valueId});
        } else {
            if(values.containsKey(DatabaseHandler.STATUS) && (values.get(DatabaseHandler.STATUS).equals("Deleted") || values.get(DatabaseHandler.STATUS).equals("InActive"))){
                Log.e(LOGTAG,TableName + " :c- "+values.get(IDColumn)+" deleted");
            }else {
                insertId = database.insert(TableName, null, values);
            }
        }
        cursor.close();
        return insertId;
    }

    public LookBoardItem createReconcileLookBoard(LookBoardItem combodata,long reconcileDateInMilli) {
        ContentValues values1 = new ContentValues();
        values1.put(DatabaseHandler.COMBO_ID, combodata.getCombo_ID());
        values1.put(DatabaseHandler.COMPAIGN_ID, combodata.getCampaign_ID());
        values1.put(DatabaseHandler.CURATOR_ID, combodata.getCurator_ID());
        values1.put(DatabaseHandler.GENDER, combodata.getCombo_Gender());
        populateContentValue(values1, DatabaseHandler.COMBO_STYLE_CATEGORY, combodata.getCategory());
        values1.put(DatabaseHandler.COMBO_PIC_PNG_KEY_NAME, combodata.getImage_Key_Name());
        values1.put(DatabaseHandler.STATUS, combodata.getStatus());
        values1.put(DatabaseHandler.FORCED_RANK, combodata.getRank());
        populateContentValue(values1, DatabaseHandler.PUSH_FLAG, combodata.getPush_Flag());
        populateContentValue(values1, DatabaseHandler.DATE_RECONCILE_IN_MILLI, reconcileDateInMilli);
        populateContentValue(values1, DatabaseHandler.COMBO_LOCAL_VIEW_COUNT, combodata.getViewCount());
        populateContentValue(values1, DatabaseHandler.COMBO_COLOR, combodata.getCombo_Color());
        populateContentValue(values1, DatabaseHandler.DESCRIPTION, combodata.getDescription());

        Log.d(LOGTAG, "comboDataReconcile ID: " + combodata.getCombo_ID() + "   ");
        //long insertId = insertOrUpdate(DatabaseHandler.TABLE_COMBO_LOOKBOARD, values1, DatabaseHandler.COMBO_ID);
        long insertId = insertOrUpdateTemp(DatabaseHandler.TABLE_COMBO_LOOKBOARD, values1, DatabaseHandler.COMPAIGN_ID);
        if (insertId != 0)
            combodata.setRowId(insertId);
        return combodata;
    }


    private List<LookBoardItem> comboLookBoardCursorToList(Cursor cursor) {
        List<LookBoardItem> lookBoardItemList = new ArrayList<LookBoardItem>();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                LookBoardItem lookBoardItem = new LookBoardItem();
                lookBoardItem.setCombo_ID(cursor.getString(cursor.getColumnIndex(DatabaseHandler.COMBO_ID)));

                lookBoardItem.setGender(cursor.getString(cursor.getColumnIndex(DatabaseHandler.GENDER)));
                lookBoardItem.setCategory(cursor.getString(cursor.getColumnIndex(DatabaseHandler.COMBO_STYLE_CATEGORY)));
                lookBoardItem.setImage_Key_Name(cursor.getString(cursor.getColumnIndex(DatabaseHandler.COMBO_PIC_PNG_KEY_NAME)));

                lookBoardItem.setCampaign_ID(cursor.getString(cursor.getColumnIndex(DatabaseHandler.COMPAIGN_ID)));
                lookBoardItem.setCurator_ID(cursor.getString(cursor.getColumnIndex(DatabaseHandler.CURATOR_ID)));

                lookBoardItem.setStatus(cursor.getString(cursor.getColumnIndex(DatabaseHandler.STATUS)));
                lookBoardItem.setRank(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.FORCED_RANK)));
                //lookBoardItem.setPush_Flag(cursor.getString(cursor.getColumnIndex(DatabaseHandler.PUSH_FLAG)));
                lookBoardItem.setDateReconcile((cursor.getLong(cursor.getColumnIndex(DatabaseHandler.DATE_RECONCILE_IN_MILLI))));
                lookBoardItem.setDateSeenInMilli(cursor.getLong(cursor.getColumnIndex(DatabaseHandler.DATE_SEEN_IN_MILLI))/1000);
                lookBoardItem.setViewCount(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.COMBO_LOCAL_VIEW_COUNT)));

                lookBoardItemList.add(lookBoardItem);
            }
        }
        cursor.close();
        return lookBoardItemList;
    }



    /***********
     * UPDATE LOOKBOARD
     ************/




    /***********
     * DELETE LOOKBOARD
     ************/


    /**********************************************************************************************************************
     * *******************************************************************************************************************
     * ACCESSORY
     * ********************************************************************************************************************
     ********************************************************************************************************************/


    public BaseAccessoryItem create(BaseAccessoryItem item,long reconcileDateInMilli) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHandler.OBJ_ID, item.getObjId());
        contentValues.put(DatabaseHandler.PIC_PNG_KEY_NAME, item.getThumbnailAwsKey());
        populateContentValue(contentValues, DatabaseHandler.OBJ_ID2, item.getObjId2());
        populateContentValue(contentValues, DatabaseHandler.OBJ_KEY_NAME, item.getObjAwsKey());
        populateContentValue(contentValues, DatabaseHandler.TEXTURE_KEY_NAME, item.getTextureAwsKey());
        populateContentValue(contentValues, DatabaseHandler.ACCESSORY_TYPE, item.getAccessoryType());
        populateContentValue(contentValues, DatabaseHandler.ACCESSORY_TYPE2, item.getAccessoryType2());
        populateContentValue(contentValues,DatabaseHandler.DATE_RECONCILE_IN_MILLI,reconcileDateInMilli);

        contentValues.put(DatabaseHandler.OBJ_DOWNLOAD_STATUS, item.getObjDStatus());
        contentValues.put(DatabaseHandler.TEXTURE_DOWNLOAD_STATUS, item.getTextureDStatus());
        contentValues.put(DatabaseHandler.FORCED_RANK, item.getRank());


        long insertId = -10;
        Log.d(LOGTAG, "BaseAccessoryItem ID: " + item.getObjId() + "   Type: " + item.getAccessoryType());
        String accessoryType = item.getAccessoryType();
        String[] whereColumn = null;

        if (ConstantsUtil.isAccessoryIndependent(accessoryType)) {
            whereColumn = new String[]{DatabaseHandler.OBJ_ID};
        } else if (ConstantsUtil.isAccessoryFaceRelated(accessoryType)) {
            populateContentValue(contentValues, DatabaseHandler.FACE_ID, item.getFaceID());
            whereColumn = new String[]{DatabaseHandler.OBJ_ID, DatabaseHandler.FACE_ID};
        } else if (ConstantsUtil.isAccessoryBodyRelated(accessoryType)) {
            populateContentValue(contentValues, DatabaseHandler.PB_ID, item.getPbID());
            whereColumn = new String[]{DatabaseHandler.OBJ_ID, DatabaseHandler.PB_ID};
        } else if (ConstantsUtil.isAccessoryBodyFaceRelated(accessoryType)) {
            populateContentValue(contentValues, DatabaseHandler.PB_ID, item.getPbID());
            populateContentValue(contentValues, DatabaseHandler.FACE_ID, item.getFaceID());
            whereColumn = new String[]{DatabaseHandler.OBJ_ID, DatabaseHandler.PB_ID, DatabaseHandler.FACE_ID};
        }
        insertId = insertOrUpdate(DatabaseHandler.TABLE_ACCESSORY, contentValues, whereColumn);
        if (insertId != 0)
            item.setId2(insertId);
        return item;
    }


    public BaseAccessoryItem create(BaseAccessoryItem item) {
        return  create(item,0);
    }

    private List<BaseAccessoryItem> accessoryCursorToList(Cursor cursor) {
        List<BaseAccessoryItem> list = new ArrayList<BaseAccessoryItem>();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                BaseAccessoryItem item = new BaseAccessoryItem();
                item.setAccessoryType(cursor.getString(cursor.getColumnIndex(DatabaseHandler.ACCESSORY_TYPE)));
                item.setAccessoryType2(cursor.getString(cursor.getColumnIndex(DatabaseHandler.ACCESSORY_TYPE2)));
                item.setObjId(cursor.getString(cursor.getColumnIndex(DatabaseHandler.OBJ_ID)));
                item.setObjId2(cursor.getString(cursor.getColumnIndex(DatabaseHandler.OBJ_ID2)));
                item.setTextureAwsKey(cursor.getString(cursor.getColumnIndex(DatabaseHandler.TEXTURE_KEY_NAME)));
                item.setObjAwsKey(cursor.getString(cursor.getColumnIndex(DatabaseHandler.OBJ_KEY_NAME)));
                item.setThumbnailAwsKey(cursor.getString(cursor.getColumnIndex(DatabaseHandler.PIC_PNG_KEY_NAME)));
                item.setTextureDStatus(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.TEXTURE_DOWNLOAD_STATUS)));
                item.setObjDStatus(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.OBJ_DOWNLOAD_STATUS)));
                item.setRank(cursor.getInt(cursor.getColumnIndex(DatabaseHandler.FORCED_RANK)));
                item.setPbID(cursor.getString(cursor.getColumnIndex(DatabaseHandler.PB_ID)));
                item.setFaceID(cursor.getString(cursor.getColumnIndex(DatabaseHandler.FACE_ID)));
                item.setDateReconcile(cursor.getLong(cursor.getColumnIndex(DatabaseHandler.DATE_RECONCILE_IN_MILLI)));
                list.add(item);
            }
        }
        return list;
    }

    /***********
     * QUERY ACCESSORY
     *************/

    public boolean isObjDownloaded(String key){
        String whereClause = DatabaseHandler.OBJ_KEY_NAME + " = ? ";
        String[] whereValue = new String[]{key};
        Cursor cursor = database.query(DatabaseHandler.TABLE_ACCESSORY, accessoryColumns,
                whereClause, whereValue, null, null, null, null);
        List<BaseAccessoryItem> list = accessoryCursorToList(cursor);
        if(list != null && list.size() !=0)
        {
            BaseAccessoryItem item = list.get(0);
            if(item.getObjDStatus() == ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus()){
                return true;
            }
        }
        return false;
    }

    public boolean isTexDownloaded(String key){
        String whereClause = DatabaseHandler.TEXTURE_KEY_NAME + " = ? ";
        String[] whereValue = new String[]{key};
        Cursor cursor = database.query(DatabaseHandler.TABLE_ACCESSORY, accessoryColumns,
                whereClause, whereValue, null, null, null, null);
        List<BaseAccessoryItem> list = accessoryCursorToList(cursor);
        if(list != null && list.size() !=0)
        {
            BaseAccessoryItem item = list.get(0);
            if(item.getTextureDStatus() == ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus()){
                return true;
            }
        }
        return false;
    }

    public List<BaseAccessoryItem> getDownloadedAccessories(String accessoryType,int limitCount) {
        String orderBy = DatabaseHandler.DATE_RECONCILE_IN_MILLI + " ASC ";
        String whereClause = DatabaseHandler.ACCESSORY_TYPE + " = ? " +" AND "+DatabaseHandler.TEXTURE_DOWNLOAD_STATUS+" =? ";
        String[] whereValue = new String[]{accessoryType,String.valueOf(ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus())};
        String limit = String.valueOf(limitCount);

        Cursor cursor = database.query(DatabaseHandler.TABLE_ACCESSORY, accessoryColumns,
                whereClause, whereValue, null, null, orderBy, limit);
        List<BaseAccessoryItem> list = accessoryCursorToList(cursor);
        return list;
    }

    private List<BaseAccessoryItem> getAccessoriesList(String accessoryType) {
        String orderBy = null;
        if(accessoryType.equals(ConstantsUtil.EAccessoryType.eAccTypeHair.toString()) || accessoryType.equals(ConstantsUtil.EAccessoryType.eAccTypeSpecs.toString())){
            orderBy = DatabaseHandler.FORCED_RANK + " ASC ";
        }else {
            orderBy = DatabaseHandler.DATE_RECONCILE_IN_MILLI + " DESC ";
        }
        String whereClause = DatabaseHandler.ACCESSORY_TYPE + " = ?";
        String[] whereValue = new String[]{accessoryType};
        Cursor cursor = database.query(DatabaseHandler.TABLE_ACCESSORY, accessoryColumns,
                whereClause, whereValue, null, null, orderBy, null);
        List<BaseAccessoryItem> list = accessoryCursorToList(cursor);
        cursor.close();
        return list;
    }



    public List<BaseAccessoryItem> getAccessories(String accessoryType) {
        /*String orderBy = null;
        if(accessoryType.equals(ConstantsUtil.EAccessoryType.eAccTypeHair.toString()) || accessoryType.equals(ConstantsUtil.EAccessoryType.eAccTypeSpecs.toString())){
            orderBy = DatabaseHandler.FORCED_RANK + " ASC ";
        }else {
            orderBy = DatabaseHandler.DATE_RECONCILE_IN_MILLI + " DESC ";
        }
        String whereClause = null;
        String[] whereValue = null;
        if (ConstantsUtil.isAccessoryIndependent(accessoryType)) {
            whereClause = DatabaseHandler.ACCESSORY_TYPE + " = ?";
            whereValue = new String[]{accessoryType};
        } else if (ConstantsUtil.isAccessoryFaceRelated(accessoryType)) {
            whereClause = DatabaseHandler.ACCESSORY_TYPE + " = ? AND "
                    + DatabaseHandler.FACE_ID + " = ? ";
            String faceId = User.getInstance().getDefaultFaceId();
            if (faceId == null) {
                return null;
            }
            whereValue = new String[]{accessoryType, faceId};
        } else if (ConstantsUtil.isAccessoryBodyRelated(accessoryType)) {
            whereClause = DatabaseHandler.ACCESSORY_TYPE + " = ? AND "
                    + DatabaseHandler.PB_ID + " = ? ";
            String pbId = User.getInstance().getPBId();
            if (pbId == null) {
                return null;
            }
            whereValue = new String[]{accessoryType, pbId};
        } else if (ConstantsUtil.isAccessoryBodyFaceRelated(accessoryType)) {
            whereClause = DatabaseHandler.ACCESSORY_TYPE + " = ? AND "
                    + DatabaseHandler.FACE_ID + " = ? AND "
                    + DatabaseHandler.PB_ID + " = ? ";
            String pbId = User.getInstance().getPBId();
            String faceId = User.getInstance().getDefaultFaceId();
            if (pbId == null || faceId == null) {
                return null;
            }
            whereValue = new String[]{accessoryType, faceId, pbId};
        }
        Cursor cursor = database.query(DatabaseHandler.TABLE_ACCESSORY, accessoryColumns,
                whereClause, whereValue, null, null, orderBy, null);
        List<BaseAccessoryItem> list = accessoryCursorToList(cursor);
        cursor.close(); */
        List<BaseAccessoryItem> list = getAccessoriesDirect(accessoryType);
        if(list== null ||list.size() == 0){
            list = getDefaultListForAccessory(accessoryType);
        }
        return list;
    }

    public List<BaseAccessoryItem> getAccessoriesDirect(String accessoryType) {
        String orderBy = null;
        if(accessoryType.equals(ConstantsUtil.EAccessoryType.eAccTypeHair.toString()) || accessoryType.equals(ConstantsUtil.EAccessoryType.eAccTypeSpecs.toString())){
            orderBy = DatabaseHandler.FORCED_RANK + " ASC ";
        }else {
            orderBy = DatabaseHandler.DATE_RECONCILE_IN_MILLI + " DESC ";
        }
        String whereClause = null;
        String[] whereValue = null;
        if (ConstantsUtil.isAccessoryIndependent(accessoryType)) {
            whereClause = DatabaseHandler.ACCESSORY_TYPE + " = ?";
            whereValue = new String[]{accessoryType};
        } else if (ConstantsUtil.isAccessoryFaceRelated(accessoryType)) {
            whereClause = DatabaseHandler.ACCESSORY_TYPE + " = ? AND "
                    + DatabaseHandler.FACE_ID + " = ? ";
            String faceId = User.getInstance().getDefaultFaceId();
            if (faceId == null) {
                return null;
            }
            whereValue = new String[]{accessoryType, faceId};
        } else if (ConstantsUtil.isAccessoryBodyRelated(accessoryType)) {
            whereClause = DatabaseHandler.ACCESSORY_TYPE + " = ? AND "
                    + DatabaseHandler.PB_ID + " = ? ";
            String pbId = User.getInstance().getPBId();
            if (pbId == null) {
                return null;
            }
            whereValue = new String[]{accessoryType, pbId};
        } else if (ConstantsUtil.isAccessoryBodyFaceRelated(accessoryType)) {
            whereClause = DatabaseHandler.ACCESSORY_TYPE + " = ? AND "
                    + DatabaseHandler.FACE_ID + " = ? AND "
                    + DatabaseHandler.PB_ID + " = ? ";
            String pbId = User.getInstance().getPBId();
            String faceId = User.getInstance().getDefaultFaceId();
            if (pbId == null || faceId == null) {
                return null;
            }
            whereValue = new String[]{accessoryType, faceId, pbId};
        }
        Cursor cursor = database.query(DatabaseHandler.TABLE_ACCESSORY, accessoryColumns,
                whereClause, whereValue, null, null, orderBy, null);
        List<BaseAccessoryItem> list = accessoryCursorToList(cursor);
        cursor.close();
        return list;
    }

    public List<BaseAccessoryItem> getDefaultListForAccessory(String accessoryType){
        List<BaseAccessoryItem> accessoriesList = getAccessoriesList(accessoryType);
        if(accessoriesList  == null){
            return null;
        }
        long reconcileDateInMilli = System.currentTimeMillis();
        for(BaseAccessoryItem item : accessoriesList){
            item.setFaceID(User.getInstance().getDefaultFaceId());
            item.setPbID(User.getInstance().getPBId());
            if(!ConstantsUtil.isAccessoryIndependent(item.getAccessoryType()) ){//|| !item.getAccessoryType().equals(ConstantsUtil.EAccessoryType.eAccTypeShoes.toString()
                item.setObjDStatus(ConstantsUtil.EDownloadStatusType.eDownloadTobeStarted.intStatus());
                item.setTextureDStatus(ConstantsUtil.EDownloadStatusType.eDownloadTobeStarted.intStatus());
                item.setObjAwsKey("");
                item.setTextureAwsKey("");
            }
            create(item, reconcileDateInMilli);
        }
       List<BaseAccessoryItem> accessoriesListFinal = getAccessoriesDirect(accessoryType);
        return accessoriesListFinal;
    }

    public BaseAccessoryItem getLegItem( String accessoryId, String faceId) {
        Cursor cursor = null;
        String[] whereValue = null;
        String whereClause = null;
        String accessoryType = ConstantsUtil.EAccessoryType.eAccTypeLegs.toString();

        whereClause = DatabaseHandler.ACCESSORY_TYPE + " = ? AND "
                + DatabaseHandler.OBJ_ID + " = ? AND "
                + DatabaseHandler.PB_ID + " = ? AND "
                + DatabaseHandler.FACE_ID + " = ? ";
        String pbId = User.getInstance().getPBId();
        whereValue = new String[]{accessoryType, accessoryId, pbId, faceId};
        Log.d(LOGTAG, "getAccessory " + whereClause + "  : " + accessoryType + "  " + accessoryId + "  " + pbId + "  "+faceId);
        cursor = database.query(DatabaseHandler.TABLE_ACCESSORY, accessoryColumns,
                whereClause, whereValue, null, null, null, null);
        BaseAccessoryItem item = null;
        if (cursor == null) {
            Log.d(LOGTAG, "No item found  " + accessoryType);
            return item;
        }
        List<BaseAccessoryItem> list = accessoryCursorToList(cursor);
        cursor.close();
        if (list != null && list.size() != 0)
            item = list.get(0);
        return item;
    }

    public BaseAccessoryItem getAccessory(String accessoryType, String accessoryId) {
        String orderBy = DatabaseHandler.FORCED_RANK + " ASC ";
        Cursor cursor = null;
        String[] whereValue = null;
        String whereClause = null;
        if (ConstantsUtil.isAccessoryIndependent(accessoryType)) {
            whereClause = DatabaseHandler.ACCESSORY_TYPE + " = ? AND "
                    + DatabaseHandler.OBJ_ID + " = ? ";
            whereValue = new String[]{accessoryType, accessoryId};
        } else if (ConstantsUtil.isAccessoryFaceRelated(accessoryType)) {
            whereClause = DatabaseHandler.ACCESSORY_TYPE + " = ? AND "
                    + DatabaseHandler.OBJ_ID + " = ? AND "
                    + DatabaseHandler.FACE_ID + " = ? ";
            String faceId = User.getInstance().getDefaultFaceId();
            whereValue = new String[]{accessoryType, accessoryId, faceId};
        } else if (ConstantsUtil.isAccessoryBodyRelated(accessoryType)) {
            whereClause = DatabaseHandler.ACCESSORY_TYPE + " = ? AND "
                    + DatabaseHandler.OBJ_ID + " = ? AND "
                    + DatabaseHandler.PB_ID + " = ? ";
            String pbId = User.getInstance().getPBId();
            whereValue = new String[]{accessoryType, accessoryId, pbId};
            Log.d(LOGTAG, "getAccessory " + whereClause + "  : " + accessoryType + "  " + accessoryId + "  " + pbId);
        } else if (ConstantsUtil.isAccessoryBodyFaceRelated(accessoryType)) {
            whereClause = DatabaseHandler.ACCESSORY_TYPE + " = ? AND "
                    + DatabaseHandler.OBJ_ID + " = ? AND "
                    + DatabaseHandler.PB_ID + " = ? AND "
                    + DatabaseHandler.FACE_ID + " = ? ";
            String pbId = User.getInstance().getPBId();

            String faceId = User.getInstance().getDefaultFaceId();
            whereValue = new String[]{accessoryType, accessoryId, pbId, faceId};
            Log.d(LOGTAG, "getAccessory " + whereClause + "  : " + accessoryType + "  " + accessoryId + "  " + pbId + "  "+faceId);
        }
        cursor = database.query(DatabaseHandler.TABLE_ACCESSORY, accessoryColumns,
                whereClause, whereValue, null, null, orderBy, null);
        BaseAccessoryItem item = null;
        if (cursor == null) {
            Log.d(LOGTAG, "No item found  " + accessoryType);
            return item;
        }
        List<BaseAccessoryItem> list = accessoryCursorToList(cursor);
        cursor.close();
        if (list != null && list.size() != 0)
            item = list.get(0);
        return item;
    }

//    public List<BaseAccessoryItem> getAccessoriesFaceBody(String accessoryType, String faceId, String pbId) {
//        String orderBy = DatabaseHandler.FORCED_RANK + " ASC ";
//        Cursor cursor = null;
//        if (ConstantsUtil.isAccessoryBodyFaceRelated(accessoryType)) {
//            String whereClause = DatabaseHandler.ACCESSORY_TYPE + " = ? AND "
//                    + DatabaseHandler.FACE_ID + " = ? AND "
//                    + DatabaseHandler.PB_ID + " = ? ";
//            String[] whereValue = new String[]{accessoryType, faceId, pbId};
//            cursor = database.query(DatabaseHandler.TABLE_ACCESSORY, accessoryColumns,
//                    whereClause, whereValue, null, null, orderBy, null);
//        }
//        List<BaseAccessoryItem> list = accessoryCursorToList(cursor);
//        cursor.close();
//        return list;
//    }
//
//
//    public List<BaseAccessoryItem> getAccessoriesFace(String accessoryType, String faceId) {
//        String orderBy = DatabaseHandler.FORCED_RANK + " ASC ";
//        Cursor cursor = null;
//
//        if (ConstantsUtil.isAccessoryFaceRelated(accessoryType)) {
//            String whereClause = DatabaseHandler.ACCESSORY_TYPE + " = ? AND "
//                    + DatabaseHandler.FACE_ID + " = ? ";
//            String[] whereValue = new String[]{accessoryType, faceId};
//            cursor = database.query(DatabaseHandler.TABLE_ACCESSORY, accessoryColumns,
//                    whereClause, whereValue, null, null, orderBy, null);
//        }
//        List<BaseAccessoryItem> list = accessoryCursorToList(cursor);
//        cursor.close();
//        return list;
//    }

   /* public List<BaseAccessoryItem> getAccessoriesBody(String accessoryType, String pbId) {
        String orderBy = DatabaseHandler.FORCED_RANK + " ASC ";
        Cursor cursor = null;
        if (ConstantsUtil.isAccessoryBodyRelated(accessoryType)) {
            String whereClause = DatabaseHandler.ACCESSORY_TYPE + " = ? AND "
                    + DatabaseHandler.PB_ID + " = ? ";
            String[] whereValue = new String[]{accessoryType, pbId};
            cursor = database.query(DatabaseHandler.TABLE_ACCESSORY, accessoryColumns,
                    whereClause, whereValue, null, null, orderBy, null);

        }
        List<BaseAccessoryItem> list = accessoryCursorToList(cursor);
        cursor.close();
        return list;
    }

    public List<BaseAccessoryItem> getAccessoriesBody(String pbId) {
        Cursor cursor = null;
        String whereClause = DatabaseHandler.ACCESSORY_TYPE + " IN (?) AND "
                + DatabaseHandler.PB_ID + " = ? ";

        String[] acc = new String[]{ConstantsUtil.EAccessoryType.eAccTypeShoes.toString()};
        String str = "";
        for (int i = 0; i < acc.length; i++) {
            str += "'" + acc[i] + "'";
            if (i != acc.length - 1) {
                str += ",";
            }
        }
        String[] whereValue = new String[]{str, pbId};
        cursor = database.query(DatabaseHandler.TABLE_ACCESSORY, accessoryColumns,
                whereClause, whereValue, null, null, null, null);

        List<BaseAccessoryItem> list = accessoryCursorToList(cursor);
        cursor.close();
        return list;
    }
*/
//    public List<BaseAccessoryItem> getAccessoriesFace(String faceId) {
//        Cursor cursor = null;
//        String whereClause = DatabaseHandler.ACCESSORY_TYPE + " IN (?) AND "
//                + DatabaseHandler.FACE_ID + " = ? ";
//
//        String[] acc = new String[]{ConstantsUtil.EAccessoryType.eAccTypeSpecs.toString(), ConstantsUtil.EAccessoryType.eAccTypeEarrings.toString(), ConstantsUtil.EAccessoryType.eAccTypeSunglasses.toString()};
//        String str = "";
//        for (int i = 0; i < acc.length; i++) {
//            str += "'" + acc[i] + "'";
//            if (i != acc.length - 1) {
//                str += ",";
//            }
//        }
//        String[] whereValue = new String[]{str, faceId};
//        cursor = database.query(DatabaseHandler.TABLE_ACCESSORY, accessoryColumns,
//                whereClause, whereValue, null, null, null, null);
//
//        List<BaseAccessoryItem> list = accessoryCursorToList(cursor);
//        cursor.close();
//        return list;
//    }


    /*******************************
     * DELETE ACCESSORY
     *******************************/

    public long delete(BaseAccessoryItem item) {
        String[] whereColumns = new String[]{DatabaseHandler.ACCESSORY_TYPE,DatabaseHandler.OBJ_ID};
        String[] whereValues = new String[]{ item.getAccessoryType(),item.getObjId()};
        long insertId = delete(DatabaseHandler.TABLE_ACCESSORY, whereColumns, whereValues);
        return insertId;
    }

    public long deleteSimilarMorphedAcc(BaseAccessoryItem item) {
        String accessoryType = item.getAccessoryType();
        String[] whereColumns = new String[]{};
        String[] whereValues = new String[]{};
        if (ConstantsUtil.isAccessoryIndependent(accessoryType)) {
            whereColumns = new String[]{DatabaseHandler.ACCESSORY_TYPE};
            whereValues = new String[]{ item.getAccessoryType()};
        } else if (ConstantsUtil.isAccessoryFaceRelated(accessoryType)) {
            String faceId = item.getFaceID();
            if(faceId == null) {
                //faceId = User.getInstance().getDefaultFaceItem().getFaceId();
                return 0;
            }
            whereValues = new String[]{accessoryType, faceId};
        } else if (ConstantsUtil.isAccessoryBodyRelated(accessoryType)) {
            whereColumns = new String[]{DatabaseHandler.ACCESSORY_TYPE, DatabaseHandler.PB_ID};
            String pbId = item.getPbID();
            if(pbId == null) {
                //pbId = User.getInstance().getDefaultFaceItem().getPbId();
                return 0;
            }
            whereValues = new String[]{accessoryType, pbId};
        }
        if (ConstantsUtil.isAccessoryBodyFaceRelated(accessoryType)) {
            whereColumns = new String[]{DatabaseHandler.ACCESSORY_TYPE, DatabaseHandler.PB_ID, DatabaseHandler.FACE_ID};
            if(item.getPbID() == null || item.getFaceID() == null) {
                return 0;
            }
            whereValues = new String[]{accessoryType, item.getPbID(), item.getFaceID()};
        }
        long insertId = 0;
        insertId = delete(DatabaseHandler.TABLE_ACCESSORY, whereColumns, whereValues);
        return insertId;
    }

//    public long deleteFaceBodyAccessories(String faceId, String PBId) {
//        String[] whereColumn = new String[]{DatabaseHandler.FACE_ID, DatabaseHandler.PB_ID};
//        String[] whereValue = new String[]{faceId, PBId};
//        long insertId = delete(DatabaseHandler.TABLE_ACCESSORY, whereColumn, whereValue);
//        return insertId;
//    }
//
//    public long deleteBodyAccessories(String pbId) {
//        if (pbId == null || pbId.length() == 0)
//            return -1;
//        String[] whereColumn = new String[]{DatabaseHandler.PB_ID};
//        String[] whereValue = new String[]{pbId};
//        long insertId = delete(DatabaseHandler.TABLE_ACCESSORY,
//                whereColumn, whereValue);
//        return insertId;
//    }

    public long deleteFaceBodyAccessoriesPBChange(String faceId, String PBId) {//TODO
        String[] whereColumn = new String[]{DatabaseHandler.FACE_ID, DatabaseHandler.PB_ID};
        String[] whereValue = new String[]{faceId, PBId};
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHandler.OBJ_KEY_NAME,"");
        cv.put(DatabaseHandler.TEXTURE_KEY_NAME,"");
        cv.put(DatabaseHandler.OBJ_DOWNLOAD_STATUS,ConstantsUtil.EDownloadStatusType.eDownloadTobeStarted.intStatus());
        cv.put(DatabaseHandler.TEXTURE_DOWNLOAD_STATUS,ConstantsUtil.EDownloadStatusType.eDownloadTobeStarted.intStatus());
        String whereStr = DatabaseHandler.FACE_ID + " =? AND "+ DatabaseHandler.PB_ID +" =? ";

        long insertId = database.update(DatabaseHandler.TABLE_ACCESSORY, cv, whereStr, whereValue);
        return insertId;
    }

    public long deleteBodyAccessoriesPBChange(String pbId) {//TODO
        if (pbId == null || pbId.length() == 0)
            return -1;
        //TODO remove shoes
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHandler.OBJ_KEY_NAME,"");
        cv.put(DatabaseHandler.TEXTURE_KEY_NAME,"");
        cv.put(DatabaseHandler.OBJ_DOWNLOAD_STATUS,ConstantsUtil.EDownloadStatusType.eDownloadTobeStarted.intStatus());
        cv.put(DatabaseHandler.TEXTURE_DOWNLOAD_STATUS,ConstantsUtil.EDownloadStatusType.eDownloadTobeStarted.intStatus());
        long insertId = database.update(DatabaseHandler.TABLE_ACCESSORY, cv, DatabaseHandler.PB_ID + " =? ", new String[]{pbId});
        return insertId;
    }

    /*********** UPDATE ACCESSORY ************/

}
