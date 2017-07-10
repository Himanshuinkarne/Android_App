package com.svc.sml.Adapter;

import android.content.Context;
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
import com.svc.sml.Model.HairContent;
import com.svc.sml.R;
import com.svc.sml.Utility.AWSUtil;
import com.svc.sml.Utility.CircularProgressWheel;
import com.svc.sml.Utility.ConstantsUtil;

import java.util.ArrayList;

/**
 * An array adapter that knows how to render views when given CustomData classes
 */
public class HairAdapter extends BaseAccessoryAdapter implements View.OnClickListener {
    private final static String LOGTAG = HairAdapter.class.toString();
    protected ArrayList<HairContent> arrayItem;

    public HairAdapter(Context context, ArrayList<HairContent> values, FaceItem faceItem, OnAccessoryAdapterListener onAccessoryAdapterListener) {
        this.context= context;
        this.accessoryAdapterListener = onAccessoryAdapterListener;
        arrayItem = values;
        this.faceItem = faceItem;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        transferUtility = AWSUtil.getTransferUtility(context);
    }

    public HairAdapter(Context context, ArrayList<HairContent> values, OnAccessoryAdapterListener accessoryAdapterListener) {
        //super(context, R.layout.h_list_item_hair, values);
        this.context = context;
        this.accessoryAdapterListener = accessoryAdapterListener;
        arrayItem = values;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        transferUtility = AWSUtil.getTransferUtility(context);
    }

    public void add(HairContent path) {
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
    public HairContent getItem(int position) {
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
            convertView = mInflater.inflate(R.layout.h_list_item_hair, parent, false);

            // Create and save off the holder in the tag so we get quick access to inner fields
            // This must be done for performance reasons
            holder = new Holder();
            holder.ivThumbnail1 = (ImageView) convertView.findViewById(R.id.iv_hair1);
            holder.ivThumbnail2 = (ImageView) convertView.findViewById(R.id.iv_hair2);
            holder.ivThumbnail1.setOnClickListener(this);
            holder.ivThumbnail2.setOnClickListener(this);
            holder.textView1 = (TextView) convertView.findViewById(R.id.tv_hair1);
            holder.textView2 = (TextView) convertView.findViewById(R.id.tv_hair2);
            holder.pb1 = (ProgressBar) convertView.findViewById(R.id.pb_hair1);
            holder.pb2 = (ProgressBar) convertView.findViewById(R.id.pb_hair2);
            holder.cpb1 = (CircularProgressWheel) convertView.findViewById(R.id.cpb_hair1);
            holder.cpb2 = (CircularProgressWheel) convertView.findViewById(R.id.cpb_hair2);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        //Populate the text
        HairContent hairContentItem = getItem(position);
        BaseAccessoryItem item1 = hairContentItem.hairItem1;
        BaseAccessoryItem item2 = null;
        if (hairContentItem.hairItem2 != null) {
            item2 = hairContentItem.hairItem2;
        }

        DownloadHolder dHolder1 = new DownloadHolder();
        dHolder1.item = item1;
        dHolder1.cpb = holder.cpb1;
        dHolder1.pb = holder.pb1;
        holder.ivThumbnail1.setTag(dHolder1);


        HolderItem holderItem1= new HolderItem();
        holderItem1.textView = holder.textView1;
        holderItem1.cpb = holder.cpb1;
        holderItem1.pb = holder.pb1;
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
            holder.ivThumbnail2.setAlpha(ALPHA_IMAGE_DOWNLOADING);

            DownloadHolder dholder2 = new DownloadHolder();
            dholder2.item = item2;
            dholder2.cpb = holder.cpb2;
            dholder2.pb = holder.pb2;
            holder.ivThumbnail2.setTag(dholder2);

            HolderItem holderItem2 = new HolderItem();
            holderItem2.textView = holder.textView2;
            holderItem2.cpb = holder.cpb2;
            holderItem2.pb = holder.pb2;
            holderItem2.ivThumbnail = holder.ivThumbnail2;
            showData(item2, holderItem2);
        }

        //Set the color
        //convertView.setBackgroundColor(getItem(position).getBackgroundColor());
        return convertView;
    }


    //@Override
    protected synchronized void clickHandlerOnDownloadedItem(BaseAccessoryItem item){
        if (accessoryAdapterListener != null) {
            accessoryAdapterListener.onAccessorySelected(item);
        }
    }

    //@Override
    protected String getCreateAccessoryURL(BaseAccessoryItem item){
        //String url = "http://inkarnepub-prod.elasticbeanstalk.com/Service1.svc/CreateHairstyle/5/36/MHS002/m";
        String uri = ConstantsUtil.URL_BASEPATH+ConstantsUtil.URL_METHOD_CREATE_HAIRSTYLE+ User.getInstance().getmUserId()+"/"+faceItem.getFaceId()+"/"+item.getObjId()+"/"+ User.getInstance().getmGender();
        //String uri =ConstantsUtil.URL_BASEPATH+ConstantsUtil.URL_METHOD_CREATE_HAIRSTYLE+"5"+"/"+"36"+"/"+item.getObjId()+"/"+User.getInstance().getmGender();
        return uri;
    }

    @Override
    protected void updateViewSelection(BaseAccessoryItem item) {

    }

    //@
    protected  void clickOnItem(View v){
        DownloadHolder dHolder = (DownloadHolder) v.getTag();
        //updateViewSelection( dHolder,(ImageView)v);
        BaseAccessoryItem item =dHolder.item;

//        Bitmap bitmap = getBitmap(item.getThumbnailAwsKey());
        if (((ImageView)v).getDrawable() == null)
            return;
        //if (item.getThumbnailImage() == null)
//        if (bitmap == null)
//            return;

        latestClickedItem = item;
        if (ConstantsUtil.checkFileKeysExist(item.getTextureAwsKey(), item.getObjAwsKey())) {
            for (HairContent hc : arrayItem) {
                hc.hairItem1.isSelected = false;
                if (hc.hairItem2 != null) {
                    hc.hairItem2.isSelected = false;
                }
            }
            item.isSelected = true;
            this.notifyDataSetChanged();
            clickHandlerOnDownloadedItem(item);
        } else {
            getOpenGLData(dHolder,(ImageView)v);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_hair1:
            case R.id.iv_hair2: {
                clickOnItem(v);
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
        public ImageView ivThumbnail1;
        public ImageView ivThumbnail2;
        public ProgressBar pb1;
        public ProgressBar pb2;
        public CircularProgressWheel cpb1;
        public CircularProgressWheel cpb2;
    }
}
