package com.cetnaline.findproperty.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cetnaline.findproperty.R;

/**
 * Created by diaoqf on 2016/8/27.
 */
public class ImageSelectLayout extends LinearLayout {

    private CircleImageView show_img;
    private ImageView select_img;
    private boolean isSelected;

    public ImageSelectLayout(Context context) {
        super(context);
        init(context, null);
    }

    public ImageSelectLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.image_select_layout, this);
        show_img = (CircleImageView) findViewById(R.id.show_img);
        select_img = (ImageView) findViewById(R.id.select_img);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ImageSelectLayout);
        isSelected = a.getBoolean(R.styleable.ImageSelectLayout_isSelected, false);
        Drawable d = a.getDrawable(R.styleable.ImageSelectLayout_showImg);
        if (d != null) {
            show_img.setImageDrawable(d);
        }
        a.recycle();
        select(isSelected);
    }

    public void select(boolean selected) {
        isSelected = selected;
        if (isSelected) {
            select_img.setVisibility(VISIBLE);
            show_img.setBorderWidth(3);
        } else {
            select_img.setVisibility(GONE);
            show_img.setBorderWidth(0);
        }
    }

    public boolean isSelected() {
        return isSelected;
    }

}
