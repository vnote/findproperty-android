package com.cetnaline.findproperty.presenter.impl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.TextView;

import com.cetnaline.findproperty.AppContents;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.entity.event.NormalEvent;
import com.cetnaline.findproperty.api.ErrorHanding;
import com.cetnaline.findproperty.presenter.BasePresenter;
import com.cetnaline.findproperty.presenter.ui.MyEntrustContract;
import com.cetnaline.findproperty.ui.activity.ConversationActivity;
import com.cetnaline.findproperty.ui.activity.DeputeActivity;
import com.cetnaline.findproperty.ui.activity.LoginActivity;
import com.cetnaline.findproperty.utils.DataHolder;
import com.cetnaline.findproperty.utils.MyUtils;
import com.cetnaline.findproperty.utils.RxBus;
import com.cetnaline.findproperty.utils.SchedulersCompat;

import java.util.HashMap;
import java.util.Map;

import io.rong.imlib.model.Conversation;
import rx.Subscription;

/**
 * Created by fanxl2 on 2016/8/8.
 */
public class MyEntrustPresenter extends BasePresenter<MyEntrustContract.View> implements MyEntrustContract.Presenter {

	@Override
	public void getMyEntrustList(String userId, int pageIndex, int pageSize, boolean reload) {
		iView.showLoading();
		Map<String,String> params = new HashMap<>();
		params.put("Count",pageSize+"");
		params.put("FirstIndex",pageIndex+"");
		params.put("UserId",userId);
		Subscription subscription = ApiRequest.getMyEntrustList(params)
				.subscribe(response -> {
					iView.dismissLoading();
					iView.setMyEntrustList(response.getResult(), response.getTotal(), reload);

				}, throwable -> {
					iView.dismissLoading();
					String msg = ErrorHanding.handleError(throwable);
					if (msg.equals("数据为空")){
						iView.setMyEntrustList(null,0, reload);
					}else {
						iView.showError(msg);
					}
				});
		addSubscribe(subscription);
	}

	@Override
	public void updateEntrust(long entrustId, String currentType, int position) {
		Map<String , String> params = new HashMap<>();
		params.put("EntrustID", entrustId+"");
		params.put("Status",currentType);

		Subscription subscription = ApiRequest.updateEntrustStutasRequest(params)
				.subscribe(result -> {

					iView.setDelEntrustResult(true, entrustId, position);

				}, throwable -> {
					iView.showError(ErrorHanding.handleError(throwable));

				});
		addSubscribe(subscription);
	}

//	public void callPhone(Context context){
//		Intent call = new Intent(Intent.ACTION_CALL, Uri.parse("tel:4008110117"));
//		if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//			iView.showError("权限被拒绝，不能拨打电话!");
//			return;
//		}
//		context.startActivity(call);
//	}

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

	@Override
	public void checkFormNum(Context context) {
		goFormPage(context);
	}

	@Override
	public void getEntrust(boolean result, String id, int pos) {
		if (result) {
			Subscription subscription = ApiRequest.getMyEntrustById(DataHolder.getInstance().getUserId(), id)
					.subscribe(myEntrustBos -> {
						if (myEntrustBos != null && myEntrustBos.size() > 0)
							iView.updateListForCancel(myEntrustBos.get(0), pos);
					}, throwable -> {
						String msg = ErrorHanding.handleError(throwable);
						if (msg.equals("数据为空")) {
//							iView.setMyEntrustList(null);

						} else {
							iView.showError(msg);
						}
					});

			addSubscribe(subscription);
		} else {
			iView.showError("删除失败");
		}
	}

	@Override
	public void goFormPage(Context context) {
		if (DataHolder.getInstance().isUserLogin()){
			addSubscribe(
					ApiRequest.entrustCountRequest(
							DataHolder.getInstance().getUserId())
							.compose(SchedulersCompat.applyIoSchedulers())
							.subscribe(integer -> {
								if (integer < AppContents.DEPUTE_COUNT) {
									Intent intent = new Intent(context, DeputeActivity.class);
									intent.putExtra(DeputeActivity.REFRESH_LIST, true);
									intent.putExtra(DeputeActivity.LIST_FROM,true);
									context.startActivity(intent);
								} else {
//									iView.showError("您的委托数量已达上限");
									MyUtils.showDiloag((Activity) context, R.layout.dialog_alert_single, 250, -1, true, (layout, dialog) -> {
                                        TextView title = (TextView) layout.findViewById(R.id.title);
                                        title.setText("您的委托数量已达上限");
                                        TextView submit = (TextView) layout.findViewById(R.id.submit);
                                        submit.setOnClickListener(v-> dialog.dismiss());
                                    });
								}
							}, throwable -> {
								throwable.printStackTrace();
								iView.showError("连接服务器失败，请稍后再试");
							}));
		}else {
			iView.showError("请先登录");
			context.startActivity(new Intent(context, LoginActivity.class));
		}
	}

	@Override
	public void registerListListener() {
		addSubscribe(RxBus.getDefault().toObservable(NormalEvent.class)
				.subscribe(normalEvent -> {
					if (normalEvent.type == NormalEvent.REFRESH_ENTRUST_LIST) {
						getMyEntrustList(DataHolder.getInstance().getUserId(),1,10,true);
					}
				}));
	}
}