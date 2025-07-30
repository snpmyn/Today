package litepal.crud.async;

import litepal.crud.callback.FindMultiCallback;

/**
 * Executor for find multiple records in background.
 *
 * @author Tony Green
 * @since 2017/2/22
 */
public class FindMultiExecutor<T> extends AsyncExecutor {
    private FindMultiCallback<T> findMultiCallback;

    /**
     * Register a callback listener and async task will start executing right away.
     *
     * @param findMultiCallback Callback for find multiple records in background.
     */
    public void listen(FindMultiCallback<T> findMultiCallback) {
        this.findMultiCallback = findMultiCallback;
        execute();
    }

    public FindMultiCallback<T> getListener() {
        return findMultiCallback;
    }
}
