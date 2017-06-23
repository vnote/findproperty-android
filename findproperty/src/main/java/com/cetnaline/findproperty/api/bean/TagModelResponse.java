package com.cetnaline.findproperty.api.bean;

import com.cetnaline.findproperty.db.entity.HistoryHouseTag;

import java.io.Serializable;

/**
 * Created by sunxl8 on 2016/8/22.
 */

public class TagModelResponse extends HistoryHouseTag{

    /**
     TagCode	        string	区域板块ID、经纪人no、小区code
     PN1	            string	区域名
     PN2	            string	板块名
     Tag	            string	匹配的小区名 区域名 板块名 学校名
     TagPY	            string	Tag的拼音形式
     TagCategory	    string	匹配的类别 小区:estate 区域:region 板块:block 学校:school
     SNum	            int	    可售数
     RNum	            int	    可租数
     EstateAvgPriceSale	double	小区均价：售
     EstateAvgPriceRent	double	小区均价：租
     */

    private String address; //地址
    private Double lat;   //经纬度
    private Double lng;
    private String gScopeId; //所在区域id
    private String gScopeName; //区域名称
    private String estateCode; //小区code

    public String getEstateCode() {
        return estateCode;
    }

    public void setEstateCode(String estateCode) {
        this.estateCode = estateCode;
    }

    public String getgScopeName() {
        return gScopeName;
    }

    public void setgScopeName(String gScopeName) {
        this.gScopeName = gScopeName;
    }

    public String getgScopeId() {
        return gScopeId;
    }

    public void setgScopeId(String gScopeId) {
        this.gScopeId = gScopeId;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    //    private String TagCode;
//
//    private String PN1;
//
//    private String PN2;
//
//    private String Tag;
//
//    private String TagPY;
//
//    private String TagCategory;
//
//    private int SNum;
//
//    private int RNum;
//
//    private double EstateAvgPriceSale;
//
//    private double EstateAvgPriceRent;
//
//    public String getTagCode() {
//        return TagCode;
//    }
//
//    public void setTagCode(String tagCode) {
//        TagCode = tagCode;
//    }
//
//    public String getPN1() {
//        return PN1;
//    }
//
//    public void setPN1(String PN1) {
//        this.PN1 = PN1;
//    }
//
//    public String getPN2() {
//        return PN2;
//    }
//
//    public void setPN2(String PN2) {
//        this.PN2 = PN2;
//    }
//
//    public String getTag() {
//        return Tag;
//    }
//
//    public void setTag(String tag) {
//        Tag = tag;
//    }
//
//    public String getTagPY() {
//        return TagPY;
//    }
//
//    public void setTagPY(String tagPY) {
//        TagPY = tagPY;
//    }
//
//    public String getTagCategory() {
//        return TagCategory;
//    }
//
//    public void setTagCategory(String tagCategory) {
//        TagCategory = tagCategory;
//    }
//
//    public int getSNum() {
//        return SNum;
//    }
//
//    public void setSNum(int SNum) {
//        this.SNum = SNum;
//    }
//
//    public int getRNum() {
//        return RNum;
//    }
//
//    public void setRNum(int RNum) {
//        this.RNum = RNum;
//    }
//
//    public double getEstateAvgPriceSale() {
//        return EstateAvgPriceSale;
//    }
//
//    public void setEstateAvgPriceSale(double estateAvgPriceSale) {
//        EstateAvgPriceSale = estateAvgPriceSale;
//    }
//
//    public double getEstateAvgPriceRent() {
//        return EstateAvgPriceRent;
//    }
//
//    public void setEstateAvgPriceRent(double estateAvgPriceRent) {
//        EstateAvgPriceRent = estateAvgPriceRent;
//    }
//
//    @Override
//    public String toString() {
//        return "TagModelResponse{" +
//                "TagCode='" + TagCode + '\'' +
//                ", PN1='" + PN1 + '\'' +
//                ", PN2='" + PN2 + '\'' +
//                ", Tag='" + Tag + '\'' +
//                ", TagPY='" + TagPY + '\'' +
//                ", TagCategory='" + TagCategory + '\'' +
//                ", SNum=" + SNum +
//                ", RNum=" + RNum +
//                ", EstateAvgPriceSale=" + EstateAvgPriceSale +
//                ", EstateAvgPriceRent=" + EstateAvgPriceRent +
//                '}';
//    }
}
