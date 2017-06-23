package com.cetnaline.findproperty.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.cetnaline.findproperty.AppContents;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.entity.bean.UserInfoBean;

import java.util.Set;

/**
 * app 基本缓存数据存储工具
 * Created by diaoqf on 2016/7/8.
 */
public class SharedPreferencesUtil {

    //缓存用户是否登录状态
    public static final String IS_LOGIN = "isLogin";

    private static Context mContext;

    private static SharedPreferences mSharedPreferences = null;
    private static SharedPreferences.Editor mSharedEditor;

    public static boolean isInited(){
        return mSharedPreferences != null;
    }

    public static void init(Context context){
        mContext = context;
        if (mSharedPreferences == null) {
            mSharedPreferences = context.getSharedPreferences(AppContents.SP_NAME, Activity.MODE_PRIVATE|Activity.MODE_MULTI_PROCESS);
            mSharedEditor = mSharedPreferences.edit();
        }
    }

    public static void saveString(String name, String value) {
        mSharedEditor.putString(name, value);
        mSharedEditor.commit();
    }

    public static void saveInt(String name, int value) {
        mSharedEditor.putInt(name, value);
        mSharedEditor.commit();
    }

    public static void saveFloat(String name, float value) {
        mSharedEditor.putFloat(name, value);
        mSharedEditor.commit();
    }

    public static void saveLong(String name, Long value) {
        mSharedEditor.putLong(name, value);
        mSharedEditor.commit();
    }

