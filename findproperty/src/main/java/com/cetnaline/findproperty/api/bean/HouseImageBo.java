package com.cetnaline.findproperty.api.bean;

/**
 * 房源图片
 * Created by fanxl2 on 2016/8/13.
 */
public class HouseImageBo {


	/**
	 * RowId : 0
	 * PostId : 75315001-862b-4bab-bf2d-2fe1a67f8b07
	 * ImageId : cc8465c447aa4d1aa710a7ae20cee500
	 * ImagePath : http://img.sh.centanet.com/ctpostimage/cc/84/65c447aa4d1aa710a7ae20cee500_1440x960_c.jpg
	 * CustomImagePath : http://img.sh.centanet.com/ctpostimage/cc/84/65c447aa4d1aa710a7ae20cee500_100x100_c.jpg
	 * ImageDestExt : .jpg
	 * ImageClassId : 2
	 * ImageTitle : 客厅
	 * ImageDescription : 单元图
	 * ImageOrder : 1
	 * IsDefault : true
	 * FullImagePath : cc/84/65c447aa4d1aa710a7ae20cee500
	 */

	private int RowId;
	private String PostId;
	private String ImageId;
	private String ImagePath;
	private String CustomImagePath;
	private String ImageDestExt;
	private int ImageClassId;
	private String ImageTitle;
	private String ImageDescription;
	private int ImageOrder;
	private boolean IsDefault;
	private String FullImagePath;

	public int getRowId() {
		return RowId;
	}

	public void setRowId(int RowId) {
		this.RowId = RowId;
	}

	public String getPostId() {
		return PostId;
	}

	public void setPostId(String PostId) {
		this.PostId = PostId;
	}

	public String getImageId() {
		return ImageId;
	}

	public void setImageId(String ImageId) {
		this.ImageId = ImageId;
	}

	public String getImagePath() {
		return ImagePath;
	}

	public void setImagePath(String ImagePath) {
		this.ImagePath = ImagePath;
	}

	public String getCustomImagePath() {
		return CustomImagePath;
	}

	public void setCustomImagePath(String CustomImagePath) {
		this.CustomImagePath = CustomImagePath;
	}

	public String getImageDestExt() {
		return ImageDestExt;
	}

	public void setImageDestExt(String ImageDestExt) {
		this.ImageDestExt = ImageDestExt;
	}

	public int getImageClassId() {
		return ImageClassId;
	}

	public void setImageClassId(int ImageClassId) {
		this.ImageClassId = ImageClassId;
	}

	public String getImageTitle() {
		return ImageTitle;
	}

	public void setImageTitle(String ImageTitle) {
		this.ImageTitle = ImageTitle;
	}

	public String getImageDescription() {
		return ImageDescription;
	}

	public void setImageDescription(String ImageDescription) {
		this.ImageDescription = ImageDescription;
	}

	public int getImageOrder() {
		return ImageOrder;
	}

	public void setImageOrder(int ImageOrder) {
		this.ImageOrder = ImageOrder;
	}

	public boolean isIsDefault() {
		return IsDefault;
	}

	public void setIsDefault(boolean IsDefault) {
		this.IsDefault = IsDefault;
	}

	public String getFullImagePath() {
		return FullImagePath;
	}

	public void setFullImagePath(String FullImagePath) {
		this.FullImagePath = FullImagePath;
	}
}
