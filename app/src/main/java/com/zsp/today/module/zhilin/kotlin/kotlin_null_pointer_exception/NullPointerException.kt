package com.zsp.today.module.zhilin.kotlin.kotlin_null_pointer_exception

/**
 * Created on 2022/4/19
 * @author zsp
 * @desc 判空辅助工具
 */
fun main() {
    println(getTextLength(null))
    println(getTextLength("ABC"))
}

fun getTextLength(text: String?) = text?.length ?: 0