package com.cetnaline.findproperty.api.service;

import com.cetnaline.findproperty.api.bean.AdvertBo;
import com.cetnaline.findproperty.api.bean.ApartmentBo;
import com.cetnaline.findproperty.api.bean.ApiResponse;
import com.cetnaline.findproperty.api.bean.AppAdBo;
import com.cetnaline.findproperty.api.bean.AppUpdateBo;
import com.cetnaline.findproperty.api.bean.BatchCollectRequest;
import com.cetnaline.findproperty.api.bean.BuildingNumBean;
import com.cetnaline.findproperty.api.bean.DeputeBean;
import com.cetnaline.findproperty.api.bean.Discount;
import com.cetnaline.findproperty.api.bean.EstateBo;
import com.cetnaline.findproperty.api.bean.EstateDealPriceBo;
import com.cetnaline.findproperty.api.bean.EstateMapRequest;
import com.cetnaline.findproperty.api.bean.ExerciseListBo;
import com.cetnaline.findproperty.api.bean.GscopeDealPriceBo;
import com.cetnaline.findproperty.api.bean.HouseBo;
import com.cetnaline.findproperty.api.bean.HouseDetailBo;
import com.cetnaline.findproperty.api.bean.HouseImageBo;
import com.cetnaline.findproperty.api.bean.ImageUploadBean;
import com.cetnaline.findproperty.api.bean.InsertIntentionsRequest;
import com.cetnaline.findproperty.api.bean.IntentionBo;
import com.cetnaline.findproperty.api.bean.LookAboutBean;
import com.cetnaline.findproperty.api.bean.LookAboutNumBo;
import com.cetnaline.findproperty.api.bean.LookListDeleteRequest;
import com.cetnaline.findproperty.api.bean.MyEntrustBo;
import com.cetnaline.findproperty.api.bean.NewHouseDetail;
import com.cetnaline.findproperty.api.bean.NewHouseImageBo;
import com.cetnaline.findproperty.api.bean.NewHouseListBo;
import com.cetnaline.findproperty.api.bean.NewHouseMapDetail;
import com.cetnaline.findproperty.api.bean.NewStaffBo;
import com.cetnaline.findproperty.api.bean.RegionPostBo;
import com.cetnaline.findproperty.api.bean.SchoolBo;
import com.cetnaline.findproperty.api.bean.SearchData;
import com.cetnaline.findproperty.api.bean.SendAppointmentRequest;
import com.cetnaline.findproperty.api.bean.SeoHotModelResponse;
import com.cetnaline.findproperty.api.bean.StaffComment;
import com.cetnaline.findproperty.api.bean.StaffDetailBo;
import com.cetnaline.findproperty.api.bean.StoreBo;
import com.cetnaline.findproperty.api.bean.SubscribeBean;
import com.cetnaline.findproperty.api.bean.TagModelResponse;
import com.cetnaline.findproperty.db.entity.RailLine;
import com.cetnaline.findproperty.entity.bean.BookingBean;
import com.cetnaline.findproperty.entity.bean.CmBaseSingleResult;
import com.cetnaline.findproperty.entity.bean.CmListStaffBean;
import com.cetnaline.findproperty.entity.bean.CollectInfoChangeBean;
import com.cetnaline.findproperty.entity.bean.CollectionBean;
import com.cetnaline.findproperty.entity.bean.ConsultFormBean;
import com.cetnaline.findproperty.entity.bean.GScopeBean;
import com.cetnaline.findproperty.entity.bean.QQUserBean;
import com.cetnaline.findproperty.entity.bean.SinaUserInfoBean;
import com.cetnaline.findproperty.entity.bean.StaffListBean;
import com.cetnaline.findproperty.entity.bean.SystemMessageBean;
import com.cetnaline.findproperty.entity.bean.UserImageBean;
import com.cetnaline.findproperty.entity.bean.UserInfoBean;
import com.cetnaline.findproperty.entity.bean.WXTokenBean;
import com.cetnaline.findproperty.entity.bean.WxUserBean;
import com.cetnaline.findproperty.entity.result.BaseLoginResult;
import com.cetnaline.findproperty.entity.result.BaseResult;
import com.cetnaline.findproperty.entity.result.BaseSingleResult;
import com.cetnaline.findproperty.entity.result.CmBaseResult;
import com.cetnaline.findproperty.entity.ui.DeputePushBean;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by fanxl2 on 2016/7/25.
 */
