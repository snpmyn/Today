package mvp.callback;

/**
 * Created on 2020-09-08
 *
 * @author zsp
 * @desc 回调
 */
public interface CallBack<T> {
    /**
     * 结果
     *
     * @param result T
     */
    void onResult(T result);
}