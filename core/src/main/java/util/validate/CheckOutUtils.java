package util.validate;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.value.UtilMagic;

/**
 * Created on 2017/9/15.
 *
 * @author 郑少鹏
 * @desc CheckOutUtils
 */
public class CheckOutUtils {
    /**
     * Pattern p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}"); 简匹
     * Pattern p = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*"); 复匹
     */
    private static final Pattern P0 = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
    private static final Pattern P1 = Pattern.compile("^([\u4E00-\u9FA5]|[\uF900-\uFA2D]|[\u258C]|[\u2022]|[\u2E80-\uFE4F])+$");
    private static final Pattern P2 = Pattern.compile("^[A-Za-z0-9][\\w._]*[a-zA-Z0-9]+@[A-Za-z0-9-_]+\\.([A-Za-z]{2,4})");
    private static final Pattern P3 = Pattern.compile("[0-9]*");
    private static final Pattern P5 = Pattern.compile(".*[\\u4e00-\\u9fa5]+.*");
    private static final Pattern P6 = Pattern.compile("^[A-Za-z]+$");
    private static final Pattern P7 = Pattern.compile("^[\u4E00-\u9FFF]+$");
    private static final Pattern P8 = Pattern.compile("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z.*]{6,20}$");
    private static final Pattern P9 = Pattern.compile("[A-Za-z0-9]{6,12}");
    private static final Pattern P10 = Pattern.compile(".{6,20}");
    private static final Pattern P11 = Pattern.compile(".{1,250}");
    private static final Pattern P12 = Pattern.compile("^(?![^a-zA-Z]+$)(?!\\\\D+$).{4,16}$");
    private static final Pattern P13 = Pattern.compile(".{2,25}");
    private static final Pattern P14 = Pattern.compile("[a-zA-Z][a-zA-Z0-9]+");
    private static final Pattern P15 = Pattern.compile(".{13,19}");
    private static final Pattern P16 = Pattern.compile("^((\\d{2}(([02468][048])|([13579][26]))[\\-/\\s]?((((0?[13578])|(1[02]))[\\-/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-/\\s]?((((0?[13578])|(1[02]))[\\-/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3])):([0-5]?[0-9])((\\s)|(:([0-5]?[0-9])))))?$");
    private static final Pattern P17 = Pattern.compile(".*[a-zA-Z]+.*");
    private static final Pattern P18 = Pattern.compile(".*[`~!@#$^&*()=|{}':;,\\[\\].<>/?！￥…（）—【】‘；：”“。，、？]$+.*");

    /**
     * 用户名 1 - 50 即 2 - 25 汉字
     *
     * @param userName 用户名
     * @return 2 - 25 汉字
     */
    public static boolean areUserNameYws(String userName) {
        Matcher matcher = P1.matcher(userName);
        return matcher.matches();
    }

    /**
     * 邮箱
     *
     * @param mail 邮箱
     * @return 邮箱否
     */
    public static boolean areValidEmail(String mail) {
        Matcher matcher = P2.matcher(mail);
        return matcher.matches();
    }

    /**
     * 输数字否
     */
    public static boolean areNumeric(String str) {
        return P3.matcher(str).matches();
    }

    /**
     * 联系方式
     *
     * @param str 座或手机
     * @return 联系方式
     */
    public static boolean areMobileOrPhone(String str) {
        String regex = "^((([0\\+]\\d{2,3}-)|(0\\d{2,3})-))(\\d{7,8})(-(\\d{3,}))?$|^1[0-9]{10}$";
        return match(regex, str);
    }

    /**
     * 金额有效性
     *
     * @param price 金额
     * @return 金额有效性
     */
    public static boolean arePrice(String price) {
        String regex = "^([1-9][0-9]{0,7})(\\.\\d{1,2})?$";
        return match(regex, price);
    }

    /**
     * 含中否
     */
    public static boolean areContainChinese(String str) {
        Matcher matcher = P5.matcher(str);
        return matcher.find();
    }

    /**
     * 纯英否
     */
    public static boolean areLetter(String str) {
        Matcher matcher = P6.matcher(str);
        return matcher.matches();
    }

    /**
     * 纯中否
     */
    public static boolean areChinese(String str) {
        Matcher matcher = P7.matcher(str);
        return matcher.matches();
    }

    /**
     * 密码类型否
     */
    public static boolean arePassword(String str) {
        Matcher matcher = P8.matcher(str);
        return matcher.matches();
    }

    /**
     * 警员号否
     */
    public static boolean arePoliceNumberAndLength(String str) {
        Matcher matcher = P9.matcher(str);
        return matcher.matches();
    }

    /**
     * 邮件 email 格式正确否
     */
    public static boolean areEmail(String email) {
        if ((null == email) || email.isEmpty()) {
            return false;
        }
        Matcher matcher = P0.matcher(email);
        return matcher.matches();
    }

    /**
     * 字符长 6 到 20 位
     *
     * @param str 字符
     * @return 6 到 20 位
     */
    public static boolean areLength(String str) {
        return P10.matcher(str).matches();
    }

    /**
     * 住址字符长 1 到 250 位
     *
     * @param str 住址
     * @return 1 到 250 位
     */
    public static boolean areAddressLength(String str) {
        return P11.matcher(str).matches();
    }

