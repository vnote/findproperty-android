package com.cetnaline.findproperty.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aigestudio.wheelpicker.WheelPicker;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.bean.BuildingNumBean;
import com.cetnaline.findproperty.base.BaseFragment;
import com.cetnaline.findproperty.entity.ui.DeputePushBean;
import com.cetnaline.findproperty.presenter.impl.DeputeSourceInfoPresenter;
import com.cetnaline.findproperty.presenter.ui.DeputeSourceInfoContract;
import com.cetnaline.findproperty.ui.activity.DeputeActivity;
import com.cetnaline.findproperty.ui.activity.EstateMapSelectActivity;
import com.cetnaline.findproperty.ui.activity.SearchActivity;
import com.cetnaline.findproperty.utils.SchedulersCompat;
import com.cetnaline.findproperty.widgets.FormItemLayout;
import com.cetnaline.findproperty.widgets.MyBottomDialog;
import com.cetnaline.findproperty.widgets.RemarkEditTextLayout;
import com.jakewharton.rxbinding.view.RxView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.Observable;
import rx.subscriptions.CompositeSubscription;

import static com.cetnaline.findproperty.ui.activity.SearchActivity.SEARCH_TYPE_ESTATE;

/**
 * Created by diaoqf on 2017/3/27.
 */

public class DeputeSourceInfoFragment extends BaseFragment<DeputeSourceInfoPresenter> implements DeputeSourceInfoContract.View {

    public static final int VILLAGE_REQUEST_CODE = 10;
    public static final int VILLAGE_RESULT_CODE = 20;

    public static final int LOCATION_REQUEST_CODE = 40;
    public static final int LOCATION_RESULT_CODE = 50;    //小区搜索页到地图选择页结果码
    public static final int LOCATION_RESULT_CODE_1 = 60;    //直接到到地图选择页结果码

    private static final int BUILDING_NO_SELECTOR = 0;
    private static final int ROOM_NO_SELECTOR = 1;
    private static final int ROOM_TYPE_SELECTOR = 2;

    @BindView(R.id.village_name)
    FormItemLayout village_name;
    @BindView(R.id.village_address)
    FormItemLayout village_address;
    @BindView(R.id.building_no)
    FormItemLayout building_no;
    @BindView(R.id.room_no)
    FormItemLayout room_no;
    @BindView(R.id.room_type)
    FormItemLayout room_type;
    @BindView(R.id.room_area)
    FormItemLayout room_area;
    @BindView(R.id.remark)
    RemarkEditTextLayout remark;
    @BindView(R.id.submit)
    TextView submit;
    @BindView(R.id.room_location)
    FormItemLayout room_location;

    TextView sel_title;

    private boolean isEstateSelectFromCentline; //标识是否从中原服务器选择的小区
    private boolean selectLocationFromMap;

    private String gScopeId;  //小区所在区域id
    private String gScopeName;  //小区所在区域名称
    private MyBottomDialog bottomWindow;   //底部选择框
    //底部选择器
    WheelPicker picker1;
    WheelPicker picker2;
    WheelPicker picker3;

    //选择器内容
//    private String picker1String;
//    private String picker2String;
//    private String picker3String;

    private List<BuildingNumBean> buildingNums;
    private List<BuildingNumBean> buildingRoomNums;

    //当前底部对话框类型
    private int bottomDialogType = BUILDING_NO_SELECTOR;

    private InputMethodManager inputMethodManager;
    private CompositeSubscription mCompositeSubscription;


    private List<String> buildingNos;   //楼号
    private List<String> roomNos;       //室号

    private DeputePushBean deputePushBean;

    public static DeputeSourceInfoFragment getInstance() {
        return new DeputeSourceInfoFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frg_depute_source_info;
    }

