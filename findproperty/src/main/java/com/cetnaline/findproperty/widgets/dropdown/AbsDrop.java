package com.cetnaline.findproperty.widgets.dropdown;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.cetnaline.findproperty.db.entity.DropBo;

import java.util.ArrayList;
import java.util.List;

/**
 * 下拉菜单
 * Created by guilin on 16/1/13.
 */
public abstract class AbsDrop<T> implements PopupWindow.OnDismissListener {

    protected final View anchor;
    protected PopupWindow popupWindow;
    protected ArrayList<T> arrayList = new ArrayList<>();


    protected DropListener localDropListener;
    private DropListener dropListener = new DropListener() {
        @Override
        public void dropComplete(boolean fromMore, int type, DropBo... dropBos) {
            if (localDropListener != null) {
                localDropListener.dropComplete(fromMore, type, dropBos);
            }
        }

        @Override
        public void dropDismiss(boolean isSelected) {
            if (localDropListener != null) {
                localDropListener.dropDismiss(isSelected);
            }
        }
    };

    public AbsDrop(View anchor, Activity context) {
        this.anchor = anchor;
    }

    protected void initPopWindow(View view, Drawable bg) {

        if (bg==null){
            popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT, true);
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }else {
            popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, true);
            popupWindow.setBackgroundDrawable(bg);
        }
        popupWindow.setOnDismissListener(this);
    }

    /**
     * 初始化
     */
    public abstract void init(List<T> arrayList);

    public void setDropListener(DropListener localDropListener) {
        this.localDropListener = localDropListener;
    }

    /**
     * @param fromMore 是否来源更多
     * @param type     下拉菜单类型
     * @param dropBos  数据
     */
    protected void dropComplete(boolean fromMore, int type, DropBo... dropBos) {
        dismiss();
        dropListener.dropComplete(fromMore, type, dropBos);
    }

    /**
     * 显示
     */
    public void show() {
        selected = false;
        if (popupWindow != null &&
                !popupWindow.isShowing())

            doBeforeShow();

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT){
                popupWindow.showAsDropDown(anchor, 0, 0, Gravity.CENTER_HORIZONTAL);
            }else {
                popupWindow.showAsDropDown(anchor);
            }
    }

    private boolean selected;

    protected void dismiss() {
        if (popupWindow != null)
            selected = true;
            popupWindow.dismiss();
    }

    @Override
    public void onDismiss() {
        localDropListener.dropDismiss(selected);
    }

    protected void doBeforeShow(){

    }
}
