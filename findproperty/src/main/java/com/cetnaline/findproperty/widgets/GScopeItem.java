package com.cetnaline.findproperty.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cetnaline.findproperty.R;

/**
 * Created by diaoqf on 2016/8/30.
 */
public class GScopeItem extends LinearLayout {

    private boolean isSelected;
    private VectorDrawableCompat img_black;
    private VectorDrawableCompat img_red;

    private ImageView select_img;
    private TextView title;

    private OnItemClick onItemClick;

    public GScopeItem(Context context) {
        super(context);
        init(context,null);
    }

    public GScopeItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View view = inflate(context, R.layout.item_gscope,this);
        title = (TextView) findViewById(R.id.title);
        select_img = (ImageView) findViewById(R.id.select_img);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GScopeItem);

        if (!a.getBoolean(R.styleable.GScopeItem_showIcon,true)) {
            select_img.setVisibility(GONE);
        }

        title.setText(a.getString(R.styleable.GScopeItem_showTagText));
        title.setTextSize(a.getDimension(R.styleable.GScopeItem_titleSize,14));
        img_black= VectorDrawableCompat.create(getResources(), R.drawable.ic_right_arrow_gray_24dp, null);
        img_red= VectorDrawableCompat.create(getResources(), R.drawable.ic_right_arrow_red_24dp, null);
        setSelect(a.getBoolean(R.styleable.GScopeItem_isSelected, false));
        a.recycle();
        view.setOnClickListener(v->{
            disableBrothers();
            setSelect(true);
            if (onItemClick != null) {
                onItemClick.onClick(v);
            }
        });
    }

    private void disableBrothers() {
        ViewGroup view = (ViewGroup) getParent();
        if (view != null) {
            int count = view.getChildCount();
            if (count > 0) {
                for (int i=0;i<count;i++) {
                    View child = view.getChildAt(i);
                    if (child instanceof GScopeItem) {
                        ((GScopeItem)child).setSelect(false);
                    }
                }
            }
        }
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public void setTitle(int stringId) {
        this.title.setText(getResources().getString(stringId));
    }

    public String getTitle(){
        return title.getText().toString();
    }

    public void setTitleSize(int size) {
        this.title.setTextSize(size);
    }

    public void showIcon(boolean isShow) {
        if (isShow) {
            select_img.setVisibility(VISIBLE);
        } else {
            select_img.setVisibility(GONE);
        }
    }

    public void setSelect(boolean selected) {
        isSelected = selected;
        if (isSelected) {
            title.setTextColor(getResources().getColor(R.color.appBaseColor));
            select_img.setImageDrawable(img_red);
        } else {
            title.setTextColor(getResources().getColor(R.color.normalText));
            select_img.setImageDrawable(img_black);
        }
    }

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public interface OnItemClick {
        void onClick(View v);
    }
}
