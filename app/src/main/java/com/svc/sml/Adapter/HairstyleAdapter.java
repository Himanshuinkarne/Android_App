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
import com.svc.sml.Helper.DataManager;
import com.svc.sml.InkarneAppContext;
import com.svc.sml.Model.BaseAccessoryItem;
import com.svc.sml.Model.BaseItem;
import com.svc.sml.Model.FaceItem;
import com.svc.sml.R;
import com.svc.sml.Utility.AWSUtil;
import com.svc.sml.Utility.CircularProgressWheel;
import com.svc.sml.Utility.ConstantsUtil;

import java.util.ArrayList;

/**
 * Created by himanshu on 5/11/16.
 */
public class HairstyleAdapter extends BaseAccessoryAdapter {
    private final static String LOGTAG = HairstyleAdapter.class.toString();
    protected ArrayList<BaseAccessoryItem> arrayItem;

    public HairstyleAdapter(Context context, ArrayList<BaseAccessoryItem> values, FaceItem faceItem) {
        super();
        this.context= context;
        accessoryAdapterListener = (OnAccessoryAdapterListener)context;
        arrayItem = values;
        this.faceItem = faceItem;
        dataSource = InkarneAppContext.getDataSource();
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        transferUtility = AWSUtil.getTransferUtility(context);
    }

    public HairstyleAdapter(Context context, ArrayList<BaseAccessoryItem> values, FaceItem faceItem, OnAccessoryAdapterListener onAccAdapterListener) {
       super(context);
        this.context= context;
        accessoryAdapterListener = onAccAdapterListener;
        arrayItem = values;
        this.faceItem = faceItem;
        dataSource = InkarneAppContext.getDataSource();
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        transferUtility = AWSUtil.getTransferUtility(context);
    }

    public HairstyleAdapter(Context context, ArrayList<BaseAccessoryItem> values, OnAccessoryAdapterListener accessoryAdapterListener) {
        super(context);
        this.context = context;
        //this.setAccessoryAdapterListener(accessoryAdapterListener);
        this.accessoryAdapterListener = accessoryAdapterListener;
        arrayItem = values;
        dataSource = InkarneAppContext.getDataSource();
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        transferUtility = AWSUtil.getTransferUtility(context);
    }

    public void add(BaseAccessoryItem path) {
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
    public BaseItem getItem(int position) {
        return arrayItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder=null;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_hairstyle_gridview, parent, false);
        }
        holder = new Holder();
        holder.ivThumbnail1 = (ImageView) convertView.findViewById(R.id.iv_sku);
        holder.ivThumbnail1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickOnItem(v);
            }
        });
        //holder.ivThumbnail1.setOnClickListener(this);
        holder.textView1 = (TextView) convertView.findViewById(R.id.tv_sku);
        holder.vIsDownloaded1 = convertView.findViewById(R.id.v_is_downloaded);
        holder.pb1 = (ProgressBar) convertView.findViewById(R.id.pb_sku);
        holder.cpb1 = (CircularProgressWheel) convertView.findViewById(R.id.cpb_sku);

        //Populate the text
        BaseAccessoryItem item1 = (BaseAccessoryItem) getItem(position);
        //holder.textView1.setText(item1.getObjId());

        DownloadHolder dHolder1 = new DownloadHolder();
        dHolder1.item = item1;
        dHolder1.cpb = holder.cpb1;
        dHolder1.pb = holder.pb1;
        dHolder1.vIsDownloaded = holder.vIsDownloaded1;
        holder.ivThumbnail1.setTag(dHolder1);

        holder.pb1.setTag(getItem(position).getObjId());
        holder.textView1.setTag(getItem(position).getObjId());

        //HolderItem can be replaced by Holder..To be changed.... but there was more than one item in holder in previous functionality
        HolderItem holderItem1= new HolderItem();
        holderItem1.textView = holder.textView1;
        holderItem1.cpb = holder.cpb1;
        holderItem1.pb = holder.pb1;
        holderItem1.ivThumbnail = holder.ivThumbnail1;
        holderItem1.vIsDownloaded = holder.vIsDownloaded1;
        showData(item1, holderItem1);
        //Set the color
        //convertView.setBackgroundColor(getItem(position).getBackgroundColor());
        return convertView;
    }

    //@Override
    protected void reloadListView(){
        this.notifyDataSetChanged();
    }

    //@Override
    protected void clickHandlerOnDownloadedItem(BaseAccessoryItem item){
        super.clickHandlerOnDownloadedItem(item);
    }

    //@Override
    protected String getCreateAccessoryURL(BaseAccessoryItem item){
        //String url = "http://inkarnepub-prod.elasticbeanstalk.com/Service1.svc/CreateHairstyle/5/36/MHS002/m";
        String urlMethod = null;
         if (item.getAccessoryType().equals(ConstantsUtil.EAccessoryType.eAccTypeHair.toString())){
            urlMethod = ConstantsUtil.URL_METHOD_CREATE_HAIRSTYLE;
        }

        String uri = ConstantsUtil.URL_BASEPATH+urlMethod+ User.getInstance().getmUserId()+"/"+faceItem.getFaceId()+"/"+item.getObjId()+"/"+ User.getInstance().getmGender();
        Log.d(LOGTAG, uri);
        return uri;
    }

    //@Override
    protected  void clickOnItem(View v){
        DownloadHolder dHolder = (DownloadHolder) v.getTag();
        BaseAccessoryItem item =dHolder.item;
        if (((ImageView)v).getDrawable() == null)
            return;
        //if (item.getThumbnailImage() == null)
//        if (bitmap == null)
//            return;

        latestClickedItem = item;
        if (accessoryAdapterListener != null) {
            accessoryAdapterListener.onAccessoryClicked(item);
        }
        updateViewSelection(item);
        if (ConstantsUtil.checkFileKeysExist(item.getTextureAwsKey(), item.getObjAwsKey())) {
            //updateViewSelection(item);
            Log.e(LOGTAG, " clickHandlerOnDownloadedItem  already Downloaded:" + item.getObjAwsKey());
            clickHandlerOnDownloadedItem(item);
        } else {
            if(item.countTobeDownloaded ==-1)
                getOpenGLData(dHolder,(ImageView)v);
        }
    }

    //@Override
    protected void saveData(BaseAccessoryItem item){
        DataManager.getInstance().getDataSource().create(item);
    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.iv_sku:
//                clickOnItem(v);
//                break;
//            default:
//                break;
//        }
//    }

    /**
     * View holder for the views we need access to
     */
    private static class Holder {
        public TextView textView1;
        public ImageView ivThumbnail1;
        public ProgressBar pb1;
        public CircularProgressWheel cpb1;
        public View vIsDownloaded1;
    }

    protected void updateViewSelection(BaseAccessoryItem item){
        if(item != null && arrayItem != null) {
            for (BaseAccessoryItem hc : arrayItem) {
                hc.isSelected = false;
            }
            item.isSelected = true;
            this.notifyDataSetChanged();
        }
    }
}
