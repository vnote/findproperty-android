package com.cetnaline.findproperty.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cetnaline.findproperty.R;

/**
 * Created by diaoqf on 2016/12/7.
 */

public class CustomMenuItem extends LinearLayout {

    private ImageView img;
    private TextView title,sub_title;

    public CustomMenuItem(Context context) {
        super(context);
        init(context, null);
    }

    public CustomMenuItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_menu_layout, this, true);
        img = (ImageView) view.findViewById(R.id.img);
        title = (TextView) view.findViewById(R.id.title);
        sub_title = (TextView) view.findViewById(R.id.sub_title);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomMenuItem);
        img.setImageDrawable(getResources().getDrawable(a.getResourceId(R.styleable.CustomMenuItem_left_img,R.drawable.mine_note)));
        title.setText(a.getString(R.styleable.CustomMenuItem_title));
        sub_title.setText(a.getString(R.styleable.CustomMenuItem_sub_title));
        a.recycle();
    }
}


























