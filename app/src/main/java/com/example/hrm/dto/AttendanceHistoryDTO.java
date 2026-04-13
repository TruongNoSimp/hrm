package com.example.hrm.dto;

import com.example.hrm.models.Employee;
import com.example.hrm.models.Attendance;

public class AttendanceHistoryDTO {
    private Attendance attendance;
    private Employee employee;

    public AttendanceHistoryDTO(Attendance attendance, Employee employee) {
        this.attendance = attendance;
        this.employee = employee;
    }

    public Attendance getAttendance() {
        return attendance;
    }

    public void setAttendance(Attendance attendance) {
        this.attendance = attendance;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public String getEmployeeName() {
        return employee != null ? employee.getHoTen() : "N/A";
    }

    public String getAvatar() {
        return employee != null ? employee.getAvatar() : null;
    }

    public String getGioVao() {
        return attendance != null ? attendance.getGioVao() : "";
    }

    public int getTrangThai() {
        return attendance != null ? attendance.getTrangThai() : 0;
    }
}
