package com.cetnaline.findproperty.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.bean.BatchCollectRequest;
import com.cetnaline.findproperty.api.bean.InsertCollectInfoRequest;
import com.cetnaline.findproperty.api.bean.LookAboutList;
import com.cetnaline.findproperty.api.bean.LookAboutListDetailBo;
import com.cetnaline.findproperty.api.bean.LookListDeleteRequest;
import com.cetnaline.findproperty.base.BaseFragment;
import com.cetnaline.findproperty.presenter.impl.LookAboutListPresenter;
import com.cetnaline.findproperty.presenter.ui.LookAboutListContract;
import com.cetnaline.findproperty.ui.activity.HouseDetail;
import com.cetnaline.findproperty.ui.activity.HouseList;
import com.cetnaline.findproperty.ui.activity.LookAbout;
import com.cetnaline.findproperty.ui.activity.VillageDetail;
import com.cetnaline.findproperty.ui.adapter.LookAboutAdapter;
import com.cetnaline.findproperty.utils.DataHolder;
import com.cetnaline.findproperty.utils.MyViewHolder;
import com.google.gson.Gson;
import com.jakewharton.rxbinding.view.RxView;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;

/**
 * 约看单
 * Created by fanxl2 on 2016/8/16.
 */
public class LookAboutListFragment extends BaseFragment<LookAboutListPresenter> implements LookAboutListContract.View {

	public static LookAboutListFragment getInstance(){
		LookAboutListFragment fragment = new LookAboutListFragment();
		return fragment;
	}

	@BindView(R.id.look_about_el)
	ExpandableListView look_about_el;

	@BindView(R.id.look_to_collect)
	Button look_to_collect;

	@BindView(R.id.look_list_delete)
	Button look_list_delete;

	@BindView(R.id.look_to_about)
	Button look_to_about;

	@BindView(R.id.no_data_layout)
	LinearLayout no_data_layout;

	@BindView(R.id.list_layout)
	RelativeLayout list_layout;

	@BindView(R.id.go_sale)
	TextView go_sale;
	@BindView(R.id.go_rent)
	TextView go_rent;

	private LookAboutAdapter adapter;
	private List<LookAboutList> datas;

	private boolean isEdit;
	private String userId;

	private MenuItem tmpItem;

	@Override
	protected int getLayoutId() {
		return R.layout.frag_look_about_list;
	}

	@Override
	public void onResume() {
		super.onResume();
		((LookAbout)getActivity()).setLeftText("约看单");
		mPresenter.getLookAboutList(DataHolder.getInstance().getUserId(), 0);
	}

	private HashMap<String, LookAboutListDetailBo> selectParam;

