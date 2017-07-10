package com.svc.sml.Fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.svc.sml.Database.DatabaseHandler;
import com.svc.sml.R;
import com.svc.sml.Utility.ConstantsUtil;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.abs;
import static java.lang.Math.atan2;

//import com.inkarne.inkarne.R;

//import android.opengl.Matrix;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link DialerSelectionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */


public class DialerSelectionFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String LOGTAG = DialerSelectionFragment.class.getName();
    private static final String ARG_PARAM_CURRENT_FILTER = "ARG_PARAM_CURRENT_FILTER";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnDailerFragmentInteractionListener mListener;

    //private EditText etSearch;
    private ImageView ivDailer;
//    private Button btnFormal, btnIndowenstern, btnCasual, btnParty;
//    private LinearLayout llBottomButtonLayout;
//    private ImageButton btnSearch, btnClose;
//    private ImageView btnCloseSelection;
//    private TextView tvDailerSelection;
    private String currentDialFilterStr;
    private String currentDialSelection;

    int selectedDailPosition = 0;
    Rect rectDailer;
    //private String DState;


    private static final Map<String, String> hashMapdailerSelection;

    static {
        hashMapdailerSelection = new HashMap<String, String>();
        hashMapdailerSelection.put(DatabaseHandler.LIKES_COUNT, "Liked");
        hashMapdailerSelection.put(DatabaseHandler.COMBO_LOCAL_CART_COUNT, "Added To Cart");
        hashMapdailerSelection.put(DatabaseHandler.PUSH_FLAG,"Deals");
        hashMapdailerSelection.put(DatabaseHandler.VOGUE_FLAG,"VOGUE_FLAG");
    }


    public interface OnDailerFragmentInteractionListener {
        void onDialerFragmentInteraction(String filter);
        void onDialerFragmentLaunchInteraction(String clickOption);
        void onDialerFragmentClose(String param);
    }

    public DialerSelectionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DialerSelectionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DialerSelectionFragment newInstance(String param1, String param2) {
        DialerSelectionFragment fragment = new DialerSelectionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_CURRENT_FILTER, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static DialerSelectionFragment newInstance(String currentDialFilterStr) {
        DialerSelectionFragment fragment = new DialerSelectionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_CURRENT_FILTER, currentDialFilterStr);
        fragment.setArguments(args);
        return fragment;
    }

    public static DialerSelectionFragment newInstance() {
        DialerSelectionFragment fragment = new DialerSelectionFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentDialFilterStr = getArguments().getString(ARG_PARAM_CURRENT_FILTER);
        }