public interface RegionPostsApi {

	// TODO: 2017/3/1 流量活动接口

	/**
	 * 分享回调
	 * @param id
	 * @return
	 */
	//@Url(NetContents.BASE_HOST)
	@GET("UpdateShareStateRequest")
	Observable<ApiResponse<Boolean>> updateShareStateRequest(@Query("WinningRecordID") String id);

	@GET("GetAppXuanFuAdvertRequest")
	Observable<ApiResponse<AdvertBo>> getAppXuanFuAdvertRequest();


	//地图 获取区域和板块地图数据
	@GET("GetGScopeStatisticsRequest")
	Observable<ApiResponse<List<RegionPostBo>>> getBaseHouseData(@QueryMap Map<String, String> params);

	//地图 获取板块下小区数据
//	@FormUrlEncoded
//	@POST("GetGeoEstatesRequest")
//	Observable<ApiResponse<List<EstateBo>>> getEsfEstateList(@FieldMap Map<String, String> params);

	//地图 获取板块下的小区
	@Headers({ "Content-Type: application/json;charset=UTF-8"})
	@POST("GetEstatePostAggRequest")
	Observable<ApiResponse<List<EstateBo>>> getEsf4Map(@Body EstateMapRequest request);


	//地图 /房源列表 获取小区下房源数据
	@FormUrlEncoded
	@POST("GetRegionPostsRequest")
	Observable<ApiResponse<List<HouseBo>>> getHouseList(@FieldMap Map<String, String> params);

	//房源列表 根据学校找房
	@GET("GetSchoolPostsRequest")
	Observable<ApiResponse<List<HouseBo>>> getHouseBySchool(@QueryMap(encoded = true) Map<String, String> params);

	//房源列表 根据地铁找房
	@GET("GetRailWayPostsRequest")
	Observable<ApiResponse<List<HouseBo>>> getHouseByMetro(@QueryMap(encoded = true) Map<String, String> params);

	@GET("GetStaffPostsRequest")
	Observable<ApiResponse<List<HouseBo>>> getStaffHouseList(@QueryMap(encoded = true) Map<String, String> params);

	/**
	 * 获取顾问列表（v2）
	 * @param params
	 * @return
	 */
	@GET("Staff")
	Observable<CmBaseResult<CmListStaffBean>> getStaffList(@QueryMap(encoded = true) Map<String, String> params);

	/**
	 * 获取顾问详情（v2）
	 * @param staffNo
	 * @return
	 */
	@GET("Staff")
	Observable<CmBaseSingleResult<CmListStaffBean>> getStaffByNo(@Query("staffNo") String staffNo);

	/**
	 * 发送消息到服务器
	 * @param params
	 * @return
	 */
	@FormUrlEncoded
	@POST("MessageRecordRequest")
	Observable<ApiResponse> messageRecord(@FieldMap Map<String, String> params);

	/**
	 * 获取聊天系统消息
	 * @return
     */
//	@GET("GetliaotianMsgRequest")
//	Observable<ApiResponse<SystemMsgBean>> getLiaoTianSysMsg();

	//优惠 优惠列表
	@GET("PreferentialRequest")
	Observable<ApiResponse<List<Discount>>> getDiscountList(@QueryMap() Map<String, String> params);

	//优惠 优惠头部菜单
	@GET("GetPreferentialMenuRequest")
	Observable<ApiResponse<String>> getDiscountMenu(@Query("groupType") String groupType);

	//地图 画圈找房
	@FormUrlEncoded
	@POST("GetGeoEstatesRequest")
	Observable<ApiResponse<List<EstateBo>>> getEstByCircle(@FieldMap Map<String, String> params);

