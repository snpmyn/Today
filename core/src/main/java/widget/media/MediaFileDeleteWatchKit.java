package widget.media;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.FileObserver;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import timber.log.Timber;

/**
 * @decs: 媒体文件删除观察配套原件
 * @author: 郑少鹏
 * @date: 2025/9/21 21:39
 * @version: v 1.0
 * <p>
 * 不要使用 static
 * 否则导致全局共享状态
 * 不再支持多个独立实例同时观察不同目录
 * 并发使用时容易出现状态冲突或内存泄漏
 */
public class MediaFileDeleteWatchKit {
    private final Context context;
    private ContentResolver contentResolver;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final List<Set<String>> lastFileSets = new ArrayList<>();
    private final List<FileObserver> fileObservers = new ArrayList<>();
    private final List<ContentObserver> contentObservers = new ArrayList<>();
    private MediaFileDeleteWatchListener mediaFileDeleteWatchListener;

    /**
     * constructor
     *
     * @param context 上下文
     */
    public MediaFileDeleteWatchKit(@NonNull Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * 观察多目录
     *
     * @param mediaFileDirectories 媒体文件目录枚举数组
     * @param fileNamePrefix       文件名前缀
     * @param mediaFileTypeEnum    媒体文件类型枚举
     */
    public void watchMultipleDirectories(@NonNull MediaFileDirectoryEnum[] mediaFileDirectories, @Nullable String fileNamePrefix, @NonNull MediaFileTypeEnum mediaFileTypeEnum) {
        for (MediaFileDirectoryEnum mediaFileDirectoryEnum : mediaFileDirectories) {
            watchSingleDirectory(mediaFileDirectoryEnum, fileNamePrefix, mediaFileTypeEnum);
        }
    }

    /**
     * 观察单目录
     *
     * @param mediaFileDirectoryEnum 媒体文件目录枚举
     * @param fileNamePrefix         文件名前缀
     * @param mediaFileTypeEnum      媒体文件类型枚举
     */
    public void watchSingleDirectory(@NonNull MediaFileDirectoryEnum mediaFileDirectoryEnum, @Nullable String fileNamePrefix, @NonNull MediaFileTypeEnum mediaFileTypeEnum) {
        if (mediaFileDirectoryEnum.areInternal()) {
            // 内部目录
            // 观察内部目录
            watchInternalDirectory(context.getFilesDir(), fileNamePrefix, mediaFileTypeEnum);
        } else if (mediaFileDirectoryEnum.areExternalPrivate()) {
            // 外部私有目录
            File externalPrivateDir = context.getExternalFilesDir(null);
            if (null != externalPrivateDir) {
                // 观察内部目录
                watchInternalDirectory(externalPrivateDir, fileNamePrefix, mediaFileTypeEnum);
            }
        } else if (mediaFileDirectoryEnum.areExternalPublic()) {
            // 外部公共目录
            // 观察外部公共目录
            watchExternalPublicDirectory(mediaFileDirectoryEnum.getRelativePath(), fileNamePrefix, mediaFileTypeEnum);
        }
    }

    /**
     * 观察内部目录
     *
     * @param dir               内部目录
     * @param fileNamePrefix    文件名前缀
     * @param mediaFileTypeEnum 媒体文件类型枚举
     */
    private void watchInternalDirectory(@NonNull File dir, @Nullable String fileNamePrefix, @NonNull MediaFileTypeEnum mediaFileTypeEnum) {
        if (!dir.exists()) {
            return;
        }
        FileObserver fileObserver = new FileObserver(dir.getAbsolutePath(), FileObserver.DELETE | FileObserver.DELETE_SELF) {
            @Override
            public void onEvent(int event, @Nullable String path) {
                if ((null != path) && (null != mediaFileDeleteWatchListener)) {
                    boolean matchesSuffix = ((mediaFileTypeEnum == MediaFileTypeEnum.ALL) || path.endsWith(mediaFileTypeEnum.getSuffix()));
                    if (((null == fileNamePrefix) || path.startsWith(fileNamePrefix)) && matchesSuffix) {
                        String fullPath = new File(dir, path).getAbsolutePath();
                        mainHandler.post(() -> mediaFileDeleteWatchListener.onMediaFileDeleted(fullPath, null));
                    }
                }
            }
        };
        fileObserver.startWatching();
        fileObservers.add(fileObserver);
    }

    /**
     * 观察外部公共目录
     *
     * @param relativePath      相对路径
     * @param fileNamePrefix    文件名前缀
     * @param mediaFileTypeEnum 媒体文件类型枚举
     */
    private void watchExternalPublicDirectory(@NonNull String relativePath, @Nullable String fileNamePrefix, @NonNull MediaFileTypeEnum mediaFileTypeEnum) {
        if (null == contentResolver) {
            contentResolver = context.getContentResolver();
        }
        Uri mediaUri = MediaStore.Files.getContentUri("external");
        final Set<String> lastFileSet = queryMediaStoreFiles(mediaUri, relativePath, fileNamePrefix, mediaFileTypeEnum);
        lastFileSets.add(lastFileSet);
        ContentObserver contentObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
            @Override
            public void onChange(boolean selfChange, @Nullable Uri uri) {
                detectDeletedFiles(mediaUri, relativePath, lastFileSet, fileNamePrefix, mediaFileTypeEnum);
            }
        };
        contentObservers.add(contentObserver);
        contentResolver.registerContentObserver(mediaUri, true, contentObserver);
        detectDeletedFiles(mediaUri, relativePath, lastFileSet, fileNamePrefix, mediaFileTypeEnum);
    }

