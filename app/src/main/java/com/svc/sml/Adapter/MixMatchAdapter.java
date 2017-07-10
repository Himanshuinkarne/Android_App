package com.svc.sml.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.svc.sml.Utility.Connectivity;
import com.svc.sml.Utility.ConstantsUtil;

import java.util.ArrayList;

/**
 * Created by himanshu on 2/9/16.
 */
public class MixMatchAdapter extends BaseAccessoryAdapter implements View.OnClickListener {
    private final static String LOGTAG = HairAdapter.class.toString();
    private boolean acceptClick=true;
    //protected ArrayList<BaseAccessoryItem> arrayItem;
    //public ArrayList<Holder> list = new ArrayList<>();

//    protected FaceItem faceItem;
//    private OnAccessoryAdapterListener accessoryAdapterListener;


//    public OnAccessoryAdapterListener getAccAdapterListener() {
//        return accessoryAdapterListener;
//    }
//
//    public void setAccAdapterListener(OnAccessoryAdapterListener accessoryAdapterListener) {
//        this.accessoryAdapterListener = accessoryAdapterListener;
//    }

//    public interface OnAccMixMatchAdapterListener {
//        // TODO: Update argument type and name
//        void onAccessorySelected(BaseAccessoryItem item);
//        void onAccessoryClicked(BaseAccessoryItem item);
//    }

    public MixMatchAdapter(Context context, ArrayList<BaseAccessoryItem> values, FaceItem faceItem) {
        super(context);
        this.context = context;
        arrayItem = values;
        this.faceItem = faceItem;
        init();
    }

    public MixMatchAdapter(Context context, ArrayList<BaseAccessoryItem> values, FaceItem faceItem, OnAccessoryAdapterListener onAccAdapterListener) {
        super(context);
        this.context = context;
        this.accessoryAdapterListener = onAccAdapterListener;
        arrayItem = values;
        this.faceItem = faceItem;
        this.latestClickedItem = null;
        init();
    }

    public MixMatchAdapter(Context context, ArrayList<BaseAccessoryItem> values, OnAccessoryAdapterListener accessoryAdapterListener) {
        //super(context, R.layout.h_list_item_hair, values);
        super(context);
        this.context = context;
        this.accessoryAdapterListener = accessoryAdapterListener;
        arrayItem = values;
        latestClickedItem = null;
        init();
    }

    //@Override
    protected String getCreateAccessoryURL(BaseAccessoryItem item) {
        //String url = "http://inkarnepub-prod.elasticbeanstalk.com/Service1.svc/CreateHairstyle/5/36/MHS002/m";
        String urlMethod = null;
        String uri = null;
        if (item.getAccessoryType().equals(ConstantsUtil.EAccessoryType.eAccTypeLegs.toString())) {
            User user = User.getInstance();//TODO zip
//            uri = ConstantsUtil.URL_BASEPATH_CREATE + ConstantsUtil.URL_METHOD_CREATE_LEGS
//                    + user + "/"
//                    + user.getDefaultFaceItem().getFaceId() + "/"
//                    + user.getDefaultFaceItem().getPbId();
            uri = ConstantsUtil.URL_BASEPATH_CREATE_V2 + ConstantsUtil.URL_METHOD_CREATE_LEGS
                    + user + "/"
                    + user.getDefaultFaceItem().getFaceId() + "/"
                    + user.getDefaultFaceItem().getPbId();
            Log.d(LOGTAG, uri);
            return uri;
        }
        if (item.getAccessoryType().equals(ConstantsUtil.EAccessoryType.eAccTypeShoes.toString())) {
            uri = ConstantsUtil.URL_BASEPATH_V2 + ConstantsUtil.URL_METHOD_CREATE_SHOES + User.getInstance().getmUserId() + "/" + User.getInstance().getPBId() + "/" + item.getObjId() ;//+ "/" + User.getInstance().getmGender();
            Log.d(LOGTAG, uri);
            return uri;
        }
        if (item.getAccessoryType().equals(ConstantsUtil.EAccessoryType.eAccTypeEarrings.toString())) {
            urlMethod = ConstantsUtil.URL_METHOD_CREATE_EARRINGS;
        } else if (item.getAccessoryType().equals(ConstantsUtil.EAccessoryType.eAccTypeSunglasses.toString())) {
            urlMethod = ConstantsUtil.URL_METHOD_CREATE_SUNGLASSES;
        } else if (item.getAccessoryType().equals(ConstantsUtil.EAccessoryType.eAccTypeSpecs.toString())) {
            urlMethod = ConstantsUtil.URL_METHOD_CREATE_SPECS;
        } else if (item.getAccessoryType().equals(ConstantsUtil.EAccessoryType.eAccTypeHair.toString())) {
            urlMethod = ConstantsUtil.URL_METHOD_CREATE_HAIRSTYLE;
        }
        String faceId = null;//TODO
        if(faceItem == null){
            if(User.getInstance().getDefaultFaceItem() != null){
                faceId = User.getInstance().getDefaultFaceItem().getFaceId();
            }else{
                faceId = User.getInstance().getDefaultFaceId();
            }
        }
        else {
            faceId = faceItem.getFaceId();
        }
        uri = ConstantsUtil.URL_BASEPATH_V2 + urlMethod + User.getInstance().getmUserId() + "/" + faceId + "/" + item.getObjId() + "/" + User.getInstance().getmGender();
        Log.d(LOGTAG, uri);
        return uri;
    }



