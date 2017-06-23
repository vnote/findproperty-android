package com.cetnaline.findproperty.api.bean;

/**
 * Created by diaoqf on 2017/3/29.
 */

public class BuildingNumBean {

    private String BuildNUM;
    private int ID;
    private int Floor;
    private int TotalFloor;

    public String getBuildNUM() {
        return BuildNUM;
    }

    public void setBuildNUM(String buildNUM) {
        BuildNUM = buildNUM;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getFloor() {
        return Floor;
    }

    public void setFloor(int floor) {
        Floor = floor;
    }

    public int getTotalFloor() {
        return TotalFloor;
    }

    public void setTotalFloor(int totalFloor) {
        TotalFloor = totalFloor;
    }
}
