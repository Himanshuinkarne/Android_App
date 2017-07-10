package com.svc.sml.Adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.VideoView;

import com.svc.sml.Helper.ImageFetcher;
import com.svc.sml.R;
import com.svc.sml.Utility.ConstantsUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by himanshu on 9/6/16.
 */
public class InstructionAdapter extends PagerAdapter {
    private final static String LOGTAG = InstructionAdapter.class.getSimpleName();

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private InstructionAdapterListener listener;
    private List<Integer> listSteps ;
    public ImageFetcher imageFetcher;
    private View itemView ;
    private VideoView videoViewInScreen;

    public interface InstructionAdapterListener {
        void onPageSelected(int index);
    }

    public List<Integer> getListSteps() {
        return listSteps;
    }

    public void setListSteps(ArrayList<Integer> listSteps) {
        this.listSteps = listSteps;
        notifyDataSetChanged();
    }

    public InstructionAdapter(Context context,List<Integer> items, InstructionAdapterListener listener) {
        mContext = context;
        this.listener = listener;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listSteps = items;
        imageFetcher = new ImageFetcher(context);
        initVideoItem();
    }

    public InstructionAdapter(Context context, InstructionAdapterListener listener) {
        mContext = context;
        this.listener = listener;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageFetcher = new ImageFetcher(context);
        //initVideoItem();
    }

    private void initVideoItem(){
        itemView = mLayoutInflater.inflate(R.layout.list_item_intruction_board_video, null, false);
        videoViewInScreen = (VideoView) itemView.findViewById(R.id.vv_info_face_selection);
        videoViewInScreen.setVisibility(View.VISIBLE);
        String videoPathInScreen = ConstantsUtil.FILE_PATH_RAW_FOLDER + mContext.getPackageName() + "/" + R.raw.v_inst_3;
        videoViewInScreen.setVideoPath(videoPathInScreen);
        videoViewInScreen.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
            }
        });
        videoViewInScreen.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //if(videoPathInScreen.equals(videoPathInScreen2))
                mp.setLooping(true);
            }
        });
        //videoViewInScreen.start();
        videoViewInScreen.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                /*
                if(videoView.isPlaying())
                   videoView.pause();
                else
                videoView.resume();
                */
                return true;
            }
        });
        videoViewInScreen.start();
    }


    @Override
    public int getCount() {
        if (listSteps != null)
            return listSteps.size();
        else
            return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        if(position == listSteps.size()-1){
            if(itemView == null){
                initVideoItem();
            }
//            View itemView = mLayoutInflater.inflate(R.layout.list_item_intruction_board_video, container, false);
//            VideoView videoViewInScreen = (VideoView)itemView.findViewById(R.id.vv_info_face_selection);
//            initVideo(videoViewInScreen);
//            videoViewInScreen.setVisibility(View.VISIBLE);
                if(!videoViewInScreen.isPlaying())
                 videoViewInScreen.start();
            container.addView(itemView);
            return itemView;
        }else {
            View itemView = mLayoutInflater.inflate(R.layout.list_item_intruction_board, container, false);
            ImageView ivThumbnail = (ImageView) itemView.findViewById(R.id.iv_lookboard);
            //ProgressBar pb = (ProgressBar) itemView.findViewById(R.id.pb_combo_gallery_item);
            ivThumbnail.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //Toast.makeText(mContext, "Page " + position + " clicked", Toast.LENGTH_LONG).show();
                    Log.e(LOGTAG, +position + " clicked");
                    if (listener != null)
                        listener.onPageSelected(position);

                }
            });

            ivThumbnail.setImageResource(listSteps.get(position));
            container.addView(itemView);
            return itemView;
        }
    }

    public void initVideo(VideoView videoViewInScreen){
        videoViewInScreen.setVisibility(View.VISIBLE);
        String videoPathInScreen = ConstantsUtil.FILE_PATH_RAW_FOLDER + mContext.getPackageName() + "/" + R.raw.v_face_selection_light_instruction_loop_female;
        videoViewInScreen.setVideoPath(videoPathInScreen);
        videoViewInScreen.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
            }
        });
        videoViewInScreen.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //if(videoPathInScreen.equals(videoPathInScreen2))
                mp.setLooping(true);
            }
        });
        //videoViewInScreen.start();
        videoViewInScreen.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                /*
                if(videoView.isPlaying())
                   videoView.pause();
                else
                videoView.resume();
                */
                return true;
            }
        });
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((FrameLayout) object);
    }

}