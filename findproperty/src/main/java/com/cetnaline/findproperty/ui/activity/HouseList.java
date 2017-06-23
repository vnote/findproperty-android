package com.cetnaline.findproperty.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cetnaline.findproperty.AppContents;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.bean.HouseBo;
import com.cetnaline.findproperty.api.bean.InsertIntentionsRequest;
import com.cetnaline.findproperty.api.bean.NewHouseListBo;
import com.cetnaline.findproperty.api.bean.SearchParam;
import com.cetnaline.findproperty.api.bean.SortBean;
import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.base.BaseFragment;
import com.cetnaline.findproperty.base.BaseFragmentActivity;
import com.cetnaline.findproperty.db.entity.DropBo;
import com.cetnaline.findproperty.entity.event.UnreadEvent;
import com.cetnaline.findproperty.inter.RefreshListener;
import com.cetnaline.findproperty.api.NetContents;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.cetnaline.findproperty.presenter.impl.HouseListPresenter;
import com.cetnaline.findproperty.presenter.ui.HouseListContract;
import com.cetnaline.findproperty.ui.adapter.CommonAdapter;
import com.cetnaline.findproperty.ui.adapter.ViewHolder;
import com.cetnaline.findproperty.ui.fragment.HouseDetailFragment;
import com.cetnaline.findproperty.ui.fragment.HouseListFragment;
import com.cetnaline.findproperty.ui.fragment.MapFragment;
import com.cetnaline.findproperty.utils.DataHolder;
import com.cetnaline.findproperty.utils.DbUtil;
import com.cetnaline.findproperty.utils.MyUtils;
import com.cetnaline.findproperty.utils.StatusBarCompat;
import com.cetnaline.findproperty.widgets.CircleImageView;
import com.cetnaline.findproperty.widgets.ClearableTextView;
import com.cetnaline.findproperty.widgets.FlowTag;
import com.cetnaline.findproperty.widgets.MyBottomDialog;
import com.cetnaline.findproperty.widgets.dropdown.DropCompleteListener;
import com.cetnaline.findproperty.widgets.dropdown.DropDownView;
import com.cetnaline.findproperty.widgets.dropdown.MultiSelectDrop;
import com.cetnaline.findproperty.widgets.dropdown.PriceDrop;
import com.cetnaline.findproperty.widgets.dropdown.SingleDrop;
import com.cetnaline.findproperty.widgets.dropdown.ThreeColumnDrop;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import io.rong.eventbus.EventBus;
import io.rong.imkit.RongIM;
import io.rong.imkit.model.Event;
import io.rong.imlib.model.Conversation;

/**
 * 房源列表
 * Created by fanxl2 on 2016/8/1.
 */
public class HouseList extends BaseFragmentActivity<HouseListPresenter> implements HouseListContract.View {

	@BindView(R.id.house_drop_menu)
	DropDownView house_drop_menu;

	@BindView(R.id.house_search_view)
	LinearLayout house_search_view;

	@BindView(R.id.house_search_tag)
	FlowTag house_search_tag;

	@BindView(R.id.house_search_save)
	Button house_search_save;

	private LayoutInflater inflater;

	public static final String SEARCH_PARAM_KEY = "SEARCH_PARAM_KEY";

	public static final String NEARBY_DATA_KEY = "NEARBY_DATA_KEY";

	public static final String INTENT_ID_KEY = "INTENT_ID_KEY";

	private HouseListFragment houseList;

	private int currentHouseType;

	private MenuItem menuItem;

	private RelativeLayout menu1,menu2,menu3;

	private HouseBo current_item;

	private ClearableTextView.ClearClickListener clearClickListener = new ClearableTextView.ClearClickListener() {
		@Override
		public void onClearClick(ClearableTextView view) {
			SearchParam searchParam = (SearchParam) view.getTag();

			house_search_tag.removeView(view);
			searchData.remove(searchParam.getKey());
			searchView.remove(searchParam.getKey());

			if (searchParam.getParamKey().equalsIgnoreCase("Feature") || searchParam.getParamKey().equalsIgnoreCase("Tags")){
				moreDrop.resetSelectStatus(searchParam.getText());
			}else {
				moreDrop.resetSelectStatus(searchParam.getName());
			}

			String[] paramKeys = searchParam.getParamKey().split(",");
			if (paramKeys!=null && paramKeys.length>1){
				searchParams.remove(paramKeys[0]);
				searchParams.remove(paramKeys[1]);
			}else {
				searchParams.remove(searchParam.getParamKey());
			}

			if ("Sell".equals(searchParam.getName()+"") || "Rent".equals(searchParam.getName()+"") || "NewHousePriceN".equals(searchParam.getName()+"")){
				resetTab(1);
				priceDrop.resetSelectStatus();
			}else if (NetContents.REGION_NAME.equalsIgnoreCase(searchParam.getName()+"") || NetContents.GSCOPE_NAME.equalsIgnoreCase(searchParam.getName()+"")
					|| NetContents.SCHOOL_NAME.equalsIgnoreCase(searchParam.getName()+"")){
				resetTab(0);
				threeDrop.resetSelectStatus();
				searchParams.remove("MinLng");
				searchParams.remove("MinLat");
				searchParams.remove("MaxLat");
				searchParams.remove("MaxLng");
			}else if ("Room".equals(searchParam.getName()+"")){
				resetTab(2);
				houseTypeDrop.resetSelectStatus();
			}else if (NetContents.RAILLINE_NAME.equalsIgnoreCase(searchParam.getName()+"")){
				SearchParam way = searchData.get("RailWayId");
				searchParams.remove("RailWayId");
				if (way!=null){
					ClearableTextView wayView = searchView.get(way.getKey());
					if (wayView!=null){
						house_search_tag.removeView(wayView);
					}

					searchData.remove(way.getKey());
					searchView.remove(way.getKey());
				}
				resetTab(0);
				threeDrop.resetSelectStatus();
				searchParams.remove("MinLng");
				searchParams.remove("MinLat");
				searchParams.remove("MaxLat");
				searchParams.remove("MaxLng");
			}else if (NetContents.RAILWAY_NAME.equalsIgnoreCase(searchParam.getName()+"")){
				SearchParam line = searchData.get("RailLineId");
				setTab(0, line.getText());

				searchParams.remove("MinLng");
				searchParams.remove("MinLat");
				searchParams.remove("MaxLat");
				searchParams.remove("MaxLng");
			}

			StringBuffer sb = new StringBuffer();
			for (SearchParam item : searchData.values()){
				if (item.getParamKey().equalsIgnoreCase("Direction")){
					sb.append(item.getText()).append(":");
				}else if (item.getParamKey().equalsIgnoreCase("MinOpdate,MaxOpdate")){
					sb.append(item.getText()).append(":");
				}else if (item.getParamKey().equalsIgnoreCase("MinGArea,MaxGArea")){
					sb.append(item.getText()).append(":");
				}else if (item.getParamKey().equalsIgnoreCase("EstType")){
					sb.append(item.getText()).append(":");
				}else if (item.getParamKey().equalsIgnoreCase("Fitment")){
					sb.append(item.getText()).append(":");
				}else if (item.getParamKey().equalsIgnoreCase("FloorDisplay")){
					sb.append(item.getText()).append(":");
				}else if (item.getParamKey().equalsIgnoreCase("Feature")){
					sb.append(item.getText()).append(":");
				}else if (item.getParamKey().equalsIgnoreCase("OpDateBegin,OpDateEnd")){
					sb.append(item.getText()).append(":");
				}else if (item.getParamKey().equalsIgnoreCase("Tags")){
					sb.append(item.getText()).append(":");
				}else {
					continue;
				}
			}

			if (TextUtils.isEmpty(sb)){
				resetTab(3);
			}else {
				String[] texts = sb.toString().split(":");
				if (texts!=null && texts.length>1){
					setTab(3, "多选");
				}else {
					setTab(3, sb.substring(0, sb.length()-1));
				}
			}

			showSearchTag(true);
		}
	};

