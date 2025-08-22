package com.zsp.today.module.widget.tablayout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.zsp.today.databinding.FragmentTablayoutBinding;

/**
 * @decs: 标签布局碎片
 * @author: 郑少鹏
 * @date: 2025/8/18 12:57
 * @version: v 1.0
 */
public class TabLayoutFragment extends Fragment {
    private static final String TAB_LAYOUT_FRAGMENT_KEY = "TabLayoutFragmentKey";
    private TabLayoutPageViewModel tabLayoutPageViewModel;
    private FragmentTablayoutBinding fragmentTablayoutBinding;

    @NonNull
    public static TabLayoutFragment newInstance(int index) {
        TabLayoutFragment fragment = new TabLayoutFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(TAB_LAYOUT_FRAGMENT_KEY, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tabLayoutPageViewModel = new ViewModelProvider(this).get(TabLayoutPageViewModel.class);
        int index = 1;
        if (null != getArguments()) {
            index = getArguments().getInt(TAB_LAYOUT_FRAGMENT_KEY);
        }
        tabLayoutPageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentTablayoutBinding = FragmentTablayoutBinding.inflate(inflater, container, false);
        View root = fragmentTablayoutBinding.getRoot();
        // 文本
        TextView tabLayoutFragmentTv = fragmentTablayoutBinding.tabLayoutFragmentTv;
        tabLayoutPageViewModel.getText().observe(getViewLifecycleOwner(), tabLayoutFragmentTv::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentTablayoutBinding = null;
    }
}