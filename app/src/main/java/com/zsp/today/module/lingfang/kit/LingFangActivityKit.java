package com.zsp.today.module.lingfang.kit;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.zsp.today.R;
import com.zsp.today.module.lingfang.value.LingFangConstant;

import java.util.ArrayList;
import java.util.List;

import util.mmkv.MmkvKit;
import util.resource.ResourceUtils;
import util.statusbar.StatusBarColorHelper;
import widget.carousel.CarouselItem;
import widget.dialog.materialalertdialog.CarouselMaterialAlertDialogKit;
import widget.sensor.bagua.BaguaCompassTheme;
import widget.sensor.bagua.BaguaCompassView;

/**
 * Created on 2025/10/16.
 *
 * @author 郑少鹏
 * @desc 灵方页配套原件
 */
public class LingFangActivityKit {
    /**
     * 设置背景
     *
     * @param appCompatActivity            活动
     * @param lingFangActivityIvBackground ImageView
     */
    public void setBackground(AppCompatActivity appCompatActivity, ImageView lingFangActivityIvBackground) {
        String backgroundImageResource = MmkvKit.defaultMmkv().decodeString(LingFangConstant.LING_FANG_ACTIVITY_$_BACKGROUND_IMAGE_RESOURCE);
        int imageResource = ResourceUtils.getResourceId(appCompatActivity, backgroundImageResource, "drawable");
        if (imageResource == 0) {
            imageResource = ResourceUtils.getResourceId(appCompatActivity, "ling_fang_1", "drawable");
        }
        StatusBarColorHelper.INSTANCE.setBackgroundImageResource(appCompatActivity, lingFangActivityIvBackground, imageResource, new StatusBarColorHelper.StatusBarColorListener() {
            @Override
            public void onLight() {

            }

            @Override
            public void onDark() {

            }
        });
    }

    /**
     * 执行
     *
     * @param appCompatActivity 活动
     * @param baguaCompassView  八卦罗盘视图
     * @param textView          TextView
     */
    public void execute(AppCompatActivity appCompatActivity, @NonNull BaguaCompassView baguaCompassView, TextView textView) {
        BaguaCompassTheme.switchThemeBySystem(appCompatActivity, baguaCompassView);
        baguaCompassView.setOnBaguaChangedListener((bagua, direction, element, meaning, description, symbol) -> textView.setText(String.format("%s\u3000☯\u3000%s\n\n%s\n\n%s\u3000☯\u3000%s\n\n%s", symbol, bagua, direction, element, meaning, description)));
    }

    /**
     * 选择背景
     *
     * @param appCompatActivity            活动
     * @param lingFangActivityIvBackground ImageView
     */
    public void selectBackground(AppCompatActivity appCompatActivity, ImageView lingFangActivityIvBackground) {
        // 轮播条目集
        List<CarouselItem> carouselItemList = new ArrayList<>(26);
        carouselItemList.add(new CarouselItem(R.drawable.ling_fang_1, "ling_fang_1", "九字真言"));
        carouselItemList.add(new CarouselItem(R.drawable.ling_fang_2, "ling_fang_2", "九天玄女"));
        carouselItemList.add(new CarouselItem(R.drawable.ling_fang_3, "ling_fang_3", "双鱼"));
        carouselItemList.add(new CarouselItem(R.drawable.ling_fang_4, "ling_fang_4", "九天玄女"));
        carouselItemList.add(new CarouselItem(R.drawable.ling_fang_5, "ling_fang_5", "赤龙"));
        carouselItemList.add(new CarouselItem(R.drawable.ling_fang_6, "ling_fang_6", "灵诀"));
        carouselItemList.add(new CarouselItem(R.drawable.ling_fang_7, "ling_fang_7", "易阵"));
        carouselItemList.add(new CarouselItem(R.drawable.ling_fang_8, "ling_fang_8", "八仙"));
        carouselItemList.add(new CarouselItem(R.drawable.ling_fang_9, "ling_fang_9", "佛家"));
        carouselItemList.add(new CarouselItem(R.drawable.ling_fang_10, "ling_fang_10", "玄莲"));
        carouselItemList.add(new CarouselItem(R.drawable.ling_fang_11, "ling_fang_11", "九天玄女"));
        carouselItemList.add(new CarouselItem(R.drawable.ling_fang_12, "ling_fang_12", "道家"));
        carouselItemList.add(new CarouselItem(R.drawable.ling_fang_13, "ling_fang_13", "五彩神女"));
        carouselItemList.add(new CarouselItem(R.drawable.ling_fang_14, "ling_fang_14", "九沙焰羽"));
        carouselItemList.add(new CarouselItem(R.drawable.ling_fang_15, "ling_fang_15", "吉风"));
        carouselItemList.add(new CarouselItem(R.drawable.ling_fang_16, "ling_fang_16", "飞天"));
        carouselItemList.add(new CarouselItem(R.drawable.ling_fang_17, "ling_fang_17", "白鹤"));
        carouselItemList.add(new CarouselItem(R.drawable.ling_fang_18, "ling_fang_18", "三太子"));
        carouselItemList.add(new CarouselItem(R.drawable.ling_fang_19, "ling_fang_19", "黑羽咒"));
        carouselItemList.add(new CarouselItem(R.drawable.ling_fang_20, "ling_fang_20", "驱邪化煞"));
        carouselItemList.add(new CarouselItem(R.drawable.ling_fang_21, "ling_fang_21", "玄易金莲"));
        carouselItemList.add(new CarouselItem(R.drawable.ling_fang_22, "ling_fang_22", "虚空"));
        carouselItemList.add(new CarouselItem(R.drawable.ling_fang_23, "ling_fang_23", "御符伞"));
        carouselItemList.add(new CarouselItem(R.drawable.ling_fang_24, "ling_fang_24", "苍龙道心"));
        carouselItemList.add(new CarouselItem(R.drawable.ling_fang_25, "ling_fang_25", "紧箍"));
        carouselItemList.add(new CarouselItem(R.drawable.ling_fang_26, "ling_fang_26", "摸金"));
        CarouselMaterialAlertDialogKit.getInstance().show(appCompatActivity, carouselItemList, 2, (alertDialog, carouselItem) -> {
            MmkvKit.defaultMmkv().encode(LingFangConstant.LING_FANG_ACTIVITY_$_BACKGROUND_IMAGE_RESOURCE, carouselItem.getDrawableResName());
            setBackground(appCompatActivity, lingFangActivityIvBackground);
        });
    }
}