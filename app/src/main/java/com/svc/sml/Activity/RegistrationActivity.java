package com.svc.sml.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.svc.sml.Database.InkarneDataSource;
import com.svc.sml.Database.User;
import com.svc.sml.Helper.DataManager;
import com.svc.sml.InkarneAppContext;
import com.svc.sml.Interface.OnTermsDialogFragmentInteractionListener;
import com.svc.sml.R;
import com.svc.sml.Utility.Connectivity;
import com.svc.sml.Utility.ConstantsUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationActivity extends BaseActivity implements View.OnClickListener, OnTermsDialogFragmentInteractionListener {
    private static final String LOGTAG = RegistrationActivity.class.toString();
    public static final int DIALOG_FRAGMENT = 1;
    InkarneDataSource dataSource;
    private ProgressBar pbRegistration;
    private int countRetry = 0;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private TermsDialogFragment dialogFragTermsCondition;

    private static final int INITIAL_REQUEST = 1;
    private static final int REQUEST_CODE_LOCATION = INITIAL_REQUEST + 3;

    private static final String[] PERMISSION_LOCATION = {
            Manifest.permission.ACCESS_NETWORK_STATE
           // Manifest.permission.ACCESS_COARSE_LOCATION
    };

    protected int request_code;
    protected ProgressDialog progress_dialog;

    protected LoginButton btnFbLogin;
    protected CallbackManager fbCallbackManager;
    protected ProfileTracker trackerProfile;
    private AccessTokenTracker trackerToken;

    protected String gender = null;
    protected String facebookId = null;
    protected String personPhotoUrl = "";
    protected String firstName, lastName, DOB, mobileNumber, emailId;
    protected int day, month, year;
    protected String locationCity = "india";
    private GenderDialog genderDialog;
    private boolean mShowDialog;

    private String android_id = "";
    private String info = "";
    private TextView tvTC;
    private CheckBox cbTC;
    private Button btnGuestLogin;
    //private Button btnFbLogin;
    private Button btnFbCusLogin;
    private String loginType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        //btnFbLogin = (LoginButton)findViewById(R.id.login_button);
        tvTC = (TextView)findViewById(R.id.tvTC);
        cbTC = (CheckBox)findViewById(R.id.cbTC);
        cbTC.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
        tvTC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFragmentDialog(DIALOG_FRAGMENT);
            }
        });

        btnFbCusLogin = (Button) findViewById(R.id.fb_login_cus_button);
        btnFbLogin = (LoginButton) findViewById(R.id.fb_login_button);
        btnGuestLogin = (Button)findViewById(R.id.btn_guest_login);
        btnFbCusLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!cbTC.isChecked()){
                    Toast.makeText(getApplicationContext(),"Please agree Terms & Condition",Toast.LENGTH_SHORT).show();
                    return;
                }
                btnFbLogin.performClick();
            }
        });
        btnGuestLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!cbTC.isChecked()){
                    Toast.makeText(getApplicationContext(),"Please agree Terms & Condition",Toast.LENGTH_SHORT).show();
                    return;
                }
                firstName = "Guest";
                facebookId = "";
                lastName = "User";
                emailId = "";
                personPhotoUrl = "NA";
                loginType = "Guest";
                if(User.getInstance() != null && User.getInstance().getmUserId()!= null && !User.getInstance().getmUserId().isEmpty() ){
                    if(User.getInstance().getmFirstName() != null){
                        firstName = User.getInstance().getmFirstName();
                    }
                    if(User.getInstance().getmLastName() != null && !User.getInstance().getmLastName().isEmpty()){
                        lastName = User.getInstance().getmLastName();
                    }
                    if(User.getInstance().getEmailId() != null && !User.getInstance().getEmailId().isEmpty()){
                        emailId = User.getInstance().getEmailId();
                    }
                    if(User.getInstance().getmMobileNumber() != null && !User.getInstance().getmMobileNumber().isEmpty()){
                        mobileNumber = User.getInstance().getmMobileNumber();
                    }
                }
                if(genderDialog == null){
                    genderDialog = new GenderDialog();
                }
                genderDialog.showDialog();

            }
        });
        pbRegistration = (ProgressBar) findViewById(R.id.pb_registration);
        pbRegistration.setVisibility(View.INVISIBLE);
        dataSource = InkarneDataSource.getInstance(InkarneAppContext.getAppContext());
        android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

       // String htmltext = "<font color=#cc0029>I AGREE TO STYLEMYLOOKS </font> <font color=#aaaaff>T&C</font>";
        //<u>parragraph</u>
        String htmltext = "I AGREE TO STYLEMYLOOKS <u>T&C</u>";
        tvTC.setText(Html.fromHtml(htmltext));
        initFacebook();
    }

    protected void initFacebook() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        fbCallbackManager = CallbackManager.Factory.create();
        btnFbLogin.setReadPermissions(Arrays.asList("public_profile", "email", "user_birthday", "user_location", "user_friends"));
        trackerProfile = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                // Fetch user details from New Profile
            }
        };

        btnFbLogin.registerCallback(fbCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        if(!cbTC.isChecked()){
                            Toast.makeText(getApplicationContext(),"Please agree Terms & Condition",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Log.d("Success", "Login");
                        if (AccessToken.getCurrentAccessToken() != null) {
                            fbRequestData();
                            mShowDialog = true;
                        }
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(FacebookException exception) {

                    }
                });
    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if (requestCode == REQUEST_CODE_LOCATION && grantResults != null && grantResults.length != 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //  gps functionality
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);
        fbCallbackManager.onActivityResult(requestCode, responseCode, intent);
        int SIGN_IN_CODE = 2;//todo
        if (requestCode == SIGN_IN_CODE) {
            request_code = requestCode;
            if (responseCode != RESULT_OK) {
                progress_dialog.dismiss();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (dialogFragTermsCondition != null && dialogFragTermsCondition.isVisible()) {
            dialogFragTermsCondition.goBackIfCan();
        }
        super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (dialogFragTermsCondition != null && dialogFragTermsCondition.isVisible()) {
            dialogFragTermsCondition.goBackIfCan();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    public void onStart() {
        super.onStart();
    }

    public void onStop() {
        super.onStop();
    }


    @Override
    public void onResume() {
        super.onResume();
        countRetry = 0;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();

        // play with fragments here
//        if (mShowDialog) {
//            mShowDialog = false;
//
//            // Show only if is necessary, otherwise FragmentManager will take care
//            if (getSupportFragmentManager().findFragmentByTag(PROG_DIALOG_TAG) == null) {
//                new ProgressFragment().show(getSupportFragmentManager(), PROG_DIALOG_TAG);
//            }
//        }
    }
    //@Override
    protected void proceedWithSignIn1(){
        // if (verifyInput(true)) {//TODO
        //if (firstName != null && !firstName.isEmpty() && emailId != null && !emailId.isEmpty()){
        if (firstName != null && !firstName.isEmpty()){
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        showFragmentDialog(DIALOG_FRAGMENT);
                    }
                });
                //showFragmentDialog(DIALOG_FRAGMENT);
            }
    }

    protected void proceedWithSignIn(){
        // if (verifyInput(true)) {//TODO
        //if (firstName != null && !firstName.isEmpty() && emailId != null && !emailId.isEmpty()){
        if (firstName != null && !firstName.isEmpty()){
            if(User.getInstance() != null && User.getInstance().getmUserId()!=null && !User.getInstance().getmUserId().isEmpty()){
                loginServiceCall(true);
            }else {
                loginServiceCall(false);
            }
            //showFragmentDialog(DIALOG_FRAGMENT);
        }
    }


    public void fbRequestData() {
        //resetDBAndData();
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                JSONObject json = response.getJSONObject();
                Log.e(LOGTAG, "-------------------FACEBOOK--------------");
                Log.d("Json", String.valueOf(response));
                try {
                    if (json != null) {
                        if (json.has("first_name"))
                            firstName = json.getString("first_name");
                        if (json.has("last_name"))
                            lastName = json.getString("last_name");
                        if (json.has("email")) {
                            emailId = json.getString("email");
                            Log.e(LOGTAG, emailId);
                        }
                        if (json.has("birthday")) {
                            DOB = json.getString("birthday");
                            Log.e(LOGTAG, DOB);
                        }
                        if (json.has("id")) {
                            facebookId = json.getString("id");
                            Log.e(LOGTAG, ""+facebookId);
                        }
                        if (json.has("picture")) {
                            personPhotoUrl = json.getJSONObject("picture").getJSONObject("data").getString("url");
                            // set profile image to imageview using Picasso or Native methods
                        }
                        if (json.has("location")) {
                            JSONObject loc = json.getJSONObject("location");
                            if (json.has("name")) {
                                locationCity = loc.getString("name");
                                Log.e(LOGTAG, locationCity);
                            }
                        }
                        if(User.getInstance() != null && User.getInstance().getmGender()!=null && !User.getInstance().getmGender().isEmpty()){
                            gender = User.getInstance().getmGender();
                        }
                        if (gender == null || gender.isEmpty()) {
                            if (json.has("gender")) {
                                String gen = json.getString("gender");
                                if (gen.equals("male")) {
                                    gender = "m";//male
                                } else if (gen.equals("female"))  {
                                    gender = "f";
                                }
                                Log.e(LOGTAG, "fbRequestData gender: " + gender);
                                String t = "fbRequestData gender: " + gender;
                                proceedWithSignIn();

                            } else {
                                //Gender not available
                            }
                        }else{
                            proceedWithSignIn();
                        }
                        if (gender == null || gender.isEmpty()){
                            if(genderDialog == null){
                                genderDialog = new GenderDialog();
                            }
                            genderDialog.showDialog();
                        }
//                        proceedWithSignIn();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Bundle param = new Bundle();
        param.putString("fields", "id,first_name,last_name,link,email,birthday,gender,location,cover,picture.type(large)");//,picture.type(large)
        request.setParameters(param);
        request.executeAsync();
    }

    public class GenderDialog {
        private ImageView ivMale, ivFemale;
        private TextView tvMale, tvFemale;
        //private String gender;
        private AlertDialog alertDialog;

        public GenderDialog(){
            createDialog();
        }

        private void dismissSettingDialog(){
            alertDialog.dismiss();
        }

        private void showDialog(){
            if(alertDialog == null)
                createDialog();
            alertDialog.show();
        }

        private void createDialog() {
            if (alertDialog == null) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(RegistrationActivity.this);
                LayoutInflater inflater = RegistrationActivity.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_gender, null);

                ivFemale = (ImageView) dialogView.findViewById(R.id.iv_female);
                ivMale = (ImageView) dialogView.findViewById(R.id.iv_male);
//        tvFemale = (TextView) findViewById(R.id.tv_female);
//        tvMale = (TextView) findViewById(R.id.tv_male);
                dialogBuilder.setView(dialogView);
                alertDialog = dialogBuilder.create();


                ivFemale.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gender = "f";
                        ivFemale.setImageResource(R.drawable.btn_female_selected);
                        ivMale.setImageResource(R.drawable.btn_male);
                        if(facebookId == ""){
                            facebookId = "0001";
                        }
                        alertDialog.dismiss();
                        proceedWithSignIn();
                    }
                });

                ivMale.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gender = "m";
                        if(facebookId == ""){
                            facebookId = "0000";
                        }
                        ivMale.setImageResource(R.drawable.btn_male_selected);
                        ivFemale.setImageResource(R.drawable.btn_female);
                        alertDialog.dismiss();
                        proceedWithSignIn();
                    }
                });
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }
    }

    public boolean verifyInput(boolean showToast) {
        String errorMessage = "";
        boolean isVerified = true;
        if (gender == null || gender.isEmpty()) {
            errorMessage = "Please select gender";
            showAlertError(errorMessage, "");
            isVerified = false;
        }
        return isVerified;
    }

    protected void showAlertError(String title, String msg) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
        builder.setTitle(title)
                .setMessage(msg)
                .setCancelable(false)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();
    }


   public void showFragmentDialog(int type) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        Fragment prev = getActivity().getFragmentManager().findFragmentByTag("dialog");
