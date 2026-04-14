package com.example.hrm.models;

public class Department {
    private int idPhongBan;
    private String maPb;
    private String tenPhong;
    private String moTa;

    public Department() {
    }

    public Department(int idPhongBan, String maPb, String tenPhong, String moTa) {
        this.idPhongBan = idPhongBan;
        this.maPb = maPb;
        this.tenPhong = tenPhong;
        this.moTa = moTa;
    }

    public int getIdPhongBan() {
        return idPhongBan;
    }

    public void setIdPhongBan(int idPhongBan) {
        this.idPhongBan = idPhongBan;
    }

    public String getMaPb() {
        return maPb;
    }

    public void setMaPb(String maPb) {
        this.maPb = maPb;
    }

    public String getTenPhong() {
        return tenPhong;
    }

    public void setTenPhong(String tenPhong) {
        this.tenPhong = tenPhong;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }
}