package com.cetnaline.findproperty.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.bean.EstateBo;
import com.cetnaline.findproperty.api.bean.HouseDetailBo;
import com.cetnaline.findproperty.api.bean.NewHouseDetail;
import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.base.BaseActivity;
import com.cetnaline.findproperty.base.BaseFragmentActivity;
import com.cetnaline.findproperty.entity.bean.CollectInfoChangeBean;
import com.cetnaline.findproperty.entity.bean.SystemMessageBean;
import com.cetnaline.findproperty.entity.event.NewsEvent;
import com.cetnaline.findproperty.entity.result.BaseResult;
import com.cetnaline.findproperty.entity.ui.TabEntity;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.cetnaline.findproperty.ui.fragment.HouseDetailFragment;
import com.cetnaline.findproperty.ui.fragment.MapFragment;
import com.cetnaline.findproperty.ui.fragment.VillageDetailFragment;
import com.cetnaline.findproperty.ui.listadapter.NewsAdapter;
import com.cetnaline.findproperty.ui.listadapter.SystemMessageAdapter;
import com.cetnaline.findproperty.utils.DataHolder;
import com.cetnaline.findproperty.utils.MyUtils;
import com.cetnaline.findproperty.utils.StatusBarCompat;
import com.cetnaline.findproperty.widgets.tablayout.CommonTabLayout;
import com.cetnaline.findproperty.widgets.tablayout.listener.CustomTabEntity;
import com.cetnaline.findproperty.widgets.tablayout.listener.OnTabSelectListener;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import io.rong.eventbus.EventBus;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func2;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by diaoqf on 2016/8/17.
 */
public class NewsActivity extends BaseActivity {
    @BindView(R.id.tab_bar)
    CommonTabLayout tab_bar;

    @BindView(R.id.data_list)
    SwipeMenuListView data_list;
    @BindView(R.id.no_message_layout)
    LinearLayout no_message_layout;

    private NewsAdapter adapter;
    private SystemMessageAdapter systemAdapter;

    private ArrayList<CustomTabEntity> mTabEntities;

    private List<CollectInfoChangeBean> collectInfoChangeBeanHouseList;  //房源消息
    private List<CollectInfoChangeBean> collectInfoChangeBeanEsateList;  //小区消息
    private List<SystemMessageBean> systemMessageBeanList;               //系统消息

    private int postMsgNumber;
    private int estMsgNumber;
    private int sysMsgNumber;

    private List<CollectInfoChangeBean> data;

    private int current_list;

    private CompositeSubscription mCompositeSubscription;

