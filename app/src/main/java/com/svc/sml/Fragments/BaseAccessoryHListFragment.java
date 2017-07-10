package com.svc.sml.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.svc.sml.Utility.HorizontalListView;
import com.svc.sml.Model.BaseAccessoryItem;
import com.svc.sml.Model.FaceItem;
import com.svc.sml.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BaseAccessoryHListFragment.OnListFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BaseAccessoryHListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BaseAccessoryHListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    protected static final String ARG_FACE_OBJ = "FaceItem";
    protected static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    protected OnListFragmentInteractionListener mListener;

    protected HorizontalListView mHlv;
    protected FaceItem faceItem;

    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteractionHairUpdate(BaseAccessoryItem item);
        void onListFragmentInteractionSpecsUpdate(BaseAccessoryItem item);
    }


    public BaseAccessoryHListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BaseAccessoryHListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BaseAccessoryHListFragment newInstance() {
        BaseAccessoryHListFragment fragment = new BaseAccessoryHListFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            faceItem = (FaceItem) getArguments().getSerializable(ARG_FACE_OBJ);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_base_accessory_hlist, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
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

}
