package com.cetnaline.findproperty.ui.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.cetnaline.findproperty.api.ErrorHanding;
import com.cetnaline.findproperty.api.bean.SearchData;
import com.cetnaline.findproperty.api.bean.StoreBo;
import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.db.entity.GScope;
import com.cetnaline.findproperty.db.entity.RailLine;
import com.cetnaline.findproperty.db.entity.Store;
import com.cetnaline.findproperty.entity.bean.GScopeBean;
import com.cetnaline.findproperty.utils.DbUtil;
import com.cetnaline.findproperty.utils.SharedPreferencesUtil;
import com.orhanobut.logger.Logger;

import java.util.HashMap;
import java.util.List;

import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

import static com.cetnaline.findproperty.ui.activity.SplashActivity.DATA_LOAD_SUCCESS;

/**
 * 数据下载服务
 * Created by fanxl2 on 2016/12/14.
 */
public class DataService extends Service {

	private long startTime;

	@Override
	public void onCreate() {
		super.onCreate();
		startTime = System.currentTimeMillis();
	}

	private void checkData() {

		Logger.i("数据服务启动，检查数据中...");

		if (!SharedPreferencesUtil.getBoolean("GScopeData")) {
			getGscopeData();
		}
		if (!SharedPreferencesUtil.getBoolean("railLineData")) {
			getRailLines();
		}

		if (!SharedPreferencesUtil.getBoolean("searchData")) {
			getSearchData();
		}

		if (!SharedPreferencesUtil.getBoolean("storeData")) {
			getStoreData();
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		checkData();
		return super.onStartCommand(intent, flags, startId);
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private void getSearchData() {
		Subscription subscription = ApiRequest.getSearchData()
				.subscribe(searchDatas -> {
					setSearchData(searchDatas);
				}, throwable -> {
					String msg = ErrorHanding.handleError(throwable);
					Logger.e(msg);
					dataDid();
				});
		addSubscribe(subscription);
	}

	private void getRailLines() {
		Subscription subscription = ApiRequest.getRailLines()
				.subscribe(railLines -> {

					setRailLines(railLines);

				}, throwable -> {
					String msg = ErrorHanding.handleError(throwable);
					Logger.e(msg);
					dataDid();
				});
		addSubscribe(subscription);
	}

	private void getGscopeData() {
		Subscription subscription = ApiRequest.getGScope()
				.subscribe(gscopeList -> {

					setGscopeData(gscopeList);

				}, throwable -> {

					String msg = ErrorHanding.handleError(throwable);
					Logger.e(msg);
					dataDid();
				});

		addSubscribe(subscription);
	}

	private void getStoreData() {
		Subscription subscription = ApiRequest.searchStoreSingle(
				new HashMap() {{
					put("PageIndex", "1");
					put("PageCount", "10000");
				}})
				.subscribe(new Action1<List<StoreBo>>() {
					@Override
					public void call(List<StoreBo> storeBos) {
						setStoreData(storeBos);
					}
				}, new Action1<Throwable>() {
					@Override
					public void call(Throwable throwable) {
						String msg = ErrorHanding.handleError(throwable);
						Logger.e(msg);
						dataDid();
					}
				});
		addSubscribe(subscription);
	}

	public void setSearchData(List<SearchData> searchDatas) {

		if (searchDatas == null || searchDatas.size() == 0) {
			dataDid();
			return;
		}

		DbUtil.clearSearchData();
		for (SearchData item : searchDatas) {
			DbUtil.saveSearchData(item);
		}
		SharedPreferencesUtil.saveBoolean("searchData", true);
		dataDid();
	}

	public void setRailLines(List<RailLine> railLines) {
		if (railLines == null || railLines.size() == 0) {
			dataDid();
			return;
		}
		DbUtil.clearRailLine();
		for (RailLine item : railLines) {
			DbUtil.saveRailWay(item);
		}
		DbUtil.saveRailLine(railLines);

		SharedPreferencesUtil.saveBoolean("railLineData", true);
		dataDid();
	}

	public void setGscopeData(List<GScopeBean> gscopeDatas) {

		if (gscopeDatas == null && gscopeDatas.size() == 0) {
			dataDid();
			return;
		}

		DbUtil.deleteAllGScope();
		for (GScopeBean gScopeBean : gscopeDatas) {
			GScope savedGScope = new GScope();
			savedGScope.setFirstPY(gScopeBean.FirstPY);
			savedGScope.setFullPY(gScopeBean.FullPY);
			savedGScope.setGScopeAlias(gScopeBean.GScopeAlias);
			savedGScope.setGScopeCode(gScopeBean.GScopeCode);
			savedGScope.setGScopeId(gScopeBean.GScopeId);
			savedGScope.setGScopeLevel(gScopeBean.GScopeLevel);
			savedGScope.setGScopeName(gScopeBean.GScopeName);
			savedGScope.setLat(gScopeBean.Lat);
			savedGScope.setLng(gScopeBean.Lng);
			savedGScope.setParentId(gScopeBean.ParentId);
			savedGScope.setOrderBy(gScopeBean.OrderBy);
			DbUtil.addGScope(savedGScope);
			savedGScope(gScopeBean.GScopeList);
		}

		SharedPreferencesUtil.saveBoolean("GScopeData", true);

		dataDid();
	}

	private void savedGScope(List<GScopeBean> gScopeList) {
		for (GScopeBean bean : gScopeList) {
			GScope savedGScope = new GScope();
			savedGScope.setFirstPY(bean.FirstPY);
			savedGScope.setFullPY(bean.FullPY);
			savedGScope.setGScopeAlias(bean.GScopeAlias);
			savedGScope.setGScopeCode(bean.GScopeCode);
			savedGScope.setGScopeId(bean.GScopeId);
			savedGScope.setGScopeLevel(bean.GScopeLevel);
			savedGScope.setGScopeName(bean.GScopeName);
			savedGScope.setLat(bean.Lat);
			savedGScope.setLng(bean.Lng);
			savedGScope.setParentId(bean.ParentId);
			savedGScope.setOrderBy(bean.OrderBy);
			savedGScope.setPostCount(bean.PostCount);
			DbUtil.addGScope(savedGScope);
		}
	}

	public void setStoreData(List<StoreBo> storeData) {
		if (storeData == null && storeData.size() == 0) {
			dataDid();
			return;
		}

		DbUtil.deleteAllStore();
		for (StoreBo scopeBean : storeData) {
			Store store = new Store();
			store.setGscopeId(scopeBean.getGscopeId());
			store.setLat(scopeBean.getLat());
			store.setLng(scopeBean.getLng());
			store.setPaNo(scopeBean.getPaNo());
			store.setRegionId(scopeBean.getRegionId());
			store.setStaffCount(scopeBean.getStaffCount());
			store.setStore400Tel(scopeBean.getStore400Tel());
			store.setStoreAddr(scopeBean.getStoreAddr());
			store.setStoreHonor(scopeBean.getStoreHonor());
			store.setStoreName(scopeBean.getStoreName());
			store.setStoreTel(scopeBean.getStoreTel());
			store.setTencentVistaUrl(scopeBean.getTencentVistaUrl());
			store.setStoreId(scopeBean.getStoreId());

			DbUtil.addStore(store);
		}

		SharedPreferencesUtil.saveBoolean("storeData", true);
		dataDid();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		onUnsubscribe();
		Logger.i("数据加载时间:"+(System.currentTimeMillis()-startTime));
		Logger.i("数据服务销毁");
	}

	private CompositeSubscription mCompositeSubscription;

	//RXjava注册
	protected void addSubscribe(Subscription subscription) {
		if (mCompositeSubscription == null) {
			mCompositeSubscription = new CompositeSubscription();
		}
		mCompositeSubscription.add(subscription);
	}

	//RXjava取消注册，以避免内存泄露
	public void onUnsubscribe() {
		if (mCompositeSubscription != null && mCompositeSubscription.hasSubscriptions()) {
			mCompositeSubscription.unsubscribe();
		}
	}

	private static volatile int result;

	private void dataDid() {
		synchronized (DataService.class) {
			result++;
			if (result == 4) {
				Logger.i("数据全部执行完毕");
				if (SharedPreferencesUtil.getBoolean("GScopeData") &&
						SharedPreferencesUtil.getBoolean("railLineData") &&
						SharedPreferencesUtil.getBoolean("searchData") &&
						SharedPreferencesUtil.getBoolean("storeData")) {
					SharedPreferencesUtil.saveBoolean(DATA_LOAD_SUCCESS, true);
					Logger.i("数据全部加载完毕");
				}
				stopSelf();
			}
		}
	}
}
