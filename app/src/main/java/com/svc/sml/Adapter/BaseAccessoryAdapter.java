package com.svc.sml.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.svc.sml.Activity.OnAccessoryAdapterListener;
import com.svc.sml.Database.InkarneDataSource;
import com.svc.sml.Database.User;
import com.svc.sml.Helper.DataManager;
import com.svc.sml.Helper.ImageFetcher;
import com.svc.sml.Model.BaseAccessoryItem;
import com.svc.sml.Model.FaceItem;
import com.svc.sml.Model.TransferProgressModel;
import com.svc.sml.R;
import com.svc.sml.Utility.CircularProgressWheel;
import com.svc.sml.Utility.ConstantsUtil;
import com.svc.sml.Utility.Unzip;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by himanshu on 1/25/16.
 */
public abstract class BaseAccessoryAdapter extends BaseAdapter {
    private final static String LOGTAG = BaseAccessoryAdapter.class.toString();
    protected final static float ALPHA_IMAGE_DOWNLOADING = 1.0f;
    protected final static float ALPHA_IMAGE_DOWNLOADED = 1.0f;
    protected LayoutInflater mInflater;
    protected Context context;
    protected ArrayList<TransferObserver> observers = new ArrayList<>();
    protected ArrayList<BaseAccessoryItem> arrayItem;
    protected TransferUtility transferUtility;
    protected OnAccessoryAdapterListener accessoryAdapterListener;
    protected FaceItem faceItem;
    protected InkarneDataSource dataSource;
    protected BaseAccessoryItem latestClickedItem;
    protected BaseAccessoryItem prevClickedItem;
    protected ImageFetcher imageFetcher;

    protected abstract void updateViewSelection(BaseAccessoryItem item);

    public FaceItem getFaceItem() {
        return faceItem;
    }

    public void setFaceItem(FaceItem faceItem) {
        this.faceItem = faceItem;
    }

    public BaseAccessoryAdapter(Context context, int resource) {

    }

    public BaseAccessoryAdapter() {
        imageFetcher = new ImageFetcher();
    }

    public BaseAccessoryAdapter(Context context) {
        imageFetcher = new ImageFetcher(context);
    }


    /**
     * View holder for the views we need access to
     */
    protected static class Holder {
        public TextView textView1;
        public ProgressBar pb1;
    }

    protected static class HolderItem {
        public TextView textView;
        public ImageView ivThumbnail;
        public ProgressBar pb;
        public CircularProgressWheel cpb;
        public View vIsDownloaded;
    }

    protected static class DownloadHolder {
        public BaseAccessoryItem item;
        public ImageView ivThumbnail;
        public CircularProgressWheel cpb;
        public ProgressBar pb;
        public View vIsDownloaded;
    }


    public void accessoryLoadFailed(BaseAccessoryItem item) {
        if (prevClickedItem != null && latestClickedItem != null) {
            latestClickedItem.setIsSelected(false);
            latestClickedItem = prevClickedItem;
            latestClickedItem.setIsSelected(true);
            BaseAccessoryItem removedItem = null;
            for (BaseAccessoryItem item1 : arrayItem) {
                if (item1.getObjId().equals(item.getObjId())) {//TODO comapare faceid and pbid
                    removedItem = item1;
                }
            }
            arrayItem.remove(removedItem);
            clickHandlerOnDownloadedItem(latestClickedItem);
        } else {
            BaseAccessoryItem removedItem = null;
            for (BaseAccessoryItem item1 : arrayItem) {
                item1.setIsSelected(false);
                if (item1.getObjId().equals(item.getObjId())) {//TODO comapare faceid and pbid
                    removedItem = item1;
                }
            }
            arrayItem.remove(removedItem);
        }
        dataSource.delete(item);
        notifyDataSetChanged();
        //updateViewSelection(item);
    }

    public void accessoryLoadSuccessful(BaseAccessoryItem item) {
        prevClickedItem = item;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }


