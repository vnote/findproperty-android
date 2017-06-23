package com.cetnaline.findproperty.entity.bean;


/**
 * Created by diaoqf on 2016/12/1.
 */

public class ConsultFormBean extends BaseBean {

    /**
     * CityCode : 021
     * AppName : APP
     * Source : zhiliao
     * FeedBack : String
     * UserID : String
     * Type : 1
     * RegionName : String
     * UserName : String
     * UserPhone : String
     * Reason : String
     * JJR : String
     * JJRName : String
     * StoreId : String
     * StoreName : String
     * ConsultMessageRequest : {"MsgType":"LegalAdvice","AppName":"PC","UserID":"u_u100078075","ContentMsg":"测试 我想了解一下二手房组合贷款？"}
     */

    private String CityCode;
    private String AppName;
    private String Source;
    private String FeedBack;
    private String UserID;
    private int Type;
    private String UserName;
    private String UserPhone;
    private String Reason;
    private String JJR;
    private String JJRName;
    private int ComplaintType;
    private String ComplaintTarget;

    public ConsultFormBean(String cityCode, String appName, String source, String feedBack, String userID, int type, String userName, String userPhone, String reason, String JJR, String JJRName, int ComplaintType, String ComplaintTarget) {
        CityCode = cityCode;
        AppName = appName;
        Source = source;
        FeedBack = feedBack;
        UserID = userID;
        Type = type;
        UserName = userName;
        UserPhone = userPhone;
        Reason = reason;
        this.JJR = JJR;
        this.JJRName = JJRName;
        this.ComplaintType = ComplaintType;
        this.ComplaintTarget = ComplaintTarget;
    }

    public int getComplaintType() {
        return ComplaintType;
    }

    public void setComplaintType(int complaintType) {
        ComplaintType = complaintType;
    }

    public String getComplaintTarget() {
        return ComplaintTarget;
    }

    public void setComplaintTarget(String complaintTarget) {
        ComplaintTarget = complaintTarget;
    }

    public String getCityCode() {
        return CityCode;
    }

    public void setCityCode(String CityCode) {
        this.CityCode = CityCode;
    }

    public String getAppName() {
        return AppName;
    }

    public void setAppName(String AppName) {
        this.AppName = AppName;
    }

    public String getSource() {
        return Source;
    }

    public void setSource(String Source) {
        this.Source = Source;
    }

    public String getFeedBack() {
        return FeedBack;
    }

    public void setFeedBack(String FeedBack) {
        this.FeedBack = FeedBack;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String UserID) {
        this.UserID = UserID;
    }

    public int getType() {
        return Type;
    }

    public void setType(int Type) {
        this.Type = Type;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String UserName) {
        this.UserName = UserName;
    }

    public String getUserPhone() {
        return UserPhone;
    }

    public void setUserPhone(String UserPhone) {
        this.UserPhone = UserPhone;
    }

    public String getReason() {
        return Reason;
    }

    public void setReason(String Reason) {
        this.Reason = Reason;
    }

    public String getJJR() {
        return JJR;
    }

    public void setJJR(String JJR) {
        this.JJR = JJR;
    }

    public String getJJRName() {
        return JJRName;
    }

    public void setJJRName(String JJRName) {
        this.JJRName = JJRName;
    }

}
