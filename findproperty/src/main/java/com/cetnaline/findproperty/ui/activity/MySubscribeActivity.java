package com.cetnaline.findproperty.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.base.BaseActivity;
import com.cetnaline.findproperty.entity.event.NormalEvent;
import com.cetnaline.findproperty.entity.ui.TabEntity;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.cetnaline.findproperty.ui.adapter.ViewPagerSimpleAdapter;
import com.cetnaline.findproperty.ui.fragment.SubscribePostFragment;
import com.cetnaline.findproperty.utils.StatusBarCompat;
import com.cetnaline.findproperty.widgets.tablayout.CommonTabLayout;
import com.cetnaline.findproperty.widgets.tablayout.listener.CustomTabEntity;
import com.cetnaline.findproperty.widgets.tablayout.listener.OnTabSelectListener;

import java.util.ArrayList;
import butterknife.BindView;
import io.rong.eventbus.EventBus;

/**
 * Created by diaoqf on 2016/8/21.
 */
public class MySubscribeActivity extends BaseActivity {

    public static final int REQUEST_HOUSE_LIST_CODE = 1009;
    public static final String TAB = "subscribeType";

    private static final int[] pages = new int[]{R.layout.subscribe_pager_item_1,
            R.layout.subscribe_pager_item_2, R.layout.subscribe_pager_item_3};

    @BindView(R.id.tab_bar)
    CommonTabLayout tab_bar;
    @BindView(R.id.view_pager)
    ViewPager view_pager;

    private ArrayList<CustomTabEntity> mTabEntities;
    private int cur_tab=0;

    private ArrayList<Fragment> fragments;

    @Override
    protected int getContentViewId() {
        return R.layout.act_my_subscribe;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        cur_tab = getIntent().getIntExtra(TAB,0);
        fragments = new ArrayList<>();
        fragments.add(SubscribePostFragment.getInstance(SubscribePostFragment.POST));
        fragments.add(SubscribePostFragment.getInstance(SubscribePostFragment.RENT));

        view_pager.setAdapter(new ViewPagerSimpleAdapter(this,getSupportFragmentManager(),fragments,true));
        view_pager.setCurrentItem(cur_tab);
        view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                tab_bar.setCurrentTab(position);
                cur_tab = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mTabEntities = new ArrayList(){
            {
                add(new TabEntity("二手房",R.drawable.benefit_sel,R.drawable.benefit));
                add(new TabEntity("租房",R.drawable.pan_sel,R.drawable.pan));
//                add(new TabEntity(tab_3,R.drawable.location_sel,R.drawable.location));
            }
        };

        tab_bar.setTabData(mTabEntities);
        tab_bar.setCurrentTab(cur_tab);
        view_pager.setCurrentItem(cur_tab);
        tab_bar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                view_pager.setCurrentItem(position);
                cur_tab = position;
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
    }

    /**
     * 从列表页返回刷新数据   这部分刷新移植到fragment中执行
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_HOUSE_LIST_CODE) {
            NormalEvent event = new NormalEvent(NormalEvent.INTENT_CHANGE);
//            switch (cur_tab) {
//                case 0:
//                    event.data = SubscribePostFragment.POST;
//                    break;
//                case 1:
//                    event.data = SubscribePostFragment.RENT;
//                    break;
//                case 2:
//                    event.data = SubscribePostFragment.XINFANG;
//                    break;
//            }
            EventBus.getDefault().post(event);
        }
    }

    @Override
    protected IPresenter createPresenter() {
        return null;
    }

    @Override
    protected void initToolbar() {
        StatusBarCompat.setStatusBarColor(this, Color.WHITE);
        toolbar.setNavigationOnClickListener((v) -> onBackPressed());
        toolbar.setTitle("我的意向");
    }

    @Override
    protected String getTalkingDataPageName() {
        return "我的订阅";
    }
}
