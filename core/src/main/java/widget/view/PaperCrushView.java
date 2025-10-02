package widget.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

import com.zsp.core.R;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import util.datetime.CurrentTimeMillisClock;
import util.list.ListUtils;

/**
 * @decs: 纸张粉碎视图
 * @author: 郑少鹏
 * @date: 2025/9/29 15:19
 * @version: v 1.0
 */
public class PaperCrushView extends View {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Random random = new Random();
    private final Path tmpPath = new Path();
    /**
     * 粒子池
     */
    private final Deque<PaperParticle> particlePool = new ArrayDeque<>(200);
    private final List<PaperParticle> centerParticles = new ArrayList<>();
    private final List<PaperParticle> topParticles = new ArrayList<>();
    private float maxRadius, currentRadius = 0.0F;
    private int centerX, centerY;
    private float volume = 0.0F;
    /**
     * 光圈渐变缓存
     */
    private RadialGradient cachedGradient;
    private float cachedRadius = -1;
    /**
     * FPS 监控
     */
    private long lastFrameTime = 0;
    private float fps = 60.0F;
    private int maxCenterParticles = 150;
    private int maxTopParticles = 80;
    private boolean initialized = false;
    private boolean autoInit = true;

    public PaperCrushView(Context context) {
        super(context);
    }

