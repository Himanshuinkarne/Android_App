package com.svc.sml.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.svc.sml.Database.ComboData;
import com.svc.sml.R;
import com.svc.sml.Utility.AWSUtil;
import com.svc.sml.Utility.ConstantsUtil;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by himanshu on 3/2/16.
 */
public class SkuGallaryAdapter extends BaseAdapter implements View.OnClickListener {
    private final static String LOGTAG = CollectionAdapter.class.toString();
    protected final static float ALPHA_IMAGE_DOWNLOADING = 1.0f;
    protected final static float ALPHA_IMAGE_DOWNLOADED = 1.0f;
    protected ArrayList<TransferObserver> observers = new ArrayList<>();
    protected TransferUtility transferUtility;
    ArrayList<ComboData> comboList;
    private final Context context;
    LayoutInflater inflater;
    private OnGalleryAdapterListener listener;
    private HashMap<String,Bitmap> hashMapBitmaps = new HashMap<>();

    public interface OnGalleryAdapterListener {
        // TODO: Update argument type and name
        void onComboSelected(ComboData item);
    }


    public SkuGallaryAdapter(Context context, ArrayList<ComboData> comboList, OnGalleryAdapterListener listener) {
        this.comboList = comboList;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        transferUtility = AWSUtil.getTransferUtility(context);
        this.listener = listener;
    }

    public SkuGallaryAdapter(Context context, ArrayList<ComboData> comboList) {
        this.comboList = comboList;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        transferUtility = AWSUtil.getTransferUtility(context);
        this.listener = listener;
    }

    public boolean putBitmap(String key, Bitmap value) {
        if (hashMapBitmaps.size() >= ConstantsUtil.MAX_COUNT_LOOKS_BITMAPS && !hashMapBitmaps.containsKey(key)) {
            hashMapBitmaps.remove(hashMapBitmaps.keySet().toArray()[0]);
            hashMapBitmaps.put(key,value);
            return false;
        } else {
            hashMapBitmaps.put(key,value);
            return true;
        }
    }

