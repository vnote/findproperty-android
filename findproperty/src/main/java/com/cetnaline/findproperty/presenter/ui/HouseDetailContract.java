package com.cetnaline.findproperty.presenter.ui;

import com.cetnaline.findproperty.api.bean.ApartmentBo;
import com.cetnaline.findproperty.api.bean.ExerciseListBo;
import com.cetnaline.findproperty.api.bean.HouseBo;
import com.cetnaline.findproperty.api.bean.HouseDetailBo;
import com.cetnaline.findproperty.api.bean.HouseImageBo;
import com.cetnaline.findproperty.api.bean.NewHouseDetail;
import com.cetnaline.findproperty.api.bean.NewHouseImageBo;
import com.cetnaline.findproperty.api.bean.PriceTrendBean;
import com.cetnaline.findproperty.api.bean.StaffComment;
import com.cetnaline.findproperty.entity.bean.StaffListBean;
import com.cetnaline.findproperty.presenter.BaseView;
import com.cetnaline.findproperty.presenter.IPresenter;

import java.util.List;
import java.util.Map;

/**
 * Created by fanxl2 on 2016/8/1.
 */
public interface HouseDetailContract {

	interface View extends BaseView {

		void showMsg(String msg);

		void noHouse();

		void setHouseDetail(HouseDetailBo detail);

		void setStaffComment(List<StaffComment> staffComments);

		void setNewHouseDetail(NewHouseDetail newHouseDetail);

		void setNearbyHouse(List<HouseBo> nearbyHouses);

		void setSaleApartments(List<ApartmentBo> apartments);

		void setHouseImageList(List<HouseImageBo> imageList);

		void setExercises(List<ExerciseListBo> exercises);

		void setNewHouseImage(List<NewHouseImageBo> imageBos);

		void setPriceTrend(PriceTrendBean priceTrend);

		void setCollectResult(long collectId);

		void setReservationResult(boolean result);

		void isCollected(long collectId);

		void deleteCollectResult(boolean result);

		void setLookedAboutNumber(int num);

		void setStaffDetail(StaffListBean detail);

		void setNewHouseStaff(StaffComment staff);
	}

	interface Presenter extends IPresenter<View> {

		void getHouseDetail(Map<String, String> params);

		void getStaffComment(String adsNo, String staffNo);

		void getNewHouseDetail(String estExtId);

		void getNearbyHouse(Map<String, String> params);

		void getSaleApartments(String estExtId);

		void getHouseImageList(String postId, int imgW, int imgH);

		void getExerciseList(String estExtId);

		void getNewHouseImage(String appvalue);

		void getPriceTrend(Map<String, String> gParam, Map<String, String> eParam);

		void insertCollect(Map<String, String> params);

		void insertReservation(Map<String, String> params);

		void checkCollect(String userId, String postId);

		void deleteCollect(long collectId);

		void getLookedAboutNumber(String userId, int status);

		void getStaffDetail(String staffNo);

		void getNewHouseStaff(String estExtId);
	}

}
