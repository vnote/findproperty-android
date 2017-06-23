package com.cetnaline.findproperty.entity.event;

/**
 * Created by fanxl2 on 2016/7/29.
 */
public class SlidingEvent {

	private boolean isToUp;

	private boolean isToBottom;

	private float scale;

	public boolean isToBottom() {
		return isToBottom;
	}

	public void setToBottom(boolean toBottom) {
		isToBottom = toBottom;
	}

	public boolean isToUp() {
		return isToUp;
	}

	public void setToUp(boolean toUp) {
		isToUp = toUp;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}
}
