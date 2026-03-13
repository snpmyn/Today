package com.zsp.today.module.poetry;

import android.text.TextUtils;

import androidx.lifecycle.ViewModelProvider;
import androidx.viewbinding.ViewBinding;

import com.zsp.today.basic.retrofit.RetrofitBaseUrl;
import com.zsp.today.databinding.ActivityPoetryBinding;
import com.zsp.today.module.poetry.viewmodel.PoetryViewModel;

import pool.base.BasePoolActivity;

/**
 * Created on 2026/3/10.
 *
 * @author 郑少鹏
 * @desc 诗词页
 */
public class PoetryActivity extends BasePoolActivity {
    private ActivityPoetryBinding activityPoetryBinding;
    private PoetryViewModel poetryViewModel;

    /**
     * ViewBinding
     * <p>
     * Java 动态绑定
     * Java 运行时多态
     * Java 动态分派机制
     * <p>
     * 如果子类重写 viewBinding()
     * 那么 onCreate() 中调用时会优先执行子类的方法
     *
     * @return ViewBinding
     */
    @Override
    protected ViewBinding viewBinding() {
        activityPoetryBinding = ActivityPoetryBinding.inflate(getLayoutInflater());
        return activityPoetryBinding;
    }

    /**
     * 初始控件
     */
    @Override
    protected void stepUi() {

    }

    /**
     * 初始配置
     */
    @Override
    protected void initConfiguration() {
        poetryViewModel = new ViewModelProvider(this).get(PoetryViewModel.class);

    }

    /**
     * 设置监听
     */
    @Override
    protected void setListener() {

    }

    /**
     * 开始逻辑
     */
    @Override
    protected void startLogic() {
        poetryViewModel.getPoetryModel(RetrofitBaseUrl.POETRY_BASE_URL, true).observe(this, poetryModel -> {
            if (null != poetryModel) {
                activityPoetryBinding.poetryActivityTitle.setText(poetryModel.getData().getOrigin().getTitle());
                activityPoetryBinding.poetryActivityAuthor.setText(poetryModel.getData().getOrigin().getAuthor());
                activityPoetryBinding.poetryActivityDynasty.setText(poetryModel.getData().getOrigin().getDynasty());
                activityPoetryBinding.poetryActivityContent.setText(TextUtils.join("\n", poetryModel.getData().getOrigin().getContent()));
            }
        });
    }
}