package com.cetnaline.findproperty.entity.ui;

/**
 * Created by diaoqf on 2016/8/16.
 */
public class TaxCalculateBo {
    public int modeType;    //1.二手房,2.新房
    public int houseType;    //1."普通住宅",2."非普通住宅"
    public double houseArea;
    public double houseUnitPrice;
    public double houseTotal;
    public boolean buyerFirst;  //true:首次购房,
    public boolean sellerOnly;    //true:卖房家庭唯一住房
    public int levyType;      //征收类型  1,"总价" 2,"差价"
    public double originPrice;  //原价
    public int buyYear;     // 房屋年限  0:"2年以内"  ; 1:"2-5年" ; 2:"5年以上"
}
