package widget.screen.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.zsp.core.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import util.list.ListUtils;
import util.view.ViewUtils;
import widget.recyclerview.configure.RecyclerViewConfigure;
import widget.recyclerview.controller.RecyclerViewDisplayController;
import widget.screen.bean.MutuallyExclusiveBean;
import widget.screen.bean.UnfoldAndFoldBean;
import widget.screen.listener.ScreenAdapterItemClickListener;
import widget.screen.listener.ScreenNestAdapterItemClickListener;

/**
 * Created on 2019/5/24.
 *
 * @author 郑少鹏
 * @desc 筛选适配器
 */
public class ScreenAdapter extends RecyclerView.Adapter<ScreenAdapter.ViewHolder> {
    /**
     * 上下文
     */
    private final Context context;
    /**
     * 条目视图数据
     */
    private final Map<String, View> itemViewMap;
    /**
     * 主体数据键值数据
     */
    private List<List<String>> subjectMapKeyList;
    private List<Map<Integer, Boolean>> subjectMapValueList;
    /**
     * 单选后可反选数据
     */
    private List<String> canReverseSelectAfterSingleSelectList;
    /**
     * 默选数据、默选数据键数据
     */
    private Map<String, List<String>> defaultSelectMap;
    private List<String> defaultSelectMapKeyList;
    /**
     * 互斥数据、互斥数据类别数据
     */
    private List<MutuallyExclusiveBean> mutuallyExclusiveBeanList;
    private List<String> mutuallyExclusiveBeanListClassificationList;
    /**
     * 展开 / 折叠数据、展开 / 折叠数据主控类别数据、展开 / 折叠数据被控类别数据
     */
    private List<UnfoldAndFoldBean> unfoldAndFoldBeanList;
    private List<String> unfoldAndFoldBeanListActiveControlClassificationList;
    private List<String> unfoldAndFoldBeanListPassiveControlClassificationList;
    /**
     * 筛选嵌套适配器数据
     */
    private List<ScreenNestAdapter> screenNestAdapterList;
    /**
     * 筛选适配器条目短点监听
     */
    private ScreenAdapterItemClickListener screenAdapterItemClickListener;

    /**
     * constructor
     *
     * @param context 上下文
     */
    public ScreenAdapter(Context context) {
        this.context = context;
        this.itemViewMap = new LinkedHashMap<>();
    }

    /**
     * 设筛选数据
     *
     * @param subjectMap                                            主体数据
     * @param canReverseSelectAfterSingleSelectList                 单选后可反选数据
     * @param defaultSelectMap                                      默选数据
     * @param mutuallyExclusiveBeanList                             互斥数据
     * @param mutuallyExclusiveBeanListClassificationList           互斥数据类别数据
     * @param unfoldAndFoldBeanList                                 展开 / 折叠数据
     * @param unfoldAndFoldBeanListActiveControlClassificationList  展开 / 折叠数据主控类别数据
     * @param unfoldAndFoldBeanListPassiveControlClassificationList 展开 / 折叠数据被控类别数据
     */
    public void setScreeningData(@NonNull Map<List<String>, Map<Integer, Boolean>> subjectMap, List<String> canReverseSelectAfterSingleSelectList, @NonNull Map<String, List<String>> defaultSelectMap, List<MutuallyExclusiveBean> mutuallyExclusiveBeanList, List<String> mutuallyExclusiveBeanListClassificationList, List<UnfoldAndFoldBean> unfoldAndFoldBeanList, List<String> unfoldAndFoldBeanListActiveControlClassificationList, List<String> unfoldAndFoldBeanListPassiveControlClassificationList) {
        // 主体数据键值数据
        this.subjectMapKeyList = new ArrayList<>(subjectMap.keySet());
        this.subjectMapValueList = new ArrayList<>(subjectMap.values());
        // 单选后可反选数据
        this.canReverseSelectAfterSingleSelectList = canReverseSelectAfterSingleSelectList;
        // 默选数据、默选数据键数据
        this.defaultSelectMap = defaultSelectMap;
        this.defaultSelectMapKeyList = new ArrayList<>(defaultSelectMap.keySet());
        // 互斥数据、互斥数据类别数据
        this.mutuallyExclusiveBeanList = mutuallyExclusiveBeanList;
        this.mutuallyExclusiveBeanListClassificationList = mutuallyExclusiveBeanListClassificationList;
        // 展开 / 折叠数据、展开 / 折叠数据主控类别数据、展开 / 折叠数据被控类别数据
        this.unfoldAndFoldBeanList = unfoldAndFoldBeanList;
        this.unfoldAndFoldBeanListActiveControlClassificationList = unfoldAndFoldBeanListActiveControlClassificationList;
        this.unfoldAndFoldBeanListPassiveControlClassificationList = unfoldAndFoldBeanListPassiveControlClassificationList;
        // 筛选嵌套适配器数据
        this.screenNestAdapterList = new ArrayList<>(subjectMap.size());
    }

