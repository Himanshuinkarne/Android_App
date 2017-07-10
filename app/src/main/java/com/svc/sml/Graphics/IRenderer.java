package com.svc.sml.Graphics;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;

import com.svc.sml.R;
import com.svc.sml.ShopActivity;
import com.svc.sml.Utility.ConstantsUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.log;
import static java.lang.Math.sin;
import static java.lang.Math.tan;

public abstract class IRenderer implements GLSurfaceView.Renderer {
    private static final int TIMEOFFSET = 40;
    private final GLSurfaceView view;
    private static final float FACEOFFSET = 300f;
    private static final float SOULDEROFFSET = 500f;
    private static final float BODYOFFSET = 1000f;
    private static final float FOVY = 30f;

    private static final float FACECAMYM = 1450f;
    private static final float FACECAMZM = 580f + FACEOFFSET;
    private static final float REDOFACECAMYM = 1500f;
    private static final float REDOFACECAMZM = 480f + FACEOFFSET;
    private static final float SOULDER_CAMYM = 1300f;
    private static final float SOULDER_CAMZM = 1000f + SOULDEROFFSET;
    private static final float FACECAMYF = 1450f;
    private static final float FACECAMZF = 580f + FACEOFFSET;
    private static final float REDOFACECAMYF = 1500f;
    private static final float REDOFACECAMZF = 480f + FACEOFFSET;
    private static final float SOULDER_CAMYF = 1300f;
    private static final float SOULDER_CAMZF = 1000f + SOULDEROFFSET;
    private static final float BODYCAMY = 800f;
    private static final float BODYCAMYEYE = 775f;

    /* zoom change */
    private static final float MAX_CAMY = FACECAMYM;
    private static final float MIN_CAMY = 300f;

    private static final float NEAR_BODY = 700f + BODYOFFSET;//2300
    private static final float FAR_BODY = 2700f + BODYOFFSET;

    private static final float NEAR_AUTO_ZOOM[] = {1700f, 1200f};//2300
    private static final float FAR_AUTO_ZOOM[] = {4000f, 3000f};


    private static final float NEAR_SOULDER = 700f + SOULDEROFFSET;//500
    private static final float FAR_SOULDER = 1200f + SOULDEROFFSET;

    private static final float NEAR_UPDATE_AVATAR = 200f + FACEOFFSET;//400
    private static final float FAR_UPDATE_AVATAR = 700f + FACEOFFSET;

    private static final float NEAR_FACE = 300f + FACEOFFSET;//400
    //private static final float FAR_FACE = 700f + FACEOFFSET;
    private static final float FAR_FACE = 800f + FACEOFFSET;

    private static final float BODYCAMZ = 2300f + BODYOFFSET;
    private int lookAtIndex;

    public static final int LOOKAT_INDEX_FACE = 2;
    public static final int LOOKAT_INDEX_SHOULDER = 1;
    public static final int LOOKAT_INDEX_BODY = 0;
    public static final int LOOKAT_INDEX_UPDATE_AVATAR = 3;
    public static final int LOOKAT_INDEX_AUTO_ZOOM = 4;

    private final int objNo;
    private float camx, camy, camz,camyMax;

    private float angle, angleold, zoom;
    protected Object obj[];
    private boolean isDisp[], isShift;
    private CameraMotion camMotion;
    private boolean acceptTouch;
    private final float SCROLL_THRESHOLD = 10;
    protected boolean isZoom, faceMode;
    protected boolean isZoomStart;
    private boolean autoRotation;
    private Vec3d cam, eye;
    private boolean firstTouch;
    private boolean isDrag = false, isFace = false;
    private double oldDist;
    private double newDist;
    private boolean male;
    private int frame, bagC, clutchC;
    public int vAngle = 0;
    private float px = 0, py = 0, x = 0, y = 0, p2x = 0, p2y = 0;
    private float pxMove = 0, pyMove = 0;
    protected float scale;
    private long time;
    private int state, script, t, currentBB;
    protected int width, height;
    private int pointerCount, pointerIndex, pointerId;
    private static final float INITANGLE = (float) PI;
    /**
     * Used for debug logs.
     */
    private static final String TAG = "IRenderer";
    private final Context context;
    private float aspectRatio, rot, arot;
    private boolean isBag, isCluch;
    private boolean isAutoZoom, isRendering;
    public boolean isVideo = false;
    private long startTime, endTime;

