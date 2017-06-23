package com.cetnaline.findproperty.api.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by fanxl2 on 2016/8/11.
 */
public class StaffComment implements Parcelable, Serializable {

	/**
	 * PostId : 20f0675f-70bc-4bcd-9be9-0084ff313d3d
	 * Title : 明星楼盘！叠加别墅 准现房 免佣 享团购优惠
	 * StaffNo : AA71194
	 * PostDirection : 一手免佣推荐，浦东唐镇国际社区继绿城项目之后惟一在售别墅项目，小区低容积率，绿化率45%,纯法式建筑风格， 自带3000平会所（小区西面就是占地4万方的香梅和平御园，免费使用只对内部业主开放]上海老pai开发商金大元开发。Ď此房是290平米 4房3厅3卫 叠加别墅 1楼复2楼， 楼上带一个露台 送一个大面积阁楼，使用面积很大，空间利用率很大的，私密性也是非常好的，带一个大花园 。使用率很高的，法式宫廷别墅，中心位置，仅靠4万方湿地公园，仅对该小区业主使用!!Ď小区配套4万㎡香梅和平花园，仅对小区业主开放； 靠近地铁2号线“唐镇站”，出站步行15*即可到达； 紧邻外环线，交通便利，出行方便； 楼盘现周边生活配套完善，生活便利。附近高科东路规划43万㎡大型中高端商业，未来生活高端大气上档次。 小区人车分流，车位配比充足，配套4千㎡高端会所； 金大元集团开发楼盘在业界口碑相传，值得信耐Ď为什么要通过我们买一手房？跟我买一手房： 1：您买不贵！（房子是开发商的，统一开盘，明码标价） 2：您买不亏！（20余年行业代理经验，不食差价！） 3：您还能享受优惠！（500多家门店，12000多名员工，客户众多，开发商会对我们公司的客户给予一定的团购优惠，您自己上门就是“零售”，通过我们公司就相当于“批发”，有的开发商针对我公司客户还会推出特价房，是您自己上门或者其他公司无法享受到的）《我们分行主营华侨城十号院，我承诺如果有比我们更低折扣，我们公司双倍赔偿，一手无任何中介费用》 4：帮您解决资金问题。（我们公司可以帮您提供贷款产品，缓解您的燃眉之急。我们与众多的银行是战略合作伙伴关系，可以帮您申请到更多的优惠。） 5：让您的看房过程更顺畅，不用等待，省力省心。 6：您是我尊贵的客户，通过我，您可以直接了解非常多的项目，我可以推荐适合您的产品，让您找房看房更加精准！ 谢谢参观我的网店，别忘了来电咨询!小李 15000242809
	 * StaffName : 李懿
	 * StaffImage : http://img.sh.centanet.com/shanghai/staticfile/agent/agentphoto/aa71194.jpg
	 * StaffMobile : 15000242809
	 * Staff400Tel : 4008188808,904786
	 * GoodRate : -1
	 */

	private String PostId;
	private String Title;
	private String StaffNo;
	private String PostDirection;
	private String StaffName;
	private String StaffImage;
	private String StaffMobile;
	private String Staff400Tel;
	private String StoreName;
	private int GoodRate;

	private String CnName;
	private String Phone400;
	private int TotalLookCount;
	private int PostLookCount;

	private double StaffScore;
	private int PostCount;

	public String getPostId() {
		return PostId;
	}

	public void setPostId(String postId) {
		PostId = postId;
	}

	public String getTitle() {
		return Title;
	}

	public void setTitle(String title) {
		Title = title;
	}

	public String getStaffNo() {
		return StaffNo;
	}

	public void setStaffNo(String staffNo) {
		StaffNo = staffNo;
	}

	public String getPostDirection() {
		return PostDirection;
	}

	public void setPostDirection(String postDirection) {
		PostDirection = postDirection;
	}

	public String getStaffName() {
		return StaffName;
	}

	public void setStaffName(String staffName) {
		StaffName = staffName;
	}

	public String getStaffImage() {
		return StaffImage;
	}

	public void setStaffImage(String staffImage) {
		StaffImage = staffImage;
	}

	public String getStaffMobile() {
		return StaffMobile;
	}

	public void setStaffMobile(String staffMobile) {
		StaffMobile = staffMobile;
	}

	public String getStaff400Tel() {
		return Staff400Tel;
	}

	public void setStaff400Tel(String staff400Tel) {
		Staff400Tel = staff400Tel;
	}

	public String getStoreName() {
		return StoreName;
	}

	public void setStoreName(String storeName) {
		StoreName = storeName;
	}

	public int getGoodRate() {
		return GoodRate;
	}

	public void setGoodRate(int goodRate) {
		GoodRate = goodRate;
	}

	public String getCnName() {
		return CnName;
	}

	public void setCnName(String cnName) {
		CnName = cnName;
	}

	public String getPhone400() {
		return Phone400;
	}

	public void setPhone400(String phone400) {
		Phone400 = phone400;
	}

	public int getTotalLookCount() {
		return TotalLookCount;
	}

	public void setTotalLookCount(int totalLookCount) {
		TotalLookCount = totalLookCount;
	}

	public int getPostLookCount() {
		return PostLookCount;
	}

	public void setPostLookCount(int postLookCount) {
		PostLookCount = postLookCount;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.PostId);
		dest.writeString(this.Title);
		dest.writeString(this.StaffNo);
		dest.writeString(this.PostDirection);
		dest.writeString(this.StaffName);
		dest.writeString(this.StaffImage);
		dest.writeString(this.StaffMobile);
		dest.writeString(this.Staff400Tel);
		dest.writeString(this.StoreName);
		dest.writeInt(this.GoodRate);
		dest.writeString(this.CnName);
		dest.writeString(this.Phone400);
		dest.writeInt(this.TotalLookCount);
		dest.writeInt(this.PostLookCount);
	}

	public StaffComment() {
	}

	protected StaffComment(Parcel in) {
		this.PostId = in.readString();
		this.Title = in.readString();
		this.StaffNo = in.readString();
		this.PostDirection = in.readString();
		this.StaffName = in.readString();
		this.StaffImage = in.readString();
		this.StaffMobile = in.readString();
		this.Staff400Tel = in.readString();
		this.StoreName = in.readString();
		this.GoodRate = in.readInt();
		this.CnName = in.readString();
		this.Phone400 = in.readString();
		this.TotalLookCount = in.readInt();
		this.PostLookCount = in.readInt();
	}

	public static final Creator<StaffComment> CREATOR = new Creator<StaffComment>() {
		@Override
		public StaffComment createFromParcel(Parcel source) {
			return new StaffComment(source);
		}

		@Override
		public StaffComment[] newArray(int size) {
			return new StaffComment[size];
		}
	};
}