    /**
     * 设筛选适配器条目短点监听
     *
     * @param screenAdapterItemClickListener 筛选适配器条目短点监听
     */
    public void setScreenAdapterItemClickListener(ScreenAdapterItemClickListener screenAdapterItemClickListener) {
        this.screenAdapterItemClickListener = screenAdapterItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.screen_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.itemView.setTag(position);
        List<String> leftList = subjectMapKeyList.get(position);
        Map<Integer, Boolean> rightMap = subjectMapValueList.get(position);
        List<Integer> integerList = new ArrayList<>(rightMap.keySet());
        List<Boolean> booleanList = new ArrayList<>(rightMap.values());
        // 类别
        String classification = leftList.get(0);
        if (TextUtils.isEmpty(classification)) {
            ViewUtils.hideView(holder.screenItemTv, View.GONE);
        } else {
            holder.screenItemTv.setText(classification);
        }
        // 嵌套（控件）
        RecyclerViewConfigure recyclerViewConfigure = new RecyclerViewConfigure(context, holder.screenItemRv);
        // 避内容充整屏滑时头 / 末项频繁重绘
        if (holder.screenItemRv.getItemDecorationCount() == 0) {
            recyclerViewConfigure.gridLayout(integerList.get(0), 12, true, false, false);
        }
        holder.screenItemRv.setNestedScrollingEnabled(false);
        // 嵌套（适配器）
        List<String> conditions = leftList.subList(1, leftList.size());
        boolean unfoldAndFold = (ListUtils.listIsNotEmpty(unfoldAndFoldBeanListActiveControlClassificationList) && unfoldAndFoldBeanListActiveControlClassificationList.contains(classification)) || (ListUtils.listIsNotEmpty(unfoldAndFoldBeanListPassiveControlClassificationList) && unfoldAndFoldBeanListPassiveControlClassificationList.contains(classification));
        ScreenNestAdapter screenNestAdapter = new ScreenNestAdapter(context, classification, conditions, booleanList.get(0), canReverseSelectAfterSingleSelectList.contains(classification), defaultSelectMapKeyList.contains(classification) ? indexExtract(conditions, defaultSelectMap.get(classification)) : null, mutuallyExclusiveBeanListClassificationList.contains(classification), unfoldAndFold, unfoldAndFoldActiveControlCondition(classification));
        screenNestAdapterList.add(screenNestAdapter);
        // 嵌套（控件关联适配器）
        holder.screenItemRv.setAdapter(screenNestAdapter);
        // 嵌套（监听）
        screenNestAdapter.setScreenNestAdapterItemClickListener(new ScreenNestAdapterItemClickListener() {
            @Override
            public void onItemClick(View view, String classification, String condition, boolean selected) {
                if (null != screenAdapterItemClickListener) {
                    screenAdapterItemClickListener.onItemClick(view, classification, condition, selected);
                }
            }

            @Override
            public void onItemMutuallyExclusiveClick(String classification) {
                mutuallyExclusive(classification);
            }

            @Override
            public void onItemUnfoldAndFoldClick(String classification, String condition, boolean unfold) {
                unfoldAndFold(classification, condition, unfold);
            }
        });
        // 条目试图数据
        itemViewMap.put(classification, holder.itemView);
        // 预折叠
        preFold(classification);
    }

