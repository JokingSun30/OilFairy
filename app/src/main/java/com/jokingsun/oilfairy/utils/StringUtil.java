package com.jokingsun.oilfairy.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Base64;
import android.util.Patterns;

import androidx.core.content.ContextCompat;

import com.jokingsun.oilfairy.common.constant.AppConstant;

import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.util.Arrays;

/**
 * @author cfd058
 */
public class StringUtil {

    private static final String MATH_THOUSANDS_SYMBOL = ",";

    /**
     * 取得 url 副檔名
     */
    public static String getFileExtensionWithUrl(String url) {
        String strFileExtension = "";

        if (!url.isEmpty()) {
            String[] splitString = url.split("\\.");
            if (splitString.length > 0) {
                strFileExtension = splitString[splitString.length - 1];
            }
        }

        return strFileExtension;
    }

    /**
     * 判斷身分證格式是否正確 ?
     */
    public static boolean checkSecurityNumberFormat(String securityNumber) {
        if (!securityNumber.matches(AppConstant.SECURITY_NUMBER_FORMAT_TW)) {
            return false;
        }

        String newSecurityNumber = securityNumber.toUpperCase();

        //身分證第一碼代表數值
        int[] headNum = new int[]{
                1, 10, 19, 28, 37,
                46, 55, 64, 39, 73,
                82, 2, 11, 20, 48,
                29, 38, 47, 56, 65,
                74, 83, 21, 3, 12, 30};

        char[] headCharUpper = new char[]{
                'A', 'B', 'C', 'D', 'E', 'F', 'G',
                'H', 'I', 'J', 'K', 'L', 'M', 'N',
                'O', 'P', 'Q', 'R', 'S', 'T', 'U',
                'V', 'W', 'X', 'Y', 'Z'
        };

        int index = Arrays.binarySearch(headCharUpper, newSecurityNumber.charAt(0));
        int base = 8;
        int total = 0;
        for (int i = 1; i < 10; i++) {
            int tmp = Integer.parseInt(Character.toString(newSecurityNumber.charAt(i))) * base;
            total += tmp;
            base--;
        }

        total += headNum[index];
        int remain = total % 10;
        int checkNum = (10 - remain) % 10;

        return Integer.parseInt(Character.toString(newSecurityNumber.charAt(9))) == checkNum;
    }

    /**
     * 判斷身分證格式是否正確 ?
     */
    public static boolean checkSecurityNumberWithGender(String securityNumber, String gender) {
        if (!securityNumber.matches(AppConstant.SECURITY_NUMBER_FORMAT_TW)) {
            return false;
        }

        String newSecurityNumber = securityNumber.toUpperCase();

        //身分證第一碼代表數值
        int[] headNum = new int[]{
                1, 10, 19, 28, 37,
                46, 55, 64, 39, 73,
                82, 2, 11, 20, 48,
                29, 38, 47, 56, 65,
                74, 83, 21, 3, 12, 30};

        char[] headCharUpper = new char[]{
                'A', 'B', 'C', 'D', 'E', 'F', 'G',
                'H', 'I', 'J', 'K', 'L', 'M', 'N',
                'O', 'P', 'Q', 'R', 'S', 'T', 'U',
                'V', 'W', 'X', 'Y', 'Z'
        };

        int index = Arrays.binarySearch(headCharUpper, newSecurityNumber.charAt(0));
        int base = 8;
        int total = 0;
        for (int i = 1; i < 10; i++) {
            int tmp = Integer.parseInt(Character.toString(newSecurityNumber.charAt(i))) * base;
            total += tmp;
            base--;
        }

        total += headNum[index];
        int remain = total % 10;
        int checkNum = (10 - remain) % 10;

        if (Integer.parseInt(Character.toString(newSecurityNumber.charAt(9))) != checkNum) {
            return false;
        }

        return String.valueOf(newSecurityNumber.charAt(1)).equals(gender);
    }

    public static boolean checkPhoneNumber(String phoneNumber) {

        if (phoneNumber.length() == AppConstant.GENERAL_PHONE_RULE_TW_LENGTH
                && phoneNumber.startsWith("09")) {
            return true;
        }

        return (phoneNumber.length() == AppConstant.GENERAL_PHONE_RULE_TW_LENGTH - 1)
                && phoneNumber.startsWith("9");
    }

    /**
     * 判斷 email 是否符合格式
     */
    public static boolean checkEmailFormat(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }

        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * 將目前通話秒數轉為 00:00 顯示
     *
     * @param recentSeconds
     * @return
     */
    @SuppressLint("DefaultLocale")
    public static String formatChatTimer(int recentSeconds) {

        int[] time = DateUtil.parseSecondsToCompleteStyle(recentSeconds);

        StringBuilder timeBuilder = new StringBuilder();

        if (time[0] != 0) {
            timeBuilder.append(String.format("%02d", time[0])).append("：");
        }

        timeBuilder.append(String.format("%02d", time[1])).append("：").append(String.format("%02d", time[2]));

        return timeBuilder.toString();
    }

    private static final int HOUR_SECOND = 60 * 60;

    private static final int MINUTE_SECOND = 60;

