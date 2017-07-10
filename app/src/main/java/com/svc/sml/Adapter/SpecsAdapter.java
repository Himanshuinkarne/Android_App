package com.svc.sml.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.svc.sml.Activity.OnAccessoryAdapterListener;
import com.svc.sml.Database.User;
import com.svc.sml.Model.BaseAccessoryItem;
import com.svc.sml.Model.FaceItem;
import com.svc.sml.Model.SpecsContent;
import com.svc.sml.R;
import com.svc.sml.Utility.AWSUtil;
import com.svc.sml.Utility.CircularProgressWheel;
import com.svc.sml.Utility.ConstantsUtil;

import java.util.ArrayList;

/**
 * Created by himanshu on 1/26/16.
 */
public class SpecsAdapter extends BaseAccessoryAdapter implements View.OnClickListener {
    private final static String LOGTAG = SpecsAdapter.class.toString();
    protected ArrayList<SpecsContent> arrayItem;

    public SpecsAdapter(Context context, ArrayList<SpecsContent> values) {
        //super(context, R.layout.h_list_item_hair, values);
        this.context = context;
        arrayItem = values;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        transferUtility = AWSUtil.getTransferUtility(context);
    }

    public SpecsAdapter(Context context, ArrayList<SpecsContent> values,FaceItem faceItem, OnAccessoryAdapterListener accessoryAdapterListener) {
        this.context = context;
        this.faceItem = faceItem;
        this.accessoryAdapterListener = accessoryAdapterListener;
        arrayItem = values;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        transferUtility = AWSUtil.getTransferUtility(context);
    }

    public void add(SpecsContent path) {
        arrayItem.add(path);
    }

    public void clear() {
        arrayItem.clear();
    }

    public void remove(int index) {
        arrayItem.remove(index);
    }

    @Override
    public int getCount() {
        return arrayItem.size();
    }

