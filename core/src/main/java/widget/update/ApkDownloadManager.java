package widget.update;

import android.animation.ValueAnimator;
import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialog;

import com.zsp.core.R;

import java.io.File;
import java.util.Objects;

import util.handler.HandlerKit;
import widget.dialog.materialalertdialog.HintMaterialAlertDialogKit;
import widget.dialog.materialalertdialog.ProgressMaterialAlertDialogKit;

/**
 * @decs: APK 下载管理器
 * @author: 郑少鹏
 * @date: 2025/9/24 18:44
 * @version: v 1.0
 */
public class ApkDownloadManager {
    private final AppCompatActivity appCompatActivity;

    /**
     * constructor
     *
     * @param appCompatActivity 活动
     */
    public ApkDownloadManager(AppCompatActivity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;
    }

    /**
     * 执行
     *
     * @param needUpdate                需要更新
     * @param needForceUpdate           需要强制更新
     * @param fileName                  文件名
     * @param downloadUrl               下载链接
     * @param updateDescription         更新描述
     * @param useExternalPublicDownload 使用外部公共 Download 否
     */
    public void execute(boolean needUpdate, boolean needForceUpdate, String fileName, String downloadUrl, String updateDescription, boolean useExternalPublicDownload) {
        // 先走需要强制更新
        if (needForceUpdate) {
            HintMaterialAlertDialogKit.getInstance().show(appCompatActivity, appCompatActivity.getString(R.string.versionUpdate), updateDescription + "\n\n" + appCompatActivity.getString(R.string.toEnsureNormalUseThisVersionMustBeUpdated), appCompatActivity.getString(R.string.thenUpdate), null, null);
            HintMaterialAlertDialogKit.getInstance().setHintMaterialAlertDialogKitOnPositiveClickListener(alertDialog -> {
                ProgressMaterialAlertDialogKit.getInstance().show(appCompatActivity);
                startDownload(fileName, downloadUrl, useExternalPublicDownload);
            });
            return;
        }
        // 后走需要更新
        if (needUpdate) {
            HintMaterialAlertDialogKit.getInstance().show(appCompatActivity, appCompatActivity.getString(R.string.versionUpdate), updateDescription, appCompatActivity.getString(R.string.nowUpdate), appCompatActivity.getString(R.string.noUpdateYet), null);
            HintMaterialAlertDialogKit.getInstance().setHintMaterialAlertDialogKitOnPositiveClickListener(alertDialog -> {
                ProgressMaterialAlertDialogKit.getInstance().show(appCompatActivity);
                startDownload(fileName, downloadUrl, useExternalPublicDownload);
            });
            HintMaterialAlertDialogKit.getInstance().setHintMaterialAlertDialogKitOnNegativeClickListener(AppCompatDialog::dismiss);
        }
    }

