package widget.view;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import timber.log.Timber;
import util.datetime.CurrentTimeMillisClock;
import util.handler.HandlerKit;

/**
 * @decs: 叶子视图
 * @author: 郑少鹏
 * @date: 2025/9/13 17:22
 * @version: v 1.0
 */
public class LeafView extends View {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final List<Leaf> leaves = new ArrayList<>();
    private final Random random = new Random();
    private ValueAnimator valueAnimator;
    private boolean areRunning = true;
    private int maxLeafCount = 50;
    private float windX = 0.0F;
    private float explosionSpeed = 0.008F;
    private OnLeafAnimationEndListener onLeafAnimationEndListener;
    private OnLeafExplosionListener leafExplosionListener;
    private boolean preCallbackTriggered = false;
    private long preCallbackDelayMs = 0;

    public LeafView(Context context) {
        super(context);
    }

    public LeafView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public interface OnLeafAnimationEndListener {
        void onAnimationEnd();
    }

    public interface OnLeafExplosionListener {
        void onExplosionStart();
    }

    public void setOnLeafAnimationEndListener(OnLeafAnimationEndListener onLeafAnimationEndListener) {
        this.onLeafAnimationEndListener = onLeafAnimationEndListener;
    }

    public void setOnLeafExplosionListener(OnLeafExplosionListener onLeafExplosionListener) {
        this.leafExplosionListener = onLeafExplosionListener;
    }

    public void setExplosionSpeed(float speed) {
        this.explosionSpeed = speed;
    }

    public void setPreCallbackDelayMs(long ms) {
        this.preCallbackDelayMs = ms;
    }

    public void setWindForce(float wind) {
        this.windX = wind;
    }

    public void configure(int leafCount, int maxLeafCount, int minDuration, int maxDuration, float swingRange) {
        this.maxLeafCount = maxLeafCount;
        int width = (getWidth() > 0) ? getWidth() : 1080;
        int height = (getHeight() > 0) ? getHeight() : 1920;
        leaves.clear();
        for (int i = 0; i < leafCount; i++) {
            Leaf leaf = createRandomLeaf(width, height, minDuration, maxDuration, swingRange);
            leaf.y = random.nextInt(height);
            leaves.add(leaf);
        }
        areRunning = true;
        startAnimation(minDuration, maxDuration, swingRange);
    }

    @NonNull
    private Leaf createRandomLeaf(int width, int height, int minDuration, int maxDuration, float swingRange) {
        Leaf leaf = new Leaf(width, height);
        float duration = (minDuration + random.nextFloat() * (maxDuration - minDuration));
        leaf.speedY = (20000.0F / duration);
        leaf.swing = (5.0F + random.nextFloat() * swingRange);
        return leaf;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        for (Leaf leaf : leaves) {
            paint.setColor(leaf.color);
            paint.setAlpha(leaf.alpha);
            canvas.save();
            canvas.translate(leaf.x, leaf.y);
            canvas.rotate(leaf.rotation);
            canvas.scale(leaf.scale, leaf.scale);
            canvas.drawPath(leaf.path, paint);
            canvas.restore();
        }
        if (areRunning) {
            invalidate();
        }
    }

