package com.cetnaline.findproperty.presenter.impl;

import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.api.ErrorHanding;
import com.cetnaline.findproperty.presenter.BasePresenter;
import com.cetnaline.findproperty.presenter.ui.ScanContract;
import com.cetnaline.findproperty.ui.fragment.MapFragment;

import rx.Subscription;

/**
 * Created by fanxl2 on 2016/7/22.
 */
public class ScanPresenter extends BasePresenter<ScanContract.View> implements ScanContract.Presenter {

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
}
