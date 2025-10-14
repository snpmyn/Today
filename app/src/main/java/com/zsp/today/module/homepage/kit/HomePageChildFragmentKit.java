package com.zsp.today.module.homepage.kit;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.carousel.CarouselLayoutManager;
import com.google.android.material.carousel.HeroCarouselStrategy;
import com.zsp.today.R;
import com.zsp.today.application.App;
import com.zsp.today.module.account.AccountHomeActivity;
import com.zsp.today.module.dangerous.DangerousActivity;
import com.zsp.today.module.function.database.FunctionDataBaseTable;
import com.zsp.today.module.function.value.FunctionCondition;
import com.zsp.today.module.heartbox.HeartBoxActivity;
import com.zsp.today.module.homecome.HomeComeActivity;
import com.zsp.today.module.homepage.bean.HomePageMenuEnum;
import com.zsp.today.module.zhilin.ZhiLinActivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import litepal.kit.LitePalKit;
import timber.log.Timber;
import util.glide.util.GlideUtils;
import util.intent.IntentJump;
import widget.adapttemplate.bean.MenuBean;
import widget.adapttemplate.kit.MenuAdapterKit;
import widget.carousel.CarouselItem;
import widget.carousel.CarouselKit;
import widget.carousel.CarouselListener;
import widget.dialog.materialalertdialog.PictureInfoMaterialAlertDialogKit;
import widget.kotlin.banner.IBannerView;
import widget.kotlin.banner.view.BannerView;

/**
 * Created on 2021/9/15
 *
 * @author zsp
 * @desc 首页子碎片配套元件
 */
public class HomePageChildFragmentKit {
    /**
     * 初始化标题
     *
     * @param collapsingToolbarLayout CollapsingToolbarLayout
     * @param materialToolbar         MaterialToolbar
     * @param title                   标题
     */
    public void initTitle(@NonNull CollapsingToolbarLayout collapsingToolbarLayout, @NonNull MaterialToolbar materialToolbar, String title) {
        // 禁用 CollapsingToolbarLayout 展开标题
        collapsingToolbarLayout.setTitleEnabled(false);
        // 设置 MaterialToolbar 标题
        materialToolbar.setTitle(title);
    }

    /**
     * 轮播
     *
     * @param bannerView BannerView
     */
    public void banner(@NonNull BannerView bannerView) {
        List<Integer> imageList = getImages();
        bannerView.setBannerViewImpl(new IBannerView() {
            @Override
            public void onPageSelected(int position) {
                Timber.d("选 %s", position);
            }

            @Override
            public boolean isDefaultAutoScroll() {
                return true;
            }

            @Override
            public View getDefaultView(@NotNull Context context) {
                View view = new View(context);
                view.setBackgroundColor(Color.WHITE);
                return view;
            }

            @Override
            public int getCount() {
                return imageList.size();
            }

            @NotNull
            @Override
            public View getItemView(@NotNull Context context) {
                return new ImageView(context);
            }

            @Override
            public void onBindView(@NotNull View itemView, int position) {
                if (itemView instanceof ImageView) {
                    ImageView imageView = (ImageView) itemView;
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    GlideUtils.loadByObject(itemView.getContext(), imageList.get(position), imageView);
                }
            }
        });
    }

    /**
     * 图集
     *
     * @return 图集
     */
    @NonNull
    private static List<Integer> getImages() {
        List<Integer> imageList = new ArrayList<>(9);
        imageList.add(R.drawable.banner_one);
        imageList.add(R.drawable.banner_two);
        imageList.add(R.drawable.banner_three);
        imageList.add(R.drawable.banner_four);
        imageList.add(R.drawable.banner_five);
        imageList.add(R.drawable.banner_six);
        imageList.add(R.drawable.banner_seven);
        imageList.add(R.drawable.banner_eight);
        imageList.add(R.drawable.banner_nine);
        return imageList;
    }

