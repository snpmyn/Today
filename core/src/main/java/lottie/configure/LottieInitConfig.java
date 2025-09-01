package lottie.configure;

import android.app.Application;

import com.airbnb.lottie.Lottie;
import com.airbnb.lottie.LottieConfig;

import java.io.IOException;

import timber.log.Timber;
import util.file.FileUtils;

/**
 * Created on 2025/8/23.
 *
 * @author 郑少鹏
 * @desc Lottie 初始化配置
 */
public class LottieInitConfig {
    /**
     * 初始化 Lottie
     * <p>
     * 应用程序初始化期间设置
     * <p>
     * Lottie 有一些全局配置选项。默认情况下不需要，但可以用于以下场景：
     * 从网络加载动画时，使用您自己的网络堆栈而不是 Lottie 的内置网络堆栈。
     * 为从网络获取的动画提供自己的缓存目录，而不是使用 Lottie 的默认目录 (cacheDir/lottie_network_cache)。
     * 启用 systrace 生成器进行调试
     * 如果您想实现自定义网络获取器级缓存策略，请完全禁用 Lottie 的网络缓存。
     *
     * @param application           应用
     * @param networkCacheFileName  网络缓存文件名
     * @param enableSystraceMarkers 允许 systrace 生成器调试
     * @param enableNetworkCache    允许网络缓存
     */
    public static void initLottie(Application application, String networkCacheFileName, boolean enableSystraceMarkers, boolean enableNetworkCache) {
        LottieConfig lottieConfig = null;
        try {
            lottieConfig = new LottieConfig.Builder().setNetworkCacheDir(FileUtils.createCacheFile(application, networkCacheFileName)).setEnableSystraceMarkers(enableSystraceMarkers).setEnableNetworkCache(enableNetworkCache).build();
        } catch (IOException e) {
            Timber.e(e);
        }
        assert lottieConfig != null;
        Lottie.initialize(lottieConfig);
    }
}