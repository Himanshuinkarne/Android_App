package com.svc.sml.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.svc.sml.Database.User;
import com.svc.sml.Fragments.VideoFragment;
import com.svc.sml.InkarneAppContext;
import com.svc.sml.R;
import com.svc.sml.Utility.ConstantsFunctional;
import com.svc.sml.Utility.ConstantsUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class FaceSelectionActivity extends BaseActivity implements VideoFragment.OnVideoFragmentInteractionListener, View.OnClickListener {
    public static final String LOGTAG = "FaceSelectionActivity";
    public final static String EXTRA_PARAM_PIC_PATH = "UserPicImagePath";
    public final static String EXTRA_PARAM_PIC_SOURCE_TYPE = "PicSourceType";
    public final static String SHARED_PREF_COUNT_INSTRUCTION_VIDEO = "count_instruction_video";
    public final static String EXTRA_PARAM_SHOULD_SHOW_2nd_VIDEO = "EXTRA_PARAM_SHOULD_SHOW_2nd_VIDEO";
    public final static int RESULT_CODE_SELFIE = 101;
    public final static int RESULT_CODE_GALLERY_PIC = 102;
    private String CAM_PIC_NAME = "";
    private String picSource = "0";

    private AmazonS3Client s3;
    private String videoFilePath;

    private static final int REQ_CODE_PICK_IMAGE = 1888;
    private static final int REQUEST_IMAGE_CAPTURE = 1889;

    private LinearLayout btnTakeSelfie, btnTakeExisting;
    private TextView tvTick1;//, tvTick2, tvTick3;
    private String imagePath;
    private VideoView videoViewInScreen;
    private String videoPathInScreen,videoPathInScreen2;
    private int interval_animation_in_milli = ConstantsFunctional.TIME_INSTRUCTION_VIEW_ANIMATION_IN_MILLI;
    private int interval_animation_in_milli_after_video = 6000;
    protected VideoFragment videoFragment;

    private TextView tvInstrTop;
    private TableRow tRow1;//, tRow2, tRow3;
    private View[] arrayViews;
    private int countVideoLoop = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_selection);
        CAM_PIC_NAME="usercam-"+ System.currentTimeMillis()+".png";
        //tvInstrTop = (TextView) findViewById(R.id.tv_instruction_image_selection);
        //tRow1 = (TableRow) findViewById(R.id.tableRow1);
        //tRow2 = (TableRow) findViewById(R.id.tableRow2);
        //tRow3 = (TableRow) findViewById(R.id.tableRow3);
        videoViewInScreen = (VideoView) findViewById(R.id.vv_face_selection);
        btnTakeExisting = (LinearLayout) findViewById(R.id.con_btn_use_existing);
        btnTakeSelfie = (LinearLayout) findViewById(R.id.con_btn_take_selfie);
        btnTakeExisting.setOnClickListener(this);
        btnTakeSelfie.setOnClickListener(this);
        //tvTick1 = (TextView) findViewById(R.id.tv_image_selecton_tick1);
        //tvTick1.setTypeface(InkarneAppContext.getInkarneTypeOpensans());
        //tvTick2 = (TextView) findViewById(R.id.tv_image_selecton_tick2);
        //tvTick3 = (TextView) findViewById(R.id.tv_image_selecton_tick3);
        //tvTick3.setTypeface(InkarneAppContext.getInkarneTypeOpensans());
        //tvTick3.setTextSize(15);
        //tvTick3.setSingleLine(false);
        //boolean isFaceNotCreated =  User.getInstance().getDefaultFaceId() == null || User.getInstance().getDefaultFaceId().isEmpty()? true:false;
        if (User.getInstance().getmGender().equals("m")) {
            videoPathInScreen = ConstantsUtil.FILE_PATH_RAW_FOLDER + getPackageName() + "/" + R.raw.v_face_selection_light_instruction_loop_male;
//            if(isFaceNotCreated) {
//                videoPathInScreen = ConstantsUtil.FILE_PATH_RAW_FOLDER + getPackageName() + "/" + R.raw.v_face_selection_light_instruction_loop_male;
//                //videoPathInScreen2 = ConstantsUtil.FILE_PATH_RAW_FOLDER + getPackageName() + "/" + R.raw.v_face_selection_light_instruction_loop_male_1;
//            } else {
//                videoPathInScreen = ConstantsUtil.FILE_PATH_RAW_FOLDER + getPackageName() + "/" + R.raw.v_face_selection_light_instruction_loop_male_1;
//                //videoPathInScreen2 = videoPathInScreen;
//            }
        } else {
            videoPathInScreen = ConstantsUtil.FILE_PATH_RAW_FOLDER + getPackageName() + "/" + R.raw.v_face_selection_light_instruction_loop_female;
//            if(isFaceNotCreated) {
//                videoPathInScreen = ConstantsUtil.FILE_PATH_RAW_FOLDER + getPackageName() + "/" + R.raw.v_face_selection_light_instruction_loop_female;
//                //videoPathInScreen2 = ConstantsUtil.FILE_PATH_RAW_FOLDER + getPackageName() + "/" + R.raw.v_face_selection_light_instruction_loop_female_1;
//            }else{
//                videoPathInScreen = ConstantsUtil.FILE_PATH_RAW_FOLDER + getPackageName() + "/" + R.raw.v_face_selection_light_instruction_loop_female_1;
//                //videoPathInScreen2 = videoPathInScreen;
//            }
        }
