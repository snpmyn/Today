package util.number;

import androidx.annotation.NonNull;

import util.data.StringUtils;
import util.validate.RegularUtils;
import util.value.UtilMagic;

/**
 * Created on 2021/10/12
 *
 * @author zsp
 * @desc 数字格式化工具类
 */
public class NumberFormatUtils {
    /**
     * 格式化手机号一
     * <p>
     * XXX **** XXX
     *
     * @param phoneNumber 手机号
     * @return 格式化后手机号
     */
    public static String formatPhoneNumberOne(String phoneNumber) {
        if (!RegularUtils.allMobile(phoneNumber)) {
            return phoneNumber;
        }
        return phoneNumber.replaceAll("(\\d{3})\\d+(\\d{4})", "$1 **** $2");
    }

    /**
     * 格式化手机号二
     * <p>
     * XXX  XXXX  XXXX
     *
     * @param phoneNumber 手机号
     * @return 格式化后手机号
     */
    public static String formatPhoneNumberTwo(String phoneNumber) {
        if (!RegularUtils.allMobile(phoneNumber)) {
            return phoneNumber;
        }
        StringBuilder stringBuilder = new StringBuilder(phoneNumber);
        return stringBuilder.insert(7, " ").insert(3, " ").toString();
    }

    /**
     * 格式化卡号一
     * <p>
     * XXXX ****** XXXX
     *
     * @param cardNumber 卡号
     * @return 格式化后卡号
     */
    @NonNull
    public static String formatCardNumberOne(String cardNumber) {
        if (StringUtils.areEmpty(cardNumber)) {
            return "";
        }
        if (cardNumber.length() < UtilMagic.INT_FIVE) {
            return cardNumber;
        }
        String start = cardNumber.substring(0, 4);
        String end = cardNumber.substring(cardNumber.length() - 4);
        return (start + " ****** " + end);
    }

    /**
     * 格式化卡号二
     * <p>
     * XXXX XXXX XXXX XXXX
     *
     * @param cardNumber 卡号
     * @return 格式化后卡号
     */
    @NonNull
    public static String formatCardNumberTwo(String cardNumber) {
        if (StringUtils.areEmpty(cardNumber)) {
            return "";
        }
        String rex = "(\\d{4})";
        return cardNumber.replaceAll(rex, "$1 ");
    }

    /**
     * 格式化身份证号
     * <p>
     * X **************** X
     *
     * @param identifyCardNumber 身份证号
     * @param startNumber        开始个数
     * @param endNumber          结束个数
     * @param asteriskNumber     星号个数
     * @return 格式化后身份证号
     */
    @NonNull
    public static String formatIdentifyCardNumber(String identifyCardNumber, int startNumber, int endNumber, int asteriskNumber) {
        if (StringUtils.areEmpty(identifyCardNumber)) {
            return "";
        }
        if ((startNumber + endNumber) > identifyCardNumber.length()) {
            return identifyCardNumber;
        }
        String start = identifyCardNumber.substring(0, startNumber);
        String end = identifyCardNumber.substring(identifyCardNumber.length() - endNumber);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < asteriskNumber; i++) {
            stringBuilder.append("*");
        }
        return (start + stringBuilder + end);
    }

    /**
     * 格式化条形码
     * <p>
     * XXXX XXXX XXXX XXXXXX
     *
     * @param barCode 条形码
     * @return 格式化后条形码
     */
    @NonNull
    public static String formatBarCode(String barCode) {
        if (StringUtils.areEmpty(barCode)) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder(barCode);
        if (barCode.length() <= UtilMagic.INT_EIGHT) {
            return barCode;
        }
        if (barCode.length() <= UtilMagic.INT_TWELVE) {
            return stringBuilder.insert(8, " ").insert(4, " ").toString();
        }
        if (barCode.length() <= UtilMagic.INT_SIXTEEN) {
            return stringBuilder.insert(12, " ").insert(8, " ").insert(4, " ").toString();
        }
        return stringBuilder.insert(16, " ").insert(12, " ").insert(8, " ").insert(4, " ").toString();
    }
}