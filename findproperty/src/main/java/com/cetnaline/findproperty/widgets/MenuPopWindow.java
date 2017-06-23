package com.cetnaline.findproperty.widgets;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.ui.activity.AdviserListActivity;
import com.cetnaline.findproperty.ui.activity.StoreSearchActivity;
import com.cetnaline.findproperty.utils.MyUtils;

/**
 * Created by diaoqf on 2016/10/18.
 */

public class MenuPopWindow extends PopupWindow {

    private View contentView;
    private Context mContext;
    private ContentViewListener listener;

    public MenuPopWindow(final Activity context, int layoutId, ContentViewListener listener, int width, int height){
        mContext = context;
        this.listener = listener;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        contentView = inflater.inflate(layoutId, null);

        // 设置SelectPicPopupWindow的View
        this.setContentView(contentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        if (width <= 0) {
            this.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        } else {
            this.setWidth(MyUtils.dip2px(context, width));
        }
        if (height <= 0) {
            // 设置SelectPicPopupWindow弹出窗体的高
            this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        } else {
            this.setHeight(MyUtils.dip2px(context, height));
        }

        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);
        // mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        // 设置SelectPicPopupWindow弹出窗体动画效果
//        this.setAnimationStyle(R.style.MenuPopWindowStyle);
        if (listener != null) {
            listener.setAction(contentView, this);
        }
    }

    public MenuPopWindow(final Activity context, int layoutId, ContentViewListener listener) {
        this(context,layoutId,listener,0,0);
    }

    /**
     * x,y是相对parent的偏移位置
     * @param parent
     * @param x
     * @param y
     */
    public void showPopupWindow(View parent, int x , int y) {
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
            int[] location = new int[2];
            parent.getLocationOnScreen(location);
            showAtLocation(parent, Gravity.NO_GRAVITY, location[0] + x , location[1] + y);
//            this.showAsDropDown(parent, parent.getLayoutParams().width / 2, 18);
        } else {
            this.dismiss();
        }
    }

    public void setListener(ContentViewListener listener) {
        this.listener = listener;
    }

    public interface ContentViewListener {
        void setAction(View contentView, MenuPopWindow window);
    }

}
