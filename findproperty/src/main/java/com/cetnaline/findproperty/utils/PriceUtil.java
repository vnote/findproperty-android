package com.cetnaline.findproperty.utils;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.util.Locale;

/**
 * 价格
 * Created by guilin on 16/1/11.
 */
public final class PriceUtil {

    private PriceUtil() {
        //Utility Class
    }

    /**
     * 小区二手房均价
     *
     * @param price 价格
     * @return 价格
     */
    public static String esfSaleAvgPrice(double price) {
        return String.format(Locale.CHINA, "%.0f元/平", price);
    }

    /**
     * 二手房总价
     *
     * @param price 价格
     */
    public static String salePrice(double price) {
        if (price == 0) {
            return "";
        } else if (price > 100000000) {
            return String.format(Locale.CHINA, "%.1f亿", price / 100000000);
        } else {
            return String.format(Locale.CHINA, "%.0f万", price / 10000);
        }
    }

    /**
     * 租房价格
     *
     * @param price 价格
     * @return String
     */
    public static String rentPrice(double price) {
        return String.format(Locale.CHINA, "%.0f元/月", price);
    }

    /**
     * 物业费
     *
     * @return 物业费
     */
    public static String mgtPrice(Object price) {
        if (price == null ||
                "0".equals(String.valueOf(price)) ||
                TextUtils.isEmpty(price.toString())) {
            return "暂无";
        }
        return String.format(Locale.CHINA, "%s元/平/月",
                BigDecimal.valueOf(Double.parseDouble(String.valueOf(price))).stripTrailingZeros().toPlainString());
    }

    /**
     * 小区成交历史-总价
     */
    public static String dealPrice(String price) {
        if (TextUtils.isEmpty(price)) {
            return "暂无";
        }
        double dPrice = Double.parseDouble(price);
        if (dPrice > 10000) {
            return String.format(Locale.CHINA, "%.0f万元", dPrice / 10000);
        } else {
            return String.format(Locale.CHINA, "%.0f元", dPrice);
        }
    }

    /**
     * 新盘均价
     */
    public static String newEstAvgPrice(double price) {
        if (price == 0) {
            return "暂无";
        }
        return String.format(Locale.CHINA, "%.0f元/平", price);
    }

}