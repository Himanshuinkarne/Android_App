package com.svc.sml.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.svc.sml.Adapter.LooksFragmentAdapter;
import com.svc.sml.Database.ComboData;
import com.svc.sml.Database.ComboDataReconcile;
import com.svc.sml.Database.User;
import com.svc.sml.Fragments.BaseLooksFragment;
import com.svc.sml.Helper.DataManager;
import com.svc.sml.InkarneAppContext;
import com.svc.sml.Model.LookBoardItem;
import com.svc.sml.R;
import com.svc.sml.Utility.ConstantsUtil;
import com.svc.sml.Utility.IViewPager;

import java.util.ArrayList;
import java.util.List;


public class LooksActivity extends FragmentActivity implements BaseLooksFragment.OnBaseLookFragmentInteractionListener {//implements TabListener
    private final static String LOGTAG = LooksActivity.class.getSimpleName();
    public static final String INTENT_NAME_TAB_SELECTED = "INTENT_NAME_TAB_SELECTED";
    public static final String EXTRA_PARAM_CATEGORY = "EXTRA_PARAM_CATEGORY";
    LooksFragmentAdapter mPagerAdapter;
    IViewPager mViewPager;
    TabLayout tabLayout;
    int countRetryReconcileCombo = 0;
    int countRetryReconcileLookBoard = 0;
    protected Tracker mTracker;
    protected AppEventsLogger logger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_looks);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        createTracker();
        /////mPagerAdapter = new LooksFragmentAdapter(getSupportFragmentManager(), this);
        mViewPager = (IViewPager) findViewById(R.id.looks_vpager);
        /////mViewPager.setAdapter(mPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        //tabLayout.setupWithViewPager(mViewPager);


