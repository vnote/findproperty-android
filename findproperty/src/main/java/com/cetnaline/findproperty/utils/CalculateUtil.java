package com.cetnaline.findproperty.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 计算器
 */
public final class CalculateUtil {

    private static final int YEARS = 30;

    private CalculateUtil() {
        //Utility Class
    }

    /**
     * 获取按揭年数
     */
    public static List<String> getYears() {
        final List<String> result = new ArrayList<>();
        for (int i = 1; i <= YEARS; i++) {
            result.add(String.format(Locale.CHINA, "%d年(%d期)", i, i * 12));
        }
        return result;
    }

    /**
     * 等额本息还款
     */
    public static double countBenXi(double cash, double rate, int month) {
        double trate, P;
        rate = rate / 12;
        trate = rate + (double) 1;
        for (int i = 0; i < month - 1; i++) {
            trate *= (rate + (double) 1);
        }
        P = (cash * trate * rate) / (trate - (double) 1);
        return P;
    }

    /**
     * 等额本金还款
     */
    public static double countBenJin(double cash, double rate, int month) {
        double P = 0;
        for (int i = 1; i <= month; i++) {
            P += countBenJin4Month(cash, rate, month, i);
        }
        return P;
    }

    /**
     * 等额本金还款每月还款额
     */
    public static double countBenJin4Month(double cash, double rate,
                                           int totalMonth, int currentMonth) {
        double P;
        double trate = cash / totalMonth;
        P = trate + (cash - trate * (currentMonth - 1)) * (rate / 12);
        return P;
    }
}
