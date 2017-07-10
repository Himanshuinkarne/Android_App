package com.svc.sml.Graphics;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.opengl.GLES30;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;

import com.svc.sml.Graphics.common.RawResourceReader;
import com.svc.sml.Graphics.common.ShaderHelper;
import com.svc.sml.Graphics.common.TextureHelper;
import com.svc.sml.R;
import com.svc.sml.Utility.ConstantsUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;

import static java.lang.StrictMath.PI;
import static java.lang.StrictMath.abs;
import static java.lang.StrictMath.sqrt;

/**
 * Created by Sarbartha on 07/06/16.
 */

/**
 * Store our model data in a float buffer.
 */

public class Object {
    private static final String TAG = "Object";

    private final Context context;
    private final EGLConfig config;
    private int mBrickDataHandle;
    private int mProgramHandle, mProgramHandle1, mProgramHandle2;
    private int mGrassDataHandle;
    private boolean isObjLoaded;

    /**
     * Temporary place to save the min and mag filter, in case the activity was restarted.
     */
    private int mQueuedMinFilter;
    private int mQueuedMagFilter;
    Vector<Vec3d> verts;
    Vector<Vec3d> textures;
    Vector<Vec3d> normals;
    Vector<int[]> facePly;
    public boolean isSet;
    public int faceN, vertN;
    private float maxX, maxY, maxZ;
    private float minX, minY, minZ;
    private FloatBuffer vertexBuffer;
    private FloatBuffer normalBuffer;
    private FloatBuffer textureBuffer;
    private IntBuffer indexBuffer;

    /**
     * How many bytes per float.
     */
    private final int mBytesPerFloat = 4;
    private final int mBytesPerInt = 4;
    /**
     * Size of the position data in elements.
     */
    private final int mPositionDataSize = 3;

    /**
     * Size of the normal data in elements.
     */
    private final int mNormalDataSize = 3;

    /**
     * Size of the texture coordinate data in elements.
     */
    private final int mTextureCoordinateDataSize = 2;
    /**
     * This will be used to pass in the transformation matrix.
     */
    private int mMVPMatrixHandle;

    /**
     * This will be used to pass in the modelview matrix.
     */
    private int mMVMatrixHandle;

    /**
     * This will be used to pass in the light position.
     */
    private int mLightPosHandle1, mLightPosHandle2, mLightPosHandle3, mLightPosHandle4;
    private int mLightDirHandle1, mLightDirHandle2, mLightDirHandle3, mLightDirHandle4;

    /**
     * This will be used to pass in the texture.
     */
    private int mTextureUniformHandle;

    /**
     * This will be used to pass in model position information.
     */
    private int mPositionHandle;

    /**
     * This will be used to pass in model normal information.
     */
    private int mNormalHandle;

    /**
     * This will be used to pass in model texture coordinate information.
     */
    private int mTextureCoordinateHandle;

    /**
     * Allocate storage for the final combined matrix. This will be passed into the shader program.
     */
    private float[] mMVPMatrix = new float[16];
    ;
    private final float[] mAccumulatedRotation = new float[16];

    /**
     * Store the current rotation.
     */
    private final float[] mCurrentRotation = new float[16];

    /**
     * A temporary matrix.
     */
    private float[] mTemporaryMatrix = new float[16];

    /**
     * Stores a copy of the model matrix specifically for the light position.
     */
    private float[] mLightModelMatrix = new float[16];
    /**
     * Store the model matrix. This matrix is used to move models from object space (where each model can be thought
     * of being located at the center of the universe) to world space.
     */
    private float[] mModelMatrix = new float[16];
    /**
     * Used to hold a light centered on the origin in model space. We need a 4th coordinate so we can get translations to work when
     * we multiply this by our transformation matrices.
     */

//    private float[] mLightPosInModelSpace1 = new float[] {-1200.0f, 1500.0f, -1200.0f, 1.0f};
//    private float[] mLightPosInModelSpace2 = new float[] {1200.0f, 1500.0f, 1200.0f, 1.0f};
//    private float[] mLightPosInModelSpace3 = new float[] {-1200.0f, 1500.0f, 1200.0f, 1.0f};
//    private final float[] mLightPosInModelSpace4 = new float[] {1200.0f, 1500.0f, -1200.0f, 1.0f};

    private float[] mLightPosInModelSpace1 = new float[]{0.0f, 1500.0f, -1200.0f, 1.0f};
    private float[] mLightPosInModelSpace2 = new float[]{0.0f, 1500.0f, 1200.0f, 1.0f};
    //private float[] mLightPosInModelSpace3 = new float[] {-1200.0f, 1500.0f, 1200.0f, 1.0f};
    //private final float[] mLightPosInModelSpace4 = new float[] {1200.0f, 1500.0f, 1200.0f, 1.0f};

