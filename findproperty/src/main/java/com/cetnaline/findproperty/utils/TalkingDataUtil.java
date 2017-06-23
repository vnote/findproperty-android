package com.cetnaline.findproperty.utils;

import android.content.Context;

import com.cetnaline.findproperty.BuildConfig;
import com.orhanobut.logger.Logger;
import com.tendcloud.tenddata.TCAgent;

/**
 * talking data 相关配置、工具
 * Created by diaoqf on 2017/5/18.
 */

public class TalkingDataUtil {

    /**
     * 初始化
     * @param context
     */
    public static void init(Context context) {
        if (BuildConfig.OPEN_TALKING_DATA) {
            TCAgent.LOG_ON = true;
            TCAgent.init(context);
            TCAgent.setReportUncaughtExceptions(true);  //捕获异常 用于错误报告统计
            TCAgent.setAntiCheatingEnabled(context, true); //用户质量评估开关
        }
    }

    public static void onPageStart(Context context, String pageName) {
        if (BuildConfig.OPEN_TALKING_DATA) {
            TCAgent.onPageStart(context, pageName);
        }
    }

    public static void onPageEnd(Context context, String pageName) {
        if (BuildConfig.OPEN_TALKING_DATA) {
            TCAgent.onPageEnd(context, pageName);
        }
    }

}
