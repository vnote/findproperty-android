package com.cetnaline.findproperty.presenter;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by fanxl2 on 2016/7/22.
 */
public abstract class BasePresenter<T extends IView> implements IPresenter<T>  {

	public T iView;
	private CompositeSubscription mCompositeSubscription;

	@Override
	public void attachView(T view) {
		this.iView=view;
	}

	@Override
	public void detachView() {
		this.iView = null;
		onUnsubscribe();
	}

	//RXjava注册
	protected void addSubscribe(Subscription subscription) {
		if (mCompositeSubscription == null) {
			mCompositeSubscription = new CompositeSubscription();
		}
		mCompositeSubscription.add(subscription);
	}

	//RXjava取消注册，以避免内存泄露
	public void onUnsubscribe() {
		if (mCompositeSubscription != null && mCompositeSubscription.hasSubscriptions()) {
			mCompositeSubscription.unsubscribe();
		}
	}
}
