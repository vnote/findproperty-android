package com.cetnaline.findproperty.ui.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.base.BaseActivity;
import com.cetnaline.findproperty.entity.bean.QQUserInfoEntity;
import com.cetnaline.findproperty.entity.bean.SinaUserInfoBean;
import com.cetnaline.findproperty.entity.bean.UserInfoBean;
import com.cetnaline.findproperty.entity.event.NormalEvent;
import com.cetnaline.findproperty.entity.event.UserInfoEvent;
import com.cetnaline.findproperty.entity.result.BaseSingleResult;
import com.cetnaline.findproperty.presenter.impl.UserInfoSettingPresenter;
import com.cetnaline.findproperty.presenter.ui.UserInfoSettingContract;
import com.cetnaline.findproperty.utils.DataHolder;
import com.cetnaline.findproperty.utils.MyUtils;
import com.cetnaline.findproperty.utils.StatusBarCompat;
import com.cetnaline.findproperty.widgets.MineMenuItem;
import com.cetnaline.findproperty.widgets.SettingMenuItem;
import com.tencent.connect.common.Constants;

import java.util.HashMap;

import butterknife.BindView;
import io.rong.eventbus.EventBus;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by diaoqf on 2016/8/20.
 */
public class UserInfoSettingActivity extends BaseActivity<UserInfoSettingPresenter> implements UserInfoSettingContract.View {

    @BindView(R.id.image_setting)
    SettingMenuItem image_setting;

    @BindView(R.id.sex_setting)
    SettingMenuItem sex_setting;

    @BindView(R.id.name_setting)
    SettingMenuItem name_setting;

    @BindView(R.id.phone_setting)
    SettingMenuItem phone_setting;

    @BindView(R.id.wx_setting)
    MineMenuItem wx_setting;
    @BindView(R.id.qq_setting)
    MineMenuItem qq_setting;
    @BindView(R.id.wb_setting)
    MineMenuItem wb_setting;

    private UserInfoBean userInfoBean;

    public static final int SET_NICK_NAME = 1;
    public static final int SELECT_IMG = 2;
    public static final int SELECT_PHOTO = 3;
    public static final int SELECT_PHONE = 4;


