package com.zsp.today.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.zsp.today.BuildConfig;
import com.zsp.today.R;
import com.zsp.today.basic.service.PeriodicService;
import com.zsp.today.basic.service.PeriodicServiceConnection;
import com.zsp.today.basic.value.RxBusConstant;
import com.zsp.today.main.kit.MainActivityKit;

import org.jetbrains.annotations.NotNull;

import pool.base.BasePoolActivity;
import util.log.LogUtils;
import util.rxbus.annotation.Subscribe;
import util.rxbus.annotation.Tag;
import util.rxbus.thread.EventThread;
import widget.notification.kit.NotificationKit;
import widget.service.kit.ServiceKit;
import widget.transition.kit.TransitionKit;

/**
 * @decs: 主页
 * @author: 郑少鹏
 * @date: 2025/7/30 11:18
 * @version: v 1.0
 */
public class MainActivity extends BasePoolActivity {
    private ViewPager2 mainActivityVp2;
    private BottomNavigationView mainActivityBnv;
    /**
     * 主页配套元件
     */
    private MainActivityKit mainActivityKit;
    /**
     * 周期服务连接
     */
    private PeriodicServiceConnection periodicServiceConnection;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        TransitionKit.getInstance().startPageSetting(this);
        super.onCreate(savedInstanceState);
    }

    /**
     * 布局资源 ID
     * <p>
     * Java 动态绑定
     * Java 运行时多态
     * Java 动态分派机制
     * <p>
     * 如果子类重写 layoutResId()
     * 那么 onCreate() 中调用时会优先执行子类的方法
     *
     * @return 布局资源 ID
     */
    @Override
    protected int layoutResId() {
        return R.layout.activity_main;
    }

    /**
     * 初始控件
     */
    @Override
    protected void stepUi() {
        mainActivityVp2 = findViewById(R.id.mainActivityVp2);
        mainActivityBnv = findViewById(R.id.mainActivityBnv);
        mainActivityBnv.setItemIconTintList(null);
    }

    /**
     * 初始配置
     */
    @Override
    protected void initConfiguration() {
        // 主页配套元件
        mainActivityKit = new MainActivityKit();
        // 周期服务连接
        periodicServiceConnection = new PeriodicServiceConnection(new PeriodicServiceConnection.PeriodicServiceConnectionListener() {
            @Override
            public void execute(int count) {
                LogUtils.d(getClass().getSimpleName(), String.valueOf(count));
            }
        });
    }

    /**
     * 设置监听
     */
    @Override
    protected void setListener() {
        // ViewPager2
        mainActivityVp2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                mainActivityBnv.getMenu().getItem(position).setChecked(true);
            }
        });
        // BottomNavigationView
        mainActivityBnv.setOnItemSelectedListener(menuItem -> {
            int itemId = menuItem.getItemId();
            if (itemId == com.zsp.core.R.id.bottomNavigationViewMenuHomePage) {
                mainActivityVp2.setCurrentItem(0, false);
            } else if (itemId == com.zsp.core.R.id.bottomNavigationViewMenuMine) {
                mainActivityVp2.setCurrentItem(1, false);
            }
            return true;
        });
    }

    /**
     * 开始逻辑
     */
    @Override
    protected void startLogic() {
        // 开始页面
        mainActivityKit.startPage(this, mainActivityVp2);
        // 执行
        mainActivityKit.execute(this);
        // 开始
        if (BuildConfig.DEBUG) {
            ServiceKit.getInstance().start(this, periodicServiceConnection, PeriodicService.class);
        }
    }

    @Subscribe(thread = EventThread.MAIN_THREAD, tags = {@Tag(RxBusConstant.MAIN_ACTIVITY_$_BOTTOM_NAVIGATION_VIEW)})
    public void mainActivityBottomNavigationView(@NotNull Integer integer) {
        switch (integer) {
            // 隐底导航视图
            case RxBusConstant.MAIN_ACTIVITY_$_HIDE_BOTTOM_NAVIGATION_VIEW_CODE:
                mainActivityKit.hideBottomNavigationView(mainActivityBnv);
                break;
            // 显底导航视图
            case RxBusConstant.MAIN_ACTIVITY_$_SHOW_BOTTOM_NAVIGATION_VIEW_CODE:
                mainActivityKit.showBottomNavigationView(mainActivityBnv);
                break;
            default:
                break;
        }
    }

    /**
     * 活动结果
     * <p>
     * RESULT_CANCELED (值为 0)
     * 表示用户取消了操作或 Activity 没有成功完成任务
     * 通常当用户按下返回按钮或明确取消某个操作时使用
     * 这是默认的返回结果，如果不显式设置结果，系统会默认使用 RESULT_CANCELED。
     * <p>
     * RESULT_OK (值为 -1)
     * 表示操作成功完成
     * 当 Activity 成功执行了预期任务并返回数据时使用
     * <p>
     * 用户取消操作：当用户主动取消操作（如点击取消按钮或按返回键）时，应该返回 RESULT_CANCELED。
     * 操作未完成：当 Activity 没有完成其主要任务时返回
     * 默认行为：如果没有显式设置结果，Android 会自动使用 RESULT_CANCELED。
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     *                    (various data can be attached to Intend "extras").
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((resultCode == RESULT_CANCELED) && (requestCode == NotificationKit.NOTIFICATION_KIT_REQUEST_CODE)) {
            mainActivityKit.execute(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 结束
        ServiceKit.getInstance().end(this, periodicServiceConnection);
    }
}