package com.cetnaline.findproperty.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.bean.InsertIntentionsRequest;
import com.cetnaline.findproperty.api.bean.SearchParam;
import com.cetnaline.findproperty.base.BaseFragment;
import com.cetnaline.findproperty.db.entity.DropBo;
import com.cetnaline.findproperty.db.entity.GScope;
import com.cetnaline.findproperty.api.NetContents;
import com.cetnaline.findproperty.presenter.impl.IntentPresenter;
import com.cetnaline.findproperty.presenter.ui.IntentContract;
import com.cetnaline.findproperty.ui.activity.IntentSettingActivity2;
import com.cetnaline.findproperty.ui.adapter.ChildAdapter;
import com.cetnaline.findproperty.ui.adapter.ParentAdapter;
import com.cetnaline.findproperty.utils.DataHolder;
import com.cetnaline.findproperty.utils.DbUtil;
import com.cetnaline.findproperty.widgets.StepLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 意向 区域选择
 * Created by fanxl2 on 2016/8/27.
 */
public class IntentRegionFragment extends BaseFragment<IntentPresenter> implements IntentContract.View {

	public static IntentRegionFragment getInstance(HashMap<String, SearchParam> data, int houseType){
		IntentRegionFragment fragment = new IntentRegionFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(MapFragment.HOUSE_TYPE_KEY, houseType);
		bundle.putSerializable(IntentHouseTypeFragment.INTENT_DATA_KEY, data);
		fragment.setArguments(bundle);
		return fragment;
	}

	private List<DropBo> parents = new ArrayList<>();

	@BindView(R.id.main_lv_parent)
	ListView main_lv_parent;

	@BindView(R.id.main_lv_child)
	ListView main_lv_child;

	@BindView(R.id.step_layout)
	StepLayout step_layout;

	private ParentAdapter parentAdapter;
	private ChildAdapter childAdapter;

	private int houseType;

	@Override
	protected int getLayoutId() {
		return R.layout.frag_intent_region;
	}

	private int parentSelectedPosition;
	private List<GScope> gScopes;

	private HashMap<String, SearchParam> param;

	@Override
	protected void init() {

		param = (HashMap<String, SearchParam>) getArguments().getSerializable(IntentHouseTypeFragment.INTENT_DATA_KEY);
		houseType = getArguments().getInt(MapFragment.HOUSE_TYPE_KEY);

		step_layout.enableStep(4);

		gScopes = DbUtil.getGScopeChild(21);
		for (GScope gScope : gScopes){
			List<GScope> childs = DbUtil.getGScopeChild(gScope.getGScopeId());
			GScope noGscope = new GScope();
			noGscope.setGScopeName("全部");
			noGscope.setGScopeAlias(gScope.getGScopeName());
			noGscope.setGScopeId(gScope.getGScopeId());
			noGscope.setGScopeLevel(2);
			noGscope.setParentId(0);
			childs.add(0, noGscope);
			gScope.setgScopeList(childs);
		}

		GScope noGscope = new GScope();
		noGscope.setGScopeName("全部");
		noGscope.setGScopeId(-1);
		noGscope.setParentId(-1);
		noGscope.setGScopeLevel(-1);
		noGscope.setgScopeList(new ArrayList<>());
		gScopes.add(0, noGscope);

		for (GScope item : gScopes){
			DropBo parentItem = new DropBo(item.getGScopeName(), item.getGScopeId()+"", 0);
			parentItem.setID(item.getGScopeId());
			parentItem.setPara(item.getFullPY());
			parentItem.setName(NetContents.REGION_NAME);
			parents.add(parentItem);
		}

		parentAdapter = new ParentAdapter(getActivity(), parents, R.layout.item_text);
		childAdapter = new ChildAdapter(getActivity(), new ArrayList<>(), R.layout.item_text);
		parentAdapter.setSelectedPosition(parentSelectedPosition);

		main_lv_parent.setAdapter(parentAdapter);
		main_lv_parent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				if (i==0){

				}else {
					SearchParam search = createSearch(parents.get(i));
					search.setKey("RegionId");
					param.put(search.getKey(), search);
				}
				parentSelectedPosition = i;
				parentAdapter.setSelectedPosition(parentSelectedPosition);
				setChildData(i);
			}
		});

		main_lv_child.setAdapter(childAdapter);
		main_lv_child.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				DropBo childItem = childAdapter.getItem(i);
				if (i==0){
				}else {
					SearchParam search = createSearch(childItem);
					search.setKey("GScopeId");
					param.put(search.getKey(), search);
				}
				childAdapter.setSelectedPosition(i);
			}
		});
	}

	private void setChildData(int i) {
		if (i==0){
			childAdapter.setData(new ArrayList<>());
		}else {
			List<GScope> childs = gScopes.get(i).getgScopeList();
			List<DropBo> childList = new ArrayList<DropBo>();
			for (GScope item : childs){
				DropBo parentItem = new DropBo(item.getGScopeName(), item.getGScopeId()+"", 0);
				parentItem.setID(item.getGScopeId());
				parentItem.setPara(item.getFullPY());
				parentItem.setName(NetContents.GSCOPE_NAME);
				childList.add(parentItem);
			}
			childAdapter.setData(childList);
			childAdapter.setSelectedPosition(0);
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

	@Override
	protected IntentPresenter createPresenter() {
		return new IntentPresenter();
	}

	@OnClick(R.id.intent_bt_commit)
	public void intentCommit(){
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
		mPresenter.insertIntent(request);
	}

	@Override
	public void setInsertResult() {
		toast("意向保存成功");
		DataHolder.getInstance().setChangeIntent(true);
		((IntentSettingActivity2)getActivity()).finish();
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
}
