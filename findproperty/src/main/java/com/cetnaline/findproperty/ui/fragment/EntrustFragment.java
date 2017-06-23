package com.cetnaline.findproperty.ui.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.bean.StaffDetailBo;
import com.cetnaline.findproperty.base.BaseFragment;
import com.cetnaline.findproperty.entity.ui.TabEntity;
import com.cetnaline.findproperty.api.NetContents;
import com.cetnaline.findproperty.presenter.impl.EntrustInsertPresenter;
import com.cetnaline.findproperty.presenter.ui.EntrustInsertContract;
import com.cetnaline.findproperty.ui.activity.EntrustActivity;
import com.cetnaline.findproperty.ui.activity.ExchangePhoneActivity;
import com.cetnaline.findproperty.ui.activity.SearchActivity;
import com.cetnaline.findproperty.ui.activity.WebActivity;
import com.cetnaline.findproperty.utils.DataHolder;
import com.cetnaline.findproperty.utils.MyUtils;
import com.cetnaline.findproperty.widgets.tablayout.CommonTabLayout;
import com.cetnaline.findproperty.widgets.tablayout.listener.CustomTabEntity;
import com.cetnaline.findproperty.widgets.tablayout.listener.OnTabSelectListener;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;

import static com.cetnaline.findproperty.ui.activity.SearchActivity.SEARCH_TYPE_ESTATE;

/**
 * 委托 —— 出售 和 卖房
 * Created by fanxl2 on 2016/8/8.
 */
@Deprecated
public class EntrustFragment extends BaseFragment<EntrustInsertPresenter> implements EntrustInsertContract.View {

	public static final String ENTRUST_DETAIL_TYPE = "ENTRUST_DETAIL_TYPE";
	private int fromType;

	private String[] type_arr = new String[]{"一室", "两室", "三室", "四室", "五室", "五室以上"};

	public static EntrustFragment getInstance(int type, int fromType){
		EntrustFragment fragment = new EntrustFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(ENTRUST_DETAIL_TYPE, type);
		bundle.putInt("fromType", fromType);
		fragment.setArguments(bundle);
		return fragment;
	}

	@BindView(R.id.entrust_tv_est)
	TextView entrust_tv_est;

	@BindView(R.id.entrust_tv_room)
	TextView entrust_tv_room;

	@BindView(R.id.entrust_tv_person)
	EditText entrust_tv_person;

	@BindView(R.id.entrust_tv_phone)
	TextView entrust_tv_phone;

	@BindView(R.id.entrust_price_min)
	EditText entrust_price_min;

	@BindView(R.id.entrust_price_tips)
	TextView entrust_price_tips;

	@BindView(R.id.entrust_tv_one)
	TextView entrust_tv_one;

	@BindView(R.id.entrust_tv_information)
	EditText entrust_tv_information;

	@BindView(R.id.entrust_tv_commit)
	Button entrust_tv_commit;

	@BindView(R.id.tab_bar)
	CommonTabLayout tab_bar;

	@BindString(R.string.entrustSale)
	String entrustSale;

	@BindString(R.string.entrustRent)
	String entrustRent;

	@BindView(R.id.entrust_left_title)
	TextView entrust_left_title;

	@BindView(R.id.entrust_agreement_cb)
	CheckBox entrust_agreement_cb;

	private ArrayList<CustomTabEntity> mTabEntities;
	private int entrustType;

	private Map<String, String> param;

	@Override
	protected int getLayoutId() {
		return R.layout.frag_entrust;
	}

	@Override
	protected void init() {

		param = new HashMap<>();
		entrustType = getArguments().getInt(ENTRUST_DETAIL_TYPE, 0);
		fromType = getArguments().getInt("fromType", 0);

		mTabEntities = new ArrayList(){
			{
				add(new TabEntity(entrustSale,R.drawable.ic_360_icon,R.drawable.ic_360_icon));
				add(new TabEntity(entrustRent,R.drawable.ic_360_icon,R.drawable.ic_360_icon));
			}
		};

		tab_bar.setTabData(mTabEntities);
		tab_bar.setOnTabSelectListener(new OnTabSelectListener() {
			@Override
			public void onTabSelect(int position) {
				tabSelect(position);
			}

			@Override
			public void onTabReselect(int position) {

			}
		});

		String phone = DataHolder.getInstance().getUserPhone();
		if (!TextUtils.isEmpty(phone)){
			entrust_tv_phone.setText(phone);
		}

		tab_bar.setCurrentTab(entrustType);
		tabSelect(entrustType);

		entrust_tv_person.clearFocus();

		RxTextView.textChanges(entrust_price_min)
				.debounce(400, TimeUnit.MILLISECONDS)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(charSequence -> {

					if (entrustType==0){
						String keywordInput = charSequence.toString();
						if (!TextUtils.isEmpty(keywordInput)){
							entrust_price_tips.setText("万元");
						}
					}
				});
	}

	@Override
	protected EntrustInsertPresenter createPresenter() {
		return new EntrustInsertPresenter();
	}

