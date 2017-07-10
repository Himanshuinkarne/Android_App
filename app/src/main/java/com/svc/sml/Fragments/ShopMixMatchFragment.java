package com.svc.sml.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.svc.sml.Activity.OnAccessoryAdapterListener;
import com.svc.sml.Adapter.MixMatchAdapter;
import com.svc.sml.Database.User;
import com.svc.sml.Helper.DataManager;
import com.svc.sml.InkarneAppContext;
import com.svc.sml.MixMatchSharedResource;
import com.svc.sml.Model.BaseAccessoryItem;
import com.svc.sml.Model.FaceItem;
import com.svc.sml.R;
import com.svc.sml.Utility.ConstantsUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShopMixMatchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShopMixMatchFragment extends BaseFragment {

    private static final String LOGTAG = "ShopMixMatchFragment";
    private static final List<String> listMixMatchFemaleCategory = Arrays.asList(
            ConstantsUtil.EAccessoryType.eAccTypeBags.toString(),
            ConstantsUtil.EAccessoryType.eAccTypeClutches.toString(),
            ConstantsUtil.EAccessoryType.eAccTypeEarrings.toString(),
            ConstantsUtil.EAccessoryType.eAccTypeSunglasses.toString(),
            ConstantsUtil.EAccessoryType.eAccTypeShoes.toString(),
            ConstantsUtil.EAccessoryType.eAccTypeHair.toString(),
            ConstantsUtil.EAccessoryType.eAccTypeSpecs.toString());
    private static final int[] listMixMatchFemaleIcons = {
            R.drawable.selector_mm_bags_female_touch,
            R.drawable.selector_mm_clutches_female_touch,
            R.drawable.selector_mm_earrings_female_touch,
            R.drawable.selector_mm_sunglasses_female_touch,
            R.drawable.selector_mm_shoes_female_touch,
            R.drawable.selector_mm_hair_female_touch,
            R.drawable.selector_mm_specs_female_touch};

    private static final List<String> listMixMatchMaleCategory = Arrays.asList(
            ConstantsUtil.EAccessoryType.eAccTypeBags.toString(),
            ConstantsUtil.EAccessoryType.eAccTypeSunglasses.toString(),
            ConstantsUtil.EAccessoryType.eAccTypeShoes.toString(),
            ConstantsUtil.EAccessoryType.eAccTypeHair.toString(),
            ConstantsUtil.EAccessoryType.eAccTypeSpecs.toString());

    private static final int[] listMixMatchMaleIcons = {
            R.drawable.selector_mm_bags_male_touch,
            R.drawable.selector_mm_sunglasses_male_touch,
            R.drawable.selector_mm_shoes_male_touch,
            R.drawable.selector_mm_hair_male_touch,
            R.drawable.selector_mm_specs_male_touch
    };

    private ArrayList<BaseAccessoryItem> arrayAccessories = new ArrayList<BaseAccessoryItem>();
    private List<String> arrayCategory = new ArrayList<>();
    private int[] arrayIcons;
    private MixMatchAdapter adapterSkuList;
    private AdapterCategory adapterCategory;
    private String selectedAccType;
    protected ListView listView;
    private ProgressBar pbMixMatchList;
    protected ImageButton ibBack, ibRemoveAccessory, ibSetDefault;
    protected LinearLayout conButtons;

    protected OnMixMatchFragmentInteractionListener mMixMatchListener;
    private FaceItem faceItem;
    private int countRetryHairstyle = 0;
    private int countRetrySpecs = 0;

    public interface OnMixMatchFragmentInteractionListener {
        // TODO: Update argument type and name
        void onMixMatchFragmentInteraction(BaseAccessoryItem item);

        void onMixMatchFragmentInteractionSetDefault(BaseAccessoryItem item);

        void onMixMatchFragmentInteractionRemoveAccessory(String item);
        //void onMixMatchFragmentInteractionRemoveAccessory(String item,String);
    }

    public FaceItem getFaceItem() {
        return faceItem;
    }

    public void setFaceItem(FaceItem faceItem) {
        this.faceItem = faceItem;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ShopMixMatchFragment() {

    }


    //TODO: Customize parameter initialization
    public static ShopMixMatchFragment newInstance(FaceItem faceItem) {
        ShopMixMatchFragment fragment = new ShopMixMatchFragment();
        Bundle args = new Bundle();
        args.putSerializable("ARG_FACE_OBJ", faceItem);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && faceItem == null) {
            faceItem = (FaceItem) getArguments().getSerializable("ARG_FACE_OBJ");
        }
        //GATrackActivity(LOGTAG);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        if (getArguments() != null && faceItem == null) {
//            faceItem = (FaceItem) getArguments().getSerializable("ARG_FACE_OBJ");
//        }
        if (User.getInstance().getmGender().equals("m")) {
            arrayIcons = listMixMatchMaleIcons;
            arrayCategory = listMixMatchMaleCategory;
        } else {
            arrayIcons = listMixMatchFemaleIcons;
            arrayCategory = listMixMatchFemaleCategory;
        }
        View view = inflater.inflate(R.layout.fragment_shop_mix_match, container, false);
        pbMixMatchList = (ProgressBar) view.findViewById(R.id.pb_mixmatch_list_data);
        listView = (ListView) view.findViewById(R.id.lv_mixmatch);
        conButtons = (LinearLayout) view.findViewById(R.id.con_mixmatch_buttons);
        ibBack = (ImageButton) view.findViewById(R.id.btn_mixmatch_back);
        ibRemoveAccessory = (ImageButton) view.findViewById(R.id.btn_mixmatch_cross);
        ibSetDefault = (ImageButton) view.findViewById(R.id.btn_mixmatch_set_default);
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populateCategory();
            }
        });
        ibRemoveAccessory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMixMatchListener.onMixMatchFragmentInteractionRemoveAccessory(selectedAccType);
                updateButtonVisibility(selectedAccType);
            }
        });
        ibSetDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MixMatchSharedResource.getInstance().currentSelectedItem != null) {
                    setDefaultAccessory(MixMatchSharedResource.getInstance().currentSelectedItem);
                    updateButtonVisibility(selectedAccType);
                }
            }
        });
        populateCategory();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(faceItem == null){
            faceItem = User.getInstance().getDefaultFaceItem();
        }
        countRetrySpecs = 0;
        countRetryHairstyle = 0;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMixMatchFragmentInteractionListener) {
            mMixMatchListener = (OnMixMatchFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mMixMatchListener = null;
    }

    public void setDefaultAccessory(final BaseAccessoryItem item) {
        if(item == null)
            return;
        if ( item.getAccessoryType().equals(ConstantsUtil.EAccessoryType.eAccTypeHair.toString())) {
            faceItem.setHairstyleId(item.getObjId());
            faceItem.setHairPngKey(item.getTextureAwsKey());
            faceItem.setHairObjkey(item.getObjAwsKey());
            Toast.makeText(getActivity(), "Set as your hairstyle!", Toast.LENGTH_SHORT).show();
            updateDefaultHairstyleToServer();
        } else if (item.getAccessoryType().equals(ConstantsUtil.EAccessoryType.eAccTypeSpecs.toString())) {
            faceItem.setSpecsId(item.getObjId());
            faceItem.setSpecsPngkey(item.getTextureAwsKey());
            faceItem.setSpecsObjkey(item.getObjAwsKey());
            Toast.makeText(getActivity(), "Set as your specs!", Toast.LENGTH_SHORT).show();
            updateDfaultSpecsToServer();
        }
        InkarneAppContext.getDataSource().create(faceItem);
        User.getInstance().setDefaultFaceItem(faceItem);
        InkarneAppContext.getDataSource().create(User.getInstance());
    }

    private void updateDefaultHairstyleToServer() {
        String url = ConstantsUtil.URL_BASEPATH + ConstantsUtil.URL_METHOD_UPDATE_DEFAULT_HAIRSTYLE + User.getInstance().getmUserId() + "/" + faceItem.getHairstyleId();
        DataManager.getInstance().updateDefaultFace(url, new DataManager.OnResponseHandlerInterface() {
            @Override
            public void onResponse(Object obj) {
                countRetryHairstyle = 0;

            }

            @Override
            public void onResponseError(String errorMessage, int errorCode) {
                countRetryHairstyle++;
                if (countRetryHairstyle < ConstantsUtil.COUNT_RETRY_SERVICE_CRITICAL)
                    updateDefaultHairstyleToServer();
            }
        });
    }

    private void updateDfaultSpecsToServer() {
        String url = ConstantsUtil.URL_BASEPATH + ConstantsUtil.URL_METHOD_UPDATE_DEFAULT_SPECS + User.getInstance().getmUserId() + "/" + faceItem.getSpecsId();
        DataManager.getInstance().updateDefaultFace(url, new DataManager.OnResponseHandlerInterface() {
            @Override
            public void onResponse(Object obj) {
                countRetrySpecs = 0;

            }

            @Override
            public void onResponseError(String errorMessage, int errorCode) {
                countRetrySpecs++;
                if (countRetrySpecs < ConstantsUtil.COUNT_RETRY_SERVICE_CRITICAL)
                    updateDfaultSpecsToServer();
            }
        });
    }

    public void setRemovedAccessory(String accessory, String accessoryId) {

    }

    public void setRenderedAccessory(String accessory, String accessoryId) {

    }

    //
    public void accessoryLoadFailed(BaseAccessoryItem item){
        if(adapterSkuList != null){
            adapterSkuList.accessoryLoadFailed(item);
        }
    }

    public void accessoryLoadSuccessful(BaseAccessoryItem item){
        if(adapterSkuList != null){
            adapterSkuList.accessoryLoadSuccessful(item);
        }
    }

    private void populateCategory() {
        //listView.setBackgroundColor(getActivity().getResources().getColor(R.color.color_mix_match_category_list));
        listView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.color_mix_match_category_list));
                pbMixMatchList.setVisibility(View.INVISIBLE);
        conButtons.setVisibility(View.GONE);
        adapterCategory = new AdapterCategory();
        listView.setAdapter(adapterCategory);
    }

    private void updateButtonVisibility(String accessoryType) {
        ibSetDefault.setVisibility(View.GONE);
        if (accessoryType.equals(ConstantsUtil.EAccessoryType.eAccTypeShoes.toString())) {
            ibRemoveAccessory.setVisibility(View.GONE);
        } else if (accessoryType.equals(ConstantsUtil.EAccessoryType.eAccTypeHair.toString())) {
            ibRemoveAccessory.setVisibility(View.GONE);
            String renderedHairId = MixMatchSharedResource.getInstance().renderedHairStyle;
            if (renderedHairId != null && !renderedHairId.isEmpty() && ((faceItem.getFaceId().equals("1") && faceItem.getHairstyleId()== null) || !faceItem.getHairstyleId().equals(renderedHairId))) {
                ibSetDefault.setVisibility(View.VISIBLE);
            }
            //if(faceItem.getHairstyleId().equals(mMixMatchListener.))
        } else if (!MixMatchSharedResource.getInstance().isAccessoryTypeRendered(accessoryType)) {
            ibRemoveAccessory.setVisibility(View.GONE);
        } else {
            ibRemoveAccessory.setVisibility(View.VISIBLE);
            if (accessoryType.equals(ConstantsUtil.EAccessoryType.eAccTypeSpecs.toString())) {
                String renderedSpecsId = MixMatchSharedResource.getInstance().renderedSpecs;

                if (faceItem.getSpecsId() == null
                        || (renderedSpecsId != null && !renderedSpecsId.isEmpty() && ((faceItem.getFaceId().equals("1") && faceItem.getSpecsId() == null)|| !faceItem.getSpecsId().equals(renderedSpecsId)))) {
                    ibSetDefault.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void populateSkuLists(final String accessoryType) {
        //if(accessoryType != selectedAccType)
        listView.setAdapter(null);
        selectedAccType = accessoryType;
        //listView.setBackgroundColor(getActivity().getResources().getColor(R.color.color_mix_match_accessory_list));
        listView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.color_mix_match_accessory_list));
        pbMixMatchList.setVisibility(View.VISIBLE);
        conButtons.setVisibility(View.VISIBLE);
        ibBack.setVisibility(View.VISIBLE);
        updateButtonVisibility(accessoryType);

        DataManager.getInstance().getAccessories(accessoryType, false, new DataManager.OnResponseHandlerInterface() {
            @Override
            public void onResponse(Object obj) {
                arrayAccessories = (ArrayList<BaseAccessoryItem>) obj;
                pbMixMatchList.setVisibility(View.INVISIBLE);
                adapterSkuList = new MixMatchAdapter(getActivity(), arrayAccessories, faceItem, new OnAccessoryAdapterListener() {

                    @Override
                    public void onAccessorySelected(BaseAccessoryItem item) {
                        mMixMatchListener.onMixMatchFragmentInteraction(item);
                        updateButtonVisibility(item.getAccessoryType());
                    }

                    @Override
                    public void onAccessoryClicked(BaseAccessoryItem item) {
                        trackEvent("mixmatch",item.getObjId(),"");
                    }
                });
                listView.setAdapter(adapterSkuList);
            }

            @Override
            public void onResponseError(String errorMessage, int errorCode) {
                if (errorCode == DataManager.CODE_DATA_MANAGER_NETWORK_ERROR) {
                    //populateSkuLists(accessoryType);
                }
            }
        });
    }

    public class AdapterCategory extends BaseAdapter implements View.OnClickListener {

        private LayoutInflater inflater;

        public AdapterCategory() {
            inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return arrayIcons.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ImageView imageView;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_item_mixmatch_category, parent, false);
                //final Context context = parent.getContext();
                imageView = (ImageView) convertView.findViewById(R.id.iv_mixmatch_category);
                //imageView.setLayoutParams(new LinearLayout.LayoutParams((int) 60, (int) 60));
            } else {
                //imageView = (ImageView) convertView;
                imageView = (ImageView) convertView.findViewById(R.id.iv_mixmatch_category);
            }
            final int resId = arrayIcons[position];
            imageView.setImageResource(resId);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO Auto-generated method stub
                    String selectedAccType = arrayCategory.get(position);
                    trackEvent("mixmatch",selectedAccType,"");
                    populateSkuLists(selectedAccType);
                }
            });
            return imageView;
        }

        @Override
        public void onClick(View v) {

        }
    }
}
