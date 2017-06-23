package com.cetnaline.findproperty.ui.fragment;

import android.Manifest;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.busline.BusLineResult;
import com.baidu.mapapi.search.busline.BusLineSearch;
import com.baidu.mapapi.search.busline.BusLineSearchOption;
import com.baidu.mapapi.search.busline.OnGetBusLineSearchResultListener;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.cetnaline.findproperty.AppContents;
import com.cetnaline.findproperty.BaseApplication;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.bean.EstateBo;
import com.cetnaline.findproperty.api.bean.EstateMapRequest;
import com.cetnaline.findproperty.api.bean.HouseBo;
import com.cetnaline.findproperty.api.bean.HouseBottomBean;
import com.cetnaline.findproperty.api.bean.NewEstMainMapBo;
import com.cetnaline.findproperty.api.bean.NewHouseMapDetail;
import com.cetnaline.findproperty.api.bean.RegionPostBo;
import com.cetnaline.findproperty.api.bean.SchoolBo;
import com.cetnaline.findproperty.api.bean.SearchParam;
import com.cetnaline.findproperty.base.LazyLoadFragment;
import com.cetnaline.findproperty.db.entity.DropBo;
import com.cetnaline.findproperty.db.entity.GScope;
import com.cetnaline.findproperty.db.entity.RailWay;
import com.cetnaline.findproperty.entity.event.LocationRequestEvent;
import com.cetnaline.findproperty.entity.event.MapPopEvent;
import com.cetnaline.findproperty.entity.event.TipClickEvent;
import com.cetnaline.findproperty.entity.event.TopViewEvent;
import com.cetnaline.findproperty.highline.Guide;
import com.cetnaline.findproperty.highline.GuideBuilder;
import com.cetnaline.findproperty.highline.IconComponent;
import com.cetnaline.findproperty.highline.ListComponent;
import com.cetnaline.findproperty.inter.BaiduMarkerClick;
import com.cetnaline.findproperty.api.NetContents;
import com.cetnaline.findproperty.presenter.impl.MapPresenter;
import com.cetnaline.findproperty.presenter.ui.MapContract;
import com.cetnaline.findproperty.ui.activity.HouseList;
import com.cetnaline.findproperty.ui.activity.MainTabActivity;
import com.cetnaline.findproperty.ui.activity.SearchActivity;
import com.cetnaline.findproperty.ui.activity.VillageDetail;
import com.cetnaline.findproperty.ui.adapter.HouseFragmentAdapter;
import com.cetnaline.findproperty.utils.DataHolder;
import com.cetnaline.findproperty.utils.DbUtil;
import com.cetnaline.findproperty.utils.LocationUtil;
import com.cetnaline.findproperty.utils.MyUtils;
import com.cetnaline.findproperty.utils.RailwayLineOverlay;
import com.cetnaline.findproperty.utils.RxBus;
import com.cetnaline.findproperty.utils.SchedulersCompat;
import com.cetnaline.findproperty.utils.SharedPreferencesUtil;
import com.cetnaline.findproperty.utils.StatusBarCompat;
import com.cetnaline.findproperty.widgets.DrawView;
import com.cetnaline.findproperty.widgets.bottomwindow.ExpandablePager;
import com.cetnaline.findproperty.widgets.dropdown.AreaDrop;
import com.cetnaline.findproperty.widgets.dropdown.DropCompleteListener;
import com.cetnaline.findproperty.widgets.dropdown.DropDownView;
import com.cetnaline.findproperty.widgets.dropdown.MultiSelectDrop;
import com.cetnaline.findproperty.widgets.dropdown.PriceDrop;
import com.cetnaline.findproperty.widgets.dropdown.SingleDrop;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;


/**
 * 房源地图
 * Created by fanxl2 on 2016/7/25.
 */