	/**
	 * 查询用户订阅
	 * @param params
	 * @return
	 */
	@GET("IntentionRequest")
	Observable<ApiResponse<List<SubscribeBean>>> getSubscribeList(@QueryMap(encoded = true) Map<String, String> params);

	/**
	 * 删除订阅
	 * @param id
	 * @return
	 */
	@FormUrlEncoded
	@POST("DeleteIntentionRequest")
	Observable<ApiResponse<Boolean>> deleteSubscribe(@Field("IntentionID") long id);


	/**
	 * 获取用户参与的活动
	 * @param params
	 * @return
	 */
	@GET("UserBookingListRequest")
	Observable<BaseResult<BookingBean>> getUserBookingList(@QueryMap(encoded = true) Map<String, String> params);

	/**
	 * 删除用户活动
	 * @param id
	 * @return
	 */
	@FormUrlEncoded
	@POST("DelUserBookingRequest")
	Observable<ApiResponse<Integer>> delUserBooking(@Field("BookingId") String id);

	/**
	 * 提交用户反馈
	 * @param params
	 * @return
	 */
	@FormUrlEncoded
	@POST("InsertUserFeedBackRequest")
	Observable<ApiResponse<Integer>> InsertUserFeedBack(@FieldMap(encoded = true) Map<String, String> params);

	//	@FormUrlEncoded
	@Headers({ "Content-Type: application/json;charset=UTF-8"})
	@POST("FeedBackConsultMessageUpdateRequest")
	Observable<ApiResponse<Integer>> FeedBackConsultMessage(@Body ConsultFormBean params);
//	Observable<ApiResponse<Integer>> FeedBackConsultMessage(@FieldMap(encoded = true)Map<String,String> params);

	@FormUrlEncoded
	@POST("WordFrequencyRequest")
	Observable<ApiResponse<Integer>> wordFrequency(@FieldMap(encoded = true) Map<String, String> params);

	/**
	 * 以下是8月5号新接口
	 */

	/**
	 * 获取区域、板块信息
	 * @return
	 */
	@GET("GetAllGScopeRequest")
	Observable<ApiResponse<List<GScopeBean>>> getGScope();

	/**
	 * 获取经纪人列表
	 * @param params
	 * @return
	 */
	@GET("SearchStaffRequest")
	Observable<BaseResult<StaffListBean>> getStaffs(@QueryMap(encoded = true) Map<String, Object> params);

	/**
	 * 获取经济人详情
	 * @param staffNo
	 * @return
	 */
	@GET("StaffRequest")
	Observable<ApiResponse<StaffListBean>> getStaffDetail(@Query("StaffNo") String staffNo);

	//搜索 菜单相关数据
	@GET("SearchDataRequest")
	Observable<ApiResponse<List<SearchData>>> getSearchData();

	//获取学校数据
	@GET("GetSchoolInfosRequest?PageIndex=1&PageCount=1000")
	Observable<ApiResponse<List<SchoolBo>>> getSchoolList(@Query("regionId") String regionId);

	//获取地铁数据
	@GET("GetRailLinesRequest")
	Observable<ApiResponse<List<RailLine>>> getRailLines();

	//地铁找房
	@GET("GetRailWayEstatesRequest")
	Observable<ApiResponse<List<EstateBo>>> getRailWayHouse(@QueryMap(encoded = true) Map<String, String> params);

	//新房 - 基础地图数据
//	@GET("DistrictEstRequest")
//	Observable<ApiResponse<List<NewHousePropBo>>> getNewHouseBaseMapData(@QueryMap(encoded = true) Map<String, String> params);

	//新房 — 最终楼盘数据
	@GET("NewPropExtInfoMapSearchRequest?startIndex=0&length=1000")
	Observable<ApiResponse<List<NewHouseMapDetail>>> getNewHouseList(@QueryMap(encoded = true) Map<String, String> params);

