package widget.media;

import androidx.annotation.NonNull;

/**
 * @decs: 媒体文件类型枚举
 * @author: 郑少鹏
 * @date: 2025/9/17 21:28
 * @version: v 1.0
 */
public enum MediaFileTypeEnum {
    /**
     * 音频
     * <p>
     * MP3
     */
    MP3(".mp3", "audio/mpeg"),
    /**
     * 音频
     * <p>
     * M4A
     */
    M4A(".m4a", "audio/m4a"),
    /**
     * 音频
     * <p>
     * WAV
     */
    WAV(".wav", "audio/wav"),
    /**
     * 音频
     * <p>
     * AAC
     */
    AAC(".aac", "audio/aac"),
    /**
     * 音频
     * <p>
     * OGG
     */
    OGG(".ogg", "audio/ogg"),
    /**
     * 音频
     * <p>
     * FLAC
     */
    FLAC(".flac", "audio/flac"),
    /**
     * 音频
     * <p>
     * AMR
     */
    AMR(".amr", "audio/amr"),
    /**
     * 音频
     * <p>
     * MID
     */
    MID(".mid", "audio/midi"),
    /**
     * 音频
     * <p>
     * MIDI
     */
    MIDI(".midi", "audio/midi"),
    /**
     * 音频
     * <p>
     * APE
     */
    APE(".ape", "audio/ape"),
    /**
     * 视频
     * <p>
     * MP4
     */
    MP4(".mp4", "video/mp4"),
    /**
     * 视频
     * <p>
     * AVI
     */
    AVI(".avi", "video/x-msvideo"),
    /**
     * 视频
     * <p>
     * MKV
     */
    MKV(".mkv", "video/x-matroska"),
    /**
     * 视频
     * <p>
     * MOV
     */
    MOV(".mov", "video/quicktime"),
    /**
     * 视频
     * <p>
     * WMV
     */
    WMV(".wmv", "video/x-ms-wmv"),
    /**
     * 视频
     * <p>
     * FLV
     */
    FLV(".flv", "video/x-flv"),
    /**
     * 文档
     * <p>
     * PDF
     */
    PDF(".pdf", "application/pdf"),
    /**
     * 文档
     * <p>
     * DOC
     */
    DOC(".doc", "application/msword"),
    /**
     * 文档
     * <p>
     * DOCX
     */
    DOCX(".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    /**
     * 文档
     * <p>
     * XLS
     */
    XLS(".xls", "application/vnd.ms-excel"),
    /**
     * 文档
     * <p>
     * XLSX
     */
    XLSX(".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
    /**
     * 文档
     * <p>
     * PPT
     */
    PPT(".ppt", "application/vnd.ms-powerpoint"),
    /**
     * 文档
     * <p>
     * PPTX
     */
    PPTX(".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"),
    /**
     * 文档
     * <p>
     * TXT
     */
    TXT(".txt", "text/plain"),
    /**
     * 图片
     * <p>
     * JPG
     */
    JPG(".jpg", "image/jpeg"),
    /**
     * 图片
     * <p>
     * JPEG
     */
    JPEG(".jpeg", "image/jpeg"),
    /**
     * 图片
     * <p>
     * PNG
     */
    PNG(".png", "image/png"),
    /**
     * 图片
     * <p>
     * GIF
     */
    GIF(".gif", "image/gif"),
    /**
     * 图片
     * <p>
     * BMP
     */
    BMP(".bmp", "image/bmp"),
    /**
     * 图片
     * <p>
     * WEBP
     */
    WEBP(".webp", "image/webp"),
    /**
     * 压缩
     * <p>
     * ZIP
     */
    ZIP(".zip", "application/zip"),
    /**
     * 压缩
     * <p>
     * RAR
     */
    RAR(".rar", "application/vnd.rar"),
    /**
     * 压缩
     * <p>
     * TAR
     */
    TAR(".tar", "application/x-tar"),
    /**
     * 压缩
     * <p>
     * GZ
     */
    GZ(".gz", "application/gzip"),
    /**
     * 压缩
     * <p>
     * APK
     */
    APK(".apk", "application/vnd.android.package-archive"),
    /**
     * 全部
     * <p>
     * ALL
     */
    ALL("", "application/octet-stream");
    private final String suffix;
    private final String mimeType;

    MediaFileTypeEnum(@NonNull String suffix, @NonNull String mimeType) {
        this.suffix = suffix;
        this.mimeType = mimeType;
    }

    @NonNull
    public String getSuffix() {
        return suffix;
    }

    @NonNull
    public String getMimeType() {
        return mimeType;
    }

    /**
     * 是否需要时长
     * <p>
     * 在 Android 媒体文件体系中
     * 仅带时间轴的媒体文件 (音频、视频) 有 duration (时长) 属性
     * <p>
     * 音频 (Audio)
     * 有时长
     * 单位毫秒 (MediaStore.Audio.Media.DURATION)
     * <p>
     * 视频 (Video)
     * 有时长
     * 单位毫秒 (MediaStore.Video.Media.DURATION)
     * <p>
     * 图片 (Image)
     * 没时长
     * 只有尺寸 (宽、高)、拍摄时间、EXIF 信息等
     * <p>
     * 文档 (PDF、TXT、DOC 等)
     * 没时长
     * 通常只有大小、创建时间、MIME 类型
     * <p>
     * 其它文件 (APK、压缩包、任意文件)
     * 没时长
     *
     * @return 需要时长否
     */
    public boolean needDuration() {
        return (mimeType.startsWith("audio/") || mimeType.startsWith("video/"));
    }
}