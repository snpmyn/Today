package widget.view;

import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;

import com.zsp.core.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import util.list.ListUtils;

/**
 * @decs: 账目线段视图
 * @author: 郑少鹏
 * @date: 2025/10/4 15:59
 * @version: v 1.1
 */
public class AccountLineView extends View {
    private Paint linePaint;
    private Paint textPaint;
    private float lineHeight = 60.0F;
    /**
     * 默认间距
     */
    private float itemSpacing = 20.0F;
    private int textColor = Color.DKGRAY;
    private float textSize = 42.0F;
    private boolean textBold = false;
    /**
     * 默认左右留白
     */
    private float minSidePadding = 40.0F;
    private final int[] defaultColors = new int[]{Color.parseColor("#E64A19"), Color.parseColor("#D84315"), Color.parseColor("#BF360C"), Color.parseColor("#F57C00"), Color.parseColor("#FB8C00"), Color.parseColor("#F9A825"), Color.parseColor("#FF8A65"), Color.parseColor("#F06292"), Color.parseColor("#EC407A"), Color.parseColor("#AB47BC"), Color.parseColor("#8E24AA"), Color.parseColor("#5E35B1"), Color.parseColor("#3949AB"), Color.parseColor("#1E88E5"), Color.parseColor("#039BE5"), Color.parseColor("#43A047"), Color.parseColor("#388E3C"), Color.parseColor("#FFA726"), Color.parseColor("#FF7043"), Color.parseColor("#D81B60"), Color.parseColor("#8E24AA"), Color.parseColor("#6A1B9A"), Color.parseColor("#FFB300")};
    private final RectF rect = new RectF();

    public enum TextMode {VALUE, PERCENT}

    private TextMode textMode = TextMode.VALUE;

    public static class AccountItem {
        public String category;
        public float amount;
        public int color;

        public AccountItem(String category, float amount, int color) {
            this.category = category;
            this.amount = amount;
            this.color = color;
        }
    }

    private List<AccountItem> accountItems = new ArrayList<>();
    private float[] animationProgress;

    public AccountLineView(Context context) {
        super(context);
        init(null);
    }

    public AccountLineView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(attributeSet);
    }

    public AccountLineView(Context context, AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
        init(attributeSet);
    }

    private void init(AttributeSet attributeSet) {
        if (null != attributeSet) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.AccountLineView);
            minSidePadding = typedArray.getDimension(R.styleable.AccountLineView_minSidePadding, 40.0F);
            typedArray.recycle();
        }
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStyle(Paint.Style.FILL);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setFakeBoldText(textBold);
    }

    public void setLineHeight(float height) {
        lineHeight = height;
        invalidate();
    }

    public void setItemSpacing(float spacing) {
        itemSpacing = spacing;
        invalidate();
    }

    public void setTextColor(int color) {
        textColor = color;
        textPaint.setColor(color);
        invalidate();
    }

    public void setTextSize(float size) {
        textSize = size;
        textPaint.setTextSize(size);
        invalidate();
    }

    public void setTextBold(boolean bold) {
        textBold = bold;
        textPaint.setFakeBoldText(bold);
        invalidate();
    }

    public void setTextMode(TextMode mode) {
        textMode = mode;
        invalidate();
    }

    public void setMinSidePadding(float padding) {
        minSidePadding = padding;
        invalidate();
    }

    public void setAccountItems(@NonNull List<AccountItem> accountItems) {
        this.accountItems = accountItems;
        for (int i = 0; i < accountItems.size(); i++) {
            if (accountItems.get(i).color == 0) {
                accountItems.get(i).color = defaultColors[i % defaultColors.length];
            }
        }
        animationProgress = new float[accountItems.size()];
        startSequentialAnimation();
        requestLayout();
    }

    private void startSequentialAnimation() {
        if (ListUtils.listIsEmpty(accountItems)) {
            return;
        }
        animateItem(0, 400);
    }

    private void animateItem(final int index, final float duration) {
        if (index >= accountItems.size()) {
            return;
        }
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.0F, 1.0F);
        valueAnimator.setDuration((long) duration);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(animation -> {
            animationProgress[index] = (float) animation.getAnimatedValue();
            invalidate();
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                animateItem(index + 1, duration);
            }
        });
        valueAnimator.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int count = accountItems.size();
        int desiredHeight = (int) (count * lineHeight + Math.max(0, count - 1) * itemSpacing);
        int finalHeight;
        if (heightMode == MeasureSpec.EXACTLY) {
            finalHeight = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            finalHeight = Math.min(desiredHeight, heightSize);
        } else {
            finalHeight = desiredHeight;
        }
        setMeasuredDimension(width, finalHeight);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        if (ListUtils.listIsEmpty(accountItems)) {
            return;
        }
        float maxAmount = 0.0F, totalAmount = 0.0F;
        for (AccountItem accountItem : accountItems) {
            maxAmount = Math.max(maxAmount, accountItem.amount);
            totalAmount += accountItem.amount;
        }
        if (maxAmount == 0) {
            maxAmount = 1;
        }
        if (totalAmount == 0) {
            totalAmount = 1;
        }
        int count = accountItems.size();
        float availableWidth = getWidth();
        for (int i = 0; i < count; i++) {
            AccountItem accountItem = accountItems.get(i);
            float centerY = (i * (lineHeight + itemSpacing) + lineHeight / 2.0F);
            // 生成标签
            String label;
            if (textMode == TextMode.PERCENT) {
                float percent = (accountItem.amount / totalAmount * 100.0F);
                if ((percent > 0) && (percent < 0.1F)) {
                    label = (accountItem.category + "  小于 0.1%");
                } else if (percent == 0.0F) {
                    label = (accountItem.category + "  0%");
                } else {
                    label = (accountItem.category + "  " + String.format(Locale.getDefault(), "%.1f%%", percent));
                }
            } else {
                label = (accountItem.category + "  " + accountItem.amount);
            }
            float textWidth = textPaint.measureText(label);
            // 保证左右最小留白
            float minLineLength = (textWidth + minSidePadding * 2);
            float lineLength = (availableWidth * (accountItem.amount / maxAmount) * animationProgress[i]);
            if (lineLength < minLineLength) {
                lineLength = minLineLength;
            }
            lineLength = Math.min(lineLength, availableWidth);
            // 绘制线段
            rect.set(0.0F, centerY - lineHeight / 2.0F, lineLength, centerY + lineHeight / 2.0F);
            linePaint.setColor(accountItem.color);
            canvas.drawRoundRect(rect, lineHeight / 2.0F, lineHeight / 2.0F, linePaint);
            // 绘制文字
            float textX = (rect.left + lineLength / 2.0F);
            Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
            float textY = (centerY + (fontMetrics.bottom - fontMetrics.top) / 2.0F - fontMetrics.bottom);
            canvas.drawText(label, textX, textY, textPaint);
        }
    }
}