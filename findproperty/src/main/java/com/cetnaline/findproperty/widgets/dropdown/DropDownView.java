package com.cetnaline.findproperty.widgets.dropdown;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ArrayRes;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.db.entity.DropBo;

import java.util.ArrayList;
import java.util.List;

/**
 * 下拉view
 * Created by guilin on 16/1/12.
 */
public class DropDownView extends LinearLayout implements View.OnClickListener {

    private LinearLayout ll_drop_fir, ll_drop_sec, ll_drop_thi, ll_drop_fou;
    private AppCompatCheckedTextView atv_drop_fir, atv_drop_sec, atv_drop_thi, atv_drop_fou;

    private List<AppCompatCheckedTextView> tabsTitles;

    private String[] defaultTabs;//默认标签文本
    private ArrayList<AbsDrop> absDropList = new ArrayList<>();
    private DropCompleteListener dropCompleteListener;
    private DropListener dropListener1 = new DropListener() {
        @Override
        public void dropComplete(boolean fromMore, int type, DropBo... dropBos) {
            if (dropCompleteListener != null) {
                dropCompleteListener.complete(0, fromMore, type, dropBos);
            }
        }

        @Override
        public void dropDismiss(boolean isSelected) {
            tabsTitles.get(checkedPosition).setChecked(false);
            setSelectedTextColor(checkedPosition, isSelected);
        }
    };
    private DropListener dropListener2 = new DropListener() {
        @Override
        public void dropComplete(boolean fromMore, int type, DropBo... dropBos) {
            if (dropCompleteListener != null) {
                dropCompleteListener.complete(1, fromMore, type, dropBos);
            }
        }

        @Override
        public void dropDismiss(boolean isSelected) {
            tabsTitles.get(checkedPosition).setChecked(false);
            setSelectedTextColor(checkedPosition, isSelected);
        }
    };
    private DropListener dropListener3 = new DropListener() {
        @Override
        public void dropComplete(boolean fromMore, int type, DropBo... dropBos) {
            if (dropCompleteListener != null) {
                dropCompleteListener.complete(2, fromMore, type, dropBos);
            }
        }

        @Override
        public void dropDismiss(boolean isSelected) {
            tabsTitles.get(checkedPosition).setChecked(false);
            setSelectedTextColor(checkedPosition, isSelected);
        }
    };
    private DropListener dropListener4 = new DropListener() {
        @Override
        public void dropComplete(boolean fromMore, int type, DropBo... dropBos) {
            if (dropCompleteListener != null) {
                dropCompleteListener.complete(3, fromMore, type, dropBos);
            }
        }

        @Override
        public void dropDismiss(boolean isSelected) {
            tabsTitles.get(checkedPosition).setChecked(false);
            setSelectedTextColor(checkedPosition, isSelected);
        }
    };

    public DropDownView(Context context) {
        this(context, null);
    }

    public DropDownView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DropDownView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, R.layout.layout_dropdown, this);
        ll_drop_fir = (LinearLayout) findViewById(R.id.ll_drop_fir);
        ll_drop_sec = (LinearLayout) findViewById(R.id.ll_drop_sec);
        ll_drop_thi = (LinearLayout) findViewById(R.id.ll_drop_thi);
        ll_drop_fou = (LinearLayout) findViewById(R.id.ll_drop_fou);

        atv_drop_fir = (AppCompatCheckedTextView) findViewById(R.id.atv_drop_fir);
        atv_drop_sec = (AppCompatCheckedTextView) findViewById(R.id.atv_drop_sec);
        atv_drop_thi = (AppCompatCheckedTextView) findViewById(R.id.atv_drop_thi);
        atv_drop_fou = (AppCompatCheckedTextView) findViewById(R.id.atv_drop_fou);

        tabsTitles = new ArrayList<>();
        tabsTitles.add(atv_drop_fir);
        tabsTitles.add(atv_drop_sec);
        tabsTitles.add(atv_drop_thi);
        tabsTitles.add(atv_drop_fou);

        ll_drop_fir.setOnClickListener(this);
        ll_drop_sec.setOnClickListener(this);
        ll_drop_thi.setOnClickListener(this);
        ll_drop_fou.setOnClickListener(this);
    }

    /**
     * 初始化标签文本
     *
     * @param id 资源
     */
    public void initTabs(@ArrayRes int id) {
        defaultTabs = getResources().getStringArray(id);
        final int size = defaultTabs.length;
        for (int i = 0; i < size; i++) {
            resetTab(i);
        }
    }

    /**
     * 添加下拉菜单
     *
     * @param absDrops AbsDrop列表
     */
    public void addDrops(AbsDrop... absDrops) {
        final int size = absDrops.length;
        for (int i = 0; i < size; i++) {
            switch (i) {
                case 0:
                    absDrops[i].setDropListener(dropListener1);
                    break;
                case 1:
                    absDrops[i].setDropListener(dropListener2);
                    break;
                case 2:
                    absDrops[i].setDropListener(dropListener3);
                    break;
                case 3:
                    absDrops[i].setDropListener(dropListener4);
                    break;
                default:
                    break;
            }
            absDropList.add(absDrops[i]);
        }
    }

    /**
     * 设置选中状态
     *
     * @param position 0-3
     * @param selected 是否选中
     */
    private void select(int position, boolean selected) {
        tabsTitles.get(position).setChecked(selected);
        setSelectedTextColor(position, selected);
    }

    private void setSelectedTextColor(int position, boolean isSelected){
        if (isSelected){
            tabsTitles.get(position).setTextColor(Color.parseColor("#fb2727"));
        }else {
            tabsTitles.get(position).setTextColor(Color.parseColor("#333333"));
        }
    }



    private int checkedPosition = -1;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_drop_fir:
                checkedPosition = 0;
                if (absDropList.size() > 0){
                    absDropList.get(0).show();
                }
                break;
            case R.id.ll_drop_sec:
                checkedPosition = 1;
                if (absDropList.size() > 1)
                    absDropList.get(1).show();
                break;
            case R.id.ll_drop_thi:
                checkedPosition = 2;
                if (absDropList.size() > 2)
                    absDropList.get(2).show();
                break;
            case R.id.ll_drop_fou:
                checkedPosition = 3;
                if (absDropList.size() > 3)
                    absDropList.get(3).show();
                break;
            default:
                break;
        }

        select(checkedPosition, true);
    }

    /**
     * 重置标签到默认
     * @param position 标签位置
     */
    public void resetTab(int position) {
        setTab(position, defaultTabs[position], false);
    }

    public void setTab(int position, String tab){
        tabsTitles.get(position).setText(tab);
        tabsTitles.get(position).setChecked(false);
        setSelectedTextColor(position, true);
    }

    public void setTab(int position, String tab, boolean isSelected) {
        tabsTitles.get(position).setText(tab);
        select(position, isSelected);
    }

    public void setDropCompleteListener(DropCompleteListener dropCompleteListener) {
        this.dropCompleteListener = dropCompleteListener;
    }
}
