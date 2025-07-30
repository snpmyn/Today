package litepal.crud.async;

import litepal.crud.callback.AverageCallback;

/**
 * Executor for average query in background.
 *
 * @author Tony Green
 * @since 2017/2/22
 */
public class AverageExecutor extends AsyncExecutor {
    private AverageCallback averageCallback;

    /**
     * Register a callback listener and async task will start executing right away.
     *
     * @param averageCallback Callback for average query in background.
     */
    public void listen(AverageCallback averageCallback) {
        this.averageCallback = averageCallback;
        execute();
    }

    public AverageCallback getListener() {
        return averageCallback;
    }
}