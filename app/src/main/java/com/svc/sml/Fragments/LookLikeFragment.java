package com.svc.sml.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.percent.PercentFrameLayout;
import android.support.percent.PercentLayoutHelper;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.svc.sml.Activity.WebActivity;
import com.svc.sml.Adapter.LookaLikeProductAdapter;
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
import com.svc.sml.Utility.AWSUtil;
import com.svc.sml.Utility.ConstantsUtil;
import com.svc.sml.View.FmLayout;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LookLikeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LookLikeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LookLikeFragment extends BaseFragment implements View.OnClickListener,  LookaLikeProductAdapter.OnAdapterInteractionListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String LOGTAG = "LookLikeFragment";
    // TODO: Rename and change types of parameters

    private InkarneDataSource dataSource;
    private List<LAData> laDataList = new ArrayList<LAData>();
    private OnFragmentInteractionListener mListener;
    private ProgressBar pbSku;
    private ComboData comboData;

    private ArrayList<ClausalSkuModel> arrayTopItems = new ArrayList<>();
    private ArrayList<ClausalSkuModel> arrayAccItems = new ArrayList<>();
    private ClausalSkuModel itemClausalShoe = null;
    private ClausalSkuModel itemClausalBottom = null;
    private int skuSelectedIndex = 0;
    private List<String> arrayTopItemCategory = Arrays.asList("Top" ,"Jacket","Shirt","Dress","Jumpsuit");
    private List<String> arrayBottomItemCategory = Arrays.asList("Pants" ,"Skirt","Shorts","Jeans");
    private RelativeLayout topContainer;
    private FmLayout clausal;
    int widthView  = 0 ;

    private TransferUtility transferUtility;
    private List<TransferObserver> observers;
    private View.OnClickListener onSkuClickListener = null;
    //protected ImageFetcher imageFetcher;
    private int countTotalSku = 0;
    private TextView tvPrice = null;
    private TextView tvBrand = null ;
    //private ClausalSkuModel selectedClausal = null;
    //private SkuData selectedSkuData = null;
    private ClausalSkuModel selectedClausalModel = null;
    private Button btnBuy;
    private Button btnCart;
    private ImageLoader mImageLoader;
