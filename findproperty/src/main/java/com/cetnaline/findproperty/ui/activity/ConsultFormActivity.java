package com.cetnaline.findproperty.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.base.BaseActivity;
import com.cetnaline.findproperty.db.entity.GScope;
import com.cetnaline.findproperty.entity.bean.ConsultFormBean;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.cetnaline.findproperty.utils.DataHolder;
import com.cetnaline.findproperty.utils.DbUtil;
import com.cetnaline.findproperty.utils.SchedulersCompat;
import com.cetnaline.findproperty.utils.StatusBarCompat;
import com.cetnaline.findproperty.widgets.RemarkEditTextLayout;
import com.cetnaline.findproperty.widgets.SingleSelectGridView;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import rx.Observable;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by lebro on 2016/11/25.
 * 法律法规咨询
 */

public class ConsultFormActivity extends BaseActivity {
    public static String HAS_SCOPE = "has_scope";

    @BindView(R.id.user_name)
    EditText user_name;
    @BindView(R.id.user_phone)
    TextView user_phone;
    @BindView(R.id.remark)
    RemarkEditTextLayout remark;
    @BindView(R.id.submit)
    TextView submit;

    @BindView(R.id.scope_layout)
    LinearLayout scope_layout;
    @BindView(R.id.scope_gridview)
    SingleSelectGridView scope_gridview;

    private SimpleAdapter sim_adapter;

    private CompositeSubscription mCompositeSubscription;

    private List<GScope> scopeList;
    private int gscopeId = -1;    //当前选中的区域id

    @Override
    protected int getContentViewId() {
        return R.layout.act_consult_form;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        mCompositeSubscription = new CompositeSubscription();

        remark.setContentUpdatelistener(length -> {
            if (length > 0) {
                submit.setEnabled(true);
            } else {
                submit.setEnabled(false);
            }
        });

        if (getIntent().getBooleanExtra(HAS_SCOPE, false)) {
            scope_layout.setVisibility(View.VISIBLE);
            Observable.just(DbUtil.getGScopeChild(21))
                    .map(gScopes -> {
                        List<Map<String,String>> names = new ArrayList<Map<String, String>>();
                        scopeList = new ArrayList<GScope>();
                        for (GScope gScope:gScopes) {
                            if (gScope.getGScopeName().equals("其他") || gScope.getGScopeName().equals("青浦") || gScope.getGScopeName().equals("南汇")) {
                                continue;
                            }
                            scopeList.add(gScope);
                            Map<String,String> map = new HashMap<String, String>();
                            map.put("text",gScope.getGScopeName());
                            names.add(map);
                        }
                        return names;
                    })
                    .compose(SchedulersCompat.applyIoSchedulers())
                    .subscribe(names->{
                        sim_adapter = new SimpleAdapter(ConsultFormActivity.this, names,R.layout.scope_item,new String[]{"text"},new int[]{R.id.text});
                        scope_gridview.setAdapter(sim_adapter);
                        scope_gridview.setOnItemClickListener((parent, view, position, id) -> {
                            scope_gridview.setLastPosition(position);
                            gscopeId = scopeList.get(position).getGScopeId();
                        });
                    },throwable -> throwable.printStackTrace());
        } else {
            scope_layout.setVisibility(View.GONE);
        }

        if (DataHolder.getInstance().getUserPhone() != null) {
            user_phone.setText(DataHolder.getInstance().getUserPhone());
        }

        user_phone.setOnClickListener(v->{
            Intent intent = new Intent(this,ExchangePhoneActivity.class);
            intent.putExtra(ExchangePhoneActivity.CALL_TYPE,ExchangePhoneActivity.CHECK);
            startActivityForResult(intent,101);
        });

        submit.setOnClickListener(v->{
            if (user_name.getText().toString() == null || user_name.getText().toString().equals("")) {
                toast("请输入称呼");
                return;
            }
            if (user_phone.getText() == null || user_phone.getText().equals("")) {
                toast("请填写联系方式");
                return;
            }

            if (getIntent().getBooleanExtra(HAS_SCOPE, false) && gscopeId == -1) {
                toast("请选择区域");
            }

            if (remark.getText()== null || remark.getText().equals("")) {
                toast("请填写咨询内容");
                return;
            }
            submit.setEnabled(false);
            showLoadingDialog();
            ConsultFormBean bean = null;
            if (getIntent().getBooleanExtra(HAS_SCOPE, false)) {
                bean = new ConsultFormBean(
                        "021", "APP", "",
                        remark.getText(),
                        DataHolder.getInstance().getUserId(),
                        4, user_name.getText().toString(),
                        user_phone.getText().toString(),
                        "", "", "", -1,
                        gscopeId+"");
            } else {
                bean = new ConsultFormBean(
                        "021", "APP", "",
                        remark.getText(),
                        DataHolder.getInstance().getUserId(),
                        3, user_name.getText().toString(),
                        user_phone.getText().toString(),
                        "", "", "", -1, "");
            }
//            Gson gson = new Gson();
//            Logger.i(gson.toJson(bean));
            mCompositeSubscription.add(ApiRequest.FeedBackConsultMessage(bean).subscribe(integer -> {
                toast("提交成功");
                cancelLoadingDialog();
                setResult(1001,new Intent());
                finish();
            }, throwable -> {
                toast("网络繁忙，请稍后尝试");
//                showNetworkDialog("提交失败");
                submit.setEnabled(true);
                cancelLoadingDialog();
                throwable.printStackTrace();
            }));

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && data!=null ) {
            user_phone.setText(data.getStringExtra(ExchangePhoneActivity.PHONE_DATA_KEY));
        }
    }

    @Override
    protected IPresenter createPresenter() {
        return null;
    }

    @Override
    protected void initToolbar() {
        StatusBarCompat.setStatusBarColor(this, Color.WHITE);
        toolbar.setNavigationOnClickListener((v)->onBackPressed());
        toolbar.setTitle("填写咨询内容");
    }

    @Override
    protected String getTalkingDataPageName() {
        return "法律法规咨询";
    }
}
