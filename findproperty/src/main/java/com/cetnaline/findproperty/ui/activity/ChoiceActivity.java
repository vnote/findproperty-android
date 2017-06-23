package com.cetnaline.findproperty.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.TextView;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.base.BaseActivity;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.cetnaline.findproperty.utils.StatusBarCompat;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by fanxl2 on 2016/11/3.
 */

public class ChoiceActivity extends BaseActivity {

	@BindView(R.id.choice_tip_one)
	TextView choice_tip_one;

	@BindView(R.id.choice_bt_go)
	Button choice_bt_go;

	public static final String CHOICE_INTENT_KEY = "CHOICE_INTENT_KEY";

	private int intentType;

	@Override
	protected int getContentViewId() {
		return R.layout.act_choice;
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		intentType = getIntent().getIntExtra(CHOICE_INTENT_KEY, LoginActivity.LOGIN_INTENT_INTENT);
		if (intentType==LoginActivity.LOGIN_INTENT_INTENT){
			choice_tip_one.setText("建议您填写找房意向,\n第一时间获取最新房源通知服务。");
			choice_bt_go.setText("登录填写意向");
		}else {
			choice_tip_one.setText("上海中原服务上海十八年,\n委托中原顾问, 帮您更快成交。");
			choice_bt_go.setText("登录提交委托");
		}
	}

	@OnClick(R.id.choice_bt_go)
	public void goClick(){
		startActivity(new Intent(ChoiceActivity.this, LoginActivity.class));
	}

	@OnClick(R.id.tv_no_thanks)
	public void thanksClick(){
		startActivity(new Intent(ChoiceActivity.this, MainTabActivity.class));
		finish();
	}

	@Override
	protected IPresenter createPresenter() {
		return null;
	}

	@Override
	protected void initToolbar() {
		StatusBarCompat.setStatusBarColor(this, Color.WHITE);
		showToolbar(false);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_BACK == keyCode) {
			startActivity(new Intent(ChoiceActivity.this, MainTabActivity.class));
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected String getTalkingDataPageName() {
		return "意向填写";
	}
}
