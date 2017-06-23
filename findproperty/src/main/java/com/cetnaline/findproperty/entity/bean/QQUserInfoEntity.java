package com.cetnaline.findproperty.entity.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by diaoqf on 2016/8/12.
 */
public class QQUserInfoEntity implements Serializable {
    public int ret;
    public String openid;
    public String access_token;
    public String pay_token;
    public String expires_in;
    public String pf;
    public String pfkey;
    public String msg;
    public int login_cost;
    public int query_authority_cost;
    public long authority_cost;

    public String nickname;
    public String figureurl_qq_2;
}
