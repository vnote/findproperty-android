package com.cetnaline.findproperty.ui.adapter;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.cetnaline.findproperty.inter.IMoreItem;
import com.cetnaline.findproperty.inter.IRecycleViewItemClickListener;
import com.cetnaline.findproperty.utils.WrapperUtils;

/**
 * 加载更多Adapter包装
 */
public abstract class AbstractWrapAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements IMoreItem<RecyclerView.ViewHolder> {

    protected boolean moreItemEnable = true;//加载更多是否可用
    protected final RecyclerView.Adapter<RecyclerView.ViewHolder> adapter;//用户adpter
    protected int userAdapterCount;//用户数据总数
    protected IRecycleViewItemClickListener iRecycleViewItemClickListener;
    protected IRecycleViewItemClickListener itemClickListener = new IRecycleViewItemClickListener() {
        @Override
        public void itemClick(int position, boolean fromUser) {
            if (iRecycleViewItemClickListener != null)
                iRecycleViewItemClickListener.itemClick(position, fromUser);
        }
    };

    /**
     * @param adapter {@link android.support.v7.widget.RecyclerView.Adapter}
     */
    public AbstractWrapAdapter(RecyclerView.Adapter<RecyclerView.ViewHolder> adapter) {
        if (adapter == null)
            throw new NullPointerException();
        this.adapter = adapter;
        setHasStableIds(adapter.hasStableIds());
    }

    /**
     * 设置点击监听
     *
     * @param iRecycleViewItemClickListener {@link IRecycleViewItemClickListener}
     */
    public void setIRecycleViewItemClickListener(IRecycleViewItemClickListener iRecycleViewItemClickListener) {
        this.iRecycleViewItemClickListener = iRecycleViewItemClickListener;
    }

    /**
     * @param moreItemEnable 更多item是否可用
     */
    public void setMoreItemEnable(boolean moreItemEnable) {
        if (!moreItemEnable) {
            alreadyLoaded(2);
        } else {
            alreadyLoaded(0);
            this.moreItemEnable = moreItemEnable;
        }
    }

    /**
     * 加载更多出错
     */
    public abstract void loadMoreError();

    public void alreadyLoaded(int state){

    }

    @Override
    public int getItemCount() {//更多可用&用户数据>0,添加更多item
        userAdapterCount = adapter.getItemCount();
        return moreItemEnable && userAdapterCount > 9 ? userAdapterCount + 1 : userAdapterCount;
    }

    @Override
    public long getItemId(int position) {
        return position < userAdapterCount ? adapter.getItemId(position) : getMoreItemId();
    }

    @Override
    public int getItemViewType(int position) {
        return position < userAdapterCount ? adapter.getItemViewType(position) : getMoreItemViewType();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == getMoreItemViewType()) {//创建更多Item
            return onCreateMoreViewHolder(parent);
        } else {//用户Item
            return adapter.onCreateViewHolder(parent, viewType);
        }
    }

    private int lastPosition = -1;

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (getMoreItemViewType() == getItemViewType(position)) {
            onBindMoreViewHolder(holder);
        } else {
            adapter.bindViewHolder(holder, position);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.itemClick(holder.getAdapterPosition(), true);
                }
            });
            //item 加载动画
            if (position > lastPosition)
            {
                Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), android.R.anim.fade_in);
                holder.itemView.startAnimation(animation);
                lastPosition = position;
            }
        }
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        holder.itemView.clearAnimation();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {

        WrapperUtils.onAttachedToRecyclerView(adapter, recyclerView, new WrapperUtils.SpanSizeCallback(){

            @Override
            public int getSpanSize(GridLayoutManager layoutManager, GridLayoutManager.SpanSizeLookup oldLookup, int position) {

                int viewType = getItemViewType(position);
                if (viewType==99){
                    return layoutManager.getSpanCount();
                }
                if (oldLookup != null)
                    return oldLookup.getSpanSize(position);
                return 1;
            }
        });
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {

        adapter.onViewAttachedToWindow(holder);
        int position = holder.getLayoutPosition();
        if (getItemViewType(position)==99){
            WrapperUtils.setFullSpan(holder);
        }
    }
}
