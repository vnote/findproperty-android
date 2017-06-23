package com.cetnaline.findproperty.ui.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.base.BaseActivity;
import com.cetnaline.findproperty.db.entity.GScope;
import com.cetnaline.findproperty.db.entity.Staff;
import com.cetnaline.findproperty.db.entity.Store;
import com.cetnaline.findproperty.entity.bean.StaffListBean;
import com.cetnaline.findproperty.entity.event.LocationRequestEvent;
import com.cetnaline.findproperty.entity.event.NormalEvent;
import com.cetnaline.findproperty.entity.result.BaseResult;
import com.cetnaline.findproperty.inter.IRecycleViewListener;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.cetnaline.findproperty.ui.listadapter.AdviserListAdapter;
import com.cetnaline.findproperty.utils.DataHolder;
import com.cetnaline.findproperty.utils.DbUtil;
import com.cetnaline.findproperty.utils.LocationUtil;
import com.cetnaline.findproperty.utils.RxBus;
import com.cetnaline.findproperty.utils.SchedulersCompat;
import com.cetnaline.findproperty.utils.StatusBarCompat;
import com.cetnaline.findproperty.widgets.MRecyclerView;
import com.cetnaline.findproperty.widgets.SingleSelectListView;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by diaoqf on 2016/7/28.
 */
public class AdviserListActivity extends BaseActivity {

    public static final String RANGE = "range";
    public static final String SELECT_ADVISER = "select_adviser";

    @BindView(R.id.list)
    MRecyclerView mRecyclerView;
    @BindView(R.id.float_menu)
    ImageView float_menu;
    @BindView(R.id.datalist_load_layout)
    LinearLayout datalist_load_layout;
    @BindView(R.id.datalist_load_msg)
    TextView datalist_load_msg;
    @BindView(R.id.right_menu_scope_parent)
    SingleSelectListView right_menu_scope_parent;
    @BindView(R.id.right_menu_scope_sub)
    SingleSelectListView right_menu_scope_sub;

    @BindView(R.id.toolbar_search)
    EditText toolbar_search;

    @BindView(R.id.select_store_layout)
    LinearLayout select_store_layout;
    @BindView(R.id.select_menu)
    TextView select_menu;

    @BindView(R.id.store_select_layout)
    LinearLayout store_select_layout;
    @BindView(R.id.single_list_view)
    SingleSelectListView single_list_view;

    @BindView(R.id.store_list_view)
    SingleSelectListView store_list_view;

    @BindView(R.id.shade_cover)
    LinearLayout shade_cover; //输入键盘遮罩
    @BindView(R.id.home_container)
    DrawerLayout home_container;
    @BindView(R.id.content_layout)
    LinearLayout content_layout;

    private AdviserListAdapter mAdapter;

    private int loadMenuState;
    private int current_page;

    private String gScopeId;
    private String range;
    private String searchName;

    private int totalCount;

    private Resources resources;
    private CompositeSubscription mCompositeSubscription;

    private boolean isSelectAdviser;

    private String storeID;


    public CompositeSubscription getCompositeSubscription() {
        return mCompositeSubscription;
    }

    public boolean isSelectAdviser() {
        return isSelectAdviser;
    }

