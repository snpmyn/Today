package com.zsp.today.main.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.zsp.today.module.homepage.HomePageFragment;
import com.zsp.today.module.mine.MineFragment;

/**
 * Created on 2025/12/20.
 *
 * @author 郑少鹏
 * @desc 主页碎片状态适配器
 */
public class MainActivityFragmentStateAdapter extends FragmentStateAdapter {
    public MainActivityFragmentStateAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return MineFragment.newInstance();
        }
        return HomePageFragment.newInstance();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}