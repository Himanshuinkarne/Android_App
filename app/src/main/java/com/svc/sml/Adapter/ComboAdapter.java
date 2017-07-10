package com.svc.sml.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.util.List;

/**
 * Created by himanshu on 1/9/16.
 */
public class ComboAdapter  extends BaseAdapter {
    private static final String LOGTAG = "ComboAdapter";
    private Context mContext;
    private ArrayList<ComboData> itemList;
    public ArrayList<ComboData> orignalItemList;
    private LayoutInflater mInflater;
    private String pngPath =  ConstantsUtil.FILE_PATH_APP_ROOT+"PICLoad/";
    private TransferUtility transferUtility;
    private List<TransferObserver> observers;

    public  ComboAdapter(Context ctx, ArrayList<ComboData> itemList) {
        mContext = ctx;
        this.itemList = itemList;
        this.orignalItemList = itemList;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        transferUtility = AWSUtil.getTransferUtility(mContext);
        observers = new ArrayList<TransferObserver>();
    }

    public ComboAdapter(Context ctx) {
        mContext = ctx;
    }

    public void add(ComboData path) {
        itemList.add(path);
    }

    public void clear() {
        itemList.clear();
    }

    public void remove(int index) {
        itemList.remove(index);
    }

    @Override
    public int getCount() {
        if(itemList != null)
        return itemList.size();
        else return 0;
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
            convertView = mInflater.inflate(R.layout.list_item_combos, parent, false);

            // Create and save off the holder in the tag so we get quick access to inner fields
            // This must be done for performance reasons
            holder = new Holder();
            holder.ivThumbnail = (ImageView) convertView.findViewById(R.id.iv_thumbnail_combo);
            holder.pbCombopics = (ProgressBar)convertView.findViewById(R.id.pb_combopics);
            holder.llLikeAction = (LinearLayout)convertView.findViewById(R.id.ll_like_action);
            holder.llLikeAction.setOnClickListener(mClickListner);
            holder.tvTitle = (TextView)convertView.findViewById(R.id.tv_combo_title);
            holder.tvNumOfLikes = (TextView)convertView.findViewById(R.id.tv_num_likes);
            holder.tvStyleRating = (TextView)convertView.findViewById(R.id.tv_style_rating);
            convertView.setTag(holder);
        }
        else {
            holder = (Holder) convertView.getTag();
        }

        ComboData curCombodata = itemList.get(position);
        Log.d("debug", "count product :" + position + "   ComboData :" + curCombodata.toString());
        holder.tvTitle.setText(curCombodata.getCombo_Title());
        holder.tvNumOfLikes.setText(String.valueOf(curCombodata.getLikes_Count()));
        holder.tvStyleRating.setText(String.valueOf(curCombodata.getStyle_Rating()));

        holder.ivThumbnail.setImageBitmap(null);
//        if(curCombodata.getComboBitmap()!= null){
//            holder.ivThumbnail.setImageBitmap(curCombodata.getComboBitmap());
//            holder.pbCombopics.setVisibility(View.INVISIBLE);
//            Log.d("debug","bitmap for product found");
//        }

        String downloadPath = ConstantsUtil.FILE_PATH_APP_ROOT +curCombodata.getCombo_PIC_Png_Key_Name();
//        if(ConstantsUtil.checkFileExist(downloadPath)) {
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//            Bitmap bm = BitmapFactory.decodeFile(downloadPath, options);
//            curCombodata.setComboBitmap(bm);
//            holder.ivThumbnail.setImageBitmap(bm);
//            holder.pbCombopics.setVisibility(View.INVISIBLE);
//        }

