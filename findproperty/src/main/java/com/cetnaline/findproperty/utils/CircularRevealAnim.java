package com.cetnaline.findproperty.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.DecelerateInterpolator;

public class CircularRevealAnim {

    public static final long DURATION = 1000;

    public static final String CENTER_X_KEY = "CENTER_X_KEY";
    public static final String CENTER_Y_KEY = "CENTER_Y_KEY";

    private AnimListener mListener;

    /**
     * 当动画播放完毕后，DialogFragment 中需要做响应的处理，通过接口的形式通知对方
    */
    public interface AnimListener {

        void onHideAnimationEnd();

        void onShowAnimationEnd();
    }

    /**
    * 显示/隐藏两种效果封装可以在一个方法中实现
    * triggerView：  揭露效果的中心
    * animView：     播放动画的 View
    */
    @SuppressLint("NewApi")
    private void actionOtherVisible(final boolean isShow, int tvX, int tvY, final View animView) {

        /**
        * 当版本低于 Android L 时，没有动画效果
        */
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            if (isShow) {
                animView.setVisibility(View.VISIBLE);
                if (mListener != null) mListener.onShowAnimationEnd();
            } else {
                animView.setVisibility(View.GONE);
                if (mListener != null) mListener.onHideAnimationEnd();
            }
            return;
        }

        /**
         * 计算 triggerView 的中心位置
         */
//        int[] tvLocation = new int[2];
//        triggerView.getLocationInWindow(tvLocation);
//        int tvX = tvLocation[0] + triggerView.getWidth() / 2;
//        int tvY = tvLocation[1] + triggerView.getHeight() / 2;

        /**
         * 计算 animView 的中心位置
         */
        int[] avLocation = new int[2];
        animView.getLocationInWindow(avLocation);
        int avX = avLocation[0] + animView.getWidth() / 2;
        int avY = avLocation[1] + animView.getHeight() / 2;

        /**
        * 这里就是上面分析的求最大半径的布局
        * 比较 triggerView 与 animView 的中心坐标，得到三角形的两条边
        */
        int rippleW = tvX < avX ? animView.getWidth() - tvX : tvX - avLocation[0];
        int rippleH = tvY < avY ? animView.getHeight() - tvY : tvY - avLocation[1];

        float maxRadius = (float) Math.sqrt(rippleW * rippleW + rippleH * rippleH);
        float startRadius;
        float endRadius;

        if (isShow) {
            startRadius = 0;
            endRadius = maxRadius;
        } else {
            startRadius = maxRadius;
            endRadius = 0;
        }

        Animator anim = ViewAnimationUtils.createCircularReveal(animView, tvX, tvY, startRadius, endRadius);
        animView.setVisibility(View.VISIBLE);
        anim.setDuration(DURATION);
        anim.setInterpolator(new DecelerateInterpolator());

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (isShow) {
                    animView.setVisibility(View.VISIBLE);
                    if (mListener != null) mListener.onShowAnimationEnd();
                } else {
                    animView.setVisibility(View.GONE);
                    if (mListener != null) mListener.onHideAnimationEnd();
                }
            }
        });

        anim.start();
    }

    /**
    * 方便外界调用
    */
    public void show(int tvX, int tvY, View showView) {
        actionOtherVisible(true, tvX, tvY, showView);
    }

    /**
    * 方便外界调用
    */
    public void hide(int tvX, int tvY, View hideView) {
        actionOtherVisible(false, tvX, tvY, hideView);
    }

    public void setAnimListener(AnimListener listener) {
        mListener = listener;
    }

}