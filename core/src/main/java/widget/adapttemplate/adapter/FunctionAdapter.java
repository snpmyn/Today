package widget.adapttemplate.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.zsp.core.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import util.list.ListUtils;
import util.screen.ScreenUtils;
import widget.adapttemplate.bean.FunctionBean;
import widget.recyclerview.listener.OnRecyclerViewOnItemClickListener;

/**
 * Created on 2021/12/2
 *
 * @author zsp
 * @desc 功能适配器
 */
public class FunctionAdapter extends RecyclerView.Adapter<FunctionAdapter.ViewHolder> {
    private final Context context;
    private final int spanCount;
    private final int totalMargin;
    private List<FunctionBean> functionBeans;
    private OnRecyclerViewOnItemClickListener onRecyclerViewOnItemClickListener;

    /**
     * constructor
     *
     * @param context     上下文
     * @param spanCount   跨距数
     * @param totalMargin 总外边距
     */
    public FunctionAdapter(Context context, int spanCount, int totalMargin) {
        this.context = context;
        this.spanCount = spanCount;
        this.totalMargin = totalMargin;
        this.functionBeans = new ArrayList<>();
    }

    public void setFunctionData(List<FunctionBean> functionBeans) {
        this.functionBeans = functionBeans;
    }

    public void setOnRecyclerViewOnItemClickListener(OnRecyclerViewOnItemClickListener onRecyclerViewOnItemLongClickListener) {
        this.onRecyclerViewOnItemClickListener = onRecyclerViewOnItemLongClickListener;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.function_item, viewGroup, false);
        // 宽高等同
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = ((ScreenUtils.screenWidth(context) - totalMargin) / spanCount);
        view.setLayoutParams(layoutParams);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.itemView.setTag(position);
        FunctionBean functionBean = functionBeans.get(position);
        // 功能名称
        holder.functionItemTvFunctionName.setText(functionBean.getFunctionName());
        // 功能显示
        holder.functionItemSmFunctionShow.setChecked(functionBean.isFunctionShow());
        // 选变监听
        holder.functionItemSmFunctionShow.setOnCheckedChangeListener((buttonView, isChecked) -> functionBean.setFunctionShow(isChecked));
        // 点击监听
        holder.functionItemSmFunctionShow.setOnClickListener(v -> onRecyclerViewOnItemClickListener.onItemClick(v, holder.getBindingAdapterPosition(), functionBean));
    }

    @Override
    public int getItemCount() {
        if (ListUtils.listIsNotEmpty(functionBeans)) {
            return functionBeans.size();
        }
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView functionItemTvFunctionName;
        SwitchMaterial functionItemSmFunctionShow;

        private ViewHolder(@NonNull View view) {
            super(view);
            functionItemTvFunctionName = view.findViewById(R.id.functionItemTvFunctionName);
            functionItemSmFunctionShow = view.findViewById(R.id.functionItemSmFunctionShow);
        }
    }
}