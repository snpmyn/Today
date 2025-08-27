package util.timer;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created on 2025/8/25.
 *
 * @author 郑少鹏
 * @desc Timer 配套原件
 */
public class TimerKit {
    public static TimerKit getInstance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * 执行
     *
     * @param appCompatActivity 活动
     * @param delay             延时
     * @param timerListener     Timer 监听
     */
    public void execute(AppCompatActivity appCompatActivity, long delay, TimerListener timerListener) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                // 在后台线程执行
                // 切回主线程更新 UI
                appCompatActivity.runOnUiThread(timerListener::runOnUiThread);
            }
        }, delay);
    }

    /**
     * Timer 监听
     */
    public interface TimerListener {
        /**
         * 执行在 UI 线程
         */
        void runOnUiThread();
    }

    private static final class InstanceHolder {
        static final TimerKit INSTANCE = new TimerKit();
    }
}