    protected void showData(final BaseAccessoryItem item, final HolderItem holderItem) {
        if (item == null)
            return;
        holderItem.cpb.setVisibility(View.INVISIBLE);
        imageFetcher.manageSetImage(item.getObjId(), item.getThumbnailAwsKey(), holderItem.ivThumbnail, holderItem.pb,0);
        //holderItem.textView.setText(item.getObjId());

        if (item.isSelected) {
            holderItem.ivThumbnail.setBackgroundResource(R.drawable.rectangle_outline_tcolor_rounded);
        } else {
            holderItem.ivThumbnail.setBackgroundResource(R.drawable.selector_sku_mixmatch);
        }

        if (item.countTobeDownloaded == 0) {
            //holderItem.vIsDownloaded.setBackgroundColor(context.getResources().getColor(R.color.color_is_downloaded));
            holderItem.vIsDownloaded.setBackgroundColor(ContextCompat.getColor(context, R.color.color_is_downloaded));
        } else {
            if (isDownloaded(item)) {
                item.countTobeDownloaded = 0;
                //holderItem.vIsDownloaded.setBackgroundColor(context.getResources().getColor(R.color.color_is_downloaded));
                holderItem.vIsDownloaded.setBackgroundColor(ContextCompat.getColor(context, R.color.color_is_downloaded));
            } else {
                holderItem.vIsDownloaded.setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }

    protected boolean isDownloaded(BaseAccessoryItem item) {
        if (item.getAccessoryType().equals(ConstantsUtil.EAccessoryType.eAccTypeShoes.toString())) {
            return item.getObjDStatus() == 2 && item.getTextureDStatus() == 2 && ConstantsUtil.checkFileKeysExist(item.getObjAwsKey(), item.getTextureAwsKey())
                    && item.dependentItem != null
                    && item.dependentItem.getObjDStatus() == 2 && item.dependentItem.getTextureDStatus() == 2 && ConstantsUtil.checkFileKeysExist(item.dependentItem.getObjAwsKey(), item.dependentItem.getTextureAwsKey());
        } else {
            return item.getObjDStatus() == 2 && item.getTextureDStatus() == 2 && ConstantsUtil.checkFileKeysExist(item.getObjAwsKey(), item.getTextureAwsKey());
        }
    }


    //@Override
    protected void clickHandlerOnDownloadedItem(BaseAccessoryItem item) {
        if (accessoryAdapterListener != null) {
            accessoryAdapterListener.onAccessorySelected(item);
        }
    }

    //@Override
    protected void reloadListView() {

    }

    //@Override
    protected String getCreateAccessoryURL(BaseAccessoryItem item) {
        return null;
    }


    //@Override
    protected void saveData(BaseAccessoryItem item) {
        String accessoryType = item.getAccessoryType();
        if (ConstantsUtil.isAccessoryFaceRelated(accessoryType)) {
            item.setFaceID(faceItem.getFaceId());
        } else if (ConstantsUtil.isAccessoryBodyRelated(accessoryType)) {
            item.setPbID(faceItem.getPbId());
        } else if (ConstantsUtil.isAccessoryBodyFaceRelated(accessoryType)) {
            item.setPbID(faceItem.getPbId());
            item.setFaceID(faceItem.getFaceId());
        }
        if (accessoryType.equals(ConstantsUtil.EAccessoryType.eAccTypeShoes.toString())) {

        }
        DataManager.getInstance().getDataSource().create(item);
    }


    private void getOpenGLkey(final DownloadHolder dHolder) {
        final BaseAccessoryItem item = dHolder.item;
        //TODO remove && !item.getObjAwsKey().equals("null")
        if (item.getObjAwsKey() != null && item.getObjAwsKey().length() != 0 &&
                item.getTextureAwsKey() != null && item.getTextureAwsKey().length() != 0 && !item.getObjAwsKey().equals("null")) {
            initGraphicsDownloads(dHolder);
        } else {
            String uri = getCreateAccessoryURL(dHolder.item);
            dHolder.pb.setVisibility(View.VISIBLE);
            DataManager.getInstance().requestCreateAccessory(uri, item, false, new DataManager.OnResponseHandlerInterface() {
                @Override
                public void onResponse(Object obj) {
                    if (dHolder != null && obj != null) {
                        dHolder.item = (BaseAccessoryItem) obj;
                        if (dHolder.pb != null)
                            dHolder.pb.setVisibility(View.INVISIBLE);
                        saveData(item);
                        initGraphicsDownloads(dHolder);
                    }
                }

                @Override
                public void onResponseError(String errorMessage, int errorCode) {
                    item.isDownloadFailed = true;
                }
            });
        }
    }

    private void getOpenGLkeyForShoes(final DownloadHolder dHolder) {
        final BaseAccessoryItem item = dHolder.item;
        //TODO remove && !item.getObjAwsKey().equals("null")
        if (item.getObjAwsKey() != null && item.getObjAwsKey().length() != 0 &&
                item.getTextureAwsKey() != null && item.getTextureAwsKey().length() != 0 && !item.getObjAwsKey().equals("null")) {
            getOpenGLKeyForDependentItem(dHolder);
        } else {
            String uri = getCreateAccessoryURL(dHolder.item);
            dHolder.pb.setVisibility(View.VISIBLE);
            DataManager.getInstance().requestCreateAccessory(uri, item, false, new DataManager.OnResponseHandlerInterface() {
                @Override
                public void onResponse(Object obj) {
                    if (dHolder != null && obj != null) {
                        dHolder.item = (BaseAccessoryItem) obj;
                        if (dHolder.pb != null)
                            dHolder.pb.setVisibility(View.INVISIBLE);
                        saveData(item);
                        getOpenGLKeyForDependentItem(dHolder);
                    }
                }

                @Override
                public void onResponseError(String errorMessage, int errorCode) {
                    item.isDownloadFailed = true;
                }
            });
        }
    }

    protected void getOpenGLKeyForDependentItem(final DownloadHolder dHolder) {
        final BaseAccessoryItem item = dHolder.item;
        if (item.getObjId2().equals("NA")) {
            Log.w(LOGTAG, "legId NA");
            initGraphicsDownloads(dHolder);
            return;
        }
        BaseAccessoryItem legItem = dataSource.getAccessory(ConstantsUtil.EAccessoryType.eAccTypeLegs.toString(), item.getObjId2());
        //TODO remove && !item.getObjAwsKey().equals("null")
        if (legItem != null && legItem.getObjAwsKey() != null && legItem.getObjAwsKey().length() != 0 &&
                legItem.getTextureAwsKey() != null && legItem.getTextureAwsKey().length() != 0 && !legItem.getObjAwsKey().equals("null")) {
            dHolder.item.dependentItem = legItem;
            initGraphicsDownloads(dHolder);
        } else {
            dHolder.pb.setVisibility(View.VISIBLE);
            String uri = ConstantsUtil.URL_BASEPATH_CREATE_V2 + ConstantsUtil.URL_METHOD_CREATE_LEGS
                    + User.getInstance().getmUserId() + "/"
                    + User.getInstance().getmGender() + "/"
                    + User.getInstance().getDefaultFaceItem().getFaceId() + "/"
                    + User.getInstance().getPBId();
            DataManager.getInstance().requestCreateLegs(uri, new DataManager.OnResponseHandlerInterface() {
                @Override
                public void onResponse(Object obj) {
                    if (dHolder.pb != null)
                        dHolder.pb.setVisibility(View.INVISIBLE);
                    ArrayList<BaseAccessoryItem> list = (ArrayList<BaseAccessoryItem>) obj;
                    for (BaseAccessoryItem itemLeg : list) {
                        if (itemLeg.getObjId().equals(item.dependentItem.getObjId())) {
                            item.dependentItem = itemLeg;
                        }
                    }
                    if (dHolder != null) {
                        saveData(item);
                        initGraphicsDownloads(dHolder);
                    }
                }

                @Override
                public void onResponseError(String errorMessage, int errorCode) {
                    item.isDownloadFailed = true;
                }
            });
        }
    }


    protected void getOpenGLData(final DownloadHolder dHolder, final ImageView v) {
        if (dHolder.cpb != null)
            dHolder.cpb.setVisibility(View.VISIBLE);
        dHolder.ivThumbnail = v;
        if (dHolder.item.getAccessoryType().equals(ConstantsUtil.EAccessoryType.eAccTypeShoes.toString())) {
            getOpenGLkeyForShoes(dHolder);
        } else {
            getOpenGLkey(dHolder);
        }
    }

    private void initGraphicsDownloads(final DownloadHolder dHolder) {
        final BaseAccessoryItem item = dHolder.item;
        if (item.getAccessoryType().equals(ConstantsUtil.EAccessoryType.eAccTypeShoes.toString()) && item.dependentItem != null && item.dependentItem.getObjId() != null && !item.dependentItem.getObjId().equals("NA") && item.dependentItem.getObjAwsKey() != null && !item.dependentItem.getObjAwsKey().isEmpty()) {
            if (item.countTobeDownloaded == -1) {
                dHolder.item.countTobeDownloaded = 4;
                beginGraphicsDownload(dHolder, dHolder.item.getObjAwsKey(), ConstantsUtil.EDownloadType.eDownloadTypeObj);
                beginGraphicsDownload(dHolder, dHolder.item.getTextureAwsKey(), ConstantsUtil.EDownloadType.eDownloadTypeTexture);
                beginGraphicsDownload(dHolder, dHolder.item.dependentItem.getObjAwsKey(), ConstantsUtil.EDownloadType.eDownloadTypeObj2);
                beginGraphicsDownload(dHolder, dHolder.item.dependentItem.getTextureAwsKey(), ConstantsUtil.EDownloadType.eDownloadTypeTexture2);
            }
        } else {
            if (item.countTobeDownloaded == -1) {
                dHolder.item.countTobeDownloaded = 2;
                beginGraphicsDownload(dHolder, dHolder.item.getObjAwsKey(), ConstantsUtil.EDownloadType.eDownloadTypeObj);
                beginGraphicsDownload(dHolder, dHolder.item.getTextureAwsKey(), ConstantsUtil.EDownloadType.eDownloadTypeTexture);
            }
        }
    }

    public void stopDownload() {
        for (TransferObserver ob : observers) {
            if (transferUtility != null)
                transferUtility.cancel(ob.getId());
            ob.cleanTransferListener();
        }
        observers.clear();
    }

    private void hideCircularProgressBar(WeakReference<CircularProgressWheel> cpbWeakRef) {
        if (cpbWeakRef != null) {
            CircularProgressWheel cpb = cpbWeakRef.get();
            if (cpb != null) {
                Log.d(LOGTAG, "Tag complete " + cpb.getTag());
                cpb.setVisibility(View.INVISIBLE);
            }
        }
    }

    private boolean beginGraphicsDownloadCheck(BaseAccessoryItem item, String key, ConstantsUtil.EDownloadType downloadType) {
        if (item.getAccessoryType().equals(ConstantsUtil.EAccessoryType.eAccTypeShoes.toString())) {
            return ((downloadType == ConstantsUtil.EDownloadType.eDownloadTypeObj && item.getObjDStatus() == ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus())
                    || (downloadType == ConstantsUtil.EDownloadType.eDownloadTypeTexture && item.getTextureDStatus() == ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus())
                    || (downloadType == ConstantsUtil.EDownloadType.eDownloadTypeObj2 && item.dependentItem.getObjDStatus() == ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus())
                    || (downloadType == ConstantsUtil.EDownloadType.eDownloadTypeTexture2 && item.dependentItem.getTextureDStatus() == ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus()))
                    && ConstantsUtil.checkFileKeyExist(key);

        } else {
            return ((downloadType == ConstantsUtil.EDownloadType.eDownloadTypeObj && item.getObjDStatus() == ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus())
                    || (downloadType == ConstantsUtil.EDownloadType.eDownloadTypeTexture && item.getTextureDStatus() == ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus()))
                    && ConstantsUtil.checkFileKeyExist(key);
        }
    }


    //png and obj download
    private void beginGraphicsDownload(final DownloadHolder dHolder, String key, ConstantsUtil.EDownloadType downloadType) {
        if (key == null || key.length() == 0) {
            Log.d(LOGTAG, "key is null or blank");
            dHolder.item.isDownloadFailed = true;
            return;
        }

        Log.d(LOGTAG, "To be downloaded. file:  " + key);
        if (beginGraphicsDownloadCheck(dHolder.item, key, downloadType)) {
            Log.e(LOGTAG, "clickHandlerOnDownloadedItem :" + latestClickedItem.getObjId());
            dHolder.item.countTobeDownloaded--;
            if (latestClickedItem != null && latestClickedItem.getObjId().equals(dHolder.item.getObjId()) && dHolder.item.countTobeDownloaded == 0) {
                Log.e(LOGTAG, "********** clickHandlerOnDownloadedItem 2 ***********:" + latestClickedItem.getObjId());
                //dHolder.item.setIsGraphicsObjsDownloded(true);
                dHolder.ivThumbnail.setAlpha((float) 1.0);
                dHolder.cpb.setVisibility(View.INVISIBLE);
                dHolder.vIsDownloaded.setBackgroundColor(ContextCompat.getColor(context, R.color.color_is_downloaded));
                //dHolder.vIsDownloaded.setBackgroundColor(context.getResources().getColor(R.color.color_is_downloaded));
                updateViewSelection(dHolder.item);
                clickHandlerOnDownloadedItem(latestClickedItem);
            }
            return;
        }

        if(key.contains(".ply") ){ //TODO zip remove hairstyle//& !key.contains("hairstyles/")
            key = key.replace(".ply",".gz");
        }

        File file = new File(ConstantsUtil.FILE_PATH_APP_ROOT + key);
        TransferProgressModel tm = new TransferProgressModel(key);
        if (downloadType == ConstantsUtil.EDownloadType.eDownloadTypeObj) {
            dHolder.item.setTransferModelObj(tm);
        } else if (downloadType == ConstantsUtil.EDownloadType.eDownloadTypeTexture) {
            dHolder.item.setTransferModelTexture(tm);
        } else if (downloadType == ConstantsUtil.EDownloadType.eDownloadTypeObj2) {
            dHolder.item.dependentItem.setTransferModelObj(tm);
        } else if (downloadType == ConstantsUtil.EDownloadType.eDownloadTypeTexture2) {
            dHolder.item.dependentItem.setTransferModelTexture(tm);
        }

        TransferObserver observer = transferUtility.download(ConstantsUtil.AWSBucketName, key, file);
        observer.setTransferListener(new DownloadListenerRenderingObj(dHolder, key, downloadType));
    }


    private class DownloadListenerRenderingObj implements TransferListener {
        private WeakReference<ImageView> ivWeakRefThumbnail = null;
        public WeakReference<CircularProgressWheel> cpbWeakref = null;
        public WeakReference<View> vIsDownloadWeakRef = null;
        public ConstantsUtil.EDownloadType eDType;
        private BaseAccessoryItem item;
        private String key;

        public DownloadListenerRenderingObj(final DownloadHolder dHolder, String key, ConstantsUtil.EDownloadType downloadType) {
            this.item = dHolder.item;
            this.ivWeakRefThumbnail = new WeakReference<ImageView>(dHolder.ivThumbnail);
            this.cpbWeakref = new WeakReference<CircularProgressWheel>(dHolder.cpb);
            this.vIsDownloadWeakRef = new WeakReference<View>(dHolder.vIsDownloaded);
            this.key = key;
            this.eDType = downloadType;
        }

        @Override
        public void onError(int id, Exception e) {
            Log.e(LOGTAG, "Error during download thumbnail");
            hideCircularProgressBar(cpbWeakref);
            item.countTobeDownloaded = -1;
            item.isDownloadFailed = true;
        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            int p = (int) (bytesCurrent * 360 / bytesTotal);
            if (item.getTransferModelObj() != null && eDType == ConstantsUtil.EDownloadType.eDownloadTypeObj) {
                item.getTransferModelObj().progress = p;
            } else if (item.getTransferModelTexture() != null && eDType == ConstantsUtil.EDownloadType.eDownloadTypeTexture) {
                item.getTransferModelTexture().progress = p;
            }
            if (item.dependentItem != null) {
                if (item.dependentItem.getTransferModelObj() != null && eDType == ConstantsUtil.EDownloadType.eDownloadTypeObj2) {
                    item.dependentItem.getTransferModelObj().progress = p;
                } else if (item.dependentItem.getTransferModelTexture() != null && eDType == ConstantsUtil.EDownloadType.eDownloadTypeTexture2) {
                    item.dependentItem.getTransferModelTexture().progress = p;
                }
            }

            if (cpbWeakref != null) {
                CircularProgressWheel cProgressbar = cpbWeakref.get();
                if (cProgressbar != null && item != null) {
                    int cumulativeProgress = 0;

                    if (item.getTransferModelObj() != null && item.getTransferModelTexture() != null)
                        cumulativeProgress = item.getTransferModelObj().progress + item.getTransferModelTexture().progress;
                    if (item.dependentItem != null && item.dependentItem.getTransferModelObj() != null && item.dependentItem.getTransferModelTexture() != null) {
                        cumulativeProgress += item.dependentItem.getTransferModelObj().progress + item.dependentItem.getTransferModelTexture().progress;
                        cumulativeProgress /= (4);
                    } else {
                        cumulativeProgress /= (2);
                    }
                    cProgressbar.setProgress(cumulativeProgress);
                }
            }
        }

        @Override
        public void onStateChanged(int id, TransferState state) {
            if (item == null) {
                hideCircularProgressBar(cpbWeakref);
                notifyDataSetChanged();
                return;
            }
            if (item.getTransferModelObj() != null && eDType == ConstantsUtil.EDownloadType.eDownloadTypeObj) {
                item.getTransferModelObj().transferState = state;
            } else if (item.getTransferModelTexture() != null && eDType == ConstantsUtil.EDownloadType.eDownloadTypeTexture) {
                item.getTransferModelTexture().transferState = state;
            }

            if (item.dependentItem != null) {
                if (item.dependentItem.getTransferModelObj() != null && eDType == ConstantsUtil.EDownloadType.eDownloadTypeObj2) {
                    item.dependentItem.getTransferModelObj().transferState = state;
                } else if (item.dependentItem.getTransferModelTexture() != null && eDType == ConstantsUtil.EDownloadType.eDownloadTypeTexture2) {
                    item.dependentItem.getTransferModelTexture().transferState = state;
                }
            }
            if (state == TransferState.FAILED) {
                Log.e(LOGTAG, " download failed");
                hideCircularProgressBar(cpbWeakref);
                item.countTobeDownloaded = -1;
                item.isDownloadFailed = true;
            }
            if (state == TransferState.COMPLETED) {

                /* new change*/
                Log.e(LOGTAG, " downloaded key :" + key);
                String unZipFile = "";
                if(key.contains(".gz")) {
                    Log.e(LOGTAG,"zip :"+key);
                    unZipFile = Unzip.getUnzipPlyFileName(ConstantsUtil.FILE_PATH_APP_ROOT + key);
                    Log.e(LOGTAG, "unzip File :" + unZipFile);
                    unZipFile = key.replace(".gz",".ply");
                }
                int downloadStatus = ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus();
                if(eDType == ConstantsUtil.EDownloadType.eDownloadTypeObj){
                        item.setObjDStatus(downloadStatus);
                    if(unZipFile != null && !unZipFile.isEmpty())
                        item.setObjAwsKey(unZipFile);
                        dataSource.create(item);
                }else if(eDType == ConstantsUtil.EDownloadType.eDownloadTypeTexture){
                    item.setTextureDStatus(downloadStatus);
                    dataSource.create(item);

                }else if(eDType == ConstantsUtil.EDownloadType.eDownloadTypeObj2){
                    if (item.dependentItem != null ){
                        item.dependentItem.setObjDStatus(downloadStatus);
                        if(unZipFile != null && !unZipFile.isEmpty())
                            item.dependentItem.setObjAwsKey(unZipFile);
                        dataSource.create(item.dependentItem);
                    }

                } else if(eDType == ConstantsUtil.EDownloadType.eDownloadTypeTexture2){
                    if (item.dependentItem != null ){
                        item.dependentItem.setTextureDStatus(downloadStatus);
                        dataSource.create(item.dependentItem);
                    }
                }
                 /* new change end*/

                /*
                if (item.getTransferModelObj() != null && item.getTransferModelObj().transferState == TransferState.COMPLETED) {
                    item.setObjDStatus(ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus());
                    dataSource.create(item);
                }
                if (item.getTransferModelTexture() != null && item.getTransferModelTexture().transferState == TransferState.COMPLETED) {
                    item.setTextureDStatus(ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus());
                    dataSource.create(item);
                }

                if (item.dependentItem != null) {
                    if (item.dependentItem.getTransferModelObj() != null && item.dependentItem.getTransferModelObj().transferState == TransferState.COMPLETED) {
                        item.dependentItem.setObjDStatus(ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus());
                    }
                    if (item.dependentItem.getTransferModelTexture() != null && item.dependentItem.getTransferModelTexture().transferState == TransferState.COMPLETED) {
                        item.dependentItem.setTextureDStatus(ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus());
                    }
                    dataSource.create(item.dependentItem);
                }
                */

                //updateViewSelection(this.item);
                this.item.countTobeDownloaded--;
                if (latestClickedItem != null && latestClickedItem.getObjId().equals(this.item.getObjId()) && item.countTobeDownloaded == 0) {
                    Log.e(LOGTAG, "clickHandlerOnDownloadedItem :" + latestClickedItem.getObjId());
                    hideCircularProgressBar(cpbWeakref);
                    if (this.vIsDownloadWeakRef != null) {
                        View v = vIsDownloadWeakRef.get();
                        if (v != null) {
                            //v.setBackgroundColor(context.getResources().getColor(R.color.color_is_downloaded));
                            v.setBackgroundColor(ContextCompat.getColor(context, R.color.color_is_downloaded));
                        }
                    }

                    if (ivWeakRefThumbnail != null) {
                        ImageView iv = ivWeakRefThumbnail.get();
                        if (iv != null)
                            iv.setAlpha((float) 1.0);
                    }
                    //item.setIsGraphicsObjsDownloded(true);
                    dataSource.create(item);
                    if (item.dependentItem != null)
                        dataSource.create(item.dependentItem);
                    updateViewSelection(this.item);
                    clickHandlerOnDownloadedItem(latestClickedItem);
                }
            }
        }
    }


    public class BitmapWorkerTask extends AsyncTask<Void, Void, Bitmap> {
        private WeakReference<ImageView> ivWeaKRef;
        private WeakReference<ProgressBar> pbWeakRef;
        private BaseAccessoryItem item;

        public BitmapWorkerTask(WeakReference<ImageView> ivWeaKRef, BaseAccessoryItem item, WeakReference<ProgressBar> pbWeakRef) {
            this.ivWeaKRef = ivWeaKRef;
            this.item = item;
            this.pbWeakRef = pbWeakRef;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            File file = new File(ConstantsUtil.FILE_PATH_APP_ROOT + item.getThumbnailAwsKey());
            Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            if (bm != null) {
                //putBitmap(item.getThumbnailAwsKey(), bm);
                item.isPicDownloading = false;
            }
            return bm;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (pbWeakRef != null) {
                ProgressBar pb = pbWeakRef.get();
                if (pb != null)
                    pb.setVisibility(View.INVISIBLE);
            }
            if (ivWeaKRef != null) {
                ImageView iv = ivWeaKRef.get();
                if (iv != null) {
                    iv.setVisibility(View.VISIBLE);
                    iv.setAlpha(ALPHA_IMAGE_DOWNLOADED);
                    iv.setImageBitmap(bitmap);
                    Log.e(LOGTAG, "onPostExecute : " + item.getThumbnailAwsKey());
                } else {
                    Log.e(LOGTAG, " notifyDataSetChanged 1 :" + item.getThumbnailAwsKey());
                    //notifyDataSetChanged();
                }
            } else {
                Log.e(LOGTAG, " notifyDataSetChanged 2 :" + item.getThumbnailAwsKey());
                //notifyDataSetChanged();
            }
        }
    }
}
