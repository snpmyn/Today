package widget.screen.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.zsp.core.R;

import java.util.List;

import util.list.ListUtils;
import widget.screen.listener.ScreenNestAdapterItemClickListener;

/**
 * Created on 2019/5/24.
 *
 * @author 郑少鹏
 * @desc 筛选嵌套适配器
 */
public class ScreenNestAdapter extends RecyclerView.Adapter<ScreenNestAdapter.ViewHolder> {
    /**
     * 上下文
     */
    private final Context context;
    /**
     * 条件
     */
    private final List<String> conditions;
    /**
     * 单选
     */
    private final boolean singleSelect;
    /**
     * 单选后可反选
     */
    private final boolean canReverseSelectAfterSingleSelect;
    /**
     * 默选下标数据
     */
    private final List<Integer> defaultSelectIndexList;
    /**
     * 互斥
     */
    private final boolean mutuallyExclusive;
    /**
     * 展开 / 折叠、展开 / 折叠主控条件数据
     */
    private final boolean unfoldAndFold;
    private final List<String> unfoldAndFoldActiveControlConditionList;
    /**
     * 类别
     */
    String classification;
    /**
     * 选位
     */
    int selectPosition;
    /**
     * SparseBooleanArray
     */
    SparseBooleanArray sparseBooleanArray;
    /**
     * 筛选嵌套适配器条目短点监听
     */
    private ScreenNestAdapterItemClickListener screenNestAdapterItemClickListener;

    /**
     * constructor
     *
     * @param context                                 上下文
     * @param classification                          类别
     * @param conditions                              条件
     * @param singleSelect                            单选
     * @param canReverseSelectAfterSingleSelect       单选后可反选
     * @param defaultSelectIndexList                  默选下标数据
     * @param mutuallyExclusive                       互斥
     * @param unfoldAndFold                           展开 / 折叠
     * @param unfoldAndFoldActiveControlConditionList 展开 / 折叠主控条件数据
     */
    ScreenNestAdapter(Context context, String classification, List<String> conditions, boolean singleSelect, boolean canReverseSelectAfterSingleSelect, List<Integer> defaultSelectIndexList, boolean mutuallyExclusive, boolean unfoldAndFold, List<String> unfoldAndFoldActiveControlConditionList) {
        // 上下文
        this.context = context;
        // 类别
        this.classification = classification;
        // 条件
        this.conditions = conditions;
        // 单选
        this.singleSelect = singleSelect;
        // 单选后可反选
        this.canReverseSelectAfterSingleSelect = canReverseSelectAfterSingleSelect;
        // 默选下标数据
        this.defaultSelectIndexList = defaultSelectIndexList;
        // 互斥
        this.mutuallyExclusive = mutuallyExclusive;
        // 展开 / 折叠、展开 / 折叠主控条件数据
        this.unfoldAndFold = unfoldAndFold;
        this.unfoldAndFoldActiveControlConditionList = unfoldAndFoldActiveControlConditionList;
        // 选标记
        selectMark();
    }

    /**
     * 设筛选嵌套适配器条目短点监听
     *
     * @param screenNestAdapterItemClickListener 筛选嵌套适配器条目短点监听
     */
    void setScreenNestAdapterItemClickListener(ScreenNestAdapterItemClickListener screenNestAdapterItemClickListener) {
        this.screenNestAdapterItemClickListener = screenNestAdapterItemClickListener;
        defaultSelectValue();
    }

