package com.cetnaline.findproperty.presenter.impl;

import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.api.ErrorHanding;
import com.cetnaline.findproperty.presenter.BasePresenter;
import com.cetnaline.findproperty.presenter.ui.DeputeSourceInfoContract;
import com.cetnaline.findproperty.utils.SchedulersCompat;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;

/**
 * Created by diaoqf on 2017/3/27.
 */

public class DeputeSourceInfoPresenter extends BasePresenter<DeputeSourceInfoContract.View> implements DeputeSourceInfoContract.Presenter {
    @Override
    public void getBuildingNos(String no) {
//        iView.showLoading();
        Subscription subscription = ApiRequest.getBulidNUMRequest(no)
                .subscribe(list -> {
                    iView.setBuildingNos(list);
                }, throwable -> {
                    iView.setBuildingNos(null);
                    iView.showError(ErrorHanding.handleError(throwable));
                });
        addSubscribe(subscription);
    }

    @Override
    public void getRoomNos(String no) {
        Subscription subscription = ApiRequest.getBulidRoomNUMRequest(no)
                .subscribe(list -> {
                    iView.setRoomNos(list);
                }, throwable -> {
                    iView.setRoomNos(null);
                    iView.showError(ErrorHanding.handleError(throwable));
                });
        addSubscribe(subscription);
    }

    @Override
    public void getFormdata() {
        addSubscribe(Observable.timer(200, TimeUnit.MILLISECONDS)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(aLong -> {
                    iView.reloadData();
                }));
    }

    @Override
    public void getEvaluateRequest(Map<String, String> params) {
        Subscription subscription = ApiRequest.evaluateRequest(params)
                .subscribe(num -> {
                    iView.setEvaluate(num);
                }, throwable -> {
                    iView.setEvaluate(-1);
                    iView.showError(ErrorHanding.handleError(throwable));
                });
        addSubscribe(subscription);
    }
}
