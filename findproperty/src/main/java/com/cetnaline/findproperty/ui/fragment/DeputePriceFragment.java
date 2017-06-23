package com.cetnaline.findproperty.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.bean.HouseBo;
import com.cetnaline.findproperty.api.bean.PriceTrendBean;
import com.cetnaline.findproperty.base.BaseFragment;
import com.cetnaline.findproperty.entity.ui.DeputePushBean;
import com.cetnaline.findproperty.inter.FragmentBack;
import com.cetnaline.findproperty.api.NetContents;
import com.cetnaline.findproperty.presenter.impl.DeputePricePresenter;
import com.cetnaline.findproperty.presenter.ui.DeputePriceContract;
import com.cetnaline.findproperty.ui.activity.DeputeActivity;
import com.cetnaline.findproperty.ui.activity.WebActivity;
import com.cetnaline.findproperty.utils.MyUtils;
import com.cetnaline.findproperty.widgets.FormItemLayout;
import com.cetnaline.findproperty.widgets.chart.SaleLineChart2;
import com.jakewharton.rxbinding.view.RxView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;

/**
 * Created by diaoqf on 2017/3/30.
 */

public class DeputePriceFragment extends BaseFragment<DeputePricePresenter> implements DeputePriceContract.View,FragmentBack {
    @BindView(R.id.sale)
    RadioButton sale;
    @BindView(R.id.rent)
    RadioButton rent;
    @BindView(R.id.room_price)
    FormItemLayout room_price;
    @BindView(R.id.room_advise_price)
    FormItemLayout room_advise_price;
    @BindView(R.id.calculator_price)
    TextView calculator_price;
    @BindView(R.id.calculator_price_1)
    TextView calculator_price_1;

    @BindView(R.id.price_chart)
    SaleLineChart2 price_chart;
    @BindView(R.id.chart_layout)
    LinearLayout chart_layout;

    @BindView(R.id.list_layout)
    LinearLayout list_layout;
    @BindView(R.id.list_label)
    TextView list_label;
    @BindView(R.id.list)
    ListView listView;
    @BindView(R.id.advise_layout)
    RelativeLayout advise_layout;
    @BindView(R.id.submit)
    TextView submit;

    private PriceTrendBean priceTrendBean;

    private SimpleAdapter adapter;
    private ArrayList<Map<String, String>> datas = new ArrayList<>();

    private String gScopeId;  //区域id
//    private String estateId;  //小区id
//    private String estateCode; //小区code
//    private String estateName;//小区名称
    private String gScopeName;//区域名称

    private String currentType = "S"; //当前选择的租售类别

    private DeputePushBean deputePushBean;

    private double evaluatePrice;

    public static DeputePriceFragment getInstance() {
        return new DeputePriceFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frg_depute_price;
    }

