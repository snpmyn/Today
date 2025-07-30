package litepal.crud.callback;

/**
 * Callback for find record in background.
 *
 * @author Tony Green
 * @since 2017/2/22
 */
public interface FindCallback<T> {
    void onFinish(T t);
}