	//新房 -- 列表数据
	@GET("NewPropExtInfoSearchRequest")
	Observable<ApiResponse<List<NewHouseListBo>>> getNewHouses(@QueryMap(encoded = true) Map<String, String> params);

	//新房 --
	@GET("NewPropStaffRequest?Role=1")
	Observable<ApiResponse<List<NewStaffBo>>> getNewHouseStaff(@Query("EstExtId") String estExtId);

	//房源详情   二手房和租房
	@GET("GetPostRequest")
	Observable<ApiResponse<HouseDetailBo>> getHouseDetailData(@QueryMap(encoded = true) Map<String, String> params);

	//房源详情  房源图片
	@GET("GetPostImagesRequest")
	Observable<ApiResponse<List<HouseImageBo>>> getHouseImageList(@Query("PostId") String postId, @Query("ImageWidth") int imgW, @Query("ImageHeight") int imgH);

	//小区详情
	@GET("GetEstateRequest")
	Observable<ApiResponse<EstateBo>> getEstateByCode(@QueryMap(encoded = true) Map<String, String> params);

	//房源详情 全部描述
	@GET("GetRecommendStaffRequest")
	Observable<ApiResponse<List<StaffComment>>> getStaffComments(@Query("AdsNo") String AdsNo, @Query("StaffNo") String StaffNo);

	@GET("ReserveStaffRequest")
	Observable<ApiResponse<List<StaffComment>>> getPostStaffs(@Query("EstateCodes") String EstateCodes, @Query("PostIds") String PostIds, @Query("TopCount") String TopCount ,@Query("SearchReserveStaffModel") String SearchReserveStaffModel);

	//新房 -- 楼盘详情
	@GET("NewPropExtInfoRequest")
	Observable<ApiResponse<NewHouseDetail>> getNewHouseDetail(@Query("EstExtId") String estExtId);

	//二手房 租房详情 周边相似房源
	@GET("GetGeologyPostsRequest")
	Observable<ApiResponse<List<HouseBo>>> getNearbyHouse(@QueryMap(encoded = true) Map<String, String> params);

	//地图 —— 学校找房
	@GET("GetSchoolInfoRequest")
	Observable<ApiResponse<SchoolBo>> getEstBySchoolId(@Query("SchoolId") String schoolId);

	//新房 -- 在售户型
	@GET("HouseTypesRequest")
	Observable<ApiResponse<List<ApartmentBo>>> getApartments(@Query("EstExtId") String estExtId);

	//新房  —— 活动列表
	@GET("BookingActivitySearchRequest?StartIndex=0&Length=1000")
	Observable<ApiResponse<List<ExerciseListBo>>> getExerciseList(@Query("EstExtId") String estExtId);

	//依据活动id列表获取
	@GET("BookingActivitySearchRequest?StartIndex=0&Length=1000&UsedActIds=1")
	Observable<ApiResponse<List<ExerciseListBo>>> getExerciseListByIds(@Query("ActIds") String ids);


	//新房  -- 活动详情
	@GET("BookingActivityRequest?actId=14")
	Observable<ApiResponse<ExerciseListBo>> getExercise();

	//新房 -- 活动报名
	@FormUrlEncoded
	@POST("InsertUserBookingRequest")
	Observable<ApiResponse<Integer>> insertUserBooking(@FieldMap Map<String, String> param);

	//新房 -- 活动数量加1
	@FormUrlEncoded
	@POST("UserBookingRequest")
	Observable<ApiResponse<Integer>> addBookingCount(@Field("ActId") String actId, @Field("BookingUsersCount") int number);

	//新房图片  -- 在售户型 和新房楼盘的
	@GET("NewPropImgRequest")
	Observable<ApiResponse<List<NewHouseImageBo>>> getNewHouseImages(@Query("appvalue") String appvalue);

	//区域板块 价格成交走势
	@GET("GetGScopeDealAvgPriceHistoryRequest")
	Observable<ApiResponse<List<GscopeDealPriceBo>>> getGscopeDeailPrice(@QueryMap(encoded = true) Map<String, String> params);

