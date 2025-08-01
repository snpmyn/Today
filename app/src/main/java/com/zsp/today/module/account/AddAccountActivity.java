package com.zsp.today.module.account;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.zsp.today.R;
import com.zsp.today.base.BaseActivity;
import com.zsp.today.module.account.kit.AddAccountActivityKit;

import widget.textwatcher.CustomTextWatcher;

/**
 * @desc: 添加账目页
 * @author: zsp
 * @date: 2021/3/12 5:31 PM
 */
public class AddAccountActivity extends BaseActivity {
    private MaterialToolbar addAccountActivityMt;
    private TextInputLayout addAccountActivityLlTilChooseDate;
    private MaterialAutoCompleteTextView addAccountActivityLlMactvDate;
    private TextInputLayout addAccountActivityLlTilChooseCategory;
    private MaterialAutoCompleteTextView addAccountActivityMactvCategory;
    private TextInputLayout addAccountActivityLlTilInputAmount;
    private TextInputEditText addAccountActivityLlTietAmount;
    /**
     * 添加账目页配套元件
     */
    private AddAccountActivityKit addAccountActivityKit;

    /**
     * 布局资源 ID
     *
     * @return 布局资源 ID
     */
    @Override
    protected int layoutResId() {
        return R.layout.activity_add_account;
    }

    /**
     * 初始控件
     */
    @Override
    protected void stepUi() {
        addAccountActivityMt = findViewById(R.id.addAccountActivityMt);
        addAccountActivityLlTilChooseDate = findViewById(R.id.addAccountActivityLlTilChooseDate);
        addAccountActivityLlMactvDate = findViewById(R.id.addAccountActivityLlMactvDate);
        addAccountActivityLlTilChooseCategory = findViewById(R.id.addAccountActivityLlTilChooseCategory);
        addAccountActivityMactvCategory = findViewById(R.id.addAccountActivityMactvCategory);
        addAccountActivityLlTilInputAmount = findViewById(R.id.addAccountActivityLlTilInputAmount);
        addAccountActivityLlTietAmount = findViewById(R.id.addAccountActivityLlTietAmount);
    }

    /**
     * 初始配置
     */
    @Override
    protected void initConfiguration() {
        addAccountActivityKit = new AddAccountActivityKit();
    }

    /**
     * 设置监听
     */
    @Override
    protected void setListener() {
        // MaterialToolbar
        addAccountActivityMt.setNavigationOnClickListener(v -> finish());
        addAccountActivityMt.setOnMenuItemClickListener(item -> {
            if (addAccountActivityKit.areFromAccountHomeActivityOrAccountSecondActivity(AddAccountActivity.this) || addAccountActivityKit.areFromAccountDetailActivityWithAdd(AddAccountActivity.this)) {
                addAccountActivityKit.addAccount(AddAccountActivity.this, addAccountActivityLlMactvDate, addAccountActivityLlTilChooseCategory, addAccountActivityMactvCategory, addAccountActivityLlTilInputAmount, addAccountActivityLlTietAmount);
            } else if (addAccountActivityKit.areFromAccountDetailActivityWithModify(AddAccountActivity.this)) {
                addAccountActivityKit.modifyAccount(AddAccountActivity.this, addAccountActivityLlMactvDate, addAccountActivityLlTilChooseCategory, addAccountActivityMactvCategory, addAccountActivityLlTilInputAmount, addAccountActivityLlTietAmount);
            }
            return true;
        });
        // TextInputLayout
        addAccountActivityLlTilChooseDate.setEndIconOnClickListener(v -> addAccountActivityKit.chooseDate(AddAccountActivity.this, addAccountActivityLlMactvDate));
        addAccountActivityLlTilChooseCategory.setEndIconOnClickListener(v -> addAccountActivityKit.chooseCategory(AddAccountActivity.this, addAccountActivityMactvCategory));
        // EditText
        addAccountActivityMactvCategory.addTextChangedListener(new CustomTextWatcher(addAccountActivityLlTilChooseCategory, addAccountActivityMactvCategory));
        addAccountActivityLlTietAmount.addTextChangedListener(new CustomTextWatcher(addAccountActivityLlTilInputAmount, addAccountActivityLlTietAmount));
    }

    /**
     * 开始逻辑
     */
    @Override
    protected void startLogic() {
        addAccountActivityKit.dateAndCategoryAndAmount(this, addAccountActivityLlTilChooseDate, addAccountActivityLlMactvDate, addAccountActivityMactvCategory, addAccountActivityLlTietAmount);
    }

    /**
     * 传 EditText 的 ID
     * 没传入 EditText 不处理
     *
     * @return ID 数组
     */
    @Override
    protected int[] hideSoftByEditViewIds() {
        return new int[]{R.id.addAccountActivityMactvCategory, R.id.addAccountActivityLlTietAmount};
    }
}