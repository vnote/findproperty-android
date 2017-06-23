package com.cetnaline.findproperty.presenter.impl;

import com.cetnaline.findproperty.api.bean.InsertIntentionsRequest;
import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.api.ErrorHanding;
import com.cetnaline.findproperty.presenter.BasePresenter;
import com.cetnaline.findproperty.presenter.ui.IntentContract;

import rx.Subscription;

/**
 * Created by fanxl2 on 2016/7/22.
 */
public class IntentPresenter extends BasePresenter<IntentContract.View> implements IntentContract.Presenter {


	@Override
	public void insertIntent(InsertIntentionsRequest request) {
		iView.showLoading();
		Subscription subscription = ApiRequest.insertIntent(request)
				.subscribe(intentId -> {
					iView.dismissLoading();
					iView.setInsertResult();
				}, throwable -> {
					iView.dismissLoading();
					iView.showError(ErrorHanding.handleError(throwable));
				});
		addSubscribe(subscription);
	}
}
