package com.cetnaline.findproperty.presenter.impl;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;

import com.cetnaline.findproperty.api.bean.EstateDealPriceBo;
import com.cetnaline.findproperty.api.bean.GscopeDealPriceBo;
import com.cetnaline.findproperty.api.bean.NewStaffBo;
import com.cetnaline.findproperty.api.bean.PriceTrendBean;
import com.cetnaline.findproperty.api.bean.StaffComment;
import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.entity.event.NormalEvent;
import com.cetnaline.findproperty.api.ErrorHanding;
import com.cetnaline.findproperty.api.NetContents;
import com.cetnaline.findproperty.presenter.BasePresenter;
import com.cetnaline.findproperty.presenter.ui.HouseDetailContract;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.rong.eventbus.EventBus;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func3;

/**
 * Created by fanxl2 on 2016/8/1.
 */
public class HouseDetailPresenter extends BasePresenter<HouseDetailContract.View> implements HouseDetailContract.Presenter {


	@Override
	public void getHouseDetail(Map<String, String> params) {
		iView.showLoading();
		Subscription subscription = ApiRequest.getHouseDetailData(params)
				.subscribe(houseDetail -> {
					iView.dismissLoading();
					iView.setHouseDetail(houseDetail);

				}, throwable -> {
					iView.dismissLoading();
					String msg = ErrorHanding.handleError(throwable);
					if ("数据为空".equals(msg)){
						iView.noHouse();
					}else {
						iView.showError(msg);
					}

				});
		addSubscribe(subscription);
	}

	@Override
	public void getStaffComment(String adsNo, String staffNo) {
		iView.showLoading();
		Subscription subscription = ApiRequest.getStaffComments(adsNo, staffNo)
				.subscribe(staffComments -> {
					iView.dismissLoading();
					iView.setStaffComment(staffComments);

				}, throwable -> {
					iView.dismissLoading();
					String msg = ErrorHanding.handleError(throwable);
					if (msg.equals("数据为空")){
						iView.setStaffComment(null);
					}else {
						iView.showError(msg);
					}

				});
		addSubscribe(subscription);
	}

	@Override
	public void getNewHouseDetail(String estExtId) {
		iView.showLoading();
		Subscription subscription = ApiRequest.getNewHouseDetail(estExtId)
				.subscribe(newHouseDetail -> {
					iView.dismissLoading();
					iView.setNewHouseDetail(newHouseDetail);

				}, throwable -> {
					iView.dismissLoading();
					iView.showError(ErrorHanding.handleError(throwable));

				});
		addSubscribe(subscription);
	}

	@Override
	public void getNearbyHouse(Map<String, String> params) {
		Subscription subscription = ApiRequest.getNearbyHouse(params)
				.subscribe(houseBos -> {

					iView.setNearbyHouse(houseBos);

				}, throwable -> {
					iView.dismissLoading();
					iView.setNearbyHouse(null);
//					iView.showError(ErrorHanding.handleError(throwable));

				});
		addSubscribe(subscription);
	}

	@Override
	public void getSaleApartments(String estExtId) {
		Subscription subscription = ApiRequest.getApartments(estExtId)
				.subscribe(apartmentBos -> {

					iView.setSaleApartments(apartmentBos);

				}, throwable -> {
					iView.dismissLoading();
					iView.setSaleApartments(null);
					iView.showError(ErrorHanding.handleError(throwable));

				});
		addSubscribe(subscription);
	}

	@Override
	public void getHouseImageList(String postId, int imgW, int imgH) {
		Subscription subscription = ApiRequest.getHouseImageList(postId, imgW, imgH)
				.subscribe(imageBos -> {

					iView.setHouseImageList(imageBos);

				}, throwable -> {
					iView.setHouseImageList(null);
					iView.dismissLoading();
//					iView.showError(ErrorHanding.handleError(throwable));

				});
		addSubscribe(subscription);
	}

	@Override
	public void getExerciseList(String estExtId) {
		Subscription subscription = ApiRequest.getExerciseList(estExtId)
				.subscribe(exerciseListBos -> {

					iView.setExercises(exerciseListBos);

				}, throwable -> {
					iView.dismissLoading();

					String msg = ErrorHanding.handleError(throwable);
					if (msg.equals("数据为空")){
						iView.setExercises(null);
					}else {
						iView.showError(msg);
					}
				});
		addSubscribe(subscription);
	}

	@Override
	public void getNewHouseImage(String appvalue) {
		Subscription subscription = ApiRequest.getNewHouseImages(appvalue)
				.subscribe(imageBos -> {

					iView.setNewHouseImage(imageBos);

				}, throwable -> {
					iView.dismissLoading();
					iView.showError(ErrorHanding.handleError(throwable));

				});
		addSubscribe(subscription);
	}

