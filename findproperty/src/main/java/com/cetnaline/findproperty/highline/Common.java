package com.cetnaline.findproperty.highline;

import android.app.Activity;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cetnaline.findproperty.utils.StatusBarCompat;

/**
 * Created by binIoter
 */
class Common {
  /**
   * 设置Component
   */
  static View componentToView(LayoutInflater inflater, Component c) {
    View view = c.getView(inflater);
    final MaskView.LayoutParams lp = new MaskView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT);
    lp.offsetX = c.getXOffset();
    lp.offsetY = c.getYOffset();
    lp.targetAnchor = c.getAnchor();
    lp.targetParentPosition = c.getFitPosition();
    view.setLayoutParams(lp);
    return view;
  }

  /**
   * Rect在屏幕上去掉状态栏高度的绝对位置
   */
  static Rect getViewAbsRect(View view, int parentX, int parentY, Activity activity) {
    int[] loc = new int[2];
    view.getLocationInWindow(loc);
    int top = loc[1] + StatusBarCompat.getStatusBarHeight(activity);
    int bottom = loc[1] + view.getMeasuredHeight() + StatusBarCompat.getStatusBarHeight(activity);
    Rect rect = new Rect();
    rect.set(loc[0], top, loc[0] + view.getMeasuredWidth(), bottom);
    rect.offset(-parentX, -parentY);
    return rect;
  }
}
