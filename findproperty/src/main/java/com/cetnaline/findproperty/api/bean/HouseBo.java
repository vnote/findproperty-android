package com.cetnaline.findproperty.api.bean;

import java.io.Serializable;

/**
 * Created by fanxl2 on 2016/8/8.
 */
public class HouseBo implements Serializable {


	/**
	 * TitleHighLight : 嘉定核心地段，重点学区，诚心出售，看房方便
	 * DisplayAddress : 嘉定-嘉定新城 高台路353弄
	 * DisplayEstName : 保利家园(高台路353弄)二期
	 * PostId : fc590706-f641-421c-a046-4bd370aea537
	 * EstateCode : PEEEWKWSAS
	 * BigEstateCode : RUUUFSFSQO
	 * RegionId : 2178
	 * GScopeId : 217809
	 * EstateName : 保利家园(高台路353弄) 二期
	 * Address : 高台路353弄
	 * OpDate : 1325347200
	 * PropertyType : 普通住宅
	 * Floor : 4
	 * FloorTotal : 12
	 * FloorDisplay : 低层
	 * GArea : 88
	 * NArea : 0
	 * RoomCount : 2
	 * HallCount : 2
	 * ToiletCount : 0
	 * BalconyCount : 0
	 * KitchenCount : 0
	 * Direction : 南北
	 * Fitment : 中装
	 * PostType : S
	 * SalePrice : 3100000
	 * UnitSalePrice : 35227.2727272727
	 * RentPrice : 0
	 * Title : 嘉定核心地段，重点学区，诚心出售，看房方便
	 * KeyWords : 主卧朝南,客厅朝南,南北通透,地铁口,小区景观位置,婚房首选,房型正气
	 * DefaultImage : 75/8d/c15d98294fbca25c5dacf43ba488
	 * FullImagePath : 75/8d/c15d98294fbca25c5dacf43ba488
	 * DefaultImageExt : .jpg
	 * IsFollow : false
	 * IsSole : false
	 * StaffNo : aa92959
	 * IsOnline : false
	 * PostStatus : 0
	 * RotatedIn : true
	 * PostScore : 38.6
	 * AgentScore : 0
	 * ExpiredTime : 1473044860
	 * AdsNo : SHJD10024716
	 * EstateKeyWords : 地铁房,学区房,次新房,国际社区,高端物业,品牌开发商,投资回报率高
	 * PriceChange : 0
	 * ADM_Valid : 0
	 * IsAnyTimeSee : false
	 * IsTop : false
	 * IsHot : false
	 * IsManWu : false
	 * IsManEr : false
	 * IsOnly : false
	 * IsKeys : false
	 * IsMetro : false
	 * IsSchool : false
	 * IsManager : false
	 * IsRegion : false
	 * IsExclusive : false
	 * IsJiShou : false
	 * HitCount : 1
	 * TakeToSeeCount : 0
	 * ImageCount : 10
	 * IsDel : false
	 * CreateTime : 1470452746
	 * UpdateTime : 1470640440
	 * Label1 : false
	 * Label2 : false
	 * Label3 : false
	 * Label4 : false
	 * Label5 : false
	 * PaNo :
	 * Lng : 121.265803
	 * Lat : 31.359523
	 * EstateSimilarPostsCnt : 0
	 * RegionSimilarPostsCnt : 0
	 * TencentVistaUrl :
	 * RegionName : 嘉定
	 * GscopeName : 嘉定新城
	 * MatchSchoolsCount : 0
	 * IsHasDealData : false
	 */

	private String TitleHighLight;
	private String DisplayAddress;

	public double getGArea() {
		return GArea;
	}

	public void setGArea(double GArea) {
		this.GArea = GArea;
	}