    private final float[] mLightDirInModelSpace1 = new float[]{0.0f, -1500.0f, 0.0f, 1.0f};
    private final float[] mLightDirInModelSpace2 = new float[]{0.0f, -1500.0f, 0.0f, 1.0f};
    //private final float[] mLightDirInModelSpace3 = new float[] {0.0f, -1500.0f, 0.0f, 1.0f};
    //private final float[] mLightDirInModelSpace4 = new float[] {0.0f, -1500.0f, 0.0f, 1.0f};

    private final float mLightCuttOff = 45.0f;

    /**
     * Used to hold the current position of the light in world space (after transformation via model matrix).
     */
    private final float[] mLightPosInWorldSpace1 = new float[4];
    private final float[] mLightPosInWorldSpace2 = new float[4];
    //private final float[] mLightPosInWorldSpace3 = new float[4];
    //private final float[] mLightPosInWorldSpace4 = new float[4];

    private final float[] mLightDirInWorldSpace1 = new float[4];
    private final float[] mLightDirInWorldSpace2 = new float[4];
    //private final float[] mLightDirInWorldSpace3 = new float[4];
    //private final float[] mLightDirInWorldSpace4 = new float[4];

    /**
     * Used to hold the transformed position of the light in eye space (after transformation via modelview matrix)
     */
    private final float[] mLightPosInEyeSpace1 = new float[4];
    private final float[] mLightPosInEyeSpace2 = new float[4];
    //private final float[] mLightPosInEyeSpace3 = new float[4];
    //private final float[] mLightPosInEyeSpace4 = new float[4];

    private final float[] mLightDirInEyeSpace1 = new float[4];
    private final float[] mLightDirInEyeSpace2 = new float[4];
    //private final float[] mLightDirInEyeSpace3 = new float[4];
    //private final float[] mLightDirInEyeSpace4 = new float[4];

    private boolean loadTexture;
    private Bitmap tex;
    private boolean mShouldLoadTexture;
    private int mLightSpotCuttoffHandle;

    public boolean isObjLoaded() {
        return isObjLoaded;
    }

    public void setIsObjLoaded(boolean isObjLoaded) {
        this.isObjLoaded = isObjLoaded;
    }

    public int getVertBuffSize() {
        if (vertexBuffer != null)
            return vertexBuffer.limit();
        return -1;
    }

    public int getFaceBuffSize() {
        if (indexBuffer != null)
            return indexBuffer.limit();
        return -1;
    }

