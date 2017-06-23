package com.cetnaline.findproperty.entity.bean;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.PoiResult;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.inter.BaiduMarkerClick;

import java.util.ArrayList;
import java.util.List;

/**
 * 周边功能poi
 * Created by guilin on 16/4/19.
 */
public class AroundPoiOverlay implements BaiduMap.OnMarkerClickListener {

    private static final int MAX_POI_SIZE = 20;

    final BaiduMap baiduMap;
    final LatLng estLatLng;
    final BaiduMarkerClick baiduMarkerClick;

    private View view;
    private ImageView point;

    private int resource;
    private PoiResult poiResult;
    private ArrayList<Overlay> overlayArrayList = new ArrayList<>();

    public AroundPoiOverlay(Context context, BaiduMap baiduMap, LatLng estLatLng, BaiduMarkerClick baiduMarkerClick) {
        this.baiduMap = baiduMap;
        this.estLatLng = estLatLng;
        this.baiduMarkerClick = baiduMarkerClick;
        view = LayoutInflater.from(context).inflate(R.layout.layout_around_poi_point, null);
        point = (ImageView) view;
    }

    public PoiResult getPoiResult() {
        return poiResult;
    }

    public void setPoiResult(PoiResult poiResult, int resource) {
        this.poiResult = poiResult;
        this.resource = resource;
    }

    public void addToMap() {
        if (baiduMap == null ||
                poiResult == null)
            return;
        removeFromMap();
        List<PoiInfo> poiInfos = poiResult.getAllPoi();
        if (poiInfos == null || poiInfos.size() == 0)
            return;
        int size = poiInfos.size();
        for (int i = 0; i < size && i < MAX_POI_SIZE; i++) {
            if (poiInfos.get(i).location == null ||
                    poiInfos.get(i).location.latitude == 0 ||
                    poiInfos.get(i).location.longitude == 0)
                continue;
            Bundle bundle = new Bundle();
            bundle.putInt("index", i);
            point.setImageResource(resource);
            overlayArrayList.add(baiduMap.addOverlay(new MarkerOptions()
                    .position(poiInfos.get(i).location)
                    .extraInfo(bundle)
                    .icon(BitmapDescriptorFactory.fromView(view))));
        }
        zoomToSpan();
    }

    public final void removeFromMap() {
        if (baiduMap == null)
            return;
        for (Overlay marker : overlayArrayList) {
            marker.remove();
        }
        overlayArrayList.clear();
    }

    public void zoomToSpan() {
        if (baiduMap == null ||
                overlayArrayList == null ||
                overlayArrayList.size() == 0)
            return;
        if (overlayArrayList.size() > 0) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(estLatLng);
            for (Overlay overlay : overlayArrayList) {
                if (overlay instanceof Marker) {
                    builder.include(((Marker) overlay).getPosition());
                }
            }
            baiduMap.animateMapStatus(MapStatusUpdateFactory
                    .newLatLngBounds(builder.build()));
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return overlayArrayList != null &&
                overlayArrayList.contains(marker) &&
                baiduMarkerClick.onMarkerClick(marker);
    }
}
