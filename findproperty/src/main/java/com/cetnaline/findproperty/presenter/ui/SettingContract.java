package com.cetnaline.findproperty.presenter.ui;

import android.content.Context;
import android.view.LayoutInflater;

import com.cetnaline.findproperty.presenter.BaseView;
import com.cetnaline.findproperty.presenter.IPresenter;

import java.io.File;

/**
 * Created by diaoqf on 2016/8/28.
 */
public interface SettingContract {
    interface View extends BaseView {
        void setCacheFile(File file);
        void setCacheFileSize(double size);
        void setBackAction();
        void setFinished();
        void setPushSettingText();
    }

    interface Presenter extends IPresenter<SettingContract.View> {
        void getCacheFileSize(File file);
        void getCacheFile(File dir);

        void OpenActivity(Context context, Class clz);

        void logout(Context context);
        void exchangeLanguage(Context context);
        void getAppVerstion(Context context, LayoutInflater inflater);
        void openPushDialog(Context context,LayoutInflater inflater);
        void openAlert(Context context, LayoutInflater inflater, File file);
        String getPushState();
    }
}
