package com.cetnaline.findproperty.presenter.ui;

import com.cetnaline.findproperty.presenter.BaseView;
import com.cetnaline.findproperty.presenter.IPresenter;

import java.util.Map;

/**
 * Created by fanxl2 on 2016/8/1.
 */
public interface ExerciseContract {

	interface View extends BaseView {

		void setVerifyCodeResult(boolean result);

		void checkVerifyCodeResult(boolean result);

		void setInsertBookResult(boolean result);

		void setAddBookingResult(boolean result);
	}

	interface Presenter extends IPresenter<View> {

		void getVerifyCode(String phone);

		void verificationCode(String mobile, String code);

		void insertUserBooking(Map<String, String> param);

		void addBookingCount(String actId, int number);

	}

}
