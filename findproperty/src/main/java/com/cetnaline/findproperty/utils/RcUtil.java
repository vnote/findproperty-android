package com.cetnaline.findproperty.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.view.View;

import com.cetnaline.findproperty.AppContents;
import com.cetnaline.findproperty.BaseApplication;
import com.cetnaline.findproperty.BuildConfig;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.db.entity.Staff;
import com.cetnaline.findproperty.entity.bean.RcSendMessageBean;
import com.cetnaline.findproperty.entity.bean.StaffListBean;
import com.cetnaline.findproperty.entity.event.AppLoginEvent;
import com.cetnaline.findproperty.entity.event.UnreadEvent;
import com.cetnaline.findproperty.entity.ui.MyTextMessageItemProvider;
import com.cetnaline.findproperty.entity.ui.MyVoiceMessageItemProvider;
import com.cetnaline.findproperty.entity.ui.SystemMessageItemProvider;
import com.cetnaline.findproperty.ui.activity.AdviserDetailActivity;
import com.cetnaline.findproperty.ui.activity.HouseDetail;
import com.cetnaline.findproperty.ui.activity.LoginActivity;
import com.cetnaline.findproperty.ui.activity.WebActivity;
import com.cetnaline.findproperty.ui.fragment.HouseDetailFragment;
import com.cetnaline.findproperty.ui.fragment.MapFragment;
import com.cetnaline.findproperty.widgets.MyCameraInputProvider;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

import io.rong.eventbus.EventBus;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imkit.widget.provider.ImageInputProvider;
import io.rong.imkit.widget.provider.InputProvider;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import io.rong.message.InformationNotificationMessage;
import io.rong.message.RichContentMessage;

/**
 * Created by diaoqf on 2016/8/26.
 */
public class RcUtil {
    private static boolean isInit;

    private static Context mContext;

    /**
     * 初始化配置
     * @param context
     */
    public static void init(Context context){
        if (!isInit) {
            isInit = true;
            mContext = context;
            //注册系统消息类别
//            RongIM.registerMessageType(SystemMessage.class);
//            RongIM.getInstance().registerMessageTemplate(new SystemMessageItemProvider());
            RongIM.getInstance().registerMessageTemplate(new MyTextMessageItemProvider());
            RongIM.getInstance().registerMessageTemplate(new MyVoiceMessageItemProvider(context));

            //输入框扩展功能定义
            InputProvider.ExtendProvider[] provider = {
                    new ImageInputProvider(RongContext.getInstance()),//图片
                    new MyCameraInputProvider(RongContext.getInstance())//相机
//                    new LocationInputProvider(RongContext.getInstance())//地理位置
            };
            RongIM.getInstance().resetInputExtensionProvider(Conversation.ConversationType.PRIVATE, provider);

            //状态监听
            RongIM.setConnectionStatusListener(connectionStatus -> {
                if (connectionStatus != null) {
                    switch (connectionStatus){
                        case CONNECTED://连接成功。
                            Logger.i("RongCloud Connected");
                            break;
                        case DISCONNECTED://断开连接。
                            Logger.i("RongCloud Disconnected, reconnecting");
                            connectRC();
                            break;
                        case CONNECTING://连接中。
                            Logger.i("RongCloud Connecting");
                            break;
                        case NETWORK_UNAVAILABLE://网络不可用。
                            break;
                        case KICKED_OFFLINE_BY_OTHER_CLIENT://用户账户在其他设备登录，本机会被踢掉线
                            Logger.i("RongCloud Current Client Kicked");
                            DataHolder.getInstance().setChoiceIntent(0);
                            Intent intent = new Intent(RongContext.getInstance(), LoginActivity.class);
                            intent.putExtra(LoginActivity.KICKED_RE_LOGIN, true);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            RongContext.getInstance().startActivity(intent);

                            break;
                    }
                }
            });

//            RongIM.getInstance().getRongIMClient().setConnectionStatusListener(new RongIMClient.ConnectionStatusListener() {
//                @Override
//                public void onChanged(ConnectionStatus connectionStatus) {
//                    switch (connectionStatus){
//                        case CONNECTED://连接成功。
//                            Logger.i("RongCloud Connected");
//                            break;
//                        case DISCONNECTED://断开连接。
//                            Logger.i("RongCloud Disconnected, reconnecting");
//                            connectRC();
//                            break;
//                        case CONNECTING://连接中。
//                            Logger.i("RongCloud Connecting");
//                            break;
//                        case NETWORK_UNAVAILABLE://网络不可用。
//                            break;
//                        case KICKED_OFFLINE_BY_OTHER_CLIENT://用户账户在其他设备登录，本机会被踢掉线
//                            Logger.i("RongCloud Current Client Kicked");
//                            break;
//                    }
//                }
//            });
        }
    }

