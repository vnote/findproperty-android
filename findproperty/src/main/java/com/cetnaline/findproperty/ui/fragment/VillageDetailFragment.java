package com.cetnaline.findproperty.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.cetnaline.findproperty.BaseApplication;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.bean.EstateBo;
import com.cetnaline.findproperty.api.bean.EstateImage;
import com.cetnaline.findproperty.api.bean.ImageBean;
import com.cetnaline.findproperty.api.bean.PriceTrendBean;
import com.cetnaline.findproperty.api.bean.SearchParam;
import com.cetnaline.findproperty.base.BaseFragment;
import com.cetnaline.findproperty.entity.event.ShareEvent;
import com.cetnaline.findproperty.api.NetContents;
import com.cetnaline.findproperty.presenter.impl.VillageDetailPresenter;
import com.cetnaline.findproperty.presenter.ui.VillageDetailContract;
import com.cetnaline.findproperty.ui.activity.HouseList;
import com.cetnaline.findproperty.ui.activity.ImageBrowseActivity;
import com.cetnaline.findproperty.ui.activity.LoginActivity;
import com.cetnaline.findproperty.ui.activity.NearbyActivity;
import com.cetnaline.findproperty.ui.activity.PanoramaActivity;
import com.cetnaline.findproperty.ui.activity.VillageDetail;
import com.cetnaline.findproperty.utils.DataHolder;
import com.cetnaline.findproperty.utils.DateUtil;
import com.cetnaline.findproperty.utils.MyUtils;
import com.cetnaline.findproperty.utils.RxBus;
import com.cetnaline.findproperty.utils.glide.GlideLoad;
import com.cetnaline.findproperty.widgets.DetailImgLayout;
import com.cetnaline.findproperty.widgets.MyText;
import com.cetnaline.findproperty.widgets.chart.SaleLineChart2;
import com.cetnaline.findproperty.widgets.sharedialog.ShareDialog;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;

import static com.cetnaline.findproperty.R.id.detail_tv_street;

/**
 * Created by fanxl2 on 2016/8/2.
 */
public class VillageDetailFragment extends BaseFragment<VillageDetailPresenter> implements VillageDetailContract.View {

	public static final String ESTATE_CODE_KEY = "ESTATE_CODE_KEY";
	public static final String ESTATE_NAME_KEY = "ESTATE_NAME_KEY";

	private boolean collectionExchange = false;

	public static VillageDetailFragment getInstance(String estateCode){
		VillageDetailFragment village = new VillageDetailFragment();
		Bundle bundle = new Bundle();
		bundle.putString(ESTATE_CODE_KEY, estateCode);
		village.setArguments(bundle);
		return village;
	}

	@BindView(R.id.toolbar)
	Toolbar toolbar;

	@BindView(R.id.est_detail_title)
	TextView est_detail_title;

	@BindView(R.id.est_saleing_count)
	MyText est_saleing_count;

	@BindView(R.id.est_sale_count)
	MyText est_sale_count;

	@BindView(R.id.est_ll_price)
	LinearLayout est_ll_price;

	@BindView(R.id.est_av_price)
	TextView est_av_price;

	@BindView(R.id.est_ll_type)
	LinearLayout est_ll_type;

	@BindView(R.id.est_tv_type)
	TextView est_tv_type;

	@BindView(R.id.est_ll_ratio)
	LinearLayout est_ll_ratio;

	@BindView(R.id.est_tv_ratio)
	TextView est_tv_ratio;

	@BindView(R.id.est_ll_green)
	LinearLayout est_ll_green;

	@BindView(R.id.est_tv_green)
	TextView est_tv_green;

	@BindView(R.id.est_ll_place)
	LinearLayout est_ll_place;

	@BindView(R.id.est_car_place)
	TextView est_car_place;

	@BindView(R.id.est_ll_year)
	LinearLayout est_ll_year;

	@BindView(R.id.est_build_year)
	TextView est_build_year;

	@BindView(R.id.est_ll_proprice)
	LinearLayout est_ll_proprice;

	@BindView(R.id.est_property_price)
	TextView est_property_price;

	@BindView(R.id.est_ll_company)
	LinearLayout est_ll_company;

	@BindView(R.id.est_property_company)
	TextView est_property_company;

	@BindView(R.id.est_ll_developer)
	LinearLayout est_ll_developer;

	@BindView(R.id.est_tv_developer)
	TextView est_tv_developer;

	@BindView(R.id.detail_dl_imgs)
	DetailImgLayout detail_dl_imgs;

	@BindView(R.id.atv_map_name)
	AppCompatTextView atv_map_name;

	@BindView(R.id.detail_house_map)
	ImageView detail_house_map;

	@BindView(R.id.detail_rl_map)
	RelativeLayout detail_rl_map;

