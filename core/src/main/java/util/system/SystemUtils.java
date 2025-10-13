package util.system;

import android.content.Context;
import android.content.res.Configuration;

import androidx.annotation.NonNull;

/**
 * Created on 2025/10/6.
 *
 * @author 郑少鹏
 * @desc 系统工具类
 */
public class SystemUtils {
    /**
     * 是否是深色模式
     *
     * @param context 上下文
     * @return 深色模式否
     */
    public static boolean areDark(@NonNull Context context) {
        int nightModeFlags = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return (nightModeFlags == Configuration.UI_MODE_NIGHT_YES);
    }
}