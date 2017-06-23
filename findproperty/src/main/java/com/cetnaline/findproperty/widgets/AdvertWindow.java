package com.cetnaline.findproperty.widgets;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.ui.activity.MainTabActivity;
import com.cetnaline.findproperty.ui.activity.WebActivity;
import com.cetnaline.findproperty.utils.ExternalCacheDirUtil;
import com.cetnaline.findproperty.utils.MyUtils;
import com.cetnaline.findproperty.utils.SharedPreferencesUtil;
import com.cetnaline.findproperty.utils.glide.GlideLoad;
import com.zhy.autolayout.utils.AutoUtils;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by fanxl2 on 2016/7/27.
 */
public class AdvertWindow extends DialogFragment {

	public static AdvertWindow getInstance(String imageUrl, String advertUrl){
		Bundle bundle = new Bundle();
		bundle.putString("imageUrl", imageUrl);
		bundle.putString("advertUrl", advertUrl);
		AdvertWindow myRightMenu = new AdvertWindow();
		myRightMenu.setArguments(bundle);
		return myRightMenu;
	}

	@Override
	public void onStart() {
		super.onStart();

		Dialog dialog = getDialog();
		if (dialog!=null){
			dialog.setCancelable(false);
			WindowManager.LayoutParams p = getDialog().getWindow().getAttributes();
//			int[] phones = MyUtils.getPhoneWidthAndHeight(getActivity());

			p.width = AutoUtils.getPercentWidthSize(644);
			p.height = AutoUtils.getPercentHeightSize(780)+ MyUtils.dip2px(getActivity(), 50);
			p.y = -MyUtils.dip2px(getActivity(), 30);
			p.gravity = Gravity.CENTER_HORIZONTAL;
			dialog.getWindow().setAttributes(p);
			dialog.getWindow().getAttributes().windowAnimations = R.style.AdvertStyle;
			dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		}
	}

	@OnClick(R.id.advert_iv_close)
	public void advertClose(){
		SharedPreferencesUtil.saveString(MainTabActivity.ADVERT_URL_KEY, imgUrl);
		dismiss();
	}

	@BindView(R.id.advert_rv_img)
	RectangleView advert_rv_img;

//	private FragmentManager manager;

	@OnClick(R.id.advert_rv_img)
	public void toAdvert(){
		SharedPreferencesUtil.saveString(MainTabActivity.ADVERT_URL_KEY, imgUrl);
		Intent intent = new Intent(getActivity(), WebActivity.class);
		intent.putExtra(WebActivity.TARGET_URL, advertUrl);
		intent.putExtra(WebActivity.WEB_TYPE_KEY, WebActivity.WEB_TO_HOME);
		intent.putExtra(WebActivity.WEB_SHARE_KEY, true);
		startActivity(intent);
		dismiss();
	}

	String advertUrl, imgUrl;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		View view = inflater.inflate(R.layout.dialog_advert, container);
		ButterKnife.bind(this, view);
		imgUrl = getArguments().getString("imageUrl");
		advertUrl = getArguments().getString("advertUrl");

		String advertName = imgUrl.substring(imgUrl.lastIndexOf("/")+1, imgUrl.length());
		File file = new File(ExternalCacheDirUtil.getAdvertCacheDir(getActivity()), advertName);
		if (file.exists()){
			Glide.with(this).load(file).centerCrop().into(advert_rv_img);
		}else {
			DrawableRequestBuilder<String> requestBuilder = GlideLoad.init(this);
			GlideLoad.load(new GlideLoad.Builder(requestBuilder, imgUrl)
					.into(advert_rv_img));
		}
		return view;
	}

}
