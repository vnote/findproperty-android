package com.cetnaline.findproperty.entity.ui;

import java.io.Serializable;
import java.util.List;

/**
 * Created by diaoqf on 2017/4/11.
 */

public class DeputePushBean implements Serializable {

    private DeputeOrderParam orderParam;
    private DeputeEntrustOrder entrustOrder;
    private DeputeEntrustData entrustData;
    private List<DeputeEntrustStoreRelation> entrustStoreRelation;
    private List<EntrustAttachment> entrustAttachment;

    public DeputeOrderParam getOrderParam() {
        return orderParam;
    }

    public void setOrderParam(DeputeOrderParam orderParam) {
        this.orderParam = orderParam;
    }

    public DeputeEntrustOrder getEntrustOrder() {
        return entrustOrder;
    }

    public void setEntrustOrder(DeputeEntrustOrder entrustOrder) {
        this.entrustOrder = entrustOrder;
    }

    public DeputeEntrustData getEntrustData() {
        return entrustData;
    }

    public void setEntrustData(DeputeEntrustData entrustData) {
        this.entrustData = entrustData;
    }

    public List<DeputeEntrustStoreRelation> getEntrustStoreRelation() {
        return entrustStoreRelation;
    }

    public void setEntrustStoreRelation(List<DeputeEntrustStoreRelation> entrustStoreRelation) {
        this.entrustStoreRelation = entrustStoreRelation;
    }

    public List<EntrustAttachment> getEntrustAttachment() {
        return entrustAttachment;
    }

    public void setEntrustAttachment(List<EntrustAttachment> entrustAttachment) {
        this.entrustAttachment = entrustAttachment;
    }
}
