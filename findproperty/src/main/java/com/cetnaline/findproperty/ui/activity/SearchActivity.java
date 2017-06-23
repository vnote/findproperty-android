package com.cetnaline.findproperty.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.bean.EstateBo;
import com.cetnaline.findproperty.api.bean.SearchParam;
import com.cetnaline.findproperty.api.bean.SeoHotModelResponse;
import com.cetnaline.findproperty.api.bean.TagModelResponse;
import com.cetnaline.findproperty.base.BaseActivity;
import com.cetnaline.findproperty.db.entity.HistoryHouseTag;
import com.cetnaline.findproperty.api.NetContents;
import com.cetnaline.findproperty.presenter.impl.SearchPresenter;
import com.cetnaline.findproperty.presenter.ui.SearchContract;
import com.cetnaline.findproperty.ui.adapter.CommonAdapter;
import com.cetnaline.findproperty.ui.adapter.ViewHolder;
import com.cetnaline.findproperty.ui.fragment.DeputeSourceInfoFragment;
import com.cetnaline.findproperty.ui.fragment.EntrustFragment;
import com.cetnaline.findproperty.ui.fragment.MapFragment;
import com.cetnaline.findproperty.ui.fragment.VillageDetailFragment;
import com.cetnaline.findproperty.utils.DataHolder;
import com.cetnaline.findproperty.utils.DbUtil;
import com.cetnaline.findproperty.utils.MyUtils;
import com.cetnaline.findproperty.utils.StatusBarCompat;
import com.cetnaline.findproperty.widgets.FlowTag;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;

/**房屋搜索
 * Created by fanxl2 on 2016/10/17.
 */

public class SearchActivity extends BaseActivity<SearchPresenter> implements SearchContract.View {

	private int searchType;

	public static final int ESTATE_SELECT_REQUEST = 200;

	public static final String SEARCH_TYPE_KEY = "SEARCH_TYPE_KEY";
	public static final String IS_GET_DATA = "IS_GET_DATA";
	public static final String IS_PUBLISH_SOURCE = "IS_PUBLISH_SOURCE";   //标识房源委托进入搜索页

	public static final int SEARCH_TYPE_SECOND = 0;
	public static final int SEARCH_TYPE_ESTATE = 3;

	public static final String TAG_CATEGORY_ESTATE = "estate";
	public static final String TAG_CATEGORY_REGION = "region";
	public static final String TAG_CATEGORY_BLOCK = "block";
	public static final String TAG_CATEGORY_SEARCH = "search";
	public static final String TAG_CATEGORY_SCHOOL = "school";

	private Dialog dialog;

	private boolean isGetData;
	private boolean isPublishSource;

//	private String[] category = {"二手房", "新房", "租房", "小区"};
//	private String[] category = {"二手房", "租房", "小区"};

	@BindView(R.id.search_tv_type)
	TextView search_tv_type;

	@BindView(R.id.search_iv_triangle)
	ImageView search_iv_triangle;

	@BindView(R.id.search_et)
	EditText search_et;

	//热门搜索
	@BindView(R.id.tag_cloud_view_hot)
	FlowTag tag_cloud_view_hot;

	private List<HistoryHouseTag> historyTags;

	private String userId;

	private LayoutInflater inflater;

	private String keywordInput;

	@BindView(R.id.search_lv_autocomplete)
	ListView search_lv_autocomplete;

	@BindView(R.id.ll_search_history)
	LinearLayout ll_search_history;

	@BindView(R.id.search_ly_type)
	LinearLayout search_ly_type;

	@BindView(R.id.ll_search_hot)
	LinearLayout ll_search_hot;

	private SearchTagAdapter adapter;