    private IRecycleViewListener iRecycleViewListener = new IRecycleViewListener() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void downRefresh() {
            //下拉刷新
            if (!"".equals(range)) {
                //请求定位权限
                ActivityCompat.requestPermissions(AdviserListActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            } else {
                loadData(true);
            }
        }

        @Override
        public void upRefresh() {
            loadData(false);
        }

        @Override
        public void onItemClick(int position) {
            if (isSelectAdviser) {
                Intent intent = new Intent();
                intent.putExtra("name",mAdapter.getAdviserList().get(position).getCnName());
                intent.putExtra("id",mAdapter.getAdviserList().get(position).getStaffNo());
                intent.putExtra("storeName",mAdapter.getAdviserList().get(position).getStoreName());
                intent.putExtra("storeId",mAdapter.getAdviserList().get(position).getStoreID());
                AdviserListActivity.this.setResult(RESULT_OK, intent);
                AdviserListActivity.this.finish();
            } else {
                Intent i = new Intent(AdviserListActivity.this, AdviserDetailActivity.class);
                i.putExtra(AdviserDetailActivity.ADVISER, mAdapter.getAdviserList().get(position));
                startActivity(i);
            }
        }

        @Override
        public void onScroll() {
        }

        @Override
        public void loadDataAgain() {

        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0 ) {
            if (grantResults != null && grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //请求权限，开始定位
                LocationUtil.start(1,true, getClass().getName());
            } else {
                toast("未获得定位权限，请设置“允许”后尝试");
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.act_adviserlist;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void init(Bundle savedInstanceState) {
        mCompositeSubscription = new CompositeSubscription();
        setSupportActionBar(adviser_tool_bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        adviser_tool_bar.setNavigationOnClickListener((v) -> onBackPressed());

        resources = getResources();
        loadMenuState = 0;
        totalCount = 0;
        gScopeId = "";
        storeID = "";
        current_page = 1;

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            range = intent.getExtras().getString(RANGE);
        } else {
            range = "";
        }
        searchName = "";

//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            toast("中原找房没有获取定位权限");
//        } else {
//            LocationUtil.start(1,true);
//        }

        mRecyclerView.setIRecycleViewListener(iRecycleViewListener);
        mRecyclerView.setDefaultText("");
        mAdapter = new AdviserListAdapter(new ArrayList<StaffListBean>(),R.layout.list_adviser_item, AdviserListActivity.this);
        mRecyclerView.setAdapter(mAdapter,"已显示全部顾问");
//        datalist_load_layout.setOnClickListener((v)->loadData(true));

        //搜索
        toolbar_search.setOnClickListener((v)->showMenu(false));
        toolbar_search.setOnEditorActionListener((textView,i,keyEvent)-> {
            if (i== EditorInfo.IME_ACTION_SEARCH ||(keyEvent!=null&&keyEvent.getKeyCode()== KeyEvent.KEYCODE_ENTER))
            {
                searchName = textView.getText().toString();
                range = "";
                gScopeId = "";
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
//                toolbar_search.setText("");
                //词频统计
                if (searchName != null && !searchName.equals("")) {
                    mCompositeSubscription.add(ApiRequest.wordFrequency("jingjiren", searchName)
                            .subscribe(new Action1<Integer>() {
                                @Override
                                public void call(Integer integer) {
                                    if (integer == 1) {
                                        Logger.i("词频统计已提交");
                                    }
                                }
                            },throwable -> {
                                throwable.printStackTrace();}));
                }

                loadData(true);
                return true;
            }
            return false;
        });
        mRecyclerView.startRefresh();

        isSelectAdviser = getIntent().getBooleanExtra(SELECT_ADVISER,false);
        if (isSelectAdviser) {
            home_container.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            select_store_layout.setVisibility(View.VISIBLE);
            select_menu.setOnClickListener(v->{
                if (store_select_layout.getVisibility() == View.VISIBLE) {
                    store_select_layout.setVisibility(View.GONE);
                } else {
                    store_select_layout.setVisibility(View.VISIBLE);
                }
            });
            store_select_layout.setOnClickListener(v->store_select_layout.setVisibility(View.GONE));
            float_menu.setVisibility(View.GONE);
            initSelectMenu();
            loadData(true);
        } else {
            float_menu.setOnClickListener((v)->showMenu(true));
            initMenu();
        }

        content_layout.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            content_layout.getWindowVisibleDisplayFrame(r);
            int screenHeight = content_layout.getRootView().getHeight();

            // r.bottom is the position above soft keypad or device button.
            // if keypad is shown, the r.bottom is smaller than that before.
            int keypadHeight = screenHeight - r.bottom;
            if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                if (shade_cover.getVisibility() == View.GONE) {
                    shade_cover.setVisibility(View.VISIBLE);
                }
            }
            else {
                if (shade_cover.getVisibility() == View.VISIBLE) {
                    shade_cover.setVisibility(View.GONE);
                }
            }
        });

        //定位成功监听
        mCompositeSubscription.add(
                RxBus.getDefault().toObservable(LocationRequestEvent.class)
                        .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(locationRequestEvent -> {
                    if (!locationRequestEvent.getRequestSource().equals(getClass().getName())) {
                        return;
                    }

                    if (locationRequestEvent.getRequestResult() == LocationRequestEvent.REQUEST_SUCCESS) {
                        loadData(true);
                    } else {
                        toast("未获得定位权限，请设置“允许”后尝试");
                    }
                },throwable -> throwable.printStackTrace()));

    }

