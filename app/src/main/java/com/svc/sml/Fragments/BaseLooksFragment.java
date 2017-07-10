package com.svc.sml.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.svc.sml.Adapter.LookBoardPageAdapter;
import com.svc.sml.Database.ComboData;
import com.svc.sml.Database.InkarneDataSource;
import com.svc.sml.Helper.ComboDownloader;
import com.svc.sml.Helper.DataManager;
import com.svc.sml.InkarneAppContext;
import com.svc.sml.R;
import com.svc.sml.ShopActivity;
import com.svc.sml.Utility.Connectivity;
import com.svc.sml.Utility.ConstantsUtil;
import com.svc.sml.View.LoadingView;

import java.util.ArrayList;
import java.util.HashMap;

public class BaseLooksFragment extends BaseFragment implements LookBoardPageAdapter.LookBoardPageAdapterListener {
    // TODO: Rename parameter arguments, choose names that match
    private static final String LOGTAG = "BaseLooksFragment";
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;

    protected String category;
    protected InkarneDataSource datasource;
    protected LoadingView pbLooksInfo;
    protected ComboData currentClickedComboData;
    protected ArrayList<ComboData> currentComboList;
    protected int currentComboIndex = 0;
    protected ProgressDialog pDialogDownload;
    private boolean isDownloadCancelled = false;

    private int countAccessoryInfoTobeFetched = 0;
    protected HashMap<String, Boolean> hmLoadedCombo = new HashMap<>();

    protected OnBaseLookFragmentInteractionListener mListener;


    public BaseLooksFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BaseLooksFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BaseLooksFragment newInstance(String param1, String param2) {
        BaseLooksFragment fragment = new BaseLooksFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
        datasource = InkarneAppContext.getDataSource();
        datasource.open();
        pDialogDownload = getHProgressDialogTranslucent();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TextView textView = new TextView(getActivity());
        textView.setText(R.string.hello_blank_fragment);
        return textView;
    }


    @Override
    public void onResume() {
        Log.w("received", "resume");
        super.onResume();
        hmLoadedCombo.clear();
//        if(((ShopActivity)getActivity()).listOfComboList != null && ((ShopActivity)getActivity()).listOfComboList.size() != 0 ){
//            this.listOfComboList = ((ShopActivity)getActivity()).listOfComboList;
//            collectionAdapter = new CollectionAdapter(getActivity(), listOfComboList, (CollectionAdapter.OnGridAdapterListener) CollectionFragment.this);
//            gridCollectionView.setAdapter(collectionAdapter);
//        }else {
//            new LoadLooksListTask().execute();
//        }
    }


    @Override

    public void onPause() {
        // Unregister since the activity is paused.
//        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onPause();
        //stopShowingProgress();
    }

