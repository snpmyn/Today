package util.resource;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import timber.log.Timber;

/**
 * Created on 2025/10/15.
 *
 * @author 郑少鹏
 * @desc 资源工具类
 * <p>
 * getResourceId(context, "home_come", "drawable");
 * <p>
 * getDrawable(context, "home_come");
 * <p>
 * getBitmap(context, "home_come");
 * <p>
 * getUri(context, "home_come", "raw");
 */
public class ResourceUtils {
    /**
     * 获取资源 ID
     *
     * @param context      上下文
     * @param resourceName 资源名
     * @param defType      定义类型
     *                     drawable
     *                     string
     *                     layout
     * @return 资源 ID
     */
    public static int getResourceId(Context context, String resourceName, String defType) {
        if ((null == context) || (null == resourceName) || TextUtils.isEmpty(resourceName)) {
            return 0;
        }
        try {
            return context.getResources().getIdentifier(resourceName, defType, context.getPackageName());
        } catch (Exception e) {
            Timber.d(e, "getResId - 找不到资源 " + resourceName + " 类型 " + defType);
            return 0;
        }
    }

    /**
     * 获取位图
     *
     * @param context      上下文
     * @param resourceName 资源名
     * @return 位图
     */
    @Nullable
    public static Drawable getDrawable(Context context, String resourceName) {
        int resourceId = getResourceId(context, resourceName, "drawable");
        if (resourceId != 0) {
            return ContextCompat.getDrawable(context, resourceId);
        }
        return null;
    }

    /**
     * 获取 Bitmap
     *
     * @param context      上下文
     * @param resourceName 资源名
     * @return Bitmap
     */
    @Nullable
    public static Bitmap getBitmap(Context context, String resourceName) {
        int resourceId = getResourceId(context, resourceName, "drawable");
        if (resourceId != 0) {
            return BitmapFactory.decodeResource(context.getResources(), resourceId);
        }
        return null;
    }

    /**
     * 获取统一资源标识符
     *
     * @param context      上下文
     * @param resourceName 资源名
     * @param defType      定义类型
     *                     音频
     *                     视频
     * @return 统一资源标识符
     */
    @Nullable
    public static Uri getUri(Context context, String resourceName, String defType) {
        int resourceId = getResourceId(context, resourceName, defType);
        if (resourceId != 0) {
            return Uri.parse("android.resource://" + context.getPackageName() + "/" + resourceId);
        }
        return null;
    }
}