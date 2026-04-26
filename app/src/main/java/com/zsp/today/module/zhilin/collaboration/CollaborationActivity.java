package com.zsp.today.module.zhilin.collaboration;

import androidx.viewbinding.ViewBinding;

import com.zsp.today.databinding.ActivityCollaborationBinding;

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
        // 联动页配套原件
        collaborationActivityKit = new CollaborationActivityKit();
    }

    /**
     * 设置监听
     */
    @Override
    protected void setListener() {
        // 按钮选中监听
        activityCollaborationBinding.classificationActivityMbtg.addOnButtonCheckedListener((materialButtonToggleGroup, i, b) -> {
            if (b) {
                if (i == activityCollaborationBinding.classificationActivityMbQingHai.getId()) {
                    collaborationActivityKit.getClassificationAdapter().focus(1006, true);
                } else if (i == activityCollaborationBinding.classificationActivityMbXinJiang.getId()) {
                    collaborationActivityKit.getClassificationAdapter().focus(1016, true);
                } else if (i == activityCollaborationBinding.classificationActivityMbXiZang.getId()) {
                    collaborationActivityKit.getClassificationAdapter().focus(1030, true);
                } else if (i == activityCollaborationBinding.classificationActivityMbDisable.getId()) {
                    collaborationActivityKit.getClassificationAdapter().setChildItemClickEnable(false);
                }
            } else if (i == activityCollaborationBinding.classificationActivityMbDisable.getId()) {
                collaborationActivityKit.getClassificationAdapter().setChildItemClickEnable(true);
            } else {
                collaborationActivityKit.getClassificationAdapter().clearSelect();
            }
        });
    }

    /**
     * 开始逻辑
     */
    @Override
    protected void startLogic() {
        collaborationActivityKit.execute(this, activityCollaborationBinding);
    }
}