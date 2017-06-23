package com.cetnaline.findproperty.presenter.ui;

import com.cetnaline.findproperty.presenter.BaseView;
import com.cetnaline.findproperty.presenter.IPresenter;

/**
 *
 * Created by fanxl2 on 2016/7/22.
 */
public interface ScanContract {

	interface View extends BaseView {

		void setHouse(String postId, int houseType);

		void setStaff(String staffNo, String staffName);

	}

	interface Presenter extends IPresenter<View>{

		void getHouseByAdsNo(String adsNo);

		void getStaffInfo(String staffNo);
	}

}
