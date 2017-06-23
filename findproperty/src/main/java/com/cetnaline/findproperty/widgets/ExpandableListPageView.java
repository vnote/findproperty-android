package com.cetnaline.findproperty.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cetnaline.findproperty.R;

/**
 * Created by fanxl2 on 2017/1/13.
 */

public class ExpandableListPageView extends ExpandableListView implements AbsListView.OnScrollListener {

	private Boolean canLoad = false; // 是否能够加载数据

	private int currentPageIndex = 1; // 记录页索引

	public static final int PAGE_SIZE = 100; // 每页显示项目数

	public String loadMessage = "正在加载...."; // 进度条提示消息

	public LinearLayout footerLayout;// 页脚

	public OnPageLoadListener listener;// 分布侦听事件

	public ExpandableListPageView(Context context) {
		this(context, null);
	}

	public ExpandableListPageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ExpandableListPageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);


	}

	@Override
	public void setAdapter(ExpandableListAdapter adapter) {
		this.setOnScrollListener(this);
		// 必须在setAdapter()方法之前构建进度条并添加到页脚
		this.BuildProgressBar();
		super.setAdapter(adapter);
	}

	/**
	 * 创建页脚显示进度条,必须在setAdapter()方法之前调用.
	 */
	private void BuildProgressBar() {
		if (this.getFooterViewsCount() != 0) {
			return;
		}
		footerLayout = new LinearLayout(this.getContext());
		footerLayout.setGravity(Gravity.CENTER);
		footerLayout.setPadding(0, 0, 0, 0);
		footerLayout.setOrientation(LinearLayout.HORIZONTAL);
		ProgressBar bar = new ProgressBar(this.getContext());
		footerLayout.addView(bar);
		footerLayout.setBackgroundResource(R.color.white);
		TextView txt = new TextView(this.getContext());
		txt.setText(this.loadMessage);
		footerLayout.addView(txt);
		footerLayout.setVisibility(View.GONE);
		this.addFooterView(footerLayout);
	}

	/**
	 * 是否显示进度条
	 *
	 * @param isVisible true:显示,false:不显示
	 */
	public void setProgressBarVisible(Boolean isVisible) {
		if (this.footerLayout == null) {
			return;
		}
		int visibility = View.VISIBLE;
		if (!isVisible) {
			visibility = View.GONE;
		}
		// 定位到最后一行,必须设置，要不然进度条看不到
		this.setSelection(this.getAdapter().getCount());
		this.footerLayout.setVisibility(visibility);
		// 设置页脚中组件的显示状态
		for (int i = 0; i < this.footerLayout.getChildCount(); i++) {
			View v = this.footerLayout.getChildAt(i);
			v.setVisibility(visibility);
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (this.canLoad && (scrollState == OnScrollListener.SCROLL_STATE_IDLE)) {
			// 加载数据
			if (listener != null) {
				this.currentPageIndex++;
				listener.onPageChanging(this.PAGE_SIZE, currentPageIndex);
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		this.canLoad = false;
		if (this.listener == null) {
			return;
		}
		if ((firstVisibleItem + visibleItemCount) == totalItemCount) {
			this.canLoad = this.listener.canLoadData();
		}
	}

	public interface OnPageLoadListener {

		void onPageChanging(int pageSize, int pageIndex);

		boolean canLoadData();
	}

	public void setOnPageLoadListener(OnPageLoadListener listener) {
		this.listener = listener;
	}
}
