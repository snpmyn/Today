package widget.media;

import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import timber.log.Timber;

/**
 * @decs: 媒体文件配套原件
 * @author: 郑少鹏
 * @date: 2025/9/21 21:57
 * @version: v 1.0
 */
public class MediaFileKit {
    /**
     * 最大缓存大小
     */
    private static final int MAX_CACHE_SIZE = 200;
    /**
     * 线程池
     */
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(2);
    /**
     * 时长缓存实体集
     */
    private static final Map<String, DurationCacheEntry> DURATION_CACHE_ENTRY_MAP = Collections.synchronizedMap(new LinkedHashMap<>(MAX_CACHE_SIZE, 0.75F, true) {
        @Override
        protected boolean removeEldestEntry(Entry<String, DurationCacheEntry> eldest) {
            return (size() > MAX_CACHE_SIZE);
        }
    });

    /**
     * 时长缓存实体
     */
    private static class DurationCacheEntry {
        /**
         * 时长
         */
        long duration;
        /**
         * 最后修改时间
         */
        long lastModified;
    }

    /**
     * 文件返回模式枚举
     */
    public enum FileReturnModeEnum {
        /**
         * 全部
         */
        ALL,
        /**
         * 最大
         */
        LARGEST,
        /**
         * 最小
         */
        SMALLEST,
        /**
         * 最近
         */
        LATEST,
        /**
         * 前 N
         */
        TOP_N
    }

    /**
     * 时长加载监听
     */
    public interface OnDurationLoadListener {
        /**
         * 单时长加载
         *
         * @param mediaFileInfo 媒体文件信息
         * @param position      位置
         */
        void onSingleDurationLoad(@NonNull MediaFileInfo mediaFileInfo, int position);

        /**
         * 全部时长加载
         *
         * @param mediaFileInfos 媒体文件信息集
         */
        void onAllDurationLoad(@NonNull List<MediaFileInfo> mediaFileInfos);
    }

    /**
     * 异步获取文件
     *
     * @param context                上下文
     * @param fileNamePrefix         文件名前缀
     * @param mediaFileTypeEnum      媒体文件类型枚举
     * @param mediaFileDirectoryEnum 媒体文件目录枚举
     * @param onlyToday              仅今日
     * @param fileReturnModeEnum     文件返回模式枚举
     * @param topN                   前 N
     * @param onDurationLoadListener 时长加载监听
     */
    public static void getFileAsync(@NonNull Context context, @Nullable String fileNamePrefix, @Nullable MediaFileTypeEnum mediaFileTypeEnum, @NonNull MediaFileDirectoryEnum mediaFileDirectoryEnum, boolean onlyToday, @NonNull FileReturnModeEnum fileReturnModeEnum, int topN, @NonNull OnDurationLoadListener onDurationLoadListener) {
        List<MediaFileInfo> mediaFileInfos = queryFilesByFileApi(context, fileNamePrefix, mediaFileTypeEnum, mediaFileDirectoryEnum, onDurationLoadListener);
        if (onlyToday) {
            long start = getTodayStartMillis();
            long end = getTodayEndMillis();
            mediaFileInfos.removeIf(f -> (f.systemOrMediaStoreRecordTime < start) || (f.systemOrMediaStoreRecordTime > end));
        }
        List<MediaFileInfo> resultList = new ArrayList<>();
        switch (fileReturnModeEnum) {
            // 全部
            case ALL:
                resultList = mediaFileInfos;
                break;
            // 最大
            case LARGEST:
                if (!mediaFileInfos.isEmpty()) {
                    MediaFileInfo largest = Collections.max(mediaFileInfos, Comparator.comparingLong(f -> f.size));
                    resultList.add(largest);
                }
                break;
            // 最小
            case SMALLEST:
                if (!mediaFileInfos.isEmpty()) {
                    MediaFileInfo smallest = Collections.min(mediaFileInfos, Comparator.comparingLong(f -> f.size));
                    resultList.add(smallest);
                }
                break;
            // 最近
            case LATEST:
                if (!mediaFileInfos.isEmpty()) {
                    MediaFileInfo latest = Collections.max(mediaFileInfos, Comparator.comparingLong(f -> f.systemOrMediaStoreRecordTime));
                    resultList.add(latest);
                }
                break;
            // 前 N
            case TOP_N:
                mediaFileInfos.sort((a, b) -> Long.compare(b.systemOrMediaStoreRecordTime, a.systemOrMediaStoreRecordTime));
                for (int i = 0; i < Math.min(topN, mediaFileInfos.size()); i++) {
                    resultList.add(mediaFileInfos.get(i));
                }
                break;
        }
        onDurationLoadListener.onAllDurationLoad(resultList);
    }