	//小区 价格成交走势
	@GET("GetEstateDealAvgPriceHistoryRequest")
	Observable<ApiResponse<List<EstateDealPriceBo>>> getEstateDeailPrice(@QueryMap(encoded = true) Map<String, String> params);

	//委托 新增委托
//	@FormUrlEncoded
	@Headers({ "Content-Type: application/json;charset=UTF-8"})
	@POST("EntrustRequest")
	Observable<ApiResponse<Integer>> insertEntrustInfo(@Body DeputePushBean bean);

	//委托  我的委托
	@GET("EntrustInfoListV3Request?CityCode=021&IsDel=false")
	Observable<ApiResponse<List<MyEntrustBo>>> getMyEntrustList(@QueryMap(encoded = true) Map<String,String> params);

	//委托  获取某个委托
	@GET("EntrustInfoListV3Request?CityCode=021")
	Observable<ApiResponse<List<MyEntrustBo>>> getMyEntrustById(@Query("UserId") String userId, @Query("EntrustID") String entrustId);

	//委托 删除委托
//	@FormUrlEncoded
//	@POST("DelEntrustInfoRequest")
//	Observable<ApiResponse<Integer>> delEntrust(@Field("EntrustID") long entrustId);

	//委托状态更新
	@FormUrlEncoded
	@POST("UpdateEntrustStutasV3Request")
	Observable<ApiResponse<Integer>> updateEntrustStutasRequest(@FieldMap Map<String, String> params);


	//获取小区的默认经纪人
	@GET("GetSeniorStaffToEstateRequest")
	Observable<ApiResponse<StaffDetailBo>> getEstateStaffByEstateCode(@Query("EstateCode") String estateCode);

	//收藏
	@FormUrlEncoded
	@POST("InsertCollectInfoRequest")
	Observable<ApiResponse<Long>> insertCollect(@FieldMap Map<String, String> params);

	/**
	 * 删除 收藏
	 */
	@FormUrlEncoded
	@POST("UpdateCollectInfoStatusRequest")
	Observable<ApiResponse<Integer>> deleteCollect(@Field("CollectID") long collectId, @Field("IsDel") boolean IsDel);

	//收藏  是否收藏
	@FormUrlEncoded
	@POST("CheckPostIsCollectRequest")
	Observable<ApiResponse<Long>> checkCollect(@Field("UserId") String userId, @Field("PostId") String postId);

	//收藏 批量收藏
	@Headers({ "Content-Type: application/json;charset=UTF-8"})
	@POST("InsertCollectInfoRequestBatch")
	Observable<ApiResponse<Integer>> insertCollectBatch(@Body BatchCollectRequest request);

	//约看 添加约看
	@FormUrlEncoded
	@POST("InsertLookListPlanRequest")
	Observable<ApiResponse<Integer>> insertReservation(@FieldMap Map<String, String> params);

	//约看 约看单 待约看 约看记录
	@GET("OrderLookPlanListRequest")
	Observable<ApiResponse<List<LookAboutBean>>> getLookAboutList(@Query("UserId") String userId,
																  @Query("Status") int status,
																  @Query("pageindex") int pageindex,
																  @Query("pagecount") int pagecount);

	//约看 删除约看单
	@Headers({ "Content-Type: application/json;charset=UTF-8"})
	@POST("DeleteLookListRequest")
	Observable<ApiResponse<Integer>> deleteLookAboutList(@Body LookListDeleteRequest request);

	//约看 已经约看的数量
	@GET("OrderLookPlanCountRequest")
	Observable<ApiResponse<LookAboutNumBo>> getLookedAboutNumber(@Query("UserId") String userId, @Query("Status") int status);

	//约看 提交约看单
	@Headers({ "Content-Type: application/json;charset=UTF-8"})
	@POST("SendAppointmentRequest")
	Observable<ApiResponse<Integer>> commitLookAboutList(@Body SendAppointmentRequest request);

