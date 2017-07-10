package com.svc.sml.Utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.svc.sml.Database.ComboData;
import com.svc.sml.Helper.ImageFetcher;
import com.svc.sml.R;

import java.io.File;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is an adapter that provides base, abstract class for images
 * adapter.
 */
public abstract class AbstractCoverFlowImageAdapter extends BaseAdapter {

    private final static String LOGTAG = AbstractCoverFlowImageAdapter.class.getSimpleName();
    protected final static float ALPHA_IMAGE_DOWNLOADING = 0.2f;
    protected final static float ALPHA_IMAGE_DOWNLOADED = 1.0f;
    public ArrayList<TransferObserver> observers = new ArrayList<>();
    public TransferUtility transferUtility;
    public ArrayList<ComboData> comboList;
    public String looksCategoryTitle;
    public LayoutInflater inflater;
    public Context context;
    public ImageFetcher imageFetcher;
    private HashMap<String, SoftReference<Bitmap>> hashMapBitmaps = new HashMap<>();
    //private HashMap<String,ArrayList<WeakReference<ImageView>> > hashMapImageViewWReference = new HashMap<>();

    /**
     * The width.
     */
    private float width = 0;

    /**
     * The height.
     */
    private float height = 0;

    public ArrayList<ComboData> getComboList() {
        return comboList;
    }

    public void setComboList(ArrayList<ComboData> comboList) {
        this.comboList = comboList;
    }

    /**
     * The bitmap map.
     */
    private final Map<Integer, WeakReference<Bitmap>> bitmapMap = new HashMap<Integer, WeakReference<Bitmap>>();

    public AbstractCoverFlowImageAdapter() {
        super();
    }

    public AbstractCoverFlowImageAdapter(final Context context, String looksCategoryTitle, ArrayList<ComboData> comboList) {
        super();
        this.comboList = comboList;
        this.looksCategoryTitle = looksCategoryTitle;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        transferUtility = AWSUtil.getTransferUtility(context);
        imageFetcher = new ImageFetcher(context);
        //this.listener = listener;
    }

    public boolean putBitmap(String key, Bitmap value) {
        if (hashMapBitmaps.size() >= ConstantsUtil.MAX_COUNT_LOOKS_BITMAPS && !hashMapBitmaps.containsKey(key)) {
            hashMapBitmaps.remove(hashMapBitmaps.keySet().toArray()[0]);
            hashMapBitmaps.put(key, new SoftReference<Bitmap>(value));
            return false;
        } else {
            hashMapBitmaps.put(key, new SoftReference<Bitmap>(value));
            return true;
        }
    }

    public Bitmap getBitmap(String key) {
        Bitmap bitmap = null;
        SoftReference<Bitmap> wBm = null;
        if (hashMapBitmaps.get(key) != null) {
            wBm = hashMapBitmaps.get(key);
            if (wBm != null)
                bitmap = wBm.get();
        }
        return bitmap;
    }

    /**
     * Set width for all pictures.
     *
     * @param width picture height
     */
    public synchronized void setWidth(final float width) {
        this.width = width;
    }

    /**
     * Set height for all pictures.
     *
     * @param height picture height
     */
    public synchronized void setHeight(final float height) {
        this.height = height;
    }

    @Override
    public final Bitmap getItem(final int position) {
        final WeakReference<Bitmap> weakBitmapReference = bitmapMap.get(position);
        if (weakBitmapReference != null) {
            final Bitmap bitmap = weakBitmapReference.get();
            if (bitmap == null) {
                Log.v(LOGTAG, "getItem :Empty bitmap reference at position: " + position + ":" + this);
            } else {
                Log.v(LOGTAG, "getItem :Reusing bitmap item at position: " + position + ":" + this);
                return bitmap;
            }
        }
        Log.v(LOGTAG, "getItem :Creating item at position: " + position + ":" + this);
        final Bitmap bitmap = createBitmap(position);
        bitmapMap.put(position, new WeakReference<Bitmap>(bitmap));
        Log.v(LOGTAG, "getItem :Created item at position: " + position + ":" + this);
        return bitmap;
    }

    /**
     * Creates new bitmap for the position specified.
     *
     * @param position position
     * @return Bitmap created
     */
    protected abstract Bitmap createBitmap(int position);

    protected abstract Bitmap createBitmap(Bitmap bitmap);

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public final synchronized long getItemId(final int position) {
        return position;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getView(int, android.view.View,
     * android.view.ViewGroup)
     */
    //@Override
    public final synchronized ImageView getView2(final int position, final View convertView, final ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            final Context context = parent.getContext();
            Log.v(LOGTAG, "Creating Image view at position: " + position + ":" + this);
            imageView = new ImageView(context);
            imageView.setLayoutParams(new Gallery.LayoutParams((int) width, (int) height));
        } else {
            Log.v(LOGTAG, "Reusing view at position: " + position + ":" + this);
            imageView = (ImageView) convertView;
        }

        imageView.setImageBitmap(getItem(position));
        return imageView;
    }

