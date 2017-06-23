package com.cetnaline.findproperty.utils;

import android.app.Activity;

import com.cetnaline.findproperty.AppContents;
import com.cetnaline.findproperty.entity.bean.UserInfoBean;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据单例类
 * Created by fanxl2 on 2016/8/4.
 */
public class DataHolder {

	private static volatile DataHolder dataHolder;

	private DataHolder(){}

	public static DataHolder getInstance(){
		if (dataHolder == null || dataHolder.getRcToken() == null){
			synchronized (DataHolder.class){
				if (dataHolder==null || dataHolder.getRcToken() == null){
					dataHolder = new DataHolder();
					dataHolder.isLogin = SharedPreferencesUtil.getBoolean(AppContents.USER_IS_LOGIN);
					dataHolder.userInfo = SharedPreferencesUtil.getUserInfo();
					dataHolder.rcToken = SharedPreferencesUtil.getString(AppContents.RONG_CLOUD_TOKEN);
					dataHolder.rcUserId = SharedPreferencesUtil.getString(AppContents.RONG_CLOUD_USERID);
					dataHolder.rcUsername = SharedPreferencesUtil.getString(AppContents.RONG_CLOUD_USERNAME);
					dataHolder.rcPortrait = SharedPreferencesUtil.getString(AppContents.RONG_CLOUD_PORTRAIT);
					dataHolder.messageCount = SharedPreferencesUtil.getInt(AppContents.RONG_CLOUD_UNREAD_COUNT);
					dataHolder.setMsgCount(SharedPreferencesUtil.getInt(AppContents.RONG_CLOUD_UNREAD_COUNT));

					dataHolder.closeNotice = SharedPreferencesUtil.getBoolean(AppContents.CLOSE_NOTICE);
					dataHolder.closeVibrate = SharedPreferencesUtil.getBoolean(AppContents.CLOSE_VIBRATE);
					dataHolder.closeSound = SharedPreferencesUtil.getBoolean(AppContents.CLOSE_SOUND);
					dataHolder.databaseVersion = SharedPreferencesUtil.getInt(AppContents.DATABASE_VERSION);
					dataHolder.userWap = SharedPreferencesUtil.getString(AppContents.SH_CK_USER);
					dataHolder.userAuk = SharedPreferencesUtil.getString(AppContents.SH_CK_USER_AUK);
					dataHolder.chatting = false;
					dataHolder.chatTarget = "";
					dataHolder.currentMessageNoticeId = AppContents.RONG_MESSAGE_NOTIFICATION_ID;
					dataHolder.notePushEnable = false;

					dataHolder.longitude = SharedPreferencesUtil.getFloat(AppContents.LONGITUDE);
					dataHolder.latitude = SharedPreferencesUtil.getFloat(AppContents.LATITUDE);
					dataHolder.locationRedius = SharedPreferencesUtil.getFloat(AppContents.LOCATION_REDUIS);
				}
			}
		}
		return dataHolder;
	}

	private double latitude; //纬度
	private double longitude; //经度
	private float locationRedius; //定位半径

	//用户是否登录
	private boolean isLogin;
	//登录用户信息
	private UserInfoBean userInfo;
	//RC当前用户token
	private String rcToken;
	//RC用户Id
	private String rcUserId;
	//RC用户名称
	private String rcUsername;
	//RC用户头像uri
	private String rcPortrait;
	//未读消息数量
	private int messageCount;
	//数据库版本
	private int databaseVersion;
	//系统通知关闭标识
	private boolean notePushEnable;

	private int currentMessageNoticeId;

	private boolean closeNotice;
	private boolean closeVibrate;
	private boolean closeSound;

	private boolean chatting;
	private String chatTarget;

	private String userWap;
	private String userAuk;

	//应用启动类别标记,通知栏启动
	private boolean noticeLunchMode;
	private String whichPage;
	private int newType;
	private int subscribeType;

	public void setNotePushEnable(boolean notePushEnable) {
		this.notePushEnable = notePushEnable;
	}

	public boolean isNotePushEnable() {
		return notePushEnable;
	}

	public boolean isNoticeLunchMode() {
		return noticeLunchMode;
	}

	public void setNoticeLunchMode(boolean noticeLunchMode) {
		this.noticeLunchMode = noticeLunchMode;
	}

	public int getCurrentMessageNoticeId() {
		return currentMessageNoticeId++;
	}

	public String getWhichPage() {
		return whichPage;
	}

	public void setWhichPage(String whichPage) {
		this.whichPage = whichPage;
	}

	public int getNewType() {
		return newType;
	}

	public void setNewType(int newType) {
		this.newType = newType;
	}

	public int getSubscribeType() {
		return subscribeType;
	}

	public void setSubscribeType(int subscribeType) {
		this.subscribeType = subscribeType;
	}

	public String getUserWap() {
		return userWap;
	}

	public String getUserAuk() {
		return userAuk;
	}

	public int getDatabaseVersion() {
		return databaseVersion;
	}

	public void setDatabaseVersion(int databaseVersion) {
		this.databaseVersion = databaseVersion;
		SharedPreferencesUtil.saveInt(AppContents.DATABASE_VERSION,databaseVersion);
	}

	public double getLongitude() {
		return longitude < 1 ? AppContents.CITY_LNG : longitude;
	}

	public void setLongitude(double longitude) {
		SharedPreferencesUtil.saveFloat(AppContents.LONGITUDE, (float) longitude);
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude < 1 ? AppContents.CITY_LAT : latitude;
	}

	public void setLatitude(double latitude) {
		SharedPreferencesUtil.saveFloat(AppContents.LATITUDE, (float) latitude);
		this.latitude = latitude;
	}

	public boolean isCloseNotice() {
		return closeNotice;
	}

