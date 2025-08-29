package widget.materialdatepicker;

import static util.datetime.DateUtils.yearMonthDateConversionToYearMonthDate;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.zsp.core.R;

import java.lang.ref.WeakReference;

/**
 * Created on 2021/2/28 0028
 *
 * @author zsp
 * @desp 材料日期选择器配套原件
 */
public class MaterialDatePickerKit {
    public static MaterialDatePickerKit getInstance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * 显示
     *
     * @param appCompatActivity              活动
     * @param materialDatePickerKitInterface 材料日期选择器配套原件接口
     */
    public void show(AppCompatActivity appCompatActivity, MaterialDatePickerKitInterface materialDatePickerKitInterface) {
        WeakReference<AppCompatActivity> weakReference = new WeakReference<>(appCompatActivity);
        MaterialDatePicker<?> materialDatePicker = MaterialDatePicker.Builder.datePicker().setTitleText(R.string.pleaseSelectDate).setSelection(MaterialDatePicker.todayInUtcMilliseconds()).build();
        materialDatePicker.addOnPositiveButtonClickListener((MaterialPickerOnPositiveButtonClickListener<Object>) selection -> materialDatePickerKitInterface.onPositiveButtonClick(yearMonthDateConversionToYearMonthDate(materialDatePicker.getHeaderText())));
        materialDatePicker.addOnNegativeButtonClickListener(v -> materialDatePickerKitInterface.onNegativeButtonClick());
        materialDatePicker.show(weakReference.get().getSupportFragmentManager(), weakReference.get().getClass().getName());
    }

    /**
     * 材料日期选择器配套原件接口
     */
    public interface MaterialDatePickerKitInterface {
        /**
         * 积极按钮点击
         *
         * @param date 日期
         */
        void onPositiveButtonClick(String date);

        /**
         * 消极按钮点击
         */
        void onNegativeButtonClick();
    }

    private static final class InstanceHolder {
        static final MaterialDatePickerKit INSTANCE = new MaterialDatePickerKit();
    }
}