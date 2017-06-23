package com.cetnaline.findproperty.entity.bean;

/**
 * Created by diaoqf on 2016/8/1.
 */
public class RcUserInfoBean extends RcBaseBean {
    public String userId;
    public String nickName;
    public String portraitUrl;
    public String mobile;

    public RcUserInfoBean() {
    }

    public RcUserInfoBean(String userId, String nickName, String portraitUrl, String mobile) {
        this.userId = userId;
        this.nickName = nickName;
        this.portraitUrl = portraitUrl;
        this.mobile = mobile;
    }
}
