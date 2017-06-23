package com.cetnaline.findproperty.ui.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.bean.InsertIntentionsRequest;
import com.cetnaline.findproperty.api.bean.SearchParam;
import com.cetnaline.findproperty.base.BaseFragment;
import com.cetnaline.findproperty.db.entity.DropBo;
import com.cetnaline.findproperty.db.entity.GScope;
import com.cetnaline.findproperty.api.NetContents;
import com.cetnaline.findproperty.presenter.impl.IntentPresenter;
import com.cetnaline.findproperty.presenter.ui.IntentContract;
import com.cetnaline.findproperty.ui.activity.IntentSettingActivity;
import com.cetnaline.findproperty.ui.adapter.DividerItemDecoration;
import com.cetnaline.findproperty.utils.DataHolder;
import com.cetnaline.findproperty.utils.DbUtil;
import com.cetnaline.findproperty.widgets.HorizontalWheel;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by fanxl2 on 2016/11/4.
 */

public class IntentConditionFragment extends BaseFragment<IntentPresenter> implements IntentContract.View {

	public static final int INTENT_FOR_PRICE = 0;
	public static final int INTENT_FOR_HOUSE_TYPE = 1;
	public static final int INTENT_FOR_REGION = 2;

	public static IntentConditionFragment getInstance(HashMap<String, SearchParam> data, int houseType, int type){
		IntentConditionFragment fragment = new IntentConditionFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("type", type);
		bundle.putSerializable(IntentHouseTypeFragment.INTENT_DATA_KEY, data);
		bundle.putInt(MapFragment.HOUSE_TYPE_KEY, houseType);
		fragment.setArguments(bundle);
		return fragment;
	}

	@BindView(R.id.intent_tv_title)
	TextView intent_tv_title;

	@BindView(R.id.condition_rv_region)
	RecyclerView condition_rv_region;

	@BindView(R.id.intent_bg_next)
	Button intent_bg_next;

	@BindView(R.id.condition_img_bg)
	ImageView condition_img_bg;

	@BindView(R.id.intent_condition_wheel)
	HorizontalWheel intent_condition_wheel;

	@BindView(R.id.intent_rl_wheel)
	RelativeLayout intent_rl_wheel;

	@Override
	protected int getLayoutId() {
		return R.layout.frag_intent_condition;
	}

	private int intentCondition;

	private HashMap<String, SearchParam> param;

	private int houseType;

