package com.zsp.today.module.account.bean;

/**
 * Created on 2021/1/4
 *
 * @author zsp
 * @desc 账目详情
 */
public class AccountDetailBean {
    /**
     * 日期
     */
    private final String date;
    /**
     * 类目
     */
    private final String category;
    /**
     * 金额
     */
    private final Double amount;

    /**
     * constructor
     *
     * @param date     日期
     * @param category 类目
     * @param amount   金额
     */
    public AccountDetailBean(String date, String category, Double amount) {
        this.date = date;
        this.category = category;
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public String getCategory() {
        return category;
    }

    public Double getAmount() {
        return amount;
    }
}