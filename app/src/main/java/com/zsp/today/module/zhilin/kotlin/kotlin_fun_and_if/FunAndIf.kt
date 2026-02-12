package com.zsp.today.module.zhilin.kotlin.kotlin_fun_and_if

import kotlin.math.max

/**
 * Created on 2022/4/13
 * @author zsp
 * @desc 函数和 if 条件语句
 */
fun main() {
    val a = 37
    val b = 40
    val valueOne = largerNumberOne(a, b)
    val valueTwo = largerNumberTwo(a, b)
    val valueThree = largerNumberThree(a, b)
    val valueFour = largerNumberFour(a, b)
    val valueFive = largerNumberFive(a, b)
    val valueSix = largerNumberSix(a, b)
    val valueSeven = largerNumberSeven(a, b)
    println("larger number is $valueOne $valueTwo $valueThree $valueFour $valueFive $valueSix $valueSeven")
}

fun largerNumberOne(num1: Int, num2: Int): Int {
    return max(num1, num2)
}

fun largerNumberTwo(num1: Int, num2: Int): Int = max(num1, num2)
fun largerNumberThree(num1: Int, num2: Int) = max(num1, num2)
fun largerNumberFour(num1: Int, num2: Int): Int {
    val value = if (num1 > num2) {
        num1
    } else {
        num2
    }
    return value
}

fun largerNumberFive(num1: Int, num2: Int): Int {
    return if (num1 > num2) {
        num1
    } else {
        num2
    }
}

fun largerNumberSix(num1: Int, num2: Int) = if (num1 > num2) {
    num1
} else {
    num2
}

fun largerNumberSeven(num1: Int, num2: Int) = if (num1 > num2) num1 else num2