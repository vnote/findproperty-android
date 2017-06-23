package com.cetnaline.findproperty.api.bean;

/**
 * Created by fanxl2 on 2016/8/21.
 */
public class ImageTags {

	private String text;

	private int index;

	private int size;

	public ImageTags(String text, int index, int size) {
		this.text = text;
		this.index = index;
		this.size=size;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

}
