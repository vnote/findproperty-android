package com.cetnaline.findproperty.presenter.ui;

import com.cetnaline.findproperty.api.bean.Discount;
import com.cetnaline.findproperty.db.entity.DropBo;
import com.cetnaline.findproperty.presenter.BaseView;
import com.cetnaline.findproperty.presenter.IPresenter;

import java.util.List;
import java.util.Map;

/**
 *
 * Created by fanxl2 on 2016/7/22.
 */
public interface FoundContract {

	interface View extends BaseView {

		void setExerciseMenu(List<DropBo> exerciseList);

		void setTopicMenu(List<DropBo> topicList);

		void setDiscountList(List<Discount> discounts);

		void netWorkException();

		void noData();

	}

	interface Presenter extends IPresenter<View>{

		void getTopicMenu(String groupType);

		void getExerciseMenu(String groupType);

		void getDiscountList(Map<String, String> params);
	}

}
