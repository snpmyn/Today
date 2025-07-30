package litepal.crud.callback;

/**
 * Callback for update or delete records in background.
 *
 * @author Tony Green
 * @since 2017/2/22
 */
public interface UpdateOrDeleteCallback {
    void onFinish(int rowsAffected);
}