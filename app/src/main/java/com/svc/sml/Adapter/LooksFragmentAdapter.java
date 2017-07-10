package com.svc.sml.Adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.svc.sml.Fragments.LooksFragment;
import com.svc.sml.Fragments.LooksFragmentForYou;
import com.svc.sml.Utility.ConstantsUtil;

import java.util.ArrayList;

/**
 * Created by himanshu on 9/6/16.
 */

public class LooksFragmentAdapter extends FragmentPagerAdapter {//FragmentPagerAdapter
    int size = ConstantsUtil.arrayListLooksLabelName.size();
    private ArrayList<String> tabTitles = new ArrayList(ConstantsUtil.arrayListLooksLabelName.subList(0,size-2));

    private Context context;

    public LooksFragmentAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return tabTitles.size();
    }

    @Override
    public Fragment getItem(int position) {
        //return LooksFragment.newInstance(""+position,"");
        String title = tabTitles.get(position);
        if(position == tabTitles.size()-1){
            LooksFragmentForYou collectionFragment = LooksFragmentForYou.newInstance(0);
            return collectionFragment;
        }
        else {
            LooksFragment lf =LooksFragment.newInstance(title.toUpperCase());
            return lf;
        }
//        switch (position) {
//            case 0: // Fragment # 0 - This will show FirstFragment
//                return CollectionFragment.newInstance();
//            case 1: // Fragment # 0 - This will show FirstFragment different title
//                return LooksFragment.newInstance(title);
//            case 2: // Fragment # 1 - This will show SecondFragment
//                return LooksFragment.newInstance(title);
//
//            default:
//                return LooksFragment.newInstance(title);
//        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
//
        return tabTitles.get(position);
    }

//    @Override
//    public Object instantiateItem(ViewGroup container, final int position) {
//        String title = tabTitles.get(position);
//        LooksFragment lf = null ;
//        switch (position) {
//            case 0: // Fragment # 0 - This will show FirstFragment
//                lf = LooksFragment.newInstance(title);
//            case 1: // Fragment # 0 - This will show FirstFragment different title
//                lf = LooksFragment.newInstance(title);
//            case 2: // Fragment # 1 - This will show SecondFragment
//                lf = LooksFragment.newInstance(title);
//
//            default:
//                lf =  LooksFragment.newInstance(title);
//        }
//        return  lf;
//    }


}

