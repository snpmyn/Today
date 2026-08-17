package util.mmkv;

import android.app.Application;

import com.getkeepsafe.relinker.ReLinker;
import com.tencent.mmkv.MMKV;
import com.tencent.mmkv.MMKVHandler;
import com.tencent.mmkv.MMKVLogLevel;

import org.jetbrains.annotations.NotNull;

import timber.log.Timber;

/**
 * Created on 2019/9/9.
 *
 * @author 郑少鹏
 * @desc MMKV 初始化配置
 */
public class MmkvInitConfigure {
    /**
     * 初始化 MMKV
     *
     * @param application 应用实例
     * @param debug       是否开启调试模式
     * @param mmkvHandler MMKV 处理器 [接管日志与异常]
     */
    public static void initMmkv(@NotNull Application application, boolean debug, MMKVHandler mmkvHandler) {
        String dir = application.getFilesDir().getAbsolutePath() + "/mmkv";
        MMKVLogLevel logLevel = debug ? MMKVLogLevel.LevelInfo : MMKVLogLevel.LevelNone;
        String rootDir = MMKV.initialize(application, dir, libName -> ReLinker.loadLibrary(application, libName), logLevel, mmkvHandler);
        Timber.d("mmkv root - %s", rootDir);
    }

    /**
     * 退出
     * <p>
     * 官方示例于 Activity 之 onDestroy() 调
     */
    public static void exit() {
        MMKV.onExit();
    }
}