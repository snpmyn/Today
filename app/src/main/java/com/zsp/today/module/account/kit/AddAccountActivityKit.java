package com.zsp.today.module.account.kit;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.zsp.today.R;
import com.zsp.today.application.App;
import com.zsp.today.kit.BackupKit;
import com.zsp.today.module.account.bean.AccountTransferBean;
import com.zsp.today.module.account.database.AccountDataBaseTable;
import com.zsp.today.value.AccountCondition;
import com.zsp.today.value.AccountConstant;
import com.zsp.today.value.RxBusConstant;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import litepal.kit.LitePalKit;
import util.data.BigDecimalUtils;
import util.datetime.DateUtils;
import util.intent.IntentVerify;
import util.keyboard.KeyboardUtils;
import util.list.ListUtils;
import util.rxbus.RxBus;
import widget.toast.ToastKit;
import widget.dialog.materialalertdialog.SingleChooseMaterialAlertDialogKit;
import widget.materialdatepicker.MaterialDatePickerKit;

/**
 * Created on 2020/12/23
 *
 * @author zsp
 * @desc 添加账目页配套元件
 */
public class AddAccountActivityKit {
    /**
     * 日期和类目和金额
     *
     * @param appCompatActivity                    活动
     * @param addAccountActivityLlTilChooseDate    日期选择框
     * @param materialAutoCompleteTextViewDate     日期框
     * @param materialAutoCompleteTextViewCategory 类目框
     * @param textInputEditTextAmount              金额框
     */
    public void dateAndCategoryAndAmount(@NonNull AppCompatActivity appCompatActivity, TextInputLayout addAccountActivityLlTilChooseDate, @NonNull MaterialAutoCompleteTextView materialAutoCompleteTextViewDate, @NonNull MaterialAutoCompleteTextView materialAutoCompleteTextViewCategory, @NonNull TextInputEditText textInputEditTextAmount) {
        AccountTransferBean accountTransferBean = (AccountTransferBean) IntentVerify.getSerializableExtra(appCompatActivity.getIntent(), AccountConstant.ACCOUNT_DETAIL_ACTIVITY_$_ACCOUNT_TRANSFER_BEAN);
        boolean flag = (null == accountTransferBean);
        // 日期
        String date = flag ? "" : accountTransferBean.getDate();
        materialAutoCompleteTextViewDate.setText(TextUtils.isEmpty(date) ? DateUtils.getCurrentTimeYearMonthDay() : date);
        // 类目
        materialAutoCompleteTextViewCategory.setText(flag ? "" : accountTransferBean.getCategory());
        // 金额
        String amountString = BigDecimalUtils.bigDecimalToString(BigDecimal.valueOf(flag ? 0.0 : accountTransferBean.getAmount()));
        textInputEditTextAmount.setText(TextUtils.equals("0", amountString) ? "" : amountString);
        // 账目详情页修改模式禁用日期选择框
        if (areFromAccountDetailActivityWithModify(appCompatActivity)) {
            addAccountActivityLlTilChooseDate.setEnabled(false);
            addAccountActivityLlTilChooseDate.setEndIconVisible(false);
        }
    }

    /**
     * 选择日期
     *
     * @param appCompatActivity                活动
     * @param materialAutoCompleteTextViewDate 日期框
     */
    public void chooseDate(AppCompatActivity appCompatActivity, MaterialAutoCompleteTextView materialAutoCompleteTextViewDate) {
        MaterialDatePickerKit.getInstance().show(appCompatActivity, new MaterialDatePickerKit.MaterialDatePickerKitInterface() {
            @Override
            public void onPositiveButtonClick(String date) {
                materialAutoCompleteTextViewDate.setText(date);
            }

            @Override
            public void onNegativeButtonClick() {

            }
        });
    }