    private void init() {
        dataSource = InkarneAppContext.getDataSource();
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        transferUtility = AWSUtil.getTransferUtility(context);
        // imageFetcher = new ImageFetcher(context);
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
        if (arrayItem != null)
            return arrayItem.size();
        else
            return 0;
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
        Holder holder = null;

        if (convertView == null) {
            // Inflate the view since it does not exist
            convertView = mInflater.inflate(R.layout.list_item_mixmatch, parent, false);

            // Create and save off the holder in the tag so we get quick access to inner fields
            // This must be done for performance reasons

        }
        //convertView = mInflater.inflate(R.layout.list_item_mixmatch, parent, false);
        holder = new Holder();
        holder.ivThumbnail1 = (ImageView) convertView.findViewById(R.id.iv_sku);
        holder.ivThumbnail1.setOnClickListener(this);
        holder.textView1 = (TextView) convertView.findViewById(R.id.tv_sku);
        holder.vIsDownloaded1 = convertView.findViewById(R.id.v_is_downloaded);
        holder.pb1 = (ProgressBar) convertView.findViewById(R.id.pb_sku);
        holder.cpb1 = (CircularProgressWheel) convertView.findViewById(R.id.cpb_sku);
        holder.ivThumbnail1.setImageBitmap(null);
        //convertView.setTag(holder);
        //list.add(position,holder);
//        else {
//            holder = (Holder) convertView.getTag();
//        }

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
        HolderItem holderItem1 = new HolderItem();
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
    protected void reloadListView() {
        this.notifyDataSetChanged();
    }

    //@Override
    protected void clickHandlerOnDownloadedItem(BaseAccessoryItem item) {
        super.clickHandlerOnDownloadedItem(item);
    }


    //@Override
    protected void clickOnItem(View v) {

        DownloadHolder dHolder = (DownloadHolder) v.getTag();
        if (dHolder == null)//TODO
            return;
        BaseAccessoryItem item = dHolder.item;
        if (((ImageView) v).getDrawable() == null)
            return;

        if(latestClickedItem!=null && (latestClickedItem.countTobeDownloaded == 0 || isDownloaded(latestClickedItem) || latestClickedItem.isDownloadFailed))
        {
            acceptClick=true;
        }

        if (acceptClick) {
            acceptClick = false;

            if(latestClickedItem != null ){
            if( (!Connectivity.isConnected(context) || latestClickedItem.isDownloadFailed == false) &&  latestClickedItem.getObjId().equals(item.getObjId()))
                return;
            }

            latestClickedItem = item;
            latestClickedItem.isDownloadFailed = false;
            if (accessoryAdapterListener != null) {
                accessoryAdapterListener.onAccessoryClicked(item);
            }
            if(!Connectivity.isConnected(context)){
                item.isDownloadFailed = true;
            }

            if (item.countTobeDownloaded == 0 || isDownloaded(item)) {
                item.countTobeDownloaded = 0;
                updateViewSelection(item);
                acceptClick=true;
                Log.e(LOGTAG, " clickHandlerOnDownloadedItem already Downloaded:" + item.getObjAwsKey());
                clickHandlerOnDownloadedItem(item);
            } else if (item.countTobeDownloaded == -1) {
                v.setTag(null);
                if (!Connectivity.isConnected(context)) {
                    Toast.makeText(context, context.getString(R.string.message_network_failure), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Connectivity.isConnectedWifi(context) || (Connectivity.isConnected(context) && !InkarneAppContext.getSettingIsWifiOnlyDownload())) {
                    getOpenGLData(dHolder, (ImageView) v);
                }
            }
        }
    }

    protected boolean isDownloaded(BaseAccessoryItem item) {
        if (item.getAccessoryType().equals(ConstantsUtil.EAccessoryType.eAccTypeShoes.toString())) {
            if (item.dependentItem == null) {
                BaseAccessoryItem legItem = dataSource.getAccessory(ConstantsUtil.EAccessoryType.eAccTypeLegs.toString(), item.getObjId2());
                item.dependentItem = legItem;
            }
        }
        return super.isDownloaded(item);
    }

    //@Override
    protected void saveData(BaseAccessoryItem item) {
        DataManager.getInstance().getDataSource().create(item);
    }

//    protected void updateViewAccessoryToServer(BaseAccessoryItem item) {
//        final String uri = ConstantsUtil.URL_BASEPATH + ConstantsUtil.URL_METHOD_UPDATE_MIXMATCH_SLECTION + User.getInstance().getmUserId() + "/" + item.getObjId();
//        DataManager.getInstance().updateMethodToServer(uri, ConstantsUtil.EUpdateType.eUpdateTypeViewAccessory.toString(), new DataManager.OnResponseHandlerInterface() {
//            @Override
//            public void onResponse(Object obj) {
//
//            }
//
//            @Override
//            public void onResponseError(String errorMessage, int errorCode) {
//
//            }
//        });
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_sku:
                clickOnItem(v);
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
        public ImageView ivThumbnail1;
        public ProgressBar pb1;
        public CircularProgressWheel cpb1;
        public View vIsDownloaded1;
    }

    protected void updateViewSelection(BaseAccessoryItem item) {
        if (item != null && arrayItem != null) {
            for (BaseAccessoryItem hc : arrayItem) {
                hc.isSelected = false;
            }
            item.isSelected = true;
            this.notifyDataSetChanged();
        }
    }
}
