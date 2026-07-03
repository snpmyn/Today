package widget.network;

/**
 * Created on 2026/7/2.
 *
 * @author 郑少鹏
 * @desc 网络结果
 */
public class NetworkResult<T> {
    /**
     * 是否成功
     */
    public boolean success;
    /**
     * 编码
     * <p>
     * 包含业务状态码与映射后的网络状态码
     */
    public int code;
    /**
     * 消息
     */
    public String message;
    /**
     * 数据
     * <p>
     * 承载解密后核心业务数据
     */
    public T data;

    /**
     * 成功
     *
     * @param data 数据
     * @param <T>  T
     * @return T
     */
    public static <T> NetworkResult<T> success(T data) {
        NetworkResult<T> networkResult = new NetworkResult<>();
        networkResult.success = true;
        networkResult.code = 200;
        networkResult.data = data;
        networkResult.message = "success";
        return networkResult;
    }

    /**
     * 失败
     *
     * @param code 编码
     * @param msg  消息
     * @param <T>  T
     * @return T
     */
    public static <T> NetworkResult<T> error(int code, String msg) {
        NetworkResult<T> networkResult = new NetworkResult<>();
        networkResult.success = false;
        networkResult.code = code;
        networkResult.message = msg;
        return networkResult;
    }
}