    private void startAnimation(int minDuration, int maxDuration, float swingRange) {
        if ((null != valueAnimator) && valueAnimator.isRunning()) {
            return;
        }
        valueAnimator = ValueAnimator.ofFloat(0f, 1.0F);
        valueAnimator.setDuration(10000);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(animation -> {
            if (!areRunning) {
                return;
            }
            int width = (getWidth() > 0) ? getWidth() : 1080;
            int height = (getHeight() > 0) ? getHeight() : 1920;
            for (Leaf leaf : leaves) {
                if (!leaf.exploding) {
                    if (leaf.y < (height - 50)) {
                        leaf.y += leaf.speedY;
                        leaf.x += (float) (Math.sin((leaf.y + leaf.swayOffset) / 50.0F) * leaf.swing + windX);
                        leaf.rotation += leaf.rotationSpeed;
                    } else {
                        leaf.y = (height - 50);
                        leaf.x += (float) Math.sin(CurrentTimeMillisClock.getInstance().now() / 300.0 + leaf.swayOffset) * (2 + random.nextFloat() * 3);
                        leaf.rotation += (leaf.rotationSpeed / 5.0F);
                    }
                } else {
                    leaf.flyFraction += explosionSpeed;
                    leaf.x += (float) (leaf.speedX + Math.sin(leaf.flyFraction * 10 + leaf.offset) * leaf.swingOffset);
                    leaf.y += (leaf.speedY + leaf.gravity * leaf.flyFraction);
                    leaf.rotation += (leaf.rotationSpeed + leaf.flipSpeed);
                    leaf.alpha = (int) (255 * Math.max(0.0F, 1.0F - leaf.flyFraction));
                }
            }
            while (leaves.size() < maxLeafCount) {
                Leaf newLeaf = createRandomLeaf(width, height, minDuration, maxDuration, swingRange);
                newLeaf.y = -random.nextInt(height / 2);
                newLeaf.x = random.nextInt(width);
                leaves.add(newLeaf);
            }
            checkLeavesExploded();
        });
        valueAnimator.start();
    }

    private void checkLeavesExploded() {
        boolean anyExploding = false;
        boolean allGone = true;
        float maxFlyFraction = 0.0F;
        for (Leaf leaf : leaves) {
            if (leaf.exploding) {
                anyExploding = true;
                if (leaf.alpha > 0) {
                    allGone = false;
                }
                maxFlyFraction = Math.max(maxFlyFraction, leaf.flyFraction);
            }
        }
        if (!preCallbackTriggered && anyExploding) {
            boolean flag = ((preCallbackDelayMs > 0) && ((1.0F - maxFlyFraction) / explosionSpeed * 16.0F <= preCallbackDelayMs)) || ((preCallbackDelayMs <= 0) && allGone);
            if (flag) {
                preCallbackTriggered = true;
                if (null != onLeafAnimationEndListener) {
                    post(() -> {
                        try {
                            onLeafAnimationEndListener.onAnimationEnd();
                        } catch (Exception e) {
                            // 防回调异常导致卡顿
                            Timber.e(e);
                        }
                    });
                }
            }
        }
        if (anyExploding && allGone) {
            finishAnimation();
        }
    }

    public void finishAnimation() {
        // 停止运行状态
        areRunning = false;
        // 关闭动画
        if (null != valueAnimator) {
            valueAnimator.cancel();
            valueAnimator = null;
        }
        // 清空叶子
        // 避免继续触发 onDraw
        leaves.clear();
        // 恢复点击穿透
        setOnTouchListener(null);
        // 使用父容器异步移除
        // 避免和绘制冲突
        ViewGroup parent = (ViewGroup) getParent();
        if (null != parent) {
            parent.post(() -> {
                if (getParent() instanceof ViewGroup) {
                    ((ViewGroup) getParent()).removeView(this);
                }
            });
        }
    }

    public void triggerExplosion() {
        for (Leaf leaf : leaves) {
            leaf.exploding = true;
            double angle = (random.nextDouble() * 2 * Math.PI);
            float speed = (10 + random.nextFloat() * 10);
            leaf.speedX = ((float) Math.cos(angle) * speed);
            leaf.speedY = ((float) Math.sin(angle) * speed - 6.0F);
            leaf.gravity = (random.nextFloat() * 2.0F);
            leaf.flipSpeed = (random.nextFloat() * 20.0F - 10.0F);
            leaf.flyFraction = 0.0F;
            leaf.swingOffset = (5 + random.nextFloat() * 10);
            leaf.offset = (random.nextFloat() * 360.0F);
        }
        if (null != leafExplosionListener) {
            HandlerKit.getInstance().post(() -> leafExplosionListener.onExplosionStart());
        }
    }

    private int randomWarmColor() {
        int[] colors = {Color.rgb(205, 92, 92), Color.rgb(255, 140, 0), Color.rgb(255, 215, 0)};
        int idx = random.nextInt(colors.length);
        int color = colors[idx];
        int alpha = (200 + random.nextInt(56));
        return (alpha << 24) | (color & 0x00FFFFFF);
    }

