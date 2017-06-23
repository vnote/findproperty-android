package com.cetnaline.findproperty.presenter.ui;

import com.cetnaline.findproperty.presenter.IPresenter;
import com.cetnaline.findproperty.presenter.IView;

/**
 * Created by fanxl2 on 2016/8/8.
 */
public interface SplashContract {

	interface View extends IView {

		void setDataVersion(Long dataVersion);

	}

	interface Presenter extends IPresenter<View> {

		void getDataVersion();

	}

}
