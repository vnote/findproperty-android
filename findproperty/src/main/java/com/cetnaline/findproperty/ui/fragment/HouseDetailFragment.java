package com.cetnaline.findproperty.ui.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.bumptech.glide.DrawableRequestBuilder;
import com.cetnaline.findproperty.AppContents;
import com.cetnaline.findproperty.BaseApplication;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.bean.ApartmentBo;
import com.cetnaline.findproperty.api.bean.ExerciseListBo;
import com.cetnaline.findproperty.api.bean.HouseBo;
import com.cetnaline.findproperty.api.bean.HouseDetailBo;
import com.cetnaline.findproperty.api.bean.HouseImageBo;
import com.cetnaline.findproperty.api.bean.ImageBean;
import com.cetnaline.findproperty.api.bean.NewHouseDetail;
import com.cetnaline.findproperty.api.bean.NewHouseImageBo;
import com.cetnaline.findproperty.api.bean.PriceTrendBean;
import com.cetnaline.findproperty.api.bean.RailwayInfoBo;
import com.cetnaline.findproperty.api.bean.StaffComment;
import com.cetnaline.findproperty.base.BaseActivity;
import com.cetnaline.findproperty.base.BaseFragment;
import com.cetnaline.findproperty.base.BaseFragmentActivity;
import com.cetnaline.findproperty.entity.bean.StaffListBean;
import com.cetnaline.findproperty.entity.event.CancelCollectionEvent;
import com.cetnaline.findproperty.entity.event.ShareEvent;
import com.cetnaline.findproperty.entity.event.SlidingEvent;
import com.cetnaline.findproperty.entity.event.TopViewEvent;
import com.cetnaline.findproperty.api.NetContents;
import com.cetnaline.findproperty.presenter.impl.HouseDetailPresenter;
import com.cetnaline.findproperty.presenter.ui.HouseDetailContract;
import com.cetnaline.findproperty.ui.activity.ConversationActivity;
import com.cetnaline.findproperty.ui.activity.HouseDetail;
import com.cetnaline.findproperty.ui.activity.HouseList;
import com.cetnaline.findproperty.ui.activity.ImageBrowseActivity;
import com.cetnaline.findproperty.ui.activity.LoginActivity;
import com.cetnaline.findproperty.ui.activity.LookAbout;
import com.cetnaline.findproperty.ui.activity.MainTabActivity;
import com.cetnaline.findproperty.ui.activity.NearbyActivity;
import com.cetnaline.findproperty.ui.activity.PanoramaActivity;
import com.cetnaline.findproperty.ui.activity.ToolActivity;
import com.cetnaline.findproperty.ui.activity.VillageDetail;
import com.cetnaline.findproperty.ui.adapter.ExerciseAdapter;
import com.cetnaline.findproperty.ui.adapter.NearbyHouseAdapter;
import com.cetnaline.findproperty.ui.adapter.SaleApartmentAdapter;
import com.cetnaline.findproperty.utils.DataHolder;
import com.cetnaline.findproperty.utils.DateUtil;
import com.cetnaline.findproperty.utils.MyUtils;
import com.cetnaline.findproperty.utils.RxBus;
import com.cetnaline.findproperty.utils.glide.GlideLoad;
import com.cetnaline.findproperty.widgets.BadgeView;
import com.cetnaline.findproperty.widgets.CircleImageView;
import com.cetnaline.findproperty.widgets.DetailImgLayout;
import com.cetnaline.findproperty.widgets.HouseDescribeWindow;
import com.cetnaline.findproperty.widgets.MyBottomDialog;
import com.cetnaline.findproperty.widgets.MyText;
import com.cetnaline.findproperty.widgets.chart.SaleLineChart2;
import com.cetnaline.findproperty.widgets.chart.SalePieChart;
import com.cetnaline.findproperty.widgets.sharedialog.ShareDialog;
import com.orhanobut.logger.Logger;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.subscriptions.CompositeSubscription;


/**
 * 房源详情 二手房 新房 租房
 * Created by fanxl2 on 2016/7/29.
 */