    @SuppressLint("NotifyDataSetChanged")
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.screen_nest_item, viewGroup, false);
        view.setOnClickListener(v -> {
            int position = (Integer) v.getTag();
            String condition = conditions.get(position);
            if (singleSelect) {
                if (selectPosition != position) {
                    selectPosition = position;
                    screenNestAdapterItemClickListener.onItemClick(v, classification, condition, true);
                    // 互斥（仅考虑选中状）
                    if (mutuallyExclusive) {
                        screenNestAdapterItemClickListener.onItemMutuallyExclusiveClick(classification);
                    }
                } else if (canReverseSelectAfterSingleSelect) {
                    selectPosition = -1;
                    screenNestAdapterItemClickListener.onItemClick(v, classification, condition, false);
                }
            } else {
                boolean preSelected = sparseBooleanArray.get(position);
                screenNestAdapterItemClickListener.onItemClick(v, classification, condition, !preSelected);
                sparseBooleanArray.put(position, !preSelected);
                // 互斥（仅考虑选中状）
                if (!preSelected && mutuallyExclusive) {
                    screenNestAdapterItemClickListener.onItemMutuallyExclusiveClick(classification);
                }
            }
            notifyDataSetChanged();
        });
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.itemView.setTag(position);
        String condition = conditions.get(position);
        boolean unfoldAndFoldActiveControlCondition = (ListUtils.listIsNotEmpty(unfoldAndFoldActiveControlConditionList) && unfoldAndFoldActiveControlConditionList.contains(condition));
        // 条件
        holder.screenNestItemTv.setText(condition);
        // 选否
        if (singleSelect) {
            holder.screenNestItemMcv.setChecked(selectPosition == position);
            // 展开 / 折叠（需考虑非 / 选中状）
            if (unfoldAndFold && unfoldAndFoldActiveControlCondition) {
                screenNestAdapterItemClickListener.onItemUnfoldAndFoldClick(classification, condition, selectPosition == position);
            }
        } else {
            holder.screenNestItemMcv.setChecked(sparseBooleanArray.get(position));
            // 展开 / 折叠（需考虑非 / 选中状）
            if (unfoldAndFold && unfoldAndFoldActiveControlCondition) {
                screenNestAdapterItemClickListener.onItemUnfoldAndFoldClick(classification, condition, sparseBooleanArray.get(position));
            }
        }
    }

    /**
     * 选标记
     */
    private void selectMark() {
        selectPosition = -1;
        if (null == sparseBooleanArray) {
            sparseBooleanArray = new SparseBooleanArray();
        }
        if (sparseBooleanArray.size() > 0) {
            sparseBooleanArray.clear();
        }
        // 无默选
        if (ListUtils.listIsEmpty(defaultSelectIndexList)) {
            return;
        }
        // 有默选
        if (singleSelect) {
            selectPosition = (defaultSelectIndexList.get(defaultSelectIndexList.size() - 1));
            return;
        }
        for (Integer integer : defaultSelectIndexList) {
            sparseBooleanArray.append(integer, true);
        }
    }

    /**
     * 默选值
     */
    private void defaultSelectValue() {
        if (ListUtils.listIsEmpty(defaultSelectIndexList)) {
            return;
        }
        if (singleSelect) {
            screenNestAdapterItemClickListener.onItemClick(null, classification, conditions.get(defaultSelectIndexList.get(defaultSelectIndexList.size() - 1)), true);
            return;
        }
        for (Integer integer : defaultSelectIndexList) {
            screenNestAdapterItemClickListener.onItemClick(null, classification, conditions.get(integer), true);
        }
    }

    /**
     * 普通重置
     * <p>
     * 场景一：左上角重置。
     * 场景二：互斥场景选中类别外类别重置。
     */
    @SuppressLint("NotifyDataSetChanged")
    void reset() {
        selectMark();
        notifyDataSetChanged();
        defaultSelectValue();
        if (mutuallyExclusive || unfoldAndFold) {
            screenNestAdapterItemClickListener.onItemClick(null, classification, null, false);
        }
    }

    @Override
    public int getItemCount() {
        if (ListUtils.listIsNotEmpty(conditions)) {
            return conditions.size();
        }
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView screenNestItemMcv;
        private final TextView screenNestItemTv;

        private ViewHolder(@NonNull View view) {
            super(view);
            screenNestItemTv = view.findViewById(R.id.screenNestItemTv);
            screenNestItemMcv = view.findViewById(R.id.screenNestItemMcv);
        }
    }
}