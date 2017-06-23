package com.cetnaline.findproperty.presenter.ui;

import com.cetnaline.findproperty.api.bean.LookAboutListDetailBo;
import com.cetnaline.findproperty.presenter.BaseView;
import com.cetnaline.findproperty.presenter.IPresenter;

import java.util.List;

/**
 * Created by fanxl2 on 2016/8/8.
 */
public interface ToLookAboutContract {

	interface View extends BaseView {

		void setLookAboutList(List<LookAboutListDetailBo> lookAboutList);
		void showNoData(boolean flag);
		void setTotalNumber(int totalNumber);
	}

	interface Presenter extends IPresenter<View> {

		void getLookAboutList(String userId, int status, int pageIndex, int pageSize);

	}

}