//        if (prev != null) {
//            ft.remove(prev);
//        }
//        ft.addToBackStack(null);
        switch (type) {
            case DIALOG_FRAGMENT:
                dialogFragTermsCondition = TermsDialogFragment.newInstance(0);
                dialogFragTermsCondition.listener = RegistrationActivity.this;
                //loginServiceCall();
                dialogFragTermsCondition.setCancelable(true);
                dialogFragTermsCondition.setTargetFragment(dialogFragTermsCondition, DIALOG_FRAGMENT);
                dialogFragTermsCondition.show(getSupportFragmentManager().beginTransaction(), "term_condition_dialog");
                getSupportFragmentManager().beginTransaction().commitAllowingStateLoss();
                break;
            default:
                break;
        }
    }

    public void loginServiceCall(final boolean isUpdate) {
        if (countRetry < ConstantsUtil.COUNT_RETRY_SERVICE_CRITICAL)
            pbRegistration.setVisibility(View.VISIBLE);
        //resetDBAndData(); //TODO 19may
        if (!Connectivity.isConnected(RegistrationActivity.this)) {
            Toast.makeText(getApplicationContext(), "Please check your network connection", Toast.LENGTH_SHORT).show();
        }

        /* TODO Temporary for fast testing */
        if (gender == null || gender.isEmpty()) {
            String errorMessage = "Please select gender";
            showAlertError(errorMessage, "");
            return;
        }
        if (firstName == null || firstName.length() == 0) {
            Random r = new Random();
            int i1 = r.nextInt(900) + 101;
            firstName = "TestFirstName_" + i1;
        }
        if (lastName == null || lastName.length() == 0) {
            lastName = "NA";
        }
        if (emailId == null || emailId.isEmpty()) {
            emailId = "NA";
        }
        if (mobileNumber == null || mobileNumber.isEmpty()) {
            mobileNumber = "NA";
        }
        if (day == 0) {
            day = 1;
        }
        if (month == 0) {
            month = 1;
        }
        if (year == 0) {
            year = 1900;
        }


        //Mobile_OS_Version=Android_Lollipop_2.0&Device_Manufacturer=Samsung&Device_Model=s7&Device_Memory=4GB&Device_Processor=SnapDragon&Network_Type=Wifi
        //String osVersion = System.getProperty("os.version");
        String notFound = "NA";
        String UTF8 = "utf-8";
        int osAPI = Build.VERSION.SDK_INT;
        String osVersion = Build.VERSION.RELEASE;
        String brand_Manufacturer = Build.BRAND;
        String deviceModel = Build.MODEL;
        String deviceRAM = getTotalRAM();
        String internalMemory = getTotalInternalMemorySize();
        String extMemory = getTotalExternalMemorySize();
        String arch = System.getProperty("os.arch");
        String networkType = getNetworkType(RegistrationActivity.this);
        try {
            osVersion = Uri.encode(osVersion);
            firstName = Uri.encode(firstName);
            lastName = Uri.encode(lastName);

            if (brand_Manufacturer != null)
                brand_Manufacturer = Uri.encode(brand_Manufacturer);
            else
                brand_Manufacturer = notFound;

            if (deviceModel != null) {
                String dm = deviceModel;
                deviceModel = Uri.encode(deviceModel, UTF8);

                Log.d(LOGTAG, "Uri.encode(deviceModel, UTF8) : " + deviceModel);
                String dmNotUTF8 = Uri.encode(dm);
                Log.d(LOGTAG, "Uri.encode(dm) :" + dmNotUTF8);
                String dmURLEncode = URLEncoder.encode(dm, UTF8);
                Log.d(LOGTAG, "URLEncoder.encode(dm, UTF8) :" + dmURLEncode);
            } else
                deviceModel = notFound;

//            if (deviceRAM != null)
//                deviceRAM = URLEncoder.encode(deviceRAM, UTF8);
//            else
//                deviceRAM = notFound;

            if (arch != null)
                arch = Uri.encode(arch, UTF8);
            else
                arch = notFound;

            if (locationCity != null) {
                Log.d(LOGTAG, locationCity);
                String loc = locationCity;
                locationCity = Uri.encode(locationCity, UTF8);
            } else
                locationCity = notFound;
            networkType = getNetworkType(RegistrationActivity.this);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = null;

        if(isUpdate){
            //http://styleme-prod.ap-southeast-1.elasticbeanstalk.com/Service1.svc/UpgradeUserv4/5/f/test/test/7/6/1977/test@test.com/919923123153/india/?Mobile_OS_Version=6.0.1&Device_Manufacturer=samsung&Device_Model=SM-G930F&Device_Memory=3.48GB&Device_Internal_Storage=24GB&Device_External_Storage=24GB&Device_Processor=aarch64&Network_Type=Wifi
            // &Facebook_ID=10002014&Device_ID=android-20013feas7bcc820c
            User u = User.getInstance();
            url = ConstantsUtil.URL_BASEPATH_0 + ConstantsUtil.URL_METHOD_UPDATEUSER + u.getmUserId() + "/" + gender + "/" + firstName + "/" + lastName + "/" + u.getDob_day() + "/" + u.getDob_month() + "/" + u.getDob_year() + "/" + emailId + "/" + u.getmMobileNumber()
                    + "/" + locationCity
                    + "?Mobile_OS_Version=" + osVersion
                    + "&Device_Manufacturer=" + brand_Manufacturer
                    + "&Device_Model=" + deviceModel
                    + "&Device_Memory=" + deviceRAM
                    + "&Device_Internal_Storage=" + internalMemory
                    + "&Device_External_Storage=" + extMemory
                    + "&Device_Processor=" + arch
                    + "&Network_Type=" + networkType
                    + "&Facebook_ID=" + facebookId
                    + "&Device_ID="  + android_id;
        }else {

            resetDBAndData();
            http://styleme-prod.ap-southeast-1.elasticbeanstalk.com/Service1.svc/c/f/test/test/7/6/1977/test@test.com/919923123123/india/?Mobile_OS_Version=6.0.1&Device_Manufacturer=samsung&Device_Model=SM-G930F&Device_Memory=3.48GB&Device_Internal_Storage=24GB&Device_External_Storage=24GB&Device_Processor=aarch64&Network_Type=Wifi
            // &Facebook_ID=10002000&Device_ID=android-20013feas6bcc820c
             url = ConstantsUtil.URL_BASEPATH_0 + ConstantsUtil.URL_METHOD_CREATEUSER + gender + "/" + firstName + "/" + lastName + "/" + day + "/" + month + "/" + year + "/" + emailId + "/" + mobileNumber
                    + "/" + locationCity
                    + "?Mobile_OS_Version=" + osVersion
                    + "&Device_Manufacturer=" + brand_Manufacturer
                    + "&Device_Model=" + deviceModel
                    + "&Device_Memory=" + deviceRAM
                    + "&Device_Internal_Storage=" + internalMemory
                    + "&Device_External_Storage=" + extMemory
                    + "&Device_Processor=" + arch
                    + "&Network_Type=" + networkType
                     + "&Facebook_ID=" + facebookId
                     + "&Device_ID="  + android_id;

        }
        String cusUrl =
                ConstantsUtil.URL_BASEPATH_0 + ConstantsUtil.URL_METHOD_CREATEUSER + "gender" + "/" + "Guest" + "/" + "User" + "/" + "1" + "/" + "1" + "/" + "1990" + "/" + "NA" + "/" + "NA"
                        + "/" + locationCity
                        + "?Mobile_OS_Version=" + osVersion
                        + "&Device_Manufacturer=" + brand_Manufacturer
                        + "&Device_Model=" + deviceModel
                        + "&Device_Memory=" + deviceRAM
                        + "&Device_Internal_Storage=" + internalMemory
                        + "&Device_External_Storage=" + extMemory
                        + "&Device_Processor=" + arch
                        + "&Network_Type=" + networkType
                        + "&Facebook_ID=" + "facebook_id"
                        + "&Device_ID="  + android_id;
        InkarneAppContext.saveSPValue("login_url",cusUrl);


        DataManager.getInstance().requestCreateUser(url, new DataManager.OnResponseHandlerInterface() {
            @Override
            public void onResponse(Object obj) {
                String userId = (String) obj;
                Log.d(LOGTAG, "Registration Successful userId :" + userId);
                User user = new User();
                user.setmUserId(userId);
                user.setmFirstName(firstName);
                user.setmLastName(lastName);
                user.setmMobileNumber(mobileNumber);
                user.setmGender(gender);
                user.setDob_dd_mmm_yyyy(day + "-" + month + "-" + year);
                user.setDob_day(day);
                user.setDob_month(month);
                user.setDob_year(year);
                user.setEmailId(emailId);
                user.setThumbUrl(personPhotoUrl);
                user.setmPIN(" ");
                user = InkarneAppContext.getDataSource().create(user);
                User.setInstance(user);
                User.getInstance().saveUserId(user.getmUserId());

                launchBMActivity();
                pbRegistration.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onResponseError(String errorMessage, int errorCode) {
                pbRegistration.setVisibility(View.INVISIBLE);
                countRetry++;
                if (countRetry == 1) {
                    Toast.makeText(RegistrationActivity.this, "Please check your network connection", Toast.LENGTH_SHORT).show();
                }
                if (countRetry < ConstantsUtil.COUNT_RETRY_SERVICE_CRITICAL) {
                    loginServiceCall(isUpdate);
                }
            }
        });
    }


    private void launchBMActivity() {
        //Intent myIntent = new Intent(getActivity(), FaceSelectionActivity.class);
        Intent myIntent = new Intent(RegistrationActivity.this, BodyMeasurementActivity.class);
        //myIntent.putExtra("key", ""); //Optional parameters
        startActivity(myIntent);
        finish();
    }

    private void resetDBAndData() {
        if (dataSource != null)
            dataSource.close();
        getApplicationContext().deleteDatabase("inkarne.db");
        File inkarneDir = new File(ConstantsUtil.FILE_PATH_APP_ROOT+"inkarne" );
        ConstantsUtil.deleteDirectory(inkarneDir);
        getSharedPreferences("inkarne", 0).edit().clear().commit();
        inkarneDir.delete();
    }


//    public static void verifyStoragePermissions(Activity activity) {
//        // Check if we have write permission
//        int permissionFine = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
//        boolean isPermissionFine = true;
//        if (permissionFine != PackageManager.PERMISSION_GRANTED) {
//            isPermissionFine = false;
//            //We don't have permission so prompt the user
////            ActivityCompat.requestPermissions(
////                    activity,
////                    PERMISSION_LOCATION,
////                    REQUEST_CODE_LOCATION
////            );
//        }
//
//        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
//        if (permission != PackageManager.PERMISSION_GRANTED || !isPermissionFine) {
//            //We don't have permission so prompt the user
//            ActivityCompat.requestPermissions(
//                    activity,
//                    PERMISSION_LOCATION,
//                    REQUEST_CODE_LOCATION
//            );
//        }
//    }

    /**********************
     * Get Analytics Data
     **********************/

//    private void initLocation() {
//        if (ActivityCompat.checkSelfPermission(RegistrationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(RegistrationActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        locationManager = (LocationManager) RegistrationActivity.this.getSystemService(Context.LOCATION_SERVICE);
//        Criteria criteria = new Criteria();
//        String provider = locationManager.getBestProvider(criteria, false);
//        if (provider == null)
//            provider = LocationManager.NETWORK_PROVIDER;
//        Location location = locationManager.getLastKnownLocation(provider);
//        // Define a listener that responds to location updates
//        if (location == null) {
//            locationListener = new LocationListener() {
//                public void onLocationChanged(Location location) {
//                    // Called when a new location is found by the network location provider.
//                    locationCity = getCityName(location);
//                }
//
//                public void onStatusChanged(String provider, int status, Bundle extras) {
//                }
//
//                public void onProviderEnabled(String provider) {
//                    // Toast.makeText(getActivity(), "Enabled new provider " + provider,Toast.LENGTH_SHORT).show();
//                    Log.e(LOGTAG, "Enabled new provider " + provider);
//                }
//
//                public void onProviderDisabled(String provider) {
//                    //Toast.makeText(getActivity(), "Disabled provider " + provider, Toast.LENGTH_SHORT).show();
//                }
//            };
//            // Register the listener with the Location Manager to receive location updates
//            if (ActivityCompat.checkSelfPermission(RegistrationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                    && ActivityCompat.checkSelfPermission(RegistrationActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                return;
//            }
//            //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10, 100, locationListener);
//            locationManager.requestLocationUpdates(provider, 0, 0, locationListener);
//        } else {
//            locationCity = getCityName(location);
//        }
//    }

    public String getCityName(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();

        Geocoder geoCoder = new Geocoder(RegistrationActivity.this, Locale.getDefault());
        try {
            List<Address> address = geoCoder.getFromLocation(lat, lng, 1);
            Log.e(LOGTAG, "address: " + address);
            if (address != null && address.size() != 0) {

                StringBuilder builder = new StringBuilder();
                int maxLines = address.get(0).getMaxAddressLineIndex();
                for (int i = 0; i < maxLines; i++) {
                    String addressStr = address.get(0).getAddressLine(i);
                    builder.append(addressStr);
                    builder.append(" ");
                }
                String finalAddress = builder.toString(); //This is the complete address.

                String locationcity = address.get(0).getAddressLine(maxLines - 1);
                String[] split = locationcity.split(",");
                locationCity = split[0];
                Log.e(LOGTAG, "address getAddressLine :" + locationCity);

                if (locationCity == null || locationCity.isEmpty())
                    locationCity = address.get(0).getLocality();
                Log.e(LOGTAG, "address getLocality  " + address.get(0).getLocality());

                if (locationCity != null && locationCity.length() != 0) {
//                    if (ActivityCompat.checkSelfPermission(RegistrationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
//                            || ActivityCompat.checkSelfPermission(RegistrationActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                        if (locationManager != null && locationListener != null)
//                            locationManager.removeUpdates(locationListener);
//                    }
                }
                return locationCity;
            }
        } catch (IOException e) {

        } catch (NullPointerException e) {
            //Handle NullPointerException
            return "India";
        }
        return null;
    }

    public String getTotalRAM() {

        /*
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) getActivity().getSystemService(getActivity().ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        long availableMegs = mi.availMem / 1048576L;
        */

        RandomAccessFile reader = null;
        String load = null;
        DecimalFormat twoDecimalForm = new DecimalFormat("#.##");
        double totRam = 0;
        String lastValue = "";
        try {
            reader = new RandomAccessFile("/proc/meminfo", "r");
            load = reader.readLine();

            // Get the Number value from the string
            Pattern p = Pattern.compile("(\\d+)");
            Matcher m = p.matcher(load);
            String value = "";
            while (m.find()) {
                value = m.group(1);
            }
            reader.close();

            totRam = Double.parseDouble(value);
//            double mb = totRam / 1024.0;
            double gb = totRam / 1048576.0;
//            double tb = totRam / 1073741824.0;
            lastValue = twoDecimalForm.format(gb).concat("GB");
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            // Streams.close(reader);
        }
        return lastValue;
    }


    public static String getNetworkType(Context context) {
        if (Connectivity.isConnectedWifi(context))
            return "Wifi";
        TelephonyManager mTelephonyManager = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        int networkType = mTelephonyManager.getNetworkType();
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "2G";
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                /**
                 From this link https://goo.gl/R2HOjR ..NETWORK_TYPE_EVDO_0 & NETWORK_TYPE_EVDO_A
                 EV-DO is an evolution of the CDMA2000 (IS-2000) standard that supports high data rates.

                 Where CDMA2000 https://goo.gl/1y10WI .CDMA2000 is a family of 3G[1] mobile technology standards for sending voice,
                 data, and signaling data between mobile phones and cell sites.
                 */
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                //Log.d("Type", "3g");
                //For 3g HSDPA , HSPAP(HSPA+) are main  networktype which are under 3g Network
                //But from other constants also it will 3g like HSPA,HSDPA etc which are in 3g case.
                //Some cases are added after  testing(real) in device with 3g enable data
                //and speed also matters to decide 3g network type
                //http://goo.gl/bhtVT
                return "3G";
            case TelephonyManager.NETWORK_TYPE_LTE:
                //No specification for the 4g but from wiki
                //I found(LTE (Long-Term Evolution, commonly marketed as 4G LTE))
                //https://goo.gl/9t7yrR
                return "4G";
            default:
                return "NA";
        }
    }

    public String getExternalStorageInMB() {
        StatFs stat = new StatFs(ConstantsUtil.FILE_PATH_APP_ROOT);
        long bytesAvailable = (long) stat.getBlockSize() * (long) stat.getAvailableBlocks();
        long megAvailable = bytesAvailable / (1024 * 1024);
        //long GBAvailable =  (bytesAvailable / (1024 * 1048576));
        Log.e("", "Available MB : " + megAvailable);
        return String.valueOf(megAvailable);
    }

    public static boolean externalMemoryAvailable() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }

    public static String getTotalExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            long GBAvailable = (totalBlocks * blockSize / (1024 * 1048576));
            return String.valueOf(GBAvailable) + "GB";
        } else {
            return "NA";
        }
    }

    public static String getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        long GBAvailable = (totalBlocks * blockSize / (1024 * 1048576));
        return String.valueOf(GBAvailable) + "GB";
    }

    @Override
    public void onTermsFragmentInteraction() {
        if(User.getInstance() != null && User.getInstance().getmUserId()!=null && !User.getInstance().getmUserId().isEmpty()){
            loginServiceCall(true);
        }else {
            loginServiceCall(false);
        }
    }

    @SuppressLint("ValidFragment")
    public static class TermsDialogFragment extends DialogFragment {
        public ProgressBar pbWebView;
        public WebView webView;
        private CheckBox checkBox;
        private TextView tvTitle;
        private ImageButton btnBack;
        private String url = "http://www.stylemylooks.com/termsofuse.htm";
        public OnTermsDialogFragmentInteractionListener listener;

        public TermsDialogFragment() {

        }

        public static TermsDialogFragment newInstance(int num) {
            TermsDialogFragment dialogFragment = new TermsDialogFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("num", num);
            dialogFragment.setArguments(bundle);
            return dialogFragment;
        }

        public void goBackIfCan() {
            if (webView.canGoBack()) {
                webView.goBack();
            }
        }

        public void onResume() {
            super.onResume();
            getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        if (webView.canGoBack()) {
                            webView.goBack();
                            return false;
                        }else {
                            dismiss();
                        }
                        return true; // pretend we've processed it
                    } else
                        return false; // pass on to be processed as normal
                }
            });
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            View view = getActivity().getLayoutInflater().inflate(R.layout.dailog_reg_terms_condition, new LinearLayout(getActivity()), false);
            pbWebView = (ProgressBar) view.findViewById(R.id.pb_web);
            tvTitle = (TextView) view.findViewById(R.id.tv_header_title_terms_condition);
            btnBack = (ImageButton) view.findViewById(R.id.btn_back_terms_condition);
            webView = (WebView) view.findViewById(R.id.wb_terms_condition);
