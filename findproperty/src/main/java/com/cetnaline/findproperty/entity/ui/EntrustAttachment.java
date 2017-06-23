package com.cetnaline.findproperty.entity.ui;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * 委托附件实体
 * Created by diaoqf on 2017/4/7.
 */

public class EntrustAttachment implements Serializable {
    private String type;
    private String fileName;
    private String size;
    private String ext;
    private String content;

    private String path;   //选择的文件路径

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