    /**
     * 用户名长 4 到 16 位
     *
     * @param str 用户名
     * @return 4 到 16 位
     */
    public static boolean areUserNameSsl(String str) {
        return P12.matcher(str).matches();
    }

    /**
     * 用户真名长
     *
     * @param str 用户真名
     * @return 用户真名长
     */
    public static boolean areNameLength(String str) {
        return P13.matcher(str).matches();
    }

    /**
     * 输字符首字母英否
     *
     * @param str 输字符
     * @return 输字符首字母英否
     */
    public static boolean areEnglish(String str) {
        return P14.matcher(str).matches();
    }

    /**
     * 银行卡 13 到 19 位
     *
     * @param str 银行卡号
     * @return 13 到 19 位
     */
    public static boolean areBank(String str) {
        return P15.matcher(str).matches();
    }

    /**
     * 中占两字符（英占一）
     */
    public static int stringLength(@NotNull String value) {
        int valueLength = 0;
        String chinese = "[\u4e00-\u9fa5]";
        for (int i = 0; i < value.length(); i++) {
            String temp = value.substring(i, i + 1);
            if (temp.matches(chinese)) {
                valueLength += 2;
            } else {
                valueLength += 1;
            }
        }
        return valueLength;
    }

    /**
     * 过滤特殊字符
     */
    public static @NotNull String stringFilter(String str) {
        String regEx = "[`~!@#$%^&*()+=|{}':;,\\[\\].<>/?！￥…（）—【】‘；：”“’。，、？]";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(str);
        return matcher.replaceAll("");
    }

    /**
     * 密码长（6 - 20 位除空格回车 tab 外字符）
     */
    public static boolean arePassLenLes(String str) {
        String regex = "^\\INT_S{6,20}$";
        return match(regex, str);
    }

    /**
     * 密码长（6 到 16 位数字字母组合）
     */
    public static boolean arePassLenLsl(String str) {
        String regex = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$";
        return match(regex, str);
    }

    /**
     * 匹配
     *
     * @param regex 正则表达式字符串
     * @param str   所匹字符串
     * @return str 符 regex 正则表达式格式返 true，否返 false
     */
    private static boolean match(String regex, String str) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * 字符串日期格式否
     *
     * @return 字符串日期格式否
     */
    public static boolean areDate(String strDate) {
        Matcher matcher = P16.matcher(strDate);
        return matcher.matches();
    }

    /**
     * 含英文否
     *
     * @param str 所匹字符串
     * @return 含英文否
     */
    public static boolean areContainLetter(String str) {
        Matcher matcher = P17.matcher(str);
        return matcher.matches();
    }

    /**
     * 身份证号合规否
     * <p>
     * "(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x|Y|y)$)"改"(^\\d{18}$)|(^\\d{17}(\\d|X|x|Y|y)$)"
     * 15 居民身份证编码
     * 18 公民身份证号码
     *
     * @param identityCode 身份证号
     * @return 合规否
     */
    public static boolean areIdentityCard(String identityCode) {
        if (null != identityCode) {
            String identityCardRegex = "(^\\d{18}$)|(^\\d{17}(\\d|X|x|Y|y)$)";
            return identityCode.matches(identityCardRegex);
        }
        return false;
    }

    /**
     * 据身份证号获生日
     *
     * @return 生日
     */
    public static String getUserBirthdayByCardId(@NotNull String ids) {
        String birthday = "";
        if (ids.length() == UtilMagic.INT_EIGHTEEN) {
            // 18 位
            birthday = ids.substring(6, 14);
            String years = birthday.substring(0, 4);
            String moths = birthday.substring(4, 6);
            String days = birthday.substring(6, 8);
            birthday = (years + "-" + moths + "-" + days);
        } else if (ids.length() == UtilMagic.INT_FIFTEEN) {
            // 15 位
            birthday = ids.substring(6, 12);
            String years = birthday.substring(0, 2);
            String moths = birthday.substring(2, 4);
            String days = birthday.substring(4, 6);
            birthday = ("19" + years + "-" + moths + "-" + days);
        }
        return birthday;
    }

    /**
     * 据身份证号获性别
     *
     * @return 性别
     */
    public static String getUserSexByCardId(@NotNull String ids) {
        String sexShow = "";
        if (ids.length() == UtilMagic.INT_EIGHTEEN) {
            // 身份证倒数第二位
            String sexString = ids.trim().substring(ids.length() - 2, ids.length() - 1);
            // 转数字
            int sexNum = Integer.parseInt(sexString);
            if ((sexNum % UtilMagic.INT_TWO) != 0) {
                sexShow = "男";
            } else {
                sexShow = "女";
            }
        } else if (ids.length() == UtilMagic.INT_FIFTEEN) {
            // 身份证最后一位
            String sexString = ids.trim().substring(ids.length() - 1, ids.length());
            // 转数字
            int sexNum = Integer.parseInt(sexString);
            if ((sexNum % UtilMagic.INT_TWO) != 0) {
                sexShow = "男";
            } else {
                sexShow = "女";
            }
        }
        return sexShow;
    }

    /**
     * 中文标点符号否
     *
     * @param str 所匹字符串
     * @return 中文标点符号否
     */
    private boolean areUnicodeSymbol(String str) {
        Matcher matcher = P18.matcher(str);
        return matcher.matches();
    }
}