package com.cetnaline.findproperty.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.alexvasilkov.gestures.commons.DepthPageTransformer;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.bean.LookAboutList;
import com.cetnaline.findproperty.api.bean.LookAboutListDetailBo;
import com.cetnaline.findproperty.base.BaseFragment;
import com.cetnaline.findproperty.base.BaseFragmentActivity;
import com.cetnaline.findproperty.entity.bean.StaffListBean;
import com.cetnaline.findproperty.entity.event.NormalEvent;
import com.cetnaline.findproperty.api.NetContents;
import com.cetnaline.findproperty.presenter.impl.ToLookAboutPresenter;
import com.cetnaline.findproperty.presenter.ui.ToLookAboutContract;
import com.cetnaline.findproperty.ui.activity.HouseDetail;
import com.cetnaline.findproperty.ui.activity.LookAbout;
import com.cetnaline.findproperty.ui.activity.WebActivity;
import com.cetnaline.findproperty.ui.adapter.LookAboutRecordAdapter;
import com.cetnaline.findproperty.ui.adapter.LookRecordAdapter;
import com.cetnaline.findproperty.utils.DataHolder;
import com.cetnaline.findproperty.utils.MyUtils;
import com.cetnaline.findproperty.utils.RxBus;
import com.cetnaline.findproperty.utils.SchedulersCompat;
import com.cetnaline.findproperty.widgets.ExpandableListPageView;
import com.cetnaline.findproperty.widgets.IndicatorView;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


/**
 * 待约看  约看记录
 * Created by fanxl2 on 2016/8/20.
 */
public class ToLookAboutFragment extends BaseFragment<ToLookAboutPresenter> implements ToLookAboutContract.View {

	public static final String LOOK_TYPE_KEY = "LOOK_TYPE_KEY";
	private int lookType;

	private static final int[] pages = new int[]{R.layout.look_pager_item_1,
			R.layout.look_pager_item_2, R.layout.look_pager_item_3};

	public static ToLookAboutFragment getInstance(int lookType){
		ToLookAboutFragment fragment = new ToLookAboutFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(LOOK_TYPE_KEY, lookType);
		fragment.setArguments(bundle);
		return fragment;
	}

	private int currentPage = 1;

	@BindView(R.id.look_about_list)
	ExpandableListPageView look_about_list;

	private LookRecordAdapter adapter;

	private List<LookAboutList> datas;

	@BindView(R.id.no_data)
	LinearLayout no_data;

	@BindView(R.id.guide_vp)
	ViewPager guide_vp;

	@BindView(R.id.guide_enter)
	AppCompatTextView guide_enter;

	@BindView(R.id.guide_iv)
	IndicatorView guide_iv;

	@Override
	public void onResume() {
		super.onResume();
		initData();
	}

	private void initData() {
		if (lookType==LookAbout.LOOK_TYPE_TO){
			((LookAbout)getActivity()).setLeftText("待约看");
			mPresenter.getLookAboutList(DataHolder.getInstance().getUserId(), 1, 1, ExpandableListPageView.PAGE_SIZE);
		}else {
			((LookAbout)getActivity()).setLeftText("约看记录");
			mPresenter.getLookAboutList(DataHolder.getInstance().getUserId(), 2, 1, ExpandableListPageView.PAGE_SIZE);
		}
	}

	@Override
	protected String getTalkingDataPageName() {
		if (lookType==LookAbout.LOOK_TYPE_TO){
			return "待约看页";
		}else {
			return  "约看记录页";
		}
	}

	@Override
	protected int getLayoutId() {
		return R.layout.frag_to_look_about;
	}

