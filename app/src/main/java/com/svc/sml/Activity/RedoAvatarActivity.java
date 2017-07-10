package com.svc.sml.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.Tracker;
import com.svc.sml.Database.ComboData;
import com.svc.sml.Database.User;
import com.svc.sml.Fragments.BMAvatarFragment;
import com.svc.sml.Fragments.BMFragment;
import com.svc.sml.Graphics.IRenderer;
import com.svc.sml.Graphics.PLYLoadMismatch;
import com.svc.sml.Helper.AssetDownloader;
import com.svc.sml.Helper.DataManager;
import com.svc.sml.InkarneAppContext;
import com.svc.sml.Model.BaseAccessoryItem;
import com.svc.sml.Model.FaceItem;
import com.svc.sml.R;
import com.svc.sml.ShopActivity;
import com.svc.sml.Utility.Connectivity;
import com.svc.sml.Utility.ConstantsUtil;
import com.svc.sml.View.LoadingView;

import java.io.File;
import java.util.ArrayList;

import static com.svc.sml.InkarneAppContext.getSPValue;

public class RedoAvatarActivity extends BaseActivity implements BMFragment.OnFragmentInteractionListener, View.OnClickListener {
    private final static String LOGTAG = "RedoAvatarActivity";
    public final static String EXTRA_PARAM_FACE_ID = "EXTRA_PARAM_FACE_ID";
    public final int GL_INDEX_TORSO = 0;

    private GLSurfaceView mGLView;
    private GLViewRenderer myRenderer;
    private LoadingView pbGLView;
    private FaceItem currentFaceItem;
    private FaceItem prevFaceItem;
    private FaceItem defaultFaceItem;
    private String prevDefaultFaceId = "";
    private ComboData comboDataPassed = null;

    private ArrayList<FaceItem> listFaceItem = new ArrayList<>();
    private Button btnNext;
    private View containerNextBtn;
    private int currentFaceIndex = 0;
    private int prevFaceIndex = -1;
    private FrameLayout conInstOverlayAddFace;

    //private LinearLayout btnAddFace;
    private ImageButton ibAddFace;
    private Button ibEditFace;
    private ImageButton btnBack;
    public TextView btnSetDefault;
    private ImageButton btnDeleteFace;
    public ImageButton btnForward;
    public ImageButton btnBackward;
    private BMAvatarFragment bmAvatarFragment;
    private int countRetry = 0;
    private int countReloginRetry = 0;
    private boolean isRenderComplete = false;
    private Tracker mTracker;

    private ProgressDialog pbDialogComboLoad;
    private Button btnChangeGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redo_avatar);
        bmAvatarFragment = (BMAvatarFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentBMAvatar);
        pbDialogComboLoad = getProgressDialogTransparent();
        shouldShowIns();
        mGLView = (GLSurfaceView) findViewById(R.id.surfaceviewclass);
        pbGLView = (LoadingView) findViewById(R.id.loading_view);
        btnNext = (Button) findViewById(R.id.btn_shared_next);
        //btnNext.setTypeface(InkarneAppContext.getInkarneTypeFaceFutura());
        btnNext.setText("Update Body Measurements");
        containerNextBtn = findViewById(R.id.shared_layout_bottom_view);
        ibAddFace = (ImageButton) findViewById(R.id.ib_avatar_addface);
        ibAddFace.setOnClickListener(this);
        ibEditFace = (Button) findViewById(R.id.btn_avatar_edit);
        ibEditFace.setOnClickListener(this);
//        btnAddFace = (LinearLayout) findViewById(R.id.con_btn_avatar_addface);
//        btnAddFace.setOnClickListener(this);
        btnBack = (ImageButton) findViewById(R.id.btn_back_avatar);
        btnBack.setOnClickListener(this);
        btnSetDefault = (TextView) findViewById(R.id.btn_avatar_set_default_face);
        btnSetDefault.setOnClickListener(this);
        btnDeleteFace = (ImageButton) findViewById(R.id.ib_avatar_delete);
        btnDeleteFace.setOnClickListener(this);
        btnForward = (ImageButton) findViewById(R.id.ib_avatar_forward);
        btnForward.setOnClickListener(this);
        btnBackward = (ImageButton) findViewById(R.id.ib_avatar_backword);
        btnBackward.setOnClickListener(this);

        btnChangeGender = (Button)findViewById(R.id.btn_change_gender);
        btnChangeGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeGenderAlert();
            }
        });

        if (User.getInstance().isMale()) {
            ibAddFace.setImageResource(R.drawable.add_face_male);
        } else {
            ibAddFace.setImageResource(R.drawable.add_face_female);
        }
