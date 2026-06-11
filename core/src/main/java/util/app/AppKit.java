package util.app;

import android.app.Application;
import android.content.Intent;

import androidx.annotation.NonNull;

import util.activity.ActivitySuperviseManager;

/**
 * Created on 2026/6/10.
 *
 * @author 郑少鹏
 * @desc APP 配套原件
 */
public class AppKit {
    /**
     * 杀死 APP
     */
    public static void killApp() {
        ActivitySuperviseManager.getInstance().finishAllActivity();
        // 杀死当前 Linux 进程
        // 当前进程中的 Application、Activity、Service 等都会结束
        // 可以杀死当前进程
        // 但不等于把 APP 从最近任务中移除
        // 也不等于一定会让下次启动走冷启动

        // 按 HOME（或者打开最近任务）看到的是任务（Task）
        // 不是进程
        // 即使 Activity 已经 finish
        // 进程已经被杀死
        // Android 仍然可能保留这个 Task 的缩略图
        // 方便用户再次点击恢复（系统会重新创建进程）
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * 启动 APP
     *
     * @param application Application
     * @param packageName 包名
     */
    public static void launchApp(@NonNull Application application, String packageName) {
        Intent intent = application.getPackageManager().getLaunchIntentForPackage(packageName);
        if (null != intent) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            application.startActivity(intent);
        }
    }
}