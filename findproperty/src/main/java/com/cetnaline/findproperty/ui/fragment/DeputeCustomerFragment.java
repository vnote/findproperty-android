package com.cetnaline.findproperty.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aigestudio.wheelpicker.WheelPicker;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.bean.DeputeBean;
import com.cetnaline.findproperty.base.BaseFragment;
import com.cetnaline.findproperty.entity.ui.DeputePushBean;
import com.cetnaline.findproperty.entity.ui.EntrustAttachment;
import com.cetnaline.findproperty.inter.FragmentBack;
import com.cetnaline.findproperty.presenter.impl.DeputeCustomerPresenter;
import com.cetnaline.findproperty.presenter.ui.DeputeCustomerContract;
import com.cetnaline.findproperty.ui.activity.DeputeActivity;
import com.cetnaline.findproperty.ui.activity.DeputePhotoSelectActivity;
import com.cetnaline.findproperty.ui.activity.ExchangePhoneActivity;
import com.cetnaline.findproperty.utils.DataHolder;
import com.cetnaline.findproperty.utils.MyUtils;
import com.cetnaline.findproperty.utils.SchedulersCompat;
import com.cetnaline.findproperty.widgets.FormItemLayout;
import com.cetnaline.findproperty.widgets.MyBottomDialog;
import com.jakewharton.rxbinding.view.RxView;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.Observable;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by diaoqf on 2017/3/31.
 */

public class DeputeCustomerFragment extends BaseFragment<DeputeCustomerPresenter> implements DeputeCustomerContract.View,FragmentBack {
    @BindView(R.id.name)
    FormItemLayout name;
    @BindView(R.id.number)
    FormItemLayout number;
    @BindView(R.id.pics)
    FormItemLayout pics;
    @BindView(R.id.submit)
    TextView submit;
    @BindView(R.id.submit_layout)
    RelativeLayout submit_layout;
    @BindView(R.id.close_form)
    TextView close_form;

    private MyBottomDialog bottomWindow;   //底部选择框
    private WheelPicker picker1;
    private String picker1String;
    private CompositeSubscription mCompositeSubscription;
//    private List<EntrustAttachment> files;  //上传的附件
    private DeputePushBean deputePushBean;

    private DeputeBean deputedHouse;

    public static DeputeCustomerFragment getInstance() {
        return new DeputeCustomerFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frg_depute_customer;
    }

    @Override
    protected void init() {
//        deputePushBean = ((DeputeActivity)getActivity()).getDeputePushBean();
//        name.setText(deputePushBean.getOrderParam().getNickName());
//        number.setText(deputePushBean.getOrderParam().getMobile());
//        if (deputePushBean.getEntrustAttachment() != null && deputePushBean.getEntrustAttachment().size() > 0) {
//            pics.setExtText("已上传");
//        } else {
//            pics.setExtText("未上传");
//        }

        mCompositeSubscription = new CompositeSubscription();
        initpopUpwindow();
        name.setOnExtClickListener(()->{
            InputMethodManager inputMethodManager  = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(name.getWindowToken(), 0);
            mCompositeSubscription.add(Observable.timer(200, TimeUnit.MILLISECONDS)
                    .compose(SchedulersCompat.applyIoSchedulers())
                    .subscribe(num->{
                        bottomWindow.show();
                    }));
        });

        number.setImageListener(()->{
            Intent intent = new Intent(getActivity(),ExchangePhoneActivity.class);
            intent.putExtra(ExchangePhoneActivity.CALL_TYPE,ExchangePhoneActivity.CHECK);
            startActivityForResult(intent,101);
        });

        pics.setImageListener(()->{
            Intent intent = new Intent(getActivity(), DeputePhotoSelectActivity.class);
            if (deputePushBean.getEntrustAttachment() != null && deputePushBean.getEntrustAttachment().size() > 0) {
                intent.putExtra("pics", (Serializable) deputePushBean.getEntrustAttachment());
            }
            startActivityForResult(intent, 102);
        });

        RxView.clicks(submit).throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(aVoid -> {
                    if (name.getText() == null || name.getText().equals("")) {
                        toast("请输入您的称呼");
                        return;
                    }

                    if (number.getText() == null || number.getText().equals("")) {
                        toast("请输入联系方式");
                        return;
                    }

                    //判断当前委托是否可以提交(去重)
                   if (deputedHouse != null) {
                       if (deputedHouse.getProcessStatus() != -1 && deputedHouse.getProcessStatus() != 6 && deputedHouse.getProcessStatus() != 2) {
                           //是否为待处理
                            if (deputedHouse.getProcessStatus() == 1) {
                                //委托类型是否相同
                                if (deputedHouse.getEntrustType() == deputePushBean.getEntrustOrder().getEntrustType()) {
                                    MyUtils.showDiloag(getActivity(), R.layout.dialog_alert, 250, -1, true, (layout, dialog) -> {
                                        TextView title = (TextView) layout.findViewById(R.id.title);
                                        title.setText("该房源已申请委托，是否取消并重新委托？");
                                        TextView cancel = (TextView) layout.findViewById(R.id.cancel);
                                        cancel.setOnClickListener(v->dialog.dismiss());
                                        TextView submit = (TextView) layout.findViewById(R.id.submit);
                                        submit.setOnClickListener(v->{
                                            mPresenter.updateFormState(deputedHouse.getEntrustID());
                                            dialog.dismiss();
                                            showLoading();
                                        });
                                    });
                                } else {
                                    corePush(true);
                                }
                            } else {
                                MyUtils.showDiloag(getActivity(), R.layout.dialog_alert_single, 280, -1, true, (layout, dialog) -> {
                                    TextView title = (TextView) layout.findViewById(R.id.title);
                                    title.setText("该房源已申请委托，如有变更请与中原顾问联系。");
                                    TextView submit = (TextView) layout.findViewById(R.id.submit);
                                    submit.setOnClickListener(v->dialog.dismiss());
                                });
                            }
                       } else {
                           corePush(true);
                       }
                   } else {
                       corePush(true);
                   }
                });
    }

