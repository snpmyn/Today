package litepal.crud.callback;

import java.util.List;

/**
 * Callback for find multiple records in background.
 *
 * @author Tony Green
 * @since 2017/2/22
 */
public interface FindMultiCallback<T> {
    void onFinish(List<T> list);
}