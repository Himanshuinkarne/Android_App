package com.svc.sml.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.widget.AppInviteDialog;
import com.svc.sml.Database.User;
import com.svc.sml.Helper.DataManager;
import com.svc.sml.Helper.LoadImageTask;
import com.svc.sml.Helper.RoundedImageView;
import com.svc.sml.InkarneAppContext;
import com.svc.sml.R;
import com.svc.sml.Utility.ConstantsUtil;

import java.io.File;
import java.io.FileOutputStream;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsFragment.OnSettingsFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends BaseFragment implements View.OnClickListener,LoadImageTask.Listener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String LOGTAG = "SettingsFragment";

    private static final String PATH_PROFILE_PIC_SAVED = "fb_profile_pic";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnSettingsFragmentInteractionListener mListener;
    //private ImageButton btnUpdateDetail;
    //private ImageButton btnHowWork;
    //private ImageButton btnLeaderBoard;
    private TextView tvPoints;
    private ImageButton btnInviteFriends;
    private ImageButton btnHelp;
    private SettingsDialog settingsDialog;

    private CallbackManager fbCallbackManager;
    private  int points = 0;



    //private TextView tvName;
    //private TextView tvEmail;
    private RoundedImageView ivUser;
    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
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
        GATrackActivity(LOGTAG);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
