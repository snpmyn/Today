package com.zsp.today.module.account.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zsp.today.R;
import com.zsp.today.module.account.bean.AccountDateListBean;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import util.list.ListUtils;
import util.screen.ScreenUtils;
import widget.recyclerview.listener.OnRecyclerViewOnItemClickListener;
import widget.recyclerview.listener.OnRecyclerViewOnItemLongClickListener;

/**
 * Created on 2020/12/25
 *
 * @author zsp
 * @desc 账目日列表适配器
 */
public class AccountDateListAdapter extends RecyclerView.Adapter<AccountDateListAdapter.ViewHolder> {
    private final Context context;
    private final int spanCount;
    private final int totalMargin;
    private List<AccountDateListBean> accountDateListBeans;
    private OnRecyclerViewOnItemClickListener onRecyclerViewOnItemClickListener;
    private OnRecyclerViewOnItemLongClickListener onRecyclerViewOnItemLongClickListener;

    /**
     * constructor
     *
     * @param context     上下文
     * @param spanCount   跨距数
     * @param totalMargin 总外边距
     */
    public AccountDateListAdapter(Context context, int spanCount, int totalMargin) {
        this.context = context;
        this.spanCount = spanCount;
        this.totalMargin = totalMargin;
        this.accountDateListBeans = new ArrayList<>();
    }

    public void setAccountDateListData(List<AccountDateListBean> accountDateListBeans) {
        this.accountDateListBeans = accountDateListBeans;
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
        View view = LayoutInflater.from(context).inflate(R.layout.account_date_list_item, viewGroup, false);
        // 宽高等同
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = ((ScreenUtils.screenWidth(context) - totalMargin) / spanCount);
        view.setLayoutParams(layoutParams);
        // 点击监听
        view.setOnClickListener(v -> {
            int position = (Integer) view.getTag();
            onRecyclerViewOnItemClickListener.onItemClick(v, position, accountDateListBeans.get(position));
        });
        view.setOnLongClickListener(v -> {
            int position = (Integer) view.getTag();
            onRecyclerViewOnItemLongClickListener.onItemLongClick(v, position, accountDateListBeans.get(position));
            return true;
        });
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.itemView.setTag(position);
        AccountDateListBean accountDateListBean = accountDateListBeans.get(position);
        // 总金额
        holder.accountDateListItemTvTotalAmount.setText(accountDateListBean.getTotalAmount());
        // 日期
        holder.accountDateListItemTvDate.setText(accountDateListBean.getDate());
    }

    @Override
    public int getItemCount() {
        if (ListUtils.listIsNotEmpty(accountDateListBeans)) {
            return accountDateListBeans.size();
        }
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView accountDateListItemTvTotalAmount;
        private final TextView accountDateListItemTvDate;

        private ViewHolder(@NonNull View view) {
            super(view);
            accountDateListItemTvTotalAmount = view.findViewById(R.id.accountDateListItemTvTotalAmount);
            accountDateListItemTvDate = view.findViewById(R.id.accountDateListItemTvDate);
        }
    }
}