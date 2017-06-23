package com.cetnaline.findproperty.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.cetnaline.findproperty.AppContents;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.bean.EstateBo;
import com.cetnaline.findproperty.api.bean.HouseBo;
import com.cetnaline.findproperty.api.bean.NewHouseListBo;
import com.cetnaline.findproperty.base.BaseFragment;
import com.cetnaline.findproperty.inter.IRecycleViewListener;
import com.cetnaline.findproperty.inter.RefreshListener;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.cetnaline.findproperty.ui.activity.ConversationActivity;
import com.cetnaline.findproperty.ui.activity.HouseDetail;
import com.cetnaline.findproperty.ui.activity.LoginActivity;
import com.cetnaline.findproperty.ui.activity.VillageDetail;
import com.cetnaline.findproperty.ui.adapter.BigEstateListAdapter;
import com.cetnaline.findproperty.ui.adapter.BigNewListAdapter;
import com.cetnaline.findproperty.ui.adapter.BigPicListAdapter;
import com.cetnaline.findproperty.ui.adapter.SmallPicListAdapter;
import com.cetnaline.findproperty.utils.MyUtils;
import com.cetnaline.findproperty.widgets.MRecyclerView;
import com.zhy.adapter.recyclerview.CommonAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 房源列表——小图模式
 * Created by fanxl2 on 2016/8/1.
 */
public class HouseListFragment extends BaseFragment {

	public static HouseListFragment getInstance(int type, int houseType){
		HouseListFragment smallList = new HouseListFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(LIST_TYPE, type);
		bundle.putInt(MapFragment.HOUSE_TYPE_KEY, houseType);
		smallList.setArguments(bundle);
		return smallList;
	}

	public static final String LIST_TYPE = "LIST_TYPE";
	public static final int LIST_TYPE_BIG = 0;
	public static final int LIST_TYPE_SMALL = 1;

	private int listType = 0;
	private int houseType;

	@BindView(R.id.house_list_small)
	MRecyclerView house_list_small;

	@BindView(R.id.house_list_big)
	MRecyclerView house_list_big;

	private BigPicListAdapter bigPicListAdapter;

	private CommonAdapter smallPicListAdapter;

	private BigNewListAdapter bigNewListAdapter;

//	private SmallNewListAdapter smallNewListAdapter;

	private RefreshListener refreshListener;
	private int iRefreshType = 1;

	private int pageIndex = 1;

	private boolean stopMoreData;

	@Override
	protected int getLayoutId() {
		return R.layout.frag_house_small;
	}

	private IRecycleViewListener iRecycleViewListener = new IRecycleViewListener() {
		@Override
		public void downRefresh() {
			//下拉刷新
			stopMoreData = false;
			iRefreshType = 1;
			pageIndex = 1;
			if (refreshListener!=null){
				refreshListener.down();
			}
		}

		@Override
		public void upRefresh() {
			if (stopMoreData){
				stopRefresh(false, false);
				return;
			}
			iRefreshType = 2;
			pageIndex++;
			if (refreshListener!=null){
				refreshListener.up(pageIndex);
			}
		}

		@Override
		public void onItemClick(int position) {

			Intent intent;
			if (houseType==MapFragment.HOUSE_TYPE_ESTATE){
				intent = new Intent(getActivity(), VillageDetail.class);

				EstateBo estate = estateList.get(position);
				intent.putExtra(VillageDetailFragment.ESTATE_CODE_KEY, estate.getEstateCode());
			}else if (houseType==MapFragment.HOUSE_TYPE_NEW){
				intent = new Intent(getActivity(), HouseDetail.class);
				intent.putExtra(MapFragment.HOUSE_TYPE_KEY, houseType);

				NewHouseListBo newBo = newDatas.get(position);
				intent.putExtra(HouseDetailFragment.ESTEXT_ID_KEY, newBo.getEstExtId());
			}else{
				intent = new Intent(getActivity(), HouseDetail.class);
				intent.putExtra(MapFragment.HOUSE_TYPE_KEY, houseType);

				HouseBo houseBo = datas.get(position);
				intent.putExtra(HouseDetailFragment.HOUSE_ID_KEY, houseBo.getPostId());
			}

			startActivity(intent);
		}

		@Override
		public void onScroll() {

		}

		@Override
		public void loadDataAgain() {
			if (refreshListener!=null){
				refreshListener.loadDataAgain();
			}
		}
	};

	private List<HouseBo> datas;
	private List<NewHouseListBo> newDatas;
	private List<EstateBo> estateList;
	private int selectedP;

