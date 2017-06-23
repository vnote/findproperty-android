package com.cetnaline.findproperty.presenter.ui;

import com.cetnaline.findproperty.api.bean.AdvertBo;
import com.cetnaline.findproperty.api.bean.HouseBo;
import com.cetnaline.findproperty.api.bean.NewHouseListBo;
import com.cetnaline.findproperty.api.bean.SearchParam;
import com.cetnaline.findproperty.presenter.BaseView;
import com.cetnaline.findproperty.presenter.IPresenter;

import java.util.List;
import java.util.Map;

/**
 * MainContract操作 定义Presenter和View的操作行为
 * Created by fanxl2 on 2016/7/22.
 */
public interface HomeContract {

	interface View extends BaseView {

		void setIntention(List<SearchParam> searchParams, int houseType);

		void setHouseList(List<HouseBo> houseList);

		void setNewHouseList(List<NewHouseListBo> houseList);

		void setHomeRecommend(List<HouseBo> houseBos);

		void setAdvert(AdvertBo advert);
	}

	interface Presenter extends IPresenter<View>{

		void getIntention4Home(String userId);

		void getHouseList(Map<String, String> params);

		void getNewHouseList(Map<String, String> params);

		void getHomeRecommend(double lat, double lng);

		void getHouseBySchool4AllResult(Map<String, String> params);

		void getHouseByMetroAllResult(Map<String, String> params);

		void getAppXuanFuAdvertRequest();

	}

}
