package com.zsp.today.value;

/**
 * Created on 2022/6/28
 *
 * @author zsp
 * @desc 账目条件
 */
public class AccountCondition {
    public static String ACCOUNT_PHONE_NUMBER = "phoneNumber = ?";
    public static String ACCOUNT_PHONE_NUMBER_AND_YEAR = "phoneNumber = ? and year = ?";
    public static String ACCOUNT_PHONE_NUMBER_AND_DATE = "phoneNumber = ? and date = ?";
    public static String ACCOUNT_PHONE_NUMBER_AND_YEAR_AND_MONTH = "phoneNumber = ? and year = ? and month = ?";
    public static String ACCOUNT_PHONE_NUMBER_AND_DATE_AND_CATEGORY = "phoneNumber = ? and date = ? and category = ?";
    public static String ACCOUNT_PHONE_NUMBER_AND_DATE_AND_CATEGORY_AND_AMOUNT = "phoneNumber = ? and date = ? and category = ? and amount = ?";
}