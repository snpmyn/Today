package widget.transition.kit;

import android.app.ActivityOptions;
import android.content.Intent;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.color.MaterialColors;
import com.google.android.material.transition.platform.MaterialContainerTransform;
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback;

import widget.transition.value.SharedElementConstant;

/**
 * Created on 2021/11/11
 *
 * @author zsp
 * @desc 过渡配套元件
 */
public class TransitionKit {
    public static TransitionKit getInstance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * 制造场面过渡动画
     *
     * @param appCompatActivity 活动
     * @param view              视图
     * @return ActivityOptions
     */
    private ActivityOptions makeSceneTransitionAnimation(AppCompatActivity appCompatActivity, View view) {
        return ActivityOptions.makeSceneTransitionAnimation(appCompatActivity, view, SharedElementConstant.SHARED_ELEMENT_END_ROOT);
    }

    /**
     * 构建材料容器变换
     *
     * @param appCompatActivity 活动
     * @param entering          进入中
     * @return 材料容器变换
     */
    @NonNull
    private MaterialContainerTransform buildMaterialContainerTransform(AppCompatActivity appCompatActivity, boolean entering) {
        MaterialContainerTransform materialContainerTransform = new MaterialContainerTransform(appCompatActivity, entering);
        // Use all 3 container layer colors since this transform can be configured using any fade mode and some of the start views don't have a background and the end view doesn't have a background.
        materialContainerTransform.setAllContainerColors(MaterialColors.getColor(appCompatActivity.findViewById(android.R.id.content), com.google.android.material.R.attr.colorSurface));
        materialContainerTransform.addTarget(android.R.id.content);
        return materialContainerTransform;
    }

    /**
     * 起始页设置
     *
     * @param appCompatActivity 活动
     */
    public void startPageSetting(@NonNull AppCompatActivity appCompatActivity) {
        // Set up shared element transition and disable overlay so views don't show above system bars.
        appCompatActivity.getWindow().setSharedElementsUseOverlay(false);
        appCompatActivity.getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        appCompatActivity.setExitSharedElementCallback(new MaterialContainerTransformSharedElementCallback());
    }

    /**
     * 结束页设置
     *
     * @param appCompatActivity 活动
     */
    public void endPageSetting(@NonNull AppCompatActivity appCompatActivity) {
        appCompatActivity.getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        appCompatActivity.getWindow().setSharedElementEnterTransition(buildMaterialContainerTransform(appCompatActivity, true));
        appCompatActivity.getWindow().setSharedElementReturnTransition(buildMaterialContainerTransform(appCompatActivity, false));
        // Set up shared element transition.
        appCompatActivity.findViewById(android.R.id.content).setTransitionName(SharedElementConstant.SHARED_ELEMENT_END_ROOT);
        appCompatActivity.setEnterSharedElementCallback(new MaterialContainerTransformSharedElementCallback());
    }

    /**
     * 过渡跳转
     *
     * @param appCompatActivity 活动
     * @param view              视图
     * @param intent            意图
     */
    public void jumpWithTransition(@NonNull AppCompatActivity appCompatActivity, View view, Intent intent) {
        appCompatActivity.startActivity(intent, makeSceneTransitionAnimation(appCompatActivity, view).toBundle());
    }

    private static final class InstanceHolder {
        static final TransitionKit INSTANCE = new TransitionKit();
    }
}