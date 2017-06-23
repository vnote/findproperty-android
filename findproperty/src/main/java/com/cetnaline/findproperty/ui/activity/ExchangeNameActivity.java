package com.cetnaline.findproperty.ui.activity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.base.BaseActivity;
import com.cetnaline.findproperty.entity.bean.UserInfoBean;
import com.cetnaline.findproperty.entity.result.BaseSingleResult;
import com.cetnaline.findproperty.presenter.IPresenter;

import java.util.HashMap;

import butterknife.BindView;
import io.rong.imlib.NativeObject;
import rx.Subscriber;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by diaoqf on 2016/8/20.
 */
public class ExchangeNameActivity extends BaseActivity {

    @BindView(R.id.name_input)
    EditText name_input;
    @BindView(R.id.submit)
    TextView submit;
    private CompositeSubscription mCompositeSubscription;
    @Override
    protected int getContentViewId() {
        return R.layout.act_exchange_name;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mCompositeSubscription = new CompositeSubscription();
        submit.setOnClickListener(v->{
            String name = name_input.getText().toString();
            if (name != null && name.trim().length() > 0) {
                mCompositeSubscription.add(ApiRequest.updateUserInfo(new HashMap(){
                    {
                        put("NickName",name);
                    }
                }).subscribe(new Subscriber<BaseSingleResult<UserInfoBean>>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        toast("修改姓名失败");
//                        showNetworkDialog("网络异常");
                    }

                    @Override
                    public void onNext(BaseSingleResult<UserInfoBean> integerBaseSingleResult) {
                        ExchangeNameActivity.this.setResult(RESULT_OK, getIntent().putExtra("name",name));
                        toast("修改姓名成功");
                        finish();
                    }
                }));
            } else {
                toast("请输入姓名");
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (mCompositeSubscription != null && mCompositeSubscription.hasSubscriptions()) {
            mCompositeSubscription.unsubscribe();
        }
        super.onDestroy();
    }

    @Override
    protected IPresenter createPresenter() {
        return null;
    }

    @Override
    protected void initToolbar() {
        toolbar.setNavigationOnClickListener((v) -> {
            onBackPressed();
        });
        toolbar.setTitle("修改姓名");
    }

    @Override
    protected String getTalkingDataPageName() {
        return "昵称修改";
    }
}
