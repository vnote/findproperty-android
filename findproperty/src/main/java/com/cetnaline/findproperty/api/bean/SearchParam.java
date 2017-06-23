package com.cetnaline.findproperty.api.bean;

import java.io.Serializable;

/**
 * Created by fanxl2 on 2016/8/24.
 */
public class SearchParam implements Serializable {

	private String title; //条件的名称 比如

	private String name;  // Sell

	private String para;  //p1

	private String text; //条件选择的名称　比如 150万 - 300万

	private String value; //条件选择的值 1500000,30000000

	private String key; //条件存字典的 key  tag1 tag2

	private String paramKey; //接口请求的key MinSalePrice,MaxSalePrice

	private int id; //条件的Id

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPara() {
		return para;
	}

	public void setPara(String para) {
		this.para = para;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
		setParamKey(key);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getParamKey() {
		return paramKey;
	}

	public void setParamKey(String paramKey) {
		this.paramKey = paramKey;
	}
}
