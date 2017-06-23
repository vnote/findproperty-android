package com.cetnaline.findproperty.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.Text;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.base.BaseActivity;
import com.cetnaline.findproperty.entity.ui.TabEntity;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.cetnaline.findproperty.ui.adapter.ViewPagerSimpleAdapter;
import com.cetnaline.findproperty.ui.fragment.ComplainFragment;
import com.cetnaline.findproperty.ui.fragment.HouseTipOffsFragment;
import com.cetnaline.findproperty.ui.fragment.SuggestFragment;
import com.cetnaline.findproperty.utils.StatusBarCompat;
import com.cetnaline.findproperty.widgets.tablayout.CommonTabLayout;
import com.cetnaline.findproperty.widgets.tablayout.listener.CustomTabEntity;
import com.cetnaline.findproperty.widgets.tablayout.listener.OnTabSelectListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by diaoqf on 2016/8/24.
 */
public class CommentActivity extends BaseActivity {
    public static final String SHOW_TYPE = "show_type";
    public static final String NORMAL_TYPE = "normal_type";
    public static final String CHAT_TYPE = "chat_type";

    @BindView(R.id.tab_bar)
    CommonTabLayout tab_bar;

    @BindView(R.id.success_layout)
    RelativeLayout success_layout;
    @BindView(R.id.close)
    TextView close;

    @BindView(R.id.alert)
    TextView alert;
    @BindView(R.id.msg)
    TextView msg;

    @BindView(R.id.fragment_viewpager)
    ViewPager fragment_viewpager;

    private ArrayList<CustomTabEntity> mTabEntities;
    private ArrayList<String> fragments;

    private String type;

    public String getType() {
        return type;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.act_comment;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        fragments = new ArrayList<>();
        type = getIntent().getStringExtra(SHOW_TYPE);
        if (type == null || type.equals(NORMAL_TYPE)) {
            toolbar.setTitle("建议反馈");
//            fragments.add(ComplainFragment.class.getName());
            fragments.add(SuggestFragment.class.getName());
//
//            mTabEntities = new ArrayList(){
//                {
//                    add(new TabEntity("服务投诉",R.drawable.benefit_sel,R.drawable.benefit));
//                    add(new TabEntity("建议反馈",R.drawable.pan_sel,R.drawable.pan));
//                }
//            };
            tab_bar.setVisibility(View.GONE);
        } else if(type.equals(CHAT_TYPE)) {
            toolbar.setTitle("填写举报投诉");
            fragments.add(HouseTipOffsFragment.class.getName());
            fragments.add(ComplainFragment.class.getName());

            mTabEntities = new ArrayList(){
                {
                    add(new TabEntity("举报房源",R.drawable.benefit_sel,R.drawable.benefit));
                    add(new TabEntity("投诉顾问",R.drawable.pan_sel,R.drawable.pan));
                }
            };

            tab_bar.setTabData(mTabEntities);
            tab_bar.setCurrentTab(0);
            tab_bar.setOnTabSelectListener(new OnTabSelectListener() {
                @Override
                public void onTabSelect(int position) {
                    fragment_viewpager.setCurrentItem(position);
                }

                @Override
                public void onTabReselect(int position) {}
            });
        }

        fragment_viewpager.setAdapter(new ViewPagerSimpleAdapter(this,getSupportFragmentManager(),fragments));
        fragment_viewpager.setCurrentItem(0);
        fragment_viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tab_bar.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        close.setOnClickListener(v->{
            onBackPressed();
        });
    }

    @Override
    protected IPresenter createPresenter() {
        return null;
    }

    @Override
    protected void initToolbar() {
        StatusBarCompat.setStatusBarColor(this, Color.WHITE);
        toolbar.setNavigationOnClickListener((v)->{
            onBackPressed();
        });
    }

    public void showSuccess(String msg1, String msg2){
        if (msg1 != null && msg2 != null) {
            alert.setText(msg1);
            msg.setText(msg2);
        }
        success_layout.setVisibility(View.VISIBLE);
    }

    @Override
    protected String getTalkingDataPageName() {
        return "建议反馈";
    }
}
