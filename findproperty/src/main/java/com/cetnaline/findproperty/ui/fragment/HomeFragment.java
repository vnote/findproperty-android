package com.cetnaline.findproperty.ui.fragment;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.cetnaline.findproperty.AppContents;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.bean.AdvertBo;
import com.cetnaline.findproperty.api.bean.HouseBo;
import com.cetnaline.findproperty.api.bean.NewHouseListBo;
import com.cetnaline.findproperty.api.bean.SearchParam;
import com.cetnaline.findproperty.base.BaseFragment;
import com.cetnaline.findproperty.db.entity.GScope;
import com.cetnaline.findproperty.db.entity.RailLine;
import com.cetnaline.findproperty.db.entity.RailWay;
import com.cetnaline.findproperty.api.NetContents;
import com.cetnaline.findproperty.presenter.impl.HomePresenter;
import com.cetnaline.findproperty.presenter.ui.HomeContract;
import com.cetnaline.findproperty.ui.activity.HouseDetail;
import com.cetnaline.findproperty.ui.activity.HouseList;
import com.cetnaline.findproperty.ui.activity.IntentSettingActivity;
import com.cetnaline.findproperty.ui.activity.LoginActivity;
import com.cetnaline.findproperty.ui.activity.MainTabActivity;
import com.cetnaline.findproperty.ui.activity.MySubscribeActivity;
import com.cetnaline.findproperty.ui.activity.ScanActivity;
import com.cetnaline.findproperty.ui.activity.SearchActivity;
import com.cetnaline.findproperty.ui.activity.StoreSearchActivity;
import com.cetnaline.findproperty.ui.activity.WebActivity;
import com.cetnaline.findproperty.utils.CircularRevealAnim;
import com.cetnaline.findproperty.utils.DataHolder;
import com.cetnaline.findproperty.utils.DbUtil;
import com.cetnaline.findproperty.utils.MyUtils;
import com.cetnaline.findproperty.utils.SharedPreferencesUtil;
import com.cetnaline.findproperty.utils.glide.GlideLoad;
import com.cetnaline.findproperty.widgets.AnimationLayout;
import com.cetnaline.findproperty.widgets.MyText;
import com.cetnaline.findproperty.widgets.ObservableScrollView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

import static com.cetnaline.findproperty.ui.activity.SplashActivity.DATA_LOAD_SUCCESS;


/**
 * 首页
 * Created by fanxl2 on 2016/7/21.
 */
public class HomeFragment extends BaseFragment<HomePresenter> implements HomeContract.View {

    @BindView(R.id.home_house_list)  //房源列表
            LinearLayout home_house_list;

    @BindView(R.id.home_tip_title)  //意向提示
            TextView mTvIntentTip;

    @BindView(R.id.home_to_intent)    //填写意向
            TextView mTvAddIntent;

    @BindView(R.id.home_intent_text)  //意向描述
            TextView mTvIntent;

    @BindView(R.id.home_intent_info)  //意向内容
            TextView mTvIntentInfo;

    @BindView(R.id.home_look_more)  //点击查看更多
            TextView mTvLookMore;

    @BindView(R.id.home_list_tips)  //列表默认提示
            TextView mTvNoData;

    @BindView(R.id.no_intent_data)  //列表无数据提示
            TextView no_intent_data;

    @BindView(R.id.home_intent_line)  //意向和列表的分割线
            View home_intent_line;

    @BindView(R.id.home_title_status)
    View home_title_status;

    @BindView(R.id.home_sv)
    ObservableScrollView mSrollView;

    @BindView(R.id.home_search_view)     //搜索框
            RelativeLayout mRlSearch;

    @BindView(R.id.home_search_view1)  //滑动到顶部的搜索框
            AnimationLayout mRlSearchTop;

    @BindView(R.id.home_v_line)  //顶部搜索框与顶部的分割线
            View mSearchTopLine;

    @BindView(R.id.active_btn)    //活动按钮
            AppCompatImageView mBtnActive;

    private LayoutInflater inflater;

    private boolean test = true;

    private DrawableRequestBuilder<String> requestBuilder;

    //活动按钮显示和隐藏
    ObjectAnimator animator_show;
    ObjectAnimator animator_hide;

    @Override
    protected int getLayoutId() {
        return R.layout.frag_home;
    }

