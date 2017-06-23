package com.cetnaline.findproperty.presenter.impl;

import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.api.ErrorHanding;
import com.cetnaline.findproperty.presenter.BasePresenter;
import com.cetnaline.findproperty.presenter.ui.EstateListContract;

import java.util.Map;

import rx.Subscription;

/**
 * Created by fanxl2 on 2016/8/8.
 */
public class EstateListPresenter extends BasePresenter<EstateListContract.View> implements EstateListContract.Presenter {

	@Override
	public void getEstateList(Map<String, String> params) {
		iView.showLoading();
		Subscription subscription = ApiRequest.getEstateList(params)
				.subscribe(estateList -> {

					iView.dismissLoading();
					iView.setEstateList(estateList);

				}, throwable -> {
					iView.dismissLoading();
					String msg = ErrorHanding.handleError(throwable);
					if ("数据为空".equals(msg)){
						iView.setEstateList(null);
					}else {
						iView.showError(msg);
					}

				});
		addSubscribe(subscription);
	}

	@Override
	public void getEstateByMetro(Map<String, String> params) {
		iView.showLoading();
		Subscription subscription = ApiRequest.getRailWayHouse(params)
				.subscribe(estateList -> {

					iView.dismissLoading();
					iView.setEstateList(estateList);

				}, throwable -> {
					iView.dismissLoading();

					String msg = ErrorHanding.handleError(throwable);
					if ("数据为空".equals(msg)){

					}else {
						iView.showError(msg);
					}

				});
		addSubscribe(subscription);
	}

	@Override
	public void getEstateBySchool(Map<String, String> params) {
		iView.showLoading();
		Subscription subscription = ApiRequest.getEstateBySchool(params)
				.subscribe(estateList -> {

					iView.dismissLoading();
					iView.setEstateList(estateList);

				}, throwable -> {
					iView.dismissLoading();

					String msg = ErrorHanding.handleError(throwable);
					if ("数据为空".equals(msg)){

					}else {
						iView.showError(msg);
					}

				});
		addSubscribe(subscription);
	}
}
