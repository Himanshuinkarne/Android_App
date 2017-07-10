package com.svc.sml.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.svc.sml.Database.User;
import com.svc.sml.R;
import com.svc.sml.Utility.BMSeekBar;
import com.svc.sml.Utility.ConstantsFunctional;
import com.svc.sml.Utility.ConstantsUtil;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BMFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BMFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BMFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    public  static  final  String LOGTAG = "BMFragment";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    protected OnFragmentInteractionListener mListener;

    BMSeekBarChangeListener seekBarChangeListener;// = new BMSeekBarChangeListener();
    protected BMSeekBar hipsSeekBar;
    protected BMSeekBar bustSeekBar;
    protected BMSeekBar waistSeekBar;
    protected Button btnHeightFt;
    protected Button btnHeightIn;
    protected Button btnWeight;
    protected ImageView ivBGGender;
    public BMModel bmModel = new BMModel();


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
        void onFragmentInteraction(Uri uri);
    }


    public void onWindowFocusChanged(boolean hasFocus){
        setSeekbarLayout();
        bustSeekBar.setVisibility(View.VISIBLE);
        waistSeekBar.setVisibility(View.VISIBLE);
        hipsSeekBar.setVisibility(View.VISIBLE);
    }

    public BMFragment() {
        // Required empty public constructor
    }

    @Override
    public void onClick(View v) {
        clickHandler(v);
    }

    public static class BMModel{
        public BMModel(){

        }
        public  int hipsSize;
        public  int bustSize;
        public  int waistSize;
        public  int weightBM;
        public  float heightBM;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BMFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BMFragment newInstance(String param1, String param2) {
        BMFragment fragment = new BMFragment();
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

        View v = inflater.inflate(R.layout.fragment_bm, container, false);
        Log.d(LOGTAG,"onCreateView");
        seekBarChangeListener = new BMSeekBarChangeListener();
        v = initView(v);
        return v;
    }



    public View initView(View v){
        Log.d(LOGTAG,"initView");
        bustSeekBar  = (BMSeekBar) v.findViewById(R.id.bustSeekBar);
        bustSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        if(User.getInstance().getmGender().equals("m")) {
            bustSeekBar.setBarBackgroundText("Chest", "Inches");//test
        }
        waistSeekBar  = (BMSeekBar)v.findViewById(R.id.waistSeekBar);
        waistSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        hipsSeekBar  = (BMSeekBar) v.findViewById(R.id.hipsSeekBar);
        hipsSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        btnHeightFt = (Button)v.findViewById(R.id.btn_bm_height_ft);
        btnHeightFt.setOnClickListener(this);
        btnHeightIn = (Button)v.findViewById(R.id.btn_bm_height_in);
        btnHeightIn.setOnClickListener(this);
        btnWeight = (Button)v.findViewById(R.id.btn_bm_weight);
        btnWeight.setOnClickListener(this);
        ivBGGender = (ImageView)v.findViewById(R.id.iv_bg_bm_gender);
        if(User.getInstance().getmGender().equals("f")){
            ivBGGender.setImageResource(R.drawable.bg_bm_female);
        }else {
            ivBGGender.setImageResource(R.drawable.bg_bm_male);
        }
        bustSeekBar.setVisibility(View.INVISIBLE);
        waistSeekBar.setVisibility(View.INVISIBLE);
        hipsSeekBar.setVisibility(View.INVISIBLE);
        //setSeekbarLayout();


        int height = 0, weight = 0, bust = 0, waist = 0, hip = 0;
        if (User.getInstance().getmGender().equals("m")) {
            height = ConstantsFunctional.HEIGHT_IN_INCHES_MALE;
            weight = ConstantsFunctional.WEIGHT_IN_KG_MALE;
            bust = ConstantsFunctional.SIZE_BUST_IN_INCHES_MALE;
            waist = ConstantsFunctional.SIZE_WAIST_IN_INCHES_MALE;
            hip = ConstantsFunctional.SIZE_HIP_IN_INCHES_MALE;
        } else {
            height = ConstantsFunctional.HEIGHT_IN_INCHES_FEMALE;
            weight = ConstantsFunctional.WEIGHT_IN_KG_FEMALE;
            bust = ConstantsFunctional.SIZE_BUST_IN_INCHES_FEMALE;
            waist = ConstantsFunctional.SIZE_WAIST_IN_INCHES_FEMALE;
            hip = ConstantsFunctional.SIZE_HIP_IN_INCHES_FEMALE;
        }
        btnHeightFt.setText(String.valueOf(height / 12));
        btnHeightIn.setText(String.valueOf(height % 12));
        btnWeight.setText(String.valueOf(weight));
        bustSeekBar.setProgress(bust);
        waistSeekBar.setProgress(waist);
        hipsSeekBar.setProgress(hip);
        return v;
    }

    private void setSeekbarLayout(){
        //layout = (LinearLayout) findViewById(R.id.layoutbtnlinear_aboutme);
        int h = ivBGGender.getHeight();
        LinearLayout.LayoutParams paramsBust = (LinearLayout.LayoutParams)bustSeekBar.getLayoutParams();
        LinearLayout.LayoutParams paramsWaist = (LinearLayout.LayoutParams)waistSeekBar.getLayoutParams();
        LinearLayout.LayoutParams paramsHip = (LinearLayout.LayoutParams)hipsSeekBar.getLayoutParams();
        //int px = ConstantsUtil.getPxFromDp(50);
        if(User.getInstance().getmGender().equals("f")) {
            int px = h * 34 / 100;
            Log.d(LOGTAG, "bust margin px : " + px);
            paramsBust.setMargins(0, px, 0, 0);

            //int px1 = ConstantsUtil.getPxFromDp(150);
            int px1 = (int) ((float) h * 8 / 100);
            Log.d(LOGTAG, "waist margin px : " + px1);
            paramsWaist.setMargins(0, px1, 0, 0);

            //int px2 = ConstantsUtil.getPxFromDp(150);
            int px2 = h * 7 / 100;
            Log.d(LOGTAG, "hip margin px : " + px2);
            paramsHip.setMargins(0, px2, 0, 0);
        }
        else{
            int px = h * 34 / 100;
            Log.d(LOGTAG, "bust margin px : " + px);
            paramsBust.setMargins(0, px, 0, 0);

            //int px1 = ConstantsUtil.getPxFromDp(150);
            int px1 = (int) ((float) h * 8 / 100);
            Log.d(LOGTAG, "waist margin px : " + px1);
            paramsWaist.setMargins(0, px1, 0, 0);

            //int px2 = ConstantsUtil.getPxFromDp(150);
            int px2 = (int) (h * 6.5 / 100);
            Log.d(LOGTAG, "hip margin px : " + px2);
            paramsHip.setMargins(0, px2, 0, 0);
        }

        bustSeekBar.setLayoutParams(paramsBust);
        waistSeekBar.setLayoutParams(paramsWaist);
        hipsSeekBar.setLayoutParams(paramsHip);
    }

    public void onResume(){
        super.onResume();
        initBM();
        getUpdatedBM();
    }


    private void initBM(){
        User user = User.getInstance();
        if(user.getPBId() != null){
            float ht = user.getHeight();
            Log.e(LOGTAG, "ht: " + ht);
            btnHeightFt.setText(String.valueOf((int) ht / 12));
            btnHeightIn.setText(String.valueOf(ht%12));
            btnWeight.setText(String.valueOf(user.getWeight()));
            Log.e(LOGTAG, "user.getWaist(): " + user.getWaist());
            waistSeekBar.setProgress(user.getWaist());
            Log.e(LOGTAG, "user.getHip(): " + user.getHip());
            hipsSeekBar.setProgress(user.getHip());
            Log.e(LOGTAG, "user.getBust(): " + user.getBust());
            bustSeekBar.setProgress(user.getBust());
        }
    }

    private void getUpdatedBM(){
        Log.e(LOGTAG, "ft: " + btnHeightFt.getText() + " in: " + btnHeightIn.getText());
        if(btnHeightFt.getText() != null)
        bmModel.heightBM = (float)Integer.parseInt(btnHeightFt.getText().toString()) * 12 + Float.parseFloat(btnHeightIn.getText().toString());
        bmModel.weightBM = Integer.parseInt(btnWeight.getText().toString());
        bmModel.hipsSize = hipsSeekBar.getProgress();
        bmModel.waistSize = waistSeekBar.getProgress();
        bmModel.bustSize = bustSeekBar.getProgress();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

    private void clickHandler(View v) {
        String title = null;
        final ArrayList<String> entries = new ArrayList<String>();
        final int viewId = v.getId();
        switch (viewId) {
            case R.id.btn_bm_height_ft: {
                entries.addAll(Arrays.asList(ConstantsUtil.ARRAY_HEIGHT_FT));
                title = "Height in Ft.";
            }
            break;
            case R.id.btn_bm_height_in: {
                entries.addAll(Arrays.asList(ConstantsUtil.ARRAY_HEIGHT_INCH));
                title = "Height in In.";
            }
            break;
            case R.id.btn_bm_weight: {
                entries.addAll(Arrays.asList(ConstantsUtil.ARRAY_WEIGHT));
                title = "Weight in Kg.";
            }
            break;
            default:
                break;
        }
        int selected = 0;
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        //builderSingle.setIcon(R.drawable.logo_full_105);
        builderSingle.setIcon(R.mipmap.ic_launcher);
        builderSingle.setTitle(title);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1, entries);
        arrayAdapter.notifyDataSetChanged();

        builderSingle.setNegativeButton(
                "cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(
                arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = arrayAdapter.getItem(which);

                        switch (viewId) {
                            case R.id.btn_bm_height_ft: {
                                btnHeightFt.setText(strName);
                                bmModel.heightBM = Integer.parseInt(btnHeightFt.getText().toString()) * 12 + Float.parseFloat(btnHeightIn.getText().toString());
                            }
                            break;
                            case R.id.btn_bm_height_in: {
                                btnHeightIn.setText(strName);
                                bmModel.heightBM = Integer.parseInt(btnHeightFt.getText().toString()) * 12 + Float.parseFloat(btnHeightIn.getText().toString());

                            }
                            break;
                            case R.id.btn_bm_weight: {
                                btnWeight.setText(strName);
                                bmModel.weightBM = Integer.parseInt(btnWeight.getText().toString());
                            }
                            break;
                            default:
                                break;
                        }
                    }
                });
        builderSingle.show();
    }


    public class BMSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            progress += ConstantsFunctional.MIN_SIZE_BODY_MEASUREMENT ;
            Log.d(LOGTAG,"BMSeekBarChangeListener");
//            if(progress ==0)//TODO
//                progress =1;

            if(seekBar == hipsSeekBar) {
                bmModel.hipsSize = progress;
            }
            else if(seekBar == bustSeekBar) {
                bmModel.bustSize = progress;
            }
            else if (seekBar == waistSeekBar) {
                bmModel.waistSize = progress;
            }

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
}