	@Override
	public void getPriceTrend(Map<String, String> gParam, Map<String, String> eParam) {

		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.add(Calendar.MONTH, -1);

		long end = calendar.getTimeInMillis()/1000;

		calendar.add(Calendar.MONTH, -6);
		long begin = calendar.getTimeInMillis()/1000;

		eParam.put("PostType", "S");
		eParam.put("DealTimeBegin", begin+"");
		eParam.put("DealTimeEnd", end+"");

		gParam.put("DealTimeBegin", begin+"");
		gParam.put("DealTimeEnd", end+"");
		gParam.put("PostType", "S");

		//上海市房价趋势
		Map<String,String> cParam = new HashMap<>();
		cParam.put("DealTimeBegin", begin+"");
		cParam.put("DealTimeEnd", end+"");
		cParam.put("RegionId", "021");
		cParam.put("PostType", "S");

		Subscription subscription = Observable.combineLatest(ApiRequest.getGscopeDeailPrice(gParam), ApiRequest.getEstateDeailPrice(eParam), ApiRequest.getGscopeDeailPrice(cParam)
				, new Func3<List<GscopeDealPriceBo>, List<EstateDealPriceBo>, List<GscopeDealPriceBo>, PriceTrendBean>() {
					@Override
					public PriceTrendBean call(List<GscopeDealPriceBo> gscopeDealPriceBos, List<EstateDealPriceBo> estateDealPriceBos, List<GscopeDealPriceBo> gscopeDealPriceBos2) {
						return new PriceTrendBean(gscopeDealPriceBos, estateDealPriceBos, gscopeDealPriceBos2);  //区域，小区，城市
					}
				})
				.subscribe(new Subscriber<PriceTrendBean>() {
			@Override
			public void onCompleted() {

			}

			@Override
			public void onError(Throwable e) {
				e.printStackTrace();
			}

			@Override
			public void onNext(PriceTrendBean priceTrendBean) {
				iView.setPriceTrend(priceTrendBean);
			}
		});

		addSubscribe(subscription);
	}

	@Override
	public void insertCollect(Map<String, String> params) {
		iView.showLoading();
		Subscription subscription = ApiRequest.insertCollect(params)
				.subscribe(collectId -> {

					iView.dismissLoading();
					iView.setCollectResult(collectId);

				}, throwable -> {
					iView.dismissLoading();
					iView.showError(ErrorHanding.handleError(throwable));

				});
		addSubscribe(subscription);
	}

	@Override
	public void insertReservation(Map<String, String> params) {
		iView.showLoading();
		Subscription subscription = ApiRequest.insertReservation(params)
				.subscribe(result -> {

					iView.dismissLoading();
					iView.setReservationResult(result==0);
					EventBus.getDefault().post(new NormalEvent(NormalEvent.ORDER_UPDATE));
				}, throwable -> {
					iView.dismissLoading();
					iView.showError(ErrorHanding.handleError(throwable));

				});
		addSubscribe(subscription);
	}

	@Override
	public void checkCollect(String userId, String postId) {
		Subscription subscription = ApiRequest.checkCollect(userId, postId)
				.subscribe(collectId -> {

					iView.isCollected(collectId);

				}, throwable -> {
					iView.showError(ErrorHanding.handleError(throwable));

				});
		addSubscribe(subscription);
	}

	@Override
	public void deleteCollect(long collectId) {
		iView.showLoading();
		Subscription subscription = ApiRequest.deleteCollect(collectId)
				.subscribe(result -> {

					iView.dismissLoading();
					iView.deleteCollectResult(result==0);

				}, throwable -> {
					iView.dismissLoading();
					iView.showError(ErrorHanding.handleError(throwable));

				});
		addSubscribe(subscription);
	}

	@Override
	public void getLookedAboutNumber(String userId, int status) {
		Subscription subscription = ApiRequest.getLookedAboutNumber(userId, status)
				.subscribe(numBo -> {

					iView.setLookedAboutNumber(numBo.getStatusCount());

				}, throwable -> {

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
	public void getStaffDetail(String staffNo) {
		Subscription subscription = ApiRequest.getStaffDetail(staffNo)
				.subscribe(staffDetail -> {

					iView.setStaffDetail(staffDetail);

				}, throwable -> {
					iView.showError(ErrorHanding.handleError(throwable));

				});
		addSubscribe(subscription);
	}

	@Override
	public void getNewHouseStaff(String estExtId) {
		iView.showLoading();
		Subscription subscription = ApiRequest.getNewHouseStaff(estExtId)
				.subscribe(staffBos -> {

					iView.dismissLoading();
					if (staffBos==null || staffBos.size()==0){
						iView.setNewHouseStaff(null);
					}else {
						NewStaffBo item = staffBos.get(0);
						StaffComment staffComment = new StaffComment();
						staffComment.setStaffName(item.getCnName());
						staffComment.setStaff400Tel(item.getStaff400Tel());
						staffComment.setStaffNo(item.getStaffNo());
						staffComment.setPostId(item.getEstExtId());
						staffComment.setStaffImage(NetContents.STAFF_HEAD_HOST+item.getStaffNo()+".jpg");

						iView.setNewHouseStaff(staffComment);
					}

				}, throwable -> {
					iView.dismissLoading();
					iView.showError(ErrorHanding.handleError(throwable));

				});
		addSubscribe(subscription);
	}


	public void callPhone(Context context){
		Intent call = new Intent(Intent.ACTION_CALL, Uri.parse("tel:02151787380"));
		if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
			iView.showMsg("权限被拒绝，不能拨打电话!");
			return;
		}
		context.startActivity(call);
	}


}
