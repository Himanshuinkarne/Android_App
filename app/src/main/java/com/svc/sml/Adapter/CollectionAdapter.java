package com.svc.sml.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.svc.sml.Database.ComboData;
import com.svc.sml.Database.ComboDataLooksItem;
import com.svc.sml.R;
import com.svc.sml.Utility.CoverFlow;

import java.util.ArrayList;

/**
 * Created by himanshu on 3/2/16.
 */
public class CollectionAdapter extends BaseAdapter implements SkuGallaryAdapter.OnGalleryAdapterListener {
    private final static String LOGTAG = CollectionAdapter.class.toString();
    protected LayoutInflater mInflater;
    protected ArrayList<TransferObserver> observers = new ArrayList<>();
    private ArrayList<Gallery> galleryList = new ArrayList<>();

    private GridView gv;
    private Context context;
    private ArrayList<ComboDataLooksItem> listComboArrayList;

    public OnGridAdapterListener getListener() {
        return listener;
    }

    public void setListener(OnGridAdapterListener listener) {
        this.listener = listener;
    }

    private OnGridAdapterListener listener;

    @Override
    public void onComboSelected(ComboData item) {

    }

    public interface OnGridAdapterListener {
        // TODO: Update argument type and name
        void onComboSelected(String categoryTitle,ArrayList<ComboData> comboList,ComboData item);
        void onComboSelected(String categoryTitle,ArrayList<ComboData> comboList,int position);
    }

    public CollectionAdapter(Context context, ArrayList<Gallery> galleryList1, GridView v) {
        galleryList = galleryList1;
        this.context = context;
        this.gv = v;
    }

    public CollectionAdapter(Context context, ArrayList<ComboDataLooksItem> listComboArrayList, OnGridAdapterListener fListener) {
        this.listComboArrayList = listComboArrayList;
        this.context = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.listener = fListener;
    }

    @Override
    public int getCount() {
        //return galleryList.size();
        if (listComboArrayList != null)
            return listComboArrayList.size();//TODO
        else return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Gallery g = galleryList.get(position);
        //g.setLayoutParams(new GridView.LayoutParams(gv.getWidth(), gv.getHeight()));
        //return g;
        Holder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_collection_fragment, parent, false);
//            holder = new Holder();
//            holder.gallery = (CoverFlow) convertView.findViewById(R.id.coverflow_looks);
//            holder.titleCategory = (TextView) convertView.findViewById(R.id.tv_title_gallery);
//            holder.tvNoData =(TextView) convertView.findViewById(R.id.tv_looks_nodata);
//            convertView.setTag(holder);

        } else {
            //holder = (Holder) convertView.getTag();
        }
        holder = new Holder();
        holder.gallery = (CoverFlow) convertView.findViewById(R.id.coverflow_looks);
        holder.titleCategory = (TextView) convertView.findViewById(R.id.tv_title_gallery);
        holder.tvNoData =(TextView) convertView.findViewById(R.id.tv_looks_nodata);
        convertView.setTag(holder);
        ComboDataLooksItem cl = listComboArrayList.get(position);
