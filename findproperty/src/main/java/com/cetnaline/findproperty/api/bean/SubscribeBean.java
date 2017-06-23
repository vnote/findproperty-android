package com.cetnaline.findproperty.api.bean;

/**
 * Created by diaoqf on 2016/8/24.
 */
public class SubscribeBean {

    /**
     * IntentionID : 1247
     * CityCode : 021
     * Source : ershoufang
     * IsDel : false
     * AppName : ESF
     * CreateTime : 1471099453
     * SearchModel : ershoufang_search
     * SearchPara : regionid:2185;regionfullpy:2185;t:3;pmax:1000
     * SearchUrl : /ershoufang/qingpuqu/t3/?pmin=0&pmax=1000
     * SearchModelName : 大搜索二手房_模糊
     * SearchText : 青浦,0-1000万,别墅
     */

    private int IntentionID;
    private String CityCode;
    private String Source;
    private boolean IsDel;
    private String AppName;
    private String CreateTime;
    private String SearchModel;
    private String SearchPara;
    private String SearchUrl;
    private String SearchModelName;
    private String SearchText;

    public int getIntentionID() {
        return IntentionID;
    }

    public void setIntentionID(int IntentionID) {
        this.IntentionID = IntentionID;
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

    public boolean isDel() {
        return IsDel;
    }

    public void setDel(boolean del) {
        IsDel = del;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public String getSearchModel() {
        return SearchModel;
    }

    public void setSearchModel(String SearchModel) {
        this.SearchModel = SearchModel;
    }

    public String getSearchPara() {
        return SearchPara;
    }

    public void setSearchPara(String SearchPara) {
        this.SearchPara = SearchPara;
    }

    public String getSearchUrl() {
        return SearchUrl;
    }

    public void setSearchUrl(String SearchUrl) {
        this.SearchUrl = SearchUrl;
    }

    public String getSearchModelName() {
        return SearchModelName;
    }

    public void setSearchModelName(String SearchModelName) {
        this.SearchModelName = SearchModelName;
    }

    public String getSearchText() {
        return SearchText;
    }

    public void setSearchText(String SearchText) {
        this.SearchText = SearchText;
    }
}
