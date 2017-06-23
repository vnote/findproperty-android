package com.cetnaline.findproperty.ui.activity;

import android.graphics.Color;
import android.os.Bundle;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.base.BaseFragment;
import com.cetnaline.findproperty.base.BaseFragmentActivity;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.cetnaline.findproperty.ui.fragment.IntentHouseTypeFragment;
import com.cetnaline.findproperty.utils.StatusBarCompat;

/**
 * 意向选择类
 * Created by diaoqf on 2016/8/27.
 */
public class IntentSettingActivity2 extends BaseFragmentActivity {
    private int requestCode;

    public int getRequestCode() {
        return requestCode;
    }

    @Override
    protected BaseFragment getFirstFragment(int type) {
        return IntentHouseTypeFragment.getInstance();
    }

    @Override
    protected int getFragmentContentId() {
        return  R.id.fragment_container;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
    }

    @Override
    protected int getContentViewId() {
        return R.layout.act_frag;
    }

    @Override
    protected IPresenter createPresenter() {
        return null;
    }

    @Override
    protected void initToolbar() {
        StatusBarCompat.setStatusBarColor(this, Color.WHITE);
        toolbar.setTitle("找房意向");
        toolbar.setNavigationOnClickListener(view -> {
            toBack();
        });
    }
}
