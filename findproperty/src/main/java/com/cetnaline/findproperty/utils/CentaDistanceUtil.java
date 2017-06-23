package com.cetnaline.findproperty.utils;

import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import java.util.Locale;

/**
 * 距离计算
 * Created by guilin on 16/1/11.
 */
public class CentaDistanceUtil {

    private CentaDistanceUtil() {
        //Utility Class
    }

    /**
     * 距离计算,控件显示
     *
     * @param view  AppCompatTextView
     * @param start 开始位置
     * @param end   结束位置
     */
    public static void distanceView(AppCompatTextView view, LatLng start, LatLng end) {
        if (start == null) {
            view.setVisibility(View.GONE);
        } else {
            view.setText(distance(start, end));
            view.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 距离计算
     *
     * @param start 开始位置
     * @param end   结束位置
     * @return 距离
     */
    public static String distance(LatLng start, LatLng end) {
        final StringBuilder result = new StringBuilder();
        final double distance = DistanceUtil.getDistance(start, end);
        if (distance > 20000) {
            result.append(">20km");
        } else if (distance > 1000) {
            result.append(String.format(Locale.CHINA, "%.1fkm", distance / 1000));
        } else if (distance > 100) {
            result.append(String.format(Locale.CHINA, "%.0fm", distance));
        } else {
            result.append("<100m");
        }
        return result.toString();
    }

    /**
     * 拼接房源到地铁站字段
     */
    public static String distanceOfPostAndMetro(String railLineName, String railWayName, int distance) {
        return String.format(Locale.CHINA, "距离地铁%s%s站%d米", railLineName, railWayName, distance);
    }
}
