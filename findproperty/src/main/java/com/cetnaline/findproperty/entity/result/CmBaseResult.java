package com.cetnaline.findproperty.entity.result;

import com.cetnaline.findproperty.entity.bean.CmBaseBean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by diaoqf on 2016/7/29.
 */
public class CmBaseResult<T extends CmBaseBean> implements Serializable {
    public int RCode;
    public String RMessage;
    public int Total;

    public List<T> Result;
}
