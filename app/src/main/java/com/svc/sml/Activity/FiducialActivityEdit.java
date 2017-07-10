package com.svc.sml.Activity;

//import android.app.AlertDialog;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.svc.sml.Database.User;
import com.svc.sml.Helper.AssetDownloader;
import com.svc.sml.Helper.DataManager;
import com.svc.sml.InkarneAppContext;
import com.svc.sml.Model.BaseAccessoryItem;
import com.svc.sml.Model.FaceItem;
import com.svc.sml.R;
import com.svc.sml.Utility.AWSUtil;
import com.svc.sml.Utility.ConstantsFunctional;
import com.svc.sml.Utility.ConstantsUtil;

import java.io.File;

public class FiducialActivityEdit extends FiducialActivity {
    public static final String LOGTAG = "FiducialsActivityEdit";
    public static final String EXTRA_PARAM_FEDUCIAL_PATH = "EXTRA_PARAM_FEDUCIAL_PATH";
    private boolean serverCallInProgress = false;
    private TransferUtility transferUtility;
    //private List<TransferObserver> observersDownload = new ArrayList<TransferObserver>();
    private ProgressDialog progressDialog;
    private boolean isNextActivityLaunched;
    private String feducialPath = "";
    private int retryCount = 0;
    private FaceItem faceItem;
    private String editFedPoints ="";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = getProgressDialogTranslucent();
//        if (User.getInstance().getDefaultFaceId() == null || User.getInstance().getDefaultFaceId().length() == 0) {
//            btnNextHandler();
//        }
        if (transferUtility == null)
            transferUtility = AWSUtil.getTransferUtility(this);
        //faceItem = (FaceItem) getIntent().getSerializableExtra("faceItem");
    }

    public void onResume() {
        super.onResume();
        isNextActivityLaunched = false;
        btnNext.setText("Next");
        btnNext.setEnabled(true);
//        if(User.getInstance().getDefaultFaceId() == null || User.getInstance().getDefaultFaceId().length() ==0 ){
//            btnNextHandler();
//        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
        System.gc();
    }

    @Override
    public void onStop() {
        super.onStop();
        isNextActivityLaunched = false;
        if (progressDialog != null)
            progressDialog.dismiss();
    }


    public ProgressDialog getProgressDialogTranslucent() {
        ProgressDialog p = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
        p.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        p.setCanceledOnTouchOutside(false);
        p.setCancelable(true);
        return p;
    }

    @Override
    public void onBackPressed() {
        if (progressDialog != null)
            progressDialog.dismiss();
        btnNext.setEnabled(true);
        btnNext.setText("Next");
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (progressDialog != null)
                progressDialog.dismiss();
            btnNext.setEnabled(true);
            btnNext.setText("Next");
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected int[] getFedPointsIndexToShow() {
        //int[] fedPoints = new int[]{2, 3, 4, 5, 6, 7};
        int[] fedPoints = new int[]{0, 1, 2, 3, 8, 9, 10};
        return fedPoints;
    }

    @Override
    protected boolean isExternalFiducialFeature() {
        return true;
    }


    protected void initFaceItem() {
        FaceItem faceItem = (FaceItem) getIntent().getSerializableExtra("faceItem");
        editFedPoints = faceItem.getFeducialPoints();
        uFilePathPic = faceItem.getImageSavedFilePath();
        picSource = "0";
        String filename = new File(uFilePathPic).getName();
        awsKeyPathPicUpload = ConstantsUtil.FILE_PATH_AWS_KEY_ROOT + User.getInstance().getmUserId() + "/faces/" + filename;
        String ratio = faceItem.getImageResizeRatio();
        ratioImageResize = Float.parseFloat(ratio);
    }

    protected void getUserFedPoints() {
        String fed = editFedPoints;
        if(editFedPoints.startsWith("/")){
            fed = editFedPoints.substring(1,editFedPoints.length());
        }
        String[] split = fed.split("/");
        int j = 0;
        int k = 0;
        for(int i =0 ; i<split.length; i++){
            if(i%2 == 0){
                //uheight[j] = Float.parseFloat(uRows[j]) ;
                //uwidth[j] = Float.parseFloat(uColumns[j]);
                uwidth[i/2] = Integer.parseInt(split[i])/ratioImageResize;
            }else{
                uheight[i/2] = Integer.parseInt(split[i])/ratioImageResize;
            }
        }
        drawUserFiducialPoints();
    }

    protected synchronized void createFiducialPoints() {
        feducialPath = "";
        for (int f = 0; f < 11; f++) {
            int w = (int) (uwidth[f] * ratioImageResize);
            int h = (int) (uheight[f] * ratioImageResize);
            feducialPath += "/" + w + "/";
            feducialPath += h;
        }
        Log.d(LOGTAG, "feducialPath1 :" + feducialPath);
    }

    @Override
    protected void btnNextHandler() {
        if (uheight.length == 0 || uheight[0] == 0) {
            getUserFedPoints();
            return;
        }
        for (int i = 0; i < arrayFedPointMoved.length; i++) {
            if (arrayFedPointMoved[i] == 0) {
                showAlertPointMovedError("Please fine-tune all red points", "");
                return;
            }
        }
        createFiducialPoints();
        getFaceItem();
    }

    protected void showAlertPointMovedError(String title, String msg) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(msg)
                .setCancelable(false)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                    }
                }).create();
        if (!isFinishing())
            builder.show();
    }


    public void getFaceItem() {
        if (faceItem == null)
            requestFaceObj();
        else {
            startFaceItemDownload();
        }
    }

    protected void showAlertFaceCreateError(String title, String msg) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(msg)
                .setCancelable(false)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        fedPointsIndexToShow = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
                        arrayFedPointMoved = new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
                        drawUserFiducialPoints();
                    }
                }).create();
        if (!isFinishing())
            builder.show();
    }

    public void requestFaceObj() {
        if (progressDialog == null)
            progressDialog = getProgressDialogTranslucent();
        if (retryCount == 0) {
            progressDialog.setTitle(getString(R.string.message_loading_face_response));
        } else {
            progressDialog.setTitle("Retrying ...");
        }
        progressDialog.setMessage(getString(R.string.message_wait));
        if (!isFinishing())
            progressDialog.show();

        if (serverCallInProgress) {
            return;
        }
        serverCallInProgress = true;

        String defaultHairstyle = "";
        String urlPath = null;

        if (User.getInstance().getDefaultFaceItem() != null && User.getInstance().getDefaultFaceItem().getHairstyleId() != null && !User.getInstance().getDefaultFaceItem().getHairstyleId().isEmpty()) {
            defaultHairstyle = User.getInstance().getDefaultFaceItem().getHairstyleId();
        } else {
            if (User.getInstance().getmGender().equals("m")) {
                defaultHairstyle = ConstantsFunctional.HAIRSTYLE_DEFAULT_MALE;
            } else {
                defaultHairstyle = ConstantsFunctional.HAIRSTYLE_DEFAULT_FEMALE;
            }
        }
        urlPath = ConstantsUtil.URL_BASEPATH_CREATE_V2 + ConstantsUtil.URL_METHOD_CREATEFACE_AND_HAIR + User.getInstance().getmUserId()
                + "/" + User.getInstance().getmGender()
                + "/" + picSource
                + "/" + defaultHairstyle
                + feducialPath
                + "/?Pic_Path=" + awsKeyPathPicUpload;

        DataManager.getInstance().requestFaceObj(urlPath, new DataManager.OnResponseHandlerInterface() {
            @Override
            public void onResponse(Object obj) {
                faceItem = (FaceItem) obj;
                serverCallInProgress = false;
                retryCount = 0;
                if (faceItem == null || faceItem.getFaceObjkey() == null || faceItem.getFaceObjkey().equals("null")) {
                    faceItem = null;
                    if (progressDialog != null)
                        progressDialog.dismiss();
                    showAlertFaceCreateError("Error", "Please place your facial points correctly and retry.");
                } else {
                    faceItem.setFeducialPoints(feducialPath);
                    //if(filePathAdjustedPic != null && !filePathAdjustedPic.isEmpty())
                    faceItem.setImageSavedFilePath(uFilePathPic);
                    faceItem.setImageResizeRatio(String.valueOf(ratioImageResize));
                    startFaceItemDownload();
                }
            }

            @Override
            public void onResponseError(String errorMessage, int errorCode) {
                serverCallInProgress = false;
                if (progressDialog != null)
                    progressDialog.dismiss();
                btnNext.setText("Retry");
                retryCount++;
                if (errorCode == DataManager.CODE_DATA_MANAGER_NETWORK_ERROR) {
                    if (retryCount == 1 || retryCount == ConstantsUtil.COUNT_RETRY_SERVICE_CRITICAL)
                        Toast.makeText(getApplicationContext(), ConstantsUtil.MESSAGE_TOAST_NETWORK_RESPONSE_FAILED, Toast.LENGTH_SHORT).show();
                }
                //TODO
//                if (retryCount < ConstantsUtil.COUNT_RETRY_SERVICE)
//                    requestFaceObj();
            }
        });
    }

    private void startFaceItemDownload() {
        if (progressDialog == null)
            progressDialog = getProgressDialogTranslucent();
        progressDialog.setTitle(getString(R.string.message_loading_face_response));
        //progressDialog.setMessage(getString(R.string.message_wait_face_download));
        progressDialog.setMessage(getString(R.string.message_wait));
        if (!isFinishing())
            progressDialog.show();
        final BaseAccessoryItem face = new BaseAccessoryItem(faceItem.getFaceObjkey(), faceItem.getObjFaceDStatus(), faceItem.getFacePngkey(), faceItem.getTextureFaceDStatus());
        if (faceItem.getHairObjkey() != null && !faceItem.getHairObjkey().isEmpty() && faceItem.getHairPngKey() != null && !faceItem.getHairPngKey().isEmpty()) {
            face.dependentItem = new BaseAccessoryItem(faceItem.getHairObjkey(), faceItem.getObjHairDStatus(), faceItem.getHairPngKey(), faceItem.getTextureHairDStatus());
        }
        new AssetDownloader(this).downloadAsset(face, new OnAssetDownloadListener() {
            @Override
            public void onDownload(BaseAccessoryItem item) {
                faceItem.setObjFaceDStatus(ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus());
                faceItem.setTextureFaceDStatus(ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus());
                if (faceItem.getHairObjkey() != null && !faceItem.getHairObjkey().isEmpty() && faceItem.getHairPngKey() != null && !faceItem.getHairPngKey().isEmpty()) {
                    faceItem.setObjHairDStatus(ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus());
                    faceItem.setTextureHairDStatus(ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus());
                    faceItem.setFaceObjkey(item.getObjAwsKey());
                    if (item.dependentItem != null)
                        faceItem.setHairObjkey(item.dependentItem.getObjAwsKey());
                }
                InkarneAppContext.getDataSource().create(faceItem);
                if (progressDialog != null)
                    progressDialog.dismiss();
                launchDataActivity();
            }

            @Override
            public void onDownloadFailed(String comboId) {
                if (progressDialog != null)
                    progressDialog.dismiss();
            }

            @Override
            public void onDownloadProgress(String comboId, int percentage) {

            }
        });
    }

    private void updateDefaultFace() {
        User.getInstance().setDefaultFaceId(faceItem.getFaceId());
        User.getInstance().setDefaultFaceItem(faceItem);
        InkarneAppContext.getDataSource().create(User.getInstance());
        InkarneAppContext.getDataSource().create(faceItem);
    }

    private void launchNextActivity() {
        if (!isNextActivityLaunched) {
            isNextActivityLaunched = true;
            InkarneAppContext.getDataSource().create(faceItem);
            Intent intent = new Intent(this, RedoAvatarActivity.class);
            //intent.putExtra(FaceItem.EXTRA_PARAM_FACE_OBJ, faceItem);
            intent.putExtra(RedoAvatarActivity.EXTRA_PARAM_FACE_ID, faceItem.getFaceId());
            startActivity(intent);
            finish();
        }
    }

    public void launchDataActivity() {
        if (!isNextActivityLaunched) {
            isNextActivityLaunched = true;
            updateDefaultFace();
            Log.e(LOGTAG, " ******** Launch ShopActivity in DataActivity  *******");
            Intent intent = new Intent(FiducialActivityEdit.this, DataActivity.class);
            InkarneAppContext.setIsDefaultFaceChanged(true);
            if (InkarneAppContext.comboId != null) {
                intent.putExtra("comboDataId", InkarneAppContext.comboId);
            }
            startActivity(intent);
            finish();
        }
    }
}
