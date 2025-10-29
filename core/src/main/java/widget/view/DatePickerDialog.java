package widget.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.divider.MaterialDivider;
import com.zsp.core.R;

import java.util.Calendar;
import java.util.Locale;

import util.density.DensityUtils;
import util.layoutparams.LayoutParamsUtils;
import util.system.SystemUtils;
import util.view.ViewUtils;
import widget.textview.kit.TextViewKit;
import widget.window.WindowKit;

/**
 * @decs: 日期选择对话框
 * @author: 郑少鹏
 * @date: 2025/10/8 21:04
 * @version: v 1.0
 */
public class DatePickerDialog extends Dialog {
    /**
     * 日历类型
     */
    private CalendarType calendarType = CalendarType.SOLAR;
    /**
     * 视图类型
     */
    private ViewType viewType = ViewType.YEAR;
    /**
     * 选择年
     */
    private int selectYear;
    /**
     * 选择月
     */
    private int selectMonth;
    /**
     * 选择日
     */
    private int selectDay;
    /**
     * 标题 TextView
     */
    private TextView titleTv;
    /**
     * 当前日期 TextView
     */
    private TextView currentDateTv;
    /**
     * 日历类型按钮
     */
    private MaterialButton calendarTypeMb;
    /**
     * 返回按钮
     */
    private MaterialButton backMb;
    /**
     * 滑动视图
     */
    private ScrollView scrollView;
    /**
     * 网格布局
     */
    private GridLayout gridLayout;
    /**
     * Material Design 颜色
     */
    private int colorPrimary;
    private int colorOnPrimary;
    private int colorSurface;
    private int colorOnSurface;
    private int colorSurfaceVariant;
    private int colorOnSurfaceVariant;
    /**
     * 日期选择监听
     */
    private OnDateSelectListener onDateSelectListener;

    /**
     * 日历类型枚举
     */
    public enum CalendarType {
        /**
         * 阳历
         */
        SOLAR("阳历"),
        /**
         * 农历
         */
        LUNAR("农历");
        private final String name;

        CalendarType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    /**
     * 视图类型
     */
    public enum ViewType {
        /**
         * 年
         */
        YEAR,
        /**
         * 月
         */
        MONTH,
        /**
         * 日
         */
        DAY
    }

    /**
     * constructor
     *
     * @param context 上下文
     */
    public DatePickerDialog(@NonNull Context context) {
        super(context);
        // 日历
        Calendar calendar = Calendar.getInstance();
        // 选择年
        this.selectYear = calendar.get(Calendar.YEAR);
        // 选择月
        this.selectMonth = (calendar.get(Calendar.MONTH) + 1);
        // 选择日
        this.selectDay = calendar.get(Calendar.DAY_OF_MONTH);
        // 初始化 Material Design 颜色
        initMaterialColor(context);
    }

