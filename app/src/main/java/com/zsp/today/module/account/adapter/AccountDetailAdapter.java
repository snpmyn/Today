package com.zsp.today.module.account.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zsp.today.R;
import com.zsp.today.module.account.bean.AccountDetailBean;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import util.data.BigDecimalUtils;
import util.list.ListUtils;
import widget.recyclerview.listener.OnRecyclerViewOnItemLongClickListener;

/**
 * Created on 2021/1/4
 *
 * @author zsp
 * @desc 账目详情适配器
 */
public class AccountDetailAdapter extends RecyclerView.Adapter<AccountDetailAdapter.ViewHolder> {
    private final Context context;
    private List<AccountDetailBean> accountDetailBeans;
    private OnRecyclerViewOnItemLongClickListener onRecyclerViewOnItemLongClickListener;

    /**
     * constructor
     *
     * @param context 上下文
     */
    public AccountDetailAdapter(Context context) {
        this.context = context;
        this.accountDetailBeans = new ArrayList<>();
    }

    public void setAccountDetailData(List<AccountDetailBean> accountDetailBeans) {
        this.accountDetailBeans = accountDetailBeans;
    }

    public void setOnRecyclerViewOnItemLongClickListener(OnRecyclerViewOnItemLongClickListener onRecyclerViewOnItemLongClickListener) {
        this.onRecyclerViewOnItemLongClickListener = onRecyclerViewOnItemLongClickListener;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.account_detail_item, viewGroup, false);
        view.setOnLongClickListener(v -> {
            int position = (Integer) view.getTag();
            onRecyclerViewOnItemLongClickListener.onItemLongClick(v, position, accountDetailBeans.get(position));
            return true;
        });
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.itemView.setTag(position);
        AccountDetailBean accountDetailBean = accountDetailBeans.get(position);
        // 类目
        holder.accountDetailItemTvCategory.setText(accountDetailBean.getCategory());
        // 金额
        holder.accountDetailItemTvAmount.setText(BigDecimalUtils.bigDecimalToString(BigDecimal.valueOf(accountDetailBean.getAmount())));
    }

    @Override
    public int getItemCount() {
        if (ListUtils.listIsNotEmpty(accountDetailBeans)) {
            return accountDetailBeans.size();
        }
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView accountDetailItemTvCategory;
        private final TextView accountDetailItemTvAmount;

        private ViewHolder(@NonNull View view) {
            super(view);
            accountDetailItemTvCategory = view.findViewById(R.id.accountDetailItemTvCategory);
            accountDetailItemTvAmount = view.findViewById(R.id.accountDetailItemTvAmount);
        }
    }
}