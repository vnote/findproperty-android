package com.cetnaline.findproperty.ui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 *  ViewPager简单适配
 * Created by diaoqf on 2016/11/30.
 */
public class ViewPagerSimpleAdapter extends FragmentPagerAdapter {
    private ArrayList<String> list;
    private ArrayList<Fragment> fragments;
    private boolean isFragment;
    private Context context;

    public ViewPagerSimpleAdapter(Context context, FragmentManager fm, ArrayList<String> list) {
        super(fm);
        this.list = list;
        this.context = context;
    }

    public ViewPagerSimpleAdapter(Context context, FragmentManager fm, ArrayList<Fragment> fragments, boolean isFragment) {
        super(fm);
        this.context = context;
        this.fragments = fragments;
        this.isFragment = isFragment;
    }

    @Override
    public Fragment getItem(int position) {
        if (!isFragment) {
            return Fragment.instantiate(context, list.get(position));
        } else {
            return fragments.get(position);
        }
    }

    @Override
    public int getCount() {
        if (!isFragment) {
            return list.size();
        } else {
            return fragments.size();
        }
    }
}
