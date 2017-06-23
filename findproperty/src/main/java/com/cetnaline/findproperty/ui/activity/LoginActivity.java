package com.cetnaline.findproperty.ui.activity;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cetnaline.findproperty.AppContents;
import com.cetnaline.findproperty.BaseApplication;
import com.cetnaline.findproperty.BuildConfig;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.request.RongRequest;
import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.base.BaseActivity;
import com.cetnaline.findproperty.entity.bean.QQUserBean;
import com.cetnaline.findproperty.entity.bean.QQUserInfoEntity;
import com.cetnaline.findproperty.entity.bean.SinaUserInfoBean;
import com.cetnaline.findproperty.entity.bean.UserInfoBean;
import com.cetnaline.findproperty.entity.event.AppLoginEvent;
import com.cetnaline.findproperty.entity.event.HostChangeEvent;
import com.cetnaline.findproperty.entity.event.NormalEvent;
import com.cetnaline.findproperty.entity.event.WxLoginEvent;
import com.cetnaline.findproperty.entity.result.BaseLoginResult;
import com.cetnaline.findproperty.entity.result.BaseSingleResult;
import com.cetnaline.findproperty.entity.result.RcTokenResult;
import com.cetnaline.findproperty.api.NetContents;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.cetnaline.findproperty.utils.CircularRevealAnim;
import com.cetnaline.findproperty.utils.DataHolder;
import com.cetnaline.findproperty.utils.DbUtil;
import com.cetnaline.findproperty.utils.JpushUtil;
import com.cetnaline.findproperty.utils.MyUtils;
import com.cetnaline.findproperty.utils.RxBus;
import com.cetnaline.findproperty.utils.SchedulersCompat;
import com.cetnaline.findproperty.utils.SharedPreferencesUtil;
import com.cetnaline.findproperty.utils.StatusBarCompat;
import com.google.gson.Gson;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.orhanobut.logger.Logger;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.connect.common.Constants;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.trello.rxlifecycle.ActivityEvent;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import butterknife.BindView;
import io.rong.eventbus.EventBus;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by diaoqf on 2016/8/9.
 */
public class LoginActivity extends BaseActivity {
    private static final int REQUEST_PERMISSION_CODE = 110;
    public static final String OPEN_TYPE = "open_type";

    public static final int HOME_CHAT = 3;
    public static final int HOME_MINE = 4;
    public static final int ANY_NORMAL = 2;
    public static final int NO_EXCHANGE = -1;
    public static final int LOGIN_SUCCESS_CODE = 106;
    public static final int LOGIN_INTENT_COLLECT = 107;
    public static final int LOGIN_INTENT_LOOK = 108;
    public static final int LOGIN_INTENT_LOOK_LIST = 101;
    public static final int LOGIN_INTENT_TALK = 109;
    public static final int LOGIN_INTENT_NORMAL = 106;
    public static final int LOGIN_INTENT_SUB = 110;
    public static final int LOGIN_INTENT_JOIN = 112;
    public static final int LOGIN_INTENT_INTENT = 113;
    public static final int LOGIN_INTENT_ENTRUST = 114;
    public static final int LOGIN_INTENT_SHORTCUT = 115;    //主页快捷按钮进入

    public static final String LOGIN_INTENT_KEY = "LOGIN_INTENT_KEY";
    public static final String KICKED_RE_LOGIN = "KICKED_RE_LOGIN"; //标识用户被踢下线，重新登录

    private int loginIntent;

    private int call_type = ANY_NORMAL;
    private AppLoginEvent appLoginEvent;

    @BindView(R.id.user_phone_number)
    EditText user_phone_number;
    @BindView(R.id.user_code)  //短信验证码输入框
    EditText user_code;
    @BindView(R.id.tv_code)    //短信验证码提示框
    TextView tv_code;
    @BindView(R.id.btn_login)  //登录按钮
    TextView btn_login;

