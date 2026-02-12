package com.zsp.today.module.zhilin.kotlin.kotlin_set

/**
 * Created on 2022/4/18
 * @author zsp
 * @desc Set 集合的创建与遍历
 */
fun main() {
    val set = setOf("Apple", "Banana", "Orange", "Pear", "Grape")
    for (fruit in set) {
        println(fruit)
    }
}