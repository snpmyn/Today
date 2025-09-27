package widget.screen.kit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.zsp.core.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import util.data.IntUtils;
import widget.recyclerview.configure.RecyclerViewConfigure;
import widget.screen.adapter.ScreenAdapter;
import widget.screen.bean.MutuallyExclusiveBean;
import widget.screen.bean.UnfoldAndFoldBean;
import widget.screen.listener.ScreenHandleListener;

/**
 * Created on 2019/6/10.
 *
 * @author 郑少鹏
 * @desc ScreenHandleKit
 */
public class ScreenHandleKit implements View.OnClickListener {
    /**
     * 上下文
     */
    private final Context context;
    /**
     * 主体数据
     */
    private final Map<List<String>, Map<Integer, Boolean>> subjectMap;
    /**
     * 单选后可反选数据
     */
    private final List<String> canCancelAfterSingleSelectList;
    /**
     * 默选数据
     */
    private final Map<String, List<String>> defaultSelectMap;
    /**
     * 互斥数据、互斥数据类别数据
     */
    private final List<MutuallyExclusiveBean> mutuallyExclusiveBeanList;
    private final List<String> mutuallyExclusiveBeanListClassificationList;
    /**
     * 展开 / 折叠数据、展开 / 折叠数据主控类别数据、展开 / 折叠数据被控类别数据
     */
    private final List<UnfoldAndFoldBean> unfoldAndFoldBeanList;
    private final List<String> unfoldAndFoldBeanListActiveControlClassificationList;
    private final List<String> unfoldAndFoldBeanListPassiveControlClassificationList;
    /**
     * BottomSheetDialog
     */
    private BottomSheetDialog bottomSheetDialog;
    private RecyclerView screenBottomSheetDialogRv;
    /**
     * 筛选适配器
     */
    private ScreenAdapter screenAdapter;
    /**
     * 筛选操作接口
     */
    private ScreenHandleListener screenHandleListener;

    /**
     * constructor
     *
     * @param context 上下文
     */
    public ScreenHandleKit(Context context) {
        // 上下文
        this.context = context;
        // 主体数据
        this.subjectMap = new LinkedHashMap<>();
        // 单选后可反选数据
        this.canCancelAfterSingleSelectList = new ArrayList<>();
        // 默选数据
        this.defaultSelectMap = new LinkedHashMap<>();
        // 互斥数据、互斥数据类别数据
        this.mutuallyExclusiveBeanList = new ArrayList<>();
        this.mutuallyExclusiveBeanListClassificationList = new ArrayList<>();
        // 展开 / 折叠数据、展开 / 折叠数据主控类别数据、展开 / 折叠数据被控类别数据
        this.unfoldAndFoldBeanList = new ArrayList<>();
        this.unfoldAndFoldBeanListActiveControlClassificationList = new ArrayList<>();
        this.unfoldAndFoldBeanListPassiveControlClassificationList = new ArrayList<>();
        // 初始 BottomSheetDialog
        stepBottomSheetDialog();
    }

    /**
     * 初始BottomSheetDialog
     */
    private void stepBottomSheetDialog() {
        @SuppressLint("InflateParams") View bottomSheetDialogView = LayoutInflater.from(context).inflate(R.layout.screen_bottom_sheet_dialog, null);
        // 重置
        MaterialButton screenBottomSheetDialogMbReset = bottomSheetDialogView.findViewById(R.id.screenBottomSheetDialogMbReset);
        screenBottomSheetDialogMbReset.setOnClickListener(this);
        // 确定
        MaterialButton screenBottomSheetDialogMbEnsure = bottomSheetDialogView.findViewById(R.id.screenBottomSheetDialogMbEnsure);
        screenBottomSheetDialogMbEnsure.setOnClickListener(this);
        // RecyclerView
        screenBottomSheetDialogRv = bottomSheetDialogView.findViewById(R.id.screenBottomSheetDialogRv);
        RecyclerViewConfigure recyclerViewConfigure = new RecyclerViewConfigure(context, screenBottomSheetDialogRv);
        recyclerViewConfigure.linearVerticalLayout(false, 0, false, false, false);
        // BottomSheetDialog
        bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(bottomSheetDialogView);
    }

    /**
     * 打包字符串条件
     *
     * @param classification 类别
     * @param spanCount      跨距数
     * @param singleSelect   单选否
     * @param conditions     条件
     */
    public void packStringConditions(String classification, int spanCount, boolean singleSelect, @NonNull String... conditions) {
        List<String> list = new ArrayList<>(1 + conditions.length);
        list.add(classification);
        list.addAll(Arrays.asList(conditions));
        Map<Integer, Boolean> map = new LinkedHashMap<>();
        map.put(spanCount, singleSelect);
        subjectMap.put(list, map);
    }

