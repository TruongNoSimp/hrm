package com.example.hrm.models;

public class Employee {
    private int idNv;
    private String maNv;
    private String hoTen;
    private String ngaySinh;
    private String gioiTinh;
    private String soDt;
    private String email;
    private int idPhongBan;
    private String tenPhongBan;
    private String chucVu;
    private String ngayVaoLam;
    private double heSoLuong;
    private int trangThai;
    private String gioVao;
    private String avatar;
    public Employee() {
    }

    public int getIdNv() {
        return idNv;
    }

    public void setIdNv(int idNv) {
        this.idNv = idNv;
    }

    public String getMaNv() {
        return maNv;
    }

    public void setMaNv(String maNv) {
        this.maNv = maNv;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(String ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public String getSoDt() {
        return soDt;
    }

    public void setSoDt(String soDt) {
        this.soDt = soDt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getIdPhongBan() {
        return idPhongBan;
    }

    public void setIdPhongBan(int idPhongBan) {
        this.idPhongBan = idPhongBan;
    }

    public String getTenPhongBan() {
        return tenPhongBan;
    }

    public void setTenPhongBan(String tenPhongBan) {
        this.tenPhongBan = tenPhongBan;
    }

    public String getChucVu() {
        return chucVu;
    }

    public void setChucVu(String chucVu) {
        this.chucVu = chucVu;
    }

    public String getNgayVaoLam() {
        return ngayVaoLam;
    }

    public void setNgayVaoLam(String ngayVaoLam) {
        this.ngayVaoLam = ngayVaoLam;
    }

    public double getHeSoLuong() {
        return heSoLuong;
    }

    public void setHeSoLuong(double heSoLuong) {
        this.heSoLuong = heSoLuong;
    }

    public int getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(int trangThai) {
        this.trangThai = trangThai;
    }
    public String getAvatar() {
        return avatar;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getGioVao() {
        return gioVao;
    }

    public void setGioVao(String gioVao) {
        this.gioVao = gioVao;
    }

    @Override
    public String toString() {
        return hoTen != null ? hoTen : "Chưa xác định";
    }
}