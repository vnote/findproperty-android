/**
 * © 2016 Telenav, Inc.  All Rights Reserved
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License").
 * <p/>
 * You may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0. Unless required by applicable law or
 * agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */

package com.cetnaline.findproperty.widgets.bottomwindow;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.entity.event.MapPopEvent;
import com.cetnaline.findproperty.entity.event.SlidingEvent;
import com.cetnaline.findproperty.ui.activity.MainTabActivity;
import com.cetnaline.findproperty.ui.fragment.MapFragment;
import com.cetnaline.findproperty.utils.MyUtils;
import com.cetnaline.findproperty.utils.RxBus;
import com.cetnaline.findproperty.widgets.MyViewPager;
import com.cetnaline.findproperty.widgets.bottomwindow.adapter.ExpandablePagerAdapter;
import com.cetnaline.findproperty.widgets.bottomwindow.listener.OnItemSelectedListener;
import com.cetnaline.findproperty.widgets.bottomwindow.listener.OnSlideListener;
import com.cetnaline.findproperty.widgets.bottomwindow.listener.OnSliderStateChangeListener;
import com.zhy.autolayout.utils.AutoUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Layout that contains a ViewPager and can slide vertically between 2 states (expanded and collapsed). Should be aligned to the bottom of the screen.
 */
public class ExpandablePager extends SlidingContainer implements View.OnClickListener {

    public static final byte MODE_REGULAR = 0, MODE_FIXED = 1;

    private MyViewPager mPager = null;

    private float sliderStateThreshold;

    private OnItemSelectedListener onItemSelectedListener;

    private OnSliderStateChangeListener onSliderStateChangeListener;

    private int sliderState = STATE_COLLAPSED;

    private byte sliderMode = MODE_REGULAR;

    private float historicY;

    private int collapsedHeight = (int) (80 * getResources().getDisplayMetrics().density);

    private MainTabActivity mainTabActivity;

    public ExpandablePager(Context context) {
        super(context);
        init(context);
    }

    public ExpandablePager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ExpandablePager, 0, 0);
        setAnimationDuration(a.getInt(R.styleable.ExpandablePager_animation_duration, 200));
        collapsedHeight = (int) a.getDimension(R.styleable.ExpandablePager_collapsed_height, 80 * getResources().getDisplayMetrics().density);
        a.recycle();
    }

    private RelativeLayout bottom_title_rf;
    private TextView bottom_title_left, bottom_title, bottom_title_right;
    private TextView vp_number_indicator;

    private void init(Context context) {

        int titileHeight = (int) getContext().getResources().getDimension(R.dimen.bottomWindowTitleHeight);

        bottom_title_rf = new RelativeLayout(getContext());
        bottom_title_rf.setId(R.id.bottom_title_rf);
        bottom_title_rf.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, titileHeight));
        bottom_title_rf.setPadding(20, 20, 20, 20);

        bottom_title_left = new TextView(getContext());
        bottom_title_left.setText("查看小区");
        bottom_title_left.setTextColor(ContextCompat.getColor(getContext(), R.color.bottom_text_red));
        bottom_title_left.setId(R.id.bottom_title_left);
        bottom_title_left.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        bottom_title_left.setOnClickListener(this);
        RelativeLayout.LayoutParams left = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        left.addRule(RelativeLayout.CENTER_VERTICAL);
        bottom_title_rf.addView(bottom_title_left, left);

        bottom_title = new TextView(getContext());
        bottom_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        bottom_title.setSingleLine(true);
        bottom_title.setEllipsize(TextUtils.TruncateAt.END);
        bottom_title.setMaxWidth(460);
        bottom_title.setId(R.id.bottom_title);
        RelativeLayout.LayoutParams title = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        title.addRule(RelativeLayout.CENTER_IN_PARENT);
        bottom_title_rf.addView(bottom_title, title);

        bottom_title_right = new TextView(getContext());
        bottom_title_right.setText("打开列表");
        bottom_title_right.setId(R.id.bottom_title_right);
        bottom_title_right.setOnClickListener(this);
        bottom_title_right.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        bottom_title_right.setTextColor(ContextCompat.getColor(getContext(), R.color.bottom_text_red));
        RelativeLayout.LayoutParams right = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        right.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        right.addRule(RelativeLayout.CENTER_VERTICAL);
        bottom_title_rf.addView(bottom_title_right, right);

        addView(bottom_title_rf);

        mPager = new MyViewPager(getContext());

        mPager.setId(R.id.internal_pager_id);
