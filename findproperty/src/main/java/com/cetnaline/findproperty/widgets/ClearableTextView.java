package com.cetnaline.findproperty.widgets;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.cetnaline.findproperty.R;

/**
 * 带删除功能的TextView
 */
public class ClearableTextView extends AppCompatTextView implements View.OnTouchListener {

    private Drawable mClearTextIcon;
    private OnTouchListener mOnTouchListener;

    public ClearableTextView(final Context context) {
        super(context);
        init(context);
    }

    public ClearableTextView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ClearableTextView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    public void setOnTouchListener(final OnTouchListener onTouchListener) {
        mOnTouchListener = onTouchListener;
    }

    private void init(Context context) {
        final Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_clear_black_24dp);
        final Drawable wrappedDrawable = DrawableCompat.wrap(drawable); //Wrap the drawable so that it can be tinted pre Lollipop
        DrawableCompat.setTint(wrappedDrawable, getCurrentHintTextColor());
        mClearTextIcon = wrappedDrawable;
        mClearTextIcon.setBounds(0, 0, mClearTextIcon.getIntrinsicHeight(), mClearTextIcon.getIntrinsicHeight());
        setClearIconVisible(true);
        super.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(final View view, final MotionEvent motionEvent) {
        final int x = (int) motionEvent.getX();
        if (mClearTextIcon.isVisible() && x > getWidth() - getPaddingRight() - mClearTextIcon.getIntrinsicWidth()) {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                if (clearClickListener!=null){
                    clearClickListener.onClearClick(this);
                }
            }
            return true;
        }
        return mOnTouchListener != null && mOnTouchListener.onTouch(view, motionEvent);
    }

    @Override
    public final void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
        if (isFocused()) {
            setClearIconVisible(s.length() > 0);
        }
    }

    private void setClearIconVisible(final boolean visible) {
        mClearTextIcon.setVisible(visible, false);
        final Drawable[] compoundDrawables = getCompoundDrawables();
        setCompoundDrawables(
                compoundDrawables[0],
                compoundDrawables[1],
                visible ? mClearTextIcon : null,
                compoundDrawables[3]);
    }

    private ClearClickListener clearClickListener;

    public void setOnClearClickListener(ClearClickListener clearClickListener){
        this.clearClickListener=clearClickListener;
    }

    public interface ClearClickListener{
        void onClearClick(ClearableTextView view);
    }

}
