package com.cetnaline.findproperty.utils;

import android.content.Context;
import android.location.Location;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.cetnaline.findproperty.AppContents;
import com.cetnaline.findproperty.entity.event.LocationRequestEvent;
import com.cetnaline.findproperty.entity.event.NormalEvent;
import com.orhanobut.logger.Logger;

import java.util.List;

/**
 * 百度定位工具
 * Created by diaoqf on 2016/7/15.
 */
public class LocationUtil {

    //实时定位
    public static final int LOC_FOREVER = -1;
    //定位一次
    public static final int LOC_ONCE = 1;

    private static LocationClient mLocationClient = null;
    private static BDLocationListener myListener = new MyLocationListener();

    //定位日志输出设定
    private static boolean openLocationLog = true;

    private static boolean saveLocationInfo = false;
    //定位返回码
    public static int errorCode;
    // 当次定位的纬度
    public static double latitude;
    //当次定位的经度
    public static double longitude;
    //当次定位具体地址
    public static String address;
    //当次定位城市名称
    public static String city;
    //当次定位城市code
    public static String cityCode;

    private static int locTime = LOC_ONCE;

    private static String sourceFrom;

    private LocationUtil() {
    }

    public static void init(Context context) {
        if (mLocationClient == null) {
            mLocationClient = new LocationClient(context);
            mLocationClient.registerLocationListener(myListener);

            LocationClientOption option = new LocationClientOption();
            option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
            option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
            int span=10000;
            option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
            option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
            option.setOpenGps(true);//可选，默认false,设置是否使用gps
            option.setProdName("FindProperty");
            option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
            option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
            option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
            option.setIgnoreKillProcess(true);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
            option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
            option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
            mLocationClient.setLocOption(option);
        }
    }

    /**
     * 开始定位
     * @param locationTimes 定位次数
     * @param saveLocation 是否保存定位信息
     * @param source 标识请求定位的页面
     */
    public static void start(int locationTimes, boolean saveLocation, String source){
        locTime = locationTimes;
        sourceFrom = source;
        saveLocationInfo = saveLocation;
        if (mLocationClient == null) return;
        if (mLocationClient.isStarted()) {
            mLocationClient.requestLocation();
        } else {
            mLocationClient.start();
        }
    }

    /**
     * 结束定位
     */
    public static void stop(){
        mLocationClient.stop();
    }

    public static void setLogger(boolean opened) {
        openLocationLog = opened;
    }

    /**
     * 获取定位返回码信息
     * @return
     */
    public static String getResultMessage(){
        switch (errorCode) {
            case 61:return null;
            case 62:return "无法获取有效定位依据";
            case 63:return "网络异常";
            case 65:return "定位缓存的结果";
            case 66:return "离线定位结果";
            case 67: return "离线定位失败";
            case 68: return "网络连接失败";
            case 161: return null;
            case 162: return "请求串密文解析失败";
            case 167: return "服务端定位失败";
            case 502: return "key参数错误";
            case 505: return  "key不存在或者非法";
            case 601: return "key服务被开发者自己禁用";
            case 602: return "key mcode不匹配";
        }

        return "未知错误";
    }

    public static class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {

            LocationRequestEvent locationRequestEvent;
            if (location == null || location.getLatitude() < 1) {
                RxBus.getDefault().send(new LocationRequestEvent(sourceFrom,LocationRequestEvent.REQUEST_FAILED));
                mLocationClient.stop();
                return;
            } else {
                locationRequestEvent = new LocationRequestEvent(sourceFrom,LocationRequestEvent.REQUEST_SUCCESS);
            }

            //Receive Location
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            errorCode = location.getLocType();
            sb.append(errorCode);
            sb.append("\nlatitude : ");
            latitude = location.getLatitude();
            sb.append(latitude);
            sb.append("\nlontitude : ");
            longitude = location.getLongitude();
            sb.append(longitude);
            city = location.getCity();
            cityCode = location.getCityCode();
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());// 单位：公里每小时
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// 单位：米
                sb.append("\ndirection : ");
                sb.append(location.getDirection());// 单位度
                sb.append("\naddr : ");
                address = location.getAddrStr();
                sb.append(address);
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");

            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                sb.append("\naddr : ");
                address = location.getAddrStr();
                sb.append(address);
                //运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            sb.append("\nlocationdescribe : ");
            sb.append(location.getLocationDescribe());// 位置语义化信息
            List<Poi> list = location.getPoiList();// POI数据
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }
            if (openLocationLog) {
                Logger.i(sb.toString());
            }

            if (locTime > 0) {
                //结束定位
                if (--locTime == 0) {
                    //保存定位信息
                    if (saveLocationInfo && SharedPreferencesUtil.isInited()){
                        saveLocationInfo = false;
                        SharedPreferencesUtil.saveString(AppContents.LOCATION_ADDRESS, address);
                        SharedPreferencesUtil.saveString(AppContents.LOCATION_CITY, city);
                        SharedPreferencesUtil.saveString(AppContents.LOCATION_CITY_CODE, cityCode);

                        DataHolder.getInstance().setLatitude(latitude);
                        DataHolder.getInstance().setLongitude(longitude);
                        DataHolder.getInstance().setLocationRedius(location.getRadius());
                    }
                    mLocationClient.stop();
                }
            }
            RxBus.getDefault().send(locationRequestEvent);
        }
    }

}
