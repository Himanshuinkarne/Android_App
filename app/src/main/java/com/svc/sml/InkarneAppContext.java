package com.svc.sml;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;
import com.svc.sml.Database.ComboData;
import com.svc.sml.Database.ComboDataLooksItem;
import com.svc.sml.Database.ComboDataReconcile;
import com.svc.sml.Database.InkarneDataSource;
import com.svc.sml.Database.User;
import com.svc.sml.Utility.AWSUtil;
import com.svc.sml.Utility.Connectivity;
import com.svc.sml.Utility.ConstantsFunctional;
import com.svc.sml.Utility.ConstantsUtil;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by himanshu on 1/12/16.
 */
public class InkarneAppContext extends Application {
    private final static String LOGTAG = InkarneAppContext.class.toString();
    public static String CategoryYourLikes = "isLiked";
    public static String CategoryYourHistory = "History";
    public static String CategoryYourTrending = "Trending";
    public final static String SHARED_PREF_FIEST_TIME_COMBO_RENDERED = "is_first_time_combo_Rendered";
    private static Context context;
    private static InkarneDataSource dataSource;
    private static TransferUtility transferUtility;
    private static int cartNumber = -1;

    public static ComboData combData ;
    public static String comboId = "";

    public static String adjustPicActivityPicPath;
    public static String fiducialPicPath;

    public static boolean isAddFaceForRedoAvatar = false;
    public static boolean shouldRearrangeLooks = true;
    private Timer mActivityTransitionTimer;
    private TimerTask mActivityTransitionTimerTask;
    public boolean wasInBackground;
    private final long MAX_ACTIVITY_TRANSITION_TIME_MS = 2000;

    public static ArrayList<ComboDataLooksItem> listOfComboList = new ArrayList<>();
    public static ArrayList<ComboDataLooksItem> listOfComboListTemp = new ArrayList<>();
    public static ArrayList<ComboData> listComboHistory;
    public static ArrayList<ComboData> listComboLikes;

    private static Typeface inkarneTypeFaceHeader = null;
    private static Typeface inkarneTypeFaceML = null;
    private static Typeface inkarneTypeOpensans = null;
    private static Typeface inkarneTypeFaceMolengo = null;
    private static Typeface inkarneTypeFaceFutura = null;

    //private static final String TRACKING_ID = "UA-81490524-1";
    private static final String TRACKING_ID = "UA-81490524-2";
    private static final String TEST_TRACKING_ID="UA-81467859-1";
    private Tracker mTracker;
    private static final boolean isTest = false;
    public  static  JSONObject notificationData = null;

