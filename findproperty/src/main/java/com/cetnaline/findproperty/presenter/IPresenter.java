package com.cetnaline.findproperty.presenter;

/**
 * MVP Presenter的基类
 * @param <T>
 */
public interface IPresenter<T extends IView> {

	//Presenter和V进行关联
	void attachView(T view);

	void detachView();

}