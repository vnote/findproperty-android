package com.cetnaline.findproperty.presenter.ui;

import android.content.Intent;
import android.view.View;

import com.cetnaline.findproperty.entity.bean.QQUserInfoEntity;
import com.cetnaline.findproperty.entity.bean.SinaUserInfoBean;
import com.cetnaline.findproperty.presenter.BaseView;
import com.cetnaline.findproperty.presenter.IPresenter;

/**
 * Created by diaoqf on 2016/8/28.
 */
public interface UserInfoSettingContract {
    interface View extends BaseView {
        void setSex(String sex);

        void setQQAccount(QQUserInfoEntity entity);
        void setWBAccount(SinaUserInfoBean entity);
        void setWXAccount(String account);

        void updateQQState(String account);
        void updateWBState(String account);
        void updateWXState(String account);

        void setPhone(String phone);
        void setName(String name);
        void setImage(String uri);

        void showMessage(String message);
        void showLoading(boolean show);

        void requestCameraPermissions(String[] permissions, int resultCode);
    }

    interface  Presenter extends IPresenter<UserInfoSettingContract.View> {
        void showSexDialog(android.view.View parent);

        void startActivityForResult(Class clz,int flag);

        void bindWB(boolean flag);
        void bindQQ(boolean flag);
        void bindWX(boolean flag);

        void qqCallback(int requestCode, int resultCode, Intent data);
        void wbCallback(int requestCode, int resultCode, Intent data);
        void normalCallback(int requestCode, int resultCode, Intent data);

        void showImageDialog();

        void sendAccountBind(String name,String value);

    }
}