	public void setCloseNotice(boolean closeNotice) {
		this.closeNotice = closeNotice;
		SharedPreferencesUtil.saveBoolean(AppContents.CLOSE_NOTICE,closeNotice);
	}

	public boolean isCloseVibrate() {
		return closeVibrate;
	}

	public void setCloseVibrate(boolean closeVibrate) {
		this.closeVibrate = closeVibrate;
		SharedPreferencesUtil.saveBoolean(AppContents.CLOSE_VIBRATE,closeVibrate);
	}

	public boolean isCloseSound() {
		return closeSound;
	}

	public void setCloseSound(boolean closeSound) {
		this.closeSound = closeSound;
		SharedPreferencesUtil.saveBoolean(AppContents.CLOSE_SOUND,closeSound);
	}

	public boolean isChatting() {
		return chatting;
	}

	public void setChatting(boolean chatting) {
		this.chatting = chatting;
	}

	public String getChatTarget() {
		return chatTarget;
	}

	public void setChatTarget(String chatTarget) {
		this.chatTarget = chatTarget;
	}

	/**
	 * 获取登录用户信息
	 * @return
     */
	public UserInfoBean getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserInfoBean userInfo) {
		this.userInfo = userInfo;
		SharedPreferencesUtil.saveUserInfo(userInfo);
	}

	public String getRcToken() {
		return rcToken == null ? "" : rcToken;
	}

	public void setRcToken(String rcToken) {
		this.rcToken = rcToken;
		SharedPreferencesUtil.saveString(AppContents.RONG_CLOUD_TOKEN,rcToken);
	}

	public String getRcUserId() {
		return rcUserId;
	}

	public void setRcUserId(String rcUserId) {
		this.rcUserId = rcUserId;
		SharedPreferencesUtil.saveString(AppContents.RONG_CLOUD_USERID,rcUserId);
	}

	public String getRcUsername() {
		return rcUsername;
	}

	public void setRcUsername(String rcUsername) {
		this.rcUsername = rcUsername;
		SharedPreferencesUtil.saveString(AppContents.RONG_CLOUD_USERNAME,rcUsername);
	}

	public String getRcPortrait() {
		return rcPortrait;
	}

	public void setRcPortrait(String rcPortrait) {
		this.rcPortrait = rcPortrait;
		this.userInfo.UserPhotoUrl = rcPortrait;
		SharedPreferencesUtil.saveString(AppContents.RONG_CLOUD_PORTRAIT,rcPortrait);
	}

	public int getMessageCount() {
		return messageCount;
	}

	public void setMessageCount(int messageCount) {
		this.messageCount = messageCount;
		SharedPreferencesUtil.saveInt(AppContents.RONG_CLOUD_UNREAD_COUNT,messageCount);
	}

	/**
	 * 清除缓存数据
	 */
	public void clear(){
		this.userInfo = null;
		this.isLogin = false;
		this.rcToken = "";
		this.rcPortrait = "";
		this.rcUsername = "";
		this.rcUserId = "";
		SharedPreferencesUtil.deleteUserInfo();
		SharedPreferencesUtil.deleteRCInfo();
		this.setChangeIntent(true);
	}

	/**
	 * 缓存用户信息
	 */
	public void cache(UserInfoBean userInfo, String token){
		this.userInfo = userInfo;
		this.isLogin = true;
		this.rcToken = token;
		this.rcPortrait = userInfo.UserPhotoUrl;
		this.rcUsername = userInfo.NickName;
		this.rcUserId = "u_" + userInfo.UserId.toLowerCase();
		SharedPreferencesUtil.saveUserInfo(userInfo);
		SharedPreferencesUtil.saveRcUserInfo(userInfo, token);
	}

	/**
	 * 当前是否登录
	 * @return
     */
	public boolean isUserLogin(){
		return isLogin;
	}

	public String getUserId() {
		if (userInfo==null)return "";
		return userInfo.UserId;
	}

	public String getUserPhone(){
		if (userInfo==null)return "";
		return userInfo.Phone;
	}

	public String getUserSex(){
		if (userInfo==null)return "";
		if (userInfo.Gender==null){
			return "先生";
		}
		return userInfo.Gender;
	}

	private int msgCount;

	public int getMsgCount() {
		return msgCount;
	}

	public void setMsgCount(int msgCount) {
		this.msgCount = msgCount;
	}

	private boolean isChangeIntent = true;   //是否从意向界面跳转来

	public boolean isChangeIntent() {
		return isChangeIntent;
	}

	public void setChangeIntent(boolean changeIntent) {
		isChangeIntent = changeIntent;
	}

	private int choiceIntent = -1;

	public int getChoiceIntent() {
		return choiceIntent;
	}

	public void setChoiceIntent(int choiceIntent) {
		this.choiceIntent = choiceIntent;
	}

	private ConcurrentHashMap<String, Activity> allActivity;

	public void putActivity(String key, Activity activity){
		if (allActivity==null){
			allActivity = new ConcurrentHashMap<>();
		}
		allActivity.put(key, activity);
	}

	public void closeAll(){
		if (allActivity==null || allActivity.size()==0)return;
		for (Activity item : allActivity.values()){
			if (item!=null)item.finish();
		}
	}

	public Activity getActivityByName(String name){
		if (allActivity==null)return null;
		return allActivity.get(name);
	}

	public void removeActivity(String key){
		if (allActivity!=null){
			allActivity.remove(key);
		}
	}

	public void setNull(){
		dataHolder = null;
	}

	public float getLocationRedius() {
		return (float) locationRedius;
	}

	public void setLocationRedius(float locationRedius) {
		SharedPreferencesUtil.saveFloat(AppContents.LOCATION_REDUIS, (float) locationRedius);
		this.locationRedius = locationRedius;
	}
}
