package com.cetnaline.findproperty.presenter.impl;

import com.cetnaline.findproperty.api.bean.SendAppointmentRequest;
import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.entity.event.NormalEvent;
import com.cetnaline.findproperty.api.ErrorHanding;
import com.cetnaline.findproperty.presenter.BasePresenter;
import com.cetnaline.findproperty.presenter.ui.LookWorkContract;

import io.rong.eventbus.EventBus;
import rx.Subscription;

/**
 * Created by fanxl2 on 2016/8/8.
 */
public class LookWorkPresenter extends BasePresenter<LookWorkContract.View> implements LookWorkContract.Presenter {


	@Override
	public void commitLookAboutList(SendAppointmentRequest request) {

		iView.showLoading();
		Subscription subscription = ApiRequest.commitLookAboutList(request)
				.subscribe(result -> {

					iView.dismissLoading();

					iView.setCommitLookResult(result==0);
					EventBus.getDefault().post(new NormalEvent(NormalEvent.ORDER_UPDATE));
				}, throwable -> {
					iView.dismissLoading();
					iView.showError(ErrorHanding.handleError(throwable));

				});
		addSubscribe(subscription);
	}
}