    Object(Context context, EGLConfig config) {
        this.context = context;
        this.config = config;
        final String vertexShader = RawResourceReader.readTextFileFromRawResource(context, R.raw.vertex_shader);
        final String fragmentShader = RawResourceReader.readTextFileFromRawResource(context, R.raw.fragment_shader);
        final String fragmentShader1 = RawResourceReader.readTextFileFromRawResource(context, R.raw.fragment_shader1);
        final String fragmentShader2 = RawResourceReader.readTextFileFromRawResource(context, R.raw.fragment_shader2);

        final int vertexShaderHandle = ShaderHelper.compileShader(GLES30.GL_VERTEX_SHADER, vertexShader);
        final int fragmentShaderHandle = ShaderHelper.compileShader(GLES30.GL_FRAGMENT_SHADER, fragmentShader);
        final int fragmentShaderHandle1 = ShaderHelper.compileShader(GLES30.GL_FRAGMENT_SHADER, fragmentShader1);
        final int fragmentShaderHandle2 = ShaderHelper.compileShader(GLES30.GL_FRAGMENT_SHADER, fragmentShader2);

        mProgramHandle = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle,
                new String[]{"a_Position", "a_Normal", "a_TexCoordinate"});
        mProgramHandle1 = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle1,
                new String[]{"a_Position", "a_Normal", "a_TexCoordinate"});
        mProgramHandle2 = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle2,
                new String[]{"a_Position", "a_Normal", "a_TexCoordinate"});

        // Initialize the accumulated rotation matrix
        Matrix.setIdentityM(mAccumulatedRotation, 0);
        mShouldLoadTexture = false;
    }


    //******************** changes *************************/
    void readPLYObject1(String object, Bitmap tex) {
        //    Log.d("object","path: "+object);
        File file = new File(object);
        Log.d("object", "path: " + object);
        if (file == null || !file.isFile()) {
            //readPlyFromAsset(object, gl);
            return;
        }
        InputStream in = null;
        StringBuffer data = new StringBuffer();
        try {
            in = new FileInputStream(file);
            int id = 0;
            int tempI = 0;
            try {
//                byte[] buffer = new byte[in.available()];
//                while (in.read(buffer) != -1) {
//                    data.append(new String(buffer));
//                }
                //populatePlyVertices(in, tex);
                in.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
//        if(data.length()>100){
//            populatePlyVertices(new String(data),gl);
//        }
    }


//    void populatePlyVertices(InputStream dataStream, Bitmap tex) throws IOException {
//        isObjLoaded = false;
//        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(dataStream));
//
//        //    Log.d("object","path: "+object);
//        verts = new Vector<Vec3d>();
//        textures = new Vector<Vec3d>();
//        normals = new Vector<Vec3d>();
//        facePly = new Vector<int[]>();
//
////            String lines[] = data.split("\r\n");
////            if (lines.length < 10) {
////                lines = data.split("\n");
////            }
//        boolean header = true;
//        int vertLoop = 0, faceLoop = 0, property = 0, px = 0, py = 0, pz = 0, pnx = 0, pny = 0, pnz = 0, ps = 0, pt = 0;
//        //  Log.d("line",""+lines.length);
//
//        //for (int i = 0; i < lines.length; i++) {
//        String line = bufferedReader.readLine();
//        String[] elems = null;
//        while (line != null) {
//            elems = line.split(" ");
//            if (header) {
//                if (elems[0].equalsIgnoreCase("element")) {
//                    //   Log.d("elems",elems[2]);
//                    if (elems[1].equalsIgnoreCase("vertex"))
//                        vertLoop = Integer.parseInt(elems[2].trim());
//                    else
//                        faceLoop = Integer.parseInt(elems[2].trim());
//                }
//                if (elems[0].equalsIgnoreCase("property")) {
//                    if (elems[2].equalsIgnoreCase("x"))
//                        px = property;
//                    if (elems[2].equalsIgnoreCase("y"))
//                        py = property;
//                    if (elems[2].equalsIgnoreCase("z"))
//                        pz = property;
//
//                    if (elems[2].equalsIgnoreCase("nx"))
//                        pnx = property;
//                    if (elems[2].equalsIgnoreCase("ny"))
//                        pny = property;
//                    if (elems[2].equalsIgnoreCase("nz"))
//                        pnz = property;
//
//                    if (elems[2].equalsIgnoreCase("s"))
//                        ps = property;
//                    if (elems[2].equalsIgnoreCase("t"))
//                        pt = property;
//
//
//                    property++;
//                }
//                if (elems[0].equalsIgnoreCase("end_header")) {
//                    header = false;
//                }
//            } else {
//                //   Log.d("vertLoop",""+vertLoop);
//                // Log.d("verts",""+elems.length);
//                if (elems.length == 8) {
//                    float X = Float.parseFloat(elems[px].trim());
//                    float Y = Float.parseFloat(elems[py].trim());
//                    float Z = Float.parseFloat(elems[pz].trim());
//                    verts.add(new Vec3d(X, Y, Z));   // changing axis only for display
//                    vertN++;
//                    if (Y > maxX)
//                        maxX = X;
//                    if (Y > maxZ)
//                        maxZ = Y;
//                    if (Z > maxY)
//                        maxY = Z;
//
//                    if (Y < minX)
//                        minX = X;
//                    if (Y < minZ)
//                        minZ = Y;
//                    if (Z < minY)
//                        minY = Z;
//
//                    float nx = Float.parseFloat(elems[pnx].trim());
//                    float ny = Float.parseFloat(elems[pny].trim());
//                    float nz = Float.parseFloat(elems[pnz].trim());
//                    normals.add(new Vec3d(nx, ny, nz));//normal changed for display
//                    float s = Float.parseFloat(elems[ps]);
//                    float t = 1.0f - Float.parseFloat(elems[pt]);
//                    textures.add(new Vec3d(s, t, 0));
//                }
//                //   Log.d("ply",lines[i]+" "+i);
//                //   Log.d("plyelems",elems[0]+" "+elems.length);
//                if (elems.length == 4) {
//                    int f[] = new int[3];
//                    f[0] = Integer.parseInt(elems[1]);
//                    f[1] = Integer.parseInt(elems[2]);
//                    f[2] = Integer.parseInt(elems[3]);
//                    facePly.add(f);
//                    faceN++;
//                    faceLoop--;
//                    if (faceLoop <= 0)
//                        break;
//                }
//
//            }
//        }
//        //  Log.d("PLY",""+vertN+" "+faceN);
//        // Log.d("PLY",""+px+"  "+py+"  "+pz+"  "+pnx+"  "+pny+"  "+pnz+"  "+ps+"  "+pt);
//
//        float[] vertCoord = new float[vertN * 3];
//        float[] texCoord = new float[vertN * 2];
//
//        float[] normCoord = new float[vertN * 3];
//
//        int[] indexCoord = new int[faceN * 3 * 3];
//        for (int i = 0; i < vertN; i++) {
//            vertCoord[(i * 3)] = verts.get(i).getX();
//            vertCoord[(i * 3 + 1)] = verts.get(i).getY();
//            vertCoord[(i * 3 + 2)] = verts.get(i).getZ();
//            float val = (float) sqrt(normals.get(i).getX() * normals.get(i).getX() + normals.get(i).getY() * normals.get(i).getY() + normals.get(i).getZ() * normals.get(i).getZ());
//            normCoord[(i * 3)] = normals.get(i).getX() / val;
//            normCoord[(i * 3 + 1)] = normals.get(i).getY() / val;
//            normCoord[(i * 3 + 2)] = normals.get(i).getZ() / val;
//            texCoord[(i * 2)] = textures.get(i).getX();
//            texCoord[(i * 2 + 1)] = textures.get(i).getY();
//
//
//        }
//        for (int i = 0; i < faceN; i++) {
//            for (int j = 0; j < 3; j++) {
//                indexCoord[(i * 3 + j)] = facePly.get(i)[j];
//            }
//        }
//        // Initialize the buffers.
//        vertexBuffer = ByteBuffer.allocateDirect(vertCoord.length * mBytesPerFloat)
//                .order(ByteOrder.nativeOrder()).asFloatBuffer();
//        vertexBuffer.put(vertCoord).position(0);
//
//        normalBuffer = ByteBuffer.allocateDirect(normCoord.length * mBytesPerFloat)
//                .order(ByteOrder.nativeOrder()).asFloatBuffer();
//        normalBuffer.put(normCoord).position(0);
//
//        textureBuffer = ByteBuffer.allocateDirect(texCoord.length * mBytesPerFloat)
//                .order(ByteOrder.nativeOrder()).asFloatBuffer();
//        textureBuffer.put(texCoord).position(0);
//
//        indexBuffer = ByteBuffer.allocateDirect(indexCoord.length * mBytesPerInt)
//                .order(ByteOrder.nativeOrder()).asIntBuffer();
//        indexBuffer.put(indexCoord).position(0);
//
//        // Load the texture
//        if (tex != null) {
//            this.tex = tex;
//            mShouldLoadTexture = true;
//        }
//        isSet = true;
//        isObjLoaded = true;
//    }

    public void readPLYObject(String object, Bitmap tex) throws PLYLoadMismatch {
        isObjLoaded = false;
        int faceN1 = 0;
        int vertN1 = 0;
        String line = null;
        ////loadTexture = false;
        maxX = -999999;
        maxY = -999999;
        maxZ = -999999;
        minX = 999999;
        minY = 999999;
        minZ = 999999;
        //isSet = false;
//        FloatBuffer vertexBuffer1 = null , normalBuffer1 =null ,textureBuffer1 = null;
//        IntBuffer indexBuffer1 = null;
        Vector<Vec3d> verts1 = new Vector<Vec3d>();
        Vector<Vec3d> textures1 = new Vector<Vec3d>();
        Vector<Vec3d> normals1 = new Vector<Vec3d>();
        Vector<int[]> facePly1 = new Vector<int[]>();

        AssetManager am = context.getAssets();
        File file = new File(object);
        InputStream in = null;
        int id = 0;
        String data = "";
        int tempI = 0;
        try {
            if (file == null || !file.isFile()) {
                try {
                    in = am.open(object);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                in = new FileInputStream(file);
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));

            //  Log.d("line","I am here");
//            Vector<Vec3d> verts = new Vector<Vec3d>();
//            Vector<Vec3d> textures1 = new Vector<Vec3d>();
//            Vector<Vec3d> normals1 = new Vector<Vec3d>();
//            Vector<int[]> facePly1 = new Vector<int[]>();
            boolean header = true;
            int vertLoop = 0, faceLoop = 0, property = 0, px = 0, py = 0, pz = 0, pnx = 0, pny = 0, pnz = 0, ps = 0, pt = 0;
            //  Log.d("line",""+lines.length);
            line = bufferedReader.readLine();
            while (line != null) {
                String[] elems = line.split(" ");
                if (header) {
                    if (elems[0].equalsIgnoreCase("element")) {
                        //   Log.d("elems",elems[2]);
                        if (elems[1].equalsIgnoreCase("vertex"))
                            vertLoop = Integer.parseInt(elems[2].trim());
                        else
                            faceLoop = Integer.parseInt(elems[2].trim());
                    }
                    if (elems[0].equalsIgnoreCase("property")) {
                        if (elems[2].equalsIgnoreCase("x"))
                            px = property;
                        if (elems[2].equalsIgnoreCase("y"))
                            py = property;
                        if (elems[2].equalsIgnoreCase("z"))
                            pz = property;

                        if (elems[2].equalsIgnoreCase("nx"))
                            pnx = property;
                        if (elems[2].equalsIgnoreCase("ny"))
                            pny = property;
                        if (elems[2].equalsIgnoreCase("nz"))
                            pnz = property;

                        if (elems[2].equalsIgnoreCase("s"))
                            ps = property;
                        if (elems[2].equalsIgnoreCase("t"))
                            pt = property;


                        property++;
                    }
                    if (elems[0].equalsIgnoreCase("end_header")) {
                        header = false;
                    }
                } else {
                    //   Log.d("vertLoop",""+vertLoop);
                    // Log.d("verts",""+elems.length);
                    try {


                        if (elems.length == 8) {

//                        float X = Float.parseFloat(elems[px].trim());
//                        float Y = Float.parseFloat(elems[py].trim());
//                        float Z = Float.parseFloat(elems[pz].trim());

                            float X = ConstantsUtil.parseFloatTest(elems[px].trim());
                            float Y = ConstantsUtil.parseFloatTest(elems[py].trim());
                            float Z = ConstantsUtil.parseFloatTest(elems[pz].trim());

                            verts1.add(new Vec3d(X, Y, Z));   // changing axis only for display
                            vertN1++;
                            if (Y > maxX)
                                maxX = X;
                            if (Y > maxZ)
                                maxZ = Y;
                            if (Z > maxY)
                                maxY = Z;

                            if (Y < minX)
                                minX = X;
                            if (Y < minZ)
                                minZ = Y;
                            if (Z < minY)
                                minY = Z;

//                        float nx = Float.parseFloat(elems[pnx].trim());
//                        float ny = Float.parseFloat(elems[pny].trim());
//                        float nz = Float.parseFloat(elems[pnz].trim());
//                        normals1.add(new Vec3d(nx, ny, nz));//normal changed for display
//                        float s = Float.parseFloat(elems[ps]);
//                        float t = 1.0f - Float.parseFloat(elems[pt]);
//                        textures1.add(new Vec3d(s, t, 0));

                            float nx = ConstantsUtil.parseFloatTest(elems[pnx].trim());
                            float ny = ConstantsUtil.parseFloatTest(elems[pny].trim());
                            float nz = ConstantsUtil.parseFloatTest(elems[pnz].trim());
                            normals1.add(new Vec3d(nx, ny, nz));//normal changed for display
                            float s = ConstantsUtil.parseFloatTest(elems[ps]);
                            float t = 1.0f - ConstantsUtil.parseFloatTest(elems[pt]);
                            textures1.add(new Vec3d(s, t, 0));

                        }
                        //   Log.d("ply",lines[i]+" "+i);
                        //   Log.d("plyelems",elems[0]+" "+elems.length);
                        if (elems.length == 4) {
                            int f[] = new int[3];
                            f[0] = Integer.parseInt(elems[1]);
                            f[1] = Integer.parseInt(elems[2]);
                            f[2] = Integer.parseInt(elems[3]);
                            facePly1.add(f);
                            faceN1++;

                        }
                    } catch (Exception e) {
                        isObjLoaded = true;
                        loadTexture = true;
                        isSet = true;
                        throw new PLYLoadMismatch(vertN1, vertLoop, faceN1, faceLoop);
                    }
                }
                elems = null;
                line = bufferedReader.readLine();
            }
            if (vertN1 != vertLoop || faceN1 != faceLoop || vertN1 == 0 || faceN1 == 0) {
                isObjLoaded = false;
                loadTexture = true;
                isSet = false;
                throw new PLYLoadMismatch(vertN1, vertLoop, faceN1, faceLoop);
            }
//              Log.d(TAG,"vert: "+vertN+" face: "+faceN);
//              Log.d(TAG,"verts: "+verts.size());
            // Log.d("PLY",""+px+"  "+py+"  "+pz+"  "+pnx+"  "+pny+"  "+pnz+"  "+ps+"  "+pt);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

//       this.vertexBuffer = vertexBuffer;
//       this.normalBuffer =  normalBuffer;
//        this.textureBuffer = textureBuffer;
//        this.indexBuffer = indexBuffer;
        loadTexture = false;
        isSet = false;
        this.faceN = faceN1;
        this.vertN = vertN1;
        this.verts = verts1;
        this.textures = textures1;
        this.normals = normals1;
        this.facePly = facePly1;
        this.
                vertexBuffer = normalBuffer = textureBuffer = null;
        indexBuffer = null;

        float[] vertCoord = new float[vertN * 3];
        float[] texCoord = new float[vertN * 2];

        float[] normCoord = new float[vertN * 3];

        int[] indexCoord = new int[faceN * 3 * 3];
        for (int i = 0; i < vertN; i++) {
            vertCoord[(i * 3)] = verts.get(i).getX();
            vertCoord[(i * 3 + 1)] = verts.get(i).getY();
            vertCoord[(i * 3 + 2)] = verts.get(i).getZ();
            float val = (float) sqrt(normals.get(i).getX() * normals.get(i).getX() + normals.get(i).getY() * normals.get(i).getY() + normals.get(i).getZ() * normals.get(i).getZ());
            normCoord[(i * 3)] = normals.get(i).getX() / val;
            normCoord[(i * 3 + 1)] = normals.get(i).getY() / val;
            normCoord[(i * 3 + 2)] = normals.get(i).getZ() / val;
            texCoord[(i * 2)] = textures.get(i).getX();
            texCoord[(i * 2 + 1)] = textures.get(i).getY();


        }
        //sort(new Vec3d(0,0,0));
        for (int i = 0; i < faceN; i++) {
            for (int j = 0; j < 3; j++) {
                indexCoord[(i * 3 + j)] = facePly.get(i)[j];
            }
        }

        // Initialize the buffers.
        vertexBuffer = ByteBuffer.allocateDirect(vertCoord.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(vertCoord).position(0);

        normalBuffer = ByteBuffer.allocateDirect(normCoord.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        normalBuffer.put(normCoord).position(0);

        textureBuffer = ByteBuffer.allocateDirect(texCoord.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureBuffer.put(texCoord).position(0);

        indexBuffer = ByteBuffer.allocateDirect(indexCoord.length * mBytesPerInt)
                .order(ByteOrder.nativeOrder()).asIntBuffer();
        indexBuffer.put(indexCoord).position(0);

        // Load the texture
        if (tex != null) {
            this.tex = tex;
            mShouldLoadTexture = true;
        }
        isSet = true;
        isObjLoaded = true;
    }


    //    public void sort(Vec3d cam)
//    {
//        int[] indexCoord=new int[faceN * 3 * 3];
//        for (int i = 0; i < faceN-1; i++) {
//            for (int j = i+1; j < faceN; j++) {
//                if(dist(facePly.get(i),cam)>dist(facePly.get(j),cam)){
//                    int[] temp = facePly.get(i);
//                    facePly.set(i,facePly.get(j));
//                    facePly.set(j,temp);
//                }
//            }
//        }
//        for (int i = 0; i < faceN; i++) {
//            for (int j = 0; j < 3; j++) {
//                indexCoord[(i * 3 + j)] = facePly.get(i)[j];
//            }
//        }
//        indexBuffer = ByteBuffer.allocateDirect(indexCoord.length * mBytesPerInt)
//                .order(ByteOrder.nativeOrder()).asIntBuffer();
//        indexBuffer.put(indexCoord).position(0);
//        indexCoord=null;
//    }
//    public void sort() {
//        int maxVal = facePly.size();
//        int [] bucket=new int[maxVal+1];
//
//        for (int i=0; i<bucket.length; i++) {
//            bucket[i]=0;
//        }
//
//        for (int i=0; i<a.length; i++) {
//            bucket[a[i]]++;
//        }
//
//        int outPos=0;
//        for (int i=0; i<bucket.length; i++) {
//            for (int j=0; j<bucket[i]; j++) {
//                a[outPos++]=i;
//            }
//        }
//    }
    private float dist(int[] face, Vec3d cam) {
        float d0 = abs(verts.get(face[0]).getZ() - cam.getZ());
        float d1 = abs(verts.get(face[1]).getZ() - cam.getZ());
        float d2 = abs(verts.get(face[2]).getZ() - cam.getZ());
        return (d0 + d1 + d2) / 3;
    }


    public void setMinFilter(final int filter) {
        if (mBrickDataHandle != 0 && mGrassDataHandle != 0) {
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mBrickDataHandle);
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, filter);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mGrassDataHandle);
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, filter);
        } else {
            mQueuedMinFilter = filter;
        }
    }

    public void setMagFilter(final int filter) {
        if (mBrickDataHandle != 0 && mGrassDataHandle != 0) {
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mBrickDataHandle);
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, filter);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mGrassDataHandle);
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, filter);
        } else {
            mQueuedMagFilter = filter;
        }
    }

    public void draw(float[] mViewMatrix, float[] mProjectionMatrix, float angle, int shader, boolean cull) {
//        mLightPosInModelSpace1[0]= (float) (1200*sin(-45*PI/180));
//        mLightPosInModelSpace1[2]= (float) (1200*cos(-45*PI/180));
//
//        mLightPosInModelSpace2[0]= (float) (1200*sin(-165*PI/180));
//        mLightPosInModelSpace2[2]= (float) (1200*cos(-165*PI/180));
//
//        mLightPosInModelSpace3[0]= (float) (1200*sin(-285*PI/180));
//        mLightPosInModelSpace3[2]= (float) (1200*cos(-285*PI/180));
        int mPHandle = 0;
        if (shader == 0)
            mPHandle = this.mProgramHandle;
        if (shader == 1)
            mPHandle = this.mProgramHandle1;
        if (shader == 2)
            mPHandle = this.mProgramHandle2;

        if (shader == 2) {
            GLES30.glEnable(GLES30.GL_BLEND);
            GLES30.glDepthMask(false);
        }
        if (cull) {
            GLES30.glEnable(GLES30.GL_CULL_FACE);
        }
        if (mShouldLoadTexture) {
            mBrickDataHandle = TextureHelper.loadTexture(context, tex);
            GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D);


            if (mQueuedMinFilter != 0) {
                setMinFilter(mQueuedMinFilter);
            }

            if (mQueuedMagFilter != 0) {
                setMagFilter(mQueuedMagFilter);
            }
            loadTexture = true;
            Log.d(TAG, "texture loaded");
            mShouldLoadTexture = false;
        }
        // Do a complete rotation every 10 seconds.
        long time = SystemClock.uptimeMillis() % 10000L;
        long slowTime = SystemClock.uptimeMillis() % 100000L;
        float angleInDegrees = (360.0f / 10000.0f) * ((int) time);
        float slowAngleInDegrees = (360.0f / 100000.0f) * ((int) slowTime);
        // Set our per-vertex lighting program.
        GLES30.glUseProgram(mPHandle);

        // Set program handles for drawing.
        mMVPMatrixHandle = GLES30.glGetUniformLocation(mPHandle, "u_MVPMatrix");
        mMVMatrixHandle = GLES30.glGetUniformLocation(mPHandle, "u_MVMatrix");
        mLightPosHandle1 = GLES30.glGetUniformLocation(mPHandle, "u_LightPos1");
        mLightPosHandle2 = GLES30.glGetUniformLocation(mPHandle, "u_LightPos2");
        //mLightPosHandle3 = GLES30.glGetUniformLocation(mPHandle, "u_LightPos3");
        //mLightPosHandle4 = GLES30.glGetUniformLocation(mPHandle, "u_LightPos4");

        mLightDirHandle1 = GLES30.glGetUniformLocation(mPHandle, "u_LightDir1");
        mLightDirHandle2 = GLES30.glGetUniformLocation(mPHandle, "u_LightDir2");
        //mLightDirHandle3 = GLES30.glGetUniformLocation(mPHandle, "u_LightDir3");
        //mLightDirHandle4 = GLES30.glGetUniformLocation(mPHandle, "u_LightDir4");

        mTextureUniformHandle = GLES30.glGetUniformLocation(mPHandle, "u_Texture");
        mPositionHandle = GLES30.glGetAttribLocation(mPHandle, "a_Position");
        mNormalHandle = GLES30.glGetAttribLocation(mPHandle, "a_Normal");
        mTextureCoordinateHandle = GLES30.glGetAttribLocation(mPHandle, "a_TexCoordinate");

