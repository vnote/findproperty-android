package com.cetnaline.findproperty.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.base.BaseFragmentActivity;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.cetnaline.findproperty.ui.fragment.HouseDetailFragment;
import com.cetnaline.findproperty.ui.fragment.MapFragment;
import com.cetnaline.findproperty.utils.StatusBarCompat;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

/**
 * 房源详情页面
 * Created by fanxl2 on 2016/8/2.
 */
public class HouseDetail extends BaseFragmentActivity {

	private String postId;
	private int houseType;
	private String estExtId;

	@Override
	protected Fragment getFirstFragment(int type) {
		if (houseType==MapFragment.HOUSE_TYPE_NEW){
			return HouseDetailFragment.getInstance(estExtId, HouseDetailFragment.FROM_TYPE_NORMAL);
		}else {
			return HouseDetailFragment.getInstance(postId, HouseDetailFragment.FROM_TYPE_NORMAL, houseType);
		}
	}

	@Override
	protected int getFragmentContentId() {
		return R.id.fragment_container;
	}

	@Override
	protected void initView(Bundle savedInstanceState) {
		postId = getIntent().getStringExtra(HouseDetailFragment.HOUSE_ID_KEY);
		houseType = getIntent().getIntExtra(MapFragment.HOUSE_TYPE_KEY, MapFragment.HOUSE_TYPE_SECOND);
		estExtId = getIntent().getStringExtra(HouseDetailFragment.ESTEXT_ID_KEY);
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

	public void toBack(){
		if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
			finish();
		}else {
			onBackPressed();
		}
	}

	public void showToolbar(boolean isShow){
		super.showToolbar(isShow);
	}

	public void setToolbar(Toolbar toolbar){
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		toolbar.setNavigationOnClickListener((v) -> {
			toBack();  //关闭页面
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		/**
		 * QQ分享回调
		 */
		if(requestCode == Constants.REQUEST_QQ_SHARE) {
			Tencent.handleResultData(data, new IUiListener() {
				@Override
				public void onComplete(Object o) {
					toast("分享成功");
				}

				@Override
				public void onError(UiError uiError) {
					toast("分享失败");
				}

				@Override
				public void onCancel() {
					toast("分享被取消");
				}
			});
		}
	}

	@Override
	protected String getTalkingDataPageName() {
		return "房源详情页";
	}
}
