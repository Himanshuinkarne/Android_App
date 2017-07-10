package com.svc.sml.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.svc.sml.Activity.WebActivity;
import com.svc.sml.Database.ComboData;
import com.svc.sml.Database.LAData;
import com.svc.sml.Database.User;
import com.svc.sml.Helper.DataManager;
import com.svc.sml.Helper.VolleyImageRequest;
import com.svc.sml.InkarneAppContext;
import com.svc.sml.R;
import com.svc.sml.Utility.AWSUtil;
import com.svc.sml.Utility.ConstantsUtil;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by himanshu on 12/30/15.
 */
public class LookaLikeProductAdapter extends BaseAdapter {
    private static final String LOGTAG = LookaLikeProductAdapter.class.getName();
    public static final String PARAM_EXTRA_WEB_URI = "PARAM_EXTRA_WEB_URI";
    public static final String PARAM_EXTRA_WEB_TITLE = "PARAM_EXTRA_WEB_TITLE";
    public Context mContext;
    private ArrayList<LAData> itemList;
    private ComboData comboData;
    private LayoutInflater mInflater;
    private TransferUtility transferUtility;
    private List<TransferObserver> observers;
    private OnAdapterInteractionListener listener;
    private ImageLoader mImageLoader;

    public LookaLikeProductAdapter.OnAdapterInteractionListener getListener() {
        return listener;
    }

    public void setListener(OnAdapterInteractionListener listener) {
        this.listener = listener;
    }

    public ComboData getComboData() {
        return comboData;
    }

    public void setComboData(ComboData comboData) {
        this.comboData = comboData;
        notifyDataSetChanged();
    }

    public interface OnAdapterInteractionListener {
        // TODO: Update argument type and name
        void onCartAdded(LAData ladata);

        void onBuyAdded(LAData ladata);
    }

    public LookaLikeProductAdapter(Context ctx, ArrayList<LAData> itemList, ComboData combodata) {
        mContext = ctx;
        this.itemList = itemList;
        this.comboData = combodata;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        transferUtility = AWSUtil.getTransferUtility(mContext);
        observers = new ArrayList<TransferObserver>();
        mImageLoader = VolleyImageRequest.getInstance(mContext)
                .getImageLoader();
    }

    public void updateLaDataList(List<LAData> newlist) {
        itemList.clear();
        itemList.addAll(newlist);
        this.notifyDataSetChanged();
    }

    public LookaLikeProductAdapter(Context ctx) {
        mContext = ctx;
    }

