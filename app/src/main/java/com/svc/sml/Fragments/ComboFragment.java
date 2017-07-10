package com.svc.sml.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.svc.sml.Adapter.ComboAdapter;
import com.svc.sml.Database.ComboData;
import com.svc.sml.Database.ComboDataReconcile;
import com.svc.sml.Database.InkarneDataSource;
import com.svc.sml.Database.User;
import com.svc.sml.Helper.DataManager;
import com.svc.sml.Helper.DownloadIntentService;
import com.svc.sml.R;
import com.svc.sml.Utility.ConstantsUtil;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ComboFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ComboFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComboFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String LOGTAG = ComboFragment.class.getName();
    public static final String PARAM_EXTRA_COMBO_FILTER = "ARG_PARAM_COMBO_FILTER";
    public static final String PARAM_EXTRA_COMBO_LIST = "ARG_PARAM_COMBO_LIST";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private SearchView mSearchView;
    private ListView mListView;
    private ArrayList<ComboData> arrayComboData = new ArrayList<ComboData>();
    private ArrayList<ComboData> arrayComboDataTotal = new ArrayList<ComboData>();
    private ComboAdapter comboAdapter;
    private Button btnBack;
    private ProgressBar pbSKUDownload;
    private InkarneDataSource datasource;
    private String filter ;

    private OnFragmentInteractionListener mListener;

    public ComboFragment() {
        // Required empty public constructor
    }


    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ComboFragment.
     */
    // TODO: Rename and change types and number of parameters

    public static ComboFragment newInstance(ArrayList<ComboData> arrayCombodata) {
        ComboFragment fragment = new ComboFragment();
        Bundle args = new Bundle();
        args.putSerializable(PARAM_EXTRA_COMBO_LIST, arrayCombodata);
        fragment.setArguments(args);
        return fragment;
    }

    public static ComboFragment newInstance(String filter) {
        ComboFragment fragment = new ComboFragment();
        Bundle args = new Bundle();
        args.putSerializable(PARAM_EXTRA_COMBO_FILTER, filter);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            filter = getArguments().getString(PARAM_EXTRA_COMBO_FILTER, ConstantsUtil.EDialFilter.eDailUnselected.toString());
            //arrayComboData = (ArrayList<ComboData>) getArguments().getSerializable(PARAM_EXTRA_COMBO_LIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_combo, container, false);
        datasource = InkarneDataSource.getInstance(getActivity().getApplicationContext());
        datasource.open();
        pbSKUDownload = (ProgressBar) v.findViewById(R.id.pb_combo_skuDownload);
        pbSKUDownload.setVisibility(View.INVISIBLE);
        mSearchView = (SearchView) v.findViewById(R.id.searchView1);
        mListView = (ListView) v.findViewById(R.id.listView1);
        //btnBack = (Button) v.findViewById(R.id.btn_combo_back);
        //btnBack.setOnClickListener(this);
        return v;
    }

    private ArrayList<ComboData> getFilteredComboData(ArrayList<ComboData> arrayComboDataTotal, String filter) {
        Log.d(LOGTAG, "FilterCombos :" + filter);
        int maxCombos = arrayComboDataTotal.size();
        ArrayList<ComboData> arrayComboData = new ArrayList<>();
        //arrayComboData.clear();
        if (filter.equals(ConstantsUtil.EDialFilter.eDailUnselected.toString())) {
            for (ComboData combo : arrayComboDataTotal) {
                arrayComboData.add(combo);
            }
        } else if (filter.equals(ConstantsUtil.EDialFilter.eDailCasual.toString()) ||
                filter.equals(ConstantsUtil.EDialFilter.eDailIndowestern.toString()) ||
                filter.equals(ConstantsUtil.EDialFilter.eDailFormal.toString()) ||
                filter.equals(ConstantsUtil.EDialFilter.eDailParty.toString())
                ) {
            for (ComboData comboData : arrayComboDataTotal) {
                //if ((ShownCategory.get(i).toString()).equals("Casual")) {
                if (comboData.getCombo_Style_Category().equals(filter)) {
                    arrayComboData.add(comboData);
                }
            }
        } else if (filter.equals(ConstantsUtil.EDialFilter.eDailCart.toString())) {
            for (ComboData comboData : arrayComboDataTotal)  {
                if (comboData.getCartCount() > 0) {
                    arrayComboData.add(comboData);
                }
            }
        } else if (filter.equals(ConstantsUtil.EDialFilter.eDailLookOfday.toString())) {
            for (ComboData comboData : arrayComboDataTotal)  {
                if (comboData.getPush_Flag().equals("True")) {
                    arrayComboData.add(comboData);
                }
            }
        }
        else if (filter.equals(ConstantsUtil.EDialFilter.eDailDeals.toString())) {
            for (ComboData comboData : arrayComboDataTotal)  {
                if (comboData.getVogue_Flag().equals("True")) {
                    arrayComboData.add(comboData);
                }
            }
        }
        else if (filter.equals(ConstantsUtil.EDialFilter.eDailLike.toString())) {
            for (ComboData comboData : arrayComboDataTotal) {
                if (comboData.isLiked() > 0) {
                    arrayComboData.add(comboData);
                }
            }
        } else {
            arrayComboData = arrayComboDataTotal;
        }
        Log.d(LOGTAG, " Filtered Count :" + arrayComboData.size());
        return arrayComboData;
    }


    private void setupSearchView() {
        mSearchView.setIconifiedByDefault(false);
        //mSearchView.setOnQueryTextListener(this);
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setQueryHint("Search Here");
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

    private void initResume(){
        arrayComboDataTotal = (ArrayList<ComboData>) datasource.getComboReconcileData(true);
        arrayComboData = getFilteredComboData(arrayComboDataTotal,filter);//TODO
        comboAdapter = new ComboAdapter(getActivity(), arrayComboData);
        mListView.setAdapter(comboAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(LOGTAG, "listview item clicked :" + position);
                ComboData comboDataReconcile = arrayComboData.get(position);
                pbSKUDownload.setVisibility(View.VISIBLE);
                if (comboDataReconcile.getmA1_Obj_Key_Name() != null && comboDataReconcile.getmA1_Obj_Key_Name().length() > 0
                        && ConstantsUtil.checkFileKeyExist(comboDataReconcile.getmA1_Obj_Key_Name())
                        && comboDataReconcile.getIsDisplayReady() == 1) {
                    if (mListener != null) {
                        ComboData comboData = datasource.getComboDataByComboID(comboDataReconcile.getCombo_ID());
                        mListener.onComboFragmentInteraction(comboData, filter);
                    }
                    pbSKUDownload.setVisibility(View.INVISIBLE);
                } else {
                    startSkusDownloadService(comboDataReconcile);
                }
            }
        });

        mListView.setTextFilterEnabled(true);
        setupSearchView();
    }

    @Override
    public void onResume() {
        initResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                mMessageReceiver, new IntentFilter(DownloadIntentService.INTENT_NAME_COMBOS_DOWNLOAD_SERVICE));
        super.onResume();

        super.onResume();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub
                if (query.length() > 0) {
                    comboAdapter.getFilter().filter(query);
                } else {
                    //loadData();
                }
                //hideKeyboard();
                mSearchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 0) {
                    //comboAdapter.getFilter().filter(newText);
                } else {
                    //loadData();
                    comboAdapter.getFilter().filter(newText);
                }
                return false;
            }
        });
    }

    @Override
    public void onPause() {
        // Unregister since the activity is paused.
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    public void startSkusDownloadService(ComboData combodata) {
        if(combodata.getmA2_Obj_Key_Name()!= null && combodata.getmA2_Obj_Key_Name().length()>0){
            Intent intent = new Intent(getActivity(), DownloadIntentService.class);
            intent.setAction(DownloadIntentService.ACTION_DOWNLOAD_COMBOS);
            intent.putExtra(DownloadIntentService.EXTRA_PARAM_COMBO, combodata);
            getActivity().startService(intent);
            return;
        }
        DataManager.getInstance().requestComboDataInfoById(combodata.getCombo_ID(), User.getInstance().getDefaultFaceId(),
                new DataManager.OnResponseHandlerInterface() {
                    @Override
                    public void onResponse(Object obj) {
                        ArrayList<ComboData> arrayListCombo = (ArrayList<ComboData>) obj;
                        if (arrayListCombo != null && arrayListCombo.size() > 0) {
                            ComboData comboData = arrayListCombo.get(0);
                            Intent intent = new Intent(getActivity(), DownloadIntentService.class);
                            intent.setAction(DownloadIntentService.ACTION_DOWNLOAD_COMBOS);
                            intent.putExtra(DownloadIntentService.EXTRA_PARAM_COMBO, comboData);
                            getActivity().startService(intent);
                        }
                    }

                    @Override
                    public void onResponseError(String errorMessage, int errorCode) {
                        Log.e(LOGTAG, "Error getting comboInfo :" + errorMessage);
                    }
                });
    }


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            // Get extra data included in the Intent
            ComboData receivedCombodata = (ComboData) intent.getSerializableExtra("comboData");
            String message = intent.getStringExtra("message");
            pbSKUDownload.setVisibility(View.INVISIBLE);
            if (receivedCombodata != null) {
                Log.d("receiver", "combo received : " + receivedCombodata.getCombo_ID() + "  rank :" + receivedCombodata.getForced_Rank());
                if (mListener != null)
                    mListener.onComboFragmentInteraction(receivedCombodata, filter);
            }
        }
    };

    private void getComboDetailAndFiles(ComboDataReconcile comboDataReconcile) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.btn_combo_back: {
//                mListener.onComboFragmentClose(null);
//
//            }
//            break;

            default:
                break;
        }
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
        void onComboFragmentInteraction(ComboData combodata, String filter);
        void onComboFragmentClose(Uri uri);
    }

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