	private void tabSelect(int position){
		entrustType = position;
		entrust_price_min.setText("");
		if (position==0){
			entrust_left_title.setText("期望售价");
			entrust_tv_one.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_calculator_red, 0, 0, 0);
			entrust_tv_one.setText("估价");
			entrust_price_min.setHint("您希望卖多少钱(选填)");
		}else {
			entrust_left_title.setText("期望租金");
			entrust_tv_one.setText("元/月");
			entrust_tv_one.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			entrust_price_min.setHint("您希望租多少钱(选填)");
			entrust_price_tips.setText("");
		}
	}

	@OnClick(R.id.entrust_tv_one)
	public void priceGu(){
		Intent intent = new Intent(getActivity(), WebActivity.class);
		intent.putExtra(WebActivity.TARGET_URL, NetContents.ENTRUST_PRICE);
		intent.putExtra(WebActivity.TITLE_HIDDEN_KEY, true);
		startActivity(intent);
	}

	@OnClick(R.id.entrust_tv_commit)
	public void entrustCommit(){
		String phone = entrust_tv_phone.getText().toString().trim();
		if (!MyUtils.checkPhoneNumber(phone)){
			toast("请输入手机号");
			return;
		}

		if (param.get("EstateName")==null || param.get("EstateCode")==null){
			toast("请选择房源所在小区");
			return;
		}

		if (!entrust_agreement_cb.isChecked()){
			toast("请勾选租售委托协议");
			return;
		}

		double minPrice = 0;
		if (!TextUtils.isEmpty(entrust_price_min.getText().toString())){
			minPrice = Double.parseDouble(entrust_price_min.getText().toString());
		}

		if (entrustType == EntrustActivity.ENTRUST_TYPE_SALE){
			param.put("EntrustType", "出售");
			param.put("source", "二手房");
			if (minPrice>0){
				param.put("PriceFrom", String.format("%.2f", minPrice*10000));
				param.put("PriceTo", param.get("PriceFrom"));
			}
		}else {
			param.put("EntrustType", "出租");
			param.put("source", "租房");
			if (minPrice>0){
				param.put("PriceFrom", minPrice+"");
				param.put("PriceTo", param.get("PriceFrom"));
			}
		}

		param.put("StaffName", staffName);
		param.put("StaffNo", staffNo);
		param.put("staffMobile", staffMobile);
		param.put("Status", "待处理");
		param.put("CityCode", "021");
		param.put("EntrusWay", "线上");
		param.put("CustomerSex", DataHolder.getInstance().getUserSex());
		param.put("appname", "APP");

		param.put("RoomCnt", roomCount+"");
		param.put("UserId", DataHolder.getInstance().getUserId());
		param.put("CustomerMobile", entrust_tv_phone.getText().toString());
		param.put("CustomerName", entrust_tv_person.getText().toString());
		if (!TextUtils.isEmpty(entrust_tv_information.getText().toString())){
			param.put("Remark", entrust_tv_information.getText().toString());
		}
//		mPresenter.insertEntrustInfo(param);
	}


	@Override
	public void showMsg(String msg) {
		toast(msg);
	}

	@Override
	public void insertResult(long entrustID) {

		if (entrustID>0){
			toast("委托成功");
			addFragmentNoBack(EntrustResultFragment.getInstance(entrustID));
		}else {
			toast("委托失败, 请重试!");
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		((EntrustActivity)getActivity()).setToolBarTitle("新增委托");
	}

	private String staffName, staffNo, staffMobile;

	@Override
	public void setStaff(StaffDetailBo staff) {
		staffName = staff.getCnName();
		staffNo = staff.getStaffNo();
		staffMobile = staff.getMobile();
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

//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setHasOptionsMenu(true);
//	}



	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.entrust_insert_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId()==R.id.entrust_my_entrust){
			if (fromType==0){
				addFragment(MyEntrustFragment.getInstance(1));
			}else {
				((EntrustActivity)getActivity()).toBack();
			}
		}
		return super.onOptionsItemSelected(item);
	}

	private int roomCount;

	@OnClick(R.id.entrust_tv_room)
	public void roomClick(){
		new AlertDialog.Builder(getContext()).setItems(type_arr,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						entrust_tv_room.setText(type_arr[which]);
						roomCount = (which+1);
					}
				}).show();
	}

	@OnClick(R.id.entrust_tv_phone)
	public void toGetPhone(){
		Intent intent = new Intent(getActivity(), ExchangePhoneActivity.class);
		intent.putExtra(ExchangePhoneActivity.CALL_TYPE, ExchangePhoneActivity.CHECK);
		startActivityForResult(intent, 103);
	}

	@OnClick(R.id.entrust_tv_est)
	public void toGetEstate(){
		Intent intent = new Intent(getActivity(), SearchActivity.class);
		intent.putExtra(SearchActivity.SEARCH_TYPE_KEY, SEARCH_TYPE_ESTATE);
		intent.putExtra(SearchActivity.IS_GET_DATA, true);
		startActivityForResult(intent, 104);
	}

	@OnClick(R.id.entrust_tv_agreement)
	public void agreementClick(){
		Intent intent = new Intent(getActivity(), WebActivity.class);
		intent.putExtra(WebActivity.TARGET_URL, "http://sh.centanet.com/m/page/entrust/CentalineSaleRentAgreement.html");
		startActivity(intent);
	}

	public static final int RESULT_CODE_PHONE = 104;
	public static final int RESULT_CODE_ESTATE = 105;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode==RESULT_CODE_PHONE){
			entrust_tv_phone.setText(data.getStringExtra(ExchangePhoneActivity.PHONE_DATA_KEY));
		}else if (resultCode==RESULT_CODE_ESTATE){
			param.put("EstateName", data.getStringExtra("EstateName"));
			param.put("EstateCode", data.getStringExtra("EstateCode"));
			entrust_tv_est.setText(data.getStringExtra("EstateName"));
			mPresenter.getStaff(data.getStringExtra("EstateCode"));
		}
	}
}
