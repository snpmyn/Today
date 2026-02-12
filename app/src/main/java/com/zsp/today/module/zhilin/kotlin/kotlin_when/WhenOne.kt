package com.zsp.today.module.zhilin.kotlin.kotlin_when

/**
 * Created on 2022/4/13
 * @author zsp
 * @desc when 条件语句一
 */
fun main() {
    val nameOne = "Tom"
    val nameTwo = "Jim"
    println("score is " + getScoreOne(nameOne) + " " + getScoreTwo(nameTwo))
}

fun getScoreOne(name: String) = when (name) {
    "Tom" -> 86
    "Jim" -> 77
    "Jack" -> 95
    "Lily" -> 100
    else -> 0
}

fun getScoreTwo(name: String) = when {
    name.startsWith("Tom") -> 86
    name == "Jim" -> 77
    name == "Jack" -> 95
    name == "Lily" -> 100
    else -> 0
}