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

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.svc.sml.Database.SkuData;
import com.svc.sml.Database.User;
import com.svc.sml.R;
import com.svc.sml.Utility.AWSUtil;
import com.svc.sml.Utility.ConstantsUtil;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by himanshu on 12/31/15.
 */
public class LookaLikeSkuAdapter extends BaseAdapter
{
    private static final String LOGTAG = "LookaLikeSkuAdapter";
    private Context mContext;
    private TransferUtility transferUtility;
    ArrayList<SkuData> itemList = new ArrayList<SkuData>();
    private LayoutInflater mInflater;
    private List<TransferObserver> observers;

    private HashMap<String, Integer> hmSkuImages = new HashMap<String, Integer>() {{
        put("Clutches",R.drawable.selector_ll_clutches);
        put("Pants",R.drawable.selector_ll_pants);
        put("Shoes",R.drawable.selector_ll_shoes_female);
        put("Shoes_male",R.drawable.selector_ll_shoes_male);
        put("Jacket",R.drawable.selector_ll_jacket);
        put("Skirt",R.drawable.selector_ll_skirt);
        put("Bags",R.drawable.selector_ll_bags);
        put("Belts",R.drawable.selector_ll_belts);
        put("Necklace",R.drawable.selector_ll_necklace);
        put("Earrings",R.drawable.selector_ll_earrings);
        put("Shirt",R.drawable.selector_ll_shirt);

        put("Shorts",R.drawable.selector_ll_shorts);
        put("Dress",R.drawable.selector_ll_dress);
        put("Sunglasses",R.drawable.selector_ll_sunglasses);
        put("Jeans",R.drawable.selector_ll_jeans);
        put("Jumpsuit",R.drawable.selector_ll_jumpsuit);
        put("Top",R.drawable.selector_ll_top);
    }};



    public LookaLikeSkuAdapter(Context c) {
        mContext = c;
    }
    public LookaLikeSkuAdapter(Context c, ArrayList<SkuData> listSkus) {
        mContext = c;
        itemList = listSkus;
//        if(itemList != null && itemList.size()>0)
//        itemList.get(0).isSelected = true;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        transferUtility = AWSUtil.getTransferUtility(mContext);
        observers = new ArrayList<TransferObserver>();

    }
    public void add(SkuData path) {
        itemList.add(path);
    }

    public void clear() {
        itemList.clear();
    }

