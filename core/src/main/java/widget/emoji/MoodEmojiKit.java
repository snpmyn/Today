package widget.emoji;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import util.list.ListUtils;

/**
 * @decs: 情绪表情配套原件
 * @author: 郑少鹏
 * @date: 2025/9/17 17:17
 * @version: v 1.0
 */
public class MoodEmojiKit {
    /**
     * 情绪枚举
     */
    public enum Mood {
        /**
         * 积极
         * <p>
         * 开心
         */
        HAPPY("😀", "开心", Category.POSITIVE),
        /**
         * 积极
         * <p>
         * 大笑
         */
        LAUGH("😂", "大笑", Category.POSITIVE),
        /**
         * 积极
         * <p>
         * 咧嘴笑
         */
        GRIN("😁", "咧嘴笑", Category.POSITIVE),
        /**
         * 积极
         * <p>
         * 酷
         */
        COOL("😎", "酷", Category.POSITIVE),
        /**
         * 积极
         * <p>
         * 爱意
         */
        LOVE("❤️", "爱意", Category.POSITIVE),
        /**
         * 积极
         * <p>
         * 热恋
         */
        IN_LOVE("😍", "热恋", Category.POSITIVE),
        /**
         * 积极
         * <p>
         * 亲吻
         */
        KISS("😘", "亲吻", Category.POSITIVE),
        /**
         * 积极
         * <p>
         * 激动
         */
        EXCITED("🤩", "激动", Category.POSITIVE),
        /**
         * 积极
         * <p>
         * 派对
         */
        PARTY("🥳", "派对", Category.POSITIVE),
        /**
         * 中性
         * <p>
         * 思考
         */
        THINKING("🤔", "思考", Category.NEUTRAL),
        /**
         * 中性
         * <p>
         * 一般
         */
        NEUTRAL("😐", "一般", Category.NEUTRAL),
        /**
         * 中性
         * <p>
         * 困惑
         */
        CONFUSED("😕", "困惑", Category.NEUTRAL),
        /**
         * 中性
         * <p>
         * 害羞
         */
        SHY("😊", "害羞", Category.NEUTRAL),
        /**
         * 中性
         * <p>
         * 释然
         */
        RELIEF("😌", "释然", Category.NEUTRAL),
        /**
         * 消极
         * <p>
         * 难过
         */
        SAD("😢", "难过", Category.NEGATIVE),
        /**
         * 消极
         * <p>
         * 忧伤
         */
        MELANCHOLY("😔", "忧伤", Category.NEGATIVE),
        /**
         * 消极
         * <p>
         * 失望
         */
        DISAPPOINTED("😞", "失望", Category.NEGATIVE),
        /**
         * 消极
         * <p>
         * 担忧
         */
        WORRIED("😟", "担忧", Category.NEGATIVE),
        /**
         * 消极
         * <p>
         * 大哭
         */
        CRY("😭", "大哭", Category.NEGATIVE),
        /**
         * 消极
         * <p>
         * 孤独
         */
        LONELY("🥀", "孤独", Category.NEGATIVE),
        /**
         * 消极
         * <p>
         * 疲惫
         */
        TIRED("😩", "疲惫", Category.NEGATIVE),
        /**
         * 消极
         * <p>
         * 困倦
         */
        SLEEPY("😴", "困倦", Category.NEGATIVE),
        /**
         * 愤怒恐惧
         * <p>
         * 生气
         */
        ANGRY("😡", "生气", Category.ANGRY_FEAR),
        /**
         * 愤怒恐惧
         * <p>
         * 不满
         */
        POUTING("😠", "不满", Category.ANGRY_FEAR),
        /**
         * 愤怒恐惧
         * <p>
         * 害怕
         */
        FEAR("😨", "害怕", Category.ANGRY_FEAR),
        /**
         * 愤怒恐惧
         * <p>
         * 震惊
         */
        SHOCKED("😱", "震惊", Category.ANGRY_FEAR),
        /**
         * 愤怒恐惧
         * <p>
         * 生病
         */
        SICK("🤒", "生病", Category.ANGRY_FEAR),
        /**
         * 愤怒恐惧
         * <p>
         * 恶心
         */
        NAUSEATED("🤢", "恶心", Category.ANGRY_FEAR),
        /**
         * 愤怒恐惧
         * <p>
         * 无奈
         */
        CONFUSED_TEAR("😓", "无奈", Category.ANGRY_FEAR),
        /**
         * 特殊
         * <p>
         * 惊讶
         */
        SURPRISED("😲", "惊讶", Category.SPECIAL),
        /**
         * 特殊
         * <p>
         * 冒汗
         */
        SWEAT("😅", "冒汗", Category.SPECIAL),
        /**
         * 特殊
         * <p>
         * 嘘
         */
        HUSH("🤫", "嘘", Category.SPECIAL),
        /**
         * 特殊
         * <p>
         * 尴尬
         */
        EMBARRASSED("😳", "尴尬", Category.SPECIAL),
        /**
         * 特殊
         * <p>
         * 震惊脑炸
         */
        MIND_BLOWN("🤯", "震惊脑炸", Category.SPECIAL),
        /**
         * 特殊
         * <p>
         * 流口水
         */
        HUNGRY("🤤", "流口水", Category.SPECIAL),
        /**
         * 特殊
         * <p>
         * 冷汗
         */
        COOL_SWEAT("😰", "冷汗", Category.SPECIAL),
        /**
         * 特殊
         * <p>
         * 痛苦
         */
        ANGUISH("😖", "痛苦", Category.SPECIAL),
        /**
         * 特殊
         * <p>
         * 晕
         */
        DIZZY("😵", "晕", Category.SPECIAL),
        /**
         * 特殊
         * <p>
         * 死亡
         */
        DEAD("💀", "死亡", Category.SPECIAL),
        /**
         * 特殊
         * <p>
         * 小丑
         */
        CLOWN("🤡", "小丑", Category.SPECIAL),
        /**
         * 特殊
         * <p>
         * 恶魔
         */
        DEVIL("😈", "恶魔", Category.SPECIAL),
        /**
         * 特殊
         * <p>
         * 天使
         */
        ANGEL("😇", "天使", Category.SPECIAL),
        /**
         * 天气
         * <p>
         * 晴天
         */
        SUNNY("☀️", "晴天", Category.WEATHER),
        /**
         * 天气
         * <p>
         * 多云
         */
        CLOUDY("☁️", "多云", Category.WEATHER),
        /**
         * 天气
         * <p>
         * 晴间多云
         */
        PARTLY_CLOUDY("⛅", "晴间多云", Category.WEATHER),
        /**
         * 天气
         * <p>
         * 雨天
         */
        RAINY("🌧️", "雨天", Category.WEATHER),
        /**
         * 天气
         * <p>
         * 雷雨
         */
        THUNDER("⛈️", "雷雨", Category.WEATHER),
        /**
         * 天气
         * <p>
         * 台风
         */
        TYPHOON("🌪️", "台风", Category.WEATHER),
        /**
         * 天气
         * <p>
         * 雪
         */
        SNOW("❄️", "雪", Category.WEATHER),
        /**
         * 天气
         * <p>
         * 大雪
         */
        SNOWY("🌨️", "大雪", Category.WEATHER),
        /**
         * 天气
         * <p>
         * 大风
         */
        WIND("💨", "大风", Category.WEATHER),
        /**
         * 天气
         * <p>
         * 彩虹
         */
        RAINBOW("🌈", "彩虹", Category.WEATHER),
        /**
         * 天气
         * <p>
         * 雾
         */
        FOG("🌫️", "雾", Category.WEATHER),
        /**
         * 天气
         * <p>
         * 雨伞
         */
        UMBRELLA("☔", "雨伞", Category.WEATHER),
        /**
         * 功能
         * <p>
         * 收藏
         */
        STAR("⭐", "收藏", Category.FUNCTION),
        /**
         * 功能
         * <p>
         * 文件夹
         */
        FOLDER("📂", "文件夹", Category.FUNCTION),
        /**
         * 功能
         * <p>
         * 热门
         */
        FIRE("🔥", "热门", Category.FUNCTION),
        /**
         * 功能
         * <p>
         * 音乐
         */
        MUSIC("🎵", "音乐", Category.FUNCTION),
        /**
         * 功能
         * <p>
         * 对勾
         */
        CHECK("✔️", "对勾", Category.FUNCTION),
        /**
         * 功能
         * <p>
         * 叉号
         */
        CROSS("❌", "叉号", Category.FUNCTION),
        /**
         * 功能
         * <p>
         * 时长
         */
        TIME("⏱️", "时长", Category.FUNCTION),
        /**
         * 功能
         * <p>
         * 闹钟
         */
        ALARM("⏰", "闹钟", Category.FUNCTION),
        /**
         * 功能
         * <p>
         * 沙漏
         */
        HOURGLASS("⌛", "沙漏", Category.FUNCTION),
        /**
         * 功能
         * <p>
         * 长度
         */
        RULER("📏", "长度", Category.FUNCTION),
        /**
         * 功能
         * <p>
         * 尺寸
         */
        SET_SQUARE("📐", "尺寸", Category.FUNCTION),
        /**
         * 功能
         * <p>
         * 压缩
         */
        CLAMP("🗜️", "压缩", Category.FUNCTION),
        /**
         * 功能
         * <p>
         * 日历
         */
        SPIRAL_CALENDAR("🗓️", "日历", Category.FUNCTION),
        /**
         * 功能
         * <p>
         * 大小
         */
        FILE_SIZE("📦", "大小", Category.FUNCTION),
        /**
         * 功能
         * <p>
         * 时钟
         */
        CLOCK("🕰️", "时钟", Category.FUNCTION);
        private final String emoji;
        private final String description;
        private final Category category;

