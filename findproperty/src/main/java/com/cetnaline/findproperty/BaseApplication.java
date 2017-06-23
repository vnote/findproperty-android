package com.cetnaline.findproperty;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.support.multidex.MultiDexApplication;

import com.baidu.lbsapi.BMapManager;
import com.baidu.lbsapi.MKGeneralListener;
import com.baidu.mapapi.SDKInitializer;
import com.cetnaline.findproperty.ui.activity.SplashActivity;
import com.cetnaline.findproperty.utils.DataHolder;
import com.cetnaline.findproperty.utils.DbUtil;
import com.cetnaline.findproperty.utils.LocationUtil;
import com.cetnaline.findproperty.utils.RcUtil;
import com.cetnaline.findproperty.utils.SharedPreferencesUtil;
import com.cetnaline.findproperty.utils.TalkingDataUtil;
import com.fm.openinstall.OpenInstall;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
//import com.taobao.hotfix.HotFixManager;
//import com.taobao.hotfix.PatchLoadStatusListener;
//import com.taobao.hotfix.util.PatchStatusCode;

import java.util.Locale;

import cn.jpush.android.api.JPushInterface;
import io.rong.imkit.RongIM;

/**
 * Created by fanxl2 on 2016/7/21.
 */
public class BaseApplication extends MultiDexApplication {

	private static Context mContext;

	private static boolean TEST = BuildConfig.TEST;

	public BMapManager mBMapManager = null;

	//当前进程
	public String curProcessName;

	public static String appVersion;

	public static String appId;

	//全局异常捕获
	CrashHandler crashHandler = CrashHandler.getInstance();
	@Override
	public void onCreate() {
		super.onCreate();
		TalkingDataUtil.init(this);
//		initApp();
//		initHotFix();
		//数据库初始化
		DbUtil.init(this);

		// 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
		SDKInitializer.initialize(this);
		mContext = this;

//		if (!LeakCanary.isInAnalyzerProcess(this)) {
//			// This process is dedicated to LeakCanary for heap analysis.
//			// You should not init your app in this process.
//			LeakCanary.install(this);
//		}


		//logger
		Logger.init(BuildConfig.LOGGER_TAG)
				.logLevel(BuildConfig.DEBUG? LogLevel.FULL : LogLevel.NONE)
				.methodCount(3);
		//sp工具
		SharedPreferencesUtil.init(getApplicationContext());
		//rong cloud
		//屏蔽多进程重复启动
		if (BuildConfig.APPLICATION_ID.equals(getCurProcessName())) {
			Logger.i("buobao RC init...:"+getCurProcessName());
			RongIM.init(this, BuildConfig.RONG_CLOUD_KEY);
			RcUtil.init(this);
		}

		//初始化定位工具
		LocationUtil.init(this);
		//设置语言
		setLanguage(getLanguage());

		JPushInterface.setDebugMode(TEST);
		JPushInterface.init(this);
		JPushInterface.setLatestNotificationNumber(this,4);  //最多显示4条通知栏信息
		if (!DataHolder.getInstance().isUserLogin() && !JPushInterface.isPushStopped(this)) {
			JPushInterface.stopPush(this);
		}

		initEngineManager(this);
		//异常捕获
//		crashHandler.init(getApplicationContext());

		//open install init
		OpenInstall.init(this);
		OpenInstall.setDebug(BuildConfig.DEBUG ? true : false);

		//屏蔽系统字体设置
//		getResources().getDisplayMetrics().scaledDensity = getResources().getDisplayMetrics().scaledDensity/getResources().getConfiguration().fontScale;
	}

//	@Override
//	public void onConfigurationChanged(Configuration newConfig) {
//		getResources().getDisplayMetrics().scaledDensity = getResources().getDisplayMetrics().scaledDensity/newConfig.fontScale;
//		super.onConfigurationChanged(newConfig);
//	}

