package com.cetnaline.findproperty.entity.ui;

import java.io.Serializable;

/**
 * Created by diaoqf on 2017/4/11.
 */

public class DeputeEntrustStoreRelation implements Serializable {


    /**
     * storeId : 0
     * storeCode : String
     * storeName : String
     * regionId : 0
     * gscopeId : 0
     * relationType : 0
     */

    private int storeId;
    private String storeCode;
    private String storeName;
    private int regionId;
    private int gscopeId;
    private int relationType;

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public String getStoreCode() {
        return storeCode;
    }

    public void setStoreCode(String storeCode) {
        this.storeCode = storeCode;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public int getRegionId() {
        return regionId;
    }

    public void setRegionId(int regionId) {
        this.regionId = regionId;
    }

    public int getGscopeId() {
        return gscopeId;
    }

    public void setGscopeId(int gscopeId) {
        this.gscopeId = gscopeId;
    }

    public int getRelationType() {
        return relationType;
    }

    public void setRelationType(int relationType) {
        this.relationType = relationType;
    }
}