    /**
     * 选择类目
     *
     * @param appCompatActivity                    活动
     * @param materialAutoCompleteTextViewCategory 类目框
     */
    public void chooseCategory(AppCompatActivity appCompatActivity, MaterialAutoCompleteTextView materialAutoCompleteTextViewCategory) {
        KeyboardUtils.closeKeyboardInActivity(appCompatActivity);
        SingleChooseMaterialAlertDialogKit.getInstance().show(appCompatActivity, appCompatActivity.getResources().getStringArray(R.array.category), value -> {
            materialAutoCompleteTextViewCategory.setText(value);
            materialAutoCompleteTextViewCategory.setSelection(value.length());
        });
    }

    /**
     * 添加账目
     * <p>
     * 场景一（首页子碎片进入添加账目页）
     * 场景二（账目详情页添加模式下进入添加账目页）
     *
     * @param appCompatActivity                    活动
     * @param materialAutoCompleteTextViewDate     日期框
     * @param textInputLayoutChooseCategory        类目选择框
     * @param materialAutoCompleteTextViewCategory 类目框
     * @param textInputLayoutInputAmount           金额输入框
     * @param textInputEditTextAmount              金额框
     */
    public void addAccount(AppCompatActivity appCompatActivity, @NonNull MaterialAutoCompleteTextView materialAutoCompleteTextViewDate, TextInputLayout textInputLayoutChooseCategory, @NonNull MaterialAutoCompleteTextView materialAutoCompleteTextViewCategory, TextInputLayout textInputLayoutInputAmount, @NonNull TextInputEditText textInputEditTextAmount) {
        String nowDate = materialAutoCompleteTextViewDate.getText().toString();
        String nowCategory = materialAutoCompleteTextViewCategory.getText().toString();
        String nowAmount = Objects.requireNonNull(textInputEditTextAmount.getText()).toString().trim();
        if (TextUtils.isEmpty(nowCategory)) {
            textInputLayoutChooseCategory.setError(appCompatActivity.getString(R.string.pleaseChooseCategory));
            return;
        } else if (TextUtils.isEmpty(nowAmount)) {
            textInputLayoutInputAmount.setError(appCompatActivity.getString(R.string.pleaseInputAmount));
            return;
        }
        String[] conditions = new String[]{AccountCondition.ACCOUNT_PHONE_NUMBER_AND_DATE_AND_CATEGORY, App.getAppInstance().getPhoneNumber(), nowDate, nowCategory};
        List<AccountDataBaseTable> accountDataBaseTableList = LitePalKit.getInstance().queryByWhere(AccountDataBaseTable.class, conditions);
        if (ListUtils.listIsNotEmpty(accountDataBaseTableList)) {
            AccountDataBaseTable accountDataBaseTable = accountDataBaseTableList.get(0);
            // 合并金额
            accountDataBaseTable.setAmount(BigDecimalUtils.add(BigDecimal.valueOf(Double.parseDouble(nowAmount)), BigDecimal.valueOf(accountDataBaseTable.getAmount())).doubleValue());
            LitePalKit.getInstance().multiUpdate(accountDataBaseTable, conditions);
            hintAndRefreshAccount(appCompatActivity);
        } else if (LitePalKit.getInstance().singleSave(new AccountDataBaseTable(App.getAppInstance().getPhoneNumber(), nowDate, nowCategory, Double.parseDouble(nowAmount)))) {
            hintAndRefreshAccount(appCompatActivity);
        }
        BackupKit.getInstance().backup(appCompatActivity, AccountDataBaseTable.class, null);
    }

