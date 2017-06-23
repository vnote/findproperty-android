package com.cetnaline.findproperty.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhy.autolayout.utils.AutoUtils;

public class ViewHolder {
	private final SparseArray<View> mViews;
	private int mPosition;
	private View mConvertView;
	private ViewGroup parent;
	private Context mContext;

	private ViewHolder(Context context, ViewGroup parent, int layoutId,
					   int position) {
		this.mPosition = position;
		this.mViews = new SparseArray<>();
		this.parent = parent;
		mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
		// setTag
		mConvertView.setTag(this);
		//对于listview，注意添加这一行，即可在item上使用高度
		AutoUtils.autoSize(mConvertView);
	}

	public Context getmContext() {
		return mContext;
	}

	/**
	 * 拿到一个ViewHolder对象
	 *
	 * @param context
	 * @param convertView
	 * @param parent
	 * @param layoutId

	 * @param position
	 * @return
	 */
	public static ViewHolder get(Context context, View convertView,
								 ViewGroup parent, int layoutId, int position) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder(context, parent, layoutId, position);
		} else {
			holder = (ViewHolder) convertView.getTag();
			holder.mPosition = position;
		}
		return holder;
	}

	public View getConvertView() {
		return mConvertView;
	}

	public ViewGroup getParent() {
		return parent;
	}

	/**
	 * 通过控件的Id获取对于的控件，如果没有则加入views
	 *
	 * @param viewId
	 * @return
	 */
	public <T extends View> T getView(int viewId) {
		View view = mViews.get(viewId);
		if (view == null) {
			view = mConvertView.findViewById(viewId);
			mViews.put(viewId, view);
		}
		return (T) view;
	}

	public <TB extends View> TB getViewByType(Class<TB> a, int viewId) {
		return (TB) getView(viewId);
	}
	public TextView getViewByType(int viewId) {
		return (TextView) getView(viewId);
	}
	public <T extends View> T getView(int viewId, View currentView) {
		View view = mViews.get(viewId);
		if (view == null) {
			mViews.put(viewId, currentView);
			return (T) currentView;
		}
		return (T) view;
	}

	/**
	 * 为TextView设置字符串
	 *
	 * @param viewId
	 * @param text
	 * @return
	 */
	public ViewHolder setText(int viewId, String text) {
		getViewByType(TextView.class, viewId).setText(text);
		return this;
	}

	/**
	 * 为ImageView设置图片
	 *
	 * @param viewId
	 * @param drawableId
	 * @return
	 */
	public ViewHolder setImageResource(int viewId, int drawableId) {
		getViewByType(ImageView.class, viewId).setImageResource(drawableId);
		return this;
	}

	/**
	 * 为ImageView设置图片
	 *
	 * @param viewId
	 * @param bm
	 * @return
	 */
	public ViewHolder setImageBitmap(int viewId, Bitmap bm) {
		getViewByType(ImageView.class, viewId).setImageBitmap(bm);
		return this;
	}


	public int getPosition() {
		return mPosition;
	}

}
