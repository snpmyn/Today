package util.activity;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import timber.log.Timber;
import util.list.ListUtils;

/**
 * Created on 2017/9/19.
 *
 * @author 郑少鹏
 * @desc 活动监督管理器
 * 使用一
 * {@link Application#registerActivityLifecycleCallbacks(Application.ActivityLifecycleCallbacks)} 之 onActivityCreated 当 {@link AppCompatActivity#onCreate(Bundle, PersistableBundle)} 时执行，已存在的 android:launchMode="singleTask" 实例被复用时不执行 onCreate。
 * {@link Application#registerActivityLifecycleCallbacks(Application.ActivityLifecycleCallbacks)} 之 onActivityDestroyed 当 {@link AppCompatActivity#finish()} 时执行。
 * <p>
 * 使用二
 * 基类之 {@link AppCompatActivity#onCreate(Bundle, PersistableBundle)} 推当前 Activity 至 Activity 管理容器，需时遍历容器并 finish 所有 Activity。
 */
public class ActivitySuperviseManager {
    /**
     * 活动列表
     * <p>
     * ArrayList 提升连续内存寻址性能
     * 配合全同步锁保证安全
     */
    private final List<Activity> ACTIVITIES = Collections.synchronizedList(new ArrayList<>());

    /**
     * 获取单例
     *
     * @return 单例
     */
    public static ActivitySuperviseManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * 推 Activity 至堆栈
     *
     * @param activity Activity
     */
    public void pushActivity(Activity activity) {
        if (null == activity) {
            return;
        }
        synchronized (ACTIVITIES) {
            ACTIVITIES.add(activity);
            Timber.d("推入 - %s", activity.getClass().getSimpleName());
            Timber.d("活动数 - %s", ACTIVITIES.size());
            for (int i = 0; i < ACTIVITIES.size(); i++) {
                Activity item = ACTIVITIES.get(i);
                if (null != item) {
                    Timber.d("概览 - %s", item.getClass().getSimpleName());
                }
            }
        }
    }

    /**
     * 从堆栈去 Activity
     *
     * @param activity Activity
     */
    public void removeActivity(Activity activity) {
        if (null == activity) {
            return;
        }
        synchronized (ACTIVITIES) {
            ACTIVITIES.remove(activity);
            Timber.d("去除 - %s", activity.getClass().getSimpleName());
            Timber.d("活动数 - %s", ACTIVITIES.size());
            for (int i = 0; i < ACTIVITIES.size(); i++) {
                Activity item = ACTIVITIES.get(i);
                if (null != item) {
                    Timber.d("概览 - %s", item.getClass().getSimpleName());
                }
            }
        }
    }

    /**
     * 当前 Activity 名
     * <p>
     * info.topActivity.getShortClassName() Activity 名
     * info.topActivity.getClassName() 类名
     * info.topActivity.getPackageName() 包名
     * info.topActivity.getClass() 类实例
     *
     * @return 当前 Activity 名
     */
    public String getCurrentRunningActivityName() {
        Activity topActivityInstance = getTopActivityInstance();
        String currentRunningActivityName = (null != topActivityInstance) ? topActivityInstance.getClass().getSimpleName() : null;
        Timber.d("当前活动名 - %s", String.valueOf(currentRunningActivityName));
        return currentRunningActivityName;
    }

    /**
     * 栈顶 Activity 实例
     *
     * @return 栈顶 Activity 实例
     */
    public @Nullable Activity getTopActivityInstance() {
        Activity topActivityInstance;
        synchronized (ACTIVITIES) {
            final int size = (ACTIVITIES.size() - 1);
            if (size < 0) {
                return null;
            }
            topActivityInstance = ACTIVITIES.get(size);
        }
        return topActivityInstance;
    }

    /**
     * 结束指定 Activity
     *
     * @param activity Activity
     */
    private void finishActivity(Activity activity) {
        if (ListUtils.listIsEmpty(ACTIVITIES)) {
            return;
        }
        if (null != activity) {
            Timber.d("结束 - %s", activity.getClass().getSimpleName());
            ACTIVITIES.remove(activity);
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    /**
     * 结束指定类名 Activity
     *
     * @param cls Class<?>
     */
    public void finishActivity(Class<?> cls) {
        if (ListUtils.listIsEmpty(ACTIVITIES) || (null == cls)) {
            return;
        }
        synchronized (ACTIVITIES) {
            Iterator<Activity> iterator = ACTIVITIES.iterator();
            while (iterator.hasNext()) {
                Activity activity = iterator.next();
                if ((null != activity) && activity.getClass().equals(cls)) {
                    Timber.d("结束 - %s", activity.getClass().getSimpleName());
                    iterator.remove();
                    if (!activity.isFinishing()) {
                        activity.finish();
                    }
                }
            }
        }
    }

    /**
     * 结束所有 Activity
     */
    public void finishAllActivity() {
        synchronized (ACTIVITIES) {
            for (Activity activity : ACTIVITIES) {
                if (null != activity && !activity.isFinishing()) {
                    activity.finish();
                }
            }
            ACTIVITIES.clear();
        }
    }

    private static final class InstanceHolder {
        static final ActivitySuperviseManager INSTANCE = new ActivitySuperviseManager();
    }
}