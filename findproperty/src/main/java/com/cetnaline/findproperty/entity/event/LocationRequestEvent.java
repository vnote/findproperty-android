package com.cetnaline.findproperty.entity.event;

/**
 * Created by diaoqf on 2017/5/11.
 */

public class LocationRequestEvent {
    public static final int REQUEST_SUCCESS = 1;
    public static final int REQUEST_FAILED = 0;

    private String requestSource;
    private int requestResult;

    public LocationRequestEvent(String requestSource, int requestResult) {
        this.requestSource = requestSource;
        this.requestResult = requestResult;
    }

    public String getRequestSource() {
        return requestSource;
    }

    public void setRequestSource(String requestSource) {
        this.requestSource = requestSource;
    }

    public int getRequestResult() {
        return requestResult;
    }

    public void setRequestResult(int requestResult) {
        this.requestResult = requestResult;
    }
}