	@Override
	protected void init() {

		lookType = getArguments().getInt(LOOK_TYPE_KEY, LookAbout.LOOK_TYPE_TO);

		datas = new ArrayList<>();

		look_about_list.setGroupIndicator(null);
		adapter = new LookRecordAdapter(datas, getActivity(), lookType);
		look_about_list.setAdapter(adapter);
		look_about_list.setOnPageLoadListener(new ExpandableListPageView.OnPageLoadListener() {
			@Override
			public void onPageChanging(int pageSize, int pageIndex) {
				look_about_list.setProgressBarVisible(true);
				currentPage = pageIndex;
				mPresenter.getLookAboutList(DataHolder.getInstance().getUserId(), 1, currentPage, pageSize);
			}

			@Override
			public boolean canLoadData() {
				// TODO: 2017/1/22 暂时隐藏分页功能
//				return !(totalNumber==datas.size());
				return false;
			}
		});

		adapter.setRightButtonClick((parentP, childP, iconPosition) -> {

			LookAboutList parent = datas.get(parentP);
			StaffListBean item = parent.getStaffDetail();

			switch (iconPosition){
				case LookAboutRecordAdapter.BT_TYPE_CALL:
					MyUtils.toCall400(getActivity(), item.getStaff400Tel(), item.getCnName());
					break;
				case LookAboutRecordAdapter.BT_TYPE_EVALUATE:
					if (lookType==LookAbout.LOOK_TYPE_TO){
						addFragment(LookEvaluateFragment.getInstance(parent.getPlanID(), parent.getEstateName(), item));
					}else {
						Intent intent = new Intent(getActivity(), WebActivity.class);
						StringBuffer sb = new StringBuffer(NetContents.EVALUATE_URL);
						String userId = DataHolder.getInstance().getUserId();
						sb.append("?").append("userid=").append(userId).append("&").append("staffno=").append(parent.getLookStaff()).append("&")
								.append("planid=").append(parent.getPlanID());
						Logger.i("实勘地址:"+sb.toString());
						intent.putExtra(WebActivity.TARGET_URL, sb.toString());
						startActivity(intent);
					}
					break;
				case LookAboutRecordAdapter.BT_TYPE_HOUSE:

					LookAboutListDetailBo child = parent.getList().get(childP);

					if (!child.isIsOnline()){
						return;
					}
					Intent intent = new Intent(getActivity(), HouseDetail.class);
					if (child.getPostType().equalsIgnoreCase("S")){
						intent.putExtra(MapFragment.HOUSE_TYPE_KEY, MapFragment.HOUSE_TYPE_SECOND);
					}else {
						intent.putExtra(MapFragment.HOUSE_TYPE_KEY, MapFragment.HOUSE_TYPE_RENT);
					}
					intent.putExtra(HouseDetailFragment.HOUSE_ID_KEY, child.getPostID());
					startActivity(intent);
					break;
				case LookAboutRecordAdapter.BT_TYPE_MSG:
					MyUtils.toStaffTalk(getActivity(), item.getStaffNo(), item.getCnName(), MyUtils.TALK_FROM_ADVISER_LIST, "", "", "","","");
					break;
				case LookAboutRecordAdapter.BT_TYPE_STAFF:
					MyUtils.toStoreHome(getActivity(), item.getStaffNo(), item.getCnName());
					break;

			}
		});

		RxBus.getDefault().toObservable(NormalEvent.class)
				.compose(SchedulersCompat.applyIoSchedulers())
				.subscribe(normalEvent -> {
					if (normalEvent.type == NormalEvent.REFRESH_LOOK_LIST) {
						initData();
					}
				});
	}

	private void initGuide() {
		guide_vp.setPageTransformer(true, new DepthPageTransformer());
		guide_vp.setAdapter(new GuideAdapter());
		guide_iv.setViewPager(guide_vp);
		guide_enter.setOnClickListener(view -> {
			Intent intent = new Intent(getActivity(), LookAbout.class);
			intent.putExtra(BaseFragmentActivity.FRAGMENT_TYPE, LookAbout.LOOK_TYPE_LIST);
			startActivityForResult(intent,1001);
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1001) {
			initData();
		}
	}

	@Override
	public void showNoData(boolean flag){
		if (flag) {
			no_data.setVisibility(View.VISIBLE);
			initGuide();
		} else {
			no_data.setVisibility(View.GONE);
		}
	}

	private int totalNumber;

	@Override
	public void setTotalNumber(int totalNumber) {
		this.totalNumber=totalNumber;
	}

	//刷新界面
	private void refreshExpandableListView(){
		adapter.notifyDataSetChanged();
		int groupCount = adapter.getGroupCount();
		for (int i=0; i<groupCount; i++) {
			look_about_list.collapseGroup(i); //收缩组
			look_about_list.expandGroup(i); //展开组
		}
	}

	@Override
	protected ToLookAboutPresenter createPresenter() {
		return new ToLookAboutPresenter();
	}

	@Override
	public void setLookAboutList(List<LookAboutListDetailBo> lookAboutList) {
		if (lookAboutList==null || lookAboutList.size()==0){
			look_about_list.setProgressBarVisible(false);
			showNoData(true);
		}else {
			look_about_list.setProgressBarVisible(false);
			showNoData(false);
			group(lookAboutList);
			refreshExpandableListView();
		}
	}

	private void group(List<LookAboutListDetailBo> target){
		if (currentPage==1){
			datas.clear();
		}
		if (target==null)return;
		for (int i=0; i<target.size();){
			LookAboutListDetailBo item = target.get(0);
			target.remove(item);
			LookAboutList newItem = new LookAboutList();
			newItem.setLookStaff(item.getStaffNo());
			newItem.setPlanID(item.getPlanID());
			newItem.setPlanCode(item.getPlanCode());
			newItem.setEstateName(item.getEstateName());
			newItem.setLookTime(item.getLookTime());
			newItem.setStatus(item.getStatus());
			newItem.getList().add(item);
			for (int j=0; j<target.size();){
				LookAboutListDetailBo child = target.get(j);
				if (child.getPlanCode().equals(item.getPlanCode())){
					newItem.getList().add(child);
					newItem.setEstateName(newItem.getEstateName()+","+child.getEstateName());
					target.remove(child);
				}else {
					j++;}
			}
			newItem.getList().add(new LookAboutListDetailBo());
			datas.add(newItem);
		}

//		for (LookAboutList item : datas){
//			item.getList().add(new LookAboutListDetailBo());
//		}
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


	class GuideAdapter extends PagerAdapter {

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public int getCount() {
			return pages.length;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public View instantiateItem(ViewGroup container, int position) {
			View view = getActivity().getLayoutInflater().from(getActivity()).inflate(pages[position],null);
			container.addView(view);
			return view;
		}
	}
}
