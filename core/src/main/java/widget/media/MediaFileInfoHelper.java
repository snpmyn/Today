package widget.media;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zsp.core.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import timber.log.Timber;
import widget.media.audio.DurationFormatTypeEnum;

/**
 * Created on 2025/9/18.
 *
 * @author 郑少鹏
 * @desc 媒体文件信息帮助者
 */
public class MediaFileInfoHelper {
    /**
     * 获取媒体文件大小
     *
     * @param size 大小
     *             单位字节
     * @return 媒体文件大小
     */
    @NonNull
    public static String getMediaFileSize(long size) {
        if (size <= 0) {
            return "0 B";
        }
        final String[] units = {"B", "KB", "MB", "GB", "TB", "PB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        // 防止数组越界
        digitGroups = Math.min(digitGroups, units.length - 1);
        double sizeAfterHandle = (size / Math.pow(1024, digitGroups));
        // 小于 1 MB 不显小数
        if (digitGroups < 2) {
            return String.format(Locale.getDefault(), "%d %s", Math.round(sizeAfterHandle), units[digitGroups]);
        } else {
            return String.format(Locale.getDefault(), "%.2f %s", sizeAfterHandle, units[digitGroups]);
        }
    }

    /**
     * 获取媒体文件时长
     *
     * @param context                上下文
     * @param uri                    统一资源标识符
     * @param duration               时长
     *                               单位毫秒
     *                               非空优先使用
     * @param durationFormatTypeEnum 时长格式化类型枚举
     * @return 媒体文件时长
     */
    @NonNull
    public static String getMediaFileDuration(@NonNull Context context, @Nullable Uri uri, @Nullable Long duration, @NonNull DurationFormatTypeEnum durationFormatTypeEnum) {
        long millisecond = 0;
        // 优先使用时长
        if (null != duration) {
            millisecond = duration;
        } else if (null != uri) {
            try (MediaMetadataRetriever mediaMetadataRetriever = new android.media.MediaMetadataRetriever()) {
                mediaMetadataRetriever.setDataSource(context, uri);
                String durationStr = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                if (null != durationStr) {
                    millisecond = Long.parseLong(durationStr);
                }
            } catch (Exception e) {
                Timber.e(e);
                millisecond = 0;
            }
        }
        if (millisecond <= 0) {
            return (durationFormatTypeEnum == DurationFormatTypeEnum.CHINESE) ? context.getString(R.string.zeroSecond) : "00:00";
        }
        long totalSeconds = (millisecond / 1000);
        long hours = (totalSeconds / 3600);
        long minutes = ((totalSeconds % 3600) / 60);
        long seconds = (totalSeconds % 60);
        if (durationFormatTypeEnum == DurationFormatTypeEnum.CHINESE) {
            if (hours > 0) {
                return String.format(Locale.CHINA, "%d 小时 %02d 分 %02d 秒", hours, minutes, seconds);
            } else if (minutes > 0) {
                return String.format(Locale.CHINA, "%d 分 %02d 秒", minutes, seconds);
            } else {
                return String.format(Locale.CHINA, "%d 秒", seconds);
            }
        } else {
            if (hours > 0) {
                return String.format(Locale.CHINA, "%02d:%02d:%02d", hours, minutes, seconds);
            } else {
                return String.format(Locale.CHINA, "%02d:%02d", minutes, seconds);
            }
        }
    }

    /**
     * 获取媒体文件中文日期时间成员
     *
     * @param context                  上下文
     * @param timestampOrUriOrFileName 时间戳或统一资源标识符或文件名
     *                                 统一资源标识符使用最后修改时间
     * @return 媒体文件中文日期时间成员
     */
    @NonNull
    public static String[] getMediaFileChineseDateTimeMembers(@NonNull Context context, @Nullable Object timestampOrUriOrFileName) {
        String full = getMediaFileChineseDateTime(context, timestampOrUriOrFileName);
        String unknown = context.getString(R.string.unknownDate);
        if (TextUtils.isEmpty(full.trim()) || unknown.equals(full.trim())) {
            return new String[]{unknown, ""};
        }
        // 只去首尾空白
        // 保留内部空格
        // e.g. "2025 年 09 月 18 日 15 时 24 分 26 秒"
        full = full.trim();
        // 以 '日' 为界切分
        int idx = full.indexOf('日');
        if (idx >= 0) {
            // e.g. "2025 年 09 月 18 日"
            String datePart = full.substring(0, idx + 1).trim();
            String timePart = "";
            if (idx + 1 < full.length()) {
                // e.g. "15 时 24 分 26 秒"
                timePart = full.substring(idx + 1).trim();
            }
            return new String[]{datePart, timePart};
        }
        // 兜底
        // 以第一个空格分割
        // 仍然保留内部空格
        int space = full.indexOf(' ');
        if (space > 0) {
            String datePart = full.substring(0, space).trim();
            String timePart = full.substring(space + 1).trim();
            return new String[]{datePart, timePart};
        }
        // 最后兜底
        // 整个字符串当作日期部分
        return new String[]{full, ""};
    }

    /**
     * 获取媒体文件中文日期时间
     *
     * @param context                  上下文
     * @param timestampOrUriOrFileName 时间戳或统一资源标识符或文件名
     *                                 统一资源标识符使用最后修改时间
     * @return 媒体文件中文日期时间
     */
    @NonNull
    public static String getMediaFileChineseDateTime(@NonNull Context context, @Nullable Object timestampOrUriOrFileName) {
        if (null == timestampOrUriOrFileName) {
            return context.getString(R.string.unknownDate);
        }
        // 1️⃣ 时间戳
        if (timestampOrUriOrFileName instanceof Long) {
            long timeMillis = (Long) timestampOrUriOrFileName;
            if (timeMillis <= 0) {
                return context.getString(R.string.unknownDate);
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy 年 MM 月 dd 日 HH 时 mm 分 ss 秒", Locale.CHINA);
            return simpleDateFormat.format(new Date(timeMillis));
        }
        // 2️⃣ 统一资源标识符
        if (timestampOrUriOrFileName instanceof Uri) {
            // 最后修改时间
            long lastModified = getMediaFileLastModified(context, (Uri) timestampOrUriOrFileName);
            if (lastModified > 0) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy 年 MM 月 dd 日 HH 时 mm 分 ss 秒", Locale.CHINA);
                return simpleDateFormat.format(new Date(lastModified));
            }
        }
        // 3️⃣ 文件名
        String fileName = null;
        if (timestampOrUriOrFileName instanceof String) {
            fileName = (String) timestampOrUriOrFileName;
        } else if (timestampOrUriOrFileName instanceof Uri) {
            Uri uri = (Uri) timestampOrUriOrFileName;
            if ("file".equals(uri.getScheme())) {
                fileName = new File(Objects.requireNonNull(uri.getPath())).getName();
            } else if ("content".equals(uri.getScheme())) {
                try (Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.MediaColumns.DISPLAY_NAME}, null, null, null)) {
                    if ((null != cursor) && cursor.moveToFirst()) {
                        fileName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME));
                    }
                } catch (Exception e) {
                    Timber.e(e);
                }
            }
        }
        if (null != fileName) {
            // 前缀后的第一个 '-'
            int start = fileName.indexOf('-');
            // 去掉后缀
            int end = fileName.lastIndexOf('.');
            if ((start >= 0) && (end > start)) {
                // 例如 2025-0918-15-24-26
                String datePart = fileName.substring(start + 1, end);
                try {
                    // 转 2025-0918-15-24-26 为 2025-09-18-15-24-26
                    datePart = datePart.replaceFirst("(\\d{4})-(\\d{2})(\\d{2})", "$1-$2-$3");
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.CHINA);
                    Date date = simpleDateFormat.parse(datePart);
                    if (null != date) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy 年 MM 月 dd 日 HH 时 mm 分 ss 秒", Locale.CHINA);
                        return dateFormat.format(date);
                    }
                } catch (Exception e) {
                    Timber.e(e);
                }
            }
        }
        return context.getString(R.string.unknownDate);
    }

    /**
     * 获取媒体文件最后修改时间
     *
     * @param context 上下文
     * @param uri     统一资源标识符
     * @return 媒体文件最后修改时间
     */
    public static long getMediaFileLastModified(@NonNull Context context, @Nullable Uri uri) {
        if (null == uri) {
            return 0;
        }
        long lastModified = 0;
        try {
            if ("file".equals(uri.getScheme())) {
                // 内部存储
                // file:// URI
                File file = new File(Objects.requireNonNull(uri.getPath()));
                if (file.exists()) {
                    lastModified = file.lastModified();
                }
            } else if ("content".equals(uri.getScheme())) {
                // 外部存储
                // content:// URI
                try (Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.MediaColumns.DATE_MODIFIED}, null, null, null)) {
                    if ((null != cursor) && cursor.moveToFirst()) {
                        int index = cursor.getColumnIndex(MediaStore.MediaColumns.DATE_MODIFIED);
                        if (index != -1) {
                            // 秒转毫秒
                            lastModified = cursor.getLong(index) * 1000L;
                        }
                    }
                }
                // fallback
                // 尝试通过文件描述符获取实际路径
                if (lastModified <= 0) {
                    try (ParcelFileDescriptor parcelFileDescriptor = context.getContentResolver().openFileDescriptor(uri, "r")) {
                        if (null != parcelFileDescriptor) {
                            File file = new File("/proc/self/fd/" + parcelFileDescriptor.getFd());
                            if (file.exists()) {
                                lastModified = file.lastModified();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return lastModified;
    }

    /**
     * 获取媒体文件信息
     *
     * @param context 上下文
     * @param source  资源
     *                支持文件
     *                支持统一资源标识符
     * @return 媒体文件信息
     */
    @NonNull
    public static MediaFileInfo getMediaFileInfo(@NonNull Context context, @NonNull Object source) {
        String name = "未知";
        String path = "";
        Uri uri = null;
        long size = 0;
        long duration = 0;
        long lastModified = 0;
        try {
            // 1️⃣ 处理 File 类型
            if (source instanceof File) {
                File file = (File) source;
                if (file.exists()) {
                    name = file.getName();
                    path = file.getAbsolutePath();
                    uri = Uri.fromFile(file);
                    size = file.length();
                    lastModified = file.lastModified();
                }
            } else if (source instanceof Uri) {
                // 2️⃣ 处理 Uri 类型
                uri = (Uri) source;
                if ("file".equals(uri.getScheme())) {
                    // file:// 协议
                    File file = new File(Objects.requireNonNull(uri.getPath()));
                    if (file.exists()) {
                        name = file.getName();
                        path = file.getAbsolutePath();
                        size = file.length();
                        lastModified = file.lastModified();
                    }
                } else if ("content".equals(uri.getScheme())) {
                    // content:// 协议
                    // 常见于 MediaStore 或 SAF
                    ContentResolver contentResolver = context.getContentResolver();
                    String[] projection = {MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.SIZE, MediaStore.MediaColumns.DATE_MODIFIED};
                    try (Cursor cursor = contentResolver.query(uri, projection, null, null, null)) {
                        if ((null != cursor) && cursor.moveToFirst()) {
                            int nameIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME);
                            int dataIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
                            int sizeIndex = cursor.getColumnIndex(MediaStore.MediaColumns.SIZE);
                            int dateIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DATE_MODIFIED);
                            if (nameIndex >= 0) {
                                name = cursor.getString(nameIndex);
                            }
                            if (dataIndex >= 0) {
                                path = cursor.getString(dataIndex);
                            }
                            if (sizeIndex >= 0) {
                                size = cursor.getLong(sizeIndex);
                            }
                            if (dateIndex >= 0) {
                                lastModified = cursor.getLong(dateIndex) * 1000;
                            }
                        }
                    } catch (Exception ignored) {

                    }
                    // 兜底获取大小
                    // 有些 Android 设备字段可能拿不到
                    // 大小为空时通过 ParcelFileDescriptor 获取
                    if (size <= 0) {
                        try (ParcelFileDescriptor parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r")) {
                            if (null != parcelFileDescriptor) {
                                size = parcelFileDescriptor.getStatSize();
                            }
                        } catch (Exception ignored) {
                        }
                    }
                    // 兜底获取最后修改时间
                    // 最后修改时间为空时通过 path 获取
                    if (lastModified <= 0) {
                        try {
                            if (!TextUtils.isEmpty(path)) {
                                File file = new File(path);
                                if (file.exists()) {
                                    lastModified = file.lastModified();
                                }
                            }
                        } catch (Exception ignored) {

                        }
                    }
                    // 兜底获取名称
                    // 名称为空时通过 path 获取
                    if (TextUtils.isEmpty(name) && !TextUtils.isEmpty(path)) {
                        name = new File(path).getName();
                    }
                }
            }
            // 3️⃣ 获取时长（音视频）
            if (null != uri) {
                try (MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever()) {
                    mediaMetadataRetriever.setDataSource(context, uri);
                    String dur = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                    if (!TextUtils.isEmpty(dur)) {
                        duration = Long.parseLong(dur);
                    }
                } catch (Exception ignored) {

                }
            }
        } catch (Exception ignored) {

        }
        return new MediaFileInfo(name, path, uri, size, duration, lastModified);
    }
}