	@Override
	protected void init() {
		listType = getArguments().getInt(LIST_TYPE, LIST_TYPE_BIG);
		houseType = getArguments().getInt(MapFragment.HOUSE_TYPE_KEY, MapFragment.HOUSE_TYPE_SECOND);

		house_list_big.setIRecycleViewListener(iRecycleViewListener);
		house_list_small.setIRecycleViewListener(iRecycleViewListener);

		house_list_big.setDefaultText("该条件下暂无房源");
		house_list_small.setDefaultText("该条件下暂无房源");
		house_list_big.setDefaultLogo(R.drawable.ic_no_house);
		house_list_small.setDefaultLogo(R.drawable.ic_no_house);

		if (houseType==MapFragment.HOUSE_TYPE_NEW){
			newDatas = new ArrayList<>();

			pageIndex = 0;

			bigNewListAdapter = new BigNewListAdapter(getActivity(), R.layout.item_house_new_big, newDatas);
//			smallNewListAdapter = new SmallNewListAdapter(getActivity(), R.layout.item_house_new_small, newDatas);
			house_list_big.setAdapter(bigNewListAdapter, "已显示全部房源");
//			house_list_small.setAdapter(smallNewListAdapter);
		}else {
			if (houseType==MapFragment.HOUSE_TYPE_SECOND){
				datas = new ArrayList<>();
				bigPicListAdapter = new BigPicListAdapter(getActivity(), R.layout.item_house_big, datas, MapFragment.HOUSE_TYPE_SECOND);
				smallPicListAdapter = new SmallPicListAdapter(getActivity(), R.layout.item_house_small, datas, MapFragment.HOUSE_TYPE_SECOND);

				house_list_big.setAdapter(bigPicListAdapter, "已显示全部房源");
				house_list_small.setAdapter(smallPicListAdapter, "已显示全部房源");
			}else if (houseType==MapFragment.HOUSE_TYPE_RENT){
				datas = new ArrayList<>();
				bigPicListAdapter = new BigPicListAdapter(getActivity(), R.layout.item_house_rent_big, datas, MapFragment.HOUSE_TYPE_RENT);
				smallPicListAdapter = new SmallPicListAdapter(getActivity(), R.layout.item_house_rent_small, datas, MapFragment.HOUSE_TYPE_RENT);

				house_list_big.setAdapter(bigPicListAdapter, "已显示全部房源");
				house_list_small.setAdapter(smallPicListAdapter, "已显示全部房源");
			}else {
				house_list_small.setDefaultText("该条件下暂无小区");
				estateList = new ArrayList<>();
				smallPicListAdapter = new BigEstateListAdapter(getActivity(), R.layout.item_estate_big, estateList);
				house_list_small.setAdapter(smallPicListAdapter, "已显示全部小区");
			}
		}

		if (listType==LIST_TYPE_BIG){
			switchList(false, null);
			house_list_big.startRefresh();
		}else {
			switchList(true, null);
			house_list_small.startRefresh();
		}
	}

	private void goToTalk(int position) {
		if (houseType != MapFragment.HOUSE_TYPE_NEW) {
			HouseBo houseBo = datas.get(position);
			StringBuffer houseInfo = new StringBuffer();
			houseInfo.append(houseBo.getRoomCount()+"室"+houseBo.getHallCount()+"厅").append(" ")
					.append(MyUtils.format2String(houseBo.getGArea())+"㎡").append(" ");
			if (houseType==MapFragment.HOUSE_TYPE_SECOND){
				houseInfo.append(MyUtils.format2String(houseBo.getSalePrice()/10000)+"万").append(" ");
//				houseInfo.append(NetContents.SHARE_HOUSE_HOST + "ershoufang/"+houseBo.getPostId()+".html");
			}else {
				houseInfo.append(MyUtils.format2String(houseBo.getRentPrice())+"元/月").append(" ");
//				houseInfo.append(NetContents.SHARE_HOUSE_HOST + "ershoufang/"+houseBo.getPostId()+".html");
			}

			String conversation_type = ConversationActivity.JINGJIREN;
			switch(houseType) {
				case MapFragment.HOUSE_TYPE_RENT:
					conversation_type = ConversationActivity.ZUFANG;
					break;
				case MapFragment.HOUSE_TYPE_SECOND:
					conversation_type = ConversationActivity.ERSHOUFANG;
					break;
			}
			String imgUrl = houseBo.getFullImagePath();
			if (TextUtils.isEmpty(imgUrl)) {
				imgUrl = AppContents.POST_DEFAULT_IMG_URL;
			}
			MyUtils.toStaffTalk(getActivity(), houseBo.getStaffNo(), houseBo.getStaffName(), MyUtils.TALK_FROM_SOURCE_LIST, houseInfo.toString(), conversation_type, houseBo.getPostId(),imgUrl,houseBo.getTitle());
		}
	}

