package com.svc.sml.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
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

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by himanshu on 1/19/16.
 */
public class CartAdapter extends BaseAdapter {
    public static final String PARAM_EXTRA_WEB_URI = "PARAM_EXTRA_WEB_URI";
    public static final String PARAM_EXTRA_WEB_TITLE = "PARAM_EXTRA_WEB_TITLE";
    private static final String LOGTAG = LookaLikeProductAdapter.class.getName();
    public Context mContext;
    private ArrayList<LAData> itemList;
    private LayoutInflater mInflater;
    private TransferUtility transferUtility;
    private List<TransferObserver> observers;
    private OnCartAdapterInteractionListener listener;
    private ComboData comboData;
    private ImageLoader mImageLoader;

    public OnCartAdapterInteractionListener getListener() {
        return listener;
    }

    public void setListener(OnCartAdapterInteractionListener listener) {
        this.listener = listener;
    }


    public interface OnCartAdapterInteractionListener {
        // TODO: Update argument type and name
        void onCartRemoved(LAData laData);
        void onBuyAdded(LAData laData);
        void onView3d(LAData laData);
    }

    public CartAdapter(Context ctx, ArrayList<LAData> itemList,ComboData combodata) {
        mContext = ctx;
        this.itemList = itemList;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        transferUtility = AWSUtil.getTransferUtility(mContext);
        observers = new ArrayList<TransferObserver>();
        this.comboData = combodata;
        mImageLoader = VolleyImageRequest.getInstance(mContext)
                .getImageLoader();
    }

    public void updateLaDataList(List<LAData> newlist) {
        itemList.clear();
        itemList.addAll(newlist);
        this.notifyDataSetChanged();
    }

    public CartAdapter(Context ctx, ArrayList<LAData> laDataList) {
        mContext = ctx;
        this.itemList = itemList;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        transferUtility = AWSUtil.getTransferUtility(mContext);
        observers = new ArrayList<TransferObserver>();
        mImageLoader = VolleyImageRequest.getInstance(mContext)
                .getImageLoader();
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
            //convertView = mInflater.inflate(R.layout.list_item_cart2, parent, false);
            convertView = mInflater.inflate(R.layout.list_item_cart, parent, false);

            // Create and save off the holder in the tag so we get quick access to inner fields
            // This must be done for performance reasons
            holder = new Holder();
            //holder.ivThumbnail = (ImageView) convertView.findViewById(R.id.iv_thumbnail);
            holder.conSoldOut = (LinearLayout)convertView.findViewById(R.id.con_soldout);
            holder.conOutOfStock = (LinearLayout)convertView.findViewById(R.id.con_outofstock);
            holder.ivSoldout = (ImageView)convertView.findViewById(R.id.iv_soldout);
            holder.ivOutofStock = (ImageView)convertView.findViewById(R.id.iv_outofstock);
            holder.mNetworkImageView = (NetworkImageView) convertView.findViewById(R.id.iv_network_product);
            holder.btnRemoveCart = (ImageButton) convertView.findViewById(R.id.btn_cart);

            holder.btnRemoveCart.setImageDrawable(mContext.getResources().getDrawable(R.drawable.btn_cart_delete));
            holder.btnRemoveCart.setOnClickListener(mClickListner);
            holder.btnView3d = (ImageButton) convertView.findViewById(R.id.btn_view3d);
            holder.btnView3d.setOnClickListener(mClickListner);
            holder.btnBuy = (ImageButton) convertView.findViewById(R.id.btn_buy);
            holder.btnBuy.setOnClickListener(mClickListner);
            holder.tvPrice = (TextView) convertView.findViewById(R.id.tv_price_value);
            holder.tvBrand = (TextView) convertView.findViewById(R.id.tv_brand_value);
            holder.tvSeller = (TextView) convertView.findViewById(R.id.tv_seller_value);
            holder.tvPrice.setTypeface(InkarneAppContext.getInkarneTypeFaceMolengo());
            holder.tvBrand.setTypeface(InkarneAppContext.getInkarneTypeFaceMolengo());
            holder.tvSeller.setTypeface(InkarneAppContext.getInkarneTypeFaceMolengo());

            //holder.tvFit = (TextView) convertView.findViewById(R.id.tv_fit_value);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        final LAData laData = itemList.get(position);
        Log.d("debug", "count product :" + position + "   LAData :" + laData.toString());

        //holder.tvPrice.setText( laData.getPrice());
        holder.tvPrice.setText("\u20B9" + " " + laData.getPrice());
        holder.tvBrand.setText( laData.getBrand());
        holder.tvSeller.setText( laData.getSeller());
        //holder.tvFit.setText("NA");//TODO

        ////holder.ivThumbnail.setImageBitmap(null);
        holder.btnBuy.setTag(laData);
        holder.btnRemoveCart.setTag(laData);
        holder.btnView3d.setTag(laData);

        //mImageLoader.get(laData.getPic_URL(), ImageLoader.getImageListener(holder.mNetworkImageView,R.drawable.abc_btn_radio_material, android.R.drawable.ic_dialog_alert));

        if (laData.getPic_URL() != null && !laData.getStatus().equals("InActive"))
            mImageLoader.get(laData.getPic_URL(), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (response.getBitmap() != null) {
                        //some code
                        Log.w(LOGTAG,"bitmap found");
                    }
                    else {
                        //some code
                        if(!isImmediate)
                            Log.e(LOGTAG,"bitmap null");
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    //laData.setStatus(""); //TODO
                    laData.setStatus("InActive");
                    Log.e(LOGTAG, "bitmap error");
                    notifyDataSetChanged();
                }
            });
        holder.mNetworkImageView.setImageUrl(laData.getPic_URL(), mImageLoader);

