package com.cetnaline.findproperty.inter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * 列表-更多item
 */
public interface IMoreItem<MVH extends RecyclerView.ViewHolder> {

    int getMoreItemId();

    /**
     * 更多item type
     *
     * @return int
     */
    int getMoreItemViewType();

    /**
     * 创建view
     */
    RecyclerView.ViewHolder onCreateMoreViewHolder(ViewGroup parent);

    /**
     * 绑定view
     */
    void onBindMoreViewHolder(MVH holder);
}