//        if (User.getInstance().isMale()) {
//            ibEditFace.setImageResource(R.drawable.edit_face_male);
//        } else {
//            ibEditFace.setImageResource(R.drawable.edit_face_female);
//        }
        GATrackActivity(LOGTAG);
        initStart();
        loadGLView();
    }

    public void forceCrash() {
        throw new RuntimeException("This is a Test crash");
    }

    public ProgressDialog getProgressDialogTransparent() {
        ProgressDialog p = new ProgressDialog(this, R.style.AppCompatAlertTransparentDialogStyle);
        p.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        p.setTitle("");
        p.setCanceledOnTouchOutside(false);
        //p.setCancelable(false);
        return p;
    }

    private void shouldShowIns() {
        SharedPreferences sh = getSharedPreferences("inkarne", MODE_PRIVATE);
        int count = sh.getInt(ConstantsUtil.SETTING_KEY_COUNT_INSTRUCTION_ADD_FACE_SHOWN, 0);
        if (count < 2) {
            conInstOverlayAddFace = (FrameLayout) findViewById(R.id.con_inst_overlay_addface);
            conInstOverlayAddFace.setVisibility(View.VISIBLE);
            conInstOverlayAddFace.setOnClickListener(this);
        } else {
            //conInstOverlayAddFace.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLView.onResume();

        myRenderer.onResume();
        if (((InkarneAppContext) this.getApplication()).wasInBackground) {
            myRenderer.checkObjContainer();
            //Do specific came-here-from-background code
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        isRenderComplete = true;
    }

    @Override
    public void onBackPressed() {
        // your code.
        if (backBtnPressed())
            super.onBackPressed();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!backBtnPressed()) {
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initStart() {
        prevDefaultFaceId = User.getInstance().getDefaultFaceId();
        defaultFaceItem = User.getInstance().getDefaultFaceItem();
        comboDataPassed = (ComboData) getIntent().getSerializableExtra("comboData");

        String faceId = getIntent().getStringExtra(EXTRA_PARAM_FACE_ID);
        listFaceItem = (ArrayList<FaceItem>) dataSource.getAvatars();
        if (faceId == null) {
            faceId = prevDefaultFaceId;
        }
        if (faceId != null) {
            int i = 0;
            for (FaceItem item : listFaceItem) {
                if (item.getFaceId().equals(faceId)) {
                    currentFaceIndex = i;
                    break;
                }
                i++;
            }
        }
        if (listFaceItem != null && listFaceItem.size() != 0) {
            currentFaceItem = listFaceItem.get(currentFaceIndex);
            if (faceId != null && !faceId.isEmpty()) {
                changeDefaultFace();
            }
            updateUIForFace(currentFaceIndex);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //getIntent() should always return the most recent
        setIntent(intent);
        initStart();
        myRenderer.changeObjects(currentFaceItem);
    }


    private void showConfirmationDialog(String title, int message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setTitle(title);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteFace();
                dialog.dismiss();

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        if (!isFinishing())
            dialog.show();
    }

    private void deleteFace() {
        Log.d("AAA", "B: " + listFaceItem.size());
        updateToServerDeleteFace(currentFaceItem.getFaceId());
        int index = currentFaceIndex;
        listFaceItem.remove(index);
        deleteFaceData(currentFaceItem);
        dataSource.delete(currentFaceItem);

//        if (currentFaceIndex != 0 && currentFaceIndex == listFaceItem.size() - 1) {
//            currentFaceIndex -= 1;
//        }
//        if(index < 0) {//TODO
//            index = 0;
//        }
// else if (currentFaceIndex > listFaceItem.size() - 1) {
//            currentFaceIndex = listFaceItem.size() - 1;
//        }

        if (index > listFaceItem.size() - 1) {
            index = listFaceItem.size() - 1;
        }
        currentFaceIndex = index;
        currentFaceItem = listFaceItem.get(currentFaceIndex);
        updateUIForFace(currentFaceIndex);
        Log.d("AAA", "deleteFace - faceId: " + currentFaceItem.getFaceId());
        myRenderer.changeObjects(currentFaceItem);
    }

    private void deleteFaceData(FaceItem face) {
        if (!face.getFaceId().equalsIgnoreCase("1")) {
            File file = new File(ConstantsUtil.FILE_PATH_APP_ROOT+ConstantsUtil.FILE_PATH_AWS_KEY_ROOT + User.getInstance().getmUserId() + "/faces/" + face.getFaceId());
            Log.d(LOGTAG, " **deleteFaceData "+ file.getAbsolutePath());
            ConstantsUtil.deleteDirectory(file);
            if(face.getImageSavedFilePath() != null && !face.getImageSavedFilePath().isEmpty()) {
                File fileSelfie = new File(face.getImageSavedFilePath());
                ConstantsUtil.deleteDirectory(fileSelfie);
            }
        }
    }

    private void changeDefaultFace() {
        defaultFaceItem = currentFaceItem;
        btnSetDefault.setEnabled(false);
        btnDeleteFace.setEnabled(false);
        btnSetDefault.setText("YOUR DEFAULT AVATAR");
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            btnSetDefault.setTextColor(getResources().getColor(R.color.tcolor, getTheme()));
//        }else {
//            btnSetDefault.setTextColor(getResources().getColor(R.color.tcolor));
//        }
        //btnSetDefault.setTextColor(getResources().getColor(R.color.tcolor));
        btnDeleteFace.setVisibility(View.INVISIBLE);
    }

    private void updateUIForFace(int currentIndex) {
        Log.d(LOGTAG, " currentIndex :" + currentIndex + "  size: " + listFaceItem.size());
        if (currentIndex <= 0) {
            btnBackward.setVisibility(View.INVISIBLE);
            Log.d(LOGTAG, "btnBackward hide :");
        } else {
            btnBackward.setVisibility(View.VISIBLE);
            Log.d(LOGTAG, "btnBackward show :");
        }
        if (currentIndex >= listFaceItem.size() - 1) {
            btnForward.setVisibility(View.INVISIBLE);
            Log.d(LOGTAG, "btnForward hide :");
        } else {
            btnForward.setVisibility(View.VISIBLE);
            Log.d(LOGTAG, "btnForward show :");
        }
        FaceItem curFaceItem = listFaceItem.get(currentIndex);
        if (curFaceItem.getFaceId().equals(defaultFaceItem.getFaceId())) {
            btnSetDefault.setEnabled(false);
            btnSetDefault.setText("YOUR DEFAULT AVATAR");
            btnDeleteFace.setEnabled(false);
            btnDeleteFace.setVisibility(View.INVISIBLE);
        } else {
            btnSetDefault.setEnabled(true);
            btnSetDefault.setText("SET AS DEFAULT AVATAR");
            btnDeleteFace.setEnabled(true);
            btnDeleteFace.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (e.getY() < mGLView.getHeight() || e.getY() > mGLView.getY()) {
            myRenderer.onTouchEvent(e);
            mGLView.requestRender();
        }
        return super.onTouchEvent(e);
    }

    /*GLView  */
    public void loadGLView() {
        myRenderer = new GLViewRenderer(this, mGLView, ConstantsUtil.GL_INDEX_TOTAL, User.getInstance().getmGender());
        mGLView.setEGLContextClientVersion(3);
        mGLView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);


        /*
        glView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        glView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        glView.setBackgroundResource(R.drawable.bg_default);
        glView.setZOrderOnTop(true);
        */

        mGLView.setRenderer(myRenderer);

        /*
        view.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        myRenderer = new GLViewRenderer(this, 6);
        view.setRenderer(myRenderer);

        //view.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        view.getHolder().setFormat(PixelFormat.RGBA_8888);
        view.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        view.getHolder().setFormat(PixelFormat.RGBA_8888);
        view.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        view.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        view.setZOrderMediaOverlay(true);
        */
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private boolean backBtnPressed() {
        if (!isRenderComplete) {
            return false;
        }
        memoryCleanup();
        if (!Connectivity.isConnected(this)) {
            launchShopActivity();
            //finish();
        } else {
            updateIfDefaultFaceChanged();
            if (InkarneAppContext.isDefaultFaceChanged()) {
                launchDataActivity();
            } else {
                launchShopActivity();
                //finish();
            }
        }
        return true;
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case  R.id.btn_change_gender:{
//                showChangeGenderAlert();
//            }
//            break;
            case R.id.con_inst_overlay_addface: {
                conInstOverlayAddFace.setVisibility(View.GONE);
               // InkarneAppContext.saveSPValue(ConstantsUtil.SETTING_KEY_COUNT_INSTRUCTION_ADD_FACE_SHOWN, getSPValue(ConstantsUtil.SETTING_KEY_COUNT_INSTRUCTION_ADD_FACE_SHOWN,0)+1);
            }
            break;
            case R.id.btn_back_avatar: {
                backBtnPressed();
            }
            break;
            case R.id.ib_avatar_addface: {
                int c = InkarneAppContext.getSPValue(ConstantsUtil.SETTING_KEY_COUNT_INSTRUCTION_ADD_FACE_SHOWN, 0);
                if (!Connectivity.isConnected(this)) {
                    Toast.makeText(this, ConstantsUtil.MESSAGE_TOAST_NETWORK_RESPONSE_FAILED, Toast.LENGTH_SHORT).show();
                } else {
                    trackEvent("AddFace", "", "");
                    InkarneAppContext.isAddFaceForRedoAvatar = true;
                    if(c<2){//todo
                        InkarneAppContext.saveSPValue(ConstantsUtil.SETTING_KEY_COUNT_INSTRUCTION_ADD_FACE_SHOWN, getSPValue(ConstantsUtil.SETTING_KEY_COUNT_INSTRUCTION_ADD_FACE_SHOWN,0)+2);
                        Intent i1 = new Intent(RedoAvatarActivity.this, InstructionActivity.class);
                        startActivity(i1);
                    }else {
                        Intent i1 = new Intent(RedoAvatarActivity.this, FaceSelectionActivity.class);
                        startActivity(i1);
                    }
                }
            }
            break;

            case R.id.btn_avatar_set_default_face: {
                if (!Connectivity.isConnected(this)) {
                    //showAlert("","This feature is not available offline");
                    //Toast.makeText(this, "Could not update default face.Please check your network connection", Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, ConstantsUtil.MESSAGE_TOAST_NETWORK_RESPONSE_FAILED, Toast.LENGTH_SHORT).show();
                } else {
                    trackEvent("setDefaultFace", "", "");
                    changeDefaultFace();
                }
            }
            break;
            case R.id.ib_avatar_delete: {
                showConfirmationDialog("", R.string.warning_delete_face);
            }
            break;

            case R.id.btn_avatar_edit: {
                if(currentFaceItem.getFeducialPoints()!=null && !currentFaceItem.getFeducialPoints().isEmpty()
                        && currentFaceItem.getImageSavedFilePath()!= null && !currentFaceItem.getImageSavedFilePath().isEmpty()
                        && ConstantsUtil.checkFileExist(currentFaceItem.getImageSavedFilePath())) {
                    Intent intent = new Intent(RedoAvatarActivity.this, FiducialActivityEdit.class);
                    //FaceItem faceItem = (FaceItem) getIntent().getSerializableExtra("faceItem");
                    intent.putExtra("faceItem", currentFaceItem);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(),"This avatar could not be edited",Toast.LENGTH_SHORT).show();
                }
            }
            break;
            case R.id.ib_avatar_backword: {
                myRenderer.changeState(-1);
            }
            break;
            case R.id.ib_avatar_forward: {
                myRenderer.changeState(1);
            }
            break;

            default:
                break;
        }
    }

    /********* Change Gender ********/

    private void showChangeGenderAlert(){
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Change gender")
                .setMessage("Change of gender will delete all existing data")
                .setCancelable(false)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        loginGustUser();

                    }
                }).create();
        if (!isFinishing()) {
            builder.show();
        }
    }

    private void loginGustUser(){
        final String firstName = "Guest";
//        String facebookId = "";
        final String lastName = "User";
//        String emailId = "NA";
//        String personPhotoUrl = "NA";
        String loginType = "Guest";
        String url = InkarneAppContext.getSPValue("login_url","");
        String gender = "m";
        String fb_id = "0000";
       if(User.getInstance().isMale()){
           gender = "f";
           fb_id = "0001";
       }else{
           gender = "m";
           fb_id = "0000";
       }

        url = url.replace("gender",gender);
        url = url.replace("facebook_id",fb_id);

        final String gender1 = gender;
        DataManager.getInstance().requestCreateUser(url, new DataManager.OnResponseHandlerInterface() {
            @Override
            public void onResponse(Object obj) {
                resetDBAndData();
                String userId = (String) obj;
                Log.d(LOGTAG, "Registration Successful userId :" + userId);
                User user = new User();
                user.setmUserId(userId);
                user.setmFirstName(firstName);
                user.setmLastName(lastName);
                //user.setmMobileNumber("NA");
                user.setmGender(gender1);
                user.setDob_dd_mmm_yyyy("1" + "-" + "1" + "-" + "1900");
                user.setDob_day(1);
                user.setDob_month(1);
                user.setDob_year(1900);
                user.setEmailId("NA");
                user.setmMobileNumber("NA");
                user.setThumbUrl("NA");
                user.setmPIN(" ");
                user = InkarneAppContext.getDataSource().create(user);
                User.setInstance(user);
                User.getInstance().saveUserId(user.getmUserId());

                launchBMActivity();
                //pbRegistration.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onResponseError(String errorMessage, int errorCode) {
                //pbRegistration.setVisibility(View.INVISIBLE);
                countReloginRetry++;
                if (countReloginRetry == 1) {
                    if (errorCode == DataManager.CODE_DATA_MANAGER_NETWORK_ERROR) {
                        Toast.makeText(getApplicationContext(), getString(R.string.message_network_download_title), Toast.LENGTH_SHORT).show();
                    }
                }
                if (countReloginRetry < ConstantsUtil.COUNT_RETRY_SERVICE_CRITICAL) {
                    loginGustUser();
                }
            }
        });
    }

    private void resetDBAndData() {
        if (InkarneAppContext.getDataSource() != null)
            InkarneAppContext.getDataSource().close();
        getApplicationContext().deleteDatabase("inkarne.db");
        File inkarneDir = new File(ConstantsUtil.FILE_PATH_APP_ROOT+"inkarne" );
        ConstantsUtil.deleteDirectory(inkarneDir);
        //getSharedPreferences("inkarne", 0).edit().clear().commit();
        //inkarneDir.delete();
    }

    private void launchBMActivity() {
        //Intent myIntent = new Intent(getActivity(), FaceSelectionActivity.class);
        Intent myIntent = new Intent(RedoAvatarActivity.this, BodyMeasurementActivity.class);
        //myIntent.putExtra("key", ""); //Optional parameters
        InkarneAppContext.setIsDefaultFaceChanged(true);
        startActivity(myIntent);
        finish();
    }

    /**********/
    public void updateToServerDeleteFace(String faceId) {
        //URL_METHOD_DELETE_VIDEO
        String url = ConstantsUtil.URL_BASEPATH + ConstantsUtil.URL_METHOD_DELETE_FACE + User.getInstance().getmUserId()
                + "/" + faceId;
        DataManager.getInstance().updateMethodToServer(url, "deleteFace", new DataManager.OnResponseHandlerInterface() {
            @Override
            public void onResponse(Object obj) {

            }

            @Override
            public void onResponseError(String errorMessage, int errorCode) {

            }
        });
    }

    public void nextBtnClickHandler(View v) {
        memoryCleanup();

        if (!Connectivity.isConnected(this)) {
            //showAlert("","This feature is not available offline");
            //Toast.makeText(this, "Could not update body.Please check your network connection", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, ConstantsUtil.MESSAGE_TOAST_NETWORK_RESPONSE_FAILED, Toast.LENGTH_SHORT).show();
            launchShopActivity();
            //finish();
        } else {
            updateIfDefaultFaceChanged();
            boolean isBMChanged = updateUserBM(bmAvatarFragment.bmModel);
            if (isBMChanged) {
                defaultFaceItem.setPbId("");
                dataSource.create(defaultFaceItem);
                User.getInstance().setDefaultFaceItem(defaultFaceItem);
                User.getInstance().setDefaultFaceId(defaultFaceItem.getFaceId());
                dataSource.create(User.getInstance());
            }
            if (InkarneAppContext.isDefaultFaceChanged() || isBMChanged) {
                deleteOnFaceOrBodyChange(defaultFaceItem.getFaceId());
                launchDataActivity();
            } else {
                launchShopActivity();
                //finish();
            }
        }
    }

    private void resetComboForPBOrFaceChange() {

    }

    public void launchDataActivity() {
        InkarneAppContext.refreshDataAsync();
        Log.e(LOGTAG, " ******** Launch ShopActivity in DataActivity  *******");
        Intent intent = new Intent(RedoAvatarActivity.this, DataActivity.class);
        if (comboDataPassed != null) {
            intent.putExtra("comboDataId", comboDataPassed.getCombo_ID());
        }
        startActivity(intent);
        finish();
    }

    public void launchShopActivity() {
        Log.d(LOGTAG, "Launch ShopActivity in RedoAvatarActivity");
        Intent intent = new Intent(RedoAvatarActivity.this, ShopActivity.class);
        intent.putExtra("comboData", comboDataPassed);
        ComboData comboData = (ComboData) getIntent().getSerializableExtra("comboData");
        if (comboData != null)
            intent.putExtra("comboData", comboData);
        if (InkarneAppContext.isDefaultFaceChanged()) {
            intent.putExtra("isBodyORFaceChanged", true);
        }
        startActivity(intent);
        //finish();
    }

    public void deleteOnFaceOrBodyChange(final String faceId) {
        dataSource.deleteComboDetailForFaceChange(faceId);
        dataSource.deleteComboDetailLikeForFaceChange(faceId);
    }

    private void updateIfDefaultFaceChanged() {
        if (defaultFaceItem != null && !defaultFaceItem.getFaceId().equals(prevDefaultFaceId)) {
            deleteOnFaceOrBodyChange(defaultFaceItem.getFaceId());
            InkarneAppContext.saveSettingIsDefaultFaceChanged(true);
            User.getInstance().setDefaultFaceId(defaultFaceItem.getFaceId());
            User.getInstance().setDefaultFaceItem(defaultFaceItem);
            dataSource.create(User.getInstance());
            updateDefaultFaceToServer();
        } else {
            InkarneAppContext.saveSettingIsDefaultFaceChanged(false);
        }
    }

    private void updateDefaultFaceToServer() {
        String url = ConstantsUtil.URL_BASEPATH + ConstantsUtil.URL_METHOD_UPDATE_DEFAULT_FACE + User.getInstance().getmUserId() + "/" + User.getInstance().getDefaultFaceId();
        DataManager.getInstance().updateDefaultFace(url, new DataManager.OnResponseHandlerInterface() {
            @Override
            public void onResponse(Object obj) {


            }

            @Override
            public void onResponseError(String errorMessage, int errorCode) {
                countRetry++;
                if (countRetry < ConstantsUtil.COUNT_RETRY_SERVICE_CRITICAL)
                    updateDefaultFaceToServer();
            }
        });
    }

    public boolean isBMChanged(BMFragment.BMModel bmModel) {
        User user = User.getInstance();
        boolean isBMChanged = false;
        if (Math.abs(user.getHip() - bmModel.weightBM) > 0) {
            user.setWeight(bmModel.weightBM);
            dataSource.updateUserWeight(bmModel.weightBM);
        }
        if (Math.abs(user.getBust() - bmModel.bustSize) > 0
                || Math.abs(user.getHip() - bmModel.hipsSize) > 0
                || Math.abs(user.getWaist() - bmModel.waistSize) > 0
                || Math.abs(user.getHeight() - bmModel.heightBM) > 0) {
            isBMChanged = true;
        }
        return isBMChanged;
    }

    public boolean updateUserBM(BMFragment.BMModel bmModel) {
        User user = User.getInstance();
        if (isBMChanged(bmModel)) {
            user.setBust(bmModel.bustSize);
            user.setHip(bmModel.hipsSize);
            user.setWaist(bmModel.waistSize);
            user.setHeight(bmModel.heightBM);
            user.setWeight(bmModel.weightBM);
            dataSource.create(user);
            return true;
        }else {
            return false;
        }
    }

    public class GLViewRenderer extends IRenderer {
        private final Context context;
        private int lookAtObjn;
        public boolean isLoadComplete;

        public void checkObjContainer() {
            for (int i = 0; i < 3; i++) {
                boolean isObjNull = isNull(i);
                if (!isObjNull)
                    continue;
                if (currentFaceItem == null) {
                    listFaceItem = (ArrayList<FaceItem>) dataSource.getAvatars();
                    if (listFaceItem != null && listFaceItem.size() > 0) {
                        currentFaceIndex = 0;
                        currentFaceItem = listFaceItem.get(0);
                    }
                }
                if (currentFaceItem != null)
                    changeObjects(currentFaceItem);
                break;
            }
        }

        //@Override
        public void glInit() {
            isRenderComplete = false;
            viewFace(true);
            lookAtObjn = 0;
            if (listFaceItem == null || currentFaceIndex > listFaceItem.size() - 1) {
                Log.d(LOGTAG, " listFaceItem is null or empty");
                return;
            }
            try {
                if (User.getInstance().getmGender().equals("m")) {
                    Log.d(LOGTAG, "torso texture path:  bg_shop_screen_male");
                    myRenderer.changeObj(ConstantsUtil.GL_INDEX_BODY, "ply_torso_redoavatar_male.ply", (R.drawable.texture_torso_redoavatar_male), false);

                } else {
                    Log.d(LOGTAG, "torso texture path:  bg_shop_screen_female");
                    myRenderer.changeObj(ConstantsUtil.GL_INDEX_BODY, "ply_torso_redoavatar_female.ply", (R.drawable.texture_torso_redoavatar_female), false);
                }
                currentFaceItem = listFaceItem.get(currentFaceIndex);
                changeObjects(currentFaceItem);
            } catch (PLYLoadMismatch plyLoadMismatch) {
                plyLoadMismatch.printStackTrace();
            }
        }

        //@Override
        public void changeState(int change) {
            Log.d(LOGTAG, "size: " + listFaceItem.size());

            int state = currentFaceIndex + change;
            String side = null;

            if (state < 0) {
                Log.d(LOGTAG, "Left Limit Achieved");
                return;
            }
            if (state >= listFaceItem.size()) {
                Log.d(LOGTAG, "Right Limit Achieved");
                return;
            }
            if (change == -1) {
                side = "left";
                btnForward.setVisibility(View.VISIBLE);
                Log.d(LOGTAG, "shift left");
            } else {
                side = "right";
                btnBackward.setVisibility(View.VISIBLE);
                Log.d(LOGTAG, "shift right");
            }
            if (side.equalsIgnoreCase("left")) {
                currentFaceIndex = currentFaceIndex - 1;
            } else {
                currentFaceIndex = currentFaceIndex + 1;
            }
            currentFaceItem = listFaceItem.get(currentFaceIndex);
            Log.d(LOGTAG, "face id: " + currentFaceItem.getFaceId());
            showFaceLoading();
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    changeObjects(currentFaceItem);
                }
            });

        }

        public boolean changeHair(FaceItem face, boolean isReRender) {
            boolean isAssetReady = true;
            Log.d(LOGTAG, "hairObj :" + face.getHairObjkey() + " hairPng : " + face.getHairPngKey());
            if (face.getFaceId().equals("1") && (face.getHairstyleId() == null || face.getHairstyleId().isEmpty())) {
                try {
                    if (User.getInstance().getmGender().equals("m")) {
                        Log.d(LOGTAG, "torso texture path:  bg_shop_screen_male");
                        myRenderer.changeObj(ConstantsUtil.GL_INDEX_HAIR_A8, "ply_hair_male_mhs002.ply", (R.drawable.texture_hair_male_mhs002), false);

                    } else {
                        Log.d(LOGTAG, "torso texture path:  bg_shop_screen_female");
                        myRenderer.changeObj(ConstantsUtil.GL_INDEX_HAIR_A8, "ply_hair_female_fhs005.ply", (R.drawable.texture_hair_female_fhs005), false);
                    }
                } catch (PLYLoadMismatch plyLoadMismatch) {
                    plyLoadMismatch.printStackTrace();
                    isAssetReady = false;
                }
            } else {
                if (ConstantsUtil.checkFileKeysExist(face.getHairPngKey(), face.getHairObjkey())) {
                    String hairObj = ConstantsUtil.FILE_PATH_APP_ROOT + face.getHairObjkey();
                    String hairPng = ConstantsUtil.FILE_PATH_APP_ROOT + face.getHairPngKey();
                    try {
                        myRenderer.changeObj(ConstantsUtil.GL_INDEX_HAIR_A8, hairObj, (hairPng), true);
                    } catch (PLYLoadMismatch plyLoadMismatch) {
                        plyLoadMismatch.printStackTrace();
                        if (!isReRender) {
                            ConstantsUtil.deleteDirectory(new File(hairPng));
                            ConstantsUtil.deleteDirectory(new File(hairObj));
                            isAssetReady = false;
                            downloadAsset(face.getFaceId(), ConstantsUtil.GL_INDEX_HAIR_A8, face.getHairPngKey(), face.getHairObjkey());
                        } else {
                            if (currentFaceIndex < listFaceItem.size() - 1)
                                changeState(1);
                            else {
                                changeState(-1);
                            }
                        }
                    }
                } else {
                    isAssetReady = false;
                    downloadAsset(face.getFaceId(), ConstantsUtil.GL_INDEX_HAIR_A8, face.getHairPngKey(), face.getHairObjkey());
                }
            }
            return isAssetReady;
        }

        public boolean changeSpecs(FaceItem face, boolean isReRender) {
            boolean isAssetReady = true;
            Log.d(LOGTAG, "specsObj :" + face.getSpecsObjkey() + " specsPng : " + face.getSpecsPngkey());
            if (face.getSpecsPngkey() == null || face.getSpecsPngkey().isEmpty()) {
                myRenderer.resetObj(ConstantsUtil.GL_INDEX_SPECS_A9);
            } else {
                if (ConstantsUtil.checkFileKeysExist(face.getSpecsPngkey(), face.getSpecsObjkey())) {
                    String specsObj = ConstantsUtil.FILE_PATH_APP_ROOT + face.getSpecsObjkey();
                    String specsPng = ConstantsUtil.FILE_PATH_APP_ROOT + face.getSpecsPngkey();
                    try {
                        myRenderer.changeObj(ConstantsUtil.GL_INDEX_SPECS_A9, specsObj, (specsPng), true);
                    } catch (PLYLoadMismatch plyLoadMismatch) {
                        plyLoadMismatch.printStackTrace();
                        if (!isReRender) {
                            ConstantsUtil.deleteDirectory(new File(specsPng));
                            ConstantsUtil.deleteDirectory(new File(specsObj));
                            isAssetReady = false;
                            downloadAsset(face.getFaceId(), ConstantsUtil.GL_INDEX_SPECS_A9, face.getSpecsPngkey(), face.getSpecsObjkey());
                        } else {

                        }
                    }
                } else {
                    isAssetReady = false;
                    downloadAsset(face.getFaceId(), ConstantsUtil.GL_INDEX_SPECS_A9, face.getSpecsPngkey(), face.getSpecsObjkey());
                }
            }
            return isAssetReady;
        }

        public boolean changeFace(FaceItem face, boolean isReRender) {
            boolean isAssetReady = true;
            Log.d(LOGTAG, "faceObj :" + face.getFaceObjkey() + " facePng : " + face.getFacePngkey());
            if (face.getFaceId().equals("1")) {
                try {
                    if (User.getInstance().getmGender().equals("m")) {
                        Log.d(LOGTAG, "torso texture path:  bg_shop_screen_male");
                        myRenderer.changeObj(ConstantsUtil.GL_INDEX_FACE, "ply_face_male.ply", (R.drawable.texture_face_male), false);

                    } else {
                        Log.d(LOGTAG, "torso texture path:  bg_shop_screen_female");
                        myRenderer.changeObj(ConstantsUtil.GL_INDEX_FACE, "ply_face_female.ply", (R.drawable.texture_face_female), false);
                    }
                } catch (PLYLoadMismatch plyLoadMismatch) {
                    plyLoadMismatch.printStackTrace();
                    isAssetReady = false;
                }
            } else {
                if (ConstantsUtil.checkFileKeysExist(face.getFacePngkey(), face.getFaceObjkey())) {
                    String faceObj = ConstantsUtil.FILE_PATH_APP_ROOT + face.getFaceObjkey();
                    String facePng = ConstantsUtil.FILE_PATH_APP_ROOT + face.getFacePngkey();
                    try {
                        myRenderer.changeObj(ConstantsUtil.GL_INDEX_FACE, faceObj, (facePng), false);
                    } catch (PLYLoadMismatch plyLoadMismatch) {
                        plyLoadMismatch.printStackTrace();
                        if (!isReRender) {
                            ConstantsUtil.deleteDirectory(new File(facePng));
                            ConstantsUtil.deleteDirectory(new File(faceObj));
                            isAssetReady = false;
                            downloadAsset(face.getFaceId(), ConstantsUtil.GL_INDEX_FACE, face.getFacePngkey(), face.getFaceObjkey());
                        } else {
                            loadPrevFace();
                        }
                    }
                } else {
                    isAssetReady = false;
                    downloadAsset(face.getFaceId(), ConstantsUtil.GL_INDEX_FACE, face.getFacePngkey(), face.getFaceObjkey());
                }
            }
            return isAssetReady;
        }

        public void changeObjects(final FaceItem face) {
            if (currentFaceItem == null) {
                Log.e(LOGTAG, "changeObjects failed");
                return;
            }

//            new Thread(new Runnable() {
//                @Override
//                public void run() {
            showFaceLoading();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btnBack.setEnabled(false);
                    ibAddFace.setEnabled(false);
                    btnNext.setEnabled(false);

                }
            });

            countDownloadProgress = 0;
            isLoadComplete = false;
            boolean isAssetReady = true;
            setAcceptTouch(false);

            if (!changeFace(face, false))
                isAssetReady = false;

            changeSpecs(face, false);

            if (!changeHair(face, false))
                isAssetReady = false;

            isRenderComplete = true;
            isLoadComplete = true;
            if (isAssetReady) {
                prevFaceIndex = currentFaceIndex;
                setAcceptTouch(true);
                viewFace(true);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateUIForFace(currentFaceIndex);
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                dismissFaceLoading();
                            }
                        });

                        btnBack.setEnabled(true);
                        ibAddFace.setEnabled(true);
                        btnNext.setEnabled(true);
                    }
                });
            }
