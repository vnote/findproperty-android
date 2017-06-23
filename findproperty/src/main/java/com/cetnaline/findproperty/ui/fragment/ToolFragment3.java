package com.cetnaline.findproperty.ui.fragment;

import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.entity.ui.LoanCalculateBo;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.kyleduo.switchbutton.SwitchButton;
import com.trello.rxlifecycle.FragmentEvent;

import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func2;
import rx.functions.Func4;

/**
 * Created by diaoqf on 2016/8/15.
 */
public class ToolFragment3 extends BaseToolFragment {

    @BindView(R.id.aet_fund)
    AppCompatEditText aet_fund;
    @BindView(R.id.aet_business)
    AppCompatEditText aet_business;
    @BindView(R.id.aet_year)
    AppCompatEditText aet_year;
    @BindView(R.id.aet_loan_rate)
    AppCompatEditText aet_loan_rate;
    @BindView(R.id.aet_times)
    AppCompatEditText aet_times;
    @BindView(R.id.rl_loan_type)
    RelativeLayout rl_loan_type;
    @BindView(R.id.atv_loan_type)
    AppCompatTextView atv_loan_type;
    @BindView(R.id.atv_yes_no)
    AppCompatTextView atv_yes_no;
    @BindView(R.id.atv_latest_rate)
    AppCompatTextView atv_latest_rate;
    @BindView(R.id.atv_tips_business)
    AppCompatTextView atv_tips_business;
    @BindView(R.id.atv_tips_fund)
    AppCompatTextView atv_tips_fund;
    @BindView(R.id.sc_first)
    SwitchButton sc_first;
    @BindView(R.id.btn_calc)
    TextView btn_calc;

    private double lastFundRate;
    private double fundRate;


    @Override
    protected void startCalculate() {
        LoanCalculateBo loanCalculateBo = new LoanCalculateBo();
        loanCalculateBo.modeType = 2;
        loanCalculateBo.loanType = loanType;
        loanCalculateBo.month = Integer.parseInt(aet_year.getText().toString()) * 12;
        loanCalculateBo.businessRate = lastRate;
        loanCalculateBo.fundRate = lastFundRate;
        loanCalculateBo.fundPrice = aet_fund.getText().length() > 0 ?
                Double.parseDouble(aet_fund.getText().toString()) : 0;
        loanCalculateBo.businessPrice = aet_business.getText().length() > 0 ?
                Double.parseDouble(aet_business.getText().toString()) : 0;
        loanCalculateCallback.callBack(loanCalculateBo);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frg_tool_3;
    }