	@OnClick(R.id.search_ly_type)
	public void dropSearchClick(){
		if (dialog.isShowing()) {
			dialog.dismiss();
		} else {
			dialog.show();
			dialog.getWindow().setBackgroundDrawableResource(R.drawable.search_type_bg);
			RotateAnimation anim = new RotateAnimation(180, 180, search_iv_triangle.getWidth() / 2, search_iv_triangle.getHeight() / 2);
			anim.setFillAfter(true);
			anim.setDuration(400);
			search_iv_triangle.startAnimation(anim);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@BindView(R.id.search_iv_delete)
	ImageView search_iv_delete;

	@OnClick(R.id.search_iv_delete)
	public void deleteClick(){
		search_et.setText("");
	}

	//是否是搜索
	private boolean isToSearch;

	@OnClick(R.id.search_tv_cancel)
	public void cancelClick(){
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(search_et.getWindowToken(), 0);

		finish();
	}

	@Override
	protected int getContentViewId() {
		return R.layout.act_search;
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		searchType = getIntent().getIntExtra(SEARCH_TYPE_KEY, SEARCH_TYPE_SECOND);
		isGetData = getIntent().getBooleanExtra(IS_GET_DATA, false);
		isPublishSource = getIntent().getBooleanExtra(IS_PUBLISH_SOURCE, false);
		if (searchType == SEARCH_TYPE_ESTATE) {
			search_et.setImeOptions(EditorInfo.IME_ACTION_DONE);
		} else {
			search_et.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
		}

		if (isGetData){
//			search_tv_type.setText("小区");
			search_tv_type.setVisibility(View.GONE);
			search_iv_triangle.setVisibility(View.GONE);
			search_et.setHint("请输入小区名称");
			search_ly_type.setEnabled(false);
			ll_search_hot.setVisibility(View.GONE);
			tag_cloud_view_hot.setVisibility(View.GONE);
		}

		userId = DataHolder.getInstance().getUserId();

		inflater = LayoutInflater.from(this);

		LinearLayout pView = (LinearLayout) inflater.inflate(R.layout.layout_search_popup, null);
//		ListView lv = (ListView) pView.findViewById(R.id.search_lv_popup);
//		lv.setAdapter(new ArrayAdapter<>(this, R.layout.item_search_popup, category));
		dialog = new Dialog(this, R.style.FullHeightDialog);
		dialog.setContentView(pView);
		Window win = dialog.getWindow();
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.width = MyUtils.dip2px(this, 100);
		lp.height = MyUtils.dip2px(this, 120);
		lp.gravity = Gravity.LEFT | Gravity.TOP;
		int[] loc = new int[2];
		search_tv_type.getLocationOnScreen(loc);
		lp.x = MyUtils.dip2px(this, 20);
		lp.y = loc[1] + MyUtils.dip2px(this, 60);
		win.setAttributes(lp);
		dialog.setOnDismissListener(dialogInterface -> {
			RotateAnimation anim = new RotateAnimation(-360, -360, search_iv_triangle.getWidth() / 2, search_iv_triangle.getHeight() / 2);
			anim.setFillAfter(true);
			anim.setDuration(400);
			search_iv_triangle.startAnimation(anim);
		});

		if (getTagCategory().equals(HOUSE_TYPE_COMMUNITY)) {
			search_et.setImeOptions(EditorInfo.IME_ACTION_NONE);
			search_et.setInputType(EditorInfo.TYPE_CLASS_TEXT);
			search_et.setSingleLine(true);
		}


		search_et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (v.getText() != null && v.getText().toString().length() > 0 && searchType == SEARCH_TYPE_ESTATE) {
					InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					inputMethodManager.hideSoftInputFromWindow(search_et.getWindowToken(), 0);
					toast("请点选小区");
				}

				if (isGetData)return false;

				if (actionId== EditorInfo.IME_ACTION_SEARCH ||
						(event!=null&&event.getKeyCode()== KeyEvent.KEYCODE_ENTER)){

					String searchStr = search_et.getText().toString().trim();
					if (TextUtils.isEmpty(searchStr)){
						toast("请输入搜索关键字");
						return true;
					}

					isToSearch = true;
					HistoryHouseTag tag = new HistoryHouseTag();
					tag.setTag(searchStr);
					tag.setTagCode(searchStr);
					tag.setTagCategory(TAG_CATEGORY_SEARCH);
//					tag.setTagPY();

					doSearchHotSeo(tag);
					return true;
				}
				return false;
			}
		});

