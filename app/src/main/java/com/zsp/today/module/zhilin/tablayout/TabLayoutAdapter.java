package com.zsp.today.module.zhilin.tablayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

/**
 * @decs: 标签布局适配器
 * @author: 郑少鹏
 * @date: 2025/8/18 17:15
 * @version: v 1.0
 */
public class TabLayoutAdapter extends FragmentStateAdapter {
    /**
     * constructor
     *
     * @param fragmentActivity FragmentActivity
     */
    public TabLayoutAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return TabLayoutFragment.newInstance(position + 1);
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}