package com.zsp.today.module.zhilin.collaboration.adapter;

/**
 * Created on 2026/4/23.
 *
 * @author 郑少鹏
 * @desc 列表条目
 */
public abstract class ListItem {
    /**
     * 头类型
     */
    public static final int TYPE_HEADER = 0;
    /**
     * 子类型
     */
    public static final int TYPE_CHILD = 1;

    /**
     * 获取类型
     *
     * @return 类型
     */
    public abstract int getType();
}