package com.svc.sml.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.svc.sml.R;
import com.svc.sml.Utility.ConstantsUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public class CameraViewActivity extends Activity implements SurfaceHolder.Callback {
    private static final String LOGTAG = "CameraViewActivity";
    private static final int REQUEST_CODE = 10;
    private String mCurrentPicPath;
    public Bitmap mBitmap;
    private String filePathAdjustedPic;

    private Camera camera;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private boolean cameraview = false;
    private LayoutInflater inflater = null;
    public int cameraType = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private int cameraOrientaion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_view);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getWindow().setFormat(PixelFormat.UNKNOWN);
        surfaceView = (SurfaceView) findViewById(R.id.cameraview);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        //camera.

//        inflater = LayoutInflater.from(getBaseContext());
//        View view = inflater.inflate(R.layout.overlay, null);
//        FrameLayout.LayoutParams layoutParamsControl= new LayoutParams(LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT);
//        this.addContentView(view, layoutParamsControl);
    }

    private Camera openCamera(int cameraType) {
        int cameraCount = 0;
        Camera cam = null;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            //if ( cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT  ) {
            if (cameraInfo.facing == cameraType) {
                try {
                    //cameraType = Camera.CameraInfo.CAMERA_FACING_FRONT;
                    cam = Camera.open(cameraType);
                    break;
                } catch (RuntimeException e) {
                    Log.e(LOGTAG, "Camera failed to open: " + e.getLocalizedMessage());
                }
            } else {
                //cameraType = Camera.CameraInfo.CAMERA_FACING_BACK;
                cam = Camera.open(cameraType);
            }
        }

        return cam;
    }

    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            //c = openFrontFacingCamera();
        } catch (Exception e) {
        }
        return c;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // TODO Auto-generated method stub
        if (cameraType == Camera.CameraInfo.CAMERA_FACING_FRONT)
            cameraOrientaion = 270;
        else
            cameraOrientaion = 90;
        camera.setDisplayOrientation(cameraOrientaion);
        if (cameraview) {
            camera.stopPreview();
            cameraview = false;
        }

        if (camera != null) {
            try {
                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();
                cameraview = true;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        camera = openCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
        //camera = Camera.open();
        //camera.enableShutterSound(true);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        camera.stopPreview();
        camera.release();
        camera = null;
        cameraview = false;
    }



    public void takePicture() {

        Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                //int mRotation = getCameraDisplayOrientation();
                FileOutputStream outStream = null;
                try {
                    String root = Environment.getExternalStorageDirectory().toString();
                    File myDir = new File(root + ConstantsUtil.FILE_PATH_VISAGE_SELFI_IMAGE);
                    myDir.mkdirs();
                    Random generator = new Random();
                    int n = 10;
                    n = generator.nextInt(n);
                    String fName = "type2_Image-" + n + ".jpeg";
                    File file = new File(myDir, fName);
                    //outStream = new FileOutputStream("/sdcard/Image.jpg");
                    outStream = new FileOutputStream(file);
                    outStream.write(data);
                    outStream.flush();
                    outStream.close();

                    filePathAdjustedPic = file.getAbsolutePath();
//                    BitmapFactory.Options options = new BitmapFactory.Options();
//                    options.inPreferredConfig = Bitmap.Config.RGB_565;
//                    mBitmap = BitmapFactory.decodeFile(filePathAdjustedPic, options);
                    rotatePics();
                    launchFiducial();

                } catch (FileNotFoundException e) {
                    Log.d("CAMERA", e.getMessage());
                } catch (IOException e) {
                    Log.d("CAMERA", e.getMessage());
                }

//                BitmapFactory.Options options = new BitmapFactory.Options();
//                mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
//                saveImage(mBitmap);
            }
        };

        Camera.Parameters parameters = camera.getParameters();
        int orient =cameraOrientaion;
        if(cameraOrientaion>180)
            orient = cameraOrientaion-180;
        parameters.setRotation(orient); //set rotation to save the picture
        //camera.setDisplayOrientation(cameraOrientaion); //set the rotation for preview camera
        camera.setParameters(parameters);
        camera.takePicture(null, null, mPictureCallback);
    }


    private void launchFiducial() {
        Intent intent = new Intent(CameraViewActivity.this, FiducialActivity.class);
        intent.putExtra(FaceSelectionActivity.EXTRA_PARAM_PIC_PATH, filePathAdjustedPic);
        intent.putExtra(FaceSelectionActivity.EXTRA_PARAM_PIC_SOURCE_TYPE, "1");
        startActivity(intent);
        finish();
    }

    public void onPictureTaken(byte[] data, Camera camera) {
        //decode the data obtained by the camera into a Bitmap
        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream("/sdcard/Image.jpg");
            outStream.write(data);
            outStream.close();
        } catch (FileNotFoundException e) {
            Log.d("CAMERA", e.getMessage());
        } catch (IOException e) {
            Log.d("CAMERA", e.getMessage());
        }
    }

    public void btnHandlerTakePicture(View v) {
        takePicture();
    }

    public void btnHandlerCameraChange(View v) {
        if (cameraType == Camera.CameraInfo.CAMERA_FACING_BACK) {
            cameraType = Camera.CameraInfo.CAMERA_FACING_FRONT;
        } else {
            cameraType = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
    }

    //--------------------------------------------------------------------------

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public void saveImage(Bitmap finalBitmap) {
        String root = ConstantsUtil.FILE_PATH_APP_ROOT;
        File myDir = new File(root + ConstantsUtil.FILE_PATH_VISAGE_SELFI_IMAGE);
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fName = "Image-" + n + ".jpeg";
        File file = new File(myDir, fName);
        if (file.exists()) file.delete();
        try {
            Log.d(LOGTAG, "Uploaded pic path: " + file.getAbsolutePath());
            Log.e(LOGTAG, "Uploaded pic path: " + file.getAbsolutePath());
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            filePathAdjustedPic = file.getAbsolutePath();
            out.flush();
            out.close();

            // rotatePics();
            launchFiducial();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void rotatePics() {
        ExifInterface ei = null;
        try {
            ei = new ExifInterface(filePathAdjustedPic);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap finalBitmap = BitmapFactory.decodeFile(filePathAdjustedPic, options);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotateImage(finalBitmap, 90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotateImage(finalBitmap, 180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotateImage(finalBitmap, 270);
                break;
        }
        FileOutputStream out = null;
        try {
//            File file = new File(filePathAdjustedPic);
//            if (file.exists()) file.delete();
            //filePathAdjustedPic = "2"+filePathAdjustedPic;
            out = new FileOutputStream(filePathAdjustedPic);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Bitmap retVal;

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        retVal = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);

        return retVal;
    }

}
