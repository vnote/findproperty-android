package com.cetnaline.findproperty.presenter.impl;

import com.cetnaline.findproperty.api.bean.InsertIntentionsRequest;
import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.api.ErrorHanding;
import com.cetnaline.findproperty.presenter.BasePresenter;
import com.cetnaline.findproperty.presenter.ui.HouseListContract;

import java.util.Map;

import rx.Subscription;

/**
 * Created by fanxl2 on 2016/8/8.
 */
public class HouseListPresenter extends BasePresenter<HouseListContract.View> implements HouseListContract.Presenter {

	@Override
	public void getHouseList4AllResult(Map<String, String> params) {
		Subscription subscription = ApiRequest.getHouseList4AllResult(params)
				.subscribe(listApiResponse -> {

					if (listApiResponse.isSuccess()){
						iView.setHouseList(listApiResponse.getResult(), listApiResponse.getTotal());
					}else {

						String msg = listApiResponse.getMessage();
						if (msg.equals("数据为空")){
							iView.setHouseList(null, 0);
						}else {
							iView.showError(msg);
							iView.stopRefresh();
						}
					}
				}, throwable -> {

					String msg = ErrorHanding.handleError(throwable);
					if (msg.equals("网络连接异常，请检查网络连接")){
						iView.netWorkException();
					}else {
						iView.stopRefresh();
					}

					iView.showError(msg);
				});
		addSubscribe(subscription);
	}

	@Override
	public void getHouseBySchool4AllResult(Map<String, String> params) {
		Subscription subscription = ApiRequest.getHouseBySchool4AllResult(params)
				.subscribe(listApiResponse -> {

					if (listApiResponse.isSuccess()){
						iView.setHouseList(listApiResponse.getResult(), listApiResponse.getTotal());
					}else {
						String msg = listApiResponse.getMessage();
						if (msg.equals("数据为空")){
							iView.setHouseList(null, 0);
						}else {
							iView.showError(msg);
							iView.stopRefresh();
						}
					}
				}, throwable -> {
					iView.showError(ErrorHanding.handleError(throwable));
					iView.stopRefresh();

				});
		addSubscribe(subscription);
	}

	@Override
	public void getHouseByMetroAllResult(Map<String, String> params) {
		Subscription subscription = ApiRequest.getHouseByMetroAllResult(params)
				.subscribe(listApiResponse -> {

					if (listApiResponse.isSuccess()){
						iView.setHouseList(listApiResponse.getResult(), listApiResponse.getTotal());
					}else {

						String msg = listApiResponse.getMessage();
						if (msg.equals("数据为空")){
							iView.setHouseList(null, 0);
						}else {
							iView.showError(msg);
							iView.stopRefresh();
						}
					}
				}, throwable -> {
					iView.showError(ErrorHanding.handleError(throwable));
					iView.stopRefresh();

				});
		addSubscribe(subscription);
	}

	@Override
	public void getNewHouseList(Map<String, String> params) {

		int index = Integer.parseInt(params.get("StartIndex")+"");
		params.put("StartIndex", index*10+"");

		Subscription subscription = ApiRequest.getNewHouse4AllResult(params)
				.subscribe(listApiResponse -> {

					if (listApiResponse.isSuccess()){
						iView.setNewHouseList(listApiResponse.getResult(), listApiResponse.getTotal());
					}else {
						String msg = listApiResponse.getMessage();
						if (msg.equals("数据为空")){
							iView.setNewHouseList(null, 0);
						}else {
							iView.showError(msg);
							iView.stopRefresh();
						}
					}
				}, throwable -> {

					iView.showError(ErrorHanding.handleError(throwable));
					iView.stopRefresh();

				});
		addSubscribe(subscription);
	}

	@Override
	public void getNearbyHouse(Map<String, String> params) {
		Subscription subscription = ApiRequest.getNearbyHouseAllResult(params)
				.subscribe(listApiResponse -> {

					if (listApiResponse.isSuccess()){
						iView.setHouseList(listApiResponse.getResult(), listApiResponse.getTotal());
					}else {
						String msg = listApiResponse.getMessage();
						if (msg.equals("数据为空")) {
							iView.setHouseList(null, 0);
						}else {
							iView.showError(msg);
							iView.stopRefresh();
						}
					}
				}, throwable -> {

					iView.showError(ErrorHanding.handleError(throwable));
					iView.stopRefresh();

				});
		addSubscribe(subscription);
	}

	@Override
	public void insertIntent(InsertIntentionsRequest request) {
		iView.showLoading();
		Subscription subscription = ApiRequest.insertIntent(request)
				.subscribe(intentId -> {
					iView.dismissLoading();
					iView.setInsertResult(intentId);

				}, throwable -> {
					iView.dismissLoading();
					iView.showError(ErrorHanding.handleError(throwable));
				});
		addSubscribe(subscription);
	}

	@Override
	public void deleteIntent(long intentId) {
		iView.showLoading();
		Subscription subscription = ApiRequest.deleteIntent(intentId)
				.subscribe(result -> {
					iView.dismissLoading();
					iView.setDeleteIntentResult(result);

				}, throwable -> {
					iView.dismissLoading();
					iView.showError(ErrorHanding.handleError(throwable));
				});
		addSubscribe(subscription);
	}
}
