package com.cetnaline.findproperty.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cetnaline.findproperty.R;

/**
 * Created by diaoqf on 2016/8/26.
 */
public class LoadingLayout extends LinearLayout {
    public LoadingLayout(Context context) {
        super(context);
        init(context);
    }

    public LoadingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.loading, this);
        ImageView loading = (ImageView) findViewById(R.id.loading);
        Animation animation= AnimationUtils.loadAnimation(context, R.anim.loading_rotate);
        loading.startAnimation(animation);
    }
}
