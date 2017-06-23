package com.cetnaline.findproperty.utils.glide;

import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.orhanobut.logger.Logger;

/**
 * Glide加载错误日志监听
 */
public class LogListener<T, R> implements RequestListener<T, R> {

    private static final String TAG = "GlideError";

    @Override
    public boolean onException(Exception e, T model, Target<R> target, boolean isFirstResource) {
        Logger.t(TAG).e(e, "onException(model:%s,\ttarget:%s,\tisFirstResource:%b)", model, target, isFirstResource);
        return false;
    }

    @Override
    public boolean onResourceReady(R resource, T model, Target<R> target, boolean isFromMemoryCache, boolean isFirstResource) {
        return false;
    }
}