    /**
     * 初始化 Material Design 颜色
     *
     * @param context 上下文
     */
    private void initMaterialColor(Context context) {
        boolean areDark = SystemUtils.areDark(context);
        try {
            // 系统颜色
            colorPrimary = MaterialColors.getColor(context, androidx.appcompat.R.attr.colorPrimary, 0xFF6750A4);
            colorOnPrimary = MaterialColors.getColor(context, com.google.android.material.R.attr.colorOnPrimary, 0xFFFFFFFF);
            colorSurface = MaterialColors.getColor(context, com.google.android.material.R.attr.colorSurface, areDark ? 0xFF1C1B1F : 0xFFFFFBFE);
            colorOnSurface = MaterialColors.getColor(context, com.google.android.material.R.attr.colorOnSurface, areDark ? 0xFFE6E1E5 : 0xFF1C1B1F);
            colorSurfaceVariant = MaterialColors.getColor(context, com.google.android.material.R.attr.colorSurfaceVariant, areDark ? 0xFF49454F : 0xFFE7E0EC);
            colorOnSurfaceVariant = MaterialColors.getColor(context, com.google.android.material.R.attr.colorOnSurfaceVariant, areDark ? 0xFFCAC4D0 : 0xFF49454F);
        } catch (Exception e) {
            // 获取异常
            // 备用颜色
            if (areDark) {
                colorPrimary = 0xFFD0BCFF;
                colorOnPrimary = 0xFF381E72;
                colorSurface = 0xFF1C1B1F;
                colorOnSurface = 0xFFE6E1E5;
                colorSurfaceVariant = 0xFF49454F;
                colorOnSurfaceVariant = 0xFFCAC4D0;
            } else {
                colorPrimary = 0xFF6750A4;
                colorOnPrimary = 0xFFFFFFFF;
                colorSurface = 0xFFFFFBFE;
                colorOnSurface = 0xFF1C1B1F;
                colorSurfaceVariant = 0xFFE7E0EC;
                colorOnSurfaceVariant = 0xFF49454F;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 主布局
        LinearLayout mainLayout = new LinearLayout(getContext());
        mainLayout.setPadding(DensityUtils.dipToPxByInt(24), DensityUtils.dipToPxByInt(24), DensityUtils.dipToPxByInt(24), DensityUtils.dipToPxByInt(24));
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        // 主布局 GradientDrawable
        GradientDrawable mainLayoutGradientDrawable = new GradientDrawable();
        mainLayoutGradientDrawable.setColor(colorSurface);
        mainLayoutGradientDrawable.setCornerRadius(DensityUtils.dipToPxByInt(28));
        // 主布局设置背景
        mainLayout.setBackground(mainLayoutGradientDrawable);
        // 创造视图
        createView(mainLayout);
        // 设置内容视图
        setContentView(mainLayout);
        // 窗口
        Window window = getWindow();
        if (null != window) {
            window.setGravity(Gravity.CENTER);
            WindowKit.setBackgroundDrawableResourceTransparent(window);
            LayoutParamsUtils.setWindowManagerLayoutParams(window, true, DensityUtils.dipToPxByInt(320), true, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        // 更新 UI
        updateUI();
    }

    /**
     * 创造视图
     *
     * @param parentLayout 父布局
     */
    private void createView(@NonNull LinearLayout parentLayout) {
        // 头布局
        LinearLayout headLayout = new LinearLayout(getContext());
        headLayout.setOrientation(LinearLayout.HORIZONTAL);
        headLayout.setGravity(Gravity.CENTER_VERTICAL);
        // 标题 TextView
        TextView titleTv = TextViewKit.createTextView(getContext(), getContext().getString(R.string.chooseDate), 18, colorOnSurface, true);
        LinearLayout.LayoutParams titleTvLayoutParams = new LinearLayout.LayoutParams(0, DensityUtils.dipToPxByInt(36), 1.0F);
        titleTv.setLayoutParams(titleTvLayoutParams);
        titleTv.setGravity(Gravity.CENTER_VERTICAL);
        // 头布局添加标题 TextView
        headLayout.addView(titleTv);
        // 日历类型按钮
        calendarTypeMb = new MaterialButton(getContext(), null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
        calendarTypeMb.setText((calendarType == CalendarType.SOLAR) ? CalendarType.LUNAR.getName() : CalendarType.SOLAR.getName());
        calendarTypeMb.setTextSize(12);
        calendarTypeMb.setInsetTop(0);
        calendarTypeMb.setInsetBottom(0);
        calendarTypeMb.setGravity(Gravity.CENTER);
        calendarTypeMb.setPadding(0, 0, 0, 0);
        LinearLayout.LayoutParams calendarTypeMbLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        calendarTypeMb.setLayoutParams(calendarTypeMbLayoutParams);
        calendarTypeMb.setOnClickListener(v -> toggleCalendarType());
        // 头布局添加日历类型按钮
        headLayout.addView(calendarTypeMb);
        // 父布局添加头布局
        parentLayout.addView(headLayout);
        // 当前日期 TextView
        currentDateTv = TextViewKit.createTextView(getContext(), formatDate(selectYear, selectMonth, selectDay), 22, colorPrimary, true);
        currentDateTv.setPadding(0, DensityUtils.dipToPxByInt(16), 0, DensityUtils.dipToPxByInt(16));
        currentDateTv.setGravity(Gravity.CENTER);
        // 父布局添加当前日期 TextView
        parentLayout.addView(currentDateTv);
        // 材料分隔线
        MaterialDivider materialDivider = new MaterialDivider(getContext());
        LinearLayout.LayoutParams materialDividerLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtils.dipToPxByInt(1));
        materialDivider.setLayoutParams(materialDividerLayoutParams);
        // 父布局添加材料分隔线
        parentLayout.addView(materialDivider);
        // 标题 TextView
        this.titleTv = TextViewKit.createTextView(getContext(), getContext().getString(R.string.chooseYear), 16, colorOnSurface, true);
        this.titleTv.setPadding(0, DensityUtils.dipToPxByInt(12), 0, DensityUtils.dipToPxByInt(12));
        this.titleTv.setGravity(Gravity.CENTER);
        // 父布局添加标题 TextView
        parentLayout.addView(this.titleTv);
        // 滚动视图
        scrollView = new ScrollView(getContext());
        LinearLayout.LayoutParams scrollViewLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        scrollView.setLayoutParams(scrollViewLayoutParams);
        scrollView.setVerticalScrollBarEnabled(false);
        // 网格布局
        gridLayout = new GridLayout(getContext());
        LinearLayout.LayoutParams gridLayoutLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        gridLayout.setLayoutParams(gridLayoutLayoutParams);
        gridLayout.setColumnCount(3);
        // 滚动视图添加网格布局
        scrollView.addView(gridLayout);
        // 父布局添加滚动视图
        parentLayout.addView(scrollView);
        // 脚布局
        RelativeLayout footLayout = new RelativeLayout(getContext());
        footLayout.setPadding(0, DensityUtils.dipToPxByInt(24), 0, 0);
        // 返回材料按钮
        backMb = getBackMaterialButton();
        RelativeLayout.LayoutParams backMbParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        backMbParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        // 脚布局添加返回材料按钮
        footLayout.addView(backMb, backMbParams);
        // 确定材料按钮
        MaterialButton confirmMb = getConfirmMaterialButton();
        confirmMb.setId(View.generateViewId());
        RelativeLayout.LayoutParams confirmMbParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        confirmMbParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        // 脚布局添加确定材料按钮
        footLayout.addView(confirmMb, confirmMbParams);
        // 取消材料按钮
        MaterialButton cancelMb = getCancelMaterialButton();
        RelativeLayout.LayoutParams cancelMbParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        cancelMbParams.addRule(RelativeLayout.LEFT_OF, confirmMb.getId());
        cancelMbParams.rightMargin = DensityUtils.dipToPxByInt(10);
        // 脚布局添加取消材料按钮
        footLayout.addView(cancelMb, cancelMbParams);
        // 父布局添加脚布局
        parentLayout.addView(footLayout);
    }

    /**
     * 获取返回材料按钮
     *
     * @return 返回材料按钮
     */
    @NonNull
    private MaterialButton getBackMaterialButton() {
        MaterialButton backMb = new MaterialButton(getContext(), null, com.google.android.material.R.attr.materialIconButtonFilledTonalStyle);
        backMb.setIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_chevron_backward_cos_24dp));
        backMb.setOnClickListener(v -> back());
        ViewUtils.hideView(backMb, View.GONE);
        return backMb;
    }

    /**
     * 获取确定材料按钮
     *
     * @return 确定材料按钮
     */
    @NonNull
    private MaterialButton getConfirmMaterialButton() {
        MaterialButton confirmMb = new MaterialButton(getContext(), null, com.google.android.material.R.attr.materialButtonTonalStyle);
        confirmMb.setText(getContext().getString(R.string.sure));
        confirmMb.setTextSize(12);
        confirmMb.setInsetTop(0);
        confirmMb.setInsetBottom(0);
        LinearLayout.LayoutParams confirmMbLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, DensityUtils.dipToPxByInt(32));
        confirmMbLayoutParams.setMargins(DensityUtils.dipToPxByInt(10), 0, 0, 0);
        confirmMb.setLayoutParams(confirmMbLayoutParams);
        confirmMb.setOnClickListener(v -> {
            if (null != onDateSelectListener) {
                onDateSelectListener.onDateSelect(selectYear, selectMonth, selectDay, calendarType);
            }
            dismiss();
        });
        return confirmMb;
    }

    /**
     * 获取取消材料按钮
     *
     * @return 取消材料按钮
     */
    @NonNull
    private MaterialButton getCancelMaterialButton() {
        MaterialButton cancelMb = new MaterialButton(getContext(), null, com.google.android.material.R.attr.materialButtonTonalStyle);
        cancelMb.setText(getContext().getString(R.string.cancel));
        cancelMb.setTextSize(12);
        cancelMb.setInsetTop(0);
        cancelMb.setInsetBottom(0);
        cancelMb.setOnClickListener(v -> dismiss());
        return cancelMb;
    }

    /**
     * 切换日历类型
     */
    private void toggleCalendarType() {
        calendarType = (calendarType == CalendarType.SOLAR) ? CalendarType.LUNAR : CalendarType.SOLAR;
        calendarTypeMb.setText((calendarType == CalendarType.SOLAR) ? CalendarType.LUNAR.getName() : CalendarType.SOLAR.getName());
        updateDateDisplay();
        if (viewType == ViewType.DAY) {
            updateScrollViewHeight();
            scrollView.post(this::showDayPicker);
        } else {
            updateUI();
        }
    }

    /**
     * 返回
     */
    private void back() {
        switch (viewType) {
            case MONTH:
                viewType = ViewType.YEAR;
                updateBackMbVisibility();
                titleTv.setText(getContext().getString(R.string.chooseYear));
                // 先更新高度并强制重新布局
                updateScrollViewHeight();
                scrollView.requestLayout();
                // 等待布局完成后再显示内容
                scrollView.post(() -> scrollView.post(this::showYearPicker));
                break;
            case DAY:
                viewType = ViewType.MONTH;
                updateScrollViewHeight();
                scrollView.requestLayout();
                scrollView.post(() -> scrollView.post(this::showMonthPicker));
                break;
            case YEAR:
                break;
        }
    }

    /**
     * 更新日期展示
     */
    private void updateDateDisplay() {
        currentDateTv.setText(formatDate(selectYear, selectMonth, selectDay));
    }

    /**
     * 更新滑动视图高
     */
    private void updateScrollViewHeight() {
        LinearLayout.LayoutParams scrollViewLayoutParams = (LinearLayout.LayoutParams) scrollView.getLayoutParams();
        if (viewType == ViewType.YEAR) {
            int itemHeight = DensityUtils.dipToPxByInt(50);
            int rowsToShow = 4;
            scrollViewLayoutParams.height = (rowsToShow * itemHeight);
        } else if (viewType == ViewType.MONTH) {
            int itemHeight = DensityUtils.dipToPxByInt(50);
            int rowsToShow = 4;
            scrollViewLayoutParams.height = (rowsToShow * itemHeight);
        } else {
            if (calendarType == CalendarType.SOLAR) {
                int itemHeight = DensityUtils.dipToPxByInt(45);
                int weekHeaderHeight = DensityUtils.dipToPxByInt(40);
                int daysInMonth = getDayInMonth(selectYear, selectMonth);
                int columns = 7;
                int rows = (int) Math.ceil((double) daysInMonth / columns);
                scrollViewLayoutParams.height = (weekHeaderHeight + (rows * itemHeight));
            } else {
                int itemHeight = DensityUtils.dipToPxByInt(50);
                int rowsToShow = 4;
                scrollViewLayoutParams.height = (rowsToShow * itemHeight);
            }
        }
        scrollView.setLayoutParams(scrollViewLayoutParams);
        scrollView.requestLayout();
    }

    /**
     * 更新 UI
     */
    private void updateUI() {
        updateBackMbVisibility();
        updateScrollViewHeight();
        switch (viewType) {
            case YEAR:
                titleTv.setText(getContext().getString(R.string.chooseYear));
                showYearPicker();
                break;
            case MONTH:
                titleTv.setText(String.format(Locale.CHINA, "%d 年", selectYear));
                showMonthPicker();
                break;
            case DAY:
                String monthName = (calendarType == CalendarType.SOLAR) ? (selectMonth + getContext().getString(R.string.month)) : getLunarMonthName(selectMonth);
                titleTv.setText(String.format(Locale.CHINA, "%d 年 %s", selectYear, monthName));
                showDayPicker();
                break;
        }
    }

    /**
     * 显示年选择器
     */
    private void showYearPicker() {
        gridLayout.removeAllViews();
        gridLayout.setColumnCount(3);
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 0; i < 100; i++) {
            final int year = (currentYear - i);
            TextView yearTv = createDateTextView(String.valueOf(year), year == selectYear);
            yearTv.setOnClickListener(v -> {
                selectYear = year;
                updateDateDisplay();
                viewType = ViewType.MONTH;
                updateScrollViewHeight();
                scrollView.requestLayout();
                scrollView.post(() -> scrollView.post(this::showMonthPicker));
            });
            gridLayout.addView(yearTv);
        }
        // 强制立即布局
        gridLayout.requestLayout();
        // 等待布局完成后滚动到选中位置
        scrollView.post(() -> scrollView.post(() -> {
            // 找到选中的 TextView
            int selectIndex = (currentYear - selectYear);
            if (selectIndex >= 0 && selectIndex < gridLayout.getChildCount()) {
                View selectedView = gridLayout.getChildAt(selectIndex);
                if (null != selectedView) {
                    // 获取选中 View 的实际位置
                    int viewTop = selectedView.getTop();
                    int viewHeight = selectedView.getHeight();
                    int visibleHeight = scrollView.getHeight();
                    // 让选中项在可见区域居中
                    int scrollY = (viewTop - (visibleHeight / 2) + (viewHeight / 2));
                    scrollView.scrollTo(0, Math.max(0, scrollY));
                }
            }
        }));
    }