    /**
     * 初始化店铺选择菜单
     */
    private void initSelectMenu() {
        final boolean[] showFirst = {false};
        Observable.just(DbUtil.getGScopeChild(21))
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(res -> {
                    List<String> names = new ArrayList<String>();
                    List<GScope> gScopes = new ArrayList<GScope>();
                    for (GScope scope:res) {
                        if (!scope.getGScopeName().equals("其他")) {
                            names.add(scope.getGScopeName());
                            gScopes.add(scope);
                        }
                    }
                    single_list_view.setData(names);
                    single_list_view.setHasImage(true);
                    store_list_view.setHasImage(false);
                    single_list_view.setOnSelectItemListener(position -> {
//                        mCompositeSubscription.add(ApiRequest.searchStoreSingle(new HashMap(){
//                            {
//                                put("GscopeID",gScopes.get(position).getGScopeId()+"");
//                                put("PageIndex","1");
//                                put("PageCount","200");
//                            }
//                        }).subscribe(new Action1<List<StoreBo>>() {
//                            @Override
//                            public void call(List<StoreBo> result) {
//                                if (result != null && result.size() > 0) {
//                                    List<String> stores = new ArrayList<String>();
//                                    for (StoreBo s : result) {
//                                        stores.add(s.getStoreName());
//                                    }
//                                    store_list_view.setData(stores);
//                                    store_list_view.setOnSelectItemListener(position1 -> {
//                                        storeID = result.get(position1).getStoreId() + "";
//                                        store_select_layout.setVisibility(View.GONE);
//                                        select_menu.setText(result.get(position1).getStoreName());
//                                        loadData(true);
//                                    });
//                                } else {
//                                    store_list_view.setData(new ArrayList<>());
//                                }
//                            }
//                        }, new Action1<Throwable>() {
//                            @Override
//                            public void call(Throwable throwable) {
//                                throwable.printStackTrace();
//                            }
//                        }));
                        mCompositeSubscription.add(Observable.just(DbUtil.getStoreByGscope(gScopes.get(position).getGScopeId()))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(result->{
                                    if (result != null && result.size() > 0) {
                                        List<String> stores = new ArrayList<String>();
                                        for (Store s:result) {
                                            stores.add(s.getStoreName());
                                        }
                                        store_list_view.setData(stores);
                                        store_list_view.setOnSelectItemListener(position1 -> {
                                            storeID = result.get(position1).getStoreId()+"";
                                            store_select_layout.setVisibility(View.GONE);
                                            select_menu.setText(result.get(position1).getStoreName());
                                            loadData(true);
                                        });
                                    } else {
                                        store_list_view.setData(new ArrayList<>());
                                    }
                                },throwable -> {
                                    throwable.printStackTrace();
                                }));
                    });
                    single_list_view.performItemClick(single_list_view.getChildAt(0), 0, single_list_view.getItemIdAtPosition(0));
                });
    }

