package com.cetnaline.findproperty.presenter.impl;

import com.cetnaline.findproperty.api.bean.ApiResponse;
import com.cetnaline.findproperty.api.bean.HouseBo;
import com.cetnaline.findproperty.api.bean.LookAboutBean;
import com.cetnaline.findproperty.api.bean.LookAboutListDetailBo;
import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.api.ErrorHanding;
import com.cetnaline.findproperty.api.ServerException;
import com.cetnaline.findproperty.presenter.BasePresenter;
import com.cetnaline.findproperty.presenter.ui.ToLookAboutContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;

/**
 * Created by fanxl2 on 2016/8/8.
 */
public class ToLookAboutPresenter extends BasePresenter<ToLookAboutContract.View> implements ToLookAboutContract.Presenter {

	@Override
	public void getLookAboutList(String userId, int status, int pageIndex, int pageSize) {
		iView.showLoading();

		Subscription sub = ApiRequest.getLookAboutListAll(userId, status, pageIndex, pageSize)
				.flatMap(new Func1<ApiResponse<List<LookAboutBean>>, Observable<List<LookAboutListDetailBo>>>() {

					@Override
					public Observable<List<LookAboutListDetailBo>> call(ApiResponse<List<LookAboutBean>> apiResponse) {

						List<LookAboutBean> lookAboutBeans = apiResponse.getResult();
						iView.setTotalNumber(apiResponse.getTotal());

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
													if (houseBo.getPostId().equalsIgnoreCase(bean.getPostID())){
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
														break;
													}
												}
												results.add(item);
											}
											return Observable.just(results);
										}
									});
						}else {
							return Observable.error(new ServerException(apiResponse.getMessage()+""));
						}
					}
				})
				.subscribe(lookAboutList -> {

					iView.dismissLoading();
					iView.setLookAboutList(lookAboutList);
//					iView.showNoData(false);

				}, throwable -> {
					iView.dismissLoading();

					String error = ErrorHanding.handleError(throwable);
					if (error.equals("数据为空")){
//						iView.showNoData(true);
						iView.setLookAboutList(new ArrayList<>());
					}else {
						iView.showError(error);
					}
				});

		addSubscribe(sub);


	}
}
