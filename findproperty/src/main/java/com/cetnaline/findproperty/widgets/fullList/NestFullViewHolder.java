package com.cetnaline.findproperty.widgets.fullList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by fanxl2 on 2016/10/10.
 */

public class NestFullViewHolder {

	private SparseArray<View> mViews;
	private View mConvertView;
	private Context mContext;

	public NestFullViewHolder(View mConvertView, Context mContext) {
		this.mConvertView = mConvertView;
		this.mContext = mContext;
		mViews = new SparseArray<>();
	}

	public View getConvertView() {
		return mConvertView;
	}

	public <T extends View> T getView(int viewId){
		View view = mViews.get(viewId);
		if (view==null){
			view = mConvertView.findViewById(viewId);
			mViews.put(viewId, view);
		}
		return (T)view;
	}

	public NestFullViewHolder setText(int viewId, String text){
		TextView tv = getView(viewId);
		tv.setText(text);
		return this;
	}

	public NestFullViewHolder setImageResource(int viewId, int resId) {
		ImageView view = getView(viewId);
		view.setImageResource(resId);
		return this;
	}

	public NestFullViewHolder setImageBitmap(int viewId, Bitmap bitmap) {
		ImageView view = getView(viewId);
		view.setImageBitmap(bitmap);
		return this;
	}

	public NestFullViewHolder setImageDrawable(int viewId, Drawable drawable) {
		ImageView view = getView(viewId);
		view.setImageDrawable(drawable);
		return this;
	}

	public NestFullViewHolder setBackgroundColor(int viewId, int color) {
		View view = getView(viewId);
		view.setBackgroundColor(color);
		return this;
	}

	public NestFullViewHolder setBackgroundRes(int viewId, int backgroundRes) {
		View view = getView(viewId);
		view.setBackgroundResource(backgroundRes);
		return this;
	}

	public NestFullViewHolder setTextColor(int viewId, int textColor) {
		TextView view = getView(viewId);
		view.setTextColor(textColor);
		return this;
	}

}