	public String getCurProcessName() {
		if (curProcessName == null) {
			int pid = android.os.Process.myPid();
			ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
			for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
					.getRunningAppProcesses()) {
				if (appProcess.pid == pid) {
					curProcessName = appProcess.processName;
					return appProcess.processName;
				}
			}
		}
		return curProcessName;
	}

	private void initApp() {
		this.appId = "82871-1"; //替换掉自己应用的appId
		try {
			this.appVersion = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
		} catch (Exception e) {
			this.appVersion = "1.0.0";
		}
	}


	public static Context getContext() {
		return mContext;
	}

	/**
	 * 获取当前语言设置
	 * @return
     */
	public int getLanguage(){
		if (SharedPreferencesUtil.getInt(AppContents.LANGUAGE) <= 0) {
			return AppContents.LANGUAGE_CHINESE;
		}
		return AppContents.LANGUAGE_ENGLISH;
	}

	/**
	 * 设置语言
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	public void setLanguage(int languageType){
		Configuration config = getResources().getConfiguration();
		if (languageType == AppContents.LANGUAGE_ENGLISH) {
			Locale locale = new Locale("en");
			locale.setDefault(Locale.getDefault());
			config.setLocale(locale);
			SharedPreferencesUtil.saveInt(AppContents.LANGUAGE, AppContents.LANGUAGE_ENGLISH);
			getResources().updateConfiguration(config,getResources().getDisplayMetrics());
		} else {
			/* 有问题啊
			Locale locale = new Locale("zh-rCN");
			config.setLocale(locale);
			locale.setDefault(Locale.getDefault());
			SharedPreferencesUtil.saveInt(AppContents.LANGUAGE, AppContents.LANGUAGE_CHINESE);
			getResources().updateConfiguration(config, getResources().getDisplayMetrics());
			*/
		}
	}

	public void initEngineManager(Context context) {
		if (mBMapManager == null) {
			mBMapManager = new BMapManager(context);
		}

		if (!mBMapManager.init(new MyGeneralListener())) {
			Logger.e("BMapManager  初始化错误!");
		}
	}

	// 常用事件监听，用来处理通常的网络错误，授权验证错误等
	public static class MyGeneralListener implements MKGeneralListener {

		@Override
		public void onGetPermissionState(int iError) {
			// 非零值表示key验证未通过
			if (iError != 0) {
				// 授权Key错误：
				Logger.e("请在AndoridManifest.xml中输入正确的授权Key,并检查您的网络连接是否正常！error: " + iError);
			} else {
				Logger.i("key认证成功");
			}
		}
	}

	private void initHotFix(){
//		HotFixManager.getInstance().setContext(this)
//				.setAppVersion(appVersion)
//				.setAppId(appId)
//				.setAesKey(null)
//				.setSupportHotpatch(true)
//				.setEnableDebug(true)
//				.setPatchLoadStatusStub(new PatchLoadStatusListener() {
//					@Override
//					public void onload(final int mode, final int code, final String info, final int handlePatchVersion) {
//						// 补丁加载回调通知
//						if (code == PatchStatusCode.CODE_SUCCESS_LOAD) {
//							Logger.i("补丁加载成功");
//						} else if (code == PatchStatusCode.CODE_ERROR_NEEDRESTART) {
//							// TODO: 10/24/16 表明新补丁生效需要重启. 业务方可自行实现逻辑, 提示用户或者强制重启, 建议: 用户可以监听进入后台事件, 然后应用自杀
//							Logger.i("新补丁生效需要重启. 业务方可自行实现逻辑");
//							restartApp();
//						} else if (code == PatchStatusCode.CODE_ERROR_INNERENGINEFAIL) {
//							// 内部引擎加载异常, 推荐此时清空本地补丁, 但是不清空本地版本号, 防止失败补丁重复加载
//							Logger.i("部引擎加载异常, 清空本地补丁");
//							HotFixManager.getInstance().cleanPatches(false);
//						} else {
//							// TODO: 10/25/16 其它错误信息, 查看PatchStatusCode类说明
//							Logger.e("补丁出现错误:"+code);
//						}
//					}
//				}).initialize();
	}

	private void restartApp(){
		Intent mStartActivity = new Intent(this, SplashActivity.class);
		int mPendingIntentId = 123456;
		PendingIntent mPendingIntent = PendingIntent.getActivity(this, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager mgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
		mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
		System.exit(0);
	}

}