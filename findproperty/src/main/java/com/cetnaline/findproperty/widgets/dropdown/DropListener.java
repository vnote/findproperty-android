package com.cetnaline.findproperty.widgets.dropdown;

import com.cetnaline.findproperty.db.entity.DropBo;

/**
 * 下拉监听
 * Created by guilin on 16/1/27.
 */
public interface DropListener {

    /**
     * @param fromMore 是否来源更多
     * @param type     下拉菜单类型
     * @param dropBos  数据
     */
    void dropComplete(boolean fromMore, int type, DropBo... dropBos);

    void dropDismiss(boolean isSelected);
}