	private RefreshListener refreshListener = new RefreshListener() {
		@Override
		public void down() {
			toTop = true;
			if (currentHouseType==MapFragment.HOUSE_TYPE_NEW){
				searchParams.put("StartIndex", "0");
				if (searchParams.get("RegionId")!=null){
					searchParams.put("DistrictId", searchParams.get("RegionId"));
				}
				iPresenter.getNewHouseList(searchParams);
			}else {
				searchParams.put("PageIndex", "1");
				if (searchParams.get("SchoolId")!=null){
					iPresenter.getHouseBySchool4AllResult(searchParams);
				}else if (searchParams.get("RailWayId")!=null ||  searchParams.get("RailLineId")!=null){
					iPresenter.getHouseByMetroAllResult(searchParams);
				}else {
					if (nearby==1){
						iPresenter.getNearbyHouse(searchParams);
					}else {
						iPresenter.getHouseList4AllResult(searchParams);
					}
				}
			}
		}

		@Override
		public void up(int count) {
			toTop = false;
			Logger.i("total:"+total+"---data size:"+houseList.getAdapterSize());

			if (total==houseList.getAdapterSize()){
				houseList.noMoreData();
				return;
			}

			if (currentHouseType==MapFragment.HOUSE_TYPE_NEW){
				searchParams.put("StartIndex", count+"");
				if (searchParams.get("RegionId")!=null){
					searchParams.put("DistrictId", searchParams.get("RegionId"));
				}
				iPresenter.getNewHouseList(searchParams);
			}else {
				searchParams.put("PageIndex", count+"");
				if (searchParams.get("SchoolId")!=null){
					iPresenter.getHouseBySchool4AllResult(searchParams);
				}else if (searchParams.get("RailWayId")!=null ||  searchParams.get("RailLineId")!=null ){
					iPresenter.getHouseByMetroAllResult(searchParams);
				}else {
					if (nearby==1){
						iPresenter.getNearbyHouse(searchParams);
					}else {
						iPresenter.getHouseList4AllResult(searchParams);
					}
				}
			}
		}

		@Override
		public void loadDataAgain() {
			houseList.getListData();
		}
	};

	//单选
	private PriceDrop priceDrop;

	private ThreeColumnDrop threeDrop;

//	private SingleDrop areaDrop;

	private SingleDrop houseTypeDrop;

	private MultiSelectDrop moreDrop;

	private DropCompleteListener dropCompleteListener = new DropCompleteListener() {

		@Override
		public void complete(int position, boolean fromMore, int type, DropBo... dropBos) {
			dropMenuClose(position, dropBos);
		}
	};

	/**
	 * 获取符合条件的房源列表
	 * @param type 房源类型
	 */
	@Override
	protected BaseFragment getFirstFragment(int type) {
		houseList = HouseListFragment.getInstance(HouseListFragment.LIST_TYPE_BIG, currentHouseType);
		houseList.setRefreshListener(refreshListener);
		return houseList;
	}

	@Override
	protected int getFragmentContentId() {
		return R.id.fragment_container;
	}

	@Override
	protected int getContentViewId() {
		return R.layout.act_house_list;
	}

	@Override
	protected IPresenter createPresenter() {
		return new HouseListPresenter();
	}

	@Override
	protected void initToolbar() {
		StatusBarCompat.setStatusBarColor(this, Color.WHITE);
		toolbar.setNavigationOnClickListener(view -> {
			toBack();
		});
	}

	private int nearby;

	//显示在界面上的订阅
	private HashMap<String, SearchParam> searchData = new HashMap<>();

	private HashMap<String, ClearableTextView> searchView = new HashMap<>();

	@Override
	protected void initView(Bundle savedInstanceState) {

		inflater = LayoutInflater.from(this);
		Intent intent = getIntent();

		currentHouseType = intent.getIntExtra(MapFragment.HOUSE_TYPE_KEY, MapFragment.HOUSE_TYPE_SECOND);
		nearby = intent.getIntExtra(HouseDetailFragment.NEARBY_KEY, -1);
		intentId = intent.getLongExtra(INTENT_ID_KEY, 0);

		if (intentId>0){
			house_search_save.setText("已保存");
		}

		double minLat = intent.getDoubleExtra("MinLat", 0);
		double minLng = intent.getDoubleExtra("MinLng", 0);
		double maxLat = intent.getDoubleExtra("MaxLat", 0);
		double maxLng = intent.getDoubleExtra("MaxLng", 0);

		house_drop_menu.setDropCompleteListener(dropCompleteListener);

		threeDrop = new ThreeColumnDrop(house_drop_menu, this);
		priceDrop = new PriceDrop(house_drop_menu, this, null);
		houseTypeDrop = new SingleDrop(house_drop_menu, this, null);
		moreDrop = new MultiSelectDrop(house_drop_menu, this, null);

		house_drop_menu.addDrops(threeDrop, priceDrop, houseTypeDrop, moreDrop);
		updateDropMenu();

		HashMap<String, SearchParam> datas = (HashMap<String, SearchParam>) getIntent().getSerializableExtra(SEARCH_PARAM_KEY);
		HashMap<String, String> nearbyParams = (HashMap<String, String>) getIntent().getSerializableExtra(NEARBY_DATA_KEY);

		if (nearbyParams!=null){
			searchParams.putAll(nearbyParams);
		}

		if (datas!=null && datas.size()>0){
//			if (datas.size()==1){
//				for (SearchParam param : datas.values()){
//					toolbar.setTitle(param.getTitle());
//				}
//			}
			if (datas.get("Property") != null) {
				datas.get("Property").setParamKey("PropertyTypeList");
				datas.get("Property").setName("PropertyTypeList");
				datas.get("Property").setKey("PropertyTypeList");
				datas.get("Property").setTitle("PropertyTypeList");
			}

			searchData.putAll(datas);
		}

		if (currentHouseType==MapFragment.HOUSE_TYPE_NEW){
			searchParams.put("length", "10");
			houst_list_switch.setVisibility(View.GONE);
		}else {
			searchParams.put("PageCount", "10");
			searchParams.put("ImageWidth", NetContents.HOUSE_SMALL_IMAGE_LIST_WIDTH+"");
			searchParams.put("ImageHeight", NetContents.HOUSE_SMALL_IMAGE_LIST_HEIGHT+"");

			if (minLng!=0){
				searchParams.put("MinLng", minLng+"");
				searchParams.put("MinLat", minLat+"");
				searchParams.put("MaxLat", maxLat+"");
				searchParams.put("MaxLng", maxLng+"");
			}
		}

		showSearchTag(false);
		EventBus.getDefault().register(this);
	}

