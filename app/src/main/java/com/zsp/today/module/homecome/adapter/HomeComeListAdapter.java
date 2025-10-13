package com.zsp.today.module.homecome.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.zsp.today.R;
import com.zsp.today.module.homecome.bean.HomeComeListBean;
import com.zsp.today.module.homecome.kit.HomeComeActivityKit;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import util.list.ListUtils;
import widget.recyclerview.custom.SmartFlexibleRecyclerView;
import widget.recyclerview.listener.OnRecyclerViewOnItemInnerClickListener;

/**
 * Created on 2025/10/10.
 *
 * @author 郑少鹏
 * @desc 归心列表适配器
 */
public class HomeComeListAdapter extends RecyclerView.Adapter<HomeComeListAdapter.ViewHolder> implements SmartFlexibleRecyclerView.DraggableAdapter {
    private final Context context;
    private List<HomeComeListBean> homeComeListBeans;
    private HomeComeActivityKit homeComeActivityKit;
    private OnRecyclerViewOnItemInnerClickListener onRecyclerViewOnItemInnerClickListener;

    /**
     * constructor
     *
     * @param context 上下文
     */
    public HomeComeListAdapter(Context context) {
        this.context = context;
        this.homeComeListBeans = new ArrayList<>();
    }

    public void setHomeComeListData(List<HomeComeListBean> homeComeListBeans) {
        this.homeComeListBeans = homeComeListBeans;
        this.homeComeActivityKit = new HomeComeActivityKit();
    }

    public void setOnRecyclerViewOnItemInnerClickListener(OnRecyclerViewOnItemInnerClickListener onRecyclerViewOnItemInnerClickListener) {
        this.onRecyclerViewOnItemInnerClickListener = onRecyclerViewOnItemInnerClickListener;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_come_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.itemView.setTag(position);
        HomeComeListBean homeComeListBean = homeComeListBeans.get(position);
        // 处理
        holder.homeComeItemMbHandle.setOnClickListener(v -> onRecyclerViewOnItemInnerClickListener.onItemInnerClick(holder.homeComeItemMbHandle, holder.getBindingAdapterPosition(), homeComeListBean));
        // 称呼
        holder.homeComeItemTvCall.setText(homeComeListBean.getCall());
        // 纪念信息
        holder.homeComeItemTvMemorialInfo.setText(homeComeListBean.getMemorialInfo().message);
    }

    @Override
    public int getItemCount() {
        if (ListUtils.listIsNotEmpty(homeComeListBeans)) {
            return homeComeListBeans.size();
        }
        return 0;
    }

    @Override
    public void swapData(int fromPosition, int toPosition) {
        Collections.swap(homeComeListBeans, fromPosition, toPosition);
        homeComeActivityKit.swap(homeComeListBeans);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final MaterialButton homeComeItemMbHandle;
        private final TextView homeComeItemTvCall;
        private final TextView homeComeItemTvMemorialInfo;

        private ViewHolder(@NonNull View view) {
            super(view);
            homeComeItemMbHandle = view.findViewById(R.id.homeComeItemMbHandle);
            homeComeItemTvCall = view.findViewById(R.id.homeComeItemTvCall);
            homeComeItemTvMemorialInfo = view.findViewById(R.id.homeComeItemTvMemorialInfo);
        }
    }
}