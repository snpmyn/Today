package widget.adapttemplate.kit;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import widget.adapttemplate.adapter.MenuAdapter;
import widget.adapttemplate.bean.MenuBean;
import widget.recyclerview.configure.RecyclerViewConfigure;
import widget.recyclerview.controller.RecyclerViewDisplayController;
import widget.recyclerview.listener.OnRecyclerViewOnItemClickListener;

/**
 * Created on 2021/12/7
 *
 * @author zsp
 * @desc 菜单适配器配套元件
 */
public class MenuAdapterKit {
    /**
     * 展示
     *
     * @param appCompatActivity       活动
     * @param recyclerView            控件
     * @param menuBeanList            数据集合
     * @param spanCount               跨距数
     * @param space                   间距
     * @param totalMargin             总外边距
     * @param spruce                  spruce 否
     * @param menuAdapterKitInterface 菜单适配器配套元件接口
     */
    public void display(AppCompatActivity appCompatActivity, RecyclerView recyclerView, List<MenuBean> menuBeanList, int spanCount, int space, int totalMargin, boolean spruce, MenuAdapterKitInterface menuAdapterKitInterface) {
        // 控件
        RecyclerViewConfigure recyclerViewConfigure = new RecyclerViewConfigure(appCompatActivity, recyclerView);
        recyclerViewConfigure.gridLayout(spanCount, space, true, true, spruce);
        // 适配器
        MenuAdapter menuAdapter = new MenuAdapter(appCompatActivity, spanCount, totalMargin);
        menuAdapter.setMenuData(menuBeanList);
        menuAdapter.setOnRecyclerViewOnItemClickListener(new OnRecyclerViewOnItemClickListener() {
            @Override
            public <T> void onItemClick(View view, int position, T t) {
                MenuBean menuBean = (MenuBean) t;
                if (null != menuAdapterKitInterface) {
                    menuAdapterKitInterface.onItemClick(view, menuBean);
                }
            }
        });
        // 展示
        RecyclerViewDisplayController.display(recyclerView, menuAdapter);
    }

    /**
     * 菜单适配器配套元件接口
     */
    public interface MenuAdapterKitInterface {
        /**
         * 条目点击
         *
         * @param view     视图
         * @param menuBean 菜单
         */
        void onItemClick(View view, MenuBean menuBean);
    }
}