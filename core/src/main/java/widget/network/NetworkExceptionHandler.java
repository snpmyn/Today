package widget.network;

import com.google.gson.JsonParseException;

import org.json.JSONException;

import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.HttpException;
import timber.log.Timber;

/**
 * Created on 2026/7/3.
 *
 * @author 郑少鹏
 * @desc 网络异常处理器
 */
public class NetworkExceptionHandler {
    /**
     * 处理异常
     *
     * @param throwable 异常
     *                  捕获的异常基类
     * @param <T>       泛型参数
     * @return 转化后的统一失败包裹体
     */
    public static <T> NetworkResult<T> handleException(Throwable throwable) {
        // 打印详细技术堆栈日志到控制台 / 日志文件
        throwable.printStackTrace();
        Timber.e(throwable);
        int code = 1000;
        String msg = "未知错误，请稍后再试";
        if (throwable instanceof SocketTimeoutException) {
            // 标准套接字连接 / 读取超时
            code = 1005;
            msg = "网络连接超时，请检查网络后重试";
        } else if (throwable instanceof InterruptedIOException) {
            // 拦截 OkHttp 底层常抛出的 InterruptedIOException
            code = 1005;
            msg = "网络请求超时，请检查网络后重试";
        } else if (throwable instanceof NullPointerException) {
            code = 1004;
            msg = "数据为空或解密失败，请检查密钥";
        } else if (throwable instanceof ConnectException || throwable instanceof UnknownHostException) {
            // 路由不可达、无网络、断网、DNS 解析失败
            code = 1003;
            msg = "无法连接到服务器，请检查网络状况";
        } else if (throwable instanceof JsonParseException || throwable instanceof JSONException) {
            // 数据解析结构、类型错配异常
            code = 1002;
            msg = "数据解析异常，请联系管理员";
        } else if (throwable instanceof HttpException) {
            // 拦截 Retrofit 包装的常规 HTTP 状态码异常 (如 404、500)
            HttpException httpException = (HttpException) throwable;
            code = httpException.code();
            msg = convertHttpCodeToMsg(code);
        } else {
            // 其它未知异常保留原生错误描述（或使用固定提示防御）
            msg = (throwable.getMessage() != null) ? throwable.getMessage() : msg;
        }
        return NetworkResult.error(code, msg);
    }

    /**
     * 映射 HTTP 编码到消息
     *
     * @param httpCode HTTP 编码
     * @return 消息
     */
    private static String convertHttpCodeToMsg(int httpCode) {
        switch (httpCode) {
            case 401:
                return "登录已失效，请重新登录";
            case 403:
                return "服务器拒绝请求，暂无权限";
            case 404:
                return "请求的资源不存在 (404)";
            case 500:
            case 502:
            case 503:
                return "服务器开小差了，请稍后再试 (500)";
            default:
                return "服务器响应异常 (" + httpCode + ")";
        }
    }
}