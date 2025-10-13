package com.zsp.today.module.homecome.database;

import com.zsp.today.base.BaseDataBaseTable;

import litepal.annotation.Column;

/**
 * Created on 2025/10/5.
 *
 * @author 郑少鹏
 * @desc 归心数据库表
 */
public class HomeComeDataBaseTable extends BaseDataBaseTable {
    /**
     * 称呼
     */
    @Column(nullable = false)
    private String call;
    /**
     * 日历类型
     * <p>
     * 阳历
     * 农历
     */
    @Column(nullable = false)
    private String calendarType;
    /**
     * 去世年
     */
    @Column(nullable = false)
    private int deathYear;
    /**
     * 去世月
     */
    @Column(nullable = false)
    private int deathMonth;
    /**
     * 去世日
     */
    @Column(nullable = false)
    private int deathDay;

    /**
     * constructor
     * <p>
     * Update needs a default constructor.
     */
    public HomeComeDataBaseTable() {
        super();
    }

    /**
     * constructor
     *
     * @param phoneNumber  手机号
     * @param date         日期
     * @param call         称呼
     * @param calendarType 日历类型
     * @param deathYear    去世年
     * @param deathMonth   去世月
     * @param deathDay     去世日
     */
    public HomeComeDataBaseTable(String phoneNumber, String date, String call, String calendarType, int deathYear, int deathMonth, int deathDay) {
        super(phoneNumber, date);
        this.call = call;
        this.calendarType = calendarType;
        this.deathYear = deathYear;
        this.deathMonth = deathMonth;
        this.deathDay = deathDay;
    }

    public String getCall() {
        return call;
    }

    public void setCall(String call) {
        this.call = call;
    }

    public String getCalendarType() {
        return calendarType;
    }

    public void setCalendarType(String calendarType) {
        this.calendarType = calendarType;
    }

    public int getDeathYear() {
        return deathYear;
    }

    public void setDeathYear(int deathYear) {
        this.deathYear = deathYear;
    }

    public int getDeathMonth() {
        return deathMonth;
    }

    public void setDeathMonth(int deathMonth) {
        this.deathMonth = deathMonth;
    }

    public int getDeathDay() {
        return deathDay;
    }

    public void setDeathDay(int deathDay) {
        this.deathDay = deathDay;
    }
}