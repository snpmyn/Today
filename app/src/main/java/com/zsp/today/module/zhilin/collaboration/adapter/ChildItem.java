package com.zsp.today.module.zhilin.collaboration.adapter;

import com.zsp.today.module.zhilin.collaboration.bean.StudentBean;

/**
 * Created on 2026/4/23.
 *
 * @author 郑少鹏
 * @desc 子条目
 */
public class ChildItem extends ListItem {
    /**
     * 学生数据
     */
    public StudentBean studentBean;

    /**
     * constructor
     *
     * @param studentBean 学生数据
     */
    public ChildItem(StudentBean studentBean) {
        this.studentBean = studentBean;
    }

    /**
     * 是否选中
     *
     * @return 是否选中
     */
    public boolean isSelected() {
        return (studentBean.getCheckStatus() == 2) || (studentBean.getCheckStatus() == 3);
    }

    /**
     * 获取类型
     *
     * @return 类型
     */
    @Override
    public int getType() {
        return TYPE_CHILD;
    }
}