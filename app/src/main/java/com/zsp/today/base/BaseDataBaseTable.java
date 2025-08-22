package com.zsp.today.base;

import android.text.TextUtils;

import litepal.annotation.Column;
import litepal.crud.LitePalSupport;
import litepal.value.LitePalMagic;
import util.datetime.DateUtils;

/**
 * Created on 2022/5/25
 *
 * @author zsp
 * @desc 基础数据库表
 */
public class BaseDataBaseTable extends LitePalSupport {
    /**
     * 手机号
     */
    @Column(nullable = false)
    private String phoneNumber;
    /**
     * 年
     */
    @Column(nullable = false)
    private String year;
    /**
     * 月
     */
    @Column(nullable = false)
    private String month;
    /**
     * 日期
     */
    @Column(nullable = false)
    private String date;

    /**
     * constructor
     * <p>
     * Update needs a default constructor.
     */
    public BaseDataBaseTable() {

    }

    /**
     * constructor
     *
     * @param phoneNumber 手机号
     * @param date        日期
     */
    public BaseDataBaseTable(String phoneNumber, String date) {
        this.phoneNumber = phoneNumber;
        if (TextUtils.isEmpty(date)) {
            this.date = DateUtils.getCurrentTimeYearMonthDay();
        } else {
            this.date = date;
        }
        this.year = this.date.substring(0, this.date.indexOf(LitePalMagic.STRING_WHIPPLETREE));
        this.month = this.date.substring(this.date.indexOf(LitePalMagic.STRING_WHIPPLETREE) + 1, this.date.lastIndexOf(LitePalMagic.STRING_WHIPPLETREE));
    }

    public long getBaseObjectId() {
        return getBaseObjId();
    }

    public String getPhoneNumber() {
        return phoneNumber;
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

    /**
     * Get the baseObjId of this model if it's useful for developers.
     * It's for system use usually. Do not try to assign or modify it.
     *
     * @return The base object id.
     */
    @Override
    protected long getBaseObjId() {
        return super.getBaseObjId();
    }
}