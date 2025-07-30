package litepal.crud.async;

import litepal.crud.callback.SaveCallback;

/**
 * Executor for save records in background.
 *
 * @author Tony Green
 * @since 2017/2/22
 */
public class SaveExecutor extends AsyncExecutor {
    private SaveCallback saveCallback;

    /**
     * Register a callback listener and async task will start executing right away.
     *
     * @param saveCallback Callback for save records in background.
     */
    public void listen(SaveCallback saveCallback) {
        this.saveCallback = saveCallback;
        execute();
    }

    public SaveCallback getListener() {
        return saveCallback;
    }
}