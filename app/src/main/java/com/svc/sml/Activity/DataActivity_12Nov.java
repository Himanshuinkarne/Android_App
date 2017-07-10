package com.svc.sml.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import android.widget.VideoView;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.crashlytics.android.Crashlytics;
import com.svc.sml.Database.ComboData;
import com.svc.sml.Database.ComboDataReconcile;
import com.svc.sml.Database.User;
import com.svc.sml.Helper.AssetDownloader;
import com.svc.sml.Helper.ComboDownloader;
import com.svc.sml.Helper.DataManager;
import com.svc.sml.Helper.DownloadIntentService;
import com.svc.sml.InkarneAppContext;
import com.svc.sml.Model.AppUpdateItem;
import com.svc.sml.Model.BaseAccessoryItem;
import com.svc.sml.Model.BaseItem;
import com.svc.sml.Model.FaceItem;
import com.svc.sml.R;
import com.svc.sml.ShopActivity;
import com.svc.sml.Utility.Connectivity;
import com.svc.sml.Utility.ConstantsFunctional;
import com.svc.sml.Utility.ConstantsUtil;
import com.svc.sml.View.LoadingViewData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DataActivity_12Nov extends BaseActivity {
    public static final String LOGTAG = "DataActivity";
    protected final static int COUNT_COMBO_DOWNLOADS = 3;
    protected final static int COUNT_COMBO_DOWNLOADS_UNSEEN = 1;
    protected boolean isReconcileDataUpdated = false;
    protected boolean isComboSkuDownloaded = false;
    protected boolean isBodyDownloaded = false;
    protected boolean isFaceDownloaded = false;
    protected FaceItem faceItem;
    protected boolean isLaunched = false;
    private ArrayList<TransferObserver> observers = new ArrayList<TransferObserver>();
    protected ArrayList<ComboData> comboList;
    protected int retryCountReconcile = 0;
    protected int retryCountGetBody = 0;
    protected ComboData receivedComboData = null;
    private ComboData comboDataPassed = null;
    private boolean isPBChanged = false;
    private int comboIndex = 0;
    private LoadingViewData loadingView;
    private VideoView video;
    private int countComboFailed = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        video = (VideoView) findViewById(R.id.vv_info_face_selection);
        loadingView = (LoadingViewData) findViewById(R.id.loading_view_data);
        loadingView.setOnLoadingViewInteractionListener(new LoadingViewData.OnLoadingViewInteractionListener() {
            @Override
            public void onCancel() {
                if (loadingView.getBtnCancel().getText().toString().equals("Start")) {
                    loadingView.getBtnCancel().setText("Stop");
                    video.start();
                    if (loadingView.getPbData().getProgress() == 0) {
                        loadingView.setProgressIndeterminate(true);
                        restartDataLoad(false);
                    } else
                        restartDataLoad(true);
                } else {
                    loadingView.getBtnCancel().setText("Start");
                    if (video.isPlaying())
                        video.pause();
                    /*Stop progress bar aimation*/
                    if (loadingView.getPbData().getProgress() == 0)
                        loadingView.setProgressIndeterminate(false);
                }
            }
        });
        loadingView.setLoadingText(getString(R.string.message_loading_sku_data_activity));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingView.showCancelButton();
            }
        }, ConstantsFunctional.TIME_WAIT_TO_CANCEL_TO_GET_LAUNCH_DATA_IN_MILLI);
        //verifyStoragePermissions(this);
        //InkarneAppContext.LogSecretKey();
        //deleteTest();
        GATrackActivity(LOGTAG);
        verifyStoragePermissions(this);
        checkMemory();
        checkAppVersion();
        initVideo();
        comboDataPassed = (ComboData) getIntent().getSerializableExtra("comboData");
        logUser();
    }

    private void logUser() {
        // TODO: Use the current user's information
        // You can call any combination of these three methods
        Crashlytics.setUserIdentifier(User.getInstance().getmUserId());
        Crashlytics.setUserEmail(User.getInstance().getEmailId());
        Crashlytics.setUserName(User.getInstance().getmFirstName());
    }


    private void checkMemory() {
        Intent intent = new Intent(DataActivity_12Nov.this, DownloadIntentService.class);
        intent.setAction(DownloadIntentService.ACTION_DOWNLOAD_MEMORY_CLEANUP);
        startService(intent);
    }

    private void deleteTest() {
        //File inkarneDir = new File(ConstantsUtil.FILE_PATH_APP_ROOT_VIDEO + ".chartboost");
        //deleteDirectory(inkarneDir);
        File inkarneDir2 = new File(ConstantsUtil.FILE_PATH_APP_ROOT + "inkarne/pbMastersku/FB020");
        ConstantsUtil.deleteDirectory(inkarneDir2);
    }

    private void checkAppVersion() {
//        if (InkarneAppContext.isFirstTimeComboRender()) {
//            return;
//        }
        String versionName = null;
        int versionCode1 = -1;
        try {
            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            versionCode1 = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
         final int versionCode = versionCode1;
        String url = ConstantsUtil.URL_APP_VERSION_UPDATE;
        DataManager.getInstance().requestCodeVersion(url, new DataManager.OnResponseHandlerInterface() {
            @Override
            public void onResponse(Object obj) {
                AppUpdateItem item = (AppUpdateItem) obj;
                boolean versionUpdate;
                if (item != null && item.getAppVersionCode() > versionCode) {
                    loadingView.setVisibility(View.INVISIBLE);
                    if (item.isForceUpdate()) {
                        showAlertForceUpdate(item.getDownloadLink());
                    } else {
                        showAlertRecommendUpdate(item.getDownloadLink());
                    }
                } else {
                    getRequiredData();
                }
            }

            @Override
            public void onResponseError(String errorMessage, int errorCode) {
                Log.w(LOGTAG, "requestCodeVersion failed ");
                getRequiredData();
            }
        });
    }

    private void showAlertForceUpdate(final String link) {

        AlertDialog.Builder builder = new AlertDialog.Builder(DataActivity_12Nov.this);
        builder.setTitle("Please update your app")
                .setMessage("")
                .setCancelable(false);
        //.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setNegativeButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                openPlayStore(link);
                finish();

            }
        }).create();
        if (!isFinishing())
            builder.show();
    }

    private void showAlertRecommendUpdate(final String link) {
        AlertDialog.Builder builder = new AlertDialog.Builder(DataActivity_12Nov.this);
        builder.setTitle("It is recommended to update the app")
                .setMessage("")
                .setCancelable(false);
        //.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setNegativeButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                getRequiredData();
            }
        });
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                openPlayStore(link);
                finish();

            }
        }).create();
        if (!isFinishing())
            builder.show();

    }

    private void openPlayStore(String link){
        try {
            Intent viewIntent =
                    new Intent("android.intent.action.VIEW",
                            Uri.parse(link));
            startActivity(viewIntent);
        }catch(Exception e) {
            Toast.makeText(getApplicationContext(),"Unable to Connect Try Again...",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void initVideo() {
        //video.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.inkarne_loading);
        video.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.v_face_selection_light_instruction_loop_female);
        video.start();
        video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                //launchShopActivity();//TODO
            }
        });
        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
        video.start();
        video.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        isLaunched = false;
        video.start();
        super.onResume();
        //forceCrash();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private FaceItem getDefaultFaceItem() {
        User user = User.getInstance();
        if (user != null) {
            faceItem = user.getDefaultFaceItem();
            if (faceItem == null) {
                faceItem = dataSource.getAvatar(user.getDefaultFaceId());
                user.setDefaultFaceItem(faceItem);
            }
            if (faceItem == null) {
                Log.e(LOGTAG, "faceItem null");
                FaceItem face = new FaceItem();
                face.setFaceId("1");
                faceItem = face;
                user.setDefaultFaceItem(faceItem);
                user.setDefaultFaceId(faceItem.getFaceId());
                dataSource.create(faceItem);
                dataSource.create(user);
                //Toast.makeText(DataActivity.this, "Please restart the app ,some internal error has occurred", Toast.LENGTH_SHORT).show();
                //finish();
            }
        } else {
            Log.e(LOGTAG, "User null");
            //Toast.makeText(DataActivity.this, "Please restart the app ,some internal error has occurred", Toast.LENGTH_SHORT).show();
            //finish();
        }
        return faceItem;
    }

    public void getRequiredData() {
        //checkMemory();
        loadingView.setVisibility(View.VISIBLE);
        isLaunched = false;
        faceItem = getDefaultFaceItem();
        if (!faceItem.getFaceId().equals("1")) {
            checkFaceDownload();
        } else {
            isFaceDownloaded = true;
        }
        if (/*faceItem.getBodyObjkey()== null ||faceItem.getBodyObjkey().isEmpty()
                || faceItem.getBodyPngkey()== null ||faceItem.getBodyPngkey().isEmpty()*/
                faceItem.getPbId() == null || faceItem.getPbId().isEmpty() || User.getInstance().getPBId() == null
                        || !User.getInstance().getPBId().equals(faceItem.getPbId())) {
            User user = User.getInstance();
            String legFlag = "1";
            String uri = ConstantsUtil.URL_BASEPATH_CREATE_V2 + ConstantsUtil.URL_METHOD_CREATE_BODY
                    + user.getmUserId() + "/"
                    + user.getmGender() + "/"
                    + user.getDefaultFaceItem().getFaceId() + "/"
                    + user.getHeight() + "/" + user.getWeight() + "/" + user.getBust() + "/" + user.getWaist() + "/" + user.getHip() + "/"
                    + legFlag;
            DataManager.getInstance().requestCreateBody(uri, new DataManager.OnResponseHandlerInterface() {
                @Override
                public void onResponse(Object obj) {
                    retryCountGetBody = 0;
                    BaseItem item = (BaseItem) obj;
                    if (User.getInstance().getPBId() == null) {
                        isPBChanged = true;
                    } else if (!User.getInstance().getPBId().equals(item.getObjId())) {
                        deleteForPBUpdate(User.getInstance().getPBId(), faceItem);
                        isPBChanged = true;
                    }

                    faceItem.setPbId(item.getObjId());
                    if (item.getObjAwsKey() != null && item.getObjAwsKey().length() != 0) {
                        faceItem.setBodyObjkey(item.getObjAwsKey());
                    }
                    if (item.getTextureAwsKey() != null && item.getTextureAwsKey().length() != 0) {
                        faceItem.setBodyPngkey(item.getTextureAwsKey());
                    }
                    User.getInstance().setPBId(item.getObjId());
                    dataSource.create(User.getInstance());
                    dataSource.create(faceItem);
                    checkBodyDownload(faceItem);
                    getInitialData();
                }

                @Override
                public void onResponseError(String errorMessage, int errorCode) {
                    retryCountGetBody++;
                    if (retryCountGetBody < 100)
                        if (errorCode == DataManager.CODE_DATA_MANAGER_NETWORK_ERROR) {
                            getRequiredData();
                            if (retryCountReconcile == 1)
                                Toast.makeText(DataActivity_12Nov.this, getString(R.string.message_network_download_title), Toast.LENGTH_SHORT).show();

                        } else {
                            if (retryCountReconcile == 2)
                                Toast.makeText(DataActivity_12Nov.this, getString(R.string.message_error_generic), Toast.LENGTH_SHORT).show();

                            if (retryCountGetBody < ConstantsUtil.COUNT_RETRY_SERVICE_CRITICAL)
                                getRequiredData();
                        }
                }
            });
        } else {
            checkBodyDownload(faceItem);
            getInitialData();
        }
    }

    public void deleteForPBUpdate(final String pbId, final FaceItem faceItem) {
        //Combos, shoes,
        dataSource.deleteComboDetailForPBChange(pbId);
        dataSource.deleteComboDetailLikeForPBChange(pbId);
        File inkarnePBDir = new File(ConstantsUtil.FILE_PATH_APP_ROOT+"inkarne/pbsku/" + pbId);
        Log.e(LOGTAG, "deleted: " + inkarnePBDir.getAbsolutePath());
        //ConstantsUtil.deleteDirectory(inkarnePBDir, ".ply");
        ConstantsUtil.deleteDirectory(inkarnePBDir);
        dataSource.deleteFaceBodyAccessoriesPBChange(faceItem.getFaceId(), pbId);
        dataSource.deleteBodyAccessoriesPBChange(pbId);


//        ArrayList<FaceItem> listFaceItem = (ArrayList<FaceItem>) InkarneAppContext.getDataSource().getAvatars();
//        for (FaceItem face : listFaceItem) {
//            if (!face.getPbId().equals(faceItem.getPbId())) {
//                File inkarneBody = new File(ConstantsUtil.FILE_PATH_APP_ROOT, "inkarne/users/" + User.getInstance().getmUserId() + "/" + "faces/" + face.getFaceId() + "/bodies");
//                File inkarneLegs = new File(ConstantsUtil.FILE_PATH_APP_ROOT, "inkarne/users/" + User.getInstance().getmUserId() + "/" + "faces/" + face.getFaceId() + "/legs");
//                ConstantsUtil.deleteDirectory(inkarneBody,".ply");
//                ConstantsUtil.deleteDirectory(inkarneLegs,".ply");
//                face.setBodyObjkey("");
//                face.setBodyPngkey("");
//                //face.setPbId(faceItem.getPbId());
//                dataSource.create(face);
//                dataSource.deleteFaceBodyAccessories(face.getFaceId(), face.getPbId());
//            }
//        }
    }

    public void getReconcileBodyAccessories() {
        //TODO
        Intent intent = new Intent(DataActivity_12Nov.this, DownloadIntentService.class);
        intent.setAction(DownloadIntentService.ACTION_DOWNLOAD_RECONCILE_BODY_ACC);
        startService(intent);
    }

    public void getReconcileFaceAccessories() {
        Intent intent = new Intent(DataActivity_12Nov.this, DownloadIntentService.class);
        intent.setAction(DownloadIntentService.ACTION_DOWNLOAD_RECONCILE_FACE_ACC);
        startService(intent);
    }

    public void getInitialData() {
        //getReconcileBodyAccessories();
        //getReconcileFaceAccessories();
        requestReconcileComboData();
        getDisplayComboData();
    }

    private void requestReconcileComboData() {
        InkarneAppContext.shouldRearrangeLooks = true;
        String uri = ConstantsUtil.URL_BASEPATH + ConstantsUtil.URL_METHOD_RECONCILE + User.getInstance().getmUserId() + "/" + User.getInstance().getPBId();
        //Log.d(LOGTAG, uri);
        DataManager.getInstance().requestReconcileComboData(uri, new DataManager.OnResponseHandlerInterface() {
            @Override
            public void onResponse(Object obj) {
                //ComboDataWrapper.Reconcile reconcile = (ComboDataWrapper.Reconcile)obj;
                List<ComboDataReconcile> arrayListComboReconcile = (List<ComboDataReconcile>) obj;
                for (ComboDataReconcile reComboData : arrayListComboReconcile) {
                    if (reComboData.getCombo_ID() != null) {
                        Log.d(LOGTAG, "requestReconcileComboData - comboId =" + reComboData.getCombo_ID().toString());
                        dataSource.createReconcile(reComboData);
                    }
                }
                if (comboList == null || comboList.size() == 0) {
                    getDisplayComboData();
                }
            }

            @Override
            public void onResponseError(String errorMessage, int errorCode) {
                retryCountReconcile++;
                if (comboList == null || comboList.size() == 0) {
                    if (errorCode == DataManager.CODE_DATA_MANAGER_NETWORK_ERROR) {
                        requestReconcileComboData();
                        if (retryCountReconcile == 1) {
                            Toast.makeText(DataActivity_12Nov.this, getString(R.string.message_network_download_title), Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        if (retryCountReconcile < ConstantsUtil.COUNT_RETRY_SERVICE_CRITICAL) {
                            requestReconcileComboData();
                            if (retryCountReconcile == 2) {
                                Toast.makeText(DataActivity_12Nov.this, getString(R.string.message_server_other_failure), Toast.LENGTH_SHORT).show();

                            }
                        }
                    }
                }
            }
        });
    }

    private void getDisplayComboData() {
        String passedComboId = getIntent().getStringExtra("comboDataId");
        if (passedComboId != null) {
            ComboData comboData = dataSource.getComboDataByComboID(passedComboId);
            if (comboList == null)
                comboList = new ArrayList<>();
            comboList.add(comboData);
            comboIndex = 0;
            loadCombo(comboData);
            return;
        }
        if (Connectivity.isConnected(DataActivity_12Nov.this)) {
            comboList = (ArrayList<ComboData>) dataSource.getComboDataLaunchUnseen(COUNT_COMBO_DOWNLOADS_UNSEEN);
            //comboList = (ArrayList<ComboData>) dataSource.getComboDataLaunchSeen(COUNT_COMBO_DOWNLOADS_UNSEEN);
            if (comboList == null || comboList.size() == 0) {//TODO
                ArrayList<ComboData> comboListSeen = (ArrayList<ComboData>) dataSource.getComboDataLaunchSeen(COUNT_COMBO_DOWNLOADS);
                comboList.addAll(comboListSeen);
            } else {
                Log.e(LOGTAG, "getDisplayComboData " + "  size seen:" + comboList.size());
            }
        } else {
            comboList = (ArrayList<ComboData>) dataSource.getComboDataLaunchSeen(COUNT_COMBO_DOWNLOADS);
            if (comboList != null) {
                Log.e(LOGTAG, "getDisplayComboData " + "  size seen:" + comboList.size());
            }
        }
        Log.e(LOGTAG, "getDisplayComboData " + "  size :" + comboList.size());
        if (comboList != null && comboList.size() != 0) {
            if (comboIndex < comboList.size()) {
                loadCombo(comboList.get(comboIndex));
            } else {
                loadCombo(comboList.get(comboList.size() - 1));
            }
        } else {
            Log.e(LOGTAG, "No Combo found.");
            //Toast.makeText(DataActivity.this, "Please restart the app ,some internal error has occurred", Toast.LENGTH_SHORT).show();
        }
    }

    public void restartDataLoad(boolean onlyCombo) {
        if (onlyCombo) {
            comboIndex++;
            if (comboList != null && comboIndex < comboList.size()) {
                loadCombo(comboList.get(comboIndex));
            } else {
                ArrayList<ComboData> comboListSeen = (ArrayList<ComboData>) dataSource.getComboDataLaunchSeen(COUNT_COMBO_DOWNLOADS);
                comboList.addAll(comboListSeen);
                if (comboIndex < comboList.size()) {
                    loadCombo(comboList.get(comboIndex));
                }
            }
        } else {
            getRequiredData();
        }
    }

    /* check and download ComboDetail */
    protected void loadCombo(final ComboData combodata) {
        loadingView.setProgress(0);
        loadingView.setProgressIndeterminate(true);
        new ComboDownloader(DataActivity_12Nov.this, combodata, new ComboDownloader.OnComboDownloadListener() {
            @Override
            public void onDownload(ComboData comboData) {
                Log.e(LOGTAG, "downloadComboSkuCompleted");
                receivedComboData = comboData;
                isComboSkuDownloaded = true;
                checkLaunchShopActivity();
            }

            @Override
            public void onDownloadFailed(String comboId) {
                countComboFailed++;
                if(countComboFailed < 5) {
                    Toast.makeText(DataActivity_12Nov.this, getString(R.string.message_error_generic), Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(DataActivity_12Nov.this, getString(R.string.message_network_failure), Toast.LENGTH_SHORT).show();
                }
                comboIndex++;
                restartDataLoad(true);
            }

            @Override
            public void onDownloadProgress(String comboId, int percentage) {
                if (percentage >= 82) { //TODO hack
                    //percentage -= new Random().nextInt(1);
                    //loadingView.setProgress(percentage);
                    animateProgressIncrement();
                } else
                    loadingView.setProgress(percentage);
            }

            @Override
            public void onComboInfoFailed(String comboId, int error_code) {
                countComboFailed++;
                if(countComboFailed < 5) {
                    if (error_code == DataManager.CODE_DATA_MANAGER_NETWORK_ERROR) {
                        Toast.makeText(DataActivity_12Nov.this, getString(R.string.message_network_failure), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(DataActivity_12Nov.this, getString(R.string.message_error_generic), Toast.LENGTH_SHORT).show();

                    }
                }
            }

            @Override
            public void onComboInfoResponse(String comboId) {
                loadingView.setProgressIndeterminate(false);
            }
        });
    }

    Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            loadingView.incrementProgressBy(1);
        }
    };


    private void animateProgressIncrement() {
        //progress -= 4+ new Random().nextInt(6);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (loadingView.getProgress() <= 97) {
                        Thread.sleep(500);
                        handle.sendMessage(handle.obtainMessage());
//                        if (pDialogDownload.getProgress() == pDialogDownload
//                                .getMax()) {
//                            pDialogDownload.dismiss();
//                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void checkFaceDownload() {
        //isFaceDownloaded = false;
        BaseAccessoryItem face = new BaseAccessoryItem(faceItem.getFaceId(), faceItem.getFaceObjkey(), faceItem.getObjFaceDStatus(), faceItem.getFacePngkey(), faceItem.getTextureFaceDStatus());
        face.dependentItem = new BaseAccessoryItem(faceItem.getHairstyleId(), faceItem.getHairObjkey(), faceItem.getObjHairDStatus(), faceItem.getHairPngKey(), faceItem.getTextureHairDStatus());
        new AssetDownloader(this).downloadAsset(face, new OnAssetDownloadListener() {
            @Override
            public void onDownload(BaseAccessoryItem item) {
                faceItem.setObjFaceDStatus(ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus());
                faceItem.setTextureFaceDStatus(ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus());
                if (faceItem.getHairObjkey() != null && !faceItem.getHairObjkey().isEmpty() && faceItem.getHairPngKey() != null && !faceItem.getHairPngKey().isEmpty()) {
                    faceItem.setObjHairDStatus(ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus());
                    faceItem.setFaceObjkey(item.getObjAwsKey());
                    faceItem.setHairObjkey(item.dependentItem.getObjAwsKey());
                    faceItem.setTextureHairDStatus(ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus());
                }
                isFaceDownloaded = true;
                InkarneAppContext.getDataSource().create(faceItem);
            }

            @Override
            public void onDownloadFailed(String comboId) {
                checkFaceDownload();
            }

            @Override
            public void onDownloadProgress(String comboId, int percentage) {

            }
        });
    }

    protected void checkBodyDownload(final FaceItem faceItem) {
        //isBodyDownloaded = false;
        new AssetDownloader(DataActivity_12Nov.this).downloadAsset(faceItem.getBodyObjkey(), faceItem.getObjBodyDStatus(), faceItem.getBodyPngkey(), faceItem.getTextureBodyDStatus(), new OnAssetDownloadListener() {
            @Override
            public void onDownload(BaseAccessoryItem item) {
                Log.d(LOGTAG, " downloaded body obj  " + item.getObjAwsKey() + " and texture obj " + item.getTextureAwsKey());
                isBodyDownloaded = true;
                faceItem.setObjBodyDStatus(ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus());
                faceItem.setTextureBodyDStatus(ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus());
                faceItem.setBodyObjkey(item.getObjAwsKey());
                dataSource.create(faceItem);
                checkLaunchShopActivity();
            }

            @Override
            public void onDownloadFailed(String comboId) {
                checkBodyDownload(faceItem);
            }

            @Override
            public void onDownloadProgress(String comboId, int percentage) {

            }
        });
    }

    private void updateAvatar() {
        User.getInstance().setPBId(faceItem.getPbId());
        User.getInstance().setDefaultFaceItem(faceItem);//TODO
        dataSource.create(faceItem);
        dataSource.create(User.getInstance());
    }

    protected void checkLaunchShopActivity() {
        Log.w(LOGTAG, "checkLaunchShopActivity");
        if (!isLaunched && isBodyDownloaded && isComboSkuDownloaded) {
            Log.w(LOGTAG, "shop launched");
            isLaunched = true;
            updateAvatar();
            launchShopActivity();
        }
    }

    protected synchronized void launchShopActivity() {
        memoryCleanup();
        Log.e(LOGTAG, " ******** Launch ShopActivity in DataActivity  *******");
        Intent intent = new Intent(DataActivity_12Nov.this, ShopActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Log.e(LOGTAG, "comboData passed :id " + receivedComboData.getCombo_ID());
        intent.putExtra("comboData", receivedComboData);
        if (InkarneAppContext.isDefaultFaceChanged() || isPBChanged)
            intent.putExtra("isBodyORFaceChanged", true);

        startActivity(intent);
        isComboSkuDownloaded = false;
        isPBChanged = false;
        //finish();
    }
}
