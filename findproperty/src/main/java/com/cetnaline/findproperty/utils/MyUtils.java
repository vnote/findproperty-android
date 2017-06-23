package com.cetnaline.findproperty.utils;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.cetnaline.findproperty.AppContents;
import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.api.request.ApiRequest;
import com.cetnaline.findproperty.entity.bean.StaffListBean;
import com.cetnaline.findproperty.ui.activity.AdviserDetailActivity;
import com.cetnaline.findproperty.ui.activity.ConversationActivity;
import com.cetnaline.findproperty.ui.activity.HouseDetail;
import com.cetnaline.findproperty.ui.activity.HouseList;
import com.cetnaline.findproperty.ui.activity.MainTabActivity;
import com.cetnaline.findproperty.ui.fragment.HouseDetailFragment;
import com.cetnaline.findproperty.ui.fragment.MapFragment;
import com.orhanobut.logger.Logger;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Pattern;

import io.rong.imlib.model.Conversation;

import static android.content.ContentValues.TAG;

/**
 * Created by fanxl2 on 2016/8/1.
 */
public class MyUtils {

	/**
	 * 获取百度地图截图
	 *
	 * @return String
	 */
	public static String getBaiduMapImgUrl(double lng, double lat) {
		return String.format(Locale.CHINA, "http://api.map.baidu.com/staticimage?center=%f,%f&width=480&height=240&zoom=16",
				lng, lat);
	}