    public void onStop() {
        super.onStop();
        stopShowingProgress();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnBaseLookFragmentInteractionListener) {
            mListener = (OnBaseLookFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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
    public interface OnBaseLookFragmentInteractionListener {
        // TODO: Update argument type and name
        void onLookFragmentInteraction(ComboData comboData);
    }

    /* LookBoardPageAdapter.LookBoardPageAdapterListener */
    @Override
    public void onComboDataSelected(ComboData comboData) {
        currentClickedComboData = comboData;
            int index = ConstantsUtil.arrayListLooksCategory.indexOf(comboData.getLooksCategoryTitle());
            if(index >0 && index < InkarneAppContext.listOfComboList.size()) {
                currentComboList = InkarneAppContext.listOfComboList.get(index).getComboList();

                currentComboIndex = InkarneAppContext.getComboIndex(currentComboList, currentClickedComboData);
                startShowingProgress();
//        countAccessoryInfoTobeFetched = 0;
//        String title = looksCatTitle;
//        if (looksCatTitle.equals(ConstantsUtil.arrayListLooksLabelName.get(7))
//                || looksCatTitle.equals(ConstantsUtil.arrayListLooksLabelName.get(8))) {
//            title = comboData.getCombo_Style_Category();
//        }
//        currentClickedComboData.setLooksCategoryTitle(title);
            checkComboDetailPopulated(comboData);
        }
    }

    @Override
    public void onDownwardGesture() {

    }

    @Override
    public void onUpwardGesture() {

    }

    //@Override
    public void onComboSelected(final String looksCatTitle, final ArrayList<ComboData> comboList, final int position) {
        if (!(comboList != null && comboList.size() > position)) {
            return;
        }
        currentClickedComboData = comboList.get(position);
        Log.w(LOGTAG, "listView item clicked :" + currentClickedComboData.getCombo_ID());
        Log.w("received", "listView item clicked :" + currentClickedComboData.getCombo_ID());
        hmLoadedCombo.put(currentClickedComboData.getCombo_ID(), false);
        currentComboList = comboList;
        currentComboIndex = position;
        ComboData comboData = currentClickedComboData;
        startShowingProgress();
//        countAccessoryInfoTobeFetched = 0;
        String title = looksCatTitle;
        if (looksCatTitle.equals(ConstantsUtil.arrayListLooksLabelName.get(7))
                || looksCatTitle.equals(ConstantsUtil.arrayListLooksLabelName.get(8))) {
            title = comboData.getCombo_Style_Category();
        }
        currentClickedComboData.setLooksCategoryTitle(title);
        checkComboDetailPopulated(comboData);
    }


    protected ProgressDialog getHProgressDialogTranslucent() {
        Log.e(LOGTAG,"getHProgressDialogTranslucent");
        ProgressDialog p = new ProgressDialog(getActivity(), R.style.AppCompatAlertDialogStyle);
        //p.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        p.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        p.setIndeterminate(false);
        p.setProgress(0);
        p.setCanceledOnTouchOutside(false);
        p.setCancelable(true);
        p.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dialog.dismiss();
                stopShowingProgress();
                isDownloadCancelled = true;

            }
        });
        return p;
    }


    private synchronized void startShowingProgress() {
        Log.e(LOGTAG,"startShowingProgress");
        if(pbLooksInfo != null) {
            pbLooksInfo.setVisibility(View.VISIBLE);
            pbLooksInfo.setLoadingText(getActivity().getString(R.string.message_rendering_looks));
        }
        if (pDialogDownload == null)
            pDialogDownload = getHProgressDialogTranslucent();
        pDialogDownload.setIndeterminate(true);
        if(!pDialogDownload.isShowing()) {
            pDialogDownload.setTitle(getActivity().getString(R.string.message_rendering_looks));
            pDialogDownload.show();
            pDialogDownload.setProgress(0);
        }
        if (pDialogDownload.getButton(DialogInterface.BUTTON_NEGATIVE) != null)
            pDialogDownload.getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.VISIBLE);
        isDownloadCancelled = false;
    }

    private synchronized void startShowingProgress(int progress) {
        if(pbLooksInfo != null) {
            pbLooksInfo.setVisibility(View.VISIBLE);
            pbLooksInfo.setLoadingText(getActivity().getString(R.string.message_rendering_looks));
        }
        if (pDialogDownload == null)
            pDialogDownload = getHProgressDialogTranslucent();
        pDialogDownload.setIndeterminate(true);
        if(!pDialogDownload.isShowing()) {
            pDialogDownload.setTitle(getActivity().getString(R.string.message_rendering_looks));
            pDialogDownload.show();
            pDialogDownload.setProgress(0);
        }
        if (pDialogDownload.getButton(DialogInterface.BUTTON_NEGATIVE) != null)
            pDialogDownload.getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.VISIBLE);
        isDownloadCancelled = false;
    }

    protected synchronized void stopShowingProgress() {
       // if(getActivity().isFinishing())
        Log.e(LOGTAG,"stopShowingProgress");
        if (pbLooksInfo != null)
            pbLooksInfo.setVisibility(View.INVISIBLE);
        if (pDialogDownload != null) {
            pDialogDownload.setProgress(0);
            pDialogDownload.dismiss();
        }
    }

    protected void setDialogProgress(int progress){
        Log.e(LOGTAG,"setDialogProgress");
        if (pDialogDownload == null)
            pDialogDownload = getHProgressDialogTranslucent();
        pDialogDownload.setIndeterminate(false);
        pDialogDownload.setProgress(progress);
        if(!pDialogDownload.isShowing()){
            pDialogDownload.show();
        }
    }


    private void checkComboDetailPopulated(ComboData comboData) {
        if (countAccessoryInfoTobeFetched == 0) {
            if (comboData.isDownloadedTempStatus()) {
                setDialogProgress(80);
                animateProgressIncrement();
                getComboInfoWithDownloads(comboData);
            } else {
                if (!Connectivity.isConnected(getActivity())) {
                    //stopShowingProgress();
                    InkarneAppContext.showNetworkAlert();
                    return;
                }
                //TODO wifi
                if (Connectivity.isConnectedWifi(getActivity()) || !InkarneAppContext.getSettingIsWifiOnlyDownload()) {
                    //startShowingProgress();
                    getComboInfoWithDownloads(comboData);
                } else {
                    //stopShowingProgress();
                    if(getActivity() != null)
                    Toast.makeText(getActivity(), "Please disable \"download on wifi only\" mode to use mobile data", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    public void getComboInfoWithDownloads(ComboData comboDataReconcile) {
        //startShowingProgress();
        new ComboDownloader(getActivity(), comboDataReconcile, new ComboDownloader.OnComboDownloadListener() {
            @Override
            public void onDownload(final ComboData receivedComboData) {

                if (receivedComboData != null) {
                    boolean isComboLoaded = false;
                    if (hmLoadedCombo != null && hmLoadedCombo.get(receivedComboData.getCombo_ID()) != null)
                        isComboLoaded = hmLoadedCombo.get(receivedComboData.getCombo_ID());
                    else {
                        hmLoadedCombo = new HashMap<>();
                    }
                    Log.w("receiver", "combo received : " + receivedComboData.getCombo_ID() + "  rank :" + receivedComboData.getForced_Rank());
                    if (receivedComboData.getCombo_ID().equals(currentClickedComboData.getCombo_ID()) && !isDownloadCancelled && !isComboLoaded) {
                        Log.w("receiver 2", "combo received : " + receivedComboData.getCombo_ID());
                        hmLoadedCombo.put(receivedComboData.getCombo_ID(), true);
                        if (pDialogDownload != null) {
                            synchronized (this) {

                                if (pDialogDownload != null && pDialogDownload.isShowing()) {
                                    pDialogDownload.setCancelable(true);
                                    if (pDialogDownload.getButton(DialogInterface.BUTTON_NEGATIVE) != null)
                                        pDialogDownload.getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.INVISIBLE);
                                }
                                receivedComboData.setLooksCategoryTitle(currentClickedComboData.getLooksCategoryTitle());
                                currentComboList.set(currentComboIndex, receivedComboData);
                                //mListener.onLooksFragmentInteraction(receivedComboData, currentComboList, currentComboIndex);
                                launchShopActivity(receivedComboData,currentComboList,currentComboIndex);

//                                new Handler(Looper.getMainLooper()).post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        if (pDialogDownload != null && pDialogDownload.isShowing()) {
//                                            pDialogDownload.setCancelable(true);
//                                            if (pDialogDownload.getButton(DialogInterface.BUTTON_NEGATIVE) != null)
//                                                pDialogDownload.getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.INVISIBLE);
//                                        }
//                                        receivedComboData.setLooksCategoryTitle(currentClickedComboData.getLooksCategoryTitle());
//                                        currentComboList.set(currentComboIndex, receivedComboData);
//                                        //mListener.onLooksFragmentInteraction(receivedComboData, currentComboList, currentComboIndex);
//                                        launchShopActivity(receivedComboData);
//                                        //stopShowingProgress();
//                                    }
//                                });
                            }
                        }
                    }
                }
            }

            @Override
            public void onDownloadFailed(String comboId) {
                stopShowingProgress();
                if(getActivity() != null)
                Toast.makeText(getActivity(), "Oops,could not reach our server.Please try again", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDownloadProgress(String comboId, int percentage) {
                if (currentClickedComboData != null && comboId.equals(currentClickedComboData.getCombo_ID())) {
                    if (pDialogDownload == null) {
                        startShowingProgress();
                    }
                    Log.w(LOGTAG, " " + percentage + "%");
                    if (pDialogDownload.isIndeterminate()) {
                        pDialogDownload.setIndeterminate(false);
                    }
                    if (pDialogDownload != null) {
                        if (percentage > 82) {
                            //pDialogDownload.setProgress(percentage);
                            animateProgressIncrement();
                        } else
                            pDialogDownload.setProgress(percentage);
                    }
                }
            }

            @Override
            public void onComboInfoFailed(String comboId, int error_code) {
                stopShowingProgress();
                if (error_code == DataManager.CODE_DATA_MANAGER_NETWORK_ERROR) {
                    if(getActivity()!=null)
                    Toast.makeText(getActivity(), getString(R.string.message_network_failure), Toast.LENGTH_SHORT).show();
                } else {
                    if(getActivity()!=null) {
                        //Toast.makeText(getApplicationContext(), "Oops,could not reach our server.Please try again", Toast.LENGTH_SHORT).show();
                        Toast.makeText(getActivity(), "Oops,could not reach our server.Please try again", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onComboInfoResponse(String comboId) {
                //pDialogDownload.setIndeterminate(false);
            }
        });
    }

    Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            pDialogDownload.incrementProgressBy(1);
        }
    };

    private void animateProgressIncrement() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (pDialogDownload.getProgress() <= 97) {
                        Thread.sleep(500);
                        handle.sendMessage(handle.obtainMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    protected synchronized void launchShopActivity(ComboData comboData,ArrayList<ComboData>comboList ,int currentComboIndex) {
        Log.e(LOGTAG, " ******** Launch ShopActivity in BaseLooksFragment  *******");
        if(getActivity() != null) {
            Intent intent = new Intent(getActivity(), ShopActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Log.e(LOGTAG, "comboData passed :id " + comboData.getCombo_ID());
            intent.putExtra("comboData", comboData);
            intent.putExtra("comboIndex", currentComboIndex);
            startActivity(intent);
        }
    }
}
