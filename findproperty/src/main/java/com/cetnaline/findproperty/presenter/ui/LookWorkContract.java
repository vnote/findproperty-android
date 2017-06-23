package com.cetnaline.findproperty.presenter.ui;

import com.cetnaline.findproperty.api.bean.SendAppointmentRequest;
import com.cetnaline.findproperty.presenter.BaseView;
import com.cetnaline.findproperty.presenter.IPresenter;

/**
 * Created by fanxl2 on 2016/8/8.
 */
public interface LookWorkContract {

	interface View extends BaseView {

		void setCommitLookResult(boolean result);
	}

	interface Presenter extends IPresenter<View> {

		void commitLookAboutList(SendAppointmentRequest request);

	}

}
