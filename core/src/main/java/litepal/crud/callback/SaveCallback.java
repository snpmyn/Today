package litepal.crud.callback;

/**
 * Callback for save records in background.
 *
 * @author Tony Green
 * @since 2017/2/22
 */
public interface SaveCallback {
    void onFinish(boolean success);
}