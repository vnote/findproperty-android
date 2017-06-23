package com.cetnaline.findproperty.ui.fragment;

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
import com.cetnaline.findproperty.api.bean.ExerciseListBo;
import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.base.BaseFragment;
import com.cetnaline.findproperty.entity.result.BaseResult;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.cetnaline.findproperty.entity.bean.BookingBean;
import com.cetnaline.findproperty.ui.activity.HouseDetail;
import com.cetnaline.findproperty.ui.activity.NewsActivity;
import com.cetnaline.findproperty.ui.listadapter.BookingAdapter;
import com.cetnaline.findproperty.utils.MyUtils;
import com.cetnaline.findproperty.widgets.LoadingLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import rx.Subscriber;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by diaoqf on 2016/8/24.
 */
public class BookingFragment extends BaseFragment {
    @BindView(R.id.data_list)
    SwipeMenuListView data_list;
    @BindView(R.id.no_data_layout)
    LinearLayout no_data_layout;
    @BindView(R.id.loading_layout)
    LoadingLayout loadingLayout;

    BookingAdapter adapter;

    private CompositeSubscription mCompositeSubscription;

    public static final String DATA_TYPE = "data_type";

    public static final String ACTIVE = "active";
    public static final String OVERDUE = "overdue";

    private List<BookingBean> data;
    private List<ExerciseListBo> exerciseListBoList;
    private String dataType;

    public static BookingFragment getInstance(String dataType){
        BookingFragment bookingFragment = new BookingFragment();
        Bundle bundle = new Bundle();
        bundle.putString(DATA_TYPE, dataType);
        bookingFragment.setArguments(bundle);

        return bookingFragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frg_booking;
    }

    @Override
    protected void init() {
        mCompositeSubscription = new CompositeSubscription();
        dataType = getArguments().getString(DATA_TYPE,ACTIVE);
        data = new ArrayList<>();
        exerciseListBoList = new ArrayList<>();
        adapter = new BookingAdapter(getActivity(),exerciseListBoList,R.layout.item_booking);
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
                        ExerciseListBo bean = exerciseListBoList.get(position);
                        for (BookingBean bookingBean:data) {
                            if (bookingBean.getActId() == Integer.parseInt(bean.getActId())) {
                                mCompositeSubscription.add(ApiRequest.delUserBooking(bookingBean.getBookingId()+"")
                                        .subscribe(new Subscriber<Integer>() {
                                            @Override
                                            public void onCompleted() {}

                                            @Override
                                            public void onError(Throwable e) {}

                                            @Override
                                            public void onNext(Integer integer) {
                                                exerciseListBoList.remove(position);
                                                data.remove(bookingBean);
                                                adapter.notifyDataSetChanged();
                                                toast("活动已删除");
                                            }
                                        }));
                                break;
                            }
                        }
                        break;
                }
                return false;
            }
        });

        data_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (dataType.equals(ACTIVE)) {
                    Intent intent = new Intent(getActivity(), HouseDetail.class);
                    intent.putExtra(HouseDetailFragment.ESTEXT_ID_KEY, exerciseListBoList.get(i).getEstExtId());
                    intent.putExtra(MapFragment.HOUSE_TYPE_KEY, MapFragment.HOUSE_TYPE_NEW);
                    getActivity().startActivity(intent);
                }
            }
        });
        loadData(true);
    }

    /**
     * 加载数据
     */
    private void loadData(boolean isReload){

        if (isReload) {
            data.clear();

        }

        mCompositeSubscription.add(ApiRequest.getUserBookingList(new HashMap(){
            {
                put("FirstIndex","1");
                put("Count","1000");
            }
        }).subscribe(new Subscriber<BaseResult<BookingBean>>() {
            @Override
            public void onCompleted() {}

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                loadingLayout.setVisibility(View.GONE);
                showNoDataLayout(true);
            }

            @Override
            public void onNext(BaseResult<BookingBean> bookingBeanBaseResult) {
                if (bookingBeanBaseResult.Result != null && bookingBeanBaseResult.Result.size()>0) {
                    Date date = new Date();
                    date.getTime();
                    String ids = "";
                    for (BookingBean bean:bookingBeanBaseResult.Result) {
                        ids += bean.getActId() + ",";
                    }
                    ids = ids.substring(0,ids.length()-1);
                    if (!ids.equals("")) {
                        mCompositeSubscription.add(ApiRequest.getExerciseListByIds(ids)
                                .map(new Func1<List<ExerciseListBo>, List<ExerciseListBo>>() {
                                    @Override
                                    public List<ExerciseListBo> call(List<ExerciseListBo> exerciseListBos) {
                                        //过滤有效记录
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        List<ExerciseListBo> tmp = new ArrayList<ExerciseListBo>();
                                        for (ExerciseListBo bean: exerciseListBos) {
                                            try {
                                                Date endDate = sdf.parse(bean.getEndDate());
                                                //有效活动
                                                if (System.currentTimeMillis() <= endDate.getTime() && dataType.equals(ACTIVE)) {
                                                    tmp.add(bean);
                                                }

                                                //过期活动
                                                if (System.currentTimeMillis() > endDate.getTime() && dataType.equals(OVERDUE)) {
                                                    tmp.add(bean);
                                                }

                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        return tmp;
                                    }
                                })
                                .subscribe(new Subscriber<List<ExerciseListBo>>() {
                                    @Override
                                    public void onCompleted() {}

                                    @Override
                                    public void onError(Throwable e) {
                                        e.printStackTrace();
                                        showNoDataLayout(true);
                                    }

                                    @Override
                                    public void onNext(List<ExerciseListBo> exerciseListBos) {
                                        if (exerciseListBos != null && exerciseListBos.size() > 0) {
                                            exerciseListBoList.addAll(exerciseListBos);
                                            adapter.notifyDataSetChanged();
                                            showNoDataLayout(false);
                                        } else {
                                            showNoDataLayout(true);
                                        }
                                    }
                                }));
                    }
                    data.addAll(bookingBeanBaseResult.Result);
                } else {
                    showNoDataLayout(true);
                }
                loadingLayout.setVisibility(View.GONE);
            }
        }));
    }

    @Override
    public void onDestroy() {
        if (mCompositeSubscription != null && mCompositeSubscription.hasSubscriptions()) {
            mCompositeSubscription.unsubscribe();
        }
        super.onDestroy();
    }

    private void showNoDataLayout(boolean flag) {
        if (flag) {
            no_data_layout.setVisibility(View.VISIBLE);
        } else {
            no_data_layout.setVisibility(View.GONE);
        }
    }

    @Override
    protected IPresenter createPresenter() {
        return null;
    }
}
