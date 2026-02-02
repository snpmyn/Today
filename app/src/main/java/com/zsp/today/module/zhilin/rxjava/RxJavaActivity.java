package com.zsp.today.module.zhilin.rxjava;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.button.MaterialButton;
import com.zsp.today.R;
import com.zsp.today.module.zhilin.rxjava.kit.RxJavaActivityKit;

import pool.base.BasePoolActivity;
import widget.transition.kit.TransitionKit;

/**
 * Created on 2026/1/31.
 *
 * @author 郑少鹏
 * @desc 响应式异步框架页
 */
public class RxJavaActivity extends BasePoolActivity implements View.OnClickListener {
    private MaterialButton rxJavaActivityExampleOne;
    private MaterialButton rxJavaActivityExampleTwo;
    private MaterialButton rxJavaActivityExampleThree;
    private MaterialButton rxJavaActivityExampleFour;
    private MaterialButton rxJavaActivityExampleFive;
    private MaterialButton rxJavaActivityExampleSix;
    private MaterialButton rxJavaActivityExampleSeven;
    /**
     * 响应式异步框架页配套原件
     */
    private RxJavaActivityKit rxJavaActivityKit;

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
        return R.layout.activity_rx_java;
    }

    /**
     * 初始控件
     */
    @Override
    protected void stepUi() {
        rxJavaActivityExampleOne = findViewById(R.id.rxJavaActivityExampleOne);
        rxJavaActivityExampleTwo = findViewById(R.id.rxJavaActivityExampleTwo);
        rxJavaActivityExampleThree = findViewById(R.id.rxJavaActivityExampleThree);
        rxJavaActivityExampleFour = findViewById(R.id.rxJavaActivityExampleFour);
        rxJavaActivityExampleFive = findViewById(R.id.rxJavaActivityExampleFive);
        rxJavaActivityExampleSix = findViewById(R.id.rxJavaActivityExampleSix);
        rxJavaActivityExampleSeven = findViewById(R.id.rxJavaActivityExampleSeven);
    }

    /**
     * 初始配置
     */
    @Override
    protected void initConfiguration() {
        rxJavaActivityKit = new RxJavaActivityKit();
    }

    /**
     * 设置监听
     */
    @Override
    protected void setListener() {
        rxJavaActivityExampleOne.setOnClickListener(this);
        rxJavaActivityExampleTwo.setOnClickListener(this);
        rxJavaActivityExampleThree.setOnClickListener(this);
        rxJavaActivityExampleFour.setOnClickListener(this);
        rxJavaActivityExampleFive.setOnClickListener(this);
        rxJavaActivityExampleSix.setOnClickListener(this);
        rxJavaActivityExampleSeven.setOnClickListener(this);
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
        int viewId = v.getId();
        if (viewId == R.id.rxJavaActivityExampleOne) {
            rxJavaActivityKit.exampleOne();
        } else if (viewId == R.id.rxJavaActivityExampleTwo) {
            rxJavaActivityKit.exampleTwo();
        } else if (viewId == R.id.rxJavaActivityExampleThree) {
            rxJavaActivityKit.exampleThree();
        } else if (viewId == R.id.rxJavaActivityExampleFour) {
            rxJavaActivityKit.exampleFour();
        } else if (viewId == R.id.rxJavaActivityExampleFive) {
            rxJavaActivityKit.exampleFive();
        } else if (viewId == R.id.rxJavaActivityExampleSix) {
            rxJavaActivityKit.exampleSix();
        } else if (viewId == R.id.rxJavaActivityExampleSeven) {
            rxJavaActivityKit.exampleSeven();
        }
    }
}