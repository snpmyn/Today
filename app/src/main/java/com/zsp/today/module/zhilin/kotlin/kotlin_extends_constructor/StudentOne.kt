package com.zsp.today.module.zhilin.kotlin.kotlin_extends_constructor

import com.zsp.today.module.zhilin.kotlin.kotlin_class_and_object.Person
import com.zsp.today.module.zhilin.kotlin.kotlin_interface.StudyOne

/**
 * Created on 2022/4/14
 * @author zsp
 * @desc 学生一
 */
class StudentOne : Person(), StudyOne {
    var sno = ""
    var grade = 0
    override fun readBooks() {
        println("$name is reading")
    }

    override fun doHomework() {
        println("$name is doing homework")
    }
}