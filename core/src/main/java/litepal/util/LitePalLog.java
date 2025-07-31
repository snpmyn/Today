package litepal.util;

import util.log.LogUtils;

public final class LitePalLog {
    public static final int DEBUG = 2;
    public static final int ERROR = 5;
    public static int level = ERROR;

    public static void d(String tagName, String message) {
        if (level <= DEBUG) {
            LogUtils.d(tagName, message);
        }
    }

    public static void e(String tagName, Exception e) {
        if (level <= ERROR) {
            LogUtils.e(tagName, e.getMessage(), e);
        }
    }
}