//        etSearch.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_dailer_selection, container, false);


        //tvDailerSelection = (TextView) rootView.findViewById(R.id.tv_dailer_selection);
        //btnCloseSelection = (ImageView) rootView.findViewById(R.id.btn_selection_close);
        ivDailer = (ImageView) rootView.findViewById(R.id.iv_dailer);

        //llBottomButtonLayout = (LinearLayout) rootView.findViewById(R.id.ll_dailer_bottom_selection);
        //llBottomButtonLayout.setOnClickListener(this);

        ivDailer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    inViewInBounds(v, (int) event.getRawX(), (int) event.getRawY());
                 /*
                    Log.d("Location", ", event.getX(): " + (int) event.getX() + ", event.getY(): " + (int) event.getY());
                    Matrix inverse = new Matrix();
                    ivDailer.getImageMatrix().invert(inverse);
                    // map touch point from ImageView to image
                    float[] touchPoint = new float[] {event.getX(), event.getY()};
                    inverse.mapPoints(touchPoint);
                    Log.d("Location", ", touchPoint.getX(): " + (int) touchPoint[0] + ", touchPoint.getY(): " + (int) touchPoint[1]);
               */
                }
                return true;
            }
        });
        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();
        if(currentDialFilterStr ==null)
            currentDialFilterStr = ConstantsUtil.EDialFilter.eDailUnselected.toString();
        showFilterSelection(currentDialFilterStr);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void inViewInBounds(View view, int x1, int y1) {
        if (rectDailer == null) {
            Rect outRect = new Rect();
            int[] location = new int[2];
            view.getDrawingRect(outRect);
            view.getLocationOnScreen(location);
            outRect.offset(location[0], location[1]);
            rectDailer = outRect;
        }

        Log.d(LOGTAG, ", rawX " + x1 + ", rawY " + y1);
        Log.d(LOGTAG, ",centerX: " + rectDailer.centerX() + ", centerY: " + rectDailer.centerY() + "  width: " + rectDailer.width() + "  height: " + rectDailer.height());
        Log.d(LOGTAG, ",left: " + rectDailer.left + ", top: " + rectDailer.top + "  right: " + rectDailer.right + " bottom " + rectDailer.bottom);

        int x0 = rectDailer.centerX();
        int y0 = rectDailer.centerY();
        if (!rectDailer.contains(x1, y1)) {
            return;
        }

        float innerRadius = (float) ((rectDailer.width() / 2) * 0.4);
        if (abs(y1 - y0) < innerRadius && abs(x1 - x0) < innerRadius) {
            return;
        }

        float ang;
        if (x1 - x0 == 0) {
            if (-(y1 - y0) > 0)
                ang = 90;
            else
                ang = -90;
        } else {
            float DegreeConversion = (float) (180.0 / Math.PI);
            ang = (float) atan2(-(y1 - y0), (x1 - x0)) * DegreeConversion;
        }
        if (ang < 0)
            ang += 360;

        for (int i = 1; i < 360; i += 1) {
            //if there is 6 button
            int angle = i * 360 / 8;
            if (ang < angle) {
                selectedDailPosition = i;
                break;
            }
        }

        Log.d(LOGTAG, "\n***************position " + selectedDailPosition);
//        btnCasual.setSelected(false);
//        btnParty.setSelected(false);
//        btnIndowenstern.setSelected(false);
//        btnFormal.setSelected(false);
        switch (selectedDailPosition) {
            case 1: {  //lookoftheday
                //ivDailer.setImageResource(R.drawable.dail_like_1);
                //ivDailer.setImageResource(R.drawable.dail1_lookoftheday);
                currentDialFilterStr = ConstantsUtil.EDialFilter.eDailLookOfday.toString(); //pushFlag
                mListener.onDialerFragmentInteraction(currentDialFilterStr);
                showSelectionOnBottom();
            }
            break;
            case 2: { //like_old
                //ivDailer.setImageResource(R.drawable.dail_cart_2);
                //ivDailer.setImageResource(R.drawable.dail2_likes);
                currentDialFilterStr = ConstantsUtil.EDialFilter.eDailLike.toString();
                mListener.onDialerFragmentInteraction(currentDialFilterStr);
                showSelectionOnBottom();
            }
            break;
            case 3: { //cart
                //ivDailer.setImageResource(R.drawable.dail_history_3);
                //ivDailer.setImageResource(R.drawable.dail3_cart);
                //currentDialFilterStr = ConstantsUtil.EDialFilter.eDailCart.toString();
                currentDialSelection = ConstantsUtil.EDialFilter.eDailCart.toString();
                //mListener.onDialerFragmentInteraction(currentDialFilterStr);
                //mListener.onDialerFragmentLaunchInteraction(currentDialSelection);
                //showSelectionOnBottom();
            }
            break;
            case 4: { //history
                //ivDailer.setImageResource(R.drawable.dail4_history);
                currentDialFilterStr = ConstantsUtil.EDialFilter.eDailHistory.toString();
                mListener.onDialerFragmentInteraction(currentDialFilterStr);
                showSelectionOnBottom();
            }
            break;
            case 5: {//Vague_Lag
                //ivDailer.setImageResource(R.drawable.dail5_profile);
                currentDialFilterStr = ConstantsUtil.EDialFilter.eDailDeals.toString(); //pushFlag
                mListener.onDialerFragmentInteraction(currentDialFilterStr);
                //getActivity().finish();
                showSelectionOnBottom();
            }
            break;
            case 6: {
                //ivDailer.setImageResource(R.drawable.dail_lookofday_6);
                //ivDailer.setImageResource(R.drawable.dail6_hair);

            }
            break;
            case 7: {
                //ivDailer.setImageResource(R.drawable.dail_lookofday_6);
                currentDialSelection = ConstantsUtil.EDialFilter.eDailUpdateAvatar.toString(); //pushFlag
                //ivDailer.setImageResource(R.drawable.dail7_emoji);
                mListener.onDialerFragmentLaunchInteraction(currentDialSelection);

            }
            break;
            case 8: {
                //ivDailer.setImageResource(R.drawable.dail8_eyewear);
            }
            break;
            default: {

            }
            break;
        }
    }


    @Override
    public void onClick(View v) {
        //ivDailer.setImageResource(R.drawable.dail_unselected);
        switch (v.getId()) {
//            case R.id.btn_formal: {
//                //DState = "2";
////                llBottomButtonLayout.setVisibility(View.VISIBLE);
////                tvDailerSelection.setText("FORMAL Selected");
////                  mListener.onDialerFragmentInteraction(null);
//                currentDialFilterStr = ConstantsUtil.EDialFilter.eDailFormal.toString();
//                showSelectionOnBottom();
//                mListener.onDialerFragmentInteraction(currentDialFilterStr);
//            }
//            break;
//            case R.id.btn_casual: {
//                currentDialFilterStr = ConstantsUtil.EDialFilter.eDailCasual.toString();
//                showSelectionOnBottom();
//                mListener.onDialerFragmentInteraction(currentDialFilterStr);
//            }
//            break;
//
//            case R.id.btn_indowestern: {
//                currentDialFilterStr = ConstantsUtil.EDialFilter.eDailIndowestern.toString();
//                showSelectionOnBottom();
//                mListener.onDialerFragmentInteraction(currentDialFilterStr);
//            }
//            break;
//
//            case R.id.btn_party: {
//                currentDialFilterStr = ConstantsUtil.EDialFilter.eDailParty.toString();
//                showSelectionOnBottom();
//                mListener.onDialerFragmentInteraction(currentDialFilterStr);
//            }
//
//            break;
//            case R.id.ll_dailer_bottom_selection: {
//                onRemoveSelection();
//                mListener.onDialerFragmentInteraction(currentDialFilterStr);
//            }
            default:
            break;
//            case R.id.btn_close: {
//                mListener.onDialerFragmentClose(null);
//            }
//            break;
        }
    }

    private void onRemoveSelection() {
        currentDialFilterStr = ConstantsUtil.EDialFilter.eDailUnselected.toString();
//        btnCasual.setSelected(false);
//        btnParty.setSelected(false);
//        btnIndowenstern.setSelected(false);
//        btnFormal.setSelected(false);

//        ivDailer.setImageResource(R.drawable.dail_unselected);
//        tvDailerSelection.setText("DEFAULT Selected");
//        llBottomButtonLayout.setVisibility(View.INVISIBLE);
    }

    private void showSelectionOnBottom() {
        String label = hashMapdailerSelection.get(currentDialFilterStr);
//       if(label != null)
//           tvDailerSelection.setText(label);
//        else
//           tvDailerSelection.setText(currentDialFilterStr);
//
//        llBottomButtonLayout.setVisibility(View.VISIBLE);
    }

    public void showFilterSelection(String filter) {
        Log.d(LOGTAG, "showFilterSelection :" + filter);
        currentDialFilterStr = filter;
        onRemoveSelection();
        if (filter.equals(ConstantsUtil.EDialFilter.eDailUnselected.toString())) {


        } else if (filter.equals(ConstantsUtil.EDialFilter.eDailCart.toString())) {
            //ivDailer.setImageResource(R.drawable.dail3_cart);
            showSelectionOnBottom();

        } else if (filter.equals(ConstantsUtil.EDialFilter.eDailLookOfday.toString())) {
            //ivDailer.setImageResource(R.drawable.dail1_lookoftheday);
            showSelectionOnBottom();

        } else if (filter.equals(ConstantsUtil.EDialFilter.eDailLike.toString())) {
            //ivDailer.setImageResource(R.drawable.dail2_likes);
            showSelectionOnBottom();
        } else if (filter.equals(ConstantsUtil.EDialFilter.eDailHistory.toString())) {
            //ivDailer.setImageResource(R.drawable.dail4_history);
            showSelectionOnBottom();
        } else if (filter.equals(ConstantsUtil.EDialFilter.eDailCasual.toString()) ||
                filter.equals(ConstantsUtil.EDialFilter.eDailIndowestern.toString()) ||
                filter.equals(ConstantsUtil.EDialFilter.eDailFormal.toString()) ||
                filter.equals(ConstantsUtil.EDialFilter.eDailParty.toString())
                ) {

            showSelectionOnBottom();
        } else {
            onRemoveSelection();
        }
    }

    //@Override
    public void onAttach(Context context) {
        super.onAttach((Activity) context);
        if (context instanceof OnDailerFragmentInteractionListener) {
            mListener = (OnDailerFragmentInteractionListener) context;
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
//    public interface OnLooksFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentLookLikeInteraction(Uri uri);
//    }
}
