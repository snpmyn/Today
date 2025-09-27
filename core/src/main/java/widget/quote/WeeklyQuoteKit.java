package widget.quote;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import util.datetime.DateUtils;

/**
 * Created on 2025/9/25.
 *
 * @author 郑少鹏
 * @desc 每周短语配套原件
 */
public class WeeklyQuoteKit {
    private static final Map<String, String[]> WEEK_QUOTES = new HashMap<>();
    private static final Random RANDOM = new Random();

    static {
        WEEK_QUOTES.put("星期一", new String[]{"无为而治 清净自在", "心静如水 淡泊明志", "事随心愿 步步从容", "顺其自然 笑看风云", "宁心寡欲 悠然自得", "安身立命 淡定从容", "心宽体健 清心寡欲", "静观万物 随心而行", "心安如水 平和自在", "悠然自得 随风而行"});
        WEEK_QUOTES.put("星期二", new String[]{"福生无量 天长地久", "心安无忧 悠然自得", "宁静致远 心宽体健", "自在从容 淡然微笑", "顺心顺意 笑看红尘", "随遇而安 心境清明", "心平如镜 事随意转", "安然自在 清心寡欲", "悠然无碍 心静如水", "随心而行 淡泊名利"});
        WEEK_QUOTES.put("星期三", new String[]{"居尘出尘 常乐我净", "心无挂碍 随遇而安", "清心寡欲 安心立命", "轻松自在 步履从容", "心宽体健 笑看风云", "宁静无为 心随意动", "悠然自得 随风而行", "平和淡定 心境如初", "心如明镜 处世自在", "安身立命 悠然无忧"});
        WEEK_QUOTES.put("星期四", new String[]{"明心见性 得道逍遥", "心如明镜 自在安然", "守一静心 清净无为", "天地有序 心境如初", "心静如水 万事随缘", "逍遥自在 随心而行", "安然处世 悠然自得", "宁心静气 事随心转", "心宽体健 随遇而安", "悠然无忧 平和自在"});
        WEEK_QUOTES.put("星期五", new String[]{"心无挂碍 自在从容", "安身立命 平和致远", "宁静致远 随心而行", "悠然自得 笑看云卷", "心宽如海 处世淡定", "随心随缘 万物皆安", "清心寡欲 心境平和", "安然自在 步履轻松", "平和淡定 悠然无碍", "心静如水 顺其自然"});
        WEEK_QUOTES.put("星期六", new String[]{"吉祥康宁 安稳常乐", "万事顺心 平和自得", "心静如水 悠然无碍", "顺风而行 淡定随缘", "安然处世 笑看红尘", "悠然自在 心宽体健", "随心而行 清净无为", "心安如水 淡泊明志", "宁心静气 步履轻松", "平和从容 心境如初"});
        WEEK_QUOTES.put("星期日", new String[]{"心逍遥 气长清", "安若素 淡泊自如", "悠然自在 清净安宁", "心宽似海 乐随风来", "心静无为 处世悠然", "宁静致远 随心而行", "平和自在 笑看云卷", "安身立命 随遇而安", "悠然无忧 心境平和", "心安如水 自在从容"});
    }

    /**
     * 获取今日短语
     *
     * @return 今日短语
     */
    public static String getTodayQuote() {
        String week = DateUtils.getCurrentWeek();
        String[] quotes = WEEK_QUOTES.get(week);
        if ((null == quotes) || (quotes.length == 0)) {
            return "安心当下 随缘自在";
        }
        int index = RANDOM.nextInt(quotes.length);
        return quotes[index];
    }
}