package com.cetnaline.findproperty.utils;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import java.io.File;

/**
 * Created by hewei26 on 2017/3/27.
 */

public class UriTools {

    /**
     * 转换 content:// uri
     *
     * @param imageFile
     * @return
     */
    @TargetApi(24)
    public static Uri getContentUri(Context context,File imageFile) {

        if(Build.VERSION.SDK_INT >= 24){
            String filePath = imageFile.getAbsolutePath();
            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[] { MediaStore.Images.Media._ID },
                    MediaStore.Images.Media.DATA + "=? ",
                    new String[] { filePath }, null);

            if (cursor != null && cursor.moveToFirst()) {
                int id = cursor.getInt(cursor
                        .getColumnIndex(MediaStore.MediaColumns._ID));
                Uri baseUri = Uri.parse("content://media/external/images/media");
                return Uri.withAppendedPath(baseUri, "" + id);
            } else {
                if (imageFile.exists()) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.DATA, filePath);
                    return context.getContentResolver().insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                } else {
                    return null;
                }
            }
        }else{
            return Uri.fromFile(imageFile);
        }

    }
}
