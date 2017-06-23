package com.cetnaline.findproperty.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.Selection;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiBoundSearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.cetnaline.findproperty.AppContents;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.base.BaseActivity;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.cetnaline.findproperty.ui.fragment.DeputeSourceInfoFragment;
import com.cetnaline.findproperty.utils.StatusBarCompat;


import butterknife.BindView;
/**
 * 地图点选小区
 * Created by diaoqf on 2017/4/5.
 */

public class EstateMapSelectActivity extends BaseActivity {

    public static final String ESTATE_NAME = "estate_name";
    public static final String ESTATE_ADDRESS = "estate_address";

    @BindView(R.id.back_img)
    ImageView back_img;
    @BindView(R.id.search_edt)
    EditText search_edt;
    @BindView(R.id.bmapView)
    MapView bmapView;

    private BaiduMap map;

    private PoiSearch poiSearch;

    private String keyword;

//    private boolean needLocat = true;

    @Override
    protected int getContentViewId() {
        return R.layout.act_estate_select;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        keyword = getIntent().getStringExtra(ESTATE_ADDRESS);
        back_img.setOnClickListener(v->onBackPressed());

        search_edt.setOnEditorActionListener((v, keyCode, event) -> {
            if(keyCode== EditorInfo.IME_ACTION_SEARCH){
                String s=search_edt.getText().toString().trim();
                if (s != null && !"".equals(s)) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(search_edt.getWindowToken(), 0);
                    keyword = search_edt.getText().toString();
                    searchData();
                }
                return true;
            }
            return false;
        });

        map = bmapView.getMap();
        map.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        map.setMyLocationEnabled(true);
        map.setMaxAndMinZoomLevel(21,8);

        map.setOnMarkerClickListener(marker -> {
            Bundle bundle = marker.getExtraInfo();
            Intent intent = new Intent();
            intent.putExtra("name", bundle.getString("name"));
            intent.putExtra("address", bundle.getString("address"));
            intent.putExtra("lat", bundle.getDouble("lat"));
            intent.putExtra("lng", bundle.getDouble("lng"));
            setResult(DeputeSourceInfoFragment.LOCATION_RESULT_CODE_1, intent);
            finish();
            return true;
        });

