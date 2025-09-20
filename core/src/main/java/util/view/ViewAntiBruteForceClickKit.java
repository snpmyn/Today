package util.view;

import android.view.View;

import androidx.annotation.NonNull;

import util.datetime.CurrentTimeMillisClock;

/**
 * @decs: 视图防暴力点击配套原件
 * @author: 郑少鹏
 * @date: 2025/9/16 11:42
 * @version: v 1.0
 */
public class ViewAntiBruteForceClickKit {
    /**
     * 设置视图防暴力点击监听
     *
     * @param view            视图
     * @param intervalMs      间隔毫秒
     * @param onClickListener 点击监听
     */
    public static void setViewAntiBruteForceClickListener(@NonNull final View view, final long intervalMs, final View.OnClickListener onClickListener) {
        view.setOnClickListener(new View.OnClickListener() {
            private long lastClickTime = 0;

            @Override
            public void onClick(View v) {
                long currentTime = CurrentTimeMillisClock.getInstance().now();
                if ((currentTime - lastClickTime) >= intervalMs) {
                    lastClickTime = currentTime;
                    onClickListener.onClick(v);
                }
            }
        });
    }
}