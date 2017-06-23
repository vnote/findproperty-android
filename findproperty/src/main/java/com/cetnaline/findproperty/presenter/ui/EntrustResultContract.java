package com.cetnaline.findproperty.presenter.ui;

import android.app.Activity;

import com.cetnaline.findproperty.api.bean.MyEntrustBo;
import com.cetnaline.findproperty.presenter.BaseView;
import com.cetnaline.findproperty.presenter.IPresenter;

/**
 * Created by fanxl2 on 2016/8/1.
 */
public interface EntrustResultContract {

	interface View extends BaseView {

		void setMyEntrust(MyEntrustBo entrust);

	}

	interface Presenter extends IPresenter<View> {

		void getMyEntrustById(String userId, String entrustId);

		void toMsg(Activity activity, String staffName, String staffNo, String msg);

	}

}
