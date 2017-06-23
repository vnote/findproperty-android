package com.cetnaline.findproperty.presenter.impl;

import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.api.ErrorHanding;
import com.cetnaline.findproperty.presenter.BasePresenter;
import com.cetnaline.findproperty.presenter.ui.WebContract;
import com.cetnaline.findproperty.ui.fragment.MapFragment;
import com.orhanobut.logger.Logger;

import rx.Subscription;

/**
 * Created by fanxl2 on 2016/7/22.
 */
public class WebPresenter extends BasePresenter<WebContract.View> implements WebContract.Presenter {

	@Override
	public void getHouseByAdsNo(String adsNo) {
		iView.showLoading();
		Subscription subscription = ApiRequest.getHouseByAdsNo(adsNo)
				.subscribe(houseDetailBo -> {

					iView.dismissLoading();

					iView.setHouse(houseDetailBo.getPostId(), houseDetailBo.getPostType().equalsIgnoreCase("S")? MapFragment.HOUSE_TYPE_SECOND:MapFragment.HOUSE_TYPE_RENT);

				}, throwable -> {
					iView.dismissLoading();
					iView.showError(ErrorHanding.handleError(throwable));
				});
		addSubscribe(subscription);

	}

	@Override
	public void getStaffInfo(String staffNo) {
		iView.showLoading();
		Subscription subscription = ApiRequest.getStaffDetail(staffNo)
				.subscribe(staffDetail -> {
					iView.dismissLoading();

					iView.setStaff(staffDetail.getStaffNo(), staffDetail.getCnName());

				}, throwable -> {
					iView.dismissLoading();
					iView.showError(ErrorHanding.handleError(throwable));

				});
		addSubscribe(subscription);
	}

	@Override
	public void updateShareStateRequest(String id) {
		Subscription subscription = ApiRequest.updateShareStateRequest(id)
				.subscribe(b -> {
						if (b) {
							Logger.i("分享-服务器更新成功");
						} else {
							Logger.i("分享-服务器更新失败");
						}
				}, throwable -> {
					throwable.printStackTrace();
				});
		addSubscribe(subscription);
	}
}
