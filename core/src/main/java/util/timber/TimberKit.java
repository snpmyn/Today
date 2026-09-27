package util.timber;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

import timber.log.Timber;

/**
 * @desc: Timber 配套原件
 * @author: 郑少鹏
 * @date: 2026/9/24 18:31
 * @version: v 1.0
 */
public final class TimberKit {
    /**
     * 是否显示方法信息
     * <p>
     * 类名 / 方法名 / 行号
     */
    private static boolean showMethodInfo = false;

    /**
     * constructor
     * <p>
     * 私有构造函数 + 防止实例化
     */
    private TimberKit() {

    }

    /**
     * 设置是否显示方法信息
     * <p>
     * 类名 / 方法名 / 行号
     *
     * @param show 是否显示方法信息
     */
    public static void setShowMethodInfo(boolean show) {
        showMethodInfo = show;
    }

    /**
     * D 级别日志
     * <p>
     * {@code TimberKit.d("设备连接成功，IP: %s", "192.168.1.100");}
     *
     * @param message 日志消息内容
     *                支持 String.format 占位符 (如 %s, %d)
     * @param args    格式化参数列表
     */
    public static void d(String message, Object... args) {
        log(LogLevel.D, null, showMethodInfo, message, args);
    }

    /**
     * I 级别日志
     *
     * @param message 日志消息内容
     *                支持 String.format 占位符 (如 %s, %d)
     * @param args    格式化参数列表
     */
    public static void i(String message, Object... args) {
        log(LogLevel.I, null, showMethodInfo, message, args);
    }

    /**
     * W 级别日志
     *
     * @param message 日志消息内容
     *                支持 String.format 占位符 (如 %s, %d)
     * @param args    格式化参数列表
     */
    public static void w(String message, Object... args) {
        log(LogLevel.W, null, showMethodInfo, message, args);
    }

    /**
     * E 级别日志
     *
     * @param message 日志消息内容
     *                支持 String.format 占位符 (如 %s, %d)
     * @param args    格式化参数列表
     */
    public static void e(String message, Object... args) {
        log(LogLevel.E, null, showMethodInfo, message, args);
    }

    /**
     * V 级别日志
     *
     * @param message 日志消息内容
     *                支持 String.format 占位符 (如 %s, %d)
     * @param args    格式化参数列表
     */
    public static void v(String message, Object... args) {
        log(LogLevel.V, null, showMethodInfo, message, args);
    }

    /**
     * D 级别日志
     *
     * @param showMethodInfo 是否显示方法信息
     * @param message        日志消息内容
     *                       支持 String.format 占位符 (如 %s, %d)
     * @param args           格式化参数列表
     */
    public static void d(boolean showMethodInfo, String message, Object... args) {
        log(LogLevel.D, null, showMethodInfo, message, args);
    }

    /**
     * I 级别日志
     *
     * @param showMethodInfo 是否显示方法信息
     * @param message        日志消息内容
     *                       支持 String.format 占位符 (如 %s, %d)
     * @param args           格式化参数列表
     */
    public static void i(boolean showMethodInfo, String message, Object... args) {
        log(LogLevel.I, null, showMethodInfo, message, args);
    }

    /**
     * W 级别日志
     *
     * @param showMethodInfo 是否显示方法信息
     * @param message        日志消息内容
     *                       支持 String.format 占位符 (如 %s, %d)
     * @param args           格式化参数列表
     */
    public static void w(boolean showMethodInfo, String message, Object... args) {
        log(LogLevel.W, null, showMethodInfo, message, args);
    }

    /**
     * E 级别日志
     *
     * @param showMethodInfo 是否显示方法信息
     * @param message        日志消息内容
     *                       支持 String.format 占位符 (如 %s, %d)
     * @param args           格式化参数列表
     */
    public static void e(boolean showMethodInfo, String message, Object... args) {
        log(LogLevel.E, null, showMethodInfo, message, args);
    }

    /**
     * V 级别日志
     *
     * @param showMethodInfo 是否显示方法信息
     * @param message        日志消息内容
     *                       支持 String.format 占位符 (如 %s, %d)
     * @param args           格式化参数列表
     */
    public static void v(boolean showMethodInfo, String message, Object... args) {
        log(LogLevel.V, null, showMethodInfo, message, args);
    }

    /**
     * V 级别日志
     *
     * @param tag            标签
     * @param showMethodInfo 是否显示方法信息
     * @param message        日志消息内容
     *                       支持 String.format 占位符 (如 %s, %d)
     * @param args           格式化参数列表
     */
    public static void vTag(String tag, boolean showMethodInfo, String message, Object... args) {
        log(LogLevel.V, tag, showMethodInfo, message, args);
    }

    /**
     * D 级别日志
     *
     * @param tag            标签
     * @param showMethodInfo 是否显示方法信息
     * @param message        日志消息内容
     *                       支持 String.format 占位符 (如 %s, %d)
     * @param args           格式化参数列表
     */
    public static void dTag(String tag, boolean showMethodInfo, String message, Object... args) {
        log(LogLevel.D, tag, showMethodInfo, message, args);
    }

    /**
     * I 级别日志
     *
     * @param tag            标签
     * @param showMethodInfo 是否显示方法信息
     * @param message        日志消息内容
     *                       支持 String.format 占位符 (如 %s, %d)
     * @param args           格式化参数列表
     */
    public static void iTag(String tag, boolean showMethodInfo, String message, Object... args) {
        log(LogLevel.I, tag, showMethodInfo, message, args);
    }

