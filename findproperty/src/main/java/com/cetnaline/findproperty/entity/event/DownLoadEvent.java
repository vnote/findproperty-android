package com.cetnaline.findproperty.entity.event;

/**
 * Created by fanxl2 on 2016/11/17.
 */

public class DownLoadEvent {

	private int progress;

	private int totalSize;

	public DownLoadEvent(int progress, int totalSize) {
		this.progress = progress;
		this.totalSize = totalSize;
	}

	public int getProgress() {
		return progress;
	}

	public int getTotalSize() {
		return totalSize;
	}
}
