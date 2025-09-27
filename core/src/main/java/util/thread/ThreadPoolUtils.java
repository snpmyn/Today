package util.thread;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @decs: 线程池工具类
 * @author: 郑少鹏
 * @date: 2025/9/27 10:23
 * @version: v 1.0
 * <p>
 * 暂无引用
 */
public class ThreadPoolUtils {
    private final ThreadPoolExecutor threadPoolExecutor;
    private final ScheduledExecutorService scheduledExecutorService;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private ThreadPoolUtils() {
        // 核心线程数
        // CPU 核心数 + 1
        int corePoolSize = (Runtime.getRuntime().availableProcessors() + 1);
        // 最大线程数
        // 核心线程数 * 2 + 1
        int maxPoolSize = (corePoolSize * 2 + 1);
        // 阻塞队列
        BlockingQueue<Runnable> blockingQueue = new LinkedBlockingQueue<>(128);
        // 线程工厂
        ThreadFactory threadFactory = runnable -> new Thread(runnable, "ThreadPoolUtils-Thread");
        // 拒绝策略
        // 抛出异常
        ThreadPoolExecutor.AbortPolicy abortPolicy = new ThreadPoolExecutor.AbortPolicy();
        threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, 60L, TimeUnit.SECONDS, blockingQueue, threadFactory, abortPolicy);
        scheduledExecutorService = Executors.newScheduledThreadPool(corePoolSize, threadFactory);
    }

    public static ThreadPoolUtils getInstance() {
        return InstanceHolder.instance;
    }

    /**
     * 执行普通任务
     *
     * @param runnable Runnable
     */
    public void executeCommonTask(Runnable runnable) {
        threadPoolExecutor.execute(runnable);
    }

    /**
     * 执行延迟任务
     *
     * @param runnable      Runnable
     * @param delayDuration 延迟时长
     * @param timeUnit      时间单位
     *                      {@link TimeUnit#SECONDS}}
     * @return ScheduledFuture<?>
     */
    public ScheduledFuture<?> executeDelayTask(Runnable runnable, long delayDuration, TimeUnit timeUnit) {
        return scheduledExecutorService.schedule(runnable, delayDuration, timeUnit);
    }

    /**
     * 执行定时重复任务
     *
     * @param runnable             Runnable
     * @param initialDelayDuration 初次延迟时长
     * @param period               周期
     * @param timeUnit             时间单位
     *                             {@link TimeUnit#SECONDS}}
     * @return ScheduledFuture<?>
     */
    public ScheduledFuture<?> executeScheduledRecurringTask(Runnable runnable, long initialDelayDuration, long period, TimeUnit timeUnit) {
        return scheduledExecutorService.scheduleWithFixedDelay(runnable, initialDelayDuration, period, timeUnit);
    }

    /**
     * 提交任务
     * <p>
     * 提交一个带返回值的异步任务到线程池执行
     *
     * @param callable Callable<T>
     * @param <T>      <T>
     * @return Future<T>
     */
    public <T> Future<T> submitTask(Callable<T> callable) {
        return threadPoolExecutor.submit(callable);
    }

    /**
     * 在主线程执行任务
     *
     * @param runnable Runnable
     */
    public void executeTaskOnMainThread(Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            mainHandler.post(runnable);
        }
    }

    /**
     * 移除主线程任务
     *
     * @param runnable Runnable
     */
    public void removeMainThreadTask(Runnable runnable) {
        mainHandler.removeCallbacks(runnable);
    }

    /**
     * 关闭线程池
     */
    public void closeThreadPool() {
        threadPoolExecutor.shutdown();
        scheduledExecutorService.shutdown();
    }

    /**
     * 立即关闭线程池
     */
    public void closeThreadPoolImmediately() {
        threadPoolExecutor.shutdownNow();
        scheduledExecutorService.shutdownNow();
    }

    private static final class InstanceHolder {
        private static final ThreadPoolUtils instance = new ThreadPoolUtils();
    }
}