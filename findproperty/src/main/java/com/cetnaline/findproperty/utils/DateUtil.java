package com.cetnaline.findproperty.utils;

import android.text.format.DateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by diaoqf on 2016/8/1.
 */
public class DateUtil {

    public static final String FORMAT1 = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT2 = "yyyy-MM-dd";
    public static final String FORMAT3 = "yyyy";
    public static final String FORMAT4 = "yyyyMM";
    public static final String FORMAT5 = "MM-dd";
    public static final String FORMAT6 = "yyyy-MM";
    public static final String FORMAT7 = "yyyy年MM月dd日";
    public static final String FORMAT8 = "MM-dd HH:mm";
    public static final String FORMAT9 = "HH:mm";
    public static final String FORMAT10 = "yyyy-MM-dd HH:mm";
    public static final String FORMAT11 = "yyyy/MM/dd HH:mm:ss";

    private DateUtil() {
        //Utility Class
    }

    /**
     * 时间格式转化
     *
     * @param date     时间
     * @param fromType 转化格式
     * @param toType   目标格式
     * @return String
     */
    public static String format(String date, String fromType, String toType) {
        final SimpleDateFormat fromFormat = new SimpleDateFormat(fromType, Locale.CHINA);
        final SimpleDateFormat toFormat = new SimpleDateFormat(toType, Locale.CHINA);
        try {
            return toFormat.format(fromFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
            return "parse error";
        }
    }

    /**
     * @param fromType 需要转化的类型
     * @return 时间戳
     */
    public static long formatDate(String source, String fromType) {
        try {
            DateFormat format = new SimpleDateFormat(fromType, Locale.CHINA);
            Date date = format.parse(source);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 优惠活动剩余时间
     *
     * @param arg 结束时间
     */
    public static String getRemainTime(String arg) {
        final long currentTime = DateUtil.formatDate(new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(new Date
                ()), "yyyy-MM-dd");
        final long endTime = DateUtil.formatDate(arg, "yyyy-MM-dd");
        final long remainTime = endTime - currentTime;
        StringBuilder sb = new StringBuilder();
        if (remainTime < 0) {
            sb.append("该活动已过期");
        } else {
            sb.append("剩余");
            sb.append("<font color=#6adc37>");
            int count = (int) (1 + remainTime / (24 * 3600 * 1000));
            sb.append(count);
            sb.append("</font>天");
        }
        return sb.toString();
    }

    /**
     * 时间格式转化
     *
     * @param date   时间戳
     * @param toType 转化类型
     */
    public static String format(long date, String toType) {
        final SimpleDateFormat format = new SimpleDateFormat(toType, Locale.CHINA);
        return format.format(new Date(date));
    }

    /**
     * 时间显示规则[聊天\动态等]
     *
     * @param when 时间戳
     */
    public static String timeRule(long when) {
        if (DateUtils.isToday(when)) {//今天
            return format(when, FORMAT9);
        } else {
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(when);
            //显示时间
            int thenYear = calendar.get(Calendar.YEAR);
            int thenMonth = calendar.get(Calendar.MONTH);
            int thenMonthDay = calendar.get(Calendar.DATE);
            //当前时间
            calendar.setTimeInMillis(System.currentTimeMillis());
            if (thenYear == calendar.get(Calendar.YEAR) &&
                    thenMonth == calendar.get(Calendar.MONTH) &&
                    thenMonthDay + 1 == calendar.get(Calendar.DATE)) {//昨天
                return String.format(Locale.CHINA, "昨天 %s", format(when, FORMAT9));
            } else if (thenYear == calendar.get(Calendar.YEAR) &&
                    thenMonth == calendar.get(Calendar.MONTH) &&
                    thenMonthDay + 2 == calendar.get(Calendar.DATE)) {//前天
                return String.format(Locale.CHINA, "前天 %s", format(when, FORMAT9));
            } else if (thenYear == calendar.get(Calendar.YEAR)) {//今年的其他日期
                return format(when, FORMAT8);
            } else {//非今年的日期
                return format(when, FORMAT10);
            }
        }
    }

    public static long currentDateDiff(String fTime){
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        long currentTime = c.getTimeInMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date fDate = format.parse(fTime);
            return fDate.getTime() - currentTime;
        }catch (ParseException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    public static String dateDiff(long diff) {

        long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
        long nh = 1000 * 60 * 60;// 一小时的毫秒数
        long nm = 1000 * 60;// 一分钟的毫秒数
        long ns = 1000;// 一秒钟的毫秒数

        long day = diff / nd;// 计算差多少天
        long hour = diff % nd / nh + day * 24;// 计算差多少小时
        long min = diff % nd % nh / nm + day * 24 * 60;// 计算差多少分钟
        long sec = diff % nd % nh % nm / ns;// 计算差多少秒

        long endHour = hour - day * 24;
        long endMin = min - day * 24 * 60;

        StringBuffer sBuffer = new StringBuffer();

        if (day!=0) {
            sBuffer.append(day).append("天");
        }
        if (endHour!=0) {
            sBuffer.append(endHour).append("小时");
        }
        if (endMin!=0) {
            sBuffer.append(endMin).append("分钟");
        }
        if (sec!=0) {
            sBuffer.append(sec).append("秒");
        }
        return sBuffer.toString();
    }

}