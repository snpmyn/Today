package widget.media;

import android.os.Environment;

import androidx.annotation.NonNull;

/**
 * @decs: 媒体文件目录枚举
 * @author: 郑少鹏
 * @date: 2025/9/17 22:27
 * @version: v 1.0
 */
public enum MediaFileDirectoryEnum {
    /**
     * 内部目录
     * <p>
     * context.getFilesDir()
     */
    INTERNAL(""),
    /**
     * 外部私有目录
     * <p>
     * context.getExternalFilesDir(null)
     */
    EXTERNAL_PRIVATE(""),
    /**
     * 外部公共目录
     * <p>
     * Music
     */
    MUSIC(Environment.DIRECTORY_MUSIC + "/"),
    /**
     * 外部公共目录
     * <p>
     * Downloads
     */
    DOWNLOADS(Environment.DIRECTORY_DOWNLOADS + "/"),
    /**
     * 外部公共目录
     * <p>
     * Ringtones
     */
    RINGTONES(Environment.DIRECTORY_RINGTONES + "/"),
    /**
     * 外部公共目录
     * <p>
     * Podcasts
     */
    PODCASTS(Environment.DIRECTORY_PODCASTS + "/"),
    /**
     * 外部公共目录
     * <p>
     * Alarms
     */
    ALARMS(Environment.DIRECTORY_ALARMS + "/"),
    /**
     * 外部公共目录
     * <p>
     * Notifications
     */
    NOTIFICATIONS(Environment.DIRECTORY_NOTIFICATIONS + "/"),
    /**
     * 外部公共目录
     * <p>
     * Documents
     */
    DOCUMENTS(Environment.DIRECTORY_DOCUMENTS + "/"),
    /**
     * 外部公共目录
     * <p>
     * Pictures
     */
    PICTURES(Environment.DIRECTORY_PICTURES + "/"),
    /**
     * 外部公共目录
     * <p>
     * Movies
     */
    MOVIES(Environment.DIRECTORY_MOVIES + "/"),
    /**
     * 外部公共目录
     * <p>
     * DCIM
     */
    DCIM(Environment.DIRECTORY_DCIM + "/"),
    /**
     * 外部公共目录
     * <p>
     * SCREENSHOTS
     * <p>
     * 适配不同 ROM
     * Pictures/Screenshots/
     */
    DIRECTORY_PICTURES_SCREENSHOTS(Environment.DIRECTORY_PICTURES + "/Screenshots/"),
    /**
     * 外部公共目录
     * <p>
     * SCREENSHOTS
     * <p>
     * 适配不同 ROM
     * DCIM/Screenshots/
     */
    DIRECTORY_DCIM_SCREENSHOTS(Environment.DIRECTORY_DCIM + "/Screenshots/");
    /**
     * 相对路径
     */
    private final String relativePath;

    /**
     * constructor
     *
     * @param relativePath 相对路径
     */
    MediaFileDirectoryEnum(@NonNull String relativePath) {
        this.relativePath = relativePath;
    }

    /**
     * 获取相对路径
     *
     * @return 相对路径
     */
    public String getRelativePath() {
        return relativePath;
    }

    /**
     * 是否是内部目录
     *
     * @return 内部目录否
     */
    public boolean areInternal() {
        return (this == INTERNAL);
    }

    /**
     * 是否是外部私有目录
     *
     * @return 外部私有目录否
     */
    public boolean areExternalPrivate() {
        return (this == EXTERNAL_PRIVATE);
    }

    /**
     * 是否是外部公共目录
     *
     * @return 外部公共目录否
     */
    public boolean areExternalPublic() {
        return (!areInternal() && !areExternalPrivate());
    }
}