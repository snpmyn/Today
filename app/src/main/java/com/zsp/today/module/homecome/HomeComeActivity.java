package com.zsp.today.module.homecome;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButton;
import com.zsp.today.R;
import com.zsp.today.module.homecome.kit.HomeComeActivityKit;

import pool.base.BasePoolActivity;
import widget.media.image.ImagePicker;
import widget.recyclerview.custom.SmartFlexibleRecyclerView;
import widget.view.DatePickerDialog;

/**
 * Created on 2025/10/5.
 *
 * @author 郑少鹏
 * @desc 归心页
 */
public class HomeComeActivity extends BasePoolActivity implements View.OnClickListener {
    private ImageView homeComeActivityIvBackground;
    private SmartFlexibleRecyclerView homeComeActivitySfrv;
    private MaterialButton homeComeActivityMbSelectBackground;
    private MaterialButton homeComeActivityMbPlace;
    /**
     * 归心页配套原件
     */
    private HomeComeActivityKit homeComeActivityKit;
    /**
     * 图片选择器
     */
    private ImagePicker imagePicker;

    /**
     * 布局资源 ID
     *
     * @return 布局资源 ID
     */
    @Override
    protected int layoutResId() {
        return R.layout.activity_home_come;
    }

    /**
     * 初始控件
     */
    @Override
    protected void stepUi() {
        // ImageView
        homeComeActivityIvBackground = findViewById(R.id.homeComeActivityIvBackground);
        // SmartFlexibleRecyclerView
        homeComeActivitySfrv = findViewById(R.id.homeComeActivitySfrv);
        // MaterialButton
        homeComeActivityMbSelectBackground = findViewById(R.id.homeComeActivityMbSelectBackground);
        homeComeActivityMbPlace = findViewById(R.id.homeComeActivityMbPlace);
    }

    /**
     * 初始配置
     */
    @Override
    protected void initConfiguration() {
        // 归心页配套原件
        homeComeActivityKit = new HomeComeActivityKit();
        // 图片选择器
        imagePicker = ImagePicker.with(this);
    }

    /**
     * 设置监听
     */
    @Override
    protected void setListener() {
        homeComeActivityMbSelectBackground.setOnClickListener(this);
        homeComeActivityMbPlace.setOnClickListener(this);
    }

    /**
     * 开始逻辑
     */
    @Override
    protected void startLogic() {
        // 设置背景
        homeComeActivityKit.setBackground(this, homeComeActivityIvBackground);
        // 展示
        homeComeActivityKit.display(this, homeComeActivitySfrv);
    }

    @Override
    public void onClick(@NonNull View v) {
        int id = v.getId();
        if (id == R.id.homeComeActivityMbSelectBackground) {
            // 选择背景
            homeComeActivityKit.selectBackground(this, imagePicker, homeComeActivityIvBackground);
        } else if (id == R.id.homeComeActivityMbPlace) {
            // 安放
            homeComeActivityKit.placeOrModify(this, homeComeActivitySfrv, null, DatePickerDialog.CalendarType.LUNAR.getName(), 2025, 10, 2, false);
        }
    }
}