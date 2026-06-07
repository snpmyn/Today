package com.zsp.today.module.heartbox;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zsp.today.R;

import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created on 2026/6/5.
 *
 * @author 郑少鹏
 * @desc 全屏图片查看浮层：URL 加载、缩放、拖动（边界回弹）、双击重置、以图片中心旋转
 */
public class ImageViewerOverlay extends FrameLayout {

    // ---------- 缩放限制 ----------
    private static final float MIN_SCALE = 0.5f;
    private static final float MAX_SCALE = 6.0f;

    // ---------- 触摸模式 ----------
    private static final int TOUCH_NONE = 0;
    private static final int TOUCH_DRAG = 1;
    private static final int TOUCH_PINCH = 2;

    // ---------- 回弹参数 ----------
    // 手指拖动时允许超出边界的最大像素（产生阻尼感）
    private static final float OVERSCROLL_LIMIT = 80f;
    // 回弹动画时长
    private static final int SPRING_DURATION = 300;

    // ---------- 子视图 ----------
    private ImageView ivImage;
    private ProgressBar pbLoading;
    private TextView tvError;

    // ---------- 变换矩阵 ----------
    private final Matrix matrix = new Matrix();
    private final Matrix savedMatrix = new Matrix();

    // ---------- 当前旋转累计角度 ----------
    private float currentRotation = 0f;

    // ---------- 触摸辅助 ----------
    private int touchMode = TOUCH_NONE;
    private final PointF lastTouch = new PointF();
    private final PointF midPoint = new PointF();
    private float initDist = 1f;

    // ---------- 双击检测 ----------
    private long lastTapTime = 0;
    private static final long DOUBLE_TAP_MS = 300;

    // ---------- 回调 ----------
    private OnCloseListener onCloseListener;

    public interface OnCloseListener {
        void onClose();
    }

    // ======================================================
    //  构造
    // ======================================================

    public ImageViewerOverlay(Context context) {
        super(context);
        inflate(context);
    }

