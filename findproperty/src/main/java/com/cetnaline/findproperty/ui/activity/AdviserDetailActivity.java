package com.cetnaline.findproperty.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.bean.HouseBo;
import com.cetnaline.findproperty.base.BaseActivity;
import com.cetnaline.findproperty.db.entity.Staff;
import com.cetnaline.findproperty.entity.bean.StaffListBean;
import com.cetnaline.findproperty.entity.event.ShareEvent;
import com.cetnaline.findproperty.entity.ui.TabEntity;
import com.cetnaline.findproperty.inter.RefreshListener;
import com.cetnaline.findproperty.presenter.impl.AdviserDetailPresenter;
import com.cetnaline.findproperty.presenter.ui.AdviserDetailContract;
import com.cetnaline.findproperty.ui.fragment.HouseListFragment;
import com.cetnaline.findproperty.utils.RxBus;
import com.cetnaline.findproperty.utils.StatusBarCompat;
import com.cetnaline.findproperty.utils.glide.GlideLoad;
import com.cetnaline.findproperty.widgets.CircleImageView;
import com.cetnaline.findproperty.widgets.sharedialog.ShareDialog;
import com.cetnaline.findproperty.widgets.tablayout.CommonTabLayout;
import com.cetnaline.findproperty.widgets.tablayout.listener.CustomTabEntity;
import com.cetnaline.findproperty.widgets.tablayout.listener.OnTabSelectListener;
import com.orhanobut.logger.Logger;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by diaoqf on 2016/8/3.
 */
public class AdviserDetailActivity extends BaseActivity<AdviserDetailPresenter> implements AdviserDetailContract.View {
    public static final String ADVISER = "adviser";

    @BindView(R.id.adviser_img)
    CircleImageView adviser_img;
    @BindView(R.id.adviser_dep)
    TextView adviser_dep;
    @BindView(R.id.adviser_msg)
    TextView adviser_msg;
    @BindView(R.id.adviser_phone)
    ImageView adviser_phone;
    @BindView(R.id.adviser_talk)
    ImageView adviser_talk;
    @BindView(R.id.tab_bar)
    CommonTabLayout tab_bar;
    @BindView(R.id.source_page_list)
    ViewPager source_page_list;
    @BindString(R.string.adviserdetail_say)
    String sayString;
    @BindString(R.string.adviserdetail_tab_1)
    String string_tab_1;
    @BindString(R.string.adviserdetail_tab_2)
    String string_tab_2;
    @BindString(R.string.adviserdetail_label_1)
    String label_1;
    @BindString(R.string.adviserdetail_label_2)
    String label_2;

    ShareDialog shareDialog;
    private static final String BASE_URL = "http://sh.centanet.com/m/jingjiren/m-";
    private StaffListBean adviser;
    private ArrayList<CustomTabEntity> mTabEntities;
    private ArrayList<Fragment> mFragments;

    private int current_page_1 = 1;
    private int current_page_2 = 1;

    private boolean is_all_load1 = false;
    private boolean is_all_load2 = false;

    private RefreshListener refreshListener1 = new RefreshListener() {
        @Override
        public void down() {
            current_page_1 = 1;
            is_all_load1 = false;
            mPresenter.loadSale(current_page_1,adviser);
        }
        @Override
        public void up(int count) {
            if (is_all_load1){
                return;
            }
            mPresenter.loadSale(current_page_1,adviser);
        }

        @Override
        public void loadDataAgain() {

        }
    };

    private RefreshListener refreshListener2 = new RefreshListener() {
        @Override
        public void down() {
            current_page_2 = 1;
            is_all_load2 = false;
            mPresenter.loadRent(current_page_2,adviser);
        }
        @Override
        public void up(int count) {
            if (is_all_load2){
                return;
            }
            mPresenter.loadRent(current_page_2,adviser);
        }

        @Override
        public void loadDataAgain() {

        }
    };