    //private EGLConfig config;

    /**
     * Store the view matrix. This can be thought of as our camera. This matrix transforms world space to eye space;
     * it positions things relative to our eye.
     */
    private float[] mViewMatrix = new float[16];

    /**
     * Store the projection matrix. This is used to project the scene onto a 2D viewport.
     */
    private float[] mProjectionMatrix = new float[16];
    private float ratio;
    private int mProgramHandle;
    private boolean loadNotDone = false;
    private EGLConfig config;
    private String[] objply;
    private String[] objtexS;
    private int[] objtexI;
    private boolean isTest = true;

    /**
     * Initialize the model data.
     */
    public IRenderer(final Context context, GLSurfaceView view, int objNo, String male) {
        this.view = view;
        this.context = context;
        if (male.equalsIgnoreCase("m"))
            this.male = true;
        else
            this.male = false;
        frame = 0;
        currentBB = 0;
        startTime = System.currentTimeMillis();
        camx = -37.5f;
        camy = 1500;
        camz = -1400f;
        zoom = 0;
        this.objNo = objNo;
        obj = new Object[objNo];
        objply = new String[objNo];
        objtexS = new String[objNo];
        objtexI = new int[objNo];

        isDisp = new boolean[objNo];
        camMotion = new CameraMotion(context);
        state = 0;
        t = 0;
        script = 0;
        angle = angleold = INITANGLE;
        cam = new Vec3d();
        eye = new Vec3d();
        isBag = isCluch = false;
        if (this.male) {
            setCamScript(0, R.raw.camexternal_male);
            setCamScript(1, R.raw.camapparel_male);
        } else {
            setCamScript(0, R.raw.camexternal_female);
            setCamScript(1, R.raw.camapparel_female);
        }
        isAutoZoom = false;
        acceptTouch = true;
        firstTouch = false;
        rot = 0.0f;
    }

    public abstract void glInit();

    public abstract void changeState(int no);

    public boolean isAcceptTouch() {
        return acceptTouch;
    }

    public boolean setCamScript(int s, int file) {
        return camMotion.readCamScript(s, file);
    }

    public void setAcceptTouch(boolean acceptTouch) {
        Log.d("SSS", "Touch " + acceptTouch);
        this.acceptTouch = acceptTouch;
        if (acceptTouch)
            view.requestRender();
    }

    //set a perticular obj visible or not
    public void setIsDisp(int i, boolean b) {
        isDisp[i] = b;
    }

    //set all the objs visible or not
    public void resetDisp(boolean b) {
        for (int i = 0; i < objNo; i++) {
            isDisp[i] = b;
            objply[i] = null;
            objtexI[i] = -1;
        }
    }

    //Himanshu
    public void changeObj(int no, String objval, int tex, String side, boolean shine) throws PLYLoadMismatch {
        changeObj(no, objval, tex, shine);
    }

    public void changeObj(int no, String objval, String tex, String side, boolean shine) throws PLYLoadMismatch {
        changeObj(no, objval, tex, shine);
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, final EGLConfig config) {
        this.config = config;
        GLES30.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        // Enable depth testing
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        // Position the eye in front of the origin.
        final float eyeX = 0.0f;
        final float eyeY = 1500.0f;
        final float eyeZ = -1400f;

        // We are looking toward the distance
        final float lookX = 0.0f;
        final float lookY = 875.0f;
        final float lookZ = 0.0f;

        // Set our up vector. This is where our head would be pointing were we holding the camera.
        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;

        // Set the view matrix. This matrix can be said to represent the camera position.
        // NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
        // view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
        Matrix.setLookAtM(mViewMatrix, 0, (float) ((zoom + camz) * sin(angle)), camy, (float) ((zoom + camz) * cos(angle)), 0, camy, 0, 0f, 1.0f, 0.0f);
        // Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
        for (int i = 0; i < objNo; i++) {
            obj[i] = null;
            obj[i] = new Object(context, config);
            obj[i].setIsObjLoaded(false);
            isDisp[i] = true;
        }
        glInit();
        t = state = script = 0;


    }

