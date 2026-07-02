package com.zsp.today.module.zhilin.collaboration.kit;

import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;

import com.zsp.today.R;
import com.zsp.today.databinding.ActivityCollaborationBinding;
import com.zsp.today.module.zhilin.collaboration.CollaborationActivity;
import com.zsp.today.module.zhilin.collaboration.adapter.ClassificationAdapter;
import com.zsp.today.module.zhilin.collaboration.bean.StudentBean;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
     * 材料按钮 ID
     */
    private int materialButtonId;
    /**
     * 集合
     */
    private Map<Integer, StudentBean> map;
    /**
     * 分类适配器
     */
    private ClassificationAdapter classificationAdapter;

    /**
     * 预处理
     *
     * @param collaborationActivity        联动页
     * @param activityCollaborationBinding ActivityCollaborationBinding
     */
    public void preHandle(CollaborationActivity collaborationActivity, @NonNull ActivityCollaborationBinding activityCollaborationBinding) {
        // 集合
        map = new LinkedHashMap<>(3);
        // 按钮选中监听
        activityCollaborationBinding.classificationActivityMbtg.addOnButtonCheckedListener((materialButtonToggleGroup, i, b) -> {
            if (!b) {
                return;
            }
            materialButtonId = i;
            if (i == R.id.classificationActivityMbQingHai) {
                execute(collaborationActivity, activityCollaborationBinding, getFirstStudentBeans());
            } else if (i == R.id.classificationActivityMbXinJiang) {
                execute(collaborationActivity, activityCollaborationBinding, getSecondStudentBeans());
            } else if (i == R.id.classificationActivityMbXiZang) {
                execute(collaborationActivity, activityCollaborationBinding, getThirdStudentBeans());
            }
        });
        // 手动调用一次 -> 让页面初始进来有数据
        // 获取当前被选中的按钮 ID (即 XML 中配的那个)
        int checkedButtonId = activityCollaborationBinding.classificationActivityMbtg.getCheckedButtonId();
        if (checkedButtonId == R.id.classificationActivityMbQingHai) {
            materialButtonId = checkedButtonId;
            execute(collaborationActivity, activityCollaborationBinding, getFirstStudentBeans());
        }
        // 放弃按钮监听
        activityCollaborationBinding.classificationActivityMbDisable.setOnClickListener(v -> {
            classificationAdapter.clearSelect();
            classificationAdapter.setChildItemClickEnable(false);
            activityCollaborationBinding.classificationActivityTv.setText("");
        });
    }

    /**
     * 执行
     *
     * @param collaborationActivity        联动页
     * @param activityCollaborationBinding ActivityCollaborationBinding
     * @param studentBeanList              学生数据集
     */
    private void execute(CollaborationActivity collaborationActivity, @NonNull ActivityCollaborationBinding activityCollaborationBinding, List<StudentBean> studentBeanList) {
        if (classificationAdapter == null) {
            // 线性垂直布局
            RecyclerViewConfigure recyclerViewConfigure = new RecyclerViewConfigure(collaborationActivity, activityCollaborationBinding.classificationActivityRv);
            recyclerViewConfigure.linearVerticalLayout(true, 12, false, true, false);
            // 适配器
            classificationAdapter = new ClassificationAdapter(collaborationActivity);
            // 设置数据
            classificationAdapter.setData(studentBeanList, 0, true);
            // 绑定 RecyclerView
            classificationAdapter.attachRecyclerView(activityCollaborationBinding.classificationActivityRv);
            // 设置选择监听
            classificationAdapter.setOnSelectListener(new ClassificationAdapter.OnSelectListener() {
                @Override
                public void onSelect(StudentBean studentBean, int position) {
                    LogUtils.d(TAG, "选中 " + studentBean.getStudentName() + " - " + position);
                    map.put(materialButtonId, studentBean);
                    activityCollaborationBinding.classificationActivityTv.setText(studentBean.getStudentName());
                }

                @Override
                public void onUnSelect(StudentBean studentBean, int position) {
                    LogUtils.d(TAG, "取消选中 " + studentBean.getStudentName() + " - " + position);
                    map.put(materialButtonId, null);
                }
            });
            // 设置适配器
            activityCollaborationBinding.classificationActivityRv.setAdapter(classificationAdapter);
        } else {
            // 设置数据
            classificationAdapter.setData(studentBeanList, 0, true);
        }
        // 关键修复
        activityCollaborationBinding.classificationActivityRv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                activityCollaborationBinding.classificationActivityRv.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                StudentBean studentBean = map.get(materialButtonId);
                if (studentBean != null) {
                    classificationAdapter.focus(studentBean.getStudentId(), true);
                }
                classificationAdapter.setChildItemClickEnable(true);
                activityCollaborationBinding.classificationActivityTv.setText((studentBean != null) ? studentBean.getStudentName() : "");
            }
        });
    }

    /**
     * 获取第一学生数据
     *
     * @return 第一学生数据
     */
    @NonNull
    private List<StudentBean> getFirstStudentBeans() {
        List<StudentBean> studentBeanList = new ArrayList<>(15);
        for (int i = 101; i <= 105; i++) {
            studentBeanList.add(new StudentBean("一年一班", 101, i + " - " + "张无忌", i, 0));
        }
        for (int i = 106; i <= 110; i++) {
            studentBeanList.add(new StudentBean("一年一班", 101, i + " - " + "张三丰", i, 1));
        }
        for (int i = 111; i <= 115; i++) {
            studentBeanList.add(new StudentBean("一年一班", 101, i + " - " + "令狐冲", i, 2));
        }
        return studentBeanList;
    }

    /**
     * 获取第二学生数据
     *
     * @return 第二学生数据
     */
    @NonNull
    private List<StudentBean> getSecondStudentBeans() {
        List<StudentBean> studentBeanList = new ArrayList<>(15);
        for (int i = 116; i <= 120; i++) {
            studentBeanList.add(new StudentBean("一年二班", 102, i + " - " + "左冷禅", i, 0));
        }
        for (int i = 121; i <= 125; i++) {
            studentBeanList.add(new StudentBean("一年二班", 102, i + " - " + "扫地僧", i, 1));
        }
        for (int i = 126; i <= 130; i++) {
            studentBeanList.add(new StudentBean("一年二班", 102, i + " - " + "慕容复", i, 2));
        }
        return studentBeanList;
    }

    /**
     * 获取第三学生数据
     *
     * @return 第三学生数据
     */
    @NonNull
    private List<StudentBean> getThirdStudentBeans() {
        List<StudentBean> studentBeanList = new ArrayList<>(15);
        for (int i = 131; i <= 135; i++) {
            studentBeanList.add(new StudentBean("一年三班", 103, i + " - " + "花无缺", i, 0));
        }
        for (int i = 136; i <= 140; i++) {
            studentBeanList.add(new StudentBean("一年三班", 103, i + " - " + "小鱼儿", i, 1));
        }
        for (int i = 141; i <= 145; i++) {
            studentBeanList.add(new StudentBean("一年三班", 103, i + " - " + "黄飞鸿", i, 2));
        }
        return studentBeanList;
    }
}