	@Override
	protected void init() {
		//设置 属性 GroupIndicator 去掉默认向下的箭头
		look_about_el.setGroupIndicator(null);
		datas = new ArrayList<>();
		adapter = new LookAboutAdapter(datas, getActivity());
		look_about_el.setAdapter(adapter);

		look_about_el.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
				Intent intent = new Intent(getActivity(), VillageDetail.class);
				intent.putExtra(VillageDetailFragment.ESTATE_CODE_KEY, datas.get(i).getEstateCode());
				startActivity(intent);
				return true;
			}
		});

		look_about_el.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int childP, long l) {

				LookAboutListDetailBo childItem = adapter.getChild(i, childP);
				if (isEdit){
					CheckBox item_child_select = MyViewHolder.get(view, R.id.item_child_select);
					if (item_child_select.isChecked()){
						item_child_select.setChecked(false);
						adapter.checkClick(item_child_select, false, i, childP, childItem);
					}else {
						item_child_select.setChecked(true);
						adapter.checkClick(item_child_select, true, i, childP, childItem);
					}
				}else {
					if (!childItem.isIsOnline())return false;
					Intent intent = new Intent(getActivity(), HouseDetail.class);
					if (childItem.getPostType().equalsIgnoreCase("S")){
						intent.putExtra(MapFragment.HOUSE_TYPE_KEY, MapFragment.HOUSE_TYPE_SECOND);
					}else {
						intent.putExtra(MapFragment.HOUSE_TYPE_KEY, MapFragment.HOUSE_TYPE_RENT);
					}
					intent.putExtra(HouseDetailFragment.HOUSE_ID_KEY, childItem.getPostID());
					startActivity(intent);
				}

				return true;
			}
		});

		userId = DataHolder.getInstance().getUserId();

		RxView.clicks(look_list_delete).throttleFirst(2, TimeUnit.SECONDS)
				.subscribe(aVoid -> {
					deleteLookAbout();
				});

		RxView.clicks(look_to_about).throttleFirst(2, TimeUnit.SECONDS)
				.subscribe(aVoid -> {
					selectParam = adapter.getSelctParam();
					if (selectParam.size()==0){
						toast("请先选择要约看的房源");
					}else {
						mPresenter.getLookedAboutNumber(DataHolder.getInstance().getUserId(), 1);
					}
				});

		RxView.clicks(look_to_collect).throttleFirst(2, TimeUnit.SECONDS)
				.subscribe(aVoid -> {
					insertCollectBatch();
				});

		go_sale.setOnClickListener(v->{
			Intent intent = new Intent(getActivity(), HouseList.class);
			intent.putExtra(MapFragment.HOUSE_TYPE_KEY, MapFragment.HOUSE_TYPE_SECOND);
			startActivity(intent);
		});

		go_rent.setOnClickListener(v->{
			Intent intent = new Intent(getActivity(), HouseList.class);
			intent.putExtra(MapFragment.HOUSE_TYPE_KEY, MapFragment.HOUSE_TYPE_RENT);
			startActivity(intent);
		});
	}


	//刷新界面
	private void refreshExpandableListView(){
		adapter.notifyDataSetChanged();
		int groupCount = adapter.getGroupCount();
		for (int i=0; i<groupCount; i++) {
			look_about_el.collapseGroup(i); //收缩组
			look_about_el.expandGroup(i); //展开组
		}
	}

	@Override
	protected LookAboutListPresenter createPresenter() {
		return new LookAboutListPresenter();
	}

	@Override
	public void setLookAboutList(List<LookAboutListDetailBo> lookAboutList) {
		if (lookAboutList == null || lookAboutList.size() == 0) {
			showNoData(true);
			return;
		}else {
			showNoData(false);
		}

		adapter.clearSelect();
		group(lookAboutList);
		refreshExpandableListView();
	}

	@Override
	public void deleteLookAboutResult(boolean result) {
		if (result){
			if (!isCollect){
				isCollect = false;
				toast("删除成功");
			}
			changeEditStatus();
			adapter.clearSelect();
			mPresenter.getLookAboutList(DataHolder.getInstance().getUserId(), 0);
		}else {
			toast("删除失败");
		}
	}

	@Override
	public void setLookedAboutNumber(int num) {
		if (num + selectParam.size()>6){
			toast("超过预约上限, 您还可预约"+(6-num)+"套房源");
		}else {
			addFragment(LookWorkFragment.getInstance(selectParam));
		}
	}

	private boolean isCollect;

	@Override
	public void setCollectResult(boolean result) {
		if (result){
			isCollect = true;
			toast("移入收藏成功");
			deleteLookAbout();
		}else {
			toast("移入收藏失败");
		}
	}

	@Override
	public void showNoData(boolean flag) {
		if (flag) {
			no_data_layout.setVisibility(View.VISIBLE);
			list_layout.setVisibility(View.GONE);
			mState = MENU_HIDE;
		}
		else {
			no_data_layout.setVisibility(View.GONE);
			list_layout.setVisibility(View.VISIBLE);
			mState = MENU_SHOW;
		}
		getActivity().invalidateOptionsMenu();
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


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.look_about_list_menu, menu);


		for (int i = 0; i < menu.size();i++){
			menu.getItem(i).setVisible(mState==MENU_SHOW);
		}

