package com.cetnaline.findproperty.api.bean;

/**
 * Created by fanxl2 on 2016/8/24.
 */
public class AppUpdateBo {


	/**
	 * AndroidVerCode : 18
	 * AndroidLimit : 1
	 * AndroidTitle : Android强制升级提示信息
	 * AndroidMessage : 1.在电脑中打开iTune并把iPhone链接到电脑 2.打开iTunes，当成功识别出iPhone后，请点击iTunes界面左上角栏目中的“音乐”，在此会看到相关的音乐文件了． 3.现在我们打开itunes中的“文件”再找到“将文件添加到资料库”和“将文件夹添加到资料库”这两个选项。
	 * AndroidUrl : http://static.centanet.com/app/androidcentaline.apk
	 * IOSVerCode : 26
	 * IOSLimit : 1
	 * IOSTitle : IOS强制升级提示信息
	 * IOSMessage : 1.在电脑中打开iTune并把iPhone链接到电脑 2.打开iTunes，当成功识别出iPhone后，请点击iTunes界面左上角栏目中的“音乐”，在此会看到相关的音乐文件了． 3.现在我们打开itunes中的“文件”再找到“将文件添加到资料库”和“将文件夹添加到资料库”这两个选项。
	 * IOSUrl : https://itunes.apple.com/cn/app/zhong-yuan-zhao-fang/id880575384?mt=8
	 */

	private String AndroidLimitCode;
	private int AndroidVerCode;
	private int AndroidLimit;
	private String AndroidTitle;
	private String AndroidMessage;
	private String AndroidUrl;
	private String AndroidMD5;
	private String IOSVerCode;
	private int IOSLimit;
	private String IOSTitle;
	private String IOSMessage;
	private String IOSUrl;
	private int IsShowDiagoAndroid;

	public int getAndroidVerCode() {
		return AndroidVerCode;
	}

	public void setAndroidVerCode(int AndroidVerCode) {
		this.AndroidVerCode = AndroidVerCode;
	}

	public int getAndroidLimit() {
		return AndroidLimit;
	}

	public void setAndroidLimit(int AndroidLimit) {
		this.AndroidLimit = AndroidLimit;
	}

	public String getAndroidTitle() {
		return AndroidTitle;
	}

	public void setAndroidTitle(String AndroidTitle) {
		this.AndroidTitle = AndroidTitle;
	}

	public String getAndroidMessage() {
		return AndroidMessage;
	}

	public void setAndroidMessage(String AndroidMessage) {
		this.AndroidMessage = AndroidMessage;
	}

	public String getAndroidUrl() {
		return AndroidUrl;
	}

	public void setAndroidUrl(String AndroidUrl) {
		this.AndroidUrl = AndroidUrl;
	}

	public String getIOSVerCode() {
		return IOSVerCode;
	}

	public void setIOSVerCode(String IOSVerCode) {
		this.IOSVerCode = IOSVerCode;
	}

	public int getIOSLimit() {
		return IOSLimit;
	}

	public void setIOSLimit(int IOSLimit) {
		this.IOSLimit = IOSLimit;
	}

	public String getIOSTitle() {
		return IOSTitle;
	}

	public void setIOSTitle(String IOSTitle) {
		this.IOSTitle = IOSTitle;
	}

	public String getIOSMessage() {
		return IOSMessage;
	}

	public void setIOSMessage(String IOSMessage) {
		this.IOSMessage = IOSMessage;
	}

	public String getIOSUrl() {
		return IOSUrl;
	}

	public void setIOSUrl(String IOSUrl) {
		this.IOSUrl = IOSUrl;
	}

	public String getAndroidMD5() {
		return AndroidMD5;
	}

	public void setAndroidMD5(String androidMD5) {
		AndroidMD5 = androidMD5;
	}

	public String getAndroidLimitCode() {
		return AndroidLimitCode;
	}

	public void setAndroidLimitCode(String androidLimitCode) {
		AndroidLimitCode = androidLimitCode;
	}

	public int getIsShowDiagoAndroid() {
		return IsShowDiagoAndroid;
	}

	public void setIsShowDiagoAndroid(int isShowDiagoAndroid) {
		IsShowDiagoAndroid = isShowDiagoAndroid;
	}
}
