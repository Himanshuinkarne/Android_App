package com.svc.sml.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.svc.sml.Adapter.CartAdapter;
import com.svc.sml.Database.ComboData;
import com.svc.sml.Database.InkarneDataSource;
import com.svc.sml.Database.LAData;
import com.svc.sml.InkarneAppContext;
import com.svc.sml.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CartActivity extends Activity implements CartAdapter.OnCartAdapterInteractionListener, View.OnClickListener {
    private ListView listView;
    private InkarneDataSource datasource;
    private List<LAData> laDataList = new ArrayList<LAData>();
    private CartAdapter cartAapater;
    private ProgressBar pbCart;
    private Button btnBack;
    private ComboData comboData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        datasource = InkarneDataSource.getInstance(InkarneAppContext.getAppContext());
        datasource.open();
        laDataList = datasource.getLADataDetails();

        btnBack = (Button) findViewById(R.id.btn_cart_back);
        btnBack.setOnClickListener(this);
        pbCart = (ProgressBar) findViewById(R.id.pb_cart);

        listView = (ListView) findViewById(R.id.lv_cart);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("debug", "listview item clicked :" + position);
            }
        });

        cartAapater = new CartAdapter(this, (ArrayList<LAData>) laDataList);
        cartAapater.setListener(this);

        Collections.sort(laDataList, LAData.Comparators.orderDSC);
        listView.setAdapter(cartAapater);
        cartAapater.updateLaDataList(laDataList);
        pbCart.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onCartRemoved(LAData laData) {

    }

    @Override
    public void onBuyAdded(LAData laData) {

    }

    @Override
    public void onView3d(LAData laData) {

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        // Unregister since the activity is paused.
        //LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cart_back: {
                finish();
            }
            break;
            default:
                break;
        }
    }
}
