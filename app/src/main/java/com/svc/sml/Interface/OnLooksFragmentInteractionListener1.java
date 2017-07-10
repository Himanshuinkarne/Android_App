package com.svc.sml.Interface;


import com.svc.sml.Database.ComboData;

import java.util.ArrayList;

/**
 * Created by himanshu on 9/7/16.
 */

//public interface OnLooksClickedListener {
//
//    void onLooksFragmentInteraction(ComboData combodata, ArrayList<ComboData> comboList, int index);
//}


public interface OnLooksFragmentInteractionListener1 {
    // TODO: Update argument type and name
    void onLooksFragmentInteraction1(int index);
    void onCollectionFragmentInteraction1(ComboData combodata,ArrayList<ComboData> comboList,int index);
}