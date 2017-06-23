package com.cetnaline.findproperty.api.bean;

/**
 * Created by fanxl2 on 2016/8/17.
 */
public class MyEntrustBo {


	/**
	 * JJR400 : 4008188808,906733
	 * Address : 庙泾路52弄
	 * EntrustID : 16663
	 * EntrustType : 出售
	 * RegionID : 0
	 * RegionName : 闵行
	 * GscpoeID : 0
	 * GscpoeName : 北莘庄
	 * EstateName : 驻马店大使馆
	 * CustomerMobile : 18256941011
	 * CustomerName : 常小哲
	 * IsDel : false
	 * EstateCode : PEKSWSWYBS
	 * CityCode : 021
	 * CreateTime : /Date(1473060888000+0800)/
	 * CreateTime2 : 1473060888
	 * Status : 待处理
	 * StaffNo : AA71505
	 * StaffName : 唐涛
	 * RoomCnt : 4
	 * PriceFrom : 15550000
	 * PriceTo : 15550000
	 * IsSendMsg : false
	 * IsRead : false
	 * IsReplacement : false
	 * Area : 0
	 * AllCount : 2
	 */

	private String JJR400;
	private String Address;
	private int EntrustID;
	private String EntrustType;
	private int RegionID;
	private String RegionName;
	private int GscpoeID;
	private String GscpoeName;
	private String EstateName;
	private String CustomerMobile;
	private String CustomerName;
	private boolean IsDel;
	private String EstateCode;
	private String CityCode;
	private String CreateTime;
	private long CreateTime2;
	private String Status;
	private int StatusID;
	private String StaffNo;
	private String StaffName;
	private int RoomCnt;//室
	private int ParlorCnt;//厅
	private int ToiletCnt;//卫
	private double PriceFrom;
	private double PriceTo;
	private boolean IsSendMsg;
	private boolean IsRead;
	private boolean IsReplacement;
	private double Area;
	private int AllCount;

	public String getJJR400() {
		return JJR400;
	}

	public void setJJR400(String JJR400) {
		this.JJR400 = JJR400;
	}

	public String getAddress() {
		return Address;
	}

	public void setAddress(String Address) {
		this.Address = Address;
	}

	public int getEntrustID() {
		return EntrustID;
	}

	public void setEntrustID(int EntrustID) {
		this.EntrustID = EntrustID;
	}

	public String getEntrustType() {
		return EntrustType;
	}

	public void setEntrustType(String EntrustType) {
		this.EntrustType = EntrustType;
	}

	public int getRegionID() {
		return RegionID;
	}

	public void setRegionID(int RegionID) {
		this.RegionID = RegionID;
	}

	public String getRegionName() {
		return RegionName;
	}

	public void setRegionName(String RegionName) {
		this.RegionName = RegionName;
	}

	public int getGscpoeID() {
		return GscpoeID;
	}

	public void setGscpoeID(int GscpoeID) {
		this.GscpoeID = GscpoeID;
	}

	public String getGscpoeName() {
		return GscpoeName;
	}

	public void setGscpoeName(String GscpoeName) {
		this.GscpoeName = GscpoeName;
	}

	public String getEstateName() {
		return EstateName;
	}

	public void setEstateName(String EstateName) {
		this.EstateName = EstateName;
	}

	public String getCustomerMobile() {
		return CustomerMobile;
	}

	public void setCustomerMobile(String CustomerMobile) {
		this.CustomerMobile = CustomerMobile;
	}

	public String getCustomerName() {
		return CustomerName;
	}

	public void setCustomerName(String CustomerName) {
		this.CustomerName = CustomerName;
	}

	public boolean isIsDel() {
		return IsDel;
	}

	public void setIsDel(boolean IsDel) {
		this.IsDel = IsDel;
	}

	public String getEstateCode() {
		return EstateCode;
	}

	public void setEstateCode(String EstateCode) {
		this.EstateCode = EstateCode;
	}

	public String getCityCode() {
		return CityCode;
	}

	public void setCityCode(String CityCode) {
		this.CityCode = CityCode;
	}

	public String getCreateTime() {
		return CreateTime;
	}

	public void setCreateTime(String CreateTime) {
		this.CreateTime = CreateTime;
	}

	public long getCreateTime2() {
		return CreateTime2;
	}

	public void setCreateTime2(long CreateTime2) {
		this.CreateTime2 = CreateTime2;
	}

	public String getStatus() {
		return Status;
	}

	public void setStatus(String Status) {
		this.Status = Status;
	}

	public String getStaffNo() {
		return StaffNo;
	}

	public void setStaffNo(String StaffNo) {
		this.StaffNo = StaffNo;
	}

	public String getStaffName() {
		return StaffName;
	}

	public void setStaffName(String StaffName) {
		this.StaffName = StaffName;
	}

	public int getRoomCnt() {
		return RoomCnt;
	}

	public void setRoomCnt(int RoomCnt) {
		this.RoomCnt = RoomCnt;
	}

	public double getPriceFrom() {
		return PriceFrom;
	}

	public void setPriceFrom(double PriceFrom) {
		this.PriceFrom = PriceFrom;
	}

	public double getPriceTo() {
		return PriceTo;
	}

	public void setPriceTo(double PriceTo) {
		this.PriceTo = PriceTo;
	}

	public boolean isIsSendMsg() {
		return IsSendMsg;
	}

	public void setIsSendMsg(boolean IsSendMsg) {
		this.IsSendMsg = IsSendMsg;
	}

	public boolean isIsRead() {
		return IsRead;
	}

	public void setIsRead(boolean IsRead) {
		this.IsRead = IsRead;
	}

	public boolean isIsReplacement() {
		return IsReplacement;
	}

	public void setIsReplacement(boolean IsReplacement) {
		this.IsReplacement = IsReplacement;
	}

	public double getArea() {
		return Area;
	}

	public void setArea(double Area) {
		this.Area = Area;
	}

	public int getAllCount() {
		return AllCount;
	}

	public void setAllCount(int AllCount) {
		this.AllCount = AllCount;
	}

	public int getParlorCnt() {
		return ParlorCnt;
	}

	public void setParlorCnt(int parlorCnt) {
		ParlorCnt = parlorCnt;
	}

	public int getToiletCnt() {
		return ToiletCnt;
	}

	public void setToiletCnt(int toiletCnt) {
		ToiletCnt = toiletCnt;
	}

	public int getStatusID() {
		return StatusID;
	}

	public void setStatusID(int statusID) {
		StatusID = statusID;
	}
}
