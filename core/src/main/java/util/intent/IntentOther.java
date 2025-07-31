package util.intent;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.core.content.FileProvider;

import org.jetbrains.annotations.NotNull;

import java.io.File;

import util.value.UtilMagic;

/**
 * Created on 2018/11/28.
 *
 * @author 郑少鹏
 * @desc IntentUtils
 */
public class IntentOther {
    /**
     * 重启
     *
     * @param application Application
     */
    public static void restart(@NotNull Application application) {
        Intent intent = application.getPackageManager().getLaunchIntentForPackage(application.getPackageName());
        if (null != intent) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        application.startActivity(intent);
    }

    /**
     * 打电话
     *
     * @param context         上下文
     * @param cellPhoneNumber 手机号
     */
    public static void phone(@NotNull Context context, String cellPhoneNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + cellPhoneNumber);
        intent.setData(data);
        context.startActivity(intent);
    }

    /**
     * 调用浏览器
     *
     * @param context 上下文
     * @param uri     统一资源标识符
     */
    public static void callBrowser(@NotNull Context context, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    /**
     * 装 APK
     *
     * @param context 上下文
     * @param apkPath APK 路径
     */
    public static void installApk(Context context, String apkPath) {
        File apkFile = new File(apkPath);
        if (!apkFile.exists()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + apkFile), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * Creates view intent for given file.
     *
     * @param context   Context
     * @param authority YOUR_AUTHORITY.provider
     * @param file      File
     * @return Intent
     */
    public static @NotNull Intent getViewIntent(Context context, String authority, File file) {
        Uri uri = FileProvider.getUriForFile(context, authority, file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String url = file.toString();
        if (url.contains(UtilMagic.STRING_DOT_DOC) || url.contains(UtilMagic.STRING_DOT_DOCX)) {
            // Word document
            intent.setDataAndType(uri, "application/msword");
        } else if (url.contains(UtilMagic.STRING_DOT_PDF)) {
            // PDF file
            intent.setDataAndType(uri, "application/pdf");
        } else if (url.contains(UtilMagic.STRING_DOT_PPT) || url.contains(UtilMagic.STRING_DOT_PPTX)) {
            // Powerpoint file
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        } else if (url.contains(UtilMagic.STRING_DOT_XLS) || url.contains(UtilMagic.STRING_DOT_XLSX)) {
            // Excel file
            intent.setDataAndType(uri, "application/vnd.ms-excel");
        } else if (url.contains(UtilMagic.STRING_DOT_ZIP) || url.contains(UtilMagic.STRING_DOT_RAR)) {
            // WAV audio file
            intent.setDataAndType(uri, "application/x-wav");
        } else if (url.contains(UtilMagic.STRING_DOT_RTF)) {
            // RTF file
            intent.setDataAndType(uri, "application/rtf");
        } else if (url.contains(UtilMagic.STRING_DOT_WAV) || url.contains(UtilMagic.STRING_DOT_MP3)) {
            // WAV audio file
            intent.setDataAndType(uri, "audio/x-wav");
        } else if (url.contains(UtilMagic.STRING_DOT_GIF)) {
            // GIF file
            intent.setDataAndType(uri, "image/gif");
        } else if (url.contains(UtilMagic.STRING_DOT_JPG) || url.contains(UtilMagic.STRING_DOT_JPEG) || url.contains(UtilMagic.STRING_DOT_PNG)) {
            // JPG file
            intent.setDataAndType(uri, "image/jpeg");
        } else if (url.contains(UtilMagic.STRING_DOT_TXT)) {
            // Text file
            intent.setDataAndType(uri, "text/plain");
        } else if (url.contains(UtilMagic.STRING_DOT_3GP) || url.contains(UtilMagic.STRING_DOT_MPG) || url.contains(UtilMagic.STRING_DOT_MPEG) || url.contains(UtilMagic.STRING_DOT_MPE) || url.contains(UtilMagic.STRING_DOT_MP4) || url.contains(UtilMagic.STRING_DOT_AVI)) {
            // Video files
            intent.setDataAndType(uri, "video/*");
        } else {
            intent.setDataAndType(uri, "*/*");
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        return intent;
    }

    /**
     * Get the intent for selecting content to be used in an intent chooser.
     *
     * @return the intent for opening a file with Intent.createChooser()
     */
    public static @NotNull Intent createGetContentIntent() {
        // Implicitly allow the user to select a particular kind of data.
        final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        // the mime data type filter
        intent.setType("*/*");
        // only return uris that can be opened with ContentResolver
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        return intent;
    }
}
