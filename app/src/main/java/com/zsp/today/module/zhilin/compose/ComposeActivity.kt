package com.zsp.today.module.zhilin.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Created on 2026/3/28.
 * @author 郑少鹏
 * @desc COMPOSE 页
 */
class ComposeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeScreen()
        }
    }
}

@Composable
fun ComposeScreen() {
    Column {
        CenterAlignedTopAppBarSelf()
        DynamicGrid()
    }
}

/**
 * 这个 API 还没完全稳定
 * 官方可能会改它
 * 这是 Kotlin 的一种安全机制
 * 强制你承认知道这个 API 可能会变且愿意承担风险
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CenterAlignedTopAppBarSelf() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                // 内容
                text = "COMPOSE",
                // 字体尺寸
                fontSize = 16.sp,
                // 字体粗细
                fontWeight = FontWeight.Bold
            )
        })
}

@Composable
fun DynamicGrid() {
    val itemList = remember { mutableStateListOf("1", "2", "3") }
    Column {
        Button(
            // 点击事件
            onClick = { itemList.add("新增 ${itemList.size}") },
            // 用来描述和改变 Compose 组件外观和行为的修饰器
            modifier = Modifier
                // 高度
                .height(80.dp)
                // 宽度撑满
                .fillMaxWidth()
                // 外边距
                .padding(16.dp, 16.dp, 16.dp, 16.dp), colors = ButtonDefaults.buttonColors(
                // 背景颜色
                containerColor = Color(0xFFBBDEFB),
                // 文字颜色
                contentColor = Color.White
            )
        ) {
            Text(text = "添加")
        }
        LazyVerticalGrid(
            // 每行 3 列
            // LazyVerticalGrid (或 LazyHorizontalGrid) 用来定义网格列 / 行数量和规则的对象
            columns = GridCells.Fixed(3),
            // 横 / 纵向内边距
            // 用来描述容器内部的内边距（padding）的类
            contentPadding = PaddingValues(16.dp, 0.dp, 16.dp, 16.dp),
            // 纵向间距
            // 用来控制布局中子元素如何排列的对象
            verticalArrangement = Arrangement.spacedBy(10.dp),
            // 横向间距
            // 用来控制布局中子元素如何排列的对象
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(itemList.size) { index ->
                // 是一个非常基础又常用的布局容器（Layout）
                // 作用是将子组件叠加放置
                // 类似 Stack (堆叠)
                // 所有子组件默认都从左上角开始绘制
                // 后添加的会覆盖在前面的上面（类似层叠效果）
                Box(
                    modifier = Modifier
                        // 组件宽高比
                        .aspectRatio(1f)
                        // 背景
                        .background(
                            // 背景颜色
                            color = Color(0xFFBBDEFB),
                            // 圆角形状
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp), Alignment.Center
                ) {
                    Text(
                        // 内容
                        text = itemList[index],
                        // 颜色
                        color = Color.White,
                        // 字体粗细
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * @Preview 是 debug / IDE 专属
 * @Preview 只是开发工具
 * release 包里本来就没有
 */
@Preview(showBackground = true)
@Composable
fun PreviewComposeScreen() {
    ComposeScreen()
}