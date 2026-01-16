package pool.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import util.rxbus.RxBus;

/**
 * Created on 2025/12/20.
 *
 * @author 郑少鹏
 * @desc BasePoolFragment
 */
public abstract class BasePoolFragment extends Fragment {
    protected Context baseLazyFragmentContext;
    protected AppCompatActivity baseLazyFragmentAppCompatActivity;
    protected boolean isViewCreated = false;
    protected boolean isFragmentVisible = false;
    protected boolean isFragmentFirstVisible = true;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AppCompatActivity) {
            this.baseLazyFragmentContext = context;
            baseLazyFragmentAppCompatActivity = (AppCompatActivity) context;
        } else {
            throw new IllegalStateException("Fragment must be attached to AppCompatActivity.");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        RxBus.get().register(this);
        isViewCreated = true;
        initView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isViewCreated && !isFragmentVisible) {
            isFragmentVisible = true;
            if (isFragmentFirstVisible) {
                isFragmentFirstVisible = false;
                onFragmentFirstVisible();
            }
            onFragmentVisible();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isFragmentVisible) {
            isFragmentVisible = false;
            onFragmentInvisible();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxBus.get().unregister(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        baseLazyFragmentContext = null;
        baseLazyFragmentAppCompatActivity = null;
    }

    /**
     * 获取布局 ID
     *
     * @return 布局 ID
     */
    protected abstract int getLayoutId();

    /**
     * 初始视图
     *
     * @param view 视图
     */
    protected abstract void initView(View view);

    /**
     * 头次可见
     * <p>
     * 只调一次
     * <p>
     * 网络请求
     * 耗时任务
     * 初始数据
     */
    protected abstract void onFragmentFirstVisible();

    /**
     * 每次可见
     * <p>
     * 刷新
     */
    protected void onFragmentVisible() {

    }

    /**
     * 每次不可见
     */
    protected void onFragmentInvisible() {

    }
}