package com.cetnaline.findproperty.api.bean;

/**
 * Created by sunxl8 on 2016/8/22.
 */

public class SeoHotModelResponse {

    /**
     Id	    int
     Val	string	a标签文本
     Url	string	转向的URL
     Color	string	颜色
     */

    private int Id;

    private String Val;

    private String Url;

    private String Color;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getVal() {
        return Val;
    }

    public void setVal(String val) {
        Val = val;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String getColor() {
        return Color;
    }

    public void setColor(String color) {
        Color = color;
    }

    @Override
    public String toString() {
        return "SeoHotModelResponse{" +
                "Id=" + Id +
                ", Val='" + Val + '\'' +
                ", Url='" + Url + '\'' +
                ", Color='" + Color + '\'' +
                '}';
    }
}
