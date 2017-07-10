package com.svc.sml.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.svc.sml.Database.User;
import com.svc.sml.R;

public class FirstActivity extends AppCompatActivity {

    public static final String LOGTAG = "FirstActivity";
    private EditText MobileNumber, MPin;
    private ImageView mLogin, mRegister;
    private String UID = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_first);
        MobileNumber = (EditText) findViewById(R.id.etMobNo);
        MPin = (EditText) findViewById(R.id.etMpin);
        mLogin = (ImageView) findViewById(R.id.img_login);
        mRegister = (ImageView) findViewById(R.id.img_register);
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i1 = new Intent(FirstActivity.this, RegistrationActivity.class);
                startActivity(i1);
                finish();
            }
        });

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = User.getInstance();
                login(user);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void launchDataActivity() {
        Log.e(LOGTAG, " ******** Launch DataActivity in FirstActivity  *******");
        Intent intent = new Intent(FirstActivity.this, DataActivity.class);
        startActivity(intent);
    }


    public void login(User user) {
        if (user == null) {
            Log.e(LOGTAG, "User is null");
            Toast.makeText(getApplicationContext(), "New user? Please register.", Toast.LENGTH_SHORT).show();
            return;
        }
        //Toast.makeText(getApplicationContext(), "User: " + user.getmUserId() + "  :" + user.getmFirstName() + "  :" + user.getmGender(), Toast.LENGTH_SHORT).show();
        Log.d(LOGTAG, "User: " + user.getmUserId() + "  :" + user.getmFirstName() + "  :" + user.getmGender());
        if(user.getPBId() == null || user.getPBId().isEmpty()){
            Log.d(LOGTAG, "User PB is null/not created: ");
            Intent i1 = new Intent(FirstActivity.this, BodyMeasurementActivity.class);
            startActivity(i1);
            finish();
        }
        else {
            launchDataActivity();
        }
    }

    public void login1(User user) {
        if (user == null) {
            Log.e(LOGTAG, "User is null");
            Toast.makeText(getApplicationContext(), "New user? Please register.", Toast.LENGTH_SHORT).show();
            return;
        }
        //Toast.makeText(getApplicationContext(), "User: " + user.getmUserId() + "  :" + user.getmFirstName() + "  :" + user.getmGender(), Toast.LENGTH_SHORT).show();
        Log.d(LOGTAG, "User: " + user.getmUserId() + "  :" + user.getmFirstName() + "  :" + user.getmGender());
        if (user.getDefaultFaceItem() == null) {
            Log.d(LOGTAG, "User Face is null/not created: ");
            Intent i1 = new Intent(FirstActivity.this, FaceSelectionActivity.class);
            startActivity(i1);
            finish();
        }
//        else if(user.getDefaultFaceItem().getHairstyleId() == null || user.getDefaultFaceItem().getHairstyleId().isEmpty() ){
//            Log.d(LOGTAG, "User hairstyle is null/not created: ");
//            Intent i1 = new Intent(FirstActivity.this, HairStyleActivity.class);
//            startActivity(i1);
//            finish();
//        }
        else if(user.getPBId() == null || user.getPBId().isEmpty()){
            Log.d(LOGTAG, "User PB is null/not created: ");
            Intent i1 = new Intent(FirstActivity.this, BodyMeasurementActivity.class);
            startActivity(i1);
            finish();
        }
        else {
            launchDataActivity();
        }
    }

//    public void requestUserDatabase(String uri) {
//        boolean debug = true;
//        if (debug) {
//            User user = User.getInstance();
//            user.setmUserId(UID);
//            user.setmFirstName("firstName");
//            user.setmLastName("last name");
//            user.setmMobileNumber("1234567");
//            user.setEmailId("test@gmail.com");
//            user.setDob_dd_mmm_yyyy("22-MAR-1990");
//            user.setDob_day(22);
//            user.setDob_month(3);
//            user.setDob_year(1990);
//            user.setmGender("m");
//            user.setmPIN("1234");
//            user = InkarneAppContext.getDataSource().create(user);
//            return;
//        }
//
//        String content = HttpManager.getData(uri);
//        JSONObject parentObject = null;
//        User user = new User();
//        String first_name = null, last_name = null, gender = null, mobile_number = null, pin = null;
//        if (content != null && content.length() > 0) {
//            try {
//                parentObject = new JSONObject(content);
//                JSONObject userDetails = parentObject.getJSONObject("GetUserDetailsResult");
//                first_name = userDetails.getString("First_Name");
//                gender = userDetails.getString("Gender");
//                last_name = userDetails.getString("Last_Name");
//                mobile_number = userDetails.getString("Mobile_Number");
//                pin = userDetails.getString("PIN");
//
//
//                user.setmUserId(UID);
//                user.setmFirstName(first_name);
//                user.setmLastName(last_name);
//                user.setmMobileNumber(mobile_number);
//                user.setmGender(gender);
//                user.setmPIN(pin);
//                user = InkarneAppContext.getDataSource().create(user);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//    }

}