    private class Leaf {
        float x, y, rotation, scale, speedY, swing, rotationSpeed;
        int color, alpha = 255;
        Path path;
        boolean exploding = false;
        float speedX, gravity, flyFraction, swayOffset, flipSpeed, swingOffset, offset;

        Leaf(int width, int height) {
            path = createLeafPath();
            color = randomWarmColor();
            scale = (0.8F + random.nextFloat() * 0.7F);
            if (random.nextBoolean()) {
                scale = -scale;
            }
            rotationSpeed = (1.0F + random.nextFloat() * 3.0F);
            if (random.nextBoolean()) {
                rotationSpeed = -rotationSpeed;
            }
            swayOffset = (random.nextFloat() * 360.0F);
            y = -random.nextInt(height);
            x = random.nextInt(width);
        }
    }

    @NonNull
    private Path createLeafPath() {
        Path path = new Path();
        path.moveTo(0, -30);
        path.lineTo(5, -10);
        path.lineTo(15, -15);
        path.lineTo(10, 0);
        path.lineTo(20, 5);
        path.lineTo(5, 10);
        path.lineTo(10, 20);
        path.lineTo(0, 15);
        path.lineTo(-10, 20);
        path.lineTo(-5, 10);
        path.lineTo(-20, 5);
        path.lineTo(-10, 0);
        path.lineTo(-15, -15);
        path.lineTo(-5, -10);
        path.close();
        return path;
    }

    private static void addToActivitySafe(Activity activity, LeafView leafView) {
        if ((null == activity) || (null == leafView)) {
            return;
        }
        HandlerKit.getInstance().post(() -> {
            ViewGroup root = activity.findViewById(android.R.id.content);
            if (null == root) {
                new Handler(Looper.getMainLooper()).postDelayed(() -> addToActivitySafe(activity, leafView), 50);
            } else {
                root.addView(leafView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
        });
    }

    /**
     * 开始叶子动画
     *
     * @param activity           活动
     * @param leafCount          初始叶子数量
     * @param maxLeafCount       最大叶子数量
     * @param minDuration        最小飘落周期 (ms)
     * @param maxDuration        最大飘落周期 (ms)
     * @param swingRange         摆动幅度
     * @param durationMs         总动画时长 (ms)
     * @param initialWind        初始风力
     * @param explosionSpeed     爆炸速度
     * @param preCallbackDelayMs 提前回调时间 (ms)
     * @param explosionAdvanceMs 爆炸提前时间 (ms)
     * @param endListener        动画结束回调
     * @param explosionListener  爆炸开始回调
     */
    public static void startLeafAnimation(Activity activity, int leafCount, int maxLeafCount, int minDuration, int maxDuration, float swingRange, long durationMs, float initialWind, float explosionSpeed, long preCallbackDelayMs, long explosionAdvanceMs, OnLeafAnimationEndListener endListener, OnLeafExplosionListener explosionListener) {
        if (null == activity) {
            return;
        }
        LeafView leafView = new LeafView(activity);
        leafView.configure(leafCount, maxLeafCount, minDuration, maxDuration, swingRange);
        leafView.setWindForce(initialWind);
        leafView.setExplosionSpeed(explosionSpeed);
        leafView.setPreCallbackDelayMs(preCallbackDelayMs);
        leafView.setOnLeafAnimationEndListener(endListener);
        leafView.setOnLeafExplosionListener(explosionListener);
        addToActivitySafe(activity, leafView);
        long triggerTime = durationMs;
        if ((explosionAdvanceMs > 0) && (explosionAdvanceMs < durationMs)) {
            triggerTime = (durationMs - explosionAdvanceMs);
        }
        new Handler(Looper.getMainLooper()).postDelayed(leafView::triggerExplosion, triggerTime);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            // 触发辅助功能点击事件
            performClick();
        }
        // 拦截所有触摸事件
        return true;
    }

    @Override
    public boolean performClick() {
        // 保证辅助功能正常
        super.performClick();
        return true;
    }
}