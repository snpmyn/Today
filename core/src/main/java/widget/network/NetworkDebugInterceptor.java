package widget.network;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created on 2026/7/3.
 *
 * @author 郑少鹏
 * @desc 网络调试拦截器
 */
public class NetworkDebugInterceptor implements Interceptor {
    @Override
    public @NotNull Response intercept(@NotNull Chain chain) throws IOException {
        // 未允许网络调试则直接放行
        if (!NetworkDebugConfig.isEnableNetworkDebug) {
            return chain.proceed(chain.request());
        }
        // 模拟断网
        if (NetworkDebugConfig.mockDisconnect) {
            // 抛出路由不可达异常
            // 触发 NetworkExceptionHandler 中的 "无法连接到服务器" 提示
            throw new ConnectException("Debug Mock: Network disconnected.");
        }
        // 模拟超时
        if (NetworkDebugConfig.mockTimeout) {
            // 抛出超时异常
            // 触发 NetworkExceptionHandler 中的 "网络连接超时" 提示
            throw new SocketTimeoutException("Debug Mock: Network timeout.");
        }
        // 模拟服务器异常（401、404、500 等）
        if (NetworkDebugConfig.mockHttpCode != 0) {
            int errorCode = NetworkDebugConfig.mockHttpCode;
            ResponseBody mockBody = ResponseBody.create("{}", MediaType.parse("application/json; charset=utf-8"));
            // 伪造一个对应错误编码的空响应体返回给 Retrofit
            return new Response.Builder()
                    .request(chain.request())
                    .protocol(Protocol.HTTP_1_1)
                    .code(errorCode)
                    .message("Debug Mock Error")
                    .body(mockBody)
                    .build();
        }
        // 没命中任何标志则正常发起请求
        return chain.proceed(chain.request());
    }
}