	@BindView(R.id.detail_bt_nearby)
	Button detail_bt_nearby;

//	@BindView(R.id.detail_fb_like)
//	FloatingActionButton detail_fb_like;

	@BindView(R.id.detail_sale_lc2)
	SaleLineChart2 detail_sale_lc2;

	@BindView(R.id.appbar)
	AppBarLayout home_detail_bar;

	@BindView(R.id.center_title)
	TextView center_title;

	private boolean isBlackIcon;

	private MenuItem menu_like, menu_share;

	private DrawableRequestBuilder<String> requestBuilder;

	@Override
	protected int getLayoutId() {
		return R.layout.frag_village_detail;
	}

	@OnClick(R.id.detail_bt_nearby)
	public void nearByClick(){
		toNearByMap();
	}

	private void toNearByMap() {
		Intent intent = new Intent(getActivity(), NearbyActivity.class);
		intent.putExtra(NearbyFragment.LATITUDE_KEY, latitude);
		intent.putExtra(NearbyFragment.LONGITUDE_KEY, longitude);
		startActivity(intent);
	}

	@OnClick(R.id.detail_rl_map)
	public void toMap(){
		toNearByMap();
	}

	private Subscription rxSubscription;
	private ArrayList<ImageBean> imageBrowseList = new ArrayList<>();

