package com.cetnaline.findproperty.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cetnaline.findproperty.R;

/**
 * Created by diaoqf on 2016/8/27.
 */
public class SelectTagLayout extends LinearLayout {

    private TextView text;
    private ImageView select_img;
    private boolean isSelected;
    private int value;
    private int valueExt;

    public SelectTagLayout(Context context) {
        super(context);
        init(context,null);
    }

    public SelectTagLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.select_tag_layout,this);
        text = (TextView) findViewById(R.id.text);
        select_img = (ImageView) findViewById(R.id.select_img);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SelectTagLayout);
        text.setText(a.getString(R.styleable.SelectTagLayout_showTagText));
        isSelected = a.getBoolean(R.styleable.SelectTagLayout_isSelected, false);
        int min = a.getInteger(R.styleable.SelectTagLayout_selectedValue,0);
        if (min < 0) {
            value = Integer.MIN_VALUE;
        } else {
            value = min;
        }

        int max = a.getInteger(R.styleable.SelectTagLayout_selectedValueExt,0);
        if (max < 0) {
            value = Integer.MAX_VALUE;
        } else {
            value = max;
        }

        valueExt = a.getInteger(R.styleable.SelectTagLayout_selectedValueExt,0);

        a.recycle();
        select(isSelected);
    }

    public void select(boolean flag){
        isSelected = flag;
        if (isSelected) {
            text.setEnabled(true);
            select_img.setVisibility(VISIBLE);
        } else {
            text.setEnabled(false);
            select_img.setVisibility(GONE);
        }
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getValueExt() {
        return valueExt;
    }

    public void setValueExt(int valueExt) {
        this.valueExt = valueExt;
    }

    public void setText(String str){
        text.setText(str);
    }
}
