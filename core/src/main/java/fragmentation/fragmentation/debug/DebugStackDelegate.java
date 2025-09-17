package fragmentation.fragmentation.debug;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentationMagician;

import com.zsp.core.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import fragmentation.fragmentation.Fragmentation;
import fragmentation.fragmentation.ISupportFragment;
import fragmentation.value.FragmentationMagic;
import timber.log.Timber;

/**
 * @decs: DebugStackDelegate
 * @author: 郑少鹏
 * @date: 2019/5/20 9:29
 */
public class DebugStackDelegate implements SensorEventListener {
    private final FragmentActivity mActivity;
    private SensorManager mSensorManager;
    private AlertDialog mStackDialog;

    public DebugStackDelegate(FragmentActivity fragmentActivity) {
        this.mActivity = fragmentActivity;
    }

    public void onCreate(int mode) {
        if (mode != Fragmentation.SHAKE) {
            return;
        }
        mSensorManager = (SensorManager) mActivity.getSystemService(Context.SENSOR_SERVICE);
        if (null != mSensorManager) {
            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void onPostCreate(int mode) {
        if (mode != Fragmentation.BUBBLE) {
            return;
        }
        View root = mActivity.findViewById(android.R.id.content);
        if (root instanceof FrameLayout) {
            FrameLayout content = (FrameLayout) root;
            final ImageView stackView = new ImageView(mActivity);
            stackView.setImageResource(R.drawable.fragmentation_stack);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.END;
            final int dp18 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, mActivity.getResources().getDisplayMetrics());
            params.topMargin = dp18 * 7;
            params.rightMargin = dp18;
            stackView.setLayoutParams(params);
            content.addView(stackView);
            stackView.setOnTouchListener(new StackViewTouchListener(stackView, dp18 / 4));
            stackView.setOnClickListener(v -> showFragmentStackHierarchyView());
        }
    }

    public void onDestroy() {
        if (null != mSensorManager) {
            mSensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(@NotNull SensorEvent event) {
        int sensorType = event.sensor.getType();
        float[] values = event.values;
        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            int value = 12;
            if ((Math.abs(values[0]) >= value) || (Math.abs(values[1]) >= value) || (Math.abs(values[FragmentationMagic.INT_TWO]) >= value)) {
                showFragmentStackHierarchyView();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * 调试相关
     * <p>
     * Dialog 形式显栈视图。
     */
    public void showFragmentStackHierarchyView() {
        if ((null != mStackDialog) && mStackDialog.isShowing()) {
            return;
        }
        DebugHierarchyViewContainer debugHierarchyViewContainer = new DebugHierarchyViewContainer(mActivity);
        debugHierarchyViewContainer.bindFragmentRecords(getFragmentRecords());
        debugHierarchyViewContainer.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mStackDialog = new AlertDialog.Builder(mActivity).setView(debugHierarchyViewContainer).setPositiveButton(android.R.string.cancel, null).setCancelable(true).create();
        mStackDialog.show();
    }

    /**
     * 调试相关
     * <p>
     * Log 形式打印栈视图。
     */
    public void logFragmentRecords() {
        List<DebugFragmentRecord> fragmentRecordList = getFragmentRecords();
        if (null == fragmentRecordList) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = (fragmentRecordList.size() - 1); i >= 0; i--) {
            DebugFragmentRecord fragmentRecord = fragmentRecordList.get(i);
            if (i == (fragmentRecordList.size() - 1)) {
                stringBuilder.append("═══════════════════════════════════════════════════════════════════════════════════\n");
                if (i == 0) {
                    stringBuilder.append("\t栈顶\t\t\t").append(fragmentRecord.fragmentName).append("\n");
                    stringBuilder.append("═══════════════════════════════════════════════════════════════════════════════════");
                } else {
                    stringBuilder.append("\t栈顶\t\t\t").append(fragmentRecord.fragmentName).append("\n\n");
                }
            } else if (i == 0) {
                stringBuilder.append("\t栈底\t\t\t").append(fragmentRecord.fragmentName).append("\n\n");
                processChildLog(fragmentRecord.debugFragmentRecords, stringBuilder, 1);
                stringBuilder.append("═══════════════════════════════════════════════════════════════════════════════════");
                Timber.d(stringBuilder.toString());
                return;
            } else {
                stringBuilder.append("\t↓\t\t\t").append(fragmentRecord.fragmentName).append("\n\n");
            }
            processChildLog(fragmentRecord.debugFragmentRecords, stringBuilder, 1);
        }
    }

    private @Nullable List<DebugFragmentRecord> getFragmentRecords() {
        List<DebugFragmentRecord> fragmentRecordList = new ArrayList<>();
        List<Fragment> fragmentList = FragmentationMagician.getActiveFragments(mActivity.getSupportFragmentManager());
        if (fragmentList.isEmpty()) {
            return null;
        }
        for (Fragment fragment : fragmentList) {
            addDebugFragmentRecord(fragmentRecordList, fragment);
        }
        return fragmentRecordList;
    }

    private void processChildLog(List<DebugFragmentRecord> fragmentRecordList, StringBuilder sb, int childHierarchy) {
        if ((null == fragmentRecordList) || (fragmentRecordList.isEmpty())) {
            return;
        }
        for (int j = 0; j < fragmentRecordList.size(); j++) {
            DebugFragmentRecord childFragmentRecord = fragmentRecordList.get(j);
            for (int k = 0; k < childHierarchy; k++) {
                sb.append("\t\t\t");
            }
            if (j == 0) {
                sb.append("\t子栈顶\t\t").append(childFragmentRecord.fragmentName).append("\n\n");
            } else if (j == fragmentRecordList.size() - 1) {
                sb.append("\t子栈底\t\t").append(childFragmentRecord.fragmentName).append("\n\n");
                processChildLog(childFragmentRecord.debugFragmentRecords, sb, ++childHierarchy);
                return;
            } else {
                sb.append("\t↓\t\t\t").append(childFragmentRecord.fragmentName).append("\n\n");
            }
            processChildLog(childFragmentRecord.debugFragmentRecords, sb, childHierarchy);
        }
    }

    private @Nullable List<DebugFragmentRecord> getChildFragmentRecords(@NotNull Fragment parentFragment) {
        List<DebugFragmentRecord> fragmentRecords = new ArrayList<>();
        List<Fragment> fragmentList = FragmentationMagician.getActiveFragments(parentFragment.getChildFragmentManager());
        if (fragmentList.isEmpty()) {
            return null;
        }
        for (int i = (fragmentList.size() - 1); i >= 0; i--) {
            Fragment fragment = fragmentList.get(i);
            addDebugFragmentRecord(fragmentRecords, fragment);
        }
        return fragmentRecords;
    }

    private void addDebugFragmentRecord(List<DebugFragmentRecord> fragmentRecords, Fragment fragment) {
        if (null != fragment) {
            int backStackCount;
            backStackCount = fragment.getParentFragmentManager().getBackStackEntryCount();
            CharSequence name = fragment.getClass().getSimpleName();
            if (backStackCount == 0) {
                name = span(name, " *");
            } else {
                for (int j = 0; j < backStackCount; j++) {
                    FragmentManager.BackStackEntry backStackEntry = fragment.getParentFragmentManager().getBackStackEntryAt(j);
                    boolean flag = ((null != backStackEntry.getName()) && backStackEntry.getName().equals(fragment.getTag())) || ((null == backStackEntry.getName()) && (null == fragment.getTag()));
                    if (flag) {
                        break;
                    }
                    if (j == (backStackCount - 1)) {
                        name = span(name, " *");
                    }
                }
            }
            if ((fragment instanceof ISupportFragment) && ((ISupportFragment) fragment).isSupportVisible()) {
                name = span(name, " \uD83D\uDD25");
            }
            fragmentRecords.add(new DebugFragmentRecord(name, getChildFragmentRecords(fragment)));
        }
    }

    @NonNull
    private CharSequence span(CharSequence name, String str) {
        name = (name + str);
        return name;
    }

    private static class StackViewTouchListener implements View.OnTouchListener {
        private final View stackView;
        private final int clickLimitValue;
        private float dx, dy = 0.0F;
        private float xDown, yDown = 0.0F;
        private boolean isClickState;

        StackViewTouchListener(View stackView, int clickLimitValue) {
            this.stackView = stackView;
            this.clickLimitValue = clickLimitValue;
        }

        @Override
        public boolean onTouch(View v, @NotNull MotionEvent event) {
            float x = event.getRawX();
            float y = event.getRawY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isClickState = true;
                    xDown = x;
                    yDown = y;
                    dx = (stackView.getX() - event.getRawX());
                    dy = (stackView.getY() - event.getRawY());
                    break;
                case MotionEvent.ACTION_MOVE:
                    if ((Math.abs(x - xDown) < clickLimitValue) && (Math.abs(y - yDown) < clickLimitValue) && isClickState) {
                        Timber.d("areClickState = true");
                    } else {
                        isClickState = false;
                        stackView.setX(event.getRawX() + dx);
                        stackView.setY(event.getRawY() + dy);
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    if (((x - xDown) < clickLimitValue) && isClickState) {
                        stackView.performClick();
                    }
                    break;
                default:
                    return false;
            }
            return true;
        }
    }
}