package com.cetnaline.findproperty.api.bean;

import java.io.Serializable;

/**
 * 新房 - 户型
 * Created by fanxl2 on 2016/8/12.
 */
public class ApartmentBo implements Serializable {


	/**
	 * HouseTypeId : 191
	 * EstId : 10157
	 * HouseTypeName : 方钻型8号楼03室
	 * MainHuxing : true
	 * Area : 159
	 * InsideArea : 0
	 * BuildType :
	 * Park : 0
	 * Fitment :
	 * RoomCnt : 4
	 * HallCnt : 2
	 * KitchenCnt : 1
	 * ToiletCnt : 3
	 * BalconyCnt : 0
	 * Description : 两梯两户设计，南北通透，通风绝佳
	 四房两卫三厅（南北双套间）布局，尽显大户风范
	 入户玄关宽敞，气派非凡，彰显恢宏大气
	 主厅空间诺大，功能完善
	 附设大面积飘窗，增加室内空间感及采光面
	 燃气入户，方便周全
	 */

	private int HouseTypeId;
	private String EstId;
	private String HouseTypeName;
	private boolean MainHuxing;
	private int Area;
	private int InsideArea;
	private String BuildType;
	private int Park;
	private String Fitment;
	private int RoomCnt;
	private int HallCnt;
	private int KitchenCnt;
	private int ToiletCnt;
	private int BalconyCnt;
	private String Description;
	private String imageUrl;
	private String thumbImage;

	public String getThumbImage() {
		return thumbImage;
	}

	public void setThumbImage(String thumbImage) {
		this.thumbImage = thumbImage;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public int getHouseTypeId() {
		return HouseTypeId;
	}

	public void setHouseTypeId(int HouseTypeId) {
		this.HouseTypeId = HouseTypeId;
	}

	public String getEstId() {
		return EstId;
	}

	public void setEstId(String EstId) {
		this.EstId = EstId;
	}

	public String getHouseTypeName() {
		return HouseTypeName;
	}

	public void setHouseTypeName(String HouseTypeName) {
		this.HouseTypeName = HouseTypeName;
	}

	public boolean isMainHuxing() {
		return MainHuxing;
	}

	public void setMainHuxing(boolean MainHuxing) {
		this.MainHuxing = MainHuxing;
	}

	public int getArea() {
		return Area;
	}

	public void setArea(int Area) {
		this.Area = Area;
	}

	public int getInsideArea() {
		return InsideArea;
	}

	public void setInsideArea(int InsideArea) {
		this.InsideArea = InsideArea;
	}

	public String getBuildType() {
		return BuildType;
	}

	public void setBuildType(String BuildType) {
		this.BuildType = BuildType;
	}

	public int getPark() {
		return Park;
	}

	public void setPark(int Park) {
		this.Park = Park;
	}

	public String getFitment() {
		return Fitment;
	}

	public void setFitment(String Fitment) {
		this.Fitment = Fitment;
	}

	public int getRoomCnt() {
		return RoomCnt;
	}

	public void setRoomCnt(int RoomCnt) {
		this.RoomCnt = RoomCnt;
	}

	public int getHallCnt() {
		return HallCnt;
	}

	public void setHallCnt(int HallCnt) {
		this.HallCnt = HallCnt;
	}

	public int getKitchenCnt() {
		return KitchenCnt;
	}

	public void setKitchenCnt(int KitchenCnt) {
		this.KitchenCnt = KitchenCnt;
	}

	public int getToiletCnt() {
		return ToiletCnt;
	}

	public void setToiletCnt(int ToiletCnt) {
		this.ToiletCnt = ToiletCnt;
	}

	public int getBalconyCnt() {
		return BalconyCnt;
	}

	public void setBalconyCnt(int BalconyCnt) {
		this.BalconyCnt = BalconyCnt;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String Description) {
		this.Description = Description;
	}
}
