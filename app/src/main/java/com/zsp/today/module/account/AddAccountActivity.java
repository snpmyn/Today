package com.zsp.today.module.account;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.zsp.today.R;
import com.zsp.today.module.account.kit.AddAccountActivityKit;

import pool.base.BasePoolActivity;
import widget.textwatcher.CustomTextWatcher;

/**
 * @desc: 添加账目页
 * @author: zsp
 * @date: 2021/3/12 5:31 PM
 */
public class AddAccountActivity extends BasePoolActivity {
    private MaterialToolbar addAccountActivityMt;
    private TextInputLayout addAccountActivityTilChooseDate;
    private MaterialAutoCompleteTextView addAccountActivityMactvDate;
    private TextInputLayout addAccountActivityTilChooseCategory;
    private MaterialAutoCompleteTextView addAccountActivityMactvCategory;
    private TextInputLayout addAccountActivityTilInputAmount;
    private TextInputEditText addAccountActivityTietAmount;
    /**
     * 添加账目页配套元件
     */
    private AddAccountActivityKit addAccountActivityKit;

    /**
     * 布局资源 ID
     * <p>
     * Java 动态绑定
     * Java 运行时多态
     * Java 动态分派机制
     * <p>
     * 如果子类重写 layoutResId()
     * 那么 onCreate() 中调用时会优先执行子类的方法
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
        addAccountActivityTilChooseDate = findViewById(R.id.addAccountActivityTilChooseDate);
        addAccountActivityMactvDate = findViewById(R.id.addAccountActivityMactvDate);
        addAccountActivityTilChooseCategory = findViewById(R.id.addAccountActivityTilChooseCategory);
        addAccountActivityMactvCategory = findViewById(R.id.addAccountActivityMactvCategory);
        addAccountActivityTilInputAmount = findViewById(R.id.addAccountActivityTilInputAmount);
        addAccountActivityTietAmount = findViewById(R.id.addAccountActivityTietAmount);
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
        addAccountActivityMt.setOnMenuItemClickListener(item -> {
            if (addAccountActivityKit.areFromAccountHomeActivityOrAccountSecondActivity(AddAccountActivity.this) || addAccountActivityKit.areFromAccountDetailActivityWithAdd(AddAccountActivity.this)) {
                addAccountActivityKit.addAccount(AddAccountActivity.this, addAccountActivityMactvDate, addAccountActivityTilChooseCategory, addAccountActivityMactvCategory, addAccountActivityTilInputAmount, addAccountActivityTietAmount);
            } else if (addAccountActivityKit.areFromAccountDetailActivityWithModify(AddAccountActivity.this)) {
                addAccountActivityKit.modifyAccount(AddAccountActivity.this, addAccountActivityMactvDate, addAccountActivityTilChooseCategory, addAccountActivityMactvCategory, addAccountActivityTilInputAmount, addAccountActivityTietAmount);
            }
            return true;
        });
        // TextInputLayout
        addAccountActivityTilChooseDate.setEndIconOnClickListener(v -> addAccountActivityKit.chooseDate(AddAccountActivity.this, addAccountActivityMactvDate));
        addAccountActivityTilChooseCategory.setEndIconOnClickListener(v -> addAccountActivityKit.chooseCategory(AddAccountActivity.this, addAccountActivityMactvCategory));
        // MaterialAutoCompleteTextView
        addAccountActivityMactvCategory.addTextChangedListener(new CustomTextWatcher(addAccountActivityTilChooseCategory, addAccountActivityMactvCategory));
        // TextInputEditText
        addAccountActivityTietAmount.addTextChangedListener(new CustomTextWatcher(addAccountActivityTilInputAmount, addAccountActivityTietAmount));
    }

    /**
     * 开始逻辑
     */
    @Override
    protected void startLogic() {
        addAccountActivityKit.dateAndCategoryAndAmount(this, addAccountActivityTilChooseDate, addAccountActivityMactvDate, addAccountActivityMactvCategory, addAccountActivityTietAmount);
    }

    /**
     * 传 EditText 的 ID
     * 没传入 EditText 不处理
     *
     * @return ID 数组
     */
    @Override
    protected int[] hideSoftByEditViewIds() {
        return new int[]{R.id.addAccountActivityMactvCategory, R.id.addAccountActivityTietAmount};
    }
}