package widget.dialog.materialalertdialog.kit;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import widget.dialog.materialalertdialog.listener.UseGuideMaterialAlertDialogKitListener;

/**
 * Created on 2025/9/3.
 *
 * @author 郑少鹏
 * @desc 使用指南材料对话框配套元件
 * <p>
 * 该类用单例模式会导致数据重复紊乱
 */
public class UseGuideMaterialAlertDialogKit {
    /**
     * 使用指南内容集
     */
    private List<List<String>> useGuideContentList;

    /**
     * 准备数据
     *
     * @param useGuideContent 使用指南内容
     */
    public void prepareData(String... useGuideContent) {
        if (null == useGuideContentList) {
            useGuideContentList = new ArrayList<>();
        }
        useGuideContentList.add(List.of(useGuideContent));
    }

    /**
     * 显示
     *
     * @param appCompatActivity                      活动
     * @param index                                  下标
     * @param cancelable                             取消否
     * @param useGuideMaterialAlertDialogKitListener 使用指南材料对话框配套元件监听
     */
    public void show(AppCompatActivity appCompatActivity, int index, boolean cancelable, UseGuideMaterialAlertDialogKitListener useGuideMaterialAlertDialogKitListener) {
        List<String> stringList = useGuideContentList.get(index);
        // 使用指南标题
        String useGuideTitle = stringList.get(0);
        // 使用指南内容
        String useGuideContent = stringList.get(1);
        // 使用指南消极按钮文本
        String useGuideNegativeButtonText = stringList.get(2);
        // 使用指南积极按钮文本
        String useGuidePositiveButtonText = stringList.get(3);
        new MaterialAlertDialogBuilderKit(appCompatActivity).setTitle(useGuideTitle).setMessage(useGuideContent).setPositiveButton(useGuidePositiveButtonText, (dialog, which) -> {
            dialog.dismiss();
            // 下一步
            if (index < (useGuideContentList.size() - 1)) {
                show(appCompatActivity, index + 1, cancelable, useGuideMaterialAlertDialogKitListener);
            }
            // 去使用
            boolean goToUse = (null != useGuideMaterialAlertDialogKitListener) && (index == (useGuideContentList.size() - 1));
            if (goToUse) {
                useGuideMaterialAlertDialogKitListener.closeOrGoToUse();
            }
        }).setNegativeButton(useGuideNegativeButtonText, (dialog, which) -> {
            dialog.dismiss();
            // 关闭
            boolean close = (null != useGuideMaterialAlertDialogKitListener) && (index == 0);
            if (close) {
                useGuideMaterialAlertDialogKitListener.closeOrGoToUse();
            }
            // 上一步
            boolean flag = (index > 0) && (index < useGuideContentList.size());
            if (flag) {
                show(appCompatActivity, index - 1, cancelable, useGuideMaterialAlertDialogKitListener);
            }
        }).setCancelable(cancelable).show();
    }
}