public class HouseDetailFragment extends BaseFragment<HouseDetailPresenter>
		implements HouseDetailContract.View {

	public static final String FROM_TYPE = "FROM_TYPE";
	public static final int FROM_TYPE_MAP = 0;
	public static final int FROM_TYPE_NORMAL = 1;
	public static final String HOUSE_ID_KEY = "HOUSE_ID_KEY";
	public static final String ESTEXT_ID_KEY = "ESTEXT_ID_KEY";

	private boolean collectionExchange = false;

	public static HouseDetailFragment getInstance(String postId, int fromType, int houseType) {
		Bundle bundle = new Bundle();
		bundle.putInt(FROM_TYPE, fromType);
		bundle.putInt(MapFragment.HOUSE_TYPE_KEY, houseType);
		bundle.putString(HOUSE_ID_KEY, postId);
		return getInstance(bundle);
	}

	public static HouseDetailFragment getInstance(String estExtId, int fromType) {
		Bundle bundle = new Bundle();
		bundle.putInt(FROM_TYPE, fromType);
		bundle.putInt(MapFragment.HOUSE_TYPE_KEY, MapFragment.HOUSE_TYPE_NEW);
		bundle.putString(ESTEXT_ID_KEY, estExtId);
		return getInstance(bundle);
	}

	public static HouseDetailFragment getInstance(Bundle bundle) {
		HouseDetailFragment houseDetail = new HouseDetailFragment();
		houseDetail.setArguments(bundle);
		return houseDetail;
	}

	@BindDrawable(R.drawable.ic_support_water)
	Drawable ic_support_water;

	@BindDrawable(R.drawable.ic_support_no)
	Drawable ic_support_no;

	@BindDrawable(R.drawable.ic_support_electric)
	Drawable ic_support_electric;

	@BindDrawable(R.drawable.ic_support_net)
	Drawable ic_support_net;

	@BindDrawable(R.drawable.ic_support_gas)
	Drawable ic_support_gas;

	@BindDrawable(R.drawable.ic_support_air)
	Drawable ic_support_air;

	@BindDrawable(R.drawable.ic_support_isdn)
	Drawable ic_support_isdn;

	@BindDrawable(R.drawable.ic_support_heating)
	Drawable ic_support_heating;

	@BindDrawable(R.drawable.ic_support_heater)
	Drawable ic_support_heater;

	@BindDrawable(R.drawable.ic_support_wash)
	Drawable ic_support_wash;

	@BindDrawable(R.drawable.ic_support_icebox)
	Drawable ic_support_icebox;

	@BindDrawable(R.drawable.ic_support_colortv)
	Drawable ic_support_colortv;

	@BindDrawable(R.drawable.ic_support_mobile)
	Drawable ic_support_mobile;

	@BindDrawable(R.drawable.ic_support_bed)
	Drawable ic_support_bed;

	@BindDrawable(R.drawable.ic_support_furniture)
	Drawable ic_support_furniture;

	@BindDrawable(R.drawable.ic_support_linetv)
	Drawable ic_support_linetv;

	@BindView(R.id.detail_sale_pc)
	SalePieChart detail_sale_pc;

	@BindView(R.id.detail_dai_text)
	TextView detail_dai_text;

	@BindView(R.id.detail_sale_lc2)
	SaleLineChart2 detail_sale_lc2;

	@BindView(R.id.detail_house_map)
	ImageView detail_house_map;

	@BindView(R.id.toolbar)
	Toolbar toolbar;

	@BindView(R.id.center_title)
	TextView center_title;

//	@BindView(R.id.detail_fb_like)
//	FloatingActionButton detail_fb_like;

//	@BindView(R.id.detail_fb_share)
//	FloatingActionButton detail_fb_share;

	@BindView(R.id.detail_dl_imgs)
	DetailImgLayout detail_dl_imgs;

	@BindView(R.id.vs_house_type)
	ViewStub vs_house_type;

	@BindView(R.id.vs_new_type)
	ViewStub vs_new_type;

	//底部弹窗图片文字描述
	@BindView(R.id.vt_detail_house_info)
	ViewStub vt_detail_house_info;

	private TextView bottom_house_area, bottom_house_info, bottom_house_price, bottom_house_money;

	@BindView(R.id.vs_rent_support)
	ViewStub vs_rent_support;

	//约看单
	@BindView(R.id.detail_look_about)
	TextView detail_look_about;

	@BindView(R.id.house_detail_title)
	TextView house_detail_title;

	@BindView(R.id.atv_map_name)
	AppCompatTextView atv_map_name;

	@BindView(R.id.house_detail_desc)
	TextView house_detail_desc;

	@BindView(R.id.house_detail_keys)
	LinearLayout house_detail_keys;

	@BindView(R.id.detail_rl_map)
	RelativeLayout detail_rl_map;

	@BindView(R.id.detail_bt_nearby)
	Button detail_bt_nearby;

	@BindView(R.id.appbar)
	AppBarLayout home_detail_bar;

	@BindView(R.id.detail_nv_scroll)
	NestedScrollView detail_nv_scroll;

	@BindView(R.id.add_look_about)
	Button add_look_about;

	@BindView(R.id.detail_ll_trend)
	LinearLayout detail_ll_trend;

	@BindView(R.id.detail_support_line)
	View detail_support_line;

	@BindView(R.id.detail_nearby_line)
	View detail_nearby_line;

	@BindView(R.id.coordinatorLayout)
	CoordinatorLayout coordinatorLayout;

	@BindView(R.id.detail_tv_street)
	TextView detail_tv_street;

	private StringBuffer houseInfo;

	private LayoutInflater inflater;

	private DrawableRequestBuilder<String> requestBuilder;

	private int fromType;

	private CompositeSubscription mCompositeSubscription;

	@Override
	protected int getLayoutId() {
		return R.layout.frag_house_detail;
	}

	private int houseType;
	private String estExtId;

	private boolean isSend;

	//	private MenuItem menu_like, menu_share;
	@BindView(R.id.house_iv_like)
	ImageView menu_like;

	@BindView(R.id.house_iv_share)
	ImageView menu_share;

	// 滑动距离及坐标
	private float xDistance, yDistance, xLast, yLast;
	private boolean isToTop = true;
	private boolean isBlackIcon;

	@Override
	protected void init() {
//		EventBus.getDefault().register(this);
		inflater = LayoutInflater.from(getActivity());

		fromType = getArguments().getInt(FROM_TYPE, FROM_TYPE_NORMAL);
		houseType = getArguments().getInt(MapFragment.HOUSE_TYPE_KEY, MapFragment.HOUSE_TYPE_SECOND);

		home_detail_bar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
			@Override
			public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

				if (isSend){
					if (verticalOffset==0){
						Observable.timer(300, TimeUnit.MILLISECONDS)
								.subscribe(time -> {
									isToTop = true;
//									Logger.e("滑动到顶部"+System.currentTimeMillis());
								});
					}else {
						isToTop = false;
						RxBus.getDefault().send(new TopViewEvent(false));
					}
				}

//				Logger.i("verticalOffset:"+verticalOffset);

				if (verticalOffset>-MyUtils.px2dip(BaseApplication.getContext(),350) && isBlackIcon){
					toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
					if (menu_like!=null){
						if (collectId>0){
//							menu_like.setIcon(R.drawable.ic_liked);
						}else {
							menu_like.setImageResource(R.drawable.ic_like_menu_white);
//							menu_like.setIcon(R.drawable.ic_like_menu_white);
						}
						menu_share.setImageResource(R.drawable.ic_share_menu_white);
//						menu_share.setIcon(R.drawable.ic_share_menu_white);
					}
					isBlackIcon = false;
					center_title.setText("");
				}else if (verticalOffset <=-MyUtils.px2dip(BaseApplication.getContext(),350) && !isBlackIcon){
					toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
					if (menu_like!=null){
						if (collectId>0){
//							menu_like.setIcon(R.drawable.ic_liked);
						}else {
							menu_like.setImageResource(R.drawable.ic_like_menu_black);
						}
						menu_share.setImageResource(R.drawable.ic_share_menu_black);
					}
					center_title.setText(estName);
					isBlackIcon = true;
				}
			}
		});

		coordinatorLayout.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent ev) {
				if (!isToTop){
					return false;
				}

				switch (ev.getAction()){
					case MotionEvent.ACTION_DOWN:
						xDistance = yDistance = 0f;
						xLast = ev.getX();
						yLast = ev.getY();
						break;
					case MotionEvent.ACTION_MOVE:
						final float curX = ev.getX();
						final float curY = ev.getY();

						xDistance += Math.abs(curX - xLast);
						yDistance += Math.abs(curY - yLast);
						xLast = curX;
						yLast = curY;

						if(yDistance > xDistance && yDistance>100){
							Logger.e("拦截的时间:"+System.currentTimeMillis());
							RxBus.getDefault().send(new TopViewEvent(true));
							isSend = false;
							isToTop = false;
						}
						break;
				}
				return true;
			}
		});

		home_detail_bar.setExpanded(true);

		detail_nv_scroll.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent ev) {
				if (!isToTop){
					return false;
				}
				switch (ev.getAction()){
					case MotionEvent.ACTION_DOWN:
						xDistance = yDistance = 0f;
						xLast = ev.getX();
						yLast = ev.getY();
						break;
					case MotionEvent.ACTION_MOVE:
						final float curX = ev.getX();
						final float curY = ev.getY();

						xDistance += Math.abs(curX - xLast);
						yDistance += Math.abs(curY - yLast);
						xLast = curX;
						yLast = curY;

						if(yDistance > xDistance && yDistance>100){
							RxBus.getDefault().send(new TopViewEvent(true));
							isSend = false;
							isToTop = false;
						}
						break;
				}
				return false;
			}
		});

		mCompositeSubscription = new CompositeSubscription();

		mCompositeSubscription.add(RxBus.getDefault().toObservable(SlidingEvent.class)
				.subscribe(slidingEvent -> {
					if (slidingEvent.isToUp()) {
//						RxBus.getDefault().send(new TopViewEvent(false));
						isSend = true;
						toolbar.setVisibility(View.VISIBLE);
						detail_tv_street.setVisibility(View.VISIBLE);
//						detail_fb_share.setVisibility(View.VISIBLE);
//						detail_fb_like.setVisibility(View.VISIBLE);
						vt_detail_house_info.setVisibility(View.GONE);
						detail_dl_imgs.showText(View.VISIBLE);
					}

					if (slidingEvent.isToBottom()) {
						isSend = false;
						isToTop = false;
						toolbar.setVisibility(View.INVISIBLE);
						detail_tv_street.setVisibility(View.GONE);
//						detail_fb_share.setVisibility(View.GONE);
//						detail_fb_like.setVisibility(View.GONE);
						vt_detail_house_info.setVisibility(View.VISIBLE);
						detail_dl_imgs.showText(View.INVISIBLE);
					}
				}));

		if (fromType == FROM_TYPE_NORMAL) {  //从房源列表进
			((HouseDetail) getActivity()).showToolbar(false);
			toolbar.setTitle("");
			((HouseDetail) getActivity()).setToolbar(toolbar);
			detail_dl_imgs.showText(View.VISIBLE);
			detail_tv_street.setVisibility(View.VISIBLE);
		} else {							 //从地图进
			detail_dl_imgs.showText(View.GONE);
//			detail_fb_share.setVisibility(View.GONE);
//			detail_fb_like.setVisibility(View.GONE);

			if (getActivity() instanceof MainTabActivity){
				toolbar.setTitle("");
				toolbar.setVisibility(View.INVISIBLE);

				MainTabActivity mainTabActivity = (MainTabActivity) getActivity();
				mainTabActivity.setSupportActionBar(toolbar);
				mainTabActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
				toolbar.setNavigationOnClickListener(view -> {
					if (mainTabActivity.getMapFragment()!=null){
						mainTabActivity.getMapFragment().hiddenBottomWindow(false, true);
					}
				});
			}
			vt_detail_house_info.inflate();
			bottom_house_area = (TextView) rootView.findViewById(R.id.bottom_house_area);
			bottom_house_info = (TextView) rootView.findViewById(R.id.bottom_house_info);
			bottom_house_price = (TextView) rootView.findViewById(R.id.bottom_house_price);
			bottom_house_money = (TextView) rootView.findViewById(R.id.bottom_house_money);
		}

		if (houseType == MapFragment.HOUSE_TYPE_NEW) {
			estExtId = getArguments().getString(ESTEXT_ID_KEY);
			mPresenter.getNewHouseDetail(estExtId);
			mPresenter.getSaleApartments(estExtId);
			mPresenter.getExerciseList(estExtId);
			if (DataHolder.getInstance().isUserLogin()){
				mPresenter.checkCollect(DataHolder.getInstance().getUserId(), estExtId);
			}

			detail_look_about.setVisibility(View.GONE);
			add_look_about.setVisibility(View.GONE);
			detail_support_line.setVisibility(View.GONE);
			detail_nearby_line.setVisibility(View.GONE);
		} else {

			postId = getArguments().getString(HOUSE_ID_KEY);

			Map<String, String> params = new HashMap<>();

			if (houseType == MapFragment.HOUSE_TYPE_SECOND) {
				params.put("PostType", "S");
				detail_support_line.setVisibility(View.GONE);
			} else{
				params.put("PostType", "R");
			}

			params.put("PostId", postId);
			mPresenter.getHouseDetail(params);

			if (DataHolder.getInstance().isUserLogin()){
				mPresenter.checkCollect(DataHolder.getInstance().getUserId(), postId);
			}

			mPresenter.getHouseImageList(postId, NetContents.IMAGE_BIG_WIDTH, NetContents.IMAGE_BIG_HEIGHT);
		}

		mCompositeSubscription.add(RxBus.getDefault().toObservable(ShareEvent.class)
				.subscribe(shareEvent -> {
					switch (shareEvent.getEventType()){
						case ShareEvent.EVENT_TYPE_SUCCESS:
							shareDialog.dismiss();
							toast("分享成功");
							break;
						case ShareEvent.EVENT_TYPE_CANCLE:
							toast("分享被取消");
							break;
						case ShareEvent.EVENT_TYPE_FAIL:
							toast("分享失败");
							break;
					}
				}));

		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
		detail_nearby_house.setLayoutManager(linearLayoutManager);

		showUIByHouseType();

		requestBuilder = GlideLoad.init(this);

		detail_dl_imgs.setRequestBuilder(requestBuilder);

		detail_dl_imgs.setImgViewPagerClickListener(position -> {
			Intent intent = new Intent(getActivity(), ImageBrowseActivity.class);
			intent.putExtra(ImageBrowseActivity.DEFAULT_POSITION_KEY, position);
			intent.putExtra(MapFragment.HOUSE_TYPE_KEY, houseType);
			intent.putParcelableArrayListExtra(ImageBrowseActivity.IMAGE_DATA_KEY, imageBrowseList);
			startActivityForResult(intent, 103);
		});

		detail_tv_street.setOnClickListener(v->{
			if (latitude > 0 && longitude > 0) {
				Intent intent = new Intent(getActivity(), PanoramaActivity.class);
				intent.putExtra(NearbyFragment.LATITUDE_KEY, latitude);
				intent.putExtra(NearbyFragment.LONGITUDE_KEY, longitude);
				intent.putExtra(PanoramaActivity.PANORAMA_MARK_NAME, estName);
				getActivity().startActivity(intent);
			} else {
				toast("该房源没有经纬度信息");
			}
		});
	}

	private ArrayList<ImageBean> imageBrowseList = new ArrayList<>();

	private String estCode, estName, gscopeName;

	@OnClick(R.id.detail_bt_est)
	public void estDetailClick() {
		Intent intent = new Intent(getActivity(), VillageDetail.class);
		intent.putExtra(VillageDetailFragment.ESTATE_CODE_KEY, estCode);
		startActivity(intent);
	}

//	@OnClick(R.id.detail_iv_back)
//	public void pressBack(){
//		RxBus.getDefault().send(new BottomWindowEvent(false));
//	}


//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		setHasOptionsMenu(true);
//		return super.onCreateView(inflater, container, savedInstanceState);
//	}

//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//
//	}

//	@Override
//	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//		super.onCreateOptionsMenu(menu, inflater);
//		menu.clear();
//		inflater.inflate(R.menu.village_detail_menu, menu);
//		menu_like = menu.findItem(R.id.menu_ic_like);
//		menu_share = menu.findItem(R.id.menu_ic_share);
//	}

	/**
	 * 未读数量刷新
	 * @param
	 */
//	public void onEventMainThread(UnreadEvent e) {
//		refreshUnreadCount();
//	}

//	/**
//	 * 新消息监听
//	 *
//	 */
//	public void onEventMainThread(final Event.OnReceiveMessageEvent event) {
//		refreshUnreadCount();
//	}

