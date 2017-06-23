package com.cetnaline.findproperty.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.base.BaseActivity;
import com.cetnaline.findproperty.db.entity.Staff;
import com.cetnaline.findproperty.entity.bean.StaffListBean;
import com.cetnaline.findproperty.entity.event.UnreadEvent;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.cetnaline.findproperty.ui.fragment.ConversationFragment;
import com.cetnaline.findproperty.utils.DataHolder;
import com.cetnaline.findproperty.utils.DbUtil;
import com.cetnaline.findproperty.utils.MyUtils;
import com.cetnaline.findproperty.utils.RcUtil;
import com.cetnaline.findproperty.utils.SchedulersCompat;
import com.cetnaline.findproperty.utils.StatusBarCompat;
import com.orhanobut.logger.Logger;

import java.util.HashMap;
import java.util.Locale;

import io.rong.eventbus.EventBus;
import io.rong.imkit.RongIM;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by diaoqf on 2016/7/25.
 */
public class ConversationActivity extends BaseActivity {

    public static final String DEFAULT_MESSAGE = "default_message";

    public static final String CONVERSATION_TARGET = "conversation_target";
    public static final String CONVERSATION_TARGET_VALUE = "conversation_target_value";

    public static final String OPEN_CONVERSATION_TYPE = "open_conversation_type";
    public static final String FULL_PATH = "full_path";
    public static final String CONTENT = "content";


    public static final String ERSHOUFANG = "ershoufang";
    public static final String XINFANG = "xinfang";
    public static final String ZUFANG = "zufang";
    public static final String JINGJIREN = "jingjiren";

    private String currentTargetType;
    private String currentTargetValue;

    private String sourceInfo;
    private String mTargetId;
    private String mTitle;
    private String fullPath;
    private String content;

    private UserInfo userInfo;
    private Staff staff;

    private String openConversationType; //打开聊天页来源(经济人列表or房源)
    /**
     * 会话类型
     */
    private Conversation.ConversationType mConversationType;
    private CompositeSubscription mCompositeSubscription;
    @Override
    protected int getContentViewId() {
        return R.layout.act_conversation;
    }

