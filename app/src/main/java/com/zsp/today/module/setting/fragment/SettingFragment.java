package com.zsp.today.module.setting.fragment;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.zsp.today.R;

/**
 * Created on 2025/9/27.
 *
 * @author 郑少鹏
 * @desc 设置碎片
 */
public class SettingFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }
}