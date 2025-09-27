package com.zsp.today.module.mine.adapter;

import android.animation.ValueAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.zsp.today.R;
import com.zsp.today.module.mine.bean.SplashAnimationListBean;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import lottie.kit.LottieKit;
import util.list.ListUtils;
import util.screen.ScreenUtils;
import widget.recyclerview.listener.OnRecyclerViewOnItemClickListener;

/**
 * Created on 2022/5/7
 *
 * @author zsp
 * @desc 闪屏动画列表适配器
 */
public class SplashAnimationListAdapter extends RecyclerView.Adapter<SplashAnimationListAdapter.ViewHolder> {
    private final Context context;
    private final int spanCount;
    private final int totalMargin;
    private List<SplashAnimationListBean> splashAnimationListBeans;
    private OnRecyclerViewOnItemClickListener onRecyclerViewOnItemClickFullScreenListener;

    /**
     * constructor
     *
     * @param context     上下文
     * @param spanCount   跨距数
     * @param totalMargin 总外边距
     */
    public SplashAnimationListAdapter(Context context, int spanCount, int totalMargin) {
        this.context = context;
        this.spanCount = spanCount;
        this.totalMargin = totalMargin;
        this.splashAnimationListBeans = new ArrayList<>();
    }

    public void setSplashAnimationData(List<SplashAnimationListBean> splashAnimationListBeans) {
        this.splashAnimationListBeans = splashAnimationListBeans;
    }

    public void setOnRecyclerViewOnItemClickFullScreenListener(OnRecyclerViewOnItemClickListener onRecyclerViewOnItemClickFullScreenListener) {
        this.onRecyclerViewOnItemClickFullScreenListener = onRecyclerViewOnItemClickFullScreenListener;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.splash_animation_list_item, viewGroup, false);
        // 宽高等同
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = ((ScreenUtils.screenWidth(context) - totalMargin) / spanCount);
        view.setLayoutParams(layoutParams);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.itemView.setTag(position);
        SplashAnimationListBean splashAnimationListBean = splashAnimationListBeans.get(position);
        // 动画
        LottieKit.getInstance().useWithAsset(holder.splashAnimationListItemLav, splashAnimationListBean.getResName() + ".json", ValueAnimator.INFINITE, null);
        // 全屏
        holder.splashAnimationListItemIb.setOnClickListener(v -> onRecyclerViewOnItemClickFullScreenListener.onItemClick(v, holder.getBindingAdapterPosition(), splashAnimationListBean));
    }

    @Override
    public int getItemCount() {
        if (ListUtils.listIsNotEmpty(splashAnimationListBeans)) {
            return splashAnimationListBeans.size();
        }
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final LottieAnimationView splashAnimationListItemLav;
        private final ImageButton splashAnimationListItemIb;

        private ViewHolder(@NonNull View view) {
            super(view);
            splashAnimationListItemLav = view.findViewById(R.id.splashAnimationListItemLav);
            splashAnimationListItemIb = view.findViewById(R.id.splashAnimationListItemIb);
        }
    }
}