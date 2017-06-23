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
import com.cetnaline.findproperty.api.bean.NewHouseListBo;
import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.base.BaseFragment;
import com.cetnaline.findproperty.entity.bean.CollectionBean;
import com.cetnaline.findproperty.entity.result.BaseResult;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.cetnaline.findproperty.ui.activity.HouseDetail;
import com.cetnaline.findproperty.ui.listadapter.CollectionNewListAdapter;
import com.cetnaline.findproperty.utils.DataHolder;
import com.cetnaline.findproperty.utils.MyUtils;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * 新房收藏
 * Created by diaoqf on 2016/8/20.
 */
public class CollectionNewHouseFragment extends BaseFragment {
    @BindView(R.id.data_list)
    SwipeMenuListView data_list;
    @BindView(R.id.no_data_layout)
    LinearLayout no_data_layout;

    private CollectionNewListAdapter adapter;

    private List<NewHouseListBo> datas;

    private List<CollectionBean> collections;

    private int current_page = 1;
    private boolean isAllLoaded = false;

    private CompositeSubscription mCompositeSubscription;

    @Override
    protected int getLayoutId() {
        return R.layout.frg_collection;
    }

    @Override
    protected void init() {
        mCompositeSubscription = new CompositeSubscription();
        collections = new ArrayList<>();
        datas = new ArrayList<>();
        adapter = new CollectionNewListAdapter(getActivity(),R.layout.item_post,datas);

        data_list.setAdapter(adapter);
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
                        String id = datas.get(position).getEstExtId();
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
                intent.putExtra(HouseDetailFragment.ESTEXT_ID_KEY, datas.get(i).getEstExtId());
                intent.putExtra(MapFragment.HOUSE_TYPE_KEY, MapFragment.HOUSE_TYPE_NEW);
                startActivityForResult(intent,1001);
            }
        });

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
                put("Source","loupan");
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
                            mCompositeSubscription.add(ApiRequest.getNewHouseByIds(ids.substring(0, ids.length() - 1))
                                    .subscribe(result -> {
                                        cancelLoadingDialog();
                                        if ((result.getResult() == null || result.getResult().size() == 0) && isAllLoaded == false) {
                                            showMessage(true);
                                        }
                                        List<NewHouseListBo> list = result.getResult();
                                        datas.addAll(list);
                                        adapter.notifyDataSetChanged();
                                        current_page++;
                                        showMessage(false);
                                        if (result.getTotal() <= datas.size()) {
                                            isAllLoaded = true;
//                                            toast("已读取所有数据");
                                        }
                                    }, throwable -> {
                                        cancelLoadingDialog();
                                        showMessage(true);
                                        Logger.i(throwable.getMessage());
                                    }));
                        } else {
                            cancelLoadingDialog();
                            showMessage(true);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        cancelLoadingDialog();
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
//        if (event.type.equals(CancelCollectionEvent.NEW)) {
//            if (!event.saveCollection && event.value != null) {
//                for (NewHouseListBo bean : datas) {
//                    if (bean.getEstExtId().equals(event.value)) {
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
