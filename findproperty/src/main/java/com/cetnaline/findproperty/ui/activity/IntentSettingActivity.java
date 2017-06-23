package com.cetnaline.findproperty.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.base.BaseFragment;
import com.cetnaline.findproperty.base.BaseFragmentActivity;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.cetnaline.findproperty.ui.fragment.HouseChoiceFragment;
import com.cetnaline.findproperty.ui.fragment.MapFragment;
import com.cetnaline.findproperty.utils.CircularRevealAnim;
import com.cetnaline.findproperty.utils.DataHolder;
import com.cetnaline.findproperty.utils.StatusBarCompat;

import butterknife.BindView;

/**
 * 意向选择类
 * Created by fanxl2 on 2016/11/3.
 */
public class IntentSettingActivity extends BaseFragmentActivity {

    private View rootView;

    @BindView(R.id.intent_toolbar)
    Toolbar intent_toolbar;

    private int tvX, tvY;

    private int currentHouseType;

    private CircularRevealAnim mCircularRevealAnim;

    @Override
    protected int getContentViewId() {
        return R.layout.act_frag_intent;
    }

    @Override
    protected IPresenter createPresenter() {
        return null;
    }

    @Override
    protected void initToolbar() {
        StatusBarCompat.translucentStatusBar(this, true, false);
        showToolbar(false);
    }

    @Override
    protected BaseFragment getFirstFragment(int type) {
        return HouseChoiceFragment.getInstance(currentHouseType);
    }

    @Override
    protected int getFragmentContentId() {
        return R.id.fragment_container;
    }

    private boolean justClose;

    @Override
    protected void initView(Bundle savedInstanceState) {
        tvX = getIntent().getIntExtra(CircularRevealAnim.CENTER_X_KEY, 0);
        tvY = getIntent().getIntExtra(CircularRevealAnim.CENTER_Y_KEY, 0);
        justClose = getIntent().getBooleanExtra("justClose", false);
        currentHouseType = getIntent().getIntExtra(MapFragment.HOUSE_TYPE_KEY, MapFragment.HOUSE_TYPE_SECOND);

        intent_toolbar.setTitle("");

        setSupportActionBar(intent_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        intent_toolbar.setNavigationOnClickListener((v) -> {
            toBack();  //关闭页面
        });

        rootView = findViewById(R.id.intent_root_view);
        mCircularRevealAnim = new CircularRevealAnim();
        mCircularRevealAnim.setAnimListener(new CircularRevealAnim.AnimListener() {
            @Override
            public void onHideAnimationEnd() {
                IntentSettingActivity.this.finish();
                if (!justClose){
                    DataHolder.getInstance().setChangeIntent(true);
                    Intent intent = new Intent(IntentSettingActivity.this, MainTabActivity.class);
                    intent.putExtra(MainTabActivity.SELECTED_TAB_KEY, MainTabActivity.TAB_HOME);
                    startActivity(intent);
                }
            }

            @Override
            public void onShowAnimationEnd() {
                rootView.setVisibility(View.VISIBLE);
            }
        });

        if (savedInstanceState == null) {
            ViewTreeObserver viewTreeObserver = rootView.getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            mCircularRevealAnim.show(tvX, tvY, rootView);
                        }
                    }
                });
            }
        }
    }

    public void close(){
        mCircularRevealAnim.hide(tvX, tvY, rootView);
    }

    @Override
    protected void beforeFinish() {
        startActivity(new Intent(this, MainTabActivity.class));
    }

    @Override
    protected String getTalkingDataPageName() {
        return "意向引导页";
    }
}
