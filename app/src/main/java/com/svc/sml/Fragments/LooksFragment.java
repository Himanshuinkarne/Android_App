package com.svc.sml.Fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.svc.sml.Activity.LooksActivity;
import com.svc.sml.Adapter.LookBoardPageAdapter;
import com.svc.sml.Adapter.LooksFragmentBottomAdapter;
import com.svc.sml.Database.ComboData;
import com.svc.sml.Database.InkarneDataSource;
import com.svc.sml.Helper.DataManager;
import com.svc.sml.Utility.HorizontalListView;
import com.svc.sml.InkarneAppContext;
import com.svc.sml.Model.LookBoardItem;
import com.svc.sml.R;
import com.svc.sml.Utility.CircleIndicator;

import java.util.ArrayList;

public class LooksFragment extends BaseLooksFragment  {
    // TODO: Rename parameter arguments, choose names that match
    private final static String LOGTAG = LooksFragment.class.getSimpleName();
    private static final String ARG_PARAM_CATEGORY = "category";

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    // TODO: Rename and change types of parameters
    private String category;
    private InkarneDataSource dataSource;
    private ViewPager vPagerLookBoard;
    private HorizontalListView hlvBottom;
    private LookBoardPageAdapter lookBoardPageAdapter;
    private CircleIndicator indicator;
    private ArrayList<LookBoardItem> listLookBoard;
    private LinearLayout conHLV;
    private TextView tvHLVtype;
    private ImageButton btnExpandToggle;
    ArrayList<ComboData> comboList;
    GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;
    private RelativeLayout conTop;


