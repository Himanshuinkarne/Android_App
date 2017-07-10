package com.svc.sml.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.svc.sml.Adapter.CollectionAdapter;
import com.svc.sml.Database.ComboData;
import com.svc.sml.Database.ComboDataLooksItem;
import com.svc.sml.Database.ComboDataReconcile;
import com.svc.sml.Database.DatabaseHandler;
import com.svc.sml.Database.InkarneDataSource;
import com.svc.sml.Database.User;
import com.svc.sml.Helper.ComboDownloader;
import com.svc.sml.Helper.DataManager;
import com.svc.sml.InkarneAppContext;
import com.svc.sml.Model.BaseAccessoryItem;
import com.svc.sml.Model.BaseItem;
import com.svc.sml.R;
import com.svc.sml.Utility.Connectivity;
import com.svc.sml.Utility.ConstantsUtil;
import com.svc.sml.View.LoadingView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class CollectionFragment extends BaseFragment implements View.OnClickListener, CollectionAdapter.OnGridAdapterListener {
    private static final String LOGTAG = "CollectionFragment";
    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    public static final String PARAM_EXTRA_COMBO_FILTER = "ARG_PARAM_COMBO_FILTER";
    public static final String PARAM_EXTRA_COMBO_LIST_OF_LISTS = "PARAM_EXTRA_COMBO_LIST_OF_LISTS";
    private static final int INDEX_DOWNLOAD_A6 = 6;
    private static final int INDEX_DOWNLOAD_A6_TEX = 61;
    private static final int INDEX_DOWNLOAD_A7 = 7;
    private static final int INDEX_DOWNLOAD_A7_LEGS = 72;
    private static final int INDEX_DOWNLOAD_A7_TEX = 71;
    private static final int INDEX_DOWNLOAD_A8 = 8;
    private static final int INDEX_DOWNLOAD_A8_TEX = 81;
    private static final int INDEX_DOWNLOAD_A9 = 9;
    private static final int INDEX_DOWNLOAD_A9_TEX = 91;
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnFragmentInteractionListener mListener;
    private ListView gridCollectionView;
    private ImageButton btnHelp;
    // private ArrayList<Gallery> galleryList = new ArrayList<>();
    private ArrayList<ComboDataLooksItem> listOfComboList = new ArrayList<>();

    private CollectionAdapter collectionAdapter;
    private InkarneDataSource datasource;
    private LoadingView pbLooksInfo;
    private ComboData currentClickedComboData;
    private ArrayList<ComboData> currentComboList;
    private int currentComboIndex = 0;
    private ProgressDialog pDialogDownload;
    private boolean isDownloadCancelled = false;
    private SettingsDialog settingsDialog;

    public ProgressDialog pBInitData;
    private int countAccessoryInfoTobeFetched = 0;
    private HashMap<String,Boolean> hmLoadedCombo = new HashMap<>();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CollectionFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static CollectionFragment newInstance(int columnCount) {
        CollectionFragment fragment = new CollectionFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    public static CollectionFragment newInstance() {
        CollectionFragment fragment = new CollectionFragment();
        return fragment;
    }

    public static CollectionFragment newInstance(ArrayList<ComboData> arrayCombodata) {
        CollectionFragment fragment = new CollectionFragment();
        Bundle args = new Bundle();
        args.putSerializable(PARAM_EXTRA_COMBO_LIST_OF_LISTS, arrayCombodata);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
        GATrackActivity(LOGTAG);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collection, container, false);
        //pbLooksInfo = (ProgressBar) view.findViewById(R.id.pb_collection_skuDownload);
        pbLooksInfo = (LoadingView) view.findViewById(R.id.loading_view);
        pbLooksInfo.setVisibility(View.INVISIBLE);
        gridCollectionView = (ListView) view.findViewById(R.id.collectionView);
        btnHelp = (ImageButton) view.findViewById(R.id.btn_download_help);
        btnHelp.setVisibility(View.INVISIBLE);

        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(settingsDialog == null){
                    settingsDialog = new SettingsDialog();
                }
                settingsDialog.showSettingDialog();
                trackEvent("Settings", "", "");
            }
        });
        datasource = InkarneDataSource.getInstance(getActivity().getApplicationContext());
        datasource.open();
        pDialogDownload = getHProgressDialogTranslucent();
        //initData();
        return view;
    }


    @Override
    public void onResume() {
        Log.w("received", "resume");
        super.onResume();
        hmLoadedCombo.clear();
        this.listOfComboList = InkarneAppContext.getListOfComboList();
        if(listOfComboList != null) {
            collectionAdapter = new CollectionAdapter(getActivity(), listOfComboList, CollectionFragment.this);
            gridCollectionView.setAdapter(collectionAdapter);
        }

        /*
        if(((ShopActivity)getActivity()).listOfComboList != null && ((ShopActivity)getActivity()).listOfComboList.size() != 0 ){
            this.listOfComboList = ((ShopActivity)getActivity()).listOfComboList;
            collectionAdapter = new CollectionAdapter(getActivity(), listOfComboList, CollectionFragment.this);
            gridCollectionView.setAdapter(collectionAdapter);
        }else {
            InkarneAppContext.getListOfComboList();
            new LoadLooksListTask().execute();
        }
/       */
    }


    @Override

    public void onPause() {
        // Unregister since the activity is paused.
//        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onPause();
        //stopShowingProgress();
    }
    public void onStop(){
        super.onStop();
        stopShowingProgress();
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
            addToLooksListForYou(c, "StyleRating");

            Log.w(LOGTAG, "Cmbo ID: " + c.getCombo_ID());
            if (c.getVogue_Flag() == null) {
                Log.d(LOGTAG, "Cmbo ID 1: " + c.getCombo_ID());
                continue;
            }
            //Log.e(LOGTAG, "Cmbo ID 2: " + c.getCombo_ID());
            if (c.getVogue_Flag().equals("True")) {
                //addToLooksList(c, "Vogue_Flag"); //8jul
            } else if (c.getPush_Flag().equals("True")) {
                //addToLooksList(c, "Push_Flag");

            }
            if (c.getIsLiked() == 1) {
                addToLooksList(c, "isLiked");
            }

            for (String style : ConstantsUtil.arrayListCategoryStyle) {
                if (c.getCombo_Style_Category().equals(style.toUpperCase())) {
                    addToLooksList(c, style);
                }
            }
            if (c.getViewCount() > 0) {
                addToLooksList(c, DatabaseHandler.DATE_SEEN_IN_MILLI);
            }
        }

        addToTrendingLooksList();
        reArrangeLikesAndHistory();

