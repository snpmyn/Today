package com.zsp.today.module.account.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.zsp.today.R;
import com.zsp.today.module.account.bean.AccountMonthListBean;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import util.list.ListUtils;
import widget.recyclerview.listener.OnRecyclerViewOnItemClickListener;
import widget.recyclerview.listener.OnRecyclerViewOnItemInnerClickListener;
import widget.recyclerview.listener.OnRecyclerViewOnItemLongClickListener;
import widget.textview.kit.TextViewKit;

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
    private OnRecyclerViewOnItemInnerClickListener onRecyclerViewOnItemInnerClickListener;

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

    public void setOnRecyclerViewOnItemInnerClickListener(OnRecyclerViewOnItemInnerClickListener onRecyclerViewOnItemInnerClickListener) {
        this.onRecyclerViewOnItemInnerClickListener = onRecyclerViewOnItemInnerClickListener;
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
        // 总金额
        holder.accountMonthListItemTvTotalAmount.setText(String.format("￥ %1$s", accountMonthListBean.getTotalAmount()));
        // 月
        holder.accountMonthListItemTvMonth.setText(String.format("%1$s 月", accountMonthListBean.getMonth()));
        // 消费笔数
        holder.accountMonthListItemTvNumberOfConsumptionTransactions.setText(String.valueOf(accountMonthListBean.getCount()));
        // 月度环比
        if (accountMonthListBean.monthOnMonthGreaterThanZero()) {
            int color = ContextCompat.getColor(context, com.zsp.core.R.color.color_FFF44336);
            holder.accountMonthListItemTvMonthOnMonth.setTextColor(color);
            TextViewKit.setDrawable(context, holder.accountMonthListItemTvMonthOnMonth, R.drawable.ic_trending_up_cos_20dp, 1, 10);
            TextViewKit.setDrawableColor(holder.accountMonthListItemTvMonthOnMonth, color);
        } else if (accountMonthListBean.monthOnMonthLessThanZero()) {
            int color = ContextCompat.getColor(context, com.zsp.core.R.color.color_228B22);
            holder.accountMonthListItemTvMonthOnMonth.setTextColor(color);
            TextViewKit.setDrawable(context, holder.accountMonthListItemTvMonthOnMonth, R.drawable.ic_trending_down_cos_20dp, 1, 10);
            TextViewKit.setDrawableColor(holder.accountMonthListItemTvMonthOnMonth, color);
        }
        holder.accountMonthListItemTvMonthOnMonth.setText(accountMonthListBean.getMonthOnMonthDescribe());
        // 最大消费
        holder.accountMonthListItemTvLargestConsumer.setText(accountMonthListBean.getMaxCategoryAndAmount());
        // 排序
        holder.accountMonthListItemMbSort.setOnClickListener(v -> onRecyclerViewOnItemInnerClickListener.onItemInnerClick(holder.accountMonthListItemMbSort, holder.getBindingAdapterPosition(), accountMonthListBean));
    }

    @Override
    public int getItemCount() {
        if (ListUtils.listIsNotEmpty(accountMonthListBeans)) {
            return accountMonthListBeans.size();
        }
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView accountMonthListItemTvTotalAmount;
        private final TextView accountMonthListItemTvMonth;
        private final TextView accountMonthListItemTvNumberOfConsumptionTransactions;
        private final TextView accountMonthListItemTvMonthOnMonth;
        private final TextView accountMonthListItemTvLargestConsumer;
        private final MaterialButton accountMonthListItemMbSort;

        private ViewHolder(@NonNull View view) {
            super(view);
            accountMonthListItemTvTotalAmount = view.findViewById(R.id.accountMonthListItemTvTotalAmount);
            accountMonthListItemTvMonth = view.findViewById(R.id.accountMonthListItemTvMonth);
            accountMonthListItemTvNumberOfConsumptionTransactions = view.findViewById(R.id.accountMonthListItemTvNumberOfConsumptionTransactions);
            accountMonthListItemTvMonthOnMonth = view.findViewById(R.id.accountMonthListItemTvMonthOnMonth);
            accountMonthListItemTvLargestConsumer = view.findViewById(R.id.accountMonthListItemTvLargestConsumer);
            accountMonthListItemMbSort = view.findViewById(R.id.accountMonthListItemMbSort);
        }
    }
}