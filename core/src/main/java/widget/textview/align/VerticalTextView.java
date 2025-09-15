package widget.textview.align;

import static android.view.MotionEvent.ACTION_MOVE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;

import com.zsp.core.R;

import org.jetbrains.annotations.Contract;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import timber.log.Timber;
import util.density.DensityUtils;
import util.keyboard.KeyboardUtils;
import util.screen.ScreenUtils;
import util.statusbar.StatusBarUtils;
import util.vibrator.VibratorUtils;
import widget.toast.ToastKit;
import widget.value.WidgetMagic;

/**
 * Created on 2022/3/22
 *
 * @author zsp
 * @desc 垂直 TextView
 */
public class VerticalTextView extends androidx.appcompat.widget.AppCompatTextView {
    private static final String TAG = VerticalTextView.class.getSimpleName();
    /**
     * 上下文
     */
    private final Context mContext;
    /**
     * 屏高
     */
    private int mScreenHeight;
    /**
     * 从左到右否
     * <p>
     * 从左到右否
     * 默从右到左
     */
    private boolean areLeftToRight;
    /**
     * 行距
     * <p>
     * 默 6px
     */
    private float mLineSpacingExtra;
    /**
     * 字符间距
     * <p>
     * 默 6px
     */
    private float mCharSpacingExtra;
    /**
     * 需下划线否
     * <p>
     * 默 false
     */
    private boolean areUnderLineText;
    /**
     * 下划线色
     * <p>
     * 默 Color.RED
     */
    private int mUnderLineColor;
    /**
     * 下划线宽
     * <p>
     * 默 1.5F
     */
    private float mUnderLineWidth;
    /**
     * 下划线偏移
     * <p>
     * 默 3px
     */
    private float mUnderLineOffset;
    /**
     * 显 ActionMenu 否
     * <p>
     * 默 true
     */
    private boolean areShowActionMenu;
    /**
     * 选中文本背景高亮色
     * <p>
     * 默 0x60ffeb3b
     */
    private int mTextHighlightColor;
    /**
     * 粗算文本最大显示区(含 padding)
     * <p>
     * 用于 view 测量和不同 Gravity 场景文本绘制
     */
    private int[] mTextAreaRoughBound;
    /**
     * 记录每行文本 X、Y 偏移量
     */
    private SparseArray<Float[]> mLinesOffsetArray;
    /**
     * 记录每行文本开始和结束字符 index
     */
    private SparseArray<int[]> mLinesTextIndex;
    /**
     * 最大行数
     */
    private int mMaxTextLine = 0;
    /**
     * 状态栏高
     */
    private int mStatusBarHeight;
    /**
     * ActionMenu 高
     */
    private int mActionMenuHeight;
    /**
     * 选择文本
     */
    private String mSelectedText;
    private float mTouchDownX = 0;
    private float mTouchDownY = 0;
    private float mTouchDownRawY = 0;
    /**
     * 触发长按事件否
     */
    private boolean areLongPress = false;
    /**
     * 长按事件结束后标记该次事件(避手指抬起后 view 没重绘)
     */
    private boolean areLongPressTouchActionUp = false;
    /**
     * 触发长按震动否
     */
    private boolean areVibrator = false;
    /**
     * 触发全选事件否
     */
    private boolean areActionSelectAll = false;
    /**
     * 长按触摸事件
     * <p>
     * 所选文本起始行
     */
    private int mStartLine;
    /**
     * 长按触摸事件
     * <p>
     * 移动过程手指所在行
     */
    private int mCurrentLine;
    /**
     * 长按触摸事件
     * <p>
     * 所选文本开始位 Y 偏移值
     */
    private float mStartTextOffset;
    /**
     * 长按触摸事件
     * <p>
     * 移动过程所选文本结束位 Y 偏移值
     */
    private float mCurrentTextOffset;
    /**
     * 长按弹出菜单
     */
    private PopupWindow mActionMenuPopupWindow;
    /**
     * ActionMenu
     */
    private ActionMenu mActionMenu = null;
    private OnClickListener mOnClickListener;
    private ActionMenuCallBack mActionMenuCallBack;
    private final OnClickListener mMenuClickListener = new OnClickListener() {
        @Override
        public void onClick(@NonNull View v) {
            String menuItemTitle = (String) v.getTag();
            if (menuItemTitle.equals(ActionMenu.DEFAULT_ACTION_MENU_ITEM_TITLE_SELECT_ALL)) {
                mSelectedText = getText().toString();
                int[] drawPadding = getDrawPadding(areLeftToRight);
                mStartLine = 1;
                mCurrentLine = mMaxTextLine;
                mStartTextOffset = drawPadding[1];
                mCurrentTextOffset = getHeight() - drawPadding[3];
                areLongPressTouchActionUp = true;
                invalidate();
            } else if (menuItemTitle.equals(ActionMenu.DEFAULT_ACTION_MENU_ITEM_TITLE_COPY)) {
                KeyboardUtils.copyText(mContext, mSelectedText);
                ToastKit.showShort(mContext.getString(R.string.copySuccessful));
                hideActionMenu();
            } else {
                if (null != mActionMenuCallBack) {
                    mActionMenuCallBack.onActionMenuItemClick(menuItemTitle, mSelectedText);
                }
                hideActionMenu();
            }
        }
    };

