package com.cetnaline.findproperty.api.bean;

import java.util.List;

/**
 * Created by fanxl2 on 2016/8/28.
 */
public class IntentionBo {

	private long IntentionID;

	private String Source;

	private List<SearchParam> SearchPara;

	public String getSource() {
		return Source;
	}

	public void setSource(String source) {
		Source = source;
	}

	public long getIntentionID() {
		return IntentionID;
	}

	public void setIntentionID(long intentionID) {
		IntentionID = intentionID;
	}

	public List<SearchParam> getSearchPara() {
		return SearchPara;
	}

	public void setSearchPara(List<SearchParam> searchPara) {
		SearchPara = searchPara;
	}
}
