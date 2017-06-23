package com.cetnaline.findproperty.presenter.ui;

import android.app.Activity;
import android.content.Context;

import com.cetnaline.findproperty.api.bean.MyEntrustBo;
import com.cetnaline.findproperty.presenter.BaseView;
import com.cetnaline.findproperty.presenter.IPresenter;

import java.util.List;

/**
 * Created by fanxl2 on 2016/8/8.
 */
public interface MyEntrustContract {

	interface View extends BaseView {

		void setMyEntrustList(List<MyEntrustBo> entrustList,long total, boolean isReload);

		void setDelEntrustResult(boolean result,long entrustId, int position);

		void updateListForCancel(MyEntrustBo bo, int pos);
	}

	interface Presenter extends IPresenter<View> {

		void getMyEntrustList(String userId, int pageIndex, int pageSize, boolean reload);

		void updateEntrust(long entrustId, String currentType,int position);

		void toMsg(Activity activity, String staffName, String staffNo, String msg);

		void checkFormNum(Context context);

		void getEntrust(boolean result,String id, int pos);

		void goFormPage(Context context);

		void registerListListener();
	}

}
