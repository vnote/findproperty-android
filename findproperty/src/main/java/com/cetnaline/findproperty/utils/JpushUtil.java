package com.cetnaline.findproperty.utils;

import android.app.Notification;
import android.content.Context;

import com.cetnaline.findproperty.AppContents;
import com.cetnaline.findproperty.R;
import com.orhanobut.logger.Logger;

import java.util.HashSet;
import java.util.Set;

import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * Created by diaoqf on 2016/9/23.
 */
public class JpushUtil {

    /**
     * 设置极光别名 初始化
     */
    public static void initJpush(Context context){

        Logger.i("021_" + DataHolder.getInstance().getUserId().toLowerCase());
//        Set<String> sets = new HashSet<>();
//        sets.add("021_"+DataHolder.getInstance().getUserId().toLowerCase());
//        JPushInterface.setTags(context,sets, new TagAliasCallback(){
//            @Override
//            public void gotResult(int i, String s, Set<String> set) {
//                Logger.i("极光推送Tag设置回调:" + i + "s:" + s);
//            }
//        });

        checkPushOpen(context);
        setJpushNotificationBehavior(context);
    }

    private static void checkPushOpen(Context context) {
        if (DataHolder.getInstance().isCloseNotice() && !JPushInterface.isPushStopped(context)) {
            JPushInterface.stopPush(context);
        } else {
            JPushInterface.resumePush(context);
            JPushInterface.setAlias(context, "021_" + DataHolder.getInstance().getUserId().toLowerCase(), new TagAliasCallback() {
                @Override
                public void gotResult(int i, String s, Set<String> set) {
                    Logger.i("极光推送别名设置回调:" + i + "s:" + s);
                }
            });
        }
    }

    public static void setJpushNotificationBehavior(Context context){
        checkPushOpen(context);

        BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(context);
        builder.statusBarDrawable = R.mipmap.ic_launcher;
        builder.notificationFlags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_SHOW_LIGHTS;  //设置为自动消失和呼吸灯闪烁

        int flag = Notification.DEFAULT_LIGHTS;

        if (!DataHolder.getInstance().isCloseVibrate()) {
            flag |= Notification.DEFAULT_VIBRATE;
        }
        if (!DataHolder.getInstance().isCloseSound()) {
            flag |= Notification.DEFAULT_SOUND;
        }

        builder.notificationDefaults = flag;  // 设置为铃声、震动、呼吸灯闪烁
        JPushInterface.setDefaultPushNotificationBuilder(builder);
    }

    /**
     * 登出极光
     * @param context
     */
    public static void logout(Context context) {
        JPushInterface.setAlias(context,"",new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {
                if(i== 0) {
                    Logger.i("清除别名成功");
                } else {
                    Logger.i("没能清除别名");
                }
            }
        });
        JPushInterface.stopPush(context);
    }
}
