package com.cetnaline.findproperty.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.base.BaseFragment;
import com.cetnaline.findproperty.entity.bean.ConsultFormBean;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.cetnaline.findproperty.ui.activity.SearchActivity;
import com.cetnaline.findproperty.utils.DataHolder;
import com.cetnaline.findproperty.utils.SchedulersCompat;
import com.cetnaline.findproperty.widgets.MyBottomDialog;
import com.cetnaline.findproperty.widgets.RemarkEditTextLayout;
import com.google.gson.Gson;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.orhanobut.logger.Logger;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicMarkableReference;

import butterknife.BindView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by diaoqf on 2016/11/30.
 * 举报房源
 */

public class HouseTipOffsFragment extends BaseFragment {

    @BindView(R.id.object_name)
    TextView object_name;
    @BindView(R.id.object_reason)
    TextView object_reason;
    @BindView(R.id.submit)
    TextView submit;
    @BindView(R.id.remark)
    RemarkEditTextLayout remark;
    @BindView(R.id.form)
    LinearLayout form;
    @BindView(R.id.basic_layout)
    LinearLayout basic_layout;
    @BindView(R.id.detail_layout)
    LinearLayout detail_layout;

    private MyBottomDialog sortWindow;
    private String villageId="",villageName="";
    private CompositeSubscription mCompositeSubscription;

    @Override
    protected int getLayoutId() {
        return R.layout.frg_house_tip_offs;
    }

    @Override
    protected void init() {
        mCompositeSubscription = new CompositeSubscription();
        initpopUpwindow();

        remark.setContentUpdatelistener(length -> {
            if (length > 0 ) {
                submit.setEnabled(true);
            } else {
                submit.setEnabled(false);
            }
        });

        basic_layout.setOnClickListener(v->{
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            intent.putExtra(SearchActivity.SEARCH_TYPE_KEY, SearchActivity.SEARCH_TYPE_ESTATE);
            intent.putExtra(SearchActivity.IS_GET_DATA, true);
            startActivityForResult(intent, 1001);
        });

        detail_layout.setOnClickListener(v->{
            if (sortWindow.isShowing()) {
                sortWindow.dismiss();
            } else {
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(detail_layout.getWindowToken(), 0);
                Observable.timer(500, TimeUnit.MILLISECONDS)
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(num->{
                            sortWindow.show();
                        });
            }
        });

        submit.setOnClickListener(v->{
            if (object_name.getText().equals("")) {
                toast("请选择举报小区");
                return;
            }
            if (object_reason.getText().equals("")) {
                toast("请选择举报原因");
                return;
            }
            if (remark.getText() == null || remark.getText().equals("")) {
                toast("请填写举报内容描述");
                return;
            }
            submit.setEnabled(false);
            showLoadingDialog();
            ConsultFormBean bean = new ConsultFormBean(
                    "021", "APP", "",
                    remark.getText(),
                    DataHolder.getInstance().getUserId(),
                    2, "", DataHolder.getInstance().getUserPhone() != null ? DataHolder.getInstance().getUserPhone() : "",
                    object_reason.getText().toString(), "", "",
                    1, villageId);
            Gson gson = new Gson();
            Logger.i(gson.toJson(bean));
            mCompositeSubscription.add(ApiRequest.FeedBackConsultMessage(
                    bean
                    ).subscribe(new Action1<Integer>() {
                @Override
                public void call(Integer integer) {
                    cancelLoadingDialog();
                    toast("提交成功");
                    getActivity().setResult(1001,new Intent());
                    getActivity().finish();
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    throwable.printStackTrace();
                    toast("提交失败");
                    object_reason.setText("");
                    villageId = "";
                    villageName = "";
                    object_name.setText("");
                    remark.setText("");
                    submit.setEnabled(true);
                    cancelLoadingDialog();
                }
            }));
        });
    }

    private void initpopUpwindow() {
        LinearLayout dialog_layout = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.tip_offs_reason_layout, null);
        sortWindow = new MyBottomDialog(getActivity());
        sortWindow.setContentView(dialog_layout);
        for (int i=0; i<9;i=i+2) {
            dialog_layout.getChildAt(i).setOnClickListener(vv->{
                TextView tmp = (TextView)vv;
                ((TextView)dialog_layout.getChildAt(0)).setTextColor(getResources().getColor(R.color.grayText));
                ((TextView)dialog_layout.getChildAt(2)).setTextColor(getResources().getColor(R.color.grayText));
                ((TextView)dialog_layout.getChildAt(4)).setTextColor(getResources().getColor(R.color.grayText));
                ((TextView)dialog_layout.getChildAt(6)).setTextColor(getResources().getColor(R.color.grayText));
                ((TextView)dialog_layout.getChildAt(8)).setTextColor(getResources().getColor(R.color.grayText));
                tmp.setTextColor(getResources().getColor(R.color.appBaseColor));
                object_reason.setText(tmp.getText());
                sortWindow.dismiss();
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && data != null) {
            villageName = data.getStringExtra("EstateName");
            villageId = data.getStringExtra("EstateCode");
            object_name.setText(villageName);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCompositeSubscription != null && !mCompositeSubscription.isUnsubscribed()) {
            mCompositeSubscription.unsubscribe();
        }
    }

    @Override
    protected IPresenter createPresenter() {
        return null;
    }
}
