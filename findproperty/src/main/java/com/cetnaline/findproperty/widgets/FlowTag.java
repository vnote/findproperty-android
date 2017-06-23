package com.cetnaline.findproperty.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import java.util.ArrayList;
import java.util.List;

public class FlowTag extends ViewGroup {

	public FlowTag(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FlowTag(Context context) {
		this(context, null);
	}

	private List<List<View>> allViews = new ArrayList<List<View>>();

	//存的每行的高度
	private List<Integer> mLineHeight = new ArrayList<Integer>();

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
		int modeWidth = MeasureSpec.getMode(widthMeasureSpec);

		int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
		int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

		//Wrap_content 这个就需要自己去计算， 如果是EXACITY，上面的方法就可以获取到
		int width = 0;
		int height = 0;

		//记录每一行的高度和宽度
		int lineHeight = 0;
		int lineWidth = 0;

		int count = getChildCount();

		for (int i = 0; i < count; i++) {

			View child = getChildAt(i);
			measureChild(child, widthMeasureSpec, heightMeasureSpec);
			// 得到LayoutParams
			MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
			// 子View占据的宽度
			int childWidth = child.getMeasuredWidth() + lp.leftMargin+ lp.rightMargin;
			// 子View占据的高度
			int childHeight = child.getMeasuredHeight() + lp.topMargin+ lp.bottomMargin;

			if(lineWidth + childWidth > sizeWidth - getPaddingLeft() - getPaddingRight()){
				width = Math.max(width, lineWidth);
				// 重置lineWidth
				lineWidth = childWidth;
				// 记录行高
				height += lineHeight;
				lineHeight = childHeight;
			}else{
				lineWidth += childWidth;
				lineHeight = Math.max(lineHeight, childHeight);
			}

			if(i == count - 1){
				width = Math.max(lineWidth, width);
				height += lineHeight;
			}
		}
		setMeasuredDimension(modeWidth == MeasureSpec.EXACTLY?sizeWidth:width + getPaddingLeft() + getPaddingRight(),
				modeHeight== MeasureSpec.EXACTLY?sizeHeight:height+getPaddingTop()+getPaddingBottom());

	}

	@SuppressLint("DrawAllocation") @Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		allViews.clear();
		mLineHeight.clear();
		// 当前ViewGroup的宽度
		int width = getWidth();

		int lineWidth = 0;
		int lineHeight = 0;

		List<View> lineViews = new ArrayList<View>();

		int cCount = getChildCount();
		for (int i = 0; i < cCount; i++) {

			View child = getChildAt(i);
			MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

			int childWidth = child.getMeasuredWidth();
			int childHeight = child.getMeasuredHeight();

			if(childWidth+lineWidth+lp.leftMargin+lp.rightMargin>width-getPaddingLeft()-getPaddingRight()){
				// 记录LineHeight
				mLineHeight.add(lineHeight);
				// 记录当前行的Views
				allViews.add(lineViews);

				// 重置我们的行宽和行高
				lineWidth = 0;
				lineHeight = childHeight + lp.topMargin + lp.bottomMargin;
				// 重置我们的View集合
				lineViews = new ArrayList<View>();
			}

			lineWidth += childWidth + lp.leftMargin + lp.rightMargin;
			lineHeight = Math.max(lineHeight, childHeight + lp.topMargin
					+ lp.bottomMargin);
			lineViews.add(child);
		}

		// 处理最后一行
		mLineHeight.add(lineHeight);
		allViews.add(lineViews);

		// 设置子View的位置
		int left = getPaddingLeft();
		int top = getPaddingTop();

		int lineNum = allViews.size();

		for (int i = 0; i < lineNum; i++) {
			// 当前行的所有的View
			lineViews = allViews.get(i);
			lineHeight = mLineHeight.get(i);

			for (int j = 0; j < lineViews.size(); j++) {
				View child = lineViews.get(j);
				MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

				int lc = left + lp.leftMargin;
				int tc = top + lp.topMargin;
				int rc = lc + child.getMeasuredWidth();
				int bc = tc + child.getMeasuredHeight();
				child.layout(lc, tc, rc, bc);

				left += child.getMeasuredWidth() + lp.leftMargin+ lp.rightMargin;
			}
			left = getPaddingLeft() ;
			top += lineHeight ;
		}
	}


	/**
	 * 与当前ViewGroup对应的LayoutParams
	 */
	@Override
	public LayoutParams generateLayoutParams(AttributeSet attrs)
	{
		return new MarginLayoutParams(getContext(), attrs);
	}

	public void clearSelectedStatus(CheckBox currentCb){
		int childNum = getChildCount();
		for (int i = 0; i < childNum; i++) {
			CheckBox cb = (CheckBox) getChildAt(i);
			cb.setChecked(false);
		}
		currentCb.setChecked(true);
	}

}
