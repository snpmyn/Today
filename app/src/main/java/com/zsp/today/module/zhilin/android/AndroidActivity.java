package com.zsp.today.module.zhilin.android;

import android.webkit.WebView;

import com.google.android.material.appbar.MaterialToolbar;
import com.zsp.today.R;
import com.zsp.today.module.zhilin.android.bean.AndroidArticleEnum;
import com.zsp.today.module.zhilin.android.kit.AndroidActivityKit;

import pool.base.BasePoolActivity;

/**
 * Created on 2025/9/28.
 *
 * @author 郑少鹏
 * @desc 安卓页
 */
public class AndroidActivity extends BasePoolActivity {
    private MaterialToolbar androidActivityMt;
    private WebView androidActivityWv;
    /**
     * 安卓页配套原件
     */
    private AndroidActivityKit androidActivityKit;

    /**
     * 布局资源 ID
     *
     * @return 布局资源 ID
     */
    @Override
    protected int layoutResId() {
        return R.layout.activity_android;
    }

    /**
     * 初始控件
     */
    @Override
    protected void stepUi() {
        // androidActivityMt
        androidActivityMt = findViewById(R.id.androidActivityMt);
        // WebView
        androidActivityWv = findViewById(R.id.androidActivityWv);
    }

    /**
     * 初始配置
     */
    @Override
    protected void initConfiguration() {
        androidActivityKit = new AndroidActivityKit();
    }

    /**
     * 设置监听
     */
    @Override
    protected void setListener() {
        androidActivityMt.setOnMenuItemClickListener(item -> {
            androidActivityKit.showAndroidArticleList(AndroidActivity.this, androidActivityWv);
            return true;
        });
    }

    /**
     * 开始逻辑
     */
    @Override
    protected void startLogic() {
        // 准备数据
        androidActivityKit.prepareData();
        // 设置标题
        androidActivityKit.setTitle(androidActivityWv, androidActivityMt);
        // 显示安卓文章内容
        androidActivityKit.showAndroidArticleContent(androidActivityWv, AndroidArticleEnum.OVERLOAD_AND_OVERRIDE.getArticleUrl());
    }
}