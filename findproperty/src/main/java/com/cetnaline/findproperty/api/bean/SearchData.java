package com.cetnaline.findproperty.api.bean;

import com.cetnaline.findproperty.db.entity.DropBo;

import java.util.List;

public class SearchData {

    private String Name;
    private List<DropBo> SearchDataItemList;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public List<DropBo> getSearchDataItemList() {
        return SearchDataItemList;
    }

    public void setSearchDataItemList(List<DropBo> searchDataItemList) {
        SearchDataItemList = searchDataItemList;
    }
}
