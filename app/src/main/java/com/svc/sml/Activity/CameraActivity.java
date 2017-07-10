package com.svc.sml.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;

import com.svc.sml.R;
import com.svc.sml.View.CapturePreview;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

public class CameraActivity extends Activity {
    private static final String LOGTAG = "CameraActivity";
    private static final int REQUEST_CODE = 10;
    private String mCurrentPicPath;
    private CapturePreview capturePreview;
    public  Bitmap mBitmap;
    private String filePathAdjustedPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        capturePreview = (CapturePreview)findViewById(R.id.capture_preview);
    }


    public void checkDrawOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_CODE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    // continue here - permission was granted
                }
            }
        }
    }

    public void takePicture(){
        Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {

                BitmapFactory.Options options = new BitmapFactory.Options();
                mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
                saveImage(mBitmap);
            }
        };
        capturePreview.mCamera.takePicture(null, null, mPictureCallback);
    }

    public void saveImage(Bitmap finalBitmap) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/inkarne1/userpics");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fName = "Image-"+ n +".png";
        File file = new File (myDir, fName);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            filePathAdjustedPic = file.getAbsolutePath();
            out.flush();
            out.close();
            launchFiducial();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void launchFiducial(){
        Intent intent = new Intent(CameraActivity.this, FiducialActivity.class);
        intent.putExtra(FaceSelectionActivity.EXTRA_PARAM_PIC_PATH,filePathAdjustedPic );
        intent.putExtra(FaceSelectionActivity.EXTRA_PARAM_PIC_SOURCE_TYPE,"1" );
        startActivity(intent);
        finish();
    }
}