    private IUiListener iUiListener = new IUiListener() {
        @Override
        public void onComplete(Object o) {
            RxBus.getDefault().send(new ShareEvent(ShareEvent.EVENT_TYPE_SUCCESS));
            toast("分享成功");
        }

        @Override
        public void onError(UiError uiError) {
            RxBus.getDefault().send(new ShareEvent(ShareEvent.EVENT_TYPE_FAIL));
        }

        @Override
        public void onCancel() {
            RxBus.getDefault().send(new ShareEvent(ShareEvent.EVENT_TYPE_CANCLE));
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.adviser_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.adviser_share:

                Map<String, String> shareParam = new HashMap<>();
                StaffListBean adviser = getAdviser();

                shareParam.put("title", adviser.CnName+"的店铺");
                String store = adviser.StoreName == null ? "":adviser.StoreName;
                shareParam.put("summary","上海中原\r\n" + store);
                shareParam.put("imageUrl", adviser.StaffImg);
                shareParam.put("url", BASE_URL + adviser.StaffNo+"/");
                shareDialog = new ShareDialog(this, shareParam);
                shareDialog.show();
                break;
        }
        return true;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.act_adviserdetail;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        adviser = (StaffListBean) getIntent().getSerializableExtra(ADVISER);
        toolbar.setTitle(adviser.CnName + sayString);
        mTabEntities = new ArrayList(){
            {
                add(new TabEntity(string_tab_1,R.drawable.ic_360_icon,R.drawable.ic_360_icon));
                add(new TabEntity(string_tab_2,R.drawable.ic_360_icon,R.drawable.ic_360_icon));
            }
        };

        //加载头像
        loadHeadImage(adviser.StaffImg);
        setDepartment(adviser.StoreName);
        mPresenter.getFragments(refreshListener1,refreshListener2);
        //获取服务小区
        mPresenter.getDepartments(adviser.StaffID,adviser.StaffNo);
        //分享监听
        mPresenter.registerShareEvent(ShareEvent.class);
        initTabBar();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Constants.REQUEST_QQ_SHARE) {
//            if(resultCode == Tencent.REQUEST_LOGIN) {}
            Tencent.onActivityResultData(requestCode, resultCode, data, iUiListener);
            Tencent.handleResultData(data, iUiListener);
        }

    }

    /**
     * 初始化tabs
     */
    private void initTabBar() {
        StatusBarCompat.setStatusBarColor(this, Color.WHITE);
        tab_bar.setTabData(mTabEntities);
        tab_bar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                source_page_list.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {
            }
        });

        source_page_list.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        source_page_list.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
        source_page_list.setCurrentItem(0);
    }

    @OnClick({R.id.adviser_phone,R.id.adviser_talk})
    protected void clickFunction(View v){
        switch (v.getId()) {
            case R.id.adviser_phone:
                mPresenter.showCallDialog(AdviserDetailActivity.this,String.format(Locale.CHINA,label_2,adviser.CnName),adviser);
                break;
            case R.id.adviser_talk:
                mPresenter.openConversation(AdviserDetailActivity.this, adviser);
                break;
        }
    }

    @Override
    protected AdviserDetailPresenter createPresenter() {
        return new AdviserDetailPresenter();
    }

    @Override
    protected void initToolbar() {
        toolbar.setNavigationOnClickListener((v) -> onBackPressed());
        toolbar.setTitle("");
        toolbar.setBackgroundColor(getResources().getColor(R.color.grayBigLine));
        center_title.setVisibility(View.VISIBLE);
    }

    public StaffListBean getAdviser() {
        return adviser;
    }

    @Override
    public void setFragments(List<Fragment> fragments) {
        mFragments = (ArrayList<Fragment>) fragments;
    }

    @Override
    public void setStaffMsg(String msg) {
        adviser_msg.setText(label_1+msg);
    }

    @Override
    public void setStaff(Staff staff) {
        adviser.Staff400Tel = staff.getStaff400Tel();
        adviser.MobileBy400 = staff.getStaff400Tel();
        adviser.StoreName = staff.getDepartmentName();
        adviser.CnName = staff.getName();
        adviser.StaffNo = staff.getUId();
        adviser.StaffImg = staff.getImageUrl();
    }

    @Override
    public void setDepartment(String name) {
        adviser_dep.setText(name);
    }

    @Override
    public void loadHeadImage(String uri) {
        GlideLoad.initRound(this)
                .load(uri)
                .placeholder(R.drawable.rc_icon_admin)
                .error(R.drawable.rc_icon_admin)
                .fitCenter()
                .into(adviser_img);
    }

    @Override
    public void setSaleAllLoad(boolean loaded) {
        is_all_load1 = loaded;
    }

    @Override
    public void setRentAllLoad(boolean loaded) {
        is_all_load2 = loaded;
    }

    @Override
    public void showShareDialog(boolean show) {
        if (show) {
            shareDialog.show();
        } else {
            shareDialog.dismiss();
        }
    }

    @Override
    public void showMessage(String message) {
        Logger.i(message);
    }

    @Override
    public void addSalePage() {
        current_page_1++;
    }

    @Override
    public void addRentPage() {
        current_page_2++;
    }

    @Override
    public void setSaleFragmentData(List<HouseBo> data) {
        ((HouseListFragment)mFragments.get(0)).setRefreshData(data, false);
    }

    @Override
    public void setRendFragmentData(List<HouseBo> data) {
        ((HouseListFragment)mFragments.get(1)).setRefreshData(data, false);
    }

    @Override
    public void setFragmentNoData(int fragment) {
        ((HouseListFragment)mFragments.get(fragment)).noData();
    }

    @Override
    public void showLoading() {}

    @Override
    public void dismissLoading() {}

    @Override
    public void showError(String msg) {}

    private class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabEntities.get(position).getTabTitle();
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }
    }

    @Override
    protected String getTalkingDataPageName() {
        return "经济人店铺";
    }
}
