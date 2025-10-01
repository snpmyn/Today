package widget.quote;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import util.datetime.DateUtils;

/**
 * @decs: 每周短语配套原件
 * @author: 郑少鹏
 * @date: 2025/9/29 11:34
 * @version: v 1.0
 */
public class WeeklyQuoteKit {
    private static final Map<String, String[]> QUOTES = new HashMap<>();
    private static final Random RANDOM = new Random();

    static {
        // 道家
        QUOTES.put("taoism-星期一", new String[]{"无为而治 清净自在", "心静如水 淡泊明志", "事随心愿 步步从容", "顺其自然 笑看风云", "宁心寡欲 悠然自得", "安身立命 淡定从容", "心宽体健 清心寡欲", "静观万物 随心而行", "心安如水 平和自在", "悠然自得 随风而行"});
        QUOTES.put("taoism-星期二", new String[]{"福生无量 天长地久", "心安无忧 悠然自得", "宁静致远 心宽体健", "自在从容 淡然微笑", "顺心顺意 笑看红尘", "随遇而安 心境清明", "心平如镜 事随意转", "安然自在 清心寡欲", "悠然无碍 心静如水", "随心而行 淡泊名利"});
        QUOTES.put("taoism-星期三", new String[]{"居尘出尘 常乐我净", "心无挂碍 随遇而安", "清心寡欲 安心立命", "轻松自在 步履从容", "心宽体健 笑看风云", "宁静无为 心随意动", "悠然自得 随风而行", "平和淡定 心境如初", "心如明镜 处世自在", "安身立命 悠然无忧"});
        QUOTES.put("taoism-星期四", new String[]{"明心见性 得道逍遥", "心如明镜 自在安然", "守一静心 清净无为", "天地有序 心境如初", "心静如水 万事随缘", "逍遥自在 随心而行", "安然处世 悠然自得", "宁心静气 事随心转", "心宽体健 随遇而安", "悠然无忧 平和自在"});
        QUOTES.put("taoism-星期五", new String[]{"心无挂碍 自在从容", "安身立命 平和致远", "宁静致远 随心而行", "悠然自得 笑看云卷", "心宽如海 处世淡定", "随心随缘 万物皆安", "清心寡欲 心境平和", "安然自在 步履轻松", "平和淡定 悠然无碍", "心静如水 顺其自然"});
        QUOTES.put("taoism-星期六", new String[]{"吉祥康宁 安稳常乐", "万事顺心 平和自得", "心静如水 悠然无碍", "顺风而行 淡定随缘", "安然处世 笑看红尘", "悠然自在 心宽体健", "随心而行 清净无为", "心安如水 淡泊明志", "宁心静气 步履轻松", "平和从容 心境如初"});
        QUOTES.put("taoism-星期日", new String[]{"心逍遥 气长清", "安若素 淡泊自如", "悠然自在 清净安宁", "心宽似海 乐随风来", "心静无为 处世悠然", "宁静致远 随心而行", "平和自在 笑看云卷", "安身立命 随遇而安", "悠然无忧 心境平和", "心安如水 自在从容"});
        // 来财
        QUOTES.put("LaiCai-星期一", new String[]{"钱包鼓鼓 好运连连", "元气满满 收入翻倍", "笑口常开 钞票快来", "好运在线 红包不断", "余额暴涨 快乐常在", "开心赚钱 财运开挂", "钞票满仓 开心常伴", "福气冲天 收入翻番", "红包飞舞 幸运连击", "财运在线 笑容常在"});
        QUOTES.put("LaiCai-星期二", new String[]{"财气满满 幸福暴涨", "好运爆棚 收入节节", "钞票飞来 笑口常开", "红包不断 元气满格", "财富加速 快乐跟随", "开心加薪 好运到位", "余额上涨 福气满仓", "好运连击 红包满屏", "收获满满 笑声不断", "开心暴富 快乐升级"});
        QUOTES.put("LaiCai-星期三", new String[]{"好运爆棚 钱包鼓鼓", "福气满满 收入翻倍", "开心暴富 好运跟随", "红包开路 笑容常驻", "元气充电 财富拉满", "财运亨通 快乐加倍", "余额满格 笑声不断", "红包飞舞 好运在线", "财富冲浪 快乐常青", "开心到账 好运升级"});
        QUOTES.put("LaiCai-星期四", new String[]{"笑口常开 红包快到", "好运满满 财富跟随", "开心连连 收入不断", "福气爆棚 钱包鼓鼓", "余额满仓 好运升级", "财气冲天 快乐跟随", "红包开花 幸福常驻", "开心加薪 好运暴涨", "财富滚滚 笑容常开", "好运到账 快乐升级"});
        QUOTES.put("LaiCai-星期五", new String[]{"福气爆满 收入节节", "财运开挂 好运不断", "红包满仓 开心暴涨", "余额冲高 快乐到位", "财富拉满 幸运跟随", "开心暴富 收入翻倍", "福气到账 钞票开花", "好运升级 红包满屏", "开心连连 钱包不空", "财富常在 笑容常开"});
        QUOTES.put("LaiCai-星期六", new String[]{"好运不断 红包飞来", "余额满满 开心常在", "笑容暴涨 财富升级", "红包滚滚 福气连连", "开心到账 好运不散", "钞票开花 快乐满仓", "财气爆棚 幸运升级", "开心暴富 收入暴涨", "红包不断 笑声不断", "财富常驻 好运连击"});
        QUOTES.put("LaiCai-星期日", new String[]{"福气连连 财富翻倍", "开心到账 好运常驻", "笑容满满 钞票滚滚", "红包飞舞 幸运不断", "余额暴涨 快乐常在", "财运升级 好运开挂", "开心暴富 收入满仓", "红包开花 笑口常开", "财富常青 好运连连", "幸福到账 快乐翻倍"});
        // 佛家
        QUOTES.put("buddhism-星期一", new String[]{"佛法无边 慈悲无量", "诸行无常 心境自在", "一念清净 福慧增长", "放下执著 安然自在", "菩提常照 慈悲常随", "善心一念 光明无尽", "随缘自在 慧心圆融", "福寿绵长 慈悲常伴", "心境清净 智慧无边", "法音朗朗 普度众生"});
        QUOTES.put("buddhism-星期二", new String[]{"佛光普照 福泽无边", "因缘和合 慈悲常伴", "心安无碍 慧日当空", "诸恶莫作 众善奉行", "慧心清净 福寿绵长", "佛法广大 普度众生", "随缘自在 禅悦常随", "觉悟圆满 智慧无尽", "心如琉璃 慈悲长存", "福慧双修 安然自在"});
        QUOTES.put("buddhism-星期三", new String[]{"佛心广大 慈悲常住", "一念觉悟 解脱自在", "善缘常聚 福泽无量", "心如莲花 出淤泥净", "法力无边 慧光长明", "普度众生 慈悲无尽", "随缘自在 安住当下", "慧灯常明 福慧圆满", "清净无为 心境自在", "法喜充满 智慧无边"});
        QUOTES.put("buddhism-星期四", new String[]{"佛法无尽 慈悲常照", "心安如海 智慧无边", "慧光普照 福寿延绵", "随缘自在 无我无相", "菩萨护佑 福慧双修", "诸法无常 心境清净", "一念清明 万缘放下", "悲智双运 福慧增长", "法音常随 心安自在", "佛心广大 光明无尽"});
        QUOTES.put("buddhism-星期五", new String[]{"佛心常住 慈悲广大", "慧海无边 智慧常明", "随缘自在 安然清净", "善行无量 福慧长存", "佛光常照 心境自在", "一念清净 福报自来", "悲心常伴 福寿无量", "觉悟圆满 慧光无尽", "心无挂碍 禅悦随行", "法喜充满 福慧双修"});
        QUOTES.put("buddhism-星期六", new String[]{"佛法无边 普度众生", "智慧广大 慈悲无量", "随缘自在 心安无忧", "一念觉悟 当下解脱", "法音常随 慧心圆融", "福慧双修 快乐无忧", "慈悲常住 福寿绵延", "慧光常照 心境清净", "普度众生 福泽无边", "觉悟当下 智慧无边"});
        QUOTES.put("buddhism-星期日", new String[]{"佛光普照 慧日常明", "佛心广大 慈悲无边", "诸善圆满 福寿安康", "一念清净 心安自在", "慧灯长明 福慧圆满", "佛法无边 智慧无尽", "随缘自在 安然常乐", "普度众生 慈悲常伴", "福慧双修 慧心常明", "禅心清净 法喜无边"});
    }