    public LooksFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment LooksFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LooksFragment newInstance(String param1) {
        LooksFragment fragment = new LooksFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_CATEGORY, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = getArguments().getString(ARG_PARAM_CATEGORY);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_looks, container, false);
        conTop = (RelativeLayout)v.findViewById(R.id.con_top);
        conHLV = (LinearLayout)v.findViewById(R.id.con_hlv);
        tvHLVtype = (TextView)v.findViewById(R.id.tvHLV_type);
        btnExpandToggle = (ImageButton)v.findViewById(R.id.btn_expand);
//        btnExpandToggle.setTag(1);
//        animateViewToggle((Integer) btnExpandToggle.getTag());
        btnExpandToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateViewToggle((int)v.getTag());
            }
        });

        vPagerLookBoard = (ViewPager) v.findViewById(R.id.vpager_lookboard);
        indicator = (CircleIndicator) v.findViewById(R.id.look_board_indicator);

        hlvBottom = (HorizontalListView) v.findViewById(R.id.hlv_combos_bottom);
        hlvBottom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(LOGTAG, "HLV item clicked :" + position);
                //ComboDataReconcile comboData = comboList.get(position);
                onComboSelected(category,comboList,position);
            }
        });
        dataSource = InkarneAppContext.getDataSource();
        populateLookBoardItem();
        populateBottomList();


        gestureDetector = new GestureDetector(new MyGestureDetector());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };
        //conTop.setOnTouchListener(gestureListener);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    btnExpandToggle.setTag(1);
                    if(isAdded()) {
                        animateViewToggle((Integer) btnExpandToggle.getTag());
                    }
                }
            }, 600);
    }

    @Override
    public void onDownwardGesture() {
        animateViewToggle(1);
    }

    @Override
    public void onUpwardGesture() {
      animateViewToggle(0);
    }

    private void animateViewToggle(final int shouldShow){
        btnExpandToggle.setEnabled(false);
        float dy = 0;
        int heightMargin = (int) getResources().getDimension(R.dimen.height_combo_gallery_item);
        if(shouldShow == 0){
            dy = -0;
        }
        else {
            dy = heightMargin;
        }
        conHLV.animate()
                .translationY(dy)
                .alpha(1.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        //view.setVisibility(View.GONE);
                        if(shouldShow == 0) {
                            btnExpandToggle.setImageResource(R.drawable.arrow_down);
                            btnExpandToggle.setTag(1);
                            //tvHLVtype.setVisibility(View.VISIBLE);
                        }else {
                            btnExpandToggle.setImageResource(R.drawable.arrow_up);
                            btnExpandToggle.setTag(0);
                            //tvHLVtype.setVisibility(View.INVISIBLE);
                        }
                        btnExpandToggle.setEnabled(true);
                    }
                });

    }

    @Override
    public void onResume() {
        Log.w("received", "resume");
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver, new IntentFilter(LooksActivity.INTENT_NAME_TAB_SELECTED));

        super.onResume();
    }

    @Override
    public void onStart() {
        Log.w("received", "resume");
        super.onStart();

    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String cate = intent.getStringExtra(LooksActivity.EXTRA_PARAM_CATEGORY);
            Log.e(LOGTAG,cate);
            if(cate != null)
            tvHLVtype.setText("More "+cate+" Looks...");
        }
    };

    // TODO: Rename method, update argument and hook method into UI event

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnBaseLookFragmentInteractionListener) {
            mListener = (OnBaseLookFragmentInteractionListener) context;
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

    private void populateLookBoardItem() {
        //vPagerLookBoard.setAdapter(null);
        listLookBoard = (ArrayList<LookBoardItem>) dataSource.getComboLookBoardByCategory(category,true);
        if (lookBoardPageAdapter == null) {
            lookBoardPageAdapter = new LookBoardPageAdapter(getActivity(), listLookBoard,LooksFragment.this);
            vPagerLookBoard.setOffscreenPageLimit(3);
            if (vPagerLookBoard.getAdapter() == null) {
                vPagerLookBoard.setAdapter(lookBoardPageAdapter);
                indicator.setViewPager(vPagerLookBoard);
                //vPagerLookBoard.registerDataSetObserver(indicator.getDataSetObserver());
            }
        } else {
            if (vPagerLookBoard.getAdapter() == null) {
                vPagerLookBoard.setAdapter(lookBoardPageAdapter);
                indicator.setViewPager(vPagerLookBoard);
            }
            lookBoardPageAdapter.setListLookBoard(listLookBoard);
        }
    }

    private void populateLookBoardItem1() {
        String uri = "http://ec2-52-77-8-232.ap-southeast-1.compute.amazonaws.com/svc/v1/campaigns/?category=party&gender=m";
        uri = "http://ec2-52-77-8-232.ap-southeast-1.compute.amazonaws.com/svc/v1/campaignsAll/";
        DataManager.getInstance().requestLookBoard(uri, new DataManager.OnResponseHandlerInterface() {
            @Override
            public void onResponse(Object obj) {
                listLookBoard = (ArrayList<LookBoardItem>) obj;
                if (lookBoardPageAdapter == null) {
                    lookBoardPageAdapter = new LookBoardPageAdapter(getActivity(), listLookBoard,LooksFragment.this);
                    vPagerLookBoard.setOffscreenPageLimit(3);
                    if (vPagerLookBoard.getAdapter() == null) {
                        vPagerLookBoard.setAdapter(lookBoardPageAdapter);
                    }

                } else {
                    if (vPagerLookBoard.getAdapter() == null) {
                        vPagerLookBoard.setAdapter(lookBoardPageAdapter);
                    }
                    lookBoardPageAdapter.setListLookBoard(listLookBoard);
                }
            }

            @Override
            public void onResponseError(String errorMessage, int errorCode) {

            }
        });
    }

    private void populateBottomList() {
         comboList = (ArrayList<ComboData>) dataSource.getComboReconcileByCategory(category,InkarneAppContext.shouldRearrangeLooks);
        LooksFragmentBottomAdapter ad = new LooksFragmentBottomAdapter(getActivity(), category, comboList);
        hlvBottom.setAdapter(ad);
    }

    private class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.e(LOGTAG,"gesture onSingleTapUp") ;

//            if(clickedLBItem != null) {
//                ComboData comboData = InkarneAppContext.getDataSource().getComboDataByComboID(clickedLBItem.getCombo_ID());
//                if (comboData != null) {
//                    clickedLBItem = null;
//                    //comboData.setLooksCategoryTitle(item.getCategory());
//                    comboData.setLooksCategoryTitle(comboData.getCombo_Style_Category());//todo
//                    if (listener != null)
//                        listener.onComboDataSelected(comboData);
//                }
//            }
            return false;
        }
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.e(LOGTAG,"\ngesture onFling") ;
            try {
                // downward swipe
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {

                    if( e2.getY() - e1.getY() >0){
                        //Toast.makeText(mContext, "Downward Swipe", Toast.LENGTH_SHORT).show();
                        Log.e(LOGTAG,"gesture Downward Swipe") ;
//                        if (listener != null) {
//                            listener.onDownwardGesture();
//                        }
                    }
                    else {
                        Log.e(LOGTAG,"gesture Upward Swipe") ;
                        //Toast.makeText(mContext, "Upward Swipe", Toast.LENGTH_SHORT).show();
//                        if (listener != null) {
//                            listener.onUpwardGesture();
//                        }
                    }
                }
//                else if (Math.abs(e2.getY() - e1.getY()) > SWIPE_MAX_OFF_PATH && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
//                    Toast.makeText(mContext, "Upward Swipe", Toast.LENGTH_SHORT).show();
//
//                }
                // right to left swipe
//                else if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
//                    Toast.makeText(SelectFilterActivity.this, "Left Swipe", Toast.LENGTH_SHORT).show();
//                }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
//                    Toast.makeText(SelectFilterActivity.this, "Right Swipe", Toast.LENGTH_SHORT).show();
//                }
            } catch (Exception e) {
                // nothing
            }
            return false;
        }

    }



}
