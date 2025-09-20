package widget.permissionx.kit;

import android.Manifest;
import android.os.Build;

import androidx.annotation.NonNull;

import com.permissionx.guolindev.PermissionX;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2025/9/15.
 *
 * @author 郑少鹏
 * @desc 权限配套原件
 */
public class PermissionKit {
    /**
     * 存储
     *
     * @return 存储权限集
     */
    @NonNull
    public static List<String> storage() {
        List<String> list = new ArrayList<>(3);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // ✅ Android 11+ (API 30+)
            list.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE);
        } else {
            // ✅ Android 7 – 10 (API 24 – 29)
            list.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            list.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        return list;
    }

    /**
     * 定位
     *
     * @return 定位权限集
     */
    @NonNull
    public static List<String> location() {
        List<String> list = new ArrayList<>(1);
        // ✅ Android 7+ (API 24+)
        list.add(Manifest.permission.ACCESS_FINE_LOCATION);
        // ✅ Android 10+ (API 29+)
        // 高德地图定位暂不申请台定位权限
        /*list.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);*/
        return list;
    }

    /**
     * 通知
     *
     * @return 通知权限集
     */
    @NonNull
    public static List<String> notification() {
        List<String> list = new ArrayList<>(1);
        // ✅ Android 13+ (API 33+)
        list.add(PermissionX.permission.POST_NOTIFICATIONS);
        return list;
    }

    /**
     * 短信
     *
     * @return 短信权限集
     */
    @NonNull
    public static List<String> sms() {
        List<String> list = new ArrayList<>(1);
        // ✅ Android 7+ (API 24+)
        // ✅ 潜在问题：Android 9+ 限制了短信发送行为，需在 Play Store 申请⭐默认短信应用⭐才可使用部分功能。
        list.add(Manifest.permission.SEND_SMS);
        return list;
    }

    /**
     * 录制音频
     *
     * @return 录制音频权限集
     */
    @NonNull
    public static List<String> recordAudio() {
        List<String> list = new ArrayList<>(1);
        // ✅ Android 7+ (API 24+)
        // ✅ Android 10+ (API 29+) 需前台使用才可正常录制
        // ✅ 后台录制需额外策略（一般不推荐）
        list.add(Manifest.permission.RECORD_AUDIO);
        return list;
    }

    /**
     * 读取音频
     *
     * @return 读取音频权限集
     */
    @NonNull
    public static List<String> readAudio() {
        List<String> list = new ArrayList<>(2);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // ✅ Android 13+ (API 33+)
            list.add(Manifest.permission.READ_MEDIA_AUDIO);
        } else {
            // ✅ Android 7 – 12L (API 24 – 32)
            list.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        return list;
    }
}