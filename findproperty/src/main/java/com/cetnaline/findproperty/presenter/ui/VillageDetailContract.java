package com.cetnaline.findproperty.presenter.ui;

import com.cetnaline.findproperty.api.bean.EstateBo;
import com.cetnaline.findproperty.api.bean.PriceTrendBean;
import com.cetnaline.findproperty.presenter.BaseView;
import com.cetnaline.findproperty.presenter.IPresenter;

import java.util.Map;

/**
 * Created by fanxl2 on 2016/8/1.
 */
public interface VillageDetailContract {

	interface View extends BaseView {

		void setPriceTrend(PriceTrendBean priceTrend);

		void setVillageDetail(EstateBo detail);

		void setCollectResult(long collectId);

		void isCollected(long collectId);

		void deleteCollectResult(boolean result);

		void hidePriceTrend();
	}

	interface Presenter extends IPresenter<View> {

		void getPriceTrend(Map<String, String> gParam, Map<String, String> eParam);

		void getVillageDetail(Map<String, String> params);

		void insertCollect(Map<String, String> params);

		void checkCollect(String userId, String postId);

		void deleteCollect(long collectId);

	}

}
