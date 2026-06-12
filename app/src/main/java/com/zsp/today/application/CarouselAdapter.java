package com.zsp.today.application;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2026/6/3.
 *
 * @author 郑少鹏
 * @desc 轮播适配器
 */
public abstract class CarouselAdapter<T> extends RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder> {
    /**
     * 轮播数据
     */
    private final List<T> carouselData = new ArrayList<>();
    /**
     * 是否无限循环
     */
    private boolean infiniteLoop = true;
    /**
     * 条目点击监听
     */
    private OnItemClickListener<T> onItemClickListener;

    @NonNull
    @Override
    public CarouselViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(getItemLayoutId(), parent, false);
        return new CarouselViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarouselAdapter.CarouselViewHolder holder, int position) {
        if (carouselData.isEmpty()) {
            return;
        }
        // 真实位置
        final int realPosition = getRealPosition(position);
        // 条目
        final T item = carouselData.get(realPosition);
        // 绑定条目
        onBindItem(holder, item, realPosition);
        // 点击事件
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(v, item, realPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (carouselData.isEmpty()) {
            return 0;
        }
        return (infiniteLoop ? Integer.MAX_VALUE : carouselData.size());
    }

    /**
     * 获取真实位置
     *
     * @param adapterPosition 适配器位置
     * @return 真实位置
     */
    public int getRealPosition(int adapterPosition) {
        if (carouselData.isEmpty()) {
            return 0;
        }
        return (adapterPosition % carouselData.size());
    }

    /**
     * 获取轮播数据
     *
     * @return 轮播数据
     */
    public List<T> getCarouselData() {
        return carouselData;
    }

    /**
     * 设置轮播数据
     *
     * @param list List<T>
     */
    public void setCarouselData(List<T> list) {
        carouselData.clear();
        if (list != null) {
            carouselData.addAll(list);
        }
        notifyDataSetChanged();
    }

    /**
     * 添加轮播数据
     *
     * @param list List<T>
     */
    public void addCarouselData(List<T> list) {
        if ((null == list) || list.isEmpty()) {
            return;
        }
        int start = carouselData.size();
        carouselData.addAll(list);
        notifyItemRangeInserted(start, list.size());
    }

    /**
     * 获取真实数量
     *
     * @return 真实数量
     */
    public int getRealCount() {
        return carouselData.size();
    }

    /**
     * 是否无限循环
     *
     * @return 是否无限循环
     */
    public boolean isInfiniteLoop() {
        return infiniteLoop;
    }

    /**
     * 设置是否无限循环
     *
     * @param infiniteLoop 是否无限循环
     */
    public void setInfiniteLoop(boolean infiniteLoop) {
        this.infiniteLoop = infiniteLoop;
        notifyDataSetChanged();
    }

    /**
     * 获取初始位置
     *
     * @return 初始位置
     */
    public int getStartPosition() {
        if (!infiniteLoop || carouselData.isEmpty()) {
            return 0;
        }
        int middle = (Integer.MAX_VALUE / 2);
        return (middle - middle % carouselData.size());
    }

    /**
     * 设置条目点击监听
     *
     * @param onItemClickListener 条目点击监听
     */
    public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 获取条目布局 ID
     *
     * @return 条目布局 ID
     */
    @LayoutRes
    protected abstract int getItemLayoutId();

    /**
     * 绑定条目
     *
     * @param holder   持有者
     * @param item     条目
     * @param position 位置
     */
    protected abstract void onBindItem(@NonNull CarouselViewHolder holder, @NonNull T item, int position);

    /**
     * 条目点击监听
     *
     * @param <T> T
     */
    public interface OnItemClickListener<T> {
        /**
         * 条目点击
         *
         * @param view     视图
         * @param item     条目
         * @param position 位置
         */
        void onItemClick(View view, T item, int position);
    }

    /**
     * 轮播视图持有者
     */
    public static class CarouselViewHolder extends RecyclerView.ViewHolder {
        public CarouselViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public <V extends View> V findViewById(int id) {
            return itemView.findViewById(id);
        }
    }
}