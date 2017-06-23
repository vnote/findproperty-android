package com.cetnaline.findproperty.inter;

/**
 * 刷新监听
 * Created by guilin on 16/1/26.
 */
public interface RefreshListener {

    void down();

    void up(int count);

    void loadDataAgain();
}