    @Override
    protected int getContentViewId() {
        return R.layout.act_userinfo_setting;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        userInfoBean = DataHolder.getInstance().getUserInfo();
        image_setting.setImage(userInfoBean.UserPhotoUrl);
        name_setting.setHintText(userInfoBean.NickName);
        sex_setting.setHintText(userInfoBean.Gender);
        phone_setting.setHintText(userInfoBean.Phone == null ? "":userInfoBean.Phone);
        if (userInfoBean.WeiXinAccount != null  && !userInfoBean.WeiXinAccount.equals("") ) {
            wx_setting.enableMenu(true, "已绑定");
        } else {
            wx_setting.enableMenu(false, "绑定");
        }

        if (userInfoBean.QQAccount != null && !userInfoBean.QQAccount.equals("")) {
            qq_setting.enableMenu(true, "已绑定");
        } else {
            qq_setting.enableMenu(false, "绑定");
        }

        if (userInfoBean.SinaAccount != null && !userInfoBean.SinaAccount.equals("")) {
            wb_setting.enableMenu(true, "已绑定");
        } else {
            wb_setting.enableMenu(false, "绑定");
        }

        image_setting.setOnItemClick(v->mPresenter.showImageDialog());

        name_setting.setOnItemClick(v-> mPresenter.startActivityForResult(ExchangeNameActivity.class,SET_NICK_NAME));
        sex_setting.setOnItemClick(v->mPresenter.showSexDialog(getWindow().getDecorView()));
        phone_setting.setOnItemClick(v->mPresenter.startActivityForResult(ExchangePhoneActivity.class,SELECT_PHONE));

        wx_setting.setOnItemClick(v->mPresenter.bindWX(!wx_setting.isEnable()));
        qq_setting.setOnItemClick(v->mPresenter.bindQQ(!qq_setting.isEnable()));
        wb_setting.setOnItemClick(v->mPresenter.bindWB(!wb_setting.isEnable()));

        EventBus.getDefault().register(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_LOGIN){
            mPresenter.qqCallback(requestCode,resultCode,data);
        }

        if (requestCode == 32973){
            mPresenter.wbCallback(requestCode, resultCode, data);
        }

        if (RESULT_OK == resultCode) {
            mPresenter.normalCallback(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void initToolbar() {
        StatusBarCompat.setStatusBarColor(this, Color.WHITE);
        toolbar.setNavigationOnClickListener((v) -> {
            updateUserInfo();
            onBackPressed();
        });
        toolbar.setTitle("用户信息设置");
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateUserInfo();
    }

    @Override
    public void onBackPressed() {
        updateUserInfo();
        super.onBackPressed();
    }

    private void updateUserInfo(){
        DataHolder.getInstance().setUserInfo(userInfoBean);
        DataHolder.getInstance().setRcPortrait(userInfoBean.UserPhotoUrl);
        DataHolder.getInstance().setRcUsername(userInfoBean.NickName);
        RongIM.getInstance().refreshUserInfoCache(new UserInfo(DataHolder.getInstance().getRcUserId(), userInfoBean.NickName, Uri.parse(userInfoBean.UserPhotoUrl)));
        EventBus.getDefault().post(new UserInfoEvent());
    }

    /**
     * 微信绑定事件监听
     * @param event
     */
    public void onEventMainThread(NormalEvent event) {
        cancelLoadingDialog();
        if (event.type == NormalEvent.WX_BIND && event.result) {
            setWXAccount(event.data);
        }
    }

    @Override
    protected UserInfoSettingPresenter createPresenter() {
        return new UserInfoSettingPresenter(this,getResources().getStringArray(R.array.sex));
    }

    @Override
    public void setSex(String sex) {
        sex_setting.setHintText(sex);
        userInfoBean.Gender = sex;
    }

    @Override
    public void setQQAccount(QQUserInfoEntity entity) {
        mPresenter.sendAccountBind("QQAccount",entity.openid);
    }

    @Override
    public void setWBAccount(SinaUserInfoBean entity) {
        mPresenter.sendAccountBind("SinaAccount",entity.getIdstr());//entity.getIdstr()
    }

    @Override
    public void setWXAccount(String account) {
        mPresenter.sendAccountBind("WeiXinAccount",account);
    }

    @Override
    public void updateQQState(String account) {
        qq_setting.enableMenu(true,"已绑定");
        userInfoBean.QQAccount = account;
    }

    @Override
    public void updateWBState(String account) {
        wb_setting.enableMenu(true,"已绑定");
        userInfoBean.SinaAccount = account;
    }

    @Override
    public void updateWXState(String account) {
        wx_setting.enableMenu(true,"已绑定");
        userInfoBean.WeiXinAccount = account;
    }

    @Override
    public void setPhone(String phone) {
        phone_setting.setHintText(phone);
        userInfoBean.Phone = phone;
    }

    @Override
    public void setName(String name) {
        name_setting.setHintText(name);
        userInfoBean.NickName = name;
    }

    @Override
    public void setImage(String uri) {
        image_setting.setImage(uri);
        userInfoBean.UserPhotoUrl = uri;
    }

    @Override
    public void showMessage(String message) {
        toast(message);
        cancelLoadingDialog();
    }

    @Override
    public void showLoading(boolean show) {
        if (show) {
            showLoadingDialog();
        } else {
            cancelLoadingDialog();
        }
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void requestCameraPermissions(String[] permissions, int resultCode) {
        requestPermissions(permissions,resultCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == UserInfoSettingPresenter.REQUEST_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                String state = Environment.getExternalStorageState();
                if (state.equals(Environment.MEDIA_MOUNTED)) {
                    try {
                        Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
                        startActivityForResult(getImageByCamera, UserInfoSettingActivity.SELECT_PHOTO);
                    } catch (Exception e) {
                        MyUtils.showAlertDialog(this,"没有摄像权限,请在系统设置中打开");
                    }
                } else {
                    toast("没有储存卡");
                }
            } else {
                //权限拒绝
                toast("APP没有摄像头使用权限，无法拍照");
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected String getTalkingDataPageName() {
        return "用户信息设置";
    }
}