//        // Calculate position of the light. Rotate and then push into the distance.
        Matrix.setIdentityM(mLightModelMatrix, 0);
//        Matrix.translateM(mLightModelMatrix, 0, 0.0f, 0.0f, -2.0f);
        Matrix.rotateM(mLightModelMatrix, 0, (float) ((angle * 180) / PI), 0.0f, 1.0f, 0.0f);
//        Matrix.translateM(mLightModelMatrix, 0, 0.0f, 0.0f, 3.5f);
//
        Matrix.multiplyMV(mLightPosInWorldSpace1, 0, mLightModelMatrix, 0, mLightPosInModelSpace1, 0);
        Matrix.multiplyMV(mLightPosInEyeSpace1, 0, mViewMatrix, 0, mLightPosInWorldSpace1, 0);
        Matrix.multiplyMV(mLightPosInWorldSpace2, 0, mLightModelMatrix, 0, mLightPosInModelSpace2, 0);
        Matrix.multiplyMV(mLightPosInEyeSpace2, 0, mViewMatrix, 0, mLightPosInWorldSpace2, 0);
        //Matrix.multiplyMV(mLightPosInWorldSpace3, 0, mLightModelMatrix, 0, mLightPosInModelSpace3, 0);
        //Matrix.multiplyMV(mLightPosInEyeSpace3, 0, mViewMatrix, 0, mLightPosInWorldSpace3, 0);
        //Matrix.multiplyMV(mLightPosInWorldSpace4, 0, mLightModelMatrix, 0, mLightPosInModelSpace4, 0);
        //Matrix.multiplyMV(mLightPosInEyeSpace4, 0, mViewMatrix, 0, mLightPosInWorldSpace4, 0);
