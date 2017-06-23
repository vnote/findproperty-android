package com.cetnaline.findproperty.ui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.bean.ApiResponse;
import com.cetnaline.findproperty.api.bean.HouseBo;
import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.base.BaseFragment;
import com.cetnaline.findproperty.entity.bean.CollectionBean;
import com.cetnaline.findproperty.entity.result.BaseResult;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.cetnaline.findproperty.ui.activity.HouseDetail;
import com.cetnaline.findproperty.ui.listadapter.CollectionAdapter;
import com.cetnaline.findproperty.utils.DataHolder;
import com.cetnaline.findproperty.utils.MyUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by diaoqf on 2016/8/19.
 */
public class CollectionPostsFragment extends BaseFragment {

    public static final String SHOW_TYPE = "show_type";
    @BindView(R.id.data_list)
    SwipeMenuListView data_list;
    @BindView(R.id.no_data_layout)
    LinearLayout no_data_layout;

    private CollectionAdapter adapter;

    private List<CollectionBean> collections;

    private List<HouseBo> datas;

    private int current_page = 1;
    private boolean isAllLoaded = false;
    private String type;

    private CompositeSubscription mCompositeSubscription;

    @Override
    protected int getLayoutId() {
        return R.layout.frg_collection;
    }

    @Override
    protected void init() {
        collections = new ArrayList<>();
        mCompositeSubscription = new CompositeSubscription();

        type = getArguments().getString(SHOW_TYPE);
        datas = new ArrayList<>();
        adapter = new CollectionAdapter(getActivity(), datas, R.layout.item_post);
        data_list.setMenuCreator(new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity());
                deleteItem.setBackground(R.color.appBaseColor);
                deleteItem.setWidth(MyUtils.dip2px(getActivity(),70));
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
                        String id = datas.get(position).getPostId();
                        long collectId=-1;
                        for (CollectionBean bean: collections) {
                            if (bean.getCollectValue().equals(id)) {
                                collectId = bean.getCollectID();
                                break;
                            }
                        }

                        ApiRequest.deleteCollect(collectId)
                                .subscribe(result -> {
                                    datas.remove(position);
                                    collections.remove(position);
                                    adapter.notifyDataSetChanged();
                                    if (datas.size() == 0) {
                                        showMessage(true);
                                    }
                                });
                        break;
                }
                return false;
            }
        });

        data_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), HouseDetail.class);
                if ("ershoufang".equals(type)) {
                    intent.putExtra(MapFragment.HOUSE_TYPE_KEY, MapFragment.HOUSE_TYPE_SECOND);
                } else {
                    intent.putExtra(MapFragment.HOUSE_TYPE_KEY, MapFragment.HOUSE_TYPE_RENT);
                }
                HouseBo houseBo = datas.get(i);
                intent.putExtra(HouseDetailFragment.HOUSE_ID_KEY, houseBo.getPostId());
                startActivityForResult(intent,1001);
            }
        });

        data_list.setAdapter(adapter);
        loadData(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == 1) {
            loadData(true);
        }
    }

    private void loadData(boolean isReload){
        showLoadingDialog();
        if (isReload) {
            current_page = 1;
            isAllLoaded = false;
            datas.clear();
        }
        if (isAllLoaded) {
//            toast("已读取所有数据");
            return;
        }

        mCompositeSubscription.add(ApiRequest.getCollectList(new HashMap(){
            {
                put("Source",type);
                put("UserId", DataHolder.getInstance().getUserId());
                put("FirstIndex",current_page+"");
                put("Count","1000");
            }
        }).subscribe(new Action1<BaseResult<CollectionBean>>() {
                    @Override
                    public void call(BaseResult<CollectionBean> collectionBeanBaseResult) {
                        if (collectionBeanBaseResult.Result != null) {
                            String ids = "";
                            for (CollectionBean bean : collectionBeanBaseResult.Result) {
                                ids += bean.getCollectValue() + ",";
                                collections.add(bean);
                            }
                            final String finalIds = ids.substring(0, ids.length() - 1);
                            mCompositeSubscription.add(ApiRequest.getMultiplePost(new HashMap() {
                                {
                                    put("PostIds", finalIds);
                                    put("ImageWidth", "200");
                                    put("ImageHeight", "200");
                                }
                            }).subscribe(new Action1<ApiResponse<List<HouseBo>>>() {
                                        @Override
                                        public void call(ApiResponse<List<HouseBo>> listApiResponse) {
                                            cancelLoadingDialog();
                                            if ((listApiResponse.getResult() == null || listApiResponse.getResult().size() == 0) && isAllLoaded == false) {
                                                showMessage(true);
                                            }
                                            datas.addAll(listApiResponse.getResult());
                                            adapter.notifyDataSetChanged();
                                            current_page++;
                                            showMessage(false);
                                            if (listApiResponse.getTotal() <= datas.size()) {
                                                isAllLoaded = true;
//                                                toast("已读取所有数据");
                                            }
                                        }
                                    }, new Action1<Throwable>() {
                                        @Override
                                        public void call(Throwable throwable) {
                                            cancelLoadingDialog();
                                            throwable.printStackTrace();
                                            showMessage(true);
                                        }
                                    }));
                        } else {
                            cancelLoadingDialog();
                            showMessage(true);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        cancelLoadingDialog();
                        throwable.printStackTrace();
                        showMessage(true);
                    }
                }));
    }

    private void showMessage(boolean show) {
        if (show) {
            no_data_layout.setVisibility(View.VISIBLE);
            data_list.setVisibility(View.GONE);
        } else {
            no_data_layout.setVisibility(View.GONE);
            data_list.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        if (mCompositeSubscription != null && mCompositeSubscription.hasSubscriptions()) {
            mCompositeSubscription.unsubscribe();
        }
        super.onDestroy();
    }

//    public void onEventMainThread(CancelCollectionEvent event){
//        if (event.type.equals(type)) {
//            if (!event.saveCollection && event.value != null) {
//                for (HouseBo bean : datas) {
//                    if (bean.getPostId().equals(event.value)) {
//                        datas.remove(bean);
//                        collections.remove(bean);
//                        adapter.notifyDataSetChanged();
//                        if (datas.size() == 0) {
//                            showMessage(true);
//                        }
//                        break;
//                    }
//                }
//            } else {
//                loadData(true);
//            }
//        }
//    }

    @Override
    protected IPresenter createPresenter() {
        return null;
    }
}
