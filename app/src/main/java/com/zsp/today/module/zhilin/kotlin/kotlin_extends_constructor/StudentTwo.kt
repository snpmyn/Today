package com.zsp.today.module.zhilin.kotlin.kotlin_extends_constructor

import com.zsp.today.module.zhilin.kotlin.kotlin_class_and_object.Person
import com.zsp.today.module.zhilin.kotlin.kotlin_interface.StudyTwo

/**
 * Created on 2022/4/14
 * @author zsp
 * @desc 学生二
 */
class StudentTwo(sno: String, grade: Int) : Person(), StudyTwo {
    init {
        println("sno is $sno")
        println("grade is $grade")
    }

    override fun readBooks() {
        println("$name is reading")
    }
}