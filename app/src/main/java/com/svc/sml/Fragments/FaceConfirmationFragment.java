package com.svc.sml.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.svc.sml.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFaceConfirmationFragmentListener} interface
 * to handle interaction events.
 * Use the {@link FaceConfirmationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FaceConfirmationFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private ImageButton ibBad;
    private ImageButton ibOkay;
    private ImageButton ibGreat;
    protected OnFaceConfirmationFragmentListener mListener;

    public FaceConfirmationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FaceConfirmationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FaceConfirmationFragment newInstance() {
        FaceConfirmationFragment fragment = new FaceConfirmationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_face_confirmation, container, false);
        ibBad = (ImageButton)v.findViewById(R.id.ib_face_confirm_bad);
        ibBad.setOnClickListener(this);
        ibOkay = (ImageButton)v.findViewById(R.id.ib_face_confirm_okay);
        ibOkay.setOnClickListener(this);
        ibGreat = (ImageButton)v.findViewById(R.id.ib_face_confirm_great);
        ibGreat.setOnClickListener(this);
        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFaceConfirmationFragmentListener) {
            mListener = (OnFaceConfirmationFragmentListener) context;
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ib_face_confirm_bad:
            case R.id.ib_face_confirm_okay:

            case R.id.ib_face_confirm_great:{
                if (mListener != null) {
                    mListener.onFaceConfirmationFragmentInteraction(v.getId());
                }
            }
            break;
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
    public interface OnFaceConfirmationFragmentListener {
        // TODO: Update argument type and name
        void onFaceConfirmationFragmentInteraction(int i);
    }
}
