package com.cetnaline.findproperty.widgets;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cetnaline.findproperty.db.entity.DropBo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fanxl2 on 2016/11/7.
 */

public class HorizontalWheel extends HorizontalScrollView {

	private static final String TAG = "HorizontalWheel";

	public HorizontalWheel(Context context) {
		this(context, null);
	}

	public HorizontalWheel(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public HorizontalWheel(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);

	}

	//每个item的宽度
	private int itemWidth;

	private Context mContext;

	private LinearLayout views;

	int newCheck = 50;

	private void init(Context context) {
		this.mContext = context;
		setHorizontalFadingEdgeEnabled(false);
		views = new LinearLayout(context);
		views.setOrientation(LinearLayout.HORIZONTAL);
		this.addView(views);

		itemWidth = getPhoneWidth() / 3;

		scrollerTask = new Runnable() {

			public void run() {

				int newX = getScrollX();
				if (initialX - newX == 0) { // stopped
					final int remainder = initialX % itemWidth;
					if (remainder != 0) {
						if (remainder > itemWidth / 2) {
							HorizontalWheel.this.post(new Runnable() {
								@Override
								public void run() {
									HorizontalWheel.this.smoothScrollTo(initialX - remainder + itemWidth, 0);
									if (wheelListener != null) {
										wheelListener.scrollSelected(currentPos - 1);
									}
								}
							});
						} else {
							HorizontalWheel.this.post(new Runnable() {
								@Override
								public void run() {
									HorizontalWheel.this.smoothScrollTo(initialX - remainder, 0);
									if (wheelListener != null) {
										wheelListener.scrollSelected(currentPos - 1);
									}
								}
							});
						}
					} else {
						if (wheelListener != null) {
							wheelListener.scrollSelected(currentPos - 1);
						}
					}
				} else {
					initialX = getScrollX();
					HorizontalWheel.this.postDelayed(scrollerTask, newCheck);
				}
			}
		};
	}

	List<DropBo> items;

	public void setItems(List<DropBo> list) {
		if (null == items) {
			items = new ArrayList<>();
		}
		items.clear();

		DropBo temp = new DropBo();
		items.add(temp);
		items.addAll(list);
		items.add(temp);
		initData();
	}

	private void initData() {
		for (int i = 0; i < items.size(); i++) {
			views.addView(createView(items.get(i), i));
		}
	}

	private int DEFAULT_POSITION = 1;
	private int currentPos;

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
//		Log.i(TAG, "x: "+l+"--oldX:"+oldl);
		refreshItemView(l);
	}

	private void refreshItemView(int x) {
//		Log.i(TAG, "x: "+x+"----itemWidth:"+itemWidth);
		int remainder = x % itemWidth;
		int divided = x / itemWidth;

//		Log.i(TAG, "remainder: "+remainder);
		if (remainder < itemWidth / 2) {
			currentPos = DEFAULT_POSITION + divided;
		} else {
			currentPos = DEFAULT_POSITION + divided + 1;
		}

		if (currentPos == 0) currentPos = 1;

//		Log.i(TAG, "currentPos: "+currentPos);

		int childSize = views.getChildCount();
		for (int i = 0; i < childSize; i++) {
			TextView itemView = (TextView) views.getChildAt(i);
			if (null == itemView) {
				return;
			}
			if (currentPos == i) {
				itemView.setTextColor(Color.WHITE);
				itemView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
			} else {
				itemView.setTextColor(Color.parseColor("#B3FFFFFF"));
				itemView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
			}
		}
	}

	private TextView createView(DropBo item, int pos) {
		TextView tv = new TextView(mContext);
		tv.setLayoutParams(new LayoutParams(itemWidth, ViewGroup.LayoutParams.MATCH_PARENT));
		tv.setSingleLine(true);
		if (pos == DEFAULT_POSITION) {
			tv.setTextColor(Color.WHITE);
			tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		} else {
			tv.setTextColor(Color.parseColor("#B3FFFFFF"));
			tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		}
		tv.setText(item.getText());
		tv.setGravity(Gravity.CENTER);
		return tv;
	}

	private int getPhoneWidth() {
		WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		return wm.getDefaultDisplay().getWidth();
	}

	@Override
	public void fling(int velocityX) {
		super.fling(velocityX / 2);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_UP) {
			startScrollerTask();
		}
		return super.onTouchEvent(ev);
	}

	int initialX;

	public void startScrollerTask() {

		initialX = getScrollX();
		this.postDelayed(scrollerTask, newCheck);
	}

	Runnable scrollerTask;

	private WheelListener wheelListener;

	public void setWheelListener(WheelListener wheelListener) {
		this.wheelListener = wheelListener;
	}

	public interface WheelListener {
		void scrollSelected(int pos);
	}

}