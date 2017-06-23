package com.cetnaline.findproperty.presenter.impl;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateInterpolator;

import com.cetnaline.findproperty.api.bean.EstateBo;
import com.cetnaline.findproperty.api.bean.EstateMapRequest;
import com.cetnaline.findproperty.api.bean.SearchParam;
import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.db.entity.DropBo;
import com.cetnaline.findproperty.api.ErrorHanding;
import com.cetnaline.findproperty.presenter.BasePresenter;
import com.cetnaline.findproperty.presenter.ui.MapContract;
import com.cetnaline.findproperty.ui.fragment.MapFragment;
import com.cetnaline.findproperty.utils.DataHolder;
import com.cetnaline.findproperty.utils.MyUtils;
import com.orhanobut.logger.Logger;
import com.tendcloud.tenddata.TCAgent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscription;

/**
 * Created by fanxl2 on 2016/7/25.
 */
public class MapPresenter extends BasePresenter<MapContract.View> implements MapContract.Presenter {

	@Override
	public void getBaseMapData(Map<String, String> params) {
		iView.showLoading();
		Subscription subscription = ApiRequest.getBaseMapData(params)
				.subscribe(regionList -> {
					iView.dismissLoading();
					iView.setBaseMapData(regionList);

				}, throwable -> {
					iView.dismissLoading();
					String msg = ErrorHanding.handleError(throwable);
					if (msg.equals("数据为空")){
						iView.setBaseMapData(null);
						iView.showError("区域内暂无房源");
					}else {
						iView.showError(msg);
					}

				});
		addSubscribe(subscription);
	}

	@Override
	public void getEsfEstateList(Context context, EstateMapRequest request) {
		iView.showLoading();
		Subscription subscription = ApiRequest.getEsf4Map(request)
				.subscribe(esfEstateDoList -> {

					Map<String, String> attributes = new HashMap<>();

					if (request.getPostFilter().getDrawCircle() != null && "".equals(request.getPostFilter().getDrawCircle())) {
						//百度统计，圈画次数
						attributes.put("user_id", DataHolder.getInstance().getUserId());
						attributes.put("device_id", MyUtils.getDeviceId(context));
						TCAgent.onEvent(context,"圈画搜索","",attributes);
//						StatService.onEvent(context, AppContents.DRAW_SEARCH_COUNTER, "draw_search", 1, attributes);
					} else {
						//百度统计，地铁搜索次数
						attributes.put("user_id", DataHolder.getInstance().getUserId());
						attributes.put("device_id", MyUtils.getDeviceId(context));
						TCAgent.onEvent(context,"地铁搜索","",attributes);
//						StatService.onEvent(context, AppContents.SUBWAY_SEARCH_COUNTER, "subway_search", 1, attributes);
					}


					iView.dismissLoading();
					iView.setEsfEstateData(esfEstateDoList);

				}, throwable -> {
					iView.dismissLoading();
					String msg = ErrorHanding.handleError(throwable);
					if (msg.equals("数据为空")){
						iView.noData();
						iView.showError("区域内暂无房源");
					}else {
						iView.showError(msg);
					}
				});
		addSubscribe(subscription);
	}

	@Override
	public void getHouseList(Map<String, String> params) {
		iView.showProgress();
		Subscription subscription = ApiRequest.getHouseList(params)
				.subscribe(postDoList -> {
					iView.dismissLoading();
					iView.setHouseList(postDoList);

				}, throwable -> {
					iView.dismissLoading();
					iView.showError(ErrorHanding.handleError(throwable));

				});
		addSubscribe(subscription);
	}

	@Override
	public void getEstByCircle(Context context, Map<String, String> params) {
		iView.showLoading();
		Subscription subscription = ApiRequest.getEstByCircle(params)
				.subscribe(esfEstateDoList -> {
					iView.dismissLoading();

					//百度统计，圈画次数
					Map<String, String> attributes = new HashMap<>();
					attributes.put("user_id", DataHolder.getInstance().getUserId());
					attributes.put("device_id", MyUtils.getDeviceId(context));
					TCAgent.onEvent(context,"圈画搜索","",attributes);
//					StatService.onEvent(context, AppContents.DRAW_SEARCH_COUNTER, "draw_search", 1, attributes);


					if (esfEstateDoList==null){
						iView.noData();
						iView.showError("圈画范围内暂无房源");
					}else {
						iView.setEstByCircle(esfEstateDoList);
					}

				}, throwable -> {
					iView.dismissLoading();
					String msg = ErrorHanding.handleError(throwable);
					if (msg.equals("数据为空")){
						iView.noData();
						iView.showError("圈画范围内暂无房源");
					}else {
						iView.showError(msg);
					}

				});
		addSubscribe(subscription);
	}

