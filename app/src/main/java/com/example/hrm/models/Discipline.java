package com.example.hrm.models;

public class Discipline {
    private int idKyLuat;
    private int idNhanVien;
    private String ngayQuyetDinh;
    private String hinhThuc;
    private double soTienPhat;
    private String lyDo;

    // Dùng để hiển thị khi JOIN với bảng NhanVien
    private String tenNhanVien;
    private String maNhanVien;

    public Discipline() {
    }

    public Discipline(int idKyLuat, int idNhanVien, String ngayQuyetDinh, String hinhThuc, double soTienPhat, String lyDo) {
        this.idKyLuat = idKyLuat;
        this.idNhanVien = idNhanVien;
        this.ngayQuyetDinh = ngayQuyetDinh;
        this.hinhThuc = hinhThuc;
        this.soTienPhat = soTienPhat;
        this.lyDo = lyDo;
    }

    public Discipline(int idKyLuat, int idNhanVien, String ngayQuyetDinh, String hinhThuc,
                      double soTienPhat, String lyDo, String tenNhanVien, String maNhanVien) {
        this.idKyLuat = idKyLuat;
        this.idNhanVien = idNhanVien;
        this.ngayQuyetDinh = ngayQuyetDinh;
        this.hinhThuc = hinhThuc;
        this.soTienPhat = soTienPhat;
        this.lyDo = lyDo;
        this.tenNhanVien = tenNhanVien;
        this.maNhanVien = maNhanVien;
    }

    public int getIdKyLuat() {
        return idKyLuat;
    }

    public void setIdKyLuat(int idKyLuat) {
        this.idKyLuat = idKyLuat;
    }

    public int getIdNhanVien() {
        return idNhanVien;
    }

    public void setIdNhanVien(int idNhanVien) {
        this.idNhanVien = idNhanVien;
    }

    public String getNgayQuyetDinh() {
        return ngayQuyetDinh;
    }

    public void setNgayQuyetDinh(String ngayQuyetDinh) {
        this.ngayQuyetDinh = ngayQuyetDinh;
    }

    public String getHinhThuc() {
        return hinhThuc;
    }

    public void setHinhThuc(String hinhThuc) {
        this.hinhThuc = hinhThuc;
    }

    public double getSoTienPhat() {
        return soTienPhat;
    }

    public void setSoTienPhat(double soTienPhat) {
        this.soTienPhat = soTienPhat;
    }

    public String getLyDo() {
        return lyDo;
    }

    public void setLyDo(String lyDo) {
        this.lyDo = lyDo;
    }

    public String getTenNhanVien() {
        return tenNhanVien;
    }

    public void setTenNhanVien(String tenNhanVien) {
        this.tenNhanVien = tenNhanVien;
    }

    public String getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(String maNhanVien) {
        this.maNhanVien = maNhanVien;
    }
}