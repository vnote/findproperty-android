package com.cetnaline.findproperty.presenter.ui;

import com.cetnaline.findproperty.api.bean.BuildingNumBean;
import com.cetnaline.findproperty.presenter.BaseView;
import com.cetnaline.findproperty.presenter.IPresenter;

import java.util.List;
import java.util.Map;

/**
 * Created by diaoqf on 2017/3/27.
 */

public interface DeputeSourceInfoContract {
    interface View extends BaseView {
        void setBuildingNos(List<BuildingNumBean> list);
        void setRoomNos(List<BuildingNumBean> list);
        void reloadData();
        void setEvaluate(double num);
    }

    interface Presenter extends IPresenter<View> {
        void getBuildingNos(String no);
        void getRoomNos(String no);
        void getFormdata();
        void getEvaluateRequest(Map<String, String> params);
    }
}
