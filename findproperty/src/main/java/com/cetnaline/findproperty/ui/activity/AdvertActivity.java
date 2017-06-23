package com.cetnaline.findproperty.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.bean.AppAdBo;
import com.cetnaline.findproperty.base.BaseActivity;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.cetnaline.findproperty.utils.ExternalCacheDirUtil;
import com.cetnaline.findproperty.utils.glide.GlideLoad;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

/**
 *广告页
 * Created by fanxl2 on 2016/9/1.
 */
public class AdvertActivity extends BaseActivity {

	private DrawableRequestBuilder<String> requestBuilder;

	public static final String ADVERT_DATA_KEY = "ADVERT_DATA_KEY";

	private AppAdBo advert;

	@BindView(R.id.advert_img)
	ImageView advert_img;

	@BindView(R.id.advert_over)
	Button advert_over;

	@BindView(R.id.progress)
	CircularProgressBar progress;

	@Override
	protected int getContentViewId() {
		return R.layout.act_advert;
	}

	@OnClick(R.id.advert_over)
	public void overAdvert(){
		timer.cancel();
		toMain();
	}

	@Override
	protected void initBeforeSetContentView() {
		super.initBeforeSetContentView();
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		requestBuilder = GlideLoad.init(this);
		advert = (AppAdBo) getIntent().getSerializableExtra(AdvertActivity.ADVERT_DATA_KEY);
		progress.setProgressWithAnimation(100, 4500);

		String url = advert.getImgUrl();
		String advertName = url.substring(url.lastIndexOf("/")+1, url.length());
		File file = new File(ExternalCacheDirUtil.getAdvertCacheDir(this), advertName);
		if (file.exists()){
			Glide.with(this).load(file).centerCrop().into(advert_img);
		}else {
			GlideLoad.load(new GlideLoad.Builder(requestBuilder, advert.getImgUrl())
					.into(advert_img));
		}
		advertUrl = advert.getAdvertUrl();
		countDown();
	}

	@Override
	protected IPresenter createPresenter() {
		return null;
	}

	@Override
	protected void initToolbar() {
		showToolbar(false);
	}

	private String advertUrl;

	@OnClick(R.id.advert_img)
	public void toAdvert(){
		timer.cancel();
		Intent intent = new Intent(AdvertActivity.this, WebActivity.class);
		intent.putExtra(WebActivity.TARGET_URL, advertUrl);
		intent.putExtra(WebActivity.WEB_TYPE_KEY, WebActivity.WEB_TO_HOME);
		intent.putExtra(WebActivity.WEB_SHARE_KEY, true);
		startActivity(intent);
		finish();
	}

	private CountDownTimer timer;

	private void countDown(){
		timer = new CountDownTimer(4 * 1000, 1000) {

			@Override
			public void onTick(long millisUntilFinished) {
				advert_over.setText("跳过"+millisUntilFinished / 1000);
			}

			@Override
			public void onFinish() {
				toMain();
			}
		}.start();
	}

	private void toMain(){
		startActivity(new Intent(AdvertActivity.this, MainTabActivity.class));
		AdvertActivity.this.finish();
	}

	@Override
	protected String getTalkingDataPageName() {
		return "首页-弹出广告";
	}
}
