package com.cetnaline.findproperty.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MapViewLayoutParams;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.cetnaline.findproperty.AppContents;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.bean.StoreBo;
import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.base.BaseActivity;
import com.cetnaline.findproperty.db.entity.Store;
import com.cetnaline.findproperty.entity.event.LocationRequestEvent;
import com.cetnaline.findproperty.entity.event.NormalEvent;
import com.cetnaline.findproperty.entity.ui.StoremapEntity;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.cetnaline.findproperty.utils.DataHolder;
import com.cetnaline.findproperty.utils.DbUtil;
import com.cetnaline.findproperty.utils.LocationUtil;
import com.cetnaline.findproperty.utils.MyUtils;
import com.cetnaline.findproperty.utils.RxBus;
import com.cetnaline.findproperty.utils.SchedulersCompat;
import com.cetnaline.findproperty.utils.StatusBarCompat;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindDrawable;
import butterknife.BindView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by diaoqf on 2016/10/18.
 */
public class StoreSearchActivity extends BaseActivity {
    @BindView(R.id.back_img)
    ImageView back_img;
    @BindView(R.id.search_edt)
    TextView search_edt;
    @BindView(R.id.exchange_img)
    ImageView exchange_img;

    @BindDrawable(R.drawable.ic_list_small)
    Drawable ic_list_small;

    @BindDrawable(R.drawable.ic_list_map)
    Drawable ic_list_map;

    @BindView(R.id.bmapView)
    MapView bmapView;

    @BindView(R.id.map_icon_location)
    AppCompatImageView map_icon_location;

    @BindView(R.id.store_list)
    LinearLayout store_list_layout;

    @BindView(R.id.list)
    ListView store_list;

    private BaiduMap map;
    private CompositeSubscription mCompositeSubscription;

    private StoreListAdapter mAdapter;
    private List<Map<String, String>> datas;
    private List<Map<String,String>> allDatas;

    private int showType = 0;

    private boolean isCleared;

    private View poiView;

    private void exchangeType(int type) {
        if (poiView != null) {
            poiView.setVisibility(View.GONE);
        }
        if (showType == type) {
            return;
        } else {
            showType = type;
            if (type == 0) {
                exchange_img.setImageDrawable(ic_list_small);
                store_list_layout.setVisibility(View.GONE);
                if (isCleared) {
                    isCleared = false;
                    map.clear();
                    for (Map<String, String> item:datas) {
                        addMaker(Double.parseDouble(item.get("lat")),
                                Double.parseDouble(item.get("lng")),
                                item.get("name"),
                                item.get("location"));
                    }
                    locatUser();
                }
            } else {
                exchange_img.setImageDrawable(ic_list_map);
                store_list_layout.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.act_store_search;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        isCleared = false;
        mCompositeSubscription = new CompositeSubscription();
        bmapView.showZoomControls(false);
        StatusBarCompat.setStatusBarColor(this, Color.WHITE);
        exchange_img.setOnClickListener(v->exchangeType(showType>0?0:1));

        back_img.setOnClickListener(v->onBackPressed());

        search_edt.setOnClickListener(v->{
            Intent intent = new Intent(StoreSearchActivity.this, StoreSearchDetailActivity.class);
            startActivity(intent);
        });

        map_icon_location.setOnClickListener(v->{
            locatUser();
//            loadData(new LatLng(DataHolder.getInstance().getLatitude(), DataHolder.getInstance().getLongitude()));
        });

        map = bmapView.getMap();
        map.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        map.setMyLocationEnabled(true);
        map.setMaxAndMinZoomLevel(21,14);
        map.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                LatLng location = mapStatus.target;
                if (!isCleared) {
                    loadData(location);
                }
            }
        });

        map.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Bundle bundle = marker.getExtraInfo();
                StoremapEntity entity = (StoremapEntity) bundle.getSerializable("info");

                if (poiView == null) {
                    poiView = LayoutInflater.from(StoreSearchActivity.this).inflate(R.layout.layout_map_around_poi, null);
                    poiView.setVisibility(View.GONE);
                    bmapView.addView(poiView);
                }

