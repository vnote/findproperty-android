package com.cetnaline.findproperty.utils;

import android.content.Context;

import com.baidu.mobstat.SendStrategyEnum;
import com.baidu.mobstat.StatService;
import com.cetnaline.findproperty.BuildConfig;
import com.cetnaline.findproperty.api.NetContents;

/**
 * 百度统计配置
 * Created by diaoqf on 2017/5/18.
 */

public class BaiduCountUtil {

    public static void init(Context context) {
        //百度统计
        String channelName = MyUtils.getAppMetaData(context, NetContents.CHANNAL_KEY);
        StatService.setAppChannel(context, channelName, true);

        //打开调试
        if (BuildConfig.DEBUG) {
            StatService.setDebugOn(true);
        }

        //打开百度统计异常收集
        StatService.setOn(context,StatService.EXCEPTION_LOG );
        //发送日志策略 (启动时发送)
        StatService.setSendLogStrategy(context, SendStrategyEnum.APP_START, 1, false);
        //设置百度统计发送日志延迟时间（s）
        StatService.setLogSenderDelayed(5);
    }
}
