package com.cetnaline.findproperty.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.ContextCompat;

import java.io.File;

/**
 * Created by diaoqf on 2016/10/13.
 */

public class PermissionUtils {
    /**
     * 判断摄像头是否可用
     * 主要针对6.0 之前的版本，现在主要是依靠try...catch... 报错信息，感觉不太好，
     * 以后有更好的方法的话可适当替换
     *
     * @return
     */
    public static boolean isCameraCanUse(Context context) {
        boolean canUse = true;
        Camera mCamera = null;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            canUse = (PackageManager.PERMISSION_GRANTED ==
                    ContextCompat.checkSelfPermission(context, "android.permission.CAMERA"));
        } else {
            try {
                mCamera = Camera.open();
                Camera.Parameters mParameters = mCamera.getParameters();
                mCamera.setParameters(mParameters);
            } catch (Exception e) {
                canUse = false;
            }
            if (mCamera != null) {
                mCamera.release();
            }
        }
        return canUse;
    }

    /**
     * 判断文件读写的权限是否可用
     */
    public static boolean isFileOperateCanUse(){
        File directory = Environment.getExternalStorageDirectory();
        boolean canUse = directory.canWrite();
        return canUse;
    }
}
