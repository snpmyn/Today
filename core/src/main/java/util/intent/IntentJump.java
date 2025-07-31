package util.intent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * Created on 2020-08-19
 *
 * @author zsp
 * @desc 意图跳转
 */
public class IntentJump {
    public static IntentJump getInstance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * 跳转
     *
     * @param withValueIntent     携值意图
     * @param context             上下文
     * @param finish              是否关闭
     * @param targetActivityClass 目标活动
     */
    public void jump(Intent withValueIntent, Context context, boolean finish, Class<?> targetActivityClass) {
        Intent intent;
        if (null == withValueIntent) {
            intent = new Intent(context, targetActivityClass);
        } else {
            intent = withValueIntent;
            intent.setClass(context, targetActivityClass);
        }
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
        if (finish && (context instanceof Activity)) {
            Activity activity = (Activity) context;
            activity.finish();
        }
    }

    /**
     * 携动画跳转
     * <p>
     * 默淡入淡出
     *
     * @param withValueIntent     携值意图
     * @param activity            活动
     * @param finish              是否关闭
     * @param targetActivityClass 目标活动
     * @param enterAnim           进动画
     * @param exitAnim            出动画
     */
    public void jumpWithAnimation(Intent withValueIntent, Activity activity, boolean finish, Class<?> targetActivityClass, int enterAnim, int exitAnim) {
        jump(withValueIntent, activity, finish, targetActivityClass);
        activity.overridePendingTransition((enterAnim == 0) ? android.R.anim.fade_in : enterAnim, (exitAnim == 0) ? android.R.anim.fade_out : exitAnim);
    }

    private static final class InstanceHolder {
        static final IntentJump INSTANCE = new IntentJump();
    }
}