    private static  boolean isDefaultFaceChanged;
    @Override public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        //LogSecretKey();//TODO
        // SharedPreferences settings = InkarneAppContext.getAppContext().getSharedPreferences("inkarne", 0);
        SharedPreferences settings = context.getSharedPreferences("inkarne", 0);
        isDefaultFaceChanged = settings.getBoolean(User.SHARED_PREF_IS_DEFAULT_FACE_CHANGED, true);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        //OneSignal.setLogLevel(OneSignal.LOG_LEVEL.DEBUG, OneSignal.LOG_LEVEL.DEBUG);
        //OneSignal.startInit(this).init();
        OneSignal.startInit(this)
                .setNotificationOpenedHandler(new ExampleNotificationOpenedHandler())
                .autoPromptLocation(true)
                .init();
        //Todo crashlytics change - to be uncommented
        //Fabric.with(this, new Crashlytics());
    }

    public static String getLOGTAG() {
        return LOGTAG;
    }

    public ArrayList<ComboData> getListComboLikes() {
        return listComboLikes;
    }

    public void setListComboLikes(ArrayList<ComboData> listComboLikes) {
        InkarneAppContext.listComboLikes = listComboLikes;
    }

    public ArrayList<ComboData> getListComboHistory() {
        return listComboHistory;
    }

    public void setListComboHistory(ArrayList<ComboData> listComboHistory) {
        InkarneAppContext.listComboHistory = listComboHistory;
    }

    public static ArrayList<ComboDataLooksItem> getListOfComboList() {
        Log.e(LOGTAG,"load data sync");
        loadDataIfNotLoaded();
        return listOfComboList;
    }

    public static ComboDataLooksItem getLookItemForIndex(int index) {
        ComboDataLooksItem item = null;
        Log.e(LOGTAG,"getLookItemForIndex :" + index);
        loadDataIfNotLoaded();
        ArrayList<ComboDataLooksItem> list = getListOfComboList();
        if(list.size() > index){
            item = list.get(index);
            Log.e(LOGTAG,"getLookItemForIndex :" + item.getLooksLabelName());
        }
        return item;
    }

    public static void setListOfComboList(ArrayList<ComboDataLooksItem> listOfComboList1) {
        listOfComboList = listOfComboList1;
    }

    public void startActivityTransitionTimer() {
        this.mActivityTransitionTimer = new Timer();
        this.mActivityTransitionTimerTask = new TimerTask() {
            public void run() {
                InkarneAppContext.this.wasInBackground = true;
            }
        };

        this.mActivityTransitionTimer.schedule(mActivityTransitionTimerTask,
                MAX_ACTIVITY_TRANSITION_TIME_MS);
    }

    public void stopActivityTransitionTimer() {
        if (this.mActivityTransitionTimerTask != null) {
            this.mActivityTransitionTimerTask.cancel();
        }

        if (this.mActivityTransitionTimer != null) {
            this.mActivityTransitionTimer.cancel();
        }

        this.wasInBackground = false;
    }

    public static boolean isDefaultFaceChanged() {
        return isDefaultFaceChanged;
    }

    public static void setIsDefaultFaceChanged(boolean isDefaultFaceChanged) {
        InkarneAppContext.isDefaultFaceChanged = isDefaultFaceChanged;
    }

    public static void setCartNumber(int cartNumbers){
        SharedPreferences settings = context.getSharedPreferences("inkarne", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(ConstantsUtil.SETTING_KEY_CART_NUMBER, cartNumbers);
        editor.commit();
        cartNumber = cartNumbers;
    }

    public static int getCartNumber(){
        if(cartNumber == -1) {
            SharedPreferences settings = context.getSharedPreferences("inkarne", 0);
            cartNumber = settings.getInt(ConstantsUtil.SETTING_KEY_CART_NUMBER, 0);
            return cartNumber;
        }
        else return cartNumber;
    }

    public static boolean isFirstTimeComboRender(){
        SharedPreferences settings = context.getSharedPreferences("inkarne", 0);
        return settings.getBoolean(SHARED_PREF_FIEST_TIME_COMBO_RENDERED, true);
    }
    public static void setFirstTimeComboRender(boolean isFirstTimeComboRender){
        SharedPreferences settings = context.getSharedPreferences("inkarne", 0);
        SharedPreferences.Editor e = settings.edit();
        e.putBoolean(SHARED_PREF_FIEST_TIME_COMBO_RENDERED, isFirstTimeComboRender);
        e.commit();
    }

    public static void incrementCartNumber(int increment){
        int cartNumber = getCartNumber();
        cartNumber += increment;
        setCartNumber(cartNumber);
    }

    public static Context getAppContext() {
        return InkarneAppContext.context;
    }
    public static InkarneDataSource getDataSource(){
        if(dataSource == null)
            dataSource = InkarneDataSource.getInstance(context);
        if(dataSource.getDatabase() == null || !dataSource.getDatabase().isOpen())
            dataSource.open();
        return dataSource;
    }

    synchronized public Tracker getTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
// To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            if(isTest)
                mTracker = analytics.newTracker(TEST_TRACKING_ID);
            else
                mTracker = analytics.newTracker(TRACKING_ID);
        }
        return mTracker;
    }

