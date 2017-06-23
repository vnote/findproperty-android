package com.cetnaline.findproperty.presenter.impl;

import com.cetnaline.findproperty.api.bean.PriceTrendBean;
import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.presenter.BasePresenter;
import com.cetnaline.findproperty.presenter.ui.DeputePriceContract;
import com.cetnaline.findproperty.utils.SchedulersCompat;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

/**
 * Created by diaoqf on 2017/3/30.
 */

public class DeputePricePresenter extends BasePresenter<DeputePriceContract.View> implements DeputePriceContract.Presenter {

    @Override
    public void getPrices(String estateCode, String gScopeId, String type) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MONTH, -1);

        long end = calendar.getTimeInMillis()/1000;

        calendar.add(Calendar.MONTH, -6);
        long begin = calendar.getTimeInMillis()/1000;

        Map<String, String> eParam = new HashMap<>();
        if (estateCode == null || "".equals(estateCode)) {
            eParam.put("EstateCode", "0000");
        } else {
            eParam.put("EstateCode", estateCode);
        }
        eParam.put("PostType", type);  //S-R
        eParam.put("DealTimeBegin", begin+"");
        eParam.put("DealTimeEnd", end+"");

        Map<String, String> gParam = new HashMap<>();
        gParam.put("GscopeId",gScopeId);
        gParam.put("DealTimeBegin", begin+"");
        gParam.put("DealTimeEnd", end+"");
        gParam.put("PostType", type);

        //上海市房价趋势
        Map<String,String> cParam = new HashMap<>();
        cParam.put("DealTimeBegin", begin+"");
        cParam.put("DealTimeEnd", end+"");
        cParam.put("RegionId", "021");
        cParam.put("PostType", type);

        Subscription subscription = Observable.combineLatest(ApiRequest.getGscopeDeailPrice(gParam), ApiRequest.getEstateDeailPrice(eParam), ApiRequest.getGscopeDeailPrice(cParam)
                , (gscopeDealPriceBos, estateDealPriceBos, gscopeDealPriceBos2) -> {
                    return new PriceTrendBean(gscopeDealPriceBos, estateDealPriceBos, gscopeDealPriceBos2);  //区域，小区，城市
                })
                .subscribe(new Subscriber<PriceTrendBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        iView.setPrices(null);
                    }

                    @Override
                    public void onNext(PriceTrendBean priceTrendBean) {
                        iView.setPrices(priceTrendBean);
                    }
                });

        addSubscribe(subscription);
    }

    /**
     * 获取同小区房源数据
     * @param estateCode
     * @param type
     */
    @Override
    public void getHouseList(String estateCode, String type) {
        if (estateCode == null || "".equals(estateCode)) {
            iView.setHouseList(null);
        } else {
            Map<String, String> param = new HashMap<>();
            param.put("PageCount","100");
            param.put("ImageWidth","100");
            param.put("PostType",type);
            param.put("PageIndex","1");
            param.put("ImageHeight","100");
            param.put("EstateCode",estateCode);
            Subscription subscription = ApiRequest.getHouseList(param)
                    .subscribe(houseBos -> {
                        iView.setHouseList(houseBos);
                    }, throwable -> {
                        iView.setHouseList(null);
                        throwable.printStackTrace();
                    });
            addSubscribe(subscription);
        }

    }

    /**
     * 获取表单数据
     */
    @Override
    public void getFormdata() {
        addSubscribe(
                Observable.timer(200, TimeUnit.MILLISECONDS)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(aLong -> {
                    iView.reloadData();
            }));
    }
}