//
        Matrix.multiplyMV(mLightDirInWorldSpace1, 0, mLightModelMatrix, 0, mLightDirInModelSpace1, 0);
        Matrix.multiplyMV(mLightDirInEyeSpace1, 0, mViewMatrix, 0, mLightDirInWorldSpace1, 0);
        Matrix.multiplyMV(mLightDirInWorldSpace2, 0, mLightModelMatrix, 0, mLightDirInModelSpace2, 0);
        Matrix.multiplyMV(mLightDirInEyeSpace2, 0, mViewMatrix, 0, mLightDirInWorldSpace2, 0);
        //Matrix.multiplyMV(mLightDirInWorldSpace3, 0, mLightModelMatrix, 0, mLightDirInModelSpace3, 0);
        //Matrix.multiplyMV(mLightDirInEyeSpace3, 0, mViewMatrix, 0, mLightDirInWorldSpace3, 0);
        //Matrix.multiplyMV(mLightDirInWorldSpace4, 0, mLightModelMatrix, 0, mLightDirInModelSpace4, 0);
        //Matrix.multiplyMV(mLightDirInEyeSpace4, 0, mViewMatrix, 0, mLightDirInWorldSpace4, 0);

        // Draw a cube.
        // Translate the cube into the screen.
        Matrix.setIdentityM(mModelMatrix, 0);
        //  Matrix.translateM(mModelMatrix, 0, 0.0f, 0.8f, -3.5f);

        // Set a matrix that contains the current rotation.
        Matrix.setIdentityM(mCurrentRotation, 0);
        // Matrix.rotateM(mCurrentRotation, 0, angle, 0.0f, 1.0f, 0.0f);
        //     Matrix.rotateM(mCurrentRotation, 0, 0, 1.0f, 0.0f, 0.0f);


