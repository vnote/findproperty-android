package com.cetnaline.findproperty.api;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Created by wukewei on 16/6/1.
 */
public class ErrorHanding {

    public ErrorHanding() {
    }

    public static String handleError(Throwable throwable) {
        throwable.printStackTrace();
        String message;
        if (throwable instanceof ServerException) {
            message = throwable.getMessage();
        } else if(throwable instanceof SocketTimeoutException){
            message = "数据获取超时, 请稍后尝试!";
        }else if (throwable instanceof UnknownHostException){
            message = "网络连接失败，请检查网络!";
        }else if (throwable instanceof ConnectException){
            message = "网络连接异常，请检查网络连接";
        }
//        else if (throwable instanceof NullPointerException) {
//            message = "数据为空";
//        }
        else {
            message = "连接服务器失败";
        }
        return message;
    }
}