    @BindView(R.id.login_for_qq)  //QQ登录
    ImageView login_for_qq;
    @BindView(R.id.login_for_wx)  //微信登录
    ImageView login_for_wx;
    @BindView(R.id.login_for_wb)  //微博登录
    ImageView login_for_sina;

    @BindView(R.id.radioGroup)  //切换正式和测试环境
    RadioGroup radioGroup;
    @BindView(R.id.normal)  //正式环境
    RadioButton mRgNormal;
    @BindView(R.id.test)    //测试环境
    RadioButton mRgTest;

    @BindView(R.id.user_invite_code)    //邀请码输入框
    EditText user_invite_code;
    @BindView(R.id.bottom_layout)   //第三方登录父布局
    LinearLayout bottom_layout;
    @BindView(R.id.exchange_invite_login)  //邀请码登录
    TextView exchange_invite_login;
    @BindView(R.id.exchange_normal_login)  //普通登录
    TextView exchange_normal_login;
    @BindView(R.id.login_ly)
    LinearLayout login_ly;

    private boolean isInviteCodeLogin = false;  //是否为邀请码登录

//    private String path = ExternalCacheDirUtil.getImageDownloadCacheDir();
//    private String fileName = "portrait_"+System.currentTimeMillis()+".jpg";

    private String thirdLoginPicUrl = "";

    private boolean isSmsWaiting = false;  //短信验证码是否在倒计时

    private long timeStamp = 0;
    private int tvX, tvY;

    private CompositeSubscription mCompositeSubscription;

    //微博
    private AuthInfo authInfo;
    private SsoHandler ssoHandler;
    private MyWeiboAuthListener weiboAuthListener;

    /*微信*/
    private IWXAPI iwxapi;

    //QQ
    protected Tencent tencent;
    private MyIUiListener iUiListener;

