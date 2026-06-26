package com.zsp.today.module.zhilin.collaboration.bean;

/**
 * @decs: 学生数据
 * @author: 郑少鹏
 * @date: 2026/6/20 21:27
 * @version: v 1.0
 */
public class StudentBean {
    /**
     * 识别状态
     * <p>
     * 0 - 未识别
     * 1 - 已识别
     * 2 - 已提交
     */
    private final int recognizeState;
    /**
     * 班级名称
     */
    String className;
    /**
     * 班级 ID
     */
    Integer classId;
    /**
     * 学生姓名
     */
    String studentName;
    /**
     * 学生 ID
     */
    int studentId;

    /**
     * constructor
     *
     * @param className      班级姓名
     * @param classId        班级 ID
     * @param studentName    学生姓名
     * @param studentId      学生 ID
     * @param recognizeState 识别状态
     */
    public StudentBean(String className, Integer classId, String studentName, int studentId, int recognizeState) {
        this.className = className;
        this.classId = classId;
        this.studentName = studentName;
        this.studentId = studentId;
        this.recognizeState = recognizeState;
    }

    public String getStudentName() {
        return studentName;
    }

    public int getStudentId() {
        return studentId;
    }

    public int getRecognizeState() {
        return recognizeState;
    }
}