package com.cetnaline.findproperty.base;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.cetnaline.findproperty.BaseApplication;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.cetnaline.findproperty.presenter.IView;
import com.cetnaline.findproperty.utils.DataHolder;
import com.cetnaline.findproperty.utils.TalkingDataUtil;
import com.cetnaline.findproperty.widgets.CProgressDialog;
import com.cetnaline.findproperty.widgets.MenuPopWindow;

import butterknife.ButterKnife;

/**
 * Activity的基类
 * Created by fanxl2 on 2016/7/21.
 */
public abstract class BaseActivity<T extends IPresenter> extends AutoLayoutActivity implements IView {

	static {
		AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
	}

	protected BaseApplication mApp;
	//布局文件ID
	protected abstract int getContentViewId();
	//初始化
	protected abstract void init(Bundle savedInstanceState);

	protected T mPresenter;

	protected abstract T createPresenter();

	protected Toolbar toolbar;

	protected TextView center_title;

	protected LinearLayout rootLayout;

	protected RelativeLayout shade_layout;
	protected boolean isShadeShow;

	//初始化
	protected abstract void initToolbar();

	private boolean isNetworkDialogShow = false;

	public void showNetworkDialog(String msg) {
		if (!isNetworkDialogShow) {
			isNetworkDialogShow = true;
			MenuPopWindow window = new MenuPopWindow(this, R.layout.network_dialog, (contentView, window1) -> {
				TextView textView = (TextView) contentView.findViewById(R.id.msg);
				textView.setText(msg);
			});
			new Handler().postDelayed(() -> {window.dismiss();isNetworkDialogShow = false;}, 3000);
			window.showAtLocation(rootLayout,Gravity.CENTER,0,0);
		}

	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		mApp = (BaseApplication) getApplication();
		initBeforeSetContentView();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		mPresenter = createPresenter();
		super.onCreate(savedInstanceState);
		// 这句很关键，注意是调用父类的方法
		if (getContentViewId() > 0) {
			super.setContentView(R.layout.base_tool_bar);
			setContentView(getContentViewId());
			initBaseToolbar();
		}
		if (toolbar!=null){
			initToolbar();
		}
		if (mPresenter!=null){
			mPresenter.attachView(this);
		}
		ButterKnife.bind(this);
		init(savedInstanceState);
		DataHolder.getInstance().putActivity(this.getClass().getName(), this);
	}

	protected void initBeforeSetContentView(){

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mPresenter != null) mPresenter.detachView();
	}

	@Override
	public void setContentView(int layoutId) {
		setContentView(View.inflate(this, layoutId, null));
	}

	@Override
	public void setContentView(View view) {
		rootLayout = (LinearLayout) findViewById(R.id.root_layout);
		if (rootLayout == null) return;
		rootLayout.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		initBaseToolbar();

		if (hasShade()) {
			RelativeLayout decor_layout = (RelativeLayout) findViewById(R.id.decor_layout);
			shade_layout = new RelativeLayout(this);
			decor_layout.addView(shade_layout,new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
			shade_layout.setBackgroundColor(getResources().getColor(R.color.shadeBg));
			if (clickShadeHide()) {
				shade_layout.setOnTouchListener(new View.OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if (event.getAction() == MotionEvent.ACTION_DOWN) {
							hideShade();
							return true;
						}
						return false;
					}
				});
			} else {
				shade_layout.setOnTouchListener((v, event) -> true);
			}
			shade_layout.setVisibility(View.GONE);
		}
	}

	/**
	 * 点击遮罩层关闭遮罩，默认关闭
	 * @return
     */
	public boolean clickShadeHide(){
		return true;
	}

	public void showShade(View[] vs) {
		if (shade_layout != null && initShade(vs)) {
			shade_layout.setVisibility(View.VISIBLE);
			isShadeShow = true;
		}
	}

	protected boolean initShade(View[] vs){
		return true;
	}

	public void hideShade() {
		if (shade_layout != null) {
			shade_layout.setVisibility(View.GONE);
			shade_layout.removeAllViews();
			isShadeShow = false;
		}
	}

	public boolean isShadeShow(){
		return isShadeShow;
	}

	private void initBaseToolbar(){
//		StatusBarCompat.setStatusBarColor(this, Color.parseColor("#FFEF4968"));
//		StatusBarCompat.translucentStatusBar(this, true);
		center_title = (TextView) findViewById(R.id.center_title);
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//		StatusBarCompat.setStatusBarColor(this, Color.TRANSPARENT);
	}

	/**
	 * Toast统一显示入口
	 *
	 * @param text 显示内容
	 */
	public void toast(CharSequence text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	protected void showToolbar(boolean isShow){
		toolbar.setVisibility(isShow?View.VISIBLE:View.GONE);
	}


	@Override
		protected void onResume() {
			if (!getTalkingDataPageName().equals("")) {
				TalkingDataUtil.onPageStart(this, getTalkingDataPageName());
				StatService.onPageStart(this, getTalkingDataPageName());
			}
			super.onResume();
		}

		@Override
		protected void onPause() {
			if (!getTalkingDataPageName().equals(""))
				TalkingDataUtil.onPageEnd(this, getTalkingDataPageName());
			StatService.onPageEnd(this, getTalkingDataPageName());
		super.onPause();
	}

	@Override
	public Resources getResources() {
		Resources res = super.getResources();
		Configuration config = new Configuration();
		config.setToDefaults();
		res.updateConfiguration(config, res.getDisplayMetrics());
		return res;
	}

	protected CProgressDialog loadingDialog;//加载进度框

	/**
	 * 显示进度Dialog
	 */
	protected void showLoadingDialog() {
		if (loadingDialog == null) {
			loadingDialog = new CProgressDialog(this,loadingCancelable());
		}
		loadingDialog.show();
	}

	protected boolean loadingCancelable() {
		return true;
	}

	/**
	 * 取消进度Dialog
	 */
	protected void cancelLoadingDialog() {
		if (loadingDialog != null &&
				loadingDialog.isShowing()) {
			loadingDialog.dismiss();
		}
	}

	protected boolean hasShade(){
		return false;
	}

	@Override
	public void finish() {
		super.finish();
		DataHolder.getInstance().removeActivity(this.getClass().getName());
	}

	protected String getTalkingDataPageName() {
		return "";
	}
}
