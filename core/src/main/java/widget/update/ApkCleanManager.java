package widget.update;

import android.content.Context;
import android.os.Environment;

import androidx.annotation.NonNull;

import java.io.File;

import timber.log.Timber;
import widget.media.MediaFileTypeEnum;

/**
 * Created on 2025/9/25.
 *
 * @author 郑少鹏
 * @desc APK 清理管理器
 * <p>
 * 结合 {@link ApkDownloadManager#startDownload(String, String, boolean)} 中目录设计
 */
public class ApkCleanManager {
    /**
     * TAG
     */
    private static final String TAG = ApkCleanManager.class.getSimpleName();

    /**
     * 执行
     *
     * @param context 上下文
     */
    public static void execute(@NonNull Context context) {
        // 内部 Download 目录
        File internalDownloadDirectory = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        delete(internalDownloadDirectory);
        // 外部公共 Download 目录
        File externalPublicDownloadDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        delete(externalPublicDownloadDirectory);
    }

    /**
     * 删除
     *
     * @param dir 目录
     */
    private static void delete(File dir) {
        if ((null != dir) && dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (null == files) {
                return;
            }
            for (File file : files) {
                try {
                    if (file.isFile() && file.getName().endsWith(MediaFileTypeEnum.APK.getSuffix())) {
                        boolean deleted = file.delete();
                        if (!deleted) {
                            Timber.i(TAG, "无法删除 APK 文件 %s", file.getAbsolutePath());
                        }
                    }
                } catch (Exception e) {
                    Timber.i(TAG, "删除 APK 文件异常 %s %s", file.getAbsolutePath(), e.getMessage());
                }
            }
        }
    }
}