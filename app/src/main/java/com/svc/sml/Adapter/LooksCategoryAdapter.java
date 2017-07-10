package com.svc.sml.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by himanshu on 8/29/16.
 */
public class LooksCategoryAdapter extends ArrayAdapter {
    ArrayList<String> listObject;

    int resource ;
    public LooksCategoryAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
        listObject = (ArrayList<String>) objects;
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(this.resource, parent, false);
        }
        //TextView tvName = (TextView) convertView.findViewById(R.id.tv_looks_category);
        //tvName.setText(listObject.get(position));
        return convertView;
    }
}
