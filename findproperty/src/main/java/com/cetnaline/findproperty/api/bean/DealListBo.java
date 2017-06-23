package com.cetnaline.findproperty.api.bean;

/**
 * 成交列表
 * Created by Ruan on 2016/2/16.
 */
public class DealListBo {

    private int DealYear;
    private int DealMonth;
    private double DealAvgPrice;
    private String PostType;

    public int getDealYear() {
        return DealYear;
    }

    public void setDealYear(int dealYear) {
        DealYear = dealYear;
    }

    public int getDealMonth() {
        return DealMonth;
    }

    public void setDealMonth(int dealMonth) {
        DealMonth = dealMonth;
    }

    public double getDealAvgPrice() {
        return DealAvgPrice;
    }

    public void setDealAvgPrice(double dealAvgPrice) {
        DealAvgPrice = dealAvgPrice;
    }

    public String getPostType() {
        return PostType;
    }

    public void setPostType(String postType) {
        PostType = postType;
    }
}
