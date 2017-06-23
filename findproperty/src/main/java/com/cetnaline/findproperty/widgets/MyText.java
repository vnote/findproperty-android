package com.cetnaline.findproperty.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.cetnaline.findproperty.R;

/**
 * 自定义控件
 * @author fanxl2
 *
 */
public class MyText extends View {

	/** 标题文本内容 */
	private String rightText;
	/** 标题文本字体大小 */
	private int rightTextSize;
	/** 标题文本字体颜色 */
	private int rightTextColor;
	/** 标题文本区域左padding */
	private int rightPaddingLeft;
	/** 标题文本区域上padding */
	private int rightPaddingTop;
	/** 标题文本区域右padding */
	private int titlePaddingRight;
	/** 标题文本区域下padding */
	private int titlePaddingBottom;

	/** 子标题文本内容 */
	private String leftText;
	/** 子标题文本字体大小 */
	private int leftTextSize;
	/** 子标题文本字体颜色 */
	private int leftTextColor;
	/** 子标题文本区域左padding */
	private int leftPaddingLeft;
	/** 子标题文本区域上padding */
	private int leftPaddingTop;
	/** 子标题文本区域右padding */
	private int leftPaddingRight;
	/** 子标题文本区域下padding */
	private int leftPaddingBottom;

	/** 控件用的paint */
	private Paint paint;
	private TextPaint textPaint;
	/** 用来界定控件中不同部分的绘制区域 */
	private Rect rect;
	/** 宽度和高度的最小值 */
	private static final int MIN_SIZE = 12;
	/** 控件的宽度 */
	private int mViewWidth;
	/** 控件的高度 */
	private int mViewHeight;


