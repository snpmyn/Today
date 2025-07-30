package litepal.crud.callback;

/**
 * Callback for average query in background.
 *
 * @author Tony Green
 * @since 2017/2/22
 */
public interface AverageCallback {
    void onFinish(double average);
}