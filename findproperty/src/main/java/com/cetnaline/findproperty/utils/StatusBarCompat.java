package com.cetnaline.findproperty.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 状态栏的工具类
 * Created by fanxl2 on 2016/7/21.
 */
public class StatusBarCompat {

	private static final int COLOR_TRANSLUCENT = Color.parseColor("#00000000");

	public static final int DEFAULT_COLOR_ALPHA = 112;

	public static void translucentStatusBar(Activity activity, boolean hideStatusBarBackground){
		translucentStatusBar(activity, hideStatusBarBackground, true);
	}

	/**
	 * 设置为全屏模式，延伸到顶部的状态栏
	 * @param hideStatusBarBackground hide status bar alpha Background when SDK > 21, true if hide it
	 */
	public static void translucentStatusBar(Activity activity, boolean hideStatusBarBackground, boolean changeStatusColor) {
		Window window = activity.getWindow();
		ViewGroup mContentView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);

		//set child View not fill the system window
		View mChildView = mContentView.getChildAt(0);
		if (mChildView != null) {
			ViewCompat.setFitsSystemWindows(mChildView, false);
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			int statusBarHeight = getStatusBarHeight(activity);

			//First translucent status bar.
			window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				//After LOLLIPOP just set LayoutParams.
				window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
				if (hideStatusBarBackground) {
					window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
					if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
						window.setStatusBarColor(COLOR_TRANSLUCENT);
					}else {
						window.setStatusBarColor(Color.parseColor("#80000000"));
					}
				} else {
					window.setStatusBarColor(calculateStatusBarColor(COLOR_TRANSLUCENT, DEFAULT_COLOR_ALPHA));
				}
				//must call requestApplyInsets, otherwise it will have space in screen bottom
				if (mChildView != null) {
					ViewCompat.requestApplyInsets(mChildView);
				}

