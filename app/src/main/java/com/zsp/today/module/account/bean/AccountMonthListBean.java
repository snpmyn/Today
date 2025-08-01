package com.zsp.today.module.account.bean;

/**
 * Created on 2021/11/4
 *
 * @author zsp
 * @desc 账目月列表
 */
public class AccountMonthListBean {
    /**
     * 年
     */
    private final String year;
    /**
     * 月
     */
    private final String month;
    /**
     * 总金额
     */
    private final String totalAmount;

    /**
     * constructor
     *
     * @param year        年
     * @param month       月
     * @param totalAmount 总金额
     */
    public AccountMonthListBean(String year, String month, String totalAmount) {
        this.year = year;
        this.month = month;
        this.totalAmount = totalAmount;
    }

    public String getYear() {
        return year;
    }

    public String getMonth() {
        return month;
    }

    public String getTotalAmount() {
        return totalAmount;
    }
}
