package widget.screenshot;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.NonNull;

import java.io.File;

import timber.log.Timber;
import widget.media.MediaFileDirectoryEnum;

/**
 * @decs: 截屏目录配套原件
 * @author: 郑少鹏
 * @date: 2025/9/25 21:07
 * @version: v 1.0
 */
public class ScreenshotDirectoryKit {
    /**
     * 获取可用截屏目录
     *
     * @param context 上下文
     * @return 媒体文件目录枚举
     */
    @NonNull
    public static MediaFileDirectoryEnum getAvailableScreenshotDirectory(@NonNull Context context) {
        MediaFileDirectoryEnum[] mediaFileDirectoryEnums = {MediaFileDirectoryEnum.DIRECTORY_PICTURES_SCREENSHOTS, MediaFileDirectoryEnum.DIRECTORY_DCIM_SCREENSHOTS};
        for (MediaFileDirectoryEnum mediaFileDirectoryEnum : mediaFileDirectoryEnums) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+
                // 通过 MediaStore 检查系统截屏目录是否存在
                if (areScreenshotDirectoryExist(context, mediaFileDirectoryEnum.getRelativePath())) {
                    return mediaFileDirectoryEnum;
                }
            } else {
                // Android 9-
                // 直接判断外部公共目录是否存在
                File dir = new File(Environment.getExternalStorageDirectory(), mediaFileDirectoryEnum.getRelativePath());
                if (dir.exists() && dir.isDirectory() && dir.canRead()) {
                    return mediaFileDirectoryEnum;
                }
            }
        }
        // 返第一候选
        return mediaFileDirectoryEnums[0];
    }

    /**
     * 截屏目录是否存在
     *
     * @param context      上下文
     * @param relativePath 相对路径
     * @return 截屏目录存在否
     */
    private static boolean areScreenshotDirectoryExist(@NonNull Context context, @NonNull String relativePath) {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String selection = (MediaStore.Images.Media.RELATIVE_PATH + " LIKE ?");
        String[] selectionArgs = new String[]{relativePath + "%"};
        try (Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.Media._ID}, selection, selectionArgs, null)) {
            return ((null != cursor) && (cursor.getCount() > 0));
        } catch (Exception e) {
            Timber.e(e);
            return false;
        }
    }
}