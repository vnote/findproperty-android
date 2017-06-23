package com.cetnaline.findproperty.api.bean;

/**
 * Created by fanxl on 2016/8/10 0010.
 */
public class EstateImage {


	/**
	 * ImagePath : b0/6f/b09e9e2047089b0f9c8e92805dfb
	 * ImageDestExt : .jpg
	 * ImageTypeID : 3
	 * IsDefault : true
	 * ImageTitle :
	 * ImageTypeExt : 栋座外观
	 * ImageFullPath : http://img.sh.centanet.com/ctpostimage/b0/6f/b09e9e2047089b0f9c8e92805dfb_400x300_c.jpg
	 */

	private String ImagePath;
	private String ImageDestExt;
	private int ImageTypeID;
	private boolean IsDefault;
	private String ImageTitle;
	private String ImageTypeExt;
	private String ImageFullPath;

	public String getImagePath() {
		return ImagePath;
	}

	public void setImagePath(String ImagePath) {
		this.ImagePath = ImagePath;
	}

	public String getImageDestExt() {
		return ImageDestExt;
	}

	public void setImageDestExt(String ImageDestExt) {
		this.ImageDestExt = ImageDestExt;
	}

	public int getImageTypeID() {
		return ImageTypeID;
	}

	public void setImageTypeID(int ImageTypeID) {
		this.ImageTypeID = ImageTypeID;
	}

	public boolean isIsDefault() {
		return IsDefault;
	}

	public void setIsDefault(boolean IsDefault) {
		this.IsDefault = IsDefault;
	}

	public String getImageTitle() {
		return ImageTitle;
	}

	public void setImageTitle(String ImageTitle) {
		this.ImageTitle = ImageTitle;
	}

	public String getImageTypeExt() {
		return ImageTypeExt;
	}

	public void setImageTypeExt(String ImageTypeExt) {
		this.ImageTypeExt = ImageTypeExt;
	}

	public String getImageFullPath() {
		return ImageFullPath;
	}

	public void setImageFullPath(String ImageFullPath) {
		this.ImageFullPath = ImageFullPath;
	}
}
