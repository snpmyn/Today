package com.zsp.today.module.zhilin.kotlin.kotlin_interface

import com.zsp.today.module.zhilin.kotlin.kotlin_extends_constructor.StudentOne

/**
 * Created on 2022/4/15
 * @author zsp
 * @desc 接口一
 */
fun main() {
    val studentOne = StudentOne()
    studentOne.sno = "xxxxxx"
    studentOne.grade = 16
    studentOne.name = "Lily"
    doStudy(studentOne)
}

fun doStudy(studyOne: StudyOne) {
    studyOne.readBooks()
    studyOne.doHomework()
}