    @Override
    protected int getContentViewId() {
        return R.layout.act_login;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mCompositeSubscription = new CompositeSubscription();

        if (getIntent().getBooleanExtra(KICKED_RE_LOGIN, false)) {
            //将当前用户下线
            DataHolder.getInstance().clear();
            JpushUtil.logout(this);
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mNotificationManager.cancel(AppContents.RONG_MESSAGE_NOTIFICATION_ID); //清除直聊通知
            mNotificationManager.cancel(AppContents.JPUSH_MESSAGE_NOTIFICATION_ID); //清除极光通知
            EventBus.getDefault().post(new AppLoginEvent(false,2));

            MyUtils.showDiloag(this, R.layout.dialog_alert_single, 280, -1, false, ((layout, dialog) -> {
                TextView submit = (TextView) layout.findViewById(R.id.submit);
                TextView title = (TextView) layout.findViewById(R.id.title);
                title.setText("你的账号已在另一台设备登录。如需继续使用，请重新登录。");
                submit.setOnClickListener(v-> dialog.dismiss());
            }));

        }

        if (BuildConfig.DEBUG) {
            radioGroup.setVisibility(View.VISIBLE);
            if (NetContents.IS_TEST_ENV ) {
                mRgTest.setChecked(true);
            } else {
                mRgNormal.setChecked(true);
            }
            mRgNormal.setOnClickListener(v->{
                if (NetContents.IS_TEST_ENV) {
                    NetContents.IS_TEST_ENV = false;
                    RxBus.getDefault().send(new HostChangeEvent());
                }
            });
            mRgTest.setOnClickListener(v->{
                if (!NetContents.IS_TEST_ENV) {
                    NetContents.IS_TEST_ENV = true;
                    RxBus.getDefault().send(new HostChangeEvent());
                }
            });
        } else {
            radioGroup.setVisibility(View.GONE);
        }

        EventBus.getDefault().register(this);
        iUiListener = new MyIUiListener(this);
        weiboAuthListener = new MyWeiboAuthListener(this);
        //设置状态栏颜色
        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.grayBigLine));

        Intent intent = getIntent();
        loginIntent = intent.getIntExtra(LOGIN_INTENT_KEY, LOGIN_INTENT_NORMAL);

        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            call_type = bundle.getInt(OPEN_TYPE, ANY_NORMAL);
        }
        appLoginEvent = new AppLoginEvent(true,call_type);
        //
        user_phone_number.setText(SharedPreferencesUtil.getString(AppContents.USER_INFO_PHONE));

        //获取验证码
        RxView.clicks(tv_code)
                .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(aVoid -> {
                    //申请读取短信权限
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, REQUEST_PERMISSION_CODE);
                    }
                    String number = user_phone_number.getText().toString();
                    isSmsWaiting = true;
                    user_code.requestFocus();
                    timer(number);
                });

        //输入手机号码
        String patten = "^1[3-9]\\d{9}$";//"^1(3[0-9]|4[57]|5[0-35-9]|8[0-9]|7[0-9])\\d{8}$";
        Pattern r = Pattern.compile(patten);
        RxTextView.textChanges(user_phone_number)
                .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(charSequence->{
                    if (r.matcher(charSequence).matches()) {
                        if (!isSmsWaiting) {
                            tv_code.setEnabled(true);
                        }
                    } else {
                        tv_code.setEnabled(false);
                    }
                    checkForm();
                });

        //输入短信验证码
        RxTextView.textChanges(user_code)
                .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(charSequence->{
                    checkForm();
                });

        //输入邀请码
        RxTextView.textChanges(user_invite_code)
                .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(charSequence -> {
                    checkForm();
                });

        //登录
        RxView.clicks(btn_login).throttleFirst(500, TimeUnit.MILLISECONDS)
                .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(aVoid -> {
                    String number = user_phone_number.getText().toString();
                    String code = user_code.getText().toString();
                    String inviteCode = user_invite_code.getText().toString();
                    if (!r.matcher(number).matches()) {
                        toast("请输入正确的手机号");
                        return;
                    }
                    if (code == null || "".equals(code) || code.length() < 4) {
                        toast("请输入正确的验证码");
                    }

                    if (isInviteCodeLogin && (inviteCode == null || "".equals(inviteCode))) {
                        toast("请输入邀请码");
                    }

                    int[] tvLocation = new int[2];
                    btn_login.getLocationInWindow(tvLocation);
                    tvX = tvLocation[0] + btn_login.getWidth() / 2;
                    tvY = tvLocation[1] + btn_login.getHeight() / 2;

                    //保存验证码
                    SharedPreferencesUtil.saveCode(code);
                    //登录
                    if (isInviteCodeLogin) {
                        login(number, code, inviteCode);
                    } else {
                        login(number, code);
                    }
                });

        //三方登录-------------------------------
        RxView.clicks(login_for_qq).throttleFirst(500, TimeUnit.MILLISECONDS)
                .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(aVoid -> {
                    showLoadingDialog();
                    int[] tvLocation = new int[2];
                    login_for_qq.getLocationInWindow(tvLocation);
                    tvX = tvLocation[0] + login_for_qq.getWidth() / 2;
                    tvY = tvLocation[1] + login_for_qq.getHeight() / 2;
                    QQLogin();
                });

        RxView.clicks(login_for_wx).throttleFirst(500, TimeUnit.MILLISECONDS)
                .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(aVoid -> {
                    showLoadingDialog();
                    int[] tvLocation = new int[2];
                    login_for_wx.getLocationInWindow(tvLocation);
                    tvX = tvLocation[0] + login_for_wx.getWidth() / 2;
                    tvY = tvLocation[1] + login_for_wx.getHeight() / 2;
                    WXLogin();
                });

        RxView.clicks(login_for_sina).throttleFirst(500, TimeUnit.MILLISECONDS)
                .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(aVoid -> {
                    showLoadingDialog();
                    int[] tvLocation = new int[2];
                    login_for_sina.getLocationInWindow(tvLocation);
                    tvX = tvLocation[0] + login_for_sina.getWidth() / 2;
                    tvY = tvLocation[1] + login_for_sina.getHeight() / 2;
                    WBLogin();
                });

        //切换到邀请码登录
        RxView.clicks(exchange_invite_login).throttleFirst(500, TimeUnit.MILLISECONDS)
                .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(aVoid -> {
                    exchangeInviteLogin(true);
                    checkForm();
                });
        //切换至普通登录
        RxView.clicks(exchange_normal_login).throttleFirst(500, TimeUnit.MILLISECONDS)
                .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(aVoid -> {
                    exchangeInviteLogin(false);
                    checkForm();
                });

        RxBus.getDefault().toObservable(NormalEvent.class)
                .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                .filter(normalEvent -> normalEvent.type == NormalEvent.SMS_CODE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(normalEvent -> {
                    user_code.setText(normalEvent.data);
                });
    }

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
//                            user_code.setText(normalEvent.data);
//                        }));
//    }

    /**
     * 检测当前表单是否填写完整
     * @return
     */
    private boolean checkForm() {
        String patten = "^1[3-9]\\d{9}$";//"^1(3[0-9]|4[57]|5[0-35-9]|8[0-9]|7[0-9])\\d{8}$";
        Pattern r = Pattern.compile(patten);

        String phone = user_phone_number.getText().toString();
        String code = user_code.getText().toString();
        String invidecode = user_invite_code.getText().toString();
        if (r.matcher(phone).matches() &&
                (code != null && !"".equals(code) && code.length() >= 4) &&
                (((isInviteCodeLogin && (invidecode != null && !"".equals(invidecode))) || !isInviteCodeLogin))) {
            btn_login.setEnabled(true);
            return true;
        } else {
            btn_login.setEnabled(false);
            return false;
        }
    }

    /**
     * 切换至邀请码登录
     * @param isInviteLogin
     */
    private void exchangeInviteLogin(boolean isInviteLogin) {
        isInviteCodeLogin = isInviteLogin;
        if (isInviteLogin) {
            user_invite_code.setVisibility(View.VISIBLE);
            bottom_layout.setVisibility(View.GONE);
            exchange_normal_login.setVisibility(View.VISIBLE);
            exchange_invite_login.setVisibility(View.GONE);

            user_invite_code.animate().setDuration(500).alpha(1);
            bottom_layout.animate().setDuration(500).alpha(0);
            login_ly.animate().setDuration(500).translationY(MyUtils.dip2px(this,8));
        } else {
            user_invite_code.setVisibility(View.GONE);
            bottom_layout.setVisibility(View.VISIBLE);
            exchange_normal_login.setVisibility(View.GONE);
            exchange_invite_login.setVisibility(View.VISIBLE);

            user_invite_code.animate().setDuration(500).alpha(0);
            bottom_layout.animate().setDuration(500).alpha(1);
            login_ly.animate().setDuration(500).translationY(MyUtils.dip2px(this,-8));
        }
    }

    /**
     * 登录到服务器
     * @param number
     * @param code
     */
    private void login(String number, String code){
        coreLogin(new HashMap(){
            {
                put("Phone",number);
                put("VerificationCode",code);
                String yaoqingma = SharedPreferencesUtil.getString(AppContents.YAO_QING_MA);
                if (yaoqingma != null && !"".equals(yaoqingma.trim()) && !"null".equals(yaoqingma)) {
                    put("YaoQingMa",yaoqingma);
                }
            }
        });
    }

    /**
     * 手动填写邀请码登录
     * @param number
     * @param code
     * @param inviteCode
     */
    private void login(String number, String code, String inviteCode){
        coreLogin(new HashMap(){
            {
                put("Phone",number);
                put("VerificationCode",code);
                if (inviteCode != null && !"".equals(inviteCode.trim())) {
                    put("YaoQingMa",inviteCode);
                }
                put("deviceNumber",MyUtils.getDeviceId(LoginActivity.this));
            }
        });
    }

    /**
     * 调用核心登录接口
     * @param params
     */
    private void coreLogin(Map<String,String> params) {
        showLoadingDialog();
        ApiRequest.login(params)
                .compose(SchedulersCompat.applyIoSchedulers())
                .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Action1<BaseSingleResult<UserInfoBean>>() {
                    @Override
                    public void call(BaseSingleResult<UserInfoBean> userInfoBeanBaseSingleResult) {
                        cancelLoadingDialog();
                        switch (userInfoBeanBaseSingleResult.ResultNo) {
                            case 0:
                                UserInfoBean bean = userInfoBeanBaseSingleResult.Result;
                                if (bean != null) {
                                    //显示三方昵称
                                    if (bean.NickName == null || "".equals(bean.NickName)) {
                                        bean.NickName = params.get("NickName");
                                    }

                                    //第三方登录显示第三方头像
                                    if (bean.Phone == null || "".equals(bean.Phone)) {
                                        bean.UserPhotoUrl = thirdLoginPicUrl;
                                    }
//                                loadUserImage(bean);
                                    DataHolder.getInstance().cache(bean, null);
                                    initRC(bean.UserId.toLowerCase());
                                }
                                break;
                            case -1:
                                if (userInfoBeanBaseSingleResult.Message != null) {
                                    toast(userInfoBeanBaseSingleResult.Message);
                                }
//                            case -1:
//                                toast("验证码错误或已过期");
//                                break;
//                            case -2:
//                                toast("邀请码错误");
//                                break;
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        toast("网络连接失败，请检查网络！");
                        cancelLoadingDialog();
                    }
                });
    }

    /**
     * 加载用户头像并缓存
     * @param bean
     */