	/**
	 * 计算两个经纬度之间的距离 Km
	 *
	 * @param start
	 * @param end
	 * @return
	 */
	public static double getDistance(LatLng start, LatLng end) {
		double lat1 = (Math.PI / 180) * start.latitude;
		double lat2 = (Math.PI / 180) * end.latitude;

		double lon1 = (Math.PI / 180) * start.longitude;
		double lon2 = (Math.PI / 180) * end.longitude;

		//地球半径
		double R = 6371;
		//两点间距离 km，如果想要米的话，结果*1000就可以了
		double d = Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1)) * R;
		return d;
	}

	/**
	 * 获取手机的宽度
	 *
	 * @param activity
	 * @return
	 */
	public static int getPhoneWidth(Activity activity) {
		DisplayMetrics metric = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
		return metric.widthPixels;
	}

	public static int[] getPhoneWidthAndHeight(Activity activity) {
		DisplayMetrics metric = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
		int[] whs = new int[2];
		whs[0] = metric.widthPixels;
		whs[1] = metric.heightPixels;
		return whs;
	}

	public static int getPhoneHeight(Activity activity) {
		DisplayMetrics metric = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
		return metric.heightPixels;
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static double format2(Object num) {
//		NumberFormat numberFormat= NumberFormat.getInstance(Locale.CHINA);
		return new BigDecimal(num + "").setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * 房源面积转换
	 * @param num
	 * @return
	 */
	public static String formatHouseArea(Object num) {
		double area = new BigDecimal(num + "").setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		if (area - (int)area > 0.001) {
			return area + "";
		} else {
			return (int)area + "";
		}
	}

	public static double format1(Object num) {
		return new BigDecimal(num + "").setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public static int format2Integer(Object num) {
		if (num == null || TextUtils.isEmpty(num + "")) return 0;
		double number = Double.parseDouble(num.toString());
		return (int) Math.floor(number);
	}

	public static String format2String(Object num) {
		if (num == null || TextUtils.isEmpty(num + "")) return "0";
		double number = Double.parseDouble(num.toString());
		int intNum = new BigDecimal(number).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
		NumberFormat numberFormat = NumberFormat.getInstance(Locale.CHINA);
		return numberFormat.format(intNum);
	}

	/**
	 * 去经纪人的店铺首页
	 *
	 * @param activity
	 * @param staffNo
	 * @param staffName
	 */
	public static void toStoreHome(Activity activity, String staffNo, String staffName) {
		Intent intent = new Intent(activity, AdviserDetailActivity.class);
		StaffListBean staffBean = new StaffListBean();
		staffBean.StaffNo = staffNo;
		staffBean.CnName = staffName;
		intent.putExtra(AdviserDetailActivity.ADVISER, staffBean);
		activity.startActivity(intent);
	}


	public static final String TALK_FROM_ADVISER_LIST = "0";
	public static final String TALK_FROM_SOURCE_LIST = "1";
	/**
	 * 打开聊天页面
	 * @param activity
	 * @param staffNo 业务员id
	 * @param staffName 业务员名称
	 * @param openConversationType 打开类型（0：业务员列表，1：房源列表）
	 * @param info 默认发送信息内容(标题内容)
	 * @param targetType  咨询房源租售类型
	 * @param targetValue 咨询房源id
	 * @param fullPath 图片路径
	 * @param content 描述信息
	 */
	public static void toStaffTalk(Activity activity, String staffNo, String staffName, String openConversationType , String info, String targetType, String targetValue, String fullPath, String content) {
		Uri uri = Uri.parse("rong://" + activity.getApplicationInfo().processName).buildUpon()
				.appendPath("conversation")
				.appendPath(Conversation.ConversationType.PRIVATE.getName().toLowerCase())
				.appendQueryParameter("targetId", "s_021_" + staffNo.toLowerCase())
				.appendQueryParameter("title", staffName)
				.appendQueryParameter(ConversationActivity.FULL_PATH,fullPath)
				.appendQueryParameter(ConversationActivity.CONTENT,content)
				.appendQueryParameter(ConversationActivity.OPEN_CONVERSATION_TYPE,openConversationType)
				.appendQueryParameter(ConversationActivity.DEFAULT_MESSAGE, info)
				.appendQueryParameter(ConversationActivity.CONVERSATION_TARGET, targetType)
				.appendQueryParameter(ConversationActivity.CONVERSATION_TARGET_VALUE, targetValue)
				.build();
		activity.startActivity(new Intent("android.intent.action.VIEW", uri));
	}

	public static void toCall400(Activity activity, String tel, String staffName) {
		LayoutInflater inflater = activity.getLayoutInflater();
		View linearlayout = inflater.inflate(R.layout.dialog_call_adviser, null);
		Dialog dialog = new Dialog(activity);
		dialog.requestWindowFeature(1);
		dialog.setContentView(linearlayout);
		dialog.show();
		((TextView) linearlayout.findViewById(R.id.show_adviser_phone))
				.setText(tel);
		linearlayout.findViewById(R.id.call_phone).setOnClickListener((view) -> {
			Intent intent = new Intent(Intent.ACTION_CALL);
			Uri data = Uri.parse("tel:" + tel);
			intent.setData(data);
			if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
				return;
			}
			activity.startActivity(intent);
		});
		linearlayout.findViewById(R.id.cancel_call).setOnClickListener((view) -> {
			dialog.dismiss();
		});

		dialog.getWindow().setWindowAnimations(R.style.AlertDialogTheme_App);
		android.view.WindowManager.LayoutParams p = dialog.getWindow().getAttributes();  //获取对话框当前的参数值
		p.width = MyUtils.dip2px(activity, 300);
		dialog.getWindow().setAttributes(p);
	}

	/**
	 * 对话框
	 * @param context
	 * @param layout    对话框布局
	 * @param width     对话框宽度 默认-1
	 * @param height	 对话框高度 默认-1
	 */
	public static Dialog showDiloag(Activity context, int layout, int width, int height, boolean cancelable ,DialogActionListener listener) {
		View layoutView = context.getLayoutInflater().inflate(layout, null);
		Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(1);
		dialog.setContentView(layoutView);
		if (!cancelable) {
			dialog.setCanceledOnTouchOutside(false);
			dialog.setCancelable(false);
		}

		dialog.show();
		if (listener != null) {
			listener.listener(layoutView,dialog);
		}
		dialog.getWindow().setWindowAnimations(R.style.AlertDialogTheme_App);
		android.view.WindowManager.LayoutParams p = dialog.getWindow().getAttributes();  //获取对话框当前的参数值
		if (width > 0) {
			p.width = MyUtils.dip2px(context, width);
		}
		if (height > 0) {
			p.height = MyUtils.dip2px(context, height);
		}
		dialog.getWindow().setAttributes(p);
		return dialog;
	}

	public interface DialogActionListener{
		void listener(View layout, Dialog dialog);
	}

	/**
	 * 显示信息提示框
	 *
	 * @param activity
	 * @param msg      要显示的信息内容
	 */
	public static void showAlertDialog(Activity activity, String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);

		AlertDialog dialog = builder.setMessage(msg)
				.setPositiveButton("确定", (d, which) -> d.dismiss()).create();
		dialog.getWindow().setWindowAnimations(R.style.AlertDialogTheme_App);
		dialog.show();
	}

	/**
	 * 获取当前程序的版本
	 *
	 * @param context
	 * @return
	 */
	public static int getAppVersionCode(Context context) {
		try {
			PackageInfo pkg = context.getPackageManager().getPackageInfo(context.getApplicationContext().getPackageName(), 0);
			return pkg.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static boolean checkPhoneNumber(String phone) {
		String patten = "^1(3[0-9]|4[57]|5[0-35-9]|8[0-9]|70)\\d{8}$";
		Pattern r = Pattern.compile(patten);
		if (r.matcher(phone).matches()) {
			return true;
		}
		return false;
	}

	/**
	 * 删除文件夹下所有文件
	 *
	 * @param path
	 * @return
	 */
	public static boolean deleteFiles(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists() || !file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		if (tempList != null) {
			for (int i = 0; i < tempList.length; i++) {
				if (path.endsWith(File.separator)) {
					temp = new File(path + tempList[i]);
				} else {
					temp = new File(path + File.separator + tempList[i]);
				}
				if (temp.isFile()) {
					temp.delete();
				}
				if (temp.isDirectory()) {
					deleteFiles(path + "/" + tempList[i]);//先删除文件夹里面的文件
					deleteFiles(path + "/" + tempList[i]);//再删除空文件夹
					flag = true;
				}
			}
		}

		return flag;
	}

	/**
	 * 获取当前网络状态
	 *
	 * @param context
	 * @return
	 */
	public static boolean checkNetwork(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo.State wifiState = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		NetworkInfo.State mobileState = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
		if (wifiState != NetworkInfo.State.CONNECTED && mobileState != NetworkInfo.State.CONNECTED) {
			return false;
		}
		return true;
	}

	/**
	 * 获取application中指定的meta-data
	 *
	 * @return 如果没有获取成功(没有对应值，或者异常)，则返回值为空
	 */
	public static String getAppMetaData(Context ctx, String key) {
		if (ctx == null || TextUtils.isEmpty(key)) {
			return null;
		}
		String resultData = null;
		try {
			PackageManager packageManager = ctx.getPackageManager();
			if (packageManager != null) {
				ApplicationInfo applicationInfo = packageManager.getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
				if (applicationInfo != null) {
					if (applicationInfo.metaData != null) {
						resultData = applicationInfo.metaData.getString(key);
					}
				}

			}
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}

		return resultData;
	}

	/**
	 * check NetworkAvailable
	 *
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context.getApplicationContext().getSystemService(
				Context.CONNECTIVITY_SERVICE);
		if (null == manager)
			return false;
		NetworkInfo info = manager.getActiveNetworkInfo();
		if (null == info || !info.isAvailable())
			return false;
		return true;
	}

	/**
	 * 当前应用是否正在运行
	 *
	 * @return
	 */
	public static boolean isAppRunning(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
		boolean isAppRunning = false;

		String MY_PKG_NAME = context.getPackageName();
		for (ActivityManager.RunningTaskInfo info : list) {
			if (info.topActivity.getPackageName().equals(MY_PKG_NAME) || info.baseActivity.getPackageName().equals(MY_PKG_NAME)) {
				isAppRunning = true;
				break;
			}
		}

		return isAppRunning;
	}

	/**
	 * 当前应用是否在后台运行
	 *
	 * @param context
	 * @return
	 */
	public static boolean isRunningBackground(Context context) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();

		for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(context.getPackageName())) {
				if (appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}

	/**
	 * url判断
	 *
	 * @param url
	 * @return
	 */
	public static boolean isWebUrl(String url) {
		String web_pattern = "(http://|ftp://|https://|www){0,1}[^一-龥\\s]*?\\.(com|net|cn|me|tw|fr|org|cc)[^一-龥\\s]*";
		Pattern rweb = Pattern.compile(web_pattern);
		if (rweb.matcher(url).matches()) {
			return true;
		}
		return false;
	}

	/**
	 * 通过url打开页面
	 *
	 * @param context
	 * @param url
	 */
	public static boolean openActivityForUrl(Context context, String url, boolean outContext) {
		if (url.contains("http://sh.centanet.com/m/ershoufang") && url.contains(".html")) {
			String postId = url.substring(url.indexOf("ershoufang/") + 11, url.indexOf(".html"));
			Intent intent = new Intent(context, HouseDetail.class);
			intent.putExtra(MapFragment.HOUSE_TYPE_KEY, MapFragment.HOUSE_TYPE_SECOND);
			intent.putExtra(HouseDetailFragment.HOUSE_ID_KEY, postId);
			if (outContext) {
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			}
			context.startActivity(intent);
			return true;
		} else if (url.contains("http://sh.centanet.com/m/xinfang/lp")) {
			String estExtId = url.substring(url.indexOf("lp-") + 3, url.lastIndexOf("/"));
			Intent intent = new Intent(context, HouseDetail.class);
			intent.putExtra(MapFragment.HOUSE_TYPE_KEY, MapFragment.HOUSE_TYPE_NEW);
			intent.putExtra(HouseDetailFragment.ESTEXT_ID_KEY, estExtId);
			if (outContext) {
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			}
			context.startActivity(intent);
			return true;
		} else if (url.contains("http://sh.centanet.com/m/zufang") && url.contains(".html")) {
			String postId = url.substring(url.indexOf("zufang/") + 7, url.indexOf(".html"));

			Intent intent = new Intent(context, HouseDetail.class);
			intent.putExtra(MapFragment.HOUSE_TYPE_KEY, MapFragment.HOUSE_TYPE_RENT);
			intent.putExtra(HouseDetailFragment.HOUSE_ID_KEY, postId);
			if (outContext) {
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			}
			context.startActivity(intent);
			return true;
		} else if (url.equals("http://sh.centanet.com/m/ershoufang/")) {
			//到二手房列表
			Intent intent = new Intent(context, HouseList.class);
			intent.putExtra(MapFragment.HOUSE_TYPE_KEY, MapFragment.HOUSE_TYPE_SECOND);
			if (outContext) {
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			}
			context.startActivity(intent);
			return true;
		} else if (url.equals("http://sh.centanet.com/m/zufang/")) {
			Intent intent = new Intent(context, HouseList.class);
			intent.putExtra(MapFragment.HOUSE_TYPE_KEY, MapFragment.HOUSE_TYPE_RENT);
			if (outContext) {
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			}
			context.startActivity(intent);
			return true;
		} else if (url.equals("http://sh.centanet.com/m/xinfang/")) {
			Intent intent = new Intent(context, HouseList.class);
			intent.putExtra(MapFragment.HOUSE_TYPE_KEY, MapFragment.HOUSE_TYPE_NEW);
			if (outContext) {
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			}
			context.startActivity(intent);
			return true;
		} else if (url.contains("http://sh.centanet.com/m/jingjiren/")) {
			String staffNo = url.substring(url.toLowerCase().indexOf("-aa") + 1, url.lastIndexOf("/"));

			ApiRequest.getStaffDetail(staffNo)
					.subscribe(staffListBean -> {
						Intent i = new Intent(context, AdviserDetailActivity.class);
						i.putExtra(AdviserDetailActivity.ADVISER, staffListBean);
						if (outContext) {
							i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						}
						context.startActivity(i);
					},throwable -> throwable.printStackTrace());
			return true;
		} else if ("http://sh.centanet.com/m/".equalsIgnoreCase(url)) {
			Intent intent = new Intent(context, MainTabActivity.class);
			intent.putExtra(MainTabActivity.SELECTED_TAB_KEY, MainTabActivity.TAB_HOME);
			if (outContext) {
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			}
			context.startActivity(intent);
			return true;
		}

		return false;
	}

	private final static String ENCODE = "utf-8";

	/**
	 * url解码
	 *
	 * @param str
	 * @return
	 */
	public static String getURLDecoderString(String str) {
		String result = "";
		if (null == str) {
			return "";
		}
		try {
			result = java.net.URLDecoder.decode(str, ENCODE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * url转码
	 *
	 * @param str
	 * @return
	 */
	public static String getURLEncoderString(String str) {
		String result = "";
		if (null == str) {
			return "";
		}
		try {
			result = java.net.URLEncoder.encode(str, ENCODE);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 获取单个文件的MD5值！
	 *
	 * @param file
	 * @return
	 */

	public static String getFileMD5(File file) {
		if (!file.isFile()) {
			return null;
		}
		MessageDigest digest = null;
		FileInputStream in = null;
		byte buffer[] = new byte[1024];
		int len;
		try {
			digest = MessageDigest.getInstance("MD5");
			in = new FileInputStream(file);
			while ((len = in.read(buffer, 0, 1024)) != -1) {
				digest.update(buffer, 0, len);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		BigInteger bigInt = new BigInteger(1, digest.digest());
		return bigInt.toString(16).toUpperCase();
	}

	public static void copyText2Clip(Activity activity, String text){
		ClipboardManager cmb = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
		ClipData cData = ClipData.newPlainText("text", text);
		cmb.setPrimaryClip(cData);
	}

	/**
	 * 获取包名
	 * @param context
	 * @return
	 */
	public static String getLauncherClassName(Context context) {
		PackageManager packageManager = context.getPackageManager();

		Intent intent = new Intent(Intent.ACTION_MAIN);
		// To limit the components this Intent will resolve to, by setting an
		// explicit package name.
		intent.setPackage(context.getPackageName());
		intent.addCategory(Intent.CATEGORY_LAUNCHER);

		// All Application must have 1 Activity at least.
		// Launcher activity must be found!
		ResolveInfo info = packageManager
				.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);

		// get a ResolveInfo containing ACTION_MAIN, CATEGORY_LAUNCHER
		// if there is no Activity which has filtered by CATEGORY_DEFAULT
		if (info == null) {
			info = packageManager.resolveActivity(intent, 0);
		}

		return info.activityInfo.name;
	}


	private static boolean isHwPhone = false;

	/**
	 * 获取华为手机版本号
	 * @return
	 */
	public static boolean isHwPhone(Context context) {
		// Finals 2016-6-14 如果获取过了就不用再获取了，因为读取配置文件很慢
		if (isHwPhone) {
			return isHwPhone;
		}
		Properties properties = new Properties();
		File propFile = new File(Environment.getRootDirectory(), "build.prop");
		FileInputStream fis = null;
		if (propFile != null && propFile.exists()) {
			try {
				fis = new FileInputStream(propFile);
				properties.load(fis);
				fis.close();
				fis = null;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			}
		}
		if (properties.containsKey("ro.build.hw_emui_api_level")) {
//			String valueString = properties.getProperty("ro.build.hw_emui_api_level");
			try {
				PackageManager manager = context.getPackageManager();
				PackageInfo info = manager.getPackageInfo("com.huawei.android.launcher", 0);
				if(info.versionCode>=63029){
					isHwPhone = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return isHwPhone;
			}
		}
		return isHwPhone;
	}

	/**
	 * 将文本中的数字装换为中文
	 * @param str
	 * @return
	 */
	public static String numberForWord(String str) {
		String[] patten = new String[]{"零","一","二","三","四","五","六","七","八","九"};
		String result = "";
		for (int i=0;i<str.length();i++) {
			try {
				int index = Integer.parseInt(str.substring(i,i+1));
				result += patten[index];
			} catch (NumberFormatException e) {
				result += str.substring(i,i+1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}


	/**
	 * 检测应用通知栏是否打开
	 * @param context
	 * @return
	 */
	public  static boolean isNotificationEnable(Context context) {
		String CHECK_OP_NO_THROW = "checkOpNoThrow";
		String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

		AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
		ApplicationInfo appInfo = context.getApplicationInfo();
		String pkg = context.getApplicationContext().getPackageName();
		int uid = appInfo.uid;

		Class appOpsClass = null;
     	/* Context.APP_OPS_MANAGER */
		try {
			appOpsClass = Class.forName(AppOpsManager.class.getName());
			Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE,
					String.class);
			Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);

			int value = (Integer) opPostNotificationValue.get(Integer.class);
			return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 打开应用通知栏设置页面
	 * @param context
	 */
	public static void openNotificationSettingPage(Context context) {

//		try {
//			Intent intent = new Intent("android.intent.action.MAIN");
//			ComponentName comp = new ComponentName("com.android.settings", "com.android.settings.Settings$NotificationFilterActivity");
//			intent.setComponent(comp);
//			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			intent.putExtra("package",context.getPackageName());
//			PackageManager pm = context.getPackageManager();
//			ApplicationInfo ai = null;
//			try {
//				ai = pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
//			} catch (PackageManager.NameNotFoundException e) {
//				e.printStackTrace();
//			}
//			intent.putExtra("uid", ai.uid);
//			context.startActivity(intent);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		//			Intent intent = new Intent();
		String name= Build.MANUFACTURER;

//			intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//			intent.addCategory(Intent.CATEGORY_DEFAULT);
//			intent.setData(Uri.parse("package:" + context.getPackageName()));
//			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//			intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//			context.startActivity(intent);

		if("HUAWEI".equals(name)){
			goHuaWeiMainager(context);
		}else if ("vivo".equals(name)){
			goVivoMainager(context);
		}else if ("OPPO".equals(name)){
			goOppoMainager(context);
		}else if ("Coolpad".equals(name)){
			goCoolpadMainager(context);
		}else if ("Meizu".equals(name)){
			goMeizuMainager(context);
			// getAppDetailSettingIntent();
		}else if ("Xiaomi".equals(name)){
			goXiaoMiMainager(context);
		}else if ("samsung".equals(name)){
			goSangXinMainager(context);
		}else{
			goIntentSetting(context);
		}

//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {// 运行系统在5.x环境使用\
//		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BASE) {
//			goIntentSetting(context);
//		}
	}


	private static void goHuaWeiMainager(Context context) {
		try {
			Intent intent = new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.addviewmonitor.AddViewMonitorActivity");
//			ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");
			intent.setComponent(comp);
			context.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
			goIntentSetting(context);
		}
	}
	private static void goXiaoMiMainager(Context context){
		try {
			Intent localIntent = new Intent("miui.intent.action.APP_PERM_EDITOR");
			localIntent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
			localIntent.putExtra("extra_pkgname",context.getPackageName());
			context.startActivity(localIntent);
		} catch (ActivityNotFoundException localActivityNotFoundException) {
			goIntentSetting(context);
		}
	}
	private static void goMeizuMainager(Context context){
		try {
			Intent intent=new Intent("com.meizu.safe.security.SHOW_APPSEC");
			intent.addCategory(Intent.CATEGORY_DEFAULT);
			intent.putExtra("packageName", context.getPackageName());
			context.startActivity(intent);
		} catch (ActivityNotFoundException localActivityNotFoundException) {
			localActivityNotFoundException.printStackTrace();
			goIntentSetting(context);
		}
	}
	private static void goSangXinMainager(Context context){
		//三星4.3可以直接跳转
		goIntentSetting(context);
	}
	private static void goIntentSetting(Context context){
		Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
		Uri uri = Uri.fromParts("package",context.getPackageName(), null);
		intent.setData(uri);
		try {
			context.startActivity(intent);
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	private static void goOppoMainager(Context context){
		doStartApplicationWithPackageName("com.coloros.safecenter",context);
	}

	private static void goCoolpadMainager(Context context){
		doStartApplicationWithPackageName("com.yulong.android.security:remote",context);
      /*  Intent openQQ = getPackageManager().getLaunchIntentForPackage("com.yulong.android.security:remote");
        startActivity(openQQ);*/
	}
	//vivo
	private static void goVivoMainager(Context context){
		doStartApplicationWithPackageName("com.bairenkeji.icaller",context);
     /*   Intent openQQ = getPackageManager().getLaunchIntentForPackage("com.vivo.securedaemonservice");
        startActivity(openQQ);*/
	}

	private static Intent getAppDetailSettingIntent(Context context) {
		Intent localIntent = new Intent();
		localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if (Build.VERSION.SDK_INT >= 9) {
			localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
			localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
		} else if (Build.VERSION.SDK_INT <= 8) {
			localIntent.setAction(Intent.ACTION_VIEW);
			localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
			localIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
		}
		return localIntent;
	}

	private static void doStartApplicationWithPackageName(String packagename,Context context) {

		// 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
		PackageInfo packageinfo = null;
		try {
			packageinfo = context.getPackageManager().getPackageInfo(packagename, 0);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		if (packageinfo == null) {
			return;
		}
		// 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
		Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
		resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		resolveIntent.setPackage(packageinfo.packageName);
		// 通过getPackageManager()的queryIntentActivities方法遍历
		List<ResolveInfo> resolveinfoList = context.getPackageManager()
				.queryIntentActivities(resolveIntent, 0);
		for (int i = 0; i < resolveinfoList.size(); i++) {
			Logger.i("MainActivity",resolveinfoList.get(i).activityInfo.packageName+resolveinfoList.get(i).activityInfo.name);
		}
		ResolveInfo resolveinfo = resolveinfoList.iterator().next();
		if (resolveinfo != null) {
			// packagename = 参数packname
			String packageName = resolveinfo.activityInfo.packageName;
			// 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
			String className = resolveinfo.activityInfo.name;
			// LAUNCHER Intent
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			// 设置ComponentName参数1:packagename参数2:MainActivity路径
			ComponentName cn = new ComponentName(packageName, className);
			intent.setComponent(cn);
			try {
				context.startActivity(intent);
			}catch (Exception e){
				goIntentSetting(context);
				e.printStackTrace();
			}
		}
	}

	/**
	 * 检测应用是否打开消息通知
	 */
	public static void checkNotificationSetting(Context context) {
		if (!DataHolder.getInstance().isCloseNotice() && !MyUtils.isNotificationEnable(context) && !DataHolder.getInstance().isNotePushEnable()) {
			DataHolder.getInstance().setNotePushEnable(true);
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			View linearlayout = inflater.inflate(R.layout.dialog_setting_notification, null);
			Dialog dialog = new Dialog(context);
			dialog.requestWindowFeature(1);
			dialog.setContentView(linearlayout);
			dialog.show();
			((TextView) linearlayout.findViewById(R.id.show_adviser_phone))
					.setText("应用消息通知已关闭，前往打开？");
			linearlayout.findViewById(R.id.call_phone).setOnClickListener((view) -> {
				MyUtils.openNotificationSettingPage(context);
				dialog.dismiss();
			});
			linearlayout.findViewById(R.id.cancel_call).setOnClickListener((view) -> {
				dialog.dismiss();
			});

			dialog.getWindow().setWindowAnimations(R.style.AlertDialogTheme_App);
			android.view.WindowManager.LayoutParams p = dialog.getWindow().getAttributes();  //获取对话框当前的参数值
			p.width = MyUtils.dip2px(context, 300);
			dialog.getWindow().setAttributes(p);
		}
	}

	/**
	 * 表情转换
	 * @param content
	 * @return
	 */
	public static String translateEmoji(String content) {
		for (int i = 0; i < AppContents.wx_define_code.length; i++) {
			if (content.contains(AppContents.wx_define_code[i])) {
				content = content.replace(AppContents.wx_define_code[i],AppContents.rc_define_code[i]);
			}
		}
		return content;
	}

	/**
	 * 获取设备唯一标识符
	 * @param context
	 * @return
	 */
	public static String getDeviceId(Context context) {
		TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		return  tm.getDeviceId();
	}

	/**
	 * 获取录音时长
	 * @param url
	 * @return
	 * @throws IOException
     */
	public static long getAmrDuration(String url) throws IOException {
		File file = new File(url);
		long duration = -1;
		int[] packedSize = { 12, 13, 15, 17, 19, 20, 26, 31, 5, 0, 0, 0, 0, 0, 0, 0 };
		RandomAccessFile randomAccessFile = null;
		try {
			randomAccessFile = new RandomAccessFile(file, "rw");
			long length = file.length();//文件的长度
			int pos = 6;//设置初始位置
			int frameCount = 0;//初始帧数
			int packedPos = -1;
			byte[] datas = new byte[1];//初始数据值
			while (pos <= length) {
				randomAccessFile.seek(pos);
				if (randomAccessFile.read(datas, 0, 1) != 1) {
					duration = length > 0 ? ((length - 6) / 650) : 0;
					break;
				}
				packedPos = (datas[0] >> 3) & 0x0F;
				pos += packedSize[packedPos] + 1;
				frameCount++;
			}
			duration += frameCount * 20;//帧数*20
		} finally {
			if (randomAccessFile != null) {
				randomAccessFile.close();
			}
		}
		return duration;
	}

	/**
	 * apk保存目录[downloads文件夹]
	 */
	public static String getApkCacheDir() {
		File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		if (!dir.exists()) {
			if (!dir.mkdirs()) {
				Log.e(TAG, "getApkCacheDir: 下载地址创建失败");
			}
		}
		return dir.getAbsolutePath();
	}


	/**
	 * 获取汉字串拼音首字母，英文字符不变
	 * @param chinese 汉字串
	 * @return 汉语拼音首字母
	 */
	public static String getFirstSpell(String chinese) {
		StringBuffer pybf = new StringBuffer();
		char[] arr = chinese.toCharArray();
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] > 128) {
				try {
					String[] temp = PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat);
					if (temp != null) {
						pybf.append(temp[0].charAt(0));
					}
				} catch (BadHanyuPinyinOutputFormatCombination e) {
					e.printStackTrace();
				}
			} else {
				pybf.append(arr[i]);
			}
		}
		return pybf.toString().replaceAll("\\W", "").trim();
	}

}



