	private void updateDropMenu(){

		List<DropBo> roomDrops = DbUtil.getSearchDataByName("Room");
		houseTypeDrop.init(roomDrops);

		switch (currentHouseType){
			case MapFragment.HOUSE_TYPE_SECOND:
				toolbar.setTitle("二手房");
				house_drop_menu.initTabs(R.array.listSecond);
				List<DropBo> priceDropDatas = DbUtil.getSearchDataByName("Sell");
				priceDrop.init(priceDropDatas, currentHouseType);
				searchParams.put("PostType", "s");
				break;
			case MapFragment.HOUSE_TYPE_RENT:
				toolbar.setTitle("租房");
				house_drop_menu.initTabs(R.array.listRent);
				List<DropBo> rentDropDatas = DbUtil.getSearchDataByName("Rent");
				priceDrop.init(rentDropDatas, currentHouseType);
				searchParams.put("PostType", "r");
				break;
			case MapFragment.HOUSE_TYPE_NEW:
				toolbar.setTitle("新房");
				house_drop_menu.initTabs(R.array.listNew);
				List<DropBo> aveDropDatas = DbUtil.getSearchDataByName("NewHousePriceN");
				priceDrop.init(aveDropDatas, currentHouseType);

				break;
		}

		List<DropBo> moreList = new ArrayList<>();

		if (currentHouseType==MapFragment.HOUSE_TYPE_NEW){

			List<DropBo> leftDrop = new ArrayList<>();
//			DropBo noDrop = new DropBo("不限", "", -1);
//			noDrop.setName("不限");
//			leftDrop.add(noDrop);
			DropBo one = new DropBo("区域", "", 0);
			leftDrop.add(one);
			threeDrop.init(leftDrop);


			DropBo estDrop = new DropBo("物业类型", "0", 0);
			List<DropBo> ests = DbUtil.getSearchDataByName("NewEstType");
			if (ests!=null && ests.size()>0){
				ests.remove(0);
			}
			estDrop.setChildrenList(ests);
			moreList.add(estDrop);

			DropBo timeDrop = new DropBo("开盘时间", "0", 0);
			List<DropBo> times = DbUtil.getSearchDataByName("NewPropOpDate");
			if (times!=null && times.size()>0){
				times.remove(0);
			}
			timeDrop.setChildrenList(times);
			moreList.add(timeDrop);

			DropBo tagDrop = new DropBo("特色", "0", 1);
			List<DropBo> tags = DbUtil.getSearchDataByName("NewPropFeatures");
			if (tags!=null && tags.size()>0){
				tags.remove(0);
			}
			tagDrop.setChildrenList(tags);
			moreList.add(tagDrop);

		}else {

			List<DropBo> leftDrop = new ArrayList<>();
//			DropBo noDrop = new DropBo("不限", "", -1);
//			noDrop.setName("不限");
//			leftDrop.add(noDrop);
			DropBo one = new DropBo("区域", "", 0);
			DropBo two = new DropBo("地铁", "", 1);
			DropBo three = new DropBo("学校", "", 2);
			leftDrop.add(one);
			leftDrop.add(two);
			leftDrop.add(three);

			threeDrop.init(leftDrop);

			DropBo areaDrop = new DropBo("面积", "0", 0);
			List<DropBo> areas = DbUtil.getSearchDataByName("Area");
			if (areas!=null && areas.size()>0){
				areas.remove(0);
			}
			areaDrop.setChildrenList(areas);
			moreList.add(areaDrop);

			DropBo directionDrop = new DropBo("朝向", "0", 0);
			List<DropBo> directions = DbUtil.getSearchDataByName("Direction");
			if (directions!=null && directions.size()>0){
				directions.remove(0);
			}
			directionDrop.setChildrenList(directions);
			moreList.add(directionDrop);

			DropBo ageDrop = new DropBo("房龄", "0", 0);
			List<DropBo> ages = DbUtil.getSearchDataByName("HouseAge");
			if (ages!=null && ages.size()>0){
				ages.remove(0);
			}
			ageDrop.setChildrenList(ages);
			moreList.add(ageDrop);

			DropBo fitDrop = new DropBo("装修", "0", 0);
			List<DropBo> fitments = DbUtil.getSearchDataByName("Fitment");
			if (fitments!=null && fitments.size()>0){
				fitments.remove(0);
			}
			fitDrop.setChildrenList(fitments);
			moreList.add(fitDrop);

			DropBo floorDrop = new DropBo("楼层", "0", 0);
			List<DropBo> floors = DbUtil.getSearchDataByName("Floor");
			if (floors!=null && floors.size()>0){
				floors.remove(0);
			}
			floorDrop.setChildrenList(floors);
			moreList.add(floorDrop);

			//更多菜单，物业类型
			DropBo propertyDrop = new DropBo("物业类型","0",0);
			List<DropBo> propertys = DbUtil.getSearchDataByName("Property");
			if (propertys!=null && propertys.size()>0){
				propertys.remove(0);
			}
			propertyDrop.setChildrenList(propertys);
			moreList.add(propertyDrop);

			DropBo tagDrop = new DropBo("特色", "0", 1);
			List<DropBo> tags = DbUtil.getSearchDataByName("SellTag");
			tagDrop.setChildrenList(tags);
			moreList.add(tagDrop);
		}

		moreDrop.init(moreList);

	}

	@BindView(R.id.houst_list_switch)
	CheckBox houst_list_switch;

