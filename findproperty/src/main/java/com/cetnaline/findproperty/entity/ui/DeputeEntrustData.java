package com.cetnaline.findproperty.entity.ui;

import java.io.Serializable;

/**
 * Created by diaoqf on 2017/4/11.
 */

public class DeputeEntrustData implements Serializable {


    /**
     * houseId : 0
     * estateCode : String
     * estateId : 0
     * estateName : String
     * address : String
     * buildingId : 0
     * buildingName : String
     * doorNo : String
     * roomCnt : 0
     * parlorCnt : 0
     * toiletCnt : 0
     * square : 0
     * expectedPrice : 0
     * Lng : 0
     * Lat : 0
     * priceTo : 0
     */

    private int houseId;
    private String estateCode;
    private int estateId;
    private String estateName;
    private String address;
    private int buildingId;
    private String buildingName;
    private String doorNo;
    private int roomCnt;
    private int parlorCnt;
    private int toiletCnt;
    private double square;
    private double expectedPrice;
    private double Lng;
    private double Lat;
    private int priceTo;
    private String remark;
    private int Floor;

    public int getHouseId() {
        return houseId;
    }

    public void setHouseId(int houseId) {
        this.houseId = houseId;
    }

    public String getEstateCode() {
        return estateCode;
    }

    public void setEstateCode(String estateCode) {
        this.estateCode = estateCode;
    }

    public int getEstateId() {
        return estateId;
    }

    public void setEstateId(int estateId) {
        this.estateId = estateId;
    }

    public String getEstateName() {
        return estateName;
    }

    public void setEstateName(String estateName) {
        this.estateName = estateName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(int buildingId) {
        this.buildingId = buildingId;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getDoorNo() {
        return doorNo;
    }

    public void setDoorNo(String doorNo) {
        this.doorNo = doorNo;
    }

    public int getRoomCnt() {
        return roomCnt;
    }

    public void setRoomCnt(int roomCnt) {
        this.roomCnt = roomCnt;
    }

    public int getParlorCnt() {
        return parlorCnt;
    }

    public void setParlorCnt(int parlorCnt) {
        this.parlorCnt = parlorCnt;
    }

    public int getToiletCnt() {
        return toiletCnt;
    }

    public void setToiletCnt(int toiletCnt) {
        this.toiletCnt = toiletCnt;
    }

    public double getSquare() {
        return square;
    }

    public void setSquare(double square) {
        this.square = square;
    }

    public double getExpectedPrice() {
        return expectedPrice;
    }

    public void setExpectedPrice(double expectedPrice) {
        this.expectedPrice = expectedPrice;
    }

    public double getLng() {
        return Lng;
    }

    public void setLng(double Lng) {
        this.Lng = Lng;
    }

    public double getLat() {
        return Lat;
    }

    public void setLat(double Lat) {
        this.Lat = Lat;
    }

    public int getPriceTo() {
        return priceTo;
    }

    public void setPriceTo(int priceTo) {
        this.priceTo = priceTo;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getFloor() {
        return Floor;
    }

    public void setFloor(int floor) {
        Floor = floor;
    }
}
