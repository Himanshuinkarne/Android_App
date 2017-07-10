package com.svc.sml.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.svc.sml.Database.ComboDataReconcile;
import com.svc.sml.Database.User;
import com.svc.sml.Fragments.BMFragment;
import com.svc.sml.Helper.AssetDownloader;
import com.svc.sml.Helper.DataManager;
import com.svc.sml.InkarneAppContext;
import com.svc.sml.Model.BaseAccessoryItem;
import com.svc.sml.Model.BaseItem;
import com.svc.sml.Model.FaceItem;
import com.svc.sml.Model.LookBoardItem;
import com.svc.sml.R;
import com.svc.sml.Utility.ConstantsUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class BodyMeasurementActivity extends BaseActivity implements BMFragment.OnFragmentInteractionListener {
    public static final String LOGTAG = "BodyMeasurementActivity";
    private Button btnNext;
    private BMFragment bmFragment;
    private FaceItem faceItem;
    private boolean nextBtnClicked = false;

    private ProgressDialog progressDialog;
    private int countRetryReconcileCombo = 0;
    private int countRetryGetBody = 0;
    private int countRetryReconcileLookBoard = 0;

    private long countCombo = 0;
    private long countLookBoard = 0;
    private boolean isComboReconciled = false;
    private boolean isLookBoardReconciled = true; //false; //change
    private boolean isBodyDownloaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_measurement);
        btnNext = (Button) findViewById(R.id.btn_shared_next);
        btnNext.setText("Next");
        verifyStoragePermissions(this);
        bmFragment = (BMFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentBM);
        faceItem = getDefaultFaceItem();
        getIntentVar();
        GATrackActivity(LOGTAG);
        Log.e(LOGTAG, "onCreate");
    }

    @Override
    protected void storagePermissionVerified() {
        super.storagePermissionVerified();
        //if(User.getInstance()!=null && User.getInstance().getPBId() != null&& !User.getInstance().getPBId().isEmpty()) {
            if(User.getInstance()!=null) {
                deleteOldFolder();
            }
       // }
    }

    private void deleteOldFolder() {
        SharedPreferences sh = getSharedPreferences("inkarne", MODE_PRIVATE);
        boolean isOldFileDeletedOnReinstall = sh.getBoolean("isOldFileDeletedOnReinstall", false);
        if (!isOldFileDeletedOnReinstall) {
            File inkarneDir = new File(ConstantsUtil.FILE_PATH_APP_ROOT + "inkarne");
            Log.e(LOGTAG, "deleteOldFolderOnInstall : " + inkarneDir.toString());
            ConstantsUtil.deleteDirectory(inkarneDir);
            sh.edit().putBoolean("isOldFileDeletedOnReinstall", true).commit();
        }
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
                //Toast.makeText(DataActivity.this, "Please restart the app ,some internal error has occurred", Toast.LENGTH_SHORT).show();
                //finish();
            }
        } else {
            Log.e(LOGTAG, "User null");
            //Toast.makeText(BodyMeasurementActivity.this, "Please restart the app ,some internal error has occurred", Toast.LENGTH_SHORT).show();
            finish();
        }
        return faceItem;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        bmFragment.onWindowFocusChanged(hasFocus);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        nextBtnClicked = false;
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    private void getIntentVar() {
//        awsKeyPathPicUpload = getIntent().getStringExtra(AdjustPicActivity.EXTRA_PARAM_PIC_AWS_KEY_PATH);
//        picSource = getIntent().getStringExtra(FaceSelectionActivity.EXTRA_PARAM_PIC_SOURCE_TYPE);
//        feducialPath = getIntent().getStringExtra(FiducialActivity2.EXTRA_PARAM_FEDUCIAL_PATH);
    }

    public void nextBtnClickHandler(View v) {
        nextBtnClicked = true;
        updateUserBM(bmFragment.bmModel);
        if (progressDialog == null)
            progressDialog = getProgressDialogTranslucent();
        progressDialog.setTitle(getString(R.string.message_loading_bm_response));
        progressDialog.setMessage(getString(R.string.message_wait_face_download));
        if (!isFinishing())
            progressDialog.show();
        getBodyData();
    }

    private void checkLaunchReady_old() { //changed 28Mar
        if ((isLookBoardReconciled || countLookBoard > 0) && (isComboReconciled || countCombo > 0) && nextBtnClicked && isBodyDownloaded) {
            InkarneAppContext.loadDataIfNotLoadedAsync();
            if(progressDialog != null)
                progressDialog.dismiss();
            launchDataActivity();
        }
    }

    private void checkLaunchReady() {
        if ((isComboReconciled || countCombo > 0) && nextBtnClicked && isBodyDownloaded) {
            InkarneAppContext.loadDataIfNotLoadedAsync();
            if(progressDialog != null)
                progressDialog.dismiss();
            launchDataActivity();
        }
    }

    public void launchDataActivity() {
        Log.e(LOGTAG, " ******** Launch DataActivity in BodyMeasurementActivity  *******");
        Intent intent = new Intent(BodyMeasurementActivity.this, DataActivity.class);
        startActivity(intent);
        finish();
    }

    public void updateUserBM(BMFragment.BMModel bmModel) {
        User user = User.getInstance();
        user.setBust(bmModel.bustSize);
        user.setHip(bmModel.hipsSize);
        user.setWaist(bmModel.waistSize);
        user.setHeight(bmModel.heightBM);
        user.setWeight(bmModel.weightBM);
        dataSource.create(user);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void getInitialData() {
        //reconcileLookBoard();//new change 27 March
        requestReconcileComboData();
    }

    protected void checkBodyDownload(final FaceItem item) {
        new AssetDownloader(BodyMeasurementActivity.this).downloadAsset(item.getBodyObjkey(), item.getObjBodyDStatus(), item.getBodyPngkey(), item.getTextureBodyDStatus(), new OnAssetDownloadListener() {
            @Override
            public void onDownload(BaseAccessoryItem item) {
                Log.d(LOGTAG, " downloaded body obj  " + item.getObjAwsKey() + " and texture obj " + item.getTextureAwsKey());
                faceItem.setObjBodyDStatus(ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus());
                faceItem.setTextureBodyDStatus(ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus());
                faceItem.setBodyObjkey(item.getObjAwsKey());
                dataSource.create(faceItem);
                isBodyDownloaded = true;
                User.getInstance().setDefaultFaceItem(faceItem);
                checkLaunchReady();
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

    public void getBodyData() {
        faceItem = getDefaultFaceItem();
        if (faceItem.getPbId() == null || faceItem.getPbId().isEmpty() || User.getInstance().getPBId() == null
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
                    BaseItem item = (BaseItem) obj;
                    faceItem.setPbId(item.getObjId());
                    faceItem.setBodyObjkey(item.getObjAwsKey());
                    faceItem.setBodyPngkey(item.getTextureAwsKey());
                    User.getInstance().setPBId(item.getObjId());
                    dataSource.create(User.getInstance());
                    dataSource.create(faceItem);
                    checkBodyDownload(faceItem);
                    getInitialData();
                }

                @Override
                public void onResponseError(String errorMessage, int errorCode) {
                    countRetryGetBody++;
                    if (countRetryGetBody < 100)
                        if (errorCode == DataManager.CODE_DATA_MANAGER_NETWORK_ERROR) {
                            getBodyData();
                            if (countRetryGetBody == 1)
                                Toast.makeText(getApplicationContext(), getString(R.string.message_network_download_title), Toast.LENGTH_SHORT).show();

                        } else {
                            if (countRetryGetBody == 2)
                                Toast.makeText(getApplicationContext(), getString(R.string.message_error_generic), Toast.LENGTH_SHORT).show();

                            if (countRetryGetBody < ConstantsUtil.COUNT_RETRY_SERVICE_CRITICAL)
                                getBodyData();
                        }
                }
            });
        } else {
            checkBodyDownload(faceItem);
            getInitialData();
        }
    }

    private void reconcileLookBoard() {
       // String uri = "http://ec2-52-77-8-232.ap-southeast-1.compute.amazonaws.com/svc/v1/campaigns/?category=party&gender=m";
        //String uri = "http://ec2-52-77-8-232.ap-southeast-1.compute.amazonaws.com/svc/v1/campaigns/?gender=" + User.getInstance().getmGender();
        String uri = ConstantsUtil.URL_BASEPATH_0+ConstantsUtil.URL_METHOD_RECONCILE_CAMPAIGN+User.getInstance().getmUserId()+"/"+User.getInstance().getmGender();
        DataManager.getInstance().requestLookBoard(uri, new DataManager.OnResponseHandlerInterface() {
            @Override
            public void onResponse(Object obj) {
                ArrayList<LookBoardItem> listLookBoard = (ArrayList<LookBoardItem>) obj;
                isLookBoardReconciled = true;
                checkLaunchReady();
            }

            @Override
            public void onResponseError(String errorMessage, int errorCode) {
                countRetryReconcileLookBoard++;
                checkLaunchReady();
                if (!getLookBoardCount()) {
                    reconcileLookBoard();
                    if (errorCode == DataManager.CODE_DATA_MANAGER_NETWORK_ERROR) {
                        if (countRetryReconcileLookBoard == 10) {
                            Toast.makeText(getApplicationContext(), getString(R.string.message_network_download_title), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (countRetryReconcileLookBoard == 11) {
                            Toast.makeText(getApplicationContext(), getString(R.string.message_server_other_failure), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else if (countRetryReconcileLookBoard < 2) {
                    reconcileLookBoard();
                }
            }
        });
    }


    private void requestReconcileComboData() {
        String uri = ConstantsUtil.URL_BASEPATH + ConstantsUtil.URL_METHOD_RECONCILE + User.getInstance().getmUserId() + "/" + User.getInstance().getPBId();
        DataManager.getInstance().requestReconcileComboData(uri, new DataManager.OnResponseHandlerInterface() {
            @Override
            public void onResponse(Object obj) {
                List<ComboDataReconcile> arrayListComboReconcile = (List<ComboDataReconcile>) obj;

                for (ComboDataReconcile reComboData : arrayListComboReconcile) {
                    if (reComboData.getCombo_ID() != null) {
                        Log.d(LOGTAG, "requestReconcileComboData - comboId =" + reComboData.getCombo_ID().toString());
                        dataSource.createReconcile(reComboData);
                    }
                }
                isComboReconciled = true;
                checkLaunchReady();
            }

            @Override
            public void onResponseError(String errorMessage, int errorCode) {
                countRetryReconcileCombo++;
                checkLaunchReady();
                if (!getComboCount()) {
                    requestReconcileComboData();
                    if (errorCode == DataManager.CODE_DATA_MANAGER_NETWORK_ERROR) {
                        if (countRetryReconcileCombo == 1) {
                            Toast.makeText(getApplicationContext(), getString(R.string.message_network_download_title), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (countRetryReconcileCombo < ConstantsUtil.COUNT_RETRY_SERVICE_CRITICAL) {
                            if (countRetryReconcileCombo == 2) {
                                Toast.makeText(getApplicationContext(), getString(R.string.message_server_other_failure), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } else if (countRetryReconcileCombo < 2) {
                    requestReconcileComboData();
                }
            }
        });
    }

    private boolean getComboCount() {
        countCombo = dataSource.getComboCount();
        return countCombo != 0;
    }

    private boolean getLookBoardCount() {
        countLookBoard = dataSource.getLooBoardCount();
        return countLookBoard != 0;
    }

//    private void startFaceItemDownload() {
//        isDownloadFailed = false;
//        BaseAccessoryItem face = new BaseAccessoryItem(faceItem.getFaceId(), faceItem.getFaceObjkey(), faceItem.getObjFaceDStatus(), faceItem.getFacePngkey(), faceItem.getTextureFaceDStatus());
//        if (faceItem.getHairObjkey() != null && !faceItem.getHairObjkey().isEmpty() && faceItem.getHairPngKey() != null && !faceItem.getHairPngKey().isEmpty()) {
//            face.dependentItem = new BaseAccessoryItem(faceItem.getHairstyleId(), faceItem.getHairObjkey(), faceItem.getObjHairDStatus(), faceItem.getHairPngKey(), faceItem.getTextureHairDStatus());
//        }
//        new AssetDownloader(this).downloadAsset(face, new OnAssetDownloadListener() {
//            @Override
//            public void onDownload(BaseAccessoryItem item) {
//                faceItem.setObjFaceDStatus(ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus());
//                faceItem.setTextureFaceDStatus(ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus());
//                if (faceItem.getHairObjkey() != null && !faceItem.getHairObjkey().isEmpty() && faceItem.getHairPngKey() != null && !faceItem.getHairPngKey().isEmpty()) {
//                    faceItem.setObjHairDStatus(ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus());
//                    faceItem.setTextureHairDStatus(ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus());
//                }
//                updateDefaultFace();
//                progressDialog.dismiss();
//                if (nextBtnClicked) {
//                    updateUserBM(bmFragment.bmModel);
//                    checkLaunchReady();
//                }
//            }
//
//            @Override
//            public void onDownloadFailed(String comboId) {
//                isDownloadFailed = true;
//            }
//
//            @Override
//            public void onDownloadProgress(String comboId, int percentage) {
//
//            }
//        });
//    }

//    private void updateDefaultFace() {
//        User.getInstance().setDefaultFaceId(faceItem.getFaceId());
//        User.getInstance().setDefaultFaceItem(faceItem);
//        InkarneAppContext.getDataSource().create(User.getInstance());
//        InkarneAppContext.getDataSource().create(faceItem);
//    }

}