//        btnUpdateDetail = (ImageButton)v.findViewById(R.id.btn_settings_updatedetail);
//        btnUpdateDetail.setOnClickListener(this);
//        btnHowWork = (ImageButton)v.findViewById(R.id.btn_settings_how_it_works);
//        btnHowWork.setOnClickListener(this);
//        btnLeaderBoard = (ImageButton)v.findViewById(R.id.btn_settings_leader_board);
//        btnLeaderBoard.setOnClickListener(this);
        tvPoints = (TextView)v.findViewById(R.id.tv_settings_points);
        btnInviteFriends = (ImageButton)v.findViewById(R.id.btn_settings_invite_facebook);
        btnHelp = (ImageButton)v.findViewById(R.id.btn_download_help);
        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(settingsDialog == null){
                    settingsDialog = new SettingsFragment.SettingsDialog();
                }
                settingsDialog.showSettingDialog();
                trackEvent("Settings", "", "");
            }
        });
        btnInviteFriends.setOnClickListener(this);
        //tvName = (TextView)v.findViewById(R.id.tv_settings_name);
        //tvEmail = (TextView)v.findViewById(R.id.tv_settings_email);
        ivUser = (RoundedImageView)v.findViewById(R.id.iv_profile);
        initData();
        return v;
    }

    private void initData(){
        User user =  User.getInstance();
        points = user.getPoints();
        tvPoints.setText(""+points);
        //tvName.setText(user.getmFirstName() + " "+ user.getmLastName());
        //tvEmail.setText(user.getEmailId());
        if(user.isMale()){
            ivUser.setImageResource(R.drawable.btn_male);
        }else{
            ivUser.setImageResource(R.drawable.btn_female);
        }
        if(ConstantsUtil.checkFileKeyExist(PATH_PROFILE_PIC_SAVED)){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bm = BitmapFactory.decodeFile(ConstantsUtil.FILE_PATH_APP_ROOT+PATH_PROFILE_PIC_SAVED, options);
            ivUser.setImageBitmap(bm);
        }else {
            new LoadImageTask(this).execute(User.getInstance().getThumbUrl());
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String uri) {
        if (mListener != null) {
            mListener.onSettingsFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSettingsFragmentInteractionListener) {
            mListener = (OnSettingsFragmentInteractionListener) context;
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_settings_invite_facebook: {
                inviteFriends(getActivity());
            }
            break;
            default:
                break;
        }
    }


    public void inviteFriends(final Activity activity) {
        //String AppURl = "https://fb.me/1785806635011755";
        //String AppURl = "https://fb.me/1782218472037238";//first
       // https://fb.me/1785813168344435
        String AppURl = "https://fb.me/1785813168344435";//new 18nov2:56
        //String AppURl = "https://goo.gl/dmqzHU";
        //String AppURl = "https://bit.ly:443/2fZhc9r";
        //String AppURl = "https://www.facebook.com/stylemylooksapp";
        String previewImageUrl = "https://s3-ap-southeast-1.amazonaws.com/inkarnestore/push/referral.png";

        //dev
        //AppURl = "https://fb.me/983117305126017"; //without canvas 18nov
        fbCallbackManager = CallbackManager.Factory.create();
        if (AppInviteDialog.canShow()) {
            AppInviteContent content = new AppInviteContent.Builder()
                    .setApplinkUrl(AppURl).setPreviewImageUrl(previewImageUrl)
                    .build();

            AppInviteDialog appInviteDialog = new AppInviteDialog(activity);
            appInviteDialog.registerCallback(fbCallbackManager,
                    new FacebookCallback<AppInviteDialog.Result>() {
                        @Override
                        public void onSuccess(AppInviteDialog.Result result) {
                            Log.e("Invitation", "Invitation Sent Successfully");
                            points += 100;
                            //int points =
                            //tvPoints.setText(""+points);
                            if(getActivity() != null)
                            Toast.makeText(getActivity(),"Relevant points will be updated soon",Toast.LENGTH_SHORT).show();
                            User.getInstance().setPoints(points);
                            InkarneAppContext.getDataSource().create(User.getInstance());
                            updatePointsToServer(points);
                            trackEvent("facebook invite","","");
                            logger.logEvent("facebook invite");
                        }

                        @Override
                        public void onCancel() {
                            Log.e("Invitation", "canceled");
                        }

                        @Override
                        public void onError(FacebookException e) {
                            Log.e("Invitation", "Error Occured");
                        }
                    });

            appInviteDialog.show(content);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        //64213
        //fbCallbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 64213) {
            fbCallbackManager.onActivityResult(requestCode, resultCode, data);
            int i =0;
        }
    }

    private void updatePointsToServer(int points){
        final String uri = ConstantsUtil.URL_BASEPATH_0 + ConstantsUtil.URL_METHOD_UPDATE_POINTS + User.getInstance().getmUserId() + "/" + points;
        DataManager.getInstance().updateMethodToServer(uri, ConstantsUtil.EUpdateType.eUpdateTypePoints.toString(), new DataManager.OnResponseHandlerInterface() {
            @Override
            public void onResponse(Object obj) {

            }

            @Override
            public void onResponseError(String errorMessage, int errorCode) {

            }
        });
    }

    private void getSaveImagePath(Bitmap finalBitmap) {
        String savedImagePath = null;
        File myDir = new File(ConstantsUtil.FILE_PATH_APP_ROOT);
        myDir.mkdirs();
        String path = ConstantsUtil.FILE_PATH_APP_ROOT + PATH_PROFILE_PIC_SAVED;
        File file = new File(path);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            savedImagePath = file.getAbsolutePath();
            Log.e(LOGTAG, " filePathAdjustedPic :" + savedImagePath);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onImageLoaded(Bitmap bitmap) {
        ivUser.setImageBitmap(bitmap);
        getSaveImagePath(bitmap);
    }

    @Override
    public void onError() {

    }

    public class SettingsDialog {
        public SwitchCompat switchIsAutRotate;
        public  SwitchCompat switchIsWifiOnly;
        public Button btnSettingsCancel;
        public  Button btnSettingsSave;
        private AlertDialog alertDialog;

        public SettingsDialog(){
            createSettingDialog();
        }

        private void dismissSettingDialog(){
            alertDialog.dismiss();
        }

        private void showSettingDialog(){
            if(alertDialog == null)
                createSettingDialog();

            switchIsWifiOnly.setChecked(InkarneAppContext.getSettingIsWifiOnlyDownload());
            switchIsAutRotate.setChecked(InkarneAppContext.getSettingIsAutoRotateLookDisabled());
            alertDialog.show();
        }

        private void createSettingDialog() {
            if (alertDialog == null) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_settings, null);
                dialogBuilder.setView(dialogView);
                switchIsWifiOnly = (SwitchCompat) dialogView.findViewById(R.id.switch_wifi_only);
                switchIsAutRotate = (SwitchCompat) dialogView.findViewById(R.id.switch_auto_rotate);
                btnSettingsCancel = (Button) dialogView.findViewById(R.id.btn_settings_cancel);
                btnSettingsSave = (Button) dialogView.findViewById(R.id.btn_settings_save);
                alertDialog = dialogBuilder.create();

                switchIsAutRotate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    }
                });

                switchIsWifiOnly.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        //InkarneAppContext.saveSettingIsWifiOnlyDownload(isChecked);
                        //alertDialog.dismiss();
                    }
                });

                btnSettingsSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InkarneAppContext.saveSettingIsWifiOnlyDownload(switchIsWifiOnly.isChecked());
                        InkarneAppContext.saveSettingIsAutoRotateLookDisabled(switchIsAutRotate.isChecked());
                        alertDialog.dismiss();
                    }
                });

                btnSettingsCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switchIsWifiOnly.setChecked(InkarneAppContext.getSettingIsWifiOnlyDownload());
                        switchIsAutRotate.setChecked(InkarneAppContext.getSettingIsAutoRotateLookDisabled());
                        alertDialog.dismiss();
                    }
                });
            }
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
    public interface OnSettingsFragmentInteractionListener {
        // TODO: Update argument type and name
        void onSettingsFragmentInteraction(String uri);
    }
}
