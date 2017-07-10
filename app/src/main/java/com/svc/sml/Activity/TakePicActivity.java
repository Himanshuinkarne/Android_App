package com.svc.sml.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.svc.sml.R;
import com.svc.sml.Utility.ConstantsUtil;

import java.io.File;
import java.io.IOException;

public class TakePicActivity extends Activity {
    private static final int REQUEST_CODE = 10;
    private static String LOGTAG = TakePicActivity.class.getSimpleName();
    Button btnPic;
    String foldername = "inkarne/pics/userpic";
    File newFolder = new File(ConstantsUtil.FILE_PATH_APP_ROOT, foldername);
    int i =1;
    String mCurrentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;
//    static{ System.loadLibrary("opencv_java3"); }
    public boolean SuccessStatus = false;
    ImageView r1;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_takepic);

        r1 = (ImageView) findViewById(R.id.r1);

        btnPic = (Button) findViewById(R.id.btnPic);

        if (!newFolder.exists()) {newFolder.mkdir();}

        btnPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (SuccessStatus == true) {
                    return;
                }

                if (i==1){
                    dispatchTakePictureIntent();
                    i++;
                    btnPic.setText("Click to proceed");

                    Bitmap bmp= BitmapFactory.decodeResource(getApplicationContext().getResources(),
                            R.drawable.share_option_pic);

                    r1.setImageBitmap(bmp);
                }
                else{
                    SuccessStatus = true;
//                    Intent intent = new Intent(TakePicActivity.this, FiducialActivity.class);
//                    intent.putExtra(FaceSelectionActivity.EXTRA_PARAM_PIC_PATH,mCurrentPhotoPath );
//                    intent.putExtra(FaceSelectionActivity.EXTRA_PARAM_PIC_SOURCE_TYPE,"1" );
//                    startActivity(intent);
//                    finish();

                    Intent intent = new Intent(TakePicActivity.this, AdjustPicActivity.class);
                    intent.putExtra(FaceSelectionActivity.EXTRA_PARAM_PIC_PATH,mCurrentPhotoPath );
                    intent.putExtra(FaceSelectionActivity.EXTRA_PARAM_PIC_SOURCE_TYPE,"1" );
                    startActivity(intent);
                    finish();
                }
            }
        });
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
                    addLayer();
                }
            }
        }
    }

    private void addLayer(){

        FrameLayout.LayoutParams fp = new FrameLayout.LayoutParams (FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT,
                Gravity.CENTER );
        FrameLayout mView = new FrameLayout (this);

        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setAdjustViewBounds(true);
        mView.addView(imageView, fp);
        imageView.setImageResource(R.drawable.mask_adjustpic);

        //mView = new HUDView(this);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.RIGHT | Gravity.TOP;
        params.setTitle("Load Average");
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.addView(mView, params);

    }

    private void dispatchTakePictureIntent() {
        //checkDrawOverlayPermission();
        //addLayer();
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
            }
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {

        //Note to HM - pls modify the below pic name as below and transfer to S3 along with sd card
        /*STEP 3. S3 upload of user picture - bucket & key name format:
        You need upload to following location & the following nomenclature:
        BucketName: inkarnestore
        KeyName: inkarne/users/[User_ID]/pictures/[User_ID_CurrentDateTimestamp.jpg]
        Note: Please autogenerate the Picture_File_Name as [User_ID]_DateTimestamp.jpg. This will ensure no data integrity issues (with file names) for multiple pictures & face avatars scenario.
        */

        String imageFileName = "upic"+".jpg"; //modify file nomenclature as per guidance above
        File storageDir = newFolder;
        File image = new File(storageDir,imageFileName);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
}

