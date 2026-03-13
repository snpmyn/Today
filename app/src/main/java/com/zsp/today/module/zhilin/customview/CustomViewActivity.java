package com.zsp.today.module.zhilin.customview;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.zsp.today.R;
import com.zsp.today.module.zhilin.customview.arc.MyArc;
import com.zsp.today.module.zhilin.customview.circle.MyCircle;
import com.zsp.today.module.zhilin.customview.kit.CustomViewActivityKit;
import com.zsp.today.module.zhilin.customview.line.MyLine;
import com.zsp.today.module.zhilin.customview.line.MyLines;
import com.zsp.today.module.zhilin.customview.oval.MyOval;
import com.zsp.today.module.zhilin.customview.path.MyPath;
import com.zsp.today.module.zhilin.customview.point.MyPoint;
import com.zsp.today.module.zhilin.customview.rect.MyRect;
import com.zsp.today.module.zhilin.customview.rect.MyRoundRect;
import com.zsp.today.module.zhilin.customview.text.MyText;

import pool.base.BasePoolActivity;
import widget.floatingactionbutton.DraggableFloatingActionButton;
import widget.floatingactionbutton.kit.DraggableFloatingActionButtonKit;
import widget.transition.kit.TransitionKit;

/**
 * Created on 2026/2/7.
 *
 * @author 郑少鹏
 * @desc 自定义视图页
 */
public class CustomViewActivity extends BasePoolActivity {
    private MyArc customViewActivityMyArc;
    private MyCircle customViewActivityMyCircle;
    private MyLine customViewActivityMyLine;
    private MyLines customViewActivityMyLines;
    private MyOval customViewActivityMyOval;
    private MyPath customViewActivityMyPath;
    private MyPoint customViewActivityMyPoint;
    private MyRect customViewActivityMyRect;
    private MyRoundRect customViewActivityMyRoundRect;
    private MyText customViewActivityMyText;
    private DraggableFloatingActionButton customViewActivityDfab;
    /**
     * 自定义视图页配套原件
     */
    private CustomViewActivityKit customViewActivityKit;

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
        return R.layout.activity_custom_view;
    }

    /**
     * 初始控件
     */
    @Override
    protected void stepUi() {
        customViewActivityMyArc = findViewById(R.id.customViewActivityMyArc);
        customViewActivityMyCircle = findViewById(R.id.customViewActivityMyCircle);
        customViewActivityMyLine = findViewById(R.id.customViewActivityMyLine);
        customViewActivityMyLines = findViewById(R.id.customViewActivityMyLines);
        customViewActivityMyOval = findViewById(R.id.customViewActivityMyOval);
        customViewActivityMyPath = findViewById(R.id.customViewActivityMyPath);
        customViewActivityMyPoint = findViewById(R.id.customViewActivityMyPoint);
        customViewActivityMyRect = findViewById(R.id.customViewActivityMyRect);
        customViewActivityMyRoundRect = findViewById(R.id.customViewActivityMyRoundRect);
        customViewActivityMyText = findViewById(R.id.customViewActivityMyText);
        customViewActivityDfab = findViewById(R.id.customViewActivityDfab);
        DraggableFloatingActionButtonKit.execute(customViewActivityDfab);
    }

    /**
     * 初始配置
     */
    @Override
    protected void initConfiguration() {
        customViewActivityKit = new CustomViewActivityKit(customViewActivityMyArc, customViewActivityMyCircle, customViewActivityMyLine, customViewActivityMyLines, customViewActivityMyOval, customViewActivityMyPath, customViewActivityMyPoint, customViewActivityMyRect, customViewActivityMyRoundRect, customViewActivityMyText);
    }

    /**
     * 设置监听
     */
    @Override
    protected void setListener() {
        customViewActivityDfab.setOnClickListener(v -> customViewActivityKit.execute(CustomViewActivity.this));
    }

    /**
     * 开始逻辑
     */
    @Override
    protected void startLogic() {
        customViewActivityKit.visibleAndGoneCustomView(customViewActivityMyArc);
    }
}