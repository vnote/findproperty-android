package com.cetnaline.findproperty.ui.fragment;

import android.content.Intent;
import android.widget.TextView;

import com.cetnaline.findproperty.AppContents;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.base.BaseFragment;
import com.cetnaline.findproperty.base.BaseFragmentActivity;
import com.cetnaline.findproperty.api.NetContents;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.cetnaline.findproperty.ui.activity.DeputeActivity;
import com.cetnaline.findproperty.ui.activity.EntrustActivity;
import com.cetnaline.findproperty.ui.activity.LoginActivity;
import com.cetnaline.findproperty.ui.activity.WebActivity;
import com.cetnaline.findproperty.utils.DataHolder;
import com.cetnaline.findproperty.utils.MyUtils;
import com.cetnaline.findproperty.utils.SchedulersCompat;

import butterknife.OnClick;
import rx.subscriptions.CompositeSubscription;

/**
 * 发现-委托
 * Created by lebro on 2016/12/18.
 */

public class FoundShortcutFragment extends BaseFragment {

    private CompositeSubscription compositeSubscription;

    /**
     * 发布房源
     */
    @OnClick(R.id.publish_source)
    public void leaseClick(){
        if (DataHolder.getInstance().isUserLogin()){
            compositeSubscription.add(
                    ApiRequest.entrustCountRequest(
                            DataHolder.getInstance().getUserId())
                            .compose(SchedulersCompat.applyIoSchedulers())
                            .subscribe(integer -> {
                                if (integer < AppContents.DEPUTE_COUNT) {
                                    Intent intent = new Intent(getActivity(), DeputeActivity.class);
                                    startActivity(intent);
                                } else {
//                                    toast("您的委托数量已达上限");
                                    MyUtils.showDiloag(getActivity(), R.layout.dialog_alert_single, 250, -1, true, (layout, dialog) -> {
                                        TextView title = (TextView) layout.findViewById(R.id.title);
                                        title.setText("您的委托数量已达上限");
                                        TextView submit = (TextView) layout.findViewById(R.id.submit);
                                        submit.setOnClickListener(v-> dialog.dismiss());
                                    });
                                }
                            }, throwable -> {
                                throwable.printStackTrace();
                                toast("连接服务器失败，请稍后再试");
                            }));
        }else {
            toast("请先登录");
//            DataHolder.getInstance().setChoiceIntent(LoginActivity.LOGIN_INTENT_ENTRUST);
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.putExtra(LoginActivity.OPEN_TYPE, LoginActivity.LOGIN_INTENT_SHORTCUT);
            startActivity(intent);
        }
    }
    @OnClick(R.id.entrust_home_my)
    public void saleClick(){
        if (DataHolder.getInstance().isUserLogin()){

            Intent intent = new Intent(getActivity(), EntrustActivity.class);
            intent.putExtra(BaseFragmentActivity.FRAGMENT_TYPE, EntrustActivity.ENTRUST_TYPE_MY);
            startActivity(intent);

        }else {
            toast("请先登录");
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }
    }
    @OnClick(R.id.price_tool)
    public void myClick(){
//        Intent intent = new Intent(getActivity(), EntrustActivity.class);
//        intent.putExtra(BaseFragmentActivity.FRAGMENT_TYPE, EntrustActivity.ENTRUST_TYPE_MY);
//        startActivity(intent);

        Intent intent = new Intent(getActivity(), WebActivity.class);
        intent.putExtra(WebActivity.TARGET_URL, NetContents.ENTRUST_PRICE);
        intent.putExtra(WebActivity.TITLE_HIDDEN_KEY, true);
        startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frg_found_shortcut;
    }

    @Override
    protected void init() {
        compositeSubscription = new CompositeSubscription();
    }

    @Override
    protected IPresenter createPresenter() {
        return null;
    }
}
