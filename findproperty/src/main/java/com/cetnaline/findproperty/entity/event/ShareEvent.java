package com.cetnaline.findproperty.entity.event;

/**
 * 分享事件
 * Created by fanxl2 on 2016/8/18.
 */
public class ShareEvent {

	private int eventType;

	public static final int EVENT_TYPE_SUCCESS = 0;
	public static final int EVENT_TYPE_CANCLE = 1;
	public static final int EVENT_TYPE_FAIL = 2;

	public ShareEvent(int eventType) {
		this.eventType = eventType;
	}

	public int getEventType() {
		return eventType;
	}

	public void setEventType(int eventType) {
		this.eventType = eventType;
	}
}