    /**
     * 初始化菜单
     */
    private void initMenu(){
        Observable.just(DbUtil.getGScopeChild(21))
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe((gScopes)-> {
                    List<String> names = new ArrayList<String>();
                    List<String> rangeStrings = new ArrayList() {{
                            add("1公里");
                            add("3公里");
                            add("5公里");
                        }};
                    names.add("不限");
                    names.add("附近");

                    for (GScope gScope:gScopes) {
                        names.add(gScope.getGScopeName());
                    }
                    right_menu_scope_parent.setData(names);
                    right_menu_scope_parent.setHasImage(true);
                    right_menu_scope_sub.setHasImage(false);

                    right_menu_scope_parent.setOnSelectItemListener(position -> {
                        toolbar_search.setText("");
                        if (position == 0) {
                            //不限
                            right_menu_scope_sub.setVisibility(View.GONE);
                            showMenu(false);
                            if (!"".equals(gScopeId) || !"".equals(range) || !"".equals(searchName)) {
                                gScopeId = "";
                                searchName = "";
                                range = "";
                                loadData(true);
                            }
                        } else if (position == 1) {
                            //附近
                            right_menu_scope_sub.setVisibility(View.VISIBLE);
                            right_menu_scope_sub.setData(rangeStrings);
                            right_menu_scope_sub.setOnSelectItemListener(position1 -> {
                                showMenu(false);
                                gScopeId = "";
                                searchName = "";
                                switch (position1) {
                                    case 0:range = "1000"; break;
                                    case 1:range = "3000"; break;
                                    case 2:range = "5000"; break;
                                }
                                loadData(true);
                            });
                        } else {
                            right_menu_scope_sub.setVisibility(View.VISIBLE);
                            Observable.just(gScopes.get(position-2))
                                    .flatMap((gScope1) -> {
                                        List<GScope> subMenu = DbUtil.getGScopeChild(gScope1.getGScopeId());
                                        return Observable.just(subMenu);
                                    }).compose(SchedulersCompat.applyIoSchedulers())
                                    .subscribe(gScopes1 -> {
                                        List<String> subNames = new ArrayList<String>();
                                        subNames.add("不限");
                                        for (GScope sub: gScopes1) {
                                            subNames.add(sub.getGScopeName());
                                        }
                                        right_menu_scope_sub.setData(subNames);
                                        right_menu_scope_sub.setOnSelectItemListener(position1 -> {
                                            toolbar_search.setText("");
                                            if (position1 == 0) {
                                                gScopeId = gScopes.get(position-2).getGScopeId() + "";
                                            } else {
                                                gScopeId = gScopes1.get(position1-1).getGScopeId() + "";
                                            }
                                            range = "";
                                            searchName = "";
                                            showMenu(false);
                                            loadData(true);
                                        });
                                    },throwable -> throwable.printStackTrace());
                        }
                    });

                },throwable -> throwable.printStackTrace());
    }

    /**
     * 显示侧滑菜单
     * @param flag
     */
    private void showMenu(boolean flag){
        if (flag && !home_container.isDrawerOpen(Gravity.RIGHT)) {
            home_container.openDrawer(Gravity.RIGHT);
        }
        if (!flag && home_container.isDrawerOpen(Gravity.RIGHT)) {
            home_container.closeDrawer(Gravity.RIGHT);
        }
    }

