package com.zsp.today.module.zhilin.collaboration;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.zsp.today.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @decs: 分类适配器
 * @author: 郑少鹏
 * @date: 2026/4/24 19:13
 * @version: v 1.0
 */
public class ClassificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    /**
     * 列表条目集
     */
    private final List<ListItem> listItemList = new ArrayList<>();
    /**
     * 分类子条目集
     */
    private final Map<Integer, List<ChildItem>> classificationChildItemMap = new LinkedHashMap<>();
    /**
     * RecyclerView
     */
    private RecyclerView recyclerView;
    /**
     * 选择监听
     */
    private OnSelectListener onSelectListener;
    /**
     * 当前已选学生 ID
     * <p>
     * 默认无学生选中
     */
    private int currentSelectedStudentId = -1;
    /**
     * 子条目是否可点
     * <p>
     * 默认可点
     */
    private boolean childItemClickEnable = true;
    /**
     * 默认展开识别状态
     * <p>
     * 默认无展开
     */
    private int defaultExpandRecognizeState = -1;

    /**
     * 绑定 RecyclerView
     *
     * @param recyclerView RecyclerView
     */
    public void attachRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    /**
     * 设置数据
     *
     * @param studentBeanList             学生数据集
     * @param defaultExpandRecognizeState 默认展开识别状态
     *                                    默认无展开
     */
    @SuppressLint("NotifyDataSetChanged")
    public void setData(@NonNull List<StudentBean> studentBeanList, int defaultExpandRecognizeState) {
        this.defaultExpandRecognizeState = defaultExpandRecognizeState;
        for (StudentBean studentBean : studentBeanList) {
            // putIfAbsent
            // recognizeState 不存在时放进去 + 已存在则什么都不做
            classificationChildItemMap.computeIfAbsent(studentBean.getRecognizeState(), k -> new ArrayList<>()).add(new ChildItem(studentBean));
        }
        rebuild();
        // 全量刷新
        notifyDataSetChanged();
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        if (viewType == ListItem.TYPE_HEADER) {
            return new HeadViewHolder(layoutInflater.inflate(R.layout.item_classification_header, parent, false));
        } else {
            return new ChildViewHolder(layoutInflater.inflate(R.layout.item_classification_child, parent, false));
        }
    }

    /**
     * onBindViewHolder
     * <p>
     * {@link #onBindViewHolder} 中的变量
     * 渲染时快照
     * 非实时数据
     * <p>
     * 这些局部变量（如 select）在 {@link #onBindViewHolder} 时计算
     * 属于该时刻的数据快照
     * <p>
     * 在局部刷新 - notifyItemChanged 场景
     * 数据可能已发生变化（例如 currentSelectedStudentId 改变）
     * 但这些局部变量不会自动更新
     * 除非指定条目重新触发 {@link #onBindViewHolder}
     * <p>
     * 因此在点击事件中
     * 不要依赖这些旧变量
     * 应重新基于最新数据计算状态
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ListItem listItem = listItemList.get(position);
        if (holder instanceof HeadViewHolder) {
            HeadItem headItem = (HeadItem) listItem;
            HeadViewHolder headViewHolder = (HeadViewHolder) holder;
            headViewHolder.classificationHeadItemTv.setText(headItem.getTitle());
            headViewHolder.classificationHeadItemMcv.setChecked(defaultExpandRecognizeState == headItem.recognizeState);
            headViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onClick(View v) {
                    // 场景一
                    // 头条目 A 展开 + 点击头条目 A
                    // defaultExpandRecognizeState = headerItem.recognizeState
                    // 场景二
                    // 头条目 A 展开 + 点击头条目 B
                    // defaultExpandRecognizeState != headerItem.recognizeState
                    defaultExpandRecognizeState = (defaultExpandRecognizeState == headItem.recognizeState ? -1 : headItem.recognizeState);
                    // 重建
                    rebuild();
                    // 全量刷新
                    // TODO: 2026/4/24 notifyDataSetChanged() 待优化
                    notifyDataSetChanged();
                }
            });
        } else if (holder instanceof ChildViewHolder) {
            ChildItem childItem = (ChildItem) listItem;
            StudentBean studentBean = childItem.studentBean;
            ChildViewHolder childViewHolder = (ChildViewHolder) holder;
            childViewHolder.classificationChildItemTv.setText(studentBean.getStudentName());
            childViewHolder.classificationChildItemMcv.setChecked(studentBean.getStudentId() == currentSelectedStudentId);
            childViewHolder.itemView.setOnClickListener(v -> {
                if (!childItemClickEnable) {
                    // 子条目不可点 + 直接 return
                    return;
                }
                if (studentBean.getStudentId() == currentSelectedStudentId) {
                    // 取消选中
                    currentSelectedStudentId = -1;
                    notifyItemChanged(childViewHolder.getBindingAdapterPosition());
                    if (null != onSelectListener) {
                        onSelectListener.onUnSelect(studentBean, childViewHolder.getBindingAdapterPosition());
                    }
                    return;
                }
                // 子条目旧位置
                int oldChildItemPosition = findPositionByStudentId(currentSelectedStudentId);
                // 局部刷新子条目旧位置
                if (oldChildItemPosition != -1) {
                    notifyItemChanged(oldChildItemPosition);
                }
                // 局部刷新子条目新位置
                notifyItemChanged(childViewHolder.getBindingAdapterPosition());
                // 当前已选学生 ID 赋值为当前学生 ID
                currentSelectedStudentId = studentBean.getStudentId();
                if (null != onSelectListener) {
                    onSelectListener.onSelect(studentBean, childViewHolder.getBindingAdapterPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listItemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return listItemList.get(position).getType();
    }

    /**
     * 重建
     * <p>
     * 三种场景下调用
     * <p>
     * 第一
     * 设置数据
     * {@link #setData(List, int)}
     * <p>
     * 第二
     * 点同一头条目 -> 展开 / 折叠
     * {@link #onBindViewHolder(RecyclerView.ViewHolder, int)}
     * <p>
     * 第三
     * 类目切换
     * {@link #focus(int, boolean)}
     */
    private void rebuild() {
        listItemList.clear();
        // 后端返回数据使用 0、1、2 作为三种识别状态的值
        for (int i = 0; i <= 2; i++) {
            List<ChildItem> childItemList = classificationChildItemMap.get(i);
            if (null == childItemList) {
                continue;
            }
            // 上一步查询三种识别状态
            // 查询到识别状态下存在子条目时
            // 为该组子条目创建一个头条目
            HeadItem headItem = new HeadItem(i);
            // 头条目显示数量 = 头条目下子条目数量
            headItem.count = childItemList.size();
            // 默认展开识别状态 = 当前识别状态
            // 判定需要展开
            headItem.needExpand = (defaultExpandRecognizeState == i);
            // 自上而下依次排版
            // 先添加头条目
            listItemList.add(headItem);
            if (headItem.needExpand) {
                // 需要展开
                // 后添加子条目
                listItemList.addAll(childItemList);
            }
        }
    }

    /**
     * 聚焦
     *
     * @param studentId  学生 ID
     * @param autoSelect 自动选择
     */
    public void focus(int studentId, boolean autoSelect) {
        if (null == recyclerView) {
            return;
        }
        recyclerView.post(new Runnable() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void run() {
                // 需要展开识别状态
                int needExpandRecognizeState = -1;
                // 后端返回数据使用 0、1、2 作为三种识别状态的值
                for (int i = 0; i <= 2; i++) {
                    List<ChildItem> childItemList = classificationChildItemMap.get(i);
                    if (null == childItemList) {
                        continue;
                    }
                    for (ChildItem childItem : childItemList) {
                        if (childItem.studentBean.getStudentId() == studentId) {
                            // 某子条目匹配到 studentId
                            // 重复 studentId 场景下使用最前面的
                            // 赋值需要展开识别状态 + 结束子条目循环
                            needExpandRecognizeState = i;
                            break;
                        }
                    }
                    if (needExpandRecognizeState != -1) {
                        // 重复 studentId 场景下使用最前面的
                        // 需要展开识别状态已赋值 + 结束头条目循环
                        break;
                    }
                }
                // 上一步查询全部数据后没有匹配到 studentId
                // 没有查询到需要展开识别状态 + 直接 return
                if (needExpandRecognizeState == -1) {
                    return;
                }
                // 头条目是否需要更新
                boolean headItemNeedUpdate = defaultExpandRecognizeState != needExpandRecognizeState;
                // 子条目是否需要更新
                boolean childItemNeedUpdate = (autoSelect && (currentSelectedStudentId != studentId));

                // 旧的已选学生 ID
                int oldSelectedStudentId = currentSelectedStudentId;
                if (childItemNeedUpdate) {
                    // 自动选择
                    // 先改数据 + 暂不刷新
                    currentSelectedStudentId = studentId;
                }

                if (!headItemNeedUpdate && !childItemNeedUpdate) {
                    // 头条目无需更新 + 子条目无需更新
                    return;
                }

                if (headItemNeedUpdate) {
                    // 头条目需更新
                    // 需要展开识别状态赋值给默认展开识别状态
                    defaultExpandRecognizeState = needExpandRecognizeState;
                    // 重建
                    rebuild();
                    // 全量刷新
                    // TODO: 2026/4/24 notifyDataSetChanged() 待优化
                    notifyDataSetChanged();
                } else {
                    // 头条目无需更新 + 子条目需更新
                    // 局部刷新旧子条目
                    int oldChildItemPosition = findPositionByStudentId(oldSelectedStudentId);
                    if (oldChildItemPosition != -1) {
                        notifyItemChanged(oldChildItemPosition);
                    }
                    // 局部刷新新子条目
                    int newChildItemPosition = findPositionByStudentId(currentSelectedStudentId);
                    if (newChildItemPosition != -1) {
                        notifyItemChanged(newChildItemPosition);
                    }
                }
                recyclerView.post(() -> {
                    // 子位置
                    int childPosition = findPositionByStudentId(studentId);
                    if (childPosition != -1) {
                        // 查询到对应 studentId 的子位置 + 滚动居中
                        recyclerView.scrollToPosition(childPosition);
                    }
                });
            }
        });
    }

    /**
     * 清除当前选中
     */
    public void clearSelect() {
        if (currentSelectedStudentId == -1) {
            return;
        }
        // 旧的已选学生 ID
        int oldSelectedStudentId = currentSelectedStudentId;
        // 重置当前已选学生 ID
        currentSelectedStudentId = -1;
        // 子旧位置
        int oldChildPosition = findPositionByStudentId(oldSelectedStudentId);
        // 局部刷新子旧位置
        if (oldChildPosition != -1) {
            notifyItemChanged(oldChildPosition);
        }
        if (null != onSelectListener) {
            onSelectListener.onUnSelect(findStudentBeanByStudentId(oldSelectedStudentId), oldChildPosition);
        }
    }

    /**
     * 设置子条目是否可点
     *
     * @param enable 是否允许
     */
    public void setChildItemClickEnable(boolean enable) {
        this.childItemClickEnable = enable;
    }

    /**
     * 通过 StudentId 查询学生数据
     *
     * @param studentId 学生 ID
     * @return 学生数据
     */
    @Nullable
    private StudentBean findStudentBeanByStudentId(int studentId) {
        for (List<ChildItem> childItemList : classificationChildItemMap.values()) {
            for (ChildItem childItem : childItemList) {
                if (childItem.studentBean.getStudentId() == studentId) {
                    return childItem.studentBean;
                }
            }
        }
        return null;
    }

    /**
     * 通过 StudentId 查询位置
     *
     * @param studentId 学生 ID
     * @return 位置
     */
    private int findPositionByStudentId(int studentId) {
        if (studentId == -1) {
            return -1;
        }
        for (int i = 0; i < listItemList.size(); i++) {
            ListItem listItem = listItemList.get(i);
            if (listItem instanceof ChildItem) {
                if (((ChildItem) listItem).studentBean.getStudentId() == studentId) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * 设置选择监听
     *
     * @param onSelectListener 选择监听
     */
    public void setOnSelectListener(OnSelectListener onSelectListener) {
        this.onSelectListener = onSelectListener;
    }

    /**
     * 选择监听
     */
    public interface OnSelectListener {
        /**
         * 选中
         *
         * @param studentBean 学生数据
         * @param position    位置
         */
        void onSelect(StudentBean studentBean, int position);

        /**
         * 取消选中
         *
         * @param studentBean 学生数据
         * @param position    位置
         */
        void onUnSelect(StudentBean studentBean, int position);
    }

    /**
     * 头 ViewHolder
     */
    static class HeadViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView classificationHeadItemMcv;
        TextView classificationHeadItemTv;

        public HeadViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            classificationHeadItemMcv = itemView.findViewById(R.id.classificationHeadItemMcv);
            classificationHeadItemTv = itemView.findViewById(R.id.classificationHeadItemTv);
        }
    }

    /**
     * 子 ViewHolder
     */
    static class ChildViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView classificationChildItemMcv;
        TextView classificationChildItemTv;

        public ChildViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            classificationChildItemMcv = itemView.findViewById(R.id.classificationChildItemMcv);
            classificationChildItemTv = itemView.findViewById(R.id.classificationChildItemTv);
        }
    }
}