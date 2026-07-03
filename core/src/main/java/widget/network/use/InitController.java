package widget.network.use;

import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.View;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.alibaba.fastjson.JSON;
import com.qtone.scandemo.module.bigimage.BigImageCheckErrorActivity;
import com.qtone.scandemo.module.bigimage.BigImageIdentifyActivity;
import com.qtone.scandemo.module.bigimage.BigImageUnIdentifyActivity;
import com.qtone.scandemo.module.hg.bean.EventDefStateEnum;
import com.qtone.scandemo.module.hg.kit.HGScanManagerKit;
import com.qtone.scandemo.module.scan.ScanActivity;
import com.qtone.scandemo.module.scan.adapter.AnomalousAdapter;
import com.qtone.scandemo.module.scan.adapter.IdentifyAdapter;
import com.qtone.scandemo.module.scan.adapter.UnIdentifyAdapter;
import com.qtone.scandemo.module.scan.bean.ScanCheckErrorBean;
import com.qtone.scandemo.module.scan.bean.ScanResultBean;
import com.qtone.scandemo.module.scan.bean.UnIdentifyBean;
import com.qtone.scandemo.module.scan.bean.UnRecStuPaperBean;
import com.qtone.scandemo.module.scan.broadcastreceiver.ScreenStatusReceiver;
import com.qtone.scandemo.module.scan.kit.ScanActivityKit;
import com.qtone.scandemo.module.scan.widget.ScanEndDialog;
import com.qtone.scandemo.util.activity.ActivityManager;
import com.qtone.scandemo.util.datetime.DateUtils;
import com.qtone.scandemo.util.list.ListUtils;
import com.qtone.scandemo.util.livedatabus.LiveDatabus;
import com.qtone.scandemo.util.stack.StackTrace;
import com.qtone.scandemo.util.toast.ToastUtil;
import com.qtone.scandemo.util.view.LoadingViewManager;
import com.qtone.scandemo.widget.dialog.LoadDialog;

import org.xutils.common.util.LogUtil;

import java.io.IOException;
import java.util.List;

import timber.log.Timber;

/**
 * Created on 2026/5/20.
 *
 * @author 郑少鹏
 * @desc 初始化控制器
 * <p>
 * 同 {@link ScanActivityKit} 双向引用
 */
public class InitController {
    /**
     * TAG
     */
    private static final String TAG = InitController.class.getSimpleName();
    /**
     * 扫描页
     */
    private final ScanActivity scanActivity;
    /**
     * 扫描页配套原件
     */
    private final ScanActivityKit scanActivityKit;

    /**
     * constructor
     *
     * @param scanActivity    扫描页
     * @param scanActivityKit 扫描页配套原件
     */
    public InitController(ScanActivity scanActivity, ScanActivityKit scanActivityKit) {
        this.scanActivity = scanActivity;
        this.scanActivityKit = scanActivityKit;
    }

