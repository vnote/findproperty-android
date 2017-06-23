package com.cetnaline.findproperty.api.bean;

/**
 * Created by fanxl2 on 2016/8/12.
 */
public class SortBean {

	private String text;
	private String value;

	public SortBean(String text, String value) {
		this.text = text;
		this.value = value;
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
}