    @Override
    protected void initBeforeSetContentView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        } else {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }
    }

    @Override
    protected void init(Bundle savedInstanceState) {
//        EventBus.getDefault().register(this);
        Intent intent = getIntent();
        getIntentDate(intent);
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        if (RongIMClient.getInstance().getCurrentConnectionStatus().getMessage().equals("Disconnected")) {
            RcUtil.connectRC();
        }
//        loadUserInfo();

        //设置标题
        center_title.setTextColor(getResources().getColor(R.color.grayText));
        center_title.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
        if (mTitle.length() > 10) {
            center_title.setText(mTitle.substring(0,10)+"...");
        } else {
            center_title.setText(mTitle);
        }

        center_title.setVisibility(View.VISIBLE);
        isReconnect(intent);

        //获取经纪人信息,并连接中原服务器
        mCompositeSubscription.add(Observable.just(mTargetId)
                .map(s -> {
                    String no = s.substring(s.lastIndexOf("_") + 1, s.length());
                    staff = DbUtil.getStaffByUid(no);
                    if (staff == null) {
                        mCompositeSubscription.add(ApiRequest.getStaffDetail(no)
                                .subscribe(bean -> {
                                    Staff staff1 = new Staff();
                                    staff1.setName(bean.CnName);
                                    staff1.setDepartmentName(bean.StoreName);
                                    staff1.setImageUrl(bean.StaffImg);
                                    staff1.setMobile(bean.Mobile);
                                    staff1.setUId(bean.StaffNo.toLowerCase());
                                    userInfo = new UserInfo(mTargetId,bean.CnName,Uri.parse(bean.StaffImg));
                                    RongUserInfoManager.getInstance().setUserInfo(userInfo);
                                    DbUtil.addStaff(staff1);
                                    connectServer();
                                    Logger.i("get userinfo from network");
                                }));
                        return "need request";
                    } else {
                        userInfo = new UserInfo(mTargetId,staff.getName(),Uri.parse(staff.getImageUrl()));
                        RongUserInfoManager.getInstance().setUserInfo(userInfo);
                        connectServer();
                    }
                    return "already got target information";
                })
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(s -> {
                    Logger.i(s);
                },throwable -> throwable.printStackTrace()));
    }

    /**
     * 连接到中原服务器
     */
    private void connectServer(){
        if (MyUtils.TALK_FROM_ADVISER_LIST.equals(openConversationType)) {
            //经纪人列表打开
            String number = staff.getStaff400Tel();
            String[] num_arr = number.split(",");
            mCompositeSubscription.add(ApiRequest.singleMessageRequest(new HashMap(){
                {
                    put("Send",mTargetId);
                    put("Recived",DataHolder.getInstance().getRcUserId());
                    put("Extra",mTitle);
                    put("AppName","APP_ANDROID");
                    put("MsgContent",num_arr[0]+"转"+num_arr[1]);
                }
            }).subscribe(integer -> {
                Logger.i("发送状态:" + integer);
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    throwable.printStackTrace();
                }
            }));
        } else {
            //房源打开
            if (currentTargetType != null) {
                mCompositeSubscription.add(ApiRequest.messageRecordUpdateRequest(new HashMap() {
                    {
                        put("Target", currentTargetType);
                        put("MsgURL", "http://sh.centanet.com/" + currentTargetType + "/" + currentTargetValue + ".html");
                        put("MsgDescription", sourceInfo);
                        put("MediaFileOrDefaultImageURL", "http://imgsh.centanet.com/ctpostimage/" + fullPath + "_120x120.jpg");
                        put("TargetValue", currentTargetValue);
                        put("MsgType", "RC:ImgTextMsg");
                        put("CityCode", "021");
                        put("AppName", "APP_ANDROID");
                        put("RcReceiver", mTargetId);
                        put("RcSender", DataHolder.getInstance().getRcUserId());
                        put("MsgContent", content);
                    }
                }).subscribe(integer -> {
                    Logger.i("调用结果:" + integer);
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }));
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1001) {
            finish();
        }
    }

    private void getIntentDate(Intent intent) {
        mTargetId = intent.getData().getQueryParameter("targetId");
        mTitle = intent.getData().getQueryParameter("title");
        openConversationType = intent.getData().getQueryParameter(OPEN_CONVERSATION_TYPE);
        sourceInfo = intent.getData().getQueryParameter(DEFAULT_MESSAGE);
        fullPath = intent.getData().getQueryParameter(FULL_PATH);
        content = intent.getData().getQueryParameter(CONTENT);

        currentTargetType = intent.getData().getQueryParameter(CONVERSATION_TARGET);
        currentTargetValue = intent.getData().getQueryParameter(CONVERSATION_TARGET_VALUE);
        mConversationType = Conversation.ConversationType.valueOf(intent.getData().getLastPathSegment().toUpperCase(Locale.getDefault()));

        enterFragment(mConversationType, mTargetId);
    }


    /**
     * 判断消息是否是 push 消息
     */
    private void isReconnect(Intent intent) {
        String token = DataHolder.getInstance().getRcToken();

        //push或通知过来
        if (intent != null && intent.getData() != null && intent.getData().getScheme().equals("rong")) {

            //通过intent.getData().getQueryParameter("push") 为true，判断是否是push消息
            if (intent.getData().getQueryParameter("push") != null
                    && intent.getData().getQueryParameter("push").equals("true")) {
                reconnect(token);
            } else {
                //程序切到后台，收到消息后点击进入,会执行这里
                if (RongIM.getInstance() == null || RongIM.getInstance().getRongIMClient() == null) {
                    reconnect(token);
                } else {
                    enterFragment(mConversationType, mTargetId);
                }
            }
        }
    }

    /**
     * 重连
     *
     * @param token
     */
    private void reconnect(String token) {

        if (getApplicationInfo().packageName.equals(mApp.curProcessName)) {

            RongIM.connect(token, new RongIMClient.ConnectCallback() {
                @Override
                public void onTokenIncorrect() {

                }

                @Override
                public void onSuccess(String s) {
//                    enterFragment(mConversationType, mTargetId);
                    ConversationFragment fragment = (ConversationFragment) getSupportFragmentManager().findFragmentById(R.id.conversation);
                    Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                            .appendPath("conversation").appendPath(mConversationType.getName().toLowerCase())
                            .appendQueryParameter("targetId", mTargetId).build();
                    fragment.setUri(uri);
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {

                }
            });
        }
    }

    /**
     * 加载会话页面 ConversationFragment
     *
     * @param mConversationType
     * @param mTargetId
     */
    private void enterFragment(Conversation.ConversationType mConversationType, String mTargetId) {

        ConversationFragment fragment = (ConversationFragment) getSupportFragmentManager().findFragmentById(R.id.conversation);

        Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                .appendPath("conversation").appendPath(mConversationType.getName().toLowerCase())
                .appendQueryParameter("targetId", mTargetId).build();

        fragment.setUri(uri);
    }

    @Override
    protected IPresenter createPresenter() {
        return null;
    }

    @Override
    protected void initToolbar() {
        StatusBarCompat.setStatusBarColor(this, Color.WHITE);
        toolbar.setBackgroundColor(getResources().getColor(R.color.grayBigLine));
        toolbar.setNavigationOnClickListener((v) -> {
//            WindowManager.LayoutParams params = getWindow().getAttributes();
//            // 隐藏软键盘
//            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
//            params.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN;
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(toolbar.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
            onBackPressed();
        });
        //设置标题
        toolbar.setTitle("");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.adviser_store_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.adviser_store:
                Intent i = new Intent(this, AdviserDetailActivity.class);
                StaffListBean staffBean = new StaffListBean();
                staffBean.StaffNo = mTargetId.substring(mTargetId.lastIndexOf("_") + 1, mTargetId.length());
                staffBean.CnName = mTitle;
                i.putExtra(AdviserDetailActivity.ADVISER, staffBean);
                startActivity(i);
                break;
        }
        return true;
    }

    /**
     * 首次聊天发送默认回复信息
     */
//    public void sendMessage(){
//        //自动接收消息
//        if (userInfo != null) {
//            SendContent content = new SendContent();
//            String number = "400-811-0117,00000";
//            if (staff != null) {
//                number = staff.getStaff400Tel();
//            }
//            String[] number_arr = number.split(",");
//            content.content = "欢迎咨询中原地产，顾问"+ userInfo.getName() + "将尽快为您提供服务，您也可以拨打"+number_arr[0]+","+number_arr[1]+" 咨询，谢谢!";
//            content.extra = "";
//            SendUser user = new SendUser();
//            user.icon = userInfo.getPortraitUri().toString();
//            user.id = userInfo.getUserId();
//            user.name = userInfo.getName();
//            content.user = user;
//            PushData pushData = new PushData();
//            pushData.pushData = content.content;
//
//            Gson gson = new Gson();
//            mCompositeSubscription.add(RongRequest.getRongCloudApi().sendMessage(
//                    userInfo.getUserId(),
//                    DataHolder.getInstance().getRcUserId(),
//                    "RC:TxtMsg",
//                    gson.toJson(content),
//                    gson.toJson(pushData),
//                    "0", 1, 1, 1).subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe((o) -> {
//                    }));
//        }
//    }

    /**
     * 发送系统消息
     */
//    public void sendSystemMessage(){
//        if (mCompositeSubscription == null) {
//            mCompositeSubscription = new CompositeSubscription();
//        }
//        mCompositeSubscription.add(ApiRequest.getLiaoTianSysMsg().subscribe(msg->{
//            if (msg.MsgText != null) {
//                String url = msg.LinkUrl;
//                url = MyUtils.getURLDecoderString(url);
//                if (url != null) {
//                    String[] arr = url.split("oadest=");
//                    if (arr.length > 1) {
//                        url = url.split("oadest=")[1];
//                        if (url.equals("") || url.equals("http://blank")) {
//                            url = "";
//                        }
//                    }
//                }
//                mCompositeSubscription.add(RongRequest.getRongCloudApi().sendMessage(
//                        mTargetId,
//                        DataHolder.getInstance().getRcUserId(),
//                        "RC:SysMsg",
//                        "{\"content\":\""+msg.MsgText+"\",\"extra\":\""+url+"\"}",
//                        "{\"pushData\":\""+msg.LinkUrl+"\"}",
//                        "0", 0, 0, 0).subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe((o) -> {
//                            Logger.i("推送系统消息成功");
//                        }));
//            }
//        },throwable -> {
//            throwable.printStackTrace();
//            Logger.i("获取系统消息");
//        }));
//    }


//    public void sendDefaultMessage(){
//        if (userInfo == null || staff == null) {
//            mCompositeSubscription.add(ApiRequest.getStaffDetail(null, mTargetId.substring(mTargetId.lastIndexOf("_") + 1, mTargetId.length()))
//                    .subscribe(bean -> {
//                        staff = new Staff();
//                        staff.setName(bean.CnName);
//                        staff.setDepartmentName(bean.StoreName);
//                        staff.setImageUrl(bean.StaffImg);
//                        staff.setMobile(bean.Mobile);
//                        String number = bean.getMobileBy400() != null ? bean.getMobileBy400():bean.getStaff400Tel();
//                        staff.setStaff400Tel(number);
//                        staff.setUId(bean.StaffNo.toLowerCase());
//                        DbUtil.addStaff(staff);
//                        userInfo = new UserInfo(mTargetId,bean.CnName,Uri.parse(bean.StaffImg));
//                        //默认的回复消息
//                        sendMessage();
//                    },throwable -> throwable.printStackTrace()));
//        } else {
//            sendMessage();
//        }
//    }



    private static class SendContent{
        public String content;
        public SendUser user;
        public String extra;
    }

    private static class SendUser{
        public String id;
        public String name;
        public String icon;
    }

    @Override
    protected void onResume() {
        super.onResume();
//        MyUtils.checkNotificationSetting(this);
        DataHolder.getInstance().setChatting(true);
        DataHolder.getInstance().setChatTarget(mTargetId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DataHolder.getInstance().setChatting(false);
        DataHolder.getInstance().setChatTarget("");
        EventBus.getDefault().post(new UnreadEvent());
    }

    @Override
    protected void onDestroy() {
        if (mCompositeSubscription != null && mCompositeSubscription.hasSubscriptions()) {
            mCompositeSubscription.unsubscribe();
        }
//        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private static class PushData{
        public String pushData;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //录音
        if (requestCode == 1001) {
//            if (grantResults!=null && grantResults.length>0){
//                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
////                    new android.support.v7.app.AlertDialog.Builder(this)
////                            .setTitle("录音权限")
////                            .setMessage("需要录音权限才能发送语音")
////                            .setPositiveButton("确定", (dialog, which) -> {
////                                ActivityCompat.requestPermissions(this, permissions, 1001);
////                            }).show();
//                }
//            }else {
//                Logger.i("授权失败");
//            }
        }
        //摄像头
        if (requestCode == 1002) {

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

//    public void onEventMainThread(SystemMessageEvent event){
//        //自动发送消息
//        if (mDefaultMessage != null && !"".equals(mDefaultMessage)) {
//            TextMessage myTextMessage = TextMessage.obtain(mDefaultMessage);
//            Message myMessage = Message.obtain(mTargetId, Conversation.ConversationType.PRIVATE, myTextMessage);
//            RongIM.getInstance().sendMessage(myMessage, null, null, new IRongCallback.ISendMessageCallback() {
//                @Override
//                public void onAttached(Message message) {
//                    //消息本地数据库存储成功的回调
//                }
//
//                @Override
//                public void onSuccess(Message message) {
//                    //消息通过网络发送成功的回调
////                    Logger.i(message.getContent());
//                }
//
//                @Override
//                public void onError(Message message, RongIMClient.ErrorCode errorCode) {
//                    //消息发送失败的回调
//                    toast(errorCode.getMessage());
//                }
//            });
//        }
//    }

    @Override
    protected String getTalkingDataPageName() {
        return "聊天页面";
    }
}
