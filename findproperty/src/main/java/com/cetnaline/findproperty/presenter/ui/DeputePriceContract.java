package com.cetnaline.findproperty.presenter.ui;

import com.cetnaline.findproperty.api.bean.HouseBo;
import com.cetnaline.findproperty.api.bean.PriceTrendBean;
import com.cetnaline.findproperty.presenter.BaseView;
import com.cetnaline.findproperty.presenter.IPresenter;

import java.util.List;


/**
 * Created by diaoqf on 2017/3/30.
 */

public interface DeputePriceContract {
    interface View extends BaseView {
        void setPrices(PriceTrendBean bean);
        void setHouseList(List<HouseBo> list);
        void reloadData();
    }

    interface Presenter extends IPresenter<DeputePriceContract.View> {
        void getPrices(String estateId, String gScopeId, String type);
        void getHouseList(String estateId, String type);
        void getFormdata();
    }
}