	public MyText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		//设置默认值
		leftTextColor = Color.BLACK;
		rightTextColor = Color.BLACK;
		leftTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics());
		rightTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics());

		TypedArray a = context.getTheme().obtainStyledAttributes(
				attrs, R.styleable.MyText, defStyle, 0);

		int n = a.getIndexCount();
		for (int i = 0; i < n; i++) {
			int attr = a.getIndex(i);
			switch (attr) {
				case R.styleable.MyText_rightText:
					rightText = a.getString(attr);
					break;
				case R.styleable.MyText_rightTextSize:
					rightTextSize = a.getDimensionPixelSize(attr, rightTextSize);
					break;
				case R.styleable.MyText_rightTextColor:
					rightTextColor = a.getColor(attr, rightTextColor);
					break;
				case R.styleable.MyText_rightPaddingLeft:
					rightPaddingLeft = a.getDimensionPixelSize(attr, 0);
					break;
				case R.styleable.MyText_rightPaddingTop:
					rightPaddingTop = a.getDimensionPixelSize(attr, 0);
					break;
				case R.styleable.MyText_rightPaddingRight:
					titlePaddingRight = a.getDimensionPixelSize(attr, 0);
					break;
				case R.styleable.MyText_rightPaddingBottom:
					titlePaddingBottom = a.getDimensionPixelSize(attr, 0);
					break;
				case R.styleable.MyText_leftText:
					leftText = a.getString(attr);
					break;
				case R.styleable.MyText_leftTextSize:
					leftTextSize = a.getDimensionPixelSize(attr, leftTextSize);
					break;
				case R.styleable.MyText_leftTextColor:
					leftTextColor = a.getColor(attr, leftTextColor);
					break;
				case R.styleable.MyText_leftPaddingLeft:
					leftPaddingLeft = a.getDimensionPixelSize(attr, 0);
					break;
				case R.styleable.MyText_leftPaddingTop:
					leftPaddingTop = a.getDimensionPixelSize(attr, 0);
					break;
				case R.styleable.MyText_leftPaddingRight:
					leftPaddingRight = a.getDimensionPixelSize(attr, 0);
					break;
				case R.styleable.MyText_leftPaddingBottom:
					leftPaddingBottom = a.getDimensionPixelSize(attr, 0);
					break;
			}
		}
		a.recycle();

		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		textPaint = new TextPaint(paint);
		rect = new Rect();
	}

	public MyText(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MyText(Context context) {
		this(context, null);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		int width;
		int height;

		if (widthMode == MeasureSpec.EXACTLY) {
			width = widthSize;
		} else {
			int desired = getPaddingLeft() + getPaddingRight();

			float titleTextWidth = 0.0f, subTextWidth = 0.0f;

			//根据文字的最大宽度作为宽度
			if (rightText != null) {
				paint.setTextSize(rightTextSize);
				titleTextWidth = paint.measureText(rightText);
			}

			if (leftText != null) {
				paint.setTextSize(leftTextSize);
				subTextWidth = paint.measureText(leftText);
			}

//			if(titleTextWidth>subTextWidth){
//				desired += (titleTextWidth + rightPaddingLeft + titlePaddingRight);
//			}else{
//				desired += (subTextWidth + leftPaddingLeft + leftPaddingRight);
//			}

			desired += (subTextWidth + titleTextWidth +leftPaddingLeft + leftPaddingRight
					+ rightPaddingLeft + titlePaddingRight);

			width = Math.max(MIN_SIZE, desired);
			if (widthMode == MeasureSpec.AT_MOST) {
				width = Math.min(desired, widthSize);
			}
		}

		if (heightMode == MeasureSpec.EXACTLY) {
			height = heightSize;
		} else {
			int desired = getPaddingTop() + getPaddingBottom();
			float titleTextHeight = 0.0f, subTextHeight = 0.0f;

			if (rightText != null) {
				paint.setTextSize(rightTextSize);
				FontMetrics fm = paint.getFontMetrics();
				titleTextHeight = (int) Math.ceil(fm.descent - fm.ascent);
				//desired += (textHeight + rightPaddingTop + titlePaddingBottom);
			}

			if (leftText != null) {
				paint.setTextSize(leftTextSize);
				FontMetrics fm = paint.getFontMetrics();
				subTextHeight = (int) Math.ceil(fm.descent - fm.ascent);
				//desired += (textHeight + leftPaddingTop + leftPaddingBottom);
			}

			if(subTextHeight>titleTextHeight){
				desired += (subTextHeight + leftPaddingTop + leftPaddingBottom);
			}else{
				desired += (titleTextHeight + rightPaddingTop + titlePaddingBottom);
			}

			height = Math.max(MIN_SIZE, desired);
			if (heightMode == MeasureSpec.AT_MOST) {
				height = Math.min(desired, heightSize);
			}
		}
		//System.out.println("控件的宽度:"+width+"---控件的高度:"+height);
		setMeasuredDimension(width, height);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mViewWidth = w;
		mViewHeight = h;
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		rect.left = getPaddingLeft();
		rect.top = getPaddingTop();
		rect.right = mViewWidth - getPaddingRight();
		rect.bottom = mViewHeight - getPaddingBottom();

		paint.setAlpha(255);

		float x1 = 0.0f;

		float titleWidth = 0;
		if(rightText != null){
			paint.setTextSize(rightTextSize);
			paint.setColor(rightTextColor);
			paint.setTextAlign(Paint.Align.LEFT);
			titleWidth = paint.measureText(rightText); //居中计算
		}


		if (leftText != null) {
			paint.setTextSize(leftTextSize);
			paint.setColor(leftTextColor);
			paint.setTextAlign(Paint.Align.LEFT);

			int left = getPaddingLeft() + leftPaddingLeft;
			int right = mViewWidth - getPaddingRight() - leftPaddingRight;
			//int bottom = mViewHeight - getPaddingBottom() - leftPaddingBottom;
			int bottom = rect.bottom - getPaddingBottom()-5;

			String msg = TextUtils.ellipsize(leftText, textPaint, right - left, TextUtils.TruncateAt.END).toString();

			float textWidth = paint.measureText(msg); //居中计算

			float x = (mViewWidth- textWidth- titleWidth)/2;
			canvas.drawText(msg, x, bottom, paint);
			x1 = x + textWidth;
		}

		if (rightText != null) {
			paint.setTextSize(rightTextSize);
			paint.setColor(rightTextColor);
			paint.setTextAlign(Paint.Align.LEFT);

			float left = getPaddingLeft() + rightPaddingLeft;
			float right = mViewWidth - getPaddingRight() - titlePaddingRight;
			//float bottom = rect.bottom - titlePaddingBottom;
			int bottom = rect.bottom - getPaddingBottom()-5;

			//String msg = TextUtils.ellipsize(titleText, textPaint, right - left, TextUtils.TruncateAt.END).toString();
			canvas.drawText(rightText, x1, bottom, paint);
		}
	}

	public void setRightText(String text) {
		rightText = text;
		requestLayout();
		invalidate();
	}

	public void setLeftText(String text) {
		leftText = text;
		requestLayout();
		invalidate();
	}

	public void setLeftAndRight(String left, String right){
		leftText = left;
		rightText = right;
		requestLayout();
		invalidate();
	}
}
