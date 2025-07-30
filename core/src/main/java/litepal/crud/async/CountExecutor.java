package litepal.crud.async;

import litepal.crud.callback.CountCallback;

/**
 * Executor for count query in background.
 *
 * @author Tony Green
 * @since 2017/2/22
 */
public class CountExecutor extends AsyncExecutor {
    private CountCallback countCallback;

    /**
     * Register a callback listener and async task will start executing right away.
     *
     * @param countCallback Callback for count query in background.
     */
    public void listen(CountCallback countCallback) {
        this.countCallback = countCallback;
        execute();
    }

    public CountCallback getListener() {
        return countCallback;
    }
}