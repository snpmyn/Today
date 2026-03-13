package com.zsp.today.module.poetry.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.zsp.today.module.poetry.model.PoetryModel;
import com.zsp.today.module.poetry.repository.PoetryRepository;

/**
 * Created on 2026/3/10.
 *
 * @author 郑少鹏
 * @desc 诗词视图模型
 */
public class PoetryViewModel extends ViewModel {
    private final PoetryRepository poetryRepository;

    /**
     * constructor
     */
    public PoetryViewModel() {
        this.poetryRepository = new PoetryRepository();
    }

    /**
     * 获取诗词模型
     *
     * @param baseUrl   基础统一资源定位符
     * @param needToken 需要 token 否
     * @return LiveData<PoetryModel>
     */
    public LiveData<PoetryModel> getPoetryModel(String baseUrl, boolean needToken) {
        return poetryRepository.getPoetryModel(baseUrl, needToken);
    }
}