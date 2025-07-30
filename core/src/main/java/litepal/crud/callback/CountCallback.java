package litepal.crud.callback;

/**
 * Callback for count query in background.
 *
 * @author Tony Green
 * @since 2017/2/22
 */
public interface CountCallback {
    void onFinish(int count);
}