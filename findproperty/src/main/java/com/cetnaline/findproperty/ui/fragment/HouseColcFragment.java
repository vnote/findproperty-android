package com.cetnaline.findproperty.ui.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.entity.bean.UserInfoBean;
import com.cetnaline.findproperty.entity.result.BaseSingleResult;
import com.cetnaline.findproperty.entity.ui.TaxCalculateBo;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.cetnaline.findproperty.utils.DataHolder;
import com.cetnaline.findproperty.utils.SchedulersCompat;
import com.cetnaline.findproperty.widgets.MyBottomDialog;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.kyleduo.switchbutton.SwitchButton;
import com.orhanobut.logger.Logger;
import com.trello.rxlifecycle.FragmentEvent;

import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func2;

/**
 * Created by diaoqf on 2016/8/16.
 */
public class HouseColcFragment extends BaseTaxCalFragment {

    public static final String TAX_DAN_PRICE = "TAX_DAN_PRICE"; //单价
    public static final String AREA = "Area";//面积
    public static final String TAX_TOTAL_PRICE = "TAX_TOTAL_PRICE";//房屋总价

    @BindView(R.id.rl_house_type)
    RelativeLayout rl_house_type;
    @BindView(R.id.rl_levy_type)
    RelativeLayout rl_levy_type;
    @BindView(R.id.rl_buy_years)
    RelativeLayout rl_buy_years;
    @BindView(R.id.rl_house_original)
    RelativeLayout rl_house_original;
    @BindView(R.id.aet_house_area)
    AppCompatEditText aet_house_area;
    @BindView(R.id.aet_house_unit_price)
    AppCompatEditText aet_house_unit_price;
    @BindView(R.id.aet_house_total)
    AppCompatEditText aet_house_total;
    @BindView(R.id.aet_house_original)
    AppCompatEditText aet_house_original;
    @BindView(R.id.atv_house_type)
    AppCompatTextView atv_house_type;
    @BindView(R.id.atv_levy_type)
    AppCompatTextView atv_levy_type;
    @BindView(R.id.atv_buy_years)
    AppCompatTextView atv_buy_years;
    @BindView(R.id.atv_buy_yes_no)
    AppCompatTextView atv_buy_yes_no;
    @BindView(R.id.atv_sale_yes_no)
    AppCompatTextView atv_sale_yes_no;
    @BindView(R.id.sc_seller_only)
    SwitchButton sc_seller_only;
    @BindView(R.id.sc_buyer_first)
    SwitchButton sc_buyer_first;
    @BindView(R.id.btn_tax_fragment)
    TextView btn_tax_fragment;

    private final String[] houseTypes = new String[]{"普通住宅", "非普通住宅"};
    private final String[] levyTypes = new String[]{"总价", "差价"};
    private final String[] buyYears = new String[]{"2年以内", "2-5年", "5年以上"};
    private MyBottomDialog houseTypeDialog;//房屋类型
    private MyBottomDialog levyTypeDialog;//征收方式
    private MyBottomDialog buyYearsDialog;//购置年限
    private int houseType;
    private int levyType;
    private int buyYear;

    @Override
    protected void startCalculate() {
        TaxCalculateBo taxCalculateBo = new TaxCalculateBo();
        taxCalculateBo.modeType = 1;
        taxCalculateBo.houseType = houseType;
        taxCalculateBo.houseArea = Double.parseDouble(aet_house_area.getText().toString());
        if (aet_house_unit_price.getText().length() > 0) {
            taxCalculateBo.houseUnitPrice = Double.parseDouble(aet_house_unit_price.getText().toString());
        }
        taxCalculateBo.houseTotal = Double.parseDouble(aet_house_total.getText().toString());
        taxCalculateBo.levyType = levyType;
        taxCalculateBo.originPrice = aet_house_original.getText().length() > 0 ?
                Double.parseDouble(aet_house_original.getText().toString()) : 0;
        taxCalculateBo.buyYear = buyYear;
        if (sc_buyer_first.isChecked()) {
            taxCalculateBo.buyerFirst = true;
        } else {
            taxCalculateBo.buyerFirst = false;
        }

        if (sc_seller_only.isChecked()) {
            taxCalculateBo.sellerOnly = true;
        } else {
            taxCalculateBo.sellerOnly = false;
        }
        taxCalculateCallback.callBack(taxCalculateBo);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frg_calc_house;
    }

