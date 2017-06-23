package com.cetnaline.findproperty.presenter.impl;

import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.entity.ui.DeputePushBean;
import com.cetnaline.findproperty.presenter.BasePresenter;
import com.cetnaline.findproperty.presenter.ui.DeputeCustomerContract;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscription;

/**
 * Created by diaoqf on 2017/3/31.
 */

public class DeputeCustomerPresenter extends BasePresenter<DeputeCustomerContract.View> implements DeputeCustomerContract.Presenter {
    @Override
    public void savePic(List<String> files) {

    }

    @Override
    public void getHouseById(String houseId, String type) {
        Map<String, String> params = new HashMap<>();
        params.put("houseid", houseId);
        params.put("EntrustType", type);
        addSubscribe(ApiRequest.getEnstrustByIDRequest(params)
                .subscribe(deputeBean -> {
                    if (deputeBean != null) {
                        iView.setPublishedDepute(deputeBean);
                    } else {
                        iView.setPublishedDepute(null);
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    iView.setPublishedDepute(null);
                }));
    }

    @Override
    public void submitServer(DeputePushBean deputePushBean) {
        addSubscribe(ApiRequest.insertEntrustInfo(deputePushBean).subscribe(aLong -> {
            iView.dismissLoading();
            iView.finishForm(aLong);
        },throwable -> {
            iView.dismissLoading();
            iView.finishForm(-1);
            throwable.printStackTrace();
        }));
    }

    @Override
    public void updateFormState(String id) {
        Map<String , String> params = new HashMap<>();
        params.put("EntrustID", id);
        params.put("Status","-1");
        Subscription subscription = ApiRequest.updateEntrustStutasRequest(params)
                .subscribe(result -> {
                    iView.showError("委托已取消");
                    iView.corePush(false);
                }, throwable -> {
                    iView.showError("取消委托失败");
                    iView.dismissLoading();
                });
        addSubscribe(subscription);
    }

}
