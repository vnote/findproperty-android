package com.cetnaline.findproperty.widgets;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MyViewPager extends ViewPager {
  
    private boolean isIntercept = true;
  
    public MyViewPager(Context context) {
        super(context);
    }  
  
    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);  
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isIntercept;
    }

    public void setIntercept(boolean intercept) {
        isIntercept = intercept;
    }
}