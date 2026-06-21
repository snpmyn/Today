package com.zsp.today.module.zhilin.collaboration;

import android.content.Context;

import androidx.annotation.NonNull;

import com.zsp.today.databinding.ActivityCollaborationBinding;

import java.util.ArrayList;
import java.util.List;

import util.log.LogUtils;
import widget.recyclerview.configure.RecyclerViewConfigure;

/**
 * Created on 2026/4/25.
 *
 * @author 郑少鹏
 * @desc 联动页配套原件
 */
public class CollaborationActivityKit {
    private static final String TAG = CollaborationActivityKit.class.getSimpleName();
    /**
     * 分类适配器
     */
    public ClassificationAdapter classificationAdapter;

    /**
     * 执行
     *
     * @param context                      上下文
     * @param activityCollaborationBinding ActivityCollaborationBinding
     * @param studentBeanList              学生数据集
     * @param switchExamPaper              是否切换试卷
     */
    public void execute(Context context, @NonNull ActivityCollaborationBinding activityCollaborationBinding, List<StudentBean> studentBeanList, boolean switchExamPaper) {
        if (classificationAdapter == null) {
            // 线性垂直布局
            RecyclerViewConfigure recyclerViewConfigure = new RecyclerViewConfigure(context, activityCollaborationBinding.classificationActivityRv);
            recyclerViewConfigure.linearVerticalLayout(true, 12, false, true, false);
            // 适配器
            classificationAdapter = new ClassificationAdapter(context);
            // 设置数据
            classificationAdapter.setData(studentBeanList, 0, switchExamPaper);
            // 绑定 RecyclerView
            classificationAdapter.attachRecyclerView(activityCollaborationBinding.classificationActivityRv);
            // 设置选择监听
            classificationAdapter.setOnSelectListener(new ClassificationAdapter.OnSelectListener() {
                @Override
                public void onSelect(StudentBean studentBean, int position) {
                    LogUtils.d(TAG, "选中 " + studentBean.getStudentName() + " - " + position);
                }

                @Override
                public void onUnSelect(StudentBean studentBean, int position) {
                    LogUtils.d(TAG, "取消选中 " + studentBean.getStudentName() + " - " + position);
                }
            });
            // 设置适配器
            activityCollaborationBinding.classificationActivityRv.setAdapter(classificationAdapter);
        } else {
            // 设置数据
            classificationAdapter.setData(studentBeanList, 0, switchExamPaper);
        }
    }

    /**
     * 获取第一学生数据
     *
     * @return 第一学生数据
     */
    @NonNull
    public List<StudentBean> getFirstStudentBeans() {
        List<StudentBean> studentBeanList = new ArrayList<>(15);
        for (int i = 101; i <= 105; i++) {
            studentBeanList.add(new StudentBean("一年一班", 101, i + " - " + "张无忌", i, 1, 0));
        }
        for (int i = 106; i <= 110; i++) {
            studentBeanList.add(new StudentBean("一年一班", 101, i + " - " + "张三丰", i, 1, 1));
        }
        for (int i = 111; i <= 115; i++) {
            studentBeanList.add(new StudentBean("一年一班", 101, i + " - " + "令狐冲", i, 1, 2));
        }
        return studentBeanList;
    }

    /**
     * 获取第二学生数据
     *
     * @return 第二学生数据
     */
    @NonNull
    public List<StudentBean> getSecondStudentBeans() {
        List<StudentBean> studentBeanList = new ArrayList<>(15);
        for (int i = 116; i <= 120; i++) {
            studentBeanList.add(new StudentBean("一年二班", 102, i + " - " + "左冷禅", i, 1, 0));
        }
        for (int i = 121; i <= 125; i++) {
            studentBeanList.add(new StudentBean("一年二班", 102, i + " - " + "扫地僧", i, 1, 1));
        }
        for (int i = 126; i <= 130; i++) {
            studentBeanList.add(new StudentBean("一年二班", 102, i + " - " + "慕容复", i, 1, 2));
        }
        return studentBeanList;
    }

    /**
     * 获取第三学生数据
     *
     * @return 第三学生数据
     */
    @NonNull
    public List<StudentBean> getThirdStudentBeans() {
        List<StudentBean> studentBeanList = new ArrayList<>(15);
        for (int i = 131; i <= 135; i++) {
            studentBeanList.add(new StudentBean("一年三班", 103, i + " - " + "花无缺", i, 1, 0));
        }
        for (int i = 136; i <= 140; i++) {
            studentBeanList.add(new StudentBean("一年三班", 103, i + " - " + "小鱼儿", i, 1, 1));
        }
        for (int i = 141; i <= 145; i++) {
            studentBeanList.add(new StudentBean("一年三班", 103, i + " - " + "黄飞鸿", i, 1, 2));
        }
        return studentBeanList;
    }
}