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
import com.cetnaline.findproperty.api.bean.ApiResponse;
import com.cetnaline.findproperty.api.bean.IntentionBo;
import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.base.BaseFragment;
import com.cetnaline.findproperty.entity.event.NormalEvent;
import com.cetnaline.findproperty.inter.IRecycleViewListener;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.cetnaline.findproperty.ui.activity.IntentSettingActivity;
import com.cetnaline.findproperty.ui.listadapter.SubscribePostListAdapter;
import com.cetnaline.findproperty.widgets.IndicatorView;
import com.cetnaline.findproperty.widgets.MRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import io.rong.eventbus.EventBus;
import rx.Subscriber;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by diaoqf on 2016/12/12.
 */

public class SubscribePostFragment extends BaseFragment{
    public static final int REQUEST_HOUSE_LIST_CODE = 1009;

    public static final String FLAG = "flag";
    public static final String POST = "ershoufang";
    public static final String RENT = "zufang";
    public static final String XINFANG = "xinfang";

    private static final int[] pages = new int[]{R.layout.subscribe_pager_item_1,
            R.layout.subscribe_pager_item_2, R.layout.subscribe_pager_item_3};

    public static SubscribePostFragment getInstance(String type){
        SubscribePostFragment fragment = new SubscribePostFragment();
        Bundle bundle = new Bundle();
        bundle.putString(FLAG, type);
        fragment.setArguments(bundle);
        return fragment;
    }

    private String current_type = POST;

    @BindView(R.id.list)
    MRecyclerView recyclerView;
    @BindView(R.id.no_subscribe)
    LinearLayout no_subscribe;
    @BindView(R.id.guide_vp)
    ViewPager guide_vp;
    @BindView(R.id.guide_iv)
    IndicatorView guide_iv;
    @BindView(R.id.guide_enter)
    AppCompatTextView guide_enter;

    private IRecycleViewListener iRecycleViewListener = new IRecycleViewListener() {
        @Override
        public void downRefresh() {
            //下拉刷新
            loadData(true);
        }

        @Override
        public void upRefresh() {
            loadData(false);
        }

        @Override
        public void onItemClick(int position) {

        }

        @Override
        public void onScroll() {
        }

        @Override
        public void loadDataAgain() {

        }
    };

    private SubscribePostListAdapter mAdapter;
    private boolean isAllLoad;
    private CompositeSubscription mCompositeSubscription;
    private int current_page;
    private int totalCount;

    /**
     * 读取数据
     * @param isReload
     */
    private void loadData(boolean isReload) {

        if (isReload) {
            totalCount = 0;
            current_page = 1;
            isAllLoad = false;
        }

        if (isAllLoad) {
            return;
        }

        mCompositeSubscription.add(ApiRequest.getIntentionList(new HashMap(){
            {
                put("Source",current_type);
                put("FirstIndex",""+current_page);
                put("Count", "10");
            }
        }).subscribe(new Subscriber<ApiResponse<List<IntentionBo>>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                stopRefresh(false,false);
                if (current_page == 1) {
                    showNoData(true);
                } else {
                    toast("数据获取超时，请稍后尝试！");
                }
                isAllLoad = true;
            }

            @Override
            public void onNext(ApiResponse<List<IntentionBo>> listApiResponse) {
                if (isReload) {
                    mAdapter.clear();
                }
                totalCount = listApiResponse.getTotal();
                if (listApiResponse.getResult() != null && listApiResponse.getResult().size() > 0) {
                    showNoData(false);
                    current_page++;
                    mAdapter.add(listApiResponse.getResult());
                    if (isReload){
                        stopRefresh(true,true);
                    } else {
                        if (totalCount - mAdapter.getItemCount() == 0) {
                            stopRefresh(false,false);
                        } else {
                            stopRefresh(true,false);
                        }
                    }
                } else {
                    if (current_page == 1) {
                        showNoData(true);
                    }
                    isAllLoad = true;
                }
            }
        }));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frg_subscribe;
    }

    @Override
    protected void init() {
        EventBus.getDefault().register(this);
        current_type = getArguments().getString(FLAG);
        isAllLoad = false;
        mCompositeSubscription = new CompositeSubscription();
        current_page = 1;

        recyclerView.setIRecycleViewListener(iRecycleViewListener);
        recyclerView.setDefaultText("");
        mAdapter = new SubscribePostListAdapter(new ArrayList<>(),R.layout.item_subscribe, current_type, getActivity());
        mAdapter.setOnCancelSubscribe((position, optionSuccess) -> {
            if (optionSuccess) {
                loadData(true);
                toast("取消订阅成功");
            } else {
                toast("取消订阅失败");
            }
        });
        recyclerView.setAdapter(mAdapter,"已显示全部意向");

        recyclerView.startRefresh();
    }

    /***
     * 无订阅数据
     * @param flag
     */
    private void showNoData(boolean flag){
        if (flag) {
            recyclerView.setVisibility(View.GONE);
            no_subscribe.setVisibility(View.VISIBLE);
            initGuide();
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            no_subscribe.setVisibility(View.GONE);
        }
    }

    private void stopRefresh(boolean hasMore, boolean toTop){
        recyclerView.stopRefresh(hasMore);
        recyclerView.toTopPosition(toTop);
    }

    private void initGuide() {
        guide_vp.setPageTransformer(true, new DepthPageTransformer());
        guide_vp.setAdapter(new GuideAdapter());
        guide_iv.setViewPager(guide_vp);
        guide_enter.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(),IntentSettingActivity.class);
            if (current_type.equals(POST)) {
                intent.putExtra(MapFragment.HOUSE_TYPE_KEY, MapFragment.HOUSE_TYPE_SECOND);
            } else if (current_type.equals(RENT)) {
                intent.putExtra(MapFragment.HOUSE_TYPE_KEY, MapFragment.HOUSE_TYPE_RENT);
            } else if (current_type.equals(XINFANG)) {
                intent.putExtra(MapFragment.HOUSE_TYPE_KEY, MapFragment.HOUSE_TYPE_NEW);
            }
            intent.putExtra("justClose",true);
            getActivity().startActivityForResult(intent,REQUEST_HOUSE_LIST_CODE);
        });
    }

    @Override
    protected IPresenter createPresenter() {
        return null;
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (mCompositeSubscription != null && mCompositeSubscription.hasSubscriptions()) {
            mCompositeSubscription.unsubscribe();
        }
        super.onDestroy();
    }

    public void onEventMainThread(NormalEvent event) {
        if (event.type == NormalEvent.INTENT_CHANGE) {  // && event.data.equals(current_type)
            loadData(true);
        }
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
            View view = inflater.inflate(pages[position],null);
            container.addView(view);
            return view;
        }
    }
}
