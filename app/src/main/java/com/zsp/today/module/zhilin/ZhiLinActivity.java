package com.zsp.today.module.zhilin;

import android.view.MenuItem;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.zsp.today.R;
import com.zsp.today.module.zhilin.kit.ZhiLinActivityKit;

import org.jetbrains.annotations.NotNull;

import pool.base.BasePoolActivity;
import widget.status.kit.StatusManagerKit;
import widget.status.manager.StatusManager;

/**
 * Created on 2025/9/27.
 *
 * @author 郑少鹏
 * @desc 知林页
 */
public class ZhiLinActivity extends BasePoolActivity {
    private MaterialToolbar zhiLinActivityMt;
    private RecyclerView zhiLinActivityRv;
    /**
     * 状态管理器
     */
    private StatusManager statusManager;
    /**
     * 知林页配套原件
     */
    private ZhiLinActivityKit zhiLinActivityKit;

    /**
     * 布局资源 ID
     *
     * @return 布局资源 ID
     */
    @Override
    protected int layoutResId() {
        return R.layout.activity_zhi_lin;
    }

    /**
     * 初始控件
     */
    @Override
    protected void stepUi() {
        // MaterialToolbar
        zhiLinActivityMt = findViewById(R.id.zhiLinActivityMt);
        // RecyclerView
        zhiLinActivityRv = findViewById(R.id.zhiLinActivityRv);
    }

    /**
     * 初始配置
     */
    @Override
    protected void initConfiguration() {
        // 状态管理器
        statusManager = StatusManagerKit.generate(this, zhiLinActivityRv, new StatusManagerKit.StatusManagerKitListener() {
            @Override
            public void noNetwork() {

            }

            @Override
            public void connectFail() {

            }

            @Override
            public void loadFail() {

            }
        });
        // 知林页配套原件
        zhiLinActivityKit = new ZhiLinActivityKit();
    }

    /**
     * 设置监听
     */
    @Override
    protected void setListener() {
        zhiLinActivityMt.setOnMenuItemClickListener(item -> {
            menuItemClickToExecute(item);
            return true;
        });
    }

    /**
     * 开始逻辑
     */
    @Override
    protected void startLogic() {
        zhiLinActivityKit.display(this, zhiLinActivityRv, 2, 48, 144, statusManager);
    }

    /**
     * 菜单条目点击执行
     *
     * @param menuItem 菜单条目
     */
    private void menuItemClickToExecute(@NotNull MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.zhiLinActivityTwoColumn) {
            // 两列
            zhiLinActivityRv.removeItemDecorationAt(0);
            zhiLinActivityKit.display(this, zhiLinActivityRv, 2, 48, 144, statusManager);
        } else if (itemId == R.id.zhiLinActivityThreeColumn) {
            // 三列
            zhiLinActivityRv.removeItemDecorationAt(0);
            zhiLinActivityKit.display(this, zhiLinActivityRv, 3, 48, 192, statusManager);
        }
    }
}