    /**
     * 通过文件 API 查询文件集
     *
     * @param context                上下文
     * @param fileNamePrefix         文件名前缀
     * @param mediaFileTypeEnum      媒体文件类型枚举
     * @param mediaFileDirectoryEnum 媒体文件目录枚举
     * @param onDurationLoadListener 时长加载监听
     * @return 文件集
     */
    @NonNull
    private static List<MediaFileInfo> queryFilesByFileApi(@NonNull Context context, @Nullable String fileNamePrefix, @Nullable MediaFileTypeEnum mediaFileTypeEnum, @NonNull MediaFileDirectoryEnum mediaFileDirectoryEnum, @Nullable OnDurationLoadListener onDurationLoadListener) {
        List<MediaFileInfo> mediaFileInfos = new ArrayList<>();
        File baseDir;
        if (mediaFileDirectoryEnum.areInternal()) {
            // 内部
            baseDir = context.getFilesDir();
        } else if (mediaFileDirectoryEnum.areExternalPrivate()) {
            // 外部私有
            baseDir = context.getExternalFilesDir(null);
        } else if (mediaFileDirectoryEnum.areExternalPublic()) {
            // 外部公共
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+ 仍用 Environment.getExternalStorageDirectory()
                // 注意权限
                baseDir = new File(Environment.getExternalStorageDirectory(), mediaFileDirectoryEnum.getRelativePath());
            } else {
                baseDir = Environment.getExternalStoragePublicDirectory(mediaFileDirectoryEnum.getRelativePath());
            }
        } else {
            // fallback
            baseDir = context.getFilesDir();
        }
        if (null == baseDir) {
            return mediaFileInfos;
        }
        if (!baseDir.exists() || !baseDir.isDirectory()) {
            return mediaFileInfos;
        }
        File[] files = baseDir.listFiles();
        if (null == files) {
            return mediaFileInfos;
        }
        boolean needsDuration = (null != mediaFileTypeEnum) && mediaFileTypeEnum.needDuration();
        for (File file : files) {
            if (!file.isFile()) {
                continue;
            }
            String name = file.getName();
            if (!TextUtils.isEmpty(fileNamePrefix) && !name.startsWith(fileNamePrefix)) {
                continue;
            }
            if ((null != mediaFileTypeEnum) && !TextUtils.isEmpty(mediaFileTypeEnum.getSuffix()) && !name.endsWith(mediaFileTypeEnum.getSuffix())) {
                continue;
            }
            // 统一时间字段
            long recordTime = file.lastModified();
            MediaFileInfo mediaFileInfo = new MediaFileInfo(name, file.getAbsolutePath(), Uri.fromFile(file), file.length(), 0L, recordTime);
            mediaFileInfos.add(mediaFileInfo);
            if (needsDuration && (null != onDurationLoadListener)) {
                final int position = (mediaFileInfos.size() - 1);
                EXECUTOR_SERVICE.execute(() -> {
                    long cachedDuration = getCacheDuration(file);
                    mediaFileInfo.setDuration(cachedDuration);
                    onDurationLoadListener.onSingleDurationLoad(mediaFileInfo, position);
                });
            }
        }
        return mediaFileInfos;
    }

    /**
     * 获取缓存时长
     *
     * @param file 文件
     * @return 缓存时长
     */
    private static long getCacheDuration(@NonNull File file) {
        String path = file.getAbsolutePath();
        long lastModified = file.lastModified();
        DurationCacheEntry cacheEntry = DURATION_CACHE_ENTRY_MAP.get(path);
        if ((null != cacheEntry) && (cacheEntry.lastModified == lastModified)) {
            return cacheEntry.duration;
        }
        long duration = readDuration(file);
        DurationCacheEntry durationCacheEntry = new DurationCacheEntry();
        durationCacheEntry.duration = duration;
        durationCacheEntry.lastModified = lastModified;
        DURATION_CACHE_ENTRY_MAP.put(path, durationCacheEntry);
        return duration;
    }

    /**
     * 读取时长
     *
     * @param file 文件
     * @return 时长
     */
    private static long readDuration(@NonNull File file) {
        try (MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever()) {
            mediaMetadataRetriever.setDataSource(file.getAbsolutePath());
            String durationStr = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            return (null != durationStr) ? Long.parseLong(durationStr) : 0L;
        } catch (Exception e) {
            Timber.w(e, "fail to retrieve duration: %s", file.getAbsolutePath());
            return 0L;
        }
    }

    /**
     * 获取今日 00:00:00 时间戳
     *
     * @return 今日 00:00:00 时间戳
     */
    private static long getTodayStartMillis() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * 获取今日 23:59:59 时间戳
     *
     * @return 今日 23:59:59 时间戳
     */
    private static long getTodayEndMillis() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }

    /**
     * 删除
     *
     * @param context              上下文
     * @param source               资源
     *                             文件
     *                             统一资源标识符
     * @param onFileDeleteListener 文件删除监听
     * @return 删除成功否
     */
    public static boolean delete(@NonNull Context context, @NonNull Object source, @Nullable OnFileDeleteListener onFileDeleteListener) {
        MediaFileInfo mediaFileInfo = resolveMediaFileInfo(context, source);
        return delete(context, mediaFileInfo, onFileDeleteListener);
    }

    /**
     * 删除
     *
     * @param context              上下文
     * @param mediaFileInfo        媒体文件信息
     * @param onFileDeleteListener 文件删除监听
     * @return 删除成功否
     */
    private static boolean delete(@NonNull Context context, @NonNull MediaFileInfo mediaFileInfo, @Nullable OnFileDeleteListener onFileDeleteListener) {
        boolean deleted = false;
        try {
            // 1️⃣ 直接删除
            // 文件
            File file = !TextUtils.isEmpty(mediaFileInfo.getPath()) ? new File(mediaFileInfo.getPath()) : null;
            if ((null != file) && file.exists()) {
                deleted = file.delete();
            }
            // 2️⃣ ContentResolver 删除
            // content:// URI
            if (!deleted && (null != mediaFileInfo.getUri())) {
                try {
                    ContentResolver contentResolver = context.getContentResolver();
                    int rows = contentResolver.delete(mediaFileInfo.getUri(), null, null);
                    deleted = (rows > 0);
                } catch (Exception e) {
                    Timber.w(e, "ContentResolver 删除失败: %s", mediaFileInfo.getPath());
                }
            }
            // 3️⃣ Android 11+ 外部公共目录删除
            // PendingIntent
            if (!deleted && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) && (null != mediaFileInfo.getUri())) {
                try {
                    // PendingIntent 弹窗用户确认
                    PendingIntent pendingIntent = MediaStore.createDeleteRequest(context.getContentResolver(), Collections.singletonList(mediaFileInfo.getUri()));
                    context.startIntentSender(pendingIntent.getIntentSender(), null, 0, 0, 0);
                    // 返回 false
                    return false;
                } catch (Exception e) {
                    Timber.e(e, "Android 11+ 删除失败: %s", mediaFileInfo.getPath());
                    return false;
                }
            }
            // 4️⃣ 回调删除成功
            if (deleted && (null != onFileDeleteListener)) {
                String pathOrUri = !TextUtils.isEmpty(mediaFileInfo.getPath()) ? mediaFileInfo.getPath() : (null != mediaFileInfo.getUri()) ? mediaFileInfo.getUri().toString() : "";
                onFileDeleteListener.onFileDelete(pathOrUri, mediaFileInfo.getUri());
            }
        } catch (Exception e) {
            Timber.e(e, "删除文件异常: %s", mediaFileInfo.getPath());
        }
        return deleted;
    }

    /**
     * 文件删除监听
     */
    public interface OnFileDeleteListener {
        /**
         * 文件删除
         *
         * @param absolutePathOrUri 绝对路径或统一资源标识符
         * @param mediaStoreUri     媒体存储统一资源标识符
         */
        void onFileDelete(@NonNull String absolutePathOrUri, @Nullable Uri mediaStoreUri);
    }

    /**
     * 解决媒体文件信息
     * <p>
     * 转 File 或 Uri 为 MediaFileInfo
     * 确保 content:// URI 可用
     *
     * @param context 上下文
     * @param source  资源
     *                文件
     *                统一资源标识符
     * @return 媒体文件信息
     */
    @NonNull
    private static MediaFileInfo resolveMediaFileInfo(@NonNull Context context, @NonNull Object source) {
        if (source instanceof MediaFileInfo) {
            return (MediaFileInfo) source;
        }
        if (source instanceof File) {
            File file = (File) source;
            Uri uri = resolveContentUri(context, file);
            return new MediaFileInfo(file.getName(), file.getAbsolutePath(), (null != uri) ? uri : Uri.fromFile(file), file.length(), 0, file.lastModified());
        }
        if (source instanceof Uri) {
            Uri uri = (Uri) source;
            if ("file".equals(uri.getScheme())) {
                File file = new File(Objects.requireNonNull(uri.getPath()));
                Uri contentUri = resolveContentUri(context, file);
                return new MediaFileInfo(file.getName(), file.getAbsolutePath(), (null != contentUri) ? contentUri : uri, file.length(), 0, file.lastModified());
            } else if ("content".equals(uri.getScheme())) {
                return new MediaFileInfo("", "", uri, 0, 0, 0);
            }
        }
        throw new IllegalArgumentException("Unsupported source type: " + source.getClass());
    }

    /**
     * 解决内容统一资源标识符
     * <p>
     * 根据 File 查询 MediaStore content:// URI
     *
     * @param context 上下文
     * @param file    文件
     * @return 统一资源标识符
     */
    @Nullable
    private static Uri resolveContentUri(@NonNull Context context, @NonNull File file) {
        ContentResolver contentResolver = context.getContentResolver();
        Uri baseUri = MediaStore.Files.getContentUri("external");
        String selection = (MediaStore.MediaColumns.DATA + "=?");
        String[] selectionArgs = new String[]{file.getAbsolutePath()};
        try (Cursor cursor = contentResolver.query(baseUri, new String[]{MediaStore.MediaColumns._ID}, selection, selectionArgs, null)) {
            if ((null != cursor) && cursor.moveToFirst()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID));
                return Uri.withAppendedPath(baseUri, String.valueOf(id));
            }
        } catch (Exception e) {
            Timber.e(e, "resolveContentUri 查询失败");
        }
        return null;
    }
}