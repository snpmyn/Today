package com.zsp.today.module.homecome.kit;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialog;

import com.zsp.today.R;
import com.zsp.today.application.App;
import com.zsp.today.basic.backup.BackupKit;
import com.zsp.today.module.homecome.adapter.HomeComeListAdapter;
import com.zsp.today.module.homecome.bean.HomeComeListBean;
import com.zsp.today.module.homecome.database.HomeComeDataBaseTable;
import com.zsp.today.module.homecome.value.HomeComeCondition;
import com.zsp.today.module.homecome.value.HomeComeConstant;

import java.util.ArrayList;
import java.util.List;

import litepal.kit.LitePalKit;
import util.list.ListUtils;
import util.mmkv.MmkvKit;
import util.resource.ResourceUtils;
import util.statusbar.StatusBarColorHelper;
import widget.carousel.CarouselItem;
import widget.dialog.materialalertdialog.ButtonMaterialAlertDialogKit;
import widget.dialog.materialalertdialog.CarouselMaterialAlertDialogKit;
import widget.dialog.materialalertdialog.DoubleConfirmationMaterialAlertDialogKit;
import widget.dialog.materialalertdialog.InputMaterialAlertDialogKit;
import widget.media.image.ImagePicker;
import widget.memorial.MemorialKit;
import widget.permissionx.kit.PermissionKit;
import widget.permissionx.kit.PermissionxKit;
import widget.permissionx.listener.PermissionxKitListener;
import widget.recyclerview.custom.SmartFlexibleRecyclerView;
import widget.recyclerview.listener.OnRecyclerViewOnItemInnerClickListener;
import widget.toast.ToastKit;
import widget.view.DatePickerDialog;

/**
 * Created on 2025/10/6.
 *
 * @author 郑少鹏
 * @desc 归心页配套原件
 */
public class HomeComeActivityKit {
    /**
     * 设置背景
     *
     * @param appCompatActivity            活动
     * @param homeComeActivityIvBackground ImageView
     */
    public void setBackground(AppCompatActivity appCompatActivity, ImageView homeComeActivityIvBackground) {
        String backgroundImageResource = MmkvKit.defaultMmkv().decodeString(HomeComeConstant.HOME_COME_ACTIVITY_$_BACKGROUND_IMAGE_RESOURCE);
        int imageResource = ResourceUtils.getResourceId(appCompatActivity, backgroundImageResource, "drawable");
        if (imageResource == 0) {
            imageResource = ResourceUtils.getResourceId(appCompatActivity, "home_come_1", "drawable");
        }
        StatusBarColorHelper.INSTANCE.setBackgroundImageResource(appCompatActivity, homeComeActivityIvBackground, imageResource, new StatusBarColorHelper.StatusBarColorListener() {
            @Override
            public void onLight() {

            }

            @Override
            public void onDark() {

            }
        });
    }

