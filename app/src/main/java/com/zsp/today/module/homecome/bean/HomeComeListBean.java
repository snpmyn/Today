package com.zsp.today.module.homecome.bean;

import com.zsp.today.module.homecome.MemorialKit;

/**
 * Created on 2025/10/10.
 *
 * @author 郑少鹏
 * @desc 归心月列表
 */
public class HomeComeListBean {
    /**
     * 称呼
     */
    private final String call;
    /**
     * 日历类型
     * <p>
     * 阳历
     * 农历
     */
    private final String calendarType;
    /**
     * 去世年
     */
    private final int deathYear;
    /**
     * 去世月
     */
    private final int deathMonth;
    /**
     * 去世日
     */
    private final int deathDay;
    /**
     * 纪念信息
     */
    private final MemorialKit.MemorialInfo memorialInfo;

    /**
     * constructor
     *
     * @param call         称呼
     * @param calendarType 日历类型
     * @param deathYear    去世年
     * @param deathMonth   去世月
     * @param deathDay     去世日
     * @param memorialInfo 纪念信息
     */
    public HomeComeListBean(String call, String calendarType, int deathYear, int deathMonth, int deathDay, MemorialKit.MemorialInfo memorialInfo) {
        this.call = call;
        this.calendarType = calendarType;
        this.deathYear = deathYear;
        this.deathMonth = deathMonth;
        this.deathDay = deathDay;
        this.memorialInfo = memorialInfo;
    }

    public String getCall() {
        return call;
    }

    public String getCalendarType() {
        return calendarType;
    }

    public int getDeathYear() {
        return deathYear;
    }

    public int getDeathMonth() {
        return deathMonth;
    }

    public int getDeathDay() {
        return deathDay;
    }

    public MemorialKit.MemorialInfo getMemorialInfo() {
        return memorialInfo;
    }
}