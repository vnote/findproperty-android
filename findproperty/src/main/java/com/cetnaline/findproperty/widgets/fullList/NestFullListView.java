package com.cetnaline.findproperty.widgets.fullList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fanxl2 on 2016/10/10.
 */

public class NestFullListView extends LinearLayout {

	private LayoutInflater mInflater;
	private List<NestFullViewHolder> mVHCaches;//缓存ViewHolder,按照add的顺序缓存，

	public NestFullListView(Context context) {
		this(context, null);
	}

	public NestFullListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public NestFullListView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	private void init(Context context) {
		mInflater = LayoutInflater.from(context);
		mVHCaches = new ArrayList<>();
	}

	private NestFullListViewAdapter mAdapter;

	/**
	 * 外部调用  同时刷新视图
	 *
	 * @param mAdapter
	 */
	public void setAdapter(NestFullListViewAdapter mAdapter) {
		this.mAdapter = mAdapter;
		updateUI();
	}

	public void updateUI() {
		if (mAdapter!=null){
			if (mAdapter.getDatas()!=null && !mAdapter.getDatas().isEmpty()){
				//如果数据比缓存的view还多，则清除多余的view和缓存
				if (mAdapter.getDatas().size() < getChildCount()){
					//删除view
					removeViews(mAdapter.getDatas().size(), getChildCount()-mAdapter.getDatas().size());
					//删除缓存
					while (mVHCaches.size() > mAdapter.getDatas().size()){
						mVHCaches.remove(mVHCaches.size()-1);
					}
				}

				for (int i=0; i<mAdapter.getDatas().size(); i++){
					NestFullViewHolder holder;
					if (mVHCaches.size() - 1 >= i) {//说明有缓存，不用inflate，否则inflate
						holder = mVHCaches.get(i);
					} else {
						holder = new NestFullViewHolder(mInflater.inflate(mAdapter.getItemLayoutId(), this, false), getContext());
						mVHCaches.add(holder);
					}
					mAdapter.onBind(i, holder);
					if (holder.getConvertView().getParent()==null){
						this.addView(holder.getConvertView());
					}
				}
			}else {
				removeAllViews();
			}
		}else {
			removeAllViews();
		}
	}
}
