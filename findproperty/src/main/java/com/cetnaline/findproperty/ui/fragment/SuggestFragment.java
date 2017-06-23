package com.cetnaline.findproperty.ui.fragment;

import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.base.BaseFragment;
import com.cetnaline.findproperty.entity.bean.ConsultFormBean;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.cetnaline.findproperty.ui.activity.CommentActivity;
import com.cetnaline.findproperty.utils.DataHolder;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.HashMap;

import butterknife.BindView;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by diaoqf on 2016/10/24.
 * 建议反馈
 */

public class SuggestFragment extends BaseFragment {

    @BindView(R.id.text)
    EditText text;
    @BindView(R.id.lest_number)
    TextView lest_number;
    @BindView(R.id.submit)
    TextView submit;
    @BindView(R.id.form)
    LinearLayout form;

    private CompositeSubscription mCompositeSubscription;

    @Override
    protected int getLayoutId() {
        return R.layout.frg_suggest;
    }

    @Override
    protected void init() {
        mCompositeSubscription = new CompositeSubscription();
        RxTextView.textChanges(text)
                .subscribe(s->{
                    lest_number.setText(s.length()+"");
                    if (s.length() > 0 ) {
                        submit.setEnabled(true);
                    } else {
                        submit.setEnabled(false);
                    }
                });

        submit.setOnClickListener(v->{
            if (text.getText().toString() == null || text.getText().toString().equals("")) {
                toast("请输入建议反馈内容");
                return;
            }
            submit.setEnabled(false);
            showLoadingDialog();
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            mCompositeSubscription.add(ApiRequest.FeedBackConsultMessage(
                    new ConsultFormBean(
                            "021", "APP", "", text.getText().toString(),
                            DataHolder.getInstance().getUserId(),
                            1, "", DataHolder.getInstance().getUserPhone(),
                            "", "", "", -1, "")
            ).subscribe(new Action1<Integer>() {
                @Override
                public void call(Integer integer) {
                    form.setVisibility(View.GONE);
                    ((CommentActivity)getActivity()).showSuccess(null, null);
                    cancelLoadingDialog();
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    throwable.printStackTrace();
                    toast("网络繁忙，请稍后尝试");
                    text.setText("");
                    submit.setEnabled(true);
                    cancelLoadingDialog();
                }
            }));
        });
    }

    @Override
    protected IPresenter createPresenter() {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCompositeSubscription != null && !mCompositeSubscription.isUnsubscribed()) {
            mCompositeSubscription.unsubscribe();
        }
    }
}
