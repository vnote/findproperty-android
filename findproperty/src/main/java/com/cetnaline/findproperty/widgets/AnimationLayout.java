package com.cetnaline.findproperty.widgets;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.animation.AccelerateInterpolator;
import android.widget.RelativeLayout;

import com.cetnaline.findproperty.utils.MyUtils;
import com.zhy.autolayout.utils.AutoUtils;

/**
 * Created by diaoqf on 2017/2/6.
 */

public class AnimationLayout extends RelativeLayout {

    private int viewWidth;
    private float radius;
    private int minWidth;
    private boolean is_animated;
    private LayoutAnimationListener listener;

    private Paint mPaint;

    public AnimationLayout(Context context) {
        super(context);
        init(context,null);
    }

    public AnimationLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        viewWidth = AutoUtils.getPercentWidthSize(680);
        radius = getHeight()/2.0f;
        minWidth = viewWidth;
        is_animated = false;

        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setAntiAlias(true);
    }

    public void setViewWidth(int viewWidth) {
//        Logger.i("buobao:"+viewWidth);
        this.viewWidth = viewWidth;
        invalidate();
    }

    public void setRadius(float radius) {
        this.radius = radius;
        invalidate();
    }

    /**
     * 顶部搜索栏扩散动画
     */
    public void layoutAnimate() {
        ValueAnimator animator;
        ValueAnimator animator1;
        if (!is_animated) {
            animator = ObjectAnimator.ofInt(this, "viewWidth", minWidth, MyUtils.getPhoneWidth((Activity) getContext()));
            animator1 = ObjectAnimator.ofFloat(this, "radius", getHeight()/2, 0);
        } else {
            animator = ObjectAnimator.ofFloat(this, "radius", 0, getHeight()/2);
            animator1 = ObjectAnimator.ofInt(this, "viewWidth", MyUtils.getPhoneWidth((Activity) getContext()), minWidth);
        }
        animator1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                listener.before();
//                Logger.i("buobao:before");
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                listener.after();
//                Logger.i("buobao:after");
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        AnimatorSet set = new AnimatorSet();
        set.setInterpolator(new AccelerateInterpolator());
        set.setDuration(300);
        set.playTogether(animator,animator1);
//        set.play(animator1).after(animator);
        set.start();
        is_animated = !is_animated;
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        canvas.drawRoundRect(new RectF(getWidth()/2-viewWidth/2,0,getWidth()/2+viewWidth/2,getHeight()),radius,radius,mPaint);
//        canvas.drawArc(new RectF(getWidth()/2-viewWidth/2,0,getWidth()/2-viewWidth/2+getHeight()/2,getHeight()),90,-90,false,mPaint);
//        canvas.drawRect(new RectF(getWidth()/2-viewWidth/2+getHeight()/2,0,getWidth()/2+viewWidth/2-getHeight()/2,getHeight()),mPaint);
//        canvas.drawArc(new RectF(getWidth()/2+viewWidth/2-getHeight()/2,0,getWidth()/2+viewWidth/2,getHeight()),90,-90,false,mPaint);
    }

    public boolean isAnimated() {
        return is_animated;
    }

    public void setListener(LayoutAnimationListener listener) {
        this.listener = listener;
    }

    public interface LayoutAnimationListener {
        void before();
        void after();
    }

}
