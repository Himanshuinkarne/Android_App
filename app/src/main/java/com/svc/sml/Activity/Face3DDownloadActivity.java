package com.svc.sml.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.svc.sml.Database.InkarneDataSource;
import com.svc.sml.Database.User;
import com.svc.sml.Fragments.BaseAccessoryHListFragment;
import com.svc.sml.Fragments.FaceConfirmationFragment;
import com.svc.sml.Fragments.HairHListFragment;
import com.svc.sml.Fragments.SpecsHListFragment;
import com.svc.sml.Graphics.IRenderer;
import com.svc.sml.Graphics.PLYLoadMismatch;
import com.svc.sml.Helper.DownloadIntentService;
import com.svc.sml.InkarneAppContext;
import com.svc.sml.Model.BaseAccessoryItem;
import com.svc.sml.Model.FaceItem;
import com.svc.sml.R;
import com.svc.sml.Utility.AWSUtil;
import com.svc.sml.Utility.ConstantsUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Face3DDownloadActivity extends BaseActivity implements
        BaseAccessoryHListFragment.OnListFragmentInteractionListener, FaceConfirmationFragment.OnFaceConfirmationFragmentListener {
    private final static String LOGTAG = Face3DDownloadActivity.class.toString();
    public final static String EXTRA_PARAM_CATEGORY_STAGE = "EXTRA_PARAM_CATEGORY_STAGE";
    public final static String EXTRA_PARAM_IS_REDOAVATAR = "EXTRA_PARAM_IS_REDOAVATAR";
    private static final String SIDE_MIDDLE = "middle";
    private static final String SIDE_RIGHT = "right";
    private static final String SIDE_LEFT = "left";
    private static final int GL_INDEX_FACE = 0;
    private static final int GL_INDEX_HAIR = 1;
    private static final int GL_INDEX_SPECS = 2;

    private RelativeLayout rlFragmentContainer;

    private TransferUtility transferUtility;
    private List<TransferObserver> observers;
    private InkarneDataSource datasource;
    private GLSurfaceView mGLView;
    private GLViewRenderer myRenderer;
    private ProgressDialog progressDialog;
    private ProgressBar pbGLView;
    private FaceItem faceItem;
    private ArrayList<FaceItem> listFaceItem = new ArrayList<>();
    private Button btnNext;
    private View containerNextBtn;
    private int currentFaceIndex = 0;
    private String categoryStageType;
    private boolean isRedoAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face3d_download);
        //rlFragmentContainer = (RelativeLayout) findViewById(R.id.rl_fragment_face_load);

        faceItem = (FaceItem)getIntent().getSerializableExtra(FaceItem.EXTRA_PARAM_FACE_OBJ);
        categoryStageType = getIntent().getStringExtra(Face3DDownloadActivity.EXTRA_PARAM_CATEGORY_STAGE);
        isRedoAvatar = getIntent().getBooleanExtra(Face3DDownloadActivity.EXTRA_PARAM_IS_REDOAVATAR, false);
        /* TODO */
        if (faceItem == null) {
            faceItem = User.getInstance().getDefaultFaceItem();
        }
        if (faceItem != null)
            listFaceItem.add(faceItem);

        transferUtility = AWSUtil.getTransferUtility(this);
        observers = new ArrayList<TransferObserver>();
        mGLView = (GLSurfaceView) findViewById(R.id.surfaceviewclass);
        pbGLView = (ProgressBar) findViewById(R.id.pb_face_glsurface);
        btnNext = (Button) findViewById(R.id.btn_shared_next);
        containerNextBtn = findViewById(R.id.shared_layout_bottom_view);
        loadGLView();
        if(categoryStageType != null && categoryStageType.equals(ConstantsUtil.EAccessoryType.eAccTypeSpecs.toString())){
            loadSpecsFragment(0);
        }
        else {
            categoryStageType = ConstantsUtil.EAccessoryType.eAccTypeHair.toString();
            loadHairFragment(0);
        }
    }

    @Override
    public void onBackPressed() {
        // your code.
        Fragment f = this.getSupportFragmentManager().findFragmentById(R.id.ll_bottomViewhList);
        if (f instanceof HairHListFragment) {
            containerNextBtn.setVisibility(View.VISIBLE);
        } else if (f instanceof SpecsHListFragment) {
            containerNextBtn.setVisibility(View.INVISIBLE);
        } else if (f instanceof FaceConfirmationFragment) {
            containerNextBtn.setVisibility(View.VISIBLE);
            btnNext.setText("NEXT");
        }
        super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // your code
            Fragment f = this.getSupportFragmentManager().findFragmentById(R.id.ll_bottomViewhList);
            if (f instanceof HairHListFragment) {
                containerNextBtn.setVisibility(View.VISIBLE);
            } else if (f instanceof SpecsHListFragment) {
                containerNextBtn.setVisibility(View.INVISIBLE);
            } else if (f instanceof FaceConfirmationFragment) {
                containerNextBtn.setVisibility(View.VISIBLE);
                btnNext.setText("NEXT");
            }
            //return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void nextBtnClickHandler(View v) {
        Fragment f = this.getSupportFragmentManager().findFragmentById(R.id.ll_bottomViewhList);
        if (f instanceof HairHListFragment) {
            if (isRedoAvatar) {
                InkarneAppContext.getDataSource().create(faceItem);
                Intent myIntent = new Intent(Face3DDownloadActivity.this, RedoAvatarActivity.class);
                myIntent.putExtra(RedoAvatarActivity.EXTRA_PARAM_FACE_ID, faceItem.getFaceId()); //Optional parameters
                this.startActivity(myIntent);
                finish();
            }
            else{
                loadFaceConfirmationFragment(0);
            }
        } else if (f instanceof SpecsHListFragment) {
            faceItem.setIsComplete(1);
            InkarneAppContext.getDataSource().create(faceItem);
            if (isRedoAvatar || (User.getInstance().getPBId() != null &&  User.getInstance().getPBId().length() != 0)) {
                //updateAvatar();
                Intent myIntent = new Intent(Face3DDownloadActivity.this, RedoAvatarActivity.class);
                myIntent.putExtra(RedoAvatarActivity.EXTRA_PARAM_FACE_ID, faceItem.getFaceId()); //Optional parameters
                this.startActivity(myIntent);
                finish();
            } else {
                Intent myIntent = new Intent(Face3DDownloadActivity.this, BodyMeasurementActivity.class);
                //myIntent.putExtra("key", ""); //Optional parameters
                this.startActivity(myIntent);
            }
        }
    }

    private boolean isRedoAvatar() {
        User user = User.getInstance();
        return !(user.getPBId() == null || user.getPBId().length() == 0);
    }

    private void updateDefaultFace() {
        User.getInstance().setDefaultFaceId(faceItem.getFaceId());
        User.getInstance().setDefaultFaceItem(faceItem);
        InkarneAppContext.getDataSource().create(User.getInstance());
        InkarneAppContext.getDataSource().create(faceItem);
    }

    private void loadHairFragment(int type) {
        if(isRedoAvatar){
            btnNext.setText("OK");
        }
        else{
            btnNext.setText("NEXT");
        }
        containerNextBtn.setVisibility(View.VISIBLE);
        Fragment frag = HairHListFragment.newInstance(faceItem);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.ll_bottomViewhList, frag);
        ft.addToBackStack(null);
        //ft.setTransition(FRAGM.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    private void loadSpecsFragment(int type) {
        if(isRedoAvatar){
            btnNext.setText("OK");
        }
        else{
            btnNext.setText("NEXT");
        }
        containerNextBtn.setVisibility(View.VISIBLE);
        Fragment frag = SpecsHListFragment.newInstance(faceItem);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.ll_bottomViewhList, frag);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void loadFaceConfirmationFragment(int type) {
        containerNextBtn.setVisibility(View.INVISIBLE);
        Fragment frag = FaceConfirmationFragment.newInstance();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.ll_bottomViewhList, frag);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onFaceConfirmationFragmentInteraction(int i) {

        switch (i) {
            case R.id.ib_face_confirm_bad: {
                SharedPreferences settings = InkarneAppContext.getAppContext().getSharedPreferences("inkarne", 0);
                int count = settings.getInt(FaceSelectionActivity.SHARED_PREF_COUNT_INSTRUCTION_VIDEO, 0);


                Intent myIntent = new Intent(Face3DDownloadActivity.this, FaceSelectionActivity.class);
               //Optional parameters
                if(count <3) {//will show twice
                    myIntent.putExtra(FaceSelectionActivity.EXTRA_PARAM_SHOULD_SHOW_2nd_VIDEO, true);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putInt(FaceSelectionActivity.SHARED_PREF_COUNT_INSTRUCTION_VIDEO, ++count);
                    editor.commit();
                }
                this.startActivity(myIntent);

            }
            break;
            case R.id.ib_face_confirm_okay:
            case R.id.ib_face_confirm_great: {
                if(!isRedoAvatar){
                    updateDefaultFace();
                }
                loadSpecsFragment(0);
                getReconcileFaceAccessories();
            }
            break;
            default:
                break;
        }
    }

    @Override
    public synchronized void onListFragmentInteractionHairUpdate(final BaseAccessoryItem item) {
        pbGLView.setVisibility(View.VISIBLE);
        faceItem.setHairObjkey(item.getObjAwsKey());
        faceItem.setHairPngKey(item.getTextureAwsKey());
        try {
            myRenderer.changeHair(faceItem, SIDE_MIDDLE);
        } catch (PLYLoadMismatch plyLoadMismatch) {
            plyLoadMismatch.printStackTrace();
        }
    }

    @Override
    public synchronized void onListFragmentInteractionSpecsUpdate(final BaseAccessoryItem item) {

        pbGLView.setVisibility(View.VISIBLE);
        faceItem.setSpecsObjkey(item.getObjAwsKey());
        faceItem.setSpecsPngkey(item.getTextureAwsKey());
        try {
            myRenderer.changeSpecs(faceItem, SIDE_MIDDLE);
        } catch (PLYLoadMismatch plyLoadMismatch) {
            plyLoadMismatch.printStackTrace();
        }
    }

    public void getReconcileFaceAccessories() {
        Intent intent = new Intent(Face3DDownloadActivity.this, DownloadIntentService.class);
        intent.setAction(DownloadIntentService.ACTION_DOWNLOAD_RECONCILE_FACE_ACC);
        startService(intent);
    }

    private void beginDownload(String key) {
        if (key == null) {
            return;
        }

        File file = new File(Environment.getExternalStorageDirectory().toString() + "/" + key);
        Log.d(LOGTAG, " to be downloaded. file:  " + file);
        // Initiate the download
        TransferObserver observer = transferUtility.download(ConstantsUtil.AWSBucketName, key, file);
        observers.add(observer);
        observer.setTransferListener(new DownloadListener());
    }

    /*
     * A TransferListener class that can listen to a download task and be
     * notified when the status changes.
     */
    private class DownloadListener implements TransferListener {
        @Override
        public void onError(int id, Exception e) {
            progressDialog.dismiss();
            Log.e("Debug", "Error during download");
        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

        }

        @Override
        public void onStateChanged(int id, TransferState state) {
            if (state == TransferState.FAILED) {
                Log.e("Debug", "Download failed");
            }
            TransferObserver observer = null;
            boolean isCompleted = true;
            for (int i = 0; i < observers.size(); i++) {
                observer = observers.get(i);
            }
            if (isCompleted) {
                Log.e("Debug", "Downloaded successfully");
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        //if(e.getY() >)
        myRenderer.onTouchEvent(e);
        return super.onTouchEvent(e);
    }

    /*GLView  */
    public void loadGLView() {
        GLSurfaceView glView = (GLSurfaceView) findViewById(R.id.surfaceviewclass);
        myRenderer = new GLViewRenderer(this,glView, 6);

        /*
        glView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        glView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        glView.setBackgroundResource(R.drawable.bg_default);
        glView.setZOrderOnTop(true);
        */

        glView.setRenderer(myRenderer);


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

    public void updateRenderer(FaceItem face) {

    }

    public class GLViewRenderer extends IRenderer {
        private final Context context;
        private int maxState;
        private int lookAtObjn;
        private int state;

        //@Override
        public void glInit() {
            state = 0;
            lookAtObjn = 0;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pbGLView.setVisibility(View.VISIBLE);
                }
            });
            //Load user faceItem + wig + specs
            changeObjects(currentFaceIndex, SIDE_MIDDLE);
            setAcceptTouch(true);

//            if (listFaceItem.size() > currentFaceIndex + 1) {
//                int RRefComboIndex = currentFaceIndex + 1;
//                changeObjects(RRefComboIndex, SIDE_RIGHT);
//            }
            //autoRotate();
            setAcceptTouch(true);
        }


        //@Override
        public void changeState(int change) {
            state = currentFaceIndex + change;
            String side = null;

            if (state < 0) {
                state = 0;
                Log.d(LOGTAG, "Left Limit Achieved");
                return;
            }
            if (state >= maxState) {
                state = maxState - 1;
                Log.d(LOGTAG, "Right Limit Achieved");
                return;
            }
            setAcceptTouch(false);
            if (change == -1) {
                side = SIDE_LEFT;
                Log.d(LOGTAG, "shift left");
            } else {
                side = SIDE_RIGHT;
                Log.d(LOGTAG, "shift right");
            }
            shiftObj(side);

            if (side.equalsIgnoreCase(SIDE_LEFT)) {
                currentFaceIndex = currentFaceIndex - 1;
                faceItem = listFaceItem.get(currentFaceIndex);

                if (currentFaceIndex == 0) {
                    Log.d(LOGTAG, "Just reached extreme left - Left container empty, currentFaceIndex = " + currentFaceIndex);
                } else {
                    //check left
                    int leftFaceIndex = currentFaceIndex - 1;
                    Log.d(LOGTAG, "Left container loaded with leftIndex = " + leftFaceIndex);

                    changeObjects(leftFaceIndex, side);
                }
            } else {
                currentFaceIndex = currentFaceIndex + 1;
                faceItem = listFaceItem.get(currentFaceIndex);
                //check right
                if (listFaceItem.size() == (currentFaceIndex + 1)) {
                    Log.d(LOGTAG, "Just reached extreme right - Right container empty, currentIndex = " + currentFaceIndex);
                } else {
                    //check right
                    int rightFaceIndex = currentFaceIndex + 1;
                    Log.d(LOGTAG, "Right container loaded with rightIndex = " + rightFaceIndex);
                    changeObjects(rightFaceIndex, side);
                }
            }
            setAcceptTouch(true);
        }


        public synchronized void changeHair(final FaceItem face, String side) throws PLYLoadMismatch {
            if (face.getHairObjkey() != null && face.getHairPngKey() != null) {
                String hairObj = ConstantsUtil.FILE_PATH_APP_ROOT + face.getHairObjkey();
                String hairPng = ConstantsUtil.FILE_PATH_APP_ROOT + face.getHairPngKey();
                Log.d(LOGTAG, "hairObj :" + hairObj + " hairPng : " + hairPng);
                if (ConstantsUtil.checkFileKeysExist(face.getHairPngKey(), face.getHairObjkey())) {
                    //resetObj(GL_INDEX_HAIR);
                    myRenderer.changeObj(GL_INDEX_HAIR, hairObj, (hairPng),false);
                }
            }
            changeObjFinish();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pbGLView.setVisibility(View.INVISIBLE);
                }
            });
        }

        public synchronized void changeSpecs(final FaceItem face, String side) throws PLYLoadMismatch {
            if (face.getSpecsObjkey() != null && face.getSpecsPngkey() != null) {
                String specsObj = ConstantsUtil.FILE_PATH_APP_ROOT + face.getSpecsObjkey();
                String specsPng = ConstantsUtil.FILE_PATH_APP_ROOT + face.getSpecsPngkey();
                Log.d(LOGTAG, "specsObj :" + specsObj + " specsPng : " + specsPng);
                if (ConstantsUtil.checkFileKeysExist(face.getSpecsPngkey(), face.getSpecsObjkey()))  {
                    //resetObj(GL_INDEX_SPECS);
                    myRenderer.changeObj(GL_INDEX_SPECS, specsObj, (specsPng), false);
                }
            }
            changeObjFinish();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pbGLView.setVisibility(View.INVISIBLE);
                }
            });
        }

        public void changeObjects(int index, String side) {

            if (listFaceItem.size() == 0 || currentFaceIndex < 0) {
                Log.e(LOGTAG, "changeObjects failed");
                return;
            }

            FaceItem face = listFaceItem.get(index);

            String faceObj = ConstantsUtil.FILE_PATH_APP_ROOT + face.getFaceObjkey();
            String facePng = ConstantsUtil.FILE_PATH_APP_ROOT + face.getFacePngkey();
            Log.d(LOGTAG, "faceObj :" + faceObj + " facePng : " + facePng);
            if (ConstantsUtil.checkFileKeysExist(face.getFacePngkey(), face.getFaceObjkey()))  {
                resetObj(GL_INDEX_FACE);
                try {
                    myRenderer.changeObj(GL_INDEX_FACE, faceObj, (facePng), false);
                } catch (PLYLoadMismatch plyLoadMismatch) {
                    plyLoadMismatch.printStackTrace();
                }
            }

            try {
                changeHair(face, side);
            } catch (PLYLoadMismatch plyLoadMismatch) {
                plyLoadMismatch.printStackTrace();
            }
            try {
                changeSpecs(face, side);
            } catch (PLYLoadMismatch plyLoadMismatch) {
                plyLoadMismatch.printStackTrace();
            }

            setLookat(GL_INDEX_FACE);
            changeObjFinish();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pbGLView.setVisibility(View.INVISIBLE);
                }
            });
        }

        GLViewRenderer(Context context,GLSurfaceView glView, int objNo) {
            super(context,glView, objNo,"m");
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
}
