package com.cetnaline.findproperty.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.widget.Toolbar;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.base.BaseActivity;
import com.cetnaline.findproperty.presenter.impl.ScanPresenter;
import com.cetnaline.findproperty.presenter.ui.ScanContract;
import com.cetnaline.findproperty.ui.fragment.HouseDetailFragment;
import com.cetnaline.findproperty.ui.fragment.MapFragment;
import com.cetnaline.findproperty.ui.fragment.VillageDetailFragment;
import com.cetnaline.findproperty.utils.MyUtils;
import com.cetnaline.findproperty.utils.StatusBarCompat;
import com.orhanobut.logger.Logger;

import butterknife.BindView;
import cn.bingoogolapple.qrcode.core.QRCodeView;

/**
 * Created by fanxl2 on 2016/10/18.
 */

public class ScanActivity extends BaseActivity<ScanPresenter> implements QRCodeView.Delegate, ScanContract.View {

	@BindView(R.id.scan_view)
	QRCodeView scan_view;

	@BindView(R.id.scan_toolbar)
	Toolbar scan_toolbar;

	@Override
	protected int getContentViewId() {
		return R.layout.act_scan;
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		scan_view.setDelegate(this);

		scan_toolbar.setTitle("");

		setSupportActionBar(scan_toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		scan_toolbar.setNavigationOnClickListener((v) -> {
			onBackPressed();
		});
	}

	private void vibrate() {
		Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		vibrator.vibrate(200);
	}

//	@Override
//	protected void onStart() {
//		super.onStart();
//		scan_view.startCamera();
//	}

	@Override
	protected void onResume() {
		super.onResume();
		scan_view.showScanRect();
		scan_view.startSpot();
	}

	@Override
	protected void onStop() {
		scan_view.stopCamera();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		scan_view.onDestroy();
		super.onDestroy();
	}

	@Override
	protected ScanPresenter createPresenter() {
		return new ScanPresenter();
	}

	@Override
	protected void initToolbar() {
		showToolbar(false);
		StatusBarCompat.translucentStatusBar(this, true);
//		StatusBarCompat.setStatusBarColor(this, Color.WHITE);
//		toolbar.setNavigationOnClickListener(view -> {
//			onBackPressed();
//		});
	}

	@Override
	public void onScanQRCodeSuccess(String result) {

		vibrate();

		result = result.toLowerCase();

		if (result.contains("centanet")){
			Logger.i(result);
			if (result.equals("http://sh.centanet.com/m/ershoufang/")
					|| result.equals("http://sh.centanet.com/ershoufang/")
					|| result.equals("https://sh.centanet.com/m/ershoufang/")
					|| result.equals("https://sh.centanet.com/ershoufang/")){
				//到二手房列表
				Intent intent = new Intent(this, HouseList.class);
				intent.putExtra(MapFragment.HOUSE_TYPE_KEY, MapFragment.HOUSE_TYPE_SECOND);
				startActivity(intent);
			}else if (result.equals("http://sh.centanet.com/m/zufang/")
					|| result.equals("http://sh.centanet.com/zufang/")
					||result.equals("https://sh.centanet.com/m/zufang/")
					|| result.equals("https://sh.centanet.com/zufang/")){
				Intent intent = new Intent(this, HouseList.class);
				intent.putExtra(MapFragment.HOUSE_TYPE_KEY, MapFragment.HOUSE_TYPE_RENT);
				startActivity(intent); //https://sh.centanet.com/act/index.html
			}else if (result.contains("http://sh.centanet.com/m/ershoufang") ||
					result.contains("http://sh.centanet.com/ershoufang")||
					result.contains("https://sh.centanet.com/m/ershoufang") ||
					result.contains("https://sh.centanet.com/ershoufang")){
				String postId = result.substring(result.indexOf("ershoufang/")+11, result.indexOf(".html"));

				if (postId.length()<30){
					mPresenter.getHouseByAdsNo(postId);
				}else {
					Intent intent = new Intent(this, HouseDetail.class);
					intent.putExtra(MapFragment.HOUSE_TYPE_KEY, MapFragment.HOUSE_TYPE_SECOND);
					intent.putExtra(HouseDetailFragment.HOUSE_ID_KEY, postId);
					startActivity(intent);
				}
			}else if("http://sh.centanet.com/m/".equalsIgnoreCase(result) ||
					"https://sh.centanet.com/m/".equalsIgnoreCase(result)){

				Intent intent = new Intent(this, MainTabActivity.class);
				intent.putExtra(MainTabActivity.SELECTED_TAB_KEY, MainTabActivity.TAB_HOME);
				startActivity(intent);

			}else if (result.contains("http://sh.centanet.com/m/zufang") ||
					result.contains("http://sh.centanet.com/zufang")||
					result.contains("https://sh.centanet.com/m/zufang") ||
					result.contains("https://sh.centanet.com/zufang")){
				String postId = result.substring(result.indexOf("zufang/")+7, result.indexOf(".html"));
				if (postId.length()<30){
					mPresenter.getHouseByAdsNo(postId);
				}else {
					Intent intent = new Intent(this, HouseDetail.class);
					intent.putExtra(MapFragment.HOUSE_TYPE_KEY, MapFragment.HOUSE_TYPE_RENT);
					intent.putExtra(HouseDetailFragment.HOUSE_ID_KEY, postId);
					startActivity(intent);
				} //http://sh.centanet.com/m/jingjiren/zf-aa91931/
			}else if (result.contains("http://sh.centanet.com/m/jingjiren/")
					|| result.contains("http://sh.centanet.com/jingjiren/")||
					result.contains("https://sh.centanet.com/m/jingjiren/")
					|| result.contains("https://sh.centanet.com/jingjiren/")){

				String staffNo = result.substring(result.indexOf("-a")+1, result.lastIndexOf("/"));
				mPresenter.getStaffInfo(staffNo); // https://sh.centanet.com/m/act/
			}else if (result.contains("http://sh.centanet.com/m/act") ||
					result.contains("https://sh.centanet.com/m/act") ||
					result.contains("http://sh.centanet.com/act")||
					result.contains("https://sh.centanet.com/act")){
				Intent intent = new Intent(this, MainTabActivity.class);
				intent.putExtra(MainTabActivity.SELECTED_TAB_KEY, MainTabActivity.TAB_FOUND);
				startActivity(intent);
			}else if (result.equals("http://sh.centanet.com/m/xiaoqu/") ||
					result.equals("http://sh.centanet.com/xiaoqu/")||
					result.equals("https://sh.centanet.com/m/xiaoqu/") ||
					result.equals("https://sh.centanet.com/xiaoqu/")){

				Intent intent = new Intent(this, EstateList.class);
				startActivity(intent);

			}else if (result.contains("http://sh.centanet.com/m/xiaoqu/") ||
					result.contains("http://sh.centanet.com/xiaoqu/") ||
					result.contains("https://sh.centanet.com/m/xiaoqu/") ||
					result.contains("https://sh.centanet.com/xiaoqu/")){
				String estateCode = result.substring(result.indexOf("xq-")+3, result.lastIndexOf("/"));
				Intent intent = new Intent(this, VillageDetail.class);
				intent.putExtra(VillageDetailFragment.ESTATE_CODE_KEY, estateCode);
				startActivity(intent);

			}else {
				Intent intent = new Intent(this, WebActivity.class);
				intent.putExtra(WebActivity.TARGET_URL, result);
				startActivity(intent);
			}
		}else {
			Intent intent = new Intent(this, WebActivity.class);
			intent.putExtra(WebActivity.TARGET_URL, "https://sh.centanet.com/m/notfound/");
			startActivity(intent);
		}
		scan_view.startSpot();
	}

	@Override
	protected void onPause() {
		super.onPause();
		scan_view.stopSpot();
	}

	@Override
	public void onScanQRCodeOpenCameraError() {
		toast("相机打开出错");
		Logger.e("相机打开出错!");
	}

	@Override
	public void setHouse(String postId, int houseType) {
		Intent intent = new Intent(this, HouseDetail.class);
		intent.putExtra(MapFragment.HOUSE_TYPE_KEY, houseType);
		intent.putExtra(HouseDetailFragment.HOUSE_ID_KEY, postId);
		startActivity(intent);
	}

	@Override
	public void setStaff(String staffNo, String staffName) {
		MyUtils.toStoreHome(this, staffNo, staffName);
	}

	@Override
	public void showLoading() {
		showLoadingDialog();
	}

	@Override
	public void dismissLoading() {
		cancelLoadingDialog();
	}

	@Override
	public void showError(String msg) {
		toast(msg);
	}

	@Override
	protected String getTalkingDataPageName() {
		return "二维码扫描";
	}
}
