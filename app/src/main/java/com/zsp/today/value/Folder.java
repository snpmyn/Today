package com.zsp.today.value;

import android.os.Environment;

import com.zsp.today.application.App;

import java.io.File;

/**
 * Created on 2018/11/14.
 * <p>
 * 外部存储目录备份绝对路径
 * Environment.getExternalStorageDirectory().getAbsolutePath() + "/today/backup/";
 *
 * @author 郑少鹏
 * @desc 文件夹
 */
public class Folder {
    /**
     * 崩溃
     */
    public static String CRASH = getExternalFilesDir("today/crash").getAbsolutePath();
    /**
     * 账单
     */
    public static String ACCOUNT = getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
    /**
     * 内备份
     */
    public static String INTERNAL_BACKUP = getExternalFilesDir("today/backup").getAbsolutePath();
    /**
     * 外备份
     */
    public static String EXTERNAL_BACKUP = getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();

    /**
     * 获取外部文件目录
     *
     * @param type 类型
     * @return 文件
     */
    private static File getExternalFilesDir(String type) {
        return App.getAppInstance().getExternalFilesDir(type);
    }

    /**
     * 获取外部存储公共目录
     *
     * @param type 类型
     * @return 文件
     */
    private static File getExternalStoragePublicDirectory(String type) {
        return Environment.getExternalStoragePublicDirectory(type);
    }
}