	private String DisplayEstName;
	private String PostId;
	private String EstateCode;
	private String BigEstateCode;
	private int RegionId;
	private int GScopeId;
	private String EstateName;
	private String Address;
	private long OpDate;
	private String PropertyType;
	private int Floor;
	private int FloorTotal;
	private String FloorDisplay;
	private double GArea;
	private double NArea;
	private int RoomCount;
	private int HallCount;
	private int ToiletCount;
	private int BalconyCount;
	private int KitchenCount;
	private String Direction;
	private String Fitment;
	private String PostType;
	private double SalePrice;
	private double UnitSalePrice;
	private double RentPrice;
	private String Title;
	private String KeyWords;
	private String DefaultImage;
	private String FullImagePath;
	private String DefaultImageExt;
	private boolean IsFollow;
	private boolean IsSole;
	private String StaffNo;
	private boolean IsOnline;
	private int PostStatus;
	private boolean RotatedIn;
	private double PostScore;
	private double AgentScore;
	private long ExpiredTime;
	private String AdsNo;
	private String EstateKeyWords;
	private int PriceChange;
	private int ADM_Valid;
	private boolean IsAnyTimeSee;
	private boolean IsTop;
	private boolean IsHot;
	private boolean IsManWu;
	private boolean IsManEr;
	private boolean IsOnly;
	private boolean IsKeys;
	private boolean IsMetro;
	private boolean IsSchool;
	private boolean IsManager;
	private boolean IsRegion;
	private boolean IsExclusive;
	private boolean IsJiShou;
	private int HitCount;
	private int TakeToSeeCount;
	private int ImageCount;
	private boolean IsDel;
	private long CreateTime;
	private long UpdateTime;
	private boolean Label1;
	private boolean Label2;
	private boolean Label3;
	private boolean Label4;
	private boolean Label5;
	private String PaNo;
	private double Lng;
	private double Lat;
	private int EstateSimilarPostsCnt;
	private int RegionSimilarPostsCnt;
	private String TencentVistaUrl;
	private String RegionName;
	private String GscopeName;
	private int MatchSchoolsCount;
	private boolean IsHasDealData;
	private String RentType;
	private String StaffName;
	private String Staff400Tel;

	private String BaseInfo;
	private String PostFuture;
	private String NearTransportation;

	public String getStaffName() {
		return StaffName;
	}

	public void setStaffName(String staffName) {
		StaffName = staffName;
	}

	public String getStaff400Tel() {
		return Staff400Tel;
	}

	public void setStaff400Tel(String staff400Tel) {
		Staff400Tel = staff400Tel;
	}

	public String getTitleHighLight() {
		return TitleHighLight;
	}

	public void setTitleHighLight(String TitleHighLight) {
		this.TitleHighLight = TitleHighLight;
	}

	public String getDisplayAddress() {
		return DisplayAddress;
	}

	public void setDisplayAddress(String DisplayAddress) {
		this.DisplayAddress = DisplayAddress;
	}

	public String getDisplayEstName() {
		return DisplayEstName;
	}

	public void setDisplayEstName(String DisplayEstName) {
		this.DisplayEstName = DisplayEstName;
	}

	public String getPostId() {
		return PostId;
	}

	public void setPostId(String PostId) {
		this.PostId = PostId;
	}

	public String getEstateCode() {
		return EstateCode;
	}

	public void setEstateCode(String EstateCode) {
		this.EstateCode = EstateCode;
	}

	public String getBigEstateCode() {
		return BigEstateCode;
	}

	public void setBigEstateCode(String BigEstateCode) {
		this.BigEstateCode = BigEstateCode;
	}

	public int getRegionId() {
		return RegionId;
	}

	public void setRegionId(int RegionId) {
		this.RegionId = RegionId;
	}

	public int getGScopeId() {
		return GScopeId;
	}

	public void setGScopeId(int GScopeId) {
		this.GScopeId = GScopeId;
	}

	public String getEstateName() {
		return EstateName;
	}

	public void setEstateName(String EstateName) {
		this.EstateName = EstateName;
	}

	public String getAddress() {
		return Address;
	}

	public void setAddress(String Address) {
		this.Address = Address;
	}

	public long getOpDate() {
		return OpDate;
	}

