package com.cetnaline.findproperty.widgets;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.cetnaline.findproperty.R;
import com.cetnaline.findproperty.utils.MyUtils;

/**
 * Created by diaoqf on 2017/1/3.
 */

public class SysToast{

    private Context mContext;
    private WindowManager windowManager;
    private int mDuration;
    private View mNextView;
    public static final int LENGTH_SHORT = 1500;
    public static final int LENGTH_LONG = 3000;

    public SysToast(Context context) {
        mContext = context;
        mDuration = LENGTH_SHORT;
        windowManager =  (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
    }

    public static SysToast makeText(Activity context, CharSequence text,
                                 int duration) {
        SysToast result = new SysToast(context);
        LayoutInflater inflate = (LayoutInflater) context
                .getApplicationContext().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
        View v = inflate.inflate(R.layout.transient_notification, null);
        TextView tv = (TextView) v.findViewById(android.R.id.message);
        tv.setText(text);
        result.mNextView = v;
        result.mDuration = duration;
        return result;
    }

    public static SysToast makeText(Activity context, int resId, int duration)
            throws Resources.NotFoundException {
        return makeText(context, context.getResources().getText(resId),duration);
    }

    public void show() {
        if (mNextView != null) {
            WindowManager.LayoutParams params = new WindowManager.LayoutParams();

            new Handler().postDelayed(() -> {
                if (mNextView != null) {
                    windowManager.removeView(mNextView);
                    mNextView = null;
                    windowManager = null;
                }
            }, mDuration);

            params.gravity = Gravity.CENTER | Gravity.CENTER_HORIZONTAL;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.width = WindowManager.LayoutParams.WRAP_CONTENT;
            params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
            params.format = PixelFormat.TRANSLUCENT;
//            params.windowAnimations = R.style.Animation_Toast;
            params.y = MyUtils.dip2px(mContext, 220);
            int type = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if(Build.VERSION.SDK_INT > 24){
                    type = WindowManager.LayoutParams.TYPE_PHONE;
                }else{
                    type = WindowManager.LayoutParams.TYPE_TOAST;
                }
            } else {
                type = WindowManager.LayoutParams.TYPE_PHONE;
            }
            params.type = type;
            try {
                windowManager.addView(mNextView, params);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