//        String title = cl.getLooksLabelName();
//        if(position == listComboArrayList.size() -1 || position == listComboArrayList.size() -2){
//            title =
//        }
        holder.titleCategory.setText(cl.getLooksLabelName().toUpperCase());
       //
        //if(position)
        setupCoverFlow(holder.gallery,cl.getLooksLabelName(),cl.getComboList(),true);
        /*
        Gallery gallery;
        if (position < galleryList.size()) {
            gallery = galleryList.get(position);
            holder.gallery = gallery;
            holder.titleCategory.setText(cl.getComboStyleCategory());
        } else {
            //gallery = new Gallery(context);
            holder.titleCategory.setText(cl.getComboStyleCategory());
            SkuGallaryAdapter gAdapter = new SkuGallaryAdapter(context, cl.getComboList(), new SkuGallaryAdapter.OnGalleryAdapterListener() {
                @Override
                public void onComboSelected(ComboData item) {
                    listener.onComboSelected(item);
                }
            });
            holder.gallery.setAdapter(gAdapter);
            galleryList.add(position, holder.gallery);
        }
        */
        return convertView;
    }

    private static class Holder {
        //Gallery gallery;
        CoverFlow gallery;
        TextView titleCategory;
        TextView tvNoData;
    }

    /**
     * Setup cover flow.
     *
     * @param mCoverFlow
     *            the m cover flow
     * @param reflect
     *            the reflect
     */
    private void setupCoverFlow(final CoverFlow mCoverFlow,String categoryTitle,final ArrayList<ComboData> comboList, final boolean reflect) {
        BaseAdapter coverImageAdapter;
        mCoverFlow.setAdapter(null);
        if (reflect) {
            coverImageAdapter = new RImageAdapter(new ResourceImageAdapter(context,categoryTitle,comboList));
        } else {
            coverImageAdapter = new ResourceImageAdapter(context,categoryTitle,comboList);
        }
        mCoverFlow.setAdapter(coverImageAdapter);
        int index = comboList.size()-1;
        if(comboList.size()>1)
            index =1;
        mCoverFlow.setSelection(index, true);

//        new android.os.Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                int index = comboList.size()-1;
//                if(comboList.size()>3)
//                    index =3;
//                mCoverFlow.setSelection(index, true);
//            }
//        }, 500);

        //mCoverFlow.setSelection(index, true);
        setupListeners(mCoverFlow);
    }

    /**
     * Sets the up listeners.
     *
     * @param mCoverFlow
     *            the new up listeners
     */
    private void setupListeners(final CoverFlow mCoverFlow) {
        mCoverFlow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView< ? > parent, final View view, final int position, final long id) {
                //textView.setText("Item clicked! : " + id);
                Log.d(LOGTAG,"Item clicked! : " + id +" Position: "+ position);
                SpinnerAdapter adapter = mCoverFlow.getAdapter();
                ArrayList<ComboData> list = null;
                String looksCategory = "";
                if(adapter instanceof ResourceImageAdapter) {
                    list = ((ResourceImageAdapter) mCoverFlow.getAdapter()).getComboList();
                    looksCategory =((ResourceImageAdapter) mCoverFlow.getAdapter()).looksCategoryTitle;
                }
                else if(adapter instanceof RImageAdapter) {
                    list = ((RImageAdapter) mCoverFlow.getAdapter()).getComboList();
                    looksCategory =((RImageAdapter) mCoverFlow.getAdapter()).looksCategoryTitle;
                }
                ComboData comboData =  list.get(position);
                //CollectionAdapter.this.getListener().onComboSelected(looksCategory,comboData);
                CollectionAdapter.this.getListener().onComboSelected(looksCategory,list,position);
                //((CollectionFragment.OnLooksFragmentInteractionListener)context).onLooksFragmentInteraction(comboData,"");
            }
        });
        mCoverFlow.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
                //textView.setText("Item selected! : " + id);
                Log.d(LOGTAG, "Item clicked! : " + id + " Position: " + position);
                SpinnerAdapter adapter = mCoverFlow.getAdapter();
                ArrayList<ComboData> list = null;
                if (adapter instanceof ResourceImageAdapter) {
                    list = ((ResourceImageAdapter) mCoverFlow.getAdapter()).getComboList();
                } else if (adapter instanceof RImageAdapter) {
                    list = ((RImageAdapter) mCoverFlow.getAdapter()).getComboList();
                }
                ComboData comboData = list.get(position);
                //CollectionAdapter.this.getListener().onComboSelected(comboData);
                //((CollectionFragment.OnLooksFragmentInteractionListener)context).onLooksFragmentInteraction(comboData, "");
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {
                //textView.setText("Nothing clicked!");
                Log.d(LOGTAG, "Nothing clicked! : ");
            }
        });
    }
}
