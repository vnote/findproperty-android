package com.cetnaline.findproperty.ui.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.base.BaseActivity;
import com.cetnaline.findproperty.presenter.impl.SettingPresenter;
import com.cetnaline.findproperty.presenter.ui.SettingContract;
import com.cetnaline.findproperty.utils.DataHolder;
import com.cetnaline.findproperty.utils.StatusBarCompat;
import com.cetnaline.findproperty.widgets.SettingMenuItem;


import java.io.File;

import butterknife.BindView;
/**
 * Created by diaoqf on 2016/8/11.
 */
public class SettingActivity extends BaseActivity<SettingPresenter> implements SettingContract.View {
    @BindView(R.id.logout)
    TextView logout;
    @BindView(R.id.language_setting)
    SettingMenuItem language_setting;
    @BindView(R.id.push_setting)
    SettingMenuItem push_setting;
    @BindView(R.id.check_update)
    SettingMenuItem check_update;
    @BindView(R.id.about_us)
    SettingMenuItem about_us;
    @BindView(R.id.reword)
    SettingMenuItem reword;
    @BindView(R.id.suggest)
    SettingMenuItem suggest;
    @BindView(R.id.clear_cache)
    SettingMenuItem clear_cache;
    @BindView(R.id.version_tx)
    TextView version_tx;

    File cache_file;

    @Override
    protected int getContentViewId() {
        return R.layout.act_setting;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        logout.setOnClickListener(v->mPresenter.logout(this));
        language_setting.setHintText(getResources().getString(R.string.setting_current_language));
        language_setting.setOnItemClick(v->mPresenter.exchangeLanguage(this));
        about_us.setOnItemClick(v->mPresenter.OpenActivity(this,AboutUsActivity.class));

        mPresenter.getCacheFile(getApplicationContext().getExternalCacheDir());
        mPresenter.getCacheFileSize(cache_file);
        clear_cache.setOnItemClick(v->mPresenter.openAlert(this,SettingActivity.this.getLayoutInflater(),cache_file));
//        reword.setOnItemClick(v->mPresenter.OpenActivity(this,CommentActivity.class));
        suggest.setOnItemClick(v->mPresenter.OpenActivity(this,CommentActivity.class));
        check_update.setOnItemClick(v->mPresenter.getAppVerstion(this, this.getLayoutInflater()));
        push_setting.setHintText(mPresenter.getPushState());
        push_setting.setOnItemClick(v->mPresenter.openPushDialog(this,this.getLayoutInflater()));

        PackageManager pm = getPackageManager();
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo(getPackageName(), 0);
            version_tx.setText("版本号:"+pi.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }



    @Override
    protected SettingPresenter createPresenter() {
        return new SettingPresenter();
    }

    @Override
    protected void initToolbar() {
        StatusBarCompat.setStatusBarColor(this, Color.WHITE);
        toolbar.setNavigationOnClickListener((v)->{
            Intent intent = new Intent(this, MainTabActivity.class);
            intent.putExtra(MainTabActivity.SELECTED_TAB_KEY, MainTabActivity.TAB_MINE);
            startActivity(intent);
            finish();
        });
        toolbar.setTitle("系统设置");
    }

    @Override
    public void setCacheFile(File file) {
        cache_file = file;
    }

    @Override
    public void setCacheFileSize(double size) {
        clear_cache.setHintText(size + "M");
    }

    @Override
    public void setBackAction() {
        onBackPressed();
    }

    @Override
    public void setFinished() {
        DataHolder.getInstance().setChangeIntent(true);
//        SettingActivity.this.setResult(MainTabActivity.TAG_LOGOUT);
//        this.finish();
        Intent intent = new Intent(this, MainTabActivity.class);
        intent.putExtra(MainTabActivity.SELECTED_TAB_KEY, MainTabActivity.TAB_MAP);
        startActivity(intent);
        finish();
        toast("账号已登出");
    }

    @Override
    public void setPushSettingText() {
        push_setting.setHintText(mPresenter.getPushState());
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void dismissLoading() {

    }

    @Override
    public void showError(String msg) {
        toast(msg);
    }

    @Override
    protected String getTalkingDataPageName() {
        return "应用设置";
    }
}