    @Override
    protected void init() {
        sale.setOnClickListener(v-> {
            currentType = "S";
//            chart_layout.setVisibility(View.VISIBLE);
            room_price.setTitle("预期售价");
            room_price.setExtText("万元");
            room_price.setHintText("请输入预期售价");
            list_label.setText("同小区在售房源");

            if (evaluatePrice > 0) {
                advise_layout.setVisibility(View.VISIBLE);
                calculator_price_1.setVisibility(View.GONE);
                room_advise_price.setText(evaluatePrice + "万");
            } else {
                advise_layout.setVisibility(View.GONE);
                calculator_price_1.setVisibility(View.VISIBLE);
                room_advise_price.setText("");
            }

            mPresenter.getHouseList(deputePushBean.getEntrustData().getEstateCode(),currentType);

            if (priceTrendBean != null) {
                chart_layout.setVisibility(View.VISIBLE);
                price_chart.setEstatePriceBo(deputePushBean.getEntrustData().getEstateName(), gScopeName, priceTrendBean.getEstateDealPriceBos(), priceTrendBean.getGscopeDealPriceBos(), priceTrendBean.getCityDealPriceBos());
            } else {
                String code = deputePushBean.getEntrustData().getEstateCode();
                mPresenter.getPrices(code==null?"":code,"99999",currentType);
            }
        });
        rent.setOnClickListener(v-> {
            currentType = "R";
            calculator_price_1.setVisibility(View.GONE);
            chart_layout.setVisibility(View.GONE);
            room_price.setTitle("预期租金");
            room_price.setExtText("元/月");
            room_price.setHintText("请输入预期租金");
            advise_layout.setVisibility(View.GONE);
            list_label.setText("同小区出租房源");
            mPresenter.getHouseList(deputePushBean.getEntrustData().getEstateCode(),currentType);
        });

        RxView.clicks(submit).throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(aVoid -> {
                    if (room_price.getText() == null || "".equals(room_price.getText()) || Double.parseDouble(room_price.getText()) <= 0) {
                        if (currentType.equals("S")) {
                            toast("请输入正确的预期售价");
                        } else {
                            toast("请输入正确的预期租价");
                        }
                        return;
                    }
                    double price = Double.parseDouble(room_price.getText());
                    int type = ("S".equals(currentType) ? 1:2);
                    if (type == 1) {
                        deputePushBean.getEntrustData().setExpectedPrice(price * 10000);
                    } else {
                        deputePushBean.getEntrustData().setExpectedPrice(price);
                    }
                    deputePushBean.getEntrustOrder().setEntrustType(type);
                    ((DeputeActivity)getActivity()).saveStep2(evaluatePrice,deputePushBean);
                    addFragment(DeputeCustomerFragment.getInstance());
                });

        calculator_price.setOnClickListener(v->{
            Intent intent = new Intent(getActivity(), WebActivity.class);
            intent.putExtra(WebActivity.TARGET_URL, NetContents.ENTRUST_PRICE);
            intent.putExtra(WebActivity.TITLE_HIDDEN_KEY, true);
            startActivity(intent);
        });

        calculator_price_1.setOnClickListener(v->{
            Intent intent = new Intent(getActivity(), WebActivity.class);
            intent.putExtra(WebActivity.TARGET_URL, NetContents.ENTRUST_PRICE);
            intent.putExtra(WebActivity.TITLE_HIDDEN_KEY, true);
            startActivity(intent);
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPresenter.getFormdata();
    }

    @Override
    protected DeputePricePresenter createPresenter() {
        return new DeputePricePresenter();
    }

    @Override
    public void showLoading() {
        showLoading();
    }

    @Override
    public void dismissLoading() {
        cancelLoadingDialog();
    }

    @Override
    public void showError(String msg) {
//        toast("加载数据有误");
    }

    @Override
    public void setPrices(PriceTrendBean bean) {
        priceTrendBean = bean;
        if (priceTrendBean == null) {
            chart_layout.setVisibility(View.GONE);
        } else {
            chart_layout.setVisibility(View.VISIBLE);
            //设置数据
            price_chart.setEstatePriceBo(deputePushBean.getEntrustData().getEstateName(), gScopeName, priceTrendBean.getEstateDealPriceBos(), priceTrendBean.getGscopeDealPriceBos(), priceTrendBean.getCityDealPriceBos());
        }
    }

    @Override
    public void setHouseList(List<HouseBo> list) {
        if (list == null) {
            list_layout.setVisibility(View.GONE);
        } else {
            list_layout.setVisibility(View.VISIBLE);
            datas.clear();
            for (HouseBo houseBo:list) {
                Map<String, String> item = new HashMap<>();
                item.put("RoomType",houseBo.getRoomCount()+"室"+houseBo.getHallCount()+"厅");
                item.put("FloorDisplay", houseBo.getFloorDisplay());
                item.put("Area", houseBo.getGArea()+"㎡");
                if (currentType.equals("S")) {
                    item.put("Price", (int)(houseBo.getSalePrice()/10000)+"万");
                } else {
                    item.put("Price", (int)houseBo.getRentPrice()+"元/月");
                }
                datas.add(item);
            }

            if (adapter == null) {
                adapter = new SimpleAdapter(getActivity(),
                        datas, R.layout.item_house_price,
                        new String[]{"RoomType", "Area", "FloorDisplay", "Price"},
                        new int[]{R.id.room_type, R.id.area, R.id.floor_display, R.id.price});
                listView.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }

            //修改列表高度
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = MyUtils.dip2px(getActivity(),40) * adapter.getCount() + (listView.getDividerHeight() * (adapter.getCount() - 1));
            listView.setLayoutParams(params);
            listView.setItemsCanFocus(false);
        }
    }

    @Override
    public void reloadData() {
        deputePushBean = ((DeputeActivity) getActivity()).getDeputePushBean();
        int cType = deputePushBean.getEntrustOrder().getEntrustType();
        currentType = (cType == 1 ? "S":(cType==2?"R":"RS"));
        double eprice = deputePushBean.getEntrustData().getExpectedPrice();
        if (eprice > 0) {
            if (cType == 1) {
                room_price.setText((int)eprice/10000+"");
            } else {
                room_price.setText((int)eprice+"");
            }

        } else {
            room_price.setText("");
        }

        room_advise_price.setText(deputePushBean.getEntrustData().getExpectedPrice()+"万");
        if (cType == 1) {
            sale.performClick();
        } else {
            rent.performClick();
        }

        evaluatePrice = ((DeputeActivity)getActivity()).getEvaluatePrice();
        advise_layout.setVisibility(View.GONE);
        calculator_price_1.setVisibility(View.GONE);
        if (cType == 1) {
            if (evaluatePrice > 0) {
                advise_layout.setVisibility(View.VISIBLE);
                calculator_price_1.setVisibility(View.GONE);
                room_advise_price.setText(evaluatePrice + "万");
            } else {
                advise_layout.setVisibility(View.GONE);
                calculator_price_1.setVisibility(View.VISIBLE);
                room_advise_price.setText("");
            }
            String code = deputePushBean.getEntrustData().getEstateCode();
            mPresenter.getPrices(code==null?"":code,"99999",currentType); //目前没有小区和区域价格趋势,参数：小区code，区域id，当前租售类型
        }
        mPresenter.getHouseList(deputePushBean.getEntrustData().getEstateCode(),currentType);

        if(deputePushBean.getEntrustData().getEstateId() < 0) {
            list_layout.setVisibility(View.GONE);
        }

    }

    @Override
    public void saveData() {
        int type = ("S".equals(currentType) ? 1:2);
        if (room_price.getText()!= null && !room_price.getText().trim().equals("")) {
            double price = Double.parseDouble(room_price.getText());

            if (type == 1) {
                deputePushBean.getEntrustData().setExpectedPrice(price * 10000);
            } else {
                deputePushBean.getEntrustData().setExpectedPrice(price);
            }
        }
        deputePushBean.getEntrustOrder().setEntrustType(type);
    }
}
