package litepal.tablemanager.callback;

/**
 * Callback for listening database create and upgrade events.
 *
 * @author Tony Green
 * @since 2.0
 */
public interface DatabaseListener {
    void onCreate();

    void onUpgrade(int oldVersion, int newVersion);
}