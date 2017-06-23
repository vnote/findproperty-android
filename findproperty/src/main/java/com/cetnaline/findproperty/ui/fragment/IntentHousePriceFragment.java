package com.cetnaline.findproperty.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.bean.SearchParam;
import com.cetnaline.findproperty.base.BaseFragment;
import com.cetnaline.findproperty.db.entity.DropBo;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.cetnaline.findproperty.ui.adapter.IntentSelectAdapter;
import com.cetnaline.findproperty.utils.DbUtil;
import com.cetnaline.findproperty.widgets.StepLayout;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by diaoqf on 2016/8/27.
 */
public class IntentHousePriceFragment extends BaseFragment {

    @BindView(R.id.step_layout)
    StepLayout step_layout;

    @BindView(R.id.intent_rv_list)
    RecyclerView intent_rv_list;

    @BindView(R.id.intent_tv_title)
    TextView intent_tv_title;

    private int houseType;

    public static IntentHousePriceFragment getInstance(HashMap<String, SearchParam> data, int houseType){
        IntentHousePriceFragment fragment = new IntentHousePriceFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(IntentHouseTypeFragment.INTENT_DATA_KEY, data);
        bundle.putInt(MapFragment.HOUSE_TYPE_KEY, houseType);
        fragment.setArguments(bundle);
        return fragment;
    }

    private HashMap<String, SearchParam> param;

    @Override
    protected int getLayoutId() {
        return R.layout.frg_intent_2;
    }

    @Override
    protected void init() {

        step_layout.enableStep(2);

        param = (HashMap<String, SearchParam>) getArguments().getSerializable(IntentHouseTypeFragment.INTENT_DATA_KEY);
        houseType = getArguments().getInt(MapFragment.HOUSE_TYPE_KEY);

        List<DropBo> dropBos;
        if (houseType==MapFragment.HOUSE_TYPE_SECOND){
            dropBos = DbUtil.getSearchDataByName("Sell");
            intent_tv_title.setText("您想买的房价范围");
        }else if (houseType==MapFragment.HOUSE_TYPE_RENT){
            dropBos = DbUtil.getSearchDataByName("Rent");
            intent_tv_title.setText("您想租的房价范围");
        }else {
            dropBos = DbUtil.getSearchDataByName("NewHousePriceN");
            intent_tv_title.setText("您想买的房价范围");
        }
        dropBos.remove(0);

        // 设置item动画
        intent_rv_list.setItemAnimator(new DefaultItemAnimator());
        intent_rv_list.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        intent_rv_list.setHasFixedSize(true);
        IntentSelectAdapter adapter = new IntentSelectAdapter(getActivity(), R.layout.item_intent_tag, dropBos);
        intent_rv_list.setAdapter(adapter);
        adapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, Object o, int position) {

                DropBo item = (DropBo) o;
                SearchParam search = createSearch(item);
                if (houseType==MapFragment.HOUSE_TYPE_SECOND){
                    search.setKey("MinSalePrice,MaxSalePrice");
                }else if (houseType==MapFragment.HOUSE_TYPE_RENT){
                    search.setKey("MinRentPrice,MaxRentPrice");
                }else {
                    search.setKey("MinAveragePrice,MaxAveragePrice");
                }
                adapter.setSelectedP(position);
                adapter.notifyDataSetChanged();
                param.put(search.getKey(), search);
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, Object o, int position) {
                return false;
            }
        });

        DropBo item = dropBos.get(0);
        SearchParam search = createSearch(item);
        if (houseType==MapFragment.HOUSE_TYPE_SECOND){
            search.setKey("MinSalePrice,MaxSalePrice");
        }else if (houseType==MapFragment.HOUSE_TYPE_RENT){
            search.setKey("MinRentPrice,MaxRentPrice");
        }else {
            search.setKey("MinAveragePrice,MaxAveragePrice");
        }
        param.put(search.getKey(), search);
    }

    @OnClick(R.id.intent_bt_commit)
    public void toNext(){
        addFragment(IntentHouseRoomFragment.getInstance(param, houseType));
    }


    @Override
    protected IPresenter createPresenter() {
        return null;
    }

    private SearchParam createSearch(DropBo dropType){
        SearchParam search = new SearchParam();
        search.setId(dropType.getID()==null?0:dropType.getID());
        search.setText(dropType.getText());
        search.setValue(dropType.getValue());
        search.setTitle(dropType.getName());
        search.setPara(dropType.getPara());
        search.setName(dropType.getName());
        return search;
    }
}
