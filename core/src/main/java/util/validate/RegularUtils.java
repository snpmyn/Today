package util.validate;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created on 2018/4/11.
 *
 * @author 郑少鹏
 * @desc RegularUtils
 */
public class RegularUtils {
    /**
     * all numbers
     * <p>
     * Phone number + IoT number + Data only number
     */
    private static final String REGEX_ALL_MOBILE = "^(?:\\+?86)?1(?:3\\d{3}|5[^4\\D]\\d{2}|8\\d{3}|7(?:[01356789]\\d{2}|4(?:0\\d|1[0-2]|9\\d))|9[189]\\d{2}|6[567]\\d{2}|4(?:[14]0\\d{3}|[68]\\d{4}|[579]\\d{2}))\\d{6}$";
    /**
     * 手机号（精确）
     * <p>
     * 中国电信号段 133、149、153、173、177、180、181、189、199
     * 中国联通号段 130、131、132、145、155、156、166、175、176、185、186
     * 中国移动号段 134(0-8)、135、136、137、138、139、147、150、151、152、157、158、159、178、182、183、184、187、188、198
     * <p>
     * 其它号段
     * 14 号段前为上网卡专属号段，如中国联通 145、中国移动 147 等。
     * <p>
     * 虚拟运营商
     * 电信：1700、1701、1702
     * 移动：1703、1705、1706
     * 联通：1704、1707、1708、1709、171
     * <p>
     * 卫星通信：1349
     */
    private static final String REGEX_MOBILE_EXACT = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$";
    /**
     * all numbers with SMS
     * <p>
     * Phone number + Data only number
     */
    private static final String REGEX_ALL_MOBILE_WITH_SMS = "^(?:\\+?86)?1(?:3\\d{3}|5[^4\\D]\\d{2}|8\\d{3}|7(?:[01356789]\\d{2}|4(?:0\\d|1[0-2]|9\\d))|9[189]\\d{2}|6[567]\\d{2}|4[579]\\d{2})\\d{6}$";
    /**
     * 电话号码（正则）
     */
    private static final String REGEX_TEL = "^0\\d{2,3}[- ]?\\d{7,8}";
    /**
     * 身份证号码 15 位（正则）
     */
    private static final String REGEX_ID_CARD15 = "^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$";
    /**
     * 身份证号码 18 位（正则）
     */
    private static final String REGEX_ID_CARD18 = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9Xx])$";
    /**
     * 邮箱（正则）
     */
    private static final String REGEX_EMAIL = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
    /**
     * URL（正则）
     */
    private static final String REGEX_URL = "[a-zA-z]+://[^\\s]*";
    /**
     * 汉字（正则）
     */
    private static final String REGEX_ZH = "^[\\u4e00-\\u9fa5]+$";

    /**
     * constructor
     */
    private RegularUtils() {
        throw new UnsupportedOperationException("You can't instantiate me...");
    }

    /**
     * 手机号（精确）
     *
     * @param input 待验文本
     * @return true 匹 / false 不匹
     */
    public static boolean areMobileExact(CharSequence input) {
        return areMatch(REGEX_MOBILE_EXACT, input);
    }

    /**
     * 全手机号
     *
     * @param input 待验文本
     * @return true 匹 / false 不匹
     */
    public static boolean allMobile(CharSequence input) {
        return areMatch(REGEX_ALL_MOBILE, input);
    }

    /**
     * 符合短信全手机号
     *
     * @param input 待验文本
     * @return true 匹 / false 不匹
     */
    public static boolean allMobileWithSms(CharSequence input) {
        return areMatch(REGEX_ALL_MOBILE_WITH_SMS, input);
    }

    /**
     * 电话号码
     *
     * @param input 待验文本
     * @return true 匹 / false 不匹
     */
    public static boolean areTel(CharSequence input) {
        return areMatch(REGEX_TEL, input);
    }

    /**
     * 身份证号码 15 位
     *
     * @param input 待验文本
     * @return true 匹 / false 不匹
     */
    public static boolean areIdCard15(CharSequence input) {
        return areMatch(REGEX_ID_CARD15, input);
    }

    /**
     * 身份证号码 18 位
     *
     * @param input 待验文本
     * @return true 匹 / false 不匹
     */
    public static boolean areIdCard18(CharSequence input) {
        return areMatch(REGEX_ID_CARD18, input);
    }

    /**
     * 邮箱
     *
     * @param input 待验文本
     * @return true 匹 / false 不匹
     */
    public static boolean areEmail(CharSequence input) {
        return areMatch(REGEX_EMAIL, input);
    }

    /**
     * URL
     *
     * @param input 待验文本
     * @return true 匹 / false 不匹
     */
    public static boolean areUrl(CharSequence input) {
        return areMatch(REGEX_URL, input);
    }

    /**
     * 汉字
     *
     * @param input 待验文本
     * @return true 匹 / false 不匹
     */
    public static boolean areZh(CharSequence input) {
        return areMatch(REGEX_ZH, input);
    }

    /**
     * 匹正则否
     *
     * @param regex 正则表达式
     * @param input 所匹字符串
     * @return true 匹 / false 不匹
     */
    private static boolean areMatch(String regex, CharSequence input) {
        return ((null != input) && (input.length() > 0) && Pattern.matches(regex, input));
    }

    /**
     * 正则匹配部分
     *
     * @param regex 正则表达式
     * @param input 所匹字符串
     * @return 正则匹配部分
     */
    public static List<String> getMatches(String regex, CharSequence input) {
        if (null == input) {
            return null;
        }
        List<String> matches = new ArrayList<>();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            matches.add(matcher.group());
        }
        return matches;
    }

    /**
     * 正则匹配分组
     *
     * @param input 要分组的字符串
     * @param regex 正则表达式
     * @return 正则匹配分组
     */
    public static String[] getSplits(String input, String regex) {
        if (null == input) {
            return null;
        }
        return input.split(regex);
    }

    /**
     * 替正则匹配第一部分
     *
     * @param input       所替字符串
     * @param regex       正则表达式
     * @param replacement 代替者
     * @return 替正则匹配第一部分
     */
    public static String getReplaceFirst(String input, String regex, String replacement) {
        if (null == input) {
            return null;
        }
        return Pattern.compile(regex).matcher(input).replaceFirst(replacement);
    }

    /**
     * 替所有正则匹配部分
     *
     * @param input       所替字符串
     * @param regex       正则表达式
     * @param replacement 代替者
     * @return 替所有正则匹配部分
     */
    public static String getReplaceAll(String input, String regex, String replacement) {
        if (null == input) {
            return null;
        }
        return Pattern.compile(regex).matcher(input).replaceAll(replacement);
    }
}