        Mood(String emoji, String description, Category category) {
            this.emoji = emoji;
            this.description = description;
            this.category = category;
        }

        @NonNull
        public String getEmoji() {
            return emoji;
        }

        @NonNull
        public String getDescription() {
            return description;
        }

        @NonNull
        public String getDisplayText() {
            return emoji + " " + description;
        }

        @NonNull
        public Category getCategory() {
            return category;
        }
    }

    /**
     * 分类枚举
     */
    public enum Category {
        /**
         * 积极
         */
        POSITIVE,
        /**
         * 中性
         */
        NEUTRAL,
        /**
         * 消极
         */
        NEGATIVE,
        /**
         * 愤怒恐惧
         */
        ANGRY_FEAR,
        /**
         * 特殊
         */
        SPECIAL,
        /**
         * 天气
         */
        WEATHER,
        /**
         * 功能
         */
        FUNCTION
    }

    /**
     * 根据分类获取情绪列表
     *
     * @param category 分类
     * @return 情绪列表
     */
    @NonNull
    public static List<Mood> getMoodsByCategory(@NonNull Category category) {
        List<Mood> list = new ArrayList<>();
        for (Mood mood : EnumSet.allOf(Mood.class)) {
            if (mood.getCategory() == category) {
                list.add(mood);
            }
        }
        return list;
    }