	public void setOpDate(long OpDate) {
		this.OpDate = OpDate;
	}

	public String getPropertyType() {
		return PropertyType;
	}

	public void setPropertyType(String PropertyType) {
		this.PropertyType = PropertyType;
	}

	public int getFloor() {
		return Floor;
	}

	public void setFloor(int Floor) {
		this.Floor = Floor;
	}

	public int getFloorTotal() {
		return FloorTotal;
	}

	public void setFloorTotal(int FloorTotal) {
		this.FloorTotal = FloorTotal;
	}

	public String getFloorDisplay() {
		return FloorDisplay;
	}

	public void setFloorDisplay(String FloorDisplay) {
		this.FloorDisplay = FloorDisplay;
	}

	public double getNArea() {
		return NArea;
	}

	public void setNArea(double NArea) {
		this.NArea = NArea;
	}

	public int getRoomCount() {
		return RoomCount;
	}

	public void setRoomCount(int RoomCount) {
		this.RoomCount = RoomCount;
	}

	public int getHallCount() {
		return HallCount;
	}

	public void setHallCount(int HallCount) {
		this.HallCount = HallCount;
	}

	public int getToiletCount() {
		return ToiletCount;
	}

	public void setToiletCount(int ToiletCount) {
		this.ToiletCount = ToiletCount;
	}

	public int getBalconyCount() {
		return BalconyCount;
	}

	public void setBalconyCount(int BalconyCount) {
		this.BalconyCount = BalconyCount;
	}

	public int getKitchenCount() {
		return KitchenCount;
	}

	public void setKitchenCount(int KitchenCount) {
		this.KitchenCount = KitchenCount;
	}

	public String getDirection() {
		return Direction;
	}

	public void setDirection(String Direction) {
		this.Direction = Direction;
	}

	public String getFitment() {
		return Fitment;
	}

	public void setFitment(String Fitment) {
		this.Fitment = Fitment;
	}

	public String getPostType() {
		return PostType;
	}

	public void setPostType(String PostType) {
		this.PostType = PostType;
	}

	public double getSalePrice() {
		return SalePrice;
	}

	public void setSalePrice(double SalePrice) {
		this.SalePrice = SalePrice;
	}

	public double getUnitSalePrice() {
		return UnitSalePrice;
	}

	public void setUnitSalePrice(double UnitSalePrice) {
		this.UnitSalePrice = UnitSalePrice;
	}

	public double getRentPrice() {
		return RentPrice;
	}

	public void setRentPrice(double RentPrice) {
		this.RentPrice = RentPrice;
	}

	public String getTitle() {
		return Title;
	}

	public void setTitle(String Title) {
		this.Title = Title;
	}

	public String getKeyWords() {
		return KeyWords;
	}

	public void setKeyWords(String KeyWords) {
		this.KeyWords = KeyWords;
	}

	public String getDefaultImage() {
		return DefaultImage;
	}

	public void setDefaultImage(String DefaultImage) {
		this.DefaultImage = DefaultImage;
	}

	public String getFullImagePath() {
		return FullImagePath;
	}

	public void setFullImagePath(String FullImagePath) {
		this.FullImagePath = FullImagePath;
	}

	public String getDefaultImageExt() {
		return DefaultImageExt;
	}

	public void setDefaultImageExt(String DefaultImageExt) {
		this.DefaultImageExt = DefaultImageExt;
	}

	public boolean isIsFollow() {
		return IsFollow;
	}

	public void setIsFollow(boolean IsFollow) {
		this.IsFollow = IsFollow;
	}

	public boolean isIsSole() {
		return IsSole;
	}

	public void setIsSole(boolean IsSole) {
		this.IsSole = IsSole;
	}

	public String getStaffNo() {
		return StaffNo;
	}

	public void setStaffNo(String StaffNo) {
		this.StaffNo = StaffNo;
	}

	public boolean isIsOnline() {
		return IsOnline;
	}

	public void setIsOnline(boolean IsOnline) {
		this.IsOnline = IsOnline;
	}

