package com.svc.sml.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.svc.sml.Database.User;
import com.svc.sml.Helper.DataManager;
import com.svc.sml.InkarneAppContext;
import com.svc.sml.Model.FaceItem;
import com.svc.sml.Model.TransferProgressModel;
import com.svc.sml.R;
import com.svc.sml.Utility.AWSUtil;
import com.svc.sml.Utility.ConstantsUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//import android.support.v7.app.AlertDialog;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FaceUploadActivity extends Activity {

    private final static String LOGTAG = "FaceUploadActivity";
    int countFaceCreateTrail = 0;
    protected boolean serverCallInProgress = false;
    public TransferUtility transferUtility;
    private List<TransferObserver> observersDownload;
    protected ProgressDialog progressDialog;
    public Button btnNextGetFaceItem;
    private boolean isFace3DLaunched;

    protected FaceItem faceItem;
    public AlertProgressDialog alertProgressDialog;


    public String Fed1Path = "";
    public String picSource = "0";
    public String awsKeyPathPicUpload;
    private int retryCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        picSource = getIntent().getStringExtra(FaceSelectionActivity.EXTRA_PARAM_PIC_SOURCE_TYPE);
        awsKeyPathPicUpload = getIntent().getStringExtra(AdjustPicActivity.EXTRA_PARAM_PIC_AWS_KEY_PATH);
        //uFilePathPic = getIntent().getStringExtra(FaceSelectionActivity.EXTRA_PARAM_PIC_PATH);

        transferUtility = AWSUtil.getTransferUtility(this);
        observersDownload = new ArrayList<TransferObserver>();
        alertProgressDialog = new AlertProgressDialog();
    }

    public void onResume() {
        super.onResume();
        isFace3DLaunched = false;
        btnNextGetFaceItem.setText("Next");
        btnNextGetFaceItem.setEnabled(true);
    }

    @Override
    public void onBackPressed() {
        // your code.
        if (alertProgressDialog != null && alertProgressDialog.isDialogShowing()) {
            alertProgressDialog.dismissDialog();
        }

        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
        btnNextGetFaceItem.setEnabled(true);
        btnNextGetFaceItem.setText("Next");
        super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // your code
            if (alertProgressDialog != null && alertProgressDialog.isDialogShowing()) {
                alertProgressDialog.dismissDialog();
            }
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
            btnNextGetFaceItem.setEnabled(true);
            btnNextGetFaceItem.setText("Next");
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    public void getFaceItem(String userPicPath) {
        if (faceItem == null)
            requestFaceObj();
        else {
            startDownload();
        }
        btnNextGetFaceItem.setEnabled(false);
    }

    public void requestFaceObj() {
        btnNextGetFaceItem.setEnabled(false);
        if (retryCount == 0) {
            progressDialog = ProgressDialog.show(this, getString(R.string.message_loading_face_response), getString(R.string.message_wait));
            progressDialog.setCancelable(true);
            progressDialog.setCanceledOnTouchOutside(false);
        }
        else {
            progressDialog = ProgressDialog.show(this, "Retrying ...", getString(R.string.message_wait));
        }
        if (serverCallInProgress) {
            return;
        }
        serverCallInProgress = true;

        //Fed1Path = "/852/796/285/771/660/907/497/905/693/1013/446/1006/712/732/444/724/829/1041/299/1017/567/1239";//TODO
        //urlPath = "http://inkarnepub-prod.elasticbeanstalk.com/Service1.svc/CreateFace/31/m/1/852/796/285/771/660/907/497/905/693/1013/446/1006/712/732/444/724/829/1041/299/1017/567/1239/?Pic_Path=inkarne/users/1/1001.jpg";
        String urlPath = ConstantsUtil.URL_BASEPATH + ConstantsUtil.URL_METHOD_CREATEFACE + User.getInstance().getmUserId() + "/" + User.getInstance().getmGender()
                + "/" + picSource
                + Fed1Path
                + "/?Pic_Path=" + awsKeyPathPicUpload;
        DataManager.getInstance().requestFaceObj(urlPath, new DataManager.OnResponseHandlerInterface() {
            @Override
            public void onResponse(Object obj) {
                retryCount = 0;
                faceItem = (FaceItem) obj;
                startDownload();
                serverCallInProgress = false;
                progressDialog.dismiss();
            }

            @Override
            public void onResponseError(String errorMessage, int errorCode) {
                serverCallInProgress = false;
                progressDialog.dismiss();
                btnNextGetFaceItem.setEnabled(true);
                btnNextGetFaceItem.setText("Retry");
                retryCount++;
                if (retryCount < 3)
                    Toast.makeText(FaceUploadActivity.this, ConstantsUtil.MESSAGE_TOAST_NETWORK_RESPONSE_FAILED, Toast.LENGTH_SHORT).show();
                if (retryCount < 4)
                    requestFaceObj();

            }
        });
    }

    private void startDownload() {
        alertProgressDialog.showDialog();
        alertProgressDialog.alertDialog.setCancelable(true);
        beginDownload(faceItem.getFaceObjkey());
        beginDownload(faceItem.getFacePngkey());
        beginDownload(faceItem.getHairObjkey());
        beginDownload(faceItem.getHairPngKey());
    }

    private void beginDownload(String key) {
        if (key == null || key.length() < 2) {
            Toast.makeText(this, "Could not find the filepath of the selected file", Toast.LENGTH_SHORT).show();
            Log.e(LOGTAG, "download key null or blank");
            return;
        }

        File file = new File(Environment.getExternalStorageDirectory().toString() + "/" + key);
        if (ConstantsUtil.checkFileKeyExist(key)) {
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
            alertProgressDialog.showProgress(faceItem.mapDownload, false);
            checkDownloads(faceItem);
            return;
        }
        Log.d(LOGTAG, "face download file  " + file);
        TransferProgressModel tPModel = new TransferProgressModel(key, 0, TransferState.WAITING);
        faceItem.mapDownload.put(key, tPModel);
        TransferObserver observer = transferUtility.download(ConstantsUtil.AWSBucketName, key, file);
        observer.setTransferListener(new DownloadListener(key));
        observersDownload.add(observer);
        alertProgressDialog.showProgress(faceItem.mapDownload, false);
    }

    /*
     * A TransferListener class that can listen to a download task and be
     * notified when the status changes.
     */
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
            //btnNextGetFaceItem.setEnabled(true);
            checkDownloads(faceItem);
            alertProgressDialog.showProgress(faceItem.mapDownload, false);
        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            int progress = (int) (bytesCurrent * 100 / bytesTotal);
            TransferProgressModel t = faceItem.mapDownload.get(key);
            t.progress = progress;
            faceItem.mapDownload.put(key, t);
            alertProgressDialog.showProgress(faceItem.mapDownload, false);
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
            alertProgressDialog.showProgress(faceItem.mapDownload, false);
        }
    }

    private void checkDownloads(FaceItem faceItem) {
        int downloadCount = 0;
        int failedCount = 0;
        if (faceItem.downloadedCount == 4) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    launchFace3DActivity();
                }
            },100);

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
            Toast.makeText(this, "download failed Please Retry", Toast.LENGTH_SHORT).show();
            Log.e(LOGTAG, "download failed Retry");
            btnNextGetFaceItem.setText("Retry");
            faceItem.downloadedCount = 0;
            alertProgressDialog.dismissDialog();
            stopDownload();
            btnNextGetFaceItem.setEnabled(true);
        }
    }

    private void stopDownload() {
        for (TransferObserver ob : observersDownload) {
            if (transferUtility != null)
                transferUtility.cancel(ob.getId());
            ob.cleanTransferListener();
        }
        observersDownload.clear();
    }

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
        }

        public void dismissDialog() {
            alertDialog.dismiss();
        }

        public boolean isDialogShowing() {
            return alertDialog.isShowing();
        }

        public void showDialog() {
            alertDialog.show();
        }

        public void showProgress(HashMap<String, TransferProgressModel> mapUpload, boolean isUpload) {
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
            dialogBuilder = new AlertDialog.Builder(FaceUploadActivity.this);
            LayoutInflater inflater = FaceUploadActivity.this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_upload_download, null);
            dialogBuilder.setView(dialogView);

            llDataProgressContainer = (LinearLayout) dialogView.findViewById(R.id.ll_dailog_data_pb_container);
            llUploadProgressContainer = (LinearLayout) dialogView.findViewById(R.id.ll_dailog_data_pb_container);
            lvUpload = (ListView) dialogView.findViewById(R.id.lv_dailog_upload);

            adapter = new UploadAdapter(FaceUploadActivity.this, arrayUploadModel);
            lvUpload.setAdapter(adapter);
            alertDialog = dialogBuilder.create();
            mInflater = (LayoutInflater) FaceUploadActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                    Log.d(LOGTAG, " file :" + uploadModel.filename + "  progress:" + uploadModel.progress);
                    holder.pb.setProgress(uploadModel.progress);
                    holder.textView.setText(uploadModel.filename + "        " + uploadModel.transferState.toString());
                }
                return convertView;
            }
        }
    }

    private void launchFace3DActivity() {
        stopDownload();
        alertProgressDialog.dismissDialog();
        if (!isFace3DLaunched) {
            isFace3DLaunched = true;
            updateDefaultFace();
            Intent intent = new Intent(this, BodyMeasurementActivity.class);
            //intent.putExtra(FaceItem.EXTRA_PARAM_FACE_OBJ, faceItem);
            startActivity(intent);
        }
    }

    private void updateDefaultFace() {
        User.getInstance().setDefaultFaceId(faceItem.getFaceId());
        User.getInstance().setDefaultFaceItem(faceItem);
        InkarneAppContext.getDataSource().create(User.getInstance());
        InkarneAppContext.getDataSource().create(faceItem);
    }

}