    @Override
    protected void init() {
        inflater = LayoutInflater.from(getActivity());
        requestBuilder = GlideLoad.init(this);
        animator_show = ObjectAnimator.ofFloat(mBtnActive, "translationX", MyUtils.dip2px(getActivity(), 40));  //右移40dp
        animator_hide = ObjectAnimator.ofFloat(mBtnActive, "translationX", MyUtils.dip2px(getActivity(), -20)); //左移20dp

        DataHolder.getInstance().setChangeIntent(true);

        mPresenter.getHomeRecommend(DataHolder.getInstance().getLatitude(), DataHolder.getInstance().getLongitude());

        mRlSearchTop.setListener(new AnimationLayout.LayoutAnimationListener() {
            @Override
            public void before() {
                if (mRlSearchTop.isAnimated()) {
                    mSearchTopLine.setVisibility(View.GONE);
                } else {
                    mRlSearchTop.setVisibility(View.VISIBLE);
                    home_title_status.setVisibility(View.VISIBLE);
                    mRlSearch.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void after() {
                if (mRlSearchTop.isAnimated()) {
                    //					home_search_view1.setBackgroundColor(Color.WHITE);
                    mSearchTopLine.setVisibility(View.VISIBLE);
                } else {
                    mRlSearchTop.setVisibility(View.INVISIBLE);
                    home_title_status.setVisibility(View.GONE);
                    mRlSearch.setVisibility(View.VISIBLE);
                }
            }
        });

        //监听页面移动距离  开始搜索栏扩散动画
        mSrollView.setScrollViewListener(new ObservableScrollView.ScrollViewListener() {
            @Override
            public void onScrollChanged(int x, int y, int oldx, int oldy) {
                int[] position = new int[2];
                mRlSearch.getLocationOnScreen(position);
                int toScreenHeight = position[1];
                if (toScreenHeight <= 80 && test) {
                    test = false;
                    mRlSearchTop.setVisibility(View.VISIBLE);
                    mRlSearchTop.layoutAnimate();
                } else if (toScreenHeight > 80 && !test) {
                    test = true;
                    mRlSearchTop.layoutAnimate();
                }
            }
        });

        // TODO: 2017/3/1 流量活动
        mPresenter.getAppXuanFuAdvertRequest();
    }

    @Override
    public void onResume() {
        super.onResume();
        //用户已登录 && 是否从改变意向跳转
        if (DataHolder.getInstance().isUserLogin() && DataHolder.getInstance().isChangeIntent()) {
            mPresenter.getIntention4Home(DataHolder.getInstance().getUserId());
        }

        if (!DataHolder.getInstance().isUserLogin() && DataHolder.getInstance().isChangeIntent()) {
            searchParams = null;
            mPresenter.getHomeRecommend(DataHolder.getInstance().getLatitude(), DataHolder.getInstance().getLongitude());
        }
    }

    @Override
    protected HomePresenter createPresenter() {
        return new HomePresenter();
    }

    private void addSecondHouse(HouseBo house, boolean isFullImage) {
        View view = inflater.inflate(R.layout.item_house_small, home_house_list, false);

        view.setTag(house.getPostId());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), HouseDetail.class);
                intent.putExtra(MapFragment.HOUSE_TYPE_KEY, MapFragment.HOUSE_TYPE_SECOND);
                intent.putExtra(HouseDetailFragment.HOUSE_ID_KEY, view.getTag().toString());
                startActivity(intent);
            }
        });

        String imgUrl;
        if (isFullImage) {
            imgUrl = house.getDefaultImage();
        } else {
            imgUrl = NetContents.IMG_BASE_URL + house.getDefaultImage() + "_400x300_f"+house.getDefaultImageExt();
        }

        if (TextUtils.isEmpty(imgUrl)) {
            imgUrl = AppContents.POST_DEFAULT_IMG_URL;
        }

        ImageView item_small_img = (ImageView) view.findViewById(R.id.item_small_img);
        GlideLoad.load(new GlideLoad.Builder(requestBuilder, imgUrl)
                .into(item_small_img));

        TextView item_small_title = (TextView) view.findViewById(R.id.item_small_title);
        item_small_title.setText(house.getTitle());

        TextView item_small_house = (TextView) view.findViewById(R.id.item_small_house);
        item_small_house.setText(house.getRoomCount() + "室" + house.getHallCount() + "厅 | " + MyUtils.formatHouseArea(house.getGArea()) + "㎡ | " + house.getDirection());

        MyText item_small_money = (MyText) view.findViewById(R.id.item_small_money);
        item_small_money.setLeftAndRight(MyUtils.format2String(house.getSalePrice() / 10000), "万");

