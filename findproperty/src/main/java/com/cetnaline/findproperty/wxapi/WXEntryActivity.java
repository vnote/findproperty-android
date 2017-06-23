package com.cetnaline.findproperty.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.cetnaline.findproperty.AppContents;
import com.cetnaline.findproperty.BuildConfig;
import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.entity.bean.WXTokenBean;
import com.cetnaline.findproperty.entity.bean.WxUserBean;
import com.cetnaline.findproperty.entity.event.NormalEvent;
import com.cetnaline.findproperty.entity.event.ShareEvent;
import com.cetnaline.findproperty.entity.event.WxLoginEvent;
import com.cetnaline.findproperty.utils.RxBus;
import com.orhanobut.logger.Logger;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import io.rong.eventbus.EventBus;
import rx.Subscriber;

/**
 * 微信登录回调页面
 * Created by guilin on 16/4/11.
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;
    private BaseResp resp = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        api = WXAPIFactory.createWXAPI(this, BuildConfig.APP_ID_WX, false);
        api.handleIntent(getIntent(), this);

        super.onCreate(savedInstanceState);

//        setContentView(R.layout.act_wxentry);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        finish();
    }

    @Override
    public void onResp(BaseResp baseResp) {
        WxLoginEvent event = new WxLoginEvent();
        NormalEvent bindEvent = new NormalEvent();
        bindEvent.type = NormalEvent.WX_BIND;
        event.AppNo = "APP";
        switch(baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                if (baseResp.transaction != null) {
                    switch (baseResp.transaction) {
                        case AppContents.WX_SHARE_FINISH:
                            RxBus.getDefault().send(new ShareEvent(ShareEvent.EVENT_TYPE_SUCCESS));
                            finish();
                            break;
                        default:
                            finish();
                            break;
                    }
                } else {
                    SendAuth.Resp thisResp = (SendAuth.Resp) baseResp;
                    String code = thisResp.code;
                    ApiRequest.getUserToken(code)
                            .subscribe(new Subscriber<WXTokenBean>() {
                                @Override
                                public void onCompleted() {
                                }

                                @Override
                                public void onError(Throwable e) {
                                    e.printStackTrace();
                                    Toast.makeText(WXEntryActivity.this, "获取授权失败", Toast.LENGTH_SHORT).show();
                                    finish();
                                }

                                @Override
                                public void onNext(WXTokenBean wxTokenBean) {
                                    ApiRequest.getWxUserInfo(wxTokenBean.access_token, wxTokenBean.openid)
                                            .subscribe(new Subscriber<WxUserBean>() {
                                                @Override
                                                public void onCompleted() {
                                                }

                                                @Override
                                                public void onError(Throwable e) {
                                                    e.printStackTrace();
                                                    finish();
                                                }

                                                @Override
                                                public void onNext(WxUserBean wxUserBean) {
                                                    if (thisResp.state.equals(AppContents.WX_BIND_TAG)) {
                                                        bindEvent.result = true;
                                                        bindEvent.data = wxUserBean.getOpenid();
                                                        EventBus.getDefault().post(bindEvent);
                                                    } else {
                                                        event.isOk = true;
                                                        event.NickName = wxUserBean.getNickname();
                                                        event.ThirdLoginPicUrl = wxUserBean.getHeadimgurl();
                                                        event.WeiXinAccount = wxUserBean.getOpenid();
                                                        EventBus.getDefault().post(event);
                                                    }
                                                    finish();
                                                }
                                            });
                                }
                            });
                }

                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                RxBus.getDefault().send(new ShareEvent(ShareEvent.EVENT_TYPE_CANCLE));
                event.isOk = false;
                EventBus.getDefault().post(event);

                bindEvent.result = false;
                EventBus.getDefault().post(bindEvent);
                finish();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                RxBus.getDefault().send(new ShareEvent(ShareEvent.EVENT_TYPE_FAIL));
                event.isOk = false;
                EventBus.getDefault().post(event);

                bindEvent.result = false;
                EventBus.getDefault().post(bindEvent);
                finish();
                break;
            default:
                event.isOk = false;
                EventBus.getDefault().post(event);

                bindEvent.result = false;
                EventBus.getDefault().post(bindEvent);
                finish();
                break;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
        finish();
    }
}

