package com.cetnaline.findproperty.utils;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.AppCompatTextView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapFragment;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.busline.BusLineResult;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.db.entity.RailLine;
import com.cetnaline.findproperty.db.entity.RailWay;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 用于显示一条地铁详情结果的Overlay,自己定义的
 * Created by Vctor on 16/2/4.
 */
public class RailwayLineOverlay extends OverlayManager {

    private CircleOptions circleOptions;
    private Overlay circleOverlay;
    private RailLine railLine = null;

    protected View view;
    protected AppCompatTextView atv_content;

    private LatLngBounds bounds;

    /**
     * 构造函数
     *
     * @param baiduMap 该BusLineOverlay所引用的 BaiduMap 对象
     */
    public RailwayLineOverlay(Context context, BaiduMap baiduMap) {
        super(baiduMap);
        view = LayoutInflater.from(context).inflate(R.layout.layout_map_railway, null);
        atv_content = (AppCompatTextView) view.findViewById(R.id.map_rail_name);
    }

    public LatLng getLineCenter() {
        if (railLine != null && railLine.getRailWayList().size() > 0) {
            int i = railLine.getRailWayList().size();
            return new LatLng(railLine.getRailWayList().get(i/2).getLat(), railLine.getRailWayList().get(i/2).getLng());
        } else {
            return null;
        }
    }

    /**
     * 设置公交线数据
     *
     * @param result 公交线路结果数据
     */
    public void setData(RailLine result) {
        this.railLine = result;
        railLine.setRailWayList(DbUtil.getRailWayByRailLineId(railLine.getRailLineID()+""));

        double startX = -1,startY = -1;
        double endX = -1, endY = -1;
        for (RailWay railWay : railLine.getRailWayList()) {
            if (startX < 0 || startX > railWay.getLat()) {
                startX = railWay.getLat();
            }
            if (startY < 0 || startY > railWay.getLng()) {
                startY = railWay.getLng();
            }
            if (endX < 0 || endX < railWay.getLat()) {
                endX = railWay.getLat();
            }
            if (endY < 0 || endY < railWay.getLng()) {
                endY = railWay.getLng();
            }
        }
        bounds = new LatLngBounds.Builder().include(new LatLng(startX,startY)).include(new LatLng(endX,endY)).build();
    }

    public LatLngBounds getBounds() {
        return bounds;
    }

    /**
     * 定位到具体站点
     */
    public void setStation(String stationName) {
        if (railLine == null || railLine.getRailWayList() == null) {
            return;
        }
        for (RailWay station : railLine.getRailWayList()) {
            if (stationName.contains(station.getRailWayName()) ||
                    station.getRailWayName().contains(stationName)) {
                LatLng latLng = new LatLng(station.getLat(),station.getLng());
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(
                       latLng , 17));

                //添加地铁站圆形覆盖
                if (circleOverlay != null) {
                    circleOverlay.remove();
                }
                if (circleOptions == null) {
                    circleOptions = new CircleOptions();
                    circleOptions.radius(1000);
                    circleOptions.fillColor(0x77FFFFFF);
                    circleOptions.stroke(new Stroke(5, 0xFFFFFFFF));
                }
                circleOptions.center(latLng);
                circleOverlay = mBaiduMap.addOverlay(circleOptions);

//                atv_content.setText(stationName);
//
//                OverlayOptions option = new MarkerOptions()
//                        .position(station.getLocation())
//                        .icon(BitmapDescriptorFactory.fromView(view));
//
//                mOverlayList.add(mBaiduMap.addOverlay(option));

                break;
            }
        }
    }

    /**
     * 显示线路
     */
    private OverlayOptions ooPolyline1;
    private OverlayOptions ooPolyline2;
    public void showLine() {
        ooPolyline1 = null;
        ooPolyline2 = null;
        ArrayList<LatLng> points1 = new ArrayList<>();
        ArrayList<LatLng> points2 = new ArrayList<>();
        for (RailWay way : railLine.getRailWayList()) {
            if (way.getBranchNum() == null || way.getBranchNum().equals("1") || way.getBranchNum().equals("3")) {
                points1.add(new LatLng(way.getLat(),way.getLng()));
            }

            if ("2".equals(way.getBranchNum()) || "3".equals(way.getBranchNum())) {
                points2.add(new LatLng(way.getLat(),way.getLng()));
            }
        }

        ooPolyline1 = new PolylineOptions().width(12)
                .color(Color.rgb(255,143,33)).points(points1);
        mBaiduMap.addOverlay(ooPolyline1);

        if (points2.size() >= 2) {
            ooPolyline2 = new PolylineOptions().width(12)
                    .color(Color.rgb(255,143,33)).points(points2);
            mBaiduMap.addOverlay(ooPolyline2);
        }
    }

    @Override
    public final List<OverlayOptions> getOverlayOptions() {
        if (railLine == null || railLine.getRailWayList() == null) {
            return null;
        }
        List<OverlayOptions> overlayOptionses = new ArrayList<>();
        for (RailWay station : railLine.getRailWayList()) {
            atv_content.setText(station.getRailWayName());
            overlayOptionses.add(new MarkerOptions()
                    .position(new LatLng(station.getLat(),station.getLng()))
                    .anchor(0.5f, 1f)
                    .zIndex(10)
                    .icon(BitmapDescriptorFactory
                            .fromView(view)));
        }

//        List<LatLng> points = new ArrayList<>();
//        for (BusLineResult.BusStep step : mBusLineResult.getSteps()) {
//            if (step.getWayPoints() != null) {
//                points.addAll(step.getWayPoints());
//            }
//        }
//        if (points.size() > 0) {
//            overlayOptionses
//                    .add(new PolylineOptions().width(14)
//                            .color(Color.argb(200, 241, 62, 6)).zIndex(0)
//                            .points(points));
//        }

        if (ooPolyline1 != null) {
            overlayOptionses.add(ooPolyline1);
        }
        if (ooPolyline2 != null) {
            overlayOptionses.add(ooPolyline2);
        }

        return overlayOptionses;
    }

    /**
     * 覆写此方法以改变默认点击行为
     *
     * @param index 被点击的站点在
     *              {@link BusLineResult#getStations()}
     *              中的索引
     * @return 是否处理了该点击事件
     */
    public boolean onBusStationClick(int index) {
        if (railLine.getRailWayList() != null
                && railLine.getRailWayList().get(index) != null) {

            Logger.i(railLine.getRailWayList().get(index).getRailWayName());
//            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(
//                    mBusLineResult.getStations().get(index).getLocation(), 16f));
            if (railWayClick!=null){
                railWayClick.railWayClick(railLine.getRailWayList().get(index).getRailWayName());
            }

        }
        return false;
    }

    public final boolean onMarkerClick(Marker marker) {
        if (mOverlayList != null && mOverlayList.contains(marker)) {
            return onBusStationClick(mOverlayList.indexOf(marker));
        } else {
            return false;
        }

    }

    @Override
    public boolean onPolylineClick(Polyline polyline) {
        return false;
    }

    private OnRailWayClick railWayClick;

    public void setOnRailWayClick(OnRailWayClick railWayClick){
        this.railWayClick=railWayClick;
    }

    public interface OnRailWayClick{
        void railWayClick(String name);
    }



}