	//约看 约看服务评价
	@FormUrlEncoded
	@POST("OrderLookForCommentRequest")
	Observable<ApiResponse<Integer>> lookAboutComment(@FieldMap Map<String, String> param);

	//App检查更新

	@GET("GetAppVersionRequest")
	Observable<ApiResponse<AppUpdateBo>> getAppVersion();

	//启动广告
	@GET("GetAppAdvertRequest")
	Observable<ApiResponse<AppAdBo>> getAppAdvert();

	//意向 保存意向
	@Headers({ "Content-Type: application/json;charset=UTF-8"})
	@POST("InsertIntentionsRequest")
	Observable<ApiResponse<Long>> insertIntent(@Body InsertIntentionsRequest request);

	//意向 删除意向
	@FormUrlEncoded
	@POST("DeleteIntentionRequest")
	Observable<ApiResponse<Boolean>> deleteIntent(@Field("IntentionID") long intentionId);

	//意向 获取意向
	@GET("GetIntentionForHomeRequest?CityCode=021")
	Observable<ApiResponse<List<IntentionBo>>> getIntentionList(@QueryMap(encoded = true) Map<String, String> params);

	//意向 首页我的意向
	@GET("GetIntentionForHomeRequest?CityCode=021&PageCount=1")
	Observable<ApiResponse<List<IntentionBo>>> getIntention4Home(@Query("userId") String userId);

	//首页 默认推荐
	@GET("GetGeoPostsRequest?Round=3000")
	Observable<ApiResponse<List<HouseBo>>> getHomeRecommend(@Query("Lat") double lat, @Query("lng") double lng);

	//获取系统消息列表
	@FormUrlEncoded
	@POST("SystemMessageRequestV3")
	Observable<ApiResponse<List<SystemMessageBean>>> getSystemMessageList(@FieldMap Map<String, String> params);
	//更新系统消息已读状态
	@FormUrlEncoded
	@POST("UpdatestemMessageIsReadRequestV3")
	Observable<ApiResponse<Integer>> updateSystemMessage(@Field("SystemMessageID") String id);
	//删除系统消息
	@FormUrlEncoded
	@POST("DeleteSystemMessageRequestV3")
	Observable<ApiResponse<Integer>> delSystemMessage(@Field("SystemMessageID") String id);

	/**
	 * 刷新已读消息
	 * @param params
	 * @return
	 */
	@FormUrlEncoded
	@POST("UpdateCollectInfoChangeIsReadByCollectIDRequest")
	Observable<BaseSingleResult<Integer>> updateCollectInfoChangeIsRead(@FieldMap Map<String, String> params);

	/**
	 * 检测手机号是否已经绑定
	 * @return
	 */
	@FormUrlEncoded
	@POST("CheckPhoneIsBindRequest")
	Observable<ApiResponse<String>> checkPhoneIsBind(@FieldMap Map<String, String> params);

	/**
	 * 获取我的消息更新列表
	 * @param params
	 * @return
	 */
	@GET("CollectInfoChangeListRequest")
	Observable<BaseResult<CollectInfoChangeBean>> getCollectInfoChangeList(@QueryMap(encoded = true) Map<String, String> params);

	/**
	 * 获取指定房源列表
	 * @param params
	 * @return
	 */
	@GET("GetMultiplePostRequest")
	Observable<ApiResponse<List<HouseBo>>> getMultiplePost(@QueryMap(encoded = true) Map<String, String> params);

	/**
	 * 获取收藏列表
	 * @param params
	 * @return
	 */
	@GET("CollectInfoListRequest")
	Observable<BaseResult<CollectionBean>> getCollectList(@QueryMap(encoded = true) Map<String, String> params);

	/**
	 * 依据ids获取新房列表
	 * @param params
	 * @return
	 */
	@GET("NewPropExtInfoSearchRequest")
	Observable<ApiResponse<List<NewHouseListBo>>> getNewHouseByIds(@QueryMap(encoded = true) Map<String, String> params);

