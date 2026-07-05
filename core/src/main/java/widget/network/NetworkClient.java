package widget.network;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created on 2026/7/2.
 *
 * @author 郑少鹏
 * @desc 网络客户端
 * <p>
 * DCL 双重检查锁
 * volatile 禁止指令重排序
 */
public class NetworkClient {
    /**
     * Retrofit
     */
    private static volatile Retrofit retrofit;

    /**
     * 获取单例
     *
     * @return 单例
     */
    public static Retrofit getInstance() {
        if (retrofit == null) {
            synchronized (NetworkClient.class) {
                if (retrofit == null) {
                    // HTTP 日志拦截器
                    HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
                    httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
                    // 网络调试拦截器
                    NetworkDebugInterceptor networkDebugInterceptor = new NetworkDebugInterceptor();
                    // OkHttpClient
                    // HTTPS 在此自动处理
                    OkHttpClient okHttpClient = new OkHttpClient.Builder()
                            .connectTimeout(60, TimeUnit.SECONDS) // 连接超时
                            .readTimeout(60, TimeUnit.SECONDS) // 读取超时
                            .writeTimeout(60, TimeUnit.SECONDS) // 写入超时
                            .retryOnConnectionFailure(true) // 失败重试
                            .addInterceptor(httpLoggingInterceptor) // 添加拦截器
                            .addInterceptor(networkDebugInterceptor) // 添加拦截器
                            .build();
                    retrofit = new Retrofit.Builder()
                            .baseUrl(RetrofitClient.getBaseUrl()) // 基础 URL
                            .client(okHttpClient)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                }
            }
        }
        return retrofit;
    }

    /**
     * 创建
     *
     * @param service Class<T>
     * @param <T>     T
     * @return T
     */
    public static <T> T create(Class<T> service) {
        return getInstance().create(service);
    }
}