    /**
     * 修改账目
     * <p>
     * 账目详情页修改模式下进入添加账目页。
     *
     * @param appCompatActivity                    活动
     * @param materialAutoCompleteTextViewDate     日期框
     * @param textInputLayoutChooseCategory        类目选择框
     * @param materialAutoCompleteTextViewCategory 类目框
     * @param textInputLayoutInputAmount           金额输入框
     * @param textInputEditTextAmount              金额框
     */
    public void modifyAccount(AppCompatActivity appCompatActivity, @NonNull MaterialAutoCompleteTextView materialAutoCompleteTextViewDate, TextInputLayout textInputLayoutChooseCategory, @NonNull MaterialAutoCompleteTextView materialAutoCompleteTextViewCategory, TextInputLayout textInputLayoutInputAmount, @NonNull TextInputEditText textInputEditTextAmount) {
        String oldDate = materialAutoCompleteTextViewDate.getText().toString();
        String nowCategory = materialAutoCompleteTextViewCategory.getText().toString();
        String nowAmount = Objects.requireNonNull(textInputEditTextAmount.getText()).toString().trim();
        if (TextUtils.isEmpty(nowCategory)) {
            textInputLayoutChooseCategory.setError(appCompatActivity.getString(R.string.pleaseChooseCategory));
            return;
        } else if (TextUtils.isEmpty(nowAmount)) {
            textInputLayoutInputAmount.setError(appCompatActivity.getString(R.string.pleaseInputAmount));
            return;
        }
        AccountTransferBean accountTransferBean = (AccountTransferBean) IntentVerify.getSerializableExtra(appCompatActivity.getIntent(), AccountConstant.ACCOUNT_DETAIL_ACTIVITY_$_ACCOUNT_TRANSFER_BEAN);
        assert accountTransferBean != null;
        if (TextUtils.equals(nowCategory, accountTransferBean.getCategory())) {
            // 场景一：修改金额。
            // 据原日期、原类目查询本地数据库。重设金额，多个更新。
            String[] conditions = new String[]{AccountCondition.ACCOUNT_PHONE_NUMBER_AND_DATE_AND_CATEGORY, App.getAppInstance().getPhoneNumber(), oldDate, nowCategory};
            List<AccountDataBaseTable> accountDataBaseTableList = LitePalKit.getInstance().queryByWhere(AccountDataBaseTable.class, conditions);
            AccountDataBaseTable accountDataBaseTable = accountDataBaseTableList.get(0);
            accountDataBaseTable.setAmount(Double.parseDouble(nowAmount));
            LitePalKit.getInstance().multiUpdate(accountDataBaseTable, conditions);
            hintAndRefreshAccount(appCompatActivity);
        } else {
            // 场景二：修改类目、金额。
            // 删原账目。据原日期、新类目查询本地数据库。有则合并，多个更新；无则单个保存。
            String[] oldConditions = new String[]{AccountCondition.ACCOUNT_PHONE_NUMBER_AND_DATE_AND_CATEGORY_AND_AMOUNT, App.getAppInstance().getPhoneNumber(), accountTransferBean.getDate(), accountTransferBean.getCategory(), BigDecimalUtils.bigDecimalToString(BigDecimal.valueOf(accountTransferBean.getAmount()))};
            LitePalKit.getInstance().multiDelete(AccountDataBaseTable.class, oldConditions);
            String[] nowConditions = new String[]{AccountCondition.ACCOUNT_PHONE_NUMBER_AND_DATE_AND_CATEGORY, App.getAppInstance().getPhoneNumber(), oldDate, nowCategory};
            List<AccountDataBaseTable> accountDataBaseTableNowList = LitePalKit.getInstance().queryByWhere(AccountDataBaseTable.class, nowConditions);
            if (ListUtils.listIsNotEmpty(accountDataBaseTableNowList)) {
                AccountDataBaseTable accountDataBaseTableNow = accountDataBaseTableNowList.get(0);
                accountDataBaseTableNow.setAmount(BigDecimalUtils.add(BigDecimal.valueOf(Double.parseDouble(nowAmount)), BigDecimal.valueOf(accountDataBaseTableNow.getAmount())).doubleValue());
                LitePalKit.getInstance().multiUpdate(accountDataBaseTableNow, nowConditions);
                hintAndRefreshAccount(appCompatActivity);
            } else if (LitePalKit.getInstance().singleSave(new AccountDataBaseTable(App.getAppInstance().getPhoneNumber(), oldDate, nowCategory, Double.parseDouble(nowAmount)))) {
                hintAndRefreshAccount(appCompatActivity);
            }
        }
        BackupKit.getInstance().backup(appCompatActivity, AccountDataBaseTable.class, null);
    }

