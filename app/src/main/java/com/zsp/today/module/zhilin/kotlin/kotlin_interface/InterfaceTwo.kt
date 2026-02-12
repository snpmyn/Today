package com.zsp.today.module.zhilin.kotlin.kotlin_interface

import com.zsp.today.module.zhilin.kotlin.kotlin_extends_constructor.StudentTwo

/**
 * Created on 2022/4/15
 * @author zsp
 * @desc 接口二
 */
fun main() {
    val studentTwo = StudentTwo("xxxxxx", 10)
    studentTwo.name = "ZhangSan"
    doStudyTwo(studentTwo)
}

fun doStudyTwo(studyTwo: StudyTwo) {
    studyTwo.readBooks()
    studyTwo.doHomework()
}