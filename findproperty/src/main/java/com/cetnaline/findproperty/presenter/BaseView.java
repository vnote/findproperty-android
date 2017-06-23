package com.cetnaline.findproperty.presenter;

/**
 * Created by fanxl2 on 2016/7/27.
 */
public interface BaseView extends IView{

	void showLoading();

	void dismissLoading();

	void showError(String msg);
}
