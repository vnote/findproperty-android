package com.cetnaline.findproperty.api.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by fanxl2 on 2016/8/21.
 */
public class ImageBean implements Parcelable {

	private String url;
	private String title;
	private String imageTitle;
	//当前图片在tag的位置
	private int index;
	//当前tag的总数量
	private int count;
	//tag的位置
	private int tagIndex;

	public ImageBean(String url, String title) {
		this.url = url;
		this.title = title;
		this.imageTitle=title;
	}

	public String getImageTitle() {
		return imageTitle;
	}

	public void setImageTitle(String imageTitle) {
		this.imageTitle = imageTitle;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getTagIndex() {
		return tagIndex;
	}

	public void setTagIndex(int tagIndex) {
		this.tagIndex = tagIndex;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeString(this.url);
		parcel.writeString(this.title);
		parcel.writeString(this.imageTitle);
	}

	protected ImageBean(Parcel in) {
		this.url = in.readString();
		this.title = in.readString();
		this.imageTitle = in.readString();
	}

	public static final Creator<ImageBean> CREATOR = new Creator<ImageBean>() {
		@Override
		public ImageBean createFromParcel(Parcel source) {
			return new ImageBean(source);
		}

		@Override
		public ImageBean[] newArray(int size) {
			return new ImageBean[size];
		}
	};
}
