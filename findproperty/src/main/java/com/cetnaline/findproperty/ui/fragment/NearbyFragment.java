package com.cetnaline.findproperty.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MapViewLayoutParams;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviParaOption;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.base.BaseFragment;
import com.cetnaline.findproperty.entity.bean.AroundPoiOverlay;
import com.cetnaline.findproperty.inter.BaiduMarkerClick;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.cetnaline.findproperty.ui.activity.NearbyActivity;
import com.cetnaline.findproperty.utils.BaiduNaviUtil;
import com.cetnaline.findproperty.utils.DataHolder;
import com.cetnaline.findproperty.utils.MyUtils;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by fanxl2 on 2016/8/3.
 */
public class NearbyFragment extends BaseFragment implements OnGetPoiSearchResultListener {

	private double latitude;
	private double longitude;
	public static final String LATITUDE_KEY = "LATITUDE_KEY";
	public static final String LONGITUDE_KEY = "LONGITUDE_KEY";

	public static NearbyFragment getInstance(double latitude, double longitude){
		NearbyFragment nearbyFragment = new NearbyFragment();
		Bundle bundle = new Bundle();
		bundle.putDouble(LATITUDE_KEY, latitude);
		bundle.putDouble(LONGITUDE_KEY, longitude);
		nearbyFragment.setArguments(bundle);
		return nearbyFragment;
	}

	@BindView(R.id.map_nearby)
	MapView map_nearby;

	@BindView(R.id.toolbar)
	Toolbar toolbar;

	@BindView(R.id.nearby_rg)
	RadioGroup nearby_rg;

	private BaiduMap mBaiduMap;
	private UiSettings mUiSettings;

	protected ArrayList<Overlay> overlayList;

	//打开地图，默认放大到的地图级别
	private final int FIRST_SHOW_ZOOM = 16;

	private DataHolder dataHolder;

	private View poiView;
	private int vHeight;

	@Override
	protected int getLayoutId() {
		return R.layout.frag_nearby;
	}

	@Override
	protected void init() {

		vHeight = MyUtils.dip2px(getContext(), 20)+10;
		dataHolder = DataHolder.getInstance();

		toolbar.setTitle("");
		((NearbyActivity)getActivity()).setToolbar(toolbar);

		latitude = getArguments().getDouble(LATITUDE_KEY);
		longitude = getArguments().getDouble(LONGITUDE_KEY);

		//隐藏放大缩小按钮
		map_nearby.showZoomControls(false);
		//隐藏比例尺
		map_nearby.showScaleControl(false);
		mBaiduMap = map_nearby.getMap();
		mUiSettings = mBaiduMap.getUiSettings();
		//隐藏指南针
		mUiSettings.setCompassEnabled(false);
		mUiSettings.setOverlookingGesturesEnabled(false);
		mUiSettings.setRotateGesturesEnabled(false);

		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(this);

		overlayList = new ArrayList<>();

		toLocation(0, latitude, longitude);

		estLatLng = new LatLng(latitude, longitude);

		aroundPoiOverlay = new AroundPoiOverlay(getActivity(), mBaiduMap, estLatLng, new BaiduMarkerClick() {
			@Override
			public boolean onMarkerClick(Marker marker) {
				marker.setToTop();
				mBaiduMap.hideInfoWindow();
				if (aroundPoiOverlay.getPoiResult() != null) {
					PoiInfo poiInfo = aroundPoiOverlay.getPoiResult().getAllPoi().get(marker.getExtraInfo().getInt("index"));
					switch (poiInfo.type) {
						case BUS_LINE:
						case BUS_STATION:
						case SUBWAY_LINE:
						case SUBWAY_STATION:
							updatePoiPop(poiInfo, 0);
							break;
						default:
							updatePoiPop(poiInfo, 1);
							break;
					}
				}
				return false;
			}
		});
		mBaiduMap.setOnMarkerClickListener(aroundPoiOverlay);

		poiView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_map_around_poi, null);
		poiView.setVisibility(View.GONE);
		map_nearby.addView(poiView);

