package com.cetnaline.findproperty.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.cetnaline.findproperty.R;

/**
 * Created by fanxl2 on 2016/12/6.
 */

public class RoundImageView extends View {

	private Bitmap mSrc;

	private int type;
	private static final int TYPE_CIRCLE = 0;
	private static final int TYPE_ROUND = 1;

	private int mRadius;

	private int mWidth;

	private int mHeight;

	public RoundImageView(Context context) {
		this(context, null);
	}

	public RoundImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RoundImageView, defStyleAttr, 0);
		int count = ta.getIndexCount();
		for (int i = 0; i < count; i++) {
			int index = ta.getIndex(i);
			switch (index) {
				case R.styleable.RoundImageView_borderRadius:
					mRadius = ta.getDimensionPixelSize(index, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f,
							getResources().getDisplayMetrics()));
					break;
				case R.styleable.RoundImageView_src:
					mSrc = BitmapFactory.decodeResource(getResources(), ta.getResourceId(index, 0));
					break;
				case R.styleable.RoundImageView_type:
					type = ta.getInt(index, TYPE_CIRCLE);
					break;
			}
		}
		ta.recycle();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);

		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		if (widthMode == MeasureSpec.EXACTLY) {
			mWidth = widthSize;
		} else {
			int desireByImg = getPaddingLeft() + getPaddingRight() + mSrc.getWidth();
			if (widthMode == MeasureSpec.AT_MOST) {
				mWidth = Math.min(widthSize, desireByImg);
			} else {
				mWidth = desireByImg;
			}
		}

		if (heightMode == MeasureSpec.EXACTLY) {
			mHeight = heightSize;
		} else {
			int desireByImg = getPaddingTop() + getPaddingBottom() + mSrc.getHeight();
			if (heightMode == MeasureSpec.AT_MOST) {
				mHeight = Math.min(heightSize, desireByImg);
			} else {
				mHeight = desireByImg;
			}
		}
		setMeasuredDimension(mWidth, mHeight);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		switch (type) {
			// 如果是TYPE_CIRCLE绘制圆形
			case TYPE_CIRCLE:
				int min = Math.min(mWidth, mHeight);
				mSrc = Bitmap.createScaledBitmap(mSrc, min, min, false);
				canvas.drawBitmap(createCircleImage(mSrc, min), 0, 0, null);
				break;
			case TYPE_ROUND:
				canvas.drawBitmap(createRoundConerImage(mSrc), 0, 0, null);
				break;

		}
	}

	private Bitmap createRoundConerImage(Bitmap source) {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		Bitmap target = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(target);
		RectF rect = new RectF(0, 0, mSrc.getWidth(), mSrc.getHeight());
		canvas.drawRoundRect(rect, mRadius, mRadius, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(source, 0, 0, paint);
		return target;
	}

	private Bitmap createCircleImage(Bitmap mSrc, int min) {

		Paint paint = new Paint();
		paint.setAntiAlias(true);
		Bitmap target = Bitmap.createBitmap(min, min, Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(target);
		canvas.drawCircle(min/2, min/2, min/2, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(mSrc, 0, 0, paint);
		return target;
	}
}