	public int getPostStatus() {
		return PostStatus;
	}

	public void setPostStatus(int PostStatus) {
		this.PostStatus = PostStatus;
	}

	public boolean isRotatedIn() {
		return RotatedIn;
	}

	public void setRotatedIn(boolean RotatedIn) {
		this.RotatedIn = RotatedIn;
	}

	public double getPostScore() {
		return PostScore;
	}

	public void setPostScore(double PostScore) {
		this.PostScore = PostScore;
	}

	public double getAgentScore() {
		return AgentScore;
	}

	public void setAgentScore(int AgentScore) {
		this.AgentScore = AgentScore;
	}

	public long getExpiredTime() {
		return ExpiredTime;
	}

	public void setExpiredTime(long ExpiredTime) {
		this.ExpiredTime = ExpiredTime;
	}

	public String getAdsNo() {
		return AdsNo;
	}

	public void setAdsNo(String AdsNo) {
		this.AdsNo = AdsNo;
	}

	public String getEstateKeyWords() {
		return EstateKeyWords;
	}

	public void setEstateKeyWords(String EstateKeyWords) {
		this.EstateKeyWords = EstateKeyWords;
	}

	public int getPriceChange() {
		return PriceChange;
	}

	public void setPriceChange(int PriceChange) {
		this.PriceChange = PriceChange;
	}

	public int getADM_Valid() {
		return ADM_Valid;
	}

	public void setADM_Valid(int ADM_Valid) {
		this.ADM_Valid = ADM_Valid;
	}

	public boolean isIsAnyTimeSee() {
		return IsAnyTimeSee;
	}

	public void setIsAnyTimeSee(boolean IsAnyTimeSee) {
		this.IsAnyTimeSee = IsAnyTimeSee;
	}

	public boolean isIsTop() {
		return IsTop;
	}

	public void setIsTop(boolean IsTop) {
		this.IsTop = IsTop;
	}

	public boolean isIsHot() {
		return IsHot;
	}

	public void setIsHot(boolean IsHot) {
		this.IsHot = IsHot;
	}

	public boolean isIsManWu() {
		return IsManWu;
	}

	public void setIsManWu(boolean IsManWu) {
		this.IsManWu = IsManWu;
	}

	public boolean isIsManEr() {
		return IsManEr;
	}

	public void setIsManEr(boolean IsManEr) {
		this.IsManEr = IsManEr;
	}

	public boolean isIsOnly() {
		return IsOnly;
	}

	public void setIsOnly(boolean IsOnly) {
		this.IsOnly = IsOnly;
	}

	public boolean isIsKeys() {
		return IsKeys;
	}

	public void setIsKeys(boolean IsKeys) {
		this.IsKeys = IsKeys;
	}

	public boolean isIsMetro() {
		return IsMetro;
	}

	public void setIsMetro(boolean IsMetro) {
		this.IsMetro = IsMetro;
	}

	public boolean isIsSchool() {
		return IsSchool;
	}

	public void setIsSchool(boolean IsSchool) {
		this.IsSchool = IsSchool;
	}

	public boolean isIsManager() {
		return IsManager;
	}

	public void setIsManager(boolean IsManager) {
		this.IsManager = IsManager;
	}

	public boolean isIsRegion() {
		return IsRegion;
	}

	public void setIsRegion(boolean IsRegion) {
		this.IsRegion = IsRegion;
	}

	public boolean isIsExclusive() {
		return IsExclusive;
	}

	public void setIsExclusive(boolean IsExclusive) {
		this.IsExclusive = IsExclusive;
	}

	public boolean isIsJiShou() {
		return IsJiShou;
	}

	public void setIsJiShou(boolean IsJiShou) {
		this.IsJiShou = IsJiShou;
	}

	public int getHitCount() {
		return HitCount;
	}

	public void setHitCount(int HitCount) {
		this.HitCount = HitCount;
	}

	public int getTakeToSeeCount() {
		return TakeToSeeCount;
	}

