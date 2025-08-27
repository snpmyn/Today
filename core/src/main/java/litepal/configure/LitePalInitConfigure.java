package litepal.configure;

import android.app.Application;

import litepal.LitePal;

/**
 * Created on 2018/11/23.
 *
 * @author 郑少鹏
 * @desc LitePal 初始化配置
 */
public class LitePalInitConfigure {
    /**
     * 初始化 LitePal
     *
     * @param application 应用
     */
    public static void initLitePal(Application application) {
        LitePal.initialize(application);
    }
}