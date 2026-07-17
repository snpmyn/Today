package widget.carousel.two;

import android.annotation.SuppressLint;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
    private List<T> carouselData;
    /**
     * 条目点击监听
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
        if ((carouselData == null) || carouselData.isEmpty() || (position < 0) || (position >= carouselData.size())) {
            // 数据空防御与安全越界校验
            return;
        }
        // 锁定实体数据包，由于其会被事件闭包消费，强加 final 修饰保障线程安全。
        final T itemData = carouselData.get(position);
        // 将解耦出的 holder 和条目数据上抛给具体页面实现业务填装
        onBindItem(holder, itemData, position);
        // 全局托管整个卡片的最外层原生态单击响应
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(v, itemData, position);
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull CarouselViewHolder holder, int position, @NonNull List<Object> payloads) {
        if ((carouselData == null) || carouselData.isEmpty() || (position < 0) || (position >= carouselData.size())) {
            // 数据空防御与安全越界校验
            return;
        }
        if (!payloads.isEmpty()) {
            // 如果有局部刷新负载
            // 直接分发给子类特殊处理
            onBindItem(holder, carouselData.get(position), position, payloads);
        } else {
            // 正常完整绑定
            onBindViewHolder(holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return (carouselData == null) ? 0 : carouselData.size();
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
     * 设置轮播数据
     * <p>
     * 内外两个数据变量指向同一物理内存地址
     *
     * @param carouselData 轮播数据
     */
    @SuppressLint("NotifyDataSetChanged")
    public void setCarouselData(List<T> carouselData) {
        this.carouselData = carouselData;
        // 首设全量刷新
        notifyDataSetChanged();
    }

    /**
     * 获取条目数据
     *
     * @param position 位置
     * @return 条目数据
     */
    public T getItemData(int position) {
        if ((position < 0) || (position >= carouselData.size())) {
            return null;
        }
        return carouselData.get(position);
    }

    /**
     * 获取条目布局 ID
     * <p>
     * 强迫外部业务子类去声明卡片独创的 XML 布局文件资源 ID
     *
     * @return 条目布局 ID
     */
    @LayoutRes
    protected abstract int getItemLayoutId();

    /**
     * 绑定条目
     * <p>
     * 强迫外部业务子类在其中进行 TextView、ImageView 等数据的加工与渲染填充
     *
     * @param carouselViewHolder 轮播视图持有者
     * @param item               条目
     * @param position           位置
     */
    protected abstract void onBindItem(@NonNull CarouselViewHolder carouselViewHolder, @NonNull T item, int position);

    /**
     * 绑定条目
     * <p>
     * 提供一个空实现重载方法
     * 允许具体子类有选择重写
     * <p>
     * 如果子类 Kotlin 实现
     * 为防止 Kotlin 只读协变 List 映射为字节码后与 Java 不型变泛型不一致导致多态失效、局部刷新降级
     * 必须强制声明为 List<Any>
     * 严格对齐 Java 字节码 List<Object>
     *
     * @param carouselViewHolder 轮播视图持有者
     * @param item               条目
     * @param position           位置
     * @param payloads           局部刷新标识集
     */
    protected void onBindItem(@NonNull CarouselViewHolder carouselViewHolder, @NonNull T item, int position, @NonNull List<Object> payloads) {
        // 子类不重写 -> 降级走全量刷新
        onBindItem(carouselViewHolder, item, position);
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
     * 条目点击监听
     *
     * @param <T> T
     */
    public interface OnItemClickListener<T> {
        /**
         * 条目点击
         *
         * @param view     视图
         * @param itemData 条目数据
         * @param position 位置
         */
        void onItemClick(View view, T itemData, int position);
    }

    /**
     * 轮播视图持有者
     * <p>
     * 高阶缓存复用优化 ViewHolder
     * <p>
     * 内部特制封装了一个基于二进制稀疏排布算法的 SparseArray (比常规 HashMap 节省将近 35% 内存开销)
     * 开发者调用时 holder.findViewById(id) 会优先去稀疏矩阵里低消耗抓取
     * 抓取不到才会去调用原生沉重的底层 itemView.findViewById(id)
     * 从根本上杜绝了轮播图连续高速滑行时卡片因高频 findViewById 导致的系统主线程发生微小瞬时卡顿 / 掉帧问题
     */
    public static class CarouselViewHolder extends RecyclerView.ViewHolder {
        // 控件级别的高速二进制缓存矩阵池
        private final SparseArray<View> viewSparseArray = new SparseArray<>();

        public CarouselViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @SuppressWarnings("unchecked")
        public <V extends View> V findViewById(int id) {
            // 闪电检索内存缓存池
            View view = viewSparseArray.get(id);
            if (view == null) {
                // 实在没有
                // 再去走昂贵的底层树桩反查
                view = itemView.findViewById(id);
                // 极速回填缓存池
                // 保证下一次秒开
                viewSparseArray.put(id, view);
            }
            return (V) view;
        }
    }
}