	@Override
	protected IPresenter createPresenter() {
		return null;
	}


	public void switchList(boolean isSmall, List<HouseBo> houseBoList){
		if (isSmall){
			listType = LIST_TYPE_SMALL;
			house_list_small.setVisibility(View.VISIBLE);
			house_list_big.setVisibility(View.GONE);
		}else {
			listType = LIST_TYPE_BIG;
			house_list_small.setVisibility(View.GONE);
			house_list_big.setVisibility(View.VISIBLE);
		}
		if (houseBoList!=null){
			setRefreshData(houseBoList, false);
		}
	}

	public void setRefreshListener(RefreshListener refreshListener){
		this.refreshListener=refreshListener;
	}

	public void setRefreshData(List<HouseBo> houseBoList, boolean toTop){

		if (toTop){
			iRefreshType = 1;
		}

		if (iRefreshType==1){
			datas.clear();
		}

		if (houseBoList==null || houseBoList.size()==0){
			datas.clear();
			stopRefresh(false, toTop);
			house_list_big.setLoadDataBtVisible(View.GONE);
			house_list_small.setLoadDataBtVisible(View.GONE);
		}else {
			datas.addAll(houseBoList);
			stopRefresh(true, toTop);
		}
	}

	public void noMoreData(){
		stopMoreData = true;
		stopRefresh(false, false);
	}

	public void setRefreshEstateData(List<EstateBo> estateDatas, boolean toTop){

		if (toTop){
			iRefreshType = 1;
		}

		if (iRefreshType==1){
			estateList.clear();
		}

		if (estateDatas==null || estateDatas.size()==0){
			stopRefresh(false, toTop);
			house_list_big.setLoadDataBtVisible(View.GONE);
			house_list_small.setLoadDataBtVisible(View.GONE);
		}else {
			estateList.addAll(estateDatas);
			stopRefresh(true, toTop);
		}
	}

	public void noData(){
		house_list_big.setDefaultText("该条件下暂无数据");
		house_list_small.setDefaultText("该条件下暂无数据");
		house_list_big.setLoadDataBtVisible(View.GONE);
		house_list_small.setLoadDataBtVisible(View.GONE);
		house_list_big.setDefaultLogo(R.drawable.ic_no_house);
		house_list_small.setDefaultLogo(R.drawable.ic_no_house);
		stopRefresh(false, false);
	}

	public void netWorkException(){
		house_list_big.setDefaultText("网络不给力");
		house_list_small.setDefaultText("网络不给力");
		house_list_big.setDefaultLogo(R.drawable.ic_no_network);
		house_list_small.setDefaultLogo(R.drawable.ic_no_network);
		house_list_big.setLoadDataBtVisible(View.VISIBLE);
		house_list_small.setLoadDataBtVisible(View.VISIBLE);
	}

	public void setRefreshNewData(List<NewHouseListBo> houseBoList, boolean toTop){

		if (iRefreshType==1){
			newDatas.clear();
		}

		if (houseBoList==null || houseBoList.size()==0){
			stopRefresh(false, toTop);
		}else {
			newDatas.addAll(houseBoList);
			stopRefresh(true, toTop);
		}
	}

	private void stopRefresh(boolean hasMore, boolean toTop){

		house_list_small.stopRefresh(hasMore);
		house_list_small.toTopPosition(toTop);
		if (houseType!=MapFragment.HOUSE_TYPE_ESTATE){
			house_list_big.stopRefresh(hasMore);
			house_list_big.toTopPosition(toTop);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode==LoginActivity.LOGIN_INTENT_TALK){
			goToTalk(selectedP);
		}
	}

	public void getListData(){
		if (listType==LIST_TYPE_BIG){
			house_list_big.startRefresh();
		}else {
			house_list_small.startRefresh();
		}
	}

	public int getAdapterSize(){
		if (houseType==MapFragment.HOUSE_TYPE_NEW){
			return newDatas==null?0:newDatas.size();
		}else if (houseType==MapFragment.HOUSE_TYPE_ESTATE){
			return estateList==null?0:estateList.size();
		}else {
			return datas==null?0:datas.size();
		}
	}

	@Override
	protected String getTalkingDataPageName() {
		if (houseType==MapFragment.HOUSE_TYPE_NEW) {
			return "新房列表页";
		} else if (houseType==MapFragment.HOUSE_TYPE_SECOND){
			return  "二手房列表页";
		}else if (houseType==MapFragment.HOUSE_TYPE_RENT){
			return "租房列表页";
		}else {
			return "小区列表页";
		}
	}
}
