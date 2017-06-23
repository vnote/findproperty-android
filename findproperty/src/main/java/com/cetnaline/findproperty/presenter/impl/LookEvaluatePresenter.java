package com.cetnaline.findproperty.presenter.impl;

import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.api.ErrorHanding;
import com.cetnaline.findproperty.presenter.BasePresenter;
import com.cetnaline.findproperty.presenter.ui.LookEvaluateContract;

import java.util.Map;

import rx.Subscription;

/**
 * Created by fanxl2 on 2016/8/8.
 */
public class LookEvaluatePresenter extends BasePresenter<LookEvaluateContract.View> implements LookEvaluateContract.Presenter {

	@Override
	public void lookAboutComment(Map<String, String> param) {
		iView.showLoading();
		Subscription subscription = ApiRequest.lookAboutComment(param)
				.subscribe(result -> {

					iView.dismissLoading();

					iView.setCommentResult(result==0);

				}, throwable -> {
					iView.dismissLoading();
					iView.showError(ErrorHanding.handleError(throwable));

				});
		addSubscribe(subscription);
	}
}
