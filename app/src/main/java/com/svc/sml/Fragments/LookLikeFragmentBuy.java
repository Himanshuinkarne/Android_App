package com.svc.sml.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.svc.sml.Activity.WebActivity;
import com.svc.sml.Adapter.LookaLikeProductAdapter;
import com.svc.sml.Adapter.LookaLikeProductBuyAdapter;
import com.svc.sml.Database.ComboData;
import com.svc.sml.Database.InkarneDataSource;
import com.svc.sml.Database.LAData;
import com.svc.sml.Database.SkuData;
import com.svc.sml.Database.User;
import com.svc.sml.Helper.DataManager;
import com.svc.sml.Helper.GsonRequest;
import com.svc.sml.Helper.VolleyHelper;
import com.svc.sml.Helper.VolleyImageRequest;
import com.svc.sml.InkarneAppContext;
import com.svc.sml.Model.ClausalSkuModel;
import com.svc.sml.R;
import com.svc.sml.ShopActivity;
import com.svc.sml.Utility.ConstantsUtil;
import com.svc.sml.Utility.HorizontalListView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LookLikeFragmentBuy.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LookLikeFragmentBuy#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LookLikeFragmentBuy extends BaseFragment implements View.OnClickListener,  LookaLikeProductBuyAdapter.OnAdapterInteractionListener {
    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String LOGTAG = "LookLikeFragment";
    // TODO: Rename and change types of parameters

    private InkarneDataSource dataSource;
    private List<LAData> laDataList = new ArrayList<LAData>();
    private OnFragmentInteractionListener mListener;
    //private ProgressBar pbSku;
    private ComboData comboData;

    private int skuSelectedIndex = 0;
    private View.OnClickListener onSkuClickListener = null;
    private LinearLayout btnBuy;
    private TextView btnBuyTv;
    private LinearLayout btnCart;
    private TextView btnCartTv;
    private ImageLoader mImageLoader;

    private List<SkuData> listSkus;
    private LookaLikeProductBuyAdapter skuAdapter;
    private HorizontalListView hlvSku;
    private SkuData selectedSkuData;

    public ComboData getComboData() {
        return comboData;
    }

    public void setComboData(ComboData comboData) {
        skuSelectedIndex = 0;
        this.comboData = comboData;
        initData();
        if (skuAdapter != null)
            skuAdapter.notifyDataSetChanged();
    }

    public LookLikeFragmentBuy() {
        // Required empty public constructor
    }

    public static LookLikeFragmentBuy newInstance(String param1, String param2) {
        LookLikeFragmentBuy fragment = new LookLikeFragmentBuy();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static LookLikeFragmentBuy newInstance(ComboData comboData) {
        LookLikeFragmentBuy fragment = new LookLikeFragmentBuy();
        Bundle args = new Bundle();
        args.putSerializable("comboData", comboData);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && comboData == null) {
            comboData = (ComboData) getArguments().getSerializable("comboData");
        }
        mImageLoader = VolleyImageRequest.getInstance(getActivity())
                .getImageLoader();
        GATrackActivity(LOGTAG);
        onSkuClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               skuClickHandler((NetworkImageView)v,true);
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_lookalike_buy, container, false);
        dataSource = InkarneDataSource.getInstance(InkarneAppContext.getAppContext());
        dataSource.open();

        btnBuy = (LinearLayout) v.findViewById(R.id.btn_buy);
        btnBuyTv = (TextView)v.findViewById(R.id.btn_buy_tv);
        btnCart = (LinearLayout)v.findViewById(R.id.btn_cart);
        btnCartTv = (TextView) v.findViewById(R.id.btn_cart_tv);
        btnBuy.setOnClickListener(this);
        btnCart.setOnClickListener(this);
        //pbSku = (ProgressBar)v.findViewById(R.id.pb_sku);
        //pbSku.setVisibility(View.VISIBLE);
        hlvSku = (HorizontalListView)v.findViewById(R.id.hlvSKUList);
        //initData();
        return v;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart(){
        super.onStart();
        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnLooksFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void initData() {
        SkuData sku = null;
        if(listSkus == null)
        listSkus = new ArrayList<SkuData>();
        else
            listSkus.clear();
        if(comboData.getmA1_Category()!=null && !comboData.getmA1_Category().isEmpty()) {
            sku = new SkuData();
            sku.setmA_Category(comboData.getmA1_Category());
            sku.setmA_PIC_Png_Key_Name(comboData.getmA1_PIC_Png_Key_Name());
            sku.isSelected = false;
            sku.setmSKU_ID(comboData.getmSKU_ID1());
            listSkus.add(sku);
        }
        if (comboData.getmA2_Category() != null && comboData.getmSKU_ID2() != null && comboData.getmSKU_ID2().length() != 0) {
            sku = new SkuData();
            sku.setmA_Category(comboData.getmA2_Category());
            sku.setmA_PIC_Png_Key_Name(comboData.getmA2_PIC_Png_Key_Name());
            sku.setmSKU_ID(comboData.getmSKU_ID2());
            sku.isSelected = false;
            listSkus.add(sku);
        }

        if (comboData.getmA3_Category() != null && comboData.getmSKU_ID3() != null && comboData.getmSKU_ID3().length() != 0) {
            sku = new SkuData();
            sku.setmA_Category(comboData.getmA3_Category());
            sku.setmA_PIC_Png_Key_Name(comboData.getmA3_PIC_Png_Key_Name());
            sku.setmSKU_ID(comboData.getmSKU_ID3());
            sku.isSelected = false;
            listSkus.add(sku);
        }

        if (comboData.getmSKU_ID4() != null && comboData.getmSKU_ID4().length() != 0) {
            sku = new SkuData();
            sku.setmA_Category(comboData.getmA4_Category());
            sku.setmA_PIC_Png_Key_Name(comboData.getmA4_PIC_Png_Key_Name());
            sku.setmSKU_ID(comboData.getmSKU_ID4());
            sku.isSelected = false;
            listSkus.add(sku);
        }

        if (comboData.getmA5_Category() != null && comboData.getmSKU_ID5() != null && comboData.getmSKU_ID5().length() != 0) {
            sku = new SkuData();
            sku.setmA_Category(comboData.getmA5_Category());
            sku.setmA_PIC_Png_Key_Name(comboData.getmA5_PIC_Png_Key_Name());
            sku.setmSKU_ID(comboData.getmSKU_ID5());
            sku.isSelected = false;
            listSkus.add(sku);
        }

        if (!comboData.isA61Removed()) {
            if (comboData.getA61() != null && comboData.getA61().getObjId() != null && comboData.getA61().getObjId().length() != 0) {
                sku = new SkuData();
//                if(comboData.getmA6_Category() != null && !comboData.getmA6_Category().isEmpty())
//                  sku.setmA_Category(comboData.getmA6_Category());
//                else
                sku.setmA_Category(comboData.getA61().getAccessoryType());
                sku.setmA_PIC_Png_Key_Name(comboData.getA61().getThumbnailAwsKey());
                sku.setmSKU_ID(comboData.getA61().getObjId());
                sku.isSelected = false;
                listSkus.add(sku);
            } else if (comboData.getmA6_Category() != null && comboData.getmSKU_ID6() != null && comboData.getmSKU_ID6().length() != 0) {
                sku = new SkuData();
                sku.setmA_Category(comboData.getmA6_Category());
                sku.setmA_PIC_Png_Key_Name(comboData.getmA6_PIC_Png_Key_Name());
                sku.setmSKU_ID(comboData.getmSKU_ID6());
                sku.isSelected = false;
                listSkus.add(sku);
            }
        }

        if (comboData.getA71() != null && comboData.getA71().getObjId() != null && comboData.getA71().getObjId().length() != 0) {
            sku = new SkuData();
            //sku.setmA_Category(comboData.getmA7_Category());
            sku.setmA_Category(comboData.getA71().getAccessoryType());
            sku.setmA_PIC_Png_Key_Name(comboData.getA71().getThumbnailAwsKey());
            sku.setmSKU_ID(comboData.getA71().getObjId());
            sku.isSelected = false;
            listSkus.add(sku);
        } else if (comboData.getmA7_Category() != null && comboData.getmSKU_ID7() != null && comboData.getmSKU_ID7().length() != 0) {
            sku = new SkuData();
            sku.setmA_Category(comboData.getmA7_Category());
            sku.setmA_PIC_Png_Key_Name(comboData.getmA7_PIC_Png_Key_Name());
            sku.setmSKU_ID(comboData.getmSKU_ID7());
            sku.isSelected = false;
            listSkus.add(sku);
        }

        if (!comboData.isA91Removed()) {
            if (comboData.getA91() != null && comboData.getA91().getObjId() != null && comboData.getA91().getObjId().length() != 0
                    && comboData.getA91().getAccessoryType().equals(ConstantsUtil.EAccessoryType.eAccTypeSunglasses.toString())) {
                sku = new SkuData();
                //sku.setmA_Category(comboData.getmA9_Category());
                sku.setmA_Category(comboData.getA91().getAccessoryType());
                sku.setmA_PIC_Png_Key_Name(comboData.getA91().getThumbnailAwsKey());
                sku.setmSKU_ID(comboData.getA91().getObjId());
                sku.isSelected = false;
                listSkus.add(sku);
            } else if (comboData.getmA9_Category() != null && comboData.getmSKU_ID9() != null && comboData.getmSKU_ID9().length() != 0) {
                sku = new SkuData();
                sku.setmA_Category(comboData.getmA9_Category());
                sku.setmA_PIC_Png_Key_Name(comboData.getmA9_PIC_Png_Key_Name());
                sku.setmSKU_ID(comboData.getmSKU_ID9());
                sku.isSelected = false;
                listSkus.add(sku);
            }
        }
        if (!comboData.isA101Removed()) {
            if (comboData.getA101() != null && comboData.getA101().getObjId() != null && comboData.getA101().getObjId().length() != 0) {
                sku = new SkuData();
                sku.setmA_Category(comboData.getA101().getAccessoryType());
                sku.setmA_PIC_Png_Key_Name(comboData.getA101().getThumbnailAwsKey());
                sku.setmSKU_ID(comboData.getA101().getObjId());
                sku.isSelected = false;
                listSkus.add(sku);
            } else if (comboData.getmA10_Category()!= null && comboData.getmSKU_ID10() != null && comboData.getmSKU_ID10().length() != 0) {
                sku = new SkuData();
                sku.setmA_Category(comboData.getmA10_Category());
                sku.setmA_PIC_Png_Key_Name(comboData.getmA10_PIC_Png_Key_Name());
                sku.setmSKU_ID(comboData.getmSKU_ID10());
                sku.isSelected = false;
                listSkus.add(sku);
            }
        }
    }

    private void initView() {
        if (listSkus == null || listSkus.size() == 0)
            initData();

        skuAdapter = new LookaLikeProductBuyAdapter(getActivity(), (ArrayList<SkuData>) listSkus,comboData);
        hlvSku.setAdapter(skuAdapter);
        if (listSkus.size() != 0) {

        }
    }

    private void skuClickHandler(final NetworkImageView v,boolean shouldLaunchBuy){
        String skuId = (String)v.getTag();
        requestLAData(selectedSkuData,true);
        v.setBackgroundResource(R.drawable.bg_sku_selected);
    }

    private void showProductInfo(LAData ladata,boolean launchBuy){
        if (ladata.getCart_Count() > 0 || ladata.getUser_Cart_Flag() != 0){
            btnCart.setEnabled(false);
            if(getContext()!= null)
                btnCartTv.setTextColor(ContextCompat.getColor(getContext(), R.color.tcolor_yellow));
        }else{
            btnCart.setEnabled(true);
            if(getContext()!= null)
                btnCartTv.setTextColor(ContextCompat.getColor(getContext(), R.color.tcolor_green));
        }
    }

    public void requestLAData(final SkuData skuData,final boolean onclick) {
        if(skuData == null)
            return;
        trackEvent("lookalike",skuData.getmSKU_ID(),"");
        String uri = ConstantsUtil.URL_BASEPATH + ConstantsUtil.URL_METHOD_LOOKALIKE + User.getInstance().getmUserId() + "/" + comboData.getCombo_ID() + "/" + skuData.getmSKU_ID();
        laDataList = skuData.getLaDataList();
        if (laDataList != null && laDataList.size() != 0) {
            Log.w(LOGTAG, "already loaded:" + uri);
            /////////loadImage(CModel);
            if (skuData.getmSKU_ID().equals(selectedSkuData.getmSKU_ID())) {
                showProductInfo(laDataList.get(0), onclick);
            }
            return;
        }
        //pbSku.setVisibility(View.VISIBLE);
        Log.w(LOGTAG, "URI :" + uri);
        final GsonRequest gsonRequest = new GsonRequest(uri, LAData.LADataWrapper.class, null, new Response.Listener<LAData.LADataWrapper>() {

            @Override
            public void onResponse(LAData.LADataWrapper ladatas) {
                //pbSku.setVisibility(View.INVISIBLE);
                laDataList = ladatas.getLaDatas();
                if (laDataList.size() > 0) {
                    skuData.setLaDataList(laDataList);
                    skuData.laData = laDataList.get(0);
                    Log.w(LOGTAG, "onResponse :" );
                    /////////loadImage(CModel);
                    if (skuData != null && selectedSkuData != null && skuData.getmSKU_ID().equals(selectedSkuData.getmSKU_ID())) {

                        showProductInfo(laDataList.get(0),onclick);
                    }
                } else {
                    if(getActivity() != null)
                    Toast.makeText(getActivity(), "Oops,looks like everything is bought out.", Toast.LENGTH_SHORT).show();
                    //pbSimilarProducts.setVisibility(View.INVISIBLE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //pbSku.setVisibility(View.INVISIBLE);
                if (volleyError != null && volleyError.getMessage() != null)
                    Log.e("LookLikeFragment", volleyError.getMessage());
                //pbSimilarProducts.setVisibility(View.INVISIBLE);
                if(getActivity() != null)
                Toast.makeText(getActivity(), ConstantsUtil.MESSAGE_TOAST_NETWORK_RESPONSE_FAILED, Toast.LENGTH_SHORT).show();
            }
        });
        VolleyHelper.getInstance(this.getContext().getApplicationContext()).addToRequestQueue(gsonRequest);
    }


    private void loadImage(final ClausalSkuModel cModel){
        Log.w(LOGTAG,"status:"+cModel.skuData.laData.getStatus() + " url:" +cModel.skuData.laData.getPic_URL());
        final LAData laData = cModel.skuData.laData;
        if (laData!=null && laData.getPic_URL() != null && !laData.getPic_URL().equals("None")) {
            Log.w(LOGTAG, "get pic "+laData.getPrice() +"  "+laData.getBrand());
            mImageLoader.get(laData.getPic_URL(), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (response.getBitmap() != null) {
                        Log.w(LOGTAG, "bitmap found "+laData.getPrice() +"  "+laData.getBrand());
                        if(laData.getStatus() == null || laData.getStatus().isEmpty() ||laData.getStatus().equals("InActive")){
                            cModel.iv2.setImageResource(R.drawable.out_of_stock);
                        }
                    } else {
                        if (!isImmediate) {
                            Log.w(LOGTAG, "bitmap null " + laData.getPrice() + "  " + laData.getBrand());
                            cModel.iv.setErrorImageResId(R.drawable.sold_out);
                        }
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    cModel.skuData.laData.setStatus("InActive");
                    Log.w(LOGTAG, "bitmap error");
                    //cModel.iv.setBackgroundColor(getResources().getColor(R.color.color_gray_line));
                    cModel.iv.setErrorImageResId(R.drawable.sold_out);
                }
            });
//        mImageLoader.get(laData.getPic_URL(), ImageLoader.getImageListener(cModel.iv2,
//                R.drawable.out_of_stock, android.R.drawable.ic_dialog_alert));
            cModel.iv.setImageUrl(laData.getPic_URL(), mImageLoader);
        }else{
            Log.w(LOGTAG, "bitmap2 error");
            cModel.iv.setErrorImageResId(R.drawable.sold_out);
            cModel.iv.setBackgroundColor(getResources().getColor(R.color.color_gray_line));
            //cModel.iv2.setImageResource(Integer.parseInt(null));
        }
    }

    /*************/

    @Override
    public void onClick(View v) {
        if(selectedSkuData == null || selectedSkuData.laData == null)
            return;
        switch (v.getId()){
            case R.id.btn_buy:{
                openBuyUrl(selectedSkuData.laData);
            }
            break;
            case R.id.btn_cart:{
                addToCart(selectedSkuData.laData);
            }
            break;
            default:
                break;
        }
    }


    private void addToCart(final LAData ladata) {
        if(ladata.getPurchase_SKU_ID()==null|| ladata.getPurchase_SKU_ID().isEmpty()||ladata.getUser_Cart_Flag()!= 0){
            return;
        }
        btnCartTv.setTextColor(ContextCompat.getColor(getContext(), R.color.tcolor_yellow));
        //final String uri = ConstantsUtil.URL_BASEPATH + ConstantsUtil.URL_METHOD_UPDATE_CART + User.getInstance().getmUserId() + "/" + comboData.getCombo_ID() + "/" + ladata.getPurchase_SKU_ID();
        final String uri = ConstantsUtil.URL_BASEPATH + ConstantsUtil.URL_METHOD_UPDATE_CART + User.getInstance().getmUserId() + "/" + comboData.getCombo_ID() + "/" + ladata.getPurchase_SKU_ID() + "/" + "1";
        ladata.setCart_Count(1);
        onCartAdded(ladata);
        ladata.setUser_Cart_Flag(1);
        if(getActivity() != null)
        Toast.makeText(getActivity(),"Added to cart",Toast.LENGTH_SHORT).show();
        DataManager.getInstance().updateMethodToServer(uri, ConstantsUtil.EUpdateType.eUpdateTypeCart.toString(), new DataManager.OnResponseHandlerInterface() {
            @Override
            public void onResponse(Object obj) {
                //ladata.setUser_Cart_Flag(1);
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

    private void openBuyUrl(final LAData ladata) {
        if( ladata.getPurchase_SKU_ID()==null|| ladata.getPurchase_SKU_ID().isEmpty()){
            return;
        }
        final String uri = ConstantsUtil.URL_BASEPATH + ConstantsUtil.URL_METHOD_UPDATE_BUY + User.getInstance().getmUserId() + "/" + comboData.getCombo_ID() + "/" + ladata.getPurchase_SKU_ID();
        Intent browserIntent = new Intent(getActivity(), WebActivity.class);
        browserIntent.putExtra(LookaLikeProductAdapter.PARAM_EXTRA_WEB_URI, ladata.getLink());
        browserIntent.putExtra(LookaLikeProductAdapter.PARAM_EXTRA_WEB_TITLE, ladata.getTitle());
        getActivity().startActivity(browserIntent);
        Log.d(LOGTAG, "ComboID added to Cart :" + comboData.getCombo_ID());
        trackEvent("Buy-row", ladata.getPurchase_SKU_ID(), "");
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

    @Override
    public void onCartAdded(LAData ladata) {
        if(ladata.getPurchase_SKU_ID()==null|| ladata.getPurchase_SKU_ID().isEmpty()){
            return;
        }
        Log.d(LOGTAG, "ComboID added to Cart :" + comboData.getCombo_ID());
        comboData.setCartCount(ladata.getCart_Count());
        dataSource.create(comboData);
        trackEvent("Cart", ladata.getPurchase_SKU_ID(), "");
        Log.e(LOGTAG, "cart number 0 :" + InkarneAppContext.getCartNumber());
        InkarneAppContext.incrementCartNumber(1);
        Log.e(LOGTAG, "cart number 1 :" + InkarneAppContext.getCartNumber());
        if(mListener!=null && mListener instanceof ShopActivity)
            ((ShopActivity)mListener).updateCartNumber();
        //ladata.setCombo_ID(comboData.getCombo_ID());
        //dataSource.create(ladata);

    }

    public void onBuyAdded(LAData ladata) {
        Log.d(LOGTAG, "ComboID added to Cart :" + comboData.getCombo_ID());
        trackEvent("Buy", ladata.getPurchase_SKU_ID(),"");
        //dataSource.create(ladata);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentLookLikeInteraction(Uri uri);
        void onFragmentLookLikeInteractionCartClicked();
    }


    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }
}
