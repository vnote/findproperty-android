package com.cetnaline.findproperty.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;

import com.cetnaline.findproperty.presenter.IPresenter;

/**
 * 所有有Fragment的Activity的基类
 * Created by fanxl2 on 2016/7/21.
 */
public abstract class BaseFragmentActivity<T extends IPresenter> extends BaseActivity{

	//获取第一个fragment
	protected abstract Fragment getFirstFragment(int type);

	//布局中Fragment的ID
	protected abstract int getFragmentContentId();

	protected abstract void initView(Bundle savedInstanceState);

	public static final String FRAGMENT_TYPE = "FRAGMENT_TYPE";

	protected T iPresenter;


	@Override
	protected void init(Bundle savedInstanceState) {
		iPresenter = (T) mPresenter;
		initView(savedInstanceState);
		int fragmentType = getIntent().getIntExtra(FRAGMENT_TYPE, 0);
		//避免重复添加Fragment
		if (null == getSupportFragmentManager().getFragments()) {
			Fragment firstFragment = getFirstFragment(fragmentType);
			if (null != firstFragment) {
				addFragment(firstFragment);
			}
		}
	}

	//添加fragment
	protected void addFragment(Fragment fragment) {
		if (fragment != null) {
			FragmentTransaction tr  = getSupportFragmentManager().beginTransaction();
			tr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			tr.replace(getFragmentContentId(), fragment, fragment.getClass().getSimpleName())
					.addToBackStack(fragment.getClass().getSimpleName())
					.commitAllowingStateLoss();
		}
	}

	protected void addFragmentNoBack(Fragment fragment){
		if (fragment != null) {
			FragmentTransaction tr  = getSupportFragmentManager().beginTransaction();
			tr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			tr.replace(getFragmentContentId(), fragment, fragment.getClass().getSimpleName())
					.addToBackStack(null)
					.commitAllowingStateLoss();
		}
	}

	//返回键返回事件
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_BACK == keyCode) {
			if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
				beforeFinish();
				finish();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	//移除fragment
	protected void toBack(){
		if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
			beforeFinish();
			finish();
		}else {
			onBackPressed();
		}
	}

	protected void beforeFinish(){

	}

}
