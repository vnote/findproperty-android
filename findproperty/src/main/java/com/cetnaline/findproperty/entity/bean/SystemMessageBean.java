package com.cetnaline.findproperty.entity.bean;

/**
 * Created by diaoqf on 2017/2/22.
 */

public class SystemMessageBean extends BaseBean {

    /**
     * RelationID : 1111113333333333333311111111111
     * SystemMessageID : 139
     * CityCode : 021
     * UserId : U100144469
     * AppName : APP
     * IsRead : false
     * IsSend : false
     * IsPush : false
     * Type : 0
     * CreateTime : /Date(1487670687000+0800)/
     * UpdateTime : /Date(1487670687000+0800)/
     * Content : 约看成功约看成功约看成功约看成功
     * IsDel : 0
     * AllCount : 137
     */

    private String RelationID;
    private int SystemMessageID;
    private String CityCode;
    private String UserId;
    private String AppName;
    private boolean IsRead;
    private boolean IsSend;
    private boolean IsPush;
    private String Remark;  //zufang/ershoufang
    private int Type;     //1约看,2订阅,3委托
    private String CreateTime;
    private String UpdateTime;
    private String Content;
    private int IsDel;
    private int AllCount;

    public String getRelationID() {
        return RelationID;
    }

    public void setRelationID(String RelationID) {
        this.RelationID = RelationID;
    }

    public int getSystemMessageID() {
        return SystemMessageID;
    }

    public void setSystemMessageID(int SystemMessageID) {
        this.SystemMessageID = SystemMessageID;
    }

    public String getCityCode() {
        return CityCode;
    }

    public void setCityCode(String CityCode) {
        this.CityCode = CityCode;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String UserId) {
        this.UserId = UserId;
    }

    public String getAppName() {
        return AppName;
    }

    public void setAppName(String AppName) {
        this.AppName = AppName;
    }

    public boolean isIsRead() {
        return IsRead;
    }

    public void setIsRead(boolean IsRead) {
        this.IsRead = IsRead;
    }

    public boolean isIsSend() {
        return IsSend;
    }

    public void setIsSend(boolean IsSend) {
        this.IsSend = IsSend;
    }

    public boolean isIsPush() {
        return IsPush;
    }

    public void setIsPush(boolean IsPush) {
        this.IsPush = IsPush;
    }

    public int getType() {
        return Type;
    }

    public void setType(int Type) {
        this.Type = Type;
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

    public String getContent() {
        return Content;
    }

    public void setContent(String Content) {
        this.Content = Content;
    }

    public int getIsDel() {
        return IsDel;
    }

    public void setIsDel(int IsDel) {
        this.IsDel = IsDel;
    }

    public int getAllCount() {
        return AllCount;
    }

    public void setAllCount(int AllCount) {
        this.AllCount = AllCount;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }
}
