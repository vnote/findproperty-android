package com.cetnaline.findproperty.presenter.ui;

import com.cetnaline.findproperty.presenter.BaseView;
import com.cetnaline.findproperty.presenter.IPresenter;

import java.util.Map;

/**
 * Created by fanxl2 on 2016/8/8.
 */
public interface LookEvaluateContract {

	interface View extends BaseView {

		void setCommentResult(boolean result);

//		void setStaffDetail(StaffListBean staffDetail);
	}

	interface Presenter extends IPresenter<View> {

		void lookAboutComment(Map<String, String> param);

//		void getStaffDetail(String staffID, String staffNo);

	}

}