    @Override
    protected void init() {
        houseType = 1;
        levyType = 1;
        buyYear = 2;
        aet_house_area.requestFocus();

        sc_buyer_first.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                atv_buy_yes_no.setText(isChecked ? "是" : "否");
            }
        });
        sc_seller_only.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                atv_sale_yes_no.setText(isChecked ? "是" : "否");
            }
        });

        Observable<CharSequence> houseAreaObservable = RxTextView.textChanges(aet_house_area);
        Observable<CharSequence> housePreObservable = RxTextView.textChanges(aet_house_unit_price);
        Observable<CharSequence> totalPriceObservable = RxTextView.textChanges(aet_house_total);
        Observable<CharSequence> originObservable = RxTextView.textChanges(aet_house_original);

        //计算总价
        Observable.combineLatest(houseAreaObservable, housePreObservable, new Func2<CharSequence, CharSequence, CharSequence>() {
            @Override
            public CharSequence call(CharSequence charSequence, CharSequence charSequence2) {
                if (TextUtils.isEmpty(charSequence) || TextUtils.isEmpty(charSequence2)) {
                    return "";
                } else {
                    double insert1 = Double.parseDouble(charSequence.toString());
                    double insert2 = Double.parseDouble(charSequence2.toString());
                    return String.format(Locale.CHINA, "%.0f", insert1 * insert2 / 10000);
                }
            }
        }).compose(this.<CharSequence>bindUntilEvent(FragmentEvent.DESTROY))
                .subscribe(new Action1<CharSequence>() {
                    @Override
                    public void call(CharSequence charSequence) {
                        aet_house_total.setText(charSequence);
                    }
                });

        Observable.combineLatest(totalPriceObservable, originObservable,
                new Func2<CharSequence, CharSequence, Boolean>() {
                    @Override
                    public Boolean call(CharSequence charSequence, CharSequence charSequence2) {
                        Logger.d("charSequence : %s charSequence2 : %s", charSequence, charSequence2);
                        return charSequence.toString().equals(charSequence2.toString()) && !(TextUtils.isEmpty(charSequence));
                    }
                }).compose(this.<Boolean>bindUntilEvent(FragmentEvent.DESTROY))
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean) {
                            toast("你的房屋没有差价,请重新填写");
                        }
                    }
                });

        Observable.combineLatest(houseAreaObservable, totalPriceObservable, originObservable,
                (charSequence,charSequence2,charSequence3)-> {
                    if (levyType == 1) {
                        return !(TextUtils.isEmpty(charSequence) || TextUtils.isEmpty(charSequence2));
                    } else {
                        return !(TextUtils.isEmpty(charSequence) ||
                                TextUtils.isEmpty(charSequence2) ||
                                TextUtils.isEmpty(charSequence3) ||
                                charSequence2.equals(charSequence3));
                    }
                }).subscribe(aBoolean -> btn_tax_fragment.setEnabled(aBoolean));

        setCalcClick(btn_tax_fragment);

        Bundle bundle = getArguments();
        if (bundle != null) {
            double price = bundle.getDouble(TAX_DAN_PRICE, 0);
            double area = bundle.getDouble(AREA, 0);
            double total_price = bundle.getDouble(TAX_TOTAL_PRICE, 0);
            if (price > 0 && area > 0) {
                aet_house_area.setText(String.format(Locale.CHINA, "%.0f", area));
                aet_house_unit_price.setText(String.format(Locale.CHINA, "%.0f", price));
                aet_house_total.setText(String.format(Locale.CHINA, "%.0f", total_price/10000));
            }
        }
    }

    @Override
    protected IPresenter createPresenter() {
        return null;
    }

    @OnClick({R.id.rl_house_type,R.id.rl_levy_type,R.id.rl_buy_years})
    public void onClick(View view) {
        InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(getActivity().findViewById(android.R.id.content).getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
        Observable.timer(500, TimeUnit.MILLISECONDS)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(aLong -> {
                    switch (view.getId()) {
                        case R.id.rl_house_type:
                            if (houseTypeDialog == null) {
                                houseTypeDialog = new MyBottomDialog(HouseColcFragment.this.getActivity());
                                ListView listView = (ListView) LayoutInflater.from(getActivity()).inflate(R.layout.layout_bottom_list, null);
                                houseTypeDialog.setContentView(listView);
                                listView.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.item_bottom_dialog, new String[]{"普通住宅", "非普通住宅"}));
                                listView.setOnItemClickListener((adapterView, view1, i, l) -> {
                                    houseType = i + 1;
                                    atv_house_type.setText((String)listView.getAdapter().getItem(i));
                                    houseTypeDialog.dismiss();
                                });
                            }
                            houseTypeDialog.show();
                            break;
                        case R.id.rl_levy_type:
                            if (levyTypeDialog == null) {
                                levyTypeDialog = new MyBottomDialog(getActivity());
                                ListView listView = (ListView) LayoutInflater.from(getActivity()).inflate(R.layout.layout_bottom_list, null);
                                levyTypeDialog.setContentView(listView);
                                listView.setAdapter(new ArrayAdapter<>(getActivity(),R.layout.item_bottom_dialog,new String[]{"总价","差价"}));
                                listView.setOnItemClickListener((adapterView, view12, i, l) -> {
                                    if (i < 2) {
                                        levyType = i + 1;
                                        atv_levy_type.setText((String)listView.getAdapter().getItem(i));
                                        if (i == 1) {
                                            rl_house_original.setVisibility(View.VISIBLE);
                                        } else {
                                            rl_house_original.setVisibility(View.GONE);
                                        }
                                    }
                                    levyTypeDialog.dismiss();
                                });
                            }
                            levyTypeDialog.show();
                            break;
                        case R.id.rl_buy_years:
                            if (buyYearsDialog == null) {
                                buyYearsDialog = new MyBottomDialog(getActivity());
                                ListView listView = (ListView) LayoutInflater.from(getActivity()).inflate(R.layout.layout_bottom_list, null);
                                buyYearsDialog.setContentView(listView);
                                listView.setAdapter(new ArrayAdapter<>(getActivity(),R.layout.item_bottom_dialog,new String[]{"2年以内","2-5年","5年以上"}));
                                listView.setOnItemClickListener((adapterView, view13, i, l) -> {
                                    buyYear = i;
                                    atv_buy_years.setText((String)listView.getAdapter().getItem(i));
                                    buyYearsDialog.dismiss();
                                });
                            }
                            buyYearsDialog.show();
                            break;
                        default:
                            break;
                    }
                },throwable -> throwable.printStackTrace());

    }
}
