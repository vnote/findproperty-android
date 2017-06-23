package com.cetnaline.findproperty.entity.event;

/**
 * Created by fanxl2 on 2016/8/26.
 */
public class TopViewEvent {

	private boolean isIntercept;

	public TopViewEvent(boolean isIntercept) {
		this.isIntercept = isIntercept;
	}

	public boolean isIntercept() {
		return isIntercept;
	}

	public void setIntercept(boolean intercept) {
		isIntercept = intercept;
	}
}
