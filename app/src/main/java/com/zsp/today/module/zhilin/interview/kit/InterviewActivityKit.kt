package com.zsp.today.module.zhilin.interview.kit

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.FullScreenCarouselStrategy
import com.zsp.today.R
import util.list.ListUtils
import widget.carousel.CarouselItem
import widget.carousel.CarouselKit
import widget.carousel.CarouselType
import widget.screen.kit.ScreenHandleKit
import widget.screen.listener.ScreenHandleListener

/**
 * Created on 2026/3/26.
 * @author 郑少鹏
 * @desc 面试页配套原件
 */
class InterviewActivityKit {
    private var carouselKit: CarouselKit? = null
    private var defaultSelectCondition: String? = null
    private val conditionTemporary = arrayOfNulls<String>(1)
    private var carouselItemList: MutableList<CarouselItem>? = null
    private var carouselItemCarouselTitleList: MutableList<String>? = null

    init {
        carouselItemList = prepareCarouselItemList()
        carouselItemCarouselTitleList = prepareCarouselItemCarouselTitleList()
    }

    /**
     * 轮播
     * @param recyclerView RecyclerView
     */
    fun carousel(recyclerView: RecyclerView) {
        carouselKit = CarouselKit().apply {
            // 执行
            execute(
                recyclerView,
                carouselItemList,
                FullScreenCarouselStrategy(),
                RecyclerView.VERTICAL,
                false,
                CarouselLayoutManager.ALIGNMENT_CENTER,
                false,
                0,
                0,
                0,
                8,
                false,
                null
            )
            // 监听当前位置
            this.observeCurrentPosition(
                recyclerView
            ) { position: Int ->
                defaultSelectCondition = carouselItemCarouselTitleList!![position]
            }
        }
    }

    /**
     * 显示面试要点列表
     * @param appCompatActivity 活动
     * @param recyclerView      RecyclerView
     */
    fun showInterviewPointList(appCompatActivity: AppCompatActivity, recyclerView: RecyclerView) {
        // ScreenHandleKit
        val screenHandleKit = ScreenHandleKit(appCompatActivity).apply kit@{
            // 打包集合条件
            packListConditions(
                appCompatActivity.getString(R.string.interviewPoint),
                3,
                true,
                carouselItemCarouselTitleList!!
            )
            // 默选
            defaultSelect(
                appCompatActivity.getString(R.string.interviewPoint),
                defaultSelectCondition ?: carouselItemCarouselTitleList!![0]
            )
            // 关联
            associate()
            // 设筛选操作监听
            setScreenHandleListener(object : ScreenHandleListener {
                /**
                 * 点
                 * @param view           视图
                 * @param classification 类别
                 * @param condition      条件
                 * @param selected       选否
                 */
                override fun click(
                    view: View?, classification: String?, condition: String?, selected: Boolean
                ) {
                    conditionTemporary[0] = condition
                }

                /**
                 * 重置
                 */
                override fun reset() {
                    this@kit.reset()
                }

                /**
                 * 确定
                 */
                override fun ensure() {
                    this@kit.dismiss()
                    defaultSelectCondition = conditionTemporary[0]
                    carouselKit!!.scrollToPosition(
                        recyclerView, ListUtils.getTargetIndex(
                            carouselItemCarouselTitleList, conditionTemporary[0]
                        ), false
                    )
                }
            })
        }
        // 显示
        screenHandleKit.show()
    }

