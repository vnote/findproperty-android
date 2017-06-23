package com.cetnaline.findproperty.presenter.impl;

import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.presenter.BasePresenter;
import com.cetnaline.findproperty.presenter.ui.SplashContract;

import rx.Subscription;

/**
 * Created by fanxl2 on 2016/8/8.
 */
public class SplashPresenter extends BasePresenter<SplashContract.View> implements SplashContract.Presenter {

	@Override
	public void getDataVersion() {
		Subscription subscription = ApiRequest.getDataVersion()
				.subscribe(dataVersion -> {
					iView.setDataVersion(dataVersion);
				}, throwable -> {
					iView.setDataVersion(-1L);
				});
		addSubscribe(subscription);
	}
}
