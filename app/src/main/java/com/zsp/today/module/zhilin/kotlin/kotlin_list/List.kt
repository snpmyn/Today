package com.zsp.today.module.zhilin.kotlin.kotlin_list

/**
 * Created on 2022/4/18
 * @author zsp
 * @desc List 集合的创建与遍历
 */
fun main() {
    valList()
    varList()
}

fun valList() {
    val valListOne = listOf("Apple", "Banana", "Orange", "Pear", "Grape")
    for (fruit in valListOne) {
        println("ONE $fruit")
    }
}

fun varList() {
    val valListTwo = mutableListOf("Apple", "Banana", "Orange", "Pear", "Grape")
    valListTwo.add("Watermelon")
    for (fruit in valListTwo) {
        println("TWo $fruit")
    }
}