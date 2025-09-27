package com.zsp.today.module.setting;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.zsp.today.R;
import com.zsp.today.module.setting.fragment.SettingFragment;

/**
 * Created on 2025/9/27.
 *
 * @author 郑少鹏
 * @desc 设置页
 */
public class SettingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportFragmentManager().beginTransaction().replace(R.id.settingActivityFl, new SettingFragment()).commit();
    }
}