package com.zsp.today.module.zhilin.kotlin.kotlin_extends_constructor

/**
 * Created on 2022/4/14
 * @author zsp
 * @desc 继承与构造函数二
 */
fun main() {
    val studentTwo = StudentTwo("xxxxxx", 18)
    studentTwo.name = "Tom"
    studentTwo.age = 16
    studentTwo.eat()
}