    /**
     * 下标提取
     *
     * @param comparedList      被比对数据
     * @param needToCompareList 需比对数据
     * @return 下标数据
     */
    @Nullable
    private List<Integer> indexExtract(List<String> comparedList, List<String> needToCompareList) {
        if (ListUtils.listIsEmpty(comparedList) || ListUtils.listIsEmpty(needToCompareList)) {
            return null;
        }
        int size = needToCompareList.size();
        List<Integer> indexList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            String element = needToCompareList.get(i);
            if (comparedList.contains(element)) {
                indexList.add(comparedList.indexOf(element));
            }
        }
        return indexList;
    }

    /**
     * 互斥
     *
     * @param classification 类别
     */
    private void mutuallyExclusive(String classification) {
        List<String> classifications = getStringList(classification);
        // 除自身
        classifications.remove(classification);
        // 剩余互斥类别重置
        for (ScreenNestAdapter otherScreenNestAdapter : screenNestAdapterList) {
            boolean flag = (classifications.contains(otherScreenNestAdapter.classification) && ((otherScreenNestAdapter.selectPosition != -1) || (otherScreenNestAdapter.sparseBooleanArray.size() > 0)));
            if (flag) {
                otherScreenNestAdapter.reset();
            }
        }
    }

    /**
     * 获取字符串集
     *
     * @param classification 类别
     * @return 字符串集
     */
    @NonNull
    private List<String> getStringList(String classification) {
        String groupId = null;
        List<String> classifications = new ArrayList<>();
        // 互斥组 ID
        for (MutuallyExclusiveBean mutuallyExclusiveBean : mutuallyExclusiveBeanList) {
            if (classification.equals(mutuallyExclusiveBean.getClassification())) {
                groupId = mutuallyExclusiveBean.getGroupId();
                break;
            }
        }
        // 互斥类别
        for (MutuallyExclusiveBean mutuallyExclusive : mutuallyExclusiveBeanList) {
            if ((null != groupId) && groupId.equals(mutuallyExclusive.getGroupId())) {
                classifications.add(mutuallyExclusive.getClassification());
            }
        }
        return classifications;
    }

    /**
     * 展开 / 折叠主控条件
     *
     * @param classification 类别
     * @return 展开 / 折叠主控条件数据
     */
    @Nullable
    private List<String> unfoldAndFoldActiveControlCondition(String classification) {
        if (ListUtils.listIsEmpty(unfoldAndFoldBeanListActiveControlClassificationList) || !unfoldAndFoldBeanListActiveControlClassificationList.contains(classification)) {
            return null;
        }
        List<String> list = new ArrayList<>();
        for (UnfoldAndFoldBean unfoldAndFoldBean : unfoldAndFoldBeanList) {
            if (classification.equals(unfoldAndFoldBean.getActiveControlClassification())) {
                list.add(unfoldAndFoldBean.getActiveControlCondition());
            }
        }
        return list;
    }

    /**
     * 预折叠
     *
     * @param classification 类别
     */
    private void preFold(String classification) {
        if (unfoldAndFoldBeanListPassiveControlClassificationList.contains(classification)) {
            View itemView = itemViewMap.get(classification);
            if (null != itemView) {
                RecyclerViewDisplayController.itemViewGone(itemView);
            }
        }
    }

    /**
     * 展开 / 折叠
     *
     * @param classification 类别
     * @param condition      条件
     * @param unfold         展开
     */
    private void unfoldAndFold(String classification, String condition, boolean unfold) {
        List<String> list = new ArrayList<>();
        for (UnfoldAndFoldBean unfoldAndFoldBean : unfoldAndFoldBeanList) {
            if (classification.equals(unfoldAndFoldBean.getActiveControlClassification()) && condition.equals(unfoldAndFoldBean.getActiveControlCondition())) {
                list = unfoldAndFoldBean.getPassiveControlClassificationList();
                break;
            }
        }
        for (ScreenNestAdapter screenNestAdapter : screenNestAdapterList) {
            if (list.contains(screenNestAdapter.classification)) {
                View itemView = itemViewMap.get(screenNestAdapter.classification);
                if (null != itemView) {
                    if (unfold) {
                        RecyclerViewDisplayController.itemViewVisible(itemView);
                    } else {
                        screenNestAdapter.reset();
                        RecyclerViewDisplayController.itemViewGone(itemView);
                    }
                }
            }
        }
    }

    /**
     * 重置
     */
    public void reset() {
        for (ScreenNestAdapter screenNestAdapter : screenNestAdapterList) {
            screenNestAdapter.reset();
        }
    }

    @Override
    public int getItemCount() {
        if (ListUtils.listIsNotEmpty(subjectMapKeyList)) {
            return subjectMapKeyList.size();
        }
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView screenItemTv;
        private final RecyclerView screenItemRv;

        private ViewHolder(@NonNull View view) {
            super(view);
            screenItemTv = view.findViewById(R.id.screenItemTv);
            screenItemRv = view.findViewById(R.id.screenItemRv);
        }
    }
}