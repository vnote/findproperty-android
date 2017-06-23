package com.cetnaline.findproperty.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.inter.IRecycleViewItemClickListener;
import com.cetnaline.findproperty.inter.IRecycleViewListener;
import com.cetnaline.findproperty.inter.IRefreshType;
import com.cetnaline.findproperty.ui.adapter.AbstractWrapAdapter;

/**
 * 自定义RecycleView
 * Created by shengl on 2015/11/6.
 */
public abstract class AbstractRecyclerView extends RelativeLayout {

    protected LinearLayout ll_default_load;
    protected ImageView img_list_logo;
    protected AppCompatTextView atv_default;
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected RecyclerView recyclerView;
    protected Button load_data_again;

    protected LinearLayoutManager linearLayoutManager;
    protected StaggeredGridLayoutManager sGridLayoutManager;

    protected AbstractWrapAdapter wrapAdapter;
    protected IRecycleViewListener iRecycleViewListener;
    protected int iRefreshType = IRefreshType.DONE;
    protected boolean moreItemEnable = true;
    private boolean hasInit = false;
    private boolean refreshImmediately;//初始化完立即刷新

    /**
     * 上下拉监听&点击监听
     */
    private IRecycleViewListener listener = new IRecycleViewListener() {
        @Override
        public void downRefresh() {
            iRefreshType = IRefreshType.DOWN_REFRESH;
            if (iRecycleViewListener != null)
                iRecycleViewListener.downRefresh();
        }

        @Override
        public void upRefresh() {
            iRefreshType = IRefreshType.UP_REFRESH;
            if (iRecycleViewListener != null)
                iRecycleViewListener.upRefresh();
        }

        @Override
        public void onItemClick(int position) {
            if (iRecycleViewListener != null)
                iRecycleViewListener.onItemClick(position);
        }

        @Override
        public void onScroll() {
            if (iRecycleViewListener != null)
                iRecycleViewListener.onScroll();
        }

        @Override
        public void loadDataAgain() {
            if (iRecycleViewListener != null)
                iRecycleViewListener.loadDataAgain();
        }
    };

    /**
     * 点击监听
     */
    private IRecycleViewItemClickListener itemClickListener = new IRecycleViewItemClickListener() {
        @Override
        public void itemClick(int position, boolean fromUser) {
            if (fromUser) {
                listener.onItemClick(position);
            } else {//点击加载更多
                listener.upRefresh();
            }
        }
    };

    public AbstractRecyclerView(Context context) {
        this(context, null);
    }