        poiSearch = PoiSearch.newInstance();
        poiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {
//                Logger.i("buobao:onGetPoiResult");
                if (poiResult.error == SearchResult.ERRORNO.NO_ERROR && poiResult != null && poiResult.getAllPoi().size() > 0 ) {
                    addMarker(poiResult);
//                    for (PoiInfo info:poiResult.getAllPoi()) {
//                        Logger.i("buobao:"+info.name);
//                    }
                }
                if (poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
                    toast("未找到小区，请重新搜索或选择周边位置");
                }
                cancelLoadingDialog();
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
//                Logger.i("buobao:onGetPoiDetailResult");
            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
//                Logger.i("buobao:onGetPoiIndoorResult");
            }
        });

        map.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {
            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
//                if (!needLocat) {
//                    searchData();
//                } else {
//                    needLocat = false;
//                }
            }
        });
    }

    /**
     * 添加marker
     * @param poiResult
     */
    private void addMarker(PoiResult poiResult) {
        map.clear();
        int allData = poiResult.getAllPoi().size();
        LatLng minLatLng = new LatLng(poiResult.getAllPoi().get(0).location.latitude, poiResult.getAllPoi().get(0).location.longitude);
        LatLng maxLatLng = new LatLng(poiResult.getAllPoi().get(0).location.latitude, poiResult.getAllPoi().get(0).location.longitude);

        double minLat = poiResult.getAllPoi().get(0).location.latitude;
        double maxLat = poiResult.getAllPoi().get(0).location.latitude;
        double minLng = poiResult.getAllPoi().get(0).location.longitude;
        double maxLng = poiResult.getAllPoi().get(0).location.longitude;

        for (PoiInfo info : poiResult.getAllPoi()) {
            if (info.location != null && info.type == PoiInfo.POITYPE.POINT) {
                View layout = LayoutInflater.from(EstateMapSelectActivity.this).inflate(R.layout.estate_map_layout, null);
                AppCompatTextView textView = (AppCompatTextView) layout.findViewById(R.id.map_name);
                textView.setText(info.name);
                BitmapDescriptor bitmap = BitmapDescriptorFactory.fromView(layout);
                OverlayOptions options = new MarkerOptions()
                        .position(info.location)
                        .icon(bitmap)
                        .zIndex(9)
                        .draggable(true);
                Marker marker = (Marker) map.addOverlay(options);
                Bundle bundle = new Bundle();
                bundle.putString("name", info.name);
                bundle.putString("address", info.address);
                bundle.putDouble("lat", info.location.latitude);
                bundle.putDouble("lng", info.location.longitude);
                marker.setExtraInfo(bundle);
                marker.setZIndex(allData--);

                if (info.location.latitude < minLat) {
                    minLat = info.location.latitude;
                }
                if (info.location.latitude > maxLat) {
                    maxLat = info.location.latitude;
                }
                if (info.location.longitude < minLng) {
                    minLng = info.location.longitude;
                }
                if (info.location.longitude > maxLng) {
                    maxLng = info.location.longitude;
                }
            }
        }
//        if (needLocat) {
            moveToLocation(new LatLng(minLat,minLng), new LatLng(maxLat, maxLng));  //定位到第一个搜索结果位置
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        bmapView.onResume();
        locatUser();
        bmapView.postDelayed(() -> searchData(),800);
        search_edt.setText(getIntent().getStringExtra(ESTATE_NAME));
        Editable etext = search_edt.getText();
        Selection.setSelection(etext, etext.length());
    }

    @Override
    protected void onPause() {
        bmapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        bmapView.onDestroy();
        poiSearch.destroy();
        super.onDestroy();
    }

    @Override
    protected IPresenter createPresenter() {
        return null;
    }

    @Override
    protected void initToolbar() {
        showToolbar(false);
        StatusBarCompat.setStatusBarColor(this, Color.WHITE);
    }

    /**
     * 获取当前显示的地图范围poi数据
     */
    private void searchData() {
        map.clear();
        if (keyword == null || keyword.equals("")) {
            return;
        }

        //当前显示区域
//        int b = bmapView.getBottom();
//        int t = bmapView.getTop();
//        int r = bmapView.getRight();
//        int l = bmapView.getLeft();
//        LatLng ne = map.getProjection().fromScreenLocation(new Point(r,t));
//        LatLng sw = map.getProjection().fromScreenLocation(new Point(l,b));

        LatLng ne = new LatLng(AppContents.CITY_LAT + 0.2,AppContents.CITY_LNG + 0.3);
        LatLng sw = new LatLng(AppContents.CITY_LAT - 0.2,AppContents.CITY_LNG - 0.3);
        showLoadingDialog();
        poiSearch.searchInBound(new PoiBoundSearchOption()
                .bound(new LatLngBounds.Builder()
                        .include(ne)
                        .include(sw)
                        .build())
                .keyword(keyword)
                .pageNum(0)
                .pageCapacity(8));
    }

    /**
     * 定位当前用户位置
     */
    private void locatUser(){
        LatLng location = new LatLng(AppContents.CITY_LAT,AppContents.CITY_LNG);
        moveToLocation(new LatLng(location.latitude-0.05, location.longitude-0.05), new LatLng(location.latitude + 0.05, location.longitude + 0.05));
        showUserLocation(location);
    }

    /**
     * 显示用户位置
     * @param location
     */
    private void showUserLocation(LatLng location) {
        // 显示个人位置图标
        MyLocationData.Builder builder = new MyLocationData.Builder();
        builder.latitude(location.latitude);
        builder.longitude(location.longitude);
        MyLocationData data = builder.build();
        map.setMyLocationData(data);
    }

    /**
     * 移动到指定坐标
     */
    private void moveToLocation(LatLng min, LatLng max) {
        MapStatusUpdate mMapStatusUpdate =  MapStatusUpdateFactory.zoomTo(21f);
        map.animateMapStatus(mMapStatusUpdate);
//        mMapStatusUpdate = MapStatusUpdateFactory.newLatLng(location);
        mMapStatusUpdate = MapStatusUpdateFactory.newLatLngBounds(new LatLngBounds.Builder().include(min).include(max).build());
        map.animateMapStatus(mMapStatusUpdate);
    }

    @Override
    protected String getTalkingDataPageName() {
        return "委托小区坐标选择页面";
    }
}