//            checkBox = (CheckBox) view.findViewById(R.id.cb_reg_terms_condition);
//            checkBox.setOnCheckedChangeListener(
//                    new CompoundButton.OnCheckedChangeListener() {
//                        @Override
//                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                            dismiss();
//                            if(listener != null){
//                                listener.onTermsFragmentInteraction();
//                            }
//                        }
//                    }
//            );

            //webView.loadUrl("file:///android_asset/terms_condition.html");
            Dialog builder = new Dialog(getActivity());
            builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
            builder.setContentView(view);
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    pbWebView.setVisibility(View.VISIBLE);
                    view.loadUrl(url);
                    return true;
                }

                @Override
                public void onPageFinished(WebView view, final String url) {
                    pbWebView.setVisibility(View.INVISIBLE);
                }
            });
            webView.loadUrl(url);
            return builder;
        }
    }

    protected void initFacebook1() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        fbCallbackManager = CallbackManager.Factory.create();
        btnFbLogin.setReadPermissions(Arrays.asList("public_profile", "email", "user_birthday", "user_location", "user_friends"));
        trackerProfile = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                // Fetch user details from New Profile
            }
        };

        btnFbLogin.registerCallback(fbCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();
                Profile profile = Profile.getCurrentProfile();
                info = ("User ID: " + loginResult.getAccessToken().getUserId() + "\n" + "Auth Token: " + loginResult.getAccessToken().getToken());
            }

            @Override
            public void onCancel() {
                info = ("Login attempt canceled.");
            }

            @Override
            public void onError(FacebookException e) {
                info = ("Login attempt failed.");
            }
        });
        Log.d(LOGTAG,info);
        trackerToken = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

            }
        };
        trackerToken.startTracking();
    }
}