public class MapFragment extends LazyLoadFragment<MapPresenter> implements MapContract.View,
		BaiduMap.OnMapLoadedCallback, BaiduMap.OnMapStatusChangeListener, BaiduMap.OnMarkerClickListener {

	private static final String TAG = "MapFragment";

	public static final String IS_FIRST_IN_1 = "IS_FIRST_IN_1";                //圈画动画引导 标志
	public static final String IS_FULL_HIDE_FIRST = "IS_FULL_HIDE_FIRST";  //菜单隐藏动画引导 标志

	@BindView(R.id.map_house_mv)
	MapView map_house_mv;

	@BindView(R.id.map_tv_list)
	TextView map_tv_list;

	@BindView(R.id.map_ep_house)
	ExpandablePager map_ep_house;

	@BindView(R.id.map_dv)
	DrawView map_dv;

	@BindView(R.id.map_ll_tool)
	LinearLayout map_ll_tool;

	@BindView(R.id.map_ll_title)
	LinearLayout map_ll_title;

	@BindView(R.id.map_drop_menu)
	DropDownView map_drop_menu;

	@BindDrawable(R.drawable.ic_map_drop_four)
	Drawable ic_map_drop_four;

	@BindDrawable(R.drawable.ic_map_drop_one)
	Drawable ic_map_drop_one;

	@BindDrawable(R.drawable.ic_map_drop_three)
	Drawable ic_map_drop_three;

	@BindDrawable(R.drawable.ic_map_drop_two)
	Drawable ic_map_drop_two;

	@BindView(R.id.map_ib_search)
	ImageView map_ib_search;

	private BaiduMap mBaiduMap;
	private UiSettings mUiSettings;

	protected float startZoom;//开始zoom级别
	protected float endZoom;//开始zoom级别

	protected BaiduMarkerClick baiduMarkerClick;
	protected ArrayList<Overlay> overlayList;
	private ArrayList<Marker> markerList;

	private List<RegionPostBo> baseMapDatas;
	private CompositeSubscription mCompositeSubscription;
	private int houseNumber;

	private List<Marker> selectedOverlay;

	//小区数据
	private List<EstateBo> esfEstateDoList;

	private DropCompleteListener dropCompleteListener = new DropCompleteListener() {

		@Override
		public void complete(int position, boolean fromMore, int type, DropBo... dropBos) {
			dropMenuClose(position, dropBos);
		}
	};

	//房源类型
	private SingleDrop houseType;
	//总价
//	private SingleDrop priceDrop;
	private PriceDrop priceDrop;
	//区域
	private AreaDrop areaDrop;

	private int toastBottom;

	private MultiSelectDrop moreDrop;
//	private DoubleMultiDrop moreDrop;

	protected PoiSearch poiSearch;
	protected BusLineSearch busLineSearch;
	private Marker mLastMarker;

	private boolean isGetData;

	@Override
	protected int getLayoutId() {
		return R.layout.frag_map;
	}

	public static final int HOUSE_TYPE_SECOND = 0; //二手房
	public static final int HOUSE_TYPE_RENT = 1; //租房
	public static final int HOUSE_TYPE_NEW = 2; //新房
	public static final int HOUSE_TYPE_ESTATE = 3; //小区

	public static final String HOUSE_TYPE_KEY = "HOUSE_TYPE_KEY";

	private int currentHouseType;

	private Resources resources;
	public static final int MAP_INTENT_CODE = 102;

	protected View schoolView;
	protected AppCompatTextView school_tv_name;

	private long selectedRegionId, selectedGscopeId;

	@OnClick(R.id.map_tv_list)
	public void toMapList() {

//		startActivity(new Intent(getActivity(), TestActivity.class));

		MapStatus mapStatus = mBaiduMap.getMapStatus();
		double minLat = mapStatus.bound.southwest.latitude;
		double minLng = mapStatus.bound.southwest.longitude;
		double maxLat = mapStatus.bound.northeast.latitude;
		double maxLng = mapStatus.bound.northeast.longitude;

		Intent intent = new Intent(getActivity(), HouseList.class);
		intent.putExtra(HOUSE_TYPE_KEY, currentHouseType);
		intent.putExtra(HouseList.SEARCH_PARAM_KEY, searchData);
		intent.putExtra("MinLng", minLng);
		intent.putExtra("MinLat", minLat);
		intent.putExtra("MaxLat", maxLat);
		intent.putExtra("MaxLng", maxLng);
		startActivityForResult(intent, MAP_INTENT_CODE);
	}

	@OnClick(R.id.map_iv_hand)
	public void doHand() {

		if (isAnimation)return;

		if (maxHeight==0){
			maxHeight = map_ll_tool.getMeasuredHeight();
			minHeight = maxHeight/4;
		}

		if (isLocking) {
			if (iconClickP == 0) {
				//关闭画圈
				isLocking = false;
				mBaiduMap.clear();
				mapChange = true;
				iconMenuDo(0, false, map_iv_hand);
			} else {
				//画圈
				map_iv_school.setImageResource(R.drawable.icon_map_school);
				map_iv_metro.setImageResource(R.drawable.icon_map_metro);
				iconMenuDo(0, true, map_iv_hand);
			}
		} else {
			//画圈
			mapChange = false;
			isLocking = true;
			isUpdateMap = false;
			iconMenuDo(0, true, map_iv_hand);
		}

		//画圈找房指引
		if (!SharedPreferencesUtil.getBoolean(IS_FIRST_IN_1)){
			((MainTabActivity)getActivity()).setShadeType(0);
			((MainTabActivity) getActivity()).showShade(new View[]{map_ll_tool});
			SharedPreferencesUtil.saveBoolean(IS_FIRST_IN_1, true);
			mCompositeSubscription.add(Observable.timer(4,TimeUnit.SECONDS)
					.compose(SchedulersCompat.applyIoSchedulers())
					.subscribe(new Action1<Long>() {
						@Override
						public void call(Long aLong) {
							((MainTabActivity) getActivity()).hideShade();
						}
					},throwable -> {
						throwable.printStackTrace();
						((MainTabActivity) getActivity()).hideShade();
					}));
		}
	}

	@OnClick(R.id.map_iv_school)
	public void schoolClick() {

		if (isLocking) {
			if (iconClickP == 2) {
				isLocking = false;
				mBaiduMap.clear();
				isCircle = false;
				mapChange = true;
				searchData.remove("SchoolId");
				iconMenuDo(2, false, map_iv_school);
			} else {
				iconMenuDo(2, true, map_iv_school);
			}
		} else {
			iconMenuDo(2, true, map_iv_school);
		}
	}

	private int lastIconP;

	@OnClick(R.id.map_iv_metro)
	public void metroClick() {
		if (isLocking) {
			if (iconClickP == 1) {
				isLocking = false;
				mBaiduMap.clear();
				isCircle = false;
				mapChange = true;
				searchData.remove("RailLineId");
				searchData.remove("RailWayId");
				iconMenuDo(1, false, map_iv_metro);
			} else {
				iconMenuDo(1, true, map_iv_metro);
			}
		} else {
			iconMenuDo(1, true, map_iv_metro);
		}
	}

	private boolean isShowing;

	public void setShow(boolean isShow){
		this.isShowing = isShow;
	}

	@Override
	protected void lazyLoad() {

		toastBottom = MyUtils.dip2px(getActivity(), 100);

		toast_text = (TextView) inflater.inflate(R.layout.layout_text_toast, null);
		popupWindow = new PopupWindow(toast_text, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

		resources = getResources();
		poiSearch = PoiSearch.newInstance();
		poiSearch.setOnGetPoiSearchResultListener(onGetPoiSearchResultListener);
		busLineSearch = BusLineSearch.newInstance();
		busLineSearch.setOnGetBusLineSearchResultListener(onGetBusLineSearchResultListener);

		map_drop_menu.setDropCompleteListener(dropCompleteListener);
		//房型
		houseType = new SingleDrop(map_drop_menu, getActivity(), ic_map_drop_one);
		List<DropBo> dropBos = new ArrayList<>();
		dropBos.add(new DropBo(resources.getString(R.string.map_query_1_1), "S", HOUSE_TYPE_SECOND));
		dropBos.add(new DropBo(resources.getString(R.string.map_query_1_2), "R", HOUSE_TYPE_RENT));
//		dropBos.add(new DropBo(resources.getString(R.string.map_query_1_3), "x", HOUSE_TYPE_NEW));
		houseType.init(dropBos);

		//总价
		priceDrop = new PriceDrop(map_drop_menu, getActivity(), ic_map_drop_three);
		//区域
		areaDrop = new AreaDrop(map_drop_menu, getActivity(), ic_map_drop_two);
		//更多
//		moreDrop = new DoubleMultiDrop(map_drop_menu, getActivity(), ic_map_drop_four);
		moreDrop = new MultiSelectDrop(map_drop_menu, getActivity(), ic_map_drop_four);

		map_drop_menu.addDrops(houseType, areaDrop, priceDrop, moreDrop);

		if (DbUtil.getGscpoeCount()>0){
			List<GScope> gScopes = DbUtil.getGScopeChild(21);
			for (GScope gScope : gScopes) {
				List<GScope> childs = DbUtil.getGScopeChild(gScope.getGScopeId());
				GScope noGscope = new GScope();
				noGscope.setGScopeName(resources.getString(R.string.map_query_no_requirement));
				noGscope.setGScopeAlias(gScope.getGScopeName());
				noGscope.setGScopeId(gScope.getGScopeId());
				noGscope.setGScopeLevel(2);
				noGscope.setParentId(0);
				childs.add(0, noGscope);
				gScope.setgScopeList(childs);
			}

			GScope noGscope = new GScope();
			noGscope.setGScopeName(resources.getString(R.string.map_query_no_requirement));
			noGscope.setGScopeId(-1);
			noGscope.setParentId(-1);
			noGscope.setGScopeLevel(-1);
			noGscope.setgScopeList(new ArrayList<>());
			gScopes.add(0, noGscope);

			areaDrop.init(gScopes);
		}

		if (DbUtil.getSearchDataCount()>0){
			updateDropMenu(HOUSE_TYPE_SECOND);
		}

		mCompositeSubscription = new CompositeSubscription();

		//隐藏放大缩小按钮
		map_house_mv.showZoomControls(false);
		//隐藏比例尺
		map_house_mv.showScaleControl(false);

		//隐藏指南针
		mUiSettings.setCompassEnabled(false);
		mUiSettings.setOverlookingGesturesEnabled(false);
		mUiSettings.setRotateGesturesEnabled(false);

		mBaiduMap.setMaxAndMinZoomLevel(20f, 11f);

		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		mBaiduMap.setOnMapStatusChangeListener(this);
		mBaiduMap.setOnMarkerClickListener(this);
		mBaiduMap.setOnMapLoadedCallback(this);

		mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
			@Override
			public void onMapClick(LatLng latLng) {
				Logger.i("地图点击:"+isShowing);
				if (isAnimation || isCircle)return;
				if (isShowing){
					setAnimationStatus(true);
					hiddenBottomWindow(false);
				}
				if (!SharedPreferencesUtil.getBoolean(IS_FULL_HIDE_FIRST)) {
					((MainTabActivity)getActivity()).setShadeType(1);
					((MainTabActivity)getActivity()).showShade(new View[]{map_ll_tool,map_tv_list});
					SharedPreferencesUtil.saveBoolean(IS_FULL_HIDE_FIRST,true);
				}
			}

			@Override
			public boolean onMapPoiClick(MapPoi mapPoi) {
				return false;
			}
		});

		railwayLineOverlay = new RailwayLineOverlay(getContext(), mBaiduMap);
		mBaiduMap.setOnMarkerClickListener(railwayLineOverlay);
		railwayLineOverlay.setOnRailWayClick(new RailwayLineOverlay.OnRailWayClick() {
			@Override
			public void railWayClick(String name) {
				RailWay railWay = DbUtil.getRailWayByNameAndLineId(railLineId, name);
				if (railWay!=null){
					getEsfByRailWay(railLineId, railLineName, railWay.getRailWayID()+"", railWay.getRailWayName());
				}else {
					toast("该站点查询失败");
				}
			}
		});

		//默认到市中心
		toLocation(0, AppContents.CITY_LAT, AppContents.CITY_LNG, true);

		mCompositeSubscription.add(RxBus.getDefault().toObservable(TipClickEvent.class)
				.subscribe(tipClickEvent -> {

					if (tipClickEvent.getType()==0){
						if (iconGuide!=null)iconGuide.dismiss();
					}else if (tipClickEvent.getType()==1){
						if (listGuide!=null)listGuide.dismiss();
					}

				}));

		mCompositeSubscription.add(RxBus.getDefault().toObservable(TopViewEvent.class)
				.subscribe(event -> {
//					if (event.isIntercept()){
//						Logger.i("请求拦截");
//					}
					map_ep_house.setIntercept(event.isIntercept());
					map_ep_house.setMoveY(event.isIntercept());
				}));

		mCompositeSubscription.add(RxBus.getDefault().toObservable(MapPopEvent.class)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(event -> {
					hiddenBottomWindow(false);
				}));

		overlayList = new ArrayList<>();
		selectedOverlay = new ArrayList<>();
		markerList = new ArrayList<>();

		//大头针点击监听
		baiduMarkerClick = marker -> {
			if (marker != null) {
				marker.setToTop();
				Bundle bundle = marker.getExtraInfo();
				if (bundle != null) {
					int markType = bundle.getInt(MARK_TYPE);
					if (markType == MARK_TYPE_REGION) {
						RegionPostBo regionPostBo = bundle.getParcelable(MAP_EXTRA_INFO);
						if (regionPostBo != null) {
							if (regionPostBo.getGScopeLevel() == 2) {
//									selectedGScopeId = regionPostBo.getGScopeId();
								mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(
										new LatLng(regionPostBo.getLat(), regionPostBo.getLng()), ZOOM_TO_GSCOPE));

							} else {
								mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(
										new LatLng(regionPostBo.getLat(), regionPostBo.getLng()), ZOOM_TO_ESTATE));
							}
						}
					} else if (markType == MARK_TYPE_ESFESTATE) {
						EstateBo esfEstateDo = bundle.getParcelable(MAP_EXTRA_INFO);
						if (esfEstateDo != null) {
							//上次点击的大头针
							if(mLastMarker != null){
								lastEsfView.setBackgroundResource(R.drawable.map_level);
								((AppCompatTextView)lastEsfView.findViewById(R.id.atv_mark_title)).setTextColor(getResources().getColor(R.color.est_selected));
								((AppCompatTextView)lastEsfView.findViewById(R.id.atv_mark_num)).setTextColor(getResources().getColor(R.color.est_selected));
								mLastMarker.setIcon(BitmapDescriptorFactory.fromView(lastEsfView));
							}
							//当前点击的大头针
							esfView.setBackgroundResource(R.drawable.map_level_select);
							((AppCompatTextView)esfView.findViewById(R.id.atv_mark_title)).setTextColor(getResources().getColor(R.color.white));
							((AppCompatTextView)esfView.findViewById(R.id.atv_mark_num)).setTextColor(getResources().getColor(R.color.white));
							atv_mark_title.setText(esfEstateDo.getEstateName());

							String houseCount ="";
							if(currentHouseType == HOUSE_TYPE_SECOND){   //二手房
								houseCount = esfEstateDo.getSaleNumber()+"";
							}else if(currentHouseType== HOUSE_TYPE_RENT){ //租房
								houseCount = esfEstateDo.getRentNumber()+"";
							}
							atv_mark_num.setText(houseCount);
							marker.setIcon(BitmapDescriptorFactory.fromView(esfView));
							//记录当前的位置
							mLastMarker = marker;
							lastEsfView = esfView;
							requestHoustList(esfEstateDo);
						}
					} else if (markType == MARK_TYPE_NEW_REGION) {
						NewEstMainMapBo regionPostBo = bundle.getParcelable(MAP_EXTRA_INFO);
						if (regionPostBo != null) {
							if (regionPostBo.getLevel() == 2) {
								mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(regionPostBo.getLatLng(), NEW_ZOOM_GSCOPE));
							} else {
//									updateNewHouseStatus(regionPostBo.getGscopId());
								mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(regionPostBo.getLatLng(), NEW_ZOOM_ESTATE));
							}
						}
					} else if (markType == MARK_TYPE_NEW_HOUSE) {
						NewHouseMapDetail houseDetail = bundle.getParcelable(MAP_EXTRA_INFO);
						if (houseDetail != null) {
							List<HouseBottomBean> bottomBeanList = new ArrayList<>();
							HouseBottomBean item = new HouseBottomBean(currentHouseType);
							item.setEstExtId(houseDetail.getEstExtId());
							item.setTitle(houseDetail.getAdName());
							bottomBeanList.add(item);
							houseFragmentAdapter.setItems(bottomBeanList, currentHouseType);
							if (map_ep_house.getVisibility() != View.VISIBLE) {
								((MainTabActivity) getActivity()).hiddenTab();
								map_ep_house.show(currentHouseType, houseDetail.getAdName());
							}
						}
					}

				}
			}
			return true;
		};

//		view = inflater.inflate(R.layout.layout_map_level, null);
		view = LayoutInflater.from(BaseApplication.getContext()).inflate(R.layout.layout_map_level,null);
		map_level_name = (AppCompatTextView) view.findViewById(R.id.map_level_name);
		map_level_number = (AppCompatTextView) view.findViewById(R.id.map_level_number);

		esfView = inflater.inflate(R.layout.layout_map_esf, null);
		atv_mark_title = (AppCompatTextView) esfView.findViewById(R.id.atv_mark_title);
		atv_mark_num = (AppCompatTextView) esfView.findViewById(R.id.atv_mark_num);

		schoolView = inflater.inflate(R.layout.layout_map_school, null);
		school_tv_name = (AppCompatTextView) schoolView.findViewById(R.id.map_rail_name);

		map_ep_house.setOnTitleClickListener(new ExpandablePager.OnTitleClickListener() {
			@Override
			public void leftClick() {
				Intent intent = new Intent(getActivity(), VillageDetail.class);
				intent.putExtra(VillageDetailFragment.ESTATE_CODE_KEY, estateCode);
				startActivity(intent);
			}

			@Override
			public void rightClick() {
				HashMap<String, SearchParam> param = new HashMap<>();
				SearchParam estateParam = new SearchParam();
				estateParam.setTitle("小区");
				estateParam.setKey("EstateCode");
				estateParam.setText(estateName);
				estateParam.setValue(estateCode);
				estateParam.setName(NetContents.ESTATE_NAME);
				param.put(estateParam.getKey(), estateParam);

				if (searchData!=null){
					param.putAll(searchData);
				}

				Intent intent = new Intent(getActivity(), HouseList.class);
				intent.putExtra(HouseList.SEARCH_PARAM_KEY, param);
				intent.putExtra(HOUSE_TYPE_KEY, currentHouseType);
				startActivity(intent);
			}

			@Override
			public void hide() {
				map_ll_tool.setVisibility(View.VISIBLE);
				map_tv_list.setVisibility(View.VISIBLE);
			}
		});

		houseFragmentAdapter = new HouseFragmentAdapter(getActivity().getSupportFragmentManager());
		map_ep_house.setAdapter(houseFragmentAdapter);

		map_dv.setOnDrawListener(new DrawView.OnDrawListener() {
			@Override
			public void finished() {

				List<PointF> path = map_dv.getScreenPath();
//				Logger.i("画圈数量:"+path.size());
				if(path.size()<10){
					toast("您画圈范围太小");
					return;
				}
				List<LatLng> pts = new ArrayList<>();
				for (PointF p : path) {
					Point pp = new Point((int) p.x, (int) p.y);
					LatLng pt = new LatLng(mBaiduMap.getProjection().fromScreenLocation(pp).latitude, mBaiduMap.getProjection().fromScreenLocation(pp).longitude);
					pts.add(pt);
				}

				map_dv.setVisibility(View.GONE);

				mPresenter.startAnimation(map_ll_title, false);

				OverlayOptions ooPolygon = new PolygonOptions().points(pts)
						.stroke(new Stroke(5, 0xAAfb2727)).fillColor(0xAAFFFF00);
				mBaiduMap.addOverlay(ooPolygon);
				map_dv.clear();
				requestHandCircle(pts);
			}
		});

		map_ib_search.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(getActivity(), SearchActivity.class));
			}
		});