    /**
     * 初始化广播接收器
     */
    public void initBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(EventDefStateEnum.STATE_SELF_SCAN_FINISHED.getState());
        intentFilter.addAction(EventDefStateEnum.STATE_ERROR.getState());
        intentFilter.addAction(EventDefStateEnum.STATE_ERROR_JAM.getState());
        intentFilter.addAction(EventDefStateEnum.STATE_STANDBY.getState());
        intentFilter.addAction(EventDefStateEnum.STATE_PAPER_READY.getState());
        intentFilter.addAction(EventDefStateEnum.STATE_COVER_OPEN.getState());
        intentFilter.addAction(EventDefStateEnum.STATE_SCANNING.getState());
        intentFilter.addAction(EventDefStateEnum.STATE_SELF_PAPER_NOT_STANDBY.getState());
        intentFilter.addAction(EventDefStateEnum.STATE_SELF_PAPER_STANDBY.getState());
        LocalBroadcastManager.getInstance(scanActivity).registerReceiver(scanActivity.scanEventBroadcastReceiver, intentFilter);
        // listen to the screen
        scanActivity.screenStatusReceiver = new ScreenStatusReceiver();
        IntentFilter screenStatusIF = new IntentFilter();
        screenStatusIF.addAction(Intent.ACTION_SCREEN_ON);
        screenStatusIF.addAction(Intent.ACTION_SCREEN_OFF);
        scanActivity.screenStatusReceiver.setScreenListener(new ScreenStatusReceiver.ScreenStatusListener() {
            @Override
            public void onScreenOn() {
                Log.d(TAG, "onScreenOn");
            }

            @Override
            public void onScreenOff() {
                HGScanManagerKit.getInstance().stopScan();
                Log.d(TAG, "调用方 - stopScan() = " + StackTrace.getCaller());
                Log.d(TAG, "onScreenOff");
            }
        });
        scanActivity.registerReceiver(scanActivity.screenStatusReceiver, screenStatusIF);
    }

    /**
     * 初始化 LiveData
     * <p>
     * LiveData
     * 具有生命周期感知能力的可观察数据容器
     */
    public void initLiveData() {
        scanActivity.scanViewModel.scanLiveData.observe(scanActivity, baseData -> {
            Log.i(TAG, "initLiveData - 到这了");
            if (baseData != null) {
                String errMsg = "";
                if (baseData.getFailData() != null) {
                    UnIdentifyBean unIdentifyBean = new UnIdentifyBean();
                    unIdentifyBean.setCode(baseData.getFailData().getCode());
                    unIdentifyBean.setScanErrorMsg(baseData.getFailData().getMsg());
                    errMsg = unIdentifyBean.getScanErrorMsg();
                } else {
                    List<ScanResultBean.DataBean> list = baseData.getSuccessData();
                    for (int i = 0; i < list.size(); i++) {
                        scanActivity.scanIdList.add(list.get(i).getScanId());
                    }
                }
                scanActivity.uploadScanCallbackCount = scanActivity.uploadScanCallbackCount + 1;
                IOException ioException = new IOException(JSON.toJSONString(baseData));
                String log = String.format("scanLiveData 回调: " + "时间: %s, 回调内容: %s", DateUtils.formatDate(System.currentTimeMillis()), ioException.getLocalizedMessage());
                Timber.d(log);
                scanActivityKit.showScanningResult("observe " + errMsg);
            }
        });
        scanActivity.scanViewModel.saveScanLiveData.observe(scanActivity, baseData -> {
            try {
                scanActivity.isSaveScanning = false;
                LoadDialog.dismiss(scanActivity);
                if (baseData != null) {
                    if (baseData.getFailData() != null) {
                        LogUtil.d(baseData.getFailData().getMsg());
                        ToastUtil.showCenter(scanActivity, baseData.getFailData().getMsg() + "(" + baseData.getFailData().getCode() + ")");
                        ActivityManager.getInstance().finishTargetActivity(ScanActivity.class);
                    } else {
                        if (!scanActivity.isDestroyed() && !scanActivity.isFinishing()) {
                            /*if (!TextUtils.isEmpty(baseData.getSuccessData()) && !baseData.getSuccessData().contains("成功"))*/
                            ScanEndDialog scanEndDialog = new ScanEndDialog();
                            scanEndDialog.setScanEndDialogListener(() -> {
                                /*if (scanEndDialog.isAdded() && scanEndDialog.isResumed()) {
                                    scanEndDialog.dismissAllowingStateLoss();
                                }*/
                                // 直接关闭扫描页 -> 避免页面残留
                                ActivityManager.getInstance().finishTargetActivity(ScanActivity.class);
                            });
                            if (!scanEndDialog.isAdded()) {
                                // 只 show 一次并加上状态判断防止重复添加
                                scanEndDialog.show(scanActivity.getSupportFragmentManager(), ScanEndDialog.class.getSimpleName());
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        scanActivity.scanViewModel.checkErrorLiveData.observe(scanActivity, baseData -> {
            if (baseData != null) {
                scanActivity.scanningErrorIndex = 0;
                scanActivity.scanCheckSuccessList.clear();
                scanActivity.scanCheckErrorList.clear();
                scanActivity.scanResultSuccessList.clear();
                scanActivity.unIdentifyList.clear();
                if (baseData.getFailData() != null) {
                    ToastUtil.showCenter(scanActivity, baseData.getFailData().getMsg() + "(" + baseData.getFailData().getCode() + ")");
                    ActivityManager.getInstance().finishTargetActivity(ScanActivity.class);
                } else {
                    scanActivityKit.setLayoutVisibility(null, 0,
                            null, 0,
                            scanActivity.activityScanBinding.recognitionRl, View.GONE,
                            scanActivity.activityScanBinding.scanningAnomalousRl, View.VISIBLE);
                    scanActivity.scanCheckList = baseData.getSuccessData();
                    Log.i(TAG, "initLiveData: " + scanActivity.scanCheckList.size());
                    // 旧逻辑说明
                    // 进入手动匹配页面，进行手动匹配，然后提交。
                    // 提交后调用查询未识别试卷接口
                    // 如果未识别试卷接口返回未识别试卷数量 0
                    // 那么接着调用检查错误接口
                    // 调用检查错误接口，拿到数据，汇总正确和错误数据。
                    // 接着调用 setCheckErrorData()
                    // 在 setCheckErrorData() 中如果发现存在错误数据，那么展示【是 / 否】页面。
                    // 如果不存在错误数据，那么接着调用 showScanResult() 展示【未 / 已识别】页面，注意此时只展示下半部分的已识别数据。
                    // 而【未 / 已识别】页面中上半部分的未识别数据，是根据在【是 / 否】页面中操作后产生的数据来展示。
                    for (int i = 0; i < scanActivity.scanCheckList.size(); i++) {
                        if (scanActivity.scanCheckList.get(i).getScanErrorInfoList().size() > 0 && scanActivity.scanCheckList.get(i).getScanErrorInfoList().get(0).getScanResultCode() == 0) {
                            scanActivity.scanCheckSuccessList.add(scanActivity.scanCheckList.get(i));
                        } else {
                            scanActivity.scanCheckErrorList.add(scanActivity.scanCheckList.get(i));
                        }
                    }
                    scanActivityKit.checkErrorDataController.setCheckErrorData();
                }
            }
        });
        /*scanActivity.queryUnidentifyExamPaperViewModel.queryUnidentifyExamPaperLiveData.observe(scanActivity, baseData -> {
            if (baseData != null) {
                if (baseData.getFailData() != null) {
                    ToastUtil.showCenter(scanActivity, baseData.getFailData().getMsg() + "(" + baseData.getFailData().getCode() + ")");
                    ActivityManager.getInstance().finishTargetActivity(ScanActivity.class);
                } else {
                    List<UnRecStuPaperBean> unRecStuPaperBeanList = baseData.getSuccessData();
                    LogUtil.d("未识别试卷 - " + unRecStuPaperBeanList.size() + " || " + JSON.toJSONString(unRecStuPaperBeanList));
                    if (unRecStuPaperBeanList.size() > 0) {
                        scanActivityKit.unRecognizeController.showUnidentifyExamPaper(unRecStuPaperBeanList);
                    } else {
                        // TODO: 2026/6/23 逻辑变更
                        // 检查错误
                        *//*
                        // 保存扫描结果
                        scanActivity.scanViewModel.saveScanResult(scanActivity);
                    }
                }
            }
        });*/
        scanActivity.queryUnidentifyExamPaperViewModel.liveData.observe(scanActivity, result -> {
            if (result == null) {
                ActivityManager.getInstance().finishTargetActivity(ScanActivity.class);
                return;
            }
            if (!result.success) {
                // 1. 请求失败分支 (网络异常、HTTP 错误或后端返回的业务错误)
                ToastUtil.showCenter(scanActivity, result.message + "(" + result.code + ")");
                ActivityManager.getInstance().finishTargetActivity(ScanActivity.class);
                return;
            }
            // 2. 请求成功分支
            List<UnRecStuPaperBean> unRecStuPaperBeanList = result.data;
            if (ListUtils.listIsNotEmpty(unRecStuPaperBeanList)) {
                LogUtil.d("未识别试卷 - " + unRecStuPaperBeanList.size() + " || " + JSON.toJSONString(unRecStuPaperBeanList));
                scanActivityKit.unRecognizeController.showUnidentifyExamPaper(unRecStuPaperBeanList);
            } else {
                // TODO: 2026/6/23 逻辑变更
                /*scanActivity.scanViewModel.checkError(scanActivity.scanIdList);*/
                // 保存扫描结果
                scanActivity.scanViewModel.saveScanResult(scanActivity);
            }
        });
        scanActivity.scanViewModel.mClassInfoLiveData.observe(scanActivity, baseData -> {
            if (baseData != null) {
                LoadingViewManager.toggle(false, scanActivity.activityScanBinding.bannerPaperGallery.bannerPaperGalleryRv, scanActivity.activityScanBinding.bannerPaperGallery.bannerPaperGalleryLlLoading, scanActivity.activityScanBinding.bannerPaperGallery.bannerPaperGalleryAcivLoading);
                if (baseData.getFailData() != null) {
                    ToastUtil.showCenter(scanActivity, baseData.getFailData().getMsg() + "(" + baseData.getFailData().getCode() + ")");
                    ActivityManager.getInstance().finishTargetActivity(ScanActivity.class);
                } else {
                    scanActivityKit.unRecognizeController.showClassNameAndStudentList(null, true);
                }
            }
        });
        scanActivity.scanViewModel.mSaveUnrecognizedClassStuLiveData.observe(scanActivity, baseData -> {
            if (baseData != null) {
                if (baseData.getFailData() != null) {
                    ToastUtil.showCenter(scanActivity, "保存失败，请稍后重试！");
                } else {
                    LogUtil.d("saveUnrecognizedClassStuLiveData: " + baseData.getSuccessData());
                    scanActivity.activityScanBinding.bannerPaperGallery.getRoot().setVisibility(View.GONE);
                    scanActivity.rotateAnimator.start();
                    scanActivity.activityScanBinding.recognitionRl.setVisibility(View.VISIBLE);
                    // TODO: 2026/6/23 逻辑变更
                    // 检查错误
                    /*scanActivity.scanViewModel.checkError(scanActivity.scanIdList);*/
                    // 查询未识别试卷
                    scanActivity.queryUnidentifyExamPaperViewModel.queryUnidentifyExamPaper();
                }
            }
        });
    }

    /**
     * 初始化适配器
     */
    public void initAdapter() {
        scanActivity.unIdentifyAdapter = new UnIdentifyAdapter();
        scanActivity.identifyAdapter = new IdentifyAdapter();
        scanActivity.anomalousAdapter = new AnomalousAdapter();
        scanActivity.activityScanBinding.setIdentifyAdapter(scanActivity.identifyAdapter);
        scanActivity.activityScanBinding.setUnIdentifyAdapter(scanActivity.unIdentifyAdapter);
        scanActivity.activityScanBinding.setAnomalousAdapter(scanActivity.anomalousAdapter);
        scanActivity.unIdentifyAdapter.setItemClickListener(new UnIdentifyAdapter.ItemClickListener() {
            @Override
            public void onClick(UnIdentifyBean unIdentifyBean, int position) {
                LiveDatabus.getInstance().withSticky("unIdentifyList").postValue(unIdentifyBean);
                Intent intent = new Intent(scanActivity, BigImageUnIdentifyActivity.class);
                intent.putExtra("Position", position);
                scanActivity.startActivity(intent);
            }

            @Override
            public void onLongClick() {

            }
        });
        scanActivity.identifyAdapter.setItemClickListener(new IdentifyAdapter.ItemClickListener() {
            @Override
            public void onClick(ScanCheckErrorBean.DataBean.ScanListBean scanListBean, int outerPosition, int innerPosition) {
                Log.i(TAG, "identifyAdapter onClick: " + outerPosition + " innerPosition =+ " + innerPosition);
                Log.i(TAG, "identifyAdapter onClick size: " + scanActivity.scanResultSuccessList.size());
                LiveDatabus.getInstance().withSticky("identifyList").postValue(scanActivity.scanResultSuccessList.get(outerPosition));
                Intent intent = new Intent(scanActivity, BigImageIdentifyActivity.class);
                intent.putExtra("innerPosition", innerPosition);
                scanActivity.startActivity(intent);
            }

            @Override
            public void onLongClick() {

            }
        });
        scanActivity.anomalousAdapter.setItemClickListener(new AnomalousAdapter.ItemClickListener() {
            @Override
            public void onClick(ScanCheckErrorBean.DataBean.ScanListBean scanListBean, int position) {
                Log.i(TAG, "anomalousAdapter onClick: " + position);
                Log.i(TAG, "identifyAdapter onClick size: " + scanActivity.scanCheckErrorList.size() + " scanningErrorIndex = " + scanActivity.scanningErrorIndex);
                LiveDatabus.getInstance().withSticky("checkErrorList").postValue(scanActivity.scanCheckErrorList.get(scanActivity.scanningErrorIndex));
                Intent intent = new Intent(scanActivity, BigImageCheckErrorActivity.class);
                intent.putExtra("Position", position);
                scanActivity.startActivity(intent);
            }

            @Override
            public void onLongClick() {

            }
        });
    }
}