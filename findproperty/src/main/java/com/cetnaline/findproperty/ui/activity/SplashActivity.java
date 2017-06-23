package com.cetnaline.findproperty.ui.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

//import com.baidu.mobstat.SendStrategyEnum;
//import com.baidu.mobstat.StatService;
import com.cetnaline.findproperty.AppContents;
import com.cetnaline.findproperty.BaseApplication;
import com.cetnaline.findproperty.BuildConfig;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.base.BaseActivity;
import com.cetnaline.findproperty.ui.service.DataService;
import com.cetnaline.findproperty.presenter.impl.SplashPresenter;
import com.cetnaline.findproperty.presenter.ui.SplashContract;
import com.cetnaline.findproperty.utils.BaiduCountUtil;
import com.cetnaline.findproperty.utils.DataHolder;
import com.cetnaline.findproperty.utils.DbUtil;
import com.cetnaline.findproperty.utils.MyUtils;
import com.cetnaline.findproperty.utils.SharedPreferencesUtil;
import com.fm.openinstall.OpenInstall;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;

/**
 * Created by fanxl2 on 2016/7/27.
 */
public class SplashActivity extends BaseActivity<SplashPresenter> implements SplashContract.View {

	@Override
	protected int getContentViewId() {
		return R.layout.act_splash;
	}

	public static final String DATA_LOAD_SUCCESS = "DATA_LOAD_SUCCESS";

	public static final String DATA_VERSION_KEY = "DATA_VERSION_KEY";

	public static final int FILE_DOWN_SUCCESS = 105;
	public static final int FILE_DOWN_FAIL = 106;

	private Subscription subscription;

	private long startTime;

	@Override
	protected void initBeforeSetContentView() {
		super.initBeforeSetContentView();
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		//百度统计
		BaiduCountUtil.init(this);

		//openinstall获取渠道统计信息
		OpenInstall.getInstall((appData, error) -> {
			if (error == null) {
				//获取渠道数据
//				toast("SplashActivity channel = " + appData.getChannel());
				Logger.d("SplashActivity channel = " + appData.getChannel());
				//获取个性化安装数据
				Logger.d("buobao install = " + appData.getData());

				JSONTokener tokener = new JSONTokener(appData.getData());
				try {
					JSONObject obj = (JSONObject) tokener.nextValue();
//					Logger.d("buobao: convert:"+obj.getString("YaoQingMa"));
					String code = obj.getString("YaoQingMa");
					if (code != null && !"".equals(code.trim()) && !"null".equals(code)) {
						SharedPreferencesUtil.saveString(AppContents.YAO_QING_MA, code);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			} else {
				Logger.d("SplashActivity error : "+error.toString());
			}
        });

		ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION_CODE);
	}

	/**
	 * 网络正常就进引导页或者主页
	 */
	private void goNext(){

		startTime = System.currentTimeMillis();

		isCheckNet = false;

		int version = DbUtil.init(BaseApplication.getContext()).getVersion();
		if (version != DataHolder.getInstance().getDatabaseVersion()) {
			DataHolder.getInstance().setDatabaseVersion(version);
			SharedPreferencesUtil.saveBoolean("GScopeData",false);
			SharedPreferencesUtil.saveBoolean("railLineData",false);
			SharedPreferencesUtil.saveBoolean("searchData",false);
			SharedPreferencesUtil.saveBoolean("storeData",false);
			SharedPreferencesUtil.saveBoolean(DATA_LOAD_SUCCESS, false);
		}

		mPresenter.getDataVersion();

		subscription = Observable.timer(1000, TimeUnit.MILLISECONDS)
				.subscribe(time -> {

					if (!SharedPreferencesUtil.getBoolean(GuideActivity.HAS_GUIDE_KEY)
							|| !BuildConfig.GUILD_FLAG.equals(SharedPreferencesUtil.getString(GuideActivity.GUILD_FLAG))){
						SharedPreferencesUtil.saveString(GuideActivity.GUILD_FLAG,BuildConfig.GUILD_FLAG);
						startActivity(new Intent(SplashActivity.this, GuideActivity.class));
						SplashActivity.this.finish();
					}else {
						next();
					}

					Logger.i("引导页时间:"+ (System.currentTimeMillis()-startTime));

				});
	}


	private void next() {
		startActivity(new Intent(SplashActivity.this, MainTabActivity.class));
		SplashActivity.this.finish();
	}

	@Override
	protected SplashPresenter createPresenter() {
		return new SplashPresenter();
	}

	@Override
	protected void initToolbar() {
		showToolbar(false);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!MyUtils.isNetworkAvailable(this)){
			showNetWorkDialog();
		}else if (isCheckNet){
			goNext();
		}
	}

	private boolean isCheckNet = false;

	private void showNetWorkDialog() {
		MyUtils.showDiloag(this, R.layout.dialog_alert, 300, -1, false, new MyUtils.DialogActionListener() {
			@Override
			public void listener(View layout, Dialog dialog) {
				TextView title = (TextView) layout.findViewById(R.id.title);
				TextView submit = (TextView) layout.findViewById(R.id.submit);
				TextView cancel = (TextView) layout.findViewById(R.id.cancel);
				title.setText("无法连接到服务器，请检查网络配置或稍后再试");
				submit.setText("检查");
				cancel.setText("退出");
				submit.setOnClickListener(v->{
					isCheckNet = true;
					startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
				});

				cancel.setOnClickListener(v->{
					SplashActivity.this.finish();
				});
			}
		});
	}

//	private int requestCount = 0;

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (requestCode == REQUEST_PERMISSION_CODE) {

			if (grantResults!=null && grantResults.length>0){
				if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
					boolean notice_lunch_mode = getIntent().getBooleanExtra("notice_lunch_mode",false);
					if (notice_lunch_mode) {
						DataHolder.getInstance().setNoticeLunchMode(notice_lunch_mode);
						DataHolder.getInstance().setWhichPage(getIntent().getStringExtra("page"));
						DataHolder.getInstance().setNewType(getIntent().getIntExtra("newType",-1));
						DataHolder.getInstance().setSubscribeType(getIntent().getIntExtra("subscribeType", -1));
					}

					if (MyUtils.isNetworkAvailable(this)){
						goNext();
					}
				} else {
					new AlertDialog.Builder(this)
							.setTitle("权限申请说明")
							.setMessage("APP需要文件读取权限才能正常运行，请允许")
							.setCancelable(false)
							.setPositiveButton("确定", (dialog, which) -> {
								toast("权限被拒绝，APP退出");
								SplashActivity.this.finish();
							}).show();
				}
			}
		}
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	public static final int REQUEST_PERMISSION_CODE = 10;

	private String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.CALL_PHONE};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (subscription!=null && !subscription.isUnsubscribed()){
			subscription.unsubscribe();
		}
	}

	@Override
	public void setDataVersion(Long dataVersion) {
		long currentVersion = SharedPreferencesUtil.getLong(DATA_VERSION_KEY);
		if (dataVersion>currentVersion || currentVersion==0){
			SharedPreferencesUtil.saveBoolean("searchData", false);
			SharedPreferencesUtil.saveBoolean("GScopeData", false);
			SharedPreferencesUtil.saveBoolean("railLineData", false); //地铁线路
			SharedPreferencesUtil.saveBoolean("storeData", false);

			startService(new Intent(getApplicationContext(), DataService.class));

			SharedPreferencesUtil.saveLong(DATA_VERSION_KEY, dataVersion);
		}else {
			if (!SharedPreferencesUtil.getBoolean(DATA_LOAD_SUCCESS)){
				startService(new Intent(getApplicationContext(), DataService.class));
			}
		}

	}
}