    public ImageViewerOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context);
    }

    // ======================================================
    //  Inflate XML 布局
    // ======================================================

    private void inflate(Context context) {
        LayoutInflater.from(context).inflate(R.layout.overlay_image_viewer, this, true);
        ivImage = findViewById(R.id.iv_image);
        pbLoading = findViewById(R.id.pb_loading);
        tvError = findViewById(R.id.tv_error);

        findViewById(R.id.iv_rotate_left).setOnClickListener(v -> rotateByMatrix(-90));
        findViewById(R.id.iv_rotate_right).setOnClickListener(v -> rotateByMatrix(90));
        findViewById(R.id.iv_close).setOnClickListener(v -> dismiss());

        ivImage.setOnTouchListener(this::handleImageTouch);
    }

    // ======================================================
    //  公开 API
    // ======================================================

    public void show(ViewGroup rootView, String imageUrl) {
        resetState();
        if (getParent() == null) {
            rootView.addView(this, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        new LoadImageTask(this).execute(imageUrl);
    }

    public void dismiss() {
        if (getParent() instanceof ViewGroup) {
            ((ViewGroup) getParent()).removeView(this);
        }
        if (onCloseListener != null) onCloseListener.onClose();
    }

    public void setOnCloseListener(OnCloseListener listener) {
        this.onCloseListener = listener;
    }

    // ======================================================
    //  旋转：以图片当前可见中心为轴心
    // ======================================================

    private void rotateByMatrix(float degrees) {
        PointF imgCenter = getImageCenter();
        final float[] lastAngle = {0f};
        ValueAnimator animator = ValueAnimator.ofFloat(0f, degrees);
        animator.setDuration(280);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(anim -> {
            float current = (float) anim.getAnimatedValue();
            float delta = current - lastAngle[0];
            lastAngle[0] = current;
            matrix.postRotate(delta, imgCenter.x, imgCenter.y);
            ivImage.setImageMatrix(matrix);
        });
        animator.start();
        currentRotation += degrees;
    }

    private PointF getImageCenter() {
        if (ivImage.getDrawable() == null) {
            return new PointF(getWidth() / 2f, getHeight() / 2f);
        }
        int bmpW = ivImage.getDrawable().getIntrinsicWidth();
        int bmpH = ivImage.getDrawable().getIntrinsicHeight();
        float[] corners = {0, 0, bmpW, 0, bmpW, bmpH, 0, bmpH};
        matrix.mapPoints(corners);
        return new PointF((corners[0] + corners[4]) / 2f, (corners[1] + corners[5]) / 2f);
    }

    // ======================================================
    //  触摸手势：拖动（含阻尼 + 回弹）、双指缩放、双击重置
    // ======================================================

    private boolean handleImageTouch(View v, MotionEvent event) {
        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                lastTouch.set(event.getX(), event.getY());
                touchMode = TOUCH_DRAG;
                // 双击检测
                long now = System.currentTimeMillis();
                if (now - lastTapTime < DOUBLE_TAP_MS) resetTransform();
                lastTapTime = now;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getPointerCount() >= 2) {
                    initDist = spacing(event);
                    if (initDist > 5f) {
                        savedMatrix.set(matrix);
                        midPoint(midPoint, event);
                        touchMode = TOUCH_PINCH;
                    }
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (touchMode == TOUCH_DRAG) {
                    float rawDx = event.getX() - lastTouch.x;
                    float rawDy = event.getY() - lastTouch.y;

                    // 先预算出平移后的图片 Rect
                    Matrix testMatrix = new Matrix(savedMatrix);
                    testMatrix.postTranslate(rawDx, rawDy);
                    RectF imgRect = getImageRect(testMatrix);

                    // 对超出边界的分量施加阻尼，给用户"快到边了"的手感
                    float dx = rawDx;
                    float dy = rawDy;
                    float overX = calcOverscroll(imgRect, true);
                    float overY = calcOverscroll(imgRect, false);
                    if (overX != 0) dx = rawDx * damping(Math.abs(overX));
                    if (overY != 0) dy = rawDy * damping(Math.abs(overY));

                    matrix.set(savedMatrix);
                    matrix.postTranslate(dx, dy);
                    ivImage.setImageMatrix(matrix);

                } else if (touchMode == TOUCH_PINCH && event.getPointerCount() >= 2) {
                    float newDist = spacing(event);
                    if (newDist > 5f) {
                        float scale = newDist / initDist;
                        float[] values = new float[9];
                        savedMatrix.getValues(values);
                        float curScale = values[Matrix.MSCALE_X];
                        float next = curScale * scale;
                        if (next < MIN_SCALE) scale = MIN_SCALE / curScale;
                        if (next > MAX_SCALE) scale = MAX_SCALE / curScale;
                        matrix.set(savedMatrix);
                        matrix.postScale(scale, scale, midPoint.x, midPoint.y);
                        ivImage.setImageMatrix(matrix);
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                touchMode = TOUCH_NONE;
                // 手指抬起后，检查是否超出边界，超出则回弹
                springBack();
                break;
        }
        return true;
    }

    // ======================================================
    //  边界计算
    // ======================================================

    /**
     * 获取当前 matrix 映射后图片的边界矩形（View 坐标系）
     */
    private RectF getImageRect(Matrix m) {
        if (ivImage.getDrawable() == null) return new RectF();
        RectF rect = new RectF(0, 0, ivImage.getDrawable().getIntrinsicWidth(), ivImage.getDrawable().getIntrinsicHeight());
        m.mapRect(rect);
        return rect;
    }

    /**
     * 计算在某个 matrix 下图片超出屏幕的像素量。
     * 返回 0 表示未超出；正数/负数表示超出方向和大小。
     *
     * @param horizontal true=检查水平方向，false=检查垂直方向
     */
    private float calcOverscroll(RectF imgRect, boolean horizontal) {
        if (horizontal) {
            float imgW = imgRect.width();
            float viewW = getWidth();
            if (imgW <= viewW) {
                // 图片比屏幕窄：图片应整体居中，检查偏离居中的量
                float centerOffset = imgRect.left - (viewW - imgW) / 2f;
                return centerOffset; // 非 0 则偏离了居中位置
            } else {
                // 图片比屏幕宽：左边不能露白（left > 0），右边不能露白（right < viewW）
                if (imgRect.left > 0) return imgRect.left;           // 左侧露白
                if (imgRect.right < viewW) return imgRect.right - viewW; // 右侧露白
            }
        } else {
            float imgH = imgRect.height();
            float viewH = getHeight();
            if (imgH <= viewH) {
                float centerOffset = imgRect.top - (viewH - imgH) / 2f;
                return centerOffset;
            } else {
                if (imgRect.top > 0) return imgRect.top;
                if (imgRect.bottom < viewH) return imgRect.bottom - viewH;
            }
        }
        return 0f;
    }

    /**
     * 阻尼系数：超出越多，阻力越大，最终趋近 0（不可能无限拖出去）
     * 公式：factor = LIMIT / (LIMIT + overscroll)，使拖动量平滑衰减
     */
    private float damping(float overscroll) {
        return OVERSCROLL_LIMIT / (OVERSCROLL_LIMIT + overscroll);
    }

    // ======================================================
    //  回弹动画
    //
    //  手指抬起后，计算图片需要平移多少才能回到合法区域，
    //  然后用 ValueAnimator 平滑插值。
    // ======================================================

    private void springBack() {
        RectF imgRect = getImageRect(matrix);
        float overX = calcOverscroll(imgRect, true);
        float overY = calcOverscroll(imgRect, false);

        // 没有超出，不需要回弹
        if (overX == 0f && overY == 0f) return;

        // 需要回弹的修正量（反向平移）
        final float fixDx = -overX;
        final float fixDy = -overY;

        final Matrix startMatrix = new Matrix(matrix);
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(SPRING_DURATION);
        animator.setInterpolator(new DecelerateInterpolator(1.8f));
        animator.addUpdateListener(anim -> {
            float fraction = (float) anim.getAnimatedValue();
            matrix.set(startMatrix);
            matrix.postTranslate(fixDx * fraction, fixDy * fraction);
            ivImage.setImageMatrix(matrix);
        });
        animator.start();
    }

    // ======================================================
    //  图片居中适配 / 重置
    // ======================================================

    private void centerAndFit(int bmpW, int bmpH) {
        if (bmpW <= 0 || bmpH <= 0 || getWidth() <= 0 || getHeight() <= 0) return;
        float scale = Math.min((float) getWidth() / bmpW, (float) getHeight() / bmpH);
        float dx = (getWidth() - bmpW * scale) / 2f;
        float dy = (getHeight() - bmpH * scale) / 2f;
        matrix.reset();
        matrix.postScale(scale, scale);
        matrix.postTranslate(dx, dy);
        ivImage.setImageMatrix(matrix);
        currentRotation = 0f;
    }

    private void resetTransform() {
        if (ivImage.getDrawable() != null) {
            ivImage.post(() -> centerAndFit(ivImage.getDrawable().getIntrinsicWidth(), ivImage.getDrawable().getIntrinsicHeight()));
        }
    }

    private void resetState() {
        matrix.reset();
        currentRotation = 0f;
        ivImage.setImageBitmap(null);
        ivImage.setRotation(0f);
        pbLoading.setVisibility(View.VISIBLE);
        tvError.setVisibility(View.GONE);
    }

    // ======================================================
    //  图片加载完成
    // ======================================================

    void onImageLoaded(Bitmap bitmap) {
        pbLoading.setVisibility(View.GONE);
        if (bitmap == null) {
            tvError.setText("图片加载失败");
            tvError.setVisibility(View.VISIBLE);
            return;
        }
        ivImage.setImageBitmap(bitmap);
        ivImage.post(() -> centerAndFit(bitmap.getWidth(), bitmap.getHeight()));
    }

    // ======================================================
    //  工具方法
    // ======================================================

    private static float spacing(MotionEvent e) {
        float dx = e.getX(0) - e.getX(1);
        float dy = e.getY(0) - e.getY(1);
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    private static void midPoint(PointF out, MotionEvent e) {
        out.set((e.getX(0) + e.getX(1)) / 2f, (e.getY(0) + e.getY(1)) / 2f);
    }

    // ======================================================
    //  异步图片加载
    // ======================================================

    @SuppressWarnings("deprecation")
    private static class LoadImageTask extends AsyncTask<String, Void, Bitmap> {

        private final WeakReference<ImageViewerOverlay> ref;

        LoadImageTask(ImageViewerOverlay overlay) {
            this.ref = new WeakReference<>(overlay);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(params[0]).openConnection();
                conn.setConnectTimeout(10_000);
                conn.setReadTimeout(15_000);
                conn.setDoInput(true);
                conn.connect();
                return BitmapFactory.decodeStream(conn.getInputStream());
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            ImageViewerOverlay overlay = ref.get();
            if (overlay != null) overlay.onImageLoaded(bitmap);
        }
    }
}