	public void setTakeToSeeCount(int TakeToSeeCount) {
		this.TakeToSeeCount = TakeToSeeCount;
	}

	public int getImageCount() {
		return ImageCount;
	}

	public void setImageCount(int ImageCount) {
		this.ImageCount = ImageCount;
	}

	public boolean isIsDel() {
		return IsDel;
	}

	public void setIsDel(boolean IsDel) {
		this.IsDel = IsDel;
	}

	public long getCreateTime() {
		return CreateTime;
	}

	public void setCreateTime(long CreateTime) {
		this.CreateTime = CreateTime;
	}

	public long getUpdateTime() {
		return UpdateTime;
	}

	public void setUpdateTime(long UpdateTime) {
		this.UpdateTime = UpdateTime;
	}

	public boolean isLabel1() {
		return Label1;
	}

	public void setLabel1(boolean Label1) {
		this.Label1 = Label1;
	}

	public boolean isLabel2() {
		return Label2;
	}

	public void setLabel2(boolean Label2) {
		this.Label2 = Label2;
	}

	public boolean isLabel3() {
		return Label3;
	}

	public void setLabel3(boolean Label3) {
		this.Label3 = Label3;
	}

	public boolean isLabel4() {
		return Label4;
	}

	public void setLabel4(boolean Label4) {
		this.Label4 = Label4;
	}

	public boolean isLabel5() {
		return Label5;
	}

	public void setLabel5(boolean Label5) {
		this.Label5 = Label5;
	}

	public String getPaNo() {
		return PaNo;
	}

	public void setPaNo(String PaNo) {
		this.PaNo = PaNo;
	}

	public double getLng() {
		return Lng;
	}

	public void setLng(double Lng) {
		this.Lng = Lng;
	}

	public double getLat() {
		return Lat;
	}

	public void setLat(double Lat) {
		this.Lat = Lat;
	}

	public int getEstateSimilarPostsCnt() {
		return EstateSimilarPostsCnt;
	}

	public void setEstateSimilarPostsCnt(int EstateSimilarPostsCnt) {
		this.EstateSimilarPostsCnt = EstateSimilarPostsCnt;
	}

	public int getRegionSimilarPostsCnt() {
		return RegionSimilarPostsCnt;
	}

	public void setRegionSimilarPostsCnt(int RegionSimilarPostsCnt) {
		this.RegionSimilarPostsCnt = RegionSimilarPostsCnt;
	}

	public String getTencentVistaUrl() {
		return TencentVistaUrl;
	}

	public void setTencentVistaUrl(String TencentVistaUrl) {
		this.TencentVistaUrl = TencentVistaUrl;
	}

	public String getRegionName() {
		return RegionName;
	}

	public void setRegionName(String RegionName) {
		this.RegionName = RegionName;
	}

	public String getGscopeName() {
		return GscopeName;
	}

	public void setGscopeName(String GscopeName) {
		this.GscopeName = GscopeName;
	}

	public int getMatchSchoolsCount() {
		return MatchSchoolsCount;
	}

	public void setMatchSchoolsCount(int MatchSchoolsCount) {
		this.MatchSchoolsCount = MatchSchoolsCount;
	}

	public boolean isIsHasDealData() {
		return IsHasDealData;
	}

	public void setIsHasDealData(boolean IsHasDealData) {
		this.IsHasDealData = IsHasDealData;
	}

	public String getRentType() {
		return RentType;
	}

	public void setRentType(String rentType) {
		RentType = rentType;
	}

	public String getBaseInfo() {
		return BaseInfo;
	}

	public void setBaseInfo(String baseInfo) {
		BaseInfo = baseInfo;
	}

	public String getPostFuture() {
		return PostFuture;
	}

	public void setPostFuture(String postFuture) {
		PostFuture = postFuture;
	}

	public String getNearTransportation() {
		return NearTransportation;
	}

	public void setNearTransportation(String nearTransportation) {
		NearTransportation = nearTransportation;
	}
}