//        mPager.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int idx = 0;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                idx = mPager.getCurrentItem() + (position < mPager.getCurrentItem() ? -1 : 1);
            }

            @Override
            public void onPageSelected(int position) {
//                HouseBottomBean postDo = ((HouseFragmentAdapter) mPager.getAdapter()).getDataItems().get(position);
//                if (postDo==null)return;
//                bottom_title.setText(postDo.getTitle());
                vp_number_indicator.setText("第"+(position+1)+"/"+mPager.getAdapter().getCount()+"套");
                if (onItemSelectedListener != null)
                    onItemSelectedListener.onItemSelected(((ExpandablePagerAdapter) mPager.getAdapter()).getDataItems(), position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == 0){
                    mPager.setIntercept(true);
                }else {
                    mPager.setIntercept(false);
                }
                notifyChange(idx);
            }

            private void notifyChange(int index) {
                if (onSliderStateChangeListener != null) {
                    if (index < mPager.getAdapter().getCount())
                        onSliderStateChangeListener.onPageChanged(getPage(index), index, sliderState);
                }
            }
        });

        GestureDetectorCompat tapGestureDetector = new GestureDetectorCompat(context, new TapGestureListener());
        mPager.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                tapGestureDetector.onTouchEvent(event);
                return false;
            }
        });

        RelativeLayout.LayoutParams vp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        vp.addRule(RelativeLayout.BELOW, R.id.bottom_title_rf);

        addView(mPager, vp);
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                pinToBottom();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                if (onSliderStateChangeListener != null) {
                    int index = mPager.getCurrentItem();
                    onSliderStateChangeListener.onPageChanged(getPage(index), index, sliderState);
                }
            }
        });

        int padding = AutoUtils.getPercentWidthSize(6);
        vp_number_indicator = new TextView(getContext());
        vp_number_indicator.setId(R.id.vp_number_indicator);
        vp_number_indicator.setTextColor(Color.WHITE);
        vp_number_indicator.setBackgroundResource(R.drawable.number_tips_bg);
        vp_number_indicator.setPadding(padding+4, padding, padding+4, padding);
        RelativeLayout.LayoutParams indicator = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        indicator.addRule(RelativeLayout.CENTER_HORIZONTAL);
        indicator.addRule(RelativeLayout.BELOW, R.id.bottom_title_rf);
        indicator.setMargins(0,40,0,0);
        addView(vp_number_indicator, indicator);

        setSlideListener(onSlideListener);

        slidingEvent = new SlidingEvent();

        initAnimations();
    }

    private void initAnimations() {
        inAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.bottom_in);
        outAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.bottom_out);
        inAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                ExpandablePager.this.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