	/**
	 * 依据小区id获取小区列表
	 */
	@GET("GetEstatesByCodeListRequest")
	Observable<ApiResponse<List<EstateBo>>> getEstatesByCodeList(@QueryMap(encoded = true) Map<String, String> params);

	/**
	 * 删除收藏更新提示信息
	 * @param id
	 * @return
	 */
	@FormUrlEncoded
	@POST("DelCollectInfoChangeRequest ")
	Observable<ApiResponse<Integer>> delCollectInfoChange(@Field("ID") String id);

	/**
	 * 门店搜索 (第一版)
	 * @param params
	 * @return
	 */
	@GET("SearchStoreRequest")
	Observable<ApiResponse<List<StoreBo>>> searchStore(@QueryMap(encoded = true) Map<String, String> params);

	/**
	 * 门店搜索 (第二版)
	 * @param params
	 * @return
	 */
	@GET("SearchStoreSingleRequest")
	Observable<ApiResponse<List<StoreBo>>> searchStoreSingle(@QueryMap(encoded = true) Map<String, String> params);

	/**
	 * 房源门店搜索 (第一版)
	 * @param params
	 * @return
	 */
	@GET("GetStoreToEstatesForApp")
	Observable<ApiResponse<List<StoreBo>>> getStoreToEstates(@QueryMap(encoded = true) Map<String, String> params);

	/**
	 * 房源门店搜索 (第二版)
	 * @param params
	 * @return
	 */
	@GET("GetStoreToEstatesForAppSingle")
	Observable<ApiResponse<List<StoreBo>>> getStoreToEstatesSingle(@QueryMap(encoded = true) Map<String, String> params);

	/**
	 *	用户头像上传
	 * @param params
	 * @return
	 */
	@FormUrlEncoded
	@POST("UpdateUserImgRequest")
	Observable<ApiResponse<UserInfoBean>> updateUserImg(@FieldMap Map<String, String> params);


	@GET("UserImgRequest")
	Observable<BaseSingleResult<UserImageBean>> getUserImg(@Query("UserId") String userId);

	//根据AdsNo获取房源信息
	@GET("GetPostByAdsNoRequest")
	Observable<ApiResponse<HouseDetailBo>> getHouseByAdsNo(@Query("AdsNo") String AdsNo);

	/**
	 * 更新用户信息
	 * @param params
	 * @return
	 */
	@FormUrlEncoded
	@POST("UpdateUserInfo2Request")
	Observable<BaseSingleResult<UserInfoBean>> updateUserInfo(@FieldMap Map<String, String> params);

	/**
	 * 以下是登录接口中的请求
	 */

	/**
	 * 发送验证码
	 * @param params
	 * @return
	 */
	@FormUrlEncoded
	@POST("SmsRequest")
	Observable<BaseLoginResult> sendSms(@FieldMap Map<String, String> params);

	@FormUrlEncoded
	@POST("SmsRequest")
	Observable<ApiResponse<Integer>> getVerifyCode(@FieldMap Map<String, String> params);

	/**
	 * 验证code
	 * @param params
	 * @return
	 */
	@FormUrlEncoded
	@POST("VerificationCodeRequest")
	Observable<ApiResponse<Integer>> verificationCode(@FieldMap Map<String, String> params);

	/**
	 * 获取用户信息
	 * @param params
	 * @return
	 */
	@FormUrlEncoded
	@POST("UserInfoRequest")
	Observable<BaseResult<UserInfoBean>> getUserInfo(@FieldMap Map<String, String> params);

	/**
	 * 用户登录
	 * @param params
	 * @return
	 */
	@FormUrlEncoded
	@POST("UserLogin2Request")
	Observable<BaseSingleResult<UserInfoBean>> login(@FieldMap Map<String, String> params);


	/**
	 * 以下是sina服务接口
	 */
	@GET("https://api.weibo.com/2/users/show.json")
	Observable<SinaUserInfoBean> getSinaUserInfo(@QueryMap(encoded = true) Map<String, String> params);