    /**
     * 显示月选择器
     */
    private void showMonthPicker() {
        gridLayout.removeAllViews();
        gridLayout.setColumnCount(3);
        titleTv.setText(String.format(Locale.CHINA, "%d 年", selectYear));
        updateBackMbVisibility();
        for (int i = 1; i <= 12; i++) {
            final int month = i;
            String monthText = (calendarType == CalendarType.SOLAR) ? (i + getContext().getString(R.string.month)) : getLunarMonthName(i);
            TextView monthTv = createDateTextView(monthText, month == selectMonth);
            monthTv.setOnClickListener(v -> {
                selectMonth = month;
                updateDateDisplay();
                viewType = ViewType.DAY;
                updateScrollViewHeight();
                scrollView.requestLayout();
                scrollView.post(() -> scrollView.post(this::showDayPicker));
            });
            gridLayout.addView(monthTv);
        }
        // 强制立即布局
        gridLayout.requestLayout();
        // 等待布局完成后滚动到选中位置
        scrollView.post(() -> scrollView.post(() -> {
            int selectIndex = (selectMonth - 1);
            if (selectIndex >= 0 && selectIndex < gridLayout.getChildCount()) {
                View selectedView = gridLayout.getChildAt(selectIndex);
                if (null != selectedView) {
                    int viewTop = selectedView.getTop();
                    int viewHeight = selectedView.getHeight();
                    int visibleHeight = scrollView.getHeight();
                    int scrollY = (viewTop - (visibleHeight / 2) + (viewHeight / 2));
                    scrollView.scrollTo(0, Math.max(0, scrollY));
                }
            }
        }));
    }

