package com.zsp.today.module.widget.tablayout;

import static androidx.lifecycle.Transformations.map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * @decs: 标签布局页面视图模型
 * @author: 郑少鹏
 * @date: 2025/8/18 17:14
 * @version: v 1.0
 */
public class TabLayoutPageViewModel extends ViewModel {
    private final MutableLiveData<Integer> index = new MutableLiveData<>();
    private final LiveData<String> text = map(index, input -> this.getClass().getSimpleName() + " " + input);

    public void setIndex(int index) {
        this.index.setValue(index);
    }

    public LiveData<String> getText() {
        return text;
    }
}