	/**
	 * 微信接口，获取token
	 */
	@GET("https://api.weixin.qq.com/sns/oauth2/access_token")
	Observable<WXTokenBean> getUserToken(@QueryMap(encoded = true) Map<String, String> params);

	/**
	 * 获取微信用户信息
	 * @param params
	 * @return
	 */
	@GET("https://api.weixin.qq.com/sns/userinfo")
	Observable<WxUserBean> getWxUserInfo(@QueryMap(encoded = true) Map<String, String> params);

	/**
	 * 获取qq用户信息
	 * @param params
	 * @return
	 */
	@GET("https://graph.qq.com/user/get_simple_userinfo")
	Observable<QQUserBean> getQqUserInfo(@QueryMap(encoded = true) Map<String, String> params);


	//大文件下载
	@Streaming
	@GET
	Call<ResponseBody> downloadFile(@Url String fileUrl);

	/**
	 * 获取搜索热点
	 * @return
	 */
	@FormUrlEncoded
	@POST("SeoHotModelRequest")
	Observable<ApiResponse<List<SeoHotModelResponse>>> getSeoHot(@FieldMap Map<String, String> params);

	/**
	 * 获取搜索联想词
	 * @param params
	 * @return
	 */
	@GET("TagModelRequest")
	Observable<ApiResponse<List<TagModelResponse>>> getSearchTag(@QueryMap(encoded = true) Map<String, String> params);

	//小区列表
	@GET("GetRegionEstatesRequest")
	Observable<ApiResponse<List<EstateBo>>> getEstateList(@QueryMap(encoded = true) Map<String, String> params);

	//小区列表 根据学校来搜索
	@GET("GetSchoolEstatesRequest")
	Observable<ApiResponse<List<EstateBo>>> getEstateBySchool(@QueryMap(encoded = true) Map<String, String> params);


	//数据 版本号
	@GET("GetVersion")
	Observable<ApiResponse<Long>> getDataVersion();

	//依据小区id获取楼号
	@GET("GetBulidNUMRequest")
	Observable<ApiResponse<List<BuildingNumBean>>> getBulidNUMRequest(@Query("estid")String estid);

	//依据楼号获取室号
	@GET("GetBulidRoomNUMRequest")
	Observable<ApiResponse<List<BuildingNumBean>>> getBulidRoomNUMRequest(@Query("bulidid")String bulidid);

	/**
	 * 房源发起直聊
	 * @param params
	 * @return
     */
	@FormUrlEncoded
	@POST("MessageRecordUpdateRequest")
	Observable<ApiResponse<Integer>> messageRecordUpdateRequest(@FieldMap Map<String, String> params);


	/**
	 * 委托附件上传(只支持单文件上传)
	 * @param parts
	 * @return
     */
	@Multipart
	@POST("FilesRequest")
	Observable<ApiResponse<ImageUploadBean>> upload(@Part MultipartBody.Part parts);

	/**
	 * 查询委托数量
	 * @param id
	 * @return
     */
	@GET("EntrustCountRequest")
	Observable<ApiResponse<Integer>> entrustCountRequest(@Query("UserID")String id);
	//委托小区搜索接口，固定返回10条记录
	@GET("GetEntrustEstateRequest")
	Observable<ApiResponse<List<EstateBo>>> getEntrustEstateRequest(@Query("HybridKey") String key);

	//依据房源id获取委托房源中是否有该房源
	@GET("GetEnstrustByIDRequest")
	Observable<ApiResponse<DeputeBean>> getEnstrustByIDRequest(@QueryMap(encoded = true) Map<String, String> params);
	/**
	 * 直聊接口，直接向经纪人发起
	 * @param params
	 * @return
	 */
	@FormUrlEncoded
	@POST("SingleMessageRequest")
	Observable<ApiResponse<Integer>> singleMessageRequest(@FieldMap Map<String, String> params);

	/**
	 * 房屋估价
	 * @param params
	 * @return
     */
	@GET("EvaluateRequest")
	Observable<ApiResponse<Double>> evaluateRequest(@QueryMap(encoded = true) Map<String, String> params);

}























