package com.zsp.today.module.zhilin.interview.kit;

import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.carousel.CarouselLayoutManager;
import com.google.android.material.carousel.FullScreenCarouselStrategy;
import com.zsp.today.R;

import java.util.ArrayList;
import java.util.List;

import util.list.ListUtils;
import widget.carousel.CarouselItem;
import widget.carousel.CarouselKit;
import widget.screen.kit.ScreenHandleKit;
import widget.screen.listener.ScreenHandleListener;

/**
 * Created on 2026/1/25.
 *
 * @author 郑少鹏
 * @desc 面试页配套原件
 */
public class InterviewActivityKit {
    private CarouselKit carouselKit;
    private String defaultSelectCondition;
    protected String[] conditionTemporary;
    protected List<CarouselItem> carouselItemList;
    protected List<String> carouselItemCarouselTitleList;

    /**
     * constructor
     */
    public InterviewActivityKit() {
        this.conditionTemporary = new String[1];
        this.carouselItemList = prepareCarouselItemList();
        this.carouselItemCarouselTitleList = prepareCarouselItemCarouselTitleList();
    }

    /**
     * 轮播
     *
     * @param recyclerView RecyclerView
     */
    public void carousel(RecyclerView recyclerView) {
        carouselKit = new CarouselKit();
        // 执行
        carouselKit.execute(recyclerView, carouselItemList, new FullScreenCarouselStrategy(), RecyclerView.VERTICAL, false, CarouselLayoutManager.ALIGNMENT_CENTER, false, 0, 0, 0, 8, false, null);
        // 监听当前位置
        carouselKit.observeCurrentPosition(recyclerView, position -> defaultSelectCondition = carouselItemCarouselTitleList.get(position));
    }

