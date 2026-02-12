package com.zsp.today.module.zhilin.kotlin.kotlin_functional_api

import java.util.Locale

/**
 * Created on 2022/4/18
 * @author zsp
 * @desc 集合的函数式 API
 */
fun main() {
    functionalApiOne()
    functionalApiTwo()
    functionalApiThree()
    functionalApiFour()
}

fun functionalApiOne() {
    val list = listOf("Apple", "Banana", "Orange", "Pear", "Grape")
    val maxLengthFruit = list.maxByOrNull { it.length }
    println("max length fruit is $maxLengthFruit")
}

fun functionalApiTwo() {
    val list = listOf("Apple", "Banana", "Orange", "Pear", "Grape", "Watermelon")
    val newList = list.map { it.uppercase(Locale.getDefault()) }
    for (fruit in newList) {
        println("ONE $fruit")
    }
}

fun functionalApiThree() {
    val list = listOf("Apple", "Banana", "Orange", "Pear", "Grape", "Watermelon")
    val newList = list.filter { it.length <= 5 }.map { it.uppercase(Locale.getDefault()) }
    for (fruit in newList) {
        println("TWO $fruit")
    }
}

fun functionalApiFour() {
    val list = listOf("Apple", "Banana", "Orange", "Pear", "Grape", "Watermelon")
    val anyResult = list.any { it.length <= 5 }
    val allResult = list.all { it.length <= 5 }
    println("anyResult is $anyResult, allResult is $allResult")
}