		searchResults = new ArrayList<>();
		adapter = new SearchTagAdapter(this, searchResults, R.layout.item_search_autocomplete);
		search_lv_autocomplete.setAdapter(adapter);

		//如果是委托页选择小区则添加footview
		if (isPublishSource) {
			View footView = inflater.inflate(R.layout.store_search_footer,null);
			search_lv_autocomplete.addFooterView(footView);
		}

		search_lv_autocomplete.setOnItemClickListener((parent, view, position, id) -> {

			if (isPublishSource && position >=adapter.getCount()) {
				//打开门店选择页面
//				Intent intent = new Intent(SearchActivity.this, StoreSearchActivity.class);
//				intent.putExtra(StoreSearchActivity.IS_PUBLISH_SELECT, true);
//				startActivityForResult(intent,STORE_SELECT_REQUEST);

				//打开地图页点选小区
				Intent intent = new Intent(SearchActivity.this, EstateMapSelectActivity.class);
				startActivityForResult(intent,DeputeSourceInfoFragment.LOCATION_REQUEST_CODE);
				return;
			}

            TagModelResponse item = searchResults.get(position);
            if (isGetData) {
				Intent intent = new Intent();
				intent.putExtra("EstateName", item.getTag());
				if (isPublishSource) {
					intent.putExtra("EstateId", item.getTagCode());
					intent.putExtra("EstateAddress", item.getAddress());
					intent.putExtra("EstateLat",item.getLat());
					intent.putExtra("EstateLng",item.getLng());
					intent.putExtra("GScopeId",item.getgScopeId());
					intent.putExtra("GScopeName",item.getgScopeName());
					intent.putExtra("EstateCode",item.getEstateCode());
					SearchActivity.this.setResult(DeputeSourceInfoFragment.VILLAGE_RESULT_CODE, intent);
				} else if (searchType == SEARCH_TYPE_ESTATE) {
					intent.putExtra("EstateCode", item.getTagCode());
                    SearchActivity.this.setResult(EntrustFragment.RESULT_CODE_ESTATE, intent);
                }
				finish();
            }else {
                HistoryHouseTag tag = new HistoryHouseTag();
                tag.setTag(item.getTag());
                tag.setEstateAvgPriceRent(item.getEstateAvgPriceRent());
                tag.setEstateAvgPriceSale(item.getEstateAvgPriceSale());
                tag.setPN1(item.getPN1());
                tag.setPN2(item.getPN2());
                tag.setRNum(item.getRNum());
                tag.setSNum(item.getSNum());
                tag.setTagCategory(item.getTagCategory());
                tag.setTagCode(item.getTagCode());
                tag.setTagPY(item.getTagPY());
                doSearchHotSeo(tag);
            }
        });

		for (int i=0;i<pView.getChildCount();i++) {
			if (pView.getChildAt(i) instanceof TextView) {
				int finalI = i;
				pView.getChildAt(i).setOnClickListener(v->{
					dialog.dismiss();
					search_tv_type.setText(((TextView) pView.getChildAt(finalI)).getText());
				});
			}
		}

		RxTextView.textChanges(search_et)
				.debounce(1000, TimeUnit.MILLISECONDS)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(charSequence -> {
					keywordInput = charSequence.toString();
					if (keywordInput != null && keywordInput.length() > 0) {
						String ent = keywordInput.substring(keywordInput.length() - 1);
						if (searchType == SEARCH_TYPE_ESTATE && ent.equals("\n")) {
							InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
							inputMethodManager.hideSoftInputFromWindow(search_et.getWindowToken(), 0);
							toast("请点选小区");
						}
					}

					if (TextUtils.isEmpty(charSequence)) {
						search_lv_autocomplete.setVisibility(View.GONE);
						search_iv_delete.setVisibility(View.INVISIBLE);
					} else {
						search_iv_delete.setVisibility(View.VISIBLE);
						if (isPublishSource) {
							mPresenter.getEstateList(charSequence.toString());
						} else {
							Map<String, String> params = new HashMap<>();
							params.put("Tag", charSequence.toString());
							params.put("TagCategory", getTagCategory());
							mPresenter.getSearchTag(params);
						}
					}
				});