    public void add(LAData path) {
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
            convertView = mInflater.inflate(R.layout.list_item_lookalike_product, parent, false);

            // Create and save off the holder in the tag so we get quick access to inner fields
            // This must be done for performance reasons
            holder = new Holder();
            //holder.ivThumbnail = (ImageView) convertView.findViewById(R.id.iv_thumbnail);\
            holder.conSoldOut = (LinearLayout) convertView.findViewById(R.id.con_soldout);
            holder.conOutOfStock = (LinearLayout) convertView.findViewById(R.id.con_outofstock);
            holder.ivSoldout = (ImageView) convertView.findViewById(R.id.iv_soldout);
            holder.ivOutofStock = (ImageView) convertView.findViewById(R.id.iv_outofstock);
            holder.mNetworkImageView = (NetworkImageView) convertView.findViewById(R.id.iv_network_product);
            holder.btnCart = (ImageButton) convertView.findViewById(R.id.btn_cart);
            holder.btnCart.setOnClickListener(mClickListner);
            //holder.btnBuy = (ImageButton) convertView.findViewById(R.id.btn_buy);
            //holder.btnBuy.setOnClickListener(mClickListner);
            holder.tvPrice = (TextView) convertView.findViewById(R.id.tv_price_value);
            holder.tvBrand = (TextView) convertView.findViewById(R.id.tv_brand_value);
            holder.tvSeller = (TextView) convertView.findViewById(R.id.tv_seller_value);
            holder.tvPrice.setTypeface(InkarneAppContext.getInkarneTypeFaceMolengo());
            holder.tvBrand.setTypeface(InkarneAppContext.getInkarneTypeFaceMolengo());
            holder.tvSeller.setTypeface(InkarneAppContext.getInkarneTypeFaceMolengo());
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        final LAData laData = itemList.get(position);
        Log.d("debug", "count product :" + position + "   LAData :" + laData.toString());

        holder.tvPrice.setText("\u20B9" + " " + laData.getPrice());
        holder.tvBrand.setText(laData.getBrand());
        //holder.tvBrand.setText("Brands Inkarne is good company. Ram is good boy");
        holder.tvSeller.setText(laData.getSeller());
        //holder.tvSeller.setText("Sellers Inkarne is good company. Ram is good boy");
        //holder.tvFit.setText("NA");//TODO

        ////holder.ivThumbnail.setImageBitmap(null);
       // holder.btnBuy.setTag(laData);
        holder.btnCart.setTag(laData);

        if (laData.getCart_Count() > 0 || laData.getUser_Cart_Flag() != 0) {
            //holder.btnCart.setBackgroundColor(Color.parseColor("#dddddd"));
            //holder.btnCart.setAlpha((float) 0.6);
            holder.btnCart.setImageResource(R.drawable.btn_cart_selected);
            holder.btnCart.setEnabled(false);
        } else {
            //holder.btnCart.setBackgroundColor(Color.parseColor("#ffffff"));
            //holder.btnCart.setAlpha((float) 1.0);
            //holder.btnCart.setAlpha((float) 0.6);
            holder.btnCart.setImageResource(R.drawable.btn_cart);
            holder.btnCart.setEnabled(true);
        }

        //String downloadPath = ConstantsUtil.FILE_PATH_APP_ROOT + laData.getPic_URL();
//        if (laData.getPic_URL() != null)
//            mImageLoader.get(laData.getPic_URL(), ImageLoader.getImageListener(holder.mNetworkImageView,
//                    R.drawable.abc_btn_radio_material, android.R.drawable.ic_dialog_alert));

        if (laData.getPic_URL() != null && !laData.getStatus().equals("InActive"))//InActive  && !laData.getPic_URL().equals("None")//TODO
            mImageLoader.get(laData.getPic_URL(), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (response.getBitmap() != null) {
                        //some code
                        Log.w(LOGTAG, "bitmap found");
                    } else {
                        //some code
                        if (!isImmediate)
                            Log.e(LOGTAG, "bitmap null");
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    laData.setStatus("InActive");
                    Log.e(LOGTAG, "bitmap error");
                    notifyDataSetChanged();
                }
            });
//        mImageLoader.get(laData.getPic_URL(), ImageLoader.getImageListener(holder.mNetworkImageView,
//                R.drawable.ic_action_like, android.R.drawable.ic_dialog_alert));
        holder.mNetworkImageView.setImageUrl(laData.getPic_URL(), mImageLoader);


        /*
        if (laData.getProductBitmap() != null) {
            holder.ivThumbnail.setImageBitmap(laData.getProductBitmap());
            Log.d("debug", "bitmap for product found");
        } else if (ConstantsUtil.checkFileExist(downloadPath)) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bm = BitmapFactory.decodeFile(downloadPath, options);
            laData.setProductBitmap(bm);
            holder.ivThumbnail.setImageBitmap(bm);
        } else if (laData.getPic_URL() != null) {
            HolderData holderdata = new HolderData();
            holderdata.iv = holder.ivThumbnail;
            holderdata.laData = laData;
            beginDownload(holderdata);
        } else {
            holder.ivThumbnail.setImageBitmap(null);
            //stop progressbar
        }
        */

