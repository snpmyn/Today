package util.datetime;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created on 2019/9/21.
 *
 * @author 郑少鹏
 * @desc CurrentTimeMillisClock
 * 调 {@link CurrentTimeMillisClock#getInstance()#now()}
 */
public class CurrentTimeMillisClock {
    private volatile long now;

    private CurrentTimeMillisClock() {
        this.now = System.currentTimeMillis();
        scheduleTick();
    }

    public static CurrentTimeMillisClock getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private void scheduleTick() {
        new ScheduledThreadPoolExecutor(1, r -> {
            Thread thread = new Thread(r, "current-time-millis");
            thread.setDaemon(true);
            return thread;
        }).scheduleWithFixedDelay(() -> now = System.currentTimeMillis(), 1, 1, TimeUnit.MILLISECONDS);
    }

    public long now() {
        return now;
    }

    private static class SingletonHolder {
        private static final CurrentTimeMillisClock INSTANCE = new CurrentTimeMillisClock();
    }
}