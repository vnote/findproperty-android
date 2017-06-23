package com.cetnaline.findproperty.ui.fragment;

import android.support.v4.view.ViewPager;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.base.BaseFragment;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.cetnaline.findproperty.ui.adapter.ViewPagerSimpleAdapter;
import com.cetnaline.findproperty.widgets.tablayout.SegmentTabLayout;
import com.cetnaline.findproperty.widgets.tablayout.listener.OnTabSelectListener;

import java.util.ArrayList;

import butterknife.BindView;

import static com.cetnaline.findproperty.R.attr.position;

/**
 * 发现
 * Created by fanxl2 on 2016/7/21.
 */
public class FoundFragment extends BaseFragment {

    @BindView(R.id.found_tab_bar)
    SegmentTabLayout found_tab_bar;
    @BindView(R.id.view_pager)
    ViewPager view_pager;

    @Override
    protected int getLayoutId() {
        return R.layout.frag_found;
    }

    @Override
    protected void init() {
        found_tab_bar.setTabData(new String[] {"优惠","委托"});
        found_tab_bar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                view_pager.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {

            }
        });

        view_pager.setAdapter(new ViewPagerSimpleAdapter(getActivity(),getChildFragmentManager(),new ArrayList(){
            {
                add(FoundListFragment.class.getName());
                add(FoundShortcutFragment.class.getName());
            }
        }));
        view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                found_tab_bar.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected IPresenter createPresenter() {
        return null;
    }

    public void changeTab(int tab){
        found_tab_bar.setCurrentTab(tab);
        view_pager.setCurrentItem(tab);
    }

}