    /**
     * 初始化连接
     */
    public static void connectRC(){
        //连接融云服务器
        if (!"".equals(DataHolder.getInstance().getRcToken())) {
            RongIM.connect(DataHolder.getInstance().getRcToken(), new RongIMClient.ConnectCallback() {
                @Override
                public void onTokenIncorrect() {
                }

                @Override
                public void onSuccess(String userId) {
                    //获取用户信息
                    UserInfo userInfo = new UserInfo(userId,DataHolder.getInstance().getRcUsername(), Uri.parse(DataHolder.getInstance().getRcPortrait()));
                    RongIM.getInstance().refreshUserInfoCache(userInfo);
                    if (RongIM.getInstance() != null) {
                        setRcListener();
                    }

                    //保存未读消息数量
                    int count = RongIM.getInstance().getUnreadCount(Conversation.ConversationType.PRIVATE);
                    DataHolder.getInstance().setMessageCount(count);
                    EventBus.getDefault().post(new UnreadEvent());
                    Logger.i("连接融云成功");
                }
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    Logger.e("连接融云失败:"+errorCode);
//                    DataHolder.getInstance().clear();
                    RongIMClient.getInstance().disconnect();
                    //发送
                    EventBus.getDefault().post(new AppLoginEvent(false,2));
                }
            });
        } else {
            Logger.i("当前用户未登陆");
        }
    }

    /**
     * 设置融云通知监听
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static void setRcListener() {
        //接收消息监听
        RongIM.setOnReceiveMessageListener((message,i)->{
            Logger.i("buobao:get message from RC.");

            //(dataHolder.isChatting() && dataHolder.getChatTarget().equals(message.getTargetId()))

            if (DataHolder.getInstance().isCloseNotice() || (!MyUtils.isRunningBackground(mContext) && MyUtils.isAppRunning(mContext))) {
                return false;
            }
            UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(message.getTargetId());

            if (userInfo != null) {
                setNotification(message, userInfo);
            } else {
                ApiRequest.getStaffDetail(message.getTargetId().substring(message.getTargetId().lastIndexOf("_")+ 1))
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(bean -> {
                            if (bean != null) {
                                UserInfo userInfo1 = new UserInfo(message.getTargetId(), bean.getCnName(), Uri.parse(bean.getStaffImg()));
                                RongUserInfoManager.getInstance().setUserInfo(userInfo1);
                                setNotification(message, userInfo1);
                            }
                        },throwable -> throwable.printStackTrace());
            }


            return true;
        });

        //未读消息监听
        RongIM.getInstance().setOnReceiveUnreadCountChangedListener(new RongIM.OnReceiveUnreadCountChangedListener() {
            @Override
            public void onMessageIncreased(int i) {

            }
        }, Conversation.ConversationType.PRIVATE);


//        RongIM.getInstance().setSendMessageListener(new RongIM.OnSendMessageListener() {
//            @Override
//            public Message onSend(Message message) {
//                return message;
//            }
//
//            @Override
//            public boolean onSent(Message message, RongIM.SentMessageErrorCode sentMessageErrorCode) {
//                String msg = new String(message.getContent().encode());
//                Gson gson = new Gson();
//                RcSendMessageBean sendMessageBean = gson.fromJson(msg, RcSendMessageBean.class);
//                Logger.i("推送时间:"+ System.currentTimeMillis()+",message:"+sendMessageBean.content);
//                if (!("").equals(sendMessageBean.content)) { //message.getUId() != null &&
//                    //将发送的消息推送到服务器
//                    Map<String, String> params = new HashMap<>();
//                    params.put("CityCode", "021");
//                    params.put("AppName", "APP");
//                    params.put("Target", ConversationActivity.CURRENT_TARGET_TYPE);
//                    params.put("TargetValue", ConversationActivity.CURRENT_TARGET_VALUE);
//                    params.put("RcSender", message.getSenderUserId());
//                    params.put("RcReceiver", message.getTargetId());
//                    params.put("MsgType", "TextMessage");
//                    params.put("MsgContent", sendMessageBean.content);
//
//                    ApiRequest.messageRecord(params).subscribe(new Subscriber<ApiResponse>() {
//                        @Override
//                        public void onCompleted() {}
//
//                        @Override
//                        public void onError(Throwable e) {}
//
//                        @Override
//                        public void onNext(ApiResponse apiResponse) {
//                            Logger.i("推送消息成功,target:"+ConversationActivity.CURRENT_TARGET_TYPE+","+ConversationActivity.CURRENT_TARGET_VALUE);
//                        }
//                    });
//                }
//
//                return true;
//            }
//        });

        //聊天页面行为
        RongContext.getInstance().setConversationBehaviorListener(new RongIM.ConversationBehaviorListener() {
            @Override
            public boolean onUserPortraitClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo) {
                String id = userInfo.getUserId();

                Staff staff = DbUtil.getStaffByUid(id.substring(id.lastIndexOf("_") + 1, id.length()));
                if (staff != null) {
                    StaffListBean bean = new StaffListBean();
                    bean.setStaffNo(staff.getUId());
                    bean.setMobileBy400(staff.getStaff400Tel());
                    bean.setStaffImg(staff.getImageUrl());
                    bean.setStoreName(staff.getDepartmentName());
                    bean.setCnName(staff.getName());
                    bean.setServiceEstates(staff.getServiceEstates());

                    Intent i = new Intent(context, AdviserDetailActivity.class);
                    i.putExtra(AdviserDetailActivity.ADVISER, bean);
                    context.startActivity(i);
                }

//                ApiRequest.getStaffDetail(null, id.substring(id.lastIndexOf("_") + 1, id.length()))
//                        .subscribe(staffListBean -> {
//                            if (staffListBean != null) {
//                                Intent i = new Intent(context, AdviserDetailActivity.class);
//                                i.putExtra(AdviserDetailActivity.ADVISER, staffListBean);
//                                context.startActivity(i);
//                            }
//                        }, throwable -> {
//                            throwable.printStackTrace();
//                            Logger.i("没有查询到经纪人详情");
//                        });

                return false;
            }

            @Override
            public boolean onUserPortraitLongClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo) {
                return false;
            }

            @Override
            public boolean onMessageClick(Context context, View view, Message message) {
                //图片消息直接返回
                if (message.getObjectName().equals("RC:ImgMsg")) {
                    return false;
                }

                //图文消息点击
                if (message.getObjectName().equals("RC:ImgTextMsg")) {
                    String sourceUrl = ((RichContentMessage)message.getContent()).getUrl();
                    if (sourceUrl != null) {
                        String[] sourceString = sourceUrl.substring(0, sourceUrl.length() - 5).split("/");
                        if (sourceString.length > 2) {
                            Intent intent = new Intent(RongContext.getInstance(), HouseDetail.class);
                            intent.putExtra(MapFragment.HOUSE_TYPE_KEY, sourceString[sourceString.length - 2]);
                            intent.putExtra(HouseDetailFragment.HOUSE_ID_KEY, sourceString[sourceString.length - 1]);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            RongContext.getInstance().startActivity(intent);
                        }
                    }
                    return true;
                }

                if (message.getObjectName().equals("RC:InfoNtf") && message.getContent() != null) {
                    InformationNotificationMessage msg = (InformationNotificationMessage) message.getContent();
                    //点击系统消息链接
                    return openWeb(msg.getExtra());
                }

                String number_pattern = "^\\d{8,13}$";
                Pattern r = Pattern.compile(number_pattern);
                String msg = new String(message.getContent().encode());
                Gson gson = new Gson();
                RcSendMessageBean sendMessageBean = gson.fromJson(msg, RcSendMessageBean.class);
                //拨打8位数以上的数字电话
                if (sendMessageBean.content != null && r.matcher(sendMessageBean.content).matches()) {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    Uri data = Uri.parse("tel:"+sendMessageBean.content);
                    intent.setData(data);
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return false;
                    }
                    context.startActivity(intent);
                    return true;
                } else {
                    return false;
                }
            }

            private boolean openWeb(String url) {
                if (url == null || "".equals(url)) {
                    return true;
                } else {
                    //打开网页
                    if (MyUtils.isWebUrl(url)) {
                        if (!MyUtils.openActivityForUrl(RongContext.getInstance(),url,true)) {
                            Intent intent1 = new Intent(RongContext.getInstance(),WebActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString(WebActivity.TARGET_URL,url);
                            intent1.putExtras(bundle);
                            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            RongContext.getInstance().startActivity(intent1);
                        }
                        return true;
                    } else {
                        return false;
                    }
                }
            }

            @Override
            public boolean onMessageLinkClick(Context context, String s) {
                String number_pattern_1 = "^\\d{8,10},\\d{4,6}$";
                Pattern r1 = Pattern.compile(number_pattern_1);
                if (r1.matcher(s).matches()) {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    Uri data = Uri.parse("tel:"+s);
                    intent.setData(data);
                    if (ActivityCompat.checkSelfPermission(RongContext.getInstance(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return false;
                    }
                    RongContext.getInstance().startActivity(intent);
                    return true;
                }

                if (MyUtils.isWebUrl(s)) {
                    return openWeb(s);
                }
                return false;
            }

            @Override
            public boolean onMessageLongClick(Context context, View view, Message message) {
                return false;
            }
        });
    }

    private static void setNotification(Message message, UserInfo userInfo) {
        NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(mContext.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);

        String msg = new String(message.getContent().encode());
        Gson gson = new Gson();
        RcSendMessageBean sendMessageBean = gson.fromJson(msg, RcSendMessageBean.class);

        Uri uri = Uri.parse("rong://" + mContext.getApplicationInfo().processName).buildUpon()
                .appendPath("conversation")
                .appendPath(Conversation.ConversationType.PRIVATE.getName().toLowerCase())
                .appendQueryParameter("targetId", userInfo.getUserId())
                .appendQueryParameter("title", userInfo.getName())
                .build();

        Intent openIntent = new Intent("android.intent.action.VIEW", uri);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0,
                openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        String content = null;
        if (message.getObjectName().equals("RC:SysMsg") || message.getObjectName().equals("RC:TxtMsg")) {
            content = sendMessageBean.content;
            content = MyUtils.translateEmoji(content);
        } else if (message.getObjectName().equals("RC:ImgMsg")) {
            content = "[图片]";
        } else if (message.getObjectName().equals("RC:VcMsg")) {
            content = "[语音]";
        } else if (message.getObjectName().equals("RC:ImgTextMsg")) {
            content = "[图文]";
        } else if (message.getObjectName().equals("RC:LBSMsg")) {
            content = "[位置]";
        }

        mBuilder.setContentTitle(userInfo.getName())
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setTicker(sendMessageBean.content)
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setDefaults(Notification.DEFAULT_LIGHTS)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setOngoing(false);

        int flag = Notification.DEFAULT_LIGHTS;

        if (!DataHolder.getInstance().isCloseVibrate()) {
            flag |= Notification.DEFAULT_VIBRATE;
        }
        if (!DataHolder.getInstance().isCloseSound()) {
            flag |= Notification.DEFAULT_SOUND;
        }
        NotificationCompat.Builder notification = mBuilder.setDefaults(flag);

        /**
         * 小米手机通知数量
         */
        if (Build.MANUFACTURER.equalsIgnoreCase("Xiaomi")){
            try {
                Field field = notification.getClass().getDeclaredField("extraNotification");
                Object extraNotification = field.get(notification);
                Method method = extraNotification.getClass().getDeclaredMethod("setMessageCount", int.class);
                method.invoke(extraNotification, RongIM.getInstance().getUnreadCount(Conversation.ConversationType.PRIVATE));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {

        }
        mNotificationManager.notify(DataHolder.getInstance().getCurrentMessageNoticeId(),mBuilder.build());
    }
}

























