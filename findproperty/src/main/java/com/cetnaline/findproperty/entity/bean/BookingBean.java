package com.cetnaline.findproperty.entity.bean;

import com.cetnaline.findproperty.entity.bean.BaseBean;

/**
 * Created by diaoqf on 2016/8/24.
 */
public class BookingBean extends BaseBean {

    /**
     * BookingId : 0
     * ActId : 0
     * EstId : String
     * EstExtId : String
     * UserId : String
     * CustomerName : String
     * CustomerMobile : String
     * BookingDate : /Date(-62135596800000-0000)/
     * CustomerSex : String
     * ArriveTime : /Date(-62135596800000-0000)/
     * CreateTime : /Date(-62135596800000-0000)/
     * UpdateTime : /Date(-62135596800000-0000)/
     * CreateTime2 : -62135625600
     * UpdateTime2 : -62135625600
     * CityCode : String
     * Source : String
     * IsDel : false
     * AppName : String
     * AllCount : 0
     */

    private int BookingId;
    private int ActId;
    private String EstId;
    private String EstExtId;
    private String UserId;
    private String CustomerName;
    private String CustomerMobile;
    private String BookingDate;
    private String CustomerSex;
    private String ArriveTime;
    private String CreateTime;
    private String UpdateTime;
    private long CreateTime2;
    private long UpdateTime2;
    private String CityCode;
    private String Source;
    private boolean IsDel;
    private String AppName;
    private int AllCount;

    public int getBookingId() {
        return BookingId;
    }

    public void setBookingId(int BookingId) {
        this.BookingId = BookingId;
    }

    public int getActId() {
        return ActId;
    }

    public void setActId(int ActId) {
        this.ActId = ActId;
    }

    public String getEstId() {
        return EstId;
    }

    public void setEstId(String EstId) {
        this.EstId = EstId;
    }

    public String getEstExtId() {
        return EstExtId;
    }

    public void setEstExtId(String EstExtId) {
        this.EstExtId = EstExtId;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String UserId) {
        this.UserId = UserId;
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String CustomerName) {
        this.CustomerName = CustomerName;
    }

    public String getCustomerMobile() {
        return CustomerMobile;
    }

    public void setCustomerMobile(String CustomerMobile) {
        this.CustomerMobile = CustomerMobile;
    }

    public String getBookingDate() {
        return BookingDate;
    }

    public void setBookingDate(String BookingDate) {
        this.BookingDate = BookingDate;
    }

    public String getCustomerSex() {
        return CustomerSex;
    }

    public void setCustomerSex(String CustomerSex) {
        this.CustomerSex = CustomerSex;
    }

    public String getArriveTime() {
        return ArriveTime;
    }

    public void setArriveTime(String ArriveTime) {
        this.ArriveTime = ArriveTime;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String CreateTime) {
        this.CreateTime = CreateTime;
    }

    public String getUpdateTime() {
        return UpdateTime;
    }

    public void setUpdateTime(String UpdateTime) {
        this.UpdateTime = UpdateTime;
    }

    public long getCreateTime2() {
        return CreateTime2;
    }

    public void setCreateTime2(long CreateTime2) {
        this.CreateTime2 = CreateTime2;
    }

    public long getUpdateTime2() {
        return UpdateTime2;
    }

    public void setUpdateTime2(long UpdateTime2) {
        this.UpdateTime2 = UpdateTime2;
    }

    public String getCityCode() {
        return CityCode;
    }

    public void setCityCode(String CityCode) {
        this.CityCode = CityCode;
    }

    public String getSource() {
        return Source;
    }

    public void setSource(String Source) {
        this.Source = Source;
    }

    public boolean isIsDel() {
        return IsDel;
    }

    public void setIsDel(boolean IsDel) {
        this.IsDel = IsDel;
    }

    public String getAppName() {
        return AppName;
    }

    public void setAppName(String AppName) {
        this.AppName = AppName;
    }

    public int getAllCount() {
        return AllCount;
    }

    public void setAllCount(int AllCount) {
        this.AllCount = AllCount;
    }
}
