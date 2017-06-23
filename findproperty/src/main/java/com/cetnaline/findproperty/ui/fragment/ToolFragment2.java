package com.cetnaline.findproperty.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cetnaline.findproperty.AppContents;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.entity.ui.LoanCalculateBo;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.cetnaline.findproperty.utils.SharedPreferencesUtil;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.kyleduo.switchbutton.SwitchButton;
import com.trello.rxlifecycle.FragmentEvent;

import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func3;

/**
 * 公积金贷款计算
 * Created by diaoqf on 2016/8/15.
 */
public class ToolFragment2 extends BaseToolFragment {

    public static final String LOAN_PRICE = "LOAN_PRICE";//房贷总价

    @BindView(R.id.rg_calc_type)
    RadioGroup rg_calc_type;
    @BindView(R.id.rl_house_total)
    RelativeLayout rl_house_total;
    @BindView(R.id.rl_ratio)
    RelativeLayout rl_ratio;
    @BindView(R.id.rl_loan_total)
    RelativeLayout rl_loan_total;
    @BindView(R.id.rl_loan_type)
    RelativeLayout rl_loan_type;
    @BindView(R.id.aet_house_total)
    AppCompatEditText aet_house_total;
    @BindView(R.id.aet_loan_total)
    AppCompatEditText aet_loan_total;
    @BindView(R.id.aet_year)
    AppCompatEditText aet_year;
    @BindView(R.id.atv_ratio)
    AppCompatTextView atv_ratio;
    @BindView(R.id.atv_loan_type)
    AppCompatTextView atv_loan_type;
    @BindView(R.id.atv_yes_no)
    AppCompatTextView atv_yes_no;
    @BindView(R.id.atv_tips)
    AppCompatTextView atv_tips;
    @BindView(R.id.sc_first)
    SwitchButton sc_first;
    @BindView(R.id.btn_calc)
    TextView btn_calc;

    @Override
    protected void startCalculate() {
        LoanCalculateBo loanCalculateBo = new LoanCalculateBo();
        loanCalculateBo.modeType = mode_calc_type;
        loanCalculateBo.loanType = loanType;
        loanCalculateBo.month = Integer.parseInt(aet_year.getText().toString()) * 12;
        loanCalculateBo.fundRate = sc_first.isChecked() ? lastRate : lastRate * 1.1;

        if (mode_calc_type == 0) {//贷款总额
            loanCalculateBo.businessPrice = Double.parseDouble(aet_loan_total.getText().toString());
        } else {//按揭
            loanCalculateBo.totalPrice = Double.parseDouble(aet_house_total.getText().toString());
            loanCalculateBo.ratio = ratio;
        }

        loanCalculateCallback.callBack(loanCalculateBo);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frg_tool_2;
    }

