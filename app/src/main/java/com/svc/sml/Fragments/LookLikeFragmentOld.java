package com.svc.sml.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.svc.sml.Activity.WebActivity;
import com.svc.sml.Adapter.LookaLikeProductAdapter;
import com.svc.sml.Adapter.LookaLikeSkuAdapter;
import com.svc.sml.Database.ComboData;
import com.svc.sml.Database.InkarneDataSource;
import com.svc.sml.Database.LAData;
import com.svc.sml.Database.SkuData;
import com.svc.sml.Database.User;
import com.svc.sml.Helper.DataManager;
import com.svc.sml.Helper.GsonRequest;
import com.svc.sml.Helper.VolleyHelper;
import com.svc.sml.Utility.HorizontalListView;
import com.svc.sml.InkarneAppContext;
import com.svc.sml.R;
import com.svc.sml.ShopActivity;
import com.svc.sml.Utility.ConstantsUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LookLikeFragmentOld.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LookLikeFragmentOld#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LookLikeFragmentOld extends BaseFragment implements LookaLikeProductAdapter.OnAdapterInteractionListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String LOGTAG = "LookLikeFragment";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private Spinner spinner;
    private HorizontalListView hlvSku;
    //private RecyclerView listView;
    private ListView listView;

    private InkarneDataSource dataSource;
    private List<LAData> laDataList = new ArrayList<LAData>();
    private List<SkuData> listSkus;
    private LookaLikeProductAdapter productAapater;
    private LookaLikeSkuAdapter skuAdapter;
    private OnFragmentInteractionListener mListener;
    private ProgressBar pbSku;
    private ProgressBar pbSimilarProducts;
    private ComboData comboData;
    private FloatingActionButton fBtnCart;

    private int skuSelectedIndex = 0;

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
    }

    public LookLikeFragmentOld() {
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
    public static LookLikeFragmentOld newInstance(String param1, String param2) {
        LookLikeFragmentOld fragment = new LookLikeFragmentOld();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static LookLikeFragmentOld newInstance(ComboData comboData) {
        LookLikeFragmentOld fragment = new LookLikeFragmentOld();
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
        GATrackActivity(LOGTAG);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_lookalike_old, container, false);
        dataSource = InkarneDataSource.getInstance(InkarneAppContext.getAppContext());
        dataSource.open();
        pbSimilarProducts = (ProgressBar) v.findViewById(R.id.pb_products);
        fBtnCart = (FloatingActionButton) v.findViewById(R.id.f_btn_cart);
        fBtnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null)
                    mListener.onFragmentLookLikeInteractionCartClicked();
            }
        });
        listView = (ListView) v.findViewById(R.id.lv_similar_products);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("debug", "listview item clicked :" + position);
                LAData laData = laDataList.get(position);
                openBuyUrl(laData);
            }
        });

        hlvSku = (HorizontalListView) v.findViewById(R.id.hlvSKUList);

        hlvSku.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                skuSelectedIndex = position;
                SkuData selectedSku = listSkus.get(position);
                for (SkuData s : listSkus) {
                    s.isSelected = false;
                }
                selectedSku.isSelected = true;
                skuAdapter.notifyDataSetChanged();
                spinner.setSelection(selectedSku.indexShortingSelection);
                requestLAData(skuSelectedIndex);
                Log.d("debug", "Horizontallistview item clicked :" + position);
            }
        });

        spinner = (Spinner) v.findViewById(R.id.s_lookalike);
        ArrayAdapter<String> aspin = new ArrayAdapter<String>(getActivity(), R.layout.lookalike_spinner_sorting_dropdown_item, ConstantsUtil.arrayProductSortByOption); //the adapter for the Spinner
        //aspin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        aspin.setDropDownViewResource(R.layout.lookalike_spinner_sorting_dropdown_item);
        spinner.getBackground().setColorFilter(ContextCompat.getColor(getActivity(), R.color.textColorSecondry), PorterDuff.Mode.SRC_ATOP);
        spinner.setAdapter(aspin);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (laDataList != null && laDataList.size() > 0) {
                    SkuData selectedSku = listSkus.get(skuSelectedIndex);
                    selectedSku.indexShortingSelection = position;
                    laDataList = shortedList(position);
                    productAapater.updateLaDataList(laDataList);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        productAapater = new LookaLikeProductAdapter(getActivity(), (ArrayList<LAData>) laDataList, comboData);
        productAapater.setListener(this);

        /*
        productAapater.setListener(new LookaLikeProductAdapter.OnAccessoryAdapterListener() {
            @Override
            public void onCartAdded() {

            }
        });
        */
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
    }

    @Override
    public void onStop() {
        super.onStop();
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

    //adapter interaction listener
    @Override
    public void onCartAdded(LAData ladata) {
        Log.d(LOGTAG, "ComboID added to Cart :" + comboData.getCombo_ID());
        comboData.setCartCount(ladata.getCart_Count());
        dataSource.create(comboData);
        trackEvent("Cart", ladata.getPurchase_SKU_ID(), "");
        Log.e(LOGTAG, "cart number 0 :" + InkarneAppContext.getCartNumber());
        InkarneAppContext.incrementCartNumber(1);
        Log.e(LOGTAG, "cart number 1 :" + InkarneAppContext.getCartNumber());
        if(mListener instanceof ShopActivity)
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

    public void initData() {
        listSkus = new ArrayList<SkuData>();
        SkuData sku = new SkuData();
        sku.setmA_Category(comboData.getmA1_Category());
        sku.setmA_PIC_Png_Key_Name(comboData.getmA1_PIC_Png_Key_Name());
        sku.isSelected = false;
        sku.setmSKU_ID(comboData.getmSKU_ID1());
        listSkus.add(sku);

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
                //sku.setmA_Category(comboData.getmA10_Category());
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
        if (skuAdapter != null)
            skuAdapter.notifyDataSetChanged();
    }

    private void initView() {
        if (listSkus == null || listSkus.size() == 0)
            initData();
        if (listSkus.size() != 0) {
            SkuData skuSelected = listSkus.get(skuSelectedIndex);
            spinner.setSelection(skuSelected.indexShortingSelection);
            skuSelected.isSelected = true;
            requestLAData(skuSelectedIndex);
        }
        skuAdapter = new LookaLikeSkuAdapter(getActivity(), (ArrayList<SkuData>) listSkus);
        hlvSku.setAdapter(skuAdapter);
    }

    private void openBuyUrl(final LAData ladata) {
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

    //volley
    public void requestLAData(final int indexSku) {
        trackEvent("lookalike",listSkus.get(indexSku).getmSKU_ID(),"");
        String uri = ConstantsUtil.URL_BASEPATH + ConstantsUtil.URL_METHOD_LOOKALIKE + User.getInstance().getmUserId() + "/" + comboData.getCombo_ID() + "/" + listSkus.get(indexSku).getmSKU_ID();
        listView.setAdapter(null);
        laDataList = listSkus.get(indexSku).getLaDataList();
        if (laDataList != null && laDataList.size() != 0) {
            loadProductData();
            return;
        }
        pbSimilarProducts.setVisibility(View.VISIBLE);
        Log.d(LOGTAG, "URI :" + uri);
        final GsonRequest gsonRequest = new GsonRequest(uri, LAData.LADataWrapper.class, null, new Response.Listener<LAData.LADataWrapper>() {

            @Override
            public void onResponse(LAData.LADataWrapper ladatas) {
                laDataList = ladatas.getLaDatas();
//                for (int j = 0; j < laDataList.size(); j++) {
//                    if (laDataList.get(j).getPurchase_SKU_ID() != null)
//                        Log.d("debug", "Purchase SKUID =" + laDataList.get(j).getPurchase_SKU_ID().toString());
//                }
                if (laDataList.size() > 0) {
                    listSkus.get(indexSku).setLaDataList(laDataList);
                    if (indexSku == skuSelectedIndex)
                        loadProductData();
                } else {
                    Toast.makeText(getActivity(), "Oops,looks like everything is bought out.", Toast.LENGTH_SHORT).show();
                    pbSimilarProducts.setVisibility(View.INVISIBLE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (volleyError != null && volleyError.getMessage() != null)
                    Log.e("LookLikeFragment", volleyError.getMessage());
                pbSimilarProducts.setVisibility(View.INVISIBLE);
                Toast.makeText(getActivity(), ConstantsUtil.MESSAGE_TOAST_NETWORK_RESPONSE_FAILED, Toast.LENGTH_SHORT).show();
            }
        });
        VolleyHelper.getInstance(this.getContext().getApplicationContext()).addToRequestQueue(gsonRequest);
    }

    private synchronized void loadProductData() {
        SkuData skudata = listSkus.get(skuSelectedIndex);
        skudata.setLaDataList(laDataList);
        spinner.setSelection(skudata.indexShortingSelection);
        //Collections.sort(laDataList, LAData.Comparators.orderASC);
        laDataList = shortedList(skudata.indexShortingSelection);
        listView.setAdapter(productAapater);
        productAapater.updateLaDataList(laDataList);
        pbSimilarProducts.setVisibility(View.INVISIBLE);
    }

    private List<LAData> shortedList(int shortIndex) {
        switch (shortIndex) {
            case 0: {
                Collections.sort(laDataList, LAData.Comparators.orderASC);
            }
            break;
            case 1: {
                Collections.sort(laDataList, LAData.Comparators.priceDSC);
            }
            break;
            case 2: {
                Collections.sort(laDataList, LAData.Comparators.priceASC);
            }
            break;
            case 3: {

            }
            break;
            default: {

            }
            break;
        }
        return laDataList;
    }

}
