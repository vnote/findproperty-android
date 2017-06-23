package com.cetnaline.findproperty.presenter.ui;

import com.cetnaline.findproperty.api.bean.DeputeBean;
import com.cetnaline.findproperty.entity.ui.DeputePushBean;
import com.cetnaline.findproperty.presenter.BaseView;
import com.cetnaline.findproperty.presenter.IPresenter;

import java.util.List;

/**
 * Created by diaoqf on 2017/3/31.
 */

public interface DeputeCustomerContract {
    interface View extends BaseView {
        void finishForm(int resultCode);
        void setPublishedDepute(DeputeBean bean);
        void corePush(boolean showLoading);
    }

    interface Presenter extends IPresenter<DeputeCustomerContract.View> {
        void savePic(List<String> files);
        void getHouseById(String houseId, String type);
        void submitServer(DeputePushBean deputePushBean);
        void updateFormState(String id);
    }
}
