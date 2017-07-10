package com.svc.sml.Graphics;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.opengl.GLES30;
import android.util.Log;

import com.svc.sml.InkarneAppContext;
import com.svc.sml.R;
import com.svc.sml.Utility.ConstantsUtil;
import com.svc.sml.Utility.SMLEncoder;

import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by himanshu on 3/11/16.
 */



public class Screenshot {


    private SMLEncoder enc=null;

    public interface ScreenShotListener {
        void onScreenShotTaken(Bitmap bitmap);
        void onScreenShotTaken(String imageFilePath);
    }

    private ScreenShotListener listner;
    float x, y,  width,  height;
    GL10 gl;

    public Screenshot(int w, int h){
        this.width = w;
        this.height = h;
    }

    public Screenshot(){

    }

    public void saveVideoStart(int width, int height) {
        Log.d("V360","Video Creation Starting...");

        String filePath = ConstantsUtil.FILE_PATH_APP_ROOT_VIDEO+ConstantsUtil.VIDEO_SHARE_FILENAME;
        //String filePath = ConstantsUtil.FILE_PATH_APP_ROOT_VIDEO+ConstantsUtil.VIDEO_SHARE_FILENAME;

        Random generator = new Random();
        int n = 20;
        n = generator.nextInt(n);
        String fName = "shareImage-" + n + ".png";
        File file = new File(filePath);
//        if(file != null)
//            file.delete();

        enc = null;
        try {
            enc = new SMLEncoder(file,width,height);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("Screenshot","360 saveVideoStart Done");
    }

    public void saveVideoAddImage(Bitmap bitmap) {
        try {
//            if(enc==null)
//                saveVideoStart();
            enc.encodeNativeFrame(this.fromBitmap(bitmap));
            Log.e("Screenshot", "360 saveVideoAddImage ");
            //bitmap.recycle();

        } catch (Exception e) {
            Log.e("Screenshot", "360 error saveVideoAddImage");
            e.printStackTrace();
        }
    }

    public void saveVideoFinish(){
        try {
            enc.finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void stopVideo(){
            enc=null;
    }
    public Picture fromBitmap(Bitmap src) {
        Picture dst = Picture.create(src.getWidth(), src.getHeight(), ColorSpace.RGB);
        fromBitmap(src, dst);
        return dst;
    }
    public void fromBitmap(Bitmap src, Picture dst) {
        int[] dstData = dst.getPlaneData(0);
        int[] packed = new int[src.getWidth() * src.getHeight()];

        src.getPixels(packed, 0, src.getWidth(), 0, 0, src.getWidth(), src.getHeight());

        for (int i = 0, srcOff = 0, dstOff = 0; i < src.getHeight(); i++) {
            for (int j = 0; j < src.getWidth(); j++, srcOff++, dstOff += 3) {
                int rgb = packed[srcOff];
                dstData[dstOff] = (rgb >> 16) & 0xff;
                dstData[dstOff + 1] = (rgb >> 8) & 0xff;
                dstData[dstOff + 2] = rgb & 0xff;
            }
        }
    }

    public void SaveToFile(Bitmap bitmap, int size, String filePath, String fileName) {
        String TAG = "Saving screenshot image";
        OutputStream outStream = null;
        filePath = ConstantsUtil.FILE_PATH_APP_ROOT + filePath;
        File dir = new File( filePath );
        dir.mkdirs();
        File output = new File( filePath, fileName+".png" );
        try {
            outStream = new FileOutputStream(output);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
            Log.v(TAG, "Saving Screenshot [" + filePath + fileName + "]");
        } catch ( FileNotFoundException e ) {
            Log.e( TAG, "" + e.getMessage() );
        } catch ( IOException e ) {
            Log.e( TAG, "" + e.getMessage() );
        }
    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        float ratio = Math.min(
                maxImageSize / realImage.getWidth(),
                maxImageSize / realImage.getHeight());
        int width = Math.round(ratio * realImage.getWidth());
        int height = Math.round(ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }

    public synchronized Bitmap takeScreenshot(final float x, final float y, final float width, final float height,final GL10 gl, String fileName) {
        String filePath = ConstantsUtil.FILE_PATH_SHARE;
        Bitmap bitmap1 = takeScreenshot(x, y, width, height);
       // Bitmap bitmap = scaleDown(bitmap1, ConstantsFunctional.SIZE_IMAGE_SHARED,true);//TODO

        SaveToFile(bitmap1,(int)(width*height),filePath,fileName);
        return bitmap1;
    }


    public synchronized Bitmap takeVideoScreen(final float x, final float y, final float width, final float height) {
//        if(enc == null){
//            saveVideoStart((int)width, (int)height) ;
//        }
        String filePath = ConstantsUtil.FILE_PATH_SHARE;
        Bitmap bitmap1 = takeVideoScreenshot(x, y, width, height);
        // Bitmap bitmap = scaleDown(bitmap1, ConstantsFunctional.SIZE_IMAGE_SHARED,true);//TODO
        return bitmap1;
    }
    public Bitmap takeScreenshot(float x, float y, float width, float height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        return takeScreenshot();
    }
    public Bitmap takeVideoScreenshot(float x, float y, float width, float height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        return takeVideoScreenshot();
    }
    public Bitmap takeScreenshot()
    {
        int width = (int)this.width;
        int height = (int)this.height;
        int size = width * height;
        ByteBuffer buf = ByteBuffer.allocateDirect(size * 4);
        buf.order(ByteOrder.nativeOrder());
        GLES30.glReadPixels((int)x, (int)y, width, height, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, buf);
        int data[] = new int[size];
        buf.asIntBuffer().get(data);
        buf = null;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        bitmap.setPixels(data, size-width, -width, (int)x, (int)y, width, height);
        data = null;

        short sdata[] = new short[size];
        ShortBuffer sbuf = ShortBuffer.wrap(sdata);
        bitmap.copyPixelsToBuffer(sbuf);
        for (int i = 0; i < size; ++i) {
            //BGR-565 to RGB-565
            short v = sdata[i];
            sdata[i] = (short) (((v&0x1f) << 11) | (v&0x7e0) | ((v&0xf800) >> 11));
        }
        sbuf.rewind();
        bitmap.copyPixelsFromBuffer(sbuf);
        return bitmap;
        //return bitmapWithText(bitmap);
        //return bitmapWithLogo(bitmap);
    }
    public Bitmap takeVideoScreenshot()
    {
        int width = (int)this.width;
        int height = (int)this.height;
        int size = width * height;
        ByteBuffer buf = ByteBuffer.allocateDirect(size * 4);
        buf.order(ByteOrder.nativeOrder());
        GLES30.glReadPixels((int)x, (int)y, width, height, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, buf);
        int data[] = new int[size];
        buf.asIntBuffer().get(data);
        buf = null;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        bitmap.setPixels(data, size-width, -width, (int)x, (int)y, width, height);
        data = null;

        short sdata[] = new short[size];
        ShortBuffer sbuf = ShortBuffer.wrap(sdata);
        bitmap.copyPixelsToBuffer(sbuf);
        for (int i = 0; i < size; ++i) {
            //BGR-565 to RGB-565
            short v = sdata[i];
            sdata[i] = (short) (((v&0x1f) << 11) | (v&0x7e0) | ((v&0xf800) >> 11));
        }
        sbuf.rewind();
        bitmap.copyPixelsFromBuffer(sbuf);

        //return bitmap;
        //return bitmapWithText(bitmap);
        return bitmap;
    }
    public Bitmap bitmapWithLogo(Bitmap bitmap) {
        Bitmap.Config config = bitmap.getConfig();
        if (config == null) {
            config = Bitmap.Config.ARGB_8888;
        }

        int paddingTopBottom = 25;
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight()+paddingTopBottom*2, config);
        Canvas newCanvas = new Canvas(newBitmap);
        newCanvas.drawARGB(0xFF, 0xFF, 0xFF, 0xFF);
        newCanvas.drawBitmap(bitmap, 0, paddingTopBottom * 2, null);
        Bitmap bmLogo = BitmapFactory.decodeResource(InkarneAppContext.getAppContext().getResources(), R.drawable.logo_full_105);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //newCanvas.drawBitmap(bmLogo,(bitmap.getWidth() - bmLogo.getWidth())/2, (paddingTopBottom*2-bmLogo.getHeight())/2,paint);
        newCanvas.drawBitmap(bmLogo, 20, (paddingTopBottom * 2 - bmLogo.getHeight()) / 2 + 7, paint);
        return newBitmap;
    }

    /***** async *****/
    /*
    public Bitmap bitmapWithText(Bitmap bitmap) {
        Bitmap.Config config = bitmap.getConfig();
        if (config == null) {
            config = Bitmap.Config.ARGB_8888;
        }

        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), config);
        Canvas newCanvas = new Canvas(newBitmap);

        newCanvas.drawBitmap(bitmap, 0, 0, null);

        String captionString = ConstantsUtil.TEXT_ON_SHARED_PICS;
        Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintText.setColor(Color.BLUE);
        paintText.setTextSize(50);

        paintText.setStyle(Paint.Style.FILL);
        paintText.setShadowLayer(10f, 6f, 6f, Color.BLACK);

        Rect rectText = new Rect();
        paintText.getTextBounds(captionString, 0, captionString.length(), rectText);

        newCanvas.drawText(captionString,
                bitmap.getWidth() / 2, rectText.height(), paintText);
        return newBitmap;
    }
    */

    /*
    public synchronized void takeScreenShotAsync(float x, float y, float width, float height, GL10 gl,ScreenShotListener listner) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.gl =gl;
        this.listner = listner;
        new TakeScreenShotTask().execute(this);
    }

    private class TakeScreenShotTask extends AsyncTask<Screenshot, Integer, String> {

        protected String doInBackground(Screenshot... screenshots) {
            Screenshot screenshot = screenshots[0];
            Bitmap bitmap = screenshot.takeScreenshot();
            return saveImage(bitmap);
        }

        protected void onProgressUpdate(Integer... progress) {

        }

        protected void onPostExecute(String result) {
            Screenshot.this.listner.onScreenShotTaken(result);
        }
    }
    */

    private String saveImage(Bitmap finalBitmap) {
        String filePath = "";
        String root = ConstantsUtil.FILE_PATH_APP_ROOT;
        File myDir = new File(root + "/inkarne/share");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 4;
        n = generator.nextInt(n);
        String fName = "shareImage-" + n + ".png";
        File file = new File(myDir, fName);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            filePath = file.getAbsolutePath();
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return filePath;
    }
}