	@Override
	public void getNewEstByCircle(Context context, EstateMapRequest request) {
		iView.showLoading();
		Subscription subscription = ApiRequest.getEsf4Map(request)
				.subscribe(esfEstateDoList -> {

					iView.dismissLoading();

					//百度统计，圈画次数
					Map<String, String> attributes = new HashMap<>();
					attributes.put("user_id", DataHolder.getInstance().getUserId());
					attributes.put("device_id", MyUtils.getDeviceId(context));
					TCAgent.onEvent(context,"圈画搜索","",attributes);
//					StatService.onEvent(context, AppContents.DRAW_SEARCH_COUNTER, "draw_search", 1, attributes);

					if (esfEstateDoList==null){
						iView.noData();
						iView.showError("圈画范围内暂无房源");
					}
					iView.setEstByCircle(esfEstateDoList);

				}, throwable -> {
					iView.dismissLoading();
					String msg = ErrorHanding.handleError(throwable);
					if (msg.equals("数据为空")){
						iView.noData();
						iView.showError("圈画范围内暂无房源");
						iView.setEstByCircle(null);
					}else {
						iView.showError(msg);
					}

				});
		addSubscribe(subscription);
	}

	@Override
	public void getHouseByRailWay(Context context, Map<String, String> params) {
		iView.showLoading();
		params.put("ImageWidth", "10");
		params.put("ImageHeight", "10");
		Subscription subscription = ApiRequest.getRailWayHouse(params)
				.subscribe(esfEstateDoList -> {
					iView.dismissLoading();

					//百度统计，地铁搜索次数
					Map<String, String> attributes = new HashMap<>();
					attributes.put("user_id", DataHolder.getInstance().getUserId());
					attributes.put("device_id", MyUtils.getDeviceId(context));
					TCAgent.onEvent(context,"地铁搜索","",attributes);
//					StatService.onEvent(context, AppContents.SUBWAY_SEARCH_COUNTER, "subway_search", 1, attributes);

					if (esfEstateDoList==null){
						iView.noData();
						iView.showError("地铁周边暂无房源");
					}else {
						iView.setRailWayEsfList(esfEstateDoList, params.get("RailWayId"));
					}

				}, throwable -> {
					iView.dismissLoading();

					String msg = ErrorHanding.handleError(throwable);
					if (msg.equals("数据为空")){
						iView.setRailWayEsfList(null, null);
					}else {
						iView.showError(msg);
					}
				});
		addSubscribe(subscription);
	}

	@Override
	public void getNewHouseBaseMapData(Map<String, String> params) {
		iView.showLoading();
		Subscription subscription = ApiRequest.getNewHouseList(params)
				.subscribe(regionList -> {
					iView.dismissLoading();
					iView.setNewHouseBaseMapData(regionList);

				}, throwable -> {
					iView.dismissLoading();
					String msg = ErrorHanding.handleError(throwable);
					if (msg.equals("数据为空")){
						iView.noData();
						iView.showError("区域内暂无房源");
					}else {
						iView.showError(msg);
					}
				});
		addSubscribe(subscription);
	}

	@Override
	public void getEstBySchoolId(Context context, String schoolId) {
		iView.showLoading();
		Subscription subscription = ApiRequest.getEstBySchoolId(schoolId)
				.subscribe(schoolBo -> {
					iView.dismissLoading();

					//百度统计，学校搜索次数
					Map<String, String> attributes = new HashMap<>();
					attributes.put("user_id", DataHolder.getInstance().getUserId());
					attributes.put("device_id", MyUtils.getDeviceId(context));
					TCAgent.onEvent(context,"学校搜索","",attributes);
//					StatService.onEvent(context, AppContents.SCHOOL_SEARCH_COUNTER, "school_search", 1, attributes);

					if (schoolBo.getSchoolToEstateInfo()!=null){
						List<EstateBo> estatesBeen = schoolBo.getSchoolToEstateInfo().getEstates();
						iView.setEstBySchoolId(estatesBeen, schoolBo);
					}else {
						iView.showError("学校周边暂无房源");
					}
				}, throwable -> {
					iView.dismissLoading();
					iView.showError(ErrorHanding.handleError(throwable));

				});
		addSubscribe(subscription);
	}