    /**
     * 选择背景
     *
     * @param appCompatActivity            活动
     * @param imagePicker                  图片选择器
     * @param homeComeActivityIvBackground ImageView
     */
    public void selectBackground(AppCompatActivity appCompatActivity, ImagePicker imagePicker, ImageView homeComeActivityIvBackground) {
        // 轮播条目集
        List<CarouselItem> carouselItemList = new ArrayList<>(13);
        carouselItemList.add(new CarouselItem(true, R.drawable.home_come_1, "home_come_1", "晨光谷"));
        carouselItemList.add(new CarouselItem(true, R.drawable.home_come_2, "home_come_2", "孤影峰"));
        carouselItemList.add(new CarouselItem(true, R.drawable.home_come_3, "home_come_3", "余晖线"));
        carouselItemList.add(new CarouselItem(true, R.drawable.home_come_4, "home_come_4", "无尽行"));
        carouselItemList.add(new CarouselItem(true, R.drawable.home_come_5, "home_come_5", "星夜画"));
        carouselItemList.add(new CarouselItem(true, R.drawable.home_come_6, "home_come_6", "春街树"));
        carouselItemList.add(new CarouselItem(true, R.drawable.home_come_7, "home_come_7", "余晖月"));
        carouselItemList.add(new CarouselItem(true, R.drawable.home_come_8, "home_come_8", "黎明下"));
        carouselItemList.add(new CarouselItem(true, R.drawable.home_come_9, "home_come_9", "山居夜"));
        carouselItemList.add(new CarouselItem(true, R.drawable.home_come_10, "home_come_10", "暮雪城"));
        carouselItemList.add(new CarouselItem(true, R.drawable.home_come_11, "home_come_11", "黑独山"));
        carouselItemList.add(new CarouselItem(true, R.drawable.home_come_12, "home_come_12", "西雅拉"));
        carouselItemList.add(new CarouselItem(true, R.drawable.home_come_13, "home_come_13", "雪木屋"));
        CarouselMaterialAlertDialogKit.getInstance().show(appCompatActivity, carouselItemList, 2, (alertDialog, carouselItem) -> {
            MmkvKit.defaultMmkv().encode(HomeComeConstant.HOME_COME_ACTIVITY_$_BACKGROUND_IMAGE_RESOURCE, carouselItem.getCarouselTitle());
            setBackground(appCompatActivity, homeComeActivityIvBackground);
        });
        /*ButtonMaterialAlertDialogKit.getInstance().show(appCompatActivity, appCompatActivity.getString(R.string.selectFromAlbum), appCompatActivity.getString(R.string.camera), null);*/
        ButtonMaterialAlertDialogKit.getInstance().setButtonMaterialAlertDialogKitTopClickListener(alertDialog -> {
            alertDialog.dismiss();
            PermissionxKit.execute(appCompatActivity, true, PermissionKit.readImage(), R.string.readImageAreBasedOnThePermission, com.zsp.core.R.string.youNeedToAllowNecessaryPermissionInSettingManually, com.zsp.core.R.string.agree, com.zsp.core.R.string.refuse, new PermissionxKitListener() {
                @Override
                public void allGranted() {
                    imagePicker.single().start((ImagePicker.OnImageSingleSelectListener) uri -> {
                        if (null != uri) {
                            ToastKit.showShort(uri.toString());
                        }
                    });
                }

                @Override
                public void allGrantedContrary() {

                }
            });
        });
        ButtonMaterialAlertDialogKit.getInstance().setButtonMaterialAlertDialogKitMiddleClickListener(alertDialog -> {
            alertDialog.dismiss();
            PermissionxKit.execute(appCompatActivity, true, PermissionKit.camera(), R.string.cameraAreBasedOnThePermission, com.zsp.core.R.string.youNeedToAllowNecessaryPermissionInSettingManually, com.zsp.core.R.string.agree, com.zsp.core.R.string.refuse, new PermissionxKitListener() {
                @Override
                public void allGranted() {
                    imagePicker.camera().start((ImagePicker.OnImageSingleSelectListener) uri -> {
                        if (null != uri) {
                            ToastKit.showShort(uri.toString());
                        }
                    });
                }

                @Override
                public void allGrantedContrary() {

                }
            });
        });
    }

    /**
     * 展示
     *
     * @param appCompatActivity         活动
     * @param smartFlexibleRecyclerView SmartFlexibleRecyclerView
     */
    public void display(AppCompatActivity appCompatActivity, SmartFlexibleRecyclerView smartFlexibleRecyclerView) {
        List<HomeComeDataBaseTable> homeComeDataBaseTableList = LitePalKit.getInstance().findAll(HomeComeDataBaseTable.class);
        List<HomeComeListBean> homeComeListBeanList = new ArrayList<>(homeComeDataBaseTableList.size());
        if (ListUtils.listIsNotEmpty(homeComeDataBaseTableList)) {
            for (HomeComeDataBaseTable homeComeDataBaseTable : homeComeDataBaseTableList) {
                String calendarType = homeComeDataBaseTable.getCalendarType();
                int deathYear = homeComeDataBaseTable.getDeathYear();
                int deathMonth = homeComeDataBaseTable.getDeathMonth();
                int deathDay = homeComeDataBaseTable.getDeathDay();
                MemorialKit.MemorialInfo memorialInfo = MemorialKit.getMemorialInfo(deathYear, deathMonth, deathDay, areLunar(calendarType));
                homeComeListBeanList.add(new HomeComeListBean(homeComeDataBaseTable.getCall(), calendarType, deathYear, deathMonth, deathDay, memorialInfo));
            }
        }
        // 设置 SmartFlexibleRecyclerView
        smartFlexibleRecyclerView.setSpanCount(2);
        smartFlexibleRecyclerView.setSpacing(12);
        smartFlexibleRecyclerView.setCornerRadius(20);
        // 归心列表适配器
        HomeComeListAdapter homeComeListAdapter = getHomeComeListAdapter(appCompatActivity, smartFlexibleRecyclerView, homeComeListBeanList);
        // 设置适配器
        smartFlexibleRecyclerView.setAdapter(homeComeListAdapter);
    }

