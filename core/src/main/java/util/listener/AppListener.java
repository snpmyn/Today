package util.listener;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashSet;
import java.util.Set;

import util.log.LogUtils;

/**
 * Created on 2020-09-10
 *
 * @author zsp
 * @desc 应用监听
 */
public class AppListener {
    private final String TAG = this.getClass().getSimpleName();
    private final Set<Callback> callbackSet = new HashSet<>();
    private boolean hasInitConfiguration = false;
    private boolean areForeground = false;

    public static AppListener getInstance() {
        return AppListenerHolder.APP_LISTENER;
    }

    public void initConfiguration(Application application) {
        if (hasInitConfiguration) {
            return;
        }
        hasInitConfiguration = true;
        application.registerActivityLifecycleCallbacks(new ActivityLifecycle());
    }

    /**
     * 是否前台
     *
     * @return 是否前台
     */
    public boolean areForeground() {
        return areForeground;
    }

    /**
     * 唤醒前台
     *
     * @param areForeground 是否前台
     */
    private void notifyForeground(boolean areForeground) {
        if (this.areForeground == areForeground) {
            return;
        }
        this.areForeground = areForeground;
        for (Callback callback : callbackSet) {
            callback.onStateChange(areForeground);
        }
        LogUtils.d(TAG, "唤醒前台 " + areForeground);
    }

    /**
     * 注册回调
     *
     * @param callback 回调
     */
    public void registerCallback(Callback callback) {
        callbackSet.add(callback);
    }

    public interface Callback {
        /**
         * 状态变化
         *
         * @param areForeground 是否前台
         */
        void onStateChange(boolean areForeground);
    }

    private static final class AppListenerHolder {
        static final AppListener APP_LISTENER = new AppListener();
    }

    private class ActivityLifecycle implements Application.ActivityLifecycleCallbacks {
        private final Set<Activity> activitySet = new HashSet<>();

        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
            if (activitySet.isEmpty() && !areForeground) {
                notifyForeground(true);
                LogUtils.d(TAG, "启动 " + activity.getClass().getSimpleName());
            }
        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {

        }

        @Override
        public void onActivityResumed(@NonNull Activity activity) {
            activitySet.add(activity);
            if (!areForeground) {
                notifyForeground(true);
            }
        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {

        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {
            activitySet.remove(activity);
            if (activitySet.isEmpty()) {
                notifyForeground(false);
            }
        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {

        }
    }
}