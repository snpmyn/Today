package widget.bottomsheetdialog;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.zsp.core.R;

import java.util.List;

import util.handler.HandlerKit;
import widget.dialog.bottomsheetdialog.MyBottomSheetDialog;
import widget.recyclerview.configure.RecyclerViewConfigure;
import widget.recyclerview.controller.RecyclerViewDisplayController;
import widget.recyclerview.listener.OnRecyclerViewOnItemClickListener;

/**
 * Created on 2025/9/26.
 *
 * @author 郑少鹏
 * @desc 单选 BottomSheetDialog 配套原件
 */
public class SingleChooseBottomSheetDialogKit {
    /**
     * 显示
     *
     * @param appCompatActivity                        活动
     * @param stringList                               字符串集
     * @param defaultSelectPosition                    默选位置
     * @param singleChooseBottomSheetDialogKitListener 单选 BottomSheetDialog 配套原件监听
     */
    public static void show(AppCompatActivity appCompatActivity, List<String> stringList, int defaultSelectPosition, SingleChooseBottomSheetDialogKitListener singleChooseBottomSheetDialogKitListener) {
        // MyBottomSheetDialog
        MyBottomSheetDialog myBottomSheetDialog = new MyBottomSheetDialog(appCompatActivity, R.layout.bottom_sheet_dialog_single_choose);
        View bottomSheetDialogView = myBottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        assert bottomSheetDialogView != null;
        // 控件
        RecyclerView bottomSheetDialogSingleChooseRv = bottomSheetDialogView.findViewById(R.id.bottomSheetDialogSingleChooseRv);
        RecyclerViewConfigure recyclerViewConfigure = new RecyclerViewConfigure(appCompatActivity, bottomSheetDialogSingleChooseRv);
        recyclerViewConfigure.linearVerticalLayout(true, 12, true, true, false);
        // 适配器
        SingleChooseBottomSheetDialogAdapter singleChooseBottomSheetDialogAdapter = getSingleChooseBottomSheetDialogAdapter(appCompatActivity, myBottomSheetDialog, stringList, defaultSelectPosition, singleChooseBottomSheetDialogKitListener);
        // 展示
        RecyclerViewDisplayController.display(bottomSheetDialogSingleChooseRv, singleChooseBottomSheetDialogAdapter);
        // 显示
        myBottomSheetDialog.show();
    }

    /**
     * 获取单选 BottomSheetDialog 适配器
     *
     * @param appCompatActivity                        活动
     * @param bottomSheetDialog                        BottomSheetDialog
     * @param stringList                               字符串集
     * @param defaultSelectPosition                    默选位置
     * @param singleChooseBottomSheetDialogKitListener 单选 BottomSheetDialog 配套原件监听
     * @return 单选 BottomSheetDialog 适配器
     */
    @NonNull
    private static SingleChooseBottomSheetDialogAdapter getSingleChooseBottomSheetDialogAdapter(AppCompatActivity appCompatActivity, BottomSheetDialog bottomSheetDialog, List<String> stringList, int defaultSelectPosition, SingleChooseBottomSheetDialogKitListener singleChooseBottomSheetDialogKitListener) {
        SingleChooseBottomSheetDialogAdapter singleChooseBottomSheetDialogAdapter = new SingleChooseBottomSheetDialogAdapter(appCompatActivity, defaultSelectPosition);
        singleChooseBottomSheetDialogAdapter.setData(stringList);
        singleChooseBottomSheetDialogAdapter.setOnRecyclerViewOnItemClickListener(new OnRecyclerViewOnItemClickListener() {
            @Override
            public <T> void onItemClick(View view, int position, T t) {
                if (null != singleChooseBottomSheetDialogKitListener) {
                    singleChooseBottomSheetDialogKitListener.singleChoose(stringList.get(position));
                    HandlerKit.getInstance().postDelayed(bottomSheetDialog::dismiss, 200);
                }
            }
        });
        return singleChooseBottomSheetDialogAdapter;
    }

    /**
     * 单选 BottomSheetDialog 配套原件监听
     */
    public interface SingleChooseBottomSheetDialogKitListener {
        /**
         * 单选
         *
         * @param value 值
         */
        void singleChoose(String value);
    }
}