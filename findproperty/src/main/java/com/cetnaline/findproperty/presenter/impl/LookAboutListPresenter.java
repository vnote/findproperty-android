package com.cetnaline.findproperty.presenter.impl;

import com.cetnaline.findproperty.api.bean.BatchCollectRequest;
import com.cetnaline.findproperty.api.bean.HouseBo;
import com.cetnaline.findproperty.api.bean.LookAboutBean;
import com.cetnaline.findproperty.api.bean.LookAboutListDetailBo;
import com.cetnaline.findproperty.api.bean.LookListDeleteRequest;
import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.entity.event.NormalEvent;
import com.cetnaline.findproperty.api.ErrorHanding;
import com.cetnaline.findproperty.api.ServerException;
import com.cetnaline.findproperty.presenter.BasePresenter;
import com.cetnaline.findproperty.presenter.ui.LookAboutListContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.rong.eventbus.EventBus;
import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;

/**
 * Created by fanxl2 on 2016/8/8.
 */
public class LookAboutListPresenter extends BasePresenter<LookAboutListContract.View> implements LookAboutListContract.Presenter {

	@Override
	public void getLookAboutList(String userId, int status) {
		iView.showLoading();

//		Subscription subscription = ApiRequest.getLookAboutList(userId, status)
//				.subscribe(lookAboutList -> {
//					iView.dismissLoading();
//
////					iView.setLookAboutList(lookAboutList);
//
//				}, throwable -> {
//					iView.dismissLoading();
//					String error = ErrorHanding.handleError(throwable);
//					if (error.equals("数据为空")){
//						iView.setLookAboutList(null);
//					}else {
//						iView.showError(error);
//					}
//
//				});
//		addSubscribe(subscription);




		Subscription sub = ApiRequest.getLookAboutList(userId, status, 1, 1000)
				.flatMap(new Func1<List<LookAboutBean>, Observable<List<LookAboutListDetailBo>>>() {

					@Override
					public Observable<List<LookAboutListDetailBo>> call(List<LookAboutBean> lookAboutBeans) {

						if (lookAboutBeans!=null && lookAboutBeans.size()>0){

							StringBuffer ids = new StringBuffer();
							for (LookAboutBean bean : lookAboutBeans){
								ids.append(bean.getPostID()).append(",");
							}

							Map<String, String> params = new HashMap<>();
							params.put("PostIds", ids.toString());
							params.put("ImageWidth", "400");
							params.put("ImageHeight", "300");

							return ApiRequest.getHouseDetailList(params)
									.flatMap(new Func1<List<HouseBo>, Observable<List<LookAboutListDetailBo>>>() {

										@Override
										public Observable<List<LookAboutListDetailBo>> call(List<HouseBo> houseBos) {
											List<LookAboutListDetailBo> results = new ArrayList<>();

											for (LookAboutBean bean : lookAboutBeans){

												LookAboutListDetailBo item = new LookAboutListDetailBo();
												item.setPostID(bean.getPostID());
												item.setListID(bean.getListID());
												item.setStatus(bean.getStatus());
												item.setLookTime(bean.getLookTime());
												item.setPlanID(bean.getPlanID());
												item.setPlanCode(bean.getPlanCode());
												item.setStaffNo(bean.getLookStaff());

												for (HouseBo houseBo : houseBos){
													if (houseBo.getPostId().equals(bean.getPostID())){
														item.setEstateCode(houseBo.getEstateCode());
														item.setEstateName(houseBo.getEstateName());
														item.setDirection(houseBo.getDirection());
														item.setGArea(houseBo.getGArea());
														item.setHallCount(houseBo.getHallCount());
														item.setIsOnline(houseBo.isIsOnline());
														item.setPostType(houseBo.getPostType());
														item.setDefaultImage(houseBo.getDefaultImage());
														item.setRentPrice(houseBo.getRentPrice());
														item.setRoomCount(houseBo.getRoomCount());
														item.setSalePrice(houseBo.getSalePrice());
														item.setTitle(houseBo.getTitle());
														item.setStaffNo(houseBo.getStaffNo());
														break;
													}
												}
												results.add(item);
											}
											return Observable.just(results);
										}
									});
						}
						return null;
					}
				})
				.subscribe(lookAboutList -> {
					iView.dismissLoading();
					iView.setLookAboutList(lookAboutList);

				}, throwable -> {
					iView.dismissLoading();
					String error = ErrorHanding.handleError(throwable);
					if (error.equals("数据为空")){
						iView.setLookAboutList(null);
					}else {
						iView.showError(error);
					}
				});

		addSubscribe(sub);

	}

	@Override
	public void deleteLookAboutList(LookListDeleteRequest request) {
		iView.showLoading();
		Subscription subscription = ApiRequest.deleteLookAboutList(request)
				.subscribe(result -> {

					iView.dismissLoading();
					iView.showNoData(false);
					iView.deleteLookAboutResult(result==0);
					EventBus.getDefault().post(new NormalEvent(NormalEvent.ORDER_UPDATE));
				}, throwable -> {
					iView.dismissLoading();
					iView.showNoData(true);
					iView.showError(ErrorHanding.handleError(throwable));

				});
		addSubscribe(subscription);


		ApiRequest.deleteLookAboutList(request)
				.flatMap(new Func1<Integer, Observable<Boolean>>() {

					@Override
					public Observable<Boolean> call(Integer integer) {
						if (integer==0){
							return ApiRequest.deleteIntent(123);
						}else {



							ApiRequest.insertIntent(null)
									.subscribe(token ->{



									}, throwable -> {


									});



							return Observable.error(new ServerException("token过期"));
						}
					}
				})
				.subscribe(isSuccess -> {

				}, throwable -> {

					String error = ErrorHanding.handleError(throwable);
					if (error.equals("token过期")){

					}
				});

	}

	@Override
	public void getLookedAboutNumber(String userId, int status) {
		iView.showLoading();
		Subscription subscription = ApiRequest.getLookedAboutNumber(userId, status)
				.subscribe(numBo -> {

					iView.dismissLoading();
					iView.setLookedAboutNumber(numBo.getStatusCount());

				}, throwable -> {
					iView.dismissLoading();

					String msg = ErrorHanding.handleError(throwable);
					if (msg.equals("数据为空")){
						iView.setLookedAboutNumber(0);
					}else {
						iView.showError(msg);
					}
				});
		addSubscribe(subscription);
	}

	@Override
	public void insertCollectBatch(BatchCollectRequest request) {
		iView.showLoading();
		Subscription subscription = ApiRequest.insertCollectBatch(request)
				.subscribe(number -> {

					iView.dismissLoading();
					iView.setCollectResult(true);

				}, throwable -> {
					iView.dismissLoading();
					iView.showError(ErrorHanding.handleError(throwable));

				});
		addSubscribe(subscription);
	}
}
