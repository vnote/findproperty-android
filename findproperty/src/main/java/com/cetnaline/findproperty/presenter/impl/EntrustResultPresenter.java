package com.cetnaline.findproperty.presenter.impl;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.api.ErrorHanding;
import com.cetnaline.findproperty.presenter.BasePresenter;
import com.cetnaline.findproperty.presenter.ui.EntrustResultContract;
import com.cetnaline.findproperty.ui.activity.ConversationActivity;

import io.rong.imlib.model.Conversation;
import rx.Subscription;

/**
 * Created by fanxl2 on 2016/8/8.
 */
public class EntrustResultPresenter extends BasePresenter<EntrustResultContract.View> implements EntrustResultContract.Presenter {

	@Override
	public void getMyEntrustById(String userId, String entrustId) {
		iView.showLoading();
		Subscription subscription = ApiRequest.getMyEntrustById(userId, entrustId)
				.subscribe(myEntrustBos -> {
					iView.dismissLoading();
					if (myEntrustBos==null || myEntrustBos.size()<1){
						iView.setMyEntrust(null);
					}else {
						iView.setMyEntrust(myEntrustBos.get(0));
					}
				}, throwable -> {
					iView.dismissLoading();
					iView.showError(ErrorHanding.handleError(throwable));

				});
		addSubscribe(subscription);
	}


	@Override
	public void toMsg(Activity activity, String staffName, String staffNo, String msg) {
		Uri uri = Uri.parse("rong://" + activity.getApplicationInfo().processName).buildUpon()
				.appendPath("conversation")
				.appendPath(Conversation.ConversationType.PRIVATE.getName().toLowerCase())
				.appendQueryParameter("targetId", "s_021_"+staffNo.toLowerCase())
				.appendQueryParameter("title", staffName)
				.appendQueryParameter(ConversationActivity.DEFAULT_MESSAGE, msg)
				.build();
		activity.startActivity(new Intent("android.intent.action.VIEW", uri));
	}
}
