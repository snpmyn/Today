banner_image_two.xmlpackage com.zsp.today.application;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.qtone.banner.listener.OnBannerListener;
import com.qtone.banner.listener.OnPageChangeListener;
import com.qtone.banner.transformer.AlphaPageTransformer;
import com.qtone.carousel.CarouselAdapter;
import com.qtone.carousel.CarouselView;
import com.qtone.scandemo.R;
import com.qtone.scandemo.module.scan.ScanActivity;
import com.qtone.scandemo.module.scan.adapter.ClassAdapter;
import com.qtone.scandemo.module.scan.adapter.ImageAdapter;
import com.qtone.scandemo.module.scan.adapter.classification.ClassificationAdapter;
import com.qtone.scandemo.module.scan.bean.ClassInfo;
import com.qtone.scandemo.module.scan.bean.StudentBean;
import com.qtone.scandemo.module.scan.bean.UnRecStuPaperBean;
import com.qtone.scandemo.module.scan.bean.UnrecognizedClassStu;
import com.qtone.scandemo.util.Utils;
import com.qtone.scandemo.util.animation.AnimationManager;
import com.qtone.scandemo.util.density.DensityUtils;
import com.qtone.scandemo.util.list.ListUtils;
import com.qtone.scandemo.util.recyclerview.LinearLayoutVerticalSpaceItemDecoration;
import com.qtone.scandemo.util.toast.ToastKt;
import com.qtone.scandemo.widget.dialog.CommonDialog;
import com.qtone.scandemo.widget.dialog.LoadDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created on 2026/5/11.
 *
 * @author 郑少鹏
 * @desc 未识别控制器
 */
public class UnRecognizeController {
    /**
     * TAG
     */
    private static final String TAG = UnRecognizeController.class.getSimpleName();
    /**
     * 扫描页
     */
    private final ScanActivity scanActivity;
    /**
     * 班级适配器
     */
    private ClassAdapter classAdapter;
    /**
     * 当前已选班级信息
     */
    private ClassInfo currentSelectClassInfo;
    /**
     * 分类适配器
     */
    private ClassificationAdapter classificationAdapter;
    /**
     * 未识别学生试卷数据集
     */
    private List<UnRecStuPaperBean> unRecStuPaperBeanList;
    /**
     * 当前已选未识别试卷位置
     */
    private int currentSelectUnidentifyExamPaperPosition = -1;

    /**
     * constructor
     *
     * @param scanActivity 扫描页
     */
    public UnRecognizeController(ScanActivity scanActivity) {
        this.scanActivity = scanActivity;
    }

