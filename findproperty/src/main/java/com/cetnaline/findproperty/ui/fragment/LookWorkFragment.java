package com.cetnaline.findproperty.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.TextView;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.bean.LookAboutListDetailBo;
import com.cetnaline.findproperty.api.bean.OrderRequest;
import com.cetnaline.findproperty.api.bean.SendAppointmentRequest;
import com.cetnaline.findproperty.api.bean.StaffComment;
import com.cetnaline.findproperty.api.bean.StaffNoRequest;
import com.cetnaline.findproperty.base.BaseFragment;
import com.cetnaline.findproperty.entity.event.NormalEvent;
import com.cetnaline.findproperty.entity.event.StaffChoiceEvent;
import com.cetnaline.findproperty.presenter.impl.LookWorkPresenter;
import com.cetnaline.findproperty.presenter.ui.LookWorkContract;
import com.cetnaline.findproperty.ui.activity.ExchangePhoneActivity;
import com.cetnaline.findproperty.ui.activity.LookAbout;
import com.cetnaline.findproperty.ui.adapter.LookWordAdapter;
import com.cetnaline.findproperty.utils.DataHolder;
import com.cetnaline.findproperty.utils.MyUtils;
import com.cetnaline.findproperty.utils.RxBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.rong.eventbus.EventBus;
import rx.Subscription;

/**
 * Created by fanxl2 on 2016/8/17.
 */
public class LookWorkFragment extends BaseFragment<LookWorkPresenter> implements LookWorkContract.View {

	public static final String LOOK_DATA_KEY = "LOOK_DATA_KEY";

	public static LookWorkFragment getInstance(HashMap<String, LookAboutListDetailBo> param){
		LookWorkFragment fragment = new LookWorkFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable(LOOK_DATA_KEY, param);
		fragment.setArguments(bundle);
		return fragment;
	}

	@BindView(R.id.look_work_rv)
	RecyclerView look_work_rv;

	@BindView(R.id.look_work_phone)
	TextView look_work_phone;

	@BindView(R.id.look_work_user)
	TextView look_work_user;

	private LookWordAdapter adapter;
	private List<LookAboutListDetailBo> datas;

	private Subscription subscription;

	@Override
	protected int getLayoutId() {
		return R.layout.frag_look_work;
	}

	@Override
	public void onResume() {
		super.onResume();
		((LookAbout)getActivity()).setLeftText("填写约看信息");
	}

	private int currentHouseP;

	@Override
	protected void init() {

		datas = new ArrayList<>();

		String phone = DataHolder.getInstance().getUserPhone();
		if (!TextUtils.isEmpty(phone)){
			look_work_phone.setText(phone);
		}

		HashMap<String, LookAboutListDetailBo> selectedData = (HashMap<String, LookAboutListDetailBo>) getArguments().getSerializable(LOOK_DATA_KEY);
		for (LookAboutListDetailBo item : selectedData.values()){
			datas.add(item);
		}

		look_work_user.setHorizontallyScrolling(true);

		subscription = RxBus.getDefault().toObservable(StaffChoiceEvent.class)
				.subscribe(staffChoiceEvent -> {
					datas.get(currentHouseP).setSelectedP(staffChoiceEvent.getPosition());
					adapter.notifyDataSetChanged();
				});

		look_work_rv.setLayoutManager(new LinearLayoutManager(getActivity()));
		adapter = new LookWordAdapter(getActivity(), R.layout.item_look_adviser, datas);
		look_work_rv.setAdapter(adapter);
		adapter.setRightButtonClick( position -> {
			currentHouseP = position;
			if (datas.get(position).getStaffs()==null){
				toast("该房源没有经纪人");
				return;
			}
			ArrayList<StaffComment> staffComments = new ArrayList<>(datas.get(position).getStaffs());
			addFragment(AdviserListFragment.getInstance(staffComments, datas.get(position).getSelectedP()));
		});
	}

	@OnClick(R.id.look_work_commit)
	public void lookCommit(){

		String userName = look_work_user.getText().toString().trim();
		if (TextUtils.isEmpty(userName)){
			toast("请填写您的称呼");
			return;
		}

		String phone = look_work_phone.getText().toString().trim();
		if (!MyUtils.checkPhoneNumber(phone)){
			toast("手机号码不能为空");
			return;
		}

		List<StaffNoRequest> staffParam = new ArrayList<>();
		for (LookAboutListDetailBo item : datas){
			StaffNoRequest request = new StaffNoRequest();
			request.setListID(item.getListID());
			request.setPostID(item.getPostID());
			request.setStaffNo(item.getStaffs().get(item.getSelectedP()).getStaffNo());
			request.setPostType(item.getPostType());
			staffParam.add(request);
		}

		OrderRequest orderRequest = new OrderRequest(userName, phone);

		SendAppointmentRequest request = new SendAppointmentRequest(DataHolder.getInstance().getUserId(), staffParam, orderRequest);

		mPresenter.commitLookAboutList(request);
		EventBus.getDefault().post(new NormalEvent());
	}

	@Override
	protected LookWorkPresenter createPresenter() {
		return new LookWorkPresenter();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (!subscription.isUnsubscribed()) {
			subscription.unsubscribe();
		}
	}

	@Override
	public void setCommitLookResult(boolean result) {
		if (result){
			toast("约看单提交成功");
			removeFragment();
			addFragment(ToLookAboutFragment.getInstance(LookAbout.LOOK_TYPE_TO));
		}else {
			toast("约看单提交失败");
		}
	}

	@OnClick(R.id.look_work_phone)
	public void changePhone(){
		Intent intent = new Intent(getActivity(), ExchangePhoneActivity.class);
		intent.putExtra(ExchangePhoneActivity.CALL_TYPE, ExchangePhoneActivity.CHECK);
		startActivityForResult(intent, 103);
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
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode==EntrustFragment.RESULT_CODE_PHONE){
			look_work_phone.setText(data.getStringExtra(ExchangePhoneActivity.PHONE_DATA_KEY));
		}
	}
}