    /**
     * 显示日选择器
     */
    private void showDayPicker() {
        gridLayout.removeAllViews();
        String monthName = (calendarType == CalendarType.SOLAR) ? (selectMonth + getContext().getString(R.string.month)) : getLunarMonthName(selectMonth);
        titleTv.setText(String.format(Locale.CHINA, "%d 年 %s", selectYear, monthName));
        updateBackMbVisibility();
        if (calendarType == CalendarType.SOLAR) {
            gridLayout.setColumnCount(7);
            String[] weekDays = {"日", "一", "二", "三", "四", "五", "六"};
            for (String weekDay : weekDays) {
                TextView dayLabel = TextViewKit.createTextView(getContext(), weekDay, 12, colorOnSurfaceVariant, false);
                dayLabel.setGravity(Gravity.CENTER);
                dayLabel.setPadding(DensityUtils.dipToPxByInt(4), DensityUtils.dipToPxByInt(8), DensityUtils.dipToPxByInt(4), DensityUtils.dipToPxByInt(8));
                GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
                layoutParams.width = 0;
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                layoutParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1.0F);
                dayLabel.setLayoutParams(layoutParams);
                gridLayout.addView(dayLabel);
            }
        } else {
            gridLayout.setColumnCount(5);
        }
        int daysInMonth = getDayInMonth(selectYear, selectMonth);
        for (int i = 1; i <= daysInMonth; i++) {
            final int day = i;
            String dayText = (calendarType == CalendarType.SOLAR) ? String.valueOf(day) : getLunarDayName(day);
            TextView dayTv = createDateTextView(dayText, day == selectDay);
            dayTv.setOnClickListener(v -> {
                selectDay = day;
                updateDateDisplay();
                refreshDayTextView(day);
            });
            gridLayout.addView(dayTv);
        }
        // 强制立即布局
        gridLayout.requestLayout();
        // 多重延迟确保布局完全完成
        scrollView.post(() -> scrollView.post(() -> {
            int columnCount = (calendarType == CalendarType.SOLAR) ? 7 : 5;
            int rowIndex = (selectDay - 1) / columnCount;
            int itemHeight = (calendarType == CalendarType.SOLAR) ? DensityUtils.dipToPxByInt(45) : DensityUtils.dipToPxByInt(50);
            int itemMargin = DensityUtils.dipToPxByInt(8);
            int totalItemHeight = (itemHeight + itemMargin);
            int visibleHeight = scrollView.getHeight();
            if (visibleHeight > 0) {
                int scrollY = ((rowIndex * totalItemHeight) - (visibleHeight / 3));
                scrollView.scrollTo(0, Math.max(0, scrollY));
            }
        }));
    }