    /**
     * W 级别日志
     *
     * @param tag            标签
     * @param showMethodInfo 是否显示方法信息
     * @param message        日志消息内容
     *                       支持 String.format 占位符 (如 %s, %d)
     * @param args           格式化参数列表
     */
    public static void wTag(String tag, boolean showMethodInfo, String message, Object... args) {
        log(LogLevel.W, tag, showMethodInfo, message, args);
    }

    /**
     * E 级别日志
     *
     * @param tag            标签
     * @param showMethodInfo 是否显示方法信息
     * @param message        日志消息内容
     *                       支持 String.format 占位符 (如 %s, %d)
     * @param args           格式化参数列表
     */
    public static void eTag(String tag, boolean showMethodInfo, String message, Object... args) {
        log(LogLevel.E, tag, showMethodInfo, message, args);
    }

    /**
     * 打印异常日志
     *
     * @param t 异常对象
     */
    public static void e(Throwable t) {
        e(t, showMethodInfo, null);
    }

    /**
     * 打印异常日志
     *
     * @param t              异常对象
     * @param showMethodInfo 是否显示方法信息
     * @param message        日志消息内容
     *                       支持 String.format 占位符 (如 %s, %d)
     * @param args           格式化参数列表
     */
    public static void e(Throwable t, boolean showMethodInfo, String message, Object... args) {
        String finalMsg = buildMessage(showMethodInfo, message, args);
        if (TextUtils.isEmpty(finalMsg)) {
            Timber.e(t);
        } else {
            Timber.e(t, "%s", finalMsg);
        }
    }

    /**
     * 格式化 JSON 字符串
     *
     * @param json JSON 字符串
     */
    public static void json(String json) {
        json(null, showMethodInfo, json);
    }

    /**
     * 格式化 JSON 字符串
     *
     * @param tag            标签
     * @param showMethodInfo 是否显示方法信息
     * @param json           JSON 字符串
     */
    public static void json(String tag, boolean showMethodInfo, String json) {
        if (TextUtils.isEmpty(json)) {
            log(LogLevel.D, tag, showMethodInfo, "empty or null JSON string");
            return;
        }
        try {
            String trimmed = json.trim();
            String formatted;
            if (trimmed.startsWith("{")) {
                formatted = ("\n" + new JSONObject(trimmed).toString(2));
            } else if (trimmed.startsWith("[")) {
                formatted = ("\n" + new JSONArray(trimmed).toString(2));
            } else {
                formatted = trimmed;
            }
            log(LogLevel.D, tag, showMethodInfo, formatted);
        } catch (Exception e) {
            e(e, showMethodInfo, "JSON format error");
        }
    }

    /**
     * 日志输出
     *
     * @param logLevel       日志级别
     * @param tag            标签
     * @param showMethodInfo 是否显示方法信息
     * @param message        日志消息内容
     *                       支持 String.format 占位符 (如 %s, %d)
     * @param args           格式化参数列表
     */
    private static void log(@NonNull LogLevel logLevel, String tag, boolean showMethodInfo, String message, Object... args) {
        String msg = buildMessage(showMethodInfo, message, args);
        Timber.Tree tree = TextUtils.isEmpty(tag) ? Timber.asTree() : Timber.tag(tag);
        switch (logLevel) {
            case V:
                tree.v(msg);
                break;
            case D:
                tree.d(msg);
                break;
            case I:
                tree.i(msg);
                break;
            case W:
                tree.w(msg);
                break;
            case E:
                tree.e(msg);
                break;
        }
    }

    /**
     * 构建信息
     *
     * @param showMethodInfo 是否显示方法信息
     * @param message        日志消息内容
     *                       支持 String.format 占位符 (如 %s, %d)
     * @param args           格式化参数列表
     * @return 动态拼接后的方法信息与原始消息
     */
    @SuppressLint("DefaultLocale")
    @NonNull
    private static String buildMessage(boolean showMethodInfo, String message, Object... args) {
        String formattedMsg = ((args != null) && (args.length > 0) && !TextUtils.isEmpty(message)) ? String.format(message, args) : ((message == null) ? "" : message);
        if (!showMethodInfo) {
            return formattedMsg;
        }
        // 获取外部调用者的 StackTraceElement
        StackTraceElement caller = getStackTraceElement();
        if (caller == null) {
            return formattedMsg;
        }
        // 提取简短类名
        String className = caller.getClassName();
        className = className.substring(className.lastIndexOf('.') + 1);
        // 类名包含内部类或匿名类符号 $ 时只取主类名
        if (className.contains("$")) {
            className = className.split("\\$")[0];
        }
        // 格式
        // [ClassName.methodName:LineNumber] 原始消息
        return String.format("[%s.%s:%d] %s", className, caller.getMethodName(), caller.getLineNumber(), formattedMsg);
    }

    /**
     * 获取栈帧元素
     * <p>
     * 准确向上检索，跳过 TimberKit 自身所有栈帧，精准拿到业务调用方栈帧。
     *
     * @return 栈帧元素
     */
    @Nullable
    private static StackTraceElement getStackTraceElement() {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        boolean foundTimberKit = false;
        for (StackTraceElement element : stackTraceElements) {
            String className = element.getClassName();
            if (className.equals(TimberKit.class.getName())) {
                foundTimberKit = true;
            } else if (foundTimberKit) {
                // 找到紧跟 TimberKit 后面第一个非 TimberKit 栈帧
                return element;
            }
        }
        return null;
    }

    /**
     * 日志级别
     */
    private enum LogLevel {V, D, I, W, E}
}