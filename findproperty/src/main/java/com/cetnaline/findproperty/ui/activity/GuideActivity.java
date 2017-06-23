package com.cetnaline.findproperty.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.alexvasilkov.gestures.commons.DepthPageTransformer;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.base.BaseActivity;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.cetnaline.findproperty.utils.DataHolder;
import com.cetnaline.findproperty.utils.SharedPreferencesUtil;
import com.cetnaline.findproperty.widgets.IndicatorView;

import butterknife.BindView;
import butterknife.OnClick;

import static com.cetnaline.findproperty.ui.activity.SplashActivity.DATA_LOAD_SUCCESS;

/**
 * Created by fanxl2 on 2016/8/23.
 */
public class GuideActivity extends BaseActivity {

	private final int[] IMGS = new int[]{R.drawable.ic_lead_1,
			R.drawable.ic_lead_2, R.drawable.ic_lead_3};

	public static final String HAS_GUIDE_KEY = "HAS_GUIDE_KEY";
	public static final String GUILD_FLAG = "GUILD_FLAG";

	@BindView(R.id.guide_vp)
	ViewPager guide_vp;

	@BindView(R.id.guide_enter)
	AppCompatTextView guide_enter;

	@BindView(R.id.ll_bottom_bt)
	LinearLayout ll_bottom_bt;

	@BindView(R.id.guide_iv)
	IndicatorView guide_iv;

	@BindView(R.id.guide_bt_owner)
	AppCompatTextView  guide_bt_owner;

	@BindView(R.id.guide_bt_found)
	AppCompatTextView guide_bt_found;

	@Override
	protected int getContentViewId() {
		return R.layout.act_guide;
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		//用户已登录时隐藏按钮
		if (DataHolder.getInstance().isUserLogin()) {
			guide_bt_owner.setVisibility(View.GONE);
			guide_bt_found.setVisibility(View.GONE);
		}

//		guide_vp.setPageTransformer(true, new DepthPageTransformer());
		guide_vp.setAdapter(new GuideAdapter());
		guide_iv.setViewPager(guide_vp);
		guide_iv.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				guide_enter.setVisibility(position + 1 == IMGS.length ? View.VISIBLE : View.GONE);
				ll_bottom_bt.setVisibility(position + 1 == IMGS.length ? View.VISIBLE : View.GONE);
				guide_iv.setVisibility(position + 1 == IMGS.length ? View.GONE : View.VISIBLE);
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});
	}

	@Override
	protected IPresenter createPresenter() {
		return null;
	}

	@Override
	protected void initBeforeSetContentView() {
		super.initBeforeSetContentView();
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	@Override
	protected void initToolbar() {
		showToolbar(false);
	}

	class GuideAdapter extends PagerAdapter {

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public int getCount() {
			return IMGS.length;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public View instantiateItem(ViewGroup container, int position) {
			ImageView image = new ImageView(getApplicationContext());
			image.setScaleType(ImageView.ScaleType.CENTER_CROP);
			image.setImageResource(IMGS[position]);
			container.addView(image, ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT);
			return image;
		}
	}

	@OnClick(R.id.guide_enter)
	public void enterClick() {
		SharedPreferencesUtil.saveBoolean(HAS_GUIDE_KEY, true);
		startActivity(new Intent(GuideActivity.this, MainTabActivity.class));
		finish();
	}

	@OnClick(R.id.guide_bt_owner)
	public void ownerClick() {
		SharedPreferencesUtil.saveBoolean(HAS_GUIDE_KEY, true);
		DataHolder.getInstance().setChoiceIntent(LoginActivity.LOGIN_INTENT_ENTRUST);
		startActivity(new Intent(GuideActivity.this, LoginActivity.class));
		finish();
	}

	@OnClick(R.id.guide_bt_found)
	public void foundClick() {
		SharedPreferencesUtil.saveBoolean(HAS_GUIDE_KEY, true);
		if (SharedPreferencesUtil.getBoolean(DATA_LOAD_SUCCESS)){
			DataHolder.getInstance().setChoiceIntent(LoginActivity.LOGIN_INTENT_INTENT);
			startActivity(new Intent(GuideActivity.this, LoginActivity.class));
			finish();
		}else {
			toast("基础数据加载失败，请重启加载！");
		}

	}

	@Override
	protected String getTalkingDataPageName() {
		return "引导页";
	}

}
