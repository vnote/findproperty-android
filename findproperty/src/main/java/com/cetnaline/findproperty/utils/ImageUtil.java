package com.cetnaline.findproperty.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.orhanobut.logger.Logger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Locale;

/**
 * 图片工具{压缩}
 */
public class ImageUtil {

    private final static String TAG = "TAG";

    private final static float ww = 720;//最大目标宽度
    private final static float hh = 1280;//最大目标高度

    private ImageUtil() {
        // Utility class.
    }

    /**
     * 获取指定文件大小
     * @param url
     * @return
     */
    public static long getFileSize(String url){
        File file = new File(url);
        long size = 0;
        if (file.isFile() && file.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                size = fis.available();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return size;
    }

    /**
     * 压缩
     *
     * @param dir        压缩保存目录
     * @param path       图片路径
     * @param targetSize 目标大小
     */
    public static String compress(String dir, String path, int targetSize) {
        if (targetSize == 0)//不压缩，直接返回原图路径
            return path;

        File original = new File(path);
        final long originalSize = original.length();
        if (originalSize < targetSize)//原图大小比压缩目标小直接返回
            return path;
        String ext = path.substring(path.lastIndexOf("."));
        File target = new File(dir, String.format(Locale.CHINA, "%s-%s", Md5.encode(path), targetSize)+ext);
        if (target.exists())//已经压缩处理过的图片直接返回路径
            return target.getPath();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;//只拿图片尺寸，不加载到内存
        BitmapFactory.decodeFile(path, options);
        int w = options.outWidth;//原图宽
        int h = options.outHeight;//原图高
        Logger.t(TAG).d("original width = %d - height = %d", w, h);
        if (w < 0 || h < 0) {
            return null;
        }

        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inJustDecodeBounds = false;//需要加载图片信息
        options.inSampleSize = (int) new BigDecimal((w / ww + h / hh + 2) / 2).setScale(0, BigDecimal.ROUND_HALF_UP).floatValue();//缩放比，1：不缩放；2：1/2；类推
        Logger.t(TAG).d("options.inSampleSize = %d", options.inSampleSize);

        Bitmap scaleBitmap = BitmapFactory.decodeFile(path, options);
        final int tSize = targetSize * 1024;
        int quality = 100;
        ByteArrayOutputStream baos = null;
        FileOutputStream fileOutputStream = null;
        try {
            baos = new ByteArrayOutputStream();
            scaleBitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            while (baos.toByteArray().length > tSize) {
                quality -= 5;//每次减少5
                baos.reset();
                scaleBitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            }
            Logger.t(TAG).d("quality : %d", quality);
            fileOutputStream = new FileOutputStream(target);
            baos.writeTo(fileOutputStream);
            baos.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (baos != null)
                    baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fileOutputStream != null)
                    fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Logger.t(TAG).d("original size : %d;compress size : %d", originalSize, target.length());
        return target.getPath();
    }

}