//    private void loadUserImage(UserInfoBean bean) {
//        mCompositeSubscription.add(ApiRequest.getUserImg(bean.UserId)
//                .subscribe(result->{
//                    if (result.Result != null && result.Result.Img != null) {
//                        byte[] data = Base64.decode(result.Result.Img,0);
//                        FileOutputStream fos = null;
//                        BufferedOutputStream bos = null;
//                        MyUtils.deleteFiles(path);
//                        try {
//                            File file = new File(path ,fileName);
//                            if (!file.exists()) {file.createNewFile();}
//
//                            fos = new FileOutputStream(file);
//                            bos = new BufferedOutputStream(fos);
//                            bos.write(data);
//                            bean.UserPhotoUrl = path + File.separator + fileName;
//                        } catch (Exception e) {e.printStackTrace();}
//                        finally {
//                            if (bos != null)
//                            {try {bos.close();}
//                            catch (IOException e) {
//                                e.printStackTrace();}}
//                            if (fos != null)
//                            {try {fos.close();}
//                            catch (IOException e)
//                            {e.printStackTrace();}}
//                        }
//                    }
//                    DataHolder.getInstance().cache(bean, null);
//                    RongIM.getInstance().setCurrentUserInfo(new UserInfo("u_" + bean.UserId.toLowerCase(),bean.NickName, Uri.parse(bean.UserPhotoUrl)));
//                    initRC(bean);
//                },throwable -> {
//                    throwable.printStackTrace();
//                    DataHolder.getInstance().cache(bean, null);
//                    RongIM.getInstance().setCurrentUserInfo(new UserInfo("u_" + bean.UserId.toLowerCase(),bean.NickName, Uri.parse(bean.UserPhotoUrl)));
//                    initRC(bean);
//                }));
//    }

    /**
     * 获取token
     */
    private void initRC(String id){
        //连接融云服务器
        String userId = DataHolder.getInstance().getRcUserId();
        String nickName = DataHolder.getInstance().getRcUsername();
        String imgUrl = DataHolder.getInstance().getRcPortrait();

//            mCompositeSubscription.add(Observable.just("u_" + id)
//                    .map(s -> DbUtil.getRcTokenById(s)).subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(s -> {
//                        if (s == null) {
//                            //获取token
//
//                        } else {
//                            DataHolder.getInstance().setRcToken(s);
//                            Logger.i("get token from DB");
//                            finishLogin();
//                            EventBus.getDefault().post(appLoginEvent);
//                        }
//                    }));

        mCompositeSubscription.add(RongRequest.getRongCloudApi().getToken(userId,nickName,imgUrl)   //78h0970712037128
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<RcTokenResult>() {
                    @Override
                    public void onCompleted() {}
                    @Override
                    public void onError(Throwable e) {
                        Logger.e(e.getMessage());
                        Toast.makeText(LoginActivity.this,"登录失败，请稍后再试",Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onNext(RcTokenResult rcTokenResult) {
                        DataHolder.getInstance().setRcToken(rcTokenResult.token);
                        DbUtil.saveRcToken(rcTokenResult.token);
                        Logger.i("request token from net");
                        finishLogin();
                        if (appLoginEvent.callType == LOGIN_INTENT_SHORTCUT) {
                            openDeputeForm(true);
                        } else {
                            EventBus.getDefault().post(appLoginEvent);
                        }
                    }
                }));
    }

    /**
     * 登录成功，跳转
     */
    private void finishLogin(){
        this.setResult(loginIntent);
        int choiceIntent = DataHolder.getInstance().getChoiceIntent();

        JpushUtil.initJpush(this);
        if (choiceIntent ==LOGIN_INTENT_INTENT){  //意向选择页
            Intent intent = new Intent(this, IntentSettingActivity.class);
            intent.putExtra(CircularRevealAnim.CENTER_X_KEY, tvX);
            intent.putExtra(CircularRevealAnim.CENTER_Y_KEY, tvY);
            startActivity(intent);
            toast("登录成功");
            finish();
        }else if (choiceIntent==LOGIN_INTENT_ENTRUST){ //委托页
//            startActivity(new Intent(this, EntrustActivity.class));
            openDeputeForm(false);
        } else {
            finish();
        }
    }

    /**
     * 是否打开委托表单页面
     */
    private void openDeputeForm(boolean fromShortcut) {
        mCompositeSubscription.add(
                ApiRequest.entrustCountRequest(
                        DataHolder.getInstance().getUserId())
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(integer -> {
                            if (integer < AppContents.DEPUTE_COUNT) {
                                if (fromShortcut) {
                                    EventBus.getDefault().post(appLoginEvent);
                                } else {
                                    Intent intent = new Intent(LoginActivity.this, DeputeActivity.class);
                                    startActivity(intent);
                                }
                            } else {
                                toast("您的委托数量已达上限");
                                if (!fromShortcut) {
                                    Intent intent = new Intent(LoginActivity.this, MainTabActivity.class);
                                    startActivity(intent);
                                }
                            }
                            finish();
                        }, throwable -> {
                            throwable.printStackTrace();
                            toast("连接服务器失败，请稍后再试");
                            if (!fromShortcut) {
                                Intent intent = new Intent(LoginActivity.this, MainTabActivity.class);
                                startActivity(intent);
                            }
                            finish();
                        }));
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
                        isSmsWaiting = false;
                    }
                });
            }
        };
        timer.schedule(task,1000,1000);
    }

    /**
     * qq登录
     */
    private void QQLogin(){
//        showLoadingDialog();
        if (tencent == null) {
            tencent = Tencent.createInstance(BuildConfig.QQ_ID, BaseApplication.getContext());
        }

        if (!tencent.isSessionValid())
        {
            tencent.login(this, "all", iUiListener);
        }
    }

    /**
     * 微信登录
     */
    private void WXLogin(){
//        showLoadingDialog();
        if (iwxapi == null) {
            iwxapi = WXAPIFactory.createWXAPI(BaseApplication.getContext(), BuildConfig.APP_ID_WX,true);
        }

        if (!iwxapi.isWXAppInstalled()) {
            toast("微信未安装");
            cancelLoadingDialog();
            return;
        }

        iwxapi.registerApp(BuildConfig.APP_ID_WX);
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = AppContents.WX_LOGIN_TAG;
        iwxapi.sendReq(req);
    }

    /**
     * 微博登录
     */
    private void WBLogin(){
//        showLoadingDialog();
        if (authInfo == null) {
            authInfo = new AuthInfo(BaseApplication.getContext(), BuildConfig.SINA_APP_KEY,
                    BuildConfig.SINA_CALLBACK_URL,
                    BuildConfig.SINA_SCOPE);
        }
        if (ssoHandler == null) {
            ssoHandler = new SsoHandler(this, authInfo);
        }
        ssoHandler.authorize(weiboAuthListener);
    }


    /**
     * 新浪用户信息请求
     */
    private void requestSinaUserInfo(Oauth2AccessToken accessToken) {
        mCompositeSubscription.add(ApiRequest.getSinaUserInfo(accessToken.getToken(),accessToken.getUid(),"")
                .subscribe(new Subscriber<SinaUserInfoBean>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        cancelLoadingDialog();
                    }

                    @Override
                    public void onNext(SinaUserInfoBean sinaUserInfoBean) {
                        if (sinaUserInfoBean.getError() == null) {
                            thirdLoginPicUrl = sinaUserInfoBean.getProfile_image_url() == null ? "":sinaUserInfoBean.getProfile_image_url();
                            coreLogin(new HashMap(){
                                {
                                    put("SinaAccount", sinaUserInfoBean.getIdstr());
                                    put("NickName", sinaUserInfoBean.getScreen_name());
//                                    put("ThirdLoginPicUrl", sinaUserInfoBean.getProfile_image_url());
                                    String yaoqingma = SharedPreferencesUtil.getString(AppContents.YAO_QING_MA);
                                    if (yaoqingma != null && !"".equals(yaoqingma.trim()) && !"null".equals(yaoqingma)) {
                                        put("YaoQingMa",yaoqingma);
                                    }
                                }
                            });
                        } else {
                            toast("获取应用授权失败");
                            cancelLoadingDialog();
                        }
                    }
                }));
    }

    @Override
    protected boolean loadingCancelable() {
        return true;
    }

    @Override
    protected IPresenter createPresenter() {
        return null;
    }

    @Override
    protected void initToolbar() {
        StatusBarCompat.setStatusBarColor(this, Color.WHITE);
        toolbar.setNavigationOnClickListener((v) -> {
            if (DataHolder.getInstance().getChoiceIntent()!=-1){
                startActivity(new Intent(LoginActivity.this, MainTabActivity.class));
            }
            onBackPressed();
        });
        toolbar.setBackgroundColor(getResources().getColor(R.color.grayBigLine));
        toolbar.setTitle("用户登录");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.REQUEST_LOGIN) {
//            if(resultCode == Tencent.REQUEST_LOGIN) {}
//            Tencent.onActivityResultData(requestCode, resultCode, data, iUiListener);
            Tencent.handleResultData(data, iUiListener);
        }
        if (requestCode == 32973){
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (tencent != null) {
            tencent.logout(this);
        }
        if (mCompositeSubscription != null && mCompositeSubscription.hasSubscriptions()) {
            mCompositeSubscription.unsubscribe();
        }
        super.onDestroy();
    }

    /**
     * 微信授权登录事件监听
     * @param event
     */
    public void onEventMainThread(WxLoginEvent event) {
        if (!event.isOk) {
            cancelLoadingDialog();
            toast("授权失败");
            return;
        } else {
            if (System.currentTimeMillis() - timeStamp > 3000) {
                timeStamp = System.currentTimeMillis();
                thirdLoginPicUrl = event.ThirdLoginPicUrl;
                coreLogin(new HashMap() {
                    {
                        put("WeiXinAccount", event.WeiXinAccount);
                        put("AppNo", event.AppNo);
//                        put("ThirdLoginPicUrl", event.ThirdLoginPicUrl);
                        put("NickName", event.NickName);
                        String yaoqingma = SharedPreferencesUtil.getString(AppContents.YAO_QING_MA);
                        if (yaoqingma != null && !"".equals(yaoqingma.trim()) && !"null".equals(yaoqingma)) {
                            put("YaoQingMa",yaoqingma);
                        }
                    }
                });
            }
        }
    }

    /**
     * QQ登录监听
     */
    public static class MyIUiListener implements IUiListener {

        private WeakReference<LoginActivity> act;

        public MyIUiListener(LoginActivity act) {
            this.act = new WeakReference<LoginActivity>(act);
        }

        @Override
        public void onComplete(Object o) {
            Gson gson = new Gson();
            QQUserInfoEntity jo = gson.fromJson(o.toString(),QQUserInfoEntity.class);
            int ret = jo.ret;
            if (ret == 0) {
                //登录成功返回的必要信息
                String openID = jo.openid;
                String accessToken = jo.access_token;
                String expires = jo.expires_in;
                String name = jo.nickname;
                String imgUrl = jo.figureurl_qq_2;

                ApiRequest.getQqUserInfo(new HashMap(){
                    {
                        put("access_token",jo.access_token);
                        put("oauth_consumer_key",BuildConfig.QQ_ID);
                        put("openid",jo.openid);
                        put("format","json");
                    }
                }).subscribe(new Subscriber<QQUserBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (act.get() != null) {
                            act.get().toast("获取授权失败");
                            act.get().cancelLoadingDialog();
                        }
                    }

                    @Override
                    public void onNext(QQUserBean qqUserBean) {
                        if (act.get() != null) {
                            act.get().thirdLoginPicUrl = qqUserBean.getFigureurl_qq_2() == null ? "" : qqUserBean.getFigureurl_qq_2();
                            act.get().coreLogin(new HashMap() {
                                {
                                    put("QQAccount", openID);
                                    put("AppNo", "APP");
//                                put("ThirdLoginPicUrl",qqUserBean.getFigureurl_qq_2()==null?"":qqUserBean.getFigureurl_qq_2());
                                    put("NickName", qqUserBean.getNickname() == null ? "" : qqUserBean.getNickname());
                                    String yaoqingma = SharedPreferencesUtil.getString(AppContents.YAO_QING_MA);
                                    if (yaoqingma != null && !"".equals(yaoqingma.trim()) && !"null".equals(yaoqingma)) {
                                        put("YaoQingMa",yaoqingma);
                                    }
                                }
                            });
                            act.get().tencent.setOpenId(openID);
                            act.get().tencent.setAccessToken(accessToken, expires);
                        }
                    }
                });
            }
        }

        @Override
        public void onError(UiError uiError) {
            if (act.get()!= null) {
                act.get().cancelLoadingDialog();
                act.get().toast(uiError.errorMessage);
            }
        }

        @Override
        public void onCancel() {
            if (act.get() != null) {
                act.get().cancelLoadingDialog();
                act.get().toast("授权已取消");
            }
        }
    }

    /**
     * 微博登录监听
     */
    public static class MyWeiboAuthListener implements WeiboAuthListener {

        private WeakReference<LoginActivity> act;

        public MyWeiboAuthListener(LoginActivity act) {
            this.act = new WeakReference<LoginActivity>(act);
        }

        @Override
        public void onComplete(Bundle bundle) {
            Oauth2AccessToken accessToken = Oauth2AccessToken.parseAccessToken(bundle);
            if (act.get() != null) {
                if (accessToken.isSessionValid()) {
                    act.get().requestSinaUserInfo(accessToken);
                } else {
                    act.get().cancelLoadingDialog();
                    act.get().toast("获取授权失败");
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            if (act.get() != null) {
                act.get().cancelLoadingDialog();
                act.get().toast("获取授权失败");
            }
        }

        @Override
        public void onCancel() {
            if (act.get() != null) {
                act.get().cancelLoadingDialog();
                act.get().toast("授权已取消");
            }
        }
    }

    @Override
    protected String getTalkingDataPageName() {
        return "用户登录";
    }
}