    public void remove(int index){
        itemList.remove(index);
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) { // if it's not recycled, initialize some
            convertView = mInflater.inflate(R.layout.list_item_lookalike_sku, parent, false);
            holder = new Holder();
            holder.ivThumbnail = (ImageView) convertView.findViewById(R.id.iv_thumbnail);
            convertView.setTag(holder);
        }
        else {
            holder = (Holder) convertView.getTag();
        }
        SkuData skudata = itemList.get(position);
        Log.w(LOGTAG,"Category : " + skudata.getmA_Category());
        int resourceId =0;
        if(skudata.getmA_Category() != null && hmSkuImages.get(skudata.getmA_Category())!= null){
            if(skudata.getmA_Category().equals("Shoes") && User.getInstance().getmGender().equals("m")){
                resourceId = hmSkuImages.get(skudata.getmA_Category()+"_male");
            }else{
                resourceId = hmSkuImages.get(skudata.getmA_Category());
            }
        }
        //if(resourceId !=0)
        holder.ivThumbnail.setImageResource(resourceId);
        holder.ivThumbnail.setSelected(skudata.isSelected);
       //holder.ivThumbnail.setImageResource(0);
        //holder.ivThumbnail.setImageBitmap(null);

//        if(skudata.isSelected){
//            //holder.ivThumbnail.setBackgroundColor(Color.parseColor("#dddddd"));
//            //holder.ivThumbnail.setBackgroundResource(R.drawable.rectangle_outline_gray_rounded);
//            //holder.ivThumbnail.setBackgroundResource(R.drawable.rectangle_outline_tcolor_rounded);
//            holder.ivThumbnail.setSelected(skudata.isSelected);
//        }
//        else {
//            //holder.ivThumbnail.setBackgroundColor(Color.parseColor("#ffffff"));
//            //holder.ivThumbnail.setBackgroundResource(R.drawable.selector_sku_touch);
//            holder.ivThumbnail.setSelected(false);
//        }

//        if(skudata != null){
//           if(ConstantsUtil.checkFileKeysExist(skudata.getmA_PIC_Png_Key_Name())) {
//               String downloadPath = ConstantsUtil.FILE_PATH_APP_ROOT +skudata.getmA_PIC_Png_Key_Name();
//               BitmapFactory.Options options = new BitmapFactory.Options();
//               options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//               Bitmap bm = BitmapFactory.decodeFile(downloadPath, options);
//               if (bm != null)
//                   holder.ivThumbnail.setImageBitmap(bm);
//           }
//           else if(skudata.getmA_PIC_Png_Key_Name() != null){
//               HolderData holderdata= new HolderData();
//               //holderdata.imageViewReference = new WeakReference<ImageView>(holder.ivThumbnail);
//               //holderdata.imageViewReference = new WeakReference<ImageView>(holder.ivThumbnail);
//               holderdata.iv = holder.ivThumbnail;
//               holderdata.skuData = skudata;
//               beginDownload(holderdata);
//           }
//       }
        return convertView;
    }

    public Bitmap decodeSampledBitmapFromUri(String path, int reqWidth,
                                             int reqHeight) {
        Bitmap bm = null;
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(path, options);
        return bm;
    }

    public int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height
                        / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }

    private static class Holder{
        public ImageView ivThumbnail;
    }

    private static class HolderData {
        //public ImageView ivThumbnail;
        public ImageView iv;
        public ProgressBar pb;
        public SkuData skuData;
        //public ProgressBar pb1;
    }

    private void beginDownload(HolderData holderdata) {
        if (holderdata.skuData.getmA_PIC_Png_Key_Name() == null) {
            Log.e("Debug", " lookalikeadapter key is null");
            //ImageView pb = holderdata.imageViewReference.get();
            //ProgressBar pb = holderdata.pbWeakReference.get();
            return;
        }

        String key = holderdata.skuData.getmA_PIC_Png_Key_Name();
        //String filepath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(ConstantsUtil.FILE_PATH_APP_ROOT + key);
        //File file = new File("/sdcard/"  + key);
        Log.d(LOGTAG, " To be downloaded. file:  "+file);
        // Initiate the download
        TransferObserver observer = transferUtility.download(ConstantsUtil.AWSBucketName, key, file);
        holderdata.skuData.setPngDownloadPath(file.getAbsolutePath());
        observer.setTransferListener(new DownloadListener(holderdata.iv, holderdata.pb, holderdata.skuData));
        observers.add(observer);
    }

    public void stopDownload(){
        for(TransferObserver ob : observers){
            if(transferUtility != null)
                transferUtility.cancel(ob.getId());
            ob.cleanTransferListener();
        }
        observers.clear();
    }

    /*
     * A TransferListener class that can listen to a download task and be
     * notified when the status changes.
     */
    private class DownloadListener implements TransferListener {
        public HolderData holderdata;

        private WeakReference<ImageView> ivWeakReference = null;
        private  WeakReference<ProgressBar> pbWeakReference = null;
        private SkuData skuData;

        public DownloadListener(HolderData holderData){
            this.holderdata =holderData;
        }
        public DownloadListener(ImageView iv,ProgressBar pb, SkuData skuData){
            this.ivWeakReference = new WeakReference<ImageView>(iv);
            this.pbWeakReference = new WeakReference<ProgressBar>(pb);
            this.skuData = skuData;
        }
        @Override
        public void onError(int id, Exception e) {
            //Log.e("Debug", " lookalike adapter Error during download");
            //ImageView pb = holderdata.imageViewReference.get();
            //ProgressBar pb = holderdata.pbWeakReference.get();
        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

        }

        @Override
        public void onStateChanged(int id, TransferState state) {
            if (state == TransferState.FAILED){
                Log.e("Debug", "lookalikeAdapter Download failed");
                //ImageView pb = holderdata.imageViewReference.get();
                //ProgressBar pb = pbWeakReference.get();
            }
            else if (state == TransferState.COMPLETED){
                //Log.e("Debug", "Download failed");
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bm = BitmapFactory.decodeFile(skuData.getPngDownloadPath(), options);
                //skuData.setProductBitmap(bm);
                if(bm != null){
                    skuData.isImageDownloaded = true;
                }
                ImageView iv = ivWeakReference.get();
                if(iv != null) {
                    iv.setImageBitmap(bm);
                }
            }
        }
    }
}

