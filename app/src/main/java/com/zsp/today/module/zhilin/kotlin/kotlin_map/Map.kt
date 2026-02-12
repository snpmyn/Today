package com.zsp.today.module.zhilin.kotlin.kotlin_map

/**
 * Created on 2022/4/18
 * @author zsp
 * @desc Map 集合的创建与遍历
 */
fun main() {
    mapOne()
    mapTwo()
}

fun mapOne() {
    val map = HashMap<String, Int>()
    map["Apple"] = 1
    map["Banana"] = 2
    map["Orange"] = 3
    map["Pear"] = 4
    map["Grape"] = 5
    for ((fruit, number) in map) {
        println("ONE fruit is $fruit, number is $number")
    }
}

fun mapTwo() {
    val map = mapOf("Apple" to 1, "Banana" to 2, "Orange" to 3, "Pear" to 4, "Grape" to 5)
    for ((fruit, number) in map) {
        println("TWo fruit is $fruit, number is $number")
    }
}