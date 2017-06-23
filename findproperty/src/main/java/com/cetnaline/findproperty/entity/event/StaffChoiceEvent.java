package com.cetnaline.findproperty.entity.event;

/**
 * Created by fanxl2 on 2016/8/20.
 */
public class StaffChoiceEvent {

	private int position;

	public StaffChoiceEvent(int position) {
		this.position = position;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
}
