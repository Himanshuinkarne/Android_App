package com.svc.sml.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.svc.sml.Adapter.CollectionForyouAdapter;
import com.svc.sml.Database.ComboData;
import com.svc.sml.Database.ComboDataLooksItem;
import com.svc.sml.Database.InkarneDataSource;
import com.svc.sml.Database.User;
import com.svc.sml.Helper.DataManager;
import com.svc.sml.InkarneAppContext;
import com.svc.sml.Model.BaseAccessoryItem;
import com.svc.sml.Model.BaseItem;
import com.svc.sml.R;
import com.svc.sml.Utility.Connectivity;
import com.svc.sml.Utility.ConstantsUtil;
import com.svc.sml.View.LoadingView;

import java.util.ArrayList;

public class LooksFragmentForYou extends BaseLooksFragment implements View.OnClickListener, CollectionForyouAdapter.OnGridAdapterListener {
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
    private OnBaseLookFragmentInteractionListener mListener;
    private ListView gridCollectionView;
    //private ImageButton btnHelp;
    // private ArrayList<Gallery> galleryList = new ArrayList<>();
    private ArrayList<ComboDataLooksItem> listOfComboList = new ArrayList<>();

    private CollectionForyouAdapter collectionAdapter;
    private InkarneDataSource datasource;
    private LoadingView pbLooksInfo;
    public ProgressDialog pBInitData;
    private int countAccessoryInfoTobeFetched = 0;
    //private HashMap<String,Boolean> hmLoadedCombo = new HashMap<>();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public LooksFragmentForYou() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static LooksFragmentForYou newInstance(int columnCount) {
        LooksFragmentForYou fragment = new LooksFragmentForYou();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    public static LooksFragmentForYou newInstance() {
        LooksFragmentForYou fragment = new LooksFragmentForYou();
        return fragment;
    }

    public static LooksFragmentForYou newInstance(ArrayList<ComboData> arrayCombodata) {
        LooksFragmentForYou fragment = new LooksFragmentForYou();
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
        View view = inflater.inflate(R.layout.fragment_looks_foryou, container, false);
        pbLooksInfo = (LoadingView) view.findViewById(R.id.loading_view);
        pbLooksInfo.setVisibility(View.INVISIBLE);
        gridCollectionView = (ListView) view.findViewById(R.id.collectionView);
        datasource = InkarneDataSource.getInstance(getActivity().getApplicationContext());
        datasource.open();
        return view;
    }

    @Override
    public void onResume() {
        Log.w("received", "resume");
        super.onResume();
        hmLoadedCombo.clear();
        new LoadLooksListTask().execute();
    }
    
    @Override
    public void onPause() {
        super.onPause();
    }
    public void onStop(){
        super.onStop();
        stopShowingProgress();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnBaseLookFragmentInteractionListener) {
            mListener = (OnBaseLookFragmentInteractionListener) context;
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
//            if (pBInitData == null)
//                pBInitData = new ProgressDialog(getContext());
//            pBInitData.show();
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
            if(InkarneAppContext.getListOfComboList() != null && InkarneAppContext.getListOfComboList().size() >= ConstantsUtil.arrayListLooksLabelName.size()){
                int sizeEnd = ConstantsUtil.arrayListLooksLabelName.size();
                int sizeStart = ConstantsUtil.arrayListLooksLabelName.size()-3;
                listOfComboList = new ArrayList<>(InkarneAppContext.getListOfComboList().subList(sizeStart,sizeEnd));
            }else {
                InkarneAppContext.getListOfComboList();
                if(InkarneAppContext.getListOfComboList() != null && InkarneAppContext.getListOfComboList().size() >= ConstantsUtil.arrayListLooksLabelName.size()){
                    int sizeEnd = ConstantsUtil.arrayListLooksLabelName.size();
                    int sizeStart = ConstantsUtil.arrayListLooksLabelName.size()-3;
                    listOfComboList = (ArrayList<ComboDataLooksItem>) InkarneAppContext.getListOfComboList().subList(sizeStart,sizeEnd);
                }
            }
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
            collectionAdapter = new CollectionForyouAdapter(getActivity(), listOfComboList, LooksFragmentForYou.this);
            gridCollectionView.setAdapter(collectionAdapter);
        }
    }

    @Override
    public void onClick(View v) {

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
                    if(getActivity() != null)
                    Toast.makeText(getActivity(),"Please disable \"download on wifi only\" mode to use mobile data",Toast.LENGTH_SHORT).show();
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
}
