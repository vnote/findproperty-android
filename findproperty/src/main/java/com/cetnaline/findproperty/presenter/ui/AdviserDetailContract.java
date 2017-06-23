package com.cetnaline.findproperty.presenter.ui;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.cetnaline.findproperty.api.bean.HouseBo;
import com.cetnaline.findproperty.db.entity.Staff;
import com.cetnaline.findproperty.entity.bean.StaffListBean;
import com.cetnaline.findproperty.entity.event.ShareEvent;
import com.cetnaline.findproperty.inter.RefreshListener;
import com.cetnaline.findproperty.presenter.BaseView;
import com.cetnaline.findproperty.presenter.IPresenter;
import com.cetnaline.findproperty.ui.fragment.HouseListFragment;

import java.util.List;

/**
 * Created by diaoqf on 2016/8/28.
 */
public interface AdviserDetailContract {
    interface View extends BaseView {
        void setFragments(List<Fragment> fragments);
        void setStaffMsg(String msg);
        void setStaff(Staff staff);
        void setDepartment(String name);
        void loadHeadImage(String uri);
        void setSaleAllLoad(boolean loaded);
        void setRentAllLoad(boolean loaded);

        void showShareDialog(boolean show);
        void showMessage(String message);

        void addSalePage();
        void addRentPage();
        void setSaleFragmentData(List<HouseBo> data);
        void setRendFragmentData(List<HouseBo> data);

        void setFragmentNoData(int fragment);
    }

    interface Presenter extends IPresenter<AdviserDetailContract.View> {
        void getFragments(RefreshListener listener1, RefreshListener listener2);
        void getDepartments(String id, String no);

        void registerShareEvent(Class<ShareEvent> clz);

        void showCallDialog(Activity context, String label, StaffListBean adviser);
        void openConversation(Activity context,StaffListBean adviser);
    }
}
