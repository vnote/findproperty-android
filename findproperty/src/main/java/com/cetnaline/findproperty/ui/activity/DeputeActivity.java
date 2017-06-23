package com.cetnaline.findproperty.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.base.BaseFragmentActivity;
import com.cetnaline.findproperty.entity.event.NormalEvent;
import com.cetnaline.findproperty.entity.ui.DeputeEntrustData;
import com.cetnaline.findproperty.entity.ui.DeputeEntrustOrder;
import com.cetnaline.findproperty.entity.ui.DeputeOrderParam;
import com.cetnaline.findproperty.entity.ui.DeputePushBean;
import com.cetnaline.findproperty.inter.FragmentBack;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.cetnaline.findproperty.ui.fragment.DeputePriceFragment;
import com.cetnaline.findproperty.ui.fragment.DeputeSourceInfoFragment;
import com.cetnaline.findproperty.utils.DataHolder;
import com.cetnaline.findproperty.utils.RxBus;
import com.cetnaline.findproperty.utils.StatusBarCompat;

import java.util.ArrayList;

/**
 * Created by diaoqf on 2017/3/27.
 */
public class DeputeActivity extends BaseFragmentActivity {
    public static final String REFRESH_LIST = "refresh_list";   //是否刷新列表
    public static final String LIST_FROM = "list_from"; //从列表页打开表单

    public static final int STEP_BACK = -1;
    public static final int STEP_NEXT = 1;

    private int currentStep = 1;  //当前填写委托步骤

    private boolean isEstateSelectFromCentline = true; //标识是否从中原服务器选择的小区
    private boolean selectLocationFromMap;  //是否从地图选择的位置

    private String gScopeId; //小区所在区域id
    private String gScopeName;//区域名称

    private DeputePushBean deputePushBean;
    private double advisePrice; //建议价格
    private double evaluatePrice = -1; //估价

    private boolean isSubmit = false; //当前表单是否已提交

    @Override
    protected Fragment getFirstFragment(int type) {
        setToolBarTitle("填写房源信息");
        return DeputeSourceInfoFragment.getInstance();
    }

    @Override
    protected int getFragmentContentId() {
        return R.id.fragment_container;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        deputePushBean = new DeputePushBean();
        deputePushBean.setEntrustAttachment(new ArrayList<>());
        deputePushBean.setEntrustData(new DeputeEntrustData());
        deputePushBean.setEntrustOrder(new DeputeEntrustOrder());
        deputePushBean.setEntrustStoreRelation(null);
        deputePushBean.setOrderParam(new DeputeOrderParam());

        deputePushBean.getEntrustData().setExpectedPrice(-1);
        deputePushBean.getEntrustOrder().setEntrustType(1);
    }

    @Override
    public void finish() {
        if (getIntent().getBooleanExtra(REFRESH_LIST, false) && isSubmit) {
            //发送刷新事件
            RxBus.getDefault().send(new NormalEvent(NormalEvent.REFRESH_ENTRUST_LIST));
        }

        Intent intent;
        //从引导页进入
        if (LoginActivity.LOGIN_INTENT_ENTRUST == DataHolder.getInstance().getChoiceIntent()) {
            if (isSubmit) {
                //已提交，返回列表页
                intent = new Intent(this, EntrustActivity.class);
                intent.putExtra(BaseFragmentActivity.FRAGMENT_TYPE, EntrustActivity.ENTRUST_TYPE_MY);
                startActivity(intent);
            } else if (!getIntent().getBooleanExtra(LIST_FROM, false)) {
                intent = new Intent(this, MainTabActivity.class);
                startActivity(intent);
            }
        } else if (!getIntent().getBooleanExtra(LIST_FROM, false) && isSubmit) {
            //如果不是从列表页进入则在提交很固定打开列表页
            intent = new Intent(this, EntrustActivity.class);
            intent.putExtra(BaseFragmentActivity.FRAGMENT_TYPE, EntrustActivity.ENTRUST_TYPE_MY);
            startActivity(intent);
        }
        super.finish();
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
        toolbar.setNavigationOnClickListener(view -> toBack());
    }

    @Override
    protected void toBack() {
        FragmentBack fragment = (FragmentBack) getSupportFragmentManager().findFragmentByTag("DeputePriceFragment");
        if (fragment != null) {
            fragment.saveData();
        }
        fragment = (FragmentBack) getSupportFragmentManager().findFragmentByTag("DeputeCustomerFragment");
        if (fragment != null) {
            fragment.saveData();
        }

        if (isSubmit) {
            finish();
        } else {
            updateStep(STEP_BACK);
            super.toBack();
        }
    }

