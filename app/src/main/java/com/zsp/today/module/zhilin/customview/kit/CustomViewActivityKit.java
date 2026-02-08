package com.zsp.today.module.zhilin.customview.kit;

import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.zsp.today.R;
import com.zsp.today.module.zhilin.customview.kit.arc.MyArc;
import com.zsp.today.module.zhilin.customview.kit.circle.MyCircle;
import com.zsp.today.module.zhilin.customview.kit.line.MyLine;
import com.zsp.today.module.zhilin.customview.kit.line.MyLines;
import com.zsp.today.module.zhilin.customview.kit.oval.MyOval;
import com.zsp.today.module.zhilin.customview.kit.path.MyPath;
import com.zsp.today.module.zhilin.customview.kit.point.MyPoint;
import com.zsp.today.module.zhilin.customview.kit.rect.MyRect;
import com.zsp.today.module.zhilin.customview.kit.rect.MyRoundRect;
import com.zsp.today.module.zhilin.customview.kit.text.MyText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import widget.screen.kit.ScreenHandleKit;
import widget.screen.listener.ScreenHandleListener;

/**
 * Created on 2026/2/7.
 *
 * @author 郑少鹏
 * @desc 自定义视图页配套原件
 */
public class CustomViewActivityKit {
    private String defaultSelectCondition;
    private final String[] conditionTemporary;
    private final List<String> stringList;
    private final List<View> viewList;
    private final Map<String, View> stringViewMap;

    /**
     * constructor
     *
     * @param myArc       MyArc
     * @param myCircle    MyCircle
     * @param myLine      MyLine
     * @param myLines     MyLines
     * @param myOval      MyOval
     * @param myPath      MyPath
     * @param myPoint     MyPoint
     * @param myRect      MyRect
     * @param myRoundRect MyRoundRect
     * @param myText      MyText
     */
    public CustomViewActivityKit(MyArc myArc, MyCircle myCircle, MyLine myLine, MyLines myLines, MyOval myOval, MyPath myPath, MyPoint myPoint, MyRect myRect, MyRoundRect myRoundRect, MyText myText) {
        this.conditionTemporary = new String[1];
        this.stringList = new ArrayList<>(10);
        this.viewList = new ArrayList<>(10);
        this.stringViewMap = new HashMap<>(10);
        addIfNotNull(myArc);
        addIfNotNull(myCircle);
        addIfNotNull(myLine);
        addIfNotNull(myLines);
        addIfNotNull(myOval);
        addIfNotNull(myPath);
        addIfNotNull(myPoint);
        addIfNotNull(myRect);
        addIfNotNull(myRoundRect);
        addIfNotNull(myText);
    }

    /**
     * 非空添加
     *
     * @param view 视图
     */
    private void addIfNotNull(View view) {
        if (null != view) {
            String simpleName = view.getClass().getSimpleName();
            stringList.add(simpleName);
            viewList.add(view);
            stringViewMap.put(simpleName, view);
        }
    }

    /**
     * 执行
     *
     * @param appCompatActivity 活动
     */
    public void execute(AppCompatActivity appCompatActivity) {
        // ScreenHandleKit
        ScreenHandleKit screenHandleKit = new ScreenHandleKit(appCompatActivity);
        // 打包集合条件
        screenHandleKit.packListConditions(appCompatActivity.getString(R.string.customView), 3, true, stringList);
        // 默选
        screenHandleKit.defaultSelect(appCompatActivity.getString(R.string.customView), TextUtils.isEmpty(defaultSelectCondition) ? stringList.get(0) : defaultSelectCondition);
        // 关联
        screenHandleKit.associate();
        // 设筛选操作监听
        screenHandleKit.setScreenHandleListener(new ScreenHandleListener() {
            @Override
            public void click(View view, String classification, String condition, boolean selected) {
                conditionTemporary[0] = condition;
            }

            @Override
            public void reset() {
                screenHandleKit.reset();
            }

            @Override
            public void ensure() {
                screenHandleKit.dismiss();
                defaultSelectCondition = conditionTemporary[0];
                visibleAndGoneCustomView(stringViewMap.get(defaultSelectCondition));
            }
        });
        // 显示
        screenHandleKit.show();
    }

    /**
     * 显示和隐藏自定义视图
     *
     * @param view 视图
     */
    public void visibleAndGoneCustomView(View view) {
        for (View v : viewList) {
            if (v == view) {
                v.setVisibility(View.VISIBLE);
            } else {
                v.setVisibility(View.GONE);
            }
        }
    }
}