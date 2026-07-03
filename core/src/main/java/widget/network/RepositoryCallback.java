package widget.network;

/**
 * Created on 2026/7/3.
 *
 * @author 郑少鹏
 * @desc 仓库回调
 * <p>
 * T - 预期解析后核心业务数据类型
 */
public interface RepositoryCallback<T> {
    /**
     * 结果
     * <p>
     * 网络或业务请求结束后的统一结果回调
     *
     * @param networkResult 网络结果
     *                      包含成功 / 失败状态、状态码及数据的统一包裹体
     */
    void onResult(NetworkResult<T> networkResult);
}