        if (laData.getStatus() == null || laData.getStatus().isEmpty() || laData.getStatus().equals("NA")) {//TODO
            holder.conOutOfStock.setVisibility(View.INVISIBLE);
            if (laData.getExact_Match().equals("True")) {
                holder.ivSoldout.setImageResource(R.drawable.sold_out_exact_match);
            } else {
                holder.ivSoldout.setImageResource(R.drawable.sold_out);
            }
            holder.conSoldOut.setVisibility(View.VISIBLE);

        } else if (laData.getStatus().equals("Active")) {
            holder.conSoldOut.setVisibility(View.INVISIBLE);
            holder.conOutOfStock.setVisibility(View.INVISIBLE);
        } else {
            //sold out
            holder.conSoldOut.setVisibility(View.INVISIBLE);
            if (!laData.getSeller().equals("Amazon")) {
                if (laData.getExact_Match().equals("True")) {
                    holder.ivOutofStock.setImageResource(R.drawable.out_of_stock_exact_match);
                } else {
                    holder.ivOutofStock.setImageResource(R.drawable.out_of_stock);
                }
            }else{
                holder.ivOutofStock.setImageResource(0);
            }
            holder.conOutOfStock.setVisibility(View.VISIBLE);
        }
        return convertView;
    }




    public void stopDownload() {
        for (TransferObserver ob : observers) {
            if (transferUtility != null)
                transferUtility.cancel(ob.getId());
            ob.cleanTransferListener();
        }
        observers.clear();
    }

    private SpannableString getSpannableStr(String name, String value) {
        int initialLen = name.length();
        int len = 10 - initialLen;
        for (int i = 0; i <= len; i++)
            name += " ";
        if (value == null) {
            value = "NA";
        }
        //String.format("%9s", name);
        String str = name + value;
        SpannableString ss1 = new SpannableString(str);
        ss1.setSpan(new ForegroundColorSpan(Color.BLUE), 0, initialLen, 0);// set color
        return ss1;
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

    /**
     * View holder for the views we need access to
     */
    private View.OnClickListener mClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_buy: {
                    openBuyUrl((LAData) v.getTag());
                }
                break;
                case R.id.btn_cart:

                    addToCart((LAData) v.getTag());
                    LookaLikeProductAdapter.this.notifyDataSetChanged();
                    //showDailog("", "Added to Cart !", (LAData) v.getTag());
                    Toast.makeText(mContext.getApplicationContext(), "Added to your cart!", Toast.LENGTH_SHORT).show();
                    break;
            }
            Log.d("buttonClick", "inside list");
        }
    };

    private void addToCart(final LAData ladata) {
        //final String uri = ConstantsUtil.URL_BASEPATH + ConstantsUtil.URL_METHOD_UPDATE_CART + User.getInstance().getmUserId() + "/" + comboData.getCombo_ID() + "/" + ladata.getPurchase_SKU_ID();
        final String uri = ConstantsUtil.URL_BASEPATH + ConstantsUtil.URL_METHOD_UPDATE_CART + User.getInstance().getmUserId() + "/" + comboData.getCombo_ID() + "/" + ladata.getPurchase_SKU_ID() + "/" + "1";
        ladata.setCart_Count(1);
        if (listener != null)
            listener.onCartAdded(ladata);

        DataManager.getInstance().updateMethodToServer(uri, ConstantsUtil.EUpdateType.eUpdateTypeCart.toString(), new DataManager.OnResponseHandlerInterface() {
            @Override
            public void onResponse(Object obj) {
//                InkarneAppContext.incrementCartNumber(1);
//                ladata.setCart_Count(1);
//                if (listener != null)
//                    listener.onCartAdded(ladata);
            }

            @Override
            public void onResponseError(String errorMessage, int errorCode) {

            }
        });
    }

    public void showDailog(String title, String message, final LAData ladata) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                addToCart(ladata);
                LookaLikeProductAdapter.this.notifyDataSetChanged();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void openBuyUrl(final LAData ladata) {
        //http://inkarneweb-prod.elasticbeanstalk.com/Service1.svc/UpdateBuy/4/F081M1


        final String uri = ConstantsUtil.URL_BASEPATH + ConstantsUtil.URL_METHOD_UPDATE_BUY + User.getInstance().getmUserId() + "/" + comboData.getCombo_ID() + "/" + ladata.getPurchase_SKU_ID();
        Intent browserIntent = new Intent(mContext, WebActivity.class);
        browserIntent.putExtra(LookaLikeProductAdapter.PARAM_EXTRA_WEB_URI, ladata.getLink());
        browserIntent.putExtra(LookaLikeProductAdapter.PARAM_EXTRA_WEB_TITLE, ladata.getTitle());
        mContext.startActivity(browserIntent);
        if (listener != null)
            listener.onBuyAdded(ladata);
        DataManager.getInstance().updateMethodToServer(uri, ConstantsUtil.EUpdateType.eUpdateTypeBuy.toString(), new DataManager.OnResponseHandlerInterface() {
            @Override
            public void onResponse(Object obj) {
//                ladata.setBuy_Count(1);
//                if (listener != null)
//                    listener.onBuyAdded(ladata);
            }

            @Override
            public void onResponseError(String errorMessage, int errorCode) {

            }
        });
    }

    private static class Holder {
        //public ImageView ivThumbnail;
        private NetworkImageView mNetworkImageView;
        public ImageButton btnCart;
        public ImageButton btnBuy;
        public TextView tvPrice;
        public TextView tvBrand;
        public TextView tvSeller;
        public LinearLayout conSoldOut;
        public LinearLayout conOutOfStock;
        public ImageView ivSoldout;
        public ImageView ivOutofStock;
        //public TextView tvFit;
        public ProgressBar pb1;
    }

    private static class HolderData {
        //public ImageView ivThumbnail;
        public WeakReference<ImageView> imageViewReference;
        public ImageView iv;
        public ProgressBar pb;
        public LAData laData;
        //public ProgressBar pb1;
    }

    private void beginDownload(HolderData holderdata) {
        if (holderdata.laData.getPic_URL() == null || holderdata.laData.getPic_URL().length() < 2) {
            Log.e(LOGTAG, "key is null");
            //ProgressBar pb = holderdata.pb;
            return;
        }
        String key = holderdata.laData.getPic_URL();
        String filepath = ConstantsUtil.FILE_PATH_APP_ROOT;
        File file = new File(ConstantsUtil.FILE_PATH_APP_ROOT + key);
        //File file = new File("/sdcard/"  + key);
        Log.d(LOGTAG, " To be downloaded. file:  " + file);
        // Initiate the download
        TransferObserver observer = transferUtility.download(ConstantsUtil.AWSBucketName, key, file);
        observers.add(observer);
        holderdata.laData.setPngDownloadPath(file.getAbsolutePath());
        observer.setTransferListener(new DownloadListener(holderdata.iv, holderdata.pb, holderdata.laData));
        //hashMapDownload.put(key, observer);
    }

    /*
     * A TransferListener class that can listen to a download task and be
     * notified when the status changes.
     */
    private class DownloadListener implements TransferListener {
        public HolderData holderdata;

        private WeakReference<ImageView> ivWeakReference = null;
        private WeakReference<ProgressBar> pbWeakReference = null;
        private LAData laData;

        public DownloadListener(HolderData holderData) {
            this.holderdata = holderData;
        }

        public DownloadListener(ImageView iv, ProgressBar pb, LAData ladata) {
            this.ivWeakReference = new WeakReference<ImageView>(iv);
            this.pbWeakReference = new WeakReference<ProgressBar>(pb);
            this.laData = ladata;
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
            if (state == TransferState.FAILED) {
                Log.e("Debug", "lookalikeAdapter Download failed");
                //ProgressBar pb = holderdata.imageViewReference.get();
            } else if (state == TransferState.COMPLETED) {
                //Log.e("Debug", "Download failed");
                ImageView iv = ivWeakReference.get();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bm = BitmapFactory.decodeFile(laData.getPngDownloadPath(), options);
                laData.setProductBitmap(bm);
                if (iv != null) {
                    if (bm != null)
                        iv.setImageBitmap(bm);
                    else
                        iv.setImageBitmap(null);
                }
            }
        }
    }


}