		mPresenter.getSeoHotTag();

		initHistoryKeywords();

		search_et.requestFocus();
		search_et.postDelayed(new Runnable() {
			@Override
			public void run() {
				InputMethodManager keyboard = (InputMethodManager)
						getSystemService(Context.INPUT_METHOD_SERVICE);
				keyboard.showSoftInput(search_et, 0);
			}
		},200);
	}

	public static final String HOUSE_TYPE_COMMUNITY = "E";

//	@BindView(R.id.search_tv_no_history)
//	TextView search_tv_no_history;

	@BindView(R.id.btn_clean_history_keywords)
	TextView btn_clean_history_keywords;

	@BindView(R.id.tag_cloud_view_history)
	FlowTag tag_cloud_view_history;

	/**
	 * 初始化关键字本地历史记录
	 */
	private void initHistoryKeywords() {

		historyTags = new ArrayList<>();

		historyTags = DbUtil.getHistoryHouseTag(userId);

		if (HOUSE_TYPE_COMMUNITY.equals(getTagCategory())) {
			historyTags = DbUtil.getHistoryCommunityTag(userId);
		} else {
			historyTags = DbUtil.getHistoryHouseTag(userId);
		}

		addHistoryData();
	}

	private void addHistoryData() {
		tag_cloud_view_history.removeAllViews();
		for (HistoryHouseTag tag : historyTags) {
			createTag(tag.getTag(), tag_cloud_view_history, tag);
		}
		if (historyTags.size()>0 && !isGetData){
//			btn_clean_history_keywords.setVisibility(View.VISIBLE);
//			tag_cloud_view_history.setVisibility(View.VISIBLE);
			ll_search_history.setVisibility(View.VISIBLE);
		}else {
			ll_search_history.setVisibility(View.GONE);
//			btn_clean_history_keywords.setVisibility(View.GONE);
//			tag_cloud_view_history.setVisibility(View.GONE);
		}
	}

	public String getTagCategory() {
		switch (search_tv_type.getText().toString()) {
			//E(小区)、S(二手房)、R(租房)、A(经纪人)、M(门店)、D(查成交)、N(新房)
			case "小区":
				return "E";
			case "二手房":
				return "S";
			case "租房":
				return "R";
			case "新房":
				return "N";
			default:
				return "S";
		}
	}

	@OnClick(R.id.btn_clean_history_keywords)
	public void clearHistoryTag(){
		DbUtil.deleteHistoryHouseTagAll();
		initHistoryKeywords();
	}

	@Override
	protected SearchPresenter createPresenter() {
		return new SearchPresenter();
	}

	@Override
	protected void initToolbar() {
		showToolbar(false);
		StatusBarCompat.setStatusBarColor(this, Color.WHITE);
	}

	@Override
	public void setSeoHotTag(List<SeoHotModelResponse> hotTags) {
		tag_cloud_view_hot.removeAllViews();
		if (hotTags!=null && hotTags.size()>0){
			for (SeoHotModelResponse item : hotTags){

//				String url = item.getUrl();
//				String estateCode = url.substring(url.indexOf("xq-")+3, url.indexOf("-0"));

				HistoryHouseTag tag = new HistoryHouseTag();
				tag.setTag(item.getVal());
				tag.setTagCode(item.getVal());
				tag.setTagCategory(TAG_CATEGORY_SEARCH);

				createTag(item.getVal(), tag_cloud_view_hot, tag);
			}
		}else {
			ll_search_hot.setVisibility(View.GONE);
		}
	}

	private void createTag(String name, FlowTag parent, HistoryHouseTag tag){
		final TextView tv_tag = (TextView) inflater.inflate(R.layout.item_hot_tag, parent, false);
		tv_tag.setOnClickListener(v -> {
			doSearchHotSeo(tag);
		});
		tv_tag.setText(name);
		parent.addView(tv_tag);
	}

	private void doSearchHotSeo(HistoryHouseTag tag) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			imm.hideSoftInputFromWindow(search_et.getWindowToken(), 0);
		}

		search_et.setText("");
		if (userId!=null && !TextUtils.isEmpty(userId)){
			saveKeyword(tag);
		}
		gotoHouseList(tag);
	}

	private void gotoHouseList(HistoryHouseTag tag) {

		if ("E".equalsIgnoreCase(getTagCategory())){
			Intent estateIntent;
			if (isToSearch || tag.getTagCategory().equals(TAG_CATEGORY_SEARCH)){
				estateIntent = new Intent(this, EstateList.class);
				estateIntent.putExtra(EstateList.ESTATE_KEY_WORD, tag.getTagCode());
				estateIntent.putExtra(MapFragment.HOUSE_TYPE_KEY, MapFragment.HOUSE_TYPE_ESTATE);
			}else {
				estateIntent = new Intent(this, VillageDetail.class);
				estateIntent.putExtra(VillageDetailFragment.ESTATE_CODE_KEY, tag.getTagCode());
			}
			startActivity(estateIntent);
			isToSearch = false;

			mPresenter.wordFrequency("xiaoqu", tag.getTag());
			return;
		}

		HashMap<String, SearchParam> param = new HashMap<>();

		SearchParam estateParam = new SearchParam();
		estateParam.setPara(tag.getTagPY());
		estateParam.setText(tag.getTag());
		estateParam.setValue(tag.getTagCode());

		switch (tag.getTagCategory()+"") {
			case TAG_CATEGORY_BLOCK:
				estateParam.setTitle("板块");
				estateParam.setName(NetContents.GSCOPE_NAME);
				estateParam.setKey("GScopeId");
				break;
			case TAG_CATEGORY_REGION:
				estateParam.setTitle("区域");
				estateParam.setName(NetContents.REGION_NAME);
				estateParam.setKey("RegionId");
				break;
			case TAG_CATEGORY_SEARCH:
				estateParam.setTitle("搜索");
				estateParam.setName(NetContents.SEARCH_NAME);
				estateParam.setKey("Keywords");
				break;
			case TAG_CATEGORY_SCHOOL:
				estateParam.setTitle("学校");
				estateParam.setName(NetContents.SCHOOL_NAME);
				estateParam.setKey("SchoolId");
				break;
			default:
				estateParam.setTitle("小区");
				estateParam.setName(NetContents.ESTATE_NAME);
				estateParam.setKey("EstateCode");
				break;
		}

		int houseType;
		String housePin;

		if ("S".equalsIgnoreCase(getTagCategory())){
			estateParam.setTitle("二手房");
			houseType = MapFragment.HOUSE_TYPE_SECOND;
			housePin = "ershoufang";
		}else if ("R".equalsIgnoreCase(getTagCategory())){
			estateParam.setTitle("租房");
			houseType = MapFragment.HOUSE_TYPE_RENT;
			housePin = "zufang";
		}else {
			estateParam.setTitle("新房");
			houseType = MapFragment.HOUSE_TYPE_NEW;
			housePin = "xinfang";
		}

		//词频统计
		mPresenter.wordFrequency(housePin, tag.getTag());

		param.put(estateParam.getKey(), estateParam);

		Intent intent = new Intent(SearchActivity.this, HouseList.class);
		intent.putExtra(MapFragment.HOUSE_TYPE_KEY, houseType);
		intent.putExtra(HouseList.SEARCH_PARAM_KEY, param);
		startActivity(intent);
	}

	/**
	 * 保存搜索记录
	 */
	private void saveKeyword(HistoryHouseTag tag) {
		if (historyTags.size() == 15) {
			historyTags.remove(14);
			DbUtil.deleteHistoryHouseTag(historyTags.get(14));
		}
		for (int i=0; i<historyTags.size(); i++){
			HistoryHouseTag itemTag = historyTags.get(i);
			if (tag.getTag().equals(itemTag.getTag())){
				DbUtil.deleteHistoryHouseTag(itemTag);
				historyTags.remove(i);
			}
		}

		tag.setUserId(userId);
		tag.setHouseType(getTagCategory());
		if (DbUtil.saveHistoryHouseTag(tag)){
			historyTags.add(0, tag);
			addHistoryData();
		}
	}

	private List<TagModelResponse> searchResults;

	@Override
	public void setSearchTag(List<TagModelResponse> listTag) {
		searchResults = listTag;
		if (listTag!=null && listTag.size()>0){
			search_lv_autocomplete.setVisibility(View.VISIBLE);
			search_lv_autocomplete.setEmptyView(getLayoutInflater().inflate(R.layout.listview_no_data_layout,null));
			adapter.setData(listTag);
		}
	}

	@Override
	public void setEstateList(List<EstateBo> listTag) {
		searchResults.clear();
		if (listTag == null || listTag.size() == 0) {
			search_lv_autocomplete.setVisibility(View.VISIBLE);
			adapter.setData(new ArrayList());
			return;
		}
		for (EstateBo estateBo:listTag) {
			TagModelResponse tagModelResponse = new TagModelResponse();
			tagModelResponse.setAddress(estateBo.getEstateAddress());
			tagModelResponse.setTag(estateBo.getEstateName());
			tagModelResponse.setTagCategory(estateBo.getGscopeName());
			tagModelResponse.setTagCode(estateBo.getEstateID());
			tagModelResponse.setLat(estateBo.getLat());
			tagModelResponse.setLng(estateBo.getLng());
			tagModelResponse.setgScopeId(estateBo.getGscopeId()+"");
			tagModelResponse.setgScopeName(estateBo.getGscopeName());
			tagModelResponse.setEstateCode(estateBo.getCestCode());
			searchResults.add(tagModelResponse);
		}
		if (searchResults!=null && searchResults.size()>0){
			search_lv_autocomplete.setVisibility(View.VISIBLE);
			adapter.setData(searchResults);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == DeputeSourceInfoFragment.LOCATION_REQUEST_CODE && data != null) {
			Intent intent = new Intent();
			intent.putExtra("lat", data.getDoubleExtra("lat",-1));
			intent.putExtra("lng", data.getDoubleExtra("lng",-1));
			intent.putExtra("name", data.getStringExtra("name"));
			intent.putExtra("address", data.getStringExtra("address"));
			setResult(DeputeSourceInfoFragment.LOCATION_RESULT_CODE, intent);
			finish();
		}
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
		Logger.e(msg);
	}

	public class SearchTagAdapter extends CommonAdapter<TagModelResponse>{

		public SearchTagAdapter(Context context, List<TagModelResponse> mDatas, int itemLayoutId) {
			super(context, mDatas, itemLayoutId);
		}

		@Override
		public void convert(ViewHolder helper, TagModelResponse item) {
			String str = item.getTag();
			if (str.contains(keywordInput)){
				String red = "<font color='red'>" + keywordInput + "</font>";
				str = str.replace(keywordInput, red);
			}
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				((TextView)helper.getConvertView()).setText(Html.fromHtml(str, Html.FROM_HTML_MODE_COMPACT));
			}else {
				((TextView)helper.getConvertView()).setText(Html.fromHtml(str));
			}
		}
	}

	@Override
	protected String getTalkingDataPageName() {
		return "搜索页";
	}
}
