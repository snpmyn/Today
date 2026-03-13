package com.zsp.today.module.function;

import androidx.recyclerview.widget.RecyclerView;

import com.zsp.today.R;
import com.zsp.today.module.function.kit.FunctionActivityKit;

import pool.base.BasePoolActivity;

/**
 * @desc: 功能页
 * @author: zsp
 * @date: 2021/12/2 4:23 下午
 */
public class FunctionActivity extends BasePoolActivity {
    private RecyclerView functionActivityRv;
    /**
     * 功能页配套元件
     */
    private FunctionActivityKit functionActivityKit;

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
        return R.layout.activity_function;
    }

    /**
     * 初始控件
     */
    @Override
    protected void stepUi() {
        functionActivityRv = findViewById(R.id.functionActivityRv);
    }

    /**
     * 初始配置
     */
    @Override
    protected void initConfiguration() {
        functionActivityKit = new FunctionActivityKit();
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
        functionActivityKit.display(this, functionActivityRv);
    }
}