    public VerticalTextView(Context context) {
        this(context, null);
    }

    public VerticalTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.VerticalTextView);
        mLineSpacingExtra = typedArray.getDimension(R.styleable.VerticalTextView_verticalTextViewLineSpacingExtra, 6);
        mCharSpacingExtra = typedArray.getDimension(R.styleable.VerticalTextView_verticalTextViewCharSpacingExtra, 6);
        areLeftToRight = typedArray.getBoolean(R.styleable.VerticalTextView_verticalTextViewTextLeftToRight, false);
        areUnderLineText = typedArray.getBoolean(R.styleable.VerticalTextView_verticalTextViewUnderLineText, false);
        mUnderLineColor = typedArray.getColor(R.styleable.VerticalTextView_verticalTextViewUnderLineColor, Color.RED);
        mUnderLineWidth = typedArray.getFloat(R.styleable.VerticalTextView_verticalTextViewUnderLineWidth, 1.5F);
        mUnderLineOffset = typedArray.getDimension(R.styleable.VerticalTextView_verticalTextViewUnderlineOffset, 3);
        mTextHighlightColor = typedArray.getColor(R.styleable.VerticalTextView_verticalTextViewTextHeightLightColor, 0x60ffeb3b);
        areShowActionMenu = typedArray.getBoolean(R.styleable.VerticalTextView_verticalTextViewShowActionMenu, false);
        typedArray.recycle();
        mLineSpacingExtra = Math.max(6, mLineSpacingExtra);
        mCharSpacingExtra = Math.max(6, mCharSpacingExtra);
        if (areUnderLineText) {
            mUnderLineWidth = Math.abs(mUnderLineWidth);
            mUnderLineOffset = Math.min(Math.abs(mUnderLineOffset), Math.abs(mLineSpacingExtra) / 2);
        }
        init();
    }

    private void init() {
        mScreenHeight = ScreenUtils.screenHeight(mContext);
        setTextIsSelectable(false);
        mLinesOffsetArray = new SparseArray<>();
        mLinesTextIndex = new SparseArray<>();
        mTextAreaRoughBound = new int[]{0, 0};
        mStatusBarHeight = StatusBarUtils.getStatusBarHeight(mContext);
        mActionMenuHeight = DensityUtils.dipToPxByFloat(mContext, 45.0F);
    }

    public VerticalTextView setAreLeftToRight(boolean areLeftToRight) {
        this.areLeftToRight = areLeftToRight;
        return this;
    }

    public VerticalTextView setLineSpacingExtra(float lineSpacingExtra) {
        this.mLineSpacingExtra = DensityUtils.dipToPxByFloat(mContext, lineSpacingExtra);
        return this;
    }

    public VerticalTextView setCharSpacingExtra(float charSpacingExtra) {
        this.mCharSpacingExtra = DensityUtils.dipToPxByFloat(mContext, charSpacingExtra);
        return this;
    }

    public VerticalTextView setAreUnderLineText(boolean areUnderLineText) {
        this.areUnderLineText = areUnderLineText;
        return this;
    }

    public VerticalTextView setUnderLineColor(int underLineColor) {
        this.mUnderLineColor = underLineColor;
        return this;
    }

    public VerticalTextView setUnderLineWidth(float underLineWidth) {
        this.mUnderLineWidth = underLineWidth;
        return this;
    }

    public VerticalTextView setUnderLineOffset(float underLineOffset) {
        this.mUnderLineOffset = DensityUtils.dipToPxByFloat(mContext, underLineOffset);
        return this;
    }

    public VerticalTextView setAreShowActionMenu(boolean areShowActionMenu) {
        this.areShowActionMenu = areShowActionMenu;
        return this;
    }

    public VerticalTextView setTextHighlightColor(int color) {
        this.mTextHighlightColor = color;
        String colorHex = String.format("%08X", color);
        colorHex = "#40" + colorHex.substring(2);
        setHighlightColor(Color.parseColor(colorHex));
        return this;
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(l);
        if (null != l) {
            mOnClickListener = l;
        }
    }

    public void setCustomActionMenuCallBack(ActionMenuCallBack actionMenuCallBack) {
        this.mActionMenuCallBack = actionMenuCallBack;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        Timber.d(TAG, "widthSize %s", widthSize);
        Timber.d(TAG, "heightSize %s", heightSize);
        mTextAreaRoughBound = getTextRoughSize(heightSize == 0 ? mScreenHeight : heightSize, mLineSpacingExtra, mCharSpacingExtra);
        int measuredWidth;
        int measureHeight;
        if (widthSize == 0) {
            measuredWidth = mTextAreaRoughBound[0];
        } else {
            measuredWidth = ((widthMode == MeasureSpec.AT_MOST) || (widthMode == MeasureSpec.UNSPECIFIED)) ? mTextAreaRoughBound[0] : widthSize;
        }
        if (heightSize == 0) {
            measureHeight = mScreenHeight;
        } else {
            measureHeight = ((heightMode == MeasureSpec.AT_MOST) || (heightMode == MeasureSpec.UNSPECIFIED)) ? mTextAreaRoughBound[1] : heightSize;
        }
        setMeasuredDimension(measuredWidth, measureHeight);
        Timber.d(TAG, "measuredWidth %s", measuredWidth);
        Timber.d(TAG, "measureHeight %s", measureHeight);
    }

    @NonNull
    @Contract("_, _, _ -> new")
    private int[] getTextRoughSize(int oriHeightSize, float lineSpacingExtra, float charSpacingExtra) {
        String[] subTextStr = getText().toString().split("\n");
        int textLines = 0;
        String targetSubParam = "";
        int tempLines = 1;
        float tempLength = 0;
        for (String aSubTextStr : subTextStr) {
            float subParagraphLength = aSubTextStr.length() * (getTextSize() + charSpacingExtra);
            int subLines = (int) Math.ceil(subParagraphLength / Math.abs(oriHeightSize - getPaddingTop() - getPaddingBottom()));
            if (subLines == 0) {
                subLines = 1;
            }
            textLines += subLines;
            if ((subLines == 1) && (tempLines == 1)) {
                if (subParagraphLength > tempLength) {
                    tempLength = subParagraphLength;
                    targetSubParam = aSubTextStr;
                }
            }
            tempLines = subLines;
        }
        int textHeight = getPaddingTop() + getPaddingBottom();
        if (textLines > subTextStr.length) {
            textHeight = oriHeightSize;
        } else {
            for (int i = 0; i < targetSubParam.length(); i++) {
                String iOfChar = String.valueOf(getText().toString().charAt(i));
                if (areUnicodeSymbol(iOfChar)) {
                    // 使用 Math.round() 四舍五入
                    textHeight += Math.round(1.4F * getCharHeight(iOfChar, getTextPaint()) + charSpacingExtra);
                } else {
                    // 使用 Math.round() 四舍五入
                    textHeight += Math.round(getTextSize() + charSpacingExtra);
                }
            }
        }
        int textWidth = getPaddingLeft() + getPaddingRight() + (int) ((textLines + 1) * getTextSize() + lineSpacingExtra * (textLines - 1));
        Timber.d(TAG, "textRoughLines %s", textLines);
        Timber.d(TAG, "textRoughWidth %s", textWidth);
        Timber.d(TAG, "textRoughHeight %s", textHeight);
        return new int[]{textWidth, textHeight};
    }

    @SuppressLint({"ClickableViewAccessibility", "MissingPermission"})
    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        int action = event.getAction();
        int currentLine;
        int triggerLongPressTimeThreshold = 300;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (null == mActionMenu) {
                    mActionMenu = createActionMenu();
                }
                mTouchDownX = event.getX();
                mTouchDownY = event.getY();
                mTouchDownRawY = event.getRawY();
                areLongPress = false;
                areVibrator = false;
                areLongPressTouchActionUp = false;
                break;
            case ACTION_MOVE:
                if (areShowActionMenu || (mActionMenu.getChildCount() == 0)) {
                    currentLine = getCurrentTouchLine(event.getX(), areLeftToRight);
                    float mWordOffsetMove = event.getY();
                    boolean areTriggerTime = ((event.getEventTime() - event.getDownTime()) >= triggerLongPressTimeThreshold);
                    int triggerLongPressDistanceThreshold = 10;
                    boolean areTriggerDistance = (Math.abs(event.getX() - mTouchDownX) < triggerLongPressDistanceThreshold) && (Math.abs(event.getY() - mTouchDownY) < triggerLongPressDistanceThreshold);
                    int[] drawPadding = getDrawPadding(areLeftToRight);
                    boolean areInBound = (event.getX() >= drawPadding[0]) && (event.getX() <= getWidth() - drawPadding[2]) && (event.getY() >= drawPadding[1]) && (event.getY() <= getHeight() - drawPadding[3]);
                    if (areTriggerTime && areTriggerDistance && areInBound) {
                        areLongPress = true;
                        areLongPressTouchActionUp = false;
                        mStartLine = currentLine;
                        mStartTextOffset = mWordOffsetMove;
                        if (!areVibrator) {
                            VibratorUtils.oneShotVibration(mContext, 60, 100);
                            areVibrator = true;
                        }
                    }
                    if (areLongPress) {
                        mCurrentLine = currentLine;
                        mCurrentTextOffset = mWordOffsetMove;
                        getParent().requestDisallowInterceptTouchEvent(true);
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (areLongPress) {
                    currentLine = getCurrentTouchLine(event.getX(), areLeftToRight);
                    float mWordOffsetEnd = event.getY();
                    mCurrentLine = currentLine;
                    mCurrentTextOffset = mWordOffsetEnd;
                    selectText(mStartTextOffset, mCurrentTextOffset, mStartLine, mCurrentLine, mCharSpacingExtra, areLeftToRight);
                    if (!TextUtils.isEmpty(mSelectedText)) {
                        int mPopWindowOffsetY = calculatorActionMenuPositionOfY((int) mTouchDownRawY, (int) event.getRawY());
                        showActionMenu(mPopWindowOffsetY, mActionMenu);
                    }
                    areLongPressTouchActionUp = true;
                    areLongPress = false;
                } else if ((event.getEventTime() - event.getDownTime()) < triggerLongPressTimeThreshold) {
                    if (null != mOnClickListener) {
                        mOnClickListener.onClick(this);
                    }
                }
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
            default:
                break;
        }
        return true;
    }

    private int getCurrentTouchLine(float offsetX, boolean areLeftToRight) {
        int currentLine;
        float lineWidth = getTextSize() + mLineSpacingExtra;
        int[] drawPadding = getDrawPadding(areLeftToRight);
        if (areLeftToRight) {
            if (offsetX >= (getWidth() - drawPadding[WidgetMagic.INT_TWO])) {
                currentLine = mMaxTextLine;
            } else {
                currentLine = (int) Math.ceil((offsetX - drawPadding[0]) / lineWidth);
            }
        } else {
            if (offsetX <= drawPadding[0]) {
                currentLine = mMaxTextLine;
            } else {
                currentLine = (int) Math.ceil((getWidth() - offsetX - drawPadding[2]) / lineWidth);
            }
        }
        currentLine = (currentLine <= 0) ? 1 : (Math.min(currentLine, mMaxTextLine));
        Timber.d(TAG, "touch line is %s", currentLine);
        return currentLine;
    }

    private void selectText(float startOffsetY, float endOffsetY, int startLine, int endLine, float charSpacingExtra, boolean areLeftToRight) {
        int startIndex = getSelectTextIndex(startOffsetY, startLine, charSpacingExtra, areLeftToRight);
        int endIndex = getSelectTextIndex(endOffsetY, endLine, charSpacingExtra, areLeftToRight);
        if (startIndex == endIndex) {
            mSelectedText = "";
        } else {
            String textAll = getText().toString();
            if (TextUtils.isEmpty(textAll)) {
                mSelectedText = "";
            } else {
                mSelectedText = textAll.substring(Math.min(startIndex, endIndex), Math.max(startIndex, endIndex));
            }
        }
        Timber.d(TAG, "mSelectedText %s", mSelectedText);
    }

    private int getSelectTextIndex(float offsetY, int targetLine, float charSpacingExtra, boolean areLeftToRight) {
        int[] drawPadding = getDrawPadding(areLeftToRight);
        int[] lineIndex = mLinesTextIndex.get(targetLine);
        int targetIndex = lineIndex[1];
        float tempY = drawPadding[1];
        if (offsetY < drawPadding[1]) {
            return lineIndex[0];
        } else if (offsetY > getHeight() - drawPadding[WidgetMagic.INT_THREE]) {
            return lineIndex[1];
        }
        for (int i = lineIndex[0]; i < lineIndex[1]; i++) {
            String iOfChar = String.valueOf(getText().toString().charAt(i));
            if (TextUtils.equals("\n", iOfChar)) {
                tempY = drawPadding[1];
            } else if (areUnicodeSymbol(iOfChar)) {
                tempY += 1.4F * getCharHeight(iOfChar, getTextPaint()) + charSpacingExtra;
            } else {
                tempY += getTextSize() + charSpacingExtra;
            }
            if (tempY >= offsetY) {
                targetIndex = i;
                break;
            }
        }
        Timber.d(TAG, "target index %s", targetIndex);
        return targetIndex;
    }

    private int calculatorActionMenuPositionOfY(int yOffsetStart, int yOffsetEnd) {
        if (yOffsetStart > yOffsetEnd) {
            int temp = yOffsetStart;
            yOffsetStart = yOffsetEnd;
            yOffsetEnd = temp;
        }
        int actionMenuOffsetY;
        if (yOffsetStart < (mActionMenuHeight * WidgetMagic.INT_THREE / WidgetMagic.INT_TWO + mStatusBarHeight)) {
            if (yOffsetEnd > (mScreenHeight - mActionMenuHeight * WidgetMagic.INT_THREE / WidgetMagic.INT_TWO)) {
                actionMenuOffsetY = (mScreenHeight / 2 - mActionMenuHeight / 2);
            } else {
                actionMenuOffsetY = (yOffsetEnd + mActionMenuHeight / 2);
            }
        } else {
            actionMenuOffsetY = (yOffsetStart - mActionMenuHeight * 3 / 2);
        }
        return actionMenuOffsetY;
    }

    @NonNull
    private ActionMenu createActionMenu() {
        ActionMenu actionMenu = new ActionMenu(mContext);
        boolean areRemoveDefaultItem = false;
        if (null != mActionMenuCallBack) {
            areRemoveDefaultItem = mActionMenuCallBack.onCreateActionMenu(actionMenu);
        }
        if (!areRemoveDefaultItem) {
            actionMenu.addDefaultMenuItem();
        }
        actionMenu.addCustomMenuItem();
        actionMenu.setFocusable(true);
        actionMenu.setFocusableInTouchMode(true);
        if (actionMenu.getChildCount() != 0) {
            for (int i = 0; i < actionMenu.getChildCount(); i++) {
                actionMenu.getChildAt(i).setOnClickListener(mMenuClickListener);
            }
        }
        return actionMenu;
    }

    private void showActionMenu(int offsetY, ActionMenu actionMenu) {
        mActionMenuPopupWindow = new PopupWindow(actionMenu, WindowManager.LayoutParams.WRAP_CONTENT, mActionMenuHeight, true);
        mActionMenuPopupWindow.setFocusable(true);
        mActionMenuPopupWindow.setOutsideTouchable(false);
        mActionMenuPopupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        mActionMenuPopupWindow.showAtLocation(this, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, offsetY);
        mActionMenuPopupWindow.setOnDismissListener(this::clearSelectedTextBackground);
    }

    private void hideActionMenu() {
        if (null != mActionMenuPopupWindow) {
            mActionMenuPopupWindow.dismiss();
            mActionMenuPopupWindow = null;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawVerticalText(canvas, mLineSpacingExtra, mCharSpacingExtra, areLeftToRight);
        drawTextUnderline(canvas, areLeftToRight, mUnderLineOffset, mCharSpacingExtra);
        if (areLongPress | areActionSelectAll | areLongPressTouchActionUp) {
            drawSelectedTextBackground(canvas, mStartLine, mCurrentLine, mStartTextOffset, mCurrentTextOffset, mLineSpacingExtra, mCharSpacingExtra, areLeftToRight);
            areActionSelectAll = false;
            areLongPressTouchActionUp = false;
        }
    }

    private void drawVerticalText(Canvas canvas, float lineSpacingExtra, float charSpacingExtra, boolean areLeftToRight) {
        TextPaint textPaint = getTextPaint();
        int textStrLength = getText().length();
        if (textStrLength == 0) {
            return;
        }
        mMaxTextLine = 1;
        int currentLineStartIndex = 0;
        mLinesOffsetArray.clear();
        mLinesTextIndex.clear();
        int[] drawPadding = getDrawPadding(areLeftToRight);
        float currentLineOffsetX = areLeftToRight ? drawPadding[0] : getWidth() - drawPadding[2] - getTextSize();
        float currentLineOffsetY = drawPadding[1] + getTextSize();
        for (int j = 0; j < textStrLength; j++) {
            String jOfChar = String.valueOf(getText().charAt(j));
            boolean isLineBreaks = "\n".equals(jOfChar);
            boolean isCurrentLineFinish = (currentLineOffsetY > getHeight() - drawPadding[3]) && (!areUnicodeSymbol(jOfChar) || (areUnicodeSymbol(jOfChar) && currentLineOffsetY + getCharHeight(jOfChar, textPaint) > getHeight() - drawPadding[3] + getTextSize()));
            if (isLineBreaks || isCurrentLineFinish) {
                mLinesOffsetArray.put(mMaxTextLine, new Float[]{currentLineOffsetX, currentLineOffsetY});
                mLinesTextIndex.put(mMaxTextLine, new int[]{currentLineStartIndex, j});
                currentLineOffsetX = areLeftToRight ? (currentLineOffsetX + getTextSize() + lineSpacingExtra) : (currentLineOffsetX - getTextSize() - lineSpacingExtra);
                currentLineOffsetY = drawPadding[1] + getTextSize();
                mMaxTextLine++;
            }
            if (currentLineOffsetY == drawPadding[1] + getTextSize()) {
                currentLineStartIndex = j;
            }
            if (areUnicodeSymbol(jOfChar)) {
                float drawOffsetY = currentLineOffsetY;
                if (areSymbolNeedOffset(jOfChar)) {
                    drawOffsetY = drawOffsetY - (getTextSize() - 1.4F * getCharHeight(jOfChar, textPaint));
                }
                float drawOffsetX = currentLineOffsetX;
                if (areLeftToRight && !areVerticalSymbol(jOfChar)) {
                    drawOffsetX = drawOffsetX + getTextSize() / 2;
                }
                canvas.drawText(jOfChar, drawOffsetX, drawOffsetY, textPaint);
                currentLineOffsetY += 1.4F * getCharHeight(jOfChar, textPaint) + charSpacingExtra;
            } else {
                canvas.drawText(jOfChar, currentLineOffsetX, currentLineOffsetY, textPaint);
                currentLineOffsetY += getTextSize() + charSpacingExtra;
            }
            if (j == textStrLength - 1) {
                mLinesOffsetArray.put(mMaxTextLine, new Float[]{currentLineOffsetX, currentLineOffsetY});
                mLinesTextIndex.put(mMaxTextLine, new int[]{currentLineStartIndex, textStrLength});
            }
        }
        Timber.d(TAG, "mMaxTextLine is %s", mMaxTextLine);
    }

    private void drawTextUnderline(Canvas canvas, boolean areLeftToRight, float underLineOffset, float charSpacingExtra) {
        if (!areUnderLineText || (mUnderLineWidth == 0)) {
            return;
        }
        Paint underLinePaint = getPaint();
        underLinePaint.setColor(mUnderLineColor);
        underLinePaint.setAntiAlias(true);
        underLinePaint.setStyle(Paint.Style.FILL);
        underLinePaint.setStrokeWidth(mUnderLineWidth);
        int[] drawPadding = getDrawPadding(areLeftToRight);
        for (int i = 0; i < mMaxTextLine; i++) {
            float yStart = drawPadding[1];
            float yEnd = mLinesOffsetArray.get(i + 1)[1] - getTextSize();
            int[] lineIndex = mLinesTextIndex.get(i + 1);
            String lineText = getText().toString().substring(lineIndex[0], lineIndex[1]);
            if (yEnd <= yStart || ("\n".equals(lineText))) {
                continue;
            }
            if (yEnd > getHeight() - drawPadding[3] - getTextSize()) {
                yEnd = getHeight() - drawPadding[3];
            }
            int spaceNum = getLineStartSpaceNumber(lineText);
            if (spaceNum > 0) {
                yStart = yStart + (getTextSize() + charSpacingExtra) * spaceNum;
            }
            float xStart = mLinesOffsetArray.get(i + 1)[0];
            if (areLeftToRight) {
                xStart += getTextSize() + underLineOffset;
            } else {
                xStart -= underLineOffset;
            }
            float xEnd = xStart;
            canvas.drawLine(xStart, yStart, xEnd, yEnd, underLinePaint);
        }
    }

    private void drawSelectedTextBackground(Canvas canvas, int startLine, int endLine, float startOffsetY, float endOffsetY, float lineSpacingExtra, float charSpacingExtra, boolean areLeftToRight) {
        if ((startLine == endLine) && (Math.abs(endOffsetY - startOffsetY) == 0)) {
            return;
        }
        Paint highlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        highlightPaint.setStyle(Paint.Style.FILL);
        highlightPaint.setColor(mTextHighlightColor);
        highlightPaint.setAlpha(60);
        int[] drawPadding = getDrawPadding(areLeftToRight);
        if (startLine > endLine) {
            startLine = startLine + endLine;
            endLine = startLine - endLine;
            startLine = startLine - endLine;
            startOffsetY = startOffsetY + endOffsetY;
            endOffsetY = startOffsetY - endOffsetY;
            startOffsetY = startOffsetY - endOffsetY;
        }
        int lineWidth = (int) (getTextSize() + lineSpacingExtra);
        int startLineOffsetY = getSelectTextPreciseOffsetY(startOffsetY, startLine, charSpacingExtra, areLeftToRight);
        int endLineOffsetY = getSelectTextPreciseOffsetY(endOffsetY, endLine, charSpacingExtra, areLeftToRight);
        Path pathAll = new Path();
        if (areLeftToRight) {
            int offsetLeftPadding = (int) (drawPadding[0] - lineSpacingExtra / 2);
            pathAll.moveTo(offsetLeftPadding + (startLine - 1) * lineWidth, startLineOffsetY);
            pathAll.lineTo(offsetLeftPadding + startLine * lineWidth, startLineOffsetY);
            pathAll.lineTo(offsetLeftPadding + startLine * lineWidth, drawPadding[1]);
            pathAll.lineTo(offsetLeftPadding + endLine * lineWidth, drawPadding[1]);
            pathAll.lineTo(offsetLeftPadding + endLine * lineWidth, endLineOffsetY);
            pathAll.lineTo(offsetLeftPadding + (endLine - 1) * lineWidth, endLineOffsetY);
            pathAll.lineTo(offsetLeftPadding + (endLine - 1) * lineWidth, getHeight() - drawPadding[3] + charSpacingExtra);
            pathAll.lineTo(offsetLeftPadding + (startLine - 1) * lineWidth, getHeight() - drawPadding[3] + charSpacingExtra);
            pathAll.close();
        } else {
            int offsetRightPadding = (int) (getWidth() - drawPadding[2] + lineSpacingExtra / 2);
            pathAll.moveTo(offsetRightPadding - (startLine - 1) * lineWidth, startLineOffsetY);
            pathAll.lineTo(offsetRightPadding - startLine * lineWidth, startLineOffsetY);
            pathAll.lineTo(offsetRightPadding - startLine * lineWidth, drawPadding[1]);
            pathAll.lineTo(offsetRightPadding - endLine * lineWidth, drawPadding[1]);
            pathAll.lineTo(offsetRightPadding - endLine * lineWidth, endLineOffsetY);
            pathAll.lineTo(offsetRightPadding - (endLine - 1) * lineWidth, endLineOffsetY);
            pathAll.lineTo(offsetRightPadding - (endLine - 1) * lineWidth, getHeight() - drawPadding[3] + charSpacingExtra);
            pathAll.lineTo(offsetRightPadding - (startLine - 1) * lineWidth, getHeight() - drawPadding[3] + charSpacingExtra);
            pathAll.close();
        }
        canvas.drawPath(pathAll, highlightPaint);
        canvas.save();
        canvas.restore();
    }

    @SuppressLint("RtlHardcoded")
    @NonNull
    @Contract("_ -> new")
    private int[] getDrawPadding(boolean areLeftToRight) {
        int textBoundWidth = mTextAreaRoughBound[0];
        int textBoundHeight = mTextAreaRoughBound[1];
        int left, right, top, bottom;
        int gravity;
        if (textBoundWidth < getWidth()) {
            gravity = getGravity() & Gravity.HORIZONTAL_GRAVITY_MASK;
            if (gravity == Gravity.CENTER_HORIZONTAL) {
                left = (getPaddingLeft() + (getWidth() - textBoundWidth) / 2);
                right = (getPaddingRight() + (getWidth() - textBoundWidth) / 2);
            } else if (gravity == Gravity.RIGHT && areLeftToRight) {
                left = getPaddingLeft() + getWidth() - textBoundWidth;
                right = getPaddingRight();
            } else if (gravity == Gravity.LEFT && !areLeftToRight) {
                left = getPaddingLeft();
                right = getPaddingRight() + getWidth() - textBoundWidth;
            } else {
                left = areLeftToRight ? getPaddingLeft() : (getPaddingLeft() + getWidth() - textBoundWidth);
                right = areLeftToRight ? (getPaddingRight() + getWidth() - textBoundWidth) : getPaddingRight();
            }
        } else {
            left = getPaddingLeft();
            right = getPaddingRight();
        }
        if (textBoundHeight < getHeight()) {
            gravity = getGravity() & Gravity.VERTICAL_GRAVITY_MASK;
            if (gravity == Gravity.CENTER_VERTICAL) {
                top = (getPaddingTop() + (getHeight() - textBoundHeight) / 2);
                bottom = (getPaddingBottom() + (getHeight() - textBoundHeight) / 2);
            } else if (gravity == Gravity.BOTTOM) {
                top = getPaddingTop() + getHeight() - textBoundHeight;
                bottom = getPaddingBottom();
            } else {
                top = getPaddingTop();
                bottom = getPaddingBottom() + getHeight() - textBoundHeight;
            }
        } else {
            top = getPaddingTop();
            bottom = getPaddingBottom();
        }
        return new int[]{left, top, right, bottom};
    }

    private int getSelectTextPreciseOffsetY(float offsetY, int targetLine, float charSpacingExtra, boolean areLeftToRight) {
        int[] drawPadding = getDrawPadding(areLeftToRight);
        int[] lineIndex = mLinesTextIndex.get(targetLine);
        int targetOffset = drawPadding[1];
        int tempY = drawPadding[1];
        if (offsetY < drawPadding[1]) {
            return drawPadding[1];
        } else if (offsetY > (getHeight() - drawPadding[WidgetMagic.INT_THREE])) {
            return getHeight() - drawPadding[3];
        }
        for (int i = lineIndex[0]; i < lineIndex[1]; i++) {
            String iOfChar = String.valueOf(getText().toString().charAt(i));
            if ("\n".equals(iOfChar)) {
                tempY = drawPadding[1];
            } else if (areUnicodeSymbol(iOfChar)) {
                // 使用 Math.round() 四舍五入
                tempY += Math.round(1.4F * getCharHeight(iOfChar, getTextPaint()) + charSpacingExtra);
            } else {
                // 使用 Math.round() 四舍五入
                tempY += Math.round(getTextSize() + charSpacingExtra);
            }
            if (tempY <= offsetY) {
                targetOffset = tempY;
            }
            if (tempY > offsetY) {
                break;
            }
        }
        return Math.max(targetOffset, drawPadding[1]);
    }

    private void clearSelectedTextBackground() {
        mSelectedText = "";
        mStartLine = mCurrentLine = 0;
        mStartTextOffset = mCurrentTextOffset = 0;
        invalidate();
    }

    @NonNull
    private TextPaint getTextPaint() {
        TextPaint textPaint = getPaint();
        textPaint.setColor(getCurrentTextColor());
        return textPaint;
    }

    private int getLineStartSpaceNumber(@NonNull String lineText) {
        if (lineText.startsWith(WidgetMagic.STRING_FOUR_SPACE)) {
            return 4;
        } else if (lineText.startsWith(WidgetMagic.STRING_THREE_OTHER_SPACE) || lineText.startsWith(WidgetMagic.STRING_THREE_SPACE)) {
            return 3;
        } else if (lineText.startsWith(WidgetMagic.STRING_TWO_OTHER_SPACE) || lineText.startsWith(WidgetMagic.STRING_TWO_SPACE)) {
            return 2;
        } else if (lineText.startsWith(WidgetMagic.STRING_ONE_OTHER_SPACE) || lineText.startsWith(WidgetMagic.STRING_ONE_SPACE)) {
            return 1;
        } else {
            return 0;
        }
    }

    private float getCharHeight(String targetChar, @NonNull Paint paint) {
        Rect rect = new Rect();
        paint.getTextBounds(targetChar, 0, 1, rect);
        return rect.height();
    }

    private float getCharWidth(String targetChar, @NonNull Paint paint) {
        Rect rect = new Rect();
        paint.getTextBounds(targetChar, 0, 1, rect);
        return rect.width();
    }

    /**
     * 标点符号否
     * <p>
     * 不判断 - - —— = + ~
     *
     * @param str 所匹字符串
     * @return 标点符号否
     */
    private boolean areUnicodeSymbol(String str) {
        String regex = ".*[_\"`!@#$%^&*()|{}':;,\\[\\].<>/?！￥…（）【】‘’；：”“。，、？︵ ︷︿︹︽﹁﹃︻︶︸﹀︺︾ˉ﹂﹄︼]$+.*";
        Matcher matcher = Pattern.compile(regex).matcher(str);
        return matcher.matches();
    }

    /**
     * 需补偿的标点符号
     * <p>
     * 不补偿 - - —— = + ~
     *
     * @param str 所匹字符串
     * @return 需补偿的标点符号
     */
    private boolean areSymbolNeedOffset(String str) {
        String regex = ".*[_!@#$%&()|{}:;,\\[\\].<>/?！￥…（）【】；：。，、？︵ ︷︿︹︽﹁﹃︻]$+.*";
        Matcher matcher = Pattern.compile(regex).matcher(str);
        return matcher.matches();
    }

    /**
     * 竖排标点符号否
     *
     * @param str 所匹字符串
     * @return 竖排标点符号否
     */
    private boolean areVerticalSymbol(String str) {
        String regex = ".*[︵ ︷︿︹︽﹁﹃︻︶︸﹀︺︾ˉ﹂﹄︼|]$+.*";
        Matcher matcher = Pattern.compile(regex).matcher(str);
        return matcher.matches();
    }
}