    @Override
    protected int getContentViewId() {
        return R.layout.act_news;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        postMsgNumber = estMsgNumber = sysMsgNumber = 0;
        mCompositeSubscription = new CompositeSubscription();
        current_list = 0;
        collectInfoChangeBeanHouseList = new ArrayList<>();
        collectInfoChangeBeanEsateList = new ArrayList<>();
        systemMessageBeanList = new ArrayList<>();
        data = new ArrayList<>();

        initTab();

        data_list.setMenuCreator(new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(NewsActivity.this);
                deleteItem.setBackground(R.color.appBaseColor);
                deleteItem.setWidth(MyUtils.dip2px(NewsActivity.this,70));
                deleteItem.setTitle(R.string.conversationlist_btn);
                deleteItem.setTitleColor(Color.WHITE);
                deleteItem.setTitleSize(16);
                menu.addMenuItem(deleteItem);
            }
        });
        data_list.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        if (current_list != 2) {
                            mCompositeSubscription.add(ApiRequest.delCollectInfoChange(data.get(position).ID + "")
                                    .subscribe(result -> {
                                        if (!data.get(position).IsRead) {
                                            EventBus.getDefault().post(new NewsEvent(-1));
                                            refreshMsgNumber(current_list);
                                        }
                                        if (current_list == 0) {
                                            collectInfoChangeBeanHouseList.remove(data.get(position));
                                            data.remove(position);

                                        } else {
                                            collectInfoChangeBeanEsateList.remove(data.get(position));
                                            data.remove(position);
//                                            tab_bar.showMsg(current_list,--estMsgNumber);
                                            tab_bar.showDot(current_list);
                                        }
                                        adapter.notifyDataSetChanged();
                                        if (data.size() == 0) {
                                            showNoData(true);
                                            tab_bar.hideMsg(current_list);
                                        }
                                    }));
                        } else {
                            mCompositeSubscription.add(ApiRequest.delSystemMessage(systemMessageBeanList.get(position).getSystemMessageID()+"")
                                    .subscribe(integer -> {
                                        if (integer > 0) {
                                            if (!systemMessageBeanList.get(position).isIsRead()) {
                                                EventBus.getDefault().post(new NewsEvent(-1));
                                                refreshMsgNumber(current_list);
                                            }
                                            systemMessageBeanList.remove(position);
                                            systemAdapter.notifyDataSetChanged();
                                            if (systemMessageBeanList.size() == 0) {
                                                tab_bar.hideMsg(current_list);
                                                showNoData(true);
                                            }
                                        } else {
                                            toast("删除失败");
                                        }
                                    }));
                        }
                        break;
                }
                return true;
            }
        });

        data_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (current_list < 2) {
                    CollectInfoChangeBean bean = data.get(i);
                    if (!data.get(i).IsRead) {
                        mCompositeSubscription.add(ApiRequest.updateCollectInfoChangeIsRead(bean.CollectID + "")
                                .subscribe(result -> {
                                    data.get(i).IsRead = true;
                                    refreshMsgNumber(current_list);
                                    adapter.notifyDataSetChanged();
                                    EventBus.getDefault().post(new NewsEvent(-1));
                                }));
                    }
                    Intent intent = new Intent(NewsActivity.this, HouseDetail.class);
                    if ("xinfang".equals(bean.CollectInfos.getSource())) {
                        //新房
                        intent.putExtra(HouseDetailFragment.ESTEXT_ID_KEY, bean.CollectInfos.getCollectValue());
                        intent.putExtra(MapFragment.HOUSE_TYPE_KEY, MapFragment.HOUSE_TYPE_NEW);
                        startActivity(intent);
                    } else if ("zufang".equals(bean.CollectInfos.getSource())) {
                        //租房
                        intent.putExtra(MapFragment.HOUSE_TYPE_KEY, MapFragment.HOUSE_TYPE_RENT);
                        intent.putExtra(HouseDetailFragment.HOUSE_ID_KEY, bean.CollectInfos.getCollectValue());
                        startActivity(intent);
                    } else if ("ershoufang".equals(bean.CollectInfos.getSource())) {
                        //二手房
                        intent.putExtra(MapFragment.HOUSE_TYPE_KEY, MapFragment.HOUSE_TYPE_SECOND);
                        intent.putExtra(HouseDetailFragment.HOUSE_ID_KEY, bean.CollectInfos.getCollectValue());
                        startActivity(intent);
                    } else if ("xiaoqu".equals(bean.CollectInfos.getSource())) {
                        //小区
                        Intent intent1 = new Intent(NewsActivity.this, VillageDetail.class);
                        intent1.putExtra(VillageDetailFragment.ESTATE_CODE_KEY, bean.CollectInfos.getCollectValue());
                        startActivity(intent1);
                    }
                } else {
                    if (!systemMessageBeanList.get(i).isIsRead()) {
                        mCompositeSubscription.add(ApiRequest.updateSystemMessage(systemMessageBeanList.get(i).getSystemMessageID() + "")
                                .subscribe(result -> {
                                    systemMessageBeanList.get(i).setIsRead(true);
                                    refreshMsgNumber(current_list);
                                    systemAdapter.notifyDataSetChanged();
                                    EventBus.getDefault().post(new NewsEvent(-1));
                                }, throwable -> throwable.printStackTrace()));
                    }
                    //相应系统消息跳转,目前统一先到待约看页面
                    // TODO: 2017/2/22
                    switch (systemMessageBeanList.get(i).getType()) {
                        case 1:  //约看
                            Intent intent = new Intent(NewsActivity.this, LookAbout.class);
                            intent.putExtra(BaseFragmentActivity.FRAGMENT_TYPE, LookAbout.LOOK_TYPE_TO);
                            startActivity(intent);
                            break;
                        case 2:  //订阅
                            Intent intent1 = new Intent(NewsActivity.this, MySubscribeActivity.class);
                            if ("zufang".equals(systemMessageBeanList.get(i).getRemark())) {
                                intent1.putExtra(MySubscribeActivity.TAB, 1);
                            } else {
                                intent1.putExtra(MySubscribeActivity.TAB, 0);
                            }
                            startActivity(intent1);
                            break;
                        case 3:
                            break;
                    }
                }
            }
        });

        adapter = new NewsAdapter(this,data,R.layout.item_news);
        systemAdapter = new SystemMessageAdapter(this,systemMessageBeanList,R.layout.item_news);
        if (getIntent().getIntExtra("newType",0) == 2) {
            data_list.setAdapter(systemAdapter);
        } else {
            data_list.setAdapter(adapter);
        }

        showLoadingDialog();

        //加载消息数据
        mCompositeSubscription.add(Observable.combineLatest(ApiRequest.getCollectInfoChangeList(new HashMap() {
            {
                put("FirstIndex", "0");
                put("Count", "1000");
                put("UserId", DataHolder.getInstance().getUserId());
                put("CityCode", "021");
                put("IsDel", "false");
            }
        }), ApiRequest.getSystemMessageList(new HashMap() {
            {
                put("FirstIndex", "0");
                put("Count", "1000");
                put("UserId", DataHolder.getInstance().getUserId());
                put("CityCode", "021");
                put("IsDel", "0");
            }
        }), new Func2<BaseResult<CollectInfoChangeBean>, List<SystemMessageBean>, Boolean>() {
            @Override
            public Boolean call(BaseResult<CollectInfoChangeBean> t1, List<SystemMessageBean> t2) {
                cancelLoadingDialog();
                if (t1.Result != null && t1.Result.size() > 0) {
                    for (CollectInfoChangeBean bean : t1.Result) {
                        if (bean.CollectInfos != null && bean.CollectInfos.getSource().equals("xiaoqu")) {
                            collectInfoChangeBeanEsateList.add(bean);
                            if (!bean.IsRead) {
                                estMsgNumber++;
                            }
                        } else {
                            collectInfoChangeBeanHouseList.add(bean);
                            if (!bean.IsRead) {
                                postMsgNumber++;
                            }
                        }
                    }
                    if (current_list == 0) {
                        data.addAll(collectInfoChangeBeanHouseList);
                    } else {
                        data.addAll(collectInfoChangeBeanEsateList);
                    }

                    //加载房源信息
                    loadSource(collectInfoChangeBeanHouseList);
                    loadSource(collectInfoChangeBeanEsateList);
                }
                if (t2!=null && t2.size() > 0) {
                    for (SystemMessageBean bean:t2) {
                        if (!bean.isIsRead()) {
                            sysMsgNumber++;
                        }
                    }
                    systemMessageBeanList.addAll(t2);
                }
                return true;
            }
        }).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                cancelLoadingDialog();
                if (postMsgNumber > 0)
                    tab_bar.showDot(0);
//                    tab_bar.showMsg(0,postMsgNumber);

                if (estMsgNumber > 0)
                    tab_bar.showDot(1);
//                    tab_bar.showMsg(1,estMsgNumber);
                if (sysMsgNumber > 0)
                    tab_bar.showDot(2);
//                    tab_bar.showMsg(2,sysMsgNumber);

                if (current_list != 2) {
                    if (data.size() > 0) {
                        adapter.notifyDataSetChanged();
                        showNoData(false);
                    } else {
                        showNoData(true);
                    }
                } else {
                    if (systemMessageBeanList.size() > 0) {
                        systemAdapter.notifyDataSetChanged();
                        showNoData(false);
                    } else {
                        showNoData(true);
                    }
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                showNoData(true);
                cancelLoadingDialog();
                Logger.e(throwable.getMessage());
            }
        }));

