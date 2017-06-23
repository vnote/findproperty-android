package com.cetnaline.findproperty.presenter.impl;

import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.api.ErrorHanding;
import com.cetnaline.findproperty.presenter.BasePresenter;
import com.cetnaline.findproperty.presenter.ui.SearchContract;
import com.orhanobut.logger.Logger;

import java.util.Map;

import rx.Subscription;

/**
 * Created by sunxl8 on 2016/8/22.
 */

public class SearchPresenter extends BasePresenter<SearchContract.View> implements SearchContract.Presenter{


    @Override
    public void getSeoHotTag() {
        Subscription subscription = ApiRequest.getSeoHotTag()
                .subscribe(seoHotTagBean -> {
                    iView.setSeoHotTag(seoHotTagBean);

                }, throwable -> {
                    iView.showError(ErrorHanding.handleError(throwable));

                });
        addSubscribe(subscription);
    }

    @Override
    public void getSearchTag(Map<String, String> params) {
        Subscription subscription = ApiRequest.getSearchTag(params)
                .subscribe(tagModelResponseList -> {
                    iView.setSearchTag(tagModelResponseList);

                }, throwable -> {
                    String msg = ErrorHanding.handleError(throwable);
                    if (msg.equals("数据为空")){
                        iView.showError("暂无数据");
                    }else {
                        iView.showError(msg);
                    }

                });
        addSubscribe(subscription);
    }

    @Override
    public void getEstateList(String key) {
        Subscription subscription = ApiRequest.getEntrustEstateRequest(key)
                .subscribe(tagModelResponseList -> {
                    iView.setEstateList(tagModelResponseList);

                }, throwable -> {
                    String msg = ErrorHanding.handleError(throwable);
                    if (msg.equals("数据为空")){
                        iView.showError("暂无数据");
                        iView.setEstateList(null);
                    }else {
                        iView.showError(msg);
                    }

                });
        addSubscribe(subscription);
    }

    @Override
    public void wordFrequency(String type, String msg) {
        Subscription subscription = ApiRequest.wordFrequency(type, msg)
                .subscribe(result -> {
                    if (result == 1) {
                        Logger.i("保存词频成功");
                    }
                }, throwable -> {
                    String error = ErrorHanding.handleError(throwable);
                });
        addSubscribe(subscription);
    }
}
