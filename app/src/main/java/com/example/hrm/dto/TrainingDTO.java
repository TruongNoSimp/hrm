package com.example.hrm.dto;

public class TrainingDTO {
    private String courseId;
    private String courseName;
    private String teacher;
    private String startDate;
    private String endDate;
    private String employeeName;
    private String employeeCode;
    private String status;

    public TrainingDTO(String courseId, String courseName, String teacher, String startDate, String endDate, String employeeName, String employeeCode, String status) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.teacher = teacher;
        this.startDate = startDate;
        this.endDate = endDate;
        this.employeeName = employeeName;
        this.employeeCode = employeeCode;
        this.status = status;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getTeacher() {
        return teacher;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public String getEmployeeCode() {
        return employeeCode;
    } //

    public String getStatus() {
        return status;
    }
}