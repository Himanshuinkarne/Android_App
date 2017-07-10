package com.svc.sml.Activity;

//import android.app.AlertDialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.svc.sml.Database.User;
import com.svc.sml.Helper.AssetDownloader;
import com.svc.sml.Helper.DataManager;
import com.svc.sml.InkarneAppContext;
import com.svc.sml.Model.BaseAccessoryItem;
import com.svc.sml.Model.FaceItem;
import com.svc.sml.Model.TransferProgressModel;
import com.svc.sml.R;
import com.svc.sml.Utility.AWSUtil;
import com.svc.sml.Utility.ConstantsFunctional;
import com.svc.sml.Utility.ConstantsUtil;

import java.util.ArrayList;
import java.util.HashMap;

public class FiducialActivity2 extends FiducialActivity {
    public static final String LOGTAG = "FiducialsActivity2";
    public static final String EXTRA_PARAM_FEDUCIAL_PATH = "EXTRA_PARAM_FEDUCIAL_PATH";
    private boolean serverCallInProgress = false;
    private TransferUtility transferUtility;
    //private List<TransferObserver> observersDownload = new ArrayList<TransferObserver>();
    private ProgressDialog progressDialog;
    private boolean isNextActivityLaunched;
    private String feducialPath = "";
    private int retryCount = 0;
    private FaceItem faceItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = getProgressDialogTranslucent();
        if (User.getInstance().getDefaultFaceId() == null || User.getInstance().getDefaultFaceId().length() == 0) {
            btnNextHandler();
        }
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
//        if(progressDialog!=null)
//            progressDialog.dismiss();
    }

    public ProgressDialog getProgressDialogTranslucent() {
        //ProgressDialog p = new ProgressDialog(this, R.style.AppCompatAlertTranslucentDialogStyle);
        ProgressDialog p = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
        p.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        p.setCanceledOnTouchOutside(false);
        p.setCancelable(true);
        return p;
    }

    @Override
    public void onBackPressed() {
        // your code.
        if (progressDialog != null && progressDialog.isShowing())
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
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
            btnNext.setEnabled(true);
            btnNext.setText("Next");
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected int[] getFedPointsIndexToShow() {
        int[] fedPoints = new int[]{2, 3, 4, 5, 6, 7};
        return fedPoints;
    }

    @Override
    protected boolean isExternalFiducialFeature() {
        return false;
    }

    @Override
    protected void initDrawFedPoints() {
        initFaceItem();
        getIntentData();
        initUserFiducials();
        //initDemoFeducials();
        //drawDemoFeducialPoints();
        drawUserFiducialPoints();
    }

    //TODO
    protected void initFaceItem() {
        transferUtility = AWSUtil.getTransferUtility(FiducialActivity2.this);
        picSource = getIntent().getStringExtra(FaceSelectionActivity.EXTRA_PARAM_PIC_SOURCE_TYPE);
        awsKeyPathPicUpload = getIntent().getStringExtra(AdjustPicActivity.EXTRA_PARAM_PIC_AWS_KEY_PATH);
        ratioImageResize = getIntent().getFloatExtra(AdjustPicActivity.EXTRA_PARAM_RESIZE_RATIO,(float)1);

    }

    private void getIntentData() {
        picSource = getIntent().getStringExtra(FaceSelectionActivity.EXTRA_PARAM_PIC_SOURCE_TYPE);
        awsKeyPathPicUpload = getIntent().getStringExtra(AdjustPicActivity.EXTRA_PARAM_PIC_AWS_KEY_PATH);
        ratioImageResize = getIntent().getFloatExtra(AdjustPicActivity.EXTRA_PARAM_RESIZE_RATIO, (float)1);
        uFilePathPic = getIntent().getStringExtra(EXTRA_PARAM_USER_PIC_PATH);
        uheight = (float[]) getIntent().getSerializableExtra(EXTRA_PARAM_USER_HEIGHT_ARRAY);
        uwidth = (float[]) getIntent().getSerializableExtra(EXTRA_PARAM_USER_WIDTH_ARRAY);

        //dFilePathPic = getIntent().getStringExtra(EXTRA_PARAM_DEMO_PIC_PATH);
        //dheight = (float[]) getIntent().getSerializableExtra(EXTRA_PARAM_DEMO_HEIGHT_ARRAY);
        //dwidth = (float[]) getIntent().getSerializableExtra(EXTRA_PARAM_DEMO_WIDTH_ARRAY);
    }

    protected synchronized void createFiducialPoints() {
        Log.d(LOGTAG, "uratiow : " + uratiow + "uratioh : " + uratioh);
        int[] uwidthn = new int[11];
        int[] uheightn = new int[11];
        for (int f = 0; f < 11; f++) {
            uwidth[f] = uwidth[f] / uratiow;
            uheight[f] = uheight[f] / uratioh;
            uwidthn[f] =  (int) ( uwidth[f]*ratioImageResize);
            uheightn[f] = (int) (uheight[f] *ratioImageResize);
        }
        feducialPath = "";
        for (int i = 0; i < 11; i++) {
            feducialPath += "/" + uwidthn[i] + "/";
            feducialPath += uheightn[i];
        }
    }

    //@Override
    protected void btnNextHandler1() {
        createFiducialPoints();
        if (InkarneAppContext.isAddFaceForRedoAvatar) {
            //getFaceItem(uFilePathPic);
            getFaceItem();
        } else {
            launchBM();
        }
    }

    @Override
    protected void btnNextHandler() {
        createFiducialPoints();
        getFaceItem();
    }

    private void launchBM() {
        Intent intent = new Intent(FiducialActivity2.this, BodyMeasurementActivity.class);
        //intent.putExtra(EXTRA_PARAM_USER_PIC_PATH, uFilePathPic);
        intent.putExtra(AdjustPicActivity.EXTRA_PARAM_PIC_AWS_KEY_PATH, awsKeyPathPicUpload);
        intent.putExtra(FaceSelectionActivity.EXTRA_PARAM_PIC_SOURCE_TYPE, picSource);
        intent.putExtra(EXTRA_PARAM_FEDUCIAL_PATH, feducialPath);
        startActivity(intent);
        finish();
    }


    public void getFaceItem() {
        if (faceItem == null)
            requestFaceObj();
        else {
            startFaceItemDownload();
        }
    }

    protected void showAlertError(String title, String msg) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(msg)
                .setCancelable(false)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        finish();
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

        if (User.getInstance().getDefaultFaceItem() != null && User.getInstance().getDefaultFaceItem().getHairstyleId() != null && !User.getInstance().getDefaultFaceItem().getHairstyleId().isEmpty()){
            defaultHairstyle = User.getInstance().getDefaultFaceItem().getHairstyleId();
        }else {
            if(User.getInstance().getmGender().equals("m")){
                defaultHairstyle = ConstantsFunctional.HAIRSTYLE_DEFAULT_MALE;
            }else{
                defaultHairstyle = ConstantsFunctional.HAIRSTYLE_DEFAULT_FEMALE;
            }
        }
        urlPath = ConstantsUtil.URL_BASEPATH_CREATE_V2 + ConstantsUtil.URL_METHOD_CREATEFACE_AND_HAIR + User.getInstance().getmUserId()
                + "/" + User.getInstance().getmGender()
                + "/" + picSource
                + "/" + defaultHairstyle
                + feducialPath
                + "/?Pic_Path=" + awsKeyPathPicUpload;

//        if (User.getInstance().getDefaultFaceItem() != null && User.getInstance().getDefaultFaceItem().getHairstyleId() != null && !User.getInstance().getDefaultFaceItem().getHairstyleId().isEmpty()) {
//            defaultHairstyle = User.getInstance().getDefaultFaceItem().getHairstyleId();
//            urlPath = ConstantsUtil.URL_BASEPATH_CREATE + ConstantsUtil.URL_METHOD_CREATEFACE_AND_HAIR + User.getInstance().getmUserId()
//                    + "/" + User.getInstance().getmGender()
//                    + "/" + picSource
//                    + "/" + defaultHairstyle
//                    + feducialPath
//                    + "/?Pic_Path=" + awsKeyPathPicUpload;
//        } else {
//            urlPath = ConstantsUtil.URL_BASEPATH_CREATE + ConstantsUtil.URL_METHOD_CREATEFACE + User.getInstance().getmUserId()
//                    + "/" + User.getInstance().getmGender()
//                    + "/" + picSource
//                    + feducialPath
//                    + "/?Pic_Path=" + awsKeyPathPicUpload;
//        }

        DataManager.getInstance().requestFaceObj(urlPath, new DataManager.OnResponseHandlerInterface() {
            @Override
            public void onResponse(Object obj) {
                faceItem = (FaceItem) obj;
                serverCallInProgress = false;
                //progressDialog.dismiss();
                retryCount = 0;
                if (faceItem == null || faceItem.getFaceObjkey() == null || faceItem.getFaceObjkey().equals("null")) {
                    showAlertError("Error", "Please place your facial points correctly and retry.");
                } else {
                    startFaceItemDownload();
                }
            }

            @Override
            public void onResponseError(String errorMessage, int errorCode) {
                serverCallInProgress = false;
                progressDialog.dismiss();
                btnNext.setText("Retry");
                retryCount++;
                if (errorCode == DataManager.CODE_DATA_MANAGER_NETWORK_ERROR) {
                    if (retryCount == 1 || retryCount == ConstantsUtil.COUNT_RETRY_SERVICE_CRITICAL)

                        Toast.makeText(getApplicationContext(), ConstantsUtil.MESSAGE_TOAST_NETWORK_RESPONSE_FAILED, Toast.LENGTH_SHORT).show();
                }
                if (retryCount < ConstantsUtil.COUNT_RETRY_SERVICE)
                    requestFaceObj();
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
        BaseAccessoryItem face = new BaseAccessoryItem(faceItem.getFaceObjkey(), faceItem.getObjFaceDStatus(), faceItem.getFacePngkey(), faceItem.getTextureFaceDStatus());
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
                }
                InkarneAppContext.getDataSource().create(faceItem);
                progressDialog.dismiss();
                launchNextActivity();
            }

            @Override
            public void onDownloadFailed(String comboId) {
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

    private void launchNextActivity1() {
        if (!isNextActivityLaunched) {
            isNextActivityLaunched = true;
            if (InkarneAppContext.isAddFaceForRedoAvatar) {
                InkarneAppContext.getDataSource().create(faceItem);
                Intent intent = new Intent(this, RedoAvatarActivity.class);
                //intent.putExtra(FaceItem.EXTRA_PARAM_FACE_OBJ, faceItem);
                intent.putExtra(RedoAvatarActivity.EXTRA_PARAM_FACE_ID, faceItem.getFaceId());
                startActivity(intent);
                finish();
            } else {
                updateDefaultFace();
                Intent intent = new Intent(this, HairStyleActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    private void startDownloadProgressDialog() {
        AlertProgressDialog alertProgressDialog = null;
        if (alertProgressDialog == null)
            alertProgressDialog = new AlertProgressDialog();
        if (alertProgressDialog != null) {
            alertProgressDialog.showDialog();
            alertProgressDialog.alertDialog.setCancelable(true);
        }
    }

   /* private void startDownload1() {
        //startDownloadProgressDialog();
        progressDialog.setTitle(getString(R.string.message_loading_face_response));
        progressDialog.setMessage(getString(R.string.message_wait_face_download));
        if (!progressDialog.isShowing())
            progressDialog.show();
        beginDownload(faceItem.getFaceObjkey());
        beginDownload(faceItem.getFacePngkey());
        beginDownload(faceItem.getHairObjkey());
        beginDownload(faceItem.getHairPngKey());
    }

    private void beginDownload(String key) {
        if (key == null || key.length() < 2) {
            Log.e(LOGTAG, "download key null or blank");
            return;
        }

        File file = new File(Environment.getExternalStorageDirectory().toString() + "/" + key);
        if (ConstantsUtil.checkFileExist(file.getAbsolutePath())) {
            Log.d(LOGTAG, "File already downloaded : " + key);
            faceItem.downloadedCount++;
            TransferProgressModel t = faceItem.mapDownload.get(key);
            if (t != null) {
                t.transferState = TransferState.COMPLETED;
                t.progress = 100;
                faceItem.mapDownload.put(key, t);
            } else {
                TransferProgressModel tPModel = new TransferProgressModel(key, 100, TransferState.COMPLETED);
                faceItem.mapDownload.put(key, tPModel);
            }
            ////alertProgressDialog.showProgress(faceItem.mapDownload, false);
            checkDownloads(faceItem);
            return;
        }
        Log.d(LOGTAG, "face download file  " + file);
        TransferProgressModel tPModel = new TransferProgressModel(key, 0, TransferState.WAITING);
        faceItem.mapDownload.put(key, tPModel);
        TransferObserver observer = transferUtility.download(ConstantsUtil.AWSBucketName, key, file);
        observer.setTransferListener(new DownloadListener(key));
        observersDownload.add(observer);
        ////alertProgressDialog.showProgress(faceItem.mapDownload, false);
    }

    *//*
     * A TransferListener class that can listen to a download task and be
     * notified when the status changes.
     *//*
    private class DownloadListener implements TransferListener {

        final String key;

        private DownloadListener(String key) {
            this.key = key;
        }

        @Override
        public void onError(int id, Exception e) {
            Log.e(LOGTAG, "Error during download key " + key + "  e " + e.toString());
            TransferProgressModel t = faceItem.mapDownload.get(key);
            t.transferState = TransferState.FAILED;
            faceItem.mapDownload.put(key, t);
            //btnNext.setEnabled(true);
            checkDownloads(faceItem);
            ////alertProgressDialog.showProgress(faceItem.mapDownload, false);
        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            int progress = (int) (bytesCurrent * 100 / bytesTotal);
            TransferProgressModel t = faceItem.mapDownload.get(key);
            t.progress = progress;
            faceItem.mapDownload.put(key, t);
            ////alertProgressDialog.showProgress(faceItem.mapDownload, false);
        }

        @Override
        public void onStateChanged(int id, TransferState state) {
            if (state == TransferState.FAILED) {
                Log.e(LOGTAG, "Download failed : " + key);
                TransferProgressModel t = faceItem.mapDownload.get(key);
                t.transferState = TransferState.FAILED;
                faceItem.mapDownload.put(key, t);
                checkDownloads(faceItem);
            } else if (state == TransferState.COMPLETED) {
                Log.e(LOGTAG, "Download successful : " + key);
                TransferProgressModel t = faceItem.mapDownload.get(key);
                t.transferState = TransferState.COMPLETED;
                t.progress = 100;
                faceItem.mapDownload.put(key, t);
                faceItem.downloadedCount++;
                checkDownloads(faceItem);
            }
            /////alertProgressDialog.showProgress(faceItem.mapDownload, false);
        }
    }

    private void checkDownloads(FaceItem faceItem) {
        int downloadCount = 0;
        int failedCount = 0;
        if (faceItem.downloadedCount == 4) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    launchNextActivity();
                }
            }, 100);
            return;
        }
        for (String key : faceItem.mapDownload.keySet()) {
            TransferProgressModel t = faceItem.mapDownload.get(key);
            if (t.transferState == TransferState.COMPLETED) {
                downloadCount++;
            } else if (t.transferState == TransferState.FAILED) {
                failedCount++;
            }
        }
        if (downloadCount + failedCount == 4) {
            Toast.makeText(this, "download failed please retry", Toast.LENGTH_SHORT).show();
            Log.e(LOGTAG, "download failed Retry");
            btnNext.setText("Retry");
            faceItem.downloadedCount = 0;
            progressDialog.dismiss();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //alertProgressDialog.dismissDialog();
                }
            }, 200);

            stopDownload();
            btnNext.setEnabled(true);
        }
    }

    private void stopDownload() {
        for (TransferObserver ob : observersDownload) {
            if (transferUtility != null)
                transferUtility.cancel(ob.getId());
            ob.cleanTransferListener();
        }
        observersDownload.clear();
    }*/


    private class AlertProgressDialog {
        private LinearLayout llDataProgressContainer;
        private LinearLayout llUploadProgressContainer;
        private ListView lvUpload;

        private AlertDialog.Builder dialogBuilder;
        public AlertDialog alertDialog;
        private ArrayList<TransferProgressModel> arrayUploadModel = new ArrayList<TransferProgressModel>();
        private UploadAdapter adapter;
        private LayoutInflater mInflater;

        public AlertProgressDialog() {
            createUploadDialog();
            alertDialog.setCancelable(true);
            alertDialog.setCanceledOnTouchOutside(false);
        }

        public void dismissDialog() {
            alertDialog.dismiss();
        }

        public boolean isDialogShowing() {
            return alertDialog.isShowing();
        }

        public void showDialog() {

        }

        public void showDialog1() {
            alertDialog.show();
        }

        public void showProgress(HashMap<String, TransferProgressModel> mapUpload, boolean isUpload) {

        }

        public void showProgress1(HashMap<String, TransferProgressModel> mapUpload, boolean isUpload) {
            llDataProgressContainer.setVisibility(View.INVISIBLE);
            mapUpload.keySet();
            ArrayList<TransferProgressModel> arrayModel = new ArrayList<>();
            int i = 0;
            for (String key : mapUpload.keySet()) {
                arrayModel.add(mapUpload.get(key));
                i++;
            }
            adapter.updateList(arrayModel);
        }

//        public void showProgress() {
//            llDataProgressContainer.setVisibility(View.VISIBLE);
//            llUploadProgressContainer.setVisibility(View.INVISIBLE);
//            //alertDialog.show();
//        }

        private void createUploadDialog() {

        }

        private void createUploadDialog1() {
            dialogBuilder = new AlertDialog.Builder(FiducialActivity2.this);
            LayoutInflater inflater = FiducialActivity2.this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_upload_download, null);
            dialogBuilder.setView(dialogView);

            llDataProgressContainer = (LinearLayout) dialogView.findViewById(R.id.ll_dailog_data_pb_container);
            llUploadProgressContainer = (LinearLayout) dialogView.findViewById(R.id.ll_dailog_data_pb_container);
            lvUpload = (ListView) dialogView.findViewById(R.id.lv_dailog_upload);

            adapter = new UploadAdapter(FiducialActivity2.this, arrayUploadModel);
            lvUpload.setAdapter(adapter);
            alertDialog = dialogBuilder.create();
            mInflater = (LayoutInflater) FiducialActivity2.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        private class UploadAdapter extends ArrayAdapter<TransferProgressModel> {
            public ArrayList<TransferProgressModel> progressModelArrayList;

            public UploadAdapter(Context context, ArrayList<TransferProgressModel> progressModelArrayList) {
                super(context, 0, progressModelArrayList);
                this.progressModelArrayList = progressModelArrayList;
            }

            public void updateList(ArrayList<TransferProgressModel> progressModelList) {
                progressModelArrayList.clear();
                progressModelArrayList.addAll(progressModelList);
                this.notifyDataSetChanged();
            }

            private class Holder {
                public TextView textView;
                public ProgressBar pb;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Holder holder;
                TransferProgressModel uploadModel = getItem(position);
                if (convertView == null) {
                    //convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_upload_dailog, parent, false);
                    convertView = mInflater.inflate(R.layout.list_item_upload_dailog, parent, false);
                    holder = new Holder();
                    holder.textView = (TextView) convertView.findViewById(R.id.tv_dailog_upload_file1);
                    holder.pb = (ProgressBar) convertView.findViewById(R.id.pb_dialog_upload1);
                    convertView.setTag(holder);
                } else {
                    holder = (Holder) convertView.getTag();
                }
//                ProgressBar pb = (ProgressBar) convertView.findViewById(R.id.pb_dialog_upload1);
//                TextView tv = (TextView) convertView.findViewById(R.id.tv_dailog_upload_file1);
                if (uploadModel != null) {
                    //Log.d(LOGTAG, " file :" + uploadModel.filename + "  progress:" + uploadModel.progress);
                    holder.pb.setProgress(uploadModel.progress);
                    holder.textView.setText(uploadModel.filename + "        " + uploadModel.transferState.toString());
                }
                return convertView;
            }
        }
    }
}
