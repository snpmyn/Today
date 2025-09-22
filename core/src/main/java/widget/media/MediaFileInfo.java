package widget.media;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;

import widget.media.audio.DurationFormatTypeEnum;

/**
 * @decs: 媒体文件信息
 * @author: 郑少鹏
 * @date: 2025/9/18 17:50
 * @version: v 1.0
 */
public class MediaFileInfo {
    /**
     * 名称
     */
    public final String name;
    /**
     * 路径
     */
    public final String path;
    /**
     * 统一资源标识符
     */
    public final Uri uri;
    /**
     * 大小
     */
    public final long size;
    /**
     * 时长
     * <p>
     * 音视频时长可为 0
     */
    public long duration;
    /**
     * 系统或 MediaStore 记录的时间
     * <p>
     * 音视频文件不能保证完全一致
     * 通常接近文件实际创建时间
     */
    public long systemOrMediaStoreRecordTime;

    /**
     * constructor
     *
     * @param name                         名称
     * @param path                         路径
     * @param uri                          统一资源标识符
     * @param size                         大小
     * @param duration                     时长
     * @param systemOrMediaStoreRecordTime 系统或 MediaStore 记录的时间
     */
    public MediaFileInfo(String name, String path, Uri uri, long size, long duration, long systemOrMediaStoreRecordTime) {
        this.name = name;
        this.path = path;
        this.uri = uri;
        this.size = size;
        this.duration = duration;
        this.systemOrMediaStoreRecordTime = systemOrMediaStoreRecordTime;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public Uri getUri() {
        return uri;
    }

    public long getSize() {
        return size;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    /**
     * 输出
     *
     * @param context 上下文
     * @return 字符串
     */
    public String output(Context context) {
        return ("📄 " + name + " | 📂 " + path + " | 💾 " + formatSize() + " | ⌛ " + formatDuration(context) + " | 🗓️ " + systemOrMediaStoreRecordTime);
    }

    /**
     * 格式化大小
     *
     * @return 格式化后大小
     */
    @NonNull
    private String formatSize() {
        return MediaFileInfoHelper.getMediaFileSize(size);
    }

    /**
     * 格式化时长
     *
     * @param context 上下文
     * @return 格式化后时长
     */
    @NonNull
    private String formatDuration(Context context) {
        return MediaFileInfoHelper.getMediaFileDuration(context, uri, duration, DurationFormatTypeEnum.CHINESE);
    }
}