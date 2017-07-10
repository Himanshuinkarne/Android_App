package com.svc.sml.Fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.svc.sml.Database.User;
import com.svc.sml.R;
import com.svc.sml.Utility.BMSeekBar;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BMAvatarFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BMAvatarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BMAvatarFragment extends BMFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //private OnLooksFragmentInteractionListener mListener;

    public BMAvatarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BMAvatarFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BMAvatarFragment newInstance(String param1, String param2) {
        BMAvatarFragment fragment = new BMAvatarFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
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
        View v = inflater.inflate(R.layout.fragment_bm_avatar, container, false);
        seekBarChangeListener = new BMSeekBarChangeListener();
        return initView(v);
    }

    public View initView(View v) {
        hipsSeekBar = (BMSeekBar) v.findViewById(R.id.hipsSeekBar);
        hipsSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        bustSeekBar = (BMSeekBar) v.findViewById(R.id.bustSeekBar);
        if (User.getInstance().getmGender().equals("m")) {
            bustSeekBar.setBarBackgroundText("Chest", "Inches");//test
        }
        bustSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        waistSeekBar = (BMSeekBar) v.findViewById(R.id.waistSeekBar);
        waistSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        btnHeightFt = (Button) v.findViewById(R.id.btn_bm_height_ft);
        btnHeightFt.setOnClickListener(this);
        btnHeightIn = (Button) v.findViewById(R.id.btn_bm_height_in);
        btnHeightIn.setOnClickListener(this);
        btnWeight = (Button) v.findViewById(R.id.btn_bm_weight);
        btnWeight.setOnClickListener(this);
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnLooksFragmentInteractionListener) {
//            mListener = (OnLooksFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnLooksFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p/>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnLooksFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentLookLikeInteraction(Uri uri);
//    }
}