//        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
//            @Override
//            public void onTabSelected(TabLayout.Tab tab){
//                int position = tab.getPosition();
//                Log.e(LOGTAG, "onTabSelected: " + position);
//                mViewPager.setCurrentItem(tab.getPosition());
//                Intent intent = new Intent(INTENT_NAME_TAB_SELECTED);
//                if(ConstantsUtil.arrayListLooksLabelName.size()>position)
//                    intent.putExtra(EXTRA_PARAM_CATEGORY, ConstantsUtil.arrayListLooksLabelName.get(position));
//                LocalBroadcastManager.getInstance(LooksActivity.this).sendBroadcast(intent);
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//                int position = tab.getPosition();
//                //Log.e(LOGTAG, "onTabUnselected: " + position);
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            //This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position) {
                //Toast.makeText(LooksActivity.this,"Selected page position: " + position, Toast.LENGTH_SHORT).show();
                Log.e(LOGTAG, "onPageSelected: " + position);
            }

            //This method will be invoked when the current page is scrolled
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Code goes here
                //Log.e(LOGTAG, "onPageScrolled: " + position);
            }

            //Called when the scroll state changes:
            //SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
            @Override
            public void onPageScrollStateChanged(int state) {
                // Code goes here
                Log.e(LOGTAG, "onPageScrollStateChanged: " + state);
            }
        });
        /////InkarneAppContext.refreshDataAsync();
        logUser();
    }

    private void initStart(){
        mPagerAdapter = new LooksFragmentAdapter(getSupportFragmentManager(), this);
        mViewPager = (IViewPager) findViewById(R.id.looks_vpager);
        mViewPager.setAdapter(mPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setOnTabSelectedListener(
                new TabLayout.ViewPagerOnTabSelectedListener(mViewPager) {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        super.onTabSelected(tab);
                        int position = tab.getPosition();
                        Log.e(LOGTAG, "onTabSelected: " + position);
                        mViewPager.setCurrentItem(tab.getPosition());
                        Intent intent = new Intent(INTENT_NAME_TAB_SELECTED);
                        if(ConstantsUtil.arrayListLooksLabelName.size()>position)
                            intent.putExtra(EXTRA_PARAM_CATEGORY, ConstantsUtil.arrayListLooksLabelName.get(position));
                        LocalBroadcastManager.getInstance(LooksActivity.this).sendBroadcast(intent);
                    }
                });

        InkarneAppContext.refreshDataAsync();
    }

    public void onStart(){
        super.onStart();
        initStart();
        reconcileLookBoard();
        requestReconcileComboData();
    }

    private void logUser() {
        // TODO: Use the current user's information
        // You can call any combination of these three methods
        Crashlytics.setUserIdentifier(User.getInstance().getmUserId());
        Crashlytics.setUserEmail(User.getInstance().getEmailId());
        Crashlytics.setUserName(User.getInstance().getmFirstName());
    }

    public void forceCrash() {
        throw new RuntimeException("This is a Test crash");
    }

    protected  void createTracker(){
        InkarneAppContext application = (InkarneAppContext)getApplication();
        mTracker = application.getTracker();
        logger = AppEventsLogger.newLogger(this);
    }

    protected void GATrackActivity(String screenName){
        Log.i(LOGTAG, "screen name: " + screenName);
        mTracker.setScreenName(screenName);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        Bundle parameters = new Bundle();
        parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, LOGTAG);
        logger.logEvent(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT,parameters);
    }

    protected void trackEvent(String cat,String l2,String l3){
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory(cat)
                .setAction(l2)
                .setLabel(l3)
                .build());
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e(LOGTAG, "ON- newIntent");
        GATrackActivity(LOGTAG);
        //getIntent() should always return the most recent
        setIntent(intent);

    }

    @Override
    public void onLookFragmentInteraction(ComboData comboData) {

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */


    private void reconcileLookBoard() {
        String uri = ConstantsUtil.URL_BASEPATH_0+ConstantsUtil.URL_METHOD_RECONCILE_CAMPAIGN+User.getInstance().getmUserId()+"/"+User.getInstance().getmGender();
        Log.d(LOGTAG,"Uri :"+uri);

        DataManager.getInstance().requestLookBoard(uri, new DataManager.OnResponseHandlerInterface() {
            @Override
            public void onResponse(Object obj) {
                ArrayList<LookBoardItem> listLookBoard = (ArrayList<LookBoardItem>) obj;
                Log.e(LOGTAG,"reconcileLookBoard");
            }

            @Override
            public void onResponseError(String errorMessage, int errorCode) {
                countRetryReconcileLookBoard++;
                    if (errorCode == DataManager.CODE_DATA_MANAGER_NETWORK_ERROR) {
                        if (countRetryReconcileLookBoard == 5) {
                            //Toast.makeText(getApplicationContext(), getString(R.string.message_network_download_title), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (countRetryReconcileCombo < ConstantsUtil.COUNT_RETRY_SERVICE_CRITICAL) {
                            if (countRetryReconcileLookBoard == 6) {
                                //Toast.makeText(getApplicationContext(), getString(R.string.message_server_other_failure), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            }
        });
    }


    private void requestReconcileComboData() {
        String uri = ConstantsUtil.URL_BASEPATH + ConstantsUtil.URL_METHOD_RECONCILE + User.getInstance().getmUserId() + "/" + User.getInstance().getPBId();
        Log.d(LOGTAG,"Uri :"+uri);
        DataManager.getInstance().requestReconcileComboData(uri, new DataManager.OnResponseHandlerInterface() {
            @Override
            public void onResponse(Object obj) {
                List<ComboDataReconcile> arrayListComboReconcile = (List<ComboDataReconcile>) obj;

                for (ComboDataReconcile reComboData : arrayListComboReconcile) {
                    if (reComboData.getCombo_ID() != null) {
                        Log.d(LOGTAG, "requestReconcileComboData - comboId =" + reComboData.getCombo_ID().toString());
                        InkarneAppContext.getDataSource().createReconcile(reComboData);
                    }
                }
            }

            @Override
            public void onResponseError(String errorMessage, int errorCode) {
                countRetryReconcileCombo++;
                    requestReconcileComboData();
                    if (errorCode == DataManager.CODE_DATA_MANAGER_NETWORK_ERROR) {
                        if (countRetryReconcileCombo == 1) {
                            Toast.makeText(getApplicationContext(), getString(R.string.message_network_download_title), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (countRetryReconcileCombo < ConstantsUtil.COUNT_RETRY_SERVICE_CRITICAL) {
                            if (countRetryReconcileCombo == 2) {
                                Toast.makeText(getApplicationContext(), getString(R.string.message_server_other_failure), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
        });
    }

}