    /**
     * 提交委托数据
     */
    @Override
    public void corePush(boolean isShow) {
        //提交数据
        if (isShow) {
            showLoading();
        }

        deputePushBean.getOrderParam().setMobile(number.getText());
        deputePushBean.getOrderParam().setNickName(name.getText());
        deputePushBean.getOrderParam().setUserSex(name.getExtText());

        if (deputePushBean.getEntrustAttachment() == null || deputePushBean.getEntrustAttachment().size() == 0) {
            deputePushBean.setEntrustAttachment(null);
        }

        mPresenter.submitServer(deputePushBean);
    }


    private void initpopUpwindow() {
        LinearLayout dialog_layout = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.publish_source_bottom_layout, null);
        bottomWindow = new MyBottomDialog(getActivity());
        bottomWindow.setContentView(dialog_layout);

        picker1 = (WheelPicker) dialog_layout.findViewById(R.id.selector_1);
        picker1.setData(new ArrayList(){
            {
                add("先生");
                add("女士");
            }
        });

        picker1.setOnItemSelectedListener((picker, data, position) -> {
            picker1String = (String)data;
        });

        dialog_layout.findViewById(R.id.submit).setOnClickListener(v->{
            name.setExtText(picker1String == null ? "先生":picker1String);
            bottomWindow.dismiss();
        });
        dialog_layout.findViewById(R.id.cancel).setOnClickListener(v->{
            bottomWindow.dismiss();
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        deputePushBean = ((DeputeActivity) getActivity()).getDeputePushBean();
        name.setExtText(deputePushBean.getOrderParam().getUserSex()==null?"先生":deputePushBean.getOrderParam().getUserSex());
        name.setText(deputePushBean.getOrderParam().getNickName());
        if (deputePushBean.getOrderParam().getMobile() != null) {
            number.setText(deputePushBean.getOrderParam().getMobile());
        } else {
            number.setText(DataHolder.getInstance().getUserPhone());
        }
        if (deputePushBean.getEntrustAttachment() != null && deputePushBean.getEntrustAttachment().size() > 0) {
            pics.setExtText("已上传");
        } else {
            pics.setExtText("未上传");
        }

        //获取该房源是否已提交
        mPresenter.getHouseById(deputePushBean.getEntrustData().getHouseId()+"", deputePushBean.getEntrustOrder().getEntrustType()+"");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //电话
        if (requestCode == 101 && data!=null ) {
            number.setText(data.getStringExtra(ExchangePhoneActivity.PHONE_DATA_KEY));
        }
        //图片
        if (requestCode == 102 && data!=null) {
            deputePushBean.setEntrustAttachment((List<EntrustAttachment>) data.getSerializableExtra("pics"));
            boolean hasPic = false;
            for (int i=0;i<deputePushBean.getEntrustAttachment().size();i++) {
                if (deputePushBean.getEntrustAttachment().get(i) != null) {
                    hasPic = true;
                    break;
                }
            }

            if (deputePushBean.getEntrustAttachment() != null && hasPic) {
                pics.setExtText("已上传");
            } else {
                pics.setExtText("未上传");
            }
        }
    }

    @Override
    protected DeputeCustomerPresenter createPresenter() {
        return new DeputeCustomerPresenter();
    }

    @Override
    public void showLoading() {
        showLoadingDialog();
    }

    @Override
    public void dismissLoading() {
        cancelLoadingDialog();
    }

    @Override
    public void showError(String msg) {
        toast(msg);
    }

    @Override
    public void finishForm(int resultCode) {
        if (resultCode == 0) {
            toast("提交成功!");
            ((DeputeActivity)getActivity()).saveStep3();
            submit_layout.setVisibility(View.VISIBLE);
            close_form.setOnClickListener(v-> getActivity().finish());
        } else {
            toast("提交失败,请检查网络");
        }
    }

    @Override
    public void setPublishedDepute(DeputeBean bean) {
        deputedHouse = bean;
    }

    @Override
    public void saveData() {
        deputePushBean.getOrderParam().setMobile(number.getText());
        deputePushBean.getOrderParam().setNickName(name.getText());
        deputePushBean.getOrderParam().setUserSex(name.getExtText());

        if (deputePushBean.getEntrustAttachment() == null || deputePushBean.getEntrustAttachment().size() == 0) {
            deputePushBean.setEntrustAttachment(null);
        }
    }
}
