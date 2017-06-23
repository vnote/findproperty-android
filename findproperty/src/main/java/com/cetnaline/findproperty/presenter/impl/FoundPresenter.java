package com.cetnaline.findproperty.presenter.impl;

import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.db.entity.DropBo;
import com.cetnaline.findproperty.api.ErrorHanding;
import com.cetnaline.findproperty.presenter.BasePresenter;
import com.cetnaline.findproperty.presenter.ui.FoundContract;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rx.Subscription;

/**
 * MainPresenter最终的实现类，处理 Main的所有逻辑
 * Created by fanxl2 on 2016/7/22.
 */
public class FoundPresenter extends BasePresenter<FoundContract.View> implements FoundContract.Presenter {

	@Override
	public void getTopicMenu(String groupType) {
		Subscription subscription = ApiRequest.getDiscountMenu(groupType)
				.subscribe(menuStr -> {
					if (menuStr!=null){
						String[] menus = menuStr.split(",");
						if (menus!=null){
							List<DropBo> menuList = new ArrayList<>();
							menuList.add(new DropBo("全部专题", "全部", -1));
							for (int i=0; i<menus.length; i++){
								menuList.add(new DropBo(menus[i], menus[i], 1));
							}
							iView.setTopicMenu(menuList);
						}
					}
				}, throwable -> {
					iView.showError(ErrorHanding.handleError(throwable));
				});
		addSubscribe(subscription);
	}

	@Override
	public void getExerciseMenu(String groupType) {
		Subscription subscription = ApiRequest.getDiscountMenu(groupType)
				.subscribe(menuStr -> {
					if (menuStr!=null){
						String[] menus = menuStr.split(",");
						if (menus!=null){
							List<DropBo> menuList = new ArrayList<>();
							menuList.add(new DropBo("全部活动", "全部", -1));
							for (int i=0; i<menus.length; i++){
								menuList.add(new DropBo(menus[i], menus[i], 0));
							}
							iView.setExerciseMenu(menuList);
						}
					}
				}, throwable -> {
					iView.showError(ErrorHanding.handleError(throwable));
				});
		addSubscribe(subscription);
	}

	@Override
	public void getDiscountList(Map<String, String> params) {
		Subscription subscription = ApiRequest.getDiscountList(params)
				.subscribe(discounts -> {

					iView.setDiscountList(discounts);

				}, throwable -> {

					String msg = ErrorHanding.handleError(throwable);
					if ("数据为空".equals(msg)){
						iView.noData();
					}else if (msg.equals("网络连接异常，请检查网络连接")){
						iView.setDiscountList(null);
						iView.netWorkException();
						iView.showError(msg);
					}else {
						iView.showError(msg);
					}
				});
		addSubscribe(subscription);
	}
}