    @OnClick({R.id.rl_ratio,R.id.rl_loan_type})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_ratio:
                showRatioDialog();
                break;
            case R.id.rl_loan_type:
                showLoanTypeDialog();
                break;
            default:
                break;
        }
    }

    @Override
    protected void ratioItemClick(String text) {
        atv_ratio.setText(text);
    }

    @Override
    protected void loanTypeItemClick(String text) {
        atv_loan_type.setText(text);
    }

    /**
     * 检查计算按钮是否可用
     */
    protected void checkCalcButtonEnable() {
        CharSequence charSequence = aet_house_total.getText();
        CharSequence charSequence2 = aet_loan_total.getText();
        CharSequence charSequence3 = aet_year.getText();
        boolean enable1 = mode_calc_type == 0 || !TextUtils.isEmpty(charSequence);
        boolean enable2 = mode_calc_type == 1 || !TextUtils.isEmpty(charSequence2);
        boolean enable3 = TextUtils.isDigitsOnly(charSequence3);
        btn_calc.setEnabled(enable1 && enable2 && enable3);
    }

    /**
     * 设置利率提示
     */
    protected void setTips(double rate) {
        lastRate = rate;
        atv_tips.setText(String.format(Locale.CHINA, "最新公积金贷款利率%.2f%%", rate));
    }

    @Override
    protected void init() {
        atv_ratio.setText("3.5成");
        ratio = 3.5;
        atv_loan_type.setText("等额本息");
        fundRate();

        if (SharedPreferencesUtil.getInt(AppContents.LANGUAGE) == AppContents.LANGUAGE_CHINESE) {
            aet_year.setText(String.format(Locale.CHINA, "%d", 20));
        } else {
            aet_year.setText(String.format(Locale.ENGLISH, "%d", 20));
        }

        rg_calc_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_loan_total://贷款总额
                        mode_calc_type = 0;
                        rl_house_total.setVisibility(View.GONE);
                        rl_ratio.setVisibility(View.GONE);
                        rl_loan_total.setVisibility(View.VISIBLE);
                        break;
                    case R.id.rb_mortgage_ratio://按揭
                        mode_calc_type = 1;
                        rl_house_total.setVisibility(View.VISIBLE);
                        rl_ratio.setVisibility(View.VISIBLE);
                        rl_loan_total.setVisibility(View.GONE);
                        break;
                    default:
                        break;
                }
                checkCalcButtonEnable();
            }
        });

        Observable<CharSequence> houseTotalObservable = RxTextView.textChanges(aet_house_total);//房屋总价
        Observable<CharSequence> loanToTalObservable = RxTextView.textChanges(aet_loan_total);//贷款总额
        Observable<CharSequence> yearObservable = RxTextView.textChanges(aet_year);//贷款年限

        //贷款年限
        yearObservable.subscribe(new Action1<CharSequence>() {
            @Override
            public void call(CharSequence charSequence) {
                if (!TextUtils.isEmpty(charSequence)) {
                    int input = Integer.parseInt(charSequence.toString());
                    if (input < 6) {//2-5年
                        if (fundRateArrayList.size() > 0) {
                            String value = fundRateArrayList.get(0).getValue();
                            setTips(Float.parseFloat(value) * 100);
                        }
                    } else {//5年以上
                        if (fundRateArrayList.size() > 1) {
                            String value = fundRateArrayList.get(1).getValue();
                            setTips(Float.parseFloat(value) * 100);
                        }
                    }
                }
            }
        });

        //计算按钮
        Observable.combineLatest(houseTotalObservable,
                loanToTalObservable,
                yearObservable,
                new Func3<CharSequence, CharSequence, CharSequence, Boolean>() {
                    @Override
                    public Boolean call(CharSequence charSequence, CharSequence charSequence2,
                                        CharSequence charSequence3) {
                        boolean enable1 = false;
                        boolean enable2 = false;
                        if (mode_calc_type == 0) {
                            enable1 = true;
                            if (!TextUtils.isEmpty(charSequence2)) {
                                double d2 = Double.parseDouble(charSequence2.toString());
                                enable2 = d2 > 0;
                            }
                        } else {
                            enable2 = true;
                            if (!TextUtils.isEmpty(charSequence)) {
                                double d1 = Double.parseDouble(charSequence.toString());
                                enable1 = d1 > 0;
                            }
                        }
                        boolean enable3 = !TextUtils.isEmpty(charSequence3) && Integer.parseInt(charSequence3.toString())>0;
                        return enable1 && enable2 && enable3 && lastRate > 0;
                    }
                })
                .compose(this.<Boolean>bindUntilEvent(FragmentEvent.DESTROY))
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        btn_calc.setEnabled(aBoolean);
                    }
                });

        sc_first.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                atv_yes_no.setText(isChecked ? "是" : "否");
            }
        });
        setCalcClick(btn_calc);
        Bundle bundle = getArguments();
        if (bundle != null) {
            double price = bundle.getDouble(LOAN_PRICE, 0);
            if (price > 0 &&
                    price > 10000) {
                aet_house_total.setText(
                        BigDecimal.valueOf(Double.parseDouble(String.format(Locale.CHINA, "%f", price / 10000)))
                                .stripTrailingZeros()
                                .toPlainString());
            }
        }
    }

    @Override
    protected void initFundRate() {
        aet_year.setText(String.format(Locale.CHINA, "%d", 20));
    }

    @Override
    protected IPresenter createPresenter() {
        return null;
    }
}