//    Clutches
//    Pants
//    Shoes
//    Jacket
//    Skirt
//    Bags
//    Belts
//    Necklace
//    Earrings
//    Shirt
//    Shorts
//    Dress
//    Sunglasses
//    Jeans
//    Jumpsuit
//    Top

    public ComboData getComboData() {
        return comboData;
    }

    public void setComboData(ComboData comboData) {
        skuSelectedIndex = 0;
        this.comboData = comboData;
        initData();
        populateClausal();
    }

    public LookLikeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LookLikeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LookLikeFragment newInstance(String param1, String param2) {
        LookLikeFragment fragment = new LookLikeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static LookLikeFragment newInstance(ComboData comboData) {
        LookLikeFragment fragment = new LookLikeFragment();
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
        transferUtility = AWSUtil.getTransferUtility(getActivity());
        mImageLoader = VolleyImageRequest.getInstance(getActivity())
                .getImageLoader();
        observers = new ArrayList<TransferObserver>();
        GATrackActivity(LOGTAG);
        //imageFetcher = new ImageFetcher(getActivity());

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
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_lookalike, container, false);
        dataSource = InkarneDataSource.getInstance(InkarneAppContext.getAppContext());
        dataSource.open();
        // fBtnCart = (FloatingActionButton) v.findViewById(R.id.f_btn_cart);
//        fBtnCart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mListener != null)
//                    mListener.onFragmentLookLikeInteractionCartClicked();
//            }
//        });
        //pbSimilarProducts = (ProgressBar) v.findViewById(R.id.pb_products);
        btnBuy = (Button)v.findViewById(R.id.btn_buy);
        btnCart = (Button)v.findViewById(R.id.btn_cart);

        btnBuy.setOnClickListener(this);
        btnCart.setOnClickListener(this);
        pbSku = (ProgressBar)v.findViewById(R.id.pb_sku);
        pbSku.setVisibility(View.VISIBLE);
        topContainer = (RelativeLayout)v.findViewById(R.id.con_top);
        clausal = (FmLayout)v.findViewById(R.id.clausal);
        tvPrice = (TextView)v.findViewById(R.id.tv_price);
        tvBrand = (TextView)v.findViewById(R.id.tv_brand);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        widthView = size.x;
        int height = size.y;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(widthView, widthView);
        //params.addRule(RelativeLayout.BELOW, btnBack.getId());
        //params.setMargins(5,20,5,0);
        //params.setMargins(5,5,5,5);
        //params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        clausal.setLayoutParams(params);
        clausal.requestLayout();
        //initClausalView();
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
       // showImages();
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopDownload();
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

    private void showImages(){
        for(ClausalSkuModel c : arrayTopItems){
            requestLAData(c,false);
        }
        for(ClausalSkuModel c : arrayAccItems){
            requestLAData(c,false);
        }
        if(itemClausalBottom != null) {
            requestLAData(itemClausalBottom,false);
        }
        if(itemClausalShoe != null) {
            requestLAData(itemClausalShoe,false);
        }
    }


    private void initAddClausalItem(SkuData sku){
        countTotalSku++;
        String category = sku.getmA_Category();
        ClausalSkuModel.EClausalType type = null;
        ClausalSkuModel c = null;
        if(arrayTopItemCategory.contains(category)){
            type = ClausalSkuModel.EClausalType.eClausalTypeTop;
             c = new ClausalSkuModel(type,sku);
            arrayTopItems.add(c);
        }else if (arrayBottomItemCategory.contains(category)){
            type = ClausalSkuModel.EClausalType.eClausalTypeBottom;
             c = new ClausalSkuModel(type,sku);
            itemClausalBottom = c;
        }else if(category.equals("Shoes")){
            type =  ClausalSkuModel.EClausalType.eClausalTypeShoes;
            c = new ClausalSkuModel(type,sku);
            itemClausalShoe = c;
        }else {
            type = ClausalSkuModel.EClausalType.eClausalTypeAcc;
            c = new ClausalSkuModel(type,sku);
            arrayAccItems.add(c);
        }
        //requestLAData(c,false);
    }

    public void resetClausalList(){
        countTotalSku = 0;
        arrayTopItems.clear();
        arrayAccItems.clear();
        itemClausalBottom = null;
        itemClausalShoe = null;
    }

    public void initData() {
        resetClausalList();
        SkuData sku = null;
        //listSkus = new ArrayList<SkuData>();
        if(comboData.getmA1_Category()!=null && !comboData.getmA1_Category().isEmpty()) {
            sku = new SkuData();
            sku.setmA_Category(comboData.getmA1_Category());
            sku.setmA_PIC_Png_Key_Name(comboData.getmA1_PIC_Png_Key_Name());
            sku.isSelected = false;
            sku.setmSKU_ID(comboData.getmSKU_ID1());

            //listSkus.add(sku);
            initAddClausalItem(sku);
        }
        if (comboData.getmA2_Category() != null && comboData.getmSKU_ID2() != null && comboData.getmSKU_ID2().length() != 0) {
            sku = new SkuData();
            sku.setmA_Category(comboData.getmA2_Category());
            sku.setmA_PIC_Png_Key_Name(comboData.getmA2_PIC_Png_Key_Name());
            sku.setmSKU_ID(comboData.getmSKU_ID2());
            sku.isSelected = false;
            initAddClausalItem(sku);
        }

        if (comboData.getmA3_Category() != null && comboData.getmSKU_ID3() != null && comboData.getmSKU_ID3().length() != 0) {
            sku = new SkuData();
            sku.setmA_Category(comboData.getmA3_Category());
            sku.setmA_PIC_Png_Key_Name(comboData.getmA3_PIC_Png_Key_Name());
            sku.setmSKU_ID(comboData.getmSKU_ID3());
            sku.isSelected = false;
            initAddClausalItem(sku);
        }

        if (comboData.getmSKU_ID4() != null && comboData.getmSKU_ID4().length() != 0) {
            sku = new SkuData();
            sku.setmA_Category(comboData.getmA4_Category());
            sku.setmA_PIC_Png_Key_Name(comboData.getmA4_PIC_Png_Key_Name());
            sku.setmSKU_ID(comboData.getmSKU_ID4());
            sku.isSelected = false;
            initAddClausalItem(sku);
        }

        if (comboData.getmA5_Category() != null && comboData.getmSKU_ID5() != null && comboData.getmSKU_ID5().length() != 0) {
            sku = new SkuData();
            sku.setmA_Category(comboData.getmA5_Category());
            sku.setmA_PIC_Png_Key_Name(comboData.getmA5_PIC_Png_Key_Name());
            sku.setmSKU_ID(comboData.getmSKU_ID5());
            sku.isSelected = false;
            initAddClausalItem(sku);
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
                initAddClausalItem(sku);
            } else if (comboData.getmA6_Category() != null && comboData.getmSKU_ID6() != null && comboData.getmSKU_ID6().length() != 0) {
                sku = new SkuData();
                sku.setmA_Category(comboData.getmA6_Category());
                sku.setmA_PIC_Png_Key_Name(comboData.getmA6_PIC_Png_Key_Name());
                sku.setmSKU_ID(comboData.getmSKU_ID6());
                sku.isSelected = false;
                initAddClausalItem(sku);
            }
        }

        if (comboData.getA71() != null && comboData.getA71().getObjId() != null && comboData.getA71().getObjId().length() != 0) {
            sku = new SkuData();
            //sku.setmA_Category(comboData.getmA7_Category());
            sku.setmA_Category(comboData.getA71().getAccessoryType());
            sku.setmA_PIC_Png_Key_Name(comboData.getA71().getThumbnailAwsKey());
            sku.setmSKU_ID(comboData.getA71().getObjId());
            sku.isSelected = false;
            initAddClausalItem(sku);
        } else if (comboData.getmA7_Category() != null && comboData.getmSKU_ID7() != null && comboData.getmSKU_ID7().length() != 0) {
            sku = new SkuData();
            sku.setmA_Category(comboData.getmA7_Category());
            sku.setmA_PIC_Png_Key_Name(comboData.getmA7_PIC_Png_Key_Name());
            sku.setmSKU_ID(comboData.getmSKU_ID7());
            sku.isSelected = false;
            initAddClausalItem(sku);
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
                initAddClausalItem(sku);
            } else if (comboData.getmA9_Category() != null && comboData.getmSKU_ID9() != null && comboData.getmSKU_ID9().length() != 0) {
                sku = new SkuData();
                sku.setmA_Category(comboData.getmA9_Category());
                sku.setmA_PIC_Png_Key_Name(comboData.getmA9_PIC_Png_Key_Name());
                sku.setmSKU_ID(comboData.getmSKU_ID9());
                sku.isSelected = false;
                initAddClausalItem(sku);
            }
        }
        if (!comboData.isA101Removed()) {
            if (comboData.getA101() != null && comboData.getA101().getObjId() != null && comboData.getA101().getObjId().length() != 0) {
                sku = new SkuData();
                //sku.setmA_Category(comboData.getmA10_Category());
                sku.setmA_Category(comboData.getA101().getAccessoryType());
                sku.setmA_PIC_Png_Key_Name(comboData.getA101().getThumbnailAwsKey());
                sku.setmSKU_ID(comboData.getA101().getObjId());
                sku.isSelected = false;
                initAddClausalItem(sku);
            } else if (comboData.getmA10_Category()!= null && comboData.getmSKU_ID10() != null && comboData.getmSKU_ID10().length() != 0) {
                sku = new SkuData();
                sku.setmA_Category(comboData.getmA10_Category());
                sku.setmA_PIC_Png_Key_Name(comboData.getmA10_PIC_Png_Key_Name());
                sku.setmSKU_ID(comboData.getmSKU_ID10());
                sku.isSelected = false;
                initAddClausalItem(sku);
            }
        }
    }

    private void initView() {
        if (arrayTopItems == null || arrayTopItems.size() == 0) {
            initData();
            clausal.removeAllViews();
            populateClausal();
            drawClausalView();
            showImages();
        }
        else { //if (arrayTopItems.size() != 0)
            clausal.removeAllViews();
            populateClausal();
            drawClausalView();
            showImages();
        }
    }

    void addClausalView(ClausalSkuModel c){
        if(c == null)
            return;
        NetworkImageView iv = new NetworkImageView(getActivity());
        ImageView iv2 = new ImageView(getActivity());
        PercentFrameLayout.LayoutParams params = new PercentFrameLayout.LayoutParams(100,100,c.gravity);
        iv.setLayoutParams(params);
        iv2.setLayoutParams(params);
        PercentLayoutHelper.PercentLayoutInfo info = params.getPercentLayoutInfo();
        iv.setTag(c.skuData.getmSKU_ID());
        iv.setOnClickListener(onSkuClickListener);
//        iv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                v.setBackgroundColor(Color.GREEN);
//
//            }
//        });

        info.heightPercent = (float)(c.hPercent/100.0);
        info.widthPercent  = (float)(c.wPercent/100.0);
        //int per =
        if(c.marginLeft!=0) {
            info.leftMarginPercent = (float)(c.marginLeft/100.0);
        }
        if(c.marginTop!=0) {
            info.topMarginPercent = (float)(c.marginTop/100.0);
        }

        if(c.marginRight!=0) {
            info.rightMarginPercent = (float)(c.marginRight/100.0);
        }
        if(c.marginBottom!=0) {
            info.bottomMarginPercent = (float)(c.marginBottom/100.0);
        }

        int leftPadding = (int) convertDpToPixel(16,getActivity());
        int topPadding = (int) convertDpToPixel(17,getActivity());
        int rightPadding = (int) convertDpToPixel(16,getActivity());
        int bottomPadding = (int) convertDpToPixel(17,getActivity());
        iv.setPadding(leftPadding, topPadding, rightPadding, bottomPadding);
        iv2.setPadding(leftPadding, topPadding, rightPadding, bottomPadding);
        ProgressBar pb = new ProgressBar(getActivity());
        iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
        iv.requestLayout();
        c.iv = iv;
        c.iv2 = iv2;
        //c.iv2.setBackgroundColor(R.color.transparent);
        //beginDownload(c.skuData,iv,null);
        clausal.addView(c.iv);
        clausal.addView(c.iv2);
        //imageFetcher.manageSetImage(c.skuData.getmSKU_ID(), c.skuData.getmA_PIC_Png_Key_Name(), c.iv, pb,0);
        c.iv.setBackgroundResource(R.drawable.bg_sku_item);
        c.iv.requestLayout();
//        requestLAData(c,false);
//        if(c.eClausalType== ClausalSkuModel.EClausalType.eClausalTypeTop && c.indexViewLayer ==0){//todo
//            skuClickHandler(iv);
//        }
    }

    private void drawClausalView(){
        for(ClausalSkuModel acc : arrayAccItems){
            addClausalView(acc);
        }
        addClausalView(itemClausalShoe);
        addClausalView(itemClausalBottom);
        ClausalSkuModel cTop = null;
        for(ClausalSkuModel acc : arrayTopItems){
            addClausalView(acc);
            cTop = acc;//TODO
//            if(acc.indexViewLayer == 0){
//                cTop = acc;
//            }
        }
        if(cTop !=null) {
            final ClausalSkuModel cTop1 = cTop;
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    if (cTop1 != null)
                        skuClickHandler(cTop1.iv,false);
                }
            });
        }
        pbSku.setVisibility(View.INVISIBLE);
    }


    private void skuClickHandler(final NetworkImageView v,boolean shouldLaunchBuy){
        String skuId = (String)v.getTag();
        //selectedSkuData = skuData;
        for(ClausalSkuModel c : arrayTopItems){
            c.iv.setBackgroundResource(R.drawable.bg_sku_item);
            if(c.skuData.getmSKU_ID().equals(skuId)){
                selectedClausalModel = c;
            }
            //c.iv.requestLayout();
        }
        for(ClausalSkuModel c : arrayAccItems){
            c.iv.setBackgroundResource(R.drawable.bg_sku_item);
            if(c.skuData.getmSKU_ID().equals(skuId)){
                selectedClausalModel = c;
            }
        }
        if(itemClausalBottom != null) {
            itemClausalBottom.iv.setBackgroundResource(R.drawable.bg_sku_item);
            if(itemClausalBottom.skuData.getmSKU_ID().equals(skuId)){
                selectedClausalModel = itemClausalBottom;
            }
        }
        int t = new Random().nextInt(2);
        if(itemClausalShoe != null) {
            itemClausalShoe.iv.setBackgroundResource(R.drawable.bg_sku_item);
            if(itemClausalShoe.skuData.getmSKU_ID().equals(skuId)){
                selectedClausalModel = itemClausalShoe;
            }
        }
        requestLAData(selectedClausalModel,true);
        v.setBackgroundResource(R.drawable.bg_sku_selected);
    }

    private void showProductInfo(LAData ladata,boolean launchBuy){
        if (ladata.getCart_Count() > 0 || ladata.getUser_Cart_Flag() != 0){
            btnCart.setEnabled(false);
            if(getContext()!= null)
                btnCart.setTextColor(ContextCompat.getColor(getContext(), R.color.tcolor_yellow));
        }else{
            btnCart.setEnabled(true);
            if(getContext()!= null)
            btnCart.setTextColor(ContextCompat.getColor(getContext(), R.color.tcolor_green));
        }
        tvPrice.setText("\u20B9" + " " + ladata.getPrice());
        //tvPrice.setText(ladata.getPrice());
        tvBrand.setText(ladata.getBrand());
    }
    //volley

    public void requestLAData(final ClausalSkuModel CModel,final boolean onclick) {
        if(CModel == null || CModel.skuData == null)
            return;
        trackEvent("lookalike",CModel.skuData.getmSKU_ID(),"");
        String uri = ConstantsUtil.URL_BASEPATH + ConstantsUtil.URL_METHOD_LOOKALIKE + User.getInstance().getmUserId() + "/" + comboData.getCombo_ID() + "/" + CModel.skuData.getmSKU_ID();
        laDataList = CModel.skuData.getLaDataList();
        if (laDataList != null && laDataList.size() != 0) {
            Log.w(LOGTAG, "already loaded:" + uri);
            loadImage(CModel);
            if (CModel.skuData.getmSKU_ID().equals(selectedClausalModel.skuData.getmSKU_ID())) {
                showProductInfo(laDataList.get(0), onclick);
            }
            return;
        }
        pbSku.setVisibility(View.VISIBLE);
        Log.w(LOGTAG, "URI :" + uri);
        final GsonRequest gsonRequest = new GsonRequest(uri, LAData.LADataWrapper.class, null, new Response.Listener<LAData.LADataWrapper>() {

            @Override
            public void onResponse(LAData.LADataWrapper ladatas) {
                pbSku.setVisibility(View.INVISIBLE);
                laDataList = ladatas.getLaDatas();
                if (laDataList.size() > 0) {
                    CModel.skuData.setLaDataList(laDataList);
                    CModel.skuData.laData = laDataList.get(0);
                    Log.w(LOGTAG, "onResponse :" );
                    loadImage(CModel);
                    if (CModel.skuData != null && selectedClausalModel != null && CModel.skuData.getmSKU_ID().equals(selectedClausalModel.skuData.getmSKU_ID())) {

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
                pbSku.setVisibility(View.INVISIBLE);
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


    private void populateClausal(){
        int countGar = arrayTopItems.size();
        if(itemClausalBottom != null)
            countGar += 1;
        if(User.getInstance().isMale()){
            if(countGar == 2){
                populateMaleClausalGarment2();
            }
            else { //if(countGar == 3){
                populateMaleClausalGarment3();
            }
        }else{
            if(countGar == 1){
                populateFemaleClausalGarment1();
            }
            else if(countGar == 2) {
                populateFemaleClausalGarment2();
            }
            else{
                populateFemaleClausalGarment3();
            }
        }
    }

    private ClausalSkuModel populateClausalDimension(ClausalSkuModel c , int wp, int hp, int top, int left, int bottom, int right){
        if(c == null)
            return null;
        ClausalSkuModel temp = c;
        temp.wPercent = wp;
        temp.hPercent = hp;
        int g = Gravity.TOP;
        if(top != 0){
            temp.marginTop = top;
            if(left != 0){
                g = Gravity.TOP|Gravity.LEFT;
                temp.marginLeft = left;
            }
            else if(right != 0){
                g = Gravity.TOP|Gravity.RIGHT;
                temp.marginRight = right;
            }
        }
        else if(bottom != 0){
            temp.marginBottom = bottom;
            if(left != 0){
                g = Gravity.BOTTOM|Gravity.LEFT;
                temp.marginLeft = left;
            }
            else if(right != 0){
                g = Gravity.BOTTOM|Gravity.RIGHT;
                temp.marginRight = right;
            }
        }
        temp.gravity = g;
        return temp;
    }

    private void populateTopClausalDimension(int index , int wp, int hp, int top, int left, int bottom, int right){
        if(index >= arrayTopItems.size())
            return;
        ClausalSkuModel temp = arrayTopItems.get(index);
        temp = populateClausalDimension(temp,wp,hp,top,left,bottom,right);
        temp.indexViewLayer = 0;
        arrayTopItems.set(index,temp);
    }

    private void populateTopClausalDimension(int index , int zIndex, int wp, int hp, int top, int left, int bottom, int right){
        if(index >= arrayTopItems.size())
            return;
        ClausalSkuModel temp = arrayTopItems.get(index);
        temp = populateClausalDimension(temp,wp,hp,top,left,bottom,right);
        temp.indexViewLayer = zIndex;
        arrayTopItems.set(index,temp);
    }
    private void populateAccClausalDimension(int index , int wp, int hp, int top, int left, int bottom, int right){
        if(index >= arrayAccItems.size())
            return;
        ClausalSkuModel temp = arrayAccItems.get(index);
        temp = populateClausalDimension(temp,wp,hp,top,left,bottom,right);
        arrayAccItems.set(index,temp);
    }

    /****** MALE *******/
    /* Male garment count 2 */
    private void populateMaleClausalGarment2(){
        int wGar = 5*100/12;
        int hGar = 55;
        if(itemClausalBottom != null) {
            itemClausalBottom = populateClausalDimension(itemClausalBottom, wGar, hGar, 0, 2, 2, 0);
            populateTopClausalDimension(0,0, wGar, hGar, 2, 50 - wGar / 2, 0, 0);
        }
        else {
            /*not accessed*/
            populateTopClausalDimension(0,0, wGar, hGar, 2, 50 - wGar / 2, 0, 0);
            populateTopClausalDimension(1,0, wGar, hGar, 0, 2, 2, 0);
        }

        int wShoe = 4*100/12;
        int hShoe = 35;
        if(countTotalSku == 3){
            int topS = 50;
            int rightS = 2;
            itemClausalShoe = populateClausalDimension(itemClausalShoe,wShoe,hShoe,topS,0,0,rightS);
        }
        else if(countTotalSku == 4){
            int topS = 50;
            int rightS = 2;
            int topAcc1 = 10;
            int rightAcc1 = 2;
            itemClausalShoe = populateClausalDimension(itemClausalShoe,wShoe,hShoe,topS,0,0,rightS);
            populateAccClausalDimension(0,wShoe,hShoe,topAcc1,0,0,rightAcc1);

        }
        else if(countTotalSku == 5){
            int wAcc = 4*100/12;
            int hAcc = 30;
            itemClausalShoe = populateClausalDimension(itemClausalShoe,wShoe,hShoe,50,0,0,2);
            populateAccClausalDimension(0,wAcc,hAcc,10,2,0,0);
            populateAccClausalDimension(1,wAcc,hAcc,10,0,0,2);
        }
        else if(countTotalSku >= 6){
            itemClausalShoe = populateClausalDimension(itemClausalShoe,wShoe,hShoe,53,0,0,15);
            int wAcc = 4*100/12;
            int hAcc = 30;
            populateAccClausalDimension(0,wAcc,hAcc,10,2,0,0);
            populateAccClausalDimension(1,wAcc,hAcc,30,0,0,2);
            populateAccClausalDimension(2,wAcc,hAcc,5,0,0,2);
        }
    }

    /* Male garment count 3 */
    private void populateMaleClausalGarment3(){
        int countTop = arrayTopItems.size();
        int countAcc = arrayAccItems.size();
        int wGar = 5*100/12;
        int hGar = 55;
        int wGar1 = 4*100/12;
        int hGar1 = 45;
        if(itemClausalBottom != null) {
            itemClausalBottom = populateClausalDimension(itemClausalBottom, wGar1, hGar1, 0, 2, 2, 0);
            populateTopClausalDimension(0, wGar, hGar, 15, 50 - wGar/2, 0, 0);
            populateTopClausalDimension(1, wGar1, hGar1, 2, 0, 0, 2);
        }
        else{
            /*not accessed*/
            populateTopClausalDimension(0, wGar, hGar, 2, 50 - wGar/2, 0, 0);
            populateTopClausalDimension(1, wGar, hGar, 0, 2, 2, 0);
            populateTopClausalDimension(2, wGar, hGar, 0, 2, 2, 0);
        }

        int wShoe = 4*100/12;
        int hShoe = 35;
        if(countTotalSku == 4){
            int topS = 60;
            int rightS = 2;
            itemClausalShoe = populateClausalDimension(itemClausalShoe,wShoe,hShoe,topS,0,0,rightS);
        }
        else if(countTotalSku == 5){
            int topS = 60;
            int rightS = 2;
            itemClausalShoe = populateClausalDimension(itemClausalShoe,wShoe,hShoe,topS,0,0,rightS);
            populateAccClausalDimension(0,wShoe,hShoe,2,2,0,0);

        }
        else if(countTotalSku == 6){
            wShoe = 3*100/12;
            hShoe = 30;
            int wAcc = 4*100/12;
            int hAcc = 35;
            itemClausalShoe = populateClausalDimension(itemClausalShoe,wShoe,hShoe, 0,35,2,0);
            populateAccClausalDimension(0,wAcc,hAcc, 0,0,2,2);
            populateAccClausalDimension(1,wAcc,hAcc, 2,2,0,0);
        }
        else if(countTotalSku >= 7){
            wShoe = 3*100/12;
            hShoe = 30;
            int wAcc = 4*100/12;
            int hAcc = 35;
            int wAcc1 = 4*100/12;
            int hAcc1 = 25;
            itemClausalShoe = populateClausalDimension(itemClausalShoe,wShoe,hShoe, 0,35,2,0);
            populateAccClausalDimension(0,wAcc,hAcc, 0,0,2,2);
            populateAccClausalDimension(1,wAcc1,hAcc1, 2,2,0,2);
            populateAccClausalDimension(2,wAcc1,hAcc1, 28,2,0,2);
        }
    }


    /****** FEMALE ********/
    /* Female garment count 1 */
    private void populateFemaleClausalGarment1(){
        if(countTotalSku == 2){
            int wShoe = 5*100/12;
            int hShoe = 40;
            int bottomS = 2;
            int rightS = 2;
            itemClausalShoe = populateClausalDimension(itemClausalShoe,wShoe,hShoe,0,0,bottomS,rightS);

            int wTop = 7*100/12;
            int hTop = 75;
            populateTopClausalDimension(0, wTop, hTop, 2, 2, 0, 0);
        }

        if(countTotalSku == 3){
            int wShoe = 5*100/12;
            int hShoe = 40;
            int bottomS = 2;
            int rightS = 2;
            itemClausalShoe = populateClausalDimension(itemClausalShoe,wShoe,hShoe,0,0,bottomS,rightS);

            int wTop = 7*100/12;
            int hTop = 75;
            populateTopClausalDimension(0, wTop, hTop, 2, 2, 0, 0);

            int wAcc1 = 5*100/12;
            int hAcc1 = 40;
            populateAccClausalDimension(0,wAcc1,hAcc1,10,0,0,2);

        }
        else if(countTotalSku == 4){
            int wShoe = 4*100/12;
            int hShoe = 35;
            int leftS = 50- wShoe/10;
            int bottomS = 2;
            itemClausalShoe = populateClausalDimension(itemClausalShoe,wShoe,hShoe,0,leftS,bottomS,0);

            int wTop = 7*100/12;
            int hTop = 65;
            populateTopClausalDimension(0, wTop, hTop, 2, 2, 0, 0);

            int wAcc1 = 4*100/12;
            int hAcc1 = 30;
            populateAccClausalDimension(0,wAcc1,hAcc1,4,0,0,7);
            populateAccClausalDimension(1,wAcc1,hAcc1,35,0,0,7);

        }
        else if(countTotalSku == 5){
            int wShoe = 4*100/12;
            int hShoe = 35;
            int leftS = 50- wShoe/10;
            int bottomS = 2;
            itemClausalShoe = populateClausalDimension(itemClausalShoe,wShoe,hShoe,0,leftS,bottomS,0);

            int wTop = 7*100/12;
            int hTop = 65;
            populateTopClausalDimension(0, wTop, hTop, 2, 2, 0, 0);

            int wAcc1 = 4*100/12;
            int hAcc1 = 30;
            populateAccClausalDimension(0,wAcc1,hAcc1,4,0,0,7);
            populateAccClausalDimension(1,wAcc1,hAcc1,35,0,0,7);

            int wAcc2 = 4*100/12;
            int hAcc2 = 34;
            populateAccClausalDimension(2,wAcc2,hAcc2,0,8,2,0);
        }
        else if(countTotalSku == 6){
            int wShoe = 3*100/12;
            int hShoe = 30;
            int bottomS = 2;
            int leftS = 50- wShoe/2;
            itemClausalShoe = populateClausalDimension(itemClausalShoe,wShoe,hShoe,0,leftS,bottomS,0);

            int wTop = 6*100/12;
            int hTop = 65;
            int topGar = 50 - hTop/2;
            int leftGar = 50 - wTop*7/10;
            populateTopClausalDimension(0, wTop, hTop,topGar, leftGar, 0, 0);

            int wAcc1 = 3*100/12;
            int hAcc1 = 30;
            populateAccClausalDimension(0,wAcc1,hAcc1,2,0,0,2);
            populateAccClausalDimension(1,wAcc1,hAcc1,2,2,0,0);

            populateAccClausalDimension(2,wAcc1,hAcc1,35,2,0,0);
            populateAccClausalDimension(3,wAcc1,hAcc1,35,0,0,2);
        }

        else if(countTotalSku >= 7){
            int wShoe = 3*100/12;
            int hShoe = 30;
            int bottomS = 2;
            int leftS = 50- wShoe/3;
            itemClausalShoe = populateClausalDimension(itemClausalShoe,wShoe,hShoe,0,leftS,bottomS,0);

            int wTop = 6*100/12;
            int hTop = 65;
            int topGar = 50 - hTop*7/10;
            int leftGar = 50 - wTop/2;
            populateTopClausalDimension(0, wTop, hTop,topGar, leftGar, 0, 0);

            int wAcc1 = 3*100/12;
            int hAcc1 = 30;
            populateAccClausalDimension(0,wAcc1,hAcc1,2,0,0,2);
            populateAccClausalDimension(1,wAcc1,hAcc1,2,2,0,0);

            populateAccClausalDimension(2,wAcc1,hAcc1,35,2,0,0);
            populateAccClausalDimension(3,wAcc1,hAcc1,35,0,0,2);
            populateAccClausalDimension(4,wAcc1,hAcc1,0,50- (int)(wAcc1*130.0/100),2,0);
        }
    }


    private void populateFemaleClausalGarment2(){
        if(countTotalSku == 3){
            int wShoe = 4*100/12;
            int hShoe = 35;
            itemClausalShoe = populateClausalDimension(itemClausalShoe,wShoe,hShoe,63,60,0,0);

            int wTop = 6*100/12;
            int hTop = 65;
            if(itemClausalBottom != null) {
                populateTopClausalDimension(0, wTop, hTop, 2, 0, 0, 2);
                itemClausalBottom = populateClausalDimension(itemClausalBottom, 6*100/12, 60, 0, 2, 2, 0);
            }
            else {
            /*not accessed*/
                populateTopClausalDimension(0, wTop, hTop, 2, 0, 0, 2);
                populateTopClausalDimension(1, 6*100/12, 60, 0, 2, 2, 0);
            }
        }

        if(countTotalSku == 4){
            int wShoe = 4*100/12;
            int hShoe = 35;
            itemClausalShoe = populateClausalDimension(itemClausalShoe,wShoe,hShoe,50,0,0,2);

            int wTop = 5*100/12;
            int hTop = 55;
            if(itemClausalBottom != null) {
                populateTopClausalDimension(0, wTop, hTop, 2, 50 -wTop/2, 0, 0);
                itemClausalBottom = populateClausalDimension(itemClausalBottom, wTop, hTop, 0, 2, 2, 0);
            }
            else {
            /*not accessed*/
                populateTopClausalDimension(0, wTop, hTop, 2, 50 -wTop/2, 0, 0);
                populateTopClausalDimension(1, wTop, hTop, 0, 2, 2, 0);
            }

            int wAcc1 = 4*100/12;
            int hAcc1 = 35;
            populateAccClausalDimension(0,wAcc1,hAcc1,10,0,0,2);

        }
        else if(countTotalSku == 5){
            int wShoe = 4*100/12;
            int hShoe = 35;
            itemClausalShoe = populateClausalDimension(itemClausalShoe,wShoe,hShoe,50,0,0,2);

            int wTop = 5*100/12;
            int hTop = 55;
            if(itemClausalBottom != null) {
                populateTopClausalDimension(0, wTop, hTop, 2, 50 -wTop/2, 0, 0);
                itemClausalBottom = populateClausalDimension(itemClausalBottom, wTop, hTop, 0, 2, 2, 0);
            }
            else {
            /*not accessed*/
                populateTopClausalDimension(0, wTop, hTop, 2, 050 -wTop/2, 0, 0);
                populateTopClausalDimension(1, wTop, hTop, 0, 2, 2, 0);
            }

            int wAcc1 = 4*100/12;
            int hAcc1 = 35;
            populateAccClausalDimension(0,wAcc1,hAcc1,10,0,0,2);
            populateAccClausalDimension(1,wAcc1,hAcc1,5,2,0,0);

        }
        else if(countTotalSku == 6){
            int wShoe = 3*100/12;
            int hShoe = 35;
            itemClausalShoe = populateClausalDimension(itemClausalShoe,wShoe,hShoe,0,47,2,0);

            int wTop = 5*100/12;
            int hTop = 65;
            if(itemClausalBottom != null) {
                populateTopClausalDimension(0,5, wTop, hTop, 2, 50 -wTop*4/10, 0, 0);
                itemClausalBottom = populateClausalDimension(itemClausalBottom, wTop, 55, 0, 2, 2, 0);
            }
            else {
            /*not accessed*/
                populateTopClausalDimension(0, wTop, hTop, 2, 50 -wTop*4/10, 0, 0);
                populateTopClausalDimension(1, wTop, 55, 0, 2, 2, 0);
            }

            int wAcc1 = 3*100/12;
            int hAcc1 = 30;
            populateAccClausalDimension(0,wAcc1,hAcc1,5,0,0,2);
            populateAccClausalDimension(1,wAcc1,hAcc1,35,0,0,2);
            int wAcc2 = 4*100/12;
            int hAcc2 = 35;
            populateAccClausalDimension(2,wAcc2,hAcc2,5,2,0,0);
        }
        else if(countTotalSku >= 7){
            int wShoe = 3*100/12;
            int hShoe = 35;
            itemClausalShoe = populateClausalDimension(itemClausalShoe,wShoe,hShoe,0,0,2,2);

            int wTop = 5*100/12;
            int hTop = 65;
            if(itemClausalBottom != null) {
                populateTopClausalDimension(0,5, wTop, hTop, 2, 50 -wTop*4/10, 0, 0);
                itemClausalBottom = populateClausalDimension(itemClausalBottom, wTop, 55, 0, 2, 2, 0);
            }
            else {
            /*not accessed*/
                populateTopClausalDimension(0, wTop, hTop, 2, 50 -wTop*4/10, 0, 0);
                populateTopClausalDimension(1, wTop, 55, 0, 2, 2, 0);
            }

            int wAcc1 = 3*100/12;
            int hAcc1 = 30;
            populateAccClausalDimension(0,wAcc1,hAcc1,5,0,0,2);
            populateAccClausalDimension(1,wAcc1,hAcc1,35,0,0,2);
            int wAcc2 = 4*100/12;
            int hAcc2 = 35;
            populateAccClausalDimension(2,wAcc2,hAcc2,5,2,0,0);
            populateAccClausalDimension(3,wAcc1,hAcc2,0,47,2,0);////
        }
    }


    private void populateFemaleClausalGarment3(){
        if(countTotalSku == 4){
            int wShoe = 4*100/12;
            int hShoe = 35;
            itemClausalShoe = populateClausalDimension(itemClausalShoe,wShoe,hShoe,0,0,2,2);

            int wTop = 5*100/12;
            int hTop = 55;
            if(itemClausalBottom != null) {
                populateTopClausalDimension(0, wTop, hTop, 2, 0, 0, 2);
                populateTopClausalDimension(1, 2,wTop, hTop, 50-hTop/2,50- wTop/2, 0, 0);
                itemClausalBottom = populateClausalDimension(itemClausalBottom, wTop, hTop, 0, 2, 2, 0);
            }
            else {
            /*not accessed*/
                populateTopClausalDimension(0, wTop, hTop, 2, 0, 0, 2);
                populateTopClausalDimension(1, 2,wTop, hTop,50-hTop/2,50- wTop/2, 0, 0);
                populateTopClausalDimension(2, wTop, hTop, 0, 2, 2, 0);
            }
        }

        if(countTotalSku == 5){
            int wShoe = 4*100/12;
            int hShoe = 35;
            itemClausalShoe = populateClausalDimension(itemClausalShoe,wShoe,hShoe,0,0,2,2);

            int wTop = 5*100/12;
            int hTop = 55;
            if(itemClausalBottom != null) {
                populateTopClausalDimension(0, wTop, hTop, 2, 0, 0, 2);
                populateTopClausalDimension(1, 2,wTop, hTop, 50- hTop/2, 50-wTop/2, 0, 0);
                itemClausalBottom = populateClausalDimension(itemClausalBottom, wTop, hTop, 0, 2, 2, 0);
            }
            else {
            /*not accessed*/
                populateTopClausalDimension(0, wTop, hTop, 2, 0, 0, 2);
                populateTopClausalDimension(1, 2,wTop, hTop, 50- hTop/2, 50-wTop/2, 0, 0);
                populateTopClausalDimension(2, wTop, hTop, 0, 2, 2, 0);
            }
            int wAcc1 = 4*100/12;
            int hAcc1 = 35;
            populateAccClausalDimension(0,wAcc1,hAcc1,2,2,0,0);
        }
        else if(countTotalSku == 6){
            int wShoe = 4*100/12;
            int hShoe = 30;
            itemClausalShoe = populateClausalDimension(itemClausalShoe,wShoe,hShoe,0,44,2,0);

            int wTop = 5*100/12;
            int hTop = 55;
            if(itemClausalBottom != null) {
                populateTopClausalDimension(0, wTop, hTop, 2, 0, 0, 2);
                populateTopClausalDimension(1, 2,wTop, hTop, 50-hTop*68/100, 50-wTop/2, 0, 0);
                itemClausalBottom = populateClausalDimension(itemClausalBottom, wTop, hTop, 0, 2, 2, 0);
            }
            else {
            /*not accessed*/
                populateTopClausalDimension(0, wTop, hTop, 2, 0, 0, 2);
                populateTopClausalDimension(1, 2,wTop, hTop, 50-hTop*68/100, 50-wTop/2, 0, 0);
                populateTopClausalDimension(2, wTop, hTop, 0, 2, 2, 0);
            }
            Log.e(LOGTAG,"wtop"+ wTop  + "  "+ hTop);
            int wAcc1 = 4*100/12;
            int hAcc1 = 30;
            int wAcc2 = 3*100/12;
            int hAcc2 = 30;
            populateAccClausalDimension(0,wAcc1,hAcc1,2,2,0,0);
            populateAccClausalDimension(1,wAcc2,hAcc2,0,0,14,2);

        }
        else if(countTotalSku == 7){
            int wShoe = 3*100/12;
            int hShoe = 25;
            itemClausalShoe = populateClausalDimension(itemClausalShoe,wShoe,hShoe,0,50,2,0);

            int wTop = 5*100/12;
            int hTop = 55;
            if(itemClausalBottom != null) {
                populateTopClausalDimension(0, wTop, hTop, 2, 0, 0, 2);
                populateTopClausalDimension(1, 2,wTop, hTop, 50- hTop*60/100, 50-wTop*60/100, 0, 0);
                itemClausalBottom = populateClausalDimension(itemClausalBottom, 4*100/12, 45, 0, 2, 2, 0);
            }
            else {
            /*not accessed*/
                populateTopClausalDimension(0, wTop, hTop, 2, 0, 0, 2);
                populateTopClausalDimension(1, 2,wTop, hTop, 50- hTop*60/100, 50-wTop*60/100, 0, 0);
                populateTopClausalDimension(2, 4*100/12, 45, 0, 2, 2, 0);
            }
            int wAcc1 = 3*100/12;
            int hAcc1 = 25;
            populateAccClausalDimension(0,wAcc1,hAcc1,2,2,0,0);
            populateAccClausalDimension(1,wAcc1,hAcc1,28,2,0,0);
            populateAccClausalDimension(2,wAcc1,hAcc1,0,0,18,2);
        }
        else if(countTotalSku >= 8){
            int wShoe = 3*100/12;
            int hShoe = 25;
            itemClausalShoe = populateClausalDimension(itemClausalShoe,wShoe,hShoe,0,50,2,0);

            int wTop = 5*100/12;
            int hTop = 50;
            if(itemClausalBottom != null) {
                populateTopClausalDimension(0, wTop, hTop, 2, 0, 0, 2);
                populateTopClausalDimension(1, 2,wTop, hTop, 50- hTop*60/100, 50-wTop*60/100, 0, 0);
                itemClausalBottom = populateClausalDimension(itemClausalBottom, 4*100/12, 45, 0, 2, 2, 0);
            }
            else {
            /*not accessed*/
                populateTopClausalDimension(0, wTop, hTop, 2, 0, 0, 2);
                populateTopClausalDimension(1, 2,wTop, hTop, 50-hTop*60/100, 50-wTop*60/100, 0, 0);
                populateTopClausalDimension(2, 4*100/12, 45, 0, 2, 2, 0);
            }
            int wAcc1 = 3*100/12;
            int hAcc1 = 25;
            populateAccClausalDimension(0,wAcc1,hAcc1,2,2,0,0);
            populateAccClausalDimension(1,wAcc1,hAcc1,28,2,0,0);
            populateAccClausalDimension(2,wAcc1,hAcc1,48,0,0,2);
            populateAccClausalDimension(3,wAcc1,hAcc1,0,0,2,2);
        }
    }
    /*************/

    private void beginDownload(SkuData skuData,ImageView iv,ProgressBar pb) {
        if (skuData.getmA_PIC_Png_Key_Name() == null) {
            Log.e(LOGTAG, "Clausal key is null");
            if(pb != null){
                pb.setIndeterminate(false);
                pb.setVisibility(View.INVISIBLE);
            }
            return;
        }
        String key = skuData.getmA_PIC_Png_Key_Name();
        File file = new File(ConstantsUtil.FILE_PATH_APP_ROOT + key);
        Log.d(LOGTAG, " To be downloaded. file:  "+file);
        skuData.setPngDownloadPath(file.getAbsolutePath());
        TransferObserver observer = transferUtility.download(ConstantsUtil.AWSBucketName, key, file);
        observer.setTransferListener(new DownloadListener(iv, pb, skuData));
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

    @Override
    public void onClick(View v) {
        if(selectedClausalModel.skuData==null || selectedClausalModel.skuData.laData==null)
            return;
        switch (v.getId()){
            case R.id.btn_buy:{
                openBuyUrl(selectedClausalModel.skuData.laData);
            }
            break;
            case R.id.btn_cart:{
                addToCart(selectedClausalModel.skuData.laData);
            }
            break;
            default:
                break;
        }
    }

    /*
     * A TransferListener class that can listen to a download task and be
     * notified when the status changes.
     */
    private class DownloadListener implements TransferListener {
        private WeakReference<ImageView> ivWeakReference = null;
        private  WeakReference<ProgressBar> pbWeakReference = null;
        private SkuData skuData;

        public DownloadListener(ImageView iv,ProgressBar pb, SkuData skuData){
            this.ivWeakReference = new WeakReference<ImageView>(iv);
            this.pbWeakReference = new WeakReference<ProgressBar>(pb);
            this.skuData = skuData;
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
                Log.e(LOGTAG, "Clausal Download failed");
                if(pbWeakReference != null){
                    ProgressBar pb = this.pbWeakReference.get();
                    if(pb != null){
                        pb.setIndeterminate(false);
                        pb.setVisibility(View.INVISIBLE);
                    }
                }
            }
            else if (state == TransferState.COMPLETED){
                Log.d(LOGTAG, "Clausal Download success :"+ skuData.getPngDownloadPath());
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bm = BitmapFactory.decodeFile(skuData.getPngDownloadPath(), options);
                if(bm != null){
                    skuData.isImageDownloaded = true;
                }
                if(ivWeakReference != null) {
                    ImageView iv = ivWeakReference.get();
                    if (iv != null) {
                        iv.setImageBitmap(bm);
                    }
                }
            }
        }
    }


    /**************** CLAUSAL VIEW *********************/

    private void addToCart(final LAData ladata) {
        if(ladata.getPurchase_SKU_ID()==null|| ladata.getPurchase_SKU_ID().isEmpty()||ladata.getUser_Cart_Flag()!= 0){
            return;
        }
        btnCart.setTextColor(ContextCompat.getColor(getContext(), R.color.tcolor_yellow));
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
