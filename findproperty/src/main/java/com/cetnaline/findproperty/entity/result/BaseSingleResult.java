package com.cetnaline.findproperty.entity.result;

import com.cetnaline.findproperty.entity.bean.BaseBean;

import java.io.Serializable;

/**
 * Created by diaoqf on 2016/8/8.
 */
public class BaseSingleResult<T> implements Serializable {
    public int ResultNo;
    public String Message;
    public int Total;

    public T Result;
}