    /**
     * 提示和刷新账目
     * <p>
     * 场景一：首页子碎片进入添加账目页。只需刷新首页子碎片账目。
     * 场景二：账目详情页（添加模式、修改模式）进入添加账目页。同时刷新首页子碎片和账目详情页账目。
     *
     * @param appCompatActivity 活动
     */
    private void hintAndRefreshAccount(@NonNull AppCompatActivity appCompatActivity) {
        appCompatActivity.finish();
        ToastKit.showShort(areFromAccountDetailActivityWithModify(appCompatActivity) ? appCompatActivity.getString(R.string.modifySuccessful) : appCompatActivity.getString(R.string.enterIntoAccountSuccessful));
        RxBus.get().post(RxBusConstant.ACCOUNT_HOME_ACTIVITY_AND_SECOND_ACTIVITY_$_REFRESH_ACCOUNT, RxBusConstant.ACCOUNT_HOME_ACTIVITY_AND_SECOND_ACTIVITY_$_REFRESH_ACCOUNT_CODE);
        // 账目详情页（添加模式、修改模式）
        if (areFromAccountDetailActivityWithAdd(appCompatActivity) || areFromAccountDetailActivityWithModify(appCompatActivity)) {
            RxBus.get().post(RxBusConstant.ACCOUNT_DETAIL_ACTIVITY_$_REFRESH_ACCOUNT, RxBusConstant.ACCOUNT_DETAIL_ACTIVITY_$_REFRESH_ACCOUNT_CODE);
        }
    }

    /**
     * 来自账目主页或账目二页
     *
     * @param appCompatActivity 活动
     * @return 来自账目主页或账目二页否
     */
    public boolean areFromAccountHomeActivityOrAccountSecondActivity(@NonNull AppCompatActivity appCompatActivity) {
        AccountTransferBean accountTransferBean = (AccountTransferBean) IntentVerify.getSerializableExtra(appCompatActivity.getIntent(), AccountConstant.ACCOUNT_DETAIL_ACTIVITY_$_ACCOUNT_TRANSFER_BEAN);
        return (null == accountTransferBean);
    }

    /**
     * 来自账目详情页添加模式
     * <p>
     * 账目详情页添加模式下携日期进入添加账目页。
     *
     * @param appCompatActivity 活动
     * @return 来自账目详情页添加模式否
     */
    public boolean areFromAccountDetailActivityWithAdd(@NonNull AppCompatActivity appCompatActivity) {
        AccountTransferBean accountTransferBean = (AccountTransferBean) IntentVerify.getSerializableExtra(appCompatActivity.getIntent(), AccountConstant.ACCOUNT_DETAIL_ACTIVITY_$_ACCOUNT_TRANSFER_BEAN);
        return ((null != accountTransferBean) && !TextUtils.isEmpty(accountTransferBean.getDate()) && TextUtils.isEmpty(accountTransferBean.getCategory()) && (accountTransferBean.getAmount() == 0.0));
    }

    /**
     * 来自账目详情页修改模式
     * <p>
     * 账目详情页修改模式下携日期、类目、金额进入添加账目页。
     *
     * @param appCompatActivity 活动
     * @return 来自账目详情页修改模式否
     */
    public boolean areFromAccountDetailActivityWithModify(@NonNull AppCompatActivity appCompatActivity) {
        AccountTransferBean accountTransferBean = (AccountTransferBean) IntentVerify.getSerializableExtra(appCompatActivity.getIntent(), AccountConstant.ACCOUNT_DETAIL_ACTIVITY_$_ACCOUNT_TRANSFER_BEAN);
        return ((null != accountTransferBean) && !TextUtils.isEmpty(accountTransferBean.getDate()) && !TextUtils.isEmpty(accountTransferBean.getCategory()) && (accountTransferBean.getAmount() != 0.0));
    }
}