    /**
     * 打包集合条件
     *
     * @param classification 类别
     * @param spanCount      跨距数
     * @param singleSelect   单选否
     * @param conditions     条件
     */
    public void packListConditions(String classification, int spanCount, boolean singleSelect, @NonNull List<String> conditions) {
        List<String> list = new ArrayList<>(1 + conditions.size());
        list.add(classification);
        list.addAll(conditions);
        Map<Integer, Boolean> map = new LinkedHashMap<>();
        map.put(spanCount, singleSelect);
        subjectMap.put(list, map);
    }

    /**
     * 单选后可反选
     *
     * @param classifications 类别
     */
    public void canReverseSelectAfterSingleSelect(@NonNull String... classifications) {
        for (String classification : classifications) {
            if (canCancelAfterSingleSelectList.contains(classification)) {
                return;
            }
            canCancelAfterSingleSelectList.add(classification);
        }
    }

    /**
     * 默选
     * <p>
     * 单选仅呈现默选一条件
     * 多选可呈现默选多条件
     *
     * @param classification 类别
     * @param conditions     条件
     */
    public void defaultSelect(String classification, String... conditions) {
        defaultSelectMap.put(classification, Arrays.asList(conditions));
    }

    /**
     * 互斥
     *
     * @param strings 数据（组 ID，类别、组 ID，类别...）
     */
    public void mutuallyExclusive(@NonNull String... strings) {
        for (int i = 0; i < strings.length; i++) {
            if (IntUtils.even(i)) {
                String classification = strings[i + 1];
                mutuallyExclusiveBeanList.add(new MutuallyExclusiveBean(strings[i], classification));
                mutuallyExclusiveBeanListClassificationList.add(classification);
            }
        }
    }

    /**
     * 展开 / 折叠
     *
     * @param activeControlClassification   主控类别
     * @param activeControlCondition        主控条件
     * @param passiveControlClassifications 被控类别
     */
    public void unfoldAndFold(String activeControlClassification, String activeControlCondition, String... passiveControlClassifications) {
        List<String> list = Arrays.asList(passiveControlClassifications);
        UnfoldAndFoldBean unfoldAndFoldBean = new UnfoldAndFoldBean(activeControlClassification, activeControlCondition, list);
        unfoldAndFoldBeanList.add(unfoldAndFoldBean);
        if (!unfoldAndFoldBeanListActiveControlClassificationList.contains(activeControlClassification)) {
            unfoldAndFoldBeanListActiveControlClassificationList.add(activeControlClassification);
        }
        for (String string : list) {
            if (!unfoldAndFoldBeanListPassiveControlClassificationList.contains(string)) {
                unfoldAndFoldBeanListPassiveControlClassificationList.add(string);
            }
        }
    }

    /**
     * 关联
     */
    public void associate() {
        screenAdapter = new ScreenAdapter(context);
        screenAdapter.setScreeningData(subjectMap, canCancelAfterSingleSelectList, defaultSelectMap, mutuallyExclusiveBeanList, mutuallyExclusiveBeanListClassificationList, unfoldAndFoldBeanList, unfoldAndFoldBeanListActiveControlClassificationList, unfoldAndFoldBeanListPassiveControlClassificationList);
        screenBottomSheetDialogRv.setAdapter(screenAdapter);
        screenAdapter.setScreenAdapterItemClickListener((view, classification, condition, selected) -> screenHandleListener.click(view, classification, condition, selected));
    }

    /**
     * 显
     */
    public void show() {
        bottomSheetDialog.show();
    }

    /**
     * 消
     */
    public void dismiss() {
        bottomSheetDialog.dismiss();
    }

    /**
     * 重置
     */
    public void reset() {
        screenAdapter.reset();
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(@NonNull View v) {
        if (v.getId() == R.id.screenBottomSheetDialogMbReset) {
            // 重置
            screenHandleListener.reset();
        } else if (v.getId() == R.id.screenBottomSheetDialogMbEnsure) {
            // 确定
            screenHandleListener.ensure();
        }
    }

    /**
     * 设筛选操作监听
     *
     * @param screenHandleListener 筛选操作监听
     */
    public void setScreenHandleListener(ScreenHandleListener screenHandleListener) {
        this.screenHandleListener = screenHandleListener;
    }
}