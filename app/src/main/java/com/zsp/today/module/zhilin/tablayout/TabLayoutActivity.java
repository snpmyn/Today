package com.zsp.today.module.zhilin.tablayout;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.zsp.today.R;
import com.zsp.today.databinding.ActivityTablayoutBinding;

/**
 * @decs: 标签布局页
 * @author: 郑少鹏
 * @date: 2025/8/18 12:47
 * @version: v 1.0
 */
public class TabLayoutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityTablayoutBinding activityTablayoutBinding = ActivityTablayoutBinding.inflate(getLayoutInflater());
        setContentView(activityTablayoutBinding.getRoot());
        // 视图翻页器 2
        ViewPager2 viewPager2 = activityTablayoutBinding.tabLayoutActivityVp2;
        // 标签布局适配器
        TabLayoutAdapter tabLayoutAdapter = new TabLayoutAdapter(this);
        // 视图翻页器 2 关联标签布局适配器
        viewPager2.setAdapter(tabLayoutAdapter);
        // 标签布局
        TabLayout tabLayout = getTabLayout(activityTablayoutBinding, viewPager2);
        // 标签布局标题资源 ID 数组
        int[] tabLayoutTitleResIdArray = new int[]{R.string.tabLayoutTitleOne, R.string.tabLayoutTitleTwo};
        // 标签布局联动器
        new TabLayoutMediator(tabLayout, viewPager2, (tab, i) -> tab.setText(getString(tabLayoutTitleResIdArray[i]))).attach();
    }

    /**
     * 获取标签布局
     *
     * @param activityTablayoutBinding ActivityTablayoutBinding
     * @param viewPager2               视图翻页器 2
     * @return 标签布局
     */
    @NonNull
    private static TabLayout getTabLayout(@NonNull ActivityTablayoutBinding activityTablayoutBinding, ViewPager2 viewPager2) {
        TabLayout tabLayout = activityTablayoutBinding.tabLayoutActivityTl;
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // 选中
                viewPager2.setCurrentItem(tab.getPosition(), true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // 未选中
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // 重复选中
            }
        });
        return tabLayout;
    }
}