    public PaperCrushView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        if (null != attributeSet) {
            TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.PaperCrushView);
            autoInit = typedArray.getBoolean(R.styleable.PaperCrushView_autoInit, true);
            typedArray.recycle();
        }
        if (autoInit) {
            post(this::initView);
        }
    }

    public PaperCrushView(Context context, AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
        if (null != attributeSet) {
            TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.PaperCrushView);
            autoInit = typedArray.getBoolean(R.styleable.PaperCrushView_autoInit, true);
            typedArray.recycle();
        }
        if (autoInit) {
            post(this::initView);
        }
    }

    public void initView() {
        if (initialized) {
            return;
        }
        paint.setStyle(Paint.Style.FILL);
        tmpPath.reset();
        for (int i = particlePool.size(); i < 200; i++) {
            particlePool.add(new PaperParticle());
        }
        initialized = true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = (w / 2);
        centerY = (h / 2);
        maxRadius = (Math.min(w, h) / 3.0F);
    }

    public void setVolume(final float v) {
        float clipped = Math.max(0.0F, Math.min(1.0F, v));
        if (Looper.myLooper() == Looper.getMainLooper()) {
            setVolumeOnUiThread(clipped);
        } else {
            post(() -> setVolumeOnUiThread(clipped));
        }
    }

    private void setVolumeOnUiThread(float v) {
        this.volume = v;
        if (!initialized) {
            return;
        }
        generateCenterParticles(v);
        generateTopParticles(v);
        currentRadius = (maxRadius * v);
        invalidate();
    }

    private void generateCenterParticles(float volume) {
        int count = (int) (volume * 30);
        if (volume > 0.8F) {
            count += 15;
        }
        if ((centerParticles.size() + count) > maxCenterParticles) {
            count = Math.max(0, maxCenterParticles - centerParticles.size());
        }
        for (int i = 0; i < count; i++) {
            PaperParticle paperParticle = obtainParticle();
            float width = (random.nextFloat() * 20 + 10);
            float height = (width * (0.6F + random.nextFloat() * 0.4F));
            float angle = (random.nextFloat() * 360.0F);
            float speed = ((random.nextFloat() * 6) + 2);
            float vx = (float) Math.cos(Math.toRadians(angle)) * speed;
            float vy = (float) Math.sin(Math.toRadians(angle)) * speed;
            paperParticle.reset(centerX, centerY, width, height, Color.argb(255, 255, 180 + random.nextInt(55), 180 + random.nextInt(55)), vx, vy, random.nextFloat() * 360, (random.nextFloat() - 0.5F) * 10);
            centerParticles.add(paperParticle);
        }
    }

    private void generateTopParticles(float volume) {
        int count = (int) (volume * 10);
        if ((topParticles.size() + count) > maxTopParticles) {
            count = Math.max(0, maxTopParticles - topParticles.size());
        }
        for (int i = 0; i < count; i++) {
            PaperParticle paperParticle = obtainParticle();
            float width = (random.nextFloat() * 15 + 5);
            float height = (width * (0.5F + random.nextFloat() * 0.5F));
            float x = (getWidth() > 0) ? (random.nextFloat() * getWidth()) : centerX;
            float y = (random.nextFloat() * 50);
            float vy = ((random.nextFloat() * 2) + 1);
            paperParticle.reset(x, y, width, height, Color.argb(120, 255, 200 + random.nextInt(55), 200 + random.nextInt(55)), 0, vy, random.nextFloat() * 360, (random.nextFloat() - 0.5F) * 5);
            topParticles.add(paperParticle);
        }
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        if (!initialized) {
            return;
        }
        super.onDraw(canvas);
        long now = System.nanoTime();
        if (lastFrameTime > 0) {
            float frameMs = ((now - lastFrameTime) / 1_000_000.0F);
            fps = (1000.0F / Math.max(1.0F, frameMs));
            adjustParticleLimits();
        }
        lastFrameTime = now;
        drawRadialGradient(canvas);
        drawParticles(canvas, topParticles, getHeight() / 2.0F);
        drawParticles(canvas, centerParticles, getHeight());
        if (ListUtils.listIsNotEmpty(topParticles) || ListUtils.listIsNotEmpty(centerParticles) || (volume > 0)) {
            postInvalidateOnAnimation();
        } else if (currentRadius > 0) {
            currentRadius *= 0.9F;
            if (currentRadius < 1.0F) {
                currentRadius = 0;
            }
            postInvalidateOnAnimation();
        }
    }

    private void drawRadialGradient(Canvas canvas) {
        if (currentRadius <= 0) {
            return;
        }
        float time = (CurrentTimeMillisClock.getInstance().now() % 1000 / 1000.0F);
        float radius = (currentRadius * (1 + 0.15F * (float) Math.sin(time * 2 * Math.PI)));
        if ((null == cachedGradient) || (Math.abs(radius - cachedRadius) > 2.0F)) {
            int centerRed = 255;
            int centerGreen = (170 + (int) (30 * Math.sin(time * 2 * Math.PI)));
            int centerBlue = (170 + (int) (30 * Math.sin(time * 2 * Math.PI)));
            int alpha = 180;
            cachedGradient = new RadialGradient(centerX, centerY, Math.max(1.0F, radius), Color.argb(alpha, centerRed, centerGreen, centerBlue), Color.argb(0, 255, 200, 200), Shader.TileMode.CLAMP);
            cachedRadius = radius;
        }
        paint.setShader(cachedGradient);
        canvas.drawCircle(centerX, centerY, radius, paint);
        paint.setShader(null);
    }

    private void drawParticles(Canvas canvas, @NonNull List<PaperParticle> list, float maxY) {
        Iterator<PaperParticle> iterator = list.iterator();
        while (iterator.hasNext()) {
            PaperParticle paperParticle = iterator.next();
            paperParticle.vy += 0.05F;
            paperParticle.x += (paperParticle.vx + (float) Math.sin(paperParticle.y / 15.0F) * 1.5F);
            paperParticle.y += paperParticle.vy;
            paperParticle.rotation += paperParticle.rotationSpeed;
            paperParticle.alpha -= 4;
            if ((paperParticle.alpha <= 0) || (paperParticle.y > maxY)) {
                iterator.remove();
                recycleParticle(paperParticle);
                continue;
            }
            paint.setColor(paperParticle.color);
            paint.setAlpha(Math.max(paperParticle.alpha, 0));
            canvas.save();
            canvas.rotate(paperParticle.rotation, paperParticle.x, paperParticle.y);
            tmpPath.reset();
            tmpPath.moveTo(paperParticle.x - paperParticle.width / 2, paperParticle.y - paperParticle.height / 2);
            tmpPath.lineTo(paperParticle.x + paperParticle.width / 2, paperParticle.y - paperParticle.height / 2);
            tmpPath.lineTo(paperParticle.x + paperParticle.width / 2, paperParticle.y + paperParticle.height / 2 - paperParticle.width / 4);
            tmpPath.lineTo(paperParticle.x - paperParticle.width / 2, paperParticle.y + paperParticle.height / 2);
            tmpPath.close();
            canvas.drawPath(tmpPath, paint);
            canvas.restore();
        }
    }

    private void adjustParticleLimits() {
        if (fps < 30.0F) {
            maxCenterParticles = Math.max(50, maxCenterParticles - 5);
            maxTopParticles = Math.max(30, maxTopParticles - 2);
        } else if (fps > 50.0F) {
            maxCenterParticles = Math.min(150, maxCenterParticles + 2);
            maxTopParticles = Math.min(80, maxTopParticles + 1);
        }
    }

    private PaperParticle obtainParticle() {
        return particlePool.isEmpty() ? new PaperParticle() : particlePool.poll();
    }

    private void recycleParticle(@NonNull PaperParticle paperParticle) {
        paperParticle.reset(0, 0, 0, 0, 0, 0, 0, 0, 0);
        if (particlePool.size() < 200) {
            particlePool.offer(paperParticle);
        }
    }

    static class PaperParticle {
        float x, y, width, height, vx, vy, rotation, rotationSpeed;
        int color, alpha;

        void reset(float x, float y, float width, float height, int color, float vx, float vy, float rotation, float rotationSpeed) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.color = color;
            this.vx = vx;
            this.vy = vy;
            this.rotation = rotation;
            this.rotationSpeed = rotationSpeed;
            this.alpha = 255;
        }
    }
}