    @OnClick(R.id.rl_loan_type)
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_loan_type:
                showLoanTypeDialog();
                break;
        }
    }

    @Override
    protected void initFundRate() {
        aet_year.setText(String.format(Locale.CHINA, "%d", 20));
    }

    @Override
    protected void init() {
        atv_loan_type.setText("等额本息");
        aet_times.setText("1");
        bizRate();
        fundRate();

        Observable<CharSequence> fundObservable = RxTextView.textChanges(aet_fund);//公积金
        Observable<CharSequence> businessObservable = RxTextView.textChanges(aet_business);//商业
        Observable<CharSequence> yearObservable = RxTextView.textChanges(aet_year);//贷款年限
        Observable<CharSequence> loanRateObservable = RxTextView.textChanges(aet_loan_rate);//贷款利率
        Observable<CharSequence> loanTimesObservable = RxTextView.textChanges(aet_times);//倍数
        Observable<CharSequence> lastRateObservable = RxTextView.textChanges(atv_latest_rate);//最终利率

        //贷款年限
        yearObservable.subscribe(new Action1<CharSequence>() {
            @Override
            public void call(CharSequence charSequence) {
                if (!TextUtils.isEmpty(charSequence)) {
                    int input = Integer.parseInt(charSequence.toString());
                    if (input < 2) {//1年
                        if (bizRateArrayList.size() > 0) {
                            String value = bizRateArrayList.get(0).getValue();
                            setBusinessTips(Float.parseFloat(value) * 100);
                        }
                    } else if (input < 6) {//2-5年
                        if (bizRateArrayList.size() > 1) {
                            String value = bizRateArrayList.get(1).getValue();
                            setBusinessTips(Float.parseFloat(value) * 100);
                        }
                    } else {//5年以上
                        if (bizRateArrayList.size() > 2) {
                            String value = bizRateArrayList.get(2).getValue();
                            setBusinessTips(Float.parseFloat(value) * 100);
                        }
                    }
                    if (input < 6) {//2-5年
                        if (fundRateArrayList.size() > 0) {
                            String value = fundRateArrayList.get(0).getValue();
                            setFundTips(Float.parseFloat(value) * 100);
                        }
                    } else {//5年以上
                        if (fundRateArrayList.size() > 1) {
                            String value = fundRateArrayList.get(1).getValue();
                            setFundTips(Float.parseFloat(value) * 100);
                        }
                    }
                }
            }
        });

        //计算最终利率
        Observable.combineLatest(loanRateObservable,
                loanTimesObservable,
                new Func2<CharSequence, CharSequence, CharSequence>() {
                    @Override
                    public CharSequence call(CharSequence charSequence, CharSequence charSequence2) {
                        if (TextUtils.isEmpty(charSequence) ||
                                TextUtils.isEmpty(charSequence2)) {
                            return "";
                        } else {
                            float f1 = Float.parseFloat(charSequence.toString());
                            float f2 = Float.parseFloat(charSequence2.toString());
                            return String.format(Locale.CHINA, "%.2f", f1 * f2);
                        }
                    }
                })
                .compose(this.<CharSequence>bindUntilEvent(FragmentEvent.DESTROY))
                .subscribe(new Action1<CharSequence>() {
                    @Override
                    public void call(CharSequence charSequence) {
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
                    }
                });

        //计算按钮
        Observable.combineLatest(fundObservable,
                businessObservable,
                yearObservable,
                lastRateObservable,
                new Func4<CharSequence, CharSequence, CharSequence, CharSequence, Boolean>() {
                    @Override
                    public Boolean call(CharSequence charSequence, CharSequence charSequence2,
                                        CharSequence charSequence3, CharSequence charSequence4) {
                        boolean enable1 = false;
                        if (TextUtils.isEmpty(charSequence)) {
                            if (!TextUtils.isEmpty(charSequence2)) {
                                double d2 = Double.parseDouble(charSequence2.toString());
                                enable1 = d2 > 0;
                            }
                        } else if (TextUtils.isEmpty(charSequence2)) {
                            if (!TextUtils.isEmpty(charSequence)) {
                                double d1 = Double.parseDouble(charSequence.toString());
                                enable1 = d1 > 0;
                            }
                        } else {
                            double d1 = Double.parseDouble(charSequence.toString());
                            double d2 = Double.parseDouble(charSequence2.toString());
                            enable1 = d1 > 0 || d2 > 0;
                        }
                        boolean enable2 = !TextUtils.isEmpty(charSequence3) && Integer.parseInt(charSequence3.toString()) > 0;
                        boolean enable3 = !TextUtils.isEmpty(charSequence4);
                        return enable1 && enable2 && enable3;
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
                aet_times.setText(isChecked ? "1" : "1.1");
                lastFundRate = isChecked ? fundRate : fundRate * 1.1;
            }
        });
        setCalcClick(btn_calc);
    }


    /**
     * 商贷利率
     */
    private void setBusinessTips(double rate) {
        aet_loan_rate.setText(String.format(Locale.CHINA, "%.2f", rate));
        atv_tips_business.setText(String.format(Locale.CHINA, "最新商业贷款利率%.2f%%", rate));
    }

    /**
     * 公积金利率
     */
    private void setFundTips(double rate) {
        fundRate = rate;
        lastFundRate = rate;
        atv_tips_fund.setText(String.format(Locale.CHINA, "最新公积金贷款利率%.2f%%", rate));
    }

    @Override
    protected void loanTypeItemClick(String text) {
        atv_loan_type.setText(text);
    }

    @Override
    protected IPresenter createPresenter() {
        return null;
    }
}