        //else if(curCombodata.getCombo_PIC_Png_Key_Name() != null) {
            if(curCombodata.getCombo_PIC_Png_Key_Name() != null) {
                HolderData holderdata= new HolderData();
                holderdata.iv = holder.ivThumbnail;
                holderdata.combodata = curCombodata;
                beginDownload(holderdata);
            } else {
                //holder.pbCombopics.setVisibility(View.INVISIBLE);
                holder.ivThumbnail.setImageBitmap(null);
                //stop progressbar
            }
        //}
        return convertView;
    }

    public void stopDownload(){
        for(TransferObserver ob : observers){
            if(transferUtility != null)
                transferUtility.cancel(ob.getId());
            ob.cleanTransferListener();
        }
        observers.clear();
    }

    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final ArrayList<ComboData> results = new ArrayList<ComboData>();
                if (orignalItemList == null)
                    orignalItemList = itemList;
                if (constraint != null) {
                    if (orignalItemList != null && orignalItemList.size() > 0) {
                        for (final ComboData g : orignalItemList) {
                            if (g.getCombo_Style_Category().toLowerCase().contains(constraint.toString().toLowerCase()) ||
                                    g.getCombo_Title().toLowerCase().contains(constraint.toString().toLowerCase())  )
                                results.add(g);
                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                itemList = (ArrayList<ComboData>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public Bitmap decodeSampledBitmapFromUri(String path, int reqWidth,
                                             int reqHeight) {
        Bitmap bm = null;
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
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

    /**
     * View holder for the views we need access to
     */
    private View.OnClickListener mClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d("debug", "combo adapter item clicked :" );
        }
    };

    private static class Holder {
        public ImageView ivThumbnail;
        public ProgressBar pbCombopics;
        public TextView tvStyleRating;
        public LinearLayout llLikeAction;
        public TextView tvNumOfLikes;
        public TextView tvTitle;
    }

    private static class HolderData {
        public ImageView iv;
        public  ProgressBar pb;
        public ComboData combodata;
        //public ProgressBar pb1;
    }

    private void beginDownload(HolderData holderdata) {
        if (holderdata.combodata.getCombo_PIC_Png_Key_Name() == null) {
            Log.e(LOGTAG, "key is null");
            holderdata.pb.setVisibility(View.INVISIBLE);
            return;
        }

        String key = holderdata.combodata.getCombo_PIC_Png_Key_Name();
        String filepath = ConstantsUtil.FILE_PATH_APP_ROOT;
        File file = new File(Environment.getExternalStorageDirectory().toString() + "/" + key);
        Log.d(LOGTAG, " TO be downloaded. file:  "+file);
        TransferObserver observer = transferUtility.download(ConstantsUtil.AWSBucketName, key, file);
        observers.add(observer);
        observer.setTransferListener(new DownloadListener(holderdata.iv,holderdata.pb,holderdata.combodata));
    }


    private class DownloadListener implements TransferListener {
        public HolderData holderdata;

        private  WeakReference<ImageView> ivWeakReference = null;
        private  WeakReference<ProgressBar> pbWeakReference = null;
        private ComboData combodata;

        public DownloadListener(HolderData holderData){
            this.holderdata =holderData;
        }
        public DownloadListener(ImageView iv,ProgressBar pb, ComboData comboData){
            this.ivWeakReference = new WeakReference<ImageView>(iv);
            this.pbWeakReference = new WeakReference<ProgressBar>(pb);
            this.combodata = comboData;
        }
        @Override
        public void onError(int id, Exception e) {

        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

        }

        @Override
        public void onStateChanged(int id, TransferState state) {
            if (state == TransferState.FAILED){
                Log.e("Debug", "lookalikeAdapter Download failed");
                ProgressBar pb = pbWeakReference.get();
                if(pb != null)
                    pb.setVisibility(View.INVISIBLE);
            }
            else if (state == TransferState.COMPLETED){
                ProgressBar pb = pbWeakReference.get();
                if(pb != null)
                    pb.setVisibility(View.INVISIBLE);

                ImageView iv = ivWeakReference.get();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                String downloadPath = ConstantsUtil.FILE_PATH_APP_ROOT +combodata.getCombo_PIC_Png_Key_Name();
                Bitmap bm = BitmapFactory.decodeFile(downloadPath, options);
                if(bm != null)
               // combodata.setComboBitmap(bm);
                if(iv != null) {
                    if(bm != null) {
                        iv.setImageBitmap(bm);
                    }
                }
            }
        }
    }
}