//        TextView item_small_est = (TextView) view.findViewById(R.id.item_small_est);
//        item_small_est.setText(house.getDisplayEstName());
//
//        TextView item_small_price = (TextView) view.findViewById(R.id.item_small_price);
//        item_small_price.setText(MyUtils.format2String(house.getUnitSalePrice()) + "元/㎡");
        LinearLayout item_big_keys = (LinearLayout) view.findViewById(R.id.item_small_keys);
        item_big_keys.removeAllViews();
        if (house.getKeyWords() != null && !TextUtils.isEmpty(house.getKeyWords())) {
            String[] keyWords = house.getKeyWords().split(",");
            int length = keyWords.length;
            if (length > 3) {
                length = 3;
            }
            for (int i = 0; i < length; i++) {
                TextView key = (TextView) inflater.inflate(R.layout.item_key_text, item_big_keys, false);
                key.setText(keyWords[i]);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) key.getLayoutParams();
                params.setMargins(0, 0, 10, 0);
                item_big_keys.addView(key);
            }
        } else {
            item_big_keys.setVisibility(View.INVISIBLE);
        }
        home_house_list.addView(view);
    }

    private void addRentHouse(HouseBo house, boolean isFullImage) {
        View view = inflater.inflate(R.layout.item_house_rent_small, home_house_list, false);

        view.setTag(house.getPostId());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), HouseDetail.class);
                intent.putExtra(MapFragment.HOUSE_TYPE_KEY, MapFragment.HOUSE_TYPE_RENT);
                intent.putExtra(HouseDetailFragment.HOUSE_ID_KEY, view.getTag().toString());
                startActivity(intent);
            }
        });

        String imgUrl;
        if (isFullImage) {
            imgUrl = house.getDefaultImage();
        } else {
            imgUrl = NetContents.IMG_BASE_URL + house.getDefaultImage() + "_400x300_f"+house.getDefaultImageExt();
        }

        if (TextUtils.isEmpty(imgUrl)) {
            imgUrl = AppContents.POST_DEFAULT_IMG_URL;
        }

        ImageView item_small_img = (ImageView) view.findViewById(R.id.item_small_img);
        GlideLoad.load(new GlideLoad.Builder(requestBuilder, imgUrl)
                .into(item_small_img));

        TextView item_small_title = (TextView) view.findViewById(R.id.item_small_title);
        item_small_title.setText(house.getTitle());

        TextView item_small_house = (TextView) view.findViewById(R.id.item_small_house);

        item_small_house.setText(house.getRoomCount() + "室" + house.getHallCount() + "厅 | " + MyUtils.formatHouseArea(house.getGArea()) + "㎡ | " + house.getDirection());

