package widget.network;

import android.os.Handler;
import android.os.Looper;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

/**
 * Created on 2026/7/3.
 *
 * @author 郑少鹏
 * @desc 仓库基类
 * <p>
 * 在 OkHttp 底层实现中，response.body().string() 的内部逻辑非常特殊。
 * 它在读取完底层 Socket 流并将其转换为字符串后，会在其内部 finally 块中自动隐式调用 close()。
 * <p>
 * 在 OkHttp 中，response.body() 对应的实例（即 ResponseBody）通常只能被消费一次。
 * 某些 OkHttp 版本或特定环境在 string() 被调用、流被释放后，后续再次调用 response.body() 可能会直接返回 null。
 * 如果返回 null，你在 finally 块里写 response.body().close()，就会变成 null.close()，从而直接导致应用崩溃。
 */
public abstract class BaseRepository {
    /**
     * API 服务
     * <p>
     * 统一暴露给子类直接用
     * 避免每个仓库单独创建
     */
    protected final ApiService apiService;

    /**
     * constructor
     */
    protected BaseRepository() {
        this.apiService = NetworkClient.create(ApiService.class);
    }

    /**
     * 是否成功
     * <p>
     * 子类有特殊需求可重写此方法
     *
     * @param code 编码
     * @return 是否成功
     */
    protected boolean isSuccess(int code) {
        return code == 200;
    }

    /**
     * 网络回调
     * <p>
     * 统一网络响应回调抽象内部基类
     *
     * @param <T> 预期解析后核心业务数据类型
     */
    public static abstract class NetworkCallback<T> implements Callback<ResponseBody> {
        /**
         * 线程消息调度器
         * <p>
         * 引入全局主线程 Handler
         * 将结果安全分发给 UI 线程
         */
        private static final Handler handler = new Handler(Looper.getMainLooper());
        /**
         * 仓库回调
         */
        private final RepositoryCallback<T> repositoryCallback;

        /**
         * constructor
         *
         * @param repositoryCallback 仓库回调
         */
        public NetworkCallback(RepositoryCallback<T> repositoryCallback) {
            this.repositoryCallback = repositoryCallback;
        }

        /**
         * 解析数据
         * <p>
         * 由子类实现具体的解密与反序列化逻辑
         *
         * @param dataJsonString 未解密或原始的数据字符串
         * @return 转换后的目标业务实体或集合
         * @throws Exception 异常
         *                   抛出解析或解密期间的异常
         */
        protected abstract T onParseData(String dataJsonString) throws Exception;

        @Override
        public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
            // OkHttp 的 onResponse 默认在工作线程（子线程）执行
            // 严谨判断：HTTP 响应码非 200 ~ 300 (如 404、500)
            if (!response.isSuccessful()) {
                // 先利用 response 包装异常
                NetworkResult<T> errorResult = NetworkExceptionHandler.handleException(new HttpException(response));
                // 接着强制关闭释放错误包体流
                // 防止 Socket 挂起泄漏
                if (response.errorBody() != null) {
                    response.errorBody().close();
                }
                // 最后通过解耦的回调吐给上层
                postResult(errorResult);
                return;
            }
            // 拦截包体为空的极端边界
            if (response.body() == null) {
                postResult(NetworkResult.error(1001, "服务器未返回有效数据"));
                return;
            }
            try {
                // 在子线程中进行耗时的 I/O 读取
                // 读取原始密文 JSON 外壳
                // string() 方法执行后
                // OkHttp 内部会自动将流彻底关闭
                // 无需编写 finally {} 显式 close
                String raw = response.body().string();
                JSONObject jsonResult = new JSONObject(raw);
                int code = jsonResult.getInt("code");
                // 业务层状态码成功判断
                if (code == 200) {
                    // 提取 data 节点
                    // 交由子类具体的解析器去解密 / 反序列化
                    // 在工作线程中执行 AES 解密和 Gson 大文本反序列化，彻底避免主线程卡顿。
                    T resultData = onParseData(jsonResult.optString("data"));
                    postResult(NetworkResult.success(resultData));
                } else {
                    // 业务状态码失败分支
                    postResult(NetworkResult.error(code, jsonResult.optString("msg", "请求失败")));
                }
            } catch (Exception e) {
                // 捕获解密异常或解析异常
                // 统一托管给异常转换器
                postResult(NetworkExceptionHandler.handleException(e));
            }
        }

        @Override
        public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
            // 如果请求是被外部主动取消（如页面关闭）
            // 此处不触发任何更新
            if (call.isCanceled()) {
                return;
            }
            // 断网、超时等原生错误进入异常转换器
            postResult(NetworkExceptionHandler.handleException(t));
        }

        /**
         * 发送结果
         * <p>
         * 将结果安全分发给 UI 线程
         *
         * @param networkResult 网络结果
         */
        private void postResult(final NetworkResult<T> networkResult) {
            handler.post(() -> repositoryCallback.onResult(networkResult));
        }
    }
}