//        if(isFaceNotCreated) {
//            //View[] array = new View[]{tvInstrTop, videoViewInScreen, tRow1, tRow2, tRow3, btnTakeExisting, btnTakeSelfie};
//            View[] array = new View[]{tvInstrTop, videoViewInScreen, tRow1, btnTakeExisting, btnTakeSelfie};
//            animateViews(interval_animation_in_milli, array);
//        }
        initVideo();
        playInScreenVideo();
    }

    @Override
    public void onResume() {
        super.onResume();
        playInScreenVideo();
        CAM_PIC_NAME="usercam-"+ System.currentTimeMillis()+".png";
        //playInScreenVideo();
//        if (videoViewInScreen.getVisibility() == View.VISIBLE || countVideoLoop > 0) {
//            playInScreenVideo();
//        }
    }

    @Override
    public void onPause() {
        super.onPause();
        hideVideoInScreen();
    }

    public void animateViews(final int milli, final View... views) {
        if (views.length == 0)
            return;
        for (View v : views)
            v.setVisibility(View.INVISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                int length = views.length;
                View viewNew[] = Arrays.copyOfRange(views, 1, length);
                if (views[0] instanceof VideoView) {
                    playInScreenVideo();
                    arrayViews = viewNew;
                    animationAfterVideo();
                } else {
                    views[0].setVisibility(View.VISIBLE);
                    animateViews(milli, viewNew);
                }
            }
        }, milli);
    }

    public void animationAfterVideo() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                videoPathInScreen = videoPathInScreen2;
                videoViewInScreen.stopPlayback();
                playInScreenVideo();
                int length = arrayViews.length;
                arrayViews[0].setVisibility(View.VISIBLE);
                View viewNew[] = Arrays.copyOfRange(arrayViews, 1, length);
                animateViews(interval_animation_in_milli, viewNew);
            }
        }, interval_animation_in_milli_after_video);
    }

    public void hideVideoInScreen() {
        if (videoViewInScreen != null && videoViewInScreen.isPlaying())
            videoViewInScreen.stopPlayback();
        videoViewInScreen.setVisibility(View.INVISIBLE);
    }

    public void initVideo(){
        videoViewInScreen.setVisibility(View.VISIBLE);
        videoViewInScreen.setVideoPath(videoPathInScreen);
        videoViewInScreen.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
            }
        });
        videoViewInScreen.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //if(videoPathInScreen.equals(videoPathInScreen2))
                mp.setLooping(true);
            }
        });
        //videoViewInScreen.start();
        videoViewInScreen.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                /*
                if(videoView.isPlaying())
                   videoView.pause();
                else
                videoView.resume();
                */
                return true;
            }
        });
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                videoViewInScreen.setVisibility(View.VISIBLE);
//            }
//        }, 120);
    }


    /* change */
    public void playInScreenVideo() {
        videoViewInScreen.setVisibility(View.VISIBLE);
        videoViewInScreen.start();
//        videoViewInScreen.setVideoPath(videoPathInScreen);
//        videoViewInScreen.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            public void onCompletion(MediaPlayer mp) {
//            }
//        });
//        videoViewInScreen.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                //if(videoPathInScreen.equals(videoPathInScreen2))
//                     mp.setLooping(true);
//            }
//        });
//        videoViewInScreen.start();
//        videoViewInScreen.setOnTouchListener(new View.OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                /*
//                if(videoView.isPlaying())
//                   videoView.pause();
//                else
//                videoView.resume();
//                */
//                return true;
//            }
//        });
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                videoViewInScreen.setVisibility(View.VISIBLE);
//            }
//        }, 120);

    }

    private String[] getVideoFragmentFilePaths(int code) {
        String[] videoFilePathFragment = null;
        //boolean shouldShowSecondInstruction = getIntent().getBooleanExtra(EXTRA_PARAM_SHOULD_SHOW_2nd_VIDEO, false);
        String basePath = "android.resource://" + getPackageName() + "/";
        if (User.getInstance().getmGender().equals("m")) {
            if (code == RESULT_CODE_GALLERY_PIC) {
                videoFilePathFragment = new String[1];
                videoFilePathFragment[0] = basePath + R.raw.v_face_selection_light_instruction_loop_female;
            } else {
                videoFilePathFragment = new String[1];
                //videoFilePathFragment[0] = basePath + R.raw.v_inst_take_pics_male;
                videoFilePathFragment[0] = basePath + R.raw.v_face_selection_light_instruction_loop_female;
                //videoFilePathFragment[1] = basePath + R.raw.v_inst_adjustpic_male;
                //videoFilePathFragment = basePath + R.raw.v_instruction_1_male;
            }
        } else {
            if (code == RESULT_CODE_GALLERY_PIC) {
                videoFilePathFragment = new String[1];
                videoFilePathFragment[0] = basePath + R.raw.v_face_selection_light_instruction_loop_female;

            } else {
                videoFilePathFragment = new String[1];
                videoFilePathFragment[0] = basePath + R.raw.v_face_selection_light_instruction_loop_female;
                //videoFilePathFragment[0] = basePath + R.raw.v_inst_take_pics_female;//todo
                //videoFilePathFragment[1] = basePath + R.raw.v_inst_adjustpic_female;
                //videoFilePathFragment = basePath + R.raw.v_instruction_1_female;
            }
        }
        return videoFilePathFragment;
    }

    private String[] getVideoFragmentTitle(int code) {
        String[] videoTitles = null;
        if (code == RESULT_CODE_GALLERY_PIC) {
            videoTitles = new String[1];
            //videoTitles[0] = getString(R.string.video_title_adjust_pic_to_outline);
            videoTitles[0] = getString(R.string.video_title_lightng_instruction);
        } else {
            videoTitles = new String[2];
            videoTitles[0] = getString(R.string.video_title_lightng_instruction);

            videoTitles[1] = getString(R.string.video_title_adjust_pic_to_outline);
            //videoFilePathFragment = basePath + R.raw.v_instruction_1_male;
        }
        return videoTitles;
    }


    protected void showVideoFragment(int code) {
        hideVideoInScreen();
        if (videoFragment == null) {
            //videoFragment = VideoFragment.newInstance(isExternalFiducialFeature());
            videoFragment = VideoFragment.newInstance(getVideoFragmentFilePaths(code), getVideoFragmentTitle(code), code);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            //ft.addToBackStack(null);
            //videoFragment.onAttach((Context)FaceSelectionActivity.this);
            ft.replace(R.id.container_video_fragment, videoFragment);
            ft.attach(videoFragment);
            ft.commit();

        } else if (videoFragment.isDetached()) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.attach(videoFragment);
            ft.show(videoFragment);
            ft.commit();
        }
    }

    protected void hideVideoFragment() {
        playInScreenVideo();
        if (videoFragment != null && !videoFragment.isHidden()) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            //ft.hide(videoFragment);
            ft.detach(videoFragment);
            ft.commit();
            videoFragment = null;
        }
    }

    @Override
    public void onVideoFragmentInteractionSkip(int code) {
        Log.d(LOGTAG, "onVideoFragmentInteractionSkip");
        hideVideoFragment();
        if (code == RESULT_CODE_GALLERY_PIC) {
            picSource = "0";
            takePictureFromGallery();
        } else if (code == RESULT_CODE_SELFIE) {
            picSource = "1";
            takePictureFromCamera();
        }
    }

    public void launchAdjustPicActivity(String imagePath) {
        Intent intent = new Intent(this, AdjustPicActivity.class);//
        intent.putExtra(EXTRA_PARAM_PIC_PATH, imagePath);
        intent.putExtra(EXTRA_PARAM_PIC_SOURCE_TYPE, picSource);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.con_btn_use_existing:
                //showVideoFragment(RESULT_CODE_GALLERY_PIC);
                takePictureFromGallery();
                break;
            case R.id.con_btn_take_selfie:
                //showVideoFragment(RESULT_CODE_SELFIE);
                takePictureFromCamera();
                break;
            default:
                break;
        }
    }

       private void takePictureFromGallery()
    {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        if (Build.VERSION.SDK_INT >= 19) {
//            // For Android versions of KitKat or later, we use a
//            // different intent to ensure
//            // we can get the file path from the returned intent URI
//            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
//            intent.addCategory(Intent.CATEGORY_OPENABLE);
//            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
//        } else {
//            intent.setAction(Intent.ACTION_GET_CONTENT);
//        }

        intent.setType("image/*");
        startActivityForResult(intent, REQ_CODE_PICK_IMAGE);
    }




    private void takePictureFromGallery3() {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= 19) {
            // For Android versions of KitKat or later, we use a
            // different intent to ensure
            // we can get the file path from the returned intent URI
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        } else {
            intent.setAction(Intent.ACTION_GET_CONTENT);
        }

        intent.setType("image/*");
        startActivityForResult(intent, REQ_CODE_PICK_IMAGE);
    }


    private void takePictureFromCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File out = Environment.getExternalStorageDirectory();
        out = new File(out, CAM_PIC_NAME);
        Log.d("SS", out.getAbsolutePath());
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(out));
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            imagePath = saveCameraImage(imageBitmap, true);
            //Modified by SS
            launchAdjustPicActivity(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + CAM_PIC_NAME);
        }

        if (requestCode == REQ_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            try {
                imagePath = getPath(uri);
                launchAdjustPicActivity(imagePath);
            } catch (URISyntaxException e) {
                Toast.makeText(FaceSelectionActivity.this, "Unable to get the file from the given URI.  See error log for details", Toast.LENGTH_LONG).show();
                Log.e("DEBUG", "Unable to upload file from the given uri", e);
            }
        }
    }
    private void takePictureFromCamera1() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

   // @Override
    public void onActivityResult1(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            //Bitmap imageBitmap = (Bitmap) extras.get("CAM_PIC_NAME");
            imagePath = saveCameraImage(imageBitmap, true);
            launchAdjustPicActivity(imagePath);
        }

        if (requestCode == REQ_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            try {
                imagePath = getPath(uri);
                launchAdjustPicActivity(imagePath);
            } catch (URISyntaxException e) {
                Toast.makeText(FaceSelectionActivity.this, "Unable to get the file from the given URI.  See error log for details", Toast.LENGTH_LONG).show();
                Log.e("DEBUG", "Unable to upload file from the given uri", e);
            }
        }
    }

    private String saveCameraImage(Bitmap finalBitmap, boolean isJpg) {
        String filePath = null;
        String root = ConstantsUtil.FILE_PATH_APP_ROOT;
        File myDir = new File(root + ConstantsUtil.FILE_PATH_VISAGE_SELFI_IMAGE);
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10;
        n = generator.nextInt(n);
        String fName = "Camera-image-" + n + ".png";
        File file = new File(myDir, fName);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            if (isJpg) {
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            } else {
                finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            }
            filePath = file.getAbsolutePath();
            out.flush();
            out.close();
            return filePath;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filePath;
    }


    @SuppressLint("NewApi")
    private String getPath(Uri uri) throws URISyntaxException {
        final boolean needToCheckUri = Build.VERSION.SDK_INT >= 19;
        String selection = null;
        String[] selectionArgs = null;
        if(needToCheckUri ) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return ConstantsUtil.FILE_PATH_APP_ROOT + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = null;
            try {
                cursor = getContentResolver().query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


    /*end*/


//    private void loadSelectionFragment() {
//        Fragment frag = new FaceSelectionFragment();
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.replace(R.id.con_fragment_faceselection, frag);
//        ft.addToBackStack(null);
//        ft.commit();
//    }
//
//    /* Interface Fragment methods */
//    public void onLooksFragmentInteraction(Uri uri) {
//
//    }
//
//    public void onLooksFragmentInteraction(String imagePath) {
//        Intent intent = new Intent(this, AdjustPicActivity.class);//
//        intent.putExtra(EXTRA_PARAM_PIC_PATH, imagePath);
//        intent.putExtra(EXTRA_PARAM_PIC_SOURCE_TYPE, "0");
//        startActivity(intent);
//    }

    private String getVideoFilePath1() {
        String videoFilePath = null;
        SharedPreferences settings = InkarneAppContext.getAppContext().getSharedPreferences("inkarne", 0);
        int count = settings.getInt(SHARED_PREF_COUNT_INSTRUCTION_VIDEO, 0);
        if (count > 2) {
            return null;
        }
        boolean shouldShowSecondInstruction = getIntent().getBooleanExtra(EXTRA_PARAM_SHOULD_SHOW_2nd_VIDEO, false);
        String basePath = "android.resource://" + getPackageName() + "/";
        if (User.getInstance().getmGender().equals("m")) {
            if (count == 0) {
                videoFilePath = basePath + R.raw.v_face_selection_light_instruction_loop_female;
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt(FaceSelectionActivity.SHARED_PREF_COUNT_INSTRUCTION_VIDEO, ++count);
                editor.commit();
            } else {
//                if (shouldShowSecondInstruction)
//                    videoFilePath = basePath + R.raw.v_inst_take_pics_male;
            }

        } else {
            if (count == 0) {
                videoFilePath = basePath + R.raw.v_face_selection_light_instruction_loop_female;
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt(FaceSelectionActivity.SHARED_PREF_COUNT_INSTRUCTION_VIDEO, ++count);
                editor.commit();
            } else {

//                if (shouldShowSecondInstruction)
//                    videoFilePath = basePath + R.raw.v_inst_take_pics_female;
            }
        }
        return videoFilePath;
    }

    public void AWSTest() throws IOException {
        // AmazonS3 s3 = new AmazonS3Client(credentialsProvider);
        // TransferUtility transferUtility = new TransferUtility(s3, getApplicationContext());
        AWSCredentials creden = new BasicAWSCredentials(ConstantsUtil.AWSAccessKey, ConstantsUtil.AWSSecretKey);
        AmazonS3Client s3Client = new AmazonS3Client(creden);
        String key = "inkarne/users/100/faces/1/AWSTest.html";
        String[] separated = key.split("/");
        String str_FilePathInDevice = "/sdcard/" + "/" + "AWSInkarne" + "/" + separated[separated.length - 1];
        Toast.makeText(this, separated[separated.length - 1], Toast.LENGTH_SHORT).show();
        File file = new File(str_FilePathInDevice);
        String str_Path = file.getPath().replace(file.getName(), "");
        File fileDir = new File(str_Path);
        try {
            fileDir.mkdirs();
        } catch (Exception ex1) {
        }
        S3Object object = s3Client.getObject(new GetObjectRequest(
                ConstantsUtil.AWSBucketName, key));

        BufferedReader reader = new BufferedReader(new InputStreamReader(
                object.getObjectContent()));
        Writer writer = new OutputStreamWriter(new FileOutputStream(file));

        while (true) {
            String line = reader.readLine();
            if (line == null)
                break;
            writer.write(line + "\n");
        }
        writer.flush();
        writer.close();
        reader.close();
    }


    private class GetFileListTask extends AsyncTask<Void, Void, Void> {
        // The list of objects we find in the S3 bucket
        private List<S3ObjectSummary> s3ObjList;
        // A dialog to let the user know we are retrieving the files
        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            // dialog = ProgressDialog.show(FaceSelectionActivity.this,"Please wait","");
        }

        @Override
        protected Void doInBackground(Void... inputs) {
            // Queries files in the bucket from S3.
            s3ObjList = s3.listObjects(ConstantsUtil.AWSBucketName).getObjectSummaries();
            //transferRecordMaps.clear();
            for (S3ObjectSummary summary : s3ObjList) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("key", summary.getKey());
                //transferRecordMaps.add(map);
                Log.d("debug", "AWS Key : " + summary.getKey());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //dialog.dismiss();
            //simpleAdapter.notifyDataSetChanged();
        }
    }
}
