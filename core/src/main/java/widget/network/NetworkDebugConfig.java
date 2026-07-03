package widget.network;

import com.qtone.scandemo.BuildConfig;

/**
 * Created on 2026/7/3.
 *
 * @author 郑少鹏
 * @desc 网络调试配置
 * <p>
 * volatile 线程环境下内存可见性 / 有序性
 */
public class NetworkDebugConfig {
    /**
     * 是否允许网络调试
     * <p>
     * 线上务必置为 false
     */
    public static volatile boolean isEnableNetworkDebug = BuildConfig.DEBUG;
    /**
     * 是否模拟断网
     * <p>
     * 触发 UnknownHostException / ConnectException
     */
    public static volatile boolean mockDisconnect = false;
    /**
     * 是否模拟超时
     * <p>
     * 触发 SocketTimeoutException
     */
    public static volatile boolean mockTimeout = false;
    /**
     * 模拟 HTTP 编码
     * <p>
     * 如 500 服务器开小差、401 登录失效
     * <p>
     * 为 0 表示正常
     * 非 0 表示强制返回该状态码
     */
    public static volatile int mockHttpCode = 0;

    /**
     * 重置
     */
    public static void reset() {
        mockDisconnect = false;
        mockTimeout = false;
        mockHttpCode = 0;
    }
}