                poiView.setVisibility(View.VISIBLE);
                AppCompatTextView atv_name = (AppCompatTextView) poiView.findViewById(R.id.atv_name);
                atv_name.setText(String.format(Locale.CHINA, "%s\n%s", entity.getName(), entity.getDescription()));

                MapViewLayoutParams params = new MapViewLayoutParams.Builder()
                        .width(MapViewLayoutParams.WRAP_CONTENT)
                        .height(MapViewLayoutParams.WRAP_CONTENT)
                        .layoutMode(MapViewLayoutParams.ELayoutMode.mapMode)
                        .position(new LatLng(entity.getLatitude(),entity.getLongitude()))
                        .yOffset(-MyUtils.dip2px(StoreSearchActivity.this, 25))
                        .build();
                bmapView.updateViewLayout(poiView, params);

                return true;
            }
        });

        locatUser();

        datas = new ArrayList<>();
        allDatas = new ArrayList<>();
        mAdapter = new StoreListAdapter(this);
        store_list.setAdapter(mAdapter);

        mCompositeSubscription.add(Observable.just(DbUtil.getAllStore())
                .flatMap(new Func1<List<Store>, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(List<Store> stores) {
                        if (stores != null && stores.size() > 0) {
                            for (Store bo : stores) {
                                String distance_str = "";
                                double distance = MyUtils.getDistance(new LatLng(bo.getLat(),bo.getLng()),new LatLng(DataHolder.getInstance().getLatitude(),DataHolder.getInstance().getLongitude()));
                                if (distance < 1) {
                                    distance_str = MyUtils.format2(distance * 1000) + "m";
                                } else {
                                    distance_str = MyUtils.format2(distance) + "km";
                                }
                                String finalDistance_str = distance_str;
                                allDatas.add(new HashMap(){
                                    {
                                        put("order",distance+"");
                                        put("id", bo.getStoreId()+"");
                                        put("name", bo.getStoreName());
                                        put("location", bo.getStoreAddr());
                                        put("distance","距离:"+ finalDistance_str);
                                        put("lat", bo.getLat()+"");
                                        put("lng", bo.getLng()+"");
                                        put("tel",bo.getStore400Tel());
                                    }
                                });
                            }
                            orderData();
                            return Observable.just(true);
                        } else {
                            return Observable.just(false);
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        mAdapter.notifyDataSetChanged();
                    } else {
                        toast("未能读取门店信息");
                    }
                }));

        //监听用户点击指定的门店（列表）
        mCompositeSubscription.add(RxBus.getDefault().toObservable(NormalEvent.class)
                .subscribe(normalEvent -> {
                            if (normalEvent.type == NormalEvent.SHOW_STORE_LOCATION) {
                                exchangeType(0);
                                String[] msg = normalEvent.data.split(";");

                                LatLng latLng = new LatLng(Double.parseDouble(msg[0]), Double.parseDouble(msg[1]));
                                double distance = MyUtils.getDistance(new LatLng(DataHolder.getInstance().getLatitude(), DataHolder.getInstance().getLongitude()),
                                        new LatLng(Double.parseDouble(msg[0]), Double.parseDouble(msg[1])));
                                moveToLocation(latLng);
                                map.clear();
                                String dis_str = "";
                                if (distance < 1) {
                                    dis_str = "距离:" + MyUtils.format2Integer(distance * 1000) + "m";
                                } else {
                                    dis_str = "距离:" + MyUtils.format2String(distance) + "km";
                                }
                                addMaker(Double.parseDouble(msg[0]), Double.parseDouble(msg[1]), msg[2], dis_str);
                                isCleared = true;
                            }
                        }));

        //rxbus监听
        mCompositeSubscription.add(RxBus.getDefault().toObservable(LocationRequestEvent.class)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(locationRequestEvent -> {
                    if (!locationRequestEvent.getRequestSource().equals(getClass().getName())) {
                        return;
                    }

                    LatLng location;
                    if (locationRequestEvent.getRequestResult() == LocationRequestEvent.REQUEST_SUCCESS) {
                        location = new LatLng(DataHolder.getInstance().getLatitude(), DataHolder.getInstance().getLongitude());
                        moveToLocation(location);
                        showUserLocation(location);
                    }else {
//                        location = new LatLng(AppContents.CITY_LAT,AppContents.CITY_LNG);
                        toast("未获得定位权限，请设置“允许”后尝试");
                    }

                },throwable -> throwable.printStackTrace()));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0 ) {
            if (grantResults != null && grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //请求权限，开始定位
                if (!isCleared) {
                    LocationUtil.start(1, true, getClass().getName());
                }
            } else {
                toast("未获得定位权限，请设置“允许”后尝试");
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void locatUser(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
    }

    /**
     * 加载数据
     */
    private void loadData(LatLng location) {
        showLoadingDialog();
        if (poiView != null) {
            poiView.setVisibility(View.GONE);
        }

        //读取本地缓存
        mCompositeSubscription.add(Observable.just(DbUtil.getStoreByLocation(location,2800))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stores -> {
                    cancelLoadingDialog();
                    if (stores != null && stores.size() > 0) {
                        map.clear();
                        datas.clear();
                        for (Store bo : stores) {
                            addMaker(bo.getLat(), bo.getLng(), bo.getStoreName(), bo.getStoreAddr());
                            String distance_str = "";
                            double distance = MyUtils.getDistance(new LatLng(bo.getLat(),bo.getLng()),
                                    new LatLng(DataHolder.getInstance().getLatitude(),DataHolder.getInstance().getLongitude()));
                            if (distance < 1) {
                                distance_str = MyUtils.format2(distance * 1000) + "m";
                            } else {
                                distance_str = MyUtils.format2(distance) + "km";
                            }
                            String finalDistance_str = distance_str;
                            datas.add(new HashMap(){
                                {
                                    put("order",distance+"");
                                    put("id", bo.getStoreId()+"");
                                    put("name", bo.getStoreName());
                                    put("location", bo.getStoreAddr());
                                    put("distance","距离:"+ finalDistance_str);
                                    put("lat", bo.getLat()+"");
                                    put("lng", bo.getLng()+"");
                                    put("tel",bo.getStore400Tel());
                                }
                            });
                        }
//                        orderData();
//                        mAdapter.notifyDataSetChanged();
                    } else {
                        toast("没有查询到门店信息");
                    }
                },throwable -> {
                    cancelLoadingDialog();
                    throwable.printStackTrace();
                }));
    }

    /**
     * 为门店按照距离排序
     */
    private void orderData(){
        long s = System.currentTimeMillis();
        for (int i = allDatas.size()-1;i>=0; i--) {
            for (int j=0;j<i;j++) {
                if (Double.parseDouble(allDatas.get(j).get("order")) > Double.parseDouble(allDatas.get(j+1).get("order"))) {
                    Map<String,String> tmp = allDatas.get(j);
                    allDatas.set(j,allDatas.get(j+1));
                    allDatas.set(j+1,tmp);
                }
            }
        }
        long e = System.currentTimeMillis();
        Logger.i("排序时间:"+(e-s));

//        Collections.sort(allDatas, new Comparator<Map<String, String>>() {
//            @Override
//            public int compare(Map<String, String> o1, Map<String, String> o2) {
//                return Double.parseDouble(o1.get("order")) > Double.parseDouble(o2.get("order")) ? 1:0;
//            }
//        });
    }

    private void addMaker(double lat, double lng, String name, String address){
        StoremapEntity entity = new StoremapEntity(lat, lng, name, address);
        LatLng latLng = new LatLng(lat, lng);
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromView(LayoutInflater.from(StoreSearchActivity.this).inflate(R.layout.store_overlay,null));
        Marker marker;
        OverlayOptions options;
        options = new MarkerOptions()
                .position(latLng)//设置位置
                .icon(bitmap)//设置图标样式
                .zIndex(9) // 设置marker所在层级
                .draggable(true); // 设置手势拖拽;

        //添加marker
        marker = (Marker) map.addOverlay(options);
        //使用marker携带info信息，当点击事件的时候可以通过marker获得info信息
        Bundle bundle = new Bundle();
        //info必须实现序列化接口
        bundle.putSerializable("info", entity);
        marker.setExtraInfo(bundle);
    }


    /**
     * 移动到指定坐标
     * @param location
     */
    private void moveToLocation(LatLng location) {
//        MapStatus mMapStatus = new MapStatus.Builder().target(location).zoom(14).build();
        MapStatusUpdate mMapStatusUpdate =  MapStatusUpdateFactory.zoomTo(16f);
        // 放大
        map.animateMapStatus(mMapStatusUpdate);
        mMapStatusUpdate = MapStatusUpdateFactory.newLatLng(location);
        // 移动到某经纬度
        map.animateMapStatus(mMapStatusUpdate);

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

    @Override
    protected IPresenter createPresenter() {
        return null;
    }

    @Override
    protected void initToolbar() {
        showToolbar(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bmapView.onDestroy();
        if (mCompositeSubscription != null && mCompositeSubscription.hasSubscriptions()) {
            mCompositeSubscription.unsubscribe();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        bmapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        bmapView.onPause();
    }

//    public CompositeSubscription getCompositeSubscription() {
//        return mCompositeSubscription;
//    }


    public class StoreListAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        public StoreListAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return allDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder=new ViewHolder();

                convertView = mInflater.inflate(R.layout.list_store_item, null);
                holder.name = (TextView) convertView.findViewById(R.id.store_name);
                holder.location = (TextView)convertView.findViewById(R.id.store_location);
                holder.distance = (TextView)convertView.findViewById(R.id.store_distance);
                holder.phone_img = (ImageView) convertView.findViewById(R.id.store_phone);
                holder.location_img = (ImageView) convertView.findViewById(R.id.store_loc);
                holder.staff_img = (ImageView) convertView.findViewById(R.id.store_staff);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder)convertView.getTag();
            }

            holder.name.setText(allDatas.get(position).get("name"));
            holder.location.setText(allDatas.get(position).get("location"));
            holder.distance.setText(allDatas.get(position).get("distance"));


            holder.phone_img.setOnClickListener(v->{
                String phone = allDatas.get(position).get("tel");
                if (phone == null || "".equals(phone)) {
                    mCompositeSubscription.add(ApiRequest.searchStore(new HashMap(){
                        {
                            put("StoreID", allDatas.get(position).get("id"));
                        }
                    }).subscribe(new Action1<List<StoreBo>>() {
                        @Override
                        public void call(List<StoreBo> storeBos) {
                            if (storeBos != null) {
                                allDatas.get(position).put("tel", storeBos.get(0).getStore400Tel());
                                MyUtils.toCall400(StoreSearchActivity.this, allDatas.get(position).get("tel"), allDatas.get(position).get("name"));
                            }
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }));
                } else {
                    MyUtils.toCall400(StoreSearchActivity.this, allDatas.get(position).get("tel"), allDatas.get(position).get("name"));
                }
            });

            holder.location_img.setOnClickListener(v->{
                exchangeType(0);
                LatLng latLng = new LatLng(Double.parseDouble(allDatas.get(position).get("lat")), Double.parseDouble(allDatas.get(position).get("lng")));
                moveToLocation(latLng);
                map.clear();
                addMaker(Double.parseDouble(allDatas.get(position).get("lat")), Double.parseDouble(allDatas.get(position).get("lng")),allDatas.get(position).get("name"),allDatas.get(position).get("distance"));
                isCleared = true;
            });
            holder.staff_img.setOnClickListener(v->{
                Intent intent = new Intent(StoreSearchActivity.this, StoreStaffListActivity.class);
                intent.putExtra(StoreStaffListActivity.STORE_NAME, allDatas.get(position).get("name"));
                intent.putExtra(StoreStaffListActivity.STORE_ID, allDatas.get(position).get("id"));
                StoreSearchActivity.this.startActivity(intent);
            });

            return convertView;
        }

        public final class ViewHolder{
            public TextView name;
            public TextView location;
            public TextView distance;

            public ImageView phone_img;
            public ImageView location_img;
            public ImageView staff_img;
        }
    }

    @Override
    protected String getTalkingDataPageName() {
        return "门店搜索";
    }
}
