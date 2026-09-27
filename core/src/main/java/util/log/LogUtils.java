package util.log;

import android.text.TextUtils;

import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.UnknownHostException;

import timber.log.Timber;

/**
 * Created on 2020-09-10
 *
 * @author zsp
 * @desc 日志工具类
 */
public class LogUtils {
    private static final int VERBOSE = 2;
    private static final int DEBUG = 3;
    private static final int INFO = 4;
    private static final int WARN = 5;
    private static final int ERROR = 6;
    private static Method methodDebug;
    private static Method methodError;
    private static Method methodInfo;
    private static Method methodVerbose;
    private static Method methodWarn;
    private static Method methodDebugWithThrowable;
    private static Method methodErrorWithThrowable;
    private static Method methodInfoWithThrowable;
    private static Method methodVerboseWithThrowable;
    private static Method methodWarnWithThrowable;
    private static Class<?> cls;

    static {
        try {
            cls = Class.forName("android.util.Log");
            methodDebug = cls.getDeclaredMethod("d", String.class, String.class);
            methodError = cls.getDeclaredMethod("e", String.class, String.class);
            methodInfo = cls.getDeclaredMethod("i", String.class, String.class);
            methodVerbose = cls.getDeclaredMethod("v", String.class, String.class);
            methodWarn = cls.getDeclaredMethod("w", String.class, String.class);
            methodDebugWithThrowable = cls.getDeclaredMethod("d", String.class, String.class, Throwable.class);
            methodErrorWithThrowable = cls.getDeclaredMethod("e", String.class, String.class, Throwable.class);
            methodInfoWithThrowable = cls.getDeclaredMethod("i", String.class, String.class, Throwable.class);
            methodVerboseWithThrowable = cls.getDeclaredMethod("v", String.class, String.class, Throwable.class);
            methodWarnWithThrowable = cls.getDeclaredMethod("w", String.class, String.class, Throwable.class);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            exception(e);
        }
        if (null == cls) {
            try {
                cls = LogUtils.class;
                methodDebug = methodError = methodInfo = methodVerbose = methodWarn = methodDebugWithThrowable = methodErrorWithThrowable = methodInfoWithThrowable = methodVerboseWithThrowable = methodWarnWithThrowable = cls.getDeclaredMethod("systemLog", int.class, String.class, String.class);
            } catch (NoSuchMethodException e) {
                systemLog(INFO, "e", e.getMessage());
            }
        }
    }

    public static void d(String tag, String info) {
        logInvoke(DEBUG, methodDebug, tag, info, null);
    }

    public static void e(String tag, String info) {
        logInvoke(ERROR, methodError, tag, info, null);
    }

    public static void i(String tag, String info) {
        logInvoke(INFO, methodInfo, tag, info, null);
    }

    public static void v(String tag, String info) {
        logInvoke(VERBOSE, methodVerbose, tag, info, null);
    }

    public static void w(String tag, String info) {
        logInvoke(WARN, methodWarn, tag, info, null);
    }

    public static void d(String tag, String info, Throwable e) {
        logInvoke(DEBUG, methodDebugWithThrowable, tag, info, e);
    }

    public static void e(String tag, String info, Throwable e) {
        logInvoke(ERROR, methodErrorWithThrowable, tag, info, e);
    }

    public static void i(String tag, String info, Throwable e) {
        logInvoke(INFO, methodInfoWithThrowable, tag, info, e);
    }

    public static void v(String tag, String info, Throwable e) {
        logInvoke(VERBOSE, methodVerboseWithThrowable, tag, info, e);
    }

    public static void w(String tag, String info, Throwable e) {
        logInvoke(WARN, methodWarnWithThrowable, tag, info, e);
    }

    public static void thread() {
        d("[Thread]: ", Thread.currentThread().getName());
    }

    public static void exception(Exception e) {
        if (Builder.logException) {
            Timber.e(e);
        }
    }

