package util.file;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.zsp.core.R;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import widget.toast.ToastKit;

/**
 * Created on 2025/9/8.
 *
 * @author 郑少鹏
 * @desc 打开文件配套原件
 */
public class OpenFileKit {
    /**
     * 多用途互联网邮件扩展类型集
     */
    private static final Map<String, String> MIME_TYPES = new HashMap<>();

    static {
        // 文本
        MIME_TYPES.put("txt", "text/plain");
        MIME_TYPES.put("csv", "text/csv");
        MIME_TYPES.put("xml", "text/xml");
        MIME_TYPES.put("html", "text/html");
        // PDF
        MIME_TYPES.put("pdf", "application/pdf");
        // Word
        MIME_TYPES.put("doc", "application/msword");
        MIME_TYPES.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        // Excel
        MIME_TYPES.put("xls", "application/vnd.ms-excel");
        MIME_TYPES.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        // PowerPoint
        MIME_TYPES.put("ppt", "application/vnd.ms-powerpoint");
        MIME_TYPES.put("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
        // 压缩文件
        MIME_TYPES.put("zip", "application/zip");
        MIME_TYPES.put("rar", "application/x-rar-compressed");
        // 图片
        MIME_TYPES.put("jpg", "image/jpeg");
        MIME_TYPES.put("jpeg", "image/jpeg");
        MIME_TYPES.put("png", "image/png");
        MIME_TYPES.put("gif", "image/gif");
        MIME_TYPES.put("bmp", "image/bmp");
        MIME_TYPES.put("webp", "image/webp");
        // 音频
        MIME_TYPES.put("mp3", "audio/mpeg");
        MIME_TYPES.put("wav", "audio/x-wav");
        MIME_TYPES.put("ogg", "audio/ogg");
        MIME_TYPES.put("m4a", "audio/mp4");
        // 视频
        MIME_TYPES.put("mp4", "video/mp4");
        MIME_TYPES.put("avi", "video/x-msvideo");
        MIME_TYPES.put("wmv", "video/x-ms-wmv");
        MIME_TYPES.put("flv", "video/x-flv");
        MIME_TYPES.put("mkv", "video/x-matroska");
    }

    /**
     * 打开文件
     *
     * @param context 上下文
     * @param file    文件
     */
    public static void openFile(Context context, @NonNull File file) {
        if (!file.exists()) {
            ToastKit.showShort(context.getString(R.string.fileNotExit));
            return;
        }
        Uri fileUri;
        fileUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
        openUri(context, fileUri, getMimeType(file.getAbsolutePath()));
    }

    /**
     * 打开统一资源标识符
     * <p>
     * 适配 Android 10+ Scoped Storage
     *
     * @param context  上下文
     * @param uri      统一资源标识符
     * @param mimeType 多用途互联网邮件扩展类型
     */
    public static void openUri(Context context, @NonNull Uri uri, String mimeType) {
        if ((null == mimeType) || mimeType.isEmpty()) {
            mimeType = "*/*";
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, mimeType);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            ToastKit.showShort(context.getString(R.string.noApplicationFoundThatCanOpenThisFile));
        }
    }

    /**
     * 获取多用途互联网邮件扩展类型
     *
     * @param path 路径
     * @return 多用途互联网邮件扩展类型
     */
    private static String getMimeType(String path) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(path);
        if (null == extension) {
            return "*/*";
        }
        extension = extension.toLowerCase(Locale.getDefault());
        if (MIME_TYPES.containsKey(extension)) {
            return MIME_TYPES.get(extension);
        }
        String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        return (null != type) ? type : "*/*";
    }
}