package com.cetnaline.findproperty.presenter.ui;

import com.cetnaline.findproperty.api.bean.AppAdBo;
import com.cetnaline.findproperty.api.bean.AppUpdateBo;
import com.cetnaline.findproperty.api.bean.SchoolBo;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.cetnaline.findproperty.presenter.IView;

import java.util.List;

/**
 * MainContract操作 定义Presenter和View的操作行为
 * Created by fanxl2 on 2016/7/22.
 */
public interface MainContract {

	interface View extends IView {

		void setSchoolList(List<SchoolBo> schoolList);

		void showError(String msg);

		void setAppVersion(AppUpdateBo appVersion);

		void setAppAdvert(AppAdBo advert);

		void showSchoolLoading(boolean showing);
	}

	interface Presenter extends IPresenter<View>{

		void getSchoolList(String regionId);

		void getAppVersion();

		void getAppAdvert();

//		void showAdvertDialog(String imgUrl, String advertUrl, AppCompatActivity activity);
	}

}