//                showFirstData();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        outAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ExpandablePager.this.setVisibility(INVISIBLE);
                onTitleClickListener.hide();
                if (changeFragment){
                    mainTabActivity.showFragment(MainTabActivity.TAB_CHAT);

//                    mainTabActivity.setCurrentFragment();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * Move the layout to the bottom of the screen in case it was not moved in the xml file
     */
    private void pinToBottom() {
        ViewGroup.LayoutParams params = getLayoutParams();
        if (params != null) {
            if (params instanceof RelativeLayout.LayoutParams) {
                ((LayoutParams) params).addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            } else if (params instanceof LinearLayout.LayoutParams) {
                ((LinearLayout.LayoutParams) params).gravity = Gravity.BOTTOM;
            } else if (params instanceof FrameLayout.LayoutParams) {
                ((FrameLayout.LayoutParams) params).gravity = Gravity.BOTTOM;
            }
        }
    }

    @SliderState
    public int getSliderState() {
        return sliderState;
    }

    /**
     * Animates the container to the selected state.
     *
     * @param state - available value are: STATE_COLLAPSED, STATE_EXPANDED, STATE_HIDDEN
     */
    @Override
    public boolean animateToState(@SliderState int state) {
        sliderState = state;
        return mPager.getAdapter().getCount() > 0 && super.animateToState(state);
    }

    /**
     * Set the height of the pager in the collapsed state.
     *
     * @param collapsed collapsed height in pixels
     */
    public void setCollapsedHeight(int collapsed) {
        collapsedHeight = collapsed;
    }

    /**
     * @return current slider mode
     */
    @SliderMode
    public byte getMode() {
        return sliderMode;
    }

    /**
     * Set slider mode
     */
    public void setMode(@SliderMode byte mode) {
        sliderMode = mode;
        if (mode == MODE_FIXED)
            setSliderMode(MODE_FIXED);
    }

    private void setSliderMode(@SliderMode byte mode) {
        switch (mode) {
            case MODE_REGULAR: // full screen
                int height = getHeight();
                sliderStateThreshold = height / 2;
                sliderMode = MODE_REGULAR;
                setStopValues((float) height - collapsedHeight);
                break;
            case MODE_FIXED:
                sliderStateThreshold = Integer.MAX_VALUE;
                sliderMode = MODE_FIXED;
                getLayoutParams().height = collapsedHeight;
                setStopValues(0f);
                break;
        }
        enableSlide(mode != MODE_FIXED);
    }

    /**
     * Set the currently selected page.
     *
     * @param index        Item index to select
     * @param smoothScroll True to smoothly scroll to the new item, false to transition immediately
     */
    public void setCurrentItem(int index, boolean smoothScroll) {
        mPager.setCurrentItem(index, smoothScroll);
    }

    public int getCurrentItem() {
        return mPager.getCurrentItem();
    }

    public void setAdapter(PagerAdapter adapter) {
        int index = mPager.getCurrentItem();
        mPager.setAdapter(adapter);
        mPager.setCurrentItem(Math.min(index, adapter.getCount() - 1));
        mPager.post(new Runnable() {
            @Override
            public void run() {
                if (onSliderStateChangeListener != null) {
                    int index = mPager.getCurrentItem();
                    onSliderStateChangeListener.onPageChanged(getPage(index), index, sliderState);
                }
            }
        });
    }

//    private void showFirstData() {
//        List<HouseBottomBean> postDos = ((HouseFragmentAdapter) mPager.getAdapter()).getDataItems();
//        if (postDos==null || postDos.size()<1)return;
//        HouseBottomBean postDo = postDos.get(0);
//        if (postDo==null)return;
//        bottom_title.setText(postDo.getTitle());
//    }

    public void setOnSliderStateChangeListener(OnSliderStateChangeListener onSliderStateChangeListener) {
        this.onSliderStateChangeListener = onSliderStateChangeListener;
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        onItemSelectedListener = listener;
    }

    private View getPage(int position) {
        if (mPager.getAdapter() != null && position >= 0 && position < mPager.getAdapter().getCount()) {
            if (mPager.getAdapter() instanceof FragmentPagerAdapter ||
                    mPager.getAdapter() instanceof FragmentStatePagerAdapter)
                return ((Fragment) mPager.getAdapter().instantiateItem(mPager, position)).getView();
            else
                return findViewById(R.id.internal_page_id % 10000 + position);
        } else
            return null;
    }

    @Override
    protected void notifySlideEvent(float yPosition) {
        super.notifySlideEvent(yPosition);
        if (historicY <= sliderStateThreshold && yPosition >= sliderStateThreshold) {
            //down
            if (sliderState != STATE_HIDDEN)
                sliderState = STATE_COLLAPSED;
            if (onSliderStateChangeListener != null) {
                int index = mPager.getCurrentItem();
                onSliderStateChangeListener.onStateChanged(getPage(index), index, sliderState);
            }
        } else if (historicY >= sliderStateThreshold && yPosition < sliderStateThreshold) {
            //up
            sliderState = STATE_EXPANDED;
            if (onSliderStateChangeListener != null) {
                int index = mPager.getCurrentItem();
                onSliderStateChangeListener.onStateChanged(getPage(index), index, sliderState);
            }
        }
        historicY = yPosition;
    }

    @Override
    protected void onSettled(int slideValueIndex) {
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);

        ss.sliderState = sliderState;
        ss.sliderMode = sliderMode;
        if (mPager != null)
            ss.currentIndex = mPager.getCurrentItem();

        return ss;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        switch (sliderState) {
            case STATE_COLLAPSED:
                historicY = h - collapsedHeight;
                break;
            case STATE_EXPANDED:
                historicY = 0;
                break;
            case STATE_HIDDEN:
                historicY = h;
                break;
        }
        if (sliderMode == MODE_REGULAR) {
            setSliderMode(sliderMode);
        }
        setState(sliderState);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        sliderState = ss.sliderState;
        sliderMode = ss.sliderMode;
        if (mPager != null)
            mPager.setCurrentItem(ss.currentIndex);
    }

    @Override
    public void onClick(View view) {
        if (onTitleClickListener==null)return;
        switch (view.getId()){
            case R.id.bottom_title_left:
                onTitleClickListener.leftClick();
                break;
            case R.id.bottom_title_right:
                onTitleClickListener.rightClick();
                break;
        }
    }


    @Retention(RetentionPolicy.SOURCE)
    @IntDef({MODE_REGULAR, MODE_FIXED})
    public @interface SliderMode {
    }

    static class SavedState extends BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
        int currentIndex;
        int sliderState;
        byte sliderMode;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.currentIndex = in.readInt();
            this.sliderState = in.readInt();
            this.sliderMode = in.readByte();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.currentIndex);
            out.writeInt(this.sliderState);
            out.writeByte(this.sliderMode);
        }
    }

    private boolean isToUp = false;
    private boolean isToBottom = true;
    private SlidingEvent slidingEvent;

    private OnTitleClickListener onTitleClickListener;

    public void setOnTitleClickListener(OnTitleClickListener onTitleClickListener){
        this.onTitleClickListener=onTitleClickListener;
    }

    public interface OnTitleClickListener{
        void leftClick();
        void rightClick();
        void hide();
    }

    private OnSlideChangeListener onSlideChangeListener;

    public void setOnSlideChangeListener(OnSlideChangeListener onSlideChangeListener){
        this.onSlideChangeListener=onSlideChangeListener;
    }

    public interface OnSlideChangeListener{
        void toUp();
        void toBottom();
    }

    private OnSlideListener onSlideListener = new OnSlideListener() {
        @Override
        public void onSlide(float amount) {

        }

        @Override
        public void onScaleY(float scale) {
            if (scale > 0.4 && bottom_title_rf.getVisibility() == View.GONE) {
                bottom_title_rf.setVisibility(View.VISIBLE);
            }

            bottom_title_left.setAlpha(scale);
            bottom_title.setAlpha(scale);
            bottom_title_right.setAlpha(scale);


            slidingEvent.setToBottom(false);
            slidingEvent.setToUp(false);
            slidingEvent.setScale(scale);

            if (scale == 1 && !isToBottom) {
                isToUp = false;
                isToBottom = true;
                slidingEvent.setToUp(isToUp);
                slidingEvent.setToBottom(isToBottom);
                setIntercept(false);
                mPager.setIntercept(true);
                vp_number_indicator.setVisibility(View.VISIBLE);
                if (onSlideChangeListener!=null)onSlideChangeListener.toBottom();
                setMoveY(true);
            } else if (scale == 0 && !isToUp) {
                isToUp = true;
                isToBottom = false;
                slidingEvent.setToUp(isToUp);
                slidingEvent.setToBottom(isToBottom);
                mPager.setIntercept(false);
                bottom_title_rf.setVisibility(View.GONE);
                vp_number_indicator.setVisibility(View.GONE);
                if (onSlideChangeListener!=null)onSlideChangeListener.toUp();
                setIntercept(false);
            }else if (scale!=0 && scale!=1){
                setIntercept(true);
            }

            //用户是往下滑动
            if (scale==1 && isToBottom && hiddenPop){
                RxBus.getDefault().send(new MapPopEvent());
                hiddenPop = false;
            }

            if (scale==0 && isToUp){
                //滑动到最底部，里面再往下滑动
                setIntercept(false);
                setMoveY(true);
            }

            RxBus.getDefault().send(slidingEvent);
        }

        @Override
        public void onSlideEndByAnimator(float amount) {
            if (amount == 0) {
                isToUp = true;
                isToBottom = false;
                bottom_title_rf.setVisibility(View.GONE);
                mPager.setIntercept(false);
                vp_number_indicator.setVisibility(View.GONE);
                if (onSlideChangeListener!=null)onSlideChangeListener.toUp();
                slidingEvent.setScale(0);
                setIntercept(false);
            } else {
                isToUp = false;
                isToBottom = true;
                bottom_title_rf.setVisibility(View.VISIBLE);
                bottom_title_left.setAlpha(1f);
                bottom_title.setAlpha(1f);
                bottom_title_right.setAlpha(1f);
                setIntercept(false);
                mPager.setIntercept(true);
                vp_number_indicator.setVisibility(View.VISIBLE);
                if (onSlideChangeListener!=null)onSlideChangeListener.toBottom();
                slidingEvent.setScale(1);
                setMoveY(true);
            }

            slidingEvent.setToBottom(isToBottom);
            slidingEvent.setToUp(isToUp);
            RxBus.getDefault().send(slidingEvent);
        }
    };

    private Animation inAnimation, outAnimation;

    public void show(int houseType, String estateName){
        hiddenPop = true;
        if (houseType== MapFragment.HOUSE_TYPE_NEW){
            bottom_title_left.setVisibility(INVISIBLE);
            bottom_title_right.setVisibility(INVISIBLE);
            vp_number_indicator.setVisibility(INVISIBLE);
        }else {
            bottom_title_left.setVisibility(VISIBLE);
            bottom_title_right.setVisibility(VISIBLE);
            vp_number_indicator.setVisibility(VISIBLE);
        }
        inAnimation.setFillAfter(true);
        this.startAnimation(inAnimation);
        setBottomTitle(estateName);
        mPager.setCurrentItem(0);
    }

    public void show(){
        hiddenPop = true;
        inAnimation.setFillAfter(true);
        this.startAnimation(inAnimation);
    }

    public void setBottomTitle(String estateName){
        bottom_title.setText(estateName);
    }

    public void setBottomTitle(String estateName, int dataSize){
        bottom_title.setText(estateName);
        vp_number_indicator.setText("第1/"+dataSize+"套");
    }

    private boolean changeFragment;

    public void hidden(boolean changeFragment){
        hiddenPop = false;
        this.changeFragment = changeFragment;
        toBottom();
        this.startAnimation(outAnimation);
    }

    public void setMainTabActivity(MainTabActivity mainTabActivity){
        this.mainTabActivity=mainTabActivity;
    }

    private boolean hiddenPop;

    public class TapGestureListener extends GestureDetector.SimpleOnGestureListener{

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            // Your Code here
            toUp();
            return true;
        }
    }



}