    /**
     * 加载数据
     */
    private void loadData(boolean isReload){
        store_select_layout.setVisibility(View.GONE);
        if (isReload) {
            showLoadingDialog();
            totalCount = 0;
            current_page = 1;
            showDataListLoadMessage(false,null);
        }

        if (totalCount > 0 && totalCount <= mAdapter.getItemCount()) {
            cancelLoadingDialog();
            return;
        }
        mCompositeSubscription.add(ApiRequest.getStaffs(new HashMap<String, Object>() {
            {
                put("PageIndex", current_page);
                put("PageCount", "10");

                if (!"".equals(storeID)) {
                    put("StoreID",storeID+"");
                } else {
                    put("GScopeId", gScopeId);
                    if (!"".equals(range)) {
                        put("Radius", range==null?"3000":range);
                        put("Lng", DataHolder.getInstance().getLongitude());
                        put("Lat", DataHolder.getInstance().getLatitude());
                    }
                }

                if (!"".equals(searchName)) {
                    put("CnName","%"+searchName+"%");
                }
            }
    }).subscribe(new Observer<BaseResult<StaffListBean>>() {
        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
            e.printStackTrace();
            if (mAdapter.getItemCount() <= 0) {
                showDataListLoadMessage(true, resources.getString(R.string.adviserdetail_no_result));
            }
            stopRefresh(false,false);
            if (!"".equals(storeID)) {
                searchName = "";
            }
            cancelLoadingDialog();
        }

            @Override
            public void onNext(BaseResult<StaffListBean> staffListBeanBaseResult) {
                totalCount = staffListBeanBaseResult.Total;
                if (isReload) {
                    mAdapter.clear();
                }
                if (staffListBeanBaseResult != null &&
                        staffListBeanBaseResult.Result != null &&
                        staffListBeanBaseResult.Result.size() > 0) {
                    current_page++;
                    if (isReload){
                        stopRefresh(true,true);
                    } else {
                        if (totalCount - mAdapter.getItemCount() <= 10) {
                            stopRefresh(false,false);
                        } else {
                            stopRefresh(true,false);
                        }
                    }
                    mAdapter.add(staffListBeanBaseResult.Result);
                    cacheData(staffListBeanBaseResult.Result);
                } else {
                    if (mAdapter.getItemCount() <= 0) {
                        showDataListLoadMessage(true, resources.getString(R.string.adviserdetail_no_result));
                    }
                }
                mRecyclerView.refreshStatus();
                if (!"".equals(storeID)) {
                    searchName = "";
                }
                cancelLoadingDialog();
            }
        }));
    }

    /**
     * 缓存数据
     */
    private void cacheData(List<StaffListBean> listStaffBeen){
        if (listStaffBeen != null && listStaffBeen.size() > 0) {
            mCompositeSubscription.add(Observable.from(listStaffBeen)
                    .map((cmListStaffBean) -> {
                        Staff staff = new Staff();
                        staff.setUId(cmListStaffBean.StaffNo.toLowerCase());
                        staff.setMobile(cmListStaffBean.Mobile);
                        staff.setName(cmListStaffBean.CnName);
                        staff.setImageUrl(cmListStaffBean.StaffImg);
                        staff.setDepartmentName(cmListStaffBean.StoreName);
                        String number = cmListStaffBean.MobileBy400 != null ? cmListStaffBean.MobileBy400:cmListStaffBean.Staff400Tel;
                        staff.setStaff400Tel(number);
                        DbUtil.addStaff(staff);
                        return staff;
                    }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((staff) -> {
                    }));
        }
    }

    @Override
    protected IPresenter createPresenter() {
        return null;
    }

    @BindView(R.id.adviser_tool_bar)
    Toolbar adviser_tool_bar;

    @Override
    protected void initToolbar() {
        StatusBarCompat.setStatusBarColor(this, Color.WHITE);
        showToolbar(false);
    }

    @Override
    protected void onDestroy() {
        if (mCompositeSubscription != null && mCompositeSubscription.hasSubscriptions()) {
            mCompositeSubscription.unsubscribe();
        }
        super.onDestroy();
    }

    /**
     * 显示列表数据加载信息
     * @param flag
     * @param message
     */
    private void showDataListLoadMessage(boolean flag, String message) {
        if (flag) {
            datalist_load_msg.setText(message);
            mRecyclerView.setVisibility(View.GONE);
            datalist_load_layout.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            datalist_load_layout.setVisibility(View.GONE);
        }
    }

    private void stopRefresh(boolean hasMore, boolean toTop){
        mRecyclerView.stopRefresh(hasMore);
        mRecyclerView.toTopPosition(toTop);
    }

    @Override
    protected String getTalkingDataPageName() {
        return "经济人列表";
    }
}
