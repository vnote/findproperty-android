package com.cetnaline.findproperty.ui.activity;

import android.os.Bundle;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.base.BaseActivity;
import com.cetnaline.findproperty.presenter.IPresenter;

import butterknife.OnClick;

/**
 * Created by fanxl2 on 2016/7/27.
 */
public class TestActivity extends BaseActivity{

	@Override
	protected int getContentViewId() {
		return R.layout.test_layout;
	}

	@Override
	protected void init(Bundle savedInstanceState) {

	}

	@Override
	protected IPresenter createPresenter() {
		return null;
	}

	@Override
	protected void initToolbar() {

	}

	private String handle = "close";

	@OnClick(R.id.bt_test)
	public void click(){

	}



}