    public void stopZoom() {
        if (isAutoZoom) {

            script = camMotion.getMaxScript() - 1;
            state = camMotion.getMaxStates(script) - 1;
            t = camMotion.getStateSteps(script, state) - 1;
        }
    }

    public void stopVideo() {
        vAngle = 0;
        angle = INITANGLE;
        isVideo = false;
        ((ShopActivity) context).cancelVideo();
        view.requestRender();
    }

    public Bitmap readTexture(final String tex) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Log.d(TAG, "texture path: " + tex);
        return BitmapFactory.decodeFile(tex, options);
    }

    public synchronized void changeObj(int no, String objval, int texid, boolean shine) throws PLYLoadMismatch {
        Bitmap tex = readTextureFromAsset(texid);
        if (obj[no] == null) {
            Log.d("SSSSS", "I am INT");
            obj[no] = new Object(context, config);
            obj[no].setIsObjLoaded(false);
        }
        objply[no] = objval;
        objtexI[no] = texid;
        Log.d("SSSSSS", "no: " + no + "  texid: " + texid);
        try {
            if (objval != null)
                obj[no].readPLYObject(objval, tex);
        } catch (Exception e) {
            throw new PLYLoadMismatch(0, 0, 0, 0);
        }

    }

    public synchronized void changeObj(int no, String objval, String texPath, boolean shine) throws PLYLoadMismatch {
        Bitmap tex = readTexture(texPath);
        if (obj[no] == null) {
            Log.d("SSSSS", "I am STRING");
            obj[no] = new Object(context, config);
            obj[no].setIsObjLoaded(false);
        }
        objply[no] = objval;
        objtexS[no] = texPath;
        try {
            if (objval != null)
                obj[no].readPLYObject(objval, tex);
        } catch (Exception e) {
            throw new PLYLoadMismatch(0, 0, 0, 0);
        }
    }

    public void shiftObj(String side) {

    }

    public void runAutoZoom() {

        isAutoZoom = true;
        acceptTouch = false;
        script = 0;
        state = 0;
        t = 0;
    }

    public void createVideo() {
        isVideo = true;
        vAngle = 0;
        Log.d("IRenderer", "360 in IR create");
        view.requestRender();
    }

    public class BitmapWorkerTask extends AsyncTask<Integer, Void, Void> {
        private String objval;
        private Bitmap tex;
        private boolean isShine;

        public BitmapWorkerTask(String objval, Bitmap tex, boolean shine) {
            this.objval = objval;
            this.tex = tex;
            this.isShine = shine;
        }

//        @Override
//        protected Void doInBackground(Integer... params) {
//            return null;
//        }

        @Override
        protected void onPreExecute() {

        }

        //not needed now
        public void changeObjFinish() {
        }

        public void zoomCompleted() {

        }

        @Override
        protected Void doInBackground(Integer... params) {
            int no = params[0];
            try {
                obj[no].readPLYObject(objval, tex);
            } catch (PLYLoadMismatch plyLoadMismatch) {
                plyLoadMismatch.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void params) {

        }
    }

    public void viewFace(boolean face) {
        if (face) {
            viewLookAt(LOOKAT_INDEX_UPDATE_AVATAR);
        } else {
            camy = BODYCAMY;
            camz = BODYCAMZ;
            isFace = false;
            changeBoundingBox(LOOKAT_INDEX_BODY);

        }
    }

    public void setLookat(int lookAtObjn) {
        if (lookAtObjn == 0) {
            viewFace(true);
        } else {
            viewFace(false);
        }
    }

    public boolean isNull(int id) {
        return obj[id] == null ? true : false;
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        GLES30.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int w = metrics.widthPixels;

        this.width = width;
        this.height = height;
        // Set the OpenGL viewport to the same size as the surface.
        GLES30.glViewport(0, 0, width, height);

//        // Create a new perspective projection matrix. The height will stay the same
//        // while the width will vary as per aspect ratio.
        ratio = (float) width / height;
////        final float left = -ratio*100;
////        final float right = ratio*100;
////        final float bottom = -110.0f;
////        final float top = 110.0f;
////        final float near = 300.0f; //old
////        final float far = 4800.0f;//old
//
//        final float near = NEAR_BODY;
//        final float far = FAR_BODY;
//
////        final float near = 1000.0f;
////        final float far = 3000.0f;
//        final float fovy = 45.0f;
//
//        Matrix.perspectiveM(mProjectionMatrix, 0, fovy,ratio, near, far);
        changeBoundingBox(currentBB);
    }

    public void buttonPressed(int i) {
        //curScreen=manager.buttonPressed(i,curScreen);
    }

    public void zoomCompleted() {

    }

    public boolean isZoomCompleted() {

        return !isAutoZoom;
    }

    public boolean isLooksAnimating() {
        return isAutoZoom;
    }

    //not neded now
    public void changeObjFinish() {
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        // Log.d("SSS","onDrawFrame");
        if (!acceptTouch && !isAutoZoom) {
            return;
        }

        //Log.e("SSS","onDrawFrame");
        //
        for (int i = 0; i < objNo; i++) {
            if (obj[i] == null) {
                try {
                    Log.d("SSSSSS", "I am here creating new obj " + i + "  " + objtexI[i]);
                    if (objtexI[i] < 1)
                        changeObj(i, objply[i], objtexS[i], false);
                    else
                        changeObj(i, objply[i], objtexI[i], false);
                } catch (PLYLoadMismatch plyLoadMismatch) {
                    plyLoadMismatch.printStackTrace();
                }
                return;
            }
//            if(obj[i]!=null)
//            {
//                if((obj[i].vertN)!=obj[i].getVertBuffSize()/3){
//                    Log.d("NAKED TEST","v "+i+": "+obj[i].vertN+"   in buff: "+obj[i].getVertBuffSize()/3);
//                    //return;
//                }
//                if((obj[i].faceN)!=obj[i].getFaceBuffSize()/9){
//                    Log.d("NAKED TEST","f "+i+": "+obj[i].faceN+"   in buff: "+obj[i].getFaceBuffSize()/9);
//                    //return;
//                }
//            }

        }
        //GLES30.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
        GLES30.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        // GLES30.glEnable(GLES30.GL_CULL_FACE);

        if (isAutoZoom) {
            if (currentBB != LOOKAT_INDEX_AUTO_ZOOM) {
                changeBoundingBox(LOOKAT_INDEX_AUTO_ZOOM, script);
            }
            endTime = System.currentTimeMillis();
            long dt = endTime - startTime;
            startTime = System.currentTimeMillis();
            angle = camMotion.getCamPos(script, state, t, cam, eye);
            Matrix.setLookAtM(mViewMatrix, 0, cam.getX(), cam.getY(), cam.getZ(), eye.getX(), eye.getY(), eye.getZ(), 0f, 1.0f, 0.0f);
            if (dt < TIMEOFFSET) {
//                try {
//                    Thread.sleep(TIMEOFFSET - dt);
                dt = TIMEOFFSET;
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
            if (t == 0 && script == 0 && state == 0)
                t++;
            else {
                t += (int) (dt + TIMEOFFSET - 1) / TIMEOFFSET;
            }
            // t++;
            if (t >= camMotion.getStateSteps(script, state)) {
                state++;
                t = 0;
            }

            if (state >= camMotion.getMaxStates(script)) {
                script++;
                if (script < camMotion.getMaxScript()) {
                    changeBoundingBox(LOOKAT_INDEX_AUTO_ZOOM, script);
                }
                state = 0;
            }
            if (script >= camMotion.getMaxScript()) {
                isAutoZoom = false;
                acceptTouch = true;
                viewLookAt(LOOKAT_INDEX_BODY);
                script = 0;
                state = 0;
                zoomCompleted();
                angle = INITANGLE;
            }

            view.requestRender();
        } else {
            if (isVideo) {
                if (currentBB != LOOKAT_INDEX_BODY) {
                    viewLookAt(LOOKAT_INDEX_BODY);
                }
                Log.d("V360", "vAngle: " + vAngle);
                cam.setX((float) ((zoom + camz) * sin(INITANGLE + vAngle * (2 * PI / ConstantsUtil.VIDEO_SHARE_FRAMES))));
                cam.setY(camy);
                cam.setZ((float) ((zoom + camz) * cos(INITANGLE + vAngle * (2 * PI / ConstantsUtil.VIDEO_SHARE_FRAMES))));
                eye.setX(0);
                eye.setY(camy);
                eye.setZ(0);
                Matrix.setLookAtM(mViewMatrix, 0, cam.getX(), cam.getY(), cam.getZ(), eye.getX(), eye.getY(), eye.getZ(), 0f, 1.0f, 0.0f);
                Log.d("V360", "vAngle2: " + vAngle);

                if (vAngle > ConstantsUtil.VIDEO_SHARE_FRAMES) {
                    vAngle = 0;
                    isVideo = false;
                    ((ShopActivity) context).finishVideo();
                }
                vAngle++;
                view.requestRender();

            } else {
                cam.setX((float) ((zoom + camz) * sin(angle)));
                cam.setY(camy);
                cam.setZ((float) ((zoom + camz) * cos(angle)));
                eye.setX(0);
                eye.setY(camy);
                eye.setZ(0);
                Matrix.setLookAtM(mViewMatrix, 0, cam.getX(), cam.getY(), cam.getZ(), eye.getX(), eye.getY(), eye.getZ(), 0f, 1.0f, 0.0f);
                //  Matrix.setLookAtM(mViewMatrix, 0,0, camy, -(zoom + camz), 0, camy, 0, 0f, 1.0f, 0.0f);
            }
        }
        //   Matrix.setIdentityM(mViewMatrix, 0);
        for (int i = 0; i < objNo; i++) {
            if (obj[i] != null && obj[i].isSet) {
                if (i == ConstantsUtil.GL_INDEX_HAIR_A8) {
                    obj[i].draw(mViewMatrix, mProjectionMatrix, angle, 1, false);
                    //obj[i].sort(cam);
                    obj[i].draw(mViewMatrix, mProjectionMatrix, angle, 2, false);
                } else if (i == ConstantsUtil.GL_INDEX_FACE || i == ConstantsUtil.GL_INDEX_BODY || i == ConstantsUtil.GL_INDEX_LEGS) {
                    obj[i].draw(mViewMatrix, mProjectionMatrix, angle, 0, true);
                } else {
                    obj[i].draw(mViewMatrix, mProjectionMatrix, angle, 0, false);
                }
            }
        }

        //  obj[GL_INDEX_HAIR_A8].sort(cam);

        //GLES30.glDisable(GLES30.GL_DEPTH_TEST);

    }

    private String saveToInternalStorage(Bitmap bitmapImage, int i) {
        ContextWrapper cw = new ContextWrapper(this.context.getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, "img" + i + ".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    public void viewLookAt(int index) {
        lookAtIndex = index;
        switch (index) {
            case LOOKAT_INDEX_BODY: {
                camy = BODYCAMY;
                camz = BODYCAMZ;
            }
            break;
            case LOOKAT_INDEX_SHOULDER: {
                if (male) {
                    camy = SOULDER_CAMYM;
                    camz = SOULDER_CAMZM;
                } else {
                    camy = SOULDER_CAMYF;
                    camz = SOULDER_CAMZF;
                }
            }
            break;
            case LOOKAT_INDEX_FACE: {
                if (male) {
                    camy = FACECAMYM;
                    camz = FACECAMZM;
                } else {
                    camy = FACECAMYF;
                    camz = FACECAMZF;
                }
            }
            break;
            case LOOKAT_INDEX_UPDATE_AVATAR: {
                if (male) {
                    camy = REDOFACECAMYM;
                    camz = REDOFACECAMZM;
                } else {
                    camy = REDOFACECAMYF;
                    camz = REDOFACECAMZF;
                }
            }
            break;
            default:
                break;
        }
        changeBoundingBox(index);
    }

    private void changeBoundingBox(int index) {
        Log.d(TAG, "" + index);
        float near = 0, far = 0;
        switch (index) {
            case LOOKAT_INDEX_BODY: {
                near = NEAR_BODY;
                far = FAR_BODY;
            }
            break;
            case LOOKAT_INDEX_SHOULDER: {
                near = NEAR_SOULDER;
                far = FAR_SOULDER;
            }
            break;
            case LOOKAT_INDEX_FACE: {
                near = NEAR_FACE;
                far = FAR_FACE;
            }
            break;
            case LOOKAT_INDEX_UPDATE_AVATAR: {
                near = NEAR_UPDATE_AVATAR;
                far = FAR_UPDATE_AVATAR;
            }
            break;
            default: {
                near = NEAR_BODY;
                far = FAR_BODY;
            }
            break;
        }
        Matrix.perspectiveM(mProjectionMatrix, 0, FOVY, ratio, near, far);
        Log.d("SSSS", "near: " + near + "  far: " + far + "  " + currentBB);
        if (currentBB != index) {
            view.requestRender();
            Log.d("SSSSS", "I am changing " + index + "  " + currentBB);
        }
        currentBB = index;

    }

    private void changeBoundingBox(int index, int script) {
        Log.d(TAG, "" + index);
        float near = 0, far = 0;
        near = NEAR_AUTO_ZOOM[script];
        far = FAR_AUTO_ZOOM[script];
        Log.d(TAG, "near: " + near + "  far: " + far);
        Matrix.perspectiveM(mProjectionMatrix, 0, FOVY, ratio, near, far);
        currentBB = index;
        //view.requestRender();
    }

    public void viewLookAt(int index, int angle) {
        // if(angle != 0)

        this.angle = this.angleold = (float) (angle * 3.14 / 180);
        viewLookAt(index);
        //view.requestRender();
    }

    //abstract public void zoom(float v) ;
    public void zoom(float v) {
        //isZoom = false;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isZoomStart = false;
            }
        },100);

        newDist = 0;
        oldDist = 0;
    }

    public void onTouchEvent(MotionEvent e) {
        if (acceptTouch) {
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            int maxWidth = metrics.widthPixels;
            int action = e.getAction();
            switch (action & MotionEvent.ACTION_MASK) {

                case MotionEvent.ACTION_POINTER_DOWN:
                    // movement: cancel the touch press
                    pointerCount = e.getPointerCount();
                    for (int i = 0; i < pointerCount; ++i) {
                        pointerIndex = i;
                        pointerId = e.getPointerId(pointerIndex);
                        if (pointerId == 0) {
                            px = e.getX(0);
                            py = e.getY(0);
                        }
                        if (pointerId == 1 && e.getPointerCount()>=2) {
                            p2x = e.getX(1);
                            p2y = e.getY(1);
                            isZoomStart = true;
                        }
                    }

                    oldDist = Math.sqrt((px - p2x) * (px - p2x) + (py - p2y) * (py - p2y));
                    isDrag = false;
                    isZoom = true;


                    break;
                case MotionEvent.ACTION_DOWN:
                    px = e.getX();
                    py = e.getY();
                    pxMove = px;
                    pyMove = py;
                    isDrag = false;
                    isZoomStart = false;
                    break;

                case MotionEvent.ACTION_MOVE:
                    // movement: cancel the touch press
                    isDrag = false;
                    pointerCount = e.getPointerCount();
                    //Log.d("IRenderer", "ACTION_MOVE : "+pointerCount);

                    x = e.getX();
                    y = e.getY();
                    if (isZoom && isZoomStart) {
                        for (int i = 0; i < pointerCount; ++i) {
                            //int pointerIndex = i;
                            int pointerId = e.getPointerId(i);
                            if (pointerId == 0) {
                                px = e.getX(0);
                                py = e.getY(0);
                            }
                            if (pointerId == 1 && e.getPointerCount()>=2) {
                                p2x = e.getX(1);
                                p2y = e.getY(1);
                            }
                        }
                        newDist = Math.sqrt((px - p2x) * (px - p2x) + (py - p2y) * (py - p2y));
                        //zoom((float) (newDist - oldDist));

                    } else {

                        if(pointerCount < 2 && !isZoomStart) {
                            float dx = pxMove - e.getX();
                            float dy = pyMove - e.getY();
                            float slope =  abs(dy)/abs(dx);
                            if(lookAtIndex > 0){
                                int maxHeight1 = metrics.heightPixels;
                                float min_camy = (float) ((float) (tan(FOVY/PI)*camz) - 50.0);
                                Log.e("IRenderer", "min_camy: "+min_camy);
                                float fac = (float) ((MAX_CAMY - min_camy)/(maxHeight1));

                                if(slope > 1.8){
                                    camy += -dy*fac ;
                                    camy = Math.min(Math.max(camy,min_camy),MAX_CAMY);
                                    Log.e("IRenderer", fac +"   camy"+camy);
                                }
                            }
                            //if (abs(px - e.getX()) > SCROLL_THRESHOLD && (pointerCount < 2 && !isZoomStart)) {//&& pointerCount<2
                            if (abs(px - e.getX()) > SCROLL_THRESHOLD && (slope < 1.8 || lookAtIndex==0) ) {//&& pointerCount<2
                                //Log.d("IRenderer", "ACTION_MOVE 2 : "+pointerCount);
                                if (LOOKAT_INDEX_UPDATE_AVATAR == lookAtIndex) {
                                    if (px < 9 * maxWidth / 10 && px > maxWidth / 10) {
                                        letsDrag(x - px, y - py, false);
                                        Log.d("IRenderer", "ACTION_MOVE : letsDrag 1");
                                    }
                                } else {
                                    letsDrag(x - px, y - py, false);
                                    Log.d("IRenderer", "ACTION_MOVE : letsDrag 2");
                                }
                            }
                        }
                        pxMove = e.getX();
                        pyMove = e.getY();
                    }

                    break;
                case MotionEvent.ACTION_UP:
                    Log.d("IRenderer", "ACTION_UP : "+ pointerCount);
                    if(isZoom || isZoomStart )
                        return;
//                    if (abs(px - e.getX()) < SCROLL_THRESHOLD) {
//                        letsTouch(e.getX(), e.getY());
//                    } else {
//                        // Log.d("IRenderer", "points: X:" + px + " maxX:" + maxWidth);
//                        if (px > 9 * maxWidth / 10)
//                            changeState(1);
//                        else if (px < maxWidth / 10)
//                            changeState(-1);
//                        else {
//                            Log.d("IRenderer", "ACTION_UP : letsDrag");
//                            letsDrag(x - px, y - py, true);
//                        }
//                    }
                    changeIsZoomStatus();

                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    Log.d("IRenderer", "ACTION_POINTER_UP : "+ pointerCount);
                    if(isZoom || isZoomStart) {
                        zoom((float) (newDist - oldDist));
                        Log.d("IRenderer", "ACTION_POINTER_UP : "+ "zoom");
                    }else {
                        //changeIsZoomStatus();
                    }
                    changeIsZoomStatus();
                    break;
            }
        }

    }

    private void changeIsZoomStatus(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isZoom = false;
            }
        },100);
    }


    //handles touch function
    public void letsTouch(float x, float y) {
        if (!firstTouch) {
            firstTouch = true;
            time = System.currentTimeMillis();
        } else {
            if (System.currentTimeMillis() - time <= 300)
                onDoubleTap();
            firstTouch = false;
        }
    }

    //reset all objects to null
    public void resetObj() {
        for (int i = 0; i < objNo; i++) {
            obj[i].isSet = false;
            objply[i] = null;
        }
    }

    //reset ith object to null
    public void resetObj(int i) {
        if (obj[i] != null) {
            obj[i].isSet = false;
            objply[i] = null;

        }
    }

    public Bitmap readTextureFromAsset(final int tex) {
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), tex);
        //BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Log.d("IRenderer", "texture path: " + tex);
        return bm;//BitmapFactory.decodeFile(tex,options);
    }

    abstract public void onDoubleTap();

    public void onResume() {
        viewLookAt(currentBB);
        t = state = script = 0;
        if (isVideo)
            stopVideo();
    }

    public void letsDrag(float x, float y, boolean finish) {
        //curScreen=manager.letsDrag(dx,dy,finish,curScreen);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int height = metrics.heightPixels;
        float maxHeight = (float) (30 * tan((45 * 0.5) * (PI / 180)));
        float maxWidth = maxHeight * aspectRatio;
        float dragX = (float) (5 * log(abs(x)) / (maxHeight));
        angle -= (dragX * (x / abs(x))) / 20f;
        Log.e("Irenderer","angle: " +angle);
    }

    //    public void letsDrag(float x, float y, boolean finish) {
//        //curScreen=manager.letsDrag(dx,dy,finish,curScreen);
//        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
//        int height = metrics.heightPixels;
//        float maxHeight = (float) (30 * tan((45 * 0.5) * (PI / 180)));
//        float maxWidth = maxHeight * aspectRatio;
//        float dragX = (float) (2 * x / (maxHeight));
//        angle -= dragX / 500f;
//
//
//        // Log.d("angle", "" + angle);
//    }
    public void delete() {
        for (int i = 0; i < objNo; i++)
            obj[i] = null;
    }
}
