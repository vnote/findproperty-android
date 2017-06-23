package com.cetnaline.findproperty.presenter.ui;

import com.cetnaline.findproperty.api.bean.EstateBo;
import com.cetnaline.findproperty.presenter.BaseView;
import com.cetnaline.findproperty.presenter.IPresenter;

import java.util.List;
import java.util.Map;

/**
 * Created by fanxl2 on 2016/8/8.
 */
public interface EstateListContract {

	interface View extends BaseView {

		void setEstateList(List<EstateBo> estateList);

		void netWorkException();

	}

	interface Presenter extends IPresenter<View> {

		void getEstateList(Map<String, String> params);

		void getEstateByMetro(Map<String, String> params);

		void getEstateBySchool(Map<String, String> params);

	}

}
