package util.datetime;

import android.content.Context;
import android.text.TextUtils;

import com.zsp.core.R;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import timber.log.Timber;
import util.data.BigDecimalUtils;
import util.value.UtilMagic;

/**
 * Created on 2017/10/13.
 *
 * @author 郑少鹏
 * @desc DateUtils
 */
public class DateUtils {
    private static final String[] WEEKS = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
    private static SimpleDateFormat simpleDateFormat = null;

    /**
     * 据指定格式获当前时
     *
     * @param format 格式
     * @return String
     */
    private static @NotNull String getCurrentTime(String format) {
        SimpleDateFormat simpleDateFormat = DateFormatUtils.getFormat(format);
        Date date = new Date();
        return simpleDateFormat.format(date);
    }

    /**
     * 当前时（yyyy）
     *
     * @return String
     */
    public static @NotNull String getCurrentTimeYear() {
        return getCurrentTime(DateFormatUtils.DATE_YEAR);
    }

    /**
     * 当前时（yyyy-MM）
     *
     * @return String
     */
    public static @NotNull String getCurrentTimeYearMonth() {
        return getCurrentTime(DateFormatUtils.DATE_YEAR_MONTH);
    }

    /**
     * 当前时（yyyy-MM-dd）
     *
     * @return String
     */
    public static @NotNull String getCurrentTimeYearMonthDay() {
        return getCurrentTime(DateFormatUtils.DATE_YEAR_MONTH_DATE);
    }

    /**
     * 当前时（yyyy-MM-dd HH:mm）
     *
     * @return String
     */
    public static @NotNull String getCurrentTimeYearMonthDayHourMinute() {
        return getCurrentTime(DateFormatUtils.DATE_YEAR_MONTH_DATE_HOUR_MINUTE);
    }

    /**
     * 当前时（yyyy-MM-dd HH:mm:ss）
     *
     * @return String
     */
    public static @NotNull String getCurrentTimeYearMonthDayHourMinuteSecondOne() {
        return getCurrentTime(DateFormatUtils.DATE_YEAR_MONTH_DATE_HOUR_MINUTE_SECOND_ONE);
    }

    /**
     * 当前时（yyyy-MM-dd-HH-mm-ss）
     *
     * @return String
     */
    public static @NotNull String getCurrentTimeYearMonthDayHourMinuteSecondTwo() {
        return getCurrentTime(DateFormatUtils.DATE_YEAR_MONTH_DATE_HOUR_MINUTE_SECOND_Two);
    }

    /**
     * 指定格式当前时（为空格式为 yyyy-mm-dd HH:mm:ss）
     *
     * @param format 格式
     * @return Date
     */
    private static Date getCurrentDate(String format) {
        SimpleDateFormat simpleDateFormat = DateFormatUtils.getFormat(format);
        String stringDate = getCurrentTime(format);
        Date date = null;
        try {
            date = simpleDateFormat.parse(stringDate);
        } catch (ParseException e) {
            Timber.e(e);
        }
        return date;
    }