    /**
     * 准备轮播条目集
     * @return 轮播条目集
     */
    private fun prepareCarouselItemList(): MutableList<CarouselItem> {
        val carouselItemList: MutableList<CarouselItem> = ArrayList(66)
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE,
                R.drawable.interview_point_1,
                "Android\n坐标系",
                "Android 坐标系"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE,
                R.drawable.interview_point_2,
                "Glide\n三级缓存",
                "Glide 三级缓存"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE, R.drawable.interview_point_3, "TCP\n三次握手", "TCP 三次握手"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE,
                R.drawable.interview_point_4,
                "接口\ndefault",
                "接口 default 修饰"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE, R.drawable.interview_point_5, "PPI\nDPI", "PPI DPI"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE,
                R.drawable.interview_point_6,
                "final\nfinally finalize",
                "final finally finalize"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE,
                R.drawable.interview_point_7,
                "String\nfinal 一",
                "String final 一"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE,
                R.drawable.interview_point_8,
                "String\nfinal 二",
                "String final 二"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE, R.drawable.interview_point_9, "Java\n三兄弟", "Java 三兄弟"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE, R.drawable.interview_point_10, "事件\n分发", "事件分发"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE, R.drawable.interview_point_11, "加密", "加密"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE, R.drawable.interview_point_12, "加解密\n加验签", "加解密 加验签"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE, R.drawable.interview_point_13, "图片\n加载", "图片加载"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE, R.drawable.interview_point_14, "sleep\nwait", "sleep wait"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE, R.drawable.interview_point_15, "View\n绘制", "View 绘制"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE, R.drawable.interview_point_16, "MVC P\nMVVM", "MVC MVP MVVM"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE, R.drawable.interview_point_17, "线程\n通信", "线程通信"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE, R.drawable.interview_point_18, "进程\n通信", "进程通信"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE,
                R.drawable.interview_point_19,
                "buffer\nbuilder",
                "buffer builder"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE, R.drawable.interview_point_20, "内存\n泄露", "内存泄露"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE, R.drawable.interview_point_21, "Handler", "Handler"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE, R.drawable.interview_point_22, "自定义\nView", "自定义 View"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE,
                R.drawable.interview_point_23,
                "volatile\nsynchronized",
                "synchronized volatile"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE,
                R.drawable.interview_point_24,
                "lock\nsynchronized",
                "synchronized lock"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE, R.drawable.interview_point_25, "消息\n同异步", "同异步消息"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE, R.drawable.interview_point_26, "RxJava", "RxJava"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE, R.drawable.interview_point_27, "Rx\nAndroid", "RxAndroid"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE, R.drawable.interview_point_28, "Thread\nLocal", "ThreadLocal"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE, R.drawable.interview_point_29, "虚拟机", "虚拟机"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE, R.drawable.interview_point_30, "线程池", "线程池"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE, R.drawable.interview_point_31, "List", "List"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE,
                R.drawable.interview_point_32,
                "equals\nhashCode",
                "equals hashCode"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE,
                R.drawable.interview_point_33,
                "非 / 静态\n内部类",
                "非 / 静态内部类"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE, R.drawable.interview_point_34, "局部\n内部类", "局部内部类"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE, R.drawable.interview_point_35, "匿名\n内部类", "匿名内部类"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE, R.drawable.interview_point_36, "TCP\nUDP", "TCP UDP"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE, R.drawable.interview_point_37, "滑动\n冲突", "滑动冲突"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE, R.drawable.interview_point_38, "JIT\nAOT", "JIT AOT"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE, R.drawable.interview_point_39, "APP\n加固", "APP 加固"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE, R.drawable.interview_point_40, "TCP\n拥塞控制", "TCP 拥塞控制"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE,
                R.drawable.interview_point_41,
                "Activity\n启动一",
                "Activity 启动一"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE,
                R.drawable.interview_point_42,
                "Activity\n启动二",
                "Activity 启动二"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE, R.drawable.interview_point_43, "类\n加载一", "类加载一"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE, R.drawable.interview_point_44, "类\n加载二", "类加载二"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE,
                R.drawable.interview_point_45,
                "ListView\nRecyclerView",
                "ListView RecyclerView"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE, R.drawable.interview_point_46, "冷热温\n启动", "冷热温启动"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE,
                R.drawable.interview_point_47,
                "JVM\n内存结构一",
                "JVM 内存结构一"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE,
                R.drawable.interview_point_48,
                "JVM\n内存结构二",
                "JVM 内存结构二"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE, R.drawable.interview_point_49, "进程\n线程", "进程 线程"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE,
                R.drawable.interview_point_50,
                "内部类\n持有外部引用",
                "内部类持有外部引用"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE, R.drawable.interview_point_51, "APK\nAAB", "APK AAB"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE, R.drawable.interview_point_52, "Java\n枚举", "Java 枚举"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE, R.drawable.interview_point_53, "数组\n内存泄露", "数组内存泄露"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE, R.drawable.interview_point_54, "JAR\nAAR", "JAR AAR"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE, R.drawable.interview_point_55, "RESTful\nAPI", "RESTful API"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE,
                R.drawable.interview_point_56,
                "Preference\nScreen",
                "Preferen Screen"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE, R.drawable.interview_point_57, "DEBUG", "DEBUG"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE, R.drawable.interview_point_58, "MVI", "MVI"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE,
                R.drawable.interview_point_59,
                "Flutter\nDartCompose",
                "Flutter Dart Compose"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.IMAGE, R.drawable.interview_point_60, "scale\nType", "scaleType"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.HTML, "file:///android_asset/html/android/git.html", "Git", "Git"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.HTML,
                "file:///android_asset/html/android/operator.html",
                "运算符",
                "运算符"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.HTML,
                "file:///android_asset/html/android/AbstractMethod.html",
                "抽象\n方法",
                "抽象方法"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.HTML,
                "file:///android_asset/html/android/SdkVersion.html",
                "SDK\n版本",
                "SDK 版本"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.HTML,
                "file:///android_asset/html/android/OverloadAndOverride.html",
                "重载\n重写",
                "重载重写"
            )
        )
        carouselItemList.add(
            CarouselItem(
                CarouselType.HTML,
                "file:///android_asset/html/android/ActivityLifecycle.html",
                "声明\n周期",
                "声明周期"
            )
        )
        return carouselItemList
    }

    /**
     * 准备轮播条目轮播标题集
     * @return 轮播条目轮播标题集
     */
    private fun prepareCarouselItemCarouselTitleList(): MutableList<String> {
        val carouselItemCarouselTitleList: MutableList<String> = ArrayList(carouselItemList!!.size)
        for (carouselItem in carouselItemList!!) {
            carouselItemCarouselTitleList.add(carouselItem.getCarouselTitle())
        }
        return carouselItemCarouselTitleList
    }
}