    /**
     * 获取归心列表适配器
     *
     * @param appCompatActivity         活动
     * @param smartFlexibleRecyclerView SmartFlexibleRecyclerView
     * @param homeComeListBeanList      归心月列表集
     * @return 归心列表适配器
     */
    @NonNull
    private HomeComeListAdapter getHomeComeListAdapter(AppCompatActivity appCompatActivity, SmartFlexibleRecyclerView smartFlexibleRecyclerView, List<HomeComeListBean> homeComeListBeanList) {
        HomeComeListAdapter homeComeListAdapter = new HomeComeListAdapter(appCompatActivity);
        homeComeListAdapter.setHomeComeListData(homeComeListBeanList);
        homeComeListAdapter.setOnRecyclerViewOnItemInnerClickListener(new OnRecyclerViewOnItemInnerClickListener() {
            @Override
            public <T> void onItemInnerClick(View view, int position, T t) {
                HomeComeListBean homeComeListBean = (HomeComeListBean) t;
                ButtonMaterialAlertDialogKit.getInstance().show(appCompatActivity, appCompatActivity.getString(R.string.modify), appCompatActivity.getString(com.zsp.core.R.string.delete), null);
                ButtonMaterialAlertDialogKit.getInstance().setButtonMaterialAlertDialogKitTopClickListener(alertDialog -> {
                    alertDialog.dismiss();
                    placeOrModify(appCompatActivity, smartFlexibleRecyclerView, homeComeListBean.getCall(), homeComeListBean.getCalendarType(), homeComeListBean.getDeathYear(), homeComeListBean.getDeathMonth(), homeComeListBean.getDeathDay(), true);
                });
                ButtonMaterialAlertDialogKit.getInstance().setButtonMaterialAlertDialogKitMiddleClickListener(alertDialog -> {
                    alertDialog.dismiss();
                    delete(appCompatActivity, smartFlexibleRecyclerView, homeComeListBean.getCall(), homeComeListBean.getCalendarType(), homeComeListBean.getDeathYear(), homeComeListBean.getDeathMonth(), homeComeListBean.getDeathDay());
                });
            }
        });
        return homeComeListAdapter;
    }

    /**
     * 安放或修改
     *
     * @param appCompatActivity         活动
     * @param smartFlexibleRecyclerView SmartFlexibleRecyclerView
     * @param originalCall              原始称呼
     * @param originalCalendarType      原始日历类型
     * @param originalDeathYear         原始去世年
     * @param originalDeathMonth        原始去世月
     * @param originalDeathDay          原始去世日
     * @param modify                    修改否
     */
    public void placeOrModify(AppCompatActivity appCompatActivity, SmartFlexibleRecyclerView smartFlexibleRecyclerView, String originalCall, String originalCalendarType, int originalDeathYear, int originalDeathMonth, int originalDeathDay, boolean modify) {
        InputMaterialAlertDialogKit.getInstance().show(appCompatActivity, appCompatActivity.getString(R.string.inputCall), originalCall, appCompatActivity.getString(com.zsp.core.R.string.continueString), appCompatActivity.getString(com.zsp.core.R.string.cancel), null);
        InputMaterialAlertDialogKit.getInstance().setInputMaterialAlertDialogKitOnPositiveClickListener((alertDialog, content) -> {
            alertDialog.dismiss();
            // 日期选择对话框
            DatePickerDialog datePickerDialog = new DatePickerDialog(appCompatActivity);
            datePickerDialog.setInitialDate(originalDeathYear, originalDeathMonth, originalDeathDay, getCalendarType(originalCalendarType));
            datePickerDialog.setOnDateSelectListener((year, month, day, calendarType) -> {
                // 有变动否
                boolean areChange = ((!TextUtils.equals(originalCall, content)) || (!TextUtils.equals(originalCalendarType, calendarType.getName())) || (originalDeathYear != year) || (originalDeathMonth != month) || (originalDeathDay != day));
                if (modify) {
                    // 修改
                    if (areAlreadyExist(content, calendarType.getName(), year, month, day)) {
                        // 已存在
                        ToastKit.showShort(appCompatActivity.getString(R.string.hasBeenPlaced));
                        return;
                    }
                    if (!areChange) {
                        // 无变动
                        ToastKit.showShort(appCompatActivity.getString(R.string.myThoughtsHaveNotChanged));
                    }
                    // 不存在且有变动
                    modify(appCompatActivity, smartFlexibleRecyclerView, originalCall, originalCalendarType, originalDeathYear, originalDeathMonth, originalDeathDay, content, calendarType.getName(), year, month, day);
                } else {
                    // 安放
                    place(appCompatActivity, smartFlexibleRecyclerView, content, calendarType.getName(), year, month, day);
                }
            });
            datePickerDialog.show();
        });
        InputMaterialAlertDialogKit.getInstance().setInputMaterialAlertDialogKitOnNegativeClickListener(AppCompatDialog::dismiss);
    }

