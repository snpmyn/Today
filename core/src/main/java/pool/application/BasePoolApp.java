package pool.application;

import android.app.Activity;
import android.app.Application;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.tencent.mmkv.MMKVContentChangeNotification;
import com.tencent.mmkv.MMKVHandler;
import com.tencent.mmkv.MMKVLogLevel;
import com.tencent.mmkv.MMKVRecoverStrategic;
import com.zsp.core.R;

import java.util.List;

import util.activity.ActivitySuperviseManager;
import util.listener.AppListener;
import util.log.LogUtils;
import util.mmkv.MmkvInitConfigure;
import timber.log.Timber;
import widget.status.manager.StatusManager;

/**
 * Created on 2025/7/8.
 *
 * @author 郑少鹏
 * @desc 基类应用
 * 官方：
 * Base class for those who need to maintain global application state.
 * You can provide your own implementation by specifying its name in your AndroidManifest.xml's <application> tag,
 * which will cause that class to be instantiated for you when the process for your application / package is created.
 * Application 类（基础类）用于维护应用程序全局状态。
 * 你可提供自己的实现，在 AndroidManifest.xml 文件 <application> 标签指定它的名字，
 * 这将引起你的应用进程被创建时 Application 类为你被实例化。
 * <p>
 * Android 系统在每应用程序运行时仅创一 Application 实例，故 Application 可作单例（Singleton）模式一类；
 * 对象生命周期整应用程序最长，等同应用程序生命周期；
 * 全局唯一，不同 Activity、Service 中获实例相同；
 * 数据传递、数据共享、数据缓存等。
 */
public abstract class BasePoolApp extends Application implements MMKVHandler, MMKVContentChangeNotification {
    private static Boolean debug;
    private static List<String> permissionList;
    private static BasePoolApp basePoolAppInstance;

    /**
     * 获调试否
     *
     * @return 调试否
     */
    public static Boolean getDebug() {
        return debug;
    }

    /**
     * 获权限集
     *
     * @return 权限集
     */
    public static List<String> getPermissionList() {
        return permissionList;
    }

    /**
     * 获单例
     *
     * @return 单例
     */
    public static BasePoolApp getBasePoolAppInstance() {
        return basePoolAppInstance;
    }

    /**
     * 应用程序创调
     * <p>
     * 创和实例化任何应用程序状态变量或共享资源变量，方法内获 Application 单例。
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Timber.d("%s onCreate", getClass().getSimpleName());
        // 调试否
        debug = debug();
        // 权限集
        permissionList = permissionList();
        // Application 本已单例
        basePoolAppInstance = this;
        // 状态管理器布局 ID
        StatusManager.BASE_LOADING_LAYOUT_ID = R.layout.status_loading;
        StatusManager.BASE_EMPTY_LAYOUT_ID = R.layout.status_empty;
        StatusManager.BASE_RETRY_LAYOUT_ID = R.layout.status_retry;
        // 初始化配置
        initConfiguration();
    }

    /**
     * This method is for use in emulated process environments.
     * It will never be called on a production Android device, where processes are removed by simply killing them; no user code (including this callback) is executed when doing so.
     * <p>
     * 应用程序对象终止调
     * <p>
     * 不定调。应用程序被内核终止为别应用程序释放资源，将不提醒且不调应用程序对象 onTerminate() 而直接终止进程。
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
        Timber.d("%s onTerminate", getClass().getSimpleName());
    }

    /**
     * 系统资源匮乏调
     * <p>
     * 通于后台进程已结束且前台应用程序仍缺内存时调，重写该法清缓存或释放非必要资源。
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Timber.d("%s onLowMemory", getClass().getSimpleName());
    }

    /**
     * 运行时决定当前应用程序应减内存开销时（通进后台运行）调，含一 level 参数提供请求上下文。
     *
     * @param level 级别
     */
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Timber.d("%s onTrimMemory", getClass().getSimpleName());
    }

    /**
     * 与 Activity 不同，配置变时应用程序对象不终止和重启。应用程序用值依赖特定配置则重写该法加载这些值或于应用程序级处理配置值改变。
     *
     * @param newConfig 配置
     */
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Timber.d("%s onConfigurationChanged", getClass().getSimpleName());
    }

    /**
     * 调试否
     *
     * @return 调试否
     */
    protected abstract Boolean debug();

    /**
     * 权限集
     *
     * @return 权限集
     */
    protected abstract List<String> permissionList();

    /**
     * 初始化配置
     */
    private void initConfiguration() {
        // 日志工具类
        LogUtils.Builder.initConfiguration(true, true, true, true);
        // MMKV
        MmkvInitConfigure.initMmkv(this, debug, this, this);
        // 应用监听
        AppListener.getInstance().initConfiguration(this);
        // 全局监听 Activity 生命周期
        registerActivityListener();
    }

    /**
     * Activity 全局监听
     */
    private void registerActivityListener() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, Bundle savedInstanceState) {
                // 添监听到创事件 Activity 至集合
                ActivitySuperviseManager.getInstance().pushActivity(activity);
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {

            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {

            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                // 移监听到销事件 Activity 出集合
                ActivitySuperviseManager.getInstance().removeActivity(activity);
            }
        });
    }

    @Override
    public void onContentChangedByOuterProcess(String mmapID) {
        Timber.i("[content changed] %s", mmapID);
    }

    @Override
    public MMKVRecoverStrategic onMMKVCRCCheckFail(String mmapID) {
        return MMKVRecoverStrategic.OnErrorRecover;
    }

    @Override
    public MMKVRecoverStrategic onMMKVFileLengthError(String mmapID) {
        return MMKVRecoverStrategic.OnErrorRecover;
    }

    @Override
    public boolean wantLogRedirecting() {
        return true;
    }

    @Override
    public void mmkvLog(@NonNull MMKVLogLevel level, String file, int line, String function, String message) {
        String log = ("< " + file + " : " + line + " :: " + function + " > " + message);
        switch (level) {
            case LevelDebug:
                Timber.d("[redirect logging MMKV] %s", log);
                break;
            case LevelInfo:
                Timber.i("[redirect logging MMKV] %s", log);
                break;
            case LevelWarning:
                Timber.w("[redirect logging MMKV] %s", log);
                break;
            case LevelError:
            case LevelNone:
                Timber.e("[redirect logging MMKV] %s", log);
                break;
            default:
                break;
        }
    }
}