//		if (DataHolder.getInstance().getMsgCount()>0){
//			menu.findItem(R.id.look_about_msg).setIcon(R.drawable.ic_has_msg_black);
//		}
		super.onCreateOptionsMenu(menu, inflater);
	}

	private int mState;
	public static final int MENU_SHOW = 0;
	public static final int MENU_HIDE = 1;

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId()==R.id.look_about_edit) {
			tmpItem = item;
			changeEditStatus();
		}
//		}else if (item.getItemId()==R.id.look_about_msg){
////			EventBus.getDefault().post(new AppLoginEvent(true, AppLoginEvent.CALL_CHAT_FRAGMENT));
//			Intent intent = new Intent(getActivity(), MainTabActivity.class);
//			intent.putExtra(MainTabActivity.SELECTED_TAB_KEY, MainTabActivity.TAB_CHAT);
//			getActivity().startActivity(intent);
//			((LookAbout)getActivity()).setFinish();
//		}
		return super.onOptionsItemSelected(item);
	}

	private void changeEditStatus() {
		if (isEdit){
			isEdit = false;
			tmpItem.setTitle("编辑");
			adapter.setCanSelect(false);
			look_list_delete.setVisibility(View.GONE);
			look_to_collect.setVisibility(View.GONE);
			look_to_about.setVisibility(View.VISIBLE);
			adapter.setLimit(!isEdit);
		}else {
			isEdit = true;
			tmpItem.setTitle("完成");
			adapter.setCanSelect(true);
			look_list_delete.setVisibility(View.VISIBLE);
			look_to_collect.setVisibility(View.VISIBLE);
			look_to_about.setVisibility(View.GONE);
			adapter.setLimit(!isEdit);
		}
		adapter.clearSelect();
		refreshExpandableListView();
	}

	private void group(List<LookAboutListDetailBo> target){
		datas.clear();
		if (target==null)return;
		for (int i=0; i<target.size();){
			LookAboutListDetailBo item = target.get(0);
			target.remove(item);
			LookAboutList newItem = new LookAboutList();
			newItem.setEstateCode(item.getEstateCode());
			newItem.setEstateName(item.getEstateName());
			newItem.getList().add(item);
			for (int j=0; j<target.size();){
				LookAboutListDetailBo child = target.get(j);
				if (child.getEstateCode() != null && child.getEstateCode().equals(item.getEstateCode())){
					newItem.getList().add(child);
					target.remove(child);
				}else {
					j++;
				}
			}
			datas.add(newItem);
		}

//		datas.get(0).getList().get(0).setIsOnline(false);
	}

	private void deleteLookAbout(){
		Map<String, LookAboutListDetailBo> selectParam = adapter.getSelctParam();
		if (selectParam.size()>0){
			LookListDeleteRequest request = new LookListDeleteRequest();
			request.setUserId(userId);
			List<String> postIds = new ArrayList<>();
			for (LookAboutListDetailBo item : selectParam.values()){
				postIds.add(item.getListID());
			}
			request.setLooklistids(postIds);
			Logger.i(new Gson().toJson(request));
			mPresenter.deleteLookAboutList(request);
		}
	}

	private void insertCollectBatch() {
		Map<String, LookAboutListDetailBo> selectParam = adapter.getSelctParam();
		if (selectParam.size()>0){

			BatchCollectRequest request = new BatchCollectRequest();
			request.setUserId(userId);

			List<InsertCollectInfoRequest> batchList = new ArrayList<>();
			for (LookAboutListDetailBo item : selectParam.values()){
				InsertCollectInfoRequest collect = new InsertCollectInfoRequest();
				collect.setCollectValue(item.getPostID());
				if (item.getPostType().equalsIgnoreCase("S")){
					collect.setSource("ershoufang");
				}else {
					collect.setSource("zufang");
				}
				collect.setUserId(userId);
				batchList.add(collect);
			}
			request.setBatch(batchList);
			mPresenter.insertCollectBatch(request);
		}
	}

}
