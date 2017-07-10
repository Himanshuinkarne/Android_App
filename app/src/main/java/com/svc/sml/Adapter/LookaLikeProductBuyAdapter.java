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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.svc.sml.Activity.WebActivity;
import com.svc.sml.Database.ComboData;
import com.svc.sml.Database.LAData;
import com.svc.sml.Database.SkuData;
import com.svc.sml.Database.User;
import com.svc.sml.Helper.DataManager;
import com.svc.sml.Helper.GsonRequest;
import com.svc.sml.Helper.VolleyHelper;
import com.svc.sml.Helper.VolleyImageRequest;
import com.svc.sml.InkarneAppContext;
import com.svc.sml.R;
import com.svc.sml.Utility.ConstantsUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by himanshu on 12/30/15.
 */
public class LookaLikeProductBuyAdapter extends BaseAdapter {
    private static final String LOGTAG = LookaLikeProductBuyAdapter.class.getName();
    public static final String PARAM_EXTRA_WEB_URI = "PARAM_EXTRA_WEB_URI";
    public static final String PARAM_EXTRA_WEB_TITLE = "PARAM_EXTRA_WEB_TITLE";
    public Context mContext;
    private ArrayList<SkuData> skuList;
    private ComboData comboData;
    private LayoutInflater mInflater;
    private OnAdapterInteractionListener listener;
    private ImageLoader mImageLoader;

