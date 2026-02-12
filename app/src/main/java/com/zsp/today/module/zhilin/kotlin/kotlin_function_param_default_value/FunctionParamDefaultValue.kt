package com.zsp.today.module.zhilin.kotlin.kotlin_function_param_default_value

/**
 * Created on 2022/4/19
 * @author zsp
 * @desc 函数的参数默认值
 */
fun main() {
    printParamsOne(123)
    printParamsTwo(str = "world")
}

fun printParamsOne(num: Int, str: String = "hello") {
    println("num is $num, str is $str")
}

fun printParamsTwo(num: Int = 100, str: String) {
    println("num is $num, str is $str")
}