    /**
     * 查询媒体存储文件集
     *
     * @param mediaUri          媒体统一资源标识符
     * @param relativePath      相对路径
     * @param fileNamePrefix    文件名前缀
     * @param mediaFileTypeEnum 媒体文件类型枚举
     * @return 媒体存储文件集
     */
    @NonNull
    private Set<String> queryMediaStoreFiles(@NonNull Uri mediaUri, @Nullable String relativePath, @Nullable String fileNamePrefix, @NonNull MediaFileTypeEnum mediaFileTypeEnum) {
        Set<String> fileSet = new HashSet<>();
        try {
            String selection = null;
            String[] selectionArgs = null;
            if (!TextUtils.isEmpty(relativePath)) {
                selection = (MediaStore.Files.FileColumns.RELATIVE_PATH + " LIKE ?");
                selectionArgs = new String[]{relativePath + "%"};
            }
            try (Cursor cursor = contentResolver.query(mediaUri, new String[]{MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.DISPLAY_NAME}, selection, selectionArgs, null)) {
                if (null != cursor) {
                    int nameIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME);
                    while (cursor.moveToNext()) {
                        String name = cursor.getString(nameIndex);
                        boolean matchesSuffix = ((mediaFileTypeEnum == MediaFileTypeEnum.ALL) || name.endsWith(mediaFileTypeEnum.getSuffix()));
                        if (((null == fileNamePrefix) || name.startsWith(fileNamePrefix)) && matchesSuffix) {
                            fileSet.add(name);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return fileSet;
    }

    /**
     * 检测已删除文件
     *
     * @param mediaUri          媒体统一资源标识符
     * @param relativePath      相对路径
     * @param lastFileSet       上次文件集
     * @param fileNamePrefix    文件名前缀
     * @param mediaFileTypeEnum 媒体文件类型枚举
     */
    private void detectDeletedFiles(@NonNull Uri mediaUri, @Nullable String relativePath, @NonNull Set<String> lastFileSet, @Nullable String fileNamePrefix, @NonNull MediaFileTypeEnum mediaFileTypeEnum) {
        Set<String> currentFiles = queryMediaStoreFiles(mediaUri, relativePath, fileNamePrefix, mediaFileTypeEnum);
        Set<String> deletedFiles = new HashSet<>(lastFileSet);
        deletedFiles.removeAll(currentFiles);
        for (String name : deletedFiles) {
            if (null != mediaFileDeleteWatchListener) {
                String absolutePath = getAbsolutePathFromRelativePathAndFileName(relativePath, name);
                Uri fileUri = Uri.withAppendedPath(mediaUri, name);
                mainHandler.post(() -> mediaFileDeleteWatchListener.onMediaFileDeleted(absolutePath, fileUri));
            }
        }
        lastFileSet.clear();
        lastFileSet.addAll(currentFiles);
    }

    /**
     * 通过相对路径和文件名获取绝对路径
     *
     * @param relativePath 相对路径
     * @param fileName     文件名
     * @return 绝对路径
     */
    private String getAbsolutePathFromRelativePathAndFileName(@Nullable String relativePath, @NonNull String fileName) {
        if (TextUtils.isEmpty(relativePath)) {
            return fileName;
        }
        File baseDir = Environment.getExternalStoragePublicDirectory("");
        return new File(baseDir, relativePath + fileName).getAbsolutePath();
    }

    /**
     * 停止观察
     */
    public void stopWatching() {
        for (FileObserver fileObserver : fileObservers) {
            fileObserver.stopWatching();
        }
        fileObservers.clear();
        if (null != contentResolver) {
            for (ContentObserver contentObserver : contentObservers) {
                contentResolver.unregisterContentObserver(contentObserver);
            }
        }
        contentObservers.clear();
        lastFileSets.clear();
    }

    /**
     * 获取上次文件集
     * <p>
     * lastFileSet 并非当前瞬时文件状态，而是上次检测完后记录的文件状态。
     * 作用是用作基准，对比当前文件状态，发现哪些文件被删除了。
     * 每次检测完后更新到最新状态，变成下次检测的上次状态。
     *
     * @return 上次文件集
     */
    @NonNull
    public List<Set<String>> getLastFileSets() {
        return new ArrayList<>(lastFileSets);
    }

    /**
     * 获取正在观察的公共目录数量
     *
     * @return 正在观察的公共目录数量
     */
    public int getWatchedPublicDirectoryCount() {
        return lastFileSets.size();
    }

    /**
     * 媒体文件删除观察监听
     */
    public interface MediaFileDeleteWatchListener {
        /**
         * 媒体文件已删除
         *
         * @param absolutePathOrUri 绝对路径或统一资源标识符
         * @param mediaStoreUri     文件对应 MediaStore 统一资源标识符
         *                          外部公共目录有效
         */
        void onMediaFileDeleted(@NonNull String absolutePathOrUri, @Nullable Uri mediaStoreUri);
    }

    /**
     * 设置媒体文件删除观察监听
     *
     * @param mediaFileDeleteWatchListener 媒体文件删除观察监听
     */
    public void setMediaFileDeleteWatchListener(@NonNull MediaFileDeleteWatchListener mediaFileDeleteWatchListener) {
        this.mediaFileDeleteWatchListener = mediaFileDeleteWatchListener;
    }
}