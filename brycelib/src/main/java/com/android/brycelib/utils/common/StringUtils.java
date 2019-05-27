package com.android.brycelib.utils.common;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Deprecated
public class StringUtils {

    /**
     * @param list
     * @param splite 以什么字符分割
     * @return
     * @说明 将list数组地址转化为urls地址字符串
     */
    public final static String converListToString(List<String> list, String splite) {
        String urlLink = "";
        if (list == null || list.size() == 0) return urlLink;
        for (String url : list) {
            urlLink += url + splite;
        }
        urlLink = urlLink.substring(0, urlLink.length() - 1);
        return urlLink;
    }

    /**
     * @param list
     * @param splite 以什么字符分割
     * @return
     * @说明 将list数组地址转化为urls地址字符串
     */
    public final static String converIntListToString(List<Integer> list, String splite) {
        String urlLink = "";
        if (list == null || list.size() == 0) return urlLink;
        for (int url : list) {
            urlLink += url + splite;
        }
        urlLink = urlLink.substring(0, urlLink.length() - 1);
        return urlLink;
    }

    /**
     * @param
     * @param splitExpressione
     * @return
     * @说明 将str数组字符串转为list
     */
    public final static List<String> converStringToList(String strStr,

                                                        String splitExpressione) {
        List<String> list = new ArrayList<String>();
        if (TextUtils.isEmpty(strStr)) {
            return list;
        }
        String[] array = strStr.split(splitExpressione);
        for (String str : array) {
            list.add(str);
        }
        return list;
    }

    /**
     * unicode 转字符串
     */
    public static String unicode2String(String unicode) {

        StringBuffer string = new StringBuffer();

        String[] hex = unicode.split("\\\\u");

        for (int i = 1; i < hex.length; i++) {

            // 转换出每一个代码点
            int data = Integer.parseInt(hex[i], 16);

            // 追加成string
            string.append((char) data);
        }

        return string.toString();
    }

    public static boolean isNumber(String input) {
        Pattern p = Pattern.compile("[0-9]*");
        Matcher m = p.matcher(input);
        return m.matches();
    }

    public static boolean isLetter(String input) {
        Pattern p = Pattern.compile("[a-zA-Z]");
        Matcher m = p.matcher(input);
        return m.matches();
    }
    public static boolean isPunctuation(String input) {
        Pattern p = Pattern.compile("[，。.]");
        Matcher m = p.matcher(input);
        return m.matches();
    }

    public static boolean isChinese(String input) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(input);
        return m.matches();
    }
    public static boolean isSameChars (String str) throws IllegalArgumentException {
        if (str == null)
            throw new IllegalArgumentException("Input string should not be null.");
        else if (str.length() < 2)
            return true;
        char first = str.charAt(0);
        for (int i=1; i<str.length(); i++)
            if (str.charAt(i) != first)
                return false;
        return true;
    }


    /**
     * 判断字符串中是否包含中文
     * @param str
     * 待校验字符串
     * @return 是否为中文
     * @warn 不能校验是否为中文标点符号
     */
    public static boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }


    /*方法二：推荐，速度最快
     * 判断是否为整数
     * @param str 传入的字符串
     * @return 是整数返回true,否则返回false
     */

    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }
}