//                }
//            }).start();
        }

        GLViewRenderer(Context context, GLSurfaceView glVew, int objNo, String gender) {
            super(context, glVew, objNo, gender);
            this.context = context;
        }

        @Override
        public void onDoubleTap() {
            Log.d("Double Tap", "I am Double Tapped.");
        }
//        @Override
//        public void zoom(float v) {
//            scale += v / 10000f;
//            Log.d("zoomEffect", "val: " + v);
//            fixZoom();
//        }
    }

//    public void hideLoadingView() {
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                pbGLView.setVisibility(View.INVISIBLE);
//            }
//        }, 60);
//    }


    private int countDownloadProgress;

    private void loadPrevFace() {
        if (prevFaceIndex != currentFaceIndex && prevFaceIndex > -1 && prevFaceIndex < listFaceItem.size()) {
            currentFaceItem = listFaceItem.get(prevFaceIndex);
            myRenderer.changeObjects(currentFaceItem);
        } else {
            if (currentFaceIndex < listFaceItem.size() - 1)
                myRenderer.changeState(1);
            else {
                myRenderer.changeState(-1);
            }
        }
    }


    public void downloadAsset(final String faceId, final int index, final String keyPng, final String keyObj) {
        countDownloadProgress++;
        if (!Connectivity.isConnected(InkarneAppContext.getAppContext())) {
            dismissFaceLoading();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), getString(R.string.message_network_failure), Toast.LENGTH_SHORT).show();
                }
            });

            countDownloadProgress =0;
            loadPrevFace();
        }
        new AssetDownloader(RedoAvatarActivity.this).downloadAsset(keyObj, keyPng, new OnAssetDownloadListener() {
            @Override
            public void onDownload(BaseAccessoryItem item) {
                if (!currentFaceItem.getFaceId().equals(faceId)) {
                    countDownloadProgress = 0;
                    return;
                }
                countDownloadProgress--;
                switch (index) {
                    case GL_INDEX_TORSO: {

                    }
                    break;

                    case ConstantsUtil.GL_INDEX_FACE: {
                        myRenderer.changeFace(currentFaceItem, true);
                    }
                    break;

                    case ConstantsUtil.GL_INDEX_HAIR_A8: {
                        myRenderer.changeHair(currentFaceItem, true);
                    }
                    break;

                    case ConstantsUtil.GL_INDEX_SPECS_A9: {
                        myRenderer.changeSpecs(currentFaceItem, true);
                    }
                    break;
                }
                if (countDownloadProgress <= 0 && myRenderer.isLoadComplete) {
                    myRenderer.setAcceptTouch(true);
                    myRenderer.viewFace(true);
                    countDownloadProgress = 0;
                    prevFaceIndex = currentFaceIndex;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateUIForFace(currentFaceIndex);
                            btnBack.setEnabled(true);
                            ibAddFace.setEnabled(true);
                            btnNext.setEnabled(true);
                            dismissFaceLoading();
                        }
                    });
                }
            }

            @Override
            public void onDownloadFailed(String faceId) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissFaceLoading();
                    }
                });
                if (!Connectivity.isConnected(RedoAvatarActivity.this)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), getString(R.string.message_network_failure), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                loadPrevFace();
            }

            @Override
            public void onDownloadProgress(String faceId, int percentage) {

            }
        });
    }


    public void showFaceLoading() {
        //        if (pbCircular != null)
//            pbCircular.setVisibility(View.VISIBLE);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (pbDialogComboLoad != null && !isFinishing())
                    pbDialogComboLoad.show();
                if (pbGLView != null) {
                    pbGLView.setLoadingText("Loading...");
                    pbGLView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void dismissFaceLoading() {
//        if (pbCircular != null)
//            pbCircular.setVisibility(View.INVISIBLE);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (pbDialogComboLoad != null)
                    pbDialogComboLoad.dismiss();
                if (pbGLView != null) {

                    pbGLView.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}
