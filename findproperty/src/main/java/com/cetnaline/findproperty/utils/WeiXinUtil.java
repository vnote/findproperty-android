package com.cetnaline.findproperty.utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class WeiXinUtil {

	private static final int THUMB_SIZE = 150;

	public static byte[] bmpToByteArray(final Bitmap bmp,
			final boolean needRecycle) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		Bitmap temp = createBitmapThumbnail(bmp);
		temp.compress(CompressFormat.JPEG, 100, output);
		ByteArrayInputStream isBm = new ByteArrayInputStream(
				output.toByteArray());
		// 尺寸压缩
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		int photoW = temp.getWidth();
		int photoH = temp.getHeight();
		int scaleFactor = 1;
		if (photoW >= photoH && photoW > THUMB_SIZE) {
			scaleFactor = photoW / THUMB_SIZE + 1;
		} else if (photoW <= photoH && photoH > THUMB_SIZE) {
			scaleFactor = photoH / THUMB_SIZE + 1;
		}
		if (scaleFactor <= 0)
			scaleFactor = 1;
		newOpts.inJustDecodeBounds = false;
		newOpts.inPreferredConfig = Config.RGB_565;
		newOpts.inSampleSize = scaleFactor;
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 30) {
			baos.reset();// 重置baos即清空baos
			bitmap.compress(CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 5;// 每次都减少10
		}
		byte[] result = output.toByteArray();
		bmp.recycle();
		temp.recycle();
		bitmap.recycle();
		try {
			output.close();
			baos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private static Bitmap createBitmapThumbnail(Bitmap bitMap) {
		int width = bitMap.getWidth();
		int height = bitMap.getHeight();
		// 设置想要的大小
		int newWidth = 99;
		int newHeight = 99;
		// 计算缩放比例
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 取得想要缩放的matrix参数
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		// 得到新的图片
		Bitmap newBitMap = Bitmap.createBitmap(bitMap, 0, 0, width, height,
				matrix, true);
		return newBitMap;
	}

}
