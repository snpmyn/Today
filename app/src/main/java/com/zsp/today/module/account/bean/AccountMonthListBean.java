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
     * 数量
     */
    private final int count;
    /**
     * 最大类目和金额
     */
    private final String maxCategoryAndAmount;

    /**
     * constructor
     *
     * @param year                 年
     * @param month                月
     * @param totalAmount          总金额
     * @param count                数量
     * @param maxCategoryAndAmount 最大类目和金额
     */
    public AccountMonthListBean(String year, String month, String totalAmount, int count, String maxCategoryAndAmount) {
        this.year = year;
        this.month = month;
        this.totalAmount = totalAmount;
        this.count = count;
        this.maxCategoryAndAmount = maxCategoryAndAmount;
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

    public int getCount() {
        return count;
    }

    public String getMaxCategoryAndAmount() {
        return maxCategoryAndAmount;
    }
}