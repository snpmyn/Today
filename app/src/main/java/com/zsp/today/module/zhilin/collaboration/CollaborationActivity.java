package com.zsp.today.module.zhilin.collaboration;

import androidx.viewbinding.ViewBinding;

import com.zsp.today.databinding.ActivityCollaborationBinding;
import com.zsp.today.module.zhilin.collaboration.kit.CollaborationActivityKit;

import pool.base.BasePoolActivity;

/**
 * Created on 2026/4/24.
 *
 * @author 郑少鹏
 * @desc 联动页
 */
public class CollaborationActivity extends BasePoolActivity {
    /**
     * ActivityCollaborationBinding
     */
    private ActivityCollaborationBinding activityCollaborationBinding;
    /**
     * 联动页配套原件
     */
    private CollaborationActivityKit collaborationActivityKit;

    /**
     * ViewBinding
     * <p>
     * Java 动态绑定
     * Java 运行时多态
     * Java 动态分派机制
     * <p>
     * 如果子类重写 viewBinding()
     * 那么 onCreate() 中调用时会优先执行子类的方法
     *
     * @return ViewBinding
     */
    @Override
    protected ViewBinding viewBinding() {
        activityCollaborationBinding = ActivityCollaborationBinding.inflate(getLayoutInflater());
        return activityCollaborationBinding;
    }

    /**
     * 初始控件
     */
    @Override
    protected void stepUi() {

    }

    /**
     * 初始配置
     */
    @Override
    protected void initConfiguration() {
        collaborationActivityKit = new CollaborationActivityKit();
    }

    /**
     * 设置监听
     */
    @Override
    protected void setListener() {

    }

    /**
     * 开始逻辑
     */
    @Override
    protected void startLogic() {
        collaborationActivityKit.preHandle(this, activityCollaborationBinding);
    }
}