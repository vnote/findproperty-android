package com.cetnaline.findproperty.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.bean.EstateBo;
import com.cetnaline.findproperty.api.bean.SearchParam;
import com.cetnaline.findproperty.api.bean.SortBean;
import com.cetnaline.findproperty.base.BaseFragment;
import com.cetnaline.findproperty.base.BaseFragmentActivity;
import com.cetnaline.findproperty.db.entity.DropBo;
import com.cetnaline.findproperty.entity.event.UnreadEvent;
import com.cetnaline.findproperty.inter.RefreshListener;
import com.cetnaline.findproperty.api.NetContents;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.cetnaline.findproperty.presenter.impl.EstateListPresenter;
import com.cetnaline.findproperty.presenter.ui.EstateListContract;
import com.cetnaline.findproperty.ui.adapter.CommonAdapter;
import com.cetnaline.findproperty.ui.adapter.ViewHolder;
import com.cetnaline.findproperty.ui.fragment.HouseListFragment;
import com.cetnaline.findproperty.ui.fragment.MapFragment;
import com.cetnaline.findproperty.utils.DataHolder;
import com.cetnaline.findproperty.utils.DbUtil;
import com.cetnaline.findproperty.utils.StatusBarCompat;
import com.cetnaline.findproperty.widgets.ClearableTextView;
import com.cetnaline.findproperty.widgets.FlowTag;
import com.cetnaline.findproperty.widgets.dropdown.DropCompleteListener;
import com.cetnaline.findproperty.widgets.dropdown.DropDownView;
import com.cetnaline.findproperty.widgets.dropdown.PriceDrop;
import com.cetnaline.findproperty.widgets.dropdown.SingleDrop;
import com.cetnaline.findproperty.widgets.dropdown.ThreeColumnDrop;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.rong.imkit.RongIM;
import io.rong.imkit.model.Event;
import io.rong.imlib.model.Conversation;

/**
 * 房源列表
 * Created by fanxl2 on 2016/8/1.
 */
public class EstateList extends BaseFragmentActivity<EstateListPresenter> implements EstateListContract.View {

	@BindView(R.id.house_drop_menu)
	DropDownView house_drop_menu;

	@BindView(R.id.house_search_tag)
	FlowTag house_search_tag;

	@BindView(R.id.list_view_line)
	View list_view_line;

	public static final String ESTATE_KEY_WORD = "ESTATE_KEY_WORD";

	private HouseListFragment houseList;

	private int currentHouseType;

	private MenuItem menuItem;

	private LayoutInflater inflater;

	private HashMap<String, SearchParam> searchData = new HashMap<>();

	private HashMap<String, ClearableTextView> searchView = new HashMap<>();

	private ClearableTextView.ClearClickListener clearClickListener = new ClearableTextView.ClearClickListener() {
		@Override
		public void onClearClick(ClearableTextView view) {
			SearchParam searchParam = (SearchParam) view.getTag();
			house_search_tag.removeView(view);
			searchData.remove(searchParam.getKey());
			searchView.remove(searchParam.getKey());

			String[] paramKeys = searchParam.getParamKey().split(",");
			if (paramKeys!=null && paramKeys.length>1){
				searchParams.remove(paramKeys[0]);
				searchParams.remove(paramKeys[1]);
			}else {
				searchParams.remove(searchParam.getParamKey());
			}

			if ("EsfAvgPrice".equals(searchParam.getName()+"")){
				resetTab(1);
				priceDrop.resetSelectStatus();
			}else if (NetContents.REGION_NAME.equalsIgnoreCase(searchParam.getName()+"") || NetContents.GSCOPE_NAME.equalsIgnoreCase(searchParam.getName()+"")
					|| NetContents.SCHOOL_NAME.equalsIgnoreCase(searchParam.getName()+"")){
				resetTab(0);
				threeDrop.resetSelectStatus();
			}else if ("Construction".equals(searchParam.getName()+"")){
				resetTab(2);
				buildTimeDrop.resetSelectStatus();
			}else if ("PropertyEstate".equals(searchParam.getName()+"")){
				resetTab(3);
				propertyDrop.resetSelectStatus();
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
			}else if (NetContents.RAILWAY_NAME.equalsIgnoreCase(searchParam.getName()+"")){
				SearchParam line = searchData.get("RailLineId");
				setTab(0, line.getText());
			}

			showSearchTag(true);
		}
	};

