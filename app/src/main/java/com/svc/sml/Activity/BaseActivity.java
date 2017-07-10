package com.svc.sml.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.facebook.appevents.*;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.svc.sml.Database.InkarneDataSource;
import com.svc.sml.InkarneAppContext;
import com.svc.sml.R;
import com.svc.sml.Utility.AWSUtil;

import java.util.ArrayList;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public abstract class BaseActivity extends AppCompatActivity {

    private static final String LOGTAG = BaseActivity.class.getName();
    private static final int INDEX_DOWNLOAD_LEG_OBJ = 11;
    private static final int INDEX_DOWNLOAD_LEG_TEX = 111;
    private static final int INDEX_DOWNLOAD_A1_OBJ = 0;
    private static final int INDEX_DOWNLOAD_A1_TEX = 1;
    private static final int INDEX_DOWNLOAD_A6_OBJ = 6;
    private static final int INDEX_DOWNLOAD_A6_TEX = 61;
    private static final int INDEX_DOWNLOAD_A7_OBJ = 7;
    private static final int INDEX_DOWNLOAD_A7_TEX = 71;
    private static final int INDEX_DOWNLOAD_A8_OBJ = 8;
    private static final int INDEX_DOWNLOAD_A8_TEX = 81;
    private static final int INDEX_DOWNLOAD_A9_OBJ = 9;
    private static final int INDEX_DOWNLOAD_A9_TEX = 91;
    private static final int INDEX_DOWNLOAD_A10_OBJ = 10;
    private static final int INDEX_DOWNLOAD_A10_TEX = 101;

    protected ProgressDialog mProgressDialog;
    protected InkarneDataSource dataSource;
    protected TransferUtility transferUtility;
    private int countRetry = 0;
    protected ArrayList<TransferObserver> observers = new ArrayList<TransferObserver>();
    protected Tracker mTracker;
    protected  AppEventsLogger logger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        createTracker();
        //verifyStoragePermissions(this);
        dataSource = InkarneAppContext.getDataSource();
        transferUtility = AWSUtil.getTransferUtility(this);
        createProgressDialog();
        //setContentView(R.layout.activity_base);
    }

    protected  void createTracker(){
        InkarneAppContext application = (InkarneAppContext)getApplication();
        mTracker = application.getTracker();
        logger = AppEventsLogger.newLogger(this);
    }

    protected void GATrackActivity(String screenName){
        Log.i(LOGTAG, "screen name: " + screenName);
        mTracker.setScreenName(screenName);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        Bundle parameters = new Bundle();
        parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, LOGTAG);
        logger.logEvent(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT,parameters);
    }

    protected void trackEvent(String cat,String l2,String l3){
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory(cat)
                .setAction(l2)
                .setLabel(l3)
                .build());
    }


    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE

    };

    public void createProgressDialog(){
        mProgressDialog = new ProgressDialog(this,R.style.AppCompatAlertDialogStyle);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                ProgressBar v = (ProgressBar) mProgressDialog.findViewById(android.R.id.progress);
                // v.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.tcolor,null),android.graphics.PorterDuff.Mode.MULTIPLY);

            }
        });
    }

    public ProgressDialog getProgressDialog(){
        ProgressDialog p = new ProgressDialog(BaseActivity.this,R.style.AppCompatAlertDialogStyle);
        p.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        p.setCanceledOnTouchOutside(false);
        p.setCancelable(true);
        return p;
    }
    public ProgressDialog getProgressDialogTranslucent(){
        //ProgressDialog p = new ProgressDialog(BaseActivity.this,R.style.AppCompatAlertTranslucentDialogStyle);
        ProgressDialog p = new ProgressDialog(BaseActivity.this,R.style.AppCompatAlertDialogStyle);
        p.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        p.setCanceledOnTouchOutside(false);
        p.setCancelable(true);
        return p;
    }
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        boolean isReadPermission = true;
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            isReadPermission = false;
            //We don't have permission so prompt the user
