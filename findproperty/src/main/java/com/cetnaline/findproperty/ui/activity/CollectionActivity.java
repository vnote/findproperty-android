package com.cetnaline.findproperty.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.base.BaseActivity;
import com.cetnaline.findproperty.entity.ui.TabEntity;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.cetnaline.findproperty.ui.fragment.CollectionEstateFragment;
import com.cetnaline.findproperty.ui.fragment.CollectionPostsFragment;
import com.cetnaline.findproperty.ui.fragment.CollectionNewHouseFragment;
import com.cetnaline.findproperty.utils.StatusBarCompat;
import com.cetnaline.findproperty.widgets.tablayout.CommonTabLayout;
import com.cetnaline.findproperty.widgets.tablayout.listener.CustomTabEntity;
import com.cetnaline.findproperty.widgets.tablayout.listener.OnTabSelectListener;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;

/**
 * Created by diaoqf on 2016/8/19.
 */
public class CollectionActivity extends BaseActivity {
    @BindView(R.id.tab_bar)
    CommonTabLayout tab_bar;

    private CollectionPostsFragment fragment1;
    private CollectionNewHouseFragment fragment2;
    private CollectionPostsFragment fragment3;
    private CollectionEstateFragment fragment4;

    @BindString(R.string.map_query_1_1)
    String tab_1;
    @BindString(R.string.map_query_1_3)
    String tab_2;
    @BindString(R.string.map_query_1_2)
    String tab_3;
    @BindString(R.string.map_query_1_4)
    String tab_4;

    private ArrayList<CustomTabEntity> mTabEntities;
    private FragmentManager fragmentManager;

    @Override
    protected int getContentViewId() {
        return R.layout.act_collection;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        fragmentManager = getSupportFragmentManager();
        Bundle bundle = new Bundle();
        fragment1 = new CollectionPostsFragment();
        bundle.putString(CollectionPostsFragment.SHOW_TYPE,"ershoufang");
        fragment1.setArguments(bundle);
        fragment2 = new CollectionNewHouseFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putString(CollectionPostsFragment.SHOW_TYPE,"zufang");
        fragment3 = new CollectionPostsFragment();
        fragment3.setArguments(bundle1);
        fragment4 = new CollectionEstateFragment();

        exchangeFragment(0);

        mTabEntities = new ArrayList(){
            {
                add(new TabEntity(tab_1,R.drawable.benefit_sel,R.drawable.benefit));
//                add(new TabEntity(tab_2,R.drawable.pan_sel,R.drawable.pan));
                add(new TabEntity(tab_3,R.drawable.location_sel,R.drawable.location));
                add(new TabEntity(tab_4,R.drawable.location_sel,R.drawable.location));
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
            public void onTabReselect(int position) {
            }
        });
    }

    private void exchangeFragment(int position) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        switch (position) {
            case 0:
                transaction.replace(R.id.frame_layout, fragment1);
                break;
//            case 1:
//                transaction.replace(R.id.frame_layout, fragment2);
//                break;
            case 1:
                transaction.replace(R.id.frame_layout, fragment3);
                break;
            case 2:
                transaction.replace(R.id.frame_layout, fragment4);
                break;
        }
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
        toolbar.setTitle("我的收藏");
    }

    @Override
    protected String getTalkingDataPageName() {
        return "我的收藏";
    }
}
