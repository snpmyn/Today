package com.zsp.today.module.zhilin.collaboration;

import android.content.Context;

import androidx.annotation.NonNull;

import com.zsp.today.databinding.ActivityCollaborationBinding;

import java.util.ArrayList;
import java.util.List;

import widget.recyclerview.configure.RecyclerViewConfigure;

/**
 * Created on 2026/4/25.
 *
 * @author 郑少鹏
 * @desc 联动页配套原件
 */
public class CollaborationActivityKit {
    /**
     * 分类适配器
     */
    private ClassificationAdapter classificationAdapter;

    /**
     * 执行
     *
     * @param context                      上下文
     * @param activityCollaborationBinding ActivityCollaborationBinding
     */
    public void execute(Context context, @NonNull ActivityCollaborationBinding activityCollaborationBinding) {
        // 学生数据集
        List<StudentBean> studentBeanList = getStudentBeans();
        // 控件
        RecyclerViewConfigure recyclerViewConfigure = new RecyclerViewConfigure(context, activityCollaborationBinding.classificationActivityRv);
        recyclerViewConfigure.linearVerticalLayout(true, 12, false, true, false);
        // 适配器
        classificationAdapter = new ClassificationAdapter();
        classificationAdapter.attachRecyclerView(activityCollaborationBinding.classificationActivityRv);
        classificationAdapter.setData(studentBeanList);
        classificationAdapter.setOnSelectListener(new ClassificationAdapter.OnSelectListener() {
            @Override
            public void onSelect(StudentBean studentBean, int position) {
                int recognizeState = studentBean.getRecognizeState();
                if ((recognizeState == 0) && !activityCollaborationBinding.classificationActivityMbQingHai.isChecked()) {
                    activityCollaborationBinding.classificationActivityMbQingHai.setChecked(true);
                }
                if ((recognizeState == 1) && !activityCollaborationBinding.classificationActivityMbXinJiang.isChecked()) {
                    activityCollaborationBinding.classificationActivityMbXinJiang.setChecked(true);
                }
                if ((recognizeState == 2) && !activityCollaborationBinding.classificationActivityMbXiZang.isChecked()) {
                    activityCollaborationBinding.classificationActivityMbXiZang.setChecked(true);
                }
            }

            @Override
            public void onUnSelect(StudentBean studentBean, int position) {
                int recognizeState = studentBean.getRecognizeState();
                if ((recognizeState == 0) && activityCollaborationBinding.classificationActivityMbQingHai.isChecked()) {
                    activityCollaborationBinding.classificationActivityMbQingHai.setChecked(false);
                }
                if ((recognizeState == 1) && activityCollaborationBinding.classificationActivityMbXinJiang.isChecked()) {
                    activityCollaborationBinding.classificationActivityMbXinJiang.setChecked(false);
                }
                if ((recognizeState == 2) && activityCollaborationBinding.classificationActivityMbXiZang.isChecked()) {
                    activityCollaborationBinding.classificationActivityMbXiZang.setChecked(false);
                }
            }
        });
        // 设置适配器
        activityCollaborationBinding.classificationActivityRv.setAdapter(classificationAdapter);
    }

    /**
     * 获取学生数据
     *
     * @return 学生数据
     */
    @NonNull
    private static List<StudentBean> getStudentBeans() {
        int studentId = 1000;
        List<StudentBean> studentBeanList = new ArrayList<>(36);
        for (int i = 1; i <= 12; i++) {
            StudentBean studentBean = new StudentBean();
            studentBean.setStudentId(studentId);
            studentBean.setStudentName("青海" + " - " + i);
            studentBean.setRecognizeState(0);
            studentBeanList.add(studentBean);
            studentId++;
        }
        for (int i = 1; i <= 12; i++) {
            StudentBean studentBean = new StudentBean();
            studentBean.setStudentId(studentId);
            studentBean.setStudentName("新疆" + " - " + i);
            studentBean.setRecognizeState(1);
            studentBeanList.add(studentBean);
            studentId++;
        }
        for (int i = 1; i <= 12; i++) {
            StudentBean studentBean = new StudentBean();
            studentBean.setStudentId(studentId);
            studentBean.setStudentName("西藏" + " - " + i);
            studentBean.setRecognizeState(2);
            studentBeanList.add(studentBean);
            studentId++;
        }
        return studentBeanList;
    }

    /**
     * 获取分类适配器
     *
     * @return 分类适配器
     */
    public ClassificationAdapter getClassificationAdapter() {
        return classificationAdapter;
    }
}