    /**
     * 显示未识别试卷
     *
     * @param unRecStuPaperBeans 未识别学生试卷数据集
     */
    void showUnidentifyExamPaper(List<UnRecStuPaperBean> unRecStuPaperBeans) {
        if (null != scanActivity.rotateAnimator) {
            scanActivity.rotateAnimator.pause();
        }

        // 未识别学生试卷数据集
        unRecStuPaperBeanList = unRecStuPaperBeans;

        // 进度提示 - 隐藏
        scanActivity.activityScanBinding.recognitionRl.setVisibility(View.GONE);
        // 单面 / 双面扫描 - 隐藏
        scanActivity.activityScanBinding.llScanMode.setVisibility(View.GONE);
        // 扫描 - 隐藏
        scanActivity.activityScanBinding.scanningRl.setVisibility(View.GONE);
        // 未识别试卷 - 可见
        scanActivity.activityScanBinding.bannerPaperGallery.getRoot().setVisibility(View.VISIBLE);

        // 未识别试卷提示
        scanActivity.activityScanBinding.bannerPaperGallery.tvErrorNum.setText(buildUnRecognizeHintSpannableString(unRecStuPaperBeans.size()));

        // 初始化图片适配器
        initImageAdapter();
        // 初始化轮播
        initBanner(unRecStuPaperBeans);

        if ((null != unRecStuPaperBeanList) && (unRecStuPaperBeanList.size() <= 1)) {
            // 特殊场景 size <= 1
            // 左右均设暗色
            scanActivity.activityScanBinding.bannerPaperGallery.bannerPaperGalleryIbLeft.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(scanActivity, R.color.color_C1C1C1)));
            scanActivity.activityScanBinding.bannerPaperGallery.bannerPaperGalleryIbRight.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(scanActivity, R.color.color_C1C1C1)));
        }
        // 选择学生
        selectStudent();
        // 关闭
        close();
        // 取消
        cancel();
        // 确定
        ensure();
    }

    /**
     * 初始化图片适配器
     */
    private void initImageAdapter() {
        scanActivity.mImageTitleAdapter = new ImageAdapter(unRecStuPaperBeanList, (dataBean, pos) -> {
            if (currentSelectUnidentifyExamPaperPosition == pos) {
                return;
            }
            currentSelectUnidentifyExamPaperPosition = pos;
            // 加载名字
            Glide.with(scanActivity)
                    .load(dataBean.getNameFileUrl())
                    .skipMemoryCache(false)
                    .into(scanActivity.activityScanBinding.bannerPaperGallery.ivPreviewName)
                    .waitForLayout();
            // 置空当前已选班级信息
            currentSelectClassInfo = null;
            // 班级列表 - 隐藏
            scanActivity.activityScanBinding.bannerPaperGallery.listViewClass.setVisibility(View.GONE);
            // 显示班级名称和学生
            showClassNameAndStudent(scanActivity.mHomeClassInfoMap.get(dataBean.getHomeworkId()), dataBean);

            UnRecStuPaperBean unRecStuPaperBean = unRecStuPaperBeanList.get(currentSelectUnidentifyExamPaperPosition);
            StudentBean studentBean = unRecStuPaperBean.getSelectStu();
            if (studentBean != null) {
                classificationAdapter.setChildItemClickEnable(true);
                classificationAdapter.focus(studentBean.getStudentId(), true);
            } else if (classificationAdapter != null) {
                // TODO: 2026/4/27 此处适配器判空适用于扫描结束但未操作前场景 -> fetchUnrecognizedStuList 方法未调用结束前使用适配器导致闪退
                classificationAdapter.setChildItemClickEnable(unRecStuPaperBean.getHandleFlag() != 2);
            }
        });
        scanActivity.mImageTitleAdapter.setBannerListener(new OnBannerListener<UnRecStuPaperBean>() {
            @Override
            public void OnBannerClick(UnRecStuPaperBean data, int position) {
                if (position != currentSelectUnidentifyExamPaperPosition) {
                    scanActivity.activityScanBinding.bannerPaperGallery.paperBanner.setCurrentItem(position, true);
                }
            }

            @Override
            public void onGiveUp(UnRecStuPaperBean data, int position) {
                CommonDialog commonDialog = new CommonDialog(scanActivity);
                commonDialog.setTitleText("温馨提示")
                        .setContentText("该页已放弃\n请从扫描卷中取出该页")
                        .setPositiveText("确定")
                        .setNegativeText("取消")
                        .setShowNegative(true)
                        .setOnDialogClickListener(new CommonDialog.OnDialogClickListener() {
                            @Override
                            public void onCancel() {
                                commonDialog.dismiss();
                            }

                            @Override
                            public void onConfirm() {
                                commonDialog.dismiss();
                                unRecStuPaperBeanList.get(position).setHandleFlag(2);
                                scanActivity.mImageTitleAdapter.notifyItemChanged(position);
                                classificationAdapter.setChildItemClickEnable(false);
                                if (unRecStuPaperBeanList.get(currentSelectUnidentifyExamPaperPosition).getSelectStu() != null) {
                                    unRecStuPaperBeanList.get(currentSelectUnidentifyExamPaperPosition).setSelectStu(null);
                                    scanActivity.activityScanBinding.bannerPaperGallery.tvStudentName.setText("");
                                    classificationAdapter.clearSelect();
                                    unRecognizeHint();
                                }
                            }
                        });
                commonDialog.show();
            }
        });
    }

    /**
     * 初始化轮播
     *
     * @param unRecStuPaperBeans 未识别学生试卷数据集
     */
    private void initBanner(List<UnRecStuPaperBean> unRecStuPaperBeans) {
        // 画廊效果
        scanActivity.activityScanBinding.bannerPaperGallery.paperBanner.setAdapter(scanActivity.mImageTitleAdapter, false);

        //////
        BannerAdapter bannerAdapter = new BannerAdapter();
        unRecStuPaperBeanList.addAll(unRecStuPaperBeanList);
        bannerAdapter.setCarouselData(unRecStuPaperBeanList);

        // 核心控制 API：
        // 150f 是现在的默认值，你可以传入 200f（更慢、更丝滑），或者 100f（稍微快一点点）
        scanActivity.activityScanBinding.bannerPaperGallery.bannerPaperGalleryCv.setScrollSpeed(200f);
        scanActivity.activityScanBinding.bannerPaperGallery.bannerPaperGalleryCv.setVisibleCount(5);
        //scanActivity.activityScanBinding.bannerPaperGallery.bannerPaperGalleryCv.setAutoScroll(true);
        scanActivity.activityScanBinding.bannerPaperGallery.bannerPaperGalleryCv.setCarouselAdapter(bannerAdapter);

        // 挂载监听器（必须在 setCarouselAdapter 之前或者紧随其后挂载）
        scanActivity.activityScanBinding.bannerPaperGallery.bannerPaperGalleryCv.setOnPageChangeListener(new CarouselView.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // [核心表现] 进入页面时此方法会自动执行一次，返回初始正中间卡片的索引（通常是 0）
                // 之后的轮播、点击、手势滑动，只要居中条目变了，这里都会触发
                Log.d("Carousel", "当前正中间展示的条目索引是: " + position);
                ToastKt.showToast("Carousel = " + position);

                // 可以在这里安全地更新你的下方文案、业务关联数据等
            }
        });

        scanActivity.activityScanBinding.bannerPaperGallery.bannerPaperGalleryCv.setCurrentItem(2, false);

        bannerAdapter.setOnItemClickListener(new CarouselAdapter.OnItemClickListener<UnRecStuPaperBean>() {
            @Override
            public void onItemClick(View view, UnRecStuPaperBean item, int position) {
                ToastKt.showToast("点击 = " + position);
            }
        });
        //////

        /*mImageTitleAdapter.setSelectPos(1, false);*/
        /*scanBinding.bannerPaperGallery.paperBanner.setStartPosition(2);*/
        scanActivity.activityScanBinding.bannerPaperGallery.paperBanner.isAutoLoop(false);
        scanActivity.activityScanBinding.bannerPaperGallery.paperBanner.setBannerGalleryEffect(130, 0, 0.85f);

        // 可以和其他 PageTransformer 组合使用
        // 比如 AlphaPageTransformer
        // 注意但和其他带有缩放的 PageTransformer 会显示冲突
        // 添加透明效果（画廊配合透明效果更棒）
        scanActivity.activityScanBinding.bannerPaperGallery.paperBanner.addPageTransformer(new AlphaPageTransformer());

        /*scanBinding.bannerPaperGallery.paperBanner.setOnBannerListener(new OnBannerListener<UnRecStuPaperBean>() {
            @Override
            public void OnBannerClick(UnRecStuPaperBean data, int position) {
                if (position != currentSelectPaperPos) {
                    scanBinding.bannerPaperGallery.paperBanner.setCurrentItem(position, true);
                }
            }
        });*/
        scanActivity.activityScanBinding.bannerPaperGallery.paperBanner.addOnLayoutChangeListener(scanActivity.mOnLayoutChangeListener);

        final int[] currentPosition = {0};
        // 向左滑动
        scanActivity.activityScanBinding.bannerPaperGallery.bannerPaperGalleryIbLeft.setOnClickListener(v -> {
            if (currentPosition[0] > 0) {
                scanActivity.activityScanBinding.bannerPaperGallery.paperBanner.setCurrentItem(currentPosition[0] - 1, true);
            }
        });
        // 向右滑动
        scanActivity.activityScanBinding.bannerPaperGallery.bannerPaperGalleryIbRight.setOnClickListener(v -> {
            if (currentPosition[0] < (unRecStuPaperBeans.size() - 1)) {
                scanActivity.activityScanBinding.bannerPaperGallery.paperBanner.setCurrentItem(currentPosition[0] + 1, true);
            }
        });
        scanActivity.activityScanBinding.bannerPaperGallery.paperBanner.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPosition[0] = position;
                scanActivity.mImageTitleAdapter.setSelectPos(position, true);
                if (unRecStuPaperBeans.size() <= 1) {
                    // 特殊场景 size <= 1
                    // 左右均设暗色
                    // TODO: 2026/4/27 size = 1 时不可滑动，该部分逻辑可删除。待优化
                    scanActivity.activityScanBinding.bannerPaperGallery.bannerPaperGalleryIbLeft.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(scanActivity, R.color.color_C1C1C1)));
                    scanActivity.activityScanBinding.bannerPaperGallery.bannerPaperGalleryIbRight.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(scanActivity, R.color.color_C1C1C1)));
                } else if (position == 0) {
                    // 特殊场景 size = 2
                    // position = 0 时
                    // 左设暗色 + 右设亮色
                    scanActivity.activityScanBinding.bannerPaperGallery.bannerPaperGalleryIbLeft.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(scanActivity, R.color.color_C1C1C1)));
                    scanActivity.activityScanBinding.bannerPaperGallery.bannerPaperGalleryIbRight.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(scanActivity, R.color.color_1F7FEE)));
                } else if (position == (unRecStuPaperBeans.size() - 1)) {
                    // 特殊场景 size = 2
                    // position = 1 时
                    // 左设亮色 + 右设暗色
                    scanActivity.activityScanBinding.bannerPaperGallery.bannerPaperGalleryIbLeft.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(scanActivity, R.color.color_1F7FEE)));
                    scanActivity.activityScanBinding.bannerPaperGallery.bannerPaperGalleryIbRight.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(scanActivity, R.color.color_C1C1C1)));
                } else {
                    scanActivity.activityScanBinding.bannerPaperGallery.bannerPaperGalleryIbLeft.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(scanActivity, R.color.color_1F7FEE)));
                    scanActivity.activityScanBinding.bannerPaperGallery.bannerPaperGalleryIbRight.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(scanActivity, R.color.color_1F7FEE)));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 选择学生
     */
    private void selectStudent() {
        scanActivity.activityScanBinding.bannerPaperGallery.bannerPaperGalleryMbSelectStudent.setOnClickListener(view -> {
            scanActivity.activityScanBinding.bannerPaperGallery.rlStudentLayout.setVisibility(View.VISIBLE);
            scanActivity.activityScanBinding.bannerPaperGallery.listViewClass.setVisibility(View.GONE);
            UnRecStuPaperBean unRecStuPaperBean = unRecStuPaperBeanList.get(currentSelectUnidentifyExamPaperPosition);
            List<ClassInfo> classInfoList = scanActivity.mHomeClassInfoMap.get(unRecStuPaperBean.getHomeworkId());
            // 显示班级名称和学生
            showClassNameAndStudent(classInfoList, unRecStuPaperBean);
        });
        scanActivity.activityScanBinding.bannerPaperGallery.bannerPaperGalleryLlPleaseSelectClass.setOnClickListener(view -> {
            if (scanActivity.activityScanBinding.bannerPaperGallery.listViewClass.getVisibility() == View.VISIBLE) {
                AnimationManager.rotation(scanActivity.activityScanBinding.bannerPaperGallery.bannerPaperGalleryAciv, 300, 180F, 0F, null);
                scanActivity.activityScanBinding.bannerPaperGallery.listViewClass.setVisibility(View.GONE);
                return;
            } else {
                AnimationManager.rotation(scanActivity.activityScanBinding.bannerPaperGallery.bannerPaperGalleryAciv, 300, 0F, 180F, null);
            }
            scanActivity.activityScanBinding.bannerPaperGallery.listViewClass.setVisibility(View.VISIBLE);
            UnRecStuPaperBean unRecStuPaperBean = unRecStuPaperBeanList.get(currentSelectUnidentifyExamPaperPosition);
            List<ClassInfo> classInfoList = scanActivity.mHomeClassInfoMap.get(unRecStuPaperBean.getHomeworkId());
            if (classInfoList != null) {
                // 已缓存作业对应班级数据
                showClassNameAndList(classInfoList);
            } else {
                // 未缓存作业对应的班级和学生 -> 查询班级和学生
                scanActivity.scanViewModel.queryClassAndStudent(scanActivity, unRecStuPaperBean.getHomeworkId(), unRecStuPaperBean.getScanId());
            }
        });
    }

    /**
     * 关闭
     */
    private void close() {
        scanActivity.activityScanBinding.bannerPaperGallery.bannerPaperGalleryMbClose.setOnClickListener(view -> scanActivity.activityScanBinding.bannerPaperGallery.rlStudentLayout.setVisibility(View.GONE));
    }

    /**
     * 取消
     */
    private void cancel() {
        scanActivity.activityScanBinding.bannerPaperGallery.bannerPaperGalleryMbCancel.setOnClickListener(v -> {
            if (!Utils.doubleClick()) {
                CommonDialog commonDialog = new CommonDialog(scanActivity);
                commonDialog.setTitleText("温馨提示")
                        .setContentText("不提交未识别的学生卷\n请从扫描卷中取出未提交的学生卷\n重新扫描")
                        .setPositiveText("知道了")
                        .setOnDialogClickListener(new CommonDialog.OnDialogClickListener() {
                            @Override
                            public void onConfirm() {
                                commonDialog.dismiss();
                                // 点击确认
                                scanActivity.activityScanBinding.bannerPaperGallery.getRoot().setVisibility(View.GONE);
                                /*scanBinding.llScanMode.setVisibility(View.VISIBLE);*/
                                scanActivity.rotateAnimator.start();
                                scanActivity.activityScanBinding.recognitionRl.setVisibility(View.VISIBLE);
                                // 跳过学生姓名旋转
                                checkError(scanActivity.scanIdList);
                            }
                        });
                commonDialog.show();
            }
        });
    }

    /**
     * 确定
     */
    private void ensure() {
        scanActivity.activityScanBinding.bannerPaperGallery.bannerPaperGalleryMbEnsure.setOnClickListener(view -> {
            int emptyCount = 0;
            int firstEmptyIndex = -1;
            // 遍历检查
            for (int i = 0; i < unRecStuPaperBeanList.size(); i++) {
                if (unRecStuPaperBeanList.get(i).getSelectStu() == null) {
                    emptyCount++;
                    if (firstEmptyIndex == -1) {
                        firstEmptyIndex = i;
                    }
                }
            }
            // 全部未匹配
            if (emptyCount == unRecStuPaperBeanList.size()) {
                CommonDialog commonDialogAll = new CommonDialog(scanActivity);
                commonDialogAll.setTitleText("提示")
                        .setContentText("当前所有学生卷都未匹配")
                        .setPositiveText("全部放弃")
                        .setNegativeText("返回匹配")
                        .setShowNegative(true)
                        .setCancelable(true);
                commonDialogAll.setOnDialogClickListener(new CommonDialog.OnDialogClickListener() {
                    @Override
                    public void onCancel() {
                        commonDialogAll.dismiss();
                        scanActivity.activityScanBinding.bannerPaperGallery.paperBanner.setCurrentItem(0);
                    }

                    @Override
                    public void onConfirm() {
                        commonDialogAll.dismiss();
                        scanActivity.activityScanBinding.bannerPaperGallery.getRoot().setVisibility(View.GONE);
                        /*scanBinding.llScanMode.setVisibility(View.VISIBLE);*/
                        scanActivity.rotateAnimator.start();
                        scanActivity.activityScanBinding.recognitionRl.setVisibility(View.VISIBLE);
                        // 跳过学生姓名旋转
                        checkError(scanActivity.scanIdList);
                    }
                });
                commonDialogAll.show();
                return;
            }
            // 部分未匹配
            if (emptyCount > 0) {
                CommonDialog commonDialogPart = new CommonDialog(scanActivity);
                commonDialogPart.setTitleText("提示")
                        .setContentText("存在未匹配的学生卷，确定提交？")
                        .setPositiveText("确定")
                        .setNegativeText("取消")
                        .setShowNegative(true)
                        .setCancelable(true);
                int finalFirstEmptyIndex = firstEmptyIndex;
                commonDialogPart.setOnDialogClickListener(new CommonDialog.OnDialogClickListener() {
                    @Override
                    public void onCancel() {
                        commonDialogPart.dismiss();
                        scanActivity.activityScanBinding.bannerPaperGallery.paperBanner.setCurrentItem(finalFirstEmptyIndex);
                    }

                    @Override
                    public void onConfirm() {
                        commonDialogPart.dismiss();
                        submit();
                    }
                });
                commonDialogPart.show();
                return;
            }
            // 全部正常
            submit();
        });
    }

    /**
     * 显示班级名称和学生
     *
     * @param classInfoList     班级信息数据集
     * @param unRecStuPaperBean 未识别学生试卷
     */
    private void showClassNameAndStudent(List<ClassInfo> classInfoList, UnRecStuPaperBean unRecStuPaperBean) {
        if (ListUtils.listIsNotEmpty(classInfoList)) {
            // 已缓存作业对应的班级和学生
            showClassNameAndStudentList(null);
        } else {
            // 未缓存作业对应的班级和学生 -> 查询班级和学生
            scanActivity.scanViewModel.queryClassAndStudent(scanActivity, unRecStuPaperBean.getHomeworkId(), unRecStuPaperBean.getScanId());
        }
    }

    /**
     * 显示班级名称和列表
     *
     * @param classInfoList 班级信息数据集
     */
    private void showClassNameAndList(List<ClassInfo> classInfoList) {
        if (currentSelectClassInfo == null) {
            UnRecStuPaperBean unRecStuPaperBean = unRecStuPaperBeanList.get(currentSelectUnidentifyExamPaperPosition);
            if (areClassIdentify(classInfoList, unRecStuPaperBean)) {
                currentSelectClassInfo = getClassInfoIdentify(classInfoList, unRecStuPaperBean);
            } else {
                ClassInfo classInfo = getFirstClassInfoIdentifyBaseOnUnidentifyExamPaper(classInfoList, unRecStuPaperBeanList);
                currentSelectClassInfo = (null == classInfo) ? classInfoList.get(0) : classInfo;
            }
        }
        scanActivity.activityScanBinding.bannerPaperGallery.setSelectClass(currentSelectClassInfo);
        if (classAdapter == null) {
            classAdapter = new ClassAdapter(scanActivity, classInfoList);
            classAdapter.getMutableLiveData().observe(scanActivity, classInfo -> {
                scanActivity.activityScanBinding.bannerPaperGallery.listViewClass.setVisibility(View.GONE);
                showClassNameAndStudentList(classInfo);
            });
            classAdapter.setSelectClass(currentSelectClassInfo);
            scanActivity.activityScanBinding.bannerPaperGallery.setClassAdapter(classAdapter);
        } else {
            classAdapter.setList(classInfoList);
            classAdapter.setSelectClass(currentSelectClassInfo);
            classAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 显示班级名称和学生列表
     *
     * @param selectClassInfo 已选班级信息
     */
    void showClassNameAndStudentList(ClassInfo selectClassInfo) {
        UnRecStuPaperBean unRecStuPaperBean = unRecStuPaperBeanList.get(currentSelectUnidentifyExamPaperPosition);
        List<ClassInfo> classInfoList = scanActivity.mHomeClassInfoMap.get(unRecStuPaperBean.getHomeworkId());
        StudentBean hasSelectStudentBean = unRecStuPaperBeanList.get(currentSelectUnidentifyExamPaperPosition).getSelectStu();
        if (selectClassInfo != null) {
            this.currentSelectClassInfo = selectClassInfo;
        } else {
            if ((hasSelectStudentBean != null) && ListUtils.listIsNotEmpty(classInfoList)) {
                scanActivity.activityScanBinding.bannerPaperGallery.tvStudentName.setText(hasSelectStudentBean.getStudentName());
                for (ClassInfo classInfo : classInfoList) {
                    if (Objects.equals(classInfo.getClassId(), hasSelectStudentBean.getClassId())) {
                        this.currentSelectClassInfo = classInfo;
                        break;
                    }
                }
            } else {
                if (ListUtils.listIsNotEmpty(classInfoList)) {
                    if (areClassIdentify(classInfoList, unRecStuPaperBean)) {
                        this.currentSelectClassInfo = getClassInfoIdentify(classInfoList, unRecStuPaperBean);
                    } else {
                        ClassInfo classInfo = getFirstClassInfoIdentifyBaseOnUnidentifyExamPaper(classInfoList, unRecStuPaperBeanList);
                        this.currentSelectClassInfo = (null == classInfo) ? classInfoList.get(0) : classInfo;
                    }
                }
                scanActivity.activityScanBinding.bannerPaperGallery.tvStudentName.setText("");
            }
        }
        scanActivity.activityScanBinding.bannerPaperGallery.setSelectClass(this.currentSelectClassInfo);

        List<StudentBean> studentBeanList = null;
        if (null != this.currentSelectClassInfo) {
            studentBeanList = this.currentSelectClassInfo.getStuList();
        }

        if (classificationAdapter == null) {
            // 添加条目间距
            if (scanActivity.activityScanBinding.bannerPaperGallery.bannerPaperGalleryRv.getItemDecorationCount() == 0) {
                scanActivity.activityScanBinding.bannerPaperGallery.bannerPaperGalleryRv.addItemDecoration(new LinearLayoutVerticalSpaceItemDecoration(DensityUtils.dipToPxByInt(4), false));
            }
            // 设置布局管理器
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(scanActivity);
            scanActivity.activityScanBinding.bannerPaperGallery.bannerPaperGalleryRv.setLayoutManager(linearLayoutManager);
            // 条目高度固定时优化
            scanActivity.activityScanBinding.bannerPaperGallery.bannerPaperGalleryRv.setHasFixedSize(true);
            // 适配器
            classificationAdapter = new ClassificationAdapter();
            classificationAdapter.attachRecyclerView(scanActivity.activityScanBinding.bannerPaperGallery.bannerPaperGalleryRv);
            classificationAdapter.setOnSelectListener(new ClassificationAdapter.OnSelectListener() {
                @Override
                public void onSelect(StudentBean studentBean, int position) {
                    Log.d(TAG, "选中 " + studentBean.getStudentName() + " - " + position);
                    // 未放弃状态下可操作
                    if (unRecStuPaperBeanList.get(currentSelectUnidentifyExamPaperPosition).getHandleFlag() != 2) {
                        unRecStuPaperBeanList.get(currentSelectUnidentifyExamPaperPosition).setHandleFlag(1);
                        scanActivity.mImageTitleAdapter.notifyItemChanged(currentSelectUnidentifyExamPaperPosition);

                        studentBean.setClassId(UnRecognizeController.this.currentSelectClassInfo.getClassId());
                        studentBean.setClassName(UnRecognizeController.this.currentSelectClassInfo.getClassName());
                        unRecStuPaperBeanList.get(currentSelectUnidentifyExamPaperPosition).setSelectStu(studentBean);
                        scanActivity.activityScanBinding.bannerPaperGallery.tvStudentName.setText(studentBean.getStudentName());
                    }
                    unRecognizeHint();
                }

                @Override
                public void onUnSelect(StudentBean studentBean, int position) {
                    Log.d(TAG, "取消选中 " + studentBean.getStudentName() + " - " + position);
                    // 未放弃状态下可操作
                    if (unRecStuPaperBeanList.get(currentSelectUnidentifyExamPaperPosition).getHandleFlag() != 2) {
                        unRecStuPaperBeanList.get(currentSelectUnidentifyExamPaperPosition).setHandleFlag(0);
                        scanActivity.mImageTitleAdapter.notifyItemChanged(currentSelectUnidentifyExamPaperPosition);

                        unRecStuPaperBeanList.get(currentSelectUnidentifyExamPaperPosition).setSelectStu(null);
                        scanActivity.activityScanBinding.bannerPaperGallery.tvStudentName.setText("");
                    }
                    unRecognizeHint();
                }
            });
            // 设置数据
            classificationAdapter.setData(studentBeanList, 0);
            // 设置适配器
            scanActivity.activityScanBinding.bannerPaperGallery.bannerPaperGalleryRv.setAdapter(classificationAdapter);
        } else {
            // 设置数据
            classificationAdapter.setData(studentBeanList, 0);
        }
        // 关键修复
        scanActivity.activityScanBinding.bannerPaperGallery.bannerPaperGalleryRv.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        scanActivity.activityScanBinding.bannerPaperGallery.bannerPaperGalleryRv.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        if (unRecStuPaperBeanList.get(currentSelectUnidentifyExamPaperPosition).getSelectStu() != null) {
                            int id = unRecStuPaperBeanList.get(currentSelectUnidentifyExamPaperPosition)
                                    .getSelectStu()
                                    .getStudentId();
                            classificationAdapter.focus(id, true);
                        }
                    }
                }
        );
    }

    /**
     * 是否识别到班级
     * <p>
     * 根据 classId 判定
     *
     * @param classInfoList     班级信息数据集
     * @param unRecStuPaperBean 未识别学生试卷
     * @return 是否识别到班级
     */
    private boolean areClassIdentify(List<ClassInfo> classInfoList, UnRecStuPaperBean unRecStuPaperBean) {
        if (null == unRecStuPaperBean.getClassId()) {
            return false;
        }
        for (ClassInfo classInfo : classInfoList) {
            if (Objects.equals(classInfo.getClassId(), unRecStuPaperBean.getClassId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取识别到的班级信息
     *
     * @param classInfoList     班级信息数据集
     * @param unRecStuPaperBean 未识别学生试卷
     * @return 识别到的班级信息
     */
    private ClassInfo getClassInfoIdentify(List<ClassInfo> classInfoList, UnRecStuPaperBean unRecStuPaperBean) {
        for (ClassInfo classInfo : classInfoList) {
            if (Objects.equals(classInfo.getClassId(), unRecStuPaperBean.getClassId())) {
                return classInfo;
            }
        }
        return null;
    }

    /**
     * 基于未识别试卷获取第一个识别到的班级信息
     *
     * @param classInfoList         班级信息数据集
     * @param unRecStuPaperBeanList 未识别学生试卷数据集
     * @return 第一个识别到的班级信息
     */
    private ClassInfo getFirstClassInfoIdentifyBaseOnUnidentifyExamPaper(List<ClassInfo> classInfoList, List<UnRecStuPaperBean> unRecStuPaperBeanList) {
        List<UnRecStuPaperBean> reverseList = new ArrayList<>(unRecStuPaperBeanList);
        for (UnRecStuPaperBean unRecStuPaperBean : reverseList) {
            for (ClassInfo classInfo : classInfoList) {
                if (Objects.equals(unRecStuPaperBean.getClassId(), classInfo.getClassId())) {
                    return classInfo;
                }
            }
        }
        return null;
    }

    /**
     * 检查错误
     */
    void checkError(List<String> list) {
        scanActivity.scanViewModel.checkError(list);
    }

    /**
     * 提交
     */
    private void submit() {
        List<UnrecognizedClassStu> unrecognizedClassStuList = new ArrayList<>();
        for (int i = 0; i < unRecStuPaperBeanList.size(); i++) {
            UnRecStuPaperBean unRecStuPaperBean = unRecStuPaperBeanList.get(i);
            if ((unRecStuPaperBean.getHandleFlag() != 2) && (unRecStuPaperBean.getSelectStu() != null)) {
                UnrecognizedClassStu unrecognizedClassStu = new UnrecognizedClassStu();
                unrecognizedClassStu.setScanId(unRecStuPaperBeanList.get(i).getScanId());
                StudentBean studentBean = unRecStuPaperBeanList.get(i).getSelectStu();
                unrecognizedClassStu.setClassId(studentBean.getClassId());
                unrecognizedClassStu.setClassName(studentBean.getClassName());
                unrecognizedClassStu.setStudentId(studentBean.getStudentId());
                unrecognizedClassStu.setStudentName(studentBean.getStudentName());
                unrecognizedClassStuList.add(unrecognizedClassStu);
            }
        }
        if (!Utils.doubleClick()) {
            LoadDialog.show(scanActivity, "正在加载...");
            saveUnrecognizedClassStu(unrecognizedClassStuList);
        }
    }

    /**
     * 手动匹配学生 - 保存
     *
     * @param unrecognizedClassStuList 已匹配学生列表
     */
    private void saveUnrecognizedClassStu(List<UnrecognizedClassStu> unrecognizedClassStuList) {
        scanActivity.scanViewModel.saveUnrecognizedClassStu(scanActivity, JSON.toJSONString(unrecognizedClassStuList));
    }

    /**
     * 未识别提示
     */
    private void unRecognizeHint() {
        int size = unRecStuPaperBeanList.size();
        for (UnRecStuPaperBean unRecStuPaperBean : unRecStuPaperBeanList) {
            if (unRecStuPaperBean.getSelectStu() != null) {
                size--;
            }
        }
        if (size > 0) {
            scanActivity.activityScanBinding.bannerPaperGallery.tvErrorNum.setText(buildUnRecognizeHintSpannableString(size));
        } else {
            scanActivity.activityScanBinding.bannerPaperGallery.tvErrorNum.setText("学生卷匹配完成");
        }
    }

    /**
     * 构建未识别提示 SpannableString
     *
     * @param number 数量
     * @return 未识别提示 SpannableString
     */
    private SpannableString buildUnRecognizeHintSpannableString(int number) {
        // 创建一个 SpannableString 对象
        SpannableString spannableString = new SpannableString(number + "张学生卷未识别，请操作匹配");
        spannableString.setSpan(
                new ForegroundColorSpan(Color.parseColor("#1F7FEE")),
                0, // 开始索引
                String.valueOf(number).length() + 1, // 结束索引
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        return spannableString;
    }
}