package com.svc.sml.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;

import com.svc.sml.Database.ComboData;
import com.svc.sml.Helper.ImageFetcher;
import com.svc.sml.Utility.AWSUtil;
import com.svc.sml.Utility.AbstractCoverFlowImageAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is an adapter that provides images from a fixed set of resource
 * ids. Bitmaps and ImageViews are kept as weak references so that they can be
 * cleared by garbage collection when not needed.
 * 
 */
public class ResourceImageAdapter extends AbstractCoverFlowImageAdapter {

    /** The Constant TAG. */
    private static final String LOGTAG = ResourceImageAdapter.class.getSimpleName();

    /** The Constant DEFAULT_LIST_SIZE. */
    private static final int DEFAULT_LIST_SIZE = 20;

    /** The Constant IMAGE_RESOURCE_IDS. */
    private static final List<Integer> IMAGE_RESOURCE_IDS = new ArrayList<Integer>(DEFAULT_LIST_SIZE);

    /** The Constant DEFAULT_RESOURCE_LIST. */
   // private static final int[] DEFAULT_RESOURCE_LIST = { R.drawable.image01, R.drawable.image02, R.drawable.image03,
   //         R.drawable.image04, R.drawable.image05,R.drawable.image06,R.drawable.image07,R.drawable.image08,R.drawable.image09,R.drawable.image10,R.drawable.image11 };

   // private static final int[] DEFAULT_RESOURCE_LIST = {R.drawable.image06,R.drawable.image07,R.drawable.image08,R.drawable.image09,R.drawable.image10,R.drawable.image11 };

    /** The bitmap map. */
    private final Map<Integer, WeakReference<Bitmap>> bitmapMap = new HashMap<Integer, WeakReference<Bitmap>>();

    //private final Context context;

    /**
     * Creates the adapter with default set of resource images.
     * 
     * @param context
     *            context
     */
    public ResourceImageAdapter(final Context context) {
        super();
        this.context = context;
        //setResources(DEFAULT_RESOURCE_LIST);
    }

//    public ResourceImageAdapter(final Context context, ArrayList<ComboData> comboList) {
//        super();
//        this.comboList = comboList;
//        this.context = context;
//        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        transferUtility = AWSUtil.getTransferUtility(context);
//        //this.listener = listener;
//    }
    public ResourceImageAdapter(final Context context, String looksCategoryTitle ,ArrayList<ComboData> comboList) {
        super(context,looksCategoryTitle,comboList);
        this.comboList = comboList;
        this.looksCategoryTitle = looksCategoryTitle;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        transferUtility = AWSUtil.getTransferUtility(context);
        imageFetcher = new ImageFetcher(context);
        //this.listener = listener;
    }

    /**
     * Replaces resources with those specified.
     * 
     * @param resourceIds
     *            array of ids of resources.
     */

    public final synchronized void setResources(final int[] resourceIds) {
        IMAGE_RESOURCE_IDS.clear();
        for (final int resourceId : resourceIds) {
            IMAGE_RESOURCE_IDS.add(resourceId);
        }
        notifyDataSetChanged();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public synchronized int getCount() {
        if (comboList != null) {
            //Log.d(LOGTAG,"size : "+comboList.size());
            return comboList.size();
        }
        return 0;
        //return IMAGE_RESOURCE_IDS.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.svc.inkarne.Utility.AbstractCoverFlowImageAdapter#createBitmap(int)
     */
    @Override
    protected Bitmap createBitmap(final int position) {

        Log.v(LOGTAG, "creating item " + position);
        final Bitmap bitmap = ((BitmapDrawable) context.getResources().getDrawable(IMAGE_RESOURCE_IDS.get(position)))
                .getBitmap();
        bitmapMap.put(position, new WeakReference<Bitmap>(bitmap));
        return bitmap;
    }

    @Override
    protected Bitmap createBitmap(Bitmap bitmap) {
       return bitmap;
    }

}