		MarkerOptions ooA = new MarkerOptions().position(estLatLng)
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_detail_marker));

		mBaiduMap.addOverlay(ooA);

		nearby_rg.setOnCheckedChangeListener( (radioGroup, checkId) -> {
			lastPosition = checkId;
			switch (lastPosition){
				case R.id.nearby_rb_bus:
					search("公交");
					break;
				case R.id.nearby_rb_metro:
					search("地铁");
					break;
				case R.id.nearby_rb_school:
					search("学校");
					break;
				case R.id.nearby_rb_house:
					search("楼盘");
					break;
				case R.id.nearby_rb_hospital:
					search("医院");
					break;
				case R.id.nearby_rb_restaurant:
					search("餐馆");
					break;
				case R.id.nearby_rb_bank:
					search("银行");
					break;
			}

		});

	}

	private PoiSearch mPoiSearch;
	private LatLng estLatLng;
	private String keyWord;
	private int lastPosition = -1;
	private AroundPoiOverlay aroundPoiOverlay;


	private void search(String key){
		if (showPoiView) {
			poiView.setVisibility(View.GONE);
			showPoiView = false;
		}
		showLoadingDialog();
		keyWord = key;
		mPoiSearch.searchNearby(new PoiNearbySearchOption()
				.keyword(key)
				.location(estLatLng)
				.radius(5000)
				.sortType(PoiSortType.distance_from_near_to_far));
	}


	/**
	 * 定位到指定的坐标
	 * @param radius
	 * @param latitude
	 * @param longitude
	 */
	private void toLocation(float radius, double latitude, double longitude){
		MyLocationData locData = new MyLocationData.Builder()
				.accuracy(radius)
				// 此处设置开发者获取到的方向信息，顺时针0-360
				.direction(100).latitude(latitude)
				.longitude(longitude).build();

		mBaiduMap.setMyLocationData(locData);

		LatLng ll = new LatLng(latitude, longitude);

		MapStatus.Builder builder = new MapStatus.Builder();
		builder.target(ll);
		builder.zoom(FIRST_SHOW_ZOOM);
		mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
	}

	@Override
	protected IPresenter createPresenter() {
		return null;
	}

	@Override
	public void onGetPoiResult(PoiResult poiResult) {
		cancelLoadingDialog();
		if (poiResult == null
				|| poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
			((NearbyActivity)getActivity()).toast("附近没有搜到"+keyWord);
			return;
		}

		if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {
			int resource;
			switch (lastPosition){
				case R.id.nearby_rb_bus:
					resource = R.drawable.ic_map_bus;
					break;
				case R.id.nearby_rb_metro:
					resource = R.drawable.ic_map_metro;
					break;
				case R.id.nearby_rb_school:
					resource = R.drawable.ic_map_school;
					break;
				case R.id.nearby_rb_house:
					resource = R.drawable.ic_map_build;
					break;
				case R.id.nearby_rb_hospital:
					resource = R.drawable.ic_map_hospital;
					break;
				case R.id.nearby_rb_restaurant:
					resource = R.drawable.ic_map_restaurant;
					break;
				case R.id.nearby_rb_bank:
					resource = R.drawable.ic_map_bank;
					break;
				default:
					resource = 0;
					break;
			}
			aroundPoiOverlay.setPoiResult(poiResult, resource);
			aroundPoiOverlay.addToMap();
		}


	}

	@Override
	public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
		if (poiDetailResult.error != SearchResult.ERRORNO.NO_ERROR) {
			((NearbyActivity)getActivity()).toast("附近没有搜到"+keyWord);
		}
	}

	@Override
	public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

	}

	@OnClick(R.id.nearby_fb_nav)
	public void navClick(){
		navi4Baidu();
	}

	private void navi4Baidu(){
		NaviParaOption naviParaOption = new NaviParaOption();
		naviParaOption.startPoint(new LatLng(dataHolder.getLatitude(), dataHolder.getLongitude()))
				.endPoint(estLatLng);
		try {
			BaiduMapNavigation.setSupportWebNavi(true);
			BaiduMapNavigation.openBaiduMapNavi(naviParaOption, getActivity());
		} catch (BaiduMapAppNotSupportNaviException e) {
			e.printStackTrace();
//			((NearbyActivity)getActivity()).toast("当前没有安装百度地图或者百度地图版本过低");

			//打开百度地图网页导航
			Uri mapUri = Uri.parse(BaiduNaviUtil.getBaiduMapUri(String.valueOf(dataHolder.getLatitude()),
					String.valueOf(dataHolder.getLongitude()), "我的位置", String.valueOf(estLatLng.latitude), String.valueOf(estLatLng.longitude), "终点", "上海市", getActivity().getPackageName() + "|上海中原"));
			Intent loction = new Intent(Intent.ACTION_VIEW, mapUri);
			getActivity().startActivity(loction);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	private boolean showPoiView;

	private void updatePoiPop(PoiInfo poiInfo, int type) {
		AppCompatTextView atv_name = (AppCompatTextView) poiView.findViewById(R.id.atv_name);
		atv_name.setText(type == 0 ?
				String.format(Locale.CHINA, "%s\n%s", poiInfo.name, poiInfo.address) :
				String.format("%s(%s)", poiInfo.name, poiInfo.address));
		poiView.setVisibility(View.VISIBLE);
		MapViewLayoutParams params = new MapViewLayoutParams.Builder()
				.width(MapViewLayoutParams.WRAP_CONTENT)
				.height(MapViewLayoutParams.WRAP_CONTENT)
				.layoutMode(MapViewLayoutParams.ELayoutMode.mapMode)
				.position(poiInfo.location)
				.yOffset(-vHeight)
				.build();
		map_nearby.updateViewLayout(poiView, params);
		showPoiView = true;
	}

	@Override
	public void onResume() {
		map_nearby.onResume();
		super.onResume();
	}

	@Override
	public void onPause() {
		map_nearby.onPause();
		super.onPause();
	}

	@Override
	public void onDestroy() {
		map_nearby.onDestroy();
		super.onDestroy();
	}
}
