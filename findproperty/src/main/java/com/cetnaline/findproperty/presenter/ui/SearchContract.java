package com.cetnaline.findproperty.presenter.ui;

import com.cetnaline.findproperty.api.bean.EstateBo;
import com.cetnaline.findproperty.api.bean.SeoHotModelResponse;
import com.cetnaline.findproperty.api.bean.TagModelResponse;
import com.cetnaline.findproperty.presenter.BaseView;
import com.cetnaline.findproperty.presenter.IPresenter;

import java.util.List;
import java.util.Map;

/**
 * Created by sunxl8 on 2016/8/22.
 */

public interface SearchContract {

    interface View extends BaseView {

        void setSeoHotTag(List<SeoHotModelResponse> listSeoHotTag);

        void setSearchTag(List<TagModelResponse> listTag);

        void setEstateList(List<EstateBo> listTag);
    }

    interface Presenter extends IPresenter<View> {

        void getSeoHotTag();

        void getSearchTag(Map<String, String> params);

        void getEstateList(String key);

        void wordFrequency(String type, String msg);

    }
}
