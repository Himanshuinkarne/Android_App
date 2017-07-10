package com.svc.sml.Activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.svc.sml.Adapter.InstructionAdapter;
import com.svc.sml.R;
import com.svc.sml.Utility.CircleIndicator;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static com.svc.sml.Activity.FaceSelectionActivity.EXTRA_PARAM_PIC_PATH;
import static com.svc.sml.Activity.FaceSelectionActivity.EXTRA_PARAM_PIC_SOURCE_TYPE;

public class InstructionActivity extends AppCompatActivity {
    public static final String LOGTAG = "InstructionActivity";
    private static final int REQ_CODE_PICK_IMAGE = 1888;
    private static final int REQUEST_IMAGE_CAPTURE = 1889;
    private String CAM_PIC_NAME = "usercam-"+ System.currentTimeMillis()+".jpeg";

    private final static List<Integer> listInsImages = Arrays.asList(R.drawable.inst_1, R.drawable.inst_2, R.drawable.inst_1);
    private ViewPager vPagerInstruction;
    private InstructionAdapter instructionAdapter;
    private CircleIndicator indicator;
    private Button btnNext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        btnNext = (Button) findViewById(R.id.btn_shared_next);
        vPagerInstruction = (ViewPager)findViewById(R.id.vpager_lookboard);
        indicator = (CircleIndicator)findViewById(R.id.look_board_indicator);
//        instructionAdapter = new InstructionAdapter(InstructionActivity.this,listInsImages, new InstructionAdapter.InstructionAdapterListener() {
//            @Override
//            public void onPageSelected(int index) {
//
//            }
//        });
//        vPagerInstruction.setOffscreenPageLimit(3);
//        if (vPagerInstruction.getAdapter() == null) {
//            vPagerInstruction.setAdapter(instructionAdapter);
//            indicator.setViewPager(vPagerInstruction);
//        }
    }

    private void initView(){
        if (vPagerInstruction.getAdapter() == null) {
            if(instructionAdapter == null){
                instructionAdapter = new InstructionAdapter(InstructionActivity.this,listInsImages, new InstructionAdapter.InstructionAdapterListener() {
                    @Override
                    public void onPageSelected(int index) {

                    }
                });
            }
            vPagerInstruction.setAdapter(instructionAdapter);
            indicator.setViewPager(vPagerInstruction);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        initView();

    }
    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onStop() {
        super.onStop();
        instructionAdapter = null;
        vPagerInstruction.setAdapter(null);

    }

    public void nextBtnClickHandler(View v) {
        int ct = getItem(+1);
        if(ct >= listInsImages.size()){
            takePictureFromCamera();
        }else {
            vPagerInstruction.setCurrentItem(getItem(+1), true); //getItem(-1) for previous
        }
    }

    private int getItem(int i) {
        int c = vPagerInstruction.getCurrentItem() + i;
        return c;
    }

    private void takePictureFromCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File out = Environment.getExternalStorageDirectory();
        out = new File(out, CAM_PIC_NAME);
        Log.d(LOGTAG, out.getAbsolutePath());
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(out));
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            launchAdjustPicActivity(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + CAM_PIC_NAME);
        }
//        if (requestCode == REQ_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
//            Uri uri = data.getData();
//            try {
//                String imagePath = getPath(uri);
//                launchAdjustPicActivity(imagePath);
//            } catch (URISyntaxException e) {
//                Toast.makeText(getApplicationContext(), "Unable to get the file from the given URI.  See error log for details", Toast.LENGTH_LONG).show();
//                Log.e("DEBUG", "Unable to upload file from the given uri", e);
//            }
//        }
    }

    public void launchAdjustPicActivity(String imagePath) {
        Intent intent = new Intent(this, AdjustPicActivity.class);
        intent.putExtra(EXTRA_PARAM_PIC_PATH, imagePath);
        intent.putExtra(EXTRA_PARAM_PIC_SOURCE_TYPE, "0");
        startActivity(intent);
    }

}