    /**
     * 將目前通話秒數轉為 00:00 顯示 ,超過一小時則顯示 00:00:00
     *
     * @param seconds
     * @return
     */
    public static String getTimeStrBySecond(int seconds) {
        if (seconds <= 0) {
            return "00:00:00";
        }

        int hours = seconds / HOUR_SECOND;

        if (hours > 0) {
            seconds -= hours * HOUR_SECOND;
        }

        int minutes = seconds / MINUTE_SECOND;

        if (minutes > 0) {
            seconds -= minutes * MINUTE_SECOND;
        }

        String formatMinute = minutes >= 10 ? (minutes + "") : ("0" + minutes);

        String formatSecond = seconds >= 10 ? (seconds + "") : ("0" + seconds);

        return (hours == 0) ? formatMinute + "：" + formatSecond
                : (hours >= 10) ? (hours + "")
                : ("0" + hours) + ":" + formatMinute + "："
                + formatSecond;
    }

    public static String adjustPriceFormatThousands(String price) {
        StringBuilder formatPrice = new StringBuilder();

        if (price.contains(MATH_THOUSANDS_SYMBOL)) {
            String[] tempPrice = price.split(MATH_THOUSANDS_SYMBOL);

            for (String str : tempPrice) {
                formatPrice.append(str);
            }

            return formatPrice.toString();
        }

        return price;
    }

    public static String formatToThousandSeparator(int number) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        return decimalFormat.format(number);
    }

    /***
     * 格式化指定字串
     * @param context   文本
     * @param targetStr 目標字串
     * @param typeFace  字體風格
     * @param color     字體顏色
     * @return
     */
    public static SpannableString formatSpbString(Context context, String targetStr, int typeFace, int color) {

        SpannableString spb = new SpannableString(targetStr);

        //粗體
        spb.setSpan(new StyleSpan(typeFace), 0, targetStr.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        //顏色
        spb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, color)),
                0, targetStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spb;
    }

    /**
     * 字串 Base 64 加密
     *
     * @param targetStr 目標字串
     * @return
     */
    public static String encodeTextByBase64(String targetStr) {

        try {
            return Base64.encodeToString(targetStr.getBytes("UTF-8"), Base64.DEFAULT);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * 字串 Base 64 解密
     *
     * @param codeStr 目標解密字串
     * @return
     */
    public static String decodeTextByBase64(String codeStr) {

        try {
            return new String(Base64.decode(codeStr.getBytes("UTF-8"), Base64.DEFAULT));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * 簡易化比較大的數字 1300 --> 1k
     */
    public static String formatSimpleNumber(long number) {

        if (number >= Math.pow(10, 12)) {
            //1T 10的十二次方 -->一兆
            return (int) Math.floor(number / Math.pow(10, 12)) + "T";

        } else if (number >= Math.pow(10, 9)) {
            //1B 10的九次方 -->十億
            return (int) Math.floor(number / Math.pow(10, 9)) + "B";

        } else if (number >= Math.pow(10, 6)) {
            //1M 10的六次方 -->一百萬
            return (int) Math.floor(number / Math.pow(10, 6)) + "M";

        } else if (number >= Math.pow(10, 3)) {
            //1K 10的三次方 -->一千
            return (int) Math.floor(number / Math.pow(10, 3)) + "K";

        } else {
            return String.valueOf(number);
        }
    }

    public static boolean isNotBlankAndEmpty(String str) {
        return StringUtils.isNotBlank(str) && StringUtils.isNotEmpty(str);
    }

    /**
     * 超出此上限，都用 + 表示  如: 99+
     */
    public static String plusCountToLimit(String srcCount) {

        if (srcCount == null) {
            return "0";
        }

        int count = Integer.parseInt(srcCount);

        if (count < 0) {
            count = 0;
        }

        if (count > 99) {
            return "99+";
        }

        return String.valueOf(count);
    }

    /**
     * 輸出限時時間字串
     *
     * @return
     */
    public static String outputLimitTimeStr(String days, String hours) {
        try {
            String str = "倒數 ";

            int day = Integer.parseInt(days);
            int hour = Integer.parseInt(hours);

            if (day == 0) {
                //一天以內
                return hour == 0 ? "即將到期" : str + hour + " 小時";

            } else {
                return str + day + " 天 " + hour + " 小時";
            }

        } catch (Exception e) {
            return "";
        }
    }

    public static boolean isNumberStringType(String targetStr) {
        if (targetStr == null || targetStr.isEmpty()) {
            return false;
        }

        try {
            Integer.parseInt(targetStr);

        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public static boolean isNullSafeString(String targetStr) {
        return targetStr != null && !targetStr.isEmpty();
    }

    public static float analyticsOilPrice(String srcPrice) {
        if (!isNullSafeString(srcPrice)) {
            return 0.0f;
        }

        if (TextUtils.equals(srcPrice, "不調整")) {
            return 0.0f;
        }

        try {
            float result = Float.parseFloat(srcPrice.substring(1, srcPrice.length() - 1));

            if (srcPrice.startsWith("升") || srcPrice.startsWith("漲")) {
                return result * 1;
            }

            if (srcPrice.startsWith("降") || srcPrice.startsWith("跌")) {
                return result * -1;
            }

        } catch (Exception e) {
            return 0.0f;
        }

        return 0.0f;
    }

}