	private RefreshListener refreshListener = new RefreshListener() {
		@Override
		public void down() {
			getData();
		}

		@Override
		public void up(int count) {
			toTop = false;
			searchParams.put("PageIndex", count+"");
			if (searchParams.get("RailWayId")!=null || searchParams.get("RailLineId")!=null){
				iPresenter.getEstateByMetro(searchParams);
			}else if (searchParams.get("SchoolId")!=null){
				iPresenter.getEstateBySchool(searchParams);
			}else {
				iPresenter.getEstateList(searchParams);
			}
		}

		@Override
		public void loadDataAgain() {
			houseList.getListData();
		}
	};

	private void getData() {
		toTop = true;
		searchParams.put("PageIndex", "1");
		if (searchParams.get("RailWayId")!=null || searchParams.get("RailLineId")!=null){
			iPresenter.getEstateByMetro(searchParams);
		}else if (searchParams.get("SchoolId")!=null){
			iPresenter.getEstateBySchool(searchParams);
		}else {
			iPresenter.getEstateList(searchParams);
		}
	}

	//单选
	private PriceDrop priceDrop;

	private ThreeColumnDrop threeDrop;

	private SingleDrop buildTimeDrop;

	private SingleDrop propertyDrop;

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
		houseList = HouseListFragment.getInstance(HouseListFragment.LIST_TYPE_SMALL, currentHouseType);
		houseList.setRefreshListener(refreshListener);
		return houseList;
	}

	@Override
	protected int getFragmentContentId() {
		return R.id.fragment_container;
	}

	@Override
	protected int getContentViewId() {
		return R.layout.act_estate_list;
	}

	@Override
	protected IPresenter createPresenter() {
		return new EstateListPresenter();
	}

	@Override
	protected void initToolbar() {
		StatusBarCompat.setStatusBarColor(this, Color.WHITE);
		toolbar.setNavigationOnClickListener(view -> {
			toBack();
		});
	}


	@Override
	protected void initView(Bundle savedInstanceState) {

		inflater = LayoutInflater.from(this);

		currentHouseType = getIntent().getIntExtra(MapFragment.HOUSE_TYPE_KEY, MapFragment.HOUSE_TYPE_ESTATE);

		String keyWord = getIntent().getStringExtra(ESTATE_KEY_WORD);
		if (keyWord!=null && !TextUtils.isEmpty(keyWord+"")){
			searchParams.put("HybridKey", keyWord);

			SearchParam keyParam = new SearchParam();
			keyParam.setKey("HybridKey");
			keyParam.setValue(keyWord);
			keyParam.setText(keyWord);
			searchData.put("HybridKey", keyParam);
		}

		house_drop_menu.setDropCompleteListener(dropCompleteListener);

		threeDrop = new ThreeColumnDrop(house_drop_menu, this);
		priceDrop = new PriceDrop(house_drop_menu, this, null);
		buildTimeDrop = new SingleDrop(house_drop_menu, this, null);
		propertyDrop = new SingleDrop(house_drop_menu, this, null);

		house_drop_menu.addDrops(threeDrop, priceDrop, buildTimeDrop, propertyDrop);

		updateDropMenu();

		searchParams.put("PageCount", "10");
		searchParams.put("ImageWidth", NetContents.HOUSE_SMALL_IMAGE_LIST_WIDTH+"");
		searchParams.put("ImageHeight", NetContents.HOUSE_SMALL_IMAGE_LIST_HEIGHT+"");

		showSearchTag(false);
	}

	private void updateDropMenu(){

		toolbar.setTitle("小区");

		List<DropBo> leftDrop = new ArrayList<>();
		DropBo one = new DropBo("区域", "", 0);
		DropBo two = new DropBo("地铁", "", 1);
		DropBo three = new DropBo("学校", "", 2);
		leftDrop.add(one);
		leftDrop.add(two);
		leftDrop.add(three);
		threeDrop.init(leftDrop);

		house_drop_menu.initTabs(R.array.listEstate);
		List<DropBo> estateDropDatas = DbUtil.getSearchDataByName("EsfAvgPrice");
		priceDrop.init(estateDropDatas, currentHouseType);

		List<DropBo> buildDatas = DbUtil.getSearchDataByName("Construction");
		buildTimeDrop.init(buildDatas);

		List<DropBo> PropertyDatas = DbUtil.getSearchDataByName("PropertyEstate");
		propertyDrop.init(PropertyDatas);
	}

	@Override
	public void onResume() {
		super.onResume();
		refreshUnreadCount();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_list, menu);
		if (DataHolder.getInstance().getMsgCount()>0){
			menuItem = menu.findItem(R.id.house_list_msg);
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
		if (DataHolder.getInstance().isUserLogin()){
			Intent intent = new Intent(this, MainTabActivity.class);
			intent.putExtra(MainTabActivity.SELECTED_TAB_KEY, MainTabActivity.TAB_CHAT);
			startActivity(intent);
			this.finish();
		}else {
			toast("请先登录");
			startActivity(new Intent(this, LoginActivity.class));
		}
		return super.onOptionsItemSelected(item);
	}

	private boolean toTop;

	@Override
	public void showLoading() {

	}

	@Override
	public void dismissLoading() {

	}

	@Override
	public void showError(String msg) {
		toast(msg);
	}

	private Map<String, String> searchParams = new HashMap<>();

	private void dropMenuClose(int position, DropBo... dropBos){
		switch (position){
			case 0:

//				searchParams.remove("SchoolId");
				removeData("SchoolId", null, position, false);
//				searchParams.remove("RegionId");
				removeData("RegionId", null, position, false);
//				searchParams.remove("GScopeId");
				removeData("GScopeId", null, position, false);
//				searchParams.remove("RailLineId");
				removeData("RailLineId", null, position, false);
//				searchParams.remove("RailWayId");
				removeData("RailWayId", null, position, false);

				DropBo dropBo;
				if (dropBos.length>1){
					dropBo = dropBos[0];
					SearchParam lineSp = createSearch(dropBo);
					lineSp.setText(dropBo.getValue());
					lineSp.setValue(dropBo.getID()+"");
					lineSp.setKey(dropBo.getKey());
					searchData.put(lineSp.getKey(), lineSp);

					DropBo wayDb = dropBos[1];
					SearchParam waySp = createSearch(wayDb);
					waySp.setKey(wayDb.getKey());
					searchData.put(waySp.getKey(), waySp);

//					searchParams.put(dropBo.getKey(), dropBo.getValue());

					setTab(position, wayDb.getText());
				}else {
					dropBo = dropBos[0];

					if ("不限".equals(dropBo.getName())){
						resetTab(0);
					}else if (NetContents.REGION_NAME.equalsIgnoreCase(dropBo.getName())){
						SearchParam search = createSearch(dropBo);
						if (dropBo.getType()==-1){
							search.setText(dropBo.getValue());
							search.setValue(dropBo.getID()+"");
						}
						search.setKey(dropBo.getKey());
						searchData.put(search.getKey(), search);
//						searchParams.put(dropBo.getKey(), dropBo.getID()+"");
						setTab(position, dropBo.getValue());
					}else if (NetContents.GSCOPE_NAME.equalsIgnoreCase(dropBo.getName())){
						SearchParam search = createSearch(dropBo);
						search.setKey(dropBo.getKey());
						searchData.put(search.getKey(), search);
//						searchParams.put(dropBo.getKey(), dropBo.getValue());
						setTab(position, dropBo.getText());
					}else if (NetContents.SCHOOL_NAME.equalsIgnoreCase(dropBo.getName())){
						SearchParam search = createSearch(dropBo);
						search.setKey(dropBo.getKey());
						searchData.put(search.getKey(), search);
//						searchParams.put(dropBo.getKey(), dropBo.getValue());
						setTab(position, dropBo.getText());
					}else if (NetContents.RAILLINE_NAME.equalsIgnoreCase(dropBo.getName())){
						SearchParam search = createSearch(dropBo);
						if (dropBo.getType()==-1){
							search.setText(dropBo.getValue());
							search.setValue(dropBo.getID()+"");
						}
						search.setKey(dropBo.getKey());
						searchData.put(search.getKey(), search);
//						searchParams.put(dropBo.getKey(), dropBo.getID()+"");
						setTab(position, dropBo.getValue());
					}
				}
				break;
			case 1:
				DropBo dropType = dropBos[0];
				String[] prices = dropType.getValue().split(",");
				if (prices.length>1){
					SearchParam search = createSearch(dropType);
					search.setKey("MinSaleAvgPrice,MaxSaleAvgPrice");
					search.setName("EsfAvgPrice");
					searchData.put(search.getKey(), search);
//					searchParams.put("MinSaleAvgPrice", prices[0]);
//					searchParams.put("MaxSaleAvgPrice", prices[1]);
					setTab(position, dropType.getText());
				}else {
					removeData("MinSaleAvgPrice", "MaxSaleAvgPrice", position, true);
//					searchParams.remove("MinSaleAvgPrice");
//					searchParams.remove("MaxSaleAvgPrice");
//					resetTab(1);
				}
				break;
			case 2:
				DropBo timeDrop = dropBos[0];
				String[] times = timeDrop.getValue().split(",");
				if (times.length>1){
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
					try {
						Date minDate = format.parse(times[0]);
						Date maxDate = format.parse(times[1]);

						long minTime = minDate.getTime()/1000;
						long maxTime = maxDate.getTime()/1000;

						SearchParam search = createSearch(timeDrop);
						search.setKey("MinOpdate,MaxOpdate");
						search.setValue(minTime+","+maxTime);
						searchData.put(search.getKey(), search);

//						searchParams.put("MinOpdate", minTime+"");
//						searchParams.put("MaxOpdate", maxTime+"");
						setTab(position, timeDrop.getText());
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}else {
					removeData("MinOpdate", "MaxOpdate", position, true);
//					searchParams.remove("MinOpdate");
//					searchParams.remove("MaxOpdate");
//					resetTab(2);
				}
				break;
			case 3:
				DropBo propertyDrop = dropBos[0];
				if ("不限".equals(propertyDrop.getText())){
					removeData("PropertyTypeList", null, position, true);
//					searchParams.remove("PropertyTypeList");
//					resetTab(3);
				}else {
					SearchParam search = createSearch(propertyDrop);
					search.setKey("PropertyTypeList");
					searchData.put(search.getKey(), search);
//					searchParams.put("PropertyTypeList", propertyDrop.getValue());
					setTab(position, propertyDrop.getText());
				}
				break;

		}

//		Logger.i(new Gson().toJson(searchParams));

		showSearchTag(true);
//		houseList.getListData();
	}


	@Override
	protected boolean hasShade() {
		return true;
	}


	@OnClick(R.id.house_list_sort)
	public void sortClick(){
		showSort();
	}

	private PopupWindow popupWindow;

	private int sortSelectedPosition;

	public void showSort(){
		if (popupWindow==null){

			ListView listView = (ListView) LayoutInflater.from(this).inflate(R.layout.layout_bottom_list, null);
			popupWindow = new PopupWindow(listView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			popupWindow.setBackgroundDrawable(new ColorDrawable());
			popupWindow.setOutsideTouchable(true);
			popupWindow.setFocusable(true);
			popupWindow.update();
			popupWindow.setAnimationStyle(R.style.PopBottomToTop);

			List<SortBean> sortList = new ArrayList<>();
			sortList.add(new SortBean("默认排序", "DefaultOrder"));
			sortList.add(new SortBean("均价从低到高", "SaleAvgPrice"));
			sortList.add(new SortBean("均价从高到低", "SaleAvgPriceDesc"));
			sortList.add(new SortBean("年代从近到远", "OpDateDesc"));
			sortList.add(new SortBean("年代从远到近", "OpDate"));

			listView.setAdapter(new CommonAdapter<SortBean>(this, sortList, R.layout.item_sort_text) {

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

			listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
					sortSelectedPosition = i;
					searchParams.put("OrderByCriteria", sortList.get(i).getValue());
//					refreshListener.down();
					houseList.getListData();
					popupWindow.dismiss();
				}
			});
		}
		popupWindow.showAtLocation(house_drop_menu, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
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

	@Override
	public void setEstateList(List<EstateBo> estateList) {
		this.houseList.setRefreshEstateData(estateList, toTop);
	}

	@Override
	public void netWorkException() {
		this.houseList.setRefreshData(null, false);
		houseList.netWorkException();
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

	private void showSearchTag(boolean isGetData){
		if (searchData.size()>0){
			list_view_line.setVisibility(View.GONE);
			house_search_tag.setVisibility(View.VISIBLE);
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
					searchParams.put(param.getParamKey(), param.getValue());
				}
			}
		}else {
			list_view_line.setVisibility(View.VISIBLE);
			house_search_tag.setVisibility(View.GONE);
		}
		Logger.i(new Gson().toJson(searchParams));

		if (isGetData){
			getData();
		}
	}

	@Override
	protected String getTalkingDataPageName() {
		return "房源列表";
	}
}
