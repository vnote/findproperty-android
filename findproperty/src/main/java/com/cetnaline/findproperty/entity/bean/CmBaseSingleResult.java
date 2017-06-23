package com.cetnaline.findproperty.entity.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by diaoqf on 2016/8/2.
 */
public class CmBaseSingleResult<T extends CmBaseBean> implements Serializable {
    public int RCode;
    public String RMessage;
    public int Total;

    public T Result;
}