//	private void refreshUnreadCount(){
//		//获取未读消息数
//		int count = RongIM.getInstance().getUnreadCount(Conversation.ConversationType.PRIVATE);
//		DataHolder.getInstance().setMessageCount(count);
//		if (menuItem != null) {
//			if (count > 0) {
//				menuItem.setIcon(R.drawable.ic_has_msg);
//			} else {
//				menuItem.setIcon(R.drawable.ic_msg_white_24dp);
//			}
//		}
//	}

	@OnClick(R.id.house_iv_share)
	public void share(){
		shareClick();
	}

	@OnClick(R.id.house_iv_like)
	public void like(){
		collectClick();
	}

//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//
//		if (item.getItemId()==R.id.menu_ic_like){
//			collectClick();
//		}else if (item.getItemId()==R.id.menu_ic_share){
//			shareClick();
//		}
//		return super.onOptionsItemSelected(item);
//	}

	@Override
	protected HouseDetailPresenter createPresenter() {
		return new HouseDetailPresenter();
	}

	@Override
	public void onDestroy() {
		unbindDrawables(rootView);
//		EventBus.getDefault().unregister(this);
		super.onDestroy();
		if (mCompositeSubscription != null && mCompositeSubscription.hasSubscriptions()) {
			mCompositeSubscription.unsubscribe();
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}


	@Override
	public void onDetach() {
		super.onDetach();
	}

	/**
	 * 移除Fragment视图上的View
	 */
	private void unbindDrawables(View view) {
		if (view.getBackground() != null)
		{
			view.getBackground().setCallback(null);
		}
		if (view instanceof ViewGroup && !(view instanceof AdapterView))
		{
			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++)
			{
				unbindDrawables(((ViewGroup) view).getChildAt(i));
			}
			((ViewGroup) view).removeAllViews();
		}
	}

	@Override
	public void showMsg(String msg) {
		((BaseActivity) getActivity()).toast(msg);
	}

	@Override
	public void noHouse() {
		new AlertDialog.Builder(getActivity())
				.setTitle("提示")
				.setMessage("该房源已售出或已删除")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						((HouseDetail)getActivity()).finish();
					}
				})
				.show();
	}

	private HashMap<String, String> nearbyParams = new HashMap<>();
	private String shareTitle;
	private String shareImage;
	private String shareSumtitle;
	private String shareUrl;

	private String defStaffNo, defDesc, defStaff400Tel;
	private double salePrice;

	private boolean isOnLine;

	private String imgFullPath;

	private HouseDetailBo houseDetailBo;

	@Override
	public void setHouseDetail(HouseDetailBo detail) {
		houseDetailBo = detail;
		detail_nv_scroll.scrollTo(0, 0);

		imgFullPath = TextUtils.isEmpty(detail.getFullImagePath()) ? AppContents.POST_DEFAULT_IMG_URL : detail.getFullImagePath();
		isOnLine = detail.isIsOnline();
		houseInfo = new StringBuffer();
		estCode = detail.getEstateCode();
		estName = detail.getEstateName();
		gscopeName = detail.getGscopeName();

		defStaffNo = detail.getStaffNo();
		defStaff400Tel = detail.getStaff400Tel();
		defDesc = detail.getPlainDescription();

		mPresenter.getStaffDetail(defStaffNo);

		if (houseType==MapFragment.HOUSE_TYPE_SECOND){
			Map<String, String> eParam = new HashMap<>();
			eParam.put("EstateCode", estCode);
			Map<String, String> gParam = new HashMap<>();
			gParam.put("GscopeId", detail.getGScopeId()+"");
			mPresenter.getPriceTrend(gParam, eParam);
		}

		latitude = detail.getLat();
		longitude = detail.getLng();

		if (latitude==0 || longitude==0){
			detail_rl_map.setVisibility(View.GONE);
			detail_bt_nearby.setVisibility(View.GONE);
		}

		shareTitle = detail.getTitle();

		if (fromType == FROM_TYPE_MAP){

			bottom_house_info.setText(detail.getRoomCount()+"室"+detail.getHallCount()+"厅"+detail.getToiletCount()+"卫");
			bottom_house_area.setText("|  "+ MyUtils.formatHouseArea(detail.getGArea())+"m²");
			if (houseType==MapFragment.HOUSE_TYPE_SECOND){
				bottom_house_price.setText("|  "+ MyUtils.format2(detail.getUnitSalePrice()/10000)+"万/m²");
				bottom_house_money.setText(MyUtils.format2String(detail.getSalePrice()/10000)+"万");
			}else if (houseType==MapFragment.HOUSE_TYPE_RENT){
				bottom_house_price.setVisibility(View.GONE);
				bottom_house_money.setText(MyUtils.format2String(detail.getRentPrice())+"元/月");
			}
		}

//		houseInfo.append(detail.getEstateName()).append(" ");

		houseInfo.append(detail.getRoomCount()+"室"+detail.getHallCount()+"厅").append(" ")
				.append(MyUtils.format2String(detail.getGArea())).append("㎡ ");

		//获取周边房源
		nearbyParams.put("Lat", detail.getLat() + "");
		nearbyParams.put("Lng", detail.getLng() + "");
		nearbyParams.put("Round", "1000");
		if (houseType == MapFragment.HOUSE_TYPE_SECOND) {
			nearbyParams.put("PostType", "s");
			double MinSalePrice = detail.getSalePrice() * 0.95;
			double MaxSalePrice = detail.getSalePrice() * (1.05);
			nearbyParams.put("MinSalePrice", MinSalePrice + "");
			nearbyParams.put("MaxSalePrice", MaxSalePrice + "");
			houseInfo.append(MyUtils.format2String(detail.getSalePrice()/10000)).append("万");

		} else if (houseType == MapFragment.HOUSE_TYPE_RENT) {
			nearbyParams.put("PostType", "r");
			double minRentPrice = detail.getRentPrice() * 0.95;
			double maxRentPrice = detail.getRentPrice() * (1.05);
			nearbyParams.put("MinRentPrice", minRentPrice + "");
			nearbyParams.put("MaxRentPrice", maxRentPrice + "");
			houseInfo.append(MyUtils.format2String(detail.getRentPrice())+"元/月").append(" ");

			String support = detail.getApplianceInfo();
			if (support!=null && !TextUtils.isEmpty(support)){
				String[] supports = support.split(",");
				for (int i=0; i<supports.length; i++){
					setSupportRes(supportList.get(i), supportImgs.get(i), supports[i], false);
				}

				int index = supports.length;//supports.length;
				for (Map.Entry<String, Integer> entry : supportMap.entrySet()){
					TextView item = supportList.get(index);
					ImageView icon = supportImgs.get(index);
					item.setText(entry.getKey());
					item.setTextColor(Color.GRAY);
					icon.setImageDrawable(ic_support_no);
//					item.setCompoundDrawablesWithIntrinsicBounds(ic_support_no, null, null, null);
//					item.setCompoundDrawables(ic_support_no, null, null, null);
					index++;
				}
			} else {
				vs_rent_support.setVisibility(View.GONE);
			}
		}
		mPresenter.getNearbyHouse(nearbyParams);

		house_detail_title.setText(detail.getTitle());
		GlideLoad.load(new GlideLoad.Builder(requestBuilder, MyUtils.getBaiduMapImgUrl(detail.getLng(), detail.getLat()))
				.into(detail_house_map));

		atv_map_name.setText("\t\t\t\t"+detail.getAddress()+"\t\t\t\t");

		//房源描述
		setHouseDesc(detail);

		if (houseType == MapFragment.HOUSE_TYPE_SECOND){
			house_detail_keys.removeAllViews();
			if (detail.getKeyWords() != null && !TextUtils.isEmpty(detail.getKeyWords())) {
				String[] keyWords = detail.getKeyWords().split(",");
				int length = keyWords.length;
				if (length > 5) {
					length = 5;
				}

				for (int i = 0; i < length; i++) {
					TextView key = (TextView) inflater.inflate(R.layout.item_key_text, house_detail_keys, false);
					key.setText(keyWords[i]);
					LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) key.getLayoutParams();
					params.setMargins(0, 0, 10, 0);
					house_detail_keys.addView(key);
				}
			}else {
				house_detail_keys.setVisibility(View.GONE);
			}
		}else {
			house_detail_keys.setVisibility(View.GONE);
		}

		if (!detail.isIsOnline()){
			detail_free_talk.setBackgroundResource(R.drawable.bt_line_gray);
			detail_free_talk.setEnabled(false);
			detail_free_talk.setText("该房源已下架");
		}

		setTwoColorText(detail_tv_floor, 6, R.color.normalText, "楼层\t\t\t\t" + (TextUtils.isEmpty(detail.getFloorDisplay()) ? "--":detail.getFloorDisplay()));

		setTwoColorText(detail_tv_direction, 6, R.color.normalText, "朝向\t\t\t\t" + (TextUtils.isEmpty(detail.getDirection()) ? "--":detail.getDirection()));

		setTwoColorText(detail_tv_fitment, 6, R.color.normalText, "装修\t\t\t\t" + (TextUtils.isEmpty(detail.getFitment()) ? "--":detail.getFitment()));

		long time = detail.getOpDate()*1000L;
		setTwoColorText(detail_tv_year, 6, R.color.normalText, "年代\t\t\t\t" + DateUtil.format(time, DateUtil.FORMAT3) + "年");

		setTwoColorText(detail_tv_est, 6, R.color.estateColor, "小区\t\t\t\t"+detail.getEstateName());

		detail_tv_est.setOnClickListener(view -> {
			Intent intent = new Intent(getActivity(), VillageDetail.class);
			intent.putExtra(VillageDetailFragment.ESTATE_CODE_KEY, estCode);
			startActivity(intent);
		});
		detail_tv_est.setVisibility(View.VISIBLE);

		if (detail.getRailWayInfos()!=null && detail.getRailWayInfos().size()>0){
			RailwayInfoBo railwayInfoBo = detail.getRailWayInfos().get(0);
			setTwoColorText(detail_tv_address, 6, R.color.normalText, "地铁\t\t\t\t" + "距离地铁"+railwayInfoBo.getRailLineName()+railwayInfoBo.getRailWayName()+"站"+MyUtils.format2String(railwayInfoBo.getDistance())+"米");
		}
		setTwoColorText(detail_tv_code, 6, R.color.normalText, "编号\t\t\t\t" + (TextUtils.isEmpty(detail.getAdsNo()) ? "--":detail.getAdsNo()));
		detail_tv_code.setOnLongClickListener(view -> {
			MyUtils.copyText2Clip(getActivity(), detail.getAdsNo());
			toast("已复制");
			return false;
		});


		if (houseType == MapFragment.HOUSE_TYPE_SECOND) {

			if (detail.getICPNumber()!=null){
				setTwoColorText(detail_tv_record, 6, R.color.normalText, "备案\t\t\t\t" + (TextUtils.isEmpty(detail.getICPNumber()) ? "--":detail.getICPNumber()));
			}

			shareUrl = NetContents.SHARE_HOUSE_HOST + "ershoufang/"+detail.getPostId()+".html";
			shareSumtitle = detail.getEstateName()+"\n"+detail.getRoomCount()+"室"
					+detail.getHallCount()+"厅"+" "+detail.getGArea()+"m²\n"+"总价:"+(int)detail.getSalePrice()/10000+"万";

			setTwoColorText(detail_unit_price, 6, R.color.normalText, "单价\t\t\t\t" + MyUtils.format2String(detail.getUnitSalePrice()) + "元/㎡");

			house_mt_price.setLeftText(MyUtils.format2String(detail.getSalePrice() / 10000));
			house_mt_price.setRightText("万");

			house_mt_room.setLeftText(detail.getRoomCount() + "");
			house_mt_hall.setLeftText(detail.getHallCount() + "");
			house_mt_toilet.setLeftText(detail.getToiletCount() + "");
			house_mt_area.setLeftText(MyUtils.formatHouseArea(detail.getGArea())+"");

			salePrice = detail.getSalePrice();
			detail_sale_pc.setTotalPriceAndRate(salePrice, 0.049);
		} else if (houseType == MapFragment.HOUSE_TYPE_RENT) {

			shareUrl = NetContents.SHARE_HOUSE_HOST + "zufang/"+detail.getPostId()+".html";
			shareSumtitle = detail.getEstateName()+"\n"+detail.getRoomCount()+"室"
					+detail.getHallCount()+"厅"+" "+detail.getGArea()+"m²\n"+"租金:"+MyUtils.format2String(detail.getRentPrice())+"元/月";

//			setTwoColorText(detail_rent_type, 6, R.color.normalText, "方式\t\t\t\t" + detail.getRentType());
//
//			setTwoColorText(detail_price_type, 6, R.color.normalText, "付款\t\t\t\t" + detail.getRentPayType());

			house_mt_price.setLeftText(MyUtils.format2String(detail.getRentPrice()));
			house_mt_price.setRightText("元/月");
			house_mt_room.setLeftText(detail.getRoomCount() + "");
			house_mt_hall.setLeftText(detail.getHallCount() + "");
			house_mt_toilet.setLeftText(detail.getToiletCount() + "");
			house_mt_area.setLeftText(MyUtils.formatHouseArea(detail.getGArea()));
		}

