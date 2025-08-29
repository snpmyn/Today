package litepal.crud.async;

import litepal.crud.callback.FindCallback;

/**
 * Executor for find record in background.
 *
 * @author Tony Green
 * @since 2017/2/22
 */
public class FindExecutor<T> extends AsyncExecutor {
    private FindCallback<T> findCallback;

    /**
     * Register a callback listener and async task will start executing right away.
     *
     * @param findCallback Callback for find record in background.
     */
    public void listen(FindCallback<T> findCallback) {
        this.findCallback = findCallback;
        execute();
    }

    public FindCallback<T> getListener() {
        return findCallback;
    }
}