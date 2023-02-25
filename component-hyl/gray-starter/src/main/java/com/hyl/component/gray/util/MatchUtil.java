package com.hyl.component.gray.util;


import cn.hutool.core.util.StrUtil;

/**
 * 2022-12-04 03:32
 * create by hyl
 * desc:
 */
public class MatchUtil {

    public static double getProportion(String proportion) {
        if (StrUtil.isEmpty(proportion)) {
            return 0;
        }
        double step = 0;
        try {
            if (proportion.endsWith("%")) {
                step = Double.parseDouble(proportion.replace("%", "")) / 100;
            } else {
                step = Double.parseDouble(proportion);
            }
        } catch (Exception e) {
            //配置错误，不进行转发
            step = 0;
        }
        return Math.min(Math.max(step, 0), 1);
    }

    /**
     * 判断str字符串是否能够被regex匹配
     * 如a*b?d可以匹配aAAAbcd
     *
     * @param str   任意字符串
     * @param regex 包含*或？的匹配表达式
     * @return
     */
    public static boolean isMatch(String str, String regex) {
        return isMatch(str, regex, false);
    }

    /**
     * 判断str字符串是否能够被regex匹配
     * 如a*b?d可以匹配aAAAbcd
     *
     * @param str        任意字符串
     * @param regex      包含*或？的匹配表达式
     * @param ignoreCase 大小写敏感
     * @return
     */
    public static boolean isMatch(String str, String regex, boolean ignoreCase) {
        if (str == null || regex == null) {
            return false;
        }
        if (ignoreCase) {
            str = str.toLowerCase();
            regex = regex.toLowerCase();
        }
        return matches(str, regex.replaceAll("(^|([^\\\\]))[\\*]{2,}", "$2*"));// 去除多余*号
    }

    private static boolean matches(String str, String regex) {
        // 如果str与regex完全相等，且str不包含反斜杠，则返回true。
        if (str.equals(regex) && str.indexOf('\\') < 0) {
            return true;
        }
        int rIdx = 0, sIdx = 0;// 同时遍历源字符串与匹配表达式
        while (rIdx < regex.length() && sIdx < str.length()) {
            char c = regex.charAt(rIdx);// 以匹配表达式为主导
            switch (c) {
                case '*':// 匹配到*号进入下一层递归
                    String tempSource = str.substring(sIdx);// 去除前面已经完全匹配的前缀
                    String tempRegex = regex.substring(rIdx + 1);// 从星号后一位开始认为是新的匹配表达式
                    for (int j = 0; j <= tempSource.length(); j++) {// 此处等号不能缺，如（ABCD，*），等号能达成("", *)条件
                        if (matches(tempSource.substring(j), tempRegex)) {// 很普通的递归思路
                            return true;
                        }
                    }
                    return false;// 排除所有潜在可能性，则返回false
                case '?':
                    break;
                case '\\':// 匹配到反斜杠跳过一位，匹配下一个字符串
                    c = regex.charAt(++rIdx);
                default:
                    if (str.charAt(sIdx) != c) {
                        return false;// 普通字符的匹配
                    }
            }
            rIdx++;
            sIdx++;
        }
        // 最终str被匹配完全，而regex也被匹配完整或只剩一个*号
        return str.length() == sIdx
                && (regex.length() == rIdx || regex.length() == rIdx + 1 && regex.charAt(rIdx) == '*');
    }

}
