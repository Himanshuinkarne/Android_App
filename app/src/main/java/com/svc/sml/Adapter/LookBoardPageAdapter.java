package com.svc.sml.Adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.svc.sml.Database.ComboData;
import com.svc.sml.Helper.ImageFetcher;
import com.svc.sml.InkarneAppContext;
import com.svc.sml.Model.LookBoardItem;
import com.svc.sml.R;

import java.util.ArrayList;

/**
 * Created by himanshu on 9/6/16.
 */
public class LookBoardPageAdapter extends PagerAdapter {
    private final static String LOGTAG = LookBoardPageAdapter.class.getSimpleName();
    Context mContext;
    LayoutInflater mLayoutInflater;
    private String category;
    private LookBoardPageAdapterListener listener;
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 120;//250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 150;//200;

    private GestureDetector gestureDetector;
    private View.OnTouchListener gestureListener;
    private LookBoardItem clickedLBItem;

    public interface LookBoardPageAdapterListener {
        // TODO: Update argument type and name
        //void onComboSelected(String categoryTitle, ArrayList<ComboData> comboList, ComboData item);
        //void onComboDataSelected(String categoryTitle, ArrayList<ComboData> comboList, int position);
        void onComboDataSelected(ComboData comboData);
        void onDownwardGesture();
        void onUpwardGesture();
    }

    public ArrayList<LookBoardItem> getListLookBoard() {
        return listLookBoard;
    }


    public void setListLookBoard(ArrayList<LookBoardItem> listLookBoard) {
        this.listLookBoard = listLookBoard;
        notifyDataSetChanged();
    }

    ArrayList<LookBoardItem> listLookBoard;
    public ImageFetcher imageFetcher;

    public LookBoardPageAdapter(Context context, ArrayList<LookBoardItem> items, LookBoardPageAdapterListener listener) {
        mContext = context;
        this.listener = listener;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listLookBoard = items;
        imageFetcher = new ImageFetcher(context);
        gestureDetector = new GestureDetector(mContext,new MyGestureDetector());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };
    }

    public LookBoardPageAdapter(Context context, ArrayList<LookBoardItem> items, LookBoardPageAdapterListener listener,String category) {
        mContext = context;
        this.listener = listener;
        this.category = category;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listLookBoard = items;
        imageFetcher = new ImageFetcher(context);
    }

    @Override
    public int getCount() {
        if (listLookBoard != null)
            return listLookBoard.size();
        else
            return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = mLayoutInflater.inflate(R.layout.list_item_lookboard, container, false);
        ImageView ivThumbnail = (ImageView) itemView.findViewById(R.id.iv_lookboard);
        ProgressBar pb = (ProgressBar) itemView.findViewById(R.id.pb_combo_gallery_item);
        ivThumbnail.setOnTouchListener(gestureListener);
        ivThumbnail.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Toast.makeText(mContext, "Page " + position + " clicked", Toast.LENGTH_LONG).show();
                Log.e(LOGTAG, +position + " clicked");
                LookBoardItem item = listLookBoard.get(position);
                clickedLBItem = item;

                clickedLBItem.setViewCount(clickedLBItem.getViewCount()+1);
                InkarneAppContext.getDataSource().createReconcileLookBoard(clickedLBItem,clickedLBItem.getDateReconcile());

                ComboData comboData = InkarneAppContext.getDataSource().getComboDataByComboID(item.getCombo_ID());
                if (comboData != null) {
                    //comboData.setLooksCategoryTitle(item.getCategory());
                    comboData.setLooksCategoryTitle(comboData.getCombo_Style_Category());//todo
                    if (listener != null)
                        listener.onComboDataSelected(comboData);
                }
            }
        });

        LookBoardItem item = listLookBoard.get(position);
        imageFetcher.manageSetImage(item.getCombo_ID(), item.getImage_Key_Name(), ivThumbnail, pb,0);
        container.addView(itemView);
        return itemView;
    }

    //@Override
    public boolean isViewFromObject1(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((FrameLayout) object);
    }

    private class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.e(LOGTAG,"gesture onSingleTapUp") ;

//            if(clickedLBItem != null) {
//                ComboData comboData = InkarneAppContext.getDataSource().getComboDataByComboID(clickedLBItem.getCombo_ID());
//                clickedLBItem.setViewCount(clickedLBItem.getViewCount()+1);
//                InkarneAppContext.getDataSource().createReconcileLookBoard(clickedLBItem,clickedLBItem.getDateReconcile());
//                //COMBO_LOCAL_VIEW_COUNT
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
                        if (listener != null) {
                            listener.onDownwardGesture();
                        }
                    }
                    else {
                        Log.e(LOGTAG,"gesture Upward Swipe") ;
                        //Toast.makeText(mContext, "Upward Swipe", Toast.LENGTH_SHORT).show();
                        if (listener != null) {
                             listener.onUpwardGesture();
                        }
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
            return true;
        }

    }

}