    /**
     * 轮播
     *
     * @param appCompatActivity 活动
     * @param recyclerView      RecyclerView
     */
    public void carousel(AppCompatActivity appCompatActivity, RecyclerView recyclerView) {
        // 轮播条目集
        List<CarouselItem> carouselItemList = new ArrayList<>(9);
        carouselItemList.add(new CarouselItem(R.drawable.banner_one, "上木居村"));
        carouselItemList.add(new CarouselItem(R.drawable.banner_two, "火星一号公路"));
        carouselItemList.add(new CarouselItem(R.drawable.banner_three, "那拉提河谷草原"));
        carouselItemList.add(new CarouselItem(R.drawable.banner_four, "赛里木湖"));
        carouselItemList.add(new CarouselItem(R.drawable.banner_five, "禾木村"));
        carouselItemList.add(new CarouselItem(R.drawable.banner_six, "西府老街"));
        carouselItemList.add(new CarouselItem(R.drawable.banner_seven, "贾村塬"));
        carouselItemList.add(new CarouselItem(R.drawable.banner_eight, "金台区"));
        carouselItemList.add(new CarouselItem(R.drawable.banner_nine, "文昌门"));
        // 轮播配套原件
        CarouselKit carouselKit = new CarouselKit();
        carouselKit.execute(recyclerView, carouselItemList, new HeroCarouselStrategy(), RecyclerView.HORIZONTAL, false, CarouselLayoutManager.ALIGNMENT_CENTER, false, 4, 0, 4, 0, false, new CarouselListener() {
            @Override
            public void onItemLongClick(CarouselItem carouselItem, int position) {
                PictureInfoMaterialAlertDialogKit.getInstance().show(appCompatActivity, carouselItem);
            }
        });
    }

    /**
     * 功能数据库表
     * <p>
     * 场景一 - 初次安装
     * 场景二 - 卸载安装
     * 场景三 - 覆盖安装
     *
     * @param appCompatActivity 活动
     * @param recyclerView      控件
     */
    public void functionDataBaseTable(AppCompatActivity appCompatActivity, RecyclerView recyclerView) {
        if (needSave(LitePalKit.getInstance().findAll(FunctionDataBaseTable.class))) {
            // 先删
            // 避免数据污染
            LitePalKit.getInstance().allDelete(FunctionDataBaseTable.class);
            // 后存
            // 只存可显示主页菜单
            HomePageMenuEnum[] homePageMenuEnums = HomePageMenuEnum.values();
            List<FunctionDataBaseTable> functionDataBaseTableList = new ArrayList<>(homePageMenuEnums.length);
            for (HomePageMenuEnum homePageMenuEnum : homePageMenuEnums) {
                if (homePageMenuEnum.getMenuShow()) {
                    functionDataBaseTableList.add(new FunctionDataBaseTable(App.getAppInstance().getPhoneNumber(), null, homePageMenuEnum.getMenuId(), homePageMenuEnum.getMenuIconResName(), homePageMenuEnum.getMenuName(), true));
                }
            }
            if (LitePalKit.getInstance().multiSave(functionDataBaseTableList)) {
                displayFunctionDataBaseTable(appCompatActivity, recyclerView);
            }
        } else {
            displayFunctionDataBaseTable(appCompatActivity, recyclerView);
        }
    }

    /**
     * 需要保存
     *
     * @param functionDataBaseTableList 功能数据库表集
     * @return 需要保存否
     */
    public static boolean needSave(List<FunctionDataBaseTable> functionDataBaseTableList) {
        // 可显示主页菜单枚举集
        List<HomePageMenuEnum> homePageMenuEnumsCanShow = new ArrayList<>();
        for (HomePageMenuEnum homePageMenuEnum : HomePageMenuEnum.values()) {
            if (homePageMenuEnum.getMenuShow()) {
                homePageMenuEnumsCanShow.add(homePageMenuEnum);
            }
        }
        // 可显示主页菜单枚举集数量
        // 功能数据库表集数量
        // 不一致
        if (functionDataBaseTableList.size() != homePageMenuEnumsCanShow.size()) {
            return true;
        }
        // 建立映射
        Map<Integer, FunctionDataBaseTable> functionDataBaseTableMap = new HashMap<>();
        for (FunctionDataBaseTable functionDataBaseTable : functionDataBaseTableList) {
            functionDataBaseTableMap.put(functionDataBaseTable.getFunctionId(), functionDataBaseTable);
        }
        // 以可显示主页菜单枚举集为基准
        // 遍历比较
        for (HomePageMenuEnum homePageMenuEnum : homePageMenuEnumsCanShow) {
            FunctionDataBaseTable functionDataBaseTable = functionDataBaseTableMap.get(homePageMenuEnum.getMenuId());
            if (null == functionDataBaseTable) {
                // 功能数据库表无对应主页菜单
                return true;
            }
            if (!TextUtils.equals(homePageMenuEnum.getMenuIconResName(), functionDataBaseTable.getFunctionIconResName())) {
                // 菜单图标资源名称
                // 功能图标资源名称
                // 不一致
                return true;
            }
            if (!TextUtils.equals(homePageMenuEnum.getMenuName(), functionDataBaseTable.getFunctionName())) {
                // 菜单名称
                // 功能名称
                // 不一致
                return true;
            }
        }
        return false;
    }

