package com.zsp.today.module.zhilin.interview;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.zsp.today.R;
import com.zsp.today.module.zhilin.interview.kit.InterviewActivityKit;

import pool.base.BasePoolActivity;
import widget.floatingactionbutton.DraggableFloatingActionButton;
import widget.floatingactionbutton.kit.DraggableFloatingActionButtonKit;
import widget.transition.kit.TransitionKit;

/**
 * Created on 2026/1/25.
 *
 * @author 郑少鹏
 * @desc 面试页
 */
public class InterviewActivity extends BasePoolActivity {
    private MaterialToolbar interviewActivityMt;
    private RecyclerView interviewActivityRv;
    private DraggableFloatingActionButton interviewActivityDfab;
    /**
     * 面试页配套原件
     */
    private InterviewActivityKit interviewActivityKit;

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
        return R.layout.activity_interview;
    }

    /**
     * 初始控件
     */
    @Override
    protected void stepUi() {
        interviewActivityMt = findViewById(R.id.interviewActivityMt);
        interviewActivityRv = findViewById(R.id.interviewActivityRv);
        interviewActivityDfab = findViewById(R.id.interviewActivityDfab);
        DraggableFloatingActionButtonKit.execute(interviewActivityDfab);
    }

    /**
     * 初始配置
     */
    @Override
    protected void initConfiguration() {
        interviewActivityKit = new InterviewActivityKit();
    }

    /**
     * 设置监听
     */
    @Override
    protected void setListener() {
        // MaterialToolbar
        interviewActivityMt.setOnMenuItemClickListener(item -> {
            interviewActivityKit.showInterviewPointList(InterviewActivity.this, interviewActivityRv);
            return true;
        });
        // FloatingActionButton
        interviewActivityDfab.setOnClickListener(v -> interviewActivityKit.showInterviewPointList(InterviewActivity.this, interviewActivityRv));
    }

    /**
     * 开始逻辑
     */
    @Override
    protected void startLogic() {
        interviewActivityKit.carousel(interviewActivityRv);
    }
}