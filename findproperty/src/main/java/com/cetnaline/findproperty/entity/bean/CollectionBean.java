package com.cetnaline.findproperty.entity.bean;

/**
 * Created by diaoqf on 2016/8/19.
 */
public class CollectionBean extends BaseBean {

    /**
     * CollectID : 129918
     * CollectValue : 1f9c158a-8fcb-4e5c-bc46-47558d2df9e3
     * UserId : U100060917
     * CityCode : 021
     * Source : ershoufang
     * IsDel : false
     * AppName : APP
     * CreateTime : /Date(1471578732000+0800)/
     * CreateTime2 : 1471578732
     */

    private long CollectID;
    private String CollectValue;
    private String UserId;
    private String CityCode;
    private String Source;
    private boolean IsDel;
    private String AppName;
    private String CreateTime;
    private int CreateTime2;
    private String CollectUrl;

    public long getCollectID() {
        return CollectID;
    }

    public void setCollectID(long collectID) {
        CollectID = collectID;
    }

    public String getCollectValue() {
        return CollectValue;
    }

    public void setCollectValue(String collectValue) {
        CollectValue = collectValue;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getCityCode() {
        return CityCode;
    }

    public void setCityCode(String cityCode) {
        CityCode = cityCode;
    }

    public String getSource() {
        return Source;
    }

    public void setSource(String source) {
        Source = source;
    }

    public boolean isDel() {
        return IsDel;
    }

    public void setDel(boolean del) {
        IsDel = del;
    }

    public String getAppName() {
        return AppName;
    }

    public void setAppName(String appName) {
        AppName = appName;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public int getCreateTime2() {
        return CreateTime2;
    }

    public void setCreateTime2(int createTime2) {
        CreateTime2 = createTime2;
    }

    public String getCollectUrl() {
        return CollectUrl;
    }

    public void setCollectUrl(String collectUrl) {
        CollectUrl = collectUrl;
    }
}
