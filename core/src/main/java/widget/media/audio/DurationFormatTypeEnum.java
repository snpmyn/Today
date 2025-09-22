package widget.media.audio;

/**
 * @decs: 时长格式化类型枚举
 * @author: 郑少鹏
 * @date: 2025/9/21 22:27
 * @version: v 1.0
 */
public enum DurationFormatTypeEnum {
    /**
     * 00:00
     */
    MM_SS,
    /**
     * x 小时 y 分 z 秒
     * y 分 z 秒
     * z 秒
     */
    CHINESE,
    /**
     * 毫秒
     */
    MILLISECONDS;
}