    public LookaLikeProductBuyAdapter.OnAdapterInteractionListener getListener() {
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

    public LookaLikeProductBuyAdapter(Context ctx, ArrayList<SkuData> skuList, ComboData combodata) {
        mContext = ctx;
        this.skuList = skuList;
        this.comboData = combodata;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mImageLoader = VolleyImageRequest.getInstance(mContext)
                .getImageLoader();
    }

    public void updateLaDataList(List<SkuData> newlist) {
        skuList.clear();
        skuList.addAll(newlist);
        if(mImageLoader != null)
        this.notifyDataSetChanged();
    }

    public void add(SkuData path) {
        skuList.add(path);
    }

    public void clear() {
        skuList.clear();
    }

    public void remove(int index) {
        skuList.remove(index);
    }

    @Override
    public int getCount() {
        return skuList.size();
    }

    @Override
    public Object getItem(int position) {
        return skuList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) { // if it's not recycled, initialize some
            convertView = mInflater.inflate(R.layout.list_item_lookalike_product_buy, parent, false);

            // Create and save off the holder in the tag so we get quick access to inner fields
            // This must be done for performance reasons
            holder = new Holder();
            //holder.ivThumbnail = (ImageView) convertView.findViewById(R.id.iv_thumbnail);\
            holder.conSoldOut = (LinearLayout) convertView.findViewById(R.id.con_soldout);
            holder.conOutOfStock = (LinearLayout) convertView.findViewById(R.id.con_outofstock);
            holder.ivSoldout = (ImageView) convertView.findViewById(R.id.iv_soldout);
            holder.ivOutofStock = (ImageView) convertView.findViewById(R.id.iv_outofstock);
            holder.mNetworkImageView = (NetworkImageView) convertView.findViewById(R.id.iv_network_product);
            //holder.btnCart = (ImageButton) convertView.findViewById(R.id.btn_cart);
            //holder.btnCart.setOnClickListener(mClickListner);
            //holder.btnBuy = (ImageButton) convertView.findViewById(R.id.btn_buy);
            //holder.btnBuy.setOnClickListener(mClickListner);
            holder.tvPrice = (TextView) convertView.findViewById(R.id.tv_price_value);
            holder.tvBrand = (TextView) convertView.findViewById(R.id.tv_brand_value);
            //holder.tvSeller = (TextView) convertView.findViewById(R.id.tv_seller_value);
            holder.tvPrice.setTypeface(InkarneAppContext.getInkarneTypeFaceMolengo());
            holder.tvBrand.setTypeface(InkarneAppContext.getInkarneTypeFaceMolengo());
            holder.pb1 = (ProgressBar)convertView.findViewById(R.id.pb);
            //holder.tvSeller.setTypeface(InkarneAppContext.getInkarneTypeFaceMolengo());
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        final SkuData skuData = skuList.get(position);
        final LAData laData = skuData.laData;
        Log.d("debug", "count product :" + position + "skuData: " + skuData.getmSKU_ID());
        if(laData != null && laData.getSKU_ID() != null) {
            populateListItem(laData, holder);
        }else{

            requestLAData(skuData, holder);
//            if(holder.conSoldOut.getVisibility() == View.INVISIBLE) {
//                requestLAData(skuData, holder);
//            }
        }
        return convertView;
    }

    public void populateListItem(final LAData laData, final Holder holder){


        if(laData == null){
            holder.ivSoldout.setImageResource(R.drawable.sold_out_exact_match);
            holder.conSoldOut.setVisibility(View.VISIBLE);
            holder.pb1.setVisibility(View.INVISIBLE);
            return;
        }

        Log.d("debug",  "   LAData :" + laData.toString());
        holder.tvPrice.setText("\u20B9" + " " + laData.getPrice());
        holder.tvBrand.setText(laData.getBrand());
        if (laData.getCart_Count() > 0 || laData.getUser_Cart_Flag() != 0) {
            //holder.btnCart.setBackgroundColor(Color.parseColor("#dddddd"));
            //holder.btnCart.setAlpha((float) 0.6);
            //holder.btnCart.setImageResource(R.drawable.btn_cart_selected);
            //holder.btnCart.setEnabled(false);
        } else {
            //holder.btnCart.setBackgroundColor(Color.parseColor("#ffffff"));
            //holder.btnCart.setAlpha((float) 1.0);
            //holder.btnCart.setAlpha((float) 0.6);
            //holder.btnCart.setImageResource(R.drawable.btn_cart);
            //holder.btnCart.setEnabled(true);
        }

        if (laData.getPic_URL() != null && !laData.getStatus().equals("InActive")) {//InActive  && !laData.getPic_URL().equals("None")//TODO
            mImageLoader.get(laData.getPic_URL(), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    holder.pb1.setVisibility(View.INVISIBLE);
                    if (response.getBitmap() != null) {
                        Log.w(LOGTAG, "bitmap found");
                    } else {
                        if (!isImmediate)
                            Log.e(LOGTAG, "bitmap null");
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    holder.pb1.setVisibility(View.INVISIBLE);
                    laData.setStatus("InActive");
                    Log.e(LOGTAG, "bitmap error");
                    notifyDataSetChanged();
                }
            });
        }
        holder.mNetworkImageView.setImageUrl(laData.getPic_URL(), mImageLoader);

        if (laData.getStatus() == null || laData.getStatus().isEmpty() || laData.getStatus().equals("NA")) {//TODO
            holder.conOutOfStock.setVisibility(View.INVISIBLE);
            holder.pb1.setVisibility(View.INVISIBLE);
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
            } else {
                holder.ivOutofStock.setImageResource(0);
            }
            holder.conOutOfStock.setVisibility(View.VISIBLE);
        }
    }


    private static class Holder {
        private NetworkImageView mNetworkImageView;
        public TextView tvPrice;
        public TextView tvBrand;
        public LinearLayout conSoldOut;
        public LinearLayout conOutOfStock;
        public ImageView ivSoldout;
        public ImageView ivOutofStock;
        public ProgressBar pb1;
    }


    public void requestLAData(final SkuData skuData,final Holder holder) {
        if(skuData == null)
            return;
        //trackEvent("lookalike",skuData.getmSKU_ID(),"");
        String uri = ConstantsUtil.URL_BASEPATH + ConstantsUtil.URL_METHOD_LOOKALIKE + User.getInstance().getmUserId() + "/" + comboData.getCombo_ID() + "/" + skuData.getmSKU_ID();
        holder.pb1.setVisibility(View.VISIBLE);
        Log.w(LOGTAG, "URI :" + uri);
        final GsonRequest gsonRequest = new GsonRequest(uri, LAData.LADataWrapper.class, null, new Response.Listener<LAData.LADataWrapper>() {

            @Override
            public void onResponse(LAData.LADataWrapper ladatas) {
                holder.pb1.setVisibility(View.INVISIBLE);
                List<LAData> laDataList = ladatas.getLaDatas();
                if (laDataList.size() > 0) {
                    skuData.setLaDataList(laDataList);
                    skuData.laData = laDataList.get(0);
                    Log.w(LOGTAG, "onResponse :" );
                    populateListItem(skuData.laData,holder);
                } else {
                    populateListItem(skuData.laData,holder);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                holder.pb1.setVisibility(View.INVISIBLE);
                populateListItem(skuData.laData,holder);
                if (volleyError != null && volleyError.getMessage() != null)
                    Log.e("LookLikeFragment", volleyError.getMessage());

            }
        });
        VolleyHelper.getInstance(mContext.getApplicationContext()).addToRequestQueue(gsonRequest);
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
                    LookaLikeProductBuyAdapter.this.notifyDataSetChanged();
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
                LookaLikeProductBuyAdapter.this.notifyDataSetChanged();
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
        browserIntent.putExtra(LookaLikeProductBuyAdapter.PARAM_EXTRA_WEB_URI, ladata.getLink());
        browserIntent.putExtra(LookaLikeProductBuyAdapter.PARAM_EXTRA_WEB_TITLE, ladata.getTitle());
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
}
