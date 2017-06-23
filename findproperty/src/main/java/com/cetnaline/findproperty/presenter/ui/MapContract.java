package com.cetnaline.findproperty.presenter.ui;

import android.content.Context;

import com.cetnaline.findproperty.api.bean.EstateBo;
import com.cetnaline.findproperty.api.bean.EstateMapRequest;
import com.cetnaline.findproperty.api.bean.HouseBo;
import com.cetnaline.findproperty.api.bean.NewHouseMapDetail;
import com.cetnaline.findproperty.api.bean.RegionPostBo;
import com.cetnaline.findproperty.api.bean.SchoolBo;
import com.cetnaline.findproperty.api.bean.SearchParam;
import com.cetnaline.findproperty.db.entity.DropBo;
import com.cetnaline.findproperty.presenter.BaseView;
import com.cetnaline.findproperty.presenter.IPresenter;

import java.util.List;
import java.util.Map;

/**
 * Created by fanxl2 on 2016/7/25.
 */
public interface MapContract {

	interface View extends BaseView {

		void setBaseMapData(List<RegionPostBo> regionList);

//		void setEsfEstateData(List<EstateBo> esfEstateDoList);

		void setEsfEstateData(List<EstateBo> esfEstateDoList);

		void setHouseList(List<HouseBo> postDoList);

		void setRailWayEsfList(List<EstateBo> esfEstateDoList, String railWayId);

		void setEstByCircle(List<EstateBo> esfList);

		void setNewHouseBaseMapData(List<NewHouseMapDetail> baseMapData);

		void setEstBySchoolId(List<EstateBo> esfEstateDoList, SchoolBo schoolBo);

		void noData();

		void setAnimationStatus(boolean isRunning);

		void showProgress();
	}

	interface Presenter extends IPresenter<View>{
		//获取区和板块数据
		void getBaseMapData(Map<String, String> params);
		//获取板块下小区数据
//		void getEsfEstateList(Map<String, String> params);
		void getEsfEstateList(Context context,EstateMapRequest request);

		//获取小区下的房源数据
		void getHouseList(Map<String, String> params);
		//画圈找房
		void getEstByCircle(Context context, Map<String, String> params);
		void getNewEstByCircle(Context context,EstateMapRequest request);
		//地铁找房
		void getHouseByRailWay(Context context, Map<String, String> params);
		//获取新房 板块和区域数据
		void getNewHouseBaseMapData(Map<String, String> params);

		void getEstBySchoolId(Context context, String schoolId);

		//顶部菜单向上隐藏动画
		void startAnimation(android.view.View view, boolean isHide);

		//打开列表 手势工具等 渐隐动画
		void buildAndStartAnimation(android.view.View view);

		//创建房龄的SearchParam
		SearchParam createHouseAge(DropBo dropType);

		SearchParam createSearch(DropBo dropType);

		SearchParam createOpenDate(DropBo dropType);
	}



}
