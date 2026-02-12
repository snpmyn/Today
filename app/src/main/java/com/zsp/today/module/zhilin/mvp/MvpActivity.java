package com.zsp.today.module.zhilin.mvp;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.button.MaterialButton;
import com.zsp.today.R;
import com.zsp.today.module.zhilin.mvp.contract.MvpActivityContract;

import pool.base.BasePoolActivity;
import widget.transition.kit.TransitionKit;

/**
 * Created on 2026/2/8.
 *
 * @author 郑少鹏
 * @desc MVP 页
 */
public class MvpActivity extends BasePoolActivity implements MvpActivityContract.MvpActivityView, View.OnClickListener {
    private TextView mvpActivityTv;
    private MaterialButton mvpActivityMb;
    private MvpActivityContract.MvpActivityPresenter mvpActivityPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        TransitionKit.getInstance().startPageSetting(this);
        super.onCreate(savedInstanceState);
    }

    /**
     * 布局资源 ID
     *
     * @return 布局资源 ID
     */
    @Override
    protected int layoutResId() {
        return R.layout.activity_mvp;
    }

    /**
     * 初始控件
     */
    @Override
    protected void stepUi() {
        mvpActivityTv = findViewById(R.id.mvpActivityTv);
        mvpActivityMb = findViewById(R.id.mvpActivityMb);
    }

    /**
     * 初始配置
     */
    @Override
    protected void initConfiguration() {
        mvpActivityPresenter = new MvpActivityContract.MvpActivityPresenter(this);
    }

    /**
     * 设置监听
     */
    @Override
    protected void setListener() {
        mvpActivityMb.setOnClickListener(this);
    }

    /**
     * 开始逻辑
     */
    @Override
    protected void startLogic() {

    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(@NonNull View v) {
        if (v.getId() == R.id.mvpActivityMb) {
            mvpActivityPresenter.request();
        }
    }

    /**
     * 更新文本
     *
     * @param text 文本
     */
    @Override
    public void updateText(String text) {
        mvpActivityTv.setText(text);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mvpActivityPresenter.detachView();
    }
}