    /**
     * 日期转化（时分）
     *
     * @param date date
     * @return string
     */
    public static @NotNull String dateConversionToHourMinute(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.US);
        return simpleDateFormat.format(date);
    }

    /**
     * 日期转化（分秒）
     *
     * @param date date
     * @return string
     */
    public static @NotNull String dateConversionToMinuteSecond(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss", Locale.US);
        return simpleDateFormat.format(date);
    }

    /**
     * 日期转化（年月日）
     *
     * @param date date
     * @return string
     */
    public static @NotNull String dateConversionToYearMonth(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM", Locale.US);
        return simpleDateFormat.format(date);
    }

    /**
     * 日期转化（年月日）
     *
     * @param date date
     * @return string
     */
    public static @NotNull String dateConversionToYearMonthDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        return simpleDateFormat.format(date);
    }

    /**
     * 日期转化（年月日 时分）
     *
     * @param date date
     * @return string
     */
    public static @NotNull String dateConversionToYearMonthDateHourMinute(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
        return simpleDateFormat.format(date);
    }

    /**
     * 日期转化（年月日 时分秒）
     *
     * @param date date
     * @return string
     */
    public static @NotNull String dateConversionToYearMonthDateHourMinuteSecond(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        return simpleDateFormat.format(date);
    }

    /**
     * 当前时（格式 yyyy-MM-dd HH:mm:ss）
     *
     * @return Date
     */
    public static Date getCurrentDate() {
        return getCurrentDate(DateFormatUtils.DATE_YEAR_MONTH_DATE_HOUR_MINUTE_SECOND_ONE);
    }

    /**
     * 获当前时（HH:mm）
     * <p>
     * calendar.get(Calendar.YEAR)
     * calendar.get(Calendar.MONTH)
     * calendar.get(Calendar.DAY_OF_MONTH)
     *
     * @return HashMap
     */
    public static @NotNull String getCurrentHm() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
    }

    /**
     * 给指定日期加年份（空默当前时）
     *
     * @param year   年份 正加负减
     * @param date   空默当前时
     * @param format 默格式 yyyy-MM-dd HH:mm:ss
     * @return String
     */
    private static @NotNull String addYearToDate(int year, Date date, String format) {
        Calendar calender = getCalendar(date, format);
        SimpleDateFormat simpleDateFormat = DateFormatUtils.getFormat(format);
        calender.add(Calendar.YEAR, year);
        return simpleDateFormat.format(calender.getTime());
    }

    /**
     * 给指定日期加年份（空默当前时）
     *
     * @param year   年份 正加负减
     * @param date   空默当前时
     * @param format 默格式 yyyy-MM-dd HH:mm:ss
     * @return String
     */
    public static @NotNull String addYearToDate(int year, String date, String format) {
        Date newDate = new Date();
        if ((null != date) && !date.isEmpty()) {
            newDate = stringToDate(date, format);
        }
        return addYearToDate(year, newDate, format);
    }

    /**
     * 给指定日期加月份 空默当前时
     *
     * @param month  增加月份 正加负减
     * @param date   指定时
     * @param format 指定格式（空默 yyyy-mm-dd HH:mm:ss）
     * @return String
     */
    private static @NotNull String addMothToDate(int month, Date date, String format) {
        Calendar calender = getCalendar(date, format);
        SimpleDateFormat simpleDateFormat = DateFormatUtils.getFormat(format);
        calender.add(Calendar.MONTH, month);
        return simpleDateFormat.format(calender.getTime());
    }

    /**
     * 给指定日期加月份 空默当前时
     *
     * @param month  增加月份 正加负减
     * @param date   指定时
     * @param format 指定格式（空默 yyyy-mm-dd HH:mm:ss）
     * @return String
     */
    public static @NotNull String addMothToDate(int month, String date, String format) {
        Date newDate = new Date();
        if ((null != date) && !date.isEmpty()) {
            newDate = stringToDate(date, format);
        }
        return addMothToDate(month, newDate, format);
    }

    /**
     * 给指定日期加天数（空默当前时）
     *
     * @param day    加天数 正加负减
     * @param date   指定日期
     * @param format 日期格式（空默 yyyy-mm-dd HH:mm:ss）
     * @return String
     */
    private static @NotNull String addDayToDate(int day, Date date, String format) {
        Calendar calendar = getCalendar(date, format);
        SimpleDateFormat simpleDateFormat = DateFormatUtils.getFormat(format);
        calendar.add(Calendar.DATE, day);
        return simpleDateFormat.format(calendar.getTime());
    }

    /**
     * 给指定日期加天数（空默当前时）
     *
     * @param day    加天数 正加负减
     * @param date   指定日期
     * @param format 日期格式（空默 yyyy-mm-dd HH:mm:ss）
     * @return String
     */
    public static @NotNull String addDayToDate(int day, String date, String format) {
        Date newDate = new Date();
        if ((null != date) && !date.isEmpty()) {
            newDate = stringToDate(date, format);
        }
        return addDayToDate(day, newDate, format);
    }

    /**
     * 给指定日期加小时（空默当前时）
     *
     * @param hour   加小时 正加负减
     * @param date   指定日期
     * @param format 日期格式（空默 yyyy-mm-dd HH:mm:ss）
     * @return String
     */
    private static @NotNull String addHourToDate(int hour, Date date, String format) {
        Calendar calendar = getCalendar(date, format);
        SimpleDateFormat simpleDateFormat = DateFormatUtils.getFormat(format);
        calendar.add(Calendar.HOUR, hour);
        return simpleDateFormat.format(calendar.getTime());
    }

    /**
     * 给指定日期加小时（空默当前时）
     *
     * @param hour   加小时 正加负减
     * @param date   指定日期
     * @param format 日期格式（空默 yyyy-mm-dd HH:mm:ss）
     * @return String
     */
    public static @NotNull String addHourToDate(int hour, String date, String format) {
        Date newDate = new Date();
        if ((null != date) && !date.isEmpty()) {
            newDate = stringToDate(date, format);
        }
        return addHourToDate(hour, newDate, format);
    }

    /**
     * 给指定日期加分钟（空默当前时）
     *
     * @param minute 加分钟 正加负减
     * @param date   指定日期
     * @param format 日期格式（空默 yyyy-mm-dd HH:mm:ss）
     * @return String
     */
    private static @NotNull String addMinuteToDate(int minute, Date date, String format) {
        Calendar calendar = getCalendar(date, format);
        SimpleDateFormat simpleDateFormat = DateFormatUtils.getFormat(format);
        calendar.add(Calendar.MINUTE, minute);
        return simpleDateFormat.format(calendar.getTime());
    }

    /**
     * 给指定日期加分钟（空默当前时）
     *
     * @param minute 加分钟 正加负减
     * @param date   指定日期
     * @param format 日期格式（空默 yyyy-mm-dd HH:mm:ss）
     * @return String
     */
    public static @NotNull String addMinuteToDate(int minute, String date, String format) {
        Date newDate = new Date();
        if ((null != date) && !date.isEmpty()) {
            newDate = stringToDate(date, format);
        }
        return addMinuteToDate(minute, newDate, format);
    }

    /**
     * 给指定日期加秒（空默当前时）
     *
     * @param second 加秒 正加负减
     * @param date   指定日期
     * @param format 日期格式（空默 yyyy-mm-dd HH:mm:ss）
     * @return String
     */
    private static @NotNull String addSecondToDate(int second, Date date, String format) {
        Calendar calendar = getCalendar(date, format);
        SimpleDateFormat simpleDateFormat = DateFormatUtils.getFormat(format);
        calendar.add(Calendar.SECOND, second);
        return simpleDateFormat.format(calendar.getTime());
    }

    /**
     * 给指定日期加秒（空默当前时）
     *
     * @param second 加秒 正加负减
     * @param date   指定日期
     * @param format 日期格式（空默 yyyy-mm-dd HH:mm:ss）
     * @return String
     */
    public static @NotNull String addSecondToDate(int second, String date, String format) {
        Date newDate = new Date();
        if ((null != date) && !date.isEmpty()) {
            newDate = stringToDate(date, format);
        }
        return addSecondToDate(second, newDate, format);
    }

    /**
     * 指定格式指定时间日历
     *
     * @param date   时间
     * @param format 格式
     * @return Calendar
     */
    private static @NotNull Calendar getCalendar(Date date, String format) {
        if (null == date) {
            date = getCurrentDate(format);
        }
        Calendar calender = Calendar.getInstance();
        calender.setTime(date);
        return calender;
    }

    /**
     * 字符串转日期
     *
     * @param value value
     * @return Date
     */
    private static Date stringToDate(String value) {
        if ((null == value) || value.isEmpty()) {
            return null;
        }
        SimpleDateFormat simpleDateFormat = DateFormatUtils.getFormat(DateFormatUtils.DATE_YEAR_MONTH_DATE_HOUR_MINUTE_SECOND_ONE);
        Date date = null;
        try {
            value = DateFormatUtils.formatDate(value, DateFormatUtils.DATE_YEAR_MONTH_DATE_HOUR_MINUTE_SECOND_ONE);
            date = simpleDateFormat.parse(value);
        } catch (Exception e) {
            Timber.e(e);
        }
        return date;
    }

    /**
     * 字符串（格式符合规范）转 Date
     *
     * @param value  需转字符串
     * @param format 日期格式
     * @return Date
     */
    static Date stringToDate(String value, String format) {
        if ((null == value) || value.isEmpty()) {
            return null;
        }
        SimpleDateFormat simpleDateFormat = DateFormatUtils.getFormat(format);
        Date date = null;
        try {
            value = DateFormatUtils.formatDate(value, format);
            date = simpleDateFormat.parse(value);
        } catch (Exception e) {
            Timber.e(e);
        }
        return date;
    }

    /**
     * 日期格式转 String
     *
     * @param value  需转日期
     * @param format 日期格式
     * @return String
     */
    private static String dateToString(Date value, String format) {
        if (null == value) {
            return null;
        }
        SimpleDateFormat simpleDateFormat = DateFormatUtils.getFormat(format);
        return simpleDateFormat.format(value);
    }

    /**
     * 日期转字符串
     *
     * @param value value
     * @return string
     */
    static String dateToString(Date value) {
        if (null == value) {
            return null;
        }
        SimpleDateFormat simpleDateFormat = DateFormatUtils.getFormat(DateFormatUtils.DATE_YEAR_MONTH_DATE_HOUR_MINUTE_SECOND_ONE);
        return simpleDateFormat.format(value);
    }

    /**
     * 指定日期的年份
     *
     * @param value 日期
     * @return int
     */
    public static int getCurrentYear(Date value) {
        String date = dateToString(value, DateFormatUtils.DATE_YEAR);
        return Integer.parseInt(date);
    }

    /**
     * 指定日期的年份
     *
     * @param value 日期
     * @return int
     */
    public static int getCurrentYear(String value) {
        Date date = stringToDate(value, DateFormatUtils.DATE_YEAR);
        Calendar calendar = getCalendar(date, DateFormatUtils.DATE_YEAR);
        return calendar.get(Calendar.YEAR);
    }

    /**
     * 指定日期月份
     *
     * @param value 日期
     * @return int
     */
    public static int getCurrentMonth(Date value) {
        String date = dateToString(value, DateFormatUtils.DATE_MONTH);
        return Integer.parseInt(date);
    }

    /**
     * 指定日期月份
     *
     * @param value 日期
     * @return int
     */
    public static int getCurrentMonth(String value) {
        Date date = stringToDate(value, DateFormatUtils.DATE_MONTH);
        Calendar calendar = getCalendar(date, DateFormatUtils.DATE_MONTH);
        return calendar.get(Calendar.MONTH);
    }

    /**
     * 指定日期天份
     *
     * @param value 日期
     * @return int
     */
    public static int getCurrentDay(Date value) {
        String date = dateToString(value, DateFormatUtils.DATE_DAY);
        return Integer.parseInt(date);
    }

    /**
     * 指定日期天份
     *
     * @param value 日期
     * @return int
     */
    public static int getCurrentDay(String value) {
        Date date = stringToDate(value, DateFormatUtils.DATE_DAY);
        Calendar calendar = getCalendar(date, DateFormatUtils.DATE_DAY);
        return calendar.get(Calendar.DATE);
    }

    /**
     * 当前日期星期几
     *
     * @param value 日期
     * @return String
     */
    private static String getCurrentWeek(Date value) {
        Calendar calendar = getCalendar(value, DateFormatUtils.DATE_YEAR_MONTH_DATE);
        int weekIndex = Math.max(calendar.get(Calendar.DAY_OF_WEEK) - 1, 0);
        return WEEKS[weekIndex];
    }

    /**
     * 当前日期星期几
     *
     * @param value 日期
     * @return String
     */
    public static String getCurrentWeek(String value) {
        Date date = stringToDate(value, DateFormatUtils.DATE_YEAR_MONTH_DATE);
        return getCurrentWeek(date);
    }

    /**
     * 指定日期小时
     *
     * @param value 日期
     * @return int
     */
    public static int getCurrentHour(Date value) {
        String date = dateToString(value, DateFormatUtils.DATE_HOUR);
        return Integer.parseInt(date);
    }

    /**
     * 指定日期小时
     *
     * @param value 日期
     * @return int
     */
    public static int getCurrentHour(String value) {
        Date date = stringToDate(value, DateFormatUtils.DATE_HOUR);
        Calendar calendar = getCalendar(date, DateFormatUtils.DATE_HOUR);
        return calendar.get(Calendar.DATE);
    }

    /**
     * 指定日期分钟
     *
     * @param value 日期
     * @return int
     */
    public static int getCurrentMinute(Date value) {
        String date = dateToString(value, DateFormatUtils.DATE_MINUTE);
        return Integer.parseInt(date);
    }

    /**
     * 指定日期分钟
     *
     * @param value 日期
     * @return int
     */
    public static int getCurrentMinute(String value) {
        Date date = stringToDate(value, DateFormatUtils.DATE_MINUTE);
        Calendar calendar = getCalendar(date, DateFormatUtils.DATE_MINUTE);
        return calendar.get(Calendar.MINUTE);
    }

    /**
     * 较两日期隔（天、月、年）
     * <p>
     * compareDate("2009-09-12", null, 0) 天
     * compareDate("2009-09-12", null, 1) 月
     * compareDate("2009-09-12", null, 2) 年
     *
     * @param startDay 需比较时间 不能为空，正确日期格式如 2009-09-12
     * @param endDay   被比较时间 为空则为当前时间
     * @param type     返回值类型 0 天数、1 月数、2 年数
     * @return int
     */
    public static int compareDate(String startDay, String endDay, int type) {
        int n = 0;
        startDay = DateFormatUtils.formatDate(startDay, "yyyy-MM-dd");
        endDay = DateFormatUtils.formatDate(endDay, "yyyy-MM-dd");
        String formatStyle = "yyyy-MM-dd";
        if (type == 1) {
            formatStyle = "yyyy-MM";
        } else if (UtilMagic.INT_TWO == type) {
            formatStyle = "yyyy";
        }
        endDay = ((null == endDay) ? getCurrentTime("yyyy-MM-dd") : endDay);
        DateFormat dateFormat = new SimpleDateFormat(formatStyle, Locale.US);
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        try {
            c1.setTime(Objects.requireNonNull(dateFormat.parse(startDay), "must not be null"));
            c2.setTime(Objects.requireNonNull(dateFormat.parse(endDay), "must not be null"));
        } catch (Exception e) {
            Timber.e(e);
        }
        while (!c1.after(c2)) {
            // 循环对比至相等
            // n 即所要结果
            n++;
            if (type == 1) {
                // 比月（月 + 1）
                c1.add(Calendar.MONTH, 1);
            } else {
                // 比天数（日 + 1）
                c1.add(Calendar.DATE, 1);
            }
        }
        n = (n - 1);
        if (type == UtilMagic.INT_TWO) {
            n = (n / 365);
        }
        return n;
    }

    /**
     * 较两时间差（时、分、秒）
     *
     * @param oldTime 需比时间 不能为空且符正确格式 2012-12-12 12:12
     * @param newTime 需被比时间 空默当前时
     * @param type    1 小时 2 分钟 3 秒
     * @return int
     */
    public static int compareTime(String oldTime, String newTime, int type) {
        // newTime 空默当前时
        if ((null == newTime) || newTime.isEmpty()) {
            newTime = getCurrentTimeYearMonthDayHourMinuteSecondOne();
        }
        SimpleDateFormat simpleDateFormat = DateFormatUtils.getFormat("");
        int value = 0;
        try {
            Date oldDate = simpleDateFormat.parse(oldTime);
            Date newDate = simpleDateFormat.parse(newTime);
            // 转秒
            long between = 0;
            if ((null != newDate) && (null != oldDate)) {
                between = ((newDate.getTime() - oldDate.getTime()) / 1000);
            }
            if (type == 1) {
                // 小时
                value = (int) (between % (24 * 36000) / 3600);
            } else if (type == UtilMagic.INT_TWO) {
                value = (int) (between % 3600 / 60);
            }
        } catch (ParseException e) {
            Timber.e(e);
        }
        return value;
    }

    /**
     * 较两日期大小
     * <p>
     * date1 > date2 返 1
     * date1 = date2 返 0
     * date1 < date2 返 -1
     *
     * @param date1  date1
     * @param date2  date2
     * @param format 待转格式
     * @return 结果
     */
    public static int compare(String date1, String date2, String format) {
        DateFormat dateFormat = DateFormatUtils.getFormat(format);
        try {
            Date dt1 = dateFormat.parse(date1);
            Date dt2 = dateFormat.parse(date2);
            if ((null != dt1) && (null != dt2)) {
                return Long.compare(dt1.getTime(), dt2.getTime());
            }
        } catch (Exception exception) {
            Timber.e(exception);
        }
        return 0;
    }

    /**
     * 获指定月份头天
     *
     * @param date date
     * @return string
     */
    private static @NotNull String getMonthFirstDate(String date) {
        date = DateFormatUtils.formatDate(date);
        return DateFormatUtils.formatDate(date, "yyyy-MM") + "-01";
    }

    /**
     * 获指定月份最末天
     *
     * @param date date
     * @return string
     */
    public static @NotNull String getMonthLastDate(String date) {
        Date strDate = DateUtils.stringToDate(getMonthFirstDate(date));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(strDate);
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        return DateFormatUtils.formatDate(calendar.getTime());
    }

    /**
     * 所在星期头天
     *
     * @param date date
     * @return Date
     */
    public static @NotNull Date getWeekFirstDate(Date date) {
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        int today = now.get(Calendar.DAY_OF_WEEK);
        // 星期一
        int firstDayOfWeek = (now.get(Calendar.DATE) + 2 - today);
        now.set(Calendar.DATE, firstDayOfWeek);
        return now.getTime();
    }

    /**
     * 所在星期最末天
     *
     * @param date date
     * @return Date
     */
    public static @NotNull Date getWeekLastDate(Date date) {
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        int today = now.get(Calendar.DAY_OF_WEEK);
        // 星期一
        int firstDayOfWeek = (now.get(Calendar.DATE) + 2 - today);
        // 星期日
        int lastDayOfWeek = (firstDayOfWeek + 6);
        now.set(Calendar.DATE, lastDayOfWeek);
        return now.getTime();
    }

    /**
     * 较日期时间
     *
     * @param strPositive 主动日期时间
     * @param strNegative 被动日期时间
     * @param pattern     模式
     * @return 0 等、1 大、-1 小、-2 无效
     */
    public static int dateCompare(String strPositive, String strNegative, String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.US);
        Date datePositive = null;
        Date dateNegative = null;
        try {
            datePositive = simpleDateFormat.parse(strPositive);
            dateNegative = simpleDateFormat.parse(strNegative);
        } catch (ParseException e) {
            Timber.e(e);
        }
        if (null != datePositive) {
            return datePositive.compareTo(dateNegative);
        } else {
            return -2;
        }
    }

    /**
     * 当前年
     *
     * @return 当前年
     */
    public static int currentYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    /**
     * 当前月
     *
     * @return 当前月
     */
    public static int currentMonth() {
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }

    /**
     * 当前日
     * <p>
     * 安卓系统月份从 0-11 计算（故真实月份加 1）
     *
     * @return 当前日
     */
    public static int currentDay() {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 时间戳
     *
     * @return 时间戳
     */
    public static @NotNull String timeStamp() {
        return new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
    }

    /**
     * 差秒
     *
     * @param oldTime 旧时
     * @param newTime 新时
     * @return 差秒
     */
    public static long secondSeparate(String oldTime, String newTime) {
        long seconds = 0;
        // newTime 空默当前时
        if ((null == newTime) || newTime.isEmpty()) {
            newTime = getCurrentTimeYearMonthDayHourMinuteSecondOne();
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateFormatUtils.DATE_YEAR_MONTH_DATE_HOUR_MINUTE, Locale.US);
        try {
            long oldConversionResult = Objects.requireNonNull(simpleDateFormat.parse(oldTime), "must not be null").getTime();
            long newConversionResult = Objects.requireNonNull(simpleDateFormat.parse(newTime), "must not be null").getTime();
            long dc = Math.abs(newConversionResult - oldConversionResult);
            seconds = (dc / 1000);
        } catch (ParseException e) {
            Timber.e(e);
        }
        return seconds;
    }

    /**
     * 根据毫秒返回时分秒
     *
     * @param time 时间
     * @return 时间
     */
    public static @NotNull String getFormatHms(long time) {
        time = (time / 1000);
        int s = (int) (time % 60);
        int m = (int) (time / 60);
        int h = (int) (time / 3600);
        return String.format(Locale.CHINA, "%02d:%02d:%02d", h, m, s);
    }

    /**
     * 先后相差
     *
     * @param context   上下文
     * @param earlyTime 先时间
     * @param lateTime  后时间
     * @return 先后相差
     */
    public static @NotNull String earlyLateDiffer(@NotNull Context context, long earlyTime, long lateTime) {
        long between = (lateTime - earlyTime);
        long day = between / (24 * 60 * 60 * 1000);
        long hour = (between / (60 * 60 * 1000) - day * 24);
        long min = ((between / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long s = (between / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        long m = (between - day * 24 * 60 * 60 * 1000 - hour * 60 * 60 * 1000 - min * 60 * 1000 - s * 1000);
        BigDecimal second = new BigDecimal(s);
        BigDecimal millisecond = new BigDecimal(m);
        BigDecimal result = second.add(millisecond.divide(new BigDecimal(1000), RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP));
        return String.format(context.getApplicationContext().getString(R.string.formatSeconds), BigDecimalUtils.bigDecimalToString(result));
    }

    /**
     * 格式化时间
     *
     * @param num 时间毫秒
     * @return 格式化后 str
     */
    public static @NotNull String formatDate(Long num) {
        String tem;
        if (num > 0) {
            Long minute = (num / 60 / 1000);
            long remainder = (num % (60 * 1000));
            Long second = (remainder / 1000);
            tem = (addLeftZero(minute) + ":" + addLeftZero(second));
        } else {
            tem = "00:00";
        }
        return tem;
    }

    @Contract(pure = true)
    private static @NotNull String addLeftZero(Long tempNum) {
        String num;
        if (tempNum < UtilMagic.INT_TEN) {
            num = ("0" + tempNum);
        } else {
            num = ("" + tempNum);
        }
        return num;
    }

    /**
     * 分秒
     *
     * @param time 时间
     * @return 分秒
     */
    public static @NotNull String minuteSecond(int time) {
        int min = (time % 3600 / 60);
        int second = (time % 60);
        return String.format(Locale.CHINA, "%02d:%02d", min, second);
    }

    /**
     * 格式化 UTC
     *
     * @param l          long
     * @param strPattern String
     * @return String
     */
    public static String formatUtc(long l, String strPattern) {
        if (TextUtils.isEmpty(strPattern)) {
            strPattern = "yyyy-MM-dd HH:mm:ss";
        }
        if (null == simpleDateFormat) {
            try {
                simpleDateFormat = new SimpleDateFormat(strPattern, Locale.CHINA);
            } catch (Throwable e) {
                Timber.e(e);
            }
        } else {
            simpleDateFormat.applyPattern(strPattern);
        }
        return (null == simpleDateFormat) ? "null" : simpleDateFormat.format(l);
    }

    /**
     * 获取指定日期前日期
     *
     * @param daysBeforeCurrentDate 指定日期前天数
     * @return 指定日期前日期集合
     */
    public static @NotNull List<String> getDatesBeforeCurrentDate(int daysBeforeCurrentDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        Date date = null;
        try {
            date = simpleDateFormat.parse(getCurrentTimeYearMonthDay());
        } catch (ParseException e) {
            Timber.e(e);
        }
        Calendar calendar = Calendar.getInstance();
        if (null != date) {
            calendar.setTime(date);
        }
        List<String> datesBeforeCurrentDate = new ArrayList<>(daysBeforeCurrentDate + 1);
        datesBeforeCurrentDate.add(getCurrentTimeYearMonthDay());
        for (int i = 0; i < daysBeforeCurrentDate; i++) {
            calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 1);
            String value = simpleDateFormat.format(calendar.getTime());
            datesBeforeCurrentDate.add(value);
        }
        return datesBeforeCurrentDate;
    }

    /**
     * 年月日转化年月日
     *
     * @param originalDate 原始日期 格式：2021年4月4日
     * @return 转化后日期 2021-04-04
     */
    public static @NotNull String yearMonthDateConversionToYearMonthDate(@NotNull String originalDate) {
        String date = originalDate.replace("年", "-").replace("月", "-").replace("日", "");
        int index = date.indexOf("-");
        int lastIndex = date.lastIndexOf("-");
        String yearString = date.substring(0, index);
        String monthString = date.substring(index + 1, lastIndex);
        String dateString = date.substring(lastIndex + 1);
        StringBuilder stringBuilder = new StringBuilder(yearString);
        if (monthString.length() == 1) {
            stringBuilder.append("-").append("0").append(monthString);
        } else {
            stringBuilder.append("-").append(monthString);
        }
        if (dateString.length() == 1) {
            stringBuilder.append("-").append("0").append(dateString);
        } else {
            stringBuilder.append("-").append(dateString);
        }
        return stringBuilder.toString();
    }

    /**
     * 时分秒
     *
     * @param time 时间
     * @return 时分秒
     */
    private @NotNull String hourMinuteSecond(int time) {
        int hour = (time / 3600);
        int min = (time % 3600 / 60);
        int second = (time % 60);
        return String.format(Locale.CHINA, "%02d:%02d:%02d", hour, min, second);
    }
}