	@OnCheckedChanged(R.id.houst_list_switch)
	public void listSwitch(CompoundButton compoundButton, boolean isChecked){
		if (isChecked){
			houseList.switchList(true, datas);
		}else {
			houseList.switchList(false, datas);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		refreshUnreadCount();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_list, menu);
		menuItem = menu.findItem(R.id.house_list_msg);
		if (DataHolder.getInstance().getMsgCount()>0){
			refreshUnreadCount();
		}
		return true;
	}

	/**
	 * 未读数量刷新
	 * @param e
	 */
	public void onEventMainThread(UnreadEvent e) {
		refreshUnreadCount();
	}

	/**
	 * 新消息监听
	 * @param event
	 */
	public void onEventMainThread(final Event.OnReceiveMessageEvent event) {
		refreshUnreadCount();
	}

	private void refreshUnreadCount(){
		//获取未读消息数
		int count = RongIM.getInstance().getUnreadCount(Conversation.ConversationType.PRIVATE);
		DataHolder.getInstance().setMessageCount(count);
		if (menuItem != null) {
			if (count > 0) {
				menuItem.setIcon(R.drawable.ic_has_msg_black);
			} else {
				menuItem.setIcon(R.drawable.ic_msg_black_24dp);
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

//		EventBus.getDefault().post(new AppLoginEvent(true, AppLoginEvent.CALL_CHAT_FRAGMENT));

//		this.setResult(MapFragment.MAP_TO_MSG);
//		RxBus.getDefault().send(new MainTabEvent());

		if (DataHolder.getInstance().isUserLogin()){
//			Intent intent = new Intent(this, MainTabActivity.class);
//			intent.putExtra(MainTabActivity.SELECTED_TAB_KEY, MainTabActivity.TAB_CHAT);
//			startActivity(intent);
//			this.finish();
			Intent intent = new Intent(this, ConversationListActivity.class);
			startActivity(intent);
		}else {
			toast("请先登录");
			startActivity(new Intent(this, LoginActivity.class));
		}
		return super.onOptionsItemSelected(item);
	}

	private int total;
	private boolean toTop;
	private List<HouseBo> datas;

	@Override
	public void setHouseList(List<HouseBo> houseList, int total) {
		this.datas = houseList;
		this.houseList.setRefreshData(houseList, toTop);
		this.total=total;
	}

	@Override
	public void setNewHouseList(List<NewHouseListBo> houseList, int total) {
		this.houseList.setRefreshNewData(houseList, toTop);
		this.total=total;
	}

	@Override
	public void stopRefresh() {
		houseList.noData();
	}

	@Override
	public void netWorkException() {
		this.datas = null;
		this.houseList.setRefreshData(null, false);
		this.total=0;
		houseList.netWorkException();
	}

	@Override
	public void setInsertResult(long intentId) {
		this.intentId=intentId;
		if (intentId>=0){
			toast("意向保存成功");
			DataHolder.getInstance().setChangeIntent(true);
			house_search_save.setText("已保存");
		}else {
			showError("保存失败，请稍后尝试");
		}
	}

	@Override
	public void setDeleteIntentResult(boolean result) {
		if (result){
			toast("删除成功");
			intentId = -1;
			house_search_save.setText("保存");
		}else {
			showError("删除失败, 请重试!");
		}
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

	private Map<String, String> searchParams = new HashMap<>();

	private void dropMenuClose(int position, DropBo... dropBos){
		switch (position){
			case 0:
				if (nearby==1){
					nearby = -1;
					searchParams.remove("Lat");
					searchParams.remove("Lng");
					searchParams.remove("MaxSalePrice");
					searchParams.remove("MaxRentPrice");
					searchParams.remove("MinSalePrice");
					searchParams.remove("MinRentPrice");
					searchParams.remove("Round");
				}
				removeData("RegionId", null, position, false);
				removeData("GScopeId", null, position, false);
				removeData("RailWayId", null, position, false);
				removeData("RailLineId", null, position, false);
				removeData("SchoolId", null, position, false);

				removeData("EstateCode", null, position, false);

				searchParams.remove("MinLng");
				searchParams.remove("MinLat");
				searchParams.remove("MaxLat");
				searchParams.remove("MaxLng");
				searchParams.remove("EstateCode");

				if (currentHouseType==MapFragment.HOUSE_TYPE_SECOND){
					toolbar.setTitle("二手房");
				}else if (currentHouseType==MapFragment.HOUSE_TYPE_RENT){
					toolbar.setTitle("租房");
				}else {
					toolbar.setTitle("新房");
				}

				if (dropBos.length>1){
					DropBo lineDb = dropBos[0];
					SearchParam lineSp = createSearch(lineDb);
					lineSp.setText(lineDb.getValue());
					lineSp.setValue(lineDb.getID()+"");
					lineSp.setKey(lineDb.getKey());
					searchData.put(lineSp.getKey(), lineSp);

					DropBo wayDb = dropBos[1];
					SearchParam waySp = createSearch(wayDb);
					waySp.setKey(wayDb.getKey());
					searchData.put(waySp.getKey(), waySp);
					setTab(position, wayDb.getText());
				}else {
					DropBo dropOne = dropBos[0];

					if ("不限".equals(dropOne.getName())){
						resetTab(0);
					}else if (NetContents.REGION_NAME.equalsIgnoreCase(dropOne.getName())){
						SearchParam search = createSearch(dropOne);
						if (dropOne.getType()==-1){
							search.setText(dropOne.getValue());
							search.setValue(dropOne.getID()+"");
						}
						search.setKey(dropOne.getKey());
						searchData.put(search.getKey(), search);
						setTab(position, search.getText());
					}else if (NetContents.GSCOPE_NAME.equalsIgnoreCase(dropOne.getName())){
						SearchParam search = createSearch(dropOne);
						search.setKey(dropOne.getKey());
						searchData.put(search.getKey(), search);
						setTab(position, dropOne.getText());
					}else if (NetContents.RAILLINE_NAME.equalsIgnoreCase(dropOne.getName())){
						SearchParam search = createSearch(dropOne);
						if (dropOne.getType()==-1){
							search.setText(dropOne.getValue());
							search.setValue(dropOne.getID()+"");
						}
						search.setKey(dropOne.getKey());
						searchData.put(search.getKey(), search);
						setTab(position, search.getText());
					}else if (NetContents.SCHOOL_NAME.equalsIgnoreCase(dropOne.getName())){
						SearchParam search = createSearch(dropOne);
						search.setKey(dropOne.getKey());
						searchData.put(search.getKey(), search);
						setTab(position, dropOne.getText());
					}
				}
				break;
			case 1:
				DropBo dropType = dropBos[0];
				String[] prices = dropType.getValue().split(",");
				if (currentHouseType==MapFragment.HOUSE_TYPE_SECOND){
					if (prices.length>1){
						SearchParam search = createSearch(dropType);
						search.setKey("MinSalePrice,MaxSalePrice");
						searchData.put(search.getKey(), search);
						setTab(position, dropType.getText());
					}else {
						removeData("MinSalePrice", "MaxSalePrice", position, true);
					}
				}else if (currentHouseType==MapFragment.HOUSE_TYPE_RENT){
					if (prices.length>1){
						SearchParam search = createSearch(dropType);
						search.setKey("MinRentPrice,MaxRentPrice");
						searchData.put(search.getKey(), search);
						setTab(position, dropType.getText());
					}else {
						removeData("MinRentPrice", "MaxRentPrice", position, true);
					}
				}else {
					if (prices.length>1){
						SearchParam search = createSearch(dropType);
						search.setKey("MinAveragePrice,MaxAveragePrice");
						searchData.put(search.getKey(), search);
						setTab(position, dropType.getText());
					}else {
						removeData("MinAveragePrice", "MaxAveragePrice", position, true);
					}
				}

				if ("p0".equals(dropType.getPara()+"")){
					priceDrop.resetSelectStatus();
				}
				break;
			case 2:
				DropBo dropRoom = dropBos[0];
				String[] rooms = dropRoom.getValue().split(",");
				if (currentHouseType==MapFragment.HOUSE_TYPE_NEW){
					if (rooms.length>1){
						SearchParam search = createSearch(dropRoom);
						search.setKey("RoomCnt");
						search.setValue(rooms[0]);
						searchData.put(search.getKey(), search);
						setTab(position, dropRoom.getText());
					}else {
						removeData("RoomCnt", null, position, false);
					}
				}else {
					if (rooms.length>1){
						SearchParam search = createSearch(dropRoom);
						search.setKey("MinRoomCnt,MaxRoomCnt");
						searchData.put(search.getKey(), search);
						setTab(position, dropRoom.getText());
					}else {
						removeData("MinRoomCnt", "MaxRoomCnt", position, true);
					}
				}
				break;
			case 3:
				//新房
				if (currentHouseType==MapFragment.HOUSE_TYPE_NEW){
					removeData("OpDateBegin", "OpDateEnd", position, false);
					removeData("EstType", null, position, false);
					removeMultiData("Tags");

					if (dropBos.length==0){
						resetTab(position);
					}else {
						StringBuffer sb = new StringBuffer();
						for (int i=0; i<dropBos.length; i++){
							if (dropBos[i].getName().equals("NewPropOpDate")){
								String[] dates = dropBos[i].getValue().split(",");
								if (dates==null || dates.length==0){
								}else {
									sb.append(dropBos[i].getText()).append(":");
									SearchParam search = createOpenDate(dropBos[i]);
									search.setKey("OpDateBegin,OpDateEnd");
									searchData.put(search.getKey(), search);
								}
							}else if (dropBos[i].getName().equals("NewPropFeatures")){
								sb.append(dropBos[i].getText()).append(":");

								SearchParam search = createSearch(dropBos[i]);
								search.setKey("Tags"+dropBos[i].getID());
								search.setParamKey("Tags");
								searchData.put(search.getKey(), search);
							}else if (dropBos[i].getName().equals("NewEstType")){
								sb.append(dropBos[i].getText()).append(":");

								SearchParam search = createSearch(dropBos[i]);
								search.setKey("EstType");
								searchData.put(search.getKey(), search);
							}
						}
						String[] texts = sb.toString().split(":");
						if (texts!=null && texts.length>1){
							setTab(3, "多选");
						}else {
							setTab(3, sb.substring(0, sb.length()-1));
						}
//						setTab(position, sb.toString());
					}
				}else {
					//二手房租房
					removeData("Direction", null, position, false);
					removeData("MinOpdate", "MaxOpdate", position, false);
					removeData("MinGArea", "MaxGArea", position, false);
					removeData("Fitment", null, position, false);
					removeData("FloorDisplay", null, position, false);
					removeData("PropertyTypeList", null, position, false);
					removeData("PropertyType", null, position, false);
					removeMultiData("Feature");

					if (dropBos.length==0){
						resetTab(position);
					}else {
						StringBuffer sb = new StringBuffer();
						for (int i=0; i<dropBos.length; i++){
							if (dropBos[i].getName().equals("Direction")){
								sb.append(dropBos[i].getText()).append(":");

								SearchParam search = createSearch(dropBos[i]);
								search.setKey("Direction");
								searchData.put(search.getKey(), search);

							}else if (dropBos[i].getName().equals("HouseAge")){
								String[] ages = dropBos[i].getValue().split(",");
								if (ages.length>1){
									sb.append(dropBos[i].getText()).append(":");

									SearchParam search = createHouseAge(dropBos[i]);
									search.setKey("MinOpdate,MaxOpdate");
									searchData.put(search.getKey(), search);
								}
							}else if (dropBos[i].getName().equals("Fitment")){
								sb.append(dropBos[i].getText()).append(":");

								SearchParam search = createSearch(dropBos[i]);
								search.setKey("Fitment");
								searchData.put(search.getKey(), search);
							}else if (dropBos[i].getName().equals("Floor")){
								sb.append(dropBos[i].getText()).append(":");

								SearchParam search = createSearch(dropBos[i]);
								search.setKey("FloorDisplay");
								searchData.put(search.getKey(), search);
							}else if (dropBos[i].getName().equals("SellTag")){
								sb.append(dropBos[i].getText()).append(":");

								SearchParam search = createSearch(dropBos[i]);
								search.setKey("Feature"+dropBos[i].getID());
								search.setParamKey("Feature");
								searchData.put(search.getKey(), search);
							}else if (dropBos[i].getName().equals("Area")){

								String[] areas = dropBos[i].getValue().split(",");
								if (areas.length>1){
									sb.append(dropBos[i].getText()).append(":");

									SearchParam search = createSearch(dropBos[i]);
									search.setKey("MinGArea,MaxGArea");
									searchData.put(search.getKey(), search);
								}
							} else if (dropBos[i].getName().equals("Property")){

								sb.append(dropBos[i].getText()).append(":");

								SearchParam search = createSearch(dropBos[i]);
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
//						setTab(position, sb.toString());
					}
				}

				break;
		}

		showSearchTag(true);
	}

	private void removeMultiData(String keyWord) {
		List<String> removeKyes = new ArrayList<>();
		for (String key : searchData.keySet()){
			if (key.contains(keyWord)){
				removeKyes.add(key);
			}
		}
		for (String key : removeKyes){
			ClearableTextView view = searchView.get(key);
			if (view!=null){
				house_search_tag.removeView(view);
			}
			searchData.remove(key);
		}
		searchParams.remove(keyWord);
	}

	@Override
	protected boolean hasShade() {
		return true;
	}

	/**
	 * 初始化shade层
	 * @param vs
	 */
	@Override
	protected boolean initShade(View[] vs) {
		Rect rect = new Rect();
		vs[0].getGlobalVisibleRect(rect);

		if (vs[0] instanceof CircleImageView && vs[0].getHeight() == rect.height()) {
			int[] location = new int[2];
			vs[0].getLocationInWindow(location);

//			Logger.i("buobao:"+rect.left+","+rect.right+","+rect.top+","+rect.bottom+"["+rect.width()+","+rect.height()+"]");

			CircleImageView hiLight = new CircleImageView(this);
			hiLight.setImageDrawable(((CircleImageView) vs[0]).getDrawable());
			hiLight.setBorderColor(getResources().getColor(R.color.white));
			hiLight.setBorderWidth(MyUtils.dip2px(this, 4));
//			Animation animation = new ScaleAnimation(0.8f, 1.0f, 0.8f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

			//菜单动画
			RotateAnimation a = new RotateAnimation(0,45,Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF,0.5f);
			a.setDuration(100);
			a.setRepeatCount(1);
			a.setRepeatMode(Animation.REVERSE);
			MenuAnimationListener listener = new MenuAnimationListener();
			a.setAnimationListener(listener);

			menu1 = createMenu(R.drawable.ic_phone);
			Animation animation1 = new TranslateAnimation(MyUtils.dip2px(this, 100),0,0,0);
			animation1.setDuration(200);
			animation1.setStartOffset(200);
			menu1.setAnimation(animation1);
			menu1.setOnClickListener((view)->{
				view.clearAnimation();
				listener.setAction(()->{
					if (current_item != null) {
						new Handler().postDelayed(()->MyUtils.toCall400(HouseList.this, current_item.getStaff400Tel(), current_item.getStaffName()),200);
						hideShade();
					}
				});
				view.setAnimation(a);
			});

			menu2 = createMenu(R.drawable.ic_chat);
			Animation animation2 = new TranslateAnimation(MyUtils.dip2px(this, 71),0,MyUtils.dip2px(this, 71),0);
			animation2.setDuration(200);
			animation2.setStartOffset(250);
			menu2.setAnimation(animation2);
			menu2.setOnClickListener((view)->{
				view.clearAnimation();
				listener.setAction(()->{
					if (current_item != null){
						if (DataHolder.getInstance().isUserLogin()){
							new Handler().postDelayed(()->goToTalk(),200);
						}else {
							Intent intent = new Intent(HouseList.this, LoginActivity.class);
							intent.putExtra(LoginActivity.LOGIN_INTENT_KEY, LoginActivity.LOGIN_INTENT_TALK);
							new Handler().postDelayed(()->startActivityForResult(intent, 201),200);
						}
						hideShade();
					}
				});
				view.setAnimation(a);
			});

			menu3 = createMenu(R.drawable.ic_staff_store);
			Animation animation3 = new TranslateAnimation(0,0,MyUtils.dip2px(this, 100),0);
			animation3.setDuration(200);
			animation3.setStartOffset(300);
			menu3.setAnimation(animation3);
			menu3.setOnClickListener((view)->{
				view.clearAnimation();
				listener.setAction(()->{
					if (current_item != null) {
						new Handler().postDelayed(()->MyUtils.toStoreHome(HouseList.this, current_item.getStaffNo(), current_item.getStaffName()),200);
						hideShade();
					}
				});
				view.setAnimation(a);
			});

			int sataus_bar_height = StatusBarCompat.getStatusBarHeight(this);
			RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(rect.width()-MyUtils.dip2px(this,20),rect.height()-MyUtils.dip2px(this,20)); //rect.width(),rect.height()
			params1.setMargins(rect.left - MyUtils.dip2px(this, 90), rect.top -sataus_bar_height + MyUtils.dip2px(this,10), 0,0); //rect.right + 200, rect.bottom
			shade_layout.addView(menu1, params1);

			RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(rect.width()-MyUtils.dip2px(this,20),rect.height()-MyUtils.dip2px(this,20));
			params2.setMargins(rect.left - MyUtils.dip2px(this, 61), rect.top - sataus_bar_height - MyUtils.dip2px(this, 61), 0,0); //rect.right + 142, rect.bottom + 142
			shade_layout.addView(menu2, params2);

			RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(rect.width()-MyUtils.dip2px(this,20),rect.height()-MyUtils.dip2px(this,20));
			params3.setMargins(rect.left + MyUtils.dip2px(this,10), rect.top - sataus_bar_height - MyUtils.dip2px(this, 90), 0,0);  //rect.right, rect.bottom + 200
			shade_layout.addView(menu3, params3);

			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(rect.width(),rect.height());
			params.setMargins(rect.left, rect.top - sataus_bar_height, 0,0); //rect.right, rect.bottom
			shade_layout.addView(hiLight, params);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void hideShade() {
		if (menu3 != null && menu2 != null && menu1 != null) {
			menu1.setEnabled(false);
			menu2.setEnabled(false);
			menu3.setEnabled(false);

			menu3.animate().translationY(MyUtils.dip2px(this, 100)).setDuration(200).setStartDelay(50).start();
			menu2.animate().translationX(MyUtils.dip2px(this, 71)).translationY(MyUtils.dip2px(this, 71)).setDuration(200).setStartDelay(100).start();
			menu1.animate().translationX(MyUtils.dip2px(this, 100)).setDuration(200).setStartDelay(150).start();
			new Handler().postDelayed(()->HouseList.super.hideShade(),200);
		}
	}

	private RelativeLayout createMenu(int imgRes) {
		RelativeLayout layout = new RelativeLayout(this);

		RelativeLayout.LayoutParams itemParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		itemParams.addRule(RelativeLayout.CENTER_IN_PARENT);

		ImageView menu = new ImageView(this);
		menu.setImageDrawable(getResources().getDrawable(imgRes));
		layout.setBackgroundResource(R.drawable.shade_menu_bg);
		layout.addView(menu, itemParams);

		return layout;
	}

	public void setSelectItem(HouseBo current_item) {
		this.current_item = current_item;

		if (TextUtils.isEmpty(current_item.getStaff400Tel()) || TextUtils.isEmpty(current_item.getStaffName())){
			ApiRequest.getStaffDetail(current_item.getStaffNo())
					.subscribe(staffDetail -> {
						current_item.setStaffName(staffDetail.getCnName());
						current_item.setStaff400Tel(staffDetail.getStaff400Tel());
						DbUtil.addStaff(staffDetail);
					}, throwable -> {
						toast("经纪人信息获取失败");
					});
		}
	}

	private void goToTalk() {
		if (currentHouseType != MapFragment.HOUSE_TYPE_NEW && current_item != null) {
			StringBuffer houseInfo = new StringBuffer();
			houseInfo.append(current_item.getRoomCount()+"室"+current_item.getHallCount()+"厅").append(" ")
					.append(MyUtils.format2String(current_item.getGArea())+"㎡").append(" ");
//			houseInfo.append(NetContents.SHARE_HOUSE_HOST + "xinfang/lp-"+current_item)
			if (currentHouseType==MapFragment.HOUSE_TYPE_SECOND){
				houseInfo.append(MyUtils.format2String(current_item.getSalePrice()/10000)+"万").append(" ");
//				houseInfo.append(NetContents.SHARE_HOUSE_HOST + "ershoufang/"+current_item.getPostId()+".html");
			}else {
				houseInfo.append(MyUtils.format2String(current_item.getRentPrice())+"元/月").append(" ");
//				houseInfo.append(NetContents.SHARE_HOUSE_HOST + "zufang/"+current_item.getPostId()+".html");
			}

			String conversation_type = ConversationActivity.JINGJIREN;
			switch(currentHouseType) {
				case MapFragment.HOUSE_TYPE_RENT:
					conversation_type = ConversationActivity.ZUFANG;
					break;
				case MapFragment.HOUSE_TYPE_SECOND:
					conversation_type = ConversationActivity.ERSHOUFANG;
					break;
			}
			String imgUrl = current_item.getFullImagePath();
			if (TextUtils.isEmpty(imgUrl)) {
				imgUrl = AppContents.POST_DEFAULT_IMG_URL;
			}
			MyUtils.toStaffTalk(this, current_item.getStaffNo(), current_item.getStaffName(),MyUtils.TALK_FROM_SOURCE_LIST, houseInfo.toString(), conversation_type, current_item.getPostId(),imgUrl,current_item.getEstateName());
		}
	}

	@Override
	protected void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	@OnClick(R.id.house_list_sort)
	public void sortClick(){
		showSort();
	}

//	private PopupWindow popupWindow;

	private MyBottomDialog sortWindow;

	private int sortSelectedPosition;

	public void showSort(){

		if (sortWindow==null){
			ListView sortListView = (ListView) LayoutInflater.from(this).inflate(R.layout.layout_bottom_list, null);
			List<SortBean> sortList = new ArrayList<>();
			if (currentHouseType==MapFragment.HOUSE_TYPE_SECOND){
				sortList.add(new SortBean("默认排序", "DefaultOrder"));
				sortList.add(new SortBean("总价从低到高", "SalePrice"));
				sortList.add(new SortBean("总价从高到低", "SalePriceDesc"));
				sortList.add(new SortBean("单价从低到高", "UnitSalePrice"));
				sortList.add(new SortBean("单价从高到低", "UnitSalePriceDesc"));
				sortList.add(new SortBean("面积从大到小", "GareaDesc"));
				sortList.add(new SortBean("面积从小到大", "Garea"));

			}else if (currentHouseType==MapFragment.HOUSE_TYPE_RENT){
				sortList.add(new SortBean("默认排序", "DefaultOrder"));
				sortList.add(new SortBean("租金从低到高", "RentPrice"));
				sortList.add(new SortBean("租金从高到低", "RentPriceDesc"));
				sortList.add(new SortBean("面积从大到小", "GareaDesc"));
				sortList.add(new SortBean("面积从小到大", "Garea"));
			}else {
				sortList.add(new SortBean("默认排序", "0"));
				sortList.add(new SortBean("均价从低到高", "12"));
				sortList.add(new SortBean("均价从高到低", "21"));
				sortList.add(new SortBean("最近开盘", "1"));
			}

			sortListView.setAdapter(new CommonAdapter<SortBean>(this, sortList, R.layout.item_sort_text) {

				@Override
				public void convert(ViewHolder helper, SortBean item) {
					TextView sort_item_text = helper.getView(R.id.sort_item_text);
					if (sortSelectedPosition==helper.getPosition()){
						sort_item_text.setTextColor(Color.RED);
					}else {
						sort_item_text.setTextColor(Color.BLACK);
					}
					sort_item_text.setText(item.getText());
				}
			});

			sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
					sortSelectedPosition = i;
					if (currentHouseType==MapFragment.HOUSE_TYPE_NEW){
						searchParams.put("SortBy", sortList.get(i).getValue());
					}else {
						searchParams.put("OrderByCriteria", sortList.get(i).getValue());
					}
					houseList.getListData();
					sortWindow.dismiss();
				}
			});

			sortWindow = new MyBottomDialog(this);
			sortWindow.setContentView(sortListView);
		}

		if (sortWindow.isShowing()){
			sortWindow.dismiss();
		}else {
			sortWindow.show();
		}
	}

	/**
	 * 重置指定位置的标签
	 * @param position
	 */
	protected void resetTab(int position) {
		house_drop_menu.resetTab(position);
	}

	/**
	 * 设置标签
	 *
	 * @param position 位置
	 * @param tab      文本
	 */
	protected void setTab(int position, String tab) {
		house_drop_menu.setTab(position, tab);
	}

	private SearchParam createSearch(DropBo dropType){
		SearchParam search = new SearchParam();
		search.setId(dropType.getID()==null?0:dropType.getID());
		search.setText(dropType.getText());
		search.setValue(dropType.getValue());
		search.setTitle(dropType.getName());
		search.setPara(dropType.getPara());
		search.setName(dropType.getName());
		return search;
	}

	private SearchParam createHouseAge1(DropBo dropType){
		SearchParam search = new SearchParam();
		search.setId(dropType.getID());
		search.setText(dropType.getText());
		search.setTitle(dropType.getName());
		search.setValue("0,0");

		String[] yearStr = dropType.getValue().split(",");
		if (yearStr.length>1){
			Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int minYear = year - Integer.parseInt(yearStr[1]);
			int maxYear = year - Integer.parseInt(yearStr[0]);
			search.setValue(minYear+"-1-1"+","+maxYear+"-12-31");
		}
		return search;
	}

	private SearchParam createOpenDate(DropBo dropType){
		SearchParam search = new SearchParam();
		search.setId(dropType.getID());
		search.setText(dropType.getText());
		search.setTitle(dropType.getName());
		search.setValue("0,0");
		search.setPara(dropType.getPara());
		search.setName(dropType.getName());

		String[] monthStr = dropType.getValue().split(",");
		if (monthStr.length>1){

			GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTimeInMillis(System.currentTimeMillis());

			int startMonth = Integer.parseInt(monthStr[0]);
			int endMonth = Integer.parseInt(monthStr[1]);

			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

			calendar.add(Calendar.MONTH,  startMonth);
			String startDate = format.format(calendar.getTime());

			calendar.setTimeInMillis(System.currentTimeMillis());

			calendar.add(Calendar.MONTH,  endMonth);
			String endDate = format.format(calendar.getTime());

			Logger.i(startDate+","+endDate);
			search.setValue(startDate+","+endDate);
		}
		return search;
	}


	private SearchParam createHouseAge(DropBo dropType){
		SearchParam search = new SearchParam();
		search.setId(dropType.getID());
		search.setText(dropType.getText());
		search.setTitle(dropType.getName());
		search.setValue("0,0");
		search.setPara(dropType.getPara());
		search.setName(dropType.getName());

		String[] yearStr = dropType.getValue().split(",");
		if (yearStr.length>1){
			Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

			int minYear = year - Integer.parseInt(yearStr[1]);
			int maxYear = year - Integer.parseInt(yearStr[0]);

			try {
				Date minDate = format.parse(minYear+"-1-1");
				Date maxDate = format.parse(maxYear+"-12-31");

				long minTime = minDate.getTime()/1000;
				long maxTime = maxDate.getTime()/1000;

				search.setValue(minTime+","+maxTime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return search;
	}

	private long intentId;


	private void showSearchTag(boolean isGetData){

		house_search_save.setText("保存");
		intentId = 0;

		if (searchData.size()>0){
			house_search_view.setVisibility(View.VISIBLE);
			house_search_tag.removeAllViews();

			for (SearchParam param : searchData.values()){
				ClearableTextView item = (ClearableTextView) inflater.inflate(R.layout.item_ding_tag, house_search_tag, false);
				item.setText(param.getText());
				item.setTag(param);
				item.setOnClearClickListener(clearClickListener);
				ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) item.getLayoutParams();
				params.setMargins(0, 0, 40, 40);
				searchView.put(param.getKey(), item);
				house_search_tag.addView(item);

				String[] paramKeys = param.getParamKey().split(",");
				if (paramKeys!=null && paramKeys.length>1){
					String[] values = param.getValue().split(",");
					searchParams.put(paramKeys[0], values[0]);
					searchParams.put(paramKeys[1], values[1]);
				}else {
					if (param.getParamKey().equals("Tags")){
						if (searchParams.get("Tags")!=null){
							searchParams.put("Tags", searchParams.get("Tags")+"_"+param.getId());
						}else {
							searchParams.put("Tags", param.getId()+"");
						}
					}else if (param.getParamKey().equals("Feature")){
						if (searchParams.get("Feature")!=null){
							searchParams.put("Feature", searchParams.get("Feature")+"_"+param.getId());
						}else {
							searchParams.put("Feature", param.getId()+"");
						}
					}else {
						searchParams.put(param.getParamKey(), param.getValue());
					}
				}
			}
		}else {
			house_search_view.setVisibility(View.GONE);
		}

		if (isGetData){
			houseList.getListData();
		}
	}

	private void removeData(String key1, String key2, int position, boolean isRest){
		if (key2==null){
			ClearableTextView view = searchView.get(key1);
			if (view!=null){
				house_search_tag.removeView(view);
			}
			searchData.remove(key1);
			searchParams.remove(key1);
		}else {
			ClearableTextView view = searchView.get(key1+","+key2);
			if (view!=null){
				house_search_tag.removeView(view);
			}
			searchData.remove(key1+","+key2);
			searchParams.remove(key1);
			searchParams.remove(key2);
		}
		if (isRest)resetTab(position);
	}

	//订阅提交
	@OnClick(R.id.house_search_save)
	public void saveSubData(){

		if (DataHolder.getInstance().isUserLogin()){
			toSaveSub();
		}else {
			toast("请先登录");
			Intent intent = new Intent(this, LoginActivity.class);
			intent.putExtra(LoginActivity.LOGIN_INTENT_KEY, LoginActivity.LOGIN_INTENT_SUB);
			startActivityForResult(intent, 109);
		}
	}

	private void toSaveSub() {
		if (intentId>0){
//			iPresenter.deleteIntent(intentId);
		}else {
			InsertIntentionsRequest request = new InsertIntentionsRequest();
			if (currentHouseType== MapFragment.HOUSE_TYPE_SECOND){
				request.setSource("ershoufang");
				request.setSearchModelName("大搜索二手房_模糊");
				request.setSearchModel("ershoufang_search");
			}else if (currentHouseType==MapFragment.HOUSE_TYPE_RENT){
				request.setSearchModel("zufang_search");
				request.setSearchModelName("大搜索租房_模糊");
				request.setSource("zufang");
			}else {
				request.setSearchModel("xinfang_search");
				request.setSearchModelName("大搜索新房_模糊");
				request.setSource("xinfang");
			}

			List<SearchParam> searchParams = new ArrayList<>();
			SearchParam last = null;
			for (SearchParam item : searchData.values()){
				if (item.getParamKey().equals("MinAveragePrice,MaxAveragePrice")){
					if (TextUtils.isEmpty(item.getPara())){
						item.setPara("p0");
					}
					last = item;
					continue;
				}

				if (item.getParamKey().equals("MinRentPrice,MaxRentPrice")){
					if (TextUtils.isEmpty(item.getPara())){
						item.setPara("p0");
					}
				}else if (item.getParamKey().equals("MinSalePrice,MaxSalePrice")){
					if (TextUtils.isEmpty(item.getPara())){
						item.setPara("p0");
					}
				}

				if (item.getParamKey().equals("PropertyTypeList")) {
					SearchParam tmp_item = new SearchParam();
					tmp_item.setTitle("Property");
					tmp_item.setPara(item.getPara());
					tmp_item.setParamKey("Property");
					tmp_item.setPara(item.getPara());
					tmp_item.setId(item.getId());
					tmp_item.setKey("Property");
					tmp_item.setName("Property");
					tmp_item.setText(item.getText());
					tmp_item.setValue(item.getValue());
					searchParams.add(tmp_item);
					continue;
				}

				searchParams.add(item);
			}
			if (last!=null){
				searchParams.add(last);
			}
			request.setSearchPara(searchParams);
			request.setUserId(DataHolder.getInstance().getUserId());
			request.setPostTotalNum(total);

			Gson gson = new Gson();
			Logger.i(gson.toJson(request));

			iPresenter.insertIntent(request);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode==LoginActivity.LOGIN_INTENT_SUB){
			saveSubData();
		}
	}

	public static class MenuAnimationListener implements Animation.AnimationListener {

		private EndAction action;

		public void setAction(EndAction action) {
			this.action = action;
		}

		@Override
		public void onAnimationStart(Animation animation) {

		}

		@Override
		public void onAnimationEnd(Animation animation) {
			if (action != null)
				action.action();
		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}


		public interface EndAction {
			void action();
		}
	}

	@Override
	protected String getTalkingDataPageName() {
		return "房源列表";
	}
}
