package com.zsp.today.basic.value;

import android.os.Environment;

import com.zsp.today.application.App;

import java.io.File;

/**
 * Created on 2018/11/14.
 *
 * @author 郑少鹏
 * @desc 文件夹
 */
public class Folder {
    /**
     * Lottie 网络缓存
     */
    public static String LOTTIE_NETWORK_CACHE = "LottieNetworkCache";
    /**
     * 崩溃
     */
    public static String CRASH = getExternalPrivateDirectory("today/crash").getAbsolutePath();
    /**
     * 账单
     */
    public static String ACCOUNT = getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
    /**
     * 内备份
     * <p>
     * 为何 cache 下出现 backups
     * 路径包含 backup 时系统可能默认触发临时缓存机制 → cache/backups
     * 内部会尝试写到 cache 目录作临时备份后再复制到持久化目录
     * 此即 cache/backups 被创建原因
     * 目的避免写入文件失败导致数据丢失或作为缓存加速
     * 两者同时存在正常
     * <p>
     * 该机制主要在 release 版生效
     * 因系统认为 release 版更正式
     * 要保证数据不丢失
     * <p>
     * 而 debug 版通常 bypass 此类保护机制
     * 故不会在 cache 下创建 backups
     */
    public static String INTERNAL_BACKUP = getExternalPrivateDirectory("today/backup").getAbsolutePath();
    /**
     * 外备份
     */
    public static String EXTERNAL_BACKUP = getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();

    /**
     * 获取外部私有目录
     *
     * @param type 类型
     * @return 文件
     */
    private static File getExternalPrivateDirectory(String type) {
        return App.getAppInstance().getExternalFilesDir(type);
    }

    /**
     * 获取外部公共目录
     *
     * @param type 类型
     * @return 文件
     */
    private static File getExternalStoragePublicDirectory(String type) {
        return Environment.getExternalStoragePublicDirectory(type);
    }
}