package com.zsp.today.module.account.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zsp.today.R;
import com.zsp.today.module.account.bean.AccountMonthListBean;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import util.list.ListUtils;
import widget.recyclerview.listener.OnRecyclerViewOnItemClickListener;
import widget.recyclerview.listener.OnRecyclerViewOnItemLongClickListener;

/**
 * Created on 2021/11/4
 *
 * @author zsp
 * @desc 账目月列表适配器
 */
public class AccountMonthListAdapter extends RecyclerView.Adapter<AccountMonthListAdapter.ViewHolder> {
    private final Context context;
    private List<AccountMonthListBean> accountMonthListBeans;
    private OnRecyclerViewOnItemClickListener onRecyclerViewOnItemClickListener;
    private OnRecyclerViewOnItemLongClickListener onRecyclerViewOnItemLongClickListener;

    /**
     * constructor
     *
     * @param context 上下文
     */
    public AccountMonthListAdapter(Context context) {
        this.context = context;
        this.accountMonthListBeans = new ArrayList<>();
    }

    public void setAccountMonthListData(List<AccountMonthListBean> accountMonthListBeans) {
        this.accountMonthListBeans = accountMonthListBeans;
    }

    public void setOnRecyclerViewOnItemClickListener(OnRecyclerViewOnItemClickListener onRecyclerViewOnItemClickListener) {
        this.onRecyclerViewOnItemClickListener = onRecyclerViewOnItemClickListener;
    }

    public void setOnRecyclerViewOnItemLongClickListener(OnRecyclerViewOnItemLongClickListener onRecyclerViewOnItemLongClickListener) {
        this.onRecyclerViewOnItemLongClickListener = onRecyclerViewOnItemLongClickListener;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.account_month_list_item, viewGroup, false);
        // 点击监听
        view.setOnClickListener(v -> {
            int position = (Integer) view.getTag();
            onRecyclerViewOnItemClickListener.onItemClick(v, position, accountMonthListBeans.get(position));
        });
        view.setOnLongClickListener(v -> {
            int position = (Integer) view.getTag();
            onRecyclerViewOnItemLongClickListener.onItemLongClick(v, position, accountMonthListBeans.get(position));
            return true;
        });
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.itemView.setTag(position);
        AccountMonthListBean accountMonthListBean = accountMonthListBeans.get(position);
        // 月
        holder.accountMonthListItemTvMonth.setText(String.format(context.getString(R.string.formatMonth), accountMonthListBean.getMonth()));
        // 总金额
        holder.accountMonthListItemTvTotalAmount.setText(String.format(context.getString(R.string.formatYuan), accountMonthListBean.getTotalAmount()));
    }

    @Override
    public int getItemCount() {
        if (ListUtils.listIsNotEmpty(accountMonthListBeans)) {
            return accountMonthListBeans.size();
        }
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView accountMonthListItemTvMonth;
        private final TextView accountMonthListItemTvTotalAmount;

        private ViewHolder(@NonNull View view) {
            super(view);
            accountMonthListItemTvMonth = view.findViewById(R.id.accountMonthListItemTvMonth);
            accountMonthListItemTvTotalAmount = view.findViewById(R.id.accountMonthListItemTvTotalAmount);
        }
    }
}