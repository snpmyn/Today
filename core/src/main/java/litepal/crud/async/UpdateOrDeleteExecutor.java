package litepal.crud.async;

import litepal.crud.callback.UpdateOrDeleteCallback;

/**
 * Executor for update or delete records in background.
 *
 * @author Tony Green
 * @since 2017/2/22
 */
public class UpdateOrDeleteExecutor extends AsyncExecutor {
    private UpdateOrDeleteCallback updateOrDeleteCallback;

    /**
     * Register a callback listener and async task will start executing right away.
     *
     * @param updateOrDeleteCallback Callback for update or delete records in background.
     */
    public void listen(UpdateOrDeleteCallback updateOrDeleteCallback) {
        this.updateOrDeleteCallback = updateOrDeleteCallback;
        execute();
    }

    public UpdateOrDeleteCallback getListener() {
        return updateOrDeleteCallback;
    }
}