    /**
     * 获取短语
     *
     * @param type 类型
     *             taoism 道家
     *             LaiCai 来财
     *             buddhism 佛家
     * @return 短语
     */
    public static String getQuote(String type) {
        String week = DateUtils.getCurrentWeek();
        String key = (type + "-" + week);
        String[] quotes = QUOTES.get(key);
        String defaultQuote = getDefaultQuote(type);
        return randomQuote(quotes, defaultQuote);
    }

    /**
     * 获取默认短语
     *
     * @param type 类型
     *             taoism 道家
     *             LaiCai 来财
     *             buddhism 佛家
     * @return 默认短语
     */
    @NonNull
    @Contract(pure = true)
    private static String getDefaultQuote(@NonNull String type) {
        switch (type) {
            case "taoism":
                return "安心当下 随缘自在";
            case "LaiCai":
                return "开心暴富 好运跟随";
            case "buddhism":
            default:
                return "诸行无常 心境自在";
        }
    }

    /**
     * 随机短语
     *
     * @param quotes       短语数组
     * @param defaultQuote 默认短语
     * @return 随机短语
     */
    private static String randomQuote(String[] quotes, String defaultQuote) {
        if ((null == quotes) || (quotes.length == 0)) {
            return defaultQuote;
        }
        int index = RANDOM.nextInt(quotes.length);
        return quotes[index];
    }
}