package com.cetnaline.findproperty.ui.activity;

import android.graphics.Color;
import android.os.Bundle;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.base.BaseFragment;
import com.cetnaline.findproperty.base.BaseFragmentActivity;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.cetnaline.findproperty.ui.fragment.LookAboutListFragment;
import com.cetnaline.findproperty.ui.fragment.ToLookAboutFragment;
import com.cetnaline.findproperty.utils.StatusBarCompat;

/**
 * Created by fanxl2 on 2016/8/16.
 */
public class LookAbout extends BaseFragmentActivity {

	public static final int LOOK_TYPE_LIST = 0;
	public static final int LOOK_TYPE_TO = 1;
	public static final int LOOK_TYPE_RECORD = 2;

	@Override
	protected BaseFragment getFirstFragment(int type) {
		if (type==LOOK_TYPE_LIST){
			setLeftText("约看单");
			return LookAboutListFragment.getInstance();
		}else if (type==LOOK_TYPE_TO){
			setLeftText("待约看");
			return ToLookAboutFragment.getInstance(LOOK_TYPE_TO);
		}else {
			setLeftText("约看记录");
			return ToLookAboutFragment.getInstance(LOOK_TYPE_RECORD);
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

	public void setLeftText(String text){
		toolbar.setTitle(text);
	}

	public void setFinish(){
		finish();
	}

	@Override
	protected String getTalkingDataPageName() {
		return "约看单";
	}
}