//        int index =  ConstantsUtil.arrayListLooksCategory.indexOf("isLiked");
//        ComboDataLooksItem item = listOfComboList.get(index);
//        if(item.getComboList().size()==0){
//            listOfComboList.remove(index);
//        }
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

    /*
    public void filterCombo(ArrayList<ComboData> comboList) {
        ArrayList<String> listCatStyle = new ArrayList<>();
        HashMap<String, Integer> hm = new HashMap<>();
        for (ComboData c : comboList) {
            if (listCatStyle.contains(c.getCombo_Style_Category())) {
                Integer index = hm.get(c.getCombo_Style_Category());
                ComboDataLooksItem list = listOfComboList.get(index);
                list.getComboList().add(c);
            } else {
                listCatStyle.add(c.getCombo_Style_Category());
                ArrayList<ComboData> comboList1 = new ArrayList<>();
                comboList1.add(c);
                ComboDataLooksItem list = new ComboDataLooksItem();
                list.setComboStyleCategory(c.getCombo_Style_Category());
                list.setComboList(comboList1);

                listOfComboList.add(list);
                hm.put(c.getCombo_Style_Category(), listOfComboList.indexOf(list));
            }
        }
    }
    */


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

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
            collectionAdapter = new CollectionAdapter(getActivity(), listOfComboList, CollectionFragment.this);
            gridCollectionView.setAdapter(collectionAdapter);

        }
    }

    @Override
    public void onClick(View v) {

    }

    public class SettingsDialog {
        public  SwitchCompat switchIsAutRotate;
        public  SwitchCompat switchIsWifiOnly;
        public  Button btnSettingsCancel;
        public  Button btnSettingsSave;
        private AlertDialog alertDialog;

        public SettingsDialog(){
            createSettingDialog();
        }

        private void dismissSettingDialog(){
            alertDialog.dismiss();
        }

        public void showSettingDialog(){
            if(alertDialog == null)
                createSettingDialog();

            switchIsWifiOnly.setChecked(InkarneAppContext.getSettingIsWifiOnlyDownload());
            switchIsAutRotate.setChecked(InkarneAppContext.getSettingIsAutoRotateLookDisabled());
            alertDialog.show();
        }

        private void createSettingDialog() {
            if (alertDialog == null) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_settings, null);
                dialogBuilder.setView(dialogView);
                switchIsWifiOnly = (SwitchCompat) dialogView.findViewById(R.id.switch_wifi_only);
                switchIsAutRotate = (SwitchCompat) dialogView.findViewById(R.id.switch_auto_rotate);
                btnSettingsCancel = (Button) dialogView.findViewById(R.id.btn_settings_cancel);
                btnSettingsSave = (Button) dialogView.findViewById(R.id.btn_settings_save);
                alertDialog = dialogBuilder.create();

                switchIsAutRotate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    }
                });

                switchIsWifiOnly.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        //InkarneAppContext.saveSettingIsWifiOnlyDownload(isChecked);
                        //alertDialog.dismiss();
                    }
                });

                btnSettingsSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InkarneAppContext.saveSettingIsWifiOnlyDownload(switchIsWifiOnly.isChecked());
                        InkarneAppContext.saveSettingIsAutoRotateLookDisabled(switchIsAutRotate.isChecked());
                        alertDialog.dismiss();
                    }
                });

                btnSettingsCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switchIsWifiOnly.setChecked(InkarneAppContext.getSettingIsWifiOnlyDownload());
                        switchIsAutRotate.setChecked(InkarneAppContext.getSettingIsAutoRotateLookDisabled());
                        alertDialog.dismiss();
                    }
                });
            }
        }
    }



    @Override
    public void onComboSelected(final String looksCatTitle,final ArrayList<ComboData> comboList, final int position) {
        if(!(comboList != null && comboList.size()>position)){
            return;
        }
        currentClickedComboData = comboList.get(position);
        Log.w(LOGTAG, "listView item clicked :" + currentClickedComboData.getCombo_ID());
        Log.w("received", "listView item clicked :" + currentClickedComboData.getCombo_ID());
        hmLoadedCombo.put(currentClickedComboData.getCombo_ID(), false);
        currentComboList = comboList;
        currentComboIndex = position;
        ComboData comboData = currentClickedComboData;
        startShowingProgress();
        countAccessoryInfoTobeFetched = 0;
        String title = looksCatTitle;
//        if (looksCatTitle.equals(ConstantsUtil.arrayListLooksLabelName.get(7))) {
//            comboData = getComboDataForLiked(comboDataReconcile);
//        }

        if (looksCatTitle.equals(ConstantsUtil.arrayListLooksLabelName.get(7))
                || looksCatTitle.equals(ConstantsUtil.arrayListLooksLabelName.get(8))) {
            title = comboData.getCombo_Style_Category();
        }
        currentClickedComboData.setLooksCategoryTitle(title);
        checkComboDetailPopulated(comboData);
    }

    @Override
    public void onComboSelected(final String looksCatTitle,final ArrayList<ComboData> combolist, final ComboData comboDataReconcile) {
        Log.w(LOGTAG, "listView item clicked :" + comboDataReconcile.getCombo_ID());
        Log.w("received", "listView item clicked :" + comboDataReconcile.getCombo_ID());
        hmLoadedCombo.put(comboDataReconcile.getCombo_ID(), false);
        currentClickedComboData = comboDataReconcile;
        currentComboList = combolist;
        ComboData comboData = comboDataReconcile;
        //startShowingProgress();
        countAccessoryInfoTobeFetched = 0;
        String title = looksCatTitle;
//        if (looksCatTitle.equals(ConstantsUtil.arrayListLooksLabelName.get(7))) {
//            comboData = getComboDataForLiked(comboDataReconcile);
//        }

        if (looksCatTitle.equals(ConstantsUtil.arrayListLooksLabelName.get(7))
                || looksCatTitle.equals(ConstantsUtil.arrayListLooksLabelName.get(8))) {
            title = comboData.getCombo_Style_Category();
        }
        currentClickedComboData.setLooksCategoryTitle(title);
        checkComboDetailPopulated(comboData);
    }


    private void checkComboDetailPopulated(ComboData comboData) {
        if (countAccessoryInfoTobeFetched == 0) {
            if (comboData.isDownloadedTempStatus()) {
                getComboInfoWithDownloads(comboData);
            } else {
                if (!Connectivity.isConnected(getActivity())) {
                    stopShowingProgress();
                    InkarneAppContext.showNetworkAlert();
                    return;
                }
                //TODO wifi
                //getComboInfoWithDownloads(comboData);
                if (Connectivity.isConnectedWifi(getActivity()) || !InkarneAppContext.getSettingIsWifiOnlyDownload()) {
                    getComboInfoWithDownloads(comboData);
                }else{
                    stopShowingProgress();
                    Toast.makeText(getActivity().getApplicationContext(),"Please disable \"download on wifi only\" mode to use mobile data",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private ComboData getComboDataForLiked(ComboData combo) {
        ComboData comboData = combo;
        ComboData comboDataLike = datasource.getComboLikeDataByComboID(combo.getCombo_ID());
        if (comboDataLike == null || comboDataLike.getCombo_ID() == null)
            return combo;

        comboData = datasource.getComboDataByComboID(combo.getCombo_ID());
        //if (!comboData.getPbId().equals(comboDataLike.getPbId()))//TODO
        //   return combo;

        //comboData.setLooksCategoryTitle(combo.getLooksCategoryTitle());
        countAccessoryInfoTobeFetched = 0;
        int a6 = 0, a7 = 0, a8 = 0, a9 = 0;
        if (comboDataLike.getmSKU_ID6() != null && !comboDataLike.getmSKU_ID6().isEmpty()) {
            comboData.setmSKU_ID6(comboDataLike.getmSKU_ID6());
            comboData.setmA6_Category(comboDataLike.getmA6_Category());
            comboData.setmA6_PIC_Png_Key_Name(comboDataLike.getmA6_PIC_Png_Key_Name());
            if (comboDataLike.getmA6_Obj_Key_Name() != null && !comboDataLike.getmA6_Obj_Key_Name().isEmpty()) {
                comboData.setmA6_Obj_Key_Name(comboDataLike.getmA6_Obj_Key_Name());
                comboData.setmA6_Png_Key_Name(comboDataLike.getmA6_Png_Key_Name());
                comboData.setObjA6DStatus(comboDataLike.getObjA6DStatus());
                comboData.setTextureA6DStatus(comboDataLike.getTextureA6DStatus());
            } else {
                countAccessoryInfoTobeFetched++;
                a6 = 1;
            }
        } else if (comboDataLike.getmSKU_ID6() != null && comboDataLike.getmSKU_ID6().equals("NA")) {
            comboData.setmSKU_ID6("");
            comboData.setmA6_Category("");
            comboData.setmA6_Obj_Key_Name("");
            comboData.setmA6_Png_Key_Name("");
            comboData.setmA6_PIC_Png_Key_Name("");
        }
        if (comboDataLike.getmSKU_ID7() != null && !comboDataLike.getmSKU_ID7().isEmpty()) {
            comboData.setmSKU_ID7(comboDataLike.getmSKU_ID7());
            comboData.setmA7_Category(comboDataLike.getmA7_Category());
            comboData.setmA7_PIC_Png_Key_Name(comboDataLike.getmA7_PIC_Png_Key_Name());
            if (comboDataLike.getmA7_Obj_Key_Name() != null && !comboDataLike.getmA7_Obj_Key_Name().isEmpty()) {
                comboData.setmA7_Obj_Key_Name(comboDataLike.getmA7_Obj_Key_Name());
                comboData.setmA7_Png_Key_Name(comboDataLike.getmA7_Png_Key_Name());
            } else {
                countAccessoryInfoTobeFetched++;
                a7 = 1;
            }

            if (comboDataLike.getLegId() != null) {
                comboData.setLegId(comboDataLike.getLegId());
                BaseAccessoryItem item = datasource.getAccessory(ConstantsUtil.EAccessoryType.eAccTypeLegs.toString(), comboData.getLegId());
                if (item != null) {
                    comboData.setLegItem(item);
                }
            }


            if (comboDataLike.getLegObjAwsKey() != null && !comboDataLike.getLegObjAwsKey().isEmpty()
                    && comboDataLike.getLegTextureAwsKey() != null && !comboDataLike.getLegTextureAwsKey().isEmpty()) {
//                BaseAccessoryItem item = datasource.getAccessory(ConstantsUtil.EAccessoryType.eAccTypeLegs.toString(), comboData.getLegId());
//                if (item != null) {
//                    comboData.setLegItem(item);
//                } else {
//                    Log.e(LOGTAG, "******** Leg Obj not found 2 ***********");
//                }
            } else {
                if (!(comboData.getLegId() == null || comboData.getLegId().isEmpty() || comboData.getLegId().equals("NA"))) {
                    countAccessoryInfoTobeFetched++;
                    a7 = 1;
                }
            }
        }

        if (comboDataLike.getmSKU_ID8() != null && !comboDataLike.getmSKU_ID8().isEmpty()) {
            comboData.setmSKU_ID8(comboDataLike.getmSKU_ID8());
            comboData.setmA8_Category(comboDataLike.getmA8_Category());
            comboData.setmA8_PIC_Png_Key_Name(comboDataLike.getmA8_PIC_Png_Key_Name());
            if (comboDataLike.getmA8_Obj_Key_Name() != null && !comboDataLike.getmA8_Obj_Key_Name().isEmpty()) {
                comboData.setmA8_Obj_Key_Name(comboDataLike.getmA8_Obj_Key_Name());
                comboData.setmA8_Png_Key_Name(comboDataLike.getmA8_Png_Key_Name());
            } else {
                countAccessoryInfoTobeFetched++;
                a8 = 1;
            }
        }

        if (comboDataLike.getmSKU_ID9() != null && !comboDataLike.getmSKU_ID9().isEmpty()) {
            comboData.setmSKU_ID9(comboDataLike.getmSKU_ID9());
            comboData.setmA9_Category(comboDataLike.getmA9_Category());
            comboData.setmA9_PIC_Png_Key_Name(comboDataLike.getmA9_PIC_Png_Key_Name());
            if (comboDataLike.getmA9_Obj_Key_Name() != null && !comboDataLike.getmA9_Obj_Key_Name().isEmpty()) {
                comboData.setmA9_Obj_Key_Name(comboDataLike.getmA9_Obj_Key_Name());
                comboData.setmA9_Png_Key_Name(comboDataLike.getmA9_Png_Key_Name());

            } else {
                countAccessoryInfoTobeFetched++;
                a9 = 1;
            }
        } else if (comboDataLike.getmSKU_ID9() != null && comboDataLike.getmSKU_ID9().equals("NA")) {
            comboData.setmSKU_ID9("");
            comboData.setmA9_Category("");
            comboData.setmA9_Obj_Key_Name("");
            comboData.setmA9_Png_Key_Name("");
            comboData.setmA9_PIC_Png_Key_Name("");
        }
        if (comboDataLike.getmSKU_ID10() != null && !comboDataLike.getmSKU_ID10().isEmpty()) {
            comboData.setmSKU_ID10(comboDataLike.getmSKU_ID10());
            comboData.setmA10_Category(comboDataLike.getmA10_Category());
            comboData.setmA10_Obj_Key_Name(comboDataLike.getmA10_Obj_Key_Name());
            comboData.setmA10_Png_Key_Name(comboDataLike.getmA10_Png_Key_Name());
            comboData.setmA10_PIC_Png_Key_Name(comboDataLike.getmA10_PIC_Png_Key_Name());
        } else if (comboDataLike.getmSKU_ID10() != null && comboDataLike.getmSKU_ID10().equals("NA")) {
            comboData.setmSKU_ID10("");
            comboData.setmA10_Category("");
            comboData.setmA10_Obj_Key_Name("");
            comboData.setmA10_Png_Key_Name("");
            comboData.setmA10_PIC_Png_Key_Name("");
        }

        if (a6 == 1) {
            BaseAccessoryItem item = new BaseAccessoryItem(comboDataLike.getmSKU_ID6(), comboDataLike.getmA6_Category(), false);
            item.setAccessoryType(ConstantsUtil.EAccessoryType.eAccTypeEarrings.toString());
            getOpenGLKey(item, comboData, INDEX_DOWNLOAD_A6);
        }
        if (a7 == 1) {
            BaseAccessoryItem shoesItem = new BaseAccessoryItem(comboDataLike.getmSKU_ID7(), comboDataLike.getmA7_Category(), false);
            shoesItem.setAccessoryType(ConstantsUtil.EAccessoryType.eAccTypeShoes.toString());
            shoesItem.setObjId2(comboData.getLegId());
            shoesItem.setAccessoryType2(ConstantsUtil.EAccessoryType.eAccTypeLegs.toString());
            getOpenGLKeyForShoesLegs(shoesItem, comboData, INDEX_DOWNLOAD_A7);
        }

        if (a8 == 1) {
            BaseAccessoryItem item = new BaseAccessoryItem(comboDataLike.getmSKU_ID8(), comboDataLike.getmA8_Category(), false);
            item.setAccessoryType(ConstantsUtil.EAccessoryType.eAccTypeHair.toString());
            getOpenGLKey(item, comboData, INDEX_DOWNLOAD_A8);
        }
        if (a9 == 1) {
            BaseAccessoryItem item = new BaseAccessoryItem(comboDataLike.getmSKU_ID9(), comboDataLike.getmA9_Category(), false);
            item.setAccessoryType(ConstantsUtil.EAccessoryType.eAccTypeSunglasses.toString());
            getOpenGLKey(item, comboData, INDEX_DOWNLOAD_A9);
        }
        return comboData;
    }


    //@Override
    protected String getCreateAccessoryURL(BaseAccessoryItem item) {
        //String url = "http://inkarnepub-prod.elasticbeanstalk.com/Service1.svc/CreateHairstyle/5/36/MHS002/m";
        String urlMethod = null;
        String uri = null;
        if (item.getAccessoryType().equals(ConstantsUtil.EAccessoryType.eAccTypeLegs.toString())) {
            User user = User.getInstance();//TODO
            uri = ConstantsUtil.URL_BASEPATH_CREATE_V2 + ConstantsUtil.URL_METHOD_CREATE_LEGS
                    + user + "/"
                    + user.getDefaultFaceItem().getFaceId() + "/"
                    + user.getDefaultFaceItem().getPbId();
            Log.d(LOGTAG, uri);
            return uri;
        }
        if (item.getAccessoryType().equals(ConstantsUtil.EAccessoryType.eAccTypeShoes.toString())) {
            uri = ConstantsUtil.URL_BASEPATH + ConstantsUtil.URL_METHOD_CREATE_SHOES + User.getInstance().getmUserId() + "/" + User.getInstance().getPBId() + "/" + item.getObjId();//+ "/" + User.getInstance().getmGender();
            Log.d(LOGTAG, uri);
            return uri;
        }
        if (item.getAccessoryType().equals(ConstantsUtil.EAccessoryType.eAccTypeEarrings.toString())) {
            urlMethod = ConstantsUtil.URL_METHOD_CREATE_EARRINGS;
        } else if (item.getAccessoryType().equals(ConstantsUtil.EAccessoryType.eAccTypeSunglasses.toString())) {
            urlMethod = ConstantsUtil.URL_METHOD_CREATE_SUNGLASSES;
        } else if (item.getAccessoryType().equals(ConstantsUtil.EAccessoryType.eAccTypeSpecs.toString())) {
            urlMethod = ConstantsUtil.URL_METHOD_CREATE_SPECS;
        } else if (item.getAccessoryType().equals(ConstantsUtil.EAccessoryType.eAccTypeHair.toString())) {
            urlMethod = ConstantsUtil.URL_METHOD_CREATE_HAIRSTYLE;
        }
        uri = ConstantsUtil.URL_BASEPATH + urlMethod + User.getInstance().getmUserId() + "/" + User.getInstance().getDefaultFaceItem().getFaceId() + "/" + item.getObjId() + "/" + User.getInstance().getmGender();

        Log.d(LOGTAG, uri);
        return uri;

    }


    private void updateComboFromAccessoryItem(BaseAccessoryItem accItem, ComboData comboData, int index) {
        switch (index) {
            case INDEX_DOWNLOAD_A6: {
                comboData.setmA6_Obj_Key_Name(accItem.getObjAwsKey());
                comboData.setmA6_Png_Key_Name(accItem.getTextureAwsKey());
            }
            break;
            case INDEX_DOWNLOAD_A7: {
                comboData.setmA7_Obj_Key_Name(accItem.getObjAwsKey());
                comboData.setmA7_Png_Key_Name(accItem.getTextureAwsKey());
                if (accItem.dependentItem != null) {
                    comboData.setLegItem(accItem.dependentItem);
                    comboData.setLegId(accItem.getObjId2());
                }
            }
            break;
            case INDEX_DOWNLOAD_A8: {
                comboData.setmA8_Obj_Key_Name(accItem.getObjAwsKey());
                comboData.setmA8_Png_Key_Name(accItem.getTextureAwsKey());
            }
            break;
            case INDEX_DOWNLOAD_A9: {
                comboData.setmA9_Obj_Key_Name(accItem.getObjAwsKey());
                comboData.setmA9_Png_Key_Name(accItem.getTextureAwsKey());
            }
            break;
            default:
                break;
        }
    }

    private void getOpenGLKey(final BaseAccessoryItem item, final ComboData comboData, final int index) {
        BaseAccessoryItem accItem = datasource.getAccessory(item.getAccessoryType(), item.getObjId());
        if (accItem.getObjAwsKey() != null && accItem.getObjAwsKey().length() != 0 &&
                accItem.getTextureAwsKey() != null && accItem.getTextureAwsKey().length() != 0 && !accItem.getObjAwsKey().equals("null")) {
            updateComboFromAccessoryItem(item, comboData, index);
            countAccessoryInfoTobeFetched--;
            checkComboDetailPopulated(comboData);
        } else {
            String uri = getCreateAccessoryURL(item);
            DataManager.getInstance().requestCreateAccessory(uri, item, false, new DataManager.OnResponseHandlerInterface() {
                @Override
                public void onResponse(Object obj) {
                    BaseItem accItem = (BaseItem) obj;
                    updateComboFromAccessoryItem(item, comboData, index);
                    countAccessoryInfoTobeFetched--;
                    checkComboDetailPopulated(comboData);
                }

                @Override
                public void onResponseError(String errorMessage, int errorCode) {

                }
            });
        }
    }

    private void getOpenGLKeyForShoesLegs(final BaseAccessoryItem item, final ComboData comboData, final int index) {
        BaseAccessoryItem accItem = datasource.getAccessory(item.getAccessoryType(), item.getObjId());
        if (accItem != null && accItem.getObjAwsKey() != null && accItem.getObjAwsKey().length() != 0 &&
                accItem.getTextureAwsKey() != null && accItem.getTextureAwsKey().length() != 0 && !accItem.getObjAwsKey().equals("null")) {
            getOpenGLKeyForDependentItem(item, comboData, index);
            updateComboFromAccessoryItem(item, comboData, index);
            countAccessoryInfoTobeFetched--;
            checkComboDetailPopulated(comboData);
        } else {
            String uri = getCreateAccessoryURL(item);
            DataManager.getInstance().requestCreateAccessory(uri, item, false, new DataManager.OnResponseHandlerInterface() {
                @Override
                public void onResponse(Object obj) {
                    //BaseItem accItem = (BaseItem) obj;
                    getOpenGLKeyForDependentItem(item, comboData, index);
                    updateComboFromAccessoryItem(item, comboData, index);
                    //updateComboFromAccessoryItem(accItem, comboData, index);
                    countAccessoryInfoTobeFetched--;
                    checkComboDetailPopulated(comboData);
                }

                @Override
                public void onResponseError(String errorMessage, int errorCode) {

                }
            });
        }
    }


    private void getOpenGLKeyForDependentItem(final BaseAccessoryItem item, final ComboData comboData, final int index) {
        if (item.getObjId2() == null) {
            Log.e(LOGTAG, " item.getObjId2() null ");
            return;
        }
        final BaseAccessoryItem legItem = datasource.getAccessory(ConstantsUtil.EAccessoryType.eAccTypeLegs.toString(), item.getObjId2());
        if (legItem != null && legItem.getObjAwsKey() != null && legItem.getObjAwsKey().length() != 0 &&
                legItem.getTextureAwsKey() != null && legItem.getTextureAwsKey().length() != 0 && !legItem.getObjAwsKey().equals("null")) {
            updateComboFromAccessoryItem(legItem, comboData, index);
            countAccessoryInfoTobeFetched--;
            checkComboDetailPopulated(comboData);
        } else {
            String uri = getCreateAccessoryURL(legItem);
            DataManager.getInstance().requestCreateLegs(uri, new DataManager.OnResponseHandlerInterface() {
                @Override
                public void onResponse(Object obj) {
                    ArrayList<BaseAccessoryItem> list = (ArrayList<BaseAccessoryItem>) obj;
                    for (BaseAccessoryItem itemLeg : list) {
                        if (itemLeg.getObjId().equals(legItem.getObjId())) {
                            item.dependentItem = itemLeg;
                            updateComboFromAccessoryItem(legItem, comboData, index);
                            countAccessoryInfoTobeFetched--;
                            checkComboDetailPopulated(comboData);
                            break;
                        }
                    }
                }

                @Override
                public void onResponseError(String errorMessage, int errorCode) {

                }
            });
        }

    }


    private synchronized void startShowingProgress() {
        pbLooksInfo.setVisibility(View.VISIBLE);
        pbLooksInfo.setLoadingText(getActivity().getString(R.string.message_rendering_looks));
        if (pDialogDownload == null)
            pDialogDownload = getHProgressDialogTranslucent();
        pDialogDownload.setTitle(getActivity().getString(R.string.message_rendering_looks));
        pDialogDownload.setIndeterminate(true);
        pDialogDownload.show();
        pDialogDownload.setProgress(0);
        if(pDialogDownload.getButton(DialogInterface.BUTTON_NEGATIVE) != null)
            pDialogDownload.getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.VISIBLE);
        isDownloadCancelled = false;
    }

    private synchronized void stopShowingProgress() {
        if(pbLooksInfo != null)
        pbLooksInfo.setVisibility(View.INVISIBLE);
        if (pDialogDownload != null ) {
            pDialogDownload.setProgress(0);
            pDialogDownload.dismiss();
        }
    }

    public void getComboInfoWithDownloads(ComboData comboDataReconcile) {

        //       isDownloadCancelled = false;
//      pDialogDownload.setProgressNumberFormat(null);
//      pDialogDownload.setProgressPercentFormat(null);
        startShowingProgress();
        new ComboDownloader(getActivity(), comboDataReconcile, new ComboDownloader.OnComboDownloadListener() {
            @Override
            public void onDownload(final ComboData receivedComboData) {

                    if (receivedComboData != null) {
                        boolean isComboLoaded = false;
                        if(hmLoadedCombo != null && hmLoadedCombo.get(receivedComboData.getCombo_ID())!= null)
                        isComboLoaded = hmLoadedCombo.get(receivedComboData.getCombo_ID());
                        else{
                            hmLoadedCombo = new HashMap<>();
                        }
                        Log.w("receiver", "combo received : " + receivedComboData.getCombo_ID() + "  rank :" + receivedComboData.getForced_Rank());
                        if (receivedComboData.getCombo_ID().equals(currentClickedComboData.getCombo_ID()) && !isDownloadCancelled && !isComboLoaded) {
                            Log.w("receiver 2", "combo received : " + receivedComboData.getCombo_ID());
                            hmLoadedCombo.put(receivedComboData.getCombo_ID(), true);
                            if (pDialogDownload != null) {
                                synchronized(this) {

                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (pDialogDownload != null && pDialogDownload.isShowing()) {
                                                pDialogDownload.setCancelable(true);
                                                if (pDialogDownload.getButton(DialogInterface.BUTTON_NEGATIVE) != null)
                                                    pDialogDownload.getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.INVISIBLE);
                                            }
                                            receivedComboData.setLooksCategoryTitle(currentClickedComboData.getLooksCategoryTitle());
                                            currentComboList.set(currentComboIndex, receivedComboData);
                                            mListener.onCollectionFragmentInteraction(receivedComboData, currentComboList, currentComboIndex);
                                            //stopShowingProgress();
                                        }
                                    });

//                                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            receivedComboData.setLooksCategoryTitle(currentClickedComboData.getLooksCategoryTitle());
//                                            currentComboList.set(currentComboIndex, receivedComboData);
//                                            mListener.onLooksFragmentInteraction(receivedComboData, currentComboList, currentComboIndex);
//                                            //stopShowingProgress();
//                                        }
//                                    }, 100);
                                }
                        }
                    }
                }


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

                Toast.makeText(getActivity().getApplicationContext(), "Oops,could not reach our server.Please try again", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onDownloadProgress(String comboId, int percentage) {
                if(currentClickedComboData != null && comboId.equals(currentClickedComboData.getCombo_ID())) {
                    if(pDialogDownload == null){
                        startShowingProgress();
                    }
                    Log.w(LOGTAG, " " + percentage + "%");
                    if (pDialogDownload.isIndeterminate()) {
                        pDialogDownload.setIndeterminate(false);
                    }
                    if (pDialogDownload != null) {
                        if (percentage > 82) {
                            //percentage -= ( new Random().nextInt(2));//todo hack
                            //pDialogDownload.setProgress(percentage);
                            animateProgressIncrement();
                        } else
                            pDialogDownload.setProgress(percentage);
                    }
                }
            }

            @Override
            public void onComboInfoFailed(String comboId, int error_code) {
                stopShowingProgress();
                if (error_code == DataManager.CODE_DATA_MANAGER_NETWORK_ERROR) {
                    //InkarneAppContext.showNetworkAlert();
                    Toast.makeText(getActivity().getApplicationContext(), getString(R.string.message_network_failure), Toast.LENGTH_SHORT).show();
                } else {
                    //InkarneAppContext.showAlert("Looks info not available");
                    Toast.makeText(getActivity().getApplicationContext(), "Oops,could not reach our server.Please try again", Toast.LENGTH_SHORT).show();
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

//    public void startSkusDownloadService(final ComboData comboReconcile) {
//        Log.d(LOGTAG, "loadCombo " + comboReconcile.getCombo_ID());
//        ComboData comboData = datasource.getComboDataByComboID(comboReconcile.getCombo_ID());
//
//        if (comboData != null && comboData.getmA1_Obj_Key_Name() != null && comboData.getmA1_Obj_Key_Name().length() != 0) {
//            Log.d(LOGTAG, "loadCombo 2 " + comboReconcile.getCombo_ID());
//            getComboDownloads(comboData);
//        } else {
//            DataManager.getInstance().requestComboDataInfoById(comboReconcile.getCombo_ID(), User.getInstance().getDefaultFaceId(),
//                    new DataManager.OnResponseHandlerInterface() {
//                        @Override
//                        public void onResponse(Object obj) {
//                            ArrayList<ComboData> arrayListCombo = (ArrayList<ComboData>) obj;
//                            if (arrayListCombo != null && arrayListCombo.size() > 0) {
//                                ComboData comboData = (ComboData) arrayListCombo.get(0);
//                                comboData = datasource.getComboDataByComboID(comboData.getCombo_ID());
//                                getComboDownloads(comboData);
//                            } else {
//                                Toast.makeText(getActivity(), "Looks Info not available ", Toast.LENGTH_SHORT).show();
//                                pbLooksInfo.setVisibility(View.INVISIBLE);
//                            }
//                        }
//
//                        @Override
//                        public void onResponseError(String errorMessage, int errorCode) {
//                            Log.e(LOGTAG, "Error getting comboInfo :" + errorMessage);
//                            Toast.makeText(getActivity(), ConstantsUtil.MESSAGE_TOAST_NETWORK_RESPONSE_FAILED, Toast.LENGTH_SHORT).show();
//                            pbLooksInfo.setVisibility(View.INVISIBLE);
//                        }
//                    });
//        }
//    }

//    private void getComboDownloads1(ComboData receivedComboData) {
//        if (receivedComboData != null) {
//            Log.d("receiver", "combo received : " + receivedComboData.getCombo_ID() + "  rank :" + receivedComboData.getForced_Rank());
//            if (receivedComboData.getCombo_ID().equals(currentClickedComboData.getCombo_ID())) {
//                if (mListener != null) {
//                    receivedComboData.setLooksCategoryTitle(currentClickedComboData.getLooksCategoryTitle());
//                    mListener.onLooksFragmentInteraction(receivedComboData);
//                }
//            }
//        }
//        pbLooksInfo.setVisibility(View.INVISIBLE);
//    }

//    private void getComboDownloads(ComboData comboData) {
//        Log.d(LOGTAG, "getComboDownloads " + comboData.getCombo_ID());
//        Intent intent = new Intent(getActivity(), DownloadIntentService.class);
//        intent.setAction(DownloadIntentService.ACTION_DOWNLOAD_COMBOS);
//        intent.putExtra(DownloadIntentService.EXTRA_PARAM_COMBO, comboData);
//        getActivity().startService(intent);
//    }


//    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            // TODO Auto-generated method stub
//            // Get extra data included in the Intent
//            ComboData receivedComboData = (ComboData) intent.getSerializableExtra("comboData");
//
//            if (receivedComboData != null) {
//                Log.d("receiver", "combo received : " + receivedComboData.getCombo_ID() + "  rank :" + receivedComboData.getForced_Rank());
//                if (receivedComboData.getCombo_ID().equals(currentClickedComboData.getCombo_ID())) {
//                    if (mListener != null) {
//                        receivedComboData.setLooksCategoryTitle(currentClickedComboData.getLooksCategoryTitle());
//                        mListener.onLooksFragmentInteraction(receivedComboData);
//                    }
//                }
//            }
//            pbLooksInfo.setVisibility(View.INVISIBLE);
//        }
//    };

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
        //void onLooksFragmentInteraction(ComboData combodata);
        void onCollectionFragmentInteraction(ComboData combodata,ArrayList<ComboData> combolist,int index);
    }
}