    /**
     * 安放
     *
     * @param appCompatActivity         活动
     * @param smartFlexibleRecyclerView SmartFlexibleRecyclerView
     * @param call                      称呼
     * @param calendarType              日历类型
     * @param deathYear                 去世年
     * @param deathMonth                去世月
     * @param deathDay                  去世日
     */
    private void place(AppCompatActivity appCompatActivity, SmartFlexibleRecyclerView smartFlexibleRecyclerView, String call, String calendarType, int deathYear, int deathMonth, int deathDay) {
        if (areAlreadyExist(call, calendarType, deathYear, deathMonth, deathDay)) {
            ToastKit.showShort(appCompatActivity.getString(R.string.hasBeenPlaced));
            return;
        }
        HomeComeDataBaseTable homeComeDataBaseTable = new HomeComeDataBaseTable(App.getAppInstance().getPhoneNumber(), null, call, calendarType, deathYear, deathMonth, deathDay);
        if (LitePalKit.getInstance().singleSave(homeComeDataBaseTable)) {
            // 展示
            display(appCompatActivity, smartFlexibleRecyclerView);
            // 备份
            BackupKit.getInstance().backup(appCompatActivity, HomeComeDataBaseTable.class, null);
        }
    }

    /**
     * 修改
     *
     * @param appCompatActivity         活动
     * @param smartFlexibleRecyclerView SmartFlexibleRecyclerView
     * @param originalCall              原始称呼
     * @param originalCalendarType      原始日历类型
     * @param originalDeathYear         原始去世年
     * @param originalDeathMonth        原始去世月
     * @param originalDeathDay          原始去世日
     * @param nowCall                   现在称呼
     * @param nowCalendarType           现在日历类型
     * @param nowDeathYear              现在去世年
     * @param nowDeathMonth             现在去世月
     * @param nowDeathDay               现在去世日
     */
    private void modify(AppCompatActivity appCompatActivity, SmartFlexibleRecyclerView smartFlexibleRecyclerView, String originalCall, String originalCalendarType, int originalDeathYear, int originalDeathMonth, int originalDeathDay, String nowCall, String nowCalendarType, int nowDeathYear, int nowDeathMonth, int nowDeathDay) {
        // 创建待更新对象
        HomeComeDataBaseTable homeComeDataBaseTableUpdate = new HomeComeDataBaseTable();
        homeComeDataBaseTableUpdate.setCall(nowCall);
        homeComeDataBaseTableUpdate.setCalendarType(nowCalendarType);
        homeComeDataBaseTableUpdate.setDeathYear(nowDeathYear);
        homeComeDataBaseTableUpdate.setDeathMonth(nowDeathMonth);
        homeComeDataBaseTableUpdate.setDeathDay(nowDeathDay);
        // 获取被更新对象
        List<HomeComeDataBaseTable> homeComeDataBaseTableList = query(originalCall, originalCalendarType, originalDeathYear, originalDeathMonth, originalDeathDay);
        HomeComeDataBaseTable homeComeDataBaseTableOld = homeComeDataBaseTableList.get(0);
        // 单个更新
        if (LitePalKit.getInstance().singleUpdate(homeComeDataBaseTableUpdate, homeComeDataBaseTableOld.getBaseObjectId()) != 0) {
            // 展示
            display(appCompatActivity, smartFlexibleRecyclerView);
            // 备份
            BackupKit.getInstance().backup(appCompatActivity, HomeComeDataBaseTable.class, null);
        }
    }