//        TextView item_small_fitment = (TextView) view.findViewById(R.id.item_small_fitment);
//        item_small_fitment.setText(house.getFitment());

        MyText item_rent_price = (MyText) view.findViewById(R.id.item_rent_price);
        item_rent_price.setLeftAndRight(MyUtils.format2String(house.getRentPrice()), "元/月");

        LinearLayout item_small_keys = (LinearLayout) view.findViewById(R.id.item_small_keys);
        item_small_keys.removeAllViews();
        if (house.getKeyWords() != null && !TextUtils.isEmpty(house.getKeyWords())) {
            item_small_keys.setVisibility(View.VISIBLE);
            String[] keyWords = house.getKeyWords().split(",");
            int length = keyWords.length;
            if (length > 3) {
                length = 3;
            }
            for (int i = 0; i < length; i++) {
                TextView key = (TextView) inflater.inflate(R.layout.item_key_text, item_small_keys, false);
                key.setText(keyWords[i]);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) key.getLayoutParams();
                params.setMargins(0, 0, 10, 0);
                item_small_keys.addView(key);
            }
        } else {
            item_small_keys.setVisibility(View.INVISIBLE);
        }

        home_house_list.addView(view);
    }

    private void addNewHouse(NewHouseListBo house) {
        View view = inflater.inflate(R.layout.item_house_new_big, home_house_list, false);

        view.setTag(house.getEstExtId());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), HouseDetail.class);
                intent.putExtra(MapFragment.HOUSE_TYPE_KEY, MapFragment.HOUSE_TYPE_NEW);
                intent.putExtra(HouseDetailFragment.ESTEXT_ID_KEY, view.getTag().toString());
                startActivity(intent);
            }
        });

        String iconUrl = house.getIconUrl();
        iconUrl = iconUrl.substring(0, iconUrl.indexOf(".")) + "_" + NetContents.HOUSE_BIG_IMAGE_LIST_WIDTH + "x" + NetContents.HOUSE_BIG_IMAGE_LIST_HEIGHT + "_f.jpg";

        ImageView item_big_img = (ImageView) view.findViewById(R.id.item_big_img);
        GlideLoad.load(new GlideLoad.Builder(requestBuilder, NetContents.NEW_HOUSE_IMG + iconUrl)
                .into(item_big_img));

        TextView item_big_title = (TextView) view.findViewById(R.id.item_big_title);
        item_big_title.setText(house.getAdName());

        TextView item_big_house = (TextView) view.findViewById(R.id.item_big_house);
        item_big_house.setText(house.getEstType() + " | " + ("认购".equals(house.getStatus()) || "持销".equals(house.getStatus()) || "尾盘".equals(house.getStatus()) ? "在售" : "待售"));

        TextView item_big_money = (TextView) view.findViewById(R.id.item_big_money);
        if (house.getAveragePrice() > 0) {
            item_big_money.setText("均价: " + MyUtils.format2String(house.getAveragePrice()) + "元/㎡");
        } else {
            item_big_money.setText("均价: 暂无");
        }

        home_house_list.addView(view);
    }

    @OnClick(R.id.home_to_second)
    public void homeToSecond() {
        //		((MainTabActivity)getActivity()).mapHouseTypeChange(MapFragment.HOUSE_TYPE_SECOND);
        Intent intent = new Intent(getActivity(), HouseList.class);
        intent.putExtra(MapFragment.HOUSE_TYPE_KEY, MapFragment.HOUSE_TYPE_SECOND);
        startActivity(intent);
    }

    @OnClick(R.id.home_to_rent)
    public void homeToRent() {
        //		((MainTabActivity)getActivity()).mapHouseTypeChange(MapFragment.HOUSE_TYPE_RENT);
        Intent intent = new Intent(getActivity(), HouseList.class);
        intent.putExtra(MapFragment.HOUSE_TYPE_KEY, MapFragment.HOUSE_TYPE_RENT);
        startActivity(intent);
    }

    @OnClick(R.id.home_to_new)
    public void homeToNew() {
        //		((MainTabActivity)getActivity()).mapHouseTypeChange(MapFragment.HOUSE_TYPE_NEW);
        //		Intent intent = new Intent(getActivity(), EstateList.class);
        //		intent.putExtra(MapFragment.HOUSE_TYPE_KEY, MapFragment.HOUSE_TYPE_ESTATE);
        //		startActivity(intent);

        ((MainTabActivity) getActivity()).changePager(MainTabActivity.TAB_FOUND);
        ((MainTabActivity) getActivity()).changeFoundFragmentTab(1);
    }

    @OnClick(R.id.home_to_shop)
    public void homeToShop() {

        if (!DataHolder.getInstance().isUserLogin()) {
            Bundle bundle = new Bundle();
            bundle.putInt(LoginActivity.OPEN_TYPE, LoginActivity.NO_EXCHANGE);
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.putExtras(bundle);
            getActivity().startActivity(intent);
            toast("请先登录");
            return;
        }

//        ((MainTabActivity) getActivity()).changePager(MainTabActivity.TAB_CHAT);

        		startActivity(new Intent(getActivity(), StoreSearchActivity.class));
//        		if (!MyUtils.openActivityForUrl(getActivity(), "http://sh.centanet.com/m/xiezilou/", false)) {
//        			Intent intent = new Intent(getActivity(), WebActivity.class);
//        			intent.putExtra(WebActivity.TARGET_URL, "http://sh.centanet.com/m/xiezilou/");
//        			startActivity(intent);
//        		}
    }

    @OnClick(R.id.home_search_view)
    public void homeToSearch() {
        startActivity(new Intent(getActivity(), SearchActivity.class));
    }

    @OnClick(R.id.home_search_view1)
    public void homeToSearch1() {
        startActivity(new Intent(getActivity(), SearchActivity.class));
    }

    @OnClick(R.id.home_iv_scan)
    public void homeToScan() {
        checkPermission();
    }

    @OnClick(R.id.home_iv_scan1)
    public void homeToScan1() {
        checkPermission();
    }

    @OnClick(R.id.home_look_more)
    public void homeToMore() {
        goToHouseList();
    }

    private void goToHouseList() {
        Intent intent = new Intent(getActivity(), HouseList.class);
        intent.putExtra(MapFragment.HOUSE_TYPE_KEY, houseType);
        intent.putExtra(HouseList.SEARCH_PARAM_KEY, searchMap);
        startActivity(intent);
    }

    @OnClick(R.id.home_intent_info)
    public void toHouseList() {
        Intent intent = new Intent(getActivity(), MySubscribeActivity.class);
        intent.putExtra("subscribeType", houseType);
        startActivity(intent);
    }

    @OnClick(R.id.home_to_intent)
    public void homeToIntent() {
        if (DataHolder.getInstance().isUserLogin()) {

            if (!SharedPreferencesUtil.getBoolean(DATA_LOAD_SUCCESS)) {
                toast("基础数据加载失败，请重启加载！");
                return;
            }

            int[] tvLocation = new int[2];
            mTvAddIntent.getLocationInWindow(tvLocation);
            int tvX = tvLocation[0] + mTvAddIntent.getWidth() / 2;
            int tvY = tvLocation[1] + mTvAddIntent.getHeight() / 2;

            Intent intent = new Intent(getActivity(), IntentSettingActivity.class);
            intent.putExtra(CircularRevealAnim.CENTER_X_KEY, tvX);
            intent.putExtra(CircularRevealAnim.CENTER_Y_KEY, tvY);
            intent.putExtra("justClose", true);
            startActivity(intent);
        } else {
            toast("请先登录");
            startActivityForResult(new Intent(getActivity(), LoginActivity.class), 105);
        }
    }

    private int houseType;
    private HashMap<String, SearchParam> searchMap;

    private List<SearchParam> searchParams;

    @Override
    public void setIntention(List<SearchParam> searchList, int houseType) {
        this.houseType = houseType;
        this.searchParams = searchList;
        DataHolder.getInstance().setChangeIntent(false);

        Map<String, String> searchParams = new HashMap<>();
        if (searchList != null && searchList.size() > 0) {
            searchMap = new HashMap<>();
            mTvAddIntent.setVisibility(View.INVISIBLE);
            mTvIntent.setVisibility(View.INVISIBLE);
            mTvIntentInfo.setVisibility(View.VISIBLE);
            StringBuffer sb = new StringBuffer();
            for (SearchParam param : searchList) {
                if ("GScopeId".equalsIgnoreCase(param.getParamKey()) || "RegionId".equalsIgnoreCase(param.getParamKey())) {
                    GScope gScope = DbUtil.getGScopeById(Integer.parseInt(param.getValue()));
                    if (gScope != null) {
                        sb.append(gScope.getGScopeName()).append(" | ");
                        param.setText(gScope.getGScopeName());
                    }
                } else if ("RailWayId".equalsIgnoreCase(param.getParamKey())) {
                    RailWay railWay = DbUtil.getRailWayById(param.getValue());
                    if (railWay != null) {
                        sb.append(railWay.getRailWayName()).append(" | ");
                        param.setText(railWay.getRailWayName());
                    }
                } else if ("RailLineId".equalsIgnoreCase(param.getParamKey())) {
                    RailLine railLine = DbUtil.getRailLineById(param.getValue());
                    if (railLine != null) {
                        sb.append(railLine.getRailLineName()).append(" | ");
                        param.setText(railLine.getRailLineName());
                    }
                } else {
                    sb.append(param.getText()).append(" | ");
                }

                getParamData(param, searchParams);

                searchMap.put(param.getKey(), param);
            }
            mTvIntentInfo.setText(sb.toString());

            if (houseType == MapFragment.HOUSE_TYPE_NEW) {
                searchParams.put("StartIndex", "0");
                searchParams.put("length", "15");
                mPresenter.getNewHouseList(searchParams);
            } else {
                if (houseType == MapFragment.HOUSE_TYPE_SECOND) {
                    searchParams.put("PostType", "S");
                } else {
                    searchParams.put("PostType", "R");
                }
                searchParams.put("PageIndex", "0");
                searchParams.put("PageCount", "15");
                searchParams.put("ImageWidth", NetContents.HOUSE_SMALL_IMAGE_LIST_WIDTH + "");
                searchParams.put("ImageHeight", NetContents.HOUSE_SMALL_IMAGE_LIST_HEIGHT + "");

                if (searchParams.get("SchoolId") != null) {
                    mPresenter.getHouseBySchool4AllResult(searchParams);
                } else if (searchParams.get("RailWayId") != null || searchParams.get("RailLineId") != null) {
                    mPresenter.getHouseByMetroAllResult(searchParams);
                } else {
                    mPresenter.getHouseList(searchParams);
                }
            }
        } else {
            mTvAddIntent.setVisibility(View.VISIBLE);
            mTvIntent.setVisibility(View.VISIBLE);
            mTvIntentInfo.setVisibility(View.GONE);
            mTvLookMore.setVisibility(View.GONE);
            mTvNoData.setVisibility(View.VISIBLE);

            mPresenter.getHomeRecommend(DataHolder.getInstance().getLatitude(), DataHolder.getInstance().getLongitude());
        }
    }

    @Override
    public void setHouseList(List<HouseBo> houseList) {
        home_house_list.removeAllViews();
        if (houseList == null || houseList.size() == 0) {
            no_intent_data.setVisibility(View.VISIBLE);
            mPresenter.getHomeRecommend(DataHolder.getInstance().getLatitude(), DataHolder.getInstance().getLongitude());
            return;
        }

        home_intent_line.setVisibility(View.VISIBLE);
        mTvIntentTip.setText("您的意向");
        mTvLookMore.setVisibility(View.VISIBLE);
        mTvNoData.setVisibility(View.GONE);
        no_intent_data.setVisibility(View.GONE);
        if (houseType == MapFragment.HOUSE_TYPE_SECOND) {
            for (HouseBo houseBo : houseList) {
                addSecondHouse(houseBo, true);
            }
        } else {
            for (HouseBo houseBo : houseList) {
                addRentHouse(houseBo, true);
            }
        }
    }

    @Override
    public void setNewHouseList(List<NewHouseListBo> houseList) {
        home_house_list.removeAllViews();
        if (houseList == null || houseList.size() == 0) {
            no_intent_data.setVisibility(View.VISIBLE);
            mPresenter.getHomeRecommend(DataHolder.getInstance().getLatitude(), DataHolder.getInstance().getLongitude());
            return;
        }
        mTvIntentTip.setText("您的意向");
        mTvLookMore.setVisibility(View.VISIBLE);
        mTvNoData.setVisibility(View.GONE);
        no_intent_data.setVisibility(View.GONE);
        for (NewHouseListBo houseBo : houseList) {
            addNewHouse(houseBo);
        }
    }

    @Override
    public void setHomeRecommend(List<HouseBo> houseBos) {

        home_house_list.removeAllViews();

        if (houseBos != null) {
            for (int i = 0; i < houseBos.size(); i++) {
                if (i < 5) {
                    addSecondHouse(houseBos.get(i), false);
                } else {
                    addRentHouse(houseBos.get(i), false);
                }
            }
        }

        if (searchParams != null && searchParams.size() > 0) {

        } else {
            mTvAddIntent.setVisibility(View.VISIBLE);
            mTvIntent.setVisibility(View.VISIBLE);
            mTvIntentInfo.setVisibility(View.GONE);
            no_intent_data.setVisibility(View.GONE);
        }
        mTvNoData.setVisibility(View.VISIBLE);
        mTvLookMore.setVisibility(View.GONE);
    }

    // TODO: 2017/3/1 流量活动
    @Override
    public void setAdvert(AdvertBo advert) {
        mBtnActive.setVisibility(View.VISIBLE);
        Glide.with(getActivity())
                .load(advert.getImgUrl())
                .into(mBtnActive);
        animator_show.setDuration(100)
                .start();

        mBtnActive.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), WebActivity.class);
            intent.putExtra(WebActivity.TARGET_URL, advert.getAdvertUrl());
            intent.putExtra(WebActivity.WEB_TYPE_KEY, WebActivity.WEB_TYPE_NORMAL);
            intent.putExtra(WebActivity.IS_ACTIVE_URL, true);
            intent.putExtra(WebActivity.WEB_SHARE_KEY, true);
            startActivity(intent);
        });

        animator_show.setDuration(100).start();
        //监听页面滚动状态  显示和隐藏活动按钮
        mSrollView.setOnScrollListener(new ObservableScrollView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(ObservableScrollView view, int scrollState) {
                if (scrollState == ObservableScrollView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    if (mBtnActive.getVisibility() == View.VISIBLE) {
                        animator_hide.setDuration(100).start();
                    }
                }

                if (scrollState == ObservableScrollView.OnScrollListener.SCROLL_STATE_IDLE) {
                    animator_show.setDuration(100).start();
                }
            }

            @Override
            public void onScroll(ObservableScrollView view, boolean isTouchScroll, int l, int t, int oldl, int oldt) {

            }
        });
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

    private void getParamData(SearchParam param, Map<String, String> searchParams) {
        String[] paramKeys = param.getParamKey().split(",");
        if (paramKeys != null && paramKeys.length > 1) {
            String[] values = param.getValue().split(",");

            if ("MinOpdate,MaxOpdate".equals(param.getParamKey())) {

                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

                int minYear = year - Integer.parseInt(values[1]);
                int maxYear = year - Integer.parseInt(values[0]);

                try {
                    Date minDate = format.parse(minYear + "-1-1");
                    Date maxDate = format.parse(maxYear + "-12-31");

                    long minTime = minDate.getTime() / 1000;
                    long maxTime = maxDate.getTime() / 1000;

                    searchParams.put(paramKeys[0], minTime + "");
                    searchParams.put(paramKeys[1], maxTime + "");

                    param.setValue(minTime + "," + maxTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else if ("OpDateBegin,OpDateEnd".equals(param.getParamKey())) {
                GregorianCalendar calendar = new GregorianCalendar();
                calendar.setTimeInMillis(System.currentTimeMillis());

                int startMonth = Integer.parseInt(values[0]);
                int endMonth = Integer.parseInt(values[1]);

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

                calendar.add(Calendar.MONTH, startMonth);
                String startDate = format.format(calendar.getTime());

                calendar.setTimeInMillis(System.currentTimeMillis());

                calendar.add(Calendar.MONTH, endMonth);
                String endDate = format.format(calendar.getTime());
                searchParams.put(paramKeys[0], startDate);
                searchParams.put(paramKeys[1], endDate);

                param.setValue(startDate + "," + endDate);
            } else {
                searchParams.put(paramKeys[0], values[0]);
                searchParams.put(paramKeys[1], values[1]);
            }
        } else {
            if (param.getParamKey().equals("Tags")) {
                if (searchParams.get("Tags") != null) {
                    searchParams.put("Tags", searchParams.get("Tags") + "_" + param.getId());
                } else {
                    searchParams.put("Tags", param.getId() + "");
                }
            } else if (param.getParamKey().equals("Feature")) {
                if (searchParams.get("Feature") != null) {
                    searchParams.put("Feature", searchParams.get("Feature") + "_" + param.getId());
                } else {
                    searchParams.put("Feature", param.getId() + "");
                }
            } else if (param.getParamKey().equals("Property")) {
                searchParams.put("PropertyType", param.getValue());
            } else {
                searchParams.put(param.getParamKey(), param.getValue());
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == LoginActivity.LOGIN_SUCCESS_CODE) {
            startActivity(new Intent(getActivity(), IntentSettingActivity.class));
        }
    }

    public static final int REQUEST_PERMISSION_CODE = 10;

    private String[] permissions = new String[]{Manifest.permission.CAMERA};

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("权限申请")
                        .setMessage("扫码需要您开启摄像头访问权限")
                        .setPositiveButton("确定", (dialog, which) -> {

                            requestPermissions(permissions, REQUEST_PERMISSION_CODE);

                        }).show();
            } else {
                requestPermissions(permissions, REQUEST_PERMISSION_CODE);
            }
        } else {
            startActivity(new Intent(getActivity(), ScanActivity.class));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity(new Intent(getActivity(), ScanActivity.class));
            } else {
                //权限拒绝
                toast("APP没有摄像头使用权限，扫码不能使用");
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
