/*
package com.svc.inkarne;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.svc.inkarne.Activity.BaseActivity;
import com.svc.inkarne.Activity.RedoAvatarActivity;
import com.svc.inkarne.Database.ComboData;
import com.svc.inkarne.Database.User;
import com.svc.inkarne.Fragments.CartFragment;
import com.svc.inkarne.Fragments.CollectionFragment;
import com.svc.inkarne.Fragments.LookLikeFragment;
import com.svc.inkarne.Fragments.ShopMixMatchFragment;
import com.svc.inkarne.Graphics.IRenderer;
import com.svc.inkarne.Graphics.Screenshot;
import com.svc.inkarne.Helper.ComboDownloader;
import com.svc.inkarne.Helper.DataManager;
import com.svc.inkarne.Model.BaseAccessoryItem;
import com.svc.inkarne.Model.FaceItem;
import com.svc.inkarne.Utility.ConstantsUtil;
import com.svc.inkarne.View.LoadingView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

public class ShopGlViewRenderer extends IRenderer {
    private final Context context;
    private int state;
    //public int lookAtObjn = GL_INDEX_BODY;
    public int lookAtObjn = 0;

    //@Override
    public void glInit() {
        state = 0;
        //setCamScript(R.raw.camscript1);
        Log.d(LOGTAG, "glInit");
        setAcceptTouch(false);
        loadAvatar();
        if (currentComboData != null) {
            Log.d(LOGTAG, "Avatar Loaded");
            resetMixMatch(currentComboData);
            //Load 7 SKUs
            changeObjects(currentComboData);
            //setLookat(lookAtObjn);
            viewLookAt(lookAtObjn);
            setAcceptTouch(true);
        } else {//TODO

        }
    }

    public void changeLookAt(int renderedObjectIndex) {
        setLookat(renderedObjectIndex);
    }

    public void zoomAnimation() {
        Log.d("Zoom", "I ah here");
        runAutoZoom();
    }

    protected void loadAvatar() {
        if (faceItem == null)
            return;
        setAcceptTouch(false);
        Log.e(LOGTAG, "LOAD AVATAR");
        if (User.getInstance().getmGender().equals("m")) {
            Log.d(LOGTAG, "BG texture path:  bg_shop_screen_male");
            myRenderer.changeObj(GL_INDEX_BG, "ply_bg_shop_screen_male.ply", myRenderer.readTextureFromAsset(R.drawable.texture_bg_shop_screen_male), false);
        } else {
            Log.d(LOGTAG, "BG texture path:  bg_shop_screen_female");
            myRenderer.changeObj(GL_INDEX_BG, "ply_bg_shop_screen_female.ply", myRenderer.readTextureFromAsset(R.drawable.texture_bg_shop_screen_female), false);
        }


        Log.e(LOGTAG, "AVATAR: GL_INDEX_BODY");
        renderObj(GL_INDEX_BODY, faceItem.getBodyPngkey(), faceItem.getBodyObjkey(), false);

        Log.e(LOGTAG, "AVATAR: GL_INDEX_FACE");
        renderObj(GL_INDEX_FACE, faceItem.getFacePngkey(), faceItem.getFaceObjkey(), false);

        Log.e(LOGTAG, "AVATAR: GL_INDEX_HAIR_A8");
        if (faceItem.getHairObjkey() != null && faceItem.getHairObjkey().length() != 0) {
            String WigObj = StoragePath + "/" + faceItem.getHairObjkey();
            String WigPng = StoragePath + "/" + faceItem.getHairPngKey();
            Log.e(LOGTAG, "GL_INDEX_HAIR_A8  Index: " + GL_INDEX_HAIR_A8 + "  Texture :" + WigPng + "  Obj :" + WigObj);
            if (ConstantsUtil.checkFileKeysExist(faceItem.getHairObjkey(), faceItem.getHairPngKey())) {
                myRenderer.changeObj(GL_INDEX_HAIR_A8, WigObj, myRenderer.readTexture(WigPng), true);
                MixMatchSharedResource.getInstance().addAccessory(ConstantsUtil.EAccessoryType.eAccTypeHair.toString(), faceItem.getHairstyleId());
            }
        }

        Log.e(LOGTAG, "AVATAR: GL_INDEX_SPECS_A9");
        if (faceItem.getSpecsObjkey() != null && faceItem.getSpecsObjkey().length() != 0) {
            String SpecsObj = StoragePath + "/" + faceItem.getSpecsObjkey();
            String SpecsPng = StoragePath + "/" + faceItem.getSpecsPngkey();
            Log.e(LOGTAG, "GL_INDEX_SPECS_A9  Index: " + GL_INDEX_SPECS_A9 + "  Texture :" + SpecsPng + "  Obj :" + SpecsObj);
            if (ConstantsUtil.checkFileKeysExist(faceItem.getSpecsObjkey(), faceItem.getSpecsPngkey())) {
                myRenderer.changeObj(GL_INDEX_SPECS_A9, SpecsObj, myRenderer.readTexture(SpecsPng), true);
                MixMatchSharedResource.getInstance().addAccessory(ConstantsUtil.EAccessoryType.eAccTypeSpecs.toString(), faceItem.getSpecsId());
            }
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);
        if (isTakeScreenShot && !isTakingScreenshot) {
            isTakeScreenShot = false;
            isTakingScreenshot = true;
            final String filename = ConstantsUtil.FILE_NAME_SHARE;
            new Screenshot().takeScreenshot(0, 0, width, height, gl, filename);
            isTakingScreenshot = false;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pbGLView.setVisibility(View.INVISIBLE);
                    shareNow(filename + ".png");
                    Log.e(LOGTAG, "onDrawFrame share");
                }
            });
        }
    }

    //@Override
    public void changeState(int change) {
        int iComboIndex = 0;//TODO
        int state = iComboIndex + change;
        String side;
        if (state < 0) {
            state = 0;
            Log.d(LOGTAG, "Left Limit Achieved");
            return;
        }
        ArrayList<ComboData> comboDataList = null;//TODO
        if (state >= comboDataList.size()) {
            Log.d(LOGTAG, "Right Limit Achieved");
            //loadNextComboByCount();
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
        //shiftObj(side);
        if (side.equalsIgnoreCase(SIDE_LEFT)) {
            resetMixMatch(currentComboData);
            iComboIndex -= 1;
            ComboData comboData = comboDataList.get(iComboIndex);
            loadCombo(comboData, iComboIndex);
            currentComboData = comboData;
        } else {
            resetMixMatch(currentComboData);
            iComboIndex += 1;
            ComboData comboData = comboDataList.get(iComboIndex);
            loadCombo(comboData, iComboIndex);
            currentComboData = comboData;
        }


    }

    public void renderMixMatchObj(int index, BaseAccessoryItem item, boolean shouldShine) {
//            setAcceptTouch(false);
//            String pngFilePath = StoragePath + "/" + item.getTextureAwsKey();
//            String objFilePath = StoragePath + "/" + item.getObjAwsKey();
//            Log.d(LOGTAG, "renderMixMatchObj Index: " + index + " Texture :" + pngFilePath + "  Obj :" + objFilePath);
//            if (ConstantsUtil.checkFileKeysExist(item.getTextureAwsKey(), item.getObjAwsKey())) {
//                myRenderer.changeObj(index, objFilePath, myRenderer.readTexture(pngFilePath), shouldShine);
//                MixMatchSharedResource.getInstance().addAccessory(item);
//            }
//            changeObjFinish();
//            setAcceptTouch(true);
        renderMixMatchObj(index, item, shouldShine, true);
    }

    public void renderMixMatchObj(int index, BaseAccessoryItem item, boolean shouldShine, boolean shouldRender) {
        if (shouldRender)
            setAcceptTouch(false);
        String pngFilePath = StoragePath + "/" + item.getTextureAwsKey();
        String objFilePath = StoragePath + "/" + item.getObjAwsKey();
        Log.d(LOGTAG, "renderMixMatchObj Index: " + index + " Texture :" + pngFilePath + "  Obj :" + objFilePath);
        if (ConstantsUtil.checkFileKeysExist(item.getTextureAwsKey(), item.getObjAwsKey())) {
            myRenderer.changeObj(index, objFilePath, myRenderer.readTexture(pngFilePath), shouldShine);
            MixMatchSharedResource.getInstance().addAccessory(item);
        }
        //mGLView.requestRender();
        if (shouldRender) {
            changeObjFinish();
            setAcceptTouch(true);
        }
    }

    public void resetRenderMixMatchObj(int index) {
        setAcceptTouch(false);
        resetObj(index);
        changeObjFinish();
        setAcceptTouch(true);
    }

    public boolean renderObj(final int index, final String pngKey, final String objKey, final boolean shouldShine) {
        return renderObj(index, pngKey, objKey, false, shouldShine);
    }

    public synchronized boolean renderObj(final int index, final String pngKey, final String objKey, final boolean isLocking, final boolean shouldShine) {
        final boolean[] iAssetReady = {true};

        String pngFilePath = StoragePath + "/" + pngKey;
        String objFilePath = StoragePath + "/" + objKey;
        Log.d(LOGTAG, "renderObj Index: " + index + " Texture :" + pngFilePath + "  Obj :" + objFilePath);

        if (ConstantsUtil.checkFileKeysExist(pngKey, objKey)) {
            Log.e(LOGTAG, "renderObj Index: " + index + " Texture :" + pngFilePath + "  Obj :" + objFilePath);
            myRenderer.changeObj(index, objFilePath, myRenderer.readTexture(pngFilePath), shouldShine);
        } else {
//                    try {
//                        new AssetDownloader(ShopActivity.this).downloadAsset(pngKey, objKey, new AssetDownloader.OnAssetDownloadListener() {
//                            @Override
//                            public void onDownload() {
////                                if(isLocking)
////                                doneSignal.countDown();
//                                String pngFilePath = StoragePath + "/" + pngKey;
//                                String objFilePath = StoragePath + "/" + objKey;
//                                Log.e(LOGTAG, "onDownload Index: " + index + " Texture :" + pngKey + "  Obj :" + objKey);
//                                myRenderer.changeObj(index, objFilePath, myRenderer.readTexture(pngFilePath), side);
//                            }
//
//                            @Override
//                            public void onDownloadFailed() {
////                                if(isLocking)
////                                doneSignal.countDown();
//                                iAssetReady[0] = false;
//                            }
//                        });
////                        int x =0;
////                        Log.d(LOGTAG,"await");
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        if(isLocking)
////                        doneSignal.countDown();
//                        iAssetReady[0] = false;
//                    }

            iAssetReady[0] = false;
        }
        return iAssetReady[0];
    }

    public boolean renderLeg(ComboData comboData) {
        final boolean[] iAssetReady = {true};
        if (comboData.getLegId() != null && comboData.getLegId().length() != 0) {
            if (comboData.getLegObjKeyName() == null || comboData.getLegObjKeyName().length() == 0 ||
                    comboData.getLegTextureKeyName() == null || comboData.getLegTextureKeyName().length() == 0) {
                Log.e(LOGTAG, "******** Leg Obj not found ***********");
                BaseAccessoryItem item = dataSource.getAccessory(ConstantsUtil.EAccessoryType.eAccTypeLegs.toString(), comboData.getLegId());
                comboData.setLegObjKeyName(item.getObjAwsKey());
                comboData.setLegTextureKeyName(item.getTextureAwsKey());
            }
            Log.d(LOGTAG, "GL_INDEX_LEGS");
            if (!renderObj(GL_INDEX_LEGS, comboData.getLegTextureKeyName(), comboData.getLegObjKeyName(), false)) {
                iAssetReady[0] = false;
            }

        } else {
            iAssetReady[0] = false;
        }
        return iAssetReady[0];
    }

    public void changeObjects(final ComboData comboData) {
        if (comboData == null) {
            Log.e(LOGTAG, "changeObjects failed");
            return;
        }
        //listRenderedAccessory.clear();
        MixMatchSharedResource.getInstance().reset();
        setAcceptTouch(false);
        boolean isAssetReady = true;
        selectedTabIndex = TAB_INDEX_SHOP;
        Log.e(LOGTAG, "CHANGE OBJECTS ComboId: " + comboData.getCombo_ID());

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateToolbar(comboData);
                pbGLView.setLoadingText(getString(R.string.message_rendering_looks));
                pbGLView.setVisibility(View.VISIBLE);
                btnShop.setSelected(true);
                updateShopBtnImage();
                btnShop.setEnabled(false);

            }
        });

//            doneSignal =  new CountDownLatch(1);
//            try {
//                doneSignal.wait();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

        Log.e(LOGTAG, "GL_INDEX_HAIR_A8");
        if (!renderObj(GL_INDEX_HAIR_A8, faceItem.getHairPngKey(), faceItem.getHairObjkey(), true)) {
            if (!renderObj(GL_INDEX_HAIR_A8, comboData.getmA8_Png_Key_Name(), comboData.getmA8_Obj_Key_Name(), true)) {
                isAssetReady = false;
            } else {
                MixMatchSharedResource.getInstance().addAccessory(ConstantsUtil.EAccessoryType.eAccTypeHair.toString(), comboData.getmSKU_ID8());
            }
        } else {
            MixMatchSharedResource.getInstance().addAccessory(ConstantsUtil.EAccessoryType.eAccTypeHair.toString(), faceItem.getHairstyleId());
        }


        boolean isReady = renderLeg(comboData);
        if (!isReady) {
            isAssetReady = false;
        }
        //load SKUs
        Log.d(LOGTAG, "GL_INDEX_A1_BOTTOM");
        boolean isReadyA1 = renderObj(GL_INDEX_A1_BOTTOM, comboData.getmA1_Png_Key_Name(), comboData.getmA1_Obj_Key_Name(), false);
        if (isReadyA1 && isReady) {
            Log.d(LOGTAG, "GL_INDEX_SPECS_A9");
            if (!renderObj(GL_INDEX_SPECS_A9, comboData.getmA9_Png_Key_Name(), comboData.getmA9_Obj_Key_Name(), true)) {
                if (!renderObj(GL_INDEX_SPECS_A9, faceItem.getSpecsPngkey(), faceItem.getSpecsObjkey(), true)) {
                    resetObj(GL_INDEX_SPECS_A9);
                } else {
                    MixMatchSharedResource.getInstance().addAccessory(ConstantsUtil.EAccessoryType.eAccTypeSpecs.toString(), faceItem.getSpecsId());
                }
            } else {
                if (comboData.getmA9_Category().equals("Specs"))
                    MixMatchSharedResource.getInstance().addAccessory(ConstantsUtil.EAccessoryType.eAccTypeSpecs.toString(), comboData.getmSKU_ID9());
                else
                    MixMatchSharedResource.getInstance().addAccessory(ConstantsUtil.EAccessoryType.eAccTypeSunglasses.toString());
            }

            Log.d(LOGTAG, "GL_INDEX_A6_EARRINGS");
            if (!renderObj(GL_INDEX_A6_EARRINGS, comboData.getmA6_Png_Key_Name(), comboData.getmA6_Obj_Key_Name(), false)) {
                resetObj(GL_INDEX_A6_EARRINGS);
//                if (mixMatchFrag != null) {
//                    mixMatchFrag.setRemovedAccessory(ConstantsUtil.EAccessoryType.eAccTypeSpecs.toString(), comboData.getmSKU_ID9());
//                }

            } else {
                MixMatchSharedResource.getInstance().addAccessory(ConstantsUtil.EAccessoryType.eAccTypeEarrings.toString());
            }

            Log.d(LOGTAG, "GL_INDEX_A7_SHOES");
            if (!renderObj(GL_INDEX_A7_SHOES, comboData.getmA7_Png_Key_Name(), comboData.getmA7_Obj_Key_Name(), false)) {
                resetObj(GL_INDEX_A7_SHOES);
            } else {
                MixMatchSharedResource.getInstance().addAccessory(ConstantsUtil.EAccessoryType.eAccTypeShoes.toString());
            }

            //resetObj(GL_INDEX_A10_BAGS_CLUTCHES);
            Log.d(LOGTAG, "GL_INDEX_A10_BAGS_CLUTCHES");
            if (!renderObj(GL_INDEX_A10_BAGS_CLUTCHES, comboData.getmA10_Png_Key_Name(), comboData.getmA10_Obj_Key_Name(), false)) {
                resetObj(GL_INDEX_A10_BAGS_CLUTCHES);
            } else {
                MixMatchSharedResource.getInstance().addAccessory(ConstantsUtil.EAccessoryType.eAccTypeBags.toString());
            }
        } else {
            isAssetReady = isReadyA1;
        }

        //setLookat(lookAtObjn);
        if (isAssetReady) {
            //changeObjFinish();
            //mGLView.requestRender();
            zoomAnimation();
            setAcceptTouch(true);
        } else {
            if (prevComboData != null) {
                currentComboData = prevComboData;
                prevComboData = null;
                changeObjects(currentComboData);
            }
            else{
                setAcceptTouch(true);
            }
            Log.e(LOGTAG, "Could not load complete looks : isAssetReady failed");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ShopGlViewRenderer.this, "Failed to simulate complete Look.", Toast.LENGTH_SHORT).show();
                }
            });
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pbGLView.setVisibility(View.INVISIBLE);

            }
        });
    }

    RatingBarDialog ratingBarDialog;
    public void zoomCompleted() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnShop.setEnabled(true);
                if (InkarneAppContext.isDefaultFaceChanged()) {
                    ratingBarDialog =  new RatingBarDialog();
                    ratingBarDialog.showDialog();
                }

            }
        });

    }

    ShopGlViewRenderer(Context context, int objNo, String gender) {
        super(context, objNo, gender);
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

*/
