package com.svc.sml.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.svc.sml.Activity.OnAccessoryAdapterListener;
import com.svc.sml.Adapter.HairAdapter;
import com.svc.sml.Database.User;
import com.svc.sml.Helper.DataManager;
import com.svc.sml.Utility.HorizontalListView;
import com.svc.sml.Model.BaseAccessoryItem;
import com.svc.sml.Model.FaceItem;
import com.svc.sml.Model.HairContent;
import com.svc.sml.R;
import com.svc.sml.Utility.ConstantsUtil;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class HairHListFragment extends BaseAccessoryHListFragment {

    private static final String LOGTAG = HairHListFragment.class.toString();
    // TODO: Customize parameter argument names
    private int mColumnCount = 1;

    private ArrayList<HairContent> arrayHair = new ArrayList<HairContent>();
    private HairAdapter adapter;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HairHListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static HairHListFragment newInstance(int columnCount) {
        HairHListFragment fragment = new HairHListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    public static HairHListFragment newInstance(FaceItem faceItem) {
        HairHListFragment fragment = new HairHListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_FACE_OBJ, faceItem);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String url = ConstantsUtil.URL_BASEPATH+ConstantsUtil.URL_METHOD_HAIRLIST+ User.getInstance().getmUserId()+"/"+ User.getInstance().getmGender();
        requestData(url);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_horizontal_list, container, false);
        mHlv = (HorizontalListView) view.findViewById(R.id.lv_mixmatch);

        // Set the adapter
//        if (view instanceof RecyclerView) {
//            Context context = view.getContext();
//            RecyclerView recyclerView = (RecyclerView) view;
//            if (mColumnCount <= 1) {
//                recyclerView.setLayoutManager(new LinearLayoutManager(context));
//            } else {
//                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
//            }
//            recyclerView.setAdapter(new FaceObjectRecyclerViewAdapter(HairContent.ITEMS, mListener));
//        }
        return view;
    }

    private void setupCustomLists() {
        // Make an array adapter using the built in android layout to render a list of strings
         adapter = new HairAdapter(getActivity(), arrayHair, faceItem, new OnAccessoryAdapterListener() {
             @Override
             public void onAccessorySelected(BaseAccessoryItem item) {
                 if(mListener != null){
                     if(item.getAccessoryType().equals(ConstantsUtil.EAccessoryType.eAccTypeHair.toString()))
                     mListener.onListFragmentInteractionHairUpdate(item);
                     if(item.getAccessoryType().equals(ConstantsUtil.EAccessoryType.eAccTypeSpecs.toString()))
                         mListener.onListFragmentInteractionSpecsUpdate(item);
                 }

             }

             @Override
             public void onAccessoryClicked(BaseAccessoryItem item) {

             }

         });
         mHlv.setAdapter(adapter);
    }

    public void requestData(String uri){
        //String url = "http://inkarneweb-prod.elasticbeanstalk.com/Service1.svc/GetHairstyleInfo/m";
       DataManager.getInstance().requestHairstyleList(uri, false,new DataManager.OnResponseHandlerInterface() {
            @Override
            public void onResponse(Object obj) {
                arrayHair = (ArrayList<HairContent>) obj;
                for (HairContent hc : arrayHair) {
                    if (faceItem.getHairstyleId() != null && faceItem.getHairstyleId() == hc.hairItem1.getObjId()) {
                        hc.hairItem1.isSelected = true;
                        break;
                    }
                    if (hc.hairItem2 != null) {
                        if (faceItem.getHairstyleId() != null  && faceItem.getHairstyleId() == hc.hairItem2.getObjId()) {
                            hc.hairItem2.isSelected = true;
                            break;
                        }
                    }
                }
                setupCustomLists();
            }

            @Override
            public void onResponseError(String errorMessage, int errorCode) {

            }
        });
    }
}
