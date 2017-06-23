package com.cetnaline.findproperty.widgets.dropdown;

import com.cetnaline.findproperty.db.entity.DropBo;

/**
 * 下拉菜单完成
 * Created by guilin on 16/1/27.
 */
public interface DropCompleteListener {
    /**
     * @param position 下拉菜单位置
     * @param fromMore 是否来源更多
     * @param type     下拉菜单类型
     * @param dropBos  数据
     */
    void complete(int position, boolean fromMore, int type,
                  DropBo... dropBos);
}
