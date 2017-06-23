package com.cetnaline.findproperty.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.base.BaseFragment;
import com.cetnaline.findproperty.entity.bean.ConsultFormBean;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.cetnaline.findproperty.ui.activity.AdviserListActivity;
import com.cetnaline.findproperty.ui.activity.CommentActivity;
import com.cetnaline.findproperty.utils.DataHolder;
import com.cetnaline.findproperty.utils.SchedulersCompat;
import com.cetnaline.findproperty.widgets.MyBottomDialog;
import com.cetnaline.findproperty.widgets.RemarkEditTextLayout;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.Observable;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by diaoqf on 2016/10/24.
 * 投诉经纪人
 */

public class ComplainFragment extends BaseFragment {

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

//    private PopupWindow window;
    private MyBottomDialog sortWindow;

    private String staffName="",staffNo="",storeName="",storeId="";

    private CompositeSubscription mCompositeSubscription;

    @Override
    protected int getLayoutId() {
        return R.layout.frg_complain;
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
            Intent intent = new Intent(getActivity(), AdviserListActivity.class);
            intent.putExtra(AdviserListActivity.SELECT_ADVISER,true);
            startActivityForResult(intent,1001);
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
                toast("请选择要投诉经纪人");
                return;
            }
            if (object_reason.getText().equals("")) {
                toast("请选择投诉原因");
                return;
            }
            if (remark.getText() == null || remark.getText().equals("")) {
                toast("请填写投诉描述");
                return;
            }
            submit.setEnabled(false);
            showLoadingDialog();
            mCompositeSubscription.add(ApiRequest.FeedBackConsultMessage(
                    new ConsultFormBean(
                            "021", "APP", "",
                            remark.getText(),
                            DataHolder.getInstance().getUserId(),
                            2, "",
                            DataHolder.getInstance().getUserPhone() != null ? DataHolder.getInstance().getUserPhone() : "",
                            object_reason.getText().toString(),
                            staffNo.equals("")?"":staffNo,
                            staffName.equals("")?"":staffName,
                            2, "")

            ).subscribe(new Action1<Integer>() {
                @Override
                public void call(Integer integer) {
                    String type = ((CommentActivity)getActivity()).getType();
                    if (type == null || type.equals(CommentActivity.NORMAL_TYPE)) {
                        form.setVisibility(View.GONE);
                        ((CommentActivity)getActivity()).showSuccess("投诉成功", "我们已收到您的投诉，上海中原坚决维护客户权益，我们会认真处理您的投诉，感谢您对上海中原的信任与支持！");
                    } else if (type.equals(CommentActivity.CHAT_TYPE)) {
                        cancelLoadingDialog();
                        toast("提交成功");
                        getActivity().setResult(1001,new Intent());
                        getActivity().finish();
                    }

                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    throwable.printStackTrace();
                    object_reason.setText("");
                    staffName = "";
                    staffNo = "";
                    object_name.setText("");
                    remark.setText("");
                    submit.setEnabled(true);
                    cancelLoadingDialog();
                }
            }));
        });
    }

    private void initpopUpwindow() {
        LinearLayout dialog_layout = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.complain_reason_layout, null);
        sortWindow = new MyBottomDialog(getActivity());
        sortWindow.setContentView(dialog_layout);
        for (int i=0; i<11;i=i+2) {
            dialog_layout.getChildAt(i).setOnClickListener(vv->{
                TextView tmp = (TextView)vv;
                ((TextView)dialog_layout.getChildAt(0)).setTextColor(getResources().getColor(R.color.grayText));
                ((TextView)dialog_layout.getChildAt(2)).setTextColor(getResources().getColor(R.color.grayText));
                ((TextView)dialog_layout.getChildAt(4)).setTextColor(getResources().getColor(R.color.grayText));
                ((TextView)dialog_layout.getChildAt(6)).setTextColor(getResources().getColor(R.color.grayText));
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
            staffName = data.getStringExtra("name");
            staffNo = data.getStringExtra("id");
            storeName = data.getStringExtra("storeName");
//            storeId = data.getStringExtra("storeId");
            object_name.setText(staffName);
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
