package com.cetnaline.findproperty.inter;

/**
 * 自定义控件 点击监听
 */
public interface IRecycleViewItemClickListener {
    /**
     * @param position 点击位置
     * @param fromUser 更多Item点击时为false;
     */
    void itemClick(int position, boolean fromUser);

}
