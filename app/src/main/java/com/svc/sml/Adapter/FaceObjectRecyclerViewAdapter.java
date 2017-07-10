package com.svc.sml.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.svc.sml.Fragments.HairHListFragment;
import com.svc.sml.Fragments.BaseAccessoryHListFragment.OnListFragmentInteractionListener;
import com.svc.sml.Model.HairItem;
import com.svc.sml.R;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link HairItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class FaceObjectRecyclerViewAdapter extends RecyclerView.Adapter<FaceObjectRecyclerViewAdapter.ViewHolder> {

    private final List<HairItem> mValues;
    private final OnListFragmentInteractionListener mListener;

    public FaceObjectRecyclerViewAdapter(List<HairItem> items, HairHListFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.h_list_item_hair, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getObjId());
        holder.mContentView.setText(mValues.get(position).content);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteractionHairUpdate(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public HairItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.tv_hair1);
            mContentView = (TextView) view.findViewById(R.id.tv_hair2);
        }

        @Override
        public String toString() {

            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
