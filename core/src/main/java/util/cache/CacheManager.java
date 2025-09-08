package util.cache;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;

import timber.log.Timber;

/**
 * 缓存管理器
 *
 * @author 郑少鹏
 * @date 2018/11/6
 */
public class CacheManager {
    public static final String STRING_ZERO_K = "0K";

    /**
     * 全缓存大小
     *
     * @param context 上下文
     * @return 全缓存大小
     */
    public static @NotNull String totalCacheSize(@NotNull Context context) {
        long cacheSize = folderSize(context.getApplicationContext().getCacheDir());
        if (TextUtils.equals(Environment.getExternalStorageState(), Environment.MEDIA_MOUNTED)) {
            cacheSize += folderSize(context.getApplicationContext().getExternalCacheDir());
        }
        return formatSize(cacheSize);
    }

    /**
     * 目录大小
     * <p>
     * Context.getCacheDir() 缓存
     * <p>
     * Context.getExternalFilesDir() 外部缓存
     * SDCard/Android/data/应用包名/files/ 目录（通放长存数据）
     * <p>
     * Context.getExternalCacheDir() 外部缓存
     * SDCard/Android/data/应用包名/cache/ 目录（通放临缓数据）
     *
     * @param file 文件
     * @return 目录大小
     */
    private static long folderSize(File file) {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            if (null != fileList) {
                for (File fileInner : fileList) {
                    // 下面还有文件
                    size = (fileInner.isDirectory() ? (size + folderSize(fileInner)) : (size + fileInner.length()));
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return size;
    }

    /**
     * 格式化大小
     *
     * @param size 大小
     * @return 格式化大小
     */
    private static @NotNull String formatSize(double size) {
        double kiloByte = (size / 1024);
        if (kiloByte < 1) {
            return "0K";
        }
        double megaByte = (kiloByte / 1024);
        if (megaByte < 1) {
            BigDecimal result1 = BigDecimal.valueOf(kiloByte);
            return result1.setScale(2, RoundingMode.HALF_UP).toPlainString() + "KB";
        }
        double gigaByte = (megaByte / 1024);
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(megaByte);
            return result2.setScale(2, RoundingMode.HALF_UP).toPlainString() + "MB";
        }
        double teraBytes = (gigaByte / 1024);
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(gigaByte);
            return result3.setScale(2, RoundingMode.HALF_UP).toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, RoundingMode.HALF_UP).toPlainString() + "TB";
    }

    /**
     * 清全缓存
     *
     * @param context 上下文
     */
    public static void clearAllCache(@NotNull Context context) {
        deleteFile(context.getApplicationContext().getCacheDir());
        if (TextUtils.equals(Environment.getExternalStorageState(), Environment.MEDIA_MOUNTED)) {
            deleteFile(context.getApplicationContext().getExternalCacheDir());
        }
    }

    /**
     * 删除文件
     *
     * @param file 文件
     * @return 删除成功
     */
    private static boolean deleteFile(File file) {
        if ((null != file) && file.isDirectory()) {
            String[] children = file.list();
            if (null != children) {
                for (String child : children) {
                    boolean success = deleteFile(new File(file, child));
                    if (!success) {
                        return false;
                    }
                }
            }
        }
        if (null != file) {
            return file.delete();
        } else {
            return false;
        }
    }
}