	@Override
	protected void init() {
		intentCondition = getArguments().getInt("type");
		param = (HashMap<String, SearchParam>) getArguments().getSerializable(IntentHouseTypeFragment.INTENT_DATA_KEY);
		houseType = getArguments().getInt(MapFragment.HOUSE_TYPE_KEY);

		List<DropBo> dropBos;

		if (intentCondition==INTENT_FOR_PRICE){
			if (houseType==MapFragment.HOUSE_TYPE_SECOND){
				dropBos = DbUtil.getSearchDataByName("Sell");
				intent_tv_title.setText("您想买的房价范围");
			}else if (houseType==MapFragment.HOUSE_TYPE_RENT){
				dropBos = DbUtil.getSearchDataByName("Rent");
				intent_tv_title.setText("您想租的房价范围");
			}else {
				dropBos = DbUtil.getSearchDataByName("NewHousePriceN");
				intent_tv_title.setText("您想买的房价范围");
			}
			if (dropBos != null && dropBos.size() > 0) {
				dropBos.remove(0);

				intent_condition_wheel.setItems(dropBos);
				intent_condition_wheel.setWheelListener(new HorizontalWheel.WheelListener() {
					@Override
					public void scrollSelected(int pos) {
						if (pos >=0) {
							setSelectedData(dropBos.get(pos));
						}
					}
				});

				setSelectedData(dropBos.get(0));
			}

		}else if (intentCondition==INTENT_FOR_HOUSE_TYPE){

			condition_img_bg.setImageResource(R.drawable.ic_choice_room);

			if (houseType==MapFragment.HOUSE_TYPE_SECOND){
				intent_tv_title.setText("您想买的户型");
			}else if (houseType==MapFragment.HOUSE_TYPE_RENT){
				intent_tv_title.setText("您想租的户型");
			}
			dropBos = DbUtil.getSearchDataByName("Room");
			if (dropBos != null && dropBos.size() > 0)
				dropBos.remove(0);

			intent_condition_wheel.setItems(dropBos);
			intent_condition_wheel.setWheelListener(new HorizontalWheel.WheelListener() {
				@Override
				public void scrollSelected(int pos) {
					if (pos >= 0) {
						SearchParam search = createSearch(dropBos.get(pos));
						search.setKey("MinRoomCnt,MaxRoomCnt");
						param.put(search.getKey(), search);
					}
				}
			});

			SearchParam search = createSearch(dropBos.get(0));
			search.setKey("MinRoomCnt,MaxRoomCnt");
			param.put(search.getKey(), search);
		}else if (intentCondition==INTENT_FOR_REGION){

			intent_rl_wheel.setVisibility(View.GONE);

			intent_bg_next.setBackgroundResource(R.drawable.bt_line_white_filled);
			intent_bg_next.setTextColor(Color.parseColor("#fb2727"));

			if (houseType==MapFragment.HOUSE_TYPE_SECOND){
				intent_tv_title.setText("您想买在什么位置");
			}else if (houseType==MapFragment.HOUSE_TYPE_RENT){
				intent_tv_title.setText("您想租在什么位置");
			}

			condition_img_bg.setImageResource(R.drawable.ic_intent_region_bg);
			condition_rv_region.setVisibility(View.VISIBLE);
			intent_bg_next.setText("完成");

			List<GScope> regions = DbUtil.getGScopeChild(21);
			GScope noLimit = new GScope();
			noLimit.setGScopeName("不限");
			noLimit.setId(-1L);
			regions.add(0, noLimit);

			RegionAdapter adapter = new RegionAdapter(getActivity(), R.layout.item_intent_region, regions);
			adapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
				@Override
				public void onItemClick(View view, RecyclerView.ViewHolder holder, Object o, int position) {

					if (position==0){
						param.remove("RegionId");
					}else {
						GScope gScope = regions.get(position);
						SearchParam search = new SearchParam();
						search.setId(gScope.getGScopeId());
						search.setText(gScope.getGScopeName());
						search.setValue(gScope.getGScopeId()+"");
						search.setTitle("区域");
						search.setPara(gScope.getFullPY());
						search.setName(NetContents.REGION_NAME);
						search.setKey("RegionId");
						param.put(search.getKey(), search);
					}
					selectedP = position;
					adapter.notifyDataSetChanged();
				}

				@Override
				public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, Object o, int position) {
					return false;
				}
			});
			condition_rv_region.setLayoutManager(new GridLayoutManager(getActivity(), 3));
			condition_rv_region.addItemDecoration(new DividerItemDecoration(LinearLayoutManager.VERTICAL, Color.TRANSPARENT, AutoUtils.getPercentHeightSize(30)));
			condition_rv_region.setAdapter(adapter);
		}
	}

	private void setSelectedData(DropBo dropBo) {
		SearchParam search = createSearch(dropBo);
		if (houseType== MapFragment.HOUSE_TYPE_SECOND){
			search.setKey("MinSalePrice,MaxSalePrice");
		}else if (houseType==MapFragment.HOUSE_TYPE_RENT){
			search.setKey("MinRentPrice,MaxRentPrice");
		}else {
			search.setKey("MinAveragePrice,MaxAveragePrice");
		}
		param.put(search.getKey(), search);
	}

	@OnClick(R.id.intent_bg_next)
	public void nextClick(){
		if (intentCondition==IntentConditionFragment.INTENT_FOR_PRICE){
			addFragment(IntentConditionFragment.getInstance(param, houseType, IntentConditionFragment.INTENT_FOR_HOUSE_TYPE));
		}else if (intentCondition==IntentConditionFragment.INTENT_FOR_HOUSE_TYPE){
			addFragment(IntentConditionFragment.getInstance(param, houseType, IntentConditionFragment.INTENT_FOR_REGION));
		}else if (intentCondition==IntentConditionFragment.INTENT_FOR_REGION){

			//			for (SearchParam item : param.values()){
//				Logger.i("选择:"+item.getText());
//			}


			InsertIntentionsRequest request = new InsertIntentionsRequest();
			if (houseType==MapFragment.HOUSE_TYPE_SECOND){
				request.setSource("ershoufang");
				request.setSearchModelName("大搜索二手房_模糊");
				request.setSearchModel("ershoufang_search");
			}else if (houseType==MapFragment.HOUSE_TYPE_RENT){
				request.setSearchModel("zufang_search");
				request.setSearchModelName("大搜索租房_模糊");
				request.setSource("zufang");
			}else {
				request.setSearchModel("xinfang_search");
				request.setSearchModelName("大搜索新房_模糊");
				request.setSource("xinfang");
			}

			List<SearchParam> searchParams = new ArrayList<>();
			for (SearchParam item : param.values()){
				searchParams.add(item);
			}
			request.setSearchPara(searchParams);
			request.setPostTotalNum(0);
			request.setUserId(DataHolder.getInstance().getUserId());

			Logger.i(new Gson().toJson(request));
			mPresenter.insertIntent(request);
		}
	}

	private int selectedP;

	@Override
	protected IntentPresenter createPresenter() {
		return new IntentPresenter();
	}

	@Override
	public void setInsertResult() {
		toast("意向保存成功");
		DataHolder.getInstance().setChangeIntent(true);
		((IntentSettingActivity)getActivity()).close();
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

	public class RegionAdapter extends CommonAdapter<GScope>{

		public RegionAdapter(Context context, int layoutId, List<GScope> datas) {
			super(context, layoutId, datas);
		}

		@Override
		protected void convert(ViewHolder holder, GScope gScope, int position) {

			CheckedTextView item_region_name = holder.getView(R.id.item_region_name);
			if (selectedP==position){
				item_region_name.setChecked(true);
			}else {
				item_region_name.setChecked(false);
			}
			item_region_name.setText(gScope.getGScopeName());
		}
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


}
