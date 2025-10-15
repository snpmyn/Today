package widget.memorial;

import androidx.annotation.NonNull;

import com.nlf.calendar.Lunar;
import com.nlf.calendar.Solar;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * @decs: 纪念配套组件
 * @author: 郑少鹏
 * @date: 2025/10/12 23:13
 * @version: v 1.0
 */
public class MemorialKit {
    /**
     * 纪念信息
     */
    public static class MemorialInfo {
        /**
         * 去世天数
         */
        public long daysSinceDeath;
        /**
         * 距下个纪念日天数
         */
        public long daysUntilAnniversary;
        /**
         * 是否是纪念日当天
         */
        public boolean areTodayAnniversary;
        /**
         * 第几个纪念日
         */
        public int anniversaryCount;
        /**
         * 类型
         * <p>
         * 农历
         * 阳历
         * 闰月
         */
        public String type;
        /**
         * 内容
         */
        public String message;
    }

    /**
     * 获取纪念信息
     *
     * @param year     年
     * @param month    月
     * @param day      日
     * @param areLunar 农历否
     * @return 纪念信息
     */
    @NonNull
    public static MemorialInfo getMemorialInfo(int year, int month, int day, boolean areLunar) {
        MemorialInfo memorialInfo = new MemorialInfo();
        Calendar calendar;
        try {
            if (areLunar) {
                // 判断是否是闰月
                // month 为负数表示闰月
                boolean areLeapMonth = (month < 0);
                int actualMonth = Math.abs(month);
                Lunar lunar = areLeapMonth ? Lunar.fromYmd(year, -actualMonth, day) : Lunar.fromYmd(year, actualMonth, day);
                Solar solar = lunar.getSolar();
                calendar = Calendar.getInstance();
                calendar.set(solar.getYear(), solar.getMonth() - 1, solar.getDay(), 0, 0, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                memorialInfo.type = areLeapMonth ? "闰月" : "农历";
            } else {
                calendar = Calendar.getInstance();
                calendar.set(year, month - 1, day, 0, 0, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                memorialInfo.type = "阳历";
            }
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);
            long diffMillis = (today.getTimeInMillis() - calendar.getTimeInMillis());
            memorialInfo.daysSinceDeath = TimeUnit.MILLISECONDS.toDays(diffMillis);
            int thisYear = today.get(Calendar.YEAR);
            Calendar thisYearAnniversary = Calendar.getInstance();
            thisYearAnniversary.set(thisYear, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
            thisYearAnniversary.set(Calendar.MILLISECOND, 0);
            Calendar nextAnniversary;
            if (today.before(thisYearAnniversary)) {
                nextAnniversary = thisYearAnniversary;
            } else {
                nextAnniversary = Calendar.getInstance();
                nextAnniversary.set(thisYear + 1, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
                nextAnniversary.set(Calendar.MILLISECOND, 0);
            }
            memorialInfo.daysUntilAnniversary = TimeUnit.MILLISECONDS.toDays(nextAnniversary.getTimeInMillis() - today.getTimeInMillis());
            memorialInfo.areTodayAnniversary = today.equals(thisYearAnniversary);
            memorialInfo.anniversaryCount = thisYear - calendar.get(Calendar.YEAR);
            memorialInfo.message = (memorialInfo.type + "\n" + ((memorialInfo.daysSinceDeath == 0) ? "今天" : (memorialInfo.daysSinceDeath > 0) ? ("第 " + memorialInfo.daysSinceDeath + " 天") : "余生未完") + "\n" + (memorialInfo.areTodayAnniversary ? ("今天是第 " + memorialInfo.anniversaryCount + " 个纪念日") : ("距下个纪念日 " + memorialInfo.daysUntilAnniversary + " 天")));
        } catch (Exception e) {
            memorialInfo.message = ("日期处理失败 " + e.getMessage());
            memorialInfo.type = "错误";
        }
        return memorialInfo;
    }

    @NonNull
    public static String getMemorialInfoWithLeap(int year, int month, int day, boolean areLunar) {
        if (!areLunar) {
            return getMemorialInfoText(year, month, day);
        }
        try {
            // 农历 → 阳历（非闰月）
            Lunar lunar = Lunar.fromYmd(year, month, day);
            Solar solar = lunar.getSolar();
            String info = getMemorialInfoText(solar.getYear(), solar.getMonth(), solar.getDay());
            // 尝试检查闰月
            // 传入月份前加负号创建成功说明存在闰月
            try {
                Lunar leapLunar = Lunar.fromYmd(year, -month, day);
                Solar leapSolar = leapLunar.getSolar();
                String leapInfo = getMemorialInfoText(leapSolar.getYear(), leapSolar.getMonth(), leapSolar.getDay());
                info += ("\n[闰月] " + leapInfo);
            } catch (Exception e) {
                // 无闰月
                // 忽略
            }
            return info;
        } catch (Exception e) {
            return ("日期转换失败 " + e.getMessage());
        }
    }

    /**
     * 获取纪念信息文本
     *
     * @param year  年
     * @param month 月
     * @param day   日
     * @return 纪念信息文本
     */
    @NonNull
    private static String getMemorialInfoText(int year, int month, int day) {
        Calendar deathDate = Calendar.getInstance();
        deathDate.set(year, month - 1, day, 0, 0, 0);
        deathDate.set(Calendar.MILLISECOND, 0);
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        long diffMillis = today.getTimeInMillis() - deathDate.getTimeInMillis();
        long daysSinceDeath = TimeUnit.MILLISECONDS.toDays(diffMillis);
        String daysText;
        if (daysSinceDeath == 0) {
            daysText = "今天";
        } else if (daysSinceDeath > 0) {
            daysText = ("第 " + daysSinceDeath + " 天");
        } else {
            daysText = "余生未完";
        }
        int thisYear = today.get(Calendar.YEAR);
        Calendar thisYearAnniversary = Calendar.getInstance();
        thisYearAnniversary.set(thisYear, month - 1, day, 0, 0, 0);
        thisYearAnniversary.set(Calendar.MILLISECOND, 0);
        Calendar nextAnniversary;
        if (today.before(thisYearAnniversary)) {
            nextAnniversary = thisYearAnniversary;
        } else {
            nextAnniversary = Calendar.getInstance();
            nextAnniversary.set(thisYear + 1, month - 1, day, 0, 0, 0);
            nextAnniversary.set(Calendar.MILLISECOND, 0);
        }
        long daysUntil = TimeUnit.MILLISECONDS.toDays(nextAnniversary.getTimeInMillis() - today.getTimeInMillis());
        String anniversaryText;
        if (today.equals(thisYearAnniversary)) {
            int yearsPassed = (thisYear - year);
            anniversaryText = ("今天是第 " + yearsPassed + " 个纪念日");
        } else {
            anniversaryText = ("距下个纪念日 " + daysUntil + " 天");
        }
        return (daysText + "\n" + anniversaryText);
    }
}