    /**
     * 显示面试要点列表
     *
     * @param appCompatActivity 活动
     * @param recyclerView      RecyclerView
     */
    public void showInterviewPointList(AppCompatActivity appCompatActivity, RecyclerView recyclerView) {
        // ScreenHandleKit
        ScreenHandleKit screenHandleKit = new ScreenHandleKit(appCompatActivity);
        // 打包集合条件
        screenHandleKit.packListConditions(appCompatActivity.getString(R.string.interviewPoint), 3, true, carouselItemCarouselTitleList);
        // 默选
        screenHandleKit.defaultSelect(appCompatActivity.getString(R.string.interviewPoint), TextUtils.isEmpty(defaultSelectCondition) ? carouselItemCarouselTitleList.get(0) : defaultSelectCondition);
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
                carouselKit.scrollToPosition(recyclerView, ListUtils.getTargetIndex(carouselItemCarouselTitleList, conditionTemporary[0]), false);
            }
        });
        // 显示
        screenHandleKit.show();
    }

    /**
     * 准备轮播条目集
     *
     * @return 轮播条目集
     */
    @NonNull
    private List<CarouselItem> prepareCarouselItemList() {
        List<CarouselItem> carouselItemList = new ArrayList<>(46);
        carouselItemList.add(new CarouselItem(true, R.drawable.interview_point_1, "Android\n坐标系", "Android 坐标系"));
        carouselItemList.add(new CarouselItem(true, R.drawable.interview_point_2, "Glide\n三级缓存", "Glide 三级缓存"));
        carouselItemList.add(new CarouselItem(true, R.drawable.interview_point_3, "TCP\n三次握手", "TCP 三次握手"));
        carouselItemList.add(new CarouselItem(true, R.drawable.interview_point_4, "接口\ndefault", "接口 default 修饰"));
        carouselItemList.add(new CarouselItem(true, R.drawable.interview_point_5, "PPI\nDPI", "PPI DPI"));
        carouselItemList.add(new CarouselItem(true, R.drawable.interview_point_6, "final\nfinally finalize", "final finally finalize"));
        carouselItemList.add(new CarouselItem(true, R.drawable.interview_point_7, "String\nfinal 一", "String final 一"));
        carouselItemList.add(new CarouselItem(true, R.drawable.interview_point_8, "String\nfinal 二", "String final 二"));
        carouselItemList.add(new CarouselItem(true, R.drawable.interview_point_9, "Java\n三兄弟", "Java 三兄弟"));
        carouselItemList.add(new CarouselItem(true, R.drawable.interview_point_10, "事件\n分发", "事件分发"));
        carouselItemList.add(new CarouselItem(true, R.drawable.interview_point_11, "加密", "加密"));
        carouselItemList.add(new CarouselItem(true, R.drawable.interview_point_12, "加解密\n加验签", "加解密 加验签"));
        carouselItemList.add(new CarouselItem(true, R.drawable.interview_point_13, "图片\n加载", "图片加载"));
        carouselItemList.add(new CarouselItem(true, R.drawable.interview_point_14, "sleep\nwait", "sleep wait"));
        carouselItemList.add(new CarouselItem(true, R.drawable.interview_point_15, "View\n绘制", "View 绘制"));
        carouselItemList.add(new CarouselItem(true, R.drawable.interview_point_16, "MVC P\nMVVM", "MVC MVP MVVM"));
        carouselItemList.add(new CarouselItem(true, R.drawable.interview_point_17, "线程\n通信", "线程通信"));
        carouselItemList.add(new CarouselItem(true, R.drawable.interview_point_18, "进程\n通信", "进程通信"));
        carouselItemList.add(new CarouselItem(true, R.drawable.interview_point_19, "buffer\nbuilder", "buffer builder"));
        carouselItemList.add(new CarouselItem(true, R.drawable.interview_point_20, "内存\n泄露", "内存泄露"));
        carouselItemList.add(new CarouselItem(true, R.drawable.interview_point_21, "Handler", "Handler"));
        carouselItemList.add(new CarouselItem(true, R.drawable.interview_point_22, "自定义\nView", "自定义 View"));
        carouselItemList.add(new CarouselItem(true, R.drawable.interview_point_23, "volatile\nsynchronized", "synchronized volatile"));
        carouselItemList.add(new CarouselItem(true, R.drawable.interview_point_24, "lock\nsynchronized", "synchronized lock"));
        carouselItemList.add(new CarouselItem(true, R.drawable.interview_point_25, "消息\n同异步", "同异步消息"));
        carouselItemList.add(new CarouselItem(true, R.drawable.interview_point_26, "RxJava", "RxJava"));
        carouselItemList.add(new CarouselItem(true, R.drawable.interview_point_27, "Rx\nAndroid", "RxAndroid"));
        carouselItemList.add(new CarouselItem(true, R.drawable.interview_point_28, "Thread\nLocal", "ThreadLocal"));
        carouselItemList.add(new CarouselItem(true, R.drawable.interview_point_29, "虚拟机", "虚拟机"));
        carouselItemList.add(new CarouselItem(true, R.drawable.interview_point_30, "线程池", "线程池"));
        carouselItemList.add(new CarouselItem(true, R.drawable.interview_point_31, "List", "List"));
        carouselItemList.add(new CarouselItem(true, R.drawable.interview_point_32, "equals\nhashCode", "equals hashCode"));
        carouselItemList.add(new CarouselItem(true, R.drawable.interview_point_33, "非 / 静态\n内部类", "非 / 静态内部类"));
        carouselItemList.add(new CarouselItem(true, R.drawable.interview_point_34, "局部\n内部类", "局部内部类"));
        carouselItemList.add(new CarouselItem(true, R.drawable.interview_point_35, "匿名\n内部类", "匿名内部类"));
        carouselItemList.add(new CarouselItem(true, R.drawable.interview_point_36, "TCP\nUDP", "TCP UDP"));
        carouselItemList.add(new CarouselItem(true, R.drawable.interview_point_37, "滑动\n冲突", "滑动冲突"));
        carouselItemList.add(new CarouselItem(true, R.drawable.interview_point_38, "JIT\nAOT", "JIT AOT"));
        carouselItemList.add(new CarouselItem(true, R.drawable.interview_point_39, "APP\n加固", "APP 加固"));
        carouselItemList.add(new CarouselItem(true, R.drawable.interview_point_40, "TCP\n拥塞控制", "TCP 拥塞控制"));
        carouselItemList.add(new CarouselItem(true, R.drawable.interview_point_41, "Activity\n启动一", "Activity 启动一"));
        carouselItemList.add(new CarouselItem(true, R.drawable.interview_point_42, "Activity\n启动二", "Activity 启动二"));
        carouselItemList.add(new CarouselItem(true, R.drawable.interview_point_43, "类\n加载一", "类加载一"));
        carouselItemList.add(new CarouselItem(true, R.drawable.interview_point_44, "类\n加载二", "类加载二"));
        carouselItemList.add(new CarouselItem(true, R.drawable.interview_point_45, "类\n加载三", "类加载三"));
        carouselItemList.add(new CarouselItem(true, R.drawable.interview_point_46, "ListView\nRecyclerView", "ListView RecyclerView"));
        return carouselItemList;
    }

    /**
     * 准备轮播条目轮播标题集
     *
     * @return 轮播条目轮播标题集
     */
    @NonNull
    private List<String> prepareCarouselItemCarouselTitleList() {
        List<String> carouselItemCarouselTitleList = new ArrayList<>(carouselItemList.size());
        for (CarouselItem carouselItem : carouselItemList) {
            carouselItemCarouselTitleList.add(carouselItem.getCarouselTitle());
        }
        return carouselItemCarouselTitleList;
    }
}