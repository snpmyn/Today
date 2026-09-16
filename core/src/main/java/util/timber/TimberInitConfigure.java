package util.timber;

import androidx.annotation.NonNull;

import timber.log.Timber;

/**
 * Created on 2026/9/16.
 *
 * @author 郑少鹏
 * @desc Timber 初始化配置
 */
public class TimberInitConfigure {
    /**
     * 初始化 Timber
     *
     * @param debug 是否调试
     */
    public static void initTimber(boolean debug) {
        if (debug) {
            // 仅于 DEBUG 模式植入 Tree
            Timber.plant(new Timber.Tree() {
                @Override
                protected void log(int priority, String tag, @NonNull String message, Throwable t) {
                    // 传入 tag 为 null 时系统 Log 会报错或打印不规范
                    // 提供默认 Tag 保护
                    String finalTag = (tag != null) ? tag : "AppLog";
                    // 有异常则拼接 Throwable 堆栈信息
                    String finalMessage = message;
                    if (t != null) {
                        finalMessage += "\n" + android.util.Log.getStackTraceString(t);
                    }
                    android.util.Log.println(priority, finalTag, finalMessage);
                }
            });
        }
    }
}