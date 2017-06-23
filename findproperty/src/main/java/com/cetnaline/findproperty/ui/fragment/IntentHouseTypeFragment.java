package com.cetnaline.findproperty.ui.fragment;

import android.view.View;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.bean.SearchParam;
import com.cetnaline.findproperty.base.BaseFragment;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.cetnaline.findproperty.widgets.ImageSelectLayout;
import com.cetnaline.findproperty.widgets.StepLayout;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by diaoqf on 2016/8/27.
 */
public class IntentHouseTypeFragment extends BaseFragment {

    public static final String INTENT_DATA_KEY = "INTENT_DATA_KEY";

    public static IntentHouseTypeFragment getInstance(){
        IntentHouseTypeFragment fragment = new IntentHouseTypeFragment();
        return fragment;
    }

    @BindView(R.id.step_layout)
    StepLayout step_layout;

    @BindView(R.id.image_1)
    ImageSelectLayout image_1;

    @BindView(R.id.image_2)
    ImageSelectLayout image_2;

    @BindView(R.id.image_3)
    ImageSelectLayout image_3;

    private String selected = "S";

    private int houseType;

    @Override
    protected int getLayoutId() {
        return R.layout.frg_intent_1;
    }

    @Override
    protected void init() {
        houseType = MapFragment.HOUSE_TYPE_SECOND;
        step_layout.enableStep(1);
    }

    @OnClick(R.id.intent_bt_commit)
    public void toNext(){
        HashMap<String, SearchParam> data = new HashMap<>();
        addFragment(IntentHousePriceFragment.getInstance(data, houseType));
    }

    @OnClick({R.id.image_1,R.id.image_2,R.id.image_3})
    public void selectImage(View v) {
        switch (v.getId()) {
            case R.id.image_1:
                selectImage(1);
                break;
            case R.id.image_2:
                selectImage(2);
                break;
            case R.id.image_3:
                selectImage(3);
                break;
        }
    }

    private void selectImage(int position) {
        image_1.select(false);
        image_2.select(false);
        image_3.select(false);
        switch (position) {
            case 1:
                image_1.select(true);
                selected = "S";
                houseType = MapFragment.HOUSE_TYPE_SECOND;
                break;
            case 2:
                image_2.select(true);
                selected = "R";
                houseType = MapFragment.HOUSE_TYPE_RENT;
                break;
            case 3:
                image_3.select(true);
                selected = "N";
                houseType = MapFragment.HOUSE_TYPE_NEW;
                break;
        }
    }

    @Override
    protected IPresenter createPresenter() {
        return null;
    }
}
