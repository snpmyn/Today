package widget.crash;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.SystemClock;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import pool.application.BasePoolApp;
import timber.log.Timber;
import util.activity.ActivitySuperviseManager;
import util.file.FileUtils;

/**
 * Created on 2018/5/24.
 *
 * @author 郑少鹏
 * @desc 崩溃管理器初始化配置
 */
public class CrashManagerInitConfigure implements Thread.UncaughtExceptionHandler {
    /**
     * 实例
     */
    private static volatile CrashManagerInitConfigure instance;
    /**
     * Application
     */
    private final Application application;
    /**
     * 系统默 UncaughtException 处理类
     */
    private final Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
    /**
     * 存设备信息和异常信息
     */
    private final Map<String, String> information = new HashMap<>();
    /**
     * 格式化日期（日志文件名部分）
     */
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.CHINA);
    /**
     * 路径
     */
    private String path;

    /**
     * 仅一 CrashHandler 实例
     *
     * @param application Application
     */
    private CrashManagerInitConfigure(Application application) {
        this.application = application;
        this.uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * CrashHandler 实例
     *
     * @param application Application
     * @param path        路径
     */
    public static void getInstance(Application application, String path) {
        if (null == instance) {
            synchronized (CrashManagerInitConfigure.class) {
                if (null == instance) {
                    instance = new CrashManagerInitConfigure(application);
                    instance.path = path;
                }
            }
        }
    }

    /**
     * UncaughtException 时转该函数处理
     *
     * @param thread    线程
     * @param throwable 异常
     */
    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        Timber.e(throwable);
        if (!handleException(throwable) && (null != uncaughtExceptionHandler)) {
            // 程序没处理则系统默异常处理器处理
            uncaughtExceptionHandler.uncaughtException(thread, throwable);
        } else {
            SystemClock.sleep(200);
            // 退应用
            ActivitySuperviseManager.getInstance().appExit();
            // 杀已崩进程
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    /**
     * 自定错误处理
     * <p>
     * 收集错误信息、发送错误报告等均在此完成。
     *
     * @param throwable 异常
     * @return true 处理异常，不再上抛；false 不处理异常，存信息并交上层（系统异常处理器）处理
     */
    private boolean handleException(Throwable throwable) {
        if (null == throwable) {
            return false;
        }
        // 收集设备参数信息
        collectDeviceInfo(application);
        // 存错误信息至文件
        saveCrashInfoToFile(throwable);
        return true;
    }

    /**
     * 收设备参数信息
     *
     * @param application Application
     */
    private void collectDeviceInfo(@NotNull Application application) {
        try {
            PackageManager packageManager = application.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(application.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (null != packageInfo) {
                String versionName = ((null == packageInfo.versionName) ? "null" : packageInfo.versionName);
                String versionCode = packageInfo.versionCode + "";
                information.put("versionName", versionName);
                information.put("versionCode", versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Timber.e(e, "An error occurred when collecting package info.");
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                information.put(field.getName(), Objects.requireNonNull(field.get(null), "must not be null").toString());
            } catch (Exception e) {
                Timber.e(e, "An error occurred when collecting crash info.");
            }
        }
    }

    /**
     * 存错误信息至文件
     *
     * @param throwable 异常
     */
    private void saveCrashInfoToFile(Throwable throwable) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : information.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            stringBuilder.append(key).append("=").append(value).append("\n");
        }
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        throwable.printStackTrace(printWriter);
        Throwable t = throwable.getCause();
        while (null != t) {
            t.printStackTrace(printWriter);
            t = t.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        stringBuilder.append(result);
        try {
            String fileName = ("crash-" + dateFormat.format(new Date()) + ".log");
            FileUtils.saveStringAsFile(BasePoolApp.getBasePoolAppInstance(), stringBuilder.toString(), path, fileName);
        } catch (Exception e) {
            Timber.e(e);
        }
    }
}