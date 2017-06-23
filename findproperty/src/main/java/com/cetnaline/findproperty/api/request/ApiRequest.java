package com.cetnaline.findproperty.api.request;

import com.cetnaline.findproperty.AppContents;
import com.cetnaline.findproperty.BuildConfig;
import com.cetnaline.findproperty.api.service.RegionPostsApi;
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
import com.cetnaline.findproperty.api.NetWorkManager;
import com.cetnaline.findproperty.utils.DataHolder;
import com.cetnaline.findproperty.utils.Md5;
import com.cetnaline.findproperty.utils.RxResultHelper;
import com.cetnaline.findproperty.utils.SchedulersCompat;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;

/**
 * Created by fanxl2 on 2016/7/25.
 */
public class ApiRequest {

	private static RegionPostsApi commonService;
	private static RegionPostsApi noCacheService;

	// TODO: 2017/3/1 流量活动请求接口
	public static Observable<Boolean> updateShareStateRequest(String id) {
		return getCommonService().updateShareStateRequest(id)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<AdvertBo> getAppXuanFuAdvertRequest() {
		return getCommonService().getAppXuanFuAdvertRequest()
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}



	public static RegionPostsApi getCommonService() {
		if (commonService == null || NetWorkManager.commonClientRefresh) {
			NetWorkManager.commonClientRefresh = false;
			commonService = NetWorkManager.getCommonClient().create(RegionPostsApi.class);
		}
		return commonService;
	}

	public static RegionPostsApi getNoCacheService() {
		if (noCacheService == null || NetWorkManager.noCahceClientRefresh) {
			NetWorkManager.noCahceClientRefresh = false;
			noCacheService = NetWorkManager.getNoCacheClient().create(RegionPostsApi.class);
		}
		return noCacheService;
	}

	public static Observable<List<RegionPostBo>> getBaseMapData(Map<String, String> params){

		return getCommonService().getBaseHouseData(params)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

//	public static Observable<List<EstateBo>> getEsfEstateList(Map<String, String> params){
//
//		return NetWorkManager.getCommonClient()
//				.create(RegionPostsApi.class)
//				.getEsfEstateList(params)
//				.compose(SchedulersCompat.applyIoSchedulers())
//				.compose(RxResultHelper.handleResult());
//	}

	public static Observable<List<EstateBo>> getEsf4Map(EstateMapRequest request){

//			return Observable.just(null)
//					.flatMap(new Func1<Object, Observable<List<EstateBo>>>() {
//
//						@Override
//						public Observable<List<EstateBo>> call(Object o) {
//
//							if (token==null){
//								return Observable.error(new NullPointerException(""));
//							}
//							return getCommonService()
//									.getEsf4Map(request)
//									.compose(SchedulersCompat.applyIoSchedulers())
//									.compose(RxResultHelper.handleResult());
//						}
//					})
//					.compose(SchedulersCompat.retry(getCommonService()));

		return getCommonService()
				.getEsf4Map(request)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<List<HouseBo>> getHouseList(Map<String, String> params){

		return getCommonService()
				.getHouseList(params)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<ApiResponse<List<HouseBo>>> getHouseList4AllResult(Map<String, String> params){

		return getNoCacheService()
				.getHouseList(params)
				.compose(SchedulersCompat.applyIoSchedulers());
	}

	public static Observable<ApiResponse<List<HouseBo>>> getHouseBySchool4AllResult(Map<String, String> params){
		return getNoCacheService()
				.getHouseBySchool(params)
				.compose(SchedulersCompat.applyIoSchedulers());
	}

	public static Observable<ApiResponse<List<HouseBo>>> getHouseByMetroAllResult(Map<String, String> params){
		return getNoCacheService()
				.getHouseByMetro(params)
				.compose(SchedulersCompat.applyIoSchedulers());
	}

	public static Observable<ApiResponse<List<NewHouseListBo>>> getNewHouse4AllResult(Map<String, String> params){

		return getNoCacheService()
				.getNewHouses(params)
				.compose(SchedulersCompat.applyIoSchedulers());
	}

	public static Observable<List<HouseBo>> getStaffHouseList(Map<String, String> params){
		return getCommonService()
				.getStaffHouseList(params)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}


	/**
	 * 获取经济人列表（v2）
	 * @param params
	 * @return
	 */
	public static Observable<CmBaseResult<CmListStaffBean>> getStaffList(Map<String, String> params){
		return getCommonService()
				.getStaffList(params)
				.compose(SchedulersCompat.applyIoSchedulers());
	}

	/**
	 * 依据职工号获取经纪人信息（v2）
	 * @param no
	 * @return
	 */
	public static Observable<CmBaseSingleResult<CmListStaffBean>> getStaffByNo(String no){
		return getCommonService()
				.getStaffByNo(no)
				.compose(SchedulersCompat.applyIoSchedulers());
	}

	/**
	 * 直聊，向服务器推送消息
	 * @param params
	 * @return
	 */
	public static Observable<ApiResponse> messageRecord(Map<String, String> params) {
		return getCommonService()
				.messageRecord(params)
				.compose(SchedulersCompat.applyIoSchedulers());
	}

	/**
	 * 直聊，从服务器获取系统消息
	 * @return
     */
//	public static Observable<SystemMsgBean> getLiaoTianSysMsg(){
//		return getCommonService()
//				.getLiaoTianSysMsg()
//				.compose(SchedulersCompat.applyIoSchedulers())
//				.compose(RxResultHelper.handleResult());
//	}

	public static Observable<List<EstateBo>> getEstByCircle(Map<String, String> params){

		return getNoCacheService()
				.getEstByCircle(params)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}


	public static Observable<String> getDiscountMenu(String groupType){
		return getCommonService()
				.getDiscountMenu(groupType)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<List<Discount>> getDiscountList(Map<String, String> params){
		return getCommonService()
				.getDiscountList(params)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<List<NewStaffBo>> getNewHouseStaff(String estExtId){
		return getCommonService()
				.getNewHouseStaff(estExtId)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	/**
	 * 提交用户反馈
	 * @param params
	 * @return
	 */
	public static Observable<Integer> InsertUserFeedBack(Map<String, String> params) {
		return getCommonService()
				.InsertUserFeedBack(params)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<Integer> FeedBackConsultMessage(ConsultFormBean params) {
		return getCommonService()
				.FeedBackConsultMessage(params)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	/**
	 * 搜索词频统计
	 * @param type
	 * @param msg
	 * @return
	 */
	public static Observable<Integer> wordFrequency(String type,String msg) {
		Map<String, String> params = new HashMap<>();
		params.put("CityCode", "021");
		params.put("Source", "APP-ANDROID");
		params.put("Types", type);
		params.put("Keyworlds", msg);
		return getCommonService()
				.wordFrequency(params)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}


	/**
	 * 以下是8月5号后台提供的经济人接口，不知道是否是最终接口
	 */


	/**
	 * 获取区域、板块信息
	 * @return
	 */
	public static Observable<List<GScopeBean>> getGScope(){
		return getCommonService()
				.getGScope()
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	/**
	 * 获取经纪人列表
	 * @param params
	 * @return
	 */
	public static Observable<BaseResult<StaffListBean>> getStaffs(Map<String,Object> params){
		params.put("MustHasPost","false");
		return getCommonService()
				.getStaffs(params)
				.compose(SchedulersCompat.applyIoSchedulers());
	}

	/**
	 * 获取经纪人详情
	 * @param staffNo
	 * @return
	 */
	public static Observable<StaffListBean> getStaffDetail(String staffNo){
		return getCommonService()
				.getStaffDetail(staffNo)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	//获取菜单数据
	public static Observable<List<SearchData>> getSearchData(){
		return getCommonService()
				.getSearchData()
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<List<SchoolBo>> getSchoolList(String regionId){
		return getCommonService()
				.getSchoolList(regionId)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<List<RailLine>> getRailLines(){
		return getCommonService()
				.getRailLines()
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<List<EstateBo>> getRailWayHouse(Map<String, String> params){
		return getCommonService()
				.getRailWayHouse(params)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<List<EstateBo>> getEstateBySchool(Map<String, String> params){
		return getCommonService()
				.getEstateBySchool(params)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

//	public static Observable<List<NewHousePropBo>> getNewHouseBaseMapData(Map<String, String> params){
//
//		return NetWorkManager.getCommonClient()
//				.create(RegionPostsApi.class)
//				.getNewHouseBaseMapData(params)
//				.compose(SchedulersCompat.applyIoSchedulers())
//				.compose(RxResultHelper.handleResult());
//	}


	public static Observable<List<NewHouseMapDetail>> getNewHouseList(Map<String, String> params){

		return getCommonService()
				.getNewHouseList(params)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<List<NewHouseListBo>> getNewHouses(Map<String, String> params){
		return getCommonService()
				.getNewHouses(params)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<HouseDetailBo> getHouseDetailData(Map<String, String> params){
		return getNoCacheService()
				.getHouseDetailData(params)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<List<StaffComment>> getStaffComments(String AdsNo, String StaffNo){
		return getCommonService()
				.getStaffComments(AdsNo, StaffNo)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<List<StaffComment>> getPostStaffs(String EstateCodes, String PostIds, String TopCount ,String SearchReserveStaffModel){
		return getCommonService()
				.getPostStaffs(EstateCodes, PostIds, TopCount ,SearchReserveStaffModel)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<EstateBo> getEstateByCode(Map<String, String> params){
		return getCommonService()
				.getEstateByCode(params)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<NewHouseDetail> getNewHouseDetail(String estExtId){

		return getCommonService()
				.getNewHouseDetail(estExtId)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<List<HouseBo>> getNearbyHouse(Map<String, String> params){

		return getCommonService()
				.getNearbyHouse(params)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<ApiResponse<List<HouseBo>>> getNearbyHouseAllResult(Map<String, String> params){

		return getCommonService()
				.getNearbyHouse(params)
				.compose(SchedulersCompat.applyIoSchedulers());
	}

	public static Observable<SchoolBo> getEstBySchoolId(String estExtId){

		return getCommonService()
				.getEstBySchoolId(estExtId)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<List<ApartmentBo>> getApartments(String estExtId){

		return getCommonService()
				.getApartments(estExtId)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<List<HouseImageBo>> getHouseImageList(String postId, int imgW, int imgH){

		return getCommonService()
				.getHouseImageList(postId, imgW, imgH)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<List<ExerciseListBo>> getExerciseList(String estExtId){

		return getCommonService()
				.getExerciseList(estExtId)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<List<ExerciseListBo>> getExerciseListByIds(String ids){

		return getCommonService()
				.getExerciseListByIds(ids)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<List<NewHouseImageBo>> getNewHouseImages(String appvalue){

		return getCommonService()
				.getNewHouseImages(appvalue)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<List<GscopeDealPriceBo>> getGscopeDeailPrice(Map<String, String> params){

		return getCommonService()
				.getGscopeDeailPrice(params)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleNoErrorResult());
	}

	public static Observable<List<EstateDealPriceBo>> getEstateDeailPrice(Map<String, String> params){

		return getCommonService()
				.getEstateDeailPrice(params)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleNoErrorResult());
	}

	public static Observable<Integer> insertEntrustInfo(DeputePushBean bean){
//		Gson gson=new Gson();
//		String route= gson.toJson(bean);
//		RequestBody body=RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),route);
		return getCommonService()
				.insertEntrustInfo(bean)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<StaffDetailBo> getEstateStaffByEstateCode(String estateCode){
		return getCommonService()
				.getEstateStaffByEstateCode(estateCode)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<Long> insertCollect(Map<String, String> params){
		return getCommonService()
				.insertCollect(params)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<Integer> insertReservation(Map<String, String> params){
		return getCommonService()
				.insertReservation(params)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<List<LookAboutBean>> getLookAboutList(String userId, int status, int pageIndex, int pageSize){
		return getNoCacheService()
				.getLookAboutList(userId, status, pageIndex, pageSize)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<ApiResponse<List<LookAboutBean>>> getLookAboutListAll(String userId, int status, int pageIndex, int pageSize){
		return getNoCacheService()
				.getLookAboutList(userId, status, pageIndex, pageSize)
				.compose(SchedulersCompat.applyIoSchedulers());
	}

	public static Observable<ApiResponse<List<MyEntrustBo>>> getMyEntrustList(Map<String,String> params){
		return getNoCacheService()
				.getMyEntrustList(params)
				.compose(SchedulersCompat.applyIoSchedulers());
	}

//	public static Observable<Integer> delEntrust(long entrustId){
//		return getCommonService()
//				.delEntrust(entrustId)
//				.compose(SchedulersCompat.applyIoSchedulers())
//				.compose(RxResultHelper.handleResult());
//	}

	public static Observable<Integer> updateEntrustStutasRequest(Map<String, String> params) {
		return getCommonService()
				.updateEntrustStutasRequest(params)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<List<MyEntrustBo>> getMyEntrustById(String userId, String entrustId){
		return getNoCacheService()
				.getMyEntrustById(userId, entrustId)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<Integer> deleteLookAboutList(LookListDeleteRequest request){
		return getCommonService()
				.deleteLookAboutList(request)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<LookAboutNumBo> getLookedAboutNumber(String userId, int status){
		return getNoCacheService()
				.getLookedAboutNumber(userId, status)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<Long> checkCollect(String userId, String postId){
		return getNoCacheService()
				.checkCollect(userId, postId)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<Integer> insertCollectBatch(BatchCollectRequest request){
		return getNoCacheService()
				.insertCollectBatch(request)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<Integer> commitLookAboutList(SendAppointmentRequest request){
		return getCommonService()
				.commitLookAboutList(request)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<Integer> lookAboutComment(Map<String, String> param){
		return getNoCacheService()
				.lookAboutComment(param)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<AppUpdateBo> getAppVersion(){
		return getNoCacheService()
				.getAppVersion()
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<AppAdBo> getAppAdvert(){
		return getNoCacheService()
				.getAppAdvert()
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<Integer> insertUserBooking(Map<String, String> param){
		return getNoCacheService()
				.insertUserBooking(param)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<Integer> addBookingCount(String actId, int number){
		return getNoCacheService()
				.addBookingCount(actId, number)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<Long> insertIntent(InsertIntentionsRequest request){
		return getCommonService()
				.insertIntent(request)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<Boolean> deleteIntent(long intentionId){
		return getCommonService()
				.deleteIntent(intentionId)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}


	public static Observable<ApiResponse<List<IntentionBo>>> getIntentionList(Map<String,String> params){
		params.put("userId",DataHolder.getInstance().getUserId());
		return getNoCacheService()
				.getIntentionList(params)
				.compose(SchedulersCompat.applyIoSchedulers());
	}

	public static Observable<List<IntentionBo>> getIntention4Home(String userId){
		return getNoCacheService()
				.getIntention4Home(userId)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<List<HouseBo>> getHomeRecommend(double lat, double lng){
		return NetWorkManager.getCommonClient()
				.create(RegionPostsApi.class)
				.getHomeRecommend(lat, lng)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<HouseDetailBo> getHouseByAdsNo(String adsNo){
		return getCommonService()
				.getHouseByAdsNo(adsNo)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<List<SystemMessageBean>> getSystemMessageList(Map<String,String> params) {
		return getCommonService()
				.getSystemMessageList(params)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<Integer> updateSystemMessage(String id) {
		return getCommonService()
				.updateSystemMessage(id)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<Integer> delSystemMessage(String id) {
		return getCommonService()
				.delSystemMessage(id)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}


	/**
	 * 标记消息已读
	 * @param id
	 * @return
	 */
	public static Observable<BaseSingleResult<Integer>> updateCollectInfoChangeIsRead(String id) {
		Map<String, String> params = new HashMap();
		params.put("CollectID",id);
		return getNoCacheService()
				.updateCollectInfoChangeIsRead(params)
				.compose(SchedulersCompat.applyIoSchedulers());
	}

	/**
	 * 获取我的消息更新列表
	 * @param params
	 * @return
	 */
	public static Observable<BaseResult<CollectInfoChangeBean>> getCollectInfoChangeList(Map<String, String> params) {
		return getNoCacheService()
				.getCollectInfoChangeList(params)
				.compose(SchedulersCompat.applyIoSchedulers());
	}

	/**
	 * 获取是定房源列表
	 * @param params
	 * @return
	 */
	public static Observable<ApiResponse<List<HouseBo>>> getMultiplePost(Map<String, String> params){
		return getNoCacheService()
				.getMultiplePost(params)
				.compose(SchedulersCompat.applyIoSchedulers());
	}

	public static Observable<List<HouseBo>> getHouseDetailList(Map<String, String> params){
		return getNoCacheService()
				.getMultiplePost(params)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	/**
	 * 获取收藏列表
	 * @param params
	 * @return
	 */
	public static Observable<BaseResult<CollectionBean>> getCollectList(Map<String,String> params) {
		params.put("CityCode","021");
		params.put("IsDel","0");
		return getNoCacheService()
				.getCollectList(params)
				.compose(SchedulersCompat.applyIoSchedulers());
	}

	/**
	 * 根据id获取新房列表
	 * @param ids
	 * @return
	 */
	public static Observable<ApiResponse<List<NewHouseListBo>>> getNewHouseByIds(String ids) {
		Map<String, String> params = new HashMap<>();
		params.put("Length","1000");
		params.put("StartIndex","0");
		params.put("EstExtIds",ids);
		params.put("UsedEstExtIds","1");
		return getNoCacheService()
				.getNewHouseByIds(params)
				.compose(SchedulersCompat.applyIoSchedulers());
	}

	/**
	 * 获取小区列表，依据id数组
	 * @param ids
	 * @return
	 */
	public static Observable<ApiResponse<List<EstateBo>>> getEstatesByCodeList(String ids) {
		Map<String,String> params = new HashMap<>();
		params.put("ImageWidth","200");
		params.put("ImageHeight","200");
		params.put("EstateCodeList",ids);

		return getNoCacheService()
				.getEstatesByCodeList(params)
				.compose(SchedulersCompat.applyIoSchedulers());
	}

	/**
	 * 删除指定收藏信息
	 * @param
	 * @return
	 */
	public static Observable<Integer> deleteCollect(long collectId) {
		return getNoCacheService()
				.deleteCollect(collectId, true)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	/**
	 * 删除收藏更新信息
	 * @param id
	 * @return
	 */
	public static Observable<Integer> delCollectInfoChange(String id) {
		return getNoCacheService()
				.delCollectInfoChange(id)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	/**
	 * 获取用户活动列表
	 * @param params
	 * @return
	 */
	public static Observable<BaseResult<BookingBean>> getUserBookingList(Map<String, String> params) {
		params.put("AppName","APP");
		params.put("CityCode","021");
		params.put("IsDel","false");
		params.put("UserId", DataHolder.getInstance().getUserId());
		return getNoCacheService()
				.getUserBookingList(params)
				.compose(SchedulersCompat.applyIoSchedulers());
	}

	/**
	 * 删除用户参与的指定活动
	 * @param id
	 * @return
	 */
	public static Observable<Integer> delUserBooking(String id) {
		return getNoCacheService()
				.delUserBooking(id)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	/**
	 * 用户头像上传
	 * @param img
	 * @return
	 */
	public static Observable<ApiResponse<UserInfoBean>> updateUserImg(String img) {
		Map<String, String> params = new HashMap<>();
		params.put("UserId", DataHolder.getInstance().getUserId());
		params.put("Img",img);

		return getCommonService()
				.updateUserImg(params)
				.compose(SchedulersCompat.applyIoSchedulers());
	}

	/**
	 * 获取用户头像
	 * @param userId
	 * @return
	 */
	public static Observable<BaseSingleResult<UserImageBean>> getUserImg(String userId) {
		return getNoCacheService()
				.getUserImg(userId)
				.compose(SchedulersCompat.applyIoSchedulers());
	}

	/**
	 * 更新用户信息
	 * @param params
	 * @return
	 */
	public static Observable<BaseSingleResult<UserInfoBean>> updateUserInfo(Map<String, String> params) {
		params.put("UserId", DataHolder.getInstance().getUserId());
		return getCommonService()
				.updateUserInfo(params)
				.compose(SchedulersCompat.applyIoSchedulers());
	}

	/**
	 * 查询用户订阅
	 * @param params
	 * @return
	 */
	public static Observable<List<SubscribeBean>> getSubscribeList(Map<String, String> params){
		params.put("UserId",DataHolder.getInstance().getUserId());
		return getCommonService()
				.getSubscribeList(params)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	/**
	 * 删除订阅
	 * @param id
	 * @return
	 */
	public static Observable<Boolean> deleteSubscribe(long id) {
		return getCommonService()
				.deleteSubscribe(id)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	/**
	 * 门店搜索 (第一版)
	 * @param params
	 * @return
	 */
	public static Observable<List<StoreBo>> searchStore(Map<String, String> params) {
		return getCommonService()
				.searchStore(params)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<List<StoreBo>> searchStore1(Map<String, String> params) {
		return getCommonService()
				.searchStore(params)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleNoErrorResult());
	}

	/**
	 * 门店搜索 （第二版）
	 * @param params
	 * @return
	 */
	public static Observable<List<StoreBo>> searchStoreSingle(Map<String, String> params) {
		return getCommonService()
				.searchStoreSingle(params)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleNoErrorResult());
	}

	/**
	 * 小区门店搜索 (第一版)
	 * @param params
	 * @return
	 */
	public static Observable<List<StoreBo>> getStoreToEstates(Map<String, String> params) {
		return getCommonService()
				.getStoreToEstates(params)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleNoErrorResult());
	}

	/**
	 * 小区门店搜索 (第二版)
	 * @param params
	 * @return
	 */
	public static Observable<List<StoreBo>> getStoreToEstatesSingle(Map<String, String> params) {
		return getCommonService()
				.getStoreToEstatesSingle(params)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleNoErrorResult());
	}

	/**
	 * 检测手机号是否已经绑定
	 * @param params
	 * @return
	 */
	public static Observable<String> checkPhoneIsBind(Map<String, String> params) {
		return getCommonService()
				.checkPhoneIsBind(params)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}


	/**
	 * 以下是登录接口中的内容
	 */

	/**
	 * 发送短信
	 * @param mobile
	 * @return
	 */
	public static Observable<BaseLoginResult> sendSms(String mobile) {
		Map<String,String> params = new HashMap<>();
		params.put("Mobile", mobile);
		params.put("AppNo","APP");
		params.put("CityCode","021");
		String sign = Md5.encode(AppContents.PRIVATESECRET,"021",mobile,AppContents.PUBLICSECRET);
		params.put("Sign",sign);

		return getCommonService()
				.sendSms(params)
				.compose(SchedulersCompat.applyIoSchedulers());
	}

	/**
	 * 发送短信
	 * @param mobile
	 * @return
	 */
	public static Observable<Integer> getVerifyCode(String mobile) {
		Map<String,String> params = new HashMap<>();
		params.put("Mobile", mobile);
		params.put("AppNo", "APP");
		params.put("CityCode","021");
		String sign = Md5.encode(AppContents.PRIVATESECRET, "021", mobile, AppContents.PUBLICSECRET);
		params.put("Sign", sign);

		return getCommonService()
				.getVerifyCode(params)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	/**
	 * 验证code
	 */
	public static Observable<Integer> verificationCode(String mobile, String code) {

		Map<String,String> params = new HashMap<>();
		params.put("Mobile",mobile);
		params.put("AppNo","APP");
		params.put("CityCode","021");
		params.put("Code",code);
		String sign = Md5.encode("APP", "021", mobile,code,AppContents.PUBLICSECRET);
		params.put("Sign",sign);

		return getNoCacheService()
				.verificationCode(params)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	/**
	 * 获取用户信息
	 * @return
	 */
	public static Observable<BaseResult<UserInfoBean>> getUserInfo(String phone, String sinaAccount, String qqAccount, String weiXinAccount) {
		Map<String,String> params = new HashMap<>();
		params.put("Phone",phone== null ? "":phone);
		params.put("SinaAccount",sinaAccount== null ? "":sinaAccount);
		params.put("QQAccount",qqAccount== null ? "":qqAccount);
		params.put("weiXinAccount",weiXinAccount== null ? "":weiXinAccount);
		return getCommonService()
				.getUserInfo(params)
				.compose(SchedulersCompat.applyIoSchedulers());
	}

	/**
	 * 用户登录
	 * @param params
	 * @return
	 */
	public static Observable<BaseSingleResult<UserInfoBean>> login(Map<String,String> params){
		params.put("AppNo","APP_ANDROID");
		params.put("CityCode","021");
		return getCommonService()
				.login(params)
				.compose(SchedulersCompat.applyIoSchedulers());
	}

	/**
	 * 新浪接口
	 */
	/**
	 * 获取新浪账户信息
	 * @param token
	 * @param uid
	 * @param name
	 * @return
	 */
	public static Observable<SinaUserInfoBean> getSinaUserInfo(String token, String uid, String name){
		Map<String,String> params = new HashMap<>();
		params.put("access_token",token);
		params.put("uid",uid);
		params.put("screen_name",name);
		return getCommonService()
				.getSinaUserInfo(params)
				.compose(SchedulersCompat.applyIoSchedulers());
	}


	/**
	 * 微信接口
	 */

	/**
	 * 获取用户token
	 * @param code
	 * @return
	 */
	public static Observable<WXTokenBean> getUserToken(String code) {
		Map<String, String> params = new HashMap<>();
		params.put("appid", BuildConfig.APP_ID_WX);
		params.put("secret", BuildConfig.APP_SECRET_WX);
		params.put("code",code);
		params.put("grant_type","authorization_code");

		return getCommonService()
				.getUserToken(params)
				.compose(SchedulersCompat.applyIoSchedulers());
	}

	/**
	 * 获取微信用户信息
	 * @param token
	 * @param openid
	 * @return
	 */
	public static Observable<WxUserBean> getWxUserInfo(String token,String openid){
		Map<String, String> params = new HashMap<>();
		params.put("access_token",token);
		params.put("openid", openid);

		return getCommonService()
				.getWxUserInfo(params)
				.compose(SchedulersCompat.applyIoSchedulers());
	}

	/**
	 * 获取qq用户信息
	 * @param params
	 * @return
	 */
	public static Observable<QQUserBean> getQqUserInfo(Map<String,String> params) {
		return getCommonService()
				.getQqUserInfo(params)
				.compose(SchedulersCompat.applyIoSchedulers());
	}

	/**
	 * 获取搜索热点Tag
	 * @return
	 */
	public static Observable<List<SeoHotModelResponse>> getSeoHotTag() {
		Map<String,String> params = new HashMap<>();
		params.put("CityCode","021");
		params.put("AppName", "HOME");
		params.put("Source", "home");
		params.put("Top", "12");
		return getCommonService()
				.getSeoHot(params)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	/**
	 * 获取搜索联想词
	 * @return
	 */
	public static Observable<List<TagModelResponse>> getSearchTag(Map<String,String> params) {
		return getCommonService()
				.getSearchTag(params)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<List<EstateBo>> getEstateList(Map<String, String> params){

		return getNoCacheService()
				.getEstateList(params)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<Long> getDataVersion(){
		return getNoCacheService()
				.getDataVersion()
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<List<BuildingNumBean>> getBulidNUMRequest(String no) {
		return getNoCacheService()
				.getBulidNUMRequest(no)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<List<BuildingNumBean>> getBulidRoomNUMRequest(String no) {
		return getNoCacheService()
				.getBulidRoomNUMRequest(no)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<List<EstateBo>> getEntrustEstateRequest(String key) {
		return getNoCacheService()
				.getEntrustEstateRequest(key)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<ImageUploadBean> uploadImage(String url, String tag) {
		File file = new File(url);
		UploadFileBody body = new UploadFileBody(RequestBody.create(MediaType.parse("image/*"), file),tag);
//		RequestBody body = RequestBody.create(MediaType.parse("application/otcet-stream"), file);
		MultipartBody.Part part = MultipartBody.Part.createFormData("file_"+System.currentTimeMillis(), file.getName(), body);
//		MultipartBody.Part part = MultipartBody.Part.createFormData("file_"+System.currentTimeMillis(), file.getName(), RequestBody.create(MediaType.parse("image/*"), file));

		return getNoCacheService()
				.upload(part)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<Integer> singleMessageRequest(Map<String, String> params) {
		return getCommonService()
				.singleMessageRequest(params)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<Integer> messageRecordUpdateRequest(Map<String,String> params) {
		return getCommonService()
				.messageRecordUpdateRequest(params)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<Integer> entrustCountRequest(String id) {
		return getCommonService()
				.entrustCountRequest(id)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<Double> evaluateRequest(Map<String, String> params) {
		return getCommonService()
				.evaluateRequest(params)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}

	public static Observable<DeputeBean> getEnstrustByIDRequest(Map<String,String> params) {
		return getCommonService()
				.getEnstrustByIDRequest(params)
				.compose(SchedulersCompat.applyIoSchedulers())
				.compose(RxResultHelper.handleResult());
	}
}
































