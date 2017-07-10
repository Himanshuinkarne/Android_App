package com.svc.sml.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.svc.sml.Activity.WebActivity;
import com.svc.sml.Adapter.CartAdapter;
import com.svc.sml.Adapter.LookaLikeProductAdapter;
import com.svc.sml.Database.ComboData;
import com.svc.sml.Database.ComboDataReconcile;
import com.svc.sml.Database.InkarneDataSource;
import com.svc.sml.Database.LAData;
import com.svc.sml.Database.User;
import com.svc.sml.Helper.ComboDownloader;
import com.svc.sml.Helper.DataManager;
import com.svc.sml.Helper.GsonRequest;
import com.svc.sml.Helper.VolleyHelper;
import com.svc.sml.InkarneAppContext;
import com.svc.sml.R;
import com.svc.sml.ShopActivity;
import com.svc.sml.Utility.ConstantsUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CartFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CartFragment extends BaseFragment implements CartAdapter.OnCartAdapterInteractionListener, View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String LOGTAG = "CartFragment";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ListView listView;
    private InkarneDataSource datasource;
    private List<LAData> laDataList = new ArrayList<LAData>();
    private CartAdapter cartAapater;
    private ProgressBar pbCart;
    private ImageButton btnBack;
    private ComboData comboData;

    private OnFragmentInteractionListener mListener;

    private ProgressDialog pDialogDownload;
    private boolean isDownloadCancelled = false;
    private ComboDataReconcile currentClickedComboData;

    private int countAccessoryInfoTobeFetched = 0;
    //private LoadingView pbLooksInfo;

    public CartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CartFragment newInstance(String param1, String param2) {
        CartFragment fragment = new CartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static CartFragment newInstance() {
        CartFragment fragment = new CartFragment();
        Bundle args = new Bundle();
        //args.putSerializable("ARG_FACE_OBJ", faceItem);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        GATrackActivity(LOGTAG);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_cart, container, false);

        datasource = InkarneDataSource.getInstance(InkarneAppContext.getAppContext());
        datasource.open();
//        laDataList = dataSource.getLADataDetails();
//        if(laDataList.size() ==0){
//            Toast.makeText(getActivity(), "No Data Found", Toast.LENGTH_LONG).show();
//        }
//        btnBack = (Button)v.findViewById(R.id.btn_cart_back);
//        btnBack.setOnClickListener(this);
        pbCart = (ProgressBar) v.findViewById(R.id.pb_cart);
        btnBack = (ImageButton) v.findViewById(R.id.btn_back_cart);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null)
                    mListener.onFragmentCartBackBtnInteraction();
            }
        });
        listView = (ListView) v.findViewById(R.id.lv_cart);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("debug", "listview item clicked :" + position);
                LAData laData = laDataList.get(position);
                openBuyUrl(laData);
            }
        });