				//设置状态栏字体颜色
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
					if(MIUISetStatusBarLightMode(activity.getWindow(), changeStatusColor)){
						//先判断是不是MIUI
						activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
					}else if(FlymeSetStatusBarLightMode(activity.getWindow(), changeStatusColor)){
						//再判断是不是Flyme
						activity.getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
					}else {
						//最后判断是不是6.0以上系统
						if (changeStatusColor){
							activity.getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
						}else {
							activity.getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
						}
					}
				}
			} else {
				ViewGroup mDecorView = (ViewGroup) window.getDecorView();
				if (mDecorView.getTag() != null && mDecorView.getTag() instanceof Boolean && (Boolean)mDecorView.getTag()) {
					mChildView = mDecorView.getChildAt(0);
					//remove fake status bar view.
					mContentView.removeView(mChildView);
					mChildView = mContentView.getChildAt(0);
					if (mChildView != null) {
						FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mChildView.getLayoutParams();
						//cancel the margin top
						if (lp != null && lp.topMargin >= statusBarHeight) {
							lp.topMargin -= statusBarHeight;
							mChildView.setLayoutParams(lp);
						}
					}
					mDecorView.setTag(false);
				}
			}
		}
	}

	/**
	 * 设置状态栏字体图标为深色，需要MIUIV6以上
	 * @param window 需要设置的窗口
	 * @param dark 是否把状态栏字体及图标颜色设置为深色
	 * @return  boolean 成功执行返回true
	 *
	 */
	public static boolean MIUISetStatusBarLightMode(Window window, boolean dark) {
		boolean result = false;
		if (window != null) {
			Class clazz = window.getClass();
			try {
				int darkModeFlag = 0;
				Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
				Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
				darkModeFlag = field.getInt(layoutParams);
				Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
				if(dark){
					extraFlagField.invoke(window,darkModeFlag,darkModeFlag);//状态栏透明且黑色字体
				}else{
					extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
				}
				result=true;
			}catch (Exception e){

			}
		}
		return result;
	}

	/**
	 * 设置状态栏图标为深色和魅族特定的文字风格
	 * 可以用来判断是否为Flyme用户
	 * @param window 需要设置的窗口
	 * @param dark 是否把状态栏字体及图标颜色设置为深色
	 * @return  boolean 成功执行返回true
	 *
	 */
	public static boolean FlymeSetStatusBarLightMode(Window window, boolean dark) {
		boolean result = false;
		if (window != null) {
			try {
				WindowManager.LayoutParams lp = window.getAttributes();
				Field darkFlag = WindowManager.LayoutParams.class
						.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
				Field meizuFlags = WindowManager.LayoutParams.class
						.getDeclaredField("meizuFlags");
				darkFlag.setAccessible(true);
				meizuFlags.setAccessible(true);
				int bit = darkFlag.getInt(null);
				int value = meizuFlags.getInt(lp);
				if (dark) {
					value |= bit;
				} else {
					value &= ~bit;
				}
				meizuFlags.setInt(lp, value);
				window.setAttributes(lp);
				result = true;
			} catch (Exception e) {

			}
		}
		return result;
	}


	/**
	 * 设置状态栏着色 并设置透明度
	 * @param statusColor color 着色颜色值
	 * @param alpha 0 - 255 透明度 0为透明 255为不透明
	 */
	public static void setStatusBarColor(Activity activity, int statusColor, int alpha) {
		setStatusBarColor(activity, calculateStatusBarColor(statusColor, alpha));
	}

	/**
	 * 设置状态栏着色
	 * @param activity
	 * @param statusColor 着色的颜色值
	 */
	public static void setStatusBarColor(Activity activity, int statusColor) {
		Window window = activity.getWindow();
		ViewGroup mContentView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			//First translucent status bar.
			window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {   //版本大于5.0
				//After LOLLIPOP not translucent status bar
				window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
				//Then call setStatusBarColor.
				window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
				window.setStatusBarColor(statusColor);
				//set child View not fill the system window
				View mChildView = mContentView.getChildAt(0);
				if (mChildView != null) {
					ViewCompat.setFitsSystemWindows(mChildView, true);
				}
				//如果设置的状态栏是透明的，且背景是白色的，设置系统状态为深色
				if ((statusColor==Color.TRANSPARENT || statusColor==Color.WHITE)) {
//					window.getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
					if(MIUISetStatusBarLightMode(activity.getWindow(), true)){
						//先判断是不是MIUI
						activity.getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
					}else if(FlymeSetStatusBarLightMode(activity.getWindow(), true)){
						//再判断是不是Flyme
						activity.getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
					}else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
						//最后判断是不是6.0以上系统
						activity.getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
					}else {
						//After LOLLIPOP not translucent status bar
//						window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
						//Then call setStatusBarColor.
//						window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
						window.setStatusBarColor(Color.parseColor("#80000000"));
					}
				}
			} else {												//版本4.4~5.0
				ViewGroup mDecorView = (ViewGroup) window.getDecorView();
				if (mDecorView.getTag() != null && mDecorView.getTag() instanceof Boolean && (Boolean)mDecorView.getTag()) {
					//如果已经有了假的statusbar
					View mStatusBarView = mDecorView.getChildAt(0);
					if (mStatusBarView != null) {
						mStatusBarView.setBackgroundColor(statusColor);
					}
				} else {
					int statusBarHeight = getStatusBarHeight(activity);
					//页面内容留出margin给假的statusbar
					View mContentChild = mContentView.getChildAt(0);
					if (mContentChild != null) {
						ViewCompat.setFitsSystemWindows(mContentChild, false);
						FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mContentChild.getLayoutParams();
						lp.topMargin += statusBarHeight;
						mContentChild.setLayoutParams(lp);
					}
					//添加一个假的statusbar
					View mStatusBarView = new View(activity);
					FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight);
					layoutParams.gravity = Gravity.TOP;
					mStatusBarView.setLayoutParams(layoutParams);
					mStatusBarView.setBackgroundColor(statusColor);
					mDecorView.addView(mStatusBarView, 0);
					mDecorView.setTag(true);
				}
			}
		}
	}

	//计算颜色的透明度 Get alpha color
	private static int calculateStatusBarColor(int color, int alpha) {
		float a = 1 - alpha / 255f;
		int red = color >> 16 & 0xff;
		int green = color >> 8 & 0xff;
		int blue = color & 0xff;
		red = (int) (red * a + 0.5);
		green = (int) (green * a + 0.5);
		blue = (int) (blue * a + 0.5);
		return 0xff << 24 | red << 16 | green << 8 | blue;
	}

	//Get status bar height
	public static int getStatusBarHeight(Context context) {
		int result = 0;
		int resId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resId > 0) {
			result = context.getResources().getDimensionPixelOffset(resId);
		}
		return result;
	}

	//get toolbar height
	public static int getToolBarHeight(Context context){
		//测量actionBarHeight
		TypedValue tv = new TypedValue();
		int actionBarHeight = 0;
		if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)){
			actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
		}
		return actionBarHeight;
	}


}
