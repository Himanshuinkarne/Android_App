package com.svc.sml.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.svc.sml.Adapter.LooksFragmentBottomAdapter;
import com.svc.sml.Database.ComboData;
import com.svc.sml.Database.ComboDataLooksItem;
import com.svc.sml.Database.ComboDataReconcile;
import com.svc.sml.Database.DatabaseHandler;
import com.svc.sml.Database.InkarneDataSource;
import com.svc.sml.Utility.HorizontalListView;
import com.svc.sml.InkarneAppContext;
import com.svc.sml.R;
import com.svc.sml.ShopActivity;
import com.svc.sml.Utility.Connectivity;
import com.svc.sml.Utility.ConstantsUtil;
import com.svc.sml.View.LoadingView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;


public class CollectionFragment2 extends BaseLooksFragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String LOGTAG = "CollectionFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnBaseLookFragmentInteractionListener mListener;

    private HorizontalListView hListCategory;
    private HorizontalListView hListBottom;
    private ViewPager vPagerLookBoard;

    private InkarneDataSource datasource;
    private LoadingView pbLooksInfo;
    private ComboData currentClickedComboData;
    private ArrayList<ComboData> currentComboList;
    private int currentComboIndex = 0;
    private ProgressDialog pDialogDownload;

    private boolean isDownloadCancelled = false;
    private HashMap<String,Boolean> hmLoadedCombo = new HashMap<>();
    private ArrayList<ComboDataLooksItem> listOfComboList = new ArrayList<>();
    private ArrayList<ComboData>comboList = new ArrayList<>();
    public ProgressDialog pBInitData;

    private int selectedCatIndex =0;


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        //void onListFragmentInteraction(DummyItem item);
//        void onCollectionFragmentInteraction(ComboData combodata);
//        void onCollectionFragmentInteraction(ComboData combodata,ArrayList<ComboData> combolist,int index);
//    }

    public CollectionFragment2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment CollectionFragment2.
     */
    // TODO: Rename and change types and number of parameters
    public static CollectionFragment2 newInstance(String param1) {
        CollectionFragment2 fragment = new CollectionFragment2();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_collection_2, container, false);
        hListCategory =  (HorizontalListView)view.findViewById(R.id.hlist_looks_category);
        hListBottom =  (HorizontalListView)view.findViewById(R.id.hlist_looks_bottom);
        vPagerLookBoard = (ViewPager)view.findViewById(R.id.vpager_looks);

        hListCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


            }
        });


        datasource = InkarneDataSource.getInstance(getActivity().getApplicationContext());
        datasource.open();
        pDialogDownload = getHProgressDialogTranslucent();
        return view;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnBaseLookFragmentInteractionListener) {
            mListener = (OnBaseLookFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnLooksFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        Log.w("received", "resume");
        super.onResume();
        hmLoadedCombo.clear();
        if(((ShopActivity)getActivity()).listOfComboList != null && ((ShopActivity)getActivity()).listOfComboList.size() != 0 ){
            this.listOfComboList = ((ShopActivity)getActivity()).listOfComboList;
            //collectionAdapter = new CollectionAdapter(getActivity(), listOfComboList, (CollectionAdapter.OnGridAdapterListener) CollectionFragment.this);
            //gridCollectionView.setAdapter(collectionAdapter);
        }else {
            new LoadLooksListTask().execute();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop(){
        super.onStop();
        stopShowingProgress();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {

    }

//    public void populateCategory(){
//        LooksCategoryAdapter ad = new LooksCategoryAdapter(getActivity(),R.layout.hlist_item_looks_category,ConstantsUtil.arrayListLooksLabelName);
//        hListCategory.setAdapter(ad);
//    }

    public void populateBottomListview(){
        LooksFragmentBottomAdapter ad = new LooksFragmentBottomAdapter(getActivity(),ConstantsUtil.arrayListLooksLabelName.get(selectedCatIndex),comboList);
        hListCategory.setAdapter(ad);
    }


    private void checkComboDetailPopulated(ComboData comboData) {
        if (!Connectivity.isConnected(getActivity())) {
            stopShowingProgress();
            InkarneAppContext.showNetworkAlert();
            return;
        }

        if (Connectivity.isConnectedWifi(getActivity()) || !InkarneAppContext.getSettingIsWifiOnlyDownload()) {
            getComboInfoWithDownloads(comboData);
        }else{
            stopShowingProgress();
            Toast.makeText(getActivity().getApplicationContext(),"Please disable \"download on wifi only\" mode to use mobile data",Toast.LENGTH_SHORT).show();
        }
    }


    /* get service */
    private class LoadLooksListTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbLooksInfo.setVisibility(View.VISIBLE);
            pbLooksInfo.setLoadingText(getActivity().getString(R.string.message_loading_looks));
            if (pBInitData == null)
                pBInitData = new ProgressDialog(getActivity());
            pBInitData.show();
            //TODO some data issue handling -- to be modified
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (pBInitData != null && pBInitData.isShowing())
                        pBInitData.dismiss();

                    pbLooksInfo.setVisibility(View.INVISIBLE);
                }
            }, 1000);
        }

        @Override
        protected Void doInBackground(Void... params) {
            initData();
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... params) {


        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pBInitData != null)
                pBInitData.dismiss();
            pbLooksInfo.setVisibility(View.INVISIBLE);
            //collectionAdapter = new CollectionAdapter(getActivity(), listOfComboList, (CollectionAdapter.OnGridAdapterListener) CollectionFragment.this);
            //gridCollectionView.setAdapter(collectionAdapter);
        }
    }

    /* get data */

    private void initData() {
        ArrayList<ComboData> comboReconcileData = (ArrayList<ComboData>) datasource.getComboReconcileData(InkarneAppContext.shouldRearrangeLooks);
        filterCombo(comboReconcileData);
        InkarneAppContext.shouldRearrangeLooks = false;
    }

    public ArrayList<ComboDataLooksItem> getSkeletonListOfComboList() {
        ArrayList<ComboDataLooksItem> list = new ArrayList<>();
        for (int i = 0; i < ConstantsUtil.arrayListLooksLabelName.size(); i++) {
            ComboDataLooksItem looksListItem = new ComboDataLooksItem(ConstantsUtil.arrayListLooksLabelName.get(i), ConstantsUtil.arrayListLooksCategory.get(i));
            list.add(looksListItem);
        }
        return list;
    }

    public void addToLooksList(ComboData c, String looksCategory) {
        int index = ConstantsUtil.arrayListLooksCategory.indexOf(looksCategory);
        ComboDataLooksItem looksItem = listOfComboList.get(index);
        looksItem.getComboList().add(c);
    }

    public void addToTrendingLooksList() {
        int index = ConstantsUtil.arrayListLooksCategory.indexOf("Vogue_Flag");
        ComboDataLooksItem looksItem = listOfComboList.get(index);
        ArrayList<ComboData> trending = (ArrayList<ComboData>) datasource.getComboReconcileTrendingData(InkarneAppContext.shouldRearrangeLooks);
        //looksItem.getComboList().addAll(trending);
        looksItem.setComboList(trending);
    }

    public void addToLooksListForYou(ComboData c, String looksCategory) {
        int index = ConstantsUtil.arrayListLooksCategory.indexOf(looksCategory);
        ComboDataLooksItem looksItem = listOfComboList.get(index);
        int count = looksItem.getComboList().size() > 20 ? 20 : looksItem.getComboList().size();
        boolean isInserted = false;
        for (int i = 0; i < count; i++) {
            ComboData comboData = looksItem.getComboList().get(i);
            if (c.getStyle_Rating() > comboData.getStyle_Rating()) {
                looksItem.getComboList().add(i, c);
                isInserted = true;
                break;
            }
        }
        if (isInserted && count >= 20) {
            looksItem.getComboList().remove(looksItem.getComboList().size() - 1);
        }
        if (!isInserted && count < 20) {
            looksItem.getComboList().add(c);
        }
    }


    public void filterCombo(ArrayList<ComboData> comboList) {
        listOfComboList = getSkeletonListOfComboList();
        for (ComboData c : comboList) {
            if (c.getCombo_ID() == null || c.getCombo_ID().isEmpty() || c.getCombo_ID().equals("null")) //TODO
                continue;

            Log.w(LOGTAG, "filterCombo ComboId: " + c.getCombo_ID());

            addToLooksListForYou(c, "StyleRating");

            if (c.getIsLiked() == 1) {
                addToLooksList(c, "isLiked");
            }

            for (String style : ConstantsUtil.arrayListCategoryStyle) {
                if (c.getCombo_Style_Category().equals(style)) {
                    addToLooksList(c, style);
                }
            }
            if (c.getViewCount() > 0) {
                addToLooksList(c, DatabaseHandler.DATE_SEEN_IN_MILLI);
            }
        }

        addToTrendingLooksList();
        reArrangeLikesAndHistory();
    }

    private void reArrangeLikesAndHistory(){
        int index = ConstantsUtil.arrayListLooksCategory.indexOf("isLiked");
        ComboDataLooksItem looksItem = listOfComboList.get(index);
        ArrayList<ComboData> listLikes = looksItem.getComboList();
        for(ComboData co : listLikes ){
            Log.d(LOGTAG, co.getDateSeenInMilli() +"  :"+  co.getCombo_ID());
        }

        Collections.sort(listLikes, Comparators.likesDESC);
        Log.w(LOGTAG, "**************** rearranged likes *********************");
        for(ComboData co : listLikes ){
            Log.d(LOGTAG, co.getDateSeenInMilli() +"  :"+  co.getCombo_ID());
        }
        looksItem.setComboList(listLikes);

        int indexHistory = ConstantsUtil.arrayListLooksCategory.indexOf(DatabaseHandler.DATE_SEEN_IN_MILLI);
        ComboDataLooksItem historyItem = listOfComboList.get(indexHistory);
        ArrayList<ComboData> listHistory = historyItem.getComboList();
        Log.w(LOGTAG, "**************** History *********************");
        for(ComboData co : listHistory ){
            Log.d(LOGTAG, co.getDateSeenInMilli() +"  :"+  co.getCombo_ID());
        }
        Collections.sort(listHistory, Comparators.likesDESC);
        Log.w(LOGTAG, "**************** rearranged History *********************");
        for(ComboData co : listHistory ){
            Log.d(LOGTAG, co.getDateSeenInMilli() +"  :"+  co.getCombo_ID());
        }
        historyItem.setComboList(listHistory);
    }

    public static class Comparators {
        public static Comparator<ComboDataReconcile> likesDESC = new Comparator<ComboDataReconcile>() {
            @Override
            public int compare(ComboDataReconcile o1, ComboDataReconcile o2) {
                //return o1.age - o2.age;
                return (int) o2.getDateSeenInMilli() - (int) o1.getDateSeenInMilli();
            }
        };
    }
}
