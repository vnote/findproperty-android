package com.cetnaline.findproperty.presenter.impl;

import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.entity.ui.DeputePushBean;
import com.cetnaline.findproperty.api.ErrorHanding;
import com.cetnaline.findproperty.presenter.BasePresenter;
import com.cetnaline.findproperty.presenter.ui.EntrustInsertContract;

import rx.Subscription;

/**
 * MainPresenter最终的实现类，处理 Main的所有逻辑
 * Created by fanxl2 on 2016/7/22.
 */
public class EntrustInsertPresenter extends BasePresenter<EntrustInsertContract.View> implements EntrustInsertContract.Presenter {

	@Override
	public void insertEntrustInfo(DeputePushBean bean) {
		iView.showLoading();
		Subscription subscription = ApiRequest.insertEntrustInfo(bean)
				.subscribe(entrustID -> {

					iView.dismissLoading();
					iView.insertResult(entrustID);

				}, throwable -> {
					iView.dismissLoading();
					iView.showError(ErrorHanding.handleError(throwable));
				});
		addSubscribe(subscription);
	}

	@Override
	public void getStaff(String estateCode) {
		iView.showLoading();
		Subscription subscription = ApiRequest.getEstateStaffByEstateCode(estateCode)
				.subscribe(staff -> {
					iView.dismissLoading();
					iView.setStaff(staff);
				}, throwable -> {
					iView.dismissLoading();
					iView.showError(ErrorHanding.handleError(throwable));
				});
		addSubscribe(subscription);
	}
}
