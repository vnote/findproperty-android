package com.cetnaline.findproperty.entity.result;

import com.cetnaline.findproperty.entity.bean.BaseBean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by diaoqf on 2016/8/5.
 */
public class BaseResult<T extends BaseBean> implements Serializable {
    public int ResultNo; //0-正常
    public String Message; //返回的消息

    public List<T> Result; //查询结果

    public int Total;  //查询到的数据总量
}