    /**
     * 根据分类获取表情字符串列表
     *
     * @param category 分类
     * @return 表情字符串列表
     */
    @NonNull
    public static List<String> getEmojiStringByCategory(@NonNull Category category) {
        List<String> list = new ArrayList<>();
        for (Mood mood : getMoodsByCategory(category)) {
            list.add(mood.getEmoji());
        }
        return list;
    }

    /**
     * 根据分类获取展示文本列表
     *
     * @param category 分类
     * @return 展示文本列表
     */
    @NonNull
    public static List<String> getDisplayTextsByCategory(@NonNull Category category) {
        List<String> list = new ArrayList<>();
        for (Mood mood : getMoodsByCategory(category)) {
            list.add(mood.getDisplayText());
        }
        return list;
    }

    /**
     * 根据分类和分隔符获取表情字符串列表
     *
     * @param category  分类
     * @param separator 分隔符
     * @return 表情字符串列表
     */
    @NonNull
    public static String getEmojiStringByCategoryAndSeparator(@NonNull Category category, @NonNull String separator) {
        List<Mood> list = getMoodsByCategory(category);
        if (ListUtils.listIsEmpty(list)) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            stringBuilder.append(list.get(i).getEmoji());
            if (i < (list.size() - 1)) {
                stringBuilder.append(separator);
            }
        }
        return stringBuilder.toString();
    }
}