    //@Override
    public final synchronized View getView1(final int position, final View convertView, final ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            final Context context = parent.getContext();
            Log.v(LOGTAG, "Creating Image view at position: " + position + ":" + this);
            imageView = new ImageView(context);
            imageView.setLayoutParams(new Gallery.LayoutParams((int) width, (int) height));
        } else {
            Log.v(LOGTAG, "Reusing view at position: " + position + ":" + this);
            imageView = (ImageView) convertView;
        }
        imageView.setImageBitmap(getItem(position));
        return imageView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            if (inflater == null)
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_combo_gallery_foryou, parent, false);
            holder = new Holder();
            holder.ivThumbnail = (ImageView) convertView.findViewById(R.id.iv_combo_gallery_item);
            holder.textView1 = (TextView) convertView.findViewById(R.id.tv_temp_comboId);
            holder.vIsDownloaded = convertView.findViewById(R.id.v_is_downloaded);
            //holder.ivThumbnail.setOnClickListener(this);
            holder.pb = (ProgressBar) convertView.findViewById(R.id.pb_combo_gallery_item);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        ComboData item = comboList.get(position);
        //holder.textView1.setText(item.getCombo_ID());//todo
        Bitmap bitmap = getBitmap(item.getCombo_PIC_Png_Key_Name());
        holder.ivThumbnail.setImageBitmap(null);
        //if (item.getThumbnailImage() != null) {
        if (bitmap != null) {
            //holder.ivThumbnail.setImageBitmap(item.getThumbnailImage());
            holder.ivThumbnail.setImageBitmap(bitmap);
            holder.ivThumbnail.setAlpha(ALPHA_IMAGE_DOWNLOADED);
            holder.ivThumbnail.setTag(item);
            holder.pb.setVisibility(View.INVISIBLE);
        } else {
            holder.pb.setVisibility(View.VISIBLE);
            holder.ivThumbnail.setImageBitmap(null);
//            holder.ivThumbnail.setTag(item);
//            HolderItem holderItem = new HolderItem();
//            holderItem.pb = holder.pb;
//            holderItem.ivThumbnail = holder.ivThumbnail;
//            holderItem.comboData = item;
            //beginDownload(item, holderItem);
            imageFetcher.manageSetImage(item.getPbId(),item.getCombo_PIC_Png_Key_Name(),holder.ivThumbnail,holder.pb);
        }

        if(isDownloaded(item)){
            item.setIsDownloadedTempStatus(true);
            holder.vIsDownloaded.setVisibility(View.VISIBLE);
        }
        else {
            holder.vIsDownloaded.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

   private boolean isDownloaded(ComboData item){ //TODO extra check to be removed
       int d = ConstantsUtil.EDownloadStatusType.eDownloaded.intStatus();
       return item.isDownloadedTempStatus()
               || (item.getIsDisplayReady() == 1
               && ConstantsUtil.checkFileKeysExist(item.getmA1_Png_Key_Name(), item.getmA1_Obj_Key_Name())
       );
   }


    private static class Holder {
        public TextView textView1;
        public ImageView ivThumbnail;
        public ProgressBar pb;
        public View vIsDownloaded;
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
//        if(item.isPicDownloading)
//            return;
        //item.isPicDownloading = true;
        //this.ivWeakRefThumbnail = new WeakReference<ImageView>(holderItem.ivThumbnail);

        String key = item.getCombo_PIC_Png_Key_Name();
        if (key == null || key.length() == 0) {
            Log.d(LOGTAG, "key is null or blank");
            return;
        }

        //File file = new File(Environment.getExternalStorageDirectory().toString() + "/" + key);
        Log.d(LOGTAG, "To be downloaded. file:  " + key);
        if (!item.isComboPicDownloading && ConstantsUtil.checkFileKeyExist(key)) {//TODO both file need to be checked
            Log.d(LOGTAG, "already downloaded. file:  " + key);
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//            Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
//            //item.setThumbnailImage(bm);
//            putBitmap(item.getCombo_PIC_Png_Key_Name(), bm);
//            holderItemView.ivThumbnail.setImageBitmap(bm);
//            holderItemView.ivThumbnail.setAlpha(ALPHA_IMAGE_DOWNLOADED);
//            holderItemView.pb.setVisibility(View.INVISIBLE);
//            item.isPicDownloading = false;
//            holderItemView.pb.setVisibility(View.INVISIBLE);
            new BitmapWorkerTask(new WeakReference<ImageView>(holderItemView.ivThumbnail), item, new WeakReference<ProgressBar>(holderItemView.pb)).execute();
            return;
        }
        File file = new File(ConstantsUtil.FILE_PATH_APP_ROOT + key);
        TransferObserver observer = transferUtility.download(ConstantsUtil.AWSBucketName, key, file);
        observers.add(observer);
        observer.setTransferListener(new DownloadListener(item, holderItemView, observer));
    }
    /*
     * A TransferListener class that can listen to a download task and be
     * notified when the status changes.
     */


    private void hideProgressBar(WeakReference<ProgressBar> pbWeakRef) {
        if (pbWeakRef != null) {
            ProgressBar pb = pbWeakRef.get();
            if (pb != null)
                pb.setVisibility(View.INVISIBLE);
        }
    }

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
            Log.e(LOGTAG, "Error during download thumbnail : " + comboData.getCombo_ID());
//            hideDownloadProgressBar(pbWeakRef);
//            if(comboData != null)
//                comboData.isPicDownloading = false;
//            setBitmap(comboData,ivWeakRefThumbnail);
//            if(comboData != null)
//                comboData.isPicDownloading = false;
        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

        }

        @Override
        public void onStateChanged(int id, TransferState state) {
            if (state == TransferState.FAILED) {
                Log.e(LOGTAG, "Thumbnail download failed :" + comboData.getCombo_ID());
                hideProgressBar(pbWeakRef);
                if (comboData != null)
                    comboData.isComboPicDownloading = false;
                new BitmapWorkerTask(ivWeakRefThumbnail, comboData, pbWeakRef).execute();
//                if(comboData != null)
//                comboData.isPicDownloading = false;
            }

            if (state == TransferState.COMPLETED) {
                hideProgressBar(pbWeakRef);

//                BitmapFactory.Options options = new BitmapFactory.Options();
//                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//                //Bitmap bm = BitmapFactory.decodeFile(hairItem.getThumbnailDownloadFilePath(), options);
//                File file = new File(Environment.getExternalStorageDirectory().toString() + "/" + comboData.getCombo_PIC_Png_Key_Name());
//                Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
//                Log.e(LOGTAG, "Completed "+ comboData.getCombo_PIC_Png_Key_Name());
//                if (bm != null) {
//                    //comboData.setThumbnailImage(bm);
//                    Log.e(LOGTAG, "Completed 2 : "+ comboData.getCombo_PIC_Png_Key_Name());
//                    putBitmap(comboData.getCombo_PIC_Png_Key_Name(), bm);
//                }

                if (comboData != null)
                    comboData.isComboPicDownloading = false;
                new BitmapWorkerTask(ivWeakRefThumbnail, comboData, pbWeakRef).execute();
//                if (ivWeakRefThumbnail != null) {
//                    ImageView iv = ivWeakRefThumbnail.get();
//                    new BitmapWorkerTask(iv,comboData);
//                    if(iv != null) {
////                        iv.setVisibility(View.VISIBLE);
////                        iv.setAlpha(ALPHA_IMAGE_DOWNLOADED);
////                        if (bm != null) {
////                            iv.setImageBitmap(bm);
////                            Log.e(LOGTAG, "Completed 3 : " + comboData.getCombo_PIC_Png_Key_Name());
////                        }
//                    }else{
//                        Log.e(LOGTAG, " notifyDataSetChanged 1 :" + comboData.getCombo_PIC_Png_Key_Name());
//                        //notifyDataSetChanged();
//                    }
//                }
//                else {
//                    Log.e(LOGTAG, " notifyDataSetChanged 2 :" + comboData.getCombo_PIC_Png_Key_Name());
//                    //notifyDataSetChanged();
//                }

            }
        }
    }


    public class BitmapWorkerTask extends AsyncTask<Void, Void, Bitmap> {
        private WeakReference<ImageView> ivWeaKRef;
        private WeakReference<ProgressBar> pbWeakRef;
        private ComboData comboData;

        public BitmapWorkerTask(WeakReference<ImageView> ivWeaKRef, ComboData comboData, WeakReference<ProgressBar> pbWeakRef) {
            this.ivWeaKRef = ivWeaKRef;
            this.comboData = comboData;
            this.pbWeakRef = pbWeakRef;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            File file = new File(ConstantsUtil.FILE_PATH_APP_ROOT + comboData.getCombo_PIC_Png_Key_Name());
            Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            if (bm != null) {
                putBitmap(comboData.getCombo_PIC_Png_Key_Name(), bm);
                comboData.isComboPicDownloading = false;
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
                    Log.e(LOGTAG, "onPostExecute : " + comboData.getCombo_PIC_Png_Key_Name());
                } else {
                    Log.e(LOGTAG, " notifyDataSetChanged 1 :" + comboData.getCombo_PIC_Png_Key_Name());
                    //notifyDataSetChanged();
                }
            } else {
                Log.e(LOGTAG, " notifyDataSetChanged 2 :" + comboData.getCombo_PIC_Png_Key_Name());
                //notifyDataSetChanged();
            }
        }
    }

}
