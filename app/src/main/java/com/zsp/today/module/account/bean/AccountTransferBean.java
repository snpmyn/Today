package com.zsp.today.module.account.bean;

import java.io.Serializable;

/**
 * Created on 2021/11/26
 *
 * @author zsp
 * @desc 账目传输
 */
public class AccountTransferBean implements Serializable {
    /**
     * 年
     */
    private String year;
    /**
     * 月
     */
    private String month;
    /**
     * 日期
     */
    private String date;
    /**
     * 类目
     */
    private String category;
    /**
     * 金额
     */
    private Double amount = 0.0;

    /**
     * constructor
     *
     * @param year  年
     * @param month 月
     */
    public AccountTransferBean(String year, String month) {
        this.year = year;
        this.month = month;
    }

    /**
     * constructor
     *
     * @param date 日期
     */
    public AccountTransferBean(String date) {
        this.date = date;
    }

    /**
     * constructor
     *
     * @param date     日期
     * @param category 类目
     * @param amount   金额
     */
    public AccountTransferBean(String date, String category, Double amount) {
        this.date = date;
        this.category = category;
        this.amount = amount;
    }

    public String getYear() {
        return year;
    }

    public String getMonth() {
        return month;
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
