package com.svc.sml.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.svc.sml.R;

public class VideoFragment extends Fragment {
    private static final String ARG_PARAM_IS_EXTERNAL_POINTS =    "ARG_PARAM_IS_EXTERNAL_POINTS";
    private static final String ARG_PARAM_VIDEO_FILE_PATH    =    "ARG_PARAM_VIDEO_FILE_PATH";
    private static final String ARG_PARAM_CODE_RESULT    =    "ARG_PARAM_CODE_RESULT";
    private static final String ARG_PARAM_TITLE_VIDEO    =    "ARG_PARAM_TITLE_VIDEO";
    private int CODE_RESULT =0;

    private LinearLayout conBtnSkip;
    private VideoView videoView;
    private OnVideoFragmentInteractionListener mListener;
    private boolean isExternalPoint;
    private String[] videoFilePaths;
    private int indexVideo =0;
    private int codeResult;
    private TextView tvVideoTitle;
    private  String[] videoTitles;
    public VideoFragment() {
        // Required empty public constructor
    }


    public static VideoFragment newInstance(boolean isExternalPoints) {
        VideoFragment fragment = new VideoFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_PARAM_IS_EXTERNAL_POINTS, isExternalPoints);
        fragment.setArguments(args);
        return fragment;
    }

    public static VideoFragment newInstance(String[] videoFilePaths, String[] titles,int code) {
        VideoFragment fragment = new VideoFragment();
        Bundle args = new Bundle();
        args.putStringArray(ARG_PARAM_VIDEO_FILE_PATH, videoFilePaths);
        args.putInt(ARG_PARAM_CODE_RESULT, code);
        args.putStringArray(ARG_PARAM_TITLE_VIDEO, titles);
        //args.putString(ARG_PARAM_TITLE_VIDEO, titles);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //isExternalPoint = getArguments().getBoolean(ARG_PARAM_IS_EXTERNAL_POINTS,false);
            videoFilePaths = getArguments().getStringArray(ARG_PARAM_VIDEO_FILE_PATH);
            codeResult = getArguments().getInt(ARG_PARAM_CODE_RESULT);
            videoTitles = getArguments().getStringArray(ARG_PARAM_TITLE_VIDEO);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_video, container, false);
        videoView = (VideoView) v.findViewById(R.id.vv_info_face_selection);
        tvVideoTitle = (TextView)v.findViewById(R.id.tv_video_title);
        conBtnSkip = (LinearLayout) v.findViewById(R.id.con_btn_skip);
        conBtnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.stopPlayback();
                onVideoClose();
            }
        });
//        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            public void onCompletion(MediaPlayer mp) {
//                onVideoClose();
//            }
//
//        });
//        videoView.setOnTouchListener(new View.OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return true;
//            }
//        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        splashPlayer();
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onVideoClose() {
        if (mListener != null) {
            mListener.onVideoFragmentInteractionSkip(codeResult);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnVideoFragmentInteractionListener) {
            mListener = (OnVideoFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnLooksFragmentInteractionListener");
        }
    }


    // @Override
    public void onAttach(AppCompatActivity activity) {
        super.onAttach( activity);
        try {
            mListener = (OnVideoFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement FragmentCommunicator");
        }
    }

    /*
 * onAttach(Context) is not called on pre API 23 versions of Android and onAttach(Activity) is deprecated
 * Use onAttachToContext instead
 */

    /*
     * Deprecated on API 23
     * Use onAttachToContext instead
     */
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            onAttachToContext(activity);
        }
    }


        @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    protected void onAttachToContext(Activity context) {
        if (context instanceof OnVideoFragmentInteractionListener) {
            mListener = (OnVideoFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnLooksFragmentInteractionListener");
        }
    }

    public void splashPlayer() {
//        final VideoView videoView = (VideoView) findViewById(R.id.vv_info_face_selection);
//        conBtnSkip = (LinearLayout) findViewById(R.id.con_btn_skip);
//        conBtnSkip.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                video1.stopPlayback();
//                jumpMain();
//            }
//        });


//        String videoPath = null;
//        String basePath = "android.resource://" + getActivity().getPackageName() + "/";
//        if(isExternalPoint){
//            if(User.getInstance().getmGender().equals("m")){
//                videoPath = basePath+R.raw.v_fiducial_external_female;
//            }
//            else{
//                videoPath = basePath+R.raw.v_fiducial_external_female;
//            }
//        }
//        else{
//            if(User.getInstance().getmGender().equals("m")){
//                videoPath = basePath+R.raw.v_fiducial_internal_female;
//            }
//            else{
//                videoPath = basePath+R.raw.v_fiducial_internal_female;
//            }
//        }
        videoView.setVideoPath(videoFilePaths[indexVideo]);
        tvVideoTitle.setText(videoTitles[indexVideo]);
        videoView.start();
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                if (indexVideo == videoFilePaths.length - 1) {
                    onVideoClose();
                } else {
                    indexVideo++;
                    videoView.setVideoPath(videoFilePaths[indexVideo]);
                    tvVideoTitle.setText(videoTitles[indexVideo]);
                    videoView.start();
                }
            }

        });
        videoView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    public interface OnVideoFragmentInteractionListener {
        // TODO: Update argument type and name
        void onVideoFragmentInteractionSkip(int code);
    }
}
