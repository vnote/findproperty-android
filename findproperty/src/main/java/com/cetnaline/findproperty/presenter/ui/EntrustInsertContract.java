package com.cetnaline.findproperty.presenter.ui;

import com.cetnaline.findproperty.api.bean.StaffDetailBo;
import com.cetnaline.findproperty.entity.ui.DeputePushBean;
import com.cetnaline.findproperty.presenter.BaseView;
import com.cetnaline.findproperty.presenter.IPresenter;

import java.util.Map;

/**
 * Created by fanxl2 on 2016/8/1.
 */
public interface EntrustInsertContract {

	interface View extends BaseView {

		void showMsg(String msg);

		void insertResult(long entrustID);

		void setStaff(StaffDetailBo staff);

	}

	interface Presenter extends IPresenter<View> {

		void insertEntrustInfo(DeputePushBean bean);

		void getStaff(String estateCode);

	}

}
