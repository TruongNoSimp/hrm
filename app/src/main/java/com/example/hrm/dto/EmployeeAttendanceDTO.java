package com.example.hrm.dto;

import com.example.hrm.models.Employee;

public class EmployeeAttendanceDTO {
    private Employee employee;

    private String tenPhongBan;
    private String gioVao;
    private int trangThaiChamCong; // 1: Đúng giờ, 2: Muộn

    public EmployeeAttendanceDTO() {
    }

    public EmployeeAttendanceDTO(Employee employee, String tenPhongBan, String gioVao, int trangThaiChamCong) {
        this.employee = employee;
        this.tenPhongBan = tenPhongBan;
        this.gioVao = gioVao;
        this.trangThaiChamCong = trangThaiChamCong;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public String getTenPhongBan() {
        return tenPhongBan != null ? tenPhongBan : "Chưa xác định";
    }

    public void setTenPhongBan(String tenPhongBan) {
        this.tenPhongBan = tenPhongBan;
    }

    public String getGioVao() {
        return gioVao;
    }

    public void setGioVao(String gioVao) {
        this.gioVao = gioVao;
    }

    public int getTrangThaiChamCong() {
        return trangThaiChamCong;
    }

    public void setTrangThaiChamCong(int trangThaiChamCong) {
        this.trangThaiChamCong = trangThaiChamCong;
    }

    public String getEmployeeName() {
        return employee != null ? employee.getHoTen() : "N/A";
    }
}