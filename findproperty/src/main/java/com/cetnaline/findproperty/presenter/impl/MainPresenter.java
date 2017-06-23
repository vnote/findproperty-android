package com.cetnaline.findproperty.presenter.impl;

import android.text.TextUtils;

import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.api.ErrorHanding;
import com.cetnaline.findproperty.presenter.BasePresenter;
import com.cetnaline.findproperty.presenter.ui.MainContract;
import com.orhanobut.logger.Logger;


import rx.Subscription;

/**
 * MainPresenter最终的实现类，处理 Main的所有逻辑
 * Created by fanxl2 on 2016/7/22.
 */
public class MainPresenter extends BasePresenter<MainContract.View> implements MainContract.Presenter {

	@Override
	public void getSchoolList(String regionId) {
		iView.showSchoolLoading(true);
		Subscription subscription = ApiRequest.getSchoolList(regionId)
				.subscribe(schoolBoList -> {
					iView.setSchoolList(schoolBoList);
				}, throwable -> {

					String msg = ErrorHanding.handleError(throwable);
					if (msg.equals("数据为空")){
						iView.setSchoolList(null);
						iView.showError("该区域暂无学校");
					}else {
						iView.showError(msg);
						iView.showSchoolLoading(false);
					}
				});
		addSubscribe(subscription);
	}

	@Override
	public void getAppVersion() {
		Subscription subscription = ApiRequest.getAppVersion()
				.subscribe(appUpdateBo -> {

					iView.setAppVersion(appUpdateBo);

				}, throwable -> {
					String msg = ErrorHanding.handleError(throwable);
					Logger.e(msg);
				});
		addSubscribe(subscription);
	}

	@Override
	public void getAppAdvert() {
		Subscription subscription = ApiRequest.getAppAdvert()
				.subscribe(appAdBo -> {

					if (appAdBo!=null && (TextUtils.isEmpty(appAdBo.getAdvertUrl()) || TextUtils.isEmpty(appAdBo.getImgUrl()))){
						iView.setAppAdvert(null);
					}else {
						iView.setAppAdvert(appAdBo);
					}
				}, throwable -> {
					String msg = ErrorHanding.handleError(throwable);
					if ("网络连接失败，请检查网络!".equals(msg)){

					}else {
						iView.showError(msg);
					}
				});
		addSubscribe(subscription);
	}
}
