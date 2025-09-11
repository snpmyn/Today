package com.zsp.today.module.homepage.kit;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.carousel.CarouselLayoutManager;
import com.google.android.material.carousel.HeroCarouselStrategy;
import com.zsp.today.R;
import com.zsp.today.application.App;
import com.zsp.today.basic.value.RxBusConstant;
import com.zsp.today.module.account.AccountHomeActivity;
import com.zsp.today.module.dangerous.DangerousActivity;
import com.zsp.today.module.function.database.FunctionDataBaseTable;
import com.zsp.today.module.function.value.FunctionCondition;
import com.zsp.today.module.heartbox.HeartBoxActivity;
import com.zsp.today.module.homepage.bean.HomePageMenuEnum;
import com.zsp.today.module.homepage.fragment.HomePageChildFragment;
import com.zsp.today.module.widget.WidgetFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import litepal.kit.LitePalKit;
import timber.log.Timber;
import util.datetime.DateUtils;
import util.glide.util.GlideUtils;
import util.intent.IntentJump;
import util.rxbus.RxBus;
import widget.adapttemplate.bean.MenuBean;
import widget.adapttemplate.kit.MenuAdapterKit;
import widget.carousel.CarouselItem;
import widget.carousel.CarouselKit;
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
     * 显示标题
     *
     * @param homePageFragment        首页子碎片
     * @param collapsingToolbarLayout CollapsingToolbarLayout
     */
    public void showTitle(HomePageChildFragment homePageFragment, CollapsingToolbarLayout collapsingToolbarLayout) {
        switch (DateUtils.getCurrentWeek(DateUtils.getCurrentTimeYearMonthDayHourMinute())) {
            case "星期一":
                collapsingToolbarLayout.setTitle(String.format(homePageFragment.getString(R.string.formatHomePageChildFragmentTitleOne), "无为而治", "清净自在"));
                break;
            case "星期二":
                collapsingToolbarLayout.setTitle(String.format(homePageFragment.getString(R.string.formatHomePageChildFragmentTitleOne), "福生无量", "天长地久"));
                break;
            case "星期三":
                collapsingToolbarLayout.setTitle(String.format(homePageFragment.getString(R.string.formatHomePageChildFragmentTitleOne), "居尘出尘", "常乐我净"));
                break;
            case "星期四":
                collapsingToolbarLayout.setTitle(String.format(homePageFragment.getString(R.string.formatHomePageChildFragmentTitleOne), "明心见性", "得道逍遥"));
                break;
            case "星期五":
                collapsingToolbarLayout.setTitle("福生无量");
                break;
            case "星期六":
                collapsingToolbarLayout.setTitle("吉祥康宁");
                break;
            case "星期日":
                collapsingToolbarLayout.setTitle(String.format(homePageFragment.getString(R.string.formatHomePageChildFragmentTitleTwo), "心逍遥", "气长清", "安若素"));
                break;
            default:
                break;
        }
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
                view.setBackgroundColor(ContextCompat.getColor(context, com.zsp.core.R.color.white));
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
        List<Integer> imageList = new ArrayList<>(10);
        imageList.add(R.drawable.banner_three);
        imageList.add(R.drawable.banner_four);
        imageList.add(R.drawable.banner_five);
        imageList.add(R.drawable.banner_six);
        imageList.add(R.drawable.banner_seven);
        imageList.add(R.drawable.banner_eight);
        imageList.add(R.drawable.banner_nine);
        imageList.add(R.drawable.banner_ten);
        imageList.add(R.drawable.banner_eleven);
        imageList.add(R.drawable.banner_twelve);
        return imageList;
    }

    /**
     * 轮播
     *
     * @param recyclerView RecyclerView
     */
    public void carousel(RecyclerView recyclerView) {
        // 轮播条目集
        List<CarouselItem> carouselItemList = new ArrayList<>(10);
        carouselItemList.add(new CarouselItem(R.drawable.banner_three));
        carouselItemList.add(new CarouselItem(R.drawable.banner_four));
        carouselItemList.add(new CarouselItem(R.drawable.banner_five));
        carouselItemList.add(new CarouselItem(R.drawable.banner_six));
        carouselItemList.add(new CarouselItem(R.drawable.banner_seven));
        carouselItemList.add(new CarouselItem(R.drawable.banner_eight));
        carouselItemList.add(new CarouselItem(R.drawable.banner_nine));
        carouselItemList.add(new CarouselItem(R.drawable.banner_ten));
        carouselItemList.add(new CarouselItem(R.drawable.banner_eleven));
        carouselItemList.add(new CarouselItem(R.drawable.banner_twelve));
        // 轮播配套原件
        CarouselKit carouselKit = new CarouselKit();
        carouselKit.execute(recyclerView, carouselItemList, new HeroCarouselStrategy(), false, CarouselLayoutManager.ALIGNMENT_CENTER, false);
    }

    /**
     * 展示
     *
     * @param appCompatActivity     活动
     * @param homePageChildFragment 首页子碎片
     * @param recyclerView          控件
     */
    public void display(AppCompatActivity appCompatActivity, HomePageChildFragment homePageChildFragment, RecyclerView recyclerView) {
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
        menuAdapterKit.display(appCompatActivity, recyclerView, menuBeanList, 3, 48, 192, false, (view, menuBean) -> distribute(appCompatActivity, homePageChildFragment, menuBean.getMenuId()));
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
            // 用 menuName 和 menuIconResId
            // 因在 HomePageMenuEnum 变迁过程中，menuName 和 menuIconResId 可最大程度保持一一对应，而 menuId 顺序可能被打乱。
            // 场景：APP 原有三个主页菜单，某天新增了一个主页菜单。然而在没卸载的情况下重装了 APP，此时因 menuId 顺序可能已被打乱，从而导致 menuName 和 menuIconResId 显示不匹配。
            // 上述场景一般不会出现，且不允许出现。
            menuIconResIdMap.put(homePageMenuEnum.getMenuName(), homePageMenuEnum.getMenuIconResId());
        }
        return menuIconResIdMap;
    }

    /**
     * 分发
     *
     * @param appCompatActivity     活动
     * @param homePageChildFragment 首页子碎片
     * @param menuId                菜单 ID
     */
    private void distribute(AppCompatActivity appCompatActivity, HomePageChildFragment homePageChildFragment, int menuId) {
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
            // 组件
            case 4:
                RxBus.get().post(RxBusConstant.MAIN_ACTIVITY_$_BOTTOM_NAVIGATION_VIEW, RxBusConstant.MAIN_ACTIVITY_$_HIDE_BOTTOM_NAVIGATION_VIEW_CODE);
                homePageChildFragment.start(WidgetFragment.newInstance());
                break;
            default:
                break;
        }
    }
}