package com.cetnaline.findproperty.ui.fragment;

import android.view.View;

import com.cetnaline.findproperty.base.BaseFragment;
import com.cetnaline.findproperty.entity.ui.TaxCalculateBo;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.jakewharton.rxbinding.view.RxView;
import com.trello.rxlifecycle.FragmentEvent;

import java.util.concurrent.TimeUnit;

import rx.functions.Action1;

/**
 * Created by diaoqf on 2016/8/16.
 */
public abstract class BaseTaxCalFragment extends BaseFragment {
    protected TaxCalculateCallback taxCalculateCallback;

    public void setTaxCalculateCallback(TaxCalculateCallback taxCalculateCallback) {
        this.taxCalculateCallback = taxCalculateCallback;
    }

    /**
     * 设置计算点击
     */
    protected void setCalcClick(View view) {
        RxView.clicks(view)
                .throttleFirst(1, TimeUnit.SECONDS)
                .compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY))
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        startCalculate();
                    }
                });
    }

    /**
     * 开始计算
     */
    protected abstract void startCalculate();


    public interface TaxCalculateCallback {
        void callBack(TaxCalculateBo taxCalculateBo);
    }

}