    /**
     * 刷新日 TextView
     *
     * @param selectDay 选择日
     */
    private void refreshDayTextView(int selectDay) {
        int childCount = gridLayout.getChildCount();
        int startIndex = (calendarType == CalendarType.SOLAR) ? 7 : 0;
        for (int i = startIndex; i < childCount; i++) {
            View child = gridLayout.getChildAt(i);
            if (child instanceof TextView) {
                TextView textView = (TextView) child;
                int day = (i - startIndex + 1);
                updateTextViewStyle(textView, day == selectDay);
            }
        }
    }

    /**
     * 创造日期 TextView
     *
     * @param text   文本
     * @param select 选否
     * @return 日期 TextView
     */
    @NonNull
    private TextView createDateTextView(String text, boolean select) {
        TextView textView = new TextView(getContext());
        textView.setText(text);
        textView.setTextSize(12);
        textView.setGravity(Gravity.CENTER);
        if ((viewType == ViewType.DAY) && (calendarType == CalendarType.SOLAR)) {
            textView.setPadding(DensityUtils.dipToPxByInt(4), DensityUtils.dipToPxByInt(10), DensityUtils.dipToPxByInt(4), DensityUtils.dipToPxByInt(10));
        } else {
            textView.setPadding(DensityUtils.dipToPxByInt(8), DensityUtils.dipToPxByInt(12), DensityUtils.dipToPxByInt(8), DensityUtils.dipToPxByInt(12));
        }
        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
        layoutParams.width = 0;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1.0F);
        layoutParams.setMargins(DensityUtils.dipToPxByInt(4), DensityUtils.dipToPxByInt(4), DensityUtils.dipToPxByInt(4), DensityUtils.dipToPxByInt(4));
        textView.setLayoutParams(layoutParams);
        updateTextViewStyle(textView, select);
        return textView;
    }

    /**
     * 更新 TextView 样式
     *
     * @param textView TextView
     * @param select   选否
     */
    private void updateTextViewStyle(@NonNull TextView textView, boolean select) {
        textView.setTextColor(select ? colorOnPrimary : colorOnSurface);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setCornerRadius(DensityUtils.dipToPxByInt(12));
        if (select) {
            gradientDrawable.setColor(colorPrimary);
        } else {
            gradientDrawable.setColor(colorSurfaceVariant);
        }
        textView.setBackground(gradientDrawable);
    }

    /**
     * 格式化日期
     *
     * @param year  年
     * @param month 月
     * @param day   日
     * @return 格式化后日期
     */
    @NonNull
    private String formatDate(int year, int month, int day) {
        if (calendarType == CalendarType.SOLAR) {
            return String.format(Locale.CHINA, "%d 年 %d 月 %d 日", year, month, day);
        } else {
            return String.format(Locale.CHINA, "%d 年 %s %s", year, getLunarMonthName(month), getLunarDayName(day));
        }
    }

    private String getLunarMonthName(int month) {
        String[] lunarMonths = {"正月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "冬月", "腊月"};
        return ((month >= 1) && (month <= 12)) ? lunarMonths[month - 1] : (month + "月");
    }

    private String getLunarDayName(int day) {
        String[] lunarDays = {"初一", "初二", "初三", "初四", "初五", "初六", "初七", "初八", "初九", "初十", "十一", "十二", "十三", "十四", "十五", "十六", "十七", "十八", "十九", "二十", "廿一", "廿二", "廿三", "廿四", "廿五", "廿六", "廿七", "廿八", "廿九", "三十"};
        return ((day >= 1) && (day <= 30)) ? lunarDays[day - 1] : String.valueOf(day);
    }

    private int getDayInMonth(int year, int month) {
        if (calendarType == CalendarType.SOLAR) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month - 1, 1);
            return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        } else {
            return (month == 2) ? 29 : 30;
        }
    }

    /**
     * 更新返回按钮可见性
     */
    private void updateBackMbVisibility() {
        backMb.setVisibility((viewType == ViewType.YEAR) ? View.GONE : View.VISIBLE);
    }

    /**
     * 设置初始日期
     *
     * @param year  年
     * @param month 月
     * @param day   日
     */
    public void setInitialDate(int year, int month, int day) {
        this.selectYear = year;
        this.selectMonth = month;
        this.selectDay = day;
    }

    /**
     * 设置初始日期
     *
     * @param year         年
     * @param month        月
     * @param day          日
     * @param calendarType 日历类型
     */
    public void setInitialDate(int year, int month, int day, CalendarType calendarType) {
        this.selectYear = year;
        this.selectMonth = month;
        this.selectDay = day;
        this.calendarType = calendarType;
        if (null != calendarTypeMb) {
            calendarTypeMb.setText((calendarType == CalendarType.SOLAR) ? CalendarType.LUNAR.getName() : CalendarType.SOLAR.getName());
        }
        if (null != currentDateTv) {
            updateDateDisplay();
        }
    }

    /**
     * 设置日历类型
     *
     * @param calendarType 日历类型
     */
    public void setCalendarType(CalendarType calendarType) {
        this.calendarType = calendarType;
    }

    /**
     * 设置日期选择监听
     *
     * @param onDateSelectListener 日期选择监听
     */
    public void setOnDateSelectListener(OnDateSelectListener onDateSelectListener) {
        this.onDateSelectListener = onDateSelectListener;
    }

    /**
     * 日期选择监听
     */
    public interface OnDateSelectListener {
        /**
         * 日期选择
         *
         * @param year         年
         * @param month        月
         * @param day          日
         * @param calendarType 日历类型
         */
        void onDateSelect(int year, int month, int day, CalendarType calendarType);
    }
}