//		if (!SharedPreferencesUtil.getBoolean(IS_FIRST_IN)){
//			mCompositeSubscription.add(Observable.timer(300, TimeUnit.MILLISECONDS)
//					.observeOn(AndroidSchedulers.mainThread())
//					.subscribe(time -> {
//						showIconGuide();
//					}));
//		}

		//定位成功后的事件处理
		mCompositeSubscription.add(
				RxBus.getDefault().toObservable(LocationRequestEvent.class)
						.compose(SchedulersCompat.applyIoSchedulers())
						.subscribe(locationRequestEvent -> {
							if (!locationRequestEvent.getRequestSource().equals(getClass().getName())) {
								return;
							}

							if (locationRequestEvent.getRequestResult() == LocationRequestEvent.REQUEST_SUCCESS) {
								if (isLocking) {
									map_iv_hand.setImageResource(R.drawable.icon_map_hand);
									map_iv_metro.setImageResource(R.drawable.icon_map_metro);
									map_iv_school.setImageResource(R.drawable.icon_map_school);

									if (iconClickP==0){
										startAnimation(false);
									}

									iconClickP = -1;

									isLocking = false;
									isCircle = false;
									mapChange = true;

									searchData.remove("SchoolId");
									searchData.remove("RailWayId");
									searchData.remove("RailLineId");

									mBaiduMap.clear();
								}

								changeMenu = true;

								searchParams.clear();

								if (currentHouseType==HOUSE_TYPE_SECOND){
									searchParams.put("PostType", "S");
								}else {
									searchParams.put("PostType", "R");
								}
								searchData.clear();

								removeData("GScopeId", null, 1, false);
								removeData("RegionId", null, 1, true);

								selectedRegionId = 0;
								selectedGscopeId = 0;

								areaDrop.clearMenu();

								toLocation(DataHolder.getInstance().getLocationRedius(),
										DataHolder.getInstance().getLatitude(),
										DataHolder.getInstance().getLongitude(), false);
							} else {
								toast("未获得定位权限，请设置“允许”后尝试");
							}
						},throwable -> {
							throwable.printStackTrace();
						}));
	}

	/**
	 * 显示板块下新房所有楼盘
	 * @param gscopId
	 */
	private void updateNewHouseStatus(long gscopId) {

		if (newHouseMapDetails == null) return;
		removeFromMap();
		//锁定地图
		isUpdateMap = false;

		houseNumber = 0;

		Observable.from(newHouseMapDetails)
				.filter(new Func1<NewHouseMapDetail, Boolean>() {
					@Override
					public Boolean call(NewHouseMapDetail esfEstateDo) {
						return gscopId == esfEstateDo.getGScopeId();
					}
				})
				.subscribe(esfEstateDo -> {
					overlayList.add(mBaiduMap.addOverlay(getMarkerOptionsForNew(esfEstateDo)));
				}, e -> {
					Logger.i("esfMapChange -->:" + e.getMessage());
				}, () -> {
					showToast("共" + houseNumber + resources.getString(R.string.map_query_alert_2));
				});

		zoomToSpan();

		Observable.timer(1000, TimeUnit.MILLISECONDS)
				.subscribe(time -> {
					isUpdateMap = true;
				});
	}

	private void updateDropMenu(int houseType) {
		switch (houseType) {
			case HOUSE_TYPE_SECOND:
				searchParams.put("PostType", "S");
				esfParams.put("PostType", "S");
//				esfParams.put("ImageWidth", "100");
//				esfParams.put("ImageHeight", "100");
				map_drop_menu.initTabs(R.array.mapSecond);
				//总价
				List<DropBo> priceDropDatas = DbUtil.getSearchDataByName("Sell");
				priceDrop.init(priceDropDatas, currentHouseType);

				setMoreDropData(0);
				break;
			case HOUSE_TYPE_RENT:
				searchParams.put("PostType", "R");
				esfParams.put("PostType", "R");
//				esfParams.put("ImageWidth", "100");
//				esfParams.put("ImageHeight", "100");
				map_drop_menu.initTabs(R.array.mapRent);
				//租金
				List<DropBo> rentDropDatas = DbUtil.getSearchDataByName("Rent");
				priceDrop.init(rentDropDatas, currentHouseType);

				setMoreDropData(0);
				break;
			case HOUSE_TYPE_NEW:
				map_drop_menu.initTabs(R.array.mapNew);
				//均价
				List<DropBo> aveDropDatas = DbUtil.getSearchDataByName("NewHousePriceN");
				priceDrop.init(aveDropDatas, currentHouseType);
				setMoreDropData(1);
				break;
		}


	}

	private void setMoreDropData(int type) {
		List<DropBo> moreList = new ArrayList<>();
		DropBo roomDrop = new DropBo(resources.getString(R.string.map_query_house_type), "0", 0);
		List<DropBo> rooms = DbUtil.getSearchDataByName("Room");
		if (rooms != null && rooms.size() > 0) {
			rooms.remove(0);
		}
		roomDrop.setChildrenList(rooms);
		moreList.add(roomDrop);

		//租房、二手房
		if (type == 0) {

			DropBo mianDrop = new DropBo(resources.getString(R.string.map_query_house_area), "0", 0);
			List<DropBo> areas = DbUtil.getSearchDataByName("Area");
			areas.remove(0);
			mianDrop.setChildrenList(areas);
			moreList.add(mianDrop);

			DropBo directionDrop = new DropBo(resources.getString(R.string.map_query_house_direction), "0", 0);
			List<DropBo> directions = DbUtil.getSearchDataByName("Direction");
			directions.remove(0);
			directionDrop.setChildrenList(directions);
			moreList.add(directionDrop);

			DropBo ageDrop = new DropBo(resources.getString(R.string.map_query_house_age), "0", 0);
			List<DropBo> ages = DbUtil.getSearchDataByName("HouseAge");
			ages.remove(0);
			ageDrop.setChildrenList(ages);
			moreList.add(ageDrop);

			DropBo fitDrop = new DropBo(resources.getString(R.string.map_query_house_fitment), "0", 0);
			List<DropBo> fitments = DbUtil.getSearchDataByName("Fitment");
			fitments.remove(0);
			fitDrop.setChildrenList(fitments);
			moreList.add(fitDrop);

			DropBo floorDrop = new DropBo(resources.getString(R.string.map_query_house_floor), "0", 0);
			List<DropBo> floors = DbUtil.getSearchDataByName("Floor");
			floors.remove(0);
			floorDrop.setChildrenList(floors);
			moreList.add(floorDrop);

			DropBo propertyDrop = new DropBo(resources.getString(R.string.map_query_house_property),"0",0);
			List<DropBo> propertys = DbUtil.getSearchDataByName("Property");
			propertys.remove(0);
			propertyDrop.setChildrenList(propertys);
			moreList.add(propertyDrop);

			DropBo tagDrop = new DropBo(resources.getString(R.string.map_query_house_feature), "0", 1);
			List<DropBo> tags = DbUtil.getSearchDataByName("SellTag");
			tagDrop.setChildrenList(tags);
			moreList.add(tagDrop);
		} else {
			//新房
			DropBo estDrop = new DropBo(resources.getString(R.string.map_query_house_property), "0", 0);
			List<DropBo> ests = DbUtil.getSearchDataByName("NewEstType");
			ests.remove(0);
			estDrop.setChildrenList(ests);
			moreList.add(estDrop);

			DropBo timeDrop = new DropBo(resources.getString(R.string.map_query_house_start), "0", 0);
			List<DropBo> times = DbUtil.getSearchDataByName("NewPropOpDate");
			times.remove(0);
			timeDrop.setChildrenList(times);
			moreList.add(timeDrop);

			DropBo tagDrop = new DropBo(resources.getString(R.string.map_query_house_feature), "0", 1);
			List<DropBo> tags = DbUtil.getSearchDataByName("NewPropFeatures");
			tags.remove(0);
			tagDrop.setChildrenList(tags);
			moreList.add(tagDrop);
		}

		moreDrop.init(moreList);
	}

	private String circleStr;

	private void requestHandCircle(List<LatLng> pts) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < pts.size(); i++) {
			LatLng ll = pts.get(i);
			sb.append(ll.latitude).append(",").append(ll.longitude);
			if (i != (pts.size() - 1)) {
				sb.append("|");
			}
		}

		circleStr = sb.toString();

		EstateMapRequest circleParam = new EstateMapRequest();
		circleParam.getPostFilter().setDrawCircle(circleStr);
		if (currentHouseType==MapFragment.HOUSE_TYPE_RENT){
			circleParam.getPostFilter().setPostType("R");
		}

		mPresenter.getNewEstByCircle(getActivity(), circleParam);

//		Map<String, String> param = new HashMap<>();
//		param.put("ImageWidth", "10");
//		param.put("ImageHeight", "10");
//		param.put("PageIndex", "0");
//		param.put("PageCount", "50");
//		param.put("ShowAll", "true");
//		param.put("DrawCircle", circleStr);
//
//		param.putAll(searchParams);

