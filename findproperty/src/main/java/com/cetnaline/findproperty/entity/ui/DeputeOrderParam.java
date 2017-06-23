package com.cetnaline.findproperty.entity.ui;

import java.io.Serializable;

/**
 * Created by diaoqf on 2017/4/11.
 */

public class DeputeOrderParam implements Serializable {


    /**
     * nickName : String
     * mobile : String
     */

    private String nickName;
    private String mobile;
    private String UserSex;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUserSex() {
        return UserSex;
    }

    public void setUserSex(String userSex) {
        UserSex = userSex;
    }
}
