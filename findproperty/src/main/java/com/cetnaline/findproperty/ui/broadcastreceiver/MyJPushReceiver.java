package com.cetnaline.findproperty.ui.broadcastreceiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.cetnaline.findproperty.AppContents;
import com.cetnaline.findproperty.ui.activity.MySubscribeActivity;
import com.cetnaline.findproperty.ui.activity.NewsActivity;
import com.cetnaline.findproperty.ui.activity.SplashActivity;
import com.cetnaline.findproperty.ui.activity.WebActivity;
import com.cetnaline.findproperty.utils.MyUtils;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.orhanobut.logger.Logger;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by diaoqf on 2016/9/3.
 */
public class MyJPushReceiver extends BroadcastReceiver {
    private NotificationManager nm;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (null == nm) {
            nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        Bundle bundle = intent.getExtras();

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            //JPush用户注册成功

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            //接受到推送下来的自定义消息
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            //接受到推送下来的通知
            receivingNotification(context,bundle);
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            //用户点击打开了通知
            openNotification(context,bundle);
        } else {

        }
    }

    private void receivingNotification(Context context, Bundle bundle){
        String title = bundle.getString(JPushInterface.EXTRA_TITLE);
        Logger.i("title:"+title);
        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
        Logger.i("message:"+message);
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        Logger.i("extras:"+extras);
        String type = bundle.getString(JPushInterface.EXTRA_CONTENT_TYPE);
        Logger.i("type:"+type);
        AppContents.JPUSH_MESSAGE_NOTIFICATION_ID = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID,AppContents.JPUSH_MESSAGE_NOTIFICATION_ID); // 获取通知的ID
    }

    private void openNotification(Context context, Bundle bundle){
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        Gson gson = new Gson();
        PushBean bean = gson.fromJson(extras,PushBean.class);

        Logger.i(extras);

        Logger.i("bean:"+bean.toString());

        if (bean.name == null) {
            return;
        }

        if (bean.name.equals("0") && bean.PostType !=null) {
            Intent intent;
            if (MyUtils.isAppRunning(context)) {
                intent = new Intent(context, NewsActivity.class);
            } else {
                intent = new Intent(context, SplashActivity.class);
                intent.putExtra("notice_lunch_mode",true);
                intent.putExtra("page","news");
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //我的消息
            if (bean.PostType.equals("0")) {
                //房源动态
                intent.putExtra("newType",0);
            } else if (bean.PostType.equals("1")) {
                //小区动态
                intent.putExtra("newType",1);
            }
            context.startActivity(intent);
        } else if (bean.name.equals("1")  && bean.PostType !=null) {
            Intent intent;
            if (MyUtils.isAppRunning(context)) {
                intent = new Intent(context, MySubscribeActivity.class);
            } else {
                intent = new Intent(context, SplashActivity.class);
                intent.putExtra("notice_lunch_mode",true);
                intent.putExtra("page","subscribe");
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //订阅
            if (bean.PostType.equals("xinfang")) {
                //新房
                intent.putExtra("subscribeType",2);
            } else if (bean.PostType.equals("ershoufang")) {
                //二手房
                 intent.putExtra("subscribeType",0);
            } else if (bean.PostType.equals("zufang")) {
                //租房
                intent.putExtra("subscribeType",1);
            }
            context.startActivity(intent);
        } else if (bean.name.equals("2") && bean.PostType !=null) {
            //打开网页
            if (MyUtils.isWebUrl(bean.PostType)) {
                if(bean.PostType.indexOf("http://") < 0) {
                    bean.PostType = "http://" + bean.PostType;
                }
                if (!MyUtils.openActivityForUrl(context,bean.PostType,true)) {
                    Intent intent1 = new Intent(context,WebActivity.class);
                    Bundle bundle1 = new Bundle();
                    bundle1.putString(WebActivity.TARGET_URL,bean.PostType);
                    intent1.putExtras(bundle1);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent1);
                }
            }
        } else if (bean.name.equals("3")) {
            //系统消息
            Intent intent;
            if (MyUtils.isAppRunning(context)) {
                intent = new Intent(context, NewsActivity.class);
            } else {
                intent = new Intent(context, SplashActivity.class);
                intent.putExtra("notice_lunch_mode",true);
                intent.putExtra("page","news");
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("newType",2);
            context.startActivity(intent);
        }
    }

    public static class PushBean {

        public String ID;    //这里保存意向的id，或者消息的id

        @SerializedName("PushType")
        public String name;

        public String PostType;

        @Override
        public String toString() {
            return "PushBean{" +
                    "ID='" + ID + '\'' +
                    ", name='" + name + '\'' +
                    ", PostType='" + PostType + '\'' +
                    '}';
        }
    }
}
