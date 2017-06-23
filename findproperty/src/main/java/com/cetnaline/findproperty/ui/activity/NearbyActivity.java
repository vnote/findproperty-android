package com.cetnaline.findproperty.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.base.BaseFragment;
import com.cetnaline.findproperty.base.BaseFragmentActivity;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.cetnaline.findproperty.ui.fragment.NearbyFragment;
import com.cetnaline.findproperty.utils.StatusBarCompat;

/**
 * Created by fanxl2 on 2016/8/3.
 */
public class NearbyActivity extends BaseFragmentActivity {

	private double latitude;
	private double longitude;

	@Override
	protected BaseFragment getFirstFragment(int type) {
		return NearbyFragment.getInstance(latitude, longitude);
	}

	@Override
	protected int getFragmentContentId() {
		return R.id.fragment_container;
	}

	@Override
	protected void initView(Bundle savedInstanceState) {
		latitude = getIntent().getDoubleExtra(NearbyFragment.LATITUDE_KEY, 0);
		longitude = getIntent().getDoubleExtra(NearbyFragment.LONGITUDE_KEY, 0);
	}

	@Override
	protected int getContentViewId() {
		return R.layout.act_fragment;
	}

	@Override
	protected IPresenter createPresenter() {
		return null;
	}

	@Override
	protected void initToolbar() {
		showToolbar(false);
		StatusBarCompat.translucentStatusBar(this, true);
	}

	public void setToolbar(Toolbar toolbar){
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		toolbar.setNavigationOnClickListener((v) -> {
			toBack();
		});
	}

	@Override
	protected String getTalkingDataPageName() {
		return "房源详情周边";
	}
}