//        mCompositeSubscription.add(ApiRequest.getCollectInfoChangeList(new HashMap(){
//            {
//                put("FirstIndex", "0");
//                put("Count","1000");
//                put("UserId", DataHolder.getInstance().getUserId());
//                put("CityCode", "021");
//                put("IsDel","false");
//            }
//        }).subscribe(new Action1<BaseResult<CollectInfoChangeBean>>() {
//            @Override
//            public void call(BaseResult<CollectInfoChangeBean> result) {
//                if (result.Result != null && result.Result.size() > 0) {
//                    for (CollectInfoChangeBean bean : result.Result) {
//                        if (bean.CollectInfos != null && bean.CollectInfos.getSource().equals("xiaoqu")) {
//                            collectInfoChangeBeanEsateList.add(bean);
//                        } else {
//                            collectInfoChangeBeanHouseList.add(bean);
//                        }
//                    }
//                    if (getIntent().getIntExtra("newType",0) == 0) {
//                        data.addAll(collectInfoChangeBeanHouseList);
//                    } else {
//                        data.addAll(collectInfoChangeBeanEsateList);
//                    }
//                    if (data.size() > 0) {
//                        adapter.notifyDataSetChanged();
//                        showNoData(false);
//                    } else {
//                        showNoData(true);
//                    }
//                    //加载房源信息
//                    loadSource(collectInfoChangeBeanHouseList);
//                    loadSource(collectInfoChangeBeanEsateList);
//
//                } else {
//                    cancelLoadingDialog();
//                    showNoData(true);
//                }
//            }
//        }, new Action1<Throwable>() {
//            @Override
//            public void call(Throwable throwable) {
//                showNoData(true);
//                cancelLoadingDialog();
//                Logger.e(throwable.getMessage());
//            }
//        }));
//
//        mCompositeSubscription.add(
//                ApiRequest.getSystemMessageList(new HashMap(){
//                    {
//                        put("FirstIndex", "0");
//                        put("Count","1000");
//                        put("UserId", DataHolder.getInstance().getUserId());
//                        put("CityCode", "021");
//                        put("IsDel","false");
//                    }
//                }).subscribe(systemMessageBeen -> {
//                    if (systemMessageBeen!=null && systemMessageBeen.size() > 0) {
//                        systemMessageBeanList.addAll(systemMessageBeen);
//                        if (getIntent().getIntExtra("newType",0)==2) {
//                            if (systemMessageBeanList.size() == 0) {
//                                systemAdapter.notifyDataSetChanged();
//                                showNoData(false);
//                            } else {
//                                showNoData(true);
//                            }
//                        }
//                    }
//                }, new Action1<Throwable>() {
//                    @Override
//                    public void call(Throwable throwable) {
//                        showNoData(true);
//                        cancelLoadingDialog();
//                        Logger.e(throwable.getMessage());
//                    }
//                })
//        );

    }

    /**
     * 刷新消息数量
     * @param tab
     */
    private void refreshMsgNumber(int tab) {
        if (tab == 0) {
            if (--postMsgNumber > 0) {
//                tab_bar.showMsg(tab, postMsgNumber);
                tab_bar.showDot(tab);
            } else {
                tab_bar.hideMsg(tab);
            }
        } else if (tab == 1) {
            if (--estMsgNumber > 0) {
//                tab_bar.showMsg(tab, estMsgNumber);
                tab_bar.showDot(tab);
            } else {
                tab_bar.hideMsg(tab);
            }
        } else {
            if (--sysMsgNumber > 0) {
                tab_bar.showDot(tab);
//                tab_bar.showMsg(tab, sysMsgNumber);
            } else {
                tab_bar.hideMsg(tab);
            }
        }
    }


    /**
     * 加载房源、新房、小区信息等
     */
    private void loadSource(List<CollectInfoChangeBean> list){
        showLoadingDialog();
        if (list.size() == 0) {
            return;
        }
        for (CollectInfoChangeBean bean:list) {
            if ("ershoufang".equals(bean.CollectInfos.getSource())) {
                queryPost(bean,"S",bean.CollectInfos.getCollectValue());
            } else if ("zufang".equals(bean.CollectInfos.getSource())) {
                queryPost(bean,"R",bean.CollectInfos.getCollectValue());
            } else if ("xinfang".equals(bean.CollectInfos.getSource())) {
                queryNewHouse(bean, bean.CollectInfos.getCollectValue());
            } else if ("xiaoqu".equals(bean.CollectInfos.getSource())) {
                queryVillage(bean, bean.CollectInfos.getCollectValue());
            }
        }
    }

    /**
     * 查询房源
     * @param bean
     * @param postType
     * @param id
     */
    private void queryPost(CollectInfoChangeBean bean, String postType, String id) {
        Map<String,String> params = new HashMap();
        params.put("PostId",id);
        params.put("PostType",postType);
        mCompositeSubscription.add(ApiRequest.getHouseDetailData(params)
                .subscribe(new Action1<HouseDetailBo>() {
                    @Override
                    public void call(HouseDetailBo houseDetailBo) {
                        cancelLoadingDialog();
                        if (houseDetailBo != null) {
                            bean.postTitle = houseDetailBo.getTitle();
                            adapter.notifyDataSetChanged();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        cancelLoadingDialog();
                        Logger.i(throwable.getMessage());
                    }
                }));
    }

    /**
     * 查询新房
     * @param bean
     * @param id
     */
    private void queryNewHouse(CollectInfoChangeBean bean, String id) {
        mCompositeSubscription.add(ApiRequest.getNewHouseDetail(id)
                .subscribe(new Action1<NewHouseDetail>() {
                    @Override
                    public void call(NewHouseDetail result) {
                        cancelLoadingDialog();
                        if (result != null) {
                            bean.postTitle = result.getAdName();
                            adapter.notifyDataSetChanged();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        cancelLoadingDialog();
                        Logger.i(throwable.getMessage());
                    }
                }));
    }

    /**
     * 查询小区
     * @param bean
     * @param code
     */
    private void queryVillage(CollectInfoChangeBean bean, String code) {
        Map<String,String> params = new HashMap<>();
        params.put("ImageWidth","1");
        params.put("ImageHeight","1");
        params.put("EstateCode",code);
        mCompositeSubscription.add(ApiRequest.getEstateByCode(params)
                .subscribe(new Action1<EstateBo>() {
                    @Override
                    public void call(EstateBo estateBo) {
                        cancelLoadingDialog();
                        bean.postTitle = estateBo.getEstateName();
                        adapter.notifyDataSetChanged();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        cancelLoadingDialog();
                        Logger.i(throwable.getMessage());
                    }
                }));
    }


    private void initTab() {
        mTabEntities = new ArrayList(){
            {
                add(new TabEntity("房源动态",R.drawable.benefit_sel,R.drawable.benefit));
                add(new TabEntity("小区动态",R.drawable.pan_sel,R.drawable.pan));
                add(new TabEntity("系统消息",R.drawable.pan_sel,R.drawable.pan));
            }
        };

        tab_bar.setTabData(mTabEntities);
        tab_bar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                data.clear();
                switch (position) {
                    case 0:
                        if (collectInfoChangeBeanHouseList.size() > 0) {
                            showNoData(false);
                        } else{
                            showNoData(true);
                        }
                        data.addAll(collectInfoChangeBeanHouseList);
                        current_list = position;
                        data_list.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        break;
                    case 1:
                        if (collectInfoChangeBeanEsateList.size() > 0) {
                            showNoData(false);
                        } else{
                            showNoData(true);
                        }
                        data.addAll(collectInfoChangeBeanEsateList);
                        current_list = position;
                        data_list.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        break;
                    case 2:
                        if (systemMessageBeanList.size() > 0) {
                            showNoData(false);
                        } else{
                            showNoData(true);
                        }
                        data_list.setAdapter(systemAdapter);
                        systemAdapter.notifyDataSetChanged();
                        current_list = position;
                        break;
                }
            }

            @Override
            public void onTabReselect(int position) {
            }
        });
        current_list = getIntent().getIntExtra("newType",0);
        tab_bar.setCurrentTab(current_list);
    }


    public void showNoData(boolean show) {
        if (show) {
            no_message_layout.setVisibility(View.VISIBLE);
        } else {
            no_message_layout.setVisibility(View.GONE);
        }
    }

    @Override
    protected IPresenter createPresenter() {
        return null;
    }

    @Override
    protected void onDestroy() {
        if (mCompositeSubscription != null && mCompositeSubscription.hasSubscriptions()) {
            mCompositeSubscription.unsubscribe();
        }
        super.onDestroy();
    }

    @Override
    protected void initToolbar() {
        StatusBarCompat.setStatusBarColor(this, Color.WHITE);
        toolbar.setNavigationOnClickListener((v)->onBackPressed());
        toolbar.setTitle("我的消息");
    }

    @Override
    protected String getTalkingDataPageName() {
        return "我的消息";
    }
}