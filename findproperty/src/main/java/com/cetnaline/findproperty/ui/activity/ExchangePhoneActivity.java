package com.cetnaline.findproperty.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.EditText;
import android.widget.TextView;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.base.BaseActivity;
import com.cetnaline.findproperty.entity.bean.UserInfoBean;
import com.cetnaline.findproperty.entity.event.NormalEvent;
import com.cetnaline.findproperty.entity.result.BaseLoginResult;
import com.cetnaline.findproperty.entity.result.BaseSingleResult;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.cetnaline.findproperty.ui.fragment.EntrustFragment;
import com.cetnaline.findproperty.utils.DataHolder;
import com.cetnaline.findproperty.utils.RxBus;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import butterknife.BindView;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by diaoqf on 2016/8/20.
 */
public class ExchangePhoneActivity extends BaseActivity{
    private static final int REQUEST_PERMISSION_CODE = 110;
    public static final String CALL_TYPE = "call_type";
    public static final String UPDATE= "update";
    public static final String CHECK = "check";

    public static final String PHONE_DATA_KEY = "PHONE_DATA_KEY";
    private CompositeSubscription mCompositeSubscription;
    private String type;

    @BindView(R.id.phone)
    EditText phone;
    @BindView(R.id.code)
    EditText code;
    @BindView(R.id.tv_code)
    TextView tv_code;
    @BindView(R.id.submit)
    TextView submit;

    @Override
    protected int getContentViewId() {
        return R.layout.act_exchange_phone;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();
        mCompositeSubscription = new CompositeSubscription();
        if (bundle != null) {
            type = getIntent().getExtras().getString(CALL_TYPE, UPDATE);
        } else {
            type = UPDATE;
        }

        if (type.equals(UPDATE)) {
            toolbar.setTitle("绑定手机");
        } else {
            toolbar.setTitle("填写联系方式");
        }

        String patten = "^1[3-9]\\d{9}$"; //"^1(3[0-9]|4[57]|5[0-35-9]|8[0-9]|7[0-9])\\d{8}$";
        Pattern r = Pattern.compile(patten);
        //获取验证码
        tv_code.setOnClickListener(v->{
            //申请读取短信权限
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, REQUEST_PERMISSION_CODE);
            }
            String number = phone.getText().toString();
            code.requestFocus();
            if (r.matcher(number).matches()) {
                if (!type.equals(CHECK)) {
                    mCompositeSubscription.add(ApiRequest.checkPhoneIsBind(new HashMap() {
                        {
                            put("LoginName", number);
                            put("UserID", DataHolder.getInstance().getUserId());
                        }
                    }).subscribe(new Action1<String>() {
                        @Override
                        public void call(String s) {
                            toast("手机号已绑定，请更换手机号");
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            timer(number);
                            throwable.printStackTrace();
                        }
                    }));
                } else {
                    timer(number);
                }
            } else {
                toast("请输入正确的手机号");
            }
        });

        submit.setOnClickListener(v->{

            mCompositeSubscription.add(ApiRequest.verificationCode(phone.getText().toString(),code.getText().toString())
                    .subscribe( result ->{
                        if (result==0){
                            if (type.equals(UPDATE)) {
                                ApiRequest.updateUserInfo(new HashMap(){
                                    {
                                        put("Phone",phone.getText().toString());
                                        if (type.equals(UPDATE)) {
                                            put("IsBind","1");
                                        }
                                    }
                                }).subscribe(new Subscriber<BaseSingleResult<UserInfoBean>>() {
                                    @Override
                                    public void onCompleted() {}

                                    @Override
                                    public void onError(Throwable e) {
                                        e.printStackTrace();
                                    }

                                    @Override
                                    public void onNext(BaseSingleResult<UserInfoBean> integerBaseSingleResult) {
                                        if (integerBaseSingleResult.ResultNo == -1) {
                                            toast("手机号保存成功");
                                        } else {
                                            ExchangePhoneActivity.this.setResult(RESULT_OK, getIntent().putExtra("phone", phone.getText().toString()));
                                            finish();
                                        }
                                    }
                                });

                            } else if (type.equalsIgnoreCase(CHECK)){
                                Intent intent = new Intent();
                                intent.putExtra(PHONE_DATA_KEY, phone.getText().toString());
                                ExchangePhoneActivity.this.setResult(EntrustFragment.RESULT_CODE_PHONE, intent);
                                finish();
                                toast("手机号保存成功");
                            }
                        }
                    }, throwable -> {
                        throwable.printStackTrace();
                        if (type.equals(UPDATE)) {
                            toast("验证码错误或已过期");
                        }
                    }));
        });

        mCompositeSubscription.add(
                RxBus.getDefault().toObservable(NormalEvent.class)
                        .filter(normalEvent -> normalEvent.type == NormalEvent.SMS_CODE)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(normalEvent -> {
                            code.setText(normalEvent.data);
                        }));
    }

//    private void registerSMSListener() {
//        //注册短信监听
//        SMSContent content = new SMSContent(null,this,"\\d{6}");
//        // 注册短信变化监听
//        this.getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, content);
//        mCompositeSubscription.add(
//                RxBus.getDefault().toObservable(NormalEvent.class)
//                        .filter(normalEvent -> normalEvent.type == NormalEvent.SMS_CODE)
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(normalEvent -> {
//                            code.setText(normalEvent.data);
//                        }));
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults!=null && grantResults.length>0){
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    toast("获取短信读取权限失败，请在设置页面为应用开启");
                }
            }else {
                toast("获取短信读取权限失败，请在设置页面为应用开启");
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 验证码计时
     */
    private void timer(String number){
        tv_code.setEnabled(false);
        final int[] seconds = {60};
        final boolean[] required = {false};
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(()->{
                    if (!required[0]) {
                        mCompositeSubscription.add(ApiRequest.sendSms(number)
                                .subscribe(new Subscriber<BaseLoginResult>() {
                                    @Override
                                    public void onCompleted() {}
                                    @Override
                                    public void onError(Throwable e) {e.printStackTrace();}
                                    @Override
                                    public void onNext(BaseLoginResult baseLoginResult) {
                                        toast("验证码已发送");
                                    }
                                }));
                        required[0] = true;
                    }
                    seconds[0]--;
                    if (seconds[0] > 0) {
                        tv_code.setText(seconds[0] +"s后重新获取");
                    } else {
                        timer.cancel();
                        tv_code.setText("获取验证码");
                        tv_code.setEnabled(true);
                    }
                });
            }
        };
        timer.schedule(task,1000,1000);
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
    }

    @Override
    protected String getTalkingDataPageName() {
        return "手机号修改或绑定";
    }
}