	@Override
	protected void init() {

		toolbar.setTitle("");
		((VillageDetail)getActivity()).showToolbar(false);
		((VillageDetail)getActivity()).setToolbar(toolbar);

		rxSubscription = RxBus.getDefault().toObservable(ShareEvent.class)
				.subscribe(shareEvent -> {
					switch (shareEvent.getEventType()){
						case ShareEvent.EVENT_TYPE_SUCCESS:
							shareDialog.dismiss();
							toast("分享成功");
							break;
						case ShareEvent.EVENT_TYPE_CANCLE:
							toast("分享被取消");
							break;
						case ShareEvent.EVENT_TYPE_FAIL:
							toast("分享失败");
							break;
					}
				});

		home_detail_bar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
			@Override
			public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

				if (verticalOffset>-MyUtils.px2dip(BaseApplication.getContext(),350) && isBlackIcon){
					toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
					if (menu_like!=null){
						if (collectId>0){
						}else {
							menu_like.setIcon(R.drawable.ic_like_menu_white);
						}
						menu_share.setIcon(R.drawable.ic_share_menu_white);
					}
					isBlackIcon = false;
					center_title.setText("");
				}else if (verticalOffset <=-MyUtils.px2dip(BaseApplication.getContext(),350) && !isBlackIcon){
					toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
					if (menu_like!=null){
						if (collectId>0){
						}else {
							menu_like.setIcon(R.drawable.ic_like_menu_black);
						}
						menu_share.setIcon(R.drawable.ic_share_menu_black);
					}
					center_title.setText(estateName);
					isBlackIcon = true;
				}
			}
		});

		estateCode = getArguments().getString(ESTATE_CODE_KEY);
		Map<String, String> params = new HashMap<>();
		params.put("EstateCode", estateCode);
		params.put("ImageWidth", NetContents.IMAGE_BIG_WIDTH+"");
		params.put("ImageHeight", NetContents.IMAGE_BIG_HEIGHT+"");

		mPresenter.getVillageDetail(params);

		requestBuilder = GlideLoad.init(this);

		requestBuilder.error(R.drawable.ic_est_not_found);
		requestBuilder.placeholder(R.drawable.ic_est_not_found);
		requestBuilder.centerCrop();

		detail_dl_imgs.setRequestBuilder(requestBuilder);
		detail_dl_imgs.setImgViewPagerClickListener(position -> {
			Intent intent = new Intent(getActivity(), ImageBrowseActivity.class);
			intent.putExtra(ImageBrowseActivity.DEFAULT_POSITION_KEY, position);
			intent.putParcelableArrayListExtra(ImageBrowseActivity.IMAGE_DATA_KEY, imageBrowseList);
			startActivityForResult(intent, 103);
		});

	}

	@Override
	protected VillageDetailPresenter createPresenter() {
		return new VillageDetailPresenter();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.village_detail_menu, menu);
		menu_like = menu.findItem(R.id.menu_ic_like);
		menu_share = menu.findItem(R.id.menu_ic_share);
		super.onCreateOptionsMenu(menu, inflater);

		if (DataHolder.getInstance().isUserLogin()){

			Logger.e("estateCode:"+estateCode+"---userId:"+DataHolder.getInstance().getUserId());

			mPresenter.checkCollect(DataHolder.getInstance().getUserId(), estateCode);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId()==R.id.menu_ic_like){
			collectClick();
		}else if (item.getItemId()==R.id.menu_ic_share){
			shareClick();
		}
		return super.onOptionsItemSelected(item);
	}

	private double latitude;
	private double longitude;

	private int saleNumber, rentNumber;

	private String gscopeName;

	@BindView(R.id.detail_ll_trend)
	LinearLayout detail_ll_trend;

	@Override
	public void setPriceTrend(PriceTrendBean priceTrend) {
		detail_sale_lc2.setEstatePriceBo(estateName, gscopeName, priceTrend.getEstateDealPriceBos(), priceTrend.getGscopeDealPriceBos(),priceTrend.getCityDealPriceBos());
	}

	@Override
	public void setVillageDetail(EstateBo detail) {

		shareTitle = detail.getEstateName();
		shareSumtitle = detail.getAddress()+"\n小区均价 "+MyUtils.format2(detail.getSaleAvgPrice())+"元/㎡";
		shareUrl = NetContents.SHARE_HOUSE_HOST + "xiaoqu/xq-"+detail.getEstateCode()+"/";

		GlideLoad.load(new GlideLoad.Builder(requestBuilder, MyUtils.getBaiduMapImgUrl(detail.getLng(), detail.getLat()))
				.into(detail_house_map));

		estateCode = detail.getEstateCode();
		estateName = detail.getEstateName();
		gscopeName = detail.getGscopeName();

		Map<String, String> eParam = new HashMap<>();
		eParam.put("EstateCode", estateCode);
		Map<String, String> gParam = new HashMap<>();
		gParam.put("GscopeId", detail.getGscopeId()+"");
		mPresenter.getPriceTrend(gParam, eParam);

		latitude = detail.getLat();
		longitude = detail.getLng();

		if (latitude==0 || longitude==0){
			detail_rl_map.setVisibility(View.GONE);
			detail_bt_nearby.setVisibility(View.GONE);
		}

		atv_map_name.setText(detail.getAddress());

		est_detail_title.setText(detail.getEstateName());

		saleNumber = detail.getSaleNumber();
		rentNumber = detail.getRentNumber();
		est_saleing_count.setLeftText(detail.getSaleNumber()+"");
		est_sale_count.setLeftText(detail.getRentNumber()+"");

		setTwoColorText(est_av_price, est_ll_price, MyUtils.format2String(detail.getSaleAvgPrice())+"元/㎡");

		setTwoColorText(est_tv_type, est_ll_type, detail.getPropertyType());

		if (detail.getEstateInfo().getFloorRatio()!=0){
			setTwoColorText(est_tv_ratio, est_ll_ratio, detail.getEstateInfo().getFloorRatio()+"");
		}

		if (detail.getEstateInfo().getGreenRatio()!=0){
			setTwoColorText(est_tv_green, est_ll_green, MyUtils.format2String(detail.getEstateInfo().getGreenRatio()*100)+"%");
		}

		if (!TextUtils.isEmpty(detail.getEstateInfo().getPark())){
			setTwoColorText(est_car_place, est_ll_place, detail.getEstateInfo().getPark());
		}

		if (detail.getOpDate()>0){
			long time = detail.getOpDate()*1000L;
			setTwoColorText(est_build_year, est_ll_year, DateUtil.format(time, DateUtil.FORMAT3));
		}

		if (!TextUtils.isEmpty(detail.getEstateInfo().getPropertyCharges())){
			setTwoColorText(est_property_price, est_ll_proprice, detail.getEstateInfo().getPropertyCharges()+"元/㎡/月");
		}
		if (!TextUtils.isEmpty(detail.getEstateInfo().getPropertyCompany())){
			setTwoColorText(est_property_company, est_ll_company, detail.getEstateInfo().getPropertyCompany());
		}
		if (!TextUtils.isEmpty(detail.getDevelopers())){
			setTwoColorText(est_tv_developer, est_ll_developer, detail.getDevelopers());
		}

		imageBrowseList.clear();
		List<EstateImage> images = detail.getEstateImages();
		List<String> imgList = new ArrayList<>();
		if (images!=null && images.size()>0){
			for (EstateImage item : images){
				imgList.add(item.getImageFullPath());
				imageBrowseList.add(new ImageBean(item.getImageFullPath(), item.getImageTitle()));
			}
		} else {
			imgList.add("http://imgsh.centanet.com/shanghai/staticfile/web/defaultestate-android.png");
		}
		detail_dl_imgs.setImgList(imgList);
		shareImage = imgList.get(0);
	}

	private void setTwoColorText(TextView tv, LinearLayout parent, String text){
		tv.setText(text);
		parent.setVisibility(View.VISIBLE);
	}

	@Override
	public void setCollectResult(long collectId) {
		this.collectId = collectId;
		if (collectId>0){
			toast("收藏成功");
			menu_like.setIcon(R.drawable.ic_liked);
		}else {
			toast("收藏失败");
		}
	}

	private long collectId;

	@Override
	public void isCollected(long collectId) {
		this.collectId = collectId;
		if (collectId>0 && menu_like!=null){
			menu_like.setIcon(R.drawable.ic_liked);
		}
	}

	@Override
	public void deleteCollectResult(boolean result) {
		if (result){
			toast("取消收藏成功");
			collectId = 0;
			menu_like.setIcon(R.drawable.ic_like_menu_white);
//			detail_fb_like.setImageResource(R.drawable.ic_like_menu_black);
		}else {
			toast("取消收藏失败");
		}
	}

	@Override
	public void hidePriceTrend() {
		detail_ll_trend.setVisibility(View.GONE);
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

	private String estateCode, estateName;

	@OnClick(R.id.estate_sale)
	public void saleClick(){
		if (saleNumber>0){

			HashMap<String, SearchParam> param = new HashMap<>();
			SearchParam searchParam = new SearchParam();
			searchParam.setTitle("小区");
			searchParam.setKey("EstateCode");
			searchParam.setValue(estateCode);
			searchParam.setText(estateName);
			searchParam.setName("Estate");
			param.put(searchParam.getKey(), searchParam);

			Intent intent = new Intent(getActivity(), HouseList.class);
			intent.putExtra(HouseList.SEARCH_PARAM_KEY, param);
			intent.putExtra(MapFragment.HOUSE_TYPE_KEY, MapFragment.HOUSE_TYPE_SECOND);
			startActivity(intent);
		}
	}

	@OnClick(R.id.estate_rent)
	public void rentClick(){
		if (rentNumber>0){

			HashMap<String, SearchParam> param = new HashMap<>();
			SearchParam searchParam = new SearchParam();
			searchParam.setTitle("小区");
			searchParam.setKey("EstateCode");
			searchParam.setValue(estateCode);
			searchParam.setText(estateName);
			searchParam.setName("Estate");
			param.put(searchParam.getKey(), searchParam);

			Intent intent = new Intent(getActivity(), HouseList.class);
			intent.putExtra(HouseList.SEARCH_PARAM_KEY, param);
			intent.putExtra(MapFragment.HOUSE_TYPE_KEY, MapFragment.HOUSE_TYPE_RENT);
			startActivity(intent);
		}
	}

	private void collectClick(){
		collectionExchange = !collectionExchange;
		if (DataHolder.getInstance().isUserLogin()){
			if (collectId>0){
				mPresenter.deleteCollect(collectId);
			}else {
				toCollect();
			}

			if (!collectionExchange) {
				getActivity().setResult(0);
			} else {
				getActivity().setResult(1);
			}
		}else {
			Intent intent = new Intent(getActivity(), LoginActivity.class);
			intent.putExtra(LoginActivity.LOGIN_INTENT_KEY, LoginActivity.LOGIN_INTENT_COLLECT);
			startActivityForResult(intent, 104);
		}

	}

	private void toCollect() {
		Map<String, String> param = new HashMap<>();
		param.put("CollectValue", estateCode);
		param.put("UserId", DataHolder.getInstance().getUserId());
		param.put("CityCode", "021");
		param.put("Source", "xiaoqu");
		param.put("AppName", "APP");
		param.put("CollectUrl", "");
		mPresenter.insertCollect(param);
	}

	private ShareDialog shareDialog;
	private String shareTitle;
	private String shareImage;
	private String shareSumtitle;
	private String shareUrl;

	private void shareClick(){

		Map<String, String> shareParam = new HashMap<>();
		shareParam.put("title", shareTitle);
		shareParam.put("summary", shareSumtitle);
		if (!TextUtils.isEmpty(shareImage)){
			shareParam.put("imageUrl", shareImage);
		}
		shareParam.put("url", shareUrl);

		shareDialog = new ShareDialog(getActivity(), shareParam);
		shareDialog.show();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (!rxSubscription.isUnsubscribed()) {
			rxSubscription.unsubscribe();
		}
	}

	@OnClick(detail_tv_street)
	public void outDoorClick(){

		if (latitude > 0 && longitude > 0) {
			Intent intent = new Intent(getActivity(), PanoramaActivity.class);
			intent.putExtra(NearbyFragment.LATITUDE_KEY, latitude);
			intent.putExtra(NearbyFragment.LONGITUDE_KEY, longitude);
			intent.putExtra(PanoramaActivity.PANORAMA_MARK_NAME, estateName);
			getActivity().startActivity(intent);
		} else {
			toast("该房源没有经纬度信息");
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode==LoginActivity.LOGIN_INTENT_COLLECT){
			toCollect();
		}else if (resultCode==LoginActivity.LOGIN_INTENT_TALK){
//			toChat();
		}
	}
}
