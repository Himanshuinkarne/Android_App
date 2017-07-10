package com.svc.sml.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.svc.sml.Adapter.HairstyleAdapter;
import com.svc.sml.Database.User;
import com.svc.sml.Helper.DataManager;
import com.svc.sml.InkarneAppContext;
import com.svc.sml.Model.BaseAccessoryItem;
import com.svc.sml.Model.FaceItem;
import com.svc.sml.R;
import com.svc.sml.Utility.ConstantsUtil;

import java.util.ArrayList;

public class HairStyleActivity extends Activity {
    public static final String LOGTAG = "HairStyleActivity";
     private GridView gridview;
    private HairstyleAdapter adapter;
    private ArrayList<BaseAccessoryItem>arrayAccessories;
    private BaseAccessoryItem selecteDownloadedAccItem;
    private BaseAccessoryItem latestClickedItem;
    private ProgressDialog progressDialog;
    private boolean shouldLaunchTrue = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hairstyle);
        gridview = (GridView) findViewById(R.id.gridview);
        populateHairstyleList(ConstantsUtil.EAccessoryType.eAccTypeHair.toString());
        progressDialog = getProgressDialog();
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(HairStyleActivity.this, "" + position,
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onResume(){
        super.onResume();
        shouldLaunchTrue = false;
    }

    public ProgressDialog getProgressDialog(){
        ProgressDialog p = new ProgressDialog(this,R.style.AppCompatAlertDialogStyle);
        p.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        p.setCanceledOnTouchOutside(false);
        p.setCancelable(true);
        return p;
    }

    private void populateHairstyleList(String accessoryType) {
        DataManager.getInstance().getAccessories(accessoryType, false, new DataManager.OnResponseHandlerInterface() {
            @Override
            public void onResponse(Object obj) {
                arrayAccessories = (ArrayList<BaseAccessoryItem>) obj;
                //adapter = new HairstyleAdapter(HairStyleActivity.this, arrayAccessories, User.getInstance().getDefaultFaceItem());
                adapter = new HairstyleAdapter(HairStyleActivity.this, arrayAccessories, User.getInstance().getDefaultFaceItem(), new OnAccessoryAdapterListener() {
                    @Override
                    public void onAccessorySelected(BaseAccessoryItem item) {
                        selecteDownloadedAccItem = item;
                        if (checkDownloads() && shouldLaunchTrue) {
                            progressDialog.dismiss();
                            launchDataActivity();
                        }
                    }

                    @Override
                    public void onAccessoryClicked(BaseAccessoryItem item) {
                        latestClickedItem = item;
                    }
                });
                gridview.setAdapter(adapter);
            }

            @Override
            public void onResponseError(String errorMessage, int errorCode) {

            }
        });
    }

    protected void showAlertToSelectHairstyle(String title,String msg) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(msg)
                .setCancelable(false)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        //finish();
                    }
                }).create().show();
    }

    public boolean checkDownloads(){
        if(selecteDownloadedAccItem != null && latestClickedItem != null && selecteDownloadedAccItem.getObjId().equals(latestClickedItem.getObjId())){
            progressDialog.dismiss();
            FaceItem faceItem = User.getInstance().getDefaultFaceItem();
            faceItem.setHairstyleId(selecteDownloadedAccItem.getObjId());
            faceItem.setHairObjkey(selecteDownloadedAccItem.getObjAwsKey());
            faceItem.setHairPngKey(selecteDownloadedAccItem.getTextureAwsKey());
            InkarneAppContext.getDataSource().create(faceItem);
            User.getInstance().setDefaultFaceItem(faceItem);
            InkarneAppContext.getDataSource().create(User.getInstance());
           return true;
        }
        return false;
    }


    public void nextBtnClickHandler(View v) {
        if(progressDialog == null)
        progressDialog = getProgressDialog();
        if(latestClickedItem == null){
            showAlertToSelectHairstyle("","Please choose your default hairstyle");
            return;
        }
        shouldLaunchTrue = true;
        if(checkDownloads()){
            progressDialog.dismiss();
            launchDataActivity();
        }else {
            progressDialog.setTitle("Creating your 3D face...");
            progressDialog.setMessage(getString(R.string.message_wait));
            if(!isFinishing())
                progressDialog.show();
        }
    }

    public void launchDataActivity() {
        Log.e(LOGTAG, " ******** Launch ShopActivity in BodyMeasurementActivity  *******");
        //Intent intent = new Intent(this, BodyMeasurementActivity.class);
        Intent intent = new Intent(this, BodyMeasurementActivity.class);
        //intent.putExtra(FaceItem.EXTRA_PARAM_FACE_OBJ, faceItem);
        startActivity(intent);
        finish();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(progressDialog!=null)
             progressDialog.dismiss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(progressDialog!=null)
            progressDialog.dismiss();
    }

}
