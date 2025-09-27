package widget.quote;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @decs: 舒适短语提供者
 * @author: 郑少鹏
 * @date: 2025/9/21 23:19
 * @version: v 1.0
 */
public class ComfortQuoteProvider {
    private static final String[] WORD = {"慢慢来就好", "今天也不错", "一切都在变", "你已经努力", "休息一下吧", "明天再试", "事情会过去", "没什么大不了", "保持微笑吧", "别急着决定", "慢慢想清楚", "自己最重要", "平常心最好", "一切随缘吧", "日子会轻松", "小步也算进步", "不用太焦虑", "慢慢会好", "现在这样也好", "别苛责自己", "放轻松一点", "生活没那么快", "慢慢安排吧", "别给自己压力", "遇到困难正常", "先喘口气吧", "再继续努力", "你很努力了", "允许自己休息", "明天再开始", "事情不会太坏", "慢慢看就好", "别太紧张", "先做好眼前", "别想太远", "一步一步来", "今天先放下", "明天再想", "心会轻松些", "尝试就够了", "别苛求完美", "慢慢调整吧", "有问题很正常", "慢慢去解决", "没什么大不了", "休息也是收获", "不必总忙碌", "给自己空间", "生活本就平凡", "不必过分焦虑", "保持简单好", "别总看远方", "脚下很重要", "慢慢走就好", "小事不用太在意", "淡然就好", "生活会自然", "每天都是新开始", "慢慢整理心情", "保持温暖心", "慢慢会好", "别担心太多", "给自己时间", "轻松一点", "不必急着", "顺其自然吧", "今天也不错", "平凡就很好", "别太纠结", "慢慢适应吧", "一切都有节奏", "别急", "给自己空间", "让心轻松", "慢慢整理", "保持安静心", "不要太焦虑", "一切会好", "别苛责自己", "努力就好", "给自己肯定", "慢慢去完成", "不要急", "脚步稳一点", "小进步也好", "别太在意", "慢慢积累", "心情放轻松", "不要过分担心", "一切都好", "平静一点", "慢慢去想", "别慌", "每天都是新开始", "慢慢调整自己", "生活会好", "慢慢来吧", "别给自己压力", "一切顺其自然", "轻松一点", "慢慢走", "不用急", "脚步放慢", "心会舒服", "慢慢来", "别焦虑", "事情慢慢解决", "一切都有安排", "尝试就好", "不要太执着", "慢慢就好", "给自己时间", "慢慢去适应", "别太着急", "慢慢会明白", "别心急", "事情会顺利", "生活平淡也好", "慢慢享受", "不必焦虑", "脚下很重要", "慢慢走", "别看太远", "保持简单", "慢慢整理", "心会轻松", "每天都是新的", "慢慢适应", "别担心", "放轻松", "别太纠结", "一切会顺", "慢慢调整", "不要急", "一切自然", "给自己空间", "慢慢放松", "心会平静", "不要太焦虑", "慢慢会理解", "事情会好", "小事不必在意", "慢慢调整", "心会舒服", "慢慢积累", "一点一点来", "别着急", "每天都是新机会", "慢慢来", "生活会好", "轻松面对", "慢慢解决", "不要着急", "慢慢适应", "事情会好", "心会平静", "别太在意", "慢慢来", "一切自然", "慢慢整理", "心会安静", "事情会顺"};

    public static int size() {
        return WORD.length;
    }

    public static String getWordAt(int index) {
        if ((index < 0) || (index >= WORD.length)) {
            return "";
        }
        return WORD[index];
    }

    private static final List<Integer> threePool = new ArrayList<>();
    private static int threeIndex = 0;

    static {
        resetThreePool();
    }

    private static void resetThreePool() {
        threePool.clear();
        for (int i = 0; i < (WORD.length / 3); i++) {
            threePool.add(i);
        }
        Collections.shuffle(threePool);
        threeIndex = 0;
    }

    @NonNull
    @Contract(" -> new")
    public static String[] getNextThree() {
        if (threeIndex >= threePool.size()) {
            resetThreePool();
        }
        int idx = threePool.get(threeIndex++);
        int start = (idx * 3);
        return new String[]{getWordAt(start), getWordAt(start + 1), getWordAt(start + 2)};
    }

    private static final List<Integer> singlePool = new ArrayList<>();
    private static int singleIndex = 0;

    static {
        resetSinglePool();
    }

    private static void resetSinglePool() {
        singlePool.clear();
        for (int i = 0; i < WORD.length; i++) {
            singlePool.add(i);
        }
        Collections.shuffle(singlePool);
        singleIndex = 0;
    }

    public static String getNextSingle() {
        if (singleIndex >= singlePool.size()) {
            resetSinglePool();
        }
        return getWordAt(singlePool.get(singleIndex++));
    }
}