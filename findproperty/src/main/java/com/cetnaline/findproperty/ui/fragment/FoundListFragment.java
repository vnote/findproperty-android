package com.cetnaline.findproperty.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.bean.Discount;
import com.cetnaline.findproperty.base.BaseFragment;
import com.cetnaline.findproperty.db.entity.DropBo;
import com.cetnaline.findproperty.inter.IRecycleViewListener;
import com.cetnaline.findproperty.api.NetContents;
import com.cetnaline.findproperty.presenter.impl.FoundPresenter;
import com.cetnaline.findproperty.presenter.ui.FoundContract;
import com.cetnaline.findproperty.ui.activity.WebActivity;
import com.cetnaline.findproperty.utils.MyUtils;
import com.cetnaline.findproperty.utils.glide.GlideLoad;
import com.cetnaline.findproperty.widgets.MRecyclerView;
import com.cetnaline.findproperty.widgets.dropdown.DropListener;
import com.cetnaline.findproperty.widgets.dropdown.SingleDrop;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 发现-优惠
 * Created by lebro on 2016/12/18.
 */

public class FoundListFragment extends BaseFragment<FoundPresenter> implements FoundContract.View  {

    @BindView(R.id.discount_rv_list)
    MRecyclerView discount_rv_list;

    @BindView(R.id.home_ll_emptyview)
    LinearLayout mEmptyView;

    @BindView(R.id.home_btn_retry)
    Button mBtnRetry;

    @BindView(R.id.atv_drop_exercise_1)
    TextView atv_drop_exercise_1;

    @BindView(R.id.atv_drop_topic_1)
    TextView atv_drop_topic_1;

    @BindView(R.id.ll_drop_exercise)
    LinearLayout ll_drop_exercise;

    @BindView(R.id.ll_drop_topic)
    LinearLayout ll_drop_topic;

    @BindView(R.id.atv_drop_topic)
    AppCompatCheckedTextView atv_drop_topic;

    @BindView(R.id.atv_drop_exercise)
    AppCompatCheckedTextView atv_drop_exercise;

    @OnClick(R.id.ll_drop_exercise)
    public void exerciseClick(){
        atv_drop_exercise.setChecked(true);
        exerciseDrop.show();
    }

    @OnClick(R.id.ll_drop_topic)
    public void topicClick(){
        atv_drop_topic.setChecked(true);
        topicDrop.show();
    }

    private int mPageIndex = 1;
    private int iRefreshType;

    private Map<String, String> param;

    private List<Discount> discounts;

    private FoundListFragment.DiscountAdapter adapter;