    public static void saveBoolean(String name, boolean value) {
        mSharedEditor.putBoolean(name, value);
        mSharedEditor.commit();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void saveStringSet(String name, Set<String> sets){
        mSharedEditor.putStringSet(name, sets);
        mSharedEditor.commit();
    }

    public static String getString(String name) {
        return mSharedPreferences.getString(name, "");
    }

    public static float getFloat(String name) {
        return mSharedPreferences.getFloat(name,0.0f);
    }

    public static long getLong(String name) {
        return mSharedPreferences.getLong(name, 0);
    }

    public static boolean getBoolean(String name) {
        return mSharedPreferences.getBoolean(name,false);
    }

    public static int getInt(String name) {
        return mSharedPreferences.getInt(name, -1);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static Set<String> getStringSet(String name) {
        return mSharedPreferences.getStringSet(name,null);
    }

    /**
     * 保存用户登录信息
     * @return
     */
    public static boolean saveUserInfo(UserInfoBean bean){
        saveBoolean(AppContents.USER_IS_LOGIN, true);
        saveString(AppContents.USER_INFO_USERID,bean.UserId);
        if (bean.NickName==null||bean.NickName.equals("")) {
            bean.NickName = "尊敬的用户";
        }
        saveString(AppContents.USER_INFO_NICKNAME,bean.NickName);
        saveString(AppContents.USER_INFO_PASSWORD,bean.PassWord);
        saveString(AppContents.USER_INFO_EMAIL,bean.Email);
        saveString(AppContents.USER_INFO_PHONE,bean.Phone);
        saveString(AppContents.USER_INFO_SINAACCOUNT,bean.SinaAccount);
        saveString(AppContents.USER_INFO_QQACCOUNT,bean.QQAccount);
        saveString(AppContents.USER_INFO_WEIXINACCOUNT,bean.WeiXinAccount);
        saveString(AppContents.USER_INFO_USERPHOTOURL,bean.UserPhotoUrl);
        saveString(AppContents.USER_INFO_SEX, bean.Gender);
        saveString(AppContents.SH_CK_USER, bean.sh_ck_user);
        saveString(AppContents.SH_CK_USER_AUK, bean.sh_ck_user_auk);

        return true;
    }

    /**
     * 保存融云用户信息
     * @param bean
     * @return
     */
    public static boolean saveRcUserInfo(UserInfoBean bean, String token) {
        saveString(AppContents.RONG_CLOUD_PORTRAIT, bean.UserPhotoUrl);
        if (token != null) {
            saveString(AppContents.RONG_CLOUD_TOKEN, token);
        }

        saveInt(AppContents.RONG_CLOUD_UNREAD_COUNT, 0);
        saveString(AppContents.RONG_CLOUD_USERID,"u_"+bean.UserId.toLowerCase());
        saveString(AppContents.RONG_CLOUD_USERNAME, bean.NickName == null ? mContext.getResources().getString(R.string.login_default_name):bean.NickName);
        saveString(AppContents.RONG_CLOUD_PORTRAIT, bean.UserPhotoUrl);

        return true;
    }

    public static UserInfoBean getUserInfo(){
        UserInfoBean user = new UserInfoBean();
        user.UserId = getString(AppContents.USER_INFO_USERID);
        String name = getString(AppContents.USER_INFO_NICKNAME);
        user.NickName = name == "" ? "尊敬的用户": name;
        user.PassWord = getString(AppContents.USER_INFO_PASSWORD);
        user.Email = getString(AppContents.USER_INFO_EMAIL);
        user.Phone = getString(AppContents.USER_INFO_PHONE);
        user.SinaAccount = getString(AppContents.USER_INFO_SINAACCOUNT);
        user.QQAccount = getString(AppContents.USER_INFO_QQACCOUNT);
        user.WeiXinAccount = getString(AppContents.USER_INFO_WEIXINACCOUNT);
        user.UserPhotoUrl = getString(AppContents.USER_INFO_USERPHOTOURL);
        user.Gender = getString(AppContents.USER_INFO_SEX);
        return user;
    }

    /**
     * 清除用户登录信息
     * @return
     */
    public static boolean deleteUserInfo(){
        saveBoolean(AppContents.USER_IS_LOGIN,false);
        mSharedEditor.remove(AppContents.USER_INFO_USERID);
        mSharedEditor.remove(AppContents.USER_INFO_NICKNAME);
        mSharedEditor.remove(AppContents.USER_INFO_PASSWORD);
        mSharedEditor.remove(AppContents.USER_INFO_EMAIL);
        mSharedEditor.remove(AppContents.USER_INFO_PHONE);
        mSharedEditor.remove(AppContents.USER_INFO_SINAACCOUNT);
        mSharedEditor.remove(AppContents.USER_INFO_QQACCOUNT);
        mSharedEditor.remove(AppContents.USER_INFO_WEIXINACCOUNT);
        mSharedEditor.remove(AppContents.USER_INFO_USERPHOTOURL);
        mSharedEditor.remove(AppContents.USER_INFO_SEX);
        mSharedEditor.remove(AppContents.SH_CK_USER);
        mSharedEditor.remove(AppContents.SH_CK_USER_AUK);
        mSharedEditor.commit();
        return true;
    }

    /**
     * 清除融云信息
     * @return
     */
    public static boolean deleteRCInfo(){
        mSharedEditor.remove(AppContents.RONG_CLOUD_PORTRAIT);
        mSharedEditor.remove(AppContents.RONG_CLOUD_TOKEN);
        mSharedEditor.remove(AppContents.RONG_CLOUD_UNREAD_COUNT);
        mSharedEditor.remove(AppContents.RONG_CLOUD_USERID);
        mSharedEditor.remove(AppContents.RONG_CLOUD_USERNAME);
        mSharedEditor.commit();
        return true;
    }

    /**
     * 保存短信验证码
     * @param code
     */
    public static void saveCode(String code){
        saveLong(AppContents.SMS_GET_TIME, System.currentTimeMillis());
        saveString(AppContents.SMS_CODE, code);
    }

    /**
     * 获取缓存的有效验证码
     * @return
     */
    public static String getCode(){
        long current = System.currentTimeMillis();
        if (current - getLong(AppContents.SMS_GET_TIME) < 30*60*1000) {
            return getString(AppContents.SMS_CODE);
        } else {
            mSharedEditor.remove(AppContents.SMS_CODE);
            mSharedEditor.commit();
            return null;
        }
    }
}



















