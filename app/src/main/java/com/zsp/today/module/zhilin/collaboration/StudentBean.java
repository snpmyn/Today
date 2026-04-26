package com.zsp.today.module.zhilin.collaboration;

/**
 * @decs: 学生数据
 * @author: 郑少鹏
 * @date: 2026/4/25 0:00
 * @version: v 1.0
 */
public class StudentBean {
    private int studentId;
    private String studentName;
    /**
     * 识别状态
     * <p>
     * 0 - 未识别
     * 1 - 已识别
     * 2 - 已提交
     */
    private int recognizeState;

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public int getRecognizeState() {
        return recognizeState;
    }

    public void setRecognizeState(int recognizeState) {
        this.recognizeState = recognizeState;
    }
}