//		mPresenter.getEstByCircle(param);
	}

	private boolean isFull = false;

	//隐藏底部导航栏和打开列表栏
	public void hiddenBottomWindow(boolean changeFragment){
		hiddenBottomWindow(changeFragment, false);
	}

	public void hiddenBottomWindow(boolean changeFragment, boolean fromDetail) {
		if (map_ep_house.getVisibility() == View.VISIBLE) {
			if (!isCircle && !fromDetail){
				((MainTabActivity) getActivity()).showTab();
			}
			if (changeFragment){
				map_ep_house.setMainTabActivity((MainTabActivity) getActivity());
			}
			map_ep_house.hidden(changeFragment);
			setAnimationStatus(false);
		} else {
			if (isFull) {
				isFull = false;
				if (!isCircle){
					((MainTabActivity) getActivity()).showTab();
				}
			} else {
				isFull = true;
				if (!isCircle){
					((MainTabActivity) getActivity()).hiddenTab();
				}
			}

			if (iconClickP!=0){
				mPresenter.buildAndStartAnimation(map_ll_tool);
			}
			if (!isCircle){
				mPresenter.buildAndStartAnimation(map_tv_list);
			}
		}

		if(fromDetail)map_ep_house.show();
	}

	private void hiddenPop(){
		if (map_ep_house.getVisibility() == View.VISIBLE) {
			if (!isCircle){
				((MainTabActivity) getActivity()).showTab();
			}
			map_ep_house.hidden(false);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (requestCode == 0 ) {
			if (grantResults != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//				getLocation();
				LocationUtil.start(1,true, getClass().getName());
			} else {
				toast("未获得定位权限，请设置“允许”后尝试");
			}
		}
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}



	@OnClick(R.id.map_iv_location)
	public void iconLocation() {
		requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
	}

	//是否是锁定状态
	private boolean isLocking;

	@Override
	protected MapPresenter createPresenter() {
		return new MapPresenter();
	}

	@Override
	protected void init() {
		mBaiduMap = map_house_mv.getMap();
		mUiSettings = mBaiduMap.getUiSettings();
	}

	@Override
	public void showLoading() {
		showToast("房源加载中...", true);
//		showLoadingDialog();
	}

	@Override
	public void dismissLoading() {
//		dismissWindow();
		cancelLoadingDialog();
	}

	@Override
	public void showProgress() {
		showLoadingDialog();
	}

	@Override
	public void showError(String msg) {
		showToast(msg);
	}

	@Override
	public void setBaseMapData(List<RegionPostBo> regionList) {
		houseNumber = 0;
		baseMapDatas = regionList;
		isGetData = false;

		if (baseMapDatas==null){
			removeFromMap();
		}else {
			updateMap(mBaiduMap.getMapStatus(),true);
		}
	}

	private boolean isCircle = false;
	private boolean isTrain = false;
	private boolean isUpdateMap = true;

	public boolean isCircle() {
		return isCircle;
	}

	@Override
	public void setEsfEstateData(List<EstateBo> esfEstateDoList) {
		this.esfEstateDoList = esfEstateDoList;
		esfMapChange(mBaiduMap.getMapStatus(),true);
	}

	@Override
	public void setEstByCircle(List<EstateBo> esfList) {

		this.isCircle = true;

		isGetData = true;

		if (esfList == null || esfList.size() == 0) {
			iconClickP = lastIconP;
		} else {

			lastIconP = 0;
			this.esfEstateDoList = esfList;
			esfMapChange(mBaiduMap.getMapStatus(),true);
			zoomToSpan();
		}

	}

	@Override
	public void setHouseList(List<HouseBo> postDoList) {
		showHouse(postDoList);
	}

	@Override
	public void setRailWayEsfList(List<EstateBo> esfList, String railWayId) {

		isGetData = true;
		clearMenuData();
		lastIconP = 1;
		mBaiduMap.clear();
		mapChange = false;
		isLocking = true;
		isUpdateMap = false;
		map_iv_metro.setImageResource(R.drawable.ic_close_white_24dp);
		map_iv_hand.setImageResource(R.drawable.icon_map_hand);
		map_iv_school.setImageResource(R.drawable.icon_map_school);

		searchData.remove("SchoolId");

		//添加地铁站和地铁线searchParam
		if (railWayId!=null && !TextUtils.isEmpty(railWayId)){
			SearchParam searchWay = new SearchParam();
			searchWay.setId(Integer.parseInt(railWayId));
			searchWay.setText(stationName);
			searchWay.setValue(railWayId);
			searchWay.setTitle("站点");
			searchWay.setKey("RailWayId");
			searchWay.setPara("");
			searchWay.setName(NetContents.RAILWAY_NAME);
			searchData.put(searchWay.getKey(), searchWay);
		}

		if (esfList == null || esfList.size() == 0) {
			toast("地铁周边暂无房源");
			dismissWindow();
//			iconClickP = lastIconP;
		} else {
//			clearMenuData();
//			lastIconP = 1;
//			mBaiduMap.clear();
//			mapChange = false;
//			isLocking = true;
//			isUpdateMap = false;
//			map_iv_metro.setImageResource(R.drawable.ic_close_white_24dp);
//			map_iv_hand.setImageResource(R.drawable.icon_map_hand);
//			map_iv_school.setImageResource(R.drawable.icon_map_school);

			//地铁找房
			this.esfEstateDoList = esfList;
			esfMapChange(mBaiduMap.getMapStatus(),true);
//			zoomToSpan();
		}
	}

	//新房的区域数据
	private List<NewEstMainMapBo> newRegionList = new ArrayList<>();
	//新房的板块数据
	private List<NewEstMainMapBo> newGsopeList = new ArrayList<>();

	private List<NewHouseMapDetail> newHouseMapDetails;

	@Override
	public void setNewHouseBaseMapData(List<NewHouseMapDetail> baseMapData) {
		houseNumber = 0;
		newHouseMapDetails = baseMapData;

		newRegionList.clear();
		newGsopeList.clear();

		ArrayList<String> regionKey = new ArrayList<>();
		ArrayList<String> gscopKey = new ArrayList<>();
		NewEstMainMapBo newEstMainMapBo;
		for (NewHouseMapDetail newEstDo : baseMapData) {
			//区域
			String districtId = String.valueOf(newEstDo.getDistrictId());
			if (regionKey.contains(districtId)) {
				newRegionList.get(regionKey.indexOf(districtId)).add();
				if (newEstDo.getDistrict() != null) {
					newRegionList.get(regionKey.indexOf(districtId)).setName(
							newEstDo.getDistrict().getGScopeCnName());
					if (newEstDo.getDistrict().getLat() != 0 &&
							newEstDo.getDistrict().getLng() != 0) {
						newRegionList.get(regionKey.indexOf(districtId))
								.setLatLng(new LatLng(
										newEstDo.getDistrict().getLat(),
										newEstDo.getDistrict().getLng()));
					}
				}
			} else {
				regionKey.add(districtId);
				newEstMainMapBo = new NewEstMainMapBo(2, Long.parseLong(districtId));
				newEstMainMapBo.add();
				if (newEstDo.getDistrict() != null) {
					newEstMainMapBo.setName(newEstDo.getDistrict().getGScopeCnName());
					if (newEstDo.getDistrict().getLat() != 0 &&
							newEstDo.getDistrict().getLng() != 0) {
						newEstMainMapBo.setLatLng(new LatLng(
								newEstDo.getDistrict().getLat(),
								newEstDo.getDistrict().getLng()));
					}
				}
				newRegionList.add(newEstMainMapBo);
			}
			//板块
			String gscopId = String.valueOf(newEstDo.getGScopeId());
			if (gscopKey.contains(gscopId)) {
				newGsopeList.get(gscopKey.indexOf(gscopId)).add();
				if (newEstDo.getGScope() != null) {
					newGsopeList.get(gscopKey.indexOf(gscopId)).setName(
							newEstDo.getGScope().getGScopeCnName());
					if (newEstDo.getGScope().getLat() != 0 &&
							newEstDo.getGScope().getLng() != 0) {
						newGsopeList.get(gscopKey.indexOf(gscopId))
								.setLatLng(new LatLng(
										newEstDo.getGScope().getLat(),
										newEstDo.getGScope().getLng()));
					}
				}
			} else {
				gscopKey.add(gscopId);
				newEstMainMapBo = new NewEstMainMapBo(3, Long.parseLong(gscopId));
				newEstMainMapBo.add();
				if (newEstDo.getGScope() != null) {
					newEstMainMapBo.setName(newEstDo.getGScope().getGScopeCnName());
					if (newEstDo.getGScope().getLat() != 0 &&
							newEstDo.getGScope().getLng() != 0) {
						newEstMainMapBo.setLatLng(new LatLng(
								newEstDo.getGScope().getLat(),
								newEstDo.getGScope().getLng()));
					}
				}
				newGsopeList.add(newEstMainMapBo);
			}
		}


		updateMap(mBaiduMap.getMapStatus(),true);

		//回到初始位置
//		toLocation(radius, CENTER_LATITUDE, CENTER_LONGITUDE, true);
	}

//	private boolean isShowNoLimit = false;

	@Override
	public void setEstBySchoolId(List<EstateBo> esfEstateDoList, SchoolBo schoolBo) {

		isGetData = true;
		clearMenuData();
		lastIconP = 2;
		mBaiduMap.clear();
		mapChange = false;
		isLocking = true;
		isUpdateMap = false;
		map_iv_school.setImageResource(R.drawable.ic_close_white_24dp);
		map_iv_hand.setImageResource(R.drawable.icon_map_hand);
		map_iv_metro.setImageResource(R.drawable.icon_map_metro);

		isUpdateMap = false;
		this.esfEstateDoList = esfEstateDoList;
		if (esfEstateDoList.size() == 0) {
			toast("学校周边暂无房源");
			esfMapChange(mBaiduMap.getMapStatus(), false);
		} else {
			esfMapChange(mBaiduMap.getMapStatus(), true);
			searchData.remove("RailLineId");
			searchData.remove("RailWayId");

			//添加学校的searchParam
			SearchParam search = new SearchParam();
			search.setId(schoolBo.getSchoolId());
			search.setText(schoolName);
			search.setValue(schoolBo.getSchoolId() + "");
			search.setTitle("学校");
			search.setKey("SchoolId");
			search.setPara("");
			search.setName(NetContents.SCHOOL_NAME);
			searchData.put(search.getKey(), search);
		}

		school_tv_name.setText(schoolName);
		OverlayOptions option = new MarkerOptions()
				.position(new LatLng(schoolLat, schoolLng))
//					.animateType(MarkerOptions.MarkerAnimateType.grow)
				.icon(BitmapDescriptorFactory.fromView(schoolView));
		//添加当前图标
		overlayList.add(mBaiduMap.addOverlay(option));

		zoomToSpan();
	}

	@Override
	public void noData() {
		houseNumber = 0;
		removeFromMap();
	}

	private HouseFragmentAdapter houseFragmentAdapter;

	private void showHouse(List<HouseBo> postDoList) {
		if (postDoList == null) return;
		List<HouseBottomBean> bottomBeanList = new ArrayList<>();
		for (HouseBo houseBo : postDoList) {
			HouseBottomBean item = new HouseBottomBean(currentHouseType);
			item.setPostId(houseBo.getPostId());
			item.setTitle(houseBo.getTitle());
			bottomBeanList.add(item);
		}
		houseFragmentAdapter.setItems(bottomBeanList, currentHouseType);
		map_ep_house.setCurrentItem(0,true);
		map_ep_house.setBottomTitle(estateName, postDoList.size());
		if (map_ep_house.getVisibility() != View.VISIBLE) {
			if (!isCircle){
				map_ll_tool.setVisibility(View.GONE);
				map_tv_list.setVisibility(View.GONE);
				((MainTabActivity) getActivity()).hiddenTab();
			}
			map_ep_house.show(currentHouseType, estateName);
			((MainTabActivity)getActivity()).showFirstSwipe();
		}
	}

	@Override
	public void onMapLoaded() {
		getBaseMapData();
	}

	@Override
	public void onMapStatusChangeStart(MapStatus mapStatus) {
		startZoom = mapStatus.zoom;
	}

	@Override
	public void onMapStatusChange(MapStatus mapStatus) {

	}

	private boolean mapChange = true;
	protected int mapStatusChangeTag;//地图状态改变标签
	private String stationName;

	@Override
	public void onMapStatusChangeFinish(MapStatus mapStatus) {
		endZoom = mapStatus.zoom;
//		Logger.i("endZoom:" + endZoom);

		if (isUpdateMap && mapChange) {
			updateMap(mapStatus, true);
		} else {
			isUpdateMap = true;
		}
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		return overlayList != null &&
				overlayList.contains(marker) &&
				baiduMarkerClick.onMarkerClick(marker);
	}

	private void esfMapChange(final MapStatus mapStatus, boolean showMsg) {
		selectedOverlay.clear();
		esfView.setBackgroundResource(R.drawable.map_level);
		houseNumber = 0;
		removeFromMap();

		if (currentHouseType == HOUSE_TYPE_NEW) {
			if (newHouseMapDetails == null) return;
			Observable.from(newHouseMapDetails)
					.filter(new Func1<NewHouseMapDetail, Boolean>() {
						@Override
						public Boolean call(NewHouseMapDetail esfEstateDo) {

							return mapStatus.bound.contains(new LatLng(esfEstateDo.getLat(), esfEstateDo.getLng()));
						}
					})
					.subscribe(esfEstateDo -> {
						overlayList.add(mBaiduMap.addOverlay(getMarkerOptionsForNew(esfEstateDo)));
					}, e -> {
						Logger.i("esfMapChange -->:" + e.getMessage());
					}, () -> {
						if (showMsg) {
							showToast("共" + houseNumber + resources.getString(R.string.map_query_alert_2));
						}
					});
		} else {
			//小区层级2级
			Observable.from(esfEstateDoList)
					.filter(new Func1<EstateBo, Boolean>() {
						@Override
						public Boolean call(EstateBo esfEstateDo) {

							if (esfEstateDo.getLat()==0 || esfEstateDo.getLng()==0){
								return false;
							}

							if (isCircle || !isUpdateMap) {
								return currentHouseType==HOUSE_TYPE_SECOND?esfEstateDo.getSaleNumber() > 0:esfEstateDo.getRentNumber() > 0;
							} else {
								if (currentHouseType==HOUSE_TYPE_SECOND){
									return mapStatus.bound.contains(new LatLng(esfEstateDo.getLat(), esfEstateDo.getLng())) && esfEstateDo.getSaleNumber() > 0;
								}else {
									return mapStatus.bound.contains(new LatLng(esfEstateDo.getLat(), esfEstateDo.getLng())) && esfEstateDo.getRentNumber() > 0;
								}
							}
						}
					})
					.subscribe(esfEstateDo -> {
						overlayList.add(mBaiduMap.addOverlay(getMarkerOptionsForDetail(esfEstateDo)));
					}, e -> {
						Logger.i("esfMapChange -->:" + e.getMessage());
					}, () -> {
						if (showMsg) {
							if (houseNumber == 0) {
								showToast("区域内暂无房源");
							} else {
								showToast(resources.getString(R.string.map_query_alert_1) + houseNumber + resources.getString(R.string.map_query_alert_3));
							}
						}
					});
		}
	}

	/**
	 * 更新地图
	 */
	protected void updateMap(final MapStatus mapStatus, boolean showMsg) {
		houseNumber = 0;
		if (currentHouseType == HOUSE_TYPE_NEW) {
			if (endZoom < NEW_ZOOM_GSCOPE) {
				removeFromMap();
				//当地图缩放到12级以下，显示2级房源
				Observable.from(newRegionList)
						.filter(new Func1<NewEstMainMapBo, Boolean>() {
							@Override
							public Boolean call(NewEstMainMapBo bo) {
								if (selectedRegionId==0){
									return bo.getLatLng()!=null && bo.getCount() > 0 && mapStatus.bound.contains(bo.getLatLng());
								}else {
									return bo.getLatLng()!=null && bo.getCount()> 0 && bo.getGscopId()==selectedGscopeId;
								}
							}
						})
						.subscribe(regionPostBo -> {
							overlayList.add(mBaiduMap.addOverlay(getNewMarkerOptions(regionPostBo)));
						}, e -> {

						}, () -> {
							if (showMsg) {
								if (houseNumber == 0) {
									showToast("区域内暂无房源");
								} else {
									showToast(resources.getString(R.string.map_query_alert_1) + houseNumber + resources.getString(R.string.map_query_alert_3));
								}
							}
						});

			} else if (endZoom < NEW_ZOOM_ESTATE) {
				removeFromMap();

				//当地图缩放到14级，那就显示3级房源
				Observable.from(newGsopeList)
						.filter(new Func1<NewEstMainMapBo, Boolean>() {
							@Override
							public Boolean call(NewEstMainMapBo bo) {

								if (selectedGscopeId==0){
									return mapStatus.bound.contains(bo.getLatLng()) && bo.getCount() > 0;
								}else {
									return mapStatus.bound.contains(bo.getLatLng()) && bo.getCount() > 0 && bo.getGscopId()==selectedGscopeId;
								}
							}
						})
						.subscribe(regionPostBo -> {
							overlayList.add(mBaiduMap.addOverlay(getNewMarkerOptions(regionPostBo)));
						}, e -> {

						}, () -> {
							if (showMsg) {
								if (houseNumber == 0) {
									showToast("区域内暂无房源");
								} else {
									showToast(resources.getString(R.string.map_query_alert_1) + houseNumber + resources.getString(R.string.map_query_alert_3));
								}
							}
						});

			} else {
				esfMapChange(mapStatus, true);
			}
		} else {
			if (baseMapDatas == null || isGetData){
				requestMapData();
				return;
			}
			if (endZoom < ZOOM_TO_GSCOPE) {
				removeFromMap();
				mBaiduMap.setMyLocationEnabled(false);
				//当地图缩放到12级以下，显示2级房源
				Observable.from(baseMapDatas)
						.filter(new Func1<RegionPostBo, Boolean>() {
							@Override
							public Boolean call(RegionPostBo bo) {
								if (currentHouseType == HOUSE_TYPE_SECOND) {
									if (selectedRegionId==0){
										return bo.getGScopeLevel() == 2 && bo.getSaleCount() > 0 && mapStatus.bound.contains(new LatLng(bo.getLat(), bo.getLng()));
									}else {
										return bo.getGScopeLevel() == 2 && bo.getGScopeId()==selectedRegionId;
									}
								} else if (currentHouseType == HOUSE_TYPE_RENT) {
									if (selectedRegionId==0){
										return bo.getGScopeLevel() == 2 && bo.getRentCount() > 0 && mapStatus.bound.contains(new LatLng(bo.getLat(), bo.getLng()));
									}else {
										return bo.getGScopeLevel() == 2 && bo.getGScopeId()==selectedRegionId;
									}
								} else {
									return bo.getGScopeLevel() == 2;
								}
							}
						})
						.subscribe(regionPostBo -> {
							overlayList.add(mBaiduMap.addOverlay(getMarkerOptions(regionPostBo)));
						}, e -> {

						}, () -> {
							if (showMsg) {
								if (houseNumber == 0) {
									showToast("区域内暂无房源");
								} else {
									showToast(resources.getString(R.string.map_query_alert_1) + houseNumber + resources.getString(R.string.map_query_alert_3));
								}
							}
						});

			} else if (endZoom < ZOOM_TO_ESTATE) {
				mBaiduMap.setMyLocationEnabled(true);
				houseNumber = 0;
				removeFromMap();
				//当地图缩放到14级，那就显示3级房源
				Observable.from(baseMapDatas)
						.filter(new Func1<RegionPostBo, Boolean>() {
							@Override
							public Boolean call(RegionPostBo regionPostBo) {

								if (selectedGscopeId==0){
									return mapStatus.bound.contains(new LatLng(regionPostBo.getLat(), regionPostBo.getLng()))
											&& regionPostBo.getGScopeLevel() == 3;
								}else {
									return regionPostBo.getGScopeLevel() == 3 && regionPostBo.getGScopeId()==selectedGscopeId;
								}
							}
						})
						.subscribe(regionPostBo -> {
							overlayList.add(mBaiduMap.addOverlay(getMarkerOptions(regionPostBo)));
						}, e -> {

						}, () -> {
							if (showMsg) {
								if (houseNumber == 0) {
									showToast("区域内暂无房源");
								} else {
									showToast(resources.getString(R.string.map_query_alert_1) + houseNumber + resources.getString(R.string.map_query_alert_3));
								}
							}
						});

			} else {
				mBaiduMap.setMyLocationEnabled(true);
				if (endMapStatus == null) {
					endMapStatus = mBaiduMap.getMapStatus();
				}
				//小区层级
				LatLngBounds latLngBounds = mBaiduMap.getMapStatus().bound;

				if (esfEstateDoList == null || esfEstateDoList.size() == 0 || selectedGscopeId!=0) {

					requestEsfList(mapStatus);

				} else if (endMapStatus.bound.southwest.longitude > latLngBounds.southwest.longitude ||
						endMapStatus.bound.southwest.latitude > latLngBounds.southwest.latitude ||
						endMapStatus.bound.northeast.longitude < latLngBounds.northeast.longitude ||
						endMapStatus.bound.northeast.latitude < latLngBounds.northeast.latitude) {

					requestEsfList(mapStatus);

				} else {
					if (changeMenu){
						requestEsfList(mapStatus);
						changeMenu = false;
					}else {
						Log.i(TAG, "updateMap: 在范围内，不请求数据");
						esfMapChange(mapStatus, showMsg);
					}
				}
			}
		}
	}

	private String estateCode;
	private String estateName;

	//请求小区房源数据，构造请求参数
	private void requestHoustList(EstateBo esfEstateDo) {
		estateCode = esfEstateDo.getEstateCode();
		estateName = esfEstateDo.getEstateName();
		Map<String, String> params = new HashMap<>();
		if (currentHouseType==HOUSE_TYPE_SECOND){
			params.put("PostType", "S");
		}else {
			params.put("PostType", "R");
		}
		params.put("EstateCode", estateCode);
		params.put("PageIndex", "0");
		params.put("PageCount", "100");

		if (esfParams.getEstateParam()!=null){
			params.putAll(esfParams.getEstateParam());
		}

		mPresenter.getHouseList(params);
	}

	//请求小区级别的参数
	private EstateMapRequest esfParams = new EstateMapRequest();

	//请求小区数据，构造请求参数
	private void requestEsfList(MapStatus mapStatus) {
		endMapStatus = mapStatus;

		if (selectedGscopeId==0){
			if (selectedRegionId==0){
				esfParams.remove("RegionId");
			}else {
				esfParams.put("RegionId", selectedRegionId+"");
			}
			esfParams.remove("GScopeId");
			esfParams.put("MinLng", mapStatus.bound.southwest.longitude + "");
			esfParams.put("MaxLng", mapStatus.bound.northeast.longitude + "");
			esfParams.put("MinLat", mapStatus.bound.southwest.latitude + "");
			esfParams.put("MaxLat", mapStatus.bound.northeast.latitude + "");
		}else {
			esfParams.remove("MinLng");
			esfParams.remove("MaxLng");
			esfParams.remove("MinLat");
			esfParams.remove("MaxLat");
			esfParams.put("GScopeId", selectedGscopeId+"");
		}

		Gson gson = new Gson();
		Logger.i("小区请求"+gson.toJson(esfParams));

		mPresenter.getEsfEstateList(getActivity(), esfParams);
	}

	private MapStatus endMapStatus;
	private final String MARK_TYPE = "MARK_TYPE";
	private final String MAP_EXTRA_INFO = "MAP_EXTRA_INFO";
	private final int MARK_TYPE_REGION = 0;
	private final int MARK_TYPE_GSCOPE = 1;
	private final int MARK_TYPE_ESFESTATE = 2;

	private final int MARK_TYPE_NEW_REGION = 3;
	private final int MARK_TYPE_NEW_HOUSE = 4;

	protected View view;
	//一级和二级视图上的文字显示
	private AppCompatTextView map_level_name, map_level_number;
	//小区级别
	private View esfView,lastEsfView;
	private AppCompatTextView atv_mark_title, atv_mark_num;

	protected OverlayOptions getMarkerOptions(RegionPostBo regionPostBo) {
		Bundle bundle = new Bundle();
		bundle.putParcelable(MAP_EXTRA_INFO, regionPostBo);
		bundle.putInt(MARK_TYPE, MARK_TYPE_REGION);
		map_level_name.setText(regionPostBo.getGScopeName());

		if (currentHouseType == HOUSE_TYPE_SECOND) {
			houseNumber += regionPostBo.getSaleCount();
			map_level_number.setText(regionPostBo.getSaleCount() + "");
		} else if (currentHouseType == HOUSE_TYPE_RENT) {
			houseNumber += regionPostBo.getRentCount();
			map_level_number.setText(regionPostBo.getRentCount() + "");
		} else {

		}

		return new MarkerOptions()
				.position(new LatLng(regionPostBo.getLat(), regionPostBo.getLng()))
				.extraInfo(bundle)
				.icon(BitmapDescriptorFactory.fromView(view));
	}

	protected OverlayOptions getNewMarkerOptions(NewEstMainMapBo regionPostBo) {
		Bundle bundle = new Bundle();
		bundle.putParcelable(MAP_EXTRA_INFO, regionPostBo);
		bundle.putInt(MARK_TYPE, MARK_TYPE_NEW_REGION);
		map_level_name.setText(regionPostBo.getName());

//		houseNumber += regionPostBo.getNewPropCount();
		houseNumber += regionPostBo.getCount();
		map_level_number.setText(regionPostBo.getCount() + "");

		return new MarkerOptions()
				.position(regionPostBo.getLatLng())
				.extraInfo(bundle)
//				.animateType(MarkerOptions.MarkerAnimateType.grow)
				.icon(BitmapDescriptorFactory.fromView(view));
	}


	private OverlayOptions getMarkerOptionsForDetail(EstateBo esfEstateDo) {
		Bundle bundle = new Bundle();
		bundle.putParcelable(MAP_EXTRA_INFO, esfEstateDo);
		bundle.putInt(MARK_TYPE, MARK_TYPE_ESFESTATE);

		if (currentHouseType == HOUSE_TYPE_SECOND) {
			houseNumber += esfEstateDo.getSaleNumber();
			atv_mark_num.setText(esfEstateDo.getSaleNumber() + "");
		} else if (currentHouseType == HOUSE_TYPE_RENT) {
			houseNumber += esfEstateDo.getRentNumber();
			atv_mark_num.setText(esfEstateDo.getRentNumber() + "");
		} else {

		}

		atv_mark_title.setText(esfEstateDo.getEstateName());
		return new MarkerOptions()
				.position(new LatLng(esfEstateDo.getLat(), esfEstateDo.getLng()))
				.extraInfo(bundle)
//				.animateType(MarkerOptions.MarkerAnimateType.grow)
				.icon(BitmapDescriptorFactory.fromView(esfView));
	}

	private OverlayOptions getMarkerOptionsForNew(NewHouseMapDetail houseDetail) {
		Bundle bundle = new Bundle();
		bundle.putParcelable(MAP_EXTRA_INFO, houseDetail);
		bundle.putInt(MARK_TYPE, MARK_TYPE_NEW_HOUSE);

		houseNumber++;
		atv_mark_num.setText("1");

		atv_mark_title.setText(houseDetail.getAdName());
		return new MarkerOptions()
				.position(new LatLng(houseDetail.getLat(), houseDetail.getLng()))
				.extraInfo(bundle)
//				.animateType(MarkerOptions.MarkerAnimateType.grow)
				.icon(BitmapDescriptorFactory.fromView(esfView));
	}


	/**
	 * 移除所有Overlay
	 */
	public void removeFromMap() {
		for (Overlay overlay : overlayList) {
			overlay.remove();
		}
		overlayList.clear();
	}

	private void getBaseMapData() {
		if (currentHouseType == HOUSE_TYPE_NEW) {
			mPresenter.getNewHouseBaseMapData(searchParams);
		} else {
			mPresenter.getBaseMapData(searchParams);
		}
	}

	private void getNewHouseMapData() {
		mPresenter.getNewHouseBaseMapData(searchParams);
	}

	//打开地图，默认放大到的地图级别
	private final float FIRST_SHOW_ZOOM = 12.6F;
	//用户点击定位图标，放大到的地图级别
	private final int USER_LOCATION_ZOOM = 17;
	//	private boolean isFirstLocation = true;
	private float radius;

	//板块显示的级别
	private static final float ZOOM_TO_GSCOPE = 15f;
	//小区显示的级别
	private static final float ZOOM_TO_ESTATE = 17f;

	private static final float NEW_ZOOM_GSCOPE = 14F;

	private static final float NEW_ZOOM_ESTATE = 15F;

	/**
	 * 定位到指定的坐标
	 *
	 * @param radius
	 * @param latitude
	 * @param longitude
	 */
	private void toLocation(float radius, double latitude, double longitude, boolean isToFirstZoom) {
		this.radius = radius;

		double uLat = DataHolder.getInstance().getLatitude();
		double uLng = DataHolder.getInstance().getLongitude();

		MyLocationData locData = new MyLocationData.Builder()
				.accuracy(radius)
				// 此处设置开发者获取到的方向信息，顺时针0-360
				.direction(100).latitude(uLat)
				.longitude(uLng).build();

		mBaiduMap.setMyLocationData(locData);


		LatLng ll = new LatLng(latitude, longitude);

		MapStatus.Builder builder = new MapStatus.Builder();
		builder.target(ll);
		if (isToFirstZoom) {
			builder.zoom(FIRST_SHOW_ZOOM);
			endZoom = FIRST_SHOW_ZOOM;
		} else {
			builder.zoom(USER_LOCATION_ZOOM);
			endZoom = USER_LOCATION_ZOOM;
		}
		mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
	}

	@Override
	public void onResume() {
		isShowing = true;
		map_house_mv.onResume();
		super.onResume();
	}

	@Override
	public void onPause() {
		isShowing = false;
		map_house_mv.onPause();
//		hiddenPop();
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		map_house_mv.onDestroy();
		map_house_mv = null;

		if (mCompositeSubscription != null && mCompositeSubscription.hasSubscriptions()) {
			mCompositeSubscription.unsubscribe();
		}
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		StatusBarCompat.translucentStatusBar(getActivity(), true);
	}

	Map<String, String> searchParams = new HashMap<>();

	@BindView(R.id.map_iv_metro)
	AppCompatImageView map_iv_metro;

	@BindView(R.id.map_iv_school)
	AppCompatImageView map_iv_school;

	@BindView(R.id.map_iv_hand)
	AppCompatImageView map_iv_hand;

	@BindView(R.id.map_iv_location)
	AppCompatImageView map_iv_location;

	@BindView(R.id.map_line_one)
	View map_line_one;

	@BindView(R.id.map_line_two)
	View map_line_two;

	@BindView(R.id.map_line_three)
	View map_line_three;

	private boolean changeMenu = false;

	private void dropMenuClose(int position, DropBo... dropBos) {

		changeMenu = true;

		hiddenPop();

		if (isLocking) {

			if ((isTrain || isCircle) && (position ==2 || position == 3)){

			}else if(isCircle && position == 1){
				map_iv_hand.setImageResource(R.drawable.icon_map_hand);
				startAnimation(false);
				((MainTabActivity) getActivity()).showTab();   //底部菜单
				mPresenter.buildAndStartAnimation(map_tv_list);  //列表按钮动画
				mBaiduMap.clear();
				isLocking = false;
				isCircle = false;
				mapChange = true;
				iconClickP = -1;

				esfParams.getPostFilter().setDrawCircle(null);
			}else if (isTrain && position == 1){
				map_iv_metro.setImageResource(R.drawable.icon_map_metro);
				mBaiduMap.clear();
				isLocking = false;
				isTrain = false;
				mapChange = true;
				iconClickP = -1;    //点击菜单的位置

				searchParams.clear();  //区域板块参数
				esfParams.clear();    //小区查询参数
				searchData.clear();   //菜单数据

				if (currentHouseType==HOUSE_TYPE_SECOND){
					searchParams.put("PostType", "S");
					esfParams.put("PostType", "S");
				}else if (currentHouseType==HOUSE_TYPE_RENT){
					searchParams.put("PostType", "R");
					esfParams.put("PostType", "R");
				}

				priceDrop.resetSelectStatus();
				resetTab(2);

				moreDrop.resetSelectStatus();
				resetTab(3);

				esfParams.getPostFilter().setDrawCircle(null);
			}else {
				map_iv_hand.setImageResource(R.drawable.icon_map_hand);
				map_iv_metro.setImageResource(R.drawable.icon_map_metro);
				map_iv_school.setImageResource(R.drawable.icon_map_school);

				//学校和地铁操作
				clearMenuData();

				isLocking = false;
				mBaiduMap.clear();
				isCircle = false;
				mapChange = true;

				if (iconClickP==0){
					startAnimation(false);
					((MainTabActivity) getActivity()).showTab();
					mPresenter.buildAndStartAnimation(map_tv_list);
					esfParams.clear();
					esfEstateDoList = null;
				}
				iconClickP = -1;
			}
		}

		if (position == 0) {

			DropBo dropType = dropBos[0];
			updateHouseType(dropType);
			return;
		} else if (position == 1) {
			DropBo dropType = dropBos[0];
			if (dropType.getType() == -1) {
				//全上海，不限制区域
				selectedRegionId = 0;
				selectedGscopeId = 0;

				if (currentHouseType==HOUSE_TYPE_NEW){
					removeData("DistrictId", null, position, false);
				}else {
					removeData("RegionId", null, position, false);
				}
				removeData("GScopeId", null, position, false);

				toLocation(radius, AppContents.CITY_LAT, AppContents.CITY_LNG, true);
				resetTab(1);
			} else if (dropType.getType() == 0) {
				//到区
//				mapShowType = MARK_TYPE_GSCOPE;

				removeData("GScopeId", null, position, false);

				endZoom = ZOOM_TO_GSCOPE;

				selectedRegionId = dropType.getID();
				selectedGscopeId = 0;

				SearchParam search = mPresenter.createSearch(dropType);
				search.setValue(dropType.getID()+"");
				search.setName(NetContents.REGION_NAME);
				search.setPara(dropType.getPara());
				if (currentHouseType==HOUSE_TYPE_NEW){
					search.setKey("DistrictId");
				}else {
					search.setKey("RegionId");
				}
				searchData.put(search.getKey(), search);

				mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(
						new LatLng(dropType.getLat(), dropType.getLng()), ZOOM_TO_GSCOPE));

				setTab(1, dropType.getText());
			} else {
				//到板块 获取小区数据 调用小区接口
				selectedRegionId = dropType.getParentId();
				selectedGscopeId = dropType.getID();
				mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(
						new LatLng(dropType.getLat(), dropType.getLng()), ZOOM_TO_ESTATE));

//				mapShowType = MARK_TYPE_ESFESTATE;
				endZoom = ZOOM_TO_ESTATE;

				SearchParam search = mPresenter.createSearch(dropType);
				search.setValue(dropType.getID()+"");
				search.setKey("GScopeId");
				search.setName(NetContents.GSCOPE_NAME);
				search.setPara(dropType.getPara());
				searchData.put(search.getKey(), search);

				setTab(1, dropType.getText());
				return;
			}
		} else if (position == 2) {
			DropBo dropType = dropBos[0];
//			String[] prices = dropType.getValue().split(",");
			if (currentHouseType == HOUSE_TYPE_SECOND) {
				if (dropType.getValue().length() > 2) {
					SearchParam search = mPresenter.createSearch(dropType);
					search.setKey("MinSalePrice,MaxSalePrice");
					searchData.put(search.getKey(), search);
					setTab(2, dropType.getText());
				} else {
					removeData("MinSalePrice", "MaxSalePrice", position, true);
				}
			} else if (currentHouseType == HOUSE_TYPE_RENT) {
				if (dropType.getValue().length() > 2) {
					SearchParam search = mPresenter.createSearch(dropType);
					search.setKey("MinRentPrice,MaxRentPrice");
					searchData.put(search.getKey(), search);
					setTab(2, dropType.getText());
				} else {
					removeData("MinRentPrice", "MaxRentPrice", position, true);
				}
			} else {
				if (dropType.getValue().length() > 2) {
					SearchParam search = mPresenter.createSearch(dropType);
					search.setKey("MinAveragePrice,MaxAveragePrice");
					searchData.put(search.getKey(), search);
					setTab(2, dropType.getText());
				} else {
					removeData("MinAveragePrice", "MaxAveragePrice", position, true);
				}
			}
		} else if (position == 3) {

			//二手房和租房
			removeData("MinRoomCnt", "MaxRoomCnt", position, false);
			removeData("MinGArea", "MaxGArea", position, false);
			removeData("Direction", null, position, false);
			removeData("MinOpdate", "MaxOpdate", position, false);
			removeData("Fitment", null, position, false);
			removeData("PropertyTypeList", null, position, false);
			removeData("FloorDisplay", null, position, false);
			removeMultiData("Feature");

			//新房
			removeData("EstType", null, position, false);
			removeData("RoomCnt", null, position, false);
			removeData("OpDateBegin", "OpDateEnd", position, false);
			removeMultiData("Tags");

			if (dropBos.length == 0) {
				resetTab(3);
			} else {

				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < dropBos.length; i++) {
					if (dropBos[i].getName().equals("Room")) {
						String[] rooms = dropBos[i].getValue().split(",");
						if (rooms.length>1){
							sb.append(dropBos[i].getText()).append(":");
							SearchParam search = mPresenter.createSearch(dropBos[i]);
							if (currentHouseType==HOUSE_TYPE_NEW){
								search.setKey("RoomCnt");
								search.setValue(rooms[0]);
							}else {
								search.setKey("MinRoomCnt,MaxRoomCnt");
							}
							searchData.put(search.getKey(), search);
						}
					} else if (dropBos[i].getName().equals("Area")) {
						String[] areas = dropBos[i].getValue().split(",");
						if (areas.length>1){
							sb.append(dropBos[i].getText()).append(":");
							SearchParam search = mPresenter.createSearch(dropBos[i]);
							search.setKey("MinGArea,MaxGArea");
							searchData.put(search.getKey(), search);
						}
					} else if (dropBos[i].getName().equals("Direction")) {
						sb.append(dropBos[i].getText()).append(":");
						SearchParam search = mPresenter.createSearch(dropBos[i]);
						search.setKey("Direction");
						searchData.put(search.getKey(), search);
					} else if (dropBos[i].getName().equals("HouseAge")) {
						String[] ages = dropBos[i].getValue().split(",");
						if (ages.length>1){
							sb.append(dropBos[i].getText()).append(":");
							SearchParam search = mPresenter.createHouseAge(dropBos[i]);
							search.setKey("MinOpdate,MaxOpdate");
							searchData.put(search.getKey(), search);
						}
					} else if (dropBos[i].getName().equals("Fitment")) {
						sb.append(dropBos[i].getText()).append(":");
						SearchParam search = mPresenter.createSearch(dropBos[i]);
						search.setKey("Fitment");
						searchData.put(search.getKey(), search);
					} else if (dropBos[i].getName().equals("Floor")) {
						sb.append(dropBos[i].getText());
						SearchParam search = mPresenter.createSearch(dropBos[i]);
						search.setKey("FloorDisplay");
						searchData.put(search.getKey(), search);
					} else if (dropBos[i].getName().equals("SellTag")) {
						sb.append(dropBos[i].getText()).append(":");

						SearchParam search = mPresenter.createSearch(dropBos[i]);
						search.setKey("Feature"+dropBos[i].getID());
						search.setParamKey("Feature");
						searchData.put(search.getKey(), search);
					}else if (dropBos[i].getName().equals("NewEstType")) {
						sb.append(dropBos[i].getText());
						SearchParam search = mPresenter.createSearch(dropBos[i]);
						search.setKey("EstType");
						searchData.put(search.getKey(), search);
					}else if (dropBos[i].getName().equals("NewPropOpDate")) {
						String[] opDates = dropBos[i].getValue().split(",");
						if (opDates.length>1){
							sb.append(dropBos[i].getText()).append(":");
							SearchParam search = mPresenter.createOpenDate(dropBos[i]);
							search.setKey("OpDateBegin,OpDateEnd");
							searchData.put(search.getKey(), search);
						}
					}else if (dropBos[i].getName().equals("NewPropFeatures")) {
						sb.append(dropBos[i].getText()).append(":");

						SearchParam search = mPresenter.createSearch(dropBos[i]);
						search.setKey("Tags"+dropBos[i].getID());
						search.setParamKey("Tags");
						searchData.put(search.getKey(), search);
					} else if (dropBos[i].getName().equals("Property")){
						sb.append(dropBos[i].getText()).append(":");
						SearchParam search = mPresenter.createSearch(dropBos[i]);
						search.setKey("PropertyTypeList");
						searchData.put(search.getKey(), search);
					}
				}
				String[] texts = sb.toString().split(":");
				if (texts!=null && texts.length>1){
					setTab(3, "多选");
				}else {
					setTab(3, sb.substring(0, sb.length()-1));
				}
			}
		}
		getDataByMenu();
	}

	/**
	 * 切换房源类型
	 * @param houseType
	 */
	public void changeHouseType(int houseType){
		DropBo dropBo = new DropBo();
		dropBo.setType(houseType);
		updateHouseType(dropBo);
	}


	private void updateHouseType(DropBo dropType) {

		searchParams.clear();
		searchData.clear();
		selectedRegionId = 0;
		selectedGscopeId = 0;

		toLocation(radius, AppContents.CITY_LAT, AppContents.CITY_LNG, true);

		areaDrop.clearMenu();
		moreDrop.resetSelectStatus();

		switch (dropType.getType()) {
			case HOUSE_TYPE_SECOND:
				map_tv_list.setText("打开列表");
				currentHouseType = HOUSE_TYPE_SECOND;
				updateDropMenu(currentHouseType);
				break;
			case HOUSE_TYPE_RENT:
				map_tv_list.setText("打开列表");
				currentHouseType = HOUSE_TYPE_RENT;
				updateDropMenu(currentHouseType);
				break;
			case HOUSE_TYPE_NEW:
				map_tv_list.setText("全部楼盘");
				currentHouseType = HOUSE_TYPE_NEW;
				map_iv_metro.setVisibility(View.GONE);
				map_iv_school.setVisibility(View.GONE);
				map_iv_hand.setVisibility(View.GONE);
				map_line_one.setVisibility(View.GONE);
				map_line_two.setVisibility(View.GONE);
				map_line_three.setVisibility(View.GONE);

				updateDropMenu(currentHouseType);
				getNewHouseMapData();
				return ;
		}

		map_iv_metro.setVisibility(View.VISIBLE);
		map_iv_school.setVisibility(View.VISIBLE);
		map_iv_hand.setVisibility(View.VISIBLE);
		map_line_one.setVisibility(View.VISIBLE);
		map_line_two.setVisibility(View.VISIBLE);
		map_line_three.setVisibility(View.VISIBLE);

		getBaseMapData();
	}


	private void getEstateData() {
		if (currentHouseType == HOUSE_TYPE_NEW) {
			requestNewHouseList();
		} else {
			requestEsfList(mBaiduMap.getMapStatus());
		}
	}

	private void requestNewHouseList(){
		getNewHouseMapData();
	}

	private String railLineId, railLineName;

	public void getEsfByRailLine(String railLineId, String railLineName) {
		this.iconClickP = 1;
		this.railLineId = railLineId;
		this.railLineName=railLineName;

		Map<String, String> param = new HashMap<>();
		param.put("PostType", searchParams.get("PostType"));

//		poiSearch.searchInCity(new PoiCitySearchOption()
//				.city(NetContents.CITY_NAME)
//				.pageCapacity(20)
//				.keyword(railLineName));
		stationName = railLineName;
		param.put("RailLineId", railLineId);

		railwayLineOverlay.setData(DbUtil.getRailLineById(railLineId));
		railwayLineOverlay.showLine();   //显示线路标线
		clearMenuData();
		lastIconP = 1;
		isGetData = true;
		mBaiduMap.clear();
		mapChange = true;
		isUpdateMap = true;
		isLocking = true;
		map_iv_metro.setImageResource(R.drawable.ic_close_white_24dp);
		map_iv_hand.setImageResource(R.drawable.icon_map_hand);
		map_iv_school.setImageResource(R.drawable.icon_map_school);
		railwayLineOverlay.addToMap();
//		railwayLineOverlay.zoomToSpan();
		//定位到线路中点，指定层级
//		mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(
//				railwayLineOverlay.getLineCenter(), ZOOM_TO_ESTATE));
		mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngBounds(railwayLineOverlay.getBounds()));

		updateMap(mBaiduMap.getMapStatus(), true);

		SearchParam searchLine = new SearchParam();
		searchLine.setId(Integer.parseInt(railLineId));
		searchLine.setText(railLineName);
		searchLine.setValue(railLineId);
		searchLine.setTitle("地铁线");
		searchLine.setKey("RailLineId");
		searchLine.setPara("");
		searchLine.setName(NetContents.RAILLINE_NAME);
		searchData.put(searchLine.getKey(), searchLine);

	}

	//地铁站点搜索房源 v1