    /**
     * 删除
     *
     * @param appCompatActivity         活动
     * @param smartFlexibleRecyclerView SmartFlexibleRecyclerView
     * @param call                      称呼
     * @param calendarType              日历类型
     * @param deathYear                 去世年
     * @param deathMonth                去世月
     * @param deathDay                  去世日
     */
    private void delete(AppCompatActivity appCompatActivity, SmartFlexibleRecyclerView smartFlexibleRecyclerView, String call, String calendarType, int deathYear, int deathMonth, int deathDay) {
        DoubleConfirmationMaterialAlertDialogKit.getInstance().show(appCompatActivity, appCompatActivity.getString(com.zsp.core.R.string.hint), appCompatActivity.getString(R.string.wantToDeleteTheThought), appCompatActivity.getString(R.string.yes), appCompatActivity.getString(R.string.wait), appCompatActivity.getString(R.string.hintAgain), appCompatActivity.getString(R.string.deletionCannotBeRestored), appCompatActivity.getString(com.zsp.core.R.string.delete), appCompatActivity.getString(R.string.wait), dialog -> {
            dialog.dismiss();
            List<HomeComeDataBaseTable> homeComeDataBaseTableList = query(call, calendarType, deathYear, deathMonth, deathDay);
            HomeComeDataBaseTable homeComeDataBaseTable = homeComeDataBaseTableList.get(0);
            LitePalKit.getInstance().singleDelete(HomeComeDataBaseTable.class, homeComeDataBaseTable.getBaseObjectId());
            // 展示
            display(appCompatActivity, smartFlexibleRecyclerView);
            // 备份
            BackupKit.getInstance().backup(appCompatActivity, HomeComeDataBaseTable.class, null);
        });
    }

    /**
     * 交换
     *
     * @param homeComeListBeanList 归心月列表集
     */
    public void swap(@NonNull List<HomeComeListBean> homeComeListBeanList) {
        List<HomeComeDataBaseTable> homeComeDataBaseTableList = new ArrayList<>(homeComeListBeanList.size());
        for (HomeComeListBean homeComeListBean : homeComeListBeanList) {
            HomeComeDataBaseTable homeComeDataBaseTableFromQuery = query(homeComeListBean.getCall(), homeComeListBean.getCalendarType(), homeComeListBean.getDeathYear(), homeComeListBean.getDeathMonth(), homeComeListBean.getDeathDay()).get(0);
            HomeComeDataBaseTable homeComeDataBaseTable = new HomeComeDataBaseTable(homeComeDataBaseTableFromQuery.getPhoneNumber(), homeComeDataBaseTableFromQuery.getDate(), homeComeDataBaseTableFromQuery.getCall(), homeComeDataBaseTableFromQuery.getCalendarType(), homeComeDataBaseTableFromQuery.getDeathYear(), homeComeDataBaseTableFromQuery.getDeathMonth(), homeComeDataBaseTableFromQuery.getDeathDay());
            homeComeDataBaseTableList.add(homeComeDataBaseTable);
        }
        LitePalKit.getInstance().allDelete(HomeComeDataBaseTable.class);
        LitePalKit.getInstance().multiSave(homeComeDataBaseTableList);
    }

    /**
     * 是否是已存在
     *
     * @param call         称呼
     * @param calendarType 日历类型
     * @param deathYear    去世年
     * @param deathMonth   去世月
     * @param deathDay     去世日
     * @return 已存在否
     */
    private boolean areAlreadyExist(String call, String calendarType, int deathYear, int deathMonth, int deathDay) {
        return ListUtils.listIsNotEmpty(query(call, calendarType, deathYear, deathMonth, deathDay));
    }

    /**
     * 查询
     *
     * @param call         称呼
     * @param calendarType 日历类型
     * @param deathYear    去世年
     * @param deathMonth   去世月
     * @param deathDay     去世日
     * @return 归心数据库表集
     */
    private List<HomeComeDataBaseTable> query(String call, String calendarType, int deathYear, int deathMonth, int deathDay) {
        String[] conditions = new String[]{HomeComeCondition.HOME_COME_PHONE_NUMBER_AND_CALL_AND_CALENDAR_TYPE_AND_DEATH_YEAR_AND_DEATH_MONTH_AND_DEATH_DAY, App.getAppInstance().getPhoneNumber(), call, calendarType, String.valueOf(deathYear), String.valueOf(deathMonth), String.valueOf(deathDay)};
        return LitePalKit.getInstance().queryByWhere(HomeComeDataBaseTable.class, conditions);
    }

    /**
     * 是否是农历
     *
     * @param calendarType 日历类型
     * @return 农历否
     */
    private boolean areLunar(String calendarType) {
        return TextUtils.equals(calendarType, DatePickerDialog.CalendarType.LUNAR.getName());
    }

    /**
     * 获取日历类型枚举
     *
     * @param calendarType 日历类型
     * @return 日历类型枚举
     */
    private DatePickerDialog.CalendarType getCalendarType(String calendarType) {
        return TextUtils.equals(calendarType, DatePickerDialog.CalendarType.LUNAR.getName()) ? DatePickerDialog.CalendarType.LUNAR : DatePickerDialog.CalendarType.SOLAR;
    }
}