    @Override
    protected void init() {
        deputePushBean = ((DeputeActivity)getActivity()).getDeputePushBean();
        inputMethodManager  = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mCompositeSubscription = new CompositeSubscription();
        buildingNos = new ArrayList<>();
        roomNos = new ArrayList<>();
        initpopUpwindow();

        village_name.setImageListener(()->{
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            intent.putExtra(SearchActivity.SEARCH_TYPE_KEY, SEARCH_TYPE_ESTATE);
            intent.putExtra(SearchActivity.IS_GET_DATA, true);
            intent.putExtra(SearchActivity.IS_PUBLISH_SOURCE, true);
            startActivityForResult(intent, VILLAGE_REQUEST_CODE);
        });

        building_no.setImageListener(()-> clickBuildingNoFild());

        room_no.setImageListener(()-> clickRoomNoFild());

        room_type.setImageListener(()->{
            inputMethodManager.hideSoftInputFromWindow(room_type.getWindowToken(), 0);
            setPickersData(ROOM_TYPE_SELECTOR);
            mCompositeSubscription.add(Observable.timer(500, TimeUnit.MILLISECONDS)
                    .compose(SchedulersCompat.applyIoSchedulers())
                    .subscribe(num->{
                        sel_title.setText("选择户型");
                        bottomWindow.show();
                    }));
        });

        room_location.setImageListener(()->{
            //打开地图页点选小区
            Intent intent = new Intent(getActivity(), EstateMapSelectActivity.class);
            intent.putExtra(EstateMapSelectActivity.ESTATE_NAME,village_name.getText());
            intent.putExtra(EstateMapSelectActivity.ESTATE_ADDRESS,village_address.getText());
            startActivityForResult(intent,LOCATION_REQUEST_CODE);
        });

        RxView.clicks(submit).throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(aVoid -> {

                    showLoading();

                    if (village_name.getText() == null || village_name.getText().equals("")) {
                        toast("请选择小区");
                        return;
                    }

                    if (village_address.getText() == null || village_address.getText().trim().equals("")) {
                        toast("请输入小区地址");
                        return;
                    }

                    if (room_type.getText() == null || "".equals(room_type.getText())) {
                        toast("请选择户型");
                        return;
                    } else {
                        String strRoomType = room_type.getText().replaceAll("[\u4e00-\u9fa5]+", "");
                        deputePushBean.getEntrustData().setRoomCnt(Integer.parseInt(strRoomType.substring(0,1)));
                        deputePushBean.getEntrustData().setParlorCnt(Integer.parseInt(strRoomType.substring(1,2)));
                        deputePushBean.getEntrustData().setToiletCnt(Integer.parseInt(strRoomType.substring(2)));
                    }

                    if (room_area.getText() == null || "".equals(room_area.getText()) || Double.parseDouble(room_area.getText()) <= 0) {
                        toast("请输入正确的房屋面积");
                        return;
                    }

                    if (room_location.getExtText().equals("未选择") && room_location.getVisibility() == View.VISIBLE) {
                        toast("请选择小区位置");
                        return;
                    }

                    //用户自定义填写的楼号查询是否有匹配的系统楼号
                    int[] totalFloor = new int[]{-1};
                    if (deputePushBean.getEntrustData().getBuildingId() <= 0 && buildingNums != null) {
                        for (BuildingNumBean bean :buildingNums) {
                            if (bean.getBuildNUM().equals(building_no.getText())) {
                                deputePushBean.getEntrustData().setBuildingId(bean.getID());
                                totalFloor[0] = bean.getTotalFloor();
                                break;
                            }
                        }
                    }

                    if (deputePushBean.getEntrustData().getHouseId() <= 0 && buildingRoomNums != null) {
                        for (BuildingNumBean bean :buildingRoomNums) {
                            if (bean.getBuildNUM().equals(room_no.getText())) {
                                deputePushBean.getEntrustData().setHouseId(bean.getID());
                                deputePushBean.getEntrustData().setFloor(bean.getFloor());
                                if (totalFloor[0] < 0) {
                                    totalFloor[0] = bean.getFloor() * 2;
                                }
                                break;
                            }
                        }
                    }

                    deputePushBean.getEntrustData().setAddress(village_address.getText());
                    deputePushBean.getEntrustData().setEstateName(village_name.getText());
                    deputePushBean.getEntrustData().setBuildingName(building_no.getText());
                    deputePushBean.getEntrustData().setDoorNo(room_no.getText());
                    deputePushBean.getEntrustData().setSquare(Double.parseDouble(room_area.getText()));
                    deputePushBean.getEntrustData().setRemark(remark.getText());

                    //估价
                    mPresenter.getEvaluateRequest(new HashMap(){
                        {
                            put("estateName", deputePushBean.getEntrustData().getEstateName());
                            put("area", room_area.getText());
                            put("ting", deputePushBean.getEntrustData().getParlorCnt() + "");
                            put("wei", deputePushBean.getEntrustData().getToiletCnt() + "");
                            put("totalFloor", totalFloor[0] + "");
                            put("floor", deputePushBean.getEntrustData().getFloor() + "");
                        }
                    });
                    dismissLoading();
                    ((DeputeActivity)getActivity()).saveStep1(isEstateSelectFromCentline, selectLocationFromMap, gScopeId, gScopeName, deputePushBean);
                });


        building_no.setItemFocusListener(hasFocus -> {
            if (!hasFocus) {
                //如果用户手输的楼号在楼号列表中存在，则将房号选择打开
                if (deputePushBean.getEntrustData().getBuildingId() <= 0 && buildingNums != null) {
                    for (BuildingNumBean bean :buildingNums) {
                        if (bean.getBuildNUM().equals(building_no.getText())) {
                            deputePushBean.getEntrustData().setBuildingId(bean.getID());
                            room_no.setInputEnable(false);
                            room_no.setImgShow(true);
                            break;
                        }
                    }
                }

                if (deputePushBean.getEntrustData().getEstateId() > 0) {
                    building_no.setInputEnable(false);
                    building_no.setImgShow(true);
                }
//                room_no.setInputEnable(false);
//                room_no.setImgShow(true);
            }
        });

        room_no.setItemFocusListener(hasFocus -> {
            if (!hasFocus && deputePushBean.getEntrustData().getBuildingId() > 0) {
                room_no.setInputEnable(false);
                room_no.setImgShow(true);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPresenter.getFormdata();
    }

    @Override
    public void onDestroy() {
        if (mCompositeSubscription != null && mCompositeSubscription.hasSubscriptions()) {
            mCompositeSubscription.unsubscribe();
        }
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data!= null) {
            if (resultCode == LOCATION_RESULT_CODE_1) {
                selectLocationFromMap = true;
                deputePushBean.getEntrustData().setLat(data.getDoubleExtra("lat",-1));
                deputePushBean.getEntrustData().setLng(data.getDoubleExtra("lng",-1));
                room_location.setExtText("已选择");
                return;
            }

            //清除已选小区信息
            village_address.setText("");
            village_name.setText("");
            building_no.setText("");
            room_no.setText("");
            deputePushBean.getEntrustData().setBuildingId(0);
            deputePushBean.getEntrustData().setHouseId(0);
            deputePushBean.getEntrustData().setEstateId(0);
            deputePushBean.getEntrustData().setEstateCode(null);

            selectLocationFromMap = false;
             if (resultCode == VILLAGE_RESULT_CODE) {
                 isEstateSelectFromCentline = true;
                 deputePushBean.getEntrustData().setEstateId(Integer.parseInt(data.getStringExtra("EstateId")));
                 deputePushBean.getEntrustData().setEstateCode(data.getStringExtra("EstateCode"));
                 gScopeId = data.getStringExtra("GScopeId");
                 gScopeName = data.getStringExtra("GScopeName");
                 village_name.setText(data.getStringExtra("EstateName"));
                 village_address.setText(data.getStringExtra("EstateAddress"));

                 deputePushBean.getEntrustData().setLng(data.getDoubleExtra("EstateLng",-1));
                 deputePushBean.getEntrustData().setLat(data.getDoubleExtra("EstateLat",-1));
                 if ( deputePushBean.getEntrustData().getLat() <= 1) {
                     room_location.setVisibility(View.VISIBLE);
                 } else {
                     room_location.setVisibility(View.GONE);
                 }

                building_no.setInputEnable(false);
                building_no.setImgShow(true);
                room_no.setInputEnable(false);
                room_no.setImgShow(true);

                 //自动打开楼号选择
                 clickBuildingNoFild();

            } else if (resultCode == LOCATION_RESULT_CODE) {
                 isEstateSelectFromCentline = false;
                 village_name.setText(data.getStringExtra("name"));
                 village_address.setText(data.getStringExtra("address"));

                 deputePushBean.getEntrustData().setLng(data.getDoubleExtra("lng",-1));
                 deputePushBean.getEntrustData().setLat(data.getDoubleExtra("lat",-1));
                 if ( deputePushBean.getEntrustData().getLat() <= 1) {
                     room_location.setVisibility(View.VISIBLE);
                 } else {
                     room_location.setVisibility(View.GONE);
                 }

                 //百度地图搜索出来的小区是没有楼号室号数据的
                 building_no.setInputEnable(true);
                 building_no.setImgShow(false);
                 room_no.setInputEnable(true);
                 room_no.setImgShow(false);
             }
        }
    }

    @Override
    protected DeputeSourceInfoPresenter createPresenter() {
        return new DeputeSourceInfoPresenter();
    }

    @Override
    public void setBuildingNos(List<BuildingNumBean> list) {
        buildingNos.clear();
        roomNos.clear();
        if (list != null && list.size() > 0) {
            buildingNums = list;
            for (BuildingNumBean buildingNumBean:buildingNums) {
                buildingNos.add(buildingNumBean.getBuildNUM());
            }

            //默认选中当前wheel所在位置的值
//            building_no.setText((String) picker1.getData().get(picker1.getCurrentItemPosition()));
        }

        buildingNos.add("自定义");
        buildingNos.add("无");
        if (bottomDialogType == BUILDING_NO_SELECTOR) {
            picker1.setData(buildingNos);
        }

        //如果wheel所在位置在可选幢号内则默认获取该幢号id
        if (list != null && picker1.getCurrentItemPosition() < list.size()) {
            deputePushBean.getEntrustData().setBuildingId(list.get(picker1.getCurrentItemPosition()).getID() );
        }

    }

    @Override
    public void setRoomNos(List<BuildingNumBean> list) {
        roomNos.clear();
        if (list != null && list.size() > 0) {
            buildingRoomNums = list;
            for (BuildingNumBean buildingNumBean:buildingRoomNums) {
                roomNos.add(buildingNumBean.getBuildNUM());
            }

            //默认选中当前wheel所在位置的值
//            room_no.setText((String) picker1.getData().get(picker1.getCurrentItemPosition()));
        }
        roomNos.add("自定义");
        if (bottomDialogType == ROOM_NO_SELECTOR) {
            picker1.setData(roomNos);
        }
    }

    @Override
    public void reloadData() {
        DeputeActivity activity = (DeputeActivity) getActivity();
        deputePushBean = activity.getDeputePushBean();
        gScopeId = activity.getgScopeId();
        gScopeName = activity.getgScopeName();

        village_name.setText(deputePushBean.getEntrustData().getEstateName());
        village_address.setText(deputePushBean.getEntrustData().getAddress());
        building_no.setText(deputePushBean.getEntrustData().getBuildingName());
        room_no.setText(deputePushBean.getEntrustData().getDoorNo());
        remark.setText(deputePushBean.getEntrustData().getRemark());
        int num1 = deputePushBean.getEntrustData().getRoomCnt();
        int num2 = deputePushBean.getEntrustData().getParlorCnt();
        int num3 = deputePushBean.getEntrustData().getToiletCnt();
        if (num1 + num2 + num3 > 0) {
            room_type.setText(num1 + "厅" + num2 + "室" + num3 + "卫");
        }
        if (deputePushBean.getEntrustData().getSquare() > 0) {
            room_area.setText(deputePushBean.getEntrustData().getSquare() + "");
        }
//        remark.setText(activity.getHouseRemark()); 设置备注信息

        isEstateSelectFromCentline = activity.isEstateSelectFromCentline();
        selectLocationFromMap = activity.isSelectLocationFromMap();

        if (!isEstateSelectFromCentline) {
            building_no.setInputEnable(true);
            building_no.setImgShow(false);
            room_no.setInputEnable(true);
            room_no.setImgShow(false);
        }

        if (selectLocationFromMap) {
            room_location.setVisibility(View.VISIBLE);
            room_location.setExtText("已选择");
        }
    }

    @Override
    public void setEvaluate(double num) {
        ((DeputeActivity)getActivity()).setEvaluatePrice(num);
        addFragment(DeputePriceFragment.getInstance());  //跳转到下一页
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
//        toast("加载数据有误");
    }

    private void clickBuildingNoFild() {
        sel_title.setText("选择楼号");
        inputMethodManager.hideSoftInputFromWindow(building_no.getWindowToken(), 0);
        setPickersData(BUILDING_NO_SELECTOR);
        if (deputePushBean.getEntrustData().getEstateId() <= 0) {
            toast("请先选择小区");
            return;
        }
        mCompositeSubscription.add(Observable.timer(200, TimeUnit.MILLISECONDS)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(num->{
                    bottomWindow.show();
                }));
    }

    private void clickRoomNoFild() {
        sel_title.setText("选择室号");
        inputMethodManager.hideSoftInputFromWindow(room_no.getWindowToken(), 0);
        setPickersData(ROOM_NO_SELECTOR);
        if (deputePushBean.getEntrustData().getEstateId() <= 0) {
            toast("请先选择小区");
            return;
        }
        if (deputePushBean.getEntrustData().getBuildingId() <= 0) {
            toast("请先选择楼号");
            return;
        }

        mCompositeSubscription.add(Observable.timer(200, TimeUnit.MILLISECONDS)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(num->{
                    bottomWindow.show();
                }));
    }

    /**
     * 初始化底部对话框
     */
    private void initpopUpwindow() {
        LinearLayout dialog_layout = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.publish_source_bottom_layout, null);
        bottomWindow = new MyBottomDialog(getActivity());
        bottomWindow.setContentView(dialog_layout);
        sel_title = (TextView) dialog_layout.findViewById(R.id.sel_title);
        picker1 = (WheelPicker) dialog_layout.findViewById(R.id.selector_1);
        picker2 = (WheelPicker) dialog_layout.findViewById(R.id.selector_2);
        picker3 = (WheelPicker) dialog_layout.findViewById(R.id.selector_3);

        dialog_layout.findViewById(R.id.cancel).setOnClickListener(v->{
            bottomWindow.dismiss();
        });
        dialog_layout.findViewById(R.id.submit).setOnClickListener(v->{
            switch (bottomDialogType) {
                case BUILDING_NO_SELECTOR:
                    room_no.setInputEnable(false);
                    room_no.setImgShow(true);
                    String selString = (String) picker1.getData().get(picker1.getCurrentItemPosition());
                    if ("自定义".equals(selString)) {
                        //自定义填写
                        building_no.setInputEnable(true);
                        building_no.setImgShow(false);
                        building_no.focusInput();
                        //强制显示键盘
                        inputMethodManager.showSoftInput(building_no, 0);
                        room_no.setInputEnable(true);
                        room_no.setImgShow(false);
                        deputePushBean.getEntrustData().setBuildingId(0);
                    } else if ("无".equals(selString)){
                        room_no.setInputEnable(true);
                        room_no.setImgShow(false);
                        room_no.focusInput();
                        building_no.setText(selString == null ? (String)picker1.getData().get(0) : selString);
                        deputePushBean.getEntrustData().setBuildingId(0);
                    } else {
                        building_no.setText(selString == null ? (String)picker1.getData().get(0) : selString);
                        if (buildingNums != null) {
                            deputePushBean.getEntrustData().setBuildingId(buildingNums.get(picker1.getCurrentItemPosition()).getID());
                        }else {
                            deputePushBean.getEntrustData().setBuildingId(-1);
                        }
                        //自动触发室号选择
                        clickRoomNoFild();
                    }
                    break;
                case ROOM_NO_SELECTOR:
                    String roomString = (String) picker1.getData().get(picker1.getCurrentItemPosition());
                    room_no.setText("自定义".equals(roomString)?"":roomString);
                    if ("自定义".equals(roomString)) {
                        room_no.setInputEnable(true);
                        room_no.setImgShow(false);
                        room_no.focusInput();
                    } else {

                    }
                    break;
                case ROOM_TYPE_SELECTOR:
                    String shi = (String) picker1.getData().get(picker1.getCurrentItemPosition());
                    String ting = (String) picker2.getData().get(picker2.getCurrentItemPosition());
                    String wei = (String) picker3.getData().get(picker3.getCurrentItemPosition());
                    room_type.setText(shi+ting+wei);
                    break;
            }
            bottomWindow.dismiss();
        });
        setPickersData(BUILDING_NO_SELECTOR);
    }

    private void setPickersData(int type) {
        if (type != bottomDialogType) {
            picker1.setSelectedItemPosition(0);
            picker2.setSelectedItemPosition(0);
            picker3.setSelectedItemPosition(0);
            bottomDialogType = type;
        }
        switch (bottomDialogType) {
            case BUILDING_NO_SELECTOR:
                if (deputePushBean.getEntrustData().getEstateId() > 0) {
                    mPresenter.getBuildingNos(deputePushBean.getEntrustData().getEstateId()+"");
                }
                picker2.setVisibility(View.GONE);
                picker3.setVisibility(View.GONE);
                break;
            case ROOM_NO_SELECTOR:
                if (deputePushBean.getEntrustData().getBuildingId() > 0) {
                    mPresenter.getRoomNos(deputePushBean.getEntrustData().getBuildingId()+"");
                }
                picker2.setVisibility(View.GONE);
                picker3.setVisibility(View.GONE);
                break;
            case ROOM_TYPE_SELECTOR:
                picker1.setData(Arrays.asList(getResources().getStringArray(R.array.roomType1)));
                picker2.setVisibility(View.VISIBLE);
                picker3.setVisibility(View.VISIBLE);
//                picker2.setData(Arrays.asList(getResources().getStringArray(R.array.roomType2)));
//                picker3.setData(Arrays.asList(getResources().getStringArray(R.array.roomType3)));
                break;
        }
    }
}