    @Override
    public SpecsContent getItem(int position) {
        return arrayItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;

        if (convertView == null) {
            // Inflate the view since it does not exist
            convertView = mInflater.inflate(R.layout.h_list_item_specs, parent, false);

            // Create and save off the holder in the tag so we get quick access to inner fields
            // This must be done for performance reasons
            holder = new Holder();
            holder.ivThumbnail1 = (ImageView) convertView.findViewById(R.id.iv_hair1);
            holder.ivThumbnail2 = (ImageView) convertView.findViewById(R.id.iv_hair2);
            holder.ivThumbnail3 = (ImageView) convertView.findViewById(R.id.iv_hair3);
            holder.ivThumbnail1.setOnClickListener(this);
            holder.ivThumbnail2.setOnClickListener(this);
            holder.ivThumbnail3.setOnClickListener(this);
            holder.textView1 = (TextView) convertView.findViewById(R.id.tv_hair1);
            holder.textView2 = (TextView) convertView.findViewById(R.id.tv_hair2);
            holder.textView3 = (TextView) convertView.findViewById(R.id.tv_hair3);
            holder.pb1 = (ProgressBar) convertView.findViewById(R.id.pb_hair1);
            holder.pb2 = (ProgressBar) convertView.findViewById(R.id.pb_hair2);
            holder.pb3 = (ProgressBar) convertView.findViewById(R.id.pb_hair3);
            holder.cpb1 = (CircularProgressWheel) convertView.findViewById(R.id.cpb_hair1);
            holder.cpb2 = (CircularProgressWheel) convertView.findViewById(R.id.cpb_hair2);
            holder.cpb3 = (CircularProgressWheel) convertView.findViewById(R.id.cpb_hair3);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        //Populate the text
        SpecsContent contentItem = getItem(position);
        BaseAccessoryItem item1 = contentItem.item1;
        BaseAccessoryItem item2 = null;
        BaseAccessoryItem item3 = null;
        if (contentItem.item2 != null) {
            item2 = contentItem.item2;
        }
        if (contentItem.item3 != null) {
            item3 = contentItem.item3;
        }

        BaseAccessoryAdapter.DownloadHolder dHolder1 = new BaseAccessoryAdapter.DownloadHolder();
        dHolder1.item = item1;
        dHolder1.cpb = holder.cpb1;
        dHolder1.pb = holder.pb1;
        holder.ivThumbnail1.setTag(dHolder1);

        BaseAccessoryAdapter.HolderItem holderItem1 = new BaseAccessoryAdapter.HolderItem();
        holderItem1.cpb = holder.cpb1;
        holderItem1.pb = holder.pb1;
        holderItem1.textView = holder.textView1;
        holderItem1.ivThumbnail = holder.ivThumbnail1;

        showData(item1, holderItem1);
        if (item2 == null) {
            holder.ivThumbnail2.setVisibility(View.INVISIBLE);
            holder.pb2.setVisibility(View.INVISIBLE);
            holder.textView2.setVisibility(View.INVISIBLE);
        } else {
            holder.ivThumbnail2.setVisibility(View.VISIBLE);
            holder.pb2.setVisibility(View.VISIBLE);
            holder.textView2.setVisibility(View.VISIBLE);
            //holder.textView2.setText(item2.getObjId()); donot show text
            holder.ivThumbnail2.setAlpha(ALPHA_IMAGE_DOWNLOADING);

            BaseAccessoryAdapter.DownloadHolder dholder = new BaseAccessoryAdapter.DownloadHolder();
            dholder.item = item2;
            dholder.cpb = holder.cpb2;
            dholder.pb = holder.pb2;
            holder.ivThumbnail2.setTag(dholder);

            BaseAccessoryAdapter.HolderItem holderItem = new BaseAccessoryAdapter.HolderItem();
            holderItem.cpb = holder.cpb2;
            holderItem.pb = holder.pb2;
            holderItem.ivThumbnail = holder.ivThumbnail2;
            holderItem.textView = holder.textView2;
            showData(item2, holderItem);
        }

        if (item3 == null) {
            holder.ivThumbnail3.setVisibility(View.INVISIBLE);
            holder.pb3.setVisibility(View.INVISIBLE);
            holder.textView3.setVisibility(View.INVISIBLE);
        } else {
            holder.ivThumbnail3.setVisibility(View.VISIBLE);
            holder.pb3.setVisibility(View.VISIBLE);
            holder.textView3.setVisibility(View.VISIBLE);

            //holder.textView3.setText(item3.getObjId()); donot show text
            holder.ivThumbnail3.setAlpha(ALPHA_IMAGE_DOWNLOADING);

            BaseAccessoryAdapter.DownloadHolder dholder3 = new BaseAccessoryAdapter.DownloadHolder();
            dholder3.item = item3;
            dholder3.cpb = holder.cpb3;
            dholder3.pb = holder.pb3;
            holder.ivThumbnail3.setTag(dholder3);

            BaseAccessoryAdapter.HolderItem holderItem = new BaseAccessoryAdapter.HolderItem();
            holderItem.cpb = holder.cpb3;
            holderItem.pb = holder.pb3;
            holderItem.ivThumbnail = holder.ivThumbnail3;
            holderItem.textView = holder.textView3;
            showData(item3, holderItem);
        }

        // Set the color
        //convertView.setBackgroundColor(getItem(position).getBackgroundColor());
        return convertView;
    }


    //@Override
    protected void clickHandlerOnDownloadedItem(BaseAccessoryItem item) {
        if (accessoryAdapterListener != null) {
            accessoryAdapterListener.onAccessorySelected(item);
        }
    }

    //@Override
    protected String getCreateAccessoryURL(BaseAccessoryItem item){
        //String url = "http://inkarnepub-prod.elasticbeanstalk.com/Service1.svc/CreateHairstyle/5/36/MHS002/m";
        String uri = ConstantsUtil.URL_BASEPATH+ConstantsUtil.URL_METHOD_CREATE_SPECS+ User.getInstance().getmUserId()+"/"+faceItem.getFaceId()+"/"+item.getObjId()+"/"+ User.getInstance().getmGender();
        Log.d(LOGTAG,uri);
        //uri =ConstantsUtil.URL_BASEPATH+ConstantsUtil.URL_METHOD_CREATE_SPECS+"5"+"/"+"36"+"/"+item.getObjId()+"/"+User.getInstance().getmGender();
        return uri;
    }

    @Override
    protected void updateViewSelection(BaseAccessoryItem item) {

    }

    //@Override
    protected void clickOnDownloadedItem(View v) {
        BaseAccessoryAdapter.DownloadHolder dHolder = (BaseAccessoryAdapter.DownloadHolder) v.getTag();
        BaseAccessoryItem item = dHolder.item;
        //Bitmap bitmap = getBitmap(item.getThumbnailAwsKey());
        //if (item.getThumbnailImage() == null)
        if (((ImageView)v).getDrawable() == null)
            return;
//        if (bitmap == null)
//            return;

        latestClickedItem = item;
        if (ConstantsUtil.checkFileKeysExist(item.getTextureAwsKey(), item.getObjAwsKey())) {
            for (SpecsContent hc : arrayItem) {
                hc.item1.isSelected = false;
                if (hc.item2 != null) {
                    hc.item2.isSelected = false;
                }
                if (hc.item3 != null) {
                    hc.item3.isSelected = false;
                }
            }
            item.isSelected = true;
            this.notifyDataSetChanged();
            clickHandlerOnDownloadedItem(item);
        } else {
            getOpenGLData(dHolder, (ImageView) v);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_hair1:
            case R.id.iv_hair2:
            case R.id.iv_hair3:{
                clickOnDownloadedItem(v);
            }
            break;
            default:
                break;
        }
    }

    /**
     * View holder for the views we need access to
     */
    private static class Holder {
        public TextView textView1;
        public TextView textView2;
        public TextView textView3;
        public ImageView ivThumbnail1;
        public ImageView ivThumbnail2;
        public ImageView ivThumbnail3;
        public ProgressBar pb1;
        public ProgressBar pb2;
        public ProgressBar pb3;
        public CircularProgressWheel cpb1;
        public CircularProgressWheel cpb2;
        public CircularProgressWheel cpb3;
    }
}