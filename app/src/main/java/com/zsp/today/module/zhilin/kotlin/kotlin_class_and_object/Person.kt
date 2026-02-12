package com.zsp.today.module.zhilin.kotlin.kotlin_class_and_object

/**
 * Created on 2022/4/14
 * @author zsp
 * @desc 人
 */
open class Person {
    var name = ""
    var age = 0
    fun eat() {
        println("$name is eating. He is $age years old.")
    }
}