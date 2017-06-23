package com.cetnaline.findproperty.widgets;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class ObservableScrollView extends ScrollView {
    private static final boolean DEBUG = false;
    private static final int CHECK_SCROLL_STOP_DELAY_MILLIS = 80;
    private static final int MSG_SCROLL = 1;

    private boolean mIsTouched = false;
    private int mScrollState = OnScrollListener.SCROLL_STATE_IDLE;

    private OnScrollListener mOnScrollListener;

    private final Handler mHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
        private int mLastY = Integer.MIN_VALUE;

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == MSG_SCROLL) {
                final int scrollY = getScrollY();
                if (!mIsTouched && mLastY == scrollY) {
                    mLastY = Integer.MIN_VALUE;
                    setScrollState(OnScrollListener.SCROLL_STATE_IDLE);
                } else {
                    mLastY = scrollY;
                    restartCheckStopTiming();
                }
                return true;
            }
            return false;
        }
    });

    private void restartCheckStopTiming() {
        mHandler.removeMessages(MSG_SCROLL);
        mHandler.sendEmptyMessageDelayed(MSG_SCROLL, CHECK_SCROLL_STOP_DELAY_MILLIS);
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        mOnScrollListener = onScrollListener;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        handleDownEvent(ev);
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        handleUpEvent(ev);
        return super.onTouchEvent(ev);
    }

    private void handleDownEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mIsTouched = true;
                break;
        }
    }

    private void handleUpEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsTouched = false;
                restartCheckStopTiming();
                break;
        }
    }

    private void setScrollState(int state) {
        if (mScrollState != state) {
            mScrollState = state;
            if (mOnScrollListener != null) {
                mOnScrollListener.onScrollStateChanged(this, state);
            }
        }
    }


    private ScrollViewListener scrollViewListener = null;
  
    public ObservableScrollView(Context context) {
        super(context);
    }  
  
    public ObservableScrollView(Context context, AttributeSet attrs,
            int defStyle) {  
        super(context, attrs, defStyle);  
    }

    public interface ScrollViewListener{
        void onScrollChanged( int x, int y, int oldx, int oldy);
    }
  
    public ObservableScrollView(Context context, AttributeSet attrs) {  
        super(context, attrs);  
    }  
  
    public void setScrollViewListener(ScrollViewListener scrollViewListener) {  
        this.scrollViewListener = scrollViewListener;  
    }  
  
    @Override  
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {  
        super.onScrollChanged(x, y, oldx, oldy);  
        if (scrollViewListener != null) {  
            scrollViewListener.onScrollChanged(x, y, oldx, oldy);
        }

        if (mIsTouched) {
            setScrollState(OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
        } else {
            setScrollState(OnScrollListener.SCROLL_STATE_FLING);
            restartCheckStopTiming();
        }
        if (mOnScrollListener != null) {
            mOnScrollListener.onScroll(this, mIsTouched, x, y, oldx, oldy);
        }
    }

    public interface OnScrollListener {
        /**
         * The view is not scrolling. Note navigating the list using the trackball counts as
         * being in the idle state since these transitions are not animated.
         */
        int SCROLL_STATE_IDLE = 0;

        /**
         * The user is scrolling using touch, and their finger is still on the screen
         */
        int SCROLL_STATE_TOUCH_SCROLL = 1;

        /**
         * The user had previously been scrolling using touch and had performed a fling. The
         * animation is now coasting to a stop
         */
        int SCROLL_STATE_FLING = 2;

        void onScrollStateChanged(ObservableScrollView view, int scrollState);

        void onScroll(ObservableScrollView view, boolean isTouchScroll, int l, int t, int oldl, int oldt);
    }

}