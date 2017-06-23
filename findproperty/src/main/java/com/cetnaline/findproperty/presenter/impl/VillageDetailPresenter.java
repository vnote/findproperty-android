package com.cetnaline.findproperty.presenter.impl;

import com.cetnaline.findproperty.api.bean.EstateDealPriceBo;
import com.cetnaline.findproperty.api.bean.GscopeDealPriceBo;
import com.cetnaline.findproperty.api.bean.PriceTrendBean;
import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.api.ErrorHanding;
import com.cetnaline.findproperty.presenter.BasePresenter;
import com.cetnaline.findproperty.presenter.ui.VillageDetailContract;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func3;
import rx.schedulers.Schedulers;

/**
 * Created by fanxl2 on 2016/8/1.
 */
public class VillageDetailPresenter extends BasePresenter<VillageDetailContract.View> implements VillageDetailContract.Presenter {


	@Override
	public void getPriceTrend(Map<String, String> gParam, Map<String, String> eParam) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.add(Calendar.MONTH, -1);

		long end = calendar.getTimeInMillis()/1000;

		calendar.add(Calendar.MONTH, -5);
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

		Observable.combineLatest(ApiRequest.getGscopeDeailPrice(gParam), ApiRequest.getEstateDeailPrice(eParam), ApiRequest.getGscopeDeailPrice(cParam)
				, new Func3<List<GscopeDealPriceBo>, List<EstateDealPriceBo>,List<GscopeDealPriceBo>, PriceTrendBean>() {
					@Override
					public PriceTrendBean call(List<GscopeDealPriceBo> gscopeDealPriceBos, List<EstateDealPriceBo> estateDealPriceBos,List<GscopeDealPriceBo> gscopeDealPriceBos2) {

						if ((gscopeDealPriceBos==null || gscopeDealPriceBos.size()==0) && (estateDealPriceBos==null || estateDealPriceBos.size()==0)){
							return null;
						}
						return new PriceTrendBean(gscopeDealPriceBos, estateDealPriceBos,gscopeDealPriceBos2);
					}
				})
				.observeOn(Schedulers.io())
				.subscribeOn(AndroidSchedulers.mainThread())
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
						if (priceTrendBean==null){
							iView.hidePriceTrend();
						}else {
							iView.setPriceTrend(priceTrendBean);
						}

					}
				});
	}

	@Override
	public void getVillageDetail(Map<String, String> params) {
		iView.showLoading();
		Subscription subscription = ApiRequest.getEstateByCode(params)
				.subscribe(estateBo -> {
					iView.dismissLoading();
					iView.setVillageDetail(estateBo);

				}, throwable -> {
					iView.dismissLoading();
					iView.showError(ErrorHanding.handleError(throwable));

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

}
