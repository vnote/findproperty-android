package com.cetnaline.findproperty.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.bean.MyEntrustBo;
import com.cetnaline.findproperty.base.BaseFragment;
import com.cetnaline.findproperty.entity.bean.StaffListBean;
import com.cetnaline.findproperty.presenter.impl.MyEntrustPresenter;
import com.cetnaline.findproperty.presenter.ui.MyEntrustContract;
import com.cetnaline.findproperty.ui.activity.AdviserDetailActivity;
import com.cetnaline.findproperty.ui.activity.EntrustActivity;
import com.cetnaline.findproperty.ui.adapter.DividerItemDecoration;
import com.cetnaline.findproperty.ui.adapter.MyEntrustAdapter;
import com.cetnaline.findproperty.utils.DataHolder;
import com.cetnaline.findproperty.utils.MyUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by fanxl2 on 2016/8/8.
 */
public class MyEntrustFragment extends BaseFragment<MyEntrustPresenter> implements
		MyEntrustContract.View{

	private int fromType;

	public static MyEntrustFragment getInstance(int fromType){
		MyEntrustFragment fragment = new MyEntrustFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("fromType", fromType);
		fragment.setArguments(bundle);
		return fragment;
	}

	@BindView(R.id.my_entrust_lv)
	RecyclerView my_entrust_lv;

	@BindView(R.id.my_entrust_no)
	LinearLayout my_entrust_no;

	private MyEntrustAdapter adapter;
	private List<MyEntrustBo> mData;

	private int pageSize = 10;
	private int pageIndex = 1;
	private boolean isAlloaded = false;
//	private int deletePosition;

	@Override
	protected int getLayoutId() {
		return R.layout.frag_my_entrust;
	}

	@Override
	protected void init() {
		fromType = getArguments().getInt("fromType", 0);
		((EntrustActivity)getActivity()).setToolBarTitle("我的委托");
		mData = new ArrayList<>();
		adapter = new MyEntrustAdapter(getActivity(), mData);
		adapter.setOnBtClickListener((position, iconPosition) -> {

			MyEntrustBo item = mData.get(position);

			if (iconPosition==0){
				MyUtils.toCall400(getActivity(), item.getJJR400(), item.getStaffName());  //打电话
			}else if (iconPosition==1){
				mPresenter.toMsg(getActivity(), item.getStaffName(), item.getStaffNo(), item.getEstateName());  //发消息
			}else if (iconPosition==2){//取消委托
//				deletePosition = position;
				MyUtils.showDiloag(getActivity(),R.layout.dialog_alert, 280, -1,true,((layout, dialog) -> {
					TextView submit = (TextView) layout.findViewById(R.id.submit);
					TextView cancel = (TextView) layout.findViewById(R.id.cancel);
					TextView title = (TextView) layout.findViewById(R.id.title);
					title.setText("是否取消当前委托？");
					submit.setOnClickListener(v-> {
						dialog.dismiss();
						mPresenter.updateEntrust(item.getEntrustID(),"-1",position);
					});
					cancel.setOnClickListener(v-> dialog.dismiss());
				}));
			}else {
				Intent intent = new Intent(getActivity(), AdviserDetailActivity.class);
				StaffListBean staffBean = new StaffListBean();
				staffBean.StaffNo = item.getStaffNo();
				staffBean.CnName = item.getStaffName();
				intent.putExtra(AdviserDetailActivity.ADVISER, staffBean);
				startActivity(intent);
			}
		});

		my_entrust_lv.setLayoutManager(new LinearLayoutManager(getActivity()));
		my_entrust_lv.setItemAnimator(new DefaultItemAnimator());
//		my_entrust_lv.addItemDecoration(new DividerItemDecoration(DividerItemDecoration.VERTICAL_LIST, ContextCompat.getColor(getActivity(), R.color.grayBigLine), MyUtils.dip2px(getActivity(), 6)));
		my_entrust_lv.setAdapter(adapter);

		my_entrust_lv.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);
			}

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				if (recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset() >= recyclerView.computeVerticalScrollRange()) {
					if (!isAlloaded) {
						mPresenter.getMyEntrustList(DataHolder.getInstance().getUserId(), pageIndex, pageSize, false);
					}
				}
			}
		});

		mPresenter.getMyEntrustList(DataHolder.getInstance().getUserId(), pageIndex,pageSize, false);

		mPresenter.registerListListener(); //监听新增委托
	}

	@Override
	protected MyEntrustPresenter createPresenter() {
		return new MyEntrustPresenter();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.entrust_my_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId()==R.id.entrust_add_entrust){
			if (fromType==0){
//				addFragment(EntrustFragment.getInstance(EntrustActivity.ENTRUST_TYPE_SALE, 1));
				mPresenter.goFormPage(getActivity());
			}else {
				((EntrustActivity)getActivity()).toBack();
			}
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void setMyEntrustList(List<MyEntrustBo> entrustList, long total,boolean reload) {

		if (mData == null) {
			mData = new ArrayList<>();
		}

		if (reload) {
			mData.clear();
			pageIndex = 1;
			isAlloaded = false;
		}

		my_entrust_no.setVisibility(View.GONE);
		my_entrust_lv.setVisibility(View.VISIBLE);

		//没有查到数据
		if (entrustList == null || entrustList.size() == 0) {
			if (mData.size() > 0) {
				isAlloaded = true;
			} else {
				//没有可显示的数据
				my_entrust_no.setVisibility(View.VISIBLE);
				my_entrust_lv.setVisibility(View.GONE);
			}
		} else {
			if (mData.size() + entrustList.size() >= total) {
				isAlloaded = true;
			}
			adapter.setDatas(entrustList);
		}
		pageIndex++;

//		if (entrustList==null || entrustList.size()==0){
//			my_entrust_no.setVisibility(View.VISIBLE);
//			my_entrust_lv.setVisibility(View.GONE);
//		}else {
//			my_entrust_no.setVisibility(View.GONE);
//			my_entrust_lv.setVisibility(View.VISIBLE);
//
//			DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new EntrustDiff(mData, entrustList), true);
//			diffResult.dispatchUpdatesTo(adapter);
//			this.mData = entrustList;
//			adapter.setDatas(mData);
//		}
	}

	@Override
	public void setDelEntrustResult(boolean result,long entrustId, int position) {
		toast("取消委托成功");
		//获取更新的记录
		mPresenter.getEntrust(result, entrustId+"", position);
		//重新获取委托数据
//		mPresenter.getMyEntrustList(DataHolder.getInstance().getUserId());
//		if (adapter.getItemCount() == 0) {
//			my_entrust_no.setVisibility(View.VISIBLE);
//			my_entrust_lv.setVisibility(View.GONE);
//		}
	}

	@Override
	public void updateListForCancel(MyEntrustBo bo, int pos) {
		adapter.delOne(bo, pos);
	}


	@Override
	public void onResume() {
		super.onResume();
		((EntrustActivity)getActivity()).setToolBarTitle("我的委托");
	}

	@OnClick(R.id.my_entrust_go)
	public void goEntrust(){
//		addFragment(EntrustFragment.getInstance(EntrustActivity.ENTRUST_TYPE_SALE, 1));
		mPresenter.goFormPage(getActivity());
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