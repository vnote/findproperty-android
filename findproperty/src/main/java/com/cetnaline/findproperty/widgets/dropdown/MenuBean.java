package com.cetnaline.findproperty.widgets.dropdown;

import java.util.List;

/**
 * Created by fanxl2 on 2016/8/5.
 */
public class MenuBean {

	public MenuBean(String title) {
		this.title = title;
	}

	private String title;

	private List<String> childs;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getChilds() {
		return childs;
	}

	public void setChilds(List<String> childs) {
		this.childs = childs;
	}
}
