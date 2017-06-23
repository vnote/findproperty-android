package com.cetnaline.findproperty.presenter.impl;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.cetnaline.findproperty.api.bean.HouseBo;
import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.db.entity.Staff;
import com.cetnaline.findproperty.entity.bean.StaffListBean;
import com.cetnaline.findproperty.entity.event.ShareEvent;
import com.cetnaline.findproperty.inter.RefreshListener;
import com.cetnaline.findproperty.api.ErrorHanding;
import com.cetnaline.findproperty.api.NetContents;
import com.cetnaline.findproperty.presenter.BasePresenter;
import com.cetnaline.findproperty.presenter.ui.AdviserDetailContract;
import com.cetnaline.findproperty.ui.activity.LoginActivity;
import com.cetnaline.findproperty.ui.fragment.HouseListFragment;
import com.cetnaline.findproperty.ui.fragment.MapFragment;
import com.cetnaline.findproperty.utils.DataHolder;
import com.cetnaline.findproperty.utils.DbUtil;
import com.cetnaline.findproperty.utils.MyUtils;
import com.cetnaline.findproperty.utils.RxBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by diaoqf on 2016/8/28.
 */
public class AdviserDetailPresenter extends BasePresenter<AdviserDetailContract.View> implements AdviserDetailContract.Presenter {
    @Override
    public void getFragments(RefreshListener listener1, RefreshListener listener2) {
        HouseListFragment fragment1 = HouseListFragment.getInstance(HouseListFragment.LIST_TYPE_SMALL, MapFragment.HOUSE_TYPE_SECOND);
        HouseListFragment fragment2 = HouseListFragment.getInstance(HouseListFragment.LIST_TYPE_SMALL,MapFragment.HOUSE_TYPE_RENT);
        fragment1.setRefreshListener(listener1);
        fragment2.setRefreshListener(listener2);
        List<Fragment> fragments = new ArrayList(){
            {
                add(fragment1);
                add(fragment2);
            }
        };
        iView.setFragments(fragments);
    }

    @Override
    public void getDepartments(String id, String no) {
        //获取服务小区
        Subscription subscription = ApiRequest.getStaffDetail(no).subscribe((staffListBeanBaseResult)-> {
            if (staffListBeanBaseResult != null) {
                iView.setStaffMsg(staffListBeanBaseResult.ServiceEstates);
                //加载头像
                iView.loadHeadImage(staffListBeanBaseResult.getStaffImg());
                iView.setDepartment(staffListBeanBaseResult.getStoreName());
                Observable.just(staffListBeanBaseResult)
                        .map((staffListBean)->{
                            Staff staff = DbUtil.addStaff(staffListBean);
                            return staff;
                        }).subscribeOn(Schedulers.io())
                        .subscribe((s)->{
                            iView.setStaff(s);
                        });
            }
        },throwable -> {throwable.printStackTrace();});
        addSubscribe(subscription);
    }

    @Override
    public void registerShareEvent(Class<ShareEvent> clz) {
        //分享事件监听
        Subscription rxSubscription = RxBus.getDefault().toObservable(clz)
                .subscribe(shareEvent -> {
                    switch (shareEvent.getEventType()){
                        case ShareEvent.EVENT_TYPE_SUCCESS:
                            iView.showShareDialog(false);
                            iView.showMessage("分享成功");
                            break;
                        case ShareEvent.EVENT_TYPE_CANCLE:
                            iView.showMessage("分享被取消");
                            break;
                        case ShareEvent.EVENT_TYPE_FAIL:
                            iView.showMessage("分享失败");
                            break;
                    }
                });

        addSubscribe(rxSubscription);
    }

    @Override
    public void showCallDialog(Activity context,String label,StaffListBean adviser) {
        MyUtils.toCall400(context,adviser.MobileBy400,adviser.CnName);
    }

    @Override
    public void openConversation(Activity context,StaffListBean adviser) {
        if (DataHolder.getInstance().isUserLogin()) {
            String targetId = "s_021_" + adviser.StaffNo.toLowerCase();
            String title = adviser.CnName;
            RongIM.getInstance().startConversation(context, Conversation.ConversationType.PRIVATE, targetId, title);
        } else {
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
        }
    }


    public void loadSale(int currentPage,StaffListBean adviser){
        Subscription subscription = ApiRequest.getStaffHouseList(new HashMap(){
            {
                put("StaffNo",adviser.StaffNo);
                put("PageIndex",currentPage+"");
                put("StaffNo",adviser.StaffNo);
                put("PageCount","10");
                put("PostType","s");   //s
            }
        }).subscribe(new Action1<List<HouseBo>>() {
            @Override
            public void call(List<HouseBo> houseBos) {
                if (houseBos == null || houseBos.size() == 0) {
                    iView.setSaleAllLoad(true);
                    return;
                }
                for (HouseBo houseBo : houseBos) {
                    houseBo.setDefaultImage(NetContents.IMG_BASE_URL + houseBo.getDefaultImage() + "_640x400.jpg");
                }
                iView.setSaleFragmentData(houseBos);
                iView.addSalePage();
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                iView.showMessage(ErrorHanding.handleError(throwable));
                iView.setFragmentNoData(0);
            }
        });

        addSubscribe(subscription);
    }


    public void loadRent(int currentPage,StaffListBean adviser){
        Subscription subscription = ApiRequest.getStaffHouseList(new HashMap(){
            {
                put("StaffNo",adviser.StaffNo);
                put("PageIndex",currentPage+"");
                put("StaffNo",adviser.StaffNo);
                put("PageCount","10");
                put("PostType","r");   //s
            }
        }).subscribe(new Action1<List<HouseBo>>() {
            @Override
            public void call(List<HouseBo> houseBos) {
                if (houseBos == null || houseBos.size() == 0) {
                    iView.setRentAllLoad(true);
                    return;
                }
                for (HouseBo houseBo:houseBos) {
                    houseBo.setDefaultImage(NetContents.IMG_BASE_URL +houseBo.getDefaultImage()+"_640x400.jpg");
                }
                iView.setRendFragmentData(houseBos);
                iView.addRentPage();
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                iView.showMessage(ErrorHanding.handleError(throwable));
                iView.setFragmentNoData(1);
            }
        });
        addSubscribe(subscription);
    }

}































