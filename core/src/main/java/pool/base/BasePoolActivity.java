package pool.base;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;

import util.edittext.EditTextUtils;
import util.keyboard.KeyboardUtils;
import util.rxbus.RxBus;
import util.view.ViewUtils;

/**
 * Created on 2021/3/9
 *
 * @author zsp
 * @desc BasePoolActivity
 * 优点：
 * 方便代码编写，减重复代码，加快开发；
 * 优化代码结构，降耦合度，方便修改；
 * 提代码可读性，显井井有条、优美。
 * <p>
 * 下抽象法子类须实现：
 * {@link #layoutResId()}
 * {@link #stepUi()}
 * {@link #initConfiguration()}
 * {@link #setListener()}
 * {@link #startLogic()}
 */
public abstract class BasePoolActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 注册事件
        RxBus.get().register(this);
        // 加载视图
        setContentView(layoutResId());
        // 初始控件
        stepUi();
        // 初始配置
        initConfiguration();
        // 设置监听
        setListener();
        // 开始逻辑
        startLogic();
    }

    /**
     * 布局资源 ID
     *
     * @return 布局资源 ID
     */
    protected abstract int layoutResId();

    /**
     * 初始控件
     */
    protected abstract void stepUi();

    /**
     * 初始配置
     */
    protected abstract void initConfiguration();

    /**
     * 设置监听
     */
    protected abstract void setListener();

    /**
     * 开始逻辑
     */
    protected abstract void startLogic();

    /**
     * 传 EditText 的 ID
     * 没传入 EditText 不处理
     *
     * @return ID 数组
     */
    protected int[] hideSoftByEditViewIds() {
        return null;
    }

    /**
     * 传需过滤 View
     * 过滤后点无隐软键盘操作
     *
     * @return ID 数组
     */
    protected View[] filterViewByIds() {
        return null;
    }

    /**
     * Note: return supportActivityDelegate.dispatchTouchEvent(ev) || super.dispatchTouchEvent(ev);
     *
     * @param ev 手势事件
     * @return boolean
     */
    @Override
    public boolean dispatchTouchEvent(@NotNull MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (ViewUtils.isTouchView(filterViewByIds(), ev)) {
                return super.dispatchTouchEvent(ev);
            }
            if ((null == hideSoftByEditViewIds()) || (hideSoftByEditViewIds().length == 0)) {
                return super.dispatchTouchEvent(ev);
            }
            View view = getCurrentFocus();
            if (EditTextUtils.isFocusEditText(view, hideSoftByEditViewIds())) {
                if (ViewUtils.isTouchView(this, hideSoftByEditViewIds(), ev)) {
                    return super.dispatchTouchEvent(ev);
                }
                // 隐键盘
                KeyboardUtils.closeKeyboardInActivity(this);
                EditTextUtils.clearViewFocus(view, hideSoftByEditViewIds());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 反注册事件
        RxBus.get().unregister(this);
    }
}