//        // Multiply the current rotation by the accumulated rotation, and then set the accumulated rotation to the result.
//        Matrix.multiplyMM(mTemporaryMatrix, 0, mCurrentRotation, 0, mAccumulatedRotation, 0);
//        System.arraycopy(mTemporaryMatrix, 0, mAccumulatedRotation, 0, 16);

        // Rotate the cube taking the overall rotation into account.
        Matrix.multiplyMM(mTemporaryMatrix, 0, mModelMatrix, 0, mCurrentRotation, 0);
        System.arraycopy(mTemporaryMatrix, 0, mModelMatrix, 0, 16);
        if (loadTexture) {

            GLES30.glEnable(GLES30.GL_BLEND);
            GLES30.glBlendFunc(GLES30.GL_ONE, GLES30.GL_ONE_MINUS_SRC_ALPHA);

            // Set the active texture unit to texture unit 0.
            GLES30.glActiveTexture(GLES30.GL_TEXTURE0);

            // Bind the texture to this unit.
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mBrickDataHandle);

            // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
            GLES30.glUniform1i(mTextureUniformHandle, 0);

            // Pass in the texture coordinate information
            textureBuffer.position(0);
            GLES30.glVertexAttribPointer(mTextureCoordinateHandle, mTextureCoordinateDataSize, GLES30.GL_FLOAT, true,
                    0, textureBuffer);

        }
        GLES30.glEnableVertexAttribArray(mTextureCoordinateHandle);

        // Pass in the position information
        vertexBuffer.position(0);
        GLES30.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES30.GL_FLOAT, false,
                0, vertexBuffer);

        GLES30.glEnableVertexAttribArray(mPositionHandle);

        // Pass in the normal information
        normalBuffer.position(0);
        GLES30.glVertexAttribPointer(mNormalHandle, mNormalDataSize, GLES30.GL_FLOAT, false,
                0, normalBuffer);

        GLES30.glEnableVertexAttribArray(mNormalHandle);

        // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
        // (which currently contains model * view).
        //  Matrix.setIdentityM(mMVPMatrix, 0);

        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);

        // Pass in the modelview matrix.
        GLES30.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mMVPMatrix, 0);

        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        Matrix.multiplyMM(mTemporaryMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
        System.arraycopy(mTemporaryMatrix, 0, mMVPMatrix, 0, 16);

        // Pass in the combined matrix.
        GLES30.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        // Pass in the light position in eye space.
        GLES30.glUniform3f(mLightPosHandle1, mLightPosInEyeSpace1[0], mLightPosInEyeSpace1[1], mLightPosInEyeSpace1[2]);
        GLES30.glUniform3f(mLightPosHandle2, mLightPosInEyeSpace2[0], mLightPosInEyeSpace2[1], mLightPosInEyeSpace2[2]);
        //GLES30.glUniform3f(mLightPosHandle3, mLightPosInEyeSpace3[0], mLightPosInEyeSpace3[1], mLightPosInEyeSpace3[2]);
        //GLES30.glUniform3f(mLightPosHandle4, mLightPosInEyeSpace4[0], mLightPosInEyeSpace4[1], mLightPosInEyeSpace4[2]);

        GLES30.glUniform3f(mLightDirHandle1, mLightDirInEyeSpace1[0], mLightDirInEyeSpace1[1], mLightDirInEyeSpace1[2]);
        GLES30.glUniform3f(mLightDirHandle2, mLightDirInEyeSpace2[0], mLightDirInEyeSpace2[1], mLightDirInEyeSpace2[2]);
        //GLES30.glUniform3f(mLightDirHandle3, mLightDirInEyeSpace3[0], mLightDirInEyeSpace3[1], mLightDirInEyeSpace3[2]);
        //GLES30.glUniform3f(mLightDirHandle4, mLightDirInEyeSpace4[0], mLightDirInEyeSpace4[1], mLightDirInEyeSpace4[2]);

        // Draw the cube.
        // GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 36);
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, indexBuffer.limit(), GLES30.GL_UNSIGNED_INT, indexBuffer);

        if (cull) {
            GLES30.glDisable(GLES30.GL_CULL_FACE);
        }
        if (shader == 2) {
            GLES30.glDisable(GLES30.GL_BLEND);
            GLES30.glDepthMask(true);
        }
        // GLES30.glDisable(GLES30.GL_CULL_FACE);

    }


}
