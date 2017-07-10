package com.svc.sml.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.svc.sml.Database.ComboData;
import com.svc.sml.Helper.ImageFetcher;
import com.svc.sml.R;
import com.svc.sml.Utility.AWSUtil;
import com.svc.sml.Utility.ConstantsUtil;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is an adapter that provides base, abstract class for images
 * adapter.
 */
public class LooksFragmentBottomAdapter extends BaseAdapter {

    private final static String LOGTAG = LooksFragmentBottomAdapter.class.getSimpleName();
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

    public LooksFragmentBottomAdapter() {
        super();
    }

    public LooksFragmentBottomAdapter(final Context context, String looksCategoryTitle, ArrayList<ComboData> comboList) {
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
    public int getCount() {
        if(comboList != null)
        return comboList.size();
        else
            return 0;
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
//    protected abstract Bitmap createBitmap(int position);
//
//    protected abstract Bitmap createBitmap(Bitmap bitmap);

    protected Bitmap createBitmap(final int position) {

//        Log.v(LOGTAG, "creating item " + position);
//        final Bitmap bitmap = ((BitmapDrawable) context.getResources().getDrawable(IMAGE_RESOURCE_IDS.get(position)))
//                .getBitmap();
//        bitmapMap.put(position, new WeakReference<Bitmap>(bitmap));
//        return bitmap;
        return null;
    }

    //@Override
    protected Bitmap createBitmap(Bitmap bitmap) {
        return bitmap;
    }


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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            if (inflater == null)
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_looksfragment_hlv_bottom, parent, false);
            holder = new Holder();
            holder.ivThumbnail = (ImageView) convertView.findViewById(R.id.iv_combo_gallery_item);
//            holder.ivThumbnail.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(context,
//                            "Page " + position + " clicked",
//                            Toast.LENGTH_LONG).show();
//                }
//            });

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
            imageFetcher.manageSetImage(item.getPbId(),item.getCombo_PIC_Png_Key_Name(),holder.ivThumbnail,holder.pb,0);
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
}
