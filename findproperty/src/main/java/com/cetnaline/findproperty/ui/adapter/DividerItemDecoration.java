package com.cetnaline.findproperty.ui.adapter;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by fanxl2 on 2016/10/11.
 */

public class DividerItemDecoration extends RecyclerView.ItemDecoration {

	private int dividerHeight = 10;

	public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;

	public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;

	private int mOrientation;

	private Paint paint;

	public DividerItemDecoration(int orientation, int color, int dividerHeight) {
		this(orientation, color);
		this.dividerHeight=dividerHeight;
	}

	public DividerItemDecoration(int orientation, int color) {
		setOrientation(orientation);
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(color);
	}

	public void setOrientation(int orientation) {
		if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
			throw new IllegalArgumentException("invalid orientation");
		}
		mOrientation = orientation;
	}

	@Override
	public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
		super.onDraw(c, parent, state);
		if (mOrientation == VERTICAL_LIST) {
			drawVertical(c, parent);
		} else {
			drawHorizontal(c, parent);
		}
	}

	private void drawHorizontal(Canvas c, RecyclerView parent) {
		int childCount = parent.getChildCount();
		int top = parent.getPaddingTop();
		int bottom = parent.getHeight() - parent.getPaddingBottom();

		for (int i=0; i<childCount; i++){
			View view = parent.getChildAt(i);
			RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();

			int left = view.getRight() + params.rightMargin;
			int right = left + dividerHeight;

			c.drawRect(left, top, right, bottom, paint);
//			mDivider.setBounds(left, top, right, bottom);
//			mDivider.draw(c);

		}
	}

	private void drawVertical(Canvas c, RecyclerView parent) {
		int childCount = parent.getChildCount();
		int left = parent.getPaddingLeft();
		int right = parent.getWidth()-parent.getPaddingRight();

		for (int i=0; i<childCount;i++){
			View view = parent.getChildAt(i);
			RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
			int top = view.getTop() - params.topMargin;
			int bottom = top + dividerHeight;

			c.drawRect(left, top, right, bottom, paint);
//			mDivider.setBounds(left, top, right, top + mDivider.getIntrinsicHeight());
//			mDivider.draw(c);
		}

	}

	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
		super.getItemOffsets(outRect, view, parent, state);
		if (mOrientation == VERTICAL_LIST) {
			outRect.set(0, 0, 0, dividerHeight);
		} else {
			outRect.set(0, 0, dividerHeight, 0);
		}
	}
}
