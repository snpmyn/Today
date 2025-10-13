package widget.adapttemplate.kit;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import widget.adapttemplate.adapter.FunctionAdapter;
import widget.adapttemplate.bean.FunctionBean;
import widget.recyclerview.configure.RecyclerViewConfigure;
import widget.recyclerview.controller.RecyclerViewDisplayController;
import widget.recyclerview.listener.OnRecyclerViewOnItemClickListener;

/**
 * Created on 2021/12/7
 *
 * @author zsp
 * @desc 功能适配器配套元件
 */
public class FunctionAdapterKit {
    /**
     * 展示
     *
     * @param appCompatActivity           活动
     * @param recyclerView                控件
     * @param functionBeanList            数据集合
     * @param spanCount                   跨距数
     * @param space                       间距
     * @param totalMargin                 总外边距
     * @param functionAdapterKitInterface 功能适配器配套元件接口
     */
    public void display(AppCompatActivity appCompatActivity, RecyclerView recyclerView, List<FunctionBean> functionBeanList, int spanCount, int space, int totalMargin, FunctionAdapterKitInterface functionAdapterKitInterface) {
        // 控件
        RecyclerViewConfigure recyclerViewConfigure = new RecyclerViewConfigure(appCompatActivity, recyclerView);
        recyclerViewConfigure.gridLayout(spanCount, space, true, true, false);
        // 适配器
        FunctionAdapter functionAdapter = new FunctionAdapter(appCompatActivity, spanCount, totalMargin);
        functionAdapter.setFunctionData(functionBeanList);
        functionAdapter.setOnRecyclerViewOnItemClickListener(new OnRecyclerViewOnItemClickListener() {
            @Override
            public <T> void onItemClick(View view, int position, T t) {
                FunctionBean functionBean = (FunctionBean) t;
                if (null != functionAdapterKitInterface) {
                    functionAdapterKitInterface.onItemClick(functionBean);
                }
            }
        });
        // 展示
        RecyclerViewDisplayController.display(recyclerView, functionAdapter);
    }

    /**
     * 功能适配器配套元件接口
     */
    public interface FunctionAdapterKitInterface {
        /**
         * 条目点击
         *
         * @param functionBean 功能
         */
        void onItemClick(FunctionBean functionBean);
    }
}