    private static boolean canLog() {
        return Builder.logEnable;
    }

    private static void logInvoke(int level, Method method, String tag, String info, Throwable throwable) {
        if (!canLog()) {
            return;
        }
        printInvokeStack();
        log(level, method, tag, info, throwable);
    }

    @SuppressWarnings("all")
    private static void systemLog(int level, String tag, String info) {
        String levelTag;
        switch (level) {
            case DEBUG:
                levelTag = "[DEBUG]: ";
                break;
            case INFO:
                levelTag = "[INFO]: ";
                break;
            case VERBOSE:
                levelTag = "[VERBOSE]: ";
                break;
            case ERROR:
                levelTag = "[ERROR]: ";
                break;
            case WARN:
                levelTag = "[WARN]: ";
                break;
            default:
                levelTag = "    ";
                break;
        }
        System.out.println(levelTag + "  [" + tag + "]  " + info);
    }

    private static void log(int level, Method method, String tag, String info, Throwable throwable) {
        if (null == info) {
            return;
        }
        if (!Builder.logSystem && null != cls && cls != LogUtils.class) {
            try {
                if ((method == methodDebugWithThrowable) || (method == methodInfoWithThrowable) || (method == methodErrorWithThrowable) || (method == methodWarnWithThrowable) || (method == methodVerboseWithThrowable)) {
                    if (null != method) {
                        method.invoke(cls, tag, info, throwable);
                    }
                } else {
                    if (null != method) {
                        method.invoke(cls, tag, (null == throwable) ? info : (info + "\n" + getStackTraceString(throwable)));
                    }
                }
                return;
            } catch (Exception e) {
                exception(e);
            }
        }
        systemLog(level, tag, (null == throwable) ? info : (info + "\n" + getStackTraceString(throwable)));
    }

    private static void printInvokeStack() {
        if (!Builder.logInvokeStack) {
            return;
        }
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        StringBuilder stringBuilder = new StringBuilder(256);
        StackTraceElement stackTraceElement;
        boolean start = false;
        String logUtilsClassName = LogUtils.class.getName();
        for (int index = 0; index < stackTraceElements.length; index++) {
            if (!start && (index < stackTraceElements.length - 1)) {
                boolean flag = TextUtils.equals(stackTraceElements[index].getClassName(), logUtilsClassName) && !TextUtils.equals(stackTraceElements[index + 1].getClassName(), logUtilsClassName);
                if (flag) {
                    start = true;
                }
            }
            if (start) {
                stackTraceElement = stackTraceElements[index];
                String fileName = (null != stackTraceElement.getFileName()) ? stackTraceElement.getFileName() : "Unknown Source";
                stringBuilder.append("    ").append(stackTraceElement.getClassName()).append(".").append(stackTraceElement.getMethodName()).append("(").append(fileName).append(":").append(stackTraceElement.getLineNumber()).append(")\n");
            }
        }
        log(INFO, methodInfo, "stack", stringBuilder.toString(), null);
    }

    private static @NotNull String getStackTraceString(Throwable e) {
        if (null == e) {
            return "";
        }
        // This is to reduce the amount of log spew that apps do in the non-error condition of the network being unavailable.
        Throwable throwable = e;
        while (null != throwable) {
            if (throwable instanceof UnknownHostException) {
                return "";
            }
            throwable = throwable.getCause();
        }
        StringWriter stringWriter = new StringWriter(256);
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        printWriter.flush();
        return stringWriter.toString();
    }

    public static class Builder {
        private static volatile boolean logEnable = false;
        private static volatile boolean logInvokeStack = false;
        private static volatile boolean logException = false;
        private static volatile boolean logSystem = false;

        public static void initConfiguration(boolean logEnable, boolean logInvokeStack, boolean logException, boolean logSystem) {
            Builder.logEnable = logEnable;
            Builder.logInvokeStack = logInvokeStack;
            Builder.logException = logException;
            Builder.logSystem = logSystem;
        }
    }
}