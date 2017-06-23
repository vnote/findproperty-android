package com.cetnaline.findproperty.presenter.ui;

import com.cetnaline.findproperty.api.bean.InsertIntentionsRequest;
import com.cetnaline.findproperty.presenter.BaseView;
import com.cetnaline.findproperty.presenter.IPresenter;

/**
 *
 * Created by fanxl2 on 2016/7/22.
 */
public interface IntentContract {

	interface View extends BaseView {

		void setInsertResult();
	}

	interface Presenter extends IPresenter<View>{

		void insertIntent(InsertIntentionsRequest request);
	}

}