    @Override
    public int getCount() {
        if (comboList != null) {
            Log.d(LOGTAG,"size : "+comboList.size());
            return comboList.size();
        }
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
        Holder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_combo_gallery, parent, false);
            holder = new Holder();
            holder.ivThumbnail = (ImageView) convertView.findViewById(R.id.iv_combo_gallery_item);
            holder.ivThumbnail.setOnClickListener(this);
            holder.ivThumbnail.setAlpha(ALPHA_IMAGE_DOWNLOADED);
            holder.pb = (ProgressBar) convertView.findViewById(R.id.pb_combo_gallery_item);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        ComboData item = comboList.get(position);
        Bitmap bitmap = null;
        if( hashMapBitmaps.get(item.getCombo_PIC_Png_Key_Name() ) != null){
            bitmap = hashMapBitmaps.get(item.getCombo_PIC_Png_Key_Name() );
        }
//        if (item.getThumbnailImage() != null) {
        if (bitmap != null) {
            holder.ivThumbnail.setImageBitmap(bitmap);
            holder.ivThumbnail.setTag(item);
            holder.ivThumbnail.setAlpha(ALPHA_IMAGE_DOWNLOADED);
            holder.pb.setVisibility(View.INVISIBLE);
        } else {
            holder.pb.setVisibility(View.VISIBLE);
            holder.ivThumbnail.setTag(item);
            HolderItem holderItem = new HolderItem();
            holderItem.pb = holder.pb;
            holderItem.ivThumbnail = holder.ivThumbnail;
            holderItem.comboData = item;
            beginDownload(item, holderItem);

        }
        return convertView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_combo_gallery_item :{
                listener.onComboSelected((ComboData) v.getTag());
            }
        }
    }

    private static class Holder {
        public TextView textView1;
        public ImageView ivThumbnail;
        public ProgressBar pb;

    }

    protected static class HolderItem {
        public ImageView ivThumbnail;
        public ProgressBar pb;
        public ComboData comboData;
    }

    public void stopDownload() {
        for (TransferObserver ob : observers) {
            if (transferUtility != null)
                transferUtility.cancel(ob.getId());
            ob.cleanTransferListener();
        }
        observers.clear();
    }

    private void beginDownload(ComboData item, HolderItem holderItemView) {
        String key = item.getCombo_PIC_Png_Key_Name();
        if (key == null || key.length() == 0) {
            Log.d(LOGTAG, "key is null or blank");
            return;
        }

        File file = new File(ConstantsUtil.FILE_PATH_APP_ROOT+ key);
        Log.d(LOGTAG, "To be downloaded. file:  " + key);
        if (ConstantsUtil.checkFileKeyExist(key)) {//TODO both file need to be checked
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            //item.setThumbnailImage(bm);//

            holderItemView.ivThumbnail.setImageBitmap(bm);
            holderItemView.ivThumbnail.setAlpha(ALPHA_IMAGE_DOWNLOADED);
            holderItemView.pb.setVisibility(View.INVISIBLE);
            return;
        }

        TransferObserver observer = transferUtility.download(ConstantsUtil.AWSBucketName, key, file);
        observers.add(observer);
        observer.setTransferListener(new DownloadListener(item, holderItemView, observer));
    }
    /*
     * A TransferListener class that can listen to a download task and be
     * notified when the status changes.
     */

    private class DownloadListener implements TransferListener {
        private WeakReference<ImageView> ivWeakRefThumbnail = null;
        public WeakReference<ProgressBar> pbWeakRef = null;
        private ComboData comboData;

        public DownloadListener(ComboData item, HolderItem holderItem, TransferObserver observer) {
            this.comboData = item;
            this.ivWeakRefThumbnail = new WeakReference<ImageView>(holderItem.ivThumbnail);
            this.pbWeakRef = new WeakReference<ProgressBar>(holderItem.pb);
        }

        @Override
        public void onError(int id, Exception e) {
            Log.e(LOGTAG, "Error during download thumbnail");
            if (pbWeakRef != null) {
                ProgressBar pb = pbWeakRef.get();
                if(pb != null)
                    pb.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

        }

        @Override
        public void onStateChanged(int id, TransferState state) {
            if (state == TransferState.FAILED) {
                Log.e(LOGTAG, "Thumbnail download failed");
                if (pbWeakRef != null) {
                    ProgressBar pb = pbWeakRef.get();
                    if(pb != null)
                        pb.setVisibility(View.INVISIBLE);
                }
            }

            if (state == TransferState.COMPLETED) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                //Bitmap bm = BitmapFactory.decodeFile(hairItem.getThumbnailDownloadFilePath(), options);
                File file = new File(ConstantsUtil.FILE_PATH_APP_ROOT + comboData.getCombo_PIC_Png_Key_Name());
                Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                if (bm != null) {
                    //comboData.setThumbnailImage(bm);
                    putBitmap(comboData.getCombo_PIC_Png_Key_Name(),bm);
                }
                else {
                    Log.d(LOGTAG," bitmap null");
                }

                if (ivWeakRefThumbnail != null) {
                    ImageView iv = ivWeakRefThumbnail.get();
                    if(iv != null) {
                        iv.setVisibility(View.VISIBLE);
                        iv.setAlpha(ALPHA_IMAGE_DOWNLOADED);
                        if (bm != null) {
                            iv.setImageBitmap(bm);
                        }
                    }
                    else {
                        Log.d(LOGTAG," notifyDataSetChanged 1");
                        notifyDataSetChanged();
                    }
                }
                else{
                    Log.d(LOGTAG," notifyDataSetChanged 2");
                    notifyDataSetChanged();
                }
                if (pbWeakRef != null) {
                    ProgressBar pb = pbWeakRef.get();
                    if(pb != null)
                        pb.setVisibility(View.INVISIBLE);
                }
            }
        }
    }
}