	@Override
	public void startAnimation(View view, boolean isHide) {
		ObjectAnimator alphaObject, translationYObject;
		if (isHide){
			alphaObject = ObjectAnimator.ofFloat(view, "alpha", 1, 0);
			translationYObject = ObjectAnimator.ofFloat(view, "translationY", 0, -300);
		}else {
			alphaObject = ObjectAnimator.ofFloat(view, "alpha", 0, 1);
			translationYObject = ObjectAnimator.ofFloat(view, "translationY", -300, 0);
		}

		AnimatorSet set = new AnimatorSet();
		set.playTogether(alphaObject, translationYObject);

		set.addListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {
				iView.setAnimationStatus(true);
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				iView.setAnimationStatus(false);
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				iView.setAnimationStatus(false);
			}

			@Override
			public void onAnimationRepeat(Animator animation) {

			}
		});

		//设置动画时间 (作用到每个动画)
		set.setDuration(MapFragment.ANIMATION_TIME);
		set.setInterpolator(new AccelerateInterpolator());
		set.start();
	}

	@Override
	public void buildAndStartAnimation(View view) {
		ViewPropertyAnimator animator = view.animate();

		float alphaValue = view.getAlpha() == 0f ? 1f : 0f;
		animator.alpha(alphaValue);

//		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//			float zValue = view.getTranslationZ() != 25f ? 25f : 2f;
//			animator.translationZ(zValue);
//		}

		animator.setListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {
				iView.setAnimationStatus(true);
				if (view.getVisibility()!=View.VISIBLE){
					view.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				if (alphaValue==0){
					view.setVisibility(View.GONE);
				}
				iView.setAnimationStatus(false);
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				iView.setAnimationStatus(false);
			}

			@Override
			public void onAnimationRepeat(Animator animation) {

			}
		});

		animator.setDuration(MapFragment.ANIMATION_TIME);
		animator.setInterpolator(new AccelerateInterpolator());
		animator.start();
	}

	@Override
	public SearchParam createHouseAge(DropBo dropType) {
		SearchParam search = new SearchParam();
		search.setId(dropType.getID());
		search.setText(dropType.getText());
		search.setTitle(dropType.getName());
		search.setValue("0,0");
		search.setPara(dropType.getPara());
		search.setName(dropType.getName());

		String[] yearStr = dropType.getValue().split(",");
		if (yearStr.length>1){
			Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

			int minYear = year - Integer.parseInt(yearStr[1]);
			int maxYear = year - Integer.parseInt(yearStr[0]);

			try {
				Date minDate = format.parse(minYear+"-1-1");
				Date maxDate = format.parse(maxYear+"-12-31");

				long minTime = minDate.getTime()/1000;
				long maxTime = maxDate.getTime()/1000;

				search.setValue(minTime+","+maxTime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return search;
	}

	/**
	 * 根据菜单Model创建对应的SearchParam
	 * @param dropType
	 * @return SearchParam
	 */
	@Override
	public SearchParam createSearch(DropBo dropType) {
		SearchParam search = new SearchParam();
		search.setId(dropType.getID() == null ? 0 : dropType.getID());
		search.setText(dropType.getText());
		search.setValue(dropType.getValue());
		search.setTitle(dropType.getName());
		search.setPara(dropType.getPara());
		search.setName(dropType.getName());
		return search;
	}

	@Override
	public SearchParam createOpenDate(DropBo dropType) {
		SearchParam search = new SearchParam();
		search.setId(dropType.getID());
		search.setText(dropType.getText());
		search.setTitle(dropType.getName());
		search.setValue("0,0");
		search.setPara(dropType.getPara());
		search.setName(dropType.getName());

		String[] monthStr = dropType.getValue().split(",");
		if (monthStr.length>1){

			GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTimeInMillis(System.currentTimeMillis());

			int startMonth = Integer.parseInt(monthStr[0]);
			int endMonth = Integer.parseInt(monthStr[1]);

			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

			calendar.add(Calendar.MONTH,  startMonth);
			String startDate = format.format(calendar.getTime());

			calendar.setTimeInMillis(System.currentTimeMillis());

			calendar.add(Calendar.MONTH,  endMonth);
			String endDate = format.format(calendar.getTime());

			Logger.i(startDate+","+endDate);
			search.setValue(startDate+","+endDate);
		}
		return search;
	}
}
