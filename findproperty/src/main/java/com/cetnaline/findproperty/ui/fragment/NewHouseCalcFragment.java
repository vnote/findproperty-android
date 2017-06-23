package com.cetnaline.findproperty.ui.fragment;

import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.entity.ui.TaxCalculateBo;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.trello.rxlifecycle.FragmentEvent;

import butterknife.BindView;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func2;

/**
 * Created by diaoqf on 2016/8/16.
 */
public class NewHouseCalcFragment extends BaseTaxCalFragment {

    @BindView(R.id.aet_house_area)
    AppCompatEditText aet_house_area;
    @BindView(R.id.aet_house_unit_price)
    AppCompatEditText aet_house_unit_price;
    @BindView(R.id.btn_tax_fragment)
    TextView btn_tax_fragment;


    @Override
    protected void startCalculate() {
        TaxCalculateBo taxCalculateBo = new TaxCalculateBo();
        taxCalculateBo.modeType = 2;
        taxCalculateBo.houseArea = Double.parseDouble(aet_house_area.getText().toString());
        taxCalculateBo.houseUnitPrice = Double.parseDouble(aet_house_unit_price.getText().toString());
        taxCalculateCallback.callBack(taxCalculateBo);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frg_calc_new_house;
    }

    @Override
    protected void init() {
        Observable<CharSequence> houseAreaObservable = RxTextView.textChanges(aet_house_area);//房屋面积
        Observable<CharSequence> houseUnitPriceObservable = RxTextView.textChanges(aet_house_unit_price); //房屋单价

        //计算按钮
        Observable.combineLatest(houseAreaObservable, houseUnitPriceObservable,
                new Func2<CharSequence, CharSequence, Boolean>() {
                    @Override
                    public Boolean call(CharSequence charSequence, CharSequence charSequence2) {

                        boolean enable1 = !TextUtils.isEmpty(charSequence);
                        boolean enable2 = !TextUtils.isEmpty(charSequence2);
                        return enable1 && enable2;
                    }
                }).compose(this.<Boolean>bindUntilEvent(FragmentEvent.DESTROY))
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        btn_tax_fragment.setEnabled(aBoolean);
                    }
                });

        setCalcClick(btn_tax_fragment);
    }

    @Override
    protected IPresenter createPresenter() {
        return null;
    }
}