    public AbstractRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AbstractRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.MRecyclerView, defStyleAttr, 0);
        int model = ta.getInt(R.styleable.MRecyclerView_LayoutModel, 0);
        ta.recycle();

        LayoutInflater.from(context).inflate(R.layout.layout_recycleview, this, true);
        ll_default_load = (LinearLayout) findViewById(R.id.ll_default_load);
        img_list_logo = (ImageView) findViewById(R.id.img_list_logo);
        load_data_again = (Button) findViewById(R.id.load_data_again);
        atv_default = (AppCompatTextView) findViewById(R.id.atv_default);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        img_list_logo.setImageResource(getLogo());

        final int colorSchemeResources = getSwipeRefreshLayoutColorSchemeResources();
        swipeRefreshLayout.setColorSchemeResources(colorSchemeResources, colorSchemeResources, colorSchemeResources, colorSchemeResources);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (listener != null) {
                    listener.downRefresh();
                }
            }
        });

        load_data_again.setOnClickListener(v -> {
            if (listener!=null){
//                setLoadDataBtVisible(View.GONE);
                listener.loadDataAgain();
            }
        });

        if (model==0){
            linearLayoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(linearLayoutManager);
        }else if (model==1){
            linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(linearLayoutManager);
        }else {
            sGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(sGridLayoutManager);
        }
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                listener.onScroll();
                final int count = wrapAdapter.getItemCount();

                final int lastCompletelyVisibleItemPosition;
                if (linearLayoutManager==null){
                    lastCompletelyVisibleItemPosition = sGridLayoutManager.findLastCompletelyVisibleItemPositions(null)[0];
                }else {
                    lastCompletelyVisibleItemPosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                }
                if (moreItemEnable &&
                        count > 10
                        && IRefreshType.DONE == iRefreshType
                        && wrapAdapter != null
                        && lastCompletelyVisibleItemPosition == count - 1) {
                    if (listener != null) {
                        listener.upRefresh();
                    }
                }
            }
        });

        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!hasInit) {
                    hasInit = true;
                    if (refreshImmediately) {
                        swipeRefreshLayout.setRefreshing(true);
                        if (listener != null) {
                            listener.downRefresh();
                        }
                    }
                }
            }
        });
    }

    /**
     * 设置中心图片logo
     */
    @DrawableRes
    protected abstract int getLogo();

    /**
     * 设置{@link SwipeRefreshLayout}颜色
     */
    @ColorRes
    protected abstract int getSwipeRefreshLayoutColorSchemeResources();

    public void setDefaultText(CharSequence charSequence) {
        atv_default.setText(charSequence);
    }

    public void setDefaultLogo(int logoRes){
        img_list_logo.setImageResource(logoRes);
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    /**
     * 设置Adapter
     */
    public void setWrapAdapter(AbstractWrapAdapter wrapAdapter) {
        this.wrapAdapter = wrapAdapter;
        recyclerView.setAdapter(wrapAdapter);
        wrapAdapter.setIRecycleViewItemClickListener(itemClickListener);
        refreshStatus();
    }

    /**
     * 设置监听
     */
    public void setIRecycleViewListener(IRecycleViewListener iRecycleViewListener) {
        this.iRecycleViewListener = iRecycleViewListener;
    }

    /**
     * 开始刷新
     */
    public void startRefresh() {
        if (hasInit) {
            recyclerView.smoothScrollToPosition(0);
            swipeRefreshLayout.setRefreshing(true);
            if (listener != null) {
                listener.downRefresh();
            }
        } else {
            refreshImmediately = true;
        }
    }

    /**
     * 停止刷新
     *
     * @param hasMore 是否还有更多
     */
    public void stopRefresh(boolean hasMore) {
        wrapAdapter.setMoreItemEnable(hasMore);
        refreshRecyclerView(true);
    }

    /**
     * 刷新出错<p>
     * 该方法会调用{@link #refreshRecyclerView(boolean)} ()}停止刷新
     */
    public void refreshError() {
        switch (iRefreshType) {
            case IRefreshType.UP_REFRESH:
                if (wrapAdapter != null)
                    wrapAdapter.loadMoreError();
                moreItemEnable = false;
                refreshRecyclerView(false);
                break;
            default:
                refreshRecyclerView(false);
                break;
        }
    }

    /**
     * 刷新状态
     */
    public void refreshStatus() {
        ll_default_load.setVisibility(wrapAdapter.getItemCount() > 0 ? INVISIBLE : VISIBLE);
        swipeRefreshLayout.setVisibility(wrapAdapter.getItemCount() > 0 ? VISIBLE : INVISIBLE);
//        setLoadDataBtVisible(wrapAdapter.getItemCount() > 0 ? GONE : VISIBLE);
//        atv_default.setVisibility(wrapAdapter.getItemCount() > 0 ? INVISIBLE : VISIBLE);
    }

    public void notifyItemInserted(int position){
        wrapAdapter.notifyItemInserted(position);
        refreshStatus();
    }

    /**
     * 移除
     */
    public void notifyItemRemoved(int position) {
        wrapAdapter.notifyItemRemoved(position);
        refreshStatus();
    }

    /**
     * 更新
     */
    public void notifyItemChanged(int position) {
        wrapAdapter.notifyItemChanged(position);
        refreshStatus();
    }

    public void toTopPosition(boolean toTop){
        if (toTop){
            recyclerView.scrollToPosition(0);
        }
    }

    public RecyclerView.Adapter getAdapter(){
        return wrapAdapter;
    }

    /**
     * 刷新
     *
     * @param needRefresh 是否需要刷新页面
     */
    private void refreshRecyclerView(boolean needRefresh) {
        switch (iRefreshType) {
            case IRefreshType.DOWN_REFRESH:
                iRefreshType = IRefreshType.DONE;
                swipeRefreshLayout.setRefreshing(false);
                break;
            case IRefreshType.UP_REFRESH:
                iRefreshType = IRefreshType.DONE;
                break;
            default:
                break;
        }
        if (needRefresh
                && wrapAdapter != null) {

//            Handler handler = new Handler();
//            Runnable r = new Runnable() {
//                public void run() {
//                    wrapAdapter.notifyDataSetChanged();
//                }
//            };
//            handler.post(r);

            wrapAdapter.notifyDataSetChanged();

            refreshStatus();
        }
    }

    public void setLoadDataBtVisible(int visible){
        load_data_again.setVisibility(visible);
        swipeRefreshLayout.setVisibility(visible== View.VISIBLE ? INVISIBLE : VISIBLE);
    }
}
