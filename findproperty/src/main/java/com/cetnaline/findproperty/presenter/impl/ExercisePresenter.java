package com.cetnaline.findproperty.presenter.impl;

import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.api.ErrorHanding;
import com.cetnaline.findproperty.presenter.BasePresenter;
import com.cetnaline.findproperty.presenter.ui.ExerciseContract;

import java.util.Map;

import rx.Subscription;

/**
 * Created by fanxl2 on 2016/8/12.
 */
public class ExercisePresenter extends BasePresenter<ExerciseContract.View> implements ExerciseContract.Presenter {

	@Override
	public void getVerifyCode(String phone) {
		Subscription subscription = ApiRequest.getVerifyCode(phone)
				.subscribe(result -> {

					iView.setVerifyCodeResult(result==0);

				}, throwable -> {

					iView.showError(ErrorHanding.handleError(throwable));

				});
		addSubscribe(subscription);
	}

	@Override
	public void verificationCode(String mobile, String code) {
		Subscription subscription = ApiRequest.verificationCode(mobile, code)
				.subscribe(result -> {

					iView.checkVerifyCodeResult(result==0);

				}, throwable -> {

					iView.showError(ErrorHanding.handleError(throwable));

				});
		addSubscribe(subscription);
	}

	@Override
	public void insertUserBooking(Map<String, String> param) {
		iView.showLoading();
		Subscription subscription = ApiRequest.insertUserBooking(param)
				.subscribe(result -> {
					iView.dismissLoading();
					iView.setInsertBookResult(result==0);

				}, throwable -> {
					iView.dismissLoading();
					iView.showError(ErrorHanding.handleError(throwable));

				});
		addSubscribe(subscription);
	}

	@Override
	public void addBookingCount(String actId, int number) {
		iView.showLoading();
		Subscription subscription = ApiRequest.addBookingCount(actId, number)
				.subscribe(result -> {
					iView.dismissLoading();
					iView.setAddBookingResult(true);

				}, throwable -> {
					iView.dismissLoading();
					iView.showError(ErrorHanding.handleError(throwable));

				});
		addSubscribe(subscription);
	}
}
