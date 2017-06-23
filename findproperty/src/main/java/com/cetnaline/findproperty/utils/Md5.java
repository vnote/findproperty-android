package com.cetnaline.findproperty.utils;

import com.orhanobut.logger.Logger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by diaoqf on 2016/8/10.
 */
public class Md5 {
    final static char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String encode(String... strings) {
        final StringBuilder input = new StringBuilder();
        for (String s : strings) {
            input.append(s);
        }
        Logger.d("md5 : %s", input.toString());
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(input.toString().getBytes());
            byte[] bytes = messageDigest.digest();
            int len = bytes.length;
            char[] resultCharArray = new char[len * 2];
            int index = 0;
            for (byte b : bytes) {
                resultCharArray[index++] = HEX_DIGITS[b >>> 4 & 0xf];
                resultCharArray[index++] = HEX_DIGITS[b & 0xf];
            }
            return new String(resultCharArray);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

}
