package com.cetnaline.findproperty.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
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

import java.math.BigDecimal;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;

/**
 * Created by diaoqf on 2016/8/15.
 */
public class ToolFragment1 extends BaseToolFragment {
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
    @BindView(R.id.aet_loan_rate)
    AppCompatEditText aet_loan_rate;
    @BindView(R.id.aet_times)
    AppCompatEditText aet_times;
    @BindView(R.id.atv_ratio)
    AppCompatTextView atv_ratio;
    @BindView(R.id.atv_loan_type)
    AppCompatTextView atv_loan_type;
    @BindView(R.id.atv_yes_no)
    AppCompatTextView atv_yes_no;
    @BindView(R.id.atv_latest_rate)
    AppCompatTextView atv_latest_rate;
    @BindView(R.id.atv_tips)
    AppCompatTextView atv_tips;
    @BindView(R.id.sc_first)
    SwitchButton sc_first;
    @BindView(R.id.btn_calc)
    TextView btn_calc;

    @Override
    protected int getLayoutId() {
        return R.layout.frg_tool_1;
    }

    @Override
    protected void init() {
        aet_loan_rate.setText("4.9");
        atv_tips.setText("最新商业贷款利率4.9%");
        atv_latest_rate.setText("4.9%");

        atv_ratio.setText("3.5成");
        ratio = 3.5;
        atv_loan_type.setText("等额本息");
        aet_times.setText("1");
        bizRate();

        rg_calc_type.setOnCheckedChangeListener((group,checkedId)-> {
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
        });
        aet_house_total.requestFocus();
        Observable<CharSequence> houseTotalObservable = RxTextView.textChanges(aet_house_total);//房屋总价
        Observable<CharSequence> loanToTalObservable = RxTextView.textChanges(aet_loan_total);//贷款总额
        Observable<CharSequence> yearObservable = RxTextView.textChanges(aet_year);//贷款年限
        Observable<CharSequence> loanRateObservable = RxTextView.textChanges(aet_loan_rate);//贷款利率
        Observable<CharSequence> loanTimesObservable = RxTextView.textChanges(aet_times);//倍数
        Observable<CharSequence> lastRateObservable = RxTextView.textChanges(atv_latest_rate);//最终利率

        //贷款年限
        yearObservable.subscribe((charSequence)-> {
            if (!TextUtils.isEmpty(charSequence)) {
                int input = Integer.parseInt(charSequence.toString());
                if (input < 2) {//1年
                    if (bizRateArrayList.size() > 0) {
                        String value = bizRateArrayList.get(0).getValue();
                        setTips(Double.parseDouble(value) * 100);
                    }
                } else if (input < 6) {//2-5年
                    if (bizRateArrayList.size() > 1) {
                        String value = bizRateArrayList.get(1).getValue();
                        setTips(Double.parseDouble(value) * 100);
                    }
                } else {//5年以上
                    if (bizRateArrayList.size() > 2) {
                        String value = bizRateArrayList.get(2).getValue();
                        setTips(Double.parseDouble(value) * 100);
                    }
                }
            }
        });

        //计算最终几率
        Observable.combineLatest(loanRateObservable,
                loanTimesObservable,
                (charSequence, charSequence2)-> {
                    if (TextUtils.isEmpty(charSequence) ||
                            TextUtils.isEmpty(charSequence2)) {
                        return "";
                    } else {
                        float f1 = Float.parseFloat(charSequence.toString());
                        float f2 = Float.parseFloat(charSequence2.toString());
                        return String.format(Locale.CHINA, "%.2f", f1 * f2);
                    }
                })
                .compose(this.<CharSequence>bindUntilEvent(FragmentEvent.DESTROY))
                .subscribe(charSequence-> {
                    if (TextUtils.isEmpty(charSequence)) {
                        atv_latest_rate.setText(charSequence);
                        lastRate = 0;
                    } else {
                        lastRate = Float.parseFloat(charSequence.toString());
                        if (lastRate > 0) {
                            atv_latest_rate.setText(String.format(Locale.CHINA, "%s%%", charSequence));
                        } else {
                            atv_latest_rate.setText("");
                        }
                    }
                });

        //计算按钮
        Observable.combineLatest(houseTotalObservable,
                loanToTalObservable,
                yearObservable,
                lastRateObservable,
                (charSequence,charSequence2,charSequence3,charSequence4)-> {
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
                    boolean enable4 = !TextUtils.isEmpty(charSequence4);
                    return enable1 && enable2 && enable3 && enable4 && lastRate > 0;
                })
                .compose(this.<Boolean>bindUntilEvent(FragmentEvent.DESTROY))
                .subscribe((aBoolean)-> btn_calc.setEnabled(aBoolean));

        sc_first.setOnCheckedChangeListener((buttonView, isChecked)-> {
            atv_yes_no.setText(isChecked ? "是" : "否");
            aet_times.setText(isChecked ? "1" : "1.1");
        });
        setCalcClick(btn_calc);
        Bundle bundle = getArguments();
        if (bundle != null) {
            double price = bundle.getDouble(LOAN_PRICE, 0);    //传入的总价
            if (price > 0 && price > 10000) {
                aet_house_total.setText(
                        BigDecimal.valueOf(Double.parseDouble(String.format(Locale.CHINA, "%f", price / 10000)))
                                .stripTrailingZeros()
                                .toPlainString());
            }
        }

        if (SharedPreferencesUtil.getInt(AppContents.LANGUAGE) == AppContents.LANGUAGE_CHINESE) {
            aet_year.setText(String.format(Locale.CHINA, "%d", 20));
        } else {
            aet_year.setText(String.format(Locale.ENGLISH, "%d", 20));
        }

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
    protected void startCalculate() {
        LoanCalculateBo loanCalculateBo = new LoanCalculateBo();
        loanCalculateBo.modeType = mode_calc_type;
        loanCalculateBo.loanType = loanType;
        loanCalculateBo.month = Integer.parseInt(aet_year.getText().toString()) * 12;
        loanCalculateBo.businessRate = lastRate;

        if (mode_calc_type == 0) {//贷款总额
            loanCalculateBo.businessPrice = Double.parseDouble(aet_loan_total.getText().toString());
        } else {//按揭
            loanCalculateBo.totalPrice = Double.parseDouble(aet_house_total.getText().toString());
            loanCalculateBo.ratio = ratio;
        }
        loanCalculateCallback.callBack(loanCalculateBo);
    }

    /**
     * 首付比例
     */
//    protected void showRatioDialog() {
//        if (ratioDialog == null) {
//            ratioDialog = new AlertDialog.Builder(getContext())
//                    .setTitle(R.string.calc_mortgage_ratio)
//                    .setSingleChoiceItems(ratios, 2, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            ratio = ratiosDouble[which];
//                            ratioItemClick(ratios[which]);
//                            ratioDialog.dismiss();
//                        }
//                    })
//                    .create();
//        }
//        ratioDialog.show();
//    }

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
        CharSequence charSequence4 = atv_latest_rate.getText();
        boolean enable1 = mode_calc_type == 0 || !TextUtils.isEmpty(charSequence);
        boolean enable2 = mode_calc_type == 1 || !TextUtils.isEmpty(charSequence2);
        boolean enable3 = TextUtils.isDigitsOnly(charSequence3);
        boolean enable4 = !TextUtils.isEmpty(charSequence4);
        btn_calc.setEnabled(enable1 && enable2 && enable3 && enable4);
    }

    /**
     * 设置利率提示
     */
    protected void setTips(double rate) {
        aet_loan_rate.setText(String.format(Locale.CHINA, "%.2f", rate));
        atv_tips.setText(String.format(Locale.CHINA, "最新商业贷款利率%.2f%%", rate));
    }

    @Override
    protected IPresenter createPresenter() {
        return null;
    }
}
