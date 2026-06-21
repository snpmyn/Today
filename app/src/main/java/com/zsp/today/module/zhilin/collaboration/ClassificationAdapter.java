package com.zsp.today.module.zhilin.collaboration;

import android.annotation.SuppressLint;
import android.content.Context;
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
import java.util.Objects;

import widget.recyclerview.controller.RecyclerViewScrollController;

/**
 * @decs: 分类适配器
 * @author: 郑少鹏
 * @date: 2026/4/24 19:13
 * @version: v 1.0
 */
public class ClassificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    /**
     * 上下文
     */
    private final Context context;
    /**
     * 列表条目集
     */
    private final List<ListItem> listItemList = new ArrayList<>();
    /**
     * 子条目选择状态集
     */
    private final Map<Integer, Boolean> childItemSelectStateMap = new LinkedHashMap<>();
    /**
     * 子条目选择数量集
     */
    private final Map<Integer, Integer> childItemSelectCountMap = new LinkedHashMap<>();
    /**
     * 子条目分类集
     */
    private final Map<Integer, List<ChildItem>> childItemClassificationMap = new LinkedHashMap<>();
    /**
     * RecyclerView
     */
    private RecyclerView recyclerView;
    /**
     * 选择监听
     */
    private OnSelectListener onSelectListener;
    /**
     * 默认展开识别状态
     * <p>
     * 默认无展开
     */
    private int defaultExpandRecognizeState = -1;
    /**
     * 子条目是否可点
     * <p>
     * 默认可点
     */
    private boolean childItemClickEnable = true;
    /**
     * 当前已选学生 ID
     * <p>
     * 默认无学生选中
     */
    private int currentSelectedStudentId = -1;
    /**
     * RecyclerViewScrollController
     */
    private RecyclerViewScrollController recyclerViewScrollController;

    /**
     * constructor
     *
     * @param context 上下文
     */
    public ClassificationAdapter(Context context) {
        this.context = context;
    }

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
     * <p>
     * 第一步 - 先 setData(List, int, boolean)
     * 此时 Adapter 还没 attach 到 {@link RecyclerView}
     * 虽然 adapter.mObservable.notifyChanged() 会发通知
     * 但因没有 observer (RecyclerView 还没注册监听)
     * 故不会触发 {@link #onCreateViewHolder(ViewGroup, int)} + {@link #onBindViewHolder(RecyclerView.ViewHolder, int)}
     * 不会 layout
     * <p>
     * 第二步 - 再 {@link RecyclerView#setAdapter(RecyclerView.Adapter)}
     * 这时 {@link RecyclerView} attach adapter
     * 立刻读取数据
     * 执行 {@link #getItemCount()} + {@link #onCreateViewHolder(ViewGroup, int)} + {@link #onBindViewHolder(RecyclerView.ViewHolder, int)}
     *
     * @param studentBeanList             学生数据集
     * @param defaultExpandRecognizeState 默认展开识别状态
     *                                    默认无展开
     * @param switchExamPaper             是否切换试卷
     */
    public void setData(List<StudentBean> studentBeanList, int defaultExpandRecognizeState, boolean switchExamPaper) {
        if (null == studentBeanList) {
            // 默认显示
            defaultShow();
            return;
        }
        clear(switchExamPaper);
        this.defaultExpandRecognizeState = defaultExpandRecognizeState;
        for (StudentBean studentBean : studentBeanList) {
            // putIfAbsent
            // 不存在添加 + 存在无操作
            childItemSelectStateMap.putIfAbsent(studentBean.getStudentId(), false);
            // clear 中已预存放三个桶
            Objects.requireNonNull(childItemClassificationMap.get(studentBean.getRecognizeState())).add(new ChildItem(studentBean));
        }
        // 构建并通知
        buildAndNotify();
    }

    /**
     * 默认显示
     * <p>
     * 默认显示三个头条目
     */
    public void defaultShow() {
        clear(true);
        defaultExpandRecognizeState = -1;
        buildAndNotify();
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        if (viewType == ListItem.TYPE_HEADER) {
            return new HeaderViewHolder(layoutInflater.inflate(R.layout.item_classification_head, parent, false));
        } else {
            return new ChildViewHolder(layoutInflater.inflate(R.layout.item_classification_child, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        ListItem listItem = listItemList.get(position);
        if (holder instanceof HeaderViewHolder) {
            HeadItem headItem = (HeadItem) listItem;
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            // 头条目名称
            headerViewHolder.classificationHeadItemTv.setText(headItem.getTitle());
            // 展开
            // 1. 默认展开识别状态 = 当前头条目识别状态
            // 2. 当前头条目下有子条目
            /*boolean expand = (defaultExpandRecognizeState == headItem.recognizeState) && (!Objects.requireNonNull(childItemClassificationMap.get(headItem.recognizeState)).isEmpty());*/
            // 头条目图标
            /*headerViewHolder.classificationHeadItemAciv.setRotation(expand ? 180F : 0F);*/
            // 头条目点击
            headerViewHolder.itemView.setOnClickListener(v -> {
                // 场景一
                // 头条目 A 展开 + 点击头条目 A
                // defaultExpandRecognizeState = headerItem.recognizeState
                // 最终 defaultExpandRecognizeState = -1
                // 场景二
                // 头条目 A 展开 + 点击头条目 B
                // defaultExpandRecognizeState != headerItem.recognizeState
                // 最终 defaultExpandRecognizeState = headItem.recognizeState
                defaultExpandRecognizeState = (defaultExpandRecognizeState == headItem.recognizeState ? -1 : headItem.recognizeState);
                /*if (defaultExpandRecognizeState == -1) {
                    // 折叠
                    AnimationManager.rotation(headerViewHolder.classificationHeadItemAciv, 300, 180F, 0F, null);
                } else {
                    // 展开
                    AnimationManager.rotation(headerViewHolder.classificationHeadItemAciv, 300, 0F, 180F, null);
                }*/
                // 构建并通知
                buildAndNotify();
            });
        } else {
            ChildItem childItem = (ChildItem) listItem;
            StudentBean studentBean = childItem.studentBean;
            ChildViewHolder childViewHolder = (ChildViewHolder) holder;
            // 已选
            /*boolean selected = Boolean.TRUE.equals(childItemSelectStateMap.get(studentBean.getStudentId()));*/
            // 子条目名称
            /*childViewHolder.itemView.setBackground(ContextCompat.getDrawable(childViewHolder.itemView.getContext(), selected ? R.drawable.shape_stu_checked : R.drawable.shape_stu_uncheck));*/
            // 子条目图标
            /*childViewHolder.ivDui.setVisibility(selected ? View.VISIBLE : View.GONE);*/
            // 子条目选择数量
            Integer integer = childItemSelectCountMap.get(studentBean.getStudentId());
            if (null == integer) {
                childViewHolder.classificationChildItemTv.setText(studentBean.getStudentName());
            } else {
                if (integer > 0) {
                    childViewHolder.classificationChildItemTv.setText(String.format(context.getString(R.string.formatSdStudentCount), studentBean.getStudentName(), integer));
                } else {
                    childViewHolder.classificationChildItemTv.setText(studentBean.getStudentName());
                }
            }
            // 子条目点击
            childViewHolder.itemView.setOnClickListener(v -> {
                if (!childItemClickEnable) {
                    // 子条目不可点 + 直接 return
                    return;
                }
                if (Boolean.TRUE.equals(childItemSelectStateMap.get(studentBean.getStudentId()))) {
                    // 子条目选择数量
                    maintainChildItemSelectCountMap(studentBean.getStudentId());
                    // 取消选中
                    childItemSelectStateMap.put(studentBean.getStudentId(), false);
                    currentSelectedStudentId = -1;
                    // 子条目局部刷新
                    notifyItemChanged(childViewHolder);
                    if (null != onSelectListener) {
                        onSelectListener.onUnSelect(studentBean, -1);
                    }
                } else {
                    // 旧子条目选择数量
                    maintainChildItemSelectCountMap(currentSelectedStudentId);
                    // 新子条目选择数量
                    // 新子条目已选择过 / 已存在 -> 增加 1
                    // 新子条目初次选择 / 不存在 -> 设为 1
                    childItemSelectCountMap.merge(studentBean.getStudentId(), 1, Integer::sum);
                    // 清旧 + 选新
                    if (currentSelectedStudentId != -1) {
                        childItemSelectStateMap.put(currentSelectedStudentId, false);
                    }
                    childItemSelectStateMap.put(studentBean.getStudentId(), true);
                    int oldChildItemPosition = findPositionByStudentId(currentSelectedStudentId);
                    currentSelectedStudentId = studentBean.getStudentId();
                    // 旧子条目局部刷新
                    if (oldChildItemPosition != -1) {
                        notifyItemChanged(oldChildItemPosition);
                    }
                    // 新子条目局部刷新
                    notifyItemChanged(childViewHolder);
                    if (null != onSelectListener) {
                        onSelectListener.onSelect(studentBean, -1);
                    }
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
     * 清空
     * <p>
     * 场景一 - 切换试卷
     * 不同试卷可选相同学生
     * 需 clear(boolean) 中 currentSelectedStudentId = -1
     * 隔离不同试卷场景
     * 否则切换下张试卷时会清空上张试卷已选学生数量
     * 无法实现相同学生数量累加
     * <p>
     * 场景二 - 切换班级
     * 基于一张试卷
     * 可切换不同班级 + 仅可选一个学生
     * 无需 clear(boolean) 中 currentSelectedStudentId = -1
     * 全局共享 currentSelectedStudentId
     * 这样切换不同班级时可减掉上个班级中已选学生数量
     *
     * @param switchExamPaper 是否切换试卷
     */
    private void clear(boolean switchExamPaper) {
        // 清空子条目选择状态集
        childItemSelectStateMap.clear();
        // 清空子条目分类集
        childItemClassificationMap.clear();
        // 预先存放三个桶
        // 不然在只有某一类数据场景下只显示该类头条目
        childItemClassificationMap.put(0, new ArrayList<>());
        childItemClassificationMap.put(1, new ArrayList<>());
        childItemClassificationMap.put(2, new ArrayList<>());
        // 重置当前已选学生 ID
        if (switchExamPaper) {
            currentSelectedStudentId = -1;
        }
    }

    /**
     * 构建数据
     * <p>
     * {@link RecyclerView} 在 layout / measure 阶段会主动读取 Adapter 数据
     * - {@link #getItemCount()}
     * - {@link #onBindViewHolder(RecyclerView.ViewHolder, int)}
     * <p>
     * buildData() 会直接修改 {@link #listItemList} 数据源
     * <p>
     * {@link RecyclerView} 并不会感知数据变化
     * 因为没有调用 {@link #notifyDataSetChanged()}
     * 如果此时 {@link RecyclerView} 正在执行 layout 流程 onMeasure → onLayout → onBindViewHolder
     * 就会出现
     * - RecyclerView 使用旧状态 itemCount / position
     * - Adapter 使用新数据 {@link #listItemList}
     * 导致数据与 UI 状态不一致 -> 可能进入死循环 -> 从而 ANR
     * <p>
     * 因此 buildData() 后必须立刻调用 {@link #notifyDataSetChanged()}
     * <p>
     * 不能延迟到 OnGlobalLayoutListener 中再刷新
     * 因为 OnGlobalLayoutListener 触发时
     * RecyclerView 很可能已经完成或正在进行 layout
     * 此时状态已经异常 + 刷新为时已晚 + 可能导致 ANR
     */
    private void buildData() {
        // 清空列表条目集
        if (!listItemList.isEmpty()) {
            listItemList.clear();
        }
        // 后端返回数据使用 0、1、2 作为三种识别状态的值
        for (int i = 0; i <= 2; i++) {
            List<ChildItem> childItemList = childItemClassificationMap.get(i);
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
            headItem.needExpand = (i == defaultExpandRecognizeState);
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
    @SuppressLint("NotifyDataSetChanged")
    public void focus(int studentId, boolean autoSelect) {
        if (null == recyclerView) {
            return;
        }
        recyclerView.post(() -> {
            // 需要展开识别状态
            int needExpandRecognizeState = -1;
            // 后端返回数据使用 0、1、2 作为三种识别状态的值
            for (int i = 0; i <= 2; i++) {
                List<ChildItem> childItemList = childItemClassificationMap.get(i);
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
            if (needExpandRecognizeState == -1) {
                // 上一步查询全部数据后没有匹配到 studentId
                // 没有查询到需要展开识别状态 + 直接 return
                return;
            }
            // 需要展开识别状态赋值给默认展开识别状态
            defaultExpandRecognizeState = needExpandRecognizeState;
            // 自动选择
            if (autoSelect) {
                childItemSelectStateMap.put(studentId, true);
                currentSelectedStudentId = studentId;
            }
            // 构建数据
            buildData();
            // 全量刷新
            notifyDataSetChanged();
            // 子位置
            int childPosition = findPositionByStudentId(studentId);
            if (childPosition != -1) {
                if (null == recyclerViewScrollController) {
                    recyclerViewScrollController = new RecyclerViewScrollController();
                }
                // 查询到对应 studentId 的子位置 + 滚动居中
                recyclerViewScrollController.itemScrollToCenterInVertical(recyclerView, childPosition, true);
            }
        });
    }

    /**
     * 清除当前选中
     * <p>
     * 保留历史
     */
    @SuppressLint("NotifyDataSetChanged")
    public void clearSelect() {
        if (currentSelectedStudentId == -1) {
            return;
        }
        // 子条目选择数量
        maintainChildItemSelectCountMap(currentSelectedStudentId);
        // 旧的已选学生 ID
        int oldSelectedStudentId = currentSelectedStudentId;
        childItemSelectStateMap.put(oldSelectedStudentId, false);
        currentSelectedStudentId = -1;
        notifyDataSetChanged();
        if (null != onSelectListener) {
            onSelectListener.onUnSelect(findStudentBean(oldSelectedStudentId), -1);
        }
    }

    /**
     * 构建并通知
     * <p>
     * {@link RecyclerView#post(Runnable)} 避免
     * 1. onLayout 中间改数据
     * 2. RecyclerView 状态错乱
     */
    @SuppressLint("NotifyDataSetChanged")
    private void buildAndNotify() {
        if (null != recyclerView) {
            recyclerView.post(() -> {
                // 原子操作
                // buildData() + notifyDataSetChanged()
                buildData();
                notifyDataSetChanged();
            });
        } else {
            // 原子操作
            // buildData() + notifyDataSetChanged()
            buildData();
            notifyDataSetChanged();
        }
    }

    /**
     * 通知条目改变
     *
     * @param viewHolder RecyclerView.ViewHolder
     */
    private void notifyItemChanged(@NonNull RecyclerView.ViewHolder viewHolder) {
        int position = viewHolder.getBindingAdapterPosition();
        if (position != RecyclerView.NO_POSITION) {
            notifyItemChanged(position);
        }
    }

    /**
     * 维护子条目选择数量集
     *
     * @param studentId 学生 ID
     */
    private void maintainChildItemSelectCountMap(int studentId) {
        if (childItemSelectCountMap.containsKey(studentId)) {
            Integer childItemSelectCount = childItemSelectCountMap.get(studentId);
            if ((null != childItemSelectCount) && (childItemSelectCount > 0)) {
                childItemSelectCountMap.put(studentId, childItemSelectCount - 1);
            }
        }
    }

    /**
     * 查询学生数据
     *
     * @param studentId 学生 ID
     * @return 学生数据
     */
    @Nullable
    private StudentBean findStudentBean(int studentId) {
        for (List<ChildItem> childItemList : childItemClassificationMap.values()) {
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
     * 子条目是否可点
     *
     * @return 子条目是否可点
     */
    public boolean isChildItemClickEnable() {
        return childItemClickEnable;
    }

    /**
     * 设置子条目是否可点
     *
     * @param childItemClickEnable 子条目是否可点
     */
    public void setChildItemClickEnable(boolean childItemClickEnable) {
        this.childItemClickEnable = childItemClickEnable;
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
         * <p>
         * 使用场景中 position 不稳定
         * 故而统一传 -1
         *
         * @param studentBean 学生数据
         * @param position    位置
         */
        void onSelect(StudentBean studentBean, int position);

        /**
         * 取消选中
         * <p>
         * 使用场景中 position 不稳定
         * 故而统一传 -1
         *
         * @param studentBean 学生数据
         * @param position    位置
         */
        void onUnSelect(StudentBean studentBean, int position);
    }

    /**
     * 头 ViewHolder
     */
    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView classificationHeadItemTv;
        MaterialCardView classificationHeadItemMcv;

        public HeaderViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            classificationHeadItemTv = itemView.findViewById(R.id.classificationHeadItemTv);
            classificationHeadItemMcv = itemView.findViewById(R.id.classificationHeadItemMcv);
        }
    }

    /**
     * 子 ViewHolder
     */
    static class ChildViewHolder extends RecyclerView.ViewHolder {
        TextView classificationChildItemTv;
        MaterialCardView classificationChildItemMcv;

        public ChildViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            classificationChildItemTv = itemView.findViewById(R.id.classificationChildItemTv);
            classificationChildItemMcv = itemView.findViewById(R.id.classificationChildItemMcv);
        }
    }
}