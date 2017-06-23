package com.cetnaline.findproperty.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.base.BaseActivity;
import com.cetnaline.findproperty.entity.ui.TabEntity;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.cetnaline.findproperty.ui.fragment.BookingFragment;
import com.cetnaline.findproperty.utils.StatusBarCompat;
import com.cetnaline.findproperty.widgets.tablayout.CommonTabLayout;
import com.cetnaline.findproperty.widgets.tablayout.listener.CustomTabEntity;
import com.cetnaline.findproperty.widgets.tablayout.listener.OnTabSelectListener;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by diaoqf on 2016/8/24.
 */
public class BookListActivity extends BaseActivity {

    @BindView(R.id.tab_bar)
    CommonTabLayout tab_bar;

    private ArrayList<CustomTabEntity> mTabEntities;
    private ArrayList<Fragment> fragments;

    private FragmentManager fragmentManager;

    @Override
    protected int getContentViewId() {
        return R.layout.act_booklist;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        fragmentManager = getSupportFragmentManager();
        fragments = new ArrayList<>();
        fragments.add(BookingFragment.getInstance(BookingFragment.ACTIVE));
        fragments.add(BookingFragment.getInstance(BookingFragment.OVERDUE));

        exchangeFragment(0);

        mTabEntities = new ArrayList(){
            {
                add(new TabEntity("已报名",R.drawable.benefit_sel,R.drawable.benefit));
                add(new TabEntity("已过期",R.drawable.pan_sel,R.drawable.pan));
            }
        };

        tab_bar.setTabData(mTabEntities);
        tab_bar.setCurrentTab(0);
        tab_bar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                exchangeFragment(position);
            }

            @Override
            public void onTabReselect(int position) {}
        });
    }

    private void exchangeFragment(int position) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout, fragments.get(position));
        transaction.commit();
    }

    @Override
    protected IPresenter createPresenter() {
        return null;
    }

    @Override
    protected void initToolbar() {
        StatusBarCompat.setStatusBarColor(this, Color.WHITE);
        toolbar.setNavigationOnClickListener((v) -> onBackPressed());
        toolbar.setTitle("我的活动");
    }

    @Override
    protected String getTalkingDataPageName() {
        return "报名活动列表";
    }
}
