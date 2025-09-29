package widget.bottomsheetdialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.zsp.core.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import util.list.ListUtils;
import widget.recyclerview.listener.OnRecyclerViewOnItemClickListener;

/**
 * Created on 2025/9/26.
 *
 * @author 郑少鹏
 * @desc 单选 BottomSheetDialog 适配器
 */
public class SingleChooseBottomSheetDialogAdapter extends RecyclerView.Adapter<SingleChooseBottomSheetDialogAdapter.ViewHolder> {
    private final Context context;
    private List<String> stringList;
    private int currentPosition;
    private OnRecyclerViewOnItemClickListener onRecyclerViewOnItemClickListener;

    /**
     * constructor
     *
     * @param context               上下文
     * @param defaultSelectPosition 默选位置
     *                              不选 {@link RecyclerView#NO_POSITION}
     */
    public SingleChooseBottomSheetDialogAdapter(Context context, int defaultSelectPosition) {
        this.context = context;
        this.stringList = new ArrayList<>();
        this.currentPosition = defaultSelectPosition;
    }

    public void setData(List<String> stringList) {
        this.stringList = stringList;
    }

    public void setOnRecyclerViewOnItemClickListener(OnRecyclerViewOnItemClickListener onRecyclerViewOnItemClickListener) {
        this.onRecyclerViewOnItemClickListener = onRecyclerViewOnItemClickListener;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_dialog_single_choose_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // MaterialCardView
        holder.bottomSheetDialogSingleChooseItemMcv.setChecked(position == currentPosition);
        // TextView
        holder.bottomSheetDialogSingleChooseItemTv.setText(stringList.get(position));
        // 短点
        holder.itemView.setOnClickListener(v -> {
            // 点击出现新位置
            int position1 = holder.getBindingAdapterPosition();
            // 1 - 赋值以前位置为当前位置
            int previousPosition = currentPosition;
            // 2 - 赋值当前位置为新位置
            currentPosition = position1;
            // 3 - 以前位置非 -1 刷新
            if (previousPosition != RecyclerView.NO_POSITION) {
                notifyItemChanged(previousPosition);
            }
            // 4 - 刷新当前位置
            notifyItemChanged(currentPosition);
            // 回调
            onRecyclerViewOnItemClickListener.onItemClick(v, position1, getCurrentItem());
        });
    }

    @Override
    public int getItemCount() {
        if (ListUtils.listIsNotEmpty(stringList)) {
            return stringList.size();
        }
        return 0;
    }

    /**
     * 获取当前位置
     *
     * @return 当前位置
     */
    public int getCurrentPosition() {
        return currentPosition;
    }

    /**
     * 获取当前条目
     *
     * @return 当前条目
     */
    public String getCurrentItem() {
        if (currentPosition != RecyclerView.NO_POSITION) {
            return stringList.get(currentPosition);
        }
        return null;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView bottomSheetDialogSingleChooseItemMcv;
        private final TextView bottomSheetDialogSingleChooseItemTv;

        private ViewHolder(@NonNull View view) {
            super(view);
            bottomSheetDialogSingleChooseItemMcv = view.findViewById(R.id.bottomSheetDialogSingleChooseItemMcv);
            bottomSheetDialogSingleChooseItemTv = view.findViewById(R.id.bottomSheetDialogSingleChooseItemTv);
        }
    }
}