//            ActivityCompat.requestPermissions(
//                    activity,
//                    PERMISSIONS_STORAGE,
//                    REQUEST_EXTERNAL_STORAGE
//            );
        }

        permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED || !isReadPermission) {
            //We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    //@Override


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    storagePermissionVerified();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    showPermissionAlert();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    protected void storagePermissionVerified(){

    }

    private void showPermissionAlert(){
        android.app.AlertDialog.Builder b = new android.app.AlertDialog.Builder(BaseActivity.this);
        b.setTitle("Permission required to access device storage");
        b.setMessage("We need permission to store data on device storage to proceed, kindly allow to proceed.");
        b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                verifyStoragePermissions(BaseActivity.this);
            }
        });
        if(!isFinishing())
         b.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        dataSource.open();
        InkarneAppContext myApp = (InkarneAppContext)this.getApplication();
        if (myApp.wasInBackground)
        {
            //Do specific came-here-from-background code
        }

        myApp.stopActivityTransitionTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ((InkarneAppContext)this.getApplication()).startActivityTransitionTimer();
        //dataSource.close();
    }

    @Override
    protected void onStop() {
        super.onStop();
        removeTransferListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissProgress();
    }

    protected void showProgress(String msg) {
        if(mProgressDialog== null)
            mProgressDialog = getProgressDialog();
        if (mProgressDialog != null && mProgressDialog.isShowing())
            dismissProgress();

        mProgressDialog.setTitle(getResources().getString(R.string.app_name));
        mProgressDialog.setMessage(msg);
        if(!isFinishing())
        mProgressDialog.show();
    }

    protected void showProgress(String title, String msg) {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            dismissProgress();
        mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(msg);
        mProgressDialog.show();
    }

    protected void dismissProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    protected void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    protected void showAlert(String msg) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setTitle(getResources().getString(R.string.app_name))
                .setMessage(msg)
                .setCancelable(false)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();
    }

    protected void showAlert(String title, String msg) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setTitle(title)
                .setMessage(msg)
                .setCancelable(false)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();
    }

    public void memoryCleanup() {
//        System.gc();
//        Runtime.getRuntime().gc();
    }

    //Override
    protected void removeTransferListener() {
        for (TransferObserver ob : observers) {
            if (transferUtility != null)
                transferUtility.cancel(ob.getId());
            ob.cleanTransferListener();
        }
        observers.clear();
    }


   /* *//* check and download ComboDetail *//*
    protected void loadCombo(final ComboData combodata, final int index) {

        new ComboDownloader(BaseActivity.this, combodata, new ComboDownloader.OnComboDownloadListener() {
            @Override
            public void onDownload(ComboData comboData) {
                downloadComboSkuCompleted(comboData);
            }

            @Override
            public void onDownloadFailed(String comboId) {

            }

            @Override
            public void onDownloadProgress(String comboId, int percentage) {

            }

            @Override
            public void onComboInfoFailed(String comboId, int error_code) {

            }
        });

       *//* ComboData comboData = null;
        Log.d(LOGTAG, "loadCombo " + combodata.getCombo_ID());
        if (combodata.getmA1_Obj_Key_Name() == null || combodata.getmA1_Obj_Key_Name().length() == 0) {
            comboData = dataSource.getComboDataByComboID(combodata.getCombo_ID());
            comboData.indexTemp = index;
        }
        if (comboData != null && comboData.getmA1_Obj_Key_Name() != null && comboData.getmA1_Obj_Key_Name().length() != 0) {
            comboData.indexTemp = index;
            Log.d(LOGTAG, "loadCombo 2 " + combodata.getCombo_ID() + "  index:" + index);
            downloadSkus(comboData);
        } else {
            DataManager.getInstance().requestComboDataInfoById(combodata.getCombo_ID(), User.getInstance().getDefaultFaceId(),
                    new DataManager.OnResponseHandlerInterface() {
                        @Override
                        public void onResponse(Object obj) {
                            countRetry = 0;
                            ArrayList<ComboData> arrayListCombo = (ArrayList<ComboData>) obj;
                            if (arrayListCombo != null && arrayListCombo.size() > 0) {
                                ComboData comboData = (ComboData) arrayListCombo.get(0);
                                comboData = dataSource.create(comboData);
                                comboData = dataSource.getComboDataByComboID(comboData.getCombo_ID());
                                comboData.indexTemp = index;
                                Log.d(LOGTAG, "loadCombo 3 requestComboDataInfoById " + combodata.getCombo_ID() + "  index:" + index);
                                downloadSkus(comboData);
                            } else {
                                //Toast.makeText(BaseActivity.this, " ComboInfo not available for comboId: "+combodata.getCombo_ID(), Toast.LENGTH_SHORT).show();
                                Toast.makeText(BaseActivity.this, "Looks Info not available.", Toast.LENGTH_SHORT).show();
                                downloadComboSkuFailed(combodata);
                            }
                        }

                        @Override
                        public void onResponseError(String errorMessage, int errorCode) {
                            Log.e(LOGTAG, "Error getting comboInfo :" + errorMessage);
                            countRetry++;
                            if (!Connectivity.isConnected(getApplicationContext())) {
                                if (countRetry < ConstantsUtil.COUNT_RETRY_SERVICE_CRITICAL) {
                                    loadCombo(combodata, index);
                                }
                                if(countRetry ==1){
                                    Toast.makeText(BaseActivity.this,ConstantsUtil.MESSAGE_TOAST_NETWORK_RESPONSE_FAILED,Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                downloadComboSkuFailed(combodata);
                            }
                            //Toast.makeText(DataActivity.this, "Error getting ComboInfo data ", Toast.LENGTH_LONG).show();
                        }
                    });
        }*//*
    }


    *//* download SKU *//*

    /*//***************************************************************************************************

    protected void downloadSkus(ComboData combodata) {
        combodata.countTobeDownloaded = 12;
        //beginDownload(combodata.getLegItem().getObjAwsKey(), INDEX_DOWNLOAD_LEG_OBJ, combodata.getLegItem().getObjDStatus(), combodata);
        //beginDownload(combodata.getLegItem().getTexAwsKey(), INDEX_DOWNLOAD_LEG_TEX, combodata.getLegItem().getTexDStatus(), combodata);

        beginDownload(combodata.getLegObjKeyName(), INDEX_DOWNLOAD_LEG_OBJ, -1, combodata);
        beginDownload(combodata.getLegTextureKeyName(), INDEX_DOWNLOAD_LEG_TEX, -1, combodata);

        beginDownload(combodata.getmA1_Obj_Key_Name(), INDEX_DOWNLOAD_A1_OBJ, combodata.getObjA1DStatus(), combodata);
        beginDownload(combodata.getmA1_Png_Key_Name(), INDEX_DOWNLOAD_A1_TEX, combodata.getTextureA1DStatus(), combodata);

        beginDownload(combodata.getmA6_Obj_Key_Name(), INDEX_DOWNLOAD_A6_OBJ, combodata.getObjA6DStatus(), combodata);
        beginDownload(combodata.getmA6_Png_Key_Name(), INDEX_DOWNLOAD_A6_TEX, combodata.getTextureA6DStatus(), combodata);

        beginDownload(combodata.getmA7_Obj_Key_Name(), INDEX_DOWNLOAD_A7_OBJ, combodata.getObjA7DStatus(), combodata);
        beginDownload(combodata.getmA7_Png_Key_Name(), INDEX_DOWNLOAD_A7_TEX, combodata.getTextureA7DStatus(), combodata);

        beginDownload(combodata.getmA9_Obj_Key_Name(), INDEX_DOWNLOAD_A9_OBJ, combodata.getObjA9DStatus(), combodata);
        beginDownload(combodata.getmA9_Png_Key_Name(), INDEX_DOWNLOAD_A9_TEX, combodata.getTextureA9DStatus(), combodata);

        beginDownload(combodata.getmA10_Obj_Key_Name(), INDEX_DOWNLOAD_A10_OBJ, combodata.getObjA10DStatus(), combodata);
        beginDownload(combodata.getmA10_Png_Key_Name(), INDEX_DOWNLOAD_A10_TEX, combodata.getTextureA10DStatus(), combodata);

    }


    private void beginDownload(String key, int downloadIndex, int downloadStatus, ComboData comboData) {
        if (key == null || key.length() < 2 || key.equals("null")) {
            Log.d(LOGTAG, "combo :" + comboData.getCombo_ID() + " download key is null or blank ");
            comboData.countTobeDownloaded--;
            if (comboData.countTobeDownloaded == 0)
                checkDownload(comboData);
            return;
        }

        if (downloadStatus == ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus() && ConstantsUtil.checkFileKeysExist(key)) {
            Log.e(LOGTAG, " already downloaded : " + key);
            comboData.countTobeDownloaded--;
            if (comboData.countTobeDownloaded == 0)
                checkDownload(comboData);
            return;
        }
        updateDownloadStatus(comboData,downloadIndex,ConstantsUtil.EDownloadStatusType.eDownloading.intStatus());
        File file = new File(Environment.getExternalStorageDirectory().toString() + "/" + key);
        Log.d(LOGTAG, "combo :" + comboData.getCombo_ID() + "  to be downloaded. file:  " + key);
        if (transferUtility == null) {
            Log.d(LOGTAG, " transferUtility is null. file:  " + key);
            return;
        }
        TransferObserver observer = transferUtility.download(ConstantsUtil.AWSBucketName, key, file);
        Log.d(LOGTAG, " test " + key);
        DownloadListener dListener = new DownloadListener(key, downloadIndex, comboData);
        observer.setTransferListener(dListener);
        observers.add(observer);
    }

    private class DownloadListener implements TransferListener {
        public DownloadListener() {

        }

        private ComboData comboData = null;
        private String key = "";
        private int downloadIndex = -1;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public DownloadListener(String key, int downloadIndex, ComboData comboData) {
            this.comboData = comboData;
            this.key = key;
            this.downloadIndex = downloadIndex;
        }

        @Override
        public void onError(int id, Exception e) {
            Log.e(LOGTAG, e.getStackTrace() + " Error during download  :" + key);
            updateDownloadStatus(comboData, downloadIndex, ConstantsUtil.EDownloadStatusType.eDownloadError.intStatus());
            downloadComboSkuFailed(comboData);
        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

        }

        @Override
        public void onStateChanged(int id, TransferState state) {
            if (state == TransferState.FAILED) {
                Log.e(LOGTAG, "combo :" + comboData.getCombo_ID() + " Download failed :" + key);
                downloadComboSkuFailed(comboData);
            } else if (state == TransferState.COMPLETED) {
                Log.e(LOGTAG, "combo :" + comboData.getCombo_ID() + " ***** Download successful  *****  :" + key);
                comboData.countTobeDownloaded--;
                updateDownloadStatus(comboData, downloadIndex, ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus());
                checkDownload(comboData);
            }
        }
    }

    private void updateDownloadStatus(ComboData comboData, int downloadIndex,int downloadType) {
        switch (downloadIndex) {
            case INDEX_DOWNLOAD_A1_OBJ: {
                comboData.setObjA1DStatus(downloadType);
            }
            break;
            case INDEX_DOWNLOAD_A1_TEX: {
                comboData.setTextureA1DStatus(downloadType);
            }
            break;
            case INDEX_DOWNLOAD_A6_OBJ: {
                comboData.setObjA6DStatus(downloadType);
            }
            break;
            case INDEX_DOWNLOAD_A6_TEX: {
                comboData.setTextureA6DStatus(downloadType);
            }
            break;
            case INDEX_DOWNLOAD_A7_OBJ: {
                comboData.setObjA7DStatus(downloadType);
            }
            break;
            case INDEX_DOWNLOAD_A7_TEX: {
                comboData.setTextureA7DStatus(downloadType);
            }
            break;

            case INDEX_DOWNLOAD_A9_OBJ: {
                comboData.setObjA9DStatus(downloadType);
            }
            break;
            case INDEX_DOWNLOAD_A9_TEX: {
                comboData.setTextureA9DStatus(downloadType);
            }
            break;
            case INDEX_DOWNLOAD_A10_OBJ: {
                comboData.setObjA10DStatus(downloadType);
            }
            break;
            case INDEX_DOWNLOAD_A10_TEX: {
                comboData.setTextureA10DStatus(downloadType);
            }
            break;
            default:
                break;
        }
    }

/*//***************************************************************************************************//*

*//*
    protected void downloadSkus(ComboData combodata) {
        combodata.countTobeDownloaded = 16;
        DownloadListener dListener = new DownloadListener(combodata);
        beginDownload(combodata.getmA1_Obj_Key_Name(), dListener);
        beginDownload(combodata.getmA1_Png_Key_Name(), dListener);
        beginDownload(combodata.getmA2_Obj_Key_Name(), dListener);
        beginDownload(combodata.getmA2_Png_Key_Name(), dListener);
        beginDownload(combodata.getmA6_Obj_Key_Name(), dListener);
        beginDownload(combodata.getmA6_Png_Key_Name(), dListener);
        beginDownload(combodata.getmA7_Obj_Key_Name(), dListener);
        beginDownload(combodata.getmA7_Png_Key_Name(), dListener);
        beginDownload(combodata.getmA8_Obj_Key_Name(), dListener);
        beginDownload(combodata.getmA8_Png_Key_Name(), dListener);
        beginDownload(combodata.getmA9_Obj_Key_Name(), dListener);
        beginDownload(combodata.getmA9_Png_Key_Name(), dListener);
        beginDownload(combodata.getmA10_Obj_Key_Name(), dListener);
        beginDownload(combodata.getmA10_Png_Key_Name(), dListener);
        beginDownload(combodata.getLegObjKeyName(), dListener);
        beginDownload(combodata.getLegTextureKeyName(), dListener);
    }

    private void beginDownload(String key, DownloadListener dListener) {
        ComboData comboData = dListener.getComboData();
        if (key == null || key.length() < 2 || key.equals("null")) {
            Log.d(LOGTAG, "combo :" + comboData.getCombo_ID() + " download key is null or blank ");
            comboData.countTobeDownloaded--;
            checkDownload(comboData);
            return;
        }
        File file = new File(Environment.getExternalStorageDirectory().toString() + "/" + key);
        if (ConstantsUtil.checkFileExist(file.getAbsolutePath())) {
            Log.e(LOGTAG, " already downloaded : " + key);
            comboData.countTobeDownloaded--;
            checkDownload(comboData);
            return;
        }
        Log.d(LOGTAG, "combo :" + comboData.getCombo_ID() + "  to be downloaded. file:  " + key);
        if (transferUtility == null) {
            Log.d(LOGTAG, " transferUtility is null. file:  " + key);
            return;
        }
        TransferObserver observer = transferUtility.download(ConstantsUtil.AWSBucketName, key, file);
        Log.d(LOGTAG, " test " + key);
        observer.setTransferListener(dListener);
        observers.add(observer);
        dListener.comboData.hmDownloadTrack.put(observer.getId(), key);
    }


    private class DownloadListener implements TransferListener {
        public DownloadListener() {
        }

        private ComboData comboData = null;
        private String key = "";

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public DownloadListener(ComboData comboData) {
            this.comboData = comboData;
        }

        public ComboData getComboData() {
            return comboData;
        }

        public void setComboData(ComboData comboData) {
            this.comboData = comboData;
        }

        @Override
        public void onError(int id, Exception e) {
            String key = comboData.hmDownloadTrack.get(id);
            Log.e(LOGTAG, e.getStackTrace() + " Error during download  :" + key);
            downloadComboSkuFailed(comboData);
        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

        }

        @Override
        public void onStateChanged(int id, TransferState state) {
            if (state == TransferState.FAILED) {
                String key = comboData.hmDownloadTrack.get(id);
                Log.e(LOGTAG, "combo :" + comboData.getCombo_ID() + " Download failed :" + key);
                //comboData.countDownloadFailed++;
                downloadComboSkuFailed(comboData);
            } else if (state == TransferState.COMPLETED) {

                String key = comboData.hmDownloadTrack.get(id);
                if (key != null) {
                    Log.e(LOGTAG, "combo :" + comboData.getCombo_ID() + " ***** Download successful  *****  :" + key);
                    if (key.equals("inkarne/users/618/faces/1/legs/FL3_FB001.png")) {
                        Log.d(LOGTAG, "***********  key ");
                    }
                    comboData.countTobeDownloaded--;
                    comboData.hmDownloadTrack.remove(id);
                }
                checkDownload(comboData);
            }
        }
    }
*//*

    private void checkDownload(final ComboData comboData) {
        if (comboData.countTobeDownloaded == 0) {//TODO
            Log.e(LOGTAG, " -- Downloaded ComboId :" + comboData.getCombo_ID());
            comboData.setIsDisplayReady(1);
            dataSource.create(comboData);
            ComboData comboDataFromDB = dataSource.getComboDataByComboID(comboData.getCombo_ID());
            comboDataFromDB.indexTemp = comboData.indexTemp;
            downloadComboSkuCompleted(comboData);
        }

//        Handler handler = new Handler();
//        Runnable r = new Runnable() {
//            public void run() {
//                Log.d(LOGTAG," downloading ComboId :"+comboData.getCombo_ID()+"   count:"+comboData.countTobeDownloaded);
//                if (comboData.countTobeDownloaded == 0){//TODO
//                    comboData.hmDownloadTrack.clear();
//                    Log.e(LOGTAG, "Downloaded ComboId :" + comboData.getCombo_ID());
//                    comboData.setIsDisplayReady(1);
//                     dataSource.create(comboData);
//                    ComboData comboDataFromDB = dataSource.getComboDataByComboID(comboData.getCombo_ID());
//                    comboDataFromDB.indexTemp =comboData.indexTemp;
//                    downloadComboSkuCompleted(comboData);
//                }
//            }
//        };
//        handler.postDelayed(r, 50);
    }

    //Override
    protected void downloadComboSkuCompleted(ComboData comboData) {

    }

    //Override
    protected void downloadComboSkuFailed(ComboData comboData) {
        removeTransferListener();
    }

    public void nextBtnClickHandler(View v) {

    }
*/


}
