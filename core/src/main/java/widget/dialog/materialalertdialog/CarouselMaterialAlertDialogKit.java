package widget.dialog.materialalertdialog;

import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.carousel.CarouselLayoutManager;
import com.google.android.material.carousel.CarouselStrategy;
import com.google.android.material.carousel.FullScreenCarouselStrategy;
import com.google.android.material.carousel.HeroCarouselStrategy;
import com.zsp.core.R;

import java.util.List;

import util.animation.AnimationManager;
import util.density.DensityUtils;
import util.layoutparams.LayoutParamsUtils;
import util.screen.ScreenUtils;
import widget.carousel.CarouselItem;
import widget.carousel.CarouselKit;
import widget.carousel.CarouselListener;

/**
 * Created on 2025/10/14.
 *
 * @author 郑少鹏
 * @desc 轮播材料对话框配套元件
 */
public class CarouselMaterialAlertDialogKit {
    public static CarouselMaterialAlertDialogKit getInstance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * 显示
     *
     * @param appCompatActivity                      活动
     * @param carouselItemList                       轮播条目集
     * @param mode                                   模式
     *                                               1 - 横向
     *                                               2 - 竖向
     * @param carouselMaterialAlertDialogKitListener 轮播材料对话框配套元件监听
     */
    public void show(AppCompatActivity appCompatActivity, List<CarouselItem> carouselItemList, int mode, CarouselMaterialAlertDialogKitListener carouselMaterialAlertDialogKitListener) {
        AlertDialog alertDialog = new MaterialAlertDialogBuilderKit(appCompatActivity, R.style.ThemeOverlay_Catalog_MaterialAlertDialog_Centered_FullWidthButtons).create();
        // 轮播条目
        final CarouselItem[] carouselItemResult = new CarouselItem[1];
        // 视图
        View view = appCompatActivity.getLayoutInflater().inflate(R.layout.alert_dialog_carousel_material, null);
        // RecyclerView
        RecyclerView carouselMaterialAlertDialogRv = view.findViewById(R.id.carouselMaterialAlertDialogRv);
        // 关闭按钮
        MaterialButton carouselMaterialAlertDialogMbClose = view.findViewById(R.id.carouselMaterialAlertDialogMbClose);
        carouselMaterialAlertDialogMbClose.setOnClickListener(v -> alertDialog.dismiss());
        // 使用按钮
        MaterialButton carouselMaterialAlertDialogMbUse = view.findViewById(R.id.carouselMaterialAlertDialogMbUse);
        carouselMaterialAlertDialogMbUse.setOnClickListener(v -> {
            if (null != carouselMaterialAlertDialogKitListener) {
                CarouselItem carouselItem = carouselItemResult[0];
                if (null == carouselItem) {
                    AnimationManager.xyScale(carouselMaterialAlertDialogMbUse, 400, null);
                } else {
                    carouselMaterialAlertDialogKitListener.use(alertDialog, carouselItemResult[0]);
                }
            }
        });
        // 轮播策略
        CarouselStrategy carouselStrategy = null;
        // 方向
        int orientation = -1;
        // 左边距
        int left = 0;
        // 上边距
        int top = 0;
        // 右边距
        int right = 0;
        // 下边距
        int bottom = 0;
        if (mode == 1) {
            // 横向
            LayoutParamsUtils.setViewLayoutParams(carouselMaterialAlertDialogRv, false, 0, true, DensityUtils.dipToPxByInt(200));
            carouselStrategy = new HeroCarouselStrategy();
            orientation = RecyclerView.HORIZONTAL;
            left = 4;
            right = 4;
        } else if (mode == 2) {
            // 竖向
            LayoutParamsUtils.setViewLayoutParams(carouselMaterialAlertDialogRv, false, 0, true, ScreenUtils.screenHeight(appCompatActivity) / 2);
            carouselStrategy = new FullScreenCarouselStrategy();
            orientation = RecyclerView.VERTICAL;
            top = 4;
            bottom = 4;
        }
        // 轮播配套原件
        CarouselKit carouselKit = new CarouselKit();
        carouselKit.execute(carouselMaterialAlertDialogRv, carouselItemList, carouselStrategy, orientation, false, CarouselLayoutManager.ALIGNMENT_CENTER, false, left, top, right, bottom, true, new CarouselListener() {
            @Override
            public void onItemClick(CarouselItem carouselItem, int position) {
                carouselItemResult[0] = carouselItem;
                carouselMaterialAlertDialogMbUse.setText(carouselItem.getDrawableDescribe());
            }

            @Override
            public void onItemLongClick(CarouselItem carouselItem, int position) {
                carouselItemResult[0] = carouselItem;
                carouselMaterialAlertDialogMbUse.setText(carouselItem.getDrawableDescribe());
            }
        });
        // 设置视图
        alertDialog.setView(view);
        // 可取消否
        alertDialog.setCancelable(false);
        // 显示
        alertDialog.show();
    }

    /**
     * 轮播材料对话框配套元件监听
     */
    public interface CarouselMaterialAlertDialogKitListener {
        /**
         * 使用
         *
         * @param alertDialog  AlertDialog
         * @param carouselItem 轮播条目
         */
        void use(AlertDialog alertDialog, CarouselItem carouselItem);
    }

    private static final class InstanceHolder {
        static final CarouselMaterialAlertDialogKit INSTANCE = new CarouselMaterialAlertDialogKit();
    }
}