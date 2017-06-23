package com.cetnaline.findproperty.inter;

/**
 * Created by diaoqf on 2016/8/16.
 */
public interface ReserveItemClick {

    void itemClick(int groupPosition, int childPosition);

    void check(boolean isChecked, int groupPosition, int childPosition);
}
