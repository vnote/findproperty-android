package com.cetnaline.findproperty.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.base.BaseFragment;
import com.cetnaline.findproperty.base.BaseFragmentActivity;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.cetnaline.findproperty.ui.fragment.EntrustFragment;
import com.cetnaline.findproperty.ui.fragment.MyEntrustFragment;
import com.cetnaline.findproperty.utils.DataHolder;
import com.cetnaline.findproperty.utils.StatusBarCompat;

/**
 * 委托 Activity
 * Created by fanxl2 on 2016/8/8.
 */
public class EntrustActivity extends BaseFragmentActivity {

	public static final int ENTRUST_TYPE_SALE = 0; //出售
	public static final int ENTRUST_TYPE_RENT = 1; //出租
	public static final int ENTRUST_TYPE_MY = 2; //我的委托

	@Override
	protected BaseFragment getFirstFragment(int type) {
		switch (type){
			case ENTRUST_TYPE_SALE:
				setToolBarTitle("新增委托");
				return EntrustFragment.getInstance(ENTRUST_TYPE_SALE, 0);
			case ENTRUST_TYPE_RENT:
				setToolBarTitle("新增委托");
				return EntrustFragment.getInstance(ENTRUST_TYPE_RENT, 0);
			case ENTRUST_TYPE_MY:
				setToolBarTitle("我的委托");
				return MyEntrustFragment.getInstance(0);
			default:
				return EntrustFragment.getInstance(ENTRUST_TYPE_SALE, 0);
		}
	}

	@Override
	protected int getFragmentContentId() {
		return R.id.fragment_container;
	}

	@Override
	protected void initView(Bundle savedInstanceState) {

	}

	@Override
	protected int getContentViewId() {
		return R.layout.act_frag;
	}

	@Override
	protected IPresenter createPresenter() {
		return null;
	}

	@Override
	protected void initToolbar() {
		StatusBarCompat.setStatusBarColor(this, Color.WHITE);
		toolbar.setNavigationOnClickListener(view -> {
			toBack();
		});
	}

	public void setToolBarTitle(String title){
		toolbar.setTitle(title);
		center_title.setVisibility(View.INVISIBLE);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	public void setCenterTitle(String title){
		toolbar.setTitle("");
		center_title.setText(title);
		center_title.setVisibility(View.VISIBLE);
	}

	public void hideBack(){
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
	}

	public void toBack(){
		if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
			if (DataHolder.getInstance().getChoiceIntent()==LoginActivity.LOGIN_INTENT_ENTRUST){
				startActivity(new Intent(this, MainTabActivity.class));
			}
			finish();
		}else {
			onBackPressed();
		}
	}

	@Override
	protected void beforeFinish() {
		super.beforeFinish();
		if (DataHolder.getInstance().getChoiceIntent()==LoginActivity.LOGIN_INTENT_ENTRUST){
			startActivity(new Intent(this, MainTabActivity.class));
		}
	}

	@Override
	protected String getTalkingDataPageName() {
		return "我的委托";
	}
}
