package widget.dialog.materialalertdialog.kit;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.zsp.core.R;

import widget.carousel.CarouselItem;

/**
 * Created on 2025/9/26.
 *
 * @author 郑少鹏
 * @desc 图片信息材料对话框配套元件
 */
public class PictureInfoMaterialAlertDialogKit {
    public static PictureInfoMaterialAlertDialogKit getInstance() {
        return PictureInfoMaterialAlertDialogKit.InstanceHolder.INSTANCE;
    }

    /**
     * 显示
     *
     * @param appCompatActivity 活动
     * @param carouselItem      轮播条目
     */
    public void show(@NonNull AppCompatActivity appCompatActivity, @NonNull CarouselItem carouselItem) {
        View view = appCompatActivity.getLayoutInflater().inflate(R.layout.alert_dialog_picture_info_material, null);
        // ImageView
        ImageView carouselDetailIv = view.findViewById(R.id.progressMaterialAlertDialogIv);
        carouselDetailIv.setImageResource(carouselItem.getDrawableResId());
        // TextView
        TextView carouselDetailTv = view.findViewById(R.id.progressMaterialAlertDialogTv);
        carouselDetailTv.setText(carouselItem.getDrawableDescribe());
        new MaterialAlertDialogBuilderKit(appCompatActivity).setView(view).show();
    }

    private static final class InstanceHolder {
        static final PictureInfoMaterialAlertDialogKit INSTANCE = new PictureInfoMaterialAlertDialogKit();
    }
}