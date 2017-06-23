package com.cetnaline.findproperty.api.request;

import com.cetnaline.findproperty.api.NetWorkManager;
import com.cetnaline.findproperty.api.service.RongCloudApi;

/**
 * Created by diaoqf on 2016/7/15.
 */
public class RongRequest {
    //声明service
    private static RongCloudApi rongCloudApi;

    private RongRequest(){}

    public static RongCloudApi getRongCloudApi(){
        if (rongCloudApi == null) {
            rongCloudApi = NetWorkManager.getRongCloudClient().create(RongCloudApi.class);
        }
        return rongCloudApi;
    }

}