    private IRecycleViewListener iRecycleViewListener = new IRecycleViewListener() {
        @Override
        public void downRefresh() {
//            showLoadingDialog();
            mPageIndex = 1;
            iRefreshType = 0;
            param.put("pageIndex", mPageIndex+"");
            mPresenter.getDiscountList(param);
        }

        @Override
        public void upRefresh() {
            mPageIndex++;
            iRefreshType = 1;
            param.put("pageIndex", mPageIndex+"");
            mPresenter.getDiscountList(param);
        }

        @Override
        public void onItemClick(int position) {
            if (!MyUtils.openActivityForUrl(getActivity(),discounts.get(position).getUrl(), false)) {
                Intent intent = new Intent(getActivity(), WebActivity.class);
                intent.putExtra(WebActivity.TARGET_URL, discounts.get(position).getUrl());
                intent.putExtra(WebActivity.WEB_SHARE_KEY, true);
                startActivity(intent);
            }
        }

        @Override
        public void onScroll() {
        }

        @Override
        public void loadDataAgain() {
            mPresenter.getDiscountList(param);
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.frg_found_list;
    }

    @Override
    protected void init() {
        exerciseDrop = new SingleDrop(ll_drop_exercise, getActivity(), null);
        topicDrop = new SingleDrop(ll_drop_topic, getActivity(), null);

        exerciseDrop.setDropListener(dropListener1);
        topicDrop.setDropListener(dropListener2);

        discounts = new ArrayList<>();

        discount_rv_list.setIRecycleViewListener(iRecycleViewListener);
        adapter = new FoundListFragment.DiscountAdapter(getActivity(), R.layout.item_discount_list, discounts);
        discount_rv_list.setAdapter(adapter, "已显示全部优惠", 20);

        param = new HashMap<>();
        param.put("pageIndex", mPageIndex+"");
        param.put("pageCount", 10+"");
        discount_rv_list.startRefresh();
        mPresenter.getExerciseMenu("活动");
        mPresenter.getTopicMenu("专题");

        atv_drop_exercise_1.setOnClickListener(v->{
            setColor(2);
            mPageIndex = 1;
            discount_rv_list.toTopPosition(true);
            this.discounts.clear();
            param.put("pageIndex", "1");
            param.put("pageCount", 10+"");
            param.put("groupType", "活动");

            discount_rv_list.startRefresh();
//            showLoadingDialog();
//            mPresenter.getDiscountList(param);
        });

        atv_drop_topic_1.setOnClickListener(v->{
            setColor(3);
            mPageIndex = 1;
            discount_rv_list.toTopPosition(true);
            this.discounts.clear();
            param.put("pageIndex", "1");
            param.put("pageCount", 10+"");
            param.put("groupType", "专题");

            discount_rv_list.startRefresh();
//            showLoadingDialog();
//            mPresenter.getDiscountList(param);
        });
    }

    @Override
    protected FoundPresenter createPresenter() {
        return new FoundPresenter();
    }

    @Override
    public void setExerciseMenu(List<DropBo> exerciseList) {
        exerciseDrop.init(exerciseList);
    }

    @Override
    public void setTopicMenu(List<DropBo> topicList) {
        topicDrop.init(topicList);
    }
    @Override
    public void setDiscountList(List<Discount> discountList) {
        if (iRefreshType==0){
            this.discounts.clear();
        }

        if (discountList==null || discountList.size()==0){
            discount_rv_list.setDefaultText("暂无数据");
            this.discounts.clear();
//            discount_rv_list.setLoadDataBtVisible(View.GONE);
            discount_rv_list.setDefaultLogo(R.drawable.ic_no_house);
            discount_rv_list.stopRefresh(false);
        }else {
            discounts.addAll(discountList);
            discount_rv_list.stopRefresh(true);
        }
    }

    @Override
    public void netWorkException() {
        discount_rv_list.setDefaultText("网络不给力");
        discount_rv_list.setDefaultLogo(R.drawable.ic_no_network);
        discount_rv_list.setLoadDataBtVisible(View.VISIBLE);
    }

    @Override
    public void noData() {
        discount_rv_list.stopRefresh(false);
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

    public class DiscountAdapter extends CommonAdapter<Discount> {

        private DrawableRequestBuilder<String> requestBuilder;

        public DiscountAdapter(Activity context, int layoutId, List<Discount> datas) {
            super(context, layoutId, datas);
            requestBuilder = GlideLoad.init(context);
        }

        @Override
        protected void convert(ViewHolder holder, Discount discount, int position) {
            holder.setText(R.id.item_home_tv_title, discount.getTitle());
            holder.setText(R.id.item_home_tv_subtitle, discount.getSubtitle());
            holder.setText(R.id.item_discount_group, discount.getGroup());
            holder.setText(R.id.item_discount_tags, discount.getTags());
            ImageView item_home_iv_pic = holder.getView(R.id.item_home_iv_pic);
            GlideLoad.load(new GlideLoad.Builder(requestBuilder, NetContents.HOME_PRIVILEGE + discount.getImg())
                    .into(item_home_iv_pic));
        }
    }

    private SingleDrop exerciseDrop, topicDrop;

    private DropListener dropListener1 = new DropListener() {
        @Override
        public void dropComplete(boolean fromMore, int type, DropBo... dropBos) {
            atv_drop_exercise.setChecked(false);
            DropBo dropType = dropBos[0];
            int currentType = dropType.getType();
            if (currentType==-1){
                param.put("groupType", "活动");
                param.remove("tagsValue");
                atv_drop_exercise.setText("活动");
            }else {
                param.put("groupType", "活动");
                param.put("tagsValue", dropType.getValue());
                atv_drop_exercise.setText(dropType.getText());
            }
            atv_drop_topic.setText("专题");
            atv_drop_topic.setTextColor(Color.BLACK);
            atv_drop_exercise.setTextColor(Color.RED);
            topicDrop.resetSelectStatus();
            discount_rv_list.startRefresh();
        }

        @Override
        public void dropDismiss(boolean isSelected) {
            atv_drop_exercise.setChecked(false);
        }
    };

    private DropListener dropListener2 = new DropListener() {
        @Override
        public void dropComplete(boolean fromMore, int type, DropBo... dropBos) {

            atv_drop_topic.setChecked(false);
            DropBo dropType = dropBos[0];

            int currentType = dropType.getType();
            if (currentType==-1){
                param.put("groupType", "专题");
                param.remove("tagsValue");
                atv_drop_topic.setText("专题");
            }else {
                param.put("groupType", "专题");
                param.put("tagsValue", dropType.getValue());
                atv_drop_topic.setText(dropType.getText());
            }
            exerciseDrop.resetSelectStatus();
            atv_drop_exercise.setText("活动");
            atv_drop_topic.setTextColor(Color.RED);
            atv_drop_exercise.setTextColor(Color.BLACK);
            discount_rv_list.startRefresh();
        }

        @Override
        public void dropDismiss(boolean isSelected) {
            atv_drop_topic.setChecked(false);
        }
    };

    @OnClick(R.id.ll_drop_all)
    public void getAll(){
//        showLoadingDialog();
        setColor(1);
        param.remove("groupType");
        param.remove("tagsValue");
        atv_drop_topic.setText("专题");
        atv_drop_topic.setTextColor(Color.BLACK);
        atv_drop_exercise.setText("活动");
        atv_drop_exercise.setTextColor(Color.BLACK);
        exerciseDrop.resetSelectStatus();
        topicDrop.resetSelectStatus();

        discount_rv_list.startRefresh();
    }

    /**
     * 记得把这个删除了！
     * @param index
     */
    private void setColor(int index){
        ((TextView)getActivity().findViewById(R.id.ll_drop_all)).setTextColor(getResources().getColor(R.color.black));
        ((TextView)getActivity().findViewById(R.id.atv_drop_exercise_1)).setTextColor(getResources().getColor(R.color.black));
        ((TextView)getActivity().findViewById(R.id.atv_drop_topic_1)).setTextColor(getResources().getColor(R.color.black));
        switch (index) {
            case 1:
                ((TextView)getActivity().findViewById(R.id.ll_drop_all)).setTextColor(getResources().getColor(R.color.appBaseColor));
                break;
            case 2:
                ((TextView)getActivity().findViewById(R.id.atv_drop_exercise_1)).setTextColor(getResources().getColor(R.color.appBaseColor));
                break;
            case 3:
                ((TextView)getActivity().findViewById(R.id.atv_drop_topic_1)).setTextColor(getResources().getColor(R.color.appBaseColor));
                break;
        }
    }
}