        if(laData.getStatus() == null || laData.getStatus().isEmpty()|| laData.getStatus().equals("NA")){  //TOD
            holder.conOutOfStock.setVisibility(View.INVISIBLE);
            holder.conSoldOut.setVisibility(View.VISIBLE);
            if(laData.getExact_Match().equals("True")){
                holder.ivSoldout.setImageResource(R.drawable.sold_out_exact_match);
            }else{
                holder.ivSoldout.setImageResource(R.drawable.sold_out);
            }
        }
        else if (laData.getStatus().equals("Active")){
            holder.conSoldOut.setVisibility(View.INVISIBLE);
            holder.conOutOfStock.setVisibility(View.INVISIBLE);
        }
        else{
            //sold out
            holder.conSoldOut.setVisibility(View.INVISIBLE);
            holder.conOutOfStock.setVisibility(View.VISIBLE);
            if (!laData.getSeller().equals("Amazon")) {
                if (laData.getExact_Match().equals("True")) {
                    holder.ivOutofStock.setImageResource(R.drawable.out_of_stock_exact_match);
                } else {
                    holder.ivOutofStock.setImageResource(R.drawable.out_of_stock);
                }
            }else{
                holder.ivOutofStock.setImageResource(0);
            }
        }
        return convertView;
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
                case R.id.btn_view3d: {
                  if(listener != null) {
                      LAData ladata = (LAData) v.getTag();
                      listener.onView3d((LAData) v.getTag());
                  }
                }
                break;
                case R.id.btn_cart:
                    showDailog("","Will be removed from Cart !",(LAData) v.getTag());
                    //removeFromCart((LAData) v.getTag());
                    break;
            }
            Log.d("buttonClick", "inside list");
        }
    };

    private void removeFromCart(final LAData ladata) {
        //TODO
//        ladata.setCart_Count(0);
//        if (listener != null)
//            listener.onCartRemoved(ladata);
        itemList.remove(ladata);
        notifyDataSetChanged();
        //final String uri = ConstantsUtil.URL_BASEPATH + ConstantsUtil.URL_METHOD_UPDATE_CART + User.getInstance().getmUserId() + "/" + ladata.getPurchase_SKU_ID()+"/"+ladata.getCart_Count();
        final String uri = ConstantsUtil.URL_BASEPATH + ConstantsUtil.URL_METHOD_UPDATE_CART + User.getInstance().getmUserId() + "/"  + ladata.getCombo_ID()+"/" + ladata.getPurchase_SKU_ID()+"/"+"0";

        DataManager.getInstance().updateMethodToServer(uri, ConstantsUtil.EUpdateType.eUpdateTypeCart.toString(), new DataManager.OnResponseHandlerInterface() {
            @Override
            public void onResponse(Object obj) {
                ladata.setCart_Count(0);
                if (listener != null)
                    listener.onCartRemoved(ladata);
                InkarneAppContext.getDataSource().delete(ladata);
            }

            @Override
            public void onResponseError(String errorMessage, int errorCode) {

            }
        });
    }

    public void  showDailog(String title, String message,final LAData ladata) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                removeFromCart(ladata);
                CartAdapter.this.notifyDataSetChanged();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                //dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void openBuyUrl(final LAData ladata) {
        //http://inkarneweb-prod.elasticbeanstalk.com/Service1.svc/UpdateBuy/4/F081M1
        final String uri = ConstantsUtil.URL_BASEPATH + ConstantsUtil.URL_METHOD_UPDATE_BUY + User.getInstance().getmUserId() + "/" + ladata.getCombo_ID()+"/" +ladata.getPurchase_SKU_ID();
        Intent browserIntent = new Intent(mContext, WebActivity.class);
        browserIntent.putExtra(LookaLikeProductAdapter.PARAM_EXTRA_WEB_URI, ladata.getLink());
        browserIntent.putExtra(LookaLikeProductAdapter.PARAM_EXTRA_WEB_TITLE, ladata.getTitle());
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //mContext.startActivityForResult(browserIntent,1);
        mContext.startActivity(browserIntent);
        if (listener != null)
            listener.onBuyAdded(ladata);
        DataManager.getInstance().updateMethodToServer(uri, ConstantsUtil.EUpdateType.eUpdateTypeBuy.toString(), new DataManager.OnResponseHandlerInterface() {
            @Override
            public void onResponse(Object obj) {
                ladata.setBuy_Count(1);
//                if (listener != null)
//                    listener.onBuyAdded(ladata);
            }

            @Override
            public void onResponseError(String errorMessage, int errorCode) {

            }
        });
    }

    private static class Holder {
        private NetworkImageView mNetworkImageView;
        public ImageButton btnRemoveCart;
        public ImageButton btnView3d;
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
}