    /**
     * 设置标题
     * @param title
     */
    public void setToolBarTitle(String title){
        toolbar.setTitle("");
        center_title.setVisibility(View.VISIBLE);
        if (!isSubmit) {
            center_title.setText(title + "(" + currentStep + "/3)");
        } else {
            center_title.setText(title);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * 保存房源信息
     */
    public void saveStep1(boolean isEstateSelectFromCentline, boolean selectLocationFromMap,String gScopeId, String gScopeName, DeputePushBean bean){
        this.isEstateSelectFromCentline = isEstateSelectFromCentline;
        deputePushBean.getEntrustData().setEstateCode(bean.getEntrustData().getEstateCode());
        deputePushBean.getEntrustData().setEstateId(bean.getEntrustData().getEstateId());
        deputePushBean.getEntrustData().setAddress(bean.getEntrustData().getAddress());
        deputePushBean.getEntrustData().setEstateName(bean.getEntrustData().getEstateName());
        this.selectLocationFromMap = selectLocationFromMap;
        deputePushBean.getEntrustData().setLat(bean.getEntrustData().getLat());
        deputePushBean.getEntrustData().setLng(bean.getEntrustData().getLng());
        this.gScopeId = gScopeId;
        this.gScopeName = gScopeName;
        deputePushBean.getEntrustData().setBuildingName(bean.getEntrustData().getBuildingName());
        deputePushBean.getEntrustData().setBuildingId(bean.getEntrustData().getBuildingId());
        deputePushBean.getEntrustData().setDoorNo(bean.getEntrustData().getDoorNo());
        deputePushBean.getEntrustData().setSquare(bean.getEntrustData().getSquare());
        deputePushBean.getEntrustData().setRoomCnt(bean.getEntrustData().getRoomCnt());
        deputePushBean.getEntrustData().setParlorCnt(bean.getEntrustData().getParlorCnt());
        deputePushBean.getEntrustData().setToiletCnt(bean.getEntrustData().getToiletCnt());
        //添加备注
        updateStep(STEP_NEXT);
    }

    /**
     * 保存价格和租售类别
     */
    public void saveStep2(double advisePrice,DeputePushBean bean) {
        deputePushBean.getEntrustData().setExpectedPrice(bean.getEntrustData().getExpectedPrice());
        deputePushBean.getEntrustOrder().setEntrustType(bean.getEntrustOrder().getEntrustType());
        deputePushBean.getEntrustOrder().setProcessStatus(1);
        deputePushBean.getEntrustOrder().setAppName("APP_ANDROID");
        deputePushBean.getEntrustOrder().setVersion(1);
        deputePushBean.getEntrustOrder().setUserId(DataHolder.getInstance().getUserId());
        this.advisePrice = advisePrice;
        updateStep(STEP_NEXT);
    }

    public void saveStep3() {
        isSubmit = true;
        updateStep(STEP_NEXT);
    }


    /**
     * 更新填写步骤
     * @param steps
     */
    public void updateStep(int steps) {
        if (steps == STEP_BACK || steps == STEP_NEXT) {
            currentStep += steps;
            switch (currentStep) {
                case 1:
                    setToolBarTitle("填写房源信息");
                    break;
                case 2:
                    setToolBarTitle("填写挂牌价格");
                    break;
                case 3:
                    setToolBarTitle("填写业主信息");
                    break;
                case 4:
                    setToolBarTitle("发布成功");
            }
        }
    }

    public DeputePushBean getDeputePushBean() {
        return deputePushBean;
    }

    /**
     * 获取区域id
     * @return
     */
    public String getgScopeId() {
        return gScopeId;
    }

    /**
     * 获取区域名称
     * @return
     */
    public String getgScopeName() {
        return gScopeName;
    }

    public boolean isEstateSelectFromCentline() {
        return isEstateSelectFromCentline;
    }

    public boolean isSelectLocationFromMap() {
        return selectLocationFromMap;
    }

    public double getEvaluatePrice() {
        return evaluatePrice;
    }

    public void setEvaluatePrice(double evaluatePrice) {
        this.evaluatePrice = evaluatePrice;
    }

    @Override
    protected String getTalkingDataPageName() {
        return "委托填写";
    }
}
