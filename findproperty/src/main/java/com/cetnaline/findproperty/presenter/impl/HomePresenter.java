package com.cetnaline.findproperty.presenter.impl;

import com.cetnaline.findproperty.api.bean.IntentionBo;
import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.api.ErrorHanding;
import com.cetnaline.findproperty.presenter.BasePresenter;
import com.cetnaline.findproperty.presenter.ui.HomeContract;
import com.cetnaline.findproperty.ui.fragment.MapFragment;

import java.util.Map;

import rx.Subscription;

/**
 * MainPresenter最终的实现类，处理 Main的所有逻辑
 * Created by fanxl2 on 2016/7/22.
 */
public class HomePresenter extends BasePresenter<HomeContract.View> implements HomeContract.Presenter {

	@Override
	public void getIntention4Home(String userId) {
		iView.showLoading();
		Subscription subscription = ApiRequest.getIntention4Home(userId)
				.subscribe(intentionBos -> {
					iView.dismissLoading();
					if (intentionBos.size()>0){
						IntentionBo item = intentionBos.get(0);
						if (item.getSource().equalsIgnoreCase("ershoufang")){
							iView.setIntention(item.getSearchPara(), MapFragment.HOUSE_TYPE_SECOND);
						}else if (item.getSource().equalsIgnoreCase("zufang")){
							iView.setIntention(item.getSearchPara(), MapFragment.HOUSE_TYPE_RENT);
						}else {
							iView.setIntention(item.getSearchPara(), MapFragment.HOUSE_TYPE_NEW);
						}
					}else {
						iView.setIntention(null, MapFragment.HOUSE_TYPE_SECOND);
					}
				}, throwable -> {
					iView.dismissLoading();
					iView.showError(ErrorHanding.handleError(throwable));
				});
		addSubscribe(subscription);
	}

	@Override
	public void getHouseList(Map<String, String> params) {
		Subscription subscription = ApiRequest.getHouseList(params)
				.subscribe(houseList -> {
					iView.setHouseList(houseList);

				}, throwable -> {

					String msg = ErrorHanding.handleError(throwable);
					if (msg.equals("数据为空")){
						iView.setHouseList(null);
					}else {
						iView.showError(msg);
					}

				});
		addSubscribe(subscription);
	}

	@Override
	public void getNewHouseList(Map<String, String> params) {
		Subscription subscription = ApiRequest.getNewHouses(params)
				.subscribe(houseList -> {
					iView.setNewHouseList(houseList);
				}, throwable -> {
					String msg = ErrorHanding.handleError(throwable);
					if (msg.equals("数据为空")){
						iView.setNewHouseList(null);
					}else {
						iView.showError(msg);
					}

				});
		addSubscribe(subscription);
	}

	@Override
	public void getHomeRecommend(double lat, double lng) {
		Subscription subscription = ApiRequest.getHomeRecommend(lat, lng)
				.subscribe(houseList -> {
					iView.setHomeRecommend(houseList);
				}, throwable -> {
					iView.showError(ErrorHanding.handleError(throwable));

				});
		addSubscribe(subscription);
	}

	@Override
	public void getHouseBySchool4AllResult(Map<String, String> params) {
		Subscription subscription = ApiRequest.getHouseBySchool4AllResult(params)
				.subscribe(listApiResponse -> {

					if (listApiResponse.isSuccess()){
						iView.setHouseList(listApiResponse.getResult());
					}else {
						String msg = listApiResponse.getMessage();
						if (msg.equals("数据为空")){
							iView.setHouseList(null);
						}else {
							iView.showError(msg);
						}
					}
				}, throwable -> {
					iView.showError(ErrorHanding.handleError(throwable));
				});
		addSubscribe(subscription);
	}

	@Override
	public void getHouseByMetroAllResult(Map<String, String> params) {
		Subscription subscription = ApiRequest.getHouseByMetroAllResult(params)
				.subscribe(listApiResponse -> {

					if (listApiResponse.isSuccess()){
						iView.setHouseList(listApiResponse.getResult());
					}else {

						String msg = listApiResponse.getMessage();
						if (msg.equals("数据为空")){
							iView.setHouseList(null);
						}else {
							iView.showError(msg);
						}
					}
				}, throwable -> {
					iView.showError(ErrorHanding.handleError(throwable));

				});
		addSubscribe(subscription);
	}

	@Override
	public void getAppXuanFuAdvertRequest() {
		Subscription subscription = ApiRequest.getAppXuanFuAdvertRequest()
				.subscribe(advertBo -> {
					if (advertBo.getAdvertUrl() != null && advertBo.getImgUrl() != null) {
						iView.setAdvert(advertBo);
					}
				}, throwable -> {
					iView.showError(ErrorHanding.handleError(throwable));
				});
		addSubscribe(subscription);
	}
}