    /**
     * 开始下载
     * <p>
     * 当 useExternalPublicDownload = true 时
     * 下载目标确实是外部公共目录 /storage/emulated/0/Download/
     * 但为何外部私有目录 /storage/emulated/0/Android/data/<package>/files/Download/ 仍会创建
     * 其实这是 DownloadManager + Android 框架在做环境准备
     * <p>
     * DownloadManager 内部实现
     * DownloadManager 在下载时，除指定保存目录，还会用到应用自己的私有目录做一些 临时缓存 / 中间文件 / 校验。
     * 这会触发系统自动初始化应用的 files/Download/ 目录，即便最终保存路径不在里面。
     * <p>
     * Environment.DIRECTORY_DOWNLOADS 副作用
     * 调用 setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName) 或者 setDestinationInExternalFilesDir(...) 时
     * 系统会确保 Download 标准目录存在
     * 该检查可能会同时波及公共目录和私有目录 (它们都叫 Download，API 层面会做 lazy-init)
     * <p>
     * Android 存储沙盒机制 (特别是 Q+)
     * Android 10+ 引入分区存储
     * 外部私有目录 /Android/data/<package>/files/Download 在首次访问时就会被系统创建
     * 即使没主动保存东西进去，只要某 API 调用到 Environment.DIRECTORY_DOWNLOADS 并关联应用上下文，系统就会创建该目录。
     *
     * @param fileName                  文件名
     * @param downloadUrl               下载链接
     * @param useExternalPublicDownload 使用外部公共 Download 否
     */
    private void startDownload(String fileName, String downloadUrl, boolean useExternalPublicDownload) {
        // APK 清理管理器
        // 首先删除已下载的 APK
        ApkCleanManager.execute(appCompatActivity);
        // 下载管理器请求对象
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
        request.setTitle(appCompatActivity.getString(R.string.knowAndCompanionUpdate));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        if (useExternalPublicDownload) {
            // 外部公共 Download 目录
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
            } else {
                File destination = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
                request.setDestinationUri(Uri.fromFile(destination));
            }
        } else {
            // 内部 Download 目录
            request.setDestinationInExternalFilesDir(appCompatActivity, Environment.DIRECTORY_DOWNLOADS, fileName);
        }
        // 下载管理器
        // 获取系统服务实例
        // 系统里面已有一 DownloadManager 系统服务
        // 不能自己 new DownloadManager()
        // 通过 getSystemService() 得到的是系统统一的下载调度器
        // 负责管理下载任务队列、通知栏显示、任务恢复等
        // 就像你拿到一个「工头」
        // 它能帮你排任务、跟进进度
        DownloadManager downloadManager = (DownloadManager) appCompatActivity.getSystemService(Context.DOWNLOAD_SERVICE);
        // 调用 .enqueue(request) 提交给系统
        long downloadId = downloadManager.enqueue(request);
        new Thread(() -> {
            boolean downloading = true;
            // 当前进度
            int currentProgress = 0;
            while (downloading) {
                Cursor cursor = downloadManager.query(new DownloadManager.Query().setFilterById(downloadId));
                if ((null != cursor) && cursor.moveToFirst()) {
                    int bytesTotal = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                    int bytesDownloaded = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    int progress = (bytesTotal > 0) ? Math.round((bytesDownloaded * 100.0F) / bytesTotal) : 0;
                    // 仅进度变化时更新
                    if (progress != currentProgress) {
                        final int start = currentProgress;
                        final int end = progress;
                        currentProgress = progress;
                        HandlerKit.getInstance().post(() -> {
                            // 插值动画
                            // 平滑过渡
                            ValueAnimator valueAnimator = ValueAnimator.ofInt(start, end);
                            valueAnimator.setDuration(150);
                            valueAnimator.setInterpolator(new android.view.animation.DecelerateInterpolator());
                            valueAnimator.addUpdateListener(animation -> {
                                // 更新
                                ProgressMaterialAlertDialogKit.getInstance().update((int) animation.getAnimatedValue());
                            });
                            valueAnimator.start();
                        });
                    }
                    int status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS));
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        downloading = false;
                        HandlerKit.getInstance().post(() -> {
                            // 关闭
                            ProgressMaterialAlertDialogKit.getInstance().close();
                        });
                        // 用 DownloadManager.COLUMN_LOCAL_URI 获取系统保存的实际文件路径
                        String columnLocalUri = cursor.getString(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI));
                        if (null != columnLocalUri) {
                            File file = new File(Objects.requireNonNull(Uri.parse(columnLocalUri).getPath()));
                            HandlerKit.getInstance().post(() -> {
                                // 安装
                                ApkInstallManager.getInstance(appCompatActivity).execute(file);
                            });
                        }
                    } else if (status == DownloadManager.STATUS_FAILED) {
                        downloading = false;
                        HandlerKit.getInstance().post(() -> {
                            // 关闭
                            ProgressMaterialAlertDialogKit.getInstance().close();
                        });
                    }
                }
                if (null != cursor) {
                    cursor.close();
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {

                }
            }
        }).start();
    }
}