//        cartAapater = new CartAdapter(getActivity(), (ArrayList<LAData>) laDataList,comboData);
//        cartAapater.setListener((CartAdapter.OnCartAdapterInteractionListener) this);
//
//        Collections.sort(laDataList, LAData.Comparators.orderDSC);
//        listView.setAdapter(cartAapater);
//        cartAapater.updateLaDataList(laDataList);
//        pbCart.setVisibility(View.INVISIBLE);
        return v;
    }

    public void onResume() {
        super.onResume();
        requestCartData();
        /*
        laDataList = datasource.getLADataDetails();


        if(laDataList.size() ==0){
            Toast.makeText(getActivity(), "You can add products you like to cart and track them here!", Toast.LENGTH_SHORT).show();
        }
        cartAapater = new CartAdapter(getActivity(), (ArrayList<LAData>) laDataList,comboData);
        cartAapater.setListener((CartAdapter.OnCartAdapterInteractionListener) this);

        //Collections.sort(laDataList, LAData.Comparators.orderDSC);
        listView.setAdapter(cartAapater);
        //cartAapater.updateLaDataList(laDataList);
        pbCart.setVisibility(View.INVISIBLE);
        */
    }

    private void openBuyUrl(final LAData ladata) {
        final String uri = ConstantsUtil.URL_BASEPATH + ConstantsUtil.URL_METHOD_UPDATE_BUY + User.getInstance().getmUserId() + "/" + ladata.getCombo_ID() + "/" + ladata.getPurchase_SKU_ID();
        Intent browserIntent = new Intent(getActivity(), WebActivity.class);
        browserIntent.putExtra(LookaLikeProductAdapter.PARAM_EXTRA_WEB_URI, ladata.getLink());
        browserIntent.putExtra(LookaLikeProductAdapter.PARAM_EXTRA_WEB_TITLE, ladata.getTitle());
        getActivity().startActivity(browserIntent);
        Log.d(LOGTAG, "ComboID added to Cart :" + ladata.getCombo_ID());
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
    public void requestCartData() {
        String uri = ConstantsUtil.URL_BASEPATH + ConstantsUtil.URL_METHOD_GET_CARTS + User.getInstance().getmUserId();
        listView.setAdapter(null);
        pbCart.setVisibility(View.VISIBLE);
        Log.d(LOGTAG, "URI :" + uri);
        final GsonRequest gsonRequest = new GsonRequest(uri, LAData.LACartDataWrapper.class, null, new Response.Listener<LAData.LACartDataWrapper>() {

            @Override
            public void onResponse(LAData.LACartDataWrapper ladatas) {
                pbCart.setVisibility(View.INVISIBLE);
                laDataList = ladatas.getLaDatas();
//                for (int j = 0; j < laDataList.size(); j++) {
//                    if (laDataList.get(j).getPurchase_SKU_ID() != null)
//                        Log.d("debug", "Purchase SKUID =" + laDataList.get(j).getPurchase_SKU_ID().toString());
//                }
                InkarneAppContext.setCartNumber(laDataList.size());
                if (laDataList.size() > 0) {
                    //InkarneAppContext.setCartNumber(laDataList.size());
                    cartAapater = new CartAdapter(getActivity(), (ArrayList<LAData>) laDataList, comboData);
                    cartAapater.setListener(CartFragment.this);

                    //Collections.sort(laDataList, LAData.Comparators.orderDSC);
                    listView.setAdapter(cartAapater);
                } else {
                    if(getActivity() != null)
                    Toast.makeText(getActivity(), "Your cart is empty", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (volleyError != null && volleyError.getMessage() != null)
                    Log.e("LookLikeFragment", volleyError.getMessage());
                pbCart.setVisibility(View.INVISIBLE);
                if(getActivity() != null)
                Toast.makeText(getActivity(), ConstantsUtil.MESSAGE_TOAST_NETWORK_RESPONSE_FAILED, Toast.LENGTH_SHORT).show();
            }
        });
        VolleyHelper.getInstance(this.getContext().getApplicationContext()).addToRequestQueue(gsonRequest);
    }

    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentCartInteraction(ComboData co);
//        }
//    }

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

    @Override
    public void onCartRemoved(LAData laData) {
        Log.d(LOGTAG, "ComboID added to Cart :" + laData.getCombo_ID());
        //comboData.setCartCount(laData.getCart_Count());
        datasource.updateComboCartCount(laData.getCombo_ID(), laData.getCart_Count());
        laData.setCombo_ID(laData.getCombo_ID());
        InkarneAppContext.incrementCartNumber(-1);
        datasource.delete(laData);
        trackEvent("Cart", laData.getPurchase_SKU_ID(), "");
    }

    @Override
    public void onBuyAdded(LAData laData) {

        trackEvent("Buy", laData.getPurchase_SKU_ID(), "");
    }

    @Override
    public void onView3d(LAData laData) {
        ComboData comboData = datasource.getComboDataByComboID(laData.getCombo_ID());
        trackEvent("View3d", laData.getPurchase_SKU_ID(), "");
        if (comboData != null && comboData.getCombo_ID() != null && !comboData.getCombo_ID().isEmpty()) {
            startShowingProgress();
            currentClickedComboData = comboData;
            getComboInfoWithDownloads(comboData);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.btn_cart_back:{
//                //getActivity().finish();
//            }
//            break;
            default:
                break;
        }
    }

    public void startShowingProgress() {
        //pbLooksInfo.setVisibility(View.VISIBLE);
        //pbLooksInfo.setLoadingText(getActivity().getString(R.string.message_rendering_looks));
        if (pDialogDownload == null)
            pDialogDownload = getHProgressDialogTranslucent();
        pDialogDownload.setTitle(getActivity().getString(R.string.message_rendering_looks));
        pDialogDownload.setIndeterminate(true);
        pDialogDownload.setProgress(0);
        isDownloadCancelled = false;
        if (mListener instanceof ShopActivity && !((ShopActivity) mListener).isFinishing())
            pDialogDownload.show();
    }

    public void stopShowingProgress() {
        //pbLooksInfo.setVisibility(View.INVISIBLE);
        if (pDialogDownload != null) {
            pDialogDownload.setProgress(100);
            pDialogDownload.dismiss();
        }
    }

    public ProgressDialog getHProgressDialogTranslucent() {
        ProgressDialog p = new ProgressDialog(getActivity(), R.style.AppCompatAlertDialogStyle);
        //p.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        p.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        p.setIndeterminate(false);
        p.setProgress(0);
        p.setCanceledOnTouchOutside(false);
        p.setCancelable(true);
        p.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dialog.dismiss();
                stopShowingProgress();
                isDownloadCancelled = true;

            }
        });
        return p;
    }


    public void getComboInfoWithDownloads(ComboData comboDataReconcile) {

        isDownloadCancelled = false;
//      pDialogDownload.setProgressNumberFormat(null);
//      pDialogDownload.setProgressPercentFormat(null);
        new ComboDownloader(getActivity(), comboDataReconcile, new ComboDownloader.OnComboDownloadListener() {
            @Override
            public void onDownload(final ComboData receivedComboData) {

                if (receivedComboData != null) {
                    Log.d("receiver", "combo received : " + receivedComboData.getCombo_ID() + "  rank :" + receivedComboData.getForced_Rank());
                    if (receivedComboData.getCombo_ID().equals(currentClickedComboData.getCombo_ID()) && !isDownloadCancelled) {
                        if (mListener != null) {
                            receivedComboData.setLooksCategoryTitle(receivedComboData.getLooksCategoryTitle());
                            mListener.onFragmentCartInteraction(receivedComboData);//
                        }
                    }
                }
                if (pDialogDownload != null)
                    pDialogDownload.setProgress(100);
                stopShowingProgress();

//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (receivedComboData != null) {
//                            Log.d("receiver", "combo received : " + receivedComboData.getCombo_ID() + "  rank :" + receivedComboData.getForced_Rank());
//                            if (receivedComboData.getCombo_ID().equals(currentClickedComboData.getCombo_ID()) &&  !isDownloadCancelled) {
//                                if (mListener != null) {
//                                    receivedComboData.setLooksCategoryTitle(currentClickedComboData.getLooksCategoryTitle());
//                                    mListener.onLooksFragmentInteraction(receivedComboData);
//                                }
//                            }
//                        }
//                    }
//                },60);

                //pbLooksInfo.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onDownloadFailed(String comboId) {
                stopShowingProgress();

            }

            @Override
            public void onDownloadProgress(String comboId, int percentage) {
                Log.w(LOGTAG, " " + percentage + "%");
                if (pDialogDownload.isIndeterminate()) {
                    pDialogDownload.setIndeterminate(false);
                }
                if (pDialogDownload != null) {
                    if (percentage > 85) {
                        percentage -= (4 + new Random().nextInt(6));//todo hack
                        animateProgressIncrement();
                    } else
                        pDialogDownload.setProgress(percentage);

                }
            }

            @Override
            public void onComboInfoFailed(String comboId, int error_code) {
                stopShowingProgress();
                if (error_code == DataManager.CODE_DATA_MANAGER_NETWORK_ERROR) {
                    //InkarneAppContext.showNetworkAlert();
                    if(getActivity() != null)
                    Toast.makeText(getActivity(), getString(R.string.message_network_failure), Toast.LENGTH_SHORT).show();
                } else {
                    //InkarneAppContext.showAlert("Looks info not available");
                    if(getActivity() != null)
                    Toast.makeText(getActivity(), "Oops,could not reach our server.Please try again", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onComboInfoResponse(String comboId) {
                pDialogDownload.setIndeterminate(false);
            }
        });
    }

    Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            pDialogDownload.incrementProgressBy(1);
        }
    };

    private void animateProgressIncrement() {
        // progress -= 4+ new Random().nextInt(6);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (pDialogDownload.getProgress() <= 97) {
                        Thread.sleep(500);
                        handle.sendMessage(handle.obtainMessage());
//                        if (pDialogDownload.getProgress() == pDialogDownload
//                                .getMax()) {
//                            pDialogDownload.dismiss();
//                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
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
        void onFragmentCartInteraction(ComboData comboData);

        void onFragmentCartBackBtnInteraction();
    }
}