//		houseInfo.append(shareUrl);
	}

	private void setHouseDesc(HouseDetailBo detail) {

		//poi搜索
		PoiSearch mPoiSearch1 = PoiSearch.newInstance();
		PoiSearch mPoiSearch2 = PoiSearch.newInstance();
		PoiSearch mPoiSearch3 = PoiSearch.newInstance();
		PoiSearch mPoiSearch4 = PoiSearch.newInstance();
		MyPoiListener myPoiListener1 = new MyPoiListener(0);
		MyPoiListener myPoiListener2 = new MyPoiListener(1);
		MyPoiListener myPoiListener3 = new MyPoiListener(2);
		MyPoiListener myPoiListener4 = new MyPoiListener(3);
		mPoiSearch1.setOnGetPoiSearchResultListener(myPoiListener1);
		mPoiSearch2.setOnGetPoiSearchResultListener(myPoiListener2);
		mPoiSearch3.setOnGetPoiSearchResultListener(myPoiListener3);
		mPoiSearch4.setOnGetPoiSearchResultListener(myPoiListener4);
		mPoiSearch1.searchNearby(new PoiNearbySearchOption()
				.keyword("学校")
				.location(new LatLng(detail.getLat(),detail.getLng()))
				.radius(1500)
				.sortType(PoiSortType.distance_from_near_to_far));
		mPoiSearch2.searchNearby(new PoiNearbySearchOption()
				.keyword("医院")
				.location(new LatLng(detail.getLat(),detail.getLng()))
				.radius(1500)
				.sortType(PoiSortType.distance_from_near_to_far));
		mPoiSearch3.searchNearby(new PoiNearbySearchOption()
				.keyword("商场超市")
				.location(new LatLng(detail.getLat(),detail.getLng()))
				.radius(1500)
				.sortType(PoiSortType.distance_from_near_to_far));

		mPoiSearch4.searchNearby(new PoiNearbySearchOption()
				.keyword("公交")
				.location(new LatLng(detail.getLat(),detail.getLng()))
				.radius(1000)
				.sortType(PoiSortType.distance_from_near_to_far));


		house_detail_desc.setText("\t\t\t" + detail.getBaseInfo() + detail.getPostFuture());
//		house_detail_desc.setText("\t\t\t" + detail.getPlainDescription().replaceAll("Ď", ""));
	}

	private void setTwoColorText(TextView tv, int start, int colorRes, String text){
		int color = ContextCompat.getColor(getActivity(), colorRes);
		SpannableString strStyle = new SpannableString(text);
		strStyle.setSpan(new ForegroundColorSpan(color), start, strStyle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		tv.setText(strStyle);
		tv.setVisibility(View.VISIBLE);
	}

	private List<StaffComment> staffList;

	@Override
	public void setStaffComment(List<StaffComment> staffComments) {
		this.staffList = staffComments;
		if (defStaff!=null){
			if (staffList==null){
				staffList = new ArrayList<>();
			}
			defStaff.setCnName(defStaff.getStaffName());
			staffList.add(0, defStaff);
		}
		staffDo();
	}

	private NewHouseDetail newHouseDetail;

	@Override
	public void setNewHouseDetail(NewHouseDetail detail) {
		newHouseDetail = detail;
		house_detail_title.setText(detail.getAdName());

		shareTitle = detail.getAdName();
		estName = detail.getAdName();

		shareUrl = NetContents.SHARE_HOUSE_HOST + "xinfang/lp-"+estExtId;
		houseInfo = new StringBuffer();
		houseInfo.append(detail.getEstType()).append(" ")
				.append(detail.getBaseNewProp().getAddress()).append(" ")
				.append(detail.getAveragePrice()+"元/㎡").append(" ");

		if(detail.getAveragePrice()>0){
			shareSumtitle = detail.getBaseNewProp().getAddress() +"\n均价:"+detail.getAveragePrice()+"元/㎡";
		}else {
			shareSumtitle = detail.getBaseNewProp().getAddress() + "\n均价: 暂无";
		}

		latitude = detail.getBaseNewProp().getLat();
		longitude = detail.getBaseNewProp().getLng();

		if (latitude==0 || longitude==0){
			detail_rl_map.setVisibility(View.GONE);
			detail_bt_nearby.setVisibility(View.GONE);
		}

		if (fromType == FROM_TYPE_MAP){

			bottom_house_info.setText(detail.getEstType());
			bottom_house_area.setText("|  "+ ("认购".equals(detail.getBaseNewProp().getStatus()) || "持销".equals(detail.getBaseNewProp().getStatus()) || "尾盘".equals(detail.getBaseNewProp().getStatus()) ? "在售" : "待售"));
			bottom_house_price.setVisibility(View.GONE);
			if(detail.getAveragePrice()>0){
				bottom_house_money.setText("均价:"+MyUtils.format2String(detail.getAveragePrice())+"元/㎡");
			}else {
				bottom_house_money.setText("均价:暂无");
			}

		}

		GlideLoad.load(new GlideLoad.Builder(requestBuilder, MyUtils.getBaiduMapImgUrl(detail.getBaseNewProp().getLng(), detail.getBaseNewProp().getLat()))
				.into(detail_house_map));

		atv_map_name.setText(detail.getBaseNewProp().getAddress());

		if (detail.getAveragePrice()>0){
			new_mt_price.setLeftText(MyUtils.format2String(detail.getAveragePrice()));
		}else {
			new_mt_price.setLeftAndRight(null, "暂无");
		}
		new_property_type.setText(detail.getEstType());
		new_house_status.setText(("认购".equals(detail.getBaseNewProp().getStatus()) || "持销".equals(detail.getBaseNewProp().getStatus()) || "尾盘".equals(detail.getBaseNewProp().getStatus()) ? "在售" : "待售"));

		detail_new_kai.setText("开盘时间\t\t\t\t" + detail.getOpDate());
		detail_new_kai.setVisibility(View.VISIBLE);
		detail_new_chan.setText("产权\t\t\t\t" + detail.getPropertyRight() + "年");
		detail_new_chan.setVisibility(View.VISIBLE);
		detail_new_ban.setText("板块\t\t\t\t" + detail.getBaseNewProp().getDistrict().getGScopeCnName() + "/" + detail.getBaseNewProp().getGScope().getGScopeCnName());
		detail_new_ban.setVisibility(View.VISIBLE);
		detail_new_develop.setText("开发商\t\t\t\t" + detail.getBaseNewProp().getDeveloper());
		detail_new_develop.setVisibility(View.VISIBLE);
		if (TextUtils.isEmpty(detail.getLiveDate())){
			detail_new_jiao.setText("交房  暂无");
		}else {
			detail_new_jiao.setText("交房\t\t\t\t" + detail.getLiveDate());
		}
		detail_new_jiao.setVisibility(View.VISIBLE);

		if (detail.getRoomRatio()>0){
			detail_new_get.setText("得房率\t\t\t\t" + detail.getRoomRatio()+"%");
			detail_new_get.setVisibility(View.VISIBLE);
		}

		if (detail.getBaseNewProp().getMgtPrice()>0){
			detail_new_property.setText("物业费\t\t\t\t" + detail.getBaseNewProp().getMgtPrice() + "元/㎡/月");
			detail_new_property.setVisibility(View.VISIBLE);
		}

		if (!detail.isIsOnline()){
			detail_free_talk.setBackgroundResource(R.drawable.bt_line_gray);
			detail_free_talk.setEnabled(false);
			detail_free_talk.setText("该房源已下架");
		}
	}

	@Override
	public void setNearbyHouse(List<HouseBo> nearbyHouses) {

		if (nearbyHouses==null || nearbyHouses.size()==0){
			detail_nearby_house.setVisibility(View.GONE);
			detail_nearby_type.setVisibility(View.GONE);
			detail_open_list.setVisibility(View.GONE);
			detail_ll_nearby.setVisibility(View.GONE);
		}else {

			List<HouseBo> list = new ArrayList<>();
			for (HouseBo item : nearbyHouses){
				if (estCode.equalsIgnoreCase(item.getEstateCode())){
					continue;
				}else {
					list.add(item);
				}
			}

			if (list.size()==0){
				detail_nearby_house.setVisibility(View.GONE);
				detail_nearby_type.setVisibility(View.GONE);
				detail_open_list.setVisibility(View.GONE);
				detail_ll_nearby.setVisibility(View.GONE);
				return;
			}

			NearbyHouseAdapter nearbyAdapter = new NearbyHouseAdapter(getActivity(), R.layout.item_detail_nearby, list, houseType);
			nearbyAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
				@Override
				public void onItemClick(View view, RecyclerView.ViewHolder holder, Object o, int position) {

					if (fromType == FROM_TYPE_NORMAL){
						addFragment(HouseDetailFragment.getInstance(list.get(position).getPostId(), HouseDetailFragment.FROM_TYPE_NORMAL, houseType));
					}else {
						Intent intent = new Intent(getActivity(), HouseDetail.class);
						intent.putExtra(MapFragment.HOUSE_TYPE_KEY, houseType);
						intent.putExtra(HouseDetailFragment.HOUSE_ID_KEY, list.get(position).getPostId());
						startActivity(intent);
					}
				}

				@Override
				public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, Object o, int position) {
					return false;
				}
			});
			detail_nearby_house.setAdapter(nearbyAdapter);
		}
	}

	@Override
	public void setSaleApartments(List<ApartmentBo> apartments) {
		if (apartments==null || apartments.size()==0){
			detail_nearby_house.setVisibility(View.GONE);
			detail_nearby_type.setVisibility(View.GONE);
			detail_ll_nearby.setVisibility(View.GONE);
		}else {
			mPresenter.getNewHouseImage(apartments.get(0).getEstId());
			SaleApartmentAdapter adapter = new SaleApartmentAdapter(getActivity(), R.layout.item_sale_apartment, apartments);
			adapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
				@Override
				public void onItemClick(View view, RecyclerView.ViewHolder holder, Object o, int position) {

					ApartmentBo apartmentBo = apartments.get(position);

					ArrayList<ImageBean> imageBrowseList = new ArrayList<>();
					imageBrowseList.add(new ImageBean(apartmentBo.getImageUrl(), ""));

					Intent intent = new Intent(getActivity(), ImageBrowseActivity.class);
					intent.putExtra(ImageBrowseActivity.DEFAULT_POSITION_KEY, 0);
					intent.putParcelableArrayListExtra(ImageBrowseActivity.IMAGE_DATA_KEY, imageBrowseList);
					startActivityForResult(intent, 103);
				}

				@Override
				public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, Object o, int position) {
					return false;
				}
			});
			detail_nearby_house.setAdapter(adapter);
		}
	}

	@Override
	public void setHouseImageList(List<HouseImageBo> imageList) {
		List<String> imgList = new ArrayList<>();
		if (imageList != null) {
			shareImage = imageList.get(0).getCustomImagePath();
			imageBrowseList.clear();
			for (HouseImageBo image : imageList){
				imgList.add(image.getCustomImagePath());
				imageBrowseList.add(new ImageBean(image.getImagePath().replace("c.jpg", "f.jpg"), image.getImageTitle()));
			}
		} else {
			imgList.add(AppContents.POST_DEFAULT_IMG_URL);
		}
		detail_dl_imgs.setImgList(imgList);
	}

	@Override
	public void setExercises(List<ExerciseListBo> exercises) {
		if (exercises==null || exercises.size()==0){
			detail_fl_exercise.setVisibility(View.GONE);
		}else {

			List<ExerciseListBo> list = new ArrayList<>();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date currentDate = new Date(System.currentTimeMillis());

			for (ExerciseListBo item : exercises){
				String startStr = item.getStartDate();
				String endStr = item.getEndDate();

				try {
					Date strDate = format.parse(startStr);  //2016-09-10
					Date endDate = format.parse(endStr);  //2016-05-30
					if (currentDate.after(endDate)){
						continue;
					}

					if (currentDate.before(strDate)){
						continue;
					}

				} catch (ParseException e) {
					e.printStackTrace();
				}

				list.add(item);

			}

			if (list.size()==0){
				detail_fl_exercise.setVisibility(View.GONE);
				return;
			}

			ExerciseAdapter exerciseAdapter = new ExerciseAdapter(getActivity().getSupportFragmentManager(), list);
			detail_new_exercises.setAdapter(exerciseAdapter);
			detail_new_exercises.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
				@Override
				public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

				}

				@Override
				public void onPageSelected(int position) {
					tv_exercise_number.setText("第"+(position+1)+"/"+exercises.size()+"个");
				}

				@Override
				public void onPageScrollStateChanged(int state) {

				}
			});
			tv_exercise_number.setText("第1/"+exercises.size()+"个");
		}
	}

	@Override
	public void setNewHouseImage(List<NewHouseImageBo> imageBos) {
		imageBrowseList.clear();
		List<String> imgList = new ArrayList<>();
		for (NewHouseImageBo image : imageBos){
			String imgUrl = NetContents.NEW_HOUSE_IMG+image.getFileUrl().substring(0, image.getFileUrl().indexOf("."))+"_1280x720_f.jpg";
			String thumb = NetContents.NEW_HOUSE_IMG+image.getFileUrl().substring(0, image.getFileUrl().indexOf("."))+"_500x400_f.jpg";
			imgList.add(thumb);

			imageBrowseList.add(new ImageBean(imgUrl, image.getImgType()));
		}
		shareImage = imgList.get(0);
		detail_dl_imgs.setImgList(imgList);

	}

	@Override
	public void setPriceTrend(PriceTrendBean priceTrend) {

//		if ((priceTrend.getEstateDealPriceBos()==null || priceTrend.getEstateDealPriceBos().size()==0)
//				&& (priceTrend.getGscopeDealPriceBos()==null || priceTrend.getGscopeDealPriceBos().size()==0)){
//
//		}else {
//			detail_ll_trend.setVisibility(View.VISIBLE);
//			detail_sale_lc2.setEstatePriceBo(estName, gscopeName, priceTrend.getEstateDealPriceBos(), priceTrend.getGscopeDealPriceBos());
//		}

		detail_ll_trend.setVisibility(View.VISIBLE);
		detail_sale_lc2.setEstatePriceBo(estName, gscopeName, priceTrend.getEstateDealPriceBos(), priceTrend.getGscopeDealPriceBos(), priceTrend.getCityDealPriceBos());

//		detail_ll_trend.setVisibility(View.VISIBLE);
//		detail_sale_lc2.setEstatePriceBo(estName, gscopeName, priceTrend.getEstateDealPriceBos(), priceTrend.getGscopeDealPriceBos(), priceTrend.getCityDealPriceBos());

	}

	@Override
	public void setCollectResult(long collectId) {
		this.collectId = collectId;
		if (collectId>0){
			toast("收藏成功");
			menu_like.setImageResource(R.drawable.ic_liked);
//			detail_fb_like.setImageResource(R.drawable.ic_liked);
		}else {
			toast("收藏失败, 请稍后尝试");
		}
	}

	@Override
	public void setReservationResult(boolean result) {
		toast("加入约看成功");
		lookAboutNum ++;
		updateLookAboutNumber(lookAboutNum);
	}

	private long collectId;

	@Override
	public void isCollected(long collectId) {
		this.collectId = collectId;
		if (collectId>0){
			menu_like.setImageResource(R.drawable.ic_liked);
//			detail_fb_like.setImageResource(R.drawable.ic_liked);
		}
	}

	@Override
	public void deleteCollectResult(boolean result) {
		if (result){
			toast("取消收藏成功");
			collectId = 0;
			menu_like.setImageResource(R.drawable.ic_like_menu_white);
//			detail_fb_like.setImageResource(R.drawable.ic_like_menu_black);
		}else {
			toast("取消收藏失败");
		}
	}

	private int lookAboutNum;

	@Override
	public void setLookedAboutNumber(int num) {
		lookAboutNum = num;
		if (num>0){
			updateLookAboutNumber(num);
		}
	}

	private StaffComment defStaff;

	@Override
	public void setStaffDetail(StaffListBean detail) {
		StaffComment staff = new StaffComment();
		staff.setPostId(postId);
		staff.setTitle(detail.getTitle());
		staff.setStaffNo(detail.getStaffNo());
		staff.setStaff400Tel(defStaff400Tel);
		staff.setPostDirection(defDesc);
		staff.setStaffName(detail.getCnName());
		staff.setStaffImage(detail.getStaffImg());
		staff.setStoreName(detail.getStoreName());
		defStaff = staff;
	}

	@Override
	public void setNewHouseStaff(StaffComment staff) {
		this.defStaff = staff;
		if (staffList==null){
			staffList = new ArrayList<>();
		}
		if (defStaff!=null){
			staffList.add(0, defStaff);
		}
		staffDo();
	}

	private void updateLookAboutNumber(int num){
		BadgeView bv = new BadgeView(getActivity());
		bv.setTargetView(detail_look_about);
		bv.setBadgeCount(num);
	}

	@Override
	public void showLoading() {
		showLoadingDialog();
	}

	@Override
	public void dismissLoading() {
		cancelLoadingDialog();
	}

	@Override
	public void showError(String msg) {
		toast(msg);
	}

	private double latitude;
	private double longitude;

	@OnClick(R.id.detail_bt_nearby)
	public void nearByClick() {
		toNearByMap();
	}

	@OnClick(R.id.detail_tv_report)
	public void reportClick() {
		View linearlayout = inflater.inflate(R.layout.dialog_alert, null);
		TextView title = (TextView) linearlayout.findViewById(R.id.title);
		title.setText("是否拨打举报热线021-51787380?");
		Dialog dialog = new AlertDialog.Builder(getContext()).setView(linearlayout).show();
		dialog.getWindow().setWindowAnimations(R.style.AlertDialogTheme_App);
		android.view.WindowManager.LayoutParams p = dialog.getWindow().getAttributes();
		p.width = MyUtils.dip2px(getContext(),300);
		dialog.getWindow().setAttributes(p);
		linearlayout.findViewById(R.id.submit).setOnClickListener((view) -> {
			checkPermission();
			dialog.dismiss();
		});
		linearlayout.findViewById(R.id.cancel).setOnClickListener((view)->dialog.dismiss());

	}

	public static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;

	private void checkPermission() {
		if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);
		} else {
			mPresenter.callPhone(getActivity());
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE) {
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				mPresenter.callPhone(getActivity());
			} else {
				//权限拒绝
				showMsg("权限被拒绝，不能拨打电话!");
			}
		}
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	private String postId;

	private final int STAFF_TO_DESC = 1;
	private final int STAFF_TO_TALK = 0;
	private int staffType;

	@OnClick(R.id.detail_bt_desc)
	public void houseDescClick() {
//		if (staffList==null){
//			mPresenter.getStaffComment(postId, true);
//			staffType = STAFF_TO_DESC;
//			return;
//		}
//		ArrayList<StaffComment> staffComments = new ArrayList<>(staffList);
//		HouseDescribeWindow.getInstance(staffComments, houseInfo.toString(), houseType, postId, imgFullPath, estName).show(getActivity().getSupportFragmentManager(), "HouseDescribeWindow");
		if (houseDetailBo == null) {
			toast("没有获取相应的描述信息");
			return;
		}

		final Dialog dialog = new Dialog(getActivity(), R.style.myDetailDialogStyle);
		View layout = View.inflate(getActivity(), R.layout.house_detail_dialog, null);
		dialog.setContentView(layout);
		ImageView iv_close = (ImageView) layout.findViewById(R.id.iv_close);
		TextView base_info = (TextView) layout.findViewById(R.id.base_info);
		TextView base_feature = (TextView) layout.findViewById(R.id.base_feature);
		TextView base_traffic = (TextView) layout.findViewById(R.id.base_traffic);

		base_info.setText(houseDetailBo.getBaseInfo());
		base_feature.setText(houseDetailBo.getPostFuture());

		if (poiInfoList1 != null && poiInfoList2 != null && poiInfoList3 != null) {
			houseDetailBo.setNearTransportation("周围生活设施配套齐全，学校、医院、商场超市一应俱全。"
					+ houseDetailBo.getNearTransportation());
		} else if (poiInfoList1 == null && poiInfoList2 == null && poiInfoList3 == null) {
			houseDetailBo.setNearTransportation("本房周边配套还在逐步完善中，本区域未来具有较大的升值空间。"
					+ houseDetailBo.getNearTransportation());
		} else {
			houseDetailBo.setNearTransportation("本房周围有较完备的生活配套设施，本区域内房产有很大的升值空间。"
					+ houseDetailBo.getNearTransportation());
		}

		String lines="";
		if (poiInfoList4 != null) {
			for (PoiInfo poiInfo:poiInfoList4) {
				lines = poiInfo.address + ";";
			}
			lines = lines.substring(0, lines.length()-1);
			String ls = "";
			String[] arr = lines.split(";");
			for (int i = 0;i<arr.length;i++) {
				ls += arr[i] + "、";
			}
			houseDetailBo.setNearTransportation(houseDetailBo.getNearTransportation() + "。附近有"+ls.substring(0,ls.length()-1)+"等线路。交通条件很方便。");
		}


		base_traffic.setText(houseDetailBo.getNearTransportation());

		dialog.show();
		// 点击关闭图片
		iv_close.setOnClickListener(v -> dialog.dismiss());
	}

	private TextView house_price_type;
	private MyText house_mt_price, house_mt_room, house_mt_hall, house_mt_toilet, house_mt_area;

	@BindView(R.id.detail_unit_price)
	TextView detail_unit_price;

	@BindView(R.id.detail_tv_floor)
	TextView detail_tv_floor;

	@BindView(R.id.detail_tv_direction)
	TextView detail_tv_direction;

	@BindView(R.id.detail_tv_fitment)
	TextView detail_tv_fitment;

	@BindView(R.id.detail_tv_year)
	TextView detail_tv_year;

	@BindView(R.id.detail_tv_est)
	TextView detail_tv_est;

	@BindView(R.id.detail_tv_address)
	TextView detail_tv_address;

	@BindView(R.id.detail_tv_code)
	TextView detail_tv_code;

	@BindView(R.id.detail_tv_record)
	TextView detail_tv_record;

	@BindView(R.id.detail_rent_type)
	TextView detail_rent_type;

	@BindView(R.id.detail_price_type)
	TextView detail_price_type;

	@BindView(R.id.detail_bt_tools)
	Button detail_bt_tools;

	@BindView(R.id.detail_bt_est)
	Button detail_bt_est;

	//新房相关
	MyText new_mt_price;
	TextView new_property_type, new_house_status;

	@BindView(R.id.detail_new_kai)
	TextView detail_new_kai;

	@BindView(R.id.detail_new_chan)
	TextView detail_new_chan;

	@BindView(R.id.detail_new_ban)
	TextView detail_new_ban;

	@BindView(R.id.detail_new_develop)
	TextView detail_new_develop;

	@BindView(R.id.detail_new_jiao)
	TextView detail_new_jiao;

	@BindView(R.id.detail_new_get)
	TextView detail_new_get;

	@BindView(R.id.detail_new_property)
	TextView detail_new_property;

	@BindView(R.id.detail_bt_desc)
	Button detail_bt_desc;

	@BindView(R.id.house_desc_title)
	TextView house_desc_title;

	@BindView(R.id.detail_more_info)
	Button detail_more_info;

	@BindView(R.id.detail_new_exercises)
	ViewPager detail_new_exercises;

	@BindView(R.id.tv_exercise_number)
	TextView tv_exercise_number;

	@BindView(R.id.detail_fl_exercise)
	FrameLayout detail_fl_exercise;

	@BindView(R.id.detail_nearby_type)
	TextView detail_nearby_type;

	@BindView(R.id.detail_ll_nearby)
	LinearLayout detail_ll_nearby;

	@BindView(R.id.detail_open_list)
	Button detail_open_list;

	private List<TextView> supportList;
	private List<ImageView> supportImgs;
	private int[] ids = new int[]{R.id.rent_tr_0, R.id.rent_tr_1, R.id.rent_tr_2, R.id.rent_tr_3,
			R.id.rent_tr_4, R.id.rent_tr_5, R.id.rent_tr_6, R.id.rent_tr_7, R.id.rent_tr_8, R.id.rent_tr_9,
			R.id.rent_tr_10, R.id.rent_tr_11, R.id.rent_tr_12, R.id.rent_tr_13, R.id.rent_tr_14};

	private int[] icons = new int[]{R.id.rent_ic_0, R.id.rent_ic_1, R.id.rent_ic_2, R.id.rent_ic_3,
			R.id.rent_ic_4, R.id.rent_ic_5, R.id.rent_ic_6, R.id.rent_ic_7, R.id.rent_ic_8, R.id.rent_ic_9,
			R.id.rent_ic_10, R.id.rent_ic_11, R.id.rent_ic_12, R.id.rent_ic_13, R.id.rent_ic_14};

	private Map<String, Integer> supportMap;

	private void showUIByHouseType() {
		switch (houseType) {
			case MapFragment.HOUSE_TYPE_SECOND:

				detail_bt_tools.setVisibility(View.VISIBLE);
				detail_sale_pc.setVisibility(View.VISIBLE);
				detail_dai_text.setVisibility(View.VISIBLE);
				detail_bt_est.setVisibility(View.VISIBLE);

				vs_house_type.inflate();

				house_price_type = (TextView) rootView.findViewById(R.id.house_price_type);
				house_price_type.setText("总价");

				house_mt_price = (MyText) rootView.findViewById(R.id.house_mt_price);
				house_mt_room = (MyText) rootView.findViewById(R.id.house_mt_room);
				house_mt_hall = (MyText) rootView.findViewById(R.id.house_mt_hall);
				house_mt_toilet = (MyText) rootView.findViewById(R.id.house_mt_toilet);
				house_mt_area = (MyText) rootView.findViewById(R.id.house_mt_area);
				break;
			case MapFragment.HOUSE_TYPE_RENT:
				vs_house_type.inflate();
				house_price_type = (TextView) rootView.findViewById(R.id.house_price_type);
				house_price_type.setText("租金");

				house_mt_price = (MyText) rootView.findViewById(R.id.house_mt_price);
				house_mt_room = (MyText) rootView.findViewById(R.id.house_mt_room);
				house_mt_hall = (MyText) rootView.findViewById(R.id.house_mt_hall);
				house_mt_toilet = (MyText) rootView.findViewById(R.id.house_mt_toilet);
				house_mt_area = (MyText) rootView.findViewById(R.id.house_mt_area);

				vs_rent_support.inflate();

				supportList = new ArrayList<>();
				supportImgs = new ArrayList<>();
				for (int i=0; i<ids.length; i++){
					TextView tv_support = (TextView) rootView.findViewById(ids[i]);
					supportList.add(tv_support);

					ImageView ic_support = (ImageView) rootView.findViewById(icons[i]);
					supportImgs.add(ic_support);
				}

				supportMap = new HashMap<>();
				supportMap.put("水", 0);
				supportMap.put("电", 0);
				supportMap.put("宽带", 0);
				supportMap.put("电视", 0);
				supportMap.put("有线电视", 0);
				supportMap.put("暖气", 0);
				supportMap.put("热水器", 0);
				supportMap.put("空调", 0);
				supportMap.put("床", 0);
				supportMap.put("煤气/天然气", 0);
				supportMap.put("冰箱", 0);
				supportMap.put("ISDN", 0);
				supportMap.put("家具", 0);
				supportMap.put("电话", 0);
//				supportMap.put("彩电", 0);
				supportMap.put("洗衣机", 0);

				break;
			case MapFragment.HOUSE_TYPE_NEW:

				house_detail_desc.setVisibility(View.GONE);
				detail_bt_desc.setVisibility(View.GONE);
				house_desc_title.setVisibility(View.GONE);
				detail_more_info.setVisibility(View.VISIBLE);

				vs_new_type.inflate();

				new_mt_price = (MyText) rootView.findViewById(R.id.new_mt_price);
				new_property_type = (TextView) rootView.findViewById(R.id.new_property_type);
				new_house_status = (TextView) rootView.findViewById(R.id.new_house_status);

				detail_fl_exercise.setVisibility(View.VISIBLE);

				detail_nearby_type.setText("在售户型");
				detail_open_list.setVisibility(View.GONE);

//				vs_est_exercise.inflate();

				break;
		}
	}

	@OnClick(R.id.detail_more_info)
	public void moreInfoClick() {
		showInfoDialog();
	}

	private Dialog rightMenu;
	private TextView dialog_new_title, new_item_developer, new_item_status, new_item_average, new_item_opdate,
			new_item_gscope, new_item_address, new_sale_address, new_est_type, new_build_type, new_item_fitment,
			new_fitment_style, new_mgt_company, new_mgt_price, new_item_property_year, new_room_rate,
			new_green_rate, new_floor_rate, new_plan_unit;

	private void showInfoDialog() {
		if (rightMenu == null) {
			rightMenu = new Dialog(getActivity(), R.style.Theme_dialog);
			View view = inflater.inflate(R.layout.layout_new_house_info, null);

			dialog_new_title = (TextView) view.findViewById(R.id.dialog_new_title);
			new_item_developer = (TextView) view.findViewById(R.id.new_item_developer);
			new_item_status = (TextView) view.findViewById(R.id.new_item_status);
			new_item_average = (TextView) view.findViewById(R.id.new_item_average);
			new_item_opdate = (TextView) view.findViewById(R.id.new_item_opdate);
			new_item_gscope = (TextView) view.findViewById(R.id.new_item_gscope);
			new_item_address = (TextView) view.findViewById(R.id.new_item_address);
			new_sale_address = (TextView) view.findViewById(R.id.new_sale_address);
			new_est_type = (TextView) view.findViewById(R.id.new_est_type);
			new_build_type = (TextView) view.findViewById(R.id.new_build_type);
			new_item_fitment = (TextView) view.findViewById(R.id.new_item_fitment);
			new_fitment_style = (TextView) view.findViewById(R.id.new_fitment_style);
			new_mgt_company = (TextView) view.findViewById(R.id.new_mgt_company);
			new_mgt_price = (TextView) view.findViewById(R.id.new_mgt_price);
			new_item_property_year = (TextView) view.findViewById(R.id.new_item_property_year);
			new_room_rate = (TextView) view.findViewById(R.id.new_room_rate);
			new_green_rate = (TextView) view.findViewById(R.id.new_green_rate);
			new_floor_rate = (TextView) view.findViewById(R.id.new_floor_rate);
			new_plan_unit = (TextView) view.findViewById(R.id.new_plan_unit);

			int[] whs = MyUtils.getPhoneWidthAndHeight(getActivity());

			rightMenu.setContentView(view);
			Window win = rightMenu.getWindow();
			win.setGravity(Gravity.CENTER);
			rightMenu.setCanceledOnTouchOutside(true);
			win.setLayout((int) (whs[0] * 0.8), (int) (whs[1] * 0.7));

			dialog_new_title.setText(newHouseDetail.getAdName());
			new_item_developer.setText(newHouseDetail.getBaseNewProp().getDeveloper());
			new_item_status.setText(("认购".equals(newHouseDetail.getBaseNewProp().getStatus()) || "持销".equals(newHouseDetail.getBaseNewProp().getStatus()) || "尾盘".equals(newHouseDetail.getBaseNewProp().getStatus()) ? "在售" : "待售"));
			if (newHouseDetail.getAveragePrice()>0){
				new_item_average.setText(newHouseDetail.getAveragePrice()+"元/㎡");
			}else {
				new_item_average.setText("暂无");
			}
			new_item_opdate.setText(newHouseDetail.getOpDate());
			new_item_gscope.setText(newHouseDetail.getBaseNewProp().getDistrict().getGScopeCnName()+"—"+newHouseDetail.getBaseNewProp().getGScope().getGScopeCnName());
			new_item_address.setText(newHouseDetail.getBaseNewProp().getAddress());
			new_sale_address.setText(newHouseDetail.getBaseNewProp().getSalesOffice());
			new_est_type.setText(newHouseDetail.getBaseNewProp().getEstType());
			new_build_type.setText(newHouseDetail.getBuildingTypes());
			new_item_fitment.setText(newHouseDetail.getBaseNewProp().getFitment());
			new_fitment_style.setText(newHouseDetail.getArchitecturalStyle());
			new_mgt_company.setText(newHouseDetail.getBaseNewProp().getMgtCompany());
			new_mgt_price.setText(newHouseDetail.getMgtPrice()+"元/㎡/月");
			new_item_property_year.setText(newHouseDetail.getPropertyRight()+"年");

			new_room_rate.setText(newHouseDetail.getRoomRatio()+"%");
			if (newHouseDetail.getRoomRatio()==0){
				new_room_rate.setVisibility(View.GONE);
			}

			if (newHouseDetail.getBaseNewProp().getGreenRatio()==0){
				new_green_rate.setVisibility(View.GONE);
			}
			new_green_rate.setText(newHouseDetail.getBaseNewProp().getGreenRatio()+"%");

			if (newHouseDetail.getBaseNewProp().getFloorRatio()==0){
				new_floor_rate.setVisibility(View.GONE);
			}
			new_floor_rate.setText(newHouseDetail.getBaseNewProp().getFloorRatio()+"");

			if (newHouseDetail.getBaseNewProp().getPlanUnit()==0){
				new_plan_unit.setVisibility(View.GONE);
			}
			new_plan_unit.setText(newHouseDetail.getBaseNewProp().getPlanUnit()+"户");
		}
		rightMenu.show();
	}

	@BindView(R.id.detail_nearby_house)
	RecyclerView detail_nearby_house;

	private MyBottomDialog bottomSheetDialog;

	@BindView(R.id.detail_free_talk)
	Button detail_free_talk;

	@OnClick(R.id.detail_free_talk)
	public void talkClick() {

		if (staffList==null){
			if (houseType==MapFragment.HOUSE_TYPE_NEW){
				mPresenter.getNewHouseStaff(estExtId);
			}else {
				mPresenter.getStaffComment(houseDetailBo.getAdsNo(), houseDetailBo.getStaffNo());
			}
			staffType = STAFF_TO_TALK;
			return;
		}

		showTalkPop();
	}

	private StaffComment currentStaff;

	private void showTalkPop() {
		if (bottomSheetDialog == null) {

			View view = inflater.inflate(R.layout.layout_staff_bottom, null);
			view.setFocusable(true);
			view.setFocusableInTouchMode(true);
			bottomSheetDialog = new MyBottomDialog(getActivity());
			bottomSheetDialog.setContentView(view);

			RecyclerView house_desc_rv = (RecyclerView) view.findViewById(R.id.house_desc_rv);

			view.findViewById(R.id.desc_item_talk).setOnClickListener(view1 -> {
				currentStaff = staffList.get(selectedPosition);

				if (DataHolder.getInstance().isUserLogin()){
					MyUtils.toStaffTalk(getActivity(), currentStaff.getStaffNo(), currentStaff.getCnName(),MyUtils.TALK_FROM_SOURCE_LIST,
							houseInfo.toString(), houseType==MapFragment.HOUSE_TYPE_SECOND? ConversationActivity.ERSHOUFANG:ConversationActivity.ZUFANG, postId
							,imgFullPath,estName);
				}else {

					Intent intent = new Intent(getActivity(), LoginActivity.class);
					intent.putExtra(LoginActivity.LOGIN_INTENT_KEY, LoginActivity.LOGIN_INTENT_TALK);
					startActivityForResult(intent, 107);
				}
			});

			view.findViewById(R.id.desc_item_call).setOnClickListener(view12 -> {
				StaffComment staff = staffList.get(selectedPosition);
				MyUtils.toCall400(getActivity(), staff.getPhone400(), staff.getCnName());
			});

			TextView desc_item_name = (TextView) view.findViewById(R.id.desc_item_name);
			TextView desc_item_group = (TextView) view.findViewById(R.id.desc_item_group);
			CircleImageView desc_select_head = (CircleImageView) view.findViewById(R.id.desc_select_head);

			LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
			house_desc_rv.setLayoutManager(linearLayoutManager);

			choiceStaff(0, desc_item_name, desc_item_group, desc_select_head);

			HeadAdapter headAdapter = new HeadAdapter(getActivity(), R.layout.item_head_view, staffList);
			headAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
				@Override
				public void onItemClick(View view, RecyclerView.ViewHolder holder, Object o, int position) {
					headAdapter.notifyItemChanged(selectedPosition);
					selectedPosition = position;
					choiceStaff(position, desc_item_name, desc_item_group, desc_select_head);
//					linearLayoutManager.scrollToPositionWithOffset(position, 0);
//					headAdapter.notifyDataSetChanged();
					headAdapter.notifyItemChanged(position);
				}

				@Override
				public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, Object o, int position) {
					return false;
				}
			});
			house_desc_rv.setAdapter(headAdapter);
		}

		bottomSheetDialog.show();

	}

	private void choiceStaff(int position, TextView desc_item_name, TextView desc_item_group, CircleImageView desc_select_head){
		StaffComment staff = staffList.get(position);
		desc_item_name.setText(staff.getCnName());
		desc_item_group.setText(staff.getStoreName());
		//延迟20ms加载,等待控件绘制
		desc_select_head.postDelayed(() -> GlideLoad.load(new GlideLoad.Builder(requestBuilder, staff.getStaffImage()).into(desc_select_head)),20);
//		GlideLoad.load(new GlideLoad.Builder(requestBuilder, staff.getStaffImage())
//				.into(desc_select_head));
	}

	private int selectedPosition = 0;

	public class HeadAdapter extends com.zhy.adapter.recyclerview.CommonAdapter<StaffComment> {

		private int normalWidth, bigWidth;

		public HeadAdapter(Context context, int layoutId, List<StaffComment> datas) {
			super(context, layoutId, datas);
			normalWidth = MyUtils.dip2px(context, 50);
			bigWidth = MyUtils.dip2px(context, 60);
		}

		@Override
		protected void convert(com.zhy.adapter.recyclerview.base.ViewHolder holder, StaffComment comment, int position) {

			CircleImageView head = holder.getView(R.id.desc_item_head);
			ViewGroup.LayoutParams params = head.getLayoutParams();
			if (selectedPosition == position){
				params.height = bigWidth;
				params.width = bigWidth;
			}else {
				params.height = normalWidth;
				params.width = normalWidth;
			}
			head.setLayoutParams(params);

			GlideLoad.load(new GlideLoad.Builder(requestBuilder, comment.getStaffImage())
					.placeHolder(R.drawable.default_head)
					.error(R.drawable.default_head)
					.into(head));
		}
	}

	public static final String NEARBY_KEY = "NEARBY_KEY";

	@OnClick(R.id.detail_open_list)
	public void nearbyListClick(){
		Intent intent = new Intent(getActivity(), HouseList.class);
		intent.putExtra(MapFragment.HOUSE_TYPE_KEY, houseType);
		intent.putExtra(NEARBY_KEY, 1);
		intent.putExtra(HouseList.NEARBY_DATA_KEY, nearbyParams);
		startActivity(intent);
	}

	private void setSupportRes(TextView tv, ImageView ic, String str, boolean isNo){
		switch (str){
			case "水":
				ic.setImageDrawable(ic_support_water);
//				tv.setCompoundDrawablesWithIntrinsicBounds(ic_support_water, null, null, null);
				break;
			case "电":
				ic.setImageDrawable(ic_support_electric);
//				tv.setCompoundDrawablesWithIntrinsicBounds(ic_support_electric, null, null, null);
//				tv.setCompoundDrawablesWithIntrinsicBounds(ic_test_net, null, null, null);
//				tv.setCompoundDrawables(ic_test_net, null, null, null);
				break;
			case "宽带":
				ic.setImageDrawable(ic_support_net);
//				tv.setCompoundDrawablesWithIntrinsicBounds(ic_support_net, null, null, null);
//				tv.setCompoundDrawables(ic_test_net, null, null, null);
				break;
			case "有线电视":
				ic.setImageDrawable(ic_support_linetv);
//				tv.setCompoundDrawablesWithIntrinsicBounds(ic_support_linetv, null, null, null);
				break;
			case "电视":
				ic.setImageDrawable(ic_support_linetv);
//				tv.setCompoundDrawablesWithIntrinsicBounds(ic_support_linetv, null, null, null);
				break;
			case "暖气":
				ic.setImageDrawable(ic_support_heating);
//				tv.setCompoundDrawablesWithIntrinsicBounds(ic_support_heating, null, null, null);
				break;
			case "热水器":
				ic.setImageDrawable(ic_support_heater);
//				tv.setCompoundDrawablesWithIntrinsicBounds(ic_support_heater, null, null, null);
				break;
			case "空调":
				ic.setImageDrawable(ic_support_air);
//				tv.setCompoundDrawablesWithIntrinsicBounds(ic_support_air, null, null, null);
				break;
			case "床":
				ic.setImageDrawable(ic_support_bed);
//				tv.setCompoundDrawablesWithIntrinsicBounds(ic_support_bed, null, null, null);
				break;
			case "煤气/天然气":
				ic.setImageDrawable(ic_support_gas);
//				tv.setCompoundDrawablesWithIntrinsicBounds(ic_support_gas, null, null, null);
				break;
			case "冰箱":
				ic.setImageDrawable(ic_support_icebox);
//				tv.setCompoundDrawablesWithIntrinsicBounds(ic_support_icebox, null, null, null);
				break;
			case "ISDN":
				ic.setImageDrawable(ic_support_isdn);
//				tv.setCompoundDrawablesWithIntrinsicBounds(ic_support_isdn, null, null, null);
				break;
			case "家具":
				ic.setImageDrawable(ic_support_furniture);
//				tv.setCompoundDrawablesWithIntrinsicBounds(ic_support_furniture, null, null, null);
				break;
			case "电话":
				ic.setImageDrawable(ic_support_mobile);
//				tv.setCompoundDrawablesWithIntrinsicBounds(ic_support_mobile, null, null, null);
				break;
			case "彩电":
				ic.setImageDrawable(ic_support_colortv);
//				tv.setCompoundDrawablesWithIntrinsicBounds(ic_support_colortv, null, null, null);
				break;
			case "洗衣机":
				ic.setImageDrawable(ic_support_wash);
//				tv.setCompoundDrawablesWithIntrinsicBounds(ic_support_wash, null, null, null);
				break;
			default:
				ic.setImageDrawable(ic_support_no);
				break;
		}

		tv.setText(str);
		supportMap.remove(str);
		if (str.equals("电视") || str.equals("有线电视")) {
			supportMap.remove("电视");
			supportMap.remove("有线电视");
		}
	}

	private void collectClick(){
		collectionExchange = !collectionExchange;
		if (!collectionExchange) {
			getActivity().setResult(0);
		} else {
			getActivity().setResult(1);
		}
		if (DataHolder.getInstance().isUserLogin()){
			toCollection();
		}else {
			Intent intent = new Intent(getActivity(), LoginActivity.class);
			intent.putExtra(LoginActivity.LOGIN_INTENT_KEY, LoginActivity.LOGIN_INTENT_COLLECT);
			startActivityForResult(intent, 103);
		}
	}

	private void toCollection() {
		CancelCollectionEvent cancelCollectionEvent = null;
		if (collectId>0){
			mPresenter.deleteCollect(collectId);
		}else {
			Map<String, String> param = new HashMap<>();

			param.put("UserId", DataHolder.getInstance().getUserId());
			param.put("CityCode", "021");
			if (houseType== MapFragment.HOUSE_TYPE_SECOND){
				param.put("Source", "ershoufang");
				param.put("CollectValue", postId);
			}else if (houseType==MapFragment.HOUSE_TYPE_RENT){
				param.put("Source", "zufang");
				param.put("CollectValue", postId);
			}else {
				param.put("Source", "loupan");
				param.put("CollectValue", estExtId);
			}
			param.put("AppName", "APP");
			param.put("CollectUrl", "");

			mPresenter.insertCollect(param);
		}
	}

	@OnClick(R.id.detail_look_about)
	public void lookListClick(){

//		toast("敬请期待");

		if (!DataHolder.getInstance().isUserLogin()){
			Intent intent = new Intent(getActivity(), LoginActivity.class);
			intent.putExtra(LoginActivity.LOGIN_INTENT_KEY, LoginActivity.LOGIN_INTENT_LOOK_LIST);
			startActivityForResult(intent, 114);
		}else {
			Intent intent = new Intent(getActivity(), LookAbout.class);
			intent.putExtra(BaseFragmentActivity.FRAGMENT_TYPE, LookAbout.LOOK_TYPE_LIST);
			startActivity(intent);
		}
	}

	@OnClick(R.id.add_look_about)
	public void addLookAbout(){
//		toast("敬请期待");
		if (!DataHolder.getInstance().isUserLogin()){
			Intent intent = new Intent(getActivity(), LoginActivity.class);
			intent.putExtra(LoginActivity.LOGIN_INTENT_KEY, LoginActivity.LOGIN_INTENT_LOOK);
			startActivityForResult(intent, 105);
		}else {
			if (isOnLine){
				addHouse2Look();
			}else {
				toast("下架房源无法加入约看");
			}
		}
	}

	private void addHouse2Look() {
		Map<String, String> param = new HashMap<>();
		param.put("PostID", postId);
		param.put("AdsNo",houseDetailBo.getAdsNo());
		param.put("Source", "Android_APP");
		param.put("UserId", DataHolder.getInstance().getUserId());
		if (houseType== MapFragment.HOUSE_TYPE_SECOND){
			param.put("PostType", "S");
		}else {
			param.put("PostType", "R");
		}
		mPresenter.insertReservation(param);
	}

	private ShareDialog shareDialog;

	private void shareClick(){

		Map<String, String> shareParam = new HashMap<>();
		shareParam.put("title", shareTitle);
		shareParam.put("summary", shareSumtitle);
		shareParam.put("imageUrl", shareImage);
		shareParam.put("url", shareUrl);

		shareDialog = new ShareDialog(getActivity(), shareParam);
		shareDialog.show();
	}

	@OnClick(R.id.detail_rl_map)
	public void mapClick(){
		toNearByMap();
	}

	@OnClick(R.id.detail_bt_tools)
	public void toolClick(){
		Intent intent = new Intent(getActivity(), ToolActivity.class);
		intent.putExtra(ToolActivity.TOTAL_PRICE, salePrice);
		startActivity(intent);
	}

	private void toNearByMap() {
		Intent intent = new Intent(getActivity(), NearbyActivity.class);
		intent.putExtra(NearbyFragment.LATITUDE_KEY, latitude);
		intent.putExtra(NearbyFragment.LONGITUDE_KEY, longitude);
		startActivity(intent);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode==ImageBrowseActivity.FINISH_CODE){
			int position = data.getIntExtra(ImageBrowseActivity.IMAGE_POSITION_KEY, 0);
			detail_dl_imgs.setImagePosition(position);
		}else if (resultCode==LoginActivity.LOGIN_INTENT_LOOK){
			addHouse2Look();
		}else if (resultCode==LoginActivity.LOGIN_INTENT_TALK){
			MyUtils.toStaffTalk(getActivity(), currentStaff.getStaffNo(), currentStaff.getStaffName(),MyUtils.TALK_FROM_SOURCE_LIST, houseInfo.toString(),
					houseType==MapFragment.HOUSE_TYPE_SECOND? ConversationActivity.ERSHOUFANG:ConversationActivity.ZUFANG, postId,
					imgFullPath, estName);
		}else if (resultCode==LoginActivity.LOGIN_INTENT_LOOK_LIST){
			Intent intent = new Intent(getActivity(), LookAbout.class);
			intent.putExtra(BaseFragmentActivity.FRAGMENT_TYPE, LookAbout.LOOK_TYPE_LIST);
			startActivity(intent);
		}else if (resultCode==LoginActivity.LOGIN_INTENT_COLLECT){
			toCollection();
		}
	}

	private void staffDo(){
		if (staffList==null || staffList.size()==0){
			toast("没有查询到经纪人");
			return;
		}
		if (staffType==STAFF_TO_DESC){
			ArrayList<StaffComment> staffComments = new ArrayList<>(staffList);
			HouseDescribeWindow.getInstance(staffComments, houseInfo.toString(), houseType, postId, imgFullPath, shareTitle).show(getActivity().getSupportFragmentManager(), "HouseDescribeWindow");
		}else if (staffType==STAFF_TO_TALK){
			showTalkPop();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (DataHolder.getInstance().isUserLogin()){
			mPresenter.getLookedAboutNumber(DataHolder.getInstance().getUserId(), 0);
		}
	}


	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	protected String getTalkingDataPageName() {
		if (houseType == MapFragment.HOUSE_TYPE_NEW) {
			return "新房详情页";
		} else if (houseType == MapFragment.HOUSE_TYPE_SECOND) {
			return  "二手房详情页";
		} else{
			return "租房详情页";
		}
	}


	private List<PoiInfo> poiInfoList1;
	private List<PoiInfo> poiInfoList2;
	private List<PoiInfo> poiInfoList3;
	private List<PoiInfo> poiInfoList4;
	private class MyPoiListener implements OnGetPoiSearchResultListener {

		private int type;
		public MyPoiListener(int type) {
			this.type = type;
		}

		@Override
		public void onGetPoiResult(PoiResult poiResult) {
			if (poiResult.error != SearchResult.ERRORNO.NO_ERROR) {
				return;
			}
			switch (type) {
				case 0:
					HouseDetailFragment.this.poiInfoList1 = poiResult.getAllPoi();
					break;
				case 1:
					HouseDetailFragment.this.poiInfoList2 = poiResult.getAllPoi();
					break;
				case 2:
					HouseDetailFragment.this.poiInfoList3 = poiResult.getAllPoi();
					break;
				case 3:
					HouseDetailFragment.this.poiInfoList4 = poiResult.getAllPoi();
					break;
			}
		}

		@Override
		public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

		}

		@Override
		public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}
	}
}
