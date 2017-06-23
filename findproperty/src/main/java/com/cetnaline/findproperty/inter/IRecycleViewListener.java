package com.cetnaline.findproperty.inter;

/**
 * 自定义 的监听
 */
public interface IRecycleViewListener {

    /**
     * 下拉刷新
     */
    void downRefresh();

    /**
     * 更多加载
     */
    void upRefresh();

    /**
     * item 点击
     *
     * @param position 位置
     */
    void onItemClick(int position);

    void onScroll();

    void loadDataAgain();
}
