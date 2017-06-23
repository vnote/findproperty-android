package com.cetnaline.findproperty.presenter.ui;

import com.cetnaline.findproperty.api.bean.BatchCollectRequest;
import com.cetnaline.findproperty.api.bean.LookAboutListDetailBo;
import com.cetnaline.findproperty.api.bean.LookListDeleteRequest;
import com.cetnaline.findproperty.presenter.BaseView;
import com.cetnaline.findproperty.presenter.IPresenter;

import java.util.List;

/**
 * Created by fanxl2 on 2016/8/8.
 */
public interface LookAboutListContract {

	interface View extends BaseView {

		void setLookAboutList(List<LookAboutListDetailBo> lookAboutList);

		void deleteLookAboutResult(boolean result);

		void setLookedAboutNumber(int num);

		void setCollectResult(boolean result);

		void showNoData(boolean flag);
	}

	interface Presenter extends IPresenter<View> {

		void getLookAboutList(String userId, int status);

		void deleteLookAboutList(LookListDeleteRequest request);

		void getLookedAboutNumber(String userId, int status);

		void insertCollectBatch(BatchCollectRequest request);

	}

}
