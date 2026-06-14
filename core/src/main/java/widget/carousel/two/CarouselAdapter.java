package widget.carousel.two;

import android.annotation.SuppressLint;
import android.util.SparseArray;
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
     * 内存核心业务通用泛型数据集
     */
    private final List<T> carouselData = new ArrayList<>();
    /**
     * 对外解耦暴露的条目单击手势回调
     */
    private OnItemClickListener<T> onItemClickListener;

    @NonNull
    @Override
    public CarouselViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 动态加载外部由 getItemLayoutId() 抛出的单张独立卡片 XML 资源布局文件
        View view = LayoutInflater.from(parent.getContext()).inflate(getItemLayoutId(), parent, false);
        return new CarouselViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarouselViewHolder holder, int position) {
        // 数据空防御机制与安全越界校验
        if (carouselData.isEmpty() || (position < 0) || (position >= carouselData.size())) {
            return;
        }
        // 锁定实体数据包，由于其会被事件闭包消费，强加 final 修饰保障线程安全。
        final T item = carouselData.get(position);
        // 将解耦出的 holder 和单条数据上抛给具体的页面实现业务填装
        onBindItem(holder, item, position);
        // 全局托管整个卡片的最外层原生态单击响应
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(v, item, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return carouselData.size();
    }

    /**
     * 获取真实且未经伪造的内存原始数据总量大小
     * <p>
     * 流式非循环模式下与 getItemCount 等价
     *
     * @return 真实且未经伪造的内存原始数据总量大小
     */
    public int getRealCount() {
        return carouselData.size();
    }

    /**
     * 数据管道清洗更新
     * <p>
     * 安全清洗并整盘重写底层内存数据 + 强行同步驱动重绘视图
     *
     * @param data List<T>
     */
    @SuppressLint("NotifyDataSetChanged")
    public void addCarouselData(List<T> data) {
        if (data == null) return;
        this.carouselData.clear();
        this.carouselData.addAll(data);
        // 通知 RecyclerView 全盘刷新重绘
        notifyDataSetChanged();
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
     * 强迫外部业务子类去声明卡片独创的 XML 布局文件资源 ID
     *
     * @return 卡片独创的 XML 布局文件资源 ID
     */
    @LayoutRes
    protected abstract int getItemLayoutId();

    /**
     * 强迫外部业务子类在其中进行 TextView、ImageView 等数据的加工与渲染填充
     *
     * @param holder   CarouselViewHolder
     * @param item     条目
     * @param position 位置
     */
    protected abstract void onBindItem(@NonNull CarouselViewHolder holder, @NonNull T item, int position);

    /**
     * 解耦的卡片通用单击手势接口定义
     *
     * @param <T> T
     */
    public interface OnItemClickListener<T> {
        void onItemClick(View view, T item, int position);
    }

    /**
     * 高阶缓存复用优化 Holder
     * <p>
     * 内部特制封装了一个基于二进制稀疏排布算法的 SparseArray (比常规的 HashMap 节省将近 35% 内存开销)
     * 开发者调用时 holder.findViewById(id) 会优先去稀疏矩阵里低消耗抓取
     * 抓取不到才会去调用原生沉重的底层 itemView.findViewById(id)
     * 从根本上杜绝了轮播图连续高速滑行时
     * 卡片因高频 findViewById 导致的系统主线程发生微小瞬时卡顿 / 掉帧问题
     */
    public static class CarouselViewHolder extends RecyclerView.ViewHolder {
        // 控件级别的高速二进制缓存矩阵池
        private final SparseArray<View> views = new SparseArray<>();

        public CarouselViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @SuppressWarnings("unchecked")
        public <V extends View> V findViewById(int id) {
            // 闪电检索内存缓存池
            View view = views.get(id);
            if (view == null) {
                // 实在没有
                // 再去走昂贵的底层树桩反查
                view = itemView.findViewById(id);
                // 极速回填如缓存池
                // 保证下一次秒开
                views.put(id, view);
            }
            return (V) view;
        }
    }
}