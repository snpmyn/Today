package widget.materialcontainertransform;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.transition.TransitionManager;

import com.google.android.material.transition.MaterialArcMotion;
import com.google.android.material.transition.MaterialContainerTransform;

import util.view.ViewUtils;

/**
 * Created on 2025/8/10.
 *
 * @author 郑少鹏
 * @desc 材料容器转换配套原件
 */
public class MaterialContainerTransformKit {
    public static MaterialContainerTransformKit getInstance() {
        return MaterialContainerTransformKit.InstanceHolder.INSTANCE;
    }

    /**
     * 设转换名
     *
     * @param view           视图
     * @param transitionName 转换名
     */
    public void setTransitionName(View view, String transitionName) {
        ViewCompat.setTransitionName(view, transitionName);
    }

    /**
     * 显示终止视图
     *
     * @param context   上下文
     * @param sceneRoot 根场景节点
     * @param StartView 开始视图
     * @param endView   终止视图
     * @param debug     调试
     */
    public void showEndView(Context context, ViewGroup sceneRoot, View StartView, View endView, boolean debug) {
        // Construct a container transform transition between two views.
        MaterialContainerTransform materialContainerTransform = buildContainerTransform(context, sceneRoot, true, debug);
        materialContainerTransform.setStartView(StartView);
        materialContainerTransform.setEndView(endView);
        // Add a single target to stop the container transform from running on both the start and end view.
        materialContainerTransform.addTarget(endView);
        // Trigger the container transform transition.
        TransitionManager.beginDelayedTransition(sceneRoot, materialContainerTransform);
        // Show the endView.
        ViewUtils.showView(endView);
    }

    /**
     * 显示开始视图
     *
     * @param context   上下文
     * @param sceneRoot 根场景节点
     * @param StartView 开始视图
     * @param endView   终止视图
     * @param debug     调试
     */
    public void showStartView(Context context, ViewGroup sceneRoot, View StartView, View endView, boolean debug) {
        // Construct a container transform transition between two views.
        MaterialContainerTransform materialContainerTransform = buildContainerTransform(context, sceneRoot, false, debug);
        materialContainerTransform.setStartView(endView);
        materialContainerTransform.setEndView(StartView);
        // Add a single target to stop the container transform from running on both the start and end view.
        materialContainerTransform.addTarget(StartView);
        // Trigger the container transform transition.
        TransitionManager.beginDelayedTransition(sceneRoot, materialContainerTransform);
        // Hide the endView.
        ViewUtils.hideView(endView, View.GONE);
    }

    /**
     * 构建容器转换
     *
     * @param context   上下文
     * @param sceneRoot 根场景节点
     * @param entering  正在进入
     * @param debug     调试
     * @return 材料容器转换
     */
    @NonNull
    private MaterialContainerTransform buildContainerTransform(Context context, @NonNull ViewGroup sceneRoot, boolean entering, boolean debug) {
        MaterialContainerTransform materialContainerTransform = new MaterialContainerTransform(context, entering);
        materialContainerTransform.setDuration(200L);
        materialContainerTransform.setDrawDebugEnabled(debug);
        materialContainerTransform.setScrimColor(Color.TRANSPARENT);
        materialContainerTransform.setDrawingViewId(sceneRoot.getId());
        materialContainerTransform.setPathMotion(new MaterialArcMotion());
        materialContainerTransform.setInterpolator(new FastOutSlowInInterpolator());
        materialContainerTransform.setFadeMode(MaterialContainerTransform.FADE_MODE_THROUGH);
        return materialContainerTransform;
    }

    private static final class InstanceHolder {
        static final MaterialContainerTransformKit INSTANCE = new MaterialContainerTransformKit();
    }
}
