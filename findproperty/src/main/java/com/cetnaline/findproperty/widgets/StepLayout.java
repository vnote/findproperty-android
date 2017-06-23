package com.cetnaline.findproperty.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.cetnaline.findproperty.R;

/**
 * Created by diaoqf on 2016/8/27.
 */
public class StepLayout extends LinearLayout {

    public StepLayout(Context context) {
        super(context);
        init(context);
    }

    public StepLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.step_layout,  this);
    }

    public void enableStep(int step) {
        ViewGroup view = (ViewGroup) getChildAt(0);
        for (int i = 0; i < step; i++) {
            view.getChildAt((i*2-1)<0 ? 0 : (i*2-1)).setEnabled(true);
            view.getChildAt(i * 2).setEnabled(true);
        }
    }
}
