package com.cetnaline.findproperty.entity.bean;

import java.util.List;

/**
 * Created by diaoqf on 2016/7/28.
 */
public class CmAdviserBean extends CmBaseBean {

    public String cityCode;
    public String StaffNo;//经纪人编号
    public String CnName;//名称
    public String DepartmentName;//部门名称
    public String Mobile;//电话
    public String Email;//邮件
    public String ImgUrl;//经纪人图片
    public String BigCode;//400电话号主号
    public String ExtCode;//400电话号副号
    public int StarsSum;//经纪人评价
    public int StoreID;

    public List<StaffEstatePostGroupBean> StaffEstatePostGroupBean;

}
