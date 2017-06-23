package com.cetnaline.findproperty.entity.ui;

import java.io.Serializable;

/**
 * Created by diaoqf on 2017/4/11.
 */

public class DeputeEntrustOrder implements Serializable {


    /**
     * entrustType : 0
     * userId : String
     * entrusCode : String
     * version : 0
     * appName : String
     * staffNo : String
     * adsNo : String
     * staffName : String
     * processStatus : 0
     * postID : 00000000000000000000000000000000
     */

    private int entrustType;
    private String userId;
    private String entrusCode;
    private int version;
    private String appName;
    private String staffNo;
    private String adsNo;
    private String staffName;
    private int processStatus;
    private String postID;

    public int getEntrustType() {
        return entrustType;
    }

    public void setEntrustType(int entrustType) {
        this.entrustType = entrustType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEntrusCode() {
        return entrusCode;
    }

    public void setEntrusCode(String entrusCode) {
        this.entrusCode = entrusCode;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getStaffNo() {
        return staffNo;
    }

    public void setStaffNo(String staffNo) {
        this.staffNo = staffNo;
    }

    public String getAdsNo() {
        return adsNo;
    }

    public void setAdsNo(String adsNo) {
        this.adsNo = adsNo;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public int getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(int processStatus) {
        this.processStatus = processStatus;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }
}
