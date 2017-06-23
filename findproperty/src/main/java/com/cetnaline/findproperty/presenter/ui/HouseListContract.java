package com.cetnaline.findproperty.presenter.ui;

import com.cetnaline.findproperty.api.bean.HouseBo;
import com.cetnaline.findproperty.api.bean.InsertIntentionsRequest;
import com.cetnaline.findproperty.api.bean.NewHouseListBo;
import com.cetnaline.findproperty.presenter.BaseView;
import com.cetnaline.findproperty.presenter.IPresenter;

import java.util.List;
import java.util.Map;

/**
 * Created by fanxl2 on 2016/8/8.
 */
public interface HouseListContract {

	interface View extends BaseView {

		void setHouseList(List<HouseBo> houseList, int total);

		void setNewHouseList(List<NewHouseListBo> houseList, int total);

		void stopRefresh();

		void netWorkException();

		void setInsertResult(long intentId);

		void setDeleteIntentResult(boolean result);

	}

	interface Presenter extends IPresenter<View> {

//		void getHouseList(Map<String, String> params);

		void getHouseList4AllResult(Map<String, String> params);

		void getHouseBySchool4AllResult(Map<String, String> params);

		void getHouseByMetroAllResult(Map<String, String> params);

		void getNewHouseList(Map<String, String> params);

		void getNearbyHouse(Map<String, String> params);

		void insertIntent(InsertIntentionsRequest request);

		void deleteIntent(long intentId);

	}

}
