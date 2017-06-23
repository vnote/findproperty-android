package com.cetnaline.findproperty.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import java.net.URISyntaxException;

/**
 * Created by diaoqf on 2017/6/2.
 */

public class BaiduNaviUtil {

    /**
     * 打开百度导航
     * @param context
     * @param origin
     * @param destination
     * @param mode
     * @param region
     * @param origin_region
     * @param destination_region
     * @param coord_type
     * @param zoom
     * @param src
     */
    public static  void goToNaviActivity(Context context, String origin , String destination  , String mode , String region , String origin_region , String destination_region
            , String coord_type , String zoom , String src){
        StringBuffer stringBuffer  = new StringBuffer("intent://map/direction?origin=");
        stringBuffer.append(origin)
                .append("&destination=").append(destination)
                .append("&mode=").append(mode);
        if (!TextUtils.isEmpty(region)){
            stringBuffer.append("&region=").append(region);
        }
        if (!TextUtils.isEmpty(origin_region)){
            stringBuffer.append("&origin_region=").append(origin_region);
        }
        if (!TextUtils.isEmpty(destination_region)){
            stringBuffer.append("&destination_region=").append(destination_region);
        }
        if (!TextUtils.isEmpty(coord_type)){
            stringBuffer.append("&coord_type=").append(coord_type);
        }


        if (!TextUtils.isEmpty(zoom)){
            stringBuffer.append("&zoom=").append(zoom);
        }
        stringBuffer.append("&src=").append(src).append("#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
        String intentString = stringBuffer.toString();
        try {
            Intent intent  = Intent.getIntent(intentString);
            context.startActivity(intent);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


    }

    /**
     * 获取百度导航uri
     * @param originLat
     * @param originLon
     * @param originName
     * @param desLat
     * @param desLon
     * @param destination
     * @param region
     * @param src
     * @return
     */
    public static String getBaiduMapUri(String originLat, String originLon, String originName, String desLat, String desLon, String destination, String region, String src){
        String uri = "http://api.map.baidu.com/direction?origin=latlng:%1$s,%2$s|name:%3$s" +
                "&destination=latlng:%4$s,%5$s|name:%6$s&mode=walking&region=%7$s&output=html" + "&src=%8$s";
        return String.format(uri, originLat, originLon, originName, desLat, desLon, destination, region, src);
    }

}
