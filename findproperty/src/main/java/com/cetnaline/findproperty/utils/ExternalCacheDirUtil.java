package com.cetnaline.findproperty.utils;

import android.content.Context;
import android.os.Environment;

import com.orhanobut.logger.Logger;

import java.io.File;

/**
 * ExternalCacheDirUtil SD卡目录工具
 * <p>
 */
public final class ExternalCacheDirUtil {

    private final static String TAG = "ExternalCacheDirUtil";

    private ExternalCacheDirUtil() {
        // Utility class.
    }

    /**
     * 图片下载目录[拍照目录]
     */
    public static String getImageDownloadCacheDir() {
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Logger.t(TAG).e("Unable to create external cache directory");
            }
        }
        return dir.getAbsolutePath();
    }

    /**
     * 图片压缩目录[应用文件夹]
     */
    public static String getImageCompressCacheDir(Context context) {
        File dir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            dir = new File(context.getExternalCacheDir(), "compress");
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    Logger.t(TAG).e("Unable to create external cache directory");
                }
            }
        } else {
            dir = new File(context.getCacheDir(), "compress");
            if (!dir.exists())
                dir.mkdirs();
        }

        return dir.getAbsolutePath();
    }

    public static String getAdvertCacheDir(Context context) {
//        File dir = new File(context.getExternalCacheDir(), "advert");
        File dir = new File(context.getFilesDir(), "advert");
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Logger.t(TAG).e("Unable to create external cache directory");
            }
        }
        return dir.getAbsolutePath();
    }

    /**
     * apk保存目录[downloads文件夹]
     */
    public static String getApkCacheDir() {
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Logger.t(TAG).e("Unable to create external cache directory");
            }
        }
        return dir.getAbsolutePath();
    }
}
