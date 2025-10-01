package widget.adapttemplate.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zsp.core.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import util.list.ListUtils;
import util.screen.ScreenUtils;
import widget.adapttemplate.bean.MenuBean;
import widget.recyclerview.listener.OnRecyclerViewOnItemClickListener;
import widget.textview.kit.TextViewKit;

/**
 * Created on 2021/3/29
 *
 * @author zsp
 * @desc 菜单适配器
 */
public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {
    private final Context context;
    private final int spanCount;
    private final int totalMargin;
    private List<MenuBean> menuBeans;
    private OnRecyclerViewOnItemClickListener onRecyclerViewOnItemClickListener;

    /**
     * constructor
     *
     * @param context     上下文
     * @param spanCount   跨距数
     * @param totalMargin 总外边距
     */
    public MenuAdapter(Context context, int spanCount, int totalMargin) {
        this.context = context;
        this.spanCount = spanCount;
        this.totalMargin = totalMargin;
        this.menuBeans = new ArrayList<>();
    }

    public void setMenuData(List<MenuBean> menuBeans) {
        this.menuBeans = menuBeans;
    }

    public void setOnRecyclerViewOnItemClickListener(OnRecyclerViewOnItemClickListener onRecyclerViewOnItemLongClickListener) {
        this.onRecyclerViewOnItemClickListener = onRecyclerViewOnItemLongClickListener;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.menu_item, viewGroup, false);
        // 宽高等同
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = ((ScreenUtils.screenWidth(context) - totalMargin) / spanCount);
        view.setLayoutParams(layoutParams);
        // 点击监听
        view.setOnClickListener(v -> {
            int position = (Integer) view.getTag();
            onRecyclerViewOnItemClickListener.onItemClick(v, position, menuBeans.get(position));
        });
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.itemView.setTag(position);
        MenuBean menuBean = menuBeans.get(position);
        // 菜单
        TextViewKit.setDrawable(context, holder.menuItemTv, menuBean.getMenuIconResId(), 2, 20);
        holder.menuItemTv.setText(menuBean.getMenuName());
    }

    @Override
    public int getItemCount() {
        if (ListUtils.listIsNotEmpty(menuBeans)) {
            return menuBeans.size();
        }
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView menuItemTv;

        private ViewHolder(@NonNull View view) {
            super(view);
            menuItemTv = view.findViewById(R.id.menuItemTv);
        }
    }
}