//    public static boolean isOnline() {
//        //ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
//        ConnectivityManager cm = (ConnectivityManager) getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo netInfo = cm.getActiveNetworkInfo();
//        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
//            Log.e(LOGTAG,"*** network connected *****");
//            return true;
//        } else {
//            Log.e(LOGTAG,"**** network not connected *****");
//            return false;
//        }
//    }

    public static TransferUtility getTransferUtility() {
        if(transferUtility == null) {
            transferUtility = AWSUtil.getTransferUtility(context);
        }

        return transferUtility;
    }

    public static void setTransferUtility(TransferUtility transferUtility) {
        InkarneAppContext.transferUtility = transferUtility;
    }

    public static void cleanUpDownload(ArrayList<TransferObserver>observers){
        for (TransferObserver ob : observers) {
            if (transferUtility != null)
                transferUtility.cancel(ob.getId());
            ob.cleanTransferListener();
        }
        observers.clear();
    }

    public static Typeface getInkarneTypeFaceMolengo(){
        if(inkarneTypeFaceMolengo ==null) {
            String fontPath = "fonts/Molengo-Regular.ttf";
            inkarneTypeFaceMolengo = Typeface.createFromAsset(context.getAssets(), fontPath);
        }
        return inkarneTypeFaceMolengo;
    }


    public static Typeface getInkarneTypeFaceHeaderJennaSue(){
        if(inkarneTypeFaceHeader ==null) {
            //String fontPath = "fonts/font-lobster-two-regular.otf";
            String fontPath = "fonts/Jenna_sue.ttf";
            inkarneTypeFaceHeader = Typeface.createFromAsset(context.getAssets(), fontPath);
        }
        return inkarneTypeFaceHeader;
    }

    public static Typeface getInkarneTypeFaceML(){
        if(inkarneTypeFaceML ==null) {
            //String fontPath = "fonts/font-lobster-two-regular.otf";
            String fontPath = "fonts/font-moon-light.otf";
            inkarneTypeFaceML = Typeface.createFromAsset(context.getAssets(), fontPath);
        }
        return inkarneTypeFaceML;
    }

    public static Typeface getInkarneTypeOpensans(){
        if(inkarneTypeOpensans ==null) {
            //String fontPath = "fonts/font-moon-light.otf";
            //String fontPath = "fonts/montserrat_regular.ttf";
            String fontPath = "fonts/opensans-regular.ttf";
            inkarneTypeOpensans = Typeface.createFromAsset(context.getAssets(), fontPath);
        }
        return inkarneTypeOpensans;
    }


    public static SQLiteDatabase getOpenedDB() {
        Log.i("debug", "Database opened");
        if(getDataSource().getDatabase() == null || !getDataSource().getDatabase().isOpen())
            getDataSource().open();
        return getDataSource().getDatabase();
    }
    public static void closeDB() {
        Log.i("debug", "Database opened");
        getDataSource().close();
    }


    /* Checks if external storage is available for read and write */
    public boolean isExtStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /* Checks if external storage is available to at least read */
    public boolean isExtStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    public static void showNetworkAlert(){
        if(!Connectivity.isConnected(getAppContext())){
            Toast.makeText(getAppContext(), ConstantsUtil.MESSAGE_TOAST_NETWORK_RESPONSE_FAILED,Toast.LENGTH_SHORT).show();
        }
    }

    public static void showAlert(String message){
        if(Connectivity.isConnected(getAppContext())){
            Toast.makeText(getAppContext(), message,Toast.LENGTH_SHORT).show();
        }
    }

    public static void saveComboToPref(ComboData combodata) {
        combData = combodata;
    }

    public static ComboData getComboFromPref() {
        return combData;

    }

    public static boolean getSettingIsWifiOnlyDownload() {
        SharedPreferences settings = InkarneAppContext.getAppContext().getSharedPreferences("inkarne", 0);
        boolean shouldDownloadWithoutWifi = settings.getBoolean(ConstantsUtil.SETTING_KEY_IS_DOWNLOAD_WIFI_ONLY, false);
        return shouldDownloadWithoutWifi;
    }
    public static void saveSettingIsWifiOnlyDownload(boolean isChecked) {
        SharedPreferences settings = InkarneAppContext.getAppContext().getSharedPreferences("inkarne", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(ConstantsUtil.SETTING_KEY_IS_DOWNLOAD_WIFI_ONLY, isChecked);
        editor.commit();
        //setIsDefaultFaceChanged(isChecked);
    }

    public static boolean getSettingIsAutoRotateLookDisabled() {
        //boolean shouldAutoRotate = getSP().getBoolean(ConstantsUtil.SETTING_KEY_IS_AUTO_ROTATE_LOOK, true);//nov
        boolean shouldAutoRotate = getSP().getBoolean(ConstantsUtil.SETTING_KEY_IS_AUTO_ROTATE_LOOK, false);//nov
        return shouldAutoRotate;
    }
    public static void saveSettingIsAutoRotateLookDisabled(boolean isChecked) {
        SharedPreferences.Editor editor = getSP().edit();
        editor.putBoolean(ConstantsUtil.SETTING_KEY_IS_AUTO_ROTATE_LOOK, isChecked);
        editor.commit();
    }

    public static void saveSettingIsDefaultFaceChanged(boolean isChanged) {
        SharedPreferences settings = InkarneAppContext.getAppContext().getSharedPreferences("inkarne", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(User.SHARED_PREF_IS_DEFAULT_FACE_CHANGED, isChanged);
        editor.commit();
        isDefaultFaceChanged = isChanged;
    }


    //NOTE: TO KET SECRETKEY USED IN MSG91 SEND OTP , application creation on console
    public static void LogSecretKey() {
        MessageDigest md = null;
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(),
                    PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures) {
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
        String key = Base64.encodeToString(md.digest(), Base64.DEFAULT);
        Log.i("SecretKey = ", key);
        Toast.makeText(getAppContext(),key,Toast.LENGTH_LONG).show();
        //writeToFile("key",key);

    }

    public static void writeToFile(String fileName, String body)
    {
        FileOutputStream fos = null;
        try {
            final File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/msg99/" );
            if (!dir.exists())
            {
                if(!dir.mkdirs()){
                    Log.e("ALERT","could not create the directories");
                }
            }
            final File myFile = new File(dir, fileName + ".txt");
            if (!myFile.exists())
            {
                myFile.createNewFile();
            }
            fos = new FileOutputStream(myFile);
            fos.write(body.getBytes());
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    /******************SETTINGS *******************
     *
     */
    public static SharedPreferences getSP(){
        SharedPreferences settings = InkarneAppContext.getAppContext().getSharedPreferences("inkarne", 0);
        return settings;
    }

    public static int getSPValue(String key,int defaultValue){
        return  getSP().getInt(key, defaultValue);
    }


    public static String getSPValue(String key,String defValue){
        return  getSP().getString(key, defValue);
    }

    public static boolean getSPValue(String key,boolean defaultValue){
        return  getSP().getBoolean(key, defaultValue);
    }


    public static void saveSPValue(String key,int value){
        getSP().edit().putInt(key,value).commit();
    }


    public static void saveSPValue(String key,String value){
        getSP().edit().putString(key, value).commit();
    }

    public static void saveSPValue(String key,boolean value){
        getSP().edit().putBoolean(key, value).commit();
    }


    /******************ComboData *******************
     *
     */
    public static int getComboIndex(ArrayList<ComboData> trending, ComboData comboData) {
        int index = 0;
        if (comboData == null)
            return 0;
        if (trending == null)
            return -1;
        Log.e(LOGTAG, "Trending.. id: " + comboData.getCombo_ID());
        for (ComboData c : trending) {
            Log.w(LOGTAG, "c id: " + c.getCombo_ID());
            if (c.getCombo_ID().equals(comboData.getCombo_ID())) {
                return index;
            }
            index++;
        }
        return index;
    }

    public static void loadDataIfNotLoaded(){
        if (listOfComboList == null || listOfComboList.size() == 0) {
            Log.e(LOGTAG,"load data sync");
            initData();
            Log.e(LOGTAG,"loaded data sync");
        }
    }
    public static void loadDataIfNotLoadedAsync(){
        if (listOfComboList == null || listOfComboList.size() == 0) {
            Log.e(LOGTAG,"load data async");
            new LoadLooksListTask().execute();
        }
    }
    public static void refreshData(){
        listOfComboList.clear();
        initData();
    }

    public static void refreshDataAsync(){
        listOfComboListTemp.clear();
        new LoadLooksListTask().execute();
    }

    public static class LoadLooksListTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            initData();
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... params) {


        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    public static void initData() {
      synchronized (listOfComboList) {
          if (dataSource == null)
              dataSource = getDataSource();
          ArrayList<ComboData> comboReconcileData = (ArrayList<ComboData>) dataSource.getComboReconcileData(InkarneAppContext.shouldRearrangeLooks);
          filterCombo(comboReconcileData);
          InkarneAppContext.shouldRearrangeLooks = false;
      }
    }

    public static ArrayList<ComboDataLooksItem> getSkeletonListOfComboList() {
        ArrayList<ComboDataLooksItem> list = new ArrayList<>();
        for (int i = 0; i < ConstantsUtil.arrayListLooksCategory.size(); i++) {
            ComboDataLooksItem looksListItem = new ComboDataLooksItem(ConstantsUtil.arrayListLooksLabelName.get(i), ConstantsUtil.arrayListLooksCategory.get(i));
            list.add(looksListItem);
        }
        return list;
    }

    public static void filterCombo(ArrayList<ComboData> comboList) {
        listOfComboListTemp = getSkeletonListOfComboList();
        for (ComboData c : comboList) {
            if (c.getCombo_ID() == null || c.getCombo_ID().isEmpty() || c.getCombo_ID().equals("null")) //TODO
                continue;

            //Log.e(LOGTAG, "filterCombo :combo Id : " + c.getCombo_ID());
//            if (c.getVogue_Flag() == null) {
//                continue;
//            }
            if (c.getIsLiked() == 1) {
                addToLooksList(listOfComboListTemp,c, CategoryYourLikes);
            }
            if (c.getViewCount() > 0) {
                addToLooksList(listOfComboListTemp,c, CategoryYourHistory);
            }

            for (String style : ConstantsUtil.arrayListLooksCategory) {
                if (c.getCombo_Style_Category().equals(style)) {
                    addToLooksList(listOfComboListTemp,c, style);
                }
            }
            addToLooksListForYou(listOfComboListTemp,c,ConstantsUtil.arrayListLooksCategory.get(ConstantsUtil.arrayListLooksCategory.size()-3));
        }
        addToTrendingLooksList(listOfComboListTemp);
        reArrangeLikes(listOfComboListTemp);
        reArrangeHistory(listOfComboListTemp);
        listOfComboList = listOfComboListTemp;
    }

    //ArrayList<ComboDataLooksItem>

    private static void addToLooksList(ArrayList<ComboDataLooksItem> list,ComboData c, String looksCategory) {
        int index = ConstantsUtil.arrayListLooksCategory.indexOf(looksCategory);
        ComboDataLooksItem looksItem = list.get(index);
        looksItem.getComboList().add(c);
    }
    private static void addToLooksList(ComboData c, String looksCategory) {
        int index = ConstantsUtil.arrayListLooksCategory.indexOf(looksCategory);
        ComboDataLooksItem looksItem = listOfComboList.get(index);
        looksItem.getComboList().add(c);
    }

    private static void addToTrendingLooksList(ArrayList<ComboDataLooksItem> list) {
        int index = ConstantsUtil.arrayListLooksCategory.indexOf(CategoryYourTrending);
        ComboDataLooksItem TrendingItem = list.get(index);
        ArrayList<ComboData> trending = (ArrayList<ComboData>) dataSource.getComboReconcileTrendingData(InkarneAppContext.shouldRearrangeLooks);
        TrendingItem.setComboList(trending);
    }

    private static void addToTrendingLooksList() {
        int index = ConstantsUtil.arrayListLooksCategory.indexOf("Trending");
        ComboDataLooksItem TrendingItem = listOfComboList.get(index);
        ArrayList<ComboData> trending = (ArrayList<ComboData>) dataSource.getComboReconcileTrendingData(InkarneAppContext.shouldRearrangeLooks);
        TrendingItem.setComboList(trending);
    }

    private static void addToLooksListForYou(ArrayList<ComboDataLooksItem> list,ComboData c, String looksCategory) {
        int index = ConstantsUtil.arrayListLooksCategory.indexOf(looksCategory);
        ComboDataLooksItem looksItem = list.get(index);
        looksItem.getComboList().add(c);

//        int maxCount = ConstantsFunctional.COUNT_LOOKS_FORYOU;
//        int count = looksItem.getComboList().size() > maxCount ? maxCount : looksItem.getComboList().size();
//        boolean isInserted = false;
//        for (int i = 0; i < count; i++) {
//            ComboData comboData = looksItem.getComboList().get(i);
//            if (c.getStyle_Rating() > comboData.getStyle_Rating()) {
//                looksItem.getComboList().add(i, c);
//                isInserted = true;
//                break;
//            }
//        }
//        if (isInserted && count >= maxCount) {
//            looksItem.getComboList().remove(looksItem.getComboList().size() - 1);
//        }
//        if (!isInserted && count < maxCount) {
//            looksItem.getComboList().add(c);
//        }
    }


    private static void addToLooksListForYou(ComboData c, String looksCategory) {
        int index = ConstantsUtil.arrayListLooksCategory.indexOf(looksCategory);
        ComboDataLooksItem looksItem = listOfComboList.get(index);
        int maxCount = ConstantsFunctional.COUNT_LOOKS_FORYOU;
        int count = looksItem.getComboList().size() > maxCount ? maxCount : looksItem.getComboList().size();
        boolean isInserted = false;
        for (int i = 0; i < count; i++) {
            ComboData comboData = looksItem.getComboList().get(i);
            if (c.getStyle_Rating() > comboData.getStyle_Rating()) {
                looksItem.getComboList().add(i, c);
                isInserted = true;
                break;
            }
        }
        if (isInserted && count >= maxCount) {
            looksItem.getComboList().remove(looksItem.getComboList().size() - 1);
        }
        if (!isInserted && count < maxCount) {
            looksItem.getComboList().add(c);
        }
    }

    public static boolean isListContainCombo(ArrayList<ComboData> list, ComboData comboData) {
        if (list == null || comboData == null)
            return false;
        for (ComboData c : list) {
            if (c.getCombo_ID().equals(comboData.getCombo_ID())) {
                return true;
            }
        }
        return false;
    }

    public static void updateListOfComboListForLikesAndHistory() {
        if(listOfComboList == null||listOfComboList.size()== ConstantsUtil.arrayListLooksCategory.size() ){
            return;
        }
        int index = ConstantsUtil.arrayListLooksCategory.indexOf(CategoryYourLikes);
        ComboDataLooksItem looksItem = listOfComboList.get(index);

        ArrayList<ComboData> tempList = looksItem.getComboList();
        tempList.clear();
        //looksItem.setComboList(listComboLikes);
        for (ComboData c : listComboLikes) {
            tempList.add(c);
        }

        int indexH = ConstantsUtil.arrayListLooksCategory.indexOf(CategoryYourHistory);
        ComboDataLooksItem historyItem = listOfComboList.get(indexH);

        ArrayList<ComboData> tempList2 = historyItem.getComboList();
        tempList2.clear();
        // historyItem.setComboList(listComboHistory);
        for (ComboData c : listComboHistory) {
            tempList2.add(c);
        }
    }

    public static void populateHistoryAndLikes() {
        if (listOfComboList == null || listOfComboList.size() < ConstantsUtil.arrayListLooksCategory.size())
            return;
        int index = ConstantsUtil.arrayListLooksCategory.indexOf(CategoryYourLikes);
        ComboDataLooksItem looksItem = listOfComboList.get(index);
        ArrayList<ComboData> tempList = looksItem.getComboList();

        if (listComboLikes == null)
            listComboLikes = new ArrayList<>();
        listComboLikes.clear();
        for (ComboData c : tempList) {
            listComboLikes.add(c);
        }

        //listComboLikes.addAll(looksItem.getComboList());

        int indexH = ConstantsUtil.arrayListLooksCategory.indexOf(CategoryYourHistory);
        ComboDataLooksItem historyItem = listOfComboList.get(indexH);
        ArrayList<ComboData> tempList2 = historyItem.getComboList();

        if (listComboHistory == null)
            listComboHistory = new ArrayList<>();
        listComboHistory.clear();
        for (ComboData c : tempList2) {
            listComboHistory.add(c);
        }
    }


    public static void addToLikes(final ComboData comboData) {
        if (listComboLikes == null)
            return;
        int isLiked = comboData.isLiked() == 0 ? 1 : 0;
        if (isLiked == 0) {
            listComboLikes.remove(comboData);
        } else {
            if (!isListContainCombo(listComboLikes, comboData)) {
                listComboLikes.add(0, comboData);
            } else {
                listComboLikes.remove(comboData);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        listComboLikes.add(0, comboData);
                    }
                });
            }
        }
    }


    public static void addToHistory(final ComboData comboData) {
        if (listComboHistory == null)
            return;
        if (!isListContainCombo(listComboHistory, comboData)) {
            listComboHistory.add(0, comboData);
        } else {
            listComboHistory.remove(comboData);
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    listComboHistory.add(0, comboData);
                }
            });
        }
    }

    private static void reArrangeLikes(ArrayList<ComboDataLooksItem> list) {
        if (list == null || list.size() < ConstantsUtil.arrayListLooksCategory.size())
            return;
        int index = ConstantsUtil.arrayListLooksCategory.indexOf(CategoryYourLikes);
        ComboDataLooksItem looksItem = list.get(index);
        ArrayList<ComboData> listLikes = looksItem.getComboList();
        for (ComboData co : listLikes) {
            Log.d(LOGTAG, co.getDateSeenInMilli() + "  :" + co.getCombo_ID());
        }

        Collections.sort(listLikes, Comparators.likesDESC);
        Log.w(LOGTAG, "**************** rearranged likes *********************");
        for (ComboData co : listLikes) {
            Log.d(LOGTAG, co.getDateSeenInMilli() + "  :" + co.getCombo_ID());
        }
        looksItem.setComboList(listLikes);

        if (listComboLikes == null)
            listComboLikes = new ArrayList<>();
        listComboLikes.clear();
        for (ComboData c : listLikes) {
            listComboLikes.add(c);
        }
    }

    private static void reArrangeLikes() {
        if (listOfComboList == null || listOfComboList.size() < ConstantsUtil.arrayListLooksCategory.size())
            return;
        int index = ConstantsUtil.arrayListLooksCategory.indexOf(CategoryYourLikes);
        ComboDataLooksItem looksItem = listOfComboList.get(index);
        ArrayList<ComboData> listLikes = looksItem.getComboList();
        for (ComboData co : listLikes) {
            Log.d(LOGTAG, co.getDateSeenInMilli() + "  :" + co.getCombo_ID());
        }

        Collections.sort(listLikes, Comparators.likesDESC);
        Log.w(LOGTAG, "**************** rearranged likes *********************");
        for (ComboData co : listLikes) {
            Log.d(LOGTAG, co.getDateSeenInMilli() + "  :" + co.getCombo_ID());
        }
        looksItem.setComboList(listLikes);

        if (listComboLikes == null)
            listComboLikes = new ArrayList<>();
        listComboLikes.clear();
        for (ComboData c : listLikes) {
            listComboLikes.add(c);
        }
    }

    private static void reArrangeHistory(ArrayList<ComboDataLooksItem> list) {
        if (list == null || list.size() < ConstantsUtil.arrayListLooksCategory.size())
            return;
        int indexHistory = ConstantsUtil.arrayListLooksCategory.indexOf(CategoryYourHistory);
        ComboDataLooksItem historyItem = list.get(indexHistory);
        ArrayList<ComboData> listHistory = historyItem.getComboList();
        Log.w(LOGTAG, "**************** History *********************");
        for (ComboData co : listHistory) {
            Log.d(LOGTAG, co.getDateSeenInMilli() + "  :" + co.getCombo_ID());
        }
        Collections.sort(listHistory, Comparators.likesDESC);
        Log.w(LOGTAG, "**************** rearranged History *********************");
        for (ComboData co : listHistory) {
            Log.d(LOGTAG, co.getDateSeenInMilli() + "  :" + co.getCombo_ID());
        }
        historyItem.setComboList(listHistory);

        if (listComboHistory == null)
            listComboHistory = new ArrayList<>();
        //listComboLikes.addAll(listLikes);
        listComboHistory.clear();
        for (ComboData c : listHistory) {
            listComboHistory.add(c);
        }
    }

    private static void reArrangeHistory() {
        if (listOfComboList == null || listOfComboList.size() < ConstantsUtil.arrayListLooksCategory.size())
            return;
        int indexHistory = ConstantsUtil.arrayListLooksCategory.indexOf(CategoryYourHistory);
        ComboDataLooksItem historyItem = listOfComboList.get(indexHistory);
        ArrayList<ComboData> listHistory = historyItem.getComboList();
        Log.w(LOGTAG, "**************** History *********************");
        for (ComboData co : listHistory) {
            Log.d(LOGTAG, co.getDateSeenInMilli() + "  :" + co.getCombo_ID());
        }
        Collections.sort(listHistory, Comparators.likesDESC);
        Log.w(LOGTAG, "**************** rearranged History *********************");
        for (ComboData co : listHistory) {
            Log.d(LOGTAG, co.getDateSeenInMilli() + "  :" + co.getCombo_ID());
        }
        historyItem.setComboList(listHistory);

        if (listComboHistory == null)
            listComboHistory = new ArrayList<>();
        //listComboLikes.addAll(listLikes);
        listComboHistory.clear();
        for (ComboData c : listHistory) {
            listComboHistory.add(c);
        }
    }

    public static class Comparators {
        public static Comparator<ComboDataReconcile> likesDESC = new Comparator<ComboDataReconcile>() {
            @Override
            public int compare(ComboDataReconcile o1, ComboDataReconcile o2) {
                //return o1.age - o2.age;
                return (int) o2.getDateSeenInMilli() - (int) o1.getDateSeenInMilli();
            }
        };
    }


    private class ExampleNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
        // This fires when a notification is opened by tapping on it.
        @Override
        public void notificationOpened(OSNotificationOpenResult result) {
            OSNotificationAction.ActionType actionType = result.action.type;
            notificationData = result.notification.payload.additionalData;
            String customKey;

            if (notificationData != null) {
                customKey = notificationData.optString("url", null);
                if (customKey != null)
                    Log.i("OneSignalExample", "customkey set with value: " + customKey);
            }

            if (actionType == OSNotificationAction.ActionType.ActionTaken)
                Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionID);

            // The following can be used to open an Activity of your choice.

            // Intent intent = new Intent(getApplication(), YourActivity.class);
            // intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            // startActivity(intent);

            // Add the following to your AndroidManifest.xml to prevent the launching of your main Activity
            //  if you are calling startActivity above.
         /*
            <application ...>
              <meta-data android:name="com.onesignal.NotificationOpened.DEFAULT" android:value="DISABLE" />
            </application>
         */
        }
    }
}