//	public void getEsfByRailWay(String railLineId, String railLineName, String railWayId, String railWayName) {
//		this.iconClickP = 1;
//		this.railLineId = railLineId;
//		this.railLineName=railLineName;
//		mapStatusChangeTag = 1;
//		Map<String, String> param = new HashMap<>();
//		param.put("PostType", searchParams.get("PostType"));
//		poiSearch.searchInCity(new PoiCitySearchOption()
//				.city(NetContents.CITY_NAME)
//				.pageCapacity(20)
//				.keyword(railLineName));
//		stationName = railWayName;
//		param.put("RailWayId", railWayId);
//
//		mPresenter.getHouseByRailWay(param);
//
//
//	}

	//地铁站点搜索房源 v2
	public void getEsfByRailWay(String railLineId, String railLineName, String railWayId, String railWayName) {
		this.railLineId = railLineId;
		isGetData = true;
		lastIconP = 1;  //上次点击菜单的位置
		isLocking = true;  //地图锁定状态

		mapChange = true;  //更新地图数据
		isUpdateMap = true;

		//清除菜单查询数据
		clearMenuData();
		this.iconClickP = 1;
		stationName = railLineName;
		this.railLineName=railLineName;

		map_iv_metro.setImageResource(R.drawable.ic_close_white_24dp);
		map_iv_hand.setImageResource(R.drawable.icon_map_hand);
		map_iv_school.setImageResource(R.drawable.icon_map_school);

		railwayLineOverlay.setData(DbUtil.getRailLineById(railLineId));
		railwayLineOverlay.showLine();   //显示线路标线
		railwayLineOverlay.addToMap();
		if (!TextUtils.isEmpty(railLineName)) {
			railwayLineOverlay.setStation(railWayName);
			stationName = null;
		}

//		SearchParam searchLine = new SearchParam();
//		searchLine.setId(Integer.parseInt(railLineId));
//		searchLine.setText(railLineName);
//		searchLine.setValue(railLineId);
//		searchLine.setTitle("地铁线");
//		searchLine.setKey("RailLineId");
//		searchLine.setPara("");
//		searchLine.setName(NetContents.RAILLINE_NAME);
//		searchData.put(searchLine.getKey(), searchLine);
	}

	private double schoolLat, schoolLng;
	private String schoolName;

	public void getEsfBySchool(String schoolName, String schoolId, double lat, double lng) {
		this.iconClickP = 2;
		this.schoolName = schoolName;
		schoolLat = lat;
		schoolLng = lng;
		mPresenter.getEstBySchoolId(getActivity(),schoolId);
	}

	/**
	 * 缩放地图，使所有Overlay都在合适的视野内
	 */
	private void zoomToSpan() {
		if (overlayList.size() > 0) {
			LatLngBounds.Builder builder = new LatLngBounds.Builder();
			for (Overlay overlay : overlayList) {
				// polyline 中的点可能太多，只按marker 缩放
				if (overlay instanceof Marker) {
					builder.include(((Marker) overlay).getPosition());
				}
			}
			mBaiduMap.animateMapStatus(MapStatusUpdateFactory
					.newLatLngBounds(builder.build()));
		}
	}

	/**
	 * 重置指定位置的标签
	 *
	 * @param position
	 */
	protected void resetTab(int position) {
		map_drop_menu.resetTab(position);
	}

	/**
	 * 设置标签
	 *
	 * @param position 位置
	 * @param tab      文本
	 */
	protected void setTab(int position, String tab) {
		map_drop_menu.setTab(position, tab);
	}

	private int iconClickP = -1;

	private void iconMenuDo(int position, boolean isLocking, AppCompatImageView view) {
		if (isLocking) {
			switch (position) {
				case 0:
					this.iconClickP = 0;
					//清空菜单条件
					clearMenuData();

					showToast(resources.getString(R.string.map_draw_alert));
					map_iv_hand.setImageResource(R.drawable.ic_close_white_24dp);
					mBaiduMap.clear();
					//菜单隐藏
					mPresenter.startAnimation(map_ll_title, true);
					//收缩画圈工具栏
					startAnimation(true);
					//隐藏底部导航栏
					((MainTabActivity) getActivity()).hiddenTab();
					//隐藏打开列表
					mPresenter.buildAndStartAnimation(map_tv_list);
					//画圈画布显示
					map_dv.setVisibility(View.VISIBLE);
					break;
				case 1:
					isTrain = true;
					((MainTabActivity) getActivity()).showMenu(0);
					break;
				case 2:
					((MainTabActivity) getActivity()).showMenu(1);
					break;
			}
		} else {
			this.iconClickP = position;
			switch (position) {
				case 0:
					//伸展画圈工具栏
					startAnimation(false);
					if (!isCircle){
						mPresenter.startAnimation(map_ll_title, false);
						map_dv.setVisibility(View.GONE);
					}
					//清除菜单条件
					clearMenuData();
					//更新地图
					updateMap(mBaiduMap.getMapStatus(),true);
					//显示底部导航栏
					((MainTabActivity) getActivity()).showTab();
					//显示打开列表
					mPresenter.buildAndStartAnimation(map_tv_list);

					//重置画圈状态
					isCircle = false;
					iconClickP = -1;
					view.setImageResource(R.drawable.icon_map_hand);
					esfEstateDoList = null;
					break;
				case 1:
					isTrain = false;
					view.setImageResource(R.drawable.icon_map_metro);
					toLocation(radius, AppContents.CITY_LAT, AppContents.CITY_LNG, true);
					break;
				case 2:
					view.setImageResource(R.drawable.icon_map_school);
					toLocation(radius, AppContents.CITY_LAT, AppContents.CITY_LNG, true);
					break;
			}
		}
	}

	//菜单条件处理
	private HashMap<String, SearchParam> searchData = new HashMap<>();

	private void clearMenuData() {

		searchParams.clear();
		esfParams.clear();
		searchData.clear();
		selectedRegionId = 0;
		selectedGscopeId = 0;

		//重置菜单2
		areaDrop.clearMenu();
		resetTab(1);

		priceDrop.resetSelectStatus();
		resetTab(2);

		moreDrop.resetSelectStatus();
		resetTab(3);

		if (currentHouseType==HOUSE_TYPE_SECOND){
			searchParams.put("PostType", "S");
			esfParams.put("PostType", "S");
//			esfParams.put("ImageWidth", "100");
//			esfParams.put("ImageHeight", "100");
		}else if (currentHouseType==HOUSE_TYPE_RENT){
			searchParams.put("PostType", "R");
			esfParams.put("PostType", "R");
//			esfParams.put("ImageWidth", "100");
//			esfParams.put("ImageHeight", "100");
		}
	}

	/**
	 * @param key1
	 * @param key2
	 * @param position 菜单位置
	 * @param isRest 是否重置菜单名字
	 */
	private void removeData(String key1, String key2, int position, boolean isRest) {
		if (key2 == null) {
			searchData.remove(key1);
			searchParams.remove(key1);
			esfParams.remove(key1);
		} else {
			searchData.remove(key1 + "," + key2);

			searchParams.remove(key1);
			searchParams.remove(key2);

			esfParams.remove(key1);
			esfParams.remove(key2);

		}
		if (isRest) resetTab(position);
	}

	private void removeMultiData(String keyWord) {
		List<String> removeKyes = new ArrayList<>();
		for (String key : searchData.keySet()){
			if (key.contains(keyWord)){
				removeKyes.add(key);
			}
		}
		for (String key : removeKyes){
			searchData.remove(key);
		}
		searchParams.remove(keyWord);
		esfParams.remove(keyWord);
	}

	private void getDataByMenu() {

		for (SearchParam param : searchData.values()) {

			if (param.getKey().equals("RegionId") || param.getKey().equals("GScopeId")){
				continue;
			}

			String[] paramKeys = param.getParamKey().split(",");

			if (paramKeys != null && paramKeys.length > 1) {
				String[] values = param.getValue().split(",");

				searchParams.put(paramKeys[0], values[0]);
				searchParams.put(paramKeys[1], values[1]);

				esfParams.put(paramKeys[0], values[0]);
				esfParams.put(paramKeys[1], values[1]);

//				if (endZoom < ZOOM_TO_ESTATE){
//					searchParams.put(paramKeys[0], values[0]);
//					searchParams.put(paramKeys[1], values[1]);
//				}else {
//					esfParams.put(paramKeys[0], values[0]);
//					esfParams.put(paramKeys[1], values[1]);
//				}
			} else {


				if (param.getParamKey().equals("Tags")) {
					if (searchParams.get("Tags") != null) {
						searchParams.put("Tags", searchParams.get("Tags") + "_" + param.getId());
					} else {
						searchParams.put("Tags", param.getId() + "");
					}
				} else if (param.getParamKey().equals("Feature")) {
					esfParams.put("Feature", param.getId() + "");
					if (searchParams.get("Feature") != null) {
						searchParams.put("Feature", searchParams.get("Feature") + "_" + param.getId());
					} else {
						searchParams.put("Feature", param.getId() + "");
					}
				} else if (param.getParamKey().equals("PropertyTypeList")) {
					esfParams.put("PropertyTypeList",param.getValue());
					searchParams.put("PropertyTypeList",param.getValue());
				}else {
					searchParams.put(param.getParamKey(), param.getValue());
					esfParams.put(param.getParamKey(), param.getValue());
				}

			}
		}

//		for (Map.Entry<String, String> item : searchParams.entrySet()) {
//			Logger.i(item.getKey() + "---" + item.getValue());
//		}

		requestMapData();
	}

	private void requestMapData() {
		if (isLocking && iconClickP==0 && isCircle){

//			for (Map.Entry<String, String> item : param.entrySet()) {
//				Logger.i(item.getKey() + "---" + item.getValue());
//			}

			esfParams.getPostFilter().setDrawCircle(circleStr);

			mPresenter.getNewEstByCircle(getActivity(), esfParams);
		}else {
			if (endZoom < ZOOM_TO_ESTATE) {
				getBaseMapData();
			} else {
				getEstateData();
			}
		}
	}

	protected RailwayLineOverlay railwayLineOverlay;

	// 地铁搜索
	protected OnGetPoiSearchResultListener onGetPoiSearchResultListener = new OnGetPoiSearchResultListener() {

		@Override
		public void onGetPoiResult(PoiResult poiResult) {
			if (poiResult == null ||
					poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
				cancelLoadingDialog();
				toast("未找到结果");
				return;
			}

			if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {
				for (PoiInfo poiInfo : poiResult.getAllPoi()) {
					if (poiInfo.type == PoiInfo.POITYPE.SUBWAY_LINE) {
						busLineSearch.searchBusLine(new BusLineSearchOption()
								.city(NetContents.CITY_NAME)
								.uid(poiInfo.uid));
						break;
					}
				}
			}

		}

		@Override
		public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
			if (poiDetailResult.error != SearchResult.ERRORNO.NO_ERROR) {
				toast("未找到结果");
			}
		}

		@Override
		public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

		}
	};

	/**
	 * 线路搜索
	 */
	protected OnGetBusLineSearchResultListener onGetBusLineSearchResultListener = new OnGetBusLineSearchResultListener() {
		@Override
		public void onGetBusLineResult(BusLineResult busLineResult) {
			if (busLineResult == null ||
					busLineResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
				toast("未找到结果");
				return;
			}

			if (busLineResult.error == SearchResult.ERRORNO.NO_ERROR) {
//				railwayLineOverlay.setData(busLineResult);
//				if (mapStatusChangeTag==0){
//					clearMenuData();
//					lastIconP = 1;
//					mBaiduMap.clear();
//					mapChange = false;
//					isLocking = true;
//					isUpdateMap = false;
//					map_iv_metro.setImageResource(R.drawable.ic_close_white_24dp);
//					map_iv_hand.setImageResource(R.drawable.icon_map_hand);
//					map_iv_school.setImageResource(R.drawable.icon_map_school);
//
//					railwayLineOverlay.addToMap();
//					railwayLineOverlay.zoomToSpan();
////					if (TextUtils.isEmpty(stationName)) {
////						mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(busLineResult.getStations().get(0).getLocation(), 14f));
////					} else {
////						mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(busLineResult.getStations().get(0).getLocation(), 16f));
////					}
//				}else {
//					railwayLineOverlay.addToMap();
//					if (!TextUtils.isEmpty(stationName)) {
//						railwayLineOverlay.setStation(stationName);
//						stationName = null;
//					}
//				}
//
//				SearchParam searchLine = new SearchParam();
//				searchLine.setId(Integer.parseInt(railLineId));
//				searchLine.setText(railLineName);
//				searchLine.setValue(railLineId);
//				searchLine.setTitle("地铁线");
//				searchLine.setKey("RailLineId");
//				searchLine.setPara("");
//				searchLine.setName(NetContents.RAILLINE_NAME);
//				searchData.put(searchLine.getKey(), searchLine);
			}
		}
	};

	private Guide iconGuide, listGuide;

	private void showIconGuide() {
		GuideBuilder builder = new GuideBuilder();
		builder.setTargetView(map_ll_tool)
				.setAlpha(150)
				.setHighTargetCorner(20)
				.setHighTargetPadding(6)
				.setOverlayTarget(false)
				.setOutsideTouchable(false);
		builder.setOnVisibilityChangedListener(new GuideBuilder.OnVisibilityChangedListener() {
			@Override
			public void onShown() {

			}

			@Override
			public void onDismiss() {
				showListGuide();
			}
		});

		builder.addComponent(new IconComponent());
		iconGuide = builder.createGuide();
		iconGuide.setShouldCheckLocInWindow(true);
		iconGuide.show(getActivity());
	}


	private void showListGuide(){
		GuideBuilder builder = new GuideBuilder();
		builder.setTargetView(map_tv_list)
				.setAlpha(150)
				.setHighTargetCorner(20)
				.setHighTargetPadding(6)
				.setOverlayTarget(false)
				.setOutsideTouchable(false);
		builder.setOnVisibilityChangedListener(new GuideBuilder.OnVisibilityChangedListener() {
			@Override
			public void onShown() {
			}

			@Override
			public void onDismiss() {
				listGuide = null;
				iconGuide = null;
			}
		});

		builder.addComponent(new ListComponent());
		listGuide = builder.createGuide();
		listGuide.setShouldCheckLocInWindow(true);
		listGuide.show(getActivity());
	}

	private int maxHeight, minHeight;

	/**
	 * 画圈找房向上向下隐藏动画
	 * @param isShow
	 */
	private void startAnimation(final boolean isShow) {
		ValueAnimator animator;
		if (isShow){
			animator = ValueAnimator.ofInt(maxHeight, minHeight);
		}else {
			animator = ValueAnimator.ofInt(minHeight, maxHeight);
		}

		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				int value = (int) animation.getAnimatedValue();
				RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) map_ll_tool.getLayoutParams();
				param.height = value;
				map_ll_tool.setLayoutParams(param);
				isAnimation = true;
			}
		});
		animator.addListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {
				if (isShow){

				}else {
//					map_iv_hand.setImageResource(R.drawable.icon_map_hand);
					map_iv_location.setVisibility(View.VISIBLE);
					map_iv_metro.setVisibility(View.VISIBLE);
					map_iv_school.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				setAnimationStatus(false);
				if (isShow){
					map_iv_location.setVisibility(View.GONE);
					map_iv_metro.setVisibility(View.GONE);
					map_iv_school.setVisibility(View.GONE);
//					map_iv_hand.setImageResource(R.drawable.ic_close_white_24dp);
				}
			}

			@Override
			public void onAnimationCancel(Animator animation) {

			}

			@Override
			public void onAnimationRepeat(Animator animation) {

			}
		});
		animator.setInterpolator(new AccelerateInterpolator());
		animator.setDuration(400);
		animator.start();
	}

	//是否在进行动画
	private boolean isAnimation;

	public static final int ANIMATION_TIME = 500;

	@Override
	public void setAnimationStatus(boolean isRunning) {
		isAnimation = isRunning;
	}

	public boolean isAnimation(){
		return isAnimation;
	}

	private PopupWindow popupWindow;
	private TextView toast_text;
	private Subscription toastSub;

	private void showToast(String text){
		showToast(text, true);
	}

	private void showToast(String text, boolean autoDismiss){

		toast_text.setText(text);

		if (popupWindow.isShowing() && toastSub!=null && !toastSub.isUnsubscribed()){
			toastSub.unsubscribe();
		}

		if (autoDismiss){
			toastSub = Observable.timer(2000, TimeUnit.MILLISECONDS)
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(time -> {
						if (popupWindow!=null){
							popupWindow.dismiss();
						}
					});

		}

		if (!popupWindow.isShowing()){
			popupWindow.showAtLocation(map_house_mv, Gravity.BOTTOM, 0, toastBottom);
		}
	}

	private void dismissWindow(){
		if (popupWindow!=null && popupWindow.isShowing()){
			popupWindow.dismiss();
			if (toastSub!=null && toastSub.isUnsubscribed()){
				toastSub.unsubscribe();
			}
		}
	}


}
