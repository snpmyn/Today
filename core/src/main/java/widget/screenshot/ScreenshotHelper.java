package widget.screenshot;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.View;

import com.zsp.core.R;

import java.io.OutputStream;

import timber.log.Timber;
import util.datetime.CurrentTimeMillisClock;
import util.handler.HandlerKit;
import widget.media.MediaFileDirectoryEnum;
import widget.media.MediaFileTypeEnum;
import widget.toast.ToastKit;

/**
 * Created on 2025/9/20.
 *
 * @author 郑少鹏
 * @desc 截屏帮助者
 */
public class ScreenshotHelper {
    /**
     * 获取位图
     *
     * @param view 视图
     * @return 位图
     */
    public static Bitmap getBitmap(View view) {
        if ((null == view) || (view.getWidth() <= 0) || (view.getHeight() <= 0)) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    /**
     * 保存位图
     *
     * @param context        上下文
     * @param bitmap         位图
     * @param fileNamePrefix 文件名前缀
     */
    public static void saveBitmap(Context context, Bitmap bitmap, String fileNamePrefix) {
        MediaFileTypeEnum mediaFileTypeEnum = MediaFileTypeEnum.WEBP;
        MediaFileDirectoryEnum mediaFileDirectoryEnum = MediaFileDirectoryEnum.getScreenshotMediaFileDirectoryEnum(context);
        Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.WEBP;
        if (null == bitmap) {
            return;
        }
        new Thread(() -> {
            try {
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileNamePrefix + "-" + CurrentTimeMillisClock.getInstance().now() + mediaFileTypeEnum.getSuffix());
                contentValues.put(MediaStore.Images.Media.MIME_TYPE, mediaFileTypeEnum.getMimeType());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, mediaFileDirectoryEnum.getRelativePath());
                }
                Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                if (null != uri) {
                    OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
                    if (null != outputStream) {
                        bitmap.compress(compressFormat, 100, outputStream);
                        outputStream.flush();
                        outputStream.close();
                    }
                    postToast(context, context.getString(R.string.theScreenshotHasBeenSavedToTheAlbum));
                } else {
                    postToast(context, context.getString(R.string.saveFail));
                }
            } catch (Exception e) {
                Timber.e(e);
                postToast(context, context.getString(R.string.saveFail));
            }
        }).start();
    }

    /**
     * 发送吐司
     *
     * @param context 上下文
     * @param message 消息
     */
    private static void postToast(Context context, String message) {
        if (null != context) {
            HandlerKit.getInstance().post(() -> ToastKit.showShort(message));
        }
    }
}