    /**
     * 展示功能数据库表
     *
     * @param appCompatActivity 活动
     * @param recyclerView      控件
     */
    private void displayFunctionDataBaseTable(AppCompatActivity appCompatActivity, RecyclerView recyclerView) {
        // 获取主页菜单图标资源 ID 集
        Map<String, Integer> homePageMenuIconResIdMap = getHomePageMenuIconResIdMap();
        // 获取功能数据库表可显数据集
        List<FunctionDataBaseTable> functionDataBaseTableList = LitePalKit.getInstance().queryByWhere(FunctionDataBaseTable.class, FunctionCondition.FUNCTION_PHONE_NUMBER_AND_FUNCTION_SHOW, App.getAppInstance().getPhoneNumber(), "1");
        // 获取菜单集
        List<MenuBean> menuBeanList = new ArrayList<>(functionDataBaseTableList.size());
        for (FunctionDataBaseTable functionDataBaseTable : functionDataBaseTableList) {
            String menuName = functionDataBaseTable.getFunctionName();
            Integer menuIconResId = homePageMenuIconResIdMap.get(menuName);
            if (null != menuIconResId) {
                menuBeanList.add(new MenuBean(functionDataBaseTable.getFunctionId(), menuIconResId, menuName));
            }
        }
        // 菜单适配器配套元件
        MenuAdapterKit menuAdapterKit = new MenuAdapterKit();
        menuAdapterKit.display(appCompatActivity, recyclerView, menuBeanList, 3, 12, 48, false, (view, menuBean) -> distribute(appCompatActivity, menuBean.getMenuId()));
    }

    /**
     * 获取主页菜单图标资源 ID 集
     *
     * @return 主页菜单图标资源 ID 集
     */
    @NonNull
    private static Map<String, Integer> getHomePageMenuIconResIdMap() {
        HomePageMenuEnum[] homePageMenuEnums = HomePageMenuEnum.values();
        Map<String, Integer> menuIconResIdMap = new HashMap<>(homePageMenuEnums.length);
        for (HomePageMenuEnum homePageMenuEnum : homePageMenuEnums) {
            menuIconResIdMap.put(homePageMenuEnum.getMenuName(), homePageMenuEnum.getMenuIconResId());
        }
        return menuIconResIdMap;
    }

    /**
     * 分发
     *
     * @param appCompatActivity 活动
     * @param menuId            菜单 ID
     */
    private void distribute(AppCompatActivity appCompatActivity, int menuId) {
        switch (menuId) {
            // 账目
            case 1:
                IntentJump.getInstance().jump(null, appCompatActivity, false, AccountHomeActivity.class);
                break;
            // 险情
            case 2:
                IntentJump.getInstance().jump(null, appCompatActivity, false, DangerousActivity.class);
                break;
            // 心盒
            case 3:
                IntentJump.getInstance().jump(null, appCompatActivity, false, HeartBoxActivity.class);
                break;
            // 归心
            case 4:
                IntentJump.getInstance().jump(null, appCompatActivity, false, HomeComeActivity.class);
                break;
            // 知林
            case 5:
                IntentJump.getInstance().jump(null, appCompatActivity, false, ZhiLinActivity.class);
                break;
            default:
                break;
        }
    }
}