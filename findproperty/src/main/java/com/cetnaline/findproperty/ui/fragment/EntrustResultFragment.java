package com.cetnaline.findproperty.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.bean.MyEntrustBo;
import com.cetnaline.findproperty.base.BaseFragment;
import com.cetnaline.findproperty.entity.bean.StaffListBean;
import com.cetnaline.findproperty.api.NetContents;
import com.cetnaline.findproperty.presenter.impl.EntrustResultPresenter;
import com.cetnaline.findproperty.presenter.ui.EntrustResultContract;
import com.cetnaline.findproperty.ui.activity.AdviserDetailActivity;
import com.cetnaline.findproperty.ui.activity.EntrustActivity;
import com.cetnaline.findproperty.utils.DataHolder;
import com.cetnaline.findproperty.utils.MyUtils;
import com.cetnaline.findproperty.utils.glide.GlideLoad;
import com.cetnaline.findproperty.widgets.CircleImageView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by fanxl2 on 2016/8/19.
 */
public class EntrustResultFragment extends BaseFragment<EntrustResultPresenter> implements EntrustResultContract.View {

	public static EntrustResultFragment getInstance(long entrustID){
		EntrustResultFragment fragment = new EntrustResultFragment();
		Bundle bundle = new Bundle();
		bundle.putString("entrustID", entrustID+"");
		fragment.setArguments(bundle);
		return fragment;
	}

	@BindView(R.id.entrust_bt_cancel)
	TextView entrust_bt_cancel;

	private DrawableRequestBuilder<String> requestBuilder;

	@Override
	protected int getLayoutId() {
		return R.layout.frag_entrust_result;
	}

	@Override
	protected void init() {

		requestBuilder = GlideLoad.init(this);

		String entrustID = getArguments().getString("entrustID");
		mPresenter.getMyEntrustById(DataHolder.getInstance().getUserId(), entrustID);

		entrust_bt_cancel.setVisibility(View.INVISIBLE);
		((EntrustActivity)getActivity()).setCenterTitle("委托成功");
		((EntrustActivity)getActivity()).hideBack();
	}

	@Override
	protected EntrustResultPresenter createPresenter() {
		return new EntrustResultPresenter();
	}

	@OnClick(R.id.entrust_result_done)
	public void entrustDone(){
		removeFragment();
		addFragment(MyEntrustFragment.getInstance(1));
	}

	@BindView(R.id.entrust_item_name)
	TextView entrust_item_name;

	@BindView(R.id.entrust_item_tag)
	TextView entrust_item_tag;

	@BindView(R.id.entrust_item_info)
	TextView entrust_item_info;

	@BindView(R.id.entrust_item_type)
	TextView entrust_item_type;

	@BindView(R.id.entrust_item_price)
	TextView entrust_item_price;

	@BindView(R.id.entrust_user_name)
	TextView entrust_user_name;

//	@BindView(R.id.entrust_item_gscope)
//	TextView entrust_item_gscope;

	@BindView(R.id.entrust_item_head)
	CircleImageView entrust_item_head;

	private MyEntrustBo item;

	@Override
	public void setMyEntrust(MyEntrustBo item) {
		this.item = item;

		entrust_item_name.setText(item.getEstateName());
		entrust_item_tag.setText(item.getEntrustType());

		entrust_item_info.setText(item.getRegionName()+" "+item.getGscpoeName()+" | "+item.getAddress());

		int priceFrom = MyUtils.format2Integer(item.getPriceFrom());
		int priceTo = MyUtils.format2Integer(item.getPriceTo());
		if (item.getEntrustType().equals("出售")){
			entrust_item_type.setText("期望售价");
			if (priceFrom==0){
				entrust_item_price.setText("暂无");
			}else if (priceFrom==priceTo){
				entrust_item_price.setText(priceFrom/10000+"万");
			}else {
				entrust_item_price.setText(priceFrom/10000+"万——"+priceTo/10000+"万");
			}
		}else {
			entrust_item_type.setText("期望租金");
			if (priceFrom==0){
				entrust_item_price.setText("暂无");
			}else if (priceFrom==priceTo){
				entrust_item_price.setText(priceFrom+"元/月");
			}else {
				entrust_item_price.setText(priceFrom+"元/月——"+priceTo+"元/月");
			}
		}
		entrust_user_name.setText(item.getStaffName());
//		entrust_item_gscope.setText(item.getRegionName()+" "+item.getGscpoeName()+" | "+item.getAddress());

		GlideLoad.load(new GlideLoad.Builder(requestBuilder, NetContents.STAFF_HEAD_HOST + item.getStaffNo()+".jpg")
				.into(entrust_item_head));

	}

	@OnClick(R.id.entrust_item_msg)
	public void msgClick(){
		mPresenter.toMsg(getActivity(), item.getStaffName(), item.getStaffNo(), item.getEstateName());
	}

	@OnClick(R.id.entrust_item_call)
	public void callClick(){
		MyUtils.toCall400(getActivity(), item.getJJR400(), item.getStaffName());
	}

	@OnClick(R.id.entrust_item_head)
	public void headClick(){
		Intent intent = new Intent(getActivity(), AdviserDetailActivity.class);
		StaffListBean staffBean = new StaffListBean();
		staffBean.StaffNo = item.getStaffNo();
		staffBean.CnName = item.getStaffName();
		intent.putExtra(AdviserDetailActivity.ADVISER, staffBean);
		startActivity(intent);
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
