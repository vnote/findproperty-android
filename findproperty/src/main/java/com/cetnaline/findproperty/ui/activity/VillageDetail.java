package com.cetnaline.findproperty.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.base.BaseFragment;
import com.cetnaline.findproperty.base.BaseFragmentActivity;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.cetnaline.findproperty.ui.fragment.VillageDetailFragment;
import com.cetnaline.findproperty.utils.StatusBarCompat;

/**
 * 小区详情—activity布局
 * Created by fanxl2 on 2016/8/2.
 */
public class VillageDetail extends BaseFragmentActivity {

	private String estCode;

	@Override
	protected BaseFragment getFirstFragment(int type) {
		return VillageDetailFragment.getInstance(estCode);
	}

	@Override
	protected int getFragmentContentId() {
		return R.id.fragment_container;
	}

	@Override
	protected void initView(Bundle savedInstanceState) {
		estCode = getIntent().getStringExtra(VillageDetailFragment.ESTATE_CODE_KEY);
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

	public void showToolbar(boolean isShow){
		super.showToolbar(isShow);
	}

	public void toBack(){
		if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
			finish();
		}else {
			onBackPressed();
		}
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
		return "小区详情";
	}
}
