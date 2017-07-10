package com.svc.sml.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.svc.sml.Activity.OnAccessoryAdapterListener;
import com.svc.sml.Adapter.SpecsAdapter;
import com.svc.sml.Database.User;
import com.svc.sml.Helper.DataManager;
import com.svc.sml.Utility.HorizontalListView;
import com.svc.sml.Model.BaseAccessoryItem;
import com.svc.sml.Model.FaceItem;
import com.svc.sml.Model.SpecsContent;
import com.svc.sml.R;
import com.svc.sml.Utility.ConstantsUtil;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class SpecsHListFragment extends BaseAccessoryHListFragment {

    private static final String LOGTAG = SpecsHListFragment.class.toString();
    private ArrayList<SpecsContent> arraySpecs = new ArrayList<SpecsContent>();
    private SpecsAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SpecsHListFragment() {
    }

    // TODO: Customize parameter initialization
    public static SpecsHListFragment newInstance(FaceItem faceItem) {
        SpecsHListFragment fragment = new SpecsHListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_FACE_OBJ, faceItem);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String url = ConstantsUtil.URL_BASEPATH + ConstantsUtil.URL_METHOD_SPECSLIST + User.getInstance().getmUserId() + "/" + User.getInstance().getmGender();
        requestData(url);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_horizontal_list, container, false);
        mHlv = (HorizontalListView) view.findViewById(R.id.lv_mixmatch);

        return view;
    }

    private void setupCustomLists() {
        // Make an array adapter using the built in android layout to render a list of strings
        adapter = new SpecsAdapter(getActivity(), arraySpecs, faceItem, new OnAccessoryAdapterListener() {

            @Override
            public void onAccessorySelected(BaseAccessoryItem item) {
                if (mListener != null) {
                    if (item.getAccessoryType().equals(ConstantsUtil.EAccessoryType.eAccTypeHair.toString())) {
                        mListener.onListFragmentInteractionHairUpdate(item);
                    } else if (item.getAccessoryType().equals(ConstantsUtil.EAccessoryType.eAccTypeSpecs.toString())) {
                        mListener.onListFragmentInteractionSpecsUpdate(item);
                    }
                }
            }

            @Override
            public void onAccessoryClicked(BaseAccessoryItem item) {

            }

        });
        mHlv.setAdapter(adapter);
    }

    private void requestData(String uri) {
        //String url = "http://inkarnepub-prod.elasticbeanstalk.com/Service1.svc/ReconcileSpecsData/4/m"
        DataManager.getInstance().requestSpecsList(uri, false, new DataManager.OnResponseHandlerInterface() {
            @Override
            public void onResponse(Object obj) {
                arraySpecs = (ArrayList<SpecsContent>) obj;
                for (SpecsContent hc : arraySpecs) {
                    if (faceItem.getSpecsId() != null && faceItem.getSpecsId() == hc.item1.getObjId()) {
                        hc.item1.isSelected = true;
                        break;
                    }
                    if (hc.item2 != null) {
                        if (faceItem.getSpecsId() != null && faceItem.getSpecsId() == hc.item2.getObjId()) {
                            hc.item2.isSelected = true;
                            break;
                        }
                    }
                    if (hc.item3 != null) {
                        if (faceItem.getSpecsId() != null && faceItem.getSpecsId() == hc.item3.getObjId()) {
                            hc.item3.isSelected = true;
                            break;
                        }
                    }
                }

                /*
                for (int i = 0; i < 4; i+= 2) {
                    HairItem hItem = new HairItem();
                    hItem.setObjId("MHS00" + i);
                    String str = "hair" + i % 3 + "png";
                    hItem.setThumbnailAwsKey("inkarne/pics/sku/F081MPIC.png");

                    if(i==2){
                        hItem.setThumbnailAwsKey("inkarne/pics/sku/F082MPIC.png");
                    }

                    int index = i+1;
                    HairItem hItem2 = new HairItem();
                    hItem2.setObjId("MHS00"+index);
                    String str2 = "hair" + index % 3 + "png";
                    if(index==5){
                        hItem2.setThumbnailAwsKey("inkarne/pics/sku/F012MPIC.png");
                    }
                    else
                    hItem2.setThumbnailAwsKey("inkarne/pics/sku/F032MPIC.png");

                    HairContent hairContent = new HairContent();
                    hairContent.hairItem1 = hItem;
                    hairContent.hairItem2 = hItem2;
                    arraySpecs.add(hairContent);
                }
                */
                setupCustomLists();
            }

            @Override
            public void onResponseError(String errorMessage, int errorCode) {

            }
        });
    }
}
